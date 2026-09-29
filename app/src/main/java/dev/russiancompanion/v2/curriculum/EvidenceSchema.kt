package dev.russiancompanion.v2.curriculum

/**
 * Amendment 1 review chain and evidence classes.
 * A pending stage, green CI, a counter, self-attestation, or a model name
 * does not approve curriculum, a mapping, or a variance.
 */
internal const val RESEARCH_VERIFIED = "RESEARCH_VERIFIED"
internal const val IMPLEMENTED_PENDING_FINAL_AUDIT = "IMPLEMENTED_PENDING_FINAL_AUDIT"
internal const val FINAL_CURRICULUM_AUDIT_PASSED = "FINAL_CURRICULUM_AUDIT_PASSED"
internal const val INDEPENDENT_AUDIT_VERIFIED = "INDEPENDENT_AUDIT_VERIFIED"
internal const val OWNER_ACCEPTED = "OWNER_ACCEPTED"

internal const val QUOTED_OR_ADAPTED_SOURCE = "QUOTED_OR_ADAPTED_SOURCE"
internal const val ORIGINAL_RESEARCH_SYNTHESIS = "ORIGINAL_RESEARCH_SYNTHESIS"

internal val REVIEW_CHAIN = listOf(
    RESEARCH_VERIFIED,
    IMPLEMENTED_PENDING_FINAL_AUDIT,
    FINAL_CURRICULUM_AUDIT_PASSED,
    INDEPENDENT_AUDIT_VERIFIED,
    OWNER_ACCEPTED,
)

internal val PRIOR_REVIEW_STAGES = REVIEW_CHAIN.dropLast(1)

internal val DOWNSTREAM_AUDIT_STAGES = setOf(
    FINAL_CURRICULUM_AUDIT_PASSED,
    INDEPENDENT_AUDIT_VERIFIED,
    OWNER_ACCEPTED,
)

internal val EVIDENCE_CLASSES = setOf(
    QUOTED_OR_ADAPTED_SOURCE,
    ORIGINAL_RESEARCH_SYNTHESIS,
)

internal val EVIDENCE_REVIEW_STATES = REVIEW_CHAIN.toSet() + setOf(
    "APPROVED",
    "REJECTED",
    "NOT_REQUIRED",
)

internal val CLOSED_REVIEW_STATES = EVIDENCE_REVIEW_STATES

internal data class EvidenceRecord(
    val evidenceClass: String? = null,
    val sourceIdentity: String? = null,
    val retainedBytes: Boolean = false,
    val exactSpanOrAssetHash: String? = null,
    val licenseOrRights: String? = null,
    val transformation: String? = null,
    val attribution: String? = null,
    val claimBinding: String? = null,
    val bibliography: String? = null,
    val sourceDateOrVersion: String? = null,
    val authorshipDeclaration: String? = null,
    val objectiveOrClaimBinding: String? = null,
    val finalCurriculumAudit: String? = null,
)

internal data class ApprovalAttempt(
    val stage: String,
    val priorStages: List<String> = emptyList(),
    val reviewedContentDigest: String? = null,
    val currentContentDigest: String? = null,
    val evidence: EvidenceRecord? = null,
    val ciGreen: Boolean = false,
    val countersComplete: Boolean = false,
    val selfAttested: Boolean = false,
    val modelName: String? = null,
)

internal fun evidenceDefects(record: EvidenceRecord?): List<String> {
    if (record == null || record.evidenceClass.isNullOrBlank()) {
        return listOf("evidence class is not recorded")
    }
    if (record.evidenceClass !in EVIDENCE_CLASSES) {
        return listOf("evidence class ${record.evidenceClass} is not recognized")
    }
    val missing = mutableListOf<String>()
    if (record.evidenceClass == QUOTED_OR_ADAPTED_SOURCE) {
        if (record.sourceIdentity.isNullOrBlank()) missing += "immutable source identity"
        if (!record.retainedBytes) missing += "retained bytes"
        if (record.exactSpanOrAssetHash.isNullOrBlank()) missing += "exact span or asset hash"
        if (record.licenseOrRights.isNullOrBlank()) missing += "license or rights"
        if (record.transformation.isNullOrBlank()) missing += "transformation"
        if (record.attribution.isNullOrBlank()) missing += "attribution"
        if (record.claimBinding.isNullOrBlank()) missing += "claim binding"
    }
    if (record.evidenceClass == ORIGINAL_RESEARCH_SYNTHESIS) {
        if (record.bibliography.isNullOrBlank()) missing += "claim-level bibliography"
        if (record.sourceDateOrVersion.isNullOrBlank()) missing += "source date or version"
        if (record.authorshipDeclaration.isNullOrBlank()) missing += "authorship declaration"
        if (record.objectiveOrClaimBinding.isNullOrBlank()) missing += "governed objective or claim binding"
        if (record.finalCurriculumAudit.isNullOrBlank()) missing += "final curriculum audit"
    }
    return if (missing.isEmpty()) emptyList() else listOf("evidence is missing ${missing.joinToString(", ")}")
}

internal fun requiredPriorStages(stage: String): List<String> {
    val index = REVIEW_CHAIN.indexOf(stage)
    if (index <= 0) return emptyList()
    return REVIEW_CHAIN.take(index)
}

internal fun automatedSignalsGrantApproval(
    ciGreen: Boolean,
    countersComplete: Boolean,
    selfAttested: Boolean,
    modelName: String?,
): Boolean {
    if (ciGreen || countersComplete || selfAttested || !modelName.isNullOrBlank()) return false
    return false
}

internal fun reviewChainDefects(attempt: ApprovalAttempt, enforceEvidence: Boolean = true): List<String> {
    val defects = mutableListOf<String>()
    if (attempt.stage !in REVIEW_CHAIN && attempt.stage != "APPROVED") {
        defects += "stage ${attempt.stage} is not a review-chain stage"
    }
    val required = if (attempt.stage in DOWNSTREAM_AUDIT_STAGES) requiredPriorStages(attempt.stage) else emptyList()
    if (required.isNotEmpty() && !attempt.priorStages.containsAll(required)) {
        defects += "stage ${attempt.stage} is missing prior stages ${required.filter { it !in attempt.priorStages }}"
    }
    if (attempt.stage in DOWNSTREAM_AUDIT_STAGES) {
        if (attempt.reviewedContentDigest.isNullOrBlank() || attempt.currentContentDigest.isNullOrBlank()) {
            defects += "stage ${attempt.stage} is missing a content digest"
        } else if (attempt.reviewedContentDigest != attempt.currentContentDigest) {
            defects += "stage ${attempt.stage} digest does not match the governed content"
        }
    }
    if (enforceEvidence && attempt.stage == OWNER_ACCEPTED) {
        defects += evidenceDefects(attempt.evidence)
    }
    return defects
}

internal fun approvalGranted(attempt: ApprovalAttempt): Boolean {
    if (attempt.stage != OWNER_ACCEPTED) return false
    if (reviewChainDefects(attempt).isNotEmpty()) return false
    if (attempt.reviewedContentDigest.isNullOrBlank()) return false
    if (attempt.reviewedContentDigest != attempt.currentContentDigest) return false
    if (!attempt.priorStages.containsAll(PRIOR_REVIEW_STAGES)) return false
    return evidenceDefects(attempt.evidence).isEmpty()
}

internal fun releaseLinguisticReviewSatisfied(
    status: String,
    reviewedContentDigest: String?,
    reviewChainStages: List<String>,
): Boolean {
    if (status != OWNER_ACCEPTED) return false
    if (reviewedContentDigest.isNullOrBlank()) return false
    return reviewChainStages.containsAll(PRIOR_REVIEW_STAGES)
}

internal fun downstreamAuditRemainsBound(
    recordedDigest: String,
    currentDigest: String,
    stage: String,
): Boolean {
    if (stage !in DOWNSTREAM_AUDIT_STAGES) return true
    return recordedDigest.isNotBlank() && recordedDigest == currentDigest
}

internal fun mappingApprovedBySignals(
    ciGreen: Boolean,
    countersComplete: Boolean,
    selfAttested: Boolean,
    ownerDispositionRecorded: Boolean,
): Boolean {
    if (automatedSignalsGrantApproval(ciGreen, countersComplete, selfAttested, null)) return false
    return ownerDispositionRecorded
}

internal fun varianceActivationPermitted(
    ciGreen: Boolean,
    exactSequenceDifferenceRecorded: Boolean,
    boundedResolutionRecorded: Boolean,
    finalAuditPassed: Boolean,
    ownerAccepted: Boolean,
    versionedDigest: String?,
): Boolean {
    if (ciGreen && !ownerAccepted) return false
    if (!exactSequenceDifferenceRecorded) return false
    if (!boundedResolutionRecorded) return false
    if (!finalAuditPassed) return false
    if (!ownerAccepted) return false
    if (versionedDigest.isNullOrBlank()) return false
    return true
}
