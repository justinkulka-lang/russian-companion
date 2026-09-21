package dev.russiancompanion.release

import dev.russiancompanion.data.local.EligibilityStatus
import dev.russiancompanion.data.local.InclusionResult
import dev.russiancompanion.data.local.LicenseStatus
import dev.russiancompanion.data.local.PlanActivityType
import dev.russiancompanion.data.local.ShareAlikeConfirmation
import dev.russiancompanion.data.local.seed.PreparedBundle
import dev.russiancompanion.data.local.seed.SeedJson
import dev.russiancompanion.data.local.seed.SeedParse
import dev.russiancompanion.data.local.seed.prepareBundledContent
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.system.exitProcess

internal enum class ReleaseDecision {
    APPROVED,
    NOT_APPROVED,
}

internal enum class ReleaseBlocker(val message: String) {
    CONTENT_UNREADABLE("Packaged content is absent or corrupt."),
    PATHWAY_NOT_INSTALLED("The packaged beginner pathway is not installed."),
    PLAN_DEFINITIONS_NOT_INSTALLED("Required plan definitions are not installed."),
    DEFAULT_GATE_NOT_INSTALLED("A default gate is not installed."),
    LICENSED_AUDIO_NOT_PREPARED("Required licensed native audio is not prepared."),
    RIGHTS_RECORDS_ABSENT("Required rights records are not installed."),
    SHARE_ALIKE_UNCONFIRMED("Public release is blocked until share-alike confirmation is recorded."),
    EVIDENCE_MANIFEST_UNREADABLE("The retained-evidence manifest is absent or unreadable."),
    OFFLINE_VERIFICATION_NOT_RETAINED("AT-004 offline verification evidence is not retained."),
    REBOOT_EVIDENCE_NOT_RETAINED("AT-020 device reboot evidence is not retained."),
    LESSON_CYCLE_NOT_RETAINED("AT-030 lesson-cycle evidence is not retained."),
    OWNER_RELEASE_DECISION_NOT_RETAINED("AT-031 owner release decision is not retained."),
}

internal data class ReleaseAssessment(
    val decision: ReleaseDecision,
    val blockers: List<ReleaseBlocker>,
) {
    fun report(): String {
        return (listOf(decision.name) + blockers.map { it.message }).joinToString("\n")
    }
}

@Serializable
internal data class RetainedEvidenceManifest(
    val schemaVersion: Int,
    val records: List<RetainedEvidenceRecord>,
)

@Serializable
internal data class RetainedEvidenceRecord(
    val procedure: String,
    val state: String,
    val evidencePath: String? = null,
)

private val evidenceJson = Json {
    ignoreUnknownKeys = false
    explicitNulls = true
}

private val requiredProcedures = listOf(
    "AT-004" to ReleaseBlocker.OFFLINE_VERIFICATION_NOT_RETAINED,
    "AT-020" to ReleaseBlocker.REBOOT_EVIDENCE_NOT_RETAINED,
    "AT-030" to ReleaseBlocker.LESSON_CYCLE_NOT_RETAINED,
    "AT-031" to ReleaseBlocker.OWNER_RELEASE_DECISION_NOT_RETAINED,
)

internal fun assessRepositoryRelease(repositoryRoot: File): ReleaseAssessment {
    val seedFile = File(repositoryRoot, "app/src/main/assets/content/bundled-content.json")
    val manifestFile = File(repositoryRoot, RELEASE_EVIDENCE_MANIFEST)
    val seed = if (seedFile.isFile) seedFile.readText() else null
    val manifest = if (manifestFile.isFile) manifestFile.readText() else null
    return assessPackagedRelease(seed, manifest, repositoryRoot)
}

internal fun assessPackagedRelease(
    packagedSeed: String?,
    evidenceManifest: String?,
    repositoryRoot: File,
): ReleaseAssessment {
    val prepared = when {
        packagedSeed == null -> null
        else -> when (val parsed = SeedJson.parse(packagedSeed)) {
            SeedParse.Corrupt -> null
            is SeedParse.Ok -> prepareBundledContent(parsed.document)
        }
    }
    val blockers = buildList {
        if (prepared == null) {
            add(ReleaseBlocker.CONTENT_UNREADABLE)
        } else {
            addAll(contentBlockers(prepared))
        }
        addAll(evidenceBlockers(evidenceManifest, repositoryRoot))
    }
    val decision = if (blockers.isEmpty()) ReleaseDecision.APPROVED else ReleaseDecision.NOT_APPROVED
    return ReleaseAssessment(decision, blockers)
}

internal const val RELEASE_EVIDENCE_MANIFEST = "release/retained-evidence.json"

private const val EVIDENCE_PATH_PREFIX = "release/evidence/"

fun main() {
    val workingDirectory = System.getProperty("user.dir")
    if (workingDirectory.isNullOrBlank()) {
        System.err.println("Public release is not approved.")
        exitProcess(1)
    }
    val repositoryRoot = File(workingDirectory)
    val assessment = assessRepositoryRelease(repositoryRoot)
    val report = File(repositoryRoot, "app/build/reports/release-gate.txt")
    report.parentFile?.mkdirs()
    report.writeText(assessment.report() + "\n")
    if (assessment.decision != ReleaseDecision.APPROVED) {
        System.err.println("Public release is not approved.")
        System.err.println(assessment.report())
        exitProcess(1)
    }
}

private fun contentBlockers(prepared: PreparedBundle): List<ReleaseBlocker> {
    val blockers = mutableListOf<ReleaseBlocker>()
    if (prepared.segments.none { it.eligibilityStatus == EligibilityStatus.ELIGIBLE }) {
        blockers += ReleaseBlocker.PATHWAY_NOT_INSTALLED
    }
    val requiredPlans = setOf(
        PlanActivityType.BOOK,
        PlanActivityType.ANKI_DROID,
        PlanActivityType.LISTEN_RECORD_COMPARE,
    )
    val installedPlans = prepared.plans
        .filter { it.eligibilityStatus == EligibilityStatus.ELIGIBLE && it.required }
        .map { it.activityType }
        .toSet()
    if (!installedPlans.containsAll(requiredPlans)) {
        blockers += ReleaseBlocker.PLAN_DEFINITIONS_NOT_INSTALLED
    }
    if (prepared.gates.none { it.eligibilityStatus == EligibilityStatus.ELIGIBLE }) {
        blockers += ReleaseBlocker.DEFAULT_GATE_NOT_INSTALLED
    }
    val eligibleAudio = prepared.audio.filter { it.eligibilityStatus == EligibilityStatus.ELIGIBLE }
    if (eligibleAudio.isEmpty()) {
        blockers += ReleaseBlocker.LICENSED_AUDIO_NOT_PREPARED
    }
    val rightsIncomplete = eligibleAudio.any { model ->
        val rights = prepared.rights.firstOrNull { it.id == model.rightsRecordId }
        val text = prepared.texts.firstOrNull { it.id == model.sourceTextId }
        val source = prepared.sources.firstOrNull { it.id == model.sourceRecordId }
        rights == null ||
            text == null ||
            source == null ||
            rights.licenseStatus != LicenseStatus.COMPATIBLE ||
            rights.inclusionResult != InclusionResult.INCLUDED ||
            !text.matchVerified
    }
    if (prepared.sources.isEmpty() || prepared.rights.isEmpty() || rightsIncomplete) {
        blockers += ReleaseBlocker.RIGHTS_RECORDS_ABSENT
    }
    if (prepared.sources.any { it.shareAlikeConfirmation == ShareAlikeConfirmation.UNCONFIRMED }) {
        blockers += ReleaseBlocker.SHARE_ALIKE_UNCONFIRMED
    }
    return blockers
}

private fun evidenceBlockers(manifest: String?, repositoryRoot: File): List<ReleaseBlocker> {
    val reading = readEvidence(manifest, repositoryRoot)
    val blockers = mutableListOf<ReleaseBlocker>()
    if (!reading.readable) blockers += ReleaseBlocker.EVIDENCE_MANIFEST_UNREADABLE
    for ((procedure, blocker) in requiredProcedures) {
        if (procedure !in reading.retained) blockers += blocker
    }
    return blockers
}

private data class EvidenceReading(
    val readable: Boolean,
    val retained: Set<String>,
)

private fun readEvidence(manifest: String?, repositoryRoot: File): EvidenceReading {
    if (manifest == null) return EvidenceReading(readable = false, retained = emptySet())
    val parsed = try {
        evidenceJson.decodeFromString(RetainedEvidenceManifest.serializer(), manifest)
    } catch (_: SerializationException) {
        return EvidenceReading(readable = false, retained = emptySet())
    } catch (_: IllegalArgumentException) {
        return EvidenceReading(readable = false, retained = emptySet())
    }
    if (parsed.schemaVersion != 1) return EvidenceReading(readable = false, retained = emptySet())
    val procedures = parsed.records.map { it.procedure }
    if (procedures.size != procedures.toSet().size) {
        return EvidenceReading(readable = false, retained = emptySet())
    }
    val retained = requiredProcedures.map { it.first }.filter { procedure ->
        val record = parsed.records.firstOrNull { it.procedure == procedure } ?: return@filter false
        isRetained(record, repositoryRoot)
    }.toSet()
    return EvidenceReading(readable = true, retained = retained)
}

private fun isRetained(record: RetainedEvidenceRecord, repositoryRoot: File): Boolean {
    if (record.state != "RETAINED") return false
    val relative = record.evidencePath ?: return false
    if (!relative.startsWith(EVIDENCE_PATH_PREFIX)) return false
    val file = resolveEvidenceFile(repositoryRoot, relative) ?: return false
    return file.isFile && file.length() > 0L
}

private fun resolveEvidenceFile(repositoryRoot: File, relative: String): File? {
    if (relative.isBlank()) return null
    if (relative.startsWith("/") || relative.startsWith("\\") || relative.contains('\\')) return null
    if (relative.contains(':')) return null
    val parts = relative.split('/')
    if (parts.any { it.isBlank() || it == "." || it == ".." }) return null
    val root = repositoryRoot.canonicalFile
    val candidate = File(root, relative).canonicalFile
    val rootPath = root.path.trimEnd('/') + "/"
    if (!candidate.path.startsWith(rootPath)) return null
    return candidate
}
