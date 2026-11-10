package dev.russiancompanion.v2.curriculum

import kotlinx.serialization.Serializable

/** A2-34 unit test bounds. The pass mark is shown before the attempt (MA2-085). */
internal const val UNIT_TEST_MIN_ITEMS = 10
internal const val UNIT_TEST_MAX_ITEMS = 20
internal const val UNIT_TEST_MIN_TYPED_PERCENT = 30
internal const val UNIT_TEST_PASS_PERCENT = 80
internal const val UNIT_TEST_SCHEMA = 1

internal data class V2UnitTestPlan(
    val unitNumber: Int,
    val itemIds: List<String>,
    val typedCount: Int,
    val passMarkPercent: Int,
    val digest: String,
)

@Serializable
internal data class V2UnitTestAnswer(val index: Int, val selected: String)

/** One attempt. Open while submittedOrder is 0; a submitted attempt is never changed. */
@Serializable
internal data class V2UnitTestAttempt(
    val id: String,
    val unitNumber: Int,
    val planDigest: String,
    val itemIds: List<String>,
    val passMarkPercent: Int,
    val answers: List<V2UnitTestAnswer> = emptyList(),
    val submittedOrder: Long = 0,
    val correctCount: Int = 0,
    val passed: Boolean = false,
    val schema: Int = UNIT_TEST_SCHEMA,
)

/** A2-32 binder result: the learner's own self-check, never demonstrated ability (MA2-082, MA2-090). */
@Serializable
internal data class V2BinderSelfCheck(val unitNumber: Int, val taskDigest: String, val recordedAtLocal: String)

internal const val SELF_CHECK_LABEL = "SELF_CHECK"

internal fun binderTaskDigest(task: V2BinderTask): String = reviewHash(canonicalParts(listOf(
    task.id, task.prompt, canonicalParts(task.objectiveIds), task.modelAnswer ?: "")))

/** Checked items that can be scored now: a reviewed typed key or a valid closed option set. */
private fun unitTestScorable(item: V2CheckItem): Boolean = if (item.isTypedResponse())
    item.typedAnswerKey?.acceptedAnswers?.firstOrNull()?.let { scoreCheckedResponse(item, it) == true } == true
else item.options.size >= 2 && item.answer in item.options && scoreCheckedResponse(item, item.answer) == true

private fun unitTestCandidates(pack: V2CurriculumPack, unit: V2Unit): List<V2CheckItem> =
    unit.objectives.filter { it.gating }.flatMap { o -> o.checkItemIds.mapNotNull { pack.item(it) }.filter { it.objectiveId == o.id } }
        .distinctBy { it.id }.filter(::unitTestScorable)

/** Why the unit has no valid test yet, or null when a plan exists. Never narrowed to make a test appear. */
internal fun unitTestUnavailableReason(pack: V2CurriculumPack, unit: V2Unit): String? {
    val candidates = unitTestCandidates(pack, unit)
    val typed = candidates.count { it.isTypedResponse() }
    return when {
        candidates.size < UNIT_TEST_MIN_ITEMS ->
            "Unit test not yet available: ${candidates.size} checked items; at least $UNIT_TEST_MIN_ITEMS are needed."
        unitTestPlan(pack, unit) == null ->
            "Unit test not yet available: $typed typed items; at least $UNIT_TEST_MIN_TYPED_PERCENT percent of the test must be typed."
        else -> null
    }
}

/**
 * 10 to 20 checked items from this unit with at least 30 percent typed. Typed items come first in
 * authored order; closed items fill the rest round-robin across objectives. Deterministic.
 */
internal fun unitTestPlan(pack: V2CurriculumPack, unit: V2Unit): V2UnitTestPlan? {
    val candidates = unitTestCandidates(pack, unit)
    val typed = candidates.filter { it.isTypedResponse() }
    val closedByObjective = candidates.filterNot { it.isTypedResponse() }.groupBy { it.objectiveId }.values.map { it.toMutableList() }
    // Largest size whose typed share can reach the minimum (integer arithmetic, no rounding up).
    val size = (minOf(UNIT_TEST_MAX_ITEMS, candidates.size) downTo UNIT_TEST_MIN_ITEMS).firstOrNull { n ->
        minOf(typed.size, n) * 100 >= n * UNIT_TEST_MIN_TYPED_PERCENT
    } ?: return null
    val chosen = typed.take(size).toMutableList()
    while (chosen.size < size) {
        val before = chosen.size
        for (queue in closedByObjective) if (chosen.size < size && queue.isNotEmpty()) chosen += queue.removeAt(0)
        if (chosen.size == before) break
    }
    if (chosen.size !in UNIT_TEST_MIN_ITEMS..UNIT_TEST_MAX_ITEMS) return null
    val typedCount = chosen.count { it.isTypedResponse() }
    if (typedCount * 100 < chosen.size * UNIT_TEST_MIN_TYPED_PERCENT) return null
    val digest = reviewHash(canonicalParts(listOf("unit-test", unit.number.toString(), UNIT_TEST_PASS_PERCENT.toString()) +
        chosen.map { canonicalParts(listOf(it.id, V2Validator.checkItemContentDigest(it), responseRuleDigest(it))) }))
    return V2UnitTestPlan(unit.number, chosen.map { it.id }, typedCount, UNIT_TEST_PASS_PERCENT, digest)
}

/** An attempt is current only for the unit's current plan; content or key changes make it stale. */
internal fun unitTestAttemptCurrent(pack: V2CurriculumPack, attempt: V2UnitTestAttempt): Boolean {
    val plan = pack.unit(attempt.unitNumber)?.let { unitTestPlan(pack, it) } ?: return false
    return attempt.schema == UNIT_TEST_SCHEMA && attempt.planDigest == plan.digest &&
        attempt.itemIds == plan.itemIds && attempt.passMarkPercent == plan.passMarkPercent
}

/** Scores every answered index against the current reviewed items; unanswered items count as wrong. */
internal fun scoreUnitTest(pack: V2CurriculumPack, attempt: V2UnitTestAttempt): Int =
    attempt.itemIds.indices.count { index ->
        val answer = attempt.answers.lastOrNull { it.index == index } ?: return@count false
        pack.item(attempt.itemIds[index])?.let { scoreCheckedResponse(it, answer.selected) } == true
    }

internal fun unitTestPasses(correct: Int, total: Int, passMarkPercent: Int): Boolean =
    total > 0 && correct * 100 >= total * passMarkPercent

/** Remediation for a failed attempt: each missed item's objective, its first remediation step. */
internal fun unitTestRemediation(pack: V2CurriculumPack, attempt: V2UnitTestAttempt): List<Pair<V2Objective, String>> {
    val unit = pack.unit(attempt.unitNumber) ?: return emptyList()
    val missed = attempt.itemIds.indices.filter { index ->
        val answer = attempt.answers.lastOrNull { it.index == index }
        answer == null || pack.item(attempt.itemIds[index])?.let { scoreCheckedResponse(it, answer.selected) } != true
    }.mapNotNull { pack.item(attempt.itemIds[it])?.objectiveId }.distinct()
    return missed.mapNotNull { id -> unit.objectives.singleOrNull { it.id == id }?.let { o ->
        o.remediationStepIds.firstOrNull { pack.step(it) != null }?.let { o to it } } }
}

/** Structural checks for stored and restored attempts and self-checks. */
internal fun unitTestAttemptsDefect(attempts: List<V2UnitTestAttempt>): String? {
    if (attempts.map { it.id }.distinct().size != attempts.size) return "duplicate unit-test attempt"
    val submitted = attempts.filter { it.submittedOrder > 0 }
    if (submitted.map { it.submittedOrder }.distinct().size != submitted.size) return "duplicate unit-test order"
    for (a in attempts) {
        if (a.id.isBlank() || a.unitNumber !in 1..30 || a.schema != UNIT_TEST_SCHEMA || a.planDigest.length != 64 ||
            a.itemIds.size !in UNIT_TEST_MIN_ITEMS..UNIT_TEST_MAX_ITEMS || a.itemIds.distinct().size != a.itemIds.size ||
            a.passMarkPercent !in 1..100 || a.submittedOrder < 0 ||
            a.answers.any { it.index !in a.itemIds.indices || it.selected.isBlank() } ||
            a.answers.map { it.index }.distinct().size != a.answers.size) return "invalid unit-test attempt"
        if (a.submittedOrder == 0L && (a.correctCount != 0 || a.passed)) return "open unit-test attempt has a result"
        if (a.submittedOrder > 0 && (a.correctCount !in 0..a.itemIds.size ||
            a.passed != unitTestPasses(a.correctCount, a.itemIds.size, a.passMarkPercent))) return "inconsistent unit-test result"
    }
    if (attempts.filter { it.submittedOrder == 0L }.groupBy { it.unitNumber }.values.any { it.size > 1 }) return "two open unit-test attempts"
    return null
}

internal fun binderSelfChecksDefect(checks: List<V2BinderSelfCheck>): String? {
    if (checks.map { it.unitNumber }.distinct().size != checks.size) return "duplicate binder self-check"
    return if (checks.any { it.unitNumber !in 1..30 || it.taskDigest.length != 64 || it.recordedAtLocal.isBlank() })
        "invalid binder self-check" else null
}

/** A self-check counts only for the unit's current binder task. */
internal fun binderSelfCheckCurrent(pack: V2CurriculumPack, check: V2BinderSelfCheck): Boolean =
    pack.unit(check.unitNumber)?.binderTask?.let { binderTaskDigest(it) == check.taskDigest } == true

/** Validator: binder and tutor task declarations (A2-32, A2-33). */
internal fun courseworkDefects(pack: V2CurriculumPack): List<String> = pack.units.flatMap { unit ->
    val errors = mutableListOf<String>()
    unit.binderTask?.let { task ->
        if (task.id.isBlank() || task.prompt.isBlank()) errors += "Unit ${unit.number} binder task needs an id and a prompt"
        if (task.objectiveIds.isEmpty() || task.objectiveIds.any { id -> unit.objectives.none { it.id == id } })
            errors += "Unit ${unit.number} binder task must name this unit's objectives"
        if (task.modelAnswer?.isBlank() == true) errors += "Unit ${unit.number} binder model answer is blank"
    }
    unit.tutorTask?.let { task ->
        if (task.id.isBlank() || task.bring.isBlank()) errors += "Unit ${unit.number} tutor task must name what to bring"
    }
    errors
}
