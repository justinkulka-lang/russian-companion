package dev.russiancompanion.v2.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import dev.russiancompanion.v2.curriculum.OPTIONAL_PRACTICE
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.curriculum.practiceOffers
import dev.russiancompanion.v2.curriculum.practiceQueue
import dev.russiancompanion.v2.curriculum.practiceStepId
import dev.russiancompanion.v2.curriculum.savedWorkEntries

@Composable
internal fun V2SavedWorkScreen(
    pack: V2CurriculumPack,
    learner: V2LearnerView,
    onBack: () -> Unit,
    onResume: (String, Int) -> Unit,
    onOpenReference: (String) -> Unit,
) {
    val entries = savedWorkEntries(pack, learner)
    V2Scaffold(title = "Saved work", tab = null, onTab = {}, onBack = onBack) {
        Text(
            "Resume a saved lesson, bookmark, note, or tutor question. Completed checks are not listed here.",
            modifier = Modifier.semantics { testTag = "v2-saved-heading" },
        )
        if (entries.isEmpty()) {
            V2Status("No saved work yet.")
        }
        entries.forEach { entry ->
            V2Card(
                title = entry.title,
                body = entry.detail,
                status = if (entry.resumable) "Can resume" else "Not resumed",
                tag = "v2-saved-${entry.id}",
                onClick = if (!entry.resumable) {
                    null
                } else if (entry.referenceId != null) {
                    { onOpenReference(entry.referenceId) }
                } else if (entry.sessionId != null) {
                    { onResume(entry.sessionId, entry.stepIndex) }
                } else {
                    null
                },
            )
        }
    }
}

@Composable
internal fun V2LabScreen(
    pack: V2CurriculumPack,
    learner: V2LearnerView,
    labId: String,
    pendingKeys: Set<String>,
    onBack: () -> Unit,
    onSubmit: (String, String) -> Boolean,
) {
    val offer = practiceOffers(pack, learner).firstOrNull { it.id == labId }
    val items = practiceQueue(pack, learner, labId)
    var index by rememberSaveable(labId) { mutableIntStateOf(0) }
    var chosen by rememberSaveable(labId, index) { mutableStateOf<String?>(null) }
    val item = items.getOrNull(index.coerceAtMost(items.lastIndex.coerceAtLeast(0)))
    val recorded = learner.governedAttempts.orEmpty()
        .filter { it.attemptRole == OPTIONAL_PRACTICE && it.stepId == practiceStepId(labId) && it.itemId == item?.id }
        .lastOrNull()
    val pending = item != null && "practice:$labId:${item.id}:${chosen.orEmpty()}" in pendingKeys
    V2Scaffold(
        title = offer?.title ?: "Practice",
        tab = null,
        onTab = {},
        onBack = onBack,
        footer = {
            Box(Modifier.padding(16.dp)) {
                if (item == null || recorded != null) {
                    val atEnd = items.isEmpty() || index >= items.lastIndex
                    V2PrimaryButton(
                        text = if (items.isEmpty() || atEnd) "Done" else "Next item",
                        onClick = {
                            if (items.isNotEmpty() && index < items.lastIndex) index += 1
                            else onBack()
                        },
                        tag = "v2-lab-next",
                    )
                } else {
                    V2PrimaryButton(
                        text = "Submit answer",
                        enabled = chosen != null && !pending,
                        onClick = {
                            val selection = chosen ?: return@V2PrimaryButton
                            onSubmit(item.id, selection)
                        },
                        tag = "v2-lab-submit",
                    )
                }
            }
        },
    ) {
        if (offer?.runnable != true) {
            Text(offer?.detail ?: "This practice is unavailable.", modifier = Modifier.semantics { testTag = "v2-lab-blocked" })
            return@V2Scaffold
        }
        if (item == null) {
            Text(
                "No closed-response items are available for the units you have reached. Nothing was marked complete.",
                modifier = Modifier.semantics { testTag = "v2-lab-empty" },
            )
            return@V2Scaffold
        }
        Text(
            "Item ${index + 1} of ${items.size}. Optional practice does not complete a required check.",
            modifier = Modifier.semantics { heading(); testTag = "v2-lab-progress" },
        )
        Spacer(Modifier.height(8.dp))
        Text(item.prompt, style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { testTag = "v2-lab-prompt" })
        item.options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .semantics {
                        role = Role.RadioButton
                        this.selected = (recorded?.selected ?: chosen) == option
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = (recorded?.selected ?: chosen) == option,
                    enabled = recorded == null && !pending,
                    onClick = { chosen = option },
                    modifier = Modifier.semantics { testTag = "v2-lab-option-$option" },
                )
                Text(option)
            }
        }
        if (recorded != null) {
            Text(
                if (recorded.correct) {
                    "Recorded as correct. This optional practice does not complete a required check. ${item.feedback}"
                } else {
                    "Not the reviewed answer. ${item.feedback}"
                },
                modifier = Modifier.semantics { testTag = "v2-lab-feedback" },
            )
        }
    }
}
