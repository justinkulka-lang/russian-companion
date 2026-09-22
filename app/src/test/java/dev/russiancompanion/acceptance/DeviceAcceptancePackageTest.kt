package dev.russiancompanion.acceptance

import dev.russiancompanion.release.RELEASE_EVIDENCE_MANIFEST
import dev.russiancompanion.release.ReleaseBlocker
import dev.russiancompanion.release.ReleaseDecision
import dev.russiancompanion.release.assessRepositoryRelease
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class DeviceAcceptancePackageTest {
    @Test
    fun physicalDevicePackageStaysUnexecutedAndDoesNotApproveRelease() {
        val root = repositoryRoot()
        val instructions = File(root, "release/acceptance/DEVICE_ACCEPTANCE.md").readText()
        val candidate = Json.parseToJsonElement(
            File(root, "release/acceptance/candidate.json").readText(),
        ).jsonObject
        val manifest = Json.parseToJsonElement(
            File(root, RELEASE_EVIDENCE_MANIFEST).readText(),
        ).jsonObject
        val workflow = File(root, ".github/workflows/build.yml").readText()
        val formPaths = listOf(
            "release/acceptance/forms/AT-001.md",
            "release/acceptance/forms/AT-002.md",
            "release/acceptance/forms/AT-004.md",
            "release/acceptance/forms/AT-008.md",
            "release/acceptance/forms/AT-009.md",
            "release/acceptance/forms/AT-010.md",
            "release/acceptance/forms/AT-020.md",
            "release/acceptance/forms/AT-030.md",
            "release/acceptance/forms/AT-031-OWNER-DECISION.md",
        )
        val templatePaths = formPaths.map { it.replace("/forms/", "/templates/") }
        val forms = formPaths.associateWith { File(root, it).readText() }
        val templates = templatePaths.associateWith { File(root, it).readText() }

        assertTrue(instructions.contains("Galaxy S26 Ultra"))
        listOf("AT-001", "AT-002", "AT-004", "AT-008", "AT-009", "AT-010", "AT-020", "AT-030", "AT-031")
            .forEach { id -> assertTrue(id, instructions.contains(id)) }
        assertTrue(instructions.contains("Airplane mode"))
        assertTrue(instructions.contains("sha256sum"))
        assertTrue(instructions.contains("acceptance-candidate-debug-apk"))
        assertTrue(instructions.contains("Do not rebuild locally"))
        assertTrue(instructions.contains("release/evidence/AT-002.json"))
        assertTrue(instructions.contains("release/evidence/AT-008.json"))
        assertTrue(instructions.contains("release/evidence/AT-009.json"))
        assertTrue(instructions.contains("release/evidence/AT-010.json"))
        assertTrue(instructions.contains("AT-008-rights-inventory.tsv"))
        assertTrue(instructions.contains("SHARE_ALIKE_UNCONFIRMED"))
        assertTrue(instructions.contains("Use your copy of the book for this lesson"))
        assertTrue(instructions.contains("Do the assigned AnkiDroid work"))
        assertTrue(instructions.contains("Start listen-record-compare"))
        assertTrue(instructions.contains("Choose a local destination"))
        assertTrue(instructions.contains("Force stop"))
        assertTrue(instructions.contains("Restart"))
        assertTrue(instructions.contains("assembleDebug"))
        assertTrue(instructions.contains("NOT_APPROVED"))
        assertFalse(instructions.contains("\"state\": \"RETAINED\""))

        assertTrue(workflow.contains("actions/upload-artifact@ea165f8d65b6e75b540449e92b4886f43607fa02"))
        assertTrue(workflow.contains("acceptance-candidate-debug-apk"))
        assertTrue(workflow.contains("artifact-identity.json"))
        assertTrue(workflow.contains("if-no-files-found: error"))
        assertTrue(workflow.contains("lintDebug"))
        assertTrue(workflow.contains("verifyReleaseGateAttachment"))

        assertEquals("physical-device-acceptance-candidate", candidate.getValue("kind").jsonPrimitive.content)
        assertEquals("NOT_APPROVED", candidate.getValue("publicRelease").jsonPrimitive.content)
        assertEquals("assembleDebug", candidate.getValue("gradleTask").jsonPrimitive.content)
        assertEquals("2.0.0-dev", candidate.getValue("versionName").jsonPrimitive.content)
        assertEquals(5, candidate.getValue("versionCode").jsonPrimitive.content.toInt())
        assertEquals(4, candidate.getValue("schemaVersion").jsonPrimitive.content.toInt())
        assertEquals("null", candidate.getValue("sourceCommit").toString())
        val sourceDigest = candidate.getValue("sourceDigest").jsonPrimitive.content
        assertTrue(sourceDigest.matches(Regex("^[a-f0-9]{64}$")))
        assertEquals(
            dev.russiancompanion.release.computeReleaseSourceDigest(root),
            sourceDigest,
        )
        assertEquals(false, candidate.getValue("doNotRebuildForHashMatch").jsonPrimitive.content.toBoolean())
        assertEquals(false, candidate.getValue("frozen").jsonPrimitive.content.toBoolean())
        val preserved = candidate.getValue("preservedPriorCandidate").jsonObject
        assertEquals(
            "0602edaeb05a353de5e994c127eb87b5b97078a3",
            preserved.getValue("sourceCommit").jsonPrimitive.content,
        )
        assertEquals("0.0.0", preserved.getValue("versionName").jsonPrimitive.content)
        val preservedArtifact = preserved.getValue("artifact").jsonObject
        assertEquals("35740338300", preservedArtifact.getValue("runId").jsonPrimitive.content)
        assertEquals(
            "1a95949ba7e2edd0ca72dea18e728ec105e427a2e5f5c093ff33fe82dfcba33e",
            preservedArtifact.getValue("sha256").jsonPrimitive.content,
        )
        val artifact = candidate.getValue("artifact").jsonObject
        assertEquals("NOT_RECORDED", artifact.getValue("status").jsonPrimitive.content)
        val failed = candidate.getValue("preservedFailedCandidate").jsonObject
        assertEquals("1.1.0", failed.getValue("versionName").jsonPrimitive.content)
        assertEquals(3, failed.getValue("versionCode").jsonPrimitive.content.toInt())
        assertEquals(
            "3dcbea82be4d9a1d997d4a9c892d0231f55b871d",
            failed.getValue("sourceCommit").jsonPrimitive.content,
        )
        val failedArtifact = failed.getValue("artifact").jsonObject
        assertEquals("RECORDED", failedArtifact.getValue("status").jsonPrimitive.content)
        assertEquals(
            "33a57f945ee8a178c6f228c2513e37bae6ad072cd9c4968daf930351efef6016",
            failedArtifact.getValue("sha256").jsonPrimitive.content,
        )
        assertEquals(29277335L, failedArtifact.getValue("apkBytes").jsonPrimitive.content.toLong())
        assertEquals("35765579094", failedArtifact.getValue("runId").jsonPrimitive.content)
        assertEquals(
            "https://github.com/justinkulka-lang/russian-companion/actions/runs/35765579094",
            failedArtifact.getValue("runUrl").jsonPrimitive.content,
        )
        val frozen = candidate.getValue("preservedFrozenCandidate").jsonObject
        assertEquals("1.0.0", frozen.getValue("versionName").jsonPrimitive.content)
        assertEquals(
            "e86e72d1377a3b33f2a77b44c7354e56b3c0b5ce",
            frozen.getValue("sourceCommit").jsonPrimitive.content,
        )
        val frozenArtifact = frozen.getValue("artifact").jsonObject
        assertEquals("35754785768", frozenArtifact.getValue("runId").jsonPrimitive.content)
        assertEquals(
            "dd873f420b1ecf98790943dd69a8a352db0c2a81464e915f87dbd21b48bf73ca",
            frozenArtifact.getValue("sha256").jsonPrimitive.content,
        )
        assertEquals(29164707L, frozenArtifact.getValue("apkBytes").jsonPrimitive.content.toLong())
        assertEquals("MISSING", candidate.getValue("retainedEvidence").jsonPrimitive.content)
        val unexecuted = candidate.getValue("unexecutedProcedures").jsonArray.map {
            it.jsonPrimitive.content
        }
        assertEquals(
            listOf(
                "AT-001",
                "AT-002",
                "AT-004",
                "AT-008",
                "AT-009",
                "AT-010",
                "AT-020",
                "AT-030",
                "AT-031",
            ),
            unexecuted,
        )

        manifest.getValue("records").jsonArray.forEach { record ->
            assertEquals("MISSING", record.jsonObject.getValue("state").jsonPrimitive.content)
        }
        assertFalse(File(root, RELEASE_EVIDENCE_MANIFEST).readText().contains("RETAINED"))

        forms.values.forEach { body ->
            assertTrue(body.contains("NOT_EXECUTED"))
            assertFalse(body.contains("Result: **PASS**"))
            assertFalse(body.contains("Result: **FAIL**"))
        }
        templates.values.forEach { body ->
            assertTrue(body.contains("NOT_EXECUTED"))
            assertFalse(body.contains("Result: **PASS**"))
            assertFalse(body.contains("Result: **FAIL**"))
        }
        assertTrue(instructions.contains("release/acceptance/templates/"))
        val owner = forms.getValue("release/acceptance/forms/AT-031-OWNER-DECISION.md")
        assertTrue(owner.contains("Reserved for the Product Owner"))
        assertTrue(owner.contains("AT-002 retained: no"))
        assertTrue(owner.contains("AT-008 retained: no"))
        assertTrue(owner.contains("AT-009 retained: no"))
        assertTrue(owner.contains("AT-010 retained: no"))
        assertTrue(owner.contains("Decision (leave unset)"))
        assertFalse(owner.contains("Decision: APPROVED"))

        val rollup = Json.parseToJsonElement(
            File(root, "release/acceptance/audits/catalog-audit-rollup.json").readText(),
        ).jsonObject
        assertEquals("WORKING_AUDIT_NOT_RETAINED", rollup.getValue("status").jsonPrimitive.content)
        assertEquals("NOT_EXECUTED", rollup.getValue("result").jsonPrimitive.content)
        assertEquals(1000, File(root, "release/acceptance/audits/AT-008-rights-inventory.tsv").readLines().size - 1)
        assertEquals(1000, File(root, "release/acceptance/audits/AT-010-audio-license-match.tsv").readLines().size - 1)
        assertTrue(File(root, "release/acceptance/audits/AT-010-source-decisions.tsv").readLines().size > 1000)
        val afterGate = File(root, "release/acceptance/audits/AT-009-after-activation-assembleRelease.txt").readText()
        assertTrue(afterGate.contains("AT-004 offline verification evidence is not retained."))
        assertFalse(afterGate.contains("share-alike confirmation is recorded."))
        assertTrue(File(root, "release/acceptance/audits/AT-009-before-after.md").readText().contains("UNCONFIRMED"))

        val assessment = assessRepositoryRelease(root)
        assertEquals(ReleaseDecision.NOT_APPROVED, assessment.decision)
        assertFalse(assessment.blockers.contains(ReleaseBlocker.SHARE_ALIKE_UNCONFIRMED))
        assertTrue(assessment.blockers.contains(ReleaseBlocker.INSTALLATION_NOT_RETAINED))
        assertTrue(assessment.blockers.contains(ReleaseBlocker.OFFLINE_VERIFICATION_NOT_RETAINED))
        assertTrue(assessment.blockers.contains(ReleaseBlocker.REBOOT_EVIDENCE_NOT_RETAINED))
        assertTrue(assessment.blockers.contains(ReleaseBlocker.LESSON_CYCLE_NOT_RETAINED))
        assertTrue(assessment.blockers.contains(ReleaseBlocker.OWNER_RELEASE_DECISION_NOT_RETAINED))
        listOf("AT-001", "AT-002", "AT-004", "AT-008", "AT-009", "AT-010", "AT-020", "AT-030", "AT-031")
            .forEach { id ->
                assertEquals(id, AcceptanceDisposition.OUTSTANDING, ACCEPTANCE_DISPOSITION.getValue(id))
            }
        assertFalse(File(root, "app/src/main/AndroidManifest.xml").readText().contains("android.permission.INTERNET"))
    }

    private fun repositoryRoot(): File {
        val candidates = listOf(File("."), File(".."))
        return candidates.first { File(it, RELEASE_EVIDENCE_MANIFEST).isFile }.canonicalFile
    }
}
