package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.security.MessageDigest

/**
 * T17f (owner decision D50g, approved rule change; D2 precedent): for obj-u17-age only, a distractor of the age check may be a
 * competing English meaning from another verified frozen Unit 17 record (never content-audit research, synthesis or coverage),
 * bound to obj-u17-age by a frozen selection record and not nested in or around the key; the cue and keyed answer stay on the
 * Russian grammar age-statement line (line 38). u17-i-age-1 offers "forty-one years" (the numbers page).
 */
class AgeCompetingMeaningRuleTest {
    private val pack by lazy { V2PackLoader.parse(File(if (File("app").isDirectory) "." else "..", "app/src/main/assets/content/v2-curriculum.json").readText()) }
    private val item by lazy { pack.item("u17-i-age-1")!! }
    private val records by lazy { pack.provenanceRecords.associateBy { it.itemId } }
    private val bound = "prov-t17f-u17-age-forty-one-years"

    private fun sha(text: String) = MessageDigest.getInstance("SHA-256").digest(text.toByteArray()).joinToString("") { "%02x".format(it) }
    private fun participants(p: V2CurriculumPack = pack) = item.assessment!!.relationParticipantIds.map { id -> p.provenanceRecords.single { it.itemId == id } }
    private fun relation(p: V2CurriculumPack = pack) = participants(p).let { V2Validator.classifyRelation(it[0], it[1], it.drop(2)) }
    private fun withRecord(id: String, change: (V2ProvenanceRecord) -> V2ProvenanceRecord) =
        pack.copy(provenanceRecords = pack.provenanceRecords.map { if (it.itemId == id) change(it) else it })
    private fun itemErrors(p: V2CurriculumPack) = V2Validator.validate(p).errors.filter { "u17-i-age-1" in it || "obj-u17-age" in it || "cap-u17-age" in it }

    @Test fun theShippedAgeCheckUsesTheAllowanceAndValidates() {
        assertEquals(listOf("I am 20 (years old)", "forty-one years"), item.options)
        val (cue, answer) = participants().take(2)
        assertEquals("grammar:age statements:", cue.sourceRecordId.take("grammar:age statements:".length))
        assertEquals(cue.originalSourceText, answer.originalSourceText)
        val competitor = records.getValue(bound)
        val source = records.getValue("prov-ex-u17-years-3-en")
        // The selection record keeps the frozen text and hashes of its numbers-page record; only its id and objective differ.
        assertEquals(source.copy(itemId = bound, objectiveId = "obj-u17-age"), competitor)
        assertNotEquals(cue.originalSourceText, competitor.originalSourceText)
        assertEquals("RULE_CLASSIFIES_SOURCE_EXAMPLE", relation())
        assertTrue(V2Validator.featureValid(pack, item))
        assertEquals(emptyList<String>(), itemErrors(pack))
    }

    @Test fun anUnsourcedOffUnitOrContentAuditDistractorIsRejected() {
        // Unsourced: the distractor's record no longer ships frozen text (its content hash no longer matches what it ships).
        val unsourced = withRecord(bound) { it.copy(shippedText = "forty-two years", contentHash = sha("forty-two years")) }
        assertNotEquals("RULE_CLASSIFIES_SOURCE_EXAMPLE", relation(unsourced))
        assertTrue(itemErrors(unsourced).isNotEmpty())
        // Off-unit: the same frozen gloss bound to another unit.
        val offUnit = withRecord(bound) { it.copy(unitNumber = 9) }
        assertNotEquals("RULE_CLASSIFIES_SOURCE_EXAMPLE", relation(offUnit))
        assertTrue(itemErrors(offUnit).isNotEmpty())
        // Content-audit: research synthesis, coverage or its source is never a competing meaning.
        val audit = pack.provenanceRecords.first { it.sourceId == "src-content-audit-research" && it.sourceRecordId.startsWith("ca-coverage:") }
        val auditBound = withRecord(bound) { audit.copy(itemId = bound, objectiveId = "obj-u17-age", unitNumber = 17, shippedText = "forty-one years") }
        assertNotEquals("RULE_CLASSIFIES_SOURCE_EXAMPLE", relation(auditBound))
        assertTrue(itemErrors(auditBound).isNotEmpty())
        val auditSource = withRecord(bound) { it.copy(sourceId = "src-content-audit-research") }
        assertNotEquals("RULE_CLASSIFIES_SOURCE_EXAMPLE", relation(auditSource))
        assertTrue(itemErrors(auditSource).isNotEmpty())
    }

    @Test fun theAllowanceBelongsToTheAgeObjectiveOnly() {
        // The same records bound to another objective lose the allowance: cue, key and distractor moved to obj-u17-years.
        val ids = item.assessment!!.relationParticipantIds.toSet()
        val moved = pack.copy(provenanceRecords = pack.provenanceRecords.map { if (it.itemId in ids) it.copy(objectiveId = "obj-u17-years") else it })
        assertNotEquals("RULE_CLASSIFIES_SOURCE_EXAMPLE", relation(moved))
        // Full validation rejects it too. The item moves with its records, so its cue, key and distractor provenance still match
        // its objective and the only failure left is the lost allowance (the off-line distractor is unrelated to the feature).
        val movedPack = moved.copy(items = moved.items.map { if (it.id == item.id) it.copy(objectiveId = "obj-u17-years") else it })
        val movedErrors = V2Validator.validate(movedPack).errors
        assertFalse(V2Validator.validate(movedPack).ok)
        assertTrue(movedErrors.joinToString("\n"), "Item u17-i-age-1 distractor is unrelated to the governed feature" in movedErrors)
        assertTrue(movedErrors.none { it.startsWith("Item u17-i-age-1 cue has no contrast provenance") || it.startsWith("Item u17-i-age-1 answer has no contrast provenance") })
        // A cue and key on another span of the same objective lose it too (the allowance is the age-statement line only).
        val otherSpan = withRecord(item.assessment!!.relationParticipantIds[0]) { it.copy(sourceRecordId = "grammar:indirect object") }
        assertNotEquals("RULE_CLASSIFIES_SOURCE_EXAMPLE", relation(otherSpan))
        assertTrue(itemErrors(otherSpan).isNotEmpty())
    }

    /**
     * T17f-n1 (owner decision D53c): the allowance is bound to the frozen source, revision and exact span hash of Russian grammar
     * line 38, not to the record-id prefix. Codex's spoof: the line-37 impersonal-clause span (frozen, verified, same source and
     * revision) filed under the age-statement prefix, with the competing distractor "forty-one years", fails full validation.
     */
    @Test fun aLine37RecordUnderTheAgePrefixDoesNotGetTheAllowance() {
        val root = File(if (File("app").isDirectory) "." else "..")
        val lines = File(root, "catalog/sources/units-26-30/wikipedia/russian-grammar-1369617627.txt").readLines()
        val line38 = lines[37].removePrefix("** ").removeSuffix("';")
        val (cue, answer) = participants().take(2)
        assertEquals(line38, cue.originalSourceText) // the shipped binding is line 38
        val line37 = lines[36].removePrefix("** ").removeSuffix(";")
        assertTrue(line37, line37.startsWith("impersonal clauses:"))
        val shown = Regex("\\{\\{lang\\|ru\\|([^}]+)\\}\\}").find(line37)!!.groupValues[1]
        val meaning = Regex("'([^']+)'").find(line37)!!.groupValues[1]
        val prefixed = "grammar:age statements: " + line37.take(60)
        fun spoof(r: V2ProvenanceRecord, text: String) = r.copy(sourceRecordId = prefixed, originalSourceText = line37,
            sourceHash = sha(line37), shippedText = text, contentHash = sha(text))
        val spoofed = pack.copy(
            provenanceRecords = pack.provenanceRecords.map {
                when (it.itemId) { cue.itemId -> spoof(it, shown); answer.itemId -> spoof(it, meaning); else -> it }
            },
            items = pack.items.map {
                if (it.id != item.id) it else it.copy(promptLetter = shown, answer = meaning, options = listOf(meaning, "forty-one years"),
                    assessment = it.assessment!!.copy(contrastMembers = listOf(meaning, "forty-one years")))
            },
        )
        assertEquals(listOf(meaning, "forty-one years"), spoofed.item(item.id)!!.options)
        assertNotEquals("RULE_CLASSIFIES_SOURCE_EXAMPLE", relation(spoofed))
        assertFalse(V2Validator.featureValid(spoofed, spoofed.item(item.id)!!))
        assertFalse(V2Validator.validate(spoofed).ok)
        // The same spoof keeps the line-38 hash but not its text: the record no longer verifies, so it fails too.
        val forged = withRecord(cue.itemId) { it.copy(sourceRecordId = prefixed, originalSourceText = line37) }
        assertNotEquals("RULE_CLASSIFIES_SOURCE_EXAMPLE", relation(forged))
        assertFalse(V2Validator.validate(forged).ok)
    }

    @Test fun aDistractorThatNestsInOrAroundTheKeyIsRejected() {
        // "years" (inside the key's tokens) and a gloss that would contain the whole key are both rejected by the allowance.
        val inside = withRecord(bound) { it.copy(shippedText = "years", contentHash = sha("years")) }
        assertNotEquals("RULE_CLASSIFIES_SOURCE_EXAMPLE", relation(inside))
        val key = records.getValue(item.assessment!!.relationParticipantIds[1])
        val around = pack.copy(provenanceRecords = pack.provenanceRecords.map {
            if (it.itemId == key.itemId) it.copy(shippedText = "years", contentHash = sha("years")) else it
        })
        assertNotEquals("RULE_CLASSIFIES_SOURCE_EXAMPLE", relation(around))
        // The response-shape rule rejects a nested option pair on its own as well.
        val nestedItem = item.copy(options = listOf(item.answer, "20 years"),
            assessment = item.assessment!!.copy(contrastMembers = listOf(item.answer, "20 years")))
        assertFalse(V2Validator.validate(pack.copy(items = pack.items.map { if (it.id == item.id) nestedItem else it })).ok)
    }
}
