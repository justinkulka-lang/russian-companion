package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class ContentChecksTest {
    private val root = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
    private fun pack() = V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText())

    @Test fun generatorDataAndPackChecksAgreeAndMissingOrDuplicateChecksFailClosed() {
        val p = pack()
        val data = Json.decodeFromString(ListSerializer(V2ContentCheck.serializer()),
            File(root, "catalog/content-checks.json").readText())
        assertEquals(data, p.contentChecks)
        assertEquals(5, data.size)
        assertNotEquals(V2Validator.contentReviewDigest(p),
            V2Validator.contentReviewDigest(p.copy(contentChecks = emptyList())))
        for (rule in data) {
            assertTrue(rule.id, V2ContentChecks.matches(p, rule.id))
            assertFalse(V2ContentChecks.matches(p.copy(contentChecks = data - rule), rule.id))
            assertFalse(V2ContentChecks.matches(p.copy(contentChecks = data + rule), rule.id))
        }
    }

    @Test fun migratedChecksRejectChangesToEveryBoundSourceAndObjective() {
        val p = pack()
        for (rule in p.contentChecks) {
            for (bound in rule.sources) {
                val r = p.provenanceRecords.single { it.itemId == bound.id }
                val bad = listOf(r.copy(unitNumber = r.unitNumber + 1), r.copy(objectiveId = "changed"),
                    r.copy(sourceId = "changed"), r.copy(sourceRevision = "changed"),
                    r.copy(sourceUrl = "https://example.invalid/changed"), r.copy(sourceRecordId = "changed"),
                    r.copy(sourceHash = "0".repeat(64)), r.copy(originalSourceText = "changed"),
                    r.copy(shippedText = "changed"), r.copy(contentHash = "0".repeat(64)),
                    r.copy(transformation = "changed"), r.copy(licenseId = "changed"))
                for ((index, changed) in bad.withIndex()) assertFalse("${rule.id} ${bound.id} field $index",
                    V2ContentChecks.matches(p.copy(provenanceRecords = p.provenanceRecords.map {
                        if (it.itemId == bound.id) changed else it
                    }), rule.id))
                assertFalse(V2ContentChecks.matches(p.copy(provenanceRecords = p.provenanceRecords + r), rule.id))
            }
            for (bound in rule.objectives) {
                val o = p.unit(bound.unitNumber)!!.objectives.single { it.id == bound.id }
                val bad = mutableListOf(o.copy(kind = "changed"), o.copy(gating = !o.gating),
                    o.copy(checkItemIds = o.checkItemIds + "changed"))
                if (bound.practiceStepIds != null || bound.guidedItemIds != null)
                    bad += o.copy(practiceStepIds = listOf("changed"))
                for (changed in bad) assertFalse("${rule.id} ${bound.id}", V2ContentChecks.matches(
                    p.copy(units = p.units.map { u -> u.copy(objectives = u.objectives.map {
                        if (it.id == bound.id) changed else it
                    }) }), rule.id))
            }
        }
    }

    @Test fun migratedChecksRejectChangedItemRoleFeaturePromptAndOptionalReferenceAssessment() {
        val p = pack()
        for (rule in p.contentChecks) for (bound in rule.items) {
            val item = p.item(bound.id)!!
            val bad = mutableListOf(item.copy(id = "missing"))
            if (bound.objectiveId != null) bad += item.copy(objectiveId = "changed")
            if (bound.independent != null) bad += item.copy(assessment = item.assessment!!.copy(independent = !bound.independent))
            if (bound.testedFeature != null) bad += item.copy(assessment = item.assessment!!.copy(testedFeature = "changed"))
            if (bound.promptSourceId != null) bad += item.copy(promptLetter = "changed")
            if (bound.differentPromptItemId != null) bad += item.copy(promptLetter = p.item(bound.differentPromptItemId)!!.promptLetter)
            if (bound.kind != null) bad += item.copy(kind = "changed")
            if (bound.noAssessment) bad += item.copy(assessment = p.items.first { it.assessment != null }.assessment)
            for (changed in bad) assertFalse("${rule.id} ${bound.id}", V2ContentChecks.matches(
                p.copy(items = p.items.map { if (it.id == bound.id) changed else it }), rule.id))
        }
    }
}
