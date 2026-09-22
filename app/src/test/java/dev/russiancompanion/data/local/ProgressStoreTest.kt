package dev.russiancompanion.data.local

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.domain.progress.ExportCounts
import dev.russiancompanion.domain.progress.reproducedProgressMatches
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ProgressStoreTest {
    @Test
    fun grammarAndVocabularyLogsRequireSelfAttestationAndUnknownSegmentsAreRejected() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        LocalStore.openInMemory(context).use { store ->
            val reading = store.recordActivity(
                category = ActivityCategory.READING,
                durationMinutes = 15,
                completed = true,
                recordedAtLocal = "2026-09-21T09:00",
                segmentId = null,
                selfAttested = false,
            )
            assertFalse(reading.selfAttested)
            try {
                store.recordActivity(
                    category = ActivityCategory.GRAMMAR,
                    durationMinutes = 10,
                    completed = true,
                    recordedAtLocal = "2026-09-21T09:01",
                    segmentId = null,
                    selfAttested = false,
                )
                error("grammar without attestation was stored")
            } catch (_: IllegalArgumentException) {
                assertEquals(listOf(reading.id), store.activityLogs().map { it.id })
            }
            try {
                store.recordActivity(
                    category = ActivityCategory.VOCABULARY_AND_FORMS,
                    durationMinutes = 10,
                    completed = true,
                    recordedAtLocal = "2026-09-21T09:02",
                    segmentId = null,
                    selfAttested = false,
                )
                error("vocabulary without attestation was stored")
            } catch (_: IllegalArgumentException) {
                assertEquals(listOf(reading.id), store.activityLogs().map { it.id })
            }
            val grammar = store.recordActivity(
                category = ActivityCategory.GRAMMAR,
                durationMinutes = 10,
                completed = true,
                recordedAtLocal = "2026-09-21T09:03",
                segmentId = null,
                selfAttested = true,
            )
            assertTrue(grammar.selfAttested)
            try {
                store.recordActivity(
                    category = ActivityCategory.WRITING,
                    durationMinutes = 5,
                    completed = false,
                    recordedAtLocal = "2026-09-21T09:04",
                    segmentId = "segment-missing",
                    selfAttested = false,
                )
                error("unknown segment was stored")
            } catch (_: IllegalArgumentException) {
                assertEquals(listOf(reading.id, grammar.id), store.activityLogs().map { it.id })
            }
            try {
                store.recordActivity(
                    category = ActivityCategory.WRITING,
                    durationMinutes = 5,
                    completed = false,
                    recordedAtLocal = "",
                    segmentId = null,
                    selfAttested = false,
                )
                error("blank timestamp was stored")
            } catch (_: IllegalArgumentException) {
                assertEquals(listOf(reading.id, grammar.id), store.activityLogs().map { it.id })
            }
        }
    }

    @Test
    fun laterLogsDoNotRewriteACapturedProgressSnapshot() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        LocalStore.openInMemory(context).use { store ->
            val first = store.recordActivity(
                category = ActivityCategory.READING,
                durationMinutes = 15,
                completed = true,
                recordedAtLocal = "2026-09-21T09:00",
                segmentId = null,
                selfAttested = false,
            )
            val snapshot = store.storedProgressSnapshot()
            assertEquals(listOf(first.id), snapshot.activityLogIds)
            store.recordActivity(
                category = ActivityCategory.LISTENING,
                durationMinutes = 40,
                completed = true,
                recordedAtLocal = "2026-09-21T10:00",
                segmentId = null,
                selfAttested = false,
            )
            val reproduced = store.reproduceStoredProgress(snapshot)
            assertTrue(reproducedProgressMatches(snapshot, reproduced))
            assertEquals(15L, reproduced.totalMinutes)
            assertEquals(1, reproduced.totalCompletions)
            assertEquals(0L, reproduced.categories.single { it.category.name == "LISTENING" }.recordedMinutes)
            assertEquals(2, store.activityLogs().size)
            assertEquals(
                55L,
                store.activityTotals().sumOf { it.recordedMinutes },
            )
            val liveIds = store.activityLogs().map { it.id }
            assertTrue(first.id in liveIds)
            assertEquals(2, liveIds.size)
        }
    }

    @Test
    fun saveExportRejectsCompletedCountsThatDoNotMatchABuiltPackage() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        LocalStore.openInMemory(context).use { store ->
            val ready = ExportCounts(logCount = 1, evidenceCount = 0, progressCount = 7, recordingCount = 0)
            try {
                store.saveExport(exportRecord(ExportStatus.COMPLETED, ready.copy(logCount = 1)), packageCounts = null)
                error("completed export without a package was stored")
            } catch (_: IllegalArgumentException) {
                assertTrue(store.exports().isEmpty())
            }
            try {
                store.saveExport(
                    exportRecord(ExportStatus.COMPLETED, ready.copy(recordingCount = 4)),
                    packageCounts = ready,
                )
                error("mismatched completed export was stored")
            } catch (_: IllegalArgumentException) {
                assertTrue(store.exports().isEmpty())
            }
            val failed = store.saveExport(exportRecord(ExportStatus.FAILED, ExportCounts(0, 0, 0, 0)))
            assertEquals(ExportStatus.FAILED, failed.status)
            val interrupted = store.saveExport(exportRecord(ExportStatus.NOT_CONFIRMED, ExportCounts(0, 0, 0, 0)))
            assertEquals(ExportStatus.NOT_CONFIRMED, interrupted.status)
            val completed = store.saveExport(exportRecord(ExportStatus.COMPLETED, ready), packageCounts = ready)
            assertEquals(ExportStatus.COMPLETED, completed.status)
            assertEquals(1, completed.logCount)
            assertEquals(7, completed.progressCount)
            assertEquals(3, store.exports().size)
        }
    }

    private fun exportRecord(status: ExportStatus, counts: ExportCounts): ExportRecordEntity {
        return ExportRecordEntity(
            id = java.util.UUID.randomUUID().toString(),
            exportedAtLocal = "2026-09-21T10:00",
            destinationReference = "export.zip",
            logCount = counts.logCount,
            evidenceCount = counts.evidenceCount,
            progressCount = counts.progressCount,
            recordingCount = counts.recordingCount,
            status = status,
        )
    }
}
