package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.security.MessageDigest

/** Exact engineering source/conservation proofs,not linguistic/rights/owner approval. */
class Unit13BindingConservationTest {
    private val root = File(if (File("app").isDirectory) "." else "..")
    private val pack by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()) }

    @Test fun currentDeclarationReconstructsOnlyUnit13ThenEveryGenuineEarlierPack() {
        val before13 = RecordedCurriculumConservationScope.beforeUnit13PresentReview(root, pack)
        assertEquals(RecordedIntakeCorrectionScope.load(root, "31ac807387253082c44bd2f6d67021478f0df66e"), before13)
        assertEquals(Unit13WiderCumulativeReviewPolicy.definition.id, before13.unit(13)!!.advancement!!.cumulativeReviewPolicyId)
        // Original Unit13 contract predates only the proven Unit14 declaration change.
        RecordedCurriculumConservationScope.beforeUnit14PluralPossessionReview(root, pack).let { pack ->
            assertEquals(pack.units.filter { it.number != 13 }, before13.units.filter { it.number != 13 })
        }
        RecordedCurriculumConservationScope.assertCurrent(root, pack)
        assertEquals(RecordedLiteracyEpochScope.beforeCorrection(root, pack).items, before13.items)
        assertEquals(pack.items.map { it.id to it.answer }, before13.items.map { it.id to it.answer })
        assertEquals(pack.provenanceRecords, before13.provenanceRecords)
        assertNotEquals(V2Validator.unitContentReviewDigest(before13, 13), V2Validator.unitContentReviewDigest(pack, 13))
        assertEquals(RecordedIntakeCorrectionScope.load(root, "23b9179eaf54844f0b38074b2db56c7113c38c00"),
            RecordedCurriculumConservationScope.beforeUnit12GenderReview(root, pack))
        val before11 = RecordedCurriculumConservationScope.beforeUnit11NounReview(root, pack)
        assertEquals(RecordedIntakeCorrectionScope.load(root, "745234c99e3d69fadca835ba7f8eb98ad1a8ab48"), before11)
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
        val changed = pack.copy(units = pack.units.map { u -> if (u.number != 13) u else u.copy(
            advancement = u.advancement!!.copy(minimumDistinctChecks = u.advancement.minimumDistinctChecks + 1)) })
        assertTrue(runCatching { RecordedCurriculumConservationScope.beforeUnit12GenderReview(root, changed) }.exceptionOrNull() is AssertionError)
    }

    @Test fun kernelReferenceChangesOnlyItsExactHashAndAppendsPendingEngineeringRecord() {
        val path = "app/src/test/resources/reviewed-curriculum-cyrillic.json"
        val process = ProcessBuilder("git", "show", "aa93df85b4290406d5b78b0035b4a77912ae25fe:$path").directory(root).start()
        val old = Json.parseToJsonElement(process.inputStream.bufferedReader().readText()).jsonObject
        assertEquals(0, process.waitFor())
        // Keep the exact Unit13 append/count/source assertions on its genuine matching pair.
        val referenceCommit = "9101af8c182b3415426ed9226cb211a707a2c393"
        val now = Json.parseToJsonElement(sourceBytes(referenceCommit, path).toString(Charsets.UTF_8)).jsonObject
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
                val actual = MessageDigest.getInstance("SHA-256").digest(sourceBytes(referenceCommit, "app/$name")).joinToString("") { "%02x".format(it) }
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
        assertEquals("bd00e5082c9f31d2ae3b379dd6204dd3b28e4475", last.getValue("sourceCommit").jsonPrimitive.content)
        assertTrue(last.getValue("allExistingBindingsUnchanged").jsonPrimitive.boolean)
        assertFalse(last.getValue("semanticAcceptance").jsonPrimitive.boolean)
        assertFalse(last.getValue("rightsAcceptance").jsonPrimitive.boolean)
        // Current kernel identity remains separately checked, alongside unchanged Unit14 binding proofs.
        val live = Json.parseToJsonElement(File(root, path).readText()).jsonObject
        val liveEntry = live.getValue("files").jsonArray.map { it.jsonObject }.single {
            it.getValue("path").jsonPrimitive.content == kernel
        }
        val liveHash = MessageDigest.getInstance("SHA-256").digest(File(root, "app/$kernel").readBytes()).joinToString("") { "%02x".format(it) }
        assertEquals(liveHash, liveEntry.getValue("sha256").jsonPrimitive.content)
        assertFalse(live.getValue("semanticAcceptance").jsonPrimitive.boolean)
        assertFalse(live.getValue("rightsAcceptance").jsonPrimitive.boolean)
    }

    private fun sourceBytes(commit: String, path: String): ByteArray {
        val process = ProcessBuilder("git", "show", "$commit:$path").directory(root).start()
        val bytes = process.inputStream.readBytes()
        assertEquals(0, process.waitFor())
        return bytes
    }
}
