package dev.russiancompanion.v2.curriculum

import kotlinx.serialization.Serializable

/** A2-30 checklist entry kinds, in display order. */
internal enum class V2AssignmentKind { BOOK_PAGES, BOOK_EXERCISE, APP_SESSION, BINDER_TASK, REVIEW_DUE }

/** DONE and TODO describe recorded work only; NOT_ASSIGNED means the pack names nothing yet. */
internal enum class V2AssignmentStatus { DONE, TODO, NOT_ASSIGNED }

internal data class V2AssignmentEntry(
    val id: String,
    val kind: V2AssignmentKind,
    val label: String,
    val status: V2AssignmentStatus,
    val detail: String? = null,
)

/** The learner's own target date for a unit (A2-31). It never resets or penalizes anything. */
@Serializable
internal data class V2UnitTarget(val unitNumber: Int, val targetEpochDay: Long)

/** Inclusive bounds: 1970-01-01 through 9999-12-31. */
internal const val UNIT_TARGET_MIN_EPOCH_DAY = 0L
internal const val UNIT_TARGET_MAX_EPOCH_DAY = 2_932_896L

internal fun unitTargetDefect(target: V2UnitTarget): String? = when {
    target.unitNumber !in 1..30 -> "unit target out of range"
    target.targetEpochDay !in UNIT_TARGET_MIN_EPOCH_DAY..UNIT_TARGET_MAX_EPOCH_DAY -> "unit target date out of range"
    else -> null
}

internal fun unitTargetsDefect(targets: List<V2UnitTarget>): String? {
    if (targets.map { it.unitNumber }.distinct().size != targets.size) return "duplicate unit target"
    return targets.firstNotNullOfOrNull(::unitTargetDefect)
}

/** Days from today to the target; negative once the date has passed. Display only. */
internal fun unitTargetDaysRemaining(targetEpochDay: Long, todayEpochDay: Long): Long = targetEpochDay - todayEpochDay

internal fun unitTargetLabel(targetEpochDay: Long?, todayEpochDay: Long): String {
    if (targetEpochDay == null) return "No target date set."
    val date = java.time.LocalDate.ofEpochDay(targetEpochDay)
    val days = unitTargetDaysRemaining(targetEpochDay, todayEpochDay)
    val remaining = when {
        days > 1 -> "$days days remaining"
        days == 1L -> "1 day remaining"
        days == 0L -> "Due today"
        days == -1L -> "Passed 1 day ago. Nothing is reset."
        else -> "Passed ${-days} days ago. Nothing is reset."
    }
    return "Target date: $date · $remaining"
}

/**
 * The unit's assignments as a checklist (A2-30): book pages and sections, named book exercises,
 * app sessions, the binder task and review due. Statuses come from recorded learner evidence;
 * book work is the learner's own attestation, never a demonstrated app check.
 */
internal fun unitAssignmentChecklist(pack: V2CurriculumPack, unit: V2Unit, learner: V2LearnerView,
                                     dueSpacedItems: Int = 0): List<V2AssignmentEntry> {
    val entries = mutableListOf<V2AssignmentEntry>()
    val attested = unit.number in learner.bookAttestedUnits
    val mappings = pack.ownedEditionMappings.filter { it.unitNumber == unit.number }
    val pages = mappings.filter { it.printedPageStart != null && it.printedPageEnd != null }
    if (pages.isEmpty()) {
        entries += V2AssignmentEntry("book-pages", V2AssignmentKind.BOOK_PAGES, "Book pages and sections",
            V2AssignmentStatus.NOT_ASSIGNED, "No book pages are assigned for this unit yet.")
    } else pages.forEach { m ->
        val lesson = m.proposedLessonNumber?.let { "Lesson $it, " } ?: ""
        entries += V2AssignmentEntry("book-pages-${m.id}", V2AssignmentKind.BOOK_PAGES,
            "Book: ${lesson}printed pages ${m.printedPageStart}–${m.printedPageEnd}",
            if (attested) V2AssignmentStatus.DONE else V2AssignmentStatus.TODO,
            "Recorded by your own book attestation.")
    }
    val exercises = mappings.flatMap { it.assignedExercises }.distinct()
    if (exercises.isEmpty()) {
        entries += V2AssignmentEntry("book-exercises", V2AssignmentKind.BOOK_EXERCISE, "Book exercises",
            V2AssignmentStatus.NOT_ASSIGNED, "No named book exercises are assigned for this unit yet.")
    } else exercises.forEachIndexed { index, name ->
        entries += V2AssignmentEntry("book-exercise-$index", V2AssignmentKind.BOOK_EXERCISE, "Book exercise: $name",
            if (attested) V2AssignmentStatus.DONE else V2AssignmentStatus.TODO,
            "Recorded by your own book attestation.")
    }
    unit.sessions.forEach { session ->
        val appSteps = session.steps.filter { it.kind !in setOf("BINDER", "TUTOR") }
        if (appSteps.isEmpty()) return@forEach
        val done = appSteps.all { completedStepCounts(pack, learner, it.id) }
        entries += V2AssignmentEntry("session-${session.id}", V2AssignmentKind.APP_SESSION, "App session: ${session.title}",
            if (done) V2AssignmentStatus.DONE else V2AssignmentStatus.TODO)
    }
    val binder = unit.sessions.flatMap { it.steps }.filter { it.kind == "BINDER" }
    val binderTask = unit.binderTask
    if (binderTask != null) {
        val checked = learner.binderSelfChecks.any { it.unitNumber == unit.number && binderSelfCheckCurrent(pack, it) }
        entries += V2AssignmentEntry("binder-task-${binderTask.id}", V2AssignmentKind.BINDER_TASK, "Binder task: ${binderTask.prompt}",
            if (checked) V2AssignmentStatus.DONE else V2AssignmentStatus.TODO,
            "Self-check only; not shown as demonstrated ability.")
    } else if (binder.isEmpty()) {
        entries += V2AssignmentEntry("binder", V2AssignmentKind.BINDER_TASK, "Binder task",
            V2AssignmentStatus.NOT_ASSIGNED, "No binder task is assigned for this unit yet.")
    } else binder.forEach { step ->
        entries += V2AssignmentEntry("binder-${step.id}", V2AssignmentKind.BINDER_TASK, "Binder task: ${step.title}",
            if (completedStepCounts(pack, learner, step.id)) V2AssignmentStatus.DONE else V2AssignmentStatus.TODO,
            "Self-check only; not shown as demonstrated ability.")
    }
    if (cumulativeReviewPolicy(unit.number) != null) {
        entries += V2AssignmentEntry("review-due", V2AssignmentKind.REVIEW_DUE, "Review due: cumulative review",
            if (cumulativeReviewComplete(pack, learner, unit.number)) V2AssignmentStatus.DONE else V2AssignmentStatus.TODO,
            "Required before you advance.")
    }
    if (dueSpacedItems > 0) {
        entries += V2AssignmentEntry("spaced-due", V2AssignmentKind.REVIEW_DUE,
            "Review due: $dueSpacedItems ${if (dueSpacedItems == 1) "item" else "items"} in Due review",
            V2AssignmentStatus.TODO, "Optional practice; it does not replace required unit review.")
    }
    return entries
}
