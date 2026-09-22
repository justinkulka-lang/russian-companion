package dev.russiancompanion.domain.progress

import dev.russiancompanion.domain.plan.ActivityLogView
import dev.russiancompanion.domain.plan.RecordedCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class BackupAndBalanceTest {
    @Test
    fun backupStatusUsesRecordingSetDigestNotCounts() {
        val first = member("rec-1", "recordings/a.m4a", 10, "aaa")
        val replaced = member("rec-1", "recordings/b.m4a", 10, "bbb")
        val added = member("rec-2", "recordings/c.m4a", 8, "ccc")
        val missing = member("rec-1", "recordings/a.m4a", null, null)
        val originalDigest = recordingSetDigest(listOf(first))
        val completed = ExportRecordView(
            exportedAtLocal = "2026-09-22T10:00",
            fileName = "export.zip",
            logCount = 2,
            evidenceCount = 1,
            progressCount = 3,
            recordingCount = 1,
            outcome = BackupOutcome.COMPLETED,
            recordingSetDigest = originalDigest,
        )
        assertEquals(BackupOutcome.NEVER_EXPORTED, backupStatus(emptyList(), originalDigest).outcome)
        assertFalse(backupStatus(emptyList(), originalDigest).claimsProtection)
        val done = backupStatus(listOf(completed), originalDigest)
        assertEquals(BackupOutcome.COMPLETED, done.outcome)
        assertTrue(done.claimsProtection)
        assertEquals("export.zip", done.lastSuccessfulFileName)
        assertEquals(true, done.expectedRecordingsIncluded)

        val sameCountReplacement = backupStatus(listOf(completed), recordingSetDigest(listOf(replaced)))
        assertEquals(false, sameCountReplacement.expectedRecordingsIncluded)
        assertTrue(sameCountReplacement.claimsProtection)

        val addedRecording = backupStatus(listOf(completed), recordingSetDigest(listOf(first, added)))
        assertEquals(false, addedRecording.expectedRecordingsIncluded)

        val missingFile = backupStatus(listOf(completed), recordingSetDigest(listOf(missing)))
        assertEquals(false, missingFile.expectedRecordingsIncluded)

        val failed = backupStatus(
            listOf(completed, completed.copy(exportedAtLocal = "2026-09-22T12:00", outcome = BackupOutcome.FAILED)),
            originalDigest,
        )
        assertEquals(BackupOutcome.FAILED, failed.outcome)
        assertTrue(failed.successfulExportExists)
        assertFalse(failed.claimsProtection)
        assertNull(failed.expectedRecordingsIncluded)

        val countOnlyLegacy = backupStatus(
            listOf(completed.copy(recordingSetDigest = null)),
            originalDigest,
        )
        assertEquals(false, countOnlyLegacy.expectedRecordingsIncluded)
    }

    @Test
    fun storageHealthLeavesUnreadablesUnguessed() {
        val health = StorageHealth.unavailable()
        assertNull(health.databaseReadable)
        assertNull(health.packagedAudioCount)
        assertEquals("contentVersion=2;contentHash=abc", bundledContentIdentity(2, "abc"))
        assertNull(bundledContentIdentity(null, null))
        val (bytes, missing) = recordingStorage(listOf(10L, null, 5L))
        assertEquals(15L, bytes)
        assertEquals(1, missing)
    }

    @Test
    fun studyBalanceWindowsHonorZeroMinutesAndHideNegativeDurations() {
        val today = LocalDate.parse("2026-09-22")
        val logs = listOf(
            ActivityLogView("1", RecordedCategory.GRAMMAR, 0, true, "2026-09-22T09:00"),
            ActivityLogView("2", RecordedCategory.LISTENING, 12, true, "2026-09-10T09:00"),
        )
        val seven = studyBalance(logs, today, 7)
        assertTrue(seven.readable)
        val grammar = seven.categories.first { it.category == RecordedCategory.GRAMMAR }
        assertEquals(1, grammar.completionCount)
        assertEquals(0L, grammar.recordedMinutes)
        assertTrue(categoryHasRecordedActivity(grammar))
        assertTrue(RecordedCategory.LISTENING in seven.unrecordedCategories)
        val thirty = studyBalance(logs, today, 30)
        assertTrue(RecordedCategory.LISTENING in thirty.recordedCategories)
        val corrupt = studyBalance(
            listOf(ActivityLogView("3", RecordedCategory.WRITING, -1, false, "2026-09-22T09:00")),
            today,
            7,
        )
        assertFalse(corrupt.readable)
        assertTrue(corrupt.categories.isEmpty())
    }

    private fun member(
        id: String,
        reference: String,
        bytes: Long?,
        digest: String?,
    ): RecordingSetMember {
        return RecordingSetMember(id, reference, bytes, digest)
    }
}
