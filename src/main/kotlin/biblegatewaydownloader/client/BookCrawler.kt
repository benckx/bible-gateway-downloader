package biblegatewaydownloader.client

import biblegatewaydownloader.model.Book
import biblegatewaydownloader.model.Chapter
import biblegatewaydownloader.model.bible.Version
import biblegatewaydownloader.parser.ChapterParser

class BookCrawler(
    private val client: BibleGatewayClient,
    private val onProgress: (String) -> Unit = {},
) {

    /**
     * Download all chapters of the book identified by [osis] (e.g. "Ezek") in
     * [version], starting at chapter [start].
     *
     * Searches are issued as OSIS references ("$osis.$n"), which Bible Gateway
     * resolves regardless of the version's language. The localized display name
     * is read from the fetched pages and used for titles and stop detection.
     */
    suspend fun crawl(osis: String, version: Version, start: Int = 1): Book {
        require(start >= 1) { "start chapter must be >= 1" }

        val chapters = mutableListOf<Chapter>()
        var canonicalBook: String? = null
        var number = start

        while (number <= MAX_CHAPTERS) {
            val query = "$osis.$number"
            onProgress("Fetching ${canonicalBook?.let { "$it $number" } ?: query} ($version)…")

            val html = client.fetchPassageHtml(query, version)
            val page = ChapterParser.parse(html)

            if (canonicalBook == null) {
                canonicalBook = page.currentTitle?.let(::bookPartOf) ?: osis
            }

            val heading = page.currentTitle ?: "$canonicalBook $number"
            chapters += Chapter(
                book = canonicalBook,
                number = number,
                heading = heading,
                contentHtml = page.contentHtml,
            )

            val nextBook = page.nextChapterTitle?.let(::bookPartOf)
            if (nextBook == null || !nextBook.equals(canonicalBook, ignoreCase = true)) {
                break
            }
            number++
        }

        val name = canonicalBook ?: osis
        return Book(name = name, version = version, chapters = chapters)
    }

    companion object {
        private const val MAX_CHAPTERS = 200

        /**
         * Strip the trailing chapter number from a "Book N" title.
         * Handles numbered book names such as "1 Rois 2" -> "1 Rois".
         */
        fun bookPartOf(title: String): String {
            val tokens = title.trim().split(Regex("\\s+"))
            return if (tokens.size > 1 && tokens.last().matches(Regex("\\d+"))) {
                tokens.dropLast(1).joinToString(" ")
            } else {
                title.trim()
            }
        }
    }
}
