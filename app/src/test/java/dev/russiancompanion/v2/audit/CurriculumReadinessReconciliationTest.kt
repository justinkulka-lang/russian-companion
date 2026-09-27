package dev.russiancompanion.v2.audit

import dev.russiancompanion.release.computeReleaseSourceDigest
import dev.russiancompanion.release.readAcceptanceCandidate
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2SourceCapability
import dev.russiancompanion.v2.curriculum.V2Step
import dev.russiancompanion.v2.curriculum.V2Unit
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.advancementDecision
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
        assertEquals("ABSENT", model.internetPermissionDeclaredInMainManifest)
        assertEquals("ABSENT", model.internetPermissionDeclaredInMergedDebugManifest)
        assertEquals(0, model.generatedRussianAdmissionCount)
        assertEquals(0, model.itemsMarkedContainsGeneratedRussian)
        assertEquals(0, model.penguinProseAdmissionCount)
        assertEquals(0, model.knownPenguinTitleMarkerMatches)
        assertTrue(model.packagedExampleRecords == model.reachableExampleRecords + model.orphanedExampleRecords)
        assertTrue(model.packagedCheckItems == model.reachableCheckItems + model.orphanedCheckItems)
        assertEquals("ex-u4-verb-знать, ex-u4-verb-работать", model.orphanedExampleIds)
        assertEquals("NONE", model.orphanedCheckItemIds)
        assertEquals(2, model.orphanedExampleRecords)
        assertEquals(0, model.orphanedCheckItems)
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
        val unit7 = model.units.first { it.unitNumber == 7 }
        assertEquals("NOT_PREPARED", unit7.readiness)
        assertFalse(unit7.advancementCanPresentlySucceed)
        assertFalse(unit7.finalDisposition.contains("INVALID_ADVANCEMENT_READINESS_INCONSISTENCY"))
        assertTrue((16..30).all { number -> model.units.first { it.unitNumber == number }.assetBlocker == "NONE" })
        listOf(16, 18, 20).forEach { number ->
            assertFalse(
                model.units.first { it.unitNumber == number }.contentBlocker.contains("MISSING_SOURCE_SUPPORTED_CURRICULUM"),
            )
        }
        (listOf(7, 17, 23) + (25..30).toList()).forEach { number ->
            assertTrue(
                model.units.first { it.unitNumber == number }.contentBlocker.contains("MISSING_SOURCE_SUPPORTED_CURRICULUM"),
            )
        }
        assertTrue(model.units.all { it.localExperienceBlocker == "NONE" })
        assertEquals(listOf(1, 2), model.units.filter { it.assetBlocker != "NONE" }.map { it.unitNumber })
        assertEquals(emptyList<Int>(), model.units.filter { it.reviewPackageDigest == "NONE" }.map { it.unitNumber })
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

    @Test
    fun notPreparedUnitsCannotAdvanceAndReadinessChangeDoesNotClearOtherBlockers() {
        val pack = pack()
        listOf(17, 30).forEach { number ->
            val unit = pack.unit(number)
            val decision = advancementDecision(pack, unit, idealizedLearner(pack, unit))
            assertFalse(decision.allowed)
            assertTrue(decision.remaining.any { it == "This unit is not prepared" })
            val row = liveModel().units.first { it.unitNumber == number }
            assertFalse(row.advancementCanPresentlySucceed)
        }
        val early = pack.unit(1).copy(readiness = "NOT_PREPARED")
        val earlyDecision = advancementDecision(pack, early, idealizedLearner(pack, early))
        assertFalse(earlyDecision.allowed)
        assertTrue(earlyDecision.remaining.any { it == "This unit is not prepared" })

        val preparedSixteen = pack.unit(16).copy(readiness = "CONTENT_COMPLETE_REVIEW_PENDING")
        val preparedDecision = advancementDecision(pack, preparedSixteen, idealizedLearner(pack, preparedSixteen))
        assertFalse(preparedDecision.remaining.any { it == "This unit is not prepared" })
        assertTrue(preparedDecision.remaining.joinToString("\n"), preparedDecision.allowed)

        val late = pack.unit(27).copy(readiness = "CONTENT_COMPLETE_REVIEW_PENDING")
        val lateDecision = advancementDecision(pack, late, idealizedLearner(pack, late))
        assertFalse(lateDecision.allowed)
        assertFalse(lateDecision.remaining.any { it == "This unit is not prepared" })
        assertTrue(lateDecision.remaining.any { it == "Unit 27 is blocked by an unresolved authority variance" })
        assertTrue(lateDecision.remaining.any { it.contains("participle-stress") })

        val missingChecks = pack.unit(1)
        val starved = idealizedLearner(pack, missingChecks).copy(correctItemIds = emptySet())
        val starvedDecision = advancementDecision(
            pack,
            missingChecks.copy(readiness = "INSTALLABLE"),
            starved,
        )
        assertFalse(starvedDecision.allowed)
        assertTrue(starvedDecision.remaining.any { it.contains("distinct in-app checks") || it.contains("Demonstrate") })

        val inconsistent = reconcileCurriculumReadiness(
            root(),
            pack,
            runtimeAdvancementAllowed = { _, unit -> unit.number == 17 },
        ).units.first { it.unitNumber == 17 }
        assertFalse(inconsistent.advancementCanPresentlySucceed)
        assertTrue(inconsistent.finalDisposition.startsWith("INVALID_ADVANCEMENT_READINESS_INCONSISTENCY"))
        val closed = reportAdvancement("NOT_PREPARED", runtimeAllowed = false)
        assertFalse(closed.canPresentlySucceed)
        assertFalse(closed.readinessInconsistent)
        val open = reportAdvancement("CONTENT_COMPLETE_REVIEW_PENDING", runtimeAllowed = true)
        assertTrue(open.canPresentlySucceed)
        assertFalse(open.readinessInconsistent)
    }

    @Test
    fun narrowMarkerChecksDoNotClaimComprehensiveInspection() {
        val pack = pack()
        val flagged = pack.items.first().copy(id = "probe-generated-flag", containsGeneratedRussian = true)
        val unmarkedRussian = pack.items.first().copy(
            id = "probe-unmarked-russian",
            containsGeneratedRussian = false,
            prompt = "это не flagged generated Russian",
            answer = "привет",
        )
        val flaggedPack = pack.copy(items = pack.items + flagged + unmarkedRussian)
        assertEquals(1, itemsMarkedContainsGeneratedRussian(flaggedPack))
        assertEquals(0, itemsMarkedContainsGeneratedRussian(pack))
        assertEquals(0, flaggedPack.admissions.generatedRussianWords)

        val marked = pack.unit(3)
        val penguinStep = marked.sessions.first().steps.first().copy(
            id = "probe-penguin-title",
            body = "See the new penguin course title marker.",
        )
        val otherPenguin = marked.sessions.first().steps.first().copy(
            id = "probe-penguin-books",
            body = "Penguin Books is not the known title marker.",
        )
        assertEquals(1, knownPenguinTitleMarkerMatches(pack.withUnit(marked.withAddedStep(penguinStep))))
        assertEquals(0, knownPenguinTitleMarkerMatches(pack.withUnit(marked.withAddedStep(otherPenguin))))
        assertEquals(0, knownPenguinTitleMarkerMatches(pack))

        val markdown = renderCurriculumReadinessMarkdown(liveModel())
        assertTrue(markdown.contains("not comprehensive authorship, copyright, or merged-manifest analysis"))
        assertEquals("ABSENT", internetPermissionDeclaredInManifestText(File(root(), "app/src/main/AndroidManifest.xml").readText()))
        assertEquals("ABSENT", internetPermissionDeclaredInManifestText("<uses-permission android:name=\"android.permission.RECORD_AUDIO\" />"))
        assertEquals("PRESENT", internetPermissionDeclaredInManifestText("<uses-permission android:name=\"android.permission.INTERNET\" />"))
        val merged = mergedDebugManifestFile(root())
        assertTrue(merged.isFile)
        assertEquals("ABSENT", internetPermissionDeclaredInManifestText(merged.readText()))
        assertFalse(merged.readText().contains("android.permission.INTERNET"))
    }

    @Test
    fun unavailableStepsSplitContentFromAssetAndLocalExperience() {
        val pack = pack()
        val live = liveModel()
        assertEquals("NONE", live.units.first { it.unitNumber == 16 }.assetBlocker)
        assertEquals("NONE", live.units.first { it.unitNumber == 16 }.localExperienceBlocker)
        assertFalse(live.units.first { it.unitNumber == 16 }.contentBlocker.contains("MISSING_SOURCE_SUPPORTED_CURRICULUM"))
        assertEquals("NONE", live.units.first { it.unitNumber == 17 }.assetBlocker)
        assertTrue(live.units.first { it.unitNumber == 17 }.contentBlocker.contains("MISSING_SOURCE_SUPPORTED_CURRICULUM"))
        assertFalse(live.units.first { it.unitNumber == 19 }.contentBlocker.contains("MISSING_SOURCE_SUPPORTED_CURRICULUM"))
        assertTrue(live.units.first { it.unitNumber == 1 }.assetBlocker.contains("REQUIRED_LICENSED_AUDIO_ABSENT"))
        assertEquals("NONE", live.units.first { it.unitNumber == 1 }.localExperienceBlocker)

        val both = reconcileCurriculumReadiness(
            root(),
            pack.withUnit(pack.unit(1).withAddedStep(unavailableStep("probe-source-gap", "The paradigm has no admitted frozen span."))),
        ).units.first { it.unitNumber == 1 }
        assertTrue(both.contentBlocker.contains("MISSING_SOURCE_SUPPORTED_CURRICULUM"))
        assertTrue(both.assetBlocker.contains("REQUIRED_LICENSED_AUDIO_ABSENT"))
        assertEquals("NONE", both.localExperienceBlocker)

        val local = reconcileCurriculumReadiness(
            root(),
            pack.withUnit(
                pack.unit(8).withAddedStep(
                    unavailableStep("probe-local", "This local exercise cannot be opened on the device."),
                ),
            ),
        ).units.first { it.unitNumber == 8 }
        assertEquals("NONE", local.contentBlocker)
        assertEquals("NONE", local.assetBlocker)
        assertEquals("REQUIRED_LOCAL_EXPERIENCE_UNAVAILABLE", local.localExperienceBlocker)
        assertFalse(local.completeInstructionalChain)

        val namedAsset = reconcileCurriculumReadiness(
            root(),
            pack.withUnit(
                pack.unit(19).withAddedStep(
                    unavailableStep("probe-audio", "Required licensed pronunciation audio record is absent."),
                ),
            ),
        ).units.first { it.unitNumber == 19 }
        assertTrue(namedAsset.assetBlocker.contains("REQUIRED_LICENSED_AUDIO_ABSENT"))
        assertFalse(namedAsset.contentBlocker.contains("MISSING_SOURCE_SUPPORTED_CURRICULUM"))
        assertEquals("NONE", namedAsset.localExperienceBlocker)

        val classes = unavailableExperienceClasses(
            "UNAVAILABLE",
            "The licensed audio example has no admitted frozen span.",
        )
        assertTrue(UnavailableExperienceClass.SOURCE_SUPPORTED_CURRICULUM in classes)
        assertTrue(UnavailableExperienceClass.LICENSED_OR_MAPPED_ASSET in classes)
        assertFalse(UnavailableExperienceClass.LOCAL_EXPERIENCE in classes)
    }

    @Test
    fun catalogReachabilityCountsOrphansSharesAndIntraUnitDuplicatesSeparately() {
        val orphan = catalogReachability(
            catalogIds = setOf("a", "orphan"),
            referencesByUnit = mapOf(1 to listOf("a")),
        )
        assertEquals(2, orphan.packaged)
        assertEquals(1, orphan.reachable)
        assertEquals(1, orphan.orphaned)
        assertEquals(listOf("orphan"), orphan.orphanIds)
        assertEquals(0, orphan.sharedAcrossUnits)
        assertEquals(0, orphan.crossUnitReferenceExcess)
        assertEquals(0, orphan.duplicateUnitReferenceExcess)

        val twoUnits = catalogReachability(
            catalogIds = setOf("shared"),
            referencesByUnit = mapOf(1 to listOf("shared"), 2 to listOf("shared")),
        )
        assertEquals(1, twoUnits.sharedAcrossUnits)
        assertEquals(1, twoUnits.crossUnitReferenceExcess)
        assertEquals(0, twoUnits.orphaned)

        val threeUnits = catalogReachability(
            catalogIds = setOf("shared"),
            referencesByUnit = mapOf(1 to listOf("shared"), 2 to listOf("shared"), 3 to listOf("shared")),
        )
        assertEquals(1, threeUnits.sharedAcrossUnits)
        assertEquals(2, threeUnits.crossUnitReferenceExcess)
        assertEquals(1, threeUnits.reachable)
        assertEquals(0, threeUnits.orphaned)

        val duplicate = catalogReachability(
            catalogIds = setOf("dup"),
            referencesByUnit = mapOf(4 to listOf("dup", "dup")),
        )
        assertEquals(0, duplicate.sharedAcrossUnits)
        assertEquals(0, duplicate.crossUnitReferenceExcess)
        assertEquals(1, duplicate.duplicateUnitReferenceExcess)
        assertEquals(1, duplicate.reachable)

        val control = catalogReachability(
            catalogIds = setOf("a", "b"),
            referencesByUnit = mapOf(1 to listOf("a"), 2 to listOf("b")),
        )
        assertEquals(0, control.orphaned)
        assertEquals(2, control.reachable)
        assertEquals(control.packaged, control.reachable + control.orphaned)
        assertEquals(0, control.sharedAcrossUnits)

        val live = liveModel()
        assertEquals(live.packagedExampleRecords, live.reachableExampleRecords + live.orphanedExampleRecords)
        assertEquals(3, live.sharedExampleRecords)
        assertEquals(3, live.exampleCrossUnitReferenceExcess)
        assertTrue(live.exampleDuplicateUnitReferenceExcess > 0)
        assertNotEquals(live.packagedExampleRecords, live.units.sumOf { it.exampleCount } - live.sharedExampleRecords)
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

    private fun V2CurriculumPack.withUnit(unit: V2Unit) =
        copy(units = units.map { if (it.number == unit.number) unit else it })

    private fun V2CurriculumPack.unit(number: Int) = units.first { it.number == number }

    private fun V2Unit.withAddedStep(step: V2Step): V2Unit {
        val session = sessions.first()
        return copy(sessions = listOf(session.copy(steps = session.steps + step)) + sessions.drop(1))
    }

    private fun unavailableStep(id: String, reason: String) = V2Step(
        id = id,
        kind = "UNAVAILABLE",
        title = "Probe",
        body = "",
        unavailableReason = reason,
    )
}
