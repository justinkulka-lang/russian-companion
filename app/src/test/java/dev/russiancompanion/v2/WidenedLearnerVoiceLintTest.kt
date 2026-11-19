package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2DesignDiagnostics
import dev.russiancompanion.v2.curriculum.V2PackLoader
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/**
 * T15f (owner decision D6c, an approved rule change): the A2-26 narration lint also catches source narration,
 * commentary about exercises and assessment, and raw table-cell markup. T15g (D18d): only the notice sentences are exempt,
 * the ref-*-attribution licence notices by owner and the D8 self-attestation sentence by exact template. Blocking for Units 1-10; Units 11-30 are
 * reported here and each later unit task makes them blocking for its own unit.
 */
class WidenedLearnerVoiceLintTest {
    private val root = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
    private val current: V2CurriculumPack by lazy {
        V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText())
    }
    private val rules = setOf("LEARNER_PROVENANCE_NARRATION", "LEARNER_TABLE_CELL_MARKUP")

    private fun signals(text: String, rule: String) = V2DesignDiagnostics.audit(
        current.copy(units = current.units.map { u -> if (u.number != 3) u else u.copy(topicSummary = text) }),
    ).signals.filter { it.rule == rule && it.unit == 3 && it.field == "topicSummary" }

    @Test fun eachWidenedCategoryIsCaught() {
        for (text in listOf(
            "The frozen note says two, three, and four take the genitive.",
            "The frozen source pairs the shown form with this gloss.",
            "The frozen example uses this as the agreeing reply.",
            "The dictionary classifies this noun as neuter.",
            "The dictionary lists this form.",
            "The retained source illustrates the relation.",
            "The current source gives this order.",
            "The source pairs С уважением with Respectfully.",
            "The guided choice asks which form matches.",
            "The checks recognize the supplied forms only.",
            "Selecting the label does not demonstrate applying the rule.",
            // D8 (Codex T15e check): source framing and exercise commentary.
            "Source example",
            "Keep the same first-person singular referent when moving to the genitive row.",
            "Их is the bare genitive table form of Они, meaning they.",
            "этих женщин is the animate plural accusative object phrase in the source example meaning I like these women.",
            "These choices practise recognition of the supplied pair; they do not supply unseen sentence production.",
            "Quantity patterns are separate from the simple nominative possessed thing in the son checks.",
            "The proposed records in this unit are a separate licensed set and do not copy the book.",
            "This unit does not teach a possession pattern that leaves out the verb shown in its examples.",
            "No audio is mapped here.",
            "Keep each whole source line with its own supplied translation.",
            "These locators are pending reviewer assignment and are not an approved mapping.",
            "Book activity is not a demonstrated app check.",
            "Tutor work is optional and does not count as demonstrated ability.",
            "Open Lesson 6 in the copy you own. The app records do not replace that lesson.",
            "The choices below ask which Russian expression names the kind of words adverbs are.",
            "These choices test the requested form of the adjective.",
        )) assertTrue(text, signals(text, "LEARNER_PROVENANCE_NARRATION").isNotEmpty())
        for (text in listOf("| Дательный", "двумя matches Творительный (\"| Творительный\").", "| I/me || Я || Меня"))
            assertTrue(text, signals(text, "LEARNER_TABLE_CELL_MARKUP").isNotEmpty())
    }

    /**
     * T15g (owner decision D18d): an exemption keyword no longer excuses a sentence. The T15f check's bypass examples and
     * the five sentences this test used to list as exempt (each a pattern hit next to a keyword) are now caught.
     */
    @Test fun anExemptionKeywordNoLongerExcusesNarration() {
        for (text in listOf(
            "The source pairs this answer with this cue.",
            "The source pairs this answer with this cue for contributors.",
            "The guided choice asks which form matches and credits the contributors.",
            "The source pairs this answer with this cue under the CC BY-SA 4.0 attribution.",
            "Your book: the dictionary lists this form.",
            "Self-attest AnkiDroid work. It remains user-reported and does not demonstrate a check.",
            "Wikibooks contributors, CC BY-SA 4.0, and the source lists every contributor in its page history.",
            "Licence notice: the source gives the attribution required by its licence.",
            "Open your binder: the table shows your own notes.",
            "In your book, the table lists the endings.",
        )) assertTrue(text, signals(text, "LEARNER_PROVENANCE_NARRATION").isNotEmpty())
        assertTrue(signals("| Дательный (contributors)", "LEARNER_TABLE_CELL_MARKUP").isNotEmpty())
        // A notice template only exempts itself: the sentence next to it is still linted.
        assertTrue(signals("Marking this done records your own report; it is not scored. The source pairs this answer with this cue.",
            "LEARNER_PROVENANCE_NARRATION").isNotEmpty())
        // The licence notices themselves are exempt by owner, not by wording: the same text in a ref-*-attribution detail
        // passes, and in a teaching reference it is caught.
        val licence = "Licence notice: the source gives the attribution required by its licence."
        fun referenceHit(id: String) = V2DesignDiagnostics.audit(current.copy(references = current.references.map {
            if (it.id == id) it.copy(detail = licence) else it
        })).signals.any { it.rule == "LEARNER_PROVENANCE_NARRATION" && id in it.entityIds }
        assertFalse(referenceHit("ref-u610-attribution"))
        assertTrue(referenceHit("ref-ca-cases"))
    }

    @Test fun exemptNoticesAndPlainTeachingAreNotCaught() {
        for (text in listOf(
            "These words are masculine.",
            "Answer without looking back.",
            // D8 keeps short operating instructions and the one-sentence self-attestation notice.
            "Answer without looking back. A miss returns to the explanation.",
            "A missed check returns you to the explanation.",
            "Read the whole shown line, then choose the complete case explanation. A missed check returns to all teaching and examples.",
            "Compare your answer with the worked examples for this form, then try again.",
            "Stress marks are optional when you type.",
            "Marking this done records your own report; it is not scored.",
            "Marking this done records your own check; it is not scored.",
            "На э́той у́лице есть францу́зы — There are Frenchmen on this street. This positive line states that something exists.",
        )) {
            assertTrue(text, signals(text, "LEARNER_PROVENANCE_NARRATION").isEmpty())
            assertTrue(text, signals(text, "LEARNER_TABLE_CELL_MARKUP").isEmpty())
        }
        // The pre-T15f patterns keep their strictness: an exemption word does not excuse them.
        assertTrue(signals("Self-attest: the dictionary gives this meaning.", "LEARNER_PROVENANCE_NARRATION").isNotEmpty())
    }

    @Test fun unitsOneToTenHaveNoWidenedLintHits() {
        val hits = V2DesignDiagnostics.audit(current).signals.filter { it.rule in rules && (it.unit ?: 0) in 1..10 }
        assertTrue(hits.joinToString("\n") { "${it.unit} ${it.rule} ${it.entityIds} ${it.field}" }, hits.isEmpty())
    }

    @Test fun unitsElevenToThirtyAreReportedNotBlocking() {
        val signals = V2DesignDiagnostics.audit(current).signals.filter { it.rule in rules }
        val counts = (11..30).associateWith { unit -> rules.associateWith { rule -> signals.count { it.unit == unit && it.rule == rule } } }
        println("T15f advisory widened A2-26 hits, Units 11-30: $counts")
        assertEquals((11..30).toSet(), counts.keys)
    }

    @Test fun companionAndBookDirectionsKeepOneNoticeAndNoApprovalLanguage() {
        // D8: MA2-076 (AnkiDroid self-attestation label), MA2-082 and A2-32 (self-attested work and binder self-checks are
        // not demonstrated performance) keep one short notice; nothing else in the directions narrates review or scoring.
        val forbidden = Regex("pending reviewer|approved mapping|approve a mapping|demonstrated (?:app |in-app )?(?:check|ability)|" +
            "No audio is mapped|separate licensed set|does not teach|stays out of this unit|do not replace that lesson", RegexOption.IGNORE_CASE)
        for (unit in current.units) {
            val steps = unit.sessions.flatMap { it.steps }
            steps.filter { it.kind in setOf("BOOK", "ANKI", "BINDER", "TUTOR") || it.id.endsWith("-st-cumulative-direction") }.forEach { step ->
                listOfNotNull(step.body, step.bookDirection, step.companionDirection).forEach {
                    assertFalse("${step.id}: $it", forbidden.containsMatchIn(it))
                }
            }
            assertTrue(steps.any { it.kind == "ANKI" } && steps.any { it.kind == "BINDER" })
            steps.filter { it.kind == "ANKI" }.forEach {
                assertEquals(it.id, "Marking this done records your own report; it is not scored.", it.companionDirection)
            }
            steps.filter { it.kind == "BINDER" }.forEach {
                assertEquals(it.id, "Marking this done records your own check; it is not scored.", it.companionDirection)
            }
            if (unit.number >= 3) assertTrue(unit.number.toString(), steps.any { it.kind == "BOOK" &&
                it.body.endsWith("mark it done here. Marking this done records your own report; it is not scored.") })
        }
        // The licensing sentence moved to the batch attribution notices, which the added patterns exempt.
        val notices = current.references.filter { it.id.endsWith("-attribution") && "separate licensed set" in it.detail }
        assertEquals(6, notices.size)
        assertTrue(V2DesignDiagnostics.audit(current).signals.none { it.rule == "LEARNER_PROVENANCE_NARRATION" && it.entityIds.any { id -> id in notices.map { n -> n.id } } })
    }
}
