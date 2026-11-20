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
     * T15a-n3: the exact records whose source has no frozen file under catalog/sources/, each pinned to its source and to
     * the SHA-256 of its originalSourceText. S1 (owner decisions D16a, D24c) froze Reflexive pronoun oldid 1374712982,
     * Обособление oldid 144053197 and Больтраффио oldid 149826784 under catalog/sources/s1-intake, and all 24 records
     * that were listed here (4 Unit 21, 20 Unit 29) now bind to those files, so the list is empty. It only shrinks.
     */
    private val unfrozenRecords: Map<String, Pair<String, String>> = emptyMap()

    /** S1: the 24 records that were excepted before S1, each now bound to its frozen revision. */
    private val boundInS1: Set<String> =
        listOf("other", "personal", "reflexive", "self").map { "prov-u21-object-transfer-native-$it" }.toSet() +
            listOf("head", "region-1", "region-2", "ru").map { "prov-u29-punctuation-native-after-$it" } +
            listOf("head", "region-0", "region-1", "region-2", "ru").map { "prov-u29-punctuation-native-cause-$it" } +
            listOf("boundary-bergognone", "head", "list-foppa", "region-0", "region-1", "region-2", "ru")
                .map { "prov-u29-punctuation-native-pronoun-$it" } +
            listOf("head", "region-0", "region-1", "ru").map { "prov-u29-punctuation-native-terminal-$it" }

    @Test
    fun everyRecordNamingAFrozenSourceIsBoundToTheNamedFrozenFile() {
        val pack = pack()
        // S1: the exception is empty (was 24 records before S1).
        assertEquals(0, unfrozenRecords.size)
        val errors = violations(pack, pack.provenanceRecords)
        assertTrue(errors.joinToString("\n"), errors.isEmpty())
        // The 24 records excepted before S1 are present and each binds to its frozen revision with no error.
        val formerlyExcepted = pack.provenanceRecords.filter { it.itemId in boundInS1 }
        assertEquals(24, boundInS1.size)
        assertEquals(boundInS1, formerlyExcepted.map { it.itemId }.toSet())
        assertEquals(emptyList<String>(), bindingErrors(pack, formerlyExcepted))
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
    fun anS1RecordOffItsFrozenRevisionOrTextFails() {
        // S1 replaces aTwentyFifthRecordUnderAnAllowedUnfrozenSourceFails: with no exception left, a record under an S1
        // source binds only through its frozen revision and only with text that is in that file.
        val pack = pack()
        val bound = pack.provenanceRecords.single { it.itemId == "prov-u29-punctuation-native-pronoun-head" }
        assertTrue(bindingErrors(pack, listOf(bound)).isEmpty())
        val otherRevision = bound.copy(sourceRevision = "149826783")
        assertTrue(violations(pack, listOf(otherRevision)).any { it.contains("names no frozen file") })
        val changed = bound.copy(originalSourceText = bound.originalSourceText + " Probe.")
        assertTrue(violations(pack, listOf(changed)).any { it.contains("not in the frozen file") })
        val reflexive = pack.provenanceRecords.single { it.itemId == "prov-u21-object-transfer-native-self" }
        assertTrue(bindingErrors(pack, listOf(reflexive)).isEmpty())
        val relabelled = reflexive.copy(sourceId = "src-ruwiki-obosoblenie-144053197")
        assertTrue(violations(pack, listOf(relabelled)).any { it.contains("names no frozen file") })
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
