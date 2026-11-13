package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class Unit27RemainingInstructionTest {
    @Test fun comprehensionSeparatesTheWriterFromAnUnspecifiedReaderBeforePractice() {
        val p = pack()
        val s = p.unit(27)!!.sessions.single { it.id == "u27-s-gap-comprehension" }
        val teaching = s.steps.takeWhile { it.kind != "GUIDED" }.filter { it.kind == "TEACH" }
        assertEquals("Both readings must be explained before the first guided choice", 2, teaching.size)
        val text = teaching.joinToString("\n") { it.body }
        assertTrue(text.contains("Anna is the named writer"))
        assertTrue(text.contains("Anna might or might not have read it"))
        assertTrue(text.contains("does not name the reader"))
        assertTrue(text.contains("main statement locates the letter on the table"))
        assertTrue(text.contains("not an ongoing action by Anna"))
        assertTrue(p.item("u27-i-comp-2")!!.feedback.contains("does not identify that reader"))
    }

    @Test fun optionalPresentTeachingDistinguishesFiniteInfinitiveAndPassiveMeanings() {
        val p = pack()
        val s = p.unit(27)!!.sessions.single { it.id == "u27-s-present-passive" }
        val teaching = s.steps.takeWhile { it.kind != "GUIDED" }.filter { it.kind == "TEACH" }
        assertEquals("Optional study must also teach both cards before practice", 2, teaching.size)
        val text = teaching.joinToString("\n") { it.body }
        assertTrue(text.contains("first-person plural present-tense form"))
        assertTrue(text.contains("not itself a participle"))
        assertTrue(text.contains("does not by itself name a we subject"))
        assertTrue(text.contains("does not establish which other verbs permit a present passive"))
        assertTrue(text.contains("retained accents are on the third syllable"))
        for (id in listOf("u27-g-present-passive-0", "u27-i-present-passive-1", "u27-i-present-passive-2")) {
            val item = p.item(id)!!
            assertFalse("The learner must interpret meaning rather than a source's layout", item.prompt.contains("parenthetical") || item.prompt.contains("admitted"))
            assertTrue(item.feedback.contains(item.answer))
            for (option in item.options) assertTrue("Untaught option $option", text.contains(option))
        }
    }

    @Test fun remediationRestoresEveryCardAndExampleWithoutMakingOptionalStudyRequired() {
        val p = pack()
        val unit = p.unit(27)!!
        for (id in listOf("u27-s-gap-comprehension", "u27-s-present-passive")) {
            val s = unit.sessions.single { it.id == id }
            val teaching = s.steps.filter { it.kind == "TEACH" }
            val remediation = s.steps.single { it.kind == "REMEDIATE" }
            assertEquals(teaching.joinToString("\n\n") { it.body }, remediation.body)
            assertEquals(teaching.flatMap { it.exampleIds }.distinct(), remediation.exampleIds)
            assertEquals(2, remediation.exampleIds.size)
        }
        assertFalse(unit.objectives.single { it.id == "obj-u27-present-passive" }.gating)
        assertTrue(unit.objectives.single { it.id == "obj-u27-comprehension" }.gating)
        assertEquals(listOf("u27-i-stress-2", "u27-i-stress-3", "u27-i-stress-4"), unit.objectives.single { it.id == "obj-u27-stress" }.checkItemIds)
        assertEquals("DEVELOPMENT", unit.installState)
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", unit.readiness)
    }

    @Test fun pendingInstructionHasBoundProvenanceAndTheProductionValidatorAcceptsIt() {
        val p = pack()
        val records = p.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u27-remaining-") }
        assertEquals(73, records.size)
        records.forEach { assertContentAuditResearch(it) }
        val result = V2Validator.validate(p)
        assertTrue(result.errors.joinToString("\n"), result.ok)
    }

    @Test fun mutableApplicationMetadataCannotRebindTheAlreadyReadCheckToTheWritingCue() {
        val p = pack()
        val cap = p.sourceCapabilities.single { it.id == "cap-obj-u27-comprehension" }
        val item = p.item("u27-i-comp-3")!!
        val cue = requireNotNull(p.item("u27-i-comp-1")!!.promptLetter)
        val cueRecord = item.assessment!!.relationParticipantIds.first()
        val records = p.provenanceRecords.map { record ->
            if (record.sourceRecordId != cap.sourceRecordId || record.sourceHash != cap.sourceHash) record
            else {
                val evidence = record.synthesis!!
                val applications = evidence.applications.map { application ->
                    if (application.cue == item.promptLetter) application.copy(cue = cue) else application
                }
                val text = if (record.itemId == cueRecord) cue else record.shippedText
                record.copy(shippedText = text, contentHash = hash(text),
                    synthesis = evidence.copy(applications = applications))
            }
        }
        val forged = p.copy(items = p.items.map { if (it.id == item.id) it.copy(promptLetter = cue) else it },
            provenanceRecords = records)
        assertEquals(cap.exactSpan, forged.sourceCapabilities.single { it.id == cap.id }.exactSpan)
        assertEquals(cap.sourceHash, forged.sourceCapabilities.single { it.id == cap.id }.sourceHash)
        assertTrue("A writing cue cannot acquire the unrelated already-read answer through mutable metadata",
            correctedCapabilityDefects(forged, cap).any { it.contains("exact") && it.contains("applications") })
    }

    private fun hash(text: String): String = java.security.MessageDigest.getInstance("SHA-256")
        .digest(text.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it.toInt() and 255) }

    private fun pack(): V2CurriculumPack = V2PackLoader.parse(
        (System.getProperty("unit27.remaining.baseline")?.let(::File)
            ?: File(if (File("app").isDirectory) "." else "..", "app/src/main/assets/content/v2-curriculum.json")).readText(),
    )
}
