package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/**
 * T16c-n1 (owner decision D39a, approved rule change): a shipped selection starts and ends on word boundaries within its
 * displayed frozen text. Codex's T17c/T17d-check probe ("port" cut from "report") is refused for every caller of the displayed
 * check; a whole-word selection still passes. An apostrophe inside a word is a word character; one used as a quotation mark
 * around a gloss is not.
 */
class SelectionWordBoundaryRuleTest {
    private val pack by lazy { V2PackLoader.parse(File(if (File("app").isDirectory) "." else "..", "app/src/main/assets/content/v2-curriculum.json").readText()) }
    private fun record(id: String) = pack.provenanceRecords.single { it.itemId == id }

    @Test fun aMidWordSelectionIsRefusedAndAWholeWordPasses() {
        val line = "Employees must report to work on time."
        assertFalse(V2Validator.containsWhole(line, "port"))
        assertTrue(V2Validator.containsWhole(line, "report"))
        assertTrue(V2Validator.containsWhole(line, line))
        assertFalse(V2Validator.containsWhole("o'clock", "clock"))
        assertTrue(V2Validator.containsWhole("'my table' (x)", "my table"))
        assertTrue(V2Validator.containsWhole("кото́рый", "кото́рый"))
        assertFalse(V2Validator.containsWhole("кото́рый", "рый"))
    }

    @Test fun thePortProbeFailsTheClassifierAndTheWholeSelectionPasses() {
        val cue = record("prov-ex-u19-probability-0-ru")
        val answer = record("prov-ex-u19-probability-0-en")
        val employees = record("prov-ex-u19-probability-1-en") // W:38, "Employees must report to work on time."
        assertEquals("EXPLICIT_SOURCE_PAIR", V2Validator.classifyRelation(cue, answer, listOf(employees)))
        // The same frozen line, shipping "port" cut from "report": refused (it classified before D39a).
        assertNull(V2Validator.classifyRelation(cue, answer, listOf(employees.copy(shippedText = "port"))))
        // A whole word of the same line still passes.
        assertEquals("EXPLICIT_SOURCE_PAIR", V2Validator.classifyRelation(cue, answer, listOf(employees.copy(shippedText = "report"))))
    }

    @Test fun everyExistingSelectionRecordIsAWholeWordSelection() {
        // The pack validates with the rule in place: no existing selection record in Units 1-30 cuts through a word.
        val validation = V2Validator.validate(pack)
        assertTrue(validation.errors.joinToString("\n"), validation.ok)
    }
}
