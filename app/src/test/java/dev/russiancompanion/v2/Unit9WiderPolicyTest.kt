package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.V2App
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
/** Current Unit9 transition against genuine pre-transition production histories. */
class Unit9WiderPolicyTest {
    @get:Rule val compose = createComposeRule()
    private val names = mutableListOf<String>()
    @After fun cleanup() {
        names.forEach { context().deleteDatabase(it); File(context().filesDir, "recordings-$it").deleteRecursively() }
    }

    @Test fun threeFreshSlotsUseNonFirstGenderCandidateAndRequireAllAnswersBeforeActualAdvance() {
        restored(document("ORDINARY_UNIT9")).use { s ->
            record(s, door, false)
            val a = open(s)
            assertEquals(5, a.schema)
            assertEquals(Unit9WiderCumulativeReviewPolicy.definition.id, a.policyId)
            assertEquals(listOf("obj-u1-decoding", "obj-u8-plural", "obj-u3-gender"), a.slots.map { it.objectiveId })
            assertEquals(listOf(1, 8, 3), a.slots.map { it.ownerUnit })
            assertEquals(listOf("u8-i-plural-2", door), a.slots.drop(1).map { it.itemId })
            assertEquals(3, a.slots.map { it.skill }.distinct().size)
            record(s, door, true)
            assertEquals(a, open(s))
            assertTrue(completedReviewSlots(p, a, s.v2LearnerView().reviewAnswers).isEmpty())
            answer(s, a, 0); answer(s, a, 1)
            assertFalse(advancementDecision(p, p.unit(9)!!, s.v2LearnerView()).allowed)
            assertTrue(runCatching { advance(s) }.isFailure)
            assertFalse(s.governedLearnerDocument().unitCompletions.any { it.unitNumber == 9 })
            answer(s, a, 2)
            assertTrue(advancementDecision(p, p.unit(9)!!, s.v2LearnerView()).allowed)
            s.failAdvanceAfterCompletionForTests = true
            assertTrue(runCatching { advance(s) }.isFailure)
            assertFalse(s.governedLearnerDocument().unitCompletions.any { it.unitNumber == 9 })
            assertEquals(9, s.v2Profile().currentUnitNumber)
            assertTrue(advance(s))
            val row = s.governedLearnerDocument().unitCompletions.single { it.unitNumber == 9 }
            assertEquals(a.id, row.reviewAssignmentId); assertEquals(a.policyDigest, row.reviewPolicyDigest)
            assertTrue(row.reviewCompletionOrder > a.boundary)
            answer(s, a, 2, false)
            assertFalse(cumulativeReviewComplete(p, s.v2LearnerView(), 9))
            assertEquals((1..9).toSet(), currentCompletedUnitNumbers(p, s.v2LearnerView()))
            val bytes = ByteArrayOutputStream()
            writeGovernedBackup(bytes, "dev.russiancompanion", p.packId, p.packVersion, "FOLLOW_SYSTEM", s.governedLearnerDocument(), emptyList())
            val staging = File(context().cacheDir, "wider-backup-${System.nanoTime()}")
            val name = newName()
            try {
                val read = readGovernedBackup(bytes.toByteArray().inputStream(), "dev.russiancompanion", p.packId, p.packVersion, staging)
                assertTrue(read is BackupReadResult.Valid)
                LocalStore.openNamed(context(), name).use { target ->
                    assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner((read as BackupReadResult.Valid).backup.document, emptyList()))
                    assertEquals(s.cumulativeReviewHistory(), target.cumulativeReviewHistory())
                }
                LocalStore.openNamed(context(), name).use { target ->
                    assertEquals(a, open(target))
                    assertEquals((1..9).toSet(), currentCompletedUnitNumbers(p, target.v2LearnerView()))
                    assertFalse(cumulativeReviewComplete(p, target.v2LearnerView(), 9))
                    val learner = target.v2LearnerView()
                    val wrongPolicy = learner.copy(unitReviewCompletions = learner.unitReviewCompletions +
                        (9 to learner.unitReviewCompletions.getValue(9).copy(policyDigest = Unit9CumulativeReviewPolicy.definition.digest)))
                    assertFalse(9 in currentCompletedUnitNumbers(p, wrongPolicy))
                }
            } finally { staging.deleteRecursively() }
        }
    }

    @Test fun genuineOldPartialTasksUseOwnEligibilityAndActualLegacyPolicyOnAdvance() {
        for (prefix in listOf("")) {
            val original = document(prefix + "PARTIAL_REVIEW")
            // Negative eligibility probe removes a completion; never fabricates old review credit.
            val placement = original.copy(unitCompletions = original.unitCompletions.filter { it.unitNumber != 3 },
                profile = original.profile.copy(startingPoint = "RESUME_FROM_BOOK"))
            restored(placement).use { s ->
                val old = original.reviewAssignments.single { it.targetUnit == 9 }
                assertNull(createCumulativeReviewAssignment(p, s.v2LearnerView(), 9, "new-ineligible", 0))
                assertTrue(reviewAssignmentEligible(p, s.v2LearnerView(), old))
                assertEquals(old, open(s))
                answer(s, old, 1)
                assertTrue(advance(s))
                val row = s.governedLearnerDocument().unitCompletions.single { it.unitNumber == 9 }
                assertEquals(old.id, row.reviewAssignmentId)
                assertEquals(Unit9CumulativeReviewPolicy.definition.digest, row.reviewPolicyDigest)
                assertEquals(old, s.cumulativeReviewHistory().first.single { it.targetUnit == 9 })
            }
        }
    }

    @Test fun genuinePriorPackDeclaresV1AndCannotAcceptFutureV2Tasks() {
        val process = ProcessBuilder("git", "show",
            "61c68cfcf20cd92a4b6c310c4abf8ff2284e36e5:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
        val old = V2PackLoader.parse(process.inputStream.bufferedReader().readText())
        assertEquals(0, process.waitFor()); V2PackIdentityIndex.warm(old)
        assertEquals(Unit9CumulativeReviewPolicy.definition, cumulativeReviewDeclaredPolicy(old, 9))
        restored(document("ORDINARY_UNIT9")).use { s ->
            val prior = s.openCumulativeReview(old, 9, s.learnerMutationEpoch())!!
            assertEquals(1, prior.schema)
            assertEquals(Unit9CumulativeReviewPolicy.definition.id, prior.policyId)
            assertEquals(2, prior.slots.size)
            assertTrue(reviewAssignmentCurrent(p, prior))
            assertEquals(prior, open(s))
            val future = createCumulativeReviewAssignment(p, s.v2LearnerView(), 9, "future-v2", prior.boundary)!!
            assertEquals(5, future.schema); assertEquals(3, future.slots.size)
            assertFalse(reviewAssignmentCurrent(old, future))
            assertFalse(reviewAssignmentEligible(old, s.v2LearnerView(), future))
        }
    }

    @Test fun currentAndDownstreamOldSnapshotsRejectChangedContentAndAcceptOnlyExactPriorDeclaration() {
        for (prefix in listOf("")) restored(document(prefix + "COMPLETED_UNIT10")).use { s ->
            val learner = s.v2LearnerView()
            assertEquals((1..10).toSet(), currentCompletedUnitNumbers(p, learner))
            val downstream = learner.reviewAssignments.single { it.targetUnit == 10 }
            assertTrue(reviewAssignmentCurrent(p, downstream))
            val changedItem = p.copy(items = p.items.map {
                if (it.id == downstream.slots[1].itemId) it.copy(feedback = it.feedback + " changed") else it
            })
            val changedTeaching = p.copy(units = p.units.map { u -> if (u.number != 9) u else u.copy(
                sessions = u.sessions.map { se -> se.copy(steps = se.steps.map { step ->
                    if (step.id == "u9-st-teach-odindva-1") step.copy(body = step.body + " changed") else step
                }) }) })
            val changedRule = p.copy(units = p.units.map { u -> if (u.number == 9) u.copy(
                advancement = u.advancement!!.copy(minimumDistinctChecks = u.advancement.minimumDistinctChecks + 1)) else u })
            for (changed in listOf(changedItem, changedTeaching, changedRule)) {
                V2PackIdentityIndex.warm(changed)
                assertFalse(reviewAssignmentCurrent(changed, downstream))
                assertFalse(9 in currentCompletedUnitNumbers(changed, learner))
                assertFalse(10 in currentCompletedUnitNumbers(changed, learner))
            }
            val unknown = p.copy(units = p.units.map { u -> if (u.number == 9) u.copy(
                advancement = u.advancement!!.copy(cumulativeReviewPolicyId = "invented-v1")) else u })
            assertFalse(9 in currentCompletedUnitNumbers(unknown, learner))
            val unrelated = p.copy(units = p.units.map { u -> if (u.number == 30) u.copy(title = u.title + " changed") else u })
            assertEquals((1..10).toSet(), currentCompletedUnitNumbers(unrelated, learner))
        }
    }

    @Test fun versionsRolesLinksEpochAndPacklessWritesFailClosedWithoutChangingHistory() {
        restored(document("ORDINARY_UNIT9")).use { s ->
            val a = open(s)
            for (bad in listOf(a.copy(schema = 1), a.copy(schema = 3), a.copy(schema = 4),
                a.copy(targetUnit = 5), a.copy(policyId = Unit9CumulativeReviewPolicy.definition.id),
                a.copy(policyDigest = "0".repeat(64)))) {
                val coherent = bad.copy(digest = reviewAssignmentDigest(bad))
                assertFalse(reviewAssignmentCurrent(p, coherent))
            }
            val legacy = document("UNSTARTED_REVIEW").reviewAssignments.single { it.targetUnit == 9 }
            val relabelled = legacy.copy(schema = 5).let { it.copy(digest = reviewAssignmentDigest(it)) }
            assertFalse(reviewAssignmentCurrent(p, relabelled))
            assertNotNull(reviewHistoryDefects(listOf(relabelled), emptyList()))
            val epoch = s.learnerMutationEpoch(); val before = s.cumulativeReviewHistory()
            s.openLearnerReplacement(); s.closeLearnerReplacement()
            assertFalse(s.answerCumulativeReview(p, a.id, 0, p.item(a.slots[0].itemId)!!.answer, epoch))
            assertEquals(before, s.cumulativeReviewHistory())
            answer(s, a, 0)
            val accepted = s.v2LearnerView().reviewAnswers.last { it.assignmentId == a.id }
            for (bad in listOf(accepted.copy(role = PRESCRIBED_CHECK), accepted.copy(schema = 4),
                accepted.copy(acceptedOrder = a.boundary), accepted.copy(assignmentDigest = "0".repeat(64)))) {
                assertTrue(completedReviewSlots(p, a, listOf(bad)).isEmpty())
            }
            val next = p.unit(10)!!.sessions.first().id
            assertTrue(runCatching { runBlocking { s.v2AdvanceUnitIfCurrentAsync(s.learnerMutationEpoch(), 9,
                "packless", 10, next, resumePositionDigest(p, next, 0)!!, GOVERNED_IDENTITY_SCHEMA) } }.isFailure)
            assertFalse(s.governedLearnerDocument().unitCompletions.any { it.unitNumber == 9 })
        }
    }

    @Test fun newGenderUiRepairsFullTeachingAndRequiresAllThreeFreshAssignedAnswers() {
        restored(document("ORDINARY_UNIT9")).use { s ->
            record(s, door, false)
            compose.setContent { V2App(s, p, {}) }
            waitTag("v2-continue"); compose.onNodeWithTag("v2-continue").performClick()
            waitTag("v2-review-start"); compose.onNodeWithTag("v2-review-start").performScrollTo().performClick()
            waitTag("v2-review-count")
            val a = s.cumulativeReviewHistory().first.single { it.targetUnit == 9 }
            assertEquals(5, a.schema); assertEquals(door, a.slots[2].itemId)
            compose.onNodeWithTag("v2-review-count").assertTextEquals("0 of 3 review checks recorded")
            val item = p.item(door)!!
            compose.onNodeWithText(item.promptLetter!!).performScrollTo().assertIsDisplayed()
            val wrong = item.options.first { it != item.answer }
            compose.onNodeWithTag("v2-review-option-2-$wrong").performScrollTo().performClick()
            waitTag("v2-review-feedback-2")
            compose.onNodeWithTag("v2-review-feedback-2").assertTextEquals("Not the reviewed answer. ${item.feedback}")
            compose.onNodeWithTag("v2-review-remediate-2").performScrollTo().performClick()
            val body = p.step("u3-st-rem-gender")!!.second.body
            assertTrue(body.contains("дверь — door is feminine") && body.contains("дя́дя") && body.contains("мо́ре — sea"))
            waitTag("v2-review-practice"); compose.onNodeWithTag("v2-review-practice").performClick()
            reviewedGuidedPractice(p, "u3-st-rem-gender").forEach { step -> p.step(step)!!.second.itemIds.forEach { id ->
                val i = p.item(id)!!; waitTag("v2-option-$id-${i.answer}")
                compose.onNodeWithTag("v2-option-$id-${i.answer}").performScrollTo().performClick()
            } }
            waitTag("v2-return-to-check"); compose.onNodeWithTag("v2-return-to-check").performClick()
            waitTag("v2-review-count"); assertEquals(a, open(s))
            assertTrue(completedReviewSlots(p, a, s.v2LearnerView().reviewAnswers).isEmpty())
            finishUi(s, a)
        }
    }

    @Test fun genuineLegacyUiKeepsTwoChecksAndBoundPluralRepairInsteadOfNewObligation() {
        restored(document("UNSTARTED_REVIEW")).use { s ->
            compose.setContent { V2App(s, p, {}) }
            waitTag("v2-continue"); compose.onNodeWithTag("v2-continue").performClick()
            waitTag("v2-review-count")
            val a = s.cumulativeReviewHistory().first.single { it.targetUnit == 9 }
            assertEquals(1, a.schema); assertEquals(2, a.slots.size)
            compose.onNodeWithTag("v2-review-count").assertTextEquals("0 of 2 review checks recorded")
            val item = p.item(a.slots[1].itemId)!!
            val wrong = item.options.first { it != item.answer }
            compose.onNodeWithTag("v2-review-option-1-$wrong").performScrollTo().performClick()
            waitTag("v2-review-feedback-1"); compose.onNodeWithTag("v2-review-remediate-1").performScrollTo().performClick()
            waitTag("v2-review-practice")
            val repair = cumulativeReviewAssignmentPolicy(a)!!.pool[1].remediationStepId!!
            assertEquals("u8-st-rem-plural", repair)
            compose.onNodeWithTag("v2-review-practice").performClick()
            reviewedGuidedPractice(p, repair).forEach { step -> p.step(step)!!.second.itemIds.forEach { id ->
                val i = p.item(id)!!; waitTag("v2-option-$id-${i.answer}")
                compose.onNodeWithTag("v2-option-$id-${i.answer}").performScrollTo().performClick()
            } }
            waitTag("v2-return-to-check"); compose.onNodeWithTag("v2-return-to-check").performClick()
            waitTag("v2-review-count"); assertEquals(a, open(s))
            finishUi(s, a)
        }
    }

    @Test fun allGenuineLifecycleBackupsRestoreReopenAndRetainExactRowsAndCutoff() {
        for (label in listOf("BEFORE_UNIT9_WORK", "ORDINARY_UNIT9", "UNSTARTED_REVIEW", "PARTIAL_REVIEW",
            "ANSWERED_REVIEW", "COMPLETED_UNIT9", "POST_COMPLETION_WRONG", "COMPLETED_UNIT10")) {
            val staging = File(context().cacheDir, "unit9-history-${System.nanoTime()}")
            val name = newName()
            try {
                val read = readGovernedBackup(File(root(), "audit/independent-gpt61-2026-10-12/ia003-unit9-history-transition/$label.zip").inputStream(),
                    "dev.russiancompanion", p.packId, p.packVersion, staging)
                assertTrue(read is BackupReadResult.Valid)
                val original = document(label)
                assertEquals(original, (read as BackupReadResult.Valid).backup.document)
                LocalStore.openNamed(context(), name).use { store ->
                    assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(read.backup.document, emptyList()))
                }
                LocalStore.openNamed(context(), name).use { store ->
                    val learner = store.v2LearnerView()
                    assertEquals(original.reviewAssignments to original.reviewAnswers, store.cumulativeReviewHistory())
                    assertEquals(original.unitCompletions, store.governedLearnerDocument().unitCompletions)
                    val last = when (label) { "COMPLETED_UNIT10" -> 10; "COMPLETED_UNIT9", "POST_COMPLETION_WRONG" -> 9; else -> 8 }
                    assertEquals(label, (1..last).toSet(), currentCompletedUnitNumbers(p, learner))
                    val old = original.reviewAssignments.singleOrNull { it.targetUnit == 9 }
                    assertEquals(old, currentCumulativeAssignment(p, learner, 9))
                    old?.let { assertEquals(it, open(store)); assertEquals(1, it.schema); assertEquals(2, it.slots.size) }
                }
            } finally { staging.deleteRecursively() }
        }
    }

    @Test fun fiveGenderCandidatesRequireCurrentTaughtEvidenceAndExactRepairWithoutChangingAssetTuples() {
        val oldProcess = ProcessBuilder("git", "show", "61c68cfcf20cd92a4b6c310c4abf8ff2284e36e5:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
        val prior = V2PackLoader.parse(oldProcess.inputStream.bufferedReader().readText()); assertEquals(0, oldProcess.waitFor())
        assertEquals(prior.copy(units = prior.units.map { u -> if (u.number == 9) u.copy(advancement =
            u.advancement!!.copy(cumulativeReviewPolicyId = Unit9WiderCumulativeReviewPolicy.definition.id)) else u }), p)
        val pool = Unit9WiderCumulativeReviewPolicy.definition.pool[2]
        assertEquals(p.unit(3)!!.objectives.single { it.id == "obj-u3-gender" }.checkItemIds, pool.candidateItemIds)
        assertEquals("u3-st-rem-gender", pool.remediationStepId)
        restored(document("ORDINARY_UNIT9")).use { store ->
            for (id in pool.candidateItemIds) {
                record(store, id, false)
                val a = createCumulativeReviewAssignment(p, store.v2LearnerView(), 9, "probe-$id", 0)!!
                assertEquals(id, a.slots[2].itemId)
                record(store, id, true)
            }
            val learner = store.v2LearnerView()
            val withoutTeaching = learner.copy(completedStepIds = learner.completedStepIds - "u3-st-teach-gender-3",
                governedStepDigests = learner.governedStepDigests!! - "u3-st-teach-gender-3")
            assertNull(createCumulativeReviewAssignment(p, withoutTeaching, 9, "untaught", 0))
            val changed = p.copy(units = p.units.map { u -> if (u.number == 3) u.copy(sessions = u.sessions.map { se -> se.copy(steps = se.steps.map { st ->
                if (st.id == "u3-st-teach-gender-3") st.copy(body = st.body + " changed") else st }) }) else u })
            assertNull(createCumulativeReviewAssignment(changed, learner, 9, "stale-owner", 0))
        }
    }

    private fun finishUi(s: LocalStore, a: CumulativeReviewAssignment) {
        a.slots.forEachIndexed { index, slot ->
            val correct = p.item(slot.itemId)!!.answer
            compose.onNodeWithTag("v2-review-option-$index-$correct").performScrollTo().performClick()
            compose.waitUntil(30000) { s.v2LearnerView().reviewAnswers.lastOrNull { it.assignmentId == a.id && it.slotIndex == index }?.selected == correct }
        }
        waitTag("v2-review-gate"); compose.onNodeWithTag("v2-review-gate").performScrollTo().performClick()
        waitTag("v2-advance"); compose.onNodeWithTag("v2-advance").performScrollTo().assertIsEnabled().performClick()
        compose.waitUntil(30000) { s.governedLearnerDocument().unitCompletions.any { it.unitNumber == 9 && it.reviewAssignmentId == a.id } }
        val row = s.governedLearnerDocument().unitCompletions.single { it.unitNumber == 9 }
        assertEquals(a.policyDigest, row.reviewPolicyDigest)
        assertEquals(10, s.v2Profile().currentUnitNumber)
    }

    private fun open(s: LocalStore) = s.openCumulativeReview(p, 9, s.learnerMutationEpoch())!!
    private fun answer(s: LocalStore, a: CumulativeReviewAssignment, index: Int, correct: Boolean = true) {
        val i = p.item(a.slots[index].itemId)!!
        assertTrue(s.answerCumulativeReview(p, a.id, index, if (correct) i.answer else i.options.first { it != i.answer }, s.learnerMutationEpoch()))
    }
    private fun record(s: LocalStore, id: String, correct: Boolean) {
        val i = p.item(id)!!
        val step = p.units.flatMap { it.sessions }.flatMap { it.steps }.single { it.kind == "CHECK" && id in it.itemIds }
        s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, if (correct) i.answer else i.options.first { it != i.answer }, PRESCRIBED_CHECK, step.id, "same-clock")!!)
    }
    private fun advance(s: LocalStore) = runBlocking {
        val next = p.unit(10)!!.sessions.first().id
        s.v2AdvanceUnitIfCurrentAsync(s.learnerMutationEpoch(), 9, "advance", 10, next, resumePositionDigest(p, next, 0)!!, GOVERNED_IDENTITY_SCHEMA, p)
    }
    private fun document(label: String) = backupJson.decodeFromString(GovernedLearnerDocument.serializer(),
        File(root(), "audit/independent-gpt61-2026-10-12/ia003-unit9-history-transition/$label.json").readText())
    private fun restored(d: GovernedLearnerDocument) = LocalStore.openNamed(context(), newName()).also {
        assertEquals(GovernedReplaceOutcome.REPLACED, it.replaceGovernedLearner(d, emptyList()))
    }
    private fun newName() = "unit9-wider-${System.nanoTime()}.db".also { names += it }
    private fun waitTag(tag: String) = compose.waitUntil(conditionDescription = tag, timeoutMillis = 30000) {
        compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
    }
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    private fun root() = File(if (File("app").isDirectory) "." else "..")
    companion object {
        private const val door = "u3-i-gender-дверь"
        private val p by lazy { V2PackLoader.parse(File(rootStatic(), "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) } }
        private fun rootStatic() = File(if (File("app").isDirectory) "." else "..")
    }
}
