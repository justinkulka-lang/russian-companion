package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class Unit24NegationInstructionTest {
    @Test fun allNegationContrastsAreExplainedBeforePracticeAndRepeatedAfterAMiss() {
        val p=pack()
        for((slug,count) in mapOf("verbal-ne" to 4,"absence" to 3,"alternation" to 2,"stand-ins" to 3,"concord" to 2,"word-order-contrast" to 2,"placement" to 2)) {
            val s=p.unit(24)!!.sessions.single { it.id=="u24-s-$slug" };val teaches=s.steps.takeWhile { it.kind!="GUIDED" }.filter { it.kind=="TEACH" }
            assertEquals(slug,count,teaches.size);val body=teaches.joinToString("\n") { it.body }
            p.items.filter { it.objectiveId=="obj-u24-$slug" }.forEach { i ->
                assertTrue("${i.id} cue",body.contains(i.promptLetter!!));i.options.forEach { assertTrue("${i.id} option $it",body.contains(it)) }
            }
            val rem=s.steps.single { it.kind=="REMEDIATE" };assertEquals(teaches.joinToString("\n\n") { it.body },rem.body);assertEquals(teaches.flatMap { it.exampleIds }.distinct(),rem.exampleIds)
        }
    }
    @Test fun numberAbsenceConcordAndPersuasionContextHaveDistinctExplanations() {
        val p=pack();fun text(slug:String)=p.unit(24)!!.sessions.single { it.id=="u24-s-$slug" }.steps.filter { it.kind=="TEACH" }.joinToString("\n") { it.body }
        val example=p.examples.single { it.id=="ex-u24-alternation-0" };val item=p.item("u24-i-alternation-2")!!
        assertEquals(item.promptLetter,example.display);assertEquals(item.answer,example.note);assertEquals(item.sourceId,example.sourceId)
        assertTrue(text("alternation").contains("not unambiguous genitive plural"));assertTrue(text("alternation").contains("not a mechanical rule"))
        assertTrue(text("absence").contains("pronunciation/transliteration label, not an English meaning"))
        assertTrue(text("stand-ins").contains("do not uniquely identify genitive"))
        assertTrue(text("concord").contains("one overall negative statement"))
        assertTrue(text("word-order-contrast").contains("Intonation and discourse context"))
        assertTrue(p.item("u24-i-word-order-contrast-1")!!.prompt.contains("will do it anyway"))
        assertTrue(p.item("u24-i-word-order-contrast-2")!!.prompt.contains("would never agree"))
        assertTrue(text("placement").contains("does not by itself identify that other person"))
    }
    @Test fun existingKeysStayBoundAndSourceAndReviewRestrictionsRemainClosed() {
        val p=pack();val items=p.items.filter { it.objectiveId.startsWith("obj-u24-") };assertEquals(21,items.size)
        for(i in items) {
            if(i.assessment!!.independent)assertTrue(i.id,V2Validator.featureValid(p,i))
            assertTrue(i.id,i.feedback.contains(i.answer));for(n in 1 until i.options.size)assertTrue(i.id,i.distractorRationales[n].contains(i.options[n]))
        }
        for(id in listOf("u24-i-alternation-2","u24-i-stand-ins-1","u24-i-concord-1","u24-i-placement-1")) {
            val i=p.item(id)!!;val bad=i.copy(answer=i.options[1]);assertFalse(id,V2Validator.featureValid(p.copy(items=p.items.map { if(it.id==id)bad else it }),bad))
        }
        val records=p.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u24-instruction-") };assertEquals(119,records.size);records.forEach { assertContentAuditResearch(it) }
        val result=V2Validator.validate(p);assertTrue(result.errors.joinToString("\n"),result.ok);assertEquals("DEVELOPMENT",p.unit(24)!!.installState)
    }
    private fun pack():V2CurriculumPack=V2PackLoader.parse((System.getProperty("unit24.instruction.baseline")?.let(::File)?:File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json")).readText())
}
