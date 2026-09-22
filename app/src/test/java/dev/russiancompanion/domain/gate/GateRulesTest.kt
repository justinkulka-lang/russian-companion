package dev.russiancompanion.domain.gate

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GateRulesTest {
    @Test
    fun defaultCompoundRuleLocksUntilBookAnkiAndPrescribedWorkAreAllMet() {
        val rule = defaultAdvancementRule()
        val incomplete = listOf(
            RecordedEvidence("e-book", GateEvidenceKind.BOOK_REVISION_SCORE, "0", "2026-09-21T09:00"),
            RecordedEvidence("e-anki", GateEvidenceKind.ANKI_DROID_STATUS, AnkiStatusValue.COMPLETED.name, "2026-09-21T09:00"),
            RecordedEvidence("e-work", GateEvidenceKind.PRESCRIBED_WORK_COMPLETION, PRESENCE_RECORDED, "2026-09-21T09:00"),
        )
        val failing = evaluateGate(rule, incomplete)
        assertEquals(GateStatus.NOT_YET_SATISFIED, failing.status)
        assertTrue(gateIsLocked(failing))
        assertEquals(
            listOf(GateEvidenceKind.BOOK_REVISION_SCORE, GateEvidenceKind.ANKI_DROID_STATUS),
            remainingKinds(failing, rule),
        )
        assertEquals(ConditionResult.NOT_SATISFIED, failing.outcomes.single { it.conditionId == DEFAULT_BOOK_CONDITION_ID }.result)
        assertEquals(ConditionResult.NOT_SATISFIED, failing.outcomes.single { it.conditionId == DEFAULT_ANKI_CONDITION_ID }.result)
        assertEquals(ConditionResult.SATISFIED, failing.outcomes.single { it.conditionId == DEFAULT_WORK_CONDITION_ID }.result)

        val raised = listOf(
            RecordedEvidence("e-book-2", GateEvidenceKind.BOOK_REVISION_SCORE, "1", "2026-09-21T10:00"),
            RecordedEvidence("e-anki-2", GateEvidenceKind.ANKI_DROID_STATUS, AnkiStatusValue.MATURE.name, "2026-09-21T10:00"),
            RecordedEvidence("e-work", GateEvidenceKind.PRESCRIBED_WORK_COMPLETION, PRESENCE_RECORDED, "2026-09-21T09:00"),
        )
        val passing = reproduceDecision(rule, raised)
        assertEquals(GateStatus.SATISFIED, passing.status)
        assertFalse(gateIsLocked(passing))
        assertTrue(remainingKinds(passing, rule).isEmpty())
        assertEquals(evaluateGate(rule, raised), passing)
        assertEquals(passing.outcomes, decodeConditionOutcomes(encodeConditionOutcomes(passing.outcomes)))
        assertEquals("e-book-2", passing.outcomes.single { it.conditionId == DEFAULT_BOOK_CONDITION_ID }.evidenceId)
    }

    @Test
    fun missingOrUnreadableEvidenceWithholdsASatisfiedResult() {
        val rule = defaultAdvancementRule()
        val awaiting = evaluateGate(rule, emptyList())
        assertEquals(GateStatus.NOT_EVALUABLE, awaiting.status)
        assertTrue(gateIsLocked(awaiting))
        assertEquals(
            listOf(
                GateEvidenceKind.BOOK_REVISION_SCORE,
                GateEvidenceKind.ANKI_DROID_STATUS,
                GateEvidenceKind.PRESCRIBED_WORK_COMPLETION,
            ),
            remainingKinds(awaiting, rule),
        )
        assertTrue(awaiting.outcomes.all { it.result == ConditionResult.AWAITING })

        val corrupt = evaluateGate(
            rule,
            listOf(
                RecordedEvidence("e-book", GateEvidenceKind.BOOK_REVISION_SCORE, "user-entered", "2026-09-21T09:00"),
                RecordedEvidence("e-anki", GateEvidenceKind.ANKI_DROID_STATUS, AnkiStatusValue.MATURE.name, "2026-09-21T09:00"),
                RecordedEvidence("e-work", GateEvidenceKind.PRESCRIBED_WORK_COMPLETION, PRESENCE_RECORDED, "2026-09-21T09:00"),
            ),
        )
        assertEquals(GateStatus.NOT_EVALUABLE, corrupt.status)
        assertEquals(ConditionResult.UNREADABLE, corrupt.outcomes.single { it.conditionId == DEFAULT_BOOK_CONDITION_ID }.result)
        assertEquals(listOf(GateEvidenceKind.BOOK_REVISION_SCORE), remainingKinds(corrupt, rule))
        assertFalse(evidenceEntryAccepted(GateComparison.AT_LEAST, "user-entered", GateEvidenceKind.BOOK_REVISION_SCORE))
        assertTrue(evidenceEntryAccepted(GateComparison.AT_LEAST, "1.5", GateEvidenceKind.BOOK_REVISION_SCORE))
        assertFalse(evidenceEntryAccepted(GateComparison.EQUALS, "mature", GateEvidenceKind.ANKI_DROID_STATUS))
        assertTrue(evidenceEntryAccepted(GateComparison.EQUALS, AnkiStatusValue.MATURE.name, GateEvidenceKind.ANKI_DROID_STATUS))
        assertTrue(evidenceEntryAccepted(GateComparison.PRESENT, PRESENCE_RECORDED, GateEvidenceKind.PRESCRIBED_WORK_COMPLETION))
    }

    @Test
    fun tutorSignOffWaitsUntilRecordedAndDoesNotSatisfyOtherConditions() {
        val rule = defaultAdvancementRule() + tutorSignOffCondition()
        val defaultMet = listOf(
            RecordedEvidence("e-book", GateEvidenceKind.BOOK_REVISION_SCORE, "2", "2026-09-21T09:00"),
            RecordedEvidence("e-anki", GateEvidenceKind.ANKI_DROID_STATUS, AnkiStatusValue.MATURE.name, "2026-09-21T09:00"),
            RecordedEvidence("e-work", GateEvidenceKind.PRESCRIBED_WORK_COMPLETION, PRESENCE_RECORDED, "2026-09-21T09:00"),
        )
        val waiting = evaluateGate(rule, defaultMet)
        assertEquals(GateStatus.NOT_EVALUABLE, waiting.status)
        assertEquals(listOf(GateEvidenceKind.TUTOR_SIGN_OFF), remainingKinds(waiting, rule))
        assertEquals(ConditionResult.AWAITING, waiting.outcomes.single { it.conditionId == USER_ADDED_TUTOR_CONDITION_ID }.result)
        assertTrue(
            waiting.outcomes
                .filter { it.conditionId != USER_ADDED_TUTOR_CONDITION_ID }
                .all { it.result == ConditionResult.SATISFIED },
        )

        val signed = defaultMet + RecordedEvidence(
            "e-tutor",
            GateEvidenceKind.TUTOR_SIGN_OFF,
            PRESENCE_RECORDED,
            "2026-09-21T10:00",
        )
        val passed = reproduceDecision(rule, signed)
        assertEquals(GateStatus.SATISFIED, passed.status)
        assertFalse(gateIsLocked(passed))
        assertEquals(ConditionResult.SATISFIED, passed.outcomes.single { it.conditionId == USER_ADDED_TUTOR_CONDITION_ID }.result)

        val bookStillLow = listOf(
            RecordedEvidence("e-book", GateEvidenceKind.BOOK_REVISION_SCORE, "0", "2026-09-21T09:00"),
            RecordedEvidence("e-anki", GateEvidenceKind.ANKI_DROID_STATUS, AnkiStatusValue.MATURE.name, "2026-09-21T09:00"),
            RecordedEvidence("e-work", GateEvidenceKind.PRESCRIBED_WORK_COMPLETION, PRESENCE_RECORDED, "2026-09-21T09:00"),
            RecordedEvidence("e-tutor", GateEvidenceKind.TUTOR_SIGN_OFF, PRESENCE_RECORDED, "2026-09-21T10:00"),
        )
        val blocked = evaluateGate(rule, bookStillLow)
        assertEquals(GateStatus.NOT_YET_SATISFIED, blocked.status)
        assertEquals(listOf(GateEvidenceKind.BOOK_REVISION_SCORE), remainingKinds(blocked, rule))
        assertEquals(ConditionResult.SATISFIED, blocked.outcomes.single { it.conditionId == USER_ADDED_TUTOR_CONDITION_ID }.result)
        assertEquals(ConditionResult.NOT_SATISFIED, blocked.outcomes.single { it.conditionId == DEFAULT_BOOK_CONDITION_ID }.result)
    }

    @Test
    fun prescribedWorkIsAPresenceConfirmation() {
        val rule = listOf(
            GateRuleCondition("c-work", GateEvidenceKind.PRESCRIBED_WORK_COMPLETION, GateComparison.PRESENT, null),
        )
        val open = evaluateGate(rule, emptyList())
        assertEquals(GateStatus.NOT_EVALUABLE, open.status)
        val closed = evaluateGate(
            rule,
            listOf(RecordedEvidence("e-work", GateEvidenceKind.PRESCRIBED_WORK_COMPLETION, PRESENCE_RECORDED, "2026-09-21T09:00")),
        )
        assertEquals(GateStatus.SATISFIED, closed.status)
    }
}
