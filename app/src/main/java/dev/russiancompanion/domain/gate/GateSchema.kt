package dev.russiancompanion.domain.gate

/**
 * Full v1 advancement-gate schema.
 *
 * A shipped default gate is the conjunction of a book revision score,
 * self-attested AnkiDroid completion or maturity, and prescribed-work
 * confirmation. Tutor sign-off is never shipped; the user may add it
 * later as the only extra strictness.
 */
internal enum class AnkiStatusValue {
    COMPLETED,
    MATURE,
}

internal const val DEFAULT_BOOK_REVISION_MINIMUM = "1"
internal const val PRESENCE_RECORDED = "recorded"
internal const val DEFAULT_BOOK_CONDITION_ID = "c-book"
internal const val DEFAULT_ANKI_CONDITION_ID = "c-anki"
internal const val DEFAULT_WORK_CONDITION_ID = "c-work"

internal val DEFAULT_ANKI_STATUS = AnkiStatusValue.MATURE

internal data class GateConditionDraft(
    val id: String,
    val evidenceType: String,
    val operator: String,
    val operand: String?,
    val sortOrder: Int,
)

internal data class GateDefinitionDraft(
    val id: String,
    val segmentId: String,
    val ruleVersion: String,
    val displayedRuleText: String,
    val conditions: List<GateConditionDraft>,
)

internal data class GateAdmission(
    val accepted: Boolean,
    val reason: String? = null,
    val conditions: List<GateRuleCondition> = emptyList(),
)

internal fun parseAnkiStatus(value: String): AnkiStatusValue? {
    val trimmed = value.trim()
    return AnkiStatusValue.entries.firstOrNull { it.name == trimmed }
}

internal fun defaultAdvancementRule(
    bookConditionId: String = DEFAULT_BOOK_CONDITION_ID,
    ankiConditionId: String = DEFAULT_ANKI_CONDITION_ID,
    workConditionId: String = DEFAULT_WORK_CONDITION_ID,
    bookMinimum: String = DEFAULT_BOOK_REVISION_MINIMUM,
    ankiStatus: AnkiStatusValue = DEFAULT_ANKI_STATUS,
): List<GateRuleCondition> {
    return listOf(
        GateRuleCondition(
            id = bookConditionId,
            kind = GateEvidenceKind.BOOK_REVISION_SCORE,
            comparison = GateComparison.AT_LEAST,
            operand = bookMinimum,
        ),
        GateRuleCondition(
            id = ankiConditionId,
            kind = GateEvidenceKind.ANKI_DROID_STATUS,
            comparison = GateComparison.EQUALS,
            operand = ankiStatus.name,
        ),
        GateRuleCondition(
            id = workConditionId,
            kind = GateEvidenceKind.PRESCRIBED_WORK_COMPLETION,
            comparison = GateComparison.PRESENT,
            operand = null,
        ),
    )
}

internal fun admitGateDefinition(
    draft: GateDefinitionDraft,
    segmentExists: Boolean,
): GateAdmission {
    if (draft.id.isBlank() || draft.segmentId.isBlank()) {
        return GateAdmission(false, "identity")
    }
    if (!segmentExists) {
        return GateAdmission(false, "missing-segment")
    }
    if (draft.ruleVersion.isBlank() || draft.displayedRuleText.isBlank()) {
        return GateAdmission(false, "rule-blank")
    }
    if (draft.conditions.isEmpty()) {
        return GateAdmission(false, "empty-conditions")
    }
    val seenIds = mutableSetOf<String>()
    val kinds = mutableSetOf<GateEvidenceKind>()
    val parsed = mutableListOf<GateRuleCondition>()
    for (condition in draft.conditions.sortedWith(compareBy({ it.sortOrder }, { it.id }))) {
        if (condition.id.isBlank() || !seenIds.add(condition.id)) {
            return GateAdmission(false, "duplicate-condition")
        }
        val kind = parseEvidenceKind(condition.evidenceType)
            ?: return GateAdmission(false, "unknown-evidence")
        if (kind == GateEvidenceKind.TUTOR_SIGN_OFF) {
            return GateAdmission(false, "tutor-not-shipped")
        }
        if (!kinds.add(kind)) {
            return GateAdmission(false, "duplicate-kind")
        }
        val comparison = parseComparison(condition.operator)
            ?: return GateAdmission(false, "unknown-operator")
        when (kind) {
            GateEvidenceKind.BOOK_REVISION_SCORE -> {
                if (comparison != GateComparison.AT_LEAST) {
                    return GateAdmission(false, "book-must-be-at-least")
                }
                if (condition.operand?.let(::parseRuleNumber) == null) {
                    return GateAdmission(false, "operand-mismatch")
                }
            }
            GateEvidenceKind.ANKI_DROID_STATUS -> {
                if (comparison != GateComparison.EQUALS) {
                    return GateAdmission(false, "anki-must-be-equals")
                }
                if (parseAnkiStatus(condition.operand.orEmpty()) == null) {
                    return GateAdmission(false, "anki-status-required")
                }
            }
            GateEvidenceKind.PRESCRIBED_WORK_COMPLETION -> {
                if (comparison != GateComparison.PRESENT) {
                    return GateAdmission(false, "work-must-be-present")
                }
                if (condition.operand != null) {
                    return GateAdmission(false, "operand-mismatch")
                }
            }
            GateEvidenceKind.TUTOR_SIGN_OFF -> {
                return GateAdmission(false, "tutor-not-shipped")
            }
        }
        parsed += GateRuleCondition(
            id = condition.id,
            kind = kind,
            comparison = comparison,
            operand = condition.operand,
        )
    }
    val required = setOf(
        GateEvidenceKind.BOOK_REVISION_SCORE,
        GateEvidenceKind.ANKI_DROID_STATUS,
        GateEvidenceKind.PRESCRIBED_WORK_COMPLETION,
    )
    if (kinds != required) {
        return GateAdmission(false, "default-kinds-required")
    }
    return GateAdmission(accepted = true, conditions = parsed)
}

private fun parseEvidenceKind(raw: String): GateEvidenceKind? {
    return GateEvidenceKind.entries.firstOrNull { it.name == raw }
}

private fun parseComparison(raw: String): GateComparison? {
    return GateComparison.entries.firstOrNull { it.name == raw }
}
