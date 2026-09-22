package dev.russiancompanion.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Adds the recording-to-log link and the export document reference.
 * Existing rows keep their data. Recordings that were not created with a
 * completion log keep a null activity-log id.
 */
internal val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `export_records` ADD COLUMN `documentReference` TEXT")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `recording_records_new` (
                `id` TEXT NOT NULL,
                `audioModelId` TEXT NOT NULL,
                `localFileReference` TEXT NOT NULL,
                `createdAtLocal` TEXT NOT NULL,
                `markedForTutorReview` INTEGER NOT NULL,
                `completionState` TEXT NOT NULL,
                `activityLogId` TEXT,
                PRIMARY KEY(`id`),
                FOREIGN KEY(`audioModelId`) REFERENCES `audio_models`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT,
                FOREIGN KEY(`activityLogId`) REFERENCES `activity_logs`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT
            )
            """.trimIndent(),
        )
        db.execSQL(
            """
            INSERT INTO `recording_records_new` (
                `id`,
                `audioModelId`,
                `localFileReference`,
                `createdAtLocal`,
                `markedForTutorReview`,
                `completionState`,
                `activityLogId`
            )
            SELECT
                `id`,
                `audioModelId`,
                `localFileReference`,
                `createdAtLocal`,
                `markedForTutorReview`,
                `completionState`,
                NULL
            FROM `recording_records`
            """.trimIndent(),
        )
        db.execSQL("DROP TABLE `recording_records`")
        db.execSQL("ALTER TABLE `recording_records_new` RENAME TO `recording_records`")
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_recording_records_audioModelId` ON `recording_records` (`audioModelId`)",
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_recording_records_activityLogId` ON `recording_records` (`activityLogId`)",
        )
    }
}

/**
 * Adds pathway lesson numbers and prescribed audio-model IDs on plan definitions.
 * Existing rows keep their data; new columns default to unknown lesson and no audio IDs.
 */
internal val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE `course_segments` ADD COLUMN `lessonNumber` INTEGER NOT NULL DEFAULT 0",
        )
        db.execSQL("ALTER TABLE `plan_definitions` ADD COLUMN `lessonNumber` INTEGER")
        db.execSQL(
            "ALTER TABLE `plan_definitions` ADD COLUMN `audioModelIds` TEXT NOT NULL DEFAULT ''",
        )
    }
}
