package dev.russiancompanion.acceptance

/**
 * Classification of acceptance procedures for this build.
 * OUTSTANDING procedures have not been executed to the criterion's preconditions.
 * A green automated test does not move an outstanding procedure to SATISFIED.
 *
 * Release-hardening copy now present in the app:
 * generic offline-companion onboarding; grammar practice in the user's book and answer key;
 * vocabulary and forms practice remaining in AnkiDroid as a user report.
 * AT-001 stays outstanding. Units 1-5 are development content with review
 * still pending. Units 6-30 are not yet installed. Linguistic review and
 * Penguin edition mapping are pending. Structural tests do not satisfy AT-001.
 * AT-002, AT-004, AT-008, AT-009, AT-010, AT-020, AT-030, and AT-031
 * stay outstanding. A physical-device acceptance package lives under
 * release/acceptance/; those forms and working audits are not retained
 * evidence. assembleRelease reads release/retained-evidence.json
 * and refuses a public release while any retained-evidence record is
 * missing. This is not a release approval.
 */
internal enum class AcceptanceDisposition {
    SATISFIED_BY_AUTOMATED_TEST,
    NOT_APPLICABLE,
    OUTSTANDING,
}

internal val ACCEPTANCE_DISPOSITION: Map<String, AcceptanceDisposition> = mapOf(
    "AT-001" to AcceptanceDisposition.OUTSTANDING,
    "AT-002" to AcceptanceDisposition.OUTSTANDING,
    "AT-003" to AcceptanceDisposition.SATISFIED_BY_AUTOMATED_TEST,
    "AT-004" to AcceptanceDisposition.OUTSTANDING,
    "AT-005" to AcceptanceDisposition.SATISFIED_BY_AUTOMATED_TEST,
    "AT-006" to AcceptanceDisposition.SATISFIED_BY_AUTOMATED_TEST,
    "AT-007" to AcceptanceDisposition.SATISFIED_BY_AUTOMATED_TEST,
    "AT-008" to AcceptanceDisposition.OUTSTANDING,
    "AT-009" to AcceptanceDisposition.OUTSTANDING,
    "AT-010" to AcceptanceDisposition.OUTSTANDING,
    "AT-011" to AcceptanceDisposition.SATISFIED_BY_AUTOMATED_TEST,
    "AT-012" to AcceptanceDisposition.SATISFIED_BY_AUTOMATED_TEST,
    "AT-013" to AcceptanceDisposition.SATISFIED_BY_AUTOMATED_TEST,
    "AT-014" to AcceptanceDisposition.SATISFIED_BY_AUTOMATED_TEST,
    "AT-015" to AcceptanceDisposition.SATISFIED_BY_AUTOMATED_TEST,
    "AT-016" to AcceptanceDisposition.SATISFIED_BY_AUTOMATED_TEST,
    "AT-017" to AcceptanceDisposition.SATISFIED_BY_AUTOMATED_TEST,
    "AT-018" to AcceptanceDisposition.SATISFIED_BY_AUTOMATED_TEST,
    "AT-019" to AcceptanceDisposition.SATISFIED_BY_AUTOMATED_TEST,
    "AT-020" to AcceptanceDisposition.OUTSTANDING,
    "AT-021" to AcceptanceDisposition.SATISFIED_BY_AUTOMATED_TEST,
    "AT-022" to AcceptanceDisposition.SATISFIED_BY_AUTOMATED_TEST,
    "AT-023" to AcceptanceDisposition.SATISFIED_BY_AUTOMATED_TEST,
    "AT-024" to AcceptanceDisposition.SATISFIED_BY_AUTOMATED_TEST,
    "AT-025" to AcceptanceDisposition.NOT_APPLICABLE,
    "AT-026" to AcceptanceDisposition.NOT_APPLICABLE,
    "AT-027" to AcceptanceDisposition.NOT_APPLICABLE,
    "AT-028" to AcceptanceDisposition.SATISFIED_BY_AUTOMATED_TEST,
    "AT-029" to AcceptanceDisposition.SATISFIED_BY_AUTOMATED_TEST,
    "AT-030" to AcceptanceDisposition.OUTSTANDING,
    "AT-031" to AcceptanceDisposition.OUTSTANDING,
)
