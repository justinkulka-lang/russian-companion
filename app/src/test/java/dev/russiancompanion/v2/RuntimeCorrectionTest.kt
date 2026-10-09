package dev.russiancompanion.v2

import android.content.Context
import android.os.Looper
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.GOVERNED_IDENTITY_SCHEMA
import dev.russiancompanion.v2.curriculum.PRESCRIBED_CHECK
import dev.russiancompanion.v2.curriculum.governedSelectionKey
import dev.russiancompanion.v2.curriculum.governedSessionSelections
import dev.russiancompanion.v2.curriculum.V2CheckItem
import dev.russiancompanion.v2.curriculum.prepareGovernedAttempt
import dev.russiancompanion.v2.curriculum.stepContentDigest
import dev.russiancompanion.v2.curriculum.verifiedResume
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.advancementDecision
import dev.russiancompanion.v2.curriculum.continueTarget
import dev.russiancompanion.v2.curriculum.example
import dev.russiancompanion.v2.curriculum.reviewedGuidedPractice
import dev.russiancompanion.v2.curriculum.item
import dev.russiancompanion.v2.curriculum.session
import dev.russiancompanion.v2.curriculum.step
import dev.russiancompanion.v2.curriculum.unit
import dev.russiancompanion.v2.ui.StoreV2Persistence
import dev.russiancompanion.v2.ui.V2App
import dev.russiancompanion.v2.ui.V2Persistence
import dev.russiancompanion.v2.ui.V2StartupHost
import dev.russiancompanion.v2.ui.V2StartupResult
import dev.russiancompanion.v2.ui.V2UiWork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], qualifiers = "w411dp-h914dp-xxhdpi")
class RuntimeCorrectionTest {
    @get:Rule
    val compose = createComposeRule()

    private val stores = mutableListOf<LocalStore>()

    @After
    fun close() {
        V2UiWork.blockForTests = null
        LocalStore.learnerReadFailuresRemainingForTests.set(0)
        LocalStore.holdNextLearnerRefreshForTests = null
        LocalStore.learnerRefreshEnteredForTests = null
        LocalStore.failHeldLearnerRefreshForTests = false
        stores.forEach { it.close() }
    }

    @Test
    fun linkedExamplesAppearOnTheRunningLessonScreen() {
        val pack = pack()
        val store = openedStore()
        showStep(store, pack, "u1-st-iotated")
        val iotated = pack.step("u1-st-iotated")!!.second
        assertTrue(iotated.exampleIds.size >= 2)
        iotated.exampleIds.forEach { id ->
            val example = pack.example(id)!!
            assertFalse(iotated.body.contains(example.display))
            compose.onNodeWithTag("v2-example-$id").performScrollTo().assertIsDisplayed()
            compose.onNodeWithText(example.display).assertIsDisplayed()
            compose.onNodeWithTag("v2-example-note-$id").performScrollTo().assertIsDisplayed()
            compose.onNodeWithText(example.note).assertIsDisplayed()
        }
    }

    @Test
    fun laterUnitLinkedExamplesAppearOnTheSamePlayer() {
        val pack = pack()
        val store = openedStore()
        val later = pack.step("u7-st-teach-manner-1")!!.second
        showStep(store, pack, later.id, unitNumber = 7)
        later.exampleIds.forEach { id ->
            val example = pack.example(id)!!
            assertFalse(later.body.contains(example.display))
            compose.onNodeWithTag("v2-example-$id").performScrollTo().assertIsDisplayed()
            compose.onNodeWithText(example.display).assertIsDisplayed()
            compose.onNodeWithTag("v2-example-note-$id").performScrollTo().assertIsDisplayed()
        }
    }

    @Test
    fun questionsKeepIndependentSelectionsAndFeedback() {
        val pack = pack()
        val store = openedStore()
        val step = pack.step("u1-st-confusable-rest")!!.second
        assertEquals(3, step.itemIds.size)
        showStep(store, pack, step.id)
        val first = pack.item(step.itemIds[0])!!
        val second = pack.item(step.itemIds[1])!!
        val third = pack.item(step.itemIds[2])!!
        choose(first, wrong = true)
        compose.onNodeWithTag("v2-item-feedback-${first.id}").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Not the reviewed answer. ${first.feedback}", substring = true).assertIsDisplayed()
        assertDoesNotExist("v2-item-feedback-${second.id}")
        assertDoesNotExist("v2-item-feedback-${third.id}")
        choose(second, wrong = false)
        compose.onNodeWithTag("v2-item-feedback-${first.id}").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("v2-item-feedback-${second.id}").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Recorded as correct. ${second.feedback}", substring = true).assertIsDisplayed()
        assertDoesNotExist("v2-item-feedback-${third.id}")
        compose.onNodeWithTag("v2-option-${first.id}-${wrongOption(first)}").assertIsSelected()
        compose.onNodeWithTag("v2-option-${second.id}-${second.answer}").assertIsSelected()
        compose.onNodeWithTag("v2-option-${third.id}-${third.options.first()}").assertIsNotSelected()
    }

    @Test
    fun remediationOpensTheDeclaredTargetAndReturnsToTheFailedCheck() {
        val pack = pack()
        val store = openedStore()
        val step = pack.step("u1-st-confusable-rest")!!.second
        val targetId = step.remediationStepId!!
        val target = pack.step(targetId)!!.second
        showStep(store, pack, step.id)
        val item = pack.item(step.itemIds.first())!!
        choose(item, wrong = true)
        compose.onNodeWithTag("v2-remediation-${item.id}").performScrollTo().performClick()
        compose.onNodeWithTag("v2-step-title").assertIsDisplayed()
        compose.onNodeWithText(target.title).assertIsDisplayed()
        compose.onNodeWithText(target.body, substring = true).assertIsDisplayed()
        target.exampleIds.forEach { id ->
            compose.onNodeWithTag("v2-example-$id").performScrollTo().assertIsDisplayed()
        }
        completeRemediationPractice(pack, target.id)
        compose.onNodeWithTag("v2-return-to-check").performClick()
        compose.onNodeWithText(step.title).assertIsDisplayed()
        compose.onNodeWithTag("v2-item-feedback-${item.id}").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("v2-option-${item.id}-${wrongOption(item)}").assertIsSelected()
        assertEquals(
            wrongOption(item),
            governedSessionSelections(pack, store.v2LearnerView())[governedSelectionKey(step.id, item.id)],
        )
        assertEquals(step.id, store.v2Profile().currentSessionId?.let { pack.session(it)!!.steps[store.v2Profile().currentStepIndex].id })
    }

    @Test
    fun todayOpensAdvancementReviewInsteadOfTheLastLessonStep() {
        val pack = pack()
        val store = openedStore()
        val unit = pack.unit(1)!!
        unit.sessions.flatMap { it.steps }.forEach { step ->
            store.v2CompleteGovernedStep(step.id, stepContentDigest(pack, step), "2026-10-02T12:00")
        }
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 1))
        val target = continueTarget(pack, store.v2LearnerView())
        assertTrue(target.opensUnitReview)
        assertEquals(null, target.sessionId)
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        compose.onNodeWithText("Review advancement").assertIsDisplayed()
        compose.onNodeWithTag("v2-continue").performClick()
        compose.onNodeWithTag("v2-advancement-rule").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("v2-advance").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Remaining:", substring = true).assertIsDisplayed()
        assertDoesNotExist("v2-step-title")
    }

    @Test
    fun unitOneSequenceKeepsDraftsThroughRemediationResumeAndAdvancement() {
        V2UiWork.blockForTests = false
        val pack = pack()
        val store = openedStore()
        val unit = pack.unit(1)!!
        val iotated = pack.step("u1-st-iotated")!!.second
        val session = pack.session("u1-s-iotated")!!
        val check = session.steps.last()
        val activeStepIds = session.steps.map { it.id }.toSet()
        unit.sessions.flatMap { it.steps }
            .filter { it.id !in activeStepIds }
            .forEach { completed ->
                dev.russiancompanion.v2.curriculum.stepCompletionItemIds(pack, completed.id).forEach { itemId ->
                    val priorItem = pack.item(itemId)!!
                    store.v2RecordGovernedAttempt(
                        prepareGovernedAttempt(pack, itemId, priorItem.answer, PRESCRIBED_CHECK, completed.id, "2026-10-02T12:00")!!,
                    )
                }
                store.v2CompleteGovernedStep(completed.id, stepContentDigest(pack, completed), "2026-10-02T12:00")
            }
        store.v2SaveProfile(
            store.v2Profile().copy(
                onboardingCompleted = true,
                currentUnitNumber = 1,
                currentSessionId = "u1-s-iotated",
                currentStepIndex = 0,
            ).verifiedResume(pack),
        )
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        awaitTag("v2-continue")
        compose.onNodeWithTag("v2-continue").performClick()
        awaitTag("v2-step-title")
        iotated.exampleIds.forEach { id ->
            compose.onNodeWithTag("v2-example-$id").performScrollTo().assertIsDisplayed()
        }
        compose.onNodeWithTag("v2-session-continue").performClick()
        val intervening = pack.session("u1-s-iotated")!!.steps.drop(1).dropLast(1)
        intervening.forEach { guided ->
            awaitText(guided.title)
            guided.itemIds.forEach { id -> choose(pack.item(id)!!, wrong = false) }
            compose.onNodeWithTag("v2-session-continue").performClick()
        }
        awaitText(check.title)
        val item = pack.item(check.itemIds.first())!!
        choose(item, wrong = true)
        awaitTag("v2-remediation-${item.id}")
        compose.onNodeWithTag("v2-remediation-${item.id}").performScrollTo().performClick()
        compose.onNodeWithText(iotated.title).assertIsDisplayed()
        iotated.exampleIds.forEach { id ->
            compose.onNodeWithTag("v2-example-$id").performScrollTo().assertIsDisplayed()
        }
        completeRemediationPractice(pack, iotated.id)
        compose.onNodeWithTag("v2-return-to-check").performClick()
        compose.onNodeWithTag("v2-item-feedback-${item.id}").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("v2-note-draft").performScrollTo().performTextInput("letter draft")
        compose.onNodeWithTag("v2-back").performClick()
        awaitTag("v2-continue")
        compose.onNodeWithTag("v2-continue").performClick()
        compose.onNodeWithText("letter draft").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("v2-option-${item.id}-${wrongOption(item)}").performScrollTo().assertIsSelected()
        check.itemIds.drop(1).forEach { id ->
            val other = pack.item(id)!!
            choose(other, wrong = true)
            awaitTag("v2-item-feedback-$id")
        }
        compose.onNodeWithTag("v2-session-continue").performClick()
        awaitText("Review advancement")
        compose.onNodeWithText("Review advancement").assertIsDisplayed()
        compose.onNodeWithTag("v2-continue").performClick()
        compose.onNodeWithTag("v2-advancement-rule").performScrollTo().assertIsDisplayed()
        awaitTag("v2-advance")
        compose.onNodeWithTag("v2-advance").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun delayedStorageLeavesTheLessonScreenResponsive() {
        V2UiWork.blockForTests = false
        val pack = pack()
        val store = openedStore()
        val release = CountDownLatch(1)
        val entered = CountDownLatch(1)
        val persistence = GatedPersistence(StoreV2Persistence(store), entered, release)
        val step = pack.step("u1-st-confusable-rest")!!.second
        showStep(store, pack, step.id, persistence = persistence)
        val item = pack.item(step.itemIds.first())!!
        persistence.gateAttempts = true
        choose(item, wrong = true)
        choose(item, wrong = true)
        awaitEntered(entered)
        awaitText("Saving")
        assertDoesNotExist("v2-item-feedback-${item.id}")
        compose.onNodeWithTag("v2-note-draft").performScrollTo().performTextInput("still typing")
        compose.onNodeWithText("still typing").assertIsDisplayed()
        assertDoesNotExist("v2-item-feedback-${step.itemIds[1]}")
        release.countDown()
        awaitText("Saved")
        assertEquals(1, store.v2Attempts().count { it.itemId == item.id })
        val selections = governedSessionSelections(pack, store.v2LearnerView())
        assertEquals(1, selections.count { it.key == governedSelectionKey(step.id, item.id) })
        assertEquals(wrongOption(item), selections[governedSelectionKey(step.id, item.id)])
    }

    @Test
    fun failedWritePreservesPriorDataAndShowsOneFailure() {
        V2UiWork.blockForTests = false
        val pack = pack()
        val store = openedStore()
        val step = pack.step("u1-st-confusable-rest")!!.second
        val kept = pack.item(step.itemIds[0])!!
        val failed = pack.item(step.itemIds[1])!!
        store.v2RecordGovernedAttempt(
            prepareGovernedAttempt(pack, kept.id, kept.answer, PRESCRIBED_CHECK, step.id, "2026-10-02T12:00")!!,
        )
        val persistence = object : V2Persistence by StoreV2Persistence(store) {
            override suspend fun recordAttempt(write: dev.russiancompanion.v2.curriculum.GovernedAttemptWrite) {
                error("storage failed")
            }
        }
        showStep(store, pack, step.id, persistence = persistence)
        compose.onNodeWithTag("v2-item-feedback-${kept.id}").performScrollTo().assertIsDisplayed()
        choose(failed, wrong = true)
        awaitText("Not saved. The previous record is unchanged.")
        assertEquals(1, compose.onAllNodesWithText("Not saved. The previous record is unchanged.").fetchSemanticsNodes().size)
        compose.onNodeWithTag("v2-item-feedback-${kept.id}").performScrollTo().assertIsDisplayed()
        assertDoesNotExist("v2-item-feedback-${failed.id}")
        val saved = store.v2LearnerView()
        val selections = governedSessionSelections(pack, saved)
        assertEquals(kept.answer, selections[governedSelectionKey(step.id, kept.id)])
        assertFalse(selections.containsKey(governedSelectionKey(step.id, failed.id)))
        assertTrue(kept.id in saved.correctItemIds)
    }

    @Test
    fun repeatedContinueCannotSkipAStep() {
        V2UiWork.blockForTests = false
        val pack = pack()
        val store = openedStore()
        val gate = GatedPersistence(StoreV2Persistence(store))
        val session = pack.session("u1-s-orient")!!
        val first = session.steps[0]
        val second = session.steps[1]
        showStep(store, pack, first.id, persistence = gate)
        awaitQuiet(gate)
        gate.gateRefresh = true
        compose.onNodeWithTag("v2-session-continue").performClick()
        awaitEntered(gate.entered)
        assertEquals(1, gate.completeCalls.get())
        runCatching { compose.onNodeWithTag("v2-session-continue").performClick() }
        compose.onNodeWithTag("v2-session-continue").assertIsNotEnabled()
        assertEquals(1, gate.completeCalls.get())
        gate.release.countDown()
        awaitText(second.title)
        assertEquals(listOf(first.id), store.v2StepProgress().map { it.stepId })
        assertFalse(second.id in store.v2LearnerView().completedStepIds)
        compose.onNodeWithTag("v2-step-title").assertIsDisplayed()
    }

    @Test
    fun lateContinueCallbackCannotNavigateTheNextScreen() {
        V2UiWork.blockForTests = false
        val pack = pack()
        val store = openedStore()
        val gate = GatedPersistence(StoreV2Persistence(store))
        val first = pack.session("u1-s-orient")!!.steps[0]
        showStep(store, pack, first.id, persistence = gate)
        awaitQuiet(gate)
        gate.gateRefresh = true
        compose.onNodeWithTag("v2-session-continue").performClick()
        awaitEntered(gate.entered)
        compose.onNodeWithTag("v2-back").performClick()
        awaitTag("v2-continue")
        assertDoesNotExist("v2-step-title")
        gate.release.countDown()
        awaitQuiet(gate)
        compose.onNodeWithTag("v2-continue").assertIsDisplayed()
        assertDoesNotExist("v2-step-title")
        assertEquals(listOf(first.id), store.v2StepProgress().map { it.stepId })
    }

    @Test
    fun committedSaveReportsReloadFailureWithoutErasingTheWrite() {
        V2UiWork.blockForTests = false
        val pack = pack()
        val store = openedStore()
        val gate = GatedPersistence(StoreV2Persistence(store))
        val step = pack.step("u1-st-confusable-rest")!!.second
        val kept = pack.item(step.itemIds[0])!!
        val added = pack.item(step.itemIds[1])!!
        store.v2RecordGovernedAttempt(
            prepareGovernedAttempt(pack, kept.id, kept.answer, PRESCRIBED_CHECK, step.id, "2026-10-02T12:00")!!,
        )
        showStep(store, pack, step.id, persistence = gate)
        awaitQuiet(gate)
        gate.failRefresh = true
        choose(added, wrong = true)
        awaitText("Saved. The screen could not reload the new record.")
        assertEquals(
            0,
            compose.onAllNodesWithText("Not saved. The previous record is unchanged.").fetchSemanticsNodes().size,
        )
        compose.onNodeWithTag("v2-item-feedback-${kept.id}").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("v2-item-feedback-${added.id}").performScrollTo().assertIsDisplayed()
        assertEquals(setOf(kept.id, added.id), store.v2Attempts().map { it.itemId }.toSet())
        gate.failRefresh = false
        compose.onNodeWithTag("v2-back").performClick()
        awaitTag("v2-continue")
        compose.onNodeWithTag("v2-continue").performClick()
        awaitTag("v2-step-title")
        awaitQuiet(gate)
        compose.onNodeWithText("Saved. The screen could not reload the new record.").assertIsDisplayed()
        compose.onNodeWithTag("v2-option-${kept.id}-${kept.answer}").performScrollTo().assertIsSelected()
        compose.onNodeWithTag("v2-option-${added.id}-${wrongOption(added)}").performScrollTo().assertIsSelected()
    }

    @Test
    fun olderRefreshCannotReplaceANewerSelection() {
        V2UiWork.blockForTests = false
        val pack = pack()
        val store = openedStore()
        val gate = GatedPersistence(StoreV2Persistence(store))
        val step = pack.step("u1-st-confusable-rest")!!.second
        val first = pack.item(step.itemIds[0])!!
        val second = pack.item(step.itemIds[1])!!
        showStep(store, pack, step.id, persistence = gate)
        awaitQuiet(gate)
        gate.armStaleRefresh = true
        choose(first, wrong = true)
        awaitEntered(gate.entered)
        choose(second, wrong = false)
        awaitText("Saved")
        gate.release.countDown()
        awaitQuiet(gate)
        compose.onNodeWithTag("v2-item-feedback-${first.id}").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("v2-item-feedback-${second.id}").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("v2-option-${second.id}-${second.answer}").assertIsSelected()
        assertEquals(setOf(first.id, second.id), store.v2Attempts().map { it.itemId }.toSet())
    }

    @Test
    fun failedRetryKeepsTheSavedAnswerOnScreen() {
        V2UiWork.blockForTests = false
        val pack = pack()
        val store = openedStore()
        val step = pack.step("u1-st-confusable-rest")!!.second
        val item = pack.item(step.itemIds.first())!!
        store.v2RecordGovernedAttempt(
            prepareGovernedAttempt(pack, item.id, item.answer, PRESCRIBED_CHECK, step.id, "2026-10-02T12:00")!!,
        )
        val persistence = object : V2Persistence by StoreV2Persistence(store) {
            override suspend fun recordAttempt(write: dev.russiancompanion.v2.curriculum.GovernedAttemptWrite) {
                error("storage failed")
            }
        }
        showStep(store, pack, step.id, persistence = persistence)
        compose.onNodeWithTag("v2-item-feedback-${item.id}").performScrollTo().assertIsDisplayed()
        val before = store.v2Attempts().size
        choose(item, wrong = true)
        awaitText("Not saved. The previous record is unchanged.")
        compose.onNodeWithTag("v2-option-${item.id}-${item.answer}").performScrollTo().assertIsSelected()
        compose.onNodeWithTag("v2-option-${item.id}-${wrongOption(item)}").assertIsNotSelected()
        compose.onNodeWithText("Recorded as correct. ${item.feedback}", substring = true).assertIsDisplayed()
        assertEquals(before, store.v2Attempts().size)
        assertEquals(
            item.answer,
            governedSessionSelections(pack, store.v2LearnerView())[governedSelectionKey(step.id, item.id)],
        )
    }

    @Test
    fun repeatedTutorTapsCreateOneQueueRow() {
        V2UiWork.blockForTests = false
        val pack = pack()
        val store = openedStore()
        val gate = GatedPersistence(StoreV2Persistence(store))
        val ticks = AtomicInteger()
        val step = pack.step("u1-st-orient")!!.second
        showStep(store, pack, step.id, persistence = gate, recordedAt = { "tutor-${ticks.incrementAndGet()}" })
        awaitQuiet(gate)
        assertEquals(0, ticks.get())
        gate.gateTutor = true
        compose.onNodeWithTag("v2-tutor").performScrollTo().performClick()
        awaitEntered(gate.entered)
        runCatching { compose.onNodeWithTag("v2-tutor").performScrollTo().performClick() }
        runCatching { compose.onNodeWithTag("v2-tutor").performClick() }
        compose.onNodeWithTag("v2-tutor").performScrollTo().assertIsNotEnabled()
        assertEquals(1, gate.tutorCalls.get())
        assertEquals(1, ticks.get())
        gate.release.countDown()
        awaitText("Saved")
        val rows = store.v2TutorQueue()
        assertEquals(1, rows.size)
        assertEquals(step.id, rows.single().targetId)
        assertEquals("tutor-1", rows.single().createdAtLocal)
        assertEquals(1, compose.onAllNodesWithText("Saved").fetchSemanticsNodes().size)
    }

    @Test
    fun olderSaveRefreshCannotHideANewerRejectedWrite() {
        V2UiWork.blockForTests = false
        val pack = pack()
        val store = openedStore()
        val gate = GatedPersistence(StoreV2Persistence(store))
        val step = pack.step("u1-st-orient")!!.second
        showStep(store, pack, step.id, persistence = gate)
        awaitQuiet(gate)
        compose.onNodeWithTag("v2-note-draft").performScrollTo().performTextInput("kept note")
        gate.gateRefresh = true
        compose.onNodeWithTag("v2-save-note").performScrollTo().performClick()
        awaitEntered(gate.entered)
        gate.failTutor = true
        compose.onNodeWithTag("v2-tutor").performScrollTo().performClick()
        awaitText("Not saved. The previous record is unchanged.")
        gate.release.countDown()
        awaitQuiet(gate)
        compose.onNodeWithText("Not saved. The previous record is unchanged.").assertIsDisplayed()
        assertEquals(0, compose.onAllNodesWithText("Saved").fetchSemanticsNodes().size)
        assertEquals(1, store.v2Notes().size)
        assertEquals(0, store.v2TutorQueue().size)
        assertEquals(step.id, store.v2Notes().single().targetId)
    }

    @Test
    fun olderSaveRefreshCannotClaimSuccessWhileALaterWriteIsPending() {
        V2UiWork.blockForTests = false
        val pack = pack()
        val store = openedStore()
        val gate = GatedPersistence(StoreV2Persistence(store))
        val tutorEntered = CountDownLatch(1)
        val tutorRelease = CountDownLatch(1)
        gate.separateTutorGate = tutorEntered to tutorRelease
        val step = pack.step("u1-st-orient")!!.second
        showStep(store, pack, step.id, persistence = gate)
        awaitQuiet(gate)
        compose.onNodeWithTag("v2-note-draft").performScrollTo().performTextInput("kept note")
        gate.gateRefresh = true
        compose.onNodeWithTag("v2-save-note").performScrollTo().performClick()
        awaitEntered(gate.entered)
        gate.gateTutor = true
        compose.onNodeWithTag("v2-tutor").performScrollTo().performClick()
        awaitEntered(tutorEntered)
        gate.release.countDown()
        awaitQuiet(gate)
        compose.onNodeWithText("Saving").assertIsDisplayed()
        assertEquals(0, compose.onAllNodesWithText("Saved").fetchSemanticsNodes().size)
        compose.onNodeWithTag("v2-tutor").performScrollTo().assertIsNotEnabled()
        assertEquals(1, store.v2Notes().size)
        assertEquals(0, store.v2TutorQueue().size)
        tutorRelease.countDown()
        awaitExactText("Saved")
        assertEquals(1, store.v2TutorQueue().size)
        assertEquals(1, store.v2Notes().size)
        assertEquals(1, compose.onAllNodesWithText("Saved").fetchSemanticsNodes().size)
    }

    /**
     * Compose saved-state restoration in the current test Activity.
     * This does not recreate MainActivity.
     */
    @Test
    fun unsavedNoteDraftSurvivesComposeSavedState() {
        V2UiWork.blockForTests = false
        val pack = pack()
        val store = openedStore()
        val located = pack.step("u1-st-confusable-rest")!!
        val step = located.second
        val item = pack.item(step.itemIds.first())!!
        val index = located.first.steps.indexOfFirst { it.id == step.id }
        store.v2SaveProfile(
            store.v2Profile().copy(
                onboardingCompleted = true,
                currentUnitNumber = 1,
                currentSessionId = located.first.id,
                currentStepIndex = index,
            ).verifiedResume(pack),
        )
        val tester = StateRestorationTester(compose)
        tester.setContent {
            V2App(store = store, pack = pack, onRetryOpen = {}, persistence = StoreV2Persistence(store))
        }
        awaitTag("v2-continue")
        compose.onNodeWithTag("v2-continue").performClick()
        awaitText(step.title)
        compose.onNodeWithTag("v2-note-draft").performScrollTo().performTextInput("letter draft")
        choose(item, wrong = true)
        awaitText("Saved")
        tester.emulateSavedInstanceStateRestore()
        awaitText(step.title)
        compose.onNodeWithText("letter draft").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("v2-option-${item.id}-${wrongOption(item)}").performScrollTo().assertIsSelected()
        assertEquals(located.first.id, store.v2Profile().currentSessionId)
        assertEquals(index, store.v2Profile().currentStepIndex)
    }

    /**
     * Compose saved-state restoration in the current test Activity.
     * This does not recreate MainActivity or its startup path.
     */
    @Test
    fun composeSavedStateDuringADelayedWriteKeepsOneOutcome() {
        V2UiWork.blockForTests = false
        val pack = pack()
        val store = openedStore()
        val gate = GatedPersistence(StoreV2Persistence(store))
        val session = pack.session("u1-s-orient")!!
        val first = session.steps[0]
        val second = session.steps[1]
        store.v2SaveProfile(
            store.v2Profile().copy(
                onboardingCompleted = true,
                currentUnitNumber = 1,
                currentSessionId = session.id,
                currentStepIndex = 0,
            ).verifiedResume(pack),
        )
        val tester = StateRestorationTester(compose)
        tester.setContent {
            V2App(store = store, pack = pack, onRetryOpen = {}, persistence = gate)
        }
        awaitTag("v2-continue")
        compose.onNodeWithTag("v2-continue").performClick()
        awaitText(first.title)
        awaitQuiet(gate)
        compose.onNodeWithTag("v2-note-draft").performScrollTo().performTextInput("pending draft")
        gate.gateRefresh = true
        compose.onNodeWithTag("v2-session-continue").performClick()
        awaitEntered(gate.entered)
        tester.emulateSavedInstanceStateRestore()
        gate.release.countDown()
        awaitText("Saved")
        awaitQuiet(gate)
        assertEquals(1, gate.completeCalls.get())
        assertEquals(listOf(first.id), store.v2StepProgress().map { it.stepId })
        assertFalse(second.id in store.v2LearnerView().completedStepIds)
        if (compose.onAllNodesWithText(first.title).fetchSemanticsNodes().isEmpty()) {
            compose.onNodeWithTag("v2-continue").performClick()
            awaitText(first.title)
        }
        compose.onNodeWithText("pending draft").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText(second.title).assertDoesNotExist()
    }

    @Test
    fun remediationReachesGuidedPracticeThenReturnsToTheFailedCheck() {
        V2UiWork.blockForTests = false
        val pack = pack()
        val store = openedStore()
        val check = pack.step("u7-st-check-manner")!!.second
        val target = pack.step(check.remediationStepId!!)!!.second
        val guidedId = reviewedGuidedPractice(pack, target.id).single()
        val guided = pack.step(guidedId)!!.second
        assertEquals("u7-st-guided-manner-1", guidedId)
        showStep(store, pack, check.id, unitNumber = 7)
        val item = pack.item(check.itemIds.first())!!
        val practice = pack.item(guided.itemIds.first())!!
        choose(item, wrong = true)
        awaitTag("v2-remediation-${item.id}")
        compose.onNodeWithTag("v2-remediation-${item.id}").performScrollTo().performClick()
        awaitText(target.title)
        target.exampleIds.forEach { id ->
            compose.onNodeWithTag("v2-example-$id").performScrollTo().assertIsDisplayed()
        }
        compose.onNodeWithTag("v2-review-practice").performClick()
        awaitText(guided.title)
        compose.onNodeWithTag("v2-item-prompt-${practice.id}").performScrollTo().assertIsDisplayed()
        choose(practice, wrong = false)
        awaitTag("v2-item-feedback-${practice.id}")
        compose.onNodeWithTag("v2-return-to-check").performClick()
        awaitText(check.title)
        compose.onNodeWithTag("v2-option-${item.id}-${wrongOption(item)}").performScrollTo().assertIsSelected()
        val completed = store.v2LearnerView().completedStepIds
        assertFalse(guidedId in completed)
        assertFalse(check.id in completed)
        assertFalse(target.id in completed)
        val selections = governedSessionSelections(pack, store.v2LearnerView())
        assertEquals(wrongOption(item), selections[governedSelectionKey(check.id, item.id)])
        assertEquals(practice.answer, selections[governedSelectionKey(guided.id, practice.id)])
    }

    @Test
    fun formerGuidedPracticeGapsNowHaveRealItems() {
        val pack = pack()
        assertEquals(listOf("u7-st-guided-manner-1"), reviewedGuidedPractice(pack, "u7-st-rem-manner"))
        val targets = pack.units.flatMap { it.sessions }.flatMap { it.steps }.mapNotNull { it.remediationStepId }.toSet()
        assertTrue(targets.size >= 156)
        targets.forEach { target ->
            val guided = reviewedGuidedPractice(pack, target)
            assertTrue("$target is missing its linked guided practice", guided.isNotEmpty())
        }
        dev.russiancompanion.v2.curriculum.FILLED_GUIDED_PRACTICE_ROUTES.forEach { (remediationId, guidedId) ->
            val guided = pack.step(guidedId)!!.second
            assertEquals("GUIDED", guided.kind)
            assertTrue("$guidedId has no item", guided.itemIds.isNotEmpty())
            assertEquals(remediationId, guided.remediationStepId)
            val item = pack.item(guided.itemIds.first())!!
            assertTrue(item.answer.isNotBlank())
            assertTrue(item.answer in item.options)
            assertTrue(item.feedback.isNotBlank())
        }
    }

    @Test
    fun advancementRollsBackWhenTheFollowingProfileWriteFails() = kotlinx.coroutines.runBlocking {
        val store = openedStore()
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 1))
        store.failAdvanceAfterCompletionForTests = true
        val failed = runCatching {
            store.v2AdvanceUnitAsync(1, "2026-10-02T12:00", 2, "u2-s-next", "a".repeat(64), GOVERNED_IDENTITY_SCHEMA)
        }
        assertTrue(failed.isFailure)
        assertFalse(store.failAdvanceAfterCompletionForTests)
        assertFalse(1 in store.v2LearnerView().completedUnitNumbers)
        assertEquals(1, store.v2Profile().currentUnitNumber)
        assertEquals(0, store.v2Profile().currentStepIndex)
        assertEquals("", store.v2Profile().resumeContentDigest)
        assertEquals(0, store.v2Profile().resumeIdentitySchema)
        store.v2AdvanceUnitAsync(1, "2026-10-02T12:01", 2, "u2-s-next", "b".repeat(64), GOVERNED_IDENTITY_SCHEMA)
        assertTrue(1 in store.v2LearnerView().completedUnitNumbers)
        assertEquals(2, store.v2Profile().currentUnitNumber)
        assertEquals("u2-s-next", store.v2Profile().currentSessionId)
        assertEquals("b".repeat(64), store.v2Profile().resumeContentDigest)
        assertEquals(GOVERNED_IDENTITY_SCHEMA, store.v2Profile().resumeIdentitySchema)
    }

    @Test
    fun failedAdvancementLeavesTheReviewInPlace() {
        V2UiWork.blockForTests = false
        val pack = pack()
        val store = openedStore()
        val unit = pack.unit(1)!!
        unit.sessions.flatMap { it.steps }.forEach { step ->
            store.v2CompleteGovernedStep(step.id, stepContentDigest(pack, step), "2026-10-02T12:00")
        }
        unit.sessions.flatMap { it.steps }.flatMap { it.itemIds }.distinct().forEach { id ->
            val item = pack.item(id)!!
            val stepId = unit.sessions.flatMap { it.steps }.first { id in it.itemIds }.id
            store.v2RecordGovernedAttempt(
                prepareGovernedAttempt(pack, item.id, item.answer, PRESCRIBED_CHECK, stepId, "2026-10-02T12:00")!!,
            )
        }
        store.v2AttestBook(1, "2026-10-02T12:00")
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 1))
        val decision = advancementDecision(pack, unit, store.v2LearnerView())
        assertTrue(decision.remaining.joinToString(" | "), decision.allowed)
        store.failAdvanceAfterCompletionForTests = true
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        awaitTag("v2-continue")
        compose.onNodeWithText("Review advancement").assertIsDisplayed()
        compose.onNodeWithTag("v2-continue").performClick()
        awaitTag("v2-advance")
        compose.onNodeWithTag("v2-advance").performScrollTo().performClick()
        awaitText("Not saved. The previous record is unchanged.")
        assertFalse(1 in store.v2LearnerView().completedUnitNumbers)
        assertEquals(1, store.v2Profile().currentUnitNumber)
        assertFalse(store.failAdvanceAfterCompletionForTests)
        compose.onNodeWithTag("v2-advance").performScrollTo().performClick()
        awaitText("Saved")
        assertTrue(1 in store.v2LearnerView().completedUnitNumbers)
        assertEquals(2, store.v2Profile().currentUnitNumber)
    }

    @Test
    fun startupLoadingDoesNotBlockTheMainThread() {
        V2UiWork.blockForTests = false
        val entered = CountDownLatch(1)
        val release = CountDownLatch(1)
        val worker = AtomicReference<Thread>()
        compose.setContent {
            V2StartupHost(
                load = {
                    worker.set(Thread.currentThread())
                    entered.countDown()
                    release.await(10, TimeUnit.SECONDS)
                    V2StartupResult(store = null, pack = null, message = "stopped")
                },
                onRetryOpen = {},
            ) { _, _ ->
                Text("Course ready", modifier = Modifier.semantics { testTag = "v2-course-ready" })
            }
        }
        assertTrue(entered.await(5, TimeUnit.SECONDS))
        compose.onNodeWithText("Loading curriculum").assertIsDisplayed()
        compose.onNodeWithTag("v2-persistence-state").assertIsDisplayed()
        assertNotEquals(Looper.getMainLooper().thread, worker.get())
        release.countDown()
    }

    @Test
    fun storeWorkRunsOnTheStoreThread() {
        val store = openedStore()
        val caller = Thread.currentThread()
        val worker = runBlocking { store.awaitStore { Thread.currentThread() } }
        assertNotEquals(caller, worker)
        assertTrue(worker.name.startsWith("russian-companion-store"))
        val view = runBlocking { store.v2LearnerViewAsync() }
        assertFalse(view.onboardingCompleted)
        runBlocking {
            withContext(Dispatchers.Unconfined) {
                assertNotEquals("russian-companion-store", Thread.currentThread().name)
            }
        }
    }

    private fun showStep(
        store: LocalStore,
        pack: V2CurriculumPack,
        stepId: String,
        unitNumber: Int = 1,
        persistence: V2Persistence? = null,
        recordedAt: () -> String = { java.time.LocalDateTime.now().toString() },
    ) {
        val located = pack.step(stepId)!!
        val index = located.first.steps.indexOfFirst { it.id == stepId }
        store.v2SaveProfile(
            store.v2Profile().copy(
                onboardingCompleted = true,
                currentUnitNumber = unitNumber,
                currentSessionId = located.first.id,
                currentStepIndex = index,
            ).verifiedResume(pack),
        )
        compose.setContent {
            V2App(
                store = store,
                pack = pack,
                onRetryOpen = {},
                persistence = persistence ?: StoreV2Persistence(store),
                recordedAt = recordedAt,
            )
        }
        awaitTag("v2-continue")
        compose.onNodeWithTag("v2-continue").performClick()
        awaitTag("v2-step-title")
        compose.onNodeWithText(located.second.title).assertIsDisplayed()
    }

    private fun choose(item: V2CheckItem, wrong: Boolean) {
        val option = if (wrong) wrongOption(item) else item.answer
        compose.onNodeWithTag("v2-option-${item.id}-$option").performScrollTo().performClick()
    }

    private fun completeRemediationPractice(pack: V2CurriculumPack, targetId: String) {
        val guidedIds = reviewedGuidedPractice(pack, targetId)
        if (guidedIds.isEmpty()) return
        compose.onNodeWithTag("v2-review-practice").performClick()
        guidedIds.forEachIndexed { index, id ->
            val guided = pack.step(id)!!.second
            awaitText(guided.title)
            guided.itemIds.forEach { itemId ->
                val item = pack.item(itemId)!!
                compose.onNodeWithTag("v2-item-prompt-${item.id}").performScrollTo().assertIsDisplayed()
                choose(item, wrong = false)
                awaitTag("v2-item-feedback-${item.id}")
            }
            if (index < guidedIds.lastIndex) compose.onNodeWithTag("v2-next-practice").performClick()
        }
    }

    private fun wrongOption(item: V2CheckItem): String = item.options.first { it != item.answer }

    private fun assertDoesNotExist(tag: String) {
        compose.onNodeWithTag(tag).assertDoesNotExist()
    }

    private fun awaitExactText(text: String) {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < 5_000) {
            compose.waitForIdle()
            if (compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()) return
            Thread.sleep(20)
        }
        compose.onNodeWithText(text).assertIsDisplayed()
    }

    private fun awaitText(text: String) {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < 5_000) {
            compose.waitForIdle()
            if (compose.onAllNodesWithText(text, substring = true).fetchSemanticsNodes().isNotEmpty()) return
            Thread.sleep(20)
        }
        compose.onNodeWithText(text, substring = true).assertIsDisplayed()
    }

    private fun awaitEntered(latch: CountDownLatch) {
        val start = System.currentTimeMillis()
        while (latch.count > 0 && System.currentTimeMillis() - start < 5_000) {
            compose.waitForIdle()
            if (latch.count == 0L) return
            Thread.sleep(20)
        }
        assertEquals(0L, latch.count)
    }

    private fun awaitQuiet(gate: GatedPersistence) {
        val start = System.currentTimeMillis()
        var stable = 0
        var last = -1
        while (System.currentTimeMillis() - start < 20_000) {
            compose.waitForIdle()
            val calls = gate.refreshCalls.get()
            val flight = gate.refreshInFlight.get()
            // Opening a session submits two position writes after the initial load.
            // A write can still be inside its block while no refresh is in flight, so
            // wait until that third refresh has finished before arming the next gate.
            if (flight == 0 && calls >= 3 && calls == last) {
                stable += 1
                if (stable >= 3) return
            } else {
                stable = 0
            }
            last = calls
            Thread.sleep(30)
        }
        throw AssertionError("learner refresh did not settle; calls=${gate.refreshCalls.get()} inFlight=${gate.refreshInFlight.get()}")
    }

    private fun awaitTag(tag: String) {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < 5_000) {
            compose.waitForIdle()
            try {
                compose.onNodeWithTag(tag).assertExists()
                return
            } catch (_: AssertionError) {
                Thread.sleep(20)
            }
        }
        compose.onNodeWithTag(tag).assertExists()
    }

    private fun openedStore(): LocalStore = LocalStore.openInMemory(context()).also { stores += it }

    private fun context(): Context = ApplicationProvider.getApplicationContext()

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}

private class GatedPersistence(
    private val delegate: V2Persistence,
    val entered: CountDownLatch = CountDownLatch(1),
    val release: CountDownLatch = CountDownLatch(1),
) : V2Persistence by delegate {
    var gateAttempts: Boolean = false
    var gateRefresh: Boolean = false
    var gateTutor: Boolean = false
    var failTutor: Boolean = false
    var failRefresh: Boolean = false
    var armStaleRefresh: Boolean = false
    var separateTutorGate: Pair<CountDownLatch, CountDownLatch>? = null
    val tutorCalls = AtomicInteger()
    val completeCalls = AtomicInteger()
    val refreshCalls = AtomicInteger()
    val refreshInFlight = AtomicInteger()

    override suspend fun learner(): V2LearnerView {
        refreshInFlight.incrementAndGet()
        try {
            if (armStaleRefresh) {
                armStaleRefresh = false
                val stale = delegate.learner()
                entered.countDown()
                withContext(Dispatchers.IO) { release.await(10, TimeUnit.SECONDS) }
                refreshCalls.incrementAndGet()
                return stale
            }
            if (failRefresh) error("refresh failed")
            if (gateRefresh) {
                entered.countDown()
                withContext(Dispatchers.IO) { release.await(10, TimeUnit.SECONDS) }
            }
            refreshCalls.incrementAndGet()
            return delegate.learner()
        } finally {
            refreshInFlight.decrementAndGet()
        }
    }

    override suspend fun completeStep(stepId: String, recordedAt: String, contentDigest: String) {
        completeCalls.incrementAndGet()
        delegate.completeStep(stepId, recordedAt, contentDigest)
    }

    override suspend fun recordAttempt(write: dev.russiancompanion.v2.curriculum.GovernedAttemptWrite) {
        if (gateAttempts) {
            entered.countDown()
            withContext(Dispatchers.IO) { release.await(10, TimeUnit.SECONDS) }
        }
        delegate.recordAttempt(write)
    }

    override suspend fun addTutor(targetId: String, note: String, recordedAt: String, targetDigest: String) {
        tutorCalls.incrementAndGet()
        if (failTutor) error("tutor rejected")
        if (gateTutor) {
            val (tutorEntered, tutorRelease) = separateTutorGate ?: (entered to release)
            tutorEntered.countDown()
            withContext(Dispatchers.IO) { tutorRelease.await(10, TimeUnit.SECONDS) }
        }
        delegate.addTutor(targetId, note, recordedAt, targetDigest)
    }
}

