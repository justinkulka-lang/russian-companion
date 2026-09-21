package dev.russiancompanion.presentation.navigation

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import dev.russiancompanion.R
import dev.russiancompanion.domain.gate.ConditionResult
import dev.russiancompanion.domain.gate.GateComparison
import dev.russiancompanion.domain.gate.GateConditionView
import dev.russiancompanion.domain.gate.GateDetailView
import dev.russiancompanion.domain.gate.GateEvidenceEntry
import dev.russiancompanion.domain.gate.GateEvidenceKind
import dev.russiancompanion.domain.gate.GateStatus
import dev.russiancompanion.domain.gate.evidenceEntryAccepted
import dev.russiancompanion.domain.plan.PlanUpdate

@Composable
internal fun GateListBody(details: List<GateDetailView>, onOpen: (String) -> Unit) {
    if (details.isEmpty()) {
        Text(stringResource(R.string.gates_none))
        return
    }
    details.forEach { detail ->
        TextButton(onClick = { onOpen(detail.id) }) { Text(detail.displayedRuleText) }
        val status = detail.recordedStatus
        if (status == null) {
            Text(stringResource(R.string.home_gate_no_decision))
        } else {
            Text(stringResource(R.string.home_gate_status, gateStatusText(status)))
        }
        val missing = detail.conditions.filter { it.result == ConditionResult.AWAITING }
        if (missing.isNotEmpty()) {
            val names = mutableListOf<String>()
            for (condition in missing) {
                names += evidenceLabel(condition.kind)
            }
            Text(stringResource(R.string.gate_missing, names.joinToString(separator = ", ")))
        }
        if (detail.conditions.any { it.result == ConditionResult.UNREADABLE }) {
            Text(stringResource(R.string.gate_evidence_unreadable))
        }
    }
}

@Composable
internal fun GateDetailBody(
    detail: GateDetailView,
    onSave: (List<GateEvidenceEntry>) -> PlanUpdate,
    onAddTutor: () -> PlanUpdate,
) {
    val drafts = remember(detail.id) { mutableStateMapOf<String, String>() }
    var invalidIds by remember(detail.id) { mutableStateOf(setOf<String>()) }
    var saveFailed by remember(detail.id) { mutableStateOf(false) }
    var failedAdd by remember(detail.id) { mutableStateOf(false) }
    Text(detail.displayedRuleText)
    val status = detail.recordedStatus
    if (status == null) {
        Text(stringResource(R.string.home_gate_no_decision))
    } else {
        Text(stringResource(R.string.home_gate_status, gateStatusText(status)))
    }
    detail.conditions.forEach { condition ->
        ConditionBlock(
            condition = condition,
            draft = drafts[condition.id].orEmpty(),
            invalid = condition.id in invalidIds,
            onDraft = { drafts[condition.id] = it },
            onRecordPresence = { drafts[condition.id] = "recorded" },
        )
    }
    detail.evidence.forEach { record ->
        Text(stringResource(R.string.gate_recorded_value, record.enteredValue, record.recordedAtLocal))
    }
    if (!detail.tutorRequired) {
        TextButton(
            onClick = {
                failedAdd = onAddTutor() is PlanUpdate.NotSaved
                saveFailed = failedAdd
            },
        ) { Text(stringResource(R.string.gate_add_tutor)) }
    }
    if (saveFailed) {
        Text(stringResource(R.string.plan_not_saved))
    }
    TextButton(
        onClick = {
            val invalid = detail.conditions.mapNotNull { condition ->
                val value = drafts[condition.id].orEmpty()
                if (value.isBlank()) {
                    null
                } else if (evidenceEntryAccepted(condition.comparison, value)) {
                    null
                } else {
                    condition.id
                }
            }.toSet()
            invalidIds = invalid
            if (invalid.isNotEmpty()) {
                saveFailed = false
                failedAdd = false
                return@TextButton
            }
            val entries = detail.conditions.mapNotNull { condition ->
                val value = drafts[condition.id].orEmpty().trim()
                if (value.isEmpty()) null else GateEvidenceEntry(condition.kind, value)
            }
            val update = onSave(entries)
            failedAdd = false
            saveFailed = update is PlanUpdate.NotSaved
            if (!saveFailed) {
                drafts.clear()
                invalidIds = emptySet()
            }
        },
    ) { Text(stringResource(R.string.gate_save)) }
    if (saveFailed) {
        TextButton(
            onClick = {
                if (failedAdd) {
                    failedAdd = onAddTutor() is PlanUpdate.NotSaved
                    saveFailed = failedAdd
                } else {
                    val entries = detail.conditions.mapNotNull { condition ->
                        val value = drafts[condition.id].orEmpty().trim()
                        if (value.isEmpty()) null else GateEvidenceEntry(condition.kind, value)
                    }
                    val update = onSave(entries)
                    saveFailed = update is PlanUpdate.NotSaved
                    if (!saveFailed) {
                        drafts.clear()
                        invalidIds = emptySet()
                    }
                }
            },
        ) { Text(stringResource(R.string.navigate_retry)) }
    }
}

@Composable
private fun ConditionBlock(
    condition: GateConditionView,
    draft: String,
    invalid: Boolean,
    onDraft: (String) -> Unit,
    onRecordPresence: () -> Unit,
) {
    Text(evidenceLabel(condition.kind))
    when (condition.kind) {
        GateEvidenceKind.BOOK_REVISION_SCORE -> Text(stringResource(R.string.gate_book_attestation))
        GateEvidenceKind.ANKI_DROID_STATUS -> Text(stringResource(R.string.gate_anki_attestation))
        GateEvidenceKind.PRESCRIBED_WORK_COMPLETION,
        GateEvidenceKind.TUTOR_SIGN_OFF,
        -> Unit
    }
    Text(comparisonLabel(condition))
    Text(resultLabel(condition.result))
    if (condition.comparison == GateComparison.PRESENT) {
        TextButton(onClick = onRecordPresence) { Text(stringResource(R.string.gate_record_evidence)) }
        if (draft.isNotBlank()) {
            Text(draft)
        }
    } else {
        OutlinedTextField(
            value = draft,
            onValueChange = onDraft,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (condition.comparison == GateComparison.AT_LEAST) {
                    KeyboardType.Decimal
                } else {
                    KeyboardType.Text
                },
            ),
            singleLine = true,
        )
    }
    if (invalid) {
        Text(stringResource(R.string.gate_field_invalid))
    }
}

@Composable
private fun evidenceLabel(kind: GateEvidenceKind): String {
    return stringResource(
        when (kind) {
            GateEvidenceKind.BOOK_REVISION_SCORE -> R.string.gate_book_label
            GateEvidenceKind.ANKI_DROID_STATUS -> R.string.gate_anki_label
            GateEvidenceKind.PRESCRIBED_WORK_COMPLETION -> R.string.gate_work_label
            GateEvidenceKind.TUTOR_SIGN_OFF -> R.string.gate_tutor_label
        },
    )
}

@Composable
private fun comparisonLabel(condition: GateConditionView): String {
    return when (condition.comparison) {
        GateComparison.PRESENT -> stringResource(R.string.gate_compare_present)
        GateComparison.AT_LEAST -> stringResource(R.string.gate_compare_at_least, condition.operand.orEmpty())
        GateComparison.EQUALS -> stringResource(R.string.gate_compare_equals, condition.operand.orEmpty())
    }
}

@Composable
private fun resultLabel(result: ConditionResult): String {
    return stringResource(
        when (result) {
            ConditionResult.SATISFIED -> R.string.gate_condition_met
            ConditionResult.NOT_SATISFIED -> R.string.gate_condition_not_met
            ConditionResult.AWAITING -> R.string.gate_awaiting
            ConditionResult.UNREADABLE -> R.string.gate_evidence_unreadable
        },
    )
}

@Composable
private fun gateStatusText(status: GateStatus): String {
    return stringResource(
        when (status) {
            GateStatus.SATISFIED -> R.string.gate_status_satisfied
            GateStatus.NOT_YET_SATISFIED -> R.string.gate_status_not_yet
            GateStatus.NOT_EVALUABLE -> R.string.gate_status_not_evaluable
        },
    )
}
