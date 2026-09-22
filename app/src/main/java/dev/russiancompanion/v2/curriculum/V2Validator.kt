package dev.russiancompanion.v2.curriculum

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
        if (pack.linguisticReview.status == "APPROVED") {
            errors += "A fabricated or premature linguistic approval is not accepted in this pack"
        }
        pack.units.forEach { unit ->
            if (unit.penguinLesson != unit.number) {
                errors += "Unit ${unit.number} penguinLesson must equal the unit number"
            }
            when (unit.installState) {
                "INSTALLED" -> validateInstalled(pack, unit, errors)
                "NOT_YET_INSTALLED" -> validateUninstalled(unit, errors)
                else -> errors += "Unit ${unit.number} has unknown installState ${unit.installState}"
            }
        }
        pack.items.forEach { item ->
            if (item.containsGeneratedRussian) errors += "Item ${item.id} marks generated Russian"
            if (item.language != "en") errors += "Item ${item.id} is not independently authored English"
            if (item.options.isEmpty() || item.answer !in item.options) {
                errors += "Item ${item.id} has no usable answer key"
            }
            if (pack.units.flatMap { it.objectives }.none { it.id == item.objectiveId }) {
                errors += "Item ${item.id} objective is missing"
            }
        }
        pack.letters.forEach { letter ->
            if (letter.uppercase.length != 1 || letter.lowercase.length != 1) {
                errors += "Letter ${letter.id} is not a single printed pair"
            }
            if (letter.exampleWordId != null) errors += "Letter ${letter.id} has an example word"
            if (letter.audioAssetId != null) errors += "Letter ${letter.id} claims audio that is not intake-reviewed"
        }
        return V2Validation(errors.isEmpty(), errors)
    }

    private fun validateInstalled(pack: V2CurriculumPack, unit: V2Unit, errors: MutableList<String>) {
        if (unit.number !in 1..2) {
            errors += "Only Units 1-2 may be installed in this pack"
        }
        if (unit.sessions.isEmpty()) errors += "Installed unit ${unit.number} has no sessions"
        if (unit.objectives.isEmpty()) errors += "Installed unit ${unit.number} has no objectives"
        if (unit.mappingStatus != "PENDING_CURRICULUM_REVIEWER") {
            errors += "Unit ${unit.number} mapping must remain pending until a reviewer signs the owned edition"
        }
        unit.sessions.forEach { session ->
            if (session.steps.isEmpty()) errors += "Session ${session.id} has no steps"
            session.steps.forEach { step ->
                step.itemIds.forEach { itemId ->
                    if (pack.item(itemId) == null) errors += "Step ${step.id} references missing item $itemId"
                }
                step.referenceIds.forEach { refId ->
                    if (pack.reference(refId) == null) errors += "Step ${step.id} references missing entry $refId"
                }
                step.remediationStepId?.let { rem ->
                    if (pack.step(rem) == null) errors += "Step ${step.id} remediation $rem is missing"
                }
            }
        }
    }

    private fun validateUninstalled(unit: V2Unit, errors: MutableList<String>) {
        if (unit.sessions.isNotEmpty()) errors += "Unit ${unit.number} is not installed but has sessions"
        if (unit.objectives.isNotEmpty()) errors += "Unit ${unit.number} is not installed but has objectives"
        if (unit.advancement != null) errors += "Unit ${unit.number} is not installed but has advancement"
    }
}
