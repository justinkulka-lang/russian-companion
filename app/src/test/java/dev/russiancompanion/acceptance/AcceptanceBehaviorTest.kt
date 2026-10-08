package dev.russiancompanion.acceptance

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.RussianCompanionApplication
import dev.russiancompanion.data.export.readAppRecording
import dev.russiancompanion.data.local.ActivityCategory
import dev.russiancompanion.data.local.CompletionState
import dev.russiancompanion.data.local.EligibilityStatus
import dev.russiancompanion.data.local.EvidenceType
import dev.russiancompanion.data.local.ExportStatus
import dev.russiancompanion.data.local.GateDecisionStatus
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.data.local.audio
import dev.russiancompanion.data.local.gate
import dev.russiancompanion.data.local.minimalDocument
import dev.russiancompanion.data.local.rights
import dev.russiancompanion.data.local.seed.PlanDefinitionSeed
import dev.russiancompanion.data.local.seed.SeedJson
import dev.russiancompanion.data.local.source
import dev.russiancompanion.data.local.text
import dev.russiancompanion.data.navigation.RoomNavigationGateway
import dev.russiancompanion.domain.gate.AnkiStatusValue
import dev.russiancompanion.domain.gate.GateEvidenceKind
import dev.russiancompanion.domain.gate.GateStatus
import dev.russiancompanion.domain.gate.PRESENCE_RECORDED
import dev.russiancompanion.domain.gate.RecordedEvidence
import dev.russiancompanion.domain.gate.defaultAdvancementRule
import dev.russiancompanion.domain.gate.evaluateGate
import dev.russiancompanion.domain.gate.remainingKinds
import dev.russiancompanion.domain.gate.tutorSignOffCondition
import dev.russiancompanion.domain.plan.ActivityLogView
import dev.russiancompanion.domain.plan.RecordedCategory
import dev.russiancompanion.domain.practice.PracticeAudio
import dev.russiancompanion.domain.progress.ExportUpdate
import dev.russiancompanion.domain.progress.reviewProgress
import dev.russiancompanion.presentation.navigation.ExportSelection
import dev.russiancompanion.presentation.navigation.LocalExportScreen
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.io.IOException
import java.io.OutputStream

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class AcceptanceBehaviorTest {
    @get:Rule
    val compose = createComposeRule()

    private val stores = mutableListOf<LocalStore>()

    @After
    fun closeStores() {
        stores.forEach { it.close() }
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.deleteDatabase(LocalStore.DATABASE_NAME)
        File(context.filesDir, "recordings").deleteRecursively()
    }

    @Test
    fun at009_shareAlikeFlagFollowsStoredConfirmationOnly() {
        val unconfirmed = apply(source().shareAlikeConfirmation)
        assertEquals(true, unconfirmed.seedOutcome()?.shareAlikeBlocksRelease)
        assertEquals(1, unconfirmed.seedOutcome()?.eligibleAudioCount)

        val confirmed = apply("CONFIRMED")
        assertEquals(false, confirmed.seedOutcome()?.shareAlikeBlocksRelease)
        assertEquals(1, confirmed.seedOutcome()?.eligibleAudioCount)

        val notApplicable = apply("NOT_APPLICABLE")
        assertEquals(false, notApplicable.seedOutcome()?.shareAlikeBlocksRelease)
    }

    @Test
    fun at015_at016_decisionsFollowTheDisplayedRuleAndTutorSignOffIsStricter() {
        val rule = defaultAdvancementRule()
        val low = listOf(
            RecordedEvidence("e-low", GateEvidenceKind.BOOK_REVISION_SCORE, "0", "2026-09-21T09:00"),
            RecordedEvidence("e-anki", GateEvidenceKind.ANKI_DROID_STATUS, AnkiStatusValue.MATURE.name, "2026-09-21T09:00"),
            RecordedEvidence("e-work", GateEvidenceKind.PRESCRIBED_WORK_COMPLETION, PRESENCE_RECORDED, "2026-09-21T09:00"),
        )
        val high = listOf(
            RecordedEvidence("e-high", GateEvidenceKind.BOOK_REVISION_SCORE, "1", "2026-09-21T09:01"),
            RecordedEvidence("e-anki", GateEvidenceKind.ANKI_DROID_STATUS, AnkiStatusValue.MATURE.name, "2026-09-21T09:00"),
            RecordedEvidence("e-work", GateEvidenceKind.PRESCRIBED_WORK_COMPLETION, PRESENCE_RECORDED, "2026-09-21T09:00"),
        )
        val failing = evaluateGate(rule, low)
        val passing = evaluateGate(rule, high)
        assertEquals(GateStatus.NOT_YET_SATISFIED, failing.status)
        assertEquals(listOf(GateEvidenceKind.BOOK_REVISION_SCORE), remainingKinds(failing, rule))
        assertEquals(GateStatus.SATISFIED, passing.status)
        assertEquals(passing, evaluateGate(rule, high))

        val stricter = rule + tutorSignOffCondition()
        assertEquals(GateStatus.NOT_EVALUABLE, evaluateGate(stricter, high).status)
        assertEquals(listOf(GateEvidenceKind.TUTOR_SIGN_OFF), remainingKinds(evaluateGate(stricter, high), stricter))
        val signed = RecordedEvidence("e-tutor", GateEvidenceKind.TUTOR_SIGN_OFF, PRESENCE_RECORDED, "2026-09-21T09:02")
        assertEquals(GateStatus.SATISFIED, evaluateGate(stricter, high + signed).status)
    }

    @Test
    fun at018_everyGovernedCategoryIsRecordedActivity() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        val views = ActivityCategory.entries.mapIndexed { index, category ->
            val saved = store.recordActivity(
                category = category,
                durationMinutes = 10 + index,
                completed = true,
                recordedAtLocal = "2026-09-21T09:%02d".format(index),
                segmentId = null,
                selfAttested = category == ActivityCategory.GRAMMAR || category == ActivityCategory.VOCABULARY_AND_FORMS,
            )
            ActivityLogView(
                id = saved.id,
                category = RecordedCategory.valueOf(saved.category.name),
                minutes = saved.durationMinutes,
                completed = saved.completed,
                recordedAtLocal = saved.recordedAtLocal,
                selfAttested = saved.selfAttested,
            )
        }
        val review = reviewProgress(views)
        assertTrue(review.readable)
        assertEquals(RecordedCategory.entries, review.categories.map { it.category })
        assertEquals(views.sumOf { it.minutes }.toLong(), review.totalMinutes)
        assertEquals(views.size, review.totalCompletions)
        assertEquals(true, views.single { it.category == RecordedCategory.GRAMMAR }.selfAttested)
        assertEquals(false, views.single { it.category == RecordedCategory.READING }.selfAttested)
    }

    @Test
    fun at019_closedStoreReopensWithTheSameUserRecordsAndRecordingBytes() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        (context.applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
        context.deleteDatabase(LocalStore.DATABASE_NAME)
        val reference = "recordings/at019.m4a"
        val bytes = byteArrayOf(3, 1, 4, 1, 5)
        val created = LocalStore.open(context).use { store ->
            store.applySeed(SeedJson.encode(persistenceDocument()))
            store.confirmLesson("segment-1", "2026-09-21")
            assertTrue(store.completeExternalActivity("plan-book", "2026-09-21", "2026-09-21T09:00"))
            val decision = store.recordGateEvidenceAndDecide(
                gateDefinitionId = "gate-1",
                entries = listOf(EvidenceType.BOOK_REVISION_SCORE to "1"),
                recordedAtLocal = "2026-09-21T09:01",
            )
            val file = File(context.filesDir, reference)
            file.parentFile?.mkdirs()
            file.writeBytes(bytes)
            val recording = store.commitRecording(
                audioModelId = "audio-1",
                localFileReference = reference,
                createdAtLocal = "2026-09-21T09:02",
                markedForTutorReview = true,
                completionState = CompletionState.NOT_COMPLETED,
            )
            assertTrue(store.completeListenPractice("audio-1", "2026-09-21", "2026-09-21T09:02"))
            SavedState(
                logIds = store.activityLogs().map { it.id },
                evidence = store.allEvidence().map { it.evidenceType to it.enteredValue },
                decisionStatus = decision.status,
                recordingId = recording.id,
                onboarding = store.learner().onboardingCompleted,
                segmentId = store.learner().activeSegmentId,
            )
        }

        LocalStore.open(context).use { reopened ->
            assertEquals(created.logIds, reopened.activityLogs().map { it.id })
            assertEquals(created.evidence, reopened.allEvidence().map { it.evidenceType to it.enteredValue })
            assertEquals(created.decisionStatus, reopened.decisions().single().status)
            assertEquals(created.recordingId, reopened.recordings().single().id)
            assertTrue(reopened.recordings().single().markedForTutorReview)
            assertEquals(CompletionState.COMPLETED, reopened.recordings().single().completionState)
            assertEquals(created.onboarding, reopened.learner().onboardingCompleted)
            assertEquals(created.segmentId, reopened.learner().activeSegmentId)
            assertEquals(EligibilityStatus.EXCLUDED, reopened.audio("audio-1")?.eligibilityStatus)
            assertTrue(bytes.contentEquals(readAppRecording(context, reference)))
            reopened.applySeed(SeedJson.encode(persistenceDocument()))
            assertEquals(EligibilityStatus.ELIGIBLE, reopened.audio("audio-1")?.eligibilityStatus)
            assertEquals(created.recordingId, reopened.recordings().single().id)
            assertTrue(bytes.contentEquals(readAppRecording(context, reference)))
        }
    }

    @Test
    fun at022_incompleteExportIsReportedUnsuccessful() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(SeedJson.encode(persistenceDocument()))
        val recording = store.commitRecording(
            audioModelId = "audio-1",
            localFileReference = "recordings/missing.m4a",
            createdAtLocal = "2026-09-21T09:03",
            markedForTutorReview = false,
            completionState = CompletionState.NOT_COMPLETED,
        )
        var written = 0
        val unread = RoomNavigationGateway(store, { null }, UnusedAudio(), openRecording = { null }).exportLocal(
            object : OutputStream() {
                override fun write(b: Int) {
                    written += 1
                }
            },
            "2026-09-21T09:04",
            "missing.zip",
            null,
        )
        assertEquals(ExportUpdate.RecordingUnreadable(recording.id), unread)
        assertEquals(0, written)
        assertEquals(ExportStatus.FAILED, store.exports().last().status)
        assertEquals(0, store.exports().last().recordingCount)

        val interrupted = RoomNavigationGateway(
            store,
            { null },
            UnusedAudio(),
            openRecording = { byteArrayOf(1).inputStream() },
        ).exportLocal(
            object : OutputStream() {
                override fun write(b: Int) = throw IOException("no space")
                override fun write(b: ByteArray) = throw IOException("no space")
                override fun write(b: ByteArray, off: Int, len: Int) = throw IOException("no space")
            },
            "2026-09-21T09:05",
            "partial.zip",
            null,
        )
        assertEquals(ExportUpdate.NotConfirmed, interrupted)
        assertEquals(ExportStatus.NOT_CONFIRMED, store.exports().last().status)
        assertEquals("partial.zip", store.exports().last().destinationReference)
        assertEquals(0, store.exports().last().recordingCount)

        compose.setContent {
            MaterialTheme {
                LocalExportScreen(
                    onExport = { _, _, _, done -> done(ExportUpdate.DestinationUnavailable) },
                    chooseExportDestination = { null },
                    onProgress = {},
                    onHome = null,
                )
            }
        }
        compose.onNodeWithText("Choose a local destination").performScrollTo().performClick()
        compose.onNodeWithText("Export did not complete.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("The incomplete export is unusable.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("The destination is unavailable.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Export completed.").assertDoesNotExist()
    }

    private fun apply(confirmation: String): LocalStore {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(
            SeedJson.encode(
                minimalDocument().copy(
                    sourceRecords = listOf(source().copy(shareAlikeConfirmation = confirmation)),
                    sourceTexts = listOf(text(matchVerified = true)),
                    assetRightsRecords = listOf(rights()),
                    audioModels = listOf(audio()),
                ),
            ),
        )
        return store
    }

    private fun persistenceDocument() = minimalDocument().copy(
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
        gateDefinitions = listOf(gate()),
        sourceTexts = listOf(text(matchVerified = true)),
        assetRightsRecords = listOf(rights()),
        audioModels = listOf(audio()),
    )

    private data class SavedState(
        val logIds: List<String>,
        val evidence: List<Pair<EvidenceType, String>>,
        val decisionStatus: GateDecisionStatus,
        val recordingId: String,
        val onboarding: Boolean,
        val segmentId: String?,
    )

    private class UnusedAudio : PracticeAudio {
        override fun modelPlayable(localAudioReference: String) = false
        override fun recordingReadable(localFileReference: String) = false
        override fun playModel(localAudioReference: String, onFinished: (Boolean) -> Unit) = Unit
        override fun playRecording(localFileReference: String, onFinished: (Boolean) -> Unit) = Unit
        override fun stopPlayback() = Unit
        override fun startCapture() = ""
        override fun stopCapture(keep: Boolean): String? = null
    }
}
