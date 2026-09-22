package dev.russiancompanion.presentation.navigation

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import dev.russiancompanion.R
import dev.russiancompanion.domain.gate.AnkiStatusValue
import dev.russiancompanion.domain.gate.ConditionResult
import dev.russiancompanion.domain.gate.GateComparison
import dev.russiancompanion.domain.gate.GateConditionView
import dev.russiancompanion.domain.gate.GateDetailView
import dev.russiancompanion.domain.gate.GateEvidenceEntry
import dev.russiancompanion.domain.gate.GateEvidenceKind
import dev.russiancompanion.domain.gate.GateStatus
import dev.russiancompanion.domain.gate.PRESENCE_RECORDED
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
        GateStatusLine(detail.recordedStatus)
        GateLockedRemainder(detail)
        if (detail.conditions.any { it.result == ConditionResult.UNREADABLE }) {
            Text(stringResource(R.string.gate_evidence_unreadable))
        }
    }
}

@Composable
internal fun GateLockedRemainder(detail: GateDetailView) {
    if (!detail.locked || detail.remainingKinds.isEmpty()) return
    val names = buildList {
        for (kind in detail.remainingKinds) {
            add(evidenceLabel(kind))
        }
    }.joinToString(separator = ", ")
    Text(stringResource(R.string.gate_locked_remaining, names))
}

@Composable
internal fun GateStatusLine(status: GateStatus?) {
    if (status == null) {
        Text(stringResource(R.string.home_gate_no_decision))
    } else {
        Text(stringResource(R.string.home_gate_status, gateStatusText(status)))
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
    GateStatusLine(detail.recordedStatus)
    GateLockedRemainder(detail)
    detail.conditions.forEach { condition ->
        ConditionBlock(
            condition = condition,
            draft = drafts[condition.id].orEmpty(),
            invalid = condition.id in invalidIds,
            onDraft = { drafts[condition.id] = it },
            onRecordPresence = { drafts[condition.id] = PRESENCE_RECORDED },
        )
    }
    detail.evidence.forEach { record ->
        Text(stringResource(R.string.gate_recorded_value, record.enteredValue, record.recordedAtLocal))
        Text(attestationLabel(record.kind))
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
                } else if (evidenceEntryAccepted(condition.comparison, value, condition.kind)) {
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
    when (condition.kind) {
        GateEvidenceKind.ANKI_DROID_STATUS -> {
            AnkiStatusValue.entries.forEach { status ->
                TextButton(onClick = { onDraft(status.name) }) {
                    Text(ankiStatusText(status))
                }
            }
        }
        GateEvidenceKind.BOOK_REVISION_SCORE -> {
            OutlinedTextField(
                value = draft,
                onValueChange = onDraft,
                label = { Text(stringResource(R.string.gate_book_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
            )
        }
        GateEvidenceKind.PRESCRIBED_WORK_COMPLETION,
        GateEvidenceKind.TUTOR_SIGN_OFF,
        -> {
            TextButton(onClick = onRecordPresence) {
                Text(
                    stringResource(
                        if (condition.kind == GateEvidenceKind.PRESCRIBED_WORK_COMPLETION) {
                            R.string.gate_confirm_work
                        } else {
                            R.string.gate_record_tutor
                        },
                    ),
                )
            }
            if (draft.isNotBlank()) {
                Text(draft)
            }
        }
    }
    if (invalid) {
        Text(
            stringResource(R.string.gate_field_invalid),
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive },
        )
    }
}

@Composable
internal fun evidenceLabel(kind: GateEvidenceKind): String {
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
private fun attestationLabel(kind: GateEvidenceKind): String {
    return stringResource(
        when (kind) {
            GateEvidenceKind.BOOK_REVISION_SCORE -> R.string.gate_book_attestation
            GateEvidenceKind.ANKI_DROID_STATUS -> R.string.gate_anki_attestation
            GateEvidenceKind.PRESCRIBED_WORK_COMPLETION,
            GateEvidenceKind.TUTOR_SIGN_OFF,
            -> R.string.gate_user_recorded
        },
    )
}

@Composable
private fun comparisonLabel(condition: GateConditionView): String {
    return when (condition.comparison) {
        GateComparison.PRESENT -> stringResource(R.string.gate_compare_present)
        GateComparison.AT_LEAST -> stringResource(R.string.gate_compare_at_least, condition.operand.orEmpty())
        GateComparison.EQUALS -> {
            val operand = if (condition.kind == GateEvidenceKind.ANKI_DROID_STATUS) {
                ankiStatusText(
                    AnkiStatusValue.entries.firstOrNull { it.name == condition.operand } ?: AnkiStatusValue.MATURE,
                )
            } else {
                condition.operand.orEmpty()
            }
            stringResource(R.string.gate_compare_equals, operand)
        }
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

@Composable
private fun ankiStatusText(status: AnkiStatusValue): String {
    return stringResource(
        when (status) {
            AnkiStatusValue.COMPLETED -> R.string.gate_anki_completed
            AnkiStatusValue.MATURE -> R.string.gate_anki_mature
        },
    )
}
