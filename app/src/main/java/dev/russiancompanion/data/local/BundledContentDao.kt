package dev.russiancompanion.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import dev.russiancompanion.data.local.seed.PreparedBundle

@Dao
internal abstract class BundledContentDao {
    @Upsert
    abstract fun upsertSources(rows: List<SourceRecordEntity>)

    @Upsert
    abstract fun upsertTexts(rows: List<SourceTextEntity>)

    @Upsert
    abstract fun upsertRights(rows: List<AssetRightsRecordEntity>)

    @Upsert
    abstract fun upsertSegments(rows: List<CourseSegmentEntity>)

    @Upsert
    abstract fun upsertPlans(rows: List<PlanDefinitionEntity>)

    @Upsert
    abstract fun upsertGates(rows: List<GateDefinitionEntity>)

    @Upsert
    abstract fun upsertConditions(rows: List<GateConditionEntity>)

    @Upsert
    abstract fun upsertAudio(rows: List<AudioModelEntity>)

    @Upsert
    abstract fun upsertDrills(rows: List<OptionalDrillDefinitionEntity>)

    @Upsert
    abstract fun upsertSeedState(state: BundledSeedStateEntity)

    @Query("DELETE FROM gate_conditions WHERE gateDefinitionId IN (:gateIds)")
    abstract fun deleteConditionsFor(gateIds: List<String>)

    @Query("UPDATE course_segments SET eligibilityStatus = 'EXCLUDED'")
    abstract fun excludeAllSegments()

    @Query("UPDATE course_segments SET eligibilityStatus = 'EXCLUDED' WHERE id NOT IN (:ids)")
    abstract fun excludeSegmentsNotIn(ids: List<String>)

    @Query("UPDATE plan_definitions SET eligibilityStatus = 'EXCLUDED'")
    abstract fun excludeAllPlans()

    @Query("UPDATE plan_definitions SET eligibilityStatus = 'EXCLUDED' WHERE id NOT IN (:ids)")
    abstract fun excludePlansNotIn(ids: List<String>)

    @Query(
        "UPDATE plan_definitions SET eligibilityStatus = 'EXCLUDED' " +
            "WHERE segmentId IN (SELECT id FROM course_segments WHERE eligibilityStatus = 'EXCLUDED')",
    )
    abstract fun excludePlansForExcludedSegments()

    @Query("UPDATE gate_definitions SET eligibilityStatus = 'EXCLUDED'")
    abstract fun excludeAllGates()

    @Query("UPDATE gate_definitions SET eligibilityStatus = 'EXCLUDED' WHERE id NOT IN (:ids)")
    abstract fun excludeGatesNotIn(ids: List<String>)

    @Query(
        "UPDATE gate_definitions SET eligibilityStatus = 'EXCLUDED' " +
            "WHERE segmentId IN (SELECT id FROM course_segments WHERE eligibilityStatus = 'EXCLUDED')",
    )
    abstract fun excludeGatesForExcludedSegments()

    @Query("UPDATE audio_models SET eligibilityStatus = 'EXCLUDED'")
    abstract fun excludeAllAudio()

    @Query("UPDATE audio_models SET eligibilityStatus = 'EXCLUDED' WHERE id NOT IN (:ids)")
    abstract fun excludeAudioNotIn(ids: List<String>)

    @Query("UPDATE optional_drill_definitions SET eligibilityStatus = 'EXCLUDED'")
    abstract fun excludeAllDrills()

    @Query("UPDATE optional_drill_definitions SET eligibilityStatus = 'EXCLUDED' WHERE id NOT IN (:ids)")
    abstract fun excludeDrillsNotIn(ids: List<String>)

    @Query("SELECT * FROM course_segments WHERE eligibilityStatus = 'ELIGIBLE' ORDER BY sortOrder, id")
    abstract fun eligibleSegments(): List<CourseSegmentEntity>

    @Query("SELECT * FROM course_segments WHERE id = :id")
    abstract fun segment(id: String): CourseSegmentEntity?

    @Query("SELECT * FROM plan_definitions WHERE eligibilityStatus = 'ELIGIBLE' ORDER BY sortOrder, id")
    abstract fun eligiblePlans(): List<PlanDefinitionEntity>

    @Query("SELECT * FROM plan_definitions WHERE id = :id")
    abstract fun plan(id: String): PlanDefinitionEntity?

    @Query("SELECT * FROM gate_definitions WHERE eligibilityStatus = 'ELIGIBLE' ORDER BY id")
    abstract fun eligibleGates(): List<GateDefinitionEntity>

    @Query("SELECT * FROM gate_definitions WHERE id = :id")
    abstract fun gate(id: String): GateDefinitionEntity?

    @Query("SELECT * FROM gate_conditions WHERE gateDefinitionId = :gateId ORDER BY sortOrder, id")
    abstract fun conditions(gateId: String): List<GateConditionEntity>

    @Query("SELECT * FROM audio_models WHERE eligibilityStatus = 'ELIGIBLE' ORDER BY id")
    abstract fun eligibleAudio(): List<AudioModelEntity>

    @Query("SELECT * FROM audio_models WHERE id = :id")
    abstract fun audio(id: String): AudioModelEntity?

    @Query("SELECT * FROM audio_models ORDER BY id")
    abstract fun allAudio(): List<AudioModelEntity>

    @Query("SELECT * FROM source_records ORDER BY id")
    abstract fun sources(): List<SourceRecordEntity>

    @Query("SELECT * FROM source_texts WHERE id = :id")
    abstract fun sourceText(id: String): SourceTextEntity?

    @Query("SELECT * FROM asset_rights_records ORDER BY id")
    abstract fun rights(): List<AssetRightsRecordEntity>

    @Query("SELECT * FROM optional_drill_definitions WHERE eligibilityStatus = 'ELIGIBLE' ORDER BY id")
    abstract fun eligibleDrills(): List<OptionalDrillDefinitionEntity>

    @Query("SELECT * FROM bundled_seed_state WHERE id = :id")
    abstract fun seedState(id: Int): BundledSeedStateEntity?

    @Query("SELECT COUNT(*) FROM audio_models WHERE eligibilityStatus = 'ELIGIBLE'")
    abstract fun eligibleAudioCount(): Int

    @Query("SELECT COUNT(*) FROM audio_models WHERE eligibilityStatus = 'EXCLUDED'")
    abstract fun excludedAudioCount(): Int

    @Query("SELECT COUNT(*) FROM gate_definitions WHERE eligibilityStatus = 'ELIGIBLE'")
    abstract fun eligibleGateCount(): Int

    @Query("SELECT COUNT(*) FROM gate_definitions WHERE eligibilityStatus = 'EXCLUDED'")
    abstract fun excludedGateCount(): Int

    @Query(
        "SELECT COUNT(*) FROM source_records WHERE shareAlikeConfirmation = 'UNCONFIRMED'",
    )
    abstract fun unconfirmedShareAlikeCount(): Int

    @Transaction
    open fun replaceBundledContent(bundle: PreparedBundle) {
        if (bundle.sources.isNotEmpty()) upsertSources(bundle.sources)
        if (bundle.texts.isNotEmpty()) upsertTexts(bundle.texts)
        if (bundle.rights.isNotEmpty()) upsertRights(bundle.rights)
        if (bundle.segments.isNotEmpty()) upsertSegments(bundle.segments)
        if (bundle.plans.isNotEmpty()) upsertPlans(bundle.plans)
        if (bundle.gates.isNotEmpty()) upsertGates(bundle.gates)
        val gateIds = bundle.gates.map { it.id }
        if (gateIds.isNotEmpty()) {
            deleteConditionsFor(gateIds)
        }
        if (bundle.conditions.isNotEmpty()) upsertConditions(bundle.conditions)
        if (bundle.audio.isNotEmpty()) upsertAudio(bundle.audio)
        if (bundle.drills.isNotEmpty()) upsertDrills(bundle.drills)
        excludeMissing(bundle.segments.map { it.id }, ::excludeAllSegments, ::excludeSegmentsNotIn)
        excludeMissing(bundle.plans.map { it.id }, ::excludeAllPlans, ::excludePlansNotIn)
        excludePlansForExcludedSegments()
        excludeMissing(bundle.gates.map { it.id }, ::excludeAllGates, ::excludeGatesNotIn)
        excludeGatesForExcludedSegments()
        excludeMissing(bundle.audio.map { it.id }, ::excludeAllAudio, ::excludeAudioNotIn)
        excludeMissing(bundle.drills.map { it.id }, ::excludeAllDrills, ::excludeDrillsNotIn)
        upsertSeedState(BundledSeedStateEntity(contentVersion = bundle.contentVersion))
    }

    private fun excludeMissing(
        ids: List<String>,
        excludeAll: () -> Unit,
        excludeNotIn: (List<String>) -> Unit,
    ) {
        if (ids.isEmpty()) {
            excludeAll()
        } else {
            excludeNotIn(ids)
        }
    }
}
