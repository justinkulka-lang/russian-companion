package dev.russiancompanion.v2.ui

import dev.russiancompanion.v2.curriculum.cumulativeReviewPolicy
import dev.russiancompanion.v2.curriculum.cumulativeReviewTitle

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import dev.russiancompanion.domain.appearance.AppearancePreference
import dev.russiancompanion.v2.curriculum.V2ContinueTarget
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.V2UnitState
import dev.russiancompanion.v2.curriculum.advancementDecision
import dev.russiancompanion.v2.curriculum.advancementRuleSummary
import dev.russiancompanion.v2.curriculum.example
import dev.russiancompanion.v2.curriculum.completedStepCounts
import dev.russiancompanion.v2.curriculum.governedSelectionKey
import dev.russiancompanion.v2.curriculum.currentObjective
import dev.russiancompanion.v2.curriculum.installLabel
import dev.russiancompanion.v2.curriculum.item
import dev.russiancompanion.v2.curriculum.lessonPresentedOptions
import dev.russiancompanion.v2.curriculum.objectiveState
import dev.russiancompanion.v2.curriculum.practiceOffers
import dev.russiancompanion.v2.curriculum.readinessLabel
import dev.russiancompanion.v2.curriculum.reviewedGuidedPractice
import dev.russiancompanion.v2.curriculum.stepCompletionItemIds
import dev.russiancompanion.v2.curriculum.step
import dev.russiancompanion.v2.curriculum.V2PackIdentityIndex
import dev.russiancompanion.v2.curriculum.recordGovernedIdentityForTests
import dev.russiancompanion.v2.curriculum.recordedSessionCount
import dev.russiancompanion.v2.curriculum.reference
import dev.russiancompanion.v2.curriculum.searchReferences
import dev.russiancompanion.v2.curriculum.session
import dev.russiancompanion.v2.curriculum.todaysSession
import dev.russiancompanion.v2.curriculum.topicLabel
import dev.russiancompanion.v2.curriculum.unit
import dev.russiancompanion.v2.curriculum.unitState
import dev.russiancompanion.v2.curriculum.visibleReferences
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

@Composable
internal fun <T> rememberGoverned(
    pack: V2CurriculumPack,
    learner: V2LearnerView,
    label: String = "",
    compute: () -> T,
): T? {
    val inline = V2UiWork.blocking()
    val inlineValue = remember(pack, learner, inline, label) {
        if (inline) {
            V2PackIdentityIndex.warm(pack)
            compute()
        } else {
            null
        }
    }
    // Keep the last worker snapshot so a learner refresh does not blank the screen.
    var published by remember { mutableStateOf<T?>(null) }
    val generation = remember { java.util.concurrent.atomic.AtomicLong(0) }
    LaunchedEffect(pack, learner, inline, label) {
        if (inline) return@LaunchedEffect
        val token = generation.incrementAndGet()
        recordGovernedIdentityForTests(if (label.isEmpty()) "scheduled" else "$label:scheduled")
        val computed = try {
            withContext(Dispatchers.Default) {
                V2UiWork.governedComputeEnteredForTests?.countDown()
                V2UiWork.governedComputeGateForTests?.invoke(label, token)
                recordGovernedIdentityForTests(label)
                V2PackIdentityIndex.warm(pack)
                compute()
            }
        } catch (cancelled: CancellationException) {
            withContext(NonCancellable) {
                V2UiWork.governedSnapshotOutcomeForTests?.invoke(label, token, false)
            }
            throw cancelled
        }
        val publish = token == generation.get()
        if (publish) published = computed
        V2UiWork.governedSnapshotOutcomeForTests?.invoke(label, token, publish)
    }
    return if (inline) inlineValue else published
}

@Composable
internal fun V2LocalDataScreen(message: String, onRetry: () -> Unit) {
    V2Scaffold(title = "Local data", tab = null, onTab = {}, footer = {
        androidx.compose.foundation.layout.Box(Modifier.padding(16.dp)) {
            V2PrimaryButton("Retry", onRetry, tag = "v2-retry")
        }
    }) {
        Text(message, style = MaterialTheme.typography.bodyLarge)
        V2Status("Bundled curriculum and saved progress are affected.")
    }
}

@Composable
internal fun V2OnboardingScreen(saveFailed: Boolean, onFinished: (V2OnboardingDraft) -> Unit) {
    var page by remember { mutableIntStateOf(0) }
    var penguin by remember { mutableStateOf(true) }
    var anki by remember { mutableStateOf(false) }
    var binder by remember { mutableStateOf(false) }
    var tutor by remember { mutableStateOf(false) }
    var start by remember { mutableStateOf(V2StartingPoint.NEW_TO_CYRILLIC) }
    var minutes by remember { mutableIntStateOf(35) }
    val title = when (page) {
        0 -> "Welcome"
        1 -> "Study system"
        2 -> "Starting point"
        3 -> "Daily commitment"
        else -> "Ready"
    }
    V2Scaffold(
        title = title,
        tab = null,
        onTab = {},
        footer = {
            androidx.compose.foundation.layout.Box(Modifier.padding(16.dp)) {
                when (page) {
                    4 -> V2PrimaryButton("Start Unit 1", {
                        onFinished(
                            V2OnboardingDraft(penguin, anki, binder, tutor, start, minutes),
                        )
                    }, tag = "v2-start-unit-1")
                    else -> V2PrimaryButton("Next", { page += 1 }, tag = "v2-onboarding-next")
                }
            }
        },
    ) {
        when (page) {
            0 -> {
                Text(
                    "Russian Companion is a fully offline course companion. It tells you the current unit, what to study in your own book, and what the app can practice with verified material.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.semantics { testTag = "v2-onboarding-welcome" },
                )
                Spacer(Modifier.height(12.dp))
                Text("Your data stays on this device. There is no account and no network.")
            }
            1 -> {
                Text("Select the companions you have. This changes guidance, not the curriculum.")
                CompanionToggle("I have the book", penguin) { penguin = it }
                CompanionToggle("I use AnkiDroid", anki) { anki = it }
                CompanionToggle("I keep a binder", binder) { binder = it }
                CompanionToggle("I have a tutor now", tutor) { tutor = it }
            }
            2 -> {
                Text("New to Cyrillic is the recommended start. Resume from a later book lesson is not available while Units 3–30 are not installed.")
                StartingChoice(V2StartingPoint.NEW_TO_CYRILLIC, start) { start = it }
                StartingChoice(V2StartingPoint.CAN_READ_CYRILLIC, start) { start = it }
                StartingChoice(V2StartingPoint.RESUME_FROM_BOOK, start) { start = it }
                if (start == V2StartingPoint.RESUME_FROM_BOOK) {
                    V2Status("Later units are not yet installed. You will still begin at Unit 1.")
                }
            }
            3 -> {
                Text("Choose a daily time. There are no streaks and no punishment for missing a day.")
                listOf(20, 35, 50).forEach { value ->
                    CompanionToggle("$value minutes", minutes == value) { minutes = value }
                }
            }
            else -> {
                Text("Unit 1 — Learning to read Russian", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.semantics { heading() })
                Text("You will learn printed letter identities, avoid Latin look-alike traps, and do Lesson 1 in your book. The app will not invent Russian example words or copy the book.")
            }
        }
        if (saveFailed) V2Status("The setup was not saved. Stay here and try again.")
    }
}

@Composable
private fun CompanionToggle(label: String, selected: Boolean, onChange: (Boolean) -> Unit) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .toggleable(value = selected, role = Role.Checkbox, onValueChange = onChange),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        androidx.compose.material3.Checkbox(checked = selected, onCheckedChange = null)
        Text(label)
    }
}

@Composable
private fun StartingChoice(value: V2StartingPoint, selected: V2StartingPoint, onSelect: (V2StartingPoint) -> Unit) {
    val label = when (value) {
        V2StartingPoint.NEW_TO_CYRILLIC -> "New to Cyrillic"
        V2StartingPoint.CAN_READ_CYRILLIC -> "I can already read Cyrillic"
        V2StartingPoint.RESUME_FROM_BOOK -> "Resume from a book lesson"
    }
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .selectable(selected = value == selected, role = Role.RadioButton,
                onClick = { if (value != selected) onSelect(value) }),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        RadioButton(selected = value == selected, onClick = null)
        Text(label)
    }
}

@Composable
internal fun V2TodayScreen(
    pack: V2CurriculumPack,
    learner: V2LearnerView,
    target: V2ContinueTarget,
    onTab: (V2Tab) -> Unit,
    onContinue: (V2ContinueTarget) -> Unit,
    onSavedWork: () -> Unit,
) {
    val unit = pack.unit(learner.currentUnitNumber)
    val governed = rememberGoverned(pack, learner, label = "today") {
        val current = unit
        Triple(
            current?.let { currentObjective(pack, it, learner) },
            current?.let { todaysSession(pack, it, learner) },
            current?.let { recordedSessionCount(pack, it, learner) } ?: 0,
        )
    }
    val doneSteps = rememberGoverned(pack, learner, label = "today-plan") {
        unit?.sessions?.associate { session ->
            session.id to session.steps.all { completedStepCounts(pack, learner, it.id) }
        } ?: emptyMap()
    }
    if (governed == null) {
        V2Scaffold(title = "Today", tab = V2Tab.TODAY, onTab = onTab) {
            Text("Preparing today's plan")
        }
        return
    }
    val objective = governed.first
    val today = governed.second
    val recorded = governed.third
    var showPlan by remember { mutableStateOf(false) }
    V2Scaffold(
        title = "Today",
        tab = V2Tab.TODAY,
        onTab = onTab,
        footer = {
            androidx.compose.foundation.layout.Box(Modifier.padding(16.dp)) {
                V2PrimaryButton(
                    text = if (target.unavailable) "Unavailable" else "Continue",
                    onClick = { onContinue(target) },
                    enabled = !target.unavailable,
                    tag = "v2-continue",
                )
            }
        },
    ) {
        Text(
            "Unit ${learner.currentUnitNumber} · ${unit?.topicLabel() ?: "Unavailable"}",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.semantics { heading(); testTag = "v2-today-unit" },
        )
        V2Status(unit?.let { installLabel(it) } ?: "Unavailable")
        Text(objective?.title ?: "No current objective", modifier = Modifier.semantics { testTag = "v2-today-objective" })
        Spacer(Modifier.height(12.dp))
        Text(target.title, style = MaterialTheme.typography.titleLarge)
        Text(target.reason)
        Spacer(Modifier.height(12.dp))
        if (today != null) {
            Text("Today’s session", style = MaterialTheme.typography.titleMedium)
            Text("${today.title} · ${today.estimatedMinutes} min of a ${learner.dailyMinutes}-minute commitment")
        } else {
            Text("Today’s session is complete for this unit.")
        }
        V2Status("$recorded of ${unit?.sessions?.size ?: 0} sessions recorded")
        V2SecondaryButton("Saved work", onSavedWork, tag = "v2-open-saved-work")
        if (unit != null) {
            V2SecondaryButton(
                if (showPlan) "Hide full unit plan" else "Show full unit plan",
                { showPlan = !showPlan },
            )
            if (showPlan) {
                unit.sessions.forEach { session ->
                    val done = doneSteps?.get(session.id) == true
                    V2Status("${session.title} — ${if (done) "Recorded" else "Not finished"} · ${session.estimatedMinutes} min")
                }
            }
        }
    }
}

@Composable
internal fun V2CourseScreen(
    pack: V2CurriculumPack,
    learner: V2LearnerView,
    onTab: (V2Tab) -> Unit,
    onOpenUnit: (Int) -> Unit,
) {
    val recordedByUnit = rememberGoverned(pack, learner, label = "course") {
        pack.units.associate { candidate ->
            candidate.number to recordedSessionCount(pack, candidate, learner)
        }
    }
    V2LazyScaffold(title = "Course", tab = V2Tab.COURSE, onTab = onTab) {
        item {
            Text(
                "Thirty units in the proposed lesson order. Development units are incomplete. Later units stay Not yet installed.",
                modifier = Modifier.padding(bottom = 12.dp).semantics { testTag = "v2-course-intro" },
            )
        }
        items(pack.units, key = { it.id }) { unit ->
            val state = unitState(pack, unit, learner)
            val status = when (state) {
                V2UnitState.CURRENT -> "Current"
                V2UnitState.AVAILABLE -> "Available"
                V2UnitState.COMPLETED -> "Completed"
                V2UnitState.LOCKED -> "Locked"
                V2UnitState.NOT_YET_INSTALLED -> "Not yet installed"
            }
            val progress = if (unit.sessions.isEmpty() || recordedByUnit == null) {
                installLabel(unit)
            } else {
                "${recordedByUnit[unit.number] ?: 0}/${unit.sessions.size}"
            }
            V2UnitRow(
                number = unit.number,
                topic = unit.topicLabel(),
                state = status,
                progress = progress,
                onClick = { onOpenUnit(unit.number) },
            )
        }
    }
}

@Composable
internal fun V2UnitScreen(
    pack: V2CurriculumPack,
    learner: V2LearnerView,
    unitNumber: Int,
    onBack: () -> Unit,
    onTab: (V2Tab) -> Unit,
    onOpenSession: (String) -> Unit,
    onAttestBook: (Int) -> Unit,
    onAdvance: (Int) -> Unit,
    onOpenCumulativeReview: () -> Unit = {},
) {
    val unit = pack.unit(unitNumber) ?: return
    val state = unitState(pack, unit, learner)
    val decision = rememberGoverned(pack, learner, label = "unit") { advancementDecision(pack, unit, learner) }
    V2Scaffold(title = "Unit ${unit.number}", tab = V2Tab.COURSE, onTab = onTab, onBack = onBack) {
        Text(
            unit.topicLabel(),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.semantics { heading(); testTag = "v2-unit-heading" },
        )
        Text(
            when (state) {
                V2UnitState.NOT_YET_INSTALLED -> "Not yet installed"
                V2UnitState.LOCKED -> "Locked by prerequisite"
                V2UnitState.CURRENT -> "Current"
                V2UnitState.AVAILABLE -> "Available"
                V2UnitState.COMPLETED -> "Completed"
            },
            modifier = Modifier.semantics { testTag = "v2-unit-state" },
        )
        Text(installLabel(unit), modifier = Modifier.semantics { testTag = "v2-unit-install" })
        val readiness = readinessLabel(unit)
        if (readiness.isNotEmpty()) {
            Text(readiness, modifier = Modifier.semantics { testTag = "v2-readiness" })
        }
        if (unit.number in 11..15 && unit.mappingStatus == "PENDING_CURRICULUM_REVIEWER") {
            Text(unit.mappingNote, modifier = Modifier.semantics { testTag = "v2-mapping-pending" })
        }
        if (state == V2UnitState.NOT_YET_INSTALLED) {
            Text("This unit is on the map so you can see the Penguin sequence. It has no lesson content, audio, or completion path.")
            return@V2Scaffold
        }
        if (state == V2UnitState.LOCKED) {
            Text("Finish the previous openable unit before this one opens as current work.")
            return@V2Scaffold
        }
        if (unit.number == 1) {
            Text("Letter board", style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { testTag = "v2-section-letter-board" })
            V2LetterBoard(pack.letters)
            Spacer(Modifier.height(12.dp))
        }
        Text("Sessions", style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { testTag = "v2-section-sessions" })
        unit.sessions.forEach { session ->
            V2Card(
                title = session.title,
                body = "${session.estimatedMinutes} min · ${session.steps.size} steps",
                onClick = { onOpenSession(session.id) },
            )
        }
        V2SecondaryButton("I completed the book work for this unit", { onAttestBook(unit.number) })
        unit.advancement?.let { rule ->
            Text(
                advancementRuleSummary(rule),
                modifier = Modifier.semantics { testTag = "v2-advancement-rule" },
            )
        }
        if (cumulativeReviewPolicy(unit.number) != null) {
            Text("Before advancing, complete the assigned earlier-objective review. Earlier lesson answers and optional practice do not complete this review.")
            V2SecondaryButton(cumulativeReviewTitle(unit.number, unit.advancement?.cumulativeReviewPolicyId), onOpenCumulativeReview, tag = "v2-open-cumulative-review")
        }
        if (decision == null) {
            V2Status("Preparing advancement")
        } else {
            V2Status(
                if (decision.allowed) {
                    "Advancement requirements are recorded. This is not proficiency."
                } else {
                    "Remaining: ${decision.remaining.joinToString("; ")}"
                },
            )
            V2PrimaryButton("Advance if requirements are met", { onAdvance(unit.number) }, enabled = decision.allowed, tag = "v2-advance")
        }
    }
}

@Composable
internal fun V2SessionScreen(
    pack: V2CurriculumPack,
    learner: V2LearnerView,
    sessionId: String,
    initialIndex: Int,
    remedial: Boolean,
    selections: Map<String, String>,
    lockedItemIds: Set<String>,
    pendingKeys: Set<String>,
    noteDrafts: Map<String, String>,
    onBack: () -> Unit,
    onCompleteStep: (String, () -> Unit) -> Unit,
    onAttempt: (String, String, String) -> Boolean,
    onBookmark: (String, String) -> Unit,
    onNoteDraft: (String, String) -> Unit,
    onNote: (String, String) -> Unit,
    onTutor: (String, String) -> Unit,
    onOpenReference: (String) -> Unit,
    onRemediate: (String) -> Unit,
    onReturnToCheck: () -> Unit,
    onPosition: (String, Int) -> Unit,
    onFinished: () -> Unit,
) {
    val session = pack.session(sessionId) ?: return
    var index by rememberSaveable(sessionId) {
        mutableIntStateOf(initialIndex.coerceIn(0, session.steps.lastIndex))
    }
    val routeStep = session.steps[index]
    var shownStepId by rememberSaveable(routeStep.id) { mutableStateOf(routeStep.id) }
    val step = if (remedial) pack.step(shownStepId)?.second ?: routeStep else routeStep
    val requiredItemIds = remember(pack, step.id) { stepCompletionItemIds(pack, step.id).toSet() }
    fun selectedAnswer(stepId: String, itemId: String): String? {
        return selections[governedSelectionKey(stepId, itemId)]
    }
    val guidedPractice = if (remedial) reviewedGuidedPractice(pack, routeStep.id) else emptyList()
    val guidedIndex = guidedPractice.indexOf(step.id)
    val navigationOpen = remember { mutableStateOf(true) }
    val consumedSteps = remember { mutableSetOf<String>() }
    DisposableEffect(Unit) {
        navigationOpen.value = true
        onDispose { navigationOpen.value = false }
    }
    if (!remedial) {
        LaunchedEffect(session.id, index) {
            onPosition(session.id, index)
        }
    }
    V2Scaffold(
        title = session.title,
        tab = null,
        onTab = {},
        onBack = onBack,
        footer = {
            androidx.compose.foundation.layout.Box(Modifier.padding(16.dp)) {
                if (remedial && guidedIndex < 0 && guidedPractice.isNotEmpty()) {
                    V2PrimaryButton(
                        text = "Review the guided practice",
                        onClick = { shownStepId = guidedPractice.first() },
                        tag = "v2-review-practice",
                    )
                } else if (remedial && guidedIndex >= 0 && guidedIndex < guidedPractice.lastIndex) {
                    V2PrimaryButton(
                        text = "Next guided practice",
                        onClick = { shownStepId = guidedPractice[guidedIndex + 1] },
                        tag = "v2-next-practice",
                    )
                } else if (remedial) {
                    V2PrimaryButton(
                        text = "Return to the check",
                        onClick = onReturnToCheck,
                        tag = "v2-return-to-check",
                    )
                } else {
                    val unanswered = requiredItemIds.any { selectedAnswer(step.id, it) == null }
                    V2PrimaryButton(
                        text = if (index == session.steps.lastIndex) "Finish session" else step.dominantAction,
                        onClick = {
                            if (requiredItemIds.any { selectedAnswer(step.id, it) == null }) return@V2PrimaryButton
                            val stepId = step.id
                            onCompleteStep(stepId) {
                                if (!navigationOpen.value || !consumedSteps.add(stepId)) return@onCompleteStep
                                if (index == session.steps.lastIndex) onFinished() else index += 1
                            }
                        },
                        enabled = "step:${step.id}" !in pendingKeys && !unanswered,
                        tag = "v2-session-continue",
                    )
                }
            }
        },
    ) {
        Text(step.title, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.semantics { heading(); testTag = "v2-step-title" })
        V2Status("Step ${index + 1} of ${session.steps.size} · ${step.kind}")
            Text(step.body)
            if (requiredItemIds.any { selectedAnswer(step.id, it) == null }) {
                V2Status(if (requiredItemIds.size == step.itemIds.size) {
                    "Answer each item before this step is recorded."
                } else {
                    "Answer each required item before this step is recorded."
                })
            }
        step.exampleIds.forEach { exampleId ->
            val example = pack.example(exampleId) ?: return@forEach
            Text(
                example.display,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.semantics { testTag = "v2-example-$exampleId" },
            )
            if (example.note.isNotBlank()) {
                Text(
                    example.note,
                    modifier = Modifier.semantics { testTag = "v2-example-note-$exampleId" },
                )
            }
        }
        step.unavailableReason?.let { V2Status("Unavailable: $it") }
        step.bookDirection?.let { Text("Book work: $it") }
        step.companionDirection?.let { Text("Companion: $it") }
        step.referenceIds.forEach { id ->
            V2SecondaryButton(pack.reference(id)?.title ?: id, { onOpenReference(id) })
        }
        step.itemIds.forEach { itemId ->
            val item = pack.item(itemId) ?: return@forEach
            if (itemId !in requiredItemIds) V2Status("Optional practice · not required to complete this step")
            val selected = selectedAnswer(step.id, item.id)
            val locked = item.id in lockedItemIds
            V2QuestionCue(item, "v2-prompt-letter")
            Text(item.prompt, style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { testTag = "v2-item-prompt-${item.id}" })
            lessonPresentedOptions(item, step.id).forEach { option ->
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .selectable(selected = selected == option, enabled = !locked,
                            role = Role.RadioButton,
                            onClick = { if (selected != option) onAttempt(item.id, option, step.id) })
                        .semantics { testTag = "v2-option-${item.id}-$option" },
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = selected == option,
                        enabled = !locked,
                        onClick = null,
                    )
                    Text(option)
                }
            }
            if (selected != null) {
                val correct = selected == item.answer
                Text(
                    if (correct) "Recorded as correct. ${item.feedback}" else "Not the reviewed answer. ${item.feedback}",
                    modifier = Modifier.semantics { testTag = "v2-item-feedback-${item.id}" },
                )
                if (!correct) {
                    step.remediationStepId?.let { target ->
                        V2SecondaryButton(
                            "Open the explanation",
                            { onRemediate(target) },
                            tag = "v2-remediation-${item.id}",
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        V2SecondaryButton("Bookmark this step", { onBookmark(step.id, "STEP") })
        val note = noteDrafts[step.id].orEmpty()
        OutlinedTextField(
            value = note,
            onValueChange = { onNoteDraft(step.id, it) },
            label = { Text("Local note — never scored") },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { testTag = "v2-note-draft" },
        )
        V2SecondaryButton(
            "Save note",
            { if (note.isNotBlank()) onNote(step.id, note) },
            enabled = note.isNotBlank() && "note:${step.id}" !in pendingKeys,
            tag = "v2-save-note",
        )
        V2SecondaryButton(
            "Mark a question for a tutor",
            { onTutor(step.id, note.ifBlank { "Please review this step." }) },
            enabled = "tutor:${step.id}" !in pendingKeys,
            tag = "v2-tutor",
        )
        learner.notes.filter { it.targetId == step.id }.forEach { saved ->
            Text(
                "Saved note, not scored: ${saved.text}",
                modifier = Modifier.semantics { testTag = "v2-saved-note-${saved.id}" },
            )
        }
        learner.tutorItems.filter { it.targetId == step.id }.forEach { saved ->
            Text(
                "Tutor question, not scored: ${saved.note}",
                modifier = Modifier.semantics { testTag = "v2-saved-tutor-${saved.id}" },
            )
        }
    }
}

@Composable
internal fun V2PracticeScreen(
    pack: V2CurriculumPack,
    learner: V2LearnerView,
    onTab: (V2Tab) -> Unit,
    onAlphabet: () -> Unit,
    onLab: (String) -> Unit,
    onSavedWork: () -> Unit,
) {
    val offers = rememberGoverned(pack, learner, label = "practice") {
        practiceOffers(pack, learner)
    }
    V2Scaffold(title = "Practice", tab = V2Tab.PRACTICE, onTab = onTab) {
        Text(
            "Alphabet Lab is the visual practice surface for printed letters.",
            modifier = Modifier.semantics { testTag = "v2-practice-heading" },
        )
        V2Card("Alphabet Lab", "Letter board, print pairs, and confusable forms.", "Available", onAlphabet)
        V2SecondaryButton("Saved work", onSavedWork, tag = "v2-open-saved-work")
        if (offers == null) {
            V2Status("Preparing practice")
            return@V2Scaffold
        }
        offers.forEach { offer ->
            if (offer.runnable) {
                V2Card(offer.title, offer.detail, "Optional", { onLab(offer.id) })
            } else {
                V2Status("${offer.title}. ${offer.detail}")
            }
        }
    }
}

@Composable
internal fun V2AlphabetLabScreen(
    pack: V2CurriculumPack,
    learner: V2LearnerView,
    onBack: () -> Unit,
    onRecognition: () -> Unit = {},
) {
    var selectedId by remember { mutableStateOf<String?>(null) }
    val selected = pack.letters.firstOrNull { it.id == selectedId }
    val confusable = pack.letters.filter { !it.latinLookalike.isNullOrBlank() }
    V2Scaffold(title = "Alphabet Lab", tab = null, onTab = {}, onBack = onBack) {
        Text(
            if (learner.transliterationEnabled) {
                "Transliteration support is on. Turn it off in Settings when print recognition is stable."
            } else {
                "Transliteration is off."
            },
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Text("Confusable forms", style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { heading(); testTag = "v2-section-confusable" })
        confusable.forEach { letter ->
            V2ConfusableRow(letter.uppercase, letter.latinLookalike.orEmpty())
        }
        Spacer(Modifier.height(12.dp))
        Text("Letter board", style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { testTag = "v2-section-letter-board" })
        V2LetterBoard(pack.letters, selectedId) { selectedId = it.id }
        selected?.let { letter ->
            V2Status("${letter.uppercase} ${letter.lowercase} · ${letter.group} · pending review")
        }
        Spacer(Modifier.height(12.dp))
        Text("Print pairs", style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { testTag = "v2-section-print-pairs" })
        V2PrintPairGrid(pack.letters)
        Spacer(Modifier.height(12.dp))
        V2SecondaryButton("Letter recognition checks", onRecognition, tag = "v2-alphabet-recognition")
        V2Status("Licensed letter audio is not installed. Listening and pronunciation are not scored here.")
    }
}

@Composable
internal fun V2ReferenceScreen(
    pack: V2CurriculumPack,
    learner: V2LearnerView,
    onTab: (V2Tab) -> Unit,
    onOpen: (String) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val entries = searchReferences(visibleReferences(pack, learner), query)
    V2LazyScaffold(title = "Reference", tab = V2Tab.REFERENCE, onTab = onTab) {
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search installed reference") },
                modifier = Modifier.fillMaxWidth().semantics { testTag = "v2-reference-search" },
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Only material introduced through the current unit is listed.",
                modifier = Modifier.semantics { testTag = "v2-reference-heading" },
            )
        }
        items(entries, key = { it.id }) { entry ->
            V2Card(entry.title, entry.summary, "Unit ${entry.introducedInUnit}", onClick = { onOpen(entry.id) })
        }
        if (entries.isEmpty()) {
            item { V2Status("No installed reference matches that search.") }
        }
    }
}

@Composable
internal fun V2ReferenceDetailScreen(
    pack: V2CurriculumPack,
    entryId: String,
    onBack: () -> Unit,
    onBookmark: (String, String) -> Unit,
) {
    val entry = pack.reference(entryId) ?: return
    V2Scaffold(title = entry.title, tab = null, onTab = {}, onBack = onBack) {
        Text(entry.summary, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text(entry.detail)
        V2Status("Review status: ${entry.reviewStatus}")
        V2SecondaryButton("Bookmark", { onBookmark(entry.id, "REFERENCE") })
    }
}

@Composable
internal fun V2ProgressScreen(
    pack: V2CurriculumPack,
    learner: V2LearnerView,
    onTab: (V2Tab) -> Unit,
    onSettings: () -> Unit,
    onBackup: () -> Unit,
) {
    val unit = pack.unit(learner.currentUnitNumber)
    val objectiveLines = rememberGoverned(pack, learner, label = "progress") {
        unit?.objectives.orEmpty().map { objective ->
            "${objective.title}: ${objectiveState(objective, learner, pack).name.replace('_', ' ').lowercase()}"
        }
    }
    V2Scaffold(title = "Progress", tab = V2Tab.PROGRESS, onTab = onTab) {
        Text(
            "Recorded activity and curriculum state. This is not fluency, mastery, or proficiency.",
            modifier = Modifier.semantics { testTag = "v2-progress-heading" },
        )
        V2Status("Current unit: ${unit?.title ?: "Unavailable"}")
        val currentCompletions = dev.russiancompanion.v2.curriculum.currentCompletedUnitNumbers(pack, learner)
        V2Status("Completed units: ${currentCompletions.sorted().joinToString().ifBlank { "none" }}")
        val historicalOnly = learner.completedUnitNumbers - currentCompletions
        if (historicalOnly.isNotEmpty()) {
            V2Status("Past completion records need current evidence: ${historicalOnly.sorted().joinToString()}")
        }
        V2Status("Book self-attestations: ${learner.bookAttestedUnits.sorted().joinToString().ifBlank { "none" }}")
        V2Status("In-app checks marked correct: ${learner.correctItemIds.size}. This count includes historical rows and is not current prescribed credit.")
        if (objectiveLines == null) {
            V2Status("Preparing objective state")
        } else {
            objectiveLines.forEach { line -> V2Status(line) }
        }
        Text("A local backup is saved only after you start it and it finishes. The app does not claim these records are protected unless that backup succeeded.")
        V2SecondaryButton("Back up or restore", onBackup, tag = "v2-open-backup")
        V2SecondaryButton("Settings, appearance, and attributions", onSettings)
    }
}

@Composable
internal fun V2SettingsScreen(
    appearance: AppearancePreference,
    appearanceSaveFailed: Boolean,
    transliterationEnabled: Boolean,
    onBack: () -> Unit,
    onAppearance: (AppearancePreference) -> Unit,
    onTransliteration: (Boolean) -> Unit,
    onAttributions: () -> Unit,
) {
    V2Scaffold(title = "Settings", tab = null, onTab = {}, onBack = onBack) {
        Text("Appearance", style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { testTag = "v2-settings-heading" })
        FilterChip(
            selected = appearance == AppearancePreference.DARK,
            onClick = { onAppearance(AppearancePreference.DARK) },
            label = { Text("Dark") },
            modifier = Modifier.heightIn(min = 48.dp),
        )
        FilterChip(
            selected = appearance == AppearancePreference.FOLLOW_SYSTEM,
            onClick = { onAppearance(AppearancePreference.FOLLOW_SYSTEM) },
            label = { Text("Follow system") },
            modifier = Modifier.heightIn(min = 48.dp),
        )
        if (appearanceSaveFailed) V2Status("Appearance was not saved.")
        Text("Transliteration", style = MaterialTheme.typography.titleMedium)
        CompanionToggle("Show transliteration support notes", transliterationEnabled, onTransliteration)
        Text("Limits", style = MaterialTheme.typography.titleMedium)
        V2Status("Unrelated catalog audio is not assigned to Units 1–2.")
        V2Status("The app does not score handwriting or pronunciation.")
        V2Status("Handwriting study uses your book until verified images exist.")
        V2Status("Unit 2 reviews Unit 1 letters and does not teach Lesson 3 grammar.")
        Text("Secondary tools", style = MaterialTheme.typography.titleMedium)
        V2SecondaryButton("Attributions and licenses", onAttributions)
        V2Status("Build identity and storage diagnostics stay here, not on Today.")
        V2Status("Version ${dev.russiancompanion.BuildConfig.VERSION_NAME}")
    }
}

@Composable
internal fun V2AttributionsScreen(pack: V2CurriculumPack, onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var catalog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<dev.russiancompanion.v2.notices.OfflineNoticeCatalog?>(null) }
    androidx.compose.runtime.LaunchedEffect(pack.packId, pack.packVersion) {
        catalog = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            dev.russiancompanion.v2.notices.loadOfflineNotices(context, pack)
        }
    }
    val notices = catalog
    V2LazyScaffold(title = "Attributions", tab = null, onTab = {}, onBack = onBack) {
        item {
            Text(
                "Licenses and attributions packaged with this app. These notices stay on the device.",
                modifier = Modifier.semantics { testTag = "v2-attributions-heading" },
            )
        }
        if (notices == null) {
            item { V2Status("Preparing notices") }
        } else {
            item { Text(notices.penguinStatement) }
            items(notices.sources, key = { "source:${it.id}" }) { source ->
                Text(source.creatorOrProject, style = MaterialTheme.typography.titleMedium)
                Text("Source: ${source.id}")
                Text("License: ${source.license}")
                Text(source.attribution)
                Text("Modification: ${source.modification}")
                Text("Share-alike: ${source.shareAlike}")
                source.licenseUrls.forEach { url -> Text(url) }
                V2Status(source.availability)
                Spacer(Modifier.height(12.dp))
            }
            items(notices.licenses, key = { "license:${it.name}" }) { license ->
                Text(license.title, style = MaterialTheme.typography.titleMedium)
                V2Status(license.availability)
                Text(license.body)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

/** Text semantics expose the same complete governed cue in lesson and optional practice. */
@Composable
internal fun V2QuestionCue(item: dev.russiancompanion.v2.curriculum.V2CheckItem, tag: String) {
    if (item.promptLetterRole != "CUE") return
    item.promptLetter?.takeIf { it.isNotBlank() }?.let { cue ->
        Text(
            cue,
            style = if (cue.length <= 3) MaterialTheme.typography.displayLarge else MaterialTheme.typography.titleLarge,
            modifier = Modifier.semantics { testTag = tag },
        )
    }
}
