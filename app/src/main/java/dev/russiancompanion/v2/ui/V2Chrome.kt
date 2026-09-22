package dev.russiancompanion.v2.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp

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
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
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
                            modifier = Modifier.heightIn(min = 48.dp),
                        ) { Text("Back") }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
        bottomBar = {
            Column {
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
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            content = content,
        )
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
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
                },
                navigationIcon = {
                    if (onBack != null) {
                        TextButton(onClick = onBack, modifier = Modifier.heightIn(min = 48.dp)) { Text("Back") }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
        bottomBar = {
            Column {
                footer?.invoke()
                if (tab != null) V2BottomBar(tab = tab, onTab = onTab)
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            content = content,
        )
    }
}

@Composable
internal fun V2BottomBar(tab: V2Tab, onTab: (V2Tab) -> Unit) {
    NavigationBar(
        modifier = Modifier.semantics { testTag = "v2-bottom-nav" },
        containerColor = MaterialTheme.colorScheme.surface,
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
internal fun V2SecondaryButton(text: String, onClick: () -> Unit, enabled: Boolean = true) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
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
internal fun V2Status(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 4.dp).semantics { stateDescription = text },
    )
}
