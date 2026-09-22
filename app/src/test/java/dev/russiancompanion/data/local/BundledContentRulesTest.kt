package dev.russiancompanion.data.local

import dev.russiancompanion.data.local.seed.AssetRightsSeed
import dev.russiancompanion.data.local.seed.AudioModelSeed
import dev.russiancompanion.data.local.seed.BundledSeedDocument
import dev.russiancompanion.data.local.seed.CourseSegmentSeed
import dev.russiancompanion.data.local.seed.GateConditionSeed
import dev.russiancompanion.data.local.seed.GateDefinitionSeed
import dev.russiancompanion.data.local.seed.OptionalDrillSeed
import dev.russiancompanion.data.local.seed.PlanDefinitionSeed
import dev.russiancompanion.data.local.seed.SeedJson
import dev.russiancompanion.data.local.seed.SeedParse
import dev.russiancompanion.data.local.seed.SourceRecordSeed
import dev.russiancompanion.data.local.seed.SourceTextSeed
import dev.russiancompanion.data.local.seed.prepareBundledContent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class BundledContentRulesTest {
    @Test
    fun shippedSeedMatchesLicensedAudioAndKeepsThePathwayEmpty() {
        val raw = shippedSeedFile().readText()
        val parsed = SeedJson.parse(raw)
        assertTrue(parsed is SeedParse.Ok)
        val document = (parsed as SeedParse.Ok).document
        assertEquals(1, document.contentVersion)
        assertTrue(document.courseSegments.isEmpty())
        assertTrue(document.planDefinitions.isEmpty())
        assertTrue(document.gateDefinitions.isEmpty())
        assertTrue(document.optionalDrills.isEmpty())
        assertTrue(document.audioModels.isNotEmpty())
        assertTrue(document.sourceRecords.all { it.shareAlikeConfirmation == "UNCONFIRMED" })
        val prepared = prepareBundledContent(document)
        assertEquals(document.audioModels.size, prepared.audio.count { it.eligibilityStatus == EligibilityStatus.ELIGIBLE })
        assertTrue(prepared.drills.isEmpty())
    }

    @Test
    fun duplicateIdsMakeTheDocumentCorrupt() {
        val document = minimalDocument().copy(
            courseSegments = listOf(segment(), segment()),
        )
        assertTrue(SeedJson.parse(SeedJson.encode(document)) is SeedParse.Corrupt)
    }

    @Test
    fun malformedJsonIsCorrupt() {
        assertTrue(SeedJson.parse("{") is SeedParse.Corrupt)
    }

    @Test
    fun absentOrUnmatchedAudioIsExcludedAndTextIsCopiedUnchanged() {
        val prepared = prepareBundledContent(
            minimalDocument().copy(
                sourceTexts = listOf(text(matchVerified = true)),
                assetRightsRecords = listOf(rights(licenseStatus = "ABSENT", inclusionResult = "INCLUDED")),
                audioModels = listOf(audio()),
            ),
        )
        assertEquals(EligibilityStatus.EXCLUDED, prepared.audio.single().eligibilityStatus)
        assertEquals(LicenseStatus.ABSENT, prepared.rights.single().licenseStatus)
        assertEquals(InclusionResult.EXCLUDED, prepared.rights.single().inclusionResult)
        assertEquals("SOURCE_TEXT_TOKEN", prepared.texts.single().stressMarkedText)

        val matched = prepareBundledContent(
            minimalDocument().copy(
                sourceTexts = listOf(text(matchVerified = true)),
                assetRightsRecords = listOf(rights()),
                audioModels = listOf(audio()),
            ),
        )
        assertEquals(EligibilityStatus.ELIGIBLE, matched.audio.single().eligibilityStatus)
        assertEquals(InclusionResult.INCLUDED, matched.rights.single().inclusionResult)
    }

    @Test
    fun unverifiedOrSharedTextMatchIsExcluded() {
        val unverified = prepareBundledContent(
            minimalDocument().copy(
                sourceTexts = listOf(text(matchVerified = false)),
                assetRightsRecords = listOf(rights()),
                audioModels = listOf(audio()),
            ),
        )
        assertEquals(EligibilityStatus.EXCLUDED, unverified.audio.single().eligibilityStatus)

        val shared = prepareBundledContent(
            minimalDocument().copy(
                sourceTexts = listOf(text(matchVerified = true)),
                assetRightsRecords = listOf(rights(), rights(id = "rights-2", assetIdentifier = "audio-2")),
                audioModels = listOf(audio(), audio(id = "audio-2", rightsRecordId = "rights-2")),
            ),
        )
        assertTrue(shared.audio.all { it.eligibilityStatus == EligibilityStatus.EXCLUDED })
    }

    @Test
    fun defaultGateCannotRequireTutorAndOptionalDrillsAreNotEvidence() {
        val prepared = prepareBundledContent(
            minimalDocument().copy(
                gateDefinitions = listOf(
                    gate(
                        conditions = listOf(
                            GateConditionSeed("c-tutor", "TUTOR_SIGN_OFF", "PRESENT", null, 0),
                        ),
                    ),
                ),
                optionalDrills = listOf(
                    OptionalDrillSeed(
                        id = "drill-1",
                        packageId = "pkg",
                        transformId = "identity",
                        sourceFieldReferences = "field-a",
                        sourceRecordId = "source-1",
                        inheritedErrorDisclosure = "Inherits source errors",
                    ),
                ),
            ),
        )
        assertEquals(EligibilityStatus.EXCLUDED, prepared.gates.single().eligibilityStatus)
        assertTrue(prepared.conditions.isEmpty())
        assertEquals(EligibilityStatus.ELIGIBLE, prepared.drills.single().eligibilityStatus)
        assertFalse(EvidenceType.entries.any { it.name.contains("DRILL") })
    }

    @Test
    fun defaultGateRequiresBookAnkiAndPrescribedWork() {
        val incomplete = prepareBundledContent(
            minimalDocument().copy(
                gateDefinitions = listOf(
                    gate(
                        conditions = listOf(
                            GateConditionSeed("c-book", "BOOK_REVISION_SCORE", "AT_LEAST", "1", 0),
                            GateConditionSeed("c-anki", "ANKI_DROID_STATUS", "EQUALS", "MATURE", 1),
                        ),
                    ),
                ),
            ),
        )
        assertEquals(EligibilityStatus.EXCLUDED, incomplete.gates.single().eligibilityStatus)
        assertTrue(incomplete.conditions.isEmpty())

        val openAnkiText = prepareBundledContent(
            minimalDocument().copy(
                gateDefinitions = listOf(
                    gate(
                        conditions = listOf(
                            GateConditionSeed("c-book", "BOOK_REVISION_SCORE", "AT_LEAST", "1", 0),
                            GateConditionSeed("c-anki", "ANKI_DROID_STATUS", "EQUALS", "mature", 1),
                            GateConditionSeed("c-work", "PRESCRIBED_WORK_COMPLETION", "PRESENT", null, 2),
                        ),
                    ),
                ),
            ),
        )
        assertEquals(EligibilityStatus.EXCLUDED, openAnkiText.gates.single().eligibilityStatus)

        val admitted = prepareBundledContent(
            minimalDocument().copy(gateDefinitions = listOf(gate())),
        )
        assertEquals(EligibilityStatus.ELIGIBLE, admitted.gates.single().eligibilityStatus)
        assertEquals(
            listOf(
                EvidenceType.BOOK_REVISION_SCORE,
                EvidenceType.ANKI_DROID_STATUS,
                EvidenceType.PRESCRIBED_WORK_COMPLETION,
            ),
            admitted.conditions.map { it.evidenceType },
        )
        assertEquals("MATURE", admitted.conditions.single { it.evidenceType == EvidenceType.ANKI_DROID_STATUS }.operand)
        assertEquals(null, admitted.conditions.single { it.evidenceType == EvidenceType.PRESCRIBED_WORK_COMPLETION }.operand)
    }

    @Test
    fun requiredTutorPlanIsExcluded() {
        val prepared = prepareBundledContent(
            minimalDocument().copy(
                planDefinitions = listOf(
                    PlanDefinitionSeed(
                        id = "plan-tutor",
                        segmentId = "segment-1",
                        activityType = "TUTOR",
                        instruction = "Optional tutor work, only when you choose it",
                        sortOrder = 4,
                        required = true,
                    ),
                ),
            ),
        )
        assertEquals(EligibilityStatus.EXCLUDED, prepared.plans.single().eligibilityStatus)
    }

    @Test
    fun listenPlanIsDroppedWhenAnyPrescribedAudioIsIneligible() {
        val book = PlanDefinitionSeed(
            id = "plan-book",
            segmentId = "segment-1",
            activityType = "BOOK",
            instruction = "Use your copy of the book for this lesson",
            sortOrder = 1,
            required = true,
            lessonNumber = 1,
        )
        fun listen(id: String, audioIds: List<String>) = PlanDefinitionSeed(
            id = id,
            segmentId = "segment-1",
            activityType = "LISTEN_RECORD_COMPARE",
            instruction = "Listen to the licensed model",
            sortOrder = 2,
            required = true,
            audioModelIds = audioIds,
        )
        val incompatible = prepareBundledContent(
            minimalDocument().copy(
                sourceTexts = listOf(text(matchVerified = true)),
                assetRightsRecords = listOf(rights(licenseStatus = "INCOMPATIBLE", inclusionResult = "INCLUDED")),
                audioModels = listOf(audio()),
                planDefinitions = listOf(book, listen("plan-listen", listOf("audio-1"))),
            ),
        )
        assertEquals(EligibilityStatus.EXCLUDED, incompatible.audio.single().eligibilityStatus)
        assertEquals(listOf("plan-book"), incompatible.plans.map { it.id })

        val unverified = prepareBundledContent(
            minimalDocument().copy(
                sourceTexts = listOf(text(matchVerified = false)),
                assetRightsRecords = listOf(rights()),
                audioModels = listOf(audio()),
                planDefinitions = listOf(book, listen("plan-listen", listOf("audio-1"))),
            ),
        )
        assertEquals(EligibilityStatus.EXCLUDED, unverified.audio.single().eligibilityStatus)
        assertEquals(listOf("plan-book"), unverified.plans.map { it.id })

        val missingSource = prepareBundledContent(
            minimalDocument().copy(
                sourceTexts = listOf(text(matchVerified = true)),
                assetRightsRecords = listOf(rights()),
                audioModels = listOf(audio().copy(sourceRecordId = "missing-source")),
                planDefinitions = listOf(book, listen("plan-listen", listOf("audio-1"))),
            ),
        )
        assertEquals(EligibilityStatus.EXCLUDED, missingSource.audio.single().eligibilityStatus)
        assertEquals(listOf("plan-book"), missingSource.plans.map { it.id })

        val sharedText = prepareBundledContent(
            minimalDocument().copy(
                sourceTexts = listOf(text(matchVerified = true)),
                assetRightsRecords = listOf(rights(), rights(id = "rights-2", assetIdentifier = "audio-2")),
                audioModels = listOf(audio(), audio(id = "audio-2", rightsRecordId = "rights-2")),
                planDefinitions = listOf(book, listen("plan-listen", listOf("audio-1", "audio-2"))),
            ),
        )
        assertTrue(sharedText.audio.all { it.eligibilityStatus == EligibilityStatus.EXCLUDED })
        assertEquals(listOf("plan-book"), sharedText.plans.map { it.id })

        val missingPackaged = prepareBundledContent(
            minimalDocument().copy(
                sourceTexts = listOf(text(matchVerified = true)),
                assetRightsRecords = listOf(rights()),
                audioModels = listOf(audio().copy(localAudioReference = "")),
                planDefinitions = listOf(book, listen("plan-listen", listOf("audio-1"))),
            ),
        )
        assertEquals(EligibilityStatus.EXCLUDED, missingPackaged.audio.single().eligibilityStatus)
        assertEquals(listOf("plan-book"), missingPackaged.plans.map { it.id })

        val missingId = prepareBundledContent(
            minimalDocument().copy(
                sourceTexts = listOf(text(matchVerified = true)),
                assetRightsRecords = listOf(rights()),
                audioModels = listOf(audio()),
                planDefinitions = listOf(book, listen("plan-listen", listOf("audio-missing"))),
            ),
        )
        assertEquals(EligibilityStatus.ELIGIBLE, missingId.audio.single().eligibilityStatus)
        assertEquals(listOf("plan-book"), missingId.plans.map { it.id })

        val mixed = prepareBundledContent(
            minimalDocument().copy(
                sourceTexts = listOf(
                    text(matchVerified = true),
                    text(matchVerified = true, id = "text-2", stressMarkedText = "SOURCE_TEXT_TOKEN_TWO"),
                ),
                assetRightsRecords = listOf(
                    rights(),
                    rights(id = "rights-2", assetIdentifier = "audio-2", licenseStatus = "INCOMPATIBLE"),
                ),
                audioModels = listOf(
                    audio(),
                    audio(id = "audio-2", rightsRecordId = "rights-2").copy(sourceTextId = "text-2"),
                ),
                planDefinitions = listOf(book, listen("plan-listen", listOf("audio-1", "audio-2"))),
            ),
        )
        assertEquals(EligibilityStatus.ELIGIBLE, mixed.audio.single { it.id == "audio-1" }.eligibilityStatus)
        assertEquals(EligibilityStatus.EXCLUDED, mixed.audio.single { it.id == "audio-2" }.eligibilityStatus)
        assertEquals(listOf("plan-book"), mixed.plans.map { it.id })
    }

    @Test
    fun unconfirmedShareAlikeStaysOnTheSourceRecord() {
        val prepared = prepareBundledContent(
            minimalDocument().copy(
                sourceTexts = listOf(text(matchVerified = true)),
                assetRightsRecords = listOf(rights()),
                audioModels = listOf(audio()),
            ),
        )
        assertEquals(ShareAlikeConfirmation.UNCONFIRMED, prepared.sources.single().shareAlikeConfirmation)
        assertEquals(EligibilityStatus.ELIGIBLE, prepared.audio.single().eligibilityStatus)
    }

    @Test
    fun bookAndAnkiEvidenceIsSelfAttestedAndRecordingRequiresAFile() {
        val book = UserRecordRules.evidence(
            gateDefinitionId = "gate-1",
            evidenceType = EvidenceType.BOOK_REVISION_SCORE,
            enteredValue = "user-entered",
            recordedAtLocal = "local-time",
        )
        val anki = UserRecordRules.evidence(
            gateDefinitionId = "gate-1",
            evidenceType = EvidenceType.ANKI_DROID_STATUS,
            enteredValue = "user-entered",
            recordedAtLocal = "local-time",
        )
        assertEquals(SelfAttestationStatus.SELF_ATTESTED, book.selfAttestationStatus)
        assertEquals(SelfAttestationStatus.SELF_ATTESTED, anki.selfAttestationStatus)
        assertFalse(SelfAttestationStatus.entries.any { it.name.contains("VERIFIED") })
        try {
            UserRecordRules.recording(
                audioModelId = "audio-1",
                localFileReference = " ",
                createdAtLocal = "local-time",
                markedForTutorReview = false,
                completionState = CompletionState.COMPLETED,
            )
            error("blank recording file was accepted")
        } catch (_: IllegalArgumentException) {
            // expected
        }
        try {
            UserRecordRules.activityLog(
                category = ActivityCategory.READING,
                durationMinutes = -1,
                completed = false,
                recordedAtLocal = "local-time",
                segmentId = null,
                planInstanceId = null,
                selfAttested = false,
                planActivityType = null,
            )
            error("negative duration was accepted")
        } catch (_: IllegalArgumentException) {
            // expected
        }
    }

    @Test
    fun totalsAreRecomputedFromLogsForEveryGovernedCategory() {
        val logs = listOf(
            log(ActivityCategory.READING, minutes = 10, completed = true),
            log(ActivityCategory.READING, minutes = 5, completed = false),
            log(ActivityCategory.WRITING, minutes = 0, completed = true),
        )
        val totals = totalsFrom(logs)
        assertEquals(ActivityCategory.entries.toList(), totals.map { it.category })
        assertEquals(15L, totals.first { it.category == ActivityCategory.READING }.recordedMinutes)
        assertEquals(1, totals.first { it.category == ActivityCategory.READING }.completionCount)
        assertEquals(0L, totals.first { it.category == ActivityCategory.LISTENING }.recordedMinutes)
        assertNull(totals.firstOrNull { it.category.name.contains("PROFICIENCY") })
    }

    @Test
    fun userRecordsHaveNoJudgmentColumns() {
        val forbidden = listOf("score", "pronunciation", "fluency", "transcript", "proficiency", "verified", "asr")
        val types = listOf(
            LearnerStateEntity::class.java,
            PlanInstanceEntity::class.java,
            ActivityLogEntity::class.java,
            RecordingRecordEntity::class.java,
            GateEvidenceEntity::class.java,
            GateDecisionEntity::class.java,
            ExportRecordEntity::class.java,
        )
        val names = types.flatMap { type -> type.declaredFields.map { it.name.lowercase() } }
        assertTrue(names.none { name -> forbidden.any(name::contains) })
    }

    private fun shippedSeedFile(): File {
        val candidates = listOf(
            File("src/main/assets/content/bundled-content.json"),
            File("app/src/main/assets/content/bundled-content.json"),
        )
        return candidates.firstOrNull { it.isFile }
            ?: error("bundled-content.json was not found from ${File(".").absolutePath}")
    }

    private fun log(category: ActivityCategory, minutes: Int, completed: Boolean): ActivityLogEntity {
        return ActivityLogEntity(
            id = category.name + minutes,
            category = category,
            durationMinutes = minutes,
            completed = completed,
            recordedAtLocal = "local-time",
            segmentId = null,
            planInstanceId = null,
            selfAttested = false,
        )
    }

    private companion object {
        val CYRILLIC = 0x0400..0x04FF
    }
}

internal fun minimalDocument(): BundledSeedDocument {
    return BundledSeedDocument(
        contentVersion = 1,
        courseSegments = listOf(segment()),
        planDefinitions = emptyList(),
        gateDefinitions = emptyList(),
        sourceRecords = listOf(source()),
        sourceTexts = emptyList(),
        assetRightsRecords = emptyList(),
        audioModels = emptyList(),
        optionalDrills = emptyList(),
    )
}

internal fun segment(): CourseSegmentSeed {
    return CourseSegmentSeed(
        id = "segment-1",
        lessonReference = "Lesson 1",
        lessonNumber = 1,
        sortOrder = 1,
    )
}

internal fun source(): SourceRecordSeed {
    return SourceRecordSeed(
        id = "source-1",
        provenance = "test-source",
        redistributionBasis = "test-rights",
        licenseName = "TEST-LICENSE",
        attribution = "Test attribution",
        modificationStatus = "UNMODIFIED",
        shareAlikeTreatment = "share-alike-applies",
        shareAlikeConfirmation = "UNCONFIRMED",
    )
}

internal fun text(
    matchVerified: Boolean,
    id: String = "text-1",
    stressMarkedText: String = "SOURCE_TEXT_TOKEN",
    sourceRecordId: String = "source-1",
): SourceTextSeed {
    return SourceTextSeed(
        id = id,
        stressMarkedText = stressMarkedText,
        sourceRecordId = sourceRecordId,
        modificationStatus = "UNMODIFIED",
        matchVerified = matchVerified,
    )
}

internal fun rights(
    id: String = "rights-1",
    assetIdentifier: String = "audio-1",
    licenseStatus: String = "COMPATIBLE",
    inclusionResult: String = "INCLUDED",
): AssetRightsSeed {
    return AssetRightsSeed(
        id = id,
        sourceRecordId = "source-1",
        assetIdentifier = assetIdentifier,
        licenseStatus = licenseStatus,
        compatibilityDecision = licenseStatus,
        attributionRequired = true,
        inclusionResult = inclusionResult,
    )
}

internal fun audio(id: String = "audio-1", rightsRecordId: String = "rights-1"): AudioModelSeed {
    return AudioModelSeed(
        id = id,
        localAudioReference = "audio/$id",
        sourceTextId = "text-1",
        sourceRecordId = "source-1",
        rightsRecordId = rightsRecordId,
    )
}

internal fun gate(conditions: List<GateConditionSeed> = defaultGateConditionSeeds()): GateDefinitionSeed {
    return GateDefinitionSeed(
        id = "gate-1",
        segmentId = "segment-1",
        ruleVersion = "1",
        displayedRuleText = "Displayed test rule",
        conditions = conditions,
    )
}

internal fun defaultGateConditionSeeds(): List<GateConditionSeed> {
    return listOf(
        GateConditionSeed("c-book", "BOOK_REVISION_SCORE", "AT_LEAST", "1", 0),
        GateConditionSeed("c-anki", "ANKI_DROID_STATUS", "EQUALS", "MATURE", 1),
        GateConditionSeed("c-work", "PRESCRIBED_WORK_COMPLETION", "PRESENT", null, 2),
    )
}
