package dev.russiancompanion.v2.curriculum

import dev.russiancompanion.v2.data.V2ProfileEntity
import java.security.MessageDigest

/** Identity schema written on new attempts, bookmarks, notes, and resume positions. */
internal const val GOVERNED_IDENTITY_SCHEMA = 1

/**
 * Canonical step identity. Schema 1 hashed only the step id, kind, title,
 * body, and item digests. Schema 2 added instructional text with ambiguous
 * newline joins. Schema 3 is an unambiguous length-prefixed encoding and
 * binds the displayed session id, title, and objective ids. A stored schema
 * 1 or 2 digest is not rewritten and does not count as the current step.
 * Session estimated minutes are a schedule budget and are outside this identity.
 */
internal const val STEP_IDENTITY_SCHEMA = 3

/**
 * Legacy and unverifiable rows keep this sentinel. It is not a content hash and
 * must not be treated as a match for the current pack.
 */
internal const val UNRECOVERABLE_GOVERNED_IDENTITY = "UNRECOVERABLE"

internal const val PRESCRIBED_CHECK = "PRESCRIBED_CHECK"
internal const val OPTIONAL_PRACTICE = "OPTIONAL_PRACTICE"
internal const val LEGACY_UNVERIFIED = "LEGACY_UNVERIFIED"

internal const val CLOSED_RESPONSE_RULE_ID = "closed-response-exact-option"

/**
 * Guided-practice routes that still have no reviewed item.
 * The six foundation and nominative gaps now have their own guided steps, so this set is empty.
 * Do not put a step here to hide a missing item.
 */
internal val GUIDED_PRACTICE_GAP_STEP_IDS = emptySet<String>()

/** Concepts that previously had no guided route. Tests require a real guided item for each. */
internal val FILLED_GUIDED_PRACTICE_ROUTES = mapOf(
    "u1-st-confusable" to "u1-st-guided-confusable",
    "u1-st-iotated" to "u1-st-guided-iotated",
    "u1-st-signs" to "u1-st-guided-signs",
    "u1-st-print" to "u1-st-guided-print",
    "u1-st-decoding" to "u1-st-guided-decoding",
    "u4-st-rem-nominative" to "u4-st-guided-nominative",
)

private const val CLOSED_RESPONSE_RULE_DOCUMENT =
    "{\"id\":\"closed-response-exact-option\",\"schema\":1,\"comparison\":\"selected option equals the reviewed answer\",\"asr\":false,\"pronunciationScore\":false}"

internal data class GovernedAttemptView(
    val id: String,
    val itemId: String,
    val selected: String,
    val correct: Boolean,
    val recordedAtLocal: String,
    val itemDigest: String,
    val unitContentDigest: String,
    val ruleDigest: String,
    val identitySchema: Int,
    val attemptRole: String,
    val objectiveId: String,
    val unitNumber: Int,
    val stepId: String,
    /** 0 means the historical insertion order was not recorded and must not be invented. */
    val acceptedOrder: Long = 0,
)

internal data class GovernedBookmarkView(
    val targetId: String,
    val kind: String,
    val createdAtLocal: String,
    val targetDigest: String,
    val identitySchema: Int,
)

internal data class GovernedNoteView(
    val id: String,
    val targetId: String,
    val text: String,
    val createdAtLocal: String,
    val targetDigest: String,
    val identitySchema: Int,
)

internal data class GovernedTutorView(
    val id: String,
    val targetId: String,
    val note: String,
    val createdAtLocal: String,
    val targetDigest: String,
    val identitySchema: Int,
)

internal data class GovernedAttemptWrite(
    val id: String,
    val itemId: String,
    val selected: String,
    val correct: Boolean,
    val recordedAtLocal: String,
    val itemDigest: String,
    val unitContentDigest: String,
    val ruleDigest: String,
    val identitySchema: Int,
    val attemptRole: String,
    val objectiveId: String,
    val unitNumber: Int,
    val stepId: String,
)

internal data class PracticeOffer(
    val id: String,
    val title: String,
    val detail: String,
    val runnable: Boolean,
    val blockedReason: String?,
)

internal data class SavedWorkEntry(
    val id: String,
    val title: String,
    val detail: String,
    val kind: String,
    val resumable: Boolean,
    val sessionId: String? = null,
    val stepIndex: Int = 0,
    val referenceId: String? = null,
)

internal fun closedResponseRuleDigest(): String = sha256Hex(CLOSED_RESPONSE_RULE_DOCUMENT)

/** Length-prefixed text. Field boundaries stay unambiguous when a value contains separators. */
internal fun canonicalText(value: String): String {
    val size = value.toByteArray(Charsets.UTF_8).size
    return "$size:$value"
}

internal fun canonicalParts(parts: List<String>): String {
    return parts.size.toString() + "{" + parts.joinToString(";") { canonicalText(it) } + "}"
}

internal fun stepContentDigest(pack: V2CurriculumPack, step: V2Step): String {
    V2PackIdentityIndex.stepDigest(pack, step.id)?.let { return it }
    return rawStepContentDigest(pack, step)
}

internal fun rawStepContentDigest(pack: V2CurriculumPack, step: V2Step): String {
    val items = canonicalParts(step.itemIds.map { id ->
        val item = pack.item(id)
        if (item == null) canonicalText("missing:$id") else V2Validator.checkItemContentDigest(item)
    })
    val examples = canonicalParts(step.exampleIds.map { id ->
        val example = pack.example(id)
        if (example == null) {
            canonicalText("missing:$id")
        } else {
            canonicalParts(
                listOf(
                    example.id,
                    example.kind,
                    example.letterId,
                    example.display,
                    example.note,
                    example.objectiveId,
                    example.sourceId,
                ),
            )
        }
    })
    val references = canonicalParts(step.referenceIds.map { id ->
        val entry = pack.reference(id)
        if (entry == null) canonicalText("missing:$id") else rawReferenceContentDigest(entry)
    })
    val session = pack.step(step.id)?.first
    val sessionContext = if (session == null) {
        canonicalParts(listOf("session-absent"))
    } else {
        canonicalParts(listOf(session.id, session.title, canonicalParts(session.objectiveIds)))
    }
    val relationships = canonicalParts(pack.units.flatMap { it.objectives }.mapNotNull { objective ->
        val roles = listOfNotNull(
            "instruction".takeIf { step.id in objective.instructionStepIds },
            "practice".takeIf { step.id in objective.practiceStepIds },
            "remediation".takeIf { step.id in objective.remediationStepIds },
        )
        if (roles.isEmpty()) null else canonicalParts(listOf(objective.id, canonicalParts(roles)))
    }.sorted())
    return sha256Hex(
        canonicalParts(
            listOf(
                "step-identity-schema:$STEP_IDENTITY_SCHEMA",
                step.id,
                step.kind,
                step.title,
                step.body,
                step.dominantAction,
                step.bookDirection.orEmpty(),
                step.companionDirection.orEmpty(),
                step.unavailableReason.orEmpty(),
                step.remediationStepId.orEmpty(),
                items,
                examples,
                references,
                sessionContext,
                relationships,
            ),
        ),
    )
}

internal fun resumePositionDigest(pack: V2CurriculumPack, sessionId: String, stepIndex: Int): String? {
    val session = pack.session(sessionId) ?: return null
    val step = session.steps.getOrNull(stepIndex) ?: return null
    return stepContentDigest(pack, step)
}

internal fun referenceContentDigest(entry: V2ReferenceEntry): String = rawReferenceContentDigest(entry)

internal fun rawReferenceContentDigest(entry: V2ReferenceEntry): String {
    return sha256Hex(
        canonicalParts(listOf("reference-identity-schema:1", entry.id, entry.title, entry.summary, entry.detail)),
    )
}

internal fun targetContentDigest(pack: V2CurriculumPack, targetId: String, kind: String): String? {
    return when (kind) {
        "STEP" -> pack.step(targetId)?.second?.let { stepContentDigest(pack, it) }
        "REFERENCE" -> pack.reference(targetId)?.let { referenceContentDigest(it) }
        else -> null
    }
}

internal fun V2ProfileEntity.verifiedResume(pack: V2CurriculumPack): V2ProfileEntity {
    val sessionId = currentSessionId ?: return copy(resumeContentDigest = "", resumeIdentitySchema = 0)
    val digest = resumePositionDigest(pack, sessionId, currentStepIndex) ?: return this
    return copy(resumeContentDigest = digest, resumeIdentitySchema = GOVERNED_IDENTITY_SCHEMA)
}

internal fun resumeIsCurrent(pack: V2CurriculumPack, learner: V2LearnerView): Boolean {
    if (learner.resumeIdentitySchema != GOVERNED_IDENTITY_SCHEMA) return false
    val sessionId = learner.currentSessionId ?: return false
    if (learner.resumeContentDigest.isBlank() || learner.resumeContentDigest == UNRECOVERABLE_GOVERNED_IDENTITY) {
        return false
    }
    val digest = resumePositionDigest(pack, sessionId, learner.currentStepIndex) ?: return false
    return digest == learner.resumeContentDigest
}

internal fun prepareGovernedAttempt(
    pack: V2CurriculumPack,
    itemId: String,
    selected: String,
    role: String,
    stepId: String,
    recordedAt: String,
): GovernedAttemptWrite? {
    if (pack.schemaVersion != 1) return null
    if (role != PRESCRIBED_CHECK && role != OPTIONAL_PRACTICE) return null
    if (stepId.isBlank() || recordedAt.isBlank() || selected.isBlank()) return null
    val item = pack.item(itemId) ?: return null
    if (item.options.isEmpty() || selected !in item.options || item.answer !in item.options) return null
    val unitNumber = itemUnitNumber(pack, item) ?: return null
    val unit = pack.unit(unitNumber) ?: return null
    if (!unit.openable()) return null
    val itemDigest = V2Validator.checkItemContentDigest(item)
    val unitDigest = V2PackIdentityIndex.unitDigest(pack, unitNumber)
        ?: V2Validator.unitContentReviewDigest(pack, unitNumber)
    val ruleDigest = closedResponseRuleDigest()
    return GovernedAttemptWrite(
        id = "$itemId:$role:$stepId:$recordedAt:${selected.hashCode()}",
        itemId = itemId,
        selected = selected,
        correct = selected == item.answer,
        recordedAtLocal = recordedAt,
        itemDigest = itemDigest,
        unitContentDigest = unitDigest,
        ruleDigest = ruleDigest,
        identitySchema = GOVERNED_IDENTITY_SCHEMA,
        attemptRole = role,
        objectiveId = item.objectiveId,
        unitNumber = unitNumber,
        stepId = stepId,
    )
}

internal fun governedSelectionKey(stepId: String, itemId: String): String = "$stepId\u001f$itemId"

/**
 * A session item counts as answered only when this attempt is a prescribed check
 * for that exact step and every current governed identity matches the pack.
 * An incorrect answer still counts. Legacy, optional, and digest-mismatched rows do not.
 */
internal fun attemptSuppliesStepSelection(
    attempt: GovernedAttemptView,
    pack: V2CurriculumPack,
    stepId: String,
): Boolean {
    if (pack.schemaVersion != 1) return false
    if (attempt.attemptRole != PRESCRIBED_CHECK) return false
    if (attempt.identitySchema != GOVERNED_IDENTITY_SCHEMA) return false
    if (attempt.stepId != stepId || stepId.isBlank()) return false
    if (attempt.selected.isBlank()) return false
    if (attempt.itemDigest == UNRECOVERABLE_GOVERNED_IDENTITY ||
        attempt.ruleDigest == UNRECOVERABLE_GOVERNED_IDENTITY ||
        attempt.unitContentDigest == UNRECOVERABLE_GOVERNED_IDENTITY
    ) {
        return false
    }
    if (attempt.ruleDigest != closedResponseRuleDigest()) return false
    val item = pack.item(attempt.itemId) ?: return false
    if (attempt.selected !in item.options) return false
    if (attempt.itemDigest != V2Validator.checkItemContentDigest(item)) return false
    val unitNumber = itemUnitNumber(pack, item) ?: return false
    if (attempt.unitNumber != unitNumber || pack.unit(unitNumber) == null) return false
    if (!prescribedUnitIdentityMatches(pack, unitNumber, attempt.unitContentDigest)) return false
    val step = pack.step(stepId)?.second ?: return false
    return attempt.itemId in step.itemIds
}

/**
 * The latest sequenced prescribed attempt wins. Rows with acceptedOrder 0 have
 * no recorded insertion order and do not become the current selection.
 */
internal fun governedSessionSelections(pack: V2CurriculumPack, learner: V2LearnerView): Map<String, String> {
    val attempts = learner.governedAttempts ?: return emptyMap()
    val selections = linkedMapOf<String, String>()
    attempts.filter { it.acceptedOrder > 0 }
        .sortedBy { it.acceptedOrder }
        .forEach { attempt ->
            if (attemptSuppliesStepSelection(attempt, pack, attempt.stepId)) {
                selections[governedSelectionKey(attempt.stepId, attempt.itemId)] = attempt.selected
            }
        }
    return selections
}

internal fun stepReadyForGovernedCompletion(
    pack: V2CurriculumPack,
    learner: V2LearnerView,
    stepId: String,
): Boolean {
    val step = pack.step(stepId)?.second ?: return false
    val requiredItemIds = stepCompletionItemIds(pack, stepId)
    if (requiredItemIds.isEmpty()) return true
    val selections = governedSessionSelections(pack, learner)
    return requiredItemIds.all { itemId -> governedSelectionKey(stepId, itemId) in selections }
}

/** Optional questions in a required session do not become a step-completion obligation. */
internal fun stepCompletionItemIds(pack: V2CurriculumPack, stepId: String): List<String> {
    val (session, step) = pack.step(stepId) ?: return emptyList()
    val unit = pack.units.firstOrNull { candidate -> candidate.sessions.any { it.id == session.id } }
        ?: return step.itemIds
    val optionalIds = unit.objectives.filter { !it.gating }.map { it.id }.toSet()
    if (session.objectiveIds.isNotEmpty() && session.objectiveIds.all { it in optionalIds }) return step.itemIds
    return step.itemIds.filter { pack.item(it)?.objectiveId !in optionalIds }
}

/** Set by governed completion so tests can see that identity work left the UI thread. */
@Volatile
internal var lastGovernedIdentityThreadForTests: Thread? = null

/** Labels the latest worker snapshot so a test can tell which screen published it. */
@Volatile
internal var lastGovernedSnapshotLabelForTests: String? = null

/**
 * Thread that last prepared each labeled snapshot. Later screens do not erase
 * an earlier label, so a test can wait for one screen without a last-writer race.
 */
internal val governedIdentityThreadsForTests = java.util.concurrent.ConcurrentHashMap<String, Thread>()

internal fun recordGovernedIdentityForTests(label: String) {
    val thread = Thread.currentThread()
    lastGovernedIdentityThreadForTests = thread
    if (label.isNotEmpty()) {
        lastGovernedSnapshotLabelForTests = label
        governedIdentityThreadsForTests[label] = thread
    }
}

/** Null refuses the schema-1 step write. Teach and orient steps with no items stay completable. */
internal fun governedStepCompletionDigest(
    pack: V2CurriculumPack,
    learner: V2LearnerView,
    stepId: String,
): String? {
    lastGovernedIdentityThreadForTests = Thread.currentThread()
    if (!stepReadyForGovernedCompletion(pack, learner, stepId)) return null
    val step = pack.step(stepId)?.second ?: return null
    return stepContentDigest(pack, step)
}

internal fun attemptGrantsPrescribedCredit(attempt: GovernedAttemptView, pack: V2CurriculumPack): Boolean {
    if (attempt.acceptedOrder <= 0) return false
    if (attempt.attemptRole != PRESCRIBED_CHECK) return false
    if (attempt.identitySchema != GOVERNED_IDENTITY_SCHEMA || !attempt.correct) return false
    if (attempt.itemDigest == UNRECOVERABLE_GOVERNED_IDENTITY ||
        attempt.ruleDigest == UNRECOVERABLE_GOVERNED_IDENTITY ||
        attempt.unitContentDigest == UNRECOVERABLE_GOVERNED_IDENTITY
    ) {
        return false
    }
    if (attempt.ruleDigest != closedResponseRuleDigest()) return false
    val item = pack.item(attempt.itemId) ?: return false
    if (attempt.itemDigest != V2Validator.checkItemContentDigest(item)) return false
    if (pack.unit(attempt.unitNumber) == null) return false
    if (attempt.selected != item.answer || attempt.objectiveId != item.objectiveId ||
        itemUnitNumber(pack, item) != attempt.unitNumber) return false
    return prescribedUnitIdentityMatches(pack, attempt.unitNumber, attempt.unitContentDigest)
}

internal fun creditItemIds(pack: V2CurriculumPack, learner: V2LearnerView): Set<String> {
    val attempts = learner.governedAttempts ?: return learner.correctItemIds
    return attempts.filter { attemptGrantsPrescribedCredit(it, pack) }.map { it.itemId }.toSet()
}

internal fun completedStepCounts(pack: V2CurriculumPack, learner: V2LearnerView, stepId: String): Boolean {
    val digests = learner.governedStepDigests ?: return stepId in learner.completedStepIds
    val recorded = digests[stepId] ?: return false
    if (recorded == UNRECOVERABLE_GOVERNED_IDENTITY) return false
    val schema = learner.governedStepSchemas?.get(stepId)
    if (learner.governedStepSchemas != null && schema != STEP_IDENTITY_SCHEMA) return false
    val step = pack.step(stepId)?.second ?: return false
    return recorded == stepContentDigest(pack, step)
}

internal fun practiceOffers(pack: V2CurriculumPack, learner: V2LearnerView): List<PracticeOffer> {
    val unsupported = pack.schemaVersion != 1
    fun runnable(id: String, title: String, detail: String): PracticeOffer {
        if (unsupported) {
            return PracticeOffer(id, title, "This curriculum version is not supported.", false, "UNSUPPORTED_VERSION")
        }
        val count = practiceQueue(pack, learner, id).size
        val body = if (count == 0) {
            "$detail No closed-response items are available for the units you have reached."
        } else {
            "$detail $count closed-response items. Optional practice does not complete a required check."
        }
        return PracticeOffer(id, title, body, true, null)
    }
    return listOf(
        runnable(
            "alphabet",
            "Alphabet recognition",
            "Printed-letter checks from the units you have reached.",
        ),
        runnable(
            "reading",
            "Reading Lab",
            "Stress-marked closed responses from the units you have reached.",
        ),
        runnable(
            "grammar",
            "Grammar Lab",
            "Form, case, and contrast checks from the units you have reached.",
        ),
        runnable(
            "vocabulary",
            "Vocabulary and forms",
            "Lesson-filtered meaning and form checks from the units you have reached.",
        ),
        runnable(
            "mixed",
            "Mixed review",
            "Closed responses from units you have already completed, with recent misses first.",
        ),
        PracticeOffer(
            "listening",
            "Listening Lab",
            "Mapped audio is not installed. Listening practice cannot be completed.",
            false,
            "V2_MAPPED_AUDIO_ABSENT",
        ),
        PracticeOffer(
            "pronunciation",
            "Pronunciation Lab",
            "Model audio is not installed. Pronunciation is not scored.",
            false,
            "V2_MAPPED_AUDIO_ABSENT",
        ),
        PracticeOffer(
            "handwriting",
            "Handwriting",
            "Handwriting assets are not installed. Handwriting is not scored.",
            false,
            "HANDWRITING_ABSENT",
        ),
    )
}

internal fun practiceQueue(pack: V2CurriculumPack, learner: V2LearnerView, labId: String): List<V2CheckItem> {
    if (pack.schemaVersion != 1) return emptyList()
    if (labId in setOf("listening", "pronunciation", "handwriting")) return emptyList()
    val reached = currentCompletedUnitNumbers(pack, learner) + learner.currentUnitNumber
    val pool = pack.items.mapNotNull { item ->
        val unitNumber = itemUnitNumber(pack, item) ?: return@mapNotNull null
        val unit = pack.unit(unitNumber) ?: return@mapNotNull null
        if (!unit.openable() || unitNumber !in reached) return@mapNotNull null
        if (item.options.isEmpty() || item.answer !in item.options) return@mapNotNull null
        if (!itemKindMatchesLab(item.kind, labId, unitNumber, learner, pack)) return@mapNotNull null
        if (itemOnlyOnGapSteps(pack, item.id)) return@mapNotNull null
        item
    }
    if (labId != "mixed") return pool.sortedBy { it.id }
    val recentWrong = learner.governedAttempts.orEmpty()
        .filter {
            it.acceptedOrder > 0 &&
                !it.correct &&
                (
                    attemptSuppliesStepSelection(it, pack, it.stepId) ||
                        (it.attemptRole == OPTIONAL_PRACTICE && attemptMatchesCurrentOptional(it, pack, it.stepId))
                    )
        }
        .sortedByDescending { it.acceptedOrder }
        .map { it.itemId }
        .distinct()
    val rank = recentWrong.withIndex().associate { it.value to it.index }
    return pool.sortedWith(compareBy({ rank[it.id] ?: Int.MAX_VALUE }, { it.id }))
}

internal fun practiceStepId(labId: String): String = "practice:$labId"

internal data class PracticeLabSnapshot(
    val offer: PracticeOffer?,
    val items: List<V2CheckItem>,
    val recordedByItem: Map<String, GovernedAttemptView>,
)

/** Offers, ranking, and current selections for one lab. Call off the main thread. */
internal fun practiceLabSnapshot(
    pack: V2CurriculumPack,
    learner: V2LearnerView,
    labId: String,
): PracticeLabSnapshot {
    val items = practiceQueue(pack, learner, labId)
    val recorded = linkedMapOf<String, GovernedAttemptView>()
    items.forEach { item ->
        optionalPracticeSelection(pack, learner, labId, item.id)?.let { recorded[item.id] = it }
    }
    return PracticeLabSnapshot(
        offer = practiceOffers(pack, learner).firstOrNull { it.id == labId },
        items = items,
        recordedByItem = recorded,
    )
}

private fun currentUnitDigest(pack: V2CurriculumPack, unitNumber: Int): String {
    return V2PackIdentityIndex.unitDigest(pack, unitNumber)
        ?: V2Validator.unitContentReviewDigest(pack, unitNumber)
}

/**
 * The latest optional answer for this lab item, only when schema, item,
 * unit-content, rule, role, and practice-context identities match the current
 * pack. A stale row that selected the same option does not count.
 */
internal fun optionalPracticeSelection(
    pack: V2CurriculumPack,
    learner: V2LearnerView,
    labId: String,
    itemId: String,
): GovernedAttemptView? {
    if (pack.schemaVersion != 1 || itemId.isBlank()) return null
    val stepId = practiceStepId(labId)
    return learner.governedAttempts.orEmpty()
        .filter { attempt ->
            attempt.acceptedOrder > 0 &&
                attempt.itemId == itemId &&
                attemptMatchesCurrentOptional(attempt, pack, stepId)
        }
        .maxByOrNull { it.acceptedOrder }
}

internal fun attemptMatchesCurrentOptional(
    attempt: GovernedAttemptView,
    pack: V2CurriculumPack,
    stepId: String,
): Boolean {
    if (attempt.attemptRole != OPTIONAL_PRACTICE) return false
    if (attempt.identitySchema != GOVERNED_IDENTITY_SCHEMA) return false
    if (attempt.stepId != stepId || stepId.isBlank()) return false
    if (attempt.selected.isBlank()) return false
    if (attempt.itemDigest == UNRECOVERABLE_GOVERNED_IDENTITY ||
        attempt.ruleDigest == UNRECOVERABLE_GOVERNED_IDENTITY ||
        attempt.unitContentDigest == UNRECOVERABLE_GOVERNED_IDENTITY
    ) {
        return false
    }
    if (attempt.ruleDigest != closedResponseRuleDigest()) return false
    val item = pack.item(attempt.itemId) ?: return false
    if (attempt.selected !in item.options) return false
    if (attempt.itemDigest != V2Validator.checkItemContentDigest(item)) return false
    val unitNumber = itemUnitNumber(pack, item) ?: return false
    if (attempt.unitNumber != unitNumber || pack.unit(unitNumber) == null) return false
    return attempt.unitContentDigest == currentUnitDigest(pack, unitNumber)
}

internal fun savedWorkEntries(pack: V2CurriculumPack, learner: V2LearnerView): List<SavedWorkEntry> {
    val entries = mutableListOf<SavedWorkEntry>()
    val sessionId = learner.currentSessionId
    if (!sessionId.isNullOrBlank()) {
        val session = pack.session(sessionId)
        val step = session?.steps?.getOrNull(learner.currentStepIndex)
        if (session == null || step == null) {
            entries += SavedWorkEntry(
                id = "resume:$sessionId",
                title = "Saved lesson",
                detail = "The saved lesson is not in the current curriculum. It will not be opened.",
                kind = "STALE",
                resumable = false,
            )
        } else if (!resumeIsCurrent(pack, learner)) {
            val legacy = learner.resumeIdentitySchema != GOVERNED_IDENTITY_SCHEMA
            entries += SavedWorkEntry(
                id = "resume:$sessionId",
                title = step.title,
                detail = if (legacy) {
                    "This saved position has no verified lesson identity. It will not be opened or scored."
                } else {
                    "This saved position does not match the current lesson. It will not be opened or scored."
                },
                kind = "STALE",
                resumable = false,
            )
        } else if (!completedStepCounts(pack, learner, step.id)) {
            entries += SavedWorkEntry(
                id = "resume:$sessionId",
                title = step.title,
                detail = "Resume ${session.title}, step ${learner.currentStepIndex + 1} of ${session.steps.size}.",
                kind = "RESUME",
                resumable = true,
                sessionId = sessionId,
                stepIndex = learner.currentStepIndex,
            )
        }
    }
    learner.bookmarks.forEach { bookmark ->
        entries += bookmarkEntry(pack, bookmark)
    }
    learner.notes.forEach { note ->
        val open = savedTarget(pack, note.targetId, "STEP", note.targetDigest, note.identitySchema)
        entries += SavedWorkEntry(
            id = "note:${note.id}",
            title = "Local note",
            detail = "Not course content and not scored. ${note.text}",
            kind = "NOTE",
            resumable = open != null,
            sessionId = open?.first,
            stepIndex = open?.second ?: 0,
            referenceId = if (open == null) null else null,
        )
    }
    learner.tutorItems.forEach { item ->
        val open = savedTarget(pack, item.targetId, "STEP", item.targetDigest, item.identitySchema)
        entries += SavedWorkEntry(
            id = "tutor:${item.id}",
            title = "Question for a tutor",
            detail = "Optional and not scored. This is not advancement evidence. ${item.note}",
            kind = "TUTOR",
            resumable = open != null,
            sessionId = open?.first,
            stepIndex = open?.second ?: 0,
        )
    }
    return entries
}

private fun bookmarkEntry(pack: V2CurriculumPack, bookmark: GovernedBookmarkView): SavedWorkEntry {
    val open = savedTarget(pack, bookmark.targetId, bookmark.kind, bookmark.targetDigest, bookmark.identitySchema)
    val title = when (bookmark.kind) {
        "REFERENCE" -> pack.reference(bookmark.targetId)?.title ?: "Saved reference"
        "STEP" -> pack.step(bookmark.targetId)?.second?.title ?: "Saved step"
        else -> "Saved item"
    }
    return if (open == null && bookmark.kind == "REFERENCE") {
        val reference = pack.reference(bookmark.targetId)
        val digestOk = reference != null &&
            bookmark.identitySchema == GOVERNED_IDENTITY_SCHEMA &&
            bookmark.targetDigest == referenceContentDigest(reference)
        SavedWorkEntry(
            id = "bookmark:${bookmark.targetId}",
            title = title,
            detail = if (digestOk) "Saved reference." else "This saved reference cannot be opened.",
            kind = if (digestOk) "BOOKMARK" else "STALE",
            resumable = digestOk,
            referenceId = if (digestOk) bookmark.targetId else null,
        )
    } else if (open != null) {
        SavedWorkEntry(
            id = "bookmark:${bookmark.targetId}",
            title = title,
            detail = "Saved ${bookmark.kind.lowercase()}.",
            kind = "BOOKMARK",
            resumable = true,
            sessionId = open.first,
            stepIndex = open.second,
        )
    } else {
        SavedWorkEntry(
            id = "bookmark:${bookmark.targetId}",
            title = title,
            detail = "This saved item does not match the current curriculum. It will not be opened.",
            kind = "STALE",
            resumable = false,
        )
    }
}

private fun savedTarget(
    pack: V2CurriculumPack,
    targetId: String,
    kind: String,
    digest: String,
    schema: Int,
): Pair<String, Int>? {
    if (schema != GOVERNED_IDENTITY_SCHEMA || digest == UNRECOVERABLE_GOVERNED_IDENTITY) return null
    if (kind != "STEP") return null
    val located = pack.step(targetId) ?: return null
    if (digest != stepContentDigest(pack, located.second)) return null
    val index = located.first.steps.indexOfFirst { it.id == targetId }
    if (index < 0) return null
    return located.first.id to index
}

private fun itemOnlyOnGapSteps(pack: V2CurriculumPack, itemId: String): Boolean {
    val steps = pack.units.flatMap { it.sessions }.flatMap { it.steps }.filter { itemId in it.itemIds }
    return steps.isNotEmpty() && steps.all { it.id in GUIDED_PRACTICE_GAP_STEP_IDS }
}

private fun itemKindMatchesLab(kind: String, labId: String, unitNumber: Int, learner: V2LearnerView, pack: V2CurriculumPack): Boolean {
    return when (labId) {
        "alphabet" -> kind in setOf("LETTER_IDENTITY", "CONFUSABLE", "SIGN", "PRINT_PAIR")
        "reading" -> kind == "SOURCE_STRESS"
        "grammar" -> kind in setOf(
            "FORM_SELECTION",
            "CASE_RECOGNITION",
            "PATTERN_CLASSIFICATION",
            "NUMBER_FORM_SELECTION",
            "SOURCE_CASE",
            "SOURCE_INDECLINABLE",
            "CONTRAST_SELECTION",
        )
        "vocabulary" -> kind in setOf("SOURCE_MEANING", "SOURCE_FORM", "SOURCE_GENDER", "SOURCE_QUESTION")
        "mixed" -> unitNumber in currentCompletedUnitNumbers(pack, learner)
        else -> false
    }
}

private fun itemUnitNumber(pack: V2CurriculumPack, item: V2CheckItem): Int? {
    return pack.units.firstOrNull { unit -> unit.objectives.any { it.id == item.objectiveId } }?.number
}

private fun sha256Hex(text: String): String {
    val digest = MessageDigest.getInstance("SHA-256").digest(text.toByteArray(Charsets.UTF_8))
    return digest.joinToString("") { byte -> "%02x".format(byte) }
}

/**
 * Immutable pack digests. Keys are the pack instance, not its structural hash.
 * A data-class hash walks the whole curriculum. Production warms this on a
 * worker before learner-dependent work. A lookup misses until [warm] has
 * finished for that pack instance.
 */
internal object V2PackIdentityIndex {
    private val steps = IdentityWeakCache<Map<String, String>>()
    private val units = IdentityWeakCache<Map<Int, String>>()

    fun warm(pack: V2CurriculumPack) {
        if (steps.get(pack) != null && units.get(pack) != null) return
        val stepMap = pack.units.flatMap { it.sessions }.flatMap { it.steps }
            .associate { step -> step.id to rawStepContentDigest(pack, step) }
        val unitMap = pack.units.associate { unit ->
            unit.number to V2Validator.unitContentReviewDigest(pack, unit.number)
        }
        steps.put(pack, stepMap)
        units.put(pack, unitMap)
    }

    fun stepDigest(pack: V2CurriculumPack, stepId: String): String? = steps.get(pack)?.get(stepId)

    fun unitDigest(pack: V2CurriculumPack, unitNumber: Int): String? = units.get(pack)?.get(unitNumber)

    fun clearForTests() {
        V2CompletionEvidence.clearForTests()
        steps.clear()
        units.clear()
    }
}

/**
 * Weak cache keyed by object identity. [System.identityHashCode] does not
 * walk a data-class structure. Cleared referents are removed from a
 * [java.lang.ref.ReferenceQueue] on the next lookup, publication, or
 * [reclaim], including when the next key hashes to a different bucket.
 * A live entry past [maxRetained] evicts the oldest so a process cannot
 * keep every historical digest map.
 */
internal class IdentityWeakCache<V>(private val maxRetained: Int = 8) {
    private class Node<V>(
        referent: Any,
        queue: java.lang.ref.ReferenceQueue<Any>,
        val identity: Int,
        val value: V,
    ) : java.lang.ref.WeakReference<Any>(referent, queue)

    private val queue = java.lang.ref.ReferenceQueue<Any>()
    private val lock = Any()
    private val buckets = HashMap<Int, MutableList<Node<V>>>()
    private val order = ArrayDeque<Node<V>>()

    fun get(key: Any): V? {
        reclaim()
        val id = System.identityHashCode(key)
        synchronized(lock) {
            val list = buckets[id] ?: return null
            var found: V? = null
            val iterator = list.iterator()
            while (iterator.hasNext()) {
                val node = iterator.next()
                val current = node.get()
                if (current == null) {
                    iterator.remove()
                    order.remove(node)
                } else if (current === key) {
                    found = node.value
                }
            }
            if (list.isEmpty()) buckets.remove(id)
            return found
        }
    }

    fun put(key: Any, value: V) {
        reclaim()
        val id = System.identityHashCode(key)
        val node = Node(key, queue, id, value)
        synchronized(lock) {
            val list = buckets.getOrPut(id) { mutableListOf() }
            val iterator = list.iterator()
            while (iterator.hasNext()) {
                val existing = iterator.next()
                val current = existing.get()
                if (current == null || current === key) {
                    iterator.remove()
                    order.remove(existing)
                }
            }
            list.add(node)
            order.add(node)
            while (order.size > maxRetained) {
                val oldest = order.removeFirst()
                val oldestList = buckets[oldest.identity] ?: continue
                oldestList.remove(oldest)
                if (oldestList.isEmpty()) buckets.remove(oldest.identity)
            }
        }
    }

    fun reclaim() {
        val cleared = ArrayList<Node<V>>()
        while (true) {
            @Suppress("UNCHECKED_CAST")
            val node = queue.poll() as Node<V>? ?: break
            cleared.add(node)
        }
        if (cleared.isEmpty()) return
        synchronized(lock) {
            for (node in cleared) {
                order.remove(node)
                val list = buckets[node.identity] ?: continue
                list.remove(node)
                if (list.isEmpty()) buckets.remove(node.identity)
            }
        }
    }

    fun clear() {
        synchronized(lock) {
            buckets.clear()
            order.clear()
        }
    }

    fun retainedValueCountForTests(): Int {
        reclaim()
        synchronized(lock) { return order.size }
    }
}
