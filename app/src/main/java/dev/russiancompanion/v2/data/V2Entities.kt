package dev.russiancompanion.v2.data

import androidx.room.ForeignKey
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.russiancompanion.v2.curriculum.UNRECOVERABLE_GOVERNED_IDENTITY

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
    @ColumnInfo(defaultValue = "''") val resumeContentDigest: String = "",
    @ColumnInfo(defaultValue = "0") val resumeIdentitySchema: Int = 0,
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
    @ColumnInfo(defaultValue = "'UNRECOVERABLE'") val contentDigest: String = UNRECOVERABLE_GOVERNED_IDENTITY,
    @ColumnInfo(defaultValue = "0") val identitySchema: Int = 0,
)

@Entity(tableName = "v2_attempts", indices = [Index("itemId")])
internal data class V2AttemptEntity(
    @PrimaryKey val id: String,
    val itemId: String,
    val selected: String,
    val correct: Boolean,
    val recordedAtLocal: String,
    @ColumnInfo(defaultValue = "'UNRECOVERABLE'") val itemDigest: String = UNRECOVERABLE_GOVERNED_IDENTITY,
    @ColumnInfo(defaultValue = "'UNRECOVERABLE'") val unitContentDigest: String = UNRECOVERABLE_GOVERNED_IDENTITY,
    @ColumnInfo(defaultValue = "'UNRECOVERABLE'") val ruleDigest: String = UNRECOVERABLE_GOVERNED_IDENTITY,
    @ColumnInfo(defaultValue = "0") val identitySchema: Int = 0,
    @ColumnInfo(defaultValue = "'LEGACY_UNVERIFIED'") val attemptRole: String = "LEGACY_UNVERIFIED",
    @ColumnInfo(defaultValue = "''") val objectiveId: String = "",
    @ColumnInfo(defaultValue = "0") val unitNumber: Int = 0,
    @ColumnInfo(defaultValue = "''") val stepId: String = "",
    /** 0 means this row predates durable write order. Do not infer order from the clock. */
    @ColumnInfo(defaultValue = "0") val acceptedOrder: Long = 0,
)

@Entity(tableName = "v2_bookmarks")
internal data class V2BookmarkEntity(
    @PrimaryKey val targetId: String,
    val kind: String,
    val createdAtLocal: String,
    @ColumnInfo(defaultValue = "'UNRECOVERABLE'") val targetDigest: String = UNRECOVERABLE_GOVERNED_IDENTITY,
    @ColumnInfo(defaultValue = "0") val identitySchema: Int = 0,
)

@Entity(tableName = "v2_notes")
internal data class V2NoteEntity(
    @PrimaryKey val id: String,
    val targetId: String,
    val text: String,
    val createdAtLocal: String,
    val userAuthoredRussian: Boolean,
    @ColumnInfo(defaultValue = "'UNRECOVERABLE'") val targetDigest: String = UNRECOVERABLE_GOVERNED_IDENTITY,
    @ColumnInfo(defaultValue = "0") val identitySchema: Int = 0,
)

@Entity(tableName = "v2_tutor_queue")
internal data class V2TutorItemEntity(
    @PrimaryKey val id: String,
    val targetId: String,
    val note: String,
    val createdAtLocal: String,
    @ColumnInfo(defaultValue = "'UNRECOVERABLE'") val targetDigest: String = UNRECOVERABLE_GOVERNED_IDENTITY,
    @ColumnInfo(defaultValue = "0") val identitySchema: Int = 0,
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

/** Immutable serialized assignment/answer identities; no UPDATE/REPLACE write API. */
@Entity(tableName = "v2_review_assignments")
internal data class V2ReviewAssignmentEntity(@PrimaryKey val id: String, val targetUnit: Int, val document: String)

@Entity(tableName = "v2_review_answers",
    foreignKeys = [ForeignKey(entity = V2ReviewAssignmentEntity::class, parentColumns = ["id"],
        childColumns = ["assignmentId"], onDelete = ForeignKey.RESTRICT)],
    indices = [Index("assignmentId"), Index(value = ["acceptedOrder"], unique = true)])
internal data class V2ReviewAnswerEntity(@PrimaryKey val id: String, val assignmentId: String,
    val acceptedOrder: Long, val document: String)
