package dev.russiancompanion.data.local.seed

import dev.russiancompanion.data.local.AssetRightsRecordEntity
import dev.russiancompanion.data.local.AudioModelEntity
import dev.russiancompanion.data.local.CourseSegmentEntity
import dev.russiancompanion.data.local.EligibilityStatus
import dev.russiancompanion.data.local.EvidenceType
import dev.russiancompanion.data.local.GateConditionEntity
import dev.russiancompanion.data.local.GateDefinitionEntity
import dev.russiancompanion.data.local.InclusionResult
import dev.russiancompanion.data.local.LicenseStatus
import dev.russiancompanion.data.local.ModificationStatus
import dev.russiancompanion.data.local.OptionalDrillDefinitionEntity
import dev.russiancompanion.data.local.PlanActivityType
import dev.russiancompanion.data.local.PlanDefinitionEntity
import dev.russiancompanion.data.local.RuleOperator
import dev.russiancompanion.data.local.ShareAlikeConfirmation
import dev.russiancompanion.data.local.SourceRecordEntity
import dev.russiancompanion.data.local.SourceTextEntity
import dev.russiancompanion.domain.pathway.PathwaySegment
import dev.russiancompanion.domain.pathway.PlanActivityKind
import dev.russiancompanion.domain.pathway.PlanDefinitionDraft
import dev.russiancompanion.domain.pathway.admitPlanDefinition
import dev.russiancompanion.domain.pathway.validatePathwaySegments

internal data class PreparedBundle(
    val contentVersion: Int,
    val segments: List<CourseSegmentEntity>,
    val plans: List<PlanDefinitionEntity>,
    val gates: List<GateDefinitionEntity>,
    val conditions: List<GateConditionEntity>,
    val sources: List<SourceRecordEntity>,
    val texts: List<SourceTextEntity>,
    val rights: List<AssetRightsRecordEntity>,
    val audio: List<AudioModelEntity>,
    val drills: List<OptionalDrillDefinitionEntity>,
)

internal fun prepareBundledContent(document: BundledSeedDocument): PreparedBundle {
    val sources = document.sourceRecords.mapNotNull(::admitSource)
    val sourcesById = sources.associateBy { it.id }
    val texts = document.sourceTexts.mapNotNull { admitText(it, sourcesById) }
    val textsById = texts.associateBy { it.id }
    val rights = document.assetRightsRecords.mapNotNull { admitRights(it, sourcesById) }
    val rightsById = rights.associateBy { it.id }
    val pathway = validatePathwaySegments(
        document.courseSegments.map { seed ->
            PathwaySegment(
                id = seed.id,
                lessonReference = seed.lessonReference,
                lessonNumber = seed.lessonNumber,
                sortOrder = seed.sortOrder,
            )
        },
    )
    val segments = pathway.segments.map { segment ->
        CourseSegmentEntity(
            id = segment.id,
            lessonReference = segment.lessonReference,
            lessonNumber = segment.lessonNumber,
            sortOrder = segment.sortOrder,
            eligibilityStatus = EligibilityStatus.ELIGIBLE,
        )
    }
    val segmentsById = segments.associateBy { it.id }
    val knownAudioIds = document.audioModels.map { it.id }.filter { it.isNotBlank() }.toSet()
    val plans = document.planDefinitions.mapNotNull { admitPlan(it, segmentsById, knownAudioIds) }
    val admittedGates = document.gateDefinitions.mapNotNull { admitGate(it, segmentsById) }
    val audio = admitAudio(
        seeds = document.audioModels,
        textsById = textsById,
        rightsById = rightsById,
        sourcesById = sourcesById,
    )
    val drills = document.optionalDrills.mapNotNull { admitDrill(it, sourcesById) }
    val storedRights = rights.map { record ->
        val included = audio.any { model ->
            model.rightsRecordId == record.id && model.eligibilityStatus == EligibilityStatus.ELIGIBLE
        }
        record.copy(
            inclusionResult = if (included) InclusionResult.INCLUDED else InclusionResult.EXCLUDED,
        )
    }
    return PreparedBundle(
        contentVersion = document.contentVersion,
        segments = segments,
        plans = plans,
        gates = admittedGates.map { it.gate },
        conditions = admittedGates.flatMap { it.conditions },
        sources = sources,
        texts = texts,
        rights = storedRights,
        audio = audio,
        drills = drills,
    )
}

private fun admitSource(seed: SourceRecordSeed): SourceRecordEntity? {
    val modification = enumOrNull<ModificationStatus>(seed.modificationStatus) ?: return null
    val confirmation = enumOrNull<ShareAlikeConfirmation>(seed.shareAlikeConfirmation) ?: return null
    if (seed.id.isBlank()) return null
    return SourceRecordEntity(
        id = seed.id,
        provenance = seed.provenance,
        redistributionBasis = seed.redistributionBasis,
        licenseName = seed.licenseName,
        attribution = seed.attribution,
        modificationStatus = modification,
        shareAlikeTreatment = seed.shareAlikeTreatment,
        shareAlikeConfirmation = confirmation,
    )
}

private fun sourceCanSupportInclusion(source: SourceRecordEntity, attributionRequired: Boolean): Boolean {
    if (source.provenance.isBlank() || source.redistributionBasis.isBlank() || source.licenseName.isBlank()) {
        return false
    }
    if (source.shareAlikeTreatment.isBlank()) return false
    if (attributionRequired && source.attribution.isBlank()) return false
    return true
}

private fun admitText(seed: SourceTextSeed, sources: Map<String, SourceRecordEntity>): SourceTextEntity? {
    if (seed.id.isBlank() || sources[seed.sourceRecordId] == null) return null
    val modification = enumOrNull<ModificationStatus>(seed.modificationStatus) ?: return null
    return SourceTextEntity(
        id = seed.id,
        stressMarkedText = seed.stressMarkedText,
        sourceRecordId = seed.sourceRecordId,
        modificationStatus = modification,
        matchVerified = seed.matchVerified,
    )
}

private fun admitRights(
    seed: AssetRightsSeed,
    sources: Map<String, SourceRecordEntity>,
): AssetRightsRecordEntity? {
    if (seed.id.isBlank() || seed.assetIdentifier.isBlank() || sources[seed.sourceRecordId] == null) return null
    val license = enumOrNull<LicenseStatus>(seed.licenseStatus) ?: return null
    val compatibility = enumOrNull<LicenseStatus>(seed.compatibilityDecision) ?: return null
    val proposed = enumOrNull<InclusionResult>(seed.inclusionResult) ?: return null
    return AssetRightsRecordEntity(
        id = seed.id,
        sourceRecordId = seed.sourceRecordId,
        assetIdentifier = seed.assetIdentifier,
        licenseStatus = license,
        compatibilityDecision = compatibility,
        attributionRequired = seed.attributionRequired,
        inclusionResult = proposed,
    )
}

private fun admitPlan(
    seed: PlanDefinitionSeed,
    segments: Map<String, CourseSegmentEntity>,
    knownAudioModelIds: Set<String>,
): PlanDefinitionEntity? {
    val activity = enumOrNull<PlanActivityType>(seed.activityType) ?: return null
    val kind = when (activity) {
        PlanActivityType.BOOK -> PlanActivityKind.BOOK
        PlanActivityType.ANKI_DROID -> PlanActivityKind.ANKI_DROID
        PlanActivityType.LISTEN_RECORD_COMPARE -> PlanActivityKind.LISTEN_RECORD_COMPARE
        PlanActivityType.TUTOR -> PlanActivityKind.TUTOR
    }
    val segment = segments[seed.segmentId]
    val admission = admitPlanDefinition(
        PlanDefinitionDraft(
            id = seed.id,
            segmentId = seed.segmentId,
            activity = kind,
            instruction = seed.instruction,
            sortOrder = seed.sortOrder,
            required = seed.required,
            lessonNumber = seed.lessonNumber,
            audioModelIds = seed.audioModelIds,
        ),
        segmentLessonNumber = segment?.lessonNumber,
        knownAudioModelIds = knownAudioModelIds,
    )
    if (!admission.accepted) {
        if (admission.reason == "tutor-must-be-optional" && segment != null) {
            return PlanDefinitionEntity(
                id = seed.id,
                segmentId = seed.segmentId,
                activityType = activity,
                instruction = seed.instruction,
                sortOrder = seed.sortOrder,
                required = seed.required,
                lessonNumber = seed.lessonNumber,
                audioModelIds = seed.audioModelIds,
                eligibilityStatus = EligibilityStatus.EXCLUDED,
            )
        }
        return null
    }
    return PlanDefinitionEntity(
        id = seed.id,
        segmentId = seed.segmentId,
        activityType = activity,
        instruction = seed.instruction,
        sortOrder = seed.sortOrder,
        required = seed.required,
        lessonNumber = seed.lessonNumber,
        audioModelIds = seed.audioModelIds,
        eligibilityStatus = EligibilityStatus.ELIGIBLE,
    )
}

private data class AdmittedGate(
    val gate: GateDefinitionEntity,
    val conditions: List<GateConditionEntity>,
)

private fun admitGate(
    seed: GateDefinitionSeed,
    segments: Map<String, CourseSegmentEntity>,
): AdmittedGate? {
    if (seed.id.isBlank() || segments[seed.segmentId] == null) return null
    val conditions = mutableListOf<GateConditionEntity>()
    var eligible = seed.ruleVersion.isNotBlank() && seed.displayedRuleText.isNotBlank() && seed.conditions.isNotEmpty()
    val seenConditionIds = mutableSetOf<String>()
    for (condition in seed.conditions) {
        val evidence = enumOrNull<EvidenceType>(condition.evidenceType)
        val operator = enumOrNull<RuleOperator>(condition.operator)
        if (
            condition.id.isBlank() ||
            !seenConditionIds.add(condition.id) ||
            evidence == null ||
            operator == null ||
            evidence == EvidenceType.TUTOR_SIGN_OFF ||
            !operandMatches(operator, condition.operand)
        ) {
            eligible = false
            continue
        }
        conditions += GateConditionEntity(
            id = condition.id,
            gateDefinitionId = seed.id,
            evidenceType = evidence,
            operator = operator,
            operand = condition.operand,
            sortOrder = condition.sortOrder,
        )
    }
    if (conditions.isEmpty()) eligible = false
    return AdmittedGate(
        gate = GateDefinitionEntity(
            id = seed.id,
            segmentId = seed.segmentId,
            ruleVersion = seed.ruleVersion,
            displayedRuleText = seed.displayedRuleText,
            eligibilityStatus = if (eligible) EligibilityStatus.ELIGIBLE else EligibilityStatus.EXCLUDED,
        ),
        conditions = if (eligible) conditions else emptyList(),
    )
}

private fun operandMatches(operator: RuleOperator, operand: String?): Boolean {
    return when (operator) {
        RuleOperator.PRESENT -> operand == null
        RuleOperator.AT_LEAST -> operand != null && operand.matches(NUMERIC_OPERAND)
        RuleOperator.EQUALS -> !operand.isNullOrBlank()
    }
}

private fun admitAudio(
    seeds: List<AudioModelSeed>,
    textsById: Map<String, SourceTextEntity>,
    rightsById: Map<String, AssetRightsRecordEntity>,
    sourcesById: Map<String, SourceRecordEntity>,
): List<AudioModelEntity> {
    val preliminary = seeds.mapNotNull { seed ->
        if (seed.id.isBlank()) return@mapNotNull null
        val rights = seed.rightsRecordId?.let(rightsById::get)
        val text = seed.sourceTextId?.let(textsById::get)
        val source = seed.sourceRecordId?.let(sourcesById::get)
        val eligible = audioIsEligible(seed, rights, text, source)
        AudioModelEntity(
            id = seed.id,
            localAudioReference = seed.localAudioReference,
            sourceTextId = text?.id,
            sourceRecordId = source?.id,
            rightsRecordId = rights?.id,
            eligibilityStatus = if (eligible) EligibilityStatus.ELIGIBLE else EligibilityStatus.EXCLUDED,
        )
    }
    val sharedTextIds = preliminary
        .filter { it.eligibilityStatus == EligibilityStatus.ELIGIBLE && it.sourceTextId != null }
        .groupBy { it.sourceTextId }
        .filterValues { it.size > 1 }
        .keys
    if (sharedTextIds.isEmpty()) return preliminary
    return preliminary.map { model ->
        if (model.sourceTextId in sharedTextIds) {
            model.copy(eligibilityStatus = EligibilityStatus.EXCLUDED)
        } else {
            model
        }
    }
}

private fun audioIsEligible(
    seed: AudioModelSeed,
    rights: AssetRightsRecordEntity?,
    text: SourceTextEntity?,
    source: SourceRecordEntity?,
): Boolean {
    if (seed.localAudioReference.isBlank() || rights == null || text == null || source == null) return false
    if (rights.licenseStatus != LicenseStatus.COMPATIBLE) return false
    if (rights.compatibilityDecision != LicenseStatus.COMPATIBLE) return false
    if (rights.inclusionResult != InclusionResult.INCLUDED) return false
    if (rights.assetIdentifier != seed.id && rights.assetIdentifier != seed.localAudioReference) return false
    if (text.sourceRecordId != source.id || rights.sourceRecordId != source.id) return false
    if (!text.matchVerified || text.stressMarkedText.isBlank()) return false
    return sourceCanSupportInclusion(source, rights.attributionRequired)
}

private fun admitDrill(
    seed: OptionalDrillSeed,
    sources: Map<String, SourceRecordEntity>,
): OptionalDrillDefinitionEntity? {
    if (seed.id.isBlank()) return null
    val source = seed.sourceRecordId?.let(sources::get)
    val eligible = source != null &&
        seed.packageId.isNotBlank() &&
        seed.transformId.isNotBlank() &&
        seed.sourceFieldReferences.isNotBlank() &&
        seed.inheritedErrorDisclosure.isNotBlank() &&
        sourceCanSupportInclusion(source, attributionRequired = true)
    if (source == null && seed.sourceRecordId != null) return null
    return OptionalDrillDefinitionEntity(
        id = seed.id,
        packageId = seed.packageId,
        transformId = seed.transformId,
        sourceFieldReferences = seed.sourceFieldReferences,
        sourceRecordId = source?.id,
        inheritedErrorDisclosure = seed.inheritedErrorDisclosure,
        eligibilityStatus = if (eligible) EligibilityStatus.ELIGIBLE else EligibilityStatus.EXCLUDED,
    )
}

private inline fun <reified T : Enum<T>> enumOrNull(raw: String): T? {
    return enumValues<T>().firstOrNull { it.name == raw }
}

private val NUMERIC_OPERAND = Regex("[0-9]+(\\.[0-9]+)?")
