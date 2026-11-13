package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class Unit16InstrumentalUsesTest {
    @Test fun rolesAndLimitsAreTaughtBeforePractice() {
        val pack = pack()
        val claims = mapOf(
            "inst-means" to listOf("tool", "accompaniment line", "between line", "without a preposition"),
            "inst-prep" to listOf("location", "accusative", "destination", "Movement within a location", "in front of", "isolated-word"),
            "complements" to listOf("role or state of the subject", "infinitives", "change", "continuation", "does not demonstrate"),
        )
        for ((slug, required) in claims) {
            val session = pack.unit(16)!!.sessions.first { it.id == "u16-s-$slug" }
            val before = session.steps.takeWhile { it.kind != "GUIDED" }.filter { it.kind == "TEACH" }
            assertEquals(3, before.size)
            val body = before.joinToString("\n") { it.body }
            required.forEach { assertTrue("$slug missing $it", body.contains(it)) }
            val remediation = session.steps.first { it.kind == "REMEDIATE" }
            assertEquals(before.joinToString("\n\n") { it.body }, remediation.body)
            assertEquals(before.flatMap { it.exampleIds }.distinct(), remediation.exampleIds)
        }
    }

    @Test fun feedbackExplainsRolesAndRejectsPastTenseDistractor() {
        val pack = pack()
        for (slug in listOf("inst-means", "inst-prep", "complements")) {
            for (item in pack.items.filter { it.objectiveId == "obj-u16-$slug" }) {
                assertFalse(item.feedback.contains("Does this"))
                assertFalse(item.feedback.contains("frozen source"))
                assertEquals(item.feedback, item.assessment!!.whyAnswerDemonstratesObjective)
                assertEquals(item.distractorRationales, item.assessment!!.distractorDifference)
            }
        }
        assertTrue(pack.item("u16-i-inst-means-1")!!.feedback.contains("pencil"))
        for (id in listOf("u16-i-complements-1", "u16-i-complements-2")) {
            val item = pack.item(id)!!
            val index = item.options.indexOf("became")
            assertTrue(index >= 0)
            assertTrue(item.distractorRationales[index].contains("past-tense"))
            assertTrue(item.distractorRationales[index].contains("does not correctly label"))
            assertTrue(item.prompt.contains("infinitive"))
        }
    }

    @Test fun originalSynthesisRemainsPendingAndValid() {
        val pack = pack()
        val records = pack.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u16-uses-") }
        assertEquals(66, records.size)
        assertTrue(records.all { it.reviewStatus == IMPLEMENTED_PENDING_FINAL_AUDIT })
        assertTrue(records.all { it.synthesis!!.bibliography.contains("oldid 4219602") })
        assertTrue(records.all { it.synthesis!!.bibliography.contains("le41_50_d.htm") })
        val validation = V2Validator.validate(pack)
        assertTrue(validation.errors.joinToString("\n"), validation.ok)
    }

    @Test fun prepositionQuestionsDoNotSupplyTheAnswerAndMutationsFail() {
        val pack = pack()
        for (item in pack.items.filter { it.objectiveId == "obj-u16-inst-prep" }) {
            assertFalse(item.prompt.contains(item.answer, ignoreCase = true))
            val disclosure = item.copy(prompt = "Which admitted English label is the ${item.answer} label?")
            val disclosedPack = pack.copy(items = pack.items.map { if (it.id == item.id) disclosure else it })
            assertTrue(V2Validator.validate(disclosedPack).errors.any { it.contains("prompt discloses") })
            val ambiguous = item.copy(prompt = "Choose one.")
            val ambiguousPack = pack.copy(items = pack.items.map { if (it.id == item.id) ambiguous else it })
            assertTrue(V2Validator.validate(ambiguousPack).errors.any { it.contains("no explicit target") })
            val wrongKey = item.copy(answer = item.options.first { it != item.answer })
            val wrongPack = pack.copy(items = pack.items.map { if (it.id == item.id) wrongKey else it })
            assertFalse(V2Validator.validate(wrongPack).ok)
        }
    }

    private fun pack(): V2CurriculumPack {
        val file = System.getProperty("unit16.uses.baseline")?.let(::File)
            ?: File(if (File("app").isDirectory) "." else "..", "app/src/main/assets/content/v2-curriculum.json")
        return V2PackLoader.parse(file.readText())
    }
}
