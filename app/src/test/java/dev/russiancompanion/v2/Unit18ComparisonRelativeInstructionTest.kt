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
                // T17c: typed items join (16 checks, 2 guided). A typed cue may be its taught example with the key blanked ("___"),
                // so the cue is checked with the key restored (was: the cue as shown); the typed key is the taught Russian answer.
                assertTrue("${item.id} cue not taught", body.contains(item.promptLetter!!.replace("___", item.answer)))
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
        // T17c: the sweep row (A2-26 governance) replaces the pending-review sentence with an instruction (was: contains "Qualified review").
        assertFalse(comparison.contains("Qualified review"))
        assertTrue(comparison.contains("Interpret the gloss in its sentence context."))
        val relative = p.unit(18)!!.sessions.first { it.id == "u18-s-relative" }.steps.joinToString("\n") { it.body }
        assertTrue(relative.contains("direct questions"))
        assertTrue(relative.contains("case comes from the role inside the relative clause"))
        assertTrue(relative.contains("instrumental case"))
        // T17c: prep section 3 removes the commentary on what recognition demonstrates (line 2183); the unit now practises the
        // relative forms it checks by typing (was: contains "does not demonstrate independently").
        assertFalse(relative.contains("does not demonstrate independently"))
        assertTrue(relative.contains("then write only its form of the word for which"))
    }
    @Test fun feedbackExplainsTheRequestedMeaningAndPreservesCitation() {
        val p = pack()
        // T17c: the 16 typed checks and 2 typed guided items have no options and a Russian key; the six closed items keep every
        // assertion below (was: all 6 Unit 18 items, before typed items existed).
        val all = p.items.filter { it.objectiveId in listOf("obj-u18-comparison", "obj-u18-relative") }
        assertEquals(24, all.size)
        assertEquals(18, all.count { it.kind == "TYPED_FORM" })
        val items = all.filter { it.kind != "TYPED_FORM" }
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
