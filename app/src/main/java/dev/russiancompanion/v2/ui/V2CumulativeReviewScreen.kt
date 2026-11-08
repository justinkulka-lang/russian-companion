package dev.russiancompanion.v2.ui

import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import dev.russiancompanion.v2.curriculum.*

private data class ReviewScreenState(val assignment: CumulativeReviewAssignment?, val eligible: Boolean,
                                     val completed: Set<Int>, val stale: Boolean, val repairUnits: List<Int>)

@Composable
internal fun V2CumulativeReviewScreen(pack: V2CurriculumPack, learner: V2LearnerView, targetUnit: Int, busy: Boolean,
    onBack: () -> Unit, onTab: (V2Tab) -> Unit, onStart: () -> Unit,
    onAnswer: (String, Int, String) -> Unit, onOpenEarlierUnit: (Int) -> Unit,
    onGate: () -> Unit, onRemediate: (String) -> Unit) {
    val declaredPolicy = cumulativeReviewDeclaredPolicy(pack, targetUnit)
    val state = rememberGoverned(pack, learner, label = "cumulative-review") {
        val a = currentCumulativeAssignment(pack, learner, targetUnit)
        val eligible = if (a != null) reviewAssignmentEligible(pack, learner, a) else
            createCumulativeReviewAssignment(pack, learner, targetUnit, "eligibility-preview", 0) != null
        ReviewScreenState(a, eligible, a?.let { completedReviewSlots(pack, it, learner.reviewAnswers) }.orEmpty(),
            a == null && learner.reviewAssignments.any { it.targetUnit == targetUnit }, (a?.let(::cumulativeReviewAssignmentPolicy) ?: declaredPolicy)?.pool.orEmpty().mapNotNull { spec ->
                pack.units.firstOrNull { u -> u.objectives.any { it.id == spec.objectiveId } }?.number
            }.distinct().filter { it !in currentCompletedUnitNumbers(pack, learner) })
    }
    V2Scaffold(cumulativeReviewTitle(pack, targetUnit), V2Tab.COURSE, onTab, onBack) {
        Text(pack.reviewCopy.introductions[targetUnit] ?: pack.reviewCopy.text("fallbackIntroduction"))
        if (state == null) { V2Status(pack.reviewCopy.text("preparing")); return@V2Scaffold }
        if (!state.eligible) {
            Text(pack.reviewCopy.text("insufficient"), Modifier.semantics { testTag = "v2-review-insufficient" })
            state.repairUnits.ifEmpty { listOf(1) }.forEachIndexed { index, number ->
                V2SecondaryButton(pack.reviewCopy.text("openEarlierUnit", "unit" to number), { onOpenEarlierUnit(number) },
                    tag = if (index == 0) "v2-review-repair" else "v2-review-repair-$number")
            }
            return@V2Scaffold
        }
        val a = state.assignment
        if (a == null) {
            if (state.stale) Text(pack.reviewCopy.text("stale"))
            V2PrimaryButton(if (state.stale) pack.reviewCopy.text("startCurrent") else pack.reviewCopy.text("startAssigned"), onStart,
                enabled = !busy, tag = "v2-review-start")
            return@V2Scaffold
        }
        Text(pack.reviewCopy.text("count", "completed" to state.completed.size, "total" to a.slots.size), Modifier.semantics { testTag = "v2-review-count" })
        a.slots.forEachIndexed { index, slot ->
            val item = pack.item(slot.itemId) ?: return@forEachIndexed
            Text(item.prompt)
            if (item.promptLetterRole == "CUE") item.promptLetter?.let { Text(it) }
            reviewPresentedOptions(item, a, index).forEach { option ->
                V2SecondaryButton(option, { onAnswer(a.id, index, option) }, enabled = !busy,
                    tag = "v2-review-option-$index-$option")
            }
            val latest = learner.reviewAnswers.filter { it.assignmentId == a.id && it.slotIndex == index }
                .maxByOrNull { it.acceptedOrder }
            if (latest != null) {
                Text(if (index in state.completed) pack.reviewCopy.text("correctFeedback", "feedback" to item.feedback) else pack.reviewCopy.text("wrongFeedback", "feedback" to item.feedback),
                    Modifier.semantics { testTag = "v2-review-feedback-$index" })
                selectedWrongOptionRationale(item, latest.selected)?.let { rationale ->
                    Text(rationale, Modifier.semantics { testTag = "v2-review-rationale-$index" })
                }
                val objective = pack.unit(slot.ownerUnit)?.objectives?.firstOrNull { it.id == slot.objectiveId }
                (cumulativeReviewAssignmentPolicy(a)?.pool?.firstOrNull { it.objectiveId == slot.objectiveId }?.remediationStepId
                    ?: objective?.remediationStepIds?.firstOrNull())?.let { step ->
                    V2SecondaryButton(pack.reviewCopy.text("remediate"), { onRemediate(step) }, tag = "v2-review-remediate-$index")
                }
            }
        }
        if (state.completed.size == a.slots.size) {
            Text(pack.reviewCopy.text("complete"))
            V2PrimaryButton(pack.reviewCopy.text("returnToGate", "unit" to targetUnit), onGate, tag = "v2-review-gate")
        }
    }
}
