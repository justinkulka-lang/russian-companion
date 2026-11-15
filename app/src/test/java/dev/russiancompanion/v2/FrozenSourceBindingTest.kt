package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.BatchARemainingSources
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2ProvenanceRecord
import dev.russiancompanion.v2.curriculum.V2Validator
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.security.MessageDigest

/**
 * T15a-n2 (owner direction 11/15/2026): build-time binding of provenance records to the frozen files under
 * catalog/sources/. The app does not ship catalog/sources, so this is a JVM test, not a validator rule.
 *
 * A record that names a licensed (not original-app-text) source must resolve, through the FREEZE.json manifests, to a
 * frozen file that the manifest binds to that same source ID at the named revision (the oldid, the retained HTML sha256,
 * or the OpenRussian commit and file); T15a-n3: a revision alone does not select a file for a different source. That
 * file's bytes must match the manifest hash, its originalSourceText must occur verbatim in the file, and the record's
 * sourceHash must equal the hash recomputed from the span found in the file. "readyrussian text extraction" records
 * hold the visible text of the frozen HTML (BatchARemainingSources.htmlVisible), so they are found in that text.
 */
class FrozenSourceBindingTest {
    private data class FrozenFile(val file: File, val sha256: String)

    /**
     * T15a-n3: the exact records whose source has no frozen file under catalog/sources/ (reported to the owner; Units 21
     * and 29), each pinned to its source and to the SHA-256 of its originalSourceText. Any other unbound record fails,
     * including a new record under one of these source IDs, and so does a listed record whose text changes.
     */
    private val unfrozenRecords: Map<String, Pair<String, String>> = run {
        val reflexive = "src-enwiki-reflexive-pronoun-1374712982" to "0111295b473dbe0dd7f6353d48e7bbeccf180e48aa7a93b3cf3827dd62affa50"
        val obosoblenieAfter = "src-ruwiki-obosoblenie-144053197" to "a2c8e66f9f55fb67f145c16941ebae8a44dbff5efae1da4c5a76b1889697455d"
        val obosoblenieCause = "src-ruwiki-obosoblenie-144053197" to "60ab6e0afc66709184089243bbeb6d173d336a6e95c01d2baea8ea8c3ccaacdd"
        val boltraffio = "src-ruwiki-boltraffio-149826784" to "04798a919d45e77e4162a186ab72313dab529da5b5ac9d664748d1ca5a482acf"
        val obosoblenieTerminal = "src-ruwiki-obosoblenie-144053197" to "875206a0ab7268a29758dcc2b9c6feff6e4a91bfb6b63ce0c7197cf2250a262e"
        listOf("other", "personal", "reflexive", "self").associate { "prov-u21-object-transfer-native-$it" to reflexive } +
            listOf("head", "region-1", "region-2", "ru").associate { "prov-u29-punctuation-native-after-$it" to obosoblenieAfter } +
            listOf("head", "region-0", "region-1", "region-2", "ru").associate { "prov-u29-punctuation-native-cause-$it" to obosoblenieCause } +
            listOf("boundary-bergognone", "head", "list-foppa", "region-0", "region-1", "region-2", "ru")
                .associate { "prov-u29-punctuation-native-pronoun-$it" to boltraffio } +
            listOf("head", "region-0", "region-1", "ru").associate { "prov-u29-punctuation-native-terminal-$it" to obosoblenieTerminal }
    }

    @Test
    fun everyRecordNamingAFrozenSourceIsBoundToTheNamedFrozenFile() {
        val pack = pack()
        assertEquals(24, unfrozenRecords.size)
        val errors = violations(pack, pack.provenanceRecords)
        assertTrue(errors.joinToString("\n"), errors.isEmpty())
        // Every allowlisted record is present, unbound, unchanged, and cited by no russian-fact marker.
        val listed = pack.provenanceRecords.filter { it.itemId in unfrozenRecords }
        assertEquals(unfrozenRecords.keys, listed.map { it.itemId }.toSet())
        assertTrue(bindingErrors(pack, listed).all { it.contains("names no frozen file") })
        assertEquals(24, bindingErrors(pack, listed).size)
        val cited = (pack.items.flatMap { it.russianFactSources.values.flatten() } +
            pack.units.flatMap { it.sessions }.flatMap { it.steps }.flatMap { it.russianFactSources.values.flatten() }).toSet()
        assertTrue(listed.none { it.itemId in cited })
        assertTrue(pack.provenanceRecords.count { !isOriginal(pack, it) } > 2000)
    }

    @Test
    fun aGenuineRecordRelabelledToAnotherFrozenSourceFails() {
        val pack = pack()
        val record = pack.provenanceRecords.first { it.sourceId == "src-wikibooks-russian-cc-by-sa" && it.sourceRevision == "4330404" }
        assertTrue(violations(pack, listOf(record)).isEmpty())
        // src-ruwiki-narechie-152437423 is itself frozen, but not at 4330404: the revision alone must not bind it to the
        // Wikibooks accusative page.
        assertTrue(pack.sources.any { it.id == "src-ruwiki-narechie-152437423" })
        val relabelled = record.copy(sourceId = "src-ruwiki-narechie-152437423")
        assertTrue(violations(pack, listOf(relabelled)).any { it.contains("names no frozen file") })
    }

    @Test
    fun aTwentyFifthRecordUnderAnAllowedUnfrozenSourceFails() {
        val pack = pack()
        val listed = pack.provenanceRecords.single { it.itemId == "prov-u29-punctuation-native-pronoun-head" }
        assertTrue(violations(pack, listOf(listed)).isEmpty())
        val extra = listed.copy(itemId = "prov-u29-punctuation-native-pronoun-extra")
        assertTrue(violations(pack, listOf(extra)).any { it.contains("names no frozen file") })
        // A listed record whose text changes no longer matches its pinned hash.
        val changed = listed.copy(originalSourceText = listed.originalSourceText + " ")
        assertTrue(violations(pack, listOf(changed)).any { it.contains("names no frozen file") })
    }

    @Test
    fun aWikibooksRecordWhoseTextIsInNoFrozenFileFails() {
        val pack = pack()
        val record = pack.provenanceRecords.first { it.sourceId == "src-wikibooks-russian-cc-by-sa" && it.sourceRevision == "4330404" }
        assertTrue(bindingErrors(pack, listOf(record)).isEmpty())
        val absent = "Russian nouns in this sentence were never written on the frozen page."
        assertTrue(frozenTexts().none { absent in it })
        val changed = record.copy(originalSourceText = absent)
        assertTrue(bindingErrors(pack, listOf(changed)).any { it.contains("not in the frozen file") })
    }

    @Test
    fun aSelfConsistentButFabricatedTextAndHashPairFails() {
        val pack = pack()
        val record = pack.provenanceRecords.first { it.sourceId == "src-wikibooks-russian-cc-by-sa" && it.sourceRevision == "4330404" }
        val fabricated = "| Our || Наш || Нашего || нашу || Наше || Наши || Нашими"
        val forged = record.copy(originalSourceText = fabricated, sourceHash = sha256(fabricated), sourceRecordId = "accusative:$fabricated")
        // The record is internally consistent: its stored hash is the hash of its stored text.
        assertEquals(sha256(forged.originalSourceText), forged.sourceHash)
        assertTrue(V2Validator.isFrozenSourceRecord(pack, forged))
        assertTrue(bindingErrors(pack, listOf(forged)).any { it.contains("not in the frozen file") })
        // A stored hash that does not match the span found in the file also fails.
        val wrongHash = record.copy(sourceHash = sha256(record.originalSourceText + " "))
        assertTrue(bindingErrors(pack, listOf(wrongHash)).any { it.contains("recomputed from the frozen file") })
    }

    /** Binding errors, less the 24 allowlisted unfrozen records, each matched by ID, source and text hash. */
    private fun violations(pack: V2CurriculumPack, records: List<V2ProvenanceRecord>): List<String> {
        val byId = records.associateBy { it.itemId }
        return bindingErrors(pack, records).filterNot { error ->
            val record = byId.getValue(error.substringBefore(": "))
            error.contains("names no frozen file") &&
                unfrozenRecords[record.itemId] == (record.sourceId to sha256(record.originalSourceText))
        }
    }

    private fun bindingErrors(pack: V2CurriculumPack, records: List<V2ProvenanceRecord>): List<String> {
        val (bySourceRevision, openRussian) = manifests()
        val errors = mutableListOf<String>()
        records.filterNot { isOriginal(pack, it) }.forEach { record ->
            val candidates = if (record.sourceId.startsWith("src-openrussian-")) {
                val (commit, files) = openRussian
                if (record.sourceId == "src-openrussian-${commit.take(8)}" && record.sourceRevision == commit)
                    listOfNotNull(files[record.sourceRecordId.substringBefore(':')]) else emptyList()
            } else bySourceRevision[record.sourceId to record.sourceRevision].orEmpty()
            if (candidates.isEmpty()) {
                errors += "${record.itemId}: ${record.sourceId} at ${record.sourceRevision} names no frozen file"
                return@forEach
            }
            candidates.forEach { frozen ->
                if (sha256(frozen.file.readBytes()) != frozen.sha256) errors += "${record.itemId}: ${frozen.file} does not match its manifest hash"
            }
            val found = candidates.mapNotNull { frozen ->
                val raw = frozen.file.readText()
                if (record.transformation == "readyrussian text extraction") {
                    // The intake extracts visible blocks of the frozen HTML and joins them with a blank line.
                    val visible = BatchARemainingSources.htmlVisible(raw)
                    val blocks = record.originalSourceText.split("\n\n").map { block ->
                        val at = visible.indexOf(block)
                        if (block.isEmpty() || at < 0) null else visible.substring(at, at + block.length)
                    }
                    if (blocks.any { it == null }) null else blocks.joinToString("\n\n")
                } else {
                    val at = raw.indexOf(record.originalSourceText)
                    if (record.originalSourceText.isEmpty() || at < 0) null else raw.substring(at, at + record.originalSourceText.length)
                }
            }
            when {
                found.isEmpty() -> errors += "${record.itemId}: originalSourceText is not in the frozen file ${candidates.joinToString { it.file.name }}"
                found.none { sha256(it) == record.sourceHash } -> errors += "${record.itemId}: sourceHash is not the hash recomputed from the frozen file span"
            }
        }
        return errors
    }

    private fun isOriginal(pack: V2CurriculumPack, record: V2ProvenanceRecord): Boolean =
        pack.sources.single { it.id == record.sourceId }.license in setOf("Original app text", "Public domain writing-system facts")

    /**
     * (source ID, revision) -> frozen files, and the OpenRussian commit with its files by name. A content entry is bound to
     * the source ID it names; entries of a manifest's "wikibooks" section that name none are pages of the Wikibooks
     * Russian book (src-wikibooks-russian-cc-by-sa). License-route pages, OpenRussian rows, decisions and exclusions
     * are not content files for any source.
     */
    private fun manifests(): Pair<Map<Pair<String, String>, List<FrozenFile>>, Pair<String, Map<String, FrozenFile>>> {
        val bySourceRevision = mutableMapOf<Pair<String, String>, MutableSet<FrozenFile>>()
        var commit = ""
        val openRussian = mutableMapOf<String, FrozenFile>()
        fun walk(element: JsonElement, base: File, implicitSource: String?) {
            when (element) {
                is JsonObject -> {
                    val file = element["file"] as? JsonPrimitive
                    val sha = element["sha256"] as? JsonPrimitive
                    val source = (element["sourceId"] as? JsonPrimitive)?.content ?: implicitSource
                    if (file != null && file.isString && sha != null && source != null) {
                        val revision = (element["oldid"] as? JsonPrimitive)?.content ?: sha.content
                        bySourceRevision.getOrPut(source to revision) { mutableSetOf() } +=
                            FrozenFile(File(base, file.content).canonicalFile, sha.content)
                    }
                    element.values.forEach { walk(it, base, implicitSource) }
                }
                is JsonArray -> element.forEach { walk(it, base, implicitSource) }
                else -> Unit
            }
        }
        File(repo(), "catalog/sources").listFiles()!!.mapNotNull { File(it, "FREEZE.json").takeIf(File::isFile) }.forEach { manifest ->
            val json = Json.parseToJsonElement(manifest.readText())
            json.jsonObject.forEach { (section, value) ->
                when (section) {
                    "license", "openrussian", "decisions", "excluded", "retrieved" -> Unit
                    "wikibooks" -> walk(value, manifest.parentFile, WIKIBOOKS_BOOK_SOURCE)
                    else -> walk(value, manifest.parentFile, null)
                }
            }
            json.jsonObject["openrussian"]?.jsonObject?.let { block ->
                commit = block.getValue("commit").jsonPrimitive.content
                block.getValue("files").jsonObject.forEach { (name, value) ->
                    val entry = value.jsonObject
                    openRussian[name] = FrozenFile(File(manifest.parentFile, entry.getValue("path").jsonPrimitive.content).canonicalFile,
                        entry.getValue("sha256").jsonPrimitive.content)
                }
            }
        }
        assertTrue(commit.isNotEmpty())
        return bySourceRevision.mapValues { it.value.toList() } to (commit to openRussian)
    }

    private companion object {
        const val WIKIBOOKS_BOOK_SOURCE = "src-wikibooks-russian-cc-by-sa"
    }

    private fun frozenTexts(): List<String> = File(repo(), "catalog/sources").walkTopDown()
        .filter { it.isFile && it.extension in setOf("txt", "csv", "html") }.map { it.readText() }.toList()

    private fun sha256(text: String) = sha256(text.toByteArray())
    private fun sha256(bytes: ByteArray) = MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())
    private fun repo() = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
