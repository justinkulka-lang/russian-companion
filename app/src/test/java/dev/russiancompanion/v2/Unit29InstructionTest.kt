package dev.russiancompanion.v2
import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
class Unit29InstructionTest {
    @Test fun instructionSeparatesTimeMeaningAndClassifiesTheDisplayedForm() {
        val text = lesson("u29-s-active")
        assertTrue(text.contains("entity performing the action"))
        assertTrue(text.contains("Classify the displayed form itself"))
        assertTrue(text.contains("not a general algorithm"))
        val adjective = lesson("u29-s-short-adjective")
        assertTrue(adjective.contains("invariant colour form"))
        assertTrue(adjective.contains("not a universal rule about one fixed word position"))
    }
    @Test fun punctuationExplainsBothBoundariesAndTheExceptions() {
        val text = lesson("u29-s-punctuation")
        assertTrue(text.contains("omit its closing boundary"))
        assertTrue(text.contains("precedes the noun"))
        assertTrue(text.contains("personal pronoun or the participial phrase expresses a cause"))
    }
    @Test fun registerDoesNotReplaceActorSequenceOrEventMeaning() {
        val text = lesson("u29-s-gap-register")
        assertTrue(text.contains("does not reverse those events"))
        assertTrue(text.contains("does not establish this bookish classification"))
        val reading = lesson("u29-s-gap-reading")
        assertTrue(reading.contains("Anna performs the actions"))
        assertTrue(reading.contains("letter as the object"))
        assertTrue(reading.contains("cannot answer who acted"))
    }
    @Test fun remediationRestoresEveryCardAndEveryExampleBeforePractice() {
        val p=pack()
        for (s in p.unit(29)!!.sessions.filter { it.steps.any { t -> t.kind=="TEACH" } }) {
            val teaching=s.steps.filter { it.kind=="TEACH" }
            assertEquals(teaching.size,s.steps.takeWhile { it.kind!="GUIDED" }.count { it.kind=="TEACH" })
            val rem=s.steps.single { it.kind=="REMEDIATE" }
            assertEquals(teaching.joinToString("\n\n") { it.body },rem.body)
            assertEquals(teaching.flatMap { it.exampleIds }.distinct(),rem.exampleIds)
        }
        assertEquals("DEVELOPMENT",p.unit(29)!!.installState)
    }
    @Test fun independentlyAuthoredPendingCoveragePassesTheProductionValidator() {
        val p=pack();val records=p.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u29-instruction-") }
        assertEquals(65,records.size);records.forEach { assertContentAuditResearch(it) }
        val result=V2Validator.validate(p);assertTrue(result.errors.joinToString("\n"),result.ok)
    }
    private fun lesson(id:String)=pack().unit(29)!!.sessions.single { it.id==id }.steps.filter { it.kind=="TEACH" }.joinToString("\n") { it.body }
    private fun pack()=V2PackLoader.parse((System.getProperty("unit29.baseline")?.let(::File) ?: File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json")).readText())
}
