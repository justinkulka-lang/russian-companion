package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class Unit21ReflexivePurposeInstructionTest {
    @Test fun completeInstructionAndRemediationCoverEveryCheckedCueBeforePractice() {
        val p = pack()
        for ((slug, count) in mapOf("svoy" to 4, "object-reference" to 3, "purpose" to 3)) {
            val s = p.unit(21)!!.sessions.first { it.id == "u21-s-$slug" }
            val teaches = s.steps.takeWhile { it.kind != "GUIDED" }.filter { it.kind == "TEACH" }
            assertEquals(count, teaches.size)
            val body = teaches.joinToString("\n") { it.body }
            p.items.filter { it.objectiveId == "obj-u21-$slug" }.forEach { item ->
                assertTrue("${item.id} missing cue", body.contains(item.promptLetter!!))
                item.options.forEach { assertTrue("${item.id} missing option $it", body.contains(it)) }
            }
            val rem = s.steps.first { it.kind == "REMEDIATE" }
            assertEquals(teaches.joinToString("\n\n") { it.body }, rem.body)
            assertEquals(teaches.flatMap { it.exampleIds }.distinct(), rem.exampleIds)
            assertFalse(rem.body.contains("жену́ = I love"))
        }
    }
    @Test fun possessionTracksSubjectButAgreementTracksThePossessedNoun() {
        val p = pack()
        val text = p.unit(21)!!.sessions.first { it.id == "u21-s-svoy" }.steps.joinToString("\n") { it.body }
        assertTrue(text.contains("subject as the owner"))
        assertTrue(text.contains("possessed noun, not with the owner"))
        assertTrue(text.contains("do not demonstrate independently declining"))
        val i = p.item("u21-i-object-reference-1")!!
        assertEquals(BatchBSources.WIFE_SENTENCE + " = I love my wife", BatchBSources.WIFE_LINE_CITE)
        assertTrue(i.feedback.contains(BatchBSources.WIFE_LINE_CITE))
        val bad = i.copy(feedback=i.feedback.replace(BatchBSources.WIFE_LINE_CITE,"жену́ = I love"),
            assessment=i.assessment!!.copy(whyAnswerDemonstratesObjective=i.assessment!!.whyAnswerDemonstratesObjective.replace(BatchBSources.WIFE_LINE_CITE,"жену́ = I love")))
        assertTrue(V2Validator.validate(p.copy(items=p.items.map { if(it.id==i.id) bad else it })).errors.any {
            it.contains(i.id) && it.contains("wife relationship citation is not the wife line")
        })
    }
    @Test fun causalCheckHasNoSynonymousSinceDistractorAndAdmittedBindingsStayValid() {
        val p = pack()
        val i = p.item("u21-i-purpose-2")!!
        assertEquals(listOf("because","although","after"), i.options)
        assertEquals("because", i.answer)
        assertEquals("prov-ex-u21-purpose-3-en", i.assessment!!.distractorSourceIds[1])
        assertEquals("EXPLICIT_SOURCE_PAIR", i.assessment!!.relationKind)
        assertTrue(V2Validator.featureValid(p,i))
        assertFalse(p.item("u21-g-object-reference-0")!!.prompt.contains("equals sign"))
        val records = p.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u21-") }
        assertEquals(75,records.count { !it.itemId.startsWith("prov-ca-u21-object-transfer-") })
        assertEquals(10,records.count { it.itemId.startsWith("prov-ca-u21-object-transfer-") })
        assertEquals(85,records.size)
        records.forEach { assertContentAuditResearch(it) }
        assertTrue(V2Validator.validate(p).errors.joinToString("\n"), V2Validator.validate(p).ok)
        assertEquals("DEVELOPMENT",p.unit(21)!!.installState)
    }
    @Test fun omittedEmptyFinalAuditObjectsDecodeToIdenticalTypedEvidence() {
        val file=File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json")
        val raw=Json.parseToJsonElement(file.readText()).jsonObject
        val full=JsonObject(raw + ("provenanceRecords" to JsonArray(raw.getValue("provenanceRecords").jsonArray.map { r ->
            val obj=r.jsonObject
            val sy=obj["synthesis"] as? JsonObject
            if(sy==null || "finalAudit" in sy) obj else JsonObject(obj + ("synthesis" to JsonObject(sy + ("finalAudit" to JsonObject(
                listOf("artifactIdentity","artifactDigest","auditedContentDigest","disposition","modelVersion","date").associateWith { JsonPrimitive("") }
            )))))
        })))
        val current=V2PackLoader.parse(file.readText());val expanded=V2PackLoader.parse(full.toString())
        assertEquals(expanded,current)
        assertEquals(V2Validator.contentReviewDigest(expanded),V2Validator.contentReviewDigest(current))
    }
    private fun pack(): V2CurriculumPack = V2PackLoader.parse(
        (System.getProperty("unit21.instruction.baseline")?.let(::File)
            ?: File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json")).readText())
}
