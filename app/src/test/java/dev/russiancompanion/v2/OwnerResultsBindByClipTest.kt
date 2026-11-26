package dev.russiancompanion.v2

import dev.russiancompanion.testing.pythonProcess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.util.concurrent.TimeUnit

/**
 * T17g (owner decision D52a): an owner listen-check result binds by clip ID and SHA-256; its manifestId is a locator only. The
 * manifest row IDs are positional (A16-NNN), so a new course word renumbers the rows after it (T17f moved over and become from
 * A16-012/A16-018 to A16-013/A16-019). The manifest builder runs in a scratch copy of the repository layout with the real pack,
 * clip catalog and registry:
 * - every owner manifestId rotated to another result's locator (and one to a locator that does not exist): the manifest is
 *   byte for byte the committed one;
 * - a course word moved to an earlier unit, which renumbers the rows: every decided clip keeps the status and owner note of its
 *   committed row, whatever its row ID is now;
 * - a result whose SHA-256 is not the bundled clip's: that decision applies to no clip.
 */
class OwnerResultsBindByClipTest {
    private val root = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }.canonicalFile
    private val ownerFile = File(root, "audit/listen-check/owner-verified-clips.json")
    private val owner by lazy { Json.parseToJsonElement(ownerFile.readText()).jsonObject }
    private val results by lazy { owner.getValue("results").jsonArray.map { it.jsonObject } }
    private val committed by lazy { File(root, "docs/audio/audio-manifest.csv").readText() }
    private fun JsonObject.str(name: String) = getValue(name).jsonPrimitive.content
    private fun JsonObject.with(name: String, value: String) = JsonObject(this + (name to JsonPrimitive(value)))

    /** Runs build_audio_manifest.py with [ownerJson] as the owner results and [packJson] as the pack; returns the manifest text. */
    private fun build(ownerJson: String, packJson: String? = null): String {
        val scratch = Files.createTempDirectory("d52a").toFile()
        try {
            File(scratch, "catalog").mkdirs()
            File(root, "catalog/build_audio_manifest.py").copyTo(File(scratch, "catalog/build_audio_manifest.py"))
            File(scratch, "audit/listen-check").mkdirs()
            File(scratch, "audit/listen-check/owner-verified-clips.json").writeText(ownerJson)
            val content = File(root, "app/src/main/assets/content")
            val pack = if (packJson == null) File(content, "v2-curriculum.json") else File(scratch, "content/v2-curriculum.json").also {
                it.parentFile.mkdirs(); it.writeText(packJson)
                File(content, "audio-verification.json").copyTo(File(it.parentFile, "audio-verification.json"))
            }
            val out = File(scratch, "audio-manifest.csv")
            val process = pythonProcess("-I", File(scratch, "catalog/build_audio_manifest.py").path, pack.path,
                File(content, "bundled-content.json").path, out.path).directory(scratch).redirectErrorStream(true).start()
            val log = process.inputStream.bufferedReader().readText()
            assertTrue("manifest builder timed out", process.waitFor(300, TimeUnit.SECONDS))
            assertEquals("manifest builder failed:\n$log", 0, process.exitValue())
            return out.readText()
        } finally {
            scratch.deleteRecursively()
        }
    }

    private fun ownerJson(changed: List<JsonObject>) = Json.encodeToString(JsonObject.serializer(),
        JsonObject(owner + ("results" to JsonArray(changed))))

    /** Row of [clip] in a manifest: (id, status, owner note present). */
    private fun rowOf(manifest: String, clip: String): Triple<String, String, Boolean> =
        csvRows(manifest).drop(1).single { it[7] == clip }.let { Triple(it[0], it[6], it[9].contains("owner listen-check MATCH (D35)")) }

    @Test fun theCommittedOwnerFileBuildsTheCommittedManifest() {
        assertEquals(committed, build(ownerFile.readText()))
    }

    @Test fun rotatedOrMissingLocatorsChangeNothing() {
        val ids = results.map { it.str("manifestId") }
        val rotated = results.mapIndexed { i, r -> r.with("manifestId", if (i == 0) "A99-999" else ids[(i + 1) % ids.size]) }
        assertNotEquals(ids, rotated.map { it.str("manifestId") })
        assertEquals(committed, build(ownerJson(rotated)))
    }

    @Test fun aRenumberingKeepsEveryDecisionOnItsClip() {
        // Move the first Unit 3 owner-decided word into Unit 2 (a reference introduced there): it sorts ahead of every Unit 2
        // word, so the Unit 2 rows and the Unit 3 rows renumber.
        val rows = csvRows(committed).drop(1)
        val decided = results.associateBy { it.str("clipId") }
        val probe = rows.filter { it[2] == "3" && it[7] in decided }.minByOrNull { it[0] }!!
        val word = decided.getValue(probe[7]).str("text")
        val text = File(root, "app/src/main/assets/content/v2-curriculum.json").readText()
        assertEquals(1, Regex("\"references\":\\[").findAll(text).count())
        val reference = Json.encodeToString(JsonObject.serializer(), JsonObject(mapOf("id" to JsonPrimitive("ref-d52a-probe"),
            "title" to JsonPrimitive(word), "introducedInUnit" to JsonPrimitive(2))))
        val moved = build(ownerFile.readText(), text.replace("\"references\":[", "\"references\":[$reference,"))
        assertNotEquals(committed, moved)
        var renumbered = 0
        for (r in results) {
            val clip = r.str("clipId")
            val before = rowOf(committed, clip)
            val after = rowOf(moved, clip)
            if (before.first != after.first) renumbered++
            assertEquals(clip, before.second to before.third, after.second to after.third)
            assertTrue(clip, after.third)
        }
        // The probe word and the Unit 2 word behind it moved; so did the stale locators the owner file still holds.
        assertNotEquals(probe[0], rowOf(moved, probe[7]).first)
        assertTrue(renumbered >= 2)
        assertTrue(results.any { rowOf(moved, it.str("clipId")).first != it.str("manifestId") })
    }

    @Test fun aDecisionWithAnotherShaAppliesToNoClip() {
        val target = results.first()
        val clip = target.str("clipId")
        val changed = results.map { if (it === target) it.with("clipSha256", "0".repeat(64)) else it }
        val manifest = build(ownerJson(changed))
        val row = rowOf(manifest, clip)
        assertEquals("EXISTING_CLIP_CANDIDATE", row.second)
        assertFalse(row.third)
        // Every other decision is unaffected.
        for (r in results.drop(1)) assertEquals(rowOf(committed, r.str("clipId")), rowOf(manifest, r.str("clipId")))
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
