package dev.russiancompanion.v2

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.*
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream
import java.io.File

/** A2-32 binder SELF_CHECK, A2-33 tutor task and A2-34 unit test, with English-only fixtures (MA2-162). */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class UnitTestAndBinderTest {
    @get:Rule val compose = createComposeRule()
    @After fun reset() { V2UiWork.blockForTests = null }

    @Test fun currentPackReportsWhyNoUnitTestExistsInsteadOfNarrowingTheRule() {
        val p = pack()
        // T14-b: Units 1-2 carry typed items (owner decision), so each has a unit test meeting A2-34.
        for (unit in p.units) {
            if (unit.number <= 5) { assertNotNull(unitTestPlan(p, unit)); assertNull(unitTestUnavailableReason(p, unit)) }
            else { assertNull(unitTestPlan(p, unit)); assertNotNull(unitTestUnavailableReason(p, unit)) }
        }
        unitTestPlan(p, p.unit(1)!!)!!.let { assertEquals(10, it.itemIds.size); assertEquals(3, it.typedCount) }
        unitTestPlan(p, p.unit(2)!!)!!.let { assertEquals(16, it.itemIds.size); assertEquals(5, it.typedCount) }
        // T14-b: Units 3-4 reach 20 checked items with 6 typed.
        // T14-c: Unit 5 likewise; later blocks have no typed items yet.
        for (n in 3..5) unitTestPlan(p, p.unit(n)!!)!!.let { assertEquals(20, it.itemIds.size); assertEquals(6, it.typedCount) }
        assertTrue(unitTestUnavailableReason(p, p.unit(6)!!)!!.contains("0 typed items"))
        val few = p.copy(units = p.units.map { if (it.number != 2) it else it.copy(objectives = it.objectives.map { o -> o.copy(checkItemIds = o.checkItemIds.take(2)) }) })
        assertTrue(unitTestUnavailableReason(few, few.unit(2)!!)!!.contains("at least 10 are needed"))
        assertTrue(courseworkDefects(p).isEmpty())
    }

    @Test fun planUsesTenToTwentyUnitItemsWithAtLeastThirtyPercentTypedAndAFixedPassMark() {
        val f = fixture(typed = 6)
        val plan = unitTestPlan(f, f.unit(4)!!)!!
        assertEquals(20, plan.itemIds.size); assertEquals(6, plan.typedCount); assertEquals(UNIT_TEST_PASS_PERCENT, plan.passMarkPercent)
        assertEquals(plan, unitTestPlan(f, f.unit(4)!!))
        assertTrue(plan.itemIds.all { id -> f.unit(4)!!.objectives.any { id in it.checkItemIds } })
        assertEquals("20 checked items, 6 typed. Pass mark: 80 percent.", unitTestSummary(plan))
        // Fewer typed items shrink the test to keep the typed share; below 10 items there is no test.
        val small = fixture(typed = 4)
        unitTestPlan(small, small.unit(4)!!)!!.let { assertEquals(13, it.itemIds.size); assertEquals(4, it.typedCount) }
        val none = fixture(typed = 2)
        assertNull(unitTestPlan(none, none.unit(4)!!))
        assertTrue(unitTestUnavailableReason(none, none.unit(4)!!)!!.contains("30 percent"))
        assertTrue(unitTestPasses(16, 20, 80)); assertFalse(unitTestPasses(15, 20, 80))
        // A changed reviewed key changes the plan identity.
        val changed = f.copy(items = f.items.map { if (it.id == "fixture-typed-0") it.copy(prompt = it.prompt + " Changed.") else it })
        assertNotEquals(plan.digest, unitTestPlan(changed, changed.unit(4)!!)!!.digest)
    }

    @Test fun attemptsScoreOnceRouteAFailToRemediationAllowRetakeAndSurviveCurrentBackupRestore() {
        val f = fixture(typed = 6)
        LocalStore.openInMemory(context()).use { s ->
            ready(f, s)
            val epoch = s.learnerMutationEpoch()
            val attemptsBefore = s.v2LearnerView().governedAttempts
            val reviewBefore = s.cumulativeReviewHistory()
            val first = s.startUnitTest(f, 4, epoch)!!
            assertEquals(first, s.startUnitTest(f, 4, epoch))
            assertNull(s.submitUnitTest(f, first.id, epoch))
            assertFalse(s.answerUnitTest(f, first.id, 0, "", epoch))
            assertFalse(s.answerUnitTest(f, first.id, 99, "x", epoch))
            first.itemIds.forEachIndexed { index, id -> assertTrue(s.answerUnitTest(f, first.id, index, if (index < 5) wrong(f, id) else right(f, id), epoch)) }
            val failed = s.submitUnitTest(f, first.id, epoch)!!
            assertEquals(15, failed.correctCount); assertFalse(failed.passed); assertTrue(failed.submittedOrder > 0)
            assertFalse(s.answerUnitTest(f, first.id, 0, right(f, first.itemIds[0]), epoch))
            assertNull(s.submitUnitTest(f, first.id, epoch))
            assertTrue(unitTestRemediation(f, failed).isNotEmpty())
            assertTrue(unitTestRemediation(f, failed).all { (o, step) -> step in o.remediationStepIds })
            // Unit-test answers are their own record: no prescribed credit, no review answers.
            assertEquals(attemptsBefore, s.v2LearnerView().governedAttempts)
            assertEquals(reviewBefore, s.cumulativeReviewHistory())
            val retake = s.startUnitTest(f, 4, epoch)!!
            assertNotEquals(first.id, retake.id)
            retake.itemIds.forEachIndexed { index, id -> assertTrue(s.answerUnitTest(f, retake.id, index, right(f, id), epoch)) }
            val passed = s.submitUnitTest(f, retake.id, epoch)!!
            assertTrue(passed.passed); assertEquals(20, passed.correctCount); assertTrue(passed.submittedOrder > failed.submittedOrder)
            // A content change makes an open attempt stale; starting again replaces it.
            val open = s.startUnitTest(f, 4, epoch)!!
            val changed = f.copy(items = f.items.map { if (it.id == "fixture-typed-0") it.copy(prompt = it.prompt + " Changed.") else it })
            assertFalse(unitTestAttemptCurrent(changed, open))
            assertFalse(s.answerUnitTest(changed, open.id, 0, right(changed, open.itemIds[0]), epoch))
            val fresh = s.startUnitTest(changed, 4, epoch)!!
            assertNotEquals(open.id, fresh.id)
            assertEquals(1, s.v2LearnerView().unitTestAttempts.count { it.submittedOrder == 0L })

            val document = s.governedLearnerDocument()
            assertEquals(3, document.unitTestAttempts.size)
            val bytes = ByteArrayOutputStream()
            writeGovernedBackup(bytes, "dev.russiancompanion", f.packId, f.packVersion, "FOLLOW_SYSTEM", document, emptyList())
            val temp = File(context().cacheDir, "unit-test-${System.nanoTime()}")
            try {
                val read = readGovernedBackup(bytes.toByteArray().inputStream(), "dev.russiancompanion", f.packId, f.packVersion, temp) as BackupReadResult.Valid
                assertEquals(document, read.backup.document)
                LocalStore.openInMemory(context()).use { target ->
                    assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner(read.backup.document, emptyList()))
                    assertEquals(document.unitTestAttempts.toSet(), target.v2LearnerView().unitTestAttempts.toSet())
                    val before = target.governedLearnerFingerprint()
                    val forgedPass = document.copy(unitTestAttempts = document.unitTestAttempts.map { if (it.id == failed.id) it.copy(passed = true) else it })
                    val twoOpen = document.copy(unitTestAttempts = document.unitTestAttempts + fresh.copy(id = "second-open"))
                    for (bad in listOf(forgedPass, twoOpen)) {
                        assertEquals(GovernedReplaceOutcome.UNCHANGED, target.replaceGovernedLearner(bad, emptyList()))
                        assertEquals(before, target.governedLearnerFingerprint())
                    }
                }
            } finally { temp.deleteRecursively() }
        }
    }

    @Test fun binderSelfCheckIsRecordedAsSelfCheckOnlyAndTutorTaskNamesWhatToBring() {
        val f = fixture(typed = 6)
        val p = pack()
        assertNotEquals(V2Validator.unitContentReviewDigest(p, 4), V2Validator.unitContentReviewDigest(f, 4))
        assertEquals(V2Validator.unitContentReviewDigest(p, 5), V2Validator.unitContentReviewDigest(f, 5))
        assertTrue(courseworkDefects(f).isEmpty())
        val bad = f.copy(units = f.units.map { if (it.number == 4) it.copy(binderTask = it.binderTask!!.copy(objectiveIds = listOf("obj-u3-gender")),
            tutorTask = V2TutorTask("t", " ")) else it })
        assertEquals(2, courseworkDefects(bad).size)
        LocalStore.openInMemory(context()).use { s ->
            ready(f, s)
            val epoch = s.learnerMutationEpoch()
            // A unit without a binder task records no self-check.
        assertFalse(s.recordBinderSelfCheck(p.copy(units = p.units.map { if (it.number == 4) it.copy(binderTask = null) else it }), 4, "now", epoch))
            val unit = f.unit(4)!!
            val entry = { unitAssignmentChecklist(f, unit, s.v2LearnerView()).single { it.kind == V2AssignmentKind.BINDER_TASK } }
            assertEquals(V2AssignmentStatus.TODO, entry().status)
            assertTrue(s.recordBinderSelfCheck(f, 4, "now", epoch))
            assertEquals(V2AssignmentStatus.DONE, entry().status)
            assertEquals("Self-check only; not shown as demonstrated ability.", entry().detail)
            // A self-check is never demonstrated ability: progress and objective states are unchanged.
            val view = s.v2LearnerView()
            assertEquals(currentCompletedUnitNumbers(f, view.copy(binderSelfChecks = emptyList())), currentCompletedUnitNumbers(f, view))
            unit.objectives.forEach { assertEquals(objectiveState(it, view.copy(binderSelfChecks = emptyList()), f), objectiveState(it, view, f)) }
            val edited = f.copy(units = f.units.map { if (it.number == 4) it.copy(binderTask = it.binderTask!!.copy(prompt = "A different fixture prompt.")) else it })
            assertFalse(binderSelfCheckCurrent(edited, view.binderSelfChecks.single()))
            assertNull(binderSelfChecksDefect(view.binderSelfChecks))
            assertNotNull(binderSelfChecksDefect(view.binderSelfChecks + view.binderSelfChecks))
        }
    }

    @Test fun unitScreenAndTestScreenShowPassMarkFirstHideResultsUntilSubmitAndOfferRemediationAndRetake() {
        val f = fixture(typed = 6)
        LocalStore.openInMemory(context()).use { s ->
            ready(f, s)
            compose.setContent { V2App(s, f, {}) }
            waitTag("v2-continue")
            compose.onNodeWithText("Course").performClick()
            waitTag("v2-course-intro"); compose.onNode(hasScrollToIndexAction()).performScrollToIndex(4)
            waitTag("v2-unit-row-4"); compose.onNodeWithTag("v2-unit-row-4").performClick()
            waitTag("v2-binder-prompt"); compose.onNodeWithTag("v2-binder-prompt").performScrollTo().assertTextEquals("Write three fixture sentences about the unit topic.")
            compose.onNodeWithTag("v2-tutor-task").performScrollTo().assertTextEquals("Optional tutor task: bring your three fixture sentences")
            compose.onNodeWithTag("v2-binder-self-check").performScrollTo().performClick()
            waitTag("v2-binder-model"); compose.onNodeWithTag("v2-binder-model").performScrollTo().assertTextEquals("Model answer for comparison: Three short fixture sentences.")
            compose.onNodeWithTag("v2-unit-test-summary").performScrollTo().assertTextEquals("20 checked items, 6 typed. Pass mark: 80 percent.")
            compose.onNodeWithTag("v2-unit-test-open").performScrollTo().performClick()
            waitTag("v2-unit-test-passmark"); compose.onNodeWithTag("v2-unit-test-passmark").assertTextEquals("20 checked items, 6 typed. Pass mark: 80 percent.")
            compose.onNodeWithTag("v2-unit-test-start").performClick()
            waitTag("v2-unit-test-count")
            val attempt = s.v2LearnerView().unitTestAttempts.single()
            attempt.itemIds.forEachIndexed { index, id ->
                val item = f.item(id)!!
                val value = if (index < 5) wrong(f, id) else right(f, id)
                if (item.isTypedResponse()) {
                    compose.onNodeWithTag("v2-test-typed-$index").performScrollTo().performTextReplacement(value)
                    compose.onNodeWithTag("v2-test-typed-save-$index").performScrollTo().performClick()
                } else compose.onNodeWithTag("v2-test-option-$index-$value").performScrollTo().performClick()
                compose.waitUntil(30_000) { s.v2LearnerView().unitTestAttempts.single().answers.size == index + 1 }
            }
            assertTrue(compose.onAllNodesWithText("Not the reviewed answer", substring = true).fetchSemanticsNodes().isEmpty())
            compose.onNodeWithTag("v2-unit-test-submit").performScrollTo().assertIsEnabled().performClick()
            waitTag("v2-unit-test-result")
            compose.onNodeWithTag("v2-unit-test-result").assertTextEquals("15 of 20 correct. Pass mark 80 percent. Not passed.")
            val missed = unitTestRemediation(f, s.v2LearnerView().unitTestAttempts.single())
            compose.onNodeWithTag("v2-unit-test-remediate-${missed.first().first.id}").performScrollTo().assertIsDisplayed()
            compose.onNodeWithTag("v2-unit-test-retake").performScrollTo().performClick()
            compose.waitUntil(30_000) { s.v2LearnerView().unitTestAttempts.count { it.submittedOrder == 0L } == 1 }
            waitTag("v2-unit-test-count")
        }
    }

    private fun right(p: V2CurriculumPack, id: String): String = p.item(id)!!.let { it.typedAnswerKey?.acceptedAnswers?.first() ?: it.answer }
    private fun wrong(p: V2CurriculumPack, id: String): String = p.item(id)!!.let { item ->
        if (item.isTypedResponse()) "Not the fixture answer" else item.options.first { it != item.answer } }

    /** Unit 4 with English-only typed fixtures, an English binder task with a model answer and a tutor task. */
    private fun fixture(typed: Int): V2CurriculumPack {
        // Unit 4's own typed checks are set aside so the fixture controls the typed share exactly.
        val p = pack().let { base -> base.copy(units = base.units.map { u -> if (u.number != 4) u else u.copy(objectives = u.objectives.map { o ->
            o.copy(checkItemIds = o.checkItemIds.filter { id -> base.item(id)?.isTypedResponse() != true }) }) }) }
        val objective = "obj-u4-pronouns"
        val items = (0 until typed).map { n ->
            val key = V2TypedAnswerKey("fixture-key-$n", 1, listOf("fixture answer $n"))
            V2CheckItem("fixture-typed-$n", "TYPED_FORM", "en", "Type fixture answer $n.", options = emptyList(),
                answer = "fixture answer $n", feedback = "Compare with fixture answer $n.", objectiveId = objective,
                sourceId = "src-app-english-v2", reviewStatus = IMPLEMENTED_PENDING_FINAL_AUDIT,
                typedAnswerKey = key.copy(review = V2TypedKeyReview("English fixture reviewer", RESEARCH_VERIFIED, typedAnswerKeyDigest(key))))
        }
        return p.copy(items = p.items + items, units = p.units.map { unit ->
            if (unit.number != 4) unit else unit.copy(
                objectives = unit.objectives.map { if (it.id == objective) it.copy(checkItemIds = it.checkItemIds + items.map { i -> i.id }) else it },
                binderTask = V2BinderTask("fixture-binder", "Write three fixture sentences about the unit topic.", listOf(objective),
                    "Three short fixture sentences."),
                tutorTask = V2TutorTask("fixture-tutor", "your three fixture sentences"))
        })
    }

    private fun ready(p: V2CurriculumPack, s: LocalStore) {
        for (n in 1..2) { ordinary(p, s, n); s.v2CompleteUnit(n, "done") }
        ordinary(p, s, 3)
        val a = s.openCumulativeReview(p, 3, s.learnerMutationEpoch())!!
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
