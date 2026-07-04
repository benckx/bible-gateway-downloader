package biblegatewaydownloader.model

/**
 * The 66 books of the Protestant canon, in canonical order.
 *
 * @param osis the OSIS book code used as a language-independent search key
 *        (e.g. "Ezek"); Bible Gateway accepts "$osis.$chapter" as a search.
 * @param englishName the English display name (used for the interactive picker)
 * @param testament which testament the book belongs to
 */
enum class BibleBook(
    val osis: String,
    val englishName: String,
    val testament: Testament,
) {
    // --- Old Testament ---
    GENESIS("Gen", "Genesis", Testament.OLD),
    EXODUS("Exod", "Exodus", Testament.OLD),
    LEVITICUS("Lev", "Leviticus", Testament.OLD),
    NUMBERS("Num", "Numbers", Testament.OLD),
    DEUTERONOMY("Deut", "Deuteronomy", Testament.OLD),
    JOSHUA("Josh", "Joshua", Testament.OLD),
    JUDGES("Judg", "Judges", Testament.OLD),
    RUTH("Ruth", "Ruth", Testament.OLD),
    FIRST_SAMUEL("1Sam", "1 Samuel", Testament.OLD),
    SECOND_SAMUEL("2Sam", "2 Samuel", Testament.OLD),
    FIRST_KINGS("1Kgs", "1 Kings", Testament.OLD),
    SECOND_KINGS("2Kgs", "2 Kings", Testament.OLD),
    FIRST_CHRONICLES("1Chr", "1 Chronicles", Testament.OLD),
    SECOND_CHRONICLES("2Chr", "2 Chronicles", Testament.OLD),
    EZRA("Ezra", "Ezra", Testament.OLD),
    NEHEMIAH("Neh", "Nehemiah", Testament.OLD),
    ESTHER("Esth", "Esther", Testament.OLD),
    JOB("Job", "Job", Testament.OLD),
    PSALMS("Ps", "Psalm", Testament.OLD),
    PROVERBS("Prov", "Proverbs", Testament.OLD),
    ECCLESIASTES("Eccl", "Ecclesiastes", Testament.OLD),
    SONG_OF_SONGS("Song", "Song of Songs", Testament.OLD),
    ISAIAH("Isa", "Isaiah", Testament.OLD),
    JEREMIAH("Jer", "Jeremiah", Testament.OLD),
    LAMENTATIONS("Lam", "Lamentations", Testament.OLD),
    EZEKIEL("Ezek", "Ezekiel", Testament.OLD),
    DANIEL("Dan", "Daniel", Testament.OLD),
    HOSEA("Hos", "Hosea", Testament.OLD),
    JOEL("Joel", "Joel", Testament.OLD),
    AMOS("Amos", "Amos", Testament.OLD),
    OBADIAH("Obad", "Obadiah", Testament.OLD),
    JONAH("Jonah", "Jonah", Testament.OLD),
    MICAH("Mic", "Micah", Testament.OLD),
    NAHUM("Nah", "Nahum", Testament.OLD),
    HABAKKUK("Hab", "Habakkuk", Testament.OLD),
    ZEPHANIAH("Zeph", "Zephaniah", Testament.OLD),
    HAGGAI("Hag", "Haggai", Testament.OLD),
    ZECHARIAH("Zech", "Zechariah", Testament.OLD),
    MALACHI("Mal", "Malachi", Testament.OLD),

    // --- New Testament ---
    MATTHEW("Matt", "Matthew", Testament.NEW),
    MARK("Mark", "Mark", Testament.NEW),
    LUKE("Luke", "Luke", Testament.NEW),
    JOHN("John", "John", Testament.NEW),
    ACTS("Acts", "Acts", Testament.NEW),
    ROMANS("Rom", "Romans", Testament.NEW),
    FIRST_CORINTHIANS("1Cor", "1 Corinthians", Testament.NEW),
    SECOND_CORINTHIANS("2Cor", "2 Corinthians", Testament.NEW),
    GALATIANS("Gal", "Galatians", Testament.NEW),
    EPHESIANS("Eph", "Ephesians", Testament.NEW),
    PHILIPPIANS("Phil", "Philippians", Testament.NEW),
    COLOSSIANS("Col", "Colossians", Testament.NEW),
    FIRST_THESSALONIANS("1Thess", "1 Thessalonians", Testament.NEW),
    SECOND_THESSALONIANS("2Thess", "2 Thessalonians", Testament.NEW),
    FIRST_TIMOTHY("1Tim", "1 Timothy", Testament.NEW),
    SECOND_TIMOTHY("2Tim", "2 Timothy", Testament.NEW),
    TITUS("Titus", "Titus", Testament.NEW),
    PHILEMON("Phlm", "Philemon", Testament.NEW),
    HEBREWS("Heb", "Hebrews", Testament.NEW),
    JAMES("Jas", "James", Testament.NEW),
    FIRST_PETER("1Pet", "1 Peter", Testament.NEW),
    SECOND_PETER("2Pet", "2 Peter", Testament.NEW),
    FIRST_JOHN("1John", "1 John", Testament.NEW),
    SECOND_JOHN("2John", "2 John", Testament.NEW),
    THIRD_JOHN("3John", "3 John", Testament.NEW),
    JUDE("Jude", "Jude", Testament.NEW),
    REVELATION("Rev", "Revelation", Testament.NEW),
    ;

    companion object {
        fun of(testament: Testament): List<BibleBook> = entries.filter { it.testament == testament }

        fun byOsis(osis: String): BibleBook? = entries.firstOrNull { it.osis.equals(osis, ignoreCase = true) }
    }
}
