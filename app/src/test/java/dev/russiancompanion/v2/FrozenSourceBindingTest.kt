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
 * frozen file at its named revision (the oldid, the retained HTML sha256, or the OpenRussian commit and file). That
 * file's bytes must match the manifest hash, its originalSourceText must occur verbatim in the file, and the record's
 * sourceHash must equal the hash recomputed from the span found in the file. "readyrussian text extraction" records
 * hold the visible text of the frozen HTML (BatchARemainingSources.htmlVisible), so they are found in that text.
 */
class FrozenSourceBindingTest {
    private data class FrozenFile(val file: File, val sha256: String)

    /** Sources with records but no frozen file under catalog/sources/; reported to the owner, Units 21 and 29 only. */
    private val unfrozenSources = setOf(
        "src-ruwiki-obosoblenie-144053197",
        "src-ruwiki-boltraffio-149826784",
        "src-enwiki-reflexive-pronoun-1374712982",
    )

    @Test
    fun everyRecordNamingAFrozenSourceIsBoundToTheNamedFrozenFile() {
        val pack = pack()
        val errors = bindingErrors(pack, pack.provenanceRecords)
        val unbound = errors.filter { it.contains("names no frozen file") }
        val other = errors - unbound.toSet()
        assertTrue(other.joinToString("\n"), other.isEmpty())
        // The only records naming a source with no frozen file belong to the three listed sources, in Units 21 and 29,
        // and no russian-fact marker cites them. This set may not grow.
        val unboundRecords = pack.provenanceRecords.filter { record -> unbound.any { it.startsWith(record.itemId + ":") } }
        assertEquals(unfrozenSources, unboundRecords.map { it.sourceId }.toSet())
        assertEquals(setOf(21, 29), unboundRecords.map { it.unitNumber }.toSet())
        val cited = (pack.items.flatMap { it.russianFactSources.values.flatten() } +
            pack.units.flatMap { it.sessions }.flatMap { it.steps }.flatMap { it.russianFactSources.values.flatten() }).toSet()
        assertTrue(unboundRecords.none { it.itemId in cited })
        assertTrue(pack.provenanceRecords.count { !isOriginal(pack, it) } > 2000)
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

    private fun bindingErrors(pack: V2CurriculumPack, records: List<V2ProvenanceRecord>): List<String> {
        val (byRevision, openRussian) = manifests()
        val errors = mutableListOf<String>()
        records.filterNot { isOriginal(pack, it) }.forEach { record ->
            val candidates = if (record.sourceId.startsWith("src-openrussian-")) {
                val (commit, files) = openRussian
                if (record.sourceRevision == commit) listOfNotNull(files[record.sourceRecordId.substringBefore(':')]) else emptyList()
            } else byRevision[record.sourceRevision].orEmpty()
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

    /** Revision (oldid or retained HTML sha256) -> frozen files, and the OpenRussian commit with its files by name. */
    private fun manifests(): Pair<Map<String, List<FrozenFile>>, Pair<String, Map<String, FrozenFile>>> {
        val byRevision = mutableMapOf<String, MutableSet<FrozenFile>>()
        var commit = ""
        val openRussian = mutableMapOf<String, FrozenFile>()
        fun walk(element: JsonElement, base: File) {
            when (element) {
                is JsonObject -> {
                    val file = (element["file"] ?: element["path"]) as? JsonPrimitive
                    val sha = element["sha256"] as? JsonPrimitive
                    if (file != null && file.isString && sha != null) {
                        val key = (element["oldid"] as? JsonPrimitive)?.content ?: sha.content
                        byRevision.getOrPut(key) { mutableSetOf() } += FrozenFile(File(base, file.content).canonicalFile, sha.content)
                    }
                    element.values.forEach { walk(it, base) }
                }
                is JsonArray -> element.forEach { walk(it, base) }
                else -> Unit
            }
        }
        File(repo(), "catalog/sources").listFiles()!!.mapNotNull { File(it, "FREEZE.json").takeIf(File::isFile) }.forEach { manifest ->
            val json = Json.parseToJsonElement(manifest.readText())
            walk(json, manifest.parentFile)
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
        return byRevision.mapValues { it.value.toList() } to (commit to openRussian)
    }

    private fun frozenTexts(): List<String> = File(repo(), "catalog/sources").walkTopDown()
        .filter { it.isFile && it.extension in setOf("txt", "csv", "html") }.map { it.readText() }.toList()

    private fun sha256(text: String) = sha256(text.toByteArray())
    private fun sha256(bytes: ByteArray) = MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())
    private fun repo() = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
