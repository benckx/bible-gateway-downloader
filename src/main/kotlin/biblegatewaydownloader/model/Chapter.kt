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
)
