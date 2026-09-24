package dev.russiancompanion.v2.audit

import dev.russiancompanion.release.computeReleaseSourceDigest
import dev.russiancompanion.release.readAcceptanceCandidate
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2SourceCapability
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.review.buildUnitReviewPackage
import dev.russiancompanion.v2.review.curriculumCommitForUnit
import dev.russiancompanion.v2.review.governedMappingFacts
import dev.russiancompanion.v2.review.mappingPackageDigest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class CurriculumReadinessReconciliationTest {
    @Test
    fun committedReportsMatchTheGeneratorAndStayByteIdentical() {
        val model = liveModel()
        val markdown = renderCurriculumReadinessMarkdown(model)
        val jsonText = renderCurriculumReadinessJson(model)
        assertEquals(markdown, renderCurriculumReadinessMarkdown(model))
        assertEquals(jsonText, renderCurriculumReadinessJson(model))
        if (System.getenv("WRITE_CURRICULUM_READINESS") == "1") {
            writeCurriculumReadinessReports(root())
        }
        val markdownFile = File(root(), CURRICULUM_READINESS_MARKDOWN)
        val jsonFile = File(root(), CURRICULUM_READINESS_JSON)
        assertEquals(markdown, markdownFile.readText())
        assertEquals(jsonText, jsonFile.readText())
        val parsed = Json.parseToJsonElement(jsonText).jsonObject
        val markdownRows = markdownTable(markdown, "| Unit number |")
        val jsonRows = parsed.getValue("units").jsonArray.map { it.jsonObject }
        assertEquals(30, markdownRows.size)
        assertEquals((1..30).toList(), markdownRows.map { it.getValue("Unit number").toInt() })
        assertEquals(markdownRows.map { it.getValue("Unit number") }, jsonRows.map { it.getValue("unitNumber").jsonPrimitive.content })
        jsonRows.forEachIndexed { index, jsonRow ->
            CURRICULUM_READINESS_COLUMNS.forEach { (key, label) ->
                assertEquals(jsonRow.getValue(key).jsonPrimitive.content, markdownRows[index].getValue(label))
            }
        }
        val totals = parsed.getValue("totals").jsonObject
        markdownTable(markdown, "| Total |").forEach { row ->
            assertEquals(row.getValue("Value"), totals.getValue(row.getValue("Total")).jsonPrimitive.content)
        }
    }

    @Test
    fun matrixUsesCanonicalDigestsReviewsAndDoesNotInventApproval() {
        val pack = pack()
        val model = liveModel()
        assertEquals(V2Validator.contentReviewDigest(pack), model.packContentReviewDigest)
        assertEquals(computeReleaseSourceDigest(root()), model.repositorySourceDigest)
        assertEquals("ABSENT", model.internetPermission)
        assertEquals(0, model.generatedRussianAdmissionCount)
        assertEquals(0, model.detectedGeneratedRussianItems)
        assertEquals(0, model.penguinProseAdmissionCount)
        assertEquals(0, model.detectedPenguinProseMarkers)
        assertEquals(0, model.packagedAudioBoundToV2Units)
        assertEquals("NOT_APPROVED", model.publicRelease)
        assertFalse(model.candidateFrozen)
        assertEquals("NOT_RECORDED", model.candidateArtifact)
        assertEquals("null", model.candidateSourceCommit)
        assertEquals("MISSING", model.retainedEvidence)
        assertEquals("UNEXECUTED", model.at031)
        assertEquals("NOT_APPROVED", model.releaseDecision)
        assertEquals("PROPOSED_NOT_ACTIVATED", model.varianceProposalStatus)
        assertEquals("PROPOSED_NOT_ACTIVATED", model.varianceResolutionStatus)
        val candidate = readAcceptanceCandidate(root())
        assertEquals(candidate?.frozen, model.candidateFrozen)
        assertEquals(candidate?.publicRelease, model.publicRelease)
        assertEquals(candidate?.sourceCommit, null)
        model.units.forEach { row ->
            val unit = pack.unit(row.unitNumber)!!
            assertEquals(V2Validator.unitContentReviewDigest(pack, row.unitNumber), row.unitContentReviewDigest)
            assertEquals(mappingPackageDigest(governedMappingFacts(row.unitNumber)), row.mappingPackageDigest)
            assertEquals(curriculumCommitForUnit(row.unitNumber), row.curriculumSourceCommit)
            val manifest = File(root(), "review/unit-${row.unitNumber}/UNIT_${row.unitNumber}_REVIEW_MANIFEST.json")
            if (manifest.isFile) {
                assertEquals(buildUnitReviewPackage(pack, row.unitNumber).reviewPackageDigest, row.reviewPackageDigest)
                assertNotEquals("NONE", row.reviewPackageDigest)
            } else {
                assertEquals("NONE", row.reviewPackageDigest)
                assertTrue(row.linguisticReviewBlocker.contains("MISSING_REVIEW_PACKET"))
            }
            assertEquals("PENDING_QUALIFIED_REVIEW", row.unitReviewStatus)
            assertEquals("ABSENT", row.reviewerIdentity)
            assertEquals("ABSENT", row.reviewerQualification)
            assertEquals("ABSENT", row.signedReviewedDigest)
            assertEquals("PENDING_CURRICULUM_REVIEWER", row.mappingStatus)
            assertFalse(row.finalDisposition.contains("review=APPROVED"))
            assertFalse(row.finalDisposition.startsWith("INVALID_INSTALL_OR_READINESS_CLAIM"))
            val errors = V2Validator.contentCompletenessErrors(pack, unit)
            if (row.readiness != "NOT_PREPARED") {
                assertTrue(row.unitNumber.toString() + " " + errors.joinToString("\n"), errors.isEmpty())
                assertEquals("YES", if (row.completeInstructionalChain) "YES" else "NO")
                assertFalse(row.contentBlocker.contains("RECORDED_NOT_PREPARED"))
            }
            if (errors.isNotEmpty()) {
                assertNotEquals("NONE", row.contentBlocker)
                assertFalse(row.completeInstructionalChain)
            }
            if (V2Validator.authorityVarianceApplies(row.unitNumber)) {
                assertEquals("PROPOSED_NOT_ACTIVATED", row.authorityVarianceBlocker)
            } else {
                assertEquals("NOT_APPLICABLE", row.authorityVarianceBlocker)
            }
            if (row.unresolvedRequiredSourceCapabilities != "NONE") {
                assertTrue(row.contentBlocker.contains("UNRESOLVED_REQUIRED_SOURCE_CAPABILITY"))
            }
        }
        assertEquals(listOf(18, 20, 23), model.units.filter { it.reviewPackageDigest == "NONE" }.map { it.unitNumber })
        assertTrue(model.releaseGateBlockers.contains("OWNER_RELEASE_DECISION_NOT_RETAINED"))
        assertTrue(model.releaseGateBlockers.contains("CANDIDATE_NOT_FROZEN"))
    }

    @Test
    fun aMissingManifestIsNotReportedAsAReviewPacket() {
        val hidden = reconcileCurriculumReadiness(root(), pack(), reviewManifests = mapOf(1 to null))
        val unit = hidden.units.first { it.unitNumber == 1 }
        assertEquals("NONE", unit.reviewPackageDigest)
        assertTrue(unit.linguisticReviewBlocker.contains("MISSING_REVIEW_PACKET"))
    }

    @Test
    fun droppedOrDuplicatedUnitsFailClosed() {
        val pack = pack()
        val dropped = pack.copy(units = pack.units.filter { it.number != 30 })
        val duplicated = pack.copy(units = pack.units.dropLast(1) + pack.units.last().copy(number = 29))
        assertTrue(runCatching { reconcileCurriculumReadiness(root(), dropped) }.exceptionOrNull() is IllegalArgumentException)
        assertTrue(runCatching { reconcileCurriculumReadiness(root(), duplicated) }.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun keywordTextBookAttestationAndCountersDoNotClearRealBlockers() {
        val pack = pack()
        val keyword = pack.provenanceRecords.first { it.unitNumber == 26 }.copy(
            itemId = "probe-keyword-indefinite",
            objectiveId = "obj-u26-indefinite",
            originalSourceText = "This keyword mentions indefinite-pronoun without a capability record.",
            shippedText = "indefinite-pronoun",
            sourceHash = "not-the-frozen-hash",
        )
        val keywordPack = pack.copy(provenanceRecords = pack.provenanceRecords + keyword)
        val keywordRow = reconcileCurriculumReadiness(root(), keywordPack).units.first { it.unitNumber == 26 }
        assertTrue(keywordRow.unresolvedRequiredSourceCapabilities.contains("indefinite-pronoun"))
        val blankCapability = V2SourceCapability(
            id = "probe-blank-capability",
            sourceId = "src-wikipedia-russian-grammar-1369617627",
            sourceRevision = "1369617627",
            sourceRecordId = "probe",
            sourceHash = "abc",
            exactSpan = "",
            unitNumber = 26,
            objectiveId = "obj-u26-indefinite",
            supportedClaim = "indefinite-pronoun",
        )
        val capabilityPack = pack.copy(sourceCapabilities = pack.sourceCapabilities + blankCapability)
        val capabilityRow = reconcileCurriculumReadiness(root(), capabilityPack).units.first { it.unitNumber == 26 }
        val liveUnit26 = liveModel().units.first { it.unitNumber == 26 }
        assertTrue(capabilityRow.unresolvedRequiredSourceCapabilities.contains("indefinite-pronoun"))
        assertFalse(capabilityRow.unresolvedRequiredSourceCapabilities.contains("word-order"))
        assertEquals(liveUnit26.resolvedSourceCapabilityCount, capabilityRow.resolvedSourceCapabilityCount)

        val unit = pack.unit(3)!!
        val bookOnly = unit.copy(
            readiness = "CONTENT_COMPLETE_REVIEW_PENDING",
            objectives = unit.objectives.filter { it.kind == "SELF_ATTESTATION" },
            sessions = unit.sessions.map { session ->
                session.copy(steps = session.steps.filter { it.kind == "BOOK" })
            }.filter { it.steps.isNotEmpty() },
        )
        val bookRow = reconcileCurriculumReadiness(root(), pack.withUnit(bookOnly)).units.first { it.unitNumber == 3 }
        assertTrue(bookRow.contentBlocker.contains("MISSING_INSTRUCTIONAL_STRAND"))
        assertFalse(bookRow.completeInstructionalChain)
        assertFalse(bookRow.advancementCanPresentlySucceed)

        val inflated = pack.copy(
            admissions = pack.admissions.copy(mappedPronunciationAudio = 40, handwritingAssets = 8, catalogAudioBoundToUnits = 0),
        )
        val inflatedModel = reconcileCurriculumReadiness(root(), inflated)
        val unit1 = inflatedModel.units.first { it.unitNumber == 1 }
        val unit2 = inflatedModel.units.first { it.unitNumber == 2 }
        assertEquals(0, unit1.mappedAudioCount)
        assertEquals(0, unit2.handwritingAssetCount)
        assertTrue(unit1.assetBlocker.contains("REQUIRED_LICENSED_AUDIO_ABSENT"))
        assertTrue(unit2.assetBlocker.contains("REQUIRED_HANDWRITING_ASSET_ABSENT"))
        assertEquals(0, inflatedModel.packagedAudioBoundToV2Units)
    }

    @Test
    fun unsupportedOrUntaughtObjectivesCannotAdvanceAndInstallClaimsStayUnapproved() {
        val pack = pack()
        val unit = pack.unit(1)!!
        val guidance = unit.objectives.first { it.kind == "GUIDANCE" }
        val check = unit.objectives.first { it.kind == "RECOGNITION" && it.checkItemIds.isNotEmpty() }.checkItemIds.first()
        val withUnsupported = unit.copy(
            objectives = unit.objectives.map { objective ->
                if (objective.id == guidance.id) objective.copy(checkItemIds = listOf(check)) else objective
            },
        )
        val unsupported = reconcileCurriculumReadiness(root(), pack.withUnit(withUnsupported)).units.first()
        assertTrue(unsupported.contentBlocker.contains("UNSUPPORTED_OBJECTIVE_HAS_ADVANCEMENT_CHECK"))
        assertFalse(unsupported.advancementCanPresentlySucceed)

        val recognition = unit.objectives.first { it.kind == "RECOGNITION" && it.gating }
        val untaughtUnit = unit.copy(
            objectives = unit.objectives.map { objective ->
                if (objective.id == recognition.id) objective.copy(instructionStepIds = emptyList()) else objective
            },
        )
        val untaught = reconcileCurriculumReadiness(root(), pack.withUnit(untaughtUnit)).units.first()
        assertTrue(untaught.contentBlocker.contains("UNTAUGHT_OBJECTIVE"))
        assertTrue(untaught.contentBlocker.contains("MISSING_INSTRUCTION"))
        assertFalse(untaught.advancementCanPresentlySucceed)

        val installed = unit.copy(installState = "INSTALLED", readiness = "INSTALLABLE", mappingStatus = "APPROVED")
        val claimed = reconcileCurriculumReadiness(root(), pack.withUnit(installed)).units.first()
        assertTrue(claimed.finalDisposition.startsWith("INVALID_INSTALL_OR_READINESS_CLAIM"))
        assertEquals("PENDING_QUALIFIED_REVIEW", claimed.unitReviewStatus)
        assertEquals("ABSENT", claimed.signedReviewedDigest)
        assertNotEquals("APPROVED", claimed.unitReviewStatus)
    }

    private fun liveModel() = reconcileCurriculumReadiness(root(), pack())

    private fun pack() = loadCurriculumPack(root())

    private fun root(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }

    private fun markdownTable(markdown: String, headerPrefix: String): List<Map<String, String>> {
        val lines = markdown.lines()
        val start = lines.indexOfFirst { it.startsWith(headerPrefix) }
        val header = lines[start].trim('|').split('|').map { it.trim() }
        return lines.drop(start + 2).takeWhile { it.startsWith("|") }.map { line ->
            header.zip(line.trim('|').split('|').map { it.trim() }).toMap()
        }
    }

    private fun V2CurriculumPack.withUnit(unit: dev.russiancompanion.v2.curriculum.V2Unit) =
        copy(units = units.map { if (it.number == unit.number) unit else it })

    private fun V2CurriculumPack.unit(number: Int) = units.first { it.number == number }
}
