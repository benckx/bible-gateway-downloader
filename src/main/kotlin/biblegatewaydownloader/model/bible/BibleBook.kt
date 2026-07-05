package biblegatewaydownloader.model.bible

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
    val wikipediaSlug: String,
) {
    // --- Old Testament ---
    GENESIS("Gen", "Genesis", Testament.OLD, "Book_of_Genesis"),
    EXODUS("Exod", "Exodus", Testament.OLD, "Book_of_Exodus"),
    LEVITICUS("Lev", "Leviticus", Testament.OLD, "Book_of_Leviticus"),
    NUMBERS("Num", "Numbers", Testament.OLD, "Book_of_Numbers"),
    DEUTERONOMY("Deut", "Deuteronomy", Testament.OLD, "Book_of_Deuteronomy"),
    JOSHUA("Josh", "Joshua", Testament.OLD, "Book_of_Joshua"),
    JUDGES("Judg", "Judges", Testament.OLD, "Book_of_Judges"),
    RUTH("Ruth", "Ruth", Testament.OLD, "Book_of_Ruth"),
    FIRST_SAMUEL("1Sam", "1 Samuel", Testament.OLD, "Books_of_Samuel"),
    SECOND_SAMUEL("2Sam", "2 Samuel", Testament.OLD, "Books_of_Samuel"),
    FIRST_KINGS("1Kgs", "1 Kings", Testament.OLD, "Books_of_Kings"),
    SECOND_KINGS("2Kgs", "2 Kings", Testament.OLD, "Books_of_Kings"),
    FIRST_CHRONICLES("1Chr", "1 Chronicles", Testament.OLD, "Books_of_Chronicles"),
    SECOND_CHRONICLES("2Chr", "2 Chronicles", Testament.OLD, "Books_of_Chronicles"),
    EZRA("Ezra", "Ezra", Testament.OLD, "Book_of_Ezra"),
    NEHEMIAH("Neh", "Nehemiah", Testament.OLD, "Book_of_Nehemiah"),
    ESTHER("Esth", "Esther", Testament.OLD, "Book_of_Esther"),
    JOB("Job", "Job", Testament.OLD, "Book_of_Job"),
    PSALMS("Ps", "Psalm", Testament.OLD, "Psalms"),
    PROVERBS("Prov", "Proverbs", Testament.OLD, "Book_of_Proverbs"),
    ECCLESIASTES("Eccl", "Ecclesiastes", Testament.OLD, "Ecclesiastes"),
    SONG_OF_SONGS("Song", "Song of Songs", Testament.OLD, "Song_of_Songs"),
    ISAIAH("Isa", "Isaiah", Testament.OLD, "Book_of_Isaiah"),
    JEREMIAH("Jer", "Jeremiah", Testament.OLD, "Book_of_Jeremiah"),
    LAMENTATIONS("Lam", "Lamentations", Testament.OLD, "Book_of_Lamentations"),
    EZEKIEL("Ezek", "Ezekiel", Testament.OLD, "Book_of_Ezekiel"),
    DANIEL("Dan", "Daniel", Testament.OLD, "Book_of_Daniel"),
    HOSEA("Hos", "Hosea", Testament.OLD, "Book_of_Hosea"),
    JOEL("Joel", "Joel", Testament.OLD, "Book_of_Joel"),
    AMOS("Amos", "Amos", Testament.OLD, "Book_of_Amos"),
    OBADIAH("Obad", "Obadiah", Testament.OLD, "Book_of_Obadiah"),
    JONAH("Jonah", "Jonah", Testament.OLD, "Book_of_Jonah"),
    MICAH("Mic", "Micah", Testament.OLD, "Book_of_Micah"),
    NAHUM("Nah", "Nahum", Testament.OLD, "Book_of_Nahum"),
    HABAKKUK("Hab", "Habakkuk", Testament.OLD, "Book_of_Habakkuk"),
    ZEPHANIAH("Zeph", "Zephaniah", Testament.OLD, "Book_of_Zephaniah"),
    HAGGAI("Hag", "Haggai", Testament.OLD, "Book_of_Haggai"),
    ZECHARIAH("Zech", "Zechariah", Testament.OLD, "Book_of_Zechariah"),
    MALACHI("Mal", "Malachi", Testament.OLD, "Book_of_Malachi"),

    // --- New Testament ---
    MATTHEW("Matt", "Matthew", Testament.NEW, "Gospel_of_Matthew"),
    MARK("Mark", "Mark", Testament.NEW, "Gospel_of_Mark"),
    LUKE("Luke", "Luke", Testament.NEW, "Gospel_of_Luke"),
    JOHN("John", "John", Testament.NEW, "Gospel_of_John"),
    ACTS("Acts", "Acts", Testament.NEW, "Acts_of_the_Apostles"),
    ROMANS("Rom", "Romans", Testament.NEW, "Epistle_to_the_Romans"),
    FIRST_CORINTHIANS("1Cor", "1 Corinthians", Testament.NEW, "First_Epistle_to_the_Corinthians"),
    SECOND_CORINTHIANS("2Cor", "2 Corinthians", Testament.NEW, "Second_Epistle_to_the_Corinthians"),
    GALATIANS("Gal", "Galatians", Testament.NEW, "Epistle_to_the_Galatians"),
    EPHESIANS("Eph", "Ephesians", Testament.NEW, "Epistle_to_the_Ephesians"),
    PHILIPPIANS("Phil", "Philippians", Testament.NEW, "Epistle_to_the_Philippians"),
    COLOSSIANS("Col", "Colossians", Testament.NEW, "Epistle_to_the_Colossians"),
    FIRST_THESSALONIANS("1Thess", "1 Thessalonians", Testament.NEW, "First_Epistle_to_the_Thessalonians"),
    SECOND_THESSALONIANS("2Thess", "2 Thessalonians", Testament.NEW, "Second_Epistle_to_the_Thessalonians"),
    FIRST_TIMOTHY("1Tim", "1 Timothy", Testament.NEW, "First_Epistle_to_Timothy"),
    SECOND_TIMOTHY("2Tim", "2 Timothy", Testament.NEW, "Second_Epistle_to_Timothy"),
    TITUS("Titus", "Titus", Testament.NEW, "Epistle_to_Titus"),
    PHILEMON("Phlm", "Philemon", Testament.NEW, "Epistle_to_Philemon"),
    HEBREWS("Heb", "Hebrews", Testament.NEW, "Epistle_to_the_Hebrews"),
    JAMES("Jas", "James", Testament.NEW, "Epistle_of_James"),
    FIRST_PETER("1Pet", "1 Peter", Testament.NEW, "First_Epistle_of_Peter"),
    SECOND_PETER("2Pet", "2 Peter", Testament.NEW, "Second_Epistle_of_Peter"),
    FIRST_JOHN("1John", "1 John", Testament.NEW, "First_Epistle_of_John"),
    SECOND_JOHN("2John", "2 John", Testament.NEW, "Second_Epistle_of_John"),
    THIRD_JOHN("3John", "3 John", Testament.NEW, "Third_Epistle_of_John"),
    JUDE("Jude", "Jude", Testament.NEW, "Epistle_of_Jude"),
    REVELATION("Rev", "Revelation", Testament.NEW, "Book_of_Revelation"),
    ;

    companion object {
        fun of(testament: Testament): List<BibleBook> = entries.filter { it.testament == testament }

        fun byOsis(osis: String): BibleBook? = entries.firstOrNull { it.osis.equals(osis, ignoreCase = true) }
    }
}
