package dev.russiancompanion.v2.review

import dev.russiancompanion.v2.curriculum.V2CheckItem
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2Example
import dev.russiancompanion.v2.curriculum.V2Letter
import dev.russiancompanion.v2.curriculum.V2Objective
import dev.russiancompanion.v2.curriculum.V2ReferenceEntry
import dev.russiancompanion.v2.curriculum.V2Session
import dev.russiancompanion.v2.curriculum.V2Step
import dev.russiancompanion.v2.curriculum.V2Unit
import dev.russiancompanion.release.currentReleaseBinding
import dev.russiancompanion.v2.curriculum.V2Validator
import java.io.File
import dev.russiancompanion.v2.curriculum.example
import dev.russiancompanion.v2.curriculum.item
import dev.russiancompanion.v2.curriculum.letter
import dev.russiancompanion.v2.curriculum.reference
import dev.russiancompanion.v2.curriculum.step
import dev.russiancompanion.v2.curriculum.unit
import java.security.MessageDigest

internal const val UNIT1_REVIEW_SCHEMA_VERSION = 3

/**
 * Commit that contains the curriculum, validators, and review pipeline.
 * Review packets are generated in a later commit, so this value is not HEAD.
 */
internal const val CURRICULUM_SOURCE_COMMIT = "fc73429d91edb6c8a84924a42d2edc7f591b0c8f"

internal const val UNIT1_REVIEW_SOURCE_COMMIT = CURRICULUM_SOURCE_COMMIT

internal const val REVIEW_SOURCE_COMMIT = CURRICULUM_SOURCE_COMMIT

internal fun reviewPacketName(unitNumber: Int) = "UNIT_${unitNumber}_REVIEW_PACKET.md"

internal fun reviewManifestName(unitNumber: Int) = "UNIT_${unitNumber}_REVIEW_MANIFEST.json"

internal fun reviewWorksheetName(unitNumber: Int) = "UNIT_${unitNumber}_REVIEW_WORKSHEET.tsv"

internal fun reviewMappingName(unitNumber: Int) = "UNIT_${unitNumber}_OWNED_EDITION_MAPPING_WORKSHEET.tsv"

internal fun reviewFileNames(unitNumber: Int) = listOf(
    reviewPacketName(unitNumber),
    reviewManifestName(unitNumber),
    reviewWorksheetName(unitNumber),
    reviewMappingName(unitNumber),
    UNIT1_INSTRUCTIONS_NAME,
)

internal fun reviewPackageId(unitNumber: Int) = "unit-$unitNumber-linguistic-and-mapping-review"

internal const val UNIT1_REVIEW_APPLICATION_ID = "dev.russiancompanion"

internal const val UNIT1_REVIEW_VERSION_NAME = "2.0.0-dev"

internal const val UNIT1_REVIEW_VERSION_CODE = 5

internal const val UNIT1_PACKET_NAME = "UNIT_1_REVIEW_PACKET.md"

internal const val UNIT1_MANIFEST_NAME = "UNIT_1_REVIEW_MANIFEST.json"

internal const val UNIT1_WORKSHEET_NAME = "UNIT_1_REVIEW_WORKSHEET.tsv"

internal const val UNIT1_MAPPING_NAME = "UNIT_1_OWNED_EDITION_MAPPING_WORKSHEET.tsv"

internal const val UNIT1_INSTRUCTIONS_NAME = "REVIEWER_INSTRUCTIONS.md"

internal val UNIT1_REVIEW_FILE_NAMES = listOf(
    UNIT1_PACKET_NAME,
    UNIT1_MANIFEST_NAME,
    UNIT1_WORKSHEET_NAME,
    UNIT1_MAPPING_NAME,
    UNIT1_INSTRUCTIONS_NAME,
)

private val WORKSHEET_HEADER = listOf(
    "record_id",
    "record_type",
    "objective_ids",
    "session_ids",
    "content",
    "source_id",
    "rights_ids",
    "classification",
    "current_review_status",
    "content_hash",
    "disposition",
    "correction_comment",
    "reviewer_identity",
    "review_date",
    "reviewed_version_digest",
    "reviewer_qualification",
)

private val MAPPING_HEADER = listOf(
    "record_id",
    "record_type",
    "app_title",
    "edition_identification",
    "isbn",
    "publisher_edition",
    "owned_copy_verified",
    "corresponding_lesson",
    "corresponding_pages",
    "corresponding_topic",
    "alignment_disposition",
    "variance_conflict",
    "reviewer_notes",
    "reviewer_identity",
    "review_date",
    "reviewed_version_digest",
    "reviewer_qualification",
)

private val BLANK_REVIEW_COLUMNS = listOf(
    "disposition",
    "correction_comment",
    "reviewer_identity",
    "review_date",
    "reviewed_version_digest",
    "reviewer_qualification",
)

private val BLANK_MAPPING_COLUMNS = listOf(
    "edition_identification",
    "isbn",
    "publisher_edition",
    "owned_copy_verified",
    "corresponding_lesson",
    "corresponding_pages",
    "corresponding_topic",
    "alignment_disposition",
    "variance_conflict",
    "reviewer_notes",
    "reviewer_identity",
    "review_date",
    "reviewed_version_digest",
    "reviewer_qualification",
)

internal data class Unit1ReviewRecord(
    val id: String,
    val type: String,
    val objectiveIds: List<String>,
    val sessionIds: List<String>,
    val content: String,
    val sourceId: String,
    val rightsIds: List<String>,
    val classification: String,
    val reviewStatus: String,
    val contentHash: String,
)

internal data class Unit1ReviewEdge(
    val fromId: String,
    val toId: String,
    val kind: String,
)

internal data class Unit1ReviewPackage(
    val unitNumber: Int,
    val contentReviewDigest: String,
    val packContentReviewDigest: String,
    val unitReviewStatus: String,
    val reviewPackageDigest: String,
    val repositorySourceDigest: String,
    val records: List<Unit1ReviewRecord>,
    val edges: List<Unit1ReviewEdge>,
    val unresolved: List<String>,
    val bookDirectedIds: List<String>,
    val unavailableIds: List<String>,
)

internal data class Unit1ReviewFiles(
    val files: Map<String, String>,
)

internal fun buildUnit1ReviewPackage(pack: V2CurriculumPack): Unit1ReviewPackage = buildUnitReviewPackage(pack, 1)

internal fun buildUnitReviewPackage(pack: V2CurriculumPack, unitNumber: Int): Unit1ReviewPackage {
    val unit = pack.unit(unitNumber)
    val unresolved = mutableListOf<String>()
    val records = mutableListOf<Unit1ReviewRecord>()
    val seen = linkedSetOf<String>()
    val edges = mutableListOf<Unit1ReviewEdge>()
    val bookDirected = mutableListOf<String>()
    val unavailable = mutableListOf<String>()
    if (unitNumber !in 1..30) {
        unresolved += "Unit $unitNumber is outside Units 1-30"
    }
    if (unit == null) {
        unresolved += "Unit $unitNumber is missing from the curriculum pack"
        val instructional = V2Validator.unitContentReviewDigest(pack, unitNumber)
        val distinctUnresolved = unresolved.distinct()
        return Unit1ReviewPackage(
            unitNumber = unitNumber,
            contentReviewDigest = instructional,
            packContentReviewDigest = V2Validator.contentReviewDigest(pack),
            unitReviewStatus = "MISSING",
            reviewPackageDigest = reviewPackageDigest(
                unitNumber,
                "MISSING",
                "",
                "",
                "MISSING",
                instructional,
                emptyList(),
                emptyList(),
                emptyList(),
                emptyList(),
                distinctUnresolved,
            ),
            repositorySourceDigest = repositorySourceDigest(),
            records = emptyList(),
            edges = emptyList(),
            unresolved = distinctUnresolved,
            bookDirectedIds = emptyList(),
            unavailableIds = emptyList(),
        )
    }
    fun add(record: Unit1ReviewRecord) {
        if (!seen.add(record.id)) {
            unresolved += "Curriculum id ${record.id} is emitted more than once before deduplication"
            return
        }
        records += record
    }
    fun addEdge(edge: Unit1ReviewEdge) {
        if (edges.any { it == edge }) {
            unresolved += "Relationship ${edge.fromId} ${edge.kind} ${edge.toId} is duplicated"
            return
        }
        edges += edge
    }
    val unitReviewMatches = pack.unitReviews.filter { it.unitNumber == unitNumber }
    val unitReviewStatus = when (unitReviewMatches.size) {
        0 -> {
            unresolved += "Unit $unitNumber review record is missing"
            "MISSING"
        }
        1 -> {
            add(unitReviewRecord(unitReviewMatches.first()))
            unitReviewMatches.first().status
        }
        else -> {
            unresolved += "Unit $unitNumber has more than one review record"
            "DUPLICATE"
        }
    }
    add(unitRecord(unit))
    val advancement = unit.advancement
    if (advancement == null) {
        unresolved += "Unit $unitNumber advancement rule is missing"
    } else {
        add(
            record(
                id = "unit-$unitNumber-advancement",
                type = "ADVANCEMENT",
                objectiveIds = emptyList(),
                sessionIds = emptyList(),
                content = listOf(
                    "requireInstruction=${advancement.requireInstruction}",
                    "requireBookSelfAttestation=${advancement.requireBookSelfAttestation}",
                    "requireAppChecks=${advancement.requireAppChecks}",
                    "minimumDistinctChecks=${advancement.minimumDistinctChecks}",
                    "requireDueReview=${advancement.requireDueReview}",
                    "tutorOptional=${advancement.tutorOptional}",
                ).joinToString("\n"),
                sourceId = "",
                rightsIds = emptyList(),
                classification = "",
                reviewStatus = "NO_SEPARATE_REVIEW_FIELD",
            ),
        )
    }
    val objectiveOrder = unit.objectives.map { it.id }
    unit.objectives.forEach { objective ->
        add(objectiveRecord(objective))
        objective.instructionStepIds.forEach { stepId ->
            addEdge(Unit1ReviewEdge(objective.id, stepId, "INSTRUCTION"))
            if (pack.step(stepId) == null) unresolved += "Objective ${objective.id} instruction $stepId cannot be resolved"
        }
        objective.exampleIds.forEach { exampleId ->
            addEdge(Unit1ReviewEdge(objective.id, exampleId, "EXAMPLE"))
            if (pack.example(exampleId) == null) unresolved += "Objective ${objective.id} example $exampleId cannot be resolved"
        }
        objective.practiceStepIds.forEach { stepId ->
            addEdge(Unit1ReviewEdge(objective.id, stepId, "PRACTICE"))
            if (pack.step(stepId) == null) unresolved += "Objective ${objective.id} practice $stepId cannot be resolved"
        }
        objective.checkItemIds.forEach { itemId ->
            addEdge(Unit1ReviewEdge(objective.id, itemId, "CHECK"))
            if (pack.item(itemId) == null) unresolved += "Objective ${objective.id} check $itemId cannot be resolved"
        }
        objective.remediationStepIds.forEach { stepId ->
            addEdge(Unit1ReviewEdge(objective.id, stepId, "REMEDIATION"))
            if (pack.step(stepId) == null) unresolved += "Objective ${objective.id} remediation $stepId cannot be resolved"
        }
    }
    val guidedItemIds = linkedSetOf<String>()
    val checkItemIds = linkedSetOf<String>()
    unit.sessions.forEach { session ->
        add(sessionRecord(session))
        session.objectiveIds.forEach { objectiveId ->
            addEdge(Unit1ReviewEdge(session.id, objectiveId, "OBJECTIVE"))
            if (unit.objectives.none { it.id == objectiveId }) {
                unresolved += "Session ${session.id} objective $objectiveId cannot be resolved"
            }
        }
        session.steps.forEach { step ->
            add(stepRecord(pack, unit, step, session))
            if (!step.bookDirection.isNullOrBlank()) bookDirected += step.id
            if (step.kind == "UNAVAILABLE" || !step.unavailableReason.isNullOrBlank()) unavailable += step.id
            if (step.kind == "GUIDED") guidedItemIds += step.itemIds
            if (step.kind == "CHECK") checkItemIds += step.itemIds
            step.remediationStepId?.let { target ->
                addEdge(Unit1ReviewEdge(step.id, target, "REMEDIATION"))
                if (pack.step(target) == null) unresolved += "Step ${step.id} remediation $target cannot be resolved"
            }
            step.exampleIds.forEach { exampleId ->
                addEdge(Unit1ReviewEdge(step.id, exampleId, "EXAMPLE"))
                if (pack.example(exampleId) == null) unresolved += "Step ${step.id} example $exampleId cannot be resolved"
            }
            step.itemIds.forEach { itemId ->
                addEdge(Unit1ReviewEdge(step.id, itemId, "ITEM"))
                if (pack.item(itemId) == null) unresolved += "Step ${step.id} item $itemId cannot be resolved"
            }
            step.referenceIds.forEach { referenceId ->
                addEdge(Unit1ReviewEdge(step.id, referenceId, "REFERENCE"))
                if (pack.reference(referenceId) == null) unresolved += "Step ${step.id} reference $referenceId cannot be resolved"
            }
        }
    }
    val exampleIds = linkedSetOf<String>()
    unit.objectives.forEach { exampleIds += it.exampleIds }
    unit.sessions.forEach { session -> session.steps.forEach { exampleIds += it.exampleIds } }
    exampleIds.forEach { exampleId ->
        val example = pack.example(exampleId) ?: return@forEach
        add(exampleRecord(pack, unit, example))
    }
    val itemIds = linkedSetOf<String>()
    itemIds += guidedItemIds
    itemIds += checkItemIds
    unit.objectives.forEach { itemIds += it.checkItemIds }
    itemIds.forEach { itemId ->
        val item = pack.item(itemId) ?: return@forEach
        val type = when {
            itemId in guidedItemIds && itemId in checkItemIds -> "GUIDED_AND_INDEPENDENT_CHECK"
            itemId in guidedItemIds -> "GUIDED_PRACTICE"
            else -> "INDEPENDENT_CHECK"
        }
        add(itemRecord(pack, unit, item, type))
    }
    val referenceIds = linkedSetOf<String>()
    unit.sessions.forEach { session -> session.steps.forEach { referenceIds += it.referenceIds } }
    referenceIds.forEach { referenceId ->
        val reference = pack.reference(referenceId) ?: return@forEach
        add(referenceRecord(pack, unit, reference))
    }
    val letterIds = exampleIds.mapNotNull { pack.example(it)?.letterId }.distinct()
    letterIds.forEach { letterId ->
        val letter = pack.letter(letterId)
        if (letter == null) {
            unresolved += "Letter $letterId cannot be resolved"
        } else {
            add(letterRecord(pack, unit, letter))
        }
    }
    val sourceIds = records.map { it.sourceId }.filter { it.isNotBlank() }.distinct()
    sourceIds.forEach { sourceId ->
        val source = pack.sources.firstOrNull { it.id == sourceId }
        if (source == null) {
            unresolved += "Source $sourceId cannot be resolved"
        } else {
            add(
                record(
                    id = source.id,
                    type = "SOURCE",
                    objectiveIds = emptyList(),
                    sessionIds = emptyList(),
                    content = listOf(source.title, source.license, source.attribution, source.modification).joinToString("\n"),
                    sourceId = source.id,
                    rightsIds = pack.rights.filter { it.sourceId == source.id }.map { it.id },
                    classification = "",
                    reviewStatus = "NO_SEPARATE_REVIEW_FIELD",
                ),
            )
        }
    }
    val rightsIds = sourceIds.flatMap { sourceId -> pack.rights.filter { it.sourceId == sourceId }.map { it.id } }.distinct()
    rightsIds.forEach { rightsId ->
        val rights = pack.rights.firstOrNull { it.id == rightsId }
        if (rights == null) {
            unresolved += "Rights $rightsId cannot be resolved"
        } else {
            add(
                record(
                    id = rights.id,
                    type = "RIGHTS",
                    objectiveIds = emptyList(),
                    sessionIds = emptyList(),
                    content = listOf(rights.sourceId, rights.license, "permitsAppUse=${rights.permitsAppUse}").joinToString("\n"),
                    sourceId = rights.sourceId,
                    rightsIds = listOf(rights.id),
                    classification = "",
                    reviewStatus = "NO_SEPARATE_REVIEW_FIELD",
                ),
            )
        }
    }
    if (sourceIds.isEmpty()) unresolved += "Unit $unitNumber records do not cite a source"
    objectiveOrder.forEach { objectiveId ->
        if (records.none { it.id == objectiveId }) unresolved += "Objective $objectiveId was not emitted"
    }
    val instructional = V2Validator.unitContentReviewDigest(pack, unitNumber)
    val distinctUnresolved = unresolved.distinct()
    val distinctBookDirected = bookDirected.distinct()
    val distinctUnavailable = unavailable.distinct()
    return Unit1ReviewPackage(
        unitNumber = unitNumber,
        contentReviewDigest = instructional,
        packContentReviewDigest = V2Validator.contentReviewDigest(pack),
        unitReviewStatus = unitReviewStatus,
        reviewPackageDigest = reviewPackageDigest(
            unitNumber,
            unit.installState,
            unit.readiness,
            unit.mappingStatus,
            unitReviewStatus,
            instructional,
            records,
            edges,
            distinctBookDirected,
            distinctUnavailable,
            distinctUnresolved,
        ),
        repositorySourceDigest = repositorySourceDigest(),
        records = records,
        edges = edges,
        unresolved = distinctUnresolved,
        bookDirectedIds = distinctBookDirected,
        unavailableIds = distinctUnavailable,
    )
}

internal fun renderUnit1ReviewFiles(pack: V2CurriculumPack): Unit1ReviewFiles = renderUnitReviewFiles(pack, 1)

internal fun renderUnitReviewFiles(pack: V2CurriculumPack, unitNumber: Int): Unit1ReviewFiles {
    val review = buildUnitReviewPackage(pack, unitNumber)
    val names = reviewFileNames(unitNumber)
    return Unit1ReviewFiles(
        files = linkedMapOf(
            names[0] to renderPacket(pack, review),
            names[1] to renderManifest(pack, review),
            names[2] to renderWorksheet(review),
            names[3] to renderMappingWorksheet(pack, unitNumber),
            names[4] to renderInstructions(review),
        ),
    )
}

internal fun validateUnit1ReviewFiles(pack: V2CurriculumPack, files: Map<String, String>): List<String> =
    validateUnitReviewFiles(pack, 1, files)

internal fun validateUnitReviewFiles(pack: V2CurriculumPack, unitNumber: Int, files: Map<String, String>): List<String> {
    val errors = mutableListOf<String>()
    val names = reviewFileNames(unitNumber)
    val expected = renderUnitReviewFiles(pack, unitNumber).files
    names.forEach { name ->
        if (name !in files) errors += "Review file $name is omitted"
    }
    files.keys.filter { it !in names }.forEach { name ->
        errors += "Review file $name is not part of the Unit $unitNumber package"
    }
    val rendered = buildUnitReviewPackage(pack, unitNumber)
    if (rendered.unresolved.isNotEmpty()) {
        errors += "Unit $unitNumber review traversal cannot resolve: ${rendered.unresolved.joinToString("; ")}"
    }
    val unit = pack.unit(unitNumber)
    val manifest = files[reviewManifestName(unitNumber)]
    if (manifest != null) {
        val digest = jsonStringField(manifest, "contentReviewDigest")
        val packageDigest = jsonStringField(manifest, "reviewPackageDigest")
        val sourceDigest = jsonStringField(manifest, "repositorySourceDigest")
        if (digest != rendered.contentReviewDigest) {
            errors += "Content-review digest does not match the application digest"
        }
        if (packageDigest != rendered.reviewPackageDigest) {
            errors += "Review package digest does not match the review package"
        }
        if (sourceDigest != rendered.repositorySourceDigest) {
            errors += "Repository source digest does not match the release binding"
        }
        if (digest != null && digest == packageDigest) {
            errors += "Review package digest is only an alias of the content-review digest"
        }
        if (digest != null && digest == sourceDigest) {
            errors += "Content-review digest collides with the repository source digest"
        }
        if (packageDigest != null && packageDigest == sourceDigest) {
            errors += "Review package digest collides with the repository source digest"
        }
        if (jsonStringField(manifest, "packageId") != reviewPackageId(unitNumber)) {
            errors += "Review package is not for Unit $unitNumber"
        }
        val recordedCommit = jsonStringField(manifest, "curriculumSourceCommit")
        if (recordedCommit != CURRICULUM_SOURCE_COMMIT) {
            errors += "Package curriculum source commit does not match the curriculum revision"
        }
        if (recordedCommit != null) {
            errors += curriculumSourceCommitErrors(recordedCommit, reviewRepositoryRoot())
        }
        if (unit != null && jsonStringField(manifest, "installState") != unit.installState) {
            errors += "Package does not keep Unit $unitNumber in ${unit.installState}"
        }
        if (unit != null && jsonStringField(manifest, "readiness") != unit.readiness) {
            errors += "Package does not keep Unit $unitNumber readiness ${unit.readiness}"
        }
        if (unit != null && jsonStringField(manifest, "mappingStatus") != unit.mappingStatus) {
            errors += "Package does not keep the Unit $unitNumber mapping unsigned"
        }
        val unitReview = pack.unitReviews.singleOrNull { it.unitNumber == unitNumber }
        val expectedReviewStatus = if (unitReview == null && pack.unitReviews.count { it.unitNumber == unitNumber } > 1) {
            "DUPLICATE"
        } else {
            unitReview?.status ?: "MISSING"
        }
        if (jsonStringField(manifest, "unitReviewStatus") != expectedReviewStatus) {
            errors += "Package does not keep the Unit $unitNumber review pending"
        }
        if (jsonStringField(manifest, "packContentReviewDigest") != V2Validator.contentReviewDigest(pack)) {
            errors += "Pack content-review digest does not match the repository aggregate"
        }
        if (jsonStringField(manifest, "contentReviewDigest") == jsonStringField(manifest, "packContentReviewDigest")) {
            errors += "Unit content-review digest is only an alias of the pack aggregate"
        }
        val recordsJson = manifest.substringAfter("\"records\": [").substringBefore("\n  ],\n  \"edges\"")
        val ids = Regex("\"id\": \"([^\"]+)\"").findAll(recordsJson).map { it.groupValues[1] }.toList()
        if (ids.size != ids.distinct().size) errors += "Manifest repeats a stable id"
        val expectedIds = rendered.records.map { it.id }
        if (ids != expectedIds) errors += "Manifest record ids do not match the Unit $unitNumber traversal"
        rendered.records.forEach { record ->
            if (!manifest.contains("\"contentHash\": \"${record.contentHash}\"")) {
                errors += "Content hash for ${record.id} does not match"
            }
        }
        val edgesJson = manifest.substringAfter("\"edges\": [").substringBefore("\n  ],\n  \"bookDirectedIds\"")
        val edgeTriples = Regex("\"from\": \"([^\"]+)\", \"to\": \"([^\"]+)\", \"kind\": \"([^\"]+)\"").findAll(edgesJson).map {
            Triple(it.groupValues[1], it.groupValues[2], it.groupValues[3])
        }.toList()
        val expectedEdges = rendered.edges.map { Triple(it.fromId, it.toId, it.kind) }
        if (edgeTriples != expectedEdges) errors += "Manifest edges do not match the Unit $unitNumber traversal"
    }
    val worksheet = files[reviewWorksheetName(unitNumber)]
    if (worksheet != null) {
        errors += worksheetErrors(worksheet, rendered, unitNumber)
    }
    val mapping = files[reviewMappingName(unitNumber)]
    if (mapping != null) {
        errors += mappingErrors(mapping, pack, unitNumber)
    }
    val combined = files.values.joinToString("\n")
    if (combined.contains("Penguin-aligned")) errors += "Package describes Unit $unitNumber as Penguin-aligned"
    if (combined.contains("The New Penguin Russian Course")) errors += "Package introduces Penguin prose"
    if (Regex("(?i)exercise\\s+\\d+").containsMatchIn(combined)) errors += "Package introduces Penguin exercise content"
    if (combined.contains("Qualified Reviewer")) errors += "Package fabricates a reviewer identity"
    if (combined.contains("installState: INSTALLED") || combined.contains("\"installState\": \"INSTALLED\"")) {
        errors += "Package describes Unit $unitNumber as installed"
    }
    if (combined.contains("\"readiness\": \"INSTALLABLE\"") || combined.contains("readiness: INSTALLABLE")) {
        errors += "Package describes Unit $unitNumber as installable"
    }
    if (combined.contains("\"readiness\": \"REVIEW_APPROVED\"") || combined.contains("readiness: REVIEW_APPROVED")) {
        errors += "Package describes Unit $unitNumber as reviewed"
    }
    if (combined.contains("\"status\": \"APPROVED\"") || combined.contains("unitReviewStatus\": \"APPROVED\"") || combined.contains("linguisticReviewStatus\": \"APPROVED\"")) {
        errors += "Package pre-populates linguistic review as approved"
    }
    if (combined.contains("08663616a79af9009c657ecfb66d7f6d42a531bf")) {
        errors += "Package records a curriculum commit that predates this curriculum"
    }
    expected.forEach { (name, text) ->
        val actual = files[name] ?: return@forEach
        if (actual != text) errors += "Review file $name is not the deterministic rendering"
    }
    return errors.distinct()
}

private fun worksheetErrors(worksheet: String, rendered: Unit1ReviewPackage, unitNumber: Int): List<String> {
    val errors = mutableListOf<String>()
    val rows = parseTsv(worksheet)
    if (rows.isEmpty()) return listOf("Review worksheet is empty")
    if (rows.first() != WORKSHEET_HEADER) errors += "Review worksheet header is not the governed column set"
    val body = rows.drop(1)
    val ids = body.map { it[0] }
    if (ids.size != ids.distinct().size) errors += "Review worksheet repeats a stable id"
    if (ids != rendered.records.map { it.id }) errors += "Review worksheet omits or reorders a Unit $unitNumber record"
    val disposition = WORKSHEET_HEADER.indexOf("disposition")
    val identity = WORKSHEET_HEADER.indexOf("reviewer_identity")
    val date = WORKSHEET_HEADER.indexOf("review_date")
    val digest = WORKSHEET_HEADER.indexOf("reviewed_version_digest")
    val comment = WORKSHEET_HEADER.indexOf("correction_comment")
    val qualification = WORKSHEET_HEADER.indexOf("reviewer_qualification")
    val hash = WORKSHEET_HEADER.indexOf("content_hash")
    body.forEachIndexed { index, row ->
        if (row.size != WORKSHEET_HEADER.size) {
            errors += "Review worksheet row ${index + 2} has the wrong column count"
            return@forEachIndexed
        }
        if (row[disposition].isNotBlank() || row[identity].isNotBlank() || row[date].isNotBlank() || row[digest].isNotBlank() || row[comment].isNotBlank() || row[qualification].isNotBlank()) {
            errors += "Review worksheet pre-populates a reviewer field for ${row[0]}"
        }
        if (row[disposition] == "APPROVED" || row[disposition] == "CORRECTION_REQUIRED" || row[disposition] == "REJECTED") {
            errors += "Review worksheet disposition for ${row[0]} is not blank"
        }
        val expected = rendered.records.firstOrNull { it.id == row[0] }
        if (expected != null && row[hash] != expected.contentHash) {
            errors += "Review worksheet hash for ${row[0]} does not match"
        }
    }
    return errors
}

private fun mappingErrors(mapping: String, pack: V2CurriculumPack, unitNumber: Int): List<String> {
    val errors = mutableListOf<String>()
    val unit = pack.unit(unitNumber) ?: return listOf("Unit $unitNumber is missing")
    val rows = parseTsv(mapping)
    if (rows.isEmpty()) return listOf("Mapping worksheet is empty")
    if (rows.first() != MAPPING_HEADER) errors += "Mapping worksheet header is not the governed column set"
    val expectedIds = unit.objectives.map { it.id } + unit.sessions.map { it.id }
    val body = rows.drop(1)
    val ids = body.map { it[0] }
    if (ids.size != ids.distinct().size) errors += "Mapping worksheet repeats a stable id"
    if (ids != expectedIds) errors += "Mapping worksheet does not list every Unit $unitNumber objective and session"
    body.forEach { row ->
        if (row.size != MAPPING_HEADER.size) {
            errors += "Mapping worksheet row ${row.firstOrNull()} has the wrong column count"
            return@forEach
        }
        BLANK_MAPPING_COLUMNS.forEach { column ->
            if (row[MAPPING_HEADER.indexOf(column)].isNotBlank()) {
                errors += "Mapping worksheet pre-populates $column for ${row[0]}"
            }
        }
    }
    if (!mapping.contains("must not contain copied Penguin prose")) {
        errors += "Mapping worksheet omits the Penguin-prose warning"
    }
    return errors
}

private fun renderPacket(pack: V2CurriculumPack, review: Unit1ReviewPackage): String {
    val unitRecord = review.records.firstOrNull { it.type == "UNIT" }
    val unit = pack.unit(review.unitNumber)
    return buildString {
        append("# Unit ${review.unitNumber} linguistic and owned-edition mapping packet\n\n")
        append("This packet prepares review. It does not record a qualified review, an owned-edition mapping, or an install decision.\n\n")
        if (unit?.installState == "DEVELOPMENT" && unit.readiness == "CONTENT_COMPLETE_REVIEW_PENDING") {
            append("Unit ${review.unitNumber} remains in development. Its readiness is content-complete and review-pending. ")
        } else {
            append("Unit ${review.unitNumber} install state is ${unit?.installState ?: "missing"} and readiness is ${unit?.readiness?.ifBlank { "unset" } ?: "unset"}. ")
        }
        if (unit?.mappingStatus == "PENDING_CURRICULUM_REVIEWER") {
            append("The Lesson ${review.unitNumber} relationship is a proposed, unsigned mapping. It is not an approved edition alignment.\n\n")
        } else {
            append("No owned-edition mapping is approved for this unit.\n\n")
        }
        append("## Package identity\n\n")
        append("- Schema version: $UNIT1_REVIEW_SCHEMA_VERSION\n")
        append("- Application: $UNIT1_REVIEW_APPLICATION_ID\n")
        append("- Version: $UNIT1_REVIEW_VERSION_NAME ($UNIT1_REVIEW_VERSION_CODE)\n")
        append("- Curriculum source commit: $CURRICULUM_SOURCE_COMMIT\n")
        append("- Unit content-review SHA-256: ${review.contentReviewDigest}\n")
        append("- Pack content-review SHA-256: ${review.packContentReviewDigest}\n")
        append("- Review-package SHA-256: ${review.reviewPackageDigest}\n")
        append("- Repository source SHA-256: ${review.repositorySourceDigest}\n\n")
        append("The unit content-review digest is what a reviewer of Unit ${review.unitNumber} approves. ")
        append("It covers this unit and the instructional records, relationships, shared dependencies, sources, rights, assets, and advancement rule reachable from it. ")
        append("It does not cover any other unit. Recording a review status, reviewer signature, mapping approval, install state, or readiness does not change it.\n\n")
        append("The pack content-review digest is the repository aggregate. It is not an approval of this unit, and it is not part of the review-package digest.\n\n")
        append("The review-package digest identifies this pending packet: ordered records, relationship hashes, workflow fields as they stand here, and every instructional edge.\n\n")
        append("The repository source digest is the release-gate hash of repository inputs. Recording an approval later changes that repository hash. ")
        append("That change does not replace the signed content-review digest.\n\n")
        append("## Unit\n\n")
        append(unitRecord?.content ?: "Unit ${review.unitNumber} is missing.")
        append("\n\n## Not yet approved\n\n")
        append("No reviewer has signed this packet. Reviewer identity, qualification, review date, disposition, and reviewed digest are blank. ")
        append("Do not treat this development unit as reviewed, installable, or installed.\n\n")
        append("## Records\n\n")
        review.records.forEach { record ->
            append("### ${record.type} `${record.id}`\n\n")
            append("- Objectives: ${record.objectiveIds.joinToString(", ").ifBlank { "none" }}\n")
            append("- Sessions: ${record.sessionIds.joinToString(", ").ifBlank { "none" }}\n")
            append("- Source: ${record.sourceId.ifBlank { "none" }}\n")
            append("- Rights: ${record.rightsIds.joinToString(", ").ifBlank { "none" }}\n")
            append("- Classification: ${record.classification.ifBlank { "none" }}\n")
            append("- Current review status: ${record.reviewStatus}\n")
            append("- Content hash: ${record.contentHash}\n\n")
            append(record.content)
            append("\n\n")
        }
        append("## Instructional relationships\n\n")
        if (review.edges.isEmpty()) {
            append("None.\n\n")
        } else {
            review.edges.forEach { append("- `${it.fromId}` ${it.kind} `${it.toId}`\n") }
        }
        append("\n## Book-directed experiences\n\n")
        if (review.bookDirectedIds.isEmpty()) append("None.\n\n") else review.bookDirectedIds.forEach { append("- `$it`\n") }
        append("\n## Unavailable experiences\n\n")
        if (review.unavailableIds.isEmpty()) append("None.\n\n") else review.unavailableIds.forEach { append("- `$it`\n") }
        append("\n## Unresolved relationships\n\n")
        if (review.unresolved.isEmpty()) append("None. Every referenced Unit ${review.unitNumber} identifier resolves.\n") else review.unresolved.forEach { append("- $it\n") }
    }
}

private fun renderManifest(pack: V2CurriculumPack, review: Unit1ReviewPackage): String {
    val unitRecord = review.records.firstOrNull { it.type == "UNIT" }
    val unit = pack.unit(review.unitNumber)
    val counts = review.records.groupingBy { it.type }.eachCount().toSortedMap()
    return buildString {
        append("{\n")
        append("  \"schemaVersion\": $UNIT1_REVIEW_SCHEMA_VERSION,\n")
        append("  \"packageId\": ${jsonString(reviewPackageId(review.unitNumber))},\n")
        append("  \"applicationId\": ${jsonString(UNIT1_REVIEW_APPLICATION_ID)},\n")
        append("  \"versionName\": ${jsonString(UNIT1_REVIEW_VERSION_NAME)},\n")
        append("  \"versionCode\": $UNIT1_REVIEW_VERSION_CODE,\n")
        append("  \"curriculumSourceCommit\": ${jsonString(CURRICULUM_SOURCE_COMMIT)},\n")
        append("  \"contentReviewDigest\": ${jsonString(review.contentReviewDigest)},\n")
        append("  \"packContentReviewDigest\": ${jsonString(review.packContentReviewDigest)},\n")
        append("  \"reviewPackageDigest\": ${jsonString(review.reviewPackageDigest)},\n")
        append("  \"repositorySourceDigest\": ${jsonString(review.repositorySourceDigest)},\n")
        append("  \"unitReviewStatus\": ${jsonString(review.unitReviewStatus)},\n")
        append("  \"unit\": {\n")
        append("    \"id\": ${jsonString(unit?.id ?: "unit-${review.unitNumber}")},\n")
        append("    \"installState\": ${jsonString(unit?.installState ?: "MISSING")},\n")
        append("    \"readiness\": ${jsonString(unit?.readiness.orEmpty())},\n")
        append("    \"mappingStatus\": ${jsonString(unit?.mappingStatus ?: "MISSING")},\n")
        append("    \"contentHash\": ${jsonString(unitRecord?.contentHash.orEmpty())}\n")
        append("  },\n")
        append("  \"counts\": {\n")
        counts.entries.forEachIndexed { index, (type, count) ->
            val comma = if (index == counts.size - 1) "" else ","
            append("    ${jsonString(type)}: $count$comma\n")
        }
        append("  },\n")
        append("  \"records\": [\n")
        review.records.forEachIndexed { index, record ->
            val comma = if (index == review.records.lastIndex) "" else ","
            append("    {\"id\": ${jsonString(record.id)}, \"type\": ${jsonString(record.type)}, \"objectiveIds\": ${jsonArray(record.objectiveIds)}, \"sessionIds\": ${jsonArray(record.sessionIds)}, \"contentHash\": ${jsonString(record.contentHash)}, \"sourceId\": ${jsonString(record.sourceId)}, \"rightsIds\": ${jsonArray(record.rightsIds)}, \"reviewStatus\": ${jsonString(record.reviewStatus)}}$comma\n")
        }
        append("  ],\n")
        append("  \"edges\": [\n")
        review.edges.forEachIndexed { index, edge ->
            val comma = if (index == review.edges.lastIndex) "" else ","
            append("    {\"from\": ${jsonString(edge.fromId)}, \"to\": ${jsonString(edge.toId)}, \"kind\": ${jsonString(edge.kind)}}$comma\n")
        }
        append("  ],\n")
        append("  \"bookDirectedIds\": ${jsonArray(review.bookDirectedIds)},\n")
        append("  \"unavailableIds\": ${jsonArray(review.unavailableIds)},\n")
        append("  \"unresolved\": ${jsonArray(review.unresolved)}\n")
        append("}\n")
    }
}

private fun renderWorksheet(review: Unit1ReviewPackage): String {
    val lines = mutableListOf(WORKSHEET_HEADER.joinToString("\t"))
    review.records.forEach { record ->
        lines += listOf(
            record.id,
            record.type,
            record.objectiveIds.joinToString(","),
            record.sessionIds.joinToString(","),
            record.content,
            record.sourceId,
            record.rightsIds.joinToString(","),
            record.classification,
            record.reviewStatus,
            record.contentHash,
            "",
            "",
            "",
            "",
            "",
            "",
        ).joinToString("\t") { escapeTsv(it) }
    }
    lines += "# Allowed disposition values, to be entered by the reviewer and not by this generator: APPROVED, CORRECTION_REQUIRED, REJECTED."
    lines += "# Reviewer identity, qualification, review date, correction comment, and reviewed version digest must stay blank until a human reviewer completes them."
    return lines.joinToString("\n") + "\n"
}

private fun renderMappingWorksheet(pack: V2CurriculumPack, unitNumber: Int): String {
    val unit = pack.unit(unitNumber) ?: error("Unit $unitNumber is missing")
    val lines = mutableListOf(
        "# Warning: this worksheet must not contain copied Penguin prose or exercises.",
        "# Enter edition, ISBN, owned-copy verification, lesson, page, topic, alignment, variance, notes, identity, date, reviewed digest, and qualification only after comparing the Product Owner's legally owned edition.",
        MAPPING_HEADER.joinToString("\t"),
    )
    unit.objectives.forEach { objective ->
        lines += mappingRow(objective.id, "OBJECTIVE", objective.title)
    }
    unit.sessions.forEach { session ->
        lines += mappingRow(session.id, "SESSION", session.title)
    }
    return lines.joinToString("\n") + "\n"
}

private fun mappingRow(id: String, type: String, title: String): String {
    return listOf(id, type, title, "", "", "", "", "", "", "", "", "", "", "", "", "", "").joinToString("\t") { escapeTsv(it) }
}

private fun renderInstructions(review: Unit1ReviewPackage): String {
    return """
        # Reviewer instructions for the Unit ${review.unitNumber} packet

        Schema version: $UNIT1_REVIEW_SCHEMA_VERSION
        Application: $UNIT1_REVIEW_APPLICATION_ID $UNIT1_REVIEW_VERSION_NAME ($UNIT1_REVIEW_VERSION_CODE)
        Curriculum source commit: $CURRICULUM_SOURCE_COMMIT
        Unit content-review digest: ${review.contentReviewDigest}
        Pack content-review digest: ${review.packContentReviewDigest}
        Review-package digest: ${review.reviewPackageDigest}
        Repository source digest: ${review.repositorySourceDigest}

        This packet was generated from the packaged curriculum at curriculum source commit $CURRICULUM_SOURCE_COMMIT. That commit contains the reviewed curriculum. This packet commit records that identity and does not perform review.

        ## Digests

        `contentReviewDigest` is the unit-scoped digest the reviewer of Unit ${review.unitNumber} approves. It is canonical JSON, digest schema ${V2Validator.CONTENT_REVIEW_DIGEST_SCHEMA}, with scope `unit`. It includes this unit and the reachable instructional records, relationships, shared dependencies, sources, rights, assets, and advancement rule. It excludes review status, reviewer signature fields, the reviewed digest, mapping approval status, install state, and readiness. Recording those workflow values leaves this digest unchanged. A later change to substantive content reachable from this unit changes it and invalidates this unit's approval. A change that this unit cannot reach does not. Approval binds only to version $UNIT1_REVIEW_VERSION_NAME and this unit digest. Signing Unit ${review.unitNumber} does not approve any other unit.

        `packContentReviewDigest` is the pack-wide aggregate used for repository and release integrity. It is not the digest a unit reviewer signs, and it is not hashed into `reviewPackageDigest`.

        `reviewPackageDigest` identifies which packet was reviewed: manifest metadata, ordered records, per-record hashes including objective and session relationships, every instructional edge, book-directed and unavailable lists, the pending workflow fields in this packet, the curriculum source commit, and the unit content-review digest. It does not approve the curriculum by itself.

        `repositorySourceDigest` is the release-gate hash of repository inputs. It tracks the repository and release state after an approval is recorded. A later commit changes it. Release-gate validation and the state-transition rules still have to accept that repository state. It is not the digest the reviewer signs.

        `curriculumSourceCommit` is the commit that contains this unit's governed curriculum. It is an ancestor of the packet commit. A packet that names its own commit, or a commit that does not contain this curriculum, is rejected.

        ## Qualifications

        A reviewer must possess demonstrable advanced Russian competence and sufficient language-teaching or curriculum-review competence to validate Cyrillic letter identity, phonetics, instructional explanations, answer keys, feedback, remediation, and owned-edition placement. The repository does not name, appoint, or attest any reviewer. The reviewer qualification field stays blank until that person attests their own qualification.

        ## What must be checked

        Check every Cyrillic identity, phonetic statement, instructional explanation, answer key, feedback item, remediation statement, lesson placement, and mapping row. Compare the mapping worksheet with the Product Owner's legally owned edition. Do not copy Penguin text, exercises, answer keys, screenshots, or scans into the repository or the worksheet.

        ## What approval means

        A later approval applies only to version $UNIT1_REVIEW_VERSION_NAME and unit content-review digest ${review.contentReviewDigest}. Confirm review-package digest ${review.reviewPackageDigest} and curriculum source commit $CURRICULUM_SOURCE_COMMIT before relying on this packet. Any later substantive change reachable from Unit ${review.unitNumber} invalidates this unit's approval. Leave disposition, correction comment, reviewer identity, reviewer qualification, review date, and reviewed digest blank until that human review happens. Allowed disposition values are APPROVED, CORRECTION_REQUIRED, and REJECTED.

        Unresolved findings, corrections, or a missing owned-edition comparison prevent APPROVED, MAPPING_APPROVED, INSTALLABLE, and INSTALLED. This packet must not be used to mark Unit ${review.unitNumber} reviewed, installable, installed, or as an approved edition alignment. A packet for one unit cannot approve another unit.

        ## Generator

        The deterministic generator and validator live in `app/src/test/java/dev/russiancompanion/v2/review/Unit1ReviewPackage.kt` and accept a unit number from 1 through 30. Regenerate the files in `review/unit-${review.unitNumber}/` from the packaged curriculum after any instructional change, then review the new digest.
    """.trimIndent() + "\n"
}

private fun unitReviewRecord(review: dev.russiancompanion.v2.curriculum.V2UnitReviewRecord): Unit1ReviewRecord {
    return record(
        id = review.id,
        type = "UNIT_REVIEW",
        objectiveIds = emptyList(),
        sessionIds = emptyList(),
        content = listOf(
            "status=${review.status}",
            "reviewer=${review.reviewer.orEmpty()}",
            "reviewerQualification=${review.reviewerQualification.orEmpty()}",
            "packVersion=${review.packVersion?.toString().orEmpty()}",
            "reviewedContentDigest=${review.reviewedContentDigest.orEmpty()}",
            "curriculumMappingId=${review.curriculumMappingId.orEmpty()}",
            "notes=${review.notes.orEmpty()}",
        ).joinToString("\n"),
        sourceId = "",
        rightsIds = emptyList(),
        classification = "",
        reviewStatus = review.status,
    )
}

private fun unitRecord(unit: V2Unit): Unit1ReviewRecord {
    return record(
        id = unit.id,
        type = "UNIT",
        objectiveIds = unit.objectives.map { it.id },
        sessionIds = unit.sessions.map { it.id },
        content = listOf(
            "number=${unit.number}",
            "title=${unit.title}",
            "topicSummary=${unit.topicSummary}",
            "installState=${unit.installState}",
            "readiness=${unit.readiness}",
            "mappingStatus=${unit.mappingStatus}",
            "mappingNote=${unit.mappingNote}",
            "penguinLessonField=${unit.penguinLesson}",
            "prerequisites=${unit.prerequisiteUnitNumbers.joinToString(",")}",
            "estimatedMinutes=${unit.estimatedMinutes?.toString().orEmpty()}",
        ).joinToString("\n"),
        sourceId = "",
        rightsIds = emptyList(),
        classification = "",
        reviewStatus = "NO_SEPARATE_REVIEW_FIELD",
    )
}

private fun objectiveRecord(objective: V2Objective): Unit1ReviewRecord {
    return record(
        id = objective.id,
        type = "OBJECTIVE",
        objectiveIds = listOf(objective.id),
        sessionIds = emptyList(),
        content = listOf(
            "title=${objective.title}",
            "kind=${objective.kind}",
            "unitNumber=${objective.unitNumber}",
            "instruction=${objective.instructionStepIds.joinToString(",")}",
            "examples=${objective.exampleIds.joinToString(",")}",
            "practice=${objective.practiceStepIds.joinToString(",")}",
            "checks=${objective.checkItemIds.joinToString(",")}",
            "remediation=${objective.remediationStepIds.joinToString(",")}",
        ).joinToString("\n"),
        sourceId = "",
        rightsIds = emptyList(),
        classification = "",
        reviewStatus = "NO_SEPARATE_REVIEW_FIELD",
    )
}

private fun sessionRecord(session: V2Session): Unit1ReviewRecord {
    return record(
        id = session.id,
        type = "SESSION",
        objectiveIds = session.objectiveIds,
        sessionIds = listOf(session.id),
        content = listOf(
            "title=${session.title}",
            "estimatedMinutes=${session.estimatedMinutes}",
            "objectives=${session.objectiveIds.joinToString(",")}",
            "steps=${session.steps.joinToString(",") { it.id }}",
        ).joinToString("\n"),
        sourceId = "",
        rightsIds = emptyList(),
        classification = "",
        reviewStatus = "NO_SEPARATE_REVIEW_FIELD",
    )
}

private fun stepRecord(pack: V2CurriculumPack, unit: V2Unit, step: V2Step, session: V2Session): Unit1ReviewRecord {
    return record(
        id = step.id,
        type = "STEP",
        objectiveIds = objectiveIdsFor(unit, step.id, session),
        sessionIds = listOf(session.id),
        content = listOf(
            "kind=${step.kind}",
            "title=${step.title}",
            "body=${step.body}",
            "dominantAction=${step.dominantAction}",
            "references=${step.referenceIds.joinToString(",")}",
            "items=${step.itemIds.joinToString(",")}",
            "examples=${step.exampleIds.joinToString(",")}",
            "unavailableReason=${step.unavailableReason.orEmpty()}",
            "bookDirection=${step.bookDirection.orEmpty()}",
            "companionDirection=${step.companionDirection.orEmpty()}",
            "remediationStepId=${step.remediationStepId.orEmpty()}",
        ).joinToString("\n"),
        sourceId = "",
        rightsIds = emptyList(),
        classification = "",
        reviewStatus = "NO_SEPARATE_REVIEW_FIELD",
    )
}

private fun exampleRecord(pack: V2CurriculumPack, unit: V2Unit, example: V2Example): Unit1ReviewRecord {
    return record(
        id = example.id,
        type = "EXAMPLE",
        objectiveIds = listOf(example.objectiveId),
        sessionIds = sessionIdsReferring(unit) { it.exampleIds.contains(example.id) },
        content = listOf(
            "kind=${example.kind}",
            "letterId=${example.letterId}",
            "display=${example.display}",
            "note=${example.note}",
            "objectiveId=${example.objectiveId}",
            "reviewStatus=${example.reviewStatus}",
        ).joinToString("\n"),
        sourceId = example.sourceId,
        rightsIds = rightsIds(pack, example.sourceId),
        classification = "",
        reviewStatus = example.reviewStatus,
    )
}

private fun itemRecord(pack: V2CurriculumPack, unit: V2Unit, item: V2CheckItem, type: String): Unit1ReviewRecord {
    return record(
        id = item.id,
        type = type,
        objectiveIds = listOf(item.objectiveId),
        sessionIds = sessionIdsReferring(unit) { it.itemIds.contains(item.id) },
        content = listOf(
            "kind=${item.kind}",
            "language=${item.language}",
            "prompt=${item.prompt}",
            "promptLetter=${item.promptLetter.orEmpty()}",
            "options=${item.options.joinToString(" | ")}",
            "answer=${item.answer}",
            "feedback=${item.feedback}",
            "objectiveId=${item.objectiveId}",
            "containsGeneratedRussian=${item.containsGeneratedRussian}",
            "reviewStatus=${item.reviewStatus}",
        ).joinToString("\n"),
        sourceId = item.sourceId,
        rightsIds = rightsIds(pack, item.sourceId),
        classification = "",
        reviewStatus = item.reviewStatus,
    )
}

private fun referenceRecord(pack: V2CurriculumPack, unit: V2Unit, reference: V2ReferenceEntry): Unit1ReviewRecord {
    return record(
        id = reference.id,
        type = "REFERENCE",
        objectiveIds = emptyList(),
        sessionIds = sessionIdsReferring(unit) { it.referenceIds.contains(reference.id) },
        content = listOf(
            "title=${reference.title}",
            "summary=${reference.summary}",
            "detail=${reference.detail}",
            "classification=${reference.classification}",
            "introducedInUnit=${reference.introducedInUnit}",
            "searchTerms=${reference.searchTerms.joinToString(",")}",
            "reviewStatus=${reference.reviewStatus}",
        ).joinToString("\n"),
        sourceId = reference.sourceId,
        rightsIds = rightsIds(pack, reference.sourceId),
        classification = reference.classification,
        reviewStatus = reference.reviewStatus,
    )
}

private fun letterRecord(pack: V2CurriculumPack, unit: V2Unit, letter: V2Letter): Unit1ReviewRecord {
    val exampleIds = pack.examples.filter { it.letterId == letter.id }.map { it.id }.toSet()
    return record(
        id = letter.id,
        type = "LETTER",
        objectiveIds = pack.examples.filter { it.letterId == letter.id }.map { it.objectiveId }.distinct(),
        sessionIds = sessionIdsReferring(unit) { step -> step.exampleIds.any { it in exampleIds } },
        content = listOf(
            "uppercase=${letter.uppercase}",
            "lowercase=${letter.lowercase}",
            "group=${letter.group}",
            "englishNote=${letter.englishNote}",
            "latinLookalike=${letter.latinLookalike.orEmpty()}",
            "audioAssetId=${letter.audioAssetId.orEmpty()}",
            "exampleWordId=${letter.exampleWordId.orEmpty()}",
            "reviewStatus=${letter.reviewStatus}",
        ).joinToString("\n"),
        sourceId = letter.sourceId,
        rightsIds = rightsIds(pack, letter.sourceId),
        classification = "",
        reviewStatus = letter.reviewStatus,
    )
}

private fun objectiveIdsFor(unit: V2Unit, stepId: String, session: V2Session): List<String> {
    val fromObjectives = unit.objectives.filter { objective ->
        stepId in objective.instructionStepIds || stepId in objective.practiceStepIds || stepId in objective.remediationStepIds
    }.map { it.id }
    return (session.objectiveIds + fromObjectives).distinct()
}

private fun sessionIdsReferring(unit: V2Unit, predicate: (V2Step) -> Boolean): List<String> {
    return unit.sessions.filter { session -> session.steps.any(predicate) }.map { it.id }
}

private fun rightsIds(pack: V2CurriculumPack, sourceId: String): List<String> {
    return pack.rights.filter { it.sourceId == sourceId }.map { it.id }
}

private fun record(
    id: String,
    type: String,
    objectiveIds: List<String>,
    sessionIds: List<String>,
    content: String,
    sourceId: String,
    rightsIds: List<String>,
    classification: String,
    reviewStatus: String,
): Unit1ReviewRecord {
    val hash = sha256(
        canonObj(
            "type" to canonStr(type),
            "id" to canonStr(id),
            "objectiveIds" to canonArr(objectiveIds.map(::canonStr)),
            "sessionIds" to canonArr(sessionIds.map(::canonStr)),
            "content" to canonStr(content),
            "sourceId" to canonStr(sourceId),
            "rightsIds" to canonArr(rightsIds.map(::canonStr)),
            "classification" to canonStr(classification),
            "reviewStatus" to canonStr(reviewStatus),
        ),
    )
    return Unit1ReviewRecord(id, type, objectiveIds, sessionIds, content, sourceId, rightsIds, classification, reviewStatus, hash)
}

private fun reviewPackageDigest(
    unitNumber: Int,
    installState: String,
    readiness: String,
    mappingStatus: String,
    linguisticReviewStatus: String,
    contentReviewDigest: String,
    records: List<Unit1ReviewRecord>,
    edges: List<Unit1ReviewEdge>,
    bookDirectedIds: List<String>,
    unavailableIds: List<String>,
    unresolved: List<String>,
): String {
    return sha256(
        canonObj(
            "digestSchema" to "1",
            "schemaVersion" to UNIT1_REVIEW_SCHEMA_VERSION.toString(),
            "packageId" to canonStr(reviewPackageId(unitNumber)),
            "applicationId" to canonStr(UNIT1_REVIEW_APPLICATION_ID),
            "versionName" to canonStr(UNIT1_REVIEW_VERSION_NAME),
            "versionCode" to UNIT1_REVIEW_VERSION_CODE.toString(),
            "curriculumSourceCommit" to canonStr(CURRICULUM_SOURCE_COMMIT),
            "contentReviewDigest" to canonStr(contentReviewDigest),
            "unitReviewStatus" to canonStr(linguisticReviewStatus),
            "unit" to canonObj(
                "id" to canonStr("unit-$unitNumber"),
                "installState" to canonStr(installState),
                "readiness" to canonStr(readiness),
                "mappingStatus" to canonStr(mappingStatus),
            ),
            "records" to canonArr(records.map { record ->
                canonObj(
                    "id" to canonStr(record.id),
                    "type" to canonStr(record.type),
                    "objectiveIds" to canonArr(record.objectiveIds.map(::canonStr)),
                    "sessionIds" to canonArr(record.sessionIds.map(::canonStr)),
                    "contentHash" to canonStr(record.contentHash),
                    "sourceId" to canonStr(record.sourceId),
                    "rightsIds" to canonArr(record.rightsIds.map(::canonStr)),
                    "classification" to canonStr(record.classification),
                    "reviewStatus" to canonStr(record.reviewStatus),
                )
            }),
            "edges" to canonArr(edges.map { edge ->
                canonObj(
                    "from" to canonStr(edge.fromId),
                    "to" to canonStr(edge.toId),
                    "kind" to canonStr(edge.kind),
                )
            }),
            "bookDirectedIds" to canonArr(bookDirectedIds.map(::canonStr)),
            "unavailableIds" to canonArr(unavailableIds.map(::canonStr)),
            "unresolved" to canonArr(unresolved.map(::canonStr)),
        ),
    )
}

private fun canonObj(vararg fields: Pair<String, String>): String {
    return fields.joinToString(prefix = "{", postfix = "}") { (key, value) -> "${canonStr(key)}:$value" }
}

private fun canonArr(values: List<String>): String = values.joinToString(prefix = "[", postfix = "]")

private fun canonStr(value: String): String = jsonString(value)

internal fun reviewRepositoryRoot(): File =
    listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }

internal fun curriculumSourceCommitErrors(recorded: String, repositoryRoot: File): List<String> {
    val errors = mutableListOf<String>()
    if (!Regex("[0-9a-f]{40}").matches(recorded)) {
        errors += "Curriculum source commit is not a full commit id"
        return errors
    }
    val head = gitOutput(repositoryRoot, "rev-parse", "HEAD")?.trim()
    if (head.isNullOrBlank()) {
        errors += "Curriculum source commit cannot be resolved"
        return errors
    }
    if (recorded == head) {
        errors += "Curriculum source commit is the packet commit itself"
    }
    if (gitExit(repositoryRoot, "merge-base", "--is-ancestor", recorded, "HEAD") != 0) {
        errors += "Curriculum source commit is not an ancestor of HEAD"
    }
    val committed = gitOutput(repositoryRoot, "show", "$recorded:app/src/main/assets/content/v2-curriculum.json")
    if (committed == null) {
        errors += "Curriculum source commit does not contain the packaged curriculum"
        return errors
    }
    val current = File(repositoryRoot, "app/src/main/assets/content/v2-curriculum.json")
    val currentText = if (current.isFile) current.readText() else ""
    if (currentText != committed) {
        errors += "Curriculum source commit does not contain this revision's governed curriculum"
    }
    try {
        val recordedPack = dev.russiancompanion.v2.curriculum.V2PackLoader.parse(committed)
        val headPack = dev.russiancompanion.v2.curriculum.V2PackLoader.parse(currentText)
        (1..30).forEach { number ->
            if (V2Validator.unitContentReviewDigest(recordedPack, number) != V2Validator.unitContentReviewDigest(headPack, number)) {
                errors += "Unit $number content-review digest at the curriculum source commit does not match this revision"
            }
        }
    } catch (_: RuntimeException) {
        errors += "Curriculum source commit does not contain a readable curriculum"
    }
    return errors
}

private fun gitOutput(root: File, vararg args: String): String? {
    val process = ProcessBuilder(listOf("git") + args)
        .directory(root)
        .redirectError(File("/dev/null"))
        .start()
    val text = process.inputStream.bufferedReader().readText()
    return if (process.waitFor() == 0) text else null
}

private fun gitExit(root: File, vararg args: String): Int {
    val process = ProcessBuilder(listOf("git") + args)
        .directory(root)
        .redirectOutput(File("/dev/null"))
        .redirectError(File("/dev/null"))
        .start()
    return process.waitFor()
}

internal fun repositorySourceDigest(): String {
    val root = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
    return currentReleaseBinding(root)?.sourceDigest ?: error("Release source digest is unavailable")
}

private fun sha256(text: String): String {
    val digest = MessageDigest.getInstance("SHA-256").digest(text.toByteArray(Charsets.UTF_8))
    return digest.joinToString("") { byte -> "%02x".format(byte) }
}

private fun jsonString(value: String): String {
    return buildString {
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
}

private fun jsonArray(values: List<String>): String {
    return values.joinToString(prefix = "[", postfix = "]") { jsonString(it) }
}

private fun escapeTsv(value: String): String {
    return value.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n").replace("\r", "\\r")
}

private fun unescapeTsv(value: String): String {
    val out = StringBuilder()
    var index = 0
    while (index < value.length) {
        val char = value[index]
        if (char == '\\' && index + 1 < value.length) {
            when (value[index + 1]) {
                '\\' -> out.append('\\')
                't' -> out.append('\t')
                'n' -> out.append('\n')
                'r' -> out.append('\r')
                else -> out.append(value[index + 1])
            }
            index += 2
        } else {
            out.append(char)
            index += 1
        }
    }
    return out.toString()
}

internal fun parseTsv(text: String): List<List<String>> {
    return text.lineSequence()
        .filter { it.isNotBlank() && !it.startsWith("#") }
        .map { line -> line.split('\t').map(::unescapeTsv) }
        .toList()
}

private fun jsonStringField(json: String, name: String): String? {
    val match = Regex("\"${Regex.escape(name)}\": \"((?:\\\\.|[^\"\\\\])*)\"").find(json) ?: return null
    return unescapeJson(match.groupValues[1])
}

private fun unescapeJson(value: String): String {
    val out = StringBuilder()
    var index = 0
    while (index < value.length) {
        val char = value[index]
        if (char == '\\' && index + 1 < value.length) {
            when (val next = value[index + 1]) {
                '\\', '"' -> out.append(next)
                'n' -> out.append('\n')
                'r' -> out.append('\r')
                't' -> out.append('\t')
                else -> out.append(next)
            }
            index += 2
        } else {
            out.append(char)
            index += 1
        }
    }
    return out.toString()
}
