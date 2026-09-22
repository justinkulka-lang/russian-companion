package dev.russiancompanion.data.local

import android.content.Context
import androidx.room.Room
import dev.russiancompanion.data.local.seed.BundledContentSeeder
import dev.russiancompanion.data.local.seed.SeedOutcome
import dev.russiancompanion.domain.gate.GateEvaluation
import dev.russiancompanion.domain.practice.StoredRecordingRef
import dev.russiancompanion.domain.practice.listenCompletionAccepted
import dev.russiancompanion.domain.practice.recordingCommitAccepted
import java.io.IOException
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

internal class LocalStore private constructor(
    private val database: AppDatabase,
) : AutoCloseable {
    private val io = Executors.newSingleThreadExecutor()
    private val seeder = BundledContentSeeder(database)
    private var lastSeedOutcome: SeedOutcome? = null

    fun applySeed(json: String?): SeedOutcome {
        val outcome = call { seeder.apply(json) }
        lastSeedOutcome = outcome
        return outcome
    }

    fun seedOutcome(): SeedOutcome? = lastSeedOutcome

    fun confirmLesson(segmentId: String, localDate: String) = call {
        val segment = database.bundled().segment(segmentId)
        if (segment == null || segment.eligibilityStatus != EligibilityStatus.ELIGIBLE) {
            throw IllegalArgumentException("An eligible course segment is required")
        }
        val hasPlan = database.bundled().eligiblePlans().any { it.segmentId == segment.id }
        if (!hasPlan) throw IllegalArgumentException("Plan definitions are unavailable")
        val date = UserRecordRules.requireLocalDate(localDate)
        val current = database.users().learner(LearnerStateEntity.LOCAL_ID) ?: LearnerStateEntity.newUser()
        database.users().updateLearner(
            current.copy(
                activeSegmentId = segment.id,
                onboardingCompleted = true,
                planContext = date,
            ),
        )
    }

    fun planInstances(localDate: String, segmentId: String): List<PlanInstanceEntity> = call {
        database.users().planInstancesFor(localDate, segmentId)
    }

    fun completeExternalActivity(
        planDefinitionId: String,
        localDate: String,
        recordedAtLocal: String,
    ): Boolean = call {
        val plan = requireEligiblePlan(planDefinitionId)
        val category = UserRecordRules.checklistCategory(plan.activityType)
            ?: throw IllegalArgumentException("Listen-record-compare is started in the app")
        if (plan.activityType == PlanActivityType.TUTOR && plan.required) {
            throw IllegalArgumentException("A tutor task must be optional")
        }
        val learner = database.users().learner(LearnerStateEntity.LOCAL_ID) ?: LearnerStateEntity.newUser()
        if (learner.activeSegmentId != plan.segmentId) {
            throw IllegalArgumentException("The activity is not in the current lesson")
        }
        val date = UserRecordRules.requireLocalDate(localDate)
        val instanceId = UserRecordRules.planInstanceId(date, plan.id)
        val existing = database.users().planInstance(instanceId)
        if (plan.activityType == PlanActivityType.TUTOR && existing == null) {
            throw IllegalArgumentException("Tutor task is not included")
        }
        if (existing?.completionState == CompletionState.COMPLETED) return@call false
        val instance = PlanInstanceEntity(
            id = instanceId,
            localDate = date,
            segmentId = plan.segmentId,
            planDefinitionId = plan.id,
            completionState = CompletionState.COMPLETED,
            completedAtLocal = UserRecordRules.requireTimestamp(recordedAtLocal),
        )
        val log = UserRecordRules.activityLog(
            category = category,
            durationMinutes = UserRecordRules.CHECKLIST_DURATION_MINUTES,
            completed = true,
            recordedAtLocal = recordedAtLocal,
            segmentId = plan.segmentId,
            planInstanceId = instance.id,
            selfAttested = plan.activityType == PlanActivityType.BOOK ||
                plan.activityType == PlanActivityType.ANKI_DROID,
            planActivityType = plan.activityType,
        )
        database.users().recordPlanCompletion(instance, log)
        database.users().updateLearner(learner.copy(planContext = date))
        true
    }

    fun includeTutorTask(planDefinitionId: String, localDate: String) = call {
        val plan = requireEligiblePlan(planDefinitionId)
        if (plan.activityType != PlanActivityType.TUTOR || plan.required) {
            throw IllegalArgumentException("Only an optional tutor task can be added")
        }
        val learner = database.users().learner(LearnerStateEntity.LOCAL_ID)
        if (learner?.activeSegmentId != plan.segmentId) {
            throw IllegalArgumentException("The activity is not in the current lesson")
        }
        val date = UserRecordRules.requireLocalDate(localDate)
        val instanceId = UserRecordRules.planInstanceId(date, plan.id)
        if (database.users().planInstance(instanceId) != null) return@call
        database.users().upsertPlanInstance(
            PlanInstanceEntity(
                id = instanceId,
                localDate = date,
                segmentId = plan.segmentId,
                planDefinitionId = plan.id,
                completionState = CompletionState.NOT_COMPLETED,
                completedAtLocal = null,
            ),
        )
    }

    fun removeTutorTask(planDefinitionId: String, localDate: String) = call {
        val plan = requireEligiblePlan(planDefinitionId)
        if (plan.activityType != PlanActivityType.TUTOR || plan.required) {
            throw IllegalArgumentException("Only an optional tutor task can be removed")
        }
        val date = UserRecordRules.requireLocalDate(localDate)
        database.users().removePlanInstance(UserRecordRules.planInstanceId(date, plan.id))
    }

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

    fun allEvidence(): List<GateEvidenceEntity> = call { database.users().allEvidence() }

    fun addTutorSignOffRequirement(gateDefinitionId: String, recordedAtLocal: String) = call {
        val gate = requireEligibleGate(gateDefinitionId)
        val recordedAt = UserRecordRules.requireTimestamp(recordedAtLocal)
        val conditions = database.bundled().conditions(gate.id)
        val evidence = database.users().evidenceFor(gate.id)
        val decision = storedGateDecision(
            gate = gate,
            conditions = conditions,
            tutorRequired = true,
            evidence = evidence,
            evaluatedAtLocal = recordedAt,
        )
        if (decision.conditionOutcomes.isBlank()) {
            throw IllegalArgumentException("Condition outcomes are required")
        }
        database.users().saveTutorRequirement(
            GateTutorRequirementEntity(gateDefinitionId = gate.id, recordedAtLocal = recordedAt),
            decision,
        )
    }

    fun recordGateEvidenceAndDecide(
        gateDefinitionId: String,
        entries: List<Pair<EvidenceType, String>>,
        recordedAtLocal: String,
    ): GateDecisionEntity = call {
        val gate = requireEligibleGate(gateDefinitionId)
        val recordedAt = UserRecordRules.requireTimestamp(recordedAtLocal)
        val conditions = database.bundled().conditions(gate.id)
        val tutorRequired = database.users().tutorRequirement(gate.id) != null
        val prepared = linkedMapOf<EvidenceType, String>()
        entries.forEach { (type, value) ->
            val trimmed = value.trim()
            if (trimmed.isEmpty()) return@forEach
            if (!evidenceAcceptedForGate(type, trimmed, conditions, tutorRequired)) {
                throw IllegalArgumentException("Evidence entry is not valid")
            }
            prepared[type] = trimmed
        }
        val created = prepared.map { (type, value) ->
            UserRecordRules.evidence(
                gateDefinitionId = gate.id,
                evidenceType = type,
                enteredValue = value,
                recordedAtLocal = recordedAt,
            )
        }
        val decision = storedGateDecision(
            gate = gate,
            conditions = conditions,
            tutorRequired = tutorRequired,
            evidence = database.users().evidenceFor(gate.id) + created,
            evaluatedAtLocal = recordedAt,
        )
        if (decision.conditionOutcomes.isBlank()) {
            throw IllegalArgumentException("Condition outcomes are required")
        }
        database.users().recordGateDecision(created, decision)
        decision
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
        val decision = verifiedCallerDecision(
            gate = gate,
            conditions = database.bundled().conditions(gate.id),
            tutorRequired = database.users().tutorRequirement(gate.id) != null,
            evidenceIds = evidenceIds,
            availableEvidence = database.users().allEvidence(),
            conditionOutcomes = conditionOutcomes,
            status = status,
            evaluatedAtLocal = UserRecordRules.requireTimestamp(evaluatedAtLocal),
        )
        database.users().insertDecision(decision)
        decision
    }

    fun decisions(): List<GateDecisionEntity> = call { database.users().decisions() }

    fun reproduceStoredDecision(decision: GateDecisionEntity): GateEvaluation = call {
        reproduceStoredDecision(decision, database.users().allEvidence())
    }

    fun commitRecording(
        audioModelId: String,
        localFileReference: String,
        createdAtLocal: String,
        markedForTutorReview: Boolean,
        completionState: CompletionState,
    ): RecordingRecordEntity = call {
        val model = database.bundled().audio(audioModelId)
        val admission = recordingCommitAccepted(
            modelEligible = model != null && model.eligibilityStatus == EligibilityStatus.ELIGIBLE,
            localFileReference = localFileReference,
            requestedCompleted = completionState == CompletionState.COMPLETED,
        )
        if (!admission.accepted) {
            throw IllegalArgumentException(admission.reason ?: "Recording was not accepted")
        }
        val record = UserRecordRules.recording(
            audioModelId = checkNotNull(model).id,
            localFileReference = localFileReference,
            createdAtLocal = createdAtLocal,
            markedForTutorReview = markedForTutorReview,
            completionState = completionState,
        )
        database.users().insertRecording(record)
        record
    }

    fun setRecordingTutorReview(recordingId: String, marked: Boolean) = call {
        val recording = database.users().recordings().firstOrNull { it.id == recordingId }
            ?: throw IllegalArgumentException("Unknown recording")
        database.users().updateRecording(recording.copy(markedForTutorReview = marked))
    }

    fun completeListenPractice(
        audioModelId: String,
        localDate: String,
        recordedAtLocal: String,
    ): Boolean = call {
        val model = database.bundled().audio(audioModelId)
        val recording = model?.let { stored ->
            database.users().recordings().lastOrNull { it.audioModelId == stored.id }
        }
        val admission = listenCompletionAccepted(
            modelEligible = model != null && model.eligibilityStatus == EligibilityStatus.ELIGIBLE,
            expectedAudioModelId = audioModelId,
            recording = recording?.let { stored ->
                StoredRecordingRef(
                    audioModelId = stored.audioModelId,
                    localFileReference = stored.localFileReference,
                    completed = stored.completionState == CompletionState.COMPLETED,
                )
            },
        )
        if (!admission.accepted) {
            if (admission.reason == "already-completed") return@call false
            throw IllegalArgumentException(admission.reason ?: "Practice completion was not accepted")
        }
        checkNotNull(model)
        checkNotNull(recording)
        val learner = database.users().learner(LearnerStateEntity.LOCAL_ID)
        val plan = learner?.let { state ->
            database.bundled().eligiblePlans().firstOrNull { candidate ->
                candidate.segmentId == state.activeSegmentId &&
                    candidate.activityType == PlanActivityType.LISTEN_RECORD_COMPARE
            }
        }
        if (learner == null || plan == null) {
            database.users().updateRecording(recording.copy(completionState = CompletionState.COMPLETED))
            return@call true
        }
        val date = UserRecordRules.requireLocalDate(localDate)
        val instanceId = UserRecordRules.planInstanceId(date, plan.id)
        val existing = database.users().planInstance(instanceId)
        if (existing?.completionState == CompletionState.COMPLETED) {
            database.users().updateRecording(recording.copy(completionState = CompletionState.COMPLETED))
            return@call true
        }
        val instance = PlanInstanceEntity(
            id = instanceId,
            localDate = date,
            segmentId = plan.segmentId,
            planDefinitionId = plan.id,
            completionState = CompletionState.COMPLETED,
            completedAtLocal = UserRecordRules.requireTimestamp(recordedAtLocal),
        )
        val log = UserRecordRules.activityLog(
            category = UserRecordRules.practiceCategory(),
            durationMinutes = UserRecordRules.CHECKLIST_DURATION_MINUTES,
            completed = true,
            recordedAtLocal = recordedAtLocal,
            segmentId = plan.segmentId,
            planInstanceId = instance.id,
            selfAttested = false,
            planActivityType = plan.activityType,
        )
        database.users().recordListenCompletion(
            recording = recording.copy(
                completionState = CompletionState.COMPLETED,
                activityLogId = log.id,
            ),
            instance = instance,
            log = log,
        )
        database.users().updateLearner(learner.copy(planContext = date))
        true
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

    private fun requireEligibleGate(gateDefinitionId: String): GateDefinitionEntity {
        val gate = database.bundled().gate(gateDefinitionId)
        if (gate == null || gate.eligibilityStatus != EligibilityStatus.ELIGIBLE) {
            throw IllegalArgumentException("An eligible gate is required")
        }
        return gate
    }

    private fun requireEligiblePlan(planDefinitionId: String): PlanDefinitionEntity {
        val plan = database.bundled().plan(planDefinitionId)
        if (plan == null || plan.eligibilityStatus != EligibilityStatus.ELIGIBLE) {
            throw IllegalArgumentException("An eligible plan definition is required")
        }
        return plan
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
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
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

        internal fun readPackagedSeed(context: Context): String? {
            return try {
                context.applicationContext.assets.open(SEED_ASSET).bufferedReader(Charsets.UTF_8).use { it.readText() }
            } catch (_: IOException) {
                null
            }
        }

        private fun readSeed(context: Context): String? = readPackagedSeed(context)
    }
}
