package dev.russiancompanion.v2.ui

import dev.russiancompanion.v2.curriculum.V2CheckItem
import java.text.Normalizer

private val nonExplanatoryFragments = listOf(
    "does not match the category or whole-phrase meaning requested here",
    "is a different meaning or construction and does not match",
    "This differs from the meaning or structure requested in this context",
    "This is a different time relation",
    "This differs from the classification or conversational function",
)
private val namedShownForm = Regex("pairs the shown form \"([^\"]+)\"", RegexOption.IGNORE_CASE)

/** Display only. This rejects known defective patterns; it is not linguistic review or approval. */
internal fun selectedWrongOptionRationale(item: V2CheckItem, selected: String): String? {
    if (selected == item.answer) return null
    val index = item.options.indexOf(selected)
    val rationale = item.distractorRationales.getOrNull(index)?.takeIf { it.isNotBlank() } ?: return null
    if (rationale.trimStart().startsWith("Rejected reading:", ignoreCase = true)) return null
    if (nonExplanatoryFragments.any { rationale.contains(it, ignoreCase = true) }) return null
    namedShownForm.find(rationale)?.groupValues?.get(1)?.let { named ->
        val cue = item.promptLetter?.takeIf { item.promptLetterRole == "CUE" } ?: return null
        // Preserve case and stress: distinct Russian forms must not be collapsed by an audit heuristic.
        if (Normalizer.normalize(named.trim(), Normalizer.Form.NFC) !=
            Normalizer.normalize(cue.trim(), Normalizer.Form.NFC)) return null
    }
    return rationale
}
