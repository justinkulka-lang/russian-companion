package dev.russiancompanion.v2.backup

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

internal const val BACKUP_FORMAT_VERSION = 1
internal const val BACKUP_SCHEMA_VERSION = 1
internal const val BACKUP_LEARNER_ENTRY = "learner.json"
internal const val BACKUP_MANIFEST_ENTRY = "manifest.json"

internal const val BACKUP_SAVED = "Backup saved. This device has a successful local backup."
internal const val BACKUP_FAILED = "Backup failed. The records on this device are unchanged."
internal const val BACKUP_CANCELLED = "Backup cancelled. The records on this device are unchanged."
internal const val RESTORE_FAILED = "Restore failed. The records already on this device are unchanged."
internal const val RESTORE_RECOVERY_REQUIRED =
    "Restore failed. Local recovery is still required. Open the app again to finish restoring the learner records from before this attempt."
internal const val RESTORE_DONE = "Restore finished. Learner records were replaced with the backup. They were not merged."
internal const val RESTORE_CANCELLED = "Restore cancelled. The records on this device are unchanged."
internal const val REPLACE_EXPLANATION =
    "Restoring replaces learner records on this device with this backup. It does not merge. Records only on this device will be removed."

internal val backupJson = Json {
    ignoreUnknownKeys = false
    explicitNulls = true
    encodeDefaults = true
}

@Serializable
internal data class BackupManifest(
    val formatVersion: Int,
    val applicationId: String,
    val packId: String,
    val packVersion: Int,
    val appearance: String,
    val attemptCount: Int,
    val noteCount: Int,
    val bookmarkCount: Int,
    val tutorCount: Int,
    val stepCount: Int,
    val recordingCount: Int,
    val activityCount: Int,
    val evidenceCount: Int,
    val files: List<BackupFileRecord>,
)

@Serializable
internal data class BackupFileRecord(
    val path: String,
    val sha256: String,
    val bytes: Long,
)

@Serializable
internal data class GovernedLearnerDocument(
    val schemaVersion: Int,
    val profile: BackupProfile,
    val steps: List<BackupStep>,
    val attempts: List<BackupAttempt>,
    val bookmarks: List<BackupBookmark>,
    val notes: List<BackupNote>,
    val tutorQueue: List<BackupTutorItem>,
    val bookAttestations: List<BackupBookAttestation>,
    val unitCompletions: List<BackupUnitCompletion>,
    val learnerState: BackupLearnerState?,
    val planInstances: List<BackupPlanInstance>,
    val activityLogs: List<BackupActivityLog>,
    val evidence: List<BackupEvidence>,
    val tutorRequirements: List<BackupTutorRequirement>,
    val decisions: List<BackupDecision>,
    val recordings: List<BackupRecording>,
)

@Serializable
internal data class BackupProfile(
    val id: String,
    val onboardingCompleted: Boolean,
    val currentUnitNumber: Int,
    val startingPoint: String,
    val hasPenguin: Boolean,
    val hasAnki: Boolean,
    val hasBinder: Boolean,
    val hasTutor: Boolean,
    val dailyMinutes: Int,
    val transliterationEnabled: Boolean,
    val currentSessionId: String?,
    val currentStepIndex: Int,
)

@Serializable
internal data class BackupStep(val stepId: String, val completedAtLocal: String)

@Serializable
internal data class BackupAttempt(
    val id: String,
    val itemId: String,
    val selected: String,
    val correct: Boolean,
    val recordedAtLocal: String,
)

@Serializable
internal data class BackupBookmark(val targetId: String, val kind: String, val createdAtLocal: String)

@Serializable
internal data class BackupNote(
    val id: String,
    val targetId: String,
    val text: String,
    val createdAtLocal: String,
    val userAuthoredRussian: Boolean,
)

@Serializable
internal data class BackupTutorItem(
    val id: String,
    val targetId: String,
    val note: String,
    val createdAtLocal: String,
)

@Serializable
internal data class BackupBookAttestation(val unitNumber: Int, val recordedAtLocal: String)

@Serializable
internal data class BackupUnitCompletion(val unitNumber: Int, val recordedAtLocal: String)

@Serializable
internal data class BackupLearnerState(
    val id: String,
    val activeSegmentId: String?,
    val onboardingCompleted: Boolean,
    val planContext: String?,
)

@Serializable
internal data class BackupPlanInstance(
    val id: String,
    val localDate: String,
    val segmentId: String,
    val planDefinitionId: String,
    val completionState: String,
    val completedAtLocal: String?,
)

@Serializable
internal data class BackupActivityLog(
    val id: String,
    val category: String,
    val durationMinutes: Int,
    val completed: Boolean,
    val recordedAtLocal: String,
    val segmentId: String?,
    val planInstanceId: String?,
    val selfAttested: Boolean,
)

@Serializable
internal data class BackupEvidence(
    val id: String,
    val gateDefinitionId: String,
    val evidenceType: String,
    val enteredValue: String,
    val selfAttestationStatus: String,
    val recordedAtLocal: String,
)

@Serializable
internal data class BackupTutorRequirement(val gateDefinitionId: String, val recordedAtLocal: String)

@Serializable
internal data class BackupDecision(
    val id: String,
    val gateDefinitionId: String,
    val ruleVersion: String,
    val ruleSnapshot: String,
    val evidenceIds: String,
    val conditionOutcomes: String,
    val status: String,
    val evaluatedAtLocal: String,
)

@Serializable
internal data class BackupRecording(
    val id: String,
    val audioModelId: String,
    val localFileReference: String,
    val createdAtLocal: String,
    val markedForTutorReview: Boolean,
    val completionState: String,
    val activityLogId: String?,
)

internal data class StagedRecording(val recording: BackupRecording, val file: File)

internal data class ValidatedBackup(
    val manifest: BackupManifest,
    val document: GovernedLearnerDocument,
    val recordings: List<StagedRecording>,
)

internal sealed interface BackupReadResult {
    data class Valid(val backup: ValidatedBackup) : BackupReadResult
    data class Rejected(val reason: BackupRejection) : BackupReadResult
}

internal enum class GovernedReplaceOutcome {
    REPLACED,
    UNCHANGED,
    RECOVERY_REQUIRED,
}

internal enum class BackupRejection {
    MALFORMED,
    TRUNCATED,
    UNSUPPORTED_VERSION,
    INTEGRITY,
    INCOMPLETE,
    INCOMPATIBLE,
    UNAUTHORIZED,
}

internal class BackupWriteException(message: String = "backup-write-failed") : IOException(message)

internal object V2BackupFaults {
    @Volatile var failWrite: Boolean = false
    @Volatile var failBeforeMutation: Boolean = false
    @Volatile var failInsideReplacement: Boolean = false
    @Volatile var failAfterCommit: Boolean = false
    @Volatile var failDuringFilePublish: Boolean = false
    @Volatile var crashAfterFileSwapBeforeCommit: Boolean = false
    @Volatile var failJournalWrite: Boolean = false
    @Volatile var failSnapshotRestore: Boolean = false

    fun reset() {
        failWrite = false
        failBeforeMutation = false
        failInsideReplacement = false
        failAfterCommit = false
        failDuringFilePublish = false
        crashAfterFileSwapBeforeCommit = false
        failJournalWrite = false
        failSnapshotRestore = false
    }
}

@Serializable
internal data class PreservedExportRecord(
    val id: String,
    val exportedAtLocal: String,
    val destinationReference: String? = null,
    val logCount: Int,
    val evidenceCount: Int,
    val progressCount: Int,
    val recordingCount: Int,
    val status: String,
    val documentReference: String? = null,
    val recordingSetDigest: String? = null,
)

@Serializable
internal data class ReplacementJournal(
    val phase: String,
    val previousFingerprint: String,
    val nextFingerprint: String,
    val snapshotDir: String,
    val previousDocument: String,
    val exports: List<PreservedExportRecord>,
)

internal fun writeGovernedBackup(
    output: OutputStream,
    applicationId: String,
    packId: String,
    packVersion: Int,
    appearance: String,
    document: GovernedLearnerDocument,
    recordingFiles: List<Pair<BackupRecording, File>>,
    formatVersion: Int = BACKUP_FORMAT_VERSION,
) {
    if (V2BackupFaults.failWrite) throw BackupWriteException()
    val learnerBytes = backupJson.encodeToString(GovernedLearnerDocument.serializer(), document).encodeToByteArray()
    val staged = recordingFiles.map { (recording, file) ->
        val path = recordingEntryPath(recording.id, recording.localFileReference)
        BackupFileRecord(path = path, sha256 = sha256(file), bytes = file.length()) to file
    }
    val files = mutableListOf(BackupFileRecord(BACKUP_LEARNER_ENTRY, sha256(learnerBytes), learnerBytes.size.toLong()))
    files += staged.map { it.first }
    val manifest = BackupManifest(
        formatVersion = formatVersion,
        applicationId = applicationId,
        packId = packId,
        packVersion = packVersion,
        appearance = appearance,
        attemptCount = document.attempts.size,
        noteCount = document.notes.size,
        bookmarkCount = document.bookmarks.size,
        tutorCount = document.tutorQueue.size,
        stepCount = document.steps.size,
        recordingCount = document.recordings.size,
        activityCount = document.activityLogs.size,
        evidenceCount = document.evidence.size,
        files = files,
    )
    val manifestBytes = backupJson.encodeToString(BackupManifest.serializer(), manifest).encodeToByteArray()
    ZipOutputStream(output).use { zip ->
        putBytes(zip, BACKUP_MANIFEST_ENTRY, manifestBytes)
        putBytes(zip, BACKUP_LEARNER_ENTRY, learnerBytes)
        staged.forEach { (record, file) ->
            zip.putNextEntry(ZipEntry(record.path))
            file.inputStream().use { input -> input.copyTo(zip) }
            zip.closeEntry()
        }
    }
}

internal fun readGovernedBackup(
    input: InputStream,
    expectedApplicationId: String,
    expectedPackId: String,
    expectedPackVersion: Int,
    stagingDir: File,
): BackupReadResult {
    stagingDir.mkdirs()
    val zipFile = File(stagingDir, "incoming.zip")
    try {
        zipFile.outputStream().use { destination -> input.copyTo(destination) }
    } catch (_: IOException) {
        return BackupReadResult.Rejected(BackupRejection.TRUNCATED)
    }
    if (zipFile.length() < 2L) return BackupReadResult.Rejected(BackupRejection.MALFORMED)
    val zipHeader = ByteArray(2)
    val headerBytes = zipFile.inputStream().use { it.read(zipHeader) }
    val looksLikeZip = headerBytes == 2 && zipHeader[0] == 'P'.code.toByte() && zipHeader[1] == 'K'.code.toByte()
    val opened = try {
        ZipFile(zipFile)
    } catch (_: ZipException) {
        return BackupReadResult.Rejected(if (looksLikeZip) BackupRejection.TRUNCATED else BackupRejection.MALFORMED)
    } catch (_: IOException) {
        return BackupReadResult.Rejected(BackupRejection.MALFORMED)
    }
    opened.use { zip ->
        val names = zip.entries().toList().map { it.name }
        if (names.size != names.toSet().size) return BackupReadResult.Rejected(BackupRejection.UNAUTHORIZED)
        if (names.any { !allowedBackupEntry(it) }) return BackupReadResult.Rejected(BackupRejection.UNAUTHORIZED)
        if (BACKUP_MANIFEST_ENTRY !in names || BACKUP_LEARNER_ENTRY !in names) {
            return BackupReadResult.Rejected(BackupRejection.INCOMPLETE)
        }
        val manifestBytes = readEntry(zip, BACKUP_MANIFEST_ENTRY) ?: return BackupReadResult.Rejected(BackupRejection.TRUNCATED)
        val manifest = try {
            backupJson.decodeFromString(BackupManifest.serializer(), manifestBytes.toString(Charsets.UTF_8))
        } catch (_: Exception) {
            return BackupReadResult.Rejected(BackupRejection.MALFORMED)
        }
        if (manifest.formatVersion != BACKUP_FORMAT_VERSION) {
            return BackupReadResult.Rejected(BackupRejection.UNSUPPORTED_VERSION)
        }
        if (manifest.applicationId != expectedApplicationId) {
            return BackupReadResult.Rejected(BackupRejection.UNAUTHORIZED)
        }
        if (manifest.packId != expectedPackId || manifest.packVersion != expectedPackVersion) {
            return BackupReadResult.Rejected(BackupRejection.INCOMPATIBLE)
        }
        if (manifest.appearance != "DARK" && manifest.appearance != "FOLLOW_SYSTEM") {
            return BackupReadResult.Rejected(BackupRejection.INCOMPATIBLE)
        }
        val declared = manifest.files.map { it.path }
        if (BACKUP_LEARNER_ENTRY !in declared) return BackupReadResult.Rejected(BackupRejection.INCOMPLETE)
        val expectedNames = declared.toSet() + BACKUP_MANIFEST_ENTRY
        if (names.toSet() != expectedNames) return BackupReadResult.Rejected(BackupRejection.INCOMPLETE)
        for (file in manifest.files) {
            if (file.path.startsWith("recordings/")) continue
            val entry = zip.getEntry(file.path) ?: return BackupReadResult.Rejected(BackupRejection.INCOMPLETE)
            if (entry.size >= 0 && entry.size != file.bytes) return BackupReadResult.Rejected(BackupRejection.INTEGRITY)
            val bytes = readEntry(zip, file.path) ?: return BackupReadResult.Rejected(BackupRejection.TRUNCATED)
            if (bytes.size.toLong() != file.bytes || sha256(bytes) != file.sha256) {
                return BackupReadResult.Rejected(BackupRejection.INTEGRITY)
            }
        }
        val learnerRecord = manifest.files.firstOrNull { it.path == BACKUP_LEARNER_ENTRY }
            ?: return BackupReadResult.Rejected(BackupRejection.INCOMPLETE)
        val learnerBytes = readEntry(zip, BACKUP_LEARNER_ENTRY) ?: return BackupReadResult.Rejected(BackupRejection.TRUNCATED)
        if (learnerBytes.size.toLong() != learnerRecord.bytes || sha256(learnerBytes) != learnerRecord.sha256) {
            return BackupReadResult.Rejected(BackupRejection.INTEGRITY)
        }
        val document = try {
            backupJson.decodeFromString(GovernedLearnerDocument.serializer(), learnerBytes.toString(Charsets.UTF_8))
        } catch (_: Exception) {
            return BackupReadResult.Rejected(BackupRejection.MALFORMED)
        }
        if (document.schemaVersion != BACKUP_SCHEMA_VERSION) {
            return BackupReadResult.Rejected(BackupRejection.UNSUPPORTED_VERSION)
        }
        if (
            document.attempts.size != manifest.attemptCount ||
            document.notes.size != manifest.noteCount ||
            document.bookmarks.size != manifest.bookmarkCount ||
            document.tutorQueue.size != manifest.tutorCount ||
            document.steps.size != manifest.stepCount ||
            document.recordings.size != manifest.recordingCount ||
            document.activityLogs.size != manifest.activityCount ||
            document.evidence.size != manifest.evidenceCount
        ) {
            return BackupReadResult.Rejected(BackupRejection.INCOMPLETE)
        }
        if (duplicateIds(document)) return BackupReadResult.Rejected(BackupRejection.MALFORMED)
        val stagedRecordings = mutableListOf<StagedRecording>()
        val recordingPaths = document.recordings.map { recordingEntryPath(it.id, it.localFileReference) }
        if (recordingPaths.toSet().size != recordingPaths.size) {
            return BackupReadResult.Rejected(BackupRejection.MALFORMED)
        }
        val declaredRecordings = manifest.files.map { it.path }.filter { it.startsWith("recordings/") }.toSet()
        if (declaredRecordings != recordingPaths.toSet()) return BackupReadResult.Rejected(BackupRejection.INCOMPLETE)
        val declaredHashes = manifest.files.associate { it.path to it }
        for (recording in document.recordings) {
            if (!safeRecordingId(recording.id)) return BackupReadResult.Rejected(BackupRejection.UNAUTHORIZED)
            val path = recordingEntryPath(recording.id, recording.localFileReference)
            val declaredFile = declaredHashes[path] ?: return BackupReadResult.Rejected(BackupRejection.INCOMPLETE)
            val target = File(stagingDir, "${recording.id}.bin")
            val entry = zip.getEntry(path) ?: return BackupReadResult.Rejected(BackupRejection.INCOMPLETE)
            val digest = MessageDigest.getInstance("SHA-256")
            var size = 0L
            try {
                zip.getInputStream(entry).use { source ->
                    target.outputStream().use { destination ->
                        val buffer = ByteArray(8192)
                        while (true) {
                            val read = source.read(buffer)
                            if (read < 0) break
                            destination.write(buffer, 0, read)
                            digest.update(buffer, 0, read)
                            size += read
                        }
                    }
                }
            } catch (_: IOException) {
                return BackupReadResult.Rejected(BackupRejection.TRUNCATED)
            }
            if (size != declaredFile.bytes || hex(digest.digest()) != declaredFile.sha256) {
                return BackupReadResult.Rejected(BackupRejection.INTEGRITY)
            }
            stagedRecordings += StagedRecording(recording, target)
        }
        return BackupReadResult.Valid(ValidatedBackup(manifest, document, stagedRecordings))
    }
}

internal fun recordingEntryPath(id: String, localFileReference: String): String {
    val extension = localFileReference.substringAfterLast('.', "m4a").lowercase()
    val safe = if (extension.length in 1..8 && extension.all { it.isLetterOrDigit() }) extension else "m4a"
    return "recordings/$id.$safe"
}

internal fun allowedBackupEntry(name: String): Boolean {
    if (name == BACKUP_MANIFEST_ENTRY || name == BACKUP_LEARNER_ENTRY) return true
    if (!name.startsWith("recordings/") || name.contains("..") || name.contains('\\')) return false
    val leaf = name.removePrefix("recordings/")
    val id = leaf.substringBeforeLast('.', "")
    val extension = leaf.substringAfterLast('.', "")
    return id.isNotEmpty() && safeRecordingId(id) && extension.length in 1..8 && extension.all { it.isLetterOrDigit() }
}

internal fun safeRecordingId(id: String): Boolean {
    return id.length in 1..80 && id.all { it.isLetterOrDigit() || it == '_' || it == '-' }
}

private fun duplicateIds(document: GovernedLearnerDocument): Boolean {
    fun <T> dup(values: List<T>, id: (T) -> String) = values.size != values.map(id).toSet().size
    return dup(document.steps) { it.stepId } ||
        dup(document.attempts) { it.id } ||
        dup(document.bookmarks) { it.targetId } ||
        dup(document.notes) { it.id } ||
        dup(document.tutorQueue) { it.id } ||
        dup(document.bookAttestations) { it.unitNumber.toString() } ||
        dup(document.unitCompletions) { it.unitNumber.toString() } ||
        dup(document.planInstances) { it.id } ||
        dup(document.activityLogs) { it.id } ||
        dup(document.evidence) { it.id } ||
        dup(document.tutorRequirements) { it.gateDefinitionId } ||
        dup(document.decisions) { it.id } ||
        dup(document.recordings) { it.id }
}

private fun readEntry(zip: ZipFile, name: String): ByteArray? {
    val entry = zip.getEntry(name) ?: return null
    return try {
        zip.getInputStream(entry).use { it.readBytes() }
    } catch (_: IOException) {
        null
    }
}

private fun putBytes(zip: ZipOutputStream, name: String, bytes: ByteArray) {
    zip.putNextEntry(ZipEntry(name))
    zip.write(bytes)
    zip.closeEntry()
}

internal fun sha256(bytes: ByteArray): String = hex(MessageDigest.getInstance("SHA-256").digest(bytes))

internal fun sha256(file: File): String {
    val digest = MessageDigest.getInstance("SHA-256")
    file.inputStream().use { input ->
        val buffer = ByteArray(8192)
        while (true) {
            val read = input.read(buffer)
            if (read < 0) break
            digest.update(buffer, 0, read)
        }
    }
    return hex(digest.digest())
}

private fun hex(bytes: ByteArray): String = bytes.joinToString("") { "%02x".format(it) }
