package biblegatewaydownloader.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode

/**
 * Thin HTTP client around the Bible Gateway public "print" passage view.
 *
 * The print interface returns a lightweight HTML page containing the passage
 * text and prev/next chapter navigation, which is all we need for scraping.
 */
class BibleGatewayClient(
    private val userAgent: String = DEFAULT_USER_AGENT,
) : AutoCloseable {

    private val client = HttpClient(CIO)

    /**
     * Fetch the raw HTML of the print page for [search] (e.g. "Ézéchiel 1")
     * in the given [version] (e.g. "SG21").
     */
    suspend fun fetchPassageHtml(search: String, version: String): String {
        val response = client.get(PASSAGE_URL) {
            parameter("search", search)
            parameter("version", version)
            parameter("interface", "print")
            header("User-Agent", userAgent)
        }
        if (response.status != HttpStatusCode.OK) {
            error("Unexpected HTTP status ${response.status} for search='$search' version='$version'")
        }
        return response.bodyAsText()
    }

    override fun close() = client.close()

    companion object {
        private const val PASSAGE_URL = "https://www.biblegateway.com/passage/"
        private const val DEFAULT_USER_AGENT =
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/124.0 Safari/537.36"
    }
}
