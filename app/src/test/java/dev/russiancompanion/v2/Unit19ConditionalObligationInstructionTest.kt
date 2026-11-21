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
        assertTrue(body("conditional").contains("It cannot begin its clause"))
        assertTrue(body("obligation").contains("not literal translations"))
        assertTrue(body("obligation").contains("subject is nominative"))
        assertTrue(body("probability").contains("not a duty imposed on the eclipse"))
        assertTrue(body("probability").contains("other classifies the employee obligation"))
        assertNotEquals(p.item("u19-i-probability-1")!!.promptLetter, p.item("u19-i-probability-2")!!.promptLetter)
    }
    @Test fun feedbackAndPromptsAskForTheActualRetainedAnswerType() {
        val p = pack()
        val items = p.items.filter { it.objectiveId in listOf("obj-u19-conditional", "obj-u19-obligation", "obj-u19-probability") }
        assertEquals(9, items.size)
        items.forEach { item ->
            assertTrue(item.feedback.contains(item.answer))
            assertFalse(item.feedback.contains("frozen source"))
            assertTrue(item.assessment!!.whyAnswerDemonstratesObjective.endsWith(item.feedback))
            assertEquals(item.distractorRationales, item.assessment!!.distractorDifference)
            item.options.forEachIndexed { n, option -> if (option != item.answer) assertTrue(item.distractorRationales[n].contains(option)) }
        }
        assertTrue(p.item("u19-i-conditional-1")!!.prompt.contains("speaker description"))
        assertTrue(p.item("u19-i-obligation-1")!!.prompt.contains("subject-agreement label"))
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
