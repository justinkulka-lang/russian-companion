package dev.russiancompanion.presentation.navigation

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.ActivityCategory
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.data.local.audio
import dev.russiancompanion.data.local.gate
import dev.russiancompanion.data.local.minimalDocument
import dev.russiancompanion.data.local.rights
import dev.russiancompanion.data.local.seed.PlanDefinitionSeed
import dev.russiancompanion.data.local.seed.SeedJson
import dev.russiancompanion.data.local.text
import dev.russiancompanion.data.navigation.RoomNavigationGateway
import dev.russiancompanion.domain.practice.PracticeAudio
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
        show(RoomNavigationGateway(store, { null }, PermissivePracticeAudio()))

        repeat(3) { compose.onNodeWithText("Next").performClick() }
        compose.onNodeWithText("Lesson 1", substring = false).performClick()
        compose.onNodeWithText("Confirm lesson").performClick()

        showText("Required activities completed: 0 of 3")
        showText("Next required activity")
        showText("Use your copy of the book for this lesson")
        showText("Activity gate, not proficiency certification.")
        showText("No advancement decision is recorded.")
        showText("Locked — remaining: Book revision score, AnkiDroid completion or maturity, Prescribed work completion")
        showText("No activity is recorded.")
        compose.onNodeWithText("Optional drills").assertDoesNotExist()
        clickText("Today's Plan", substring = false)

        showText("Grammar practice stays in your copy of the book and its answer key.")
        compose.onAllNodesWithText("Lesson number: 1").assertCountEquals(2)
        showText("Vocabulary and forms practice stays in AnkiDroid. The app records only what you report completing.")
        showText("Listen-record-compare practice.")
        showText("Prescribed catalog words: 1")
        compose.onAllNodesWithText("Self-attested").assertCountEquals(2)
        compose.onNodeWithText("Remove tutor task").assertDoesNotExist()
        clickText("Add tutor task")
        showText("Remove tutor task")
        clickText("Remove tutor task")
        showText("Add tutor task")
        compose.onNodeWithText("Remove tutor task").assertDoesNotExist()

        compose.onAllNodesWithText("Mark complete")[0].performScrollTo().performClick()
        compose.onAllNodesWithText("Mark complete")[0].performScrollTo().performClick()
        compose.onAllNodesWithText("Completed").assertCountEquals(2)
        clickText("Start listen-record-compare")
        compose.onNodeWithText("Practice session").assertIsDisplayed()
        showText("SOURCE_TEXT_TOKEN")
        compose.onNodeWithText("Back").performClick()
        showText("Not completed")

        val logs = store.activityLogs()
        assertEquals(listOf(ActivityCategory.GRAMMAR, ActivityCategory.VOCABULARY_AND_FORMS), logs.map { it.category })
        assertTrue(logs.all { it.selfAttested && it.completed && it.durationMinutes == 0 })

        clickText("Home", substring = false)
        showText("Required activities completed: 2 of 3")
        compose.onNodeWithText("Do the assigned AnkiDroid work").assertDoesNotExist()
        showText("Listen to the licensed model")
        clickContinue()
        compose.onNodeWithText("Practice session").assertIsDisplayed()
    }

    @Test
    fun missingPracticeAudioStaysIncompleteWhileOtherWorkRemainsAvailable() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(SeedJson.encode(checklistDocument(includeAudio = false)))
        show(RoomNavigationGateway(store, { null }, PermissivePracticeAudio()))
        repeat(3) { compose.onNodeWithText("Next").performClick() }
        compose.onNodeWithText("Lesson 1", substring = false).performClick()
        compose.onNodeWithText("Confirm lesson").performClick()

        compose.onNodeWithText("Practice").performScrollTo().assertIsNotEnabled()
        showText("No licensed practice audio is available.")
        clickText("Today's Plan", substring = false)
        compose.onNodeWithText("Start listen-record-compare").assertDoesNotExist()
        compose.onAllNodesWithText("Mark complete")[0].performScrollTo().performClick()
        showText("Completed")
        clickText("Progress", substring = false)
        showText("Recorded activity is work completed, not certified ability.")
    }

    @Test
    fun failedChecklistSaveKeepsThePriorStateAndRetries() {
        val gateway = FakePlanGateway()
        show(gateway)
        clickText("Today's Plan", substring = false)
        clickText("Mark complete")
        showText("The change was not saved.")
        showText("Not completed")
        gateway.failNext = false
        clickText("Retry")
        showText("Completed")
        compose.onNodeWithText("The change was not saved.").assertDoesNotExist()
    }

    @Test
    fun beginAndStartOpenTheFirstPlayablePrescribedWord() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(SeedJson.encode(prescribedCatalogDocument()))
        val audio = SelectivePracticeAudio(setOf("audio/audio-b", "audio/audio-c", "audio/audio-unrelated"))
        show(RoomNavigationGateway(store, { null }, audio), audio)
        repeat(3) { compose.onNodeWithText("Next").performClick() }
        compose.onNodeWithText("Lesson 1", substring = false).performClick()
        compose.onNodeWithText("Confirm lesson").performClick()

        showText("Listen to the licensed model")
        clickContinue()
        compose.onNodeWithText("Practice session").assertIsDisplayed()
        showText("WORD_B_TOKEN")
        compose.onNodeWithText("WORD_A_TOKEN").assertDoesNotExist()
        compose.onNodeWithText("WORD_UNRELATED_TOKEN").assertDoesNotExist()
        compose.onNodeWithText("Back").performClick()

        clickText("Today's Plan", substring = false)
        clickText("Start listen-record-compare")
        compose.onNodeWithText("Practice session").assertIsDisplayed()
        showText("WORD_B_TOKEN")
        compose.onNodeWithText("WORD_A_TOKEN").assertDoesNotExist()
        compose.onNodeWithText("WORD_UNRELATED_TOKEN").assertDoesNotExist()
    }

    @Test
    fun prescribedPracticeStaysDisabledWhenNoPrescribedWordIsPlayable() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(SeedJson.encode(prescribedCatalogDocument()))
        val audio = SelectivePracticeAudio(setOf("audio/audio-unrelated"))
        show(RoomNavigationGateway(store, { null }, audio), audio)
        repeat(3) { compose.onNodeWithText("Next").performClick() }
        compose.onNodeWithText("Lesson 1", substring = false).performClick()
        compose.onNodeWithText("Confirm lesson").performClick()

        compose.onAllNodesWithText("Continue").onLast().assertIsNotEnabled()
        showText("The prescribed listen-record-compare words are unavailable.")
        clickText("Today's Plan", substring = false)
        compose.onNodeWithText("Start listen-record-compare").performScrollTo().assertIsNotEnabled()
        showText("The prescribed listen-record-compare words are unavailable.")
        compose.onNodeWithText("Practice session").assertDoesNotExist()
        compose.onNodeWithText("WORD_UNRELATED_TOKEN").assertDoesNotExist()
    }

    @Test
    fun continueOpensTheNamedBookPlanNotTheGenericChecklist() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(SeedJson.encode(checklistDocument(includeAudio = true)))
        show(RoomNavigationGateway(store, { null }, PermissivePracticeAudio()))
        repeat(3) { compose.onNodeWithText("Next").performClick() }
        compose.onNodeWithText("Lesson 1", substring = false).performClick()
        compose.onNodeWithText("Confirm lesson").performClick()

        clickContinue()
        showText("Continue destination: Book practice")
        showText("Use your copy of the book for this lesson")
        compose.onAllNodesWithText("Mark complete")[0].performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Practice session").assertDoesNotExist()
    }

    @Test
    fun continueOpensTheNamedAnkiPlanAfterBookCompletion() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(SeedJson.encode(checklistDocument(includeAudio = true)))
        show(RoomNavigationGateway(store, { null }, PermissivePracticeAudio()))
        repeat(3) { compose.onNodeWithText("Next").performClick() }
        compose.onNodeWithText("Lesson 1", substring = false).performClick()
        compose.onNodeWithText("Confirm lesson").performClick()

        clickText("Today's Plan", substring = false)
        compose.onAllNodesWithText("Mark complete")[0].performScrollTo().performClick()
        clickText("Home", substring = false)
        clickContinue()
        showText("Continue destination: AnkiDroid practice")
        showText("Do the assigned AnkiDroid work")
        showText("Mark complete")
    }

    private fun clickText(text: String, substring: Boolean = true) {
        compose.onNodeWithText(text, substring = substring).performScrollTo().performClick()
    }

    private fun clickContinue() {
        compose.onAllNodesWithText("Continue").onLast().performScrollTo().performClick()
    }

    private fun showText(text: String) {
        compose.onNodeWithText(text).performScrollTo().assertIsDisplayed()
    }

    private fun show(gateway: NavigationGateway, practiceAudio: PracticeAudio = PermissivePracticeAudio()) {
        compose.setContent {
            CompanionApp(
                gateway = gateway,
                onRetryOpen = {},
                today = { "2026-09-21" },
                recordedAt = { "2026-09-21T09:0${stamp++}" },
                practiceAudio = practiceAudio,
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
                lessonNumber = 1,
            ),
            PlanDefinitionSeed(
                id = "plan-anki",
                segmentId = "segment-1",
                activityType = "ANKI_DROID",
                instruction = "Do the assigned AnkiDroid work",
                sortOrder = 2,
                required = true,
                lessonNumber = 1,
            ),
            PlanDefinitionSeed(
                id = "plan-listen",
                segmentId = "segment-1",
                activityType = "LISTEN_RECORD_COMPARE",
                instruction = "Listen to the licensed model",
                sortOrder = 3,
                required = true,
                audioModelIds = listOf("audio-1"),
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
        gateDefinitions = listOf(gate()),
        sourceTexts = listOf(text(matchVerified = includeAudio)),
        assetRightsRecords = listOf(rights()),
        audioModels = listOf(audio()),
    )

    private fun prescribedCatalogDocument() = minimalDocument().copy(
        planDefinitions = listOf(
            PlanDefinitionSeed(
                id = "plan-listen",
                segmentId = "segment-1",
                activityType = "LISTEN_RECORD_COMPARE",
                instruction = "Listen to the licensed model",
                sortOrder = 1,
                required = true,
                audioModelIds = listOf("audio-a", "audio-b", "audio-c"),
            ),
        ),
        sourceTexts = listOf(
            text(matchVerified = true, id = "text-a", stressMarkedText = "WORD_A_TOKEN"),
            text(matchVerified = true, id = "text-b", stressMarkedText = "WORD_B_TOKEN"),
            text(matchVerified = true, id = "text-c", stressMarkedText = "WORD_C_TOKEN"),
            text(matchVerified = true, id = "text-unrelated", stressMarkedText = "WORD_UNRELATED_TOKEN"),
        ),
        assetRightsRecords = listOf(
            rights(id = "rights-a", assetIdentifier = "audio-a"),
            rights(id = "rights-b", assetIdentifier = "audio-b"),
            rights(id = "rights-c", assetIdentifier = "audio-c"),
            rights(id = "rights-unrelated", assetIdentifier = "audio-unrelated"),
        ),
        audioModels = listOf(
            audio(id = "audio-a", rightsRecordId = "rights-a").copy(sourceTextId = "text-a"),
            audio(id = "audio-b", rightsRecordId = "rights-b").copy(sourceTextId = "text-b"),
            audio(id = "audio-c", rightsRecordId = "rights-c").copy(sourceTextId = "text-c"),
            audio(id = "audio-unrelated", rightsRecordId = "rights-unrelated").copy(sourceTextId = "text-unrelated"),
        ),
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

        override fun saveRecording(
            audioModelId: String,
            localFileReference: String,
            recordedAtLocal: String,
        ): PlanUpdate = PlanUpdate.NotSaved(audioModelId)

        override fun setRecordingTutorReview(recordingId: String, marked: Boolean): PlanUpdate {
            return PlanUpdate.NotSaved(recordingId)
        }

        override fun completeListenPractice(
            audioModelId: String,
            localDate: String,
            recordedAtLocal: String,
        ): PlanUpdate = PlanUpdate.NotSaved(audioModelId)

        override fun saveGateEvidence(
            gateId: String,
            entries: List<dev.russiancompanion.domain.gate.GateEvidenceEntry>,
            recordedAtLocal: String,
        ): PlanUpdate = PlanUpdate.NotSaved(gateId)

        override fun addTutorSignOff(gateId: String, recordedAtLocal: String): PlanUpdate {
            return PlanUpdate.NotSaved(gateId)
        }

        override fun saveActivityLog(
            category: dev.russiancompanion.domain.plan.RecordedCategory,
            durationMinutes: Int,
            completed: Boolean,
            recordedAtLocal: String,
            associateWithLesson: Boolean,
        ): PlanUpdate = PlanUpdate.NotSaved("activity")

        override fun exportLocal(
            destination: java.io.OutputStream,
            recordedAtLocal: String,
            destinationFileName: String?,
            documentReference: String?,
        ): dev.russiancompanion.domain.progress.ExportUpdate {
            return dev.russiancompanion.domain.progress.ExportUpdate.DestinationUnavailable
        }
    }
}

internal class PermissivePracticeAudio : PracticeAudio {
    override fun modelPlayable(localAudioReference: String): Boolean = true

    override fun recordingReadable(localFileReference: String): Boolean = localFileReference.isNotBlank()

    override fun playModel(localAudioReference: String, onFinished: (Boolean) -> Unit) {
        onFinished(true)
    }

    override fun playRecording(localFileReference: String, onFinished: (Boolean) -> Unit) {
        onFinished(true)
    }

    override fun stopPlayback() = Unit

    override fun startCapture(): String = "recordings/test-capture"

    override fun stopCapture(keep: Boolean): String? = if (keep) "recordings/test-capture" else null
}

internal class SelectivePracticeAudio(private val playableReferences: Set<String>) : PracticeAudio {
    override fun modelPlayable(localAudioReference: String): Boolean = localAudioReference in playableReferences

    override fun recordingReadable(localFileReference: String): Boolean = localFileReference.isNotBlank()

    override fun playModel(localAudioReference: String, onFinished: (Boolean) -> Unit) {
        onFinished(true)
    }

    override fun playRecording(localFileReference: String, onFinished: (Boolean) -> Unit) {
        onFinished(true)
    }

    override fun stopPlayback() = Unit

    override fun startCapture(): String = "recordings/test-capture"

    override fun stopCapture(keep: Boolean): String? = if (keep) "recordings/test-capture" else null
}
