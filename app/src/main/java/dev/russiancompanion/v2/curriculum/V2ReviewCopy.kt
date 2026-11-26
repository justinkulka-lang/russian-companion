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

/** A unit reference ("Unit 4", "Units 3, 5 and 7") is a name, not a count. */
private val INTRODUCTION_UNIT_REFERENCE = Regex("\\bUnits? \\d+(?:(?:,| and| or) \\d+)*\\b")

/**
 * T16c-n3 (owner decision D46e, approved rule change): an introduction names no count, judged by a complete cardinal pattern,
 * not a short word list: any digit (outside a unit reference); zero; the units one to nine; ten to nineteen; every tens word,
 * alone, hyphenated, spaced or joined with a unit ("twenty-two", "ninety nine", "sixtyfour"); the scale words (hundred,
 * thousand and every -illion); and the count words both, single, dozen and couple (was: a test-only list that stopped at fifty).
 */
private val INTRODUCTION_CARDINAL = Regex(
    "(?i)(?<![\\p{L}])(?:zero|nought|(?:twenty|thirty|forty|fourty|fifty|sixty|seventy|eighty|ninety)(?:[- ]?(?:one|two|three|four|five|six|seven|eight|nine))?|" +
        "ten|eleven|twelve|thirteen|fourteen|fifteen|sixteen|seventeen|eighteen|nineteen|one|two|three|four|five|six|seven|eight|nine|" +
        "hundreds?|thousands?|\\p{L}*illions?|both|single|dozens?|couple)(?![\\p{L}])",
)

internal fun introductionNamesACount(text: String): Boolean {
    val rest = INTRODUCTION_UNIT_REFERENCE.replace(text, "Unit")
    return rest.any(Char::isDigit) || INTRODUCTION_CARDINAL.containsMatchIn(rest)
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
    for ((unit, text) in copy.introductions) if (introductionNamesACount(text)) errors += "Unit $unit review introduction names a count"
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
