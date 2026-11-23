package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class Unit19ConditionalObligationInstructionTest {
    @Test fun checkedFormsHaveCompleteInstructionBeforePracticeAndOnRemediation() {
        val p = pack()
        for ((slug, count) in mapOf("conditional" to 3, "obligation" to 3, "probability" to 2)) {
            val session = p.unit(19)!!.sessions.first { it.id == "u19-s-$slug" }
            val teaches = session.steps.takeWhile { it.kind != "GUIDED" }.filter { it.kind == "TEACH" }
            assertEquals(count, teaches.size)
            val body = teaches.joinToString("\n") { it.body }
            p.items.filter { it.objectiveId == "obj-u19-$slug" }.forEach { item ->
                if (item.promptLetter!!.any { it.code in 0x0400..0x04ff }) assertTrue("${item.id} missing cue", body.contains(item.promptLetter!!))
                assertTrue("${item.id} missing answer", body.contains(item.answer))
            }
            val rem = session.steps.first { it.kind == "REMEDIATE" }
            assertEquals(teaches.joinToString("\n\n") { it.body }, rem.body)
            assertEquals(teaches.flatMap { it.exampleIds }.distinct(), rem.exampleIds)
        }
    }
    @Test fun labelsAreNotTaughtAsTranslationsAndConditionalDoesNotImplyPastEvent() {
        val p = pack()
        fun body(slug: String) = p.unit(19)!!.sessions.first { it.id == "u19-s-$slug" }.steps.joinToString("\n") { it.body }
        assertTrue(body("conditional").contains("not translations"))
        assertTrue(body("conditional").contains("need not report an actual past event"))
        // T17d: S1 PARTIAL (Wiktionary бы 94001807 line 58 says sentence, not clause): the claim is reworded to the frozen text
        // (was: contains "It cannot begin its clause").
        assertFalse(body("conditional").contains("It cannot begin its clause"))
        assertTrue(body("conditional").contains("The particle cannot begin a sentence."))
        assertTrue(body("obligation").contains("not literal translations"))
        assertTrue(body("obligation").contains("subject is nominative"))
        assertTrue(body("probability").contains("not a duty imposed on the eclipse"))
        // T17d: the sweep row (A2-26 assessment commentary) replaces the description of the two checks with an instruction
        // (was: contains "other classifies the employee obligation").
        assertFalse(body("probability").contains("other classifies the employee obligation"))
        assertTrue(body("probability").contains("Distinguish the eclipse sentence’s meaning from the employee’s obligation."))
        assertNotEquals(p.item("u19-i-probability-1")!!.promptLetter, p.item("u19-i-probability-2")!!.promptLetter)
    }
    @Test fun feedbackAndPromptsAskForTheActualRetainedAnswerType() {
        val p = pack()
        // T17d: the 7 typed checks and 1 typed guided item of these objectives have no options and a Russian key; the nine closed
        // items keep every assertion below (was: all 9 items, before typed items existed).
        val all = p.items.filter { it.objectiveId in listOf("obj-u19-conditional", "obj-u19-obligation", "obj-u19-probability") }
        assertEquals(17, all.size)
        val items = all.filter { it.kind != "TYPED_FORM" }
        assertEquals(9, items.size)
        items.forEach { item ->
            assertTrue(item.feedback.contains(item.answer))
            assertFalse(item.feedback.contains("frozen source"))
            assertTrue(item.assessment!!.whyAnswerDemonstratesObjective.endsWith(item.feedback))
            assertEquals(item.distractorRationales, item.assessment!!.distractorDifference)
            item.options.forEachIndexed { n, option -> if (option != item.answer) assertTrue(item.distractorRationales[n].contains(option)) }
        }
        assertTrue(p.item("u19-i-conditional-1")!!.prompt.contains("speaker description"))
        // T17d (prep section 1, A2-24 label/position finding): the obligation checks ask which subject description matches the
        // agreement form (was: the prompt asked for a "subject-agreement label").
        assertTrue(p.item("u19-i-obligation-1")!!.prompt.contains("subject description matches the shown agreement form"))
    }
    @Test fun synthesisAndSourceChainsValidateWithoutApprovingReview() {
        val p = pack()
        val records = p.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u19-instruction-") }
        assertEquals(73, records.size)
        records.forEach { assertContentAuditResearch(it) }
        val validation = V2Validator.validate(p)
        assertTrue(validation.errors.joinToString("\n"), validation.ok)
        assertEquals("DEVELOPMENT", p.unit(19)!!.installState)
    }
    private fun pack(): V2CurriculumPack {
        val file = System.getProperty("unit19.instruction.baseline")?.let(::File)
            ?: File(if (File("app").isDirectory) "." else "..", "app/src/main/assets/content/v2-curriculum.json")
        return V2PackLoader.parse(file.readText())
    }
}
