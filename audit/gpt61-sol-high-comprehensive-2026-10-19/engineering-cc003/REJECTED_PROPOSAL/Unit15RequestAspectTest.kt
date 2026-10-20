package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class Unit15RequestAspectTest {
    @Test fun consolidationRequiresIndependentContextEvidenceBeyondPairRecall() {
        val p=pack();val o=p.unit(15)!!.objectives.single { it.id=="obj-u15-aspect-consolidate" }
        val checks=o.checkItemIds.map { p.item(it)!! }
        val contexts=checks.filter { it.assessment!!.testedFeature.startsWith("BOUNDED_REQUEST_ASPECT_") }
        assertEquals("Single request, repetition and ordinary prohibition need distinct context checks",3,contexts.size)
        assertEquals(setOf("BOUNDED_REQUEST_ASPECT_SINGLE","BOUNDED_REQUEST_ASPECT_REPEAT","BOUNDED_REQUEST_ASPECT_PROHIBIT"),contexts.map { it.assessment!!.testedFeature }.toSet())
        assertTrue(contexts.all { it.assessment!!.independent && it.assessment.cueLanguage=="en" && it.options.toSet()==setOf("imperfective","perfective") })
        assertEquals(3,contexts.map { it.promptLetter }.distinct().size)
        assertEquals(setOf("u15-i-aspect-consolidate-1","u15-i-aspect-consolidate-2"),checks.filter { it !in contexts }.map { it.id }.toSet())
    }
    @Test fun allPairAndContextTeachingPrecedesPracticeAndFullRepairRestoresIt() {
        val p=pack();val s=p.unit(15)!!.sessions.single { it.id=="u15-s-aspect-consolidate" }
        val teaching=s.steps.filter { it.kind=="TEACH" };val rem=p.step("u15-st-rem-aspect-consolidate")!!.second
        assertEquals(6,teaching.size)
        assertEquals(teaching,s.steps.takeWhile { it.kind!="GUIDED" }.filter { it.kind=="TEACH" })
        assertEquals(teaching.joinToString("\n\n") { it.body },rem.body)
        assertEquals(teaching.flatMap { it.exampleIds }.distinct(),rem.exampleIds)
        assertEquals(8,rem.exampleIds.size)
        assertEquals(listOf("u15-st-guided-aspect-consolidate-1","u15-st-guided-request-aspect"),reviewedGuidedPractice(p,rem.id))
    }
    private fun pack()=V2PackLoader.parse(File(if(File("app").isDirectory)"." else "..","app/src/main/assets/content/v2-curriculum.json").readText())
}
