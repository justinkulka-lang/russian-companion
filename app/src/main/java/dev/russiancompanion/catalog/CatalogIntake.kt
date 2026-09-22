package dev.russiancompanion.catalog

import dev.russiancompanion.domain.practice.CatalogAudioProposal
import dev.russiancompanion.domain.practice.admitCatalogProposal
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

internal const val CATALOG_INTAKE = "catalog/intake.json"

internal const val CATALOG_SOURCE_DECISIONS = "catalog/source-decisions.json"

internal const val INTAKE_NOT_INSTALLED = "NOT_INSTALLED"

private val intakeJson = Json {
    ignoreUnknownKeys = false
    explicitNulls = true
}

@Serializable
internal data class CatalogIntakeFile(
    val intakeVersion: Int,
    val state: String,
    val proposals: List<AudioProposal>,
)

@Serializable
internal data class AudioProposal(
    val id: String,
    val localAudioReference: String,
    val licenseStatus: String,
    val compatibilityDecision: String,
    val stressMarkedText: String,
    val matchVerified: Boolean,
    val provenance: String,
    val redistributionBasis: String,
    val licenseName: String,
    val attribution: String,
    val modificationStatus: String,
    val shareAlikeTreatment: String,
    val shareAlikeConfirmation: String,
)

internal data class IntakeAssessment(
    val readable: Boolean,
    val state: String,
    val replacePackagedSeed: Boolean,
    val eligibleIds: List<String>,
    val excludedIds: List<String>,
)

@Serializable
internal data class SourceDecisionsFile(
    val decisionsVersion: Int,
    val decisions: List<SourceDecision>,
)

@Serializable
internal data class SourceDecision(
    val sourceId: String,
    val provenance: String,
    val decision: String,
    val licenseName: String,
    val reason: String,
)

internal fun assessCatalogIntake(
    raw: String?,
    sourceDecisions: String? = null,
): IntakeAssessment {
    val blockedProvenance = blockedProvenance(sourceDecisions)
        ?: return IntakeAssessment(
            readable = false,
            state = "UNREADABLE",
            replacePackagedSeed = false,
            eligibleIds = emptyList(),
            excludedIds = emptyList(),
        )
    val parsed = parseIntake(raw) ?: return IntakeAssessment(
        readable = false,
        state = "UNREADABLE",
        replacePackagedSeed = false,
        eligibleIds = emptyList(),
        excludedIds = emptyList(),
    )
    val eligible = mutableListOf<String>()
    val excluded = mutableListOf<String>()
    for (proposal in parsed.proposals) {
        val blocked = proposal.provenance in blockedProvenance
        if (!blocked && proposalIsEligible(proposal)) eligible += proposal.id else excluded += proposal.id
    }
    val replace = parsed.state == "INSTALLED_DROP" && eligible.isNotEmpty() && excluded.isEmpty()
    return IntakeAssessment(
        readable = true,
        state = parsed.state,
        replacePackagedSeed = replace,
        eligibleIds = eligible,
        excludedIds = excluded,
    )
}

private fun blockedProvenance(raw: String?): Set<String>? {
    if (raw == null) return emptySet()
    val parsed = try {
        intakeJson.decodeFromString(SourceDecisionsFile.serializer(), raw)
    } catch (_: SerializationException) {
        return null
    } catch (_: IllegalArgumentException) {
        return null
    }
    if (parsed.decisionsVersion != 1) return null
    return parsed.decisions
        .filter { it.decision == "EXCLUDED" && it.provenance.isNotBlank() }
        .map { it.provenance }
        .toSet()
}

private fun parseIntake(raw: String?): CatalogIntakeFile? {
    if (raw == null) return null
    return try {
        val parsed = intakeJson.decodeFromString(CatalogIntakeFile.serializer(), raw)
        if (parsed.intakeVersion != 1) null else parsed
    } catch (_: SerializationException) {
        null
    } catch (_: IllegalArgumentException) {
        null
    }
}

private fun proposalIsEligible(proposal: AudioProposal): Boolean {
    return admitCatalogProposal(
        CatalogAudioProposal(
            id = proposal.id,
            localAudioReference = proposal.localAudioReference,
            licenseStatus = proposal.licenseStatus,
            compatibilityDecision = proposal.compatibilityDecision,
            stressMarkedText = proposal.stressMarkedText,
            matchVerified = proposal.matchVerified,
            provenance = proposal.provenance,
            redistributionBasis = proposal.redistributionBasis,
            licenseName = proposal.licenseName,
            attribution = proposal.attribution,
            modificationStatus = proposal.modificationStatus,
            shareAlikeTreatment = proposal.shareAlikeTreatment,
            shareAlikeConfirmation = proposal.shareAlikeConfirmation,
        ),
    ).accepted
}
