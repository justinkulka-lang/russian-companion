package dev.russiancompanion.domain.navigation

import dev.russiancompanion.domain.plan.ChecklistActivity
import dev.russiancompanion.domain.plan.ChecklistItem
import dev.russiancompanion.domain.plan.DailyPlan
import dev.russiancompanion.domain.plan.GateSummary
import dev.russiancompanion.domain.plan.RecordedGateStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ContinueDestinationTest {
    @Test
    fun resolveContinueHandlesLessonAbsenceCompletionAndUnavailableAudio() {
        assertEquals(
            ContinueDestination.SelectLesson,
            resolveContinueDestination(baseSnapshot()),
        )
        val book = item("book", ChecklistActivity.BOOK)
        assertEquals(
            ContinueDestination.BookPractice("book"),
            resolveContinueDestination(planned(book)),
        )
        val anki = item("anki", ChecklistActivity.ANKI_DROID)
        assertEquals(
            ContinueDestination.AnkiPractice("anki"),
            resolveContinueDestination(planned(anki)),
        )
        val listen = item("listen", ChecklistActivity.LISTEN_RECORD_COMPARE, audio = listOf("model-1", "other"))
        val listenSnap = planned(listen).copy(
            audio = listOf(
                AudioOption(id = "other", stressMarkedText = "x", playable = true),
                AudioOption(id = "model-1", stressMarkedText = "y", playable = true),
            ),
        )
        val listenDest = resolveContinueDestination(listenSnap)
        assertEquals(ContinueDestination.ListenRecordCompare("model-1", "listen"), listenDest)
        assertFalse(continueOpensUnrelatedAudio(listenDest, listOf("model-1", "other")))
        assertTrue(
            continueOpensUnrelatedAudio(
                ContinueDestination.ListenRecordCompare("unrelated", "listen"),
                listOf("model-1"),
            ),
        )
        assertEquals(
            ContinueDestination.Unavailable,
            resolveContinueDestination(
                planned(listen).copy(audio = listOf(AudioOption("other", "x", playable = false))),
            ),
        )
        val complete = planned(item("book", ChecklistActivity.BOOK, completed = true)).copy(
            gateSummaries = listOf(GateSummary("gate-1", "rule", RecordedGateStatus.NOT_YET_SATISFIED)),
        )
        assertEquals(ContinueDestination.GateEvidence("gate-1"), resolveContinueDestination(complete))
        assertEquals(
            ContinueDestination.Progress,
            resolveContinueDestination(
                planned(item("book", ChecklistActivity.BOOK, completed = true)).copy(
                    gateSummaries = listOf(GateSummary("gate-1", "rule", RecordedGateStatus.SATISFIED)),
                ),
            ),
        )
        assertEquals(
            ContinueDestination.Unavailable,
            resolveContinueDestination(baseSnapshot().copy(localDataOpen = false)),
        )
        assertEquals(
            ContinueNavigation.PlanItem("book", ChecklistActivity.BOOK),
            continueNavigation(ContinueDestination.BookPractice("book")),
        )
        assertEquals(
            ContinueNavigation.PlanItem("anki", ChecklistActivity.ANKI_DROID),
            continueNavigation(ContinueDestination.AnkiPractice("anki")),
        )
    }

    private fun planned(item: ChecklistItem): NavigationSnapshot {
        return baseSnapshot().copy(
            activeSegmentId = "seg-1",
            activeLessonReference = "Lesson 1",
            lessons = listOf(LessonOption("seg-1", "Lesson 1", planCount = 1)),
            dailyPlan = DailyPlan(items = listOf(item), addableTutorTasks = emptyList()),
        )
    }

    private fun item(
        id: String,
        activity: ChecklistActivity,
        completed: Boolean = false,
        audio: List<String> = emptyList(),
    ): ChecklistItem {
        return ChecklistItem(
            planDefinitionId = id,
            activity = activity,
            instruction = id,
            sortOrder = 1,
            required = true,
            completed = completed,
            lessonNumber = 1,
            audioModelIds = audio,
        )
    }

    private fun baseSnapshot(): NavigationSnapshot {
        return NavigationSnapshot.closed().copy(
            localDataOpen = true,
            onboardingCompleted = true,
        )
    }
}
