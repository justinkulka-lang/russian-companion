package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.V2DesignDiagnostics
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * T16d (owner decision D32b): every hardcoded string literal in the main-source UI code passes the learner-voice lint
 * (governance terms, provenance and widened narration, table-cell markup), as the pack's learner fields do. Template
 * expressions inside a literal (${...}, $name) are replaced by a neutral word before the lint runs.
 */
class UiStringLintTest {
    private val root = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
    private val uiFiles: List<File> by lazy {
        (File(root, "app/src/main/java/dev/russiancompanion/v2/ui").listFiles()!!.toList() +
            File(root, "app/src/main/java/dev/russiancompanion/presentation").listFiles()!!.toList())
            .filter { it.extension == "kt" }.sortedBy { it.name }
    }

    /**
     * Literals the learner never sees as written: V2ReviewStatusLabels holds the D31b status labels (owner wording, the one
     * status a learner sees), and V2AnswerFeedback's nonExplanatoryFragments is a filter matched against pack rationales.
     * Identifier-like literals (test tags, keys, routes, enum names) are not learner text.
     */
    private val exemptFiles = setOf("V2ReviewStatusLabels.kt")
    private fun identifierLike(literal: String) = literal.none(Char::isWhitespace) &&
        (literal.any { it == '-' || it == '_' || it == '$' } || literal.all { !it.isLetter() || it.isUpperCase() })
    private fun nonExplanatoryFragmentLines(source: String): IntRange {
        val lines = source.lines()
        val start = lines.indexOfFirst { it.contains("val nonExplanatoryFragments") }
        if (start < 0) return IntRange.EMPTY
        val end = (start until lines.size).first { lines[it].trim() == ")" }
        return (start + 1)..(end + 1)
    }

    @Test fun everyHardcodedUiStringPassesTheLearnerLint() {
        val hits = uiFiles.filter { it.name !in exemptFiles }.flatMap { file ->
            val filterLines = nonExplanatoryFragmentLines(file.readText())
            uiLiterals(file.readText()).filter { (line, literal) -> line !in filterLines && !identifierLike(literal) }.mapNotNull { (line, literal) ->
                val rules = V2DesignDiagnostics.learnerVoiceRules(neutral(literal))
                if (rules.isEmpty()) null else "${file.name}:$line $rules \"$literal\""
            }
        }
        assertTrue(hits.joinToString("\n"), hits.isEmpty())
        assertTrue(uiFiles.sumOf { uiLiterals(it.readText()).size } > 200)
    }

    @Test fun theScanCatchesAHardcodedNarrationLiteral() {
        // Regression: the replaced feedback prefix and a workflow sentence would be reported if they came back.
        val probe = "Text(\"Not the reviewed answer. \${item.feedback}\")\nV2Status(\"Release stays blocked until review.\")\n" +
            "Text(\"Choose the requested form.\")\nText(\"Not quite. Try again.\")"
        val found = uiLiterals(probe).filter { V2DesignDiagnostics.learnerVoiceRules(neutral(it.second)).isNotEmpty() }.map { it.second }
        assertEquals(listOf("Release stays blocked until review.", "Choose the requested form."), found)
        // "Not the reviewed answer." (replaced by "Not quite. Try again.", D32b) is caught as a fixed literal, not by a
        // pattern: it must not come back.
        assertTrue(uiFiles.none { "Not the reviewed answer" in it.readText() })
    }

    /** Ordinary double-quoted Kotlin literals with their 1-based line numbers (raw triple-quoted strings are not used in UI). */
    private fun uiLiterals(source: String): List<Pair<Int, String>> {
        val out = mutableListOf<Pair<Int, String>>()
        var i = 0; var line = 1
        while (i < source.length) {
            val c = source[i]
            when {
                c == '\n' -> { line++; i++ }
                source.startsWith("//", i) -> { while (i < source.length && source[i] != '\n') i++ }
                source.startsWith("/*", i) -> { val end = source.indexOf("*/", i + 2).let { if (it < 0) source.length else it + 2 }
                    line += source.substring(i, end).count { it == '\n' }; i = end }
                source.startsWith("\"\"\"", i) -> { val end = source.indexOf("\"\"\"", i + 3).let { if (it < 0) source.length else it + 3 }
                    line += source.substring(i, end).count { it == '\n' }; i = end }
                c == '\'' -> { i++; while (i < source.length && source[i] != '\'') { if (source[i] == '\\') i++; i++ }; i++ }
                c == '"' -> {
                    val start = line; val sb = StringBuilder(); i++
                    var depth = 0
                    while (i < source.length) {
                        val d = source[i]
                        if (depth == 0 && d == '"') break
                        if (d == '\\' && depth == 0) { sb.append(source.getOrNull(i + 1) ?: ' '); i += 2; continue }
                        if (d == '$' && source.getOrNull(i + 1) == '{' && depth == 0) { depth = 1; sb.append("\${"); i += 2; continue }
                        if (depth > 0) { if (d == '{') depth++; if (d == '}') depth--; sb.append(d); i++; continue }
                        if (d == '\n') line++
                        sb.append(d); i++
                    }
                    i++
                    if (sb.any(Char::isLetter)) out += start to sb.toString()
                }
                else -> i++
            }
        }
        return out
    }

    private fun neutral(literal: String): String =
        literal.replace(Regex("\\$\\{[^}]*}|\\$[A-Za-z_][A-Za-z0-9_.]*"), "value")
}
