package dev.russiancompanion.acceptance

import android.Manifest
import android.app.Application
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
import dev.russiancompanion.data.export.readAppRecording
import dev.russiancompanion.data.local.ActivityCategory
import dev.russiancompanion.data.local.CompletionState
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
import dev.russiancompanion.domain.practice.PracticeAudio
import dev.russiancompanion.domain.progress.ANKI_ATTESTATION
import dev.russiancompanion.domain.progress.ANKI_PRACTICE
import dev.russiancompanion.domain.progress.BOOK_ATTESTATION
import dev.russiancompanion.domain.progress.BOOK_PRACTICE
import dev.russiancompanion.presentation.navigation.CompanionApp
import dev.russiancompanion.presentation.navigation.ExportSelection
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import java.io.File
import java.util.zip.ZipFile

/**
 * Implementation evidence for the governed lesson cycle.
 * AT-004 and AT-030 stay outstanding: this run does not enable airplane mode,
 * and the packaged catalog still has no lesson or audio.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class AcceptanceCycleTest {
    @get:Rule
    val compose = createComposeRule()

    private val stores = mutableListOf<LocalStore>()
    private var stamp = 0

    @After
    fun closeStores() {
        stores.forEach { it.close() }
        val context = ApplicationProvider.getApplicationContext<Context>()
        File(context.filesDir, "recordings").deleteRecursively()
    }

    @Test
    fun at007_at012_at013_at014_at021_at030_lessonCycleStaysLocalAndUnscored() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        Shadows.shadowOf(context as Application).grantPermissions(Manifest.permission.RECORD_AUDIO)
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(SeedJson.encode(cycleDocument()))
        val capture = FileCapture(context)
        val exportFile = File(context.cacheDir, "acceptance-cycle.zip")
        compose.setContent {
            CompanionApp(
                gateway = RoomNavigationGateway(store, { null }, capture) { reference ->
                    readAppRecording(context, reference)
                },
                onRetryOpen = {},
                today = { "2026-09-21" },
                recordedAt = { "2026-09-21T09:%02d".format(stamp++) },
                practiceAudio = capture,
                chooseExportDestination = {
                    ExportSelection(
                        fileName = "acceptance-cycle.zip",
                        documentReference = "content://documents/acceptance-cycle.zip",
                    ) { exportFile.outputStream() }
                },
            )
        }

        show("This offline app is a study companion for an independent adult beginner working through The New Penguin Russian Course.")
        show("This app coordinates your book, AnkiDroid work, listening and recording, gates, and logs.")
        click("Next")
        show("This app does not teach from reproduced Penguin content, connect to AnkiDroid, judge Russian, or certify proficiency.")
        click("Next")
        show("Default gates do not require a tutor. Tutor sign-off can be added later only as stricter evidence you choose. All data stays on this device. Backup is a local export you start.")
        click("Next")
        click("Lesson 1")
        click("Confirm lesson")

        show("Required activities completed: 0 of 3")
        show("Next required activity")
        show("Use your copy of the book for this lesson")
        compose.onNodeWithText("Optional drills").assertDoesNotExist()
        compose.onNodeWithText("fluency").assertDoesNotExist()
        click("Today's Plan")
        show("Grammar practice stays in your copy of the book and its answer key.")
        show("Vocabulary and forms practice stays in AnkiDroid. The app records only what you report completing.")
        show("Listen-record-compare practice.")
        click("Add tutor task")
        show("Remove tutor task")
        click("Remove tutor task")
        compose.onNodeWithText("Remove tutor task").assertDoesNotExist()
        compose.onAllNodesWithText("Mark complete")[0].performScrollTo().performClick()
        compose.onAllNodesWithText("Mark complete")[0].performScrollTo().performClick()
        compose.onAllNodesWithText("Completed").assertCountEquals(2)

        click("Start listen-record-compare")
        show("Practice session")
        show("SOURCE_TEXT_TOKEN")
        show("No pronunciation score is provided.")
        click("Play model")
        click("Record")
        click("Stop")
        click("Replay model")
        click("Play your recording")
        click("Mark for tutor review")
        click("Complete practice")
        show("Completed")
        compose.onNodeWithText("fluency").assertDoesNotExist()

        click("Home")
        show("Required activities completed: 3 of 3")
        show("Today's required activities are complete.")
        show("Completion is recorded work, not proficiency certification.")
        click("Gates")
        click("Displayed test rule")
        show(BOOK_ATTESTATION)
        show(ANKI_ATTESTATION)
        replaceField(0, "1")
        replaceField(1, "mature")
        click("Save evidence")
        show("Recorded gate status: Satisfied")

        click("Progress")
        show("Recorded activity is work completed, not certified ability.")
        click("Local export")
        click("Choose a local destination")
        show("Export completed.")
        compose.onNodeWithText("This export contains no logs, gate evidence, or recordings.").assertDoesNotExist()

        val logs = store.activityLogs()
        assertEquals(
            setOf(
                ActivityCategory.GRAMMAR,
                ActivityCategory.VOCABULARY_AND_FORMS,
                ActivityCategory.SPEAKING_AND_PRONUNCIATION,
            ),
            logs.map { it.category }.toSet(),
        )
        assertTrue(logs.single { it.category == ActivityCategory.GRAMMAR }.selfAttested)
        assertTrue(logs.single { it.category == ActivityCategory.VOCABULARY_AND_FORMS }.selfAttested)
        assertFalse(logs.single { it.category == ActivityCategory.SPEAKING_AND_PRONUNCIATION }.selfAttested)
        val recording = store.recordings().single()
        assertTrue(recording.markedForTutorReview)
        assertEquals(CompletionState.COMPLETED, recording.completionState)
        assertEquals(logs.single { it.category == ActivityCategory.SPEAKING_AND_PRONUNCIATION }.id, recording.activityLogId)

        ZipFile(exportFile).use { zip ->
            val packaged = zip.entries().toList().map { it.name }
            assertTrue(packaged.contains("activity-logs.csv"))
            assertTrue(packaged.contains("gate-evidence.csv"))
            assertTrue(packaged.contains("gate-decisions.csv"))
            assertTrue(packaged.contains("progress.csv"))
            assertTrue(packaged.contains("recordings.csv"))
            val logText = zip.read("activity-logs.csv")
            assertTrue(logText.contains(BOOK_PRACTICE))
            assertTrue(logText.contains(ANKI_PRACTICE))
            val evidenceText = zip.read("gate-evidence.csv")
            assertTrue(evidenceText.contains(BOOK_ATTESTATION))
            assertTrue(evidenceText.contains(ANKI_ATTESTATION))
            assertTrue(evidenceText.contains(",1,"))
            assertTrue(evidenceText.contains("mature"))
            val decisionText = zip.read("gate-decisions.csv")
            assertTrue(decisionText.contains("SATISFIED"))
            val recordingText = zip.read("recordings.csv")
            val activityLogId = checkNotNull(recording.activityLogId)
            assertTrue(recordingText.contains(recording.id))
            assertTrue(recordingText.contains(activityLogId))
            val audioName = packaged.single { it.startsWith("recordings/") }
            val audioBytes = zip.getInputStream(zip.getEntry(audioName)).use { it.readBytes() }
            assertTrue(audioBytes.contentEquals(capture.bytes))
        }
    }

    private fun click(text: String) {
        compose.onNodeWithText(text, substring = false).performScrollTo().performClick()
    }

    private fun show(text: String) {
        compose.onNodeWithText(text, substring = false).performScrollTo().assertIsDisplayed()
    }

    private fun replaceField(index: Int, value: String) {
        compose.onAllNodes(hasSetTextAction())[index].performScrollTo().performTextReplacement(value)
    }

    private fun ZipFile.read(name: String): String {
        return getInputStream(getEntry(name)).bufferedReader().use { it.readText() }
    }

    private fun cycleDocument() = minimalDocument().copy(
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
                    GateConditionSeed("c-anki", "ANKI_DROID_STATUS", "EQUALS", "mature", 1),
                ),
            ),
        ),
        sourceTexts = listOf(text(matchVerified = true)),
        assetRightsRecords = listOf(rights()),
        audioModels = listOf(audio()),
    )

    private class FileCapture(private val context: Context) : PracticeAudio {
        val bytes: ByteArray = byteArrayOf(9, 8, 7, 6)
        private val reference = "recordings/cycle.m4a"

        override fun modelPlayable(localAudioReference: String) = true

        override fun recordingReadable(localFileReference: String): Boolean {
            return readAppRecording(context, localFileReference) != null
        }

        override fun playModel(localAudioReference: String, onFinished: (Boolean) -> Unit) {
            onFinished(true)
        }

        override fun playRecording(localFileReference: String, onFinished: (Boolean) -> Unit) {
            onFinished(true)
        }

        override fun stopPlayback() = Unit

        override fun startCapture(): String = reference

        override fun stopCapture(keep: Boolean): String? {
            if (!keep) return null
            val file = File(context.filesDir, reference)
            file.parentFile?.mkdirs()
            file.writeBytes(bytes)
            return reference
        }
    }
}
