package dev.russiancompanion.v2.ui

import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.data.V2ProfileEntity
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.withContext

internal interface V2Persistence {
    /**
     * Identity of the open database. A new wrapper around the same store keeps
     * in-flight work. A reopened store is a different key.
     */
    val bindingKey: Any get() = this

    suspend fun learner(): V2LearnerView
    suspend fun profile(): V2ProfileEntity
    suspend fun saveProfile(profile: V2ProfileEntity)
    suspend fun completeStep(stepId: String, recordedAt: String, contentDigest: String)
    suspend fun openCumulativeReview(pack: dev.russiancompanion.v2.curriculum.V2CurriculumPack, targetUnit: Int) { error("Review persistence unavailable") }
    suspend fun answerCumulativeReview(pack: dev.russiancompanion.v2.curriculum.V2CurriculumPack, assignment: String, slot: Int, selected: String) { error("Review persistence unavailable") }
    suspend fun recordAttempt(write: dev.russiancompanion.v2.curriculum.GovernedAttemptWrite)
    suspend fun toggleBookmark(targetId: String, kind: String, recordedAt: String, targetDigest: String)
    suspend fun addNote(targetId: String, text: String, recordedAt: String, targetDigest: String)
    suspend fun addTutor(targetId: String, note: String, recordedAt: String, targetDigest: String)
    suspend fun attestBook(unitNumber: Int, recordedAt: String)
    suspend fun completeUnit(unitNumber: Int, recordedAt: String)
    suspend fun advanceUnit(
        unitNumber: Int,
        recordedAt: String,
        nextUnitNumber: Int,
        nextSessionId: String?,
        nextResumeDigest: String,
        nextResumeSchema: Int,
    )
}

/**
 * Epoch captured when the user action started. Store writes recheck it after
 * any suspending work, including a test gate, and before mutation.
 */
internal class CapturedLearnerEpoch(val epoch: Long) : kotlin.coroutines.AbstractCoroutineContextElement(CapturedLearnerEpoch) {
    companion object Key : CoroutineContext.Key<CapturedLearnerEpoch>
}

internal suspend fun <T> withCapturedLearnerEpoch(epoch: Long, block: suspend () -> T): T {
    return withContext(CapturedLearnerEpoch(epoch)) { block() }
}

internal class StoreV2Persistence(private val store: LocalStore, private val pack: dev.russiancompanion.v2.curriculum.V2CurriculumPack? = null) : V2Persistence {
    override val bindingKey: Any = store

    override suspend fun learner(): V2LearnerView = store.v2LearnerViewAsync()

    override suspend fun profile(): V2ProfileEntity = store.v2ProfileAsync()

    override suspend fun saveProfile(profile: V2ProfileEntity) {
        val epoch = coroutineContext[CapturedLearnerEpoch]?.epoch
        if (epoch == null) {
            store.v2SaveProfileAsync(profile)
        } else if (!store.v2SaveProfileIfCurrentAsync(epoch, profile)) {
            error("stale profile")
        }
    }

    override suspend fun completeStep(stepId: String, recordedAt: String, contentDigest: String) {
        val epoch = coroutineContext[CapturedLearnerEpoch]?.epoch
        if (epoch == null) {
            store.v2CompleteStepAsync(stepId, recordedAt, contentDigest)
        } else if (!store.v2CompleteStepIfCurrentAsync(epoch, stepId, recordedAt, contentDigest)) {
            error("stale completion")
        }
    }

    override suspend fun openCumulativeReview(pack: dev.russiancompanion.v2.curriculum.V2CurriculumPack, targetUnit: Int) {
        val epoch = coroutineContext[CapturedLearnerEpoch]?.epoch ?: store.learnerMutationEpoch()
        val opened = store.awaitStore { store.openCumulativeReview(pack, targetUnit, epoch) }
        if (opened == null) error("Earlier teaching/check evidence is required or the learner changed")
    }
    override suspend fun answerCumulativeReview(pack: dev.russiancompanion.v2.curriculum.V2CurriculumPack, assignment: String, slot: Int, selected: String) {
        val epoch = coroutineContext[CapturedLearnerEpoch]?.epoch ?: store.learnerMutationEpoch()
        if (!store.awaitStore { store.answerCumulativeReview(pack, assignment, slot, selected, epoch) }) error("Review answer could not be saved")
    }

    override suspend fun recordAttempt(write: dev.russiancompanion.v2.curriculum.GovernedAttemptWrite) {
        val epoch = coroutineContext[CapturedLearnerEpoch]?.epoch
        if (epoch == null) {
            store.v2RecordGovernedAttemptAsync(write)
        } else if (!store.v2RecordGovernedAttemptIfCurrentAsync(epoch, write)) {
            error("stale attempt")
        }
    }

    override suspend fun toggleBookmark(targetId: String, kind: String, recordedAt: String, targetDigest: String) {
        val epoch = coroutineContext[CapturedLearnerEpoch]?.epoch
        if (epoch == null) {
            store.awaitStore {
                store.v2ToggleBookmark(
                    targetId,
                    kind,
                    recordedAt,
                    targetDigest,
                    dev.russiancompanion.v2.curriculum.GOVERNED_IDENTITY_SCHEMA,
                )
            }
        } else if (store.v2ToggleBookmarkIfCurrent(epoch, targetId, kind, recordedAt, targetDigest) == null) {
            error("stale bookmark")
        }
    }

    override suspend fun addNote(targetId: String, text: String, recordedAt: String, targetDigest: String) {
        val epoch = coroutineContext[CapturedLearnerEpoch]?.epoch
        if (epoch == null) {
            store.awaitStore {
                store.v2AddNote(
                    targetId,
                    text,
                    recordedAt,
                    targetDigest,
                    dev.russiancompanion.v2.curriculum.GOVERNED_IDENTITY_SCHEMA,
                )
            }
        } else if (!store.v2AddNoteIfCurrent(epoch, targetId, text, recordedAt, targetDigest)) {
            error("stale note")
        }
    }

    override suspend fun addTutor(targetId: String, note: String, recordedAt: String, targetDigest: String) {
        val epoch = coroutineContext[CapturedLearnerEpoch]?.epoch
        if (epoch == null) {
            store.awaitStore {
                store.v2AddTutorItem(
                    targetId,
                    note,
                    recordedAt,
                    targetDigest,
                    dev.russiancompanion.v2.curriculum.GOVERNED_IDENTITY_SCHEMA,
                )
            }
        } else if (!store.v2AddTutorItemIfCurrent(epoch, targetId, note, recordedAt, targetDigest)) {
            error("stale tutor")
        }
    }

    override suspend fun attestBook(unitNumber: Int, recordedAt: String) {
        val epoch = coroutineContext[CapturedLearnerEpoch]?.epoch
        if (epoch == null) {
            store.awaitStore { store.v2AttestBook(unitNumber, recordedAt) }
        } else if (!store.v2AttestBookIfCurrent(epoch, unitNumber, recordedAt)) {
            error("stale attestation")
        }
    }

    override suspend fun completeUnit(unitNumber: Int, recordedAt: String) {
        store.awaitStore { store.v2CompleteUnit(unitNumber, recordedAt) }
    }

    override suspend fun advanceUnit(
        unitNumber: Int,
        recordedAt: String,
        nextUnitNumber: Int,
        nextSessionId: String?,
        nextResumeDigest: String,
        nextResumeSchema: Int,
    ) {
        val epoch = coroutineContext[CapturedLearnerEpoch]?.epoch
        val saved = if (epoch == null) {
            store.v2AdvanceUnitAsync(
                unitNumber,
                recordedAt,
                nextUnitNumber,
                nextSessionId,
                nextResumeDigest,
                nextResumeSchema,
                pack,
            )
        } else {
            store.v2AdvanceUnitIfCurrentAsync(
                epoch,
                unitNumber,
                recordedAt,
                nextUnitNumber,
                nextSessionId,
                nextResumeDigest,
                nextResumeSchema,
                pack,
            )
        }
        if (!saved) error("stale advance")
    }
}
