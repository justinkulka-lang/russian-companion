package dev.russiancompanion.v2

import android.content.Context
import android.os.Looper
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.V2CheckItem
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.continueTarget
import dev.russiancompanion.v2.curriculum.example
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
        compose.onNodeWithTag("v2-return-to-check").performClick()
        compose.onNodeWithText(step.title).assertIsDisplayed()
        compose.onNodeWithTag("v2-item-feedback-${item.id}").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("v2-option-${item.id}-${wrongOption(item)}").assertIsSelected()
        assertEquals(wrongOption(item), store.v2LearnerView().attemptSelections[item.id])
        assertEquals(step.id, store.v2Profile().currentSessionId?.let { pack.session(it)!!.steps[store.v2Profile().currentStepIndex].id })
    }

    @Test
    fun todayOpensAdvancementReviewInsteadOfTheLastLessonStep() {
        val pack = pack()
        val store = openedStore()
        val unit = pack.unit(1)!!
        unit.sessions.flatMap { it.steps }.forEach { step ->
            store.v2CompleteStep(step.id, "2026-10-02T12:00")
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
        val pack = pack()
        val store = openedStore()
        val unit = pack.unit(1)!!
        val iotated = pack.step("u1-st-iotated")!!.second
        val check = pack.session("u1-s-iotated")!!.steps.last()
        unit.sessions.flatMap { it.steps }
            .filter { it.id != iotated.id && it.id != check.id }
            .forEach { store.v2CompleteStep(it.id, "2026-10-02T12:00") }
        store.v2SaveProfile(
            store.v2Profile().copy(
                onboardingCompleted = true,
                currentUnitNumber = 1,
                currentSessionId = "u1-s-iotated",
                currentStepIndex = 0,
            ),
        )
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        compose.onNodeWithTag("v2-continue").performClick()
        iotated.exampleIds.forEach { id ->
            compose.onNodeWithTag("v2-example-$id").performScrollTo().assertIsDisplayed()
        }
        compose.onNodeWithTag("v2-session-continue").performClick()
        val item = pack.item(check.itemIds.first())!!
        choose(item, wrong = true)
        compose.onNodeWithTag("v2-remediation-${item.id}").performScrollTo().performClick()
        compose.onNodeWithText(iotated.title).assertIsDisplayed()
        iotated.exampleIds.forEach { id ->
            compose.onNodeWithTag("v2-example-$id").performScrollTo().assertIsDisplayed()
        }
        compose.onNodeWithTag("v2-return-to-check").performClick()
        compose.onNodeWithTag("v2-item-feedback-${item.id}").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("v2-note-draft").performScrollTo().performTextInput("letter draft")
        compose.onNodeWithTag("v2-back").performClick()
        compose.onNodeWithTag("v2-continue").performClick()
        compose.onNodeWithText("letter draft").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("v2-option-${item.id}-${wrongOption(item)}").performScrollTo().assertIsSelected()
        compose.onNodeWithTag("v2-session-continue").performClick()
        compose.onNodeWithText("Review advancement").assertIsDisplayed()
        compose.onNodeWithTag("v2-continue").performClick()
        compose.onNodeWithTag("v2-advancement-rule").performScrollTo().assertIsDisplayed()
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
        assertTrue(entered.await(5, TimeUnit.SECONDS))
        awaitText("Saving")
        compose.onNodeWithTag("v2-note-draft").performScrollTo().performTextInput("still typing")
        compose.onNodeWithText("still typing").assertIsDisplayed()
        assertDoesNotExist("v2-item-feedback-${step.itemIds[1]}")
        release.countDown()
        awaitText("Saved")
        assertEquals(1, store.v2LearnerView().attemptSelections.count { it.key == item.id })
        assertEquals(wrongOption(item), store.v2LearnerView().attemptSelections[item.id])
    }

    @Test
    fun failedWritePreservesPriorDataAndShowsOneFailure() {
        val pack = pack()
        val store = openedStore()
        val step = pack.step("u1-st-confusable-rest")!!.second
        val kept = pack.item(step.itemIds[0])!!
        val failed = pack.item(step.itemIds[1])!!
        store.v2RecordAttempt(kept.id, kept.answer, true, "2026-10-02T12:00")
        val persistence = object : V2Persistence by StoreV2Persistence(store) {
            override suspend fun recordAttempt(itemId: String, selected: String, correct: Boolean, recordedAt: String) {
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
        assertEquals(kept.answer, saved.attemptSelections[kept.id])
        assertFalse(saved.attemptSelections.containsKey(failed.id))
        assertTrue(kept.id in saved.correctItemIds)
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
    ) {
        val located = pack.step(stepId)!!
        val index = located.first.steps.indexOfFirst { it.id == stepId }
        store.v2SaveProfile(
            store.v2Profile().copy(
                onboardingCompleted = true,
                currentUnitNumber = unitNumber,
                currentSessionId = located.first.id,
                currentStepIndex = index,
            ),
        )
        compose.setContent {
            V2App(
                store = store,
                pack = pack,
                onRetryOpen = {},
                persistence = persistence ?: StoreV2Persistence(store),
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

    private fun wrongOption(item: V2CheckItem): String = item.options.first { it != item.answer }

    private fun assertDoesNotExist(tag: String) {
        compose.onNodeWithTag(tag).assertDoesNotExist()
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
    private val entered: CountDownLatch,
    private val release: CountDownLatch,
) : V2Persistence by delegate {
    var gateAttempts: Boolean = false

    override suspend fun recordAttempt(itemId: String, selected: String, correct: Boolean, recordedAt: String) {
        if (gateAttempts) {
            entered.countDown()
            withContext(Dispatchers.IO) {
                release.await(10, TimeUnit.SECONDS)
            }
        }
        delegate.recordAttempt(itemId, selected, correct, recordedAt)
    }
}
