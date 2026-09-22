package dev.russiancompanion.data.local

/**
 * Closed stored values for governed records.
 * Book and AnkiDroid evidence use [SelfAttestationStatus.SELF_ATTESTED] only.
 * There is no independently verified status.
 */
internal enum class PlanActivityType {
    BOOK,
    ANKI_DROID,
    LISTEN_RECORD_COMPARE,
    TUTOR,
}

/** MA-069 lists these seven categories. */
internal enum class ActivityCategory {
    READING,
    LISTENING,
    VOCABULARY_AND_FORMS,
    GRAMMAR,
    SPEAKING_AND_PRONUNCIATION,
    WRITING,
    TUTOR_WORK,
}

internal enum class EvidenceType {
    BOOK_REVISION_SCORE,
    ANKI_DROID_STATUS,
    PRESCRIBED_WORK_COMPLETION,
    TUTOR_SIGN_OFF,
}

internal enum class SelfAttestationStatus {
    SELF_ATTESTED,
    USER_RECORDED,
}

internal enum class CompletionState {
    NOT_COMPLETED,
    COMPLETED,
}

internal enum class GateDecisionStatus {
    SATISFIED,
    NOT_YET_SATISFIED,
    NOT_EVALUABLE,
}

internal enum class ExportStatus {
    COMPLETED,
    FAILED,
    NOT_CONFIRMED,
}

internal enum class EligibilityStatus {
    ELIGIBLE,
    EXCLUDED,
}

internal enum class LicenseStatus {
    COMPATIBLE,
    ABSENT,
    UNCLEAR,
    INCOMPATIBLE,
}

internal enum class InclusionResult {
    INCLUDED,
    EXCLUDED,
}

internal enum class ModificationStatus {
    UNMODIFIED,
    MODIFIED,
}

internal enum class ShareAlikeConfirmation {
    NOT_APPLICABLE,
    UNCONFIRMED,
    CONFIRMED,
}

internal enum class RuleOperator {
    PRESENT,
    AT_LEAST,
    EQUALS,
}

internal fun evidenceSelfAttestation(type: EvidenceType): SelfAttestationStatus {
    return when (type) {
        EvidenceType.BOOK_REVISION_SCORE,
        EvidenceType.ANKI_DROID_STATUS,
        -> SelfAttestationStatus.SELF_ATTESTED
        EvidenceType.PRESCRIBED_WORK_COMPLETION,
        EvidenceType.TUTOR_SIGN_OFF,
        -> SelfAttestationStatus.USER_RECORDED
    }
}
