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
            // T18e (D64): typed retrieval and specific prompts replace repeated cue labels.
            p.items.filter { it.objectiveId == "obj-u25-$slug" }.forEach { item ->
                assertTrue(item.id, body.contains(item.answer, ignoreCase = true))
                item.options.forEach { assertTrue("${item.id} option $it", body.contains(it, ignoreCase = true)) }
            }
            val rem=s.steps.single { it.kind=="REMEDIATE" }
            assertEquals(teaches.joinToString("\n\n") { it.body },rem.body)
            assertEquals(teaches.flatMap { it.exampleIds }.distinct(),rem.exampleIds)
        }
    }
    @Test fun questionsDoNotRevealNameClassificationAndInstructionExplainsTheLimits() {
        val p=pack();fun text(slug:String)=p.unit(25)!!.sessions.single { it.objectiveIds==listOf("obj-u25-$slug") }.steps.filter { it.kind=="TEACH" }.joinToString("\n") { it.body }
        for(id in listOf("u25-g-name-form-1","u25-i-name-form-1","u25-i-name-form-2")) {
            val i=p.item(id)!!;assertTrue(i.prompt, i.prompt.contains("classif") || i.prompt.contains("description"));assertFalse(i.prompt,i.prompt.contains("full-form"));assertFalse(i.prompt,i.prompt.contains("short-form"));assertFalse(i.prompt,i.prompt.contains("disparaging"))
        }
        assertTrue(text("name-structure").contains("surname is not shown"))
        assertTrue(text("name-question").contains("must be followed by a name"))
        assertTrue(text("courtesy").contains("polite address to one person"))
        assertTrue(text("nickname-rule").contains("Among people of equal standing it has also conveyed closeness"))
        assertTrue(text("nickname-rule").contains("meaning from the short form"))
        assertTrue(text("address").contains("Intonation, relationship and setting matter"))
        // T18e (D64): assessment-policy prose -> bounded learner direction.
        assertTrue(text("documented-name-form").contains("Use only the documented forms in this exercise."))
        val unit = p.unit(25)!!
        val checks = unit.sessions.last()
        assertEquals("u25-s-checks", checks.id)
        assertEquals(unit.objectives.flatMap { it.checkItemIds }.toSet(), checks.steps.flatMap { it.itemIds }.toSet())
        val (counts, findings) = V2AssessmentDiagnostics.audit(p)
        val summary = counts.single { it.unit == 25 }
        assertEquals(listOf(23, 23, 7), listOf(summary.checkedItems, summary.requiredChecks, summary.typedRequiredChecks))
        assertTrue(unit.sessions.dropLast(1).flatMap { it.steps }.none { it.kind == "CHECK" })
        val items = checks.steps.flatMap { it.itemIds }.map { p.item(it)!! }
        val typed = items.take(7)
        assertTrue(typed.all { it.isTypedResponse() })
        assertTrue(items.drop(7).none { it.isTypedResponse() })
        assertEquals(listOf("имя", "отчество", "фамилия", "Пожа́луйста", "Спаси́бо", "Извини́те", "извини́"), typed.map { it.answer })
        for (item in typed) {
            assertEquals(listOf(item.answer), item.typedAnswerKey!!.acceptedAnswers)
            assertFalse(typed.any { it.prompt.contains(item.answer) || it.promptLetter.orEmpty().contains(item.answer) })
        }
        val signals = findings.filter { it.unit == 25 && it.rule.startsWith("A2_24") }
        assertTrue(signals.toString(), signals.isEmpty())
        val display = V2DesignDiagnostics.audit(p).signals.filter { it.unit == 25 && it.rule == "NO_CYRILLIC_IN_REQUIRED_DISPLAY" }
        assertTrue(display.toString(), display.isEmpty())
        p.items.filter { it.objectiveId.startsWith("obj-u25-") }.forEach { assertFalse(it.id,it.prompt.contains("frozen",ignoreCase=true));assertFalse(it.id,it.prompt.contains("written for")) }
    }
    @Test fun keysStayBoundAndNameFormationAndReleaseRestrictionsStayClosed() {
        val p=pack();val items=p.items.filter { it.objectiveId.startsWith("obj-u25-") };assertEquals(30,items.size) // T18e (D64): 23 -> 30 practice and check items, including seven typed checks.
        for(i in items.filterNot { it.isTypedResponse() }) {
            if(i.assessment!!.independent)assertTrue(i.id,V2Validator.featureValid(p,i))
            assertTrue(i.id,i.feedback.contains(i.answer));for(n in 1 until i.options.size)assertTrue(i.id,i.distractorRationales[n].contains(i.options[n]))
        }
        for(id in listOf("u25-i-name-form-1","u25-i-name-form-2","u25-i-name-form-base","u25-i-address-1")) {
            val i=p.item(id)!!;val bad=i.copy(answer=i.options[1]);assertFalse(id,V2Validator.featureValid(p.copy(items=p.items.map { if(it.id==id)bad else it }),bad))
        }
        // T18e (D64, D65): keep checking authored-record identity and assert the separate frozen key citations.
        val records = p.provenanceRecords.filter { it.itemId.startsWith("prov-ca-t18e-") }
        assertTrue(records.isNotEmpty())
        records.forEach { assertContentAuditResearch(it) }
        val typed = items.filter { it.isTypedResponse() }
        assertEquals(7, typed.size)
        typed.forEach { assertTrue(it.id, V2Validator.typedKeyCitationErrors(p, it).isEmpty()) }
        val result=V2Validator.validate(p);assertTrue(result.errors.joinToString("\n"),result.ok);assertEquals("DEVELOPMENT",p.unit(25)!!.installState)
    }
    private fun pack():V2CurriculumPack=V2PackLoader.parse((System.getProperty("unit25.instruction.baseline")?.let(::File)?:File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json")).readText())
}
