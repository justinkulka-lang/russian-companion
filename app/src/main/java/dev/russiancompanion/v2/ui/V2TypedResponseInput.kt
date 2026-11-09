package dev.russiancompanion.v2.ui

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.input.KeyboardCapitalization
import dev.russiancompanion.v2.curriculum.V2CheckItem
import dev.russiancompanion.v2.curriculum.typedItemDefects
import dev.russiancompanion.v2.curriculum.isTypedResponse

/** Draft restoration never submits, scores, navigates or writes learner data. */
@Composable
internal fun V2TypedResponseInput(item: V2CheckItem, context: String, recorded: String?, enabled: Boolean,
    tag: String = "v2-typed-${item.id}", onSubmit: (String) -> Unit) {
    var draft by rememberSaveable(context, item.id, item.prompt, item.promptLetter,
        item.typedAnswerKey?.version, item.typedAnswerKey?.review?.reviewedDigest) { mutableStateOf(recorded.orEmpty()) }
    val available = item.isTypedResponse() && typedItemDefects(item).isEmpty()
    OutlinedTextField(value = draft, onValueChange = { draft = it },
        label = { Text("Your answer") }, singleLine = true,
        enabled = enabled && available,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None, autoCorrectEnabled = false),
        modifier = Modifier.semantics { testTag = "$tag-input" })
    V2SecondaryButton("Submit answer", { onSubmit(draft) },
        enabled = enabled && available && draft.isNotBlank() && draft != recorded, tag = "$tag-submit")
    if (!available) V2Status("Answer checking is unavailable for this item.")
}
