package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.security.MessageDigest

/** Exact engineering source/conservation proofs,not linguistic/rights/owner approval. */
class Unit10BindingConservationTest {
    private val root = File(if (File("app").isDirectory) "." else "..")
    private val pack by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()) }

    @Test fun currentDeclarationReconstructsOnlyUnit10ThenEveryGenuineEarlierPack() {
        val before10 = RecordedCurriculumConservationScope.beforeUnit10PresentReview(root, pack)
        assertEquals(RecordedIntakeCorrectionScope.load(root, "c14379625f0189849614c4fd1241ba285ecb313a"), before10)
        assertEquals(Unit10CumulativeReviewPolicy.definition.id, before10.unit(10)!!.advancement!!.cumulativeReviewPolicyId)
        // This original equality describes the Unit10-only transition before Unit11 v2.
        RecordedCurriculumConservationScope.beforeUnit11NounReview(root, pack).let { pack ->
            assertEquals(pack.units.filter { it.number != 10 }, before10.units.filter { it.number != 10 })
        }
        assertEquals(pack.items, before10.items); assertEquals(pack.provenanceRecords, before10.provenanceRecords)
        RecordedIntakeCorrectionScope.assertCurrentUnitMatches(before10,
            RecordedIntakeCorrectionScope.load(root, RecordedCurriculumConservationScope.beforeUnit10TeachingImplementation), 10)
        assertNotEquals(V2Validator.unitContentReviewDigest(before10, 10), V2Validator.unitContentReviewDigest(pack, 10))
        val before9 = RecordedCurriculumConservationScope.beforeUnit9NounBehaviorReview(root, pack)
        assertEquals(RecordedIntakeCorrectionScope.load(root, "7e2296f067da861cbebdc9cf63bfd8e0ddd62436"), before9)
        assertEquals(Unit9WiderCumulativeReviewPolicy.definition.id, before9.unit(9)!!.advancement!!.cumulativeReviewPolicyId)
        assertEquals(RecordedIntakeCorrectionScope.load(root, "6fd0406da6dd687196eea76ff56ad7cadc06087d"),
            RecordedCurriculumConservationScope.beforeUnit8PresentReview(root, pack))
        assertEquals(Unit7CumulativeReviewPolicy.definition.id,
            RecordedCurriculumConservationScope.beforeUnit7GenderReview(root, pack).unit(7)!!.advancement!!.cumulativeReviewPolicyId)
        assertEquals(Unit6WiderCumulativeReviewPolicy.definition.id,
            RecordedCurriculumConservationScope.beforeUnit6PresentReview(root, pack).unit(6)!!.advancement!!.cumulativeReviewPolicyId)
        assertEquals(Unit5CumulativeReviewPolicy.definition.id,
            RecordedCurriculumConservationScope.beforeUnit5GrammarReview(root, pack).unit(5)!!.advancement!!.cumulativeReviewPolicyId)
        val changed = pack.copy(units = pack.units.map { u -> if (u.number != 11) u else u.copy(
            advancement = u.advancement!!.copy(minimumDistinctChecks = u.advancement.minimumDistinctChecks + 1)) })
        assertTrue(runCatching { RecordedCurriculumConservationScope.beforeUnit10PresentReview(root, changed) }.exceptionOrNull() is AssertionError)
    }

    @Test fun kernelReferenceChangesOnlyItsExactHashAndAppendsPendingEngineeringRecord() {
        val path = "app/src/test/resources/reviewed-curriculum-cyrillic.json"
        val process = ProcessBuilder("git", "show", "245db4a15b3f126a8e66ebd0539f63790b4021d0:$path").directory(root).start()
        val old = Json.parseToJsonElement(process.inputStream.bufferedReader().readText()).jsonObject
        assertEquals(0, process.waitFor())
        val now = Json.parseToJsonElement(historicalReferenceBytes(path).decodeToString()).jsonObject
        assertFalse(now.getValue("semanticAcceptance").jsonPrimitive.boolean)
        assertFalse(now.getValue("rightsAcceptance").jsonPrimitive.boolean)
        val before = old.getValue("files").jsonArray.map { it.jsonObject }.associateBy { it.getValue("path").jsonPrimitive.content }
        val after = now.getValue("files").jsonArray.map { it.jsonObject }.associateBy { it.getValue("path").jsonPrimitive.content }
        assertEquals(before.keys, after.keys)
        val kernel = "src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt"
        for ((name, entry) in before) {
            if (name != kernel) assertEquals(entry, after.getValue(name))
            else {
                assertEquals(entry.filterKeys { it != "sha256" }, after.getValue(name).filterKeys { it != "sha256" })
                assertNotEquals(entry.getValue("sha256"), after.getValue(name).getValue("sha256"))
                val actual = MessageDigest.getInstance("SHA-256").digest(historicalReferenceBytes("app/$name")).joinToString("") { "%02x".format(it) }
                assertEquals(actual, after.getValue(name).getValue("sha256").jsonPrimitive.content)
            }
        }
        assertEquals(19, after.size)
        assertEquals(292, after.values.sumOf { it.getValue("bindings").jsonArray.size })
        val prior = old.getValue("subsequentEngineeringReferenceReviews").jsonArray
        val current = now.getValue("subsequentEngineeringReferenceReviews").jsonArray
        assertEquals(prior.toList(), current.take(prior.size))
        assertEquals(prior.size + 1, current.size)
        val last = current.last().jsonObject
        assertEquals("85e7eaf21335a107d64b99a4f1969bab4f74670d", last.getValue("sourceCommit").jsonPrimitive.content)
        assertTrue(last.getValue("allExistingBindingsUnchanged").jsonPrimitive.boolean)
        assertFalse(last.getValue("semanticAcceptance").jsonPrimitive.boolean)
        assertFalse(last.getValue("rightsAcceptance").jsonPrimitive.boolean)
    }
    // Genuine matching Unit10 reference/kernel pair after its verified broad checkpoint.
    // Actual-current hash/append is independently checked by Unit11BindingConservationTest.
    private fun historicalReferenceBytes(path: String): ByteArray {
        val process = ProcessBuilder("git", "show", "1d96cb4e89768f69246960e263d9b51412a3b9f4:$path")
            .directory(root).start()
        val bytes = process.inputStream.readBytes()
        assertEquals("Historical Unit10 reference input $path", 0, process.waitFor())
        return bytes
    }

}
