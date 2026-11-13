package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2AuthoredEnglishRecord
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
    fun unsourcedCueAndTeachingGlossFail() {
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
    fun unsourcedInstructionActuallyUsesTheFieldExemption() {
        val pack = pack()
        val instruction = "Take your time and think about the situation first."
        val changed = withStep(pack, 3, "GUIDED") { it.copy(body = instruction) }
        val host = V2Validator.englishProvenanceHosts(changed).first { it.field == "instruction body" && it.text == instruction }
        val spans = pack.provenanceRecords.filter { it.unitNumber == 3 }.map { it.shippedText } +
            pack.authoredEnglishRecords.filter { it.unitNumber == 3 }.map { it.text }
        // The pre-T15-n0 span-only rule rejects this exact unsourced fixture.
        assertTrue(V2Validator.unboundLetters(instruction, spans).isNotEmpty())
        val exemptions = V2Validator.exemptEnglishRanges(host, V2Validator.englishQuoteTargets(changed))
        assertTrue(instruction.indices.filter { instruction[it].isLetter() }.all { i -> exemptions.any { i in it } })
        assertEquals("", V2Validator.unboundLetters(instruction, spans, exemptions))
        assertEquals(errors(pack), errors(changed))
        // The same wording remains unsourced when placed in teaching rather than an instruction field.
        assertTrue(errors(withStep(pack, 3, "TEACH") { it.copy(body = instruction) }).any { it.contains(englishError) })
    }

    @Test
    fun feedbackQuotingPunctuatedOptionOrCueFails() {
        val pack = pack()
        val option = pack.items.flatMap { it.options }.first { it.endsWith(".") && it.all { c -> c.code < 128 } && it.length > 15 }
        val cue = pack.items.mapNotNull { it.promptLetter }.first { '?' in it && it.all { c -> c.code < 128 } }
        assertTrue(cue in pack.items.mapNotNull { it.promptLetter })
        val contraction = pack.items.flatMap { it.options }.first {
            '\'' in it && it.last() in ".!?" && it.all { c -> c.code < 128 }
        }
        val item = unitItem(pack, 3)
        // A punctuation-only English fixture also covers exclamation marks without adding Russian content.
        val exclamation = "Fixture choice!"
        val withOption = pack.copy(items = pack.items.map { if (it.id == item.id) it.copy(options = it.options + exclamation) else it })
        listOf(option to pack, cue to pack, contraction to pack, exclamation to withOption).forEach { (target, fixture) ->
            listOf("\"$target\"", "“$target”", "'$target'").forEach { quote ->
                val text = "Consider $quote before continuing."
                val changed = fixture.copy(items = fixture.items.map { if (it.id == item.id) it.copy(feedback = text) else it })
                assertTrue("Missing source error for $quote", errors(changed).any { it.contains(englishError) && "Consider" in it })
            }
        }
    }

    @Test
    fun feedbackQuotingANonTargetHeadingPasses() {
        val pack = pack()
        val heading = pack.provenanceRecords.first { it.shippedText == "Recorded masculine nouns" }.shippedText
        assertTrue(heading.lowercase() !in V2Validator.englishQuoteTargets(pack))
        val item = unitItem(pack, 3)
        val changed = pack.copy(items = pack.items.map {
            if (it.id == item.id) it.copy(feedback = "Consider \"$heading\" before continuing.") else it
        })
        assertEquals(errors(pack), errors(changed))
    }

    @Test
    fun scaffoldCoveredRussianFactMarkerStillNeedsSource() {
        val pack = pack()
        val sentence = "Consider this assertion before continuing."
        val item = unitItem(pack, 3)
        val scaffold = V2AuthoredEnglishRecord("test-marked-scaffold", 3, sentence, "INDEPENDENTLY_AUTHORED_SCAFFOLD")
        val fixture = pack.copy(authoredEnglishRecords = pack.authoredEnglishRecords + scaffold)
        val unmarked = fixture.copy(items = fixture.items.map { if (it.id == item.id) it.copy(feedback = sentence) else it })
        assertEquals(errors(pack), errors(unmarked))
        val marked = unmarked.copy(items = unmarked.items.map { if (it.id == item.id) it.copy(russianFacts = listOf(sentence)) else it })
        assertTrue(errors(marked).any { it.contains(englishError) && "marked sentence" in it })
    }

    @Test
    fun unitOneRussianFactMarkerStillNeedsSource() {
        val pack = pack()
        val item = unitItem(pack, 1)
        val sentence = "Consider this assertion before continuing."
        val unmarked = pack.copy(items = pack.items.map { if (it.id == item.id) it.copy(feedback = sentence) else it })
        assertEquals(errors(pack), errors(unmarked))
        val marked = unmarked.copy(items = unmarked.items.map { if (it.id == item.id) it.copy(russianFacts = listOf(sentence)) else it })
        assertTrue(errors(marked).any { it.contains(englishError) && "marked sentence in unit 1" in it })
    }

    @Test
    fun optionalReferenceItemMarkerStillNeedsSource() {
        val pack = pack()
        val item = pack.items.first { it.id == "u30-i-letter-names-1" }
        val sentence = "Consider this assertion before continuing."
        assertTrue(V2Validator.englishProvenanceHosts(pack).none { it.ownerId == item.id })
        val changed = pack.copy(items = pack.items.map {
            if (it.id == item.id) it.copy(feedback = sentence, russianFacts = listOf(sentence)) else it
        })
        assertTrue(errors(changed).any { it.contains(englishError) && "marked sentence in unit 30" in it })
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
