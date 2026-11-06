package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/** Synthetic authoring fixtures are not admitted curriculum, source evidence or learner credit. */
class CurriculumDesignDiagnosticsTest {
    @Test fun currentCorpusHasLocatedSignalsWhileCompatibilityValidationStillPasses() {
        val report = V2DesignDiagnostics.audit(current)
        assertEquals(30, report.counts["units"])
        assertEquals(626, report.counts["items"])
        assertEquals(429, report.counts["requiredChecks"])
        assertEquals("NEEDS_CORRECTION_OR_REVIEW", report.verdict)
        assertTrue(report.signals.any { it.rule == "LEARNER_MARKUP" && "u14-st-teach-impersonal-2" in it.entityIds })
        assertTrue(report.signals.any { it.rule == "NO_CYRILLIC_IN_REQUIRED_DISPLAY" && "u13-i-tense-keep-2" in it.entityIds })
        // New authoring standards do not silently change app startup or claim the course is good.
        assertTrue(V2Validator.validate(current).ok)
    }

    @Test fun identicalRequiredDisplaysAreLocatedWithoutRewritingTheirKeys() {
        val a = question("a")
        val b = a.copy(id = "b")
        val report = audit(listOf(a, b))
        val signal = report.signals.single { it.rule == "IDENTICAL_REQUIRED_DISPLAY_TASK" }
        assertEquals(listOf("a", "b"), signal.entityIds)
        assertEquals(V2DesignSignalKind.REVIEW_SIGNAL, signal.kind)
        assertEquals(a.answer, b.answer)
    }

    @Test fun sameSurfaceFormsInDifferentGrammaticalQuestionsAreNotIdenticalTasks() {
        val a = current.item("u6-i-animacy-2")!!.copy(id = "a", objectiveId = objectiveId)
        val b = current.item("u10-i-preposition-2")!!.copy(id = "b", objectiveId = objectiveId)
        assertEquals(a.promptLetter, b.promptLetter)
        assertEquals(a.options, b.options)
        assertEquals(a.answer, b.answer)
        assertNotEquals(a.prompt, b.prompt)
        assertFalse(audit(listOf(a, b)).signals.any { it.rule == "IDENTICAL_REQUIRED_DISPLAY_TASK" })
    }

    @Test fun pluralAndGenitiveStressContrastsAreNotCollapsed() {
        val a = current.item("u8-i-plural-2")!!.copy(id = "a", objectiveId = objectiveId)
        val b = current.item("u10-i-preposition-1")!!.copy(id = "b", objectiveId = objectiveId, prompt = a.prompt)
        assertNotEquals(a.answer, b.answer) // о́кна versus окна́ is not optional decoration.
        assertFalse(audit(listOf(a, b)).signals.any { it.rule == "IDENTICAL_REQUIRED_DISPLAY_TASK" })
    }

    @Test fun caseSensitiveLetterCuesAreNotLowercasedIntoCollisions() {
        val a = question("a").copy(promptLetter = "А", options = listOf("а", "Б"), answer = "Б")
        assertFalse(audit(listOf(a)).signals.any { it.rule == "CUE_AS_DISTRACTOR" })
    }

    @Test fun anExactVisibleCueDistractorIsAReviewSignal() {
        val a = question("a")
        val signal = audit(listOf(a)).signals.single { it.rule == "CUE_AS_DISTRACTOR" }
        assertEquals(listOf("a"), signal.entityIds)
        assertEquals(V2DesignSignalKind.REVIEW_SIGNAL, signal.kind)
    }

    @Test fun hiddenProvenanceIsNeitherAVisibleCueNorRussianQuestionContent() {
        val a = question("a").copy(prompt = "Choose a label", promptLetter = "город",
            promptLetterRole = "ANSWER_PROVENANCE", options = listOf("place", "person"), answer = "place")
        val report = audit(listOf(a))
        assertFalse(report.signals.any { it.rule == "CUE_AS_DISTRACTOR" })
        assertTrue(report.signals.any { it.rule == "NO_CYRILLIC_IN_REQUIRED_DISPLAY" })
    }

    @Test fun nongatingOrientationIsNotTreatedAsRequiredRussianAssessment() {
        val a = question("a").copy(prompt = "Choose a study aid", promptLetter = null,
            options = listOf("binder", "cards"), answer = "binder")
        val report = audit(listOf(a), gating = false)
        assertEquals(0, report.counts["requiredChecks"])
        assertFalse(report.signals.any { it.rule in setOf("FEWER_THAN_THREE_OPTIONS", "NO_CYRILLIC_IN_REQUIRED_DISPLAY", "CONSTANT_REQUIRED_KEY") })
    }

    @Test fun twoOptionChecksAndConstantKeysNeedReviewRatherThanAutomaticCondemnation() {
        val a = question("a")
        val b = a.copy(id = "b", prompt = "Apply the form in another context")
        val report = audit(listOf(a, b))
        for (rule in listOf("FEWER_THAN_THREE_OPTIONS", "CONSTANT_REQUIRED_KEY")) {
            val matching = report.signals.filter { it.rule == rule }
            assertTrue(matching.isNotEmpty())
            assertTrue(matching.all { it.kind == V2DesignSignalKind.REVIEW_SIGNAL })
        }
    }

    @Test fun aWorkedExampleContainingTheAnswerIsNotAutomaticallyAnAnswerScript() {
        val a = question("a")
        val report = audit(listOf(a), teaching = "Plural forms follow the taught pattern. Worked example: города́.")
        assertFalse(report.signals.any { it.rule == "ANSWER_IN_INSTRUCTION_SIGNAL" })
    }

    @Test fun explicitAnswerSelectionScriptsHaveInstructionLocators() {
        val report = audit(listOf(question("a")), teaching = "When the check asks for a plural, choose города́.")
        val signal = report.signals.single { it.rule == "ANSWER_IN_INSTRUCTION_SIGNAL" }
        assertEquals(listOf("teach"), signal.relatedStepIds)
        assertEquals(V2DesignSignalKind.REVIEW_SIGNAL, signal.kind)
    }

    @Test fun lexicalAbsenceCanBeTransferAndIsNotProofOfUntaughtContent() {
        val report = audit(listOf(question("a")), teaching = "Apply the pattern to a new noun.", remediation = "Compare the endings using the rule.")
        for (rule in listOf("TEACHING_TRANSFER_OR_GAP", "REMEDIATION_TRANSFER_OR_GAP")) {
            val signal = report.signals.single { it.rule == rule }
            assertEquals(V2DesignSignalKind.REVIEW_SIGNAL, signal.kind)
        }
        assertEquals(0, report.structuralErrors)
    }

    @Test fun brokenDeclaredTeachingAndRemediationLinksAreStructuralErrors() {
        val p = scenario(listOf(question("a")))
        val u = p.units.single()
        val o = u.objectives.single().copy(instructionStepIds = listOf("missing-teach"), remediationStepIds = listOf("missing-repair"))
        val report = V2DesignDiagnostics.audit(p.copy(units = listOf(u.copy(objectives = listOf(o)))))
        for (rule in listOf("MISSING_INSTRUCTION_LINK", "MISSING_REMEDIATION_LINK")) {
            assertEquals(V2DesignSignalKind.STRUCTURAL_ERROR, report.signals.single { it.rule == rule }.kind)
        }
    }

    @Test fun requiredChecksBeforeTheirLinkedTeachingAreLocated() {
        val report = audit(listOf(question("a")), checkFirst = true)
        val signal = report.signals.single { it.rule == "CHECK_BEFORE_LINKED_TEACHING" }
        assertEquals(V2DesignSignalKind.STRUCTURAL_ERROR, signal.kind)
        assertEquals(listOf("check"), signal.relatedStepIds)
    }

    @Test fun sourceMarkupIsFlaggedOnlyWhenItReachesLearnerFields() {
        val marked = audit(listOf(question("a")), teaching = "Read '''this''' and [[case|the ending]].")
        assertTrue(marked.signals.any { it.rule == "LEARNER_MARKUP" && it.entityIds == listOf("teach") })
        // Inherited provenance and capability source spans still contain legitimate frozen markup.
        val ordinary = audit(listOf(question("a")), teaching = "A learner's example retains a single apostrophe.")
        assertFalse(ordinary.signals.any { it.rule == "LEARNER_MARKUP" })
    }

    @Test fun diagnosticsAreDeterministicAndDoNotCalculateCanonicalUnitDigests() {
        val p = scenario(listOf(question("a")))
        val before = V2Validator.unitDigestExecutionsForTests.get()
        val first = V2DesignDiagnostics.audit(p)
        assertEquals(first, V2DesignDiagnostics.audit(p))
        assertEquals(before, V2Validator.unitDigestExecutionsForTests.get())
        assertEquals(mapOf(0 to 1), first.authoredRequiredKeyPositions)
        assertTrue(first.limitations.any { it.contains("runtime permutation") })
    }

    private fun question(id: String): V2CheckItem = current.item("u8-i-plural-1")!!.copy(
        id = id, objectiveId = objectiveId, prompt = "Choose the plural", promptLetter = "го́род",
        options = listOf("города́", "го́род"), answer = "города́",
        feedback = "Apply the noun rule.", distractorRationales = emptyList(),
    )

    private fun audit(items: List<V2CheckItem>, gating: Boolean = true,
                      teaching: String = "Compare го́род and города́ using the noun rule.",
                      remediation: String = "Review го́род and города́.", checkFirst: Boolean = false): V2DesignReport =
        V2DesignDiagnostics.audit(scenario(items, gating, teaching, remediation, checkFirst))

    private fun scenario(items: List<V2CheckItem>, gating: Boolean = true,
                         teaching: String = "Compare го́род and города́ using the noun rule.",
                         remediation: String = "Review го́род and города́.", checkFirst: Boolean = false): V2CurriculumPack {
        val teach = V2Step("teach", "TEACH", "Rule", teaching)
        val check = V2Step("check", "CHECK", "Check", "Apply the rule.", itemIds = items.map { it.id })
        val repair = V2Step("repair", "REMEDIATE", "Explanation", remediation)
        val o = V2Objective(objectiveId, 3, "Synthetic diagnostic fixture", "RECOGNITION",
            instructionStepIds = listOf("teach"), checkItemIds = items.map { it.id }, remediationStepIds = listOf("repair"), gating = gating)
        val session = V2Session("session", "Diagnostic fixture", 1, objectiveIds = listOf(objectiveId),
            steps = if (checkFirst) listOf(check, teach, repair) else listOf(teach, check, repair))
        val unit = current.unit(3)!!.copy(objectives = listOf(o), sessions = listOf(session))
        return current.copy(units = listOf(unit), items = items, examples = emptyList(), references = emptyList())
    }

    companion object {
        private const val objectiveId = "fixture-objective"
        private val root = File(if (File("app").isDirectory) "." else "..")
        private val current by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()) }
    }
}
