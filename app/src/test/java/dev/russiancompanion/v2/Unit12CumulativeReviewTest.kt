package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.ui.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit12CumulativeReviewTest {
    @get:Rule val compose = createComposeRule()
    @After fun reset() { V2UiWork.blockForTests = null }

    @Test fun optionalReplaysStalePrecheckAndInjectedAdvanceFailureCannotCreateCompletion() {
        val p = pack()
        ready(p).use { s ->
            val a = open(p, s)
            a.slots.forEach { slot ->
                val i = p.item(slot.itemId)!!
                s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, i.id, i.answer, OPTIONAL_PRACTICE, practiceStepId("mixed"), "replay")!!)
            }
            assertFalse(cumulativeReviewComplete(p, s.v2LearnerView(), 12))
            a.slots.indices.forEach { answer(p, s, a, it) }
            assertTrue(advancementDecision(p, p.unit(12)!!, s.v2LearnerView()).allowed)
            answer(p, s, a, 1, correct = false)
            assertTrue(runCatching { advance(p, s) }.isFailure)
            assertFalse(s.governedLearnerDocument().unitCompletions.any { it.unitNumber == 12 })
            (1..a.slots.lastIndex).forEach { answer(p, s, a, it) }
            s.failAdvanceAfterCompletionForTests = true
            assertTrue(runCatching { advance(p, s) }.isFailure)
            assertEquals(12, s.v2Profile().currentUnitNumber)
            assertFalse(s.governedLearnerDocument().unitCompletions.any { it.unitNumber == 12 })
            val wrong = p.copy(units = p.units.map { if (it.number == 12) it.copy(advancement = it.advancement!!.copy(requireDueReview = false)) else it })
            assertFalse(reviewAssignmentCurrent(wrong, a)); assertFalse(advancementDecision(wrong, wrong.unit(12)!!, s.v2LearnerView()).allowed)
            assertFalse(V2Validator.validate(wrong).ok)
            s.failAdvanceAfterCompletionForTests = false
            val next = p.unit(13)!!.sessions.first().id
            assertTrue(runCatching { runBlocking {
                s.v2AdvanceUnitIfCurrentAsync(s.learnerMutationEpoch(), 12, "packless", 13, next,
                    resumePositionDigest(p, next, 0)!!, GOVERNED_IDENTITY_SCHEMA)
            } }.isFailure)
            assertFalse(s.governedLearnerDocument().unitCompletions.any { it.unitNumber == 12 })
        }
    }

    @Test fun actualUnit12ContinuePastGenderCueTeachingPracticeReturnAndAdvanceStayOnUnit12() {
        val p = pack()
        ready(p).use { s ->
            compose.setContent { V2App(s, p, {}) }
            waitTag("v2-continue"); compose.onNodeWithTag("v2-continue").performClick()
            waitTag("v2-review-start"); compose.onNodeWithTag("v2-review-start").performScrollTo().performClick()
            waitTag("v2-review-count"); val a = s.cumulativeReviewHistory().first.single { it.targetUnit == 12 }
            val noun = p.item(a.slots[1].itemId)!!
            compose.onNode(hasText(noun.promptLetter!!) and !hasClickAction()).performScrollTo().assertIsDisplayed()
            val wrong = noun.options.first { it != noun.answer }
            compose.onNodeWithTag("v2-review-option-1-$wrong").performScrollTo().performClick()
            waitTag("v2-review-feedback-1"); compose.onNodeWithTag("v2-review-feedback-1").assertTextEquals("Not the reviewed answer. ${noun.feedback}")
            compose.onNodeWithTag("v2-review-remediate-1").performScrollTo().performClick()
            waitTag("v2-example-ex-or-u11-past-aspect-4"); compose.onNodeWithTag("v2-example-ex-or-u11-past-aspect-4").performScrollTo().assertIsDisplayed()
            compose.onNodeWithText("The pronoun I does not select one past-tense gender for every speaker.", substring = true).performScrollTo().assertIsDisplayed()
            waitTag("v2-review-practice"); compose.onNodeWithTag("v2-review-practice").performClick()
            val practice = reviewedGuidedPractice(p, "u11-st-rem-past-aspect")
            assertEquals(listOf("u11-st-guided-past-aspect-1"), practice)
            assertEquals(3, p.step("u11-st-rem-past-aspect")!!.second.exampleIds.size)
            assertNotEquals(noun.promptLetter, p.item("u11-g-past-aspect-0")!!.promptLetter)
            assertNotEquals(noun.answer, p.item("u11-g-past-aspect-0")!!.answer)
            val remediation = p.step("u11-st-rem-past-aspect")!!.second
            for (n in 1..4) assertTrue(remediation.body.contains(p.step("u11-st-teach-past-aspect-$n")!!.second.body))
            practice.forEach { step ->
                p.step(step)!!.second.itemIds.forEach { id ->
                    val i = p.item(id)!!; waitTag("v2-option-$id-${i.answer}")
                    compose.onNodeWithTag("v2-option-$id-${i.answer}").performScrollTo().performClick()
                }
            }
            waitTag("v2-return-to-check"); compose.onNodeWithTag("v2-return-to-check").performClick()
            waitTag("v2-review-count"); assertEquals(a, s.cumulativeReviewHistory().first.single { it.targetUnit == 12 })
            assertFalse(1 in completedReviewSlots(p, a, s.cumulativeReviewHistory().second))
            a.slots.forEachIndexed { index, slot ->
                val answer = p.item(slot.itemId)!!.answer
                compose.onNodeWithTag("v2-review-option-$index-$answer").performScrollTo().performClick()
                compose.waitForIdle()
            }
            waitTag("v2-review-gate"); compose.onNodeWithTag("v2-review-gate").performScrollTo().performClick()
            waitTag("v2-advance"); compose.onNodeWithTag("v2-advance").performScrollTo().assertIsEnabled().performClick()
            compose.waitUntil(30000) { compose.waitForIdle(); s.v2Profile().currentUnitNumber == 13 }
        }
    }

    @Test fun heldUnit12WriteRejectsReplacementEpochEvenWhenNewLearnerStillHasEligibleTeaching() {
        val p = pack()
        ready(p).use { s ->
            val entered = CompletableDeferred<Unit>(); val release = CompletableDeferred<Unit>()
            val api = object : V2Persistence by StoreV2Persistence(s, p) {
                override suspend fun openCumulativeReview(pack: V2CurriculumPack, targetUnit: Int) {
                    assertEquals(12, targetUnit); entered.complete(Unit); release.await()
                    StoreV2Persistence(s, p).openCumulativeReview(pack, targetUnit)
                }
            }
            V2UiWork.blockForTests = false
            compose.setContent { V2App(s, p, {}, persistence = api) }
            waitTag("v2-continue"); compose.onNodeWithTag("v2-continue").performClick()
            waitTag("v2-review-start"); compose.onNodeWithTag("v2-review-start").performScrollTo().performClick()
            compose.waitUntil(30000) { compose.waitForIdle(); entered.isCompleted }
            compose.onNodeWithTag("v2-back").performClick(); waitTag("v2-continue")
            val d = s.governedLearnerDocument(); val replacement = d.copy(profile = d.profile.copy(currentUnitNumber = 1))
            s.openLearnerReplacement()
            try { assertEquals(GovernedReplaceOutcome.REPLACED, s.replaceGovernedLearner(replacement, emptyList())) }
            finally { s.closeLearnerReplacement() }
            assertNotNull(createCumulativeReviewAssignment(p, s.v2LearnerView(), 12, "still-eligible", 0))
            val before = s.governedLearnerFingerprint(); release.complete(Unit)
            compose.waitUntil(30000) { compose.waitForIdle(); compose.onAllNodesWithText("Not saved. The previous record is unchanged.", substring = true).fetchSemanticsNodes().isNotEmpty() }
            assertEquals(before, s.governedLearnerFingerprint())
            assertFalse(s.cumulativeReviewHistory().first.any { it.targetUnit == 12 })
        }
    }

    private fun ready(p: V2CurriculumPack) = LocalStore.openInMemory(context()).also { prepare(p, it) }
    private fun prepare(p: V2CurriculumPack, s: LocalStore) {
        for (n in 1..2) { ordinary(p, s, n); s.v2CompleteUnit(n, "done") }
        ordinary(p, s, 3)
        val a = s.openUnit3CumulativeReview(p, s.learnerMutationEpoch())!!
        a.slots.indices.forEach { answer(p, s, a, it) }
        runBlocking { advanceFrom(p, s, 3) }
        ordinary(p, s, 4)
        val previous = s.openCumulativeReview(p, 4, s.learnerMutationEpoch())!!
        previous.slots.indices.forEach { answer(p, s, previous, it) }
        runBlocking { advanceFrom(p, s, 4) }
        ordinary(p, s, 5)
        val fifth = s.openCumulativeReview(p, 5, s.learnerMutationEpoch())!!
        fifth.slots.indices.forEach { answer(p, s, fifth, it) }
        runBlocking { advanceFrom(p, s, 5) }
        ordinary(p, s, 6)
        val sixth = s.openCumulativeReview(p, 6, s.learnerMutationEpoch())!!
        sixth.slots.indices.forEach { answer(p, s, sixth, it) }
        runBlocking { advanceFrom(p, s, 6) }
        ordinary(p, s, 7)
        val seventh = s.openCumulativeReview(p, 7, s.learnerMutationEpoch())!!
        seventh.slots.indices.forEach { answer(p, s, seventh, it) }
        runBlocking { advanceFrom(p, s, 7) }
        ordinary(p, s, 8)
        val eighth = s.openCumulativeReview(p, 8, s.learnerMutationEpoch())!!
        eighth.slots.indices.forEach { answer(p, s, eighth, it) }
        runBlocking { advanceFrom(p, s, 8) }
        ordinary(p, s, 9)
        val ninth = s.openCumulativeReview(p, 9, s.learnerMutationEpoch())!!
        ninth.slots.indices.forEach { answer(p, s, ninth, it) }
        runBlocking { advanceFrom(p, s, 9) }
        ordinary(p, s, 10)
        val tenth = s.openCumulativeReview(p, 10, s.learnerMutationEpoch())!!
        tenth.slots.indices.forEach { answer(p, s, tenth, it) }
        runBlocking { advanceFrom(p, s, 10) }
        ordinary(p, s, 11)
        val eleventh = s.openCumulativeReview(p, 11, s.learnerMutationEpoch())!!
        eleventh.slots.indices.forEach { answer(p, s, eleventh, it) }
        runBlocking { advanceFrom(p, s, 11) }
        ordinary(p, s, 12)
        assertEquals((1..11).toSet(), currentCompletedUnitNumbers(p, s.v2LearnerView()))
    }
    private fun ordinary(p: V2CurriculumPack, s: LocalStore, n: Int) {
        s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = n, currentSessionId = null))
        for (step in p.unit(n)!!.sessions.flatMap { it.steps }) {
            for (id in step.itemIds) s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, p.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "same-clock")!!)
            s.v2CompleteGovernedStep(step.id, governedStepCompletionDigest(p, s.v2LearnerView(), step.id)!!, "same-clock")
        }
        s.v2AttestBook(n, "same-clock")
    }
    private fun open(p: V2CurriculumPack, s: LocalStore) = s.openCumulativeReview(p, 12, s.learnerMutationEpoch())!!
    private fun answer(p: V2CurriculumPack, s: LocalStore, a: CumulativeReviewAssignment, index: Int, correct: Boolean = true) {
        val i = p.item(a.slots[index].itemId)!!
        assertTrue(s.answerCumulativeReview(p, a.id, index, if (correct) i.answer else i.options.first { it != i.answer }, s.learnerMutationEpoch()))
    }
    private fun advance(p: V2CurriculumPack, s: LocalStore): Boolean = runBlocking { advanceFrom(p, s, 12) }
    private suspend fun advanceFrom(p: V2CurriculumPack, s: LocalStore, n: Int): Boolean {
        val session = p.unit(n + 1)!!.sessions.first().id
        return s.v2AdvanceUnitIfCurrentAsync(s.learnerMutationEpoch(), n, "advance", n + 1, session, resumePositionDigest(p, session, 0)!!, GOVERNED_IDENTITY_SCHEMA, p)
    }
    private fun waitTag(tag: String) = compose.waitUntil(conditionDescription = tag, timeoutMillis = 30000) {
        compose.waitForIdle(); compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
    }
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    private fun pack() = sharedPack
    companion object { private val sharedPack by lazy {
        val root = File(if (File("app").isDirectory) "." else "..")
        V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) }
    } }
}
