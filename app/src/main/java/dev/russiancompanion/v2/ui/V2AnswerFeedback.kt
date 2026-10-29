package dev.russiancompanion.v2.ui

import dev.russiancompanion.v2.curriculum.V2CheckItem

/** Display only: match the authored option value, independently of presentation order. */
internal fun selectedWrongOptionRationale(item: V2CheckItem, selected: String): String? {
    if (selected == item.answer) return null
    val index = item.options.indexOf(selected)
    return item.distractorRationales.getOrNull(index)?.takeIf { it.isNotBlank() }
}
