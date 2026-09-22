package dev.russiancompanion.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert

@Dao
internal abstract class UserDataDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertLearnerIfAbsent(state: LearnerStateEntity): Long

    @Query("SELECT * FROM learner_state WHERE id = :id")
    abstract fun learner(id: String): LearnerStateEntity?

    @Update
    abstract fun updateLearner(state: LearnerStateEntity)

    @Upsert
    abstract fun upsertPlanInstance(instance: PlanInstanceEntity)

    @Query("SELECT * FROM plan_instances WHERE id = :id")
    abstract fun planInstance(id: String): PlanInstanceEntity?

    @Query(
        "SELECT * FROM plan_instances WHERE localDate = :localDate AND segmentId = :segmentId ORDER BY id",
    )
    abstract fun planInstancesFor(localDate: String, segmentId: String): List<PlanInstanceEntity>

    @Query("UPDATE activity_logs SET planInstanceId = NULL WHERE planInstanceId = :planInstanceId")
    abstract fun detachActivityLogs(planInstanceId: String)

    @Query("DELETE FROM plan_instances WHERE id = :id")
    abstract fun deletePlanInstance(id: String)

    @Transaction
    open fun removePlanInstance(id: String) {
        detachActivityLogs(id)
        deletePlanInstance(id)
    }

    @Insert
    abstract fun insertActivityLog(log: ActivityLogEntity)

    @Query("SELECT * FROM activity_logs ORDER BY recordedAtLocal, id")
    abstract fun activityLogs(): List<ActivityLogEntity>

    @Insert
    abstract fun insertRecording(record: RecordingRecordEntity)

    @Query("SELECT * FROM recording_records ORDER BY createdAtLocal, id")
    abstract fun recordings(): List<RecordingRecordEntity>

    @Query("SELECT * FROM recording_records WHERE audioModelId = :audioModelId ORDER BY createdAtLocal, id")
    abstract fun recordingsFor(audioModelId: String): List<RecordingRecordEntity>

    @Query("DELETE FROM recording_records WHERE id IN (:ids)")
    abstract fun deleteRecordings(ids: List<String>)

    @Update
    abstract fun updateRecording(record: RecordingRecordEntity)

    @Insert
    abstract fun insertEvidence(evidence: GateEvidenceEntity)

    @Query("SELECT * FROM gate_evidence WHERE gateDefinitionId = :gateId ORDER BY recordedAtLocal, id")
    abstract fun evidenceFor(gateId: String): List<GateEvidenceEntity>

    @Query("SELECT * FROM gate_evidence ORDER BY recordedAtLocal, id")
    abstract fun allEvidence(): List<GateEvidenceEntity>

    @Upsert
    abstract fun upsertTutorRequirement(requirement: GateTutorRequirementEntity)

    @Query("SELECT * FROM gate_tutor_requirements")
    abstract fun allTutorRequirements(): List<GateTutorRequirementEntity>

    @Query("SELECT * FROM gate_tutor_requirements WHERE gateDefinitionId = :gateId")
    abstract fun tutorRequirement(gateId: String): GateTutorRequirementEntity?

    @Insert
    abstract fun insertDecision(decision: GateDecisionEntity)

    @Query("SELECT * FROM gate_decisions ORDER BY evaluatedAtLocal, id")
    abstract fun decisions(): List<GateDecisionEntity>

    @Insert
    abstract fun insertExport(record: ExportRecordEntity)

    @Query("SELECT * FROM export_records ORDER BY exportedAtLocal, id")
    abstract fun exports(): List<ExportRecordEntity>

    @Query("SELECT COUNT(*) FROM learner_state")
    abstract fun learnerCount(): Int

    @Transaction
    open fun recordPlanCompletion(instance: PlanInstanceEntity, log: ActivityLogEntity) {
        upsertPlanInstance(instance)
        insertActivityLog(log)
    }

    @Transaction
    open fun recordListenCompletion(
        recording: RecordingRecordEntity,
        instance: PlanInstanceEntity,
        log: ActivityLogEntity,
    ) {
        upsertPlanInstance(instance)
        insertActivityLog(log)
        updateRecording(recording)
    }

    @Transaction
    open fun recordGateDecision(evidence: List<GateEvidenceEntity>, decision: GateDecisionEntity) {
        evidence.forEach(::insertEvidence)
        insertDecision(decision)
    }

    @Transaction
    open fun saveTutorRequirement(requirement: GateTutorRequirementEntity, decision: GateDecisionEntity) {
        upsertTutorRequirement(requirement)
        insertDecision(decision)
    }
}
