package dev.russiancompanion.data.local

import dev.russiancompanion.domain.gate.ConditionOutcome
import dev.russiancompanion.domain.gate.GateComparison
import dev.russiancompanion.domain.gate.GateConditionView
import dev.russiancompanion.domain.gate.GateDetailView
import dev.russiancompanion.domain.gate.GateEvaluation
import dev.russiancompanion.domain.gate.GateEvidenceKind
import dev.russiancompanion.domain.gate.GateEvidenceView
import dev.russiancompanion.domain.gate.GateRuleCondition
import dev.russiancompanion.domain.gate.GateStatus
import dev.russiancompanion.domain.gate.RecordedEvidence
import dev.russiancompanion.domain.gate.decodeConditionOutcomes
import dev.russiancompanion.domain.gate.displayedRuleText
import dev.russiancompanion.domain.gate.encodeConditionOutcomes
import dev.russiancompanion.domain.gate.encodeEvidenceIds
import dev.russiancompanion.domain.gate.encodeGateRuleSnapshot
import dev.russiancompanion.domain.gate.evaluateGate
import dev.russiancompanion.domain.gate.evidenceEntryAccepted
import dev.russiancompanion.domain.gate.gateIsLocked
import dev.russiancompanion.domain.gate.gateRuleSnapshot
import dev.russiancompanion.domain.gate.parseEvidenceIds
import dev.russiancompanion.domain.gate.remainingKinds
import dev.russiancompanion.domain.gate.reproduceDecision
import dev.russiancompanion.domain.gate.tutorSignOffCondition
import java.util.UUID

internal fun ruleConditions(
    conditions: List<GateConditionEntity>,
    tutorRequired: Boolean,
): List<GateRuleCondition> {
    val shipped = conditions.sortedBy { it.sortOrder }.map { condition ->
        GateRuleCondition(
            id = condition.id,
            kind = condition.evidenceType.toKind(),
            comparison = condition.operator.toComparison(),
            operand = condition.operand,
        )
    }
    return if (tutorRequired) shipped + tutorSignOffCondition() else shipped
}

internal fun evidenceAcceptedForGate(
    evidenceType: EvidenceType,
    enteredValue: String,
    conditions: List<GateConditionEntity>,
    tutorRequired: Boolean,
): Boolean {
    val comparisons = comparisonsFor(evidenceType, conditions, tutorRequired)
    if (comparisons.isEmpty()) return false
    val kind = evidenceType.toKind()
    return comparisons.all { evidenceEntryAccepted(it, enteredValue, kind) }
}

internal fun gateDetailView(
    gate: GateDefinitionEntity,
    conditions: List<GateConditionEntity>,
    tutorRequired: Boolean,
    evidence: List<GateEvidenceEntity>,
    recordedStatus: GateDecisionStatus?,
): GateDetailView {
    val rule = ruleConditions(conditions, tutorRequired)
    val evaluation = evaluateGate(rule, evidence.map { it.toRecorded() })
    val evidenceById = evidence.associateBy { it.id }
    return GateDetailView(
        id = gate.id,
        displayedRuleText = displayedRuleText(rule),
        tutorRequired = tutorRequired,
        recordedStatus = recordedStatus?.toGateStatus(),
        locked = gateIsLocked(evaluation),
        remainingKinds = remainingKinds(evaluation, rule),
        conditions = rule.zip(evaluation.outcomes).map { (condition, outcome) ->
            val latest = outcome.evidenceId?.let(evidenceById::get)
            GateConditionView(
                id = condition.id,
                kind = condition.kind,
                comparison = condition.comparison,
                operand = condition.operand,
                result = outcome.result,
                latestValue = latest?.enteredValue,
                latestRecordedAt = latest?.recordedAtLocal,
            )
        },
        evidence = evidence.map { record ->
            GateEvidenceView(
                id = record.id,
                kind = record.evidenceType.toKind(),
                enteredValue = record.enteredValue,
                selfAttested = record.selfAttestationStatus == SelfAttestationStatus.SELF_ATTESTED,
                recordedAtLocal = record.recordedAtLocal,
            )
        },
    )
}

internal fun evidenceType(kind: GateEvidenceKind): EvidenceType {
    return when (kind) {
        GateEvidenceKind.BOOK_REVISION_SCORE -> EvidenceType.BOOK_REVISION_SCORE
        GateEvidenceKind.ANKI_DROID_STATUS -> EvidenceType.ANKI_DROID_STATUS
        GateEvidenceKind.PRESCRIBED_WORK_COMPLETION -> EvidenceType.PRESCRIBED_WORK_COMPLETION
        GateEvidenceKind.TUTOR_SIGN_OFF -> EvidenceType.TUTOR_SIGN_OFF
    }
}

internal fun GateDecisionStatus.toGateStatus(): GateStatus {
    return when (this) {
        GateDecisionStatus.SATISFIED -> GateStatus.SATISFIED
        GateDecisionStatus.NOT_YET_SATISFIED -> GateStatus.NOT_YET_SATISFIED
        GateDecisionStatus.NOT_EVALUABLE -> GateStatus.NOT_EVALUABLE
    }
}

internal fun reproduceStoredDecision(
    decision: GateDecisionEntity,
    evidence: List<GateEvidenceEntity>,
): GateEvaluation {
    return reproduceDecision(
        ruleSnapshot = decision.ruleSnapshot,
        recordedEvidenceIds = parseEvidenceIds(decision.evidenceIds),
        evidence = evidence.map { it.toRecorded() },
    )
}

internal fun storedOutcomes(decision: GateDecisionEntity): List<ConditionOutcome> {
    return decodeConditionOutcomes(decision.conditionOutcomes)
}

internal fun storedGateDecision(
    gate: GateDefinitionEntity,
    conditions: List<GateConditionEntity>,
    tutorRequired: Boolean,
    evidence: List<GateEvidenceEntity>,
    evaluatedAtLocal: String,
): GateDecisionEntity {
    val rule = ruleConditions(conditions, tutorRequired)
    val evaluation = evaluateGate(
        conditions = rule,
        evidence = evidence.map { it.toRecorded() },
    )
    return evaluation.toDecision(gate, rule, evaluatedAtLocal)
}

private fun comparisonsFor(
    evidenceType: EvidenceType,
    conditions: List<GateConditionEntity>,
    tutorRequired: Boolean,
): List<GateComparison> {
    if (evidenceType == EvidenceType.TUTOR_SIGN_OFF) {
        return if (tutorRequired) listOf(GateComparison.PRESENT) else emptyList()
    }
    return conditions.filter { it.evidenceType == evidenceType }.map { it.operator.toComparison() }
}

private fun GateEvidenceEntity.toRecorded(): RecordedEvidence {
    return RecordedEvidence(
        id = id,
        kind = evidenceType.toKind(),
        enteredValue = enteredValue,
        recordedAtLocal = recordedAtLocal,
    )
}

private fun GateEvaluation.toDecision(
    gate: GateDefinitionEntity,
    rule: List<GateRuleCondition>,
    evaluatedAtLocal: String,
): GateDecisionEntity {
    val snapshot = gateRuleSnapshot(gate.ruleVersion, rule)
    return GateDecisionEntity(
        id = UUID.randomUUID().toString(),
        gateDefinitionId = gate.id,
        ruleVersion = snapshot.version,
        ruleSnapshot = encodeGateRuleSnapshot(snapshot),
        evidenceIds = encodeEvidenceIds(outcomes.mapNotNull { it.evidenceId }),
        conditionOutcomes = encodeConditionOutcomes(outcomes),
        status = when (status) {
            GateStatus.SATISFIED -> GateDecisionStatus.SATISFIED
            GateStatus.NOT_YET_SATISFIED -> GateDecisionStatus.NOT_YET_SATISFIED
            GateStatus.NOT_EVALUABLE -> GateDecisionStatus.NOT_EVALUABLE
        },
        evaluatedAtLocal = evaluatedAtLocal,
    )
}

private fun EvidenceType.toKind(): GateEvidenceKind {
    return when (this) {
        EvidenceType.BOOK_REVISION_SCORE -> GateEvidenceKind.BOOK_REVISION_SCORE
        EvidenceType.ANKI_DROID_STATUS -> GateEvidenceKind.ANKI_DROID_STATUS
        EvidenceType.PRESCRIBED_WORK_COMPLETION -> GateEvidenceKind.PRESCRIBED_WORK_COMPLETION
        EvidenceType.TUTOR_SIGN_OFF -> GateEvidenceKind.TUTOR_SIGN_OFF
    }
}

private fun RuleOperator.toComparison(): GateComparison {
    return when (this) {
        RuleOperator.PRESENT -> GateComparison.PRESENT
        RuleOperator.AT_LEAST -> GateComparison.AT_LEAST
        RuleOperator.EQUALS -> GateComparison.EQUALS
    }
}
