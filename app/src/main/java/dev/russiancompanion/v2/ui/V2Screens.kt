package dev.russiancompanion.v2.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import dev.russiancompanion.v2.curriculum.canOpenUnit
import dev.russiancompanion.v2.curriculum.item
import dev.russiancompanion.v2.curriculum.objectiveState
import dev.russiancompanion.v2.curriculum.reference
import dev.russiancompanion.v2.curriculum.searchReferences
import dev.russiancompanion.v2.curriculum.session
import dev.russiancompanion.v2.curriculum.unit
import dev.russiancompanion.v2.curriculum.unitState
import dev.russiancompanion.v2.curriculum.visibleReferences

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
                    "Russian Companion is a fully offline course companion. It tells you the current Penguin-aligned unit, what to study in your own book, and what the app can practice with verified material.",
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
            .semantics {
                role = Role.Checkbox
                this.selected = selected
                stateDescription = if (selected) "Selected" else "Not selected"
            },
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        androidx.compose.material3.Checkbox(checked = selected, onCheckedChange = onChange)
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
            .semantics {
                role = Role.RadioButton
                this.selected = value == selected
                stateDescription = if (value == selected) "Selected" else "Not selected"
            },
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        RadioButton(selected = value == selected, onClick = { onSelect(value) })
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
    onSettings: () -> Unit,
) {
    val unit = pack.unit(learner.currentUnitNumber)
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
        Text(unit?.title ?: "Current unit unavailable", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.semantics { heading(); testTag = "v2-today-unit" })
        V2Status("Penguin-aligned Unit ${learner.currentUnitNumber}")
        Text(target.reason)
        Spacer(Modifier.height(12.dp))
        Text("Today’s next action", style = MaterialTheme.typography.titleMedium)
        Text(target.title, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        unit?.sessions.orEmpty().forEach { session ->
            val done = session.steps.all { it.id in learner.completedStepIds }
            V2Status("${session.title} — ${if (done) "Recorded" else "Not finished"} · ${session.estimatedMinutes} min")
        }
        Spacer(Modifier.height(16.dp))
        V2SecondaryButton("Settings and data", onSettings)
    }
}

@Composable
internal fun V2CourseScreen(
    pack: V2CurriculumPack,
    learner: V2LearnerView,
    onTab: (V2Tab) -> Unit,
    onOpenUnit: (Int) -> Unit,
) {
    V2LazyScaffold(title = "Course", tab = V2Tab.COURSE, onTab = onTab) {
        item {
            Text(
                "Thirty Penguin-aligned units. Unfinished units stay labeled Not yet installed and have no invented lesson body.",
                modifier = Modifier.padding(bottom = 12.dp),
            )
        }
        items(pack.units, key = { it.id }) { unit ->
            val state = unitState(pack, unit, learner)
            val status = when (state) {
                V2UnitState.CURRENT -> "Current"
                V2UnitState.AVAILABLE -> "Available"
                V2UnitState.COMPLETED -> "Completed"
                V2UnitState.LOCKED -> "Locked by prerequisite"
                V2UnitState.NOT_YET_INSTALLED -> "Not yet installed"
            }
            V2Card(
                title = "Unit ${unit.number} · ${unit.title}",
                body = unit.topicSummary,
                status = status,
                onClick = if (canOpenUnit(state) || state == V2UnitState.NOT_YET_INSTALLED || state == V2UnitState.LOCKED) {
                    { onOpenUnit(unit.number) }
                } else {
                    null
                },
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
) {
    val unit = pack.unit(unitNumber) ?: return
    val state = unitState(pack, unit, learner)
    V2Scaffold(title = "Unit ${unit.number}", tab = V2Tab.COURSE, onTab = onTab, onBack = onBack) {
        Text(unit.title, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.semantics { heading() })
        V2Status(
            when (state) {
                V2UnitState.NOT_YET_INSTALLED -> "Not yet installed"
                V2UnitState.LOCKED -> "Locked by prerequisite"
                V2UnitState.CURRENT -> "Current"
                V2UnitState.AVAILABLE -> "Available"
                V2UnitState.COMPLETED -> "Completed"
            },
        )
        Text(unit.topicSummary)
        V2Status(unit.mappingNote)
        if (state == V2UnitState.NOT_YET_INSTALLED) {
            Text("This unit is on the map so you can see the Penguin sequence. It has no lesson content, audio, or completion path.")
            return@V2Scaffold
        }
        if (state == V2UnitState.LOCKED) {
            Text("Finish the previous installed unit before this one opens as current work.")
            return@V2Scaffold
        }
        Text("Learning objectives", style = MaterialTheme.typography.titleMedium)
        unit.objectives.forEach { objective ->
            val label = when (objectiveState(objective, learner, pack)) {
                dev.russiancompanion.v2.curriculum.V2ObjectiveState.NOT_STARTED -> "Not started"
                dev.russiancompanion.v2.curriculum.V2ObjectiveState.LEARNING -> "Learning"
                dev.russiancompanion.v2.curriculum.V2ObjectiveState.REVIEW_REQUIRED -> "Review required"
                dev.russiancompanion.v2.curriculum.V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS -> "Demonstrated in app checks"
                dev.russiancompanion.v2.curriculum.V2ObjectiveState.COMPLETED_BY_SELF_ATTESTATION_ONLY -> "Completed by self-attestation only"
            }
            V2Status("${objective.title} — $label")
        }
        Text("Sessions", style = MaterialTheme.typography.titleMedium)
        unit.sessions.forEach { session ->
            V2Card(
                title = session.title,
                body = "${session.estimatedMinutes} min · ${session.steps.size} steps",
                onClick = { onOpenSession(session.id) },
            )
        }
        V2SecondaryButton("I completed the book work for this unit", { onAttestBook(unit.number) })
        val decision = advancementDecision(pack, unit, learner)
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

@Composable
internal fun V2SessionScreen(
    pack: V2CurriculumPack,
    learner: V2LearnerView,
    sessionId: String,
    initialIndex: Int,
    onBack: () -> Unit,
    onCompleteStep: (String) -> Unit,
    onAttempt: (String, String, Boolean) -> Unit,
    onBookmark: (String, String) -> Unit,
    onNote: (String, String) -> Unit,
    onTutor: (String, String) -> Unit,
    onOpenReference: (String) -> Unit,
    onFinished: () -> Unit,
) {
    val session = pack.session(sessionId) ?: return
    var index by remember { mutableIntStateOf(initialIndex.coerceIn(0, session.steps.lastIndex)) }
    val step = session.steps[index]
    var selected by remember(step.id) { mutableStateOf<String?>(null) }
    var note by remember(step.id) { mutableStateOf("") }
    V2Scaffold(
        title = session.title,
        tab = null,
        onTab = {},
        onBack = onBack,
        footer = {
            androidx.compose.foundation.layout.Box(Modifier.padding(16.dp)) {
                V2PrimaryButton(
                    text = if (index == session.steps.lastIndex) "Finish session" else step.dominantAction,
                    onClick = {
                        onCompleteStep(step.id)
                        if (index == session.steps.lastIndex) onFinished() else index += 1
                    },
                    tag = "v2-session-continue",
                )
            }
        },
    ) {
        Text(step.title, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.semantics { heading(); testTag = "v2-step-title" })
        V2Status("Step ${index + 1} of ${session.steps.size} · ${step.kind}")
        Text(step.body)
        step.unavailableReason?.let { V2Status("Unavailable: $it") }
        step.bookDirection?.let { Text("Book work: $it") }
        step.companionDirection?.let { Text("Companion: $it") }
        step.referenceIds.forEach { id ->
            V2SecondaryButton(pack.reference(id)?.title ?: id, { onOpenReference(id) })
        }
        step.itemIds.forEach { itemId ->
            val item = pack.item(itemId) ?: return@forEach
            Text(item.prompt, style = MaterialTheme.typography.titleMedium)
            item.options.forEach { option ->
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .semantics {
                            role = Role.RadioButton
                            this.selected = selected == option
                        },
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = selected == option,
                        onClick = {
                            selected = option
                            onAttempt(item.id, option, option == item.answer)
                        },
                    )
                    Text(option)
                }
            }
            if (selected != null) {
                Text(if (selected == item.answer) "Recorded as correct. ${item.feedback}" else "Not the reviewed answer. ${item.feedback}")
                step.remediationStepId?.let { V2Status("Remediation: return to the explanation for this check.") }
            }
        }
        Spacer(Modifier.height(12.dp))
        V2SecondaryButton("Bookmark this step", { onBookmark(step.id, "STEP") })
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Local note — never scored") },
            modifier = Modifier.fillMaxWidth(),
        )
        V2SecondaryButton("Save note", { if (note.isNotBlank()) onNote(step.id, note) }, enabled = note.isNotBlank())
        V2SecondaryButton("Mark a question for a tutor", { onTutor(step.id, note.ifBlank { "Please review this step." }) })
    }
}

@Composable
internal fun V2PracticeScreen(
    pack: V2CurriculumPack,
    learner: V2LearnerView,
    onTab: (V2Tab) -> Unit,
    onAlphabet: () -> Unit,
) {
    V2Scaffold(title = "Practice", tab = V2Tab.PRACTICE, onTab = onTab) {
        Text("Tools for the current and previous units only. Unmapped catalog audio is not a practice library.")
        V2Card("Letter recognition", "Browse the Unit 1 letter sequence.", "Available", onAlphabet)
        V2Card("Sound-symbol review", "English notes pending review. No invented words.", "Available", onAlphabet)
        V2Card("Staged decoding", "Letter and print-pair level only.", "Limited", onAlphabet)
        V2Card(
            "Licensed model playback",
            "No reviewer-mapped audio is installed for Units 1–2.",
            "Unavailable",
        )
        V2Card(
            "Listen-record-compare",
            "The recorder exists, but this unit has no mapped model. The app will not score pronunciation.",
            "Unavailable for current unit",
        )
        V2Card("Current-unit review", "Reopen Unit ${learner.currentUnitNumber} checks from Course.", "Available") { onTab(V2Tab.COURSE) }
        V2Card("Manual self-assessment", "Book and Anki work stay self-attested.", "Available") { onTab(V2Tab.PROGRESS) }
        V2Status("Bookmarks and tutor marks are saved from inside a session.")
    }
}

@Composable
internal fun V2AlphabetLabScreen(pack: V2CurriculumPack, learner: V2LearnerView, onBack: () -> Unit) {
    val groups = pack.letters.groupBy { it.group }
    V2LazyScaffold(title = "Alphabet Lab", tab = null, onTab = {}, onBack = onBack) {
        item {
            Text(
                if (learner.transliterationEnabled) {
                    "Transliteration support is on. Turn it off in Settings when print recognition is stable."
                } else {
                    "Transliteration is off."
                },
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
        groups.forEach { (group, letters) ->
            item { Text(group, style = MaterialTheme.typography.titleMedium) }
            items(letters, key = { it.id }) { letter ->
                V2Card(
                    title = "${letter.uppercase} ${letter.lowercase}",
                    body = letter.englishNote,
                    status = "Pending linguistic review · no example word · no audio",
                )
            }
        }
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
            Text("Only material introduced through the current unit is listed.")
        }
        items(entries, key = { it.id }) { entry ->
            V2Card(entry.title, entry.summary, "Unit ${entry.introducedInUnit}") { onOpen(entry.id) }
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
) {
    val unit = pack.unit(learner.currentUnitNumber)
    V2Scaffold(title = "Progress", tab = V2Tab.PROGRESS, onTab = onTab) {
        Text("Recorded activity and curriculum state. This is not fluency, mastery, or proficiency.")
        V2Status("Current unit: ${unit?.title ?: "Unavailable"}")
        V2Status("Completed units: ${learner.completedUnitNumbers.sorted().joinToString().ifBlank { "none" }}")
        V2Status("Book self-attestations: ${learner.bookAttestedUnits.sorted().joinToString().ifBlank { "none" }}")
        V2Status("In-app checks marked correct: ${learner.correctItemIds.size}")
        unit?.objectives.orEmpty().forEach { objective ->
            V2Status("${objective.title}: ${objectiveState(objective, learner, pack).name.replace('_', ' ').lowercase()}")
        }
        Text("Backup is a local export you start. The app does not claim data is protected unless that export succeeded. Restore preview is not finished in this slice.")
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
        Text("Appearance", style = MaterialTheme.typography.titleMedium)
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
        Text("Secondary tools", style = MaterialTheme.typography.titleMedium)
        V2SecondaryButton("Attributions and licenses", onAttributions)
        V2Status("Build identity and storage diagnostics stay here, not on Today.")
        V2Status("Version ${dev.russiancompanion.BuildConfig.VERSION_NAME}")
    }
}

@Composable
internal fun V2AttributionsScreen(onBack: () -> Unit) {
    V2Scaffold(title = "Attributions", tab = null, onTab = {}, onBack = onBack) {
        Text("Letter identities are public-domain writing-system facts.")
        Text("English instruction in this pack is independently authored.")
        Text("Packaged Shtooka and OpenRussian catalog files remain a rights archive. They are not assigned to Units 1–2.")
        Text("Share-alike catalog notices stay in the packaged license texts. Public release is not approved.")
    }
}
