package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.S1IntakeSources
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2ProvenanceRecord
import dev.russiancompanion.v2.curriculum.V2Validator
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * T16c (owner decision D29a, approved rule change): every revision S1 froze under catalog/sources/s1-intake is admitted to its
 * licence route by exact source ID and revision, and an unlisted revision of the same page is rejected. Checked through the
 * validator: each FREEZE.json entry becomes a record on its route (copied from an existing record of that route), once at the
 * frozen revision and once at another revision.
 */
class S1IntakeAdmissionTest {
    private val root = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
    private val freeze = Json.parseToJsonElement(File(root, "catalog/sources/s1-intake/FREEZE.json").readText()).jsonObject
    private val pages = (freeze.getValue("pages").jsonArray + freeze.getValue("supplemental").jsonArray).map { it.jsonObject }
    private val routes = mapOf(
        "wikipedia" to S1IntakeSources.Route.WIKIPEDIA, "ruwiki" to S1IntakeSources.Route.RU_WIKIPEDIA,
        "wiktionary" to S1IntakeSources.Route.WIKTIONARY, "readyrussian" to S1IntakeSources.Route.READY_RUSSIAN,
    )
    private val routeError = Regex("license route is missing")

    @Test
    fun theAdmissionListIsExactlyTheS1FreezeList() {
        val expected = pages.map { page ->
            S1IntakeSources.Entry(page.string("sourceId"), routes.getValue(page.string("licenseRoute")), page.string("oldid"))
        }
        assertEquals(17, expected.size)
        assertEquals(expected.toSet(), S1IntakeSources.ENTRIES.toSet())
        assertEquals(expected.size, S1IntakeSources.ENTRIES.size)
        // No wildcard: another revision of a listed page, or a listed revision under another source ID, is not admitted.
        S1IntakeSources.ENTRIES.forEach { entry ->
            assertTrue(entry.sourceId, S1IntakeSources.admits(entry.sourceId, entry.revision, entry.route))
            assertFalse(entry.sourceId, S1IntakeSources.admits(entry.sourceId, entry.revision + "0", entry.route))
            assertFalse(entry.sourceId, S1IntakeSources.admits(entry.sourceId + "-x", entry.revision, entry.route))
            S1IntakeSources.Route.values().filter { it != entry.route }.forEach { other ->
                assertFalse("${entry.sourceId} $other", S1IntakeSources.admits(entry.sourceId, entry.revision, other))
            }
        }
    }

    @Test
    fun everyS1RevisionIsAdmittedToItsRouteAndAnUnlistedRevisionIsRejected() {
        val pack = V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText())
        fun template(route: String) = pack.provenanceRecords.first {
            when (route) {
                "wikipedia" -> it.sourceId == "src-enwiki-grammatical-aspect-1384289498"
                "ruwiki" -> it.sourceId == "src-ruwiki-narechie-152437423"
                // T18de (D62a, D66): retired nekogo template -> live Wiktionary template; the S1 revision probes stay.
                "wiktionary" -> it.sourceId == "src-enwiktionary-poltora-89737719"
                else -> it.sourceId == "src-readyrussian-grammar20"
            }
        }
        val admitted = mutableListOf<V2ProvenanceRecord>()
        val unlisted = mutableListOf<V2ProvenanceRecord>()
        pages.forEach { page ->
            val revision = page.string("oldid")
            val record = template(page.string("licenseRoute")).copy(
                itemId = "probe-s1-${page.string("key")}", sourceId = page.string("sourceId"), sourceRevision = revision,
                sourceUrl = page.string("url"), attribution = page.string("attribution"),
            )
            admitted += record
            val other = if (revision.all(Char::isDigit)) (revision.toLong() + 1).toString() else revision.reversed()
            unlisted += record.copy(
                itemId = "probe-s1-unlisted-${page.string("key")}", sourceRevision = other,
                sourceUrl = record.sourceUrl.replace(revision, other), attribution = record.attribution.replace(revision, other),
            )
        }
        val errors = V2Validator.validate(pack.copy(provenanceRecords = pack.provenanceRecords + admitted + unlisted)).errors
        // T16c CI fix: the two Unit 29 native-punctuation sources keep their per-record chain check, so a new probe record of
        // those pages is rejected even at the frozen revision; their admission is checked on the pack's own records instead.
        val chainChecked = setOf("src-ruwiki-obosoblenie-144053197", "src-ruwiki-boltraffio-149826784")
        chainChecked.forEach { id ->
            val own = pack.provenanceRecords.filter { it.sourceId == id }
            assertTrue(id, own.isNotEmpty())
            own.forEach { record ->
                assertTrue(record.itemId, errors.none { "Provenance ${record.itemId} " in it && routeError.containsMatchIn(it) })
            }
            assertTrue("$id probe at the frozen revision", errors.any { "Provenance probe-s1-" in it && routeError.containsMatchIn(it) &&
                admitted.any { r -> r.sourceId == id && "Provenance ${r.itemId} " in it } })
        }
        admitted.filter { it.sourceId !in chainChecked }.forEach { record ->
            val hits = errors.filter { "Provenance ${record.itemId} " in it && routeError.containsMatchIn(it) }
            assertTrue("${record.sourceId} ${record.sourceRevision}: $hits", hits.isEmpty())
        }
        unlisted.forEach { record ->
            assertTrue("${record.sourceId} ${record.sourceRevision} was admitted",
                errors.any { "Provenance ${record.itemId} " in it && routeError.containsMatchIn(it) })
        }
    }

    /**
     * T16c-n1 (owner decision D36a, approved rule change): an S1 source ID is admitted only at its exact S1 revision. A legacy
     * revision allowlist (here the ru-Wikipedia legacy revision 152437423) no longer admits it. Codex's T16c-check probe.
     */
    @Test
    fun anS1SourceIdAtALegacyRevisionIsRejected() {
        val pack = V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText())
        val page = pages.single { it.string("sourceId") == "src-ruwiki-vid-lingvistika-153277825" }
        val template = pack.provenanceRecords.first { it.sourceId == "src-ruwiki-narechie-152437423" }
        assertEquals("152437423", template.sourceRevision) // the legacy ru-Wikipedia revision
        val frozen = template.copy(itemId = "probe-d36a-frozen", sourceId = page.string("sourceId"), sourceRevision = page.string("oldid"),
            sourceUrl = page.string("url"), attribution = page.string("attribution"))
        val legacy = frozen.copy(itemId = "probe-d36a-legacy", sourceRevision = "152437423",
            sourceUrl = frozen.sourceUrl.replace("oldid=${page.string("oldid")}", "oldid=152437423"))
        val errors = V2Validator.validate(pack.copy(provenanceRecords = pack.provenanceRecords + frozen + legacy)).errors
        assertTrue(errors.joinToString("\n"), errors.any { "Provenance probe-d36a-legacy " in it && routeError.containsMatchIn(it) })
        assertTrue(errors.none { "Provenance probe-d36a-frozen " in it && routeError.containsMatchIn(it) })
        // The legacy revision still admits a source that S1 does not list (every existing narechie record).
        pack.provenanceRecords.filter { it.sourceId == "src-ruwiki-narechie-152437423" }.forEach { record ->
            assertTrue(record.itemId, errors.none { "Provenance ${record.itemId} " in it && routeError.containsMatchIn(it) })
        }
    }

    private fun kotlinx.serialization.json.JsonObject.string(key: String) = getValue(key).jsonPrimitive.content
}
