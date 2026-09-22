package dev.russiancompanion.v2.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
internal interface V2Dao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertProfileIfAbsent(profile: V2ProfileEntity): Long

    @Query("SELECT * FROM v2_profiles WHERE id = :id")
    fun profile(id: String): V2ProfileEntity?

    @Update
    fun updateProfile(profile: V2ProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertStep(progress: V2StepProgressEntity)

    @Query("SELECT * FROM v2_step_progress")
    fun steps(): List<V2StepProgressEntity>

    @Insert
    fun insertAttempt(attempt: V2AttemptEntity)

    @Query("SELECT * FROM v2_attempts ORDER BY recordedAtLocal, id")
    fun attempts(): List<V2AttemptEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertBookmark(bookmark: V2BookmarkEntity)

    @Query("SELECT * FROM v2_bookmarks ORDER BY createdAtLocal")
    fun bookmarks(): List<V2BookmarkEntity>

    @Query("DELETE FROM v2_bookmarks WHERE targetId = :targetId")
    fun deleteBookmark(targetId: String)

    @Insert
    fun insertNote(note: V2NoteEntity)

    @Query("SELECT * FROM v2_notes ORDER BY createdAtLocal")
    fun notes(): List<V2NoteEntity>

    @Insert
    fun insertTutorItem(item: V2TutorItemEntity)

    @Query("SELECT * FROM v2_tutor_queue ORDER BY createdAtLocal")
    fun tutorQueue(): List<V2TutorItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertBookAttestation(row: V2BookAttestationEntity)

    @Query("SELECT * FROM v2_book_attestations")
    fun bookAttestations(): List<V2BookAttestationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertUnitCompletion(row: V2UnitCompletionEntity)

    @Query("SELECT * FROM v2_unit_completions")
    fun unitCompletions(): List<V2UnitCompletionEntity>
}
