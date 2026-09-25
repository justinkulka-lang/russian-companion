package dev.russiancompanion.v2.audit

import dev.russiancompanion.release.ACCEPTANCE_CANDIDATE_PATH
import dev.russiancompanion.release.assessRepositoryRelease
import dev.russiancompanion.release.computeReleaseSourceDigest
import dev.russiancompanion.release.readAcceptanceCandidate
import dev.russiancompanion.v2.curriculum.NON_LINGUISTIC_REFERENCE_CLASSIFICATIONS
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.V2Unit
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.advancementDecision
import dev.russiancompanion.v2.curriculum.example
import dev.russiancompanion.v2.curriculum.item
import dev.russiancompanion.v2.curriculum.letter
import dev.russiancompanion.v2.review.buildUnitReviewPackage
import dev.russiancompanion.v2.review.curriculumCommitForUnit
import dev.russiancompanion.v2.review.governedMappingFacts
import dev.russiancompanion.v2.review.mappingPackageDigest
import dev.russiancompanion.v2.review.reviewFileNames
import dev.russiancompanion.v2.review.reviewManifestName
import dev.russiancompanion.v2.review.reviewMappingName
import java.io.File

internal const val CURRICULUM_READINESS_DIRECTORY = "audit/curriculum-readiness"

internal const val CURRICULUM_READINESS_MARKDOWN =
    "$CURRICULUM_READINESS_DIRECTORY/V2_CURRICULUM_READINESS_RECONCILIATION.md"

internal const val CURRICULUM_READINESS_JSON =
    "$CURRICULUM_READINESS_DIRECTORY/V2_CURRICULUM_READINESS_RECONCILIATION.json"

internal val CURRICULUM_READINESS_COLUMNS = listOf(
    "unitNumber" to "Unit number",
    "title" to "Title",
    "installState" to "Install state",
    "readiness" to "Readiness",
    "mappingStatus" to "Mapping status",
    "unitReviewStatus" to "Unit-review status",
    "reviewerIdentity" to "Reviewer identity",
    "reviewerQualification" to "Reviewer qualification",
    "signedReviewedDigest" to "Signed reviewed digest",
    "unitContentReviewDigest" to "Unit content-review digest",
    "mappingPackageDigest" to "Mapping-package digest",
    "reviewPackageDigest" to "Review-package digest",
    "curriculumSourceCommit" to "Curriculum source commit",
    "objectiveCount" to "Objective count",
    "sessionCount" to "Session count",
    "instructionStepCount" to "Instruction-step count",
    "exampleCount" to "Example count",
    "guidedPracticeCount" to "Guided-practice count",
    "independentCheckCount" to "Independent-check count",
    "remediationCount" to "Remediation count",
    "advancementRuleState" to "Advancement-rule state",
    "advancementCanPresentlySucceed" to "Advancement can presently succeed",
    "bookDirectedActivityCount" to "Book-directed activity count",
    "unavailableExperienceCount" to "Unavailable-experience count",
    "provenanceRecordCount" to "Provenance-record count",
    "authoredEnglishRecordCount" to "Authored-English record count",
    "admittedSourceCount" to "Admitted source count",
    "rightsRecordCount" to "Rights-record count",
    "sourceCapabilityCount" to "Source-capability count",
    "resolvedSourceCapabilityCount" to "Resolved source-capability count",
    "unresolvedRequiredSourceCapabilities" to "Unresolved required source capabilities",
    "mappedAudioCount" to "Mapped audio count",
    "handwritingAssetCount" to "Handwriting-asset count",
    "contentBlocker" to "Content-completeness blocker",
    "linguisticReviewBlocker" to "Linguistic-review blocker",
    "mappingBlocker" to "Mapping blocker",
    "assetBlocker" to "Asset blocker",
    "localExperienceBlocker" to "Local-experience blocker",
    "authorityVarianceBlocker" to "Authority-variance blocker",
    "completeInstructionalChain" to "Complete instructional chain",
    "finalDisposition" to "Final disposition",
)

internal data class ReadinessUnitRow(
    val unitNumber: Int,
    val title: String,
    val installState: String,
    val readiness: String,
    val mappingStatus: String,
    val unitReviewStatus: String,
    val reviewerIdentity: String,
    val reviewerQualification: String,
    val signedReviewedDigest: String,
    val unitContentReviewDigest: String,
    val mappingPackageDigest: String,
    val reviewPackageDigest: String,
    val curriculumSourceCommit: String,
    val objectiveCount: Int,
    val sessionCount: Int,
    val instructionStepCount: Int,
    val exampleCount: Int,
    val guidedPracticeCount: Int,
    val independentCheckCount: Int,
    val remediationCount: Int,
    val advancementRuleState: String,
    val advancementCanPresentlySucceed: Boolean,
    val bookDirectedActivityCount: Int,
    val unavailableExperienceCount: Int,
    val provenanceRecordCount: Int,
    val authoredEnglishRecordCount: Int,
    val admittedSourceCount: Int,
    val rightsRecordCount: Int,
    val sourceCapabilityCount: Int,
    val resolvedSourceCapabilityCount: Int,
    val unresolvedRequiredSourceCapabilities: String,
    val mappedAudioCount: Int,
    val handwritingAssetCount: Int,
    val contentBlocker: String,
    val linguisticReviewBlocker: String,
    val mappingBlocker: String,
    val assetBlocker: String,
    val localExperienceBlocker: String,
    val authorityVarianceBlocker: String,
    val completeInstructionalChain: Boolean,
    val finalDisposition: String,
    val recordedTopicSummary: String,
)

internal data class ReadinessNextWork(
    val id: String,
    val title: String,
    val units: List<Int>,
    val requiredInput: String,
)

internal data class CurriculumReadinessReconciliation(
    val units: List<ReadinessUnitRow>,
    val packContentReviewDigest: String,
    val repositorySourceDigest: String,
    val generatedRussianAdmissionCount: Int,
    val itemsMarkedContainsGeneratedRussian: Int,
    val penguinProseAdmissionCount: Int,
    val knownPenguinTitleMarkerMatches: Int,
    val packagedAudioBoundToV2Units: Int,
    val packagedExampleRecords: Int,
    val reachableExampleRecords: Int,
    val sharedExampleRecords: Int,
    val exampleCrossUnitReferenceExcess: Int,
    val exampleDuplicateUnitReferenceExcess: Int,
    val orphanedExampleRecords: Int,
    val orphanedExampleIds: String,
    val packagedCheckItems: Int,
    val reachableCheckItems: Int,
    val sharedCheckItems: Int,
    val checkItemCrossUnitReferenceExcess: Int,
    val checkItemDuplicateUnitReferenceExcess: Int,
    val orphanedCheckItems: Int,
    val orphanedCheckItemIds: String,
    val distinctAdmittedSources: Int,
    val distinctRightsRecords: Int,
    val crossUnitSourceRecords: Int,
    val internetPermissionDeclaredInMainManifest: String,
    val internetPermissionDeclaredInMergedDebugManifest: String,
    val publicRelease: String,
    val candidateFrozen: Boolean,
    val candidateArtifact: String,
    val candidateSourceCommit: String,
    val retainedEvidence: String,
    val at031: String,
    val releaseDecision: String,
    val releaseGateBlockers: List<String>,
    val varianceProposalStatus: String,
    val varianceResolutionStatus: String,
    val nextWork: List<ReadinessNextWork>,
)

internal fun loadCurriculumPack(repositoryRoot: File): V2CurriculumPack {
    val file = File(repositoryRoot, "app/src/main/assets/content/v2-curriculum.json")
    return V2PackLoader.parse(file.readText())
}

/**
 * Builds the Units 1–30 readiness reconciliation from the packaged curriculum
 * and governed repository records. A second call with the same inputs returns
 * an equal model; the rendered files are byte-identical.
 *
 * Invoke with:
 * WRITE_CURRICULUM_READINESS=1 ./gradlew :app:testDebugUnitTest --offline --tests dev.russiancompanion.v2.audit.CurriculumReadinessReconciliationTest
 */
internal fun reconcileCurriculumReadiness(
    repositoryRoot: File,
    pack: V2CurriculumPack = loadCurriculumPack(repositoryRoot),
    reviewManifests: Map<Int, String?>? = null,
    runtimeAdvancementAllowed: ((V2CurriculumPack, V2Unit) -> Boolean)? = null,
): CurriculumReadinessReconciliation {
    val numbers = pack.units.map { it.number }
    if (numbers != (1..30).toList()) {
        throw IllegalArgumentException("Units 1-30 must be present exactly once in order")
    }
    val rows = pack.units.map { unit ->
        unitRow(repositoryRoot, pack, unit, reviewManifests, runtimeAdvancementAllowed)
    }
    val assessment = assessRepositoryRelease(repositoryRoot)
    val candidateFile = File(repositoryRoot, ACCEPTANCE_CANDIDATE_PATH)
    val candidateText = if (candidateFile.isFile) candidateFile.readText() else ""
    val candidate = readAcceptanceCandidate(repositoryRoot)
    val mainManifest = File(repositoryRoot, "app/src/main/AndroidManifest.xml").let { file ->
        if (file.isFile) file.readText() else ""
    }
    val mergedManifest = mergedDebugManifestFile(repositoryRoot).let { file ->
        if (file.isFile) file.readText() else null
    }
    val examples = catalogReachability(pack.examples.map { it.id }.toSet(), exampleReferences(pack))
    val checks = catalogReachability(pack.items.map { it.id }.toSet(), checkItemReferences(pack))
    val model = CurriculumReadinessReconciliation(
        units = rows,
        packContentReviewDigest = V2Validator.contentReviewDigest(pack),
        repositorySourceDigest = computeReleaseSourceDigest(repositoryRoot),
        generatedRussianAdmissionCount = pack.admissions.generatedRussianWords,
        itemsMarkedContainsGeneratedRussian = itemsMarkedContainsGeneratedRussian(pack),
        penguinProseAdmissionCount = pack.admissions.penguinProseAssets,
        knownPenguinTitleMarkerMatches = knownPenguinTitleMarkerMatches(pack),
        packagedAudioBoundToV2Units = pack.admissions.catalogAudioBoundToUnits,
        packagedExampleRecords = examples.packaged,
        reachableExampleRecords = examples.reachable,
        sharedExampleRecords = examples.sharedAcrossUnits,
        exampleCrossUnitReferenceExcess = examples.crossUnitReferenceExcess,
        exampleDuplicateUnitReferenceExcess = examples.duplicateUnitReferenceExcess,
        orphanedExampleRecords = examples.orphaned,
        orphanedExampleIds = idList(examples.orphanIds),
        packagedCheckItems = checks.packaged,
        reachableCheckItems = checks.reachable,
        sharedCheckItems = checks.sharedAcrossUnits,
        checkItemCrossUnitReferenceExcess = checks.crossUnitReferenceExcess,
        checkItemDuplicateUnitReferenceExcess = checks.duplicateUnitReferenceExcess,
        orphanedCheckItems = checks.orphaned,
        orphanedCheckItemIds = idList(checks.orphanIds),
        distinctAdmittedSources = distinctAdmittedSources(pack).size,
        distinctRightsRecords = distinctRightsRecords(pack),
        crossUnitSourceRecords = crossUnitSources(pack),
        internetPermissionDeclaredInMainManifest = internetPermissionDeclaredInManifestText(mainManifest),
        internetPermissionDeclaredInMergedDebugManifest = if (mergedManifest == null) {
            "NOT_GENERATED"
        } else {
            internetPermissionDeclaredInManifestText(mergedManifest)
        },
        publicRelease = candidate?.publicRelease ?: "UNREADABLE",
        candidateFrozen = candidate?.frozen ?: false,
        candidateArtifact = candidate?.artifact?.status ?: "NOT_RECORDED",
        candidateSourceCommit = candidate?.sourceCommit ?: "null",
        retainedEvidence = jsonStringField(candidateText, "retainedEvidence") ?: "UNREADABLE",
        at031 = at031State(candidateText, assessment.blockers.map { it.name }),
        releaseDecision = assessment.decision.name,
        releaseGateBlockers = assessment.blockers.map { it.name },
        varianceProposalStatus = proposalStatus(
            File(repositoryRoot, "docs/governance/UNITS_26_30_AUTHORITY_VARIANCE_PROPOSAL.md"),
        ),
        varianceResolutionStatus = proposalStatus(
            File(repositoryRoot, "docs/governance/UNITS_26_30_AUTHORITY_VARIANCE_PROPOSED_RESOLUTION.md"),
        ),
        nextWork = emptyList(),
    )
    return model.copy(nextWork = nextWork(model))
}

internal fun renderCurriculumReadinessMarkdown(model: CurriculumReadinessReconciliation): String {
    return buildString {
        append("# v2.0 curriculum-readiness reconciliation\n\n")
        append("This report is generated from the packaged curriculum, the curriculum validator, ")
        append("the review-packet generator, the source-capability model, and the release gate. ")
        append("It does not approve linguistic content, owned-edition mappings, unit installation, ")
        append("either authority-variance proposal, candidate freeze, physical acceptance, AT-031, or public release. ")
        append("A future independent curriculum audit, including a Claude audit, is not a qualified human signature.\n\n")
        append("Generator: `app/src/test/java/dev/russiancompanion/v2/audit/CurriculumReadinessReconciliation.kt`.\n\n")
        append("Invocation: `WRITE_CURRICULUM_READINESS=1 ./gradlew :app:testDebugUnitTest --offline ")
        append("--tests dev.russiancompanion.v2.audit.CurriculumReadinessReconciliationTest`.\n\n")
        append("Narrow checks, not comprehensive inspection: `itemsMarkedContainsGeneratedRussian` counts ")
        append("check items whose `containsGeneratedRussian` flag is true and does not inspect item text for ")
        append("Russian authorship. `knownPenguinTitleMarkerMatches` counts occurrences of the known title ")
        append("marker \"the new penguin\" in packaged unit, example, item, provenance, and reference text ")
        append("and does not search the repository for other Penguin prose. ")
        append("`internetPermissionDeclaredInMainManifest` reads only `app/src/main/AndroidManifest.xml`. ")
        append("`internetPermissionDeclaredInMergedDebugManifest` reads only the debug merged manifest at ")
        append("`app/build/intermediates/merged_manifest/debug/processDebugMainManifest/AndroidManifest.xml` ")
        append("when that build output exists, and is `NOT_GENERATED` when it does not. ")
        append("These are narrow structural or known-marker checks. They are not comprehensive authorship, ")
        append("copyright, or merged-manifest analysis.\n\n")
        append("## Global reconciliation\n\n")
        append("| Total | Value |\n")
        append("| --- | --- |\n")
        globalRows(model).forEach { (key, value) ->
            append("| ${escapeCell(key)} | ${escapeCell(value)} |\n")
        }
        append("\n`totalExamples` and `totalIndependentChecks` sum unit rows. ")
        append("A row sum minus the count of shared records does not prove catalog coverage. ")
        append("`packagedExampleRecords` = `reachableExampleRecords` + `orphanedExampleRecords`. ")
        append("`exampleCrossUnitReferenceExcess` adds one for every unit beyond the first that references a record, ")
        append("so a record shared by three units contributes 2. ")
        append("`exampleDuplicateUnitReferenceExcess` counts repeated references to the same record inside one unit. ")
        append("The same identities apply to check items. ")
        append("Orphaned example ids: ${model.orphanedExampleIds}. ")
        append("Orphaned check-item ids: ${model.orphanedCheckItemIds}.\n")
        append("`totalAdmittedSources` and `totalRightsRecords` sum the unit rows. ")
        append("${model.crossUnitSourceRecords} admitted sources are referenced by more than one unit. ")
        append("`distinctAdmittedSources` and `distinctRightsRecords` count each shared source or rights record once.\n")
        append("\n## Blocker totals\n\n")
        append("| Category | Units |\n")
        append("| --- | --- |\n")
        blockerTotals(model).forEach { (key, value) ->
            append("| ${escapeCell(key)} | $value |\n")
        }
        append("\n## Unit 1-30 matrix\n\n")
        append("| ${CURRICULUM_READINESS_COLUMNS.joinToString(" | ") { it.second }} |\n")
        append("| ${CURRICULUM_READINESS_COLUMNS.joinToString(" | ") { "---" }} |\n")
        model.units.forEach { row ->
            append("| ${matrixCells(row).joinToString(" | ") { escapeCell(it) }} |\n")
        }
        append("\n## Next development sequence\n\n")
        append("Groups follow shared dependencies. They are not a unit-by-unit march from Unit 1. ")
        append("Nothing in this section is implemented by this report.\n\n")
        model.nextWork.forEach { group ->
            append("### ${group.title}\n\n")
            append("- Affected units: ${if (group.units.isEmpty()) "NONE" else group.units.joinToString(", ")}\n")
            append("- Required input: ${group.requiredInput}\n\n")
        }
        append("## Acceptance and release, kept separate\n\n")
        append("Public release is `${model.publicRelease}`. The candidate is ")
        append(if (model.candidateFrozen) "frozen" else "unfrozen")
        append(", artifact `${model.candidateArtifact}`, source commit `${model.candidateSourceCommit}`. ")
        append("Retained evidence is `${model.retainedEvidence}`. AT-031 is `${model.at031}`. ")
        append("Release decision `${model.releaseDecision}` with blockers: ")
        append(if (model.releaseGateBlockers.isEmpty()) "NONE" else model.releaseGateBlockers.joinToString(", "))
        append(".\n\n")
        append("Authority-variance proposal status: `${model.varianceProposalStatus}`. ")
        append("Proposed-resolution status: `${model.varianceResolutionStatus}`. ")
        append("Neither file is activated authority.\n")
    }
}

internal fun renderCurriculumReadinessJson(model: CurriculumReadinessReconciliation): String {
    return buildString {
        append("{\n")
        append("  \"schemaVersion\": 2,\n")
        append("  \"kind\": \"v2-curriculum-readiness-reconciliation\",\n")
        append("  \"packContentReviewDigest\": ${json(model.packContentReviewDigest)},\n")
        append("  \"repositorySourceDigest\": ${json(model.repositorySourceDigest)},\n")
        append("  \"generatedRussianAdmissionCount\": ${model.generatedRussianAdmissionCount},\n")
        append("  \"itemsMarkedContainsGeneratedRussian\": ${model.itemsMarkedContainsGeneratedRussian},\n")
        append("  \"penguinProseAdmissionCount\": ${model.penguinProseAdmissionCount},\n")
        append("  \"knownPenguinTitleMarkerMatches\": ${model.knownPenguinTitleMarkerMatches},\n")
        append("  \"packagedAudioBoundToV2Units\": ${model.packagedAudioBoundToV2Units},\n")
        append("  \"internetPermissionDeclaredInMainManifest\": ${json(model.internetPermissionDeclaredInMainManifest)},\n")
        append("  \"internetPermissionDeclaredInMergedDebugManifest\": ${json(model.internetPermissionDeclaredInMergedDebugManifest)},\n")
        append("  \"publicRelease\": ${json(model.publicRelease)},\n")
        append("  \"candidateFrozen\": ${model.candidateFrozen},\n")
        append("  \"candidateArtifact\": ${json(model.candidateArtifact)},\n")
        append("  \"candidateSourceCommit\": ${json(model.candidateSourceCommit)},\n")
        append("  \"retainedEvidence\": ${json(model.retainedEvidence)},\n")
        append("  \"at031\": ${json(model.at031)},\n")
        append("  \"releaseDecision\": ${json(model.releaseDecision)},\n")
        append("  \"releaseGateBlockers\": ${jsonArray(model.releaseGateBlockers)},\n")
        append("  \"varianceProposalStatus\": ${json(model.varianceProposalStatus)},\n")
        append("  \"varianceResolutionStatus\": ${json(model.varianceResolutionStatus)},\n")
        append("  \"totals\": {\n")
        val totals = globalRows(model)
        totals.forEachIndexed { index, (key, value) ->
            val comma = if (index == totals.lastIndex) "" else ","
            append("    ${json(key)}: ${json(value)}$comma\n")
        }
        append("  },\n")
        append("  \"blockerTotals\": {\n")
        val blockers = blockerTotals(model)
        blockers.forEachIndexed { index, (key, value) ->
            val comma = if (index == blockers.lastIndex) "" else ","
            append("    ${json(key)}: $value$comma\n")
        }
        append("  },\n")
        append("  \"units\": [\n")
        model.units.forEachIndexed { index, row ->
            val comma = if (index == model.units.lastIndex) "" else ","
            append("    ${unitJson(row)}$comma\n")
        }
        append("  ],\n")
        append("  \"nextWork\": [\n")
        model.nextWork.forEachIndexed { index, group ->
            val comma = if (index == model.nextWork.lastIndex) "" else ","
            append("    {\"id\": ${json(group.id)}, \"title\": ${json(group.title)}, ")
            append("\"units\": [${group.units.joinToString(", ")}], ")
            append("\"requiredInput\": ${json(group.requiredInput)}}$comma\n")
        }
        append("  ]\n")
        append("}\n")
    }
}

internal fun writeCurriculumReadinessReports(repositoryRoot: File): Pair<String, String> {
    val model = reconcileCurriculumReadiness(repositoryRoot)
    val markdown = renderCurriculumReadinessMarkdown(model)
    val jsonText = renderCurriculumReadinessJson(model)
    val directory = File(repositoryRoot, CURRICULUM_READINESS_DIRECTORY)
    directory.mkdirs()
    File(repositoryRoot, CURRICULUM_READINESS_MARKDOWN).writeText(markdown)
    File(repositoryRoot, CURRICULUM_READINESS_JSON).writeText(jsonText)
    return markdown to jsonText
}

internal fun matrixValue(row: ReadinessUnitRow, key: String): String = when (key) {
    "unitNumber" -> row.unitNumber.toString()
    "title" -> row.title
    "installState" -> row.installState
    "readiness" -> row.readiness
    "mappingStatus" -> row.mappingStatus
    "unitReviewStatus" -> row.unitReviewStatus
    "reviewerIdentity" -> row.reviewerIdentity
    "reviewerQualification" -> row.reviewerQualification
    "signedReviewedDigest" -> row.signedReviewedDigest
    "unitContentReviewDigest" -> row.unitContentReviewDigest
    "mappingPackageDigest" -> row.mappingPackageDigest
    "reviewPackageDigest" -> row.reviewPackageDigest
    "curriculumSourceCommit" -> row.curriculumSourceCommit
    "objectiveCount" -> row.objectiveCount.toString()
    "sessionCount" -> row.sessionCount.toString()
    "instructionStepCount" -> row.instructionStepCount.toString()
    "exampleCount" -> row.exampleCount.toString()
    "guidedPracticeCount" -> row.guidedPracticeCount.toString()
    "independentCheckCount" -> row.independentCheckCount.toString()
    "remediationCount" -> row.remediationCount.toString()
    "advancementRuleState" -> row.advancementRuleState
    "advancementCanPresentlySucceed" -> if (row.advancementCanPresentlySucceed) "YES" else "NO"
    "bookDirectedActivityCount" -> row.bookDirectedActivityCount.toString()
    "unavailableExperienceCount" -> row.unavailableExperienceCount.toString()
    "provenanceRecordCount" -> row.provenanceRecordCount.toString()
    "authoredEnglishRecordCount" -> row.authoredEnglishRecordCount.toString()
    "admittedSourceCount" -> row.admittedSourceCount.toString()
    "rightsRecordCount" -> row.rightsRecordCount.toString()
    "sourceCapabilityCount" -> row.sourceCapabilityCount.toString()
    "resolvedSourceCapabilityCount" -> row.resolvedSourceCapabilityCount.toString()
    "unresolvedRequiredSourceCapabilities" -> row.unresolvedRequiredSourceCapabilities
    "mappedAudioCount" -> row.mappedAudioCount.toString()
    "handwritingAssetCount" -> row.handwritingAssetCount.toString()
    "contentBlocker" -> row.contentBlocker
    "linguisticReviewBlocker" -> row.linguisticReviewBlocker
    "mappingBlocker" -> row.mappingBlocker
    "assetBlocker" -> row.assetBlocker
    "localExperienceBlocker" -> row.localExperienceBlocker
    "authorityVarianceBlocker" -> row.authorityVarianceBlocker
    "completeInstructionalChain" -> if (row.completeInstructionalChain) "YES" else "NO"
    "finalDisposition" -> row.finalDisposition
    else -> error("Unknown readiness column $key")
}

private fun matrixCells(row: ReadinessUnitRow): List<String> =
    CURRICULUM_READINESS_COLUMNS.map { matrixValue(row, it.first) }

private fun unitRow(
    repositoryRoot: File,
    pack: V2CurriculumPack,
    unit: V2Unit,
    reviewManifests: Map<Int, String?>?,
    runtimeAdvancementAllowed: ((V2CurriculumPack, V2Unit) -> Boolean)?,
): ReadinessUnitRow {
    val reviewPackage = buildUnitReviewPackage(pack, unit.number)
    val contentDigest = V2Validator.unitContentReviewDigest(pack, unit.number)
    val mappingDigest = mappingPackageDigest(governedMappingFacts(unit.number))
    val manifestText = manifestText(repositoryRoot, unit.number, reviewManifests)
    val reviewDigest = if (manifestText == null) {
        "NONE"
    } else {
        reviewPackage.reviewPackageDigest
    }
    val curriculumCommit = curriculumCommitForUnit(unit.number)
    val reviews = pack.unitReviews.filter { it.unitNumber == unit.number }
    val review = reviews.singleOrNull()
    val reviewStatus = when (reviews.size) {
        0 -> "MISSING"
        1 -> review!!.status.ifBlank { "MISSING" }
        else -> "DUPLICATE"
    }
    val identityPresent = review?.reviewer?.isNotBlank() == true
    val qualificationPresent = review?.reviewerQualification?.isNotBlank() == true
    val signed = review?.status == "APPROVED" &&
        identityPresent &&
        qualificationPresent &&
        review.reviewedContentDigest == contentDigest
    val counts = reviewPackage.records.groupingBy { it.type }.eachCount()
    val unresolvedCaps = V2Validator.unresolvedRequiredSourceCapabilities(pack, unit.number)
    val resolvedBindings = V2Validator.requiredSourceCapabilityBindings(unit.number).count { requirement ->
        unresolvedCaps.none { it.objectiveId == requirement.objectiveId && it.claim == requirement.claim }
    }
    val contentErrors = V2Validator.contentCompletenessErrors(pack, unit)
    val unavailableClasses = unit.sessions.flatMap { it.steps }.flatMap { step ->
        unavailableExperienceClasses(step.kind, step.unavailableReason)
    }.toSet()
    val contentCodes = contentCodes(pack, unit, contentErrors, unresolvedCaps.map { it.claim }, unavailableClasses)
    val linguisticCodes = linguisticCodes(
        pack,
        unit,
        reviewStatus,
        identityPresent,
        qualificationPresent,
        signed,
        manifestText,
        reviewPackage.reviewPackageDigest,
        curriculumCommit,
    )
    val mappingCodes = mappingCodes(repositoryRoot, pack, unit, mappingDigest)
    val assetCodes = assetCodes(pack, unit, unavailableClasses)
    val localExperience = if (UnavailableExperienceClass.LOCAL_EXPERIENCE in unavailableClasses) {
        "REQUIRED_LOCAL_EXPERIENCE_UNAVAILABLE"
    } else {
        "NONE"
    }
    val variance = if (V2Validator.authorityVarianceApplies(unit.number)) "PROPOSED_NOT_ACTIVATED" else "NOT_APPLICABLE"
    val chainComplete = contentErrors.isEmpty()
    val untaught = unit.objectives.any { it.kind == "RECOGNITION" && it.gating && it.instructionStepIds.isEmpty() }
    val unsupported = unit.objectives.any { it.kind != "RECOGNITION" && it.checkItemIds.isNotEmpty() }
    val decisionAllowed = runtimeAdvancementAllowed?.invoke(pack, unit)
        ?: (unit.advancement != null && advancementDecision(pack, unit, idealizedLearner(pack, unit)).allowed)
    val advancement = reportAdvancement(unit.readiness, decisionAllowed)
    val canAdvance = advancement.canPresentlySucceed && !untaught && !unsupported
    val claimsInstall = unit.installState == "INSTALLED" || unit.readiness == "INSTALLABLE"
    val installApproved = signed && unit.mappingStatus == "APPROVED" && mappingCodes.none { it != "NONE" }
    val disposition = disposition(
        unit = unit,
        reviewStatus = reviewStatus,
        canAdvance = canAdvance,
        advancementInconsistent = advancement.readinessInconsistent,
        claimsInstall = claimsInstall,
        installApproved = installApproved,
        content = joinCodes(contentCodes),
        linguistic = joinCodes(linguisticCodes),
        mapping = joinCodes(mappingCodes),
        asset = joinCodes(assetCodes),
        localExperience = localExperience,
        variance = variance,
    )
    val sourceIds = admittedSourceIds(pack, unit)
    return ReadinessUnitRow(
        unitNumber = unit.number,
        title = unit.title.ifBlank { "NONE" },
        installState = unit.installState.ifBlank { "NONE" },
        readiness = unit.readiness.ifBlank { "NONE" },
        mappingStatus = unit.mappingStatus.ifBlank { "NONE" },
        unitReviewStatus = reviewStatus,
        reviewerIdentity = if (identityPresent) "PRESENT" else "ABSENT",
        reviewerQualification = if (qualificationPresent) "PRESENT" else "ABSENT",
        signedReviewedDigest = if (signed) "PRESENT" else "ABSENT",
        unitContentReviewDigest = contentDigest,
        mappingPackageDigest = mappingDigest,
        reviewPackageDigest = reviewDigest,
        curriculumSourceCommit = curriculumCommit,
        objectiveCount = unit.objectives.size,
        sessionCount = unit.sessions.size,
        instructionStepCount = reviewPackage.edges.filter { it.kind == "INSTRUCTION" }.map { it.toId }.distinct().size,
        exampleCount = counts["EXAMPLE"] ?: 0,
        guidedPracticeCount = (counts["GUIDED_PRACTICE"] ?: 0) + (counts["GUIDED_AND_INDEPENDENT_CHECK"] ?: 0),
        independentCheckCount = (counts["INDEPENDENT_CHECK"] ?: 0) + (counts["GUIDED_AND_INDEPENDENT_CHECK"] ?: 0),
        remediationCount = reviewPackage.edges.filter { it.kind == "REMEDIATION" }.map { it.toId }.distinct().size,
        advancementRuleState = if (unit.advancement == null) "NONE" else "PRESENT",
        advancementCanPresentlySucceed = canAdvance,
        bookDirectedActivityCount = reviewPackage.bookDirectedIds.size,
        unavailableExperienceCount = reviewPackage.unavailableIds.size,
        provenanceRecordCount = pack.provenanceRecords.count { it.unitNumber == unit.number },
        authoredEnglishRecordCount = pack.authoredEnglishRecords.count { it.unitNumber == unit.number },
        admittedSourceCount = sourceIds.size,
        rightsRecordCount = pack.rights.count { it.sourceId in sourceIds },
        sourceCapabilityCount = pack.sourceCapabilities.count { it.unitNumber == unit.number },
        resolvedSourceCapabilityCount = resolvedBindings,
        unresolvedRequiredSourceCapabilities = if (unresolvedCaps.isEmpty()) "NONE" else unresolvedCaps.joinToString(", ") { it.claim },
        mappedAudioCount = pack.audioAssets.count { it.unitNumber == unit.number },
        handwritingAssetCount = pack.handwritingAssets.count { it.unitNumber == unit.number },
        contentBlocker = joinCodes(contentCodes),
        linguisticReviewBlocker = joinCodes(linguisticCodes),
        mappingBlocker = joinCodes(mappingCodes),
        assetBlocker = joinCodes(assetCodes),
        localExperienceBlocker = localExperience,
        authorityVarianceBlocker = variance,
        completeInstructionalChain = chainComplete,
        finalDisposition = disposition,
        recordedTopicSummary = unit.topicSummary,
    )
}

private fun contentCodes(
    pack: V2CurriculumPack,
    unit: V2Unit,
    errors: List<String>,
    unresolvedClaims: List<String>,
    unavailableClasses: Set<UnavailableExperienceClass>,
): List<String> {
    val codes = linkedSetOf<String>()
    if (unit.readiness == "NOT_PREPARED") codes += "RECORDED_NOT_PREPARED"
    if (unit.sessions.isEmpty() || unit.objectives.isEmpty() || unit.objectives.none { it.kind == "RECOGNITION" } ||
        unit.sessions.flatMap { it.steps }.none { it.kind == "TEACH" } ||
        errors.any {
            it.contains("book direction does not replace") ||
                it.contains("empty sessions or objectives") ||
                it.contains("has no teach step") ||
                (it.contains("not content-complete without") && !it.contains("source capabilit"))
        }
    ) {
        codes += "MISSING_INSTRUCTIONAL_STRAND"
    }
    unit.objectives.filter { it.kind == "RECOGNITION" }.forEach { objective ->
        if (objective.instructionStepIds.isEmpty()) codes += "MISSING_INSTRUCTION"
        if (objective.exampleIds.size < 2) codes += "MISSING_EXAMPLES"
        if (objective.practiceStepIds.isEmpty()) codes += "MISSING_GUIDED_PRACTICE"
        if (objective.checkItemIds.isEmpty()) codes += "MISSING_INDEPENDENT_CHECKS"
        if (objective.checkItemIds.any { pack.item(it)?.feedback.isNullOrBlank() }) codes += "MISSING_FEEDBACK"
        if (objective.remediationStepIds.isEmpty()) codes += "MISSING_REMEDIATION"
    }
    if (errors.any { it.contains("feedback") && "MISSING_FEEDBACK" !in codes && it.contains("no factual feedback") }) {
        codes += "MISSING_FEEDBACK"
    }
    if (unit.advancement == null) codes += "MISSING_OR_INVALID_ADVANCEMENT_RULE"
    if (unresolvedClaims.isNotEmpty() || errors.any { it.contains("source capabilities") }) {
        codes += "UNRESOLVED_REQUIRED_SOURCE_CAPABILITY"
    }
    if (UnavailableExperienceClass.SOURCE_SUPPORTED_CURRICULUM in unavailableClasses) {
        codes += "MISSING_SOURCE_SUPPORTED_CURRICULUM"
    }
    if (unit.objectives.any { it.kind != "RECOGNITION" && it.checkItemIds.isNotEmpty() } ||
        errors.any { it.contains("must not carry advancement checks") }
    ) {
        codes += "UNSUPPORTED_OBJECTIVE_HAS_ADVANCEMENT_CHECK"
    }
    if (unit.objectives.any { it.kind == "RECOGNITION" && it.gating && it.instructionStepIds.isEmpty() }) {
        codes += "UNTAUGHT_OBJECTIVE"
    }
    val explainedByOtherClass = UnavailableExperienceClass.LOCAL_EXPERIENCE in unavailableClasses ||
        UnavailableExperienceClass.LICENSED_OR_MAPPED_ASSET in unavailableClasses
    if (errors.isNotEmpty() && codes.isEmpty() && !explainedByOtherClass) {
        codes += "VALIDATOR_REJECTS_CONTENT_COMPLETE"
    }
    return codes.toList()
}

private fun linguisticCodes(
    pack: V2CurriculumPack,
    unit: V2Unit,
    reviewStatus: String,
    identityPresent: Boolean,
    qualificationPresent: Boolean,
    signed: Boolean,
    manifestText: String?,
    canonicalReviewDigest: String,
    curriculumCommit: String,
): List<String> {
    val codes = linkedSetOf<String>()
    when (reviewStatus) {
        "MISSING", "DUPLICATE" -> codes += "MISSING_UNIT_REVIEW"
        "PENDING_QUALIFIED_REVIEW" -> codes += "PENDING_REVIEW"
        "REJECTED" -> codes += "REJECTED_REVIEW"
        "APPROVED" -> Unit
        else -> codes += "PENDING_REVIEW"
    }
    if (!identityPresent) codes += "MISSING_REVIEWER_IDENTITY"
    if (!qualificationPresent) codes += "MISSING_QUALIFICATION"
    if (!signed) codes += "MISSING_MATCHING_REVIEWED_DIGEST"
    if (manifestText == null) {
        codes += "MISSING_REVIEW_PACKET"
    } else {
        val recordedDigest = jsonStringField(manifestText, "reviewPackageDigest")
        val recordedCommit = jsonStringField(manifestText, "curriculumSourceCommit")
        if (recordedDigest != canonicalReviewDigest) codes += "REVIEW_PACKAGE_DIGEST_MISMATCH"
        if (recordedCommit != curriculumCommit) codes += "CURRICULUM_SOURCE_COMMIT_MISMATCH"
    }
    if (unapprovedLinguisticRecords(pack, unit) > 0) codes += "REACHABLE_LINGUISTIC_RECORD_NOT_APPROVED"
    return codes.toList()
}

private fun mappingCodes(
    repositoryRoot: File,
    pack: V2CurriculumPack,
    unit: V2Unit,
    canonicalDigest: String,
): List<String> {
    val codes = linkedSetOf<String>()
    when (unit.mappingStatus) {
        "APPROVED" -> Unit
        "PENDING_CURRICULUM_REVIEWER" -> codes += "PENDING_MAPPING"
        "REJECTED" -> codes += "REJECTED_MAPPING"
        "" -> codes += "MISSING_MAPPING"
        else -> codes += "PENDING_MAPPING"
    }
    val facts = governedMappingFacts(unit.number)
    if (facts.mappingRecordId.isNullOrBlank() || facts.printedPageStart == null || facts.printedPageEnd == null ||
        facts.topicDescription.isNullOrBlank()
    ) {
        codes += "MISSING_PAGE_OR_TOPIC"
    }
    if (facts.isbnObservation != "OBSERVED" || facts.isbn.isNullOrBlank()) codes += "MISSING_EDITION_ISBN"
    val worksheet = File(repositoryRoot, "review/unit-${unit.number}/${reviewMappingName(unit.number)}")
    if (!worksheet.isFile) {
        codes += "MISSING_MAPPING_WORKSHEET"
        codes += "MISSING_OWNED_COPY_VERIFICATION"
        codes += "MISSING_VARIANCE_OR_COVERAGE_DISPOSITION"
    } else {
        val lines = worksheet.readLines()
        val commentDigest = lines.firstOrNull { it.startsWith("# mappingPackageDigest: ") }
            ?.removePrefix("# mappingPackageDigest: ")
            ?.trim()
        val header = lines.firstOrNull { it.startsWith("record_id\t") }?.split('\t')
        val data = if (header == null) emptyList() else lines.filter { it.isNotBlank() && !it.startsWith("#") && !it.startsWith("record_id\t") }
        if (commentDigest != canonicalDigest) codes += "DIGEST_MISMATCH"
        if (header == null || data.isEmpty()) {
            codes += "MISSING_MAPPING"
        } else {
            fun column(row: List<String>, name: String): String {
                val index = header.indexOf(name)
                return if (index < 0 || index >= row.size) "" else row[index]
            }
            val parsed = data.map { it.split('\t') }
            if (parsed.any { column(it, "owned_copy_verified").isBlank() }) codes += "MISSING_OWNED_COPY_VERIFICATION"
            if (parsed.any { column(it, "variance_conflict").isBlank() }) codes += "MISSING_VARIANCE_OR_COVERAGE_DISPOSITION"
            if (parsed.any { column(it, "isbn_observation") != "OBSERVED" || column(it, "isbn").isBlank() }) {
                codes += "MISSING_EDITION_ISBN"
            }
            if (parsed.any { column(it, "corresponding_pages").isBlank() || column(it, "corresponding_topic").isBlank() }) {
                codes += "MISSING_PAGE_OR_TOPIC"
            }
            if (parsed.any { column(it, "mapping_package_digest") != canonicalDigest }) codes += "DIGEST_MISMATCH"
            if (parsed.any { column(it, "alignment_disposition") != "APPROVED" && unit.mappingStatus == "APPROVED" }) {
                codes += "PENDING_MAPPING"
            }
        }
    }
    if (unit.mappingStatus == "APPROVED" && pack.ownedEditionMappings.none { it.unitNumber == unit.number && it.status == "APPROVED" }) {
        codes += "MISSING_MAPPING"
    }
    return codes.toList()
}

private fun assetCodes(
    pack: V2CurriculumPack,
    unit: V2Unit,
    unavailableClasses: Set<UnavailableExperienceClass>,
): List<String> {
    val codes = linkedSetOf<String>()
    if (V2Validator.installationRequiresPronunciationAudio(unit.number)) {
        val records = pack.audioAssets.filter { it.unitNumber == unit.number }
        if (!V2Validator.mappedPronunciationAudioResolves(pack, unit)) {
            if (records.isEmpty()) {
                codes += "REQUIRED_LICENSED_AUDIO_ABSENT"
            } else {
                codes += "REQUIRED_VERIFIED_AUDIO_TO_TEXT_MAPPING_ABSENT"
            }
        }
    }
    if (V2Validator.installationRequiresHandwritingAsset(unit.number) &&
        !V2Validator.verifiedHandwritingAssetResolves(pack, unit)
    ) {
        codes += "REQUIRED_HANDWRITING_ASSET_ABSENT"
    }
    if (UnavailableExperienceClass.LICENSED_OR_MAPPED_ASSET in unavailableClasses) {
        unit.sessions.flatMap { it.steps }.forEach { step ->
            if (UnavailableExperienceClass.LICENSED_OR_MAPPED_ASSET in unavailableExperienceClasses(step.kind, step.unavailableReason)) {
                codes += assetRecordCode(step.unavailableReason.orEmpty())
            }
        }
    }
    return codes.toList()
}

private fun admittedSourceIds(pack: V2CurriculumPack, unit: V2Unit): Set<String> {
    val ids = linkedSetOf<String>()
    pack.provenanceRecords.filter { it.unitNumber == unit.number }.forEach { ids += it.sourceId }
    val exampleIds = unit.objectives.flatMap { it.exampleIds } + unit.sessions.flatMap { session -> session.steps.flatMap { it.exampleIds } }
    exampleIds.forEach { id -> pack.example(id)?.sourceId?.let { ids += it } }
    val itemIds = unit.objectives.flatMap { it.checkItemIds } + unit.sessions.flatMap { session -> session.steps.flatMap { it.itemIds } }
    itemIds.forEach { id -> pack.item(id)?.sourceId?.let { ids += it } }
    exampleIds.mapNotNull { pack.example(it)?.letterId }.forEach { id -> pack.letter(id)?.sourceId?.let { ids += it } }
    unit.sessions.flatMap { session -> session.steps.flatMap { it.referenceIds } }.forEach { id ->
        pack.references.firstOrNull { it.id == id }?.sourceId?.let { ids += it }
    }
    return ids.filter { sourceId -> pack.sources.any { it.id == sourceId } }.toSet()
}

private fun unapprovedLinguisticRecords(pack: V2CurriculumPack, unit: V2Unit): Int {
    var count = 0
    val exampleIds = (unit.objectives.flatMap { it.exampleIds } + unit.sessions.flatMap { it.steps.flatMap { step -> step.exampleIds } }).toSet()
    val itemIds = (unit.objectives.flatMap { it.checkItemIds } + unit.sessions.flatMap { it.steps.flatMap { step -> step.itemIds } }).toSet()
    exampleIds.forEach { id ->
        val example = pack.example(id) ?: return@forEach
        if (example.reviewStatus != "APPROVED") count += 1
    }
    itemIds.forEach { id ->
        val item = pack.item(id) ?: return@forEach
        if (item.reviewStatus != "APPROVED") count += 1
    }
    exampleIds.mapNotNull { pack.example(it)?.letterId?.takeIf(String::isNotBlank) }.toSet().forEach { id ->
        val letter = pack.letter(id) ?: return@forEach
        if (letter.reviewStatus != "APPROVED") count += 1
    }
    unit.sessions.flatMap { it.steps.flatMap { step -> step.referenceIds } }.toSet().forEach { id ->
        val reference = pack.references.firstOrNull { it.id == id } ?: return@forEach
        val allowedSkip = reference.reviewStatus == "NOT_REQUIRED" &&
            reference.classification in NON_LINGUISTIC_REFERENCE_CLASSIFICATIONS
        if (reference.reviewStatus != "APPROVED" && !allowedSkip) count += 1
    }
    return count
}

internal fun idealizedLearner(pack: V2CurriculumPack, unit: V2Unit): V2LearnerView {
    val steps = unit.sessions.flatMap { it.steps }
    val items = (steps.flatMap { it.itemIds } + unit.objectives.flatMap { it.checkItemIds }).toSet()
    return V2LearnerView(
        onboardingCompleted = true,
        currentUnitNumber = unit.number,
        startingPoint = V2StartingPoint.NEW_TO_CYRILLIC,
        hasPenguin = true,
        hasAnki = false,
        hasBinder = false,
        hasTutor = false,
        dailyMinutes = 25,
        transliterationEnabled = false,
        currentSessionId = unit.sessions.lastOrNull()?.id,
        currentStepIndex = 0,
        completedStepIds = steps.map { it.id }.toSet(),
        correctItemIds = items,
        bookAttestedUnits = setOf(unit.number),
        completedUnitNumbers = emptySet(),
    )
}

private fun disposition(
    unit: V2Unit,
    reviewStatus: String,
    canAdvance: Boolean,
    advancementInconsistent: Boolean,
    claimsInstall: Boolean,
    installApproved: Boolean,
    content: String,
    linguistic: String,
    mapping: String,
    asset: String,
    localExperience: String,
    variance: String,
): String {
    val categories = listOfNotNull(
        if (content != "NONE") "CONTENT" else null,
        if (linguistic != "NONE") "LINGUISTIC_REVIEW" else null,
        if (mapping != "NONE") "OWNED_EDITION_MAPPING" else null,
        if (asset != "NONE") "ASSET" else null,
        if (localExperience != "NONE") "LOCAL_EXPERIENCE" else null,
        if (variance != "NOT_APPLICABLE") "AUTHORITY_VARIANCE" else null,
    )
    val claim = when {
        advancementInconsistent -> "INVALID_ADVANCEMENT_READINESS_INCONSISTENCY"
        claimsInstall && !installApproved -> "INVALID_INSTALL_OR_READINESS_CLAIM"
        else -> "RECORDED_STATE"
    }
    return "$claim;install=${unit.installState};readiness=${unit.readiness};review=$reviewStatus;" +
        "mapping=${unit.mappingStatus};advancement=${if (canAdvance) "YES" else "NO"};" +
        "categories=${categories.joinToString("+").ifEmpty { "NONE" }}"
}

private fun manifestText(repositoryRoot: File, unitNumber: Int, overrides: Map<Int, String?>?): String? {
    if (overrides != null && overrides.containsKey(unitNumber)) return overrides[unitNumber]
    val file = File(repositoryRoot, "review/unit-$unitNumber/${reviewManifestName(unitNumber)}")
    val expected = reviewFileNames(unitNumber)
    if (!file.isFile || expected.any { name -> !File(repositoryRoot, "review/unit-$unitNumber/$name").isFile }) {
        return null
    }
    return file.readText()
}

private fun nextWork(model: CurriculumReadinessReconciliation): List<ReadinessNextWork> {
    val sourceUnits = model.units.filter { row ->
        row.readiness == "NOT_PREPARED" ||
            row.unresolvedRequiredSourceCapabilities != "NONE" ||
            row.contentBlocker.split(", ").any {
                it in setOf(
                    "MISSING_INSTRUCTIONAL_STRAND",
                    "MISSING_INSTRUCTION",
                    "MISSING_EXAMPLES",
                    "MISSING_GUIDED_PRACTICE",
                    "MISSING_INDEPENDENT_CHECKS",
                    "MISSING_FEEDBACK",
                    "MISSING_REMEDIATION",
                    "UNRESOLVED_REQUIRED_SOURCE_CAPABILITY",
                    "MISSING_SOURCE_SUPPORTED_CURRICULUM",
                    "RECORDED_NOT_PREPARED",
                )
            }
    }
    val audioUnits = model.units.filter { V2Validator.installationRequiresPronunciationAudio(it.unitNumber) }
    val handwritingUnits = model.units.filter { V2Validator.installationRequiresHandwritingAsset(it.unitNumber) }
    val varianceUnits = model.units.filter { it.authorityVarianceBlocker != "NOT_APPLICABLE" }
    val reviewUnits = model.units.filter { it.unitReviewStatus != "APPROVED" || it.signedReviewedDigest != "PRESENT" }
    val mappingUnits = model.units.filter { it.mappingStatus != "APPROVED" }
    return listOf(
        ReadinessNextWork(
            id = "source-supported-curriculum-gaps",
            title = "Source-supported curriculum gaps",
            units = sourceUnits.map { it.unitNumber },
            requiredInput = sourceInput(sourceUnits),
        ),
        ReadinessNextWork(
            id = "missing-licensed-audio",
            title = "Missing licensed audio",
            units = audioUnits.map { it.unitNumber },
            requiredInput = "Reviewer-mapped licensed pronunciation audio for Units 1-2, each record bound to a reachable letter, with a resolving source, rights record, and approved intake status. mappedPronunciationAudio and any other admissions counter do not clear a missing record.",
        ),
        ReadinessNextWork(
            id = "missing-handwriting-assets",
            title = "Missing handwriting assets",
            units = handwritingUnits.map { it.unitNumber },
            requiredInput = "A verified handwriting asset for Unit 2, bound to a reachable letter, with a resolving source, rights record, and approved intake status. The handwritingAssets counter does not clear a missing record.",
        ),
        ReadinessNextWork(
            id = "required-local-experience-unavailable",
            title = "Required local experience unavailable",
            units = model.units.filter { it.localExperienceBlocker != "NONE" }.map { it.unitNumber },
            requiredInput = "A required local experience that the packaged step marks unavailable for a reason other than a missing admitted source and other than a missing licensed or mapped asset record. An UNAVAILABLE step whose reason is a missing frozen span stays in the source-supported curriculum group. It does not create an asset blocker.",
        ),
        ReadinessNextWork(
            id = "unresolved-authority-variances",
            title = "Unresolved authority variances",
            units = varianceUnits.map { it.unitNumber },
            requiredInput = "A written Product Owner decision and a qualified-reviewer decision. Current proposal status is ${model.varianceProposalStatus}. Current proposed-resolution status is ${model.varianceResolutionStatus}. Production validation applies this unactivated variance to Units 27, 29, and 30 only. This report does not activate either proposal.",
        ),
        ReadinessNextWork(
            id = "qualified-linguistic-review",
            title = "Qualified linguistic review",
            units = reviewUnits.map { it.unitNumber },
            requiredInput = "A qualified human reviewer identity, qualification, review date, and signed reviewed digest matching that unit's content-review digest. Units 18, 20, and 23 also need a generated review packet before a signature can bind to one. An independent curriculum audit, including a future Claude audit, cannot supply that signature.",
        ),
        ReadinessNextWork(
            id = "owned-edition-mapping-review",
            title = "Owned-edition mapping review",
            units = mappingUnits.map { it.unitNumber },
            requiredInput = "Owned-copy verification, an observed ISBN or a still-unresolved ISBN observation left unresolved until the identifier is seen, printed page and topic confirmation, and a variance or coverage disposition signed by the mapping reviewer. Pending disposition is not approval. Units 18, 20, and 23 still need their mapping worksheets committed with the review packet.",
        ),
        ReadinessNextWork(
            id = "final-curriculum-audit-preparation",
            title = "Final curriculum audit preparation",
            units = (1..30).toList(),
            requiredInput = "An independent curriculum audit after the source, asset, variance, linguistic-review, and mapping inputs above exist. The audit may inspect this reconciliation. It does not fabricate a human approval.",
        ),
        ReadinessNextWork(
            id = "physical-acceptance-and-release-governance",
            title = "Physical acceptance and release governance",
            units = emptyList(),
            requiredInput = "Separate from curriculum readiness: freeze the candidate, retain executed AT-001 through AT-030 evidence, execute AT-031, and record a public-release decision. Current release decision is ${model.releaseDecision}. AT-031 is ${model.at031}. Public release is ${model.publicRelease}.",
        ),
    )
}

private fun sourceInput(rows: List<ReadinessUnitRow>): String {
    if (rows.isEmpty()) return "NONE"
    return rows.joinToString(" ") { row ->
        val claims = row.unresolvedRequiredSourceCapabilities
        val topic = if (row.readiness == "NOT_PREPARED" && row.completeInstructionalChain) {
            " Recorded topic: ${row.recordedTopicSummary}"
        } else {
            ""
        }
        "Unit ${row.unitNumber}: content ${row.contentBlocker}; unresolved capabilities $claims.$topic"
    }
}

private fun globalRows(model: CurriculumReadinessReconciliation): List<Pair<String, String>> {
    val units = model.units
    return listOf(
        "unitsByInstallState" to countLabel(units.groupingBy { it.installState }.eachCount()),
        "unitsByReadiness" to countLabel(units.groupingBy { it.readiness }.eachCount()),
        "unitsByMappingState" to countLabel(units.groupingBy { it.mappingStatus }.eachCount()),
        "unitsByReviewState" to countLabel(units.groupingBy { it.unitReviewStatus }.eachCount()),
        "unitsWithCompleteInstructionalChains" to units.count { it.completeInstructionalChain }.toString(),
        "unitsWithUnresolvedSourceCapabilities" to units.count { it.unresolvedRequiredSourceCapabilities != "NONE" }.toString(),
        "unitsWithApplicableUnresolvedAuthorityVariances" to units.count { it.authorityVarianceBlocker != "NOT_APPLICABLE" }.toString(),
        "unitsLackingReviewPacket" to units.count { it.reviewPackageDigest == "NONE" }.toString(),
        "unitsLackingAdvancementRule" to units.count { it.advancementRuleState == "NONE" }.toString(),
        "unitsRequiringAudio" to units.count { V2Validator.installationRequiresPronunciationAudio(it.unitNumber) }.toString(),
        "unitsRequiringHandwritingAssets" to units.count { V2Validator.installationRequiresHandwritingAsset(it.unitNumber) }.toString(),
        "totalObjectives" to units.sumOf { it.objectiveCount }.toString(),
        "totalSessions" to units.sumOf { it.sessionCount }.toString(),
        "totalInstructionSteps" to units.sumOf { it.instructionStepCount }.toString(),
        "totalExamples" to units.sumOf { it.exampleCount }.toString(),
        "totalGuidedPractice" to units.sumOf { it.guidedPracticeCount }.toString(),
        "totalIndependentChecks" to units.sumOf { it.independentCheckCount }.toString(),
        "totalRemediationEdges" to units.sumOf { it.remediationCount }.toString(),
        "totalProvenanceRecords" to units.sumOf { it.provenanceRecordCount }.toString(),
        "totalAuthoredEnglishRecords" to units.sumOf { it.authoredEnglishRecordCount }.toString(),
        "totalAdmittedSources" to units.sumOf { it.admittedSourceCount }.toString(),
        "totalRightsRecords" to units.sumOf { it.rightsRecordCount }.toString(),
        "distinctAdmittedSources" to model.distinctAdmittedSources.toString(),
        "distinctRightsRecords" to model.distinctRightsRecords.toString(),
        "crossUnitSourceRecords" to model.crossUnitSourceRecords.toString(),
        "totalSourceCapabilities" to units.sumOf { it.sourceCapabilityCount }.toString(),
        "totalResolvedSourceCapabilities" to units.sumOf { it.resolvedSourceCapabilityCount }.toString(),
        "totalMappedAudioRecords" to units.sumOf { it.mappedAudioCount }.toString(),
        "totalHandwritingAssetRecords" to units.sumOf { it.handwritingAssetCount }.toString(),
        "generatedRussianAdmissionCount" to model.generatedRussianAdmissionCount.toString(),
        "itemsMarkedContainsGeneratedRussian" to model.itemsMarkedContainsGeneratedRussian.toString(),
        "penguinProseAdmissionCount" to model.penguinProseAdmissionCount.toString(),
        "knownPenguinTitleMarkerMatches" to model.knownPenguinTitleMarkerMatches.toString(),
        "packagedAudioBoundToV2Units" to model.packagedAudioBoundToV2Units.toString(),
        "packagedExampleRecords" to model.packagedExampleRecords.toString(),
        "reachableExampleRecords" to model.reachableExampleRecords.toString(),
        "sharedExampleRecords" to model.sharedExampleRecords.toString(),
        "exampleCrossUnitReferenceExcess" to model.exampleCrossUnitReferenceExcess.toString(),
        "exampleDuplicateUnitReferenceExcess" to model.exampleDuplicateUnitReferenceExcess.toString(),
        "orphanedExampleRecords" to model.orphanedExampleRecords.toString(),
        "orphanedExampleIds" to model.orphanedExampleIds,
        "packagedCheckItems" to model.packagedCheckItems.toString(),
        "reachableCheckItems" to model.reachableCheckItems.toString(),
        "sharedCheckItems" to model.sharedCheckItems.toString(),
        "checkItemCrossUnitReferenceExcess" to model.checkItemCrossUnitReferenceExcess.toString(),
        "checkItemDuplicateUnitReferenceExcess" to model.checkItemDuplicateUnitReferenceExcess.toString(),
        "orphanedCheckItems" to model.orphanedCheckItems.toString(),
        "orphanedCheckItemIds" to model.orphanedCheckItemIds,
        "internetPermissionDeclaredInMainManifest" to model.internetPermissionDeclaredInMainManifest,
        "internetPermissionDeclaredInMergedDebugManifest" to model.internetPermissionDeclaredInMergedDebugManifest,
        "packContentReviewDigest" to model.packContentReviewDigest,
        "repositorySourceDigest" to model.repositorySourceDigest,
        "candidateFrozen" to model.candidateFrozen.toString(),
        "candidateArtifact" to model.candidateArtifact,
        "candidateSourceCommit" to model.candidateSourceCommit,
        "publicRelease" to model.publicRelease,
        "retainedEvidence" to model.retainedEvidence,
        "at031" to model.at031,
        "releaseDecision" to model.releaseDecision,
        "releaseGateBlockers" to if (model.releaseGateBlockers.isEmpty()) "NONE" else model.releaseGateBlockers.joinToString(", "),
        "varianceProposalStatus" to model.varianceProposalStatus,
        "varianceResolutionStatus" to model.varianceResolutionStatus,
    )
}

private fun blockerTotals(model: CurriculumReadinessReconciliation): List<Pair<String, Int>> {
    val units = model.units
    return listOf(
        "CONTENT" to units.count { it.contentBlocker != "NONE" },
        "LINGUISTIC_REVIEW" to units.count { it.linguisticReviewBlocker != "NONE" },
        "OWNED_EDITION_MAPPING" to units.count { it.mappingBlocker != "NONE" },
        "ASSET" to units.count { it.assetBlocker != "NONE" },
        "LOCAL_EXPERIENCE" to units.count { it.localExperienceBlocker != "NONE" },
        "AUTHORITY_VARIANCE" to units.count { it.authorityVarianceBlocker != "NOT_APPLICABLE" },
    )
}

private fun unitJson(row: ReadinessUnitRow): String {
    val fields = CURRICULUM_READINESS_COLUMNS.map { (key, _) ->
        val raw = matrixValue(row, key)
        val encoded = if (key.endsWith("Count") || key == "unitNumber") raw else json(raw)
        "${json(key)}: $encoded"
    }
    return "{${fields.joinToString(", ")}}"
}

private fun countLabel(counts: Map<String, Int>): String =
    counts.toSortedMap().entries.joinToString(", ") { (key, value) -> "$key=$value" }

private fun joinCodes(codes: List<String>): String = if (codes.isEmpty()) "NONE" else codes.distinct().joinToString(", ")

private fun sourceOwners(pack: V2CurriculumPack): Map<String, Set<Int>> {
    val owners = linkedMapOf<String, MutableSet<Int>>()
    pack.units.forEach { unit ->
        admittedSourceIds(pack, unit).forEach { id ->
            owners.getOrPut(id) { linkedSetOf() }.add(unit.number)
        }
    }
    return owners
}

private fun distinctAdmittedSources(pack: V2CurriculumPack): Set<String> = sourceOwners(pack).keys

private fun crossUnitSources(pack: V2CurriculumPack): Int = sourceOwners(pack).values.count { it.size > 1 }

private fun distinctRightsRecords(pack: V2CurriculumPack): Int {
    val sourceIds = distinctAdmittedSources(pack)
    return pack.rights.count { it.sourceId in sourceIds }
}

internal data class AdvancementReport(
    val canPresentlySucceed: Boolean,
    val readinessInconsistent: Boolean,
)

/**
 * A NOT_PREPARED unit is never reported as able to advance. If a runtime
 * regression allows that unit anyway, the report keeps advancement at NO and
 * marks the readiness inconsistency.
 */
internal fun reportAdvancement(readiness: String, runtimeAllowed: Boolean): AdvancementReport {
    val inconsistent = readiness == "NOT_PREPARED" && runtimeAllowed
    return AdvancementReport(
        canPresentlySucceed = runtimeAllowed && readiness != "NOT_PREPARED",
        readinessInconsistent = inconsistent,
    )
}

internal enum class UnavailableExperienceClass {
    SOURCE_SUPPORTED_CURRICULUM,
    LICENSED_OR_MAPPED_ASSET,
    LOCAL_EXPERIENCE,
}

private val SOURCE_GAP_MARKERS = listOf(
    "no admitted",
    "admitted frozen",
    "instructional source",
    "does not by itself support",
    "not that source",
)

private val ASSET_GAP_MARKERS = listOf(
    "licensed audio",
    "pronunciation audio",
    "handwriting asset",
    "handwriting record",
    "audio record",
    "mapped audio",
    "audio-to-text",
    "audio to text",
)

internal fun unavailableExperienceClasses(kind: String, unavailableReason: String?): Set<UnavailableExperienceClass> {
    if (kind != "UNAVAILABLE" && unavailableReason.isNullOrBlank()) return emptySet()
    val text = unavailableReason.orEmpty().lowercase()
    val classes = linkedSetOf<UnavailableExperienceClass>()
    if (SOURCE_GAP_MARKERS.any { it in text }) classes += UnavailableExperienceClass.SOURCE_SUPPORTED_CURRICULUM
    if (ASSET_GAP_MARKERS.any { it in text }) classes += UnavailableExperienceClass.LICENSED_OR_MAPPED_ASSET
    if (classes.isEmpty()) classes += UnavailableExperienceClass.LOCAL_EXPERIENCE
    return classes
}

private fun assetRecordCode(reason: String): String {
    val text = reason.lowercase()
    return when {
        "handwriting" in text -> "REQUIRED_HANDWRITING_ASSET_ABSENT"
        "audio-to-text" in text || "audio to text" in text -> "REQUIRED_VERIFIED_AUDIO_TO_TEXT_MAPPING_ABSENT"
        else -> "REQUIRED_LICENSED_AUDIO_ABSENT"
    }
}

internal data class CatalogReachability(
    val packaged: Int,
    val reachable: Int,
    val sharedAcrossUnits: Int,
    val crossUnitReferenceExcess: Int,
    val duplicateUnitReferenceExcess: Int,
    val orphaned: Int,
    val orphanIds: List<String>,
)

internal fun catalogReachability(
    catalogIds: Set<String>,
    referencesByUnit: Map<Int, List<String>>,
): CatalogReachability {
    val mentions = linkedMapOf<String, MutableMap<Int, Int>>()
    referencesByUnit.forEach { (unitNumber, ids) ->
        ids.forEach { id ->
            val perUnit = mentions.getOrPut(id) { linkedMapOf() }
            perUnit[unitNumber] = (perUnit[unitNumber] ?: 0) + 1
        }
    }
    val inCatalog = mentions.filterKeys { it in catalogIds }
    val orphanIds = catalogIds.filter { it !in mentions }.sorted()
    return CatalogReachability(
        packaged = catalogIds.size,
        reachable = inCatalog.size,
        sharedAcrossUnits = inCatalog.values.count { it.size >= 2 },
        crossUnitReferenceExcess = inCatalog.values.sumOf { (it.size - 1).coerceAtLeast(0) },
        duplicateUnitReferenceExcess = inCatalog.values.sumOf { perUnit ->
            perUnit.values.sumOf { (it - 1).coerceAtLeast(0) }
        },
        orphaned = orphanIds.size,
        orphanIds = orphanIds,
    )
}

private fun exampleReferences(pack: V2CurriculumPack): Map<Int, List<String>> =
    pack.units.associate { unit ->
        unit.number to (
            unit.objectives.flatMap { it.exampleIds } +
                unit.sessions.flatMap { session -> session.steps.flatMap { it.exampleIds } }
            )
    }

private fun checkItemReferences(pack: V2CurriculumPack): Map<Int, List<String>> =
    pack.units.associate { unit ->
        unit.number to (
            unit.objectives.flatMap { it.checkItemIds } +
                unit.sessions.flatMap { session -> session.steps.flatMap { it.itemIds } }
            )
    }

private fun idList(ids: List<String>): String = if (ids.isEmpty()) "NONE" else ids.joinToString(", ")

internal fun itemsMarkedContainsGeneratedRussian(pack: V2CurriculumPack): Int =
    pack.items.count { it.containsGeneratedRussian }

internal fun knownPenguinTitleMarkerMatches(pack: V2CurriculumPack): Int = penguinMarkerCount(pack)

internal fun internetPermissionDeclaredInManifestText(manifest: String): String =
    if (manifest.contains("android.permission.INTERNET")) "PRESENT" else "ABSENT"

internal fun mergedDebugManifestFile(repositoryRoot: File): File =
    File(
        repositoryRoot,
        "app/build/intermediates/merged_manifest/debug/processDebugMainManifest/AndroidManifest.xml",
    )

private fun penguinMarkerCount(pack: V2CurriculumPack): Int {
    val marker = "the new penguin"
    var count = 0
    fun scan(text: String?) {
        if (text != null && marker in text.lowercase()) count += 1
    }
    pack.units.forEach { unit ->
        scan(unit.title)
        scan(unit.topicSummary)
        scan(unit.mappingNote)
        unit.objectives.forEach { scan(it.title) }
        unit.sessions.forEach { session ->
            scan(session.title)
            session.steps.forEach { step ->
                scan(step.title)
                scan(step.body)
                scan(step.bookDirection)
                scan(step.companionDirection)
            }
        }
    }
    pack.examples.forEach {
        scan(it.display)
        scan(it.note)
    }
    pack.items.forEach {
        scan(it.prompt)
        scan(it.answer)
        scan(it.feedback)
        scan(it.promptLetter)
    }
    pack.provenanceRecords.forEach { scan(it.shippedText) }
    pack.references.forEach {
        scan(it.title)
        scan(it.summary)
        scan(it.detail)
    }
    return count
}

private fun proposalStatus(file: File): String {
    if (!file.isFile) return "MISSING"
    val line = file.readLines().firstOrNull { it.startsWith("Status:") } ?: return "UNREADABLE"
    return line.removePrefix("Status:").trim().ifBlank { "UNREADABLE" }
}

private fun at031State(candidateText: String, blockerNames: List<String>): String {
    val unexecuted = Regex(""""unexecutedProcedures"\s*:\s*\[(.*?)]""", RegexOption.DOT_MATCHES_ALL)
        .find(candidateText)
        ?.groupValues
        ?.get(1)
        .orEmpty()
    val listed = Regex(""""(AT-\d+)"""").findAll(unexecuted).map { it.groupValues[1] }.toSet()
    return if ("AT-031" in listed || "OWNER_RELEASE_DECISION_NOT_RETAINED" in blockerNames) {
        "UNEXECUTED"
    } else {
        "NOT_LISTED"
    }
}

private fun jsonStringField(text: String, name: String): String? =
    Regex(""""$name"\s*:\s*"([^"]*)"""").find(text)?.groupValues?.get(1)

private fun escapeCell(value: String): String = value.replace("|", "/").replace("\n", " ")

private fun json(value: String): String = buildString {
    append('"')
    value.forEach { char ->
        when (char) {
            '\\' -> append("\\\\")
            '"' -> append("\\\"")
            '\n' -> append("\\n")
            '\r' -> append("\\r")
            '\t' -> append("\\t")
            else -> if (char.code < 0x20) append("\\u%04x".format(char.code)) else append(char)
        }
    }
    append('"')
}

private fun jsonArray(values: List<String>): String = values.joinToString(prefix = "[", postfix = "]") { json(it) }
