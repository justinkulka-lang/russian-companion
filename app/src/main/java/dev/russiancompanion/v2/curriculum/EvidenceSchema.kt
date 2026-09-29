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

internal const val RETAINED_BYTE_STATUS = "RETAINED"

private val SHA256_HEX = Regex("^[0-9a-f]{64}$")
private val ISO_DATE = Regex("^\\d{4}-\\d{2}-\\d{2}$")
private val PLACEHOLDER = Regex(
    "(?i)(\\bnot yet passed\\b|\\btodo\\b|\\btbd\\b|\\bplaceholder\\b|\\bnone\\b|\\bpending\\b|\\bn/a\\b|^na$|\\bunspecified\\b)",
)

internal data class EvidenceRecord(
    val evidenceClass: String? = null,
    val quotedSource: V2QuotedSourceEvidence? = null,
    val originalSynthesis: V2OriginalSynthesisEvidence? = null,
)

internal data class ApprovalAttempt(
    val stage: String,
    val priorStages: List<String> = emptyList(),
    val reviewedContentDigest: String? = null,
    val currentContentDigest: String? = null,
    val evidence: EvidenceRecord? = null,
    val reviewFinalAudit: V2FinalAuditIdentity? = null,
    val ciGreen: Boolean = false,
    val countersComplete: Boolean = false,
    val selfAttested: Boolean = false,
    val modelName: String? = null,
)

internal fun isSha256(value: String?): Boolean = value != null && SHA256_HEX.matches(value)

internal fun isSubstantive(value: String?): Boolean {
    if (value.isNullOrBlank()) return false
    return !PLACEHOLDER.containsMatchIn(value.trim())
}

internal fun V2PersistedEvidence.toEvidenceRecord(): EvidenceRecord = EvidenceRecord(
    evidenceClass = evidenceClass.ifBlank { null },
    quotedSource = quotedSource,
    originalSynthesis = originalSynthesis,
)

internal fun finalAuditDefects(audit: V2FinalAuditIdentity?, governedDigest: String?): List<String> {
    if (audit == null) return listOf("final curriculum audit artifact is absent")
    val missing = mutableListOf<String>()
    if (!isSubstantive(audit.artifactIdentity)) missing += "retained audit artifact"
    if (!isSha256(audit.artifactDigest)) missing += "audit artifact digest"
    if (!isSha256(audit.auditedContentDigest)) missing += "audited content digest"
    else if (!governedDigest.isNullOrBlank() && audit.auditedContentDigest != governedDigest) {
        missing += "audited content digest mismatch"
    }
    if (audit.disposition != FINAL_CURRICULUM_AUDIT_PASSED) {
        missing += "audit disposition $FINAL_CURRICULUM_AUDIT_PASSED"
    }
    if (!isSubstantive(audit.modelVersion)) missing += "model version"
    if (!ISO_DATE.matches(audit.date) || !isSubstantive(audit.date)) missing += "audit date"
    return if (missing.isEmpty()) emptyList() else listOf("final audit is missing or invalid: ${missing.joinToString(", ")}")
}

internal fun evidenceDefects(
    record: EvidenceRecord?,
    governedDigest: String? = record?.originalSynthesis?.finalAudit?.auditedContentDigest,
    reviewFinalAudit: V2FinalAuditIdentity? = null,
): List<String> {
    if (record == null || record.evidenceClass.isNullOrBlank()) {
        return listOf("evidence class is not recorded")
    }
    if (record.evidenceClass !in EVIDENCE_CLASSES) {
        return listOf("evidence class ${record.evidenceClass} is not recognized")
    }
    val quoted = record.quotedSource
    val synthesis = record.originalSynthesis
    if (quoted == null && synthesis == null) {
        return listOf("evidence class label has no persisted evidence record")
    }
    if (quoted != null && synthesis != null) {
        return listOf("evidence record is contradictory")
    }
    if (record.evidenceClass == QUOTED_OR_ADAPTED_SOURCE && quoted == null) {
        return listOf("evidence record is contradictory")
    }
    if (record.evidenceClass == ORIGINAL_RESEARCH_SYNTHESIS && synthesis == null) {
        return listOf("evidence record is contradictory")
    }
    val missing = mutableListOf<String>()
    if (quoted != null) {
        if (!isSubstantive(quoted.sourceIdentity)) missing += "immutable source identity"
        if (quoted.retainedByteStatus != RETAINED_BYTE_STATUS) missing += "retained-byte status"
        if (!isSha256(quoted.retainedByteIdentity)) missing += "retained-byte identity"
        if (!isSubstantive(quoted.exactSpanOrAssetHash)) missing += "exact span or asset hash"
        if (!isSubstantive(quoted.licenseOrRights)) missing += "license or rights"
        if (!isSubstantive(quoted.transformation)) missing += "transformation"
        if (!isSubstantive(quoted.attribution)) missing += "attribution"
        if (!isSubstantive(quoted.claimBinding)) missing += "claim binding"
    }
    if (synthesis != null) {
        if (!isSubstantive(synthesis.bibliography)) missing += "claim-level bibliography"
        if (!isSubstantive(synthesis.sourceDateOrVersion)) missing += "source date or version"
        if (!isSubstantive(synthesis.authorshipDeclaration)) missing += "authorship declaration"
        if (!isSubstantive(synthesis.objectiveOrClaimBinding)) missing += "governed objective or claim binding"
        val auditDefects = finalAuditDefects(synthesis.finalAudit, governedDigest)
        if (auditDefects.isNotEmpty()) missing += "final curriculum audit"
        if (reviewFinalAudit != null && synthesis.finalAudit != reviewFinalAudit) {
            missing += "final audit identity contradicts the review record"
        }
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
    if (attempt.stage !in REVIEW_CHAIN &&
        attempt.stage != "APPROVED" &&
        attempt.stage != "REJECTED" &&
        attempt.stage != "NOT_REQUIRED"
    ) {
        defects += "stage ${attempt.stage} is not a review-chain stage"
    }
    if (OWNER_ACCEPTED in attempt.priorStages) {
        defects += "OWNER_ACCEPTED appears in prior stages"
    }
    val unknown = attempt.priorStages.filter { it !in REVIEW_CHAIN }.distinct()
    if (unknown.isNotEmpty()) {
        defects += "prior stages contain unknown stages ${unknown.joinToString(", ")}"
    }
    if (attempt.priorStages.size != attempt.priorStages.distinct().size) {
        defects += "prior stages contain duplicates"
    }
    if (attempt.stage in REVIEW_CHAIN) {
        val required = requiredPriorStages(attempt.stage)
        val prefixRecorded = attempt.priorStages.isNotEmpty() || attempt.stage in DOWNSTREAM_AUDIT_STAGES
        if (prefixRecorded && attempt.priorStages != required) {
            defects += "stage ${attempt.stage} prior stages are not the exact required prefix"
        }
    }
    if (attempt.stage in DOWNSTREAM_AUDIT_STAGES) {
        if (!isSha256(attempt.reviewedContentDigest) || !isSha256(attempt.currentContentDigest)) {
            defects += "stage ${attempt.stage} is missing a governed content digest"
        } else if (attempt.reviewedContentDigest != attempt.currentContentDigest) {
            defects += "stage ${attempt.stage} digest does not match the governed content"
        }
        defects += finalAuditDefects(attempt.reviewFinalAudit, attempt.currentContentDigest)
        val evidenceAudit = attempt.evidence?.originalSynthesis?.finalAudit
        if (attempt.evidence?.evidenceClass == ORIGINAL_RESEARCH_SYNTHESIS &&
            attempt.reviewFinalAudit != null &&
            evidenceAudit != attempt.reviewFinalAudit
        ) {
            defects += "final audit identity contradicts the evidence record"
        }
    }
    if (enforceEvidence && attempt.stage == OWNER_ACCEPTED) {
        defects += evidenceDefects(attempt.evidence, attempt.currentContentDigest, attempt.reviewFinalAudit)
    }
    return defects
}

internal fun approvalGranted(attempt: ApprovalAttempt): Boolean {
    if (attempt.stage != OWNER_ACCEPTED) return false
    return reviewChainDefects(attempt).isEmpty()
}

internal fun reviewAcceptanceAttempt(
    status: String,
    priorStages: List<String>,
    reviewedContentDigest: String?,
    currentContentDigest: String,
    evidence: V2PersistedEvidence?,
    finalAudit: V2FinalAuditIdentity?,
): ApprovalAttempt = ApprovalAttempt(
    stage = status,
    priorStages = priorStages,
    reviewedContentDigest = reviewedContentDigest,
    currentContentDigest = currentContentDigest,
    evidence = evidence?.toEvidenceRecord(),
    reviewFinalAudit = finalAudit,
)

internal fun reviewReleaseAccepted(
    status: String,
    priorStages: List<String>,
    reviewedContentDigest: String?,
    currentContentDigest: String,
    evidence: V2PersistedEvidence?,
    finalAudit: V2FinalAuditIdentity?,
): Boolean = approvalGranted(
    reviewAcceptanceAttempt(
        status,
        priorStages,
        reviewedContentDigest,
        currentContentDigest,
        evidence,
        finalAudit,
    ),
)

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
