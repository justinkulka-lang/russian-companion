package dev.russiancompanion.data.local.seed

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

@Serializable
internal data class BundledSeedDocument(
    val contentVersion: Int,
    val courseSegments: List<CourseSegmentSeed>,
    val planDefinitions: List<PlanDefinitionSeed>,
    val gateDefinitions: List<GateDefinitionSeed>,
    val sourceRecords: List<SourceRecordSeed>,
    val sourceTexts: List<SourceTextSeed>,
    val assetRightsRecords: List<AssetRightsSeed>,
    val audioModels: List<AudioModelSeed>,
    val optionalDrills: List<OptionalDrillSeed>,
)

@Serializable
internal data class CourseSegmentSeed(
    val id: String,
    val lessonReference: String,
    val sortOrder: Int,
)

@Serializable
internal data class PlanDefinitionSeed(
    val id: String,
    val segmentId: String,
    val activityType: String,
    val instruction: String,
    val sortOrder: Int,
    val required: Boolean,
)

@Serializable
internal data class GateDefinitionSeed(
    val id: String,
    val segmentId: String,
    val ruleVersion: String,
    val displayedRuleText: String,
    val conditions: List<GateConditionSeed>,
)

@Serializable
internal data class GateConditionSeed(
    val id: String,
    val evidenceType: String,
    val operator: String,
    val operand: String?,
    val sortOrder: Int,
)

@Serializable
internal data class SourceRecordSeed(
    val id: String,
    val provenance: String,
    val redistributionBasis: String,
    val licenseName: String,
    val attribution: String,
    val modificationStatus: String,
    val shareAlikeTreatment: String,
    val shareAlikeConfirmation: String,
)

@Serializable
internal data class SourceTextSeed(
    val id: String,
    val stressMarkedText: String,
    val sourceRecordId: String,
    val modificationStatus: String,
    val matchVerified: Boolean,
)

@Serializable
internal data class AssetRightsSeed(
    val id: String,
    val sourceRecordId: String,
    val assetIdentifier: String,
    val licenseStatus: String,
    val compatibilityDecision: String,
    val attributionRequired: Boolean,
    val inclusionResult: String,
)

@Serializable
internal data class AudioModelSeed(
    val id: String,
    val localAudioReference: String,
    val sourceTextId: String?,
    val sourceRecordId: String?,
    val rightsRecordId: String?,
)

@Serializable
internal data class OptionalDrillSeed(
    val id: String,
    val packageId: String,
    val transformId: String,
    val sourceFieldReferences: String,
    val sourceRecordId: String?,
    val inheritedErrorDisclosure: String,
)

internal sealed interface SeedParse {
    data class Ok(val document: BundledSeedDocument) : SeedParse

    data object Corrupt : SeedParse
}

internal object SeedJson {
    private val json = Json {
        ignoreUnknownKeys = false
        explicitNulls = true
    }

    fun parse(raw: String): SeedParse {
        val document = try {
            json.decodeFromString(BundledSeedDocument.serializer(), raw)
        } catch (_: SerializationException) {
            return SeedParse.Corrupt
        } catch (_: IllegalArgumentException) {
            return SeedParse.Corrupt
        }
        if (document.contentVersion <= 0 || hasDuplicateIds(document)) {
            return SeedParse.Corrupt
        }
        return SeedParse.Ok(document)
    }

    fun encode(document: BundledSeedDocument): String {
        return json.encodeToString(BundledSeedDocument.serializer(), document)
    }

    private fun hasDuplicateIds(document: BundledSeedDocument): Boolean {
        if (duplicates(document.courseSegments.map { it.id })) return true
        if (duplicates(document.planDefinitions.map { it.id })) return true
        if (duplicates(document.gateDefinitions.map { it.id })) return true
        if (duplicates(document.gateDefinitions.flatMap { gate -> gate.conditions.map { it.id } })) return true
        if (duplicates(document.sourceRecords.map { it.id })) return true
        if (duplicates(document.sourceTexts.map { it.id })) return true
        if (duplicates(document.assetRightsRecords.map { it.id })) return true
        if (duplicates(document.audioModels.map { it.id })) return true
        if (duplicates(document.optionalDrills.map { it.id })) return true
        return false
    }

    private fun duplicates(ids: List<String>): Boolean = ids.size != ids.toSet().size
}
