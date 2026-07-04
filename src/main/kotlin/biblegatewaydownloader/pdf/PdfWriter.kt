package biblegatewaydownloader.pdf

import biblegatewaydownloader.model.Book
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import org.jsoup.Jsoup
import org.jsoup.helper.W3CDom
import org.jsoup.nodes.Document
import java.io.OutputStream
import java.nio.file.Path
import kotlin.io.path.outputStream

/**
 * Renders a [Book] to a single PDF document via openhtmltopdf.
 */
object PdfWriter {

    private val CSS = """
        @page { size: A4; margin: 2cm; }
        body { font-family: serif; font-size: 11pt; line-height: 1.4; }
        h1.book-title { font-size: 22pt; text-align: center; margin: 3cm 0 1cm 0; }
        h2.chapter { font-size: 16pt; page-break-before: always; margin-top: 0; }
        h1.book-title + * { page-break-before: avoid; }
        h3 { font-size: 12pt; font-style: italic; }
        sup.versenum, span.chapternum { font-weight: bold; }
        p { margin: 0.4em 0; text-align: justify; }
    """.trimIndent()

    fun write(book: Book, output: Path) {
        output.outputStream().use { write(book, it) }
    }

    fun write(book: Book, output: OutputStream) {
        quietLogging()
        val doc = buildDocument(book)
        val w3c = W3CDom().fromJsoup(doc)
        PdfRendererBuilder()
            .useFastMode()
            .withW3cDocument(w3c, "https://www.biblegateway.com/")
            .toStream(output)
            .run()
    }

    private fun quietLogging() {
        // openhtmltopdf's own logger and PDFBox's font fallback warnings are noisy;
        // silence them so the interactive CLI output stays clean.
        com.openhtmltopdf.util.XRLog.setLoggingEnabled(false)
        java.util.logging.Logger.getLogger("org.apache.pdfbox").level = java.util.logging.Level.SEVERE
        java.util.logging.Logger.getLogger("org.apache.fontbox").level = java.util.logging.Level.SEVERE
    }

    private fun buildDocument(book: Book): Document {
        val doc = Jsoup.parse(
            """
            <!DOCTYPE html>
            <html><head><meta charset="utf-8"/><style></style></head><body></body></html>
            """.trimIndent(),
        )
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml)
        doc.selectFirst("style")!!.text(CSS)

        val body = doc.body()
        body.appendElement("h1").addClass("book-title")
            .text("${book.name} — ${book.version}")

        book.chapters.forEach { chapter ->
            body.appendElement("h2").addClass("chapter").text(chapter.heading)
            val holder = body.appendElement("div").addClass("chapter-content")
            holder.append(chapter.contentHtml)
        }
        return doc
    }
}
