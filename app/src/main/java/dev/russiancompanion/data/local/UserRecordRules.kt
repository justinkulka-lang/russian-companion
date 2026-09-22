package dev.russiancompanion.data.local

import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.UUID

internal object UserRecordRules {
    fun requireLocalDate(value: String): String {
        try {
            LocalDate.parse(value)
        } catch (_: DateTimeParseException) {
            throw IllegalArgumentException("A local date value is required")
        }
        return value
    }

    fun requireTimestamp(value: String): String {
        if (value.isBlank()) throw IllegalArgumentException("A local timestamp is required")
        return value
    }

    fun requireDurationMinutes(minutes: Int): Int {
        if (minutes < 0) throw IllegalArgumentException("Duration must be nonnegative")
        return minutes
    }

    fun activityLog(
        category: ActivityCategory,
        durationMinutes: Int,
        completed: Boolean,
        recordedAtLocal: String,
        segmentId: String?,
        planInstanceId: String?,
        selfAttested: Boolean,
        planActivityType: PlanActivityType?,
    ): ActivityLogEntity {
        if (planActivityType == PlanActivityType.BOOK || planActivityType == PlanActivityType.ANKI_DROID) {
            if (!selfAttested) {
                throw IllegalArgumentException("Book and AnkiDroid activity must be self-attested")
            }
        }
        return ActivityLogEntity(
            id = UUID.randomUUID().toString(),
            category = category,
            durationMinutes = requireDurationMinutes(durationMinutes),
            completed = completed,
            recordedAtLocal = requireTimestamp(recordedAtLocal),
            segmentId = segmentId,
            planInstanceId = planInstanceId,
            selfAttested = selfAttested,
        )
    }

    fun evidence(
        gateDefinitionId: String,
        evidenceType: EvidenceType,
        enteredValue: String,
        recordedAtLocal: String,
    ): GateEvidenceEntity {
        if (enteredValue.isBlank()) throw IllegalArgumentException("Evidence value is required")
        return GateEvidenceEntity(
            id = UUID.randomUUID().toString(),
            gateDefinitionId = gateDefinitionId,
            evidenceType = evidenceType,
            enteredValue = enteredValue,
            selfAttestationStatus = evidenceSelfAttestation(evidenceType),
            recordedAtLocal = requireTimestamp(recordedAtLocal),
        )
    }

    fun recording(
        audioModelId: String,
        localFileReference: String,
        createdAtLocal: String,
        markedForTutorReview: Boolean,
        completionState: CompletionState,
    ): RecordingRecordEntity {
        if (localFileReference.isBlank()) {
            throw IllegalArgumentException("An identifiable local recording file is required")
        }
        return RecordingRecordEntity(
            id = UUID.randomUUID().toString(),
            audioModelId = audioModelId,
            localFileReference = localFileReference,
            createdAtLocal = requireTimestamp(createdAtLocal),
            markedForTutorReview = markedForTutorReview,
            completionState = completionState,
        )
    }

    fun planInstanceId(localDate: String, planDefinitionId: String): String {
        return "${requireLocalDate(localDate)}:$planDefinitionId"
    }

    /**
     * The checklist records completion without asking for a duration, so the
     * stored duration is zero. Book maps to grammar, AnkiDroid to vocabulary
     * and forms, and a chosen tutor task to tutor work. Listen-record-compare
     * is started in the app and is not completed from the checklist.
     */
    const val CHECKLIST_DURATION_MINUTES = 0

    fun checklistCategory(type: PlanActivityType): ActivityCategory? {
        return when (type) {
            PlanActivityType.BOOK -> ActivityCategory.GRAMMAR
            PlanActivityType.ANKI_DROID -> ActivityCategory.VOCABULARY_AND_FORMS
            PlanActivityType.TUTOR -> ActivityCategory.TUTOR_WORK
            PlanActivityType.LISTEN_RECORD_COMPARE -> null
        }
    }

    /** Completed listen-record-compare is speaking work. Duration is not collected on that screen. */
    fun practiceCategory(): ActivityCategory = ActivityCategory.SPEAKING_AND_PRONUNCIATION
}
