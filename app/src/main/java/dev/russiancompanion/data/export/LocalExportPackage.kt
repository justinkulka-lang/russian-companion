package dev.russiancompanion.data.export

import android.content.Context
import dev.russiancompanion.domain.progress.evidenceAttestationLabel
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

internal const val EXPORT_VERSION = 1

internal data class ExportLogRow(
    val id: String,
    val category: String,
    val durationMinutes: Int,
    val completed: Boolean,
    val recordedAtLocal: String,
    val segmentId: String,
    val planInstanceId: String,
    val selfAttested: Boolean,
    val attestationLabel: String,
)

internal data class ExportEvidenceRow(
    val id: String,
    val gateDefinitionId: String,
    val evidenceType: String,
    val enteredValue: String,
    val selfAttestationStatus: String,
    val attestationLabel: String,
    val recordedAtLocal: String,
)

internal data class ExportDecisionRow(
    val id: String,
    val gateDefinitionId: String,
    val ruleVersion: String,
    val evidenceIds: String,
    val conditionOutcomes: String,
    val status: String,
    val evaluatedAtLocal: String,
)

internal data class ExportProgressRow(
    val category: String,
    val recordedMinutes: Long,
    val completionCount: Int,
)

internal data class ExportAudio(
    val id: String,
    val audioModelId: String,
    val createdAtLocal: String,
    val markedForTutorReview: Boolean,
    val completionState: String,
    val fileName: String,
    val bytes: ByteArray?,
)

internal sealed interface BuiltPackage {
    class Ready(
        val bytes: ByteArray,
        val logCount: Int,
        val evidenceCount: Int,
        val decisionCount: Int,
        val progressCount: Int,
        val recordingCount: Int,
        val empty: Boolean,
    ) : BuiltPackage

    data class RecordingUnreadable(val recordingId: String) : BuiltPackage
}

internal fun buildExportZip(
    logs: List<ExportLogRow>,
    evidence: List<ExportEvidenceRow>,
    decisions: List<ExportDecisionRow>,
    progress: List<ExportProgressRow>,
    recordings: List<ExportAudio>,
): BuiltPackage {
    val missing = recordings.firstOrNull { it.bytes == null || it.bytes.isEmpty() }
    if (missing != null) return BuiltPackage.RecordingUnreadable(missing.id)
    val files = mutableListOf(
        "manifest.json",
        "activity-logs.csv",
        "gate-evidence.csv",
        "gate-decisions.csv",
        "progress.csv",
        "recordings.csv",
    )
    files += recordings.map { it.fileName }
    val manifest = ExportManifest(
        exportVersion = EXPORT_VERSION,
        empty = logs.isEmpty() && evidence.isEmpty() && decisions.isEmpty() && recordings.isEmpty(),
        logCount = logs.size,
        evidenceCount = evidence.size,
        decisionCount = decisions.size,
        progressCount = progress.size,
        recordingCount = recordings.size,
        files = files,
    )
    val output = ByteArrayOutputStream()
    ZipOutputStream(output).use { zip ->
        putText(zip, "manifest.json", manifestJson.encodeToString(manifest))
        putText(zip, "activity-logs.csv", logsCsv(logs))
        putText(zip, "gate-evidence.csv", evidenceCsv(evidence))
        putText(zip, "gate-decisions.csv", decisionsCsv(decisions))
        putText(zip, "progress.csv", progressCsv(progress))
        putText(zip, "recordings.csv", recordingsCsv(recordings))
        recordings.forEach { recording ->
            val bytes = recording.bytes ?: return BuiltPackage.RecordingUnreadable(recording.id)
            zip.putNextEntry(ZipEntry(recording.fileName))
            zip.write(bytes)
            zip.closeEntry()
        }
    }
    return BuiltPackage.Ready(
        bytes = output.toByteArray(),
        logCount = logs.size,
        evidenceCount = evidence.size,
        decisionCount = decisions.size,
        progressCount = progress.size,
        recordingCount = recordings.size,
        empty = manifest.empty,
    )
}

internal fun exportAudioName(recordingId: String, localFileReference: String): String {
    val extension = localFileReference.substringAfterLast('.', "m4a").lowercase()
    val safe = if (EXTENSION.matches(extension)) extension else "m4a"
    return "recordings/$recordingId.$safe"
}

internal fun readAppRecording(context: Context, reference: String): ByteArray? {
    if (reference.isBlank() || reference.contains("..") || reference.startsWith("/")) return null
    return try {
        val root = context.filesDir.canonicalFile
        val target = File(root, reference).canonicalFile
        val rootPath = root.path + File.separator
        if (!target.path.startsWith(rootPath)) return null
        if (!target.isFile || target.length() == 0L) return null
        target.readBytes()
    } catch (_: IOException) {
        null
    }
}

@Serializable
internal data class ExportManifest(
    val exportVersion: Int,
    val empty: Boolean,
    val logCount: Int,
    val evidenceCount: Int,
    val decisionCount: Int,
    val progressCount: Int,
    val recordingCount: Int,
    val files: List<String>,
)

private fun logsCsv(logs: List<ExportLogRow>): String {
    val rows = mutableListOf(
        listOf(
            "id",
            "category",
            "durationMinutes",
            "completed",
            "recordedAtLocal",
            "segmentId",
            "planInstanceId",
            "selfAttested",
            "attestationLabel",
        ),
    )
    logs.forEach { log ->
        rows += listOf(
            log.id,
            log.category,
            log.durationMinutes.toString(),
            log.completed.toString(),
            log.recordedAtLocal,
            log.segmentId,
            log.planInstanceId,
            log.selfAttested.toString(),
            log.attestationLabel,
        )
    }
    return csv(rows)
}

private fun evidenceCsv(evidence: List<ExportEvidenceRow>): String {
    val rows = mutableListOf(
        listOf(
            "id",
            "gateDefinitionId",
            "evidenceType",
            "enteredValue",
            "selfAttestationStatus",
            "attestationLabel",
            "recordedAtLocal",
        ),
    )
    evidence.forEach { row ->
        rows += listOf(
            row.id,
            row.gateDefinitionId,
            row.evidenceType,
            row.enteredValue,
            row.selfAttestationStatus,
            row.attestationLabel,
            row.recordedAtLocal,
        )
    }
    return csv(rows)
}

private fun decisionsCsv(decisions: List<ExportDecisionRow>): String {
    val rows = mutableListOf(
        listOf(
            "id",
            "gateDefinitionId",
            "ruleVersion",
            "evidenceIds",
            "conditionOutcomes",
            "status",
            "evaluatedAtLocal",
        ),
    )
    decisions.forEach { row ->
        rows += listOf(
            row.id,
            row.gateDefinitionId,
            row.ruleVersion,
            row.evidenceIds,
            row.conditionOutcomes,
            row.status,
            row.evaluatedAtLocal,
        )
    }
    return csv(rows)
}

private fun progressCsv(progress: List<ExportProgressRow>): String {
    val rows = mutableListOf(listOf("category", "recordedMinutes", "completionCount"))
    progress.forEach { row ->
        rows += listOf(row.category, row.recordedMinutes.toString(), row.completionCount.toString())
    }
    return csv(rows)
}

/**
 * Recording records do not store an activity-log id, so this index does not invent one.
 */
private fun recordingsCsv(recordings: List<ExportAudio>): String {
    val rows = mutableListOf(
        listOf(
            "fileName",
            "recordingId",
            "audioModelId",
            "createdAtLocal",
            "markedForTutorReview",
            "completionState",
        ),
    )
    recordings.forEach { recording ->
        rows += listOf(
            recording.fileName,
            recording.id,
            recording.audioModelId,
            recording.createdAtLocal,
            recording.markedForTutorReview.toString(),
            recording.completionState,
        )
    }
    return csv(rows)
}

internal fun csv(rows: List<List<String>>): String {
    return rows.joinToString(separator = "\n") { row ->
        row.joinToString(separator = ",") { cell ->
            if (cell.any { it == ',' || it == '"' || it == '\n' || it == '\r' }) {
                "\"" + cell.replace("\"", "\"\"") + "\""
            } else {
                cell
            }
        }
    } + "\n"
}

internal fun attestationLabelForEvidence(evidenceType: String): String = evidenceAttestationLabel(evidenceType)

private fun putText(zip: ZipOutputStream, name: String, text: String) {
    zip.putNextEntry(ZipEntry(name))
    zip.write(text.toByteArray(Charsets.UTF_8))
    zip.closeEntry()
}

private val manifestJson = Json {
    prettyPrint = true
    explicitNulls = true
}

private val EXTENSION = Regex("[a-z0-9]{1,8}")
