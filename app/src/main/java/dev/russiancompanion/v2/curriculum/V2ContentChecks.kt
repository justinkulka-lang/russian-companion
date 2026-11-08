package dev.russiancompanion.v2.curriculum

import kotlinx.serialization.Serializable
import java.security.MessageDigest

/** Reviewed constraints travel with the curriculum; this evaluator contains no unit-specific text. */
@Serializable
internal data class V2ContentCheck(
    val id: String,
    val sources: List<V2ContentSourceCheck>,
    val objectives: List<V2ContentObjectiveCheck>,
    val items: List<V2ContentItemCheck>,
    val rights: List<V2ContentRightsCheck> = emptyList(),
)

@Serializable
internal data class V2ContentRightsCheck(val sourceId: String, val license: String)

@Serializable
internal data class V2ContentSourceCheck(
    val id: String,
    val unitNumber: Int,
    val objectiveId: String,
    val sourceId: String,
    val sourceRevision: String,
    val sourceUrl: String,
    val sourceRecordId: String,
    val sourceHash: String,
    val shippedText: String,
    val transformation: String,
    val licenseId: String,
    val textMatch: String = "WIKI_LABELS",
    val attributionContains: List<String> = emptyList(),
    val requireNoAudio: Boolean = false,
)

@Serializable
internal data class V2ContentObjectiveCheck(
    val id: String,
    val unitNumber: Int,
    val kind: String,
    val gating: Boolean,
    val checkItemIds: List<String>,
    val practiceStepIds: List<String>? = null,
    val guidedItemIds: List<String>? = null,
    val requiredGuidedItemIds: List<String> = emptyList(),
)

@Serializable
internal data class V2ContentItemCheck(
    val id: String,
    val objectiveId: String? = null,
    val independent: Boolean? = null,
    val testedFeature: String? = null,
    val promptSourceId: String? = null,
    val differentPromptItemId: String? = null,
    val requireContext: Boolean = true,
    val requireStructure: Boolean = true,
    val kind: String? = null,
    val noAssessment: Boolean = false,
    val prompt: String? = null,
    val answer: String? = null,
    val options: List<String>? = null,
    val differentPromptItemIds: List<String> = emptyList(),
)

internal object V2ContentChecks {
    fun matches(pack: V2CurriculumPack, id: String, objectiveOverride: V2Objective? = null): Boolean {
        val check = pack.contentChecks.singleOrNull { it.id == id } ?: return false
        if (check.sources.isEmpty() || check.objectives.isEmpty() || check.items.isEmpty()) return false
        if (!sourcesMatch(pack, id)) return false
        if (!check.rights.all { pin ->
            pack.sources.any { it.id == pin.sourceId && it.license == pin.license } &&
                pack.rights.any { it.sourceId == pin.sourceId && it.license == pin.license && it.permitsAppUse }
        }) return false
        if (!check.objectives.all { pin ->
            (objectiveOverride?.takeIf { it.id == pin.id } ?: pack.unit(pin.unitNumber)?.objectives
                ?.singleOrNull { it.id == pin.id })?.let { o ->
                o.kind == pin.kind && o.gating == pin.gating && o.checkItemIds == pin.checkItemIds &&
                    (pin.practiceStepIds == null || o.practiceStepIds == pin.practiceStepIds) &&
                    (pin.guidedItemIds == null || o.practiceStepIds.flatMap {
                        pack.step(it)?.second?.itemIds.orEmpty()
                    } == pin.guidedItemIds) && pin.requiredGuidedItemIds.all { id ->
                        id in o.practiceStepIds.flatMap { pack.step(it)?.second?.itemIds.orEmpty() }
                    }
            } == true
        }) return false
        return check.items.all { pin ->
            pack.item(pin.id)?.let { item ->
                (pin.objectiveId == null || item.objectiveId == pin.objectiveId) &&
                    (pin.independent == null || item.assessment?.independent == pin.independent) &&
                    (pin.testedFeature == null || item.assessment?.testedFeature == pin.testedFeature) &&
                    (pin.promptSourceId == null || check.sources.singleOrNull { it.id == pin.promptSourceId }
                        ?.let { item.promptLetter == it.shippedText } == true) &&
                    (pin.differentPromptItemId == null || item.promptLetter != pack.item(pin.differentPromptItemId)?.promptLetter) &&
                    (!pin.requireContext || CompleteRuleContextPins.itemMatches(pack, item)) &&
                    (!pin.requireStructure || V2Validator.structureValid(pack, item).isEmpty()) &&
                    (pin.kind == null || item.kind == pin.kind) &&
                    (!pin.noAssessment || item.assessment == null) &&
                    (pin.prompt == null || item.promptLetter == pin.prompt) &&
                    (pin.answer == null || item.answer == pin.answer) &&
                    (pin.options == null || item.options == pin.options) &&
                    pin.differentPromptItemIds.all { item.promptLetter != pack.item(it)?.promptLetter }
            } == true
        }
    }

    fun sourcesMatch(pack: V2CurriculumPack, id: String): Boolean {
        val check = pack.contentChecks.singleOrNull { it.id == id } ?: return false
        return check.sources.isNotEmpty() && check.sources.all { pin ->
            pack.provenanceRecords.singleOrNull { it.itemId == pin.id }?.let { sourceMatches(it, pin) } == true
        }
    }

    fun checkItemIds(pack: V2CurriculumPack, id: String): List<String>? =
        pack.contentChecks.singleOrNull { it.id == id }?.objectives?.singleOrNull()?.checkItemIds

    fun requiredGuidedItemIds(pack: V2CurriculumPack, id: String): List<String>? =
        pack.contentChecks.singleOrNull { it.id == id }?.objectives?.singleOrNull()?.requiredGuidedItemIds

    fun testedFeature(pack: V2CurriculumPack, id: String, itemId: String): String? =
        pack.contentChecks.singleOrNull { it.id == id }?.items?.singleOrNull { it.id == itemId }?.testedFeature

    fun admittedNative(pack: V2CurriculumPack, id: String, record: V2ProvenanceRecord): Boolean =
        pack.contentChecks.singleOrNull { it.id == id }?.sources?.singleOrNull { it.id == record.itemId }
            ?.let { sourceMatches(record, it) } == true

    private fun sourceMatches(r: V2ProvenanceRecord, pin: V2ContentSourceCheck): Boolean =
        r.unitNumber == pin.unitNumber && r.objectiveId == pin.objectiveId &&
            r.sourceId == pin.sourceId && r.sourceRevision == pin.sourceRevision &&
            r.sourceUrl == pin.sourceUrl && r.sourceRecordId == pin.sourceRecordId &&
            r.sourceHash == pin.sourceHash && sha(r.originalSourceText) == pin.sourceHash &&
            r.shippedText == pin.shippedText && r.contentHash == sha(pin.shippedText) &&
            (when (pin.textMatch) {
                "WIKI_LABELS" -> pin.shippedText in V2Validator.unfoldWikiLabels(r.originalSourceText)
                "CONTAINS" -> pin.shippedText in r.originalSourceText
                else -> false
            }) && r.transformation == pin.transformation && r.licenseId == pin.licenseId &&
            pin.attributionContains.all { it in r.attribution } && (!pin.requireNoAudio || r.audioAssetId == null)

    private fun sha(text: String): String = MessageDigest.getInstance("SHA-256")
        .digest(text.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it.toInt() and 255) }
}
