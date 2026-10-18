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
        assertEquals(19, entries.size)
        val bindings = entries.flatMap { it["bindings"]!!.jsonArray }.map { it.jsonObject }
        assertEquals(288, bindings.size)
        assertEquals(22, bindings.count { it.text("kind") == "ITEM_ID" })
        assertEquals(2, bindings.count { it.text("purpose") == "EXISTING_RESEARCH_CUE_COMPARISON_PENDING_SOURCE_REVIEW" })
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
}
