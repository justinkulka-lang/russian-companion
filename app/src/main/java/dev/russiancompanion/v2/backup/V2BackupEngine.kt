package dev.russiancompanion.v2.backup

import dev.russiancompanion.data.local.LocalStore
import kotlinx.serialization.encodeToString
import java.io.File
import java.io.OutputStream

internal data class RestorePreview(
    val additions: Int,
    val replacements: Int,
    val omissions: Int,
    val conflicts: Int,
)

internal fun restorePreview(current: GovernedLearnerDocument, incoming: GovernedLearnerDocument): RestorePreview {
    val left = learnerRecords(current)
    val right = learnerRecords(incoming)
    var additions = 0
    var replacements = 0
    var omissions = 0
    var conflicts = 0
    val keys = left.keys + right.keys
    for (key in keys) {
        val before = left[key]
        val after = right[key]
        when {
            before == null && after != null -> additions += 1
            before != null && after == null -> omissions += 1
            before != null && after != null -> {
                replacements += 1
                if (before != after) conflicts += 1
            }
        }
    }
    return RestorePreview(additions, replacements, omissions, conflicts)
}

internal fun writeStoreBackup(
    store: LocalStore,
    output: OutputStream,
    applicationId: String,
    packId: String,
    packVersion: Int,
    appearance: String,
) {
    val document = store.governedLearnerDocument()
    val files = document.recordings.map { recording ->
        val file = store.recordingFile(recording.localFileReference)
            ?: throw BackupWriteException("missing-recording:${recording.id}:${recording.localFileReference}")
        recording to file
    }
    writeGovernedBackup(output, applicationId, packId, packVersion, appearance, document, files)
}

internal fun restoreStoreBackup(
    store: LocalStore,
    validated: ValidatedBackup,
    recoveryDir: File,
): GovernedReplaceOutcome {
    val fault = V2BackupFaults.throwOnRestore
    if (fault != null) {
        V2BackupFaults.throwOnRestore = null
        throw fault
    }
    recoveryDir.mkdirs()
    return store.replaceGovernedLearner(validated.document, validated.recordings)
}

private fun learnerRecords(document: GovernedLearnerDocument): Map<String, String> {
    val records = linkedMapOf<String, String>()
    records["profile"] = backupJson.encodeToString(BackupProfile.serializer(), document.profile)
    document.steps.forEach { records["step:${it.stepId}"] = backupJson.encodeToString(BackupStep.serializer(), it) }
    document.attempts.forEach { records["attempt:${it.id}"] = backupJson.encodeToString(BackupAttempt.serializer(), it) }
    document.bookmarks.forEach { records["bookmark:${it.targetId}"] = backupJson.encodeToString(BackupBookmark.serializer(), it) }
    document.notes.forEach { records["note:${it.id}"] = backupJson.encodeToString(BackupNote.serializer(), it) }
    document.tutorQueue.forEach { records["tutor:${it.id}"] = backupJson.encodeToString(BackupTutorItem.serializer(), it) }
    document.bookAttestations.forEach {
        records["book:${it.unitNumber}"] = backupJson.encodeToString(BackupBookAttestation.serializer(), it)
    }
    document.unitCompletions.forEach {
        records["unit:${it.unitNumber}"] = backupJson.encodeToString(BackupUnitCompletion.serializer(), it)
    }
    document.learnerState?.let { records["learner"] = backupJson.encodeToString(BackupLearnerState.serializer(), it) }
    document.planInstances.forEach { records["plan:${it.id}"] = backupJson.encodeToString(BackupPlanInstance.serializer(), it) }
    document.activityLogs.forEach { records["activity:${it.id}"] = backupJson.encodeToString(BackupActivityLog.serializer(), it) }
    document.evidence.forEach { records["evidence:${it.id}"] = backupJson.encodeToString(BackupEvidence.serializer(), it) }
    document.tutorRequirements.forEach {
        records["tutor-gate:${it.gateDefinitionId}"] = backupJson.encodeToString(BackupTutorRequirement.serializer(), it)
    }
    document.decisions.forEach { records["decision:${it.id}"] = backupJson.encodeToString(BackupDecision.serializer(), it) }
    document.recordings.forEach { records["recording:${it.id}"] = backupJson.encodeToString(BackupRecording.serializer(), it) }
    return records
}
