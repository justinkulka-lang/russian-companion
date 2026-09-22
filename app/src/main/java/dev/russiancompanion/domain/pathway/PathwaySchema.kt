package dev.russiancompanion.domain.pathway

/**
 * Full v1 pathway schema for the Penguin-concurrent beginner arc.
 *
 * Capacity is the book's numbered lesson span (lesson identification only).
 * A shipped pathway may install fewer segments; it must never invent lessons
 * outside this span or reproduce book text.
 */
internal const val PENGUIN_BEGINNER_ARC_LESSON_COUNT = 30

internal data class PathwaySegment(
    val id: String,
    val lessonReference: String,
    val lessonNumber: Int,
    val sortOrder: Int,
)

internal data class PathwayValidation(
    val segments: List<PathwaySegment>,
    val rejectedIds: Set<String>,
) {
    val isEmpty: Boolean get() = segments.isEmpty()

    val coversFullArc: Boolean
        get() = segments.map { it.lessonNumber }.toSet() == (1..PENGUIN_BEGINNER_ARC_LESSON_COUNT).toSet()
}

internal fun validatePathwaySegments(
    candidates: List<PathwaySegment>,
): PathwayValidation {
    val rejected = mutableSetOf<String>()
    val byLesson = mutableMapOf<Int, PathwaySegment>()
    val bySort = mutableMapOf<Int, PathwaySegment>()
    val accepted = mutableListOf<PathwaySegment>()
    for (segment in candidates.sortedWith(compareBy({ it.sortOrder }, { it.id }))) {
        if (segment.id.isBlank() || segment.lessonReference.isBlank()) {
            rejected += segment.id
            continue
        }
        if (segment.lessonNumber !in 1..PENGUIN_BEGINNER_ARC_LESSON_COUNT) {
            rejected += segment.id
            continue
        }
        if (segment.sortOrder < 0) {
            rejected += segment.id
            continue
        }
        if (byLesson.containsKey(segment.lessonNumber) || bySort.containsKey(segment.sortOrder)) {
            rejected += segment.id
            continue
        }
        byLesson[segment.lessonNumber] = segment
        bySort[segment.sortOrder] = segment
        accepted += segment
    }
    return PathwayValidation(segments = accepted, rejectedIds = rejected)
}

internal enum class PlanActivityKind {
    BOOK,
    ANKI_DROID,
    LISTEN_RECORD_COMPARE,
    TUTOR,
}

internal data class PlanDefinitionDraft(
    val id: String,
    val segmentId: String,
    val activity: PlanActivityKind,
    val instruction: String,
    val sortOrder: Int,
    val required: Boolean,
    val lessonNumber: Int?,
    val audioModelIds: List<String>,
)

internal data class PlanAdmission(
    val accepted: Boolean,
    val reason: String? = null,
)

/**
 * Admits a plan definition against an installed pathway segment and the set of
 * known audio model identities from the packaged catalog. Book and AnkiDroid
 * work bind to a lesson number. Listen-record-compare binds to catalog word IDs.
 * Tutor tasks stay optional and unbound to lesson content.
 */
internal fun admitPlanDefinition(
    draft: PlanDefinitionDraft,
    segmentLessonNumber: Int?,
    knownAudioModelIds: Set<String>,
): PlanAdmission {
    if (draft.id.isBlank() || draft.segmentId.isBlank() || draft.instruction.isBlank() || draft.sortOrder < 0) {
        return PlanAdmission(false, "identity")
    }
    if (segmentLessonNumber == null) {
        return PlanAdmission(false, "missing-segment")
    }
    return when (draft.activity) {
        PlanActivityKind.BOOK, PlanActivityKind.ANKI_DROID -> {
            val lesson = draft.lessonNumber
                ?: return PlanAdmission(false, "lesson-required")
            if (lesson !in 1..PENGUIN_BEGINNER_ARC_LESSON_COUNT) {
                return PlanAdmission(false, "lesson-out-of-arc")
            }
            if (lesson != segmentLessonNumber) {
                return PlanAdmission(false, "lesson-mismatch")
            }
            if (draft.audioModelIds.isNotEmpty()) {
                return PlanAdmission(false, "audio-not-allowed")
            }
            PlanAdmission(true)
        }
        PlanActivityKind.LISTEN_RECORD_COMPARE -> {
            if (draft.lessonNumber != null) {
                return PlanAdmission(false, "lesson-not-allowed")
            }
            if (draft.audioModelIds.isEmpty()) {
                return PlanAdmission(false, "audio-required")
            }
            if (draft.audioModelIds.any { it.isBlank() }) {
                return PlanAdmission(false, "audio-blank")
            }
            if (draft.audioModelIds.size != draft.audioModelIds.toSet().size) {
                return PlanAdmission(false, "audio-duplicate")
            }
            if (!knownAudioModelIds.containsAll(draft.audioModelIds)) {
                return PlanAdmission(false, "audio-unknown")
            }
            PlanAdmission(true)
        }
        PlanActivityKind.TUTOR -> {
            if (draft.required) {
                return PlanAdmission(false, "tutor-must-be-optional")
            }
            if (draft.lessonNumber != null || draft.audioModelIds.isNotEmpty()) {
                return PlanAdmission(false, "tutor-unbound")
            }
            PlanAdmission(true)
        }
    }
}

internal fun firstPrescribedPlayable(
    prescribedAudioModelIds: List<String>,
    playableAudioIds: Set<String>,
): String? {
    return prescribedAudioModelIds.firstOrNull { it in playableAudioIds }
}
