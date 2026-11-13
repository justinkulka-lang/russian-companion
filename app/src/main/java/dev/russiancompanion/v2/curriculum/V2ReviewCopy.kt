package dev.russiancompanion.v2.curriculum

import kotlinx.serialization.Serializable

@Serializable
internal data class V2ReviewCopy(
    val titles: Map<Int, String> = emptyMap(),
    val introductions: Map<Int, String> = emptyMap(),
    val messages: Map<String, String> = emptyMap(),
) {
    fun text(key: String, vararg values: Pair<String, Any>): String =
        values.fold(messages[key].orEmpty()) { result, (name, value) -> result.replace("{$name}", value.toString()) }
}

internal fun cumulativeReviewTitle(pack: V2CurriculumPack, targetUnit: Int): String =
    pack.reviewCopy.titles[targetUnit] ?: pack.reviewCopy.text("fallbackTitle")

internal fun reviewCopyDefects(copy: V2ReviewCopy): List<String> {
    val errors = mutableListOf<String>()
    val units = (3..30).toSet()
    if (copy.titles.keys != units || copy.titles.values.any { it.isBlank() })
        errors += "Current cumulative-review titles must cover Units 3–30"
    if (copy.introductions.keys != units || copy.introductions.values.any { it.isBlank() })
        errors += "Current cumulative-review introductions must cover Units 3–30"
    val tokens = mapOf(
        "fallbackTitle" to emptySet(), "fallbackIntroduction" to emptySet(), "preparing" to emptySet(),
        "insufficient" to emptySet(), "openEarlierUnit" to setOf("unit"), "stale" to emptySet(),
        "startCurrent" to emptySet(), "startAssigned" to emptySet(), "count" to setOf("completed", "total"),
        "correctFeedback" to setOf("feedback"), "wrongFeedback" to setOf("feedback"),
        "remediate" to emptySet(), "complete" to emptySet(), "returnToGate" to setOf("unit"),
        "beforeAdvancing" to emptySet(), "continueReason" to emptySet(),
        "unverifiedRule" to emptySet(), "required" to emptySet(),
    )
    if (copy.messages.keys != tokens.keys) errors += "Current cumulative-review messages are incomplete or unknown"
    for ((key, required) in tokens) {
        val value = copy.messages[key].orEmpty()
        val pattern = Regex("\\{([^{}]*)\\}")
        val actual = pattern.findAll(value).map { it.groupValues[1] }.toSet()
        val remainder = value.replace(pattern, "")
        if (value.isBlank() || actual != required || '{' in remainder || '}' in remainder)
            errors += "Invalid cumulative-review message $key"
    }
    return errors
}
