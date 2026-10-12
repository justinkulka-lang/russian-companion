package dev.russiancompanion.v2
import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
class Unit26PostalInstructionTest {
    @Test fun completeComponentAndFormatTeachingPrecedesPracticeAndReturnsAfterAMiss() {
        val p=pack();val s=p.unit(26)!!.sessions.single { it.id=="u26-s-gap-postal-address" };val t=s.steps.takeWhile { it.kind!="GUIDED" }.filter { it.kind=="TEACH" };assertEquals(2,t.size)
        val text=t.joinToString("\n") { it.body };val items=p.items.filter { it.objectiveId=="obj-u26-postal-address" }
        for(i in items) { assertTrue(i.id,text.contains(i.promptLetter!!));i.options.forEach { assertTrue("${i.id}: $it",text.contains(it)) } }
        assertTrue(text.contains("country on the final line"));assertTrue(text.contains("not a universal international rule"));assertTrue(text.contains("not declare every shorter domestic address invalid"))
        val r=s.steps.single { it.id=="u26-postal-remediate" };assertEquals(t.joinToString("\n\n") { it.body },r.body);assertEquals(t.flatMap { it.exampleIds }.distinct(),r.exampleIds)
    }
    @Test fun sampleComparisonAndOrderingKeysAreBoundToTheirDisplayedModel() {
        val p=pack();val i=p.item("u26-i-postal-5")!!;assertEquals("the regional and country lines are absent from this shorter model",i.answer);assertTrue(i.prompt.contains("supplied full model"))
        val order=p.item("u26-i-postal-11")!!;assertTrue(order.prompt.contains("supplied format"));assertFalse(order.options.contains("recipient, premises, city, and postcode alone are not the complete order"))
        assertEquals("recipient, then premises, then locality, then the applicable region, then the country, then the postcode",order.answer)
        assertEquals("Анна Петрова / ул. Садовая, д. 12, кв. 4 / г. Казань / Республика Татарстан / Россия / 420000",order.promptLetter)
    }
    @Test fun exactPostalIntakeRejectsHistoricalUniversalityAndUnboundApplications() {
        val p=pack();val cap=p.sourceCapabilities.single { it.id=="cap-obj-u26-postal-address" };assertEquals(ContentAuditSources.SOURCE_ID,cap.sourceId);assertTrue(correctedCapabilityDefects(p,cap).joinToString("\n"),correctedCapabilityDefects(p,cap).isEmpty())
        val old=p.provenanceRecords.first { it.sourceHash=="546e52bec4a95e4454791c68858a19e6d713d3218d6dde3f43c041fbd3f83a97" };val restored=cap.copy(sourceId=old.sourceId,sourceRevision=old.sourceRevision,sourceRecordId=old.sourceRecordId,sourceHash=old.sourceHash,exactSpan=old.originalSourceText);assertTrue(V2Validator.validate(p.copy(sourceCapabilities=p.sourceCapabilities.map { if(it.id==restored.id)restored else it })).errors.any { it.contains("retired universal postal-order") })
        for(bad in listOf(cap.copy(sourceHash="0".repeat(64)),cap.copy(unitNumber=25),cap.copy(supportedClaim="letter-writing"),cap.copy(exactSpan=cap.exactSpan+"unbound")))assertTrue(correctedCapabilityDefects(p,bad).isNotEmpty())
        val target=p.item("u26-i-postal-11")!!;val changed=target.copy(prompt="Which order applies to every international letter?");assertTrue(correctedCapabilityDefects(p.copy(items=p.items.map { if(it.id==target.id)changed else it }),cap).isNotEmpty())
        val removed=p.copy(items=p.items.filter { it.id!="u26-i-postal-10" });assertTrue(correctedCapabilityDefects(removed,cap).isNotEmpty())
        val records=p.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u26-postal-") };assertEquals(97,records.size);records.forEach { assertContentAuditResearch(it) };assertTrue(V2Validator.validate(p).errors.joinToString("\n"),V2Validator.validate(p).ok);assertEquals("DEVELOPMENT",p.unit(26)!!.installState)
    }
    private fun pack():V2CurriculumPack=V2PackLoader.parse((System.getProperty("unit26.postal.baseline")?.let(::File)?:File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json")).readText())
}
