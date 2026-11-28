package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class Unit22NumberInstructionTest {
    @Test fun everyCheckedNumberContrastIsTaughtBeforePracticeAndRestoredOnAMiss() {
        val p=pack()
        for ((slug,count) in mapOf("noun-forms" to 4,"compounds" to 3,"declension" to 3,"both-forms" to 3,"half-forms" to 3,"collective" to 3)) {
            val s=p.unit(22)!!.sessions.single { it.id=="u22-s-$slug" }
            val teaches=s.steps.takeWhile { it.kind!="GUIDED" }.filter { it.kind=="TEACH" }
            assertEquals("$slug complete instruction before practice",count,teaches.size)
            val body=teaches.joinToString("\n") { it.body }
            // T18b expectation change (owner decision D56c), old -> new: every item's cue and options -> every closed item's cue and
            // options, and every typed item's key; a typed cue is the nominative form and the key is what teaching shows first.
            p.items.filter { it.objectiveId=="obj-u22-$slug" }.forEach { i ->
                if(i.isTypedResponse()) { assertTrue("${i.id} key taught",body.contains(i.answer)); return@forEach }
                assertTrue("${i.id} cue",body.contains(i.promptLetter!!))
                i.options.forEach { assertTrue("${i.id} option $it",body.contains(it)) }
            }
            val rem=s.steps.single { it.kind=="REMEDIATE" }
            assertEquals(teaches.joinToString("\n\n") { it.body },rem.body)
            assertEquals(teaches.flatMap { it.exampleIds }.distinct(),rem.exampleIds)
        }
    }
    @Test fun ruleScopeDistinguishesTeensGenderCaseAmbiguityAndCollectiveUsage() {
        val p=pack()
        fun text(slug:String)=p.unit(22)!!.sessions.single { it.id=="u22-s-$slug" }.steps.filter { it.kind=="TEACH" }.joinToString("\n") { it.body }
        assertTrue(text("noun-forms").contains("genitive singular"))
        assertTrue(text("noun-forms").contains("Animate accusative"))
        assertTrue(text("compounds").contains("last two digits"))
        assertTrue(text("compounds").contains("11 through 14"))
        assertTrue(text("compounds").contains("111 differs from 41"))
        assertTrue(text("declension").contains("bare form двух can occur in more than one case"))
        assertTrue(text("both-forms").contains("Gender refers to the counted noun"))
        assertTrue(text("half-forms").contains("shared form полутора"))
        assertTrue(text("half-forms").contains("noun is plural in the case required"))
        // T18b expectation change (owner decision D56a), old -> new: the collective-usage restrictions ("not a general replacement",
        // "female-person nouns") and the assessment commentary ("does not demonstrate") are REMOVED; the form table and the quantity
        // distinction stay.
        assertFalse(text("collective").contains("not a general replacement"))
        assertFalse(text("collective").contains("female-person nouns"))
        assertFalse(text("collective").contains("does not demonstrate"))
        assertTrue(text("collective").contains("Each collective keeps the quantity of its cardinal"))
    }
    @Test fun admittedAnswersStayBoundAndWrongKeysStillFailProductionValidation() {
        val p=pack();val items=p.items.filter { it.objectiveId.startsWith("obj-u22-") }
        // T18b expectation change (owner decision D56c), old -> new: 20 -> 26 items (six typed checks); the typed checks have no
        // closed-choice assessment, so the assessment checks below read the closed items only.
        assertEquals(26,items.size)
        for(i in items.filter { !it.isTypedResponse() }) {
            if(i.assessment!!.independent) assertTrue(i.id,V2Validator.featureValid(p,i))
            assertTrue("${i.id} correct feedback",i.feedback.contains(i.answer))
            for(n in 1 until i.options.size) {
                assertTrue("${i.id} wrong feedback",i.distractorRationales[n].contains(i.options[n]))
            }
        }
        val q=p.item("u22-i-compounds-1")!!
        assertEquals("сорок один год",q.promptLetter)
        assertEquals("шестьдесят девять слов",q.answer)
        assertTrue(q.prompt.contains("component of nine"))
        // T18b expectation change (sweep row, A2-26), old -> new: "not the quantity or translation of the reference" -> the learner-voice
        // wording that replaced the question framing.
        assertTrue(q.feedback.contains("rather than the quantity or translation of the reference"))
        for(id in listOf("u22-i-declension-1","u22-i-both-forms-2","u22-i-half-forms-2","u22-i-collective-2")) {
            val i=p.item(id)!!;val bad=i.copy(answer=i.options[1])
            assertFalse(id,V2Validator.featureValid(p.copy(items=p.items.map { if(it.id==id)bad else it }),bad))
        }
        val records=p.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u22-") }
        assertEquals(116,records.size);records.forEach { assertContentAuditResearch(it) }
        val result=V2Validator.validate(p);assertTrue(result.errors.joinToString("\n"),result.ok)
        assertEquals("DEVELOPMENT",p.unit(22)!!.installState)
    }
    @Test fun omittedStepNullsAndEmptySynthesisApplicationsHaveIdenticalTypedMeaning() {
        val file=File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json")
        val raw=Json.parseToJsonElement(file.readText()).jsonObject
        val expanded=JsonObject(raw + mapOf(
            "units" to JsonArray(raw.getValue("units").jsonArray.map { u -> val unit=u.jsonObject
                JsonObject(unit+("sessions" to JsonArray(unit.getValue("sessions").jsonArray.map { s -> val session=s.jsonObject
                    JsonObject(session+("steps" to JsonArray(session.getValue("steps").jsonArray.map { st ->
                        val step=st.jsonObject;JsonObject(step+listOf("unavailableReason","bookDirection","companionDirection","remediationStepId").filter { it !in step }.associateWith { JsonNull })
                    })))
                })))
            }),
            "provenanceRecords" to JsonArray(raw.getValue("provenanceRecords").jsonArray.map { r -> val record=r.jsonObject;val sy=record["synthesis"] as? JsonObject
                if(sy==null || "applications" in sy)record else JsonObject(record+("synthesis" to JsonObject(sy+("applications" to JsonArray(emptyList())))))
            })
        ))
        val current=V2PackLoader.parse(file.readText());val full=V2PackLoader.parse(expanded.toString())
        assertEquals(full,current);assertEquals(V2Validator.contentReviewDigest(full),V2Validator.contentReviewDigest(current))
    }
    /** T18b (A2-24, D56c; CC022): every required check sits in u22-s-checks after all teaching; typed checks come first. */
    @Test fun unit22ChecksSitInTheEndOfUnitSessionWithNoKeyExposure() {
        val p=pack();val unit=p.unit(22)!!
        val checks=unit.sessions.single { it.id=="u22-s-checks" }
        val required=unit.objectives.flatMap { it.checkItemIds }
        assertEquals(20,required.size)
        assertTrue(unit.sessions.filter { it.id!=checks.id }.flatMap { it.steps }.none { it.kind=="CHECK" })
        assertTrue(required.all { id -> checks.steps.any { id in it.itemIds } })
        assertEquals("u22-s-book",unit.sessions.last().id)
        val (summaries,signals)=V2AssessmentDiagnostics.audit(p)
        val s=summaries.single { it.unit==22 }
        assertEquals(listOf(20,20,6),listOf(s.checkedItems,s.requiredChecks,s.typedRequiredChecks))
        assertEquals(emptyList<V2DesignSignal>(),signals.filter { it.unit==22 && it.rule.startsWith("A2_2") })
        val typed=required.map { p.item(it)!! }.filter { it.isTypedResponse() }
        assertEquals(listOf("двум","двумя","обеим","обоим","обеими","обоими"),typed.map { it.answer })
        typed.forEach {
            assertEquals(it.id,emptyList<String>(),V2Validator.typedKeyCitationErrors(p,it))
            assertEquals(it.id,listOf(it.answer),it.typedAnswerKey!!.acceptedAnswers)
            assertFalse(it.id,it.answer.contains('\u0301'))
            assertTrue(it.id,Regex("dative|instrumental").containsMatchIn(it.prompt))
        }
        // No accepted typed key is displayed in the check session before its own check.
        var seen=""
        for(step in checks.steps) for(id in step.itemIds) {
            val i=p.item(id)!!
            if(i.isTypedResponse()) assertFalse(id,Regex("(?<![\\p{L}])${Regex.escape(i.answer)}(?![\\p{L}])").containsMatchIn(seen))
            seen+=" "+i.prompt+" "+i.promptLetter.orEmpty()+" "+i.options.joinToString(" ")
        }
    }

    /** T18b (CC021, D56b, D56e): remediation repeats the teaching; the rewordings are in place; no raw table cells are shown. */
    @Test fun unit22RemediationRewordingsAndNoRawTableCells() {
        val p=pack();val unit=p.unit(22)!!
        for(o in unit.objectives.filter { it.checkItemIds.isNotEmpty() }) {
            val rem=o.remediationStepIds.map { p.step(it)!!.second }.joinToString("\n") { it.body }
            for(i in o.checkItemIds.map { p.item(it)!! }) assertTrue(i.id,rem.contains(i.answer) || (!i.isTypedResponse() && rem.contains(i.promptLetter!!)))
        }
        val text=unit.sessions.flatMap { it.steps }.joinToString("\n") { it.body }
        assertTrue(text.contains("In the oblique constructions shown here, the numeral and counted noun take the case required by the construction."))
        assertTrue(text.contains("Here ‘with’ gives the preposition’s meaning, and the noun is instrumental plural."))
        assertFalse(text.contains("accompaniment/means"))
        val items=p.items.filter { it.objectiveId.startsWith("obj-u22-") }
        assertTrue(items.none { i -> (listOf(i.feedback)+i.distractorRationales).any { "| " in it || "In context: \" " in it } })
        assertTrue(items.none { i -> i.distractorRationales.any { "It does not match the category" in it } })
        // Provenance stays in the assessment metadata.
        assertTrue(p.item("u22-i-both-forms-1")!!.assessment!!.whyAnswerDemonstratesObjective.contains("| Дательный"))
    }

    /** T18b (A2-26; CC029): no governance, narration or markup in Unit 22 learner text. */
    @Test fun unit22LearnerTextHasNoGovernanceOrNarration() {
        val learner=setOf("LEARNER_GOVERNANCE_TERM","LEARNER_PROVENANCE_NARRATION","LEARNER_MARKUP","LEARNER_TABLE_CELL_MARKUP")
        val hits=V2DesignDiagnostics.audit(pack()).signals.filter { it.rule in learner && it.unit==22 }
        assertTrue(hits.joinToString("\n") { "${it.rule} ${it.entityIds} ${it.field}" },hits.isEmpty())
    }

    /** T18b (WC011): Unit 22 has its own binder and tutor task, without PDF, index or review wording. */
    @Test fun unit22HasItsOwnBinderAndTutorTask() {
        val unit=pack().unit(22)!!
        assertEquals("binder-u22-numerals",unit.binderTask!!.id)
        assertEquals("tutor-u22-numerals",unit.tutorTask!!.id)
        val text=listOfNotNull(unit.binderTask!!.prompt,unit.binderTask!!.modelAnswer,unit.tutorTask!!.bring)
        assertTrue(text.none { Regex("(?i)\\bpdf\\b|\\bindex\\b|\\breview(?:er|ed)?\\b").containsMatchIn(it) })
    }
    private fun pack():V2CurriculumPack=V2PackLoader.parse((System.getProperty("unit22.instruction.baseline")?.let(::File) ?: File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json")).readText())
}
