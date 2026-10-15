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
    val state = rememberGoverned(pack, learner, label = "cumulative-review") {
        val a = currentCumulativeAssignment(pack, learner, targetUnit)
        val eligible = createCumulativeReviewAssignment(pack, learner, targetUnit, "eligibility-preview", 0) != null
        ReviewScreenState(a, eligible, a?.let { completedReviewSlots(pack, it, learner.reviewAnswers) }.orEmpty(),
            a == null && learner.reviewAssignments.any { it.targetUnit == targetUnit }, cumulativeReviewPolicy(targetUnit)?.pool.orEmpty().mapNotNull { spec ->
                pack.units.firstOrNull { u -> u.objectives.any { it.id == spec.objectiveId } }?.number
            }.distinct().filter { it !in currentCompletedUnitNumbers(pack, learner) })
    }
    V2Scaffold(cumulativeReviewTitle(targetUnit), V2Tab.COURSE, onTab, onBack) {
        Text(when (targetUnit) {
            3 -> "Review two earlier skills: reading and recognising printed letters. Use what you learned in Unit 1; complete both checks before continuing."
            4 -> "Review earlier print-pair decoding and noun-gender labels from Units 1 and 3. Complete both checks before continuing; these supplied choices do not demonstrate whole-word reading, speaking or writing."
            5 -> "Review earlier print-pair matching from Unit 1 and nominative subjects in supplied Unit 4 clauses. Complete both checks before continuing. These taught choices do not demonstrate unseen reading, productive grammar, speaking or writing."
            6 -> "Review earlier print-pair matching from Unit 1 and the behavior of supplied Unit 5 nouns. Complete both checks before continuing. These taught choices do not demonstrate unseen reading, productive morphology, speaking or writing."
            7 -> "Review earlier print-pair matching from Unit 1 and the supplied inanimate direct object from Unit 6. Complete both checks before continuing. These taught choices do not demonstrate unseen reading, productive grammar, speaking or writing."
            8 -> "Review earlier print-pair matching and supplied feminine singular nominative agreement. For agreement repair, revisit the shown car phrase and related feminine object-phrase practice, then answer this nominative check again. These taught choices do not demonstrate unseen reading, productive grammar, speaking or writing."
            9 -> "Review earlier print-pair matching from Unit 1 and the supplied window nominative plural from Unit 8. Repair reviews all seven taught noun pairs; guided table practice does not complete the window check. Complete both review checks before continuing. These supplied choices do not demonstrate unseen reading, productive grammar, speaking or writing."
            10 -> "Review earlier printed letter pairs from Unit 1 and masculine singular one with the supplied hour noun from Unit 9. Repair restores all six agreement cards; guided boy practice does not complete the hour check. Complete both review checks before continuing. These supplied choices do not demonstrate unseen reading, productive grammar, speaking or writing."
            11 -> "Review earlier printed letter pairs from Unit 1 and the supplied absence line from Unit 10. Repair restores all five possession cards and seven examples; guided positive possession does not complete the absence check. Complete both review checks before continuing. These supplied choices do not demonstrate unseen reading, productive grammar, speaking or writing."
            12 -> "Review earlier printed letter pairs from Unit 1 and supplied past gender from Unit 11. Repair restores all four gender cards and three forms of the same verb; guided neuter practice does not complete the masculine check. Complete both review checks before continuing. These supplied choices do not demonstrate unseen reading, productive grammar, aspect mastery, speaking or writing."
            13 -> "Review earlier printed letter pairs from Unit 1 and the supplied aspect partners from Unit 12. Repair restores all three pair cards and four examples; guided perfective practice does not complete the imperfective check. Complete both review checks before continuing. These supplied choices do not establish sentence-context aspect selection, unseen reading, productive grammar, speaking or writing."
            14 -> "Review earlier printed letter pairs from Unit 1 and the supplied evening report from Unit 13. Repair restores all three report cards and four examples; guided nationality practice does not complete the evening-time check. Complete both review checks before continuing. These supplied interpretations do not establish unseen reading, unrestricted reported-speech production, speaking or writing."
            15 -> "Review earlier printed letter pairs from Unit 1 and the supplied female-cold state from Unit 14. Repair restores all three state cards and four examples; guided male-cold practice does not complete the female-cold check. Complete both review checks before continuing. Recognition of these supplied examples does not establish unseen reading, unrestricted grammar production, speaking or writing."
            else -> "Review the required earlier objectives before continuing."
        })
        if (state == null) { V2Status("Preparing review"); return@V2Scaffold }
        if (!state.eligible) {
            Text("Finish the earlier instruction and checks before starting this review.", Modifier.semantics { testTag = "v2-review-insufficient" })
            state.repairUnits.ifEmpty { listOf(1) }.forEachIndexed { index, number ->
                V2SecondaryButton("Open earlier Unit $number instruction and checks", { onOpenEarlierUnit(number) },
                    tag = if (index == 0) "v2-review-repair" else "v2-review-repair-$number")
            }
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
                (cumulativeReviewPolicy(targetUnit)?.pool?.firstOrNull { it.objectiveId == slot.objectiveId }?.remediationStepId
                    ?: objective?.remediationStepIds?.firstOrNull())?.let { step ->
                    V2SecondaryButton("Return to this teaching", { onRemediate(step) }, tag = "v2-review-remediate-$index")
                }
            }
        }
        if (state.completed.size == a.slots.size) {
            Text("Assigned review complete. Ordinary unit instruction, book work and checks remain required.")
            V2PrimaryButton("Return to Unit $targetUnit advancement", onGate, tag = "v2-review-gate")
        }
    }
}
