package dev.russiancompanion.v2.ui

import dev.russiancompanion.v2.curriculum.CLOSED_REVIEW_STATES
import dev.russiancompanion.v2.curriculum.FINAL_CURRICULUM_AUDIT_PASSED
import dev.russiancompanion.v2.curriculum.IMPLEMENTED_PENDING_FINAL_AUDIT
import dev.russiancompanion.v2.curriculum.INDEPENDENT_AUDIT_VERIFIED
import dev.russiancompanion.v2.curriculum.OWNER_ACCEPTED
import dev.russiancompanion.v2.curriculum.RESEARCH_VERIFIED

/**
 * T16-n2 (owner decision D31b): a reference's review status stays visible, because the app is honest about content that has
 * not had its final review, but a learner sees one plain label, never the raw enum value. The metadata is unchanged; this
 * mapping lives in UI code. An unknown value falls back to the not-yet-checked label rather than showing the value.
 */
internal object V2ReviewStatusLabels {
    const val NOT_YET_CHECKED = "Not yet checked by a qualified reviewer."

    val LABELS: Map<String, String> = linkedMapOf(
        RESEARCH_VERIFIED to NOT_YET_CHECKED,
        IMPLEMENTED_PENDING_FINAL_AUDIT to NOT_YET_CHECKED,
        FINAL_CURRICULUM_AUDIT_PASSED to "Checked by a qualified reviewer; an independent check is still to come.",
        INDEPENDENT_AUDIT_VERIFIED to "Checked by a qualified reviewer and independently verified.",
        OWNER_ACCEPTED to "Checked and accepted for this course.",
        "APPROVED" to "Checked by a qualified reviewer.",
        "REJECTED" to "Withdrawn after review. Do not rely on this entry.",
        "NOT_REQUIRED" to "This notice does not need a separate review.",
    )

    init {
        check(LABELS.keys == CLOSED_REVIEW_STATES) { "Every review status needs one plain label" }
    }

    fun label(status: String): String = LABELS[status] ?: NOT_YET_CHECKED
}
