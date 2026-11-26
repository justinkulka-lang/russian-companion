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
        // T15f (owner decision D6c): commentary about what the choices ask and do not demonstrate is gone.
        assertFalse(body.contains("does not demonstrate"))
        assertFalse(body.contains("choices below ask"))
        // T15g (D18d): the "from these choices" commentary becomes a plain instruction.
        assertFalse(body.contains("do not infer a general adverb-formation rule"))
        assertTrue(body.contains("Learn each adjective and adverb pair as it is taught."))
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
