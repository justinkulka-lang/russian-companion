package dev.russiancompanion.v2.curriculum

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File
import java.security.MessageDigest
import java.time.DateTimeException
import java.time.LocalDate

internal const val AUTHORIZED_FINAL_AUDIT_DIRECTORY = "audit/final-curriculum-audit"

internal const val RETAINED_FINAL_AUDIT_SCHEMA = 1

private val ISO_CALENDAR_DATE = Regex("^\\d{4}-\\d{2}-\\d{2}$")

private val retainedAuditJson = Json {
    ignoreUnknownKeys = false
    explicitNulls = true
}

@Serializable
internal data class RetainedUnitAuditDisposition(
    val unitNumber: Int,
    val auditedContentDigest: String,
    val disposition: String,
)

@Serializable
internal data class RetainedFinalAuditDocument(
    val schemaVersion: Int,
    val disposition: String,
    val modelVersion: String,
    val date: String,
    val auditedContentDigest: String,
    val unitDispositions: List<RetainedUnitAuditDisposition> = emptyList(),
)

internal fun isIsoCalendarDate(value: String?): Boolean {
    if (value == null || !ISO_CALENDAR_DATE.matches(value)) return false
    return try {
        LocalDate.parse(value)
        true
    } catch (_: DateTimeException) {
        false
    }
}

internal fun sha256Bytes(bytes: ByteArray): String {
    val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
    return digest.joinToString("") { byte -> "%02x".format(byte) }
}

internal fun encodeRetainedFinalAudit(document: RetainedFinalAuditDocument): ByteArray {
    return retainedAuditJson.encodeToString(RetainedFinalAuditDocument.serializer(), document)
        .toByteArray(Charsets.UTF_8)
}

internal fun finalAuditPathDefect(identity: String): String? {
    if (identity.isBlank() || !isSubstantive(identity)) return "retained audit artifact"
    if (identity.startsWith("/") || identity.startsWith("\\")) return "absolute audit path"
    if (identity.contains('\\')) return "backslash audit path"
    if (identity.contains(':')) return "drive prefix"
    if (identity.contains("//")) return "audit path is not normalized"
    val parts = identity.split('/')
    if (parts.any { it.isEmpty() || it == "." || it == ".." }) return "traversal audit path"
    val prefix = "$AUTHORIZED_FINAL_AUDIT_DIRECTORY/"
    if (!identity.startsWith(prefix) || identity.length == prefix.length || identity.endsWith("/")) {
        return "audit artifact outside the authorized directory"
    }
    return null
}

internal fun retainedFinalAuditDefects(
    repositoryRoot: File?,
    audit: V2FinalAuditIdentity?,
    governedDigest: String?,
    unitNumber: Int?,
    packArtifactIdentity: String?,
): List<String> {
    if (audit == null) return emptyList()
    val pathDefect = finalAuditPathDefect(audit.artifactIdentity)
    if (pathDefect != null) return listOf("final audit path is invalid: $pathDefect")
    if (unitNumber != null && audit.artifactIdentity != packArtifactIdentity) {
        return listOf("unit audit artifact does not match the pack-wide retained audit")
    }
    if (repositoryRoot == null) return listOf("retained audit repository is absent")
    val root = repositoryRoot.canonicalFile
    val candidate = File(root, audit.artifactIdentity)
    val authorized = File(root, AUTHORIZED_FINAL_AUDIT_DIRECTORY).canonicalFile
    val authorizedPrefix = authorized.path.trimEnd('/', '\\') + File.separator
    if (!candidate.isFile) return listOf("retained audit artifact is absent")
    val canonical = candidate.canonicalFile
    if (!canonical.path.startsWith(authorizedPrefix)) {
        return listOf("audit artifact outside the authorized directory")
    }
    if (candidate.length() == 0L) return listOf("retained audit artifact is empty")
    val bytes = candidate.readBytes()
    if (sha256Bytes(bytes) != audit.artifactDigest) {
        return listOf("retained audit artifact bytes do not match the recorded digest")
    }
    val document = try {
        retainedAuditJson.decodeFromString(RetainedFinalAuditDocument.serializer(), bytes.toString(Charsets.UTF_8))
    } catch (_: SerializationException) {
        return listOf("retained audit artifact is malformed")
    } catch (_: IllegalArgumentException) {
        return listOf("retained audit artifact is malformed")
    }
    val missing = mutableListOf<String>()
    if (document.schemaVersion != RETAINED_FINAL_AUDIT_SCHEMA) missing += "audit schema"
    if (document.disposition != FINAL_CURRICULUM_AUDIT_PASSED || document.disposition != audit.disposition) {
        missing += "audit disposition"
    }
    if (document.modelVersion != audit.modelVersion || !isSubstantive(document.modelVersion)) missing += "model version"
    if (document.date != audit.date || !isIsoCalendarDate(document.date)) missing += "audit date"
    if (unitNumber == null) {
        if (document.auditedContentDigest != audit.auditedContentDigest ||
            (!governedDigest.isNullOrBlank() && document.auditedContentDigest != governedDigest)
        ) {
            missing += "audited content digest"
        }
    } else {
        val entry = document.unitDispositions.singleOrNull { it.unitNumber == unitNumber }
        if (entry == null) {
            missing += "governed unit disposition"
        } else {
            if (entry.disposition != FINAL_CURRICULUM_AUDIT_PASSED) missing += "unit audit disposition"
            if (entry.auditedContentDigest != audit.auditedContentDigest ||
                (!governedDigest.isNullOrBlank() && entry.auditedContentDigest != governedDigest)
            ) {
                missing += "audited content digest"
            }
        }
    }
    return if (missing.isEmpty()) emptyList() else listOf("retained audit artifact disagrees: ${missing.joinToString(", ")}")
}
