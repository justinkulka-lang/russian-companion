package dev.russiancompanion.presentation.navigation

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.ActivityCategory
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.data.local.audio
import dev.russiancompanion.data.local.gate
import dev.russiancompanion.data.local.minimalDocument
import dev.russiancompanion.data.local.rights
import dev.russiancompanion.data.local.seed.GateConditionSeed
import dev.russiancompanion.data.local.seed.PlanDefinitionSeed
import dev.russiancompanion.data.local.seed.SeedJson
import dev.russiancompanion.data.local.text
import dev.russiancompanion.data.navigation.RoomNavigationGateway
import dev.russiancompanion.domain.navigation.LessonOption
import dev.russiancompanion.domain.navigation.NavigationGateway
import dev.russiancompanion.domain.navigation.NavigationSnapshot
import dev.russiancompanion.domain.plan.ChecklistActivity
import dev.russiancompanion.domain.plan.ChecklistItem
import dev.russiancompanion.domain.plan.DailyPlan
import dev.russiancompanion.domain.plan.PlanUpdate
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class DailyPlanUiTest {
    @get:Rule
    val compose = createComposeRule()

    private val stores = mutableListOf<LocalStore>()
    private var stamp = 0

    @After
    fun closeStores() {
        stores.forEach { it.close() }
    }

    @Test
    fun homeIdentifiesRequiredWorkAndTheChecklistRecordsExternalCompletion() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(SeedJson.encode(checklistDocument(includeAudio = true)))
        show(RoomNavigationGateway(store) { null })

        repeat(3) { compose.onNodeWithText("Next").performClick() }
        compose.onNodeWithText("Lesson 1", substring = false).performClick()
        compose.onNodeWithText("Confirm lesson").performClick()

        compose.onNodeWithText("Required activities completed: 0 of 3").assertIsDisplayed()
        compose.onNodeWithText("Next required activity").assertIsDisplayed()
        compose.onNodeWithText("Use your copy of the book for this lesson").assertIsDisplayed()
        compose.onNodeWithText("Activity gate, not proficiency certification.").assertIsDisplayed()
        compose.onNodeWithText("No advancement decision is recorded.").assertIsDisplayed()
        compose.onNodeWithText("No activity is recorded.").assertIsDisplayed()
        compose.onNodeWithText("Optional drills").assertDoesNotExist()
        compose.onNodeWithText("Today's Plan", substring = false).performClick()

        compose.onNodeWithText("Book work uses your copy.").assertIsDisplayed()
        compose.onNodeWithText("Assigned AnkiDroid work is performed separately.").assertIsDisplayed()
        compose.onNodeWithText("Listen-record-compare practice.").assertIsDisplayed()
        compose.onAllNodesWithText("Self-attested").assertCountEquals(2)
        compose.onNodeWithText("Remove tutor task").assertDoesNotExist()
        compose.onNodeWithText("Add tutor task").performClick()
        compose.onNodeWithText("Remove tutor task").assertIsDisplayed()
        compose.onNodeWithText("Remove tutor task").performClick()
        compose.onNodeWithText("Add tutor task").assertIsDisplayed()
        compose.onNodeWithText("Remove tutor task").assertDoesNotExist()

        compose.onAllNodesWithText("Mark complete")[0].performClick()
        compose.onAllNodesWithText("Mark complete")[0].performClick()
        compose.onAllNodesWithText("Completed").assertCountEquals(2)
        compose.onNodeWithText("Start listen-record-compare").performClick()
        compose.onNodeWithText("Practice session").assertIsDisplayed()
        compose.onNodeWithText("SOURCE_TEXT_TOKEN").assertIsDisplayed()
        compose.onNodeWithText("Back").performClick()
        compose.onNodeWithText("Not completed").assertIsDisplayed()

        val logs = store.activityLogs()
        assertEquals(listOf(ActivityCategory.GRAMMAR, ActivityCategory.VOCABULARY_AND_FORMS), logs.map { it.category })
        assertTrue(logs.all { it.selfAttested && it.completed && it.durationMinutes == 0 })

        compose.onNodeWithText("Home", substring = false).performClick()
        compose.onNodeWithText("Required activities completed: 2 of 3").assertIsDisplayed()
        compose.onNodeWithText("Do the assigned AnkiDroid work").assertDoesNotExist()
        compose.onNodeWithText("Listen to the licensed model").assertIsDisplayed()
        compose.onNodeWithText("Begin").performClick()
        compose.onNodeWithText("Practice session").assertIsDisplayed()
    }

    @Test
    fun missingPracticeAudioStaysIncompleteWhileOtherWorkRemainsAvailable() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(SeedJson.encode(checklistDocument(includeAudio = false)))
        show(RoomNavigationGateway(store) { null })
        repeat(3) { compose.onNodeWithText("Next").performClick() }
        compose.onNodeWithText("Lesson 1", substring = false).performClick()
        compose.onNodeWithText("Confirm lesson").performClick()

        compose.onNodeWithText("Practice").assertIsNotEnabled()
        compose.onNodeWithText("No licensed practice audio is available.").assertIsDisplayed()
        compose.onNodeWithText("Today's Plan", substring = false).performClick()
        compose.onNodeWithText("Start listen-record-compare").assertIsNotEnabled()
        compose.onAllNodesWithText("Mark complete")[0].performClick()
        compose.onNodeWithText("Completed").assertIsDisplayed()
        compose.onNodeWithText("Progress", substring = false).performClick()
        compose.onNodeWithText("Recorded activity is work completed, not certified ability.").assertIsDisplayed()
    }

    @Test
    fun failedChecklistSaveKeepsThePriorStateAndRetries() {
        val gateway = FakePlanGateway()
        show(gateway)
        compose.onNodeWithText("Today's Plan", substring = false).performClick()
        compose.onNodeWithText("Mark complete").performClick()
        compose.onNodeWithText("The change was not saved.").assertIsDisplayed()
        compose.onNodeWithText("Not completed").assertIsDisplayed()
        gateway.failNext = false
        compose.onNodeWithText("Retry").performClick()
        compose.onNodeWithText("Completed").assertIsDisplayed()
        compose.onNodeWithText("The change was not saved.").assertDoesNotExist()
    }

    private fun show(gateway: NavigationGateway) {
        compose.setContent {
            CompanionApp(
                gateway = gateway,
                onRetryOpen = {},
                today = { "2026-09-21" },
                recordedAt = { "2026-09-21T09:0${stamp++}" },
            )
        }
    }

    private fun checklistDocument(includeAudio: Boolean) = minimalDocument().copy(
        planDefinitions = listOf(
            PlanDefinitionSeed(
                id = "plan-book",
                segmentId = "segment-1",
                activityType = "BOOK",
                instruction = "Use your copy of the book for this lesson",
                sortOrder = 1,
                required = true,
            ),
            PlanDefinitionSeed(
                id = "plan-anki",
                segmentId = "segment-1",
                activityType = "ANKI_DROID",
                instruction = "Do the assigned AnkiDroid work",
                sortOrder = 2,
                required = true,
            ),
            PlanDefinitionSeed(
                id = "plan-listen",
                segmentId = "segment-1",
                activityType = "LISTEN_RECORD_COMPARE",
                instruction = "Listen to the licensed model",
                sortOrder = 3,
                required = true,
            ),
            PlanDefinitionSeed(
                id = "plan-tutor",
                segmentId = "segment-1",
                activityType = "TUTOR",
                instruction = "Optional tutor work, only when you choose it",
                sortOrder = 4,
                required = false,
            ),
        ),
        gateDefinitions = listOf(
            gate(
                conditions = listOf(
                    GateConditionSeed("c-book", "BOOK_REVISION_SCORE", "AT_LEAST", "1", 0),
                ),
            ),
        ),
        sourceTexts = if (includeAudio) listOf(text(matchVerified = true)) else emptyList(),
        assetRightsRecords = if (includeAudio) listOf(rights()) else emptyList(),
        audioModels = if (includeAudio) listOf(audio()) else emptyList(),
    )

    private class FakePlanGateway : NavigationGateway {
        var failNext: Boolean = true
        private var completed: Boolean = false

        override fun current(localDate: String): NavigationSnapshot {
            return NavigationSnapshot(
                localDataOpen = true,
                seedCorrupt = false,
                seedAbsent = false,
                onboardingCompleted = true,
                activeSegmentId = "segment-1",
                activeLessonReference = "Lesson 1",
                lessons = listOf(LessonOption("segment-1", "Lesson 1", planCount = 1)),
                audio = emptyList(),
                gates = emptyList(),
                eligibleDrillCount = 0,
                attributions = emptyList(),
                dailyPlan = DailyPlan(
                    items = listOf(
                        ChecklistItem(
                            planDefinitionId = "plan-book",
                            activity = ChecklistActivity.BOOK,
                            instruction = "Use your copy of the book for this lesson",
                            sortOrder = 1,
                            required = true,
                            completed = completed,
                        ),
                    ),
                    addableTutorTasks = emptyList(),
                ),
            )
        }

        override fun confirmLesson(segmentId: String, localDate: String) = Unit

        override fun reloadPackagedSeed() = Unit

        override fun completeExternalActivity(
            planDefinitionId: String,
            localDate: String,
            recordedAtLocal: String,
        ): PlanUpdate {
            if (failNext) return PlanUpdate.NotSaved(planDefinitionId)
            completed = true
            return PlanUpdate.Applied
        }

        override fun includeTutorTask(planDefinitionId: String, localDate: String): PlanUpdate {
            return PlanUpdate.NotSaved(planDefinitionId)
        }

        override fun removeTutorTask(planDefinitionId: String, localDate: String): PlanUpdate {
            return PlanUpdate.NotSaved(planDefinitionId)
        }
    }
}
