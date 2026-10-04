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
import dev.russiancompanion.v2.backup.BACKUP_SCHEMA_VERSION
import dev.russiancompanion.v2.backup.BackupActivityLog
import dev.russiancompanion.v2.backup.BackupAttempt
import dev.russiancompanion.v2.backup.BackupBookAttestation
import dev.russiancompanion.v2.backup.BackupBookmark
import dev.russiancompanion.v2.backup.BackupDecision
import dev.russiancompanion.v2.backup.BackupEvidence
import dev.russiancompanion.v2.backup.BackupLearnerState
import dev.russiancompanion.v2.backup.BackupNote
import dev.russiancompanion.v2.backup.BackupPlanInstance
import dev.russiancompanion.v2.backup.BackupProfile
import dev.russiancompanion.v2.backup.BackupRecording
import dev.russiancompanion.v2.backup.BackupStep
import dev.russiancompanion.v2.backup.BackupTutorItem
import dev.russiancompanion.v2.backup.BackupTutorRequirement
import dev.russiancompanion.v2.backup.BackupUnitCompletion
import dev.russiancompanion.v2.backup.GovernedLearnerDocument
import dev.russiancompanion.v2.backup.GovernedReplaceOutcome
import dev.russiancompanion.v2.backup.PreservedExportRecord
import dev.russiancompanion.v2.backup.ReplacementJournal
import dev.russiancompanion.v2.backup.StagedRecording
import dev.russiancompanion.v2.backup.V2BackupFaults
import dev.russiancompanion.v2.backup.backupJson
import dev.russiancompanion.v2.data.V2AttemptEntity
import dev.russiancompanion.v2.data.V2BookAttestationEntity
import dev.russiancompanion.v2.data.V2BookmarkEntity
import dev.russiancompanion.v2.data.V2NoteEntity
import dev.russiancompanion.v2.data.V2ProfileEntity
import dev.russiancompanion.v2.data.V2StepProgressEntity
import dev.russiancompanion.v2.data.V2TutorItemEntity
import dev.russiancompanion.v2.data.V2UnitCompletionEntity
import dev.russiancompanion.domain.practice.StoredRecordingRef
import dev.russiancompanion.domain.practice.completedRecordingCountsTowardPlan
import dev.russiancompanion.domain.practice.listenCompletionAccepted
import dev.russiancompanion.domain.practice.listenPlanSatisfiedByCompletions
import dev.russiancompanion.domain.practice.recordingCommitAccepted
import java.io.File
import java.io.IOException
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext

internal class LocalStore private constructor(
    private val database: AppDatabase,
    private val recordingRoot: File? = null,
) : AutoCloseable {
    private val io = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "russian-companion-store").apply { isDaemon = true }
    }
    internal val ioDispatcher: CoroutineDispatcher = io.asCoroutineDispatcher()
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

    fun v2Profile(): dev.russiancompanion.v2.data.V2ProfileEntity = call { readV2Profile() }

    internal suspend fun v2ProfileAsync(): dev.russiancompanion.v2.data.V2ProfileEntity =
        withContext(ioDispatcher) { readV2Profile() }

    fun v2SaveProfile(profile: dev.russiancompanion.v2.data.V2ProfileEntity) = call {
        writeV2Profile(profile)
    }

    internal suspend fun v2SaveProfileAsync(profile: dev.russiancompanion.v2.data.V2ProfileEntity) =
        withContext(ioDispatcher) { writeV2Profile(profile) }

    fun v2LearnerView(): dev.russiancompanion.v2.curriculum.V2LearnerView = call { readV2Learner() }

    internal suspend fun v2LearnerViewAsync(): dev.russiancompanion.v2.curriculum.V2LearnerView {
        while (true) {
            val remaining = learnerReadFailuresRemainingForTests.get()
            if (remaining <= 0) break
            if (learnerReadFailuresRemainingForTests.compareAndSet(remaining, remaining - 1)) {
                error("learner read failed")
            }
        }
        val gate = holdNextLearnerRefreshForTests
        if (gate != null) {
            holdNextLearnerRefreshForTests = null
            learnerRefreshEnteredForTests?.countDown()
            withContext(Dispatchers.IO) { gate.await(30, TimeUnit.SECONDS) }
            if (failHeldLearnerRefreshForTests) {
                failHeldLearnerRefreshForTests = false
                error("learner read failed")
            }
        }
        return withContext(ioDispatcher) { readV2Learner() }
    }

    fun v2CompleteStep(stepId: String, recordedAtLocal: String) = call {
        writeV2Step(stepId, recordedAtLocal)
    }

    internal suspend fun v2CompleteStepAsync(stepId: String, recordedAtLocal: String, contentDigest: String) {
        asyncStepWritesForTests.incrementAndGet()
        withContext(ioDispatcher) { writeV2Step(stepId, recordedAtLocal, contentDigest) }
    }

    fun v2CompleteGovernedStep(stepId: String, contentDigest: String, recordedAtLocal: String) = call {
        writeV2Step(stepId, recordedAtLocal, contentDigest)
    }

    fun v2RecordAttempt(itemId: String, selected: String, correct: Boolean, recordedAtLocal: String) = call {
        writeV2Attempt(itemId, selected, correct, recordedAtLocal)
    }

    internal suspend fun v2RecordAttemptAsync(
        itemId: String,
        selected: String,
        correct: Boolean,
        recordedAtLocal: String,
    ) = withContext(ioDispatcher) { writeV2Attempt(itemId, selected, correct, recordedAtLocal) }

    fun v2RecordGovernedAttempt(write: dev.russiancompanion.v2.curriculum.GovernedAttemptWrite) = call {
        writeGovernedAttempt(write)
    }

    internal suspend fun v2RecordGovernedAttemptAsync(
        write: dev.russiancompanion.v2.curriculum.GovernedAttemptWrite,
    ) = withContext(ioDispatcher) { writeGovernedAttempt(write) }

    private fun readV2Profile(): dev.russiancompanion.v2.data.V2ProfileEntity {
        val dao = database.v2()
        dao.insertProfileIfAbsent(dev.russiancompanion.v2.data.V2ProfileEntity.fresh())
        return dao.profile(dev.russiancompanion.v2.data.V2ProfileEntity.LOCAL_ID)
            ?: dev.russiancompanion.v2.data.V2ProfileEntity.fresh()
    }

    private fun writeV2Profile(profile: dev.russiancompanion.v2.data.V2ProfileEntity) {
        database.v2().updateProfile(profile)
    }

    private fun readV2Learner(): dev.russiancompanion.v2.curriculum.V2LearnerView {
        val profile = readV2Profile()
        val dao = database.v2()
        val attempts = dao.attempts()
        val correct = attempts.filter { it.correct }.map { it.itemId }.toSet()
        val selections = linkedMapOf<String, String>()
        val prescribed = attempts.filter {
            it.attemptRole == dev.russiancompanion.v2.curriculum.PRESCRIBED_CHECK ||
                it.attemptRole == dev.russiancompanion.v2.curriculum.LEGACY_UNVERIFIED
        }
        prescribed.forEach { attempt -> selections[attempt.itemId] = attempt.selected }
        val steps = dao.steps()
        return dev.russiancompanion.v2.curriculum.V2LearnerView(
            onboardingCompleted = profile.onboardingCompleted,
            currentUnitNumber = profile.currentUnitNumber,
            startingPoint = dev.russiancompanion.v2.curriculum.V2StartingPoint.valueOf(profile.startingPoint),
            hasPenguin = profile.hasPenguin,
            hasAnki = profile.hasAnki,
            hasBinder = profile.hasBinder,
            hasTutor = profile.hasTutor,
            dailyMinutes = profile.dailyMinutes,
            transliterationEnabled = profile.transliterationEnabled,
            currentSessionId = profile.currentSessionId,
            currentStepIndex = profile.currentStepIndex,
            completedStepIds = steps.map { it.stepId }.toSet(),
            correctItemIds = correct,
            bookAttestedUnits = dao.bookAttestations().map { it.unitNumber }.toSet(),
            completedUnitNumbers = dao.unitCompletions().map { it.unitNumber }.toSet(),
            attemptSelections = selections,
            governedAttempts = attempts.map { it.toView() },
            governedStepDigests = steps.associate { it.stepId to it.contentDigest },
            resumeContentDigest = profile.resumeContentDigest,
            resumeIdentitySchema = profile.resumeIdentitySchema,
            bookmarks = dao.bookmarks().map { it.toView() },
            notes = dao.notes().map { it.toView() },
            tutorItems = dao.tutorQueue().map { it.toView() },
        )
    }

    private fun writeV2Step(stepId: String, recordedAtLocal: String, contentDigest: String? = null) {
        val governed = !contentDigest.isNullOrBlank() &&
            contentDigest != dev.russiancompanion.v2.curriculum.UNRECOVERABLE_GOVERNED_IDENTITY
        database.v2().upsertStep(
            dev.russiancompanion.v2.data.V2StepProgressEntity(
                stepId = stepId,
                completedAtLocal = recordedAtLocal,
                contentDigest = if (governed) contentDigest else dev.russiancompanion.v2.curriculum.UNRECOVERABLE_GOVERNED_IDENTITY,
                identitySchema = if (governed) dev.russiancompanion.v2.curriculum.GOVERNED_IDENTITY_SCHEMA else 0,
            ),
        )
    }

    private fun writeGovernedAttempt(write: dev.russiancompanion.v2.curriculum.GovernedAttemptWrite) {
        if (write.identitySchema != dev.russiancompanion.v2.curriculum.GOVERNED_IDENTITY_SCHEMA) error("unverified attempt")
        if (write.attemptRole != dev.russiancompanion.v2.curriculum.PRESCRIBED_CHECK &&
            write.attemptRole != dev.russiancompanion.v2.curriculum.OPTIONAL_PRACTICE
        ) {
            error("unverified attempt")
        }
        if (write.itemDigest == dev.russiancompanion.v2.curriculum.UNRECOVERABLE_GOVERNED_IDENTITY ||
            write.ruleDigest == dev.russiancompanion.v2.curriculum.UNRECOVERABLE_GOVERNED_IDENTITY ||
            write.unitContentDigest == dev.russiancompanion.v2.curriculum.UNRECOVERABLE_GOVERNED_IDENTITY ||
            write.ruleDigest != dev.russiancompanion.v2.curriculum.closedResponseRuleDigest()
        ) {
            error("unverified attempt")
        }
        val duplicate = database.v2().attempts().any { existing ->
            existing.itemId == write.itemId &&
                existing.attemptRole == write.attemptRole &&
                existing.stepId == write.stepId &&
                existing.selected == write.selected &&
                existing.itemDigest == write.itemDigest &&
                existing.ruleDigest == write.ruleDigest &&
                existing.unitContentDigest == write.unitContentDigest
        }
        if (duplicate) return
        database.v2().insertAttempt(
            dev.russiancompanion.v2.data.V2AttemptEntity(
                id = write.id,
                itemId = write.itemId,
                selected = write.selected,
                correct = write.correct,
                recordedAtLocal = write.recordedAtLocal,
                itemDigest = write.itemDigest,
                unitContentDigest = write.unitContentDigest,
                ruleDigest = write.ruleDigest,
                identitySchema = write.identitySchema,
                attemptRole = write.attemptRole,
                objectiveId = write.objectiveId,
                unitNumber = write.unitNumber,
                stepId = write.stepId,
            ),
        )
    }

    private fun writeV2Attempt(itemId: String, selected: String, correct: Boolean, recordedAtLocal: String) {
        database.v2().insertAttempt(
            dev.russiancompanion.v2.data.V2AttemptEntity(
                id = "$itemId:$recordedAtLocal:${selected.hashCode()}",
                itemId = itemId,
                selected = selected,
                correct = correct,
                recordedAtLocal = recordedAtLocal,
            ),
        )
    }

    fun v2ToggleBookmark(
        targetId: String,
        kind: String,
        recordedAtLocal: String,
        targetDigest: String = dev.russiancompanion.v2.curriculum.UNRECOVERABLE_GOVERNED_IDENTITY,
        identitySchema: Int = 0,
    ): Boolean = call {
        val existing = database.v2().bookmarks().any { it.targetId == targetId }
        if (existing) {
            database.v2().deleteBookmark(targetId)
            false
        } else if (!acceptedIdentity(identitySchema, targetDigest)) {
            false
        } else {
            database.v2().upsertBookmark(
                dev.russiancompanion.v2.data.V2BookmarkEntity(
                    targetId = targetId,
                    kind = kind,
                    createdAtLocal = recordedAtLocal,
                    targetDigest = storedDigest(identitySchema, targetDigest),
                    identitySchema = identitySchema,
                ),
            )
            true
        }
    }

    fun v2AddNote(
        targetId: String,
        text: String,
        recordedAtLocal: String,
        targetDigest: String = dev.russiancompanion.v2.curriculum.UNRECOVERABLE_GOVERNED_IDENTITY,
        identitySchema: Int = 0,
    ) = call {
        if (!acceptedIdentity(identitySchema, targetDigest)) return@call
        database.v2().insertNote(
            dev.russiancompanion.v2.data.V2NoteEntity(
                id = "$targetId:$recordedAtLocal",
                targetId = targetId,
                text = text,
                createdAtLocal = recordedAtLocal,
                userAuthoredRussian = false,
                targetDigest = storedDigest(identitySchema, targetDigest),
                identitySchema = identitySchema,
            ),
        )
    }

    fun v2AddTutorItem(
        targetId: String,
        note: String,
        recordedAtLocal: String,
        targetDigest: String = dev.russiancompanion.v2.curriculum.UNRECOVERABLE_GOVERNED_IDENTITY,
        identitySchema: Int = 0,
    ) = call {
        if (!acceptedIdentity(identitySchema, targetDigest)) return@call
        database.v2().insertTutorItem(
            dev.russiancompanion.v2.data.V2TutorItemEntity(
                id = "$targetId:$recordedAtLocal",
                targetId = targetId,
                note = note,
                createdAtLocal = recordedAtLocal,
                targetDigest = storedDigest(identitySchema, targetDigest),
                identitySchema = identitySchema,
            ),
        )
    }

    private fun acceptedIdentity(schema: Int, digest: String): Boolean {
        if (schema == 0) return true
        return schema == dev.russiancompanion.v2.curriculum.GOVERNED_IDENTITY_SCHEMA &&
            digest.isNotBlank() &&
            digest != dev.russiancompanion.v2.curriculum.UNRECOVERABLE_GOVERNED_IDENTITY
    }

    private fun storedDigest(schema: Int, digest: String): String {
        return if (schema == dev.russiancompanion.v2.curriculum.GOVERNED_IDENTITY_SCHEMA) {
            digest
        } else {
            dev.russiancompanion.v2.curriculum.UNRECOVERABLE_GOVERNED_IDENTITY
        }
    }

    private fun dev.russiancompanion.v2.data.V2AttemptEntity.toView() = dev.russiancompanion.v2.curriculum.GovernedAttemptView(
        id = id,
        itemId = itemId,
        selected = selected,
        correct = correct,
        recordedAtLocal = recordedAtLocal,
        itemDigest = itemDigest,
        unitContentDigest = unitContentDigest,
        ruleDigest = ruleDigest,
        identitySchema = identitySchema,
        attemptRole = attemptRole,
        objectiveId = objectiveId,
        unitNumber = unitNumber,
        stepId = stepId,
    )

    private fun dev.russiancompanion.v2.data.V2BookmarkEntity.toView() = dev.russiancompanion.v2.curriculum.GovernedBookmarkView(
        targetId = targetId,
        kind = kind,
        createdAtLocal = createdAtLocal,
        targetDigest = targetDigest,
        identitySchema = identitySchema,
    )

    private fun dev.russiancompanion.v2.data.V2NoteEntity.toView() = dev.russiancompanion.v2.curriculum.GovernedNoteView(
        id = id,
        targetId = targetId,
        text = text,
        createdAtLocal = createdAtLocal,
        targetDigest = targetDigest,
        identitySchema = identitySchema,
    )

    private fun dev.russiancompanion.v2.data.V2TutorItemEntity.toView() = dev.russiancompanion.v2.curriculum.GovernedTutorView(
        id = id,
        targetId = targetId,
        note = note,
        createdAtLocal = createdAtLocal,
        targetDigest = targetDigest,
        identitySchema = identitySchema,
    )

    fun v2AttestBook(unitNumber: Int, recordedAtLocal: String) = call {
        database.v2().upsertBookAttestation(
            dev.russiancompanion.v2.data.V2BookAttestationEntity(unitNumber, recordedAtLocal),
        )
    }

    fun v2CompleteUnit(unitNumber: Int, recordedAtLocal: String) = call {
        database.v2().upsertUnitCompletion(
            dev.russiancompanion.v2.data.V2UnitCompletionEntity(unitNumber, recordedAtLocal),
        )
    }

    /**
     * Test seam. When set, the next advancement transaction inserts the unit
     * completion and then throws before the profile write, so the transaction
     * rolls both back.
     */
    @Volatile
    internal var failAdvanceAfterCompletionForTests: Boolean = false

    internal suspend fun v2AdvanceUnitAsync(
        unitNumber: Int,
        recordedAtLocal: String,
        nextUnitNumber: Int,
        nextSessionId: String?,
    ) = withContext(ioDispatcher) {
        database.runInTransaction {
            database.v2().upsertUnitCompletion(
                dev.russiancompanion.v2.data.V2UnitCompletionEntity(unitNumber, recordedAtLocal),
            )
            if (failAdvanceAfterCompletionForTests) {
                failAdvanceAfterCompletionForTests = false
                error("profile write failed")
            }
            val profile = readV2Profile()
            writeV2Profile(
                profile.copy(
                    currentUnitNumber = nextUnitNumber,
                    currentSessionId = nextSessionId,
                    currentStepIndex = 0,
                ),
            )
        }
    }

    fun v2Attempts(): List<dev.russiancompanion.v2.data.V2AttemptEntity> = call { database.v2().attempts() }

    fun v2StepProgress(): List<dev.russiancompanion.v2.data.V2StepProgressEntity> = call { database.v2().steps() }

    fun v2Bookmarks(): List<dev.russiancompanion.v2.data.V2BookmarkEntity> = call { database.v2().bookmarks() }

    fun v2Notes(): List<dev.russiancompanion.v2.data.V2NoteEntity> = call { database.v2().notes() }

    fun v2TutorQueue(): List<dev.russiancompanion.v2.data.V2TutorItemEntity> = call { database.v2().tutorQueue() }

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
        recoverInterruptedReplacementLocked()
        val root = recordingRoot ?: return
        if (!root.isDirectory) return
        val referenced = database.users().recordings().map { File(it.localFileReference).name }.toSet()
        root.listFiles()?.forEach { file ->
            if (file.isFile && file.name !in referenced) {
                file.delete()
            }
        }
    }

    internal fun governedLearnerDocument(): GovernedLearnerDocument = call { readGovernedLearnerDocument() }

    internal fun governedLearnerFingerprint(): String = call {
        backupJson.encodeToString(GovernedLearnerDocument.serializer(), readGovernedLearnerDocument())
    }

    internal fun recordingDirectory(): File? = recordingRoot

    internal fun recordingFile(reference: String): File? {
        val root = recordingRoot ?: return null
        if (reference.isBlank() || reference.contains("..") || reference.startsWith("/")) return null
        val target = File(root, File(reference).name)
        return if (target.isFile && target.length() > 0L) target else null
    }

    internal fun replaceGovernedLearner(
        document: GovernedLearnerDocument,
        recordings: List<StagedRecording>,
    ): GovernedReplaceOutcome = call {
        if (!referencesResolve(document)) return@call GovernedReplaceOutcome.UNCHANGED
        if (V2BackupFaults.failBeforeMutation) return@call GovernedReplaceOutcome.UNCHANGED
        val root = recordingRoot
        if (root == null) {
            if (document.recordings.isNotEmpty() || recordings.isNotEmpty()) return@call GovernedReplaceOutcome.UNCHANGED
            return@call replaceDocumentOnly(document)
        }
        if (!root.exists() && !root.mkdirs()) return@call GovernedReplaceOutcome.UNCHANGED
        recoverInterruptedReplacementLocked()
        if (replacementPending(root)) return@call GovernedReplaceOutcome.RECOVERY_REQUIRED
        val workspace = replacementWorkspace(root)
        val snapshotDir = File(workspace, "snapshot")
        workspace.deleteRecursively()
        if (!snapshotDir.mkdirs()) return@call GovernedReplaceOutcome.UNCHANGED
        val previous = readGovernedLearnerDocument()
        val previousExports = database.users().exports()
        if (!copyRecordingFiles(root, snapshotDir)) {
            workspace.deleteRecursively()
            return@call GovernedReplaceOutcome.UNCHANGED
        }
        val staged = try {
            stageIncomingRecordings(document, recordings, File(workspace, "staged"))
        } catch (_: IOException) {
            workspace.deleteRecursively()
            return@call GovernedReplaceOutcome.UNCHANGED
        }
        if (!publishStagedRecordings(root, staged)) {
            return@call rollbackReplacementFiles(root, snapshotDir, workspace)
        }
        if (!writeReplacementJournal(root, previous, document, previousExports, snapshotDir)) {
            return@call rollbackReplacementFiles(root, snapshotDir, workspace)
        }
        if (V2BackupFaults.crashAfterFileSwapBeforeCommit) {
            return@call GovernedReplaceOutcome.RECOVERY_REQUIRED
        }
        val committed = try {
            database.runInTransaction {
                applyGovernedDocument(document)
                if (V2BackupFaults.failInsideReplacement) {
                    throw IllegalStateException("replacement-interrupted")
                }
            }
            true
        } catch (_: Exception) {
            false
        }
        if (!committed || V2BackupFaults.failAfterCommit) {
            if (committed && !reapplyPreviousDocument(previous, previousExports)) {
                return@call GovernedReplaceOutcome.RECOVERY_REQUIRED
            }
            return@call rollbackReplacementFiles(root, snapshotDir, workspace)
        }
        if (!markReplacementJournalCommitted(root)) {
            if (!reapplyPreviousDocument(previous, previousExports)) {
                return@call GovernedReplaceOutcome.RECOVERY_REQUIRED
            }
            return@call rollbackReplacementFiles(root, snapshotDir, workspace)
        }
        deleteRetiredRecordings(root, staged.map { it.first }.toSet())
        if (swapArtifactsRemain(root)) return@call GovernedReplaceOutcome.RECOVERY_REQUIRED
        deleteReplacementJournal(root)
        workspace.deleteRecursively()
        GovernedReplaceOutcome.REPLACED
    }

    private fun replaceDocumentOnly(document: GovernedLearnerDocument): GovernedReplaceOutcome {
        return try {
            database.runInTransaction {
                applyGovernedDocument(document)
                if (V2BackupFaults.failInsideReplacement || V2BackupFaults.failAfterCommit) {
                    throw IllegalStateException("replacement-interrupted")
                }
            }
            GovernedReplaceOutcome.REPLACED
        } catch (_: Exception) {
            GovernedReplaceOutcome.UNCHANGED
        }
    }

    private fun referencesResolve(document: GovernedLearnerDocument): Boolean {
        if (document.profile.id != V2ProfileEntity.LOCAL_ID) return false
        val bundled = database.bundled()
        val learnerSegment = document.learnerState?.activeSegmentId
        if (learnerSegment != null && bundled.segment(learnerSegment) == null) return false
        val planIds = document.planInstances.map { it.id }.toSet()
        val activityIds = document.activityLogs.map { it.id }.toSet()
        if (document.planInstances.any { bundled.segment(it.segmentId) == null || bundled.plan(it.planDefinitionId) == null }) {
            return false
        }
        if (document.activityLogs.any { log ->
                (log.segmentId != null && bundled.segment(log.segmentId) == null) ||
                    (log.planInstanceId != null && log.planInstanceId !in planIds)
            }
        ) {
            return false
        }
        if (document.evidence.any { bundled.gate(it.gateDefinitionId) == null || !known<EvidenceType>(it.evidenceType) || !known<SelfAttestationStatus>(it.selfAttestationStatus) }) {
            return false
        }
        if (document.tutorRequirements.any { bundled.gate(it.gateDefinitionId) == null }) return false
        if (document.decisions.any { bundled.gate(it.gateDefinitionId) == null || !known<GateDecisionStatus>(it.status) }) {
            return false
        }
        if (document.activityLogs.any { !known<ActivityCategory>(it.category) }) return false
        if (document.planInstances.any { !known<CompletionState>(it.completionState) }) return false
        if (document.recordings.any { recording ->
                bundled.audio(recording.audioModelId) == null ||
                    !known<CompletionState>(recording.completionState) ||
                    (recording.activityLogId != null && recording.activityLogId !in activityIds) ||
                    recording.localFileReference.isBlank() ||
                    recording.localFileReference.contains("..") ||
                    recording.localFileReference.startsWith("/")
            }
        ) {
            return false
        }
        return true
    }

    private fun applyGovernedDocument(document: GovernedLearnerDocument) {
        val users = database.users()
        val curriculum = database.v2()
        users.deleteAllRecordings()
        users.deleteAllDecisions()
        users.deleteAllEvidence()
        users.deleteAllTutorRequirements()
        users.deleteAllActivityLogs()
        users.deleteAllPlanInstances()
        users.deleteLearnerState()
        users.deleteAllExports()
        curriculum.deleteAttempts()
        curriculum.deleteSteps()
        curriculum.deleteBookmarks()
        curriculum.deleteNotes()
        curriculum.deleteTutorQueue()
        curriculum.deleteBookAttestations()
        curriculum.deleteUnitCompletions()
        curriculum.deleteProfiles()
        document.learnerState?.let { state ->
            users.insertLearnerIfAbsent(
                LearnerStateEntity(
                    id = state.id,
                    activeSegmentId = state.activeSegmentId,
                    onboardingCompleted = state.onboardingCompleted,
                    planContext = state.planContext,
                ),
            )
        }
        document.planInstances.forEach { plan ->
            users.upsertPlanInstance(
                PlanInstanceEntity(
                    id = plan.id,
                    localDate = plan.localDate,
                    segmentId = plan.segmentId,
                    planDefinitionId = plan.planDefinitionId,
                    completionState = CompletionState.valueOf(plan.completionState),
                    completedAtLocal = plan.completedAtLocal,
                ),
            )
        }
        document.activityLogs.forEach { log ->
            users.insertActivityLog(
                ActivityLogEntity(
                    id = log.id,
                    category = ActivityCategory.valueOf(log.category),
                    durationMinutes = log.durationMinutes,
                    completed = log.completed,
                    recordedAtLocal = log.recordedAtLocal,
                    segmentId = log.segmentId,
                    planInstanceId = log.planInstanceId,
                    selfAttested = log.selfAttested,
                ),
            )
        }
        document.evidence.forEach { evidence ->
            users.insertEvidence(
                GateEvidenceEntity(
                    id = evidence.id,
                    gateDefinitionId = evidence.gateDefinitionId,
                    evidenceType = EvidenceType.valueOf(evidence.evidenceType),
                    enteredValue = evidence.enteredValue,
                    selfAttestationStatus = SelfAttestationStatus.valueOf(evidence.selfAttestationStatus),
                    recordedAtLocal = evidence.recordedAtLocal,
                ),
            )
        }
        document.tutorRequirements.forEach { requirement ->
            users.upsertTutorRequirement(
                GateTutorRequirementEntity(
                    gateDefinitionId = requirement.gateDefinitionId,
                    recordedAtLocal = requirement.recordedAtLocal,
                ),
            )
        }
        document.decisions.forEach { decision ->
            users.insertDecision(
                GateDecisionEntity(
                    id = decision.id,
                    gateDefinitionId = decision.gateDefinitionId,
                    ruleVersion = decision.ruleVersion,
                    ruleSnapshot = decision.ruleSnapshot,
                    evidenceIds = decision.evidenceIds,
                    conditionOutcomes = decision.conditionOutcomes,
                    status = GateDecisionStatus.valueOf(decision.status),
                    evaluatedAtLocal = decision.evaluatedAtLocal,
                ),
            )
        }
        document.recordings.forEach { recording ->
            users.insertRecording(
                RecordingRecordEntity(
                    id = recording.id,
                    audioModelId = recording.audioModelId,
                    localFileReference = recording.localFileReference,
                    createdAtLocal = recording.createdAtLocal,
                    markedForTutorReview = recording.markedForTutorReview,
                    completionState = CompletionState.valueOf(recording.completionState),
                    activityLogId = recording.activityLogId,
                ),
            )
        }
        curriculum.upsertProfile(document.profile.toEntity())
        document.steps.forEach {
            curriculum.upsertStep(
                V2StepProgressEntity(
                    stepId = it.stepId,
                    completedAtLocal = it.completedAtLocal,
                    contentDigest = it.contentDigest,
                    identitySchema = it.identitySchema,
                ),
            )
        }
        document.attempts.forEach {
            curriculum.insertAttempt(
                V2AttemptEntity(
                    id = it.id,
                    itemId = it.itemId,
                    selected = it.selected,
                    correct = it.correct,
                    recordedAtLocal = it.recordedAtLocal,
                    itemDigest = it.itemDigest,
                    unitContentDigest = it.unitContentDigest,
                    ruleDigest = it.ruleDigest,
                    identitySchema = it.identitySchema,
                    attemptRole = it.attemptRole,
                    objectiveId = it.objectiveId,
                    unitNumber = it.unitNumber,
                    stepId = it.stepId,
                ),
            )
        }
        document.bookmarks.forEach {
            curriculum.upsertBookmark(
                V2BookmarkEntity(
                    targetId = it.targetId,
                    kind = it.kind,
                    createdAtLocal = it.createdAtLocal,
                    targetDigest = it.targetDigest,
                    identitySchema = it.identitySchema,
                ),
            )
        }
        document.notes.forEach {
            curriculum.insertNote(
                V2NoteEntity(
                    id = it.id,
                    targetId = it.targetId,
                    text = it.text,
                    createdAtLocal = it.createdAtLocal,
                    userAuthoredRussian = it.userAuthoredRussian,
                    targetDigest = it.targetDigest,
                    identitySchema = it.identitySchema,
                ),
            )
        }
        document.tutorQueue.forEach {
            curriculum.insertTutorItem(
                V2TutorItemEntity(
                    id = it.id,
                    targetId = it.targetId,
                    note = it.note,
                    createdAtLocal = it.createdAtLocal,
                    targetDigest = it.targetDigest,
                    identitySchema = it.identitySchema,
                ),
            )
        }
        document.bookAttestations.forEach { curriculum.upsertBookAttestation(V2BookAttestationEntity(it.unitNumber, it.recordedAtLocal)) }
        document.unitCompletions.forEach { curriculum.upsertUnitCompletion(V2UnitCompletionEntity(it.unitNumber, it.recordedAtLocal)) }
    }

    private fun BackupProfile.toEntity(): V2ProfileEntity {
        return V2ProfileEntity(
            id = id,
            onboardingCompleted = onboardingCompleted,
            currentUnitNumber = currentUnitNumber,
            startingPoint = startingPoint,
            hasPenguin = hasPenguin,
            hasAnki = hasAnki,
            hasBinder = hasBinder,
            hasTutor = hasTutor,
            dailyMinutes = dailyMinutes,
            transliterationEnabled = transliterationEnabled,
            currentSessionId = currentSessionId,
            currentStepIndex = currentStepIndex,
            resumeContentDigest = resumeContentDigest,
            resumeIdentitySchema = resumeIdentitySchema,
        )
    }

    private inline fun <reified T : Enum<T>> known(value: String): Boolean {
        return enumValues<T>().any { it.name == value }
    }

    private fun replacementWorkspace(root: File): File {
        return File(root.parentFile, "backup-replace-${root.name}-workspace")
    }

    private fun replacementJournalFile(root: File): File {
        return File(root.parentFile, "backup-replace-${root.name}.json")
    }

    private fun stageIncomingRecordings(
        document: GovernedLearnerDocument,
        recordings: List<StagedRecording>,
        stageDir: File,
    ): List<Pair<String, File>> {
        if (!stageDir.isDirectory && !stageDir.mkdirs()) throw IOException("stage-dir")
        val byName = recordings.associateBy { File(it.recording.localFileReference).name }
        if (byName.size != recordings.size) throw IOException("duplicate-recording")
        val names = document.recordings.map { File(it.localFileReference).name }
        if (names.size != names.toSet().size || names.toSet() != byName.keys) throw IOException("recording-set")
        return names.map { name ->
            val source = byName.getValue(name).file
            if (!source.isFile || source.length() <= 0L) throw IOException("missing-bytes")
            val dest = File(stageDir, name)
            copyFile(source, dest)
            if (dest.length() != source.length()) throw IOException("short-stage")
            name to dest
        }
    }

    private fun publishStagedRecordings(root: File, staged: List<Pair<String, File>>): Boolean {
        var replaced = 0
        return try {
            staged.forEach { (name, source) ->
                val incoming = File(root, "$name.incoming")
                copyFile(source, incoming)
                if (incoming.length() != source.length()) throw IOException("short-copy")
            }
            staged.forEach { (name, _) ->
                val live = File(root, name)
                val retired = File(root, "$name.retired")
                if (retired.exists() && !retired.delete()) throw IOException("retire-clear")
                if (live.exists() && !live.renameTo(retired)) throw IOException("retire-failed")
                val incoming = File(root, "$name.incoming")
                if (!incoming.renameTo(live)) throw IOException("promote-failed")
                replaced += 1
                if (V2BackupFaults.failDuringFilePublish && replaced == 1) {
                    throw IOException("publish-interrupted")
                }
            }
            true
        } catch (_: IOException) {
            false
        }
    }

    private fun copyRecordingFiles(root: File, snapshotDir: File): Boolean {
        val files = root.listFiles()?.filter { file ->
            file.isFile && !file.name.endsWith(".incoming") && !file.name.endsWith(".retired")
        }.orEmpty()
        return try {
            files.forEach { file ->
                val dest = File(snapshotDir, file.name)
                copyFile(file, dest)
                if (dest.length() != file.length()) throw IOException("short-snapshot")
            }
            true
        } catch (_: IOException) {
            false
        }
    }

    private fun restoreRecordingSnapshot(root: File, snapshotDir: File): Boolean {
        if (V2BackupFaults.failSnapshotRestore) return false
        return try {
            val snap = snapshotDir.listFiles()?.filter { it.isFile }.orEmpty()
            val keep = snap.map { it.name }.toSet()
            root.listFiles()?.forEach { file ->
                if (file.isFile && (file.name.endsWith(".incoming") || file.name.endsWith(".retired"))) {
                    file.delete()
                }
            }
            snap.forEach { source ->
                val incoming = File(root, "${source.name}.incoming")
                copyFile(source, incoming)
                if (incoming.length() != source.length()) throw IOException("short-restore")
                val live = File(root, source.name)
                if (live.exists() && !live.delete()) throw IOException("restore-replace")
                if (!incoming.renameTo(live)) throw IOException("restore-rename")
            }
            root.listFiles()?.forEach { file ->
                if (file.isFile && file.name !in keep) file.delete()
            }
            true
        } catch (_: IOException) {
            false
        }
    }

    private fun deleteRetiredRecordings(root: File, keep: Set<String>) {
        root.listFiles()?.forEach { file ->
            if (!file.isFile) return@forEach
            if (file.name.endsWith(".retired") || file.name.endsWith(".incoming") || file.name !in keep) {
                file.delete()
            }
        }
    }

    private fun writeReplacementJournal(
        root: File,
        previous: GovernedLearnerDocument,
        next: GovernedLearnerDocument,
        exports: List<ExportRecordEntity>,
        snapshotDir: File,
    ): Boolean {
        if (V2BackupFaults.failJournalWrite) return false
        val journal = ReplacementJournal(
            phase = "FILES_SWAPPED",
            previousFingerprint = documentFingerprint(previous),
            nextFingerprint = documentFingerprint(next),
            snapshotDir = snapshotDir.absolutePath,
            previousDocument = backupJson.encodeToString(GovernedLearnerDocument.serializer(), previous),
            exports = exports.map { record ->
                PreservedExportRecord(
                    id = record.id,
                    exportedAtLocal = record.exportedAtLocal,
                    destinationReference = record.destinationReference,
                    logCount = record.logCount,
                    evidenceCount = record.evidenceCount,
                    progressCount = record.progressCount,
                    recordingCount = record.recordingCount,
                    status = record.status.name,
                    documentReference = record.documentReference,
                    recordingSetDigest = record.recordingSetDigest,
                )
            },
        )
        return writeJournalAtomically(replacementJournalFile(root), journal)
    }

    private fun markReplacementJournalCommitted(root: File): Boolean {
        val journalFile = replacementJournalFile(root)
        if (!journalFile.isFile) return false
        return try {
            val journal = backupJson.decodeFromString(ReplacementJournal.serializer(), journalFile.readText())
            writeJournalAtomically(journalFile, journal.copy(phase = "COMMITTED"))
        } catch (_: Exception) {
            false
        }
    }

    private fun writeJournalAtomically(dest: File, journal: ReplacementJournal): Boolean {
        val temp = File(dest.parentFile, "${dest.name}.partial")
        return try {
            temp.writeText(backupJson.encodeToString(ReplacementJournal.serializer(), journal))
            if (temp.renameTo(dest)) return true
            val previous = File(dest.parentFile, "${dest.name}.previous")
            if (!dest.isFile || !dest.renameTo(previous)) {
                temp.delete()
                return false
            }
            if (!temp.renameTo(dest)) {
                previous.renameTo(dest)
                temp.delete()
                return false
            }
            previous.delete()
            true
        } catch (_: IOException) {
            temp.delete()
            false
        }
    }

    private fun deleteReplacementJournal(root: File) {
        replacementJournalFile(root).delete()
        File(replacementJournalFile(root).parentFile, "${replacementJournalFile(root).name}.partial").delete()
    }

    private fun rollbackReplacementFiles(
        root: File,
        snapshotDir: File,
        workspace: File,
    ): GovernedReplaceOutcome {
        if (!restoreRecordingSnapshot(root, snapshotDir)) return GovernedReplaceOutcome.RECOVERY_REQUIRED
        deleteReplacementJournal(root)
        workspace.deleteRecursively()
        return GovernedReplaceOutcome.UNCHANGED
    }

    private fun reapplyPreviousDocument(
        previous: GovernedLearnerDocument,
        exports: List<ExportRecordEntity>,
    ): Boolean {
        return try {
            database.runInTransaction {
                applyGovernedDocument(previous)
                exports.forEach { database.users().insertExport(it) }
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun replacementPending(root: File): Boolean {
        if (replacementJournalFile(root).isFile) return true
        if (replacementWorkspace(root).exists()) return true
        return swapArtifactsRemain(root)
    }

    private fun swapArtifactsRemain(root: File): Boolean {
        return root.listFiles()?.any { file ->
            file.isFile && (file.name.endsWith(".retired") || file.name.endsWith(".incoming"))
        } == true
    }

    private fun recoverInterruptedReplacementLocked() {
        val root = recordingRoot ?: return
        val journalFile = replacementJournalFile(root)
        val workspace = replacementWorkspace(root)
        if (!journalFile.isFile) {
            val snapshotDir = File(workspace, "snapshot")
            if (snapshotDir.isDirectory) {
                if (restoreSnapshot(root, snapshotDir)) workspace.deleteRecursively()
            } else {
                undoOrphanedSwapArtifacts(root)
            }
            return
        }
        val journal = try {
            backupJson.decodeFromString(ReplacementJournal.serializer(), journalFile.readText())
        } catch (_: Exception) {
            if (restoreSnapshot(root, File(workspace, "snapshot"))) {
                journalFile.delete()
                workspace.deleteRecursively()
            }
            return
        }
        if (journal.phase == "COMMITTED") {
            deleteRetiredRecordings(root, readGovernedLearnerDocument().recordings.map { File(it.localFileReference).name }.toSet())
            if (swapArtifactsRemain(root)) return
            journalFile.delete()
            workspace.deleteRecursively()
            return
        }
        val current = documentFingerprint(readGovernedLearnerDocument())
        if (current != journal.previousFingerprint) {
            try {
                val previous = backupJson.decodeFromString(GovernedLearnerDocument.serializer(), journal.previousDocument)
                database.runInTransaction {
                    applyGovernedDocument(previous)
                    journal.exports.forEach { database.users().insertExport(preservedExport(it)) }
                }
            } catch (_: Exception) {
                return
            }
        }
        if (!restoreSnapshot(root, File(journal.snapshotDir))) return
        journalFile.delete()
        workspace.deleteRecursively()
    }

    private fun restoreSnapshot(root: File, snapshotDir: File): Boolean {
        if (!snapshotDir.isDirectory) return false
        return restoreRecordingSnapshot(root, snapshotDir)
    }

    private fun preservedExport(record: PreservedExportRecord): ExportRecordEntity {
        return ExportRecordEntity(
            id = record.id,
            exportedAtLocal = record.exportedAtLocal,
            destinationReference = record.destinationReference,
            logCount = record.logCount,
            evidenceCount = record.evidenceCount,
            progressCount = record.progressCount,
            recordingCount = record.recordingCount,
            status = ExportStatus.valueOf(record.status),
            documentReference = record.documentReference,
            recordingSetDigest = record.recordingSetDigest,
        )
    }

    private fun undoOrphanedSwapArtifacts(root: File) {
        root.listFiles()?.filter { it.isFile && it.name.endsWith(".retired") }?.forEach { retired ->
            val live = File(root, retired.name.removeSuffix(".retired"))
            if (live.exists()) live.delete()
            retired.renameTo(live)
        }
        root.listFiles()?.filter { it.isFile && it.name.endsWith(".incoming") }?.forEach { it.delete() }
    }

    private fun documentFingerprint(document: GovernedLearnerDocument): String {
        return backupJson.encodeToString(GovernedLearnerDocument.serializer(), document)
    }

    private fun copyFile(source: File, dest: File) {
        dest.parentFile?.mkdirs()
        source.inputStream().use { input -> dest.outputStream().use { output -> input.copyTo(output) } }
    }

    private fun readGovernedLearnerDocument(): GovernedLearnerDocument {
        val curriculum = database.v2()
        val users = database.users()
        val profile = readV2Profile()
        return GovernedLearnerDocument(
            schemaVersion = BACKUP_SCHEMA_VERSION,
            profile = BackupProfile(
                id = profile.id,
                onboardingCompleted = profile.onboardingCompleted,
                currentUnitNumber = profile.currentUnitNumber,
                startingPoint = profile.startingPoint,
                hasPenguin = profile.hasPenguin,
                hasAnki = profile.hasAnki,
                hasBinder = profile.hasBinder,
                hasTutor = profile.hasTutor,
                dailyMinutes = profile.dailyMinutes,
                transliterationEnabled = profile.transliterationEnabled,
                currentSessionId = profile.currentSessionId,
                currentStepIndex = profile.currentStepIndex,
                resumeContentDigest = profile.resumeContentDigest,
                resumeIdentitySchema = profile.resumeIdentitySchema,
            ),
            steps = curriculum.steps().sortedBy { it.stepId }.map {
                BackupStep(it.stepId, it.completedAtLocal, it.contentDigest, it.identitySchema)
            },
            attempts = curriculum.attempts().map {
                BackupAttempt(
                    id = it.id,
                    itemId = it.itemId,
                    selected = it.selected,
                    correct = it.correct,
                    recordedAtLocal = it.recordedAtLocal,
                    itemDigest = it.itemDigest,
                    unitContentDigest = it.unitContentDigest,
                    ruleDigest = it.ruleDigest,
                    identitySchema = it.identitySchema,
                    attemptRole = it.attemptRole,
                    objectiveId = it.objectiveId,
                    unitNumber = it.unitNumber,
                    stepId = it.stepId,
                )
            },
            bookmarks = curriculum.bookmarks().map {
                BackupBookmark(it.targetId, it.kind, it.createdAtLocal, it.targetDigest, it.identitySchema)
            },
            notes = curriculum.notes().map {
                BackupNote(
                    it.id,
                    it.targetId,
                    it.text,
                    it.createdAtLocal,
                    it.userAuthoredRussian,
                    it.targetDigest,
                    it.identitySchema,
                )
            },
            tutorQueue = curriculum.tutorQueue().map {
                BackupTutorItem(it.id, it.targetId, it.note, it.createdAtLocal, it.targetDigest, it.identitySchema)
            },
            bookAttestations = curriculum.bookAttestations().sortedBy { it.unitNumber }.map {
                BackupBookAttestation(it.unitNumber, it.recordedAtLocal)
            },
            unitCompletions = curriculum.unitCompletions().sortedBy { it.unitNumber }.map {
                BackupUnitCompletion(it.unitNumber, it.recordedAtLocal)
            },
            learnerState = users.learner(LearnerStateEntity.LOCAL_ID)?.let {
                BackupLearnerState(it.id, it.activeSegmentId, it.onboardingCompleted, it.planContext)
            },
            planInstances = users.planInstances().map {
                BackupPlanInstance(it.id, it.localDate, it.segmentId, it.planDefinitionId, it.completionState.name, it.completedAtLocal)
            },
            activityLogs = users.activityLogs().map {
                BackupActivityLog(
                    it.id,
                    it.category.name,
                    it.durationMinutes,
                    it.completed,
                    it.recordedAtLocal,
                    it.segmentId,
                    it.planInstanceId,
                    it.selfAttested,
                )
            },
            evidence = users.allEvidence().map {
                BackupEvidence(it.id, it.gateDefinitionId, it.evidenceType.name, it.enteredValue, it.selfAttestationStatus.name, it.recordedAtLocal)
            },
            tutorRequirements = users.allTutorRequirements().map { BackupTutorRequirement(it.gateDefinitionId, it.recordedAtLocal) },
            decisions = users.decisions().map {
                BackupDecision(
                    it.id,
                    it.gateDefinitionId,
                    it.ruleVersion,
                    it.ruleSnapshot,
                    it.evidenceIds,
                    it.conditionOutcomes,
                    it.status.name,
                    it.evaluatedAtLocal,
                )
            },
            recordings = users.recordings().map {
                BackupRecording(
                    it.id,
                    it.audioModelId,
                    it.localFileReference,
                    it.createdAtLocal,
                    it.markedForTutorReview,
                    it.completionState.name,
                    it.activityLogId,
                )
            },
        )
    }

    internal suspend fun <T> awaitStore(block: () -> T): T = withContext(ioDispatcher) { block() }

    private fun <T> call(block: () -> T): T {
        if (Thread.currentThread().name.startsWith("russian-companion-store")) {
            return block()
        }
        try {
            return io.submit(Callable(block)).get()
        } catch (error: ExecutionException) {
            throw error.cause ?: error
        }
    }

    companion object {
        /**
         * Test seam. The next this many asynchronous learner reads fail, including
         * reads on a store opened later by retry. Synchronous test reads are unaffected.
         */
        val learnerReadFailuresRemainingForTests = AtomicInteger(0)

        /**
         * Test seam. The next asynchronous learner read waits on this latch after
         * leaving the store thread, then continues. One shot.
         */
        @Volatile
        var holdNextLearnerRefreshForTests: CountDownLatch? = null

        @Volatile
        var learnerRefreshEnteredForTests: CountDownLatch? = null

        /**
         * Test seam. After a held asynchronous learner read is released, that read
         * fails once. Reads that did not take the hold are unaffected.
         */
        @Volatile
        var failHeldLearnerRefreshForTests: Boolean = false

        val asyncStepWritesForTests = AtomicInteger(0)

        const val DATABASE_NAME = "russian-companion.db"
        const val SEED_ASSET = "content/bundled-content.json"
        const val CATALOG_NOTICE_ASSET = "content/catalog-notice.txt"
        const val LICENSE_ASSET_DIR = "licenses"

        fun open(context: Context): LocalStore {
            val appContext = context.applicationContext
            val database = Room.databaseBuilder(appContext, AppDatabase::class.java, DATABASE_NAME)
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8)
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

        fun openNamed(context: Context, name: String): LocalStore {
            val appContext = context.applicationContext
            val database = Room.databaseBuilder(appContext, AppDatabase::class.java, name)
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8)
                .build()
            val store = LocalStore(database, File(appContext.filesDir, "recordings-$name"))
            store.applySeed(null)
            store.sweepOrphanRecordingFiles()
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
                "units-3-5-curriculum-notice.txt" -> "Units 3–5 curriculum notice"
                "units-6-10-curriculum-notice.txt" -> "Units 6–10 curriculum notice"
                "units-26-30-curriculum-notice.txt" -> "Units 26–30 curriculum notice"
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
