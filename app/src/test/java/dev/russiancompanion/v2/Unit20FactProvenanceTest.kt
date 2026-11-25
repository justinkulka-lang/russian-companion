package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/**
 * T17e (Codex prep audit/prep/unit-20-prep-at-20bfde5c.txt): Unit 20's corrections, typed keys and fact markers.
 * - Он шёл к другу renders as G:1307 ("He was on his way to a friend's place"), in teaching, remediation, the example note
 *   and its selection record; the G:1305 rendering of Он пошёл ("He went to a friend's place") is not used for it.
 * - The unsupported long duration is never stated as a reading of the sentence; the setting-out and round-trip readings stay
 *   bounded to their examples; the leading verb also drives a vehicle (G:1321).
 * - Twelve typed checks, keys taught first; летят, ходит and ездил are unstressed as in their frozen lines (G:1289, 1292, 1299).
 * - Every Unit 20 marker resolves to frozen-source provenance of Unit 20; REWORD and REMOVE sentences are gone.
 */
class Unit20FactProvenanceTest {
    private val root = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
    private val pack by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()) }
    private val grammar by lazy { File(root, "catalog/sources/units-26-30/wikipedia/russian-grammar-1369617627.txt").readLines() }
    private val unit by lazy { pack.unit(20)!! }
    private val steps by lazy { unit.sessions.flatMap { it.steps } }
    private val items by lazy { pack.items.filter { it.objectiveId == "obj-u20-motion" } }
    private val learnerText by lazy {
        steps.map { it.body } + items.flatMap { listOf(it.prompt, it.feedback) + it.options + it.distractorRationales } +
            pack.examples.filter { it.objectiveId == "obj-u20-motion" }.flatMap { listOf(it.display, it.note.orEmpty()) } +
            listOf(unit.topicSummary, unit.mappingNote)
    }

    @Test fun theSourceRenderingAndBoundedReadingsAreTaught() {
        assertTrue(grammar[1306].contains("He was on his way to a friend's place") && grammar[1306].contains("Он шёл к другу."))
        val motion4 = steps.single { it.id == "u20-st-teach-motion-4" }.body
        assertTrue(motion4.contains("Он шёл к другу. — He was on his way to a friend's place."))
        assertEquals("He was on his way to a friend's place", pack.examples.single { it.id == "ex-u20-motion-5" }.note)
        assertEquals("He was on his way to a friend's place", pack.provenanceRecords.single { it.itemId == "prov-ex-u20-motion-5-en" }.shippedText)
        // The wrong rendering, the endorsed duration, the distractor framing and the removed commentary are gone.
        for (gone in listOf("He went to a friend's place", "is an imperfective duration reading", "The distractor",
                "A question about its meaning is different", "Distinguish a motion verb’s meaning from its directionality",
                "The supplied interpretation is bounded", "For нести́ choose to carry", "retained", "Qualified review")) {
            assertTrue(gone, learnerText.none { gone in it })
        }
        assertTrue(steps.single { it.id == "u20-st-teach-direction-return" }.body.contains("In this example the past round-trip interpretation"))
        assertTrue(steps.single { it.id == "u20-st-teach-motion-3" }.body.contains("вести́ — to lead; it can also mean to drive a vehicle."))
        assertTrue(grammar[1320].contains("'to drive (a vehicle)'"))
    }

    @Test fun typedChecksAreTaughtFirstAndKeepFrozenStress() {
        val objective = unit.objectives.single { it.id == "obj-u20-motion" }
        val typed = objective.checkItemIds.mapNotNull(pack::item).filter { it.isTypedResponse() }
        assertEquals(20, objective.checkItemIds.size)
        assertEquals(12, typed.size)
        val taught = objective.instructionStepIds.joinToString(" ") { id -> steps.single { it.id == id }.body }
        val forms = steps.indexOfFirst { it.id == "u20-st-teach-forms" }
        assertTrue(forms in 0 until steps.indexOfFirst { it.kind == "GUIDED" })
        for (item in typed) {
            assertTrue(item.id, Regex("(?<![\\p{L}\\u0301])${Regex.escape(item.answer)}(?![\\p{L}\\u0301])").containsMatchIn(taught))
            assertEquals(item.id, emptyList<String>(), V2Validator.typedKeyCitationErrors(pack, item))
        }
        for ((id, line) in listOf("u20-i-type-letyat" to 1289, "u20-i-type-khodit" to 1292, "u20-i-type-ezdil" to 1299)) {
            val key = pack.item(id)!!.answer
            assertFalse(id, key.contains('́'))
            assertTrue(id, grammar[line - 1].contains(key))
        }
        // Every check sits in the end-of-unit session.
        val checks = unit.sessions.single { it.id == "u20-s-checks" }.steps
        assertEquals(objective.checkItemIds, checks.single().itemIds)
        assertTrue(unit.sessions.filter { it.id != "u20-s-checks" }.flatMap { it.steps }.none { it.kind == "CHECK" })
    }

    @Test fun everyMarkerResolvesToFrozenUnit20Provenance() {
        val records = pack.provenanceRecords.associateBy { it.itemId }
        val hosts = steps.filter { it.russianFacts.isNotEmpty() }.map { it.russianFacts to it.russianFactSources } +
            items.filter { it.russianFacts.isNotEmpty() }.map { it.russianFacts to it.russianFactSources }
        assertTrue(hosts.sumOf { it.first.size } > 60)
        for ((facts, sources) in hosts) for (fact in facts) {
            val cited = sources[fact].orEmpty()
            assertTrue(fact, cited.isNotEmpty())
            for (id in cited) {
                val r = records.getValue(id)
                assertTrue("$fact -> $id", V2Validator.isFrozenSourceRecord(pack, r) && r.unitNumber == 20)
            }
        }
        // The quoted wrong option is not marked as a fact.
        assertTrue(hosts.none { (facts, _) -> "The form is perfective because this trip is completed." in facts })
        assertTrue(V2Validator.validate(pack).errors.joinToString("\n"), V2Validator.validate(pack).ok)
    }
}
