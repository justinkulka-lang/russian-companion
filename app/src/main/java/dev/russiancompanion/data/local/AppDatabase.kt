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
    ],
    version = 5,
    exportSchema = true,
)
@TypeConverters(StoredValueConverters::class)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun bundled(): BundledContentDao

    abstract fun users(): UserDataDao
}
