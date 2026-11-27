package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class Unit23TimeInstructionTest {
    @Test fun eachTemporalContrastPrecedesPracticeAndReturnsInCompleteRemediation() {
        val p=pack()
        val completionFixture = withRetiredUnit23Completion(p)
        // T18c-1 (D57a, D58b): live completion teaching/remediation -> planted baseline teaching/remediation.
        for((slug,count) in mapOf("ranges" to 2,"beginning" to 3,"during-event" to 2,"duration" to 2,"elapsed" to 3,"completion" to 2)) {
            val target = if (slug == "completion") completionFixture else p
            val obj="obj-u23-$slug";val s=target.unit(23)!!.sessions.single { obj in it.objectiveIds && it.steps.any { st -> st.kind=="TEACH" } }
            val teaches=s.steps.takeWhile { it.kind!="GUIDED" }.filter { it.kind=="TEACH" }
            assertEquals(slug,count,teaches.size)
            val body=teaches.joinToString("\n") { it.body }
            target.items.filter { it.objectiveId==obj }.forEach { i ->
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
        val completionFixture = withRetiredUnit23Completion(p)
        // T18c-1 (D57a, D58b): live completion contrast -> byte-for-byte baseline teaching text.
        fun text(slug:String)=(if (slug == "completion") completionFixture else p).unit(23)!!.sessions.single { "obj-u23-$slug" in it.objectiveIds && it.steps.any { st->st.kind=="TEACH" } }.steps.filter { it.kind=="TEACH" }.joinToString("\n") { it.body }
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
        val p = pack()
        val completionFixture = withRetiredUnit23Completion(p)
        val items = p.items.filter { it.objectiveId.startsWith("obj-u23-") }
        // T18c-1 (D57a, D58b): 18 live items -> 15 live items plus 3 baseline fixture items.
        assertEquals(15, items.size)
        val completionItems = completionFixture.items.filter { it.objectiveId == "obj-u23-completion" }
        assertEquals(3, completionItems.size)
        for (i in items + completionItems) {
            val target = if (i in completionItems) completionFixture else p
            if (i.assessment!!.independent) assertTrue(i.id, V2Validator.featureValid(target, i))
            assertTrue(i.id, i.feedback.contains(i.promptLetter!!) && i.feedback.contains(i.answer))
            for (n in 1 until i.options.size) assertTrue(i.id, i.distractorRationales[n].contains(i.options[n]))
        }
        // T18c-1 (D57a, D58b): wrong live completion keys -> wrong planted completion keys.
        for (id in listOf("u23-i-ranges-1", "u23-i-elapsed-1", "u23-i-completion-1", "u23-i-completion-2")) {
            val target = if (id.startsWith("u23-i-completion-")) completionFixture else p
            val item = target.item(id)!!
            val bad = item.copy(answer = item.options[1])
            assertFalse(id, V2Validator.featureValid(target.copy(items = target.items.map { if (it.id == id) bad else it }), bad))
        }
        val records = p.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u23-") }
        // T18c-1 (D57a, D58b): 81 live content-audit records -> 68 live records plus 13 planted records.
        assertEquals(68, records.size)
        val completionRecords = completionFixture.provenanceRecords.filter {
            it.objectiveId == "obj-u23-completion" && it.itemId.startsWith("prov-ca-u23-")
        }
        assertEquals(13, completionRecords.size)
        (records + completionRecords).forEach { assertContentAuditResearch(it) }
        // T18c-1 (D57a, D58b): live completion capability matches the synthesis -> live capability absent.
        assertFalse(p.sourceCapabilities.any { it.unitNumber == 23 && it.supportedClaim == "completion-over-period" })
        val cap = completionFixture.sourceCapabilities.single { it.supportedClaim == "completion-over-period" }
        assertEquals(Phase2Sources.COMPLETION.sourceHash, cap.sourceHash)
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

/** T18c-1 (D57a, D58b): verbatim JSON entries from f48d7b75, available without Git. */
private val retiredUnit23Completion by lazy {
    val resource = checkNotNull(Unit23TimeInstructionTest::class.java.getResource("/unit23-completion-f48d7b75.json"))
    val fixture = Json.parseToJsonElement(resource.readText()).jsonObject
    val root = File(if (File("app").isDirectory) "." else "..")
    val live = Json.parseToJsonElement(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).jsonObject
    // The fixture provenance and its source pool replace the live pair together.
    V2PackLoader.parse(JsonObject(live + fixture).toString())
}

internal fun withRetiredUnit23Completion(pack: V2CurriculumPack): V2CurriculumPack {
    val fixture = retiredUnit23Completion
    val baseline = fixture.unit(23)!!
    val objective = baseline.objectives.single { it.id == "obj-u23-completion" }
    val session = baseline.sessions.single { it.id == "u23-s-gap-completion" }
    val unit = pack.unit(23)!!
    check(unit.objectives.none { it.id == objective.id })
    return pack.copy(
        units = pack.units.map { current ->
            if (current.number != unit.number) current else current.copy(
                objectives = current.objectives + objective,
                sessions = current.sessions.map { if (it.id == session.id) session else it },
            )
        },
        items = pack.items + fixture.items,
        examples = pack.examples + fixture.examples,
        sourceCapabilities = pack.sourceCapabilities + fixture.sourceCapabilities,
        provenanceRecords = pack.provenanceRecords + fixture.provenanceRecords,
    )
}
