package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class Unit26LetterInstructionTest {
    @Test fun suppliedOrganizationAndGreetingInferenceReplaceUniversalKeys() {
        val p=pack();val organization=p.item("u26-g-letter-writing-0")!!;val greeting=p.item("u26-i-letter-writing-1")!!
        assertEquals("state the reason, explain relevant facts, then make the request",organization.answer)
        assertEquals("a greeting, not a rule about how close the recipient is",greeting.answer)
        assertEquals("Здравствуйте!",greeting.promptLetter)
        assertEquals("Closing and signature",p.item("u26-i-letter-writing-2")!!.answer)
        assertFalse(listOf(organization,greeting).any { it.answer.contains("never") || it.answer.contains("every Russian") })
        assertTrue(organization.prompt.contains("supplied"));assertTrue(p.item("u26-i-letter-writing-2")!!.prompt.contains("supplied"))
    }
    @Test fun completeFrameworkPrecedesPracticeAndReturnsAfterAMiss() {
        val p=pack();val s=p.unit(26)!!.sessions.single { it.id=="u26-s-letter-writing" }
        val teaches=s.steps.takeWhile { it.kind!="GUIDED" }.filter { it.kind=="TEACH" };assertEquals(2,teaches.size)
        val text=teaches.joinToString("\n") { it.body };for(id in listOf("u26-g-letter-writing-0","u26-i-letter-writing-1","u26-i-letter-writing-2")) {
            val i=p.item(id)!!;assertTrue(id,text.contains(i.promptLetter!!));i.options.forEach { assertTrue("$id $it",text.contains(it)) }
            assertTrue(i.feedback.contains(i.answer));for(n in 1 until i.options.size)assertTrue(i.distractorRationales[n].contains(i.options[n]))
        }
        val rem=s.steps.single { it.kind=="REMEDIATE" };assertEquals(teaches.joinToString("\n\n") { it.body },rem.body);assertEquals(teaches.flatMap { it.exampleIds }.distinct(),rem.exampleIds)
    }
    @Test fun exactResearchAdmissionRejectsLegacyCapabilityAndForgery() {
        val p=pack();val cap=p.sourceCapabilities.single { it.id=="cap-obj-u26-letter-writing" };assertEquals(ContentAuditSources.SOURCE_ID,cap.sourceId)
        assertTrue(correctedCapabilityDefects(p,cap).joinToString(),correctedCapabilityDefects(p,cap).isEmpty())
        val records=p.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u26-letter-") };assertEquals(31,records.size);records.forEach { assertContentAuditResearch(it) }
        for(bad in listOf(cap.copy(sourceHash="0".repeat(64)),cap.copy(sourceRevision="unbound"),cap.copy(unitNumber=25),cap.copy(sourceRecordId="unbound"),cap.copy(objectiveId="obj-u26-word-order"),cap.copy(supportedClaim="postal-address"),cap.copy(exactSpan=cap.exactSpan+"unbound")))assertTrue(correctedCapabilityDefects(p,bad).isNotEmpty())
        val guided=p.item("u26-g-letter-writing-0")!!;val changed=guided.copy(assessment=guided.assessment!!.copy(testedFeature="LETTER_STRUCTURE"));val wrong=p.copy(items=p.items.map { if(it.id==changed.id)changed else it });assertTrue(correctedCapabilityDefects(wrong,cap).isNotEmpty())
        val i=p.item("u26-i-letter-writing-1")!!;val bad=i.copy(answer=i.options[1]);assertFalse(V2Validator.featureValid(p.copy(items=p.items.map { if(it.id==bad.id)bad else it }),bad))
        val originalHost=p.provenanceRecords.first { it.sourceId==RemainingSources.LETTER_SOURCE && it.originalSourceText==RemainingSources.LETTER_SPAN }
        val original=cap.copy(sourceId=originalHost.sourceId,sourceRevision=originalHost.sourceRevision,sourceRecordId=originalHost.sourceRecordId,sourceHash=originalHost.sourceHash,exactSpan=originalHost.originalSourceText)
        val restored=p.copy(sourceCapabilities=p.sourceCapabilities.map { if(it.id==original.id)original else it })
        assertTrue(V2Validator.validate(restored).errors.any { it.contains("retired universal letter-writing intake") })
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(restored,26).any { it.claim=="letter-writing" })
        val legacy=records.first().copy(sourceId="src-readyrussian-letter");assertFalse(V2Validator.validate(p.copy(provenanceRecords=p.provenanceRecords.map { if(it.itemId==legacy.itemId)legacy else it })).ok)
        assertEquals("DEVELOPMENT",p.unit(26)!!.installState);assertTrue(V2Validator.validate(p).errors.joinToString("\n"),V2Validator.validate(p).ok)
    }
    private fun pack():V2CurriculumPack=V2PackLoader.parse((System.getProperty("unit26.letter.baseline")?.let(::File)?:File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json")).readText())
}
