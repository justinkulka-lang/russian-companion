package dev.russiancompanion.catalog

import dev.russiancompanion.release.RELEASE_EVIDENCE_MANIFEST
import dev.russiancompanion.release.ReleaseDecision
import dev.russiancompanion.release.assessRepositoryRelease
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class CatalogIntakeTest {
    @Test
    fun committedIntakeDoesNotInstallACatalog() {
        val root = repositoryRoot()
        val intake = File(root, CATALOG_INTAKE).readText()
        val assessment = assessCatalogIntake(intake)
        assertEquals(true, assessment.readable)
        assertEquals(INTAKE_NOT_INSTALLED, assessment.state)
        assertEquals(false, assessment.replacePackagedSeed)
        assertEquals(emptyList<String>(), assessment.eligibleIds)
        val seed = File(root, "app/src/main/assets/content/bundled-content.json").readText()
        assertTrue(seed.contains("\"courseSegments\": []"))
        assertTrue(seed.contains("\"audioModels\": []"))
    }

    @Test
    fun debugCiEvidenceStaysOutOfTheReleaseGate() {
        val root = repositoryRoot()
        val record = File(root, "release/phase-9-debug-ci.json").readText()
        assertTrue(record.contains("07479eb39f2515a1c00713481e43dfd5ee9b3eef"))
        assertTrue(record.contains("https://github.com/justinkulka-lang/russian-companion/actions/runs/35666716411"))
        assertTrue(record.contains("\"tests\": 93"))
        assertTrue(record.contains("\"skippedTest\": \"AT-020\""))
        assertTrue(record.contains("\"publicRelease\": \"NOT_APPROVED\""))
        val retained = File(root, RELEASE_EVIDENCE_MANIFEST).readText()
        assertFalse(retained.contains("07479eb"))
        assertFalse(retained.contains("35666716411"))
        assertTrue(retained.contains("\"procedure\": \"AT-004\""))
        assertTrue(retained.contains("\"state\": \"MISSING\""))
        assertFalse(retained.contains("RETAINED"))
        val release = assessRepositoryRelease(root)
        assertEquals(ReleaseDecision.NOT_APPROVED, release.decision)
        val gate = File("src/main/java/dev/russiancompanion/release/ReleaseGate.kt").readText()
        assertFalse(gate.contains("phase-9-debug-ci"))
    }

    @Test
    fun missingLicenseOrUnverifiedTextIsExcluded() {
        val missingLicense = proposal(id = "audio-absent", licenseStatus = "ABSENT", compatibilityDecision = "ABSENT")
        val unverified = proposal(id = "audio-unverified", matchVerified = false)
        val assessment = assessCatalogIntake(
            intake("INSTALLED_DROP", missingLicense, unverified),
        )
        assertEquals(false, assessment.replacePackagedSeed)
        assertEquals(listOf("audio-absent", "audio-unverified"), assessment.excludedIds)
    }

    @Test
    fun aCompleteProposalCanBeEligibleWithoutReplacingTheEmptyIntake() {
        val assessment = assessCatalogIntake(intake("NOT_INSTALLED", proposal()))
        assertEquals(listOf("audio-1"), assessment.eligibleIds)
        assertEquals(false, assessment.replacePackagedSeed)
    }

    @Test
    fun anInstalledDropOfOneEligibleProposalMayReplaceTheSeed() {
        val assessment = assessCatalogIntake(intake("INSTALLED_DROP", proposal()))
        assertEquals(true, assessment.replacePackagedSeed)
        assertEquals(listOf("audio-1"), assessment.eligibleIds)
    }

    private fun repositoryRoot(): File {
        val candidates = listOf(File("."), File(".."))
        return candidates.first { File(it, CATALOG_INTAKE).isFile }.canonicalFile
    }

    private fun intake(state: String, vararg proposals: AudioProposal): String {
        val rows = proposals.joinToString(",") { proposal ->
            """{"id":"${proposal.id}","localAudioReference":"${proposal.localAudioReference}","licenseStatus":"${proposal.licenseStatus}","compatibilityDecision":"${proposal.compatibilityDecision}","stressMarkedText":"${proposal.stressMarkedText}","matchVerified":${proposal.matchVerified},"provenance":"${proposal.provenance}","redistributionBasis":"${proposal.redistributionBasis}","licenseName":"${proposal.licenseName}","attribution":"${proposal.attribution}","modificationStatus":"${proposal.modificationStatus}","shareAlikeTreatment":"${proposal.shareAlikeTreatment}","shareAlikeConfirmation":"${proposal.shareAlikeConfirmation}"}"""
        }
        return """{"intakeVersion":1,"state":"$state","proposals":[$rows]}"""
    }

    private fun proposal(
        id: String = "audio-1",
        licenseStatus: String = "COMPATIBLE",
        compatibilityDecision: String = "COMPATIBLE",
        matchVerified: Boolean = true,
    ) = AudioProposal(
        id = id,
        localAudioReference = "audio/audio-1",
        licenseStatus = licenseStatus,
        compatibilityDecision = compatibilityDecision,
        stressMarkedText = "SOURCE-TEXT-TOKEN",
        matchVerified = matchVerified,
        provenance = "test-source",
        redistributionBasis = "test-rights",
        licenseName = "TEST-LICENSE",
        attribution = "Test attribution",
        modificationStatus = "UNMODIFIED",
        shareAlikeTreatment = "share-alike-applies",
        shareAlikeConfirmation = "UNCONFIRMED",
    )
}
