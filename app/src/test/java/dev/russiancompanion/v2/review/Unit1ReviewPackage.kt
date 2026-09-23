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
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.example
import dev.russiancompanion.v2.curriculum.item
import dev.russiancompanion.v2.curriculum.letter
import dev.russiancompanion.v2.curriculum.reference
import dev.russiancompanion.v2.curriculum.step
import dev.russiancompanion.v2.curriculum.unit
import java.security.MessageDigest

internal const val UNIT1_REVIEW_SCHEMA_VERSION = 1

internal const val UNIT1_REVIEW_SOURCE_COMMIT = "49eae12f175574add477ba4f15224650abaa2293"

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
)

private val BLANK_REVIEW_COLUMNS = listOf(
    "disposition",
    "correction_comment",
    "reviewer_identity",
    "review_date",
    "reviewed_version_digest",
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
    val instructionalContentDigest: String,
    val records: List<Unit1ReviewRecord>,
    val edges: List<Unit1ReviewEdge>,
    val unresolved: List<String>,
    val bookDirectedIds: List<String>,
    val unavailableIds: List<String>,
)

internal data class Unit1ReviewFiles(
    val files: Map<String, String>,
)

internal fun buildUnit1ReviewPackage(pack: V2CurriculumPack): Unit1ReviewPackage {
    val unit = pack.unit(1)
    val unresolved = mutableListOf<String>()
    val records = mutableListOf<Unit1ReviewRecord>()
    val seen = linkedSetOf<String>()
    val edges = mutableListOf<Unit1ReviewEdge>()
    val bookDirected = mutableListOf<String>()
    val unavailable = mutableListOf<String>()
    if (unit == null) {
        unresolved += "Unit 1 is missing from the curriculum pack"
        return Unit1ReviewPackage(V2Validator.instructionalContentDigest(pack), emptyList(), emptyList(), unresolved, emptyList(), emptyList())
    }
    fun add(record: Unit1ReviewRecord) {
        if (!seen.add(record.id)) {
            unresolved += "Curriculum id ${record.id} is emitted more than once before deduplication"
            return
        }
        records += record
    }
    add(packReviewRecord(pack))
    add(unitRecord(unit))
    val advancement = unit.advancement
    if (advancement == null) {
        unresolved += "Unit 1 advancement rule is missing"
    } else {
        add(
            record(
                id = "unit-1-advancement",
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
            edges += Unit1ReviewEdge(objective.id, stepId, "INSTRUCTION")
            if (pack.step(stepId) == null) unresolved += "Objective ${objective.id} instruction $stepId cannot be resolved"
        }
        objective.exampleIds.forEach { exampleId ->
            edges += Unit1ReviewEdge(objective.id, exampleId, "EXAMPLE")
            if (pack.example(exampleId) == null) unresolved += "Objective ${objective.id} example $exampleId cannot be resolved"
        }
        objective.practiceStepIds.forEach { stepId ->
            edges += Unit1ReviewEdge(objective.id, stepId, "PRACTICE")
            if (pack.step(stepId) == null) unresolved += "Objective ${objective.id} practice $stepId cannot be resolved"
        }
        objective.checkItemIds.forEach { itemId ->
            edges += Unit1ReviewEdge(objective.id, itemId, "CHECK")
            if (pack.item(itemId) == null) unresolved += "Objective ${objective.id} check $itemId cannot be resolved"
        }
        objective.remediationStepIds.forEach { stepId ->
            edges += Unit1ReviewEdge(objective.id, stepId, "REMEDIATION")
            if (pack.step(stepId) == null) unresolved += "Objective ${objective.id} remediation $stepId cannot be resolved"
        }
    }
    val guidedItemIds = linkedSetOf<String>()
    val checkItemIds = linkedSetOf<String>()
    unit.sessions.forEach { session ->
        add(sessionRecord(session))
        session.objectiveIds.forEach { objectiveId ->
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
                edges += Unit1ReviewEdge(step.id, target, "REMEDIATION")
                if (pack.step(target) == null) unresolved += "Step ${step.id} remediation $target cannot be resolved"
            }
            step.exampleIds.forEach { exampleId ->
                if (pack.example(exampleId) == null) unresolved += "Step ${step.id} example $exampleId cannot be resolved"
            }
            step.itemIds.forEach { itemId ->
                if (pack.item(itemId) == null) unresolved += "Step ${step.id} item $itemId cannot be resolved"
            }
            step.referenceIds.forEach { referenceId ->
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
    if (sourceIds.isEmpty()) unresolved += "Unit 1 records do not cite a source"
    objectiveOrder.forEach { objectiveId ->
        if (records.none { it.id == objectiveId }) unresolved += "Objective $objectiveId was not emitted"
    }
    return Unit1ReviewPackage(
        instructionalContentDigest = V2Validator.instructionalContentDigest(pack),
        records = records,
        edges = edges,
        unresolved = unresolved.distinct(),
        bookDirectedIds = bookDirected.distinct(),
        unavailableIds = unavailable.distinct(),
    )
}

internal fun renderUnit1ReviewFiles(pack: V2CurriculumPack): Unit1ReviewFiles {
    val review = buildUnit1ReviewPackage(pack)
    return Unit1ReviewFiles(
        files = linkedMapOf(
            UNIT1_PACKET_NAME to renderPacket(review),
            UNIT1_MANIFEST_NAME to renderManifest(review),
            UNIT1_WORKSHEET_NAME to renderWorksheet(review),
            UNIT1_MAPPING_NAME to renderMappingWorksheet(pack),
            UNIT1_INSTRUCTIONS_NAME to renderInstructions(review),
        ),
    )
}

internal fun validateUnit1ReviewFiles(pack: V2CurriculumPack, files: Map<String, String>): List<String> {
    val errors = mutableListOf<String>()
    val expected = renderUnit1ReviewFiles(pack).files
    UNIT1_REVIEW_FILE_NAMES.forEach { name ->
        if (name !in files) errors += "Review file $name is omitted"
    }
    files.keys.filter { it !in UNIT1_REVIEW_FILE_NAMES }.forEach { name ->
        errors += "Review file $name is not part of the Unit 1 package"
    }
    val rendered = buildUnit1ReviewPackage(pack)
    if (rendered.unresolved.isNotEmpty()) {
        errors += "Unit 1 review traversal cannot resolve: ${rendered.unresolved.joinToString("; ")}"
    }
    val manifest = files[UNIT1_MANIFEST_NAME]
    if (manifest != null) {
        val digest = jsonStringField(manifest, "instructionalContentDigest")
        if (digest != rendered.instructionalContentDigest) {
            errors += "Package digest does not match the instructional-content digest"
        }
        if (jsonStringField(manifest, "sourceCommit") != UNIT1_REVIEW_SOURCE_COMMIT) {
            errors += "Package source commit does not match the curriculum revision"
        }
        if (!manifest.contains("\"installState\": \"DEVELOPMENT\"")) {
            errors += "Package does not keep Unit 1 in DEVELOPMENT"
        }
        if (!manifest.contains("\"readiness\": \"CONTENT_COMPLETE_REVIEW_PENDING\"")) {
            errors += "Package does not keep Unit 1 readiness review-pending"
        }
        if (!manifest.contains("\"mappingStatus\": \"PENDING_CURRICULUM_REVIEWER\"")) {
            errors += "Package does not keep the Unit 1 mapping unsigned"
        }
        if (!manifest.contains("\"linguisticReviewStatus\": \"PENDING_QUALIFIED_REVIEW\"")) {
            errors += "Package does not keep linguistic review pending"
        }
        val recordsJson = manifest.substringAfter("\"records\": [").substringBefore("\n  ],\n  \"remediation\"")
        val ids = Regex("\"id\": \"([^\"]+)\"").findAll(recordsJson).map { it.groupValues[1] }.toList()
        if (ids.size != ids.distinct().size) errors += "Manifest repeats a stable id"
        val expectedIds = rendered.records.map { it.id }
        if (ids != expectedIds) errors += "Manifest record ids do not match the Unit 1 traversal"
        rendered.records.forEach { record ->
            if (!manifest.contains("\"contentHash\": \"${record.contentHash}\"")) {
                errors += "Content hash for ${record.id} does not match"
            }
        }
    }
    val worksheet = files[UNIT1_WORKSHEET_NAME]
    if (worksheet != null) {
        errors += worksheetErrors(worksheet, rendered)
    }
    val mapping = files[UNIT1_MAPPING_NAME]
    if (mapping != null) {
        errors += mappingErrors(mapping, pack)
    }
    val combined = files.values.joinToString("\n")
    if (combined.contains("Penguin-aligned")) errors += "Package describes Unit 1 as Penguin-aligned"
    if (combined.contains("The New Penguin Russian Course")) errors += "Package introduces Penguin prose"
    if (Regex("(?i)exercise\\s+\\d+").containsMatchIn(combined)) errors += "Package introduces Penguin exercise content"
    if (combined.contains("Qualified Reviewer")) errors += "Package fabricates a reviewer identity"
    if (combined.contains("installState: INSTALLED") || combined.contains("\"installState\": \"INSTALLED\"")) {
        errors += "Package describes Unit 1 as installed"
    }
    if (combined.contains("\"readiness\": \"INSTALLABLE\"") || combined.contains("readiness: INSTALLABLE")) {
        errors += "Package describes Unit 1 as installable"
    }
    if (combined.contains("\"readiness\": \"REVIEW_APPROVED\"") || combined.contains("readiness: REVIEW_APPROVED")) {
        errors += "Package describes Unit 1 as reviewed"
    }
    if (combined.contains("\"status\": \"APPROVED\"") || combined.contains("linguisticReviewStatus\": \"APPROVED\"")) {
        errors += "Package pre-populates linguistic review as approved"
    }
    expected.forEach { (name, text) ->
        val actual = files[name] ?: return@forEach
        if (actual != text) errors += "Review file $name is not the deterministic rendering"
    }
    return errors.distinct()
}

private fun worksheetErrors(worksheet: String, rendered: Unit1ReviewPackage): List<String> {
    val errors = mutableListOf<String>()
    val rows = parseTsv(worksheet)
    if (rows.isEmpty()) return listOf("Review worksheet is empty")
    if (rows.first() != WORKSHEET_HEADER) errors += "Review worksheet header is not the governed column set"
    val body = rows.drop(1)
    val ids = body.map { it[0] }
    if (ids.size != ids.distinct().size) errors += "Review worksheet repeats a stable id"
    if (ids != rendered.records.map { it.id }) errors += "Review worksheet omits or reorders a Unit 1 record"
    val disposition = WORKSHEET_HEADER.indexOf("disposition")
    val identity = WORKSHEET_HEADER.indexOf("reviewer_identity")
    val date = WORKSHEET_HEADER.indexOf("review_date")
    val digest = WORKSHEET_HEADER.indexOf("reviewed_version_digest")
    val comment = WORKSHEET_HEADER.indexOf("correction_comment")
    val hash = WORKSHEET_HEADER.indexOf("content_hash")
    body.forEachIndexed { index, row ->
        if (row.size != WORKSHEET_HEADER.size) {
            errors += "Review worksheet row ${index + 2} has the wrong column count"
            return@forEachIndexed
        }
        if (row[disposition].isNotBlank() || row[identity].isNotBlank() || row[date].isNotBlank() || row[digest].isNotBlank() || row[comment].isNotBlank()) {
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

private fun mappingErrors(mapping: String, pack: V2CurriculumPack): List<String> {
    val errors = mutableListOf<String>()
    val unit = pack.unit(1) ?: return listOf("Unit 1 is missing")
    val rows = parseTsv(mapping)
    if (rows.isEmpty()) return listOf("Mapping worksheet is empty")
    if (rows.first() != MAPPING_HEADER) errors += "Mapping worksheet header is not the governed column set"
    val expectedIds = unit.objectives.map { it.id } + unit.sessions.map { it.id }
    val body = rows.drop(1)
    val ids = body.map { it[0] }
    if (ids.size != ids.distinct().size) errors += "Mapping worksheet repeats a stable id"
    if (ids != expectedIds) errors += "Mapping worksheet does not list every Unit 1 objective and session"
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

private fun renderPacket(review: Unit1ReviewPackage): String {
    val unit = review.records.first { it.type == "UNIT" }
    return buildString {
        append("# Unit 1 linguistic and owned-edition mapping packet\n\n")
        append("This packet prepares review. It does not record a qualified review, an owned-edition mapping, or an install decision.\n\n")
        append("Unit 1 remains in development. Its readiness is content-complete and review-pending. ")
        append("The Lesson 1 relationship is a proposed, unsigned mapping. It is not an approved edition alignment.\n\n")
        append("## Package identity\n\n")
        append("- Schema version: $UNIT1_REVIEW_SCHEMA_VERSION\n")
        append("- Application: $UNIT1_REVIEW_APPLICATION_ID\n")
        append("- Version: $UNIT1_REVIEW_VERSION_NAME ($UNIT1_REVIEW_VERSION_CODE)\n")
        append("- Curriculum source commit: $UNIT1_REVIEW_SOURCE_COMMIT\n")
        append("- Instructional-content SHA-256: ${review.instructionalContentDigest}\n")
        append("- Package digest: ${review.instructionalContentDigest}\n\n")
        append("The package digest is the application instructional-content digest. Approval, if a reviewer later grants it, binds only to this digest and version.\n\n")
        append("## Unit\n\n")
        append(unit.content)
        append("\n\n## Not yet approved\n\n")
        append("No qualified reviewer has signed this packet. Reviewer identity, review date, disposition, and reviewed digest are blank. ")
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
        append("## Remediation paths\n\n")
        val remediation = review.edges.filter { it.kind == "REMEDIATION" }
        if (remediation.isEmpty()) append("None.\n\n") else remediation.forEach { append("- `${it.fromId}` -> `${it.toId}`\n") }
        append("\n## Book-directed experiences\n\n")
        if (review.bookDirectedIds.isEmpty()) append("None.\n\n") else review.bookDirectedIds.forEach { append("- `$it`\n") }
        append("\n## Unavailable experiences\n\n")
        if (review.unavailableIds.isEmpty()) append("None.\n\n") else review.unavailableIds.forEach { append("- `$it`\n") }
        append("\n## Unresolved relationships\n\n")
        if (review.unresolved.isEmpty()) append("None. Every referenced Unit 1 identifier resolves.\n") else review.unresolved.forEach { append("- $it\n") }
    }
}

private fun renderManifest(review: Unit1ReviewPackage): String {
    val unit = review.records.first { it.type == "UNIT" }
    val counts = review.records.groupingBy { it.type }.eachCount().toSortedMap()
    return buildString {
        append("{\n")
        append("  \"schemaVersion\": $UNIT1_REVIEW_SCHEMA_VERSION,\n")
        append("  \"packageId\": \"unit-1-linguistic-and-mapping-review\",\n")
        append("  \"applicationId\": ${jsonString(UNIT1_REVIEW_APPLICATION_ID)},\n")
        append("  \"versionName\": ${jsonString(UNIT1_REVIEW_VERSION_NAME)},\n")
        append("  \"versionCode\": $UNIT1_REVIEW_VERSION_CODE,\n")
        append("  \"sourceCommit\": ${jsonString(UNIT1_REVIEW_SOURCE_COMMIT)},\n")
        append("  \"instructionalContentDigest\": ${jsonString(review.instructionalContentDigest)},\n")
        append("  \"packageDigest\": ${jsonString(review.instructionalContentDigest)},\n")
        append("  \"linguisticReviewStatus\": \"PENDING_QUALIFIED_REVIEW\",\n")
        append("  \"unit\": {\n")
        append("    \"id\": \"unit-1\",\n")
        append("    \"installState\": \"DEVELOPMENT\",\n")
        append("    \"readiness\": \"CONTENT_COMPLETE_REVIEW_PENDING\",\n")
        append("    \"mappingStatus\": \"PENDING_CURRICULUM_REVIEWER\",\n")
        append("    \"contentHash\": ${jsonString(unit.contentHash)}\n")
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
            append("    {\"id\": ${jsonString(record.id)}, \"type\": ${jsonString(record.type)}, \"contentHash\": ${jsonString(record.contentHash)}, \"sourceId\": ${jsonString(record.sourceId)}, \"rightsIds\": ${jsonArray(record.rightsIds)}, \"reviewStatus\": ${jsonString(record.reviewStatus)}}$comma\n")
        }
        append("  ],\n")
        append("  \"remediation\": [\n")
        val remediation = review.edges.filter { it.kind == "REMEDIATION" }
        remediation.forEachIndexed { index, edge ->
            val comma = if (index == remediation.lastIndex) "" else ","
            append("    {\"from\": ${jsonString(edge.fromId)}, \"to\": ${jsonString(edge.toId)}}$comma\n")
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
        ).joinToString("\t") { escapeTsv(it) }
    }
    lines += "# Allowed disposition values, to be entered by the reviewer and not by this generator: APPROVED, CORRECTION_REQUIRED, REJECTED."
    lines += "# Reviewer identity, review date, correction comment, and reviewed version digest must stay blank until a qualified human reviewer completes them."
    return lines.joinToString("\n") + "\n"
}

private fun renderMappingWorksheet(pack: V2CurriculumPack): String {
    val unit = pack.unit(1) ?: error("Unit 1 is missing")
    val lines = mutableListOf(
        "# Warning: this worksheet must not contain copied Penguin prose or exercises.",
        "# Enter edition, ISBN, owned-copy verification, lesson, page, topic, alignment, variance, notes, identity, date, and reviewed digest only after comparing the Product Owner's legally owned edition.",
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
    return listOf(id, type, title, "", "", "", "", "", "", "", "", "", "", "", "", "").joinToString("\t") { escapeTsv(it) }
}

private fun renderInstructions(review: Unit1ReviewPackage): String {
    return """
        # Reviewer instructions for the Unit 1 packet

        Schema version: $UNIT1_REVIEW_SCHEMA_VERSION
        Application: $UNIT1_REVIEW_APPLICATION_ID $UNIT1_REVIEW_VERSION_NAME ($UNIT1_REVIEW_VERSION_CODE)
        Curriculum source commit: $UNIT1_REVIEW_SOURCE_COMMIT
        Instructional-content digest: ${review.instructionalContentDigest}

        This packet was generated from the packaged curriculum. It does not perform review.

        ## Qualifications

        The reviewer must be able to judge Cyrillic letter identity, phonetic statements, instructional explanations, answer keys, feedback, remediation, lesson placement, and an owned-edition mapping. The repository does not name or appoint that reviewer.

        ## What must be checked

        Check every Cyrillic identity, phonetic statement, instructional explanation, answer key, feedback item, remediation statement, lesson placement, and mapping row. Compare the mapping worksheet with the Product Owner's legally owned edition. Do not copy Penguin text, exercises, answer keys, screenshots, or scans into the repository or the worksheet.

        ## What approval means

        A later approval applies only to version $UNIT1_REVIEW_VERSION_NAME and digest ${review.instructionalContentDigest}. Any later instructional change invalidates the affected approval. Leave disposition, correction comment, reviewer identity, review date, and reviewed digest blank until that human review happens. Allowed disposition values are APPROVED, CORRECTION_REQUIRED, and REJECTED.

        Unresolved findings, corrections, or a missing owned-edition comparison prevent APPROVED, MAPPING_APPROVED, INSTALLABLE, and INSTALLED. This packet must not be used to mark Unit 1 reviewed, installable, installed, or as an approved edition alignment.

        ## Generator

        The deterministic generator and validator live in `app/src/test/java/dev/russiancompanion/v2/review/Unit1ReviewPackage.kt`. Regenerate the files in `review/unit-1/` from the packaged curriculum after any instructional change, then review the new digest.
    """.trimIndent() + "\n"
}

private fun packReviewRecord(pack: V2CurriculumPack): Unit1ReviewRecord {
    val review = pack.linguisticReview
    return record(
        id = review.id,
        type = "PACK_REVIEW",
        objectiveIds = emptyList(),
        sessionIds = emptyList(),
        content = listOf(
            "status=${review.status}",
            "reviewer=${review.reviewer.orEmpty()}",
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
    val hash = sha256(listOf(type, id, content, sourceId, rightsIds.joinToString(","), classification, reviewStatus).joinToString("\u001f"))
    return Unit1ReviewRecord(id, type, objectiveIds, sessionIds, content, sourceId, rightsIds, classification, reviewStatus, hash)
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
