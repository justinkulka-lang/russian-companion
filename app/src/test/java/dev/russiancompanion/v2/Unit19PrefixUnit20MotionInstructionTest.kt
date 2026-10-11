package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class Unit19PrefixUnit20MotionInstructionTest {
    @Test fun dissuadeCueUsesItsExplicitLicensedAspectPartnerWithoutAnswerLeak() {
        val p = pack()
        val i = p.item("u19-i-prefix-caution-2")!!
        assertEquals("отговаривать", i.promptLetter)
        assertEquals("отговорить", i.answer)
        val cue = p.provenanceRecords.first { it.itemId == i.assessment!!.relationParticipantIds.first() }
        assertEquals("src-wikibooks-russian-cc-by-sa", cue.sourceId)
        assertTrue(cue.originalSourceText.contains("отговаривать"))
        assertTrue(cue.originalSourceText.contains("отговорить"))
        for (id in listOf("u19-g-prefix-caution-0", "u19-i-prefix-caution-1")) {
            val item = p.item(id)!!
            assertFalse(item.prompt.contains(item.answer.removePrefix("to ")))
            assertTrue(item.feedback.contains(item.promptLetter!!))
        }
    }
    @Test fun independentLikelihoodChecksHaveDistinctCuesAndFeatures() {
        val p = pack()
        val eclipse = p.item("u19-i-probability-1")!!
        val employees = p.item("u19-i-probability-2")!!
        assertNotEquals(eclipse.promptLetter, employees.promptLetter)
        assertNotEquals(eclipse.assessment!!.testedFeature, employees.assessment!!.testedFeature)
        assertEquals("an obligation", employees.answer)
        assertTrue(employees.promptLetter!!.startsWith("Слу́жащие"))
        assertEquals("STATED_APPLICATION", employees.assessment!!.relationKind)
    }
    @Test fun completePrefixAndMotionInstructionPrecedesPracticeAndRemediation() {
        val p = pack()
        for ((number, slug, count) in listOf(Triple(19,"prefix-caution",3),Triple(20,"motion",5))) {
            val s = p.unit(number)!!.sessions.first { it.id == "u$number-s-$slug" }
            val teaches = s.steps.takeWhile { it.kind != "GUIDED" }.filter { it.kind == "TEACH" }
            assertEquals(count, teaches.size)
            val body = teaches.joinToString("\n") { it.body }
            p.items.filter { it.objectiveId == "obj-u$number-$slug" }.forEach {
                assertTrue("${it.id} missing cue", body.contains(it.promptLetter!!))
                it.options.forEach { option -> assertTrue("${it.id} missing option $option", body.contains(option)) }
            }
            val rem = s.steps.first { it.kind == "REMEDIATE" }
            assertEquals(teaches.joinToString("\n\n") { it.body }, rem.body)
            assertEquals(teaches.flatMap { it.exampleIds }.distinct(), rem.exampleIds)
        }
        val motion = p.unit(20)!!.sessions.first { it.id == "u20-s-motion" }.steps.joinToString("\n") { it.body }
        assertTrue(motion.contains("not an aspect pair"))
        assertTrue(motion.contains("does not itself mean perfective or arrival"))
        assertTrue(motion.contains("does not say that he arrived"))
        assertTrue(motion.contains("does not specify how long"))
    }
    @Test fun sourceAndApplicationChainsValidateAndReviewRemainsPending() {
        val p = pack()
        val records = p.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u1920-") }
        assertEquals(64, records.size)
        records.forEach { assertContentAuditResearch(it) }
        assertTrue(V2Validator.validate(p).errors.joinToString("\n"), V2Validator.validate(p).ok)
        for (n in listOf(19,20)) assertEquals("DEVELOPMENT", p.unit(n)!!.installState)
    }
    @Test fun omittedReviewDefaultsDecodeToTheSameTypedPack() {
        val file = File(if (File("app").isDirectory) "." else "..", "app/src/main/assets/content/v2-curriculum.json")
        val raw = Json.parseToJsonElement(file.readText()).jsonObject
        val explicit = JsonObject(raw + ("provenanceRecords" to JsonArray(raw.getValue("provenanceRecords").jsonArray.map {
            JsonObject(it.jsonObject + mapOf("reviewerIdentity" to JsonPrimitive(""), "reviewerQualification" to JsonPrimitive(""),
                "reviewDate" to JsonPrimitive(""), "reviewedDigest" to JsonPrimitive(""), "audioAssetId" to JsonNull))
        })))
        val compact = V2PackLoader.parse(file.readText())
        val expanded = V2PackLoader.parse(explicit.toString())
        assertEquals(expanded, compact)
        assertEquals(V2Validator.contentReviewDigest(expanded), V2Validator.contentReviewDigest(compact))
    }
    @Test fun neutralMeaningPromptKeepsOtherTargetAndRelationRequirements() {
        val p = pack()
        val i = p.item("u19-i-prefix-caution-1")!!
        assertTrue(V2Validator.featureValid(p, i))
        assertFalse(V2Validator.featureValid(p, i.copy(prompt="Choose an option.")))
        val wrong = i.copy(assessment=i.assessment!!.copy(testedFeature="DISSUADE_PERFECTIVE"))
        assertFalse(V2Validator.featureValid(p, wrong))
    }
    private fun pack(): V2CurriculumPack = V2PackLoader.parse(
        (System.getProperty("unit1920.instruction.baseline")?.let(::File)
            ?: File(if (File("app").isDirectory) "." else "..", "app/src/main/assets/content/v2-curriculum.json")).readText())
}
