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
    fun beginnerArcReleaseReadyRequiresThirtyLessonsWithRequiredPlansAndGates() {
        val segments = (1..PENGUIN_BEGINNER_ARC_LESSON_COUNT).map { lesson ->
            PathwaySegment("segment-$lesson", "Lesson $lesson", lesson, lesson)
        }
        assertTrue(
            beginnerArcReleaseReady(
                segments,
                arcPlans(1..PENGUIN_BEGINNER_ARC_LESSON_COUNT),
                arcGates(1..PENGUIN_BEGINNER_ARC_LESSON_COUNT),
            ),
        )
        assertFalse(
            beginnerArcReleaseReady(
                segments.take(3),
                arcPlans(1..3),
                arcGates(1..3),
            ),
        )
        assertFalse(
            beginnerArcReleaseReady(
                segments.dropLast(1),
                arcPlans(1 until PENGUIN_BEGINNER_ARC_LESSON_COUNT),
                arcGates(1 until PENGUIN_BEGINNER_ARC_LESSON_COUNT),
            ),
        )
        assertFalse(
            beginnerArcReleaseReady(
                segments.filter { it.lessonNumber != 12 },
                arcPlans(1..PENGUIN_BEGINNER_ARC_LESSON_COUNT).filter { it.segmentId != "segment-12" },
                arcGates(1..PENGUIN_BEGINNER_ARC_LESSON_COUNT).filter { it.segmentId != "segment-12" },
            ),
        )
        assertFalse(
            beginnerArcReleaseReady(
                segments,
                arcPlans(1..PENGUIN_BEGINNER_ARC_LESSON_COUNT)
                    .filterNot { it.segmentId == "segment-30" && it.activity == PlanActivityKind.LISTEN_RECORD_COMPARE },
                arcGates(1..PENGUIN_BEGINNER_ARC_LESSON_COUNT),
            ),
        )
        assertFalse(
            beginnerArcReleaseReady(
                segments,
                arcPlans(1..PENGUIN_BEGINNER_ARC_LESSON_COUNT),
                arcGates(1 until PENGUIN_BEGINNER_ARC_LESSON_COUNT),
            ),
        )
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

    private fun arcPlans(lessons: IntRange): List<ArcPlanCoverage> {
        return lessons.flatMap { lesson ->
            listOf(
                ArcPlanCoverage("segment-$lesson", PlanActivityKind.BOOK, required = true),
                ArcPlanCoverage("segment-$lesson", PlanActivityKind.ANKI_DROID, required = true),
                ArcPlanCoverage("segment-$lesson", PlanActivityKind.LISTEN_RECORD_COMPARE, required = true),
                ArcPlanCoverage("segment-$lesson", PlanActivityKind.TUTOR, required = false),
            )
        }
    }

    private fun arcGates(lessons: IntRange): List<ArcGateCoverage> {
        return lessons.map { lesson -> ArcGateCoverage("segment-$lesson") }
    }
}
