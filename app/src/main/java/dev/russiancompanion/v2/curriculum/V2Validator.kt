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
        validateSourceCapabilities(pack, errors)
        validateLateUnitRussian(pack, errors)
        validateAuthoredEnglish(pack, errors)
        validateLearnerFacingMarkup(pack, errors)
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

    private fun canonicalAssessment(assessment: V2Assessment): String {
        val fields = mutableListOf(
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
            "relationKind" to canonStr(assessment.relationKind),
            "relationParticipantIds" to canonArr(assessment.relationParticipantIds.map(::canonStr)),
        )
        if (assessment.responseKind.isNotBlank()) {
            fields += "responseKind" to canonStr(assessment.responseKind)
        }
        return canonObj(*fields.toTypedArray())
    }

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
            errors += completionGapErrors(pack, unit)
        }
        errors += unresolvedVarianceErrors(unit)
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

    private fun completionGapErrors(pack: V2CurriculumPack, unit: V2Unit): List<String> {
        if (unit.number == 7) {
            return missingCapabilityError(
                pack,
                unit,
                "Unit 7 is not content-complete without an admitted introductory adverb paradigm source capability",
            )
        }
        if (unit.number !in 16..30) return emptyList()
        return when (unit.number) {
            16 -> missingCapabilityError(
                pack,
                unit,
                "Unit 16 is not content-complete without an admitted instrumental complement span source capability",
            )
            17 -> missingCapabilityError(
                pack,
                unit,
                "Unit 17 is not content-complete without telling-time, weekday, date, and age sources as required source capabilities",
            )
            18 -> missingCapabilityError(
                pack,
                unit,
                "Unit 18 is not content-complete without comparison and relative-clause sources as required source capabilities",
            )
            19 -> missingCapabilityError(
                pack,
                unit,
                "Unit 19 is not content-complete without conditional and obligation sources as required source capabilities",
            )
            20 -> missingCapabilityError(
                pack,
                unit,
                "Unit 20 is not content-complete from a red link, general verb page, or dictionary rows alone",
            )
            21 -> missingCapabilityError(
                pack,
                unit,
                "Unit 21 is not content-complete without admitted reflexive-object and purpose source capabilities",
            )
            22 -> missingCapabilityError(
                pack,
                unit,
                "Unit 22 is not content-complete without admitted declined-numeral, both, one-and-a-half, and collective source capabilities",
            )
            23 -> missingCapabilityError(
                pack,
                unit,
                "Unit 23 is not content-complete without an admitted elapsed-time and duration source capability",
            )
            24 -> missingCapabilityError(
                pack,
                unit,
                "Unit 24 is not content-complete without admitted alternation, negative-pronoun, and scope source capabilities",
            )
            25 -> missingCapabilityError(
                pack,
                unit,
                "Unit 25 is not content-complete without admitted productive-diminutive and register source capabilities",
            )
            in 26..30 -> lateUnitSourceGap(pack, unit)
            else -> emptyList()
        }
    }

    private data class RequiredSourceCapability(val objectiveId: String, val claim: String)

    private val BATCH_A_OBJECTIVES = setOf(
        "obj-u16-complements",
        "obj-u17-age",
        "obj-u17-weekdays",
        "obj-u17-months",
        "obj-u17-clock",
        "obj-u17-clock-answers",
        "obj-u17-calendar-date",
        "obj-u7-adverb",
        "obj-u18-comparison",
        "obj-u18-relative",
        "obj-u19-conditional",
        "obj-u19-obligation",
        "obj-u20-motion",
    )

    private val REQUIRED_SOURCE_CAPABILITIES = mapOf(
        7 to listOf(
            RequiredSourceCapability("obj-u7-adverb", "adverb-form-gloss"),
            RequiredSourceCapability("obj-u7-adverb", "adverb-paradigm"),
        ),
        16 to listOf(
            RequiredSourceCapability("obj-u16-complements", "instrumental-complements"),
        ),
        17 to listOf(
            RequiredSourceCapability("obj-u17-age", "age"),
            RequiredSourceCapability("obj-u17-weekdays", "weekdays"),
            RequiredSourceCapability("obj-u17-months", "months"),
            RequiredSourceCapability("obj-u17-clock", "clock-question"),
            RequiredSourceCapability("obj-u17-clock-answers", "clock-expression-recognition"),
            RequiredSourceCapability("obj-u17-clock-answers", "productive-clock-time"),
            RequiredSourceCapability("obj-u17-calendar-date", "calendar-date"),
        ),
        18 to listOf(
            RequiredSourceCapability("obj-u18-comparison", "comparison"),
            RequiredSourceCapability("obj-u18-relative", "relative-kotoryy"),
        ),
        19 to listOf(
            RequiredSourceCapability("obj-u19-conditional", "conditional-by"),
            RequiredSourceCapability("obj-u19-obligation", "obligation-dolzhen"),
            RequiredSourceCapability("obj-u19-probability", "probability-dolzhen"),
        ),
        20 to listOf(
            RequiredSourceCapability("obj-u20-motion", "motion-directionality"),
            RequiredSourceCapability("obj-u20-motion", "motion-foot-transport"),
            RequiredSourceCapability("obj-u20-motion", "motion-carrying-leading"),
            RequiredSourceCapability("obj-u20-motion", "motion-po-setting-out"),
            RequiredSourceCapability("obj-u20-motion", "motion-prefixed-path"),
        ),
        21 to listOf(
            RequiredSourceCapability("obj-u21-object-reference", "reflexive-object"),
            RequiredSourceCapability("obj-u21-purpose", "purpose-clause"),
        ),
        22 to listOf(
            RequiredSourceCapability("obj-u22-declension", "declined-numeral"),
            RequiredSourceCapability("obj-u22-both-forms", "both-forms"),
            RequiredSourceCapability("obj-u22-half-forms", "one-and-a-half"),
            RequiredSourceCapability("obj-u22-collective", "collective-numeral"),
        ),
        23 to listOf(
            RequiredSourceCapability("obj-u23-duration", "elapsed-time-duration"),
        ),
        24 to listOf(
            RequiredSourceCapability("obj-u24-alternation", "negation-alternation"),
            RequiredSourceCapability("obj-u24-stand-ins", "negative-pronoun"),
            RequiredSourceCapability("obj-u24-placement", "negation-scope"),
        ),
        25 to listOf(
            RequiredSourceCapability("obj-u25-nickname-rule", "productive-diminutive"),
            RequiredSourceCapability("obj-u25-address", "register"),
        ),
        26 to listOf(
            RequiredSourceCapability("obj-u26-indefinite", "indefinite-pronoun"),
            RequiredSourceCapability("obj-u26-word-order", "word-order"),
            RequiredSourceCapability("obj-u26-postal-address", "postal-address"),
            RequiredSourceCapability("obj-u26-letter-writing", "letter-writing"),
        ),
        27 to listOf(
            RequiredSourceCapability("obj-u27-passive", "passive-participle"),
            RequiredSourceCapability("obj-u27-stress", "participle-stress"),
        ),
        28 to listOf(
            RequiredSourceCapability("obj-u28-imperfective-adverb", "imperfective-verbal-adverb"),
            RequiredSourceCapability("obj-u28-perfective-adverb", "perfective-verbal-adverb"),
        ),
        29 to listOf(
            RequiredSourceCapability("obj-u29-active", "active-participle"),
            RequiredSourceCapability("obj-u29-punctuation", "participle-punctuation"),
            RequiredSourceCapability("obj-u29-short-adjective", "short-form-adjective"),
            RequiredSourceCapability("obj-u29-register", "bookish-register"),
        ),
        30 to listOf(
            RequiredSourceCapability("obj-u30-dictionary", "dictionary-use"),
            RequiredSourceCapability("obj-u30-abbreviation", "abbreviation"),
            RequiredSourceCapability("obj-u30-letter-names", "letter-name-review"),
            RequiredSourceCapability("obj-u30-particle", "particle"),
            RequiredSourceCapability("obj-u30-capstone", "capstone-reading"),
        ),
    )

    /**
     * Runtime advancement for Units 26-30 stays closed while required source
     * capabilities are unresolved. Units 27, 29, and 30 also stay closed while
     * the authority variance is proposed and not activated.
     */
    /**
     * A readiness edit cannot advance a unit while one of its required source
     * capabilities is still unresolved.
     */
    internal fun unresolvedPartialUnitAdvancementBlockers(pack: V2CurriculumPack, unit: V2Unit): List<String> {
        val unresolved = unresolvedRequiredSourceCapabilities(pack, unit.number).map { it.claim }.toSet()
        return when (unit.number) {
            7 -> if ("adverb-paradigm" in unresolved) {
                listOf("Unit 7 is missing an introductory adverb paradigm source capability")
            } else {
                emptyList()
            }
            17 -> if ("productive-clock-time" in unresolved || "calendar-date" in unresolved) {
                listOf("Unit 17 is missing productive clock-time and calendar-date source capabilities")
            } else {
                emptyList()
            }
            19 -> if ("probability-dolzhen" in unresolved) {
                listOf("Unit 19 is missing a probability source capability")
            } else {
                emptyList()
            }
            else -> emptyList()
        }
    }

    internal fun unresolvedLateUnitAdvancementBlockers(pack: V2CurriculumPack, unit: V2Unit): List<String> {
        if (unit.number !in 26..30) return emptyList()
        val blockers = mutableListOf<String>()
        blockers += lateUnitSourceGap(pack, unit)
        if (unit.number in UNRESOLVED_VARIANCE_UNITS) {
            blockers += "Unit ${unit.number} is blocked by an unresolved authority variance"
        }
        return blockers
    }

    private fun lateUnitSourceGap(pack: V2CurriculumPack, unit: V2Unit): List<String> {
        if (unit.number !in 26..30) return emptyList()
        val missing = REQUIRED_SOURCE_CAPABILITIES[unit.number].orEmpty().filter { requirement ->
            pack.sourceCapabilities.none { capability ->
                capability.unitNumber == unit.number &&
                    capability.objectiveId == requirement.objectiveId &&
                    capability.supportedClaim == requirement.claim &&
                    capabilityDefects(pack, capability).isEmpty()
            }
        }.map { it.claim }
        if (missing.isEmpty()) return emptyList()
        val listed = when (missing.size) {
            1 -> missing[0]
            2 -> "${missing[0]} and ${missing[1]}"
            else -> missing.dropLast(1).joinToString(", ") + ", and " + missing.last()
        }
        return listOf("Unit ${unit.number} is not content-complete without admitted $listed source capabilities")
    }

    private fun missingCapabilityError(pack: V2CurriculumPack, unit: V2Unit, message: String): List<String> {
        val required = REQUIRED_SOURCE_CAPABILITIES[unit.number].orEmpty()
        val satisfied = required.all { requirement ->
            pack.sourceCapabilities.any { capability ->
                capability.unitNumber == unit.number &&
                    capability.objectiveId == requirement.objectiveId &&
                    capability.supportedClaim == requirement.claim &&
                    capabilityDefects(pack, capability).isEmpty()
            }
        }
        return if (satisfied) emptyList() else listOf(message)
    }

    private fun validateSourceCapabilities(pack: V2CurriculumPack, errors: MutableList<String>) {
        val seen = mutableSetOf<String>()
        pack.sourceCapabilities.forEach { capability ->
            if (!seen.add(capability.id)) {
                errors += "Source capability ${capability.id} is duplicated"
            }
            errors += capabilityDefects(pack, capability)
        }
    }

    /**
     * A completion blocker clears only when an explicit capability names the
     * governed claim. Keyword text in an unrelated licensed record does not.
     */
    private fun capabilityDefects(pack: V2CurriculumPack, capability: V2SourceCapability): List<String> {
        val errors = mutableListOf<String>()
        val binding = REQUIRED_SOURCE_CAPABILITIES[capability.unitNumber].orEmpty().firstOrNull {
            it.objectiveId == capability.objectiveId && it.claim == capability.supportedClaim
        }
        if (binding == null || capability.supportedClaim.isBlank() || capability.objectiveId.isBlank()) {
            errors += "Source capability ${capability.id} is attached to the wrong unit or objective"
            return errors
        }
        if (
            capability.sourceId.isBlank() ||
            capability.sourceRevision.isBlank() ||
            capability.sourceRecordId.isBlank() ||
            capability.sourceHash.isBlank() ||
            capability.exactSpan.isBlank()
        ) {
            errors += "Source capability ${capability.id} revision does not match the frozen record"
            return errors
        }
        val frozen = pack.provenanceRecords.filter {
            it.sourceId == capability.sourceId &&
                it.sourceRevision == capability.sourceRevision &&
                it.sourceRecordId == capability.sourceRecordId &&
                it.sourceHash == capability.sourceHash &&
                it.sourceHash == sha256(it.originalSourceText) &&
                isShareAlikeLicense(it.licenseId)
        }
        if (frozen.isEmpty()) {
            errors += "Source capability ${capability.id} revision does not match the frozen record"
            return errors
        }
        val spanHost = frozen.firstOrNull { capability.exactSpan == it.originalSourceText }
        if (spanHost == null) {
            errors += "Source capability ${capability.id} span is not in the frozen record"
            return errors
        }
        if (spanHost.unitNumber != capability.unitNumber || spanHost.objectiveId != capability.objectiveId) {
            errors += "Source capability ${capability.id} is attached to the wrong unit or objective"
            return errors
        }
        if (capability.supportedClaim == "adverb-paradigm" && capability.exactSpan == ExplicitSourcePairs.adverbSpan) {
            errors += "Source capability ${capability.id} bounded form/gloss recognition does not clear adverb-paradigm"
        }
        if (capability.supportedClaim == "productive-clock-time" && capability.exactSpan == ExplicitSourcePairs.clockSpan) {
            errors += "Source capability ${capability.id} bounded clock phrases do not clear productive-clock-time"
        }
        if (capability.unitNumber in 26..30) {
            if (STRUCTURAL_PAGE_RANGE.matches(capability.exactSpan.trim())) {
                errors += "Source capability ${capability.id} structural page range is not linguistic evidence"
            }
            if (mutableWebpage(spanHost)) {
                errors += "Source capability ${capability.id} mutable webpage cannot clear a source blocker"
            }
            if (!admittedLateSource(capability)) {
                errors += "Source capability ${capability.id} is not an admitted frozen source"
            }
        }
        if (!capabilityHasCheckChain(pack, capability, spanHost)) {
            errors += "Source capability ${capability.id} has no reviewable instruction and check chain"
        }
        return errors
    }

    private val STRUCTURAL_PAGE_RANGE = Regex("""^\d{2,3}-\d{2,3}$""")

    /**
     * Exact frozen identities for Units 26-30. A keyword, a page range, a mutable
     * page, or a different record cannot join this allowlist.
     */
    private val ADMITTED_UNITS_26_30_SOURCES = setOf(
        "src-wikipedia-russian-grammar-1369617627|1369617627|grammar:Basic word order, both in conversation and written language, is [[subject–verb–o|30e7e08a32bd5ca619593ff841bc22fff849a3ce2fc516d1f0cf67ea0c8e5bb6",
        "src-wikipedia-russian-grammar-1369617627|1369617627|grammar::\u0441\u0434\u0435\u0301\u043b\u0430\u0442\u044c \u2013 to do/to make (perfective verb)\n:\u0441\u0434\u0435\u0301\u043b\u0430'''\u043d\u043d'''\u044b\u0439 \u2013 done/made\n\nPassi|d5d52caab6dbed44f28ad3781309cb45b3437e19e891124b10dda641eadccf7c",
        "src-wikipedia-russian-grammar-1369617627|1369617627|grammar:Present adverbial participles are formed by adding the suffix -\u0430/-\u044f (or sometime|20f7c259be895994aca929d2db990f14799def4b936f69efea557d852bb7a511",
        "src-wikipedia-russian-grammar-1369617627|1369617627|grammar:most are formed with the suffix -\u0432 (alternative form -\u0432\u0448\u0438, always used before -\u0441|686f0cfb2d846647d6ab756b646000f47d7de4b7060ba8a3860cf4f32bb660f6",
        "src-wikipedia-russian-grammar-1369617627|1369617627|grammar:The active past participle is used in order to indicate actions that happened in|dee66aeeccb1cff104490a5dd0378a8f9f44bf6570a07d2fa906224785b1e9b8",
        "src-wikipedia-russian-grammar-1369617627|1369617627|grammar:such as {{wikt-lang|ru|\u0431\u0435\u0436}} ('beige', non-adapted form of {{wikt-lang|ru|\u0431\u0435\u0301\u0436\u0435\u0432|0b5b1bad657a8c901ba9f471ff7adff3a945be6f281aa760f58c59fd0f8c78e8",
        "src-wikipedia-russian-alphabet-1371795481|1371795481|alphabet:! Name\n! Old Church Slavonic word-name\n! [[International Phonetic Alphabet|IPA]]|0ebe7c0ee3c60d5e931fe34660098e88ee1b955585bddb9f03e2388c00dfc526",
    )

    private fun admittedLateSource(capability: V2SourceCapability): Boolean {
        val key = listOf(
            capability.sourceId,
            capability.sourceRevision,
            capability.sourceRecordId,
            capability.sourceHash,
        ).joinToString("|")
        return key in ADMITTED_UNITS_26_30_SOURCES
    }

    private fun mutableWebpage(record: V2ProvenanceRecord): Boolean {
        val url = record.sourceUrl.lowercase()
        val mutableHost = listOf(
            "wikipedia.org",
            "wikibooks.org",
            "wiktionary.org",
            "wikimedia.org",
            "wikisource.org",
        ).any { url.contains(it) }
        return mutableHost && !url.contains("oldid=")
    }

    private val UNRESOLVED_VARIANCE_UNITS = setOf(27, 29, 30)

    /**
     * The Units 26-30 authority variance is proposed and not activated.
     * Affected units cannot become content-complete, installable, or installed
     * until a Product Owner and qualified reviewer activate it.
     */
    private fun unresolvedVarianceErrors(unit: V2Unit): List<String> {
        if (unit.number !in UNRESOLVED_VARIANCE_UNITS) return emptyList()
        val claimsCompletion = unit.readiness != "NOT_PREPARED"
        val claimsInstall = unit.installState == "INSTALLED" || unit.readiness == "INSTALLABLE"
        if (!claimsCompletion && !claimsInstall) return emptyList()
        return listOf("Unit ${unit.number} is blocked by an unresolved authority variance")
    }

    private fun validateLateUnitRussian(pack: V2CurriculumPack, errors: MutableList<String>) {
        pack.units.filter { it.number in 26..30 }.forEach { unit ->
            val provenance = pack.provenanceRecords.filter { it.unitNumber == unit.number }
            learnerRussian(pack, unit).forEach { text ->
                val resolved = provenance.any { record ->
                    record.shippedText == text || record.originalSourceText == text
                }
                if (!resolved) {
                    errors += "Unit ${unit.number} Russian text has no resolved provenance record"
                }
            }
        }
    }

    private fun learnerRussian(pack: V2CurriculumPack, unit: V2Unit): List<String> {
        val texts = mutableListOf<String>()
        unit.sessions.forEach { session ->
            session.steps.forEach { step ->
                texts += step.body
                texts += step.title
                step.bookDirection?.let { texts += it }
                step.companionDirection?.let { texts += it }
                step.unavailableReason?.let { texts += it }
            }
        }
        val objectiveIds = unit.objectives.map { it.id }.toSet()
        pack.examples.filter { it.objectiveId in objectiveIds }.forEach { example ->
            texts += example.display
            texts += example.note
        }
        pack.items.filter { it.objectiveId in objectiveIds }.forEach { item ->
            texts += item.prompt
            texts += item.answer
            texts += item.feedback
            texts += item.options
            texts += item.distractorRationales
            item.assessment?.let { assessment ->
                texts += assessment.whyAnswerDemonstratesObjective
                texts += assessment.distractorDifference
            }
        }
        return texts.filter { hasCyrillic(it) }
    }

    private fun capabilityHasCheckChain(
        pack: V2CurriculumPack,
        capability: V2SourceCapability,
        spanHost: V2ProvenanceRecord,
    ): Boolean {
        val unit = pack.units.firstOrNull { it.number == capability.unitNumber } ?: return false
        val objective = unit.objectives.firstOrNull { it.id == capability.objectiveId } ?: return false
        if (objective.kind != "RECOGNITION" ||
            objective.instructionStepIds.isEmpty() ||
            objective.exampleIds.size < 2 ||
            objective.practiceStepIds.isEmpty() ||
            objective.checkItemIds.isEmpty() ||
            objective.remediationStepIds.isEmpty()
        ) {
            return false
        }
        return objective.checkItemIds.mapNotNull { pack.item(it) }.any { item ->
            val participants = item.assessment?.relationParticipantIds.orEmpty().mapNotNull { id ->
                pack.provenanceRecords.firstOrNull { it.itemId == id }
            }
            val bound = participants.any {
                it.sourceId == spanHost.sourceId &&
                    it.sourceRevision == spanHost.sourceRevision &&
                    it.sourceRecordId == spanHost.sourceRecordId &&
                    it.sourceHash == spanHost.sourceHash &&
                    it.originalSourceText == capability.exactSpan &&
                    it.objectiveId == capability.objectiveId
            }
            bound && item.assessment?.independent == true && structureValid(pack, item).isEmpty()
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
        if (unit.number !in 1..30) {
            errors += "Only Units 1-30 may be development content in this pack"
        }
        if (unit.mappingStatus != "PENDING_CURRICULUM_REVIEWER") {
            errors += "Unit ${unit.number} mapping must remain pending until a reviewer signs the owned edition"
        }
        if (unit.sessions.isEmpty()) errors += "Development unit ${unit.number} has no sessions"
        if (unit.objectives.isEmpty()) errors += "Development unit ${unit.number} has no objectives"
        validateOpenableBody(pack, unit, errors, requireCompleteRecognition = true)
        if (unit.number in 6..30) validateGovernedChecks(pack, unit, errors)
    }

    private val ENGLISH_CUE = Regex("(?i)which admitted\\b.{0,80}\\brecorded for\\b")
    private val NUMBER_CUE = Regex("[0-9][0-9,~/\\.\\-]*")
    private val RELATION_KINDS = setOf(
        "SAME_LEMMA_DIFFERENT_FIELD",
        "SAME_CONSTRUCTION_DIFFERENT_FORM",
        "SAME_RULE_DIFFERENT_BOUNDARY",
        "SOURCE_SUPPLIED_PAIR",
        "RULE_CLASSIFIES_SOURCE_EXAMPLE",
        "EXPLICIT_PARENTHETICAL_GLOSS",
        "EXPLICIT_SOURCE_PAIR",
    )
    private val LEMMA_RECORD = Regex("^(nouns|adjectives|others|verbs)\\.csv:(.+)#([A-Za-z0-9_]+)$")
    private val OPENRUSSIAN_SOURCE = "src-openrussian-50e210c4"
    private val WIKIBOOKS_SOURCE = "src-wikibooks-russian-cc-by-sa"
    private val TOKEN = Regex("[0-9]+|[a-z\\u0430-\\u044f]+")
    private val LATIN_LOOKALIKE = mapOf(
        "e" to "\u0435",
        "y" to "\u0443",
        "a" to "\u0430",
        "o" to "\u043e",
        "p" to "\u0440",
        "c" to "\u0441",
        "x" to "\u0445",
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

    internal fun qualifyingOverlap(shown: String, answer: String): String? {
        val shownTokens = governedTokens(shown)
        val answerTokens = governedTokens(answer)
        if (shownTokens.isNotEmpty() && shownTokens == answerTokens) return "repeats"
        if (tokenSequenceContained(answerTokens, shownTokens)) return "answer-in-cue"
        if (tokenSequenceContained(shownTokens, answerTokens)) return "cue-in-answer"
        val foldedAnswer = foldGoverned(answer)
        if (foldedAnswer.length == 1) {
            val letter = LATIN_LOOKALIKE[foldedAnswer] ?: foldedAnswer
            val foldedShown = foldGoverned(shown)
            if (foldedAnswer in foldedShown || letter in foldedShown) return "answer-in-cue"
        }
        return null
    }

    private fun foldGoverned(text: String): String =
        text.replace("\u0301", "").replace("\u0451", "\u0435").replace("\u0401", "\u0435").lowercase()

    private fun governedTokens(text: String): List<String> =
        TOKEN.findAll(foldGoverned(text)).map { it.value }.toList()

    private fun tokenSequenceContained(small: List<String>, big: List<String>): Boolean {
        if (small.isEmpty() || small.size >= big.size) return false
        val width = small.size
        return (0..big.size - width).any { index -> big.subList(index, index + width) == small }
    }

    private fun hasCyrillic(text: String): Boolean = text.any { it.code in 0x0400..0x04FF }

    private fun verifiedFrozen(record: V2ProvenanceRecord): Boolean {
        if (record.sourceId.isBlank() || record.sourceRevision.isBlank() || record.sourceRecordId.isBlank()) return false
        if (record.originalSourceText.isBlank()) return false
        return record.sourceHash == sha256(record.originalSourceText)
    }

    /** One frozen span. Matching sourceRecordId is not enough: the needle in a Wikibooks id is truncated. */
    private fun sameFrozenSpan(left: V2ProvenanceRecord, right: V2ProvenanceRecord): Boolean =
        verifiedFrozen(left) && verifiedFrozen(right) &&
            left.sourceId == right.sourceId &&
            left.sourceRevision == right.sourceRevision &&
            left.sourceRecordId == right.sourceRecordId &&
            left.sourceHash == right.sourceHash &&
            left.originalSourceText == right.originalSourceText

    private fun sameFrozenSource(left: V2ProvenanceRecord, right: V2ProvenanceRecord): Boolean =
        verifiedFrozen(left) && verifiedFrozen(right) &&
            left.sourceId == right.sourceId &&
            left.sourceRevision == right.sourceRevision

    private fun displayedInFrozen(record: V2ProvenanceRecord): Boolean {
        val original = record.originalSourceText
        val shipped = record.shippedText
        if (shipped.isNotEmpty() && (shipped in original || foldGoverned(shipped) in foldGoverned(original))) return true
        val folded = foldGoverned(shipped)
        if (folded.isNotEmpty() && folded == foldGoverned(original.replace("'", ""))) return true
        val formatted = foldGoverned(formatWikibooksEmphasis(original))
        if (folded.isNotEmpty() && folded in formatted) return true
        val unfolded = foldGoverned(unfoldWikiLabels(original))
        return folded.isNotEmpty() && folded in unfolded
    }

    private val wikiStressMark = Regex(
        "'''([\u0430\u0435\u0451\u0438\u043e\u0443\u044b\u044d\u044e\u044f\u0410\u0415\u0401\u0418\u041e\u0423\u042b\u042d\u042e\u042f])'''",
    )

    /** Admitted Wikibooks bold around one intra-word vowel is a stress mark. Yo keeps its own letter. */
    private fun formatWikibooksEmphasis(text: String): String {
        val marked = wikiStressMark.replace(text) { match ->
            val vowel = match.groupValues[1]
            val start = match.range.first
            val end = match.range.last + 1
            val before = if (start > 0) text[start - 1] else ' '
            val after = if (end < text.length) text[end] else ' '
            if (vowel == "\u0451" || vowel == "\u0401") {
                vowel
            } else if (isWikiWordChar(before) || isWikiWordChar(after)) {
                vowel + "\u0301"
            } else {
                vowel
            }
        }
        return marked.replace("'''", "").replace("''", "")
    }

    private fun isWikiWordChar(char: Char): Boolean = char.code in 0x0400..0x04FF && char.isLetter()

    /** Mechanical wiki-label unfolding. It does not paraphrase the retained span. */
    internal fun unfoldWikiLabels(text: String): String {
        val withoutLang = Regex("""\{\{lang\|ru\|([^{}]*)\}\}""").replace(text) { it.groupValues[1] }
        val withoutLinks = Regex("""\[\[([^\[\]]+)\]\]""").replace(withoutLang) { match ->
            val body = match.groupValues[1]
            val bar = body.indexOf('|')
            if (bar >= 0) body.substring(bar + 1) else body
        }
        return formatWikibooksEmphasis(withoutLinks)
    }

    private val mediaWikiLearnerArtifact = Regex(
        """'''|''|\[\[|\]\]|\{\{|\}\}|<ref|</ref>|\{[|]|\|[}]""",
    )

    private fun validateLearnerFacingMarkup(pack: V2CurriculumPack, errors: MutableList<String>) {
        fun reject(field: String, text: String?) {
            if (!text.isNullOrEmpty() && mediaWikiLearnerArtifact.containsMatchIn(text)) {
                errors += "MediaWiki markup in learner-facing $field"
            }
        }
        pack.units.filter { it.number in 16..30 }.forEach { unit ->
            reject("unit ${unit.number} title", unit.title)
            reject("unit ${unit.number} topic", unit.topicSummary)
            unit.sessions.forEach { session ->
                reject("session ${session.id} title", session.title)
                session.steps.forEach { step ->
                    reject("step ${step.id} title", step.title)
                    reject("step ${step.id} body", step.body)
                    reject("step ${step.id} book direction", step.bookDirection)
                    reject("step ${step.id} companion direction", step.companionDirection)
                    reject("step ${step.id} unavailable reason", step.unavailableReason)
                }
            }
            unit.objectives.forEach { objective ->
                reject("objective ${objective.id} title", objective.title)
            }
        }
        val governedUnits = pack.units.filter { it.number in 16..30 }
        val exampleIds = governedUnits.flatMap { unit -> unit.objectives.flatMap { it.exampleIds } }.toSet()
        val itemIds = governedUnits.flatMap { unit ->
            unit.objectives.flatMap { it.checkItemIds } + unit.sessions.flatMap { session -> session.steps.flatMap { it.itemIds } }
        }.toSet()
        pack.examples.filter { it.id in exampleIds }.forEach { example ->
            reject("example ${example.id} display", example.display)
            reject("example ${example.id} note", example.note)
        }
        pack.items.filter { it.id in itemIds }.forEach { item ->
            reject("item ${item.id} prompt", item.prompt)
            reject("item ${item.id} promptLetter", item.promptLetter)
            reject("item ${item.id} answer", item.answer)
            reject("item ${item.id} feedback", item.feedback)
            item.options.forEach { reject("item ${item.id} option", it) }
            item.distractorRationales.forEach { reject("item ${item.id} rationale", it) }
            item.assessment?.let { assessment ->
                reject("item ${item.id} why", assessment.whyAnswerDemonstratesObjective)
                assessment.distractorDifference.forEach { reject("item ${item.id} assessment difference", it) }
                assessment.contrastMembers.forEach { reject("item ${item.id} contrast", it) }
            }
        }
        pack.provenanceRecords.filter { it.unitNumber in 16..30 }.forEach { record ->
            reject("provenance ${record.itemId} shippedText", record.shippedText)
        }
        pack.authoredEnglishRecords.filter { it.unitNumber in 16..30 }.forEach { record ->
            reject("authored ${record.id}", record.text)
        }
    }

    /**
     * File and lemma before `#`, plus the governed field, for a verified OpenRussian cell.
     * A Wikibooks record whose sourceRecordId only resembles that shape is rejected.
     */
    private fun openRussianLemma(record: V2ProvenanceRecord): Pair<String, String>? {
        if (record.sourceId != OPENRUSSIAN_SOURCE || !verifiedFrozen(record)) return null
        val match = LEMMA_RECORD.matchEntire(record.sourceRecordId) ?: return null
        val fileName = "${match.groupValues[1]}.csv"
        if (fileName !in record.sourceUrl || record.sourceRevision !in record.sourceUrl) return null
        return "$fileName:${match.groupValues[2]}" to match.groupValues[3]
    }

    private fun frozenTextShows(original: String, shown: String): Boolean {
        val folded = foldGoverned(shown)
        return folded.isNotEmpty() && folded in foldGoverned(original)
    }

    /**
     * Recompute the relationship from resolved provenance. Participant ids are pointers only.
     * Rationale text is not consulted.
     */
    private val parentheticalRelation = Regex("(?<![\\u0400-\\u04FF\\u0301])([^\\s()]+)\\s+\\(([^)]+)\\)")

    private fun parentheticalOwners(original: String): Map<String, Set<String>> {
        val formatted = formatWikibooksEmphasis(original)
        val owners = mutableMapOf<String, MutableSet<String>>()
        parentheticalRelation.findAll(formatted).forEach { match ->
            owners.getOrPut(match.groupValues[2]) { mutableSetOf() }.add(match.groupValues[1])
        }
        return owners
    }

    private fun boundedParenthetical(original: String, form: String, gloss: String): Boolean {
        val formatted = formatWikibooksEmphasis(original)
        val needle = "$form ($gloss)"
        var index = formatted.indexOf(needle)
        while (index >= 0) {
            if (index == 0 || !isWikiWordChar(formatted[index - 1])) return true
            index = formatted.indexOf(needle, index + 1)
        }
        return false
    }

    /**
     * Null means the pair is not an explicit parenthetical and other rules may apply.
     * An empty string means the gloss has an explicit owner and this pair is not that owner.
     */
    private fun parentheticalDecision(
        cue: V2ProvenanceRecord,
        answer: V2ProvenanceRecord,
        distractors: List<V2ProvenanceRecord>,
    ): String? {
        if (cue.unitNumber < 26) return null
        val owners = parentheticalOwners(cue.originalSourceText)[answer.shippedText] ?: return null
        val group = listOf(cue, answer) + distractors
        if (
            owners == setOf(cue.shippedText) &&
            boundedParenthetical(cue.originalSourceText, cue.shippedText, answer.shippedText) &&
            hasCyrillic(cue.shippedText) &&
            !hasCyrillic(answer.shippedText) &&
            group.all { sameFrozenSpan(cue, it) } &&
            cue.shippedText != answer.shippedText &&
            group.all { displayedInFrozen(it) } &&
            distractors.all { it.shippedText != answer.shippedText }
        ) {
            return "EXPLICIT_PARENTHETICAL_GLOSS"
        }
        return ""
    }

    /**
     * Null means the span is not one of the admitted explicit-pair slices.
     * An empty string means the slice forbids this cue and gloss.
     */
    internal fun explicitSourcePair(
        cue: V2ProvenanceRecord,
        answer: V2ProvenanceRecord,
        distractors: List<V2ProvenanceRecord>,
    ): String? {
        val table = ExplicitSourcePairs.glosses[cue.originalSourceText] ?: return null
        val group = listOf(cue, answer) + distractors
        val allowed = table[cue.shippedText]
        if (
            allowed != null &&
            answer.shippedText in allowed &&
            cue.shippedText != answer.shippedText &&
            group.all { sameFrozenSpan(cue, it) } &&
            group.all { displayedInFrozen(it) } &&
            distractors.all { it.shippedText != answer.shippedText }
        ) {
            return "EXPLICIT_SOURCE_PAIR"
        }
        return ""
    }

    internal fun classifyRelation(
        cue: V2ProvenanceRecord,
        answer: V2ProvenanceRecord,
        distractors: List<V2ProvenanceRecord>,
    ): String? {
        val parenthetical = parentheticalDecision(cue, answer, distractors)
        if (parenthetical == "") return null
        if (parenthetical != null) return parenthetical
        val explicit = explicitSourcePair(cue, answer, distractors)
        if (explicit == "") return null
        if (explicit != null) return explicit
        val group = listOf(cue, answer) + distractors
        val parsed = group.map { openRussianLemma(it) }
        if (parsed.all { it != null }) {
            val bases = parsed.map { it!!.first }.toSet()
            val revisions = group.map { it.sourceRevision }.toSet()
            val answerField = parsed[1]!!.second
            if (
                bases.size == 1 &&
                revisions.size == 1 &&
                answerField != parsed[0]!!.second &&
                parsed.drop(2).all { answerField != it!!.second } &&
                group.all { displayedInFrozen(it) }
            ) {
                return "SAME_LEMMA_DIFFERENT_FIELD"
            }
        }
        if (
            hasCyrillic(cue.shippedText) &&
            !hasCyrillic(answer.shippedText) &&
            distractors.all { !hasCyrillic(it.shippedText) } &&
            group.all { sameFrozenSpan(cue, it) } &&
            cue.shippedText != answer.shippedText &&
            group.all { displayedInFrozen(it) }
        ) {
            return "RULE_CLASSIFIES_SOURCE_EXAMPLE"
        }
        if (
            cue.sourceId == WIKIBOOKS_SOURCE &&
            cue.sourceRecordId.startsWith("numbers:") &&
            sameFrozenSpan(cue, answer) &&
            displayedInFrozen(cue) &&
            displayedInFrozen(answer) &&
            distractors.isNotEmpty() &&
            distractors.all {
                sameFrozenSource(cue, it) &&
                    it.sourceRecordId.startsWith("numbers:") &&
                    it.sourceRecordId != cue.sourceRecordId &&
                    it.originalSourceText != cue.originalSourceText &&
                    displayedInFrozen(it)
            }
        ) {
            return "SAME_RULE_DIFFERENT_BOUNDARY"
        }
        if (
            group.all { sameFrozenSpan(cue, it) } &&
            cue.shippedText != answer.shippedText &&
            group.all { displayedInFrozen(it) }
        ) {
            return "SOURCE_SUPPLIED_PAIR"
        }
        if (
            sameFrozenSpan(cue, answer) &&
            "||" in cue.originalSourceText &&
            displayedInFrozen(cue) &&
            displayedInFrozen(answer) &&
            distractors.all {
                sameFrozenSource(cue, it) &&
                    "||" in it.originalSourceText &&
                    it.originalSourceText != cue.originalSourceText &&
                    it.sourceRecordId != cue.sourceRecordId &&
                    displayedInFrozen(it)
            }
        ) {
            return "SAME_CONSTRUCTION_DIFFERENT_FORM"
        }
        val answerLemma = openRussianLemma(answer)
        val distractorLemmas = distractors.map { openRussianLemma(it) }
        if (
            answerLemma != null &&
            distractorLemmas.isNotEmpty() &&
            distractorLemmas.all { it != null } &&
            distractorLemmas.all { it!!.first == answerLemma.first && it.second != answerLemma.second } &&
            distractors.all { it.sourceRevision == answer.sourceRevision } &&
            verifiedFrozen(cue) &&
            displayedInFrozen(cue) &&
            displayedInFrozen(answer) &&
            distractors.all { displayedInFrozen(it) } &&
            frozenTextShows(cue.originalSourceText, answer.shippedText) &&
            frozenTextShows(cue.originalSourceText, cue.shippedText) &&
            foldGoverned(cue.shippedText) != foldGoverned(answer.shippedText)
        ) {
            return "SAME_CONSTRUCTION_DIFFERENT_FORM"
        }
        if (
            distractors.isNotEmpty() &&
            distractors.all { sameFrozenSpan(answer, it) } &&
            !sameFrozenSpan(cue, answer) &&
            verifiedFrozen(cue) &&
            displayedInFrozen(answer) &&
            distractors.all { displayedInFrozen(it) } &&
            frozenTextShows(cue.originalSourceText, answer.shippedText) &&
            frozenTextShows(cue.originalSourceText, cue.shippedText) &&
            foldGoverned(cue.shippedText) != foldGoverned(answer.shippedText)
        ) {
            return "SAME_CONSTRUCTION_DIFFERENT_FORM"
        }
        return null
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
        if (assessment.relationKind !in RELATION_KINDS || assessment.evidenceMode != assessment.relationKind) {
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
        if (assessment.contrastMembers.toSet() != item.options.toSet()) {
            errors += "Item ${item.id} options are outside the governed contrast"
        }
        val distractorCount = item.options.count { it != item.answer }
        if (assessment.distractorDifference.size != item.options.size ||
            assessment.distractorSourceIds.size != item.options.size ||
            assessment.relationParticipantIds.size != distractorCount + 2
        ) {
            errors += "Item ${item.id} distractor metadata does not match the options"
        } else {
            val participants = assessment.relationParticipantIds.map { id ->
                pack.provenanceRecords.firstOrNull { it.itemId == id }
            }
            val cue = participants.firstOrNull()
            val answerRecord = participants.getOrNull(1)
            val shown = item.promptLetter.orEmpty()
            if (cue == null || cue.shippedText != shown || cue.objectiveId != item.objectiveId) {
                errors += "Item ${item.id} cue has no contrast provenance"
            }
            if (answerRecord == null || answerRecord.shippedText != item.answer || answerRecord.objectiveId != item.objectiveId ||
                assessment.answerSourceIds != listOf(answerRecord.itemId)
            ) {
                errors += "Item ${item.id} answer has no contrast provenance"
            }
            var distractorIndex = 0
            item.options.forEachIndexed { index, option ->
                val difference = assessment.distractorDifference[index]
                val sourceItemId = assessment.distractorSourceIds[index]
                if (option == item.answer) {
                    if (difference.isNotEmpty() || sourceItemId.isNotEmpty()) {
                        errors += "Item ${item.id} records distractor metadata for the answer"
                    }
                } else {
                    if (difference.isBlank()) {
                        errors += "Item ${item.id} distractor is unrelated to the governed feature"
                    }
                    val expectedId = assessment.relationParticipantIds.getOrNull(distractorIndex + 2)
                    val provenance = participants.getOrNull(distractorIndex + 2)
                    distractorIndex += 1
                    if (sourceItemId != expectedId || provenance == null || provenance.shippedText != option || provenance.objectiveId != item.objectiveId) {
                        errors += "Item ${item.id} distractor is unrelated to the governed feature"
                    }
                }
            }
            if (errors.isEmpty() && cue != null && answerRecord != null) {
                val distractorRecords = participants.drop(2).filterNotNull()
                val actual = classifyRelation(cue, answerRecord, distractorRecords)
                if (actual != assessment.relationKind) {
                    errors += "Item ${item.id} distractor is unrelated to the governed feature"
                }
            }
        }
        if (assessment.independent) {
            val overlap = qualifyingOverlap(item.promptLetter.orEmpty(), item.answer)
            if (overlap != null) {
                errors += "Item ${item.id} qualifying check is $overlap"
            }
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
        val unitNumber = item.objectiveId.removePrefix("obj-u").substringBefore("-").toIntOrNull()
        if (unitNumber in 11..30 || item.objectiveId in BATCH_A_OBJECTIVES) {
            errors += explicitTargetErrors(pack, item)
        }
        return errors
    }

    /**
     * Structural protection for Units 11–20 advancement checks.
     * It rejects known ambiguity classes. It is not qualified linguistic approval.
     */
    private val COLOCATION_PROMPT = Regex("(?i)\\bother row\\b|\\bother line\\b|same frozen record|another cell of the same")
    private val PAST_FIELDS = setOf("past_m", "past_f", "past_n", "past_pl")
    private val IMPERSONAL_INFLECTION = setOf("past_m", "past_f", "past_pl")
    private val TARGET_PHRASES = mapOf(
        "FEMININE_PAST" to "feminine past",
        "MASCULINE_PAST" to "masculine past",
        "NEUTER_PAST" to "neuter past",
        "PLURAL_PAST" to "plural past",
        "DATIVE_SINGULAR" to "dative singular",
        "PREPOSITIONAL_PLURAL" to "prepositional plural",
        "SINGULAR_IMPERATIVE" to "singular imperative",
        "PLURAL_IMPERATIVE" to "plural imperative",
        "FIRST_PERSON_SINGULAR_FUTURE" to "first-person singular future",
        "FIRST_PERSON_PLURAL_FUTURE" to "first-person plural future",
        "SECOND_PERSON_PLURAL_FUTURE" to "second-person plural future",
        "THIRD_PERSON_PLURAL_FUTURE" to "third-person plural future",
        "PERFECTIVE_PARTNER" to "perfective partner",
        "IMPERFECTIVE_PARTNER" to "imperfective partner",
        "PERFECTIVE_PAST" to "perfective past",
        "IMPERFECTIVE_PAST" to "imperfective past",
        "REPORTED_EVENING" to "reported evening",
        "DIRECT_EVENING" to "direct evening",
        "MOSCOW_FUTURE" to "Moscow",
        "BOUNDED_FUTURE" to "bounded future",
        "UNBOUNDED_FUTURE" to "unbounded future",
        "IMPERSONAL_PAST" to "impersonal past",
        "ACCEPTED_SPELLING" to "accepted spelling",
        "REJECTED_SPELLING" to "rejected spelling",
        "NEUTER_FORM" to "neuter form",
        "MASCULINE_FORM" to "masculine form",
        "TENSE_KEPT" to "same tense",
        "ENGLISH_TENSE_CHANGE" to "changing the reported tense",
        "INSTRUMENTAL_SINGULAR" to "instrumental singular",
        "INSTRUMENTAL_PLURAL" to "instrumental plural",
        "MASCULINE_INSTRUMENTAL" to "masculine instrumental",
        "PLURAL_INSTRUMENTAL" to "plural instrumental",
        "PENCIL_INSTRUMENTAL" to "pencil instrumental",
        "ACCOMPANIMENT_LINE" to "accompaniment line",
        "MEANS_INSTRUMENTAL" to "means instrumental",
        "BETWEEN_LINE" to "between line",
        "OVER_LABEL" to "over label",
        "UNDER_LABEL" to "under label",
        "BEFORE_LABEL" to "before label",
        "FIRST_ORDINAL" to "first ordinal",
        "SECOND_ORDINAL" to "second ordinal",
        "THIRD_ORDINAL" to "third ordinal",
        "ONE_HOUR_ROW" to "one-hour row",
        "TWO_HOUR_ROW" to "two-hour row",
        "FIVE_HOUR_ROW" to "five-hour row",
        "ONE_YEAR_ROW" to "one-year row",
        "TWO_YEAR_ROW" to "two-year row",
        "FIVE_YEAR_ROW" to "five-year row",
        "ONE_MINUTE_ROW" to "one-minute row",
        "TWO_MINUTE_ROW" to "two-minute row",
        "FIVE_MINUTE_ROW" to "five-minute row",
        "DISSUADE_MEANING" to "dissuade meaning",
        "AGREE_MEANING" to "agree meaning",
        "DISSUADE_PERFECTIVE" to "dissuade perfective",
    )
    private val RESPONSE_KINDS = setOf(
        "RUSSIAN_TOKEN",
        "RUSSIAN_PHRASE",
        "RUSSIAN_CLAUSE",
        "ENGLISH_CLASSIFICATION",
    )
    private val CLAUSE_START = setOf(
        "\u043e\u043d", "\u043e\u043d\u0430", "\u043e\u043d\u043e", "\u043e\u043d\u0438", "\u044f", "\u0442\u044b", "\u0432\u044b", "\u043c\u044b",
        "\u0435\u043c\u0443", "\u0435\u0439", "\u0438\u043c", "\u043c\u043d\u0435", "\u0442\u0435\u0431\u0435", "\u0432\u0430\u043c", "\u043d\u0430\u043c", "\u043a\u043e\u0433\u0434\u0430",
    )
    private val ENGLISH_CLASSIFICATION_PROMPT = Regex("(?i)\\blabel\\b|\\benglish\\b")

    /**
     * Structural option shape for Units 11–15. Equal word counts are not required.
     * A full clause and one of its components do not share a shape.
     */
    internal fun structuralResponseKind(text: String): String {
        if (!hasCyrillic(text)) return "ENGLISH_CLASSIFICATION"
        val tokens = governedTokens(text)
        if (tokens.size <= 1) return "RUSSIAN_TOKEN"
        if (text.contains(',') || text.contains(';') || tokens.first() in CLAUSE_START) return "RUSSIAN_CLAUSE"
        return "RUSSIAN_PHRASE"
    }

    private fun responseShapeIncompatible(item: V2CheckItem): Boolean {
        val declared = item.assessment?.responseKind.orEmpty()
        if (declared !in RESPONSE_KINDS) return true
        if (item.options.any { structuralResponseKind(it) != declared }) return true
        if (declared == "ENGLISH_CLASSIFICATION" && !ENGLISH_CLASSIFICATION_PROMPT.containsMatchIn(item.prompt)) return true
        val lists = item.options.map { governedTokens(it) }
        return lists.indices.any { left ->
            lists.indices.any { right -> left != right && tokenSequenceContained(lists[left], lists[right]) }
        }
    }

    private val TARGET_FIELDS = mapOf(
        "FEMININE_PAST" to "past_f",
        "MASCULINE_PAST" to "past_m",
        "NEUTER_PAST" to "past_n",
        "PLURAL_PAST" to "past_pl",
        "DATIVE_SINGULAR" to "sg_dat",
        "PREPOSITIONAL_PLURAL" to "pl_prep",
        "SINGULAR_IMPERATIVE" to "imperative_sg",
        "PLURAL_IMPERATIVE" to "imperative_pl",
        "NEUTER_FORM" to "decl_n_nom",
        "MASCULINE_FORM" to "decl_m_nom",
        "FIRST_PERSON_SINGULAR_FUTURE" to "presfut_sg1",
        "FIRST_PERSON_PLURAL_FUTURE" to "presfut_pl1",
        "SECOND_PERSON_PLURAL_FUTURE" to "presfut_pl2",
        "THIRD_PERSON_PLURAL_FUTURE" to "presfut_pl3",
        "INSTRUMENTAL_SINGULAR" to "sg_inst",
        "INSTRUMENTAL_PLURAL" to "pl_inst",
        "MASCULINE_INSTRUMENTAL" to "decl_m_inst",
        "PLURAL_INSTRUMENTAL" to "decl_pl_inst",
    )

    private val DISCLOSED_CLASSIFICATION = Regex(
        "(?i)\\b(?:film agreement line|book agreement line|jeans agreement line|twenty-one line|hundred-one line|" +
            "two-hundred-fifteen line|shot-glass line|forty-one-year line|sixty-nine-word line|one-boy line|" +
            "two-girl line|five-girl line|not-speaking line|not-student line|not-coming line|absence form|" +
            "existence form|particle form|first-name form|patronymic form|family-name form|alexander diminutive|" +
            "alexei diminutive|dmitri diminutive|please line|thanks line|apology line|name-question line|" +
            "name-answer line|formal-apology line)\\b",
    )
    private val LINE_LABEL_PROMPT = Regex(
        "(?i)which admitted (?:[a-z]+ ){0,4}(?:line|label) is the\\b",
    )
    private val CIRCULAR_RATIONALE = Regex(
        "(?i)^does this (?:answer|row|line)\\b|^does the admitted record show the governed contrast\\??$",
    )

    private fun observableDistinctionErrors(pack: V2CurriculumPack, item: V2CheckItem): List<String> {
        val errors = mutableListOf<String>()
        val assessment = item.assessment ?: return listOf("Item ${item.id} has no explicit target")
        val why = assessment.whyAnswerDemonstratesObjective
        if (LINE_LABEL_PROMPT.containsMatchIn(item.prompt) || DISCLOSED_CLASSIFICATION.containsMatchIn(item.prompt)) {
            errors += "Item ${item.id} prompt discloses its classification"
            errors += "Item ${item.id} tests line-label recall"
        }
        val hosted = listOf(why) + item.distractorRationales + assessment.distractorDifference
        if (hosted.any { CIRCULAR_RATIONALE.containsMatchIn(it.trim()) } || why.trim().endsWith("?")) {
            errors += "Item ${item.id} circular feedback"
        }
        val rationales = item.distractorRationales.filter { it.isNotBlank() }
        val differences = assessment.distractorDifference.filter { it.isNotBlank() }
        if (rationales.size != rationales.toSet().size || differences.size != differences.toSet().size) {
            errors += "Item ${item.id} repeats a generic rationale"
        }
        val originals = assessment.relationParticipantIds.mapNotNull { id ->
            pack.provenanceRecords.firstOrNull { it.itemId == id }?.originalSourceText
        }
        if (!citesFrozenEvidence(why, originals)) {
            errors += "Item ${item.id} answer rationale does not cite the frozen evidence"
        }
        errors += keyedGlossErrors(item)
        return errors
    }

    /**
     * Batch A gloss checks must describe the keyed shown form and answer.
     * A swapped gloss, or a numbered "other gloss" rationale, fails here.
     */
    private fun keyedGlossErrors(item: V2CheckItem): List<String> {
        if (item.objectiveId !in BATCH_A_OBJECTIVES) return emptyList()
        val assessment = item.assessment ?: return listOf("Item ${item.id} explanation does not name the keyed answer")
        val why = assessment.whyAnswerDemonstratesObjective
        val errors = mutableListOf<String>()
        val shown = item.promptLetter.orEmpty()
        if (!namesGloss(why, item.answer) || !namesGloss(item.feedback, item.answer)) {
            errors += "Item ${item.id} explanation does not name the keyed answer"
        }
        if (shown.isNotBlank() && !namesGloss(why, shown)) {
            errors += "Item ${item.id} explanation does not name the shown form"
        }
        item.options.filter { it != item.answer }.forEach { distractor ->
            if (namesGloss(why, distractor) && !namesGloss(item.answer, distractor)) {
                errors += "Item ${item.id} explanation names a different gloss"
            }
        }
        item.options.forEachIndexed { index, option ->
            if (option == item.answer) return@forEachIndexed
            val rationale = item.distractorRationales.getOrElse(index) { "" }
            val difference = assessment.distractorDifference.getOrElse(index) { "" }
            if (!namesGloss(rationale, option) || !namesGloss(difference, option)) {
                errors += "Item ${item.id} distractor rationale does not name its competing gloss"
            }
            if (namesGloss(rationale, item.answer) && !namesGloss(option, item.answer)) {
                errors += "Item ${item.id} distractor rationale names the keyed answer"
            }
            if (namesGloss(difference, item.answer) && !namesGloss(option, item.answer)) {
                errors += "Item ${item.id} distractor rationale names the keyed answer"
            }
        }
        return errors
    }

    private fun namesGloss(host: String, phrase: String): Boolean = phrase.isNotEmpty() && host.contains(phrase)

    private fun citesFrozenEvidence(why: String, originals: List<String>): Boolean {
        if (why.length < 12) return false
        return originals.any { original ->
            original.length >= 12 && original.windowed(12).any { window ->
                window.any { it.isLetter() } && window in why
            }
        }
    }

    private fun explicitTargetErrors(pack: V2CurriculumPack, item: V2CheckItem): List<String> {
        val errors = mutableListOf<String>()
        val assessment = item.assessment ?: return listOf("Item ${item.id} has no explicit target")
        val feature = assessment.testedFeature
        val unitNumber = item.objectiveId.removePrefix("obj-u").substringBefore("-").toIntOrNull()
        if (unitNumber in 21..30 || item.objectiveId in BATCH_A_OBJECTIVES) {
            errors += observableDistinctionErrors(pack, item)
        } else {
            val phrase = TARGET_PHRASES[feature]
            if (feature.isBlank() || phrase == null || !item.prompt.contains(phrase)) {
                errors += "Item ${item.id} has no explicit target"
            }
            if (phrase != null && !assessment.whyAnswerDemonstratesObjective.contains(phrase)) {
                errors += "Item ${item.id} answer rationale is not tied to the target"
            }
        }
        if (item.options.count { it == item.answer } != 1) {
            errors += "Item ${item.id} does not have one correct option"
        }
        if (COLOCATION_PROMPT.containsMatchIn(item.prompt)) {
            errors += "Item ${item.id} source co-location prompt"
        }
        val answerCyrillic = hasCyrillic(item.answer)
        val distractorTexts = item.options.filter { it != item.answer }
        if (responseShapeIncompatible(item)) {
            errors += "Item ${item.id} response type"
        }
        if (
            item.prompt.contains("spelling", ignoreCase = true) &&
            !answerCyrillic &&
            item.options.any { hasCyrillic(it) }
        ) {
            errors += "Item ${item.id} English gloss"
        }
        if (
            item.prompt.contains("imperative", ignoreCase = true) &&
            !item.prompt.contains("singular", ignoreCase = true) &&
            !item.prompt.contains("plural", ignoreCase = true)
        ) {
            errors += "Item ${item.id} unspecified imperative"
        }
        if (!connectedToObjective(pack, item)) {
            errors += "Item ${item.id} is not connected to its objective"
        }
        val participants = assessment.relationParticipantIds.map { id ->
            pack.provenanceRecords.firstOrNull { it.itemId == id }
        }
        val cue = participants.firstOrNull()
        val answerRecord = participants.getOrNull(1)
        val distractorRecords = participants.drop(2).filterNotNull()
        val answerField = answerRecord?.let { openRussianLemma(it) }
        val expectedField = TARGET_FIELDS[feature]
        if (expectedField != null && answerField != null && answerField.second != expectedField) {
            errors += "Item ${item.id} has no explicit target"
        }
        if (expectedField != null && distractorRecords.any { openRussianLemma(it)?.second == expectedField }) {
            errors += "Item ${item.id} distractor is also supported"
        }
        if (answerField != null && answerField.second in PAST_FIELDS) {
            val sameLemmaPast = distractorRecords.any { record ->
                val parsed = openRussianLemma(record)
                parsed != null && parsed.first == answerField.first && parsed.second in PAST_FIELDS
            }
            val named = listOf("feminine", "masculine", "neuter", "plural").any {
                item.prompt.contains(it, ignoreCase = true)
            }
            if (sameLemmaPast && !named) {
                errors += "Item ${item.id} past target"
            }
        }
        if (feature.startsWith("IMPERSONAL") && answerRecord != null && answerRecord.shippedText == item.answer) {
            val field = openRussianLemma(answerRecord)?.second
            if (field in IMPERSONAL_INFLECTION) {
                errors += "Item ${item.id} unrelated inflected form"
            }
        }
        if (feature == "ACCEPTED_SPELLING" && answerRecord != null) {
            val original = answerRecord.originalSourceText
            val at = original.indexOf(item.answer)
            if (at >= 0) {
                val window = original.substring(maxOf(0, at - 40), at)
                if (Regex("(?i)\\bnot\\b").containsMatchIn(window)) {
                    errors += "Item ${item.id} rejected spelling"
                }
            }
        }
        val originals = listOfNotNull(cue?.originalSourceText, answerRecord?.originalSourceText).distinct()
        if (distractorTexts.any { distractor -> originals.any { alternativeGloss(it, item.answer, distractor) } }) {
            errors += "Item ${item.id} distractor is also supported"
        }
        return errors
    }

    private fun alternativeGloss(original: String, answer: String, distractor: String): Boolean {
        if (hasCyrillic(answer) || hasCyrillic(distractor)) return false
        if (answer.isBlank() || distractor.isBlank() || answer == distractor) return false
        val answerAt = original.indexOf(answer)
        val distractorAt = original.indexOf(distractor)
        if (answerAt < 0 || distractorAt < 0) return false
        val between = if (answerAt < distractorAt) {
            original.substring(answerAt + answer.length, distractorAt)
        } else {
            original.substring(distractorAt + distractor.length, answerAt)
        }
        return between.length <= 24 && Regex("(?i)\\bor\\b").containsMatchIn(between)
    }

    private fun connectedToObjective(pack: V2CurriculumPack, item: V2CheckItem): Boolean {
        val objective = pack.units.flatMap { it.objectives }.firstOrNull { it.id == item.objectiveId } ?: return false
        if (objective.kind != "RECOGNITION") return false
        if (item.id in objective.checkItemIds) return true
        val stepIds = objective.practiceStepIds.toSet()
        return pack.units.flatMap { it.sessions }.flatMap { it.steps }.any { step ->
            step.id in stepIds && item.id in step.itemIds
        }
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
            val governedUnit = item.objectiveId.removePrefix("obj-u").substringBefore("-").toIntOrNull()
            if (governedUnit in 6..15 || governedUnit in 21..30 || item.objectiveId in BATCH_A_OBJECTIVES) {
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
        if (unitNumber in 6..15 && example.display.none { it.code in 0x0400..0x04FF }) {
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
            if (record.unitNumber in 26..30 && record.sourceId.startsWith("src-wikipedia-")) {
                if (!record.attribution.contains("Wikipedia:Copyrights oldid 1369466282") ||
                    !record.attribution.contains("CC BY-SA 4.0")
                ) {
                    errors += "Provenance ${record.itemId} Wikipedia license route is missing"
                }
            }
            if (record.unitNumber in 26..30 && record.sourceId.startsWith("src-wikibooks-")) {
                if (!record.attribution.contains("Wikibooks:Copyrights oldid 4622060") ||
                    record.attribution.contains("Wikipedia:Copyrights oldid")
                ) {
                    errors += "Provenance ${record.itemId} Wikibooks license route is missing"
                }
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
            if (record.unitNumber !in 3..30) {
                errors += "Provenance ${record.itemId} is outside the proposed Units 3-30 source intake"
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
            if (record.unitNumber !in 3..30) {
                errors += "Authored English ${record.id} is outside the proposed Units 3-30 source intake"
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

    /**
     * The same content-complete checks `validateReadiness` runs once a unit
     * leaves `NOT_PREPARED`: substantive chain, unavailable experiences,
     * recognition example depth, objective-chain gaps, and source-capability gaps.
     * Authority-variance errors stay out of this list.
     */
    internal fun contentCompletenessErrors(pack: V2CurriculumPack, unit: V2Unit): List<String> {
        val errors = mutableListOf<String>()
        validateSubstantiveContent(pack, unit, errors)
        if (unit.sessions.any { session ->
                session.steps.any { it.kind == "UNAVAILABLE" || !it.unavailableReason.isNullOrBlank() }
            }
        ) {
            errors += "Unit ${unit.number} is not content-complete while a required experience is unavailable"
        }
        if (unit.objectives.filter { it.kind == "RECOGNITION" }.any { it.exampleIds.size < 2 }) {
            errors += "Unit ${unit.number} content-complete claim has a recognition objective with fewer than two examples"
        }
        unit.objectives.forEach { objective ->
            validateObjectiveChain(pack, unit, objective, errors, requireCompleteRecognition = true)
        }
        errors += completionGapErrors(pack, unit)
        return errors.distinct()
    }

    internal data class SourceCapabilityRequirement(
        val objectiveId: String,
        val claim: String,
    )

    internal fun requiredSourceCapabilityBindings(unitNumber: Int): List<SourceCapabilityRequirement> {
        return REQUIRED_SOURCE_CAPABILITIES[unitNumber].orEmpty().map {
            SourceCapabilityRequirement(it.objectiveId, it.claim)
        }
    }

    /** A claim is resolved only when a defect-free capability names that claim. Keyword text is not evidence. */
    internal fun unresolvedRequiredSourceCapabilities(
        pack: V2CurriculumPack,
        unitNumber: Int,
    ): List<SourceCapabilityRequirement> {
        return REQUIRED_SOURCE_CAPABILITIES[unitNumber].orEmpty().filter { requirement ->
            pack.sourceCapabilities.none { capability ->
                capability.unitNumber == unitNumber &&
                    capability.objectiveId == requirement.objectiveId &&
                    capability.supportedClaim == requirement.claim &&
                    capabilityDefects(pack, capability).isEmpty()
            }
        }.map { SourceCapabilityRequirement(it.objectiveId, it.claim) }
    }

    internal fun authorityVarianceApplies(unitNumber: Int): Boolean = unitNumber in UNRESOLVED_VARIANCE_UNITS

    /** This pack installs only Units 1–2, and those installed units must resolve pronunciation audio. */
    internal fun installationRequiresPronunciationAudio(unitNumber: Int): Boolean = unitNumber in 1..2

    /** Installed Unit 2 is the only unit `validateInstalled` requires a handwriting asset for. */
    internal fun installationRequiresHandwritingAsset(unitNumber: Int): Boolean = unitNumber == 2

    internal fun mappedPronunciationAudioResolves(pack: V2CurriculumPack, unit: V2Unit): Boolean =
        pronunciationAudioResolves(pack, unit)

    internal fun verifiedHandwritingAssetResolves(pack: V2CurriculumPack, unit: V2Unit): Boolean =
        handwritingAssetResolves(pack, unit)
}
