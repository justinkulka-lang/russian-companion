package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class Unit15AspectPreparationTest {
    @Test fun bothPairsAreTaughtBeforePracticeAndRepairRestoresAllThreeCardsAndFourForms() {
        val p=pack();val s=p.unit(15)!!.sessions.single { it.id=="u15-s-aspect-consolidate" }
        val teaching=s.steps.filter { it.kind=="TEACH" };val rem=p.step("u15-st-rem-aspect-consolidate")!!.second
        // T16e: 3 -> 4 teaching cards; the do/make imperative card (u15-st-teach-aspect-consolidate-4) teaches the typed keys before their checks.
        assertEquals(4,teaching.size)
        assertEquals(teaching,s.steps.takeWhile { it.kind!="GUIDED" }.filter { it.kind=="TEACH" })
        assertEquals(teaching.joinToString("\n\n") { it.body },rem.body)
        // T16e: the four infinitive examples -> plus the four do/make imperative examples of the new card (verbs.csv:19, :20).
        assertEquals(listOf("ex-u15-aspect-consolidate-0","ex-u15-aspect-consolidate-1","ex-u15-aspect-consolidate-2","ex-u15-aspect-consolidate-3","ex-or-u15-aspect-consolidate-4","ex-or-u15-aspect-consolidate-5","ex-or-u15-aspect-consolidate-6","ex-or-u15-aspect-consolidate-7"),rem.exampleIds)
        assertEquals(rem.exampleIds,teaching.flatMap { it.exampleIds }.distinct())
        assertEquals(listOf("u15-st-guided-aspect-consolidate-1"),reviewedGuidedPractice(p,rem.id))
    }
    @Test fun preparationDoesNotClaimToAssessUnqualifiedRequestContexts() {
        val p=pack();val o=p.unit(15)!!.objectives.single { it.id=="obj-u15-aspect-consolidate" }
        // T16e: two checks -> plus eight typed checks of taught pair and imperative forms (A2-23); none is a request context.
        assertEquals(listOf("u15-i-aspect-consolidate-1","u15-i-aspect-consolidate-2","u15-i-type-govorit","u15-i-type-skazat","u15-i-type-delat","u15-i-type-sdelat","u15-i-type-delai","u15-i-type-delaite","u15-i-type-sdelai","u15-i-type-sdelaite"),o.checkItemIds)
        assertFalse(p.items.any { it.id.contains("request-aspect-") })
        // T16e (owner decision D4): "does not demonstrate that contextual skill" -> the book-directed gap sentence; the unit still claims no request-context assessment.
        assertTrue(p.step("u15-st-teach-aspect-consolidate-3")!!.second.body.contains("For choosing the aspect of a request, use the matching lesson in your book."))
        assertEquals(6,p.unit(15)!!.advancement!!.minimumDistinctChecks)
    }
    @Test fun overlappingEnglishLabelsRejectWithoutThrowingAndDoNotBecomeNativeEvidence() {
        val p = pack()
        val id = "u15-i-aspect-consolidate-1"
        val item = p.item(id)!!
        val options = listOf("imperfective", "perfective")
        val bad = item.copy(answer = "perfective", options = options,
            assessment = item.assessment!!.copy(contrastMembers = options))
        val validation = V2Validator.validate(p.copy(items = p.items.map { if (it.id == id) bad else it }))
        assertFalse(validation.ok)
        assertTrue(validation.errors.joinToString("\n"), validation.errors.any {
            it.contains(id) && (it.contains("response type") || it.contains("explicit target"))
        })
    }

    private fun pack()=V2PackLoader.parse(File(if(File("app").isDirectory)"." else "..","app/src/main/assets/content/v2-curriculum.json").readText())
}
