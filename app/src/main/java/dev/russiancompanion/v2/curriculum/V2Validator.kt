package dev.russiancompanion.v2.curriculum

import java.io.File
import java.security.MessageDigest

internal object V2Validator {
    fun validate(pack: V2CurriculumPack, repositoryRoot: File? = null): V2Validation {
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
        validateGovernedReadyRussianText(pack, errors)
        validateReview(pack, repositoryRoot, errors)
        validateUnitReviews(pack, repositoryRoot, errors)
        validateSourcesAndRights(pack, errors)
        if (repositoryRoot != null) {
            errors += dev.russiancompanion.v2.notices.packagedNoticeErrors(repositoryRoot, pack)
        }
        pack.units.forEach { unit ->
            if (unit.penguinLesson != unit.number) {
                errors += "Unit ${unit.number} penguinLesson must equal the unit number"
            }
            if (unit.installState !in CLOSED_INSTALL_STATES) {
                errors += "Unit ${unit.number} has unrecognized installState ${unit.installState}"
                return@forEach
            }
            when (unit.installState) {
                "INSTALLED" -> validateInstalled(pack, unit, repositoryRoot, errors)
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
    /** Counts real canonical-unit digest executions, including cache misses. */
    internal val unitDigestExecutionsForTests = java.util.concurrent.atomic.AtomicInteger(0)

    @Volatile
    internal var unitDigestExecutionThreadForTests: Thread? = null

    internal fun unitContentReviewDigest(pack: V2CurriculumPack, unitNumber: Int): String {
        unitDigestExecutionsForTests.incrementAndGet()
        unitDigestExecutionThreadForTests = Thread.currentThread()
        return sha256(canonicalUnitInstructionalDocument(pack, unitNumber))
    }

    internal fun instructionalContentDigest(pack: V2CurriculumPack): String = contentReviewDigest(pack)

    /**
     * SHA-256 of the canonical check item. Attempt rows store this value at
     * creation so a later edit of the item cannot be treated as the same key.
     */
    internal fun checkItemContentDigest(item: V2CheckItem): String = sha256(canonicalCheckItem(item))

    /**
     * Release acceptance for the pack-wide linguistic review and every unit.
     * This is the same decision [validate] records when a chain claims
     * OWNER_ACCEPTED. A reduced review DTO cannot call it.
     */
    internal fun linguisticReleaseAccepted(pack: V2CurriculumPack, repositoryRoot: File? = null): Boolean {
        val packReview = pack.linguisticReview
        if (!reviewReleaseAccepted(
                packReview.status,
                packReview.reviewChainStages,
                packReview.reviewedContentDigest,
                contentReviewDigest(pack),
                packReview.evidence,
                packReview.finalAudit,
                repositoryRoot,
            )
        ) {
            return false
        }
        val packArtifact = packReview.finalAudit?.artifactIdentity
        val byUnit = pack.unitReviews.groupBy { it.unitNumber }
        return (1..30).all { number ->
            val review = byUnit[number]?.singleOrNull() ?: return@all false
            reviewReleaseAccepted(
                review.status,
                review.reviewChainStages,
                review.reviewedContentDigest,
                unitContentReviewDigest(pack, number),
                review.evidence,
                review.finalAudit,
                repositoryRoot,
                number,
                packArtifact,
            )
        }
    }

    private fun sha256(text: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(text.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { byte -> "%02x".format(byte) }
    }

    internal const val CONTENT_REVIEW_DIGEST_SCHEMA = 3

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
                    "gating" to canonBool(objective.gating),
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
        if (item.promptLetterRole != "CUE") {
            fields += "promptLetterRole" to canonStr(item.promptLetterRole)
        }
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

    private fun validateReview(pack: V2CurriculumPack, repositoryRoot: File?, errors: MutableList<String>) {
        val review = pack.linguisticReview
        if (review.status !in CLOSED_REVIEW_STATES) {
            errors += "Unrecognized linguistic review status ${review.status}"
            return
        }
        if (review.status == "APPROVED") {
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
        validateClaimedChain(
            "Pack review",
            review.status,
            review.reviewChainStages,
            review.reviewedContentDigest,
            contentReviewDigest(pack),
            review.evidence,
            review.finalAudit,
            repositoryRoot,
            null,
            null,
            errors,
        )
    }

    private fun validateUnitReviews(pack: V2CurriculumPack, repositoryRoot: File?, errors: MutableList<String>) {
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
            if (review.status == "APPROVED") {
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
            validateClaimedChain(
                "Unit ${review.unitNumber} review",
                review.status,
                review.reviewChainStages,
                review.reviewedContentDigest,
                unitContentReviewDigest(pack, review.unitNumber),
                review.evidence,
                review.finalAudit,
                repositoryRoot,
                review.unitNumber,
                pack.linguisticReview.finalAudit?.artifactIdentity,
                errors,
            )
        }
    }

    private fun validateClaimedChain(
        label: String,
        status: String,
        stages: List<String>,
        reviewedContentDigest: String?,
        currentContentDigest: String,
        evidence: V2PersistedEvidence?,
        finalAudit: V2FinalAuditIdentity?,
        repositoryRoot: File?,
        auditUnitNumber: Int?,
        packArtifactIdentity: String?,
        errors: MutableList<String>,
    ) {
        if (status !in REVIEW_CHAIN) return
        val defects = reviewChainDefects(
            reviewAcceptanceAttempt(
                status,
                stages,
                reviewedContentDigest,
                currentContentDigest,
                evidence,
                finalAudit,
                repositoryRoot,
                auditUnitNumber,
                packArtifactIdentity,
            ),
        )
        defects.forEach { errors += "$label $it" }
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
            if (source.attribution.isBlank()) errors += "Source ${source.id} has no attribution"
            if (dev.russiancompanion.v2.notices.penguinRedistributionClaim(source.title) ||
                dev.russiancompanion.v2.notices.penguinRedistributionClaim(source.attribution)
            ) {
                errors += "Source ${source.id} presents Penguin material as redistributed content"
            }
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

    private fun validateInstalled(pack: V2CurriculumPack, unit: V2Unit, repositoryRoot: File?, errors: MutableList<String>) {
        val installedReview = unitReview(pack, unit.number)
        val legacyApproved = installedReview?.status == "APPROVED"
        val chainAccepted = installedReview != null &&
            reviewReleaseAccepted(
                installedReview.status,
                installedReview.reviewChainStages,
                installedReview.reviewedContentDigest,
                unitContentReviewDigest(pack, unit.number),
                installedReview.evidence,
                installedReview.finalAudit,
                repositoryRoot,
                unit.number,
                pack.linguisticReview.finalAudit?.artifactIdentity,
            )
        if (!legacyApproved && !chainAccepted) {
            errors += "Installed unit ${unit.number} review chain is not OWNER_ACCEPTED"
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
        if (installationRequiresPronunciationAudio(unit.number) && !pronunciationAudioResolves(pack, unit)) {
            errors += "Installed unit ${unit.number} requires a mapped pronunciation audio record"
        }
        if (installationRequiresHandwritingAsset(unit.number) && !handwritingAssetResolves(pack, unit)) {
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
                "Unit 7 is not content-complete without an admitted bounded manner-adverb formation source capability",
            )
        }
        if (unit.number !in 16..30) return emptyList()
        return when (unit.number) {
            16 -> missingCapabilityError(
                pack,
                unit,
                "Unit 16 is not content-complete without an admitted instrumental complement span source capability",
            )
            17 -> unit17SourceBlockers(
                unresolvedRequiredSourceCapabilities(pack, unit.number).map { it.claim }.toSet(),
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
            23 -> unit23SourceGap(pack, unit)
            24 -> missingCapabilityError(
                pack,
                unit,
                "Unit 24 is not content-complete without admitted alternation, negative-pronoun, and scope source capabilities",
            )
            25 -> missingCapabilityError(
                pack,
                unit,
                "Unit 25 is not content-complete without admitted documented-name-form and register source capabilities",
            )
            in 26..30 -> lateUnitSourceGap(pack, unit)
            else -> emptyList()
        }
    }

    private data class RequiredSourceCapability(val objectiveId: String, val claim: String)

    private val BATCH_D_SOURCE_CLAIMS = setOf(
        "adverb-paradigm",
        "bounded-manner-adverb-formation",
        "productive-clock-time",
        "completion-over-period",
        "negation-scope",
        "productive-diminutive",
        "documented-name-form",
    )

    private val BATCH_A_OBJECTIVES = setOf(
        "obj-u16-complements",
        "obj-u17-age",
        "obj-u17-weekdays",
        "obj-u17-months",
        "obj-u17-clock",
        "obj-u17-clock-answers",
        "obj-u17-clock-productive",
        "obj-u17-calendar-date",
        "obj-u7-adverb",
        "obj-u7-adverb-invariance",
        "obj-u7-adverb-pairs",
        "obj-u17-quarter-hour",
        "obj-u19-probability",
        "obj-u18-comparison",
        "obj-u18-relative",
        "obj-u19-conditional",
        "obj-u19-obligation",
        "obj-u20-motion",
    )

    private val BATCH_B_OBJECTIVES = setOf(
        "obj-u21-object-reference",
        "obj-u21-purpose",
        "obj-u22-declension",
        "obj-u22-both-forms",
        "obj-u22-half-forms",
        "obj-u22-collective",
        "obj-u23-ranges",
        "obj-u23-beginning",
        "obj-u23-during-event",
        "obj-u23-duration",
        "obj-u23-elapsed",
        "obj-u24-alternation",
        "obj-u24-stand-ins",
        "obj-u24-concord",
        "obj-u24-placement",
        "obj-u24-word-order-contrast",
        "obj-u25-address",
        "obj-u25-nickname-rule",
        "obj-u26-indefinite",
        "obj-u29-punctuation",
        "obj-u28-imperfective-adverb",
        "obj-u28-perfective-adverb",
    )

    private val REQUIRED_SOURCE_CAPABILITIES = mapOf(
        7 to listOf(
            RequiredSourceCapability("obj-u7-adverb", "adverb-form-gloss"),
            RequiredSourceCapability("obj-u7-adverb-paradigm", "bounded-manner-adverb-formation"),
            RequiredSourceCapability("obj-u7-adverb-invariance", "adverb-invariance"),
            RequiredSourceCapability("obj-u7-adverb-pairs", "adverb-bounded-pairs"),
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
            RequiredSourceCapability("obj-u17-clock-productive", "productive-clock-time"),
            RequiredSourceCapability("obj-u17-quarter-hour", "productive-quarter-hour-time"),
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
            RequiredSourceCapability("obj-u23-completion", "completion-over-period"),
        ),
        24 to listOf(
            RequiredSourceCapability("obj-u24-alternation", "negation-alternation"),
            RequiredSourceCapability("obj-u24-stand-ins", "negative-pronoun"),
            RequiredSourceCapability("obj-u24-placement", "negation-scope"),
        ),
        25 to listOf(
            RequiredSourceCapability("obj-u25-nickname-rule", "diminutive-form-classification"),
            RequiredSourceCapability("obj-u25-documented-name-form", "documented-name-form"),
            RequiredSourceCapability("obj-u25-address", "register"),
        ),
        26 to listOf(
            RequiredSourceCapability("obj-u26-indefinite", "indefinite-pronoun"),
            RequiredSourceCapability("obj-u26-word-order", "word-order"),
            RequiredSourceCapability("obj-u26-postal-address", "postal-address"),
            RequiredSourceCapability("obj-u26-letter-writing", "letter-writing"),
            RequiredSourceCapability("obj-u26-correspondence", "correspondence-work"),
        ),
        27 to listOf(
            RequiredSourceCapability("obj-u27-passive", "passive-participle"),
            RequiredSourceCapability("obj-u27-stress", "participle-stress"),
            RequiredSourceCapability("obj-u27-comprehension", "participle-comprehension"),
        ),
        28 to listOf(
            RequiredSourceCapability("obj-u28-imperfective-adverb", "imperfective-verbal-adverb"),
            RequiredSourceCapability("obj-u28-perfective-adverb", "perfective-verbal-adverb"),
            RequiredSourceCapability("obj-u28-readings", "verbal-adverb-reading"),
        ),
        29 to listOf(
            RequiredSourceCapability("obj-u29-active", "active-participle"),
            RequiredSourceCapability("obj-u29-punctuation", "participle-punctuation"),
            RequiredSourceCapability("obj-u29-short-adjective", "short-form-adjective"),
            RequiredSourceCapability("obj-u29-register", "bookish-register"),
            RequiredSourceCapability("obj-u29-reading", "bookish-reading"),
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
     * A readiness edit cannot advance a unit while one of its required source
     * capabilities is still unresolved. Units 27, 29, and 30 are not named here.
     * Their pedagogical boundaries are activated separately and are not an
     * unresolved source gap.
     */
    internal fun unresolvedPartialUnitAdvancementBlockers(pack: V2CurriculumPack, unit: V2Unit): List<String> {
        val unresolved = unresolvedRequiredSourceCapabilities(pack, unit.number).map { it.claim }.toSet()
        return when (unit.number) {
            7 -> when {
                unresolved.isEmpty() -> emptyList()
                "bounded-manner-adverb-formation" in unresolved ->
                    listOf("Unit 7 is missing a bounded manner-adverb formation source capability")
                else -> listOf("Unit 7 is missing a required source capability")
            }
            17 -> unit17SourceBlockers(unresolved)
            19 -> if ("probability-dolzhen" in unresolved) {
                listOf("Unit 19 is missing a probability source capability")
            } else {
                emptyList()
            }
            23 -> if ("completion-over-period" in unresolved) {
                listOf("Unit 23 is missing a completion-over-period source capability")
            } else {
                emptyList()
            }
            24 -> if ("negation-scope" in unresolved) {
                listOf("Unit 24 is missing a negation-scope source capability")
            } else {
                emptyList()
            }
            25 -> if ("documented-name-form" in unresolved) {
                listOf("Unit 25 is missing a documented-name-form source capability")
            } else {
                emptyList()
            }
            else -> emptyList()
        }
    }

    /**
     * Name only the Unit 17 source capabilities that are still unresolved.
     * A resolved calendar-date claim must not be reported as missing, and a
     * resolved productive clock-time claim must not be reported as missing.
     */
    internal fun unit17SourceBlockers(unresolved: Set<String>): List<String> {
        val missing = listOf("productive-clock-time", "calendar-date").filter { it in unresolved }
        val named = missing.map { claim ->
            if (claim == "productive-clock-time") "productive clock-time" else "calendar-date"
        }
        return when (named.size) {
            0 -> emptyList()
            1 -> listOf("Unit 17 is missing a ${named.single()} source capability")
            else -> listOf("Unit 17 is missing ${named.joinToString(" and ")} source capabilities")
        }
    }

    internal fun unresolvedLateUnitAdvancementBlockers(
        pack: V2CurriculumPack,
        unit: V2Unit,
        unresolvedVarianceUnits: Set<Int> = UNRESOLVED_VARIANCE_UNITS,
    ): List<String> {
        if (unit.number !in 26..30) return emptyList()
        val blockers = mutableListOf<String>()
        blockers += lateUnitSourceGap(pack, unit)
        if (unit.number in unresolvedVarianceUnits) {
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

    /**
     * Duration and completion over a period are separate claims. A resolved
     * duration span must not be reported as the missing completion distinction.
     */
    private fun unit23SourceGap(pack: V2CurriculumPack, unit: V2Unit): List<String> {
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
            1 -> "an admitted ${missing[0]}"
            else -> "admitted ${missing.joinToString(" and ")}"
        }
        val noun = if (missing.size == 1) "source capability" else "source capabilities"
        return listOf("Unit 23 is not content-complete without $listed $noun")
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
        if (capability.sourceId == ContentAuditSources.SOURCE_ID) {
            return correctedCapabilityDefects(pack, capability)
        }
        val errors = mutableListOf<String>()
        if (capability.supportedClaim == "letter-writing" && capability.exactSpan == RemainingSources.LETTER_SPAN) {
            errors += "Source capability ${capability.id} retired universal letter-writing intake requires the exact pending correction"
        }
        if (capability.supportedClaim == "word-order" && capability.sourceHash == "30e7e08a32bd5ca619593ff841bc22fff849a3ce2fc516d1f0cf67ea0c8e5bb6") {
            errors += "Source capability ${capability.id} historical word-order labels require contextual application correction"
        }
        if (capability.supportedClaim == "postal-address" && capability.sourceHash == "546e52bec4a95e4454791c68858a19e6d713d3218d6dde3f43c041fbd3f83a97") {
            errors += "Source capability ${capability.id} retired universal postal-order intake requires the exact contextual correction"
        }
        val binding = REQUIRED_SOURCE_CAPABILITIES[capability.unitNumber].orEmpty().firstOrNull {
            it.objectiveId == capability.objectiveId && it.claim == capability.supportedClaim
        }
        val productiveClassificationRelabel =
            capability.supportedClaim == "productive-diminutive" &&
                capability.objectiveId == "obj-u25-nickname-rule"
        val productiveClockRelabel =
            capability.supportedClaim == "productive-clock-time" && binding == null
        val retiredBroadClaim =
            capability.supportedClaim == "adverb-paradigm" ||
                capability.supportedClaim == "productive-diminutive"
        if ((binding == null || capability.supportedClaim.isBlank() || capability.objectiveId.isBlank()) &&
            !productiveClassificationRelabel &&
            !productiveClockRelabel &&
            !retiredBroadClaim
        ) {
            errors += "Source capability ${capability.id} is attached to the wrong unit or objective"
            return errors
        }
        if (binding == null) {
            errors += "Source capability ${capability.id} is attached to the wrong unit or objective"
        }
        if (capability.sourceId == "src-openrussian-50e210c4" &&
            (capability.supportedClaim in BATCH_D_SOURCE_CLAIMS || capability.unitNumber in 26..30)
        ) {
            errors += "Source capability ${capability.id} a dictionary row does not clear ${capability.supportedClaim}"
        }
        if (
            (capability.supportedClaim in BATCH_D_SOURCE_CLAIMS || capability.unitNumber in 26..30) &&
            (capability.exactSpan.contains("Self-attest") || capability.exactSpan.contains("self-attest"))
        ) {
            errors += "Source capability ${capability.id} book self-attestation does not clear ${capability.supportedClaim}"
        }
        if (capability.supportedClaim == "productive-clock-time" && !admittedProductiveClockTime(capability)) {
            errors += "Source capability ${capability.id} has no independent productive-clock-time admission"
        }
        if (capability.supportedClaim == "adverb-paradigm" && !BatchARemainingSources.admits(capability, BatchARemainingSources.ADMITTED_ADVERB_PARADIGM, "adverb-paradigm")) {
            errors += "Source capability ${capability.id} has no independent adverb-paradigm admission"
        }
        val phase2 = Phase2Sources.admission(capability.supportedClaim)
        if (phase2 != null && !Phase2Sources.matches(capability)) {
            errors += "Source capability ${capability.id} has no independent ${capability.supportedClaim} admission"
        }
        val phase3 = Phase3Sources.admission(capability.supportedClaim)
        if (phase3 != null && !Phase3Sources.matches(capability)) {
            errors += "Source capability ${capability.id} has no independent ${capability.supportedClaim} admission"
        }
        errors += phase3CrossClaimErrors(capability)
        if (capability.supportedClaim == "adverb-invariance" && !BatchARemainingSources.admits(capability, BatchARemainingSources.ADMITTED_ADVERB_INVARIANCE, "adverb-invariance")) {
            errors += "Source capability ${capability.id} has no independent adverb-invariance admission"
        }
        if (capability.supportedClaim == "adverb-bounded-pairs" && !BatchARemainingSources.admits(capability, BatchARemainingSources.ADMITTED_ADVERB_BOUNDED_PAIRS, "adverb-bounded-pairs")) {
            errors += "Source capability ${capability.id} has no independent adverb-bounded-pairs admission"
        }
        if (capability.supportedClaim == "productive-quarter-hour-time" && !BatchARemainingSources.admits(capability, BatchARemainingSources.ADMITTED_QUARTER_HOUR, "productive-quarter-hour-time")) {
            errors += "Source capability ${capability.id} has no independent productive-quarter-hour-time admission"
        }
        if (capability.supportedClaim == "probability-dolzhen" && !BatchARemainingSources.admits(capability, BatchARemainingSources.ADMITTED_PROBABILITY_DOLZHEN, "probability-dolzhen")) {
            errors += "Source capability ${capability.id} has no independent probability-dolzhen admission"
        }
        errors += batchBAdmissionErrors(capability)
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
                retainedWorkLicense(it.licenseId, it.sourceId)
        }
        if (frozen.isEmpty()) {
            errors += "Source capability ${capability.id} revision does not match the frozen record"
            return errors
        }
        val spanHost = frozen.firstOrNull {
            capability.exactSpan == it.originalSourceText &&
                it.unitNumber == capability.unitNumber &&
                it.objectiveId == capability.objectiveId
        } ?: frozen.firstOrNull { capability.exactSpan == it.originalSourceText }
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
        if (capability.supportedClaim == "adverb-paradigm" && capability.exactSpan == BatchARemainingSources.INVARIANCE_SPAN) {
            errors += "Source capability ${capability.id} bounded invariance evidence does not clear adverb-paradigm"
        }
        if (capability.supportedClaim == "adverb-paradigm" &&
            (capability.exactSpan == BatchARemainingSources.INVARIANCE_SPAN || capability.exactSpan == BatchARemainingSources.SUFFIXAL_SPAN)
        ) {
            errors += "Source capability ${capability.id} bounded adverb pairs do not clear adverb-paradigm"
        }
        if (capability.supportedClaim == "productive-clock-time" && capability.exactSpan == ExplicitSourcePairs.clockSpan) {
            errors += "Source capability ${capability.id} bounded clock phrases do not clear productive-clock-time"
        }
        if (capability.supportedClaim == "productive-clock-time" && capability.exactSpan == BatchARemainingSources.QUARTER_SPAN) {
            errors += "Source capability ${capability.id} quarter-hour patterns do not clear productive-clock-time"
        }
        if (capability.supportedClaim == "probability-dolzhen" && capability.exactSpan == BatchARemainingSources.OBLIGATION_EXAMPLE_SPAN) {
            errors += "Source capability ${capability.id} obligation evidence does not clear probability-dolzhen"
        }
        errors += batchBOverreachErrors(capability)
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
            errors += lateClaimContentErrors(capability)
        }
        if (!capabilityHasCheckChain(pack, capability, spanHost)) {
            errors += "Source capability ${capability.id} has no reviewable instruction and check chain"
        }
        if (phase2 != null && Phase2Sources.matches(capability)) {
            errors += phase2ChainDefects(pack, capability, spanHost, phase2)
        }
        if (phase3 != null && Phase3Sources.matches(capability)) {
            errors += phase3ChainDefects(pack, capability, spanHost, phase3)
        }
        if (capability.supportedClaim == "productive-diminutive" &&
            !hasProductiveDiminutiveApplication(pack, capability, spanHost)
        ) {
            errors += "Source capability ${capability.id} has no independent application check for productive-diminutive"
        }
        if (capability.supportedClaim == "productive-diminutive" &&
            exactPairMemorization(pack, capability)
        ) {
            errors += "Source capability ${capability.id} exact-pair memorization does not clear productive-diminutive"
        }
        if (capability.supportedClaim == "productive-clock-time" &&
            !hasProductiveClockApplication(pack, capability, spanHost)
        ) {
            val objective = pack.units.firstOrNull { it.number == capability.unitNumber }
                ?.objectives?.firstOrNull { it.id == capability.objectiveId }
            errors += "Source capability ${capability.id} exact-pair memorization does not clear productive-clock-time"
        }
        if (capability.supportedClaim == "letter-writing" &&
            !hasLetterConventionApplication(pack, capability, spanHost)
        ) {
            val objective = pack.units.firstOrNull { it.number == capability.unitNumber }
                ?.objectives?.firstOrNull { it.id == capability.objectiveId }
            errors += "Source capability ${capability.id} greeting recognition does not clear letter-writing"
        }
        if (capability.supportedClaim == "participle-stress" &&
            !hasParticipleStressDistinction(pack, capability, spanHost)
        ) {
            errors += "Source capability ${capability.id} headword-versus-infinitive selection does not clear participle-stress"
        }
        if (capability.supportedClaim == "dictionary-use" &&
            !hasPracticalDictionaryUse(pack, capability, spanHost)
        ) {
            errors += "Source capability ${capability.id} field-label recall does not clear dictionary-use"
        }
        if (capability.supportedClaim == "postal-address" &&
            !hasCompletePostalOrganization(pack, capability, spanHost)
        ) {
            errors += "Source capability ${capability.id} incomplete address order does not clear postal-address"
        }
        if (capability.supportedClaim == "participle-stress" && capability.exactSpan == RemainingSources.PARTICIPLE_HOST_SPAN) {
            val joined = pack.provenanceRecords
                .filter { it.objectiveId == capability.objectiveId && it.unitNumber == capability.unitNumber }
                .joinToString("\n") { it.shippedText }
            val forms = listOf(
                "\u043e\u0441\u0442\u0430\u0432\u043b\u044f\u0301\u0435\u043c\u044b\u0439",
                "\u043e\u0431\u0441\u0443\u0436\u0434\u0430\u0301\u0435\u043c\u044b\u0439",
                "\u043d\u0430\u043f\u0438\u0301\u0441\u0430\u043d\u043d\u044b\u0439",
                "\u043f\u0440\u043e\u0447\u0438\u0301\u0442\u0430\u043d\u043d\u044b\u0439",
            )
            if (forms.any { form -> form !in joined }) {
                errors += "Source capability ${capability.id} a marked example does not clear participle-stress"
            }
        }
        return errors
    }

    /**
     * The admitted synthesis clears only its own stated applications.
     * A missing chain, a cloned check, or a distractor that names only one side
     * of the completion contrast leaves the claim unresolved.
     */
    private fun phase2ChainDefects(
        pack: V2CurriculumPack,
        capability: V2SourceCapability,
        spanHost: V2ProvenanceRecord,
        admission: Phase2Sources.Admission,
    ): List<String> {
        val errors = mutableListOf<String>()
        val id = capability.id
        val claim = admission.claim
        val synthesis = spanHost.synthesis
        if (synthesis == null || synthesis.objectiveOrClaimBinding != admission.binding) {
            errors += "Source capability $id synthesis binding does not match"
        }
        if (synthesis == null || synthesis.bibliography.isBlank() || synthesis.authorshipDeclaration.isBlank()) {
            errors += "Source capability $id original synthesis evidence is incomplete"
        }
        if (synthesis == null || synthesis.sourceDateOrVersion != Phase2Sources.DATE) {
            errors += "Source capability $id original synthesis date does not match"
        }
        val audit = synthesis?.finalAudit
        if (audit != null && (audit.artifactIdentity.isNotBlank() || audit.disposition.isNotBlank())) {
            errors += "Source capability $id records a final audit before the final audit"
        }
        if (synthesis == null || synthesis.applications.isEmpty()) {
            errors += "Source capability $id has no stated applications"
        } else {
            synthesis.applications.forEach { application ->
                val outside = application.cue !in spanHost.originalSourceText ||
                    application.answer !in spanHost.originalSourceText ||
                    application.distractors.any { it !in spanHost.originalSourceText }
                if (outside) {
                    errors += "Source capability $id stated application is outside the frozen span"
                }
            }
        }
        val unit = pack.units.firstOrNull { it.number == capability.unitNumber }
        val objective = unit?.objectives?.firstOrNull { it.id == capability.objectiveId }
        val instruction = objective?.instructionStepIds.orEmpty().mapNotNull { pack.step(it)?.second }
        if (instruction.isEmpty() || instruction.any { it.body.isBlank() }) {
            errors += "Source capability $id missing instruction"
        }
        val practiceItems = objective?.practiceStepIds.orEmpty().flatMap { stepId ->
            pack.step(stepId)?.second?.itemIds.orEmpty()
        }.mapNotNull { pack.item(it) }
        if (objective == null || objective.practiceStepIds.isEmpty() || practiceItems.isEmpty()) {
            errors += "Source capability $id missing guided practice"
        }
        val checks = objective?.checkItemIds.orEmpty().mapNotNull { pack.item(it) }
        val independent = checks.filter { it.assessment?.independent == true }
        if (independent.isEmpty()) {
            errors += "Source capability $id missing independent check"
        }
        val features = independent.mapNotNull { it.assessment?.testedFeature }.toSet()
        if (!features.containsAll(admission.independentFeatures)) {
            errors += "Source capability $id has no independent application check"
        }
        if (independent.any { it.assessment?.relationKind != "STATED_APPLICATION" }) {
            errors += "Source capability $id independent check is not a stated application"
        }
        if (checks.any { it.feedback.isBlank() } || practiceItems.any { it.feedback.isBlank() }) {
            errors += "Source capability $id missing feedback"
        }
        val remediation = objective?.remediationStepIds.orEmpty().mapNotNull { pack.step(it)?.second }
        val shipped = pack.provenanceRecords
            .filter { it.objectiveId == capability.objectiveId }
            .map { it.shippedText }
        if (remediation.isEmpty() || remediation.any { step ->
                shipped.none { span -> span.length >= 8 && span in step.body }
            }
        ) {
            errors += "Source capability $id missing remediation"
        }
        val assessed = (checks + practiceItems).distinctBy { it.id }
        fun duplicate(values: List<String>): Boolean = values.size != values.toSet().size
        val relationships = assessed.map { it.promptLetter.orEmpty() to it.answer }
        if (
            duplicate(assessed.map { it.prompt }) ||
            duplicate(assessed.map { it.promptLetter.orEmpty() }) ||
            duplicate(assessed.map { it.answer }) ||
            relationships.size != relationships.toSet().size
        ) {
            errors += "Source capability $id duplicate assessment checks do not clear $claim"
        }
        if (claim == "completion-over-period") {
            val unexplained = independent.any { item ->
                item.distractorRationales.filter { it.isNotBlank() }.any { rationale ->
                    !rationale.contains("accusative") ||
                        (!rationale.contains("imperfective") && !rationale.contains("perfective"))
                }
            }
            if (unexplained) {
                errors += "Source capability $id distractor does not explain aspect and construction"
            }
        }
        if (claim == "documented-name-form") {
            errors += documentedNameFormCoherenceErrors(pack, id, synthesis, checks + practiceItems)
        }
        if (independent.any { item ->
                SOURCE_LAYOUT_RECALL.containsMatchIn(item.prompt) ||
                    SOURCE_SET_RECALL.containsMatchIn(item.prompt) ||
                    LINE_LABEL_PROMPT.containsMatchIn(item.prompt)
            }
        ) {
            errors += "Source capability $id label or source-layout recall does not clear $claim"
        }
        return errors
    }

    /**
     * A paired claim or a resolved neighbor cannot be relabeled onto one of the six claims.
     * These checks run before span-host alignment so a mismatched objective still names the neighbor.
     */
    private fun phase3CrossClaimErrors(capability: V2SourceCapability): List<String> {
        if (Phase3Sources.matches(capability)) return emptyList()
        val id = capability.id
        val span = capability.exactSpan
        val errors = mutableListOf<String>()
        if (capability.supportedClaim == "postal-address" && span == RemainingSources.LETTER_SPAN) {
            errors += "Source capability $id letter-writing evidence does not clear postal-address"
        }
        if (capability.supportedClaim == "postal-address" && span == RemainingSources.CORRESPONDENCE_SPAN) {
            errors += "Source capability $id correspondence-work evidence does not clear postal-address"
        }
        if (capability.supportedClaim == "dictionary-use" && span == RemainingSources.CAPSTONE_SPAN) {
            errors += "Source capability $id capstone-reading evidence does not clear dictionary-use"
        }
        if (capability.supportedClaim == "participle-stress" && capability.sourceRecordId == Phase3Sources.COMPREHENSION.recordId) {
            errors += "Source capability $id participle-comprehension evidence does not clear participle-stress"
        }
        if (capability.supportedClaim == "participle-comprehension" && capability.sourceRecordId == Phase3Sources.STRESS.recordId) {
            errors += "Source capability $id participle-stress evidence does not clear participle-comprehension"
        }
        if (capability.supportedClaim == "bookish-register" && capability.sourceRecordId == Phase3Sources.READING.recordId) {
            errors += "Source capability $id bookish-reading evidence does not clear bookish-register"
        }
        if (capability.supportedClaim == "bookish-reading" && capability.sourceRecordId == Phase3Sources.REGISTER.recordId) {
            errors += "Source capability $id bookish-register evidence does not clear bookish-reading"
        }
        return errors
    }

    /**
     * The Phase 3 synthesis clears only its own stated applications.
     * A paired claim, a cloned check, or generic feedback leaves the claim unresolved.
     */
    private fun phase3ChainDefects(
        pack: V2CurriculumPack,
        capability: V2SourceCapability,
        spanHost: V2ProvenanceRecord,
        admission: Phase3Sources.Admission,
    ): List<String> {
        val errors = mutableListOf<String>()
        val id = capability.id
        val claim = admission.claim
        val synthesis = spanHost.synthesis
        if (!OriginalApplicationTuplePins.matchesIfPinned(spanHost)) {
            errors += "Source capability $id immutable application tuple identity does not match"
        }
        if (synthesis == null || synthesis.objectiveOrClaimBinding != admission.binding) {
            errors += "Source capability $id synthesis binding does not match"
        }
        if (synthesis == null || synthesis.bibliography.isBlank() || synthesis.authorshipDeclaration.isBlank()) {
            errors += "Source capability $id original synthesis evidence is incomplete"
        }
        if (synthesis == null || synthesis.sourceDateOrVersion != Phase3Sources.DATE) {
            errors += "Source capability $id original synthesis date does not match"
        }
        val audit = synthesis?.finalAudit
        if (audit != null && (audit.artifactIdentity.isNotBlank() || audit.disposition.isNotBlank())) {
            errors += "Source capability $id records a final audit before the final audit"
        }
        if (synthesis == null || synthesis.applications.isEmpty()) {
            errors += "Source capability $id has no stated applications"
        } else {
            synthesis.applications.forEach { application ->
                val outside = application.cue !in spanHost.originalSourceText ||
                    application.answer !in spanHost.originalSourceText ||
                    application.distractors.any { it !in spanHost.originalSourceText }
                if (outside) {
                    errors += "Source capability $id stated application is outside the frozen span"
                }
            }
        }
        val unit = pack.units.firstOrNull { it.number == capability.unitNumber }
        val objective = unit?.objectives?.firstOrNull { it.id == capability.objectiveId }
        val instruction = objective?.instructionStepIds.orEmpty().mapNotNull { pack.step(it)?.second }
        if (instruction.isEmpty() || instruction.any { it.body.isBlank() }) {
            errors += "Source capability $id missing instruction"
        }
        val practiceItems = objective?.practiceStepIds.orEmpty().flatMap { stepId ->
            pack.step(stepId)?.second?.itemIds.orEmpty()
        }.mapNotNull { pack.item(it) }
        if (objective == null || objective.practiceStepIds.isEmpty() || practiceItems.isEmpty()) {
            errors += "Source capability $id missing guided practice"
        }
        val checks = objective?.checkItemIds.orEmpty().mapNotNull { pack.item(it) }
        val independent = checks.filter { it.assessment?.independent == true }
        if (independent.isEmpty()) {
            errors += "Source capability $id missing independent check"
        }
        val features = independent.mapNotNull { it.assessment?.testedFeature }.toSet()
        if (!features.containsAll(admission.independentFeatures)) {
            errors += "Source capability $id has no independent application check"
        }
        if (independent.any { it.assessment?.relationKind != "STATED_APPLICATION" }) {
            errors += "Source capability $id independent check is not a stated application"
        }
        if (checks.any { it.feedback.isBlank() } || practiceItems.any { it.feedback.isBlank() }) {
            errors += "Source capability $id missing feedback"
        }
        val assessed = (checks + practiceItems).distinctBy { it.id }
        val generic = assessed.any { item ->
            !item.feedback.contains(item.answer) ||
                item.options.indices.any { index ->
                    val option = item.options[index]
                    val rationale = item.distractorRationales.getOrElse(index) { "" }
                    option != item.answer && (rationale.isBlank() || !rationale.contains(option) || rationale == "The other option is different.")
                }
        }
        if (generic) {
            errors += "Source capability $id generic feedback does not clear $claim"
        }
        val remediation = objective?.remediationStepIds.orEmpty().mapNotNull { pack.step(it)?.second }
        val shipped = pack.provenanceRecords
            .filter { it.objectiveId == capability.objectiveId }
            .map { it.shippedText }
        if (remediation.isEmpty() || remediation.any { step ->
                shipped.none { span -> span.length >= 8 && span in step.body }
            }
        ) {
            errors += "Source capability $id missing remediation"
        }
        fun duplicate(values: List<String>): Boolean = values.size != values.toSet().size
        val relationships = assessed.map { it.promptLetter.orEmpty() to it.answer }
        if (
            duplicate(assessed.map { it.prompt }) ||
            duplicate(assessed.map { it.promptLetter.orEmpty() }) ||
            duplicate(assessed.map { it.answer }) ||
            relationships.size != relationships.toSet().size
        ) {
            errors += "Source capability $id duplicate assessment checks do not clear $claim"
        }
        if (independent.any { item ->
                SOURCE_LAYOUT_RECALL.containsMatchIn(item.prompt) ||
                    SOURCE_SET_RECALL.containsMatchIn(item.prompt) ||
                    LINE_LABEL_PROMPT.containsMatchIn(item.prompt)
            }
        ) {
            errors += "Source capability $id label or source-layout recall does not clear $claim"
        }
        return errors
    }

    /**
     * The admitted nickname record keeps one form dismissive.
     * Full-name, short-form, and dismissive readings cannot be swapped.
     */
    private fun documentedNameFormCoherenceErrors(
        pack: V2CurriculumPack,
        id: String,
        synthesis: V2OriginalSynthesisEvidence?,
        assessed: List<V2CheckItem>,
    ): List<String> {
        val errors = mutableListOf<String>()
        val governedDismissive = pack.provenanceRecords.any {
            it.itemId == "prov-ex-u25-nickname-rule-2-en" &&
                it.unitNumber == 25 &&
                it.sourceId != Phase2Sources.SOURCE_ID &&
                it.shippedText.isNotBlank()
        }
        val applications = synthesis?.applications.orEmpty()
        fun application(cue: String) = applications.firstOrNull { it.cue == cue }
        fun item(cue: String) = assessed.firstOrNull { it.promptLetter == cue }
        val required = mapOf(
            "\u041c\u0430\u0440\u044e\u043d\u044f" to "full name",
            "\u041c\u0430\u0448\u0435\u043d\u044c\u043a\u0430" to "short form",
            "\u0421\u0430\u0448\u043a\u0430" to "dismissive",
        )
        if (governedDismissive) {
            required.forEach { (cue, anchor) ->
                val reading = item(cue)
                val stated = application(cue)
                val answer = reading?.answer.orEmpty()
                if (stated == null || reading == null || !answer.contains(anchor) || stated.answer != answer) {
                    errors += "Source capability $id conflicting governed descriptions for $cue"
                }
                if (answer == "documented familiar short-form coloring" || stated?.answer == "documented familiar short-form coloring") {
                    errors += "Source capability $id familiar short-form coloring does not preserve the dismissive reading of \u0421\u0430\u0448\u043a\u0430"
                }
                if (reading != null && !reading.feedback.contains(answer)) {
                    errors += "Source capability $id feedback does not agree with the keyed answer"
                }
                if (reading != null && stated != null) {
                    reading.options.forEachIndexed { index, option ->
                        if (option == answer) return@forEachIndexed
                        val rationale = reading.distractorRationales.getOrElse(index) { "" }
                        if (!rationale.contains(option) || stated.distractors.none { it == option }) {
                            errors += "Source capability $id distractor rationale does not agree with the competing application"
                        }
                    }
                }
            }
            val sashka = item("\u0421\u0430\u0448\u043a\u0430")?.answer
            val others = listOfNotNull(item("\u041c\u0430\u0440\u044e\u043d\u044f")?.answer, item("\u041c\u0430\u0448\u0435\u043d\u044c\u043a\u0430")?.answer)
            if (sashka != null && sashka in others) {
                errors += "Source capability $id swaps the \u0421\u0430\u0448\u043a\u0430 interpretation"
            }
        }
        val rejected = item("an unstudied personal name")
        val rejectedApplication = application("an unstudied personal name")
        if (
            rejected == null ||
            rejectedApplication == null ||
            rejected.answer != rejectedApplication.answer ||
            !rejected.answer.contains("names dictionary") ||
            rejectedApplication.distractors.none { it.contains("mechanically") }
        ) {
            errors += "Source capability $id arbitrary name-form generation is not rejected"
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
        "src-wikipedia-russian-grammar-1369617627|1369617627|grammar:imperfective ones are usually present, while perfective ones can only be past (s|478f332bf84bea14a7c5453564225d6a436335132b652ef5ec9162719cf017ad",
        "src-wikipedia-russian-grammar-1369617627|1369617627|grammar:usually present, while perfective ones can only be past (since they denote actio|831e0bb025f3bd4e591be111760c5fc3eaa5d138ff761fa2f21688304bf2b102",
        "src-wikipedia-russian-grammar-1369617627|1369617627|grammar:The active past participle is used in order to indicate actions that happened in|dee66aeeccb1cff104490a5dd0378a8f9f44bf6570a07d2fa906224785b1e9b8",
        "src-wikipedia-russian-grammar-1369617627|1369617627|grammar:such as {{wikt-lang|ru|\u0431\u0435\u0436}} ('beige', non-adapted form of {{wikt-lang|ru|\u0431\u0435\u0301\u0436\u0435\u0432|0b5b1bad657a8c901ba9f471ff7adff3a945be6f281aa760f58c59fd0f8c78e8",
        "src-wikipedia-russian-alphabet-1371795481|1371795481|alphabet:! Name\n! Old Church Slavonic word-name\n! [[International Phonetic Alphabet|IPA]]|0ebe7c0ee3c60d5e931fe34660098e88ee1b955585bddb9f03e2388c00dfc526",
        "src-ruwiki-morfologiya-148939075|148939075|morfologiya:\u0420\u044f\u0434 \u0441\u0443\u0449\u0435\u0441\u0442\u0432\u0438\u0442\u0435\u043b\u044c\u043d\u044b\u0445 \u044f\u0432\u043b\u044f\u044e\u0442\u0441\u044f \u043d\u0435\u0441\u043a\u043b\u043e\u043d\u044f\u0435\u043c\u044b\u043c: \u043c\u043d\u043e\u0433\u0438\u0435 \u0441\u043b\u043e\u0432\u0430 \u0438\u043d\u043e\u044f\u0437\u044b\u0447\u043d\u043e\u0433\u043e \u043f\u0440\u043e\u0438\u0441\u0445\u043e\u0436\u0434\u0435\u043d\u0438|6e73bc5bc5e94fb4a6497a7c94f0ec204a284bb0dc34295bb4ad57998597c5ee",
        "src-ruwiki-morfologiya-148939075|148939075|morfologiya:\u041f\u043e\u0432\u0435\u043b\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0435 \u043d\u0430\u043a\u043b\u043e\u043d\u0435\u043d\u0438\u0435 \u043c\u043e\u0436\u0435\u0442 \u0431\u044b\u0442\u044c \u0432\u044b\u0440\u0430\u0436\u0435\u043d\u043e \u0442\u0430\u043a\u0436\u0435 \u043f\u0440\u0438 \u043f\u043e\u043c\u043e\u0449\u0438 \u0447\u0430\u0441\u0442\u0438\u0446 ''\u043f\u0443\u0441\u0442\u044c (\u043f\u0443|d3148bc360a2b6620f80ae154c5f4d84b822ce065cd17343558f366c4d3070fd",
        "src-ruwiki-neopredelennoe-mestoimenie-149963314|149963314|neopredelennoe:==== \u0421\u0435\u0440\u0438\u044f \u043d\u0430 ''-\u0442\u043e'' ====\n\u041c\u0435\u0441\u0442\u043e\u0438\u043c\u0435\u043d\u0438\u044f \u0434\u0430\u043d\u043d\u043e\u0433\u043e \u0442\u0438\u043f\u0430 \u0432\u044b\u0440\u0430\u0436\u0430\u044e\u0442 \u0440\u0435\u0444\u0435\u0440\u0435\u043d\u0442\u043d\u043e\u0441\u0442\u044c \u0438 \u043d\u0435\u0438|2363c34b89dbc172efc484777d248e3b94438c836b3847203f118b1beecb273b",
        "src-ruwiki-prichastie-153078996|153078996|prichastie:\u0412 \u0440\u0443\u0441\u0441\u043a\u043e\u043c \u044f\u0437\u044b\u043a\u0435 \u043f\u0440\u0438\u0447\u0430\u0441\u0442\u043d\u044b\u0439 \u043e\u0431\u043e\u0440\u043e\u0442 \u0447\u0430\u0441\u0442\u043e \u0432\u044b\u0434\u0435\u043b\u044f\u0435\u0442\u0441\u044f [[\u0417\u0430\u043f\u044f\u0442\u0430\u044f|\u0437\u0430\u043f\u044f\u0442\u044b\u043c\u0438]]. \u0415\u0441\u043b\u0438 \u043f\u0440|821d77bb38feb19b79078e8af3f507f2fcaa4d7e6735f4c1d10a2475a73cbc9a",
        lateIdentityKey(RemainingSources.LETTER_SOURCE, RemainingSources.LETTER_REVISION, RemainingSources.LETTER_PAGE, RemainingSources.LETTER_SPAN),
        lateIdentityKey(RemainingSources.LETTER_SOURCE, RemainingSources.LETTER_REVISION, RemainingSources.LETTER_PAGE, RemainingSources.CORRESPONDENCE_SPAN),
        lateIdentityKey(RemainingSources.PARTICIPLE_SOURCE, RemainingSources.PARTICIPLE_REVISION, RemainingSources.PARTICIPLE_PAGE, RemainingSources.PARTICIPLE_HOST_SPAN),
        lateIdentityKey(RemainingSources.VECHERA_SOURCE, RemainingSources.VECHERA_REVISION, RemainingSources.VECHERA_PAGE, RemainingSources.READING_SPAN),
        lateIdentityKey(RemainingSources.GUIDE_SOURCE, RemainingSources.GUIDE_REVISION, RemainingSources.GUIDE_PAGE, RemainingSources.DICTIONARY_SPAN),
        lateIdentityKey(RemainingSources.READING_SOURCE, RemainingSources.READING_REVISION, RemainingSources.READING_PAGE, RemainingSources.CAPSTONE_SPAN),
    )

    private fun lateIdentityKey(sourceId: String, revision: String, page: String, span: String): String {
        return listOf(sourceId, revision, RemainingSources.recordId(page, span), RemainingSources.sha256(span)).joinToString("|")
    }

    /**
     * The abbreviation span must state both groups and the consonant-final pair.
     * Glossed examples, address marks, and a letter-only list do not.
     */
    private fun clearsAbbreviation(span: String): Boolean {
        val letterGroup = "\u0431\u0443\u043a\u0432\u0435\u043d\u043d\u044b\u0435 \u0430\u0431\u0431\u0440\u0435\u0432\u0438\u0430\u0442\u0443\u0440\u044b \u0438 \u0437\u0432\u0443\u043a\u043e\u0432\u044b\u0435 \u0430\u0431\u0431\u0440\u0435\u0432\u0438\u0430\u0442\u0443\u0440\u044b \u043d\u0430 \u0433\u043b\u0430\u0441\u043d\u0443\u044e"
        val consonantGroup = "\u0437\u0432\u0443\u043a\u043e\u0432\u044b\u0435 \u0430\u0431\u0431\u0440\u0435\u0432\u0438\u0430\u0442\u0443\u0440\u044b \u043d\u0430 \u0441\u043e\u0433\u043b\u0430\u0441\u043d\u0443\u044e"
        val pair = "\u0432 \u041c\u0418\u0414 \u0438 \u0432 \u041c\u0418\u0414\u0435"
        return span.contains(letterGroup) && span.contains(consonantGroup) && span.contains(pair)
    }

    /**
     * The particle span must give the command examples and the supposition statement.
     * A class list that only names the words does not.
     */
    /**
     * The indefinite span must state the three series meanings, a form from each,
     * and the overlap of the to-series with the nibud-series meaning.
     * A bare inventory of forms does not.
     */
    private fun clearsIndefinitePronoun(span: String): Boolean {
        return span.contains("\u0440\u0435\u0444\u0435\u0440\u0435\u043d\u0442\u043d\u043e\u0441\u0442\u044c \u0438 \u043d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u043e\u0441\u0442\u044c \u0433\u043e\u0432\u043e\u0440\u044f\u0449\u0435\u043c\u0443") &&
            span.contains("\u044f\u0432\u043b\u044f\u044e\u0442\u0441\u044f \u043d\u0435\u0440\u0435\u0444\u0435\u0440\u0435\u043d\u0442\u043d\u044b\u043c\u0438") &&
            span.contains("\u0440\u0435\u0444\u0435\u0440\u0435\u043d\u0442\u043d\u044b\u0439 \u0434\u0435\u043d\u043e\u0442\u0430\u0442\u0438\u0432\u043d\u044b\u0439 \u0441\u0442\u0430\u0442\u0443\u0441 \u0441\u043b\u0430\u0431\u043e\u0439 \u043e\u043f\u0440\u0435\u0434\u0435\u043b\u0451\u043d\u043d\u043e\u0441\u0442\u0438") &&
            span.contains("\u0412 \u0434\u043e\u043c\u0435 \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u0442\u0435\u043b\u044c\u043d\u043e \u043a\u0442\u043e-\u0442\u043e \u0431\u044b\u043b") &&
            span.contains("\u043a\u0443\u0434\u0430-\u043d\u0438\u0431\u0443\u0434\u044c") &&
            span.contains("\u043a\u043e\u0435-\u043a\u0430\u043a\u0438\u0435") &&
            span.contains("\u043c\u043e\u0436\u0435\u0442 \u0443\u043f\u043e\u0442\u0440\u0435\u0431\u043b\u044f\u0442\u044c\u0441\u044f \u0442\u0430\u043a\u0436\u0435 \u0438 \u0432 \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0438")
    }

    /**
     * The punctuation span must state the after-noun commas, the before-noun
     * default, both examples, the end-of-sentence comma, and the two exceptions.
     * The rule says the phrase is often set off. One example does not make it universal.
     */
    private fun clearsParticiplePunctuation(span: String): Boolean {
        return span.contains("\u0447\u0430\u0441\u0442\u043e \u0432\u044b\u0434\u0435\u043b\u044f\u0435\u0442\u0441\u044f") &&
            span.contains("\u0432\u044b\u0434\u0435\u043b\u044f\u0435\u0442\u0441\u044f \u0437\u0430\u043f\u044f\u0442\u044b\u043c\u0438 \u0441 \u0434\u0432\u0443\u0445 \u0441\u0442\u043e\u0440\u043e\u043d") &&
            span.contains("\u0437\u0430\u043f\u044f\u0442\u044b\u0435 \u043d\u0435 \u0441\u0442\u0430\u0432\u044f\u0442\u0441\u044f") &&
            span.contains("\u043a\u0440\u043e\u043c\u0435 \u0442\u0435\u0445 \u0441\u043b\u0443\u0447\u0430\u0435\u0432") &&
            span.contains("\u043b\u0438\u0447\u043d\u044b\u043c \u043c\u0435\u0441\u0442\u043e\u0438\u043c\u0435\u043d\u0438\u0435\u043c") &&
            span.contains("\u043e\u0431\u043e\u0437\u043d\u0430\u0447\u0430\u0435\u0442 \u043f\u0440\u0438\u0447\u0438\u043d\u0443") &&
            span.contains("\u0437\u0430\u043f\u044f\u0442\u0430\u044f \u0441\u0442\u0430\u0432\u0438\u0442\u0441\u044f \u0442\u043e\u043b\u044c\u043a\u043e \u043f\u0435\u0440\u0435\u0434 \u043f\u0440\u0438\u0447\u0430\u0441\u0442\u043d\u044b\u043c \u043e\u0431\u043e\u0440\u043e\u0442\u043e\u043c") &&
            span.contains("\u041f\u0440\u043e\u0433\u0440\u0430\u043c\u043c\u0430, \u043d\u0430\u043f\u0438\u0441\u0430\u043d\u043d\u0430\u044f \u0432 \u0441\u043f\u0435\u0448\u043a\u0435, \u0432\u044b\u043f\u043e\u043b\u043d\u0438\u043b\u0430 \u043d\u0435\u0434\u043e\u043f\u0443\u0441\u0442\u0438\u043c\u0443\u044e \u043e\u043f\u0435\u0440\u0430\u0446\u0438\u044e.") &&
            span.contains("\u041d\u0430\u043f\u0438\u0441\u0430\u043d\u043d\u0430\u044f \u0432 \u0441\u043f\u0435\u0448\u043a\u0435 \u043f\u0440\u043e\u0433\u0440\u0430\u043c\u043c\u0430 \u0432\u044b\u043f\u043e\u043b\u043d\u0438\u043b\u0430 \u043d\u0435\u0434\u043e\u043f\u0443\u0441\u0442\u0438\u043c\u0443\u044e \u043e\u043f\u0435\u0440\u0430\u0446\u0438\u044e.")
    }

    private fun clearsParticle(span: String): Boolean {
        return span.contains("\u043f\u0440\u0438 \u043f\u043e\u043c\u043e\u0449\u0438 \u0447\u0430\u0441\u0442\u0438\u0446") &&
            span.contains("\u041f\u0443\u0441\u0442\u044c \u043e\u043d \u0447\u0438\u0442\u0430\u0435\u0442") &&
            span.contains("\u0414\u0430 \u0431\u0443\u0434\u0435\u0442 \u043c\u0438\u0440") &&
            span.contains("\u0447\u0438\u0442\u0430\u043b \u0431\u044b") &&
            span.contains("\u043f\u0440\u0435\u0434\u043f\u043e\u043b\u043e\u0436\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c \u0438\u043b\u0438 \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u044c")
    }

    /**
     * Unresolved Units 26-30 claims stay closed. A neighboring span, a keyword,
     * or a bounded example does not become the missing rule or reading.
     */
    private fun lateClaimContentErrors(capability: V2SourceCapability): List<String> {
        if (Phase3Sources.matches(capability)) return emptyList()
        val span = capability.exactSpan
        val id = capability.id
        return when (capability.supportedClaim) {
            "abbreviation" -> if (clearsAbbreviation(span)) {
                emptyList()
            } else {
                listOf("Source capability $id does not state the abbreviation contrast")
            }
            "particle" -> if (clearsParticle(span)) {
                emptyList()
            } else {
                listOf("Source capability $id does not state the particle contrast")
            }
            "indefinite-pronoun" -> if (clearsIndefinitePronoun(span)) {
                emptyList()
            } else {
                listOf("Source capability $id an indefinite inventory does not clear indefinite-pronoun")
            }
            "postal-address" -> listOf("Source capability $id address marks do not clear postal-address")
            "letter-writing" -> if (span == RemainingSources.LETTER_SPAN && clearsLetterWriting(span)) {
                emptyList()
            } else {
                listOf("Source capability $id does not state letter-writing")
            }
            "participle-stress" -> if (span == RemainingSources.PARTICIPLE_HOST_SPAN && !span.contains(".ogg")) {
                emptyList()
            } else {
                listOf("Source capability $id a marked example does not clear participle-stress")
            }
            "participle-punctuation" -> if (clearsParticiplePunctuation(span)) {
                emptyList()
            } else {
                listOf("Source capability $id does not state participle-punctuation")
            }
            "bookish-register" -> listOf("Source capability $id the adverbial bookish note does not clear bookish-register")
            "dictionary-use" -> if (span == RemainingSources.DICTIONARY_SPAN && !span.contains(".ogg") && !span.contains("audio")) {
                emptyList()
            } else {
                listOf("Source capability $id a dictionary heading does not clear dictionary-use")
            }
            "capstone-reading" -> if (span == RemainingSources.CAPSTONE_SPAN && !span.contains(".ogg")) {
                emptyList()
            } else {
                listOf("Source capability $id does not state capstone-reading")
            }
            "correspondence-work" -> if (admitsLateExperience(capability, ADMITTED_CORRESPONDENCE_WORK, "correspondence-work")) {
                emptyList()
            } else {
                listOf("Source capability $id has no independent correspondence-work admission")
            }
            "participle-comprehension" -> if (admitsLateExperience(capability, ADMITTED_PARTICIPLE_COMPREHENSION, "participle-comprehension")) {
                emptyList()
            } else {
                listOf("Source capability $id has no independent participle-comprehension admission")
            }
            "verbal-adverb-reading" -> if (admitsLateExperience(capability, ADMITTED_VERBAL_ADVERB_READING, "verbal-adverb-reading")) {
                emptyList()
            } else {
                listOf("Source capability $id bounded verbal-adverb examples do not clear verbal-adverb-reading")
            }
            "bookish-reading" -> if (admitsLateExperience(capability, ADMITTED_BOOKISH_READING, "bookish-reading")) {
                emptyList()
            } else {
                listOf("Source capability $id has no independent bookish-reading admission")
            }
            else -> emptyList()
        }
    }

    /**
     * Empty until a frozen passage supplies the whole instructional chain.
     * Membership requires the immutable source identity, the governed claim,
     * and the objective. A keyword, a bounded example, or a neighboring span does not join.
     */
    internal data class LateExperienceAdmission(
        val sourceId: String,
        val sourceRevision: String,
        val sourceRecordId: String,
        val sourceHash: String,
        val exactSpan: String,
        val unitNumber: Int,
        val objectiveId: String,
        val governedClaim: String,
    )

    internal val ADMITTED_CORRESPONDENCE_WORK: Set<LateExperienceAdmission> = setOf(
        lateExperience(
            RemainingSources.LETTER_SOURCE,
            RemainingSources.LETTER_REVISION,
            RemainingSources.LETTER_PAGE,
            RemainingSources.CORRESPONDENCE_SPAN,
            26,
            "obj-u26-correspondence",
            "correspondence-work",
        ),
    )
    internal val ADMITTED_PARTICIPLE_COMPREHENSION: Set<LateExperienceAdmission> = emptySet()
    internal val ADMITTED_VERBAL_ADVERB_READING: Set<LateExperienceAdmission> = setOf(
        lateExperience(
            RemainingSources.VECHERA_SOURCE,
            RemainingSources.VECHERA_REVISION,
            RemainingSources.VECHERA_PAGE,
            RemainingSources.READING_SPAN,
            28,
            "obj-u28-readings",
            "verbal-adverb-reading",
        ),
    )

    private fun lateExperience(
        sourceId: String,
        revision: String,
        page: String,
        span: String,
        unitNumber: Int,
        objectiveId: String,
        claim: String,
    ): LateExperienceAdmission {
        return LateExperienceAdmission(
            sourceId = sourceId,
            sourceRevision = revision,
            sourceRecordId = RemainingSources.recordId(page, span),
            sourceHash = RemainingSources.sha256(span),
            exactSpan = span,
            unitNumber = unitNumber,
            objectiveId = objectiveId,
            governedClaim = claim,
        )
    }

    private fun clearsLetterWriting(span: String): Boolean {
        if (span.contains("please me informed")) return false
        if (span.contains("\u0414\u043e\u0440\u043e\u0433\u043e\u0439")) return false
        if (span.contains("\u0421 \u043b\u044e\u0431\u043e\u0432\u044c\u044e")) return false
        return span.contains("They have the same basic elements") &&
            span.contains("Closing and signature") &&
            span.contains("Respected Ivan Ivanovich")
    }
    internal val ADMITTED_BOOKISH_READING: Set<LateExperienceAdmission> = emptySet()

    internal fun admitsLateExperience(
        capability: V2SourceCapability,
        admitted: Set<LateExperienceAdmission>,
        claim: String,
    ): Boolean {
        return admitted.any {
            it.governedClaim == claim &&
                capability.supportedClaim == claim &&
                it.sourceId == capability.sourceId &&
                it.sourceRevision == capability.sourceRevision &&
                it.sourceRecordId == capability.sourceRecordId &&
                it.sourceHash == capability.sourceHash &&
                it.exactSpan == capability.exactSpan &&
                it.unitNumber == capability.unitNumber &&
                it.objectiveId == capability.objectiveId &&
                it.sourceHash == sha256(it.exactSpan)
        }
    }

    /**
     * Independently recorded productive-clock-time admissions.
     * Each entry names the complete frozen identity and the governed claim.
     * A capability's own supportedClaim is not an entry and is not proof.
     * The set stays empty until a genuine productive clock source is frozen
     * and admitted. No current span can clear the claim.
     */
    internal data class ProductiveClockTimeAdmission(
        val sourceId: String,
        val sourceRevision: String,
        val sourceRecordId: String,
        val sourceHash: String,
        val exactSpan: String,
        val unitNumber: Int,
        val objectiveId: String,
        val governedClaim: String,
    )

    internal val ADMITTED_PRODUCTIVE_CLOCK_TIME: Set<ProductiveClockTimeAdmission> = setOf(
        ProductiveClockTimeAdmission(
            sourceId = RemainingSources.CLOCK_SOURCE,
            sourceRevision = RemainingSources.CLOCK_REVISION,
            sourceRecordId = RemainingSources.recordId(RemainingSources.CLOCK_PAGE, RemainingSources.CLOCK_SPAN),
            sourceHash = RemainingSources.sha256(RemainingSources.CLOCK_SPAN),
            exactSpan = RemainingSources.CLOCK_SPAN,
            unitNumber = 17,
            objectiveId = "obj-u17-clock-productive",
            governedClaim = "productive-clock-time",
        ),
    )

    private fun admittedProductiveClockTime(capability: V2SourceCapability): Boolean {
        val recorded = ProductiveClockTimeAdmission(
            sourceId = capability.sourceId,
            sourceRevision = capability.sourceRevision,
            sourceRecordId = capability.sourceRecordId,
            sourceHash = capability.sourceHash,
            exactSpan = capability.exactSpan,
            unitNumber = capability.unitNumber,
            objectiveId = capability.objectiveId,
            governedClaim = "productive-clock-time",
        )
        return recorded in ADMITTED_PRODUCTIVE_CLOCK_TIME
    }

    private fun admittedLateSource(capability: V2SourceCapability): Boolean {
        if (Phase3Sources.matches(capability)) return true
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

    private val UNRESOLVED_VARIANCE_UNITS = emptySet<Int>()

    private val ACTIVATED_AUTHORITY_VARIANCE_UNITS = setOf(27, 29, 30)

    /**
     * An unresolved authority variance blocks content-complete and install claims.
     * Units 27, 29, and 30 are no longer in that set. Their pedagogical boundaries
     * are activated. Activation is not qualified review, installation, or release approval.
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
            val admitted = provenance.flatMap { listOf(it.shippedText, it.originalSourceText) }.toSet()
            learnerRussian(pack, unit).forEach { text ->
                cyrillicRegions(text).forEach { region ->
                    if (!admittedRegion(region, admitted)) {
                        errors += "Unit ${unit.number} Russian text has no resolved provenance record"
                    }
                }
            }
        }
    }

    private fun cyrillicRegions(text: String): List<String> {
        return CYRILLIC_REGION.findAll(text).map { it.value.trim() }.filter { it.isNotEmpty() }.toList()
    }

    private fun admittedRegion(region: String, admitted: Set<String>): Boolean {
        val trimmed = region.trimEnd { it == '?' || it == '!' || it == '.' || it == ',' }
        return region in admitted || trimmed in admitted
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
        if (capability.unitNumber == 29 && capability.objectiveId == "obj-u29-short-adjective") {
            // This claim requires curated grammar contrasts; vocabulary glosses cannot clear it.
            if (capability.supportedClaim != "short-form-adjective" ||
                capability.sourceId != "src-wikipedia-russian-grammar-1369617627" ||
                capability.sourceRevision != "1369617627" ||
                capability.sourceHash != "0b5b1bad657a8c901ba9f471ff7adff3a945be6f281aa760f58c59fd0f8c78e8" ||
                spanHost.sourceHash != capability.sourceHash || spanHost.originalSourceText != capability.exactSpan ||
                !verifiedFrozen(spanHost) || objective.checkItemIds != listOf("u29-i-adjective-form-1", "u29-i-adjective-form-2")) return false
            val checks = objective.checkItemIds.mapNotNull { pack.item(it) }
            val practice = objective.practiceStepIds.flatMap { id -> pack.step(id)?.second?.itemIds.orEmpty() }
            val guided = pack.item("u29-g-adjective-form-0") ?: return false
            return checks.size == 2 && checks.all {
                it.assessment?.independent == true && it.assessment.testedFeature == "SHORT_FORM" &&
                    CompleteRuleContextPins.itemMatches(pack, it) && structureValid(pack, it).isEmpty()
            } && guided.id in practice && guided.assessment?.independent == false &&
                CompleteRuleContextPins.itemMatches(pack, guided) && structureValid(pack, guided).isEmpty()
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

    /**
     * Classifying a listed source label is not an application of a base and
     * suffix. A future productive chain needs an independent check that is
     * bound to the span, names its answer in the feedback, and is not one of
     * the bounded classification labels.
     */
    private fun hasProductiveDiminutiveApplication(
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
            val assessment = item.assessment ?: return@any false
            if (!assessment.independent || item.feedback.isBlank()) return@any false
            if (!item.feedback.contains(item.answer)) return@any false
            if (!assessment.whyAnswerDemonstratesObjective.contains(item.answer)) return@any false
            if (item.prompt.contains("classifies", ignoreCase = true)) return@any false
            if (assessment.testedFeature == "DIMINUTIVE_FORM_CLASSIFICATION") return@any false
            if (BatchBSources.diminutiveClassificationLabel(item.answer)) return@any false
            if (!capability.exactSpan.contains(item.answer)) return@any false
            val distractorInSpan = item.options.any { option ->
                option != item.answer && capability.exactSpan.contains(option)
            }
            if (!distractorInSpan) return@any false
            val participants = assessment.relationParticipantIds.mapNotNull { id ->
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
            bound && structureValid(pack, item).isEmpty()
        }
    }

    private val CLOCK_APPLICATION_FEATURES = setOf(
        "CLOCK_MINUTES_INTO_HOUR",
        "CLOCK_MINUTES_REMAINING",
        "CLOCK_DIRECT_READING",
    )

    private val LETTER_APPLICATION_FEATURES = setOf(
        "LETTER_ORGANIZATION",
        "LETTER_ADDRESS_LIMIT",
        "LETTER_STRUCTURE",
    )

    /**
     * Listed-pair selection is not formation. Classification labels stay out of
     * this predicate so a relabeled classification chain keeps its own errors.
     */
    private fun exactPairMemorization(pack: V2CurriculumPack, capability: V2SourceCapability): Boolean {
        val objective = pack.units.firstOrNull { it.number == capability.unitNumber }
            ?.objectives?.firstOrNull { it.id == capability.objectiveId }
            ?: return false
        return objective.checkItemIds.mapNotNull { pack.item(it) }.any { item ->
            val feature = item.assessment?.testedFeature.orEmpty()
            val relation = item.assessment?.relationKind.orEmpty()
            feature != "DIMINUTIVE_FORM_CLASSIFICATION" &&
                (relation == "EXPLICIT_SOURCE_PAIR" || relation == "SOURCE_SUPPLIED_PAIR")
        }
    }

    private fun applicationChecks(
        pack: V2CurriculumPack,
        capability: V2SourceCapability,
        spanHost: V2ProvenanceRecord,
        independentOnly: Boolean = true,
    ): List<V2CheckItem> {
        val unit = pack.units.firstOrNull { it.number == capability.unitNumber } ?: return emptyList()
        val objective = unit.objectives.firstOrNull { it.id == capability.objectiveId } ?: return emptyList()
        val ids = objective.checkItemIds.toMutableList()
        if (!independentOnly) {
            unit.sessions.flatMap { it.steps }
                .filter { it.id in objective.practiceStepIds }
                .forEach { step -> ids += step.itemIds }
        }
        return ids.distinct().mapNotNull { pack.item(it) }.filter { item ->
            val assessment = item.assessment ?: return@filter false
            if ((independentOnly && !assessment.independent) || structureValid(pack, item).isNotEmpty()) return@filter false
            val participants = assessment.relationParticipantIds.mapNotNull { id ->
                pack.provenanceRecords.firstOrNull { it.itemId == id }
            }
            participants.any {
                it.sourceId == spanHost.sourceId &&
                    it.sourceRevision == spanHost.sourceRevision &&
                    it.sourceRecordId == spanHost.sourceRecordId &&
                    it.sourceHash == spanHost.sourceHash &&
                    it.originalSourceText == capability.exactSpan &&
                    it.objectiveId == capability.objectiveId
            }
        }
    }

    private fun hasProductiveClockApplication(
        pack: V2CurriculumPack,
        capability: V2SourceCapability,
        spanHost: V2ProvenanceRecord,
    ): Boolean {
        val checks = applicationChecks(pack, capability, spanHost, independentOnly = false)
        val features = checks.map { it.assessment?.testedFeature }.toSet()
        val independent = checks.filter { it.assessment?.independent == true }
        return CLOCK_APPLICATION_FEATURES.all { it in features } &&
            independent.count { it.assessment?.testedFeature in CLOCK_APPLICATION_FEATURES } >= 2 &&
            checks.filter { it.assessment?.testedFeature in CLOCK_APPLICATION_FEATURES }
                .all { it.assessment?.relationKind == "STATED_APPLICATION" }
    }

    private fun hasLetterConventionApplication(
        pack: V2CurriculumPack,
        capability: V2SourceCapability,
        spanHost: V2ProvenanceRecord,
    ): Boolean {
        val checks = applicationChecks(pack, capability, spanHost, independentOnly = false)
        val features = checks.map { it.assessment?.testedFeature }.toSet()
        val independent = checks.filter { it.assessment?.independent == true }
        return LETTER_APPLICATION_FEATURES.all { it in features } &&
            independent.count { it.assessment?.testedFeature in LETTER_APPLICATION_FEATURES } >= 2 &&
            checks.filter { it.assessment?.testedFeature in LETTER_APPLICATION_FEATURES }
                .all { it.assessment?.relationKind == "STATED_APPLICATION" }
    }

    /**
     * Stress is a placement contrast on one form. Choosing the headword instead
     * of its infinitive can be answered from category alone.
     */
    private fun hasParticipleStressDistinction(
        pack: V2CurriculumPack,
        capability: V2SourceCapability,
        spanHost: V2ProvenanceRecord,
    ): Boolean {
        return applicationChecks(pack, capability, spanHost).any { item ->
            val stressed = item.options.filter { option ->
                option.any { it == '\u0301' } && capability.exactSpan.contains(option)
            }
            stressed.size >= 2 &&
                stressed.map { foldGoverned(it) }.toSet().size == 1 &&
                item.answer in stressed
        }
    }

    private val DICTIONARY_FIELD_LABEL = Regex(
        "(?i)\\b(?:headwords?|declensions?|definitions?|pronunciations?|conjugations?|field|fields|section|sections)\\b",
    )

    private data class GovernedDictionaryEntry(
        val headword: String,
        val partOfSpeech: String,
        val forms: List<String>,
        val sense: String,
        val example: String,
    )

    /**
     * A governed entry is the labeled block the learner can see.
     * A part-of-speech label or a row of forms does not become an entry.
     */
    private fun governedDictionaryEntries(span: String): List<GovernedDictionaryEntry> {
        val entries = mutableListOf<GovernedDictionaryEntry>()
        var headword = ""
        var partOfSpeech = ""
        var forms = emptyList<String>()
        var sense = ""
        var example = ""
        fun flush() {
            val distinctForm = forms.any { it.isNotBlank() && it != headword }
            if (
                headword.isNotBlank() &&
                headword in forms &&
                partOfSpeech.length >= 3 &&
                sense.length >= 12 &&
                distinctForm
            ) {
                entries += GovernedDictionaryEntry(headword, partOfSpeech, forms, sense, example)
            }
            headword = ""
            partOfSpeech = ""
            forms = emptyList()
            sense = ""
            example = ""
        }
        span.lineSequence().forEach { raw ->
            val line = raw.trim()
            when {
                line.startsWith("Headword ") -> {
                    if (headword.isNotBlank()) flush()
                    headword = line.removePrefix("Headword ").trim()
                }
                line.startsWith("Part of speech ") -> partOfSpeech = line.removePrefix("Part of speech ").trim()
                line.startsWith("Forms ") -> {
                    forms = line.removePrefix("Forms ").split(",").map { it.trim() }.filter { it.isNotEmpty() }
                }
                line.startsWith("Sense ") -> sense = line.removePrefix("Sense ").trim()
                line.startsWith("Example ") -> example = line.removePrefix("Example ").trim()
            }
        }
        if (headword.isNotBlank()) flush()
        return entries
    }

    /**
     * Practical dictionary use reads a governed entry.
     * The feature name is not evidence: an inflected form or example in the cue
     * must be resolved by the sense of the one entry that governs that form.
     */
    private fun hasPracticalDictionaryUse(
        pack: V2CurriculumPack,
        capability: V2SourceCapability,
        spanHost: V2ProvenanceRecord,
    ): Boolean {
        val entries = governedDictionaryEntries(capability.exactSpan)
        if (entries.isEmpty()) return false
        return applicationChecks(pack, capability, spanHost).any { item ->
            interpretsGovernedEntry(item, entries)
        }
    }

    private fun interpretsGovernedEntry(
        item: V2CheckItem,
        entries: List<GovernedDictionaryEntry>,
    ): Boolean {
        if (item.assessment?.relationKind != "STATED_APPLICATION") return false
        val cue = item.promptLetter.orEmpty()
        val answer = item.answer
        if (cue.isBlank() || answer.length < 12) return false
        if (DICTIONARY_FIELD_LABEL.containsMatchIn(item.prompt) || DICTIONARY_FIELD_LABEL.containsMatchIn(item.answer)) {
            return false
        }
        if (entries.any { it.headword == cue || it.forms.joinToString(", ") == cue }) return false
        val owners = entries.filter { entry ->
            entry.sense !in cue &&
                entry.forms.any { form -> form != entry.headword && form.length >= 2 && form in cue }
        }
        if (owners.size != 1) return false
        val owner = owners.single()
        if (owner.sense !in answer) return false
        return entries.none { other -> other.sense != owner.sense && other.sense in answer }
    }

    /**
     * Home delivery is the full governed organization.
     * Recipient, premises, locality, and postcode without the applicable region
     * and the country are not that organization. A post-office box and an
     * apartment are conditional.
     */
    private fun hasCompletePostalOrganization(
        pack: V2CurriculumPack,
        capability: V2SourceCapability,
        spanHost: V2ProvenanceRecord,
    ): Boolean {
        val span = capability.exactSpan
        if (!span.contains("are not the complete order")) return false
        val checks = applicationChecks(pack, capability, spanHost)
        val complete = checks.filter { item ->
            val assessment = item.assessment
            assessment != null &&
                assessment.testedFeature == "COMPLETE_GOVERNED_ADDRESS" &&
                assessment.relationKind == "STATED_APPLICATION"
        }
        if (complete.size != 1) return false
        val item = complete.single()
        val cue = item.promptLetter.orEmpty()
        val answer = item.answer
        if (cue.length < 40 || !Regex("\\d{6}").containsMatchIn(cue)) return false
        if (cue !in span || answer !in span) return false
        if (answer == "recipient then premises then city then postcode") return false
        if (!postalOrderComplete(answer)) return false
        if (checks.none { it.answer.contains("post-office box") && it.answer.contains("not required") }) return false
        if (checks.none {
                it.answer.contains("federal city") &&
                    it.answer.contains("oblast") &&
                    it.answer.contains("does not add")
            }
        ) return false
        if (checks.none { it.answer.contains("apartment") && it.answer.contains("omitted") }) return false
        if (checks.any {
                it.answer.contains("must include a post-office box") ||
                    it.answer.contains("must invent an apartment") ||
                    it.answer.contains("must add an oblast")
            }
        ) return false
        return true
    }

    private fun postalOrderComplete(answer: String): Boolean {
        val markers = listOf("recipient", "premises", "locality", "region", "country", "postcode")
        var at = -1
        markers.forEach { marker ->
            val found = answer.indexOf(marker, at + 1)
            if (found < 0) return false
            at = found
        }
        return true
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
            IMPLEMENTED_PENDING_FINAL_AUDIT,
            RESEARCH_VERIFIED,
            FINAL_CURRICULUM_AUDIT_PASSED,
            INDEPENDENT_AUDIT_VERIFIED,
            OWNER_ACCEPTED,
            "REJECTED",
            -> {
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
        "GOVERNED_SENSE_CONTRAST",
        "STATED_APPLICATION",
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
        if (record.transformation == "wiktionary template selection") {
            return shipped.isNotEmpty() && shipped in BatchARemainingSources.unfoldWiktionary(original)
        }
        if (record.transformation == "readyrussian text extraction") {
            return shipped.isNotEmpty() && "<" !in original && ">" !in original && shipped in original
        }
        if (record.transformation == "pressbooks markup selection" || record.transformation == "readyrussian markup selection") {
            return shipped.isNotEmpty() && shipped in BatchARemainingSources.htmlVisible(original)
        }
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

    /**
     * Null means the records are not on an admitted Batch A remaining span.
     * An empty string means that span forbids the proposed relationship.
     * Co-occurrence on the span is not a relationship.
     */
    private fun batchBAdmissionErrors(capability: V2SourceCapability): List<String> {
        val claim = capability.supportedClaim
        val admitted = when (claim) {
            "reflexive-object" -> BatchBSources.ADMITTED_REFLEXIVE_OBJECT
            "purpose-clause" -> BatchBSources.ADMITTED_PURPOSE_CLAUSE
            "declined-numeral" -> BatchBSources.ADMITTED_DECLINED_NUMERAL
            "both-forms" -> BatchBSources.ADMITTED_BOTH_FORMS
            "one-and-a-half" -> BatchBSources.ADMITTED_ONE_AND_A_HALF
            "collective-numeral" -> BatchBSources.ADMITTED_COLLECTIVE_NUMERAL
            "negation-alternation" -> BatchBSources.ADMITTED_NEGATION_ALTERNATION
            "negative-pronoun" -> BatchBSources.ADMITTED_NEGATIVE_PRONOUN
            "register" -> BatchBSources.ADMITTED_REGISTER
            "elapsed-time-duration" -> BatchBSources.ADMITTED_ELAPSED_TIME
            "completion-over-period" -> BatchBSources.ADMITTED_COMPLETION_OVER_PERIOD
            "negation-scope" -> BatchBSources.ADMITTED_NEGATION_SCOPE
            "productive-diminutive" -> BatchBSources.ADMITTED_PRODUCTIVE_DIMINUTIVE
            "diminutive-form-classification" -> BatchBSources.ADMITTED_DIMINUTIVE_FORM_CLASSIFICATION
            else -> return emptyList()
        }
        if (claim == "completion-over-period" && Phase2Sources.matches(capability)) {
            return emptyList()
        }
        val admittedRecord = when (claim) {
            "negation-scope" -> BatchBSources.clearsNegationScope(capability, admitted)
            "productive-diminutive" -> BatchBSources.clearsProductiveDiminutive(capability, admitted)
            else -> BatchBSources.admits(capability, admitted, claim)
        }
        return if (admittedRecord) {
            emptyList()
        } else {
            listOf("Source capability ${capability.id} has no independent $claim admission")
        }
    }

    private fun batchBOverreachErrors(capability: V2SourceCapability): List<String> {
        val span = capability.exactSpan
        val folded = span.lowercase()
        val errors = mutableListOf<String>()
        if (capability.supportedClaim == "reflexive-object" && folded.contains("\u0441\u0432\u043e") && !folded.contains("\u0441\u0435\u0431\u044f")) {
            errors += "Source capability ${capability.id} \u0441\u0432\u043e\u0439 evidence does not clear reflexive-object"
        }
        if (capability.supportedClaim == "purpose-clause" && folded.contains("\u0447\u0442\u043e\u0431\u044b") && !span.contains("so that")) {
            errors += "Source capability ${capability.id} a sentence containing \u0447\u0442\u043e\u0431\u044b does not clear purpose-clause"
        }
        if (capability.supportedClaim == "one-and-a-half" && folded.contains("\u043f\u043e\u043b\u0442\u043e\u0440") && !span.contains("governs the")) {
            errors += "Source capability ${capability.id} \u043f\u043e\u043b\u0442\u043e\u0440\u0430 without the usage note does not clear one-and-a-half"
        }
        if (capability.supportedClaim == "collective-numeral" && !span.contains("collective numbers") &&
            (span.contains("cardinal") || folded.contains("\u0434\u0432\u0430"))
        ) {
            errors += "Source capability ${capability.id} a cardinal list does not clear collective-numeral"
        }
        if (capability.supportedClaim == "elapsed-time-duration" && span in BatchBSources.BOUNDED_TIME_SPANS) {
            errors += "Source capability ${capability.id} bounded time expression does not clear elapsed-time-duration"
        }
        if (capability.supportedClaim == "elapsed-time-duration" && !span.contains("emphasizes the duration")) {
            errors += "Source capability ${capability.id} partial duration evidence does not clear elapsed-time-duration"
        }
        if (capability.supportedClaim == "completion-over-period" && span.contains("emphasizes the duration")) {
            errors += "Source capability ${capability.id} duration evidence does not clear completion-over-period"
        }
        if (capability.supportedClaim == "completion-over-period" && (
                span in BatchBSources.BOUNDED_TIME_SPANS ||
                    span.contains("in a certain decade") ||
                    span.contains("during another event") ||
                    span.contains("relate events in time")
                )
        ) {
            errors += "Source capability ${capability.id} ago, during, or range evidence does not clear completion-over-period"
        }
        if (capability.supportedClaim == "completion-over-period" &&
            !Phase2Sources.matches(capability) &&
            !BatchBSources.admits(capability, BatchBSources.ADMITTED_COMPLETION_OVER_PERIOD, "completion-over-period") &&
            (span.contains("completion") || span.contains("co-occur"))
        ) {
            errors += "Source capability ${capability.id} keyword or co-occurrence evidence does not clear completion-over-period"
        }
        if (
            capability.supportedClaim == "imperfective-verbal-adverb" && !span.contains("usually present") ||
            capability.supportedClaim == "perfective-verbal-adverb" && !span.contains("can only be past")
        ) {
            errors += "Source capability ${capability.id} an isolated adverbial example does not state the present-versus-past relationship"
        }
        if (capability.supportedClaim == "negation-scope" &&
            (folded.contains("\u043d\u0438\u043a\u043e\u043c\u0443") || span.contains("\u043d\u0438\u0447\u0435\u0433\u043e \u043d\u0435"))
        ) {
            errors += "Source capability ${capability.id} negative concord does not clear negation-scope"
        }
        if (capability.supportedClaim == "negation-scope" &&
            (folded.contains("\u043a\u043d\u0438\u0433") || span.contains("I don't see"))
        ) {
            errors += "Source capability ${capability.id} the genitive and accusative compare does not clear negation-scope"
        }
        if (capability.supportedClaim == "negation-scope" &&
            span.contains("logical stress") &&
            !span.contains("alternative word order can change the meaning entirely")
        ) {
            errors += "Source capability ${capability.id} word-order emphasis does not clear negation-scope"
        }
        if (capability.supportedClaim == "negation-scope" && span.contains("emphasizes the duration")) {
            errors += "Source capability ${capability.id} duration evidence does not clear negation-scope"
        }
        if (capability.supportedClaim == "negation-scope" &&
            !BatchBSources.admits(capability, BatchBSources.ADMITTED_NEGATION_SCOPE, "negation-scope") &&
            (span.contains("negation scope") || span.contains("scope placement"))
        ) {
            errors += "Source capability ${capability.id} keyword or co-occurrence evidence does not clear negation-scope"
        }
        if (capability.supportedClaim == "completion-over-period" &&
            (span.contains("How long something takes") || span.contains("\u041d\u0430 \u0441\u043a\u043e\u043b\u044c\u043a\u043e \u0432\u0440\u0435\u043c\u0435\u043d\u0438"))
        ) {
            errors += "Source capability ${capability.id} intended-length evidence does not clear completion-over-period"
        }
        if (capability.supportedClaim == "negation-scope" && BatchBSources.wordOrderMeaningContrast(span)) {
            errors += "Source capability ${capability.id} word-order evidence does not clear negation-scope"
        }
        if (span.contains("alternative word order can change the meaning entirely")) {
            if (capability.supportedClaim == "completion-over-period") {
                errors += "Source capability ${capability.id} word-order evidence does not clear completion-over-period"
            }
            if (capability.supportedClaim == "adverb-paradigm") {
                errors += "Source capability ${capability.id} word-order evidence does not clear adverb-paradigm"
            }
            if (capability.supportedClaim == "productive-clock-time") {
                errors += "Source capability ${capability.id} word-order evidence does not clear productive-clock-time"
            }
            if (capability.supportedClaim == "productive-diminutive") {
                errors += "Source capability ${capability.id} word-order evidence does not clear productive-diminutive"
            }
            if (capability.supportedClaim == "elapsed-time-duration") {
                errors += "Source capability ${capability.id} word-order evidence does not clear elapsed-time-duration"
            }
        }
        if (capability.supportedClaim == "adverb-paradigm" &&
            !BatchARemainingSources.admits(capability, BatchARemainingSources.ADMITTED_ADVERB_PARADIGM, "adverb-paradigm") &&
            span.contains("adverb paradigm")
        ) {
            errors += "Source capability ${capability.id} keyword or co-occurrence evidence does not clear adverb-paradigm"
        }
        if (capability.supportedClaim == "adverb-paradigm" &&
            (capability.sourceId == Phase2Sources.SOURCE_ID || capability.sourceHash == Phase2Sources.MANNER.sourceHash)
        ) {
            errors += "Source capability ${capability.id} bounded manner-adverb evidence does not clear adverb-paradigm"
        }
        val phase2Claim = Phase2Sources.admission(capability.supportedClaim)
        if (phase2Claim != null && !Phase2Sources.matches(capability) &&
            (span.contains(capability.supportedClaim) || span.contains("co-occurrence") || span.contains("co-occur"))
        ) {
            errors += "Source capability ${capability.id} keyword or co-occurrence evidence does not clear ${capability.supportedClaim}"
        }
        val phase3Claim = Phase3Sources.admission(capability.supportedClaim)
        if (phase3Claim != null && !Phase3Sources.matches(capability) &&
            (span.contains(capability.supportedClaim) || span.contains("co-occurrence") || span.contains("co-occur"))
        ) {
            errors += "Source capability ${capability.id} keyword or co-occurrence evidence does not clear ${capability.supportedClaim}"
        }
        if (capability.supportedClaim == "bounded-manner-adverb-formation" &&
            (span == BatchARemainingSources.INVARIANCE_SPAN || span == BatchARemainingSources.SUFFIXAL_SPAN || span == ExplicitSourcePairs.adverbSpan)
        ) {
            errors += "Source capability ${capability.id} bounded adverb evidence does not clear bounded-manner-adverb-formation"
        }
        if (capability.supportedClaim == "documented-name-form" &&
            !Phase2Sources.matches(capability) &&
            BatchBSources.boundedDiminutiveRecognition(span)
        ) {
            errors += "Source capability ${capability.id} bounded diminutive recognition does not clear documented-name-form"
        }
        if (capability.supportedClaim == "productive-diminutive" && capability.sourceHash == Phase2Sources.NAME_FORM.sourceHash) {
            errors += "Source capability ${capability.id} documented name-form evidence does not clear productive-diminutive"
        }
        if (capability.supportedClaim == "productive-clock-time" &&
            !admittedProductiveClockTime(capability) &&
            span.contains("productive clock")
        ) {
            errors += "Source capability ${capability.id} keyword or co-occurrence evidence does not clear productive-clock-time"
        }
        if (capability.supportedClaim == "productive-diminutive" &&
            !BatchBSources.admits(capability, BatchBSources.ADMITTED_PRODUCTIVE_DIMINUTIVE, "productive-diminutive") &&
            span.contains("productive diminutive")
        ) {
            errors += "Source capability ${capability.id} keyword or co-occurrence evidence does not clear productive-diminutive"
        }
        if (capability.supportedClaim == "productive-diminutive" &&
            !BatchBSources.clearsProductiveDiminutive(capability) &&
            (folded.contains("\u0438\u0440\u043e\u0447\u043a\u0430") || span.contains("diminutive") || folded.contains("\u0443\u043c\u0435\u043d\u044c\u0448\u0438\u0442\u0435\u043b\u044c\u043d"))
        ) {
            errors += "Source capability ${capability.id} bounded diminutive recognition does not clear productive-diminutive"
        }
        return errors
    }

    /**
     * Null means the records are not on a Batch B span.
     * An empty string means that span forbids the proposed relationship.
     */
    private fun batchBRelation(
        cue: V2ProvenanceRecord,
        answer: V2ProvenanceRecord,
        distractors: List<V2ProvenanceRecord>,
    ): String? {
        val table = BatchBSources.GOVERNED_GLOSSES[cue.originalSourceText] ?: return null
        if (cue.originalSourceText == BatchBSources.REFLEXIVE_SPAN && cue.shippedText == BatchBSources.ISOLATED_REFLEXIVE) {
            return ""
        }
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

    private fun governedRelation(
        cue: V2ProvenanceRecord,
        answer: V2ProvenanceRecord,
        distractors: List<V2ProvenanceRecord>,
    ): String? {
        val batchB = batchBRelation(cue, answer, distractors)
        if (batchB == "") return ""
        if (batchB != null) return batchB
        val group = listOf(cue, answer) + distractors
        if (
            distractors.size == 1 &&
            cue.shippedText == BatchARemainingSources.LIKELIHOOD_SENSE &&
            answer.shippedText == BatchARemainingSources.LIKELIHOOD_RUSSIAN &&
            distractors[0].shippedText == BatchARemainingSources.OBLIGATION_RUSSIAN &&
            cue.originalSourceText == BatchARemainingSources.LIKELIHOOD_SPAN &&
            answer.originalSourceText == BatchARemainingSources.LIKELIHOOD_SPAN &&
            distractors[0].originalSourceText == BatchARemainingSources.OBLIGATION_EXAMPLE_SPAN &&
            cue.sourceId == BatchARemainingSources.WIKTIONARY_SOURCE &&
            answer.sourceId == BatchARemainingSources.WIKTIONARY_SOURCE &&
            distractors[0].sourceId == BatchARemainingSources.WIKTIONARY_SOURCE &&
            cue.sourceRevision == BatchARemainingSources.WIKTIONARY_REVISION &&
            answer.sourceRevision == BatchARemainingSources.WIKTIONARY_REVISION &&
            distractors[0].sourceRevision == BatchARemainingSources.WIKTIONARY_REVISION &&
            sameFrozenSpan(cue, answer) &&
            !sameFrozenSpan(cue, distractors[0]) &&
            group.all { displayedInFrozen(it) }
        ) {
            return "GOVERNED_SENSE_CONTRAST"
        }
        if (cue.originalSourceText == BatchARemainingSources.INVARIANCE_SPAN) {
            val wording = BatchARemainingSources.dashColonWording(cue.originalSourceText)
            val shown = cue.originalSourceText.substringBefore('\u2014').trim()
            if (
                wording != null &&
                cue.shippedText == shown &&
                answer.shippedText == wording &&
                cue.shippedText != answer.shippedText &&
                group.all { sameFrozenSpan(cue, it) } &&
                group.all { displayedInFrozen(it) } &&
                distractors.all { it.shippedText != wording }
            ) {
                return "EXPLICIT_SOURCE_PAIR"
            }
            return ""
        }
        val table = BatchARemainingSources.GOVERNED_GLOSSES[cue.originalSourceText] ?: return null
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

    /**
     * Content-audit synthesis states each corrected cue, answer, and distractor set.
     * Null means the cue is not a content-audit record. Empty means the proposed
     * relationship is not one of the stated applications.
     */
    private fun contentAuditRelation(
        cue: V2ProvenanceRecord,
        answer: V2ProvenanceRecord,
        distractors: List<V2ProvenanceRecord>,
    ): String? {
        if (cue.transformation != ContentAuditSources.TRANSFORMATION) return null
        if (cue.sourceId != ContentAuditSources.SOURCE_ID) return ""
        val applications = cue.synthesis?.applications.orEmpty()
        if (applications.isEmpty()) return ""
        val group = listOf(cue, answer) + distractors
        val distractorTexts = distractors.map { it.shippedText }.toSet()
        if (!group.all { CompleteRuleContextPins.matchesIfPinned(it) }) return ""
        val spec = applications.firstOrNull {
            it.cue == cue.shippedText && it.answer == answer.shippedText &&
                it.distractors.toSet() == distractorTexts
        } ?: return ""
        if (
            answer.shippedText == spec.answer &&
            distractorTexts == spec.distractors.toSet() &&
            spec.distractors.isNotEmpty() &&
            spec.answer !in distractorTexts &&
            group.all { sameFrozenSpan(cue, it) } &&
            group.all { displayedInFrozen(it) }
        ) {
            return "STATED_APPLICATION"
        }
        return ""
    }

    /**
     * Original synthesis states each cue, answer, and distractor set.
     * Co-occurrence on the same span is not that application.
     * Null means the cue is not original synthesis. Empty means the proposed
     * relationship is forbidden for that synthesis.
     */
    private fun originalSynthesisRelation(
        cue: V2ProvenanceRecord,
        answer: V2ProvenanceRecord,
        distractors: List<V2ProvenanceRecord>,
    ): String? {
        if (cue.transformation != Phase2Sources.TRANSFORMATION) return null
        if (cue.sourceId != Phase2Sources.SOURCE_ID) return ""
        if (!(listOf(cue, answer) + distractors).all { OriginalApplicationTuplePins.matchesIfPinned(it) }) return ""
        val applications = cue.synthesis?.applications.orEmpty()
        if (applications.isEmpty()) return ""
        val spec = applications.firstOrNull { it.cue == cue.shippedText } ?: return ""
        val group = listOf(cue, answer) + distractors
        val distractorTexts = distractors.map { it.shippedText }.toSet()
        if (
            answer.shippedText == spec.answer &&
            distractorTexts == spec.distractors.toSet() &&
            spec.distractors.isNotEmpty() &&
            spec.answer !in distractorTexts &&
            group.all { sameFrozenSpan(cue, it) } &&
            group.all { displayedInFrozen(it) }
        ) {
            return "STATED_APPLICATION"
        }
        return ""
    }

    /**
     * A stated application names one frozen construction and the competing
     * construction the same page pairs with a different meaning. Any other
     * combination on that span is not co-occurrence.
     */
    private fun statedApplication(
        cue: V2ProvenanceRecord,
        answer: V2ProvenanceRecord,
        distractors: List<V2ProvenanceRecord>,
    ): String? {
        val table = STATED_APPLICATIONS[cue.originalSourceText] ?: return null
        val spec = table[cue.shippedText] ?: return ""
        val keyed = spec.first
        val allowed = spec.second
        val group = listOf(cue, answer) + distractors
        val distractorTexts = distractors.map { it.shippedText }.toSet()
        if (
            answer.shippedText == keyed &&
            distractorTexts.isNotEmpty() &&
            allowed.containsAll(distractorTexts) &&
            keyed !in distractorTexts &&
            group.all { sameFrozenSpan(cue, it) } &&
            group.all { displayedInFrozen(it) }
        ) {
            return "STATED_APPLICATION"
        }
        return ""
    }

    private val STATED_APPLICATIONS: Map<String, Map<String, Pair<String, Set<String>>>> = mapOf(
        RemainingSources.CLOCK_SPAN to mapOf(
            "In Russian we can state the number of minutes into the hour which is in progress." to (
                "\u0432 \u043f\u044f\u0442\u043d\u0430\u0434\u0446\u0430\u0442\u044c \u043c\u0438\u043d\u0443\u0442 \u0432\u0442\u043e\u0440\u043e\u0433\u043e" to
                    setOf("\u0432 \u0447\u0430\u0441 \u043f\u044f\u0442\u043d\u0430\u0434\u0446\u0430\u0442\u044c \u043c\u0438\u043d\u0443\u0442")
                ),
            "This is used after the half hour." to (
                "\u0431\u0435\u0437 \u0434\u0432\u0430\u0434\u0446\u0430\u0442\u0438 \u043f\u044f\u0442\u0438 \u0434\u0432\u0430" to
                    setOf("\u0432 \u0447\u0430\u0441 \u0442\u0440\u0438\u0434\u0446\u0430\u0442\u044c \u043f\u044f\u0442\u044c \u043c\u0438\u043d\u0443\u0442")
                ),
            "This option is always available." to (
                "\u0432 \u0434\u0432\u0435\u043d\u0430\u0434\u0446\u0430\u0442\u044c \u0447\u0430\u0441\u043e\u0432 \u0434\u0435\u0441\u044f\u0442\u044c \u043c\u0438\u043d\u0443\u0442" to
                    setOf("\u0432 \u0434\u0435\u0441\u044f\u0442\u044c \u043c\u0438\u043d\u0443\u0442 \u043f\u0435\u0440\u0432\u043e\u0433\u043e")
                ),
        ),
        RemainingSources.LETTER_SPAN to mapOf(
            "In English we begin with a topic sentence and then follow with supporting facts." to (
                "In Russian the facts come first with the topic sentence as the conclusion." to
                    setOf("Strangers and people who are not closed friends should never be addressed as \u201cdear\u201d.")
                ),
            "\u0417\u0434\u0440\u0430\u0432\u0441\u0442\u0432\u0443\u0439\u0442\u0435!" to (
                "Strangers and people who are not closed friends should never be addressed as \u201cdear\u201d." to
                    setOf("In Russian the facts come first with the topic sentence as the conclusion.")
                ),
            "Body" to (
                "Closing and signature" to setOf("Greeting", "Sender\u2019s address and date")
                ),
        ),
    )

    internal fun classifyRelation(
        cue: V2ProvenanceRecord,
        answer: V2ProvenanceRecord,
        distractors: List<V2ProvenanceRecord>,
    ): String? {
        val audit = contentAuditRelation(cue, answer, distractors)
        if (audit == "") return null
        if (audit != null) return audit
        val synthesis = originalSynthesisRelation(cue, answer, distractors)
        if (synthesis == "") return null
        if (synthesis != null) return synthesis
        val governed = governedRelation(cue, answer, distractors)
        if (governed == "") return null
        if (governed != null) return governed
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
        val stated = statedApplication(cue, answer, distractors)
        if (stated == "") return null
        if (stated != null) return stated
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
    private val CONCEPT_CHECK_OBJECTIVES = setOf(
        "obj-u21-object-reference",
        "obj-u21-purpose",
        "obj-u22-declension",
        "obj-u22-both-forms",
        "obj-u22-half-forms",
        "obj-u22-collective",
        "obj-u23-ranges",
        "obj-u23-beginning",
        "obj-u23-during-event",
        "obj-u23-duration",
        "obj-u23-elapsed",
        "obj-u24-alternation",
        "obj-u24-stand-ins",
        "obj-u24-concord",
        "obj-u24-placement",
        "obj-u24-word-order-contrast",
        "obj-u25-nickname-rule",
        "obj-u25-productive-formation",
        "obj-u26-indefinite",
        "obj-u26-word-order",
        "obj-u26-letter-writing",
        "obj-u26-correspondence",
        "obj-u27-passive",
        "obj-u27-stress",
        "obj-u29-punctuation",
        "obj-u28-imperfective-adverb",
        "obj-u28-perfective-adverb",
        "obj-u28-readings",
        "obj-u30-abbreviation",
        "obj-u30-dictionary",
        "obj-u30-capstone",
        "obj-u30-particle",
    )
    /**
     * Independent concept checks cannot be answered by retrieving a source position.
     * Rewording "written after the equals sign" still names the layout, not the distinction.
     */
    private val SOURCE_LAYOUT_RECALL = Regex(
        "(?i)(?:" +
            "equals\\s+sign|" +
            "frozen\\s+line|" +
            "\\bwritten\\s+(?:after|for|on|in|beside|next\\s+to)\\b|" +
            "\\bcolumn\\b|" +
            "\\bbeside\\s+(?:the\\s+)?(?:shown\\s+)?tag\\b|" +
            "\\bg\\s*=\\s*[fmn]\\b" +
            ")",
    )
    /**
     * Source-location and set-recall prompts. A rewording of "written beside"
     * or "the other source line" still asks where an item sits, not what it means.
     */
    private val SOURCE_SET_RECALL = Regex(
        "(?i)(?:" +
            "\\bwritten\\b|" +
            "\\blisted\\b|" +
            "\\bgrouped\\s+with\\b|" +
            "\\bbeside\\b|" +
            "\\bshown\\s+in\\s+the\\s+source\\b|" +
            "\\bother\\s+source\\s+line\\b|" +
            "\\bsource\\s+also\\s+allow\\b|" +
            "\\bother\\s+\\w+\\s+line\\b|" +
            "\\bappears\\s+in\\s+(?:a|the)\\s+source\\b|" +
            "\\bsource\\s+set\\b|" +
            "\\bwhich\\s+item\\s+appears\\b|" +
            "\\bplaced\\s+(?:beside|with|next\\s+to)\\b" +
            ")",
    )
    private val USE_CLASSIFICATION_OBJECTIVES = setOf(
        "obj-u25-nickname-rule",
        "obj-u26-indefinite",
        "obj-u29-punctuation",
        "obj-u30-abbreviation",
        "obj-u30-particle",
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
        if (assessment.independent &&
            item.objectiveId in CONCEPT_CHECK_OBJECTIVES &&
            (SOURCE_LAYOUT_RECALL.containsMatchIn(item.prompt) || SOURCE_SET_RECALL.containsMatchIn(item.prompt))
        ) {
            errors += "Item ${item.id} prompt discloses its classification"
            errors += "Item ${item.id} tests line-label recall"
        }
        if (assessment.independent && item.objectiveId in USE_CLASSIFICATION_OBJECTIVES) {
            if (!why.contains(item.answer)) {
                errors += "Item ${item.id} answer rationale does not cite the keyed answer"
            }
            val spans = assessment.relationParticipantIds.mapNotNull { id ->
                pack.provenanceRecords.firstOrNull { it.itemId == id }?.originalSourceText
            }
            if (spans.none { it.contains(item.answer) }) {
                errors += "Item ${item.id} keyed answer is not in the frozen span"
            }
            item.options.forEachIndexed { index, option ->
                if (option == item.answer) return@forEachIndexed
                val rationale = item.distractorRationales.getOrElse(index) { "" }
                val difference = assessment.distractorDifference.getOrElse(index) { "" }
                if (!rationale.contains(option) || !difference.contains(option)) {
                    errors += "Item ${item.id} distractor rationale does not cite the distractor"
                }
            }
        }
        errors += reflexiveRelationshipErrors(item)
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
        errors += falseRelationshipErrors(item, originals)
        return errors
    }

    private val CLAIMED_PAIR = Regex(
        """The frozen source pairs (?:the shown form )?"([^"]+)" with (?:the admitted gloss )?"([^"]+)"""",
    )
    private val CO_OCCURRENCE_CLAIM = Regex("(?i)co-occur|appear together")
    private val DASH_PAIR = Regex("""([^\n—–]{1,90})\s*[—–]\s*([^\n—–]{1,160})""")

    /**
     * A rationale may cite only a relationship the frozen record states.
     * Pairing a gloss with itself, swapping two rows, or calling co-occurrence
     * a relationship fails closed.
     */
    private fun falseRelationshipErrors(item: V2CheckItem, originals: List<String>): List<String> {
        val errors = mutableListOf<String>()
        val explicit = linkedMapOf<String, MutableSet<String>>()
        originals.forEach { original ->
            explicitPairs(original).forEach { (left, rights) ->
                explicit.getOrPut(left) { linkedSetOf() }.addAll(rights)
            }
        }
        val hosted = listOf(item.assessment?.whyAnswerDemonstratesObjective.orEmpty(), item.feedback) +
            item.distractorRationales +
            item.assessment?.distractorDifference.orEmpty()
        hosted.forEach { text ->
            if (text.isBlank()) return@forEach
            val claims = CLAIMED_PAIR.findAll(text).map { it.groupValues[1] to it.groupValues[2] }.toList()
            claims.forEach { (left, right) ->
                if (left == right && !explicitSelfPair(originals, left)) {
                    errors += "Item ${item.id} false self-pair"
                } else if (left != right && left in explicit && !pairSupported(explicit.getValue(left), right)) {
                    val swapped = explicit.any { (other, rights) -> other != left && pairSupported(rights, right) }
                    if (swapped) {
                        errors += "Item ${item.id} swapped source relationship"
                    } else {
                        errors += "Item ${item.id} unsupported by the cited frozen record"
                    }
                }
            }
            if (CO_OCCURRENCE_CLAIM.containsMatchIn(text) && claims.none { (left, right) ->
                    left != right && left in explicit && pairSupported(explicit.getValue(left), right)
                }
            ) {
                errors += "Item ${item.id} co-occurrence without the stated relationship"
            }
        }
        return errors
    }

    /** The production relationship predicate, so a lost dash pair cannot hide a rejection. */
    internal fun unsupportedRelationshipErrorsForTests(item: V2CheckItem, originals: List<String>): List<String> {
        return falseRelationshipErrors(item, originals)
    }

    private fun pairSupported(rights: Set<String>, claimed: String): Boolean {
        return rights.any { gloss ->
            gloss == claimed ||
                (claimed.length >= 4 && (gloss.startsWith(claimed) || claimed.startsWith(gloss)))
        }
    }

    private fun explicitSelfPair(originals: List<String>, text: String): Boolean {
        return originals.any { original ->
            val visible = BatchARemainingSources.htmlVisible(original)
            visible.contains("$text—$text") || visible.contains("$text – $text") || visible.contains("$text - $text")
        }
    }

    private val explicitPairCache = object : LinkedHashMap<String, Map<String, Set<String>>>(64, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Map<String, Set<String>>>): Boolean {
            return size > 128
        }
    }

    private fun explicitPairs(original: String): Map<String, Set<String>> {
        synchronized(explicitPairCache) {
            explicitPairCache[original]?.let { return it }
        }
        val computed = computeExplicitPairs(original, linearDashes = true)
        synchronized(explicitPairCache) {
            explicitPairCache[original] = computed
        }
        return computed
    }

    internal fun explicitPairsProbe(original: String): Map<String, Set<String>> = explicitPairs(original)

    internal fun explicitPairsRegexProbe(original: String): Map<String, Set<String>> {
        return computeExplicitPairs(original, linearDashes = false)
    }

    /** Raw scanner pairs, without the explicit-pair cache or trim. */
    internal fun dashPairsProbe(visible: String): List<Pair<String, String>> = linearDashPairs(visible)

    /** Raw JVM regex pairs for the same pattern the scanner implements. */
    internal fun dashPairsRegexProbe(visible: String): List<Pair<String, String>> = regexDashPairs(visible)

    internal fun dashScanOperationsForTests(): Long = dashScanOperations.get()

    internal fun resetDashScanOperationsForTests() {
        dashScanOperations.set(0)
    }

    private fun computeExplicitPairs(original: String, linearDashes: Boolean): Map<String, Set<String>> {
        val visible = BatchARemainingSources.htmlVisible(unfoldWikiLabels(original))
        val found = linkedMapOf<String, MutableSet<String>>()
        fun add(left: String, right: String) {
            val key = left.trim().trim('\'', '"', '\u201c', '\u201d', '{', '}')
            val value = right.trim().trim('\'', '"', '\u201c', '\u201d')
            if (key.isNotEmpty() && value.isNotEmpty() && key != value) {
                found.getOrPut(key) { linkedSetOf() }.add(value)
            }
        }
        val dashes = if (linearDashes) linearDashPairs(visible) else regexDashPairs(visible)
        dashes.forEach { (leftRaw, rightRaw) ->
            val left = leftRaw.substringAfterLast("}}")
            val right = rightRaw.trim()
            val paren = Regex("""^([^()]{1,140}?)\s*\(([^()]*)\)\s*$""").find(right)
            if (paren != null) {
                add(left, paren.groupValues[1])
                add(left, paren.groupValues[2])
            } else {
                add(left, right)
            }
        }
        Regex("""((?:"[^"]+"\s*,\s*)+"[^"]+")\s*\(([^)]+)\)""").findAll(visible).forEach { match ->
            val forms = Regex(""""([^"]+)"""").findAll(match.groupValues[1]).map { it.groupValues[1] }.toList()
            val glosses = match.groupValues[2].split(",").map { it.trim() }
            if (forms.size == glosses.size) {
                forms.zip(glosses).forEach { (form, gloss) -> add(form, gloss) }
            }
        }
        return found
    }

    private fun regexDashPairs(visible: String): List<Pair<String, String>> {
        return DASH_PAIR.findAll(visible).map { it.groupValues[1] to it.groupValues[2] }.toList()
    }

    private val dashScanOperations = java.util.concurrent.atomic.AtomicLong()

    /**
     * Same non-overlapping pairs as [DASH_PAIR].
     *
     * Java's `Matcher.find` counts `[^\n—–]{1,90}` and `{1,160}` in code points
     * (`Character.codePointAt` / `charCount`), so a supplementary character is
     * one capture unit and is never split. Search still resumes on UTF-16
     * boundaries after each match. A dash is the separator: the leftmost start
     * that reaches it with greedy `A{1,90}\s*` owns that dash, and the right
     * side is `\s*` with backtracking onto non-newline whitespace. Failed
     * starts do not rescan the remaining suffix; a string with no dash is one
     * forward pass.
     */
    private fun linearDashPairs(visible: String): List<Pair<String, String>> {
        val out = ArrayList<Pair<String, String>>()
        var search = 0
        val length = visible.length
        while (search < length) {
            val dash = findNextDash(visible, search)
            if (dash < 0) break
            val left = leftmostLeftCapture(visible, search, dash)
            if (left == null) {
                search = dash + 1
                continue
            }
            val right = matchDashRight(visible, dash)
            if (right == null) {
                search = dash + 1
                continue
            }
            out += visible.substring(left.first, left.second) to visible.substring(right.first, right.second)
            search = right.second
        }
        return out
    }

    private fun findNextDash(text: String, from: Int): Int {
        var index = from
        val length = text.length
        while (index < length) {
            dashScanOperations.incrementAndGet()
            val character = text[index]
            if (character == '—' || character == '–') return index
            index++
        }
        return -1
    }

    /**
     * Left capture bounds for the leftmost start at or after [search] whose
     * greedy left side lands on [dash]. Null when no such start exists.
     */
    private fun leftmostLeftCapture(text: String, search: Int, dash: Int): Pair<Int, Int>? {
        var whitespace = dash
        while (whitespace > search && isPatternWhitespace(text[whitespace - 1])) {
            dashScanOperations.incrementAndGet()
            whitespace--
        }
        var runStart = whitespace
        while (runStart > search) {
            dashScanOperations.incrementAndGet()
            val previous = runStart - 1
            var codeStart = previous
            if (text[previous].isLowSurrogate() && previous > search && text[previous - 1].isHighSurrogate()) {
                codeStart = previous - 1
            }
            if (codeStart < search) break
            if (!isDashCaptureCodePoint(Character.codePointAt(text, codeStart))) break
            runStart = codeStart
        }
        if (search >= dash) return null
        if (search > runStart && search >= whitespace) {
            var start = search
            while (start < dash && !isDashCaptureCodePoint(Character.codePointAt(text, start))) {
                dashScanOperations.incrementAndGet()
                start++
            }
            if (start >= dash) return null
            return greedyLeftCapture(text, start, start, dash)
        }
        if (runStart == whitespace) {
            var start = maxOf(runStart, search)
            while (start < dash && !isDashCaptureCodePoint(Character.codePointAt(text, start))) {
                dashScanOperations.incrementAndGet()
                start++
            }
            if (start >= dash) return null
            return greedyLeftCapture(text, start, whitespace, dash)
        }
        val region = maxOf(runStart, search)
        return greedyLeftCapture(text, region, whitespace, dash)
    }

    private fun greedyLeftCapture(text: String, start: Int, whitespace: Int, dash: Int): Pair<Int, Int>? {
        var index = start
        var count = 0
        var end = start
        while (index < dash && count < 90) {
            dashScanOperations.incrementAndGet()
            val codePoint = Character.codePointAt(text, index)
            if (!isDashCaptureCodePoint(codePoint)) break
            val next = index + Character.charCount(codePoint)
            if (next > dash) break
            index = next
            count++
            end = index
            if (count == 90 && end < whitespace) return startForLeftEnd(text, start, whitespace)
        }
        if (count == 0) return null
        var rest = end
        while (rest < dash) {
            dashScanOperations.incrementAndGet()
            if (!isPatternWhitespace(text[rest])) return null
            rest++
        }
        return start to end
    }

    private fun startForLeftEnd(text: String, region: Int, limit: Int): Pair<Int, Int>? {
        val starts = ArrayList<Int>()
        var index = region
        while (index < limit) {
            dashScanOperations.incrementAndGet()
            starts += index
            index += Character.charCount(Character.codePointAt(text, index))
        }
        if (starts.size < 90) return null
        return starts[starts.size - 90] to limit
    }

    /**
     * `\s*([^\n—–]{1,160})` after a dash. Greedy whitespace is kept when the
     * following code point can start the right capture. Otherwise whitespace
     * backtracks to the rightmost code point the capture can take, including
     * non-newline whitespace. The capture counts code points and does not
     * split a surrogate pair.
     */
    private fun matchDashRight(visible: String, dash: Int): Pair<Int, Int>? {
        val length = visible.length
        var whitespaceEnd = dash + 1
        while (whitespaceEnd < length && isPatternWhitespace(visible[whitespaceEnd])) {
            dashScanOperations.incrementAndGet()
            whitespaceEnd++
        }
        if (whitespaceEnd < length && isDashCaptureCodePoint(Character.codePointAt(visible, whitespaceEnd))) {
            return whitespaceEnd to consumeDashCapture(visible, whitespaceEnd)
        }
        var rightStart = whitespaceEnd - 1
        while (rightStart > dash) {
            dashScanOperations.incrementAndGet()
            if (isDashCaptureCodePoint(Character.codePointAt(visible, rightStart))) {
                return rightStart to consumeDashCapture(visible, rightStart)
            }
            rightStart--
        }
        return null
    }

    private fun consumeDashCapture(visible: String, start: Int): Int {
        var end = start
        var count = 0
        while (end < visible.length && count < 160) {
            dashScanOperations.incrementAndGet()
            val codePoint = Character.codePointAt(visible, end)
            if (!isDashCaptureCodePoint(codePoint)) break
            end += Character.charCount(codePoint)
            count++
        }
        return end
    }

    private fun isDashCaptureCodePoint(codePoint: Int): Boolean {
        return codePoint != '\n'.code && codePoint != '—'.code && codePoint != '–'.code
    }

    private fun isPatternWhitespace(character: Char): Boolean {
        return character == ' ' || character == '\t' || character == '\n' ||
            character == '\u000B' || character == '\u000C' || character == '\r'
    }

    private val READY_RUSSIAN_MEDIA = Regex(
        "(?i)<img|data:image|base64|<script|<audio|\\.ogg|data:audio",
    )

    private fun validateGovernedReadyRussianText(pack: V2CurriculumPack, errors: MutableList<String>) {
        fun reject(field: String, text: String?) {
            if (!text.isNullOrEmpty() && READY_RUSSIAN_MEDIA.containsMatchIn(text)) {
                errors += "governed ReadyRussian span contains embedded media in $field"
            }
        }
        pack.provenanceRecords.filter { it.sourceId.startsWith("src-readyrussian-") }.forEach { record ->
            reject("provenance ${record.itemId}", record.originalSourceText)
            reject("provenance ${record.itemId}", record.shippedText)
        }
        val readyUnits = pack.provenanceRecords
            .filter { it.sourceId.startsWith("src-readyrussian-") }
            .map { it.unitNumber }
            .toSet()
        pack.units.filter { it.number in readyUnits }.forEach { unit ->
            reject("unit ${unit.number} title", unit.title)
            reject("unit ${unit.number} topic", unit.topicSummary)
            unit.sessions.forEach { session ->
                session.steps.forEach { step ->
                    reject("step ${step.id}", step.title)
                    reject("step ${step.id}", step.body)
                }
            }
        }
        val readyObjectives = pack.units.filter { it.number in readyUnits }.flatMap { it.objectives }.map { it.id }.toSet()
        pack.items.filter { it.objectiveId in readyObjectives }.forEach { item ->
            reject("item ${item.id}", item.prompt)
            reject("item ${item.id}", item.promptLetter)
            reject("item ${item.id}", item.answer)
            reject("item ${item.id}", item.feedback)
            item.options.forEach { reject("item ${item.id}", it) }
            item.distractorRationales.forEach { reject("item ${item.id}", it) }
        }
        pack.examples.filter { it.objectiveId in readyObjectives }.forEach { example ->
            reject("example ${example.id}", example.display)
            reject("example ${example.id}", example.note)
        }
    }

    /**
     * Batch A gloss checks must describe the keyed shown form and answer.
     * A swapped gloss, or a numbered "other gloss" rationale, fails here.
     */
    private fun keyedGlossErrors(item: V2CheckItem): List<String> {
        if (item.objectiveId !in BATCH_A_OBJECTIVES && item.objectiveId !in BATCH_B_OBJECTIVES) return emptyList()
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
            citationTexts(original).any { text ->
                text.length >= 12 && text.windowed(12).any { window ->
                    window.any { it.isLetter() } && window in why
                }
            }
        }
    }

    /** Emphasis marks are mechanical wiki markup. The self line's equation is contiguous only after they are removed. */
    private fun citationTexts(original: String): List<String> =
        listOf(original, original.replace("'''", "").replace("''", ""))

    /**
     * The self sentence is paired with its own translation.
     * A wife-line window, or the isolated reflexive token, does not validate that pair.
     */
    private fun reflexiveRelationshipErrors(item: V2CheckItem): List<String> {
        if (item.objectiveId != "obj-u21-object-reference") return emptyList()
        val shown = item.promptLetter.orEmpty()
        val why = item.assessment?.whyAnswerDemonstratesObjective.orEmpty()
        val feedback = item.feedback
        val errors = mutableListOf<String>()
        if (shown == BatchBSources.ISOLATED_REFLEXIVE) {
            errors += "Item ${item.id} isolated reflexive token does not inherit the sentence translation"
        }
        if (shown == BatchBSources.SELF_SENTENCE) {
            if (!why.contains(BatchBSources.SELF_LINE_CITE) || !feedback.contains(BatchBSources.SELF_LINE_CITE)) {
                errors += "Item ${item.id} self relationship citation is not the self line"
            }
            if (why.contains(BatchBSources.WIFE_LINE_CITE) || feedback.contains(BatchBSources.WIFE_LINE_CITE)) {
                errors += "Item ${item.id} wife-line citation does not validate the self relationship"
            }
        }
        if (shown == BatchBSources.WIFE_SENTENCE) {
            if (!why.contains(BatchBSources.WIFE_LINE_CITE) || !feedback.contains(BatchBSources.WIFE_LINE_CITE)) {
                errors += "Item ${item.id} wife relationship citation is not the wife line"
            }
            if (why.contains(BatchBSources.SELF_LINE_CITE) || feedback.contains(BatchBSources.SELF_LINE_CITE)) {
                errors += "Item ${item.id} self-line citation does not validate the wife relationship"
            }
        }
        return errors
    }

    /**
     * The invariance prompt names one position in the frozen sentence.
     * The gloss table does not make a co-occurring clause the answer.
     */
    private fun invarianceRelationshipErrors(pack: V2CurriculumPack, item: V2CheckItem): List<String> {
        if (item.objectiveId != "obj-u7-adverb-invariance") return emptyList()
        val errors = mutableListOf<String>()
        val assessment = item.assessment
        val why = assessment?.whyAnswerDemonstratesObjective.orEmpty()
        if (
            !item.prompt.contains(BatchARemainingSources.INVARIANCE_RELATION) ||
            !why.contains(BatchARemainingSources.INVARIANCE_DASH_LINE) ||
            !item.feedback.contains(BatchARemainingSources.INVARIANCE_DASH_LINE)
        ) {
            errors += "Item ${item.id} precise dash-colon relationship wording is missing"
        }
        val cueId = assessment?.relationParticipantIds?.firstOrNull()
        val cue = cueId?.let { id -> pack.provenanceRecords.firstOrNull { it.itemId == id } }
        val span = cue?.originalSourceText ?: BatchARemainingSources.INVARIANCE_SPAN
        val wording = BatchARemainingSources.dashColonWording(span)
        if (wording == null || item.answer != wording || item.options.count { it == wording } != 1) {
            errors += "Item ${item.id} answer is not the wording immediately after the dash and before the colon"
        }
        val colon = span.indexOf(':')
        if (wording != null && item.answer != wording && colon >= 0 && span.indexOf(item.answer, colon) >= 0) {
            errors += "Item ${item.id} answer co-occurs after the colon and does not satisfy the dash-colon relationship"
        }
        if (assessment != null) {
            item.options.forEachIndexed { index, option ->
                if (option == item.answer) return@forEachIndexed
                val continuation = BatchARemainingSources.INVARIANCE_POST_COLON[option] ?: return@forEachIndexed
                val rationale = item.distractorRationales.getOrElse(index) { "" }
                val difference = assessment.distractorDifference.getOrElse(index) { "" }
                if (
                    !rationale.contains(continuation) ||
                    !difference.contains(continuation) ||
                    !rationale.contains("after the colon")
                ) {
                    errors += "Item ${item.id} distractor rationale does not explain the post-colon position"
                }
            }
        }
        return errors
    }

    private fun explicitTargetErrors(pack: V2CurriculumPack, item: V2CheckItem): List<String> {
        val errors = mutableListOf<String>()
        errors += invarianceRelationshipErrors(pack, item)
        val assessment = item.assessment ?: return listOf("Item ${item.id} has no explicit target") + errors
        val feature = assessment.testedFeature
        val unitNumber = item.objectiveId.removePrefix("obj-u").substringBefore("-").toIntOrNull()
        if (unitNumber in 21..30 || item.objectiveId in BATCH_A_OBJECTIVES) {
            errors += observableDistinctionErrors(pack, item)
        } else {
            val phrase = TARGET_PHRASES[feature]
            // A meaning check must not reveal its English answer in the question.
            // Keep this alternative limited to the three source-bound Unit 16 prepositions.
            val prepositionMeaningTarget = item.objectiveId == "obj-u16-inst-prep" &&
                feature in setOf("OVER_LABEL", "UNDER_LABEL", "BEFORE_LABEL") &&
                item.prompt == "Which English spatial meaning fits the shown preposition?"
            val prefixMeaningTarget = item.objectiveId == "obj-u19-prefix-caution" &&
                feature in setOf("DISSUADE_MEANING", "AGREE_MEANING") &&
                item.prompt == "Which English meaning matches the shown Russian verb?"
            if (feature.isBlank() || phrase == null || (!item.prompt.contains(phrase) && !prepositionMeaningTarget && !prefixMeaningTarget)) {
                errors += "Item ${item.id} has no explicit target"
            }
            if (item.objectiveId == "obj-u16-inst-prep" && item.prompt.contains(item.answer, ignoreCase = true)) {
                errors += "Item ${item.id} prompt discloses the preposition meaning"
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
        // Structural check for every development or installed unit. The release
        // gate runs without Android BuildConfig, so the debug default is not used.
        pack.units.filter { it.openable(developmentVisible = true) }.forEach { unit ->
            unit.sessions.forEach { session ->
                if (session.steps.isEmpty()) {
                    errors += "Session ${session.id} is not reachable because it has no steps"
                }
            }
        }
    }

    /** Curated interpretation of the retained lexical field; mutable synthesis cannot reverse it. */
    private fun validateIndeclinabilityBinding(pack: V2CurriculumPack, item: V2CheckItem, errors: MutableList<String>) {
        val cues = mapOf(
            "u5-i-indecl-радио" to "ра́дио", "u5-i-indecl-такси" to "такси́",
            "u5-i-indecl-метро" to "метро́", "u5-i-indecl-кино" to "кино́",
            "u5-i-decl-книга" to "кни́га", "u5-i-decl-окно" to "окно́",
        )
        val cue = cues[item.id]
        if (cue == null) {
            if (item.kind == "SOURCE_INDECLINABLE") errors += "Item ${item.id} has no curated indeclinability binding"
            return
        }
        val unchanged = "Indeclinable: unchanged for case or number"
        val changeable = "Declinable: can change for case"
        val flag = if (item.id.startsWith("u5-i-indecl-")) "1" else "0"
        val lemma = item.id.substringAfterLast('-')
        fun admittedCell(field: String, original: String, shown: String) = pack.provenanceRecords.any {
            it.sourceId == OPENRUSSIAN_SOURCE && it.sourceRevision == "50e210c4803237779cb562bc1abcea529066031c" &&
                it.unitNumber == 5 && it.objectiveId == "obj-u5-indeclinable" &&
                it.sourceRecordId == "nouns.csv:$lemma#$field" && it.originalSourceText == original &&
                it.shippedText == shown && verifiedFrozen(it)
        }
        val definitionBound = pack.provenanceRecords.any {
            it.itemId == "prov-u5-indecl-rule" && it.sourceRevision == "4213267" &&
                it.sourceId == "src-wikibooks-russian-cc-by-sa" &&
                it.sourceHash == "6bec55fd46db6aa454810912d195369f920b099dcfa87cd2b1992b0ed7ec95d6" && verifiedFrozen(it)
        }
        if (item.kind != "SOURCE_INDECLINABLE" || item.objectiveId != "obj-u5-indeclinable" ||
            item.sourceId != OPENRUSSIAN_SOURCE || item.promptLetter != cue ||
            item.options != listOf(unchanged, changeable) || item.answer != (if (flag == "1") unchanged else changeable) ||
            !admittedCell("indeclinable", flag, flag) || !admittedCell("accented", cue.replace("́", "'"), cue) || !definitionBound
        ) errors += "Item ${item.id} indeclinability binding does not match the frozen noun and definition"
    }

    private fun validateItem(pack: V2CurriculumPack, item: V2CheckItem, errors: MutableList<String>) {
        if (CompleteRuleContextPins.required(item.id) && !CompleteRuleContextPins.itemMatches(pack, item)) {
            errors += "Item ${item.id} complete rule context does not match its frozen application"
        }
        validateIndeclinabilityBinding(pack, item, errors)
        if (item.promptLetterRole !in setOf("CUE", "ANSWER_REFERENCE") ||
            (item.promptLetterRole == "ANSWER_REFERENCE" &&
                (item.kind != "SOURCE_QUESTION" || item.promptLetter != item.answer))) {
            errors += "Item ${item.id} has an invalid cue role"
        }
        if (item.kind == "SOURCE_QUESTION" && item.promptLetter == item.answer && item.promptLetterRole == "CUE") {
            errors += "Item ${item.id} discloses its answer as a question cue"
        }
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
            if (governedUnit in 6..15 || governedUnit in 21..30 || item.objectiveId in BATCH_A_OBJECTIVES ||
                item.objectiveId == "obj-u16-inst-prep") {
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
            if (!retainedWorkLicense(record.licenseId, record.sourceId) || record.licenseUrl.isBlank()) {
                errors += "Provenance ${record.itemId} incompatible license"
            }
            if (record.sourceId.startsWith("src-readyrussian-") && !admittedReadyRussianProvenance(record)) {
                errors += "Provenance ${record.itemId} ReadyRussian license route is missing"
            }
            if (record.sourceId == RemainingSources.VECHERA_SOURCE && !admittedVecheraProvenance(record)) {
                errors += "Provenance ${record.itemId} Russian Wikisource public-domain route is missing"
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
            if (record.sourceId == "src-wikipedia-russian-grammar-1369617627" && record.unitNumber !in 26..30) {
                if (!record.attribution.contains("Wikipedia:Copyrights oldid 1369466282") ||
                    !record.attribution.contains("CC BY-SA 4.0")
                ) {
                    errors += "Provenance ${record.itemId} Wikipedia license route is missing"
                }
            }
            if (record.sourceId.startsWith("src-ruwiki-")) {
                val copyright = "\u0412\u0438\u043a\u0438\u043f\u0435\u0434\u0438\u044f:\u0410\u0432\u0442\u043e\u0440\u0441\u043a\u0438\u0435 \u043f\u0440\u0430\u0432\u0430 oldid ${BatchARemainingSources.RU_COPYRIGHT_OLDID}"
                val revisionAllowed = record.sourceRevision == BatchARemainingSources.RU_REVISION ||
                    record.sourceRevision in BatchBSources.RU_REVISIONS
                if (!revisionAllowed ||
                    !record.sourceUrl.contains("oldid=${record.sourceRevision}") ||
                    !record.attribution.contains(copyright) ||
                    !record.attribution.contains("CC BY-SA 4.0") ||
                    record.attribution.contains("Wikipedia:Copyrights") ||
                    record.attribution.contains("Wiktionary:Copyrights") ||
                    record.attribution.contains("Wikibooks:Copyrights") ||
                    record.audioAssetId != null
                ) {
                    errors += "Provenance ${record.itemId} Russian Wikipedia license route is missing"
                }
            }
            if (record.sourceId.startsWith("src-enwiktionary-") && !admittedWiktionaryProvenance(record)) {
                errors += "Provenance ${record.itemId} Wiktionary license route is missing"
            }
            if (record.sourceId.startsWith("src-pressbooks-")) {
                val expected = BatchARemainingSources.PRESSBOOKS_REVISIONS[record.sourceId]
                val otherRevision = BatchARemainingSources.PRESSBOOKS_REVISIONS
                    .filterKeys { it != record.sourceId }
                    .values
                    .any { record.attribution.contains(it) || record.sourceRevision == it }
                if (expected == null ||
                    record.sourceRevision != expected ||
                    !record.attribution.contains(expected) ||
                    otherRevision ||
                    !record.attribution.contains("Anna Tumarkin") ||
                    !record.attribution.contains("Shannon Donnally Quinn") ||
                    !record.attribution.contains("CC BY-SA 4.0") ||
                    record.attribution.contains("Wikipedia:Copyrights") ||
                    record.attribution.contains("Wiktionary:Copyrights") ||
                    record.attribution.contains("Wikibooks:Copyrights") ||
                    record.audioAssetId != null
                ) {
                    errors += "Provenance ${record.itemId} Pressbooks license route is missing"
                }
            }
            if (isExcludedProvenance(record)) {
                errors += "Provenance ${record.itemId} excluded source cannot enter the seed"
            }
            if (record.contentHash != sha256(record.shippedText) || record.sourceHash != sha256(record.originalSourceText)) {
                errors += "Provenance ${record.itemId} source hash does not match the frozen record"
            }
            if (record.sourceId == Phase2Sources.SOURCE_ID || record.transformation == Phase2Sources.TRANSFORMATION) {
                errors += originalSynthesisProvenanceErrors(record)
            }
            if (record.sourceId == ContentAuditSources.SOURCE_ID || record.transformation == ContentAuditSources.TRANSFORMATION) {
                errors += contentAuditProvenanceErrors(record)
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
            if (rights.isEmpty() || rights.any { !retainedWorkLicense(it.license, record.sourceId) }) {
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
            val shippedForUnit = pack.provenanceRecords
                .filter { it.unitNumber == record.unitNumber }
                .map { it.shippedText }
                .toSet()
            val quoted = cyrillicRegions(record.text)
            if (quoted.any { !admittedRegion(it, shippedForUnit) } ||
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

    /**
     * A content-audit correction keeps its research note frozen for this revision
     * and does not record a final audit. Coverage spans may omit applications.
     * A contrast cue must state the application inside that same note.
     */
    private fun contentAuditProvenanceErrors(record: V2ProvenanceRecord): List<String> {
        val errors = mutableListOf<String>()
        if (!CompleteRuleContextPins.matchesIfPinned(record)) {
            errors += "Provenance ${record.itemId} complete rule context does not match its frozen application"
        }
        if (record.sourceId != ContentAuditSources.SOURCE_ID ||
            record.transformation != ContentAuditSources.TRANSFORMATION ||
            record.sourceRevision != ContentAuditSources.REVISION ||
            record.licenseId != ContentAuditSources.LICENSE ||
            record.shippedText.isBlank() ||
            record.shippedText !in record.originalSourceText
        ) {
            errors += "Provenance ${record.itemId} content-audit identity does not match"
        }
        val synthesis = record.synthesis
            ?: return errors + "Provenance ${record.itemId} content-audit evidence is incomplete"
        if (synthesis.bibliography.isBlank() ||
            synthesis.authorshipDeclaration.isBlank() ||
            synthesis.objectiveOrClaimBinding.isBlank()
        ) {
            errors += "Provenance ${record.itemId} content-audit evidence is incomplete"
        }
        if (synthesis.sourceDateOrVersion != ContentAuditSources.DATE) {
            errors += "Provenance ${record.itemId} content-audit date does not match"
        }
        if (!synthesis.objectiveOrClaimBinding.startsWith(record.objectiveId + ":")) {
            errors += "Provenance ${record.itemId} content-audit binding does not match"
        }
        val audit = synthesis.finalAudit
        if (audit.artifactIdentity.isNotBlank() ||
            audit.artifactDigest.isNotBlank() ||
            audit.auditedContentDigest.isNotBlank() ||
            audit.disposition.isNotBlank() ||
            audit.modelVersion.isNotBlank() ||
            audit.date.isNotBlank()
        ) {
            errors += "Provenance ${record.itemId} records a final audit before the final audit"
        }
        val coverageOnly = record.sourceRecordId.startsWith("ca-coverage:${record.objectiveId}:")
        if (synthesis.applications.isEmpty() && !coverageOnly) {
            errors += "Provenance ${record.itemId} content-audit contrast has no stated applications"
        }
        synthesis.applications.forEach { application ->
            val outside = application.cue.isBlank() ||
                application.answer.isBlank() ||
                application.distractors.isEmpty() ||
                application.cue !in record.originalSourceText ||
                application.answer !in record.originalSourceText ||
                application.distractors.any { it !in record.originalSourceText }
            if (outside) {
                errors += "Provenance ${record.itemId} content-audit application is outside the research note"
            }
        }
        return errors
    }

    /**
     * Original synthesis is admitted only for the Phase 2 source identity.
     * A quoted page cannot borrow the transformation, and the synthesis cannot
     * record a final audit that has not happened.
     */
    private fun originalSynthesisProvenanceErrors(record: V2ProvenanceRecord): List<String> {
        val errors = mutableListOf<String>()
        val synthesis = record.synthesis
        val phase2 = Phase2Sources.ADMISSIONS.firstOrNull {
            it.objectiveId == record.objectiveId && it.unitNumber == record.unitNumber
        }
        val phase3 = Phase3Sources.ADMISSIONS.firstOrNull {
            it.objectiveId == record.objectiveId && it.unitNumber == record.unitNumber
        }
        val expectedRevision = if (phase3 != null) Phase3Sources.REVISION else Phase2Sources.REVISION
        val expectedDate = if (phase3 != null) Phase3Sources.DATE else Phase2Sources.DATE
        val expectedRecord = phase2?.recordId ?: phase3?.recordId
        val expectedHash = phase2?.sourceHash ?: phase3?.sourceHash
        val expectedBinding = phase2?.binding ?: phase3?.binding
        if (record.sourceId != Phase2Sources.SOURCE_ID ||
            record.transformation != Phase2Sources.TRANSFORMATION ||
            record.sourceRevision != expectedRevision ||
            record.licenseId != "Original app text" ||
            expectedRecord == null ||
            record.sourceRecordId != expectedRecord ||
            record.sourceHash != expectedHash
        ) {
            errors += "Provenance ${record.itemId} original synthesis identity does not match"
        }
        if (synthesis == null) {
            errors += "Provenance ${record.itemId} original synthesis evidence is missing"
            return errors
        }
        if (!OriginalApplicationTuplePins.matchesIfPinned(record)) {
            errors += "Provenance ${record.itemId} immutable application tuple identity does not match"
        }
        if (synthesis.bibliography.isBlank() ||
            synthesis.authorshipDeclaration.isBlank() ||
            synthesis.objectiveOrClaimBinding.isBlank()
        ) {
            errors += "Provenance ${record.itemId} original synthesis evidence is incomplete"
        }
        if (synthesis.sourceDateOrVersion != expectedDate) {
            errors += "Provenance ${record.itemId} original synthesis date does not match"
        }
        if (expectedBinding != null && synthesis.objectiveOrClaimBinding != expectedBinding) {
            errors += "Provenance ${record.itemId} synthesis binding does not match"
        }
        val audit = synthesis.finalAudit
        if (audit.artifactIdentity.isNotBlank() ||
            audit.artifactDigest.isNotBlank() ||
            audit.auditedContentDigest.isNotBlank() ||
            audit.disposition.isNotBlank() ||
            audit.modelVersion.isNotBlank() ||
            audit.date.isNotBlank()
        ) {
            errors += "Provenance ${record.itemId} records a final audit before the final audit"
        }
        if (synthesis.applications.isEmpty()) {
            errors += "Provenance ${record.itemId} original synthesis has no stated applications"
        }
        synthesis.applications.forEach { application ->
            val outside = application.cue.isBlank() ||
                application.answer.isBlank() ||
                application.distractors.isEmpty() ||
                application.cue !in record.originalSourceText ||
                application.answer !in record.originalSourceText ||
                application.distractors.any { it !in record.originalSourceText }
            if (outside) {
                errors += "Provenance ${record.itemId} stated application is outside the frozen span"
            }
        }
        if (record.shippedText !in record.originalSourceText) {
            errors += "Provenance ${record.itemId} shipped text is outside the frozen span"
        }
        return errors
    }

    /**
     * Public-domain admission is limited to the frozen Chernyshevsky span.
     * A CC BY-SA label does not substitute for that work-level basis, and this
     * exception does not admit any other non-share-alike source.
     */
    private fun retainedWorkLicense(license: String, sourceId: String): Boolean {
        if (isShareAlikeLicense(license)) return sourceId != RemainingSources.VECHERA_SOURCE
        if (license == "Original app text" && sourceId == Phase2Sources.SOURCE_ID) return true
        if (license == "Original app text" && sourceId == ContentAuditSources.SOURCE_ID) return true
        return license == "Public domain" && sourceId == RemainingSources.VECHERA_SOURCE
    }

    private fun admittedReadyRussianProvenance(record: V2ProvenanceRecord): Boolean {
        val revision = RemainingSources.READY_REVISIONS[record.sourceId] ?: return false
        if (record.sourceRevision != revision) return false
        if (!record.sourceUrl.contains("readyrussian.org")) return false
        if (!record.attribution.contains("ReadyRussian.org") || !record.attribution.contains(revision)) return false
        if (!record.attribution.contains("CC BY-SA 4.0")) return false
        if (record.attribution.contains("Wikipedia:Copyrights") ||
            record.attribution.contains("Wiktionary:Copyrights") ||
            record.attribution.contains("Wikibooks:Copyrights")
        ) {
            return false
        }
        if (record.audioAssetId != null) return false
        if (record.originalSourceText.contains(".ogg") || record.shippedText.contains(".ogg")) return false
        return isShareAlikeLicense(record.licenseId)
    }

    private fun admittedVecheraProvenance(record: V2ProvenanceRecord): Boolean {
        if (record.sourceRevision != RemainingSources.VECHERA_REVISION) return false
        if (record.licenseId != "Public domain") return false
        if (!record.sourceUrl.contains("wikisource.org") || !record.sourceUrl.contains("oldid=${record.sourceRevision}")) return false
        if (!record.attribution.contains("\u0427\u0435\u0440\u043d\u044b\u0448\u0435\u0432\u0441\u043a\u0438\u0439")) return false
        if (!record.attribution.contains("1889") || !record.attribution.contains("1281")) return false
        if (!record.attribution.contains("5723825")) return false
        if (record.shareAlikeTreatment.contains("CC BY-SA")) return false
        if (!record.shareAlikeTreatment.contains("public domain")) return false
        if (record.attribution.contains("Wikipedia:Copyrights") ||
            record.attribution.contains("Wiktionary:Copyrights") ||
            record.attribution.contains("Wikibooks:Copyrights")
        ) {
            return false
        }
        return record.audioAssetId == null
    }

    private fun admittedWiktionaryProvenance(record: V2ProvenanceRecord): Boolean {
        val revision = when (record.sourceId) {
            BatchARemainingSources.WIKTIONARY_SOURCE -> BatchARemainingSources.WIKTIONARY_REVISION
            BatchBSources.POLTORA_SOURCE -> BatchBSources.POLTORA_REVISION
            else -> RemainingSources.WIKTIONARY_REVISIONS[record.sourceId] ?: return false
        }
        if (record.sourceRevision != revision) return false
        val url = record.sourceUrl
        if (!url.contains("en.wiktionary.org") || !url.contains("oldid=$revision")) return false
        if (!record.attribution.contains("Wiktionary:Copyrights oldid ${BatchARemainingSources.WIKTIONARY_COPYRIGHT_OLDID}")) return false
        if (record.attribution.contains("Wikipedia:Copyrights") || record.attribution.contains("Wikibooks:Copyrights")) return false
        if (record.audioAssetId != null) return false
        if (!isShareAlikeLicense(record.licenseId)) return false
        if (record.originalSourceText.contains(".ogg") || record.shippedText.contains(".ogg") || url.contains(".ogg")) return false
        return true
    }

    private fun admittedWiktionarySource(sourceId: String): Boolean {
        return sourceId == BatchARemainingSources.WIKTIONARY_SOURCE ||
            sourceId == BatchBSources.POLTORA_SOURCE ||
            sourceId in RemainingSources.WIKTIONARY_REVISIONS
    }

    private fun isExcludedSource(vararg parts: String): Boolean {
        if (parts.any { admittedWiktionarySource(it) }) {
            return parts.any { part ->
                val value = part.lowercase()
                listOf(
                    "penguin",
                    "fsi",
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
        return parts.any { excludedSourceToken(it) }
    }

    private fun isExcludedProvenance(record: V2ProvenanceRecord): Boolean {
        if (admittedWiktionarySource(record.sourceId)) {
            return !admittedWiktionaryProvenance(record)
        }
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

    /**
     * Product Owner activation of the Units 27, 29, and 30 pedagogical boundaries.
     * This is not a qualified-reviewer signature and does not approve a mapping.
     */
    internal fun activatedAuthorityVariance(unitNumber: Int): Boolean =
        unitNumber in ACTIVATED_AUTHORITY_VARIANCE_UNITS

    /**
     * Units 1 and 2 teach letter pronunciation, so an installed copy must resolve
     * a unit-specific audio asset linked to a reachable letter. Later units do
     * not inherit that letter-audio rule. They still need their own review,
     * mapping, and any asset that their installed-unit checks name.
     */
    internal fun installationRequiresPronunciationAudio(unitNumber: Int): Boolean = unitNumber in 1..2

    /** Installed Unit 2 is the only unit `validateInstalled` requires a handwriting asset for. */
    internal fun installationRequiresHandwritingAsset(unitNumber: Int): Boolean = unitNumber == 2

    internal fun mappedPronunciationAudioResolves(pack: V2CurriculumPack, unit: V2Unit): Boolean =
        pronunciationAudioResolves(pack, unit)

    internal fun verifiedHandwritingAssetResolves(pack: V2CurriculumPack, unit: V2Unit): Boolean =
        handwritingAssetResolves(pack, unit)
}
