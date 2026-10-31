package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class Unit25NameInstructionTest {
    @Test fun allNameAndCourtesyContrastsAreTaughtBeforePracticeAndRepeatedAfterAMiss() {
        val p=pack()
        for(slug in listOf("name-structure","diminutives","courtesy","name-question","nickname-rule","address","documented-name-form")) {
            val s=p.unit(25)!!.sessions.single { it.objectiveIds==listOf("obj-u25-$slug") }
            val teaches=s.steps.takeWhile { it.kind!="GUIDED" }.filter { it.kind=="TEACH" }
            assertEquals(slug,3,teaches.size);val body=teaches.joinToString("\n") { it.body }
            p.items.filter { it.objectiveId=="obj-u25-$slug" }.forEach { i ->
                assertTrue("${i.id} cue",body.contains(i.promptLetter!!));i.options.forEach { assertTrue("${i.id} option $it",body.contains(it)) }
            }
            val rem=s.steps.single { it.kind=="REMEDIATE" }
            assertEquals(teaches.joinToString("\n\n") { it.body },rem.body)
            assertEquals(teaches.flatMap { it.exampleIds }.distinct(),rem.exampleIds)
        }
    }
    @Test fun questionsDoNotRevealNameClassificationAndInstructionExplainsTheLimits() {
        val p=pack();fun text(slug:String)=p.unit(25)!!.sessions.single { it.objectiveIds==listOf("obj-u25-$slug") }.steps.filter { it.kind=="TEACH" }.joinToString("\n") { it.body }
        for(id in listOf("u25-g-name-form-1","u25-i-name-form-1","u25-i-name-form-2")) {
            val i=p.item(id)!!;assertTrue(i.prompt,i.prompt.startsWith("Which English"));assertFalse(i.prompt,i.prompt.contains("full-form"));assertFalse(i.prompt,i.prompt.contains("short-form"));assertFalse(i.prompt,i.prompt.contains("disparaging"))
        }
        assertTrue(text("name-structure").contains("surname is not shown"))
        assertTrue(text("name-question").contains("must be followed by a name"))
        assertTrue(text("courtesy").contains("polite address to one person"))
        assertTrue(text("nickname-rule").contains("not proof that every use"))
        assertTrue(text("nickname-rule").contains("meaning from the short form"))
        assertTrue(text("address").contains("not every possible setting"))
        assertTrue(text("documented-name-form").contains("do not assess free productive name formation"))
        p.items.filter { it.objectiveId.startsWith("obj-u25-") }.forEach { assertFalse(it.id,it.prompt.contains("frozen",ignoreCase=true));assertFalse(it.id,it.prompt.contains("written for")) }
    }
    @Test fun keysStayBoundAndNameFormationAndReleaseRestrictionsStayClosed() {
        val p=pack();val items=p.items.filter { it.objectiveId.startsWith("obj-u25-") };assertEquals(23,items.size)
        for(i in items) {
            if(i.assessment!!.independent)assertTrue(i.id,V2Validator.featureValid(p,i))
            assertTrue(i.id,i.feedback.contains(i.answer));for(n in 1 until i.options.size)assertTrue(i.id,i.distractorRationales[n].contains(i.options[n]))
        }
        for(id in listOf("u25-i-name-form-1","u25-i-name-form-2","u25-i-name-form-3","u25-i-address-1")) {
            val i=p.item(id)!!;val bad=i.copy(answer=i.options[1]);assertFalse(id,V2Validator.featureValid(p.copy(items=p.items.map { if(it.id==id)bad else it }),bad))
        }
        val records=p.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u25-instruction-") };assertEquals(135,records.size);records.forEach { assertContentAuditResearch(it) }
        val result=V2Validator.validate(p);assertTrue(result.errors.joinToString("\n"),result.ok);assertEquals("DEVELOPMENT",p.unit(25)!!.installState)
    }
    private fun pack():V2CurriculumPack=V2PackLoader.parse((System.getProperty("unit25.instruction.baseline")?.let(::File)?:File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json")).readText())
}
