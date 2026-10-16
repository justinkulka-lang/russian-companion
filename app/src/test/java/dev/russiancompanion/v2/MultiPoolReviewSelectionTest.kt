package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.V2App
import dev.russiancompanion.v2.ui.V2UiWork
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
/** Historical policy-transition regressions on the exact pre-teaching-change source.
 * Current changed-teaching rejection is covered separately by Unit3GenderTeachingTest. */
class MultiPoolReviewSelectionTest {
    @get:Rule val compose = createComposeRule()
    private val names = mutableListOf<String>()
    @After fun cleanup() {
        V2UiWork.blockForTests = null
        names.forEach { context().deleteDatabase(it); File(context().filesDir, "recordings-$it").deleteRecursively() }
    }

    @Test fun genuineSchemaOneAndTwoHistoriesKeepExactAssignmentsAndAcceptedCompletionSnapshots() {
        val d = document("BASELINE_COMPLETED_UNIT6.json")
        restored(d).use { s ->
            assertEquals((1..6).toSet(), currentCompletedUnitNumbers(p, s.v2LearnerView()))
            assertEquals(d.reviewAssignments to d.reviewAnswers, s.cumulativeReviewHistory())
            val u3 = d.reviewAssignments.single { it.targetUnit == 3 }
            assertEquals(2, u3.schema)
            assertEquals(u3, createCumulativeReviewAssignment(p, s.v2LearnerView(), 3, u3.id, u3.boundary))
            for (n in 5..6) {
                val old = d.reviewAssignments.single { it.targetUnit == n }
                assertEquals(1, old.schema); assertEquals(old.digest, reviewAssignmentDigest(old))
                assertTrue(reviewAssignmentCurrent(p, old))
                assertEquals(old, s.openCumulativeReview(p, n, s.learnerMutationEpoch()))
                val fresh = createCumulativeReviewAssignment(p, s.v2LearnerView(), n, old.id, old.boundary)!!
                assertEquals(if (n == 6) 4 else 3, fresh.schema)
                if (n == 6) { assertNotEquals(old.policyDigest, fresh.policyDigest); assertEquals(3, fresh.slots.size) }
                else assertEquals(old.policyDigest, fresh.policyDigest)
                assertNotEquals(old.digest, fresh.digest)
                answer(s, old, 1, false)
                assertFalse(cumulativeReviewComplete(p, s.v2LearnerView(), n))
                assertEquals((1..6).toSet(), currentCompletedUnitNumbers(p, s.v2LearnerView()))
            }
        }
    }

    @Test fun unit5ChoosesRecentStudentMissAndLaterCorrectResolvesItWithoutUsingClockOrOptionalRows() {
        ready(5).use { s ->
            record(s, student, false)
            assertEquals(student, preview(s, 5).slots[1].itemId)
            val wrong = s.v2LearnerView().governedAttempts!!.last()
            val rows = s.v2LearnerView().governedAttempts!!
            val shuffled = s.v2LearnerView().copy(governedAttempts = rows.reversed().map { it.copy(recordedAtLocal = "clock-rollback") })
            assertEquals(student, createCumulativeReviewAssignment(p, shuffled, 5, "shuffled", 0)!!.slots[1].itemId)
            record(s, student, true)
            assertEquals(train, preview(s, 5).slots[1].itemId)
            s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, student, p.item(student)!!.options.last(), OPTIONAL_PRACTICE, practiceStepId("mixed"), "same-clock")!!)
            assertEquals(train, preview(s, 5).slots[1].itemId)
            for (bad in listOf(wrong.copy(itemDigest = "0".repeat(64)), wrong.copy(acceptedOrder = 0), wrong.copy(correct = true),
                wrong.copy(stepId = "u4-st-guided-nominative"), wrong.copy(objectiveId = "untaught"))) {
                val learner = s.v2LearnerView().copy(governedAttempts = s.v2LearnerView().governedAttempts!! + bad.copy(id = "ignored-${bad.hashCode()}", acceptedOrder = if (bad.acceptedOrder == 0L) 0 else Long.MAX_VALUE))
                assertEquals(train, createCumulativeReviewAssignment(p, learner, 5, "ignored", 0)!!.slots[1].itemId)
            }
        }
    }

    @Test fun unit6CanSelectEveryAuthoredCandidateAndResolveMissesIncludingOppositeKeyContrasts() {
        ready(6).use { s ->
            val ids = cumulativeReviewPolicy(6)!!.pool[1].candidateItemIds
            for (id in ids) {
                record(s, id, false)
                val a = preview(s, 6)
                assertEquals(if (a.targetUnit == 6) 4 else 3, a.schema); assertEquals(id, a.slots[1].itemId)
                assertEquals(listOf(1, 5, 4), a.slots.map { it.ownerUnit })
                assertTrue(completedReviewSlots(p, a, s.cumulativeReviewHistory().second).isEmpty())
            }
            record(s, window, true)
            assertEquals(book, preview(s, 6).slots[1].itemId)
            ids.forEach { record(s, it, true) }
            // Window was resolved first. Its identical correct tap is idempotent,
            // so it remains least-recent until a changed selection is accepted.
            assertEquals(window, preview(s, 6).slots[1].itemId)
            val beforeDuplicate = s.v2Attempts()
            record(s, window, true)
            assertEquals(beforeDuplicate, s.v2Attempts())
            assertEquals(window, preview(s, 6).slots[1].itemId)
            record(s, window, false); record(s, window, true)
            assertEquals(ids.first(), preview(s, 6).slots[1].itemId)
            record(s, ids.first(), false); record(s, ids.first(), true)
            assertEquals(ids[1], preview(s, 6).slots[1].itemId)
            val radio = p.item("u5-i-indecl-радио")!!
            assertNotEquals(radio.answer, p.item(window)!!.answer)
            assertNotEquals(radio.answer, p.item(book)!!.answer)
        }
    }

    @Test fun newAssignmentsStayImmutableAndMixedVersionBackupRestartAndEpochPreserveFreshCredit() {
        for ((n, id) in listOf(5 to student, 6 to window)) ready(n).use { s ->
            record(s, id, false)
            val a = s.openCumulativeReview(p, n, s.learnerMutationEpoch())!!
            assertEquals(if (a.targetUnit == 6) 4 else 3, a.schema); assertEquals(id, a.slots[1].itemId)
            assertFalse(advancementDecision(p, p.unit(n)!!, s.v2LearnerView()).allowed)
            record(s, id, true)
            assertEquals(a, s.openCumulativeReview(p, n, s.learnerMutationEpoch()))
            assertTrue(completedReviewSlots(p, a, s.cumulativeReviewHistory().second).isEmpty())
            answer(s, a, 0); assertFalse(advancementDecision(p, p.unit(n)!!, s.v2LearnerView()).allowed)
            answer(s, a, 1)
            if (n == 6) { assertFalse(advancementDecision(p, p.unit(n)!!, s.v2LearnerView()).allowed); answer(s, a, 2) }
            assertTrue(advance(s, n))
            val row = s.governedLearnerDocument().unitCompletions.single { it.unitNumber == n }
            assertEquals(a.id, row.reviewAssignmentId); assertTrue(row.reviewCompletionOrder > a.boundary)
            val bytes = ByteArrayOutputStream()
            writeGovernedBackup(bytes, "dev.russiancompanion", p.packId, p.packVersion, "DARK", s.governedLearnerDocument(), emptyList())
            val temp = File(context().cacheDir, "multipool-backup-$n-${System.nanoTime()}")
            val name = newName("restart-$n")
            try {
                val read = readGovernedBackup(bytes.toByteArray().inputStream(), "dev.russiancompanion", p.packId, p.packVersion, temp)
                assertTrue(read is BackupReadResult.Valid)
                LocalStore.openNamed(context(), name).use { target ->
                    val epoch = target.learnerMutationEpoch()
                    target.openLearnerReplacement()
                    try { assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner((read as BackupReadResult.Valid).backup.document, emptyList())) }
                    finally { target.closeLearnerReplacement() }
                    assertFalse(target.answerCumulativeReview(p, a.id, 1, p.item(id)!!.answer, epoch))
                    assertEquals(s.cumulativeReviewHistory(), target.cumulativeReviewHistory())
                }
                LocalStore.openNamed(context(), name).use { target ->
                    assertEquals(a, target.openCumulativeReview(p, n, target.learnerMutationEpoch()))
                    assertEquals((1..n).toSet(), currentCompletedUnitNumbers(p, target.v2LearnerView()))
                    answer(target, a, 1, false)
                    assertFalse(cumulativeReviewComplete(p, target.v2LearnerView(), n))
                    assertEquals((1..n).toSet(), currentCompletedUnitNumbers(p, target.v2LearnerView()))
                }
            } finally { temp.deleteRecursively() }
        }
    }

    @Test fun versionScopesUntaughtOwnersAndChangedSourceCannotAcquireReviewCreditOrRewriteHistory() {
        for (n in 5..6) ready(n).use { s ->
            val a = preview(s, n); val learner = s.v2LearnerView(); val before = s.governedLearnerFingerprint()
            for (bad in listOf(a.copy(schema = 2), a.copy(schema = 5), a.copy(targetUnit = 3), a.copy(targetUnit = 4), a.copy(targetUnit = 7))) {
                val coherent = bad.copy(digest = reviewAssignmentDigest(bad))
                assertFalse(reviewAssignmentCurrent(p, coherent))
                assertNotNull(reviewHistoryDefects(listOf(coherent.copy(boundary = 0).let { it.copy(digest = reviewAssignmentDigest(it)) }), emptyList()))
            }
            val owner = n - 1
            assertNull(createCumulativeReviewAssignment(p, learner.copy(completedUnitNumbers = learner.completedUnitNumbers - owner), n, "untaught", 0))
            assertNull(createCumulativeReviewAssignment(p, learner.copy(governedStepDigests = emptyMap()), n, "missing-teaching", 0))
            val changed = p.copy(items = p.items.map { if (it.id == a.slots[1].itemId) it.copy(feedback = it.feedback + " changed") else it })
            V2PackIdentityIndex.warm(changed)
            assertFalse(reviewAssignmentCurrent(changed, a))
            assertNull(createCumulativeReviewAssignment(changed, learner, n, "stale-owner", 0))
            assertEquals(before, s.governedLearnerFingerprint())
        }
    }

    @Test fun nonFirstStudentUiRepairsFullRoleTeachingAndStillRequiresFreshAssignedAnswers() = nonFirstUi(5, student)
    @Test fun nonFirstWindowUiRepairsFullBehaviorTeachingAndStillRequiresOppositeKeyFreshAnswer() = nonFirstUi(6, window)

    private fun nonFirstUi(n: Int, id: String) {
        ready(n).use { s ->
            record(s, id, false)
            compose.setContent { V2App(s, p, {}) }
            waitTag("v2-continue"); compose.onNodeWithTag("v2-continue").performClick()
            waitTag("v2-review-start"); compose.onNodeWithTag("v2-review-start").performScrollTo().performClick()
            waitTag("v2-review-count")
            val a = s.cumulativeReviewHistory().first.single { it.targetUnit == n }
            assertEquals(if (a.targetUnit == 6) 4 else 3, a.schema); assertEquals(id, a.slots[1].itemId)
            val item = p.item(id)!!
            compose.onNodeWithText(item.promptLetter!!).performScrollTo().assertIsDisplayed()
            val wrong = item.options.first { it != item.answer }
            compose.onNodeWithTag("v2-review-option-1-$wrong").performScrollTo().performClick()
            waitTag("v2-review-feedback-1"); compose.onNodeWithTag("v2-review-feedback-1").assertTextEquals("Not the reviewed answer. ${item.feedback}")
            compose.onNodeWithTag("v2-review-remediate-1").performScrollTo().performClick()
            val remediation = cumulativeReviewPolicy(n)!!.pool[1].remediationStepId!!
            val body = p.step(remediation)!!.second.body
            if (n == 5) assertTrue(body.contains("Студент новый")) else assertTrue(body.contains("окно́"))
            waitTag("v2-review-practice"); compose.onNodeWithTag("v2-review-practice").performClick()
            val guided = reviewedGuidedPractice(p, remediation)
            assertEquals(1, guided.size)
            guided.forEach { step -> p.step(step)!!.second.itemIds.forEach { itemId ->
                val i = p.item(itemId)!!; waitTag("v2-option-$itemId-${i.answer}")
                compose.onNodeWithTag("v2-option-$itemId-${i.answer}").performScrollTo().performClick()
            } }
            waitTag("v2-return-to-check"); compose.onNodeWithTag("v2-return-to-check").performClick()
            waitTag("v2-review-count")
            assertEquals(a, s.cumulativeReviewHistory().first.single { it.targetUnit == n })
            assertTrue(completedReviewSlots(p, a, s.cumulativeReviewHistory().second).isEmpty())
            assertFalse(advancementDecision(p, p.unit(n)!!, s.v2LearnerView()).allowed)
            a.slots.forEachIndexed { index, slot ->
                val correct = p.item(slot.itemId)!!.answer
                compose.onNodeWithTag("v2-review-option-$index-$correct").performScrollTo().performClick()
                compose.waitUntil(30000) { s.cumulativeReviewHistory().second.lastOrNull { it.assignmentId == a.id && it.slotIndex == index }?.selected == correct }
            }
            waitTag("v2-review-gate"); compose.onNodeWithTag("v2-review-gate").performScrollTo().performClick()
            waitTag("v2-advance"); compose.onNodeWithTag("v2-advance").performScrollTo().assertIsEnabled().performClick()
            compose.waitUntil(30000) { s.governedLearnerDocument().unitCompletions.any { it.unitNumber == n && it.reviewAssignmentId == a.id } }
            assertEquals(n + 1, s.v2Profile().currentUnitNumber)
        }
    }
    private fun preview(s: LocalStore, n: Int) = createCumulativeReviewAssignment(p, s.v2LearnerView(), n, "preview-$n", 0)!!
    private fun record(s: LocalStore, id: String, correct: Boolean) {
        val item = p.item(id)!!
        val step = p.units.flatMap { it.sessions }.flatMap { it.steps }.first { it.kind == "CHECK" && id in it.itemIds }
        s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, if (correct) item.answer else item.options.first { it != item.answer }, PRESCRIBED_CHECK, step.id, "same-clock")!!)
    }
    private fun answer(s: LocalStore, a: CumulativeReviewAssignment, slot: Int, correct: Boolean = true) {
        val item = p.item(a.slots[slot].itemId)!!
        assertTrue(s.answerCumulativeReview(p, a.id, slot, if (correct) item.answer else item.options.first { it != item.answer }, s.learnerMutationEpoch()))
    }
    private fun advance(s: LocalStore, n: Int) = runBlocking {
        val session = p.unit(n + 1)!!.sessions.first().id
        s.v2AdvanceUnitIfCurrentAsync(s.learnerMutationEpoch(), n, "advance", n + 1, session, resumePositionDigest(p, session, 0)!!, GOVERNED_IDENTITY_SCHEMA, p)
    }
    private fun document(name: String) = backupJson.decodeFromString(GovernedLearnerDocument.serializer(), File(root(), "$evidence/$name").readText())
    private fun ready(n: Int) = restored(document("BASELINE_BEFORE_UNIT$n.json"))
    private fun restored(d: GovernedLearnerDocument) = LocalStore.openNamed(context(), newName("case")).also {
        assertEquals(GovernedReplaceOutcome.REPLACED, it.replaceGovernedLearner(d, emptyList()))
    }
    private fun newName(label: String) = "multipool-$label-${System.nanoTime()}.db".also { names += it }
    private fun waitTag(tag: String) = compose.waitUntil(conditionDescription = tag, timeoutMillis = 30000) { compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty() }
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    private fun root() = File(if (File("app").isDirectory) "." else "..")
    companion object {
        private const val evidence = "audit/independent-gpt61-2026-10-12/ia003-units5-6-recent-selection"
        private const val train = "u4-i-nom-role-train"
        private const val student = "u4-i-nom-role-student"
        private const val book = "u5-i-decl-книга"
        private const val window = "u5-i-decl-окно"
        private val p by lazy { Unit3GenderTeachingTest.preTeachingCorrectionPack() }
    }
}
