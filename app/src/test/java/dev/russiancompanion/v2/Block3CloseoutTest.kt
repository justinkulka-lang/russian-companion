package dev.russiancompanion.v2

import dev.russiancompanion.v2.audio.*
import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.text.Normalizer
import java.util.Base64

/** T16f: Block 3 (Units 11-15) close-out content fixes and the Block 3 listen-check sheet. */
class Block3CloseoutTest {
    private val root = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }.canonicalFile
    private val assets = File(root, "app/src/main/assets")
    private val pack by lazy { V2PackLoader.parse(File(assets, "content/v2-curriculum.json").readText()) }

    private fun bare(text: String) = Normalizer.normalize(Normalizer.normalize(text, Normalizer.Form.NFD).replace("́", ""),
        Normalizer.Form.NFC).lowercase().trim(' ', '.', ',', '!', '?', ';', ':')

    private fun contains(hay: String, needle: String): Boolean {
        val n = bare(needle)
        return n.isNotEmpty() && Regex("(?<![\\p{L}])${Regex.escape(n)}(?![\\p{L}])").containsMatchIn(bare(hay))
    }

    @Test fun everyUnits11To15RequiredCheckHasItsCueOrKeyInItsRemediation() {
        // CC021: a missed check returns to remediation that shows the check's cue or key (Unit 12 had ten that showed neither).
        val examples = pack.examples.associateBy { it.id }
        var checks = 0
        for (unit in pack.units.filter { it.number in 11..15 }) {
            val sessions = unit.sessions
            for (objective in unit.objectives.filter { it.gating && it.checkItemIds.isNotEmpty() }) {
                val remId = objective.remediationStepIds.single()
                val session = sessions.single { s -> s.steps.any { it.id == remId } }
                val rem = session.steps.single { it.id == remId }
                val text = rem.body + "\n" + rem.exampleIds.mapNotNull { examples[it] }.joinToString("\n") { it.display + " " + it.note }
                // Remediation repeats the whole teaching of its session.
                // (Paragraphs are compared trimmed: the T16a Unit 11 remediation keeps the cards' trailing spaces.)
                assertEquals(rem.id, session.steps.filter { it.kind == "TEACH" }.joinToString("\n\n") { it.body.trim() }.split("\n\n").map { it.trim() },
                    rem.body.split("\n\n").map { it.trim() })
                for (id in objective.checkItemIds) {
                    val item = pack.item(id)!!
                    val cue = item.promptLetter.orEmpty()
                    assertTrue("$id: neither cue nor key is in ${rem.id}", (cue.isNotEmpty() && contains(text, cue)) || contains(text, item.answer))
                    checks++
                }
            }
        }
        assertEquals(100, checks)
    }

    @Test fun unit12To15FormCardsNameTheLabelTheirChecksAskFor() {
        // WC005: the labels come from the OpenRussian cell fields and the Future tense person headings.
        val all = pack.units.filter { it.number in 14..15 }.flatMap { it.sessions }.flatMap { it.steps }.associateBy { it.id }
        mapOf(
            "u14-st-teach-future-person-4" to "Они ска́жут the third-person plural future",
            "u14-st-teach-future-person-5" to "Вы бу́дете говори́ть the second-person plural",
            "u15-st-teach-say-request-1" to "скажи́ is the singular imperative and скажи́те the plural imperative of сказа́ть",
            "u15-st-teach-say-request-4" to "да́й is the singular imperative and да́йте the plural imperative of дать",
        ).forEach { (step, label) -> assertTrue("$step: $label", label in all.getValue(step).body) }
        val steps = pack.units.single { it.number == 12 }.sessions.flatMap { it.steps }.associateBy { it.id }
        val labels = mapOf(
            "u12-st-teach-future-imp-1" to "Вы бу́дете говори́ть the second-person plural compound future",
            "u12-st-teach-future-perf-4" to "ска́жешь is the second-person singular future of сказа́ть",
            "u12-st-teach-dative-4" to "столу́ is the dative singular of сто́л",
            "u12-st-teach-dative-5" to "до́му is the dative singular of до́м",
            "u12-st-teach-spelling-1" to "хоро́шее is the neuter nominative singular of хоро́ший",
            "u12-st-teach-prep-plural-4" to "стола́х is the prepositional plural of сто́л",
            "u12-st-teach-prep-plural-5" to "дома́х is its prepositional plural",
        )
        labels.forEach { (step, label) -> assertTrue("$step: $label", label in steps.getValue(step).body) }
        // WC009: each Unit 12 remediation links the reference entry about its own topic.
        assertEquals(mapOf(
            "u12-st-rem-future-imp" to listOf("ref-u12-future"), "u12-st-rem-future-perf" to listOf("ref-u12-future"),
            "u12-st-rem-aspect" to listOf("ref-ca-aspect"), "u12-st-rem-dative" to listOf("ref-u12-dative-prepositional"),
            "u12-st-rem-spelling" to listOf("ref-u12-spelling"), "u12-st-rem-prep-plural" to listOf("ref-u12-dative-prepositional"),
        ), steps.values.filter { it.kind == "REMEDIATE" }.associate { it.id to it.referenceIds })
    }

    @Test fun skazatIsGlossedSayTellAndTheNationalityReportsHaveEnglishGlosses() {
        val records = pack.provenanceRecords.associateBy { it.itemId }
        // CC024: the infinitive сказа́ть is glossed say, tell from verbs.csv:3 translations_en, as the items use it.
        for (id in listOf("ex-u12-aspect-1", "ex-u13-infinitive-1", "ex-u15-aspect-consolidate-1")) {
            val example = pack.examples.single { it.id == id }
            assertEquals(id, "сказа́ть", example.display)
            assertEquals(id, "say, tell", example.note)
            val record = records.getValue("prov-$id-en")
            assertEquals("verbs.csv:сказа'ть#translations_en", record.sourceRecordId)
            assertEquals("say, tell, speak, talk", record.originalSourceText)
            assertTrue(V2Validator.isFrozenSourceRecord(pack, record))
        }
        // WC004: Future tense 3353822 line 22 gives the English of both nationality reports; the label stays an answer source.
        val line22 = File(root, "catalog/sources/units-11-15/wikibooks/future-tense-3353822.txt").readLines()[21].trim()
        for ((id, gloss) in mapOf("ex-u13-tense-keep-0" to "He said he is English", "ex-u13-reported-2" to "He said he is English",
                "ex-u13-reported-3" to "He said he was English")) {
            assertEquals(id, gloss, pack.examples.single { it.id == id }.note)
            assertEquals(line22, records.getValue("prov-$id-en").originalSourceText)
        }
        assertEquals("we keep the tense the same", records.getValue("prov-t16f-u13-tense-keep-label").shippedText)
        assertTrue(pack.items.any { "prov-t16f-u13-tense-keep-label" in it.assessment?.answerSourceIds.orEmpty() })
        assertTrue(pack.examples.filter { it.objectiveId.matches(Regex("obj-u1[1-5]-.*")) }
            .none { it.note == "we keep the tense the same" || it.note == "был" })
    }

    @Test fun committedBlock3SheetIsCurrentAndListsOnlyUnits11To15Uses() {
        val clips = loadCatalogClips(assets)
        val candidates = listenCheckCandidates(pack, blockUnits(3), clips)
        assertEquals(11..15, blockUnits(3))
        assertEquals(listOf("дать", "дом", "стол"), candidates.map { it.text })
        assertTrue(candidates.all { c -> c.uses.isNotEmpty() && c.uses.all { it.unitNumber in 11..15 } })
        val committed = File(root, "audit/listen-check/block3.html").readText()
        val wavs = Regex("src=\"data:audio/wav;base64,([A-Za-z0-9+/=]+)\"").findAll(committed).map { Base64.getDecoder().decode(it.groupValues[1]) }.toList()
        assertEquals(candidates.size, wavs.size)
        val wavById = candidates.map { it.clipId }.zip(wavs).toMap()
        for (c in candidates) assertEquals(c.clipId, oggVorbisShape(clipFile(assets, c.clipId).readBytes()), wavPcm16Shape(wavById.getValue(c.clipId)))
        val fresh = listenCheckSheetHtml("block3", "Block 3 listen-check", candidates, { clipFile(assets, it).readBytes() }) { wavById.getValue(it) }
        assertEquals("audit/listen-check/block3.html is stale; run generateListenCheckSheet -PlistenCheckBlock=3", fresh, committed)
        assertTrue(committed.contains(listenCheckCandidatesDigest("block3", candidates)))
    }
}
