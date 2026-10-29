package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.RussianCompanionApplication
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class LiteracyCorrectionCompatibilityTest {
    @Test fun patchPreservesAllKeysAndAllOtherCourseContentWithinAnExactWhitelist() {
        val old = before; val p = current
        assertEquals(old, p.copy(items = old.items, units = old.units))
        assertEquals(old.items.map { it.id to it.answer }, p.items.map { it.id to it.answer })
        assertEquals(old.units[0], p.units[0])
        assertEquals(old.units.drop(2), p.units.drop(2))
        val oldUnit = old.unit(2)!!; val unit = p.unit(2)!!
        val restoredSessions = unit.sessions.map { session -> session.copy(steps = session.steps.map { step ->
            if (step.id == "u2-st-review") {
                val oldStep = old.step(step.id)!!.second
                step.copy(body = oldStep.body, exampleIds = oldStep.exampleIds)
            } else step
        }) }
        assertEquals(oldUnit, unit.copy(sessions = restoredSessions, objectives = oldUnit.objectives))
        val objective = unit.objectives.single { it.id == "obj-u2-review" }
        assertEquals(oldUnit.objectives.single { it.id == objective.id }, objective.copy(exampleIds = oldUnit.objectives.single { it.id == objective.id }.exampleIds))
        var revised = 0
        for (item in p.items) {
            val prior = old.item(item.id)!!
            val restored = if (item.id in variants) item.copy(kind = prior.kind, prompt = prior.prompt,
                promptLetter = prior.promptLetter, options = prior.options, feedback = prior.feedback,
                distractorRationales = prior.distractorRationales) else item.copy(distractorRationales = prior.distractorRationales)
            assertEquals(item.id, prior, restored)
            if (item != prior) { revised++; assertTrue(item.id.startsWith("u1-") || item.id.startsWith("u2-")) }
        }
        assertEquals(23, revised)
        for (n in 1..30) {
            val a = V2Validator.unitContentReviewDigest(old, n); val b = V2Validator.unitContentReviewDigest(p, n)
            if (n <= 2) assertNotEquals("Unit$n", a, b) else assertEquals("Unit$n", a, b)
        }
    }

    @Test fun allTenHistoriesReopenUnchangedButObsoleteFoundationsAndDependentReviewCannotGrantCredit() {
        val context = context()
        for (file in captures.listFiles()!!.filter { it.extension == "zip" }.sortedBy { it.name }) {
            val document = read(file, context)
            val name = "literacy-new-${System.nanoTime()}.db"
            fun check(store: LocalStore) {
                assertEquals(file.name, document, store.governedLearnerDocument())
                assertEquals(document.reviewAssignments to document.reviewAnswers, store.cumulativeReviewHistory())
                assertEquals(emptySet<Int>(), currentCompletedUnitNumbers(current, store.v2LearnerView()))
                assertFalse(ordinaryUnitRequirements(current, current.unit(1)!!, store.v2LearnerView()).allowed)
                assertNull(currentCumulativeAssignment(current, store.v2LearnerView(), 3))
                val ids = current.unit(1)!!.objectives.flatMap { it.checkItemIds }.toSet()
                assertTrue(creditItemIds(current, store.v2LearnerView()).intersect(ids).isEmpty())
            }
            try {
                LocalStore.openNamed(context, name).use { store ->
                    assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(document, emptyList())); check(store)
                }
                LocalStore.openNamed(context, name).use { check(it) }
            } finally { context.deleteDatabase(name); File(context.filesDir, "recordings-$name").deleteRecursively() }
        }
        assertEquals(10, captures.listFiles()!!.count { it.extension == "zip" })
    }

    @Test fun freshCorrectedWorkReestablishesCompletionWithoutRelabellingTheLegacyReview() {
        val context = context(); val original = read(File(captures, "COMPLETED_UNIT3.zip"), context)
        LocalStore.openInMemory(context).use { store ->
            assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(original, emptyList()))
            for (n in 1..3) {
                store.v2SaveProfile(store.v2Profile().copy(currentUnitNumber = n, currentSessionId = null))
                Unit8PresentFixtures.ordinary(store, n, current)
                assertTrue(ordinaryUnitRequirements(current, current.unit(n)!!, store.v2LearnerView()).allowed)
                if (n == 3) {
                    val task = store.openCumulativeReview(current, 3, store.learnerMutationEpoch())!!
                    assertFalse(original.reviewAssignments.any { it.id == task.id })
                    assertEquals(2, task.schema) // No new schema or review policy.
                    task.slots.forEach { assertEquals(V2Validator.unitContentReviewDigest(current, it.ownerUnit), it.unitDigest) }
                    task.slots.indices.forEach { index ->
                        assertTrue(store.answerCumulativeReview(current, task.id, index,
                            current.item(task.slots[index].itemId)!!.answer, store.learnerMutationEpoch()))
                    }
                }
                assertTrue(runBlocking {
                    val next = current.unit(n + 1)!!.sessions.first().id
                    store.v2AdvanceUnitIfCurrentAsync(store.learnerMutationEpoch(), n, "literacy-fresh", n + 1,
                        next, resumePositionDigest(current, next, 0)!!, GOVERNED_IDENTITY_SCHEMA, current)
                })
            }
            assertEquals(setOf(1, 2, 3), currentCompletedUnitNumbers(current, store.v2LearnerView()))
            original.reviewAssignments.forEach { assertTrue(store.cumulativeReviewHistory().first.contains(it)) }
            original.reviewAnswers.forEach { assertTrue(store.cumulativeReviewHistory().second.contains(it)) }
        }
    }

    @Test fun revisedTasksUseActualLowercaseCuesAndThreeCyrillicAlternativesWithLinkedExamples() {
        val step = current.step("u2-st-review")!!.second
        for (id in variants) {
            val item = current.item(id)!!
            val cue = item.promptLetter!!
            assertEquals(cue.lowercase(), cue); assertNotEquals(cue.uppercase(), cue)
            assertEquals("PRINT_PAIR", item.kind)
            assertEquals(3, item.options.size)
            assertEquals(3, item.options.distinct().size)
            assertTrue(item.options.all { it.startsWith("Cyrillic ") && it.last() in 'А'..'Я' })
            assertEquals(cue.uppercase(), item.answer.takeLast(1))
            assertTrue(step.exampleIds.map { current.example(it)!!.display }.any { it.contains(cue) && it.contains(cue.uppercase()) })
            assertNotEquals(before.item(id)!!.promptLetter, item.promptLetter)
        }
    }

    private fun context(): Context = ApplicationProvider.getApplicationContext<Context>().also {
        (it.applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
    }
    private fun read(file: File, context: Context): GovernedLearnerDocument {
        val stage = File(context.cacheDir, "literacy-read-${System.nanoTime()}")
        try {
            val result = file.inputStream().use { readGovernedBackup(it, "dev.russiancompanion", current.packId, current.packVersion, stage) }
            assertTrue(result is BackupReadResult.Valid)
            return (result as BackupReadResult.Valid).backup.document
        } finally { stage.deleteRecursively() }
    }
    companion object {
        private val root = File(if (File("app").isDirectory) "." else "..")
        private val current by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) } }
        private val before by lazy { RecordedIntakeCorrectionScope.load(root, "f653da03d1bc657e8acaeef52141552c531e42ce") }
        private val captures = File(root, "audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-literacy-family-baseline-2026-10-29/SCENARIOS")
        private val variants = setOf("u2-i-в-not-b", "u2-i-у-not-y", "u2-i-х-not-x", "u2-i-я-identity")
    }
}
