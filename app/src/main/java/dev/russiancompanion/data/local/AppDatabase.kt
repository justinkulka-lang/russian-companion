package dev.russiancompanion.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        CourseSegmentEntity::class,
        PlanDefinitionEntity::class,
        GateDefinitionEntity::class,
        GateConditionEntity::class,
        SourceRecordEntity::class,
        SourceTextEntity::class,
        AssetRightsRecordEntity::class,
        AudioModelEntity::class,
        OptionalDrillDefinitionEntity::class,
        BundledSeedStateEntity::class,
        LearnerStateEntity::class,
        PlanInstanceEntity::class,
        ActivityLogEntity::class,
        RecordingRecordEntity::class,
        GateEvidenceEntity::class,
        GateTutorRequirementEntity::class,
        GateDecisionEntity::class,
        ExportRecordEntity::class,
        dev.russiancompanion.v2.data.V2ReviewAssignmentEntity::class,
        dev.russiancompanion.v2.data.V2ReviewAnswerEntity::class,
        dev.russiancompanion.v2.data.V2ProfileEntity::class,
        dev.russiancompanion.v2.data.V2StepProgressEntity::class,
        dev.russiancompanion.v2.data.V2AttemptEntity::class,
        dev.russiancompanion.v2.data.V2BookmarkEntity::class,
        dev.russiancompanion.v2.data.V2NoteEntity::class,
        dev.russiancompanion.v2.data.V2TutorItemEntity::class,
        dev.russiancompanion.v2.data.V2BookAttestationEntity::class,
        dev.russiancompanion.v2.data.V2UnitCompletionEntity::class,
    ],
    version = 10,
    exportSchema = true,
)
@TypeConverters(StoredValueConverters::class)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun bundled(): BundledContentDao

    abstract fun users(): UserDataDao

    abstract fun v2(): dev.russiancompanion.v2.data.V2Dao
}
