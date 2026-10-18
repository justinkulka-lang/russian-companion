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
/** Current Unit13 transition against genuine pre-transition production histories. */
class Unit13WiderPolicyTest {
    @get:Rule val compose = createComposeRule()
    private val names = mutableListOf<String>()
    @After fun cleanup() {
        names.forEach { context().deleteDatabase(it); File(context().filesDir, "recordings-$it").deleteRecursively() }
    }

    @Test fun threeFreshSlotsUseNonFirstGenderCandidateAndRequireAllAnswersBeforeActualAdvance() {
        restored(document("ORDINARY_UNIT13")).use { s ->
            record(s, feminine, false)
            val a = open(s)
            assertEquals(6, a.schema)
            assertEquals(Unit13WiderCumulativeReviewPolicy.definition.id, a.policyId)
            assertEquals(listOf("obj-u1-decoding", "obj-u12-aspect", "obj-u11-past-aspect"), a.slots.map { it.objectiveId })
            assertEquals(listOf(1, 12, 11), a.slots.map { it.ownerUnit })
            assertEquals(listOf("u12-i-aspect-1", feminine), a.slots.drop(1).map { it.itemId })
            assertEquals(3, a.slots.map { it.skill }.distinct().size)
            record(s, feminine, true)
            assertEquals(a, open(s))
            assertTrue(completedReviewSlots(p, a, s.v2LearnerView().reviewAnswers).isEmpty())
            answer(s, a, 0); answer(s, a, 1)
            assertFalse(advancementDecision(p, p.unit(13)!!, s.v2LearnerView()).allowed)
            assertTrue(runCatching { advance(s) }.isFailure)
            assertFalse(s.governedLearnerDocument().unitCompletions.any { it.unitNumber == 13 })
            answer(s, a, 2)
            assertTrue(advancementDecision(p, p.unit(13)!!, s.v2LearnerView()).allowed)
            s.failAdvanceAfterCompletionForTests = true
            assertTrue(runCatching { advance(s) }.isFailure)
            assertFalse(s.governedLearnerDocument().unitCompletions.any { it.unitNumber == 13 })
            assertEquals(13, s.v2Profile().currentUnitNumber)
            assertTrue(advance(s))
            val row = s.governedLearnerDocument().unitCompletions.single { it.unitNumber == 13 }
            assertEquals(a.id, row.reviewAssignmentId); assertEquals(a.policyDigest, row.reviewPolicyDigest)
            assertTrue(row.reviewCompletionOrder > a.boundary)
            answer(s, a, 2, false)
            assertFalse(cumulativeReviewComplete(p, s.v2LearnerView(), 13))
            assertEquals((1..13).toSet(), currentCompletedUnitNumbers(p, s.v2LearnerView()))
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
                    assertEquals((1..13).toSet(), currentCompletedUnitNumbers(p, target.v2LearnerView()))
                    assertFalse(cumulativeReviewComplete(p, target.v2LearnerView(), 13))
                    val learner = target.v2LearnerView()
                    val wrongPolicy = learner.copy(unitReviewCompletions = learner.unitReviewCompletions +
                        (13 to learner.unitReviewCompletions.getValue(13).copy(policyDigest = Unit13CumulativeReviewPolicy.definition.digest)))
                    assertFalse(13 in currentCompletedUnitNumbers(p, wrongPolicy))
                }
            } finally { staging.deleteRecursively() }
        }
    }

    @Test fun genuineOldPartialTasksUseOwnEligibilityAndActualLegacyPolicyOnAdvance() {
        for (prefix in listOf("")) {
            val original = document(prefix + "PARTIAL_REVIEW")
            // Negative eligibility probe removes a completion; never fabricates old review credit.
            val placement = original.copy(unitCompletions = original.unitCompletions.filter { it.unitNumber != 11 },
                profile = original.profile.copy(startingPoint = "RESUME_FROM_BOOK"))
            restored(placement).use { s ->
                val old = original.reviewAssignments.single { it.targetUnit == 13 }
                assertNull(createCumulativeReviewAssignment(p, s.v2LearnerView(), 13, "new-ineligible", 0))
                assertTrue(reviewAssignmentEligible(p, s.v2LearnerView(), old))
                assertEquals(old, open(s))
                answer(s, old, 1)
                assertTrue(advance(s))
                val row = s.governedLearnerDocument().unitCompletions.single { it.unitNumber == 13 }
                assertEquals(old.id, row.reviewAssignmentId)
                assertEquals(Unit13CumulativeReviewPolicy.definition.digest, row.reviewPolicyDigest)
                assertEquals(old, s.cumulativeReviewHistory().first.single { it.targetUnit == 13 })
            }
        }
    }

    @Test fun genuinePriorPackDeclaresV1AndCannotAcceptFutureV2Tasks() {
        val process = ProcessBuilder("git", "show",
            "8e2c6f1a8d3d4b6d1f098bc6e347d36e92e10352:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
        val old = V2PackLoader.parse(process.inputStream.bufferedReader().readText())
        assertEquals(0, process.waitFor()); V2PackIdentityIndex.warm(old)
        assertEquals(Unit13CumulativeReviewPolicy.definition, cumulativeReviewDeclaredPolicy(old, 13))
        restored(document("ORDINARY_UNIT13")).use { s ->
            val prior = s.openCumulativeReview(old, 13, s.learnerMutationEpoch())!!
            assertEquals(1, prior.schema)
            assertEquals(Unit13CumulativeReviewPolicy.definition.id, prior.policyId)
            assertEquals(2, prior.slots.size)
            assertTrue(reviewAssignmentCurrent(p, prior))
            assertEquals(prior, open(s))
            val future = createCumulativeReviewAssignment(p, s.v2LearnerView(), 13, "future-v2", prior.boundary)!!
            assertEquals(6, future.schema); assertEquals(3, future.slots.size)
            assertFalse(reviewAssignmentCurrent(old, future))
            assertFalse(reviewAssignmentEligible(old, s.v2LearnerView(), future))
        }
    }

    @Test fun currentAndDownstreamOldSnapshotsRejectChangedContentAndAcceptOnlyExactPriorDeclaration() {
        for (prefix in listOf("")) restored(document(prefix + "COMPLETED_UNIT14")).use { s ->
            val learner = s.v2LearnerView()
            assertEquals((1..14).toSet(), currentCompletedUnitNumbers(p, learner))
            val downstream = learner.reviewAssignments.single { it.targetUnit == 14 }
            assertTrue(reviewAssignmentCurrent(p, downstream))
            val changedItem = p.copy(items = p.items.map {
                if (it.id == downstream.slots[1].itemId) it.copy(feedback = it.feedback + " changed") else it
            })
            val changedTeaching = p.copy(units = p.units.map { u -> if (u.number != 13) u else u.copy(
                sessions = u.sessions.map { se -> se.copy(steps = se.steps.map { step ->
                    if (step.id == "u13-st-teach-past-aspect-1") step.copy(body = step.body + " changed") else step
                }) }) })
            val changedRule = p.copy(units = p.units.map { u -> if (u.number == 13) u.copy(
                advancement = u.advancement!!.copy(minimumDistinctChecks = u.advancement.minimumDistinctChecks + 1)) else u })
            val changedSource = p.copy(sources = p.sources.map { if (it.id == "src-openrussian-50e210c4") it.copy(attribution = it.attribution + " changed") else it })
            val changedKey = p.copy(items = p.items.map { if (it.id == downstream.slots[1].itemId) it.copy(answer = it.options.first { option -> option != it.answer }) else it })
            for (changed in listOf(changedItem, changedTeaching, changedRule, changedSource, changedKey)) {
                V2PackIdentityIndex.warm(changed)
                assertFalse(reviewAssignmentCurrent(changed, downstream))
                assertFalse(13 in currentCompletedUnitNumbers(changed, learner))
                assertFalse(14 in currentCompletedUnitNumbers(changed, learner))
            }
            val unknown = p.copy(units = p.units.map { u -> if (u.number == 13) u.copy(
                advancement = u.advancement!!.copy(cumulativeReviewPolicyId = "invented-v1")) else u })
            V2PackIdentityIndex.warm(unknown)
            assertFalse(13 in currentCompletedUnitNumbers(unknown, learner))
            val unrelated = p.copy(units = p.units.map { u -> if (u.number == 30) u.copy(title = u.title + " changed") else u })
            V2PackIdentityIndex.warm(unrelated)
            assertEquals((1..14).toSet(), currentCompletedUnitNumbers(unrelated, learner))
        }
    }

    @Test fun versionsRolesLinksEpochAndPacklessWritesFailClosedWithoutChangingHistory() {
        restored(document("ORDINARY_UNIT13")).use { s ->
            val a = open(s)
            for (bad in listOf(a.copy(schema = 1), a.copy(schema = 2), a.copy(schema = 3), a.copy(schema = 4), a.copy(schema = 5),
                a.copy(targetUnit = 5), a.copy(policyId = Unit13CumulativeReviewPolicy.definition.id),
                a.copy(policyDigest = "0".repeat(64)))) {
                val coherent = bad.copy(digest = reviewAssignmentDigest(bad))
                assertFalse(reviewAssignmentCurrent(p, coherent))
            }
            val legacy = document("UNSTARTED_REVIEW").reviewAssignments.single { it.targetUnit == 13 }
            val relabelled = legacy.copy(schema = 6).let { it.copy(digest = reviewAssignmentDigest(it)) }
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
            val next = p.unit(14)!!.sessions.first().id
            assertTrue(runCatching { runBlocking { s.v2AdvanceUnitIfCurrentAsync(s.learnerMutationEpoch(), 13,
                "packless", 14, next, resumePositionDigest(p, next, 0)!!, GOVERNED_IDENTITY_SCHEMA) } }.isFailure)
            assertFalse(s.governedLearnerDocument().unitCompletions.any { it.unitNumber == 13 })
        }
    }

    @Test fun newPastGenderUiRepairsFullTeachingAndRequiresAllThreeFreshAssignedAnswers() {
        restored(document("ORDINARY_UNIT13")).use { s ->
            record(s, feminine, false)
            compose.setContent { V2App(s, p, {}) }
            waitTag("v2-continue"); compose.onNodeWithTag("v2-continue").performClick()
            waitTag("v2-review-start"); compose.onNodeWithTag("v2-review-start").performScrollTo().performClick()
            waitTag("v2-review-count")
            val a = s.cumulativeReviewHistory().first.single { it.targetUnit == 13 }
            assertEquals(6, a.schema); assertEquals(feminine, a.slots[2].itemId)
            compose.onNodeWithTag("v2-review-count").assertTextEquals("0 of 3 review checks recorded")
            val item = p.item(feminine)!!
            compose.onNodeWithText(item.promptLetter!!).performScrollTo().assertIsDisplayed()
            val wrong = item.options.first { it != item.answer }
            compose.onNodeWithTag("v2-review-option-2-$wrong").performScrollTo().performClick()
            waitTag("v2-review-feedback-2")
            compose.onNodeWithTag("v2-review-feedback-2").assertTextEquals("Not the reviewed answer. ${item.feedback}")
            compose.onNodeWithTag("v2-review-remediate-2").performScrollTo().performClick()
            val body = p.step("u11-st-rem-past-aspect")!!.second.body
            assertTrue(body.contains("прочита́ло") && body.contains("прочита́ла") && body.contains("The pronoun I does not select one past-tense gender"))
            compose.onNodeWithText("The pronoun I does not select one past-tense gender for every speaker.", substring = true).performScrollTo().assertIsDisplayed()
            waitTag("v2-review-practice"); compose.onNodeWithTag("v2-review-practice").performClick()
            val guides = reviewedGuidedPractice(p, "u11-st-rem-past-aspect")
            assertEquals(1, guides.size)
            guides.forEachIndexed { index, step ->
                p.step(step)!!.second.itemIds.forEach { id ->
                    val i = p.item(id)!!; waitTag("v2-option-$id-${i.answer}")
                    compose.onNodeWithTag("v2-option-$id-${i.answer}").performScrollTo().performClick()
                }
                val tag = if (index == guides.lastIndex) "v2-return-to-check" else "v2-next-practice"
                waitTag(tag); compose.onNodeWithTag(tag).performClick()
            }
            waitTag("v2-review-count"); assertEquals(a, open(s))
            assertTrue(completedReviewSlots(p, a, s.v2LearnerView().reviewAnswers).isEmpty())
            finishUi(s, a)
        }
    }

    @Test fun genuineLegacyUiKeepsTwoChecksAndBoundAspectRepairInsteadOfNewObligation() {
        restored(document("UNSTARTED_REVIEW")).use { s ->
            compose.setContent { V2App(s, p, {}) }
            waitTag("v2-continue"); compose.onNodeWithTag("v2-continue").performClick()
            waitTag("v2-review-count")
            val a = s.cumulativeReviewHistory().first.single { it.targetUnit == 13 }
            assertEquals(1, a.schema); assertEquals(2, a.slots.size)
            compose.onNodeWithTag("v2-review-count").assertTextEquals("0 of 2 review checks recorded")
            val item = p.item(a.slots[1].itemId)!!
            val wrong = item.options.first { it != item.answer }
            compose.onNodeWithTag("v2-review-option-1-$wrong").performScrollTo().performClick()
            waitTag("v2-review-feedback-1"); compose.onNodeWithTag("v2-review-remediate-1").performScrollTo().performClick()
            waitTag("v2-review-practice")
            val repair = cumulativeReviewAssignmentPolicy(a)!!.pool[1].remediationStepId!!
            assertEquals("u12-st-rem-aspect", repair)
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
        for (label in listOf("BEFORE_UNIT13_WORK", "ORDINARY_UNIT13", "UNSTARTED_REVIEW", "PARTIAL_REVIEW",
            "ANSWERED_REVIEW", "COMPLETED_UNIT13", "POST_COMPLETION_WRONG", "COMPLETED_UNIT14")) {
            val staging = File(context().cacheDir, "unit13-history-${System.nanoTime()}")
            val name = newName()
            try {
                val read = readGovernedBackup(File(root(), "audit/independent-gpt61-2026-10-12/ia003-unit13-history-transition/$label.zip").inputStream(),
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
                    val last = when (label) { "COMPLETED_UNIT14" -> 14; "COMPLETED_UNIT13", "POST_COMPLETION_WRONG" -> 13; else -> 12 }
                    assertEquals(label, (1..last).toSet(), currentCompletedUnitNumbers(p, learner))
                    val old = original.reviewAssignments.singleOrNull { it.targetUnit == 13 }
                    assertEquals(old, currentCumulativeAssignment(p, learner, 13))
                    old?.let { assertEquals(it, open(store)); assertEquals(1, it.schema); assertEquals(2, it.slots.size) }
                }
            } finally { staging.deleteRecursively() }
        }
    }

    @Test fun twoPastGenderCandidatesRequireCurrentTaughtEvidenceAndExactRepairWithoutChangingAssetTuples() {
        val oldProcess = ProcessBuilder("git", "show", "8e2c6f1a8d3d4b6d1f098bc6e347d36e92e10352:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
        val prior = V2PackLoader.parse(oldProcess.inputStream.bufferedReader().readText()); assertEquals(0, oldProcess.waitFor())
        assertEquals(prior.copy(units = prior.units.map { u -> if (u.number == 13) u.copy(advancement =
            u.advancement!!.copy(cumulativeReviewPolicyId = Unit13WiderCumulativeReviewPolicy.definition.id)) else u }), p)
        val pool = Unit13WiderCumulativeReviewPolicy.definition.pool[2]
        assertEquals(p.unit(11)!!.objectives.single { it.id == "obj-u11-past-aspect" }.checkItemIds, pool.candidateItemIds)
        assertEquals("u11-st-rem-past-aspect", pool.remediationStepId)
        restored(document("ORDINARY_UNIT13")).use { store ->
            for (id in pool.candidateItemIds) {
                record(store, id, false)
                val a = createCumulativeReviewAssignment(p, store.v2LearnerView(), 13, "probe-$id", 0)!!
                assertEquals(id, a.slots[2].itemId)
                record(store, id, true)
            }
            val learner = store.v2LearnerView()
            val withoutTeaching = learner.copy(completedStepIds = learner.completedStepIds - "u11-st-teach-past-aspect-3",
                governedStepDigests = learner.governedStepDigests!! - "u11-st-teach-past-aspect-3")
            assertNull(createCumulativeReviewAssignment(p, withoutTeaching, 13, "untaught", 0))
            val changed = p.copy(units = p.units.map { u -> if (u.number == 11) u.copy(sessions = u.sessions.map { se -> se.copy(steps = se.steps.map { st ->
                if (st.id == "u11-st-teach-past-aspect-3") st.copy(body = st.body + " changed") else st }) }) else u })
            assertNull(createCumulativeReviewAssignment(changed, learner, 13, "stale-owner", 0))
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
        compose.waitUntil(30000) { s.governedLearnerDocument().unitCompletions.any { it.unitNumber == 13 && it.reviewAssignmentId == a.id } }
        val row = s.governedLearnerDocument().unitCompletions.single { it.unitNumber == 13 }
        assertEquals(a.policyDigest, row.reviewPolicyDigest)
        assertEquals(14, s.v2Profile().currentUnitNumber)
    }

    private fun open(s: LocalStore) = s.openCumulativeReview(p, 13, s.learnerMutationEpoch())!!
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
        val next = p.unit(14)!!.sessions.first().id
        s.v2AdvanceUnitIfCurrentAsync(s.learnerMutationEpoch(), 13, "advance", 14, next, resumePositionDigest(p, next, 0)!!, GOVERNED_IDENTITY_SCHEMA, p)
    }
    private fun document(label: String) = backupJson.decodeFromString(GovernedLearnerDocument.serializer(),
        File(root(), "audit/independent-gpt61-2026-10-12/ia003-unit13-history-transition/$label.json").readText())
    private fun restored(d: GovernedLearnerDocument) = LocalStore.openNamed(context(), newName()).also {
        assertEquals(GovernedReplaceOutcome.REPLACED, it.replaceGovernedLearner(d, emptyList()))
    }
    private fun newName() = "unit13-wider-${System.nanoTime()}.db".also { names += it }
    private fun waitTag(tag: String) = compose.waitUntil(conditionDescription = tag, timeoutMillis = 30000) {
        compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
    }
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    private fun root() = File(if (File("app").isDirectory) "." else "..")
    companion object {
        private const val feminine = "u11-i-past-aspect-2"
        internal fun beforePolicyTransitionPack(): V2CurriculumPack {
            val process = ProcessBuilder("git", "show", "8e2c6f1a8d3d4b6d1f098bc6e347d36e92e10352:app/src/main/assets/content/v2-curriculum.json").directory(rootStatic()).start()
            return V2PackLoader.parse(process.inputStream.bufferedReader().readText()).also { assertEquals(0, process.waitFor()); V2PackIdentityIndex.warm(it) }
        }
        private val p by lazy { Unit7AgreementTeachingTest.beforeTeachingCorrectionPack() } // Exact post-Unit13-policy source2ff71f19; this historical transition predates the later Unit7 teaching correction.
        private fun rootStatic() = File(if (File("app").isDirectory) "." else "..")
    }
}
