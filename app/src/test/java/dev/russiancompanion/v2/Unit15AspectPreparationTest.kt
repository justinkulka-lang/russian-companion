package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class Unit15AspectPreparationTest {
    @Test fun bothPairsAreTaughtBeforePracticeAndRepairRestoresAllThreeCardsAndFourForms() {
        val p=pack();val s=p.unit(15)!!.sessions.single { it.id=="u15-s-aspect-consolidate" }
        val teaching=s.steps.filter { it.kind=="TEACH" };val rem=p.step("u15-st-rem-aspect-consolidate")!!.second
        assertEquals(3,teaching.size)
        assertEquals(teaching,s.steps.takeWhile { it.kind!="GUIDED" }.filter { it.kind=="TEACH" })
        assertEquals(teaching.joinToString("\n\n") { it.body },rem.body)
        assertEquals(listOf("ex-u15-aspect-consolidate-0","ex-u15-aspect-consolidate-1","ex-u15-aspect-consolidate-2","ex-u15-aspect-consolidate-3"),rem.exampleIds)
        assertEquals(rem.exampleIds,teaching.flatMap { it.exampleIds }.distinct())
        assertEquals(listOf("u15-st-guided-aspect-consolidate-1"),reviewedGuidedPractice(p,rem.id))
    }
    @Test fun preparationDoesNotClaimToAssessUnqualifiedRequestContexts() {
        val p=pack();val o=p.unit(15)!!.objectives.single { it.id=="obj-u15-aspect-consolidate" }
        assertEquals(listOf("u15-i-aspect-consolidate-1","u15-i-aspect-consolidate-2"),o.checkItemIds)
        assertFalse(p.items.any { it.id.contains("request-aspect-") })
        assertTrue(p.step("u15-st-teach-aspect-consolidate-3")!!.second.body.contains("does not demonstrate that contextual skill"))
        assertEquals(6,p.unit(15)!!.advancement!!.minimumDistinctChecks)
    }
    private fun pack()=V2PackLoader.parse(File(if(File("app").isDirectory)"." else "..","app/src/main/assets/content/v2-curriculum.json").readText())
}
