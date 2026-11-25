package dev.russiancompanion.v2

/**
 * T16c-n1/n2 (owner decision D37a): the Kotlin string literals of one source file, for the learner-voice lint of hardcoded
 * strings. Ordinary and raw (triple-quoted) literals are both extracted, and so is every literal nested inside a template
 * expression (`"${if (x) "Release stays blocked." else ""}"`), each as its own literal. Each literal carries the name of the
 * innermost call it is an argument of, so an identifier-like literal is exempt only where it is used as an identifier.
 *
 * T16c-n3 (owner decision D46f): escapes (\uXXXX, \n, \t, \r, \b, \", \', \\, \$) are decoded in ordinary literals; the call
 * name no longer exempts anything (UiStringLintTest uses it only to refuse an exemption for text passed straight to a renderer).
 */
internal object UiLiteralScanner {
    /** [argument] is the 0-based position among the call's arguments; [named] is the parameter name of a named argument. */
    data class Literal(val line: Int, val text: String, val call: String?, val role: Role, val argument: Int = -1, val named: String? = null)

    private class Open(val name: String?, var argStart: Int = 0) { var argument = 0 }

    private const val BLOCK = "{block}"
    private fun open(name: String?) = Open(name)

    /** How the literal is used, as far as the source text shows. */
    enum class Role { ARGUMENT, WHEN_LABEL, COMPARISON, MAP_KEY, OTHER }

    fun literals(source: String): List<Literal> {
        val out = mutableListOf<Literal>()
        scan(source, 0, source.length, 1, mutableListOf(), out)
        return out.sortedWith(compareBy({ it.line }, { it.text }))
    }

    /**
     * Scans code in [from, to) and returns the line number reached. [calls] is the stack of open parentheses, each with the
     * identifier just before it (null for a bare group).
     */
    private fun scan(s: String, from: Int, to: Int, startLine: Int, calls: MutableList<Open>, out: MutableList<Literal>): Int {
        var i = from
        var line = startLine
        while (i < to) {
            val c = s[i]
            when {
                c == '\n' -> { line++; i++ }
                s.startsWith("//", i) -> { while (i < to && s[i] != '\n') i++ }
                s.startsWith("/*", i) -> {
                    val end = s.indexOf("*/", i + 2).let { if (it < 0) to else it + 2 }
                    line += s.substring(i, end).count { it == '\n' }; i = end
                }
                c == '\'' -> { i++; while (i < to && s[i] != '\'') { if (s[i] == '\\') i++; i++ }; i++ }
                c == '(' -> { calls += Open(identifierBefore(s, i), i + 1); i++ }
                c == ',' -> { calls.lastOrNull()?.let { it.argument++; it.argStart = i + 1 }; i++ }
                c == ')' -> { if (calls.isNotEmpty()) calls.removeAt(calls.size - 1); i++ }
                // A lambda or block body is its own context: a literal in it is not an argument of the enclosing call.
                c == '{' -> { calls += open(BLOCK); i++ }
                c == '}' -> { val at = calls.indexOfLast { it.name == BLOCK }; if (at >= 0) while (calls.size > at) calls.removeAt(calls.size - 1); i++ }
                c == '"' -> {
                    val raw = s.startsWith("\"\"\"", i)
                    val begin = i
                    val r = literal(s, i, to, line, raw, calls, out)
                    val text = r.first
                    i = r.second
                    val role = role(s, begin, i)
                    val top = calls.lastOrNull()?.takeIf { it.name != BLOCK }
                    if (text.any(Char::isLetter)) out += Literal(line, text, top?.name, role, top?.argument ?: -1, top?.let { namedArgument(s, it.argStart, begin) })
                    line += s.substring(begin, i).count { it == '\n' }
                }
                else -> i++
            }
        }
        return line
    }

    /** Reads one literal starting at [start]; nested template expressions are scanned as code. Returns (text, end). */
    private fun literal(s: String, start: Int, to: Int, line: Int, raw: Boolean, calls: MutableList<Open>, out: MutableList<Literal>): Pair<String, Int> {
        val sb = StringBuilder()
        var i = start + if (raw) 3 else 1
        var at = line
        while (i < to) {
            if (raw && s.startsWith("\"\"\"", i)) { i += 3; while (i < to && s[i] == '"') { sb.append('"'); i++ }; return sb.toString() to i }
            val d = s[i]
            if (!raw && d == '"') return sb.toString() to i + 1
            if (!raw && d == '\\') {
                // T16c-n3 (owner decision D46f): escapes are decoded before the lint reads the text, so an escaped word is
                // checked as the word it renders (was: the escape letter was kept and the rest copied, "A" read as "u0041").
                val e = s.getOrNull(i + 1) ?: ' '
                val hex = s.substring(minOf(i + 2, to), minOf(i + 6, to))
                when {
                    e == 'u' && hex.length == 4 && hex.all { it.isDigit() || it.lowercaseChar() in 'a'..'f' } -> { sb.append(hex.toInt(16).toChar()); i += 6 }
                    else -> { sb.append(when (e) { 'n' -> '\n'; 't' -> '\t'; 'r' -> '\r'; 'b' -> '\b'; else -> e }); i += 2 }
                }
                continue
            }
            if (d == '$' && s.getOrNull(i + 1) == '{') {
                // The template expression is code: its own literals are extracted separately.
                var depth = 1
                var j = i + 2
                while (j < to && depth > 0) {
                    when {
                        s[j] == '{' -> depth++
                        s[j] == '}' -> depth--
                        s[j] == '"' -> { j = skipLiteral(s, j, to); continue }
                    }
                    j++
                }
                // A literal nested in the template is rendered wherever the enclosing literal is: keep its call context.
                val inner = calls.map { o -> Open(o.name, o.argStart).also { it.argument = o.argument } }.toMutableList()
                scan(s, i + 2, j - 1, at, inner, out)
                sb.append("\${}")
                at += s.substring(i, j).count { it == '\n' }
                i = j
                continue
            }
            if (d == '\n') at++
            sb.append(d); i++
        }
        return sb.toString() to i
    }

    private fun skipLiteral(s: String, start: Int, to: Int): Int {
        if (s.startsWith("\"\"\"", start)) return s.indexOf("\"\"\"", start + 3).let { if (it < 0) to else it + 3 }
        var i = start + 1
        var depth = 0
        while (i < to) {
            val d = s[i]
            if (depth == 0 && d == '"') return i + 1
            if (depth == 0 && d == '\\') { i += 2; continue }
            if (d == '$' && s.getOrNull(i + 1) == '{') { depth++; i += 2; continue }
            if (depth > 0 && d == '"') { i = skipLiteral(s, i, to); continue }
            if (depth > 0 && d == '}') depth--
            i++
        }
        return to
    }

    /**
     * The parameter name when the literal is in a named argument (`tag = "x"`, `tag = if (a) "x" else "y"`) or is a declared
     * default (`tag: String = "x"`), else null.
     */
    private fun namedArgument(s: String, argStart: Int, begin: Int): String? =
        Regex("^\\s*(?:(?:private |internal |override )?(?:val |var ))?([A-Za-z_][A-Za-z0-9_]*)\\s*(?::[^=]*)?=(?!=)")
            .find(s.substring(argStart, begin))?.groupValues?.get(1)

    private fun identifierBefore(s: String, paren: Int): String? {
        var j = paren - 1
        while (j >= 0 && s[j] == ' ') j--
        val end = j + 1
        while (j >= 0 && (s[j].isLetterOrDigit() || s[j] == '_')) j--
        return if (end > j + 1) s.substring(j + 1, end) else null
    }

    private fun role(s: String, begin: Int, end: Int): Role {
        val after = s.substring(end, minOf(s.length, end + 6)).trimStart(' ')
        var b = begin - 1
        while (b >= 0 && s[b] == ' ') b--
        val before = s.substring(maxOf(0, b - 1), b + 1)
        return when {
            after.startsWith("->") || (after.startsWith(",") && lineStartsWithLiteral(s, begin)) -> Role.WHEN_LABEL
            before.endsWith("==") || before.endsWith("!=") || after.startsWith("==") || after.startsWith("!=") ||
                after.startsWith("in ") || after.startsWith("!in ") -> Role.COMPARISON
            after.startsWith("to ") || after.startsWith("to\n") -> Role.MAP_KEY
            s.getOrNull(b) == '(' || s.getOrNull(b) == ',' -> Role.ARGUMENT
            else -> Role.OTHER
        }
    }

    private fun lineStartsWithLiteral(s: String, begin: Int): Boolean {
        val lineStart = s.lastIndexOf('\n', begin - 1) + 1
        return s.substring(lineStart, begin).isBlank() && s.indexOf("->", begin).let { it > 0 && '\n' !in s.substring(begin, it) }
    }
}
