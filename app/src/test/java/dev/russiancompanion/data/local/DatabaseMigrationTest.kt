package dev.russiancompanion.data.local

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.RussianCompanionApplication
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class DatabaseMigrationTest {
    @Test
    fun everyEarlierDevelopmentDatabaseResetsAndThenKeepsCurrentWritesOnReopen() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        (context.applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
        val root = generateSequence(File(System.getProperty("user.dir"))) { it.parentFile }
            .first { File(it, "settings.gradle.kts").isFile }
        for (version in 1..11) {
            val name = "dev-reset-$version-${System.nanoTime()}.db"
            val schema = JSONObject(File(root,
                "app/schemas/dev.russiancompanion.data.local.AppDatabase/$version.json").readText())
                .getJSONObject("database")
            val helper = FrameworkSQLiteOpenHelperFactory().create(
                SupportSQLiteOpenHelper.Configuration.builder(context).name(name)
                    .callback(object : SupportSQLiteOpenHelper.Callback(version) {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            val entities = schema.getJSONArray("entities")
                            for (i in 0 until entities.length()) {
                                val entity = entities.getJSONObject(i)
                                val table = entity.getString("tableName")
                                db.execSQL(entity.getString("createSql").replace("\${TABLE_NAME}", table))
                                val indices = entity.optJSONArray("indices") ?: continue
                                for (j in 0 until indices.length()) db.execSQL(indices.getJSONObject(j)
                                    .getString("createSql").replace("\${TABLE_NAME}", table))
                            }
                            val setup = schema.getJSONArray("setupQueries")
                            for (i in 0 until setup.length()) db.execSQL(setup.getString(i))
                            db.execSQL("INSERT INTO export_records " +
                                "(id, exportedAtLocal, logCount, evidenceCount, progressCount, recordingCount, status) " +
                                "VALUES ('discarded-dev-export','old-time',1,1,1,1,'EXPORTED')")
                            // A retired table must not survive a destructive pre-v11 reset.
                            db.execSQL("CREATE TABLE retired_dev_rows (id TEXT NOT NULL PRIMARY KEY)")
                            db.execSQL("INSERT INTO retired_dev_rows VALUES ('old-learner')")
                        }
                        override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
                    }).build(),
            )
            try {
                helper.writableDatabase
                helper.close()
                val current = LocalStore.openNamed(context, name).use { store ->
                    assertTrue("v$version exports reset", store.exports().isEmpty())
                    assertTrue(store.v2Attempts().isEmpty())
                    assertTrue(store.v2StepProgress().isEmpty())
                    assertTrue(store.cumulativeReviewHistory().first.isEmpty())
                    assertTrue(store.cumulativeReviewHistory().second.isEmpty())
                    assertFalse(store.v2LearnerView().onboardingCompleted)
                    assertTrue(store.v2LearnerView().completedUnitNumbers.isEmpty())
                    store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 4))
                    store.v2AttestBook(4, "current-write")
                    store.governedLearnerDocument()
                }
                LocalStore.openNamed(context, name).use { store ->
                    assertEquals("v$version current writes survive reopen", current, store.governedLearnerDocument())
                }
                android.database.sqlite.SQLiteDatabase.openDatabase(context.getDatabasePath(name).path,
                    null, android.database.sqlite.SQLiteDatabase.OPEN_READONLY).use { db ->
                    assertEquals(12, db.version)
                    db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='retired_dev_rows'",
                        null).use { cursor ->
                        assertTrue(cursor.moveToFirst())
                        assertEquals(if (version == 11) 1 else 0, cursor.getInt(0))
                    }
                }
            } finally {
                helper.close()
                context.deleteDatabase(name)
            }
        }
    }

    @Test
    fun version11DevLearnerRowsAreDiscarded() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.deleteDatabase("dev-reset-v11.db")
        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name("dev-reset-v11.db")
                .callback(object : SupportSQLiteOpenHelper.Callback(11) {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        db.execSQL("CREATE TABLE `course_segments` (`id` TEXT NOT NULL, PRIMARY KEY(`id`))")
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
                        ).forEach { table ->
                            db.execSQL("CREATE TABLE `$table` (`id` TEXT NOT NULL, PRIMARY KEY(`id`))")
                        }
                    }

                    override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
                })
                .build(),
        )
        val db = helper.writableDatabase
        db.execSQL("INSERT INTO course_segments (id) VALUES ('kept')")
        db.execSQL("INSERT INTO v2_review_assignments (id) VALUES ('old-assignment')")
        db.execSQL("INSERT INTO v2_review_answers (id) VALUES ('old-answer')")
        db.execSQL("INSERT INTO v2_profiles (id) VALUES ('local')")
        db.execSQL("INSERT INTO learner_state (id) VALUES ('local')")
        MIGRATION_11_12.migrate(db)
        fun count(table: String): Int {
            val cursor = db.query("SELECT COUNT(*) FROM `$table`")
            cursor.moveToFirst()
            val rows = cursor.getInt(0)
            cursor.close()
            return rows
        }
        assertEquals(1, count("course_segments"))
        assertEquals(0, count("v2_review_assignments"))
        assertEquals(0, count("v2_review_answers"))
        assertEquals(0, count("v2_profiles"))
        assertEquals(0, count("learner_state"))
        helper.close()
    }

}
