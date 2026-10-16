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
class Unit16CumulativeReviewTest {
    @get:Rule val compose = createComposeRule()
    @After fun reset() {
        V2UiWork.blockForTests = null
        for (name in ownedDatabases) { context().deleteDatabase(name); File(context().filesDir, "recordings-$name").deleteRecursively() }
        ownedDatabases.clear()
    }
    private val ownedDatabases = mutableListOf<String>()
    private fun isolatedStore(label: String): LocalStore {
        val name = "unit16-$label-${System.nanoTime()}.db"; ownedDatabases += name
        return LocalStore.openNamed(context(), name)
    }

    @Test fun ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup() {
        val p = pack()
        ready(p).use { s ->
            val earlier = s.cumulativeReviewHistory().first.single { it.targetUnit == 15 }
            assertEquals(15, earlier.targetUnit)
            assertTrue(ordinaryUnitRequirements(p, p.unit(16)!!, s.v2LearnerView()).allowed)
            assertFalse(advancementDecision(p, p.unit(16)!!, s.v2LearnerView()).allowed)
            val a = open(p, s)
            assertEquals(listOf("obj-u1-decoding", "obj-u15-imperative"), a.slots.map { it.objectiveId })
            assertEquals(listOf("BOUNDED_READING", "SUPPLIED_IMPERATIVE_FORM_RECOGNITION"), a.slots.map { it.skill })
            assertEquals("u15-i-imperative-1", a.slots[1].itemId)
            assertTrue(completedReviewSlots(p, a, s.cumulativeReviewHistory().second).isEmpty())
            answer(p, s, a, 0); assertFalse(advancementDecision(p, p.unit(16)!!, s.v2LearnerView()).allowed)
            answer(p, s, a, 1); assertTrue(advancementDecision(p, p.unit(16)!!, s.v2LearnerView()).allowed)
            assertFalse(advancementDecision(p, p.unit(16)!!, s.v2LearnerView().copy(bookAttestedUnits = (1..15).toSet())).allowed)
            assertTrue(advance(p, s)); assertEquals(17, s.v2Profile().currentUnitNumber)
            val row = s.governedLearnerDocument().unitCompletions.single { it.unitNumber == 16 }
            assertEquals(a.id, row.reviewAssignmentId); assertTrue(row.reviewCompletionOrder > a.boundary)
            answer(p, s, a, 1, correct = false)
            assertFalse(cumulativeReviewComplete(p, s.v2LearnerView(), 16))
            assertTrue(16 in currentCompletedUnitNumbers(p, s.v2LearnerView()))
            val bytes = ByteArrayOutputStream()
            writeGovernedBackup(bytes, "dev.russiancompanion", p.packId, p.packVersion, "FOLLOW_SYSTEM", s.governedLearnerDocument(), emptyList())
            val temp = File(context().cacheDir, "unit16-new-${System.nanoTime()}")
            try {
                val read = readGovernedBackup(bytes.toByteArray().inputStream(), "dev.russiancompanion", p.packId, p.packVersion, temp) as BackupReadResult.Valid
                isolatedStore("case").use { target ->
                    assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner(read.backup.document, emptyList()))
                    assertEquals(s.cumulativeReviewHistory(), target.cumulativeReviewHistory())
                    assertTrue(16 in currentCompletedUnitNumbers(p, target.v2LearnerView()))
                    assertFalse(cumulativeReviewComplete(p, target.v2LearnerView(), 16))
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
            assertFalse(cumulativeReviewComplete(p, s.v2LearnerView(), 16))
            answer(p, s, a, 0); answer(p, s, a, 1)
            assertTrue(advancementDecision(p, p.unit(16)!!, s.v2LearnerView()).allowed)
            answer(p, s, a, 1, correct = false)
            assertTrue(runCatching { advance(p, s) }.isFailure)
            assertFalse(s.governedLearnerDocument().unitCompletions.any { it.unitNumber == 16 })
            answer(p, s, a, 1)
            s.failAdvanceAfterCompletionForTests = true
            assertTrue(runCatching { advance(p, s) }.isFailure)
            assertEquals(16, s.v2Profile().currentUnitNumber)
            assertFalse(s.governedLearnerDocument().unitCompletions.any { it.unitNumber == 16 })
            val wrong = p.copy(units = p.units.map { if (it.number == 16) it.copy(advancement = it.advancement!!.copy(requireDueReview = false)) else it })
            V2PackIdentityIndex.warm(wrong)
            assertFalse(reviewAssignmentCurrent(wrong, a)); assertFalse(advancementDecision(wrong, wrong.unit(16)!!, s.v2LearnerView()).allowed)
            assertFalse(V2Validator.validate(wrong).ok)
            s.failAdvanceAfterCompletionForTests = false
            val next = p.unit(17)!!.sessions.first().id
            assertTrue(runCatching { runBlocking {
                s.v2AdvanceUnitIfCurrentAsync(s.learnerMutationEpoch(), 16, "packless", 17, next,
                    resumePositionDigest(p, next, 0)!!, GOVERNED_IDENTITY_SCHEMA)
            } }.isFailure)
            assertFalse(s.governedLearnerDocument().unitCompletions.any { it.unitNumber == 16 })
        }
    }

    @Test fun schema11ExactPrePolicyRowsSurviveWhileCurrentRawRowsAndChangedInstructionDoNot() {
        val old = oldPack("85591389950bdfa388d7a18283205edd8feb61aa")
        val p = Unit3GenderTeachingTest.preTeachingCorrectionPack() // Historical activation-only source; current changed-teaching refusal has separate coverage.; val name = "unit16-old-${System.nanoTime()}.db"
        try {
            LocalStore.openNamed(context(), name).use { s ->
                prepare(old, s); s.v2CompleteUnit(16, "old-completion")
                val row = s.governedLearnerDocument().unitCompletions.single { it.unitNumber == 16 }
                assertFalse(row.preReviewActivation); assertEquals("", row.reviewAssignmentId)
                s.v2SaveProfile(s.v2Profile().copy(currentUnitNumber = 17))
            }
            LocalStore.openNamed(context(), name).use { s ->
                assertEquals((1..16).toSet(), currentCompletedUnitNumbers(p, s.v2LearnerView()))
                assertTrue(canOpenUnit(unitState(p, p.unit(17)!!, s.v2LearnerView())))
                assertFalse(s.cumulativeReviewHistory().first.any { it.targetUnit == 16 })
                assertFalse(advancementDecision(p, p.unit(16)!!, s.v2LearnerView()).allowed)
                val oldAssignments = s.cumulativeReviewHistory().first
                oldAssignments.forEach { assertTrue(reviewAssignmentCurrent(p, it)) }
                val before = s.governedLearnerFingerprint(); val a = open(p, s)
                assertNotEquals(before, s.governedLearnerFingerprint())
                answer(p, s, a, 0); answer(p, s, a, 1); assertTrue(advance(p, s))
                assertTrue(16 in currentCompletedUnitNumbers(p, s.v2LearnerView()))
                assertTrue(s.cumulativeReviewHistory().first.containsAll(oldAssignments))
                val altered = p.copy(items = p.items.map { if (it.id == "u16-i-inst-sg-1") it.copy(feedback = it.feedback + " changed") else it })
                V2PackIdentityIndex.warm(altered)
                assertFalse(16 in currentCompletedUnitNumbers(altered, s.v2LearnerView()))
            }
            ready(p).use { fresh ->
                fresh.v2CompleteUnit(16, "raw-current-source")
                assertFalse(16 in currentCompletedUnitNumbers(p, fresh.v2LearnerView()))
                val fake = fresh.governedLearnerDocument().copy(unitCompletions = fresh.governedLearnerDocument().unitCompletions.map {
                    if (it.unitNumber == 16) it.copy(preReviewActivation = true) else it })
                assertEquals(GovernedReplaceOutcome.REPLACED, fresh.replaceGovernedLearner(fake, emptyList()))
                assertFalse(16 in currentCompletedUnitNumbers(p, fresh.v2LearnerView()))
            }
        } finally { context().deleteDatabase(name) }
    }

    @Test fun oldFormatOneAndTwoKeepRawUnit16HistoryWithoutInventingCreditOrRepairingUnit11() {
        val old = oldPack("20a227817562e3a8645488fd52db2e7589713a7a"); val p = Unit3GenderTeachingTest.preTeachingCorrectionPack() // Historical activation-only source; current changed-teaching refusal has separate coverage.
        isolatedStore("case").use { source ->
            for (n in 1..16) { ordinary(old, source, n); source.v2CompleteUnit(n, "historical") }
            for (version in 1..2) {
                val doc = source.governedLearnerDocument().copy(schemaVersion = version,
                    unitCompletions = source.governedLearnerDocument().unitCompletions.map { it.copy(preReviewActivation = true) })
                val bytes = ByteArrayOutputStream()
                writeGovernedBackup(bytes, "dev.russiancompanion", p.packId, p.packVersion, "FOLLOW_SYSTEM", doc, emptyList(), formatVersion = version)
                val temp = File(context().cacheDir, "unit16-old-$version-${System.nanoTime()}")
                try {
                    val read = readGovernedBackup(bytes.toByteArray().inputStream(), "dev.russiancompanion", p.packId, p.packVersion, temp) as BackupReadResult.Valid
                    isolatedStore("case").use { target ->
                        assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner(read.backup.document, emptyList()))
                        assertEquals((1..10).toSet(), currentCompletedUnitNumbers(p, target.v2LearnerView()))
                        // Unit16 own legacy instruction survives; fixed-point prerequisites reject stale Unit11/Unit14/Unit15.
                        assertTrue(ordinaryUnitRequirements(p, p.unit(16)!!, target.v2LearnerView()).allowed)
                        assertTrue(historicalReviewCompletionCurrent(p, p.unit(16)!!, target.v2LearnerView()))
                        assertFalse(canOpenUnit(unitState(p, p.unit(17)!!, target.v2LearnerView())))
                        assertTrue(target.governedLearnerDocument().unitCompletions.any { it.unitNumber == 16 })
                        assertFalse(11 in currentCompletedUnitNumbers(p, target.v2LearnerView()))
                        assertFalse(advancementDecision(p, p.unit(16)!!, target.v2LearnerView()).allowed)
                        assertTrue(target.governedLearnerDocument().unitCompletions.any { it.unitNumber == 11 })
                        assertTrue(target.cumulativeReviewHistory().first.isEmpty()); assertTrue(target.cumulativeReviewHistory().second.isEmpty())
                        assertFalse(cumulativeReviewComplete(p, target.v2LearnerView(), 16))
                    }
                } finally { temp.deleteRecursively() }
            }
        }
    }

    @Test fun actualUnit16ContinueImperativeFormCueTeachingPracticeReturnAndAdvanceStayOnUnit16() {
        val p = pack()
        ready(p).use { s ->
            compose.setContent { V2App(s, p, {}) }
            waitTag("v2-continue"); compose.onNodeWithTag("v2-continue").performClick()
            waitTag("v2-review-start"); compose.onNodeWithTag("v2-review-start").performScrollTo().performClick()
            waitTag("v2-review-count"); val a = s.cumulativeReviewHistory().first.single { it.targetUnit == 16 }
            val noun = p.item(a.slots[1].itemId)!!
            compose.onNode(hasText(noun.promptLetter!!) and !hasClickAction()).performScrollTo().assertIsDisplayed()
            val wrong = noun.options.first { it != noun.answer }
            compose.onNodeWithTag("v2-review-option-1-$wrong").performScrollTo().performClick()
            waitTag("v2-review-feedback-1"); compose.onNodeWithTag("v2-review-feedback-1").assertTextEquals("Not the reviewed answer. ${noun.feedback}")
            compose.onNodeWithTag("v2-review-remediate-1").performScrollTo().performClick()
            waitTag("v2-example-ex-or-u15-imperative-2"); compose.onNodeWithTag("v2-example-ex-or-u15-imperative-2").performScrollTo().assertIsDisplayed()
            compose.onNodeWithText("used for multiple addressees or polite address to one person", substring = true).performScrollTo().assertIsDisplayed()
            waitTag("v2-review-practice"); compose.onNodeWithTag("v2-review-practice").performClick()
            val practice = reviewedGuidedPractice(p, "u15-st-rem-imperative")
            assertEquals(listOf("u15-st-guided-imperative-1"), practice)
            assertEquals(4, p.step("u15-st-rem-imperative")!!.second.exampleIds.size)
            assertNotEquals(noun.promptLetter, p.item("u15-g-imperative-0")!!.promptLetter)
            assertNotEquals(noun.answer, p.item("u15-g-imperative-0")!!.answer)
            val remediation = p.step("u15-st-rem-imperative")!!.second
            for (id in listOf("u15-st-teach-imperative-1", "u15-st-teach-imperative-contrast", "u15-st-teach-imperative-2", "u15-st-teach-imperative-3")) assertTrue(remediation.body.contains(p.step(id)!!.second.body))
            practice.forEach { step ->
                p.step(step)!!.second.itemIds.forEach { id ->
                    val i = p.item(id)!!; waitTag("v2-option-$id-${i.answer}")
                    compose.onNodeWithTag("v2-option-$id-${i.answer}").performScrollTo().performClick()
                }
            }
            waitTag("v2-return-to-check"); compose.onNodeWithTag("v2-return-to-check").performClick()
            waitTag("v2-review-count"); assertEquals(a, s.cumulativeReviewHistory().first.single { it.targetUnit == 16 })
            assertFalse(1 in completedReviewSlots(p, a, s.cumulativeReviewHistory().second))
            a.slots.forEachIndexed { index, slot ->
                val answer = p.item(slot.itemId)!!.answer
                compose.onNodeWithTag("v2-review-option-$index-$answer").performScrollTo().performClick()
                compose.waitForIdle()
            }
            waitTag("v2-review-gate"); compose.onNodeWithTag("v2-review-gate").performScrollTo().performClick()
            waitTag("v2-advance"); compose.onNodeWithTag("v2-advance").performScrollTo().assertIsEnabled().performClick()
            compose.waitUntil(30000) { compose.waitForIdle(); s.v2Profile().currentUnitNumber == 17 }
        }
    }

    @Test fun placementWithTaughtUnit1ButUntaughtUnit15RepairsTheMissingOwnerInsteadOfUnit1() {
        val p = pack()
        isolatedStore("case").use { s ->
            for (n in 1..2) { ordinary(p, s, n); s.v2CompleteUnit(n, "done") }
            s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 16, startingPoint = "RESUME_FROM_BOOK"))
            compose.setContent { V2App(s, p, {}) }
            compose.onNodeWithText("Course").performClick()
            waitTag("v2-course-intro"); compose.onNode(hasScrollToIndexAction()).performScrollToIndex(16)
            waitTag("v2-unit-row-16"); compose.onNodeWithTag("v2-unit-row-16").performClick()
            waitTag("v2-open-cumulative-review"); compose.onNodeWithTag("v2-open-cumulative-review").performScrollTo().performClick()
            waitTag("v2-review-insufficient"); compose.onNodeWithTag("v2-review-start").assertDoesNotExist()
            compose.onNodeWithTag("v2-review-repair").performScrollTo().assertTextEquals("Open earlier Unit 15 instruction and checks").performClick()
            waitTag("v2-unit-heading"); compose.onNodeWithText("Unit 15").assertIsDisplayed()
            assertFalse(s.cumulativeReviewHistory().first.any { it.targetUnit == 16 })
        }
    }

    @Test fun heldUnit16WriteRejectsReplacementEpochEvenWhenNewLearnerStillHasEligibleTeaching() {
        val p = pack()
        ready(p).use { s ->
            val entered = CompletableDeferred<Unit>(); val release = CompletableDeferred<Unit>()
            val api = object : V2Persistence by StoreV2Persistence(s, p) {
                override suspend fun openCumulativeReview(pack: V2CurriculumPack, targetUnit: Int) {
                    assertEquals(16, targetUnit); entered.complete(Unit); release.await()
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
            assertNotNull(createCumulativeReviewAssignment(p, s.v2LearnerView(), 16, "still-eligible", 0))
            val before = s.governedLearnerFingerprint(); release.complete(Unit)
            compose.waitUntil(30000) { compose.waitForIdle(); compose.onAllNodesWithText("Not saved. The previous record is unchanged.", substring = true).fetchSemanticsNodes().isNotEmpty() }
            assertEquals(before, s.governedLearnerFingerprint())
            assertFalse(s.cumulativeReviewHistory().first.any { it.targetUnit == 16 })
        }
    }

    private fun ready(p: V2CurriculumPack): LocalStore {
        check(p === sharedPack)
        val document = cachedReadyDocument ?: isolatedStore("seed").use { seed ->
            prepare(p, seed)
            seed.governedLearnerDocument()
        }.also { cachedReadyDocument = it }
        return isolatedStore("target").also { store ->
            assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(document, emptyList()))
            assertEquals((1..15).toSet(), currentCompletedUnitNumbers(p, store.v2LearnerView()))
            assertTrue(ordinaryUnitRequirements(p, p.unit(16)!!, store.v2LearnerView()).allowed)
            assertFalse(store.cumulativeReviewHistory().first.any { it.targetUnit == 16 })
        }
    }
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
        answer(p, s, ninth, 0); answer(p, s, ninth, 1)
        runBlocking { advanceFrom(p, s, 9) }
        ordinary(p, s, 10)
        val tenth = s.openCumulativeReview(p, 10, s.learnerMutationEpoch())!!
        answer(p, s, tenth, 0); answer(p, s, tenth, 1)
        runBlocking { advanceFrom(p, s, 10) }
        ordinary(p, s, 11)
        val eleventh = s.openCumulativeReview(p, 11, s.learnerMutationEpoch())!!
        answer(p, s, eleventh, 0); answer(p, s, eleventh, 1)
        runBlocking { advanceFrom(p, s, 11) }
        ordinary(p, s, 12)
        val twelfth = s.openCumulativeReview(p, 12, s.learnerMutationEpoch())!!
        answer(p, s, twelfth, 0); answer(p, s, twelfth, 1)
        runBlocking { advanceFrom(p, s, 12) }
        ordinary(p, s, 13)
        val thirteenth = s.openCumulativeReview(p, 13, s.learnerMutationEpoch())!!
        answer(p, s, thirteenth, 0); answer(p, s, thirteenth, 1)
        runBlocking { advanceFrom(p, s, 13) }
        ordinary(p, s, 14)
        val fourteenth = s.openCumulativeReview(p, 14, s.learnerMutationEpoch())!!
        answer(p, s, fourteenth, 0); answer(p, s, fourteenth, 1)
        runBlocking { advanceFrom(p, s, 14) }
        ordinary(p, s, 15)
        val fifteenth = s.openCumulativeReview(p, 15, s.learnerMutationEpoch())!!
        answer(p, s, fifteenth, 0); answer(p, s, fifteenth, 1)
        runBlocking { advanceFrom(p, s, 15) }
        ordinary(p, s, 16)
        assertEquals((1..15).toSet(), currentCompletedUnitNumbers(p, s.v2LearnerView()))
    }
    private fun ordinary(p: V2CurriculumPack, s: LocalStore, n: Int) {
        s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = n, currentSessionId = null))
        for (step in p.unit(n)!!.sessions.flatMap { it.steps }) {
            for (id in step.itemIds) s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, p.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "same-clock")!!)
            s.v2CompleteGovernedStep(step.id, governedStepCompletionDigest(p, s.v2LearnerView(), step.id)!!, "same-clock")
        }
        s.v2AttestBook(n, "same-clock")
    }
    private fun open(p: V2CurriculumPack, s: LocalStore) = s.openCumulativeReview(p, 16, s.learnerMutationEpoch())!!
    private fun answer(p: V2CurriculumPack, s: LocalStore, a: CumulativeReviewAssignment, index: Int, correct: Boolean = true) {
        val i = p.item(a.slots[index].itemId)!!
        assertTrue(s.answerCumulativeReview(p, a.id, index, if (correct) i.answer else i.options.first { it != i.answer }, s.learnerMutationEpoch()))
    }
    private fun advance(p: V2CurriculumPack, s: LocalStore): Boolean = runBlocking { advanceFrom(p, s, 16) }
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
    companion object { private var cachedReadyDocument: GovernedLearnerDocument? = null
        private val sharedPack by lazy {
        val root = File(if (File("app").isDirectory) "." else "..")
        V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) }
    } }
}
