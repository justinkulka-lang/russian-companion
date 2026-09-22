package dev.russiancompanion.presentation.navigation

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.data.local.minimalDocument
import dev.russiancompanion.data.local.seed.PlanDefinitionSeed
import dev.russiancompanion.data.local.seed.SeedJson
import dev.russiancompanion.data.navigation.RoomNavigationGateway
import dev.russiancompanion.domain.navigation.LessonOption
import dev.russiancompanion.domain.navigation.NavigationGateway
import dev.russiancompanion.domain.navigation.NavigationSnapshot
import dev.russiancompanion.domain.progress.BOOK_PRACTICE
import dev.russiancompanion.domain.progress.ExportUpdate
import dev.russiancompanion.domain.progress.ProgressReview
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.util.zip.ZipFile

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ProgressUiTest {
    @get:Rule
    val compose = createComposeRule()

    private val stores = mutableListOf<LocalStore>()
    private var stamp = 0

    @After
    fun closeStores() {
        stores.forEach { it.close() }
    }

    @Test
    fun progressShowsAllCategoriesAndALocalExportOfTheLogs() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(SeedJson.encode(planDocument()))
        val exportFile = File(context.cacheDir, "export.zip")
        compose.setContent {
            CompanionApp(
                gateway = RoomNavigationGateway(store, { null }, PermissivePracticeAudio()),
                onRetryOpen = {},
                today = { "2026-09-21" },
                recordedAt = { "2026-09-21T09:%02d".format(stamp++) },
                practiceAudio = PermissivePracticeAudio(),
                chooseExportDestination = {
                    ExportSelection(
                        fileName = "export.zip",
                        documentReference = "content://documents/export.zip",
                    ) { exportFile.outputStream() }
                },
            )
        }

        repeat(3) { compose.onNodeWithText("Next").performClick() }
        compose.onNodeWithText("Lesson 1", substring = false).performClick()
        compose.onNodeWithText("Confirm lesson").performClick()
        click("Progress")
        show("Recorded activity is work completed, not certified ability.")
        compose.onAllNodesWithText("Activity imbalance").assertCountEquals(7)
        show("No activity is recorded.")
        compose.onNodeWithText("fluency").assertDoesNotExist()

        click("Log activity")
        click("Reading")
        compose.onNodeWithText("An activity category is required.").assertDoesNotExist()
        compose.onNode(hasSetTextAction()).performScrollTo().performTextReplacement("15")
        click("Mark completed")
        click("Save activity")

        show("Reading: 15 minutes recorded, 1 completed")
        compose.onAllNodesWithText("Activity imbalance").assertCountEquals(6)
        show("Recorded time: 15 minutes. Recorded completions: 1.")

        click("Log activity")
        click("Grammar")
        show(BOOK_PRACTICE)
        compose.onNodeWithText("integration", substring = true).assertDoesNotExist()
        compose.onNodeWithText("verified", substring = true).assertDoesNotExist()
        compose.onNode(hasSetTextAction()).performScrollTo().performTextReplacement("5")
        click("Save activity")
        click("Grammar,", substring = true)
        show(BOOK_PRACTICE)
        click("Close log")

        click("Local export")
        click("Choose a local destination")
        show("Export completed.")
        compose.onNodeWithText("This export contains no logs, gate evidence, or recordings.").assertDoesNotExist()
        ZipFile(exportFile).use { zip ->
            val logs = zip.getInputStream(zip.getEntry("activity-logs.csv")).bufferedReader().use { it.readText() }
            assertTrue(logs.contains("READING"))
            assertTrue(logs.contains(",15,"))
            assertTrue(logs.contains(BOOK_PRACTICE))
        }
        val saved = store.exports().single()
        assertEquals("export.zip", saved.destinationReference)
        assertEquals("content://documents/export.zip", saved.documentReference)
        compose.onNodeWithText("fluency").assertDoesNotExist()
    }

    @Test
    fun zeroMinuteCompletionIsShownAsCompletionRatherThanTimeSpent() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(SeedJson.encode(planDocument()))
        compose.setContent {
            CompanionApp(
                gateway = RoomNavigationGateway(store, { null }, PermissivePracticeAudio()),
                onRetryOpen = {},
                today = { "2026-09-21" },
                recordedAt = { "2026-09-21T09:00" },
                practiceAudio = PermissivePracticeAudio(),
            )
        }
        repeat(3) { compose.onNodeWithText("Next").performClick() }
        compose.onNodeWithText("Lesson 1", substring = false).performClick()
        compose.onNodeWithText("Confirm lesson").performClick()
        click("Progress")
        click("Log activity")
        click("Grammar")
        compose.onNode(hasSetTextAction()).performScrollTo().performTextReplacement("0")
        click("Mark completed")
        click("Save activity")
        show("Grammar: completed — 0 minutes")
        compose.onAllNodesWithText("Activity imbalance").assertCountEquals(6)
        compose.onNodeWithText("Grammar: 0 minutes recorded, 1 completed").assertDoesNotExist()
        click("Grammar,", substring = true)
        compose.onNodeWithText("completed — 0 minutes", substring = false).performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("0 minutes", substring = false).assertDoesNotExist()
    }

    @Test
    fun unreadableProgressHidesTotalsAndImbalance() {
        compose.setContent {
            CompanionApp(
                gateway = UnreadableProgressGateway(),
                onRetryOpen = {},
                today = { "2026-09-21" },
                practiceAudio = PermissivePracticeAudio(),
            )
        }
        show("Local data could not be opened. Bundled content and user data are affected.")
        compose.onNodeWithText("Activity imbalance").assertDoesNotExist()
        compose.onNodeWithText("Recorded time:", substring = true).assertDoesNotExist()
        compose.onNodeWithText("No activity is recorded.").assertDoesNotExist()
        click("Progress")
        show("Local data could not be opened. Bundled content and user data are affected.")
        compose.onNodeWithText("Activity imbalance").assertDoesNotExist()
        compose.onNodeWithText("Recorded time:", substring = true).assertDoesNotExist()
    }

    private fun click(text: String, substring: Boolean = false) {
        compose.onNodeWithText(text, substring = substring).performScrollTo().performClick()
    }

    private fun show(text: String) {
        compose.onNodeWithText(text, substring = true).performScrollTo().assertIsDisplayed()
    }

    private class UnreadableProgressGateway : NavigationGateway {
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
                progress = ProgressReview.unreadable(),
            )
        }

        override fun confirmLesson(segmentId: String, localDate: String) = Unit

        override fun reloadPackagedSeed() = Unit

        override fun completeExternalActivity(
            planDefinitionId: String,
            localDate: String,
            recordedAtLocal: String,
        ) = dev.russiancompanion.domain.plan.PlanUpdate.NotSaved(planDefinitionId)

        override fun includeTutorTask(planDefinitionId: String, localDate: String) =
            dev.russiancompanion.domain.plan.PlanUpdate.NotSaved(planDefinitionId)

        override fun removeTutorTask(planDefinitionId: String, localDate: String) =
            dev.russiancompanion.domain.plan.PlanUpdate.NotSaved(planDefinitionId)

        override fun saveRecording(audioModelId: String, localFileReference: String, recordedAtLocal: String) =
            dev.russiancompanion.domain.plan.PlanUpdate.NotSaved(audioModelId)

        override fun setRecordingTutorReview(recordingId: String, marked: Boolean) =
            dev.russiancompanion.domain.plan.PlanUpdate.NotSaved(recordingId)

        override fun completeListenPractice(audioModelId: String, localDate: String, recordedAtLocal: String) =
            dev.russiancompanion.domain.plan.PlanUpdate.NotSaved(audioModelId)

        override fun saveGateEvidence(
            gateId: String,
            entries: List<dev.russiancompanion.domain.gate.GateEvidenceEntry>,
            recordedAtLocal: String,
        ) = dev.russiancompanion.domain.plan.PlanUpdate.NotSaved(gateId)

        override fun addTutorSignOff(gateId: String, recordedAtLocal: String) =
            dev.russiancompanion.domain.plan.PlanUpdate.NotSaved(gateId)

        override fun saveActivityLog(
            category: dev.russiancompanion.domain.plan.RecordedCategory,
            durationMinutes: Int,
            completed: Boolean,
            recordedAtLocal: String,
            associateWithLesson: Boolean,
        ) = dev.russiancompanion.domain.plan.PlanUpdate.NotSaved("activity")

        override fun exportLocal(
            destination: java.io.OutputStream,
            recordedAtLocal: String,
            destinationFileName: String?,
            documentReference: String?,
        ) = ExportUpdate.DestinationUnavailable
    }

    private fun planDocument() = minimalDocument().copy(
        planDefinitions = listOf(
            PlanDefinitionSeed(
                id = "plan-book",
                segmentId = "segment-1",
                activityType = "BOOK",
                instruction = "Use your copy of the book for this lesson",
                sortOrder = 1,
                required = true,
                lessonNumber = 1,
            ),
        ),
    )
}
