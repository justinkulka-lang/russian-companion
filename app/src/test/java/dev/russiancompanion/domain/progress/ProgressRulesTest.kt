package dev.russiancompanion.domain.progress

import dev.russiancompanion.domain.plan.ActivityLogView
import dev.russiancompanion.domain.plan.RecordedCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressRulesTest {
    @Test
    fun emptyReviewKeepsEveryCategoryAtZero() {
        val review = reviewProgress(emptyList())
        assertTrue(review.readable)
        assertEquals(0, review.totalMinutes)
        assertEquals(0, review.totalCompletions)
        assertEquals(RecordedCategory.entries, review.categories.map { it.category })
        assertTrue(review.categories.all { it.recordedMinutes == 0L && it.completionCount == 0 })
        assertTrue(review.recentLogs.isEmpty())
        assertEquals(review, ProgressReview.empty())
    }

    @Test
    fun recordedReadingAddsToThatCategoryAndTheCumulativeTotal() {
        val review = reviewProgress(
            listOf(
                ActivityLogView(
                    id = "older",
                    category = RecordedCategory.READING,
                    minutes = 15,
                    completed = true,
                    recordedAtLocal = "2026-09-21T08:00",
                ),
                ActivityLogView(
                    id = "newer",
                    category = RecordedCategory.LISTENING,
                    minutes = 0,
                    completed = false,
                    recordedAtLocal = "2026-09-21T09:00",
                ),
            ),
        )
        val reading = review.categories.single { it.category == RecordedCategory.READING }
        assertEquals(15L, reading.recordedMinutes)
        assertEquals(1, reading.completionCount)
        assertEquals(15L, review.totalMinutes)
        assertEquals(1, review.totalCompletions)
        assertEquals(listOf("newer", "older"), review.recentLogs.map { it.id })
        assertTrue(
            review.categories
                .filter { it.category != RecordedCategory.READING && it.category != RecordedCategory.LISTENING }
                .all { it.recordedMinutes == 0L && it.completionCount == 0 },
        )
    }

    @Test
    fun negativeDurationMakesTheReviewUnreadable() {
        val review = reviewProgress(
            listOf(
                ActivityLogView("x", RecordedCategory.READING, minutes = -1, completed = false, recordedAtLocal = "t"),
            ),
        )
        assertFalse(review.readable)
        assertTrue(review.categories.isEmpty())
        assertTrue(review.recentLogs.isEmpty())
        assertEquals(0, review.totalMinutes)
    }

    @Test
    fun attestationLabelsFollowTheRecordedCategory() {
        assertEquals(BOOK_PRACTICE, activityAttestationLabel(RecordedCategory.GRAMMAR, selfAttested = true))
        assertEquals(
            ANKI_PRACTICE,
            activityAttestationLabel(RecordedCategory.VOCABULARY_AND_FORMS, selfAttested = true),
        )
        assertEquals("", activityAttestationLabel(RecordedCategory.GRAMMAR, selfAttested = false))
        assertEquals("", activityAttestationLabel(RecordedCategory.READING, selfAttested = false))
        assertEquals("Self-attested", activityAttestationLabel(RecordedCategory.WRITING, selfAttested = true))
        assertEquals(BOOK_ATTESTATION, evidenceAttestationLabel("BOOK_REVISION_SCORE"))
        assertEquals(ANKI_ATTESTATION, evidenceAttestationLabel("ANKI_DROID_STATUS"))
        assertEquals("User recorded", evidenceAttestationLabel("PRESCRIBED_WORK_COMPLETION"))
        assertEquals("User recorded", evidenceAttestationLabel("TUTOR_SIGN_OFF"))
    }

    @Test
    fun zeroMinuteCompletionIsRecordedActivity() {
        val review = reviewProgress(
            listOf(
                ActivityLogView(
                    id = "done",
                    category = RecordedCategory.GRAMMAR,
                    minutes = 0,
                    completed = true,
                    recordedAtLocal = "2026-09-21T09:00",
                    selfAttested = true,
                ),
            ),
        )
        val grammar = review.categories.single { it.category == RecordedCategory.GRAMMAR }
        assertEquals(0L, grammar.recordedMinutes)
        assertEquals(1, grammar.completionCount)
        assertFalse(categoryShowsImbalance(grammar))
        assertTrue(review.categories.filter { it.category != RecordedCategory.GRAMMAR }.all(::categoryShowsImbalance))
        assertEquals(0L, review.totalMinutes)
        assertEquals(1, review.totalCompletions)
    }
}
