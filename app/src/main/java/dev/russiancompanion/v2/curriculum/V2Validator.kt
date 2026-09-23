package dev.russiancompanion.v2.curriculum

import java.security.MessageDigest

internal object V2Validator {
    fun validate(pack: V2CurriculumPack): V2Validation {
        val errors = mutableListOf<String>()
        if (pack.schemaVersion != 1) errors += "Unsupported curriculum schema ${pack.schemaVersion}"
        if (pack.units.size != 30) errors += "The map must contain exactly 30 Penguin-aligned units"
        if (pack.units.map { it.number } != (1..30).toList()) {
            errors += "Units must be numbered 1-30 in order"
        }
        if (pack.admissions.generatedRussianWords != 0) errors += "Generated Russian words are forbidden"
        if (pack.admissions.penguinProseAssets != 0) errors += "Penguin prose assets are forbidden"
        if (pack.admissions.catalogAudioBoundToUnits != 0) {
            errors += "Catalog audio must not be bound to units without a mapping record"
        }
        if (pack.admissions.unverifiedExampleWords != 0) {
            errors += "Unverified example words are forbidden"
        }
        if (pack.admissions.futureRussianItems != 0) {
            errors += "Future Russian items must stay at zero until reviewed content exists"
        }
        if (pack.admissions.mappedPronunciationAudio != pack.audioAssets.size) {
            errors += "mappedPronunciationAudio does not match governed audio records"
        }
        if (pack.admissions.handwritingAssets != pack.handwritingAssets.size) {
            errors += "handwritingAssets does not match governed handwriting records"
        }
        validateAssetRecords(pack, errors)
        validateMappingInputStaysOutOfCurriculum(pack, errors)
        validateProvenance(pack, errors)
        validateAuthoredEnglish(pack, errors)
        validateReview(pack, errors)
        validateUnitReviews(pack, errors)
        validateSourcesAndRights(pack, errors)
        pack.units.forEach { unit ->
            if (unit.penguinLesson != unit.number) {
                errors += "Unit ${unit.number} penguinLesson must equal the unit number"
            }
            if (unit.installState !in CLOSED_INSTALL_STATES) {
                errors += "Unit ${unit.number} has unrecognized installState ${unit.installState}"
                return@forEach
            }
            when (unit.installState) {
                "INSTALLED" -> validateInstalled(pack, unit, errors)
                "DEVELOPMENT" -> validateDevelopment(pack, unit, errors)
                "NOT_YET_INSTALLED" -> validateUninstalled(unit, errors)
            }
            validateReadiness(pack, unit, errors)
        }
        validateReachableSessions(pack, errors)
        pack.items.forEach { item -> validateItem(pack, item, errors) }
        pack.examples.forEach { example -> validateExample(pack, example, errors) }
        pack.letters.forEach { letter -> validateLetter(pack, letter, errors) }
        pack.references.forEach { entry ->
            if (entry.reviewStatus !in CLOSED_REVIEW_STATES) {
                errors += "Reference ${entry.id} has unrecognized review status ${entry.reviewStatus}"
            }
            validateReferenceClassification(entry, errors)
            if (pack.sources.none { it.id == entry.sourceId }) {
                errors += "Reference ${entry.id} source ${entry.sourceId} is missing"
            }
        }
        return V2Validation(errors.isEmpty(), errors)
    }

    /**
     * SHA-256 of the pack-wide content-review aggregate, schema 2. This digest
     * is repository and release integrity. A unit reviewer does not sign it.
     * Workflow fields stay outside the document: review status, reviewer
     * signature, reviewed digest, mapping approval status, install state, and
     * readiness. Structural edition identity, proposed lesson, printed page
     * range, topic description, and ISBN observation are inside a mapping
     * record when one exists. Approval status and the reviewer signature on
     * that record stay outside.
     */
    internal fun contentReviewDigest(pack: V2CurriculumPack): String = sha256(canonicalInstructionalDocument(pack))

    /**
     * SHA-256 of the unit-scoped content-review digest. It covers that unit and
     * the instructional records, relationships, shared dependencies, sources,
     * rights, assets, and advancement rule reachable from it. Scope is part of
     * the document so this digest cannot collide with the pack aggregate.
     */
    internal fun unitContentReviewDigest(pack: V2CurriculumPack, unitNumber: Int): String =
        sha256(canonicalUnitInstructionalDocument(pack, unitNumber))

    internal fun instructionalContentDigest(pack: V2CurriculumPack): String = contentReviewDigest(pack)

    private fun sha256(text: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(text.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { byte -> "%02x".format(byte) }
    }

    internal const val CONTENT_REVIEW_DIGEST_SCHEMA = 2

    internal const val INSTRUCTIONAL_DIGEST_SCHEMA = CONTENT_REVIEW_DIGEST_SCHEMA

    private val ISBN_OBSERVATIONS = setOf("UNRESOLVED", "OBSERVED")

    private val MAPPING_STATUSES = setOf("PENDING_CURRICULUM_REVIEWER", "APPROVED", "REJECTED")

    private val REDISTRIBUTED_MAPPING_MARKERS = listOf(
        "the new penguin russian course",
        "penguin_edition_mapping_input",
        "mapping input v1",
    )

    private fun canonicalInstructionalDocument(pack: V2CurriculumPack): String {
        return canonObj(
            "digestSchema" to canonNum(CONTENT_REVIEW_DIGEST_SCHEMA),
            "schemaVersion" to canonNum(pack.schemaVersion),
            "packId" to canonStr(pack.packId),
            "packVersion" to canonNum(pack.packVersion),
            "sources" to canonArr(pack.sources.map { source ->
                canonObj(
                    "id" to canonStr(source.id),
                    "title" to canonStr(source.title),
                    "license" to canonStr(source.license),
                    "attribution" to canonStr(source.attribution),
                    "modification" to canonStr(source.modification),
                )
            }),
            "rights" to canonArr(pack.rights.map { rights ->
                canonObj(
                    "id" to canonStr(rights.id),
                    "sourceId" to canonStr(rights.sourceId),
                    "license" to canonStr(rights.license),
                    "permitsAppUse" to canonBool(rights.permitsAppUse),
                )
            }),
            "audioAssets" to canonArr(pack.audioAssets.map { asset ->
                canonObj(
                    "id" to canonStr(asset.id),
                    "sourceId" to canonStr(asset.sourceId),
                    "rightsId" to canonStr(asset.rightsId),
                    "unitNumber" to canonNum(asset.unitNumber),
                    "letterId" to canonStr(asset.letterId),
                )
            }),
            "handwritingAssets" to canonArr(pack.handwritingAssets.map { asset ->
                canonObj(
                    "id" to canonStr(asset.id),
                    "sourceId" to canonStr(asset.sourceId),
                    "rightsId" to canonStr(asset.rightsId),
                    "unitNumber" to canonNum(asset.unitNumber),
                    "letterId" to canonStr(asset.letterId),
                )
            }),
            "ownedEditionMappings" to canonArr(pack.ownedEditionMappings.map(::canonicalMapping)),
            "letters" to canonArr(pack.letters.map { letter ->
                canonObj(
                    "id" to canonStr(letter.id),
                    "uppercase" to canonStr(letter.uppercase),
                    "lowercase" to canonStr(letter.lowercase),
                    "group" to canonStr(letter.group),
                    "englishNote" to canonStr(letter.englishNote),
                    "sourceId" to canonStr(letter.sourceId),
                    "audioAssetId" to canonStrOrNull(letter.audioAssetId),
                    "exampleWordId" to canonStrOrNull(letter.exampleWordId),
                    "latinLookalike" to canonStrOrNull(letter.latinLookalike),
                )
            }),
            "examples" to canonArr(pack.examples.map { example ->
                canonObj(
                    "id" to canonStr(example.id),
                    "kind" to canonStr(example.kind),
                    "letterId" to canonStr(example.letterId),
                    "display" to canonStr(example.display),
                    "note" to canonStr(example.note),
                    "sourceId" to canonStr(example.sourceId),
                    "objectiveId" to canonStr(example.objectiveId),
                )
            }),
            "items" to canonArr(pack.items.map(::canonicalCheckItem)),
            "references" to canonArr(pack.references.map { reference ->
                canonObj(
                    "id" to canonStr(reference.id),
                    "title" to canonStr(reference.title),
                    "summary" to canonStr(reference.summary),
                    "detail" to canonStr(reference.detail),
                    "introducedInUnit" to canonNum(reference.introducedInUnit),
                    "sourceId" to canonStr(reference.sourceId),
                    "classification" to canonStr(reference.classification),
                    "searchTerms" to canonArr(reference.searchTerms.map(::canonStr)),
                )
            }),
            "units" to canonArr(pack.units.map(::canonicalUnit)),
        ).let { base ->
            appendBoundRecords(
                base,
                pack.provenanceRecords.sortedBy { it.itemId },
                pack.authoredEnglishRecords,
                includeAuthored = true,
            )
        }
    }

    private fun canonicalUnitInstructionalDocument(pack: V2CurriculumPack, unitNumber: Int): String {
        val unit = pack.units.firstOrNull { it.number == unitNumber }
        val exampleIds = if (unit == null) emptySet() else reachableExampleIds(unit)
        val itemIds = if (unit == null) emptySet() else reachableItemIds(unit)
        val referenceIds = if (unit == null) emptySet() else reachableReferenceIds(unit)
        val letterIds = if (unit == null) emptySet() else reachableLetterIds(pack, unit)
        val examples = pack.examples.filter { it.id in exampleIds }
        val items = pack.items.filter { it.id in itemIds }
        val references = pack.references.filter { it.id in referenceIds }
        val letters = pack.letters.filter { it.id in letterIds }
        val audio = pack.audioAssets.filter { it.unitNumber == unitNumber || it.letterId in letterIds }
        val handwriting = pack.handwritingAssets.filter { it.unitNumber == unitNumber || it.letterId in letterIds }
        val mappings = pack.ownedEditionMappings.filter { it.unitNumber == unitNumber }
        val sourceIds = (
            examples.map { it.sourceId } +
                items.map { it.sourceId } +
                references.map { it.sourceId } +
                letters.map { it.sourceId } +
                audio.map { it.sourceId } +
                handwriting.map { it.sourceId }
            ).toSet()
        val rightsIds = (
            pack.rights.filter { it.sourceId in sourceIds }.map { it.id } +
                audio.map { it.rightsId } +
                handwriting.map { it.rightsId }
            ).toSet()
        return canonObj(
            "digestSchema" to canonNum(CONTENT_REVIEW_DIGEST_SCHEMA),
            "scope" to canonStr("unit"),
            "unitNumber" to canonNum(unitNumber),
            "schemaVersion" to canonNum(pack.schemaVersion),
            "packId" to canonStr(pack.packId),
            "packVersion" to canonNum(pack.packVersion),
            "sources" to canonArr(pack.sources.filter { it.id in sourceIds }.map { source ->
                canonObj(
                    "id" to canonStr(source.id),
                    "title" to canonStr(source.title),
                    "license" to canonStr(source.license),
                    "attribution" to canonStr(source.attribution),
                    "modification" to canonStr(source.modification),
                )
            }),
            "rights" to canonArr(pack.rights.filter { it.id in rightsIds }.map { rights ->
                canonObj(
                    "id" to canonStr(rights.id),
                    "sourceId" to canonStr(rights.sourceId),
                    "license" to canonStr(rights.license),
                    "permitsAppUse" to canonBool(rights.permitsAppUse),
                )
            }),
            "audioAssets" to canonArr(audio.map { asset ->
                canonObj(
                    "id" to canonStr(asset.id),
                    "sourceId" to canonStr(asset.sourceId),
                    "rightsId" to canonStr(asset.rightsId),
                    "unitNumber" to canonNum(asset.unitNumber),
                    "letterId" to canonStr(asset.letterId),
                )
            }),
            "handwritingAssets" to canonArr(handwriting.map { asset ->
                canonObj(
                    "id" to canonStr(asset.id),
                    "sourceId" to canonStr(asset.sourceId),
                    "rightsId" to canonStr(asset.rightsId),
                    "unitNumber" to canonNum(asset.unitNumber),
                    "letterId" to canonStr(asset.letterId),
                )
            }),
            "ownedEditionMappings" to canonArr(mappings.map(::canonicalMapping)),
            "letters" to canonArr(letters.map { letter ->
                canonObj(
                    "id" to canonStr(letter.id),
                    "uppercase" to canonStr(letter.uppercase),
                    "lowercase" to canonStr(letter.lowercase),
                    "group" to canonStr(letter.group),
                    "englishNote" to canonStr(letter.englishNote),
                    "sourceId" to canonStr(letter.sourceId),
                    "audioAssetId" to canonStrOrNull(letter.audioAssetId),
                    "exampleWordId" to canonStrOrNull(letter.exampleWordId),
                    "latinLookalike" to canonStrOrNull(letter.latinLookalike),
                )
            }),
            "examples" to canonArr(examples.map { example ->
                canonObj(
                    "id" to canonStr(example.id),
                    "kind" to canonStr(example.kind),
                    "letterId" to canonStr(example.letterId),
                    "display" to canonStr(example.display),
                    "note" to canonStr(example.note),
                    "sourceId" to canonStr(example.sourceId),
                    "objectiveId" to canonStr(example.objectiveId),
                )
            }),
            "items" to canonArr(items.map(::canonicalCheckItem)),
            "references" to canonArr(references.map { reference ->
                canonObj(
                    "id" to canonStr(reference.id),
                    "title" to canonStr(reference.title),
                    "summary" to canonStr(reference.summary),
                    "detail" to canonStr(reference.detail),
                    "introducedInUnit" to canonNum(reference.introducedInUnit),
                    "sourceId" to canonStr(reference.sourceId),
                    "classification" to canonStr(reference.classification),
                    "searchTerms" to canonArr(reference.searchTerms.map(::canonStr)),
                )
            }),
            "units" to canonArr(listOfNotNull(unit).map(::canonicalUnit)),
        ).let { base ->
            appendBoundRecords(
                base,
                pack.provenanceRecords.filter { it.unitNumber == unitNumber }.sortedBy { it.itemId },
                pack.authoredEnglishRecords.filter { it.unitNumber == unitNumber },
                includeAuthored = pack.authoredEnglishRecords.any { it.unitNumber == unitNumber },
            )
        }
    }

    private fun appendBoundRecords(
        base: String,
        provenance: List<V2ProvenanceRecord>,
        authored: List<V2AuthoredEnglishRecord>,
        includeAuthored: Boolean,
    ): String {
        var document = base
        if (provenance.isNotEmpty()) {
            document = document.dropLast(1) +
                ",\"provenanceRecords\":" + canonArr(provenance.map(::canonicalProvenance)) + "}"
        }
        if (includeAuthored) {
            document = document.dropLast(1) +
                ",\"authoredEnglishRecords\":" + canonArr(authored.sortedBy { it.id }.map(::canonicalAuthored)) + "}"
        }
        return document
    }

    private fun canonicalProvenance(record: V2ProvenanceRecord): String {
        return canonObj(
            "itemId" to canonStr(record.itemId),
            "unitNumber" to canonNum(record.unitNumber),
            "objectiveId" to canonStr(record.objectiveId),
            "recordType" to canonStr(record.recordType),
            "sourceId" to canonStr(record.sourceId),
            "sourceUrl" to canonStr(record.sourceUrl),
            "sourceRevision" to canonStr(record.sourceRevision),
            "sourceRecordId" to canonStr(record.sourceRecordId),
            "originalSourceText" to canonStr(record.originalSourceText),
            "shippedText" to canonStr(record.shippedText),
            "transformation" to canonStr(record.transformation),
            "licenseId" to canonStr(record.licenseId),
            "licenseUrl" to canonStr(record.licenseUrl),
            "attribution" to canonStr(record.attribution),
            "shareAlikeTreatment" to canonStr(record.shareAlikeTreatment),
            "sourceHash" to canonStr(record.sourceHash),
            "contentHash" to canonStr(record.contentHash),
            "audioAssetId" to canonStrOrNull(record.audioAssetId),
        )
    }

    private fun canonicalAuthored(record: V2AuthoredEnglishRecord): String {
        return canonObj(
            "id" to canonStr(record.id),
            "unitNumber" to canonNum(record.unitNumber),
            "text" to canonStr(record.text),
            "classification" to canonStr(record.classification),
        )
    }

    private fun canonicalMapping(mapping: V2OwnedEditionMapping): String {
        return canonObj(
            "id" to canonStr(mapping.id),
            "unitNumber" to canonNum(mapping.unitNumber),
            "editionTitle" to canonStrOrNull(mapping.editionTitle),
            "author" to canonStrOrNull(mapping.author),
            "publisherImprint" to canonStrOrNull(mapping.publisherImprint),
            "proposedLessonNumber" to canonNumOrNull(mapping.proposedLessonNumber),
            "printedPageStart" to canonNumOrNull(mapping.printedPageStart),
            "printedPageEnd" to canonNumOrNull(mapping.printedPageEnd),
            "topicDescription" to canonStrOrNull(mapping.topicDescription),
            "observationSource" to canonStrOrNull(mapping.observationSource),
            "isbnObservation" to canonStr(mapping.isbnObservation),
            "isbn" to canonStrOrNull(mapping.isbn),
        )
    }

    private fun canonicalUnit(unit: V2Unit): String {
        return canonObj(
            "number" to canonNum(unit.number),
            "id" to canonStr(unit.id),
            "title" to canonStr(unit.title),
            "shortLabel" to canonStr(unit.shortLabel),
            "topicSummary" to canonStr(unit.topicSummary),
            "penguinLesson" to canonNum(unit.penguinLesson),
            "mappingNote" to canonStr(unit.mappingNote),
            "prerequisiteUnitNumbers" to canonArr(unit.prerequisiteUnitNumbers.map(::canonNum)),
            "estimatedMinutes" to canonNumOrNull(unit.estimatedMinutes),
            "objectives" to canonArr(unit.objectives.map { objective ->
                canonObj(
                    "id" to canonStr(objective.id),
                    "unitNumber" to canonNum(objective.unitNumber),
                    "title" to canonStr(objective.title),
                    "kind" to canonStr(objective.kind),
                    "instructionStepIds" to canonArr(objective.instructionStepIds.map(::canonStr)),
                    "exampleIds" to canonArr(objective.exampleIds.map(::canonStr)),
                    "practiceStepIds" to canonArr(objective.practiceStepIds.map(::canonStr)),
                    "checkItemIds" to canonArr(objective.checkItemIds.map(::canonStr)),
                    "remediationStepIds" to canonArr(objective.remediationStepIds.map(::canonStr)),
                )
            }),
            "sessions" to canonArr(unit.sessions.map { session ->
                canonObj(
                    "id" to canonStr(session.id),
                    "title" to canonStr(session.title),
                    "estimatedMinutes" to canonNum(session.estimatedMinutes),
                    "objectiveIds" to canonArr(session.objectiveIds.map(::canonStr)),
                    "steps" to canonArr(session.steps.map { step ->
                        canonObj(
                            "id" to canonStr(step.id),
                            "kind" to canonStr(step.kind),
                            "title" to canonStr(step.title),
                            "body" to canonStr(step.body),
                            "dominantAction" to canonStr(step.dominantAction),
                            "referenceIds" to canonArr(step.referenceIds.map(::canonStr)),
                            "itemIds" to canonArr(step.itemIds.map(::canonStr)),
                            "exampleIds" to canonArr(step.exampleIds.map(::canonStr)),
                            "unavailableReason" to canonStrOrNull(step.unavailableReason),
                            "bookDirection" to canonStrOrNull(step.bookDirection),
                            "companionDirection" to canonStrOrNull(step.companionDirection),
                            "remediationStepId" to canonStrOrNull(step.remediationStepId),
                        )
                    }),
                )
            }),
            "advancement" to (unit.advancement?.let { rule ->
                canonObj(
                    "requireInstruction" to canonBool(rule.requireInstruction),
                    "requireBookSelfAttestation" to canonBool(rule.requireBookSelfAttestation),
                    "requireAppChecks" to canonBool(rule.requireAppChecks),
                    "minimumDistinctChecks" to canonNum(rule.minimumDistinctChecks),
                    "requireDueReview" to canonBool(rule.requireDueReview),
                    "tutorOptional" to canonBool(rule.tutorOptional),
                )
            } ?: "null"),
        )
    }

    private fun canonObj(vararg fields: Pair<String, String>): String {
        return fields.joinToString(prefix = "{", postfix = "}") { (key, value) -> "${canonStr(key)}:$value" }
    }

    private fun canonArr(values: List<String>): String = values.joinToString(prefix = "[", postfix = "]")

    private fun canonicalCheckItem(item: V2CheckItem): String {
        val fields = mutableListOf(
            "id" to canonStr(item.id),
            "kind" to canonStr(item.kind),
            "language" to canonStr(item.language),
            "prompt" to canonStr(item.prompt),
            "promptLetter" to canonStrOrNull(item.promptLetter),
            "options" to canonArr(item.options.map(::canonStr)),
            "answer" to canonStr(item.answer),
            "feedback" to canonStr(item.feedback),
            "objectiveId" to canonStr(item.objectiveId),
            "sourceId" to canonStr(item.sourceId),
            "containsGeneratedRussian" to canonBool(item.containsGeneratedRussian),
        )
        if (item.distractorRationales.isNotEmpty()) {
            fields += "distractorRationales" to canonArr(item.distractorRationales.map(::canonStr))
        }
        item.assessment?.let { assessment -> fields += "assessment" to canonicalAssessment(assessment) }
        return canonObj(*fields.toTypedArray())
    }

    private fun canonicalAssessment(assessment: V2Assessment): String = canonObj(
        "testedFeature" to canonStr(assessment.testedFeature),
        "evidenceMode" to canonStr(assessment.evidenceMode),
        "contrastSetId" to canonStr(assessment.contrastSetId),
        "cueLanguage" to canonStr(assessment.cueLanguage),
        "independent" to canonBool(assessment.independent),
        "answerSourceIds" to canonArr(assessment.answerSourceIds.map(::canonStr)),
        "distractorSourceIds" to canonArr(assessment.distractorSourceIds.map(::canonStr)),
        "distractorDifference" to canonArr(assessment.distractorDifference.map(::canonStr)),
        "whyAnswerDemonstratesObjective" to canonStr(assessment.whyAnswerDemonstratesObjective),
        "contrastMembers" to canonArr(assessment.contrastMembers.map(::canonStr)),
    )

    private fun canonNum(value: Int): String = value.toString()

    private fun canonNumOrNull(value: Int?): String = value?.toString() ?: "null"

    private fun canonBool(value: Boolean): String = if (value) "true" else "false"

    private fun canonStrOrNull(value: String?): String = if (value == null) "null" else canonStr(value)

    private fun canonStr(value: String): String {
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

    private fun validateReview(pack: V2CurriculumPack, errors: MutableList<String>) {
        val review = pack.linguisticReview
        if (review.status !in CLOSED_REVIEW_STATES) {
            errors += "Unrecognized linguistic review status ${review.status}"
            return
        }
        if (review.status != "APPROVED") return
        val missing = mutableListOf<String>()
        if (review.reviewer.isNullOrBlank()) missing += "reviewer identity"
        if (review.reviewerQualification.isNullOrBlank()) missing += "qualification attestation"
        if (review.packVersion != pack.packVersion) missing += "matching pack version"
        if (review.reviewedContentDigest.isNullOrBlank()) missing += "reviewed content digest"
        if (review.curriculumMappingId.isNullOrBlank()) missing += "curriculum mapping"
        if (missing.isNotEmpty()) {
            errors += "APPROVED review is missing ${missing.joinToString(", ")}"
            return
        }
        val expected = contentReviewDigest(pack)
        if (review.reviewedContentDigest != expected) {
            errors += "APPROVED review digest does not match the shipped content-review digest"
        }
    }

    private fun validateUnitReviews(pack: V2CurriculumPack, errors: MutableList<String>) {
        val seen = mutableSetOf<Int>()
        pack.unitReviews.forEach { review ->
            if (review.unitNumber !in 1..30) {
                errors += "Unit review ${review.id} is outside Units 1-30"
            }
            if (!seen.add(review.unitNumber)) {
                errors += "Unit ${review.unitNumber} has more than one review record"
            }
            if (review.status !in CLOSED_REVIEW_STATES) {
                errors += "Unrecognized unit review status ${review.status} for unit ${review.unitNumber}"
                return@forEach
            }
            if (review.status != "APPROVED") return@forEach
            val missing = mutableListOf<String>()
            if (review.reviewer.isNullOrBlank()) missing += "reviewer identity"
            if (review.reviewerQualification.isNullOrBlank()) missing += "qualification attestation"
            if (review.packVersion != pack.packVersion) missing += "matching pack version"
            if (review.reviewedContentDigest.isNullOrBlank()) missing += "reviewed content digest"
            if (review.curriculumMappingId.isNullOrBlank()) missing += "curriculum mapping"
            if (missing.isNotEmpty()) {
                errors += "Unit ${review.unitNumber} APPROVED review is missing ${missing.joinToString(", ")}"
                return@forEach
            }
            if (review.reviewedContentDigest != unitContentReviewDigest(pack, review.unitNumber)) {
                errors += "Unit ${review.unitNumber} APPROVED review digest does not match the unit content-review digest"
            }
        }
    }

    private fun unitReview(pack: V2CurriculumPack, unitNumber: Int): V2UnitReviewRecord? {
        val matches = pack.unitReviews.filter { it.unitNumber == unitNumber }
        return if (matches.size == 1) matches.first() else null
    }

    private fun validateSourcesAndRights(pack: V2CurriculumPack, errors: MutableList<String>) {
        val sourceIds = pack.sources.map { it.id }.toSet()
        if (sourceIds.size != pack.sources.size) errors += "Source ids must be unique"
        pack.sources.forEach { source ->
            if (source.license.isBlank()) errors += "Source ${source.id} has no license"
            if (pack.rights.none { it.sourceId == source.id }) {
                errors += "Source ${source.id} has no rights record"
            }
        }
        pack.rights.forEach { rights ->
            if (rights.sourceId !in sourceIds) errors += "Rights ${rights.id} references missing source ${rights.sourceId}"
            if (rights.license.isBlank()) errors += "Rights ${rights.id} has no license"
            if (!rights.permitsAppUse) errors += "Rights ${rights.id} does not permit app use"
        }
    }

    private fun validateInstalled(pack: V2CurriculumPack, unit: V2Unit, errors: MutableList<String>) {
        if (unit.number !in 1..2) {
            errors += "Only Units 1-2 may be installed in this pack"
        }
        if (unitReview(pack, unit.number)?.status != "APPROVED") {
            errors += "Installed unit ${unit.number} requires an APPROVED qualified-review record"
        }
        if (unit.mappingStatus != "APPROVED") {
            errors += "Installed unit ${unit.number} mapping must be APPROVED"
        }
        if (unit.readiness != "INSTALLABLE") {
            errors += "Installed unit ${unit.number} readiness must be INSTALLABLE"
        }
        if (unit.sessions.isEmpty()) errors += "Installed unit ${unit.number} has no sessions"
        if (unit.objectives.isEmpty()) errors += "Installed unit ${unit.number} has no objectives"
        if (unit.sessions.any { session -> session.steps.any { it.kind == "UNAVAILABLE" || !it.unavailableReason.isNullOrBlank() } }) {
            errors += "Installed unit ${unit.number} still has unavailable required experiences"
        }
        if (!pronunciationAudioResolves(pack, unit)) {
            errors += "Installed unit ${unit.number} requires a mapped pronunciation audio record"
        }
        if (unit.number == 2 && !handwritingAssetResolves(pack, unit)) {
            errors += "Installed Unit 2 requires a verified handwriting asset record"
        }
        if (!ownedEditionMappingResolves(pack, unit)) {
            errors += "Installed unit ${unit.number} mapping record does not resolve"
        }
        validateInstalledReviewRecords(pack, unit, errors)
        validateOpenableBody(pack, unit, errors, requireCompleteRecognition = true)
        if (unit.objectives.filter { it.kind == "RECOGNITION" }.any { it.exampleIds.size < 2 }) {
            errors += "Installed unit ${unit.number} has a recognition objective with fewer than two examples"
        }
    }

    private fun validateReadiness(pack: V2CurriculumPack, unit: V2Unit, errors: MutableList<String>) {
        if (unit.readiness.isBlank()) {
            errors += "Unit ${unit.number} is missing a readiness"
            return
        }
        if (unit.readiness !in CLOSED_READINESS) {
            errors += "Unit ${unit.number} has unknown readiness ${unit.readiness}"
            return
        }
        if (unit.readiness != "NOT_PREPARED") {
            validateSubstantiveContent(pack, unit, errors)
        }
        when (unit.readiness) {
            "NOT_PREPARED" -> {
                if (unit.installState == "INSTALLED") {
                    errors += "Unit ${unit.number} cannot be INSTALLED while readiness is NOT_PREPARED"
                }
            }
            "CONTENT_COMPLETE_REVIEW_PENDING" -> {
                if (unit.installState == "INSTALLED") {
                    errors += "Unit ${unit.number} cannot be INSTALLED while review is pending"
                }
                if (unitReview(pack, unit.number)?.status == "APPROVED") {
                    errors += "Unit ${unit.number} readiness is stale because review is APPROVED"
                }
                if (unit.mappingStatus == "APPROVED") {
                    errors += "Unit ${unit.number} readiness cannot stay review-pending after mapping approval"
                }
                if (unit.sessions.any { session ->
                        session.steps.any { it.kind == "UNAVAILABLE" || !it.unavailableReason.isNullOrBlank() }
                    }
                ) {
                    errors += "Unit ${unit.number} is not content-complete while a required experience is unavailable"
                }
                if (unit.objectives.filter { it.kind == "RECOGNITION" }.any { it.exampleIds.size < 2 }) {
                    errors += "Unit ${unit.number} content-complete claim has a recognition objective with fewer than two examples"
                }
            }
            "REVIEW_APPROVED" -> {
                if (unitReview(pack, unit.number)?.status != "APPROVED") {
                    errors += "Unit ${unit.number} readiness claims review approval that is not recorded"
                }
            }
            "MAPPING_APPROVED" -> {
                if (unit.mappingStatus != "APPROVED") {
                    errors += "Unit ${unit.number} readiness claims mapping approval that is not recorded"
                }
            }
            "INSTALLABLE" -> {
                if (unitReview(pack, unit.number)?.status != "APPROVED" || unit.mappingStatus != "APPROVED") {
                    errors += "Unit ${unit.number} INSTALLABLE readiness requires approved review and mapping"
                }
            }
        }
    }

    private fun validateSubstantiveContent(pack: V2CurriculumPack, unit: V2Unit, errors: MutableList<String>) {
        if (unit.sessions.isEmpty() || unit.objectives.isEmpty()) {
            errors += "Unit ${unit.number} is not content-complete: empty sessions or objectives do not count"
        }
        val recognition = unit.objectives.filter { it.kind == "RECOGNITION" }
        if (recognition.isEmpty()) {
            errors += "Unit ${unit.number} is not content-complete: book direction does not replace instruction, examples, guided practice, independent checks, feedback, and remediation"
        }
        if (unit.sessions.flatMap { it.steps }.none { it.kind == "TEACH" }) {
            errors += "Unit ${unit.number} is not content-complete: it has no teach step"
        }
        recognition.forEach { objective ->
            if (objective.instructionStepIds.isEmpty() ||
                objective.exampleIds.size < 2 ||
                objective.practiceStepIds.isEmpty() ||
                objective.checkItemIds.isEmpty() ||
                objective.remediationStepIds.isEmpty()
            ) {
                errors += "Unit ${unit.number} objective ${objective.id} is missing instruction, examples, guided practice, independent checks, or remediation"
            }
            objective.checkItemIds.forEach { itemId ->
                val item = pack.item(itemId)
                if (item == null || item.feedback.isBlank()) {
                    errors += "Unit ${unit.number} check $itemId has no factual feedback"
                }
            }
        }
    }

    private fun validateInstalledReviewRecords(
        pack: V2CurriculumPack,
        unit: V2Unit,
        errors: MutableList<String>,
    ) {
        val review = unitReview(pack, unit.number)
        if (review == null ||
            review.status != "APPROVED" ||
            review.reviewer.isNullOrBlank() ||
            review.reviewerQualification.isNullOrBlank() ||
            review.packVersion != pack.packVersion ||
            review.reviewedContentDigest != unitContentReviewDigest(pack, unit.number) ||
            review.curriculumMappingId.isNullOrBlank()
        ) {
            errors += "Installed unit ${unit.number} approval must bind to the shipped pack version and unit content-review digest"
        }
        val steps = unit.sessions.flatMap { it.steps }
        val itemIds = (steps.flatMap { it.itemIds } + unit.objectives.flatMap { it.checkItemIds }).toSet()
        val exampleIds = (steps.flatMap { it.exampleIds } + unit.objectives.flatMap { it.exampleIds }).toSet()
        val referenceIds = steps.flatMap { it.referenceIds }.toSet()
        val letterIds = exampleIds.mapNotNull { pack.example(it)?.letterId?.takeIf { id -> id.isNotBlank() } }.toSet()
        itemIds.forEach { id ->
            val item = pack.item(id) ?: return@forEach
            requireInstalledReview(item.reviewStatus, allowNotRequired = false, "Check $id", errors)
        }
        exampleIds.forEach { id ->
            val example = pack.example(id) ?: return@forEach
            requireInstalledReview(example.reviewStatus, allowNotRequired = false, "Example $id", errors)
        }
        letterIds.forEach { id ->
            val letter = pack.letter(id) ?: return@forEach
            requireInstalledReview(letter.reviewStatus, allowNotRequired = false, "Letter $id", errors)
        }
        referenceIds.forEach { id ->
            val reference = pack.reference(id) ?: return@forEach
            if (!validateReferenceClassification(reference, errors)) return@forEach
            requireInstalledReview(
                reference.reviewStatus,
                allowNotRequired = reference.classification in NON_LINGUISTIC_REFERENCE_CLASSIFICATIONS,
                "Reference $id",
                errors,
            )
        }
    }

    private fun validateReferenceClassification(entry: V2ReferenceEntry, errors: MutableList<String>): Boolean {
        if (entry.classification.isBlank()) {
            errors += "Reference ${entry.id} is missing a classification"
            return false
        }
        if (entry.classification !in CLOSED_REFERENCE_CLASSIFICATIONS) {
            errors += "Reference ${entry.id} has unknown classification ${entry.classification}"
            return false
        }
        if (entry.reviewStatus == "NOT_REQUIRED" &&
            entry.classification !in NON_LINGUISTIC_REFERENCE_CLASSIFICATIONS
        ) {
            errors += "Reference ${entry.id} is linguistically relevant and cannot be NOT_REQUIRED"
            return false
        }
        return true
    }

    private fun requireInstalledReview(
        status: String,
        allowNotRequired: Boolean,
        label: String,
        errors: MutableList<String>,
    ) {
        when (status) {
            "APPROVED" -> Unit
            "NOT_REQUIRED" -> {
                if (!allowNotRequired) {
                    errors += "$label is linguistically relevant and cannot be NOT_REQUIRED"
                }
            }
            "PENDING_QUALIFIED_REVIEW", "REJECTED" -> {
                errors += "$label review status $status is not approved for an installed unit"
            }
            else -> errors += "$label has unrecognized review status $status"
        }
    }

    private fun validateDevelopment(pack: V2CurriculumPack, unit: V2Unit, errors: MutableList<String>) {
        if (unit.number !in 1..10) {
            errors += "Only Units 1-10 may be development content in this pack"
        }
        if (unit.mappingStatus != "PENDING_CURRICULUM_REVIEWER") {
            errors += "Unit ${unit.number} mapping must remain pending until a reviewer signs the owned edition"
        }
        if (unit.sessions.isEmpty()) errors += "Development unit ${unit.number} has no sessions"
        if (unit.objectives.isEmpty()) errors += "Development unit ${unit.number} has no objectives"
        validateOpenableBody(pack, unit, errors, requireCompleteRecognition = true)
        if (unit.number in 6..10) validateGovernedChecks(pack, unit, errors)
    }

    private val ENGLISH_CUE = Regex("(?i)which admitted\\b.{0,80}\\brecorded for\\b")
    private val NUMBER_CUE = Regex("[0-9][0-9,~/\\.\\-]*")
    private val EVIDENCE_TOKENS = mapOf(
        "SAME_LEMMA_FORM" to "same word",
        "RULE_CLASSIFICATION" to "shown line",
        "FEATURE_LABEL" to "shown line",
        "NUMBER_FORM" to "shown number",
        "RULE_APPLICATION" to "shown rule",
        "FEATURE_IN_LINE" to "shown line",
        "COUNTED_PATTERN" to "pattern",
        "AGREEMENT_PAIR" to "shown word",
    )
    private val UNRELATED_DIFFERENCE = listOf(
        "different example",
        "different object example",
        "different possession",
        "different word",
        "belongs to a different word",
        "other example",
        "other row in the frozen table",
        "another example",
        "different line",
        "other person row",
        "different person",
        "unrelated",
    )

    internal fun isDirectEnglishMatch(item: V2CheckItem): Boolean {
        val shown = item.promptLetter.orEmpty()
        val shownRussian = shown.any { it.code in 0x0400..0x04FF }
        val answerRussian = item.answer.any { it.code in 0x0400..0x04FF }
        if (ENGLISH_CUE.containsMatchIn(item.prompt) && !shownRussian && answerRussian) return true
        if (item.assessment?.cueLanguage == "en") return true
        val cue = item.assessment?.cueLanguage
        return item.kind in GRAMMAR_ITEM_KINDS && !shownRussian && answerRussian && cue != "rule" && cue != "number"
    }

    private fun isUnrelatedDifference(text: String): Boolean =
        UNRELATED_DIFFERENCE.any { phrase -> text.contains(phrase, ignoreCase = true) }

    private fun validateGovernedChecks(pack: V2CurriculumPack, unit: V2Unit, errors: MutableList<String>) {
        val generic = "Return to the explanation. Compare the shown form with the admitted record."
        unit.objectives.filter { it.kind == "RECOGNITION" }.forEach { objective ->
            val checks = objective.checkItemIds.mapNotNull { pack.item(it) }
            if (checks.none { it.kind in GRAMMAR_ITEM_KINDS }) {
                errors += "Unit ${unit.number} objective ${objective.id} has no check of its grammatical distinction"
            }
            if (checks.isNotEmpty() && checks.all { it.kind == "SOURCE_MEANING" }) {
                errors += "SOURCE_MEANING alone cannot satisfy ${objective.id}"
            }
            if (checks.none { featureValid(pack, it) }) {
                errors += "Unit ${unit.number} objective ${objective.id} has no independent feature-valid check"
            }
            if (checks.any { isDirectEnglishMatch(it) }) {
                errors += "Unit ${unit.number} objective ${objective.id} independent check can be answered by direct English-to-Russian matching"
            }
            val remediation = objective.remediationStepIds.mapNotNull { pack.step(it)?.second }
            if (remediation.isEmpty() || remediation.any { it.body == generic || generic in it.body && it.body.removePrefix(generic).isBlank() }) {
                errors += "Unit ${unit.number} objective ${objective.id} remediation does not target the failed concept"
            }
            val shipped = pack.provenanceRecords.filter { it.objectiveId == objective.id }.map { it.shippedText }
            if (remediation.any { step -> shipped.none { span -> span.length >= 8 && span in step.body } }) {
                errors += "Unit ${unit.number} objective ${objective.id} remediation is not bound to an admitted source span"
            }
        }
    }

    internal fun featureValid(pack: V2CurriculumPack, item: V2CheckItem): Boolean {
        val assessment = item.assessment ?: return false
        return assessment.independent && structureValid(pack, item).isEmpty()
    }

    private fun structureValid(pack: V2CurriculumPack, item: V2CheckItem): List<String> {
        val errors = mutableListOf<String>()
        val assessment = item.assessment
        if (item.kind !in GRAMMAR_ITEM_KINDS) {
            errors += "Item ${item.id} kind does not test a grammatical distinction"
        }
        if (assessment == null) {
            errors += "Item ${item.id} has no assessment target"
            return errors
        }
        if (assessment.testedFeature.isBlank()) errors += "Item ${item.id} has no tested feature"
        if (assessment.evidenceMode !in EVIDENCE_TOKENS) {
            errors += "Item ${item.id} evidence mode ${assessment.evidenceMode} is not governed"
        }
        if (assessment.cueLanguage !in setOf("ru", "rule", "number")) {
            errors += "Item ${item.id} cue language ${assessment.cueLanguage} is not governed"
        }
        if (assessment.whyAnswerDemonstratesObjective.isBlank() ||
            assessment.whyAnswerDemonstratesObjective.startsWith("The admitted answer")
        ) {
            errors += "Item ${item.id} answer rationale does not explain the governed distinction"
        }
        val token = EVIDENCE_TOKENS[assessment.evidenceMode]
        if (token != null && !assessment.whyAnswerDemonstratesObjective.contains(token, ignoreCase = true)) {
            errors += "Item ${item.id} answer rationale does not explain the governed distinction"
        }
        if (assessment.contrastMembers.toSet() != item.options.toSet()) {
            errors += "Item ${item.id} options are outside the governed contrast"
        }
        if (assessment.distractorDifference.size != item.options.size ||
            assessment.distractorSourceIds.size != item.options.size
        ) {
            errors += "Item ${item.id} distractor metadata does not match the options"
        } else {
            item.options.forEachIndexed { index, option ->
                val difference = assessment.distractorDifference[index]
                val sourceItemId = assessment.distractorSourceIds[index]
                if (option == item.answer) {
                    if (difference.isNotEmpty() || sourceItemId.isNotEmpty()) {
                        errors += "Item ${item.id} records distractor metadata for the answer"
                    }
                } else {
                    if (difference.isBlank() || isUnrelatedDifference(difference)) {
                        errors += "Item ${item.id} distractor is unrelated to the governed feature"
                    } else if (token != null && !difference.contains(token, ignoreCase = true)) {
                        errors += "Item ${item.id} distractor is unrelated to the governed feature"
                    }
                    val provenance = pack.provenanceRecords.firstOrNull { it.itemId == sourceItemId }
                    if (provenance == null || provenance.shippedText != option || provenance.objectiveId != item.objectiveId) {
                        errors += "Item ${item.id} distractor has no contrast provenance"
                    }
                }
            }
        }
        val answerProvenance = assessment.answerSourceIds.mapNotNull { id ->
            pack.provenanceRecords.firstOrNull { it.itemId == id }
        }
        if (answerProvenance.none { it.shippedText == item.answer && it.objectiveId == item.objectiveId }) {
            errors += "Item ${item.id} answer has no contrast provenance"
        }
        if (isDirectEnglishMatch(item)) {
            errors += "Item ${item.id} can be answered by direct English-to-Russian matching"
        }
        val shown = item.promptLetter.orEmpty()
        val shownRussian = shown.any { it.code in 0x0400..0x04FF }
        when (assessment.cueLanguage) {
            "ru" -> if (!shownRussian) errors += "Item ${item.id} Russian cue has no Russian shown form"
            "number" -> if (!NUMBER_CUE.matches(shown)) errors += "Item ${item.id} number cue is not a shown number"
            "rule" -> if (shownRussian || shown.isBlank()) errors += "Item ${item.id} rule cue is not an admitted rule label"
        }
        when (assessment.evidenceMode) {
            "SAME_LEMMA_FORM", "FEATURE_IN_LINE", "COUNTED_PATTERN", "AGREEMENT_PAIR" -> {
                if (!shownRussian || item.answer.none { it.code in 0x0400..0x04FF }) {
                    errors += "Item ${item.id} does not contrast admitted Russian forms"
                }
            }
            "RULE_CLASSIFICATION", "FEATURE_LABEL" -> {
                if (!shownRussian) errors += "Item ${item.id} does not classify a Russian line"
            }
            "NUMBER_FORM" -> {
                if (item.answer.none { it.code in 0x0400..0x04FF }) {
                    errors += "Item ${item.id} number check has no admitted form"
                }
            }
            "RULE_APPLICATION" -> {
                if (item.answer.none { it.code in 0x0400..0x04FF }) {
                    errors += "Item ${item.id} rule application has no admitted form"
                }
            }
        }
        return errors
    }

    private fun validateOpenableBody(
        pack: V2CurriculumPack,
        unit: V2Unit,
        errors: MutableList<String>,
        requireCompleteRecognition: Boolean,
    ) {
        val sessionIds = mutableSetOf<String>()
        unit.sessions.forEach { session ->
            if (!sessionIds.add(session.id)) errors += "Session ${session.id} is duplicated"
            if (session.steps.isEmpty()) errors += "Session ${session.id} has no steps"
            session.objectiveIds.forEach { objectiveId ->
                if (unit.objectives.none { it.id == objectiveId }) {
                    errors += "Session ${session.id} objective $objectiveId is not on unit ${unit.number}"
                }
            }
            session.steps.forEach { step -> validateStep(pack, step, errors) }
        }
        unit.objectives.forEach { objective ->
            validateObjectiveChain(pack, unit, objective, errors, requireCompleteRecognition)
        }
    }

    private fun validateObjectiveChain(
        pack: V2CurriculumPack,
        unit: V2Unit,
        objective: V2Objective,
        errors: MutableList<String>,
        requireCompleteRecognition: Boolean,
    ) {
        if (objective.unitNumber != unit.number) {
            errors += "Objective ${objective.id} is not mapped to unit ${unit.number}"
        }
        objective.instructionStepIds.forEach { stepId ->
            if (pack.step(stepId) == null) errors += "Objective ${objective.id} instruction $stepId is missing"
        }
        objective.exampleIds.forEach { exampleId ->
            if (pack.example(exampleId) == null) errors += "Objective ${objective.id} example $exampleId is missing"
        }
        objective.practiceStepIds.forEach { stepId ->
            if (pack.step(stepId) == null) errors += "Objective ${objective.id} practice $stepId is missing"
        }
        objective.checkItemIds.forEach { itemId ->
            val item = pack.item(itemId)
            if (item == null) {
                errors += "Objective ${objective.id} check $itemId is missing"
            } else if (item.objectiveId != objective.id) {
                errors += "Check $itemId is not placed on objective ${objective.id}"
            }
        }
        objective.remediationStepIds.forEach { stepId ->
            if (pack.step(stepId) == null) errors += "Objective ${objective.id} remediation $stepId is missing"
        }
        when (objective.kind) {
            "RECOGNITION" -> {
                if (!requireCompleteRecognition) return
                if (objective.instructionStepIds.isEmpty()) {
                    errors += "Objective ${objective.id} has no instruction"
                }
                if (objective.exampleIds.isEmpty()) {
                    errors += "Objective ${objective.id} has no examples"
                }
                if (objective.practiceStepIds.isEmpty() && objective.checkItemIds.isEmpty()) {
                    errors += "Objective ${objective.id} has no guided practice or checks"
                }
                if (objective.checkItemIds.isEmpty()) {
                    errors += "Objective ${objective.id} has no independent checks"
                }
                if (objective.remediationStepIds.isEmpty()) {
                    errors += "Objective ${objective.id} has no remediation"
                }
            }
            "SELF_ATTESTATION" -> {
                val bookSteps = unit.sessions.flatMap { it.steps }.filter { it.kind == "BOOK" }
                if (bookSteps.isEmpty() && objective.instructionStepIds.isEmpty()) {
                    errors += "Objective ${objective.id} has no book attestation path"
                }
            }
            "GUIDANCE" -> {
                if (objective.checkItemIds.isNotEmpty()) {
                    errors += "Guidance objective ${objective.id} must not carry advancement checks"
                }
            }
            else -> errors += "Objective ${objective.id} has unrecognized kind ${objective.kind}"
        }
    }

    private fun validateStep(pack: V2CurriculumPack, step: V2Step, errors: MutableList<String>) {
        step.itemIds.forEach { itemId ->
            if (pack.item(itemId) == null) errors += "Step ${step.id} references missing item $itemId"
        }
        step.referenceIds.forEach { refId ->
            if (pack.reference(refId) == null) errors += "Step ${step.id} references missing entry $refId"
        }
        step.exampleIds.forEach { exampleId ->
            if (pack.example(exampleId) == null) errors += "Step ${step.id} references missing example $exampleId"
        }
        step.remediationStepId?.let { rem ->
            if (pack.step(rem) == null) errors += "Step ${step.id} remediation $rem is missing"
        }
        if (step.itemIds.isNotEmpty()) {
            step.itemIds.forEach { itemId ->
                val item = pack.item(itemId) ?: return@forEach
                if (item.feedback.isBlank()) errors += "Item ${item.id} has no feedback"
            }
            if (step.kind in setOf("CHECK", "GUIDED") && step.remediationStepId == null) {
                errors += "Step ${step.id} has checks without remediation"
            }
        }
    }

    private fun validateUninstalled(unit: V2Unit, errors: MutableList<String>) {
        if (unit.sessions.isNotEmpty()) errors += "Unit ${unit.number} is not installed but has sessions"
        if (unit.objectives.isNotEmpty()) errors += "Unit ${unit.number} is not installed but has objectives"
        if (unit.advancement != null) errors += "Unit ${unit.number} is not installed but has advancement"
    }

    private fun validateReachableSessions(pack: V2CurriculumPack, errors: MutableList<String>) {
        pack.units.filter { it.openable() }.forEach { unit ->
            unit.sessions.forEach { session ->
                if (session.steps.isEmpty()) {
                    errors += "Session ${session.id} is not reachable because it has no steps"
                }
            }
        }
    }

    private fun validateItem(pack: V2CurriculumPack, item: V2CheckItem, errors: MutableList<String>) {
        if (item.containsGeneratedRussian) errors += "Item ${item.id} marks generated Russian"
        if (item.language != "en") errors += "Item ${item.id} is not independently authored English"
        if (item.options.isEmpty() || item.answer !in item.options) {
            errors += "Item ${item.id} has no usable answer key"
        }
        if (item.feedback.isBlank()) errors += "Item ${item.id} has no feedback"
        if (pack.units.flatMap { it.objectives }.none { it.id == item.objectiveId }) {
            errors += "Item ${item.id} objective is missing"
        }
        if (pack.sources.none { it.id == item.sourceId }) {
            errors += "Item ${item.id} source ${item.sourceId} is missing"
        }
        if (item.reviewStatus !in CLOSED_REVIEW_STATES) {
            errors += "Item ${item.id} has unrecognized review status ${item.reviewStatus}"
        }
        if (item.kind !in CLOSED_ITEM_KINDS) {
            errors += "Item ${item.id} kind ${item.kind} is not a Russian-learning check"
        }
        if (item.options.size != item.options.toSet().size) {
            errors += "Item ${item.id} options are not distinct"
        }
        if (item.options.count { it == item.answer } != 1) {
            errors += "Item ${item.id} repeats the answer in the options"
        }
        if (item.kind in GRAMMAR_ITEM_KINDS) {
            if (item.distractorRationales.size != item.options.size) {
                errors += "Item ${item.id} distractor rationales do not match the options"
            } else {
                item.options.forEachIndexed { index, option ->
                    val rationale = item.distractorRationales[index]
                    if (option == item.answer) {
                        if (rationale.isNotEmpty()) {
                            errors += "Item ${item.id} records a distractor rationale for the answer"
                        }
                    } else if (rationale.isBlank()) {
                        errors += "Item ${item.id} distractor has no governed contrast rationale"
                    }
                }
            }
            if (item.objectiveId.startsWith("obj-u") && item.objectiveId.drop(5).substringBefore("-").toIntOrNull() in 6..10) {
                errors += structureValid(pack, item)
            }
        }
        if (item.isGovernancePrompt()) {
            errors += "Item ${item.id} is a governance or policy prompt, not a Russian-learning check"
        }
        if (item.promptLetter.isNullOrBlank()) {
            errors += "Item ${item.id} has no prompt letter"
        }
    }

    private fun validateExample(pack: V2CurriculumPack, example: V2Example, errors: MutableList<String>) {
        if (example.letterId.isBlank()) {
            if (example.kind != "LEXICAL") {
                errors += "Example ${example.id} letter ${example.letterId} is missing"
            }
        } else if (pack.letter(example.letterId) == null) {
            errors += "Example ${example.id} letter ${example.letterId} is missing"
        }
        if (pack.sources.none { it.id == example.sourceId }) {
            errors += "Example ${example.id} source ${example.sourceId} is missing"
        }
        if (example.reviewStatus !in CLOSED_REVIEW_STATES) {
            errors += "Example ${example.id} has unrecognized review status ${example.reviewStatus}"
        }
        if (pack.units.flatMap { it.objectives }.none { it.id == example.objectiveId }) {
            errors += "Example ${example.id} objective is missing"
        }
        val unitNumber = pack.units.firstOrNull { unit -> unit.objectives.any { it.id == example.objectiveId } }?.number
        if (unitNumber in 6..10 && example.display.none { it.code in 0x0400..0x04FF }) {
            errors += "Example ${example.id} stores English instructional prose as a Russian example"
        }
    }

    private fun validateLetter(pack: V2CurriculumPack, letter: V2Letter, errors: MutableList<String>) {
        if (letter.uppercase.length != 1 || letter.lowercase.length != 1) {
            errors += "Letter ${letter.id} is not a single printed pair"
        }
        if (letter.exampleWordId != null) errors += "Letter ${letter.id} has an example word"
        val audioId = letter.audioAssetId
        if (audioId != null && !letterAudioResolves(pack, letter)) {
            errors += "Letter ${letter.id} claims audio that is not intake-reviewed"
        }
        if (pack.sources.none { it.id == letter.sourceId }) {
            errors += "Letter ${letter.id} source ${letter.sourceId} is missing"
        }
        if (letter.reviewStatus !in CLOSED_REVIEW_STATES) {
            errors += "Letter ${letter.id} has unrecognized review status ${letter.reviewStatus}"
        }
    }

    private fun validateAssetRecords(pack: V2CurriculumPack, errors: MutableList<String>) {
        if (pack.audioAssets.map { it.id }.distinct().size != pack.audioAssets.size) {
            errors += "Audio asset ids must be unique"
        }
        if (pack.handwritingAssets.map { it.id }.distinct().size != pack.handwritingAssets.size) {
            errors += "Handwriting asset ids must be unique"
        }
        if (pack.ownedEditionMappings.map { it.id }.distinct().size != pack.ownedEditionMappings.size) {
            errors += "Owned-edition mapping ids must be unique"
        }
        pack.audioAssets.forEach { asset ->
            if (!sourceAndRightsResolve(pack, asset.sourceId, asset.rightsId)) {
                errors += "Audio ${asset.id} source or rights record does not resolve"
            }
            if (pack.letters.none { it.id == asset.letterId && it.audioAssetId == asset.id }) {
                errors += "Audio ${asset.id} is not related to letter ${asset.letterId}"
            }
            if (asset.unitNumber !in 1..30) errors += "Audio ${asset.id} is not related to a unit"
        }
        pack.handwritingAssets.forEach { asset ->
            if (!sourceAndRightsResolve(pack, asset.sourceId, asset.rightsId)) {
                errors += "Handwriting ${asset.id} source or rights record does not resolve"
            }
            if (pack.letters.none { it.id == asset.letterId }) {
                errors += "Handwriting ${asset.id} is not related to letter ${asset.letterId}"
            }
            if (asset.unitNumber !in 1..30) errors += "Handwriting ${asset.id} is not related to a unit"
        }
        pack.ownedEditionMappings.forEach { mapping ->
            errors += mappingRecordErrors(pack, mapping)
        }
    }

    private fun mappingRecordErrors(pack: V2CurriculumPack, mapping: V2OwnedEditionMapping): List<String> {
        val errors = mutableListOf<String>()
        val unit = pack.units.firstOrNull { it.number == mapping.unitNumber }
        if (mapping.unitNumber !in 1..30 || unit == null) {
            errors += "Mapping ${mapping.id} is not related to a unit"
            errors += "Mapping ${mapping.id} is for the wrong unit or lesson"
        }
        if (mapping.proposedLessonNumber != null && mapping.proposedLessonNumber != mapping.unitNumber) {
            errors += "Mapping ${mapping.id} is for the wrong unit or lesson"
        }
        if (unit != null && mapping.proposedLessonNumber != null && mapping.proposedLessonNumber != unit.penguinLesson) {
            errors += "Mapping ${mapping.id} is for the wrong unit or lesson"
        }
        if (mapping.isbnObservation !in ISBN_OBSERVATIONS) {
            errors += "Mapping ${mapping.id} has an unrecognized ISBN observation"
        }
        if (mapping.isbnObservation == "UNRESOLVED" && !mapping.isbn.isNullOrBlank()) {
            errors += "Mapping ${mapping.id} records an invented ISBN"
        }
        if (mapping.isbnObservation == "OBSERVED" && mapping.isbn.isNullOrBlank()) {
            errors += "Mapping ${mapping.id} records an invented ISBN"
        }
        if (mapping.status !in MAPPING_STATUSES) {
            errors += "Mapping ${mapping.id} has unrecognized status ${mapping.status}"
        }
        val identityMissing = mapping.editionTitle.isNullOrBlank() ||
            mapping.author.isNullOrBlank() ||
            mapping.publisherImprint.isNullOrBlank()
        if (identityMissing) {
            errors += "Mapping ${mapping.id} is missing edition identity"
        }
        if (mapping.status == "APPROVED") {
            if (!identityMissing && (mapping.isbnObservation != "OBSERVED" || mapping.isbn.isNullOrBlank())) {
                errors += "Mapping ${mapping.id} is missing edition identity"
            }
            if (mapping.printedPageStart == null || mapping.printedPageEnd == null || mapping.printedPageStart > mapping.printedPageEnd) {
                errors += "Mapping ${mapping.id} approves a blank page range"
            }
            if (mapping.reviewer.isNullOrBlank() || mapping.reviewerQualification.isNullOrBlank()) {
                errors += "Mapping ${mapping.id} is APPROVED without an identified qualified reviewer"
            }
            if (mapping.proposedLessonNumber == null || mapping.proposedLessonNumber != mapping.unitNumber) {
                errors += "Mapping ${mapping.id} is for the wrong unit or lesson"
            }
            if (unit != null && mapping.reviewedContentDigest != unitContentReviewDigest(pack, mapping.unitNumber)) {
                errors += "Mapping ${mapping.id} reviewed content digest does not match the unit content-review digest"
            }
            if (unitReview(pack, mapping.unitNumber)?.status != "APPROVED") {
                errors += "Mapping ${mapping.id} is APPROVED without an approved linguistic review"
            }
        }
        return errors.distinct()
    }

    private fun validateMappingInputStaysOutOfCurriculum(pack: V2CurriculumPack, errors: MutableList<String>) {
        pack.sources.forEach { source ->
            if (isRedistributedMappingInput(source.id, source.title, source.license, source.attribution, source.modification)) {
                errors += "Mapping input is not redistributable curriculum content (${source.id})"
            }
        }
        pack.examples.forEach { example ->
            if (isRedistributedMappingInput(example.id, example.display, example.note, example.sourceId)) {
                errors += "Mapping input is not redistributable curriculum content (${example.id})"
            }
        }
        pack.items.forEach { item ->
            if (isRedistributedMappingInput(item.id, item.prompt, item.answer, item.feedback, item.sourceId)) {
                errors += "Mapping input is not redistributable curriculum content (${item.id})"
            }
        }
        pack.references.forEach { reference ->
            if (isRedistributedMappingInput(reference.id, reference.title, reference.summary, reference.detail, reference.sourceId)) {
                errors += "Mapping input is not redistributable curriculum content (${reference.id})"
            }
        }
        pack.units.forEach { unit ->
            val parts = mutableListOf(unit.topicSummary, unit.mappingNote, unit.title)
            unit.sessions.forEach { session ->
                session.steps.forEach { step ->
                    parts += step.body
                    parts += step.title
                    step.bookDirection?.let { parts += it }
                }
            }
            if (isRedistributedMappingInput(*parts.toTypedArray())) {
                errors += "Mapping input is not redistributable curriculum content (unit ${unit.number})"
            }
        }
    }

    private fun isRedistributedMappingInput(vararg parts: String?): Boolean {
        val text = parts.filterNotNull().joinToString(" ").lowercase()
        return REDISTRIBUTED_MAPPING_MARKERS.any { text.contains(it) }
    }

    private fun pronunciationAudioResolves(pack: V2CurriculumPack, unit: V2Unit): Boolean {
        val letterIds = reachableLetterIds(pack, unit)
        return pack.audioAssets.any { asset ->
            asset.unitNumber == unit.number &&
                asset.letterId in letterIds &&
                asset.intakeReviewStatus == "APPROVED" &&
                pack.letters.any { it.id == asset.letterId && it.audioAssetId == asset.id } &&
                sourceAndRightsResolve(pack, asset.sourceId, asset.rightsId)
        }
    }

    private fun handwritingAssetResolves(pack: V2CurriculumPack, unit: V2Unit): Boolean {
        val letterIds = reachableLetterIds(pack, unit)
        return pack.handwritingAssets.any { asset ->
            asset.unitNumber == unit.number &&
                asset.letterId in letterIds &&
                asset.intakeReviewStatus == "APPROVED" &&
                pack.letters.any { it.id == asset.letterId } &&
                sourceAndRightsResolve(pack, asset.sourceId, asset.rightsId)
        }
    }

    private fun ownedEditionMappingResolves(pack: V2CurriculumPack, unit: V2Unit): Boolean {
        val review = unitReview(pack, unit.number) ?: return false
        val mapping = pack.ownedEditionMappings.firstOrNull { it.id == review.curriculumMappingId } ?: return false
        return mapping.unitNumber == unit.number &&
            mapping.status == "APPROVED" &&
            mappingRecordErrors(pack, mapping).isEmpty()
    }

    private fun letterAudioResolves(pack: V2CurriculumPack, letter: V2Letter): Boolean {
        val audioId = letter.audioAssetId ?: return false
        val asset = pack.audioAssets.firstOrNull { it.id == audioId } ?: return false
        return asset.letterId == letter.id &&
            asset.intakeReviewStatus == "APPROVED" &&
            sourceAndRightsResolve(pack, asset.sourceId, asset.rightsId)
    }

    private fun reachableExampleIds(unit: V2Unit): Set<String> {
        return (unit.objectives.flatMap { it.exampleIds } + unit.sessions.flatMap { session ->
            session.steps.flatMap { it.exampleIds }
        }).toSet()
    }

    private fun reachableItemIds(unit: V2Unit): Set<String> {
        return (unit.objectives.flatMap { it.checkItemIds } + unit.sessions.flatMap { session ->
            session.steps.flatMap { it.itemIds }
        }).toSet()
    }

    private fun reachableReferenceIds(unit: V2Unit): Set<String> {
        return unit.sessions.flatMap { session -> session.steps.flatMap { it.referenceIds } }.toSet()
    }

    private fun reachableLetterIds(pack: V2CurriculumPack, unit: V2Unit): Set<String> {
        return reachableExampleIds(unit).mapNotNull { pack.example(it)?.letterId?.takeIf { id -> id.isNotBlank() } }.toSet()
    }

    private fun sourceAndRightsResolve(pack: V2CurriculumPack, sourceId: String, rightsId: String): Boolean {
        if (pack.sources.none { it.id == sourceId }) return false
        return pack.rights.any { it.id == rightsId && it.sourceId == sourceId && it.permitsAppUse }
    }

    private val CYRILLIC_REGION = Regex("[\\u0400-\\u04FF\\u0301](?:[\\u0400-\\u04FF\\u0301]|[ \\t]+(?=[\\u0400-\\u04FF]))*[?!.,]*")

    private fun validateProvenance(pack: V2CurriculumPack, errors: MutableList<String>) {
        pack.sources.forEach { source ->
            if (isExcludedSource(source.id, source.license, source.title)) {
                errors += "Source ${source.id} excluded source cannot enter the seed"
            }
        }
        val seen = mutableSetOf<String>()
        pack.provenanceRecords.forEach { record ->
            if (!seen.add(record.itemId)) {
                errors += "Provenance ${record.itemId} is duplicated"
            }
            if (record.sourceRevision.isBlank()) {
                errors += "Provenance ${record.itemId} source revision is unresolved"
            }
            if (record.shippedText.isBlank() || record.originalSourceText.isBlank() || record.sourceRecordId.isBlank()) {
                errors += "Provenance ${record.itemId} source record is unresolved"
            }
            if (record.attribution.isBlank() || record.shareAlikeTreatment.isBlank()) {
                errors += "Provenance ${record.itemId} attribution does not resolve"
            }
            if (!isShareAlikeLicense(record.licenseId) || record.licenseUrl.isBlank()) {
                errors += "Provenance ${record.itemId} incompatible license"
            }
            if (isExcludedProvenance(record)) {
                errors += "Provenance ${record.itemId} excluded source cannot enter the seed"
            }
            if (record.contentHash != sha256(record.shippedText) || record.sourceHash != sha256(record.originalSourceText)) {
                errors += "Provenance ${record.itemId} source hash does not match the frozen record"
            }
            if (record.transformation == "sense selection" && !isDelimitedSense(record.originalSourceText, record.shippedText)) {
                errors += "Provenance ${record.itemId} sense selection is not a delimited source sense"
            }
            if (record.sourceId == "src-openrussian-50e210c4" && "/tree/" in record.sourceUrl) {
                errors += "Provenance ${record.itemId} source URL is not an exact file link"
            }
            if (record.sourceId == "src-openrussian-50e210c4" && "/blob/" !in record.sourceUrl) {
                errors += "Provenance ${record.itemId} source URL is not an exact file link"
            }
            if (pack.sources.none { it.id == record.sourceId }) {
                errors += "Provenance ${record.itemId} source does not resolve"
            }
            val rights = pack.rights.filter { it.sourceId == record.sourceId && it.permitsAppUse }
            if (rights.isEmpty() || rights.any { !isShareAlikeLicense(it.license) }) {
                errors += "Provenance ${record.itemId} rights do not resolve"
            }
            if (record.audioAssetId != null) {
                errors += "Provenance ${record.itemId} audio mapping is not an exact admitted text match"
            }
            if (record.reviewStatus !in CLOSED_REVIEW_STATES) {
                errors += "Provenance ${record.itemId} has unrecognized review status ${record.reviewStatus}"
            }
            if (record.unitNumber !in 3..10) {
                errors += "Provenance ${record.itemId} is outside the proposed Units 3-10 source intake"
            }
            if (pack.units.flatMap { it.objectives }.none { it.id == record.objectiveId && it.unitNumber == record.unitNumber }) {
                errors += "Provenance ${record.itemId} objective is missing"
            }
        }
        pack.units.filter { unit -> pack.provenanceRecords.any { it.unitNumber == unit.number } }.forEach { unit ->
            val shipped = pack.provenanceRecords.filter { it.unitNumber == unit.number }.map { it.shippedText }.toSet()
            val hosts = mutableListOf(unit.topicSummary, unit.mappingNote, unit.title)
            unit.objectives.forEach { objective ->
                hosts += objective.title
                objective.exampleIds.forEach { id ->
                    pack.example(id)?.let { hosts += it.display; hosts += it.note }
                }
                objective.checkItemIds.forEach { id -> addItemHosts(pack, id, hosts) }
            }
            unit.sessions.forEach { session ->
                session.steps.forEach { step ->
                    hosts += step.title
                    hosts += step.body
                    step.bookDirection?.let { hosts += it }
                    step.companionDirection?.let { hosts += it }
                    step.unavailableReason?.let { hosts += it }
                    step.exampleIds.forEach { id ->
                        pack.example(id)?.let { hosts += it.display; hosts += it.note }
                    }
                    step.itemIds.forEach { id -> addItemHosts(pack, id, hosts) }
                    step.referenceIds.forEach { id ->
                        pack.reference(id)?.let { hosts += it.title; hosts += it.summary; hosts += it.detail }
                    }
                }
            }
            hosts.forEach { host ->
                CYRILLIC_REGION.findAll(host).forEach { match ->
                    val text = match.value.trim()
                    val trimmed = text.trimEnd { it == '?' || it == '!' || it == '.' || it == ',' }
                    if (text !in shipped && trimmed !in shipped) {
                        errors += "Russian text has no admitted source record: $text"
                    }
                }
            }
        }
    }

    private fun addItemHosts(pack: V2CurriculumPack, itemId: String, hosts: MutableList<String>) {
        val item = pack.item(itemId) ?: return
        hosts += item.prompt
        item.promptLetter?.let { hosts += it }
        hosts += item.answer
        hosts += item.feedback
        hosts += item.options
        hosts += item.distractorRationales
        item.assessment?.let { assessment ->
            hosts += assessment.whyAnswerDemonstratesObjective
            hosts += assessment.distractorDifference
        }
    }

    private val LINGUISTIC_CLAIM = Regex(
        "(?i)(\\brussian\\b.*\\b(has|have|is|are)\\b|\\b(has|have)\\b.*\\b(gender|genders|cases|tense)\\b|\\bten genders\\b|\\b\\d+\\s+genders\\b)",
    )
    private val GRAMMAR_TERM = Regex(
        "(?i)\\b(genders?|cases?|tenses?|verbs?|nouns?|pronouns?|masculine|feminine|neuter|nominative|prepositional|indeclinable|declinable)\\b",
    )

    private fun isDelimitedSense(original: String, shipped: String): Boolean {
        if (shipped.isBlank() || shipped == original) return false
        val parts = original.split(Regex("\\s*[,;]\\s*")).map { it.trim() }.filter { it.isNotEmpty() }
        return shipped in parts
    }

    private fun validateAuthoredEnglish(pack: V2CurriculumPack, errors: MutableList<String>) {
        val seen = mutableSetOf<String>()
        pack.authoredEnglishRecords.forEach { record ->
            if (!seen.add(record.id)) {
                errors += "Authored English ${record.id} is duplicated"
            }
            if (record.classification != "INDEPENDENTLY_AUTHORED_SCAFFOLD") {
                errors += "Authored English ${record.id} classification is not independently authored"
            }
            if (record.text.isBlank()) {
                errors += "Authored English ${record.id} is blank"
            }
            if (record.unitNumber !in 3..10) {
                errors += "Authored English ${record.id} is outside the proposed Units 3-10 source intake"
            }
            if (CYRILLIC_REGION.containsMatchIn(record.text) ||
                LINGUISTIC_CLAIM.containsMatchIn(record.text) ||
                (GRAMMAR_TERM.containsMatchIn(record.text) && '?' !in record.text)
            ) {
                errors += "independently authored English makes a linguistic claim"
            }
        }
        pack.units.filter { unit -> pack.provenanceRecords.any { it.unitNumber == unit.number } }.forEach { unit ->
            val shipped = pack.provenanceRecords.filter { it.unitNumber == unit.number }.map { it.shippedText }
            val scaffolds = pack.authoredEnglishRecords.filter { it.unitNumber == unit.number }.map { it.text }
            val spans = shipped + scaffolds
            val hosts = mutableListOf<String>()
            unit.sessions.forEach { session ->
                session.steps.forEach { step ->
                    hosts += step.title
                    hosts += step.body
                    step.bookDirection?.let { hosts += it }
                    step.companionDirection?.let { hosts += it }
                    step.exampleIds.forEach { id ->
                        pack.example(id)?.let { hosts += it.display; hosts += it.note }
                    }
                    step.itemIds.forEach { id -> addItemHosts(pack, id, hosts) }
                }
            }
            unit.objectives.forEach { objective ->
                objective.exampleIds.forEach { id ->
                    pack.example(id)?.let { hosts += it.display; hosts += it.note }
                }
                objective.checkItemIds.forEach { id -> addItemHosts(pack, id, hosts) }
            }
            hosts.forEach { host ->
                val leftover = unboundLetters(host, spans)
                if (leftover.isNotEmpty()) {
                    errors += "English linguistic claim has no admitted source record: ${leftover.take(120)}"
                }
            }
            val allowed = (shipped + scaffolds).toSet()
            val itemIds = (unit.sessions.flatMap { it.steps }.flatMap { it.itemIds } + unit.objectives.flatMap { it.checkItemIds }).toSet()
            itemIds.forEach { itemId ->
                val item = pack.item(itemId) ?: return@forEach
                (listOf(item.answer) + item.options).forEach { text ->
                    if (text !in allowed) {
                        errors += "English linguistic claim has no admitted source record: $text"
                    }
                }
            }
        }
    }

    private fun unboundLetters(host: String, spans: List<String>): String {
        val covered = BooleanArray(host.length)
        spans.filter { it.isNotEmpty() }.distinct().sortedByDescending { it.length }.forEach { span ->
            var start = 0
            while (start <= host.length - span.length) {
                val index = host.indexOf(span, start)
                if (index < 0) break
                val end = index + span.length
                if ((index until end).all { !covered[it] } && englishSpanBoundary(host, index, end)) {
                    for (cursor in index until end) covered[cursor] = true
                }
                start = index + 1
            }
        }
        return host.filterIndexed { index, char -> !covered[index] && char.isLetter() }
    }

    private fun englishSpanBoundary(host: String, index: Int, end: Int): Boolean {
        val span = host.substring(index, end)
        if (span.any { it.code > 127 }) return true
        val before = if (index == 0) ' ' else host[index - 1]
        val after = if (end == host.length) ' ' else host[end]
        val startOk = !before.isLetter() || !span.first().isLetter()
        val endOk = !after.isLetter() || !span.last().isLetter()
        return startOk && endOk
    }

    private fun isShareAlikeLicense(license: String): Boolean {
        val value = license.lowercase()
        if (value.contains("nc") || value.contains("nd") || value.contains("noncommercial") || value.contains("noderivatives")) {
            return false
        }
        return license == "CC BY-SA 4.0"
    }

    private fun isExcludedSource(vararg parts: String): Boolean {
        return parts.any { part -> excludedSourceToken(part) }
    }

    private fun isExcludedProvenance(record: V2ProvenanceRecord): Boolean {
        val haystack = "${record.sourceId} ${record.sourceUrl} ${record.licenseId}"
        if (!excludedSourceToken(haystack)) return false
        val value = haystack.lowercase()
        val attributedTatoeba = "tatoeba" in value &&
            "audio" !in value &&
            record.sourceRecordId.matches(Regex("\\d+")) &&
            record.attribution.isNotBlank() &&
            "translation" in record.transformation.lowercase() &&
            isShareAlikeLicense(record.licenseId)
        return !attributedTatoeba
    }

    private fun excludedSourceToken(text: String): Boolean {
        val value = text.lowercase()
        return listOf(
            "penguin",
            "fsi",
            "wiktionary",
            "tatoeba",
            "openrussian-audio",
            "website-audio",
            "noncommercial",
            "noderivatives",
            "cc-by-nc",
            "cc-by-nd",
            "cc by-nc",
            "cc by-nd",
        ).any { token -> value.contains(token) }
    }
}
