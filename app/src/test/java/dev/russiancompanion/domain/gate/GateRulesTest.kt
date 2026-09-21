package dev.russiancompanion.domain.gate

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GateRulesTest {
    @Test
    fun decisionFollowsTheStoredRuleAndCanBeRepeated() {
        val rule = listOf(
            GateRuleCondition("c-book", GateEvidenceKind.BOOK_REVISION_SCORE, GateComparison.AT_LEAST, "1"),
            GateRuleCondition("c-anki", GateEvidenceKind.ANKI_DROID_STATUS, GateComparison.EQUALS, "mature"),
        )
        val low = listOf(
            RecordedEvidence("e-book", GateEvidenceKind.BOOK_REVISION_SCORE, "0", "2026-09-21T09:00"),
            RecordedEvidence("e-anki", GateEvidenceKind.ANKI_DROID_STATUS, "mature", "2026-09-21T09:00"),
        )
        val failing = evaluateGate(rule, low)
        assertEquals(GateStatus.NOT_YET_SATISFIED, failing.status)
        assertEquals(ConditionResult.NOT_SATISFIED, failing.outcomes.single { it.conditionId == "c-book" }.result)
        assertEquals(ConditionResult.SATISFIED, failing.outcomes.single { it.conditionId == "c-anki" }.result)

        val raised = low + RecordedEvidence("e-book-2", GateEvidenceKind.BOOK_REVISION_SCORE, "1", "2026-09-21T10:00")
        val passing = evaluateGate(rule, raised)
        assertEquals(GateStatus.SATISFIED, passing.status)
        assertEquals(evaluateGate(rule, raised), passing)
        assertEquals(encodeConditionOutcomes(passing.outcomes), encodeConditionOutcomes(evaluateGate(rule, raised).outcomes))
        assertEquals("e-book-2", passing.outcomes.single { it.conditionId == "c-book" }.evidenceId)
    }

    @Test
    fun missingOrUnreadableEvidenceWithholdsASatisfiedResult() {
        val rule = listOf(
            GateRuleCondition("c-book", GateEvidenceKind.BOOK_REVISION_SCORE, GateComparison.AT_LEAST, "1"),
        )
        val awaiting = evaluateGate(rule, emptyList())
        assertEquals(GateStatus.NOT_EVALUABLE, awaiting.status)
        assertEquals(ConditionResult.AWAITING, awaiting.outcomes.single().result)

        val corrupt = evaluateGate(
            rule,
            listOf(RecordedEvidence("e-book", GateEvidenceKind.BOOK_REVISION_SCORE, "user-entered", "2026-09-21T09:00")),
        )
        assertEquals(GateStatus.NOT_EVALUABLE, corrupt.status)
        assertEquals(ConditionResult.UNREADABLE, corrupt.outcomes.single().result)
        assertFalse(evidenceEntryAccepted(GateComparison.AT_LEAST, "user-entered"))
        assertTrue(evidenceEntryAccepted(GateComparison.AT_LEAST, "1.5"))
    }

    @Test
    fun tutorSignOffWaitsUntilRecordedAndDoesNotSatisfyOtherConditions() {
        val rule = listOf(
            GateRuleCondition("c-book", GateEvidenceKind.BOOK_REVISION_SCORE, GateComparison.AT_LEAST, "1"),
            tutorSignOffCondition(),
        )
        val bookOnly = listOf(
            RecordedEvidence("e-book", GateEvidenceKind.BOOK_REVISION_SCORE, "2", "2026-09-21T09:00"),
        )
        val waiting = evaluateGate(rule, bookOnly)
        assertEquals(GateStatus.NOT_EVALUABLE, waiting.status)
        assertEquals(ConditionResult.AWAITING, waiting.outcomes.single { it.conditionId == USER_ADDED_TUTOR_CONDITION_ID }.result)
        assertEquals(ConditionResult.SATISFIED, waiting.outcomes.single { it.conditionId == "c-book" }.result)

        val signed = bookOnly + RecordedEvidence(
            "e-tutor",
            GateEvidenceKind.TUTOR_SIGN_OFF,
            "recorded",
            "2026-09-21T10:00",
        )
        val passed = evaluateGate(rule, signed)
        assertEquals(GateStatus.SATISFIED, passed.status)
        assertEquals(ConditionResult.SATISFIED, passed.outcomes.single { it.conditionId == USER_ADDED_TUTOR_CONDITION_ID }.result)

        val bookStillLow = listOf(
            RecordedEvidence("e-book", GateEvidenceKind.BOOK_REVISION_SCORE, "0", "2026-09-21T09:00"),
            RecordedEvidence("e-tutor", GateEvidenceKind.TUTOR_SIGN_OFF, "recorded", "2026-09-21T10:00"),
        )
        val blocked = evaluateGate(rule, bookStillLow)
        assertEquals(GateStatus.NOT_YET_SATISFIED, blocked.status)
        assertEquals(ConditionResult.SATISFIED, blocked.outcomes.single { it.conditionId == USER_ADDED_TUTOR_CONDITION_ID }.result)
        assertEquals(ConditionResult.NOT_SATISFIED, blocked.outcomes.single { it.conditionId == "c-book" }.result)
    }

    @Test
    fun prescribedWorkIsComparedAsEnteredText() {
        val rule = listOf(
            GateRuleCondition("c-work", GateEvidenceKind.PRESCRIBED_WORK_COMPLETION, GateComparison.EQUALS, "done"),
        )
        val open = evaluateGate(
            rule,
            listOf(RecordedEvidence("e-work", GateEvidenceKind.PRESCRIBED_WORK_COMPLETION, "not-done", "2026-09-21T09:00")),
        )
        assertEquals(GateStatus.NOT_YET_SATISFIED, open.status)
        val closed = evaluateGate(
            rule,
            listOf(RecordedEvidence("e-work", GateEvidenceKind.PRESCRIBED_WORK_COMPLETION, "done", "2026-09-21T09:00")),
        )
        assertEquals(GateStatus.SATISFIED, closed.status)
    }
}
