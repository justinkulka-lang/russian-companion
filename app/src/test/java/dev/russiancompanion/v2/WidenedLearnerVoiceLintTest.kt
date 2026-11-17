package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2DesignDiagnostics
import dev.russiancompanion.v2.curriculum.V2PackLoader
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/**
 * T15f (owner decision D6c, an approved rule change): the A2-26 narration lint also catches source narration,
 * commentary about exercises and assessment, and raw table-cell markup. Self-attestation, licence and attribution
 * notices and the learner's own book and binder directions are exempt. Blocking for Units 1-10; Units 11-30 are
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

    @Test fun exemptNoticesAndPlainTeachingAreNotCaught() {
        for (text in listOf(
            "Self-attest AnkiDroid work. It remains user-reported and does not demonstrate a check.",
            "Wikibooks contributors, CC BY-SA 4.0, and the source lists every contributor in its page history.",
            "Licence notice: the source gives the attribution required by its licence.",
            "Open your binder: the table shows your own notes.",
            "In your book, the table lists the endings.",
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
