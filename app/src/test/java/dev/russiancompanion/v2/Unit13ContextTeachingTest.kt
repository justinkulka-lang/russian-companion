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
class Unit13ContextTeachingTest {
    @get:Rule val compose = createComposeRule()
    private fun session(p: V2CurriculumPack, key: String) = p.unit(13)!!.sessions.single { it.id == "u13-s-$key" }
    @Test fun bothChainsTeachBeforePracticeAndRepairEveryOriginalExample() {
        for (key in keys) {
            val se = session(current, key); val prior = session(previous, key)
            assertEquals(1, prior.steps.takeWhile { it.kind != "GUIDED" }.count { it.kind == "TEACH" })
            assertEquals(3, se.steps.takeWhile { it.kind != "GUIDED" }.count { it.kind == "TEACH" })
            val teaching = se.steps.filter { it.kind == "TEACH" }; val rem = current.step("u13-st-rem-$key")!!.second
            assertEquals(teaching.joinToString("\n\n") { it.body }, rem.body)
            assertEquals(teaching.flatMap { it.exampleIds }.distinct(), rem.exampleIds)
            assertEquals(if (key == "tense-keep") 3 else 4, rem.exampleIds.size)
            assertTrue(current.provenanceRecords.any { it.objectiveId == "obj-u13-$key" && it.shippedText == rem.body })
        }
        assertTrue(current.step("u13-st-rem-tense-keep")!!.second.body.contains("Backshift is not mandatory"))
        assertTrue(current.step("u13-st-rem-infinitive")!!.second.body.contains("context-dependent counterparts"))
        // Historical conservation stays above; current full-pack validation has current rule pins.
        val validationPack = V2PackLoader.parse(File(root(), "app/src/main/assets/content/v2-curriculum.json").readText())
            .also { V2PackIdentityIndex.warm(it) }
        val historicalUnit = current.unit(13)!!
        assertEquals(historicalUnit, validationPack.unit(13))
        val objectiveIds = historicalUnit.objectives.map { it.id }.toSet()
        assertEquals(current.items.filter { it.objectiveId in objectiveIds }, validationPack.items.filter { it.objectiveId in objectiveIds })
        assertEquals(current.provenanceRecords.filter { it.unitNumber == 13 }, validationPack.provenanceRecords.filter { it.unitNumber == 13 })
        assertEquals(current.sourceCapabilities.filter { it.unitNumber == 13 }, validationPack.sourceCapabilities.filter { it.unitNumber == 13 })
        val exampleIds = (historicalUnit.objectives.flatMap { it.exampleIds } +
            historicalUnit.sessions.flatMap { it.steps }.flatMap { it.exampleIds }).toSet()
        assertEquals(current.examples.filter { it.id in exampleIds }, validationPack.examples.filter { it.id in exampleIds })
        val result = V2Validator.validate(validationPack); assertTrue(result.errors.joinToString("\n"), result.ok)
    }
    @Test fun exactTwoSessionWhitelistPreservesNativeScoredRecordsPoliciesAndEarlierPastCorrection() {
        val current = Unit13WiderPolicyTest.beforePolicyTransitionPack() // Exact post-teaching source; this historical whitelist predates the later policy transition.
        val ledger = Json.parseToJsonElement(File(evidence(), "CORRECTION.json").readText()).jsonObject
        val declared = Json { ignoreUnknownKeys = true }.decodeFromJsonElement<V2Unit>(ledger.getValue("afterUnit"))
        assertEquals(declared, current.unit(13))
        assertEquals(previous, current.copy(units = previous.units, provenanceRecords = current.provenanceRecords.dropLast(8)))
        assertEquals(previous.provenanceRecords, current.provenanceRecords.dropLast(8))
        assertEquals(previous.items, current.items); assertEquals(previous.examples, current.examples)
        assertEquals(previous.units.filter { it.number != 13 }, current.units.filter { it.number != 13 })
        assertEquals(previous.unit(13)!!.copy(sessions = current.unit(13)!!.sessions), current.unit(13))
        assertEquals(previous.unit(13)!!.sessions.filter { it.id !in keys.map { k -> "u13-s-$k" } },
            current.unit(13)!!.sessions.filter { it.id !in keys.map { k -> "u13-s-$k" } })
        assertEquals(8, current.unit(13)!!.advancement!!.minimumDistinctChecks)
    }
    @Test fun oldGuidedPositionsCannotResumeAsDifferentTeachingCards() {
        for (key in keys) LocalStore.openInMemory(context()).use { store ->
            val profile = store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 13,
                currentSessionId = "u13-s-$key", currentStepIndex = 1).verifiedResume(previous)
            store.v2SaveProfile(profile)
            assertTrue(resumeIsCurrent(previous, store.v2LearnerView()))
            assertFalse(resumeIsCurrent(current, store.v2LearnerView()))
            assertEquals(profile, store.v2Profile())
            assertEquals("GUIDED", session(previous, key).steps[1].kind)
            assertEquals("u13-st-teach-$key-2", session(current, key).steps[1].id)
        }
    }
    @Test fun oldAnswersStayRawButDoNotSupplyChangedInstructionCredit() {
        for (key in keys) for (n in 1..2) LocalStore.openInMemory(context()).use { store ->
            val id = "u13-i-$key-$n"; val step = "u13-st-check-$key"
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(previous, id, previous.item(id)!!.answer, PRESCRIBED_CHECK, step, "same-clock")!!)
            assertTrue(id in creditItemIds(previous, store.v2LearnerView()))
            assertFalse(id in creditItemIds(current, store.v2LearnerView())); assertEquals(1, store.v2Attempts().size)
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(current, id, current.item(id)!!.answer, PRESCRIBED_CHECK, step, "same-clock")!!)
            assertTrue(id in creditItemIds(current, store.v2LearnerView())); assertEquals(2, store.v2Attempts().size)
            assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS, objectiveState(current.unit(13)!!.objectives.single { it.id == "obj-u13-$key" }, store.v2LearnerView(), current))
        }
    }
    @Test fun wrongReportedEveningReturnsFromCompleteRepairToTheSameCheck() = repairUi("tense-keep", 1)
    @Test fun wrongEnglishBackshiftLabelReturnsFromCompleteRepairToTheSameCheck() = repairUi("tense-keep", 2)
    @Test fun wrongPerfectivePartnerReturnsFromCompleteRepairToTheSameCheck() = repairUi("infinitive", 1)
    @Test fun wrongImperfectivePartnerReturnsFromCompleteRepairToTheSameCheck() = repairUi("infinitive", 2)
    private fun repairUi(key: String, n: Int) {
        val p = current; val id = "u13-i-$key-$n"; val item = p.item(id)!!; val check = p.step("u13-st-check-$key")!!
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 13,
                startingPoint = "RESUME_FROM_BOOK", currentSessionId = check.first.id,
                currentStepIndex = check.first.steps.indexOf(check.second)).verifiedResume(p))
            compose.setContent { V2App(store, p, {}) }
            awaitTag("v2-continue"); compose.onNodeWithTag("v2-continue").performClick()
            val wrong = item.options.single { it != item.answer }
            awaitTag("v2-option-$id-$wrong"); compose.onNodeWithTag("v2-option-$id-$wrong").performScrollTo().performClick()
            awaitTag("v2-item-feedback-$id"); compose.onNodeWithTag("v2-item-feedback-$id").performScrollTo().assertTextEquals("Not the reviewed answer. ${item.feedback}")
            compose.onNodeWithTag("v2-remediation-$id").performScrollTo().performClick()
            val rem = p.step("u13-st-rem-$key")!!.second
            rem.exampleIds.forEach { e -> awaitTag("v2-example-$e"); compose.onNodeWithTag("v2-example-$e").performScrollTo().assertIsDisplayed() }
            val qualifier = if (key == "tense-keep") "Backshift is not mandatory" else "context-dependent counterparts"
            compose.onNodeWithText(qualifier, substring = true).performScrollTo().assertIsDisplayed()
            compose.onNodeWithTag("v2-review-practice").performClick()
            val guide = p.item("u13-g-$key-0")!!
            awaitTag("v2-option-${guide.id}-${guide.answer}"); compose.onNodeWithTag("v2-option-${guide.id}-${guide.answer}").performScrollTo().performClick()
            awaitTag("v2-return-to-check"); compose.onNodeWithTag("v2-return-to-check").performClick()
            awaitTag("v2-option-$id-$wrong"); compose.onNodeWithTag("v2-option-$id-$wrong").performScrollTo().assertIsSelected()
            assertFalse(id in creditItemIds(p, store.v2LearnerView()))
            compose.onNodeWithTag("v2-option-$id-${item.answer}").performScrollTo().performClick()
            compose.waitUntil(30000) { compose.waitForIdle(); id in creditItemIds(p, store.v2LearnerView()) }
            assertFalse("u13-i-$key-${if (n == 1) 2 else 1}" in creditItemIds(p, store.v2LearnerView()))
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
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    private fun awaitTag(tag: String) = compose.waitUntil(conditionDescription = tag, timeoutMillis = 30000) {
        compose.waitForIdle(); compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
    }
    companion object {
        private val keys = listOf("tense-keep", "infinitive")
        private fun root() = File(if (File("app").isDirectory) "." else "..")
        private fun evidence() = File(root(), "audit/independent-gpt61-2026-10-12/ia003-unit13-context-teaching")
        private val current by lazy { Unit7AgreementTeachingTest.beforeTeachingCorrectionPack() } // Exact post-Unit13-policy source2ff71f19; this historical transition predates the later Unit7 teaching correction.
        private val previous by lazy {
            val process = ProcessBuilder("git", "show", "1730c1999a81e1d9d5df8bc3b957339fce1a65b4:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
            V2PackLoader.parse(process.inputStream.bufferedReader().readText()).also { assertEquals(0, process.waitFor()); V2PackIdentityIndex.warm(it) }
        }
        internal fun beforeTeachingCorrectionPack() = previous
    }
}
