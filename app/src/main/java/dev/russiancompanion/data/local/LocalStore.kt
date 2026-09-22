package dev.russiancompanion.data.local

import android.content.Context
import androidx.room.Room
import dev.russiancompanion.data.local.seed.BundledContentSeeder
import dev.russiancompanion.data.local.seed.SeedOutcome
import dev.russiancompanion.domain.gate.GateEvaluation
import dev.russiancompanion.domain.plan.ActivityLogView
import dev.russiancompanion.domain.plan.RecordedCategory
import dev.russiancompanion.domain.practice.ListenPlanRef
import dev.russiancompanion.domain.progress.ExportCounts
import dev.russiancompanion.domain.progress.ProgressReview
import dev.russiancompanion.domain.progress.ProgressSnapshot
import dev.russiancompanion.domain.progress.activityLogAccepted
import dev.russiancompanion.domain.progress.captureProgressSnapshot
import dev.russiancompanion.domain.progress.exportCompletionAccepted
import dev.russiancompanion.domain.progress.reproduceProgress
import dev.russiancompanion.domain.navigation.LicenseNoticeOption
import dev.russiancompanion.domain.practice.StoredRecordingRef
import dev.russiancompanion.domain.practice.completedRecordingCountsTowardPlan
import dev.russiancompanion.domain.practice.listenCompletionAccepted
import dev.russiancompanion.domain.practice.listenPlanSatisfiedByCompletions
import dev.russiancompanion.domain.practice.recordingCommitAccepted
import java.io.File
import java.io.IOException
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

internal class LocalStore private constructor(
    private val database: AppDatabase,
    private val recordingRoot: File? = null,
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

    fun bundledSeedIdentity(): Pair<Int?, String?> = call {
        val state = database.bundled().seedState(BundledSeedStateEntity.SINGLETON_ID)
        state?.contentVersion to state?.contentHash?.takeIf { it.isNotBlank() }
    }

    fun confirmLesson(segmentId: String, localDate: String) = call {
        val segment = database.bundled().segment(segmentId)
        if (segment == null || segment.eligibilityStatus != EligibilityStatus.ELIGIBLE) {
            throw IllegalArgumentException("An eligible course segment is required")
        }
        val hasPlan = database.bundled().eligiblePlans().any { it.segmentId == segment.id }
        if (!hasPlan) throw IllegalArgumentException("Plan definitions are unavailable")
        val date = UserRecordRules.requireLocalDate(localDate)
        val current = database.users().learner(LearnerStateEntity.LOCAL_ID) ?: LearnerStateEntity.newUser()
        val updated = current.copy(
            activeSegmentId = segment.id,
            onboardingCompleted = true,
            planContext = date,
        )
        database.users().updateLearner(updated)
        applySatisfiedListenPlanCompletions(updated, date, recordedAtLocal = null)
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
        val admission = activityLogAccepted(
            category = RecordedCategory.valueOf(category.name),
            durationMinutes = durationMinutes,
            recordedAtLocal = recordedAtLocal,
            selfAttested = selfAttested,
        )
        if (!admission.accepted) {
            throw IllegalArgumentException(admission.reason ?: "Activity log was not accepted")
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
        val superseded = database.users().recordingsFor(model.id)
            .filter { it.completionState != CompletionState.COMPLETED }
        database.users().insertRecording(record)
        if (superseded.isNotEmpty()) {
            database.users().deleteRecordings(superseded.map { it.id })
            superseded.forEach { stored -> deleteRecordingFile(stored.localFileReference) }
        }
        sweepOrphanRecordingFilesLocked()
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
            recording = recording?.toStoredRef(),
        )
        if (!admission.accepted && admission.reason != "already-completed") {
            throw IllegalArgumentException(admission.reason ?: "Practice completion was not accepted")
        }
        if (admission.accepted) {
            database.users().updateRecording(checkNotNull(recording).copy(completionState = CompletionState.COMPLETED))
        }
        val learner = database.users().learner(LearnerStateEntity.LOCAL_ID)
        val completedPlan = learner?.activeSegmentId != null &&
            applySatisfiedListenPlanCompletions(learner, localDate, recordedAtLocal)
        admission.accepted || completedPlan
    }

    fun recordings(): List<RecordingRecordEntity> = call { database.users().recordings() }

    fun navigationBundle(localDate: String): NavigationBundle = call {
        val learner = database.users().learner(LearnerStateEntity.LOCAL_ID) ?: LearnerStateEntity.newUser()
        val segmentId = learner.activeSegmentId
        val date = localDate.takeIf { it.isNotBlank() }?.let(UserRecordRules::requireLocalDate)
        NavigationBundle(
            learner = learner,
            segments = database.bundled().eligibleSegments(),
            plans = database.bundled().eligiblePlans(),
            instances = if (segmentId == null || date == null) {
                emptyList()
            } else {
                database.users().planInstancesFor(date, segmentId)
            },
            recordings = database.users().recordings(),
            sources = database.bundled().sources(),
            texts = database.bundled().allSourceTexts(),
            rights = database.bundled().rights(),
            allAudio = database.bundled().allAudio(),
            eligibleAudio = database.bundled().eligibleAudio(),
            gates = database.bundled().eligibleGates(),
            conditionsByGate = database.bundled().allConditions().groupBy { it.gateDefinitionId },
            tutorGateIds = database.users().allTutorRequirements().map { it.gateDefinitionId }.toSet(),
            evidence = database.users().allEvidence(),
            decisions = database.users().decisions(),
            logs = database.users().activityLogs(),
            eligibleDrillCount = database.bundled().eligibleDrills().size,
        )
    }

    fun sweepOrphanRecordingFiles() = call { sweepOrphanRecordingFilesLocked() }

    fun saveExport(record: ExportRecordEntity, packageCounts: ExportCounts? = null): ExportRecordEntity = call {
        val admission = exportCompletionAccepted(
            statusName = record.status.name,
            recorded = ExportCounts(
                logCount = record.logCount,
                evidenceCount = record.evidenceCount,
                progressCount = record.progressCount,
                recordingCount = record.recordingCount,
            ),
            packageCounts = packageCounts,
            exportedAtLocal = record.exportedAtLocal,
        )
        if (!admission.accepted) {
            throw IllegalArgumentException(admission.reason ?: "Export record was not accepted")
        }
        database.users().insertExport(record)
        record
    }

    fun storedProgressSnapshot(): ProgressSnapshot = call {
        captureProgressSnapshot(activityViews())
            ?: throw IllegalArgumentException("Progress is unreadable")
    }

    fun reproduceStoredProgress(snapshot: ProgressSnapshot): ProgressReview = call {
        reproduceProgress(snapshot, activityViews())
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

    private fun applySatisfiedListenPlanCompletions(
        learner: LearnerStateEntity,
        localDate: String,
        recordedAtLocal: String?,
    ): Boolean {
        val segmentId = learner.activeSegmentId ?: return false
        val date = UserRecordRules.requireLocalDate(localDate)
        val completedIds = database.users().recordings()
            .filter { stored -> completedRecordingCountsTowardPlan(stored.toStoredRef()) }
            .map { it.audioModelId }
            .toSet()
        val ready = database.bundled().eligiblePlans()
            .filter { it.activityType == PlanActivityType.LISTEN_RECORD_COMPARE }
            .map { candidate ->
                ListenPlanRef(
                    id = candidate.id,
                    segmentId = candidate.segmentId,
                    audioModelIds = candidate.audioModelIds,
                )
            }
            .filter { plan ->
                plan.segmentId == segmentId &&
                    listenPlanSatisfiedByCompletions(plan.audioModelIds, completedIds)
            }
        var completedAny = false
        for (plan in ready) {
            val instanceId = UserRecordRules.planInstanceId(date, plan.id)
            val existing = database.users().planInstance(instanceId)
            if (existing?.completionState == CompletionState.COMPLETED) continue
            val latest = database.users().recordings()
                .filter { stored ->
                    stored.audioModelId in plan.audioModelIds &&
                        completedRecordingCountsTowardPlan(stored.toStoredRef())
                }
                .maxWithOrNull(compareBy({ it.createdAtLocal }, { it.id }))
                ?: continue
            val completedAt = recordedAtLocal ?: latest.createdAtLocal
            val instance = PlanInstanceEntity(
                id = instanceId,
                localDate = date,
                segmentId = plan.segmentId,
                planDefinitionId = plan.id,
                completionState = CompletionState.COMPLETED,
                completedAtLocal = UserRecordRules.requireTimestamp(completedAt),
            )
            val log = UserRecordRules.activityLog(
                category = UserRecordRules.practiceCategory(),
                durationMinutes = UserRecordRules.CHECKLIST_DURATION_MINUTES,
                completed = true,
                recordedAtLocal = completedAt,
                segmentId = plan.segmentId,
                planInstanceId = instance.id,
                selfAttested = false,
                planActivityType = PlanActivityType.LISTEN_RECORD_COMPARE,
            )
            database.users().recordListenCompletion(
                recording = latest.copy(
                    completionState = CompletionState.COMPLETED,
                    activityLogId = log.id,
                ),
                instance = instance,
                log = log,
            )
            completedAny = true
        }
        if (completedAny) {
            database.users().updateLearner(learner.copy(planContext = date))
        }
        return completedAny
    }

    private fun activityViews(): List<ActivityLogView> {
        return database.users().activityLogs().map { log ->
            ActivityLogView(
                id = log.id,
                category = RecordedCategory.valueOf(log.category.name),
                minutes = log.durationMinutes,
                completed = log.completed,
                recordedAtLocal = log.recordedAtLocal,
                selfAttested = log.selfAttested,
                segmentId = log.segmentId,
            )
        }
    }

    private fun RecordingRecordEntity.toStoredRef(): StoredRecordingRef {
        return StoredRecordingRef(
            audioModelId = audioModelId,
            localFileReference = localFileReference,
            completed = completionState == CompletionState.COMPLETED,
        )
    }

    private fun deleteRecordingFile(reference: String) {
        val root = recordingRoot ?: return
        if (reference.isBlank() || reference.contains("..") || reference.startsWith("/")) return
        val target = File(root, File(reference).name)
        if (target.isFile) target.delete()
    }

    private fun sweepOrphanRecordingFilesLocked() {
        val root = recordingRoot ?: return
        if (!root.isDirectory) return
        val referenced = database.users().recordings().map { File(it.localFileReference).name }.toSet()
        root.listFiles()?.forEach { file ->
            if (file.isFile && file.name !in referenced) {
                file.delete()
            }
        }
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
        const val CATALOG_NOTICE_ASSET = "content/catalog-notice.txt"
        const val LICENSE_ASSET_DIR = "licenses"

        fun open(context: Context): LocalStore {
            val appContext = context.applicationContext
            val database = Room.databaseBuilder(appContext, AppDatabase::class.java, DATABASE_NAME)
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                .build()
            val store = LocalStore(database, File(appContext.filesDir, "recordings"))
            store.applySeed(readSeed(appContext))
            store.sweepOrphanRecordingFiles()
            return store
        }

        fun openInMemory(context: Context): LocalStore {
            val database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
            val store = LocalStore(database, File(context.filesDir, "recordings"))
            store.applySeed(null)
            return store
        }

        internal fun readPackagedSeed(context: Context): String? {
            return readAssetText(context, SEED_ASSET)
        }

        internal fun readPackagedCatalogNotice(context: Context): String? {
            return readAssetText(context, CATALOG_NOTICE_ASSET)
        }

        internal fun readPackagedLicenseNotices(context: Context): List<LicenseNoticeOption> {
            val assets = context.applicationContext.assets
            val names = try {
                assets.list(LICENSE_ASSET_DIR)?.sorted().orEmpty()
            } catch (_: IOException) {
                emptyList()
            }
            return names.mapNotNull { name ->
                val body = if (name.endsWith(".html")) {
                    "Official legal code is packaged locally as $LICENSE_ASSET_DIR/$name."
                } else {
                    readAssetText(context, "$LICENSE_ASSET_DIR/$name") ?: return@mapNotNull null
                }
                if (body.isBlank()) return@mapNotNull null
                LicenseNoticeOption(
                    id = name,
                    title = licenseNoticeTitle(name),
                    body = body,
                )
            }
        }

        private fun licenseNoticeTitle(filename: String): String {
            return when (filename) {
                "cc-by-sa-4.0.txt" -> "CC BY-SA 4.0"
                "cc-by-3.0-us.html" -> "CC BY 3.0 US"
                "cc-by-2.0-fr.html" -> "CC BY 2.0 FR"
                "public-domain-notice.txt" -> "Public-domain mark"
                "shtooka-readme-sakhno.txt" -> "Shtooka README (Sakhno)"
                "shtooka-readme-streit.txt" -> "Shtooka README (Streit)"
                else -> filename
            }
        }

        private fun readAssetText(context: Context, path: String): String? {
            return try {
                context.applicationContext.assets.open(path).bufferedReader(Charsets.UTF_8).use { it.readText() }
            } catch (_: IOException) {
                null
            }
        }

        private fun readSeed(context: Context): String? = readPackagedSeed(context)
    }
}

internal data class NavigationBundle(
    val learner: LearnerStateEntity,
    val segments: List<CourseSegmentEntity>,
    val plans: List<PlanDefinitionEntity>,
    val instances: List<PlanInstanceEntity>,
    val recordings: List<RecordingRecordEntity>,
    val sources: List<SourceRecordEntity>,
    val texts: List<SourceTextEntity>,
    val rights: List<AssetRightsRecordEntity>,
    val allAudio: List<AudioModelEntity>,
    val eligibleAudio: List<AudioModelEntity>,
    val gates: List<GateDefinitionEntity>,
    val conditionsByGate: Map<String, List<GateConditionEntity>>,
    val tutorGateIds: Set<String>,
    val evidence: List<GateEvidenceEntity>,
    val decisions: List<GateDecisionEntity>,
    val logs: List<ActivityLogEntity>,
    val eligibleDrillCount: Int,
)
