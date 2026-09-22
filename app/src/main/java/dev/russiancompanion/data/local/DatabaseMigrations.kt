package dev.russiancompanion.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.russiancompanion.domain.gate.GateComparison
import dev.russiancompanion.domain.gate.GateEvidenceKind
import dev.russiancompanion.domain.gate.GateRuleCondition
import dev.russiancompanion.domain.gate.decodeConditionOutcomes
import dev.russiancompanion.domain.gate.decodeLegacyConditionOutcomes
import dev.russiancompanion.domain.gate.decodeLegacyEvidenceIds
import dev.russiancompanion.domain.gate.displayedRuleText
import dev.russiancompanion.domain.gate.encodeConditionOutcomes
import dev.russiancompanion.domain.gate.encodeEvidenceIds
import dev.russiancompanion.domain.gate.encodeGateRuleSnapshot
import dev.russiancompanion.domain.gate.gateRuleSnapshot
import dev.russiancompanion.domain.gate.tutorSignOffCondition

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
 * User logs, recordings, evidence, decisions, and exports are kept. Legacy
 * segments receive lessonNumber 0, which is outside the 1–30 arc, so those
 * segments and their plan definitions become ineligible until a valid packaged
 * definition replaces them.
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
        db.execSQL(
            """
            UPDATE `course_segments`
            SET `eligibilityStatus` = 'EXCLUDED'
            WHERE `lessonNumber` < 1 OR `lessonNumber` > 30
            """.trimIndent(),
        )
        db.execSQL(
            """
            UPDATE `plan_definitions`
            SET `eligibilityStatus` = 'EXCLUDED'
            WHERE `segmentId` IN (
                SELECT `id` FROM `course_segments`
                WHERE `lessonNumber` < 1 OR `lessonNumber` > 30
            )
            """.trimIndent(),
        )
    }
}

/**
 * Stores a canonical JSON rule snapshot on every decision and converts
 * legacy comma/TSV evidence and outcome encodings to JSON. Existing
 * evidence and decisions are kept. Displayed rule text is rewritten from
 * the conditions that were present at migration time.
 */
internal val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `gate_decisions` ADD COLUMN `ruleSnapshot` TEXT NOT NULL DEFAULT ''")
        rewriteDisplayedRules(db)
        rewriteDecisions(db)
    }
}

private fun rewriteDisplayedRules(db: SupportSQLiteDatabase) {
    val ids = mutableListOf<String>()
    db.query("SELECT `id` FROM `gate_definitions`").use { cursor ->
        while (cursor.moveToNext()) ids += cursor.getString(0)
    }
    for (id in ids) {
        val conditions = loadRuleConditions(db, id, includeTutor = false)
        if (conditions.isEmpty()) continue
        db.execSQL(
            "UPDATE `gate_definitions` SET `displayedRuleText` = ? WHERE `id` = ?",
            arrayOf(displayedRuleText(conditions), id),
        )
    }
}

private fun rewriteDecisions(db: SupportSQLiteDatabase) {
    data class LegacyDecision(
        val id: String,
        val gateId: String,
        val version: String,
        val evidenceIds: String,
        val outcomes: String,
    )
    val decisions = mutableListOf<LegacyDecision>()
    db.query(
        "SELECT `id`, `gateDefinitionId`, `ruleVersion`, `evidenceIds`, `conditionOutcomes` FROM `gate_decisions`",
    ).use { cursor ->
        while (cursor.moveToNext()) {
            decisions += LegacyDecision(
                id = cursor.getString(0),
                gateId = cursor.getString(1),
                version = cursor.getString(2),
                evidenceIds = cursor.getString(3),
                outcomes = cursor.getString(4),
            )
        }
    }
    for (decision in decisions) {
        val conditions = loadRuleConditions(db, decision.gateId, includeTutor = true)
        val snapshot = encodeGateRuleSnapshot(gateRuleSnapshot(decision.version, conditions))
        val outcomes = if (decision.outcomes.trimStart().startsWith("[")) {
            decodeConditionOutcomes(decision.outcomes)
        } else {
            decodeLegacyConditionOutcomes(decision.outcomes)
        }
        db.execSQL(
            """
            UPDATE `gate_decisions`
            SET `ruleSnapshot` = ?, `evidenceIds` = ?, `conditionOutcomes` = ?
            WHERE `id` = ?
            """.trimIndent(),
            arrayOf(
                snapshot,
                encodeEvidenceIds(decodeLegacyEvidenceIds(decision.evidenceIds)),
                encodeConditionOutcomes(outcomes),
                decision.id,
            ),
        )
    }
}

private fun loadRuleConditions(
    db: SupportSQLiteDatabase,
    gateId: String,
    includeTutor: Boolean,
): List<GateRuleCondition> {
    val conditions = mutableListOf<GateRuleCondition>()
    db.query(
        """
        SELECT `id`, `evidenceType`, `operator`, `operand`
        FROM `gate_conditions`
        WHERE `gateDefinitionId` = ?
        ORDER BY `sortOrder`, `id`
        """.trimIndent(),
        arrayOf(gateId),
    ).use { cursor ->
        while (cursor.moveToNext()) {
            val kind = GateEvidenceKind.entries.firstOrNull { it.name == cursor.getString(1) } ?: continue
            val comparison = GateComparison.entries.firstOrNull { it.name == cursor.getString(2) } ?: continue
            conditions += GateRuleCondition(
                id = cursor.getString(0),
                kind = kind,
                comparison = comparison,
                operand = if (cursor.isNull(3)) null else cursor.getString(3),
            )
        }
    }
    if (includeTutor) {
        db.query(
            "SELECT 1 FROM `gate_tutor_requirements` WHERE `gateDefinitionId` = ?",
            arrayOf(gateId),
        ).use { cursor ->
            if (cursor.moveToFirst()) conditions += tutorSignOffCondition()
        }
    }
    return conditions
}
