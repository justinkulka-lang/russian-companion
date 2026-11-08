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
        assertEquals(14, data.size)
        assertEquals(emptyList<String>(), File(root, "app/src/main").walkTopDown()
            .filter { it.isFile && it.name.matches(Regex("Unit.*Pins\\.kt")) }
            .map { it.relativeTo(root).path }.toList())
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
                    r.copy(transformation = "changed"), r.copy(licenseId = "changed")) +
                    (if (bound.requireNoAudio) listOf(r.copy(audioAssetId = "changed")) else emptyList()) +
                    (if (bound.attributionContains.isNotEmpty()) listOf(r.copy(attribution = "changed")) else emptyList())
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
                if (bound.practiceStepIds != null || bound.guidedItemIds != null || bound.requiredGuidedItemIds.isNotEmpty())
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
            if (bound.prompt != null) bad += item.copy(promptLetter = "changed")
            if (bound.answer != null) bad += item.copy(answer = "changed")
            if (bound.options != null) bad += item.copy(options = listOf("changed"))
            for (id in bound.differentPromptItemIds) bad += item.copy(promptLetter = p.item(id)!!.promptLetter)
            if (bound.kind != null) bad += item.copy(kind = "changed")
            if (bound.noAssessment) bad += item.copy(assessment = p.items.first { it.assessment != null }.assessment)
            for (changed in bad) assertFalse("${rule.id} ${bound.id}", V2ContentChecks.matches(
                p.copy(items = p.items.map { if (it.id == bound.id) changed else it }), rule.id))
        }
    }

    @Test fun declaredRightsAndStandaloneNativeAdmissionRemainRequired() {
        val p = pack()
        for (rule in p.contentChecks) for (right in rule.rights) {
            assertFalse(V2ContentChecks.matches(p.copy(sources = p.sources.filter { it.id != right.sourceId }), rule.id))
            assertFalse(V2ContentChecks.matches(p.copy(sources = p.sources.map {
                if (it.id == right.sourceId) it.copy(license = "changed") else it
            }), rule.id))
            assertFalse(V2ContentChecks.matches(p.copy(rights = p.rights.filter { it.sourceId != right.sourceId }), rule.id))
            assertFalse(V2ContentChecks.matches(p.copy(rights = p.rights.map {
                if (it.sourceId == right.sourceId) it.copy(license = "changed") else it
            }), rule.id))
            assertFalse(V2ContentChecks.matches(p.copy(rights = p.rights.map {
                if (it.sourceId == right.sourceId) it.copy(permitsAppUse = false) else it
            }), rule.id))
            for (source in rule.sources) {
                val r = p.provenanceRecords.single { it.itemId == source.id }
                assertTrue(V2ContentChecks.admittedNative(p, rule.id, r))
                assertFalse(V2ContentChecks.admittedNative(p, rule.id, r.copy(itemId = "changed")))
                if (source.attributionContains.isNotEmpty())
                    assertFalse(V2ContentChecks.admittedNative(p, rule.id, r.copy(attribution = "changed")))
                if (source.requireNoAudio)
                    assertFalse(V2ContentChecks.admittedNative(p, rule.id, r.copy(audioAssetId = "changed")))
            }
        }
    }

    @Test fun sourceTextMatchingModesRemainDistinctAndUnknownModesFailClosed() {
        val p = pack()
        val rule = p.contentChecks.first()
        val source = rule.sources.first()
        val text = "[[target|reviewed]]"
        fun sha(value: String) = java.security.MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray()).joinToString("") { "%02x".format(it.toInt() and 255) }
        val r = p.provenanceRecords.single { it.itemId == source.id }.copy(
            originalSourceText = text, sourceHash = sha(text), shippedText = text, contentHash = sha(text))
        fun admitted(mode: String): Boolean {
            val changed = rule.copy(sources = listOf(source.copy(sourceHash = sha(text), shippedText = text, textMatch = mode)))
            return V2ContentChecks.admittedNative(p.copy(contentChecks = listOf(changed)), rule.id, r)
        }
        assertTrue(admitted("CONTAINS"))
        assertFalse(admitted("WIKI_LABELS"))
        assertFalse(admitted("unknown"))
    }

    @Test fun requiredGuidedMembershipDoesNotBecomeAnExactListConstraint() {
        val p = pack()
        val rule = p.contentChecks.single { it.id == "obj-u20-motion" }
        val bound = rule.objectives.single()
        assertNull(bound.guidedItemIds)
        assertEquals(2, bound.requiredGuidedItemIds.size)
        val unit = p.unit(bound.unitNumber)!!
        val objective = unit.objectives.single { it.id == bound.id }
        val withExtraTraversal = objective.copy(practiceStepIds = objective.practiceStepIds + objective.practiceStepIds.first())
        val changed = p.copy(units = p.units.map { u ->
            if (u.number != unit.number) u else u.copy(objectives = u.objectives.map {
                if (it.id == objective.id) withExtraTraversal else it
            })
        })
        assertTrue(V2ContentChecks.matches(changed, rule.id))
    }

    @Test fun adjectiveSourceMatchingStaysSeparateFromItsItemContract() {
        val p = pack()
        val id = "obj-u29-short-adjective"
        val rule = p.contentChecks.single { it.id == id }
        val itemId = rule.items.first().id
        val changed = p.copy(items = p.items.map {
            if (it.id == itemId) it.copy(assessment = it.assessment!!.copy(testedFeature = "changed")) else it
        })
        assertTrue(V2ContentChecks.sourcesMatch(changed, id))
        assertFalse(V2ContentChecks.matches(changed, id))
        assertEquals(rule.objectives.single().checkItemIds, V2ContentChecks.checkItemIds(p, id))
        assertEquals(rule.objectives.single().requiredGuidedItemIds, V2ContentChecks.requiredGuidedItemIds(p, id))
        assertEquals(rule.items.first().testedFeature, V2ContentChecks.testedFeature(p, id, itemId))
        val missing = p.copy(contentChecks = p.contentChecks - rule)
        assertFalse(V2ContentChecks.sourcesMatch(missing, id))
        assertNull(V2ContentChecks.checkItemIds(missing, id))
        assertNull(V2ContentChecks.requiredGuidedItemIds(missing, id))
        assertNull(V2ContentChecks.testedFeature(missing, id, itemId))
    }

    @Test fun languageObjectivesValidateIndependentlyAndUseTheSuppliedObjective() {
        val p = pack()
        val letters = "obj-u30-letter-names"
        val abbreviations = "obj-u30-abbrev-examples"
        val source = p.contentChecks.single { it.id == letters }.sources.first().id
        val changed = p.copy(provenanceRecords = p.provenanceRecords.map {
            if (it.itemId == source) it.copy(sourceHash = "changed") else it
        })
        assertFalse(V2ContentChecks.matches(changed, letters))
        assertTrue(V2ContentChecks.matches(changed, abbreviations))
        val objective = p.unit(30)!!.objectives.single { it.id == letters }
        assertTrue(V2ContentChecks.matches(p, letters, objective))
        assertFalse(V2ContentChecks.matches(p, letters, objective.copy(checkItemIds = listOf("changed"))))
    }

    @Test fun punctuationStandaloneAdmissionDoesNotReplaceTheGlobalLicenseRoute() {
        val p = pack()
        val id = "obj-u29-punctuation"
        val rule = p.contentChecks.single { it.id == id }
        for (right in rule.rights) {
            val source = rule.sources.first { it.sourceId == right.sourceId }
            val r = p.provenanceRecords.single { it.itemId == source.id }
            for (changed in listOf(r.copy(audioAssetId = "changed"), r.copy(attribution = "changed"))) {
                assertTrue(V2ContentChecks.admittedNative(p, id, changed))
                val validation = V2Validator.validate(p.copy(provenanceRecords = p.provenanceRecords.map {
                    if (it.itemId == changed.itemId) changed else it
                }))
                assertFalse(validation.ok)
                assertTrue(validation.errors.joinToString("\n"), validation.errors.any {
                    it.contains(changed.itemId) && it.contains("Russian Wikipedia license route is missing")
                })
            }
        }
    }

}
