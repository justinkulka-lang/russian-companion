package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class Unit28InstructionTest {
    @Test fun timingIsRelativeAndTheExplicitMainSubjectPerformsBothActions() {
        val p = pack()
        val imperfective = lesson(p, "u28-s-imperfective-adverb")
        assertTrue(imperfective.contains("Reading and smiling overlap"))
        assertTrue(imperfective.contains("The pronoun he is the explicit main-clause subject"))
        assertTrue(imperfective.contains("cannot establish the tense of an absent main verb"))
        val perfective = lesson(p, "u28-s-perfective-adverb")
        assertTrue(perfective.contains("writing precedes the future sending"))
        assertTrue(perfective.contains("does not require the writing to have happened before the present moment"))
        assertTrue(perfective.contains("writer and sender are the same person"))
        assertTrue(perfective.contains("not a complete productive algorithm"))
    }

    @Test fun passageInstructionPrecedesPracticeAndDistinguishesEpisodes() {
        val p = pack()
        val s = p.unit(28)!!.sessions.single { it.id == "u28-s-readings" }
        assertEquals(2, s.steps.takeWhile { it.kind != "GUIDED" }.count { it.kind == "TEACH" })
        val text = lesson(p, s.id)
        assertTrue(text.contains("reading accompanies the resting"))
        assertTrue(text.contains("stands, with сложа руки, with arms folded"))
        assertTrue(text.contains("do not establish that every episode has the same actor"))
        for (id in listOf("u28-g-readings-0", "u28-i-readings-1", "u28-i-readings-2")) {
            val item = p.item(id)!!
            assertFalse(item.prompt.contains("admitted"))
            assertFalse(item.feedback.contains("admitted"))
        }
    }

    @Test fun remediationRestoresAllCardsAndThePreviouslyOmittedThirdExample() {
        val u = pack().unit(28)!!
        for (s in u.sessions.filter { it.id in listOf("u28-s-imperfective-adverb", "u28-s-perfective-adverb", "u28-s-readings") }) {
            val teaching = s.steps.filter { it.kind == "TEACH" }
            val remediation = s.steps.single { it.kind == "REMEDIATE" }
            assertEquals(teaching.joinToString("\n\n") { it.body }, remediation.body)
            assertEquals(teaching.flatMap { it.exampleIds }.distinct(), remediation.exampleIds)
        }
        assertEquals("DEVELOPMENT", u.installState)
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", u.readiness)
    }

    @Test fun newPendingProvenanceAndFullProductionValidationRemainValid() {
        val p = pack()
        val records = p.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u28-instruction-") }
        assertEquals(81, records.size)
        records.forEach { assertContentAuditResearch(it) }
        val result = V2Validator.validate(p)
        assertTrue(result.errors.joinToString("\n"), result.ok)
    }

    @Test fun mutableMetadataCannotDropTheMainClauseFromAContextualCue() {
        val p = pack()
        for (objective in listOf("obj-u28-imperfective-adverb", "obj-u28-perfective-adverb")) {
            val cap = p.sourceCapabilities.single { it.id == "cap-$objective" }
            val items = p.items.filter { it.objectiveId == objective }
            val oldCue = requireNotNull(items.first().promptLetter)
            val bareCue = oldCue.substringBefore(" ")
            val cueIds = items.map { it.assessment!!.relationParticipantIds.first() }.toSet()
            val records = p.provenanceRecords.map { r ->
                if (r.sourceRecordId != cap.sourceRecordId || r.sourceHash != cap.sourceHash) r
                else {
                    val text = if (r.itemId in cueIds) bareCue else r.shippedText
                    r.copy(shippedText = text, contentHash = hash(text), synthesis = r.synthesis!!.copy(
                        applications = r.synthesis.applications.map { if (it.cue == oldCue) it.copy(cue = bareCue) else it }))
                }
            }
            val forged = p.copy(items = p.items.map { if (it.objectiveId == objective) it.copy(promptLetter = bareCue) else it }, provenanceRecords = records)
            assertEquals(cap.exactSpan, forged.sourceCapabilities.single { it.id == cap.id }.exactSpan)
            assertTrue("The hashed full sentence must bind the timing and shared-subject application: $objective",
                correctedCapabilityDefects(forged, cap).any { it.contains("exact") && it.contains("applications") })
        }
    }

    private fun hash(text: String) = java.security.MessageDigest.getInstance("SHA-256")
        .digest(text.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it.toInt() and 255) }

    private fun lesson(p: V2CurriculumPack, id: String) = p.unit(28)!!.sessions.single { it.id == id }
        .steps.filter { it.kind == "TEACH" }.joinToString("\n") { it.body }
    private fun pack() = V2PackLoader.parse(File(if (File("app").isDirectory) "." else "..", "app/src/main/assets/content/v2-curriculum.json").readText())
}
