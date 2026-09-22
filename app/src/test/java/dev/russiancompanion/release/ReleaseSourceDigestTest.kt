package dev.russiancompanion.release

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.nio.file.Files

class ReleaseSourceDigestTest {
    @Test
    fun committedCandidateRecordsTheCurrentSourceDigestAndIdentity() {
        val root = repositoryRoot()
        val binding = currentReleaseBinding(root)
        assertNotNull(binding)
        val candidate = readAcceptanceCandidate(root)
        assertEquals(binding!!.sourceDigest, candidate?.sourceDigest)
        assertTrue(candidateMatchesBinding(candidate, binding))
        assertEquals("1.1.0", binding.versionName)
        assertEquals(3, binding.versionCode)
        assertEquals("dev.russiancompanion", binding.applicationId)
        assertFalse(candidate!!.frozen)
        assertEquals(null, candidate.sourceCommit)
        assertEquals("NOT_FROZEN", candidate.artifact?.status)
        assertEquals(null, recordedCandidateIdentity(candidate))
        assertEquals("NOT_APPROVED", candidate.publicRelease)
        assertTrue(
            assessRepositoryRelease(root).blockers.contains(ReleaseBlocker.CANDIDATE_NOT_FROZEN),
        )
    }

    @Test
    fun changingReleaseInputsChangesTheDigestAndLeavesEvidenceExcluded() {
        val root = repositoryRoot()
        val base = computeReleaseSourceDigest(root)
        assertTrue(SHA256.matches(base))
        val cases = listOf(
            "app/src/main/java/dev/russiancompanion/presentation/MainActivity.kt" to "kotlin-changed\n",
            "app/src/main/res/values/strings.xml" to "<resources><string name=\"app_name\">Changed</string></resources>\n",
            "catalog/beginner_pathway.json" to "{\"changed\":true}\n",
            "app/src/main/assets/audio/.digest-probe" to "audio-changed",
            "gradle.properties" to "org.gradle.jvmargs=-Xmx99m\n",
            "app/build.gradle.kts" to File(root, "app/build.gradle.kts").readText()
                .replace("versionName = \"1.1.0\"", "versionName = \"1.1.1\""),
            "release/acceptance/DEVICE_ACCEPTANCE.md" to "changed acceptance procedure\n",
            "release/acceptance/templates/AT-001.md" to "changed acceptance template\n",
        )
        for ((relative, content) in cases) {
            val changed = computeReleaseSourceDigest(root, mapOf(relative to content.toByteArray()))
            assertNotEquals(relative, base, changed)
        }
        assertEquals(
            base,
            computeReleaseSourceDigest(
                root,
                mapOf(RELEASE_EVIDENCE_MANIFEST to "{\"schemaVersion\":2,\"records\":[]}".toByteArray()),
            ),
        )
        assertEquals(
            base,
            computeReleaseSourceDigest(
                root,
                mapOf("release/evidence/AT-001.json" to "{\"result\":\"PASS\"}".toByteArray()),
            ),
        )
        assertEquals(
            base,
            computeReleaseSourceDigest(
                root,
                mapOf("release/acceptance/forms/AT-001.md" to "executed notes\n".toByteArray()),
            ),
        )
    }

    @Test
    fun changingAnAcceptanceProcedureInvalidatesPriorEvidence() {
        assertInvalidatedBy("release/acceptance/DEVICE_ACCEPTANCE.md") { file ->
            file.writeText(file.readText() + "\nchanged procedure\n")
        }
    }

    @Test
    fun retainedEvidenceIsRejectedAfterAKotlinChange() {
        assertInvalidatedBy("app/src/main/java/dev/russiancompanion/Marker.kt") { file ->
            file.writeText("internal object MarkerChanged\n")
        }
    }

    @Test
    fun retainedEvidenceIsRejectedAfterAResourceChange() {
        assertInvalidatedBy("app/src/main/res/values/extra.xml") { file ->
            file.writeText("<resources><string name=\"x\">y</string></resources>\n")
        }
    }

    @Test
    fun retainedEvidenceIsRejectedAfterACatalogChange() {
        assertInvalidatedBy("catalog/extra-record.json") { file ->
            file.writeText("{\"n\":2}\n")
        }
    }

    @Test
    fun retainedEvidenceIsRejectedAfterAnAudioChange() {
        assertInvalidatedBy("app/src/main/assets/audio/sample.ogg") { file ->
            file.writeBytes(byteArrayOf(9, 9, 9, 9))
        }
    }

    @Test
    fun retainedEvidenceIsRejectedAfterAGradleChange() {
        assertInvalidatedBy("gradle.properties") { file ->
            file.writeText("org.gradle.jvmargs=-Xmx8g\n")
        }
    }

    @Test
    fun retainedEvidenceIsRejectedAfterAVersionIdentityChange() {
        assertInvalidatedBy("app/build.gradle.kts") { file ->
            file.writeText(file.readText().replace("versionName = \"1.1.0\"", "versionName = \"1.1.1\""))
        }
    }

    @Test
    fun mismatchedCandidateCommitRejectsRetainedEvidence() {
        val root = boundProductRoot()
        assertEquals(ReleaseDecision.APPROVED, assessRepositoryRelease(root).decision)
        File(root, "release/evidence/AT-001.json").writeText(
            boundEvidence(
                "AT-001",
                checkNotNull(currentReleaseBinding(root)),
                commit = "cccccccccccccccccccccccccccccccccccccccc",
            ),
        )
        val after = assessRepositoryRelease(root)
        assertEquals(ReleaseDecision.NOT_APPROVED, after.decision)
        assertTrue(after.blockers.contains(ReleaseBlocker.INSTALLATION_NOT_RETAINED))
        assertTrue(after.blockers.contains(ReleaseBlocker.OWNER_RELEASE_DECISION_NOT_RETAINED))
    }

    @Test
    fun mismatchedApkHashRejectsRetainedEvidence() {
        val root = boundProductRoot()
        assertEquals(ReleaseDecision.APPROVED, assessRepositoryRelease(root).decision)
        File(root, "release/evidence/AT-001.json").writeText(
            boundEvidence(
                "AT-001",
                checkNotNull(currentReleaseBinding(root)),
                hash = "cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc",
            ),
        )
        val after = assessRepositoryRelease(root)
        assertEquals(ReleaseDecision.NOT_APPROVED, after.decision)
        assertTrue(after.blockers.contains(ReleaseBlocker.INSTALLATION_NOT_RETAINED))
        assertTrue(after.blockers.contains(ReleaseBlocker.OWNER_RELEASE_DECISION_NOT_RETAINED))
    }

    @Test
    fun aNullArtifactIdentityCannotApproveRelease() {
        val root = boundProductRoot()
        rewriteCandidate(root, frozen = true, artifact = "null")
        val after = assessRepositoryRelease(root)
        assertEquals(ReleaseDecision.NOT_APPROVED, after.decision)
        assertTrue(after.blockers.contains(ReleaseBlocker.CANDIDATE_ARTIFACT_UNRECORDED))
        assertFalse(after.blockers.contains(ReleaseBlocker.CANDIDATE_NOT_FROZEN))
    }

    @Test
    fun anUnfrozenCandidateCannotApproveRelease() {
        val root = boundProductRoot()
        rewriteCandidate(root, frozen = false)
        val after = assessRepositoryRelease(root)
        assertEquals(ReleaseDecision.NOT_APPROVED, after.decision)
        assertTrue(after.blockers.contains(ReleaseBlocker.CANDIDATE_NOT_FROZEN))
        assertFalse(after.blockers.contains(ReleaseBlocker.CANDIDATE_ARTIFACT_UNRECORDED))
    }

    private fun assertInvalidatedBy(relative: String, mutate: (File) -> Unit) {
        val root = boundProductRoot()
        val ready = assessRepositoryRelease(root)
        assertEquals(ReleaseDecision.APPROVED, ready.decision)
        mutate(File(root, relative))
        val after = assessRepositoryRelease(root)
        assertEquals(ReleaseDecision.NOT_APPROVED, after.decision)
        assertTrue(after.blockers.contains(ReleaseBlocker.SOURCE_DIGEST_MISMATCH))
        assertTrue(after.blockers.contains(ReleaseBlocker.INSTALLATION_NOT_RETAINED))
        assertTrue(after.blockers.contains(ReleaseBlocker.OWNER_RELEASE_DECISION_NOT_RETAINED))
    }

    private fun boundProductRoot(): File {
        val from = repositoryRoot()
        val root = Files.createTempDirectory("bound-release").toFile()
        val relativePaths = listOf(
            "app/src/main/assets/content/bundled-content.json",
            "app/src/main/assets/content/catalog-notice.txt",
            "app/src/main/assets/licenses/cc-by-sa-4.0.txt",
            "app/src/main/assets/licenses/cc-by-3.0-us.html",
            "app/src/main/assets/licenses/cc-by-2.0-fr.html",
            "app/src/main/assets/licenses/public-domain-notice.txt",
            "app/src/main/assets/licenses/shtooka-readme-sakhno.txt",
            "app/src/main/assets/licenses/shtooka-readme-streit.txt",
            "catalog/compliance/CATALOG_NOTICE.md",
            "catalog/compliance/PROPOSED_CONFIRMATION.md",
            "catalog/compliance/share-alike-package.json",
            "app/build.gradle.kts",
            "build.gradle.kts",
            "settings.gradle.kts",
            "gradle.properties",
        )
        for (relative in relativePaths) {
            val dest = File(root, relative)
            dest.parentFile?.mkdirs()
            File(from, relative).copyTo(dest, overwrite = true)
        }
        File(root, "app/src/main/java/dev/russiancompanion").mkdirs()
        File(root, "app/src/main/java/dev/russiancompanion/Marker.kt").writeText("internal object Marker\n")
        File(root, "app/src/main/res/values").mkdirs()
        File(root, "app/src/main/res/values/extra.xml").writeText("<resources/>\n")
        File(root, "app/src/main/assets/audio").mkdirs()
        File(root, "app/src/main/assets/audio/sample.ogg").writeBytes(byteArrayOf(1, 2, 3, 4))
        File(root, "catalog/extra-record.json").writeText("{\"n\":1}\n")
        File(root, "release/acceptance").mkdirs()
        File(from, "release/acceptance/DEVICE_ACCEPTANCE.md")
            .copyTo(File(root, "release/acceptance/DEVICE_ACCEPTANCE.md"), overwrite = true)
        File(root, "release/acceptance/templates").mkdirs()
        File(from, "release/acceptance/templates/AT-001.md")
            .copyTo(File(root, "release/acceptance/templates/AT-001.md"), overwrite = true)
        val binding = checkNotNull(currentReleaseBinding(root))
        writeFrozenCandidate(root, binding)
        val records = mutableListOf<String>()
        for (procedure in REQUIRED_RELEASE_PROCEDURES.map { it.first }) {
            val relative = "release/evidence/$procedure.json"
            File(root, relative).apply {
                parentFile?.mkdirs()
                writeText(boundEvidence(procedure, binding))
            }
            records += """{"procedure":"$procedure","state":"RETAINED","evidencePath":"$relative"}"""
        }
        File(root, RELEASE_EVIDENCE_MANIFEST).writeText(
            """{"schemaVersion":2,"records":[${records.joinToString(",")}]}""",
        )
        return root
    }

    private fun writeFrozenCandidate(root: File, binding: ReleaseBinding) {
        File(root, ACCEPTANCE_CANDIDATE_PATH).apply {
            parentFile?.mkdirs()
            writeText(candidateDocument(binding, frozen = true, artifact = recordedArtifact()))
        }
    }

    private fun rewriteCandidate(root: File, frozen: Boolean, artifact: String = recordedArtifact()) {
        val binding = checkNotNull(currentReleaseBinding(root))
        File(root, ACCEPTANCE_CANDIDATE_PATH).writeText(candidateDocument(binding, frozen, artifact))
    }

    private fun candidateDocument(binding: ReleaseBinding, frozen: Boolean, artifact: String): String {
        val commit = if (frozen) """"$VALID_COMMIT"""" else "null"
        return """{"schemaVersion":4,"kind":"physical-device-acceptance-candidate","publicRelease":"NOT_APPROVED","frozen":$frozen,"sourceCommit":$commit,"applicationId":"${binding.applicationId}","versionName":"${binding.versionName}","versionCode":${binding.versionCode},"sourceDigest":"${binding.sourceDigest}","artifact":$artifact}"""
    }

    private fun recordedArtifact(): String {
        return """{"status":"RECORDED","sha256":"$VALID_HASH","apkBytes":1234,"runId":"1","runUrl":"https://example.invalid/1"}"""
    }

    private fun boundEvidence(
        procedure: String,
        binding: ReleaseBinding,
        commit: String = VALID_COMMIT,
        hash: String = VALID_HASH,
    ): String {
        val device = if (procedure == "AT-031") "" else ""","deviceModel":"Galaxy S26 Ultra""""
        val owner = if (procedure != "AT-031") {
            ""
        } else {
            ""","ownerDecision":"APPROVED","mandatoryProcedures":["AT-001","AT-002","AT-004","AT-008","AT-009","AT-010","AT-020","AT-030"]"""
        }
        return """{"schemaVersion":2,"procedure":"$procedure","result":"PASS","candidateCommit":"$commit","apkSha256":"$hash","sourceDigest":"${binding.sourceDigest}","versionName":"${binding.versionName}","versionCode":${binding.versionCode},"applicationId":"${binding.applicationId}","executedAt":"2026-09-22T12:00"$device,"artifactHashes":{"form":"$VALID_HASH"}$owner}"""
    }

    private companion object {
        const val VALID_COMMIT = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
        const val VALID_HASH = "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"
    }

    private fun repositoryRoot(): File {
        val candidates = listOf(File("."), File(".."))
        return candidates.first { File(it, RELEASE_EVIDENCE_MANIFEST).isFile }.canonicalFile
    }
}
