package dev.russiancompanion.v2

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.RussianCompanionApplication
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.presentation.MainActivity
import dev.russiancompanion.v2.curriculum.V2CheckItem
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.PRESCRIBED_CHECK
import dev.russiancompanion.v2.curriculum.governedSelectionKey
import dev.russiancompanion.v2.curriculum.governedSessionSelections
import dev.russiancompanion.v2.curriculum.item
import dev.russiancompanion.v2.curriculum.prepareGovernedAttempt
import dev.russiancompanion.v2.curriculum.session
import dev.russiancompanion.v2.curriculum.step
import dev.russiancompanion.v2.curriculum.verifiedResume
import dev.russiancompanion.v2.ui.V2UiWork
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Host-activity coverage for learner-load recovery. Robolectric launches
 * [MainActivity] and uses its Retry control. This is not process death and
 * not physical-device acceptance.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], qualifiers = "w411dp-h914dp-xxhdpi")
class LearnerReloadRecoveryTest {
    init {
        V2UiWork.blockForTests = false
        LocalStore.learnerReadFailuresRemainingForTests.set(2)
        LocalStore.holdNextLearnerRefreshForTests = null
        LocalStore.learnerRefreshEnteredForTests = null
        LocalStore.failHeldLearnerRefreshForTests = false
    }

    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @After
    fun reset() {
        V2UiWork.blockForTests = null
        LocalStore.learnerReadFailuresRemainingForTests.set(0)
        LocalStore.holdNextLearnerRefreshForTests = null
        LocalStore.learnerRefreshEnteredForTests = null
        LocalStore.failHeldLearnerRefreshForTests = false
    }

    @Test
    fun failedLearnerReadRetriesThenPersistsAnAnswerOnTheReopenedStore() {
        val pack = activityPack()
        awaitText(compose, "Saved progress could not be read.")
        assertEquals(1, LocalStore.learnerReadFailuresRemainingForTests.get())
        val closed = app().localStore!!
        val located = pack.step("u1-st-confusable-rest")!!
        val index = located.first.steps.indexOfFirst { it.id == "u1-st-confusable-rest" }
        val kept = pack.item(located.second.itemIds[0])!!
        val added = pack.item(located.second.itemIds[1])!!
        closed.v2SaveProfile(
            closed.v2Profile().copy(
                onboardingCompleted = true,
                currentUnitNumber = 1,
                currentSessionId = located.first.id,
                currentStepIndex = index,
            ).verifiedResume(pack),
        )
        closed.v2RecordGovernedAttempt(
            prepareGovernedAttempt(pack, kept.id, kept.answer, PRESCRIBED_CHECK, located.second.id, "2026-10-03T08:00")!!,
        )
        compose.onNodeWithTag("v2-retry").performClick()
        val failedRetry = awaitReplacedStore(compose, closed)
        awaitCounter(compose, 0)
        awaitText(compose, "Saved progress could not be read.")
        assertFalse(failedRetry === closed)
        compose.onNodeWithTag("v2-retry").performClick()
        awaitTag(compose, "v2-continue")
        val recovered = app().localStore!!
        assertFalse(recovered === failedRetry)
        assertEquals(
            kept.answer,
            governedSessionSelections(pack, recovered.v2LearnerView())[governedSelectionKey(located.second.id, kept.id)],
        )
        compose.onNodeWithTag("v2-continue").performClick()
        awaitText(compose, located.second.title)
        choose(compose, added, wrong = true)
        awaitText(compose, "Saved")
        assertSame(recovered, app().localStore)
        val saved = governedSessionSelections(pack, recovered.v2LearnerView())
        assertEquals(kept.answer, saved[governedSelectionKey(located.second.id, kept.id)])
        assertEquals(wrongOption(added), saved[governedSelectionKey(located.second.id, added.id)])
        assertEquals(1, recovered.v2Attempts().count { it.itemId == kept.id })
        assertEquals(1, recovered.v2Attempts().count { it.itemId == added.id })
        compose.onNodeWithTag("v2-option-${added.id}-${wrongOption(added)}").performScrollTo().assertIsSelected()
    }
}

/**
 * Recreates the host [MainActivity] through ActivityScenario. Robolectric
 * retains the activity's ViewModel store. This is not process death and not
 * a physical reboot.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], qualifiers = "w411dp-h914dp-xxhdpi")
class HostActivityRecreationTest {
    private val entered = CountDownLatch(1)
    private val release = CountDownLatch(1)
    private var extraRelease: CountDownLatch? = null

    init {
        V2UiWork.blockForTests = false
        LocalStore.learnerReadFailuresRemainingForTests.set(0)
        LocalStore.failHeldLearnerRefreshForTests = false
        LocalStore.learnerRefreshEnteredForTests = entered
        LocalStore.holdNextLearnerRefreshForTests = release
    }

    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @After
    fun reset() {
        LocalStore.failHeldLearnerRefreshForTests = false
        LocalStore.learnerReadFailuresRemainingForTests.set(0)
        extraRelease?.countDown()
        release.countDown()
        LocalStore.holdNextLearnerRefreshForTests = null
        LocalStore.learnerRefreshEnteredForTests = null
        V2UiWork.blockForTests = null
    }

    @Test
    fun retryDropsAFailedReadFromTheReplacedStore() {
        awaitLatch(compose, entered)
        val pack = activityPack()
        val original = app().localStore!!
        val located = pack.step("u1-st-confusable-rest")!!
        val index = located.first.steps.indexOfFirst { it.id == "u1-st-confusable-rest" }
        val kept = pack.item(located.second.itemIds[0])!!
        val added = pack.item(located.second.itemIds[1])!!
        original.v2SaveProfile(
            original.v2Profile().copy(
                onboardingCompleted = true,
                currentUnitNumber = 1,
                currentSessionId = located.first.id,
                currentStepIndex = index,
            ).verifiedResume(pack),
        )
        original.v2RecordGovernedAttempt(
            prepareGovernedAttempt(pack, kept.id, kept.answer, PRESCRIBED_CHECK, located.second.id, "2026-10-03T08:01")!!,
        )
        LocalStore.failHeldLearnerRefreshForTests = true
        awaitTag(compose, "v2-retry")
        compose.onNodeWithTag("v2-retry").performClick()
        awaitTag(compose, "v2-continue")
        val recovered = app().localStore!!
        assertFalse(recovered === original)
        release.countDown()
        settle(compose)
        compose.onNodeWithTag("v2-continue").assertIsDisplayed()
        compose.onNodeWithText("Saved progress could not be read.").assertDoesNotExist()
        assertEquals(
            kept.answer,
            governedSessionSelections(pack, recovered.v2LearnerView())[governedSelectionKey(located.second.id, kept.id)],
        )
        compose.onNodeWithTag("v2-continue").performClick()
        awaitText(compose, located.second.title)
        choose(compose, added, wrong = true)
        awaitText(compose, "Saved")
        assertSame(recovered, app().localStore)
        assertEquals(
            wrongOption(added),
            governedSessionSelections(pack, recovered.v2LearnerView())[governedSelectionKey(located.second.id, added.id)],
        )
        assertEquals(1, recovered.v2Attempts().count { it.itemId == added.id })
    }

    @Test
    fun recreatedActivityKeepsTheDraftAnswerAndResumePosition() {
        val pack = activityPack()
        val located = pack.step("u1-st-confusable-rest")!!
        val index = located.first.steps.indexOfFirst { it.id == "u1-st-confusable-rest" }
        openSeededStep(located.second.id, index, located.first.id)
        val item = pack.item(located.second.itemIds[0])!!
        compose.onNodeWithTag("v2-note-draft").performScrollTo().performTextInput("lifecycle draft")
        choose(compose, item, wrong = true)
        awaitText(compose, "Saved")
        val before = app().localStore!!.v2Attempts().count { it.itemId == item.id }
        compose.activityRule.scenario.recreate()
        awaitRestoredStep(compose, located.second.title)
        compose.onNodeWithText("lifecycle draft").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("v2-option-${item.id}-${wrongOption(item)}").performScrollTo().assertIsSelected()
        val store = app().localStore!!
        assertEquals(located.first.id, store.v2Profile().currentSessionId)
        assertEquals(index, store.v2Profile().currentStepIndex)
        assertEquals(before, store.v2Attempts().count { it.itemId == item.id })
    }

    @Test
    fun recreatedActivityDuringADelayedWriteKeepsOneOutcome() {
        val pack = activityPack()
        val session = pack.session("u1-s-orient")!!
        val first = session.steps[0]
        val second = session.steps[1]
        openSeededStep(first.id, 0, session.id)
        compose.onNodeWithTag("v2-note-draft").performScrollTo().performTextInput("pending draft")
        awaitNotSaving(compose)
        val stepEntered = CountDownLatch(1)
        val stepRelease = CountDownLatch(1)
        extraRelease = stepRelease
        LocalStore.learnerRefreshEnteredForTests = stepEntered
        LocalStore.holdNextLearnerRefreshForTests = stepRelease
        val writesBefore = LocalStore.asyncStepWritesForTests.get()
        compose.onNodeWithTag("v2-session-continue").performClick()
        awaitLatch(compose, stepEntered)
        assertEquals(writesBefore + 1, LocalStore.asyncStepWritesForTests.get())
        compose.activityRule.scenario.recreate()
        stepRelease.countDown()
        awaitText(compose, "Saved")
        settle(compose)
        val store = app().localStore!!
        assertEquals(writesBefore + 1, LocalStore.asyncStepWritesForTests.get())
        assertEquals(listOf(first.id), store.v2StepProgress().map { it.stepId })
        assertFalse(second.id in store.v2LearnerView().completedStepIds)
        awaitRestoredStep(compose, first.title)
        compose.onNodeWithText("pending draft").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText(second.title).assertDoesNotExist()
    }

    private fun openSeededStep(stepId: String, index: Int, sessionId: String) {
        awaitLatch(compose, entered)
        val store = app().localStore!!
        val pack = activityPack()
        store.v2SaveProfile(
            store.v2Profile().copy(
                onboardingCompleted = true,
                currentUnitNumber = 1,
                currentSessionId = sessionId,
                currentStepIndex = index,
            ).verifiedResume(pack),
        )
        release.countDown()
        awaitTag(compose, "v2-continue")
        compose.onNodeWithTag("v2-continue").performClick()
        awaitTag(compose, "v2-step-title")
        val title = activityPack().step(stepId)!!.second.title
        compose.onNodeWithText(title).assertIsDisplayed()
        awaitNotSaving(compose)
    }
}

private fun app(): RussianCompanionApplication = ApplicationProvider.getApplicationContext()

private fun activityPack(): V2CurriculumPack {
    val root = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
    return V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText())
}

private fun choose(compose: ComposeContentTestRule, item: V2CheckItem, wrong: Boolean) {
    val option = if (wrong) wrongOption(item) else item.answer
    compose.onNodeWithTag("v2-option-${item.id}-$option").performScrollTo().performClick()
}

private fun wrongOption(item: V2CheckItem): String = item.options.first { it != item.answer }

private fun awaitLatch(compose: ComposeContentTestRule, latch: CountDownLatch, timeoutMs: Long = 20_000) {
    val start = System.currentTimeMillis()
    while (latch.count > 0 && System.currentTimeMillis() - start < timeoutMs) {
        compose.waitForIdle()
        if (latch.count == 0L) return
        Thread.sleep(20)
    }
    assertEquals("learner refresh was not held", 0L, latch.count)
}

private fun awaitRestoredStep(compose: ComposeContentTestRule, title: String) {
    val start = System.currentTimeMillis()
    var continued = false
    while (System.currentTimeMillis() - start < 20_000) {
        compose.waitForIdle()
        if (compose.onAllNodesWithText(title).fetchSemanticsNodes().isNotEmpty()) return
        val loading = compose.onAllNodesWithText("Loading", substring = true).fetchSemanticsNodes().isNotEmpty()
        if (!loading && !continued && compose.onAllNodesWithTag("v2-continue").fetchSemanticsNodes().isNotEmpty()) {
            compose.onNodeWithTag("v2-continue").performClick()
            continued = true
        }
        Thread.sleep(30)
    }
    error("Restored activity did not show '$title'")
}

private fun awaitText(compose: ComposeContentTestRule, text: String, timeoutMs: Long = 20_000) {
    val start = System.currentTimeMillis()
    while (System.currentTimeMillis() - start < timeoutMs) {
        compose.waitForIdle()
        if (compose.onAllNodesWithText(text, substring = true).fetchSemanticsNodes().isNotEmpty()) return
        Thread.sleep(20)
    }
    compose.onNodeWithText(text, substring = true).assertIsDisplayed()
}

private fun awaitTag(compose: ComposeContentTestRule, tag: String, timeoutMs: Long = 20_000) {
    val start = System.currentTimeMillis()
    while (System.currentTimeMillis() - start < timeoutMs) {
        compose.waitForIdle()
        try {
            compose.onNodeWithTag(tag).assertExists()
            return
        } catch (_: AssertionError) {
            Thread.sleep(20)
        } catch (_: IllegalStateException) {
            Thread.sleep(20)
        }
    }
    compose.onNodeWithTag(tag).assertExists()
}

private fun awaitNotSaving(compose: ComposeContentTestRule) {
    val start = System.currentTimeMillis()
    var clear = 0
    while (System.currentTimeMillis() - start < 8_000) {
        compose.waitForIdle()
        val saving = compose.onAllNodesWithText("Saving").fetchSemanticsNodes().size
        if (saving == 0) {
            clear += 1
            if (clear >= 3) return
        } else {
            clear = 0
        }
        Thread.sleep(30)
    }
}

private fun awaitReplacedStore(compose: ComposeContentTestRule, previous: LocalStore): LocalStore {
    val start = System.currentTimeMillis()
    while (System.currentTimeMillis() - start < 20_000) {
        compose.waitForIdle()
        val current = app().localStore
        if (current != null && current !== previous) return current
        Thread.sleep(20)
    }
    error("Retry did not reopen local storage")
}

private fun awaitCounter(compose: ComposeContentTestRule, expected: Int) {
    val start = System.currentTimeMillis()
    while (System.currentTimeMillis() - start < 20_000) {
        compose.waitForIdle()
        if (LocalStore.learnerReadFailuresRemainingForTests.get() == expected) return
        Thread.sleep(20)
    }
    assertEquals(expected, LocalStore.learnerReadFailuresRemainingForTests.get())
}

private fun settle(compose: ComposeContentTestRule) {
    val start = System.currentTimeMillis()
    while (System.currentTimeMillis() - start < 600) {
        compose.waitForIdle()
        Thread.sleep(30)
    }
}
