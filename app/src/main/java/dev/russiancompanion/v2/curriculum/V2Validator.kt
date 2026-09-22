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
        validateReview(pack, errors)
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

    internal fun instructionalContentDigest(pack: V2CurriculumPack): String {
        val payload = buildString {
            append(pack.packId)
            append('|')
            append(pack.packVersion)
            pack.letters.forEach { letter ->
                append(letter.id)
                append(letter.uppercase)
                append(letter.lowercase)
            }
            pack.examples.forEach { example ->
                append(example.id)
                append(example.display)
                append(example.objectiveId)
            }
            pack.items.forEach { item ->
                append(item.id)
                append(item.prompt)
                append(item.answer)
                append(item.objectiveId)
            }
            pack.references.forEach { reference ->
                append(reference.id)
                append(reference.classification)
            }
            pack.units.forEach { unit ->
                append(unit.number)
                append(unit.installState)
                unit.objectives.forEach { objective ->
                    append(objective.id)
                    objective.checkItemIds.forEach(::append)
                }
                unit.sessions.forEach { session ->
                    append(session.id)
                    session.steps.forEach { step ->
                        append(step.id)
                        append(step.kind)
                        step.itemIds.forEach(::append)
                        step.exampleIds.forEach(::append)
                    }
                }
            }
        }
        val digest = MessageDigest.getInstance("SHA-256").digest(payload.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { byte -> "%02x".format(byte) }
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
        if (review.packVersion != pack.packVersion) missing += "matching pack version"
        if (review.reviewedContentDigest.isNullOrBlank()) missing += "reviewed content digest"
        if (review.curriculumMappingId.isNullOrBlank()) missing += "curriculum mapping"
        if (missing.isNotEmpty()) {
            errors += "APPROVED review is missing ${missing.joinToString(", ")}"
            return
        }
        val expected = instructionalContentDigest(pack)
        if (review.reviewedContentDigest != expected) {
            errors += "APPROVED review digest does not match the shipped instructional content"
        }
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
        if (pack.linguisticReview.status != "APPROVED") {
            errors += "Installed unit ${unit.number} requires an APPROVED qualified-review record"
        }
        if (unit.mappingStatus != "APPROVED") {
            errors += "Installed unit ${unit.number} mapping must be APPROVED"
        }
        if (unit.sessions.isEmpty()) errors += "Installed unit ${unit.number} has no sessions"
        if (unit.objectives.isEmpty()) errors += "Installed unit ${unit.number} has no objectives"
        if (unit.sessions.any { session -> session.steps.any { it.kind == "UNAVAILABLE" } }) {
            errors += "Installed unit ${unit.number} still has unavailable required experiences"
        }
        if (pack.admissions.handwritingAssets == 0 && unit.number == 2) {
            errors += "Installed Unit 2 requires verified handwriting assets"
        }
        if (pack.admissions.mappedPronunciationAudio == 0) {
            errors += "Installed unit ${unit.number} requires mapped pronunciation audio"
        }
        validateInstalledReviewRecords(pack, unit, errors)
        validateOpenableBody(pack, unit, errors, requireCompleteRecognition = true)
    }

    private fun validateInstalledReviewRecords(
        pack: V2CurriculumPack,
        unit: V2Unit,
        errors: MutableList<String>,
    ) {
        val review = pack.linguisticReview
        if (review.status != "APPROVED" ||
            review.reviewer.isNullOrBlank() ||
            review.packVersion != pack.packVersion ||
            review.reviewedContentDigest != instructionalContentDigest(pack) ||
            review.curriculumMappingId.isNullOrBlank()
        ) {
            errors += "Installed unit ${unit.number} approval must bind to the shipped pack version and instructional-content digest"
        }
        val steps = unit.sessions.flatMap { it.steps }
        val itemIds = (steps.flatMap { it.itemIds } + unit.objectives.flatMap { it.checkItemIds }).toSet()
        val exampleIds = (steps.flatMap { it.exampleIds } + unit.objectives.flatMap { it.exampleIds }).toSet()
        val referenceIds = steps.flatMap { it.referenceIds }.toSet()
        val letterIds = exampleIds.mapNotNull { pack.example(it)?.letterId }.toSet()
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
        if (unit.number !in 1..2) {
            errors += "Only Units 1-2 may be development content in this pack"
        }
        if (unit.mappingStatus != "PENDING_CURRICULUM_REVIEWER") {
            errors += "Unit ${unit.number} mapping must remain pending until a reviewer signs the owned edition"
        }
        if (unit.sessions.isEmpty()) errors += "Development unit ${unit.number} has no sessions"
        if (unit.objectives.isEmpty()) errors += "Development unit ${unit.number} has no objectives"
        validateOpenableBody(pack, unit, errors, requireCompleteRecognition = true)
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
        if (item.isGovernancePrompt()) {
            errors += "Item ${item.id} is a governance or policy prompt, not a Russian-learning check"
        }
        if (item.promptLetter.isNullOrBlank()) {
            errors += "Item ${item.id} has no prompt letter"
        }
    }

    private fun validateExample(pack: V2CurriculumPack, example: V2Example, errors: MutableList<String>) {
        if (pack.letter(example.letterId) == null) {
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
    }

    private fun validateLetter(pack: V2CurriculumPack, letter: V2Letter, errors: MutableList<String>) {
        if (letter.uppercase.length != 1 || letter.lowercase.length != 1) {
            errors += "Letter ${letter.id} is not a single printed pair"
        }
        if (letter.exampleWordId != null) errors += "Letter ${letter.id} has an example word"
        if (letter.audioAssetId != null) errors += "Letter ${letter.id} claims audio that is not intake-reviewed"
        if (pack.sources.none { it.id == letter.sourceId }) {
            errors += "Letter ${letter.id} source ${letter.sourceId} is missing"
        }
        if (letter.reviewStatus !in CLOSED_REVIEW_STATES) {
            errors += "Letter ${letter.id} has unrecognized review status ${letter.reviewStatus}"
        }
    }
}
