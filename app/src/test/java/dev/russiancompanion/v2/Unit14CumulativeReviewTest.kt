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

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit14CumulativeReviewTest {
    @get:Rule val compose = createComposeRule()
    @After fun reset() {
        V2UiWork.blockForTests = null
        for (name in ownedDatabases) { context().deleteDatabase(name); File(context().filesDir, "recordings-$name").deleteRecursively() }
        ownedDatabases.clear()
    }
    private val ownedDatabases = mutableListOf<String>()
    private fun isolatedStore(label: String): LocalStore {
        val name = "unit14-$label-${System.nanoTime()}.db"; ownedDatabases += name
        return LocalStore.openNamed(context(), name)
    }

    @Test fun optionalReplaysStalePrecheckAndInjectedAdvanceFailureCannotCreateCompletion() {
        val p = pack()
        ready(p).use { s ->
            val a = open(p, s)
            a.slots.forEach { slot ->
                val i = p.item(slot.itemId)!!
                s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, i.id, i.answer, OPTIONAL_PRACTICE, practiceStepId("mixed"), "replay")!!)
            }
            assertFalse(cumulativeReviewComplete(p, s.v2LearnerView(), 14))
            a.slots.indices.forEach { answer(p, s, a, it) }
            // Current v2 requires every declared slot before testing stale answers and transactional rollback.
            a.slots.indices.drop(2).forEach { answer(p, s, a, it) }
            assertTrue(advancementDecision(p, p.unit(14)!!, s.v2LearnerView()).allowed)
            answer(p, s, a, 1, correct = false)
            assertTrue(runCatching { advance(p, s) }.isFailure)
            assertFalse(s.governedLearnerDocument().unitCompletions.any { it.unitNumber == 14 })
            (1..a.slots.lastIndex).forEach { answer(p, s, a, it) }
            s.failAdvanceAfterCompletionForTests = true
            assertTrue(runCatching { advance(p, s) }.isFailure)
            assertEquals(14, s.v2Profile().currentUnitNumber)
            assertFalse(s.governedLearnerDocument().unitCompletions.any { it.unitNumber == 14 })
            val wrong = p.copy(units = p.units.map { if (it.number == 14) it.copy(advancement = it.advancement!!.copy(requireDueReview = false)) else it })
            assertFalse(reviewAssignmentCurrent(wrong, a)); assertFalse(advancementDecision(wrong, wrong.unit(14)!!, s.v2LearnerView()).allowed)
            assertFalse(V2Validator.validate(wrong).ok)
            s.failAdvanceAfterCompletionForTests = false
            val next = p.unit(15)!!.sessions.first().id
            assertTrue(runCatching { runBlocking {
                s.v2AdvanceUnitIfCurrentAsync(s.learnerMutationEpoch(), 14, "packless", 15, next,
                    resumePositionDigest(p, next, 0)!!, GOVERNED_IDENTITY_SCHEMA)
            } }.isFailure)
            assertFalse(s.governedLearnerDocument().unitCompletions.any { it.unitNumber == 14 })
        }
    }

    @Test fun heldUnit14WriteRejectsReplacementEpochEvenWhenNewLearnerStillHasEligibleTeaching() {
        val p = pack()
        ready(p).use { s ->
            val entered = CompletableDeferred<Unit>(); val release = CompletableDeferred<Unit>()
            val api = object : V2Persistence by StoreV2Persistence(s, p) {
                override suspend fun openCumulativeReview(pack: V2CurriculumPack, targetUnit: Int) {
                    assertEquals(14, targetUnit); entered.complete(Unit); release.await()
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
            assertNotNull(createCumulativeReviewAssignment(p, s.v2LearnerView(), 14, "still-eligible", 0))
            val before = s.governedLearnerFingerprint(); release.complete(Unit)
            compose.waitUntil(30000) { compose.waitForIdle(); compose.onAllNodesWithText("Not saved. The previous record is unchanged.", substring = true).fetchSemanticsNodes().isNotEmpty() }
            assertEquals(before, s.governedLearnerFingerprint())
            assertFalse(s.cumulativeReviewHistory().first.any { it.targetUnit == 14 })
        }
    }

    private fun ready(p: V2CurriculumPack): LocalStore {
        // Cache only the shared current source; historical packs need their own genuine fixture.
        val document = if (p === sharedPack) {
            cachedReadyDocument ?: isolatedStore("seed").use { seed ->
                prepare(p, seed)
                seed.governedLearnerDocument()
            }.also { cachedReadyDocument = it }
        } else isolatedStore("historical-seed").use { seed ->
            prepare(p, seed)
            seed.governedLearnerDocument()
        }
        return isolatedStore("target").also { store ->
            assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(document, emptyList()))
            assertEquals((1..13).toSet(), currentCompletedUnitNumbers(p, store.v2LearnerView()))
            assertTrue(ordinaryUnitRequirements(p, p.unit(14)!!, store.v2LearnerView()).allowed)
            assertFalse(store.cumulativeReviewHistory().first.any { it.targetUnit == 14 })
        }
    }
    private fun prepare(p: V2CurriculumPack, s: LocalStore) {
        for (n in 1..2) { ordinary(p, s, n); s.v2CompleteUnit(n, "done") }
        ordinary(p, s, 3)
        val a = s.openUnit3CumulativeReview(p, s.learnerMutationEpoch())!!
        a.slots.indices.forEach { answer(p, s, a, it) }
        runBlocking { advanceFrom(p, s, 3) }
        ordinary(p, s, 4)
        val previous = s.openCumulativeReview(p, 4, s.learnerMutationEpoch())!!
        previous.slots.indices.forEach { answer(p, s, previous, it) }
        runBlocking { advanceFrom(p, s, 4) }
        ordinary(p, s, 5)
        val fifth = s.openCumulativeReview(p, 5, s.learnerMutationEpoch())!!
        fifth.slots.indices.forEach { answer(p, s, fifth, it) }
        runBlocking { advanceFrom(p, s, 5) }
        ordinary(p, s, 6)
        val sixth = s.openCumulativeReview(p, 6, s.learnerMutationEpoch())!!
        sixth.slots.indices.forEach { answer(p, s, sixth, it) }
        runBlocking { advanceFrom(p, s, 6) }
        ordinary(p, s, 7)
        val seventh = s.openCumulativeReview(p, 7, s.learnerMutationEpoch())!!
        seventh.slots.indices.forEach { answer(p, s, seventh, it) }
        runBlocking { advanceFrom(p, s, 7) }
        ordinary(p, s, 8)
        val eighth = s.openCumulativeReview(p, 8, s.learnerMutationEpoch())!!
        eighth.slots.indices.forEach { answer(p, s, eighth, it) }
        runBlocking { advanceFrom(p, s, 8) }
        ordinary(p, s, 9)
        val ninth = s.openCumulativeReview(p, 9, s.learnerMutationEpoch())!!
        ninth.slots.indices.forEach { answer(p, s, ninth, it) }
        runBlocking { advanceFrom(p, s, 9) }
        ordinary(p, s, 10)
        val tenth = s.openCumulativeReview(p, 10, s.learnerMutationEpoch())!!
        tenth.slots.indices.forEach { answer(p, s, tenth, it) }
        runBlocking { advanceFrom(p, s, 10) }
        ordinary(p, s, 11)
        val eleventh = s.openCumulativeReview(p, 11, s.learnerMutationEpoch())!!
        eleventh.slots.indices.forEach { answer(p, s, eleventh, it) }
        runBlocking { advanceFrom(p, s, 11) }
        ordinary(p, s, 12)
        val twelfth = s.openCumulativeReview(p, 12, s.learnerMutationEpoch())!!
        twelfth.slots.indices.forEach { answer(p, s, twelfth, it) }
        runBlocking { advanceFrom(p, s, 12) }
        ordinary(p, s, 13)
        val thirteenth = s.openCumulativeReview(p, 13, s.learnerMutationEpoch())!!
        thirteenth.slots.indices.forEach { answer(p, s, thirteenth, it) }
        runBlocking { advanceFrom(p, s, 13) }
        ordinary(p, s, 14)
        assertEquals((1..13).toSet(), currentCompletedUnitNumbers(p, s.v2LearnerView()))
    }
    private fun ordinary(p: V2CurriculumPack, s: LocalStore, n: Int) {
        s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = n, currentSessionId = null))
        for (step in p.unit(n)!!.sessions.flatMap { it.steps }) {
            for (id in step.itemIds) s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, p.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "same-clock")!!)
            s.v2CompleteGovernedStep(step.id, governedStepCompletionDigest(p, s.v2LearnerView(), step.id)!!, "same-clock")
        }
        s.v2AttestBook(n, "same-clock")
    }
    private fun open(p: V2CurriculumPack, s: LocalStore) = s.openCumulativeReview(p, 14, s.learnerMutationEpoch())!!
    private fun answer(p: V2CurriculumPack, s: LocalStore, a: CumulativeReviewAssignment, index: Int, correct: Boolean = true) {
        val i = p.item(a.slots[index].itemId)!!
        assertTrue(s.answerCumulativeReview(p, a.id, index, if (correct) i.answer else i.options.first { it != i.answer }, s.learnerMutationEpoch()))
    }
    private fun advance(p: V2CurriculumPack, s: LocalStore): Boolean = runBlocking { advanceFrom(p, s, 14) }
    private suspend fun advanceFrom(p: V2CurriculumPack, s: LocalStore, n: Int): Boolean {
        val session = p.unit(n + 1)!!.sessions.first().id
        return s.v2AdvanceUnitIfCurrentAsync(s.learnerMutationEpoch(), n, "advance", n + 1, session, resumePositionDigest(p, session, 0)!!, GOVERNED_IDENTITY_SCHEMA, p)
    }
    private fun waitTag(tag: String) = compose.waitUntil(conditionDescription = tag, timeoutMillis = 30000) {
        compose.waitForIdle(); compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
    }
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    private fun pack() = sharedPack
    companion object { private var cachedReadyDocument: GovernedLearnerDocument? = null
        private val sharedPack by lazy {
        val root = File(if (File("app").isDirectory) "." else "..")
        V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) }
    } }
}
