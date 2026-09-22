package dev.russiancompanion.domain.gate

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
internal data class GateRuleSnapshot(
    val version: String,
    val displayedRuleText: String,
    val conditions: List<GateConditionSnapshot>,
)

@Serializable
internal data class GateConditionSnapshot(
    val id: String,
    val kind: String,
    val comparison: String,
    val operand: String? = null,
)

@Serializable
internal data class GateOutcomeSnapshot(
    val conditionId: String,
    val evidenceId: String? = null,
    val result: String,
)

private val GateJson = Json {
    ignoreUnknownKeys = false
    explicitNulls = true
    encodeDefaults = true
}

internal fun gateRuleSnapshot(version: String, conditions: List<GateRuleCondition>): GateRuleSnapshot {
    return GateRuleSnapshot(
        version = version,
        displayedRuleText = displayedRuleText(conditions),
        conditions = conditions.map { condition ->
            GateConditionSnapshot(
                id = condition.id,
                kind = condition.kind.name,
                comparison = condition.comparison.name,
                operand = condition.operand,
            )
        },
    )
}

internal fun encodeGateRuleSnapshot(snapshot: GateRuleSnapshot): String {
    return GateJson.encodeToString(GateRuleSnapshot.serializer(), snapshot)
}

internal fun parseGateRuleSnapshot(raw: String): GateRuleSnapshot? {
    if (raw.isBlank() || raw == UNRECOVERABLE_RULE_SNAPSHOT) return null
    return try {
        val snapshot = GateJson.decodeFromString(GateRuleSnapshot.serializer(), raw)
        val conditions = conditionsFromSnapshot(snapshot) ?: return null
        if (!identityTokenAccepted(snapshot.version) || snapshot.displayedRuleText != displayedRuleText(conditions)) {
            null
        } else {
            snapshot
        }
    } catch (_: SerializationException) {
        null
    } catch (_: IllegalArgumentException) {
        null
    }
}

internal fun conditionsFromSnapshot(snapshot: GateRuleSnapshot): List<GateRuleCondition>? {
    val parsed = snapshot.conditions.map { condition ->
        if (!identityTokenAccepted(condition.id)) return null
        val kind = GateEvidenceKind.entries.firstOrNull { it.name == condition.kind } ?: return null
        val comparison = GateComparison.entries.firstOrNull { it.name == condition.comparison } ?: return null
        GateRuleCondition(
            id = condition.id,
            kind = kind,
            comparison = comparison,
            operand = condition.operand,
        )
    }
    return parsed
}

internal fun encodeEvidenceIds(ids: List<String>): String {
    return GateJson.encodeToString(ids)
}

internal fun parseEvidenceIds(raw: String): List<String> {
    if (raw.isBlank()) return emptyList()
    return try {
        GateJson.decodeFromString<List<String>>(raw)
    } catch (_: SerializationException) {
        emptyList()
    } catch (_: IllegalArgumentException) {
        emptyList()
    }
}

internal fun encodeConditionOutcomes(outcomes: List<ConditionOutcome>): String {
    val snapshots = outcomes.map { outcome ->
        GateOutcomeSnapshot(
            conditionId = outcome.conditionId,
            evidenceId = outcome.evidenceId,
            result = outcome.result.name,
        )
    }
    return GateJson.encodeToString(snapshots)
}

internal const val UNRECOVERABLE_RULE_SNAPSHOT = "UNRECOVERABLE"

internal fun parseConditionOutcomes(encoded: String): List<ConditionOutcome>? {
    if (encoded.isBlank()) return null
    return try {
        val snapshots = GateJson.decodeFromString<List<GateOutcomeSnapshot>>(encoded)
        snapshots.map { snapshot ->
            val result = ConditionResult.entries.firstOrNull { it.name == snapshot.result } ?: return null
            ConditionOutcome(
                conditionId = snapshot.conditionId,
                evidenceId = snapshot.evidenceId,
                result = result,
            )
        }
    } catch (_: SerializationException) {
        null
    } catch (_: IllegalArgumentException) {
        null
    }
}

internal fun parseLegacyConditionOutcomes(encoded: String): List<ConditionOutcome>? {
    if (encoded.isBlank()) return null
    val lines = encoded.split('\n')
    if (lines.isEmpty()) return null
    return lines.map { line ->
        val parts = line.split('\t')
        if (parts.size != 3) return null
        val result = ConditionResult.entries.firstOrNull { it.name == parts[2] } ?: return null
        ConditionOutcome(
            conditionId = parts[0],
            evidenceId = parts[1].ifEmpty { null },
            result = result,
        )
    }
}

internal fun decodeConditionOutcomes(encoded: String): List<ConditionOutcome> {
    return parseConditionOutcomes(encoded) ?: parseLegacyConditionOutcomes(encoded).orEmpty()
}

internal fun parseStoredGateStatus(raw: String): GateStatus? {
    return GateStatus.entries.firstOrNull { it.name == raw }
}

internal fun decodeLegacyConditionOutcomes(encoded: String): List<ConditionOutcome> {
    if (encoded.isBlank()) return emptyList()
    return encoded.split('\n').mapNotNull { line ->
        val parts = line.split('\t')
        if (parts.size != 3) return@mapNotNull null
        val result = ConditionResult.entries.firstOrNull { it.name == parts[2] } ?: return@mapNotNull null
        ConditionOutcome(
            conditionId = parts[0],
            evidenceId = parts[1].ifEmpty { null },
            result = result,
        )
    }
}

internal fun decodeLegacyEvidenceIds(raw: String): List<String> {
    if (raw.isBlank()) return emptyList()
    if (raw.startsWith("[")) return parseEvidenceIds(raw)
    return raw.split(',').filter { it.isNotBlank() }
}

/**
 * Recomputes a stored decision from its rule snapshot and only the evidence
 * rows named by that decision. Later evidence and later packaged rules are
 * ignored.
 */
internal fun reproduceDecision(
    ruleSnapshot: String,
    recordedEvidenceIds: List<String>,
    evidence: List<RecordedEvidence>,
): GateEvaluation {
    val snapshot = parseGateRuleSnapshot(ruleSnapshot) ?: return GateEvaluation(GateStatus.NOT_EVALUABLE, emptyList())
    val conditions = conditionsFromSnapshot(snapshot) ?: return GateEvaluation(GateStatus.NOT_EVALUABLE, emptyList())
    val allowed = recordedEvidenceIds.toSet()
    val selected = evidence.filter { it.id in allowed }
    return evaluateGate(conditions, selected)
}

internal fun reproducedDecisionMatches(
    storedStatus: GateStatus,
    storedOutcomes: List<ConditionOutcome>,
    reproduced: GateEvaluation,
): Boolean {
    return reproduced.status == storedStatus && reproduced.outcomes == storedOutcomes
}
