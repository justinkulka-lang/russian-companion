package dev.russiancompanion.data.local

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class DatabaseMigrationTest {
    @Test
    fun version1RowsKeepTheirDataAndGainTheNewNullableFields() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name("phase6-v1.db")
                .callback(object : SupportSQLiteOpenHelper.Callback(1) {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        db.execSQL("CREATE TABLE IF NOT EXISTS `audio_models` (`id` TEXT NOT NULL, PRIMARY KEY(`id`))")
                        db.execSQL("CREATE TABLE IF NOT EXISTS `activity_logs` (`id` TEXT NOT NULL, PRIMARY KEY(`id`))")
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS `recording_records` (
                                `id` TEXT NOT NULL,
                                `audioModelId` TEXT NOT NULL,
                                `localFileReference` TEXT NOT NULL,
                                `createdAtLocal` TEXT NOT NULL,
                                `markedForTutorReview` INTEGER NOT NULL,
                                `completionState` TEXT NOT NULL,
                                PRIMARY KEY(`id`),
                                FOREIGN KEY(`audioModelId`) REFERENCES `audio_models`(`id`)
                                ON UPDATE NO ACTION ON DELETE RESTRICT
                            )
                            """.trimIndent(),
                        )
                        db.execSQL(
                            "CREATE INDEX IF NOT EXISTS `index_recording_records_audioModelId` ON `recording_records` (`audioModelId`)",
                        )
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS `export_records` (
                                `id` TEXT NOT NULL,
                                `exportedAtLocal` TEXT NOT NULL,
                                `destinationReference` TEXT,
                                `logCount` INTEGER NOT NULL,
                                `evidenceCount` INTEGER NOT NULL,
                                `progressCount` INTEGER NOT NULL,
                                `recordingCount` INTEGER NOT NULL,
                                `status` TEXT NOT NULL,
                                PRIMARY KEY(`id`)
                            )
                            """.trimIndent(),
                        )
                    }

                    override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
                })
                .build(),
        )
        val db = helper.writableDatabase
        db.execSQL("PRAGMA foreign_keys = ON")
        db.execSQL("INSERT INTO audio_models (id) VALUES ('audio-1')")
        db.execSQL(
            """
            INSERT INTO recording_records (
                id, audioModelId, localFileReference, createdAtLocal, markedForTutorReview, completionState
            ) VALUES ('rec-1', 'audio-1', 'recordings/rec-1.m4a', '2026-09-21T09:00', 1, 'NOT_COMPLETED')
            """.trimIndent(),
        )
        db.execSQL(
            """
            INSERT INTO export_records (
                id, exportedAtLocal, destinationReference, logCount, evidenceCount, progressCount, recordingCount, status
            ) VALUES ('exp-1', '2026-09-21T10:00', 'notes.zip', 1, 0, 7, 0, 'COMPLETED')
            """.trimIndent(),
        )
        MIGRATION_1_2.migrate(db)

        val recording = db.query("SELECT localFileReference, activityLogId FROM recording_records WHERE id = 'rec-1'")
        recording.moveToFirst()
        assertEquals("recordings/rec-1.m4a", recording.getString(0))
        assertNull(recording.getString(1))
        recording.close()

        val exported = db.query("SELECT destinationReference, documentReference FROM export_records WHERE id = 'exp-1'")
        exported.moveToFirst()
        assertEquals("notes.zip", exported.getString(0))
        assertNull(exported.getString(1))
        exported.close()
        helper.close()
    }

    @Test
    fun version2PlanRowsGainLessonNumberAndAudioModelIdColumns() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name("capability1-v2.db")
                .callback(object : SupportSQLiteOpenHelper.Callback(2) {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS `course_segments` (
                                `id` TEXT NOT NULL,
                                `lessonReference` TEXT NOT NULL,
                                `sortOrder` INTEGER NOT NULL,
                                `eligibilityStatus` TEXT NOT NULL,
                                PRIMARY KEY(`id`)
                            )
                            """.trimIndent(),
                        )
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS `plan_definitions` (
                                `id` TEXT NOT NULL,
                                `segmentId` TEXT NOT NULL,
                                `activityType` TEXT NOT NULL,
                                `instruction` TEXT NOT NULL,
                                `sortOrder` INTEGER NOT NULL,
                                `required` INTEGER NOT NULL,
                                `eligibilityStatus` TEXT NOT NULL,
                                PRIMARY KEY(`id`),
                                FOREIGN KEY(`segmentId`) REFERENCES `course_segments`(`id`)
                                ON UPDATE NO ACTION ON DELETE RESTRICT
                            )
                            """.trimIndent(),
                        )
                    }

                    override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
                })
                .build(),
        )
        val db = helper.writableDatabase
        db.execSQL(
            """
            INSERT INTO course_segments (id, lessonReference, sortOrder, eligibilityStatus)
            VALUES ('segment-1', 'Lesson 1', 1, 'ELIGIBLE')
            """.trimIndent(),
        )
        db.execSQL(
            """
            INSERT INTO plan_definitions (
                id, segmentId, activityType, instruction, sortOrder, required, eligibilityStatus
            ) VALUES ('plan-book', 'segment-1', 'BOOK', 'Use your copy', 1, 1, 'ELIGIBLE')
            """.trimIndent(),
        )
        MIGRATION_2_3.migrate(db)
        val segment = db.query("SELECT lessonNumber FROM course_segments WHERE id = 'segment-1'")
        segment.moveToFirst()
        assertEquals(0, segment.getInt(0))
        segment.close()
        val plan = db.query("SELECT lessonNumber, audioModelIds FROM plan_definitions WHERE id = 'plan-book'")
        plan.moveToFirst()
        assertNull(plan.getString(0))
        assertEquals("", plan.getString(1))
        plan.close()
        helper.close()
    }
}
