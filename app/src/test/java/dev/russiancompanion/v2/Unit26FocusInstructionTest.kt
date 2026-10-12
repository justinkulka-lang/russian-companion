package dev.russiancompanion.v2
import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
class Unit26FocusInstructionTest {
    @Test fun contextualMeaningReplacesLiteralWordOrderLabels() {
        val p=pack();val a=p.item("u26-g-word-order-0")!!;val b=p.item("u26-i-word-order-1")!!;val c=p.item("u26-i-word-order-2")!!
        assertEquals("It was me who went to the shop.",a.answer);assertEquals("I went to the shop, and the story continues.",b.answer);assertEquals("I went to the shop.",c.answer)
        assertTrue(a.prompt.contains("corrects that attribution"));assertTrue(b.prompt.contains("past visit"));assertTrue(c.prompt.contains("without contrasting"))
        for(n in 0..2) { val i=listOf(a,b,c)[n];val e=p.examples.single { it.id=="ex-u26-word-order-$n" };assertEquals(i.promptLetter,e.display);assertEquals("In the supplied context: "+i.answer,e.note);assertEquals(ContentAuditSources.SOURCE_ID,e.sourceId) }
        assertFalse(listOf(a,b,c).any { it.prompt.contains("written beside") })
    }
    @Test fun completeContextAndReferenceDistinctionsPrecedePracticeAndReturnAfterAMiss() {
        val p=pack()
        for((slug,count) in listOf("word-order" to 4,"indefinite" to 3)) {
            val s=p.unit(26)!!.sessions.single { it.id=="u26-s-$slug" };val teaches=s.steps.takeWhile { it.kind!="GUIDED" }.filter { it.kind=="TEACH" };assertEquals(count,teaches.size)
            val text=teaches.joinToString("\n") { it.body };p.items.filter { it.objectiveId=="obj-u26-$slug" }.forEach { i -> assertTrue(i.id,text.contains(i.promptLetter!!));i.options.forEach { assertTrue("${i.id} $it",text.contains(it)) } }
            val rem=s.steps.single { it.kind=="REMEDIATE" };assertEquals(teaches.joinToString("\n\n") { it.body },rem.body);assertEquals(teaches.flatMap { it.exampleIds }.distinct(),rem.exampleIds)
            if(slug=="word-order")assertTrue(text.contains("does not guarantee a unique English paraphrase"))
            else { assertTrue(text.contains("identity is unknown to the speaker"));assertTrue(text.contains("no particular fixed destination"));assertTrue(text.contains("known to the speaker but not assumed known to the listener"));assertTrue(text.contains("not infer a rigid one-suffix/one-meaning division")) }
        }
    }
    @Test fun exactContextualAdmissionAndReferenceKeysRejectForgedOrUnboundEvidence() {
        val p=pack();val cap=p.sourceCapabilities.single { it.id=="cap-obj-u26-word-order" };assertEquals(ContentAuditSources.SOURCE_ID,cap.sourceId);assertTrue(correctedCapabilityDefects(p,cap).isEmpty())
        for(bad in listOf(cap.copy(sourceHash="0".repeat(64)),cap.copy(sourceRevision="unbound"),cap.copy(unitNumber=25),cap.copy(supportedClaim="indefinite-pronoun"),cap.copy(exactSpan=cap.exactSpan+"unbound")))assertTrue(correctedCapabilityDefects(p,bad).isNotEmpty())
        val i=p.item("u26-i-word-order-1")!!;val wrong=i.copy(prompt="Which admitted English label is written beside the shown arrangement?");assertFalse(V2Validator.featureValid(p.copy(items=p.items.map { if(it.id==wrong.id)wrong else it }),wrong));assertTrue(correctedCapabilityDefects(p.copy(items=p.items.map { if(it.id==wrong.id)wrong else it }),cap).isNotEmpty())
        val altered=i.copy(prompt="Which English interpretation applies in a different discourse context?");assertTrue(correctedCapabilityDefects(p.copy(items=p.items.map { if(it.id==altered.id)altered else it }),cap).isNotEmpty())
        val historical=p.provenanceRecords.first { it.sourceHash=="30e7e08a32bd5ca619593ff841bc22fff849a3ce2fc516d1f0cf67ea0c8e5bb6" };val restored=cap.copy(sourceId=historical.sourceId,sourceRevision=historical.sourceRevision,sourceRecordId=historical.sourceRecordId,sourceHash=historical.sourceHash,exactSpan=historical.originalSourceText);assertTrue(V2Validator.validate(p.copy(sourceCapabilities=p.sourceCapabilities.map { if(it.id==restored.id)restored else it })).errors.any { it.contains("historical word-order labels") })
        assertEquals("являются нереферентными",p.item("u26-i-indefinite-1")!!.answer);assertEquals("референтный денотативный статус слабой определённости",p.item("u26-i-indefinite-2")!!.answer)
        val records=p.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u26-focus-") };assertEquals(59,records.size);records.forEach { assertContentAuditResearch(it) };assertEquals("DEVELOPMENT",p.unit(26)!!.installState);assertTrue(V2Validator.validate(p).errors.joinToString("\n"),V2Validator.validate(p).ok)
    }
    private fun pack():V2CurriculumPack=V2PackLoader.parse((System.getProperty("unit26.focus.baseline")?.let(::File)?:File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json")).readText())
}
