package dev.russiancompanion.v2.curriculum

import kotlinx.serialization.Serializable

internal val CLOSED_REVIEW_STATES = setOf(
    "PENDING_QUALIFIED_REVIEW",
    "APPROVED",
    "REJECTED",
    "NOT_REQUIRED",
)

internal val CLOSED_INSTALL_STATES = setOf(
    "DEVELOPMENT",
    "INSTALLED",
    "NOT_YET_INSTALLED",
)

internal val CLOSED_ITEM_KINDS = setOf(
    "LETTER_IDENTITY",
    "PRINT_PAIR",
    "CONFUSABLE",
    "SIGN",
    "SOURCE_GENDER",
    "SOURCE_MEANING",
    "SOURCE_FORM",
    "SOURCE_STRESS",
    "SOURCE_QUESTION",
    "SOURCE_CASE",
    "SOURCE_INDECLINABLE",
)

internal val LINGUISTIC_REFERENCE_CLASSIFICATION = "LINGUISTIC_INSTRUCTION"

internal val CLOSED_REFERENCE_CLASSIFICATIONS = setOf(
    LINGUISTIC_REFERENCE_CLASSIFICATION,
    "PRODUCT_HELP",
    "LICENSE_OR_ATTRIBUTION",
    "OPERATIONAL_GUIDANCE",
)

internal val NON_LINGUISTIC_REFERENCE_CLASSIFICATIONS = CLOSED_REFERENCE_CLASSIFICATIONS - LINGUISTIC_REFERENCE_CLASSIFICATION

internal val CLOSED_READINESS = setOf(
    "NOT_PREPARED",
    "CONTENT_COMPLETE_REVIEW_PENDING",
    "REVIEW_APPROVED",
    "MAPPING_APPROVED",
    "INSTALLABLE",
)

internal val GOVERNANCE_PROMPT_MARKERS = listOf(
    "catalog audio",
    "unrelated catalog",
    "score your handwriting",
    "score pronunciation",
    "scores pronunciation",
    "where do you study handwritten",
    "where handwriting study",
    "lesson 3",
    "sources and scoring",
    "scoring limits",
    "may unit 2 introduce",
    "why unused catalog",
)

@Serializable
internal data class V2CurriculumPack(
    val schemaVersion: Int,
    val packId: String,
    val packVersion: Int,
    val publicReleaseEligible: Boolean,
    val linguisticReview: V2ReviewRecord,
    val unitReviews: List<V2UnitReviewRecord> = emptyList(),
    val sources: List<V2SourceRecord>,
    val rights: List<V2RightsRecord> = emptyList(),
    val audioAssets: List<V2AudioAsset> = emptyList(),
    val handwritingAssets: List<V2HandwritingAsset> = emptyList(),
    val ownedEditionMappings: List<V2OwnedEditionMapping> = emptyList(),
    val provenanceRecords: List<V2ProvenanceRecord> = emptyList(),
    val letters: List<V2Letter>,
    val examples: List<V2Example> = emptyList(),
    val items: List<V2CheckItem>,
    val references: List<V2ReferenceEntry>,
    val units: List<V2Unit>,
    val admissions: V2Admissions,
)

@Serializable
internal data class V2ReviewRecord(
    val id: String,
    val status: String,
    val reviewer: String? = null,
    val reviewerQualification: String? = null,
    val packVersion: Int? = null,
    val reviewedContentDigest: String? = null,
    val curriculumMappingId: String? = null,
    val notes: String? = null,
)

@Serializable
internal data class V2UnitReviewRecord(
    val id: String,
    val unitNumber: Int,
    val status: String,
    val reviewer: String? = null,
    val reviewerQualification: String? = null,
    val packVersion: Int? = null,
    val reviewedContentDigest: String? = null,
    val curriculumMappingId: String? = null,
    val notes: String? = null,
)

@Serializable
internal data class V2SourceRecord(
    val id: String,
    val title: String,
    val license: String,
    val attribution: String,
    val modification: String,
)

@Serializable
internal data class V2RightsRecord(
    val id: String,
    val sourceId: String,
    val license: String,
    val permitsAppUse: Boolean,
)

@Serializable
internal data class V2AudioAsset(
    val id: String,
    val sourceId: String,
    val rightsId: String,
    val unitNumber: Int,
    val letterId: String,
    val intakeReviewStatus: String = "PENDING_QUALIFIED_REVIEW",
)

@Serializable
internal data class V2HandwritingAsset(
    val id: String,
    val sourceId: String,
    val rightsId: String,
    val unitNumber: Int,
    val letterId: String,
    val intakeReviewStatus: String = "PENDING_QUALIFIED_REVIEW",
)

@Serializable
internal data class V2OwnedEditionMapping(
    val id: String,
    val unitNumber: Int,
    val status: String,
    val reviewer: String? = null,
    val reviewerQualification: String? = null,
    val reviewedContentDigest: String? = null,
    val editionTitle: String? = null,
    val author: String? = null,
    val publisherImprint: String? = null,
    val proposedLessonNumber: Int? = null,
    val printedPageStart: Int? = null,
    val printedPageEnd: Int? = null,
    val topicDescription: String? = null,
    val observationSource: String? = null,
    val isbn: String? = null,
    val isbnObservation: String = "UNRESOLVED",
)

@Serializable
internal data class V2ProvenanceRecord(
    val itemId: String,
    val unitNumber: Int,
    val objectiveId: String,
    val recordType: String,
    val sourceId: String,
    val sourceUrl: String,
    val sourceRevision: String,
    val sourceRecordId: String,
    val originalSourceText: String,
    val shippedText: String,
    val transformation: String,
    val licenseId: String,
    val licenseUrl: String,
    val attribution: String,
    val shareAlikeTreatment: String,
    val sourceHash: String,
    val contentHash: String,
    val reviewStatus: String = "PENDING_QUALIFIED_REVIEW",
    val reviewerIdentity: String = "",
    val reviewerQualification: String = "",
    val reviewDate: String = "",
    val reviewedDigest: String = "",
    val audioAssetId: String? = null,
)

@Serializable
internal data class V2Letter(
    val id: String,
    val uppercase: String,
    val lowercase: String,
    val group: String,
    val englishNote: String,
    val sourceId: String,
    val reviewStatus: String,
    val audioAssetId: String? = null,
    val exampleWordId: String? = null,
    val latinLookalike: String? = null,
)

@Serializable
internal data class V2Example(
    val id: String,
    val kind: String,
    val letterId: String,
    val display: String,
    val note: String,
    val sourceId: String,
    val reviewStatus: String,
    val objectiveId: String,
)

@Serializable
internal data class V2CheckItem(
    val id: String,
    val kind: String,
    val language: String,
    val prompt: String,
    val promptLetter: String? = null,
    val options: List<String>,
    val answer: String,
    val feedback: String,
    val objectiveId: String,
    val sourceId: String,
    val reviewStatus: String,
    val containsGeneratedRussian: Boolean = false,
)

@Serializable
internal data class V2ReferenceEntry(
    val id: String,
    val title: String,
    val summary: String,
    val detail: String,
    val introducedInUnit: Int,
    val sourceId: String,
    val reviewStatus: String,
    val classification: String = "",
    val searchTerms: List<String> = emptyList(),
)

@Serializable
internal data class V2Unit(
    val number: Int,
    val id: String,
    val title: String,
    val shortLabel: String = "",
    val topicSummary: String,
    val installState: String,
    val readiness: String = "",
    val penguinLesson: Int,
    val mappingStatus: String,
    val mappingNote: String,
    val prerequisiteUnitNumbers: List<Int> = emptyList(),
    val estimatedMinutes: Int? = null,
    val objectives: List<V2Objective> = emptyList(),
    val sessions: List<V2Session> = emptyList(),
    val advancement: V2AdvancementRule? = null,
)

@Serializable
internal data class V2Objective(
    val id: String,
    val unitNumber: Int,
    val title: String,
    val kind: String,
    val instructionStepIds: List<String> = emptyList(),
    val exampleIds: List<String> = emptyList(),
    val practiceStepIds: List<String> = emptyList(),
    val checkItemIds: List<String> = emptyList(),
    val remediationStepIds: List<String> = emptyList(),
)

@Serializable
internal data class V2Session(
    val id: String,
    val title: String,
    val estimatedMinutes: Int,
    val objectiveIds: List<String> = emptyList(),
    val steps: List<V2Step> = emptyList(),
)

@Serializable
internal data class V2Step(
    val id: String,
    val kind: String,
    val title: String,
    val body: String,
    val dominantAction: String = "Continue",
    val referenceIds: List<String> = emptyList(),
    val itemIds: List<String> = emptyList(),
    val exampleIds: List<String> = emptyList(),
    val unavailableReason: String? = null,
    val bookDirection: String? = null,
    val companionDirection: String? = null,
    val remediationStepId: String? = null,
)

@Serializable
internal data class V2AdvancementRule(
    val requireInstruction: Boolean,
    val requireBookSelfAttestation: Boolean,
    val requireAppChecks: Boolean,
    val minimumDistinctChecks: Int,
    val requireDueReview: Boolean,
    val tutorOptional: Boolean,
)

@Serializable
internal data class V2Admissions(
    val generatedRussianWords: Int,
    val penguinProseAssets: Int,
    val catalogAudioBoundToUnits: Int,
    val unverifiedExampleWords: Int,
    val futureRussianItems: Int = 0,
    val handwritingAssets: Int = 0,
    val mappedPronunciationAudio: Int = 0,
)

internal enum class V2UnitState {
    CURRENT,
    AVAILABLE,
    COMPLETED,
    LOCKED,
    NOT_YET_INSTALLED,
}

internal enum class V2ObjectiveState {
    NOT_STARTED,
    LEARNING,
    REVIEW_REQUIRED,
    DEMONSTRATED_IN_APP_CHECKS,
    COMPLETED_BY_SELF_ATTESTATION_ONLY,
}

internal enum class V2StartingPoint {
    NEW_TO_CYRILLIC,
    CAN_READ_CYRILLIC,
    RESUME_FROM_BOOK,
}

internal data class V2Validation(
    val ok: Boolean,
    val errors: List<String>,
)

internal fun V2Unit.installed(): Boolean = installState == "INSTALLED"

internal fun V2Unit.openable(): Boolean = installState == "INSTALLED" || installState == "DEVELOPMENT"

internal fun V2Unit.topicLabel(): String = shortLabel.ifBlank { title }

internal fun V2CurriculumPack.unit(number: Int): V2Unit? = units.firstOrNull { it.number == number }

internal fun V2CurriculumPack.item(id: String): V2CheckItem? = items.firstOrNull { it.id == id }

internal fun V2CurriculumPack.example(id: String): V2Example? = examples.firstOrNull { it.id == id }

internal fun V2CurriculumPack.reference(id: String): V2ReferenceEntry? = references.firstOrNull { it.id == id }

internal fun V2CurriculumPack.letter(id: String): V2Letter? = letters.firstOrNull { it.id == id }

internal fun V2CurriculumPack.session(id: String): V2Session? {
    units.forEach { unit ->
        unit.sessions.firstOrNull { it.id == id }?.let { return it }
    }
    return null
}

internal fun V2CurriculumPack.step(id: String): Pair<V2Session, V2Step>? {
    units.forEach { unit ->
        unit.sessions.forEach { session ->
            session.steps.firstOrNull { it.id == id }?.let { return session to it }
        }
    }
    return null
}

internal fun V2CheckItem.isGovernancePrompt(): Boolean {
    val haystack = "$id $kind $prompt $feedback".lowercase()
    return GOVERNANCE_PROMPT_MARKERS.any { it in haystack }
}
