package dev.russiancompanion.v2

import androidx.compose.ui.test.performScrollToNode

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.appearance.AppearanceStore
import dev.russiancompanion.data.local.CompletionState
import dev.russiancompanion.data.local.ExportRecordEntity
import dev.russiancompanion.data.local.ExportStatus
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.data.local.audio
import dev.russiancompanion.data.local.minimalDocument
import dev.russiancompanion.data.local.rights
import dev.russiancompanion.data.local.seed.SeedJson
import dev.russiancompanion.data.local.text
import dev.russiancompanion.domain.appearance.AppearancePreference
import dev.russiancompanion.presentation.MainActivity
import dev.russiancompanion.v2.backup.BACKUP_CANCELLED
import dev.russiancompanion.v2.backup.BACKUP_FAILED
import dev.russiancompanion.v2.backup.BACKUP_FORMAT_VERSION
import dev.russiancompanion.v2.backup.BackupReadResult
import dev.russiancompanion.v2.backup.BackupRejection
import dev.russiancompanion.v2.backup.GovernedReplaceOutcome
import dev.russiancompanion.v2.backup.RESTORE_CANCELLED
import dev.russiancompanion.v2.backup.RESTORE_DONE
import dev.russiancompanion.v2.backup.RESTORE_FAILED
import dev.russiancompanion.v2.backup.RESTORE_RECOVERY_REQUIRED
import dev.russiancompanion.v2.backup.V2BackupFaults
import dev.russiancompanion.v2.backup.readGovernedBackup
import dev.russiancompanion.v2.backup.restorePreview
import dev.russiancompanion.v2.backup.restoreStoreBackup
import dev.russiancompanion.v2.backup.writeGovernedBackup
import dev.russiancompanion.v2.backup.writeStoreBackup
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.notices.PENGUIN_NOT_REDISTRIBUTED
import dev.russiancompanion.v2.notices.offlineNoticeCatalog
import dev.russiancompanion.v2.notices.packagedNoticeErrors
import dev.russiancompanion.v2.ui.V2App
import dev.russiancompanion.v2.ui.V2BackupDocuments
import dev.russiancompanion.v2.ui.V2BackupPhase
import dev.russiancompanion.v2.ui.V2BackupPick
import dev.russiancompanion.v2.ui.V2UiWork
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], qualifiers = "w411dp-h914dp-xxhdpi")
class BackupLicenseTest {
    @get:Rule
    val compose = createComposeRule()

    private val stores = mutableListOf<LocalStore>()
    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @After
    fun tearDown() {
        V2BackupFaults.reset()
        V2BackupDocuments.pickForTests = null
        V2UiWork.blockForTests = null
        stores.forEach { it.close() }
    }

    @Test
    fun backupRoundTripRestoresGovernedRecordsWithoutChangingTheLiveCopy() {
        val store = memoryStore()
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 1, currentSessionId = "u1-s-orient", currentStepIndex = 1))
        store.v2CompleteStep("u1-st-orient", "2026-10-03T10:00:00")
        store.v2RecordAttempt("item-a", "right", true, "2026-10-03T10:01:00")
        store.v2AddNote("u1-st-orient", "private note", "2026-10-03T10:02:00")
        store.v2ToggleBookmark("ref-1", "REFERENCE", "2026-10-03T10:03:00")
        store.v2AddTutorItem("u1-st-orient", "ask later", "2026-10-03T10:04:00")
        store.v2AttestBook(1, "2026-10-03T10:05:00")
        store.v2CompleteUnit(1, "2026-10-03T10:06:00")
        val before = store.governedLearnerFingerprint()
        val file = tempFile()
        file.outputStream().use { writeStoreBackup(store, it, APP, pack().packId, pack().packVersion, "DARK") }
        assertEquals(before, store.governedLearnerFingerprint())
        assertEquals(0, store.exports().size)
        store.v2AddNote("u1-st-orient", "later note", "2026-10-03T11:00:00")
        val read = readGovernedBackup(file.inputStream(), APP, pack().packId, pack().packVersion, tempDir())
        val validated = (read as BackupReadResult.Valid).backup
        assertEquals(GovernedReplaceOutcome.REPLACED, restoreStoreBackup(store, validated, tempDir()))
        assertEquals(before, store.governedLearnerFingerprint())
        assertEquals(1, store.v2Attempts().size)
        assertEquals(1, store.v2Notes().size)
        assertEquals("private note", store.v2Notes().single().text)
        assertEquals(1, store.v2Bookmarks().size)
        assertEquals(1, store.v2TutorQueue().size)
        assertEquals(1, store.v2Profile().currentStepIndex)
        assertTrue(1 in store.v2LearnerView().completedUnitNumbers)
        assertTrue(store.exports().isEmpty())
        val again = readGovernedBackup(file.inputStream(), APP, pack().packId, pack().packVersion, tempDir())
        assertEquals(GovernedReplaceOutcome.REPLACED, restoreStoreBackup(store, (again as BackupReadResult.Valid).backup, tempDir()))
        assertEquals(1, store.v2Attempts().size)
        assertEquals(1, store.v2Notes().size)
        val preview = restorePreview(store.governedLearnerDocument(), validated.document)
        assertEquals(0, preview.additions)
        assertEquals(0, preview.omissions)
        assertEquals(0, preview.conflicts)
    }

    @Test
    fun recordingBytesRoundTripAndBackupDoesNotClaimAFailedWrite() {
        val store = seededStore()
        store.commitRecording("audio-1", "recordings/one.m4a", "2026-10-03T09:00:00", true, CompletionState.NOT_COMPLETED)
        val recording = store.recordings().single()
        val recordingRoot = checkNotNull(store.recordingDirectory())
        recordingRoot.mkdirs()
        val media = File(recordingRoot, File(recording.localFileReference).name)
        media.writeBytes(byteArrayOf(1, 2, 3, 4))
        check(store.recordingFile(recording.localFileReference) != null) {
            "missing ${recording.localFileReference} at ${media.absolutePath} exists=${media.exists()}"
        }
        val before = store.governedLearnerFingerprint()
        val file = tempFile()
        file.outputStream().use { writeStoreBackup(store, it, APP, pack().packId, pack().packVersion, "FOLLOW_SYSTEM") }
        assertEquals(before, store.governedLearnerFingerprint())
        store.v2AddNote("step", "changed", "2026-10-03T12:00:00")
        val validated = (readGovernedBackup(file.inputStream(), APP, pack().packId, pack().packVersion, tempDir()) as BackupReadResult.Valid).backup
        assertEquals(GovernedReplaceOutcome.REPLACED, restoreStoreBackup(store, validated, tempDir()))
        val restored = store.recordings().single()
        assertEquals(byteArrayOf(1, 2, 3, 4).toList(), store.recordingFile(restored.localFileReference)!!.readBytes().toList())
        assertTrue(store.v2Notes().isEmpty())
        ZipFile(file).use { zip ->
            val names = zip.entries().toList().map { it.name }
            assertTrue(names.none { it.contains("curriculum") || it.contains("mapping") })
            val learner = zip.getInputStream(zip.getEntry("learner.json")).bufferedReader().use { it.readText() }
            assertFalse(learner.contains("provenanceRecords"))
            assertFalse(learner.contains("Penguin prose"))
        }
        V2BackupFaults.failWrite = true
        val failed = tempFile()
        assertTrue(runCatching { failed.outputStream().use { writeStoreBackup(store, it, APP, pack().packId, pack().packVersion, "DARK") } }.isFailure)
        assertEquals(store.governedLearnerFingerprint(), store.governedLearnerFingerprint())
        assertTrue(store.exports().isEmpty())
    }

    @Test
    fun malformedTruncatedUnsupportedAndTamperedPackagesDoNotChangeData() {
        val store = memoryStore()
        store.v2AddNote("step", "keep me", "2026-10-03T10:00:00")
        val before = store.governedLearnerFingerprint()
        val valid = backupFile(store)
        assertEquals(BackupRejection.MALFORMED, rejection(store, "not a backup".toByteArray()))
        val truncated = valid.readBytes().copyOf(valid.length().toInt() / 2)
        assertEquals(BackupRejection.TRUNCATED, rejection(store, truncated))
        val unsupported = rewriteManifest(valid) { it.replace(Regex("\"formatVersion\"\\s*:\\s*1"), "\"formatVersion\":99") }
        assertEquals(BackupRejection.UNSUPPORTED_VERSION, rejection(store, unsupported))
        val tampered = rewriteEntry(valid, "learner.json") { bytes ->
            val text = bytes.toString(Charsets.UTF_8).replace("keep me", "tampered")
            text.encodeToByteArray()
        }
        assertEquals(BackupRejection.INTEGRITY, rejection(store, tampered))
        val incomplete = rewriteManifest(valid) { it.replace(Regex("\"noteCount\"\\s*:\\s*1"), "\"noteCount\":0") }
        assertEquals(BackupRejection.INCOMPLETE, rejection(store, incomplete))
        assertEquals(BackupRejection.UNAUTHORIZED, (readGovernedBackup(valid.inputStream(), "other.app", pack().packId, pack().packVersion, tempDir()) as BackupReadResult.Rejected).reason)
        val imported = addEntry(valid, "v2-curriculum.json", "{}".encodeToByteArray())
        assertEquals(BackupRejection.UNAUTHORIZED, rejection(store, imported))
        assertEquals(before, store.governedLearnerFingerprint())
        assertEquals("keep me", store.v2Notes().single().text)
    }

    @Test
    fun failedReplacementKeepsTheOriginalDatabase() {
        val store = memoryStore()
        store.v2AddNote("step", "original", "2026-10-03T10:00:00")
        val other = memoryStore()
        other.v2AddNote("step", "incoming", "2026-10-03T11:00:00")
        val file = backupFile(other)
        val validated = (readGovernedBackup(file.inputStream(), APP, pack().packId, pack().packVersion, tempDir()) as BackupReadResult.Valid).backup
        V2BackupFaults.failBeforeMutation = true
        assertEquals(GovernedReplaceOutcome.UNCHANGED, restoreStoreBackup(store, validated, tempDir()))
        assertEquals("original", store.v2Notes().single().text)
        V2BackupFaults.reset()
        V2BackupFaults.failInsideReplacement = true
        assertEquals(GovernedReplaceOutcome.UNCHANGED, restoreStoreBackup(store, validated, tempDir()))
        assertEquals("original", store.v2Notes().single().text)
        V2BackupFaults.reset()
        V2BackupFaults.failAfterCommit = true
        assertEquals(GovernedReplaceOutcome.UNCHANGED, restoreStoreBackup(store, validated, tempDir()))
        assertEquals("original", store.v2Notes().single().text)
        assertTrue(store.exports().isEmpty())
    }

    @Test
    fun failedRecordingPublishRestoresDatabaseRowsAndRecordingBytes() {
        val store = recordingStore()
        val original = seedTwoRecordings(store, byteArrayOf(1, 2, 3), byteArrayOf(4, 5, 6))
        store.v2AddNote("step", "original", "2026-10-03T10:00:00")
        store.saveExport(
            ExportRecordEntity(
                id = "export-1",
                exportedAtLocal = "2026-10-03T10:05:00",
                destinationReference = "local-backup",
                logCount = 0,
                evidenceCount = 0,
                progressCount = 0,
                recordingCount = 2,
                status = ExportStatus.FAILED,
                documentReference = "backup-format-1",
            ),
        )
        val backup = backupFile(store)
        val validated = (readGovernedBackup(backup.inputStream(), APP, pack().packId, pack().packVersion, tempDir()) as BackupReadResult.Valid).backup
        writeRecordingBytes(store, original, byteArrayOf(7, 7, 7), byteArrayOf(8, 8, 8))
        store.v2AddNote("step", "changed", "2026-10-03T11:00:00")
        val before = store.governedLearnerFingerprint()
        V2BackupFaults.failDuringFilePublish = true
        assertEquals(GovernedReplaceOutcome.UNCHANGED, restoreStoreBackup(store, validated, tempDir()))
        assertEquals(before, store.governedLearnerFingerprint())
        assertEquals(listOf("export-1"), store.exports().map { it.id })
        assertBytes(store, original, byteArrayOf(7, 7, 7), byteArrayOf(8, 8, 8))
        assertNoSwapArtifacts(store)
        V2BackupFaults.reset()
        V2BackupFaults.failAfterCommit = true
        assertEquals(GovernedReplaceOutcome.UNCHANGED, restoreStoreBackup(store, validated, tempDir()))
        assertEquals(before, store.governedLearnerFingerprint())
        assertEquals(listOf("export-1"), store.exports().map { it.id })
        assertBytes(store, original, byteArrayOf(7, 7, 7), byteArrayOf(8, 8, 8))
        assertNoSwapArtifacts(store)
        V2BackupFaults.reset()
        V2BackupFaults.crashAfterFileSwapBeforeCommit = true
        assertEquals(GovernedReplaceOutcome.RECOVERY_REQUIRED, restoreStoreBackup(store, validated, tempDir()))
        store.sweepOrphanRecordingFiles()
        assertEquals(before, store.governedLearnerFingerprint())
        assertEquals(listOf("export-1"), store.exports().map { it.id })
        assertBytes(store, original, byteArrayOf(7, 7, 7), byteArrayOf(8, 8, 8))
        assertNoSwapArtifacts(store)
        V2BackupFaults.reset()
        assertEquals(GovernedReplaceOutcome.REPLACED, restoreStoreBackup(store, validated, tempDir()))
        assertEquals(setOf("original"), store.v2Notes().map { it.text }.toSet())
        assertTrue(store.exports().isEmpty())
        assertBytes(store, store.recordings(), byteArrayOf(1, 2, 3), byteArrayOf(4, 5, 6))
        assertNoSwapArtifacts(store)
    }

    @Test
    fun failedJournalWriteRestoresTheOriginalRecordingSnapshot() {
        val store = recordingStore()
        val original = seedTwoRecordings(store, byteArrayOf(1, 2, 3), byteArrayOf(4, 5, 6))
        store.v2AddNote("step", "original", "2026-10-03T10:00:00")
        store.saveExport(exportRecord())
        val backup = backupFile(store)
        val validated = (readGovernedBackup(backup.inputStream(), APP, pack().packId, pack().packVersion, tempDir()) as BackupReadResult.Valid).backup
        writeRecordingBytes(store, original, byteArrayOf(7, 7, 7), byteArrayOf(8, 8, 8))
        store.v2AddNote("step", "changed", "2026-10-03T11:00:00")
        val before = store.governedLearnerFingerprint()
        V2BackupFaults.failJournalWrite = true
        assertEquals(GovernedReplaceOutcome.UNCHANGED, restoreStoreBackup(store, validated, tempDir()))
        assertEquals(before, store.governedLearnerFingerprint())
        assertEquals(listOf("export-1"), store.exports().map { it.id })
        assertEquals(setOf("original", "changed"), store.v2Notes().map { it.text }.toSet())
        assertBytes(store, original, byteArrayOf(7, 7, 7), byteArrayOf(8, 8, 8))
        assertNoSwapArtifacts(store)
    }

    @Test
    fun failedSnapshotRestoreKeepsRecoveryMaterialsUntilTheNextStoreOpen() {
        val name = "backup-snapshot-${System.nanoTime()}"
        val store = recordingStore(name)
        val original = seedTwoRecordings(store, byteArrayOf(1, 2, 3), byteArrayOf(4, 5, 6))
        store.v2AddNote("step", "original", "2026-10-03T10:00:00")
        store.saveExport(exportRecord())
        val backup = backupFile(store)
        val validated = (readGovernedBackup(backup.inputStream(), APP, pack().packId, pack().packVersion, tempDir()) as BackupReadResult.Valid).backup
        writeRecordingBytes(store, original, byteArrayOf(7, 7, 7), byteArrayOf(8, 8, 8))
        store.v2AddNote("step", "changed", "2026-10-03T11:00:00")
        val before = store.governedLearnerFingerprint()
        V2BackupFaults.failAfterCommit = true
        V2BackupFaults.failSnapshotRestore = true
        assertEquals(GovernedReplaceOutcome.RECOVERY_REQUIRED, restoreStoreBackup(store, validated, tempDir()))
        assertEquals(before, store.governedLearnerFingerprint())
        assertEquals(listOf("export-1"), store.exports().map { it.id })
        assertTrue(journalFile(store).isFile)
        assertTrue(snapshotDir(store).isDirectory)
        assertTrue(snapshotDir(store).listFiles().orEmpty().isNotEmpty())
        assertBytes(store, original, byteArrayOf(1, 2, 3), byteArrayOf(4, 5, 6))
        assertFalse(RESTORE_RECOVERY_REQUIRED.contains("unchanged"))
        V2BackupFaults.reset()
        store.close()
        stores.remove(store)
        val reopened = LocalStore.openNamed(context, name).also { stores += it }
        assertEquals(before, reopened.governedLearnerFingerprint())
        assertEquals(listOf("export-1"), reopened.exports().map { it.id })
        assertEquals(setOf("original", "changed"), reopened.v2Notes().map { it.text }.toSet())
        assertBytes(reopened, reopened.recordings(), byteArrayOf(7, 7, 7), byteArrayOf(8, 8, 8))
        assertNoSwapArtifacts(reopened)
    }

    @Test
    fun failedJournalWriteKeepsTheSnapshotWhenImmediateRollbackCannotFinish() {
        val name = "backup-journal-recover-${System.nanoTime()}"
        val store = recordingStore(name)
        val original = seedTwoRecordings(store, byteArrayOf(1, 2, 3), byteArrayOf(4, 5, 6))
        store.v2AddNote("step", "original", "2026-10-03T10:00:00")
        store.saveExport(exportRecord())
        val backup = backupFile(store)
        val validated = (readGovernedBackup(backup.inputStream(), APP, pack().packId, pack().packVersion, tempDir()) as BackupReadResult.Valid).backup
        writeRecordingBytes(store, original, byteArrayOf(7, 7, 7), byteArrayOf(8, 8, 8))
        store.v2AddNote("step", "changed", "2026-10-03T11:00:00")
        val before = store.governedLearnerFingerprint()
        V2BackupFaults.failJournalWrite = true
        V2BackupFaults.failSnapshotRestore = true
        assertEquals(GovernedReplaceOutcome.RECOVERY_REQUIRED, restoreStoreBackup(store, validated, tempDir()))
        assertEquals(before, store.governedLearnerFingerprint())
        assertEquals(listOf("export-1"), store.exports().map { it.id })
        assertFalse(journalFile(store).exists())
        assertTrue(snapshotDir(store).isDirectory)
        assertTrue(File(snapshotDir(store), File(original[0].localFileReference).name).isFile)
        assertBytes(store, original, byteArrayOf(1, 2, 3), byteArrayOf(4, 5, 6))
        V2BackupFaults.reset()
        store.close()
        stores.remove(store)
        val reopened = LocalStore.openNamed(context, name).also { stores += it }
        assertEquals(before, reopened.governedLearnerFingerprint())
        assertEquals(listOf("export-1"), reopened.exports().map { it.id })
        assertBytes(reopened, reopened.recordings(), byteArrayOf(7, 7, 7), byteArrayOf(8, 8, 8))
        assertNoSwapArtifacts(reopened)
    }

    @Test
    fun restoreRejectsARecordingForAnUnknownAudioModel() {
        val store = memoryStore()
        store.v2AddNote("step", "stay", "2026-10-03T10:00:00")
        val document = store.governedLearnerDocument()
        val forged = document.copy(
            recordings = listOf(
                dev.russiancompanion.v2.backup.BackupRecording(
                    id = "rec-1",
                    audioModelId = "not-a-real-model",
                    localFileReference = "recordings/rec-1.m4a",
                    createdAtLocal = "2026-10-03T10:00:00",
                    markedForTutorReview = false,
                    completionState = "NOT_COMPLETED",
                    activityLogId = null,
                ),
            ),
        )
        val audio = tempFile().also { it.writeBytes(byteArrayOf(9)) }
        val zip = tempFile()
        zip.outputStream().use {
            writeGovernedBackup(it, APP, pack().packId, pack().packVersion, "DARK", forged, listOf(forged.recordings.single() to audio))
        }
        val validated = (readGovernedBackup(zip.inputStream(), APP, pack().packId, pack().packVersion, tempDir()) as BackupReadResult.Valid).backup
        assertEquals(GovernedReplaceOutcome.UNCHANGED, restoreStoreBackup(store, validated, tempDir()))
        assertEquals("stay", store.v2Notes().single().text)
        assertTrue(store.recordings().isEmpty())
    }

    @Test
    fun backupScreenRejectsABadFileAndConfirmsReplacementOnce() {
        val store = memoryStore()
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 1))
        store.v2AddNote("step", "screen note", "2026-10-03T10:00:00")
        val backup = backupFile(store)
        store.v2AddNote("step", "device only", "2026-10-03T11:00:00")
        var pick = backup
        V2BackupDocuments.pickForTests = { kind ->
            if (kind == V2BackupPick.OPEN) pick else null
        }
        compose.setContent {
            V2App(
                store = store,
                pack = pack(),
                onRetryOpen = {},
                appearanceStore = AppearanceStore.memory(AppearancePreference.DARK),
                recordedAt = { "2026-10-03T12:00:00" },
            )
        }
        compose.onNodeWithText("Progress").performClick()
        compose.onNodeWithTag("v2-open-backup").performScrollTo().performClick()
        pick = tempFile().also { it.writeText("malformed") }
        compose.onNodeWithTag("v2-backup-restore").performClick()
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodes(androidx.compose.ui.test.hasText(RESTORE_FAILED)).fetchSemanticsNodes().isNotEmpty()
        }
        assertEquals(2, store.v2Notes().size)
        pick = backup
        compose.onNodeWithTag("v2-backup-restore").performClick()
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodes(androidx.compose.ui.test.hasText("Replace learner data")).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithTag("v2-backup-confirm").performClick()
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodes(androidx.compose.ui.test.hasText(RESTORE_DONE)).fetchSemanticsNodes().isNotEmpty()
        }
        assertEquals(listOf("screen note"), store.v2Notes().map { it.text })
        assertFalse(RESTORE_FAILED.contains("screen note"))
        assertFalse(BACKUP_FAILED.contains("screen note"))
    }

    @Test
    fun cancelledBackupAndDeclinedRestoreLeaveLiveDataUnchanged() {
        val store = memoryStore()
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true))
        store.v2AddNote("step", "stay", "2026-10-03T10:00:00")
        val backup = backupFile(store)
        store.v2AddNote("step", "only on device", "2026-10-03T11:00:00")
        val before = store.governedLearnerFingerprint()
        V2BackupDocuments.pickForTests = { null }
        compose.setContent {
            V2App(
                store = store,
                pack = pack(),
                onRetryOpen = {},
                appearanceStore = AppearanceStore.memory(AppearancePreference.DARK),
            )
        }
        compose.onNodeWithText("Progress").performClick()
        compose.onNodeWithTag("v2-open-backup").performScrollTo().performClick()
        compose.onNodeWithTag("v2-backup-create").performScrollTo().performClick()
        compose.onNodeWithText(BACKUP_CANCELLED).assertIsDisplayed()
        assertEquals(before, store.governedLearnerFingerprint())
        assertTrue(store.exports().isEmpty())
        V2BackupDocuments.pickForTests = { choice -> if (choice == V2BackupPick.OPEN) backup else null }
        compose.onNodeWithTag("v2-backup-restore").performScrollTo().performClick()
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodes(androidx.compose.ui.test.hasText("Replace learner data")).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithTag("v2-backup-cancel").performScrollTo().performClick()
        compose.onNodeWithText(RESTORE_CANCELLED).assertIsDisplayed()
        assertEquals(setOf("stay", "only on device"), store.v2Notes().map { it.text }.toSet())
        assertEquals(before, store.governedLearnerFingerprint())
        V2BackupFaults.failWrite = true
        val failed = tempFile()
        V2BackupDocuments.pickForTests = { failed }
        compose.onNodeWithTag("v2-backup-create").performScrollTo().performClick()
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodes(androidx.compose.ui.test.hasText(BACKUP_FAILED)).fetchSemanticsNodes().isNotEmpty()
        }
        assertEquals(before, store.governedLearnerFingerprint())
        assertTrue(store.exports().none { it.status == ExportStatus.COMPLETED })
    }

    @Test
    fun repeatedBackupTapsDoNotStartASecondOperation() {
        val store = memoryStore()
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true))
        val activity = Robolectric.buildActivity(androidx.activity.ComponentActivity::class.java).setup().get()
        val coordinator = dev.russiancompanion.v2.ui.V2BackupCoordinator.get(
            activity,
            store,
            APP,
            pack().packId,
            pack().packVersion,
            AppearanceStore.memory(AppearancePreference.DARK),
        )
        assertTrue(coordinator.begin(V2BackupPhase.PREPARING))
        assertFalse(coordinator.begin(V2BackupPhase.PREPARING))
        coordinator.cancel()
    }

    @Test
    fun missingAttributionAndMissingNoticeFailClosed() {
        val live = pack()
        val blank = live.copy(sources = live.sources.mapIndexed { index, source ->
            if (index == 0) source.copy(attribution = "") else source
        })
        assertTrue(V2Validator.validate(blank).errors.any { it.contains("has no attribution") })
        val root = tempDir()
        assertTrue(packagedNoticeErrors(root, live).any { it.contains("not packaged") })
        assertFalse(V2Validator.validate(live, root).ok)
        val catalog = offlineNoticeCatalog(live, packagedFiles())
        assertEquals(live.sources.map { it.id }, catalog.sources.map { it.id })
        assertEquals(PENGUIN_NOT_REDISTRIBUTED, catalog.penguinStatement)
        assertTrue(catalog.sources.all {
            it.availability.contains("not a release installation") || it.availability.contains("Not referenced")
        })
        assertTrue(catalog.licenses.any { it.body.contains("Creative Commons Corporation") })
        assertTrue(catalog.licenses.any { it.availability.contains("Not installed as course audio") })
        assertTrue(catalog.sources.any { it.attribution.contains("Badestrand/russian-dictionary contributors") })
    }

    @Test
    fun attributionsScreenShowsPackagedNoticeTextAndReturns() {
        val store = memoryStore()
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 1))
        compose.setContent {
            V2App(
                store = store,
                pack = pack(),
                onRetryOpen = {},
                appearanceStore = AppearanceStore.memory(AppearancePreference.DARK),
            )
        }
        compose.onNodeWithText("Progress").performClick()
        compose.onNodeWithText("Settings, appearance, and attributions").performScrollTo().performClick()
        compose.onNodeWithText("Attributions and licenses").performScrollTo().performClick()
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodes(androidx.compose.ui.test.hasText(PENGUIN_NOT_REDISTRIBUTED)).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithTag("v2-content").performScrollToNode(androidx.compose.ui.test.hasText("Badestrand/russian-dictionary contributors", substring = true))
        compose.onNodeWithText("Badestrand/russian-dictionary contributors", substring = true).assertIsDisplayed()
        compose.onNodeWithTag("v2-content").performScrollToNode(androidx.compose.ui.test.hasText("Creative Commons Corporation", substring = true))
        compose.onNodeWithText("Creative Commons Corporation", substring = true).assertIsDisplayed()
        compose.onNodeWithTag("v2-back").performClick()
        compose.onNodeWithTag("v2-settings-heading").assertIsDisplayed()
    }

    private fun memoryStore(): LocalStore = LocalStore.openInMemory(context).also { stores += it }

    private fun exportRecord(): ExportRecordEntity {
        return ExportRecordEntity(
            id = "export-1",
            exportedAtLocal = "2026-10-03T10:05:00",
            destinationReference = "local-backup",
            logCount = 0,
            evidenceCount = 0,
            progressCount = 0,
            recordingCount = 2,
            status = ExportStatus.FAILED,
            documentReference = "backup-format-1",
        )
    }

    private fun journalFile(store: LocalStore): File {
        val root = checkNotNull(store.recordingDirectory())
        return File(checkNotNull(root.parentFile), "backup-replace-${root.name}.json")
    }

    private fun snapshotDir(store: LocalStore): File {
        val root = checkNotNull(store.recordingDirectory())
        return File(checkNotNull(root.parentFile), "backup-replace-${root.name}-workspace/snapshot")
    }

    private fun recordingStore(name: String = "backup-publish-${System.nanoTime()}"): LocalStore {
        val store = LocalStore.openNamed(context, name).also { stores += it }
        store.applySeed(
            SeedJson.encode(
                minimalDocument().copy(
                    sourceTexts = listOf(text(matchVerified = true), text(matchVerified = true, id = "text-2")),
                    assetRightsRecords = listOf(rights(), rights(id = "rights-2", assetIdentifier = "audio-2")),
                    audioModels = listOf(audio(), audio(id = "audio-2", rightsRecordId = "rights-2", sourceTextId = "text-2")),
                ),
            ),
        )
        return store
    }

    private fun seedTwoRecordings(
        store: LocalStore,
        first: ByteArray,
        second: ByteArray,
    ): List<dev.russiancompanion.data.local.RecordingRecordEntity> {
        val one = store.commitRecording("audio-1", "recordings/one.m4a", "2026-10-03T09:00:00", false, CompletionState.NOT_COMPLETED)
        val two = store.commitRecording("audio-2", "recordings/two.m4a", "2026-10-03T09:01:00", false, CompletionState.NOT_COMPLETED)
        val records = listOf(one, two).sortedBy { it.audioModelId }
        writeRecordingBytes(store, records, first, second)
        return records
    }

    private fun writeRecordingBytes(
        store: LocalStore,
        records: List<dev.russiancompanion.data.local.RecordingRecordEntity>,
        first: ByteArray,
        second: ByteArray,
    ) {
        val root = checkNotNull(store.recordingDirectory())
        root.mkdirs()
        val ordered = records.sortedBy { it.audioModelId }
        File(root, File(ordered[0].localFileReference).name).writeBytes(first)
        File(root, File(ordered[1].localFileReference).name).writeBytes(second)
    }

    private fun assertBytes(
        store: LocalStore,
        records: List<dev.russiancompanion.data.local.RecordingRecordEntity>,
        first: ByteArray,
        second: ByteArray,
    ) {
        val root = checkNotNull(store.recordingDirectory())
        val ordered = records.sortedBy { it.audioModelId }
        assertEquals(first.toList(), File(root, File(ordered[0].localFileReference).name).readBytes().toList())
        assertEquals(second.toList(), File(root, File(ordered[1].localFileReference).name).readBytes().toList())
    }

    private fun assertNoSwapArtifacts(store: LocalStore) {
        val root = checkNotNull(store.recordingDirectory())
        assertTrue(root.listFiles().orEmpty().none { it.name.endsWith(".incoming") || it.name.endsWith(".retired") })
        val parent = checkNotNull(root.parentFile)
        assertFalse(File(parent, "backup-replace-${root.name}.json").exists())
        assertFalse(File(parent, "backup-replace-${root.name}-workspace").exists())
    }

    private fun seededStore(): LocalStore {
        val store = LocalStore.openNamed(context, "backup-recordings").also { stores += it }
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

    private fun backupFile(store: LocalStore): File {
        val file = tempFile()
        file.outputStream().use { writeStoreBackup(store, it, APP, pack().packId, pack().packVersion, "DARK") }
        return file
    }

    private fun rejection(store: LocalStore, bytes: ByteArray): BackupRejection {
        val file = tempFile().also { it.writeBytes(bytes) }
        val result = readGovernedBackup(file.inputStream(), APP, pack().packId, pack().packVersion, tempDir())
        assertTrue(result is BackupReadResult.Rejected)
        assertEquals(store.governedLearnerFingerprint(), store.governedLearnerFingerprint())
        return (result as BackupReadResult.Rejected).reason
    }

    private fun rewriteManifest(source: File, edit: (String) -> String): ByteArray {
        return rewriteEntry(source, "manifest.json") { edit(it.toString(Charsets.UTF_8)).encodeToByteArray() }
    }

    private fun rewriteEntry(source: File, name: String, edit: (ByteArray) -> ByteArray): ByteArray {
        val buffer = java.io.ByteArrayOutputStream()
        ZipFile(source).use { zip ->
            ZipOutputStream(buffer).use { output ->
                zip.entries().toList().forEach { entry ->
                    val bytes = zip.getInputStream(entry).use { it.readBytes() }
                    output.putNextEntry(ZipEntry(entry.name))
                    output.write(if (entry.name == name) edit(bytes) else bytes)
                    output.closeEntry()
                }
            }
        }
        return buffer.toByteArray()
    }

    private fun addEntry(source: File, name: String, bytes: ByteArray): ByteArray {
        val buffer = java.io.ByteArrayOutputStream()
        ZipFile(source).use { zip ->
            ZipOutputStream(buffer).use { output ->
                zip.entries().toList().forEach { entry ->
                    output.putNextEntry(ZipEntry(entry.name))
                    zip.getInputStream(entry).use { it.copyTo(output) }
                    output.closeEntry()
                }
                output.putNextEntry(ZipEntry(name))
                output.write(bytes)
                output.closeEntry()
            }
        }
        return buffer.toByteArray()
    }

    private fun packagedFiles(): List<dev.russiancompanion.v2.notices.PackagedNoticeFile> {
        val root = repo()
        val files = File(root, "app/src/main/assets/licenses").listFiles().orEmpty().sortedBy { it.name }.map {
            dev.russiancompanion.v2.notices.PackagedNoticeFile(it.name, it.readText())
        }
        val catalog = File(root, "app/src/main/assets/content/catalog-notice.txt")
        return files + dev.russiancompanion.v2.notices.PackagedNoticeFile(catalog.name, catalog.readText())
    }

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun repo(): File {
        var dir = File(checkNotNull(System.getProperty("user.dir")))
        while (!File(dir, "settings.gradle.kts").isFile) dir = checkNotNull(dir.parentFile)
        return dir
    }

    private fun tempFile(): File = File.createTempFile("backup", ".zip").apply { deleteOnExit() }

    private fun tempDir(): File = File.createTempFile("backup-dir", "").apply {
        delete()
        mkdirs()
        deleteOnExit()
    }

    companion object {
        private const val APP = "dev.russiancompanion"
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], qualifiers = "w411dp-h914dp-xxhdpi", fontScale = 2f)
class BackupRecreationTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @After
    fun tearDown() {
        V2BackupDocuments.pickForTests = null
        V2BackupFaults.reset()
    }

    @Test
    fun recreatedActivityKeepsOneRestoreOutcome() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val app = context as dev.russiancompanion.RussianCompanionApplication
        compose.waitUntil(timeoutMillis = 20_000) { app.localStore != null }
        val store = checkNotNull(app.localStore)
        store.v2SaveProfile(
            store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 1, currentSessionId = null, currentStepIndex = 0),
        )
        store.v2AddNote("step", "kept across recreation", "2026-10-03T10:00:00")
        val backup = File.createTempFile("recreate", ".zip")
        backup.outputStream().use {
            writeStoreBackup(store, it, "dev.russiancompanion", packId(store), packVersion(), "DARK")
        }
        store.v2AddNote("step", "remove me", "2026-10-03T11:00:00")
        V2BackupDocuments.pickForTests = { backup }
        compose.activityRule.scenario.recreate()
        compose.waitUntil(timeoutMillis = 20_000) {
            compose.onAllNodes(androidx.compose.ui.test.hasText("Progress")).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText("Progress").performClick()
        compose.onNodeWithTag("v2-open-backup").performScrollTo().performClick()
        compose.onNodeWithTag("v2-backup-restore").performClick()
        compose.waitUntil(timeoutMillis = 20_000) {
            compose.onAllNodes(androidx.compose.ui.test.hasText("Replace learner data")).fetchSemanticsNodes().isNotEmpty()
        }
        compose.activityRule.scenario.recreate()
        compose.waitUntil(timeoutMillis = 20_000) {
            compose.onAllNodes(androidx.compose.ui.test.hasTestTag("v2-backup-confirm")).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithTag("v2-backup-confirm").performScrollTo().performClick()
        compose.waitUntil(timeoutMillis = 20_000) {
            compose.onAllNodes(androidx.compose.ui.test.hasText(RESTORE_DONE)).fetchSemanticsNodes().isNotEmpty()
        }
        assertEquals(listOf("kept across recreation"), store.v2Notes().map { it.text })
    }

    private fun packId(store: LocalStore): String {
        return V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText()).packId
    }

    private fun packVersion(): Int {
        return V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText()).packVersion
    }

    private fun repo(): File {
        var dir = File(checkNotNull(System.getProperty("user.dir")))
        while (!File(dir, "settings.gradle.kts").isFile) dir = checkNotNull(dir.parentFile)
        return dir
    }
}
