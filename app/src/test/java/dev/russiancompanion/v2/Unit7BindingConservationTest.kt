package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.security.MessageDigest

/** Tooling proofs, not linguistic, rights, runtime learner or owner acceptance. */
class Unit7BindingConservationTest {
    private val root = File(if (File("app").isDirectory) "." else "..")
    private val pack by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()) }

    @Test fun currentDeclarationReconstructsEachGenuinePrecedingWholePackAndNothingElse() {
        val before7 = RecordedCurriculumConservationScope.beforeUnit7GenderReview(root, pack)
        assertEquals(Unit7CumulativeReviewPolicy.definition.id, before7.unit(7)!!.advancement!!.cumulativeReviewPolicyId)
        assertEquals(pack.units.filter { it.number != 7 }, before7.units.filter { it.number != 7 })
        assertEquals(pack.items, before7.items)
        assertEquals(pack.provenanceRecords, before7.provenanceRecords)
        val before6 = RecordedCurriculumConservationScope.beforeUnit6PresentReview(root, pack)
        assertEquals(Unit6WiderCumulativeReviewPolicy.definition.id, before6.unit(6)!!.advancement!!.cumulativeReviewPolicyId)
        val before5 = RecordedCurriculumConservationScope.beforeUnit5GrammarReview(root, pack)
        assertEquals(Unit5CumulativeReviewPolicy.definition.id, before5.unit(5)!!.advancement!!.cumulativeReviewPolicyId)
        val changed = pack.copy(units = pack.units.map { u -> if (u.number != 8) u else u.copy(
            advancement = u.advancement!!.copy(minimumDistinctChecks = u.advancement.minimumDistinctChecks + 1)) })
        val refused = runCatching { RecordedCurriculumConservationScope.beforeUnit7GenderReview(root, changed) }
        assertTrue("Unrelated curriculum change must not be projected away", refused.exceptionOrNull() is AssertionError)
    }

    @Test fun newPolicyReferenceChangesOnlyExactFileHashWithAllBindingsStillPending() {
        val path = "app/src/test/resources/reviewed-curriculum-cyrillic.json"
        val process = ProcessBuilder("git", "show", "ebf25501c07452952fdd5666b24884d590ffa176:$path").directory(root).start()
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
