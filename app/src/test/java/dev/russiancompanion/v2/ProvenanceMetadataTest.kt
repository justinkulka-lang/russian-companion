package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.notices.offlineNoticeCatalog
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class ProvenanceMetadataTest {
    private val root = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
    private val fields = setOf("reviewerIdentity", "reviewerQualification", "reviewDate", "reviewedDigest")

    @Test fun separatedMetadataKeepsRuntimeProvenanceValidationDigestsAndOfflineNotices() {
        val raw = Json.parseToJsonElement(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).jsonObject
        val metadata = Json.parseToJsonElement(File(root, "catalog/provenance-review-metadata.json").readText()).jsonObject
        assertEquals(raw.getValue("packId"), metadata.getValue("packId"))
        assertEquals(1, metadata.getValue("schemaVersion").jsonPrimitive.int)
        val rows = metadata.getValue("records").jsonArray.map { it.jsonObject }
        val byId = rows.associateBy { it.getValue("itemId").jsonPrimitive.content }
        assertEquals(rows.size, byId.size)
        assertTrue(rows.isNotEmpty())
        val records = raw.getValue("provenanceRecords").jsonArray
        val ids = records.map { it.jsonObject.getValue("itemId").jsonPrimitive.content }.toSet()
        assertTrue(ids.containsAll(byId.keys))
        for (row in records) assertTrue(row.jsonObject.keys.intersect(fields).isEmpty())
        for (row in rows) {
            assertEquals(fields + "itemId", row.keys)
            assertTrue(fields.all { row.getValue(it).jsonPrimitive.content.isEmpty() })
        }
        // Rejoin the preserved authoring data and compare the actual typed
        // models and behaviors, including strict source/rights validation.
        val restored = JsonObject(raw + ("provenanceRecords" to JsonArray(records.map { value ->
            val record = value.jsonObject
            JsonObject(record + byId[record.getValue("itemId").jsonPrimitive.content].orEmpty())
        })))
        val bundled = V2PackLoader.parse(raw.toString())
        val joined = V2PackLoader.parse(restored.toString())
        assertEquals(joined, bundled)
        assertTrue(V2Validator.validate(bundled).ok)
        assertEquals(V2Validator.validate(joined), V2Validator.validate(bundled))
        assertEquals(V2Validator.contentReviewDigest(joined), V2Validator.contentReviewDigest(bundled))
        for (unit in bundled.units) assertEquals(V2Validator.unitContentReviewDigest(joined, unit.number),
            V2Validator.unitContentReviewDigest(bundled, unit.number))
        assertEquals(offlineNoticeCatalog(joined, emptyList()), offlineNoticeCatalog(bundled, emptyList()))
        assertFalse(bundled.publicReleaseEligible)
        assertTrue(bundled.unitReviews.all { it.reviewer.isNullOrBlank() })
        assertFalse(File(root, "app/src/main/assets/provenance-review-metadata.json").exists())
        assertFalse(File(root, "app/src/main/assets/content/provenance-review-metadata.json").exists())
    }

    @Test fun authoringProjectionPreservesDataAndRejectsConflictingIdentities() {
        val process = ProcessBuilder("python3", File(root, "catalog/test_provenance_metadata.py").path)
            .redirectErrorStream(true).start()
        val output = process.inputStream.bufferedReader().readText()
        assertEquals(output, 0, process.waitFor())
        assertTrue(output, output.contains("Ran 3 tests"))
    }
}
