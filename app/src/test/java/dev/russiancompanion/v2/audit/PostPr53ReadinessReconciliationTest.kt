package dev.russiancompanion.v2.audit

import dev.russiancompanion.release.readAcceptanceCandidate
import dev.russiancompanion.v2.curriculum.BatchARemainingSources
import dev.russiancompanion.v2.curriculum.BatchBSources
import dev.russiancompanion.v2.curriculum.V2Validator
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Locks the post-PR-53 diagnosis to the packaged curriculum.
 * A stale unresolved claim, a silently cleared claim, a report that
 * drifts from the live reconciler, or an unexpected path after the
 * recorded content commit fails this test.
 */
class PostPr53ReadinessReconciliationTest {
    @Test
    fun reportMatrixMatchesTheLiveCurriculum() {
        val root = root()
        val pack = loadCurriculumPack(root)
        val live = reconcileCurriculumReadiness(root, pack)
        val report = Json.parseToJsonElement(reportFile(root, "json").readText()).jsonObject
        val markdown = reportFile(root, "md").readText()

        assertEquals("cursor/post-pr53-readiness-reconciliation-964f", report.getValue("branch").jsonPrimitive.content)
        assertEquals(BASE_COMMIT, report.getValue("baseCommit").jsonPrimitive.content)
        assertFalse(report.containsKey("headCommit"))
        val recorded = report.getValue("reportContentCommit").jsonPrimitive.content
        assertEquals(REPORT_CONTENT_COMMIT, recorded)
        val allowlist = report.getValue("postReportAllowlist").jsonArray.map { it.jsonPrimitive.content }
        assertEquals(POST_REPORT_ALLOWLIST, allowlist)
        val branchHead = reconciliationBranchHead(root)
        assertTrue("recorded=$recorded branchHead=$branchHead", recorded != branchHead)
        assertEquals(0, gitExit(root, "merge-base", "--is-ancestor", recorded, branchHead))
        assertEquals(0, gitExit(root, "merge-base", "--is-ancestor", BASE_COMMIT, branchHead))
        val secondParent = gitOrNull(root, "rev-parse", "--verify", "HEAD^2")
        if (secondParent != null) {
            assertEquals(secondParent, branchHead)
            assertTrue(secondParent != git(root, "rev-parse", "HEAD^"))
        }
        val entries = postReportNameStatus(root, recorded, branchHead)
        assertTrue(
            "post-report entries=$entries",
            postReportStatusesAllowed(entries, POST_REPORT_ALLOWLIST),
        )
        val recordedReport = Json.parseToJsonElement(
            git(root, "show", "$recorded:audit/post-pr53-readiness/POST_PR53_CURRICULUM_READINESS_RECONCILIATION.json"),
        ).jsonObject
        assertEquals("dc98eeba48ffbbeb17ce835f3700bde158d4a4d3", recordedReport.getValue("headCommit").jsonPrimitive.content)
        assertFalse(recordedReport.containsKey("reportContentCommit"))
        assertFalse(recordedReport.getValue("localGates").jsonObject.containsKey("checkoutCorrectionRerun"))
        assertEquals(curriculumPayload(recordedReport), curriculumPayload(report))
        assertEquals(
            "testDebugUnitTest 434 tests, 0 failures, 1 skipped; assembleDebug, lintDebug, verifyReleaseGateAttachment BUILD SUCCESSFUL; assembleRelease FAILED at checkReleaseGate; no release APK",
            report.getValue("localGates").jsonObject.getValue("checkoutCorrectionRerun").jsonPrimitive.content,
        )
        val recordedMarkdown = git(root, "show", "$recorded:audit/post-pr53-readiness/POST_PR53_CURRICULUM_READINESS_RECONCILIATION.md")
        val body = "## 2. Units"
        val lock = "## 12. Regression lock"
        assertEquals(
            recordedMarkdown.substring(recordedMarkdown.indexOf(body), recordedMarkdown.indexOf(lock)),
            markdown.substring(markdown.indexOf(body), markdown.indexOf(lock)),
        )
        POST_REPORT_ALLOWLIST.forEach { path -> assertTrue(markdown.contains(path)) }
        assertTrue(markdown.contains("HEAD^2"))
        assertTrue(markdown.contains(recorded))
        assertTrue(markdown.contains(report.getValue("draftPullRequestUrl").jsonPrimitive.content))

        assertEquals(live.packContentReviewDigest, report.getValue("packContentReviewDigest").jsonPrimitive.content)
        assertEquals(live.repositorySourceDigest, report.getValue("repositorySourceDigest").jsonPrimitive.content)
        assertEquals("NOT_APPROVED", live.publicRelease)
        assertEquals("NOT_APPROVED", report.getValue("publicRelease").jsonPrimitive.content)
        assertFalse(live.candidateFrozen)
        assertFalse(report.getValue("candidateFrozen").jsonPrimitive.boolean)
        assertEquals("null", live.candidateSourceCommit)
        assertTrue(report.getValue("candidateSourceCommit") is JsonNull)
        assertEquals("NOT_RECORDED", live.candidateArtifact)
        assertEquals("NOT_RECORDED", report.getValue("candidateArtifact").jsonPrimitive.content)
        assertEquals("UNEXECUTED", live.at031)
        assertEquals("UNEXECUTED", report.getValue("at031").jsonPrimitive.content)
        assertEquals("MISSING", live.retainedEvidence)

        val candidateText = File(root, "release/acceptance/candidate.json").readText()
        val candidate = readAcceptanceCandidate(root)!!
        assertEquals(false, candidate.frozen)
        assertEquals(null, candidate.sourceCommit)
        assertEquals("NOT_APPROVED", candidate.publicRelease)
        assertEquals("NOT_RECORDED", candidate.artifact?.status)
        assertEquals(live.repositorySourceDigest, candidate.sourceDigest)
        assertTrue(candidateText.contains("\"AT-031\""))
        assertEquals(
            "0bd4dbf5a24738b9ff9bb7e782a1370021ea59b065c9e764c556f16c1e0784b4",
            report.getValue("preservedFailedCandidateSourceDigest").jsonPrimitive.content,
        )
        assertEquals(
            "7a24899055474d1d7913213b0af1dedade528d4015cd1faef35c0f71d4e8c37e",
            report.getValue("preservedFrozenCandidateSourceDigest").jsonPrimitive.content,
        )
        assertTrue(candidateText.contains(report.getValue("preservedFailedCandidateSourceDigest").jsonPrimitive.content))
        assertTrue(candidateText.contains(report.getValue("preservedFrozenCandidateSourceDigest").jsonPrimitive.content))

        assertEquals(0, pack.ownedEditionMappings.size)
        assertEquals(0, report.getValue("ownedEditionMappingCount").jsonPrimitive.content.toInt())
        assertEquals(0, report.getValue("approvedOwnedEditionMappingCount").jsonPrimitive.content.toInt())
        assertEquals(30, report.getValue("mappingWorksheetCount").jsonPrimitive.content.toInt())
        assertEquals(30, report.getValue("reviewManifestCount").jsonPrimitive.content.toInt())
        assertEquals(0, report.getValue("unitsLackingReviewPacket").jsonPrimitive.content.toInt())
        assertTrue(live.units.none { it.reviewPackageDigest == "NONE" })
        assertTrue(pack.ownedEditionMappings.none { it.status == "APPROVED" })

        val rows = report.getValue("units").jsonArray.map { it.jsonObject }
        assertEquals(30, rows.size)
        assertEquals(live.units.map { it.unitNumber }, rows.map { it.getValue("unitNumber").jsonPrimitive.content.toInt() })
        rows.forEach { row ->
            val unit = live.units.first { it.unitNumber == row.getValue("unitNumber").jsonPrimitive.content.toInt() }
            assertEquals(unit.title, row.getValue("title").jsonPrimitive.content)
            assertEquals(unit.installState, row.getValue("installState").jsonPrimitive.content)
            assertEquals("DEVELOPMENT", unit.installState)
            assertEquals(unit.readiness, row.getValue("readiness").jsonPrimitive.content)
            assertEquals(if (unit.advancementCanPresentlySucceed) "YES" else "NO", row.getValue("advancementCanPresentlySucceed").jsonPrimitive.content)
            assertEquals(unit.unresolvedRequiredSourceCapabilities, row.getValue("unresolvedRequiredSourceCapabilities").jsonPrimitive.content)
            assertEquals(unit.contentBlocker, row.getValue("contentBlocker").jsonPrimitive.content)
            assertEquals(unit.unitReviewStatus, row.getValue("linguisticReviewStatus").jsonPrimitive.content)
            assertEquals("PENDING_QUALIFIED_REVIEW", unit.unitReviewStatus)
            assertEquals(unit.linguisticReviewBlocker, row.getValue("linguisticReviewBlocker").jsonPrimitive.content)
            assertEquals(unit.mappingStatus, row.getValue("mappingStatus").jsonPrimitive.content)
            assertEquals("PENDING_CURRICULUM_REVIEWER", unit.mappingStatus)
            assertEquals(unit.mappingBlocker, row.getValue("mappingBlocker").jsonPrimitive.content)
            assertFalse(unit.mappingBlocker.contains("MISSING_MAPPING"))
            assertEquals(unit.assetBlocker, row.getValue("assetBlocker").jsonPrimitive.content)
            assertEquals(unit.authorityVarianceBlocker, row.getValue("authorityVarianceBlocker").jsonPrimitive.content)
            assertEquals("PRESENT_UNSIGNED", row.getValue("reviewPacket").jsonPrimitive.content)
            assertEquals(unit.reviewPackageDigest, row.getValue("reviewPackageDigest").jsonPrimitive.content)
            val manifest = File(root, "review/unit-${unit.unitNumber}/UNIT_${unit.unitNumber}_REVIEW_MANIFEST.json")
            val worksheet = File(root, "review/unit-${unit.unitNumber}/UNIT_${unit.unitNumber}_OWNED_EDITION_MAPPING_WORKSHEET.tsv")
            assertTrue(manifest.isFile)
            assertTrue(worksheet.isFile)
            assertFalse(worksheet.readText().contains("\tAPPROVED\t"))
        }

        val markdownRows = markdownTable(markdown, "| Unit | Install state |")
        assertEquals(rows.map { it.getValue("unitNumber").jsonPrimitive.content }, markdownRows.map { it.getValue("Unit") })
        markdownRows.forEachIndexed { index, markdownRow ->
            val row = rows[index]
            assertEquals(row.getValue("installState").jsonPrimitive.content, markdownRow.getValue("Install state"))
            assertEquals(row.getValue("readiness").jsonPrimitive.content, markdownRow.getValue("Readiness"))
            assertEquals(row.getValue("advancementCanPresentlySucceed").jsonPrimitive.content, markdownRow.getValue("Advancement"))
            assertEquals(row.getValue("unresolvedRequiredSourceCapabilities").jsonPrimitive.content, markdownRow.getValue("Unresolved required source capabilities"))
            assertEquals(row.getValue("contentBlocker").jsonPrimitive.content, markdownRow.getValue("Other content blockers"))
            assertEquals(row.getValue("linguisticReviewStatus").jsonPrimitive.content, markdownRow.getValue("Linguistic-review status"))
            assertEquals(row.getValue("mappingStatus").jsonPrimitive.content, markdownRow.getValue("Owned-edition mapping status"))
            assertEquals(row.getValue("assetBlocker").jsonPrimitive.content, markdownRow.getValue("Asset blockers"))
            assertEquals(row.getValue("authorityVarianceBlocker").jsonPrimitive.content, markdownRow.getValue("Authority variance"))
        }

        val unresolved = live.units.filter { it.unresolvedRequiredSourceCapabilities != "NONE" }
        assertEquals(listOf(7, 23, 25, 26, 27, 29, 30), unresolved.map { it.unitNumber })
        assertEquals(
            listOf(
                "adverb-paradigm",
                "completion-over-period",
                "productive-diminutive",
                "postal-address",
                "participle-stress, participle-comprehension",
                "bookish-register, bookish-reading",
                "dictionary-use",
            ),
            unresolved.map { it.unresolvedRequiredSourceCapabilities },
        )
        assertEquals(23, live.units.count { it.readiness == "CONTENT_COMPLETE_REVIEW_PENDING" })
        assertEquals(7, live.units.count { it.readiness == "NOT_PREPARED" })
        assertEquals(listOf(1, 2), live.units.filter { it.assetBlocker != "NONE" }.map { it.unitNumber })
        assertEquals(listOf(27, 29, 30), live.units.filter { it.authorityVarianceBlocker != "NOT_APPLICABLE" }.map { it.unitNumber })
        assertEquals(
            listOf(3, 4, 5, 6, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 24, 28),
            live.units.filter {
                it.readiness == "CONTENT_COMPLETE_REVIEW_PENDING" &&
                    it.unresolvedRequiredSourceCapabilities == "NONE" &&
                    it.assetBlocker == "NONE" &&
                    it.authorityVarianceBlocker == "NOT_APPLICABLE" &&
                    it.advancementCanPresentlySucceed
            }.map { it.unitNumber },
        )
        STALE_RESOLVED_CLAIMS.forEach { claim ->
            assertFalse(live.units.any { claim in it.unresolvedRequiredSourceCapabilities.split(", ") })
        }

        val named = report.getValue("namedClaims").jsonArray.map { it.jsonObject }
        val dispositions = markdownTable(markdown, "| Claim | Unit | Objective | Disposition |")
        assertEquals(named.map { it.getValue("claim").jsonPrimitive.content }, dispositions.map { it.getValue("Claim") })
        named.forEach { claim ->
            val unitNumber = claim.getValue("unit").jsonPrimitive.content.toInt()
            val objectiveId = claim.getValue("objectiveId").jsonPrimitive.content
            val name = claim.getValue("claim").jsonPrimitive.content
            val disposition = claim.getValue("disposition").jsonPrimitive.content
            val liveClaims = V2Validator.unresolvedRequiredSourceCapabilities(pack, unitNumber).map { it.claim }
            val markdownRow = dispositions.first { it.getValue("Claim") == name }
            assertEquals(unitNumber.toString(), markdownRow.getValue("Unit"))
            assertEquals(objectiveId, markdownRow.getValue("Objective"))
            assertEquals(disposition, markdownRow.getValue("Disposition"))
            val shipped = pack.sourceCapabilities.any {
                it.unitNumber == unitNumber && it.objectiveId == objectiveId && it.supportedClaim == name
            }
            when (disposition) {
                "RESOLVED" -> {
                    assertFalse(liveClaims.contains(name))
                    assertTrue(shipped)
                }
                "UNRESOLVED" -> {
                    assertTrue(liveClaims.contains(name))
                    assertFalse(shipped)
                }
                else -> error("Named claim $name has an unexpected disposition $disposition")
            }
        }
        val judgments = report.getValue("unresolvedCapabilityJudgments").jsonArray.map { it.jsonObject }
        assertEquals(
            named.filter { it.getValue("disposition").jsonPrimitive.content == "UNRESOLVED" }.map { it.getValue("claim").jsonPrimitive.content },
            judgments.map { it.getValue("claim").jsonPrimitive.content },
        )
        judgments.forEach { judgment ->
            val claim = judgment.getValue("claim").jsonPrimitive.content
            assertTrue(markdown.contains("### $claim"))
            val research = judgment.getValue("additionalLawfulResearchPlausible").jsonPrimitive.boolean
            val planning = judgment.getValue("planning").jsonPrimitive.content
            if (claim == "bookish-register") {
                assertFalse(research)
                assertEquals("DEFER_AND_AUTHORITY_DECISION", planning)
            } else {
                assertTrue(research)
                assertEquals("DEFER", planning)
            }
        }

        assertTrue(BatchARemainingSources.ADMITTED_ADVERB_PARADIGM.isEmpty())
        assertTrue(BatchBSources.ADMITTED_COMPLETION_OVER_PERIOD.isEmpty())
        assertFalse(BatchBSources.ADMITTED_PRODUCTIVE_DIMINUTIVE.isEmpty())
        assertFalse(BatchBSources.clearsProductiveDiminutive("any span"))
        assertTrue(V2Validator.ADMITTED_PARTICIPLE_COMPREHENSION.isEmpty())
        assertTrue(V2Validator.ADMITTED_BOOKISH_READING.isEmpty())
        assertFalse(V2Validator.ADMITTED_VERBAL_ADVERB_READING.isEmpty())
        assertEquals(setOf(27, 29, 30), (1..30).filter { V2Validator.authorityVarianceApplies(it) }.toSet())
    }

    @Test
    fun unexpectedFileBetweenRecordedContentCommitAndBranchHeadFailsTheLock() {
        val allowed = POST_REPORT_ALLOWLIST.map { "M" to it }
        assertTrue(postReportStatusesAllowed(allowed, POST_REPORT_ALLOWLIST))
        assertFalse(
            postReportStatusesAllowed(
                allowed + ("M" to "app/src/main/java/dev/russiancompanion/v2/curriculum/V2Validator.kt"),
                POST_REPORT_ALLOWLIST,
            ),
        )
        assertFalse(
            postReportStatusesAllowed(
                allowed + ("A" to "app/src/main/assets/content/v2-curriculum.json"),
                POST_REPORT_ALLOWLIST,
            ),
        )
        assertFalse(
            postReportStatusesAllowed(
                allowed + ("M" to "catalog/build_v2_curriculum.py"),
                POST_REPORT_ALLOWLIST,
            ),
        )
        assertFalse(
            postReportStatusesAllowed(
                allowed + ("M" to "audit/post-pr53-readiness/UNEXPECTED.md"),
                POST_REPORT_ALLOWLIST,
            ),
        )
        assertFalse(postReportStatusesAllowed(emptyList(), POST_REPORT_ALLOWLIST))
        assertFalse(
            postReportStatusesAllowed(
                listOf("D" to POST_REPORT_ALLOWLIST.first()),
                POST_REPORT_ALLOWLIST,
            ),
        )
    }

    @Test
    fun syntheticMergeValidatesTheSecondParentRatherThanItsParent() {
        val directory = File("build/post-pr53-synthetic-merge").apply {
            deleteRecursively()
            mkdirs()
        }
        try {
            git(directory, "init", "-b", "main")
            git(directory, "config", "user.email", "audit@example.com")
            git(directory, "config", "user.name", "audit")
            File(directory, "README").writeText("base\n")
            git(directory, "add", "README")
            git(directory, "-c", "commit.gpgsign=false", "commit", "-m", "base")
            git(directory, "checkout", "-b", "pr")
            File(directory, "report.txt").writeText("recorded\n")
            git(directory, "add", "report.txt")
            git(directory, "-c", "commit.gpgsign=false", "commit", "-m", "recorded")
            val recorded = git(directory, "rev-parse", "HEAD")
            File(directory, "app/src/main").mkdirs()
            File(directory, "app/src/main/Evil.kt").writeText("change\n")
            git(directory, "add", "app/src/main/Evil.kt")
            git(directory, "-c", "commit.gpgsign=false", "commit", "-m", "unexpected production change")
            val pullRequestHead = git(directory, "rev-parse", "HEAD")
            git(directory, "checkout", "main")
            git(directory, "-c", "commit.gpgsign=false", "merge", "--no-ff", "pr", "-m", "merge")
            val branchHead = reconciliationBranchHead(directory)
            assertEquals(pullRequestHead, branchHead)
            assertEquals(pullRequestHead, git(directory, "rev-parse", "HEAD^2"))
            assertEquals(recorded, git(directory, "rev-parse", "HEAD^2^"))
            val againstBranchHead = postReportNameStatus(directory, recorded, branchHead)
            assertEquals(listOf("A" to "app/src/main/Evil.kt"), againstBranchHead)
            assertFalse(postReportStatusesAllowed(againstBranchHead, POST_REPORT_ALLOWLIST))
            assertTrue(postReportNameStatus(directory, recorded, git(directory, "rev-parse", "HEAD^2^")).isEmpty())
        } finally {
            directory.deleteRecursively()
        }
    }

    private fun reportFile(root: File, extension: String): File {
        return File(root, "audit/post-pr53-readiness/POST_PR53_CURRICULUM_READINESS_RECONCILIATION.$extension")
    }

    private fun root(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }

    private fun reconciliationBranchHead(root: File): String {
        return gitOrNull(root, "rev-parse", "--verify", "HEAD^2")
            ?: git(root, "rev-parse", "HEAD")
    }

    private fun postReportNameStatus(root: File, from: String, to: String): List<Pair<String, String>> {
        val text = git(root, "diff", "--name-status", "--no-renames", from, to)
        if (text.isEmpty()) return emptyList()
        return text.lines().filter { it.isNotBlank() }.map { line ->
            val parts = line.split('\t')
            assertEquals(line, 2, parts.size)
            parts[0] to parts[1]
        }.sortedBy { it.second }
    }

    private fun postReportStatusesAllowed(
        entries: List<Pair<String, String>>,
        allowlist: List<String>,
    ): Boolean {
        if (entries.any { it.first != "M" }) return false
        return entries.map { it.second } == allowlist.sorted()
    }

    private fun curriculumPayload(report: JsonObject): JsonObject {
        val top = report.filterKeys { it !in IDENTITY_KEYS }
        val gates = top.getValue("localGates").jsonObject
        val strippedGates = JsonObject(gates.filterKeys { it != "checkoutCorrectionRerun" })
        return JsonObject(top + ("localGates" to strippedGates))
    }

    private fun git(root: File, vararg args: String): String {
        val text = gitOrNull(root, *args)
        assertTrue(args.joinToString(" "), text != null)
        return text!!
    }

    private fun gitOrNull(root: File, vararg args: String): String? {
        val process = ProcessBuilder(listOf("git", "-C", root.absolutePath) + args)
            .redirectErrorStream(true)
            .start()
        val text = process.inputStream.bufferedReader().readText().trim()
        return if (process.waitFor() == 0) text else null
    }

    private fun gitExit(root: File, vararg args: String): Int {
        val process = ProcessBuilder(listOf("git", "-C", root.absolutePath) + args)
            .redirectErrorStream(true)
            .start()
        process.inputStream.bufferedReader().readText()
        return process.waitFor()
    }

    private fun markdownTable(markdown: String, headerPrefix: String): List<Map<String, String>> {
        val lines = markdown.lines()
        val start = lines.indexOfFirst { it.startsWith(headerPrefix) }
        assertTrue(start >= 0)
        val header = lines[start].trim('|').split('|').map { it.trim() }
        return lines.drop(start + 2).takeWhile { it.startsWith("|") }.map { line ->
            header.zip(line.trim('|').split('|').map { it.trim() }).toMap()
        }
    }

    private companion object {
        const val BASE_COMMIT = "6457e690776ff12349d55f0d1ffe03a95626ea23"
        const val REPORT_CONTENT_COMMIT = "ad813aa42e5172f3c993f5581b22782a0e0327c5"
        val POST_REPORT_ALLOWLIST = listOf(
            "app/src/test/java/dev/russiancompanion/v2/audit/PostPr53ReadinessReconciliationTest.kt",
            "audit/post-pr53-readiness/POST_PR53_CURRICULUM_READINESS_RECONCILIATION.json",
            "audit/post-pr53-readiness/POST_PR53_CURRICULUM_READINESS_RECONCILIATION.md",
        )
        val IDENTITY_KEYS = setOf("headCommit", "reportContentCommit", "postReportAllowlist")
        val STALE_RESOLVED_CLAIMS = listOf(
            "productive-clock-time",
            "negation-scope",
            "verbal-adverb-reading",
            "capstone-reading",
            "letter-writing",
            "correspondence-work",
            "diminutive-form-classification",
            "elapsed-time-duration",
            "adverb-form-gloss",
            "adverb-invariance",
            "adverb-bounded-pairs",
            "passive-participle",
            "active-participle",
            "participle-punctuation",
            "short-form-adjective",
            "abbreviation",
            "letter-name-review",
            "particle",
        )
    }
}
