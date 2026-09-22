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
        val forms = listOf(
            "release/acceptance/forms/AT-001.md",
            "release/acceptance/forms/AT-004.md",
            "release/acceptance/forms/AT-020.md",
            "release/acceptance/forms/AT-030.md",
            "release/acceptance/forms/AT-031-OWNER-DECISION.md",
        ).associateWith { File(root, it).readText() }

        assertTrue(instructions.contains("Galaxy S26 Ultra"))
        assertTrue(instructions.contains("AT-001"))
        assertTrue(instructions.contains("AT-004"))
        assertTrue(instructions.contains("AT-020"))
        assertTrue(instructions.contains("AT-030"))
        assertTrue(instructions.contains("AT-031"))
        assertTrue(instructions.contains("Airplane mode"))
        assertTrue(instructions.contains("sha256sum"))
        assertTrue(instructions.contains("release/evidence/AT-004.txt"))
        assertTrue(instructions.contains("release/evidence/AT-020.txt"))
        assertTrue(instructions.contains("release/evidence/AT-030.txt"))
        assertTrue(instructions.contains("release/evidence/AT-031.txt"))
        assertTrue(instructions.contains("Use your copy of the book for this lesson"))
        assertTrue(instructions.contains("Do the assigned AnkiDroid work"))
        assertTrue(instructions.contains("Start listen-record-compare"))
        assertTrue(instructions.contains("Choose a local destination"))
        assertTrue(instructions.contains("Force stop"))
        assertTrue(instructions.contains("Restart"))
        assertTrue(instructions.contains("assembleDebug"))
        assertTrue(instructions.contains("NOT_APPROVED"))
        assertFalse(instructions.contains("\"state\": \"RETAINED\""))

        assertEquals("physical-device-acceptance-candidate", candidate.getValue("kind").jsonPrimitive.content)
        assertEquals("NOT_APPROVED", candidate.getValue("publicRelease").jsonPrimitive.content)
        assertEquals("assembleDebug", candidate.getValue("gradleTask").jsonPrimitive.content)
        assertEquals("0.0.0", candidate.getValue("versionName").jsonPrimitive.content)
        assertEquals(
            "0602edaeb05a353de5e994c127eb87b5b97078a3",
            candidate.getValue("sourceCommit").jsonPrimitive.content,
        )
        val sha256 = candidate.getValue("sha256").jsonPrimitive.content
        assertEquals(64, sha256.length)
        assertTrue(sha256.matches(Regex("[0-9a-f]{64}")))
        assertEquals("MISSING", candidate.getValue("retainedEvidence").jsonPrimitive.content)
        val unexecuted = candidate.getValue("unexecutedProcedures").jsonArray.map {
            it.jsonPrimitive.content
        }
        assertEquals(listOf("AT-001", "AT-004", "AT-020", "AT-030", "AT-031"), unexecuted)

        manifest.getValue("records").jsonArray.forEach { record ->
            assertEquals("MISSING", record.jsonObject.getValue("state").jsonPrimitive.content)
        }
        assertFalse(File(root, RELEASE_EVIDENCE_MANIFEST).readText().contains("RETAINED"))

        forms.values.forEach { body ->
            assertTrue(body.contains("NOT_EXECUTED"))
            assertFalse(body.contains("Result: **PASS**"))
            assertFalse(body.contains("Result: **FAIL**"))
        }
        val owner = forms.getValue("release/acceptance/forms/AT-031-OWNER-DECISION.md")
        assertTrue(owner.contains("Reserved for the Product Owner"))
        assertTrue(owner.contains("Decision (leave unset)"))
        assertFalse(owner.contains("Decision: APPROVED"))

        val assessment = assessRepositoryRelease(root)
        assertEquals(ReleaseDecision.NOT_APPROVED, assessment.decision)
        assertFalse(assessment.blockers.contains(ReleaseBlocker.SHARE_ALIKE_UNCONFIRMED))
        assertTrue(assessment.blockers.contains(ReleaseBlocker.OFFLINE_VERIFICATION_NOT_RETAINED))
        assertTrue(assessment.blockers.contains(ReleaseBlocker.REBOOT_EVIDENCE_NOT_RETAINED))
        assertTrue(assessment.blockers.contains(ReleaseBlocker.LESSON_CYCLE_NOT_RETAINED))
        assertTrue(assessment.blockers.contains(ReleaseBlocker.OWNER_RELEASE_DECISION_NOT_RETAINED))
        assertEquals(AcceptanceDisposition.OUTSTANDING, ACCEPTANCE_DISPOSITION.getValue("AT-001"))
        assertEquals(AcceptanceDisposition.OUTSTANDING, ACCEPTANCE_DISPOSITION.getValue("AT-004"))
        assertEquals(AcceptanceDisposition.OUTSTANDING, ACCEPTANCE_DISPOSITION.getValue("AT-020"))
        assertEquals(AcceptanceDisposition.OUTSTANDING, ACCEPTANCE_DISPOSITION.getValue("AT-030"))
        assertEquals(AcceptanceDisposition.OUTSTANDING, ACCEPTANCE_DISPOSITION.getValue("AT-031"))
        assertFalse(File(root, "app/src/main/AndroidManifest.xml").readText().contains("android.permission.INTERNET"))
    }

    private fun repositoryRoot(): File {
        val candidates = listOf(File("."), File(".."))
        return candidates.first { File(it, RELEASE_EVIDENCE_MANIFEST).isFile }.canonicalFile
    }
}
