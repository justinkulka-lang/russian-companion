package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2Validator
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import kotlinx.serialization.json.*

/** Owner step 7: every Unit 6 fact is marked; whole non-fact directions are reviewed explicitly.
 * This is a content review check, not a word classifier or a change to the field-based validator.
 */
class Unit6FactProvenanceTest {
    private val fields = setOf("item feedback", "item distractor rationale", "remediation body")
    private val reviewedNonFacts = setOf(
        "Guided practice chooses the supplied object form, without inventing a sentence or new stress.",
        "Recognize supplied forms and contexts, not unseen or unrestricted productive mastery.",
        "Recognize these supplied contexts; no universal all-case or completed-action claim.",
        "Start with the nominative forms below, then compare the feminine accusative example.",
        "The independent teacher check retains this exact pair.",
        "The new independent check classifies that inanimate context rather than repeating the teacher answer.",
        "The other option repeats the shown order.",
        "Compare your answer with the worked examples for this form, then try again.",
        "Stress marks are optional when you type.",
    )

    @Test
    fun unitSixFeedbackAndRemediationHaveNoUnmarkedFactSentences() {
        val defects = unmarkedSentences(pack())
        assertTrue(defects.joinToString("\n"), defects.isEmpty())
    }

    @Test
    fun everyEnglishOnlyBaselineFactHasAVerifiedCurrentDisposition() {
        val pack = pack()
        val all = inventory().filter { it[0] == "6" }
        assertEquals("Do not hide entries by deleting baseline inventory rows", 145, all.size)
        val entries = all.filter { it[4].startsWith("English-only") }
        assertEquals(67, entries.size)
        val dispositions = Json.parseToJsonElement(File(repo(), "app/src/test/resources/unit6-fact-dispositions.json").readText())
            .jsonObject.getValue("entries").jsonArray.map { it.jsonObject }
        fun JsonObject.string(key: String) = getValue(key).jsonPrimitive.content
        assertEquals(67, dispositions.size)
        assertEquals(entries.map { listOf(it[1], it[2], it[3]) }.toSet(), dispositions.map {
            listOf(it.string("ownerId"), it.string("field"), it.string("baselineSentence"))
        }.toSet())
        val hosts = hosts(pack)
        dispositions.forEach { disposition ->
            val matching = hosts.filter { it.ownerId == disposition.string("ownerId") && it.field == disposition.string("field") }
            val baseline = disposition.string("baselineSentence")
            val currentSentences = matching.flatMap { host -> V2Validator.englishSentenceRanges(host.text).map {
                host.text.substring(it.first, it.last + 1)
            } }
            when (disposition.string("disposition")) {
                "MARKED" -> {
                    assertTrue(baseline in currentSentences)
                    assertTrue(matching.any { baseline in it.russianFacts })
                    assertTrue(disposition.getValue("sourceRecordIds").jsonArray.isNotEmpty())
                }
                "REMOVED" -> assertFalse("Removed standalone sentence survives unaccounted: $baseline", baseline in currentSentences)
                else -> fail("Unknown disposition: $disposition")
            }
            // Rewording/removal is allowed; every surviving replacement fact still needs an actual source and marker.
            disposition.getValue("sourceRecordIds").jsonArray.forEach { source ->
                val record = pack.provenanceRecords.single { it.itemId == source.jsonPrimitive.content }
                assertEquals(6, record.unitNumber)
                assertTrue(matching.any { record.shippedText in it.russianFacts && record.shippedText in it.text })
            }
        }
    }

    @Test
    fun markedUnitSixFactsHaveActualProvenanceAndCannotUseScaffoldsInstead() {
        val pack = pack()
        val errors = V2Validator.validate(pack).errors
        assertTrue(errors.joinToString("\n"), errors.isEmpty())
        // Keep all authored-English scaffold records: removing actual provenance must still expose marked facts.
        val withoutSources = pack.copy(provenanceRecords = pack.provenanceRecords.filter { it.unitNumber != 6 })
        assertTrue(V2Validator.validate(withoutSources).errors.any {
            it.contains("English linguistic claim has no admitted source record") && it.contains("marked sentence in unit 6")
        })
    }

    @Test
    fun removingAnEnglishFactMarkerFailsTheUnitReviewCheck() {
        val pack = pack()
        val row = inventory().first { it[0] == "6" && it[2] == "item feedback" && it[4].startsWith("English-only") }
        val changed = pack.copy(items = pack.items.map { item ->
            if (item.id == row[1]) item.copy(russianFacts = item.russianFacts.filterNot { it == row[3] }) else item
        })
        assertTrue(unmarkedSentences(changed).any { row[1] in it && row[3] in it })
    }

    @Test
    fun aNewUnmarkedFactCannotEscapeBecauseItIsAbsentFromTheBaselineInventory() {
        val pack = pack()
        val item = pack.items.first { it.id == "u6-i-third-1" }
        // Reuse an existing inventory claim as an English-only fixture; invent no Russian text or fact.
        val fact = inventory().first {
            it[0] == "7" && it[2] == "item feedback" && it[4].startsWith("English-only") &&
                it[3] !in item.russianFacts && it[3] !in reviewedNonFacts
        }[3]
        val changed = pack.copy(items = pack.items.map {
            if (it.id == item.id) it.copy(feedback = it.feedback + " " + fact) else it
        })
        assertTrue(unmarkedSentences(changed).any { item.id in it && fact in it })
    }

    private fun hosts(pack: V2CurriculumPack) = V2Validator.englishProvenanceHosts(pack, includeAllUnits = true)
        .filter { it.unitNumber == 6 && it.field in fields }
        .distinctBy { listOf(it.ownerId, it.field, it.text) }

    private fun unmarkedSentences(pack: V2CurriculumPack): List<String> = hosts(pack).flatMap { host ->
        V2Validator.englishSentenceRanges(host.text).mapNotNull { range ->
            val sentence = host.text.substring(range.first, range.last + 1)
            if (!sentence.any(Char::isLetter) || sentence in host.russianFacts || sentence in reviewedNonFacts) null
            else "${host.ownerId} ${host.field}: $sentence"
        }
    }

    private fun inventory(): List<List<String>> {
        val text = File(repo(), "audit/T15-n0-unmarked-russian-facts.csv").readText()
        val rows = mutableListOf<List<String>>()
        var row = mutableListOf<String>()
        val cell = StringBuilder()
        var quoted = false
        var index = 0
        while (index < text.length) {
            val char = text[index]
            when {
                char == '"' && quoted && text.getOrNull(index + 1) == '"' -> { cell.append('"'); index++ }
                char == '"' -> quoted = !quoted
                char == ',' && !quoted -> { row.add(cell.toString()); cell.clear() }
                char == '\n' && !quoted -> {
                    row.add(cell.toString()); cell.clear(); rows.add(row); row = mutableListOf()
                }
                char == '\r' && !quoted -> Unit
                else -> cell.append(char)
            }
            index++
        }
        assertFalse("Malformed baseline CSV", quoted)
        if (cell.isNotEmpty() || row.isNotEmpty()) { row.add(cell.toString()); rows.add(row) }
        assertEquals(listOf("unit", "owner_id", "field", "sentence", "protection"), rows.first())
        assertTrue(rows.all { it.size == 5 })
        return rows.drop(1)
    }

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())
    private fun repo() = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
