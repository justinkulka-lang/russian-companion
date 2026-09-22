package dev.russiancompanion.presentation.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.russiancompanion.R
import dev.russiancompanion.domain.navigation.LessonOption
import dev.russiancompanion.domain.navigation.NavigationSnapshot
import dev.russiancompanion.domain.navigation.OnboardingConfirmation
import dev.russiancompanion.domain.navigation.onboardingConfirmation

@Composable
internal fun OnboardingScreen(
    snapshot: NavigationSnapshot,
    onAttributions: () -> Unit,
    onConfirmLesson: (String) -> Unit,
    onReloadSeed: () -> Unit,
    operationInProgress: Boolean = false,
    confirmFailed: Boolean = false,
) {
    var page by rememberSaveable { mutableIntStateOf(0) }
    var selectedLessonId by rememberSaveable { mutableStateOf<String?>(null) }
    val confirmation = onboardingConfirmation(snapshot, selectedLessonId)
    if (page == 0) {
        OnboardingIntroduction(
            onNext = { page = 1 },
            onAttributions = onAttributions,
        )
    } else {
        OnboardingLessonSelection(
            snapshot = snapshot,
            selectedLessonId = selectedLessonId,
            confirmation = confirmation,
            operationInProgress = operationInProgress,
            confirmFailed = confirmFailed,
            onSelectLesson = { selectedLessonId = it },
            onConfirm = { selectedLessonId?.let(onConfirmLesson) },
            onReloadSeed = onReloadSeed,
            onBack = { page = 0 },
        )
    }
}

@Composable
internal fun OnboardingIntroduction(
    onNext: () -> Unit,
    onAttributions: () -> Unit,
) {
    ShellPage(
        title = stringResource(R.string.onboarding_title),
        bottomBar = {
            PersistentActionBar {
                PrimaryAction(
                    text = stringResource(R.string.navigate_next),
                    onClick = onNext,
                )
            }
        },
    ) {
        Text(
            text = stringResource(R.string.onboarding_role),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = stringResource(R.string.onboarding_book),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = stringResource(R.string.onboarding_limits),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.onboarding_local_data),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        SecondaryAction(
            text = stringResource(R.string.open_attributions),
            onClick = onAttributions,
        )
    }
}

@Composable
internal fun OnboardingLessonSelection(
    snapshot: NavigationSnapshot,
    selectedLessonId: String?,
    confirmation: OnboardingConfirmation,
    operationInProgress: Boolean,
    confirmFailed: Boolean,
    onSelectLesson: (String) -> Unit,
    onConfirm: () -> Unit,
    onReloadSeed: () -> Unit,
    onBack: () -> Unit,
) {
    val plansUnavailable = confirmation == OnboardingConfirmation.PLAN_DEFINITIONS_UNAVAILABLE ||
        confirmation == OnboardingConfirmation.CATALOG_UNAVAILABLE ||
        snapshot.lessons.none { it.planCount > 0 }
    ShellPage(
        title = stringResource(R.string.onboarding_select_lesson),
        onBack = onBack,
        scrollable = false,
        busy = operationInProgress,
        bottomBar = {
            PersistentActionBar {
                PrimaryAction(
                    text = stringResource(R.string.onboarding_confirm),
                    onClick = onConfirm,
                    enabled = confirmation == OnboardingConfirmation.READY && !operationInProgress,
                )
            }
        },
    ) {
        if (selectedLessonId == null || snapshot.lessons.isEmpty()) {
            Text(
                stringResource(R.string.onboarding_lesson_required),
                color = MaterialTheme.colorScheme.onBackground,
            )
        } else {
            val selected = snapshot.lessons.firstOrNull { it.id == selectedLessonId }
            Text(
                stringResource(
                    R.string.onboarding_lesson_selected,
                    selected?.lessonReference ?: selectedLessonId,
                ),
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        if (plansUnavailable) {
            Text(
                stringResource(R.string.onboarding_plans_unavailable),
                color = MaterialTheme.colorScheme.error,
            )
            SecondaryAction(
                text = stringResource(R.string.navigate_retry),
                onClick = onReloadSeed,
                enabled = !operationInProgress,
            )
        }
        if (confirmFailed) {
            Text(
                stringResource(R.string.onboarding_confirm_failed),
                color = MaterialTheme.colorScheme.error,
            )
        }
        LessonSelectionList(
            lessons = snapshot.lessons,
            selectedLessonId = selectedLessonId,
            enabled = !operationInProgress,
            onSelectLesson = onSelectLesson,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true),
        )
    }
}

@Composable
internal fun LessonSelectionList(
    lessons: List<LessonOption>,
    selectedLessonId: String?,
    enabled: Boolean,
    onSelectLesson: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (lessons.isEmpty()) {
            item("empty") {
                Text(
                    stringResource(R.string.onboarding_lesson_required),
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
        items(lessons, key = { it.id }) { lesson ->
            LessonSelectionRow(
                lesson = lesson,
                selected = lesson.id == selectedLessonId,
                enabled = enabled,
                onSelect = { onSelectLesson(lesson.id) },
            )
        }
    }
}

@Composable
internal fun LessonSelectionRow(
    lesson: LessonOption,
    selected: Boolean,
    enabled: Boolean,
    onSelect: () -> Unit,
) {
    val container = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val content = if (selected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .selectable(
                selected = selected,
                enabled = enabled,
                role = Role.RadioButton,
                onClick = onSelect,
            )
            .semantics { this.selected = selected },
        color = container,
        contentColor = content,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            RadioButton(
                selected = selected,
                onClick = null,
                enabled = enabled,
            )
            Text(
                text = lesson.lessonReference,
                style = MaterialTheme.typography.bodyLarge,
                color = content,
            )
        }
    }
}
