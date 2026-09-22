package dev.russiancompanion.domain.progress

import java.security.MessageDigest

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
    val recordingSetDigest: String? = null,
)

internal data class RecordingSetMember(
    val recordingId: String,
    val localFileReference: String,
    val byteSize: Long?,
    val contentDigest: String?,
)

internal fun recordingSetDigest(members: List<RecordingSetMember>): String {
    val canonical = members
        .sortedWith(compareBy({ it.recordingId }, { it.localFileReference }))
        .joinToString("\n") { member ->
            listOf(
                member.recordingId,
                member.localFileReference,
                member.byteSize?.toString() ?: "missing",
                member.contentDigest ?: "unreadable",
            ).joinToString("\t")
        }
    return sha256Hex(canonical.toByteArray(Charsets.UTF_8))
}

internal fun sha256Hex(bytes: ByteArray): String {
    return MessageDigest.getInstance("SHA-256").digest(bytes)
        .joinToString("") { byte -> "%02x".format(byte) }
}

internal fun recordingsCoveredByExport(
    exportedDigest: String?,
    currentDigest: String?,
): Boolean {
    if (exportedDigest.isNullOrBlank() || currentDigest.isNullOrBlank()) return false
    return exportedDigest == currentDigest
}

internal fun backupStatus(
    records: List<ExportRecordView>,
    currentRecordingSetDigest: String,
): BackupStatus {
    val successful = records.filter { it.outcome == BackupOutcome.COMPLETED }
        .maxWithOrNull(compareBy({ it.exportedAtLocal }, { it.fileName.orEmpty() }))
    val latest = records.maxWithOrNull(compareBy({ it.exportedAtLocal }, { it.fileName.orEmpty() }))
    if (latest == null) {
        return BackupStatus(
            outcome = BackupOutcome.NEVER_EXPORTED,
            successfulExportExists = false,
            expectedRecordingsIncluded = false,
        )
    }
    val included = if (latest.outcome == BackupOutcome.COMPLETED) {
        recordingsCoveredByExport(latest.recordingSetDigest, currentRecordingSetDigest)
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