package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class Unit23TimeInstructionTest {
    @Test fun eachTemporalContrastPrecedesPracticeAndReturnsInCompleteRemediation() {
        val p=pack()
        for((slug,count) in mapOf("ranges" to 2,"beginning" to 3,"during-event" to 2,"duration" to 2,"elapsed" to 3,"completion" to 2)) {
            val obj="obj-u23-$slug";val s=p.unit(23)!!.sessions.single { obj in it.objectiveIds && it.steps.any { st -> st.kind=="TEACH" } }
            val teaches=s.steps.takeWhile { it.kind!="GUIDED" }.filter { it.kind=="TEACH" }
            assertEquals(slug,count,teaches.size)
            val body=teaches.joinToString("\n") { it.body }
            p.items.filter { it.objectiveId==obj }.forEach { i ->
                assertTrue("${i.id} cue",body.contains(i.promptLetter!!))
                i.options.forEach { assertTrue("${i.id} option $it",body.contains(it)) }
            }
            val rem=s.steps.single { it.kind=="REMEDIATE" }
            assertEquals(teaches.joinToString("\n\n") { it.body },rem.body)
            assertEquals(teaches.flatMap { it.exampleIds }.distinct(),rem.exampleIds)
        }
    }
    @Test fun intervalCaseAndCompletionAreNotConfusedWithDurationOrNoncompletion() {
        val p=pack()
        fun text(slug:String)=p.unit(23)!!.sessions.single { "obj-u23-$slug" in it.objectiveIds && it.steps.any { st->st.kind=="TEACH" } }.steps.filter { it.kind=="TEACH" }.joinToString("\n") { it.body }
        assertTrue(text("elapsed").contains("takes accusative, not genitive"))
        assertTrue(text("elapsed").contains("Ago follows an accusative time expression"))
        assertTrue(text("completion").contains("does not mean asserting that the task was never finished"))
        assertTrue(text("completion").contains("completion interval differs from the forward-looking time until"))
        assertTrue(text("during-event").contains("does not mean that the birth or meeting continued for the entire"))
        assertTrue(text("duration").contains("several changes during the day"))
        assertTrue(text("beginning").contains("location noun after the preposition is prepositional"))
        assertTrue(text("ranges").contains("does not fix the century in every context"))
    }
    @Test fun checkedMeaningsKeepAdmittedRelationshipsAndWrongKeysStillFail() {
        val p=pack();val items=p.items.filter { it.objectiveId.startsWith("obj-u23-") }
        assertEquals(18,items.size)
        for(i in items) {
            if(i.assessment!!.independent)assertTrue(i.id,V2Validator.featureValid(p,i))
            assertTrue(i.id,i.feedback.contains(i.promptLetter!!)&&i.feedback.contains(i.answer))
            for(n in 1 until i.options.size)assertTrue(i.id,i.distractorRationales[n].contains(i.options[n]))
        }
        for(id in listOf("u23-i-ranges-1","u23-i-elapsed-1","u23-i-completion-1","u23-i-completion-2")) {
            val i=p.item(id)!!;val bad=i.copy(answer=i.options[1]);assertFalse(id,V2Validator.featureValid(p.copy(items=p.items.map { if(it.id==id)bad else it }),bad))
        }
        val records=p.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u23-") }
        assertEquals(81,records.size);records.forEach { assertContentAuditResearch(it) }
        val cap=p.sourceCapabilities.single { it.supportedClaim=="completion-over-period" }
        assertEquals(Phase2Sources.COMPLETION.sourceHash,cap.sourceHash)
        assertTrue(Phase2Sources.matches(cap))
        val result=V2Validator.validate(p);assertTrue(result.errors.joinToString("\n"),result.ok)
        assertEquals("DEVELOPMENT",p.unit(23)!!.installState)
    }
    @Test fun omittedContinueAndEmptyStepListsHaveIdenticalTypedMeaning() {
        val file=File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json")
        val raw=Json.parseToJsonElement(file.readText()).jsonObject
        val full=JsonObject(raw+("units" to JsonArray(raw.getValue("units").jsonArray.map { u -> val unit=u.jsonObject
            JsonObject(unit+("sessions" to JsonArray(unit.getValue("sessions").jsonArray.map { s -> val session=s.jsonObject
                JsonObject(session+("steps" to JsonArray(session.getValue("steps").jsonArray.map { st -> val step=st.jsonObject
                    val defaults=mapOf<String,JsonElement>("dominantAction" to JsonPrimitive("Continue"),"referenceIds" to JsonArray(emptyList()),"itemIds" to JsonArray(emptyList()),"exampleIds" to JsonArray(emptyList()))
                    JsonObject(step+defaults.filterKeys { it !in step })
                })))
            })))
        })))
        val current=V2PackLoader.parse(file.readText());val expanded=V2PackLoader.parse(full.toString())
        assertEquals(expanded,current);assertEquals(V2Validator.contentReviewDigest(expanded),V2Validator.contentReviewDigest(current))
    }
    private fun pack():V2CurriculumPack=V2PackLoader.parse((System.getProperty("unit23.instruction.baseline")?.let(::File)?:File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json")).readText())
}
