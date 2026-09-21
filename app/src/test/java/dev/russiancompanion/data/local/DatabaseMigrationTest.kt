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
}
