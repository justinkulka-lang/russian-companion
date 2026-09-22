package dev.russiancompanion.domain.progress

import dev.russiancompanion.domain.plan.ActivityLogView
import dev.russiancompanion.domain.plan.RecordedCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressSchemaTest {
    @Test
    fun activityLogsRequireATimestampNonnegativeDurationAndBookAnkiAttestation() {
        assertTrue(
            activityLogAccepted(RecordedCategory.READING, 0, "2026-09-21T09:00", selfAttested = false).accepted,
        )
        assertEquals(
            "timestamp-required",
            activityLogAccepted(RecordedCategory.READING, 10, "  ".trim(), selfAttested = false).reason,
        )
        assertEquals(
            "duration-invalid",
            activityLogAccepted(RecordedCategory.READING, -1, "2026-09-21T09:00", selfAttested = false).reason,
        )
        assertEquals(
            "attestation-required",
            activityLogAccepted(RecordedCategory.GRAMMAR, 10, "2026-09-21T09:00", selfAttested = false).reason,
        )
        assertEquals(
            "attestation-required",
            activityLogAccepted(
                RecordedCategory.VOCABULARY_AND_FORMS,
                10,
                "2026-09-21T09:00",
                selfAttested = false,
            ).reason,
        )
        assertTrue(
            activityLogAccepted(RecordedCategory.GRAMMAR, 10, "2026-09-21T09:00", selfAttested = true).accepted,
        )
        assertEquals(
            "attestation-required",
            activityLogAccepted(
                RecordedCategory.READING,
                10,
                "2026-09-21T09:00",
                selfAttested = false,
                bookOrAnkiActivity = true,
            ).reason,
        )
        assertTrue(categoryRequiresSelfAttestation(RecordedCategory.GRAMMAR))
        assertTrue(categoryRequiresSelfAttestation(RecordedCategory.VOCABULARY_AND_FORMS))
        assertFalse(categoryRequiresSelfAttestation(RecordedCategory.SPEAKING_AND_PRONUNCIATION))
        assertEquals(
            PROGRESS_ACTIVITY_ONLY_STATEMENT,
            "Recorded activity is work completed, not certified ability.",
        )
    }

    @Test
    fun laterLogsDoNotRewriteANamedProgressSnapshot() {
        val first = log("log-a", RecordedCategory.READING, minutes = 15, at = "2026-09-21T09:00")
        val snapshot = checkNotNull(captureProgressSnapshot(listOf(first)))
        assertEquals(listOf("log-a"), snapshot.activityLogIds)
        assertEquals(PROGRESS_SNAPSHOT_VERSION, snapshot.version)
        assertEquals(RecordedCategory.entries.map { it.name }, snapshot.totals.map { it.category })
        assertEquals(15L, snapshot.totals.single { it.category == "READING" }.recordedMinutes)
        assertEquals(1, snapshot.totals.single { it.category == "READING" }.completionCount)
        assertTrue(snapshot.totals.filter { it.category != "READING" }.all { it.recordedMinutes == 0L && it.completionCount == 0 })

        val later = log("log-b", RecordedCategory.LISTENING, minutes = 40, at = "2026-09-21T10:00")
        val live = reviewProgress(listOf(first, later))
        assertEquals(55L, live.totalMinutes)
        val reproduced = reproduceProgress(snapshot, listOf(first, later))
        assertTrue(reproducedProgressMatches(snapshot, reproduced))
        assertEquals(15L, reproduced.totalMinutes)
        assertEquals(1, reproduced.totalCompletions)
        assertEquals(0L, reproduced.categories.single { it.category == RecordedCategory.LISTENING }.recordedMinutes)
        assertEquals(encodeProgressSnapshot(snapshot), encodeProgressSnapshot(checkNotNull(parseProgressSnapshot(encodeProgressSnapshot(snapshot)))))
    }

    @Test
    fun missingNamedLogsAndUnreadableDurationsCannotBeReproducedAsTheCapturedTotals() {
        val first = log("log-a", RecordedCategory.WRITING, minutes = 5)
        val snapshot = checkNotNull(captureProgressSnapshot(listOf(first)))
        assertFalse(reproducedProgressMatches(snapshot, reproduceProgress(snapshot, emptyList())))
        assertFalse(reviewProgress(listOf(first.copy(minutes = -1))).readable)
        assertNull(captureProgressSnapshot(listOf(first.copy(minutes = -1))))
        val empty = checkNotNull(captureProgressSnapshot(emptyList()))
        assertTrue(empty.activityLogIds.isEmpty())
        assertTrue(empty.totals.all { it.recordedMinutes == 0L && it.completionCount == 0 })
        assertTrue(reproducedProgressMatches(empty, reproduceProgress(empty, listOf(first))))
        assertNull(parseProgressSnapshot(""))
        assertNull(parseProgressSnapshot("{"))
        assertNull(parseProgressSnapshot(encodeProgressSnapshot(snapshot.copy(version = "2"))))
        val forbidden = listOf("score", "fluency", "proficiency", "verified")
        val names = listOf(ProgressSnapshot::class.java, ProgressCategoryTotal::class.java)
            .flatMap { type -> type.declaredFields.map { it.name.lowercase() } }
        assertTrue(names.none { name -> forbidden.any(name::contains) })
    }

    @Test
    fun exportCompletionRequiresMatchingPackageCountsAndAllowsUnsuccessfulRecords() {
        val ready = ExportCounts(logCount = 2, evidenceCount = 1, progressCount = 7, recordingCount = 3)
        assertTrue(
            exportCompletionAccepted("COMPLETED", ready, ready, "2026-09-21T10:00").accepted,
        )
        assertEquals(
            "package-required",
            exportCompletionAccepted("COMPLETED", ready, packageCounts = null, "2026-09-21T10:00").reason,
        )
        assertEquals(
            "counts-mismatch",
            exportCompletionAccepted(
                "COMPLETED",
                recorded = ready.copy(recordingCount = 2),
                packageCounts = ready,
                exportedAtLocal = "2026-09-21T10:00",
            ).reason,
        )
        assertEquals(
            "timestamp-required",
            exportCompletionAccepted("FAILED", ExportCounts(0, 0, 0, 0), null, "").reason,
        )
        assertEquals(
            "counts-invalid",
            exportCompletionAccepted("FAILED", ExportCounts(-1, 0, 0, 0), null, "2026-09-21T10:00").reason,
        )
        assertEquals(
            "status-invalid",
            exportCompletionAccepted("SUCCESS", ready, ready, "2026-09-21T10:00").reason,
        )
        assertTrue(
            exportCompletionAccepted("FAILED", ExportCounts(0, 0, 0, 0), null, "2026-09-21T10:00").accepted,
        )
        assertTrue(
            exportCompletionAccepted("NOT_CONFIRMED", ExportCounts(0, 0, 0, 0), null, "2026-09-21T10:00").accepted,
        )
        val empty = ExportCounts(logCount = 0, evidenceCount = 0, progressCount = 7, recordingCount = 0)
        assertTrue(exportCompletionAccepted("COMPLETED", empty, empty, "2026-09-21T10:00").accepted)
    }

    private fun log(
        id: String,
        category: RecordedCategory,
        minutes: Int = 10,
        completed: Boolean = true,
        at: String = "2026-09-21T09:00",
    ): ActivityLogView {
        return ActivityLogView(
            id = id,
            category = category,
            minutes = minutes,
            completed = completed,
            recordedAtLocal = at,
            selfAttested = categoryRequiresSelfAttestation(category),
        )
    }
}
