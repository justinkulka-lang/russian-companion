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
        private val variants = setOf("u2-i-в-not-b", "u2-i-у-not-y", "u2-i-х-not-x", "u2-i-я-identity")
    }
}
