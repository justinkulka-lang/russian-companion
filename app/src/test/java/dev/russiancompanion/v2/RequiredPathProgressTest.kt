package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test

/** English-only fixtures for scheduling; they never enter the packaged curriculum. */
class RequiredPathProgressTest {
    @Test
    fun continueAndTodaySkipOptionalSessionsBeforeRequiredWork() {
        val pack = pack()
        val learner = learner(completed = setOf("orient", "required-teach"))
        assertEquals("required-check-session", continueTarget(pack, learner).sessionId)
        assertEquals("required-check-session", todaysSession(pack, pack.units.single(), learner)?.id)
        assertEquals("required", currentObjective(pack, pack.units.single(), learner)?.id)
    }

    @Test
    fun requiredCompletionReachesAdvancementWithOptionalWorkUntouched() {
        val pack = pack()
        val learner = learner(completed = setOf("orient", "required-teach", "required-check"), correct = setOf("required-item"))
        assertTrue(continueTarget(pack, learner).opensUnitReview)
        assertNull(todaysSession(pack, pack.units.single(), learner))
        val ordinary = ordinaryUnitRequirements(pack, pack.units.single(), learner)
        assertTrue(ordinary.remaining.joinToString("\n"), ordinary.allowed)
        val full = advancementDecision(pack, pack.units.single(), learner)
        assertFalse(full.allowed)
        assertEquals(listOf("The cumulative-review rule cannot be verified"), full.remaining)
        assertFalse("optional-teach" in learner.completedStepIds)
        assertFalse("optional-check" in learner.completedStepIds)
        assertFalse("optional-item" in learner.correctItemIds)
    }

    @Test
    fun optionalCorrectAnswersCannotSatisfyTheRequiredMinimum() {
        val pack = pack(recognition = false)
        val decision = advancementDecision(pack, pack.units.single(), learner(
            completed = setOf("orient", "required-teach", "required-check"), correct = setOf("optional-item"),
        ))
        assertFalse(decision.allowed)
        assertTrue(decision.remaining.any { it.contains("distinct in-app checks") })
    }

    @Test
    fun optionalSessionRemainsAvailableForExplicitVerifiedResume() {
        val pack = pack()
        val session = pack.session("optional-session")!!
        val learner = learner().copy(
            currentSessionId = session.id,
            resumeIdentitySchema = GOVERNED_IDENTITY_SCHEMA,
            resumeContentDigest = resumePositionDigest(pack, session.id, 0)!!,
        )
        assertEquals(session.id, continueTarget(pack, learner).sessionId)
    }

    @Test
    fun optionalResumeStillRejectsAnUnverifiedContentIdentity() {
        val learner = learner().copy(currentSessionId = "optional-session")
        assertTrue(continueTarget(pack(), learner).unavailable)
    }

    @Test
    fun sharedAndUnknownSessionOwnershipRemainRequired() {
        val pack = pack()
        val unit = pack.units.single()
        val mixed = unit.sessions[1].copy(id = "mixed", objectiveIds = listOf("required", "optional"))
        val unknown = mixed.copy(id = "unknown", objectiveIds = listOf("unresolved"))
        val unassigned = mixed.copy(id = "unassigned", objectiveIds = emptyList())
        assertEquals(listOf(mixed, unknown, unassigned), requiredPathSessions(unit.copy(sessions = listOf(mixed, unknown, unassigned))))
    }

    @Test
    fun allRequiredSessionsStillRunInOrder() {
        val pack = pack()
        assertEquals("orient-session", continueTarget(pack, learner()).sessionId)
        assertEquals("required-teach-session", continueTarget(pack, learner(completed = setOf("orient"))).sessionId)
    }

    @Test
    fun unitWithoutRequiredSessionsHasAnAdvancementTarget() {
        val pack = pack()
        val optional = pack.units.single().sessions.first { it.id == "optional-session" }
        val changed = pack.copy(units = listOf(pack.units.single().copy(sessions = listOf(optional))))
        assertTrue(continueTarget(changed, learner()).opensUnitReview)
        assertNull(todaysSession(changed, changed.units.single(), learner()))
    }

    @Test
    fun mixedStepRequiresOnlyRequiredAnswersAndKeepsTheirIdentityChecks() {
        val original = pack()
        val unit = original.units.single()
        val changed = original.copy(units = listOf(unit.copy(sessions = unit.sessions.map { session ->
            if (session.id != "required-check-session") session else session.copy(steps = session.steps.map {
                it.copy(itemIds = listOf("required-item", "optional-item"))
            })
        })))
        assertEquals(listOf("required-item"), stepCompletionItemIds(changed, "required-check"))
        assertFalse(stepReadyForGovernedCompletion(changed, learner(), "required-check"))
        val attempt = attempt(changed, "required-item", "required-check")
        assertTrue(stepReadyForGovernedCompletion(changed, learner().copy(governedAttempts = listOf(attempt)), "required-check"))
        assertFalse(stepReadyForGovernedCompletion(changed, learner().copy(governedAttempts = listOf(attempt.copy(itemDigest = "stale"))), "required-check"))
        assertFalse(stepReadyForGovernedCompletion(changed, learner().copy(governedAttempts = listOf(attempt.copy(attemptRole = OPTIONAL_PRACTICE))), "required-check"))
    }

    @Test
    fun explicitlyOpenedOptionalSessionStillRequiresItsOwnAnswers() {
        val pack = pack()
        assertEquals(listOf("optional-item"), stepCompletionItemIds(pack, "optional-check"))
        assertFalse(stepReadyForGovernedCompletion(pack, learner(), "optional-check"))
        assertTrue(stepReadyForGovernedCompletion(pack, learner().copy(governedAttempts = listOf(attempt(pack, "optional-item", "optional-check"))), "optional-check"))
    }

    private fun attempt(pack: V2CurriculumPack, itemId: String, stepId: String): GovernedAttemptView {
        val written = prepareGovernedAttempt(pack, itemId, pack.item(itemId)!!.answer, PRESCRIBED_CHECK, stepId, "2026-10-09T07:00")!!
        return GovernedAttemptView(written.id, written.itemId, written.selected, written.correct, written.recordedAtLocal,
            written.itemDigest, written.unitContentDigest, written.ruleDigest, written.identitySchema, written.attemptRole,
            written.objectiveId, written.unitNumber, written.stepId, acceptedOrder = 1)
    }

    private fun learner(completed: Set<String> = emptySet(), correct: Set<String> = emptySet()) = V2LearnerView(
        onboardingCompleted = true, currentUnitNumber = 3, startingPoint = V2StartingPoint.NEW_TO_CYRILLIC,
        hasPenguin = true, hasAnki = false, hasBinder = false, hasTutor = false,
        dailyMinutes = 25, transliterationEnabled = false, currentSessionId = null, currentStepIndex = 0,
        completedStepIds = completed, correctItemIds = correct, bookAttestedUnits = emptySet(), completedUnitNumbers = emptySet(),
    )

    private fun pack(recognition: Boolean = true): V2CurriculumPack {
        fun step(id: String, kind: String, items: List<String> = emptyList()) = V2Step(id, kind, id, "English test fixture", itemIds = items)
        fun session(id: String, objectives: List<String>, vararg steps: V2Step) = V2Session(id, id, 5, objectives, steps.toList())
        fun item(id: String, objective: String) = V2CheckItem(id, "FORM_SELECTION", "en", id, options = listOf("correct", "incorrect"),
            answer = "correct", feedback = "English test fixture", objectiveId = objective, sourceId = "fixture", reviewStatus = IMPLEMENTED_PENDING_FINAL_AUDIT)
        val unit = V2Unit(
            number = 3, id = "fixture-unit", title = "English fixture", topicSummary = "Scheduling only", installState = "DEVELOPMENT",
            readiness = "CONTENT_COMPLETE_REVIEW_PENDING", penguinLesson = 3, mappingStatus = "PENDING_CURRICULUM_REVIEWER", mappingNote = "Test fixture",
            objectives = listOf(
                V2Objective("required", 3, "Required", if (recognition) "RECOGNITION" else "APPLICATION",
                    instructionStepIds = listOf("orient", "required-teach"), checkItemIds = listOf("required-item")),
                V2Objective("optional", 3, "Optional", "RECOGNITION", instructionStepIds = listOf("orient", "optional-teach"),
                    checkItemIds = listOf("optional-item"), gating = false),
            ),
            sessions = listOf(
                session("orient-session", listOf("required", "optional"), step("orient", "ORIENT")),
                session("required-teach-session", listOf("required"), step("required-teach", "TEACH")),
                session("optional-session", listOf("optional"), step("optional-teach", "TEACH"), step("optional-check", "CHECK", listOf("optional-item"))),
                session("required-check-session", listOf("required"), step("required-check", "CHECK", listOf("required-item"))),
            ),
            advancement = V2AdvancementRule(true, false, true, 1, false, true),
        )
        return V2CurriculumPack(1, "english-test-fixture", 1, false, V2ReviewRecord("pending", IMPLEMENTED_PENDING_FINAL_AUDIT),
            sources = emptyList(), letters = emptyList(), items = listOf(item("required-item", "required"), item("optional-item", "optional")),
            references = emptyList(), units = listOf(unit), admissions = V2Admissions(0, 0, 0, 0))
    }
}
