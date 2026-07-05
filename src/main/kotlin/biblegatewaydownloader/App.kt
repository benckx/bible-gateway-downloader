package biblegatewaydownloader

import biblegatewaydownloader.cli.InteractivePrompt
import biblegatewaydownloader.cli.PromptAbortedException
import biblegatewaydownloader.client.BibleGatewayClient
import biblegatewaydownloader.client.BookCrawler
import biblegatewaydownloader.client.WikipediaClient
import biblegatewaydownloader.epub.EpubWriter
import biblegatewaydownloader.model.Appendix
import biblegatewaydownloader.model.bible.BibleBook
import biblegatewaydownloader.model.bible.Version
import biblegatewaydownloader.model.bible.Testament
import biblegatewaydownloader.pdf.PdfWriter
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.path
import kotlinx.coroutines.runBlocking
import java.nio.file.Path
import kotlin.io.path.createDirectories

/** Scripted mode: `... --version SG21 --book Ezek [--start N] [--out DIR]`. */
class DownloadCommand : CliktCommand(name = "bible-gateway-downloader") {

    private val versionCode by option("-v", "--version", help = "Bible version code, e.g. SG21").required()
    private val bookKey by option("-b", "--book", help = "Book OSIS code (e.g. Ezek) or English name").required()
    private val start by option("-s", "--start", help = "First chapter to download").int().default(1)
    private val outDir by option("-o", "--out", help = "Output directory").path().default(Path.of("out"))

    override fun run() {
        val version = Version.entries.firstOrNull { it.code.equals(versionCode, ignoreCase = true) }
            ?: throw IllegalArgumentException(
                "Unknown version '$versionCode'. Known: ${Version.entries.joinToString { it.code }}",
            )
        val book = BibleBook.byOsis(bookKey)
            ?: BibleBook.entries.firstOrNull { it.englishName.equals(bookKey, ignoreCase = true) }
            ?: throw IllegalArgumentException("Unknown book '$bookKey'.")

        runDownload(book, version, start, outDir) { echo(it) }
    }
}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        runInteractive()
    } else {
        DownloadCommand().main(args)
    }
}

private fun runInteractive() {
    try {
        val version = InteractivePrompt.select(
            "Select a version",
            Version.entries,
        ) { it.label }

        val testament = InteractivePrompt.select(
            "Select a testament",
            Testament.entries.toList(),
        ) { it.label }

        val book = InteractivePrompt.select(
            "Select a book",
            BibleBook.of(testament),
        ) { it.englishName }

        runDownload(book, version, start = 1, outDir = Path.of("out")) { println(it) }
    } catch (_: PromptAbortedException) {
        System.err.println("Aborted.")
    }
}

private fun runDownload(
    book: BibleBook,
    version: Version,
    start: Int,
    outDir: Path,
    echo: (String) -> Unit,
) = runBlocking {
    outDir.createDirectories()

    val downloaded = BibleGatewayClient().use { client ->
        BookCrawler(client) { echo(it) }.crawl(book.osis, version, start)
    }

    if (downloaded.chapters.isEmpty()) {
        System.err.println("No chapters downloaded - check the book and version.")
        return@runBlocking
    }

    echo("Fetching Wikipedia article for ${book.englishName}…")
    val article = WikipediaClient().use { it.fetchArticle(book.wikipediaSlug) }
    val withAppendix = if (article != null) {
        downloaded.copy(
            appendix = Appendix(
                title = "About ${book.englishName} (Wikipedia)",
                html = article.html,
                sourceUrl = article.url,
            ),
        )
    } else {
        echo("  (Wikipedia article unavailable; continuing without appendix)")
        downloaded
    }

    val slug = slugify(withAppendix.name)
    val base = "$slug-${version.code.lowercase()}"
    val pdfPath = outDir.resolve("$base.pdf")
    val epubPath = outDir.resolve("$base.epub")

    echo("Downloaded ${withAppendix.chapters.size} chapter(s). Writing documents...")
    PdfWriter.write(withAppendix, pdfPath)
    EpubWriter.write(withAppendix, epubPath)

    echo("PDF:  ${pdfPath.toAbsolutePath()}")
    echo("EPUB: ${epubPath.toAbsolutePath()}")
}

/** Turn a book name like "Ézéchiel" into a filename-safe slug like "ezechiel". */
private fun slugify(name: String): String =
    java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "")
        .lowercase()
        .replace(Regex("[^a-z0-9]+"), "-")
        .trim('-')
        .ifEmpty { "book" }
