package biblegatewaydownloader.pdf

import biblegatewaydownloader.model.Book
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.FontStyle.*
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import com.openhtmltopdf.util.XRLog
import org.jsoup.Jsoup
import org.jsoup.helper.W3CDom
import org.jsoup.nodes.Document
import java.io.OutputStream
import java.nio.file.Path
import kotlin.io.path.outputStream

/**
 * Renders a [Book] to a single PDF document via openhtmltopdf.
 *
 * A bundled Unicode font (GNU FreeSerif) is embedded so that Greek, Hebrew and
 * accented Latin text (e.g. terms quoted in the Wikipedia appendix) render
 * correctly instead of as missing-glyph boxes.
 */
object PdfWriter {

    private const val FONT_FAMILY = "FreeSerif"

    private data class BundledFont(
        val resource: String,
        val weight: Int,
        val style: BaseRendererBuilder.FontStyle,
    )

    private val FONTS = listOf(
        BundledFont("/fonts/FreeSerif.ttf", 400, NORMAL),
        BundledFont("/fonts/FreeSerifBold.ttf", 700, NORMAL),
        BundledFont("/fonts/FreeSerifItalic.ttf", 400, ITALIC),
        BundledFont("/fonts/FreeSerifBoldItalic.ttf", 700, ITALIC),
    )

    private val CSS = """
        @page { size: A4; margin: 2cm; }
        body { font-family: '$FONT_FAMILY', serif; font-size: 11pt; line-height: 1.4; }
        h1.book-title { font-size: 22pt; text-align: center; margin: 3cm 0 1cm 0; }
        h2.chapter { font-size: 16pt; page-break-before: always; margin-top: 0; }
        h1.book-title + * { page-break-before: avoid; }
        h3 { font-size: 12pt; font-style: italic; }
        sup.versenum, span.chapternum { font-weight: bold; }
        p { margin: 0.4em 0; text-align: justify; }
        p.appendix-source { font-style: italic; color: #555; font-size: 9pt; }
    """.trimIndent()

    fun write(book: Book, output: Path) {
        output.outputStream().use { write(book, it) }
    }

    fun write(book: Book, output: OutputStream) {
        quietLogging()
        val doc = buildDocument(book)
        val w3c = W3CDom().fromJsoup(doc)
        val builder = PdfRendererBuilder()
            .useFastMode()
            .withW3cDocument(w3c, "https://www.biblegateway.com/")
        FONTS.forEach { font ->
            builder.useFont(
                { requireNotNull(javaClass.getResourceAsStream(font.resource)) { "Missing bundled font ${font.resource}" } },
                FONT_FAMILY,
                font.weight,
                font.style,
                true,
            )
        }
        builder.toStream(output).run()
    }

    private val silencedLoggers = listOf(
        java.util.logging.Logger.getLogger("org.apache.pdfbox"),
        java.util.logging.Logger.getLogger("org.apache.fontbox"),
    )

    private fun quietLogging() {
        // openhtmltopdf's own logger and PDFBox's font fallback warnings are noisy;
        // silence them so the interactive CLI output stays clean. Strong references
        // are kept above so the JVM does not GC these loggers and reset their level.
        XRLog.setLoggingEnabled(false)
        silencedLoggers.forEach { it.level = java.util.logging.Level.SEVERE }
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

        book.appendix?.let { appendix ->
            body
                .appendElement("h2")
                .addClass("chapter appendix")
                .text(appendix.title)
            body
                .appendElement("p")
                .addClass("appendix-source")
                .text("Source: ${appendix.sourceUrl}")
            body
                .appendElement("div")
                .addClass("appendix-content")
                .append(appendix.html)
        }

        return doc
    }
}
