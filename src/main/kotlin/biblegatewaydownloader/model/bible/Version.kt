package biblegatewaydownloader.model.bible

/**
 * A Bible version/translation supported by the tool.
 *
 * @param code the Bible Gateway version code used in the URL (e.g. "SG21")
 * @param displayName the human-readable translation name
 */
enum class Version(val code: String, val displayName: String) {
    SG21("SG21", "Segond 21"),
    LSG("LSG", "Louis Segond"),
    NIV("NIV", "New International Version"),
    KJV("KJV", "King James Version"),
    ;

    val label: String
        get() = "$displayName ($code)"

}
