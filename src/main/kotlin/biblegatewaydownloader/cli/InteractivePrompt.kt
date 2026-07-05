package biblegatewaydownloader.cli

import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.jline.utils.NonBlockingReader
import java.io.IOError

/** Raised when the user aborts an interactive prompt (Ctrl-C / Esc / EOF). */
class PromptAbortedException : RuntimeException("Prompt aborted")

/**
 * Interactive terminal prompts: a colored, arrow-navigable list with
 * type-to-filter (autocompletion). Falls back to plain numbered input when
 * no interactive terminal is available.
 */
object InteractivePrompt {

    private const val ESC = "\u001b"
    private const val RESET = "$ESC[0m"
    private const val BOLD = "$ESC[1m"
    private const val DIM = "$ESC[2m"
    private const val CYAN = "$ESC[36m"
    private const val GREEN = "$ESC[32m"
    private const val YELLOW = "$ESC[33m"
    private const val GREY = "$ESC[90m"

    private const val PAGE_SIZE = 10

    /**
     * Prompt the user to pick one item from [items].
     *
     * @param label prompt text shown to the user
     * @param items selectable items (non-empty)
     * @param render renders an item to its display string (also used for filtering)
     */
    fun <T> select(label: String, items: List<T>, render: (T) -> String): T {
        require(items.isNotEmpty()) { "Cannot select from an empty list" }

        val terminal = runCatching {
            TerminalBuilder.builder().system(true).build()
        }.getOrNull()

        return if (terminal == null || terminal.type == Terminal.TYPE_DUMB) {
            terminal?.close()
            fallbackSelect(label, items, render)
        } else {
            terminal.use { interactiveSelect(it, label, items, render) }
        }
    }

    private fun <T> interactiveSelect(
        terminal: Terminal,
        label: String,
        items: List<T>,
        render: (T) -> String,
    ): T {
        val writer = terminal.writer()
        val reader: NonBlockingReader = terminal.reader()
        val attributes = terminal.enterRawMode()

        var query = ""
        var cursor = 0
        var offset = 0
        var renderedLines = 0

        fun filtered(): List<T> {
            if (query.isBlank()) return items
            val q = query.lowercase()
            return items.filter { render(it).lowercase().contains(q) }
        }

        try {
            writer.write("$ESC[?25l") // hide cursor
            while (true) {
                val list = filtered()
                if (cursor > list.lastIndex) cursor = maxOf(0, list.lastIndex)
                if (cursor < offset) offset = cursor
                if (cursor >= offset + PAGE_SIZE) offset = cursor - PAGE_SIZE + 1
                offset = offset.coerceIn(0, maxOf(0, list.size - PAGE_SIZE))

                val sb = StringBuilder()
                if (renderedLines > 0) sb.append("$ESC[${renderedLines}A") // move to top
                sb.append("$ESC[0J") // clear downward

                sb.append("$GREEN?$RESET $BOLD$label$RESET ")
                sb.append("$GREY(type to filter, \u2191/\u2193 to move, Enter to select)$RESET")
                if (query.isNotEmpty()) sb.append("  $CYAN$query$RESET")
                sb.append("\r\n")

                val window = list.drop(offset).take(PAGE_SIZE)
                if (window.isEmpty()) {
                    sb.append("  $YELLOW(no matches)$RESET\r\n")
                }
                window.forEachIndexed { i, item ->
                    val globalIndex = offset + i
                    val text = render(item)
                    if (globalIndex == cursor) {
                        sb.append("$CYAN\u276f $BOLD$text$RESET\r\n")
                    } else {
                        sb.append("  $DIM$text$RESET\r\n")
                    }
                }

                var lines = 1 + if (window.isEmpty()) 1 else window.size
                if (list.size > PAGE_SIZE) {
                    sb.append("$GREY  \u2026 ${list.size} matches, showing ${offset + 1}-${offset + window.size}$RESET\r\n")
                    lines += 1
                }
                renderedLines = lines

                writer.write(sb.toString())
                writer.flush()

                when (val key = readKey(reader)) {
                    Key.ENTER -> {
                        if (list.isNotEmpty()) {
                            val chosen = list[cursor]
                            clearAndPrintResult(writer, renderedLines, label, render(chosen))
                            return chosen
                        }
                    }
                    Key.UP -> if (list.isNotEmpty()) cursor = if (cursor == 0) list.lastIndex else cursor - 1
                    Key.DOWN -> if (list.isNotEmpty()) cursor = if (cursor == list.lastIndex) 0 else cursor + 1
                    Key.BACKSPACE -> if (query.isNotEmpty()) { query = query.dropLast(1); cursor = 0; offset = 0 }
                    Key.ABORT -> throw PromptAbortedException()
                    is Key.Char -> { query += key.value; cursor = 0; offset = 0 }
                    Key.OTHER -> {}
                }
            }
        } finally {
            writer.write("$ESC[?25h") // show cursor
            writer.flush()
            terminal.setAttributes(attributes)
        }
    }

    private fun clearAndPrintResult(
        writer: java.io.Writer,
        renderedLines: Int,
        label: String,
        value: String,
    ) {
        val sb = StringBuilder()
        if (renderedLines > 0) sb.append("$ESC[${renderedLines}A")
        sb.append("$ESC[0J")
        sb.append("$GREEN\u2714$RESET $BOLD$label$RESET $CYAN$value$RESET\r\n")
        writer.write(sb.toString())
        writer.flush()
    }

    private sealed interface Key {
        data object ENTER : Key
        data object UP : Key
        data object DOWN : Key
        data object BACKSPACE : Key
        data object ABORT : Key
        data object OTHER : Key
        data class Char(val value: kotlin.Char) : Key
    }

    private fun readKey(reader: NonBlockingReader): Key {
        val c = try {
            reader.read()
        } catch (_: IOError) {
            return Key.ABORT
        }
        return when (c) {
            -1, 3, 4 -> Key.ABORT // EOF / Ctrl-C / Ctrl-D
            13, 10 -> Key.ENTER
            127, 8 -> Key.BACKSPACE
            27 -> { // escape sequence
                if (reader.peek(5) == '['.code) {
                    reader.read() // consume '['
                    when (reader.read()) {
                        'A'.code -> Key.UP
                        'B'.code -> Key.DOWN
                        else -> Key.OTHER
                    }
                } else {
                    Key.ABORT // bare Esc
                }
            }
            in 32..126 -> Key.Char(c.toChar())
            else -> Key.OTHER
        }
    }

    private fun <T> fallbackSelect(label: String, items: List<T>, render: (T) -> String): T {
        val out = System.out
        out.println(label)
        items.forEachIndexed { i, item -> out.println("  ${i + 1}. ${render(item)}") }
        while (true) {
            out.print("Enter number (1-${items.size}): ")
            out.flush()
            val line = readlnOrNull()?.trim() ?: throw PromptAbortedException()
            val choice = line.toIntOrNull()
            if (choice != null && choice in 1..items.size) return items[choice - 1]
            out.println("Invalid selection.")
        }
    }
}
