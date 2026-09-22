package dev.russiancompanion.data.local

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.domain.gate.ConditionOutcome
import dev.russiancompanion.domain.gate.GateEvidenceKind
import dev.russiancompanion.domain.gate.GateStatus
import dev.russiancompanion.domain.gate.RecordedEvidence
import dev.russiancompanion.domain.gate.UNRECOVERABLE_RULE_SNAPSHOT
import dev.russiancompanion.domain.gate.USER_ADDED_TUTOR_CONDITION_ID
import dev.russiancompanion.domain.gate.decodeConditionOutcomes
import dev.russiancompanion.domain.gate.defaultAdvancementRule
import dev.russiancompanion.domain.gate.displayedRuleText
import dev.russiancompanion.domain.gate.parseEvidenceIds
import dev.russiancompanion.domain.gate.parseGateRuleSnapshot
import dev.russiancompanion.domain.gate.reproduceDecision
import dev.russiancompanion.domain.gate.reproducedDecisionMatches
import dev.russiancompanion.domain.gate.tutorSignOffCondition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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
    fun version2PlanRowsBecomeIneligibleWhenLessonNumberCannotBeRecovered() {
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
                        db.execSQL(
                            "CREATE TABLE IF NOT EXISTS `audio_models` (`id` TEXT NOT NULL, PRIMARY KEY(`id`))",
                        )
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS `gate_definitions` (
                                `id` TEXT NOT NULL,
                                `segmentId` TEXT NOT NULL,
                                `ruleVersion` TEXT NOT NULL,
                                `displayedRuleText` TEXT NOT NULL,
                                `eligibilityStatus` TEXT NOT NULL,
                                PRIMARY KEY(`id`)
                            )
                            """.trimIndent(),
                        )
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS `activity_logs` (
                                `id` TEXT NOT NULL,
                                `category` TEXT NOT NULL,
                                `durationMinutes` INTEGER NOT NULL,
                                `completed` INTEGER NOT NULL,
                                `recordedAtLocal` TEXT NOT NULL,
                                `segmentId` TEXT,
                                `planInstanceId` TEXT,
                                `selfAttested` INTEGER NOT NULL,
                                PRIMARY KEY(`id`)
                            )
                            """.trimIndent(),
                        )
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS `recording_records` (
                                `id` TEXT NOT NULL,
                                `audioModelId` TEXT NOT NULL,
                                `localFileReference` TEXT NOT NULL,
                                `createdAtLocal` TEXT NOT NULL,
                                `markedForTutorReview` INTEGER NOT NULL,
                                `completionState` TEXT NOT NULL,
                                `activityLogId` TEXT,
                                PRIMARY KEY(`id`)
                            )
                            """.trimIndent(),
                        )
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS `gate_evidence` (
                                `id` TEXT NOT NULL,
                                `gateDefinitionId` TEXT NOT NULL,
                                `evidenceType` TEXT NOT NULL,
                                `enteredValue` TEXT NOT NULL,
                                `selfAttestationStatus` TEXT NOT NULL,
                                `recordedAtLocal` TEXT NOT NULL,
                                PRIMARY KEY(`id`)
                            )
                            """.trimIndent(),
                        )
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS `gate_decisions` (
                                `id` TEXT NOT NULL,
                                `gateDefinitionId` TEXT NOT NULL,
                                `ruleVersion` TEXT NOT NULL,
                                `evidenceIds` TEXT NOT NULL,
                                `conditionOutcomes` TEXT NOT NULL,
                                `status` TEXT NOT NULL,
                                `evaluatedAtLocal` TEXT NOT NULL,
                                PRIMARY KEY(`id`)
                            )
                            """.trimIndent(),
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
                                `documentReference` TEXT,
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
        db.execSQL("INSERT INTO audio_models (id) VALUES ('audio-1')")
        db.execSQL(
            """
            INSERT INTO gate_definitions (id, segmentId, ruleVersion, displayedRuleText, eligibilityStatus)
            VALUES ('gate-1', 'segment-1', '1', 'Displayed test rule', 'ELIGIBLE')
            """.trimIndent(),
        )
        db.execSQL(
            """
            INSERT INTO activity_logs (
                id, category, durationMinutes, completed, recordedAtLocal, segmentId, planInstanceId, selfAttested
            ) VALUES ('log-1', 'GRAMMAR', 0, 1, '2026-09-21T09:00', 'segment-1', NULL, 1)
            """.trimIndent(),
        )
        db.execSQL(
            """
            INSERT INTO recording_records (
                id, audioModelId, localFileReference, createdAtLocal, markedForTutorReview, completionState, activityLogId
            ) VALUES ('rec-1', 'audio-1', 'recordings/rec-1.m4a', '2026-09-21T09:01', 0, 'NOT_COMPLETED', NULL)
            """.trimIndent(),
        )
        db.execSQL(
            """
            INSERT INTO gate_evidence (
                id, gateDefinitionId, evidenceType, enteredValue, selfAttestationStatus, recordedAtLocal
            ) VALUES ('ev-1', 'gate-1', 'BOOK_REVISION_SCORE', '1', 'SELF_ATTESTED', '2026-09-21T09:02')
            """.trimIndent(),
        )
        db.execSQL(
            """
            INSERT INTO gate_decisions (
                id, gateDefinitionId, ruleVersion, evidenceIds, conditionOutcomes, status, evaluatedAtLocal
            ) VALUES ('dec-1', 'gate-1', '1', 'ev-1', 'met', 'NOT_YET_SATISFIED', '2026-09-21T09:03')
            """.trimIndent(),
        )
        db.execSQL(
            """
            INSERT INTO export_records (
                id, exportedAtLocal, destinationReference, logCount, evidenceCount, progressCount,
                recordingCount, status, documentReference
            ) VALUES ('exp-1', '2026-09-21T10:00', 'notes.zip', 1, 1, 7, 1, 'COMPLETED', 'notes.zip')
            """.trimIndent(),
        )
        MIGRATION_2_3.migrate(db)

        val eligibleZero = db.query(
            "SELECT COUNT(*) FROM course_segments WHERE eligibilityStatus = 'ELIGIBLE' AND lessonNumber = 0",
        )
        eligibleZero.moveToFirst()
        assertEquals(0, eligibleZero.getInt(0))
        eligibleZero.close()

        val segment = db.query("SELECT lessonNumber, eligibilityStatus FROM course_segments WHERE id = 'segment-1'")
        segment.moveToFirst()
        assertEquals(0, segment.getInt(0))
        assertEquals("EXCLUDED", segment.getString(1))
        segment.close()

        val plan = db.query(
            "SELECT lessonNumber, audioModelIds, eligibilityStatus FROM plan_definitions WHERE id = 'plan-book'",
        )
        plan.moveToFirst()
        assertNull(plan.getString(0))
        assertEquals("", plan.getString(1))
        assertEquals("EXCLUDED", plan.getString(2))
        plan.close()

        val presented = db.query("SELECT COUNT(*) FROM plan_definitions WHERE eligibilityStatus = 'ELIGIBLE'")
        presented.moveToFirst()
        assertEquals(0, presented.getInt(0))
        presented.close()

        val log = db.query("SELECT id, category FROM activity_logs WHERE id = 'log-1'")
        log.moveToFirst()
        assertEquals("log-1", log.getString(0))
        assertEquals("GRAMMAR", log.getString(1))
        log.close()
        val recording = db.query("SELECT localFileReference FROM recording_records WHERE id = 'rec-1'")
        recording.moveToFirst()
        assertEquals("recordings/rec-1.m4a", recording.getString(0))
        recording.close()
        val evidence = db.query("SELECT enteredValue FROM gate_evidence WHERE id = 'ev-1'")
        evidence.moveToFirst()
        assertEquals("1", evidence.getString(0))
        evidence.close()
        val decision = db.query("SELECT status FROM gate_decisions WHERE id = 'dec-1'")
        decision.moveToFirst()
        assertEquals("NOT_YET_SATISFIED", decision.getString(0))
        decision.close()
        val exported = db.query("SELECT documentReference FROM export_records WHERE id = 'exp-1'")
        exported.moveToFirst()
        assertEquals("notes.zip", exported.getString(0))
        exported.close()
        helper.close()
    }

    @Test
    fun version3DecisionsKeepTheirEvidenceAndGainACanonicalRuleSnapshot() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name("capability2-v3.db")
                .callback(object : SupportSQLiteOpenHelper.Callback(3) {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS `course_segments` (
                                `id` TEXT NOT NULL,
                                `lessonReference` TEXT NOT NULL,
                                `lessonNumber` INTEGER NOT NULL,
                                `sortOrder` INTEGER NOT NULL,
                                `eligibilityStatus` TEXT NOT NULL,
                                PRIMARY KEY(`id`)
                            )
                            """.trimIndent(),
                        )
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS `gate_definitions` (
                                `id` TEXT NOT NULL,
                                `segmentId` TEXT NOT NULL,
                                `ruleVersion` TEXT NOT NULL,
                                `displayedRuleText` TEXT NOT NULL,
                                `eligibilityStatus` TEXT NOT NULL,
                                PRIMARY KEY(`id`),
                                FOREIGN KEY(`segmentId`) REFERENCES `course_segments`(`id`)
                                ON UPDATE NO ACTION ON DELETE RESTRICT
                            )
                            """.trimIndent(),
                        )
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS `gate_conditions` (
                                `id` TEXT NOT NULL,
                                `gateDefinitionId` TEXT NOT NULL,
                                `evidenceType` TEXT NOT NULL,
                                `operator` TEXT NOT NULL,
                                `operand` TEXT,
                                `sortOrder` INTEGER NOT NULL,
                                PRIMARY KEY(`id`),
                                FOREIGN KEY(`gateDefinitionId`) REFERENCES `gate_definitions`(`id`)
                                ON UPDATE NO ACTION ON DELETE RESTRICT
                            )
                            """.trimIndent(),
                        )
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS `gate_tutor_requirements` (
                                `gateDefinitionId` TEXT NOT NULL,
                                `recordedAtLocal` TEXT NOT NULL,
                                PRIMARY KEY(`gateDefinitionId`),
                                FOREIGN KEY(`gateDefinitionId`) REFERENCES `gate_definitions`(`id`)
                                ON UPDATE NO ACTION ON DELETE RESTRICT
                            )
                            """.trimIndent(),
                        )
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS `gate_evidence` (
                                `id` TEXT NOT NULL,
                                `gateDefinitionId` TEXT NOT NULL,
                                `evidenceType` TEXT NOT NULL,
                                `enteredValue` TEXT NOT NULL,
                                `selfAttestationStatus` TEXT NOT NULL,
                                `recordedAtLocal` TEXT NOT NULL,
                                PRIMARY KEY(`id`),
                                FOREIGN KEY(`gateDefinitionId`) REFERENCES `gate_definitions`(`id`)
                                ON UPDATE NO ACTION ON DELETE RESTRICT
                            )
                            """.trimIndent(),
                        )
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS `gate_decisions` (
                                `id` TEXT NOT NULL,
                                `gateDefinitionId` TEXT NOT NULL,
                                `ruleVersion` TEXT NOT NULL,
                                `evidenceIds` TEXT NOT NULL,
                                `conditionOutcomes` TEXT NOT NULL,
                                `status` TEXT NOT NULL,
                                `evaluatedAtLocal` TEXT NOT NULL,
                                PRIMARY KEY(`id`),
                                FOREIGN KEY(`gateDefinitionId`) REFERENCES `gate_definitions`(`id`)
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
        db.execSQL("PRAGMA foreign_keys = ON")
        db.execSQL(
            """
            INSERT INTO course_segments (id, lessonReference, lessonNumber, sortOrder, eligibilityStatus)
            VALUES ('segment-1', 'Lesson 1', 1, 1, 'ELIGIBLE')
            """.trimIndent(),
        )
        db.execSQL(
            """
            INSERT INTO gate_definitions (id, segmentId, ruleVersion, displayedRuleText, eligibilityStatus)
            VALUES ('gate-1', 'segment-1', '1', 'Displayed test rule', 'ELIGIBLE')
            """.trimIndent(),
        )
        db.execSQL(
            """
            INSERT INTO gate_conditions (id, gateDefinitionId, evidenceType, operator, operand, sortOrder)
            VALUES
                ('c-book', 'gate-1', 'BOOK_REVISION_SCORE', 'AT_LEAST', '1', 0),
                ('c-anki', 'gate-1', 'ANKI_DROID_STATUS', 'EQUALS', 'MATURE', 1),
                ('c-work', 'gate-1', 'PRESCRIBED_WORK_COMPLETION', 'PRESENT', NULL, 2)
            """.trimIndent(),
        )
        db.execSQL(
            """
            INSERT INTO gate_evidence (
                id, gateDefinitionId, evidenceType, enteredValue, selfAttestationStatus, recordedAtLocal
            ) VALUES
                ('ev-book', 'gate-1', 'BOOK_REVISION_SCORE', '1', 'SELF_ATTESTED', '2026-09-21T09:00'),
                ('ev-anki', 'gate-1', 'ANKI_DROID_STATUS', 'MATURE', 'SELF_ATTESTED', '2026-09-21T09:00'),
                ('ev-work', 'gate-1', 'PRESCRIBED_WORK_COMPLETION', 'recorded', 'USER_RECORDED', '2026-09-21T09:00')
            """.trimIndent(),
        )
        val legacyOutcomes = listOf(
            "c-book\tev-book\tSATISFIED",
            "c-anki\tev-anki\tSATISFIED",
            "c-work\tev-work\tSATISFIED",
        ).joinToString(separator = "\n")
        db.execSQL(
            """
            INSERT INTO gate_decisions (
                id, gateDefinitionId, ruleVersion, evidenceIds, conditionOutcomes, status, evaluatedAtLocal
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            arrayOf(
                "dec-1",
                "gate-1",
                "1",
                "ev-book,ev-anki,ev-work",
                legacyOutcomes,
                "SATISFIED",
                "2026-09-21T09:03",
            ),
        )
        MIGRATION_3_4.migrate(db)

        val evidence = db.query("SELECT enteredValue FROM gate_evidence WHERE id = 'ev-book'")
        evidence.moveToFirst()
        assertEquals("1", evidence.getString(0))
        evidence.close()

        val gate = db.query("SELECT displayedRuleText, ruleVersion FROM gate_definitions WHERE id = 'gate-1'")
        gate.moveToFirst()
        assertEquals(displayedRuleText(defaultAdvancementRule()), gate.getString(0))
        assertEquals("1", gate.getString(1))
        gate.close()

        val decision = db.query(
            "SELECT ruleSnapshot, evidenceIds, conditionOutcomes, status FROM gate_decisions WHERE id = 'dec-1'",
        )
        decision.moveToFirst()
        val snapshot = decision.getString(0)
        val evidenceIds = parseEvidenceIds(decision.getString(1))
        val outcomes = decodeConditionOutcomes(decision.getString(2))
        assertEquals("SATISFIED", decision.getString(3))
        decision.close()
        assertEquals(listOf("ev-book", "ev-anki", "ev-work"), evidenceIds)
        assertEquals(3, outcomes.size)
        assertEquals(displayedRuleText(defaultAdvancementRule()), parseGateRuleSnapshot(snapshot)?.displayedRuleText)

        val recorded = listOf(
            RecordedEvidence("ev-book", GateEvidenceKind.BOOK_REVISION_SCORE, "1", "2026-09-21T09:00"),
            RecordedEvidence("ev-anki", GateEvidenceKind.ANKI_DROID_STATUS, "MATURE", "2026-09-21T09:00"),
            RecordedEvidence("ev-work", GateEvidenceKind.PRESCRIBED_WORK_COMPLETION, "recorded", "2026-09-21T09:00"),
            RecordedEvidence("ev-later", GateEvidenceKind.BOOK_REVISION_SCORE, "0", "2026-09-21T10:00"),
        )
        val reproduced = reproduceDecision(snapshot, evidenceIds, recorded)
        assertTrue(reproducedDecisionMatches(GateStatus.SATISFIED, outcomes, reproduced))
        helper.close()
    }

    @Test
    fun version3DecisionsBeforeAndAfterTutorDoNotInventAHistoricalRule() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name("capability2-v3-tutor.db")
                .callback(object : SupportSQLiteOpenHelper.Callback(3) {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        createVersion3GateTables(db)
                    }

                    override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
                })
                .build(),
        )
        val db = helper.writableDatabase
        db.execSQL("PRAGMA foreign_keys = ON")
        db.execSQL(
            """
            INSERT INTO course_segments (id, lessonReference, lessonNumber, sortOrder, eligibilityStatus)
            VALUES ('segment-1', 'Lesson 1', 1, 1, 'ELIGIBLE')
            """.trimIndent(),
        )
        db.execSQL(
            """
            INSERT INTO gate_definitions (id, segmentId, ruleVersion, displayedRuleText, eligibilityStatus)
            VALUES ('gate-1', 'segment-1', '1', 'Displayed test rule', 'ELIGIBLE')
            """.trimIndent(),
        )
        db.execSQL(
            """
            INSERT INTO gate_conditions (id, gateDefinitionId, evidenceType, operator, operand, sortOrder)
            VALUES
                ('c-book', 'gate-1', 'BOOK_REVISION_SCORE', 'AT_LEAST', '1', 0),
                ('c-anki', 'gate-1', 'ANKI_DROID_STATUS', 'EQUALS', 'MATURE', 1),
                ('c-work', 'gate-1', 'PRESCRIBED_WORK_COMPLETION', 'PRESENT', NULL, 2)
            """.trimIndent(),
        )
        db.execSQL(
            """
            INSERT INTO gate_tutor_requirements (gateDefinitionId, recordedAtLocal)
            VALUES ('gate-1', '2026-09-21T11:00')
            """.trimIndent(),
        )
        db.execSQL(
            """
            INSERT INTO gate_evidence (
                id, gateDefinitionId, evidenceType, enteredValue, selfAttestationStatus, recordedAtLocal
            ) VALUES
                ('ev-book', 'gate-1', 'BOOK_REVISION_SCORE', '1', 'SELF_ATTESTED', '2026-09-21T09:00'),
                ('ev-anki', 'gate-1', 'ANKI_DROID_STATUS', 'MATURE', 'SELF_ATTESTED', '2026-09-21T09:00'),
                ('ev-work', 'gate-1', 'PRESCRIBED_WORK_COMPLETION', 'recorded', 'USER_RECORDED', '2026-09-21T09:00'),
                ('ev-tutor', 'gate-1', 'TUTOR_SIGN_OFF', 'recorded', 'USER_RECORDED', '2026-09-21T12:00'),
                ('ev-book-low', 'gate-1', 'BOOK_REVISION_SCORE', '0', 'SELF_ATTESTED', '2026-09-21T08:00')
            """.trimIndent(),
        )
        val beforeTutor = listOf(
            "c-book\tev-book\tSATISFIED",
            "c-anki\tev-anki\tSATISFIED",
            "c-work\tev-work\tSATISFIED",
        ).joinToString(separator = "\n")
        val afterTutor = listOf(
            "c-book\tev-book\tSATISFIED",
            "c-anki\tev-anki\tSATISFIED",
            "c-work\tev-work\tSATISFIED",
            "${USER_ADDED_TUTOR_CONDITION_ID}\tev-tutor\tSATISFIED",
        ).joinToString(separator = "\n")
        db.execSQL(
            """
            INSERT INTO gate_decisions (
                id, gateDefinitionId, ruleVersion, evidenceIds, conditionOutcomes, status, evaluatedAtLocal
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            arrayOf("dec-a", "gate-1", "1", "ev-book,ev-anki,ev-work", beforeTutor, "SATISFIED", "2026-09-21T09:03"),
        )
        db.execSQL(
            """
            INSERT INTO gate_decisions (
                id, gateDefinitionId, ruleVersion, evidenceIds, conditionOutcomes, status, evaluatedAtLocal
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            arrayOf(
                "dec-b",
                "gate-1",
                "1",
                "ev-book,ev-anki,ev-work,ev-tutor",
                afterTutor,
                "SATISFIED",
                "2026-09-21T12:01",
            ),
        )
        db.execSQL(
            """
            INSERT INTO gate_decisions (
                id, gateDefinitionId, ruleVersion, evidenceIds, conditionOutcomes, status, evaluatedAtLocal
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            arrayOf(
                "dec-c",
                "gate-1",
                "1",
                "ev-book-low,ev-anki,ev-work",
                beforeTutor,
                "SATISFIED",
                "2026-09-21T08:30",
            ),
        )
        MIGRATION_3_4.migrate(db)

        val decisionA = queryDecision(db, "dec-a")
        val decisionB = queryDecision(db, "dec-b")
        val decisionC = queryDecision(db, "dec-c")
        val snapshotA = parseGateRuleSnapshot(decisionA.snapshot)
        val snapshotB = parseGateRuleSnapshot(decisionB.snapshot)
        assertEquals("SATISFIED", decisionA.status)
        assertEquals("SATISFIED", decisionB.status)
        assertEquals("SATISFIED", decisionC.status)
        assertEquals(UNRECOVERABLE_RULE_SNAPSHOT, decisionC.snapshot)
        assertEquals(null, parseGateRuleSnapshot(decisionC.snapshot))
        assertEquals(
            displayedRuleText(defaultAdvancementRule()),
            snapshotA?.displayedRuleText,
        )
        assertEquals(
            displayedRuleText(defaultAdvancementRule() + tutorSignOffCondition()),
            snapshotB?.displayedRuleText,
        )
        assertTrue(snapshotA!!.conditions.none { it.kind == GateEvidenceKind.TUTOR_SIGN_OFF.name })
        assertTrue(snapshotB!!.conditions.any { it.kind == GateEvidenceKind.TUTOR_SIGN_OFF.name })

        val recorded = listOf(
            RecordedEvidence("ev-book", GateEvidenceKind.BOOK_REVISION_SCORE, "1", "2026-09-21T09:00"),
            RecordedEvidence("ev-anki", GateEvidenceKind.ANKI_DROID_STATUS, "MATURE", "2026-09-21T09:00"),
            RecordedEvidence("ev-work", GateEvidenceKind.PRESCRIBED_WORK_COMPLETION, "recorded", "2026-09-21T09:00"),
            RecordedEvidence("ev-tutor", GateEvidenceKind.TUTOR_SIGN_OFF, "recorded", "2026-09-21T12:00"),
        )
        val reproducedA = reproduceDecision(decisionA.snapshot, decisionA.evidenceIds, recorded)
        val reproducedB = reproduceDecision(decisionB.snapshot, decisionB.evidenceIds, recorded)
        assertTrue(reproducedDecisionMatches(GateStatus.SATISFIED, decisionA.outcomes, reproducedA))
        assertTrue(reproducedDecisionMatches(GateStatus.SATISFIED, decisionB.outcomes, reproducedB))
        assertEquals(3, reproducedA.outcomes.size)
        assertEquals(4, reproducedB.outcomes.size)
        helper.close()
    }

    private fun createVersion3GateTables(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `course_segments` (
                `id` TEXT NOT NULL,
                `lessonReference` TEXT NOT NULL,
                `lessonNumber` INTEGER NOT NULL,
                `sortOrder` INTEGER NOT NULL,
                `eligibilityStatus` TEXT NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent(),
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `gate_definitions` (
                `id` TEXT NOT NULL,
                `segmentId` TEXT NOT NULL,
                `ruleVersion` TEXT NOT NULL,
                `displayedRuleText` TEXT NOT NULL,
                `eligibilityStatus` TEXT NOT NULL,
                PRIMARY KEY(`id`),
                FOREIGN KEY(`segmentId`) REFERENCES `course_segments`(`id`)
                ON UPDATE NO ACTION ON DELETE RESTRICT
            )
            """.trimIndent(),
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `gate_conditions` (
                `id` TEXT NOT NULL,
                `gateDefinitionId` TEXT NOT NULL,
                `evidenceType` TEXT NOT NULL,
                `operator` TEXT NOT NULL,
                `operand` TEXT,
                `sortOrder` INTEGER NOT NULL,
                PRIMARY KEY(`id`),
                FOREIGN KEY(`gateDefinitionId`) REFERENCES `gate_definitions`(`id`)
                ON UPDATE NO ACTION ON DELETE RESTRICT
            )
            """.trimIndent(),
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `gate_tutor_requirements` (
                `gateDefinitionId` TEXT NOT NULL,
                `recordedAtLocal` TEXT NOT NULL,
                PRIMARY KEY(`gateDefinitionId`),
                FOREIGN KEY(`gateDefinitionId`) REFERENCES `gate_definitions`(`id`)
                ON UPDATE NO ACTION ON DELETE RESTRICT
            )
            """.trimIndent(),
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `gate_evidence` (
                `id` TEXT NOT NULL,
                `gateDefinitionId` TEXT NOT NULL,
                `evidenceType` TEXT NOT NULL,
                `enteredValue` TEXT NOT NULL,
                `selfAttestationStatus` TEXT NOT NULL,
                `recordedAtLocal` TEXT NOT NULL,
                PRIMARY KEY(`id`),
                FOREIGN KEY(`gateDefinitionId`) REFERENCES `gate_definitions`(`id`)
                ON UPDATE NO ACTION ON DELETE RESTRICT
            )
            """.trimIndent(),
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `gate_decisions` (
                `id` TEXT NOT NULL,
                `gateDefinitionId` TEXT NOT NULL,
                `ruleVersion` TEXT NOT NULL,
                `evidenceIds` TEXT NOT NULL,
                `conditionOutcomes` TEXT NOT NULL,
                `status` TEXT NOT NULL,
                `evaluatedAtLocal` TEXT NOT NULL,
                PRIMARY KEY(`id`),
                FOREIGN KEY(`gateDefinitionId`) REFERENCES `gate_definitions`(`id`)
                ON UPDATE NO ACTION ON DELETE RESTRICT
            )
            """.trimIndent(),
        )
    }

    @Test
    fun version4SeedStateGainsAContentHashColumn() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name("audit-v4.db")
                .callback(object : SupportSQLiteOpenHelper.Callback(4) {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS `bundled_seed_state` (
                                `id` INTEGER NOT NULL,
                                `contentVersion` INTEGER NOT NULL,
                                `appliedAtLocal` TEXT NOT NULL,
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
        db.execSQL(
            "INSERT INTO bundled_seed_state (id, contentVersion, appliedAtLocal) VALUES (1, 1, '2026-09-21T09:00')",
        )
        MIGRATION_4_5.migrate(db)
        val cursor = db.query("SELECT contentHash FROM bundled_seed_state WHERE id = 1")
        cursor.moveToFirst()
        assertEquals("", cursor.getString(0))
        cursor.close()
        helper.close()
    }

    @Test
    fun version5ExportRowsGainANullableRecordingSetDigest() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name("v1-1-v5.db")
                .callback(object : SupportSQLiteOpenHelper.Callback(5) {
                    override fun onCreate(db: SupportSQLiteDatabase) {
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
                                `documentReference` TEXT,
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
        db.execSQL(
            """
            INSERT INTO export_records (
                id, exportedAtLocal, destinationReference, logCount, evidenceCount, progressCount,
                recordingCount, status, documentReference
            ) VALUES ('exp-1', '2026-09-21T10:00', 'notes.zip', 1, 0, 7, 1, 'COMPLETED', NULL)
            """.trimIndent(),
        )
        MIGRATION_5_6.migrate(db)
        val cursor = db.query("SELECT recordingCount, recordingSetDigest FROM export_records WHERE id = 'exp-1'")
        cursor.moveToFirst()
        assertEquals(1, cursor.getInt(0))
        assertNull(cursor.getString(1))
        cursor.close()
        helper.close()
    }

    private data class MigratedDecision(
        val snapshot: String,
        val evidenceIds: List<String>,
        val outcomes: List<ConditionOutcome>,
        val status: String,
    )

    private fun queryDecision(db: SupportSQLiteDatabase, id: String): MigratedDecision {
        val cursor = db.query(
            "SELECT ruleSnapshot, evidenceIds, conditionOutcomes, status FROM gate_decisions WHERE id = ?",
            arrayOf(id),
        )
        cursor.moveToFirst()
        val row = MigratedDecision(
            snapshot = cursor.getString(0),
            evidenceIds = parseEvidenceIds(cursor.getString(1)),
            outcomes = decodeConditionOutcomes(cursor.getString(2)),
            status = cursor.getString(3),
        )
        cursor.close()
        return row
    }
}
