package dev.russiancompanion.domain.practice

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class AudioSchemaTest {
    @Test
    fun admissionRequiresCompatibleRightsAndAVerifiedOneToOneTextMatch() {
        assertTrue(admitAudioModel(eligibleCandidate()).accepted)
        assertEquals("license", admitAudioModel(eligibleCandidate(licenseStatus = "ABSENT")).reason)
        assertEquals("license", admitAudioModel(eligibleCandidate(licenseStatus = "UNCLEAR")).reason)
        assertEquals("license", admitAudioModel(eligibleCandidate(licenseStatus = "INCOMPATIBLE")).reason)
        assertEquals("unverified-match", admitAudioModel(eligibleCandidate(matchVerified = false)).reason)
        assertEquals("blank-text", admitAudioModel(eligibleCandidate(stressMarkedText = "   ".trim())).reason)
        assertEquals("text-missing", admitAudioModel(eligibleCandidate().copy(text = null)).reason)
        assertEquals("rights-missing", admitAudioModel(eligibleCandidate().copy(rights = null)).reason)
        assertEquals("source-missing", admitAudioModel(eligibleCandidate().copy(source = null)).reason)
        assertEquals(
            "asset-mismatch",
            admitAudioModel(eligibleCandidate(assetIdentifier = "other-audio")).reason,
        )
        assertEquals(
            "source-mismatch",
            admitAudioModel(
                eligibleCandidate().let { candidate ->
                    candidate.copy(text = candidate.text?.copy(sourceRecordId = "source-other"))
                },
            ).reason,
        )
        assertEquals("identity", admitAudioModel(eligibleCandidate(id = "audio\t1")).reason)
        assertEquals("identity", admitAudioModel(eligibleCandidate(id = "audio,1")).reason)
        assertEquals("audio-reference", admitAudioModel(eligibleCandidate(localAudioReference = "")).reason)
        assertEquals("inclusion", admitAudioModel(eligibleCandidate(inclusionResult = "EXCLUDED")).reason)
        assertEquals("source-incomplete", admitAudioModel(eligibleCandidate(attribution = "")).reason)
        assertEquals("share-alike", admitAudioModel(eligibleCandidate(shareAlikeConfirmation = "UNKNOWN")).reason)
    }

    @Test
    fun sharedTextMatchesAreExcludedInsteadOfDisplayedAsAPair() {
        val first = eligibleCandidate(id = "audio-1")
        val second = eligibleCandidate(id = "audio-2", assetIdentifier = "audio-2")
        val admissions = admitAudioModels(listOf(first, second))
        assertTrue(admissions.all { !it.accepted })
        assertTrue(admissions.all { it.reason == "shared-text" })
        val unique = admitAudioModels(
            listOf(
                eligibleCandidate(id = "audio-1"),
                eligibleCandidate(
                    id = "audio-2",
                    assetIdentifier = "audio-2",
                    textId = "text-2",
                    stressMarkedText = "OTHER_SOURCE_TEXT_TOKEN",
                ),
            ),
        )
        assertTrue(unique.all { it.accepted })
    }

    @Test
    fun catalogProposalsUseTheSameAdmissionRule() {
        assertTrue(admitCatalogProposal(catalogProposal()).accepted)
        assertFalse(admitCatalogProposal(catalogProposal(matchVerified = false)).accepted)
        assertFalse(admitCatalogProposal(catalogProposal(licenseStatus = "ABSENT")).accepted)
        assertFalse(admitCatalogProposal(catalogProposal(id = "audio\n1")).accepted)
        assertTrue(admitCatalogProposal(catalogProposal(shareAlikeConfirmation = "CONFIRMED")).accepted)
        assertTrue(admitCatalogProposal(catalogProposal(shareAlikeConfirmation = "NOT_APPLICABLE")).accepted)
    }

    @Test
    fun disclosureStatementStaysThePackagedNoScoreCopy() {
        val xml = listOf(File("src/main/res/values/strings.xml"), File("app/src/main/res/values/strings.xml"))
            .first { it.isFile }
            .readText()
        assertTrue(xml.contains(PRACTICE_NO_SCORE_STATEMENT))
        assertFalse(practiceJudgmentLabelForbidden("Play model"))
        assertFalse(practiceJudgmentLabelForbidden("Record"))
        assertFalse(practiceJudgmentLabelForbidden("Complete practice"))
        assertTrue(practiceJudgmentLabelForbidden("Pronunciation fluency score"))
    }

    private fun eligibleCandidate(
        id: String = "audio-1",
        localAudioReference: String = "audio/audio-1",
        textId: String = "text-1",
        sourceId: String = "source-1",
        rightsId: String = "rights-1",
        assetIdentifier: String = "audio-1",
        licenseStatus: String = "COMPATIBLE",
        inclusionResult: String = "INCLUDED",
        matchVerified: Boolean = true,
        stressMarkedText: String = "SOURCE_TEXT_TOKEN",
        attribution: String = "Test attribution",
        shareAlikeConfirmation: String = "UNCONFIRMED",
    ): AudioModelCandidate {
        return AudioModelCandidate(
            model = AudioModelDraft(
                id = id,
                localAudioReference = localAudioReference,
                sourceTextId = textId,
                sourceRecordId = sourceId,
                rightsRecordId = rightsId,
            ),
            text = SourceTextDraft(
                id = textId,
                stressMarkedText = stressMarkedText,
                sourceRecordId = sourceId,
                modificationStatus = "UNMODIFIED",
                matchVerified = matchVerified,
            ),
            rights = AssetRightsDraft(
                id = rightsId,
                sourceRecordId = sourceId,
                assetIdentifier = assetIdentifier,
                licenseStatus = licenseStatus,
                compatibilityDecision = licenseStatus,
                attributionRequired = true,
                inclusionResult = inclusionResult,
            ),
            source = SourceRecordDraft(
                id = sourceId,
                provenance = "test-source",
                redistributionBasis = "test-rights",
                licenseName = "TEST-LICENSE",
                attribution = attribution,
                modificationStatus = "UNMODIFIED",
                shareAlikeTreatment = "share-alike-applies",
                shareAlikeConfirmation = shareAlikeConfirmation,
            ),
        )
    }

    private fun catalogProposal(
        id: String = "audio-1",
        licenseStatus: String = "COMPATIBLE",
        matchVerified: Boolean = true,
        shareAlikeConfirmation: String = "UNCONFIRMED",
    ): CatalogAudioProposal {
        return CatalogAudioProposal(
            id = id,
            localAudioReference = "audio/audio-1",
            licenseStatus = licenseStatus,
            compatibilityDecision = licenseStatus,
            stressMarkedText = "SOURCE_TEXT_TOKEN",
            matchVerified = matchVerified,
            provenance = "test-source",
            redistributionBasis = "test-rights",
            licenseName = "TEST-LICENSE",
            attribution = "Test attribution",
            modificationStatus = "UNMODIFIED",
            shareAlikeTreatment = "share-alike-applies",
            shareAlikeConfirmation = shareAlikeConfirmation,
        )
    }
}
