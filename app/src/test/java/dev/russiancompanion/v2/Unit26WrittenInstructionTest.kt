package dev.russiancompanion.v2
import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
class Unit26WrittenInstructionTest {
    @Test fun letterCaseTeachingSeparatesRecipientContextFromPluralShape() {
        val text=completeLesson("vy-capital",3)
        assertTrue(text.contains("Minuscule means a lowercase letter"))
        assertTrue(text.contains("capital initial alone does not establish the number of addressees"))
        assertTrue(text.contains("Lowercase outside this convention is not automatically rude"))
        assertTrue(text.contains("Capitalization cannot be heard in speech"))
        assertTrue(text.contains("sentence-initial capital"))
    }
    @Test fun addressMarksTeachingExplainsEveryRetainedComponentAndRestoresAllExamples() {
        val text=completeLesson("address-notation",3)
        assertTrue(text.contains("кв. identifies the apartment"))
        assertTrue(text.contains("д. identifies the building"))
        assertTrue(text.contains("first mark ул. identifies the street"))
        assertTrue(text.contains("prescribe a universal postal order"))
        assertEquals(3,pack().unit(26)!!.sessions.single { it.id=="u26-s-address-notation" }.steps.single { it.kind=="REMEDIATE" }.exampleIds.size)
    }
    @Test fun correspondenceTeachingDistinguishesEnclosureClosingAndProductionLimits() {
        val text=completeLesson("correspondence",2)
        assertTrue(text.contains("enclosure function"))
        assertTrue(text.contains("closing formula before the signature"))
        assertTrue(text.contains("enclosed please find does not close the letter"))
        assertTrue(text.contains("not universal formulas for every recipient"))
        val p=pack();val records=p.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u26-written-") };assertEquals(37,records.size);records.forEach { assertContentAuditResearch(it) }
        assertEquals("DEVELOPMENT",p.unit(26)!!.installState)
        assertTrue(V2Validator.validate(p).errors.joinToString("\n"),V2Validator.validate(p).ok)
    }
    private fun completeLesson(slug:String,count:Int):String {
        val p=pack();val s=p.unit(26)!!.sessions.single { it.id=="u26-s-$slug" }
        val all=s.steps.filter { it.kind=="TEACH" };val before=s.steps.takeWhile { it.kind!="GUIDED" }.filter { it.kind=="TEACH" };assertEquals(count,before.size);assertEquals(all,before)
        val text=before.joinToString("\n") { it.body };p.items.filter { it.objectiveId=="obj-u26-$slug" }.forEach { i -> assertTrue(i.id,text.contains(i.promptLetter!!));i.options.forEach { assertTrue("${i.id}: $it",text.contains(it)) } }
        val rem=s.steps.single { it.kind=="REMEDIATE" };assertEquals(before.joinToString("\n\n") { it.body },rem.body);assertEquals(before.flatMap { it.exampleIds }.distinct(),rem.exampleIds)
        s.steps.filter { it.kind in setOf("GUIDED","CHECK") }.forEach { assertEquals(rem.id,it.remediationStepId) }
        return text
    }
    private fun pack():V2CurriculumPack=V2PackLoader.parse((System.getProperty("unit26.written.baseline")?.let(::File)?:File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json")).readText())
}
