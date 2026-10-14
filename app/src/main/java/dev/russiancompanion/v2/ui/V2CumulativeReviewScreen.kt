package dev.russiancompanion.v2.ui

import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import dev.russiancompanion.v2.curriculum.*

private data class ReviewScreenState(val assignment: CumulativeReviewAssignment?, val eligible: Boolean,
                                     val completed: Set<Int>, val stale: Boolean)

@Composable
internal fun V2CumulativeReviewScreen(pack: V2CurriculumPack, learner: V2LearnerView, busy: Boolean,
    onBack: () -> Unit, onTab: (V2Tab) -> Unit, onStart: () -> Unit,
    onAnswer: (String, Int, String) -> Unit, onOpenEarlierUnit: () -> Unit,
    onGate: () -> Unit, onRemediate: (String) -> Unit) {
    val state = rememberGoverned(pack, learner, label = "cumulative-review") {
        val a = currentUnit3Assignment(pack, learner)
        val eligible = createUnit3ReviewAssignment(pack, learner, "eligibility-preview", 0) != null
        ReviewScreenState(a, eligible, a?.let { completedReviewSlots(pack, it, learner.reviewAnswers) }.orEmpty(),
            a == null && learner.reviewAssignments.any { it.targetUnit == 3 })
    }
    V2Scaffold("Earlier reading and letters", V2Tab.COURSE, onTab, onBack) {
        Text("Review two earlier skills: reading and recognising printed letters. Use what you learned in Unit 1; complete both checks before continuing.")
        if (state == null) { V2Status("Preparing review"); return@V2Scaffold }
        if (!state.eligible) {
            Text("Finish the earlier instruction and checks before starting this review.", Modifier.semantics { testTag = "v2-review-insufficient" })
            V2SecondaryButton("Open earlier instruction and checks", onOpenEarlierUnit, tag = "v2-review-repair")
            return@V2Scaffold
        }
        val a = state.assignment
        if (a == null) {
            if (state.stale) Text("Lesson material changed. Your previous review is saved; start a current review.")
            V2PrimaryButton(if (state.stale) "Start a current review" else "Start assigned review", onStart,
                enabled = !busy, tag = "v2-review-start")
            return@V2Scaffold
        }
        Text("${state.completed.size} of ${a.slots.size} review checks recorded", Modifier.semantics { testTag = "v2-review-count" })
        a.slots.forEachIndexed { index, slot ->
            val item = pack.item(slot.itemId) ?: return@forEachIndexed
            Text(item.prompt)
            if (item.promptLetterRole == "CUE") item.promptLetter?.let { Text(it) }
            item.options.forEach { option ->
                V2SecondaryButton(option, { onAnswer(a.id, index, option) }, enabled = !busy,
                    tag = "v2-review-option-$index-$option")
            }
            val latest = learner.reviewAnswers.filter { it.assignmentId == a.id && it.slotIndex == index }
                .maxByOrNull { it.acceptedOrder }
            if (latest != null) {
                Text(if (index in state.completed) "Review answer recorded. ${item.feedback}" else "Not the reviewed answer. ${item.feedback}",
                    Modifier.semantics { testTag = "v2-review-feedback-$index" })
                val objective = pack.unit(slot.ownerUnit)?.objectives?.firstOrNull { it.id == slot.objectiveId }
                objective?.remediationStepIds?.firstOrNull()?.let { step ->
                    V2SecondaryButton("Return to this teaching", { onRemediate(step) }, tag = "v2-review-remediate-$index")
                }
            }
        }
        if (state.completed.size == a.slots.size) {
            Text("Assigned review complete. Ordinary unit instruction, book work and checks remain required.")
            V2PrimaryButton("Return to Unit 3 advancement", onGate, tag = "v2-review-gate")
        }
    }
}
