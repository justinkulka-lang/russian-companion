package dev.russiancompanion.domain.pathway

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PathwaySchemaTest {
    @Test
    fun pathwayAcceptsTheFullBeginnerArcAndRejectsDuplicatesOrOutOfRangeLessons() {
        val full = (1..PENGUIN_BEGINNER_ARC_LESSON_COUNT).map { lesson ->
            PathwaySegment(
                id = "segment-$lesson",
                lessonReference = "Lesson $lesson",
                lessonNumber = lesson,
                sortOrder = lesson,
            )
        }
        val validated = validatePathwaySegments(
            full + listOf(
                PathwaySegment("dup-lesson", "Lesson 1 again", lessonNumber = 1, sortOrder = 100),
                PathwaySegment("dup-sort", "Lesson Extra", lessonNumber = 31, sortOrder = 1),
                PathwaySegment("out-of-range", "Lesson 99", lessonNumber = 99, sortOrder = 200),
                PathwaySegment("", "Blank", lessonNumber = 2, sortOrder = 201),
            ),
        )
        assertEquals(PENGUIN_BEGINNER_ARC_LESSON_COUNT, validated.segments.size)
        assertTrue(validated.coversFullArc)
        assertTrue(validated.rejectedIds.contains("dup-lesson"))
        assertTrue(validated.rejectedIds.contains("out-of-range"))
        assertTrue(validated.rejectedIds.contains(""))
        assertFalse(validatePathwaySegments(full.take(3)).coversFullArc)
    }

    @Test
    fun planAdmissionBindsBookAndAnkiToLessonNumbersAndListenToCatalogIds() {
        assertTrue(
            admitPlanDefinition(
                draft("plan-book", PlanActivityKind.BOOK, lessonNumber = 7),
                segmentLessonNumber = 7,
                eligibleAudioModelIds = emptySet(),
            ).accepted,
        )
        assertEquals(
            "lesson-mismatch",
            admitPlanDefinition(
                draft("plan-book", PlanActivityKind.BOOK, lessonNumber = 7),
                segmentLessonNumber = 8,
                eligibleAudioModelIds = emptySet(),
            ).reason,
        )
        assertEquals(
            "audio-required",
            admitPlanDefinition(
                draft("plan-listen", PlanActivityKind.LISTEN_RECORD_COMPARE),
                segmentLessonNumber = 1,
                eligibleAudioModelIds = setOf("audio-1"),
            ).reason,
        )
        assertTrue(
            admitPlanDefinition(
                draft(
                    "plan-listen",
                    PlanActivityKind.LISTEN_RECORD_COMPARE,
                    audioModelIds = listOf("audio-1", "audio-2"),
                ),
                segmentLessonNumber = 1,
                eligibleAudioModelIds = setOf("audio-1", "audio-2", "audio-3"),
            ).accepted,
        )
        assertEquals(
            "audio-unknown",
            admitPlanDefinition(
                draft(
                    "plan-listen",
                    PlanActivityKind.LISTEN_RECORD_COMPARE,
                    audioModelIds = listOf("audio-1", "audio-excluded"),
                ),
                segmentLessonNumber = 1,
                eligibleAudioModelIds = setOf("audio-1"),
            ).reason,
        )
        assertEquals(
            "tutor-must-be-optional",
            admitPlanDefinition(
                draft("plan-tutor", PlanActivityKind.TUTOR, required = true),
                segmentLessonNumber = 1,
                eligibleAudioModelIds = emptySet(),
            ).reason,
        )
        assertTrue(
            admitPlanDefinition(
                draft("plan-tutor", PlanActivityKind.TUTOR, required = false),
                segmentLessonNumber = 1,
                eligibleAudioModelIds = emptySet(),
            ).accepted,
        )
    }

    @Test
    fun firstPrescribedPlayableKeepsCatalogOrder() {
        assertEquals(
            "audio-2",
            firstPrescribedPlayable(listOf("audio-1", "audio-2", "audio-3"), setOf("audio-2", "audio-3")),
        )
        assertNull(firstPrescribedPlayable(listOf("audio-1"), setOf("audio-2")))
    }

    private fun draft(
        id: String,
        activity: PlanActivityKind,
        required: Boolean = true,
        lessonNumber: Int? = null,
        audioModelIds: List<String> = emptyList(),
    ): PlanDefinitionDraft {
        return PlanDefinitionDraft(
            id = id,
            segmentId = "segment-1",
            activity = activity,
            instruction = "Instruction $id",
            sortOrder = 1,
            required = required,
            lessonNumber = lessonNumber,
            audioModelIds = audioModelIds,
        )
    }
}
