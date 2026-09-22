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
        assertEquals(packagedBlockers, assessment.blockers)
        assertTrue(assessment.blockers.contains(ReleaseBlocker.PATHWAY_INCOMPLETE))
        assertTrue(assessment.blockers.contains(ReleaseBlocker.SHARE_ALIKE_UNCONFIRMED))
        assertFalse(assessment.blockers.contains(ReleaseBlocker.EVIDENCE_MANIFEST_UNREADABLE))
        assertEquals(AcceptanceDisposition.OUTSTANDING, ACCEPTANCE_DISPOSITION.getValue("AT-001"))
    }

    @Test
    fun packagedPlaceholderStaysNotApprovedWithShareAlikeAndRetainedEvidence() {
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
        assertEquals(ReleaseDecision.NOT_APPROVED, assessment.decision)
        assertEquals(listOf(ReleaseBlocker.PATHWAY_INCOMPLETE), assessment.blockers)
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
        val emptyPathway = complete.copy(
            courseSegments = emptyList(),
            planDefinitions = emptyList(),
            gateDefinitions = emptyList(),
        )
        for (catalog in listOf(missingLesson, missingListen, missingGate)) {
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
        val missingEvidenceBlockers = listOf(
            ReleaseBlocker.OFFLINE_VERIFICATION_NOT_RETAINED,
            ReleaseBlocker.REBOOT_EVIDENCE_NOT_RETAINED,
            ReleaseBlocker.LESSON_CYCLE_NOT_RETAINED,
            ReleaseBlocker.OWNER_RELEASE_DECISION_NOT_RETAINED,
        )
        val packagedBlockers = listOf(
            ReleaseBlocker.PATHWAY_INCOMPLETE,
            ReleaseBlocker.SHARE_ALIKE_UNCONFIRMED,
        ) + missingEvidenceBlockers
    }
}
