package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.BatchARemainingSources
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2Validator
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import kotlinx.serialization.json.*

/** T17d (owner direction 2026-11-23): every Unit 19 fact is marked with frozen-source citations, including the two S1 claims on
 * the conditional (line 2225, SUPPORTED by ru Сослагательное наклонение 140019162; line 2234, PARTIAL, reworded to Wiktionary бы
 * 94001807); whole instructions that state no Russian fact are reviewed explicitly; every shown phrase passes the two-part phrase
 * rule. Modelled on Unit17FactProvenanceTest. This is a content review check, not a word classifier or a change to the
 * field-based validator.
 */
class Unit19FactProvenanceTest {
    private val fields = setOf("item feedback", "item distractor rationale", "remediation body")
    private val reviewedNonFacts = setOf(
        "Choose I have to.",
        "Choose If the subject is feminine.",
        "Choose If the subject is plural.",
        "Choose The eclipse should occur at three o'clock.",
        "Choose a female speaker.",
        "Choose a male speaker.",
        "Choose the speaker is marked feminine.",
        "Compare your answer with the worked examples for this form, then try again.",
        "Distinguish subject agreement from the meaning of the whole phrase.",
        "Distinguish the eclipse sentence’s meaning from the employee’s obligation.",
        "Learn the prefixed verbs shown here; practise creating other prefixed verbs and using aspect in sentences separately.",
        "Learn these conditional examples, then practise forming other conditional sentences and choosing aspect separately.",
        "Learn these examples, then practise other uses and complete sentences separately.",
        "Learn these obligation forms, then practise complete obligation statements and other tense forms separately.",
        "Learn these two verbs as an aspect pair.",
        "Practise writing each of the four conditional forms for a male and a female speaker.",
        "Practise writing each verb above from its English meaning and its aspect partner.",
        "Practise writing the form for each kind of subject.",
        "Review the agreement examples before retrying.",
        "Select the answer for a shown form, or type the Russian asked for, using the explanations above.",
        "Stress marks are optional when you type.",
        "Translate the verb when identifying its meaning.",
    )

    @Test
    fun unitNineteenFeedbackAndRemediationHaveNoUnmarkedFactSentences() {
        val defects = unmarkedSentences(pack())
        assertTrue(defects.joinToString("\n"), defects.isEmpty())
    }

    @Test
    fun addingAnUnmarkedFactSentenceToUnitNineteenFails() {
        // Regression: a fact sentence added without a marker is reported. The fixture reuses the removed Unit 19 baseline
        // sentence (inventory line 2229); it invents no Russian text or fact.
        val pack = pack()
        val fact = "Speaker agreement and sentence meaning are different questions."
        assertTrue(unmarkedSentences(pack).isEmpty())
        val item = pack.items.single { it.id == "u19-i-conditional-1" }
        val withFeedback = pack.copy(items = pack.items.map { if (it.id == item.id) it.copy(feedback = it.feedback + " " + fact) else it })
        assertTrue(unmarkedSentences(withFeedback).any { item.id in it && fact in it })
        val remediation = "u19-st-rem-conditional"
        val withRemediation = pack.copy(units = pack.units.map { unit ->
            unit.copy(sessions = unit.sessions.map { session ->
                session.copy(steps = session.steps.map { if (it.id == remediation) it.copy(body = it.body + "\n\n" + fact) else it })
            })
        })
        assertTrue(unmarkedSentences(withRemediation).any { remediation in it && fact in it })
        // Removing an existing marker is reported too.
        val marked = pack.items.first { it.objectiveId.startsWith("obj-u19-") && it.russianFacts.isNotEmpty() }
        val unmarked = pack.copy(items = pack.items.map { if (it.id == marked.id) it.copy(russianFacts = it.russianFacts.drop(1)) else it })
        assertTrue(unmarkedSentences(unmarked).any { marked.id in it && marked.russianFacts.first() in it })
    }

    @Test
    fun everyEnglishOnlyBaselineFactHasAVerifiedCurrentDisposition() {
        val pack = pack()
        val entries = inventory().filter { it[0] == "19" && it[4].startsWith("English-only") }
        assertEquals("Do not hide entries by deleting baseline inventory rows", 67, entries.size)
        assertEquals(64, entries.map { it[3] }.toSet().size) // report section 3: 67 entries, 64 distinct sentences
        val json = Json.parseToJsonElement(File(repo(), "app/src/test/resources/unit19-fact-dispositions.json").readText()).jsonObject
        val dispositions = json.getValue("entries").jsonArray.map { it.jsonObject }
        fun JsonObject.string(key: String) = getValue(key).jsonPrimitive.content
        assertEquals(67, dispositions.size)
        assertEquals(entries.map { listOf(it[1], it[2], it[3]) }.toSet(), dispositions.map {
            listOf(it.string("ownerId"), it.string("field"), it.string("baselineSentence"))
        }.toSet())
        val totals = dispositions.groupingBy { it.string("disposition") }.eachCount()
        // Report section 3 (58 MARK, 2 REWORD, 5 REMOVE, 2 PENDING-INTAKE) with S1 2225 SUPPORTED (marked) and 2234 PARTIAL
        // (reworded to the frozen text and marked); the rebuilt obligation and probability option sets reword 2260, 2263, 2276
        // and 2284 (marked) and remove 2259 and 2262 (departures from the prep's MARK, named in each entry's reason).
        assertEquals(mapOf("MARK" to 53, "REWORD" to 7, "REMOVE" to 7), totals)
        assertEquals(totals, json.getValue("totals").jsonObject.mapValues { it.value.jsonPrimitive.int })
        val hosts = hosts(pack)
        // The S1 verdicts (audit/prep/s1-claim-verification.csv, Unit 19 rows): SUPPORTED -> marked; PARTIAL -> reworded to the
        // frozen text, then marked; NOT_SUPPORTED -> removed. Supported and reworded claims cite an S1-frozen page.
        val s1Sources = setOf("src-ruwiki-soslagatelnoe-naklonenie-140019162", "src-enwiktionary-by-94001807")
        val verdicts = dispositions.filter { it.containsKey("s1Verdict") }
        assertEquals(mapOf("SUPPORTED" to 1, "PARTIAL" to 1), verdicts.groupingBy { it.string("s1Verdict") }.eachCount()) // the Unit 19 rows of the S1 claim verification (2225, 2234)
        verdicts.forEach { d ->
            val line = d.string("csvLine")
            when (d.string("s1Verdict")) {
                "SUPPORTED" -> assertEquals(line, "MARK", d.string("disposition"))
                "PARTIAL" -> { assertEquals(line, "REWORD", d.string("disposition")); assertEquals(line, "MARKED", d.string("replacementKind")) }
                else -> assertEquals(line, "REMOVE", d.string("disposition"))
            }
            if (d.string("s1Verdict") != "NOT_SUPPORTED") assertTrue(line, d.getValue("sourceRecordIds").jsonArray.any { id ->
                pack.provenanceRecords.single { it.itemId == id.jsonPrimitive.content }.sourceId in s1Sources
            })
        }
        // 2225 cites Сослагательное наклонение line 18; 2234 cites бы line 58 (the S1 verification lines).
        mapOf("2225" to "src-ruwiki-soslagatelnoe-naklonenie-140019162", "2234" to "src-enwiktionary-by-94001807").forEach { (line, source) ->
            val cited = dispositions.single { it.string("csvLine") == line }.getValue("sourceRecordIds").jsonArray
                .map { id -> pack.provenanceRecords.single { it.itemId == id.jsonPrimitive.content } }
            assertEquals(line, 1, cited.count { it.sourceId == source })
        }
        dispositions.forEach { disposition ->
            val owner = disposition.string("ownerId")
            val matching = hosts.filter { it.ownerId == owner && it.field == disposition.string("field") }
            assertTrue(owner, matching.isNotEmpty())
            val baseline = disposition.string("baselineSentence")
            val current = matching.flatMap { host -> V2Validator.englishSentenceRanges(host.text).map { host.text.substring(it.first, it.last + 1) } }
            val cited = disposition.getValue("sourceRecordIds").jsonArray.map { it.jsonPrimitive.content }
            when (disposition.string("disposition")) {
                "MARK" -> {
                    // A marked sentence is kept; where the report also drops governance narration or the owner-directed
                    // prompt wording changes, currentSentence is the marked learner wording.
                    val marked = disposition["currentSentence"]?.jsonPrimitive?.content ?: baseline
                    if (marked != baseline) assertFalse("Replaced sentence survives: $baseline", baseline in current)
                    assertTrue(marked, marked in current)
                    assertTrue(marked, matching.any { marked in it.russianFacts })
                    assertEquals(marked, factSources(pack, owner).getValue(marked).toSet(), cited.toSet())
                    assertTrue(marked, cited.isNotEmpty())
                }
                "REWORD" -> {
                    assertFalse("Reworded sentence survives: $baseline", baseline in current)
                    val replacement = disposition.string("replacement")
                    assertTrue(replacement, replacement in current)
                    when (disposition.string("replacementKind")) {
                        "MARKED" -> {
                            assertTrue(replacement, matching.any { replacement in it.russianFacts })
                            assertEquals(replacement, factSources(pack, owner).getValue(replacement).toSet(), cited.toSet())
                            assertTrue(replacement, cited.isNotEmpty())
                        }
                        "INSTRUCTION" -> { assertTrue(replacement, replacement in reviewedNonFacts); assertTrue(cited.isEmpty()) }
                        else -> fail("Unknown replacement kind: $disposition")
                    }
                }
                "REMOVE" -> { assertFalse("Removed sentence survives: $baseline", baseline in current); assertTrue(cited.isEmpty()) }
                else -> fail("Unknown disposition: $disposition")
            }
            cited.forEach { source ->
                val record = pack.provenanceRecords.single { it.itemId == source }
                assertEquals(19, record.unitNumber)
                assertTrue(source, V2Validator.isFrozenSourceRecord(pack, record))
            }
        }
    }

    @Test
    fun everyUnitNineteenMarkerCitesRecordsOfAFrozenFileUnderCatalogSources() {
        val pack = pack()
        // As in Unit 17, a record's original text is in the raw file or in the visible text of a frozen HTML page.
        val frozenFiles = File(repo(), "catalog/sources").walkTopDown().filter { it.isFile && it.extension in setOf("csv", "txt", "html") }
            .flatMap { file -> if (file.extension == "html") listOf(file.readText(), BatchARemainingSources.htmlVisible(file.readText())) else listOf(file.readText()) }
            .toList()
        var markers = 0
        owners(pack).forEach { (owner, facts, sources) ->
            assertEquals(owner, facts.toSet(), sources.keys)
            facts.forEach { fact ->
                markers++
                val cited = sources[fact].orEmpty()
                assertTrue("$owner: $fact", cited.isNotEmpty())
                cited.forEach { id ->
                    val record = pack.provenanceRecords.single { it.itemId == id }
                    assertEquals(id, 19, record.unitNumber)
                    assertTrue(id, V2Validator.isFrozenSourceRecord(pack, record))
                    assertFalse(id, record.sourceRecordId.startsWith("ca-coverage:"))
                    // A ReadyRussian text extraction joins visible pieces with blank lines; each piece is visible page text.
                    val pieces = record.originalSourceText.split("\n\n").filter { it.isNotBlank() }
                    assertTrue("$id is not in a frozen file", frozenFiles.any { record.originalSourceText in it } ||
                        (record.transformation == "readyrussian text extraction" && pieces.all { piece -> frozenFiles.any { piece in it } }))
                    // An OpenRussian cell must be the named field of the named row in the frozen CSV.
                    Regex("^(verbs|nouns|adjectives)\\.csv:([^#]+)#(.+)$").find(record.sourceRecordId)?.let { match ->
                        val (file, bare, field) = match.destructured
                        // verbs.csv records name the accented headword (second column).
                        val csv = File(repo(), "catalog/sources/${if (file == "adjectives") "units-6-10" else "units-3-5"}/openrussian/$file.csv").readLines()
                        val header = csv.first().split('\t')
                        val row = csv.drop(1).single { it.split('\t')[1] == bare }.split('\t')
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
        assertTrue(markers >= 93) // the 93 Unit 19 markers at T17d
    }

    @Test
    fun everyUnitNineteenMarkerWithoutCyrillicHasAReviewNamingAFrozenLineThatStatesIt() {
        val pack = pack()
        val reviews = Json.parseToJsonElement(File(repo(), "app/src/test/resources/unit19-fact-dispositions.json").readText())
            .jsonObject.getValue("markerReviews").jsonArray.map { it.jsonObject }
        fun JsonObject.string(key: String) = getValue(key).jsonPrimitive.content
        val owners = owners(pack)
        val needed = owners.flatMap { (owner, facts, _) -> facts.filter { fact -> fact.none { it in 'Ѐ'..'ӿ' } }.map { owner to it } }
        assertEquals(66, needed.size)
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
                    if (cell != null) path.endsWith("/" + cell.groupValues[1]) && line.split('\t').getOrNull(1) == cell.groupValues[2]
                    else record.originalSourceText.lines().any { it.isNotBlank() && it in line }
                }
            })
        }
    }

    @Test
    fun everyMultiWordRussianPhraseUnitNineteenShowsPassesTheTwoPartPhraseRule() {
        val json = Json.parseToJsonElement(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText()).jsonObject
        val shown = shownPhrases(json, 19)
        val listed = Json.parseToJsonElement(File(repo(), "app/src/test/resources/unit19-phrase-attestations.json").readText())
            .jsonObject.getValue("phrases").jsonArray.map { it.jsonObject }
        assertEquals(shown, listed.map { it.getValue("phrase").jsonPrimitive.content }.toSet())
        assertEquals(8, shown.size) // report section 6 (7 phrases; the two piped-link sentences now pass) plus the W:35 indebtedness example
        listed.forEach { entry ->
            val phrase = entry.getValue("phrase").jsonPrimitive.content
            val path = entry.getValue("file").jsonPrimitive.content
            val number = entry.getValue("line").jsonPrimitive.content.toInt()
            assertTrue(path, path.startsWith("catalog/sources/"))
            val all = File(repo(), path).readLines()
            assertTrue("$path:$number", number in 1..all.size)
            assertTrue("$phrase is not attested at $path:$number", attests(all[number - 1], phrase))
        }
        // Regression: a phrase built from attested single forms is not attested, and once shown in Unit 19 it is
        // detected by the walk and fails the rule.
        val built = "Мы бу́дем сказа́ть"
        assertTrue(V2Validator.factWordForms(built).all { it in frozenStressForms })
        assertTrue(frozenLines().none { attests(it, built) })
        val units = json.getValue("units").jsonArray.map { unit ->
            val obj = unit.jsonObject
            if (obj.getValue("number").jsonPrimitive.content != "19") unit
            else JsonObject(obj + ("title" to JsonPrimitive("${obj.getValue("title").jsonPrimitive.content} $built")))
        }
        assertTrue(built in shownPhrases(JsonObject(json + ("units" to JsonArray(units))), 19) - shown)
    }

    private val skippedKeys = setOf("id", "sourceId", "reviewStatus", "kind", "language", "objectiveId", "letterId", "installState",
        "readiness", "mappingStatus", "classification", "russianFactSources", "assessment", "containsGeneratedRussian", "penguinLesson",
        "mappingNote", "bookDirection", "advancement", "prerequisiteUnitNumbers", "searchTerms")
    private val cyr = "\\u0400-\\u04FF"
    private val phraseRegex = Regex("[$cyr][$cyr\\u0301]*(?:-[$cyr][$cyr\\u0301]*)?(?:[ \\u00a0]+[$cyr][$cyr\\u0301]*(?:-[$cyr][$cyr\\u0301]*)?)+")

    /** Runs of two or more space-separated Cyrillic words in the learner-visible text of a unit (as in Units 6 to 9). */
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
        pack.items.filter { it.objectiveId.startsWith("obj-u19-") }.map { Triple(it.id, it.russianFacts, it.russianFactSources) } +
            pack.units.single { it.number == 19 }.sessions.flatMap { it.steps }.map { Triple(it.id, it.russianFacts, it.russianFactSources) }

    private fun factSources(pack: V2CurriculumPack, owner: String): Map<String, List<String>> =
        pack.items.firstOrNull { it.id == owner }?.russianFactSources
            ?: pack.units.flatMap { it.sessions }.flatMap { it.steps }.single { it.id == owner }.russianFactSources

    private fun hosts(pack: V2CurriculumPack) = V2Validator.englishProvenanceHosts(pack, includeAllUnits = true)
        .filter { it.unitNumber == 19 && it.field in fields }
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
