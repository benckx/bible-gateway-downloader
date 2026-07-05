package biblegatewaydownloader.model

data class Chapter(
    val book: String,
    val number: Int,
    val heading: String,
    val contentHtml: String,
)
