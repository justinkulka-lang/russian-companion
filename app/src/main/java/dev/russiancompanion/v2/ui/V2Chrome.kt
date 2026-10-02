package dev.russiancompanion.v2.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.PaddingValues
import dev.russiancompanion.v2.curriculum.V2Letter

/** Horizontal inset inside every scroll clip so the first glyph is not shaved off. */
internal val V2ContentInset = 24.dp

internal val LocalV2PersistenceStatus = staticCompositionLocalOf { "" }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun V2Scaffold(
    title: String,
    tab: V2Tab?,
    onTab: (V2Tab) -> Unit,
    onBack: (() -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .semantics { testTag = "v2-scaffold" },
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { V2TopBar(title = title, onBack = onBack) },
        bottomBar = {
            Column(Modifier.padding(horizontal = V2ContentInset)) {
                footer?.invoke()
                if (tab != null) {
                    V2BottomBar(tab = tab, onTab = onTab)
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = V2ContentInset, vertical = 12.dp)
                .semantics { testTag = "v2-content" },
        ) {
            val persistenceStatus = LocalV2PersistenceStatus.current
            if (persistenceStatus.isNotEmpty()) {
                Text(
                    persistenceStatus,
                    modifier = Modifier.semantics { testTag = "v2-persistence-state" },
                )
                Spacer(Modifier.height(8.dp))
            }
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun V2LazyScaffold(
    title: String,
    tab: V2Tab?,
    onTab: (V2Tab) -> Unit,
    onBack: (() -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    content: LazyListScope.() -> Unit,
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .semantics { testTag = "v2-scaffold" },
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { V2TopBar(title = title, onBack = onBack) },
        bottomBar = {
            Column(Modifier.padding(horizontal = V2ContentInset)) {
                footer?.invoke()
                if (tab != null) V2BottomBar(tab = tab, onTab = onTab)
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .semantics { testTag = "v2-content" },
            contentPadding = PaddingValues(horizontal = V2ContentInset, vertical = 8.dp),
            content = content,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun V2TopBar(title: String, onBack: (() -> Unit)?) {
    CenterAlignedTopAppBar(
        modifier = Modifier.padding(horizontal = V2ContentInset),
        windowInsets = WindowInsets(0, 0, 0, 0),
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.semantics { heading() },
            )
        },
        navigationIcon = {
            if (onBack != null) {
                TextButton(
                    onClick = onBack,
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .padding(start = 4.dp)
                        .semantics { testTag = "v2-back" },
                ) { Text("Back") }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
    )
}

@Composable
internal fun V2BottomBar(tab: V2Tab, onTab: (V2Tab) -> Unit) {
    NavigationBar(
        modifier = Modifier.semantics { testTag = "v2-bottom-nav" },
        containerColor = MaterialTheme.colorScheme.surface,
        windowInsets = WindowInsets(0, 0, 0, 0),
    ) {
        V2Tab.entries.forEach { destination ->
            NavigationBarItem(
                selected = destination == tab,
                onClick = { onTab(destination) },
                icon = { Text(destination.label.take(1), style = MaterialTheme.typography.labelLarge) },
                label = { Text(destination.label) },
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .semantics {
                        role = Role.Tab
                        stateDescription = if (destination == tab) "Selected" else "Not selected"
                    },
            )
        }
    }
}

private val V2Tab.label: String
    get() = when (this) {
        V2Tab.TODAY -> "Today"
        V2Tab.COURSE -> "Course"
        V2Tab.PRACTICE -> "Practice"
        V2Tab.REFERENCE -> "Reference"
        V2Tab.PROGRESS -> "Progress"
    }

@Composable
internal fun V2PrimaryButton(text: String, onClick: () -> Unit, enabled: Boolean = true, tag: String? = null) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .then(if (tag != null) Modifier.semantics { testTag = tag } else Modifier),
    ) { Text(text) }
}

@Composable
internal fun V2SecondaryButton(text: String, onClick: () -> Unit, enabled: Boolean = true, tag: String? = null) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .then(if (tag != null) Modifier.semantics { testTag = tag } else Modifier),
    ) { Text(text) }
}

@Composable
internal fun V2Card(title: String, body: String, status: String? = null, onClick: (() -> Unit)? = null) {
    Card(
        onClick = onClick ?: {},
        enabled = onClick != null,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            if (status != null) {
                Text(
                    status,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.semantics { stateDescription = status },
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
internal fun V2UnitRow(
    number: Int,
    topic: String,
    state: String,
    progress: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .semantics { testTag = "v2-unit-row-$number" },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.semantics { heading() },
            )
            Column(Modifier.weight(1f)) {
                Text("Unit $number · $topic", style = MaterialTheme.typography.titleSmall)
                Text(
                    "$state · $progress",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.semantics { stateDescription = state },
                )
            }
        }
    }
}

@Composable
internal fun V2LetterTile(
    letter: V2Letter,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    Surface(
        onClick = onClick ?: {},
        enabled = onClick != null,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        modifier = Modifier
            .size(width = 64.dp, height = 72.dp)
            .semantics { testTag = "v2-letter-${letter.uppercase}" },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(letter.uppercase, style = MaterialTheme.typography.headlineMedium)
            Text(letter.lowercase, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun V2LetterBoard(
    letters: List<V2Letter>,
    selectedId: String? = null,
    onSelect: ((V2Letter) -> Unit)? = null,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .semantics { testTag = "v2-letter-board" },
    ) {
        letters.forEach { letter ->
            V2LetterTile(
                letter = letter,
                selected = letter.id == selectedId,
                onClick = onSelect?.let { handler -> { handler(letter) } },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun V2PrintPairGrid(letters: List<V2Letter>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .semantics { testTag = "v2-print-pair-grid" },
    ) {
        letters.forEach { letter ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.heightIn(min = 48.dp),
            ) {
                Text(
                    "${letter.uppercase}  ${letter.lowercase}",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
internal fun V2ConfusableRow(cyrillic: String, latin: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .semantics { testTag = "v2-confusable-$cyrillic" },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
            Text(
                cyrillic,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(start = 4.dp),
            )
        Text("is not", style = MaterialTheme.typography.bodyLarge)
        Text(latin, style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
internal fun V2Status(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 4.dp).semantics { stateDescription = text },
    )
}
