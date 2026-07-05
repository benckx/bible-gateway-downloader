package biblegatewaydownloader.model

import biblegatewaydownloader.model.bible.Version

data class Book(
    val name: String,
    val version: Version,
    val chapters: List<Chapter>,
    val appendix: Appendix? = null,
)
