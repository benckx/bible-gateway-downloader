package biblegatewaydownloader.model

/**
 * Extra material appended after the chapters (e.g. the Wikipedia article).
 *
 * @param title heading for the appendix (e.g. "About Ezekiel")
 * @param html cleaned HTML body of the appendix
 * @param sourceUrl the source URL, shown as attribution
 */
data class Appendix(
    val title: String,
    val html: String,
    val sourceUrl: String,
)
