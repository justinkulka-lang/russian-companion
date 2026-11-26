package dev.russiancompanion.v2.ui

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import dev.russiancompanion.v2.curriculum.V2CheckItem
import dev.russiancompanion.v2.curriculum.typedItemDefects
import dev.russiancompanion.v2.curriculum.isTypedResponse
import dev.russiancompanion.v2.curriculum.scoreCheckedResponse

/** Reveal requires a recorded wrong attempt; retries never erase that first-attempt history. */
@Composable
internal fun V2TypedResponseInput(item: V2CheckItem, context: String, recorded: String?, enabled: Boolean,
    tag: String = "v2-typed-${item.id}", onSubmit: (String) -> Unit) {
    var draft by rememberSaveable(context, item.id, item.prompt, item.promptLetter,
        item.typedAnswerKey?.version, item.typedAnswerKey?.review?.reviewedDigest) { mutableStateOf(recorded.orEmpty()) }
    var revealed by rememberSaveable(context, item.id, item.typedAnswerKey?.version,
        item.typedAnswerKey?.review?.reviewedDigest) { mutableStateOf(false) }
    val available = item.isTypedResponse() && typedItemDefects(item).isEmpty()
    val canSubmit = enabled && available && draft.isNotBlank() && draft != recorded
    val submit = { if (canSubmit) onSubmit(draft) }
    OutlinedTextField(value = draft, onValueChange = { draft = it },
        label = { Text("Your answer") }, singleLine = true,
        enabled = enabled && available,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None, autoCorrectEnabled = false,
            imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { submit() }),
        modifier = Modifier.onPreviewKeyEvent { event ->
            if (event.key == Key.Enter || event.key == Key.NumPadEnter) {
                if (event.type == KeyEventType.KeyUp) submit()
                true
            } else false
        }.semantics { testTag = "$tag-input" })
    V2SecondaryButton("Submit answer", submit, enabled = canSubmit, tag = "$tag-submit")
    if (available && recorded != null && scoreCheckedResponse(item, recorded) == false) {
        V2SecondaryButton("Try again", { draft = "" }, enabled = enabled, tag = "$tag-retry")
        V2SecondaryButton("Show answer", { revealed = true }, enabled = enabled, tag = "$tag-reveal")
    }
    if (available && revealed) {
        Text("Answer: ${item.typedAnswerKey!!.acceptedAnswers.first()}", Modifier.semantics { testTag = "$tag-answer" })
        Text("Answers submitted after revealing do not earn first-attempt credit.", Modifier.semantics { testTag = "$tag-first-attempt" })
    }
    if (!available) V2Status("Answer checking is unavailable for this item.")
}
