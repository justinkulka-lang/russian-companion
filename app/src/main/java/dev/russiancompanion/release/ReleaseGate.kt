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
    OFFLINE_VERIFICATION_NOT_RETAINED("Verified offline operation is not retained."),
    REBOOT_EVIDENCE_NOT_RETAINED("Device reboot evidence is not retained."),
    LESSON_CYCLE_NOT_RETAINED("The full lesson-cycle test is not retained."),
}

internal data class RetainedReleaseEvidence(
    val offlineVerification: Boolean = false,
    val deviceReboot: Boolean = false,
    val lessonCycle: Boolean = false,
)

internal data class ReleaseAssessment(
    val decision: ReleaseDecision,
    val blockers: List<ReleaseBlocker>,
) {
    fun report(): String {
        return (listOf(decision.name) + blockers.map { it.message }).joinToString("\n")
    }
}

internal fun assessPackagedRelease(
    packagedSeed: String?,
    evidence: RetainedReleaseEvidence = RetainedReleaseEvidence(),
): ReleaseAssessment {
    val prepared = when {
        packagedSeed == null -> null
        else -> when (val parsed = SeedJson.parse(packagedSeed)) {
            SeedParse.Corrupt -> null
            is SeedParse.Ok -> prepareBundledContent(parsed.document)
        }
    }
    val blockers = if (prepared == null) {
        listOf(ReleaseBlocker.CONTENT_UNREADABLE)
    } else {
        contentBlockers(prepared) + evidenceBlockers(evidence)
    }
    val decision = if (blockers.isEmpty()) ReleaseDecision.APPROVED else ReleaseDecision.NOT_APPROVED
    return ReleaseAssessment(decision, blockers)
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

private fun evidenceBlockers(evidence: RetainedReleaseEvidence): List<ReleaseBlocker> {
    val blockers = mutableListOf<ReleaseBlocker>()
    if (!evidence.offlineVerification) blockers += ReleaseBlocker.OFFLINE_VERIFICATION_NOT_RETAINED
    if (!evidence.deviceReboot) blockers += ReleaseBlocker.REBOOT_EVIDENCE_NOT_RETAINED
    if (!evidence.lessonCycle) blockers += ReleaseBlocker.LESSON_CYCLE_NOT_RETAINED
    return blockers
}
