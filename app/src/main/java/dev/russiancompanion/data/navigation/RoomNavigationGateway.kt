package dev.russiancompanion.data.navigation

import dev.russiancompanion.data.export.BuiltPackage
import dev.russiancompanion.data.export.ExportAudio
import dev.russiancompanion.data.export.ExportDecisionRow
import dev.russiancompanion.data.export.ExportEvidenceRow
import dev.russiancompanion.data.export.ExportLogRow
import dev.russiancompanion.data.export.ExportProgressRow
import dev.russiancompanion.data.export.buildExportZip
import dev.russiancompanion.data.export.exportAudioName
import dev.russiancompanion.data.local.ActivityCategory
import dev.russiancompanion.data.local.ActivityLogEntity
import dev.russiancompanion.data.local.AssetRightsRecordEntity
import dev.russiancompanion.data.local.AudioModelEntity
import dev.russiancompanion.data.local.CompletionState
import dev.russiancompanion.data.local.EligibilityStatus
import dev.russiancompanion.data.local.ExportRecordEntity
import dev.russiancompanion.data.local.ExportStatus
import dev.russiancompanion.data.local.GateDecisionStatus
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.data.local.evidenceType
import dev.russiancompanion.data.local.gateDetailView
import dev.russiancompanion.data.local.PlanActivityType
import dev.russiancompanion.data.local.seed.SeedApplyStatus
import dev.russiancompanion.domain.gate.GateEvidenceEntry
import dev.russiancompanion.domain.navigation.AssetInventoryOption
import dev.russiancompanion.domain.navigation.AttributionOption
import dev.russiancompanion.domain.navigation.AudioOption
import dev.russiancompanion.domain.navigation.GateOption
import dev.russiancompanion.domain.navigation.LessonOption
import dev.russiancompanion.domain.navigation.LicenseNoticeOption
import dev.russiancompanion.domain.navigation.NavigationGateway
import dev.russiancompanion.domain.navigation.RecordingOption
import dev.russiancompanion.domain.navigation.NavigationSnapshot
import dev.russiancompanion.domain.navigation.OnboardingConfirmation
import dev.russiancompanion.domain.navigation.onboardingConfirmation
import dev.russiancompanion.domain.plan.ActivityLogView
import dev.russiancompanion.domain.plan.ChecklistActivity
import dev.russiancompanion.domain.plan.DecisionView
import dev.russiancompanion.domain.plan.GateStatusInput
import dev.russiancompanion.domain.plan.PlanDefinitionView
import dev.russiancompanion.domain.plan.PlanInstanceView
import dev.russiancompanion.domain.plan.PlanUpdate
import dev.russiancompanion.domain.plan.RecordedCategory
import dev.russiancompanion.domain.plan.RecordedGateStatus
import dev.russiancompanion.domain.plan.assembleDailyPlan
import dev.russiancompanion.domain.practice.PracticeAudio
import dev.russiancompanion.domain.plan.summarizeActivity
import dev.russiancompanion.domain.plan.summarizeGates
import dev.russiancompanion.domain.progress.ExportCounts
import dev.russiancompanion.domain.progress.ExportUpdate
import dev.russiancompanion.domain.progress.ProgressReview
import dev.russiancompanion.domain.progress.activityAttestationLabel
import dev.russiancompanion.domain.progress.evidenceAttestationLabel
import dev.russiancompanion.domain.progress.reviewProgress
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

internal class RoomNavigationGateway(
    private val store: LocalStore,
    private val readPackagedSeed: () -> String?,
    private val practiceAudio: PracticeAudio,
    private val readCatalogNotice: () -> String? = { null },
    private val readLicenseNotices: () -> List<LicenseNoticeOption> = { emptyList() },
    private val readRecordingBytes: (String) -> ByteArray? = { null },
) : NavigationGateway {
    override fun current(localDate: String): NavigationSnapshot {
        val outcome = store.seedOutcome()
        val plans = store.eligiblePlans()
        val lessons = store.eligibleSegments().map { segment ->
            LessonOption(
                id = segment.id,
                lessonReference = segment.lessonReference,
                planCount = plans.count { it.segmentId == segment.id },
            )
        }
        val learner = store.learner()
        val recordings = store.recordings()
        val sourceEntities = store.sources()
        val sources = sourceEntities.associateBy { it.id }
        val rightsRecords = store.rights()
        val rightsById = rightsRecords.associateBy { it.id }
        val storedAudio = store.allAudio()
        val audio = store.eligibleAudio().mapNotNull { model ->
            val textId = model.sourceTextId ?: return@mapNotNull null
            val text = store.sourceText(textId) ?: return@mapNotNull null
            val source = model.sourceRecordId?.let { sources[it] }
            val modelRecordings = recordings.filter { it.audioModelId == model.id }
            AudioOption(
                id = model.id,
                stressMarkedText = text.stressMarkedText,
                localAudioReference = model.localAudioReference,
                playable = practiceAudio.modelPlayable(model.localAudioReference),
                licenseName = source?.licenseName.orEmpty(),
                attribution = source?.attribution.orEmpty(),
                hasRecording = modelRecordings.isNotEmpty(),
                completed = modelRecordings.any { it.completionState == CompletionState.COMPLETED },
            )
        }
        val segmentId = learner.activeSegmentId
        val segmentPlans = if (segmentId == null) emptyList() else plans.filter { it.segmentId == segmentId }
        val instances = if (segmentId == null) {
            emptyList()
        } else {
            store.planInstances(localDate, segmentId).map { instance ->
                PlanInstanceView(
                    planDefinitionId = instance.planDefinitionId,
                    completed = instance.completionState == CompletionState.COMPLETED,
                )
            }
        }
        val rawLogs = store.activityLogs()
        val activityViews = rawLogs.map { log -> activityView(log) }
        val progress = if (activityViews.any { it == null }) {
            ProgressReview.unreadable()
        } else {
            reviewProgress(activityViews.filterNotNull())
        }
        val summaryLogs = if (progress.readable) activityViews.filterNotNull() else emptyList()
        val decisions = store.decisions()
        val gates = store.eligibleGates().filter { segmentId == null || it.segmentId == segmentId }
        val gateDetails = gates.map { gate ->
            val latest = decisions
                .filter { it.gateDefinitionId == gate.id }
                .maxWithOrNull(compareBy({ it.evaluatedAtLocal }, { it.id }))
            gateDetailView(
                gate = gate,
                conditions = store.conditions(gate.id),
                tutorRequired = store.tutorRequirement(gate.id) != null,
                evidence = store.evidenceFor(gate.id),
                recordedStatus = latest?.status,
            )
        }
        return NavigationSnapshot(
            localDataOpen = true,
            seedCorrupt = outcome?.status == SeedApplyStatus.CORRUPT,
            seedAbsent = outcome?.status == SeedApplyStatus.ABSENT,
            onboardingCompleted = learner.onboardingCompleted,
            activeSegmentId = segmentId,
            activeLessonReference = lessons.firstOrNull { it.id == segmentId }?.lessonReference,
            lessons = lessons,
            audio = audio,
            recordings = recordings.map { recording ->
                RecordingOption(
                    id = recording.id,
                    audioModelId = recording.audioModelId,
                    localFileReference = recording.localFileReference,
                    markedForTutorReview = recording.markedForTutorReview,
                    completed = recording.completionState == CompletionState.COMPLETED,
                    readable = practiceAudio.recordingReadable(recording.localFileReference),
                )
            },
            gates = store.eligibleGates().map { gate ->
                GateOption(id = gate.id, displayedRuleText = gate.displayedRuleText)
            },
            eligibleDrillCount = store.eligibleDrills().size,
            attributions = sourceEntities.map { source ->
                AttributionOption(
                    sourceId = source.id,
                    licenseName = source.licenseName,
                    attribution = source.attribution,
                    modificationStatus = source.modificationStatus.name,
                    shareAlikeConfirmation = source.shareAlikeConfirmation.name,
                    provenance = source.provenance,
                    redistributionBasis = source.redistributionBasis,
                    shareAlikeTreatment = source.shareAlikeTreatment,
                )
            },
            assetRecords = assetInventory(storedAudio, rightsById, rightsRecords),
            shareAlikeBlocksRelease = outcome?.shareAlikeBlocksRelease == true,
            catalogNotice = readCatalogNotice().orEmpty(),
            licenseNotices = readLicenseNotices(),
            dailyPlan = assembleDailyPlan(
                definitions = segmentPlans.mapNotNull { plan ->
                    val activity = checklistActivity(plan.activityType) ?: return@mapNotNull null
                    PlanDefinitionView(
                        id = plan.id,
                        activity = activity,
                        instruction = plan.instruction,
                        sortOrder = plan.sortOrder,
                        required = plan.required,
                        lessonNumber = plan.lessonNumber,
                        audioModelIds = plan.audioModelIds,
                    )
                },
                instances = instances,
            ),
            gateSummaries = summarizeGates(
                gates = gates.map { GateStatusInput(it.id, it.displayedRuleText) },
                decisions = decisions.mapNotNull { decision ->
                    val status = recordedStatus(decision.status) ?: return@mapNotNull null
                    DecisionView(
                        id = decision.id,
                        gateId = decision.gateDefinitionId,
                        status = status,
                        evaluatedAtLocal = decision.evaluatedAtLocal,
                    )
                },
            ),
            gateDetails = gateDetails,
            activitySummary = summarizeActivity(summaryLogs),
            progress = progress,
        )
    }

    override fun confirmLesson(segmentId: String, localDate: String) {
        if (onboardingConfirmation(current(localDate), segmentId) != OnboardingConfirmation.READY) {
            throw IllegalArgumentException("Onboarding cannot finish")
        }
        store.confirmLesson(segmentId, localDate)
    }

    override fun reloadPackagedSeed() {
        store.applySeed(readPackagedSeed())
    }

    override fun completeExternalActivity(
        planDefinitionId: String,
        localDate: String,
        recordedAtLocal: String,
    ): PlanUpdate {
        return update(planDefinitionId) {
            if (store.completeExternalActivity(planDefinitionId, localDate, recordedAtLocal)) {
                PlanUpdate.Applied
            } else {
                PlanUpdate.Unchanged
            }
        }
    }

    override fun includeTutorTask(planDefinitionId: String, localDate: String): PlanUpdate {
        return update(planDefinitionId) {
            store.includeTutorTask(planDefinitionId, localDate)
            PlanUpdate.Applied
        }
    }

    override fun removeTutorTask(planDefinitionId: String, localDate: String): PlanUpdate {
        return update(planDefinitionId) {
            store.removeTutorTask(planDefinitionId, localDate)
            PlanUpdate.Applied
        }
    }

    override fun saveRecording(
        audioModelId: String,
        localFileReference: String,
        recordedAtLocal: String,
    ): PlanUpdate {
        return update(audioModelId) {
            if (!practiceAudio.recordingReadable(localFileReference)) {
                throw IllegalArgumentException("A readable recording is required")
            }
            store.commitRecording(
                audioModelId = audioModelId,
                localFileReference = localFileReference,
                createdAtLocal = recordedAtLocal,
                markedForTutorReview = false,
                completionState = CompletionState.NOT_COMPLETED,
            )
            PlanUpdate.Applied
        }
    }

    override fun setRecordingTutorReview(recordingId: String, marked: Boolean): PlanUpdate {
        return update(recordingId) {
            store.setRecordingTutorReview(recordingId, marked)
            PlanUpdate.Applied
        }
    }

    override fun completeListenPractice(
        audioModelId: String,
        localDate: String,
        recordedAtLocal: String,
    ): PlanUpdate {
        return update(audioModelId) {
            val recording = store.recordings().lastOrNull { it.audioModelId == audioModelId }
            if (recording == null || !practiceAudio.recordingReadable(recording.localFileReference)) {
                throw IllegalArgumentException("A readable recording is required")
            }
            if (store.completeListenPractice(audioModelId, localDate, recordedAtLocal)) {
                PlanUpdate.Applied
            } else {
                PlanUpdate.Unchanged
            }
        }
    }

    override fun saveGateEvidence(
        gateId: String,
        entries: List<GateEvidenceEntry>,
        recordedAtLocal: String,
    ): PlanUpdate {
        return update(gateId) {
            store.recordGateEvidenceAndDecide(
                gateDefinitionId = gateId,
                entries = entries.map { evidenceType(it.kind) to it.enteredValue },
                recordedAtLocal = recordedAtLocal,
            )
            PlanUpdate.Applied
        }
    }

    override fun addTutorSignOff(gateId: String, recordedAtLocal: String): PlanUpdate {
        return update(gateId) {
            store.addTutorSignOffRequirement(gateId, recordedAtLocal)
            PlanUpdate.Applied
        }
    }

    override fun saveActivityLog(
        category: RecordedCategory,
        durationMinutes: Int,
        completed: Boolean,
        recordedAtLocal: String,
        associateWithLesson: Boolean,
    ): PlanUpdate {
        return update("activity") {
            // Grammar and vocabulary logs are user-entered activity. Their labels
            // are Book practice and AnkiDroid practice, without integration or verification.
            val selfAttested = category == RecordedCategory.GRAMMAR ||
                category == RecordedCategory.VOCABULARY_AND_FORMS
            val segmentId = if (associateWithLesson) {
                store.learner().activeSegmentId ?: throw IllegalArgumentException("No active lesson")
            } else {
                null
            }
            store.recordActivity(
                category = storedCategory(category),
                durationMinutes = durationMinutes,
                completed = completed,
                recordedAtLocal = recordedAtLocal,
                segmentId = segmentId,
                selfAttested = selfAttested,
            )
            PlanUpdate.Applied
        }
    }

    override fun exportLocal(
        destination: OutputStream,
        recordedAtLocal: String,
        destinationFileName: String?,
        documentReference: String?,
    ): ExportUpdate {
        val fileName = destinationFileName?.trim()?.takeIf { it.isNotEmpty() }
        val document = documentReference?.trim()?.takeIf { it.isNotEmpty() }
        return try {
            val rawLogs = store.activityLogs()
            val views = mutableListOf<ActivityLogView>()
            val logRows = mutableListOf<ExportLogRow>()
            for (log in rawLogs) {
                val view = activityView(log)
                if (view == null || log.durationMinutes < 0) {
                    recordExport(recordedAtLocal, fileName, document, ExportStatus.NOT_CONFIRMED)
                    return ExportUpdate.NotConfirmed
                }
                views += view
                logRows += ExportLogRow(
                    id = log.id,
                    category = view.category.name,
                    durationMinutes = log.durationMinutes,
                    completed = log.completed,
                    recordedAtLocal = log.recordedAtLocal,
                    segmentId = log.segmentId.orEmpty(),
                    planInstanceId = log.planInstanceId.orEmpty(),
                    selfAttested = log.selfAttested,
                    attestationLabel = activityAttestationLabel(view.category, log.selfAttested),
                )
            }
            val review = reviewProgress(views)
            if (!review.readable) {
                recordExport(recordedAtLocal, fileName, document, ExportStatus.NOT_CONFIRMED)
                return ExportUpdate.NotConfirmed
            }
            val evidenceRows = store.allEvidence().map { evidence ->
                ExportEvidenceRow(
                    id = evidence.id,
                    gateDefinitionId = evidence.gateDefinitionId,
                    evidenceType = evidence.evidenceType.name,
                    enteredValue = evidence.enteredValue,
                    selfAttestationStatus = evidence.selfAttestationStatus.name,
                    attestationLabel = evidenceAttestationLabel(evidence.evidenceType.name),
                    recordedAtLocal = evidence.recordedAtLocal,
                )
            }
            val decisionRows = store.decisions().map { decision ->
                ExportDecisionRow(
                    id = decision.id,
                    gateDefinitionId = decision.gateDefinitionId,
                    ruleVersion = decision.ruleVersion,
                    ruleSnapshot = decision.ruleSnapshot,
                    evidenceIds = decision.evidenceIds,
                    conditionOutcomes = decision.conditionOutcomes,
                    status = decision.status.name,
                    evaluatedAtLocal = decision.evaluatedAtLocal,
                )
            }
            val progressRows = review.categories.map { category ->
                ExportProgressRow(
                    category = category.category.name,
                    recordedMinutes = category.recordedMinutes,
                    completionCount = category.completionCount,
                )
            }
            val recordings = store.recordings().map { recording ->
                ExportAudio(
                    id = recording.id,
                    audioModelId = recording.audioModelId,
                    createdAtLocal = recording.createdAtLocal,
                    markedForTutorReview = recording.markedForTutorReview,
                    completionState = recording.completionState.name,
                    fileName = exportAudioName(recording.id, recording.localFileReference),
                    bytes = readRecordingBytes(recording.localFileReference),
                    activityLogId = recording.activityLogId,
                )
            }
            when (val built = buildExportZip(logRows, evidenceRows, decisionRows, progressRows, recordings)) {
                is BuiltPackage.RecordingUnreadable -> {
                    recordExport(recordedAtLocal, fileName, document, ExportStatus.FAILED)
                    ExportUpdate.RecordingUnreadable(built.recordingId)
                }
                is BuiltPackage.Ready -> {
                    try {
                        destination.write(built.bytes)
                        destination.flush()
                    } catch (_: IOException) {
                        recordExport(recordedAtLocal, fileName, document, ExportStatus.NOT_CONFIRMED)
                        return ExportUpdate.NotConfirmed
                    }
                    recordExport(
                        recordedAtLocal = recordedAtLocal,
                        destinationFileName = fileName,
                        documentReference = document,
                        status = ExportStatus.COMPLETED,
                        logCount = built.logCount,
                        evidenceCount = built.evidenceCount,
                        progressCount = built.progressCount,
                        recordingCount = built.recordingCount,
                        packageCounts = ExportCounts(
                            logCount = built.logCount,
                            evidenceCount = built.evidenceCount,
                            progressCount = built.progressCount,
                            recordingCount = built.recordingCount,
                        ),
                    )
                    ExportUpdate.Completed(built.empty)
                }
            }
        } catch (_: Exception) {
            try {
                recordExport(recordedAtLocal, fileName, document, ExportStatus.NOT_CONFIRMED)
            } catch (_: Exception) {
                // The destination was not confirmed, and the export record could not be stored.
            }
            ExportUpdate.NotConfirmed
        }
    }

    private fun recordExport(
        recordedAtLocal: String,
        destinationFileName: String?,
        documentReference: String?,
        status: ExportStatus,
        logCount: Int = 0,
        evidenceCount: Int = 0,
        progressCount: Int = 0,
        recordingCount: Int = 0,
        packageCounts: ExportCounts? = null,
    ) {
        store.saveExport(
            ExportRecordEntity(
                id = UUID.randomUUID().toString(),
                exportedAtLocal = recordedAtLocal,
                destinationReference = destinationFileName,
                documentReference = documentReference,
                logCount = logCount,
                evidenceCount = evidenceCount,
                progressCount = progressCount,
                recordingCount = recordingCount,
                status = status,
            ),
            packageCounts = packageCounts,
        )
    }

    private fun assetInventory(
        audio: List<AudioModelEntity>,
        rightsById: Map<String, AssetRightsRecordEntity>,
        rights: List<AssetRightsRecordEntity>,
    ): List<AssetInventoryOption> {
        val referenced = audio.mapNotNull { it.rightsRecordId }.toSet()
        val rows = mutableListOf<AssetInventoryOption>()
        for (model in audio) {
            val record = model.rightsRecordId?.let { rightsById[it] }
            if (record == null) {
                rows += AssetInventoryOption(
                    rightsId = null,
                    sourceId = model.sourceRecordId,
                    assetIdentifier = "",
                    licenseStatus = "",
                    compatibilityDecision = "",
                    attributionRequired = false,
                    inclusionResult = "",
                    audioModelId = model.id,
                    localAudioReference = model.localAudioReference,
                    audioEligible = false,
                    rightsMissing = true,
                )
            } else {
                rows += assetRow(record, model)
            }
        }
        for (record in rights) {
            if (record.id in referenced) continue
            rows += assetRow(record, model = null)
        }
        return rows
    }

    private fun assetRow(record: AssetRightsRecordEntity, model: AudioModelEntity?): AssetInventoryOption {
        return AssetInventoryOption(
            rightsId = record.id,
            sourceId = record.sourceRecordId,
            assetIdentifier = record.assetIdentifier,
            licenseStatus = record.licenseStatus.name,
            compatibilityDecision = record.compatibilityDecision.name,
            attributionRequired = record.attributionRequired,
            inclusionResult = record.inclusionResult.name,
            audioModelId = model?.id,
            localAudioReference = model?.localAudioReference.orEmpty(),
            audioEligible = model?.eligibilityStatus == EligibilityStatus.ELIGIBLE,
            rightsMissing = false,
        )
    }

    private fun activityView(log: ActivityLogEntity): ActivityLogView? {
        val category = recordedCategory(log.category) ?: return null
        return ActivityLogView(
            id = log.id,
            category = category,
            minutes = log.durationMinutes,
            completed = log.completed,
            recordedAtLocal = log.recordedAtLocal,
            selfAttested = log.selfAttested,
            segmentId = log.segmentId,
        )
    }

    private fun update(planDefinitionId: String, block: () -> PlanUpdate): PlanUpdate {
        return try {
            block()
        } catch (_: Exception) {
            PlanUpdate.NotSaved(planDefinitionId)
        }
    }

    private fun checklistActivity(type: PlanActivityType): ChecklistActivity? {
        return when (type) {
            PlanActivityType.BOOK -> ChecklistActivity.BOOK
            PlanActivityType.ANKI_DROID -> ChecklistActivity.ANKI_DROID
            PlanActivityType.LISTEN_RECORD_COMPARE -> ChecklistActivity.LISTEN_RECORD_COMPARE
            PlanActivityType.TUTOR -> ChecklistActivity.TUTOR
        }
    }

    private fun storedCategory(category: RecordedCategory): ActivityCategory {
        return when (category) {
            RecordedCategory.READING -> ActivityCategory.READING
            RecordedCategory.LISTENING -> ActivityCategory.LISTENING
            RecordedCategory.VOCABULARY_AND_FORMS -> ActivityCategory.VOCABULARY_AND_FORMS
            RecordedCategory.GRAMMAR -> ActivityCategory.GRAMMAR
            RecordedCategory.SPEAKING_AND_PRONUNCIATION -> ActivityCategory.SPEAKING_AND_PRONUNCIATION
            RecordedCategory.WRITING -> ActivityCategory.WRITING
            RecordedCategory.TUTOR_WORK -> ActivityCategory.TUTOR_WORK
        }
    }

    private fun recordedCategory(category: ActivityCategory): RecordedCategory? {
        return when (category) {
            ActivityCategory.READING -> RecordedCategory.READING
            ActivityCategory.LISTENING -> RecordedCategory.LISTENING
            ActivityCategory.VOCABULARY_AND_FORMS -> RecordedCategory.VOCABULARY_AND_FORMS
            ActivityCategory.GRAMMAR -> RecordedCategory.GRAMMAR
            ActivityCategory.SPEAKING_AND_PRONUNCIATION -> RecordedCategory.SPEAKING_AND_PRONUNCIATION
            ActivityCategory.WRITING -> RecordedCategory.WRITING
            ActivityCategory.TUTOR_WORK -> RecordedCategory.TUTOR_WORK
        }
    }

    private fun recordedStatus(status: GateDecisionStatus): RecordedGateStatus? {
        return when (status) {
            GateDecisionStatus.SATISFIED -> RecordedGateStatus.SATISFIED
            GateDecisionStatus.NOT_YET_SATISFIED -> RecordedGateStatus.NOT_YET_SATISFIED
            GateDecisionStatus.NOT_EVALUABLE -> RecordedGateStatus.NOT_EVALUABLE
        }
    }
}
