package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2DesignDiagnostics
import dev.russiancompanion.v2.curriculum.V2PackLoader
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/**
 * T15f (owner decision D6c, an approved rule change): the A2-26 narration lint also catches source narration,
 * commentary about exercises and assessment, and raw table-cell markup. T15h (D25d): only exact notice and direction
 * sentences are exempt (V2DesignDiagnostics.NOTICE_TEMPLATES, each matched as a whole sentence); no owner is exempt. Blocking for Units 1-10; Units 11-30 are
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

    /** T16c-n1 (D36b): the same text in a binder-direction field (the body of Unit 3's BINDER step, u3-st-binder-direction). */
    private fun binderSignals(text: String, rule: String) = V2DesignDiagnostics.audit(
        current.copy(units = current.units.map { u -> if (u.number != 3) u else u.copy(sessions = u.sessions.map { s ->
            s.copy(steps = s.steps.map { if (it.id == "u3-st-binder-direction") it.copy(body = text) else it })
        }) }),
    ).signals.filter { it.rule == rule && it.unit == 3 && "u3-st-binder-direction" in it.entityIds && it.field == "body" }

    /**
     * T16c-n1 (owner decision D36b, approved rule change): "row" is flagged only as table framing; ordinary meanings and the
     * furniture noun pass; "rows for" is exempt only in binder-direction fields. The six probes of the Codex T16c check.
     */
    @Test fun rowLintFlagsTableFramingOnly() {
        val rule = "LEARNER_PROVENANCE_NARRATION"
        // Probes 1-2: a furniture gloss and the dictionary form pass.
        assertTrue(signals("стол — table", rule).isEmpty())
        assertTrue(signals("The dictionary form of this adjective is masculine singular nominative.", rule).isEmpty())
        // Probe 3: a binder instruction with "rows for" passes in a binder field, and is table framing anywhere else.
        val binder = "In your binder, make a table of the six cases with rows for singular and plural."
        assertTrue(binderSignals(binder, rule).isEmpty())
        assertTrue(signals(binder, rule).isNotEmpty())
        // Probe 4: an ordinary row passes.
        assertTrue(signals("The row of soldiers is outside.", rule).isEmpty())
        // Probe 5: reading a table row is table framing (it passed before D36b).
        assertTrue(signals("Read the row for this case.", rule).isNotEmpty())
        assertTrue(binderSignals("Read the row for this case.", rule).isNotEmpty())
        // Probe 6: the furniture phrase passes (it was flagged before D36b).
        assertTrue(signals("Wipe the surface of the table.", rule).isEmpty())
        // The binder exemption is "rows for" only: other table framing in a binder field is still caught.
        assertTrue(binderSignals("Copy the genitive row into your binder.", rule).isNotEmpty())
    }

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
        // T15h (D25d): no owner is exempt (this was assertFalse for ref-u610-attribution under the D18d owner exemption).
        // The same narration is caught in an attribution reference and in a teaching reference.
        val licence = "Licence notice: the source gives the attribution required by its licence."
        fun referenceHit(id: String, detail: (String) -> String) = V2DesignDiagnostics.audit(current.copy(references = current.references.map {
            if (it.id == id) it.copy(detail = detail(it.detail)) else it
        })).signals.any { it.rule == "LEARNER_PROVENANCE_NARRATION" && id in it.entityIds }
        assertTrue(referenceHit("ref-u610-attribution") { licence })
        assertTrue(referenceHit("ref-ca-cases") { licence })
        // Regression (D25d): commentary appended to the real ref-u610-attribution detail is caught; the detail as shipped,
        // whose only narration sentence is the exact licence notice template, passes.
        assertFalse(referenceHit("ref-u610-attribution") { it })
        assertTrue(referenceHit("ref-u610-attribution") { "$it The independent check retains this exact pair." })
        assertTrue(referenceHit("ref-u610-attribution") { "$it The source pairs this answer with this cue." })
        // A template is exempt only as a whole sentence, not as part of a longer one.
        val notice = "The course records in these units are a separate licensed set and do not copy the book."
        assertFalse(referenceHit("ref-ca-cases") { notice })
        assertTrue(referenceHit("ref-ca-cases") { notice.dropLast(1) + " in any unit." })
    }

    /** T15g (D18d): every manual miss the T15f check found in Units 6-9 is now caught by the widened lint. */
    @Test fun theT15fManualMissesAreCaught() {
        for (text in listOf(
            "Compare the friend with the source chair clause",
            "These steps ask you to recognize the supplied forms only, not to produce endings for other nouns.",
            "Guided practice chooses the supplied object form, without inventing a sentence or new stress.",
            "The independent teacher check retains this exact pair.",
            "The new independent check classifies that inanimate context rather than repeating the teacher answer.",
            "This check does not establish completed action; no aspect or full-translation claim is tested.",
            "Recognize supplied forms and contexts, not unseen or unrestricted productive mastery.",
            "Recognize these supplied contexts; no universal all-case or completed-action claim.",
            "A singular noun does not take the plural form in these choices.",
            "на́ши is plural and does not mark the feminine singular object row.",
            "When the task shows но́вая and asks for that dictionary form, choose но́вый rather than neuter но́вое.",
            "All the options are already correctly spelled.",
            "Use this explanation to understand the rule; do not infer a general adverb-formation rule from these choices.",
            "an unrestricted rule for every adjective goes beyond this check; it asks only about the pair shown.",
            "These two supplied examples do not establish an unrestricted sentence-generation rule.",
            "Guided practice uses мальчик.",
        )) assertTrue(text, signals(text, "LEARNER_PROVENANCE_NARRATION").isNotEmpty())
        // None of the misses remains in Units 6-9.
        val shown = V2DesignDiagnostics.audit(current).signals
        assertTrue(shown.none { it.rule == "LEARNER_PROVENANCE_NARRATION" && (it.unit ?: 0) in 1..19 }) // T16: was 1..10; T16b: 1..11 -> 1..12; T16c: 1..12 -> 1..13; T16d: 1..13 -> 1..14; T16e: 1..14 -> 1..15; T17a: 1..15 -> 1..16; T17b: 1..16 -> 1..17; T17c: 1..17 -> 1..18; T17d: 1..18 -> 1..19
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

    /**
     * T16-n2 (owner decision D30b, approved rule change): review and workflow status, table and record framing, and question
     * framing are caught, including every manual miss of the Codex check at 62b4b084 (items 3 and 9).
     */
    @Test fun workflowTableAndQuestionFramingIsCaught() {
        for (text in listOf(
            // (a) review and workflow status
            "English sound notes are pending qualified review.",
            "English sound notes are pending review.",
            "Detailed phonetics remain in the book until a reviewer signs an app note.",
            "Word, phrase, and sign reading wait for reviewed examples.",
            "Public release stays blocked until review and device evidence exist.",
            "Some units contain proposed lesson bodies that still require qualified review.",
            "This reference does not approve the packaged records.",
            "This is a proposed Lesson 1 mapping, not an approved owned-edition alignment.",
            "A reviewer signs off on this note.",
            // (b) table and record framing
            "Вы бу́дете говори́ть is the second-person plural future row: the auxiliary agrees with you in that row.",
            "Я бу́ду говори́ть is first-person singular and changes the person as well as the table number.",
            "Мы ска́жем is the first-person plural future of the perfective verb in this table, agreeing with we.",
            "столу́ is the recorded dative singular form of сто́л.",
            "The requested row changes case while keeping the noun singular.",
            "Я бу́ду говори́ть is first-person singular future, not the requested second-person plural future row.",
            "стола́х is the recorded prepositional plural form of the shown noun.",
            "The keyed counterexample is incorrect Russian, not a form to use.",
            "Written stress mark from the dictionary",
            "Read the requested form before answering.",
            "до́ма is the singular dative form, so it does not supply the requested plural nominative form.",
            "бы́л is masculine singular past, not the plural past requested here.",
            "Use the requested case or the supplied context to decide a word's sentence role.",
            "Read the ending in each cell.",
            "Two speaker rows",
            "The plural ending differs from the table pair.",
            // (c) question framing
            "Recognizing the English sentence meaning is useful, but the question here is whether the noun is the subject.",
            "хорошое письмо is the rejected spelling requested by this question.",
        )) assertTrue(text, signals(text, "LEARNER_PROVENANCE_NARRATION").isNotEmpty())
    }

    /** T16-n2: ordinary grammar terms, the furniture noun, and the learner's own book and binder directions still pass. */
    @Test fun dictionaryFormFurnitureTableAndBinderDirectionsPass() {
        for (text in listOf(
            "The dictionary form of this adjective is masculine singular nominative.",
            "Look up the dictionary form before you choose.",
            "стол — table",
            "сто́л means table.",
            "The book is on the table.",
            "Put the letter on the table, then sit down.",
            "Open the matching lesson in your book and read the dialogue aloud.",
            "Choose the correct form.",
            "до́ма is the singular dative form, not the plural nominative form.",
            "The cue shows feminine singular, but the answer is plural; past agreement follows the subject number.",
            "Recognizing the English sentence meaning is useful, but also decide whether the noun is the subject or the direct object.",
            "This counterexample is incorrect Russian, not a form to use.",
            "Write the throw and the arrow.",
        )) {
            assertTrue(text, signals(text, "LEARNER_PROVENANCE_NARRATION").isEmpty())
            assertTrue(text, signals(text, "LEARNER_TABLE_CELL_MARKUP").isEmpty())
        }
        // T16c-n1 (D36b): the binder "rows for" directions pass in a binder-direction field (was: in any field).
        for (text in listOf(
            "In your binder, make a table of the six cases with rows for singular and plural.",
            "Make a table with rows for each person, then fill it in from your book.",
        )) assertTrue(text, binderSignals(text, "LEARNER_PROVENANCE_NARRATION").isEmpty())
        // Letter notes (unit null) carry no review status either.
        assertTrue(current.letters.none { "review" in it.englishNote.lowercase() })
    }

    // T16 (owner direction 2026-11-20): Unit 11 joins the blocking set (was Units 1-10); T16b: Unit 12 too (was 1..11);
    // T16c: Unit 13 too (was 1..12); T16d: Unit 14 too (was 1..13); T16e: Unit 15 too (was 1..14); T17a: Unit 16 too (was 1..15);
    // T17b: Unit 17 too (was 1..16); T17c: Unit 18 too (was 1..17); T17d: Unit 19 too (was 1..18).
    @Test fun unitsOneToNineteenHaveNoWidenedLintHits() {
        val hits = V2DesignDiagnostics.audit(current).signals.filter { it.rule in rules && (it.unit ?: 0) in 1..19 }
        assertTrue(hits.joinToString("\n") { "${it.unit} ${it.rule} ${it.entityIds} ${it.field}" }, hits.isEmpty())
    }

    // T16: Units 12-30 stay advisory (was Units 11-30); T16b: Units 13-30; T16c: Units 14-30; T16d: Units 15-30; T16e: Units 16-30; T17a: Units 17-30;
    // T17b: Units 18-30 (was 17-30); T17c: Units 19-30 (was 18-30); T17d: Units 20-30 (was 19-30).
    @Test fun unitsTwentyToThirtyAreReportedNotBlocking() {
        val signals = V2DesignDiagnostics.audit(current).signals.filter { it.rule in rules }
        val counts = (20..30).associateWith { unit -> rules.associateWith { rule -> signals.count { it.unit == unit && it.rule == rule } } }
        println("T15f advisory widened A2-26 hits, Units 20-30: $counts")
        assertEquals((20..30).toSet(), counts.keys)
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
        // The licensing sentence moved to the batch attribution notices; that exact sentence is a notice template (D25d).
        val notices = current.references.filter { it.id.endsWith("-attribution") && "separate licensed set" in it.detail }
        assertEquals(6, notices.size)
        assertTrue(V2DesignDiagnostics.audit(current).signals.none { it.rule == "LEARNER_PROVENANCE_NARRATION" && it.entityIds.any { id -> id in notices.map { n -> n.id } } })
    }
}
