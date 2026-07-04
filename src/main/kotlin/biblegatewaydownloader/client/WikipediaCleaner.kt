package biblegatewaydownloader.client

import org.jsoup.Jsoup
import org.jsoup.nodes.Element

/**
 * Cleans raw Wikipedia article HTML (from the `action=parse` API) into a compact,
 * offline-friendly fragment suitable for embedding in a PDF/EPUB appendix.
 */
object WikipediaCleaner {

    /** Elements that are pure clutter for an offline reading appendix. */
    private val NOISE_SELECTORS = listOf(
        "style", "link", "script",
        "sup.reference", ".reference", ".mw-editsection", ".mw-cite-backlink",
        ".navbox", ".navbox-styles", ".vertical-navbox", ".sidebar",
        ".infobox", ".metadata", ".ambox", ".mbox-small", ".hatnote",
        ".shortdescription", ".mw-empty-elt", ".noprint", ".mw-jump-link",
        "table", "figure", "img", ".thumb", ".gallery", ".portal",
        ".sistersitebox", ".refbegin", ".reflist", "ol.references",
        ".mw-references-wrap", ".citation", "#toc", ".toc",
        ".mw-headline-anchor", ".plainlinks", "sup.noprint",
    )

    /** Heading ids after which the rest of the article is dropped. */
    private val STOP_HEADING_IDS = setOf(
        "see_also", "notes", "references", "citations", "sources",
        "bibliography", "further_reading", "external_links", "works_cited",
        "footnotes", "explanatory_notes",
    )

    fun clean(rawHtml: String): String {
        val doc = Jsoup.parse(rawHtml)
        val root = doc.selectFirst(".mw-parser-output") ?: doc.body()

        NOISE_SELECTORS.forEach { selector -> root.select(selector).remove() }
        dropTrailingSections(root)

        // Replace links with their text; nothing to navigate to offline.
        root.select("a").forEach { it.unwrap() }

        // Keep only a small set of structural tags; strip all attributes.
        root.select("*").forEach { el ->
            el.clearAttributes()
            if (el.tagName() !in ALLOWED_TAGS) {
                el.tagName("div")
            }
        }

        // Remove now-empty blocks left behind by the pruning above.
        root.select("p, div, li, ul, ol, span").forEach { el ->
            if (el.text().isBlank() && el.select("img").isEmpty()) el.remove()
        }

        return root.html().trim()
    }

    private fun dropTrailingSections(root: Element) {
        val children = root.children()
        var cutIndex = -1
        for (i in children.indices) {
            val heading = children[i].selectFirst("h2")
                ?: children[i].takeIf { it.tagName() == "h2" }
            val id = heading?.id()?.lowercase()
            if (id != null && id in STOP_HEADING_IDS) {
                cutIndex = i
                break
            }
        }
        if (cutIndex >= 0) {
            for (i in children.size - 1 downTo cutIndex) {
                children[i].remove()
            }
        }
    }

    private val ALLOWED_TAGS = setOf(
        "p", "h1", "h2", "h3", "h4", "h5", "h6",
        "ul", "ol", "li", "dl", "dt", "dd",
        "b", "strong", "i", "em", "blockquote", "br", "span", "div",
    )
}
