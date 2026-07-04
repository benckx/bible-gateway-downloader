package biblegatewaydownloader.epub

import biblegatewaydownloader.model.Book
import biblegatewaydownloader.model.Chapter
import io.documentnode.epub4j.domain.Author
import io.documentnode.epub4j.domain.Metadata
import io.documentnode.epub4j.domain.Resource
import io.documentnode.epub4j.epub.EpubWriter
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.OutputStream
import java.nio.file.Path
import kotlin.io.path.outputStream
import io.documentnode.epub4j.domain.Book as EpubBook

/**
 * Renders a [Book] to a single EPUB file (with a chapter-based TOC index) via epub4j.
 */
object EpubWriter {

    private val CSS = """
        body { font-family: serif; line-height: 1.4; }
        h2.chapter { font-size: 1.4em; }
        h3 { font-style: italic; }
        sup.versenum, span.chapternum { font-weight: bold; }
        p { text-align: justify; }
    """.trimIndent()

    fun write(book: Book, output: Path) {
        output.outputStream().use { write(book, it) }
    }

    fun write(book: Book, output: OutputStream) {
        val epub = EpubBook()
        val metadata: Metadata = epub.metadata
        metadata.addTitle("${book.name} (${book.version})")
        metadata.addAuthor(Author("Bible", "Gateway"))
        metadata.language = "fr"

        epub.resources.add(Resource(CSS.toByteArray(Charsets.UTF_8), "style.css"))

        book.chapters.forEach { chapter ->
            val href = "chap-${chapter.number}.html"
            val resource = Resource(
                chapterXhtml(chapter).toByteArray(Charsets.UTF_8),
                href,
            )
            epub.addSection(chapter.heading, resource)
        }

        EpubWriter().write(epub, output)
    }

    private fun chapterXhtml(chapter: Chapter): String {
        val doc: Document = Jsoup.parse(
            """
            <!DOCTYPE html>
            <html xmlns="http://www.w3.org/1999/xhtml"><head>
            <meta charset="utf-8"/>
            <link rel="stylesheet" type="text/css" href="style.css"/>
            <title></title></head><body></body></html>
            """.trimIndent(),
        )
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml)
        doc.selectFirst("title")!!.text(chapter.heading)

        val body = doc.body()
        body.appendElement("h2").addClass("chapter").text(chapter.heading)
        body.append(chapter.contentHtml)
        return doc.outerHtml()
    }
}
