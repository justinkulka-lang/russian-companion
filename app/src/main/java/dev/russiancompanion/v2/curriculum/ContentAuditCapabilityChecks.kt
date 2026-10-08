package dev.russiancompanion.v2.curriculum

import java.security.MessageDigest

/** Pending research intake is distinct from the final linguistic review chain. */
internal fun correctedCapabilityDefects(pack: V2CurriculumPack, cap: V2SourceCapability): List<String> {
    val errors = mutableListOf<String>()
    fun fail(reason: String) { errors += "Source capability ${cap.id} corrected intake $reason" }
    val pin = ContentAuditCapabilityPins.all.firstOrNull { it.id == cap.id }
        ?: return listOf("Source capability ${cap.id} corrected intake is not admitted")
    if (cap.sourceRevision != ContentAuditSources.REVISION || cap.sourceRecordId != pin.recordId ||
        cap.sourceHash != pin.hash || cap.unitNumber != pin.unit || cap.objectiveId != pin.objective ||
        cap.supportedClaim != pin.claim || MessageDigest.getInstance("SHA-256")
            .digest(cap.exactSpan.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) } != pin.hash) {
        return listOf("Source capability ${cap.id} corrected intake identity does not match")
    }
    val hosts = pack.provenanceRecords.filter {
        it.sourceId == ContentAuditSources.SOURCE_ID && it.sourceRevision == cap.sourceRevision &&
            it.sourceRecordId == pin.recordId && it.sourceHash == pin.hash &&
            it.originalSourceText == cap.exactSpan && it.unitNumber == pin.unit &&
            it.objectiveId == pin.objective && it.licenseId == ContentAuditSources.LICENSE
    }
    if (hosts.isEmpty()) fail("has no frozen provenance")
    val binding = pin.objective + ":" + pin.claim
    hosts.forEach { record ->
        val synthesis = record.synthesis
        if (synthesis == null || synthesis.bibliography.isBlank() || synthesis.authorshipDeclaration.isBlank() ||
            synthesis.objectiveOrClaimBinding != binding || synthesis.sourceDateOrVersion != ContentAuditSources.DATE ||
            synthesis.applications.isEmpty()) fail("has incomplete research evidence")
        val audit = synthesis?.finalAudit
        if (audit != null && (audit.artifactIdentity.isNotBlank() || audit.artifactDigest.isNotBlank() ||
                audit.auditedContentDigest.isNotBlank() || audit.disposition.isNotBlank() ||
                audit.modelVersion.isNotBlank() || audit.date.isNotBlank())) fail("claims an unperformed final review")
    }
    val unit = pack.unit(pin.unit)
    val objective = unit?.objectives?.firstOrNull { it.id == pin.objective }
        ?: return errors + "Source capability ${cap.id} corrected intake objective is missing"
    if (objective.kind != "RECOGNITION" || objective.exampleIds.size < 2 || objective.instructionStepIds.isEmpty() ||
        objective.practiceStepIds.isEmpty() || objective.remediationStepIds.isEmpty()) fail("has an incomplete instructional chain")
    val hostIds = hosts.map { it.itemId }.toSet()
    val independent = objective.checkItemIds.mapNotNull(pack::item).filter { item ->
        item.assessment?.independent == true &&
            item.assessment.relationParticipantIds.isNotEmpty() &&
            item.assessment.relationParticipantIds.all { it in hostIds }
    }
    if (!independent.mapNotNull { it.assessment?.testedFeature }.toSet().containsAll(pin.features)) {
        fail("is missing its named independent applications")
    }
    val shapes = independent.map { Triple(it.promptLetter, it.options, it.answer) }
    if (shapes.size != shapes.toSet().size) fail("duplicates its independent check")
    independent.forEach { item ->
        if (!V2Validator.featureValid(pack, item)) fail("has an invalid independent application ${item.id}")
        if (item.assessment?.relationKind != "STATED_APPLICATION") fail("has an unstated application ${item.id}")
        if (!item.feedback.contains(item.answer)) fail("does not explain its answer ${item.id}")
        item.options.forEachIndexed { index, option ->
            if (option != item.answer && !item.distractorRationales.getOrElse(index) { "" }.contains(option)) {
                fail("does not explain its competing option ${item.id}")
            }
        }
    }
    val practiced = objective.practiceStepIds.flatMap { pack.step(it)?.second?.itemIds.orEmpty() }
    if (practiced.isEmpty()) fail("has no guided practice")
    (objective.instructionStepIds + objective.remediationStepIds).forEach { id ->
        val step = pack.step(id)?.second
        if (step == null || step.body.isBlank() || step.body !in cap.exactSpan) fail("has an unbound teaching or remediation step $id")
    }
    return errors.distinct()
}
