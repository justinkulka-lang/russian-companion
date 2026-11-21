package dev.russiancompanion.v2

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.*
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/** A2-35 gradebook from recorded evidence, with English-only unit-test fixtures (MA2-162). */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class GradebookTest {
    @get:Rule val compose = createComposeRule()
    @After fun reset() { V2UiWork.blockForTests = null }

    @Test fun perUnitAndObjectiveValuesFollowRecordedAttemptsReviewQueueAndUnitTests() {
        val f = fixture()
        LocalStore.openInMemory(context()).use { s ->
            ready(f, s)
            val epoch = s.learnerMutationEpoch()
            // One recorded miss on a Unit 4 check, then the correct answer.
            val item = f.item(f.unit(4)!!.objectives.first().checkItemIds.first())!!
            val step = f.unit(4)!!.sessions.flatMap { it.steps }.single { it.kind == "CHECK" && item.id in it.itemIds }
            s.v2RecordGovernedAttempt(prepareGovernedAttempt(f, item.id, item.options.first { it != item.answer }, PRESCRIBED_CHECK, step.id, "miss")!!)
            s.v2RecordGovernedAttempt(prepareGovernedAttempt(f, item.id, item.answer, PRESCRIBED_CHECK, step.id, "hit")!!)
            // Two unit-test attempts: a fail, then a pass.
            for (pass in listOf(false, true)) {
                val a = s.startUnitTest(f, 4, epoch)!!
                a.itemIds.forEachIndexed { index, id -> assertTrue(s.answerUnitTest(f, a.id, index, if (!pass && index < 5) wrong(f, id) else right(f, id), epoch)) }
                assertNotNull(s.submitUnitTest(f, a.id, epoch))
            }
            val view = s.v2LearnerView()
            val book = gradebook(f, view)
            assertEquals(listOf(1, 2, 3, 4), book.map { it.unitNumber })
            val attempts = view.governedAttempts!!.filter { it.acceptedOrder > 0 }
            for (unit in book) for (o in unit.objectives) {
                val own = attempts.filter { it.objectiveId == o.objectiveId && it.unitNumber == unit.unitNumber }
                assertEquals(V2Tally(own.count { it.correct }, own.size), o.attempts)
                assertEquals(view.spacedReviews.count { it.objectiveId == o.objectiveId && it.unitNumber == unit.unitNumber }, o.queueItems)
                assertTrue(o.stateLabel in setOf("Not started", "Learning", "Review required", "Demonstrated in app checks", "Completed by self-attestation only"))
                assertFalse(o.stateLabel.contains("master", ignoreCase = true))
            }
            val missed = book.single { it.unitNumber == 4 }.objectives.single { it.objectiveId == item.objectiveId }.attempts
            assertTrue(missed.total > missed.correct)
            // The Unit 3 review answers resolve to their slot objectives (earlier units); one wrong answer counts as wrong.
            val reviewTotal = book.sumOf { it.review.total }
            assertEquals(view.reviewAnswers.size, reviewTotal)
            assertEquals(view.reviewAnswers.size - 1, book.sumOf { it.review.correct })
            assertTrue(book.sumOf { it.queueItems } > 0)
            val tests = book.single { it.unitNumber == 4 }.unitTests
            assertEquals(listOf(false, true), tests.map { it.passed }); assertEquals(listOf(15, 20), tests.map { it.correctCount })
            assertTrue(book.filter { it.unitNumber < 4 }.all { it.unitTests.isEmpty() })
            assertEquals("none recorded", V2Tally(0, 0).label()); assertEquals("3 of 4 correct (75 percent)", V2Tally(3, 4).label())
            assertTrue(GRADEBOOK_NOTE.contains("not mastery or proficiency"))
        }
    }

    @Test fun progressShowsTheGradebookWithUnitTestAndReviewValues() {
        val f = fixture()
        LocalStore.openInMemory(context()).use { s ->
            ready(f, s)
            val epoch = s.learnerMutationEpoch()
            val a = s.startUnitTest(f, 4, epoch)!!
            a.itemIds.forEachIndexed { index, id -> assertTrue(s.answerUnitTest(f, a.id, index, right(f, id), epoch)) }
            assertNotNull(s.submitUnitTest(f, a.id, epoch))
            val unit4 = gradebook(f, s.v2LearnerView()).single { it.unitNumber == 4 }
            compose.setContent { V2App(s, f, {}) }
            waitTag("v2-continue")
            compose.onNodeWithText("Progress").performClick()
            waitTag("v2-gradebook")
            compose.onNodeWithTag("v2-gradebook-note").performScrollTo().assertTextEquals(GRADEBOOK_NOTE)
            compose.onNodeWithTag("v2-gradebook-unit-4").performScrollTo()
                .assertTextContains("Unit test: 20 of 20 correct. Pass mark 80 percent. Passed. 1 attempt.", substring = true)
                .assertTextContains("Check attempts: ${unit4.attempts.label()}", substring = true)
                .assertTextContains("Items in review queue: ${unit4.queueItems}", substring = true)
            val o = unit4.objectives.first()
            compose.onNodeWithTag("v2-gradebook-objective-${o.objectiveId}").performScrollTo()
                .assertTextEquals("${o.title}: ${o.stateLabel}. Attempts ${o.attempts.label()}. Review ${o.review.label()}. Queue ${o.queueItems}.")
        }
    }

    private fun right(p: V2CurriculumPack, id: String): String = p.item(id)!!.let { it.typedAnswerKey?.acceptedAnswers?.first() ?: it.answer }
    private fun wrong(p: V2CurriculumPack, id: String): String = p.item(id)!!.let { item ->
        if (item.isTypedResponse()) "Not the fixture answer" else item.options.first { it != item.answer } }

    private fun fixture(): V2CurriculumPack {
        val p = pack()
        val objective = "obj-u4-pronouns"
        val items = (0 until 6).map { n ->
            val key = V2TypedAnswerKey("fixture-key-$n", 1, listOf("fixture answer $n"))
            V2CheckItem("fixture-typed-$n", "TYPED_FORM", "en", "Type fixture answer $n.", options = emptyList(),
                answer = "fixture answer $n", feedback = "Compare with fixture answer $n.", objectiveId = objective,
                sourceId = "src-app-english-v2", reviewStatus = IMPLEMENTED_PENDING_FINAL_AUDIT,
                typedAnswerKey = key.copy(review = V2TypedKeyReview("English fixture reviewer", RESEARCH_VERIFIED, typedAnswerKeyDigest(key))))
        }
        return p.copy(items = p.items + items, units = p.units.map { unit ->
            if (unit.number != 4) unit else unit.copy(objectives = unit.objectives.map {
                if (it.id == objective) it.copy(checkItemIds = it.checkItemIds + items.map { i -> i.id }) else it })
        })
    }

    /** Units 1–3 completed through the real gate, with one wrong then right Unit 3 review answer. */
    private fun ready(p: V2CurriculumPack, s: LocalStore) {
        for (n in 1..2) { ordinary(p, s, n); s.v2CompleteUnit(n, "done") }
        ordinary(p, s, 3)
        val a = s.openCumulativeReview(p, 3, s.learnerMutationEpoch())!!
        val first = p.item(a.slots[0].itemId)!!
        assertTrue(s.answerCumulativeReview(p, a.id, 0, first.options.first { it != first.answer }, s.learnerMutationEpoch()))
        a.slots.indices.forEach { index -> assertTrue(s.answerCumulativeReview(p, a.id, index, p.item(a.slots[index].itemId)!!.answer, s.learnerMutationEpoch())) }
        val session = p.unit(4)!!.sessions.first().id
        assertTrue(runBlocking { s.v2AdvanceUnitIfCurrentAsync(s.learnerMutationEpoch(), 3, "advance", 4, session,
            resumePositionDigest(p, session, 0)!!, GOVERNED_IDENTITY_SCHEMA, p) })
        s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 4, currentSessionId = null))
    }
    private fun ordinary(p: V2CurriculumPack, s: LocalStore, n: Int) {
        s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = n, currentSessionId = null))
        for (step in p.unit(n)!!.sessions.flatMap { it.steps }) {
            for (id in step.itemIds) s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, p.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "same-clock")!!)
            s.v2CompleteGovernedStep(step.id, governedStepCompletionDigest(p, s.v2LearnerView(), step.id)!!, "same-clock")
        }
        s.v2AttestBook(n, "same-clock")
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
