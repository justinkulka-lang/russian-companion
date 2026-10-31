package dev.russiancompanion.acceptance

import dev.russiancompanion.acceptance.ReviewedCurriculumSourceGuard.text
import dev.russiancompanion.v2.curriculum.V2Validator
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test

class ReviewedCurriculumSourceGuardTest {
    @Test fun exactComparisonFilesKeepAllLiteralBindingsAndPendingResearchStatus() {
        val pack = ReviewedCurriculumSourceGuard.pack()
        val entries = ReviewedCurriculumSourceGuard.entries
        // Preserve the original 288/22 contract on its exact historical reference.
        historicalComparisonEntries().let { entries ->
            assertEquals(19, entries.size)
            val bindings = entries.flatMap { it["bindings"]!!.jsonArray }.map { it.jsonObject }
            assertEquals(288, bindings.size)
            assertEquals(22, bindings.count { it.text("kind") == "ITEM_ID" })
            assertEquals(2, bindings.count { it.text("purpose") == "EXISTING_RESEARCH_CUE_COMPARISON_PENDING_SOURCE_REVIEW" })
        }
        assertEquals(19, entries.size)
        val currentBindings = entries.flatMap { it["bindings"]!!.jsonArray }.map { it.jsonObject }
        assertEquals(292, currentBindings.size)
        assertEquals(26, currentBindings.count { it.text("kind") == "ITEM_ID" })
        assertEquals(emptyList<String>(), ReviewedCurriculumSourceGuard.offenders(ReviewedCurriculumSourceGuard.sources(), pack))
        val result = V2Validator.validate(pack)
        assertTrue(result.errors.joinToString("\n"), result.ok)
    }

    @Test fun newUiResourceAndCurriculumTextCannotBorrowAnExistingLiteral() {
        val sources = ReviewedCurriculumSourceGuard.sources()
        val value = ReviewedCurriculumSourceGuard.entries.first()["bindings"]!!.jsonArray.first().jsonObject.text("value")
        val paths = listOf("src/main/java/dev/russiancompanion/v2/ui/Unreviewed.kt",
            "src/main/java/dev/russiancompanion/v2/curriculum/Unreviewed.kt", "src/main/res/values/unreviewed.xml")
        val changed = sources + paths.associateWith { value }
        assertEquals(paths.sorted(), ReviewedCurriculumSourceGuard.offenders(changed, ReviewedCurriculumSourceGuard.pack()))
        val escaped = sources + (paths.first() to "val text = \"\\u0410\"")
        assertEquals(listOf(paths.first()), ReviewedCurriculumSourceGuard.offenders(escaped, ReviewedCurriculumSourceGuard.pack()))
    }

    @Test fun changedMovedAndMissingComparisonFilesRemainBlocked() {
        val sources = ReviewedCurriculumSourceGuard.sources()
        val original = ReviewedCurriculumSourceGuard.entries.first().text("path")
        val moved = "src/main/java/dev/russiancompanion/v2/ui/CopiedPin.kt"
        val pack = ReviewedCurriculumSourceGuard.pack()
        assertEquals(listOf(original), ReviewedCurriculumSourceGuard.offenders(sources + (original to sources.getValue(original) + "\n// changed"), pack))
        assertEquals(listOf(original, moved).sorted(), ReviewedCurriculumSourceGuard.offenders(sources - original + (moved to sources.getValue(original)), pack))
        assertEquals(listOf(original), ReviewedCurriculumSourceGuard.offenders(sources - original, pack))
    }

    @Test fun coherentSourceForgeryAndRemovedItemIdsCannotKeepUnchangedCodeAdmitted() {
        val sources = ReviewedCurriculumSourceGuard.sources()
        val pack = ReviewedCurriculumSourceGuard.pack()
        val entry = ReviewedCurriculumSourceGuard.entries.first()
        val expected = entry["bindings"]!!.jsonArray.first().jsonObject["record"]!!.jsonObject
        val id = expected.text("itemId")
        val changed = pack.copy(provenanceRecords = pack.provenanceRecords.map { r ->
            if (r.itemId != id) r else r.copy(originalSourceText = r.originalSourceText + "!",
                sourceHash = ReviewedCurriculumSourceGuard.hash(r.originalSourceText + "!"),
                shippedText = r.shippedText + "!", contentHash = ReviewedCurriculumSourceGuard.hash(r.shippedText + "!"))
        })
        assertTrue(entry.text("path") in ReviewedCurriculumSourceGuard.offenders(sources, changed))
        assertTrue(entry.text("path") in ReviewedCurriculumSourceGuard.offenders(sources,
            pack.copy(provenanceRecords = pack.provenanceRecords.filter { it.itemId != id })))
        val policy = ReviewedCurriculumSourceGuard.entries.single { it.text("path").endsWith("/V2CumulativeReview.kt") }
        val itemId = policy["bindings"]!!.jsonArray.first().jsonObject.text("value")
        assertTrue(policy.text("path") in ReviewedCurriculumSourceGuard.offenders(sources,
            pack.copy(items = pack.items.filter { it.id != itemId })))
        val catalog = ReviewedCurriculumSourceGuard.entries.flatMap { it["bindings"]!!.jsonArray }
            .map { it.jsonObject }.first { it.text("kind") == "CATALOG_FRAGMENT" }.text("catalogPath")
        val affected = ReviewedCurriculumSourceGuard.entries.filter { row -> row["bindings"]!!.jsonArray.any {
            it.jsonObject["catalogPath"]?.jsonPrimitive?.content == catalog
        } }.map { it.text("path") }.sorted()
        assertTrue(affected.isNotEmpty())
        assertEquals(affected, ReviewedCurriculumSourceGuard.offenders(sources, pack) { path ->
            java.io.File(ReviewedCurriculumSourceGuard.root(), path).readText() + if (path == catalog) "!" else ""
        })
        assertEquals(affected, ReviewedCurriculumSourceGuard.offenders(sources, pack) { path ->
            if (path == catalog) null else java.io.File(ReviewedCurriculumSourceGuard.root(), path).readText()
        })
    }

    @Test fun presentItemReferencesAddOnlyFourBindingsAndEachMissingItemStaysBlocked() {
        val historical = historicalComparisonEntries().associateBy { it.text("path") }
        val current = ReviewedCurriculumSourceGuard.entries.associateBy { it.text("path") }
        assertEquals(historical.keys, current.keys)
        val path = "src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt"
        historical.filterKeys { it != path }.forEach { (name, entry) -> assertEquals(entry, current.getValue(name)) }
        val prior = historical.getValue(path)
        val reviewed = current.getValue(path)
        assertEquals(prior.filterKeys { it !in setOf("sha256", "bindings") }, reviewed.filterKeys { it !in setOf("sha256", "bindings") })
        assertNotEquals(prior.text("sha256"), reviewed.text("sha256"))
        val priorBindings = prior.getValue("bindings").jsonArray
        val bindings = reviewed.getValue("bindings").jsonArray
        assertEquals(priorBindings.toList(), bindings.take(priorBindings.size))
        val additions = bindings.drop(priorBindings.size).map { it.jsonObject }
        val expected = setOf("u4-i-verb-говорить-мы", "u4-i-verb-видеть-он", "u4-i-verb-писать-вы", "u4-i-verb-идти-они")
        assertEquals(4, additions.size)
        assertEquals(expected, additions.map { it.text("value") }.toSet())
        assertTrue(additions.all { it.text("kind") == "ITEM_ID" && it.text("purpose") == "PERSISTED_ITEM_ID_REFERENCE" })
        val sources = ReviewedCurriculumSourceGuard.sources()
        val text = sources.getValue(path)
        val literalPattern = Regex("\"([^\"\n]*)\"")
        val literals = literalPattern.findAll(text).map { it.groupValues[1] }
            .filter { value -> value.any { it.code in 0x0400..0x04FF } }.toSet()
        assertEquals(bindings.map { it.jsonObject.text("value") }.toSet(), literals)
        val pack = ReviewedCurriculumSourceGuard.pack()
        assertEquals(emptyList<String>(), ReviewedCurriculumSourceGuard.offenders(sources, pack))
        expected.forEach { id ->
            assertTrue(pack.items.any { it.id == id })
            assertTrue("Missing current present-form item must invalidate the exact-file reference: $id",
                path in ReviewedCurriculumSourceGuard.offenders(sources, pack.copy(items = pack.items.filter { it.id != id })))
        }
    }

    private fun historicalComparisonEntries(): List<JsonObject> {
        val process = ProcessBuilder("git", "show",
            "e66dfe0771424daf6e090fb258f77523ec40a4c1:app/src/test/resources/reviewed-curriculum-cyrillic.json")
            .directory(ReviewedCurriculumSourceGuard.root()).start()
        val text = process.inputStream.bufferedReader(Charsets.UTF_8).readText()
        assertEquals(0, process.waitFor())
        assertEquals("502653747a6f558cf2564ea8c9a40142bda05d7c5d28ede7ec1cb7bd086278b8", ReviewedCurriculumSourceGuard.hash(text))
        val document = Json.parseToJsonElement(text).jsonObject
        assertFalse(document.getValue("semanticAcceptance").jsonPrimitive.boolean)
        assertFalse(document.getValue("rightsAcceptance").jsonPrimitive.boolean)
        return document.getValue("files").jsonArray.map { it.jsonObject }
    }
}
