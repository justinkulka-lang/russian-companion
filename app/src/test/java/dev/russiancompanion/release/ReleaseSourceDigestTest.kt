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
        assertEquals("1.0.0", binding.versionName)
        assertEquals(2, binding.versionCode)
        assertEquals("dev.russiancompanion", binding.applicationId)
        assertFalse(candidate!!.frozen)
        assertEquals("NOT_APPROVED", candidate.publicRelease)
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
                .replace("versionName = \"1.0.0\"", "versionName = \"1.0.1\""),
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
            file.writeText(file.readText().replace("versionName = \"1.0.0\"", "versionName = \"1.0.1\""))
        }
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
        val binding = checkNotNull(currentReleaseBinding(root))
        File(root, ACCEPTANCE_CANDIDATE_PATH).apply {
            parentFile?.mkdirs()
            writeText(
                """{"schemaVersion":3,"kind":"physical-device-acceptance-candidate","publicRelease":"NOT_APPROVED","frozen":false,"applicationId":"${binding.applicationId}","versionName":"${binding.versionName}","versionCode":${binding.versionCode},"sourceDigest":"${binding.sourceDigest}"}""",
            )
        }
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

    private fun boundEvidence(procedure: String, binding: ReleaseBinding): String {
        val device = if (procedure == "AT-031") "" else ""","deviceModel":"Galaxy S26 Ultra""""
        val owner = if (procedure != "AT-031") {
            ""
        } else {
            ""","ownerDecision":"APPROVED","mandatoryProcedures":["AT-001","AT-002","AT-004","AT-008","AT-009","AT-010","AT-020","AT-030"]"""
        }
        return """{"schemaVersion":2,"procedure":"$procedure","result":"PASS","candidateCommit":"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa","apkSha256":"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb","sourceDigest":"${binding.sourceDigest}","versionName":"${binding.versionName}","versionCode":${binding.versionCode},"applicationId":"${binding.applicationId}","executedAt":"2026-09-22T12:00"$device,"artifactHashes":{"form":"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"}$owner}"""
    }

    private fun repositoryRoot(): File {
        val candidates = listOf(File("."), File(".."))
        return candidates.first { File(it, RELEASE_EVIDENCE_MANIFEST).isFile }.canonicalFile
    }
}
