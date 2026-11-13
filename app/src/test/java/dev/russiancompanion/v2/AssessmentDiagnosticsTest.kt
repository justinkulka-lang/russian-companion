package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import java.io.File
import org.junit.Assert.*
import org.junit.Test

/** English engineering fixtures, never curriculum, reviewed content, or owner acceptance. */
class AssessmentDiagnosticsTest {
    private fun choice(id: String) = current.items.first().copy(id = id, kind = "FORM_SELECTION",
        objectiveId = "objective", prompt = "Apply the noun rule", promptLetter = "cue",
        options = listOf("key", "wrong", "other"), answer = "key", feedback = "Apply the rule.",
        typedAnswerKey = null, dictationAudioAssetId = null)

    private fun typed(id: String): V2CheckItem {
        val key = V2TypedAnswerKey("key-$id", 1, listOf("key", "variant"))
        return choice(id).copy(kind = "TYPED_FORM", options = emptyList(), distractorRationales = emptyList(),
            typedAnswerKey = key.copy(review = V2TypedKeyReview("English engineering fixture", RESEARCH_VERIFIED, typedAnswerKeyDigest(key))))
    }

    private fun pack(items: List<V2CheckItem>, number: Int = 3, required: List<String> = items.map { it.id },
                     teaching: String = "Apply the pattern to a new noun.", practice: List<String> = emptyList()): V2CurriculumPack {
        val objective = V2Objective("objective", number, "Engineering fixture", "PRODUCTION",
            checkItemIds = required, instructionStepIds = listOf("teach"))
        val session = V2Session("session", "Fixture", 1, objectiveIds = listOf("objective"), steps = listOf(
            V2Step("teach", "TEACH", "Pattern", teaching),
            V2Step("practice", "PRACTICE", "Practice", "Apply the rule", itemIds = practice)))
        return current.copy(units = listOf(current.unit(3)!!.copy(number = number, objectives = listOf(objective), sessions = listOf(session))),
            items = items, examples = emptyList(), references = emptyList())
    }

    private fun rules(p: V2CurriculumPack) = V2AssessmentDiagnostics.audit(p).second.map { it.rule }.toSet()

    @Test fun exactlyTwentyAndThirtyPercentMeetMinimumsButNineteenOrFiveTypedDoNot() {
        val enough = (0 until 20).map { if (it < 6) typed("item-$it") else choice("item-$it") }
        assertFalse(rules(pack(enough)).any { it.startsWith("A2_23") })
        assertTrue("A2_23_CHECKED_MINIMUM" in rules(pack(enough.dropLast(1))))
        assertTrue("A2_23_TYPED_MINIMUM" in rules(pack(enough.mapIndexed { n, i -> if (n == 5) choice(i.id) else i })))
        assertFalse("A2_23_TYPED_MINIMUM" in rules(pack(enough.map { choice(it.id) }, number = 2)))
    }

    @Test fun duplicateReferencesAndOrphanItemsCannotMeetMinimumsAndMissingKeysCannotImproveTypedRatio() {
        val p = pack(listOf(typed("typed")) + (1..25).map { choice("orphan-$it") }, required = List(30) { "typed" })
        assertEquals(1, V2AssessmentDiagnostics.audit(p).first.single().checkedItems)
        val missing = pack(listOf(typed("typed")), required = listOf("typed") + (1..5).map { "missing-$it" })
        assertTrue("A2_23_TYPED_MINIMUM" in rules(missing))
        assertEquals(5, V2AssessmentDiagnostics.audit(missing).second.count { it.rule == "A2_24_MISSING_REQUIRED_ITEM" })
        val unavailable = typed("typed").copy(typedAnswerKey = typed("typed").typedAnswerKey!!.copy(review = null))
        assertEquals(0, V2AssessmentDiagnostics.audit(pack(listOf(unavailable))).first.single().typedRequiredChecks)
    }

    @Test fun checkedPracticeCountsButOnlyRequiredChecksDetermineTypedPercentage() {
        val all = (0 until 20).map { if (it == 0) typed("item-$it") else choice("item-$it") }
        val row = V2AssessmentDiagnostics.audit(pack(all, required = listOf("item-0"), practice = all.map { it.id })).first.single()
        assertEquals(20, row.checkedItems)
        assertEquals(1, row.requiredChecks)
        assertEquals(1, row.typedRequiredChecks)
    }

    @Test fun eachValidityRuleLocatesItsOwnItemUnitAndTeachingStep() {
        val a = choice("prompt").copy(prompt = "Choose key from the options")
        val b = choice("cue").copy(options = listOf("key", "cue"))
        val c = choice("label").copy(prompt = "Which label is in the first option?")
        val d = typed("typed").copy(prompt = "Enter variant")
        // The checks sit in the teaching step's own session (T14-n1 placement rule).
        val signals = V2AssessmentDiagnostics.audit(pack(listOf(a, b, c, d), teaching = "The exact form is key.",
            practice = listOf(a, b, c, d).map { it.id })).second
        assertEquals(listOf("prompt", "typed"), signals.filter { it.rule == "A2_24_KEY_IN_PROMPT" }.flatMap { it.entityIds }.sorted())
        assertEquals(listOf("cue"), signals.single { it.rule == "A2_24_ONLY_DISTRACTOR_IS_CUE" }.entityIds)
        assertEquals(listOf("label"), signals.single { it.rule == "A2_24_LABEL_OR_POSITION" }.entityIds)
        assertEquals(4, signals.count { it.rule == "A2_24_EXACT_KEY_IN_TEACHING" && it.relatedStepIds == listOf("teach") })
        assertTrue(signals.all { it.unit == 3 })
    }

    @Test fun exactKeyInTeachingFlagsOnlyThePrecedingStepOrTheSameSession() {
        val item = choice("item")
        fun placed(vararg sessions: List<V2Step>): V2CurriculumPack {
            val objective = V2Objective("objective", 3, "Engineering fixture", "PRODUCTION",
                checkItemIds = listOf("item"), instructionStepIds = listOf("teach"))
            return current.copy(units = listOf(current.unit(3)!!.copy(objectives = listOf(objective), sessions = sessions.mapIndexed { n, steps ->
                V2Session("session-$n", "Fixture $n", 1, objectiveIds = listOf("objective"), steps = steps) })),
                items = listOf(item), examples = emptyList(), references = emptyList())
        }
        fun flagged(p: V2CurriculumPack) = V2AssessmentDiagnostics.audit(p).second
            .filter { it.rule == "A2_24_EXACT_KEY_IN_TEACHING" }.flatMap { it.relatedStepIds }
        val teach = V2Step("teach", "TEACH", "Pattern", "The exact form is key.")
        val other = V2Step("other", "TEACH", "Other", "A different rule.")
        val book = V2Step("book", "BOOK", "Book", "Work the lesson in your copy.")
        val check = V2Step("check", "CHECK", "Check", "Independent check.", itemIds = listOf("item"))
        // Same session: the teaching step states the key.
        assertEquals(listOf("teach"), flagged(placed(listOf(teach, check))))
        // Directly preceding step, across a session boundary.
        assertEquals(listOf("teach"), flagged(placed(listOf(teach), listOf(check))))
        // Taught in an earlier session and recalled after other work: no signal.
        assertEquals(emptyList<String>(), flagged(placed(listOf(teach), listOf(book, check))))
        // Teaching in the same session that does not state the key: no signal.
        assertEquals(emptyList<String>(), flagged(placed(listOf(teach), listOf(other, book, check))))
        // An item no step presents is not disclosed by any teaching step.
        assertEquals(emptyList<String>(), flagged(placed(listOf(teach, book))))
    }

    @Test fun hiddenCueExtraDistractorAndSubstringDoNotGiveFalseMechanicalDisclosure() {
        val a = choice("hidden").copy(options = listOf("key", "cue"), promptLetterRole = "ANSWER_PROVENANCE")
        val b = choice("extra")
        val c = choice("substring").copy(answer = "a", options = listOf("a", "b"), prompt = "Apply a grammatical rule")
        // The isolated article does disclose a single-letter key, unlike a letter inside "grammatical".
        assertTrue("A2_24_KEY_IN_PROMPT" in rules(pack(listOf(c))))
        val clean = c.copy(prompt = "Apply grammatical rules", promptLetter = "different")
        assertFalse(rules(pack(listOf(a, b, clean))).any { it in setOf("A2_24_ONLY_DISTRACTOR_IS_CUE", "A2_24_KEY_IN_PROMPT") })
    }

    @Test fun dictationRequirementWaitsForVerifiedClipsAndRequiresUnitMatchedBinding() {
        val dictation = typed("dictation").copy(kind = "TYPED_DICTATION", dictationAudioAssetId = "clip")
        val p = pack(listOf(dictation))
        assertFalse("A2_23_DICTATION_MINIMUM" in rules(p)) // Catalog records alone cannot claim verification.
        assertFalse(V2AssessmentDiagnostics.audit(p, mapOf(3 to setOf("clip"))).second.any { it.rule == "A2_23_DICTATION_MINIMUM" })
        assertTrue(V2AssessmentDiagnostics.audit(p, mapOf(3 to setOf("other-clip"))).second.any { it.rule == "A2_23_DICTATION_MINIMUM" })
        assertFalse(V2AssessmentDiagnostics.audit(p, mapOf(4 to setOf("other-clip"))).second.any { it.rule == "A2_23_DICTATION_MINIMUM" })
    }

    @Test fun validatorFailsWhenAnyOfUnitsThreeToFiveDropsBelowThirtyPercentTyped() {
        // T14-n2 verify: Units 3-5 sit exactly at 6 typed of 20 required checks.
        assertEquals(emptyList<String>(), V2Validator.typedFloorDefects(current))
        for (number in 3..5) {
            val unit = current.unit(number)!!
            val oneTyped = unit.objectives.flatMap { it.checkItemIds }.first { current.item(it)!!.isTypedResponse() }
            val dropped = current.copy(units = current.units.map { u -> if (u.number != number) u else
                u.copy(objectives = u.objectives.map { o -> o.copy(checkItemIds = o.checkItemIds - oneTyped) }) })
            assertEquals(1, V2Validator.typedFloorDefects(dropped).size)
            assertTrue(V2Validator.typedFloorDefects(dropped).single().startsWith("Unit $number has 5 of 19"))
            assertFalse(V2Validator.validate(dropped).ok)
        }
    }

    @Test fun fullReportHasEveryUnitAndDoesNotFailCompatibilityOrMutatePack() {
        val before = current.items.map { it.id to it.answer }
        val report = V2DesignDiagnostics.audit(current)
        assertEquals((1..30).toList(), report.unitAssessments.map { it.unit })
        assertTrue(report.signals.filter { it.rule.startsWith("A2_") }.all { it.unit != null })
        assertTrue(V2Validator.validate(current).ok)
        assertEquals(before, current.items.map { it.id to it.answer })
    }

    companion object {
        private val root = File(if (File("app").isDirectory) "." else "..")
        private val current by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()) }
    }
}
