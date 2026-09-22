package dev.russiancompanion.domain.navigation

import dev.russiancompanion.domain.pathway.firstPrescribedPlayable
import dev.russiancompanion.domain.plan.ChecklistActivity
import dev.russiancompanion.domain.plan.ChecklistItem
import dev.russiancompanion.domain.plan.RecordedGateStatus

internal sealed interface ContinueDestination {
    data object SelectLesson : ContinueDestination
    data class BookPractice(val planDefinitionId: String) : ContinueDestination
    data class AnkiPractice(val planDefinitionId: String) : ContinueDestination
    data class ListenRecordCompare(
        val audioModelId: String,
        val planDefinitionId: String,
    ) : ContinueDestination
    data class GateEvidence(val gateId: String) : ContinueDestination
    data object Progress : ContinueDestination
    data object Unavailable : ContinueDestination
}

internal fun resolveContinueDestination(snapshot: NavigationSnapshot): ContinueDestination {
    if (!snapshot.localDataOpen || snapshot.seedCorrupt) return ContinueDestination.Unavailable
    if (snapshot.activeSegmentId == null || !activeLessonHasPlan(snapshot)) {
        return ContinueDestination.SelectLesson
    }
    val next = snapshot.dailyPlan.nextIncompleteRequired
    if (next != null) return destinationForRequiredItem(snapshot, next)
    val pendingGate = snapshot.gateSummaries.firstOrNull { summary ->
        summary.status != RecordedGateStatus.SATISFIED
    }
    return if (pendingGate != null) {
        ContinueDestination.GateEvidence(pendingGate.id)
    } else {
        ContinueDestination.Progress
    }
}

internal fun destinationForRequiredItem(
    snapshot: NavigationSnapshot,
    item: ChecklistItem,
): ContinueDestination {
    return when (item.activity) {
        ChecklistActivity.BOOK -> ContinueDestination.BookPractice(item.planDefinitionId)
        ChecklistActivity.ANKI_DROID -> ContinueDestination.AnkiPractice(item.planDefinitionId)
        ChecklistActivity.TUTOR -> ContinueDestination.BookPractice(item.planDefinitionId)
        ChecklistActivity.LISTEN_RECORD_COMPARE -> listenDestination(snapshot, item)
    }
}

internal fun listenDestination(
    snapshot: NavigationSnapshot,
    item: ChecklistItem,
): ContinueDestination {
    val unfinished = unfinishedPrescribedAudioIds(snapshot, item.audioModelIds)
    val playable = snapshot.audio.filter { it.playable }.map { it.id }.toSet()
    val audioId = firstPrescribedPlayable(unfinished, playable)
        ?: firstPrescribedPlayable(item.audioModelIds, playable)
    return if (audioId == null) {
        ContinueDestination.Unavailable
    } else {
        ContinueDestination.ListenRecordCompare(audioId, item.planDefinitionId)
    }
}

internal fun unfinishedPrescribedAudioIds(
    snapshot: NavigationSnapshot,
    prescribedAudioModelIds: List<String>,
): List<String> {
    val completed = snapshot.audio.filter { it.completed }.map { it.id }.toSet()
    val unfinished = prescribedAudioModelIds.filter { it !in completed }
    return unfinished.ifEmpty { prescribedAudioModelIds }
}

internal fun continueOpensUnrelatedAudio(
    destination: ContinueDestination,
    prescribedAudioModelIds: List<String>,
): Boolean {
    return destination is ContinueDestination.ListenRecordCompare &&
        destination.audioModelId !in prescribedAudioModelIds
}
