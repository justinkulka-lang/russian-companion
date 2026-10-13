package dev.russiancompanion.v2.curriculum

import java.security.MessageDigest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/** Pending research intake is distinct from the final linguistic review chain. */
internal fun correctedCapabilityDefects(pack: V2CurriculumPack, cap: V2SourceCapability): List<String> {
    val errors = mutableListOf<String>()
    fun fail(reason: String) { errors += "Source capability ${cap.id} corrected intake $reason" }
    val frozenPin = ContentAuditCapabilityPins.all.firstOrNull {
        it.recordId == cap.sourceRecordId && it.hash == cap.sourceHash && cap.sourceRevision == ContentAuditSources.REVISION
    }
    if (frozenPin?.claim == "letter-writing" && cap.supportedClaim == "postal-address") {
        fail("letter-writing evidence does not clear postal-address")
    }
    val pin = ContentAuditCapabilityPins.all.firstOrNull { it.id == cap.id }
        ?: return errors + "Source capability ${cap.id} corrected intake is not admitted"
    if (cap.sourceRevision != ContentAuditSources.REVISION || cap.sourceRecordId != pin.recordId ||
        cap.sourceHash != pin.hash || cap.unitNumber != pin.unit || cap.objectiveId != pin.objective ||
        cap.supportedClaim != pin.claim || MessageDigest.getInstance("SHA-256")
            .digest(cap.exactSpan.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) } != pin.hash) {
        return errors + "Source capability ${cap.id} corrected intake identity does not match"
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
    if (pin.unit == 26 && pin.claim == "letter-writing") {
        val guided = practiced.mapNotNull(pack::item)
        if (guided.size != 1 || guided.singleOrNull()?.assessment?.testedFeature != "LETTER_ORGANIZATION" ||
            guided.any { item -> item.assessment?.relationKind != "STATED_APPLICATION" ||
                item.assessment.relationParticipantIds.isEmpty() || item.assessment.relationParticipantIds.any { it !in hostIds } }) {
            fail("is missing its bound guided letter organization application")
        }
        if (independent.size != 2 || independent.mapNotNull { it.assessment?.testedFeature }.toSet() != pin.features) {
            fail("greeting recognition does not clear letter-writing; distinct greeting-inference and layout applications are required")
        }
    }

    if (pin.unit == 26 && pin.claim == "word-order") {
        val guided = practiced.mapNotNull(pack::item)
        val checks = guided + independent
        if (guided.size != 1 || independent.size != 2 || checks.any { item ->
            item.assessment?.testedFeature != "WORD_ORDER" || item.assessment.relationKind != "STATED_APPLICATION" ||
                item.assessment.relationParticipantIds.isEmpty() || item.assessment.relationParticipantIds.any { it !in hostIds } ||
                item.prompt.isBlank() || item.prompt !in cap.exactSpan
        }) fail("is missing its exact guided and independent contextual word-order applications")
    }
    if (pin.unit == 26 && pin.claim == "postal-address") {
        val guided = practiced.mapNotNull(pack::item)
        val checks = guided + independent
        // Bind the applications themselves, not just their words, to the hashed corpus.
        val frozenApplications = runCatching {
            Json.decodeFromString<List<V2SynthesisApplication>>(cap.exactSpan.substringAfterLast('\n'))
        }.getOrNull()
        val applicationsBound = frozenApplications?.size == checks.size &&
            hosts.all { it.synthesis?.applications == frozenApplications } &&
            checks.all { item -> frozenApplications?.count { application ->
                application.cue == item.promptLetter && application.answer == item.answer &&
                    application.distractors == item.options.filter { it != item.answer }
            } == 1 }
        if (!applicationsBound) {
            fail("is missing its exact guided and independent contextual postal applications")
        }
        if (guided.size != 1 || guided.singleOrNull()?.assessment?.testedFeature != "LOCALITY_ROLE" ||
            independent.size != 11 || independent.mapNotNull { it.assessment?.testedFeature }.toSet() != pin.features ||
            checks.any { item -> item.assessment?.relationKind != "STATED_APPLICATION" ||
                item.assessment.relationParticipantIds.isEmpty() || item.assessment.relationParticipantIds.any { it !in hostIds } ||
                item.prompt.isBlank() || item.prompt !in cap.exactSpan }) {
            fail("is missing its exact guided and independent contextual postal applications")
        }
        if (checks.map { it.prompt }.distinct().size != checks.size) {
            fail("duplicate assessment checks do not clear postal-address")
        }
    }
    if ((pin.unit == 27 && pin.claim == "participle-comprehension") ||
        (pin.unit == 28 && pin.claim in setOf("imperfective-verbal-adverb", "perfective-verbal-adverb"))) {
        // Contextual cues and answers must stay paired in the hashed corpus, not mutable metadata.
        val frozenApplications = runCatching {
            Json.decodeFromString<List<V2SynthesisApplication>>(cap.exactSpan.substringAfterLast('\n'))
        }.getOrNull()
        val contextualChecks = if (pin.unit == 28) practiced.mapNotNull(pack::item) + independent else independent
        if (pin.unit == 28 && (practiced.size != 1 || independent.size != 2 ||
            contextualChecks.firstOrNull()?.assessment?.independent != false ||
            contextualChecks.any { item -> item.assessment?.relationParticipantIds.orEmpty().isEmpty() ||
                item.assessment?.relationParticipantIds.orEmpty().any { it !in hostIds } } ||
            independent.any { it.promptLetter == contextualChecks.firstOrNull()?.promptLetter })) {
            fail("is missing its distinct bound guided timing application")
        }
        if (frozenApplications?.size != contextualChecks.size ||
            hosts.any { it.synthesis?.applications != frozenApplications } ||
            contextualChecks.any { item -> frozenApplications?.count { application ->
                application.cue == item.promptLetter && application.answer == item.answer &&
                    application.distractors == item.options.filter { it != item.answer }
            } != 1 }) {
            fail(if (pin.unit == 28) "is missing its exact guided and independent contextual applications" else "is missing its exact independent contextual applications")
        }
    }
    (objective.instructionStepIds + objective.remediationStepIds).forEach { id ->
        val step = pack.step(id)?.second
        if (step == null || step.body.isBlank() || step.body !in cap.exactSpan) fail("has an unbound teaching or remediation step $id")
    }
    return errors.distinct()
}
