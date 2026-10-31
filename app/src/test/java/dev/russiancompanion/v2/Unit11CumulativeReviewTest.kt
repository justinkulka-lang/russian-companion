package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.ui.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.io.ByteArrayOutputStream

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit11CumulativeReviewTest {
    @get:Rule val compose = createComposeRule()
    @After fun reset() { V2UiWork.blockForTests = null }

    @Test fun ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup() {
        val p = pack()
        ready(p).use { s ->
            val earlier = s.cumulativeReviewHistory().first.single { it.targetUnit == 10 }
            assertEquals(10, earlier.targetUnit)
            assertTrue(ordinaryUnitRequirements(p, p.unit(11)!!, s.v2LearnerView()).allowed)
            assertFalse(advancementDecision(p, p.unit(11)!!, s.v2LearnerView()).allowed)
            val a = open(p, s)
            assertEquals(1, a.schema)
            assertEquals(Unit11NounCumulativeReviewPolicy.definition.id, a.policyId)
            assertEquals(listOf("obj-u5-indeclinable", "obj-u10-have"), a.slots.map { it.objectiveId })
            assertEquals(listOf("SUPPLIED_NOUN_BEHAVIOR_RECOGNITION", "SUPPLIED_POSSESSION_ABSENCE_RECOGNITION"), a.slots.map { it.skill })
            assertTrue(a.slots[0].itemId in Unit11NounCumulativeReviewPolicy.definition.pool[0].candidateItemIds)
            assertTrue(a.slots[1].itemId in Unit11NounCumulativeReviewPolicy.definition.pool[1].candidateItemIds)
            assertTrue(completedReviewSlots(p, a, s.cumulativeReviewHistory().second).isEmpty())
            answer(p, s, a, 0); assertFalse(advancementDecision(p, p.unit(11)!!, s.v2LearnerView()).allowed)
            answer(p, s, a, 1); assertTrue(advancementDecision(p, p.unit(11)!!, s.v2LearnerView()).allowed)
            assertFalse(advancementDecision(p, p.unit(11)!!, s.v2LearnerView().copy(bookAttestedUnits = setOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))).allowed)
            assertTrue(advance(p, s)); assertEquals(12, s.v2Profile().currentUnitNumber)
            val row = s.governedLearnerDocument().unitCompletions.single { it.unitNumber == 11 }
            assertEquals(a.id, row.reviewAssignmentId); assertTrue(row.reviewCompletionOrder > a.boundary)
            answer(p, s, a, 1, correct = false)
            assertFalse(cumulativeReviewComplete(p, s.v2LearnerView(), 11))
            assertTrue(11 in currentCompletedUnitNumbers(p, s.v2LearnerView()))
            val bytes = ByteArrayOutputStream()
            writeGovernedBackup(bytes, "dev.russiancompanion", p.packId, p.packVersion, "FOLLOW_SYSTEM", s.governedLearnerDocument(), emptyList())
            val temp = File(context().cacheDir, "unit11-new-${System.nanoTime()}")
            try {
                val read = readGovernedBackup(bytes.toByteArray().inputStream(), "dev.russiancompanion", p.packId, p.packVersion, temp) as BackupReadResult.Valid
                LocalStore.openInMemory(context()).use { target ->
                    assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner(read.backup.document, emptyList()))
                    assertEquals(s.cumulativeReviewHistory(), target.cumulativeReviewHistory())
                    assertTrue(11 in currentCompletedUnitNumbers(p, target.v2LearnerView()))
                    assertFalse(cumulativeReviewComplete(p, target.v2LearnerView(), 11))
                }
            } finally { temp.deleteRecursively() }
        }
    }

    @Test fun optionalReplaysStalePrecheckAndInjectedAdvanceFailureCannotCreateCompletion() {
        val p = pack()
        ready(p).use { s ->
            val a = open(p, s)
            a.slots.forEach { slot ->
                val i = p.item(slot.itemId)!!
                s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, i.id, i.answer, OPTIONAL_PRACTICE, practiceStepId("mixed"), "replay")!!)
            }
            assertFalse(cumulativeReviewComplete(p, s.v2LearnerView(), 11))
            answer(p, s, a, 0); answer(p, s, a, 1)
            assertTrue(advancementDecision(p, p.unit(11)!!, s.v2LearnerView()).allowed)
            answer(p, s, a, 1, correct = false)
            assertTrue(runCatching { advance(p, s) }.isFailure)
            assertFalse(s.governedLearnerDocument().unitCompletions.any { it.unitNumber == 11 })
            answer(p, s, a, 1)
            s.failAdvanceAfterCompletionForTests = true
            assertTrue(runCatching { advance(p, s) }.isFailure)
            assertEquals(11, s.v2Profile().currentUnitNumber)
            assertFalse(s.governedLearnerDocument().unitCompletions.any { it.unitNumber == 11 })
            val wrong = p.copy(units = p.units.map { if (it.number == 11) it.copy(advancement = it.advancement!!.copy(requireDueReview = false)) else it })
            assertFalse(reviewAssignmentCurrent(wrong, a)); assertFalse(advancementDecision(wrong, wrong.unit(11)!!, s.v2LearnerView()).allowed)
            assertFalse(V2Validator.validate(wrong).ok)
            s.failAdvanceAfterCompletionForTests = false
            val next = p.unit(12)!!.sessions.first().id
            assertTrue(runCatching { runBlocking {
                s.v2AdvanceUnitIfCurrentAsync(s.learnerMutationEpoch(), 11, "packless", 12, next,
                    resumePositionDigest(p, next, 0)!!, GOVERNED_IDENTITY_SCHEMA)
            } }.isFailure)
            assertFalse(s.governedLearnerDocument().unitCompletions.any { it.unitNumber == 11 })
        }
    }

    @Test fun rawCurrentCompletionAndForgedPreReviewActivationDoNotCreditThisUnit() {
        val p = Unit3GenderTeachingTest.preTeachingCorrectionPack()
        ready(p).use { fresh ->
                fresh.v2CompleteUnit(11, "raw-current-source")
                assertFalse(11 in currentCompletedUnitNumbers(p, fresh.v2LearnerView()))
                val fake = fresh.governedLearnerDocument().copy(unitCompletions = fresh.governedLearnerDocument().unitCompletions.map {
                    if (it.unitNumber == 11) it.copy(preReviewActivation = true) else it })
                assertEquals(GovernedReplaceOutcome.REPLACED, fresh.replaceGovernedLearner(fake, emptyList()))
                assertFalse(11 in currentCompletedUnitNumbers(p, fresh.v2LearnerView()))
        }
    }

    @Test fun oldFormatOneAndTwoKeepHistoricalWorkWithoutCreditingChangedUnit11Instruction() {
        val old = oldPack("20a227817562e3a8645488fd52db2e7589713a7a"); val p = Unit3GenderTeachingTest.preTeachingCorrectionPack() /* Historical activation-only source; current changed-teaching refusal has separate coverage. */
        LocalStore.openInMemory(context()).use { source ->
            for (n in 1..11) { ordinary(old, source, n); source.v2CompleteUnit(n, "historical") }
            for (version in 1..2) {
                val doc = source.governedLearnerDocument().copy(schemaVersion = version,
                    unitCompletions = source.governedLearnerDocument().unitCompletions.map { it.copy(preReviewActivation = true) })
                val bytes = ByteArrayOutputStream()
                writeGovernedBackup(bytes, "dev.russiancompanion", p.packId, p.packVersion, "FOLLOW_SYSTEM", doc, emptyList(), formatVersion = version)
                val temp = File(context().cacheDir, "unit11-old-$version-${System.nanoTime()}")
                try {
                    val read = readGovernedBackup(bytes.toByteArray().inputStream(), "dev.russiancompanion", p.packId, p.packVersion, temp) as BackupReadResult.Valid
                    LocalStore.openInMemory(context()).use { target ->
                        assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner(read.backup.document, emptyList()))
                        assertEquals(setOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), currentCompletedUnitNumbers(p, target.v2LearnerView()))
                        assertTrue(target.cumulativeReviewHistory().first.isEmpty()); assertTrue(target.cumulativeReviewHistory().second.isEmpty())
                        assertFalse(cumulativeReviewComplete(p, target.v2LearnerView(), 11))
                    }
                } finally { temp.deleteRecursively() }
            }
        }
    }

    @Test fun actualUnit11ContinuePossessionAbsenceCueTeachingPracticeReturnAndAdvanceStayOnUnit11() {
        val p = pack()
        // Historical v1 reading/absence obligation; actual-current noun/possession paths
        // and both owner5/10 routes are covered by Unit11NounReviewUiTest.
        RecordedCurriculumConservationScope.beforeUnit11NounReview(root(), p).let { p ->
            ready(p).use { s ->
                compose.setContent { V2App(s, p, {}) }
                waitTag("v2-continue"); compose.onNodeWithTag("v2-continue").performClick()
                waitTag("v2-review-start"); compose.onNodeWithTag("v2-review-start").performScrollTo().performClick()
                waitTag("v2-review-count"); val a = s.cumulativeReviewHistory().first.single { it.targetUnit == 11 }
                val noun = p.item(a.slots[1].itemId)!!
                compose.onNode(hasText(noun.promptLetter!!) and !hasClickAction()).performScrollTo().assertIsDisplayed()
                val wrong = noun.options.first { it != noun.answer }
                compose.onNodeWithTag("v2-review-option-1-$wrong").performScrollTo().performClick()
                waitTag("v2-review-feedback-1"); compose.onNodeWithTag("v2-review-feedback-1").assertTextEquals("Not the reviewed answer. ${noun.feedback}")
                compose.onNodeWithTag("v2-review-remediate-1").performScrollTo().performClick()
                waitTag("v2-example-ex-u10-have-6"); compose.onNodeWithTag("v2-example-ex-u10-have-6").performScrollTo().assertIsDisplayed()
                compose.onNodeWithText("The no-money example also denies possession.", substring = true).performScrollTo().assertIsDisplayed()
                waitTag("v2-review-practice"); compose.onNodeWithTag("v2-review-practice").performClick()
                val practice = reviewedGuidedPractice(p, "u10-st-rem-have")
                assertEquals(listOf("u10-st-guided-have-1"), practice)
                assertEquals(7, p.step("u10-st-rem-have")!!.second.exampleIds.size)
                assertNotEquals(noun.promptLetter, p.item("u10-g-have-0")!!.promptLetter)
                assertNotEquals(noun.answer, p.item("u10-g-have-0")!!.answer)
                val remediation = p.step("u10-st-rem-have")!!.second
                for (n in 1..5) assertTrue(remediation.body.contains(p.step("u10-st-teach-have-$n")!!.second.body))
                practice.forEach { step ->
                    p.step(step)!!.second.itemIds.forEach { id ->
                        val i = p.item(id)!!; waitTag("v2-option-$id-${i.answer}")
                        compose.onNodeWithTag("v2-option-$id-${i.answer}").performScrollTo().performClick()
                    }
                }
                waitTag("v2-return-to-check"); compose.onNodeWithTag("v2-return-to-check").performClick()
                waitTag("v2-review-count"); assertEquals(a, s.cumulativeReviewHistory().first.single { it.targetUnit == 11 })
                assertFalse(1 in completedReviewSlots(p, a, s.cumulativeReviewHistory().second))
                a.slots.forEachIndexed { index, slot ->
                    val answer = p.item(slot.itemId)!!.answer
                    compose.onNodeWithTag("v2-review-option-$index-$answer").performScrollTo().performClick()
                    compose.waitForIdle()
                }
                waitTag("v2-review-gate"); compose.onNodeWithTag("v2-review-gate").performScrollTo().performClick()
                waitTag("v2-advance"); compose.onNodeWithTag("v2-advance").performScrollTo().assertIsEnabled().performClick()
                compose.waitUntil(30000) { compose.waitForIdle(); s.v2Profile().currentUnitNumber == 12 }
            }
        }
    }

    @Test fun placementWithTaughtUnit1ButUntaughtUnit10RepairsTheMissingOwnerInsteadOfUnit1() {
        val p = pack()
        // Historical v1 reading/absence obligation; actual-current noun/possession paths
        // and both owner5/10 routes are covered by Unit11NounReviewUiTest.
        RecordedCurriculumConservationScope.beforeUnit11NounReview(root(), p).let { p ->
            LocalStore.openInMemory(context()).use { s ->
                for (n in 1..2) { ordinary(p, s, n); s.v2CompleteUnit(n, "done") }
                s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 11, startingPoint = "RESUME_FROM_BOOK"))
                compose.setContent { V2App(s, p, {}) }
                compose.onNodeWithText("Course").performClick()
                waitTag("v2-course-intro"); compose.onNode(hasScrollToIndexAction()).performScrollToIndex(11)
                waitTag("v2-unit-row-11"); compose.onNodeWithTag("v2-unit-row-11").performClick()
                waitTag("v2-open-cumulative-review"); compose.onNodeWithTag("v2-open-cumulative-review").performScrollTo().performClick()
                waitTag("v2-review-insufficient"); compose.onNodeWithTag("v2-review-start").assertDoesNotExist()
                compose.onNodeWithTag("v2-review-repair").performScrollTo().assertTextEquals("Open earlier Unit 10 instruction and checks").performClick()
                waitTag("v2-unit-heading"); compose.onNodeWithText("Unit 10").assertIsDisplayed()
                assertFalse(s.cumulativeReviewHistory().first.any { it.targetUnit == 11 })
            }
        }
    }

    @Test fun heldUnit11WriteRejectsReplacementEpochEvenWhenNewLearnerStillHasEligibleTeaching() {
        val p = pack()
        ready(p).use { s ->
            val entered = CompletableDeferred<Unit>(); val release = CompletableDeferred<Unit>()
            val api = object : V2Persistence by StoreV2Persistence(s, p) {
                override suspend fun openCumulativeReview(pack: V2CurriculumPack, targetUnit: Int) {
                    assertEquals(11, targetUnit); entered.complete(Unit); release.await()
                    StoreV2Persistence(s, p).openCumulativeReview(pack, targetUnit)
                }
            }
            V2UiWork.blockForTests = false
            compose.setContent { V2App(s, p, {}, persistence = api) }
            waitTag("v2-continue"); compose.onNodeWithTag("v2-continue").performClick()
            waitTag("v2-review-start"); compose.onNodeWithTag("v2-review-start").performScrollTo().performClick()
            compose.waitUntil(30000) { compose.waitForIdle(); entered.isCompleted }
            compose.onNodeWithTag("v2-back").performClick(); waitTag("v2-continue")
            val d = s.governedLearnerDocument(); val replacement = d.copy(profile = d.profile.copy(currentUnitNumber = 1))
            s.openLearnerReplacement()
            try { assertEquals(GovernedReplaceOutcome.REPLACED, s.replaceGovernedLearner(replacement, emptyList())) }
            finally { s.closeLearnerReplacement() }
            assertNotNull(createCumulativeReviewAssignment(p, s.v2LearnerView(), 11, "still-eligible", 0))
            val before = s.governedLearnerFingerprint(); release.complete(Unit)
            compose.waitUntil(30000) { compose.waitForIdle(); compose.onAllNodesWithText("Not saved. The previous record is unchanged.", substring = true).fetchSemanticsNodes().isNotEmpty() }
            assertEquals(before, s.governedLearnerFingerprint())
            assertFalse(s.cumulativeReviewHistory().first.any { it.targetUnit == 11 })
        }
    }

    private fun ready(p: V2CurriculumPack) = LocalStore.openInMemory(context()).also { prepare(p, it) }
    private fun prepare(p: V2CurriculumPack, s: LocalStore) {
        for (n in 1..2) { ordinary(p, s, n); s.v2CompleteUnit(n, "done") }
        ordinary(p, s, 3)
        val a = s.openUnit3CumulativeReview(p, s.learnerMutationEpoch())!!
        answer(p, s, a, 0); answer(p, s, a, 1)
        runBlocking { advanceFrom(p, s, 3) }
        ordinary(p, s, 4)
        val previous = s.openCumulativeReview(p, 4, s.learnerMutationEpoch())!!
        answer(p, s, previous, 0); answer(p, s, previous, 1)
        runBlocking { advanceFrom(p, s, 4) }
        ordinary(p, s, 5)
        val fifth = s.openCumulativeReview(p, 5, s.learnerMutationEpoch())!!
        answer(p, s, fifth, 0); answer(p, s, fifth, 1)
        runBlocking { advanceFrom(p, s, 5) }
        ordinary(p, s, 6)
        val sixth = s.openCumulativeReview(p, 6, s.learnerMutationEpoch())!!
        sixth.slots.indices.forEach { answer(p, s, sixth, it) }
        runBlocking { advanceFrom(p, s, 6) }
        ordinary(p, s, 7)
        val seventh = s.openCumulativeReview(p, 7, s.learnerMutationEpoch())!!
        answer(p, s, seventh, 0); answer(p, s, seventh, 1)
        runBlocking { advanceFrom(p, s, 7) }
        ordinary(p, s, 8)
        val eighth = s.openCumulativeReview(p, 8, s.learnerMutationEpoch())!!
        answer(p, s, eighth, 0); answer(p, s, eighth, 1)
        runBlocking { advanceFrom(p, s, 8) }
        ordinary(p, s, 9)
        val ninth = s.openCumulativeReview(p, 9, s.learnerMutationEpoch())!!
        ninth.slots.indices.forEach { answer(p, s, ninth, it) }
        runBlocking { advanceFrom(p, s, 9) }
        ordinary(p, s, 10)
        val tenth = s.openCumulativeReview(p, 10, s.learnerMutationEpoch())!!
        answer(p, s, tenth, 0); answer(p, s, tenth, 1)
        runBlocking { advanceFrom(p, s, 10) }
        ordinary(p, s, 11)
        assertEquals(setOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), currentCompletedUnitNumbers(p, s.v2LearnerView()))
    }
    private fun ordinary(p: V2CurriculumPack, s: LocalStore, n: Int) {
        s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = n, currentSessionId = null))
        for (step in p.unit(n)!!.sessions.flatMap { it.steps }) {
            for (id in step.itemIds) s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, p.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "same-clock")!!)
            s.v2CompleteGovernedStep(step.id, governedStepCompletionDigest(p, s.v2LearnerView(), step.id)!!, "same-clock")
        }
        s.v2AttestBook(n, "same-clock")
    }
    private fun open(p: V2CurriculumPack, s: LocalStore) = s.openCumulativeReview(p, 11, s.learnerMutationEpoch())!!
    private fun answer(p: V2CurriculumPack, s: LocalStore, a: CumulativeReviewAssignment, index: Int, correct: Boolean = true) {
        val i = p.item(a.slots[index].itemId)!!
        assertTrue(s.answerCumulativeReview(p, a.id, index, if (correct) i.answer else i.options.first { it != i.answer }, s.learnerMutationEpoch()))
    }
    private fun advance(p: V2CurriculumPack, s: LocalStore): Boolean = runBlocking { advanceFrom(p, s, 11) }
    private suspend fun advanceFrom(p: V2CurriculumPack, s: LocalStore, n: Int): Boolean {
        val session = p.unit(n + 1)!!.sessions.first().id
        return s.v2AdvanceUnitIfCurrentAsync(s.learnerMutationEpoch(), n, "advance", n + 1, session, resumePositionDigest(p, session, 0)!!, GOVERNED_IDENTITY_SCHEMA, p)
    }
    private fun waitTag(tag: String) = compose.waitUntil(conditionDescription = tag, timeoutMillis = 30000) {
        compose.waitForIdle(); compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
    }
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    private fun root() = File(if (File("app").isDirectory) "." else "..")
    private fun pack() = sharedPack
    private fun oldPack(commit: String): V2CurriculumPack {
        val process = ProcessBuilder("git", "show", "$commit:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
        val p = V2PackLoader.parse(process.inputStream.bufferedReader().readText())
        assertEquals(0, process.waitFor()); V2PackIdentityIndex.warm(p); return p
    }
    companion object { private val sharedPack by lazy {
        val root = File(if (File("app").isDirectory) "." else "..")
        V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) }
    } }
}
