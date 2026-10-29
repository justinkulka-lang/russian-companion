package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.security.MessageDigest

/** Exact engineering source/conservation proofs,not linguistic/rights/owner approval. */
class Unit11BindingConservationTest {
    private val root = File(if (File("app").isDirectory) "." else "..")
    private val pack by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()) }

    @Test fun currentDeclarationReconstructsOnlyUnit11ThenEveryGenuineEarlierPack() {
        val before11 = RecordedCurriculumConservationScope.beforeUnit11NounReview(root, pack)
        assertEquals(RecordedIntakeCorrectionScope.load(root, "745234c99e3d69fadca835ba7f8eb98ad1a8ab48"), before11)
        assertEquals(Unit11CumulativeReviewPolicy.definition.id, before11.unit(11)!!.advancement!!.cumulativeReviewPolicyId)
        // This original equality describes the Unit11-only transition before Unit12 v2.
        RecordedCurriculumConservationScope.beforeUnit12GenderReview(root, pack).let { pack ->
            assertEquals(pack.units.filter { it.number != 11 }, before11.units.filter { it.number != 11 })
        }
        assertEquals(pack.items, before11.items); assertEquals(pack.provenanceRecords, before11.provenanceRecords)
        assertNotEquals(V2Validator.unitContentReviewDigest(before11, 11), V2Validator.unitContentReviewDigest(pack, 11))
        val before10 = RecordedCurriculumConservationScope.beforeUnit10PresentReview(root, pack)
        assertEquals(RecordedIntakeCorrectionScope.load(root, "c14379625f0189849614c4fd1241ba285ecb313a"), before10)
        assertEquals(Unit10CumulativeReviewPolicy.definition.id, before10.unit(10)!!.advancement!!.cumulativeReviewPolicyId)
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
        assertTrue(runCatching { RecordedCurriculumConservationScope.beforeUnit11NounReview(root, changed) }.exceptionOrNull() is AssertionError)
    }

    @Test fun kernelReferenceChangesOnlyItsExactHashAndAppendsPendingEngineeringRecord() {
        val path = "app/src/test/resources/reviewed-curriculum-cyrillic.json"
        val process = ProcessBuilder("git", "show", "fe7968e8c690909363c753ec546cd9fe329334f9:$path").directory(root).start()
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
        assertEquals("c2d297e79fa677aa1375917cfbe052e4c0806633", last.getValue("sourceCommit").jsonPrimitive.content)
        assertTrue(last.getValue("allExistingBindingsUnchanged").jsonPrimitive.boolean)
        assertFalse(last.getValue("semanticAcceptance").jsonPrimitive.boolean)
        assertFalse(last.getValue("rightsAcceptance").jsonPrimitive.boolean)
    }

    // Genuine matching Unit11 reference/kernel pair before the Unit12 transition.
    // Unit12BindingConservationTest separately guards the actual-current pair and append.
    private fun historicalReferenceBytes(path: String): ByteArray {
        val process = ProcessBuilder("git", "show", "05407a70d1d70b196ef21b25cff51357c072c087:$path")
            .directory(root).start()
        val bytes = process.inputStream.readBytes()
        assertEquals("Historical Unit11 reference input $path", 0, process.waitFor())
        return bytes
    }
}
