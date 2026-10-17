package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.backup.*
import kotlinx.coroutines.runBlocking
import dev.russiancompanion.v2.ui.V2App
import kotlinx.serialization.json.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit13PastTeachingTest {
    @get:Rule val compose = createComposeRule()
    @Test fun completePastTeachingPrecedesPracticeAndRepairRestoresAllFiveExamples() {
        val p = pack(); val s = session(p); val old = session(baseline())
        assertEquals(1, old.steps.takeWhile { it.kind != "GUIDED" }.count { it.kind == "TEACH" })
        assertEquals(4, s.steps.takeWhile { it.kind != "GUIDED" }.count { it.kind == "TEACH" })
        val teaching = s.steps.filter { it.kind == "TEACH" }; val rem = p.step("u13-st-rem-past-aspect")!!.second
        assertEquals(teaching.joinToString("\n\n") { it.body }, rem.body)
        assertEquals(listOf("ex-u13-past-aspect-0", "ex-u13-past-aspect-1", "ex-or-u13-past-aspect-2", "ex-or-u13-past-aspect-3", "ex-or-u13-past-aspect-4"), rem.exampleIds)
        assertEquals(listOf("я читал", "я прочитал", "чита́ть", "чита́л", "чита́ла"), rem.exampleIds.map { p.example(it)!!.display })
        assertTrue(rem.body.contains("The pronoun I does not choose one past-tense gender for every speaker;"))
        assertTrue(p.provenanceRecords.any { it.objectiveId == "obj-u13-past-aspect" && it.shippedText == rem.body && it.itemId == "prov-ca-u13-past-u13-st-rem-past-aspect-complete-v1" })
        val valid = V2Validator.validate(p); assertTrue(valid.errors.joinToString("\n"), valid.ok)
    }
    @Test fun unchangedQuestionsRecordsAndRequirementsHaveAnExactSessionOnlyException() {
        val p = pack(); val old = baseline()
        assertEquals(declaredAfterSession(), session(p))
        assertEquals(old, p.copy(units = old.units, provenanceRecords = p.provenanceRecords.dropLast(5)))
        assertEquals(old.unit(13)!!.advancement, p.unit(13)!!.advancement)
        assertEquals(8, p.unit(13)!!.advancement!!.minimumDistinctChecks)
        assertEquals(old.items, p.items); assertEquals(old.examples, p.examples)
        assertEquals(old.units.filter { it.number != 13 }, p.units.filter { it.number != 13 })
        assertEquals(old.unit(13)!!.copy(sessions = p.unit(13)!!.sessions), p.unit(13))
        assertEquals(old.unit(13)!!.sessions.filter { it.id != session(p).id }, p.unit(13)!!.sessions.filter { it.id != session(p).id })
        assertEquals(old.provenanceRecords, p.provenanceRecords.dropLast(5))
        assertNull(cumulativeReviewPolicy(31))
    }
    @Test fun oldGuidePositionDoesNotResumeAsTheNewSecondTeachingCard() {
        val p = pack(); val old = baseline()
        LocalStore.openInMemory(context()).use { store ->
            val profile = store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 13,
                currentSessionId = "u13-s-past-aspect", currentStepIndex = 1).verifiedResume(old)
            store.v2SaveProfile(profile)
            assertTrue(resumeIsCurrent(old, store.v2LearnerView()))
            assertFalse(resumeIsCurrent(p, store.v2LearnerView()))
            assertEquals(profile, store.v2Profile())
            assertEquals("GUIDED", session(old).steps[1].kind)
            assertEquals("u13-st-teach-past-aspect-2", session(p).steps[1].id)
        }
    }
    @Test fun oldPrescribedAttemptsStayStoredButCannotCreditChangedUnitInstruction() {
        val p = pack(); val old = baseline(); val id = "u13-i-past-aspect-1"; val step = "u13-st-check-past-aspect"
        LocalStore.openInMemory(context()).use { store ->
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(old, id, old.item(id)!!.answer, PRESCRIBED_CHECK, step, "same-clock")!!)
            assertTrue(id in creditItemIds(old, store.v2LearnerView()))
            assertFalse(id in creditItemIds(p, store.v2LearnerView()))
            assertEquals(1, store.v2Attempts().size)
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, p.item(id)!!.answer, PRESCRIBED_CHECK, step, "same-clock")!!)
            assertTrue(id in creditItemIds(p, store.v2LearnerView()))
            assertEquals(2, store.v2Attempts().size)
            val objective = p.unit(13)!!.objectives.single { it.id == "obj-u13-past-aspect" }
            assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS, objectiveState(objective, store.v2LearnerView(), p))
        }
    }
    @Test fun wrongAspectChoiceRestoresCompleteRepairAndRequiresFreshIndependentAnswer() = repairUi("u13-i-past-aspect-1")
    @Test fun wrongGenderChoiceRestoresCompleteRepairAndRequiresFreshIndependentAnswer() = repairUi("u13-i-past-aspect-2")
    private fun repairUi(itemId: String) {
        val p = pack(); val item = p.item(itemId)!!; val check = p.step("u13-st-check-past-aspect")!!
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 13,
                startingPoint = "RESUME_FROM_BOOK", currentSessionId = check.first.id,
                currentStepIndex = check.first.steps.indexOf(check.second)).verifiedResume(p))
            compose.setContent { V2App(store, p, {}) }
            awaitTag("v2-continue"); compose.onNodeWithTag("v2-continue").performClick()
            val wrong = item.options.single { it != item.answer }
            awaitTag("v2-option-${item.id}-$wrong"); compose.onNodeWithTag("v2-option-${item.id}-$wrong").performScrollTo().performClick()
            awaitTag("v2-remediation-${item.id}"); compose.onNodeWithTag("v2-remediation-${item.id}").performScrollTo().performClick()
            for (id in listOf("ex-u13-past-aspect-0", "ex-u13-past-aspect-1", "ex-or-u13-past-aspect-2", "ex-or-u13-past-aspect-3", "ex-or-u13-past-aspect-4")) {
                awaitTag("v2-example-$id"); compose.onNodeWithTag("v2-example-$id").performScrollTo().assertIsDisplayed()
            }
            compose.onNodeWithText("The pronoun I does not choose one past-tense gender for every speaker;", substring = true).performScrollTo().assertIsDisplayed()
            awaitTag("v2-review-practice"); compose.onNodeWithTag("v2-review-practice").performClick()
            val guide = p.item("u13-g-past-aspect-0")!!
            awaitTag("v2-option-${guide.id}-${guide.answer}"); compose.onNodeWithTag("v2-option-${guide.id}-${guide.answer}").performScrollTo().performClick()
            awaitTag("v2-return-to-check"); compose.onNodeWithTag("v2-return-to-check").performClick()
            awaitTag("v2-option-${item.id}-$wrong"); compose.onNodeWithTag("v2-option-${item.id}-$wrong").performScrollTo().assertIsSelected()
            assertFalse(item.id in creditItemIds(p, store.v2LearnerView()))
            compose.onNodeWithTag("v2-option-${item.id}-${item.answer}").performScrollTo().performClick()
            compose.waitUntil(30000) { compose.waitForIdle(); item.id in creditItemIds(p, store.v2LearnerView()) }
            assertFalse(listOf("u13-i-past-aspect-1", "u13-i-past-aspect-2").single { it != itemId } in creditItemIds(p, store.v2LearnerView()))
            assertFalse(advancementDecision(p, p.unit(13)!!, store.v2LearnerView()).allowed)
        }
    }
    @Test fun genuineBackupsRetainRawHistoryButChangedTeachingRejectsOldCreditAfterReopen() {
        val p = current; val old = previous
        val labels = listOf("BEFORE_UNIT13_WORK", "ORDINARY_UNIT13", "UNSTARTED_REVIEW", "PARTIAL_REVIEW", "ANSWERED_REVIEW", "COMPLETED_UNIT13", "POST_COMPLETION_WRONG", "COMPLETED_UNIT14")
        labels.forEach { label ->
            val original = backupJson.decodeFromString(GovernedLearnerDocument.serializer(), File(evidence(), "$label.json").readText())
            val staging = File(context().cacheDir, "past-$label-${System.nanoTime()}")
            val decoded = readGovernedBackup(File(evidence(), "$label.zip").inputStream(), "dev.russiancompanion", p.packId, p.packVersion, staging)
            assertTrue(label, decoded is BackupReadResult.Valid)
            assertEquals(original, (decoded as BackupReadResult.Valid).backup.document)
            val name = "past-$label-${System.nanoTime()}.db"
            fun check(store: LocalStore) {
                assertEquals(original.reviewAssignments to original.reviewAnswers, store.cumulativeReviewHistory())
                assertEquals(original.unitCompletions, store.governedLearnerDocument().unitCompletions)
                assertEquals(original.attempts, store.governedLearnerDocument().attempts)
                assertEquals((1..12).toSet(), currentCompletedUnitNumbers(p, store.v2LearnerView()))
                assertFalse(ordinaryUnitRequirements(p, p.unit(13)!!, store.v2LearnerView()).allowed)
                assertEquals(original.reviewAssignments.singleOrNull { it.targetUnit == 13 }, currentCumulativeAssignment(p, store.v2LearnerView(), 13))
                assertNull(currentCumulativeAssignment(p, store.v2LearnerView(), 14))
                assertFalse(advancementDecision(p, p.unit(13)!!, store.v2LearnerView()).allowed)
                if (label == "COMPLETED_UNIT14") assertEquals((1..14).toSet(), currentCompletedUnitNumbers(old, store.v2LearnerView()))
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

    @Test fun freshTeachingAndAnswersReestablishCompletionWithoutRelabellingOldAssignments() {
        val p = current
        val old = backupJson.decodeFromString(GovernedLearnerDocument.serializer(), File(evidence(), "COMPLETED_UNIT14.json").readText())
        LocalStore.openInMemory(context()).use { store ->
            store.openLearnerReplacement()
            try { assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(old, emptyList())) }
            finally { store.closeLearnerReplacement() }
            for (number in 13..14) {
                ordinary(p, store, number)
                assertTrue(ordinaryUnitRequirements(p, p.unit(number)!!, store.v2LearnerView()).allowed)
                assertFalse(advancementDecision(p, p.unit(number)!!, store.v2LearnerView()).allowed)
                val assignment = store.openCumulativeReview(p, number, store.learnerMutationEpoch())!!
                if (number == 13) assertEquals(old.reviewAssignments.single { it.targetUnit == 13 }, assignment)
                else assertFalse(old.reviewAssignments.any { it.id == assignment.id })
                assertEquals(1, assignment.schema)
                assertEquals(assignment, store.openCumulativeReview(p, number, store.learnerMutationEpoch()))
                if (number == 14) assertEquals(V2Validator.unitContentReviewDigest(p, 13), assignment.slots[1].unitDigest)
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
            assertEquals((1..14).toSet(), currentCompletedUnitNumbers(p, store.v2LearnerView()))
            old.reviewAssignments.forEach { row -> assertTrue(store.cumulativeReviewHistory().first.contains(row)) }
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
    private fun session(p: V2CurriculumPack) = p.unit(13)!!.sessions.single { it.id == "u13-s-past-aspect" }
    private fun pack() = current
    private fun baseline() = previous
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    private fun awaitTag(tag: String) = compose.waitUntil(conditionDescription = tag, timeoutMillis = 30000) {
        compose.waitForIdle(); compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
    }
    companion object {
        private fun evidence() = File(root(), "audit/independent-gpt61-2026-10-12/ia003-unit13-past-teaching")
        private fun root() = File(if (File("app").isDirectory) "." else "..")
        private val current by lazy { V2PackLoader.parse(File(root(), "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) } }
        private val previous by lazy {
            val command = ProcessBuilder("git", "show", "03647d041d3d5068d4c77eb88f62403daf6e54ff:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
            V2PackLoader.parse(command.inputStream.bufferedReader().readText()).also { assertEquals(0, command.waitFor()); V2PackIdentityIndex.warm(it) }
        }
        internal fun beforeTeachingCorrectionPack(): V2CurriculumPack = previous
        internal fun declaredAfterSession(): V2Session {
            val ledger = Json.parseToJsonElement(File(root(), "audit/independent-gpt61-2026-10-12/ia003-unit13-past-teaching/CORRECTION.json").readText()).jsonObject
            return Json { ignoreUnknownKeys = true }.decodeFromJsonElement<V2Session>(ledger.getValue("afterSession"))
        }
    }
}
