package dev.russiancompanion.domain.plan

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DailyPlanRulesTest {
    @Test
    fun checklistShowsRequiredWorkAndHidesTutorUntilChosen() {
        val plan = assembleDailyPlan(
            definitions = listOf(
                definition("plan-listen", ChecklistActivity.LISTEN_RECORD_COMPARE, 3),
                definition("plan-book", ChecklistActivity.BOOK, 1),
                definition("plan-anki", ChecklistActivity.ANKI_DROID, 2),
                definition("plan-tutor", ChecklistActivity.TUTOR, 4, required = false),
                definition("plan-required-tutor", ChecklistActivity.TUTOR, 5, required = true),
            ),
            instances = emptyList(),
        )
        assertEquals(listOf("plan-book", "plan-anki", "plan-listen"), plan.items.map { it.planDefinitionId })
        assertEquals(listOf("plan-tutor"), plan.addableTutorTasks.map { it.planDefinitionId })
        assertEquals("plan-book", plan.nextIncompleteRequired?.planDefinitionId)
        assertEquals(0, plan.requiredCompletedCount)
        assertEquals(3, plan.requiredItems.size)
        assertFalse(plan.requiredWorkComplete)
        assertFalse(canMarkComplete(plan.items.single { it.activity == ChecklistActivity.LISTEN_RECORD_COMPARE }))
        assertTrue(canMarkComplete(plan.items.single { it.activity == ChecklistActivity.BOOK }))
    }

    @Test
    fun includedTutorIsVisibleAndCompletedWorkAdvancesTheNextItem() {
        val plan = assembleDailyPlan(
            definitions = listOf(
                definition("plan-book", ChecklistActivity.BOOK, 1),
                definition("plan-tutor", ChecklistActivity.TUTOR, 4, required = false),
            ),
            instances = listOf(
                PlanInstanceView("plan-book", completed = true),
                PlanInstanceView("plan-tutor", completed = false),
            ),
        )
        assertEquals(listOf("plan-book", "plan-tutor"), plan.items.map { it.planDefinitionId })
        assertTrue(plan.addableTutorTasks.isEmpty())
        assertTrue(plan.items.single { it.planDefinitionId == "plan-book" }.completed)
        assertNull(plan.nextIncompleteRequired)
        assertTrue(plan.requiredWorkComplete)
        assertFalse(canMarkComplete(plan.items.single { it.activity == ChecklistActivity.BOOK }))
        assertTrue(canMarkComplete(plan.items.single { it.activity == ChecklistActivity.TUTOR }))
    }

    @Test
    fun gateSummaryUsesTheLatestRecordedDecisionOnly() {
        val summaries = summarizeGates(
            gates = listOf(GateStatusInput("gate-1", "Displayed test rule")),
            decisions = listOf(
                DecisionView("d1", "gate-1", RecordedGateStatus.NOT_YET_SATISFIED, "2026-09-21T09:00"),
                DecisionView("d2", "gate-1", RecordedGateStatus.NOT_EVALUABLE, "2026-09-21T10:00"),
                DecisionView("other", "gate-2", RecordedGateStatus.SATISFIED, "2026-09-21T11:00"),
            ),
        )
        assertEquals(RecordedGateStatus.NOT_EVALUABLE, summaries.single().status)
        val undecided = summarizeGates(
            gates = listOf(GateStatusInput("gate-1", "Displayed test rule")),
            decisions = emptyList(),
        )
        assertNull(undecided.single().status)
    }

    @Test
    fun activitySummaryKeepsRecordedWorkAndDropsEmptyCategories() {
        val summary = summarizeActivity(
            listOf(
                ActivityLogView("a", RecordedCategory.GRAMMAR, minutes = 0, completed = true, recordedAtLocal = "2026-09-21T09:00"),
                ActivityLogView("b", RecordedCategory.READING, minutes = 5, completed = false, recordedAtLocal = "2026-09-21T08:00"),
            ),
        )
        assertEquals("a", summary.latest?.id)
        assertEquals(
            listOf(RecordedCategory.READING, RecordedCategory.GRAMMAR),
            summary.totals.map { it.category },
        )
        assertEquals(ActivitySummary.none(), summarizeActivity(emptyList()))
    }

    @Test
    fun checklistCarriesLessonNumbersAndPrescribedAudioIds() {
        val plan = assembleDailyPlan(
            definitions = listOf(
                PlanDefinitionView(
                    id = "plan-book",
                    activity = ChecklistActivity.BOOK,
                    instruction = "book",
                    sortOrder = 1,
                    required = true,
                    lessonNumber = 9,
                ),
                PlanDefinitionView(
                    id = "plan-listen",
                    activity = ChecklistActivity.LISTEN_RECORD_COMPARE,
                    instruction = "listen",
                    sortOrder = 2,
                    required = true,
                    audioModelIds = listOf("audio-a", "audio-b"),
                ),
            ),
            instances = emptyList(),
        )
        assertEquals(9, plan.items.single { it.planDefinitionId == "plan-book" }.lessonNumber)
        assertEquals(
            listOf("audio-a", "audio-b"),
            plan.items.single { it.planDefinitionId == "plan-listen" }.audioModelIds,
        )
    }

    private fun definition(
        id: String,
        activity: ChecklistActivity,
        sortOrder: Int,
        required: Boolean = true,
    ): PlanDefinitionView {
        return PlanDefinitionView(
            id = id,
            activity = activity,
            instruction = id,
            sortOrder = sortOrder,
            required = required,
        )
    }
}
