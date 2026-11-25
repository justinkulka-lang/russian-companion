package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream
import java.io.File

/** A2-28: at least 8 items, 3 earlier objectives, more than one skill, and no item in 4 consecutive unit reviews. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class CumulativeReviewA228Test {
    private val ownedDatabases = mutableListOf<String>()
    @After fun reset() {
        for (name in ownedDatabases) { context().deleteDatabase(name); File(context().filesDir, "recordings-$name").deleteRecursively() }
        ownedDatabases.clear()
    }
    private fun isolatedStore(label: String): LocalStore {
        val name = "a228-$label-${System.nanoTime()}.db"; ownedDatabases += name
        return LocalStore.openNamed(context(), name)
    }

    @Test fun everyDeclaredPolicyKeepsAnchorsFirstAndDrawsOnlyEarlierRegistryObjectives() {
        val p = pack()
        for (target in 3..30) {
            val anchor = cumulativeReviewAnchorPolicy(target)!!
            val policy = cumulativeReviewPolicy(target)!!
            assertEquals(anchor.id, policy.id)
            assertEquals(anchor.pool, policy.anchors)
            assertEquals(CUMULATIVE_REVIEW_ALLOCATION_RULE, policy.allocationRule)
            assertNotEquals(anchor.digest, policy.digest)
            assertEquals(policy, cumulativeReviewDeclaredPolicy(p, target))
            for (spec in policy.pool) {
                val owner = p.units.single { u -> u.objectives.any { it.id == spec.objectiveId } }
                assertTrue("${spec.objectiveId} is not earlier than $target", owner.number < target)
                val o = owner.objectives.single { it.id == spec.objectiveId }
                assertTrue(spec.candidateItemIds.all { it in o.checkItemIds })
            }
            assertTrue(policy.pool.size >= CUMULATIVE_REVIEW_MINIMUM_OBJECTIVES)
            assertTrue(policy.pool.sumOf { it.candidateItemIds.size } >= CUMULATIVE_REVIEW_MINIMUM_ITEMS)
            assertTrue(policy.pool.map { it.skill }.distinct().size >= 2)
        }
        // The registry reuses authored declarations; only the Unit 1 iotated-letter objective is added.
        val authored = (3..30).flatMap { cumulativeReviewAnchorPolicy(it)!!.pool.map { spec -> spec.objectiveId } }.toSet()
        assertEquals(setOf("obj-u1-iotated"), CumulativeReviewRegistry.entries.map { it.objectiveId }.toSet() - authored)
    }

    @Test fun completePathThroughUnit30MeetsSizeObjectiveSkillAndRepeatRules() {
        val p = pack()
        isolatedStore("path").use { s ->
            val assignments = walk(p, s, 30)
            for ((target, a) in assignments) {
                println("A228 unit $target: " + a.slots.joinToString { "${it.objectiveId}/${it.itemId}" })
                assertTrue("unit $target has ${a.slots.size} items", a.slots.size >= 8)
                assertEquals(a.slots.size, a.slots.map { it.itemId }.distinct().size)
                assertTrue("unit $target objectives", a.slots.map { it.objectiveId }.distinct().size >= 3)
                assertTrue("unit $target skills", a.slots.map { it.skill }.distinct().size >= 2)
                assertTrue(a.slots.all { it.ownerUnit < target })
                val policy = cumulativeReviewPolicy(target)!!
                assertEquals(policy.digest, a.policyDigest)
                // Every anchor still open under the repeat limit is represented, in authored order, first.
                val excluded = cumulativeReviewRepeatExcluded(learnerBefore(s, a), target)
                val openAnchors = policy.anchors.filter { spec -> spec.candidateItemIds.any { it !in excluded } }
                assertEquals(openAnchors.map { it.objectiveId }, a.slots.take(openAnchors.size).map { it.objectiveId })
            }
            // No item appears in more than three consecutive unit reviews.
            val ordered = (3..30).map { t -> assignments.getValue(t).slots.map { it.itemId }.toSet() }
            for (start in 0..ordered.size - 4) {
                val inAllFour = ordered[start] intersect ordered[start + 1] intersect ordered[start + 2] intersect ordered[start + 3]
                assertTrue("units ${start + 3}..${start + 6} repeat $inAllFour", inAllFour.isEmpty())
            }
            // The rule is exercised, not vacuous: the single decoding anchor is barred after three consecutive uses.
            val decodeRuns = (3..30).filter { t -> assignments.getValue(t).slots.any { it.itemId == "u1-i-decode-pair" } }
            assertTrue(decodeRuns.isNotEmpty())
            assertTrue((15..30).any { t -> "u1-i-decode-pair" in cumulativeReviewRepeatExcluded(
                learnerBefore(s, assignments.getValue(t)), t) })
        }
    }

    /**
     * T15g: the recorded A2-28 measurement for Block 2 (Units 6-10), re-runnable. A fresh learner completes Units 1-10
     * with correct governed attempts, current step identities and assignment-bound review completions; no eligibility
     * or allocation rule is bypassed. Per target unit: items, distinct earlier objectives, distinct skills, earlier-unit
     * items, the longest run of consecutive reviews ending at that unit for any of its items, and the candidates the
     * repeat limiter excluded. These numbers describe this exercised history; other histories can select other items.
     */
    @Test fun block2DueReviewMeasurementIsRecorded() {
        val p = pack()
        isolatedStore("block2").use { s ->
            val a = walk(p, s, 10)
            val expected = mapOf(
                6 to listOf(8, 6, 5, 8, 2, 1), 7 to listOf(8, 8, 7, 8, 1, 0), 8 to listOf(8, 8, 7, 8, 2, 0),
                9 to listOf(8, 8, 8, 8, 3, 0), 10 to listOf(8, 8, 8, 8, 3, 2),
            )
            for ((t, e) in expected) {
                val slots = a.getValue(t).slots
                val run = slots.maxOf { slot ->
                    var k = 0; var u = t
                    while (u >= 3 && a.getValue(u).slots.any { it.itemId == slot.itemId }) { k++; u-- }
                    k
                }
                val excluded = cumulativeReviewRepeatExcluded(learnerBefore(s, a.getValue(t)), t).size
                val measured = listOf(slots.size, slots.map { it.objectiveId }.distinct().size, slots.map { it.skill }.distinct().size,
                    slots.count { it.ownerUnit < t }, run, excluded)
                println("A228-BLOCK2 unit $t items/objectives/skills/earlier/run/excluded=$measured " +
                    slots.joinToString { "${it.objectiveId}/${it.itemId}" })
                assertEquals("unit $t", e, measured)
            }
            // Every four-review window ending in Units 6-10 shares no item.
            for (end in 6..10) {
                val window = (end - 3..end).map { t -> a.getValue(t).slots.map { it.itemId }.toSet() }
                assertTrue("units ${end - 3}..$end", window.reduce { x, y -> x intersect y }.isEmpty())
            }
        }
    }

    /**
     * T16: the recorded A2-28 measurement for Unit 11, measured as block2DueReviewMeasurementIsRecorded measures Block 2:
     * a fresh learner completes Units 1-11 with correct governed attempts and assignment-bound review completions.
     */
    @Test fun unit11DueReviewMeasurementIsRecorded() {
        val p = pack()
        isolatedStore("unit11").use { s ->
            val a = walk(p, s, 11)
            val t = 11
            val slots = a.getValue(t).slots
            val run = slots.maxOf { slot ->
                var k = 0; var u = t
                while (u >= 3 && a.getValue(u).slots.any { it.itemId == slot.itemId }) { k++; u-- }
                k
            }
            val excluded = cumulativeReviewRepeatExcluded(learnerBefore(s, a.getValue(t)), t).size
            val measured = listOf(slots.size, slots.map { it.objectiveId }.distinct().size, slots.map { it.skill }.distinct().size,
                slots.count { it.ownerUnit < t }, run, excluded)
            println("A228-UNIT11 items/objectives/skills/earlier/run/excluded=$measured " + slots.joinToString { "${it.objectiveId}/${it.itemId}" })
            assertEquals(listOf(8, 8, 8, 8, 2, 1), measured)
            // The four-review window ending at Unit 11 shares no item.
            val window = (8..11).map { u -> a.getValue(u).slots.map { it.itemId }.toSet() }
            assertTrue(window.reduce { x, y -> x intersect y }.isEmpty())
        }
    }

    /** T16b: the recorded A2-28 measurement for Unit 12, measured as unit11DueReviewMeasurementIsRecorded. */
    @Test fun unit12DueReviewMeasurementIsRecorded() {
        val p = pack()
        isolatedStore("unit12").use { s ->
            val a = walk(p, s, 12)
            val t = 12
            val slots = a.getValue(t).slots
            val run = slots.maxOf { slot ->
                var k = 0; var u = t
                while (u >= 3 && a.getValue(u).slots.any { it.itemId == slot.itemId }) { k++; u-- }
                k
            }
            val excluded = cumulativeReviewRepeatExcluded(learnerBefore(s, a.getValue(t)), t).size
            val measured = listOf(slots.size, slots.map { it.objectiveId }.distinct().size, slots.map { it.skill }.distinct().size,
                slots.count { it.ownerUnit < t }, run, excluded)
            println("A228-UNIT12 items/objectives/skills/earlier/run/excluded=$measured " + slots.joinToString { "${it.objectiveId}/${it.itemId}" })
            assertEquals(listOf(8, 8, 7, 8, 3, 0), measured)
            val window = (9..12).map { u -> a.getValue(u).slots.map { it.itemId }.toSet() }
            assertTrue(window.reduce { x, y -> x intersect y }.isEmpty())
        }
    }

    /** T16c: the recorded A2-28 measurement for Unit 13, measured as unit12DueReviewMeasurementIsRecorded. */
    @Test fun unit13DueReviewMeasurementIsRecorded() {
        val p = pack()
        isolatedStore("unit13").use { s ->
            val a = walk(p, s, 13)
            val t = 13
            val slots = a.getValue(t).slots
            val run = slots.maxOf { slot ->
                var k = 0; var u = t
                while (u >= 3 && a.getValue(u).slots.any { it.itemId == slot.itemId }) { k++; u-- }
                k
            }
            val excluded = cumulativeReviewRepeatExcluded(learnerBefore(s, a.getValue(t)), t).size
            val measured = listOf(slots.size, slots.map { it.objectiveId }.distinct().size, slots.map { it.skill }.distinct().size,
                slots.count { it.ownerUnit < t }, run, excluded)
            println("A228-UNIT13 items/objectives/skills/earlier/run/excluded=$measured " + slots.joinToString { "${it.objectiveId}/${it.itemId}" })
            assertEquals(listOf(8, 8, 8, 8, 2, 1), measured)
            val window = (10..13).map { u -> a.getValue(u).slots.map { it.itemId }.toSet() }
            assertTrue(window.reduce { x, y -> x intersect y }.isEmpty())
        }
    }

    /** T16d: the recorded A2-28 measurement for Unit 14, measured as unit13DueReviewMeasurementIsRecorded. */
    @Test fun unit14DueReviewMeasurementIsRecorded() {
        val p = pack()
        isolatedStore("unit14").use { s ->
            val a = walk(p, s, 14)
            val t = 14
            val slots = a.getValue(t).slots
            val run = slots.maxOf { slot ->
                var k = 0; var u = t
                while (u >= 3 && a.getValue(u).slots.any { it.itemId == slot.itemId }) { k++; u-- }
                k
            }
            val excluded = cumulativeReviewRepeatExcluded(learnerBefore(s, a.getValue(t)), t).size
            val measured = listOf(slots.size, slots.map { it.objectiveId }.distinct().size, slots.map { it.skill }.distinct().size,
                slots.count { it.ownerUnit < t }, run, excluded)
            println("A228-UNIT14 items/objectives/skills/earlier/run/excluded=$measured " + slots.joinToString { "${it.objectiveId}/${it.itemId}" })
            assertEquals(listOf(8, 8, 8, 8, 1, 0), measured)
            val window = (11..14).map { u -> a.getValue(u).slots.map { it.itemId }.toSet() }
            assertTrue(window.reduce { x, y -> x intersect y }.isEmpty())
        }
    }

    /** T16e: the recorded A2-28 measurement for Unit 15, measured as unit14DueReviewMeasurementIsRecorded. */
    @Test fun unit15DueReviewMeasurementIsRecorded() {
        val p = pack()
        isolatedStore("unit15").use { s ->
            val a = walk(p, s, 15)
            val t = 15
            val slots = a.getValue(t).slots
            val run = slots.maxOf { slot ->
                var k = 0; var u = t
                while (u >= 3 && a.getValue(u).slots.any { it.itemId == slot.itemId }) { k++; u-- }
                k
            }
            val excluded = cumulativeReviewRepeatExcluded(learnerBefore(s, a.getValue(t)), t).size
            val measured = listOf(slots.size, slots.map { it.objectiveId }.distinct().size, slots.map { it.skill }.distinct().size,
                slots.count { it.ownerUnit < t }, run, excluded)
            println("A228-UNIT15 items/objectives/skills/earlier/run/excluded=$measured " + slots.joinToString { "${it.objectiveId}/${it.itemId}" })
            assertEquals(listOf(8, 8, 8, 8, 2, 0), measured)
            val window = (12..15).map { u -> a.getValue(u).slots.map { it.itemId }.toSet() }
            assertTrue(window.reduce { x, y -> x intersect y }.isEmpty())
        }
    }

    /** T17a: the recorded A2-28 measurement for Unit 16, measured as unit15DueReviewMeasurementIsRecorded. */
    @Test fun unit16DueReviewMeasurementIsRecorded() {
        val p = pack()
        isolatedStore("unit16").use { s ->
            val a = walk(p, s, 16)
            val t = 16
            val slots = a.getValue(t).slots
            val run = slots.maxOf { slot ->
                var k = 0; var u = t
                while (u >= 3 && a.getValue(u).slots.any { it.itemId == slot.itemId }) { k++; u-- }
                k
            }
            val excluded = cumulativeReviewRepeatExcluded(learnerBefore(s, a.getValue(t)), t).size
            val measured = listOf(slots.size, slots.map { it.objectiveId }.distinct().size, slots.map { it.skill }.distinct().size,
                slots.count { it.ownerUnit < t }, run, excluded)
            println("A228-UNIT16 items/objectives/skills/earlier/run/excluded=$measured " + slots.joinToString { "${it.objectiveId}/${it.itemId}" })
            assertEquals(listOf(8, 8, 8, 8, 2, 0), measured)
            val window = (13..16).map { u -> a.getValue(u).slots.map { it.itemId }.toSet() }
            assertTrue(window.reduce { x, y -> x intersect y }.isEmpty())
        }
    }

    /** T17b: the recorded A2-28 measurement for Unit 17, measured as unit16DueReviewMeasurementIsRecorded. */
    @Test fun unit17DueReviewMeasurementIsRecorded() {
        val p = pack()
        isolatedStore("unit17").use { s ->
            val a = walk(p, s, 17)
            val t = 17
            val slots = a.getValue(t).slots
            val run = slots.maxOf { slot ->
                var k = 0; var u = t
                while (u >= 3 && a.getValue(u).slots.any { it.itemId == slot.itemId }) { k++; u-- }
                k
            }
            val excluded = cumulativeReviewRepeatExcluded(learnerBefore(s, a.getValue(t)), t).size
            val measured = listOf(slots.size, slots.map { it.objectiveId }.distinct().size, slots.map { it.skill }.distinct().size,
                slots.count { it.ownerUnit < t }, run, excluded)
            println("A228-UNIT17 items/objectives/skills/earlier/run/excluded=$measured " + slots.joinToString { "${it.objectiveId}/${it.itemId}" })
            // T17b: the measured record (the longest consecutive-unit run is 3, below the four-unit window checked next).
            assertEquals(listOf(8, 8, 8, 8, 3, 0), measured)
            val window = (14..17).map { u -> a.getValue(u).slots.map { it.itemId }.toSet() }
            assertTrue(window.reduce { x, y -> x intersect y }.isEmpty())
        }
    }

    /** T17c: the recorded A2-28 measurement for Unit 18, measured as unit17DueReviewMeasurementIsRecorded. */
    @Test fun unit18DueReviewMeasurementIsRecorded() {
        val p = pack()
        isolatedStore("unit18").use { s ->
            val a = walk(p, s, 18)
            val t = 18
            val slots = a.getValue(t).slots
            val run = slots.maxOf { slot ->
                var k = 0; var u = t
                while (u >= 3 && a.getValue(u).slots.any { it.itemId == slot.itemId }) { k++; u-- }
                k
            }
            val excluded = cumulativeReviewRepeatExcluded(learnerBefore(s, a.getValue(t)), t).size
            val measured = listOf(slots.size, slots.map { it.objectiveId }.distinct().size, slots.map { it.skill }.distinct().size,
                slots.count { it.ownerUnit < t }, run, excluded)
            println("A228-UNIT18 items/objectives/skills/earlier/run/excluded=$measured " + slots.joinToString { "${it.objectiveId}/${it.itemId}" })
            // T17c: the measured record (one earlier item is excluded by the repeat limit; the longest consecutive-unit run is 2).
            assertEquals(listOf(8, 8, 8, 8, 2, 1), measured)
            val window = (15..18).map { u -> a.getValue(u).slots.map { it.itemId }.toSet() }
            assertTrue(window.reduce { x, y -> x intersect y }.isEmpty())
        }
    }

    /** T17e: the recorded A2-28 measurement for Unit 20, measured as unit19DueReviewMeasurementIsRecorded. */
    @Test fun unit20DueReviewMeasurementIsRecorded() {
        val p = pack()
        isolatedStore("unit20").use { s ->
            val a = walk(p, s, 20)
            val t = 20
            val slots = a.getValue(t).slots
            val run = slots.maxOf { slot ->
                var k = 0; var u = t
                while (u >= 3 && a.getValue(u).slots.any { it.itemId == slot.itemId }) { k++; u-- }
                k
            }
            val excluded = cumulativeReviewRepeatExcluded(learnerBefore(s, a.getValue(t)), t).size
            val measured = listOf(slots.size, slots.map { it.objectiveId }.distinct().size, slots.map { it.skill }.distinct().size,
                slots.count { it.ownerUnit < t }, run, excluded)
            println("A228-UNIT20 items/objectives/skills/earlier/run/excluded=$measured " + slots.joinToString { "${it.objectiveId}/${it.itemId}" })
            // T17e: the measured record (the longest consecutive-unit run is 2; no item is excluded by the repeat limit).
            assertEquals(listOf(8, 8, 8, 8, 2, 0), measured)
            val window = (17..20).map { u -> a.getValue(u).slots.map { it.itemId }.toSet() }
            assertTrue(window.reduce { x, y -> x intersect y }.isEmpty())
        }
    }

    /** T17d: the recorded A2-28 measurement for Unit 19, measured as unit18DueReviewMeasurementIsRecorded. */
    @Test fun unit19DueReviewMeasurementIsRecorded() {
        val p = pack()
        isolatedStore("unit19").use { s ->
            val a = walk(p, s, 19)
            val t = 19
            val slots = a.getValue(t).slots
            val run = slots.maxOf { slot ->
                var k = 0; var u = t
                while (u >= 3 && a.getValue(u).slots.any { it.itemId == slot.itemId }) { k++; u-- }
                k
            }
            val excluded = cumulativeReviewRepeatExcluded(learnerBefore(s, a.getValue(t)), t).size
            val measured = listOf(slots.size, slots.map { it.objectiveId }.distinct().size, slots.map { it.skill }.distinct().size,
                slots.count { it.ownerUnit < t }, run, excluded)
            println("A228-UNIT19 items/objectives/skills/earlier/run/excluded=$measured " + slots.joinToString { "${it.objectiveId}/${it.itemId}" })
            // T17d: the measured record (the longest consecutive-unit run is 2; no item is excluded by the repeat limit).
            assertEquals(listOf(8, 8, 8, 8, 2, 0), measured)
            val window = (16..19).map { u -> a.getValue(u).slots.map { it.itemId }.toSet() }
            assertTrue(window.reduce { x, y -> x intersect y }.isEmpty())
        }
    }

    @Test fun insufficientPoolIsRefusedAndNoShortTaskStandsIn() {
        val p = pack()
        isolatedStore("short").use { s ->
            for (n in 1..2) { ordinary(p, s, n); s.v2CompleteUnit(n, "done") }
            ordinary(p, s, 3)
            val learner = s.v2LearnerView()
            val full = createCumulativeReviewAssignment(p, learner, 3, "probe", 0)!!
            assertEquals(11, cumulativeReviewPolicy(3)!!.pool.sumOf { it.candidateItemIds.size })
            // Three preceding reviews that each used the iotated and four confusable items bar them (repeat limit).
            // Units 1–2 then leave 3 open items: fewer than 8, so the task is refused rather than shortened.
            val barred = (p.unit(1)!!.objectives.single { it.id == "obj-u1-iotated" }.checkItemIds +
                listOf("u1-i-р-not-p", "u1-i-н-not-h", "u1-i-с-not-c", "u1-i-в-not-b"))
            fun prior(unit: Int) = full.copy(id = "prior-$unit", targetUnit = unit,
                slots = barred.map { id -> full.slots.first().copy(itemId = id) })
            val crowded = learner.copy(reviewAssignments = listOf(prior(0), prior(1), prior(2)))
            assertEquals(barred.toSet(), cumulativeReviewRepeatExcluded(crowded, 3))
            assertNull(createCumulativeReviewAssignment(p, crowded, 3, "probe", 0))
            // Two of three is not a consecutive run of three, so the same history with a gap allocates normally.
            val gapped = learner.copy(reviewAssignments = listOf(prior(0), prior(2)))
            assertTrue(cumulativeReviewRepeatExcluded(gapped, 3).isEmpty())
            assertNotNull(createCumulativeReviewAssignment(p, gapped, 3, "probe", 0))
            // A two-slot task built from the anchors alone is not a current A2-28 task.
            val short = full.copy(slots = full.slots.take(2), digest = "").let { it.copy(digest = reviewAssignmentDigest(it)) }
            assertFalse(reviewAssignmentCurrent(p, short))
            assertFalse(cumulativeReviewSlotsMeetA228(full.slots.take(7), 2, emptySet()))
            assertFalse(cumulativeReviewSlotsMeetA228(full.slots.filter { it.objectiveId == full.slots[1].objectiveId }, 1, emptySet()))
            assertFalse(cumulativeReviewSlotsMeetA228(full.slots, 2, setOf(full.slots.last().itemId)))
            assertTrue(cumulativeReviewSlotsMeetA228(full.slots, 2, emptySet()))
        }
    }

    @Test fun assignmentBoundCompletionNeedsEverySlotAndSurvivesCurrentBackupRestore() {
        val p = pack()
        isolatedStore("bound").use { s ->
            walk(p, s, 4)
            ordinary(p, s, 5)
            val a = s.openCumulativeReview(p, 5, s.learnerMutationEpoch())!!
            assertEquals(a, s.openCumulativeReview(p, 5, s.learnerMutationEpoch()))
            assertTrue(a.slots.size >= 8)
            a.slots.indices.toList().dropLast(1).forEach { answer(p, s, a, it) }
            assertFalse(cumulativeReviewComplete(p, s.v2LearnerView(), 5))
            assertFalse(advancementDecision(p, p.unit(5)!!, s.v2LearnerView()).allowed)
            answer(p, s, a, a.slots.lastIndex, correct = false)
            assertFalse(cumulativeReviewComplete(p, s.v2LearnerView(), 5))
            answer(p, s, a, a.slots.lastIndex)
            assertTrue(cumulativeReviewComplete(p, s.v2LearnerView(), 5))
            assertTrue(advancementDecision(p, p.unit(5)!!, s.v2LearnerView()).allowed)
            assertTrue(runBlocking { advanceFrom(p, s, 5) })
            val row = s.governedLearnerDocument().unitCompletions.single { it.unitNumber == 5 }
            assertEquals(a.id, row.reviewAssignmentId); assertTrue(row.reviewCompletionOrder > a.boundary)
            val bytes = ByteArrayOutputStream()
            writeGovernedBackup(bytes, "dev.russiancompanion", p.packId, p.packVersion, "FOLLOW_SYSTEM", s.governedLearnerDocument(), emptyList())
            val temp = File(context().cacheDir, "a228-${System.nanoTime()}")
            try {
                val read = readGovernedBackup(bytes.toByteArray().inputStream(), "dev.russiancompanion", p.packId, p.packVersion, temp) as BackupReadResult.Valid
                isolatedStore("restore").use { target ->
                    assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner(read.backup.document, emptyList()))
                    assertEquals(s.cumulativeReviewHistory(), target.cumulativeReviewHistory())
                    assertTrue(5 in currentCompletedUnitNumbers(p, target.v2LearnerView()))
                    assertEquals(a, target.cumulativeReviewHistory().first.single { it.targetUnit == 5 })
                    // A restored history keeps steering the repeat limit for the next unit's task.
                    ordinary(p, target, 6)
                    val next = target.openCumulativeReview(p, 6, target.learnerMutationEpoch())!!
                    assertTrue(next.slots.none { it.itemId in cumulativeReviewRepeatExcluded(target.v2LearnerView(), 6) })
                    assertTrue(next.slots.size >= 8)
                }
            } finally { temp.deleteRecursively() }
        }
    }

    /** The learner as allocation saw it: history before this task. */
    private fun learnerBefore(s: LocalStore, a: CumulativeReviewAssignment): V2LearnerView {
        val v = s.v2LearnerView()
        return v.copy(reviewAssignments = v.reviewAssignments.filter { it.targetUnit < a.targetUnit },
            unitReviewCompletions = v.unitReviewCompletions.filterKeys { it < a.targetUnit })
    }

    private fun walk(p: V2CurriculumPack, s: LocalStore, through: Int): Map<Int, CumulativeReviewAssignment> {
        for (n in 1..2) { ordinary(p, s, n); s.v2CompleteUnit(n, "done") }
        val out = linkedMapOf<Int, CumulativeReviewAssignment>()
        for (n in 3..through) {
            ordinary(p, s, n)
            val a = s.openCumulativeReview(p, n, s.learnerMutationEpoch())
            assertNotNull("unit $n review refused", a)
            a!!.slots.indices.forEach { answer(p, s, a, it) }
            assertTrue("unit $n advance", runBlocking { advanceFrom(p, s, n) })
            out[n] = a
        }
        return out
    }
    private fun ordinary(p: V2CurriculumPack, s: LocalStore, n: Int) {
        s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = n, currentSessionId = null))
        for (step in p.unit(n)!!.sessions.flatMap { it.steps }) {
            for (id in step.itemIds) s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, p.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "same-clock")!!)
            s.v2CompleteGovernedStep(step.id, governedStepCompletionDigest(p, s.v2LearnerView(), step.id)!!, "same-clock")
        }
        s.v2AttestBook(n, "same-clock")
    }
    private fun answer(p: V2CurriculumPack, s: LocalStore, a: CumulativeReviewAssignment, index: Int, correct: Boolean = true) {
        val i = p.item(a.slots[index].itemId)!!
        val value = if (!correct) i.options.first { it != i.answer } else if (i.isTypedResponse()) i.typedAnswerKey!!.acceptedAnswers.first() else i.answer
        assertTrue(s.answerCumulativeReview(p, a.id, index, value, s.learnerMutationEpoch()))
    }
    private suspend fun advanceFrom(p: V2CurriculumPack, s: LocalStore, n: Int): Boolean {
        val next = (n + 1).coerceAtMost(30)
        val session = p.unit(next)!!.sessions.first().id
        return s.v2AdvanceUnitIfCurrentAsync(s.learnerMutationEpoch(), n, "advance", next, session, resumePositionDigest(p, session, 0)!!, GOVERNED_IDENTITY_SCHEMA, p)
    }
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    private fun pack() = sharedPack
    companion object { private val sharedPack by lazy {
        val root = File(if (File("app").isDirectory) "." else "..")
        V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) }
    } }
}
