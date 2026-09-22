package dev.russiancompanion.release

import dev.russiancompanion.acceptance.ACCEPTANCE_DISPOSITION
import dev.russiancompanion.acceptance.AcceptanceDisposition
import dev.russiancompanion.data.local.audio
import dev.russiancompanion.data.local.gate
import dev.russiancompanion.data.local.minimalDocument
import dev.russiancompanion.data.local.rights
import dev.russiancompanion.data.local.seed.CourseSegmentSeed
import dev.russiancompanion.data.local.seed.GateConditionSeed
import dev.russiancompanion.data.local.seed.PlanDefinitionSeed
import dev.russiancompanion.data.local.seed.SeedJson
import dev.russiancompanion.data.local.seed.SeedParse
import dev.russiancompanion.data.local.source
import dev.russiancompanion.data.local.text
import dev.russiancompanion.domain.pathway.PENGUIN_BEGINNER_ARC_LESSON_COUNT
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
        assertTrue(assessment.blockers.containsAll(packagedBlockers))
        assertFalse(assessment.blockers.contains(ReleaseBlocker.SHARE_ALIKE_UNCONFIRMED))
        assertFalse(assessment.blockers.contains(ReleaseBlocker.PATHWAY_INCOMPLETE))
        assertFalse(assessment.blockers.contains(ReleaseBlocker.PATHWAY_NOT_INSTALLED))
        assertFalse(assessment.blockers.contains(ReleaseBlocker.EVIDENCE_MANIFEST_UNREADABLE))
        assertFalse(assessment.blockers.contains(ReleaseBlocker.LICENSE_NOTICES_NOT_PACKAGED))
        assertFalse(assessment.blockers.contains(ReleaseBlocker.COMPLIANCE_PACKAGE_UNREADABLE))
        assertFalse(assessment.blockers.contains(ReleaseBlocker.SHARE_ALIKE_TREATMENT_INCONSISTENT))
        assertEquals(AcceptanceDisposition.OUTSTANDING, ACCEPTANCE_DISPOSITION.getValue("AT-001"))
    }

    @Test
    fun packagedCompletePathwayClearsPathwayIncompleteWhenShareAlikeAndEvidenceAreSupplied() {
        val (root, manifest) = retainedFixture()
        val parsed = SeedJson.parse(
            File(repositoryRoot(), "app/src/main/assets/content/bundled-content.json").readText(),
        )
        assertTrue(parsed is SeedParse.Ok)
        val confirmed = (parsed as SeedParse.Ok).document.copy(
            sourceRecords = parsed.document.sourceRecords.map { record ->
                record.copy(shareAlikeConfirmation = "CONFIRMED")
            },
        )
        val assessment = assessPackagedRelease(SeedJson.encode(confirmed), manifest, root)
        assertEquals(ReleaseDecision.APPROVED, assessment.decision)
        assertEquals(emptyList<ReleaseBlocker>(), assessment.blockers)
        assertFalse(assessment.blockers.contains(ReleaseBlocker.PATHWAY_INCOMPLETE))
        assertEquals(AcceptanceDisposition.OUTSTANDING, ACCEPTANCE_DISPOSITION.getValue("AT-001"))
    }

    @Test
    fun aPartialOrMissingLessonStaysBlocked() {
        val (root, manifest) = retainedFixture()
        val complete = preparedCatalog(shareAlike = "CONFIRMED")
        val missingLesson = complete.copy(
            courseSegments = complete.courseSegments.filter { it.lessonNumber != 12 },
            planDefinitions = complete.planDefinitions.filter { it.segmentId != "segment-12" },
            gateDefinitions = complete.gateDefinitions.filter { it.segmentId != "segment-12" },
        )
        val missingListen = complete.copy(
            planDefinitions = complete.planDefinitions.filterNot {
                it.id == "plan-listen-$PENGUIN_BEGINNER_ARC_LESSON_COUNT"
            },
        )
        val missingGate = complete.copy(
            gateDefinitions = complete.gateDefinitions.filterNot { it.id == "gate-7" },
        )
        val missingAudioBinding = complete.copy(
            planDefinitions = complete.planDefinitions.map { plan ->
                if (plan.id == "plan-listen-12") plan.copy(audioModelIds = listOf("audio-missing")) else plan
            },
        )
        val emptyPathway = complete.copy(
            courseSegments = emptyList(),
            planDefinitions = emptyList(),
            gateDefinitions = emptyList(),
        )
        for (catalog in listOf(missingLesson, missingListen, missingGate, missingAudioBinding)) {
            val assessment = assessPackagedRelease(SeedJson.encode(catalog), manifest, root)
            assertEquals(ReleaseDecision.NOT_APPROVED, assessment.decision)
            assertTrue(assessment.blockers.contains(ReleaseBlocker.PATHWAY_INCOMPLETE))
        }
        val empty = assessPackagedRelease(SeedJson.encode(emptyPathway), manifest, root)
        assertEquals(ReleaseDecision.NOT_APPROVED, empty.decision)
        assertTrue(empty.blockers.contains(ReleaseBlocker.PATHWAY_NOT_INSTALLED))
        assertFalse(empty.blockers.contains(ReleaseBlocker.PATHWAY_INCOMPLETE))
    }

    @Test
    fun onlyTheCompleteBeginnerArcClearsThePathwayIncompleteBlocker() {
        val (root, manifest) = retainedFixture()
        val incomplete = assessPackagedRelease(
            SeedJson.encode(preparedCatalog(shareAlike = "CONFIRMED", lessonCount = 29)),
            manifest,
            root,
        )
        assertEquals(ReleaseDecision.NOT_APPROVED, incomplete.decision)
        assertEquals(listOf(ReleaseBlocker.PATHWAY_INCOMPLETE), incomplete.blockers)
        val complete = assessPackagedRelease(
            SeedJson.encode(preparedCatalog(shareAlike = "CONFIRMED")),
            manifest,
            root,
        )
        assertEquals(ReleaseDecision.APPROVED, complete.decision)
        assertFalse(complete.blockers.contains(ReleaseBlocker.PATHWAY_INCOMPLETE))
        assertEquals(emptyList<ReleaseBlocker>(), complete.blockers)
    }

    @Test
    fun missingOrContradictoryComplianceMetadataFailsClosed() {
        val root = Files.createTempDirectory("release-notices").toFile()
        File(root, "app/src/main/assets/content").mkdirs()
        File(repositoryRoot(), "app/src/main/assets/content/bundled-content.json")
            .copyTo(File(root, "app/src/main/assets/content/bundled-content.json"))
        File(root, "release").mkdirs()
        File(root, RELEASE_EVIDENCE_MANIFEST).writeText(committedManifest())
        val missing = assessRepositoryRelease(root)
        assertEquals(ReleaseDecision.NOT_APPROVED, missing.decision)
        assertTrue(missing.blockers.contains(ReleaseBlocker.LICENSE_NOTICES_NOT_PACKAGED))
        assertTrue(missing.blockers.contains(ReleaseBlocker.COMPLIANCE_PACKAGE_UNREADABLE))
        assertFalse(missing.blockers.contains(ReleaseBlocker.SHARE_ALIKE_UNCONFIRMED))

        val contradiction = Files.createTempDirectory("release-compliance").toFile()
        copyPackagedComplianceSurface(repositoryRoot(), contradiction)
        File(contradiction, "catalog/compliance/share-alike-package.json").writeText(
            """{"schemaVersion":1,"classification":"READY_FOR_INDEPENDENT_SHARE_ALIKE_REVIEW","shareAlikeConfirmationInSeed":"UNCONFIRMED","proposedConfirmation":"NOT_ACTIVATED","coverage":{"sourceRecords":5,"sourceTexts":1000,"assetRightsRecords":1000,"audioModels":1,"itemsWithIssues":0}}""",
        )
        val wrongCounts = assessRepositoryRelease(contradiction)
        assertTrue(wrongCounts.blockers.contains(ReleaseBlocker.COMPLIANCE_PACKAGE_UNREADABLE))
        assertEquals(ReleaseDecision.NOT_APPROVED, wrongCounts.decision)
    }

    @Test
    fun blockedClassificationFailsClosedAfterActivation() {
        val root = Files.createTempDirectory("release-blocked-class").toFile()
        copyPackagedComplianceSurface(repositoryRoot(), root)
        replaceIn(
            File(root, "catalog/compliance/share-alike-package.json"),
            "\"classification\": \"SHARE_ALIKE_CONFIRMED\"",
            "\"classification\": \"BLOCKED_BY_UNRESOLVED_LICENSE_INTERPRETATION\"",
        )
        val assessment = assessRepositoryRelease(root)
        assertEquals(ReleaseDecision.NOT_APPROVED, assessment.decision)
        assertTrue(assessment.blockers.contains(ReleaseBlocker.COMPLIANCE_PACKAGE_UNREADABLE))
        assertFalse(assessment.blockers.contains(ReleaseBlocker.SHARE_ALIKE_UNCONFIRMED))
    }

    @Test
    fun mismatchedActivationStateFailsClosed() {
        val root = Files.createTempDirectory("release-activation").toFile()
        copyPackagedComplianceSurface(repositoryRoot(), root)
        replaceIn(
            File(root, "catalog/compliance/share-alike-package.json"),
            "\"classification\": \"SHARE_ALIKE_CONFIRMED\"",
            "\"classification\": \"READY_FOR_INDEPENDENT_SHARE_ALIKE_REVIEW\"",
        )
        val assessment = assessRepositoryRelease(root)
        assertEquals(ReleaseDecision.NOT_APPROVED, assessment.decision)
        assertTrue(assessment.blockers.contains(ReleaseBlocker.COMPLIANCE_PACKAGE_UNREADABLE))
        assertFalse(assessment.blockers.contains(ReleaseBlocker.SHARE_ALIKE_UNCONFIRMED))
    }

    @Test
    fun alteredCatalogTreatmentFailsClosed() {
        val root = Files.createTempDirectory("release-treatment").toFile()
        copyPackagedComplianceSurface(repositoryRoot(), root)
        val packageFile = File(root, "catalog/compliance/share-alike-package.json")
        packageFile.writeText(
            """{"schemaVersion":1,"classification":"SHARE_ALIKE_CONFIRMED","shareAlikeConfirmationInSeed":"CONFIRMED","proposedConfirmation":"ACTIVATED","catalogShareAlikeTreatment":"text-layer only","coverage":{"sourceRecords":5,"sourceTexts":1000,"assetRightsRecords":1000,"audioModels":1000,"itemsWithIssues":0}}""",
        )
        val assessment = assessRepositoryRelease(root)
        assertEquals(ReleaseDecision.NOT_APPROVED, assessment.decision)
        assertTrue(assessment.blockers.contains(ReleaseBlocker.SHARE_ALIKE_TREATMENT_INCONSISTENT))
        assertFalse(assessment.blockers.contains(ReleaseBlocker.SHARE_ALIKE_UNCONFIRMED))
    }

    @Test
    fun confirmedSeedWithUnappliedProposalFailsClosed() {
        val root = Files.createTempDirectory("release-unapplied").toFile()
        copyPackagedComplianceSurface(repositoryRoot(), root)
        replaceIn(
            File(root, "catalog/compliance/share-alike-package.json"),
            "\"proposedConfirmation\": \"ACTIVATED\"",
            "\"proposedConfirmation\": \"NOT_ACTIVATED\"",
        )
        val assessment = assessRepositoryRelease(root)
        assertEquals(ReleaseDecision.NOT_APPROVED, assessment.decision)
        assertTrue(assessment.blockers.contains(ReleaseBlocker.COMPLIANCE_PACKAGE_UNREADABLE))
        assertFalse(assessment.blockers.contains(ReleaseBlocker.SHARE_ALIKE_UNCONFIRMED))
    }

    @Test
    fun contradictoryShareAlikeTreatmentFailsClosed() {
        val (root, manifest) = retainedFixture()
        val catalog = preparedCatalog(shareAlike = "CONFIRMED").copy(
            sourceRecords = listOf(
                source().copy(
                    licenseName = "CC BY 2.0 FR; CC BY-SA 4.0",
                    shareAlikeTreatment = "unspecified",
                    shareAlikeConfirmation = "CONFIRMED",
                ),
            ),
        )
        val assessment = assessPackagedRelease(SeedJson.encode(catalog), manifest, root)
        assertEquals(ReleaseDecision.NOT_APPROVED, assessment.decision)
        assertEquals(listOf(ReleaseBlocker.SHARE_ALIKE_TREATMENT_INCONSISTENT), assessment.blockers)
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
    fun notApplicableShareAlikeOnCcBySaCatalogFailsClosed() {
        val (root, manifest) = retainedFixture()
        val parsed = SeedJson.parse(
            File(repositoryRoot(), "app/src/main/assets/content/bundled-content.json").readText(),
        )
        assertTrue(parsed is SeedParse.Ok)
        val skipped = (parsed as SeedParse.Ok).document.copy(
            sourceRecords = parsed.document.sourceRecords.map { record ->
                record.copy(shareAlikeConfirmation = "NOT_APPLICABLE")
            },
        )
        val assessment = assessPackagedRelease(SeedJson.encode(skipped), manifest, root)
        assertEquals(ReleaseDecision.NOT_APPROVED, assessment.decision)
        assertTrue(assessment.blockers.contains(ReleaseBlocker.SHARE_ALIKE_UNCONFIRMED))
    }

    @Test
    fun notApplicableConfirmationFailsClosedOnTheCompliancePackage() {
        val root = Files.createTempDirectory("release-not-applicable").toFile()
        copyPackagedComplianceSurface(repositoryRoot(), root)
        replaceIn(
            File(root, "app/src/main/assets/content/bundled-content.json"),
            "\"shareAlikeConfirmation\": \"CONFIRMED\"",
            "\"shareAlikeConfirmation\": \"NOT_APPLICABLE\"",
        )
        replaceIn(
            File(root, "catalog/compliance/share-alike-package.json"),
            "\"shareAlikeConfirmationInSeed\": \"CONFIRMED\"",
            "\"shareAlikeConfirmationInSeed\": \"NOT_APPLICABLE\"",
        )
        val assessment = assessRepositoryRelease(root)
        assertEquals(ReleaseDecision.NOT_APPROVED, assessment.decision)
        assertTrue(assessment.blockers.contains(ReleaseBlocker.SHARE_ALIKE_UNCONFIRMED))
        assertTrue(assessment.blockers.contains(ReleaseBlocker.COMPLIANCE_PACKAGE_UNREADABLE))
        assertTrue(File(repositoryRoot(), "app/src/main/assets/content/bundled-content.json").readText()
            .contains("\"shareAlikeConfirmation\": \"CONFIRMED\""))
        assertFalse(File(repositoryRoot(), "app/src/main/assets/content/bundled-content.json").readText()
            .contains("\"shareAlikeConfirmation\": \"NOT_APPLICABLE\""))
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
    fun aNonemptyTextFileDoesNotSatisfyRetainedEvidence() {
        val root = Files.createTempDirectory("release-evidence").toFile()
        val relative = "release/evidence/AT-004.json"
        File(root, relative).apply {
            parentFile?.mkdirs()
            writeText("any nonempty text that is not the evidence schema")
        }
        val manifest = manifest(
            """{"procedure":"AT-004","state":"RETAINED","evidencePath":"$relative"}""",
        )
        val blockers = assessPackagedRelease(
            SeedJson.encode(preparedCatalog(shareAlike = "CONFIRMED")),
            manifest,
            root,
        ).blockers
        assertTrue(blockers.contains(ReleaseBlocker.OFFLINE_VERIFICATION_NOT_RETAINED))
        assertTrue(blockers.contains(ReleaseBlocker.OWNER_RELEASE_DECISION_NOT_RETAINED))
    }

    @Test
    fun ownerDecisionCannotClearReleaseWithoutEveryMandatoryProcedure() {
        val root = Files.createTempDirectory("release-evidence").toFile()
        val records = mutableListOf<String>()
        for (procedure in listOf("AT-004", "AT-020", "AT-030", "AT-031")) {
            val relative = "release/evidence/$procedure.json"
            File(root, relative).apply {
                parentFile?.mkdirs()
                writeText(procedureEvidence(procedure))
            }
            records += """{"procedure":"$procedure","state":"RETAINED","evidencePath":"$relative"}"""
        }
        val blockers = assessPackagedRelease(
            SeedJson.encode(preparedCatalog(shareAlike = "CONFIRMED")),
            manifest(*records.toTypedArray()),
            root,
        ).blockers
        assertTrue(blockers.contains(ReleaseBlocker.INSTALLATION_NOT_RETAINED))
        assertTrue(blockers.contains(ReleaseBlocker.NAVIGATION_NOT_RETAINED))
        assertTrue(blockers.contains(ReleaseBlocker.ATTRIBUTIONS_NOT_RETAINED))
        assertTrue(blockers.contains(ReleaseBlocker.DEVICE_GATE_NOT_RETAINED))
        assertTrue(blockers.contains(ReleaseBlocker.CATALOG_LICENSE_NOT_RETAINED))
        assertTrue(blockers.contains(ReleaseBlocker.OWNER_RELEASE_DECISION_NOT_RETAINED))
        assertFalse(blockers.contains(ReleaseBlocker.OFFLINE_VERIFICATION_NOT_RETAINED))
    }

    @Test
    fun deviceProceduresRequireANamedDeviceAndMatchingCandidateIdentity() {
        val (root, _) = retainedFixture()
        File(root, "release/evidence/AT-001.json").writeText(
            procedureEvidence("AT-001", device = null),
        )
        val missingDevice = assessPackagedRelease(
            SeedJson.encode(preparedCatalog(shareAlike = "CONFIRMED")),
            File(root, "unused").let {
                val records = REQUIRED_RELEASE_PROCEDURES.map { (procedure, _) ->
                    """{"procedure":"$procedure","state":"RETAINED","evidencePath":"release/evidence/$procedure.json"}"""
                }
                manifest(*records.toTypedArray())
            },
            root,
        )
        assertTrue(missingDevice.blockers.contains(ReleaseBlocker.INSTALLATION_NOT_RETAINED))
        File(root, "release/evidence/AT-001.json").writeText(procedureEvidence("AT-001"))
        File(root, "release/evidence/AT-002.json").writeText(
            procedureEvidence("AT-002", digest = "cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc"),
        )
        val mismatched = assessPackagedRelease(
            SeedJson.encode(preparedCatalog(shareAlike = "CONFIRMED")),
            File(root, "unused").let {
                val records = REQUIRED_RELEASE_PROCEDURES.map { (procedure, _) ->
                    """{"procedure":"$procedure","state":"RETAINED","evidencePath":"release/evidence/$procedure.json"}"""
                }
                manifest(*records.toTypedArray())
            },
            root,
        )
        assertEquals(missingEvidenceBlockers, mismatched.blockers)
    }

    @Test
    fun anUndocumentedBooleanDoesNotMarkEvidenceRetained() {
        val (root, _) = retainedFixture()
        val manifest = """{"schemaVersion":2,"offlineVerification":true,"records":[]}"""
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

    private fun copyPackagedComplianceSurface(from: File, to: File) {
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
            RELEASE_EVIDENCE_MANIFEST,
        )
        for (relative in relativePaths) {
            val dest = File(to, relative)
            dest.parentFile?.mkdirs()
            File(from, relative).copyTo(dest, overwrite = true)
        }
    }

    private fun replaceIn(file: File, old: String, new: String) {
        val text = file.readText()
        assertTrue(text.contains(old))
        file.writeText(text.replace(old, new))
    }

    private fun repositoryRoot(): File {
        val candidates = listOf(File("."), File(".."))
        return candidates.first { File(it, RELEASE_EVIDENCE_MANIFEST).isFile }.canonicalFile
    }

    private fun retainedFixture(): Pair<File, String> {
        val root = Files.createTempDirectory("release-evidence").toFile()
        val records = mutableListOf<String>()
        for (procedure in REQUIRED_RELEASE_PROCEDURES.map { it.first }) {
            val relative = "release/evidence/$procedure.json"
            val evidence = File(root, relative)
            evidence.parentFile?.mkdirs()
            evidence.writeText(procedureEvidence(procedure))
            records += """{"procedure":"$procedure","state":"RETAINED","evidencePath":"$relative"}"""
        }
        return root to manifest(*records.toTypedArray())
    }

    private fun procedureEvidence(
        procedure: String,
        result: String = "PASS",
        commit: String = VALID_COMMIT,
        hash: String = VALID_HASH,
        digest: String = VALID_HASH,
        versionName: String = "1.0.0",
        versionCode: Int = 2,
        applicationId: String = "dev.russiancompanion",
        device: String? = if (procedure == "AT-031") null else "Galaxy S26 Ultra",
        ownerDecision: String? = if (procedure == "AT-031") "APPROVED" else null,
    ): String {
        val deviceField = if (device == null) "" else ""","deviceModel":"$device""""
        val ownerField = if (ownerDecision == null) {
            ""
        } else {
            ""","ownerDecision":"$ownerDecision","mandatoryProcedures":${
                proceduresBeforeOwner.joinToString(prefix = "[", postfix = "]") { "\"$it\"" }
            }"""
        }
        return """{"schemaVersion":2,"procedure":"$procedure","result":"$result","candidateCommit":"$commit","apkSha256":"$hash","sourceDigest":"$digest","versionName":"$versionName","versionCode":$versionCode,"applicationId":"$applicationId","executedAt":"2026-09-22T12:00"$deviceField,"artifactHashes":{"form":"$hash"}$ownerField}"""
    }

    private fun manifest(vararg records: String): String {
        return """{"schemaVersion":2,"records":[${records.joinToString(",")}]}"""
    }

    private fun preparedCatalog(
        shareAlike: String,
        lessonCount: Int = PENGUIN_BEGINNER_ARC_LESSON_COUNT,
    ) = minimalDocument().copy(
        courseSegments = (1..lessonCount).map { lesson ->
            CourseSegmentSeed(
                id = "segment-$lesson",
                lessonReference = "Lesson $lesson",
                lessonNumber = lesson,
                sortOrder = lesson,
            )
        },
        planDefinitions = (1..lessonCount).flatMap { lesson ->
            listOf(
                plan("plan-book-$lesson", "BOOK", 1, "segment-$lesson", lesson),
                plan("plan-anki-$lesson", "ANKI_DROID", 2, "segment-$lesson", lesson),
                plan("plan-listen-$lesson", "LISTEN_RECORD_COMPARE", 3, "segment-$lesson", null),
            )
        },
        gateDefinitions = (1..lessonCount).map { lesson ->
            gate(
                conditions = listOf(
                    GateConditionSeed("c-book-$lesson", "BOOK_REVISION_SCORE", "AT_LEAST", "1", 0),
                    GateConditionSeed("c-anki-$lesson", "ANKI_DROID_STATUS", "EQUALS", "MATURE", 1),
                    GateConditionSeed("c-work-$lesson", "PRESCRIBED_WORK_COMPLETION", "PRESENT", null, 2),
                ),
                id = "gate-$lesson",
                segmentId = "segment-$lesson",
            )
        },
        sourceRecords = listOf(source().copy(shareAlikeConfirmation = shareAlike)),
        sourceTexts = listOf(text(matchVerified = true)),
        assetRightsRecords = listOf(rights()),
        audioModels = listOf(audio()),
    )

    private fun plan(
        id: String,
        activityType: String,
        sortOrder: Int,
        segmentId: String = "segment-1",
        lessonNumber: Int? = if (activityType == "BOOK" || activityType == "ANKI_DROID") 1 else null,
    ) = PlanDefinitionSeed(
        id = id,
        segmentId = segmentId,
        activityType = activityType,
        instruction = "Use the assigned local activity",
        sortOrder = sortOrder,
        required = true,
        lessonNumber = lessonNumber,
        audioModelIds = if (activityType == "LISTEN_RECORD_COMPARE") listOf("audio-1") else emptyList(),
    )

    private companion object {
        const val VALID_COMMIT = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
        const val VALID_HASH = "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"
        val proceduresBeforeOwner = listOf(
            "AT-001",
            "AT-002",
            "AT-004",
            "AT-008",
            "AT-009",
            "AT-010",
            "AT-020",
            "AT-030",
        )
        val missingEvidenceBlockers = listOf(
            ReleaseBlocker.INSTALLATION_NOT_RETAINED,
            ReleaseBlocker.NAVIGATION_NOT_RETAINED,
            ReleaseBlocker.OFFLINE_VERIFICATION_NOT_RETAINED,
            ReleaseBlocker.ATTRIBUTIONS_NOT_RETAINED,
            ReleaseBlocker.DEVICE_GATE_NOT_RETAINED,
            ReleaseBlocker.CATALOG_LICENSE_NOT_RETAINED,
            ReleaseBlocker.REBOOT_EVIDENCE_NOT_RETAINED,
            ReleaseBlocker.LESSON_CYCLE_NOT_RETAINED,
            ReleaseBlocker.OWNER_RELEASE_DECISION_NOT_RETAINED,
        )
        val packagedBlockers = missingEvidenceBlockers + listOf(ReleaseBlocker.CANDIDATE_NOT_FROZEN) + V2_RELEASE_BLOCKERS
    }
}
