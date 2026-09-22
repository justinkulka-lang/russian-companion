package dev.russiancompanion.domain.practice

/**
 * Full v1 native-audio schema for listen-record-compare.
 *
 * A model is admitted only when licensed redistribution is compatible, the
 * source-supplied stress-marked text is present and verified as the match,
 * and the audio-to-text link is one-to-one. Unmatched, unverified, or
 * incompatibly licensed assets are excluded from practice rather than
 * displayed as a pair. Missing packaged files stay admitted but unplayable;
 * they are not replaced by a recording-only substitute.
 */

internal const val PRACTICE_NO_SCORE_STATEMENT = "No pronunciation score is provided."

internal enum class AudioLicenseStatus {
    COMPATIBLE,
    ABSENT,
    UNCLEAR,
    INCOMPATIBLE,
}

internal enum class AudioInclusion {
    INCLUDED,
    EXCLUDED,
}

internal enum class AudioModification {
    UNMODIFIED,
    MODIFIED,
}

internal enum class AudioShareAlikeConfirmation {
    NOT_APPLICABLE,
    UNCONFIRMED,
    CONFIRMED,
}

internal data class SourceRecordDraft(
    val id: String,
    val provenance: String,
    val redistributionBasis: String,
    val licenseName: String,
    val attribution: String,
    val modificationStatus: String,
    val shareAlikeTreatment: String,
    val shareAlikeConfirmation: String,
)

internal data class SourceTextDraft(
    val id: String,
    val stressMarkedText: String,
    val sourceRecordId: String,
    val modificationStatus: String,
    val matchVerified: Boolean,
)

internal data class AssetRightsDraft(
    val id: String,
    val sourceRecordId: String,
    val assetIdentifier: String,
    val licenseStatus: String,
    val compatibilityDecision: String,
    val attributionRequired: Boolean,
    val inclusionResult: String,
)

internal data class AudioModelDraft(
    val id: String,
    val localAudioReference: String,
    val sourceTextId: String?,
    val sourceRecordId: String?,
    val rightsRecordId: String?,
)

internal data class AudioModelCandidate(
    val model: AudioModelDraft,
    val text: SourceTextDraft?,
    val rights: AssetRightsDraft?,
    val source: SourceRecordDraft?,
)

internal data class AudioAdmission(
    val accepted: Boolean,
    val reason: String? = null,
    val textId: String? = null,
)

internal fun audioIdentityTokenAccepted(value: String): Boolean {
    return value.isNotBlank() && value.none { it == '\t' || it == '\n' || it == '\r' || it == ',' }
}

internal fun parseAudioLicenseStatus(value: String): AudioLicenseStatus? {
    return AudioLicenseStatus.entries.firstOrNull { it.name == value }
}

internal fun parseAudioInclusion(value: String): AudioInclusion? {
    return AudioInclusion.entries.firstOrNull { it.name == value }
}

internal fun parseAudioModification(value: String): AudioModification? {
    return AudioModification.entries.firstOrNull { it.name == value }
}

internal fun parseAudioShareAlikeConfirmation(value: String): AudioShareAlikeConfirmation? {
    return AudioShareAlikeConfirmation.entries.firstOrNull { it.name == value }
}

internal fun admitAudioModel(candidate: AudioModelCandidate): AudioAdmission {
    val model = candidate.model
    if (!audioIdentityTokenAccepted(model.id)) {
        return AudioAdmission(false, "identity")
    }
    if (!audioIdentityTokenAccepted(model.localAudioReference)) {
        return AudioAdmission(false, "audio-reference")
    }
    val text = candidate.text ?: return AudioAdmission(false, "text-missing")
    val rights = candidate.rights ?: return AudioAdmission(false, "rights-missing")
    val source = candidate.source ?: return AudioAdmission(false, "source-missing")
    if (!audioIdentityTokenAccepted(text.id) ||
        !audioIdentityTokenAccepted(rights.id) ||
        !audioIdentityTokenAccepted(source.id)
    ) {
        return AudioAdmission(false, "identity")
    }
    if (model.sourceTextId != text.id || model.rightsRecordId != rights.id || model.sourceRecordId != source.id) {
        return AudioAdmission(false, "link-mismatch")
    }
    if (text.sourceRecordId != source.id || rights.sourceRecordId != source.id) {
        return AudioAdmission(false, "source-mismatch")
    }
    if (parseAudioModification(text.modificationStatus) == null ||
        parseAudioModification(source.modificationStatus) == null
    ) {
        return AudioAdmission(false, "modification")
    }
    if (parseAudioShareAlikeConfirmation(source.shareAlikeConfirmation) == null) {
        return AudioAdmission(false, "share-alike")
    }
    val license = parseAudioLicenseStatus(rights.licenseStatus)
    val compatibility = parseAudioLicenseStatus(rights.compatibilityDecision)
    if (license != AudioLicenseStatus.COMPATIBLE || compatibility != AudioLicenseStatus.COMPATIBLE) {
        return AudioAdmission(false, "license")
    }
    if (parseAudioInclusion(rights.inclusionResult) != AudioInclusion.INCLUDED) {
        return AudioAdmission(false, "inclusion")
    }
    if (!audioIdentityTokenAccepted(rights.assetIdentifier)) {
        return AudioAdmission(false, "identity")
    }
    if (rights.assetIdentifier != model.id && rights.assetIdentifier != model.localAudioReference) {
        return AudioAdmission(false, "asset-mismatch")
    }
    if (!text.matchVerified) {
        return AudioAdmission(false, "unverified-match")
    }
    if (text.stressMarkedText.isBlank()) {
        return AudioAdmission(false, "blank-text")
    }
    if (!sourceCanSupportPractice(source, rights.attributionRequired)) {
        return AudioAdmission(false, "source-incomplete")
    }
    return AudioAdmission(accepted = true, textId = text.id)
}

internal fun admitAudioModels(candidates: List<AudioModelCandidate>): List<AudioAdmission> {
    val preliminary = candidates.map(::admitAudioModel)
    val sharedTextIds = preliminary
        .filter { it.accepted && it.textId != null }
        .groupingBy { it.textId }
        .eachCount()
        .filterValues { it > 1 }
        .keys
    if (sharedTextIds.isEmpty()) return preliminary
    return preliminary.map { admission ->
        if (admission.accepted && admission.textId in sharedTextIds) {
            AudioAdmission(false, "shared-text", admission.textId)
        } else {
            admission
        }
    }
}

internal fun catalogProposalCandidate(proposal: CatalogAudioProposal): AudioModelCandidate {
    val sourceId = "source-${proposal.id}"
    val textId = "text-${proposal.id}"
    val rightsId = "rights-${proposal.id}"
    return AudioModelCandidate(
        model = AudioModelDraft(
            id = proposal.id,
            localAudioReference = proposal.localAudioReference,
            sourceTextId = textId,
            sourceRecordId = sourceId,
            rightsRecordId = rightsId,
        ),
        text = SourceTextDraft(
            id = textId,
            stressMarkedText = proposal.stressMarkedText,
            sourceRecordId = sourceId,
            modificationStatus = proposal.modificationStatus,
            matchVerified = proposal.matchVerified,
        ),
        rights = AssetRightsDraft(
            id = rightsId,
            sourceRecordId = sourceId,
            assetIdentifier = proposal.id,
            licenseStatus = proposal.licenseStatus,
            compatibilityDecision = proposal.compatibilityDecision,
            attributionRequired = true,
            inclusionResult = AudioInclusion.INCLUDED.name,
        ),
        source = SourceRecordDraft(
            id = sourceId,
            provenance = proposal.provenance,
            redistributionBasis = proposal.redistributionBasis,
            licenseName = proposal.licenseName,
            attribution = proposal.attribution,
            modificationStatus = proposal.modificationStatus,
            shareAlikeTreatment = proposal.shareAlikeTreatment,
            shareAlikeConfirmation = proposal.shareAlikeConfirmation,
        ),
    )
}

internal fun admitCatalogProposal(proposal: CatalogAudioProposal): AudioAdmission {
    return admitAudioModel(catalogProposalCandidate(proposal))
}

internal data class CatalogAudioProposal(
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

private fun sourceCanSupportPractice(source: SourceRecordDraft, attributionRequired: Boolean): Boolean {
    if (source.provenance.isBlank() || source.redistributionBasis.isBlank() || source.licenseName.isBlank()) {
        return false
    }
    if (source.shareAlikeTreatment.isBlank()) return false
    if (attributionRequired && source.attribution.isBlank()) return false
    return true
}
