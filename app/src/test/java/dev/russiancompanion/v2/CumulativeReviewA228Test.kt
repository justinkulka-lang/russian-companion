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
