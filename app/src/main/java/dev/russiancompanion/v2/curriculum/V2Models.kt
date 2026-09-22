package dev.russiancompanion.v2.curriculum

import kotlinx.serialization.Serializable

@Serializable
internal data class V2CurriculumPack(
    val schemaVersion: Int,
    val packId: String,
    val packVersion: Int,
    val publicReleaseEligible: Boolean,
    val linguisticReview: V2ReviewRecord,
    val sources: List<V2SourceRecord>,
    val letters: List<V2Letter>,
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
)

@Serializable
internal data class V2CheckItem(
    val id: String,
    val kind: String,
    val language: String,
    val prompt: String,
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
    val searchTerms: List<String> = emptyList(),
)

@Serializable
internal data class V2Unit(
    val number: Int,
    val id: String,
    val title: String,
    val topicSummary: String,
    val installState: String,
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

internal fun V2CurriculumPack.unit(number: Int): V2Unit? = units.firstOrNull { it.number == number }

internal fun V2CurriculumPack.item(id: String): V2CheckItem? = items.firstOrNull { it.id == id }

internal fun V2CurriculumPack.reference(id: String): V2ReferenceEntry? = references.firstOrNull { it.id == id }

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
