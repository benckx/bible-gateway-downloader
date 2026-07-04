package biblegatewaydownloader.parser

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * Result of parsing a single Bible Gateway print page.
 *
 * @param contentHtml cleaned passage HTML (inner markup of `.passage-content`)
 * @param nextChapterTitle the title of the next-chapter link (e.g. "Ézéchiel 2"),
 *        or null when the page has no next chapter.
 */
data class ParsedPage(
    val currentTitle: String?,
    val contentHtml: String,
    val nextChapterTitle: String?,
)

/**
 * Extracts passage content and navigation info from a Bible Gateway print page.
 */
object ChapterParser {

    private val NOISE_SELECTORS = listOf(
        ".footnotes",
        ".crossrefs",
        "sup.footnote",
        "sup.crossreference",
        ".full-chap-link",
        ".passage-scroller",
        ".publisher-info-bottom",
        "crossref",
    )

    fun parse(html: String): ParsedPage {
        val doc: Document = Jsoup.parse(html)

        val content = doc.selectFirst(".passage-text .passage-content")
            ?: error("No passage content found on page (unexpected layout or invalid passage).")

        val currentTitle = doc.selectFirst(".dropdown-display-text")
            ?.text()?.trim()?.takeIf { it.isNotEmpty() }

        // Drop footnote/cross-reference clutter so the exported document stays clean.
        NOISE_SELECTORS.forEach { selector -> content.select(selector).remove() }

        // Links add no value in a printed/offline document; keep only their text.
        content.select("a").forEach { it.unwrap() }

        val nextTitle = doc.selectFirst("a.next-chapter")?.attr("title")?.trim()?.takeIf { it.isNotEmpty() }

        return ParsedPage(
            currentTitle = currentTitle,
            contentHtml = content.html(),
            nextChapterTitle = nextTitle,
        )
    }
}
