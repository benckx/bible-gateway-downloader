package biblegatewaydownloader.model

data class Chapter(
    val book: String,
    val number: Int,
    val heading: String,
    val contentHtml: String,
)

data class Book(
    val name: String,
    val version: String,
    val chapters: List<Chapter>,
    val appendix: Appendix? = null,
)

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
