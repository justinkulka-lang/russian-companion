package dev.russiancompanion.catalog

import dev.russiancompanion.data.local.seed.SeedJson
import dev.russiancompanion.data.local.seed.SeedParse
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
import java.security.MessageDigest

class CatalogIntakeTest {
    @Test
    fun installedDropMatchesPackagedAudioAndRecordsEveryIndexedFile() {
        val root = repositoryRoot()
        val intake = File(root, CATALOG_INTAKE).readText()
        val decisions = File(root, CATALOG_SOURCE_DECISIONS).readText()
        val assessment = assessCatalogIntake(intake, decisions)
        assertEquals(true, assessment.readable)
        assertEquals("INSTALLED_DROP", assessment.state)
        assertEquals(true, assessment.replacePackagedSeed)
        assertEquals(emptyList<String>(), assessment.excludedIds)
        val seed = File(root, "app/src/main/assets/content/bundled-content.json").readText()
        assertFalse(seed.contains("\"courseSegments\": []"))
        assertTrue(seed.contains("\"shareAlikeConfirmation\": \"CONFIRMED\""))
        assertFalse(seed.contains("en.openrussian.org/dictionary-data"))
        val document = (SeedJson.parse(seed) as SeedParse.Ok).document
        assertEquals(document.audioModels.map { it.id }, assessment.eligibleIds)
        document.audioModels.forEach { audio ->
            val file = File(root, "app/src/main/assets/${audio.localAudioReference}")
            assertTrue(file.isFile && file.length() > 32)
        }
        assertTrue(decisions.contains("\"sourceId\": \"file:"))
        assertFalse(decisions.contains("CONFIRMED"))
        val retained = File(root, RELEASE_EVIDENCE_MANIFEST).readText()
        assertFalse(retained.contains("RETAINED"))
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
    fun recordedSourceExclusionsStayOutOfThePackagedSeed() {
        val root = repositoryRoot()
        val decisions = File(root, CATALOG_SOURCE_DECISIONS).readText()
        assertTrue(decisions.contains("\"sourceId\": \"openrussian-audio\""))
        assertTrue(decisions.contains("\"sourceId\": \"tatoeba-audio\""))
        assertTrue(decisions.contains("\"sourceId\": \"penguin-course-text\""))
        assertFalse(decisions.contains("CONFIRMED"))
        val openRussianAudio = proposal(provenance = "https://en.openrussian.org/dictionary-data")
        val assessment = assessCatalogIntake(
            intake("INSTALLED_DROP", openRussianAudio),
            decisions,
        )
        assertEquals(false, assessment.replacePackagedSeed)
        assertEquals(listOf("audio-1"), assessment.excludedIds)
        val seed = File(root, "app/src/main/assets/content/bundled-content.json").readText()
        assertFalse(seed.contains("en.openrussian.org/dictionary-data"))
        val retained = File(root, RELEASE_EVIDENCE_MANIFEST).readText()
        assertFalse(retained.contains("RETAINED"))
    }

    @Test
    fun anInstalledDropOfOneEligibleProposalMayReplaceTheSeed() {
        val assessment = assessCatalogIntake(intake("INSTALLED_DROP", proposal()))
        assertEquals(true, assessment.replacePackagedSeed)
        assertEquals(listOf("audio-1"), assessment.eligibleIds)
    }

    @Test
    fun packagedAudioMatchesRecordedLicenseChainsAndStaysUnreleased() {
        val root = repositoryRoot()
        val chainText = File(root, "catalog/license-chains.json").readText()
        val chains = Json.parseToJsonElement(chainText).jsonObject
        assertEquals(1, chains.getValue("chainsVersion").jsonPrimitive.content.toInt())
        val dictionary = chains.getValue("openRussianDictionary").jsonObject
        assertEquals(
            "1fd608d858f88dd2c2f5fe7ba3a1c4057294caac",
            dictionary.getValue("commit").jsonPrimitive.content,
        )
        assertEquals("Update 2020-09-09", dictionary.getValue("export").jsonPrimitive.content)
        assertEquals("CC BY-SA 4.0", dictionary.getValue("license").jsonPrimitive.content)
        val expectedBlobs = mapOf(
            "nouns.csv" to ("202e6e19918681f1de061aa8b1839885721e60e9" to "c388f9e6dde51932832be8d7e9afdbe9f0acee72fcf70677f0fe25ea61293c84"),
            "verbs.csv" to ("efc1aebf79e36dc97d86c9bbce93810b7b1a997a" to "8659de6799b949fb35f080b08088fb7d347ed300490954ebb380a31642646e1d"),
            "adjectives.csv" to ("09986b701d1b232abb50db96c30b81ceab1c6526" to "89ab5d10dcd2f21f6b485de704aef372f32251468e3aa96f18b21e259f26b80a"),
            "others.csv" to ("2f819ba39652fceab3afe39ff9ef2a69b0dbd49d" to "9f22a16b17fc9a564298112168b667fced11aafb254ccebf5b7544fc37cdaa92"),
        )
        val dictionaryFiles = dictionary.getValue("files").jsonArray.map { it.jsonObject }
        assertEquals(expectedBlobs.keys, dictionaryFiles.map { it.getValue("path").jsonPrimitive.content }.toSet())
        dictionaryFiles.forEach { row ->
            val expected = expectedBlobs.getValue(row.getValue("path").jsonPrimitive.content)
            assertEquals(expected.first, row.getValue("gitBlob").jsonPrimitive.content)
            assertEquals(expected.second, row.getValue("sha256").jsonPrimitive.content)
        }
        val frequency = chains.getValue("frequencySnapshot").jsonObject
        assertEquals("https://en.openrussian.org/list/all", frequency.getValue("source").jsonPrimitive.content)
        assertEquals("CC BY-SA 4.0", frequency.getValue("license").jsonPrimitive.content)
        val consulted = frequency.getValue("consultedStarts").jsonArray.map { it.jsonPrimitive.content.toInt() }
        val frequencyPages = frequency.getValue("pages").jsonArray.map { it.jsonObject }
        assertEquals(consulted, frequencyPages.map { it.getValue("start").jsonPrimitive.content.toInt() })
        assertTrue(consulted.isNotEmpty())
        assertEquals(0, consulted.first())
        consulted.zipWithNext { left, right -> assertEquals(50, right - left) }
        frequencyPages.forEach { page ->
            val path = page.getValue("path").jsonPrimitive.content
            assertEquals(page.getValue("sha256").jsonPrimitive.content, sha256(File(root, path)))
        }
        val packageEvidence = mutableMapOf<String, PackageChainEvidence>()
        chains.getValue("snapshots").jsonArray.map { it.jsonObject }.forEach { snapshot ->
            val path = snapshot.getValue("path").jsonPrimitive.content
            val file = File(root, path)
            assertEquals(snapshot.getValue("sha256").jsonPrimitive.content, sha256(file))
            val packageName = snapshot.getValue("package").jsonPrimitive.content
            val evidence = packageEvidence.getOrPut(packageName) { PackageChainEvidence() }
            when (snapshot.getValue("role").jsonPrimitive.content) {
                "package-readme" -> evidence.readme = file.readText()
                "package-index" -> {
                    evidence.indexed = indexFilenames(file)
                    evidence.indexLicenses = file.readLines().filter { it.startsWith("LICENSE=") }
                }
                "acquisition-listing" -> evidence.listed = listingFilenames(file.readText())
            }
        }
        assertEquals(setOf("rus-balm-voc", "rus-balm-voc-sakhno"), packageEvidence.keys)
        packageEvidence.values.forEach { evidence ->
            assertEquals(listOf("LICENSE=Creative Commons BY"), evidence.indexLicenses)
        }
        val seed = (SeedJson.parse(File(root, "app/src/main/assets/content/bundled-content.json").readText()) as SeedParse.Ok).document
        assertEquals((1..30).toList(), seed.courseSegments.map { it.lessonNumber })
        assertEquals(120, seed.planDefinitions.size)
        assertEquals(30, seed.gateDefinitions.size)
        assertTrue(seed.optionalDrills.isEmpty())
        assertTrue(seed.audioModels.isNotEmpty())
        seed.sourceRecords.forEach { record ->
            assertEquals("CONFIRMED", record.shareAlikeConfirmation)
        }
        val audioFiles = File(root, "app/src/main/assets/audio").listFiles { file -> file.extension == "ogg" }!!.toList()
        val chainRows = chains.getValue("files").jsonArray.map { it.jsonObject }
        val chainNames = chainRows.map { it.getValue("filename").jsonPrimitive.content }
        assertEquals(chainNames.size, chainNames.toSet().size)
        assertEquals(audioFiles.map { it.name }.toSet(), chainNames.toSet())
        assertEquals(
            chainNames.toSet(),
            seed.audioModels.map { it.localAudioReference.removePrefix("audio/") }.toSet(),
        )
        val byFilename = chainRows.associateBy { it.getValue("filename").jsonPrimitive.content }
        val textsById = seed.sourceTexts.associateBy { it.id }
        val rightsById = seed.assetRightsRecords.associateBy { it.id }
        assertEquals(seed.audioModels.size, seed.audioModels.map { it.id }.toSet().size)
        assertEquals(seed.audioModels.size, seed.audioModels.map { it.localAudioReference }.toSet().size)
        assertEquals(seed.sourceTexts.size, textsById.size)
        assertEquals(seed.assetRightsRecords.size, rightsById.size)
        val compatiblePageLicenses = setOf("CC BY 2.0 FR", "CC BY 3.0 US", "CC BY 3.0 FR", "Public domain")
        seed.audioModels.forEach { audio ->
            val filename = audio.localAudioReference.removePrefix("audio/")
            assertEquals("audio/$filename", audio.localAudioReference)
            val packaged = File(root, "app/src/main/assets/${audio.localAudioReference}")
            val header = ByteArray(4)
            packaged.inputStream().use { input -> assertEquals(4, input.read(header)) }
            assertEquals("OggS", String(header, Charsets.US_ASCII))
            assertTrue(packaged.length() > 32)
            val row = byFilename.getValue(filename)
            assertEquals(sha256(packaged), row.getValue("sha256").jsonPrimitive.content)
            val text = textsById.getValue(checkNotNull(audio.sourceTextId))
            val rights = rightsById.getValue(checkNotNull(audio.rightsRecordId))
            assertEquals(checkNotNull(audio.sourceRecordId), text.sourceRecordId)
            assertEquals(audio.sourceRecordId, rights.sourceRecordId)
            assertEquals(audio.id, rights.assetIdentifier)
            assertEquals("COMPATIBLE", rights.licenseStatus)
            assertEquals("COMPATIBLE", rights.compatibilityDecision)
            assertEquals("INCLUDED", rights.inclusionResult)
            assertTrue(text.matchVerified)
            assertTrue(text.stressMarkedText.isNotBlank())
            val acquisition = row.getValue("acquisition").jsonObject
            val authority = row.getValue("licenseAuthority").jsonObject
            val attribution = row.getValue("attribution").jsonPrimitive.content
            assertTrue(attribution.isNotBlank())
            val host = acquisition.getValue("host").jsonPrimitive.content
            val url = acquisition.getValue("url").jsonPrimitive.content
            if (host == "fsi-languages.yojik.eu") {
                assertEquals("package-readme", authority.getValue("kind").jsonPrimitive.content)
                assertTrue(url.startsWith("https://fsi-languages.yojik.eu/audiocollections/detailled/"))
                assertTrue(url.endsWith("/ogg/$filename"))
                val packageName = authority.getValue("package").jsonPrimitive.content
                assertTrue(url.contains("/$packageName/ogg/$filename"))
                val evidence = packageEvidence.getValue(packageName)
                assertTrue(filename in evidence.indexed)
                assertTrue(filename in evidence.listed)
                packageEvidence.filterKeys { it != packageName }.values.forEach { other ->
                    assertFalse(filename in other.indexed)
                }
                val license = authority.getValue("license").jsonPrimitive.content
                val licenseUrl = authority.getValue("licenseUrl").jsonPrimitive.content
                val expectedLicense = when (packageName) {
                    "rus-balm-voc" -> "CC BY 2.0 FR"
                    "rus-balm-voc-sakhno" -> "CC BY 3.0 US"
                    else -> error(packageName)
                }
                assertEquals(expectedLicense, license)
                assertTrue(evidence.readme.contains(licenseUrl))
                val creators = authority.getValue("creators").jsonPrimitive.content
                assertTrue(creators.isNotBlank())
                assertTrue(evidence.readme.split(Regex("\\s+")).joinToString(" ").contains(creators))
                val readmePath = authority.getValue("readmeSource").jsonPrimitive.content
                assertEquals(sha256(File(root, readmePath)), authority.getValue("readmeSha256").jsonPrimitive.content)
                assertFalse(url.contains("readme.txt"))
            } else {
                assertEquals("upload.wikimedia.org", host)
                assertEquals("commons-file-page", authority.getValue("kind").jsonPrimitive.content)
                assertTrue(url.startsWith("https://upload.wikimedia.org/"))
                assertTrue(compatiblePageLicenses.contains(authority.getValue("license").jsonPrimitive.content))
                assertTrue(authority.getValue("filePageLicense").jsonPrimitive.content.isNotBlank())
            }
        }
        val decisions = Json.parseToJsonElement(File(root, CATALOG_SOURCE_DECISIONS).readText()).jsonObject
        assertFalse(File(root, CATALOG_SOURCE_DECISIONS).readText().contains("CONFIRMED"))
        val included = decisions.getValue("decisions").jsonArray.map { it.jsonObject }
            .filter { it.getValue("sourceId").jsonPrimitive.content.startsWith("file:") }
            .filter { it.getValue("decision").jsonPrimitive.content == "INCLUDED" }
            .map { it.getValue("sourceId").jsonPrimitive.content.removePrefix("file:") }
            .toSet()
        assertEquals(chainNames.toSet(), included)
        val addendum = File(root, "docs/research/CATALOG_SOURCING_ADDENDUM_v1.0.md")
        assertTrue(addendum.isFile)
        assertTrue(addendum.readText().startsWith("# CATALOG SOURCING ADDENDUM v1.0"))
        val retained = Json.parseToJsonElement(File(root, RELEASE_EVIDENCE_MANIFEST).readText()).jsonObject
        val retainedStates = retained.getValue("records").jsonArray.map { it.jsonObject }
        assertEquals(
            listOf("AT-004", "AT-020", "AT-030", "AT-031"),
            retainedStates.map { it.getValue("procedure").jsonPrimitive.content },
        )
        retainedStates.forEach { record -> assertEquals("MISSING", record.getValue("state").jsonPrimitive.content) }
        val release = assessRepositoryRelease(root)
        assertEquals(ReleaseDecision.NOT_APPROVED, release.decision)
        assertFalse(release.blockers.contains(ReleaseBlocker.SHARE_ALIKE_UNCONFIRMED))
        assertFalse(release.blockers.contains(ReleaseBlocker.PATHWAY_INCOMPLETE))
        assertFalse(release.blockers.contains(ReleaseBlocker.PATHWAY_NOT_INSTALLED))
        assertFalse(release.decision.name.startsWith("APPROVED"))
    }

    @Test
    fun catalogRegenerationPreservesTheCompleteBeginnerConductor() {
        val root = repositoryRoot()
        val prepare = File(root, "catalog/prepare_shtooka_catalog.py").readText()
        assertTrue(prepare.contains("catalog/beginner_pathway.json"))
        assertTrue(prepare.contains("bind_beginner_pathway"))
        assertFalse(prepare.contains("placeholder_pathway.json"))
        assertFalse(File(root, "catalog/placeholder_pathway.json").isFile)
        val pathway = Json.parseToJsonElement(File(root, "catalog/beginner_pathway.json").readText()).jsonObject
        val seed = (SeedJson.parse(
            File(root, "app/src/main/assets/content/bundled-content.json").readText(),
        ) as SeedParse.Ok).document
        val expectedLessons = (1..30).toList()
        val jsonLessons = pathway.getValue("courseSegments").jsonArray.map { segment ->
            segment.jsonObject.getValue("lessonNumber").jsonPrimitive.content.toInt()
        }
        assertEquals(expectedLessons, jsonLessons)
        assertEquals(expectedLessons, seed.courseSegments.map { it.lessonNumber })
        val unboundListen = pathway.getValue("planDefinitions").jsonArray.filter { plan ->
            plan.jsonObject.getValue("activityType").jsonPrimitive.content == "LISTEN_RECORD_COMPARE"
        }
        assertEquals(30, unboundListen.size)
        unboundListen.forEach { plan ->
            assertEquals(0, plan.jsonObject.getValue("audioModelIds").jsonArray.size)
        }
        val boundListen = seed.planDefinitions.filter { it.activityType == "LISTEN_RECORD_COMPARE" }
        assertEquals(30, boundListen.size)
        assertEquals(seed.audioModels.take(30).map { it.id }, boundListen.map { it.audioModelIds.single() })
        assertEquals(120, seed.planDefinitions.size)
        assertEquals(30, seed.gateDefinitions.size)
        assertEquals(120, pathway.getValue("planDefinitions").jsonArray.size)
        assertEquals(30, pathway.getValue("gateDefinitions").jsonArray.size)
        assertFalse(prepare.contains("\"courseSegments\": []"))
        assertTrue(prepare.contains("write_compliance_artifacts"))
        assertTrue(prepare.contains("SHARE_ALIKE_CONFIRMATION"))
        assertTrue(File(root, "catalog/compliance/share-alike-package.json").isFile)
        assertTrue(File(root, "app/src/main/assets/content/catalog-notice.txt").isFile)
        assertTrue(File(root, "app/src/main/assets/licenses/cc-by-sa-4.0.txt").isFile)
    }

    private fun sha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(1024 * 1024)
            while (true) {
                val count = input.read(buffer)
                if (count < 0) break
                digest.update(buffer, 0, count)
            }
        }
        return digest.digest().joinToString("") { byte -> "%02x".format(byte) }
    }

    private fun indexFilenames(file: File): Set<String> {
        val names = mutableSetOf<String>()
        file.forEachLine { line ->
            if (line.startsWith("[rus-") && line.endsWith(".ogg]")) {
                names.add(line.substring(1, line.length - 1))
            }
        }
        return names
    }

    private fun listingFilenames(text: String): Set<String> {
        return Regex("""href="(rus-[0-9a-f]+\.ogg)"""")
            .findAll(text)
            .map { it.groupValues[1] }
            .toSet()
    }

    private class PackageChainEvidence {
        var readme: String = ""
        var indexed: Set<String> = emptySet()
        var listed: Set<String> = emptySet()
        var indexLicenses: List<String> = emptyList()
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
        provenance: String = "test-source",
    ) = AudioProposal(
        id = id,
        localAudioReference = "audio/audio-1",
        licenseStatus = licenseStatus,
        compatibilityDecision = compatibilityDecision,
        stressMarkedText = "SOURCE-TEXT-TOKEN",
        matchVerified = matchVerified,
        provenance = provenance,
        redistributionBasis = "test-rights",
        licenseName = "TEST-LICENSE",
        attribution = "Test attribution",
        modificationStatus = "UNMODIFIED",
        shareAlikeTreatment = "share-alike-applies",
        shareAlikeConfirmation = "UNCONFIRMED",
    )
}
