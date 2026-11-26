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
            p.items.filter { it.objectiveId=="obj-u22-$slug" }.forEach { i ->
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
        assertTrue(text("collective").contains("not a general replacement"))
        assertTrue(text("collective").contains("female-person nouns"))
        assertTrue(text("collective").contains("does not demonstrate"))
    }
    @Test fun admittedAnswersStayBoundAndWrongKeysStillFailProductionValidation() {
        val p=pack();val items=p.items.filter { it.objectiveId.startsWith("obj-u22-") }
        assertEquals(20,items.size)
        for(i in items) {
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
        assertTrue(q.feedback.contains("not the quantity or translation of the reference"))
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
    private fun pack():V2CurriculumPack=V2PackLoader.parse((System.getProperty("unit22.instruction.baseline")?.let(::File) ?: File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json")).readText())
}
