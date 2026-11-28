package dev.russiancompanion.v2

import dev.russiancompanion.v2.audio.*
import dev.russiancompanion.v2.curriculum.V2PackLoader
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.util.Base64

/**
 * T-AUDIO-1 (owner decisions D35, D36d; owner ruling 2026-11-24 on first-letter case): the owner listen-checked all 34
 * EXISTING_CLIP_CANDIDATE clips and marked every one MATCH. Each block sheet (1-6) is current, its results file records the
 * D35 decision for every sheet clip among the 34 in the schema-1 export format, and the committed registry is exactly what
 * listenCheckRecords makes of those results. Sheet clips outside the 34 stay undecided (open). The 7 verified clips that no
 * letter, example or item shows are VERIFIED but unbound in the manifest.
 */
class AudioVerificationBindingTest {
    private val root = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }.canonicalFile
    private val assets = File(root, "app/src/main/assets")
    private val pack by lazy { V2PackLoader.parse(File(assets, "content/v2-curriculum.json").readText()) }
    private val clips by lazy { loadCatalogClips(assets) }
    private val owner by lazy { Json.parseToJsonElement(File(root, "audit/listen-check/owner-verified-clips.json").readText()).jsonObject }
    private val ownerResults by lazy { owner.getValue("results").jsonArray.map { it.jsonObject } }
    private fun field(o: kotlinx.serialization.json.JsonObject, name: String) = o.getValue(name).jsonPrimitive.content
    private val d35At = "2026-11-23T21:22:00Z"

    /** Clips on each sheet that are not among the 34 and so have no decision. */
    private val open = mapOf(
        1 to setOf("rus-0b80646c", "rus-e58e0540", "rus-055bec5e", "rus-eb558d72", "rus-ec38496b", "rus-43455517", "rus-a4fd7810"),
        2 to setOf("rus-a4fd7810"), 3 to emptySet(), 4 to emptySet(), 5 to emptySet(), 6 to setOf("rus-0b80646c", "rus-e58e0540"),
    )

    /** Owner-verified clips no letter, example or item shows (manifest uses are teaching or reference text only). */
    private val unbound = mapOf("rus-1267a652" to "A04-015", "rus-d5ac2c6b" to "A04-016", "rus-80cf7d55" to "A05-013",
        // T18de (D52a, D66): A24-007 -> A24-005 for the same clip.
        "rus-12eda911" to "A09-001", "rus-fa1d892f" to "A11-005", "rus-8a9989d5" to "A11-007", "rus-8e14f567" to "A24-005")

    @Test fun theThirtyFourOwnerResultsMatchTheBundledClipsAndTheManifest() {
        assertEquals(1, owner.getValue("schema").jsonPrimitive.int)
        assertEquals(34, ownerResults.size)
        assertEquals(34, ownerResults.map { field(it, "clipId") }.distinct().size)
        val byId = clips.associateBy { it.id }
        val manifest = File(root, "docs/audio/audio-manifest.csv").readLines()
        for (r in ownerResults) {
            val id = field(r, "clipId")
            assertEquals(id, sha256Hex(clipFile(assets, id).readBytes()), field(r, "clipSha256"))
            assertEquals(id, byId.getValue(id).text, field(r, "text"))
            assertEquals(listOf("MATCH", "OWNER", d35At), listOf(field(r, "decision"), field(r, "verifier"), field(r, "recordedAt")))
            // D52a: the manifestId is a locator only (binding is by clip ID and SHA-256, OwnerResultsBindByClipTest); it must still
            // point at the clip's current row, so a renumbering is caught and the locator updated.
            assertEquals(id, 1, manifest.count { it.startsWith(field(r, "manifestId") + ",") && it.contains(",$id,") })
        }
    }

    @Test fun everySheetIsCurrentAndItsResultsRecordTheD35DecisionsOnly() {
        val verified = ownerResults.associate { field(it, "clipId") to field(it, "clipSha256") }
        val expected = mutableListOf<V2AudioVerificationRecord>()
        for (block in 1..6) {
            val sheet = "block$block"
            val candidates = listenCheckCandidates(pack, blockUnits(block), clips)
            val committed = File(root, "audit/listen-check/$sheet.html").readText()
            val wavs = Regex("src=\"data:audio/wav;base64,([A-Za-z0-9+/=]+)\"").findAll(committed)
                .map { Base64.getDecoder().decode(it.groupValues[1]) }.toList()
            assertEquals(sheet, candidates.size, wavs.size)
            val wavById = candidates.map { it.clipId }.zip(wavs).toMap()
            val fresh = listenCheckSheetHtml(sheet, "Block $block listen-check", candidates, { clipFile(assets, it).readBytes() }) { wavById.getValue(it) }
            assertEquals("audit/listen-check/$sheet.html is stale; run generateListenCheckSheet -PlistenCheckBlock=$block", fresh, committed)
            val text = File(root, "audit/listen-check/$sheet-results.json").readText()
            val export = Json.decodeFromString(ListenCheckExport.serializer(), text)
            assertEquals(sheet, export.results.map { it.clipId }.toSet(), candidates.map { it.clipId }.filter { it in verified }.toSet())
            assertEquals(sheet, open.getValue(block), candidates.map { it.clipId }.filter { it !in verified }.toSet())
            assertTrue(export.results.all { it.decision == "MATCH" && it.verifier == "OWNER" && it.recordedAt == d35At &&
                it.clipSha256 == verified[it.clipId] })
            val (records, errors) = listenCheckRecords(text, sheet, candidates)
            assertEquals(sheet, emptyList<String>(), errors)
            expected += records
        }
        val registry = Json.decodeFromString(V2AudioVerificationRegistry.serializer(), File(assets, "content/audio-verification.json").readText())
        assertEquals(expected, registry.records)
        assertEquals(emptyList<String>(), audioVerificationDefects(registry, pack, clips.associateBy { it.id }))
        // 27 of the 34 are bound; the other 7 appear on no sheet.
        assertEquals(verified.keys - unbound.keys, registry.records.map { it.clipId }.toSet())
        assertEquals(27, registry.records.map { it.clipId }.distinct().size)
    }

    @Test fun firstLetterCaseIsTheOnlyAddedDifference() {
        // The capitalized Unit 5 question word binds to the lowercase catalog clip (owner ruling 2026-11-24).
        val gde = clips.single { it.id == "rus-2aaba201" }
        assertEquals("где", gde.text)
        assertEquals(listOf("Где"), audioTargetTexts(pack, "EXAMPLE", "ex-u5-q-gde"))
        val ok = V2AudioVerificationRecord(gde.id, gde.sha256, gde.text, 5, "EXAMPLE", "ex-u5-q-gde", OWNER_LISTEN_VERIFIED, "OWNER", d35At, "block1")
        val map = clips.associateBy { it.id }
        assertEquals(emptyList<String>(), audioVerificationDefects(V2AudioVerificationRegistry(records = listOf(ok)), pack, map))
        assertEquals(audioMatchKey("Где"), audioMatchKey("где"))
        // T-AUDIO-1a expectation change (owner decision D48): old: any stress-mark difference matched (stressFree). New: only in
        // a word with exactly one vowel letter; где has one, so the stressed and plain forms still match.
        assertEquals(audioMatchKey("где́"), audioMatchKey("Где"))
        assertEquals(audioMatchKey("до́м"), audioMatchKey("дом"))
        // A word with more than one vowel letter must match its stress exactly: за́мок (castle) is not замо́к (lock), and
        // neither is the unmarked замок; ё is not е.
        assertNotEquals(audioMatchKey("за́мок"), audioMatchKey("замо́к"))
        assertNotEquals(audioMatchKey("за́мок"), audioMatchKey("замок"))
        assertNotEquals(audioMatchKey("её"), audioMatchKey("ее"))
        assertNotEquals(audioMatchKey("ёж"), audioMatchKey("еж"))
        // Whitespace is trimmed at the ends only.
        assertEquals(audioMatchKey(" дом "), audioMatchKey("дом"))
        assertNotEquals(audioMatchKey("мой стол"), audioMatchKey("мой  стол"))
        // A multi-vowel record whose stress differs from its target fails the registry check; with matching stress it binds.
        val zamokPack = pack.copy(examples = pack.examples.map { if (it.id == "ex-u5-q-gde") it.copy(display = "за́мок") else it })
        val zamokClip = V2CatalogClip(gde.id, "замо́к", gde.sha256)
        val zamokMap = map + (gde.id to zamokClip)
        val zamok = ok.copy(text = "замо́к")
        assertTrue(audioVerificationDefects(V2AudioVerificationRegistry(records = listOf(zamok)), zamokPack, zamokMap)
            .any { "is not the target's text" in it })
        val castlePack = pack.copy(examples = pack.examples.map { if (it.id == "ex-u5-q-gde") it.copy(display = "замо́к") else it })
        assertEquals(emptyList<String>(), audioVerificationDefects(V2AudioVerificationRegistry(records = listOf(zamok)), castlePack, zamokMap))
        // A record text that differs from a multi-vowel catalog text by its stress mark is not the clip's text either.
        assertTrue(audioVerificationDefects(V2AudioVerificationRegistry(records = listOf(zamok.copy(text = "за́мок"))), castlePack, zamokMap)
            .any { "differs from the clip's catalog text" in it })
        // Any other case difference, or a different letter, is a different text.
        for (other in listOf("ГДЕ", "гДе", "гдЕ", "Гда")) assertNotEquals(other, audioMatchKey("где"), audioMatchKey(other))
        val shouted = pack.copy(examples = pack.examples.map { if (it.id == "ex-u5-q-gde") it.copy(display = "ГДЕ") else it })
        assertTrue(audioVerificationDefects(V2AudioVerificationRegistry(records = listOf(ok)), shouted, map).isNotEmpty())
        // The record's own text must still equal the clip's catalog text (stress marks aside), not merely its key.
        assertTrue(audioVerificationDefects(V2AudioVerificationRegistry(records = listOf(ok.copy(text = "Где"))), pack, map).isNotEmpty())
    }

    /**
     * D48: the narrowed stress rule keeps every binding. All 27 bound clips are one-vowel words; 104 records bound before and
     * 104 bind after, 25 of them to a target that differs from the clip's text only by a stress mark (до́м / дом).
     */
    @Test fun everyBindingSurvivesTheNarrowedStressRule() {
        val registry = Json.decodeFromString(V2AudioVerificationRegistry.serializer(), File(assets, "content/audio-verification.json").readText())
        // T18de (D52a, D66): 104 -> 105 target bindings when the owner-verified clip also binds to the typed particle.
        assertEquals(105, registry.records.size)
        assertEquals(emptyList<String>(), audioVerificationDefects(registry, pack, clips.associateBy { it.id }))
        val vowels = "аеёиоуыэюяАЕЁИОУЫЭЮЯ"
        assertTrue(registry.records.map { it.text }.distinct().all { t -> stressFree(t).count { it in vowels } == 1 })
        val stressOnly = registry.records.count { r ->
            val targets = audioTargetTexts(pack, r.targetKind, r.targetId)!!
            targets.none { audioMatchKey(it) == audioMatchKey(r.text) && stressFree(it) == it } &&
                targets.any { audioMatchKey(it) == audioMatchKey(r.text) }
        }
        assertEquals(25, stressOnly)
    }

    @Test fun manifestStatusesFollowTheOwnerResultsAndTheRegistry() {
        val header = File(root, "docs/audio/audio-manifest.csv").readLines().first().split(",")
        assertEquals(listOf("id", "priority", "first_unit", "units", "kind", "text", "status", "clip", "clip_text", "flags", "where"), header)
        val rows = csvRows(File(root, "docs/audio/audio-manifest.csv").readText()).drop(1)
        // T17g (owner decision D52a) expectation change, old -> new: the owner result's row was looked up by its manifestId; now
        // by its clip (a result binds by clip ID and SHA-256; manifestId is a locator only, checked for currency in
        // theThirtyFourOwnerResultsMatchTheBundledClipsAndTheManifest). The status and note assertions are unchanged.
        for (r in ownerResults) {
            val row = rows.single { it[7] == field(r, "clipId") }
            val status = if (field(r, "clipId") in unbound) "VERIFIED" else "BOUND"
            assertEquals(row[0], status, row[6])
            assertTrue(row[0], row[9].contains("owner listen-check MATCH (D35)"))
        }
        assertEquals(unbound.values.toSet(), rows.filter { it[6] == "VERIFIED" }.map { it[0] }.toSet())
        assertEquals(27, rows.count { it[6] == "BOUND" })
        assertEquals(0, rows.count { it[6] == "EXISTING_CLIP_CANDIDATE" })
    }

    /** Minimal RFC 4180 reader: quoted fields may hold commas, quotes and line breaks. */
    private fun csvRows(text: String): List<List<String>> {
        val rows = mutableListOf<List<String>>(); var row = mutableListOf<String>(); val cell = StringBuilder()
        var quoted = false; var i = 0
        while (i < text.length) {
            val c = text[i]
            when {
                quoted && c == '"' && i + 1 < text.length && text[i + 1] == '"' -> { cell.append('"'); i++ }
                c == '"' -> quoted = !quoted
                !quoted && c == ',' -> { row += cell.toString(); cell.clear() }
                !quoted && (c == '\n' || c == '\r') -> {
                    if (c == '\r' && i + 1 < text.length && text[i + 1] == '\n') i++
                    row += cell.toString(); cell.clear(); rows += row; row = mutableListOf()
                }
                else -> cell.append(c)
            }
            i++
        }
        if (cell.isNotEmpty() || row.isNotEmpty()) { row += cell.toString(); rows += row }
        return rows
    }
}
