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
    fun backupStatusCoversNeverCompletedFailedAndIncompleteRecordings() {
        assertEquals(BackupOutcome.NEVER_EXPORTED, backupStatus(emptyList(), 1).outcome)
        assertFalse(backupStatus(emptyList(), 1).claimsProtection)
        val completed = ExportRecordView(
            exportedAtLocal = "2026-09-22T10:00",
            fileName = "export.zip",
            logCount = 2,
            evidenceCount = 1,
            progressCount = 3,
            recordingCount = 1,
            outcome = BackupOutcome.COMPLETED,
        )
        val done = backupStatus(listOf(completed), expectedRecordingCount = 1)
        assertEquals(BackupOutcome.COMPLETED, done.outcome)
        assertTrue(done.claimsProtection)
        assertEquals("export.zip", done.lastSuccessfulFileName)
        assertEquals(true, done.expectedRecordingsIncluded)
        val incomplete = backupStatus(
            listOf(completed.copy(recordingCount = 0, exportedAtLocal = "2026-09-22T11:00")),
            expectedRecordingCount = 1,
        )
        assertEquals(false, incomplete.expectedRecordingsIncluded)
        val failed = backupStatus(
            listOf(completed, completed.copy(exportedAtLocal = "2026-09-22T12:00", outcome = BackupOutcome.FAILED)),
            expectedRecordingCount = 1,
        )
        assertEquals(BackupOutcome.FAILED, failed.outcome)
        assertTrue(failed.successfulExportExists)
        assertFalse(failed.claimsProtection)
        val unconfirmed = backupStatus(
            listOf(completed.copy(outcome = BackupOutcome.NOT_CONFIRMED)),
            expectedRecordingCount = 0,
        )
        assertEquals(BackupOutcome.NOT_CONFIRMED, unconfirmed.outcome)
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
}
