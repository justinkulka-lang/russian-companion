package dev.russiancompanion.v2.curriculum

/**
 * Display-only permutation. Never use the answer key, learner history, clock, platform
 * PRNG or current list index. The same context/item/option text has the same position
 * after recomposition, process restart and backup restore. Raw curriculum stays intact.
 * Changing this algorithm requires a new version; saved review rules are immutable.
 */
internal fun presentedOptions(item: V2CheckItem, context: String): List<String> =
    item.options.sortedWith(compareBy<String> {
        reviewHash(canonicalParts(listOf(OPTION_PRESENTATION_VERSION, context, item.id, it)))
    }.thenBy { it })

private const val OPTION_PRESENTATION_VERSION = "sha256-option-order-v1"

/** The exact-option grading rule is unchanged; only new review presentation is extended. */
internal fun presentedReviewResponseRuleDigest(): String = reviewHash(canonicalParts(listOf(
    closedResponseRuleDigest(), OPTION_PRESENTATION_VERSION,
    "context=review assignment id and slot index; ascending hex SHA256; text tie-break",
)))

internal fun reviewResponseRuleSupported(digest: String): Boolean =
    digest == closedResponseRuleDigest() || digest == presentedReviewResponseRuleDigest()

internal fun reviewPresentedOptions(item: V2CheckItem, assignment: CumulativeReviewAssignment,
                                    slotIndex: Int): List<String> = when (assignment.responseRuleDigest) {
    closedResponseRuleDigest() -> item.options // Preserve every saved legacy assignment.
    presentedReviewResponseRuleDigest() -> presentedOptions(item, reviewOptionContext(assignment.id, slotIndex))
    else -> emptyList() // Unknown rules must not silently acquire current presentation semantics.
}

internal fun reviewOptionContext(assignmentId: String, slotIndex: Int): String =
    canonicalParts(listOf("review", assignmentId, slotIndex.toString()))

internal fun lessonPresentedOptions(item: V2CheckItem, stepId: String): List<String> =
    presentedOptions(item, canonicalParts(listOf("lesson", stepId)))

internal fun labPresentedOptions(item: V2CheckItem, labId: String): List<String> =
    presentedOptions(item, canonicalParts(listOf("lab", labId)))
