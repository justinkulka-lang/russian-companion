package dev.russiancompanion.catalog

import dev.russiancompanion.acceptance.ACCEPTANCE_DISPOSITION
import dev.russiancompanion.acceptance.AcceptanceDisposition
import dev.russiancompanion.data.local.seed.SeedJson
import dev.russiancompanion.data.local.seed.SeedParse
import dev.russiancompanion.release.ReleaseBlocker
import dev.russiancompanion.release.ReleaseDecision
import dev.russiancompanion.release.assessRepositoryRelease
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.JsonNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class CatalogComplianceTest {
    private val recognizedLicenses = mapOf(
        "CC BY-SA 4.0" to "https://creativecommons.org/licenses/by-sa/4.0/",
        "CC BY 3.0 US" to "http://creativecommons.org/licenses/by/3.0/us/",
        "CC BY 2.0 FR" to "http://creativecommons.org/licenses/by/2.0/fr/",
        "Public domain" to null,
    )
    private val approvedTreatment =
        "OpenRussian stress-marked headwords are CC BY-SA 4.0. The matched audio license has no share-alike term."
    private val requiredAssetNotices = listOf(
        "app/src/main/assets/content/catalog-notice.txt",
        "app/src/main/assets/licenses/cc-by-sa-4.0.txt",
        "app/src/main/assets/licenses/cc-by-3.0-us.html",
        "app/src/main/assets/licenses/cc-by-2.0-fr.html",
        "app/src/main/assets/licenses/public-domain-notice.txt",
        "app/src/main/assets/licenses/shtooka-readme-sakhno.txt",
        "app/src/main/assets/licenses/shtooka-readme-streit.txt",
    )

    @Test
    fun everyEligibleAudioModelResolvesToCompleteComplianceRecords() {
        val root = repositoryRoot()
        val document = packagedDocument(root)
        val packageJson = compliancePackage(root)
        val chains = Json.parseToJsonElement(File(root, "catalog/license-chains.json").readText()).jsonObject
        val chainFiles = chains.getValue("files").jsonArray.map { it.jsonObject }
            .associateBy { it.getValue("filename").jsonPrimitive.content }
        val sources = document.sourceRecords.associateBy { it.id }
        val texts = document.sourceTexts.associateBy { it.id }
        val rights = document.assetRightsRecords.associateBy { it.id }
        val items = packageJson.getValue("items").jsonArray.map { it.jsonObject }
            .associateBy { it.getValue("audioModelId").jsonPrimitive.content }

        assertEquals(5, document.sourceRecords.size)
        assertEquals(1000, document.sourceTexts.size)
        assertEquals(1000, document.assetRightsRecords.size)
        assertEquals(1000, document.audioModels.size)
        assertEquals(1000, items.size)

        document.audioModels.forEach { audio ->
            val source = sources.getValue(checkNotNull(audio.sourceRecordId))
            val text = texts.getValue(checkNotNull(audio.sourceTextId))
            val right = rights.getValue(checkNotNull(audio.rightsRecordId))
            val filename = audio.localAudioReference.removePrefix("audio/")
            val chain = chainFiles.getValue(filename)
            val item = items.getValue(audio.id)
            assertTrue(source.attribution.isNotBlank())
            assertTrue(source.provenance.isNotBlank())
            assertTrue(source.licenseName.isNotBlank())
            assertEquals("UNMODIFIED", source.modificationStatus)
            assertEquals("UNMODIFIED", text.modificationStatus)
            assertTrue(text.matchVerified)
            assertEquals(source.id, text.sourceRecordId)
            assertEquals(source.id, right.sourceRecordId)
            assertEquals("COMPATIBLE", right.licenseStatus)
            assertEquals("INCLUDED", right.inclusionResult)
            assertTrue(right.attributionRequired)
            assertEquals(source.attribution, item.getValue("attribution").jsonPrimitive.content)
            assertEquals("CC BY-SA 4.0", item.getValue("textLicense").jsonPrimitive.content)
            assertEquals(
                recognizedLicenses.getValue("CC BY-SA 4.0"),
                item.getValue("textLicenseUrl").jsonPrimitive.content,
            )
            val audioLicense = chain.getValue("licenseAuthority").jsonObject.getValue("license").jsonPrimitive.content
            assertEquals(audioLicense, item.getValue("audioLicense").jsonPrimitive.content)
            assertTrue(recognizedLicenses.containsKey(audioLicense))
        }
    }

    @Test
    fun everyPackagedSourceHasARecognizedLicenseAndShareAlikeTreatment() {
        val root = repositoryRoot()
        val document = packagedDocument(root)
        val packageJson = compliancePackage(root)
        val sources = packageJson.getValue("sources").jsonArray.map { it.jsonObject }
        assertEquals(document.sourceRecords.size, sources.size)
        document.sourceRecords.forEach { source ->
            assertEquals(approvedTreatment, source.shareAlikeTreatment)
            assertEquals("UNCONFIRMED", source.shareAlikeConfirmation)
            val tokens = source.licenseName.split(";").map { it.trim() }.filter { it.isNotEmpty() }
            assertTrue(tokens.contains("CC BY-SA 4.0"))
            tokens.forEach { token ->
                assertTrue(recognizedLicenses.containsKey(token))
                if (token != "Public domain") {
                    assertNotNull(recognizedLicenses.getValue(token))
                }
            }
        }
        sources.forEach { source ->
            val licenses = source.getValue("licenses").jsonArray.map { it.jsonObject }
            licenses.forEach { license ->
                val identifier = license.getValue("identifier").jsonPrimitive.content
                assertTrue(recognizedLicenses.containsKey(identifier))
                val expectedUrl = recognizedLicenses.getValue(identifier)
                if (expectedUrl == null) {
                    assertEquals(JsonNull, license.getValue("licenseUrl"))
                    assertEquals(
                        "licenses/public-domain-notice.txt",
                        license.getValue("packagedNotice").jsonPrimitive.content,
                    )
                } else {
                    assertEquals(expectedUrl, license.getValue("licenseUrl").jsonPrimitive.content)
                }
            }
            assertEquals(approvedTreatment, source.getValue("shareAlikeTreatment").jsonPrimitive.content)
            assertEquals("UNCONFIRMED", source.getValue("shareAlikeConfirmation").jsonPrimitive.content)
            assertFalse(source.getValue("modified").jsonPrimitive.content.toBoolean())
        }
    }

    @Test
    fun requiredAttributionAndLicenseNoticesAreAvailableOffline() {
        val root = repositoryRoot()
        val notice = File(root, "app/src/main/assets/content/catalog-notice.txt").readText()
        requiredAssetNotices.forEach { relative ->
            val file = File(root, relative)
            assertTrue(file.isFile && file.length() > 0L)
        }
        assertTrue(notice.contains("This app does not own the packaged Russian audio or stress-marked text."))
        assertTrue(notice.contains("Shtooka native audio") || notice.contains("## Shtooka native audio"))
        assertTrue(notice.contains("OpenRussian stress-marked text"))
        assertTrue(notice.contains("App code"))
        assertTrue(notice.contains("User recordings and user-entered data"))
        assertTrue(notice.contains("Unmodified versus modified assets"))
        assertTrue(notice.contains("deterministic"))
        assertTrue(notice.contains("UNCONFIRMED"))
        assertTrue(notice.contains("CC BY-SA 4.0"))
        assertFalse(notice.contains("This app owns"))
        val sa = File(root, "app/src/main/assets/licenses/cc-by-sa-4.0.txt").readText()
        assertTrue(sa.contains("Attribution-ShareAlike 4.0 International"))
        assertTrue(File(root, "catalog/compliance/PROPOSED_CONFIRMATION.md").readText().contains("NOT APPLIED"))
        val proposed = File(root, "catalog/compliance/proposed-confirmation.json").readText()
        assertTrue(proposed.contains("NOT_ACTIVATED"))
        assertFalse(File(root, "app/src/main/assets/content/bundled-content.json").readText()
            .contains("\"shareAlikeConfirmation\": \"CONFIRMED\""))
    }

    @Test
    fun regenerationPreservesTheCompliancePackageAndLeavesConfirmationUnresolved() {
        val sourceRoot = repositoryRoot()
        val root = File(System.getProperty("java.io.tmpdir"), "compliance-regen-${System.nanoTime()}")
        copyRegenSurface(sourceRoot, root)
        File(root, "catalog/compliance/share-alike-package.json").writeText("{}\n")
        val process = ProcessBuilder("python3", "catalog/write_compliance_package.py")
            .directory(root)
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().use { it.readText() }
        assertEquals(output, 0, process.waitFor())
        val regenerated = Json.parseToJsonElement(
            File(root, "catalog/compliance/share-alike-package.json").readText(),
        ).jsonObject
        assertEquals("READY_FOR_INDEPENDENT_SHARE_ALIKE_REVIEW", regenerated.getValue("classification").jsonPrimitive.content)
        assertEquals("UNCONFIRMED", regenerated.getValue("shareAlikeConfirmationInSeed").jsonPrimitive.content)
        assertEquals("NOT_ACTIVATED", regenerated.getValue("proposedConfirmation").jsonPrimitive.content)
        val coverage = regenerated.getValue("coverage").jsonObject
        assertEquals(1000, coverage.getValue("audioModels").jsonPrimitive.content.toInt())
        assertEquals(5, coverage.getValue("sourceRecords").jsonPrimitive.content.toInt())
        assertEquals(0, coverage.getValue("itemsWithIssues").jsonPrimitive.content.toInt())
        val seed = File(root, "app/src/main/assets/content/bundled-content.json").readText()
        assertTrue(seed.contains("\"shareAlikeConfirmation\": \"UNCONFIRMED\""))
        assertFalse(seed.contains("\"shareAlikeConfirmation\": \"CONFIRMED\""))
        requiredAssetNotices.forEach { relative ->
            assertTrue(File(root, relative).isFile && File(root, relative).length() > 0L)
        }
        val prepare = File(sourceRoot, "catalog/prepare_shtooka_catalog.py").readText()
        assertTrue(prepare.contains("write_compliance_artifacts"))
        assertTrue(prepare.contains("Share-alike confirmation stays UNCONFIRMED"))
    }

    @Test
    fun assembleReleaseStaysBlockedWhileShareAlikeIsUnconfirmed() {
        val assessment = assessRepositoryRelease(repositoryRoot())
        assertEquals(ReleaseDecision.NOT_APPROVED, assessment.decision)
        assertTrue(assessment.blockers.contains(ReleaseBlocker.SHARE_ALIKE_UNCONFIRMED))
        assertTrue(assessment.blockers.contains(ReleaseBlocker.OFFLINE_VERIFICATION_NOT_RETAINED))
        assertTrue(assessment.blockers.contains(ReleaseBlocker.REBOOT_EVIDENCE_NOT_RETAINED))
        assertTrue(assessment.blockers.contains(ReleaseBlocker.LESSON_CYCLE_NOT_RETAINED))
        assertTrue(assessment.blockers.contains(ReleaseBlocker.OWNER_RELEASE_DECISION_NOT_RETAINED))
        assertFalse(assessment.blockers.contains(ReleaseBlocker.PATHWAY_INCOMPLETE))
        assertEquals(AcceptanceDisposition.OUTSTANDING, ACCEPTANCE_DISPOSITION.getValue("AT-001"))
        assertEquals(AcceptanceDisposition.OUTSTANDING, ACCEPTANCE_DISPOSITION.getValue("AT-004"))
        assertEquals(AcceptanceDisposition.OUTSTANDING, ACCEPTANCE_DISPOSITION.getValue("AT-020"))
        assertEquals(AcceptanceDisposition.OUTSTANDING, ACCEPTANCE_DISPOSITION.getValue("AT-030"))
        assertEquals(AcceptanceDisposition.OUTSTANDING, ACCEPTANCE_DISPOSITION.getValue("AT-031"))
    }

    private fun packagedDocument(root: File) =
        (SeedJson.parse(File(root, "app/src/main/assets/content/bundled-content.json").readText()) as SeedParse.Ok)
            .document

    private fun compliancePackage(root: File) =
        Json.parseToJsonElement(File(root, "catalog/compliance/share-alike-package.json").readText()).jsonObject

    private fun copyRegenSurface(from: File, to: File) {
        val relativePaths = listOf(
            "app/src/main/assets/content/bundled-content.json",
            "catalog/license-chains.json",
            "catalog/write_compliance_package.py",
            "catalog/compliance/CATALOG_NOTICE.md",
            "catalog/licenses/cc-by-sa-4.0.txt",
            "catalog/licenses/cc-by-3.0-us.html",
            "catalog/licenses/cc-by-2.0-fr.html",
            "catalog/licenses/public-domain-notice.txt",
            "catalog/licenses/shtooka-readme-sakhno.txt",
            "catalog/licenses/shtooka-readme-streit.txt",
        )
        for (relative in relativePaths) {
            val dest = File(to, relative)
            dest.parentFile?.mkdirs()
            File(from, relative).copyTo(dest, overwrite = true)
        }
    }

    private fun repositoryRoot(): File {
        val candidates = listOf(File("."), File(".."))
        return candidates.first { File(it, CATALOG_INTAKE).isFile }.canonicalFile
    }
}
