package dev.russiancompanion.v2.curriculum

/**
 * Identity for content-audit corrections that cannot stay inside a faulty frozen excerpt.
 * The historical source row remains in the pack. The learner-facing correction is a
 * review-pending research note. This identity is not linguistic acceptance.
 */
internal object ContentAuditSources {
    const val SOURCE_ID = "src-content-audit-research"
    const val REVISION = "content-audit-2026-10-08"
    const val TRANSFORMATION = "content-audit research synthesis"
    const val DATE = "2026-10-08"
    const val LICENSE = "Original app text"

    /** Schema 2 omitted objective gating. An accepted digest of that schema is obsolete. */
    const val OBSOLETE_CONTENT_REVIEW_DIGEST_SCHEMA = 2
}
