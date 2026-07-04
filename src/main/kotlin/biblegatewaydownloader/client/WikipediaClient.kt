package biblegatewaydownloader.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode

/**
 * Fetches and cleans the Wikipedia article for a Bible book, to be appended to
 * the exported documents.
 */
class WikipediaClient(
    private val userAgent: String = DEFAULT_USER_AGENT,
) : AutoCloseable {

    private val client = HttpClient(CIO)

    data class Article(val title: String, val html: String, val url: String)

    /**
     * Fetch and clean the Wikipedia article identified by [slug]
     * (e.g. "Book_of_Daniel"). Returns null if the article cannot be retrieved.
     */
    suspend fun fetchArticle(slug: String): Article? {
        val json = runCatching { fetchParseJson(slug) }.getOrNull() ?: return null

        val title = extractJsonString(json, "title")?.let(::unescapeJson) ?: slug.replace('_', ' ')
        val rawHtml = extractJsonString(json, "text")?.let(::unescapeJson) ?: return null

        val cleaned = WikipediaCleaner.clean(rawHtml)
        if (cleaned.isBlank()) return null

        return Article(
            title = title,
            html = cleaned,
            url = "https://en.wikipedia.org/wiki/$slug",
        )
    }

    /**
     * Extract the raw (still-escaped) value of a top-level JSON string field named
     * [key] using a linear scan that respects backslash escapes. Avoids regex, which
     * overflows the stack on very large values.
     */
    private fun extractJsonString(json: String, key: String): String? {
        val marker = "\"$key\""
        var i = json.indexOf(marker)
        while (i >= 0) {
            var j = i + marker.length
            while (j < json.length && json[j].isWhitespace()) j++
            if (j < json.length && json[j] == ':') {
                j++
                while (j < json.length && json[j].isWhitespace()) j++
                if (j < json.length && json[j] == '"') {
                    val start = j + 1
                    var k = start
                    while (k < json.length) {
                        when (json[k]) {
                            '\\' -> k += 2
                            '"' -> return json.substring(start, k)
                            else -> k++
                        }
                    }
                    return null
                }
            }
            i = json.indexOf(marker, i + marker.length)
        }
        return null
    }

    private suspend fun fetchParseJson(slug: String): String {
        val response = client.get(API_URL) {
            parameter("action", "parse")
            parameter("page", slug)
            parameter("prop", "text")
            parameter("redirects", "1")
            parameter("format", "json")
            parameter("formatversion", "2")
            header("User-Agent", userAgent)
        }
        if (response.status != HttpStatusCode.OK) {
            error("Unexpected HTTP status ${response.status} fetching Wikipedia page '$slug'")
        }
        return response.bodyAsText()
    }

    override fun close() = client.close()

    private fun unescapeJson(s: String): String {
        val sb = StringBuilder(s.length)
        var i = 0
        while (i < s.length) {
            val c = s[i]
            if (c == '\\' && i + 1 < s.length) {
                when (val n = s[i + 1]) {
                    '"' -> { sb.append('"'); i += 2 }
                    '\\' -> { sb.append('\\'); i += 2 }
                    '/' -> { sb.append('/'); i += 2 }
                    'n' -> { sb.append('\n'); i += 2 }
                    't' -> { sb.append('\t'); i += 2 }
                    'r' -> { sb.append('\r'); i += 2 }
                    'b' -> { sb.append('\b'); i += 2 }
                    'f' -> { sb.append('\u000C'); i += 2 }
                    'u' -> {
                        if (i + 5 < s.length) {
                            val code = s.substring(i + 2, i + 6).toInt(16)
                            sb.append(code.toChar())
                            i += 6
                        } else {
                            sb.append(n); i += 2
                        }
                    }
                    else -> { sb.append(n); i += 2 }
                }
            } else {
                sb.append(c); i++
            }
        }
        return sb.toString()
    }

    companion object {
        private const val API_URL = "https://en.wikipedia.org/w/api.php"
        private const val DEFAULT_USER_AGENT =
            "BibleGatewayDownloader/1.0 (https://github.com/benckx/bible-gateway-downloader)"
    }
}
