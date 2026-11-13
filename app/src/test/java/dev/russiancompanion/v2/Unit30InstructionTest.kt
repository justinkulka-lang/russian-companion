package dev.russiancompanion.v2
import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
class Unit30InstructionTest {
    @Test fun letterNameAndHeadingQuestionsDoNotChangeTheExampleMeaning() {
        val names=lesson("u30-s-letter-names")
        assertTrue(names.contains("father when asked for that comparison word"))
        assertTrue(names.contains("do not demonstrate listening or pronunciation"))
        assertTrue(lesson("u30-s-abbrev-examples").contains("not the English gloss"))
    }
    @Test fun addressAndAbbreviationTeachingExplainsRolesAndTheSourceLimit() {
        val address=lesson("u30-s-address-marks")
        assertTrue(address.contains("postal code"));assertTrue(address.contains("first mark identifies the street"))
        val abbreviation=lesson("u30-s-abbreviation")
        assertTrue(abbreviation.contains("does not say that every member is pronounced as a single word"))
        assertTrue(abbreviation.contains("permits declined and undeclined use"))
        assertTrue(abbreviation.contains("not establish an exhaustive taxonomy"))
    }
    @Test fun particlesStoryAndDictionaryRequireMeaningAndInformationLimits() {
        assertTrue(lesson("u30-s-particle").contains("not make this a report of an actual completed reading event"))
        val story=lesson("u30-s-capstone")
        assertTrue(story.contains("not the rescued girl carrying the doll"))
        assertTrue(story.contains("reaction to the doll discovery"))
        val dictionary=lesson("u30-s-dictionary")
        assertTrue(dictionary.contains("does not by itself state the sense"))
        assertTrue(dictionary.contains("does not state the furniture sense"))
    }
    @Test fun allTeachingAndExamplesReturnOnRemediation() {
        val p=pack()
        for(s in p.unit(30)!!.sessions.filter { it.steps.any { t->t.kind=="TEACH" } }) {
            val teaching=s.steps.filter { it.kind=="TEACH" };val rem=s.steps.single { it.kind=="REMEDIATE" }
            assertEquals(teaching.size,s.steps.takeWhile { it.kind!="GUIDED" }.count { it.kind=="TEACH" })
            assertEquals(teaching.joinToString("\n\n") { it.body },rem.body)
            assertEquals(teaching.flatMap { it.exampleIds }.distinct(),rem.exampleIds)
        }
        assertEquals("DEVELOPMENT",p.unit(30)!!.installState)
    }
    @Test fun pendingInstructionAndProductionValidationRemainValid() {
        val p=pack();val records=p.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u30-instruction-") }
        assertEquals(76,records.size);records.forEach { assertContentAuditResearch(it) }
        val result=V2Validator.validate(p);assertTrue(result.errors.joinToString("\n"),result.ok)
    }
    private fun lesson(id:String)=pack().unit(30)!!.sessions.single { it.id==id }.steps.filter { it.kind=="TEACH" }.joinToString("\n") { it.body }
    private fun pack()=V2PackLoader.parse((System.getProperty("unit30.baseline")?.let(::File) ?: File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json")).readText())
}
