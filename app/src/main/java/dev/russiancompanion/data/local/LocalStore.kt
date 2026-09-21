package dev.russiancompanion.data.local

import android.content.Context
import androidx.room.Room
import dev.russiancompanion.data.local.seed.BundledContentSeeder
import dev.russiancompanion.data.local.seed.SeedOutcome
import java.io.IOException
import java.util.UUID
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

internal class LocalStore private constructor(
    private val database: AppDatabase,
) : AutoCloseable {
    private val io = Executors.newSingleThreadExecutor()
    private val seeder = BundledContentSeeder(database)

    fun applySeed(json: String?): SeedOutcome = call { seeder.apply(json) }

    fun eligibleSegments(): List<CourseSegmentEntity> = call { database.bundled().eligibleSegments() }

    fun eligiblePlans(): List<PlanDefinitionEntity> = call { database.bundled().eligiblePlans() }

    fun eligibleGates(): List<GateDefinitionEntity> = call { database.bundled().eligibleGates() }

    fun gate(id: String): GateDefinitionEntity? = call { database.bundled().gate(id) }

    fun conditions(gateId: String): List<GateConditionEntity> = call { database.bundled().conditions(gateId) }

    fun eligibleAudio(): List<AudioModelEntity> = call { database.bundled().eligibleAudio() }

    fun audio(id: String): AudioModelEntity? = call { database.bundled().audio(id) }

    fun allAudio(): List<AudioModelEntity> = call { database.bundled().allAudio() }

    fun sources(): List<SourceRecordEntity> = call { database.bundled().sources() }

    fun sourceText(id: String): SourceTextEntity? = call { database.bundled().sourceText(id) }

    fun rights(): List<AssetRightsRecordEntity> = call { database.bundled().rights() }

    fun eligibleDrills(): List<OptionalDrillDefinitionEntity> = call { database.bundled().eligibleDrills() }

    fun learner(): LearnerStateEntity = call {
        database.users().learner(LearnerStateEntity.LOCAL_ID) ?: LearnerStateEntity.newUser()
    }

    fun completeOnboarding(segmentId: String) = call {
        val segment = database.bundled().segment(segmentId)
        if (segment == null || segment.eligibilityStatus != EligibilityStatus.ELIGIBLE) {
            throw IllegalArgumentException("An eligible course segment is required")
        }
        val current = database.users().learner(LearnerStateEntity.LOCAL_ID) ?: LearnerStateEntity.newUser()
        database.users().updateLearner(
            current.copy(activeSegmentId = segment.id, onboardingCompleted = true),
        )
    }

    fun setPlanContext(localDate: String?) = call {
        val current = database.users().learner(LearnerStateEntity.LOCAL_ID) ?: error("Learner state is missing")
        val context = localDate?.let(UserRecordRules::requireLocalDate)
        database.users().updateLearner(current.copy(planContext = context))
    }

    fun recordPlanCompletion(
        localDate: String,
        planDefinitionId: String,
        category: ActivityCategory,
        durationMinutes: Int,
        recordedAtLocal: String,
    ): ActivityLogEntity = call {
        val plan = database.bundled().plan(planDefinitionId)
        if (plan == null || plan.eligibilityStatus != EligibilityStatus.ELIGIBLE) {
            throw IllegalArgumentException("An eligible plan definition is required")
        }
        val instance = PlanInstanceEntity(
            id = UserRecordRules.planInstanceId(localDate, plan.id),
            localDate = UserRecordRules.requireLocalDate(localDate),
            segmentId = plan.segmentId,
            planDefinitionId = plan.id,
            completionState = CompletionState.COMPLETED,
            completedAtLocal = UserRecordRules.requireTimestamp(recordedAtLocal),
        )
        val log = UserRecordRules.activityLog(
            category = category,
            durationMinutes = durationMinutes,
            completed = true,
            recordedAtLocal = recordedAtLocal,
            segmentId = plan.segmentId,
            planInstanceId = instance.id,
            selfAttested = plan.activityType == PlanActivityType.BOOK ||
                plan.activityType == PlanActivityType.ANKI_DROID,
            planActivityType = plan.activityType,
        )
        database.users().recordPlanCompletion(instance, log)
        log
    }

    fun recordActivity(
        category: ActivityCategory,
        durationMinutes: Int,
        completed: Boolean,
        recordedAtLocal: String,
        segmentId: String?,
        selfAttested: Boolean,
    ): ActivityLogEntity = call {
        if (segmentId != null && database.bundled().segment(segmentId) == null) {
            throw IllegalArgumentException("Unknown course segment")
        }
        val log = UserRecordRules.activityLog(
            category = category,
            durationMinutes = durationMinutes,
            completed = completed,
            recordedAtLocal = recordedAtLocal,
            segmentId = segmentId,
            planInstanceId = null,
            selfAttested = selfAttested,
            planActivityType = null,
        )
        database.users().insertActivityLog(log)
        log
    }

    fun activityLogs(): List<ActivityLogEntity> = call { database.users().activityLogs() }

    fun activityTotals(): List<CategoryTotal> = call { totalsFrom(database.users().activityLogs()) }

    fun recordEvidence(
        gateDefinitionId: String,
        evidenceType: EvidenceType,
        enteredValue: String,
        recordedAtLocal: String,
    ): GateEvidenceEntity = call {
        val gate = database.bundled().gate(gateDefinitionId)
        if (gate == null || gate.eligibilityStatus != EligibilityStatus.ELIGIBLE) {
            throw IllegalArgumentException("An eligible gate is required")
        }
        val evidence = UserRecordRules.evidence(
            gateDefinitionId = gate.id,
            evidenceType = evidenceType,
            enteredValue = enteredValue,
            recordedAtLocal = recordedAtLocal,
        )
        database.users().insertEvidence(evidence)
        evidence
    }

    fun evidenceFor(gateId: String): List<GateEvidenceEntity> = call { database.users().evidenceFor(gateId) }

    fun addTutorSignOffRequirement(gateDefinitionId: String, recordedAtLocal: String) = call {
        val gate = database.bundled().gate(gateDefinitionId)
        if (gate == null || gate.eligibilityStatus != EligibilityStatus.ELIGIBLE) {
            throw IllegalArgumentException("An eligible gate is required")
        }
        database.users().upsertTutorRequirement(
            GateTutorRequirementEntity(
                gateDefinitionId = gate.id,
                recordedAtLocal = UserRecordRules.requireTimestamp(recordedAtLocal),
            ),
        )
    }

    fun tutorRequirement(gateId: String): GateTutorRequirementEntity? = call {
        database.users().tutorRequirement(gateId)
    }

    fun saveGateDecision(
        gateDefinitionId: String,
        ruleVersion: String,
        evidenceIds: List<String>,
        conditionOutcomes: String,
        status: GateDecisionStatus,
        evaluatedAtLocal: String,
    ): GateDecisionEntity = call {
        val gate = database.bundled().gate(gateDefinitionId) ?: throw IllegalArgumentException("Unknown gate")
        if (ruleVersion.isBlank() || ruleVersion != gate.ruleVersion) {
            throw IllegalArgumentException("The decision must name the stored rule version")
        }
        if (conditionOutcomes.isBlank()) throw IllegalArgumentException("Condition outcomes are required")
        val decision = GateDecisionEntity(
            id = UUID.randomUUID().toString(),
            gateDefinitionId = gate.id,
            ruleVersion = ruleVersion,
            evidenceIds = evidenceIds.joinToString(separator = ","),
            conditionOutcomes = conditionOutcomes,
            status = status,
            evaluatedAtLocal = UserRecordRules.requireTimestamp(evaluatedAtLocal),
        )
        database.users().insertDecision(decision)
        decision
    }

    fun decisions(): List<GateDecisionEntity> = call { database.users().decisions() }

    fun commitRecording(
        audioModelId: String,
        localFileReference: String,
        createdAtLocal: String,
        markedForTutorReview: Boolean,
        completionState: CompletionState,
    ): RecordingRecordEntity = call {
        val model = database.bundled().audio(audioModelId)
        if (model == null || model.eligibilityStatus != EligibilityStatus.ELIGIBLE) {
            throw IllegalArgumentException("An eligible audio model is required")
        }
        val record = UserRecordRules.recording(
            audioModelId = model.id,
            localFileReference = localFileReference,
            createdAtLocal = createdAtLocal,
            markedForTutorReview = markedForTutorReview,
            completionState = completionState,
        )
        database.users().insertRecording(record)
        record
    }

    fun recordings(): List<RecordingRecordEntity> = call { database.users().recordings() }

    fun saveExport(record: ExportRecordEntity) = call {
        if (record.logCount < 0 || record.evidenceCount < 0 || record.progressCount < 0 || record.recordingCount < 0) {
            throw IllegalArgumentException("Export counts must be nonnegative")
        }
        UserRecordRules.requireTimestamp(record.exportedAtLocal)
        database.users().insertExport(record)
    }

    fun exports(): List<ExportRecordEntity> = call { database.users().exports() }

    fun learnerCount(): Int = call { database.users().learnerCount() }

    override fun close() {
        io.shutdown()
        database.close()
    }

    private fun <T> call(block: () -> T): T {
        try {
            return io.submit(Callable(block)).get()
        } catch (error: ExecutionException) {
            throw error.cause ?: error
        }
    }

    companion object {
        const val DATABASE_NAME = "russian-companion.db"
        const val SEED_ASSET = "content/bundled-content.json"

        fun open(context: Context): LocalStore {
            val appContext = context.applicationContext
            val database = Room.databaseBuilder(appContext, AppDatabase::class.java, DATABASE_NAME)
                .build()
            val store = LocalStore(database)
            store.applySeed(readSeed(appContext))
            return store
        }

        fun openInMemory(context: Context): LocalStore {
            val database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
            val store = LocalStore(database)
            store.applySeed(null)
            return store
        }

        private fun readSeed(context: Context): String? {
            return try {
                context.assets.open(SEED_ASSET).bufferedReader(Charsets.UTF_8).use { it.readText() }
            } catch (_: IOException) {
                null
            }
        }
    }
}
