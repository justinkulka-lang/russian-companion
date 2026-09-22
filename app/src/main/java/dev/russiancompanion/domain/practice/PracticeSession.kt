package dev.russiancompanion.domain.practice

internal interface PracticeAudio {
    fun modelPlayable(localAudioReference: String): Boolean

    fun recordingReadable(localFileReference: String): Boolean

    fun playModel(localAudioReference: String, onFinished: (Boolean) -> Unit)

    fun playRecording(localFileReference: String, onFinished: (Boolean) -> Unit)

    fun stopPlayback()

    /** Starts a capture and returns its private file reference, or null when capture does not start. */
    fun startCapture(): String?

    /**
     * Stops the current capture.
     * When [keep] is true and the file is readable, returns that file reference.
     */
    fun stopCapture(keep: Boolean): String?
}

internal data class PracticeProgress(
    val modelPlayable: Boolean,
    val modelPlayed: Boolean,
    val hasReadableRecording: Boolean,
    val modelReplayedAfterRecording: Boolean,
    val userRecordingPlayed: Boolean,
    val microphoneGranted: Boolean,
    val alreadyCompleted: Boolean,
)

internal data class PracticeWriteAdmission(
    val accepted: Boolean,
    val reason: String? = null,
)

internal data class StoredRecordingRef(
    val audioModelId: String,
    val localFileReference: String,
    val completed: Boolean,
)

/**
 * Recording begins only after a playable licensed model has been heard and
 * the microphone is granted. A recording-only substitute is never offered.
 * A completed readable take cannot be replaced; an unreadable take can.
 */
internal fun canStartRecording(progress: PracticeProgress): Boolean {
    if (!progress.modelPlayable || !progress.modelPlayed || !progress.microphoneGranted) return false
    if (!progress.hasReadableRecording) return true
    return !progress.alreadyCompleted
}

/**
 * Completion requires the A/B sequence against a playable model and a
 * readable local recording. It never depends on a pronunciation score.
 */
internal fun canCompletePractice(progress: PracticeProgress): Boolean {
    return progress.modelPlayable &&
        progress.modelPlayed &&
        progress.hasReadableRecording &&
        progress.modelReplayedAfterRecording &&
        progress.userRecordingPlayed &&
        !progress.alreadyCompleted
}

internal fun recordingCommitAccepted(
    modelEligible: Boolean,
    localFileReference: String,
    requestedCompleted: Boolean,
): PracticeWriteAdmission {
    if (!modelEligible) return PracticeWriteAdmission(false, "ineligible-model")
    if (localFileReference.isBlank()) return PracticeWriteAdmission(false, "file-required")
    if (requestedCompleted) return PracticeWriteAdmission(false, "completion-not-committed")
    return PracticeWriteAdmission(true)
}

internal fun listenCompletionAccepted(
    modelEligible: Boolean,
    expectedAudioModelId: String,
    recording: StoredRecordingRef?,
): PracticeWriteAdmission {
    if (!modelEligible) return PracticeWriteAdmission(false, "ineligible-model")
    if (recording == null) return PracticeWriteAdmission(false, "recording-required")
    if (recording.audioModelId != expectedAudioModelId) {
        return PracticeWriteAdmission(false, "wrong-model")
    }
    if (recording.localFileReference.isBlank()) {
        return PracticeWriteAdmission(false, "file-required")
    }
    if (recording.completed) return PracticeWriteAdmission(false, "already-completed")
    return PracticeWriteAdmission(true)
}

internal data class ListenPlanRef(
    val id: String,
    val segmentId: String,
    val audioModelIds: List<String>,
)

internal fun listenPlanPrescribing(
    plans: List<ListenPlanRef>,
    activeSegmentId: String?,
    audioModelId: String,
): ListenPlanRef? {
    if (activeSegmentId == null || audioModelId.isBlank()) return null
    return plans.firstOrNull { plan ->
        plan.segmentId == activeSegmentId && audioModelId in plan.audioModelIds
    }
}

internal fun completedRecordingCountsTowardPlan(recording: StoredRecordingRef): Boolean {
    return recording.completed && recording.localFileReference.isNotBlank()
}

/**
 * A listen plan completes only when every prescribed model has a valid
 * completed recording, including the model being completed now. Library
 * audio outside the prescription never satisfies the plan.
 */
internal fun prescribedListenPlanComplete(
    prescribedAudioModelIds: List<String>,
    completedAudioModelIds: Set<String>,
    completingAudioModelId: String,
): Boolean {
    if (prescribedAudioModelIds.isEmpty() || completingAudioModelId !in prescribedAudioModelIds) {
        return false
    }
    val done = completedAudioModelIds + completingAudioModelId
    return prescribedAudioModelIds.all { it in done }
}

internal fun previousAdmittedModelId(admittedIds: List<String>, currentId: String): String? {
    val index = admittedIds.indexOf(currentId)
    if (index <= 0) return null
    return admittedIds[index - 1]
}

internal fun nextAdmittedModelId(admittedIds: List<String>, currentId: String): String? {
    val index = admittedIds.indexOf(currentId)
    if (index < 0 || index >= admittedIds.lastIndex) return null
    return admittedIds[index + 1]
}

internal fun practiceJudgmentLabelForbidden(label: String): Boolean {
    val lowered = label.lowercase()
    return listOf("score", "fluency", "accent", "transcript", "pass", "fail", "correct", "incorrect")
        .any { token -> lowered.contains(token) }
}
