package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class FragmentGlossTeachingTest {
    @Test fun threeTeachingBodiesDescribePhrasesWithoutAbsentPredicates() {
        val p = FragmentGlossFixture.current
        val bad = FragmentGlossFixture.teachingIds.filter { id ->
            val body = p.step(id)!!.second.body
            body.contains("этих женщин — I like these women") || body.contains("Эти женщины — These women like me") ||
                body.trim() == "I like these women."
        }
        assertEquals("Obsolete whole-sentence meanings on displayed fragments", emptyList<String>(), bad)
        assertTrue(p.step("u7-st-teach-objectadj-3")!!.second.body.contains("accusative or genitive"))
        assertTrue(p.step("u7-st-teach-objectadj-3")!!.second.body.contains("not a complete sentence"))
        assertTrue(p.step("u10-st-teach-animate-1")!!.second.body.contains("nominative plural"))
        assertTrue(p.step("u10-st-teach-animate-1")!!.second.body.contains("no predicate"))
        assertTrue(p.step("u10-st-teach-animate-2")!!.second.body.contains("only the noun phrases"))
        assertEquals("these women, accusative or genitive plural, context not a full sentence", p.example("ex-u7-objectadj-3")!!.note)
        assertEquals(p.example("ex-u7-objectadj-3")!!.note, p.example("ex-u10-animate-0")!!.note)
        assertEquals("these women, nominative plural", p.example("ex-u10-animate-1")!!.note)
    }

    @Test fun bothRepairsRestoreCompleteTeachingAndAllSevenExamples() {
        val p = FragmentGlossFixture.current
        for ((number, stem) in listOf(7 to "objectadj", 10 to "animate")) {
            val session = p.unit(number)!!.sessions.single { it.id == "u$number-s-$stem" }
            val rem = p.step("u$number-st-rem-$stem")!!.second
            val objective = p.unit(number)!!.objectives.single { it.id == "obj-u$number-$stem" }
            assertEquals(session.steps.filter { it.kind == "TEACH" }.joinToString("\n\n") { it.body }, rem.body)
            assertEquals(objective.exampleIds, rem.exampleIds)
            assertEquals(7, rem.exampleIds.size)
            assertEquals(objective.practiceStepIds, reviewedGuidedPractice(p, rem.id))
            assertFalse(rem.body.contains("этих женщин — I like these women"))
        }
    }

    private fun context(): Context = ApplicationProvider.getApplicationContext()
}

internal object FragmentGlossFixture {
    val root get() = File(if (File("app").isDirectory) "." else "..")
    val teachingIds = listOf("u7-st-teach-objectadj-3", "u10-st-teach-animate-1", "u10-st-teach-animate-2")
    val current by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) } }
    fun record(s: LocalStore, p: V2CurriculumPack, id: String, selected: String) {
        val at = p.units.flatMap { it.sessions }.flatMap { it.steps }.single { id in it.itemIds }
        s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, selected, PRESCRIBED_CHECK, at.id,
            "row-${s.v2Attempts().size}")!!)
    }
}
