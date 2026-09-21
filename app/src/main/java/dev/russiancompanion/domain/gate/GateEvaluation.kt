package dev.russiancompanion.domain.gate

import java.math.BigDecimal

internal enum class GateEvidenceKind {
    BOOK_REVISION_SCORE,
    ANKI_DROID_STATUS,
    PRESCRIBED_WORK_COMPLETION,
    TUTOR_SIGN_OFF,
}

internal enum class GateComparison {
    PRESENT,
    AT_LEAST,
    EQUALS,
}

internal enum class ConditionResult {
    SATISFIED,
    NOT_SATISFIED,
    AWAITING,
    UNREADABLE,
}

internal enum class GateStatus {
    SATISFIED,
    NOT_YET_SATISFIED,
    NOT_EVALUABLE,
}

internal data class GateRuleCondition(
    val id: String,
    val kind: GateEvidenceKind,
    val comparison: GateComparison,
    val operand: String?,
)

internal data class RecordedEvidence(
    val id: String,
    val kind: GateEvidenceKind,
    val enteredValue: String,
    val recordedAtLocal: String,
)

internal data class ConditionOutcome(
    val conditionId: String,
    val evidenceId: String?,
    val result: ConditionResult,
)

internal data class GateEvaluation(
    val status: GateStatus,
    val outcomes: List<ConditionOutcome>,
)

internal data class GateEvidenceEntry(
    val kind: GateEvidenceKind,
    val enteredValue: String,
)

internal data class GateConditionView(
    val id: String,
    val kind: GateEvidenceKind,
    val comparison: GateComparison,
    val operand: String?,
    val result: ConditionResult,
    val latestValue: String?,
    val latestRecordedAt: String?,
)

internal data class GateEvidenceView(
    val id: String,
    val kind: GateEvidenceKind,
    val enteredValue: String,
    val selfAttested: Boolean,
    val recordedAtLocal: String,
)

internal data class GateDetailView(
    val id: String,
    val displayedRuleText: String,
    val tutorRequired: Boolean,
    val recordedStatus: GateStatus?,
    val conditions: List<GateConditionView>,
    val evidence: List<GateEvidenceView>,
)

internal const val USER_ADDED_TUTOR_CONDITION_ID = "user-added-tutor-sign-off"

internal fun tutorSignOffCondition(): GateRuleCondition {
    return GateRuleCondition(
        id = USER_ADDED_TUTOR_CONDITION_ID,
        kind = GateEvidenceKind.TUTOR_SIGN_OFF,
        comparison = GateComparison.PRESENT,
        operand = null,
    )
}

internal fun evidenceEntryAccepted(comparison: GateComparison, enteredValue: String): Boolean {
    val value = enteredValue.trim()
    if (value.isEmpty()) return false
    return when (comparison) {
        GateComparison.PRESENT,
        GateComparison.EQUALS,
        -> true
        GateComparison.AT_LEAST -> parseRuleNumber(value) != null
    }
}

/**
 * Applies the stored conditions to recorded evidence.
 * The newest record for each evidence kind is the one that counts.
 * Comparison is numeric or exact text only.
 */
internal fun evaluateGate(
    conditions: List<GateRuleCondition>,
    evidence: List<RecordedEvidence>,
): GateEvaluation {
    val outcomes = conditions.map { condition ->
        val latest = evidence
            .filter { it.kind == condition.kind }
            .maxWithOrNull(compareBy({ it.recordedAtLocal }, { it.id }))
        ConditionOutcome(
            conditionId = condition.id,
            evidenceId = latest?.id,
            result = if (latest == null) ConditionResult.AWAITING else compare(condition, latest.enteredValue),
        )
    }
    val status = when {
        outcomes.isEmpty() -> GateStatus.NOT_EVALUABLE
        outcomes.any { it.result == ConditionResult.AWAITING || it.result == ConditionResult.UNREADABLE } ->
            GateStatus.NOT_EVALUABLE
        outcomes.any { it.result == ConditionResult.NOT_SATISFIED } -> GateStatus.NOT_YET_SATISFIED
        else -> GateStatus.SATISFIED
    }
    return GateEvaluation(status = status, outcomes = outcomes)
}

internal fun encodeConditionOutcomes(outcomes: List<ConditionOutcome>): String {
    return outcomes.joinToString(separator = "\n") { outcome ->
        listOf(outcome.conditionId, outcome.evidenceId.orEmpty(), outcome.result.name).joinToString(separator = "\t")
    }
}

private fun compare(condition: GateRuleCondition, enteredValue: String): ConditionResult {
    return when (condition.comparison) {
        GateComparison.PRESENT -> {
            if (enteredValue.isBlank()) ConditionResult.UNREADABLE else ConditionResult.SATISFIED
        }
        GateComparison.EQUALS -> {
            val operand = condition.operand
            if (operand.isNullOrBlank()) {
                ConditionResult.UNREADABLE
            } else if (enteredValue == operand) {
                ConditionResult.SATISFIED
            } else {
                ConditionResult.NOT_SATISFIED
            }
        }
        GateComparison.AT_LEAST -> {
            val actual = parseRuleNumber(enteredValue)
            val minimum = condition.operand?.let(::parseRuleNumber)
            if (actual == null || minimum == null) {
                ConditionResult.UNREADABLE
            } else if (actual >= minimum) {
                ConditionResult.SATISFIED
            } else {
                ConditionResult.NOT_SATISFIED
            }
        }
    }
}

private fun parseRuleNumber(value: String): BigDecimal? {
    val trimmed = value.trim()
    if (!RULE_NUMBER.matches(trimmed)) return null
    return BigDecimal(trimmed)
}

private val RULE_NUMBER = Regex("[0-9]+(\\.[0-9]+)?")
