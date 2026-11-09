package dev.russiancompanion.v2

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.RussianCompanionApplication
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class TypedResponseIntegrationTest {
    @get:Rule val compose = createComposeRule()
    private val context = ApplicationProvider.getApplicationContext<Context>()
    @Before fun closeStartupStore() { (context.applicationContext as RussianCompanionApplication).closeStartupStoreForTests() }

    private fun fixture(version: Int = 1): V2CurriculumPack {
        val p = OptionPresentationFixture.pack
        val id = cumulativeReviewPolicy(3)!!.pool.first().candidateItemIds.first()
        val key = V2TypedAnswerKey("english-fixture-key", version, listOf("Hello", "Hi"))
        val reviewed = key.copy(review = V2TypedKeyReview("English fixture reviewer", RESEARCH_VERIFIED, typedAnswerKeyDigest(key)))
        return p.copy(items = p.items.map { if (it.id == id) it.copy(kind = "TYPED_FORM", prompt = "Type the taught response.",
            promptLetter = null, promptLetterRole = "CUE", options = emptyList(), answer = "Hello",
            feedback = "Compare with the taught response.", distractorRationales = emptyList(), assessment = null,
            typedAnswerKey = reviewed) else it })
    }
    private fun typed(p: V2CurriculumPack) = p.items.single { it.isTypedResponse() }
    private fun check(p: V2CurriculumPack) = p.units.flatMap { it.sessions }.flatMap { it.steps }
        .first { it.kind == "CHECK" && typed(p).id in it.itemIds }
    private fun write(p: V2CurriculumPack, value: String, role: String = PRESCRIBED_CHECK, time: String = "fixed") =
        prepareGovernedAttempt(p, typed(p).id, value, role, if (role == OPTIONAL_PRACTICE) practiceStepId("grammar") else check(p).id, time)!!

    @Test fun rawTypedAttemptsSurviveNamedReopenAndBackupButChangedKeysOrRulesCannotSupplyCredit() {
        val p = fixture(); val id = typed(p).id; val name = "typed-${System.nanoTime()}.db"
        val temp = File(context.cacheDir, "typed-restore-${System.nanoTime()}")
        try {
            val bytes = LocalStore.openNamed(context, name).use { s ->
                val a = write(p, "Aa"); val b = write(p, "BB")
                assertEquals("Aa".hashCode(), "BB".hashCode())
                assertNotEquals(a.id, b.id)
                s.v2RecordGovernedAttempt(a); s.v2RecordGovernedAttempt(b)
                assertFalse(id in creditItemIds(p, s.v2LearnerView()))
                val correct = write(p, " Hi ")
                assertTrue(correct.correct); assertEquals(typedResponseRuleDigest(), correct.ruleDigest)
                s.v2RecordGovernedAttempt(correct)
                s.v2RecordGovernedAttempt(correct)
                assertEquals(3, s.v2Attempts().size)
                assertEquals(" Hi ", s.v2Attempts().last().selected)
                assertTrue(id in creditItemIds(p, s.v2LearnerView()))
                ByteArrayOutputStream().also { writeStoreBackup(s, it, "dev.russiancompanion", p.packId, p.packVersion, "DARK") }.toByteArray()
            }
            LocalStore.openNamed(context, name).use { s ->
                assertEquals(" Hi ", governedSessionSelections(p, s.v2LearnerView())[governedSelectionKey(check(p).id, id)])
                assertTrue(id in creditItemIds(p, s.v2LearnerView()))
                assertFalse(id in creditItemIds(fixture(2), s.v2LearnerView()))
                val last = s.v2LearnerView().governedAttempts!!.last()
                assertFalse(attemptGrantsPrescribedCredit(last.copy(ruleDigest = closedResponseRuleDigest()), p))
                val forged = last.copy(selected = "Wrong", correct = true)
                assertFalse(attemptGrantsPrescribedCredit(forged, p))
                assertFalse(attemptSuppliesStepSelection(forged, p, check(p).id))
                val revoked = p.copy(items = p.items.map { if (it.id == id) it.copy(typedAnswerKey = it.typedAnswerKey!!.copy(review = null)) else it })
                assertFalse(id in creditItemIds(revoked, s.v2LearnerView()))
                assertNull(prepareGovernedAttempt(revoked, id, "Hi", PRESCRIBED_CHECK, check(p).id, "later"))
                val next = fixture(2)
                assertNotEquals(write(p, " Hi ").id, write(next, " Hi ").id)
                s.v2RecordGovernedAttempt(write(next, " Hi "))
                assertTrue(id in creditItemIds(next, s.v2LearnerView()))
            }
            val read = readGovernedBackup(bytes.inputStream(), "dev.russiancompanion", p.packId, p.packVersion, temp)
            assertTrue(read is BackupReadResult.Valid)
            LocalStore.openInMemory(context).use { s ->
                restoreStoreBackup(s, (read as BackupReadResult.Valid).backup, temp)
                assertEquals(" Hi ", s.v2Attempts().last().selected)
                assertTrue(id in creditItemIds(p, s.v2LearnerView()))
                assertFalse(id in creditItemIds(fixture(2), s.v2LearnerView()))
            }
        } finally { context.deleteDatabase(name); temp.deleteRecursively() }
    }

    @Test fun lessonDraftRestoresWithoutWritingThenExplicitWrongAndTrimmedVariantSubmissionsAreGraded() {
        val p = fixture(); val item = typed(p); val step = check(p)
        val session = p.units.flatMap { it.sessions }.first { step in it.steps }
        LocalStore.openInMemory(context).use { s ->
            val selections = mutableStateOf<Map<String, String>>(emptyMap())
            val restoration = StateRestorationTester(compose)
            restoration.setContent {
                V2SessionScreen(p, s.v2LearnerView(), session.id, session.steps.indexOf(step), false, selections.value,
                    emptySet(), emptySet(), emptyMap(), {}, { _, _ -> }, { id, response, stepId ->
                        s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, response, PRESCRIBED_CHECK, stepId, "now-${s.v2Attempts().size}")!!)
                        selections.value = governedSessionSelections(p, s.v2LearnerView()); true
                    }, { _, _ -> }, { _, _ -> }, { _, _ -> }, { _, _ -> }, {}, {}, {}, { _, _ -> }, {})
            }
            val input = compose.onNodeWithTag("v2-typed-${item.id}-input")
            input.performScrollTo().performTextInput("Wrong")
            restoration.emulateSavedInstanceStateRestore()
            input.assertTextContains("Wrong")
            assertTrue(s.v2Attempts().isEmpty())
            compose.onNodeWithTag("v2-typed-${item.id}-submit").performScrollTo().performClick()
            assertFalse(s.v2Attempts().last().correct)
            compose.onNodeWithTag("v2-item-feedback-${item.id}").assertTextContains("Not the reviewed answer.", substring = true)
            input.performScrollTo().performTextReplacement(" Hi ")
            compose.onNodeWithTag("v2-typed-${item.id}-submit").performScrollTo().performClick()
            assertTrue(s.v2Attempts().last().correct)
            assertEquals(" Hi ", s.v2Attempts().last().selected)
            compose.onNodeWithTag("v2-item-feedback-${item.id}").assertTextContains("Recorded as correct.", substring = true)
            restoration.emulateSavedInstanceStateRestore()
            assertEquals(2, s.v2Attempts().size)
            input.assertTextContains(" Hi ")
            compose.onNodeWithTag("v2-typed-${item.id}-submit").assertIsNotEnabled()
        }
    }

    @Test fun optionalLabSubmitsTypedVariantsWithoutGrantingRequiredCredit() {
        val p = fixture(); val id = typed(p).id
        LocalStore.openInMemory(context).use { s ->
            val learner = mutableStateOf(s.v2LearnerView())
            compose.setContent { V2LabScreen(p, learner.value, "grammar", emptySet(), {}, { _, response ->
                s.v2RecordGovernedAttempt(write(p, response, OPTIONAL_PRACTICE)); learner.value = s.v2LearnerView(); true
            }) }
            val tag = "v2-lab-typed-$id"
            waitForTag("$tag-input")
            compose.onNodeWithTag("v2-lab-next").assertIsNotEnabled()
            compose.onNodeWithTag("$tag-input").performScrollTo().performTextInput(" Hi ")
            assertTrue(s.v2Attempts().isEmpty())
            compose.onNodeWithTag("$tag-submit").performScrollTo().performClick()
            assertTrue(s.v2Attempts().last().correct)
            assertEquals(OPTIONAL_PRACTICE, s.v2Attempts().last().attemptRole)
            assertFalse(id in creditItemIds(p, s.v2LearnerView()))
            compose.onNodeWithTag("v2-lab-next").assertIsEnabled()
        }
    }

    @Test fun cumulativeReviewUsesTypedRuleRawAnswersAndAssignmentBoundCreditAcrossBackupRestore() {
        val p = fixture(); val id = typed(p).id
        LocalStore.openInMemory(context).use { s ->
            ready(p, s)
            s.v2RecordGovernedAttempt(write(p, "Wrong", time = "recent-miss"))
            val a = s.openCumulativeReview(p, 3, s.learnerMutationEpoch())!!
            val slot = a.slots.indexOfFirst { it.itemId == id }; assertTrue(slot >= 0)
            assertEquals(typedReviewResponseRuleDigest(), a.responseRuleDigest)
            val revoked = p.copy(items = p.items.map { if (it.id == id) it.copy(typedAnswerKey = it.typedAnswerKey!!.copy(review = null)) else it })
            assertFalse(reviewAssignmentCurrent(revoked, a))
            assertFalse(reviewAssignmentCurrent(p, a.copy(responseRuleDigest = presentedReviewResponseRuleDigest()).let { it.copy(digest = reviewAssignmentDigest(it)) }))
            val learner = mutableStateOf(s.v2LearnerView())
            compose.setContent { V2CumulativeReviewScreen(p, learner.value, 3, false, {}, {}, {}, { assignment, index, response ->
                assertTrue(s.answerCumulativeReview(p, assignment, index, response, s.learnerMutationEpoch()))
                learner.value = s.v2LearnerView()
            }, {}, {}, {}) }
            val tag = "v2-review-typed-$slot"
            waitForTag("$tag-input")
            compose.onNodeWithTag("$tag-input").performScrollTo().performTextInput("Wrong")
            compose.onNodeWithTag("$tag-submit").performScrollTo().performClick()
            assertFalse(slot in completedReviewSlots(p, a, s.v2LearnerView().reviewAnswers))
            compose.onNodeWithTag("$tag-input").performScrollTo().performTextReplacement(" Hi ")
            compose.onNodeWithTag("$tag-submit").performScrollTo().performClick()
            assertTrue(slot in completedReviewSlots(p, a, s.v2LearnerView().reviewAnswers))
            assertEquals(" Hi ", s.v2LearnerView().reviewAnswers.last().selected)
            for (n in a.slots.indices.filter { it != slot }) assertTrue(s.answerCumulativeReview(p, a.id, n,
                p.item(a.slots[n].itemId)!!.answer, s.learnerMutationEpoch()))
            val bytes = ByteArrayOutputStream().also { writeStoreBackup(s, it, "dev.russiancompanion", p.packId, p.packVersion, "DARK") }.toByteArray()
            val temp = File(context.cacheDir, "typed-review-${System.nanoTime()}")
            try {
                val read = readGovernedBackup(bytes.inputStream(), "dev.russiancompanion", p.packId, p.packVersion, temp) as BackupReadResult.Valid
                LocalStore.openInMemory(context).use { restored ->
                    restoreStoreBackup(restored, read.backup, temp)
                    assertEquals(a.slots.indices.toSet(), completedReviewSlots(p, a, restored.v2LearnerView().reviewAnswers))
                    assertTrue(completedReviewSlots(fixture(2), a, restored.v2LearnerView().reviewAnswers).isEmpty())
                    assertFalse(restored.answerCumulativeReview(fixture(2), a.id, slot, "Hi", restored.learnerMutationEpoch()))
                }
            } finally { temp.deleteRecursively() }
        }
    }

    @Test fun unreviewedAndBusyInputsAreDisabledAndKeyChangesResetTheUnsubmittedDraft() {
        val p = fixture(); val value = mutableStateOf(typed(p)); var submits = 0
        val enabled = mutableStateOf(false)
        compose.setContent { Column { V2TypedResponseInput(value.value, "fixture", null, enabled.value) { submits++ } } }
        val tag = "v2-typed-${typed(p).id}"
        compose.onNodeWithTag("$tag-input").assertIsNotEnabled()
        compose.runOnIdle { enabled.value = true }
        compose.onNodeWithTag("$tag-input").performTextInput("Unsubmitted draft")
        compose.runOnIdle { value.value = typed(fixture(2)) }
        compose.onNodeWithTag("$tag-input").assertTextEquals("Your answer", "")
        compose.runOnIdle { value.value = value.value.copy(typedAnswerKey = value.value.typedAnswerKey!!.copy(review = null)) }
        compose.onNodeWithTag("$tag-input").assertIsNotEnabled()
        compose.onNodeWithTag("$tag-submit").assertIsNotEnabled()
        assertEquals(0, submits)
    }

    private fun ready(p: V2CurriculumPack, s: LocalStore) {
        for (n in 1..2) {
            s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = n))
            for (step in p.unit(n)!!.sessions.flatMap { it.steps }) {
                step.itemIds.forEach { id -> s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, p.item(id)!!.answer,
                    PRESCRIBED_CHECK, step.id, "ready")!!) }
                s.v2CompleteGovernedStep(step.id, governedStepCompletionDigest(p, s.v2LearnerView(), step.id)!!, "ready")
            }
            s.v2AttestBook(n, "ready"); s.v2CompleteUnit(n, "ready")
        }
        s.v2SaveProfile(s.v2Profile().copy(currentUnitNumber = 3))
    }
    private fun waitForTag(tag: String) = compose.waitUntil(30_000) { compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty() }
}
