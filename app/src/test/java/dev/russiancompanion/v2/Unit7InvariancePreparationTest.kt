package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class Unit7InvariancePreparationTest {
    @Test fun ruleAndEnglishScaffoldingAreAvailableBeforeFirstPractice() {
        val p = pack()
        val session = p.unit(7)!!.sessions.single { it.id == "u7-s-adverb-invariance" }
        val teaching = session.steps.filter { it.kind == "TEACH" }
        assertEquals(teaching, session.steps.takeWhile { it.kind != "GUIDED" }.filter { it.kind == "TEACH" })
        val body = teaching.joinToString("\n") { it.body }
        assertTrue(body.contains("do not decline or conjugate"))
        assertTrue(body.contains("does not mean that comparative forms cannot exist"))
        assertTrue(body.contains("wording task does not demonstrate applying invariance"))
        assertEquals(listOf("u7-i-adverb-invariance-1", "u7-i-adverb-invariance-2"),
            p.unit(7)!!.objectives.single { it.id == "obj-u7-adverb-invariance" }.checkItemIds)
    }

    @Test fun repairRestoresTheWholeExplanationAndBothRetainedExamples() {
        val p = pack()
        val teaching = p.unit(7)!!.sessions.single { it.id == "u7-s-adverb-invariance" }.steps.filter { it.kind == "TEACH" }
        val repair = p.step("u7-st-rem-adverb-invariance")!!.second
        assertEquals(teaching.joinToString("\n\n") { it.body }, repair.body)
        assertEquals(listOf("ex-u7-adverb-invariance-0", "ex-u7-adverb-invariance-1"), repair.exampleIds)
        assertEquals(listOf("u7-st-guided-adverb-invariance-1"), reviewedGuidedPractice(p, repair.id))
    }

    private fun pack() = V2PackLoader.parse(File(if (File("app").isDirectory) "." else "..",
        "app/src/main/assets/content/v2-curriculum.json").readText())
}
