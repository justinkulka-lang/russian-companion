package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class Unit24NegationInstructionTest {
    @Test fun allNegationContrastsAreExplainedBeforePracticeAndRepeatedAfterAMiss() {
        val p=pack()
        for((slug,count) in mapOf("verbal-ne" to 4,"absence" to 3,"alternation" to 2,"stand-ins" to 3,"concord" to 2,"word-order-contrast" to 3,"placement" to 2)) {
            val s=p.unit(24)!!.sessions.single { it.id=="u24-s-$slug" };val teaches=s.steps.takeWhile { it.kind!="GUIDED" }.filter { it.kind=="TEACH" }
            assertEquals(slug,count,teaches.size);val body=teaches.joinToString("\n") { it.body }
            p.items.filter { it.objectiveId=="obj-u24-$slug" && !it.isTypedResponse() }.forEach { i ->
                assertTrue("${i.id} cue",body.contains(i.promptLetter!!));i.options.forEach { assertTrue("${i.id} option $it",body.contains(it)) }
            }
            val rem=s.steps.single { it.kind=="REMEDIATE" };assertEquals(teaches.joinToString("\n\n") { it.body },rem.body);assertEquals(teaches.flatMap { it.exampleIds }.distinct(),rem.exampleIds)
        }
    }
    @Test fun numberAbsenceConcordAndPersuasionContextHaveDistinctExplanations() {
        val p=pack();fun text(slug:String)=p.unit(24)!!.sessions.single { it.id=="u24-s-$slug" }.steps.filter { it.kind=="TEACH" }.joinToString("\n") { it.body }
        // T18d (D62): unsupported plural check and example -> the frozen G:55 pair and typed accusative form.
        val example=p.examples.single { it.id=="ex-u24-alternation-1" };val item=p.item("u24-i-alternation-1")!!
        assertEquals(item.promptLetter,example.display);assertEquals(item.answer,example.note);assertEquals(item.sourceId,example.sourceId)
        assertNull(p.item("u24-i-alternation-2"))
        assertNull(p.item("u24-i-stand-ins-2"))
        assertTrue(p.examples.none { it.id == "ex-u24-alternation-0" || it.id == "ex-u24-stand-ins-2" })
        val guided = p.item("u24-g-alternation-0")!!
        assertEquals(p.examples.single { it.id == "ex-u24-alternation-genitive" }.display, guided.promptLetter)
        assertTrue(V2Validator.featureValid(p, guided))
        assertTrue(text("alternation").contains("not unambiguous genitive plural"));assertTrue(text("alternation").contains("not a mechanical rule"))
        // T18d (D62): source commentary and unwitnessed paradigm -> pronunciation cue and the attested nominative gap.
        assertTrue(text("absence").contains("The pronunciation of не is shown as nye."))
        assertTrue(text("stand-ins").contains("This negative form belongs to a set with no nominative."))
        assertTrue(text("concord").contains("one overall negative statement"))
        assertTrue(text("word-order-contrast").contains("Intonation and discourse context"))
        assertTrue(p.item("u24-i-word-order-contrast-1")!!.prompt.contains("will do it anyway"))
        assertTrue(p.item("u24-i-word-order-contrast-2")!!.prompt.contains("would never agree"))
        assertTrue(text("placement").contains("does not by itself identify that other person"))
    }
    @Test fun existingKeysStayBoundAndSourceAndReviewRestrictionsRemainClosed() {
        val p=pack();val items=p.items.filter { it.objectiveId.startsWith("obj-u24-") && it.assessment != null }
        // T18d (D62): 21 -> 19 assessed closed items after two unsupported checks retired.
        assertEquals(19,items.size)
        assertEquals(1,p.items.count { it.objectiveId=="obj-u24-persuasion-reference" && it.kind=="SOURCE_MEANING" })
        for(i in items) {
            if(i.assessment!!.independent)assertTrue(i.id,V2Validator.featureValid(p,i))
            assertTrue(i.id,i.feedback.contains(i.answer));for(n in 1 until i.options.size)assertTrue(i.id,i.distractorRationales[n].contains(i.options[n]))
        }
        // T18d (D62): wrong key on retired alternation-2 -> wrong key on retained alternation-1.
        for(id in listOf("u24-i-alternation-1","u24-i-stand-ins-1","u24-i-concord-1","u24-i-placement-1")) {
            val i=p.item(id)!!;val bad=i.copy(answer=i.options[1]);assertFalse(id,V2Validator.featureValid(p.copy(items=p.items.map { if(it.id==id)bad else it }),bad))
        }
        val records=p.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u24-instruction-") || it.itemId.startsWith("prov-ca-t18d-") }
        // T18d (D62): 119 -> 301 content-audit wording records after retiring unsupported material and covering the revised text.
        assertEquals(301,records.size);records.forEach { assertContentAuditResearch(it) }
        val result=V2Validator.validate(p);assertTrue(result.errors.joinToString("\n"),result.ok);assertEquals("DEVELOPMENT",p.unit(24)!!.installState)
    }
    @Test fun requiredRetrievalFollowsAllTeachingAndTypesOnlyFrozenSingleForms() {
        val p = pack()
        val unit = p.unit(24)!!
        val finalSession = unit.sessions.last()
        assertEquals("u24-s-checks", finalSession.id)
        assertTrue(unit.sessions.dropLast(1).flatMap { it.steps }.none { it.kind == "CHECK" })
        val checks = finalSession.steps.flatMap { it.itemIds }.map { p.item(it)!! }
        assertEquals(unit.objectives.flatMap { it.checkItemIds }.toSet(), checks.map { it.id }.toSet())
        val (counts, signals) = V2AssessmentDiagnostics.audit(p)
        val summary = counts.single { it.unit == 24 }
        assertEquals(listOf(20, 20, 8), listOf(summary.checkedItems, summary.requiredChecks, summary.typedRequiredChecks))
        assertTrue(signals.toString(), signals.none { it.unit == 24 && it.rule.startsWith("A2_24") })
        val typed = checks.take(8)
        assertTrue(typed.all { it.isTypedResponse() })
        assertTrue(checks.drop(8).none { it.isTypedResponse() })
        assertEquals(listOf("не", "нет", "есть", "кни́гу", "никто́", "ничто́", "не́кого", "не́кто"), typed.map { it.answer })
        val typedDisplay = typed.joinToString(" ") { it.prompt + " " + it.promptLetter.orEmpty() }
        for (item in typed) {
            assertEquals(item.id, listOf(item.answer), item.typedAnswerKey!!.acceptedAnswers)
            assertEquals(item.id, emptyList<String>(), V2Validator.typedKeyCitationErrors(p, item))
            assertFalse(item.id, typedDisplay.contains(item.answer))
            val owner = unit.objectives.single { it.id == item.objectiveId }
            assertTrue(item.id, owner.instructionStepIds.any { p.step(it)!!.second.body.contains(item.answer) })
        }
        assertEquals(5, checks.count { it.options.size == 2 })
        assertNotNull(unit.binderTask)
        assertTrue(unit.tutorTask!!.bring.contains("Penguin book"))
    }

    private fun pack():V2CurriculumPack=V2PackLoader.parse((System.getProperty("unit24.instruction.baseline")?.let(::File)?:File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json")).readText())
}
