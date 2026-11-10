package dev.russiancompanion.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** Dev installs drop learner rows. Old review-assignment documents are not converted. */
internal val MIGRATION_11_12 = object : Migration(11, 12) {
    override fun migrate(db: SupportSQLiteDatabase) {
        listOf(
            "v2_review_answers",
            "v2_review_assignments",
            "v2_step_progress",
            "v2_attempts",
            "v2_bookmarks",
            "v2_notes",
            "v2_tutor_queue",
            "v2_book_attestations",
            "v2_unit_completions",
            "v2_profiles",
            "recording_records",
            "activity_logs",
            "plan_instances",
            "learner_state",
            "gate_evidence",
            "gate_tutor_requirements",
            "gate_decisions",
            "export_records",
        ).forEach { table -> db.execSQL("DELETE FROM `$table`") }
    }
}

/** New development queue schema starts with fresh learner data, never converted schedules. */
internal val MIGRATION_12_13 = object : Migration(12, 13) {
    override fun migrate(db: SupportSQLiteDatabase) {
        MIGRATION_11_12.migrate(db)
        db.execSQL("CREATE TABLE IF NOT EXISTS `v2_spaced_reviews` (`itemId` TEXT NOT NULL, `document` TEXT NOT NULL, PRIMARY KEY(`itemId`))")
    }
}
