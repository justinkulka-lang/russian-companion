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
import kotlinx.serialization.json.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit3GenderTeachingTest {
    @get:Rule val compose = createComposeRule()

    @Before fun finishApplicationStartupBeforeOwningTheRecordingRoot() {
        (context().applicationContext as dev.russiancompanion.RussianCompanionApplication).closeStartupStoreForTests()
    }

    @Test fun allFiveIndependentRecordsHaveExplicitTeachingAndCompleteRepair() {
        val p = current; val unit = p.unit(3)!!; val objective = unit.objectives.single { it.id == "obj-u3-gender" }
        val teaching = objective.instructionStepIds.mapNotNull { p.step(it)?.second }.filter { it.kind == "TEACH" }
        val repair = p.step("u3-st-rem-gender")!!.second
        assertEquals(5, teaching.size)
        assertEquals(teaching.joinToString("\n\n") { it.body }, repair.body)
        assertEquals(teaching.flatMap { it.exampleIds }.distinct(), repair.exampleIds)
        objective.checkItemIds.forEach { id ->
            val item = p.item(id)!!
            assertTrue(id, teaching.any { step -> step.exampleIds.any { p.example(it)!!.display == item.promptLetter } })
            assertTrue(id, repair.exampleIds.any { p.example(it)!!.display == item.promptLetter })
        }
        assertTrue(p.step("u3-st-teach-gender-2")!!.second.body.contains("друг — friend is also masculine"))
        assertTrue(p.step("u3-st-teach-gender-3")!!.second.body.contains("дверь — door is feminine"))
        assertEquals(listOf("u3-st-guided-gender-2", "u3-st-guided-gender-3", "u3-st-guided-gender-4", "u3-st-guided-gender-5"), reviewedGuidedPractice(p, repair.id))
        val validation = V2Validator.validate(p); assertTrue(validation.errors.joinToString("\n"), validation.ok)
    }

    @Test fun correctionHasAnExactWhitelistAndNoScoredOrPolicyChanges() {
        // Historical correction whitelist stays exact to its source; later Unit9 policy is tested separately.
        val p = postTeachingCorrection; val old = previous
        assertEquals(declaredAfterUnit(), p.unit(3))
        assertEquals(old, p.copy(units = old.units, examples = p.examples.dropLast(2), provenanceRecords = p.provenanceRecords.dropLast(8)))
        assertEquals(old.units.filter { it.number != 3 }, p.units.filter { it.number != 3 })
        assertEquals(old.unit(3)!!.advancement, p.unit(3)!!.advancement)
        assertEquals(12, p.unit(3)!!.advancement!!.minimumDistinctChecks)
        assertEquals(old.items, p.items); assertEquals(old.provenanceRecords, p.provenanceRecords.dropLast(8))
        assertEquals(listOf("masculine", "feminine"), p.provenanceRecords.filter { it.itemId.startsWith("prov-u3-gender-field-") }.map { it.shippedText })
        assertEquals(listOf("u3-i-gender-город"), Unit4CumulativeReviewPolicy.definition.pool[1].candidateItemIds)
        assertEquals("u3-st-teach-gender-2", Unit4CumulativeReviewPolicy.definition.pool[1].remediationStepId)
    }

    @Test fun genuineBackupsRetainRawHistoryButChangedTeachingRejectsOldCreditAfterReopen() {
        val p = current; val old = previous
        val labels = listOf("BEFORE_UNIT3_WORK", "ORDINARY_UNIT3", "UNSTARTED_REVIEW", "PARTIAL_REVIEW", "ANSWERED_REVIEW", "COMPLETED_UNIT3", "POST_COMPLETION_WRONG", "COMPLETED_UNIT4")
        labels.forEach { label ->
            val original = backupJson.decodeFromString(GovernedLearnerDocument.serializer(), File(evidence(), "$label.json").readText())
            val staging = File(context().cacheDir, "gender-$label-${System.nanoTime()}")
            val decoded = readGovernedBackup(File(evidence(), "$label.zip").inputStream(), "dev.russiancompanion", p.packId, p.packVersion, staging)
            assertTrue(label, decoded is BackupReadResult.Valid)
            assertEquals(original, (decoded as BackupReadResult.Valid).backup.document)
            val name = "gender-$label-${System.nanoTime()}.db"
            fun check(store: LocalStore) {
                assertEquals(original.reviewAssignments to original.reviewAnswers, store.cumulativeReviewHistory())
                assertEquals(original.unitCompletions, store.governedLearnerDocument().unitCompletions)
                assertEquals(original.attempts, store.governedLearnerDocument().attempts)
                assertEquals(setOf(1, 2), currentCompletedUnitNumbers(p, store.v2LearnerView()))
                assertFalse(ordinaryUnitRequirements(p, p.unit(3)!!, store.v2LearnerView()).allowed)
                assertEquals(original.reviewAssignments.singleOrNull { it.targetUnit == 3 }, currentCumulativeAssignment(p, store.v2LearnerView(), 3))
                assertNull(currentCumulativeAssignment(p, store.v2LearnerView(), 4))
                assertFalse(advancementDecision(p, p.unit(3)!!, store.v2LearnerView()).allowed)
                if (label == "COMPLETED_UNIT4") assertEquals(setOf(1, 2, 3, 4), currentCompletedUnitNumbers(old, store.v2LearnerView()))
            }
            try {
                LocalStore.openNamed(context(), name).use { store ->
                    store.openLearnerReplacement()
                    try { assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(original, emptyList())) }
                    finally { store.closeLearnerReplacement() }
                    check(store)
                }
                LocalStore.openNamed(context(), name).use { check(it) }
            } finally { staging.deleteRecursively(); context().deleteDatabase(name) }
        }
    }

    @Test fun olderPolicyHistoriesRemainRawButCannotBypassChangedUnit3Teaching() {
        for (prefix in listOf("", "predecessor-schema1/")) {
            for (label in listOf("BEFORE_UNIT6_WORK", "ORDINARY_UNIT6", "UNSTARTED_REVIEW", "PARTIAL_REVIEW", "ANSWERED_REVIEW", "COMPLETED_UNIT6", "POST_COMPLETION_WRONG", "COMPLETED_UNIT7")) {
                val file = File(root(), "audit/independent-gpt61-2026-10-12/ia003-policy-history-transition/$prefix$label.json")
                val old = backupJson.decodeFromString(GovernedLearnerDocument.serializer(), file.readText())
                LocalStore.openInMemory(context()).use { store ->
                    store.openLearnerReplacement()
                    try { assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(old, emptyList())) }
                    finally { store.closeLearnerReplacement() }
                    assertEquals(old.reviewAssignments to old.reviewAnswers, store.cumulativeReviewHistory())
                    assertEquals(old.unitCompletions, store.governedLearnerDocument().unitCompletions)
                    assertEquals(setOf(1, 2), currentCompletedUnitNumbers(current, store.v2LearnerView()))
                    assertFalse(ordinaryUnitRequirements(current, current.unit(3)!!, store.v2LearnerView()).allowed)
                    assertNull(store.openCumulativeReview(current, 6, store.learnerMutationEpoch()))
                }
            }
        }
    }

    @Test fun freshTeachingAndAnswersReestablishCompletionWithoutRelabellingOldAssignments() {
        val p = current
        val old = backupJson.decodeFromString(GovernedLearnerDocument.serializer(), File(evidence(), "COMPLETED_UNIT4.json").readText())
        LocalStore.openInMemory(context()).use { store ->
            store.openLearnerReplacement()
            try { assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(old, emptyList())) }
            finally { store.closeLearnerReplacement() }
            for (number in 3..4) {
                ordinary(p, store, number)
                assertTrue(ordinaryUnitRequirements(p, p.unit(number)!!, store.v2LearnerView()).allowed)
                assertFalse(advancementDecision(p, p.unit(number)!!, store.v2LearnerView()).allowed)
                val assignment = store.openCumulativeReview(p, number, store.learnerMutationEpoch())!!
                if (number == 3) assertEquals(old.reviewAssignments.single { it.targetUnit == 3 }, assignment)
                else assertFalse(old.reviewAssignments.any { it.id == assignment.id })
                assertEquals(if (number == 3) 2 else 1, assignment.schema)
                assertEquals(assignment, store.openCumulativeReview(p, number, store.learnerMutationEpoch()))
                if (number == 4) assertEquals(V2Validator.unitContentReviewDigest(p, 3), assignment.slots[1].unitDigest)
                assignment.slots.indices.forEach { index ->
                    assertTrue(store.answerCumulativeReview(p, assignment.id, index, p.item(assignment.slots[index].itemId)!!.answer, store.learnerMutationEpoch()))
                    if (index == 0) assertFalse(advancementDecision(p, p.unit(number)!!, store.v2LearnerView()).allowed)
                }
                assertTrue(runBlocking {
                    val next = p.unit(number + 1)!!.sessions.first().id
                    store.v2AdvanceUnitIfCurrentAsync(store.learnerMutationEpoch(), number, "fresh", number + 1, next, resumePositionDigest(p, next, 0)!!, GOVERNED_IDENTITY_SCHEMA, p)
                })
                val completion = store.governedLearnerDocument().unitCompletions.single { it.unitNumber == number }
                assertEquals(assignment.id, completion.reviewAssignmentId)
                assertEquals(assignment.policyDigest, completion.reviewPolicyDigest)
            }
            assertEquals(setOf(1, 2, 3, 4), currentCompletedUnitNumbers(p, store.v2LearnerView()))
            old.reviewAssignments.forEach { row -> assertTrue(store.cumulativeReviewHistory().first.contains(row)) }
        }
    }

    @Test fun wrongDoorAnswerRestoresTheFullContrastAndStillNeedsFreshIndependentWork() = repairUi("u3-i-gender-дверь")
    @Test fun wrongNeuterAnswerRestoresTheFullContrastAndStillNeedsFreshIndependentWork() = repairUi("u3-i-gender-море")

    private fun repairUi(id: String) {
        val p = current; val item = p.item(id)!!; val check = p.step("u3-st-check-gender")!!
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 3,
                startingPoint = "RESUME_FROM_BOOK", currentSessionId = check.first.id,
                currentStepIndex = check.first.steps.indexOf(check.second)).verifiedResume(p))
            compose.setContent { V2App(store, p, {}) }
            awaitTag("v2-continue"); compose.onNodeWithTag("v2-continue").performClick()
            val wrong = item.options.first { it != item.answer }
            awaitTag("v2-option-$id-$wrong"); compose.onNodeWithTag("v2-option-$id-$wrong").performScrollTo().performClick()
            awaitTag("v2-item-feedback-$id"); compose.onNodeWithTag("v2-item-feedback-$id").performScrollTo().assertTextEquals("Not the reviewed answer. ${item.feedback}")
            compose.onNodeWithTag("v2-remediation-$id").performScrollTo().performClick()
            awaitTag("v2-step-title"); compose.onNodeWithTag("v2-step-title").assertTextEquals("Review the admitted noun genders")
            for (example in listOf("ex-u3-друг", "ex-u3-дверь", "ex-u3-море", "ex-u3-дядя")) compose.onNodeWithTag("v2-example-$example").performScrollTo().assertIsDisplayed()
            compose.onNodeWithTag("v2-review-practice").performClick()
            val guides = reviewedGuidedPractice(p, "u3-st-rem-gender")
            guides.forEachIndexed { index, step ->
                p.step(step)!!.second.itemIds.forEach { guideId ->
                    val guide = p.item(guideId)!!
                    awaitTag("v2-option-$guideId-${guide.answer}")
                    compose.onNodeWithTag("v2-option-$guideId-${guide.answer}").performScrollTo().performClick()
                }
                val tag = if (index == guides.lastIndex) "v2-return-to-check" else "v2-next-practice"
                awaitTag(tag); compose.onNodeWithTag(tag).performClick()
            }
            awaitTag("v2-option-$id-$wrong"); compose.onNodeWithTag("v2-option-$id-$wrong").performScrollTo().assertIsSelected()
            assertFalse(id in creditItemIds(p, store.v2LearnerView()))
            assertTrue(store.cumulativeReviewHistory().first.isEmpty())
            compose.onNodeWithTag("v2-option-$id-${item.answer}").performScrollTo().performClick()
            compose.waitUntil(30000) { compose.waitForIdle(); id in creditItemIds(p, store.v2LearnerView()) }
            assertFalse(advancementDecision(p, p.unit(3)!!, store.v2LearnerView()).allowed)
        }
    }
    private fun ordinary(p: V2CurriculumPack, store: LocalStore, number: Int) {
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = number, currentSessionId = null))
        p.unit(number)!!.sessions.flatMap { it.steps }.forEach { step ->
            step.itemIds.forEach { id -> store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, p.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "same-clock")!!) }
            store.v2CompleteGovernedStep(step.id, governedStepCompletionDigest(p, store.v2LearnerView(), step.id)!!, "same-clock")
        }
        store.v2AttestBook(number, "same-clock")
    }
    private fun awaitTag(tag: String) = compose.waitUntil(conditionDescription = tag, timeoutMillis = 30000) {
        compose.waitForIdle(); compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
    }
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    companion object {
        private fun root() = File(if (File("app").isDirectory) "." else "..")
        private fun evidence() = File(root(), "audit/independent-gpt61-2026-10-12/ia003-unit3-gender-correction")
        private val current by lazy { V2PackLoader.parse(File(root(), "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) } }
        private val previous by lazy {
            val command = ProcessBuilder("git", "show", "547457b63b317dea560e4bdc36083c1a4dce315e:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
            V2PackLoader.parse(command.inputStream.bufferedReader().readText()).also { assertEquals(0, command.waitFor()); V2PackIdentityIndex.warm(it) }
        }
        private val postTeachingCorrection by lazy {
            val command = ProcessBuilder("git", "show", "61c68cfcf20cd92a4b6c310c4abf8ff2284e36e5:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
            V2PackLoader.parse(command.inputStream.bufferedReader().readText()).also { assertEquals(0, command.waitFor()); V2PackIdentityIndex.warm(it) }
        }
        internal fun preTeachingCorrectionPack(): V2CurriculumPack = previous
        internal fun declaredAfterUnit(): V2Unit {
            val ledger = Json.parseToJsonElement(File(evidence(), "CORRECTION.json").readText()).jsonObject
            return Json { ignoreUnknownKeys = true }.decodeFromJsonElement<V2Unit>(ledger.getValue("afterUnit"))
        }
    }
}
