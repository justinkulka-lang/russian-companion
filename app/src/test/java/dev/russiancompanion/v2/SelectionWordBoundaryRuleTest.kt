package dev.russiancompanion.v2

import dev.russiancompanion.testing.pythonProcess
import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * T16c-n1 (owner decision D39a, approved rule change): a shipped selection starts and ends on word boundaries within its
 * displayed frozen text. Codex's T17c/T17d-check probe ("port" cut from "report") is refused for every caller of the displayed
 * check; a whole-word selection still passes.
 *
 * T16c-n2 (owner decisions D41b-D41d, approved rule changes): the edges are judged in context, inside the frozen line: the
 * characters just before and just after the selection must not be word characters, and an apostrophe is a word character when
 * the line has letters on both sides of it ("'clock" and "o'" from "o'clock" fail). Kotlin and the Python selection helper agree
 * on every probe. Yo and ye stay distinct. Every one of the 2,839 selection records is checked directly against its frozen
 * line with the validator's own display normalization (was: validating the pack as a whole).
 */
class SelectionWordBoundaryRuleTest {
    private val root = File(if (File("app").isDirectory) "." else "..")
    private val pack by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()) }
    private fun record(id: String) = pack.provenanceRecords.single { it.itemId == id }

    /** (frozen text, selection, expected) for both implementations. */
    private val probes = listOf(
        Triple("Employees must report to work on time.", "port", false),
        Triple("Employees must report to work on time.", "report", true),
        Triple("Employees must report to work on time.", "Employees must report to work on time.", true),
        Triple("at one o'clock today", "o'clock", true),
        Triple("at one o'clock today", "'clock", false),
        Triple("at one o'clock today", "o'", false),
        Triple("at one o'clock today", "clock", false),
        Triple("at one o’clock today", "’clock", false),
        Triple("at one o’clock today", "o’clock", true),
        Triple("'my table' (x)", "my table", true),
        Triple("'my table' (x)", "'my table'", true),
        Triple("кото́рый", "кото́рый", true),
        Triple("кото́рый", "рый", false),
        Triple("кото́рый", "кото", false),
        Triple("well-known", "well", true),
    )

    @Test fun selectionEdgesAreJudgedInContextInKotlinAndPython() {
        probes.forEach { (text, part, expected) -> assertEquals("$text / $part", expected, V2Validator.containsWhole(text, part)) }
        val catalog = File(root, "catalog").canonicalPath.replace("\\", "/")
        val cases = probes.joinToString(",") { (t, p, _) -> "(${py(t)},${py(p)})" }
        val script = "import sys\nsys.path.insert(0, ${py(catalog)})\nimport apply_d7b_selection_records as d\n" +
            "print(''.join('1' if d.contains_whole(t, p) else '0' for t, p in [$cases]))\n"
        val process = pythonProcess("-I", "-c", script).redirectErrorStream(true).start()
        assertTrue(process.waitFor(60, TimeUnit.SECONDS))
        val output = process.inputStream.bufferedReader().readText().trim()
        assertEquals(output, 0, process.exitValue())
        assertEquals(probes.joinToString("") { if (it.third) "1" else "0" }, output)
    }

    @Test fun thePortAndApostropheProbesFailTheClassifierAndTheWholeSelectionPasses() {
        val cue = record("prov-ex-u19-probability-0-ru")
        val answer = record("prov-ex-u19-probability-0-en")
        val employees = record("prov-ex-u19-probability-1-en") // W:38, "Employees must report to work on time."
        assertEquals("EXPLICIT_SOURCE_PAIR", V2Validator.classifyRelation(cue, answer, listOf(employees)))
        assertNull(V2Validator.classifyRelation(cue, answer, listOf(employees.copy(shippedText = "port"))))
        assertEquals("EXPLICIT_SOURCE_PAIR", V2Validator.classifyRelation(cue, answer, listOf(employees.copy(shippedText = "report"))))
        // D41b: a genuine frozen line containing "o'clock" (ReadyRussian Grammar 20), with only its shipped selection changed.
        val clock = record("prov-ex-u17-clock-productive-0-en")
        assertTrue(clock.originalSourceText.contains("one o'clock"))
        assertTrue(V2Validator.displayedInFrozen(clock.copy(shippedText = "o'clock")))
        assertFalse(V2Validator.displayedInFrozen(clock.copy(shippedText = "'clock")))
        assertFalse(V2Validator.displayedInFrozen(clock.copy(shippedText = "o'")))
    }

    @Test fun yoAndYeStayDistinct() {
        // D41c: a selection that spells yo as ye no longer matches its frozen line (it did through the governed fold).
        val yo = pack.provenanceRecords.first { r ->
            r.transformation.contains("selection") && r.shippedText.contains('ё') && V2Validator.displayedInFrozen(r)
        }
        assertFalse(V2Validator.displayedInFrozen(yo.copy(shippedText = yo.shippedText.replace('ё', 'е'))))
        assertFalse(V2Validator.containsWhole("моё", "мое"))
    }

    @Test fun everySelectionRecordIsAWholeWordSelectionOfItsFrozenLine() {
        val selections = pack.provenanceRecords.filter { it.transformation.contains("selection") }
        // T16c-n2 expectation change: 2839 -> 2842. The 2,839 records named in D41d plus the three D40a letter-cell records
        // (prov-t16cn2-key-u1-i-type-{short i, hard sign, soft sign}) added by this task; all 2,842 are checked.
        // T17e expectation change: 2842 -> 2888. The T17e Unit 20 selection records (prov-t17e-u20-*: cues, keys, forms and fact
        // evidence lines) join them; all are checked.
        // T17f expectation change: 2888 -> 2889. The D50g frozen selection record prov-t17f-u17-age-forty-one-years (the numbers
        // page record prov-ex-u17-years-3-en bound to obj-u17-age) joins them; all are checked, and the D41d count excludes it.
        // T17f-n1 expectation change (owner decision D53d), old -> new: 2889 -> 2891; the two frozen selection records of the
        // redesigned date check (the expanded form, the all-numeric date) are whole-word selections of their date-notation span;
        // the D41d count excludes them as it excludes the earlier tasks' records.
        // T18a-2 expectation change (owner decision D54e), old -> new: 2891 -> 2942; the 51 frozen selection records of the Unit 21
        // typed keys, their cues and the Russian the new teaching shows (prov-t18a2-u21-) are whole-word selections of their lines;
        // the D41d count excludes them as it excludes the earlier tasks' records.
        // T18b expectation change (owner decision D56c), old -> new: 2942 -> 2959; the 17 frozen selection records of the Unit 22
        // typed keys, their nominative cues and the forms the new teaching shows (prov-t18b-u22-) join them; the D41d count
        // excludes them.
        // T18c-2 expectation change (D57e, D61): old -> new
        // 2959 -> 2979: 10 typed-key, 9 fact/grammar and 1 tutor-direction selection records in Unit 23.
        // T18d (D62): 2979 -> 2983 selection records (six new, two Unit 24 S1 records removed).
        // The historical subset also moves 2839 -> 2837 because it included those two removed records.
        // T18e (D64): 2983 -> 3022 whole-word selection records, all 39 additions in Unit 25.
        assertEquals(3022, selections.size)
        assertEquals(2837, selections.count { !it.itemId.startsWith("prov-t16cn2-key-") && !it.itemId.startsWith("prov-t17e-u20-") &&
            !it.itemId.startsWith("prov-t17f-u17-age-") && !it.itemId.startsWith("prov-t17fn1-u17-calendar-date-") &&
            !it.itemId.startsWith("prov-t18a2-u21-") && !it.itemId.startsWith("prov-t18b-u22-") &&
            // T18e (D64): exclude the new Unit 25 selections from the historical subset.
            !it.itemId.startsWith("prov-t18c2-u23-") && !it.itemId.startsWith("prov-t18d-u24-") && !it.itemId.startsWith("prov-t18e-u25-") })
        val failing = selections.filterNot { V2Validator.displayedInFrozen(it) }
            .map { "${it.itemId} ${it.transformation} \"${it.shippedText}\"" }
        assertTrue(failing.joinToString("\n"), failing.isEmpty())
        // The three records Codex's direct recheck flagged pass under the validator's display normalization (OpenRussian stress
        // apostrophe read as the stress mark; inline HTML joined).
        for (id in listOf("prov-d7b-u26-039", "prov-d7b-u26-044", "prov-d7b-u30-056")) assertTrue(id, V2Validator.displayedInFrozen(record(id)))
    }

    /** A Python string literal in ASCII only (the test JVM may not pass non-ASCII arguments through unchanged). */
    private fun py(text: String) = "'" + text.map { c ->
        when {
            c == '\\' -> "\\\\"
            c == '\'' -> "\\'"
            c.code < 128 -> c.toString()
            else -> "\\u%04x".format(c.code)
        }
    }.joinToString("") + "'"
}
