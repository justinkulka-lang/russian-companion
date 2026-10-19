package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class Unit8ExistenceSequenceTest {
    @Test fun activeAvailabilityChainDoesNotTeachOrScoreLaterAbsenceGrammar() {
        val p = pack()
        val u = p.unit(8)!!
        val o = u.objectives.single { it.id == "obj-u8-availability" }
        val session = u.sessions.single { it.id == "u8-s-availability" }
        val text = (listOf(o.title, session.title) + session.steps.flatMap { listOf(it.title,it.body) } +
            session.steps.flatMap { it.itemIds }.distinct().map { p.item(it)!! }.flatMap {
                listOf(it.prompt,it.promptLetter.orEmpty(),it.answer,it.feedback) + it.options + it.distractorRationales +
                    listOf(it.assessment!!.testedFeature,it.assessment.whyAnswerDemonstratesObjective) + it.assessment.distractorDifference
            }).joinToString("\n")
        assertFalse("Unit8 must retain positive existence without analyzed absence/genitive: $text",
            Regex("genitive|absence|absent|нет", RegexOption.IGNORE_CASE).containsMatchIn(text))
        assertEquals(3,o.checkItemIds.size)
        assertEquals(18,u.advancement.minimumDistinctChecks)
    }
    @Test fun fullRepairRestoresPositiveInstructionAndOnlyTwoPositiveExamples() {
        val p=pack(); val session=p.unit(8)!!.sessions.single { it.id=="u8-s-availability" }
        val teaching=session.steps.filter { it.kind=="TEACH" }
        val rem=session.steps.single { it.kind=="REMEDIATE" }
        assertEquals(3,teaching.size)
        assertEquals(teaching.joinToString("\n\n") { it.body },rem.body)
        assertEquals(listOf("ex-u8-availability-0","ex-u8-availability-1"),rem.exampleIds)
        assertEquals(rem.exampleIds,p.unit(8)!!.objectives.single { it.id=="obj-u8-availability" }.exampleIds)
        assertEquals(rem.exampleIds,teaching.flatMap { it.exampleIds }.distinct())
        assertTrue(rem.exampleIds.all { "нет" !in p.examples.single { e->e.id==it }.display })
    }
    private fun pack()=V2PackLoader.parse(File(if(File("app").isDirectory)"." else "..","app/src/main/assets/content/v2-curriculum.json").readText())
}
