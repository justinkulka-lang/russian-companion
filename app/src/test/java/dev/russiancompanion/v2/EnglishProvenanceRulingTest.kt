package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2Step
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.V2Validator.EnglishProvenanceMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * T15-n0, owner ruling 11/12/2026 and owner correction: prompts, step titles, step directions and instruction-step bodies
 * are authored English (Cyrillic still needs a source); feedback, remediation and distractor-rationale sentences are
 * authored English unless they contain Cyrillic, quote a pack gloss, answer, option or cue, or carry the "russian-fact"
 * marker; teaching bodies, glosses, answers, options, accepted answers, cues and examples keep the full requirement.
 */
class EnglishProvenanceRulingTest {
    private val englishError = "English linguistic claim has no admitted source record"

    @Test
    fun unsourcedEnglishPromptPasses() {
        val pack = pack()
        val item = unitItem(pack, 3)
        val prompt = "Which of the two shown cards fits the situation described in the lesson?"
        assertTrue(pack.provenanceRecords.none { prompt in it.shippedText } && pack.authoredEnglishRecords.none { prompt in it.text })
        val changed = pack.copy(items = pack.items.map { if (it.id == item.id) it.copy(prompt = prompt) else it })
        assertEquals(errors(pack), errors(changed))
        val title = withStep(pack, 3, "TEACH") { it.copy(title = "Before the market visit") }
        assertEquals(errors(pack), errors(title))
        val instruction = withStep(pack, 3, "GUIDED") { it.copy(body = it.body + " Take your time and think about the situation first.") }
        assertEquals(errors(pack), errors(instruction))
    }

    @Test
    fun unsourcedEnglishFeedbackSentencePasses() {
        val pack = pack()
        val item = unitItem(pack, 3)
        val feedback = pack.copy(items = pack.items.map { if (it.id == item.id) it.copy(feedback = it.feedback + " Look again at the first card, then try once more.") else it })
        assertEquals(errors(pack), errors(feedback))
        val remediation = withStep(pack, 3, "REMEDIATE") { it.copy(body = it.body + " Go back over the cards slowly before you try again.") }
        assertEquals(errors(pack), errors(remediation))
    }

    @Test
    fun unsourcedGlossFails() {
        val pack = pack()
        val item = unitItem(pack, 3)
        val cue = pack.copy(items = pack.items.map { if (it.id == item.id) it.copy(promptLetter = "towns and villages") else it })
        assertTrue(errors(cue).any { it.contains(englishError) })
        val inTeaching = withStep(pack, 3, "TEACH") { it.copy(body = it.body + " The second word means a small wooden raft.") }
        assertTrue(errors(inTeaching).any { it.contains(englishError) })
    }

    @Test
    fun feedbackQuotingAPackGlossFails() {
        val pack = pack()
        val gloss = pack.items.first { candidate ->
            candidate.answer.none { it.code in 0x0400..0x04FF } && candidate.answer.any(Char::isLetter) && candidate.answer.length > 3
        }.answer
        val item = unitItem(pack, 3)
        val quoted = pack.copy(items = pack.items.map { if (it.id == item.id) it.copy(feedback = it.feedback + " Compare your choice with \"$gloss\" here.") else it })
        assertTrue(errors(quoted).any { it.contains(englishError) })
        // The same sentence quoting a string the pack never shows as a gloss, answer, option or cue is authored English.
        val neutral = pack.copy(items = pack.items.map { if (it.id == item.id) it.copy(feedback = it.feedback + " Compare your choice with \"Next card\" here.") else it })
        assertEquals(errors(pack), errors(neutral))
    }

    @Test
    fun unsourcedRussianStringFails() {
        val pack = pack()
        val item = unitItem(pack, 3)
        val prompt = pack.copy(items = pack.items.map { if (it.id == item.id) it.copy(prompt = "Which card fits привет here?") else it })
        val promptErrors = errors(prompt)
        assertTrue(promptErrors.any { it.contains("Russian text has no admitted source record") })
        assertTrue(promptErrors.any { it.contains(englishError) })
        val feedback = pack.copy(items = pack.items.map { if (it.id == item.id) it.copy(feedback = it.feedback + " Try привет next time.") else it })
        assertTrue(errors(feedback).any { it.contains(englishError) })
    }

    @Test
    fun unsourcedEnglishStatementOfRussianGrammarFactFails() {
        val pack = pack()
        val teaching = withStep(pack, 3, "TEACH") { it.copy(body = it.body + " Russian nouns have three genders.") }
        assertTrue(errors(teaching).any { it.contains(englishError) })
        val fact = "The noun after two is in the genitive singular."
        val item = unitItem(pack, 3)
        val marked = pack.copy(items = pack.items.map { if (it.id == item.id) it.copy(feedback = it.feedback + " $fact", russianFacts = listOf(fact)) else it })
        val markedErrors = errors(marked)
        assertTrue(markedErrors.any { it.contains(englishError) })
        assertTrue(markedErrors.none { it.contains("russian-fact marker") })
        // Without the marker the same sentence is authored feedback; the owner decides which sentences to mark.
        val unmarked = pack.copy(items = pack.items.map { if (it.id == item.id) it.copy(feedback = it.feedback + " $fact") else it })
        assertEquals(errors(pack), errors(unmarked))
    }

    @Test
    fun russianFactMarkerOnRemediationFailsWithoutSourceAndMustMatchItsText() {
        val pack = pack()
        val fact = "Feminine nouns usually end in a vowel letter."
        val marked = withStep(pack, 3, "REMEDIATE") { it.copy(body = it.body + " " + fact, russianFacts = listOf(fact)) }
        assertTrue(errors(marked).any { it.contains(englishError) })
        val stray = withStep(pack, 3, "REMEDIATE") { it.copy(russianFacts = listOf("A sentence that is not in the body.")) }
        assertTrue(errors(stray).any { it.contains("russian-fact marker on step") })
        val item = unitItem(pack, 3)
        val strayItem = pack.copy(items = pack.items.map { if (it.id == item.id) it.copy(russianFacts = listOf("Not in the feedback.")) else it })
        assertTrue(errors(strayItem).any { it.contains("russian-fact marker on item ${item.id}") })
    }

    @Test
    fun teachingBodySentenceWithoutSourceStillFails() {
        val pack = pack()
        val teaching = withStep(pack, 3, "TEACH") { it.copy(body = it.body + " Take your time and think about the situation first.") }
        assertTrue(errors(teaching).any { it.contains(englishError) })
    }

    @Test
    fun existingSourcedItemStillPassesAndAnswersKeepTheRequirement() {
        val pack = pack()
        assertTrue(errors(pack).none { it.contains(englishError) || it.contains("russian-fact marker") })
        val item = pack.items.first { candidate ->
            candidate.id in unitItemIds(pack, 3) && candidate.options.isNotEmpty() && candidate.options.none { it.any { c -> c.code in 0x0400..0x04FF } }
        }
        assertTrue(pack.provenanceRecords.any { it.unitNumber == 3 && it.shippedText == item.answer })
        val replacement = "Choose the admitted record."
        val changed = pack.copy(
            items = pack.items.map { candidate ->
                if (candidate.id != item.id) candidate else candidate.copy(
                    answer = replacement,
                    options = candidate.options.map { if (it == item.answer) replacement else it },
                )
            },
        )
        assertTrue(errors(changed).any { it.contains("$englishError: $replacement") })
    }

    @Test
    fun nothingThatPassesTodayStartsFailing() {
        val pack = pack()
        val targets = V2Validator.englishQuoteTargets(pack)
        val hosts = V2Validator.englishProvenanceHosts(pack)
        assertTrue(hosts.isNotEmpty())
        hosts.forEach { host ->
            val ranges = V2Validator.exemptEnglishRanges(host, targets)
            if (host.mode == EnglishProvenanceMode.FULL) assertTrue(ranges.isEmpty())
            ranges.forEach { range -> assertTrue(range.first >= 0 && range.last < host.text.length) }
        }
        // Exempt ranges are added after source spans, so they can only remove leftovers: the committed pack, which passed
        // before T15-n0, still has no English provenance error, and every teaching body keeps the full requirement.
        assertTrue(errors(pack).none { it.contains(englishError) })
        assertTrue(hosts.filter { it.field == "teaching body" }.all { it.mode == EnglishProvenanceMode.FULL })
    }

    private fun errors(pack: V2CurriculumPack): List<String> = V2Validator.validate(pack).errors

    private fun unitItemIds(pack: V2CurriculumPack, number: Int): Set<String> =
        pack.units.first { it.number == number }.sessions.flatMap { it.steps }.flatMap { it.itemIds }.toSet()

    private fun unitItem(pack: V2CurriculumPack, number: Int) = pack.items.first { it.id in unitItemIds(pack, number) }

    private fun withStep(pack: V2CurriculumPack, number: Int, kind: String, edit: (V2Step) -> V2Step): V2CurriculumPack {
        val target = pack.units.first { it.number == number }.sessions.flatMap { it.steps }.first { it.kind == kind }
        return pack.copy(
            units = pack.units.map { unit ->
                if (unit.number != number) unit else unit.copy(
                    sessions = unit.sessions.map { session ->
                        session.copy(steps = session.steps.map { if (it.id == target.id) edit(it) else it })
                    },
                )
            },
        )
    }

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
