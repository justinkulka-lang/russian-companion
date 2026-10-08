package dev.russiancompanion.v2

import android.content.Context
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.appearance.AppearanceStore
import dev.russiancompanion.data.local.CompletionState
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.data.local.audio
import dev.russiancompanion.data.local.minimalDocument
import dev.russiancompanion.data.local.rights
import dev.russiancompanion.data.local.seed.SeedJson
import dev.russiancompanion.data.local.text
import dev.russiancompanion.domain.appearance.AppearancePreference
import dev.russiancompanion.v2.backup.BackupReadResult
import dev.russiancompanion.v2.backup.BackupRejection
import dev.russiancompanion.v2.backup.BackupWorkRegistry
import dev.russiancompanion.v2.backup.GovernedReplaceOutcome
import dev.russiancompanion.v2.backup.RESTORE_CANCELLED
import dev.russiancompanion.v2.backup.RESTORE_FAILED
import dev.russiancompanion.v2.backup.RESTORE_CLEANUP_PENDING
import dev.russiancompanion.v2.backup.RESTORE_CLEANUP_PENDING_APPEARANCE_FAILED
import dev.russiancompanion.v2.backup.RESTORE_RECOVERY_REQUIRED
import dev.russiancompanion.v2.backup.ReplacementJournal
import dev.russiancompanion.v2.backup.V2BackupFaults
import dev.russiancompanion.v2.backup.backupJson
import dev.russiancompanion.v2.backup.readGovernedBackup
import dev.russiancompanion.v2.backup.restoreStoreBackup
import dev.russiancompanion.v2.backup.writeGovernedBackup
import dev.russiancompanion.v2.backup.writeStoreBackup
import dev.russiancompanion.v2.curriculum.IdentityWeakCache
import dev.russiancompanion.v2.curriculum.OPTIONAL_PRACTICE
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2PackIdentityIndex
import dev.russiancompanion.v2.curriculum.governedSelectionKey
import dev.russiancompanion.v2.curriculum.governedSessionSelections
import dev.russiancompanion.v2.curriculum.optionalPracticeSelection
import dev.russiancompanion.v2.curriculum.practiceOffers
import dev.russiancompanion.v2.curriculum.practiceQueue
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.item
import dev.russiancompanion.v2.curriculum.governedIdentityThreadsForTests
import dev.russiancompanion.v2.curriculum.lastGovernedIdentityThreadForTests
import dev.russiancompanion.v2.curriculum.lastGovernedSnapshotLabelForTests
import dev.russiancompanion.v2.curriculum.practiceStepId
import dev.russiancompanion.v2.curriculum.prepareGovernedAttempt
import dev.russiancompanion.v2.curriculum.GOVERNED_IDENTITY_SCHEMA
import dev.russiancompanion.v2.curriculum.resumePositionDigest
import dev.russiancompanion.v2.curriculum.savedWorkEntries
import dev.russiancompanion.v2.curriculum.step
import dev.russiancompanion.v2.curriculum.stepContentDigest
import dev.russiancompanion.v2.curriculum.unit
import dev.russiancompanion.v2.data.V2AttemptEntity
import dev.russiancompanion.v2.ui.V2App
import dev.russiancompanion.v2.ui.V2BackupCoordinator
import dev.russiancompanion.v2.ui.V2BackupPhase
import dev.russiancompanion.v2.ui.V2PracticeScreen
import dev.russiancompanion.v2.ui.V2UiWork
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.lang.ref.WeakReference
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.random.Random

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], qualifiers = "w411dp-h914dp-xxhdpi")
class RemainingReauditCorrectionTest {
    @get:Rule
    val compose = createAndroidComposeRule<ComponentActivity>()

    private val stores = mutableListOf<LocalStore>()
    private val pack: V2CurriculumPack by lazy {
        V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())
    }

    @After
    fun tearDown() {
        V2UiWork.blockForTests = null
        V2UiWork.governedComputeGateForTests = null
        V2UiWork.governedComputeEnteredForTests = null
        V2UiWork.governedSnapshotOutcomeForTests = null
        V2BackupFaults.reset()
        BackupWorkRegistry.clearForTests()
        lastGovernedIdentityThreadForTests = null
        lastGovernedSnapshotLabelForTests = null
        governedIdentityThreadsForTests.clear()
        stores.forEach { store ->
            store.acceptedOrderReadHoldForTests?.countDown()
            store.close()
        }
    }

    @Test
    fun importedAcceptedOrderRejectsMalformedHashesAndContinuesAfterReopen() {
        val located = pack.step("u1-st-vowels-check")!!
        val step = located.second
        val item = pack.item(step.itemIds.single())!!
        val first = item.options[0]
        val second = item.options[1]
        fun prescribed(selected: String, at: String) =
            prepareGovernedAttempt(pack, item.id, selected, dev.russiancompanion.v2.curriculum.PRESCRIBED_CHECK, step.id, at)!!

        val source = memory()
        source.v2RecordGovernedAttempt(prescribed(first, "2026-10-08T12:00:01"))
        source.v2RecordGovernedAttempt(prescribed(second, "2026-10-08T12:00:02"))
        val document = source.governedLearnerDocument()
        val negative = document.copy(
            attempts = listOf(
                document.attempts[0].copy(acceptedOrder = -1),
                document.attempts[1].copy(acceptedOrder = -4),
            ),
        )
        val duplicate = document.copy(
            attempts = document.attempts.map { it.copy(acceptedOrder = 7) },
        )
        val live = memory()
        live.v2AddNote("kept", "stay", "2026-10-08T12:00:00")
        val before = live.governedLearnerFingerprint()
        listOf(negative, duplicate).forEach { malformed ->
            val zip = hashedBackup(malformed)
            val read = readGovernedBackup(zip.inputStream(), APP, pack.packId, pack.packVersion, tempDir())
            assertTrue(read.toString(), read is BackupReadResult.Rejected)
            assertEquals(BackupRejection.MALFORMED, (read as BackupReadResult.Rejected).reason)
            assertEquals(GovernedReplaceOutcome.UNCHANGED, live.replaceGovernedLearner(malformed, emptyList()))
            assertEquals(before, live.governedLearnerFingerprint())
            assertEquals(listOf("stay"), live.v2Notes().map { it.text })
            assertTrue(live.v2Attempts().isEmpty())
        }

        val historical = document.copy(
            attempts = listOf(
                document.attempts[0].copy(acceptedOrder = 0),
                document.attempts[1].copy(id = document.attempts[1].id + ":historical", acceptedOrder = 0),
            ) + document.attempts[0].copy(id = "sequenced", acceptedOrder = 2, selected = second),
        )
        val historicalZip = hashedBackup(historical)
        val historicalRead = readGovernedBackup(historicalZip.inputStream(), APP, pack.packId, pack.packVersion, tempDir())
        assertTrue(historicalRead is BackupReadResult.Valid)
        val named = recordingStore("order-reopen")
        assertEquals(
            GovernedReplaceOutcome.REPLACED,
            restoreStoreBackup(named, (historicalRead as BackupReadResult.Valid).backup, tempDir()),
        )
        assertEquals(listOf(0L, 0L, 2L), named.v2Attempts().map { it.acceptedOrder }.sorted())
        named.close()
        val reopened = LocalStore.openNamed(context(), "order-reopen").also { stores += it }
        reopened.v2RecordGovernedAttempt(prescribed(first, "2026-10-08T13:00:00"))
        val orders = reopened.v2Attempts().map { it.acceptedOrder }.sorted()
        assertEquals(listOf(0L, 0L, 2L, 3L), orders)
        assertFalse(orders.any { it < 0L })

        val exhausted = memory()
        val seed = prescribed(first, "2026-10-08T14:00:00")
        exhausted.v2InsertSequencedAttemptForTests(attemptRow(seed, "maxed", Long.MAX_VALUE))
        try {
            exhausted.v2RecordGovernedAttempt(prescribed(second, "2026-10-08T14:00:01"))
            throw AssertionError("exhausted ordering stored or dropped an answer")
        } catch (error: IllegalStateException) {
            assertTrue(error.message.orEmpty(), error.message.orEmpty().contains("accepted-order-exhausted"))
        }
        assertEquals(listOf(Long.MAX_VALUE), exhausted.v2Attempts().map { it.acceptedOrder })

        val raced = memory()
        val entered = CountDownLatch(1)
        val hold = CountDownLatch(1)
        raced.acceptedOrderReadEnteredForTests = entered
        raced.acceptedOrderReadHoldForTests = hold
        val failures = java.util.Collections.synchronizedList(mutableListOf<Throwable>())
        val left = Thread {
            try {
                raced.v2AllocateGovernedAttemptOffExecutorForTests(prescribed(first, "2026-10-08T15:00:01"))
            } catch (error: Throwable) {
                failures += error
            }
        }
        val right = Thread {
            try {
                raced.v2AllocateGovernedAttemptOffExecutorForTests(prescribed(second, "2026-10-08T15:00:02"))
            } catch (error: Throwable) {
                failures += error
            }
        }
        left.start()
        awaitLatch(entered)
        right.start()
        Thread.sleep(150)
        hold.countDown()
        left.join(5_000)
        right.join(5_000)
        assertTrue(failures.toString(), failures.isEmpty())
        assertEquals(listOf(1L, 2L), raced.v2Attempts().map { it.acceptedOrder }.sorted())

        migrateFileBackedSchemaEight()
    }

    @Test
    fun stagingSurvivesAnotherCoordinatorAndViewModelTeardownDuringReplacement() {
        val store = recordingStore("staging-owner")
        val seeded = seedRecording(store, byteArrayOf(9, 8, 7))
        store.v2AddNote("kept", "backup note", "2026-10-08T12:00:00")
        val backup = tempZip()
        backup.outputStream().use { writeStoreBackup(store, it, APP, pack.packId, pack.packVersion, "DARK") }
        store.v2AddNote("kept", "live note", "2026-10-08T12:05:00")
        val activity = Robolectric.buildActivity(ComponentActivity::class.java).setup().get()
        val appearance = AppearanceStore.memory(AppearancePreference.DARK)
        val first = V2BackupCoordinator.get(activity, store, APP, pack.packId, pack.packVersion, appearance)
        val other = V2BackupCoordinator.get(activity, memory(), APP, pack.packId, pack.packVersion, appearance)
        assertTrue(first.begin(V2BackupPhase.PREPARING))
        first.openBackup { backup.inputStream() }
        awaitPhase(first, V2BackupPhase.AWAITING_CONFIRMATION)
        val staged = incoming(activity).single()
        val marker = File(staged, "marker.txt").apply { writeText("keep") }
        val recordingInput = staged.listFiles().orEmpty().first { it.isFile && it.name.endsWith(".bin") }
        val inputBytes = recordingInput.readBytes()
        V2BackupCoordinator.get(activity, memory(), APP, pack.packId, pack.packVersion, appearance)
        other.sweepOrphansForTests()
        assertEquals("keep", marker.readText())
        assertEquals(inputBytes.toList(), recordingInput.readBytes().toList())

        val root = checkNotNull(store.recordingDirectory())
        val orphan = File(root, "unreferenced-orphan.bin").apply { writeBytes(byteArrayOf(4)) }
        val heldInput = File(root, "held-recording-input.bin").apply { writeBytes(byteArrayOf(5)) }
        BackupWorkRegistry.holdRecordings(listOf(heldInput))
        store.sweepOrphanRecordingFiles()
        assertFalse(orphan.exists())
        assertTrue(heldInput.readBytes().contentEquals(byteArrayOf(5)))
        BackupWorkRegistry.releaseRecordings(listOf(heldInput))
        heldInput.delete()

        val entered = CountDownLatch(1)
        val during = CountDownLatch(1)
        V2BackupFaults.replacementEntered = entered
        V2BackupFaults.holdDuringReplacement = during
        first.confirmRestore { }
        awaitLatch(entered)
        val duringOrphan = File(root, "during-replacement-orphan.bin").apply { writeBytes(byteArrayOf(6)) }
        activity.viewModelStore.clear()
        other.sweepOrphansForTests()
        store.sweepOrphanRecordingFilesBypassingExecutorForTests()
        assertTrue(marker.exists())
        assertEquals(inputBytes.toList(), recordingInput.readBytes().toList())
        assertTrue(duringOrphan.exists())
        val liveMedia = File(root, File(seeded.localFileReference).name)
        assertTrue(liveMedia.readBytes().contentEquals(byteArrayOf(9, 8, 7)))
        during.countDown()
        idleUntil("replacement finished and staging released") {
            store.v2Notes().map { it.text } == listOf("backup note") && incoming(activity).isEmpty()
        }
        assertTrue(liveMedia.readBytes().contentEquals(byteArrayOf(9, 8, 7)))
        assertFalse(marker.exists())
        store.sweepOrphanRecordingFiles()
        assertFalse(duringOrphan.exists())
    }

    @Test
    fun recoveryMessagesMatchJournalPhaseAndReopenedRecords() {
        val name = "recovery-journal-${System.nanoTime()}"
        val store = recordingStore(name)
        seedRecording(store, byteArrayOf(1, 2, 3))
        store.v2AddNote("kept", "backup note", "2026-10-08T12:00:00")
        val backup = tempZip()
        backup.outputStream().use {
            writeStoreBackup(store, it, APP, pack.packId, pack.packVersion, AppearancePreference.FOLLOW_SYSTEM.name)
        }
        store.v2AddNote("kept", "live note", "2026-10-08T12:05:00")
        val activity = Robolectric.buildActivity(ComponentActivity::class.java).setup().get()
        val appearance = AppearanceStore.memory(AppearancePreference.DARK)
        V2BackupFaults.retainSwapArtifact = true
        val cleanup = V2BackupCoordinator.get(activity, store, APP, pack.packId, pack.packVersion, appearance)
        assertTrue(cleanup.begin(V2BackupPhase.PREPARING))
        cleanup.openBackup { backup.inputStream() }
        awaitPhase(cleanup, V2BackupPhase.AWAITING_CONFIRMATION)
        val afterCommit = kotlinx.coroutines.CompletableDeferred<Unit>()
        V2BackupFaults.holdAfterCommit = afterCommit
        cleanup.confirmRestore { }
        idleUntil("cleanup journal committed") {
            journalPhase(store) == "COMMITTED" && cleanup.phase == V2BackupPhase.RESTORING
        }
        cleanup.cancelWorkForTests()
        afterCommit.complete(Unit)
        awaitPhase(cleanup, V2BackupPhase.COMPLETED)
        assertEquals(RESTORE_CLEANUP_PENDING, cleanup.message)
        assertFalse(cleanup.message.contains("from before this attempt"))
        assertFalse(cleanup.message.contains("unchanged"))
        assertEquals("COMMITTED", journalPhase(store))
        assertEquals(listOf("backup note"), store.v2Notes().map { it.text })
        assertEquals(AppearancePreference.FOLLOW_SYSTEM, runBlocking { appearance.preference.first() })
        V2BackupFaults.reset()
        store.close()
        val cleaned = LocalStore.openNamed(context(), name).also { stores += it }
        assertEquals(listOf("backup note"), cleaned.v2Notes().map { it.text })
        assertFalse(journalFile(cleaned).exists())
        assertTrue(checkNotNull(cleaned.recordingDirectory()).listFiles().orEmpty().none { it.name.endsWith(".incoming") })

        val rollbackName = "recovery-rollback-${System.nanoTime()}"
        val rollbackStore = recordingStore(rollbackName)
        seedRecording(rollbackStore, byteArrayOf(4, 5, 6))
        rollbackStore.v2AddNote("kept", "backup note", "2026-10-08T12:00:00")
        val rollbackBackup = tempZip()
        rollbackBackup.outputStream().use { writeStoreBackup(rollbackStore, it, APP, pack.packId, pack.packVersion, "DARK") }
        rollbackStore.v2AddNote("kept", "live note", "2026-10-08T12:05:00")
        val beforeRollback = rollbackStore.governedLearnerFingerprint()
        V2BackupFaults.crashAfterFileSwapBeforeCommit = true
        val failedAppearance = AppearanceStore.memory(AppearancePreference.DARK)
        val rollback = V2BackupCoordinator.get(activity, rollbackStore, APP, pack.packId, pack.packVersion, failedAppearance)
        assertTrue(rollback.begin(V2BackupPhase.PREPARING))
        rollback.openBackup { rollbackBackup.inputStream() }
        awaitPhase(rollback, V2BackupPhase.AWAITING_CONFIRMATION)
        val rollbackHold = kotlinx.coroutines.CompletableDeferred<Unit>()
        V2BackupFaults.holdAfterCommit = rollbackHold
        rollback.confirmRestore { }
        idleUntil("rollback journal open") { journalPhase(rollbackStore) == "FILES_SWAPPED" }
        rollback.cancelWorkForTests()
        rollbackHold.complete(Unit)
        awaitPhase(rollback, V2BackupPhase.FAILED)
        assertEquals(RESTORE_RECOVERY_REQUIRED, rollback.message)
        assertFalse(rollback.message.contains("were replaced with the backup"))
        assertEquals("FILES_SWAPPED", journalPhase(rollbackStore))
        assertEquals(beforeRollback, rollbackStore.governedLearnerFingerprint())
        V2BackupFaults.reset()
        rollbackStore.close()
        val rolled = LocalStore.openNamed(context(), rollbackName).also { stores += it }
        assertEquals(setOf("backup note", "live note"), rolled.v2Notes().map { it.text }.toSet())
        assertFalse(journalFile(rolled).exists())

        val appearanceName = "recovery-appearance-${System.nanoTime()}"
        val appearanceStore = recordingStore(appearanceName)
        seedRecording(appearanceStore, byteArrayOf(7))
        appearanceStore.v2AddNote("kept", "backup note", "2026-10-08T12:00:00")
        val appearanceBackup = tempZip()
        appearanceBackup.outputStream().use {
            writeStoreBackup(appearanceStore, it, APP, pack.packId, pack.packVersion, AppearancePreference.FOLLOW_SYSTEM.name)
        }
        appearanceStore.v2AddNote("kept", "live note", "2026-10-08T12:05:00")
        val failing = AppearanceStore.failing(AppearancePreference.DARK, writeFails = true)
        V2BackupFaults.retainSwapArtifact = true
        val partial = V2BackupCoordinator.get(activity, appearanceStore, APP, pack.packId, pack.packVersion, failing)
        assertTrue(partial.begin(V2BackupPhase.PREPARING))
        partial.openBackup { appearanceBackup.inputStream() }
        awaitPhase(partial, V2BackupPhase.AWAITING_CONFIRMATION)
        partial.confirmRestore { }
        awaitPhase(partial, V2BackupPhase.COMPLETED)
        assertEquals(RESTORE_CLEANUP_PENDING_APPEARANCE_FAILED, partial.message)
        assertEquals(AppearancePreference.DARK, runBlocking { failing.preference.first() })
        assertEquals(listOf("backup note"), appearanceStore.v2Notes().map { it.text })
        assertEquals("COMMITTED", journalPhase(appearanceStore))
        assertNotEquals(RESTORE_CANCELLED, partial.message)
    }

    @Test
    fun staleOptionalAnswerDoesNotSuppressTheSameCurrentOption() {
        val located = pack.step("u1-st-vowels-check")!!
        val step = located.second
        val item = pack.item(step.itemIds.single())!!
        val option = item.options[0]
        val current = prepareGovernedAttempt(
            pack,
            item.id,
            option,
            OPTIONAL_PRACTICE,
            practiceStepId("alphabet"),
            "2026-10-08T16:00:00",
        )!!
        val store = memory()
        store.v2RecordGovernedAttempt(current)
        val stored = store.v2Attempts().single()
        assertEquals(option, optionalPracticeSelection(pack, store.v2LearnerView(), "alphabet", item.id)?.selected)
        val staleDigest = "stale-item-digest"
        assertNotEquals(staleDigest, V2Validator.checkItemContentDigest(item))
        listOf(
            "item" to stored.copy(itemDigest = staleDigest),
            "unit-content" to stored.copy(unitContentDigest = "stale-unit"),
            "rule" to stored.copy(ruleDigest = "stale-rule"),
            "schema" to stored.copy(identitySchema = 0),
            "role" to stored.copy(attemptRole = dev.russiancompanion.v2.curriculum.PRESCRIBED_CHECK),
            "context" to stored.copy(stepId = practiceStepId("reading")),
        ).forEach { (label, stale) ->
            val isolated = memory()
            isolated.v2ReplaceAttemptsForTests(listOf(stale.copy(id = "stale-$label", acceptedOrder = 4)))
            assertNull(
                "$label still counted as the current optional answer",
                optionalPracticeSelection(pack, isolated.v2LearnerView(), "alphabet", item.id),
            )
            isolated.v2RecordGovernedAttempt(
                prepareGovernedAttempt(pack, item.id, option, OPTIONAL_PRACTICE, practiceStepId("alphabet"), "2026-10-08T16:05:00")!!,
            )
            val selected = optionalPracticeSelection(pack, isolated.v2LearnerView(), "alphabet", item.id)
            assertEquals(option, selected?.selected)
            assertEquals(V2Validator.checkItemContentDigest(item), selected?.itemDigest)
            assertEquals(2, isolated.v2Attempts().size)
        }

        store.v2RetargetAttemptDigestForTests(stored.id, staleDigest)
        val backup = tempZip()
        backup.outputStream().use { writeStoreBackup(store, it, APP, pack.packId, pack.packVersion, "DARK") }
        val name = "optional-reopen-${System.nanoTime()}"
        val restored = LocalStore.openNamed(context(), name).also { stores += it }
        val read = readGovernedBackup(backup.inputStream(), APP, pack.packId, pack.packVersion, tempDir())
        assertEquals(GovernedReplaceOutcome.REPLACED, restoreStoreBackup(restored, (read as BackupReadResult.Valid).backup, tempDir()))
        restored.close()
        val reopened = LocalStore.openNamed(context(), name).also { stores += it }
        assertNull(optionalPracticeSelection(pack, reopened.v2LearnerView(), "alphabet", item.id))
        reopened.v2RecordGovernedAttempt(
            prepareGovernedAttempt(pack, item.id, option, OPTIONAL_PRACTICE, practiceStepId("alphabet"), "2026-10-08T16:06:00")!!,
        )
        assertEquals(option, optionalPracticeSelection(pack, reopened.v2LearnerView(), "alphabet", item.id)?.selected)
        assertEquals(2, reopened.v2Attempts().size)
    }

    @Test
    fun coldOnboardingAndSavedWorkIdentityLeaveTheMainThread() {
        V2UiWork.blockForTests = false
        val fresh = memory()
        compose.setContent { V2App(store = fresh, pack = pack, onRetryOpen = {}) }
        repeat(4) {
            awaitTag("v2-onboarding-next")
            compose.onNodeWithTag("v2-onboarding-next").performClick()
        }
        awaitTag("v2-start-unit-1")
        lastGovernedIdentityThreadForTests = null
        lastGovernedSnapshotLabelForTests = null
        compose.onNodeWithTag("v2-start-unit-1").performClick()
        idleUntil("onboarding resume") { lastGovernedSnapshotLabelForTests == "onboarding-resume" }
        val onboardingThread = lastGovernedIdentityThreadForTests
        assertNotEquals(Looper.getMainLooper().thread, onboardingThread)
        assertFalse(onboardingThread!!.name.startsWith("russian-companion-store"))
        idleUntil("onboarding saved") { fresh.v2Profile().onboardingCompleted }
        val sessionId = pack.unit(1)!!.sessions.first().id
        assertEquals(resumePositionDigest(pack, sessionId, 0), fresh.v2Profile().resumeContentDigest)
    }

    @Test
    fun savedWorkIdentityLeavesTheMainThreadAndRetrievesCreatedRecords() {
        V2UiWork.blockForTests = false
        val fresh = memory()
        val lesson = pack.unit(1)!!.sessions.first()
        val step = lesson.steps.first { it.kind != "CHECK" }
        place(fresh, lesson.id, lesson.steps.indexOf(step))
        compose.setContent { V2App(store = fresh, pack = pack, onRetryOpen = {}) }
        awaitTag("v2-continue")
        compose.onNodeWithTag("v2-continue").performClick()
        awaitTag("v2-note-draft")
        compose.onNodeWithTag("v2-note-draft").performScrollTo().performTextInput("AT-030 note alpha")
        compose.onNodeWithTag("v2-save-note").performScrollTo().performClick()
        idleUntil("note stored") { fresh.v2Notes().any { it.text == "AT-030 note alpha" } }
        compose.onNodeWithText("Bookmark this step").performScrollTo().performClick()
        idleUntil("bookmark stored") { fresh.v2Bookmarks().isNotEmpty() }
        compose.onNodeWithTag("v2-tutor").performScrollTo().performClick()
        idleUntil("tutor stored") { fresh.v2TutorQueue().any { it.note == "AT-030 note alpha" } }
        compose.onNodeWithTag("v2-back").performClick()
        awaitTag("v2-open-saved-work")
        governedIdentityThreadsForTests.remove("saved-work")
        lastGovernedSnapshotLabelForTests = null
        lastGovernedIdentityThreadForTests = null
        compose.onNodeWithTag("v2-open-saved-work").performScrollTo().performClick()
        awaitTag("v2-saved-heading")
        idleUntil(
            "saved work snapshot keys=${governedIdentityThreadsForTests.keys} last=${lastGovernedSnapshotLabelForTests}",
            timeoutMs = 20_000,
        ) {
            governedIdentityThreadsForTests["saved-work"] != null
        }
        val savedThread = governedIdentityThreadsForTests["saved-work"]
        assertNotEquals(Looper.getMainLooper().thread, savedThread)
        assertFalse(savedThread!!.name.startsWith("russian-companion-store"))
        val index = lesson.steps.indexOf(step) + 1
        awaitText("Not course content and not scored. AT-030 note alpha")
        awaitText("Optional and not scored. This is not advancement evidence. AT-030 note alpha")
        awaitText("Saved step.")
        awaitText("Resume ${lesson.title}, step $index of ${lesson.steps.size}.")
        awaitText(step.title)
    }

    @Test
    fun wrongUnitSameOptionDoesNotSuppressAfterHashedRestoreAndReopen() {
        val located = pack.step("u1-st-vowels-check")!!
        val step = located.second
        val item = pack.item(step.itemIds.single())!!
        val option = item.options[0]
        val prescribed = prepareGovernedAttempt(
            pack,
            item.id,
            option,
            dev.russiancompanion.v2.curriculum.PRESCRIBED_CHECK,
            step.id,
            "2026-10-08T17:00:00",
        )!!
        val optional = prepareGovernedAttempt(
            pack,
            item.id,
            option,
            OPTIONAL_PRACTICE,
            practiceStepId("alphabet"),
            "2026-10-08T17:00:01",
        )!!
        val source = memory()
        source.v2RecordGovernedAttempt(prescribed)
        source.v2RecordGovernedAttempt(optional)
        val wrongUnit = source.governedLearnerDocument().copy(
            attempts = source.governedLearnerDocument().attempts.map { it.copy(unitNumber = 2) },
        )
        assertTrue(wrongUnit.attempts.all { it.unitNumber == 2 })
        val zip = hashedBackup(wrongUnit)
        val read = readGovernedBackup(zip.inputStream(), APP, pack.packId, pack.packVersion, tempDir())
        assertTrue(read.toString(), read is BackupReadResult.Valid)
        val name = "wrong-unit-${System.nanoTime()}"
        val restored = LocalStore.openNamed(context(), name).also { stores += it }
        assertEquals(
            GovernedReplaceOutcome.REPLACED,
            restoreStoreBackup(restored, (read as BackupReadResult.Valid).backup, tempDir()),
        )
        restored.close()
        val reopened = LocalStore.openNamed(context(), name).also { stores += it }
        val learner = reopened.v2LearnerView()
        assertNull(optionalPracticeSelection(pack, learner, "alphabet", item.id))
        assertFalse(governedSessionSelections(pack, learner).containsKey(governedSelectionKey(step.id, item.id)))
        reopened.v2RecordGovernedAttempt(prescribed.copy(id = "again-prescribed", recordedAtLocal = "2026-10-08T17:05:00"))
        reopened.v2RecordGovernedAttempt(optional.copy(id = "again-optional", recordedAtLocal = "2026-10-08T17:05:01"))
        val after = reopened.v2LearnerView()
        assertEquals(option, optionalPracticeSelection(pack, after, "alphabet", item.id)?.selected)
        assertEquals(option, governedSessionSelections(pack, after)[governedSelectionKey(step.id, item.id)])
        assertEquals(4, reopened.v2Attempts().size)
        assertTrue(reopened.v2Attempts().any { it.unitNumber == 1 && it.acceptedOrder > 0 })
    }

    @Test
    fun duplicateAcknowledgementRequiresTheCapturedEpoch() {
        val located = pack.step("u1-st-vowels-check")!!
        val step = located.second
        val item = pack.item(step.itemIds.single())!!
        val first = item.options[0]
        val second = item.options[1]
        fun prescribed(selected: String, at: String) =
            prepareGovernedAttempt(pack, item.id, selected, dev.russiancompanion.v2.curriculum.PRESCRIBED_CHECK, step.id, at)!!
        val store = memory()
        val original = prescribed(first, "2026-10-08T18:00:00")
        store.v2RecordGovernedAttempt(original)
        val epoch = store.learnerMutationEpoch()
        store.openLearnerReplacement()
        assertFalse(runBlocking { store.v2RecordGovernedAttemptIfCurrentAsync(epoch, original) })
        assertEquals(1, store.v2Attempts().size)
        store.closeLearnerReplacement()
        assertFalse(runBlocking { store.v2RecordGovernedAttemptIfCurrentAsync(epoch, original) })
        assertEquals(1, store.v2Attempts().size)
        assertTrue(runBlocking { store.v2RecordGovernedAttemptIfCurrentAsync(store.learnerMutationEpoch(), original) })
        assertEquals(1, store.v2Attempts().size)

        val other = prescribed(second, "2026-10-08T18:00:02")
        val hold = CompletableDeferred<Unit>()
        val entered = CountDownLatch(1)
        store.holdGovernedAttemptForTests = hold
        store.governedAttemptHoldEnteredForTests = entered
        val captured = store.learnerMutationEpoch()
        val result = AtomicReference<Boolean?>()
        val writer = Thread {
            result.set(runBlocking { store.v2RecordGovernedAttemptIfCurrentAsync(captured, other) })
        }
        writer.start()
        awaitLatch(entered)
        store.openLearnerReplacement()
        hold.complete(Unit)
        writer.join(8_000)
        assertEquals(false, result.get())
        assertEquals(1, store.v2Attempts().size)
        store.closeLearnerReplacement()
        store.holdGovernedAttemptForTests = null
        store.v2RecordGovernedAttempt(other)
        store.v2RecordGovernedAttempt(prescribed(first, "2026-10-08T18:00:04"))
        val selections = store.v2Attempts().sortedBy { it.acceptedOrder }.map { it.selected }
        assertEquals(listOf(first, second, first), selections)
        assertEquals(listOf(1L, 2L, 3L), store.v2Attempts().map { it.acceptedOrder }.sorted())
    }

    @Test
    fun stagingLeaseSurvivesQueuedBoundaryAndDirectorySweep() {
        val recoveryRoot = tempDir()
        val owned = File(recoveryRoot, "incoming-owned")
        BackupWorkRegistry.createOwnedIncoming(owned)
        File(owned, "marker.txt").writeText("keep")
        val unowned = File(recoveryRoot, "incoming-unowned").apply { mkdirs() }
        File(unowned, "marker.txt").writeText("drop")
        val sweepers = List(4) {
            Thread {
                repeat(30) { BackupWorkRegistry.sweepUnownedIncoming(recoveryRoot, null) }
            }
        }
        sweepers.forEach { it.start() }
        sweepers.forEach { it.join(5_000) }
        assertEquals("keep", File(owned, "marker.txt").readText())
        assertFalse(unowned.exists())
        BackupWorkRegistry.holdIncoming(owned)
        BackupWorkRegistry.releaseIncoming(owned)
        BackupWorkRegistry.sweepUnownedIncoming(recoveryRoot, null)
        assertTrue(owned.isDirectory)
        BackupWorkRegistry.releaseIncoming(owned)
        BackupWorkRegistry.sweepUnownedIncoming(recoveryRoot, null)
        assertFalse(owned.exists())

        val activity = Robolectric.buildActivity(ComponentActivity::class.java).setup().get()
        val orphanDir = File(activity.applicationContext.filesDir, "backup-recovery")
        orphanDir.mkdirs()
        val orphan = File(orphanDir, "incoming-constructor-orphan").apply { mkdirs() }
        File(orphan, "marker.txt").writeText("gone")
        BackupWorkRegistry.lastDeleteThreadForTests = null
        V2BackupCoordinator.get(activity, memory(), APP, pack.packId, pack.packVersion, AppearanceStore.memory(AppearancePreference.DARK))
        idleUntil("constructor sweep", timeoutMs = 10_000) { !orphan.exists() }
        val deleteThread = BackupWorkRegistry.lastDeleteThreadForTests
        assertNotEquals(Looper.getMainLooper().thread, deleteThread)
        assertEquals("russian-companion-staging", deleteThread!!.name)

        queuedBoundaryKeepsRecording(activity)
        boundaryOpenedKeepsRecording()
    }

    @Test
    fun practiceDigestsRunOffMainAndReuseWarmUnitIdentities() {
        val located = pack.step("u1-st-vowels-check")!!
        val step = located.second
        val item = pack.item(step.itemIds.single())!!
        val option = item.options[0]
        val store = memory()
        val prescribed = prepareGovernedAttempt(
            pack,
            item.id,
            option,
            dev.russiancompanion.v2.curriculum.PRESCRIBED_CHECK,
            step.id,
            "2026-10-08T19:00:00",
        )!!
        val optional = prepareGovernedAttempt(
            pack,
            item.id,
            option,
            OPTIONAL_PRACTICE,
            practiceStepId("alphabet"),
            "2026-10-08T19:00:01",
        )!!
        store.v2RecordGovernedAttempt(prescribed)
        store.v2RecordGovernedAttempt(optional)
        store.v2ReplaceAttemptsForTests(store.v2Attempts().map { it.copy(correct = false) })
        val learner = store.v2LearnerView()
        V2PackIdentityIndex.clearForTests()
        V2Validator.unitDigestExecutionsForTests.set(0)
        val coldError = AtomicReference<Throwable>()
        val cold = Thread({
            try {
                practiceQueue(pack, learner, "mixed")
                optionalPracticeSelection(pack, learner, "alphabet", item.id)
            } catch (error: Throwable) {
                coldError.set(error)
            }
        }, "digest-cold")
        cold.start()
        cold.join(20_000)
        assertNull(coldError.get()?.stackTraceToString(), coldError.get())
        assertTrue(V2Validator.unitDigestExecutionsForTests.get() > 0)
        assertEquals(cold, V2Validator.unitDigestExecutionThreadForTests)
        val coldCount = V2Validator.unitDigestExecutionsForTests.get()
        val warmError = AtomicReference<Throwable>()
        val warm = Thread({
            try {
                V2PackIdentityIndex.warm(pack)
                val afterWarm = V2Validator.unitDigestExecutionsForTests.get()
                practiceQueue(pack, learner, "mixed")
                optionalPracticeSelection(pack, learner, "alphabet", item.id)
                if (V2Validator.unitDigestExecutionsForTests.get() != afterWarm) {
                    throw AssertionError("warm unit digest was hashed again")
                }
            } catch (error: Throwable) {
                warmError.set(error)
            }
        }, "digest-warm")
        warm.start()
        warm.join(20_000)
        assertNull(warmError.get()?.stackTraceToString(), warmError.get())
        assertTrue(V2Validator.unitDigestExecutionsForTests.get() > coldCount)
        val changed = pack.copy()
        val changedThread = Thread({
            practiceQueue(changed, learner, "mixed")
            optionalPracticeSelection(changed, learner, "alphabet", item.id)
        }, "digest-changed")
        val beforeChanged = V2Validator.unitDigestExecutionsForTests.get()
        changedThread.start()
        changedThread.join(20_000)
        assertTrue(V2Validator.unitDigestExecutionsForTests.get() > beforeChanged)
        assertEquals(changedThread, V2Validator.unitDigestExecutionThreadForTests)
        assertNotEquals(Looper.getMainLooper().thread, changedThread)

        V2PackIdentityIndex.clearForTests()
        V2Validator.unitDigestExecutionsForTests.set(0)
        V2UiWork.blockForTests = false
        compose.setContent {
            V2PracticeScreen(pack, learner, onTab = {}, onAlphabet = {}, onLab = {}, onSavedWork = {})
        }
        idleUntil("practice digest", timeoutMs = 20_000) {
            V2Validator.unitDigestExecutionsForTests.get() > 0 &&
                governedIdentityThreadsForTests["practice"] != null
        }
        val published = V2Validator.unitDigestExecutionThreadForTests
        assertNotEquals(Looper.getMainLooper().thread, published)
        assertFalse(published!!.name.startsWith("russian-companion-store"))
        assertNotEquals(Looper.getMainLooper().thread, governedIdentityThreadsForTests["practice"])
        awaitText("Alphabet recognition")
    }

    @Test
    fun practiceSnapshotDiscardsAStaleGeneration() {
        V2UiWork.blockForTests = false
        val holds = java.util.concurrent.ConcurrentHashMap<Long, CompletableDeferred<Unit>>()
        val calls = java.util.concurrent.atomic.AtomicInteger(0)
        val firstEntered = CountDownLatch(1)
        val secondEntered = CountDownLatch(1)
        V2UiWork.governedComputeGateForTests = gate@{ label, token ->
            if (label != "practice") return@gate
            val hold = CompletableDeferred<Unit>()
            holds[token] = hold
            if (calls.incrementAndGet() == 1) firstEntered.countDown() else secondEntered.countDown()
            hold.await()
        }
        val outcomes = java.util.Collections.synchronizedList(mutableListOf<Pair<Long, Boolean>>())
        val discarded = CountDownLatch(1)
        val published = CountDownLatch(1)
        V2UiWork.governedSnapshotOutcomeForTests = outcome@{ label, token, didPublish ->
            if (label != "practice") return@outcome
            outcomes.add(token to didPublish)
            if (didPublish) published.countDown() else discarded.countDown()
        }
        val base = memory().v2LearnerView()
        val secondLearner = base.copy(currentUnitNumber = 30, completedUnitNumbers = setOf(1))
        assertNotEquals(base, secondLearner)
        val learner = androidx.compose.runtime.mutableStateOf(base)
        compose.setContent {
            V2PracticeScreen(pack, learner.value, onTab = {}, onAlphabet = {}, onLab = {}, onSavedWork = {})
        }
        awaitLatch(firstEntered)
        learner.value = secondLearner
        androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications()
        pumpUntil("second practice generation entered; holds=$holds", timeoutMs = 20_000) {
            secondEntered.count == 0L
        }
        val older = holds.keys.min()
        val newer = holds.keys.max()
        assertTrue("tokens $older then $newer", newer > older)
        holds.getValue(newer).complete(Unit)
        pumpUntil("newer practice snapshot published; outcomes=$outcomes", timeoutMs = 30_000) {
            published.count == 0L
        }
        holds[older]?.complete(Unit)
        pumpUntil("older practice snapshot discarded; outcomes=$outcomes", timeoutMs = 20_000) {
            discarded.count == 0L
        }
        assertEquals(false, outcomes.first { it.first == older }.second)
        assertEquals(true, outcomes.first { it.first == newer }.second)
        val expected = practiceOffers(pack, secondLearner).first { it.id == "alphabet" }.detail
        pumpUntil("text $expected") {
            compose.onAllNodes(androidx.compose.ui.test.hasText(expected, substring = true), useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun identityCacheReclaimsDeadPacksWithoutRevisitingBuckets() {
        val bounded = IdentityWeakCache<ByteArray>(maxRetained = 8)
        val live = ArrayList<Any>()
        repeat(20) {
            val key = Any()
            live += key
            bounded.put(key, ByteArray(32))
        }
        assertTrue(bounded.retainedValueCountForTests() <= 8)
        assertNotNull(bounded.get(live.last()))
        assertNull(bounded.get(live.first()))

        val collected = IdentityWeakCache<ByteArray>(maxRetained = 100)
        val weaks = ArrayList<WeakReference<Any>>()
        repeat(30) { orphanIdentity(collected, weaks) }
        val survivor = Any()
        collected.put(survivor, byteArrayOf(7))
        val started = System.currentTimeMillis()
        while (weaks.any { it.get() != null } && System.currentTimeMillis() - started < 5_000) {
            System.gc()
            Runtime.getRuntime().runFinalization()
            collected.reclaim()
            Thread.sleep(25)
        }
        collected.get(Any())
        assertTrue(weaks.all { it.get() == null })
        assertEquals(1, collected.retainedValueCountForTests())
        assertTrue(collected.get(survivor)!!.contentEquals(byteArrayOf(7)))

        val concurrent = IdentityWeakCache<Int>(maxRetained = 32)
        val keys = List(16) { Any() }
        val failures = java.util.Collections.synchronizedList(mutableListOf<Throwable>())
        val workers = List(8) { worker ->
            Thread {
                try {
                    repeat(40) { step ->
                        val key = keys[(worker + step) % keys.size]
                        concurrent.put(key, worker)
                        concurrent.reclaim()
                        concurrent.get(key)
                    }
                } catch (error: Throwable) {
                    failures += error
                }
            }
        }
        workers.forEach { it.start() }
        workers.forEach { it.join(8_000) }
        assertTrue(failures.toString(), failures.isEmpty())
        assertTrue(concurrent.retainedValueCountForTests() <= 32)
        keys.forEach { key -> assertNotNull(concurrent.get(key)) }
    }

    @Test
    fun explicitPairsMatchTheRegexOnCounterexamplesAndRandomDashText() {
        val samples = listOf(
            "a— \n—кот–cat",
            "\n—кот–cat",
            "a— \n",
            "a— \t\n—x",
            "\n\n—кот–cat",
            "  \n—кот–cat",
            "a— \n—кот–cat\nb— \n—пёс–dog",
            "я—" + "б".repeat(161) + "–х",
            "а".repeat(91) + "—б–в",
            " \n — \n—кот–cat",
        )
        samples.forEach { sample ->
            assertEquals(sample, V2Validator.explicitPairsRegexProbe(sample), V2Validator.explicitPairsProbe(sample))
        }
        assertEquals(setOf("cat"), V2Validator.explicitPairsProbe("a— \n—кот–cat")["кот"])
        assertEquals(setOf("cat"), V2Validator.explicitPairsProbe("\n—кот–cat")["кот"])
        val random = Random(20261008)
        val alphabet = charArrayOf('a', ' ', '\n', '\t', '\r', '—', '–', 'к', 'о', 'т', 'b', 'x', '"', '—')
        repeat(200) { index ->
            val text = buildString {
                repeat(1 + random.nextInt(80)) { append(alphabet[random.nextInt(alphabet.size)]) }
            }
            assertEquals(
                "random-$index:${text.replace("\n", "\\n")}",
                V2Validator.explicitPairsRegexProbe(text),
                V2Validator.explicitPairsProbe(text),
            )
        }
    }

    @Test
    fun unsupportedDashPairsStayRejected() {
        val item = pack.items.first { it.assessment != null }
        listOf("a— \n—кот–cat", "\n—кот–cat").forEach { source ->
            assertEquals(setOf("cat"), V2Validator.explicitPairsRegexProbe(source)["кот"])
            val rejected = V2Validator.unsupportedRelationshipErrorsForTests(
                relationshipClaim(item, """The frozen source pairs "кот" with "dog"."""),
                listOf(source),
            )
            assertTrue(
                rejected.joinToString(),
                rejected.any { it.contains(item.id) && it.contains("unsupported by the cited frozen record") },
            )
            val supported = V2Validator.unsupportedRelationshipErrorsForTests(
                relationshipClaim(item, """The frozen source pairs "кот" with "cat"."""),
                listOf(source),
            )
            assertFalse(
                supported.joinToString(),
                supported.any { it.contains("unsupported by the cited frozen record") },
            )
        }
    }

    @Test
    fun failedStagingParentClosesTheStreamAndAllowsRetry() {
        val store = memory()
        store.v2AddNote("kept", "stay", "2026-10-08T19:00:00")
        val before = store.governedLearnerFingerprint()
        val backup = tempZip()
        backup.outputStream().use { writeStoreBackup(store, it, APP, pack.packId, pack.packVersion, "DARK") }
        val activity = Robolectric.buildActivity(ComponentActivity::class.java).setup().get()
        val recovery = File(activity.applicationContext.filesDir, "backup-recovery")
        val preserved = File(activity.applicationContext.filesDir, "preserved-recovery.bin")
        val preservedBytes = byteArrayOf(2, 7, 1, 8)
        try {
            if (recovery.exists()) recovery.deleteRecursively()
            assertTrue(recovery.createNewFile())
            preserved.writeBytes(preservedBytes)
            val coordinator = V2BackupCoordinator.get(
                activity,
                store,
                APP,
                pack.packId,
                pack.packVersion,
                AppearanceStore.memory(AppearancePreference.DARK),
            )
            val stream = ClosingStream(ByteArrayInputStream(byteArrayOf(1, 2, 3)))
            assertTrue(coordinator.begin(V2BackupPhase.PREPARING))
            coordinator.openBackup { stream }
            awaitPhase(coordinator, V2BackupPhase.FAILED)
            assertEquals(RESTORE_FAILED, coordinator.message)
            assertTrue(stream.closed.get())
            assertEquals(0, BackupWorkRegistry.incomingOwnershipTotalForTests())
            assertEquals(before, store.governedLearnerFingerprint())
            assertEquals(listOf("stay"), store.v2Notes().map { it.text })
            assertTrue(preservedBytes.contentEquals(preserved.readBytes()))
            assertTrue(recovery.delete())
            assertTrue(recovery.mkdirs())
            assertTrue(coordinator.begin(V2BackupPhase.PREPARING))
            coordinator.openBackup { backup.inputStream() }
            awaitPhase(coordinator, V2BackupPhase.AWAITING_CONFIRMATION)
            assertEquals(before, store.governedLearnerFingerprint())
            assertTrue(preservedBytes.contentEquals(preserved.readBytes()))
            assertEquals(listOf("stay"), store.v2Notes().map { it.text })
            coordinator.cancel()
            awaitPhase(coordinator, V2BackupPhase.CANCELLED)
            idleUntil("retry staging cleared") { incoming(activity).isEmpty() }
        } finally {
            if (recovery.isFile) recovery.delete()
            preserved.delete()
        }
    }

    @Test
    fun failedStagingAcquisitionReleasesTheLeaseAndClosesTheStream() {
        val store = memory()
        store.v2AddNote("kept", "stay", "2026-10-08T19:10:00")
        val before = store.governedLearnerFingerprint()
        val backup = tempZip()
        backup.outputStream().use { writeStoreBackup(store, it, APP, pack.packId, pack.packVersion, "DARK") }
        val activity = Robolectric.buildActivity(ComponentActivity::class.java).setup().get()
        val recovery = File(activity.applicationContext.filesDir, "backup-recovery")
        if (recovery.isFile) recovery.delete()
        recovery.mkdirs()
        val marker = File(recovery, "rollback/keep-recovery.txt")
        marker.parentFile?.mkdirs()
        val recoveryBytes = byteArrayOf(3, 1, 4, 1, 5)
        marker.writeBytes(recoveryBytes)
        val other = File(recovery, "incoming-other-owner")
        other.mkdirs()
        val ownedBytes = byteArrayOf(8, 6, 7)
        File(other, "owned.bin").writeBytes(ownedBytes)
        BackupWorkRegistry.holdIncoming(other)
        val coordinator = V2BackupCoordinator.get(
            activity,
            store,
            APP,
            pack.packId,
            pack.packVersion,
            AppearanceStore.memory(AppearancePreference.DARK),
        )
        val entered = CountDownLatch(1)
        val release = CountDownLatch(1)
        V2BackupFaults.stagingCreateEntered = entered
        V2BackupFaults.stagingCreateHold = release
        val stream = ClosingStream(backup.inputStream())
        assertTrue(coordinator.begin(V2BackupPhase.PREPARING))
        coordinator.openBackup { stream }
        awaitLatch(entered)
        val partial = incoming(activity).single {
            it.name != other.name && BackupWorkRegistry.incomingOwnershipForTests(it) == 1
        }
        assertTrue(partial.isDirectory)
        assertEquals(1, BackupWorkRegistry.incomingOwnershipForTests(partial))
        assertFalse(stream.closed.get())
        coordinator.cancelWorkForTests()
        V2BackupFaults.failStagingCreate = true
        release.countDown()
        idleUntil("failed staging removed off Main", timeoutMs = 15_000) {
            stream.closed.get() &&
                !partial.exists() &&
                BackupWorkRegistry.incomingOwnershipForTests(partial) == 0
        }
        val deleter = BackupWorkRegistry.lastDeleteThreadForTests
        assertNotNull(deleter)
        assertNotEquals(Looper.getMainLooper().thread, deleter)
        assertEquals("russian-companion-staging", deleter!!.name)
        assertTrue(other.isDirectory)
        assertTrue(ownedBytes.contentEquals(File(other, "owned.bin").readBytes()))
        assertTrue(BackupWorkRegistry.incomingHeld(other))
        assertTrue(recoveryBytes.contentEquals(marker.readBytes()))
        assertEquals(before, store.governedLearnerFingerprint())
        assertEquals(listOf("stay"), store.v2Notes().map { it.text })
        assertFalse(incoming(activity).any { it.name == partial.name })

        V2BackupFaults.failStagingCreate = false
        V2BackupFaults.stagingCreateEntered = null
        V2BackupFaults.stagingCreateHold = null
        val again = V2BackupCoordinator.get(
            Robolectric.buildActivity(ComponentActivity::class.java).setup().get(),
            store,
            APP,
            pack.packId,
            pack.packVersion,
            AppearanceStore.memory(AppearancePreference.DARK),
        )
        assertTrue(again.begin(V2BackupPhase.PREPARING))
        again.openBackup { backup.inputStream() }
        awaitPhase(again, V2BackupPhase.AWAITING_CONFIRMATION)
        assertEquals(before, store.governedLearnerFingerprint())
        assertTrue(recoveryBytes.contentEquals(marker.readBytes()))
        assertTrue(ownedBytes.contentEquals(File(other, "owned.bin").readBytes()))
        assertTrue(BackupWorkRegistry.incomingHeld(other))
        again.cancel()
        awaitPhase(again, V2BackupPhase.CANCELLED)
        BackupWorkRegistry.releaseIncoming(other)
        other.deleteRecursively()
        idleUntil("acquisition fixtures cleared") { incoming(activity).isEmpty() }
        assertTrue(recoveryBytes.contentEquals(marker.readBytes()))
    }

    @Test
    fun baselineSavedRecordsSurviveRestoreAndReopen() {
        val lesson = pack.unit(1)!!.sessions.first()
        val step = lesson.steps.first { it.kind != "CHECK" }
        val digest = stepContentDigest(pack, step)
        val name = "baseline-four-${System.nanoTime()}"
        val store = LocalStore.openNamed(context(), name).also { stores += it }
        place(store, lesson.id, lesson.steps.indexOf(step))
        assertTrue(store.v2ToggleBookmark(step.id, "STEP", "2026-10-08T19:20:00", digest, GOVERNED_IDENTITY_SCHEMA))
        store.v2AddNote(step.id, "AT-030 note alpha", "2026-10-08T19:20:01", digest, GOVERNED_IDENTITY_SCHEMA)
        store.v2AddTutorItem(step.id, "AT-030 note alpha", "2026-10-08T19:20:02", digest, GOVERNED_IDENTITY_SCHEMA)
        assertBaselineSavedRecords(store)
        val baseline = hashedBackup(store.governedLearnerDocument())
        store.v2AddNote(step.id, "AT-030 note later", "2026-10-08T19:21:00", digest, GOVERNED_IDENTITY_SCHEMA)
        val later = hashedBackup(store.governedLearnerDocument())
        val laterRead = readGovernedBackup(later.inputStream(), APP, pack.packId, pack.packVersion, tempDir())
        assertTrue(laterRead is BackupReadResult.Valid)
        assertTrue((laterRead as BackupReadResult.Valid).backup.document.notes.any { it.text == "AT-030 note later" })
        val baselineRead = readGovernedBackup(baseline.inputStream(), APP, pack.packId, pack.packVersion, tempDir())
        assertEquals(
            GovernedReplaceOutcome.REPLACED,
            restoreStoreBackup(store, (baselineRead as BackupReadResult.Valid).backup, tempDir()),
        )
        stores.remove(store)
        store.close()
        val reopened = LocalStore.openNamed(context(), name).also { stores += it }
        assertBaselineSavedRecords(reopened)
        assertFalse(reopened.v2Notes().any { it.text == "AT-030 note later" })
    }

    @Test
    fun explicitPairsMatchTheRegexAndStayBoundedOnARepresentativeText() {
        val samples = listOf(
            "a — b",
            "a—b",
            "a – b",
            "ab\ncd—ef",
            "x—y—z",
            "hello — world — next",
            "a".repeat(100) + "—b",
            "кот — cat (feline)",
            "\"кот\", \"пёс\" (cat, dog)",
            "no dash here",
            "left —\nright",
            "word\n— gloss",
            "  " + "я".repeat(95) + " — gloss",
            " —bb–b",
            "  —bb–b",
            "a — b – c",
            "\u00A0слово — gloss",
            "«слово» — gloss",
            "\"кот\" — cat",
            "слово\u2014gloss",
            "  \n— gloss",
        )
        samples.forEach { sample ->
            assertEquals(sample, V2Validator.explicitPairsRegexProbe(sample), V2Validator.explicitPairsProbe(sample))
        }
        var compared = 0
        pack.provenanceRecords.forEach { record ->
            val text = record.originalSourceText
            if (text.length > 8_000) return@forEach
            if (!text.contains('—') && !text.contains('–')) return@forEach
            compared += 1
            assertEquals(
                text.take(120),
                V2Validator.explicitPairsRegexProbe(text),
                V2Validator.explicitPairsProbe(text),
            )
        }
        assertTrue("governed dash corpus compared=$compared", compared > 0)
        val repeated = buildString { repeat(300) { append("слово — gloss\n") } }
        assertEquals(V2Validator.explicitPairsRegexProbe(repeated), V2Validator.explicitPairsProbe(repeated))
        val large = buildString { repeat(4_000) { append("слово — gloss\n") } }
        val started = System.nanoTime()
        val once = V2Validator.explicitPairsProbe(large)
        val firstMs = (System.nanoTime() - started) / 1_000_000
        val cachedStart = System.nanoTime()
        repeat(30) { assertEquals(once.size, V2Validator.explicitPairsProbe(large).size) }
        val cachedMs = (System.nanoTime() - cachedStart) / 1_000_000
        println("explicitPairs representative firstMs=$firstMs cached30Ms=$cachedMs pairs=${once.size}")
        assertTrue("representative explicitPairs took ${firstMs}ms", firstMs < 2_000)
        assertTrue("cached explicitPairs took ${cachedMs}ms", cachedMs < 500)
        assertTrue(once.containsKey("слово"))

        V2PackIdentityIndex.warm(pack)
        val stepId = pack.units.first().sessions.first().steps.first().id
        assertTrue(V2PackIdentityIndex.stepDigest(pack, stepId) != null)
        val copy = pack.copy()
        assertNull(V2PackIdentityIndex.stepDigest(copy, stepId))
        val identityStart = System.nanoTime()
        repeat(1_000) { System.identityHashCode(pack) }
        val identityMs = (System.nanoTime() - identityStart) / 1_000_000
        val structuralStart = System.nanoTime()
        pack.hashCode()
        val structuralMs = (System.nanoTime() - structuralStart) / 1_000_000
        println("pack identityKey1000Ms=$identityMs structuralHashOnceMs=$structuralMs")
        assertTrue(identityMs <= structuralMs + 50)
    }

    private fun relationshipClaim(
        item: dev.russiancompanion.v2.curriculum.V2CheckItem,
        claim: String,
    ): dev.russiancompanion.v2.curriculum.V2CheckItem {
        val assessment = checkNotNull(item.assessment)
        return item.copy(
            feedback = claim,
            distractorRationales = listOf(claim),
            assessment = assessment.copy(
                whyAnswerDemonstratesObjective = claim,
                distractorDifference = listOf(claim),
            ),
        )
    }

    private fun assertBaselineSavedRecords(store: LocalStore) {
        val entries = savedWorkEntries(pack, store.v2LearnerView())
        assertTrue(entries.any { it.kind == "RESUME" && it.resumable })
        assertTrue(entries.any { it.kind == "BOOKMARK" && it.resumable })
        assertTrue(entries.any { it.kind == "NOTE" && it.detail.contains("AT-030 note alpha") })
        assertTrue(entries.any { it.kind == "TUTOR" && it.detail.contains("AT-030 note alpha") })
        assertFalse(entries.any { it.detail.contains("AT-030 note later") })
        assertEquals(listOf("AT-030 note alpha"), store.v2Notes().map { it.text })
    }

    private class ClosingStream(private val source: InputStream) : InputStream() {
        val closed = AtomicBoolean(false)
        override fun read(): Int = source.read()
        override fun read(buffer: ByteArray, offset: Int, length: Int): Int = source.read(buffer, offset, length)
        override fun close() {
            closed.set(true)
            source.close()
        }
    }

    private fun migrateFileBackedSchemaEight() {
        val name = "schema8-file-${System.nanoTime()}.db"
        val context = context()
        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(name)
                .callback(object : SupportSQLiteOpenHelper.Callback(8) {
                    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        createSchemaEight(db)
                    }

                    override fun onUpgrade(
                        db: androidx.sqlite.db.SupportSQLiteDatabase,
                        oldVersion: Int,
                        newVersion: Int,
                    ) = Unit
                })
                .build(),
        )
        val db = helper.writableDatabase
        db.execSQL(
            "INSERT INTO v2_attempts (id, itemId, selected, correct, recordedAtLocal) VALUES " +
                "('late', 'item', 'A', 1, '2026-10-08T12:00:03')",
        )
        db.execSQL(
            "INSERT INTO v2_attempts (id, itemId, selected, correct, recordedAtLocal) VALUES " +
                "('early', 'item', 'B', 1, '2026-10-08T12:00:01')",
        )
        helper.close()
        val migrated = LocalStore.openNamed(context, name).also { stores += it }
        val historical = migrated.v2Attempts().associate { it.id to it.acceptedOrder }
        assertEquals(mapOf("late" to 0L, "early" to 0L), historical)
        val located = pack.step("u1-st-vowels-check")!!
        val item = pack.item(located.second.itemIds.single())!!
        migrated.v2RecordGovernedAttempt(
            prepareGovernedAttempt(
                pack,
                item.id,
                item.options[0],
                dev.russiancompanion.v2.curriculum.PRESCRIBED_CHECK,
                located.second.id,
                "2026-10-08T18:00:00",
            )!!,
        )
        assertEquals(0L, migrated.v2Attempts().first { it.id == "early" }.acceptedOrder)
        assertEquals(0L, migrated.v2Attempts().first { it.id == "late" }.acceptedOrder)
        assertEquals(1L, migrated.v2Attempts().first { it.id != "early" && it.id != "late" }.acceptedOrder)
        migrated.close()
        context.deleteDatabase(name)
    }

    private fun createSchemaEight(db: androidx.sqlite.db.SupportSQLiteDatabase) {
        val schema = JSONObject(File(repo(), "app/schemas/dev.russiancompanion.data.local.AppDatabase/8.json").readText())
        val entities = schema.getJSONObject("database").getJSONArray("entities")
        for (index in 0 until entities.length()) {
            val entity = entities.getJSONObject(index)
            val table = entity.getString("tableName")
            db.execSQL(entity.getString("createSql").replace("\${TABLE_NAME}", table))
            val indices = entity.optJSONArray("indices") ?: continue
            for (indexIndex in 0 until indices.length()) {
                val create = indices.getJSONObject(indexIndex).getString("createSql").replace("\${TABLE_NAME}", table)
                db.execSQL(create)
            }
        }
        db.version = 8
    }

    private fun hashedBackup(document: dev.russiancompanion.v2.backup.GovernedLearnerDocument): File {
        val zip = tempZip()
        zip.outputStream().use {
            writeGovernedBackup(it, APP, pack.packId, pack.packVersion, "DARK", document, emptyList())
        }
        return zip
    }

    private fun attemptRow(
        write: dev.russiancompanion.v2.curriculum.GovernedAttemptWrite,
        id: String,
        order: Long,
    ): V2AttemptEntity {
        return V2AttemptEntity(
            id = id,
            itemId = write.itemId,
            selected = write.selected,
            correct = write.correct,
            recordedAtLocal = write.recordedAtLocal,
            itemDigest = write.itemDigest,
            unitContentDigest = write.unitContentDigest,
            ruleDigest = write.ruleDigest,
            identitySchema = write.identitySchema,
            attemptRole = write.attemptRole,
            objectiveId = write.objectiveId,
            unitNumber = write.unitNumber,
            stepId = write.stepId,
            acceptedOrder = order,
        )
    }

    private fun seedRecording(store: LocalStore, bytes: ByteArray): dev.russiancompanion.data.local.RecordingRecordEntity {
        val record = store.commitRecording(
            "audio-1",
            "recordings/one.m4a",
            "2026-10-08T09:00:00",
            false,
            CompletionState.NOT_COMPLETED,
        )
        val root = checkNotNull(store.recordingDirectory())
        root.mkdirs()
        File(root, File(record.localFileReference).name).writeBytes(bytes)
        return record
    }

    private fun recordingStore(name: String): LocalStore {
        val store = LocalStore.openNamed(context(), name).also { stores += it }
        store.applySeed(
            SeedJson.encode(
                minimalDocument().copy(
                    sourceTexts = listOf(text(matchVerified = true)),
                    assetRightsRecords = listOf(rights()),
                    audioModels = listOf(audio()),
                ),
            ),
        )
        return store
    }

    private fun journalFile(store: LocalStore): File {
        val root = checkNotNull(store.recordingDirectory())
        return File(checkNotNull(root.parentFile), "backup-replace-${root.name}.json")
    }

    private fun journalPhase(store: LocalStore): String? {
        val file = journalFile(store)
        if (!file.isFile) return null
        return backupJson.decodeFromString(ReplacementJournal.serializer(), file.readText()).phase
    }

    private fun incoming(activity: ComponentActivity): List<File> {
        val recovery = File(activity.applicationContext.filesDir, "backup-recovery")
        return recovery.listFiles().orEmpty().filter { it.isDirectory && it.name.startsWith("incoming-") }
    }

    private fun place(store: LocalStore, sessionId: String, index: Int) {
        val digest = resumePositionDigest(pack, sessionId, index).orEmpty()
        store.v2SaveProfile(
            store.v2Profile().copy(
                onboardingCompleted = true,
                currentUnitNumber = 1,
                currentSessionId = sessionId,
                currentStepIndex = index,
                resumeContentDigest = digest,
                resumeIdentitySchema = if (digest.isEmpty()) 0 else dev.russiancompanion.v2.curriculum.GOVERNED_IDENTITY_SCHEMA,
            ),
        )
    }

    private fun awaitLatch(latch: CountDownLatch, timeoutMs: Long = 8_000) {
        val started = System.currentTimeMillis()
        while (latch.count > 0 && System.currentTimeMillis() - started < timeoutMs) {
            Shadows.shadowOf(Looper.getMainLooper()).idle()
            Thread.sleep(15)
        }
        assertEquals(0, latch.count)
    }

    private fun pumpUntil(message: String, timeoutMs: Long = 8_000, condition: () -> Boolean) {
        val started = System.currentTimeMillis()
        while (!condition() && System.currentTimeMillis() - started < timeoutMs) {
            compose.mainClock.advanceTimeByFrame()
            Shadows.shadowOf(Looper.getMainLooper()).idle()
            Thread.sleep(15)
        }
        assertTrue(message, condition())
    }

    private fun idleUntil(message: String, timeoutMs: Long = 8_000, condition: () -> Boolean) {
        val started = System.currentTimeMillis()
        while (!condition() && System.currentTimeMillis() - started < timeoutMs) {
            Shadows.shadowOf(Looper.getMainLooper()).idle()
            Thread.sleep(15)
        }
        assertTrue(message, condition())
    }

    private fun awaitPhase(coordinator: V2BackupCoordinator, phase: V2BackupPhase) {
        idleUntil("phase $phase, was ${coordinator.phase}: ${coordinator.message}") { coordinator.phase == phase }
    }

    private fun awaitTag(tag: String) {
        idleUntil("tag $tag") {
            compose.onAllNodes(androidx.compose.ui.test.hasTestTag(tag), useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun awaitText(text: String) {
        idleUntil("text $text") {
            compose.onAllNodes(androidx.compose.ui.test.hasText(text, substring = true), useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun memory(): LocalStore = LocalStore.openInMemory(context()).also { stores += it }

    private fun context(): Context = ApplicationProvider.getApplicationContext()

    private fun tempZip(): File = File.createTempFile("remaining-backup", ".zip").apply { deleteOnExit() }

    private fun tempDir(): File = File.createTempFile("remaining-stage", "").apply {
        delete()
        mkdirs()
        deleteOnExit()
    }

    private fun repo(): File {
        var dir = File(checkNotNull(System.getProperty("user.dir")))
        while (!File(dir, "settings.gradle.kts").isFile) dir = checkNotNull(dir.parentFile)
        return dir
    }

    private fun queuedBoundaryKeepsRecording(activity: ComponentActivity) {
        val store = recordingStore("queued-boundary")
        val seeded = seedRecording(store, byteArrayOf(9, 8, 7))
        store.v2AddNote("kept", "backup note", "2026-10-08T12:00:00")
        val backup = tempZip()
        backup.outputStream().use { writeStoreBackup(store, it, APP, pack.packId, pack.packVersion, "DARK") }
        store.v2AddNote("kept", "live note", "2026-10-08T12:05:00")
        val queued = CountDownLatch(1)
        V2BackupFaults.boundaryQueuedForTests = queued
        val coordinator = V2BackupCoordinator.get(
            activity,
            store,
            APP,
            pack.packId,
            pack.packVersion,
            AppearanceStore.memory(AppearancePreference.DARK),
        )
        assertTrue(coordinator.begin(V2BackupPhase.PREPARING))
        coordinator.openBackup { backup.inputStream() }
        awaitPhase(coordinator, V2BackupPhase.AWAITING_CONFIRMATION)
        val staged = incoming(activity).single()
        val marker = File(staged, "queued-marker.txt").apply { writeText("keep") }
        val executorEntered = CountDownLatch(1)
        val executorRelease = CountDownLatch(1)
        store.holdExecutorForTests(executorEntered, executorRelease)
        awaitLatch(executorEntered)
        coordinator.confirmRestore { }
        awaitLatch(queued)
        activity.viewModelStore.clear()
        V2BackupCoordinator.get(
            activity,
            memory(),
            APP,
            pack.packId,
            pack.packVersion,
            AppearanceStore.memory(AppearancePreference.DARK),
        ).sweepOrphansForTests()
        assertEquals("keep", marker.readText())
        executorRelease.countDown()
        idleUntil("queued boundary replacement", timeoutMs = 15_000) {
            store.v2Notes().map { it.text } == listOf("backup note") && incoming(activity).isEmpty()
        }
        val live = File(checkNotNull(store.recordingDirectory()), File(seeded.localFileReference).name)
        assertTrue(live.readBytes().contentEquals(byteArrayOf(9, 8, 7)))
        assertFalse(marker.exists())
    }

    private fun boundaryOpenedKeepsRecording() {
        val activity = Robolectric.buildActivity(ComponentActivity::class.java).setup().get()
        val store = recordingStore("opened-boundary")
        val seeded = seedRecording(store, byteArrayOf(3, 2, 1))
        store.v2AddNote("kept", "backup note", "2026-10-08T12:00:00")
        val backup = tempZip()
        backup.outputStream().use { writeStoreBackup(store, it, APP, pack.packId, pack.packVersion, "DARK") }
        store.v2AddNote("kept", "live note", "2026-10-08T12:05:00")
        val opened = CountDownLatch(1)
        V2BackupFaults.boundaryOpenedForTests = opened
        val beforeIo = CompletableDeferred<Unit>()
        V2BackupFaults.afterBoundaryBeforeIo = beforeIo
        val coordinator = V2BackupCoordinator.get(
            activity,
            store,
            APP,
            pack.packId,
            pack.packVersion,
            AppearanceStore.memory(AppearancePreference.DARK),
        )
        assertTrue(coordinator.begin(V2BackupPhase.PREPARING))
        coordinator.openBackup { backup.inputStream() }
        awaitPhase(coordinator, V2BackupPhase.AWAITING_CONFIRMATION)
        val staged = incoming(activity).single()
        val marker = File(staged, "opened-marker.txt").apply { writeText("keep") }
        coordinator.confirmRestore { }
        awaitLatch(opened)
        activity.viewModelStore.clear()
        V2BackupCoordinator.get(
            activity,
            memory(),
            APP,
            pack.packId,
            pack.packVersion,
            AppearanceStore.memory(AppearancePreference.DARK),
        ).sweepOrphansForTests()
        assertEquals("keep", marker.readText())
        beforeIo.complete(Unit)
        idleUntil("opened boundary replacement", timeoutMs = 15_000) {
            store.v2Notes().map { it.text } == listOf("backup note") && incoming(activity).isEmpty()
        }
        val live = File(checkNotNull(store.recordingDirectory()), File(seeded.localFileReference).name)
        assertTrue(live.readBytes().contentEquals(byteArrayOf(3, 2, 1)))
        assertFalse(marker.exists())
    }

    private fun orphanIdentity(cache: IdentityWeakCache<ByteArray>, weaks: MutableList<WeakReference<Any>>) {
        val key = Any()
        weaks += WeakReference(key)
        cache.put(key, ByteArray(1024))
    }

    private companion object {
        const val APP = "dev.russiancompanion"
    }
}
