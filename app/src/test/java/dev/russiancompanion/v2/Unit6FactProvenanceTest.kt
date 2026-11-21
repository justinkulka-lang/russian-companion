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
        // T15a-n1: unsourced animacy claims reworded to instruction or scope text, identically in teaching and remediation.
        "Treat the English notes as dictionary senses, not full sentence translations.",
        "These steps ask you to recognize the supplied forms only, not to produce endings for other nouns.",
        "Use the requested case or the supplied context to decide a word’s sentence role.",
        "Answer with the case the task names.",
        "This check does not establish completed action; no aspect or full-translation claim is tested.",
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
            // T15a-n1: a MARKED entry cites exactly the frozen-source records its marker names (owner decision 11/14/2026).
            val cited = disposition.getValue("sourceRecordIds").jsonArray.map { it.jsonPrimitive.content }
            if (disposition.string("disposition") == "MARKED") {
                assertEquals(baseline, factSources(pack, disposition.string("ownerId")).getValue(baseline).toSet(), cited.toSet())
            } else assertTrue(cited.isEmpty())
            cited.forEach { source ->
                val record = pack.provenanceRecords.single { it.itemId == source }
                assertEquals(6, record.unitNumber)
                assertTrue(source, V2Validator.isFrozenSourceRecord(pack, record))
            }
        }
    }

    @Test
    fun everyUnitSixMarkerCitesRecordsOfAFrozenFileUnderCatalogSources() {
        // T15a-n1 (owner decision 11/14/2026): each cited record's original text occurs in a frozen file of a licensed source.
        val pack = pack()
        val frozenFiles = File(repo(), "catalog/sources").walkTopDown().filter { it.isFile && it.extension in setOf("csv", "txt") }
            .map { it.readText() }.toList()
        val owners = pack.items.filter { it.objectiveId.startsWith("obj-u6-") }.map { it.id to (it.russianFacts to it.russianFactSources) } +
            pack.units.single { it.number == 6 }.sessions.flatMap { it.steps }.map { it.id to (it.russianFacts to it.russianFactSources) }
        var markers = 0
        owners.forEach { (owner, marks) ->
            val (facts, sources) = marks
            facts.forEach { fact ->
                markers++
                val cited = sources[fact].orEmpty()
                assertTrue("$owner: $fact", cited.isNotEmpty())
                cited.forEach { id ->
                    val record = pack.provenanceRecords.single { it.itemId == id }
                    assertTrue(id, V2Validator.isFrozenSourceRecord(pack, record))
                    assertTrue("$id is not in a frozen file", frozenFiles.any { record.originalSourceText in it })
                    // An OpenRussian cell must be the named field of the named row in the frozen CSV.
                    Regex("^(nouns|adjectives)\\.csv:([^#]+)#(.+)$").find(record.sourceRecordId)?.let { match ->
                        val (file, bare, field) = match.destructured
                        val csv = File(repo(), "catalog/sources").walkTopDown().first { it.isFile && it.name == "$file.csv" &&
                            it.readLines().drop(1).any { line -> line.substringBefore('\t') == bare } }.readLines()
                        val header = csv.first().split('\t')
                        val row = csv.drop(1).first { it.substringBefore('\t') == bare }.split('\t')
                        assertEquals(id, record.originalSourceText, row[header.indexOf(field)].trim())
                    }
                }
            }
        }
        assertTrue(markers > 100)
    }

    @Test
    fun everyUnitSixMarkerWithoutCyrillicHasAReviewNamingAFrozenLineThatStatesIt() {
        // T15a-n2 (owner direction 11/15/2026): a marked sentence with no Cyrillic word cannot be checked mechanically,
        // so each has a review entry naming the frozen file, line and exact quote; one evidence line holds a cited record.
        val pack = pack()
        val reviews = Json.parseToJsonElement(File(repo(), "app/src/test/resources/unit6-fact-dispositions.json").readText())
            .jsonObject.getValue("markerReviews").jsonArray.map { it.jsonObject }
        fun JsonObject.string(key: String) = getValue(key).jsonPrimitive.content
        val owners = pack.items.filter { it.objectiveId.startsWith("obj-u6-") }.map { Triple(it.id, it.russianFacts, it.russianFactSources) } +
            pack.units.single { it.number == 6 }.sessions.flatMap { it.steps }.map { Triple(it.id, it.russianFacts, it.russianFactSources) }
        val needed = owners.flatMap { (owner, facts, _) -> facts.filter { fact -> fact.none { it in 'Ѐ'..'ӿ' } }.map { owner to it } }
        assertTrue(needed.size >= 50)
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
                val file = File(repo(), path)
                assertTrue(path, file.isFile)
                val number = entry.string("line").toInt()
                val all = file.readLines()
                assertTrue("$path:$number does not exist", number in 1..all.size)
                val line = all[number - 1]
                assertTrue("$path:$number does not contain the quote", entry.string("quote") in line)
                path to line
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
    fun everyMultiWordRussianPhraseUnitSixShowsIsAttestedVerbatimInAFrozenFile() {
        // T15a-n2 (owner decision 11/15/2026): attested single forms do not attest a phrase built from them.
        val json = Json.parseToJsonElement(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText()).jsonObject
        val shown = shownPhrases(json, 6)
        val listed = Json.parseToJsonElement(File(repo(), "app/src/test/resources/unit6-phrase-attestations.json").readText())
            .jsonObject.getValue("phrases").jsonArray.map { it.jsonObject }
        assertEquals(shown, listed.map { it.getValue("phrase").jsonPrimitive.content }.toSet())
        assertTrue(shown.size >= 10)
        listed.forEach { entry ->
            val phrase = entry.getValue("phrase").jsonPrimitive.content
            val path = entry.getValue("file").jsonPrimitive.content
            val number = entry.getValue("line").jsonPrimitive.content.toInt()
            assertTrue(path, path.startsWith("catalog/sources/"))
            val all = File(repo(), path).readLines()
            assertTrue("$path:$number", number in 1..all.size)
            assertTrue("$phrase is not attested at $path:$number", attests(all[number - 1], phrase))
        }
        // A worked phrase assembled from attested forms is not attested by them.
        val built = "на́шу сестру́"
        assertTrue(frozenLines().none { attests(it, built) })
        val units = json.getValue("units").jsonArray.map { unit ->
            val obj = unit.jsonObject
            if (obj.getValue("number").jsonPrimitive.content != "6") unit else JsonObject(obj + ("title" to JsonPrimitive("${obj.getValue("title").jsonPrimitive.content} $built")))
        }
        assertTrue(built in shownPhrases(JsonObject(json + ("units" to JsonArray(units))), 6) - shown)
    }

    private val skippedKeys = setOf("id", "sourceId", "reviewStatus", "kind", "language", "objectiveId", "letterId", "installState",
        "readiness", "mappingStatus", "classification", "russianFactSources", "assessment", "containsGeneratedRussian", "penguinLesson",
        "mappingNote", "bookDirection", "advancement", "prerequisiteUnitNumbers", "searchTerms")
    private val cyr = "\\u0400-\\u04FF"
    private val phraseRegex = Regex("[$cyr][$cyr\\u0301]*(?:-[$cyr][$cyr\\u0301]*)?(?:[ \\u00a0]+[$cyr][$cyr\\u0301]*(?:-[$cyr][$cyr\\u0301]*)?)+")

    /** Runs of two or more space-separated Cyrillic words in the learner-visible text of a unit. */
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

    @Test
    fun phraseAttestationNeedsPhrasePresenceAndFrozenStressEvidence() {
        // T15a-n3 owner decision: presence is compared without stress marks; each stressed word needs its exact stress in
        // some frozen row or line. gender-4310736.txt:12 has мой стол unstressed; the OpenRussian мой row
        // (units-6-10/openrussian/adjectives.csv:8, мо'й) is frozen stress evidence for мо́й.
        val gender = File(repo(), "catalog/sources/units-3-5/wikibooks/gender-4310736.txt").readLines()[11]
        assertTrue("мой стол" in gender)
        val row = File(repo(), "catalog/sources/units-6-10/openrussian/adjectives.csv").readLines()[7]
        assertTrue("мо́й" in V2Validator.factWordForms(row))
        assertTrue(attests(gender, "мо́й стол", frozenStressForms))
        // With no frozen stress evidence for мо́й, the same phrase fails.
        assertFalse(attests(gender, "мо́й стол", frozenStressForms - "мо́й"))
        assertTrue(attests(gender, "мой стол"))
        // ё and е stay distinct: vechera-5336197.txt:723 has мое письмо, which does not attest моё письмо.
        val vechera = File(repo(), "catalog/sources/units-26-30/wikisource/vechera-5336197.txt").readLines()[722]
        assertTrue("мое письмо" in vechera)
        assertTrue(attests(vechera, "мое письмо"))
        assertFalse(attests(vechera, "моё письмо"))
        assertTrue(frozenLines().none { attests(it, "моё письмо") })
        // A phrase absent from every frozen file fails even when each of its words is attested.
        assertTrue(frozenLines().none { attests(it, "на́шу сестру́") })
    }

    /**
     * T15a-n3 owner decision (11/15/2026). A shown phrase is attested by a frozen line only if both hold:
     * (a) presence: after stripping wiki and HTML markup, collapsing whitespace, ignoring case and removing stress marks
     * (U+0301 and OpenRussian apostrophes), the line contains the phrase as whole words; ё and е stay distinct;
     * (b) stress: every stressed word of the phrase has that exact stress in some frozen row or line, compared as the B3
     * marker check compares forms (V2Validator.factWordForms).
     */
    private fun attests(line: String, phrase: String, stressForms: Set<String> = frozenStressForms): Boolean {
        val present = Regex("(?<![$cyr])${Regex.escape(unstressed(phrase))}(?![$cyr])").containsMatchIn(unstressed(line))
        return present && V2Validator.factWordForms(phrase).filter { '\u0301' in it }.all { it in stressForms }
    }

    /** T15f: the shared rule; a Wiktionary piped link reads as its display text. */
    private fun unstressed(text: String): String = TwoPartPhraseRule.unstressed(text)

    private val frozenStressForms: Set<String> by lazy { frozenLines().flatMap { V2Validator.factWordForms(it) }.toSet() }

    private fun frozenLines(): List<String> = File(repo(), "catalog/sources").walkTopDown()
        .filter { it.isFile && it.extension in setOf("txt", "csv", "html") }.flatMap { it.readLines() }.toList()

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

    private fun factSources(pack: V2CurriculumPack, owner: String): Map<String, List<String>> =
        pack.items.firstOrNull { it.id == owner }?.russianFactSources
            ?: pack.units.flatMap { it.sessions }.flatMap { it.steps }.single { it.id == owner }.russianFactSources

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
