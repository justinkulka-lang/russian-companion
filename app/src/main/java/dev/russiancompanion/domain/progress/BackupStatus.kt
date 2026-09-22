package dev.russiancompanion.domain.progress

internal enum class BackupOutcome {
    NEVER_EXPORTED,
    COMPLETED,
    FAILED,
    NOT_CONFIRMED,
}

internal data class BackupStatus(
    val outcome: BackupOutcome,
    val successfulExportExists: Boolean,
    val lastSuccessfulAt: String? = null,
    val lastSuccessfulFileName: String? = null,
    val logCount: Int? = null,
    val evidenceCount: Int? = null,
    val progressCount: Int? = null,
    val recordingCount: Int? = null,
    val expectedRecordingsIncluded: Boolean? = null,
    val latestAttemptAt: String? = null,
) {
    val claimsProtection: Boolean
        get() = outcome == BackupOutcome.COMPLETED
}

internal data class ExportRecordView(
    val exportedAtLocal: String,
    val fileName: String?,
    val logCount: Int,
    val evidenceCount: Int,
    val progressCount: Int,
    val recordingCount: Int,
    val outcome: BackupOutcome,
)

internal fun backupStatus(
    records: List<ExportRecordView>,
    expectedRecordingCount: Int,
): BackupStatus {
    val successful = records.filter { it.outcome == BackupOutcome.COMPLETED }
        .maxWithOrNull(compareBy({ it.exportedAtLocal }, { it.fileName.orEmpty() }))
    val latest = records.maxWithOrNull(compareBy({ it.exportedAtLocal }, { it.fileName.orEmpty() }))
    if (latest == null) {
        return BackupStatus(
            outcome = BackupOutcome.NEVER_EXPORTED,
            successfulExportExists = false,
        )
    }
    val included = if (latest.outcome == BackupOutcome.COMPLETED) {
        latest.recordingCount >= expectedRecordingCount
    } else {
        null
    }
    return BackupStatus(
        outcome = latest.outcome,
        successfulExportExists = successful != null,
        lastSuccessfulAt = successful?.exportedAtLocal,
        lastSuccessfulFileName = successful?.fileName,
        logCount = latest.logCount,
        evidenceCount = latest.evidenceCount,
        progressCount = latest.progressCount,
        recordingCount = latest.recordingCount,
        expectedRecordingsIncluded = included,
        latestAttemptAt = latest.exportedAtLocal,
    )
}
