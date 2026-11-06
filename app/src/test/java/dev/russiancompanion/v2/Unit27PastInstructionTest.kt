package dev.russiancompanion.v2
import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
class Unit27PastInstructionTest {
    @Test fun completePastPassiveMeaningAndFormTeachingPrecedesPracticeAndRestoresEveryExample() {
        val p=pack();val text=completeLesson("passive",4);assertTrue(text.contains("entity affected by an action"));assertTrue(text.contains("imperfective past passive can exist"));assertTrue(text.contains("not a complete productive formation rule"));assertTrue(text.contains("short form напи́сан"));assertTrue(text.contains("Ivan is the writer"))
        val s=p.unit(27)!!.sessions.single { it.id=="u27-s-passive" };assertEquals(5,s.steps.single { it.kind=="REMEDIATE" }.exampleIds.size)
    }
    @Test fun requiredStressTeachingDistinguishesTheAccentedVowelAndCorrectsTheCompetingLabel() {
        val p=pack();val text=completeLesson("stress",2);assertTrue(text.contains("on the second syllable"));assertTrue(text.contains("not an accent on the final adjective ending"));assertTrue(text.contains("does not predict stress for every other participle"))
        val i=p.item("u27-g-stress-past")!!;assertEquals(listOf("stem stress напи́санный","later-vowel stress написа́нный"),i.options);assertEquals("stem stress напи́санный",i.answer);val participants=i.assessment!!.relationParticipantIds.map { id->p.provenanceRecords.single { it.itemId==id } };assertEquals(listOf(i.promptLetter)+i.options,participants.map { it.shippedText });assertTrue(participants.all { it.synthesis!!.applications.any { a->a.answer==i.answer && a.distractors==listOf(i.options[1]) } })
    }
    @Test fun meaningChecksRejectSourcePositionRecallAndOptionalPresentFormsStayOutsideGating() {
        val p=pack();for(id in listOf("u27-g-passive-0","u27-i-passive-1","u27-i-passive-2")) { val i=p.item(id)!!;assertEquals("Which English action meaning fits the shown past passive form?",i.prompt);assertTrue(i.feedback.contains(i.answer));if(i.assessment!!.independent)assertTrue(V2Validator.featureValid(p,i)) }
        val i=p.item("u27-i-passive-1")!!;val recalled=i.copy(prompt="Which admitted English label is written beside this shown form?");assertFalse(V2Validator.featureValid(p.copy(items=p.items.map { if(it.id==i.id)recalled else it }),recalled))
        val u=p.unit(27)!!;assertFalse(u.objectives.single { it.id=="obj-u27-present-passive" }.gating);assertEquals(listOf("u27-i-stress-2","u27-i-stress-3","u27-i-stress-4"),u.objectives.single { it.id=="obj-u27-stress" }.checkItemIds)
        val records=p.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u27-past-") };assertEquals(42,records.size);records.forEach { assertContentAuditResearch(it) };assertEquals("DEVELOPMENT",u.installState);assertTrue(V2Validator.validate(p).errors.joinToString("\n"),V2Validator.validate(p).ok)
    }
    private fun completeLesson(slug:String,count:Int):String {
        val p=pack();val s=p.unit(27)!!.sessions.single { it.id=="u27-s-$slug" };val t=s.steps.takeWhile { it.kind!="GUIDED" }.filter { it.kind=="TEACH" };assertEquals(count,t.size)
        val text=t.joinToString("\n") { it.body };val ids=s.steps.filter { it.kind in setOf("GUIDED","CHECK") }.flatMap { it.itemIds };for(id in ids) {val i=p.item(id)!!;i.options.forEach { assertTrue("$id $it",text.contains(it)) }}
        val r=s.steps.single { it.kind=="REMEDIATE" };assertEquals(t.joinToString("\n\n") { it.body },r.body);assertEquals(t.flatMap { it.exampleIds }.distinct(),r.exampleIds);return text
    }
    private fun pack():V2CurriculumPack=V2PackLoader.parse((System.getProperty("unit27.past.baseline")?.let(::File)?:File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json")).readText())
}
