package dev.russiancompanion.v2.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.Modifier
import dev.russiancompanion.v2.backup.REPLACE_EXPLANATION
import java.io.File

internal enum class V2BackupPick { CREATE, OPEN }

internal object V2BackupDocuments {
    var pickForTests: ((V2BackupPick) -> File?)? = null
}

@Composable
internal fun V2BackupScreen(
    coordinator: V2BackupCoordinator,
    recordedAt: () -> String,
    writesBusy: Boolean,
    onBack: () -> Unit,
    onRestored: () -> Unit,
) {
    val context = LocalContext.current
    val createLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri ->
        if (uri == null) coordinator.cancel() else coordinator.createBackup({ openOutput(context, uri) }, recordedAt())
    }
    val openLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) coordinator.cancel() else coordinator.openBackup { openInput(context, uri) }
    }
    val busy = coordinator.phase == V2BackupPhase.PREPARING ||
        coordinator.phase == V2BackupPhase.WRITING ||
        coordinator.phase == V2BackupPhase.VALIDATING ||
        coordinator.phase == V2BackupPhase.RESTORING
    V2Scaffold(title = "Backup and restore", tab = null, onTab = {}, onBack = onBack) {
        Text(
            "Backup and restore stay on this device. Nothing is uploaded.",
            modifier = Modifier.semantics { testTag = "v2-backup-heading" },
        )
        if (writesBusy) V2Status("Finish saving before backup or restore.")
        phaseLabel(coordinator.phase)?.let { V2Status(it) }
        if (coordinator.message.isNotEmpty()) {
            Text(coordinator.message, modifier = Modifier.semantics { testTag = "v2-backup-message" })
        }
        val preview = coordinator.preview
        if (coordinator.phase == V2BackupPhase.AWAITING_CONFIRMATION && preview != null) {
            Text(REPLACE_EXPLANATION)
            V2Status("Additions: ${preview.additions}")
            V2Status("Replacements: ${preview.replacements}")
            V2Status("Omissions: ${preview.omissions}")
            V2Status("Conflicts: ${preview.conflicts}")
            V2PrimaryButton(
                text = "Replace learner data",
                onClick = { coordinator.confirmRestore(onRestored) },
                enabled = !writesBusy,
                tag = "v2-backup-confirm",
            )
        }
        V2SecondaryButton(
            text = "Create backup",
            enabled = !busy && !writesBusy && coordinator.phase != V2BackupPhase.AWAITING_CONFIRMATION,
            tag = "v2-backup-create",
            onClick = {
                if (!coordinator.begin(V2BackupPhase.PREPARING)) return@V2SecondaryButton
                val testPick = V2BackupDocuments.pickForTests
                if (testPick != null) {
                    val file = testPick(V2BackupPick.CREATE)
                    if (file == null) coordinator.cancel() else coordinator.createBackup({ file.outputStream() }, recordedAt())
                } else {
                    createLauncher.launch("russian-companion-backup.zip")
                }
            },
        )
        V2SecondaryButton(
            text = "Restore backup",
            enabled = !busy && !writesBusy && coordinator.phase != V2BackupPhase.AWAITING_CONFIRMATION,
            tag = "v2-backup-restore",
            onClick = {
                if (!coordinator.begin(V2BackupPhase.PREPARING)) return@V2SecondaryButton
                val testPick = V2BackupDocuments.pickForTests
                if (testPick != null) {
                    val file = testPick(V2BackupPick.OPEN)
                    if (file == null) coordinator.cancel() else coordinator.openBackup { file.inputStream() }
                } else {
                    openLauncher.launch(arrayOf("application/zip", "application/octet-stream"))
                }
            },
        )
        if (busy || coordinator.phase == V2BackupPhase.AWAITING_CONFIRMATION) {
            V2SecondaryButton(text = "Cancel", onClick = { coordinator.cancel() }, tag = "v2-backup-cancel")
        }
    }
}

private fun phaseLabel(phase: V2BackupPhase): String? = when (phase) {
    V2BackupPhase.PREPARING -> "Preparing"
    V2BackupPhase.WRITING -> "Writing backup"
    V2BackupPhase.VALIDATING -> "Validating backup"
    V2BackupPhase.RESTORING -> "Restoring"
    V2BackupPhase.IDLE,
    V2BackupPhase.AWAITING_CONFIRMATION,
    V2BackupPhase.COMPLETED,
    V2BackupPhase.CANCELLED,
    V2BackupPhase.FAILED -> null
}

private fun openOutput(context: android.content.Context, uri: Uri) = context.contentResolver.openOutputStream(uri)

private fun openInput(context: android.content.Context, uri: Uri) = context.contentResolver.openInputStream(uri)
