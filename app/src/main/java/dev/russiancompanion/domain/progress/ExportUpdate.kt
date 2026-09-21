package dev.russiancompanion.domain.progress

internal sealed interface ExportUpdate {
    data class Completed(val empty: Boolean) : ExportUpdate

    data class RecordingUnreadable(val recordingId: String) : ExportUpdate

    data object DestinationUnavailable : ExportUpdate

    data object NotConfirmed : ExportUpdate
}
