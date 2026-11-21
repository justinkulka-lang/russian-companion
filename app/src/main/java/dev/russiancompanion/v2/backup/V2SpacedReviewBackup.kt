package dev.russiancompanion.v2.backup

import dev.russiancompanion.v2.curriculum.*

/** Preserve only current-format, structurally coherent queue rows tied to accepted checked history. */
internal fun spacedReviewBackupDefects(document: GovernedLearnerDocument): String? {
    if (document.spacedReviews.map { it.itemId }.distinct().size != document.spacedReviews.size) return "Duplicate spaced-review item"
    for (record in document.spacedReviews) {
        spacedReviewRecordDefect(record)?.let { return it }
        val event = record.lastEventId.substringAfter(':')
        if (record.lastEventId.startsWith("attempt:")) {
            val attempt = document.attempts.singleOrNull { it.id == event } ?: return "Missing spaced-review attempt"
            if (attempt.acceptedOrder <= 0 || attempt.identitySchema != GOVERNED_IDENTITY_SCHEMA ||
                attempt.attemptRole !in setOf(PRESCRIBED_CHECK, OPTIONAL_PRACTICE) || attempt.selected.isBlank() ||
                attempt.itemId != record.itemId || attempt.objectiveId != record.objectiveId || attempt.unitNumber != record.unitNumber ||
                attempt.itemDigest != record.itemDigest || attempt.unitContentDigest != record.unitContentDigest ||
                attempt.ruleDigest != record.responseRuleDigest || (!attempt.correct && record.consecutiveCorrect != 0)) return "Mismatched spaced-review attempt"
        } else {
            val answer = document.reviewAnswers.singleOrNull { it.id == event } ?: return "Missing spaced-review answer"
            val assignment = document.reviewAssignments.singleOrNull { it.id == answer.assignmentId } ?: return "Missing spaced-review assignment"
            val slot = assignment.slots.getOrNull(answer.slotIndex) ?: return "Missing spaced-review slot"
            if (answer.schema != CUMULATIVE_REVIEW_SCHEMA || answer.role != CUMULATIVE_REVIEW_ROLE ||
                answer.acceptedOrder <= assignment.boundary || answer.assignmentDigest != assignment.digest || answer.selected.isBlank() ||
                slot.itemId != record.itemId || slot.objectiveId != record.objectiveId || slot.ownerUnit != record.unitNumber ||
                slot.itemDigest != record.itemDigest || slot.unitDigest != record.unitContentDigest)
                return "Mismatched spaced-review slot"
        }
    }
    return null
}
