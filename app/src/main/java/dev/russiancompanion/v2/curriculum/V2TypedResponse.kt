package dev.russiancompanion.v2.curriculum

import kotlinx.serialization.Serializable

internal val TYPED_ITEM_KINDS = setOf("TYPED_FORM", "TYPED_TRANSFORMATION", "TYPED_TRANSLATION", "TYPED_DICTATION")

@Serializable
internal data class V2TypedKeyReview(val reviewer: String, val status: String, val reviewedDigest: String)

@Serializable
internal data class V2TypedAnswerKey(
    val id: String,
    val version: Int,
    val acceptedAnswers: List<String>,
    val schema: Int = 1,
    val review: V2TypedKeyReview? = null,
)

internal fun V2CheckItem.isTypedResponse(): Boolean = kind in TYPED_ITEM_KINDS

internal fun responseRuleDigest(item: V2CheckItem): String =
    if (item.isTypedResponse()) typedResponseRuleDigest() else closedResponseRuleDigest()

/** Review signatures/status stay outside the content they sign. */
internal fun typedAnswerKeyContent(key: V2TypedAnswerKey): String = canonicalParts(listOf(
    "typed-answer-key", key.schema.toString(), key.id, key.version.toString(),
    "a2-21-owner-trim-v2", typedResponseRuleDigest(), canonicalParts(key.acceptedAnswers),
))

internal fun typedAnswerKeyDigest(key: V2TypedAnswerKey): String = reviewHash(typedAnswerKeyContent(key))

internal fun typedResponseRuleDigest(): String = reviewHash(canonicalParts(listOf(
    "closed-typed-response-v2", "finite reviewed versioned key; exact membership; trim response leading/trailing whitespace before comparison",
    "only first-letter simple case, combining acute U+0301 removal, and U+0401/U+0451 to U+0415/U+0435",
    "no inner-whitespace, punctuation, other-letter case, Unicode decomposition, spelling, synonym or fuzzy normalization",
)))

/** Only A2-21's three permitted changes; preserve every other character. */
internal fun normalizeTypedResponse(value: String): String {
    val text = buildString {
        value.forEach { c ->
            // Unicode scalar operations on the entered response, not authored curriculum text.
            when (c.code) {
                0x0301 -> Unit
                0x0401 -> append(0x0415.toChar())
                0x0451 -> append(0x0435.toChar())
                else -> append(c)
            }
        }
    }
    var index = 0
    while (index < text.length) {
        val code = text.codePointAt(index)
        val size = Character.charCount(code)
        if (Character.isLetter(code)) {
            return text.replaceRange(index, index + size, String(Character.toChars(Character.toLowerCase(code))))
        }
        index += size
    }
    return text
}

internal fun typedItemDefects(item: V2CheckItem): List<String> {
    if (!item.isTypedResponse()) return if (item.typedAnswerKey != null || item.dictationAudioAssetId != null)
        listOf("Item ${item.id} has typed data on a non-typed kind") else emptyList()
    val errors = mutableListOf<String>()
    if (item.options.isNotEmpty() || item.distractorRationales.isNotEmpty())
        errors += "Typed item ${item.id} must not expose choice options"
    val key = item.typedAnswerKey
    if (key == null) errors += "Typed item ${item.id} has no reviewed answer key"
    else {
        if (key.schema != 1 || key.version <= 0 || key.id.isBlank())
            errors += "Typed item ${item.id} has an invalid key identity/version"
        if (key.acceptedAnswers.isEmpty() || key.acceptedAnswers.any { it.isBlank() || normalizeTypedResponse(it).isBlank() } ||
            key.acceptedAnswers.distinct().size != key.acceptedAnswers.size || item.answer !in key.acceptedAnswers)
            errors += "Typed item ${item.id} has an invalid finite answer key"
        val review = key.review
        if (review == null || review.reviewer.isBlank() || review.status !in REVIEW_CHAIN ||
            review.reviewedDigest != typedAnswerKeyDigest(key))
            errors += "Typed item ${item.id} answer key is unreviewed or changed since review"
    }
    if (item.kind == "TYPED_DICTATION") {
        // T13 introduces per-clip verification; a catalog id alone is not a verified text match.
        errors += "Typed dictation ${item.id} requires the verified clip binding introduced by T13"
    } else if (item.dictationAudioAssetId != null) errors += "Typed item ${item.id} has an unexpected dictation clip"
    return errors
}

/** Null means unavailable/invalid input; false is a valid submitted wrong answer. */
internal fun scoreCheckedResponse(item: V2CheckItem, response: String): Boolean? {
    if (response.isBlank() || item.kind !in CLOSED_ITEM_KINDS) return null
    if (!item.isTypedResponse()) {
        if (item.typedAnswerKey != null || item.dictationAudioAssetId != null || item.answer !in item.options || response !in item.options) return null
        return response == item.answer
    }
    if (typedItemDefects(item).isNotEmpty()) return null
    val submitted = normalizeTypedResponse(response.trim())
    return item.typedAnswerKey!!.acceptedAnswers.any { normalizeTypedResponse(it) == submitted }
}
