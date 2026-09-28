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
internal const val UNITS_1_5_CURRICULUM_COMMIT = "4a6c21da07b515df226a090cc0ff6b1ff4a78aa1"

internal const val UNITS_6_10_CURRICULUM_COMMIT = "1e469c124966401a54f9a3da0d52606c1afd6ea2"

internal const val UNITS_11_15_CURRICULUM_COMMIT = "b22fa0cca895a677386a77d03a63301f7454aa07"

internal const val UNITS_16_20_CURRICULUM_COMMIT = "1e469c124966401a54f9a3da0d52606c1afd6ea2"

internal const val UNITS_21_25_CURRICULUM_COMMIT = "9594f75ede55d579ae4c4d8d6cdc30db4d6c30db"

internal const val UNITS_26_30_CURRICULUM_COMMIT = "5ab3cfe06e709ee605a3cbce320d6cbea9e11776"

internal fun curriculumCommitForUnit(unitNumber: Int): String = when (unitNumber) {
    in 1..2 -> UNITS_1_5_CURRICULUM_COMMIT
    in 3..5 -> UNITS_11_15_CURRICULUM_COMMIT
    in 6..10 -> UNITS_6_10_CURRICULUM_COMMIT
    in 11..15 -> UNITS_11_15_CURRICULUM_COMMIT
    in 16..20 -> UNITS_16_20_CURRICULUM_COMMIT
    in 21..25 -> UNITS_21_25_CURRICULUM_COMMIT
    in 26..30 -> UNITS_26_30_CURRICULUM_COMMIT
    else -> UNITS_11_15_CURRICULUM_COMMIT
}

internal const val CURRICULUM_SOURCE_COMMIT = UNITS_6_10_CURRICULUM_COMMIT

internal const val UNIT1_REVIEW_SOURCE_COMMIT = UNITS_1_5_CURRICULUM_COMMIT

internal const val REVIEW_SOURCE_COMMIT = UNITS_1_5_CURRICULUM_COMMIT

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
    "mapping_record_id",
    "edition_identification",
    "author",
    "publisher_edition",
    "observed_edition_notes",
    "isbn_observation",
    "isbn",
    "owned_copy_verified",
    "corresponding_lesson",
    "corresponding_pages",
    "corresponding_topic",
    "observation_source",
    "unresolved_questions",
    "alignment_disposition",
    "mapping_package_digest",
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
    "isbn",
    "owned_copy_verified",
    "variance_conflict",
    "reviewer_notes",
    "reviewer_identity",
    "review_date",
    "reviewed_version_digest",
    "reviewer_qualification",
)

private const val OBSERVED_EDITION_TITLE = "The New Penguin Russian Course: A Complete Course for Beginners"

private const val OBSERVED_EDITION_AUTHOR = "Nicholas J. Brown"

private const val OBSERVED_PUBLISHER_IMPRINT = "Penguin Books"

private const val OBSERVED_EDITION_NOTES =
    "Introduction signed Nicholas J. Brown, London, 1996. Scanned file extent was 528 pages. ISBN was not visible and is not recorded."

private const val MAPPING_OBSERVATION_SOURCE =
    "Product Owner structural mapping input v1.0. Page numbers are printed book pages. The input is private and is not repository curriculum."

private const val UNITS_11_15_EDITION_NOTES =
    "Introduction signed Nicholas J. Brown, London, 1996. Scanned file extent was 528 pages. ISBN was not visible and is not recorded. The Product Owner asserts that the inspected file is a legally owned copy. That assertion is not reviewer verification. Private-file SHA-256 is 2bcc0c45fac4508adf283f4dd939a7e578cf2c1306fdadf4966300532beb6303."

private const val UNITS_11_15_OBSERVATION_SOURCE =
    "Product Owner structural mapping input for Units 11-15, v1.0, visually inspected on 23 September 2026. Page numbers are printed book pages. The private PDF is not repository curriculum."

private const val UNITS_16_20_EDITION_NOTES =
    "Introduction signed Nicholas J. Brown, London, 1996. Scanned file extent was 528 pages. ISBN was not visible and is not recorded. The Product Owner asserts that the inspected file is a legally owned copy. That assertion is not reviewer verification. Private-file SHA-256 is 2bcc0c45fac4508adf283f4dd939a7e578cf2c1306fdadf4966300532beb6303."

private const val UNITS_16_20_OBSERVATION_SOURCE =
    "Product Owner structural mapping input for Units 16-20, v1.0, visually inspected on 24 September 2026. Page numbers are printed book pages. The private PDF is not repository curriculum."

private const val UNITS_21_25_EDITION_NOTES =
    "Introduction signed Nicholas J. Brown, London, 1996. Scanned file extent was 528 pages. ISBN was not visible and is not recorded. The Product Owner asserts that the inspected file is a legally owned copy. That assertion is not reviewer verification."

private const val UNITS_21_25_OBSERVATION_SOURCE =
    "Product Owner structural mapping input for Units 21-25, v1.0, visually inspected on 24 September 2026. Page numbers are printed book pages. The private PDF is not repository curriculum."

private const val UNITS_26_30_EDITION_NOTES =
    "Introduction signed Nicholas J. Brown, London, 1996. Scanned file extent was 528 pages. ISBN was not visible and is not recorded. The Product Owner asserts that the inspected file is a legally owned copy. That assertion is not reviewer verification."

private const val UNITS_26_30_OBSERVATION_SOURCE =
    "Product Owner structural mapping input for Units 26-30, v1.0, visually inspected on 24 September 2026. Page numbers are printed book pages. The private PDF is not repository curriculum. Classification RESEARCH_GAPS_BLOCK_IMPLEMENTATION. This observation is not an approved alignment."

private const val MAPPING_UNRESOLVED_QUESTIONS =
    "Confirm the exact edition and ISBN from the purchase record. Confirm each printed page range against the owned copy. Record any variance. Confirm whether app objectives cover the required skill progression without later material. Confirm Unit 1 and Unit 2 linguistic accuracy. Review Units 3-5 only after lawful source-based content is prepared. The ownership statement is not reviewer verification. Qualified linguistic review is not recorded here."

private data class StructuralMappingObservation(
    val unitNumber: Int,
    val lessonLabel: String,
    val printedPages: String,
    val topic: String,
    val observedEditionNotes: String = OBSERVED_EDITION_NOTES,
    val observationSource: String = MAPPING_OBSERVATION_SOURCE,
)

private val STRUCTURAL_MAPPING_OBSERVATIONS = listOf(
    StructuralMappingObservation(
        1,
        "Lesson 1",
        "1-11",
        "Cyrillic reading foundation: alphabet, pronunciation orientation, transliteration, and street-sign reading",
    ),
    StructuralMappingObservation(
        2,
        "Lesson 2",
        "12-23",
        "Russian handwriting and additional pronunciation",
    ),
    StructuralMappingObservation(
        3,
        "Lesson 3",
        "24-35",
        "Everyday phrases, naming things, introductory grammar, and grammatical gender",
    ),
    StructuralMappingObservation(
        4,
        "Lesson 4",
        "36-45",
        "Verbs, personal pronouns, present tense, conjugation, word stress, and introduction to nouns and cases",
    ),
    StructuralMappingObservation(
        5,
        "Lesson 5",
        "46-55",
        "Asking questions, the prepositional case, indeclinable nouns, and dialogue work",
    ),
    StructuralMappingObservation(
        6,
        "Lesson 6",
        "56-67",
        "Possession, destinations and movement, the accusative, and personal-name orientation",
    ),
    StructuralMappingObservation(
        7,
        "Lesson 7",
        "68-80",
        "Adjectives, agreement, question and demonstrative forms, case agreement, and introductory adverbs",
    ),
    StructuralMappingObservation(
        8,
        "Lesson 8",
        "81-91",
        "Noun plurals, spelling rules, and availability",
    ),
    StructuralMappingObservation(
        9,
        "Lesson 9",
        "92-106",
        "Cardinal numbers, quantity-driven genitive, and neutral quantity practice",
    ),
    StructuralMappingObservation(
        10,
        "Lesson 10",
        "107-121",
        "Possession and existence, genitive pronouns and prepositions, and genitive adjective agreement",
    ),
    StructuralMappingObservation(
        11,
        "Lesson 11",
        "122-132",
        "Past-tense formation and use, including reflexive verbs",
        UNITS_11_15_EDITION_NOTES,
        UNITS_11_15_OBSERVATION_SOURCE,
    ),
    StructuralMappingObservation(
        12,
        "Lesson 12",
        "133-148",
        "Future formation, introductory verbal aspect, the dative case, additional spelling rules, and prepositional plural",
        UNITS_11_15_EDITION_NOTES,
        UNITS_11_15_OBSERVATION_SOURCE,
    ),
    StructuralMappingObservation(
        13,
        "Lesson 13",
        "149-161",
        "Aspect in past-tense contexts, tense selection, infinitive aspect, and reported speech",
        UNITS_11_15_EDITION_NOTES,
        UNITS_11_15_OBSERVATION_SOURCE,
    ),
    StructuralMappingObservation(
        14,
        "Lesson 14",
        "162-170",
        "Aspect in future-tense contexts and impersonal constructions",
        UNITS_11_15_EDITION_NOTES,
        UNITS_11_15_OBSERVATION_SOURCE,
    ),
    StructuralMappingObservation(
        15,
        "Lesson 15",
        "171-187",
        "Requests, imperative forms, and consolidation of aspect use",
        UNITS_11_15_EDITION_NOTES,
        UNITS_11_15_OBSERVATION_SOURCE,
    ),
    StructuralMappingObservation(
        16,
        "Lesson 16",
        "188-200",
        "Instrumental case: principal uses; singular and plural noun forms; adjective agreement; common instrumental-governing constructions; instrumental complements with identity, role, or becoming",
        UNITS_16_20_EDITION_NOTES,
        UNITS_16_20_OBSERVATION_SOURCE,
    ),
    StructuralMappingObservation(
        17,
        "Lesson 17",
        "201-214",
        "Time, weekdays, calendar dates, years, age expressions, and ordinal numbers",
        UNITS_16_20_EDITION_NOTES,
        UNITS_16_20_OBSERVATION_SOURCE,
    ),
    StructuralMappingObservation(
        18,
        "Lesson 18",
        "215-229",
        "Comparative forms, superlative expression, and relative clauses built with the inflected relative pronoun который",
        UNITS_16_20_EDITION_NOTES,
        UNITS_16_20_OBSERVATION_SOURCE,
    ),
    StructuralMappingObservation(
        19,
        "Lesson 19",
        "230-239",
        "Conditional constructions with бы; obligation or probability with forms of должен; productive meanings of selected verbal prefixes",
        UNITS_16_20_EDITION_NOTES,
        UNITS_16_20_OBSERVATION_SOURCE,
    ),
    StructuralMappingObservation(
        20,
        "Lesson 20",
        "240-256",
        "Unidirectional and multidirectional verbs of motion; motion by foot or transport; carrying/leading motion pairs; selected prefixed motion verbs; cumulative review through Lesson 20",
        UNITS_16_20_EDITION_NOTES,
        UNITS_16_20_OBSERVATION_SOURCE,
    ),
    StructuralMappingObservation(
        21,
        "Lesson 21",
        "259-270",
        "Possession with the reflexive possessive pronoun; reflexive reference; and purpose clauses",
        UNITS_21_25_EDITION_NOTES,
        UNITS_21_25_OBSERVATION_SOURCE,
    ),
    StructuralMappingObservation(
        22,
        "Lesson 22",
        "271-284",
        "Numeral behavior beyond basic counting, including declined numerals, forms for both and one and a half, compound quantities, and collective or quantity expressions",
        UNITS_21_25_EDITION_NOTES,
        UNITS_21_25_OBSERVATION_SOURCE,
    ),
    StructuralMappingObservation(
        23,
        "Lesson 23",
        "285-295",
        "Expressions of elapsed time, duration, ago, during, and distinctions involving completion over a period",
        UNITS_21_25_EDITION_NOTES,
        UNITS_21_25_OBSERVATION_SOURCE,
    ),
    StructuralMappingObservation(
        24,
        "Lesson 24",
        "296-306",
        "Negation, case behavior under negation, negative pronouns or particles, and the position or scope of \u043d\u0435",
        UNITS_21_25_EDITION_NOTES,
        UNITS_21_25_OBSERVATION_SOURCE,
    ),
    StructuralMappingObservation(
        25,
        "Lesson 25",
        "307-321",
        "Diminutives, Russian personal-name forms, social address, requests, apologies, and polite expressions",
        UNITS_21_25_EDITION_NOTES,
        UNITS_21_25_OBSERVATION_SOURCE,
    ),
    StructuralMappingObservation(
        26,
        "Lesson 26",
        "320-331",
        "Stock expressions; indefinite pronouns and adverbs; word order; postal addresses; letter writing; vocabulary and correspondence work",
        UNITS_26_30_EDITION_NOTES,
        UNITS_26_30_OBSERVATION_SOURCE,
    ),
    StructuralMappingObservation(
        27,
        "Lesson 27",
        "332-342",
        "Passive participles: overview, past passive formation and use, optional present passive material, vocabulary and comprehension",
        UNITS_26_30_EDITION_NOTES,
        UNITS_26_30_OBSERVATION_SOURCE,
    ),
    StructuralMappingObservation(
        28,
        "Lesson 28",
        "343-354",
        "Imperfective and perfective verbal adverbs, formation and use, vocabulary and readings",
        UNITS_26_30_EDITION_NOTES,
        UNITS_26_30_OBSERVATION_SOURCE,
    ),
    StructuralMappingObservation(
        29,
        "Lesson 29",
        "355-367",
        "Present and past active participles, participles as adjectives, optional punctuation, long and short adjectives, and bookish reading",
        UNITS_26_30_EDITION_NOTES,
        UNITS_26_30_OBSERVATION_SOURCE,
    ),
    StructuralMappingObservation(
        30,
        "Lesson 30",
        "368-377",
        "Course orientation, dictionary use, abbreviations, review and application of letter names, particles, vocabulary and a final reading",
        UNITS_26_30_EDITION_NOTES,
        UNITS_26_30_OBSERVATION_SOURCE,
    ),
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

internal data class MappingPackageFacts(
    val unitNumber: Int,
    val mappingRecordId: String?,
    val editionTitle: String?,
    val author: String?,
    val publisherImprint: String?,
    val observedEditionNotes: String?,
    val proposedLessonNumber: Int?,
    val printedPageStart: Int?,
    val printedPageEnd: Int?,
    val topicDescription: String?,
    val observationSource: String?,
    val isbnObservation: String?,
    val isbn: String?,
    val mappingDisposition: String?,
)

internal data class Unit1ReviewPackage(
    val unitNumber: Int,
    val contentReviewDigest: String,
    val packContentReviewDigest: String,
    val mappingPackageDigest: String,
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

internal fun buildUnitReviewPackage(
    pack: V2CurriculumPack,
    unitNumber: Int,
    mappingFacts: MappingPackageFacts = governedMappingFacts(unitNumber),
): Unit1ReviewPackage {
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
        val mappingDigest = mappingPackageDigest(mappingFacts)
        return Unit1ReviewPackage(
            unitNumber = unitNumber,
            contentReviewDigest = instructional,
            packContentReviewDigest = V2Validator.contentReviewDigest(pack),
            mappingPackageDigest = mappingDigest,
            unitReviewStatus = "MISSING",
            reviewPackageDigest = reviewPackageDigest(
                unitNumber,
                "MISSING",
                "",
                "",
                "MISSING",
                instructional,
                mappingDigest,
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
    pack.provenanceRecords.filter { it.unitNumber == unitNumber }.sortedBy { it.itemId }.forEach { provenance ->
        add(provenanceRecord(pack, unit, provenance))
        if (unit.objectives.none { it.id == provenance.objectiveId }) {
            unresolved += "Provenance ${provenance.itemId} objective ${provenance.objectiveId} cannot be resolved"
        } else {
            addEdge(Unit1ReviewEdge(provenance.objectiveId, provenance.itemId, "PROVENANCE"))
        }
        if (provenance.audioAssetId != null && pack.audioAssets.none { it.id == provenance.audioAssetId }) {
            unresolved += "Provenance ${provenance.itemId} audio ${provenance.audioAssetId} cannot be resolved"
        }
    }
    pack.authoredEnglishRecords.filter { it.unitNumber == unitNumber }.sortedBy { it.id }.forEach { authored ->
        add(authoredEnglishRecord(pack, authored))
    }
    if (unitNumber >= 26) {
        pack.sourceCapabilities.filter { it.unitNumber == unitNumber }.sortedBy { it.id }.forEach { capability ->
            add(
                record(
                    id = capability.id,
                    type = "SOURCE_CAPABILITY",
                    objectiveIds = listOf(capability.objectiveId),
                    sessionIds = emptyList(),
                    content = listOf(
                        "sourceId=${capability.sourceId}",
                        "sourceRevision=${capability.sourceRevision}",
                        "sourceRecordId=${capability.sourceRecordId}",
                        "sourceHash=${capability.sourceHash}",
                        "supportedClaim=${capability.supportedClaim}",
                        "exactSpan=${capability.exactSpan}",
                    ).joinToString("\n"),
                    sourceId = capability.sourceId,
                    rightsIds = pack.rights.filter { it.sourceId == capability.sourceId }.map { it.id },
                    classification = "SOURCE_CAPABILITY",
                    reviewStatus = "PENDING_QUALIFIED_REVIEW",
                ),
            )
            if (unit.objectives.none { it.id == capability.objectiveId }) {
                unresolved += "Source capability ${capability.id} objective ${capability.objectiveId} cannot be resolved"
            } else {
                addEdge(Unit1ReviewEdge(capability.objectiveId, capability.id, "SOURCE_CAPABILITY"))
            }
        }
    }
    val letterIds = exampleIds.mapNotNull { pack.example(it)?.letterId?.takeIf { id -> id.isNotBlank() } }.distinct()
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
    val mappingDigest = mappingPackageDigest(mappingFacts)
    return Unit1ReviewPackage(
        unitNumber = unitNumber,
        contentReviewDigest = instructional,
        packContentReviewDigest = V2Validator.contentReviewDigest(pack),
        mappingPackageDigest = mappingDigest,
        unitReviewStatus = unitReviewStatus,
        reviewPackageDigest = reviewPackageDigest(
            unitNumber,
            unit.installState,
            unit.readiness,
            unit.mappingStatus,
            unitReviewStatus,
            instructional,
            mappingDigest,
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
        val mappingDigest = jsonStringField(manifest, "mappingPackageDigest")
        val packageDigest = jsonStringField(manifest, "reviewPackageDigest")
        val sourceDigest = jsonStringField(manifest, "repositorySourceDigest")
        if (digest != rendered.contentReviewDigest) {
            errors += "Content-review digest does not match the application digest"
        }
        if (mappingDigest != rendered.mappingPackageDigest) {
            errors += "Mapping package digest does not match the structural mapping"
        }
        if (packageDigest != rendered.reviewPackageDigest) {
            errors += "Review package digest does not match the review package"
        }
        if (mappingDigest != null && mappingDigest == digest) {
            errors += "Mapping package digest is only an alias of the content-review digest"
        }
        if (mappingDigest != null && mappingDigest == packageDigest) {
            errors += "Review package digest is only an alias of the mapping package digest"
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
        val unitCommit = curriculumCommitForUnit(unitNumber)
        if (recordedCommit != unitCommit) {
            errors += "Package curriculum source commit does not match the curriculum revision"
        }
        if (recordedCommit != null) {
            errors += curriculumSourceCommitErrors(recordedCommit, reviewRepositoryRoot(), unitNumber)
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
    val instructional = files.filterKeys { it != reviewMappingName(unitNumber) }.values.joinToString("\n")
    if (combined.contains("Penguin-aligned")) errors += "Package describes Unit $unitNumber as Penguin-aligned"
    if (instructional.contains("The New Penguin Russian Course")) errors += "Package introduces Penguin prose"
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
    val observation = STRUCTURAL_MAPPING_OBSERVATIONS.firstOrNull { it.unitNumber == unitNumber }
    if (body.isNotEmpty() && observation == null) {
        errors += "Mapping worksheet has rows for Unit $unitNumber without a structural observation"
    }
    body.forEach { row ->
        if (row.size != MAPPING_HEADER.size) {
            errors += "Mapping worksheet row ${row.firstOrNull()} has the wrong column count"
            return@forEach
        }
        val value = { column: String -> row[MAPPING_HEADER.indexOf(column)] }
        BLANK_MAPPING_COLUMNS.forEach { column ->
            if (value(column).isNotBlank()) {
                errors += "Mapping worksheet pre-populates $column for ${row[0]}"
            }
        }
        if (value("isbn").isNotBlank() || Regex("(?i)\\b97[89]\\d{10}\\b").containsMatchIn(value("isbn"))) {
            errors += "Mapping worksheet records an invented ISBN for ${row[0]}"
        }
        if (value("app_title").contains(OBSERVED_EDITION_TITLE)) {
            errors += "Mapping worksheet places edition identity in app content for ${row[0]}"
        }
        listOf(
            "edition_identification",
            "author",
            "publisher_edition",
            "observed_edition_notes",
            "corresponding_lesson",
            "corresponding_pages",
            "observation_source",
            "unresolved_questions",
        ).forEach { column ->
            if (Regex("[\\u0400-\\u04FF]").containsMatchIn(value(column))) {
                errors += "Mapping worksheet introduces Russian lesson content in $column for ${row[0]}"
            }
        }
        val topic = value("corresponding_topic")
        if (Regex("[\\u0400-\\u04FF]").containsMatchIn(topic) && topic != observation?.topic) {
            errors += "Mapping worksheet introduces Russian lesson content in corresponding_topic for ${row[0]}"
        }
        if (observation != null) {
            if (value("edition_identification") != OBSERVED_EDITION_TITLE ||
                value("author") != OBSERVED_EDITION_AUTHOR ||
                value("publisher_edition") != OBSERVED_PUBLISHER_IMPRINT
            ) {
                errors += "Mapping worksheet is missing edition identity for ${row[0]}"
            }
            if (value("corresponding_lesson") != observation.lessonLabel || value("corresponding_lesson") != "Lesson $unitNumber") {
                errors += "Mapping worksheet is for the wrong unit or lesson for ${row[0]}"
            }
            if (value("corresponding_pages") != observation.printedPages) {
                errors += "Mapping worksheet page range does not match the observed pages for ${row[0]}"
            }
            if (value("corresponding_pages").isBlank() && value("alignment_disposition") == "APPROVED") {
                errors += "Mapping worksheet approves a blank page range for ${row[0]}"
            }
            if (value("corresponding_topic") != observation.topic) {
                errors += "Mapping worksheet topic does not match the structural observation for ${row[0]}"
            }
            if (value("observation_source") != observation.observationSource) {
                errors += "Mapping worksheet observation source does not match the structural input for ${row[0]}"
            }
            if (value("observed_edition_notes") != observation.observedEditionNotes) {
                errors += "Mapping worksheet edition notes do not match the structural observation for ${row[0]}"
            }
            if (value("alignment_disposition") != "PENDING_CURRICULUM_REVIEWER") {
                errors += "Mapping worksheet disposition for ${row[0]} is not pending"
            }
            if (value("alignment_disposition") == "APPROVED") {
                if (value("corresponding_pages").isBlank()) {
                    errors += "Mapping worksheet approves a blank page range for ${row[0]}"
                }
                if (value("reviewer_identity").isBlank() || value("reviewer_qualification").isBlank()) {
                    errors += "Mapping worksheet is APPROVED without an identified qualified reviewer for ${row[0]}"
                }
                val digest = value("reviewed_version_digest")
                if (digest != V2Validator.unitContentReviewDigest(pack, unitNumber)) {
                    errors += "Mapping worksheet reviewed content digest does not match the unit content-review digest for ${row[0]}"
                }
            }
            if (value("isbn_observation") != "UNRESOLVED") {
                errors += "Mapping worksheet ISBN observation is not unresolved for ${row[0]}"
            }
            if (value("mapping_record_id") != "map-pending-unit-$unitNumber") {
                errors += "Mapping worksheet is for the wrong unit or lesson for ${row[0]}"
            }
            val parsed = mappingFactsFromRow(unitNumber, row)
            val computed = mappingPackageDigest(parsed)
            if (value("mapping_package_digest") != computed) {
                errors += "Mapping package digest does not match the structural mapping facts for ${row[0]}"
            }
            if (computed != mappingPackageDigest(governedMappingFacts(unitNumber))) {
                errors += "Mapping package digest does not match the governed mapping for ${row[0]}"
            }
        }
    }
    if (Regex("(?i)\\b97[89]\\d{10}\\b|\\b\\d{9}[\\dX]\\b").containsMatchIn(mapping)) {
        errors += "Mapping worksheet records an invented ISBN"
    }
    // Matches a fixed list of known prohibited mapping-input markers only.
    // This is not comprehensive copyright detection and does not recognize every copy or paraphrase.
    if (mapping.contains(".pdf", ignoreCase = true) || mapping.contains("PENGUIN_EDITION_MAPPING_INPUT")) {
        errors += "Mapping worksheet matches a known prohibited mapping-input marker"
    }
    val governedDigest = mappingPackageDigest(governedMappingFacts(unitNumber))
    if (STRUCTURAL_MAPPING_OBSERVATIONS.any { it.unitNumber == unitNumber } && !mapping.contains("mappingPackageDigest: $governedDigest")) {
        errors += "Mapping worksheet omits the mapping package digest"
    }
    if (!mapping.contains("must not contain copied Penguin prose")) {
        errors += "Mapping worksheet omits the Penguin-prose warning"
    }
    if (!mapping.contains("Observed edition facts") || !mapping.contains("Proposed structural mapping") || !mapping.contains("Unresolved facts") || !mapping.contains("Qualified linguistic review")) {
        errors += "Mapping worksheet does not distinguish observed, proposed, unresolved, and linguistic-review facts"
    }
    return errors.distinct()
}

private fun renderPacket(pack: V2CurriculumPack, review: Unit1ReviewPackage): String {
    val unitRecord = review.records.firstOrNull { it.type == "UNIT" }
    val unit = pack.unit(review.unitNumber)
    return buildString {
        append("# Unit ${review.unitNumber} linguistic and owned-edition mapping packet\n\n")
        append("This packet prepares review. It records an unsigned structural mapping proposal. It does not record a qualified review, an approved edition alignment, or an install decision.\n\n")
        append("The owned-edition mapping worksheet separates observed edition facts, the proposed lesson and printed page range, unresolved edition questions, and matters that still require qualified linguistic review. ")
        append("ISBN, owned-copy verification, variance, reviewer identity, qualification, date, and approval stay blank. ")
        append("The disposition remains PENDING_CURRICULUM_REVIEWER.\n\n")
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
        append("- Curriculum source commit: ${curriculumCommitForUnit(review.unitNumber)}\n")
        append("- Unit content-review SHA-256: ${review.contentReviewDigest}\n")
        append("- Mapping-package SHA-256: ${review.mappingPackageDigest}\n")
        append("- Pack content-review SHA-256: ${review.packContentReviewDigest}\n")
        append("- Review-package SHA-256: ${review.reviewPackageDigest}\n")
        append("- Repository source SHA-256: ${review.repositorySourceDigest}\n\n")
        append("`unitContentReviewDigest` binds this unit's instructional curriculum. ")
        append("It covers this unit and the reachable instructional records, relationships, shared dependencies, sources, rights, assets, and advancement rule. ")
        append("It does not cover any other unit. Recording a review status, reviewer signature, mapping approval, install state, or readiness does not change it.\n\n")
        append("`mappingPackageDigest` binds the proposed edition-mapping facts for Unit ${review.unitNumber}: edition identity, lesson, printed page range, topic description, observation source, ISBN observation state, and the pending disposition. ")
        append("Reviewer identity, qualification, date, notes, correction notes, and signature stay outside that digest.\n\n")
        append("`packContentReviewDigest` is the repository aggregate of instructional curriculum. It is not an approval of this unit, and it is not part of the review-package digest.\n\n")
        append("`reviewPackageDigest` identifies this complete pending review package. It binds the ordered instructional records and the mapping-package digest. ")
        append("Changing a proposed page range or topic changes the mapping-package digest and therefore this package digest.\n\n")
        append("`repositorySourceDigest` binds repository and release inputs. Recording an approval later changes that repository hash. ")
        append("That change does not replace the signed content-review digest or the mapping-package digest.\n\n")
        append("The marker scan used by this package detects only a fixed list of known prohibited mapping-input markers. ")
        append("It is not comprehensive copyright detection and cannot recognize every copy or close paraphrase of protected material.\n\n")
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
        if (review.unitNumber >= 26 && unit != null) {
            append("\n## Unsupported-capability register\n\n")
            val supported = pack.sourceCapabilities.filter { it.unitNumber == review.unitNumber }
            if (supported.isEmpty()) append("None admitted.\n\n") else supported.forEach { append("- `${it.id}` supports `${it.supportedClaim}` for `${it.objectiveId}`\n") }
            val unsupported = unit.objectives.filter { it.kind == "GUIDANCE" }
            if (unsupported.isEmpty()) append("\nNo guidance objective is recorded.\n\n") else {
                append("\nGuidance objectives stay unsupported until an admitted frozen capability exists:\n\n")
                unsupported.forEach { append("- `${it.id}` ${it.title}\n") }
            }
            append("\n## Book-directed register\n\n")
            val bookSteps = unit.sessions.flatMap { it.steps }.filter { !it.bookDirection.isNullOrBlank() || !it.unavailableReason.isNullOrBlank() }
            if (bookSteps.isEmpty()) append("None.\n\n") else bookSteps.forEach { step ->
                append("- `${step.id}` ${step.bookDirection ?: step.unavailableReason.orEmpty()}\n")
            }
            append("\n## Optional non-gating register\n\n")
            val optional = unit.objectives.filter { !it.gating }
            if (optional.isEmpty()) append("None.\n\n") else optional.forEach { append("- `${it.id}` ${it.title}\n") }
        }
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
        append("  \"curriculumSourceCommit\": ${jsonString(curriculumCommitForUnit(review.unitNumber))},\n")
        append("  \"contentReviewDigest\": ${jsonString(review.contentReviewDigest)},\n")
        append("  \"mappingPackageDigest\": ${jsonString(review.mappingPackageDigest)},\n")
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
    val facts = governedMappingFacts(unitNumber)
    val digest = mappingPackageDigest(facts)
    val lines = mutableListOf(
        "# Warning: this worksheet must not contain copied Penguin prose or exercises.",
        "# Observed edition facts: edition_identification, author, publisher_edition, and observed_edition_notes.",
        "# Proposed structural mapping: corresponding_lesson, corresponding_pages, corresponding_topic, and alignment_disposition PENDING_CURRICULUM_REVIEWER.",
        "# Unresolved facts: isbn, owned_copy_verified, variance_conflict, and unresolved_questions. ISBN was not visible and must stay blank. isbn_observation stays UNRESOLVED.",
        "# Qualified linguistic review is still required. Reviewer identity, qualification, date, notes, and reviewed digest stay blank. This worksheet is not an approval.",
        "# mappingRecordId: ${facts.mappingRecordId.orEmpty()}",
        "# mappingPackageDigest: $digest",
        "# mappingPackageDigest binds the proposed edition-mapping facts. Reviewer identity, qualification, date, notes, correction notes, and signature are outside this digest.",
        "# The marker scan detects only known prohibited mapping-input markers. It is not comprehensive copyright detection.",
        MAPPING_HEADER.joinToString("\t"),
    )
    val populated = facts.mappingRecordId != null
    if (unitNumber in 26..30 && populated) {
        lines.add(
            8,
            "# This worksheet records a pending structural observation only. It is not a review packet and it does not approve instructional content.",
        )
    }
    if (unit.objectives.isEmpty() && unit.sessions.isEmpty()) {
        if (unitNumber !in 26..30) {
            lines.add(
                8,
                "# This worksheet records a pending structural observation only. It is not a review packet and it does not approve instructional content.",
            )
        }
        lines += mappingRow(unit.id, "UNIT", unit.title, if (populated) facts else null, digest)
    } else {
        unit.objectives.forEach { objective ->
            lines += mappingRow(objective.id, "OBJECTIVE", objective.title, if (populated) facts else null, digest)
        }
        unit.sessions.forEach { session ->
            lines += mappingRow(session.id, "SESSION", session.title, if (populated) facts else null, digest)
        }
    }
    return lines.joinToString("\n") + "\n"
}

internal fun structuralMappingWorksheet(pack: V2CurriculumPack, unitNumber: Int): String {
    return renderMappingWorksheet(pack, unitNumber)
}

private fun mappingRow(
    id: String,
    type: String,
    title: String,
    facts: MappingPackageFacts?,
    digest: String,
): String {
    val structural = if (facts == null) {
        List(15) { "" }
    } else {
        listOf(
            facts.mappingRecordId.orEmpty(),
            facts.editionTitle.orEmpty(),
            facts.author.orEmpty(),
            facts.publisherImprint.orEmpty(),
            facts.observedEditionNotes.orEmpty(),
            facts.isbnObservation.orEmpty(),
            facts.isbn.orEmpty(),
            "",
            "Lesson ${facts.proposedLessonNumber}",
            "${facts.printedPageStart}-${facts.printedPageEnd}",
            facts.topicDescription.orEmpty(),
            facts.observationSource.orEmpty(),
            MAPPING_UNRESOLVED_QUESTIONS,
            facts.mappingDisposition.orEmpty(),
            digest,
        )
    }
    return (listOf(id, type, title) + structural + List(6) { "" })
        .joinToString("\t") { escapeTsv(it) }
}

private fun renderInstructions(review: Unit1ReviewPackage): String {
    return """
        # Reviewer instructions for the Unit ${review.unitNumber} packet

        Schema version: $UNIT1_REVIEW_SCHEMA_VERSION
        Application: $UNIT1_REVIEW_APPLICATION_ID $UNIT1_REVIEW_VERSION_NAME ($UNIT1_REVIEW_VERSION_CODE)
        Curriculum source commit: ${curriculumCommitForUnit(review.unitNumber)}
        Unit content-review digest: ${review.contentReviewDigest}
        Mapping-package digest: ${review.mappingPackageDigest}
        Pack content-review digest: ${review.packContentReviewDigest}
        Review-package digest: ${review.reviewPackageDigest}
        Repository source digest: ${review.repositorySourceDigest}

        This packet was generated from the packaged curriculum at curriculum source commit ${curriculumCommitForUnit(review.unitNumber)}. That commit contains this unit's reviewed records. A later commit that changes only other units does not change this digest. This packet commit records that identity and does not perform review.

        ## Digests

        `unitContentReviewDigest` (manifest field `contentReviewDigest`) binds the instructional curriculum for Unit ${review.unitNumber}. It is canonical JSON, digest schema ${V2Validator.CONTENT_REVIEW_DIGEST_SCHEMA}, with scope `unit`. It includes this unit and the reachable instructional records, relationships, shared dependencies, sources, rights, assets, and advancement rule. It excludes review status, reviewer signature fields, the reviewed digest, mapping approval status, install state, and readiness. Recording those workflow values leaves this digest unchanged. A later change to substantive content reachable from this unit changes it and invalidates this unit's approval. A change that this unit cannot reach does not. Approval binds only to version $UNIT1_REVIEW_VERSION_NAME and this unit digest. Signing Unit ${review.unitNumber} does not approve any other unit.

        `mappingPackageDigest` binds the proposed edition-mapping facts in fixed order: unit number, mapping record id, edition title, author, publisher imprint, observed-edition notes, proposed lesson number, printed page start, printed page end, topic description, observation source, ISBN observation state, ISBN value or null, and the current mapping disposition. Reviewer identity, reviewer qualification, review date, reviewer notes, correction notes, and signature are outside this digest. Completing those workflow fields does not rewrite the mapping facts. Changing the lesson, page range, title, author, publisher, topic, observation source, ISBN state, or ISBN value changes this digest.

        `packContentReviewDigest` is the pack-wide instructional aggregate used for repository and release integrity. It is not the digest a unit reviewer signs, and it is not hashed into `reviewPackageDigest`.

        `reviewPackageDigest` identifies the complete pending review package. It binds the manifest metadata, ordered instructional records, relationship hashes, book-directed and unavailable lists, the pending instructional workflow fields, the curriculum source commit, the unit content-review digest, and `mappingPackageDigest`. A changed mapping fact changes this package digest because the mapping-package digest is an input. It does not approve the curriculum by itself.

        `repositorySourceDigest` binds repository and release inputs. It tracks the repository and release state after an approval is recorded. A later commit changes it. Release-gate validation and the state-transition rules still have to accept that repository state. It is not the digest the reviewer signs, and it is not a substitute for `mappingPackageDigest`.

        `curriculumSourceCommit` is the commit that contains this unit's governed curriculum. It is an ancestor of the packet commit. A packet that names its own commit, or a commit that does not contain this curriculum, is rejected.

        ## Qualifications

        A reviewer must possess demonstrable advanced Russian competence and sufficient language-teaching or curriculum-review competence to validate Cyrillic letter identity, phonetics, instructional explanations, answer keys, feedback, remediation, and owned-edition placement. The repository does not name, appoint, or attest any reviewer. The reviewer qualification field stays blank until that person attests their own qualification.

        ## What must be checked

        Check every Cyrillic identity, phonetic statement, instructional explanation, answer key, feedback item, remediation statement, lesson placement, and mapping row. Compare the mapping worksheet with the Product Owner's legally owned edition. Do not copy protected text, exercises, answer keys, screenshots, or scans into the repository or the worksheet.

        The package marker scan detects only a fixed list of known prohibited mapping-input markers, such as the private mapping-input filename or a PDF extension inside the mapping worksheet. It is not comprehensive copyright detection. It cannot recognize every copy or close paraphrase of protected material. A qualified reviewer still has to make that comparison.

        ## What approval means

        A later approval applies only to version $UNIT1_REVIEW_VERSION_NAME, unit content-review digest ${review.contentReviewDigest}, and mapping-package digest ${review.mappingPackageDigest}. Confirm review-package digest ${review.reviewPackageDigest} and curriculum source commit ${curriculumCommitForUnit(review.unitNumber)} before relying on this packet. Any later substantive instructional change reachable from Unit ${review.unitNumber} invalidates this unit's content approval. Any later change to the proposed edition facts invalidates the mapping-package digest. Leave correction comment, reviewer identity, reviewer qualification, review date, and reviewed digest blank until that human review happens. Allowed linguistic-review disposition values are APPROVED, CORRECTION_REQUIRED, and REJECTED. The mapping disposition in this packet remains PENDING_CURRICULUM_REVIEWER.

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
        ).let { lines ->
            val withRationales = if (item.distractorRationales.isEmpty()) {
                lines
            } else {
                lines + "distractorRationales=${item.distractorRationales.joinToString(" | ")}"
            }
            val assessment = item.assessment ?: return@let withRationales
            withRationales + listOf(
                "testedFeature=${assessment.testedFeature}",
                "evidenceMode=${assessment.evidenceMode}",
                "contrastSetId=${assessment.contrastSetId}",
                "cueLanguage=${assessment.cueLanguage}",
                "independent=${assessment.independent}",
                "answerSourceIds=${assessment.answerSourceIds.joinToString(" | ")}",
                "distractorSourceIds=${assessment.distractorSourceIds.joinToString(" | ")}",
                "distractorDifference=${assessment.distractorDifference.joinToString(" | ")}",
                "whyAnswerDemonstratesObjective=${assessment.whyAnswerDemonstratesObjective}",
                "contrastMembers=${assessment.contrastMembers.joinToString(" | ")}",
                "relationKind=${assessment.relationKind}",
                "relationParticipantIds=${assessment.relationParticipantIds.joinToString(" | ")}",
            ) + if (assessment.responseKind.isBlank()) {
                emptyList()
            } else {
                listOf("responseKind=${assessment.responseKind}")
            }
        }.joinToString("\n"),
        sourceId = item.sourceId,
        rightsIds = rightsIds(pack, item.sourceId),
        classification = "",
        reviewStatus = item.reviewStatus,
    )
}

private fun provenanceRecord(
    pack: V2CurriculumPack,
    unit: V2Unit,
    provenance: dev.russiancompanion.v2.curriculum.V2ProvenanceRecord,
): Unit1ReviewRecord {
    return record(
        id = provenance.itemId,
        type = "PROVENANCE",
        objectiveIds = listOf(provenance.objectiveId),
        sessionIds = unit.sessions.filter { provenance.objectiveId in it.objectiveIds }.map { it.id },
        content = listOf(
            "recordType=${provenance.recordType}",
            "sourceUrl=${provenance.sourceUrl}",
            "sourceRevision=${provenance.sourceRevision}",
            "sourceRecordId=${provenance.sourceRecordId}",
            "originalSourceText=${provenance.originalSourceText}",
            "shippedText=${provenance.shippedText}",
            "transformation=${provenance.transformation}",
            "licenseId=${provenance.licenseId}",
            "licenseUrl=${provenance.licenseUrl}",
            "attribution=${provenance.attribution}",
            "shareAlikeTreatment=${provenance.shareAlikeTreatment}",
            "sourceHash=${provenance.sourceHash}",
            "contentHash=${provenance.contentHash}",
            "audioAssetId=${provenance.audioAssetId.orEmpty()}",
            "reviewerIdentity=${provenance.reviewerIdentity}",
            "reviewerQualification=${provenance.reviewerQualification}",
            "reviewDate=${provenance.reviewDate}",
            "reviewedDigest=${provenance.reviewedDigest}",
        ).joinToString("\n"),
        sourceId = provenance.sourceId,
        rightsIds = rightsIds(pack, provenance.sourceId),
        classification = "",
        reviewStatus = provenance.reviewStatus,
    )
}

private fun authoredEnglishRecord(
    pack: V2CurriculumPack,
    authored: dev.russiancompanion.v2.curriculum.V2AuthoredEnglishRecord,
): Unit1ReviewRecord {
    return record(
        id = authored.id,
        type = "AUTHORED_ENGLISH",
        objectiveIds = emptyList(),
        sessionIds = emptyList(),
        content = listOf(
            "classification=${authored.classification}",
            "text=${authored.text}",
        ).joinToString("\n"),
        sourceId = "src-app-english-v2",
        rightsIds = rightsIds(pack, "src-app-english-v2"),
        classification = authored.classification,
        reviewStatus = "PENDING_QUALIFIED_REVIEW",
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

internal fun governedMappingFacts(unitNumber: Int): MappingPackageFacts {
    val observation = STRUCTURAL_MAPPING_OBSERVATIONS.firstOrNull { it.unitNumber == unitNumber } ?: return MappingPackageFacts(
        unitNumber = unitNumber,
        mappingRecordId = null,
        editionTitle = null,
        author = null,
        publisherImprint = null,
        observedEditionNotes = null,
        proposedLessonNumber = null,
        printedPageStart = null,
        printedPageEnd = null,
        topicDescription = null,
        observationSource = null,
        isbnObservation = null,
        isbn = null,
        mappingDisposition = null,
    )
    val pages = observation.printedPages.split("-")
    return MappingPackageFacts(
        unitNumber = unitNumber,
        mappingRecordId = "map-pending-unit-$unitNumber",
        editionTitle = OBSERVED_EDITION_TITLE,
        author = OBSERVED_EDITION_AUTHOR,
        publisherImprint = OBSERVED_PUBLISHER_IMPRINT,
        observedEditionNotes = observation.observedEditionNotes,
        proposedLessonNumber = observation.lessonLabel.removePrefix("Lesson ").toInt(),
        printedPageStart = pages[0].toInt(),
        printedPageEnd = pages[1].toInt(),
        topicDescription = observation.topic,
        observationSource = observation.observationSource,
        isbnObservation = "UNRESOLVED",
        isbn = null,
        mappingDisposition = "PENDING_CURRICULUM_REVIEWER",
    )
}

internal fun mappingPackageDigest(facts: MappingPackageFacts): String {
    return sha256(
        canonObj(
            "unitNumber" to canonNum(facts.unitNumber),
            "mappingRecordId" to canonStrOrNull(facts.mappingRecordId),
            "editionTitle" to canonStrOrNull(facts.editionTitle),
            "author" to canonStrOrNull(facts.author),
            "publisherImprint" to canonStrOrNull(facts.publisherImprint),
            "observedEditionNotes" to canonStrOrNull(facts.observedEditionNotes),
            "proposedLessonNumber" to canonNumOrNull(facts.proposedLessonNumber),
            "printedPageStart" to canonNumOrNull(facts.printedPageStart),
            "printedPageEnd" to canonNumOrNull(facts.printedPageEnd),
            "topicDescription" to canonStrOrNull(facts.topicDescription),
            "observationSource" to canonStrOrNull(facts.observationSource),
            "isbnObservation" to canonStrOrNull(facts.isbnObservation),
            "isbn" to canonStrOrNull(facts.isbn),
            "mappingDisposition" to canonStrOrNull(facts.mappingDisposition),
        ),
    )
}

internal fun mappingFactsFromRow(unitNumber: Int, row: List<String>): MappingPackageFacts {
    fun value(column: String): String = row[MAPPING_HEADER.indexOf(column)]
    val pages = value("corresponding_pages").split("-")
    return MappingPackageFacts(
        unitNumber = unitNumber,
        mappingRecordId = value("mapping_record_id").ifBlank { null },
        editionTitle = value("edition_identification").ifBlank { null },
        author = value("author").ifBlank { null },
        publisherImprint = value("publisher_edition").ifBlank { null },
        observedEditionNotes = value("observed_edition_notes").ifBlank { null },
        proposedLessonNumber = value("corresponding_lesson").removePrefix("Lesson ").toIntOrNull(),
        printedPageStart = pages.getOrNull(0)?.toIntOrNull(),
        printedPageEnd = pages.getOrNull(1)?.toIntOrNull(),
        topicDescription = value("corresponding_topic").ifBlank { null },
        observationSource = value("observation_source").ifBlank { null },
        isbnObservation = value("isbn_observation").ifBlank { null },
        isbn = value("isbn").ifBlank { null },
        mappingDisposition = value("alignment_disposition").ifBlank { null },
    )
}

private fun reviewPackageDigest(
    unitNumber: Int,
    installState: String,
    readiness: String,
    mappingStatus: String,
    linguisticReviewStatus: String,
    contentReviewDigest: String,
    mappingPackageDigest: String,
    records: List<Unit1ReviewRecord>,
    edges: List<Unit1ReviewEdge>,
    bookDirectedIds: List<String>,
    unavailableIds: List<String>,
    unresolved: List<String>,
): String {
    return sha256(
        canonObj(
            "digestSchema" to "2",
            "schemaVersion" to UNIT1_REVIEW_SCHEMA_VERSION.toString(),
            "packageId" to canonStr(reviewPackageId(unitNumber)),
            "applicationId" to canonStr(UNIT1_REVIEW_APPLICATION_ID),
            "versionName" to canonStr(UNIT1_REVIEW_VERSION_NAME),
            "versionCode" to UNIT1_REVIEW_VERSION_CODE.toString(),
            "curriculumSourceCommit" to canonStr(curriculumCommitForUnit(unitNumber)),
            "contentReviewDigest" to canonStr(contentReviewDigest),
            "mappingPackageDigest" to canonStr(mappingPackageDigest),
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

private fun canonNum(value: Int): String = value.toString()

private fun canonNumOrNull(value: Int?): String = value?.toString() ?: "null"

private fun canonStrOrNull(value: String?): String = if (value == null) "null" else canonStr(value)

private fun canonStr(value: String): String = jsonString(value)

internal fun reviewRepositoryRoot(): File =
    listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }

internal fun curriculumSourceCommitErrors(recorded: String, repositoryRoot: File, unitNumber: Int): List<String> {
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
    try {
        val recordedPack = dev.russiancompanion.v2.curriculum.V2PackLoader.parse(committed)
        val headPack = dev.russiancompanion.v2.curriculum.V2PackLoader.parse(currentText)
        if (V2Validator.unitContentReviewDigest(recordedPack, unitNumber) != V2Validator.unitContentReviewDigest(headPack, unitNumber)) {
            errors += "Unit $unitNumber content-review digest at the curriculum source commit does not match this revision"
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
