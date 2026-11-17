package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2Validator
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import kotlinx.serialization.json.*

/** T15c (owner direction 11/16/2026): every Unit 8 fact is marked with frozen-source citations; whole instructions
 * that state no Russian fact are reviewed explicitly; every shown phrase passes the two-part phrase rule.
 * This is a content review check, not a word classifier or a change to the field-based validator.
 */
class Unit8FactProvenanceTest {
    private val fields = setOf("item feedback", "item distractor rationale", "remediation body")
    private val reviewedNonFacts = setOf(
        "Compare your answer with the worked examples for this form, then try again.",
        "Stress marks are optional when you type.",
        "Copy the displayed form carefully.",
        "Use the forms taught here.",
        "Practise the displayed pairs.",
        "Copy the whole displayed form.",
        "Use this taught pair for the item.",
        "Review these seven pairs.",
        "This lesson compares singular and plural nominative forms, then explains the spelling in the plurals.",
        "Read the requested form before answering.",
        "Identify number from the requested form and context.",
        "Choose the requested form.",
        "Compare the examples.",
        "Read the whole phrase before answering.",
        "Practise the shown forms.",
        "Compare the shown singular and plural forms.",
        "Recognize what exists and where the line places it.",
        "Keep each whole source line with its own supplied translation.",
        // T15f (owner decision D8): the same instruction without source framing.
        "Keep each whole line with its own translation.",
        "Distinguish that noun from the location phrase.",
        "These two supplied examples do not establish an unrestricted sentence-generation rule.",
    )

    @Test
    fun unitEightFeedbackAndRemediationHaveNoUnmarkedFactSentences() {
        val defects = unmarkedSentences(pack())
        assertTrue(defects.joinToString("\n"), defects.isEmpty())
    }

    @Test
    fun addingAnUnmarkedFactSentenceToUnitEightFails() {
        // Regression: a fact sentence added without a marker is reported. The fixture reuses a removed Unit 8 baseline
        // claim; it invents no Russian text or fact.
        val pack = pack()
        val fact = "These patterns are word-specific recognition evidence, not permission to generate an unlisted form."
        assertTrue(unmarkedSentences(pack).isEmpty())
        val item = pack.items.single { it.id == "u8-i-plural-1" }
        val withFeedback = pack.copy(items = pack.items.map { if (it.id == item.id) it.copy(feedback = it.feedback + " " + fact) else it })
        assertTrue(unmarkedSentences(withFeedback).any { item.id in it && fact in it })
        val remediation = "u8-st-rem-plural"
        val withRemediation = pack.copy(units = pack.units.map { unit ->
            unit.copy(sessions = unit.sessions.map { session ->
                session.copy(steps = session.steps.map { if (it.id == remediation) it.copy(body = it.body + "\n\n" + fact) else it })
            })
        })
        assertTrue(unmarkedSentences(withRemediation).any { remediation in it && fact in it })
        // Removing an existing marker is reported too.
        val marked = pack.items.first { it.objectiveId.startsWith("obj-u8-") && it.russianFacts.isNotEmpty() }
        val unmarked = pack.copy(items = pack.items.map { if (it.id == marked.id) it.copy(russianFacts = it.russianFacts.drop(1)) else it })
        assertTrue(unmarkedSentences(unmarked).any { marked.id in it && marked.russianFacts.first() in it })
    }

    @Test
    fun everyEnglishOnlyBaselineFactHasAVerifiedCurrentDisposition() {
        val pack = pack()
        val entries = inventory().filter { it[0] == "8" && it[4].startsWith("English-only") }
        assertEquals("Do not hide entries by deleting baseline inventory rows", 47, entries.size)
        val json = Json.parseToJsonElement(File(repo(), "app/src/test/resources/unit8-fact-dispositions.json").readText()).jsonObject
        val dispositions = json.getValue("entries").jsonArray.map { it.jsonObject }
        fun JsonObject.string(key: String) = getValue(key).jsonPrimitive.content
        assertEquals(47, dispositions.size)
        assertEquals(entries.map { listOf(it[1], it[2], it[3]) }.toSet(), dispositions.map {
            listOf(it.string("ownerId"), it.string("field"), it.string("baselineSentence"))
        }.toSet())
        val totals = dispositions.groupingBy { it.string("disposition") }.eachCount()
        assertEquals(mapOf("MARK" to 27, "REWORD" to 15, "REMOVE" to 5), totals)
        assertEquals(totals, json.getValue("totals").jsonObject.mapValues { it.value.jsonPrimitive.int })
        val hosts = hosts(pack)
        dispositions.forEach { disposition ->
            val owner = disposition.string("ownerId")
            val matching = hosts.filter { it.ownerId == owner && it.field == disposition.string("field") }
            assertTrue(owner, matching.isNotEmpty())
            val baseline = disposition.string("baselineSentence")
            val current = matching.flatMap { host -> V2Validator.englishSentenceRanges(host.text).map { host.text.substring(it.first, it.last + 1) } }
            val cited = disposition.getValue("sourceRecordIds").jsonArray.map { it.jsonPrimitive.content }
            when (disposition.string("disposition")) {
                "MARK" -> {
                    assertTrue(baseline, baseline in current)
                    assertTrue(baseline, matching.any { baseline in it.russianFacts })
                    assertEquals(baseline, factSources(pack, owner).getValue(baseline).toSet(), cited.toSet())
                }
                "REWORD" -> {
                    assertFalse("Reworded sentence survives: $baseline", baseline in current)
                    val replacement = disposition.string("replacement")
                    assertTrue(replacement, replacement in current)
                    when (disposition.string("replacementKind")) {
                        "MARKED" -> assertEquals(replacement, factSources(pack, owner).getValue(replacement).toSet(), cited.toSet())
                        "INSTRUCTION" -> { assertTrue(replacement, replacement in reviewedNonFacts); assertTrue(cited.isEmpty()) }
                        else -> fail("Unknown replacement kind: $disposition")
                    }
                }
                "REMOVE" -> { assertFalse("Removed sentence survives: $baseline", baseline in current); assertTrue(cited.isEmpty()) }
                else -> fail("Unknown disposition: $disposition")
            }
            cited.forEach { source ->
                val record = pack.provenanceRecords.single { it.itemId == source }
                assertEquals(8, record.unitNumber)
                assertTrue(source, V2Validator.isFrozenSourceRecord(pack, record))
            }
        }
    }

    @Test
    fun everyUnitEightMarkerCitesRecordsOfAFrozenFileUnderCatalogSources() {
        val pack = pack()
        val frozenFiles = File(repo(), "catalog/sources").walkTopDown().filter { it.isFile && it.extension in setOf("csv", "txt") }
            .map { it.readText() }.toList()
        var markers = 0
        owners(pack).forEach { (owner, facts, sources) ->
            assertEquals(owner, facts.toSet(), sources.keys)
            facts.forEach { fact ->
                markers++
                val cited = sources[fact].orEmpty()
                assertTrue("$owner: $fact", cited.isNotEmpty())
                cited.forEach { id ->
                    val record = pack.provenanceRecords.single { it.itemId == id }
                    assertEquals(id, 8, record.unitNumber)
                    assertTrue(id, V2Validator.isFrozenSourceRecord(pack, record))
                    assertFalse(id, record.sourceRecordId.startsWith("ca-coverage:"))
                    assertTrue("$id is not in a frozen file", frozenFiles.any { record.originalSourceText in it })
                    // An OpenRussian cell must be the named field of the named row in the frozen CSV.
                    Regex("^(nouns|adjectives|others)\\.csv:([^#]+)#(.+)$").find(record.sourceRecordId)?.let { match ->
                        val (file, bare, field) = match.destructured
                        val csv = File(repo(), "catalog/sources").walkTopDown().first { it.isFile && it.name == "$file.csv" &&
                            it.readLines().drop(1).any { line -> line.substringBefore('\t') == bare } }.readLines()
                        val header = csv.first().split('\t')
                        val row = csv.drop(1).first { it.substringBefore('\t') == bare }.split('\t')
                        assertEquals(id, record.originalSourceText, row[header.indexOf(field)].trim())
                    }
                }
                // B3: the cited frozen text contains every Cyrillic form the marker shows, with its stress.
                val cover = cited.flatMap { id -> V2Validator.factWordForms(pack.provenanceRecords.single { it.itemId == id }.originalSourceText) }.toSet()
                V2Validator.factWordForms(fact).forEach { form ->
                    assertTrue("$owner: $form in $fact", form in cover || ('́' !in form && form in cover.map { it.replace("́", "") }))
                }
            }
        }
        assertTrue(markers > 100)
    }

    @Test
    fun everyUnitEightMarkerWithoutCyrillicHasAReviewNamingAFrozenLineThatStatesIt() {
        val pack = pack()
        val reviews = Json.parseToJsonElement(File(repo(), "app/src/test/resources/unit8-fact-dispositions.json").readText())
            .jsonObject.getValue("markerReviews").jsonArray.map { it.jsonObject }
        fun JsonObject.string(key: String) = getValue(key).jsonPrimitive.content
        val owners = owners(pack)
        val needed = owners.flatMap { (owner, facts, _) -> facts.filter { fact -> fact.none { it in 'Ѐ'..'ӿ' } }.map { owner to it } }
        assertTrue(needed.size >= 20)
        assertEquals(needed.toSet(), reviews.map { it.string("ownerId") to it.string("sentence") }.toSet())
        assertEquals(needed.size, reviews.size)
        val records = pack.provenanceRecords.associateBy { it.itemId }
        reviews.forEach { review ->
            val owner = review.string("ownerId")
            val sentence = review.string("sentence")
            val cited = owners.single { it.first == owner }.third.getValue(sentence).map { records.getValue(it) }
            val evidence = review.getValue("evidence").jsonArray.map { it.jsonObject }
            assertTrue("$owner: $sentence", evidence.isNotEmpty())
            val lines = evidence.map { entry ->
                val path = entry.string("file")
                assertTrue(path, path.startsWith("catalog/sources/"))
                val all = File(repo(), path).readLines()
                val number = entry.string("line").toInt()
                assertTrue("$path:$number does not exist", number in 1..all.size)
                assertTrue("$path:$number does not contain the quote", entry.string("quote") in all[number - 1])
                path to all[number - 1]
            }
            assertTrue("$owner: no evidence line holds a cited record: $sentence", lines.any { (path, line) ->
                cited.any { record ->
                    val cell = Regex("^(\\w+\\.csv):([^#]+)#").find(record.sourceRecordId)
                    if (cell != null) path.endsWith("/" + cell.groupValues[1]) && line.substringBefore('\t') == cell.groupValues[2]
                    else record.originalSourceText.lines().any { it.isNotBlank() && it in line }
                }
            })
        }
    }

    @Test
    fun everyMultiWordRussianPhraseUnitEightShowsPassesTheTwoPartPhraseRule() {
        val json = Json.parseToJsonElement(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText()).jsonObject
        val shown = shownPhrases(json, 8)
        val listed = Json.parseToJsonElement(File(repo(), "app/src/test/resources/unit8-phrase-attestations.json").readText())
            .jsonObject.getValue("phrases").jsonArray.map { it.jsonObject }
        assertEquals(shown, listed.map { it.getValue("phrase").jsonPrimitive.content }.toSet())
        assertEquals(5, shown.size)
        listed.forEach { entry ->
            val phrase = entry.getValue("phrase").jsonPrimitive.content
            val path = entry.getValue("file").jsonPrimitive.content
            val number = entry.getValue("line").jsonPrimitive.content.toInt()
            assertTrue(path, path.startsWith("catalog/sources/"))
            val all = File(repo(), path).readLines()
            assertTrue("$path:$number", number in 1..all.size)
            assertTrue("$phrase is not attested at $path:$number", attests(all[number - 1], phrase))
        }
        // Regression: a phrase built from attested single forms is not attested, and once shown in Unit 8 it is
        // detected by the walk and fails the rule.
        val built = "мои́ дома́"
        assertTrue(V2Validator.factWordForms(built).all { it in frozenStressForms })
        assertTrue(frozenLines().none { attests(it, built) })
        val units = json.getValue("units").jsonArray.map { unit ->
            val obj = unit.jsonObject
            if (obj.getValue("number").jsonPrimitive.content != "8") unit
            else JsonObject(obj + ("title" to JsonPrimitive("${obj.getValue("title").jsonPrimitive.content} $built")))
        }
        assertTrue(built in shownPhrases(JsonObject(json + ("units" to JsonArray(units))), 8) - shown)
    }

    private val skippedKeys = setOf("id", "sourceId", "reviewStatus", "kind", "language", "objectiveId", "letterId", "installState",
        "readiness", "mappingStatus", "classification", "russianFactSources", "assessment", "containsGeneratedRussian", "penguinLesson",
        "mappingNote", "bookDirection", "advancement", "prerequisiteUnitNumbers", "searchTerms")
    private val cyr = "\\u0400-\\u04FF"
    private val phraseRegex = Regex("[$cyr][$cyr\\u0301]*(?:-[$cyr][$cyr\\u0301]*)?(?:[ \\u00a0]+[$cyr][$cyr\\u0301]*(?:-[$cyr][$cyr\\u0301]*)?)+")

    /** Runs of two or more space-separated Cyrillic words in the learner-visible text of a unit (as in Units 6 and 7). */
    private fun shownPhrases(pack: JsonObject, number: Int): Set<String> {
        val unit = pack.getValue("units").jsonArray.map { it.jsonObject }.single { it.getValue("number").jsonPrimitive.content == number.toString() }
        val objects = mutableListOf<JsonElement>(JsonObject(unit - "sessions"))
        val exampleIds = mutableSetOf<String>()
        val referenceIds = mutableSetOf<String>()
        fun ids(element: JsonObject, key: String) = (element[key] as? JsonArray).orEmpty().map { it.jsonPrimitive.content }
        unit.getValue("sessions").jsonArray.map { it.jsonObject }.forEach { session ->
            objects += JsonObject(session - "steps")
            session.getValue("steps").jsonArray.map { it.jsonObject }.forEach { step ->
                objects += step; exampleIds += ids(step, "exampleIds"); referenceIds += ids(step, "referenceIds")
            }
        }
        pack.getValue("items").jsonArray.map { it.jsonObject }.filter { it.getValue("objectiveId").jsonPrimitive.content.startsWith("obj-u$number-") }
            .forEach { objects += it; exampleIds += ids(it, "exampleIds") }
        objects += pack.getValue("examples").jsonArray.filter { it.jsonObject.getValue("id").jsonPrimitive.content in exampleIds }
        objects += pack.getValue("references").jsonArray.filter { it.jsonObject.getValue("id").jsonPrimitive.content in referenceIds }
        val strings = mutableListOf<String>()
        fun walk(element: JsonElement) {
            when (element) {
                is JsonObject -> element.forEach { (key, value) -> if (key !in skippedKeys && !key.endsWith("Id") && !key.endsWith("Ids")) walk(value) }
                is JsonArray -> element.forEach(::walk)
                is JsonPrimitive -> if (element.isString) strings += element.content
            }
        }
        objects.forEach(::walk)
        return strings.flatMap { text -> phraseRegex.findAll(text).map { it.value } }.toSet()
    }

    /** The T15a-n3 two-part rule, compared exactly as Unit6FactProvenanceTest.attests compares it. */
    private fun attests(line: String, phrase: String, stressForms: Set<String> = frozenStressForms): Boolean {
        val present = Regex("(?<![$cyr])${Regex.escape(unstressed(phrase))}(?![$cyr])").containsMatchIn(unstressed(line))
        return present && V2Validator.factWordForms(phrase).filter { '́' in it }.all { it in stressForms }
    }

    /** T15f: the shared rule; a Wiktionary piped link reads as its display text. */
    private fun unstressed(text: String): String = TwoPartPhraseRule.unstressed(text)

    private val frozenStressForms: Set<String> by lazy { frozenLines().flatMap { V2Validator.factWordForms(it) }.toSet() }

    private fun frozenLines(): List<String> = File(repo(), "catalog/sources").walkTopDown()
        .filter { it.isFile && it.extension in setOf("txt", "csv", "html") }.flatMap { it.readLines() }.toList()

    private fun owners(pack: V2CurriculumPack): List<Triple<String, List<String>, Map<String, List<String>>>> =
        pack.items.filter { it.objectiveId.startsWith("obj-u8-") }.map { Triple(it.id, it.russianFacts, it.russianFactSources) } +
            pack.units.single { it.number == 8 }.sessions.flatMap { it.steps }.map { Triple(it.id, it.russianFacts, it.russianFactSources) }

    private fun factSources(pack: V2CurriculumPack, owner: String): Map<String, List<String>> =
        pack.items.firstOrNull { it.id == owner }?.russianFactSources
            ?: pack.units.flatMap { it.sessions }.flatMap { it.steps }.single { it.id == owner }.russianFactSources

    private fun hosts(pack: V2CurriculumPack) = V2Validator.englishProvenanceHosts(pack, includeAllUnits = true)
        .filter { it.unitNumber == 8 && it.field in fields }
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
                char == '\n' && !quoted -> { row.add(cell.toString()); cell.clear(); rows.add(row); row = mutableListOf() }
                char == '\r' && !quoted -> Unit
                else -> cell.append(char)
            }
            index++
        }
        assertFalse("Malformed baseline CSV", quoted)
        if (cell.isNotEmpty() || row.isNotEmpty()) { row.add(cell.toString()); rows.add(row) }
        assertEquals(listOf("unit", "owner_id", "field", "sentence", "protection"), rows.first())
        return rows.drop(1)
    }

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())
    private fun repo() = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
