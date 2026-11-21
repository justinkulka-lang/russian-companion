package dev.russiancompanion.v2.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import dev.russiancompanion.v2.curriculum.*

/** A2-32/33/34 on the unit screen: binder self-check, optional tutor task, unit-test entry. */
@Composable
internal fun V2UnitCourseworkSection(pack: V2CurriculumPack, learner: V2LearnerView, unit: V2Unit,
                                     onOpenUnitTest: () -> Unit, onBinderSelfCheck: () -> Unit) {
    unit.binderTask?.let { task ->
        Text("Binder task", style = MaterialTheme.typography.titleMedium)
        Text(task.prompt, modifier = Modifier.semantics { testTag = "v2-binder-prompt" })
        val checked = learner.binderSelfChecks.any { it.unitNumber == unit.number && binderSelfCheckCurrent(pack, it) }
        if (!checked) {
            V2SecondaryButton("I did this in my binder", onBinderSelfCheck, tag = "v2-binder-self-check")
        } else {
            Text("Self-check recorded. This is your own check, not a demonstrated app check.", modifier = Modifier.semantics { testTag = "v2-binder-status" })
            task.modelAnswer?.let { Text("Model answer for comparison: $it", modifier = Modifier.semantics { testTag = "v2-binder-model" }) }
        }
    }
    unit.tutorTask?.let { task ->
        Text("Optional tutor task: bring ${task.bring}", modifier = Modifier.semantics { testTag = "v2-tutor-task" })
    }
    Text("Unit test", style = MaterialTheme.typography.titleMedium)
    val plan = unitTestPlan(pack, unit)
    if (plan == null) {
        Text(unitTestUnavailableReason(pack, unit) ?: "Unit test not yet available.",
            modifier = Modifier.semantics { testTag = "v2-unit-test-unavailable" })
        return
    }
    Text(unitTestSummary(plan), modifier = Modifier.semantics { testTag = "v2-unit-test-summary" })
    learner.unitTestAttempts.filter { it.unitNumber == unit.number && it.submittedOrder > 0 }.maxByOrNull { it.submittedOrder }?.let {
        Text("Last result: ${unitTestResult(it)}", modifier = Modifier.semantics { testTag = "v2-unit-test-last" })
    }
    V2SecondaryButton("Open unit test", onOpenUnitTest, tag = "v2-unit-test-open")
}

internal fun unitTestSummary(plan: V2UnitTestPlan): String =
    "${plan.itemIds.size} checked items, ${plan.typedCount} typed. Pass mark: ${plan.passMarkPercent} percent."

internal fun unitTestResult(a: V2UnitTestAttempt): String =
    "${a.correctCount} of ${a.itemIds.size} correct. Pass mark ${a.passMarkPercent} percent. " +
        if (a.passed) "Passed." else "Not passed."

/** The pass mark shows before the attempt (MA2-085). No per-item result shows until submission. */
@Composable
internal fun V2UnitTestScreen(pack: V2CurriculumPack, learner: V2LearnerView, unitNumber: Int, busy: Boolean,
    onBack: () -> Unit, onTab: (V2Tab) -> Unit, onStart: () -> Unit,
    onAnswer: (String, Int, String) -> Unit, onSubmit: (String) -> Unit, onRemediate: (String) -> Unit) {
    val unit = pack.unit(unitNumber) ?: return
    V2Scaffold("Unit $unitNumber test", V2Tab.COURSE, onTab, onBack) {
        val plan = unitTestPlan(pack, unit)
        if (plan == null) {
            Text(unitTestUnavailableReason(pack, unit) ?: "Unit test not yet available.",
                modifier = Modifier.semantics { testTag = "v2-unit-test-unavailable" })
            return@V2Scaffold
        }
        Text(unitTestSummary(plan), modifier = Modifier.semantics { testTag = "v2-unit-test-passmark" })
        val attempts = learner.unitTestAttempts.filter { it.unitNumber == unitNumber }
        val open = attempts.singleOrNull { it.submittedOrder == 0L && unitTestAttemptCurrent(pack, it) }
        if (open == null) {
            val last = attempts.filter { it.submittedOrder > 0 }.maxByOrNull { it.submittedOrder }
            if (last != null) {
                Text(unitTestResult(last), modifier = Modifier.semantics { testTag = "v2-unit-test-result" })
                if (!last.passed) {
                    Text("Review these objectives, then retake the test.")
                    unitTestRemediation(pack, last).forEach { (objective, step) ->
                        V2SecondaryButton("Review: ${objective.title}", { onRemediate(step) }, tag = "v2-unit-test-remediate-${objective.id}")
                    }
                }
            }
            V2PrimaryButton(if (last == null) "Start unit test" else "Retake unit test", onStart, enabled = !busy,
                tag = if (last == null) "v2-unit-test-start" else "v2-unit-test-retake")
            return@V2Scaffold
        }
        Text("${open.answers.size} of ${open.itemIds.size} answered", modifier = Modifier.semantics { testTag = "v2-unit-test-count" })
        open.itemIds.forEachIndexed { index, id ->
            val item = pack.item(id) ?: return@forEachIndexed
            val saved = open.answers.lastOrNull { it.index == index }?.selected
            Text("${index + 1}. ${item.prompt}")
            if (item.promptLetterRole == "CUE") item.promptLetter?.let { Text(it) }
            if (item.isTypedResponse()) {
                V2TestTypedAnswer(open.id, index, saved, !busy) { value -> onAnswer(open.id, index, value) }
            } else item.options.forEach { option ->
                V2SecondaryButton(if (option == saved) "Selected: $option" else option, { onAnswer(open.id, index, option) },
                    enabled = !busy, tag = "v2-test-option-$index-$option")
            }
        }
        V2PrimaryButton("Submit unit test", { onSubmit(open.id) }, enabled = !busy && open.answers.size == open.itemIds.size,
            tag = "v2-unit-test-submit")
    }
}

/** Typed test answer: saves the raw text only; no correctness, retry or reveal until submission. */
@Composable
private fun V2TestTypedAnswer(attemptId: String, index: Int, saved: String?, enabled: Boolean, onSave: (String) -> Unit) {
    var draft by androidx.compose.runtime.saveable.rememberSaveable(attemptId, index) { androidx.compose.runtime.mutableStateOf(saved.orEmpty()) }
    androidx.compose.material3.OutlinedTextField(
        value = draft, onValueChange = { draft = it }, enabled = enabled, singleLine = true,
        label = { Text("Your answer") },
        modifier = Modifier.semantics { testTag = "v2-test-typed-$index" },
    )
    V2SecondaryButton(if (saved != null && saved == draft) "Saved" else "Save answer", { if (draft.isNotBlank()) onSave(draft) },
        enabled = enabled && draft.isNotBlank() && draft != saved, tag = "v2-test-typed-save-$index")
}
