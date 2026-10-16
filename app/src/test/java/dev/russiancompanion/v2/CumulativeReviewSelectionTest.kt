package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream
import java.io.File
import org.json.JSONObject

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class CumulativeReviewSelectionTest {
    @Test fun newestCurrentMissWinsAndLaterCorrectAnswerResolvesIt() = withBaseline { s ->
        val learner = s.v2LearnerView()
        val rows = listOf(attempt(r, 1, true), attempt(n, 2, false), attempt(c, 3, false))
        assertEquals(c, choose(learner, rows))
        assertEquals(n, choose(learner, rows + attempt(c, 4, true)))
        assertEquals(r, choose(learner, rows + attempt(c, 4, true) + attempt(n, 5, true)))
        assertEquals(c, choose(learner, rows.reversed()))
        // Clock text and attempt IDs never break accepted-order ranking.
        assertEquals(c, choose(learner, rows.map { it.copy(id = "renamed-${it.acceptedOrder}", recordedAtLocal = "clock-rollback") }))
    }

    @Test fun leastRecentAndUnknownHistoryUseAuthoredCandidateOrderRatherThanIdentifiers() = withBaseline { s ->
        val learner = s.v2LearnerView()
        assertEquals(r, choose(learner, emptyList()))
        assertEquals(n, choose(learner, listOf(attempt(r, 10, true))))
        assertEquals(c, choose(learner, listOf(attempt(r, 30, true), attempt(n, 20, true), attempt(c, 10, true))))
        val reversed = candidates.reversed()
        assertEquals(c, recentReviewCandidate(p, learner.copy(governedAttempts = emptyList()), reversed)!!.id)
        // Ambiguous insertion order is ignored, never resolved using serialized row order.
        val duplicate = listOf(attempt(c, 7, false), attempt(n, 7, true))
        assertEquals(r, choose(learner, duplicate))
        assertEquals(r, choose(learner, duplicate.reversed()))
    }

    @Test fun optionalGuidedStaleUnorderedForgedAndWrongContextRowsDoNotRankCandidates() = withBaseline { s ->
        val learner = s.v2LearnerView()
        val miss = attempt(c, 100, false)
        val ignored = listOf(
            miss.copy(attemptRole = OPTIONAL_PRACTICE, stepId = practiceStepId("mixed")),
            miss.copy(stepId = "u1-st-guided-confusable"),
            miss.copy(itemDigest = "0".repeat(64)), miss.copy(unitContentDigest = "0".repeat(64)),
            miss.copy(ruleDigest = "0".repeat(64)), miss.copy(identitySchema = 0),
            miss.copy(acceptedOrder = 0), miss.copy(correct = true),
            miss.copy(objectiveId = "untaught"), miss.copy(unitNumber = 30),
            miss.copy(selected = "not an option"), miss.copy(stepId = "u1-st-decoding-check"),
            miss.copy(attemptRole = LEGACY_UNVERIFIED),
        )
        for (bad in ignored) assertEquals(bad.toString(), r, choose(learner, listOf(bad)))
        assertEquals(r, recentReviewCandidate(p, learner.copy(governedAttempts = null), candidates)!!.id)
    }

    @Test fun genuineSchemaOneHistoryKeepsItsExactIdentityAndAcceptedCompletionSnapshot() = withBaseline { s ->
        val d = baseline()
        val old = d.reviewAssignments.single()
        assertEquals(1, old.schema)
        assertEquals(r, old.slots[1].itemId)
        assertEquals(old.digest, reviewAssignmentDigest(old))
        assertTrue(reviewAssignmentCurrent(p, old))
        // Default-field omission in genuine old serialization must still decode as schema1.
        val raw = JSONObject(backupJson.encodeToString(CumulativeReviewAssignment.serializer(), old))
        raw.remove("schema")
        assertEquals(old, backupJson.decodeFromString(CumulativeReviewAssignment.serializer(), raw.toString()))
        assertEquals(d.reviewAssignments to d.reviewAnswers, s.cumulativeReviewHistory())
        assertEquals(old, s.openCumulativeReview(p, 3, s.learnerMutationEpoch()))
        assertEquals(setOf(1, 2, 3), currentCompletedUnitNumbers(p, s.v2LearnerView()))
        val item = p.item(old.slots[1].itemId)!!
        assertTrue(s.answerCumulativeReview(p, old.id, 1, item.options.first { it != item.answer }, s.learnerMutationEpoch()))
        assertFalse(cumulativeReviewComplete(p, s.v2LearnerView(), 3))
        assertTrue(3 in currentCompletedUnitNumbers(p, s.v2LearnerView()))
        val fresh = createCumulativeReviewAssignment(p, s.v2LearnerView(), 3, old.id, old.boundary)!!
        assertEquals(2, fresh.schema)
        assertEquals(old.policyDigest, fresh.policyDigest)
        assertNotEquals(old.digest, fresh.digest)
    }

    @Test fun newAssignmentIsRecentAwareImmutableAndRequiresFreshAnswersAcrossBackupAndEpoch() {
        val d = baseline().let { it.copy(profile = it.profile.copy(currentUnitNumber = 3),
            reviewAssignments = emptyList(), reviewAnswers = emptyList(),
            unitCompletions = it.unitCompletions.filter { row -> row.unitNumber != 3 }) }
        LocalStore.openInMemory(context()).use { s ->
            assertEquals(GovernedReplaceOutcome.REPLACED, s.replaceGovernedLearner(d, emptyList()))
            val item = p.item(n)!!
            val step = "u1-st-confusable-n"
            s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, n, item.options.first { it != item.answer }, PRESCRIBED_CHECK, step, "same-clock")!!)
            val a = s.openCumulativeReview(p, 3, s.learnerMutationEpoch())!!
            assertEquals(2, a.schema); assertEquals(n, a.slots[1].itemId)
            assertFalse(advancementDecision(p, p.unit(3)!!, s.v2LearnerView()).allowed)
            s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, n, item.answer, PRESCRIBED_CHECK, step, "same-clock")!!)
            s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, n, item.answer, OPTIONAL_PRACTICE, practiceStepId("mixed"), "same-clock")!!)
            assertEquals(a, s.openCumulativeReview(p, 3, s.learnerMutationEpoch()))
            assertTrue(completedReviewSlots(p, a, s.cumulativeReviewHistory().second).isEmpty())
            a.slots.forEachIndexed { index, slot ->
                assertTrue(s.answerCumulativeReview(p, a.id, index, p.item(slot.itemId)!!.answer, s.learnerMutationEpoch()))
            }
            assertTrue(advancementDecision(p, p.unit(3)!!, s.v2LearnerView()).allowed)
            val bytes = ByteArrayOutputStream()
            writeGovernedBackup(bytes, "dev.russiancompanion", p.packId, p.packVersion, "DARK", s.governedLearnerDocument(), emptyList())
            val temp = File(context().cacheDir, "selection-backup-${System.nanoTime()}")
            val name = "selection-restart-${System.nanoTime()}.db"
            try {
                val read = readGovernedBackup(bytes.toByteArray().inputStream(), "dev.russiancompanion", p.packId, p.packVersion, temp)
                assertTrue(read is BackupReadResult.Valid)
                LocalStore.openNamed(context(), name).use { target ->
                    val epoch = target.learnerMutationEpoch()
                    target.openLearnerReplacement()
                    try { assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner((read as BackupReadResult.Valid).backup.document, emptyList())) }
                    finally { target.closeLearnerReplacement() }
                    assertFalse(target.answerCumulativeReview(p, a.id, 0, p.item(a.slots[0].itemId)!!.answer, epoch))
                    assertEquals(s.cumulativeReviewHistory(), target.cumulativeReviewHistory())
                }
                LocalStore.openNamed(context(), name).use { target ->
                    assertEquals(a, target.openCumulativeReview(p, 3, target.learnerMutationEpoch()))
                    assertTrue(advancementDecision(p, p.unit(3)!!, target.v2LearnerView()).allowed)
                }
            } finally { temp.deleteRecursively(); context().deleteDatabase(name) }
        }
    }

    @Test fun unknownAssignmentVersionsFailClosedAndUntaughtOwnersStillCannotBeSelected() = withBaseline { s ->
        val a = s.cumulativeReviewHistory().first.single()
        val before = s.governedLearnerFingerprint()
        for (bad in listOf(a.copy(schema = 0), a.copy(schema = 3), a.copy(schema = 2, targetUnit = 4))) {
            val coherentHash = bad.copy(digest = reviewAssignmentDigest(bad))
            assertFalse(reviewAssignmentCurrent(p, coherentHash))
            assertNotNull(reviewHistoryDefects(listOf(coherentHash), emptyList()))
            assertEquals(GovernedReplaceOutcome.UNCHANGED, s.replaceGovernedLearner(baseline().copy(reviewAssignments = listOf(coherentHash)), emptyList()))
            assertEquals(before, s.governedLearnerFingerprint())
        }
        val learner = s.v2LearnerView()
        assertNull(createCumulativeReviewAssignment(p, learner.copy(completedUnitNumbers = emptySet()), 3, "untaught", 0))
        assertNull(createCumulativeReviewAssignment(p, learner.copy(governedStepDigests = emptyMap()), 3, "unteached", 0))
        assertNull(createCumulativeReviewAssignment(p, learner.copy(governedAttempts = emptyList()), 3, "undemonstrated", 0))
    }

    private fun choose(learner: V2LearnerView, rows: List<GovernedAttemptView>) =
        recentReviewCandidate(p, learner.copy(governedAttempts = rows), candidates)!!.id
    private fun attempt(id: String, order: Long, correct: Boolean): GovernedAttemptView {
        val item = p.item(id)!!
        val step = when (id) { r -> "u1-st-confusable-r"; n -> "u1-st-confusable-n"; else -> "u1-st-confusable-s" }
        val w = prepareGovernedAttempt(p, id, if (correct) item.answer else item.options.first { it != item.answer }, PRESCRIBED_CHECK, step, "same-clock")!!
        return GovernedAttemptView(w.id, w.itemId, w.selected, w.correct, w.recordedAtLocal, w.itemDigest,
            w.unitContentDigest, w.ruleDigest, w.identitySchema, w.attemptRole, w.objectiveId, w.unitNumber, w.stepId, order)
    }
    private fun withBaseline(block: (LocalStore) -> Unit) {
        LocalStore.openInMemory(context()).use { s ->
            assertEquals(GovernedReplaceOutcome.REPLACED, s.replaceGovernedLearner(baseline(), emptyList()))
            block(s)
        }
    }
    private fun baseline(): GovernedLearnerDocument = backupJson.decodeFromString(GovernedLearnerDocument.serializer(),
        File(root(), "audit/independent-gpt61-2026-10-12/ia003-recent-selection/BASELINE_SCHEMA1_HISTORY.json").readText())
    private val candidates get() = listOf(r, n, c).map { p.item(it)!! }
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    private fun root() = File(if (File("app").isDirectory) "." else "..")
    companion object {
        private const val r = "u1-i-р-not-p"
        private const val n = "u1-i-н-not-h"
        private const val c = "u1-i-с-not-c"
        private val p by lazy { V2PackLoader.parse(File(if (File("app").isDirectory) "." else "..",
            "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) } }
    }
}
