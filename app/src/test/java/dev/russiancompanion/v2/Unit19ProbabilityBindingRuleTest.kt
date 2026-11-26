package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/**
 * T17d (owner decision D2, approved rule change): the Unit 19 probability capability stays bound to the likelihood span of
 * Wiktionary должен 84965088 (W:39-41) for its cue and keyed answer, and its questions may add competing meanings drawn from
 * other frozen sources. A frozen competing meaning is accepted; an unsourced one (content-audit research) is rejected; the cue
 * and answer must still share the likelihood span.
 */
class Unit19ProbabilityBindingRuleTest {
    private val pack by lazy { V2PackLoader.parse(File(if (File("app").isDirectory) "." else "..", "app/src/main/assets/content/v2-curriculum.json").readText()) }
    private fun record(id: String) = pack.provenanceRecords.single { it.itemId == id }
    private val cue get() = record("prov-ex-u19-probability-0-ru") // Затме́ние должно́ наступи́ть в три часа́. (W:41)
    private val answer get() = record("prov-ex-u19-probability-0-en") // The eclipse should occur at three o'clock. (W:41)

    @Test fun aFrozenCompetingMeaningIsAccepted() {
        assertEquals(BatchARemainingSources.LIKELIHOOD_SPAN, cue.originalSourceText)
        // Employees must report to work on time. (W:38, the obligation sense of the same frozen page and revision)
        val employees = record("prov-ex-u19-probability-1-en")
        assertNotEquals(BatchARemainingSources.LIKELIHOOD_SPAN, employees.originalSourceText)
        assertEquals("EXPLICIT_SOURCE_PAIR", V2Validator.classifyRelation(cue, answer, listOf(employees)))
        // The same-span distractor still classifies, alone or with the frozen competing meaning.
        val sense = record("prov-u19-probability-rem")
        assertEquals("EXPLICIT_SOURCE_PAIR", V2Validator.classifyRelation(cue, answer, listOf(sense)))
        assertEquals("EXPLICIT_SOURCE_PAIR", V2Validator.classifyRelation(cue, answer, listOf(employees, sense)))
    }

    @Test fun anUnsourcedCompetingMeaningIsRejected() {
        val employees = record("prov-ex-u19-probability-1-en")
        // Content-audit research synthesis ("an expected event") is not a frozen source.
        val unsourced = record("prov-ca-u1920-probability-contrast-2")
        assertNotNull(unsourced.synthesis)
        assertNull(V2Validator.classifyRelation(cue, answer, listOf(unsourced)))
        assertNull(V2Validator.classifyRelation(cue, answer, listOf(employees, unsourced)))
        // A frozen record whose text no longer matches its source hash is rejected.
        val tampered = employees.copy(originalSourceText = employees.originalSourceText + " ")
        assertNull(V2Validator.classifyRelation(cue, answer, listOf(tampered)))
        // A Russian distractor is not a competing English meaning.
        assertNull(V2Validator.classifyRelation(cue, answer, listOf(record("prov-ex-u19-probability-1-ru"))))
        // The keyed answer must still come from the likelihood span: the obligation gloss is not the eclipse answer.
        assertNull(V2Validator.classifyRelation(cue, employees, listOf(answer)))
    }
}
