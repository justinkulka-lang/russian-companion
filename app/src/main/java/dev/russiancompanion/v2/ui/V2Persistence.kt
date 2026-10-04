package dev.russiancompanion.v2.ui

import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.data.V2ProfileEntity

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
    suspend fun recordAttempt(write: dev.russiancompanion.v2.curriculum.GovernedAttemptWrite)
    suspend fun toggleBookmark(targetId: String, kind: String, recordedAt: String, targetDigest: String)
    suspend fun addNote(targetId: String, text: String, recordedAt: String, targetDigest: String)
    suspend fun addTutor(targetId: String, note: String, recordedAt: String, targetDigest: String)
    suspend fun attestBook(unitNumber: Int, recordedAt: String)
    suspend fun completeUnit(unitNumber: Int, recordedAt: String)
    suspend fun advanceUnit(unitNumber: Int, recordedAt: String, nextUnitNumber: Int, nextSessionId: String?)
}

internal class StoreV2Persistence(private val store: LocalStore) : V2Persistence {
    override val bindingKey: Any = store

    override suspend fun learner(): V2LearnerView = store.v2LearnerViewAsync()

    override suspend fun profile(): V2ProfileEntity = store.v2ProfileAsync()

    override suspend fun saveProfile(profile: V2ProfileEntity) {
        store.v2SaveProfileAsync(profile)
    }

    override suspend fun completeStep(stepId: String, recordedAt: String, contentDigest: String) {
        store.v2CompleteStepAsync(stepId, recordedAt, contentDigest)
    }

    override suspend fun recordAttempt(write: dev.russiancompanion.v2.curriculum.GovernedAttemptWrite) {
        store.v2RecordGovernedAttemptAsync(write)
    }

    override suspend fun toggleBookmark(targetId: String, kind: String, recordedAt: String, targetDigest: String) {
        store.awaitStore {
            store.v2ToggleBookmark(
                targetId,
                kind,
                recordedAt,
                targetDigest,
                dev.russiancompanion.v2.curriculum.GOVERNED_IDENTITY_SCHEMA,
            )
        }
    }

    override suspend fun addNote(targetId: String, text: String, recordedAt: String, targetDigest: String) {
        store.awaitStore {
            store.v2AddNote(
                targetId,
                text,
                recordedAt,
                targetDigest,
                dev.russiancompanion.v2.curriculum.GOVERNED_IDENTITY_SCHEMA,
            )
        }
    }

    override suspend fun addTutor(targetId: String, note: String, recordedAt: String, targetDigest: String) {
        store.awaitStore {
            store.v2AddTutorItem(
                targetId,
                note,
                recordedAt,
                targetDigest,
                dev.russiancompanion.v2.curriculum.GOVERNED_IDENTITY_SCHEMA,
            )
        }
    }

    override suspend fun attestBook(unitNumber: Int, recordedAt: String) {
        store.awaitStore { store.v2AttestBook(unitNumber, recordedAt) }
    }

    override suspend fun completeUnit(unitNumber: Int, recordedAt: String) {
        store.awaitStore { store.v2CompleteUnit(unitNumber, recordedAt) }
    }

    override suspend fun advanceUnit(unitNumber: Int, recordedAt: String, nextUnitNumber: Int, nextSessionId: String?) {
        store.v2AdvanceUnitAsync(unitNumber, recordedAt, nextUnitNumber, nextSessionId)
    }
}
