package biblegatewaydownloader

import biblegatewaydownloader.client.BibleGatewayClient
import biblegatewaydownloader.client.BookCrawler
import biblegatewaydownloader.epub.EpubWriter
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

class DownloadCommand : CliktCommand(name = "bible-gateway-downloader") {

    private val version by option("-v", "--version", help = "Bible version code, e.g. SG21").required()
    private val book by option("-b", "--book", help = "Book name as on Bible Gateway, e.g. Ézéchiel").required()
    private val start by option("-s", "--start", help = "First chapter to download").int().default(1)
    private val outDir by option("-o", "--out", help = "Output directory").path().default(Path.of("out"))

    override fun run() = runBlocking {
        outDir.createDirectories()

        val book = BibleGatewayClient().use { client ->
            BookCrawler(client) { echo(it) }.crawl(book, version, start)
        }

        if (book.chapters.isEmpty()) {
            echo("No chapters downloaded — check the book name and version.", err = true)
            return@runBlocking
        }

        val slug = slugify(book.name)
        val base = "$slug-${version.lowercase()}"
        val pdfPath = outDir.resolve("$base.pdf")
        val epubPath = outDir.resolve("$base.epub")

        echo("Downloaded ${book.chapters.size} chapter(s). Writing documents…")
        PdfWriter.write(book, pdfPath)
        EpubWriter.write(book, epubPath)

        echo("PDF:  ${pdfPath.toAbsolutePath()}")
        echo("EPUB: ${epubPath.toAbsolutePath()}")
    }
}

fun main(args: Array<String>) = DownloadCommand().main(args)

/** Turn a book name like "Ézéchiel" into a filename-safe slug like "ezechiel". */
private fun slugify(name: String): String =
    java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "")
        .lowercase()
        .replace(Regex("[^a-z0-9]+"), "-")
        .trim('-')
        .ifEmpty { "book" }
