package dev.russiancompanion.v2

import dev.russiancompanion.v2.audio.*
import dev.russiancompanion.v2.curriculum.V2PackLoader
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.util.Base64

/** A2-17 per-clip verification record and the A2-18 offline Block 1 listen-check sheet. */
class ListenCheckSheetTest {
    private val root = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }.canonicalFile
    private val assets = File(root, "app/src/main/assets")
    private val pack by lazy { V2PackLoader.parse(File(assets, "content/v2-curriculum.json").readText()) }
    private val clips by lazy { loadCatalogClips(assets) }
    private val candidates by lazy { listenCheckCandidates(pack, blockUnits(1), clips) }

    @Test fun block1CandidatesAreExactTextMatchesShownInUnitsOneToFive() {
        assertEquals(1..5, blockUnits(1)); assertEquals(26..30, blockUnits(6))
        assertEquals(16, candidates.size)
        assertEquals(candidates.map { it.clipId }.distinct(), candidates.map { it.clipId })
        val byId = clips.associateBy { it.id }
        for (c in candidates) {
            assertEquals(byId.getValue(c.clipId).sha256, c.clipSha256)
            assertEquals(sha256Hex(clipFile(assets, c.clipId).readBytes()), c.clipSha256)
            assertTrue(c.uses.isNotEmpty() && c.uses.all { it.unitNumber in 1..5 })
            // Every use really shows this text (stress marks ignored, nothing else normalized).
            for (use in c.uses) assertTrue(audioTargetTexts(pack, use.targetKind, use.targetId)!!.any { stressFree(it) == stressFree(c.text) })
        }
        assertTrue(candidates.any { c -> c.uses.any { it.targetKind == "LETTER" } })
        // A later block draws its own candidates; nothing from Units 1–5 leaks into it.
        assertTrue(listenCheckCandidates(pack, blockUnits(2), clips).all { c -> c.uses.all { it.unitNumber in 6..10 } })
    }

    @Test fun committedSheetIsCurrentOfflineAndOffersMatchNoMatchUnsureAndExport() {
        val committed = File(root, "audit/listen-check/block1.html").readText()
        val fresh = listenCheckSheetHtml("block1", "Block 1 listen-check", candidates) { clipFile(assets, it).readBytes() }
        assertEquals("audit/listen-check/block1.html is stale; run generateListenCheckSheet -PlistenCheckBlock=1", fresh, committed)
        assertEquals(fresh, listenCheckSheetHtml("block1", "Block 1 listen-check", candidates) { clipFile(assets, it).readBytes() })
        assertFalse(Regex("(https?:|<link|<script\\s+src|@import|\\burl\\()", RegexOption.IGNORE_CASE).findAll(committed)
            .any { it.value != "URL(" })
        assertTrue(committed.contains("default-src 'none'"))
        val sources = Regex("src=\"data:audio/ogg;base64,([A-Za-z0-9+/=]+)\"").findAll(committed).map { it.groupValues[1] }.toList()
        assertEquals(candidates.size, sources.size)
        assertEquals(candidates.map { it.clipSha256 }, sources.map { sha256Hex(Base64.getDecoder().decode(it)) })
        for ((index, _) in candidates.withIndex()) for (value in listOf("MATCH", "NO_MATCH", "UNSURE"))
            assertTrue(committed.contains("name=\"d$index\" value=\"$value\""))
        assertTrue(committed.contains("id=\"export\"") && committed.contains("block1") && committed.contains(".download = sheet + \"-results.json\""))
        assertTrue(committed.contains(listenCheckCandidatesDigest("block1", candidates)))
    }

    @Test fun onlyMatchResultsBecomeOwnerListenVerifiedRecordsAndBadExportsAreRefused() {
        val digest = listenCheckCandidatesDigest("block1", candidates)
        val decisions = candidates.mapIndexed { i, c -> ListenCheckResult(c.clipId, c.clipSha256, c.text,
            listOf("MATCH", "NO_MATCH", "UNSURE")[i % 3], "OWNER", "2026-11-11T00:00:00Z") }
        val json = Json { prettyPrint = true }
        fun export(e: ListenCheckExport) = json.encodeToString(ListenCheckExport.serializer(), e)
        val good = ListenCheckExport(LISTEN_CHECK_SCHEMA, "block1", digest, decisions)
        val (records, errors) = listenCheckRecords(export(good), "block1", candidates)
        assertEquals(emptyList<String>(), errors)
        val matched = decisions.filter { it.decision == "MATCH" }.map { it.clipId }.toSet()
        assertEquals(matched, records.map { it.clipId }.toSet())
        assertEquals(candidates.filter { it.clipId in matched }.sumOf { it.uses.size }, records.size)
        assertTrue(records.all { it.status == OWNER_LISTEN_VERIFIED && it.verifier == "OWNER" && it.sheet == "block1" })
        val registry = V2AudioVerificationRegistry(records = records)
        assertEquals(emptyList<String>(), audioVerificationDefects(registry, pack, clips.associateBy { it.id }))
        assertEquals(records.groupBy { it.unitNumber }.mapValues { (_, r) -> r.map { it.clipId }.toSet() }, verifiedAudioByUnit(registry))

        val first = decisions.first()
        val bad = listOf(
            good.copy(candidatesDigest = "0".repeat(64)),
            good.copy(sheet = "block2"),
            good.copy(schema = 2),
            good.copy(results = decisions + first),
            good.copy(results = listOf(first.copy(clipSha256 = "0".repeat(64)))),
            good.copy(results = listOf(first.copy(text = first.text + "x"))),
            good.copy(results = listOf(first.copy(decision = "MAYBE"))),
            good.copy(results = listOf(first.copy(verifier = "SOMEONE"))),
            good.copy(results = listOf(first.copy(clipId = "rus-missing"))),
        )
        for (e in bad) {
            val (none, problems) = listenCheckRecords(export(e), "block1", candidates)
            assertTrue(none.isEmpty()); assertTrue(problems.isNotEmpty())
        }
        assertTrue(listenCheckRecords("not json", "block1", candidates).second.isNotEmpty())
        // An empty export binds nothing.
        assertTrue(listenCheckRecords(export(good.copy(results = emptyList())), "block1", candidates).first.isEmpty())
    }

    @Test fun committedRegistryIsEmptyAndCatalogFlagsNeverCountAsVerification() {
        val registry = Json.decodeFromString(V2AudioVerificationRegistry.serializer(), File(assets, "content/audio-verification.json").readText())
        assertEquals(AUDIO_VERIFICATION_SCHEMA, registry.schema)
        assertTrue(registry.records.isEmpty())
        assertTrue(verifiedAudioByUnit(registry).isEmpty())
        // The legacy catalog marks clips matchVerified; that never creates a verification record.
        assertTrue(File(assets, "content/bundled-content.json").readText().contains("\"matchVerified\": true"))
        val clipMap = clips.associateBy { it.id }
        assertEquals(emptyList<String>(), audioVerificationDefects(registry, pack, clipMap))
        val c = candidates.first(); val use = c.uses.first()
        val ok = V2AudioVerificationRecord(c.clipId, c.clipSha256, c.text, use.unitNumber, use.targetKind, use.targetId,
            OWNER_LISTEN_VERIFIED, "OWNER", "2026-11-11T00:00:00Z", "block1")
        assertEquals(emptyList<String>(), audioVerificationDefects(V2AudioVerificationRegistry(records = listOf(ok)), pack, clipMap))
        val tampered = listOf(ok.copy(clipSha256 = "0".repeat(64)), ok.copy(text = ok.text + "x"), ok.copy(status = "CATALOG_MATCH"),
            ok.copy(targetId = "missing"), ok.copy(targetKind = "FILENAME"), ok.copy(clipId = "rus-missing"), ok.copy(verifier = " "))
        for (r in tampered) assertTrue(audioVerificationDefects(V2AudioVerificationRegistry(records = listOf(r)), pack, clipMap).isNotEmpty())
        assertTrue(audioVerificationDefects(V2AudioVerificationRegistry(records = listOf(ok, ok)), pack, clipMap).isNotEmpty())
        assertTrue(audioVerificationDefects(V2AudioVerificationRegistry(schema = 2), pack, clipMap).isNotEmpty())
    }
}
