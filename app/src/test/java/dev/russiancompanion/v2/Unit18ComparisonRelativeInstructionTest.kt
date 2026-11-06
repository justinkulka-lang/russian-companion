package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class Unit18ComparisonRelativeInstructionTest {
    @Test fun allCheckedFormsAreTaughtBeforeGuidedPracticeAndRemediated() {
        val p = pack()
        for (slug in listOf("comparison", "relative")) {
            val session = p.unit(18)!!.sessions.first { it.id == "u18-s-$slug" }
            val teaches = session.steps.takeWhile { it.kind != "GUIDED" }.filter { it.kind == "TEACH" }
            assertEquals(3, teaches.size)
            val body = teaches.joinToString("\n") { it.body }
            p.items.filter { it.objectiveId == "obj-u18-$slug" }.forEach { item ->
                assertTrue("${item.id} cue not taught", body.contains(item.promptLetter!!))
                assertTrue("${item.id} meaning not taught", body.contains(item.answer))
            }
            val rem = session.steps.first { it.kind == "REMEDIATE" }
            assertEquals(teaches.joinToString("\n\n") { it.body }, rem.body)
            assertEquals(teaches.flatMap { it.exampleIds }.distinct(), rem.exampleIds)
        }
    }
    @Test fun instructionDistinguishesConstructionAndScope() {
        val p = pack()
        val comparison = p.unit(18)!!.sessions.first { it.id == "u18-s-comparison" }.steps.joinToString("\n") { it.body }
        assertTrue(comparison.contains("does not change for gender, number or case"))
        assertTrue(comparison.contains("the adjective still changes for agreement"))
        assertTrue(comparison.contains("best in context"))
        assertTrue(comparison.contains("Qualified review"))
        val relative = p.unit(18)!!.sessions.first { it.id == "u18-s-relative" }.steps.joinToString("\n") { it.body }
        assertTrue(relative.contains("direct questions"))
        assertTrue(relative.contains("case comes from the role inside the relative clause"))
        assertTrue(relative.contains("instrumental case"))
        assertTrue(relative.contains("does not demonstrate independently"))
    }
    @Test fun feedbackExplainsTheRequestedMeaningAndPreservesCitation() {
        val p = pack()
        val items = p.items.filter { it.objectiveId in listOf("obj-u18-comparison", "obj-u18-relative") }
        assertEquals(6, items.size)
        items.forEach { item ->
            assertEquals("Which English meaning matches the shown form?", item.prompt)
            assertTrue(item.feedback.contains(item.answer))
            assertFalse(item.feedback.contains("frozen source"))
            assertTrue(item.assessment!!.whyAnswerDemonstratesObjective.endsWith(item.feedback))
            assertEquals(item.distractorRationales, item.assessment!!.distractorDifference)
            item.options.forEachIndexed { n, option -> if (option != item.answer) assertTrue(item.distractorRationales[n].contains(option)) }
        }
    }
    @Test fun newCoverageStaysPendingAndSourceRelationshipsValidate() {
        val p = pack()
        val records = p.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u18-instruction-") }
        assertEquals(52, records.size)
        records.forEach { assertContentAuditResearch(it) }
        val v = V2Validator.validate(p)
        assertTrue(v.errors.joinToString("\n"), v.ok)
        assertEquals("DEVELOPMENT", p.unit(18)!!.installState)
    }
    private fun pack(): V2CurriculumPack {
        val file = System.getProperty("unit18.instruction.baseline")?.let(::File)
            ?: File(if (File("app").isDirectory) "." else "..", "app/src/main/assets/content/v2-curriculum.json")
        return V2PackLoader.parse(file.readText())
    }
}
