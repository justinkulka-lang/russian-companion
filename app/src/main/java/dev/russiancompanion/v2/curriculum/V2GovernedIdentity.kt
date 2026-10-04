package dev.russiancompanion.v2.curriculum

import dev.russiancompanion.v2.data.V2ProfileEntity
import java.security.MessageDigest

/** Identity schema written on new attempts, steps, bookmarks, notes, and resume positions. */
internal const val GOVERNED_IDENTITY_SCHEMA = 1

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
 * The six guided-practice curriculum gaps. Practice must not invent items for them.
 */
internal val GUIDED_PRACTICE_GAP_STEP_IDS = setOf(
    "u1-st-confusable",
    "u1-st-iotated",
    "u1-st-signs",
    "u1-st-print",
    "u1-st-decoding",
    "u4-st-rem-nominative",
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

internal fun stepContentDigest(pack: V2CurriculumPack, step: V2Step): String {
    val items = step.itemIds.joinToString("\n") { id ->
        val item = pack.item(id)
        if (item == null) "missing:$id" else V2Validator.checkItemContentDigest(item)
    }
    return sha256Hex(listOf(step.id, step.kind, step.title, step.body, items).joinToString("\n"))
}

internal fun resumePositionDigest(pack: V2CurriculumPack, sessionId: String, stepIndex: Int): String? {
    val session = pack.session(sessionId) ?: return null
    val step = session.steps.getOrNull(stepIndex) ?: return null
    return stepContentDigest(pack, step)
}

internal fun referenceContentDigest(entry: V2ReferenceEntry): String {
    return sha256Hex(listOf(entry.id, entry.title, entry.summary, entry.detail).joinToString("\n"))
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
    val unitDigest = V2Validator.unitContentReviewDigest(pack, unitNumber)
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

internal fun attemptGrantsPrescribedCredit(attempt: GovernedAttemptView, pack: V2CurriculumPack): Boolean {
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
    return attempt.unitContentDigest == V2Validator.unitContentReviewDigest(pack, attempt.unitNumber)
}

internal fun creditItemIds(pack: V2CurriculumPack, learner: V2LearnerView): Set<String> {
    val attempts = learner.governedAttempts ?: return learner.correctItemIds
    return attempts.filter { attemptGrantsPrescribedCredit(it, pack) }.map { it.itemId }.toSet()
}

internal fun completedStepCounts(pack: V2CurriculumPack, learner: V2LearnerView, stepId: String): Boolean {
    val digests = learner.governedStepDigests ?: return stepId in learner.completedStepIds
    val recorded = digests[stepId] ?: return false
    if (recorded == UNRECOVERABLE_GOVERNED_IDENTITY) return false
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
    val reached = learner.completedUnitNumbers + learner.currentUnitNumber
    val pool = pack.items.mapNotNull { item ->
        val unitNumber = itemUnitNumber(pack, item) ?: return@mapNotNull null
        val unit = pack.unit(unitNumber) ?: return@mapNotNull null
        if (!unit.openable() || unitNumber !in reached) return@mapNotNull null
        if (item.options.isEmpty() || item.answer !in item.options) return@mapNotNull null
        if (!itemKindMatchesLab(item.kind, labId, unitNumber, learner)) return@mapNotNull null
        if (itemOnlyOnGapSteps(pack, item.id)) return@mapNotNull null
        item
    }
    if (labId != "mixed") return pool.sortedBy { it.id }
    val recentWrong = learner.governedAttempts.orEmpty()
        .asReversed()
        .filter { !it.correct && (it.attemptRole == PRESCRIBED_CHECK || it.attemptRole == OPTIONAL_PRACTICE) }
        .map { it.itemId }
        .distinct()
    val rank = recentWrong.withIndex().associate { it.value to it.index }
    return pool.sortedWith(compareBy({ rank[it.id] ?: Int.MAX_VALUE }, { it.id }))
}

internal fun practiceStepId(labId: String): String = "practice:$labId"

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
        } else if (step.id !in learner.completedStepIds) {
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
        val preview = note.text.replace('\n', ' ').take(80)
        val open = savedTarget(pack, note.targetId, "STEP", note.targetDigest, note.identitySchema)
        entries += SavedWorkEntry(
            id = "note:${note.id}",
            title = "Local note",
            detail = "Not course content. $preview",
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
            detail = "Optional. This is not advancement evidence.",
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

private fun itemKindMatchesLab(kind: String, labId: String, unitNumber: Int, learner: V2LearnerView): Boolean {
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
        "mixed" -> unitNumber in learner.completedUnitNumbers
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
