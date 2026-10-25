package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.security.MessageDigest

/** Tooling proofs, not linguistic, rights, runtime learner or owner acceptance. */
class Unit8BindingConservationTest {
    private val root = File(if (File("app").isDirectory) "." else "..")
    private val pack by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()) }

    @Test fun currentDeclarationReconstructsOnlyUnit8ThenEachGenuineEarlierPack() {
        val before8 = RecordedCurriculumConservationScope.beforeUnit8PresentReview(root, pack)
        assertEquals(RecordedIntakeCorrectionScope.load(root, "6fd0406da6dd687196eea76ff56ad7cadc06087d"), before8)
        assertEquals(Unit8CumulativeReviewPolicy.definition.id, before8.unit(8)!!.advancement!!.cumulativeReviewPolicyId)
        assertEquals(pack.units.filter { it.number != 8 }, before8.units.filter { it.number != 8 })
        assertEquals(pack.items, before8.items)
        assertEquals(pack.provenanceRecords, before8.provenanceRecords)
        RecordedIntakeCorrectionScope.assertCurrentUnitMatches(before8,
            RecordedIntakeCorrectionScope.load(root, RecordedCurriculumConservationScope.beforeUnit8TeachingImplementation), 8)
        assertNotEquals(V2Validator.unitContentReviewDigest(before8, 8), V2Validator.unitContentReviewDigest(pack, 8))
        val before7 = RecordedCurriculumConservationScope.beforeUnit7GenderReview(root, pack)
        assertEquals(Unit7CumulativeReviewPolicy.definition.id, before7.unit(7)!!.advancement!!.cumulativeReviewPolicyId)
        assertEquals(before8.units.filter { it.number != 7 }, before7.units.filter { it.number != 7 })
        assertEquals(Unit6WiderCumulativeReviewPolicy.definition.id,
            RecordedCurriculumConservationScope.beforeUnit6PresentReview(root, pack).unit(6)!!.advancement!!.cumulativeReviewPolicyId)
        assertEquals(Unit5CumulativeReviewPolicy.definition.id,
            RecordedCurriculumConservationScope.beforeUnit5GrammarReview(root, pack).unit(5)!!.advancement!!.cumulativeReviewPolicyId)
        val changed = pack.copy(units = pack.units.map { u -> if (u.number != 9) u else u.copy(
            advancement = u.advancement!!.copy(minimumDistinctChecks = u.advancement.minimumDistinctChecks + 1)) })
        assertTrue(runCatching { RecordedCurriculumConservationScope.beforeUnit8PresentReview(root, changed) }.exceptionOrNull() is AssertionError)
    }

    @Test fun newPolicyReferenceChangesOnlyExactFileHashWithAllBindingsStillPending() {
        val path = "app/src/test/resources/reviewed-curriculum-cyrillic.json"
        val process = ProcessBuilder("git", "show", "fda1fbe89004b92c09e33e425968a17cf8699d3e:$path").directory(root).start()
        val old = Json.parseToJsonElement(process.inputStream.bufferedReader().readText()).jsonObject
        assertEquals(0, process.waitFor())
        val now = Json.parseToJsonElement(File(root, path).readText()).jsonObject
        for (key in listOf("semanticAcceptance", "rightsAcceptance")) assertFalse(now.getValue(key).jsonPrimitive.boolean)
        val before = old.getValue("files").jsonArray.map { it.jsonObject }.associateBy { it.getValue("path").jsonPrimitive.content }
        val after = now.getValue("files").jsonArray.map { it.jsonObject }.associateBy { it.getValue("path").jsonPrimitive.content }
        assertEquals(before.keys, after.keys)
        val changed = "src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt"
        for ((name, entry) in before) {
            if (name != changed) assertEquals(entry, after.getValue(name))
            else {
                assertEquals(entry.filterKeys { it != "sha256" }, after.getValue(name).filterKeys { it != "sha256" })
                assertNotEquals(entry.getValue("sha256"), after.getValue(name).getValue("sha256"))
                val actual = MessageDigest.getInstance("SHA-256").digest(File(root, "app/$name").readBytes()).joinToString("") { "%02x".format(it) }
                assertEquals(actual, after.getValue(name).getValue("sha256").jsonPrimitive.content)
            }
        }
        assertEquals(292, after.values.sumOf { it.getValue("bindings").jsonArray.size })
    }
}
