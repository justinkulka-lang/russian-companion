package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.security.MessageDigest

class Unit16InstrumentalInstructionTest {
    @Test fun caseAndNumberAreExplainedBeforeGuidedChoices() {
        val pack = pack()
        val sessions = pack.unit(16)!!.sessions
        val required = mapOf(
            "inst-sg" to listOf("instrumental singular", "dative singular", "prepositional singular", "столо́м", "до́мом"),
            "inst-pl" to listOf("instrumental plural", "dative plural", "prepositional plural", "wrong number", "стола́ми"),
            "inst-adj" to listOf("masculine", "neuter", "instrumental plural", "spelling restriction", "хоро́шим"),
        )
        for ((slug, claims) in required) {
            val session = sessions.first { it.id == "u16-s-$slug" }
            val guidedIndex = session.steps.indexOfFirst { it.kind == "GUIDED" }
            val teaching = session.steps.take(guidedIndex).filter { it.kind == "TEACH" }
            assertEquals(session.steps.count { it.kind == "TEACH" }, teaching.size)
            val body = teaching.joinToString("\n") { it.body }
            for (claim in claims) assertTrue("$slug missing $claim", body.contains(claim))
        }
    }

    @Test fun remediationRetainsEveryExplanationAndCaseSpecificFeedback() {
        val pack = pack()
        for (slug in listOf("inst-sg", "inst-pl", "inst-adj")) {
            val session = pack.unit(16)!!.sessions.first { it.id == "u16-s-$slug" }
            val teaching = session.steps.filter { it.kind == "TEACH" }
            val remediation = session.steps.first { it.kind == "REMEDIATE" }
            assertEquals(teaching.joinToString("\n\n") { it.body }, remediation.body)
            assertEquals(teaching.flatMap { it.exampleIds }.distinct(), remediation.exampleIds)
            for (item in pack.items.filter { it.objectiveId == "obj-u16-$slug" }) {
                assertFalse(item.feedback.contains("Does this"))
                assertEquals(item.feedback, item.assessment!!.whyAnswerDemonstratesObjective)
                assertEquals(item.distractorRationales, item.assessment!!.distractorDifference)
                item.options.forEachIndexed { n, option ->
                    if (option != item.answer) {
                        assertTrue(item.distractorRationales[n].contains(option))
                        assertFalse(item.distractorRationales[n].endsWith("?"))
                    }
                }
            }
        }
        val pencil = pack.item("u16-i-inst-sg-2")!!
        assertTrue(pencil.prompt.contains("pencil"))
        assertTrue(pencil.distractorRationales.any { it.contains("husband") })
    }

    @Test fun newExplanationsHavePendingEvidenceAndNoAcceptance() {
        val pack = pack()
        val records = pack.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u16-worked-") }
        assertEquals(87, records.size)
        for (record in records) {
            assertEquals(IMPLEMENTED_PENDING_FINAL_AUDIT, record.reviewStatus)
            assertEquals(sha(record.shippedText), record.contentHash)
            assertEquals(sha(record.originalSourceText), record.sourceHash)
            assertTrue(record.synthesis!!.bibliography.contains("oldid 4219602"))
            assertTrue(record.synthesis!!.bibliography.contains("gr06_a_1_a.htm"))
        }
        assertEquals(IMPLEMENTED_PENDING_FINAL_AUDIT, pack.unitReviews.first { it.unitNumber == 16 }.status)
        val validation = V2Validator.validate(pack)
        assertTrue(validation.errors.joinToString("\n"), validation.ok)
    }

    private fun pack(): V2CurriculumPack {
        val file = System.getProperty("unit16.baseline")?.let(::File)
            ?: File(if (File("app").isDirectory) "." else "..", "app/src/main/assets/content/v2-curriculum.json")
        return V2PackLoader.parse(file.readText())
    }
    private fun sha(text: String) = MessageDigest.getInstance("SHA-256")
        .digest(text.toByteArray()).joinToString("") { "%02x".format(it) }
}
