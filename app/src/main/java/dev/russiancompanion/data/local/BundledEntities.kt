package dev.russiancompanion.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "course_segments")
internal data class CourseSegmentEntity(
    @PrimaryKey val id: String,
    val lessonReference: String,
    val lessonNumber: Int,
    val sortOrder: Int,
    val eligibilityStatus: EligibilityStatus,
)

@Entity(
    tableName = "plan_definitions",
    foreignKeys = [
        ForeignKey(
            entity = CourseSegmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["segmentId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("segmentId")],
)
internal data class PlanDefinitionEntity(
    @PrimaryKey val id: String,
    val segmentId: String,
    val activityType: PlanActivityType,
    val instruction: String,
    val sortOrder: Int,
    val required: Boolean,
    val lessonNumber: Int?,
    val audioModelIds: List<String>,
    val eligibilityStatus: EligibilityStatus,
)

@Entity(
    tableName = "gate_definitions",
    foreignKeys = [
        ForeignKey(
            entity = CourseSegmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["segmentId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("segmentId")],
)
internal data class GateDefinitionEntity(
    @PrimaryKey val id: String,
    val segmentId: String,
    val ruleVersion: String,
    val displayedRuleText: String,
    val eligibilityStatus: EligibilityStatus,
)

@Entity(
    tableName = "gate_conditions",
    foreignKeys = [
        ForeignKey(
            entity = GateDefinitionEntity::class,
            parentColumns = ["id"],
            childColumns = ["gateDefinitionId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("gateDefinitionId")],
)
internal data class GateConditionEntity(
    @PrimaryKey val id: String,
    val gateDefinitionId: String,
    val evidenceType: EvidenceType,
    val operator: RuleOperator,
    val operand: String?,
    val sortOrder: Int,
)

@Entity(tableName = "source_records")
internal data class SourceRecordEntity(
    @PrimaryKey val id: String,
    val provenance: String,
    val redistributionBasis: String,
    val licenseName: String,
    val attribution: String,
    val modificationStatus: ModificationStatus,
    val shareAlikeTreatment: String,
    val shareAlikeConfirmation: ShareAlikeConfirmation,
)

@Entity(
    tableName = "source_texts",
    foreignKeys = [
        ForeignKey(
            entity = SourceRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["sourceRecordId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("sourceRecordId")],
)
internal data class SourceTextEntity(
    @PrimaryKey val id: String,
    val stressMarkedText: String,
    val sourceRecordId: String,
    val modificationStatus: ModificationStatus,
    val matchVerified: Boolean,
)

@Entity(
    tableName = "asset_rights_records",
    foreignKeys = [
        ForeignKey(
            entity = SourceRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["sourceRecordId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("sourceRecordId"), Index("assetIdentifier")],
)
internal data class AssetRightsRecordEntity(
    @PrimaryKey val id: String,
    val sourceRecordId: String,
    val assetIdentifier: String,
    val licenseStatus: LicenseStatus,
    val compatibilityDecision: LicenseStatus,
    val attributionRequired: Boolean,
    val inclusionResult: InclusionResult,
)

@Entity(
    tableName = "audio_models",
    foreignKeys = [
        ForeignKey(
            entity = SourceTextEntity::class,
            parentColumns = ["id"],
            childColumns = ["sourceTextId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = SourceRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["sourceRecordId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = AssetRightsRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["rightsRecordId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index("sourceTextId"),
        Index("sourceRecordId"),
        Index("rightsRecordId"),
    ],
)
internal data class AudioModelEntity(
    @PrimaryKey val id: String,
    val localAudioReference: String,
    val sourceTextId: String?,
    val sourceRecordId: String?,
    val rightsRecordId: String?,
    val eligibilityStatus: EligibilityStatus,
)

@Entity(
    tableName = "optional_drill_definitions",
    foreignKeys = [
        ForeignKey(
            entity = SourceRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["sourceRecordId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("sourceRecordId")],
)
internal data class OptionalDrillDefinitionEntity(
    @PrimaryKey val id: String,
    val packageId: String,
    val transformId: String,
    val sourceFieldReferences: String,
    val sourceRecordId: String?,
    val inheritedErrorDisclosure: String,
    val eligibilityStatus: EligibilityStatus,
)

@Entity(tableName = "bundled_seed_state")
internal data class BundledSeedStateEntity(
    @PrimaryKey val id: Int = SINGLETON_ID,
    val contentVersion: Int,
    val contentHash: String = "",
) {
    companion object {
        const val SINGLETON_ID = 1
    }
}
