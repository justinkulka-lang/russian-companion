package dev.russiancompanion.release

import dev.russiancompanion.data.local.audio
import dev.russiancompanion.data.local.gate
import dev.russiancompanion.data.local.minimalDocument
import dev.russiancompanion.data.local.rights
import dev.russiancompanion.data.local.seed.GateConditionSeed
import dev.russiancompanion.data.local.seed.PlanDefinitionSeed
import dev.russiancompanion.data.local.seed.SeedJson
import dev.russiancompanion.data.local.source
import dev.russiancompanion.data.local.text
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.nio.file.Files

class ReleaseGateTest {
    @Test
    fun packagedCatalogAndCommittedEvidenceBlockPublicRelease() {
        val root = repositoryRoot()
        val manifest = File(root, RELEASE_EVIDENCE_MANIFEST).readText()
        assertFalse(manifest.contains("RETAINED"))
        val assessment = assessRepositoryRelease(root)
        assertEquals(ReleaseDecision.NOT_APPROVED, assessment.decision)
        assertEquals(packagedBlockers, assessment.blockers)
        assertTrue(assessment.blockers.contains(ReleaseBlocker.SHARE_ALIKE_UNCONFIRMED))
        assertFalse(assessment.blockers.contains(ReleaseBlocker.EVIDENCE_MANIFEST_UNREADABLE))
    }

    @Test
    fun productionGateDoesNotTakeATestReportOrAPropertyOverride() {
        val gradle = File("build.gradle.kts").readText()
        assertFalse(gradle.contains("testDebugUnitTest"))
        assertFalse(gradle.contains("findProperty"))
        assertFalse(gradle.contains("System.getenv"))
        assertTrue(gradle.contains("dev.russiancompanion.release.ReleaseGateKt"))
        val gate = File("src/main/java/dev/russiancompanion/release/ReleaseGate.kt").readText()
        assertFalse(gate.contains("System.getenv"))
        assertFalse(gate.contains("findProperty"))
        val workflow = File(repositoryRoot(), ".github/workflows/build.yml").readText()
        assertTrue(workflow.contains("packages: platform-tools"))
        assertFalse(workflow.contains("tools platform-tools"))
    }

    @Test
    fun unconfirmedShareAlikeBlocksAnOtherwisePreparedCatalog() {
        val (root, manifest) = retainedFixture()
        val assessment = assessPackagedRelease(
            SeedJson.encode(preparedCatalog(shareAlike = "UNCONFIRMED")),
            manifest,
            root,
        )
        assertEquals(ReleaseDecision.NOT_APPROVED, assessment.decision)
        assertEquals(listOf(ReleaseBlocker.SHARE_ALIKE_UNCONFIRMED), assessment.blockers)
    }

    @Test
    fun confirmedShareAlikeClearsOnlyThatBlocker() {
        val waiting = assessPackagedRelease(
            SeedJson.encode(preparedCatalog(shareAlike = "CONFIRMED")),
            committedManifest(),
            repositoryRoot(),
        )
        assertEquals(missingEvidenceBlockers, waiting.blockers)
        val (root, manifest) = retainedFixture()
        val ready = assessPackagedRelease(
            SeedJson.encode(preparedCatalog(shareAlike = "CONFIRMED")),
            manifest,
            root,
        )
        assertEquals(ReleaseDecision.APPROVED, ready.decision)
        assertEquals(emptyList<ReleaseBlocker>(), ready.blockers)
    }

    @Test
    fun notApplicableShareAlikeDoesNotBlock() {
        val (root, manifest) = retainedFixture()
        val assessment = assessPackagedRelease(
            SeedJson.encode(preparedCatalog(shareAlike = "NOT_APPLICABLE")),
            manifest,
            root,
        )
        assertEquals(ReleaseDecision.APPROVED, assessment.decision)
    }

    @Test
    fun retainedStateWithoutAFileStaysMissing() {
        val root = Files.createTempDirectory("release-evidence").toFile()
        val manifest = manifest(
            """{"procedure":"AT-004","state":"RETAINED","evidencePath":"release/evidence/AT-004.txt"}""",
            """{"procedure":"AT-020","state":"MISSING"}""",
            """{"procedure":"AT-030","state":"RETAINED","evidencePath":"release/evidence/AT-030.txt"}""",
            """{"procedure":"AT-031","state":"MISSING"}""",
        )
        val emptyEvidence = File(root, "release/evidence/AT-030.txt")
        emptyEvidence.parentFile?.mkdirs()
        emptyEvidence.writeText("")
        val blockers = assessPackagedRelease(
            SeedJson.encode(preparedCatalog(shareAlike = "CONFIRMED")),
            manifest,
            root,
        ).blockers
        assertEquals(missingEvidenceBlockers, blockers)
    }

    @Test
    fun evidenceOutsideTheManifestDirectoryIsRejected() {
        val root = Files.createTempDirectory("release-evidence").toFile()
        val outside = File(root, "notes/outside.txt")
        outside.parentFile?.mkdirs()
        outside.writeText("outside the evidence directory")
        val manifest = manifest(
            """{"procedure":"AT-004","state":"RETAINED","evidencePath":"notes/outside.txt"}""",
            """{"procedure":"AT-020","state":"RETAINED","evidencePath":"../outside.txt"}""",
            """{"procedure":"AT-030","state":"MISSING"}""",
            """{"procedure":"AT-031","state":"MISSING"}""",
        )
        val blockers = assessPackagedRelease(
            SeedJson.encode(preparedCatalog(shareAlike = "CONFIRMED")),
            manifest,
            root,
        ).blockers
        assertTrue(blockers.contains(ReleaseBlocker.OFFLINE_VERIFICATION_NOT_RETAINED))
        assertTrue(blockers.contains(ReleaseBlocker.REBOOT_EVIDENCE_NOT_RETAINED))
    }

    @Test
    fun anUndocumentedBooleanDoesNotMarkEvidenceRetained() {
        val (root, _) = retainedFixture()
        val manifest = """{"schemaVersion":1,"offlineVerification":true,"records":[]}"""
        val blockers = assessPackagedRelease(
            SeedJson.encode(preparedCatalog(shareAlike = "CONFIRMED")),
            manifest,
            root,
        ).blockers
        assertEquals(
            listOf(ReleaseBlocker.EVIDENCE_MANIFEST_UNREADABLE) + missingEvidenceBlockers,
            blockers,
        )
    }

    @Test
    fun absentOrCorruptContentFailsClosed() {
        val root = repositoryRoot()
        val manifest = committedManifest()
        assertEquals(
            listOf(ReleaseBlocker.CONTENT_UNREADABLE) + missingEvidenceBlockers,
            assessPackagedRelease(null, manifest, root).blockers,
        )
        assertEquals(
            listOf(ReleaseBlocker.CONTENT_UNREADABLE) + missingEvidenceBlockers,
            assessPackagedRelease("{", manifest, root).blockers,
        )
        assertTrue(
            assessPackagedRelease(null, null, root).blockers
                .contains(ReleaseBlocker.EVIDENCE_MANIFEST_UNREADABLE),
        )
    }

    @Test
    fun aTutorOnlyGateAndAPartialPlanStayBlocked() {
        val (root, manifest) = retainedFixture()
        val tutorGate = preparedCatalog(shareAlike = "CONFIRMED").copy(
            gateDefinitions = listOf(
                gate(
                    conditions = listOf(
                        GateConditionSeed("c-tutor", "TUTOR_SIGN_OFF", "PRESENT", null, 0),
                    ),
                ),
            ),
        )
        assertEquals(
            true,
            assessPackagedRelease(SeedJson.encode(tutorGate), manifest, root)
                .blockers
                .contains(ReleaseBlocker.DEFAULT_GATE_NOT_INSTALLED),
        )
        val bookOnly = preparedCatalog(shareAlike = "CONFIRMED").copy(
            planDefinitions = listOf(plan("plan-book", "BOOK", 1)),
        )
        assertEquals(
            true,
            assessPackagedRelease(SeedJson.encode(bookOnly), manifest, root)
                .blockers
                .contains(ReleaseBlocker.PLAN_DEFINITIONS_NOT_INSTALLED),
        )
    }

    private fun committedManifest(): String {
        return File(repositoryRoot(), RELEASE_EVIDENCE_MANIFEST).readText()
    }

    private fun repositoryRoot(): File {
        val candidates = listOf(File("."), File(".."))
        return candidates.first { File(it, RELEASE_EVIDENCE_MANIFEST).isFile }.canonicalFile
    }

    private fun retainedFixture(): Pair<File, String> {
        val root = Files.createTempDirectory("release-evidence").toFile()
        val records = mutableListOf<String>()
        for (procedure in listOf("AT-004", "AT-020", "AT-030", "AT-031")) {
            val relative = "release/evidence/$procedure.txt"
            val evidence = File(root, relative)
            evidence.parentFile?.mkdirs()
            evidence.writeText("Fixture record for $procedure.")
            records += """{"procedure":"$procedure","state":"RETAINED","evidencePath":"$relative"}"""
        }
        return root to manifest(*records.toTypedArray())
    }

    private fun manifest(vararg records: String): String {
        return """{"schemaVersion":1,"records":[${records.joinToString(",")}]}"""
    }

    private fun preparedCatalog(shareAlike: String) = minimalDocument().copy(
        planDefinitions = listOf(
            plan("plan-book", "BOOK", 1),
            plan("plan-anki", "ANKI_DROID", 2),
            plan("plan-listen", "LISTEN_RECORD_COMPARE", 3),
        ),
        gateDefinitions = listOf(
            gate(),
        ),
        sourceRecords = listOf(source().copy(shareAlikeConfirmation = shareAlike)),
        sourceTexts = listOf(text(matchVerified = true)),
        assetRightsRecords = listOf(rights()),
        audioModels = listOf(audio()),
    )

    private fun plan(id: String, activityType: String, sortOrder: Int) = PlanDefinitionSeed(
        id = id,
        segmentId = "segment-1",
        activityType = activityType,
        instruction = "Use the assigned local activity",
        sortOrder = sortOrder,
        required = true,
        lessonNumber = if (activityType == "BOOK" || activityType == "ANKI_DROID") 1 else null,
        audioModelIds = if (activityType == "LISTEN_RECORD_COMPARE") listOf("audio-1") else emptyList(),
    )

    private companion object {
        val missingEvidenceBlockers = listOf(
            ReleaseBlocker.OFFLINE_VERIFICATION_NOT_RETAINED,
            ReleaseBlocker.REBOOT_EVIDENCE_NOT_RETAINED,
            ReleaseBlocker.LESSON_CYCLE_NOT_RETAINED,
            ReleaseBlocker.OWNER_RELEASE_DECISION_NOT_RETAINED,
        )
        val packagedBlockers = listOf(
            ReleaseBlocker.SHARE_ALIKE_UNCONFIRMED,
        ) + missingEvidenceBlockers
    }
}
