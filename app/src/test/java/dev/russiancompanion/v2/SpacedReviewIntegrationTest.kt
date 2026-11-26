package dev.russiancompanion.v2

import android.content.Context
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
import java.io.ByteArrayOutputStream
import java.io.File
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class SpacedReviewIntegrationTest {
    @get:Rule val compose = createComposeRule()
    private val context = ApplicationProvider.getApplicationContext<Context>()
    @Before fun closeStartup() { (context.applicationContext as RussianCompanionApplication).closeStartupStoreForTests() }
    private val typedId get() = cumulativeReviewPolicy(3)!!.pool.first().candidateItemIds.first()
    private val choiceId get() = cumulativeReviewPolicy(3)!!.pool[1].candidateItemIds.first()
    private fun pack(version: Int = 1): V2CurriculumPack {
        val key = V2TypedAnswerKey("english-spaced-key", version, listOf("Hello", "Hi"))
        val reviewed = key.copy(review = V2TypedKeyReview("English fixture reviewer", RESEARCH_VERIFIED, typedAnswerKeyDigest(key)))
        return OptionPresentationFixture.pack.let { p -> p.copy(items = p.items.map { item -> when (item.id) {
            typedId -> item.copy(kind = "TYPED_FORM", language = "en", prompt = "Type the taught response.", promptLetter = null,
                options = emptyList(), answer = "Hello", distractorRationales = emptyList(), feedback = "Compare the taught response.",
                assessment = null, typedAnswerKey = reviewed)
            choiceId -> item.copy(kind = "FORM_SELECTION", language = "en", prompt = "Choose the matching form.", promptLetter = null,
                options = listOf("Right", "Wrong"), answer = "Right", distractorRationales = listOf("", "This form uses the other pattern."),
                feedback = "Compare the pattern.", assessment = null)
            else -> item
        } }) }
    }
    private fun step(p: V2CurriculumPack, id: String) = p.units.flatMap { it.sessions }.flatMap { it.steps }
        .first { it.kind == "CHECK" && id in it.itemIds }.id
    private fun write(p: V2CurriculumPack, value: String, id: String = typedId, spaced: Boolean = false, at: String = "now") =
        prepareGovernedAttempt(p, id, value, if (spaced) OPTIONAL_PRACTICE else PRESCRIBED_CHECK,
            if (spaced) practiceStepId("spaced") else step(p, id), at)!!

    @Test fun acceptedProductionWritesScheduleOnceAndSameCorrectAnswerCanReturnAtItsDueTime() {
        val p = pack()
        LocalStore.openInMemory(context).use { s ->
            val api = StoreV2Persistence(s, p)
            s.spacedReviewTimeForTests = 100_000
            val initial = write(p, "Hi")
            runBlocking { api.recordAttempt(initial); api.recordAttempt(initial) }
            assertEquals(1, s.v2Attempts().size)
            val first = s.v2LearnerView().spacedReviews.single()
            assertEquals(1, first.intervalDays)
            assertEquals("attempt:${s.v2Attempts().single().id}", first.lastEventId)
            s.spacedReviewTimeForTests = first.dueAtEpochMillis
            val due = write(p, "Hi", spaced = true, at = "due")
            runBlocking { api.recordAttempt(due); api.recordAttempt(due) }
            val second = s.v2LearnerView().spacedReviews.single()
            assertEquals(2, s.v2Attempts().size)
            assertEquals(2, second.intervalDays)
            assertEquals(2L, second.acceptedOrder)
            assertEquals(first.dueAtEpochMillis + 2 * SPACED_REVIEW_DAY_MILLIS, second.dueAtEpochMillis)
            s.spacedReviewTimeForTests = second.dueAtEpochMillis
            val sameAnswerNextTime = write(p, "Hi", spaced = true, at = "second-due")
            runBlocking { api.recordAttempt(sameAnswerNextTime); api.recordAttempt(sameAnswerNextTime) }
            assertEquals(3, s.v2Attempts().size)
            assertEquals(4, s.v2LearnerView().spacedReviews.single().intervalDays)
        }
    }

    @Test fun failedScheduleOrStaleEpochRollsBackTheAnswerAndQueueTogether() {
        val p = pack()
        LocalStore.openInMemory(context).use { s ->
            s.spacedReviewTimeForTests = 100_000
            val before = s.governedLearnerFingerprint()
            val w = write(p, "Wrong")
            try { s.v2RecordGovernedAttempt(w.copy(correct = true), p); fail("Caller must not forge correctness") }
            catch (_: IllegalArgumentException) { }
            assertEquals(before, s.governedLearnerFingerprint())
            s.failSpacedReviewForTests = true
            try { s.v2RecordGovernedAttempt(w, p); fail("Expected atomic failure") } catch (_: IllegalStateException) { }
            finally { s.failSpacedReviewForTests = false }
            assertEquals(before, s.governedLearnerFingerprint())
            runBlocking { assertFalse(s.v2RecordGovernedAttemptIfCurrentAsync(s.learnerMutationEpoch() - 1, w, p)) }
            assertEquals(before, s.governedLearnerFingerprint())
            s.v2RecordGovernedAttempt(w, p)
            assertEquals(1, s.v2Attempts().size)
            assertEquals(0, s.v2LearnerView().spacedReviews.single().consecutiveCorrect)
        }
    }

    @Test fun currentQueueSurvivesNamedReopenBackupReplacementAndKeyInvalidation() {
        val p = pack(); val name = "spaced-${System.nanoTime()}.db"
        val temp = File(context.cacheDir, "spaced-restore-${System.nanoTime()}")
        try {
            val bytes = LocalStore.openNamed(context, name).use { s ->
                s.spacedReviewTimeForTests = 100_000
                s.v2RecordGovernedAttempt(write(p, "Wrong"), p)
                ByteArrayOutputStream().also { writeStoreBackup(s, it, "dev.russiancompanion", p.packId, p.packVersion, "DARK") }.toByteArray()
            }
            LocalStore.openNamed(context, name).use { s ->
                val old = s.v2LearnerView().spacedReviews.single()
                assertEquals(100_000 + SPACED_REVIEW_MISS_DELAY_MILLIS, old.dueAtEpochMillis)
                assertTrue(spacedReviewRecordCurrent(p, old))
                assertFalse(spacedReviewRecordCurrent(pack(2), old))
                assertTrue(practiceQueue(pack(2), s.v2LearnerView(), "spaced").isEmpty())
                val before = s.governedLearnerFingerprint()
                try { s.v2RecordGovernedAttempt(write(p, "Wrong"), pack(2)); fail("Stale duplicate must not be acknowledged") }
                catch (_: IllegalArgumentException) { }
                assertEquals(before, s.governedLearnerFingerprint())
                s.spacedReviewTimeForTests = old.dueAtEpochMillis
                s.v2RecordGovernedAttempt(write(pack(2), "Hi", at = "new-key"), pack(2))
                assertEquals(1, s.v2LearnerView().spacedReviews.single().intervalDays)
            }
            val read = readGovernedBackup(bytes.inputStream(), "dev.russiancompanion", p.packId, p.packVersion, temp)
            assertTrue(read is BackupReadResult.Valid)
            LocalStore.openInMemory(context).use { restored ->
                restoreStoreBackup(restored, (read as BackupReadResult.Valid).backup, temp)
                assertEquals(1, restored.v2LearnerView().spacedReviews.size)
                assertEquals(0, restored.v2LearnerView().spacedReviews.single().consecutiveCorrect)
                assertNull(spacedReviewBackupDefects(restored.governedLearnerDocument()))
            }
        } finally { context.deleteDatabase(name); temp.deleteRecursively() }
    }

    @Test fun malformedOrUnlinkedQueueCannotReplaceLearnerState() {
        val p = pack()
        LocalStore.openInMemory(context).use { s ->
            s.v2RecordGovernedAttempt(write(p, "Wrong"), p)
            val document = s.governedLearnerDocument(); val record = document.spacedReviews.single()
            val before = s.governedLearnerFingerprint()
            for (invalid in listOf(document.copy(spacedReviews = listOf(record, record)),
                document.copy(spacedReviews = listOf(record.copy(lastEventId = "attempt:missing"))),
                document.copy(spacedReviews = listOf(record.copy(unitNumber = 30))),
                document.copy(spacedReviews = listOf(record.copy(dueAtEpochMillis = Long.MAX_VALUE))))) {
                assertNotNull(spacedReviewBackupDefects(invalid))
                assertEquals(GovernedReplaceOutcome.UNCHANGED, s.replaceGovernedLearner(invalid, emptyList()))
                assertEquals(before, s.governedLearnerFingerprint())
            }
            val temp = File(context.cacheDir, "spaced-invalid-${System.nanoTime()}")
            try {
                val bytes = ByteArrayOutputStream().also { out -> writeGovernedBackup(out, "dev.russiancompanion", p.packId,
                    p.packVersion, "DARK", document.copy(spacedReviews = listOf(record.copy(lastEventId = "attempt:missing"))), emptyList()) }.toByteArray()
                assertEquals(BackupReadResult.Rejected(BackupRejection.MALFORMED),
                    readGovernedBackup(bytes.inputStream(), "dev.russiancompanion", p.packId, p.packVersion, temp))
                assertEquals(before, s.governedLearnerFingerprint())
            } finally { temp.deleteRecursively() }
        }
    }

    @Test fun typedDueRouteKeepsFeedbackAndRetryRestoresWithoutGrantingRequiredCredit() {
        val p = pack()
        LocalStore.openInMemory(context).use { s ->
            s.spacedReviewTimeForTests = 100_000
            s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, typedId, "Wrong", OPTIONAL_PRACTICE, practiceStepId("grammar"), "seed")!!, p)
            val learner = mutableStateOf(s.v2LearnerView())
            val restoration = StateRestorationTester(compose)
            restoration.setContent { V2LabScreen(p, learner.value, "spaced", emptySet(), {}, { id, value ->
                s.v2RecordGovernedAttempt(write(p, value, id, spaced = true, at = "ui-${s.v2Attempts().size}"), p)
                learner.value = s.v2LearnerView(); true
            }) }
            val input = compose.onNodeWithTag("v2-lab-typed-$typedId-input")
            input.performScrollTo().performTextInput("Unknown")
            compose.onNodeWithTag("v2-lab-typed-$typedId-submit").performScrollTo().performClick()
            compose.onNodeWithTag("v2-lab-typed-$typedId-reveal").performScrollTo().performClick()
            compose.onNodeWithTag("v2-lab-typed-$typedId-answer").assertTextEquals("Answer: Hello")
            restoration.emulateSavedInstanceStateRestore()
            compose.onNodeWithTag("v2-lab-typed-$typedId-retry").performScrollTo().performClick()
            input.performScrollTo().performTextInput("Hi")
            input.performImeAction()
            compose.onNodeWithTag("v2-lab-feedback").assertTextContains("Recorded as correct", substring = true)
            assertFalse(typedId in creditItemIds(p, s.v2LearnerView()))
            assertFalse(typedId in firstAttemptCorrectItemIds(p, s.v2LearnerView()))
            assertEquals(0, s.v2LearnerView().spacedReviews.single().consecutiveCorrect)
            assertTrue(s.v2LearnerView().reviewAssignments.isEmpty())
            val previousEpoch = s.v2LearnerView().storageEpoch
            val restoredDocument = s.governedLearnerDocument()
            s.openLearnerReplacement()
            try { assertEquals(GovernedReplaceOutcome.REPLACED, s.replaceGovernedLearner(restoredDocument, emptyList())) }
            finally { s.closeLearnerReplacement() }
            compose.runOnIdle { learner.value = s.v2LearnerView() }
            assertTrue(s.v2LearnerView().storageEpoch > previousEpoch)
            input.assertTextEquals("Your answer", "")
            compose.onNodeWithTag("v2-lab-feedback").assertDoesNotExist()
        }
    }

    @Test fun optionDueRouteRequiresANewSubmissionAndShowsItsOwnRationale() {
        val p = pack()
        LocalStore.openInMemory(context).use { s ->
            s.spacedReviewTimeForTests = 100_000
            s.v2RecordGovernedAttempt(write(p, "Right", choiceId), p)
            val learner = mutableStateOf(s.v2LearnerView())
            compose.setContent { V2LabScreen(p, learner.value, "spaced", emptySet(), {}, { id, value ->
                s.v2RecordGovernedAttempt(write(p, value, id, spaced = true, at = "option"), p)
                learner.value = s.v2LearnerView(); true
            }) }
            compose.onNodeWithTag("v2-lab-option-Wrong").performScrollTo().performClick()
            compose.onNodeWithTag("v2-lab-submit").performClick()
            compose.onNodeWithTag("v2-lab-rationale").assertTextEquals("This form uses the other pattern.")
            assertEquals(0, s.v2LearnerView().spacedReviews.single().consecutiveCorrect)
            assertTrue(s.v2LearnerView().reviewAnswers.isEmpty())
        }
    }

    @Test fun cumulativeAnswerAndScheduleCommitOrRollBackTogetherAndGateStaysRequired() {
        val p = pack()
        LocalStore.openInMemory(context).use { s ->
            ready(p, s)
            val assignment = s.openCumulativeReview(p, 3, s.learnerMutationEpoch())!!
            val item = p.item(assignment.slots.first().itemId)!!
            val before = s.governedLearnerFingerprint()
            s.failSpacedReviewForTests = true
            try { s.answerCumulativeReview(p, assignment.id, 0, item.answer, s.learnerMutationEpoch()); fail("Expected rollback") }
            catch (_: IllegalStateException) { } finally { s.failSpacedReviewForTests = false }
            assertEquals(before, s.governedLearnerFingerprint())
            assertTrue(s.answerCumulativeReview(p, assignment.id, 0, item.answer, s.learnerMutationEpoch()))
            val record = s.v2LearnerView().spacedReviews.single()
            assertTrue(record.lastEventId.startsWith("review:"))
            assertNull(spacedReviewBackupDefects(s.governedLearnerDocument()))
            assertFalse(cumulativeReviewComplete(p, s.v2LearnerView(), 3))
        }
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
}
