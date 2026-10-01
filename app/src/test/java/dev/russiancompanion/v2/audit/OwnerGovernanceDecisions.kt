package dev.russiancompanion.v2.audit

import dev.russiancompanion.release.computeReleaseSourceDigest
import dev.russiancompanion.v2.curriculum.AUTHORIZED_FINAL_AUDIT_DIRECTORY
import dev.russiancompanion.v2.curriculum.FINAL_CURRICULUM_AUDIT_PASSED
import dev.russiancompanion.v2.curriculum.RetainedFinalAuditDocument
import dev.russiancompanion.v2.curriculum.RetainedUnitAuditDisposition
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.encodeRetainedFinalAudit
import dev.russiancompanion.v2.curriculum.retainedFinalAuditDefects
import dev.russiancompanion.v2.curriculum.unit
import dev.russiancompanion.v2.review.curriculumCommitForUnit
import dev.russiancompanion.v2.review.governedMappingFacts
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeParseException

/**
 * Test-only Product Owner decision checks. Production validation does not read
 * this file. A passing evaluation does not activate a variance, accept a
 * mapping, or write a final-audit artifact.
 */
internal const val OWNER_GOVERNANCE_PREPARATION_PATH =
    "audit/owner-governance/FINAL_AUDIT_PREPARATION_MANIFEST.json"

internal const val PREPARATION_DISPOSITION = "NOT_RECORDED"

internal const val RETAINED_PRIVATE_FILE_SHA_UNITS_11_20 =
    "2bcc0c45fac4508adf283f4dd939a7e578cf2c1306fdadf4966300532beb6303"

internal val EVIDENCE_CLASS_NAMES = listOf(
    "QUOTED_OR_ADAPTED_SOURCE",
    "ORIGINAL_RESEARCH_SYNTHESIS",
)

internal enum class DecisionSupport {
    OWNER_SIGNATURE,
    CI_SUCCESS,
    COUNTER,
    REVIEW_STATUS,
    MODEL_NAME,
}

internal data class DecisionEvaluation(
    val accepted: Boolean,
    val defects: List<String>,
    val productionUnchanged: Boolean = true,
)

internal data class VarianceDecisionAttempt(
    val unitNumber: Int,
    val disposition: String,
    val ownerName: String,
    val date: String,
    val signature: String,
    val contentDigest: String,
    val mappingPackageDigest: String,
    val repositorySourceDigest: String,
    val support: DecisionSupport,
)

internal data class MappingAcceptanceAttempt(
    val unitNumber: Int,
    val disposition: String,
    val isbn: String,
    val editionTitle: String,
    val printedPageStart: Int?,
    val printedPageEnd: Int?,
    val mappingPackageDigest: String,
    val ownedCopyVerified: String,
    val ownerName: String,
    val date: String,
    val signature: String,
    val support: DecisionSupport,
)

private val ISO_DATE = Regex("^\\d{4}-\\d{2}-\\d{2}$")

private val preparationJson = Json {
    ignoreUnknownKeys = false
    explicitNulls = true
}

internal fun evaluateVarianceDecision(
    pack: V2CurriculumPack,
    repositoryRoot: File,
    attempt: VarianceDecisionAttempt,
): DecisionEvaluation {
    val defects = mutableListOf<String>()
    if (attempt.support != DecisionSupport.OWNER_SIGNATURE) {
        defects += "non-owner signal cannot activate a variance"
    }
    if (attempt.unitNumber !in setOf(27, 29, 30)) {
        defects += "variance decision is outside Units 27, 29, and 30"
    }
    if (attempt.disposition !in setOf("ACTIVATE", "REJECT", "REVISE")) {
        defects += "variance disposition is blank or incomplete"
    }
    if (attempt.ownerName.isBlank()) defects += "owner name is blank"
    if (!isRealIsoDate(attempt.date)) defects += "owner date is blank or incomplete"
    if (attempt.signature.isBlank()) defects += "owner signature is blank"
    val unit = pack.unit(attempt.unitNumber)
    val content = unit?.let { V2Validator.unitContentReviewDigest(pack, it.number) }
    if (content == null || attempt.contentDigest != content) {
        defects += "content digest does not match the packaged unit"
    }
    val mapping = governedMappingFacts(attempt.unitNumber).let { facts ->
        if (facts.mappingDisposition == null) null else dev.russiancompanion.v2.review.mappingPackageDigest(facts)
    }
    if (mapping == null || attempt.mappingPackageDigest != mapping) {
        defects += "mapping digest does not match the retained worksheet"
    }
    val repositoryDigest = computeReleaseSourceDigest(repositoryRoot)
    if (attempt.repositorySourceDigest != repositoryDigest) {
        defects += "repository source digest does not match the live tree"
    }
    return DecisionEvaluation(
        accepted = defects.isEmpty(),
        defects = defects,
        productionUnchanged = true,
    )
}

internal fun evaluateMappingAcceptance(
    attempt: MappingAcceptanceAttempt,
): DecisionEvaluation {
    val defects = mutableListOf<String>()
    if (attempt.support != DecisionSupport.OWNER_SIGNATURE) {
        defects += "non-owner signal cannot approve a mapping"
    }
    if (attempt.unitNumber !in 1..30) defects += "mapping unit is outside Units 1-30"
    if (attempt.disposition != "MAPPING_APPROVED") {
        defects += "mapping disposition is blank or incomplete"
    }
    if (attempt.ownerName.isBlank()) defects += "owner name is blank"
    if (!isRealIsoDate(attempt.date)) defects += "owner date is blank or incomplete"
    if (attempt.signature.isBlank()) defects += "owner signature is blank"
    val facts = governedMappingFacts(attempt.unitNumber)
    if (facts.isbn != null) {
        defects += "retained ISBN identity is unexpected"
    }
    if (attempt.isbn.isBlank()) {
        defects += "ISBN is unresolved"
    } else {
        defects += "invented ISBN"
    }
    if (attempt.printedPageStart != facts.printedPageStart || attempt.printedPageEnd != facts.printedPageEnd) {
        defects += "mismatched page range"
    }
    if (attempt.editionTitle != facts.editionTitle) {
        defects += "mismatched edition identity"
    }
    val digest = dev.russiancompanion.v2.review.mappingPackageDigest(facts)
    if (attempt.mappingPackageDigest != digest) {
        defects += "mismatched mapping digest"
    }
    if (attempt.ownedCopyVerified.isBlank()) {
        defects += "owned-copy verification is unresolved"
    } else {
        defects += "ownership assertion is not retained verification"
    }
    if (facts.mappingDisposition != "PENDING_CURRICULUM_REVIEWER") {
        defects += "retained mapping disposition is not pending"
    }
    defects += "mapping remains PENDING_CURRICULUM_REVIEWER"
    return DecisionEvaluation(
        accepted = false,
        defects = defects,
        productionUnchanged = true,
    )
}

internal fun renderFinalAuditPreparationManifest(
    pack: V2CurriculumPack,
    repositoryRoot: File,
): String {
    val repositoryDigest = computeReleaseSourceDigest(repositoryRoot)
    val packDigest = V2Validator.contentReviewDigest(pack)
    return buildString {
        append("{\n")
        append("  \"kind\": \"final-curriculum-audit-preparation\",\n")
        append("  \"disposition\": \"$PREPARATION_DISPOSITION\",\n")
        append("  \"futureRetentionDirectory\": \"$AUTHORIZED_FINAL_AUDIT_DIRECTORY\",\n")
        append("  \"preparationPath\": \"$OWNER_GOVERNANCE_PREPARATION_PATH\",\n")
        append("  \"modelVersion\": \"\",\n")
        append("  \"date\": \"\",\n")
        append("  \"packContentReviewDigest\": \"$packDigest\",\n")
        append("  \"repositorySourceDigest\": \"$repositoryDigest\",\n")
        append("  \"curriculumSourceCommits\": {\n")
        (1..30).forEach { number ->
            val comma = if (number == 30) "" else ","
            append("    \"$number\": \"${curriculumCommitForUnit(number)}\"$comma\n")
        }
        append("  },\n")
        append("  \"evidenceClasses\": [\n")
        EVIDENCE_CLASS_NAMES.forEachIndexed { index, name ->
            val comma = if (index == EVIDENCE_CLASS_NAMES.lastIndex) "" else ","
            append("    \"$name\"$comma\n")
        }
        append("  ],\n")
        append("  \"evidenceRecords\": [],\n")
        append("  \"unitDispositions\": [\n")
        (1..30).forEach { number ->
            val digest = V2Validator.unitContentReviewDigest(pack, number)
            val comma = if (number == 30) "" else ","
            append("    {\"unitNumber\": $number, \"contentDigest\": \"$digest\", \"disposition\": \"\"}$comma\n")
        }
        append("  ],\n")
        append("  \"varianceDecision\": \"\",\n")
        append("  \"mappingDecision\": \"\"\n")
        append("}\n")
    }
}

internal fun preparationManifestIsNotAPassingAudit(text: String): List<String> {
    val defects = mutableListOf<String>()
    if (!text.contains("\"disposition\": \"$PREPARATION_DISPOSITION\"")) {
        defects += "preparation disposition is not NOT_RECORDED"
    }
    if (text.contains("\"disposition\": \"$FINAL_CURRICULUM_AUDIT_PASSED\"")) {
        defects += "preparation manifest records a passing final audit"
    }
    try {
        preparationJson.decodeFromString(RetainedFinalAuditDocument.serializer(), text)
        defects += "preparation manifest decoded as a retained final audit"
    } catch (_: SerializationException) {
        // Expected. The preparation document is not the retained-audit schema.
    } catch (_: IllegalArgumentException) {
        // Expected for a document the retained decoder rejects.
    }
    val synthetic = RetainedFinalAuditDocument(
        schemaVersion = 1,
        disposition = PREPARATION_DISPOSITION,
        modelVersion = "",
        date = "",
        auditedContentDigest = "",
        unitDispositions = listOf(
            RetainedUnitAuditDisposition(
                unitNumber = 1,
                auditedContentDigest = "",
                disposition = "",
            ),
        ),
    )
    val encoded = encodeRetainedFinalAudit(synthetic)
    val auditDefects = retainedFinalAuditDefects(
        repositoryRoot = null,
        audit = dev.russiancompanion.v2.curriculum.V2FinalAuditIdentity(
            artifactIdentity = "$AUTHORIZED_FINAL_AUDIT_DIRECTORY/not-recorded.json",
            artifactDigest = dev.russiancompanion.v2.curriculum.sha256Bytes(encoded),
            auditedContentDigest = "",
            disposition = PREPARATION_DISPOSITION,
            modelVersion = "",
            date = "",
        ),
        governedDigest = "abc",
        unitNumber = null,
        packArtifactIdentity = null,
    )
    if (auditDefects.isEmpty()) defects += "a NOT_RECORDED document passed the retained-audit gate"
    return defects
}

private fun isRealIsoDate(value: String): Boolean {
    if (!ISO_DATE.matches(value)) return false
    return try {
        LocalDate.parse(value)
        true
    } catch (_: DateTimeParseException) {
        false
    }
}
