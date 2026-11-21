package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import java.io.File
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

/** English engineering fixtures exercise the queue engine, never curriculum/release acceptance. */
class SpacedReviewSchedulerTest {
    private fun choice(id: String) = V2CheckItem(id, "FORM_SELECTION", "en", "Apply the requested rule.",
        options = listOf("key-$id", "wrong-$id"), answer = "key-$id", feedback = "Compare the pattern.",
        objectiveId = "objective", sourceId = "src-app-english-v2", reviewStatus = IMPLEMENTED_PENDING_FINAL_AUDIT)

    private fun typed(version: Int = 1): V2CheckItem {
        val key = V2TypedAnswerKey("english-key", version, listOf("Hello", "Hi"))
        return choice("typed").copy(kind = "TYPED_FORM", options = emptyList(), answer = "Hello",
            typedAnswerKey = key.copy(review = V2TypedKeyReview("English fixture reviewer", RESEARCH_VERIFIED, typedAnswerKeyDigest(key))))
    }

    private fun pack(): V2CurriculumPack {
        val items = listOf(choice("a"), choice("b"), typed())
        val objective = V2Objective("objective", 3, "Engineering fixture", "PRODUCTION", checkItemIds = items.map { it.id })
        return current.copy(items = items, units = listOf(current.unit(3)!!.copy(installState = "INSTALLED",
            objectives = listOf(objective), sessions = listOf(V2Session("session", "English fixture", 1,
                objectiveIds = listOf(objective.id), steps = listOf(V2Step("check", "CHECK", "Check", "Apply the pattern.",
                    itemIds = items.map { it.id })))))))
    }

    private fun answer(p: V2CurriculumPack, previous: V2SpacedReviewRecord? = null, id: String = "a",
                       correct: Boolean = true, time: Long = 100_000, order: Long = (previous?.acceptedOrder ?: 0) + 1): V2SpacedReviewRecord =
        scheduleSpacedReviewAnswer(p, previous, id, if (correct) p.item(id)!!.answer else "wrong-$id",
            "attempt:$id:$order", order, time)!!

    @Test fun missedItemsReturnInTenMinutesAndCorrectReviewsGrowAtTheirDueTimes() {
        val p = pack()
        val miss = answer(p, correct = false)
        val firstCorrect = answer(p)
        assertEquals(100_000 + SPACED_REVIEW_MISS_DELAY_MILLIS, miss.dueAtEpochMillis)
        assertEquals(0, miss.consecutiveCorrect)
        assertEquals(100_000 + SPACED_REVIEW_DAY_MILLIS, firstCorrect.dueAtEpochMillis)
        assertTrue(miss.dueAtEpochMillis < firstCorrect.dueAtEpochMillis)
        var state = firstCorrect
        for (days in listOf(2, 4, 8, 16)) {
            val at = state.dueAtEpochMillis
            state = answer(p, state, time = at)
            assertEquals(days, state.intervalDays)
            assertEquals(at + days * SPACED_REVIEW_DAY_MILLIS, state.dueAtEpochMillis)
        }
        val reset = answer(p, state, correct = false, time = state.dueAtEpochMillis)
        assertEquals(0, reset.intervalDays)
        assertEquals(0, reset.consecutiveCorrect)
        assertEquals(state.dueAtEpochMillis + SPACED_REVIEW_MISS_DELAY_MILLIS, reset.dueAtEpochMillis)
    }

    @Test fun immediateCorrectRetriesDoNotEraseMissReturnOrInflateIntervals() {
        val p = pack()
        val miss = answer(p, correct = false)
        val retry = answer(p, miss, time = 101_000)
        val another = answer(p, retry, time = 102_000)
        assertEquals(miss.dueAtEpochMillis, retry.dueAtEpochMillis)
        assertEquals(miss.dueAtEpochMillis, another.dueAtEpochMillis)
        assertEquals(0, another.consecutiveCorrect)
        val dueCorrect = answer(p, another, time = another.dueAtEpochMillis)
        assertEquals(1, dueCorrect.intervalDays)
        val early = answer(p, dueCorrect, time = dueCorrect.lastAnsweredAtEpochMillis + 1)
        assertEquals(dueCorrect.intervalDays, early.intervalDays)
        assertEquals(dueCorrect.dueAtEpochMillis, early.dueAtEpochMillis)
    }

    @Test fun dueQueryHasInclusiveBoundaryStableOrderAndNoFutureOrUnreachedItems() {
        val p = pack(); val a = answer(p); val b = answer(p, id = "b")
        assertTrue(dueSpacedReviews(p, listOf(a, b), setOf(3), a.dueAtEpochMillis - 1).isEmpty())
        assertEquals(listOf("a", "b"), dueSpacedReviews(p, listOf(b, a), setOf(3), a.dueAtEpochMillis).map { it.itemId })
        assertTrue(dueSpacedReviews(p, listOf(a, b), setOf(2), a.dueAtEpochMillis).isEmpty())
        val miss = answer(p, id = "b", correct = false)
        assertEquals(listOf("b", "a"), dueSpacedReviews(p, listOf(a, miss), setOf(3), a.dueAtEpochMillis).map { it.itemId })
        assertTrue(dueSpacedReviews(p, listOf(a, a), setOf(3), a.dueAtEpochMillis).isEmpty())
    }

    @Test fun rawTypedVariantsAreScoredByTheCurrentKeyAndUnavailableKeysNeverEnterQueue() {
        val p = pack()
        val variant = scheduleSpacedReviewAnswer(p, null, "typed", " Hi ", "attempt:variant", 1, 100_000)!!
        assertEquals(1, variant.consecutiveCorrect)
        assertEquals(typedResponseRuleDigest(), variant.responseRuleDigest)
        val wrong = scheduleSpacedReviewAnswer(p, variant, "typed", "Unknown", "review:wrong", 2, variant.dueAtEpochMillis)!!
        assertEquals(0, wrong.consecutiveCorrect)
        val unavailable = p.copy(items = p.items.map { if (it.id == "typed") it.copy(typedAnswerKey = it.typedAnswerKey!!.copy(review = null)) else it })
        assertNull(scheduleSpacedReviewAnswer(unavailable, null, "typed", "Hello", "attempt:unreviewed", 1, 100_000))
        assertFalse(spacedReviewRecordCurrent(unavailable, wrong))
        assertNull(scheduleSpacedReviewAnswer(p, null, "a", "not-an-option", "attempt:invalid", 1, 100_000))
        assertNull(scheduleSpacedReviewAnswer(p, null, "typed", " ", "attempt:blank", 1, 100_000))
    }

    @Test fun changedKeysTeachingAndRulesInvalidateOldDueStateAndResetTheNewSchedule() {
        val p = pack()
        val old = answer(p, id = "typed")
        val keyChanged = p.copy(items = p.items.map { if (it.id == "typed") typed(2) else it })
        val unit = p.units.single()
        val teachingChanged = p.copy(units = listOf(unit.copy(sessions = unit.sessions.map {
            it.copy(steps = it.steps.map { step -> step.copy(body = "A different English explanation.") })
        })))
        for (changed in listOf(keyChanged, teachingChanged)) {
            assertFalse(spacedReviewRecordCurrent(changed, old))
            assertTrue(dueSpacedReviews(changed, listOf(old), setOf(3), old.dueAtEpochMillis).isEmpty())
            val fresh = answer(changed, old, id = "typed", time = old.dueAtEpochMillis)
            assertEquals(1, fresh.consecutiveCorrect)
            assertEquals(1, fresh.intervalDays)
            assertTrue(spacedReviewRecordCurrent(changed, fresh))
        }
        assertFalse(spacedReviewRecordCurrent(p, old.copy(responseRuleDigest = "0".repeat(64))))
        assertFalse(spacedReviewRecordCurrent(p, old.copy(schedulerRuleDigest = "0".repeat(64))))
    }

    @Test fun durableOrderRejectsReplayAndClockRollbackCannotMoveAcceptedTimeBack() {
        val p = pack(); val first = answer(p)
        assertNull(scheduleSpacedReviewAnswer(p, first, "a", "key-a", first.lastEventId, 2, first.dueAtEpochMillis))
        assertNull(scheduleSpacedReviewAnswer(p, first, "a", "key-a", "attempt:old", 1, first.dueAtEpochMillis))
        assertNull(scheduleSpacedReviewAnswer(p, first, "b", "key-b", "attempt:other-item", 2, first.dueAtEpochMillis))
        val rollback = answer(p, first, correct = false, time = 99_000)
        assertEquals(first.lastAnsweredAtEpochMillis, rollback.lastAnsweredAtEpochMillis)
        assertEquals(first.lastAnsweredAtEpochMillis + SPACED_REVIEW_MISS_DELAY_MILLIS, rollback.dueAtEpochMillis)
        val forward = answer(p, first, time = first.dueAtEpochMillis + 10 * SPACED_REVIEW_DAY_MILLIS)
        assertEquals(2, forward.intervalDays)
        assertEquals(forward.lastAnsweredAtEpochMillis + 2 * SPACED_REVIEW_DAY_MILLIS, forward.dueAtEpochMillis)
    }

    @Test fun onlyCurrentReachableGatingCheckedItemsCanBeScheduled() {
        val p = pack(); val record = answer(p)
        val unit = p.units.single(); val objective = unit.objectives.single()
        val nongating = p.copy(units = listOf(unit.copy(objectives = listOf(objective.copy(gating = false)))))
        val orphan = p.copy(units = listOf(unit.copy(objectives = listOf(objective.copy(checkItemIds = emptyList())), sessions = emptyList())))
        val hidden = p.copy(units = listOf(unit.copy(installState = "NOT_YET_INSTALLED")))
        val unknownKind = p.copy(items = p.items.map { if (it.id == "a") it.copy(kind = "FREE_WRITING") else it })
        val ambiguous = p.copy(items = p.items + p.items.first())
        for (changed in listOf(nongating, orphan, hidden, unknownKind, ambiguous, p.copy(schemaVersion = 99))) {
            assertNull(scheduleSpacedReviewAnswer(changed, null, "a", "key-a", "attempt:test", 1, 100_000))
            assertFalse(spacedReviewRecordCurrent(changed, record))
        }
        val practiceOnly = p.copy(units = listOf(unit.copy(objectives = listOf(objective.copy(checkItemIds = emptyList())),
            sessions = unit.sessions.map { it.copy(steps = it.steps.map { s -> s.copy(kind = "PRACTICE") }) })))
        assertNotNull(scheduleSpacedReviewAnswer(practiceOnly, null, "a", "key-a", "attempt:practice", 1, 100_000))
    }

    @Test fun currentSerializationRoundTripsAndRejectsMissingOrUnsupportedStructure() {
        val record = answer(pack())
        val json = Json { encodeDefaults = true }
        val bytes = json.encodeToString(V2SpacedReviewRecord.serializer(), record)
        assertEquals(record, json.decodeFromString(V2SpacedReviewRecord.serializer(), bytes))
        assertNull(spacedReviewRecordDefect(record))
        assertNotNull(spacedReviewRecordDefect(record.copy(schema = 0)))
        assertNull(scheduleSpacedReviewAnswer(pack(), record.copy(schema = 0), "a", "key-a", "attempt:new", 2, record.dueAtEpochMillis))
        assertNotNull(spacedReviewRecordDefect(record.copy(acceptedOrder = 0)))
        assertNotNull(spacedReviewRecordDefect(record.copy(intervalDays = 7)))
        assertNotNull(spacedReviewRecordDefect(record.copy(itemDigest = "not-a-digest")))
        assertNotNull(spacedReviewRecordDefect(record.copy(lastEventId = "attempt:")))
        assertNotNull(spacedReviewRecordDefect(record.copy(dueAtEpochMillis = record.lastAnsweredAtEpochMillis - 1)))
        assertNotNull(spacedReviewRecordDefect(record.copy(dueAtEpochMillis = record.dueAtEpochMillis + 1)))
        try {
            json.decodeFromString(V2SpacedReviewRecord.serializer(), "{\"schema\":1}")
            fail("A missing current record must not be interpreted as an older development meaning")
        } catch (_: SerializationException) { }
    }

    @Test fun schedulingCapsLongIntervalsAndDatesWithoutIntegerOverflow() {
        val p = pack(); var state = answer(p)
        repeat(20) { state = answer(p, state, time = state.dueAtEpochMillis) }
        assertEquals(SPACED_REVIEW_MAX_INTERVAL_DAYS, state.intervalDays)
        assertEquals(13, state.consecutiveCorrect)
        assertNull(spacedReviewRecordDefect(state))
        val lastDate = answer(p, time = SPACED_REVIEW_MAX_TIME - 1)
        assertEquals(SPACED_REVIEW_MAX_TIME, lastDate.dueAtEpochMillis)
        assertNull(spacedReviewRecordDefect(lastDate))
        assertNull(scheduleSpacedReviewAnswer(p, null, "a", "key-a", "attempt:bad-date", 1, Long.MAX_VALUE))
        assertNull(scheduleSpacedReviewAnswer(p, null, "a", "key-a", "attempt:negative-date", 1, -1))
        val exhausted = answer(p, order = Long.MAX_VALUE)
        assertNull(scheduleSpacedReviewAnswer(p, exhausted, "a", "key-a", "attempt:wrapped", Long.MIN_VALUE, 100_000))
    }

    @Test fun queriesAreDeterministicAndNeverMutateRecordsOrSeedUnansweredItems() {
        val p = pack(); val initial = p.items.map { it.id to it.answer }; val records = listOf(answer(p))
        val before = records.single().copy()
        assertTrue(dueSpacedReviews(p, emptyList(), setOf(3), 100_000).isEmpty())
        val first = dueSpacedReviews(p, records, setOf(3), before.dueAtEpochMillis)
        assertEquals(first, dueSpacedReviews(p, records, setOf(3), before.dueAtEpochMillis))
        assertEquals(before, records.single())
        assertEquals(initial, p.items.map { it.id to it.answer })
        assertTrue(dueSpacedReviews(p, records, setOf(3), -1).isEmpty())
    }

    companion object {
        private val root = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
        private val current by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()) }
    }
}
