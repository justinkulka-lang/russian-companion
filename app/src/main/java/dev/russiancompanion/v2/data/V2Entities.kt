package dev.russiancompanion.v2.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "v2_profiles")
internal data class V2ProfileEntity(
    @PrimaryKey val id: String,
    val onboardingCompleted: Boolean,
    val currentUnitNumber: Int,
    val startingPoint: String,
    val hasPenguin: Boolean,
    val hasAnki: Boolean,
    val hasBinder: Boolean,
    val hasTutor: Boolean,
    val dailyMinutes: Int,
    val transliterationEnabled: Boolean,
    val currentSessionId: String?,
    val currentStepIndex: Int,
) {
    companion object {
        const val LOCAL_ID = "local"

        fun fresh(): V2ProfileEntity {
            return V2ProfileEntity(
                id = LOCAL_ID,
                onboardingCompleted = false,
                currentUnitNumber = 1,
                startingPoint = "NEW_TO_CYRILLIC",
                hasPenguin = true,
                hasAnki = false,
                hasBinder = false,
                hasTutor = false,
                dailyMinutes = 35,
                transliterationEnabled = true,
                currentSessionId = null,
                currentStepIndex = 0,
            )
        }
    }
}

@Entity(tableName = "v2_step_progress", indices = [Index("stepId")])
internal data class V2StepProgressEntity(
    @PrimaryKey val stepId: String,
    val completedAtLocal: String,
)

@Entity(tableName = "v2_attempts", indices = [Index("itemId")])
internal data class V2AttemptEntity(
    @PrimaryKey val id: String,
    val itemId: String,
    val selected: String,
    val correct: Boolean,
    val recordedAtLocal: String,
)

@Entity(tableName = "v2_bookmarks")
internal data class V2BookmarkEntity(
    @PrimaryKey val targetId: String,
    val kind: String,
    val createdAtLocal: String,
)

@Entity(tableName = "v2_notes")
internal data class V2NoteEntity(
    @PrimaryKey val id: String,
    val targetId: String,
    val text: String,
    val createdAtLocal: String,
    val userAuthoredRussian: Boolean,
)

@Entity(tableName = "v2_tutor_queue")
internal data class V2TutorItemEntity(
    @PrimaryKey val id: String,
    val targetId: String,
    val note: String,
    val createdAtLocal: String,
)

@Entity(tableName = "v2_book_attestations")
internal data class V2BookAttestationEntity(
    @PrimaryKey val unitNumber: Int,
    val recordedAtLocal: String,
)

@Entity(tableName = "v2_unit_completions")
internal data class V2UnitCompletionEntity(
    @PrimaryKey val unitNumber: Int,
    val recordedAtLocal: String,
)
