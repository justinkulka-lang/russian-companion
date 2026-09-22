package dev.russiancompanion.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "learner_state",
    foreignKeys = [
        ForeignKey(
            entity = CourseSegmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["activeSegmentId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("activeSegmentId")],
)
internal data class LearnerStateEntity(
    @PrimaryKey val id: String,
    val activeSegmentId: String?,
    val onboardingCompleted: Boolean,
    val planContext: String?,
) {
    companion object {
        const val LOCAL_ID = "local"

        fun newUser(): LearnerStateEntity {
            return LearnerStateEntity(
                id = LOCAL_ID,
                activeSegmentId = null,
                onboardingCompleted = false,
                planContext = null,
            )
        }
    }
}

@Entity(
    tableName = "plan_instances",
    foreignKeys = [
        ForeignKey(
            entity = CourseSegmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["segmentId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = PlanDefinitionEntity::class,
            parentColumns = ["id"],
            childColumns = ["planDefinitionId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(value = ["localDate", "planDefinitionId"], unique = true),
        Index("segmentId"),
        Index("planDefinitionId"),
    ],
)
internal data class PlanInstanceEntity(
    @PrimaryKey val id: String,
    val localDate: String,
    val segmentId: String,
    val planDefinitionId: String,
    val completionState: CompletionState,
    val completedAtLocal: String?,
)

@Entity(
    tableName = "activity_logs",
    foreignKeys = [
        ForeignKey(
            entity = CourseSegmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["segmentId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = PlanInstanceEntity::class,
            parentColumns = ["id"],
            childColumns = ["planInstanceId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("segmentId"), Index("planInstanceId"), Index("category")],
)
internal data class ActivityLogEntity(
    @PrimaryKey val id: String,
    val category: ActivityCategory,
    val durationMinutes: Int,
    val completed: Boolean,
    val recordedAtLocal: String,
    val segmentId: String?,
    val planInstanceId: String?,
    val selfAttested: Boolean,
)

@Entity(
    tableName = "recording_records",
    foreignKeys = [
        ForeignKey(
            entity = AudioModelEntity::class,
            parentColumns = ["id"],
            childColumns = ["audioModelId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = ActivityLogEntity::class,
            parentColumns = ["id"],
            childColumns = ["activityLogId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("audioModelId"), Index("activityLogId")],
)
internal data class RecordingRecordEntity(
    @PrimaryKey val id: String,
    val audioModelId: String,
    val localFileReference: String,
    val createdAtLocal: String,
    val markedForTutorReview: Boolean,
    val completionState: CompletionState,
    val activityLogId: String? = null,
)

@Entity(
    tableName = "gate_evidence",
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
internal data class GateEvidenceEntity(
    @PrimaryKey val id: String,
    val gateDefinitionId: String,
    val evidenceType: EvidenceType,
    val enteredValue: String,
    val selfAttestationStatus: SelfAttestationStatus,
    val recordedAtLocal: String,
)

/**
 * User-added tutor sign-off condition. Stored apart from the bundled gate
 * so seeding cannot clear it.
 */
@Entity(
    tableName = "gate_tutor_requirements",
    foreignKeys = [
        ForeignKey(
            entity = GateDefinitionEntity::class,
            parentColumns = ["id"],
            childColumns = ["gateDefinitionId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
)
internal data class GateTutorRequirementEntity(
    @PrimaryKey val gateDefinitionId: String,
    val recordedAtLocal: String,
)

@Entity(
    tableName = "gate_decisions",
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
internal data class GateDecisionEntity(
    @PrimaryKey val id: String,
    val gateDefinitionId: String,
    val ruleVersion: String,
    val ruleSnapshot: String,
    val evidenceIds: String,
    val conditionOutcomes: String,
    val status: GateDecisionStatus,
    val evaluatedAtLocal: String,
)

@Entity(tableName = "export_records")
internal data class ExportRecordEntity(
    @PrimaryKey val id: String,
    val exportedAtLocal: String,
    val destinationReference: String?,
    val logCount: Int,
    val evidenceCount: Int,
    val progressCount: Int,
    val recordingCount: Int,
    val status: ExportStatus,
    val documentReference: String? = null,
)
