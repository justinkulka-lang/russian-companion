package dev.russiancompanion.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.russiancompanion.domain.gate.ConditionOutcome
import dev.russiancompanion.domain.gate.GateComparison
import dev.russiancompanion.domain.gate.GateEvidenceKind
import dev.russiancompanion.domain.gate.GateRuleCondition
import dev.russiancompanion.domain.gate.GateStatus
import dev.russiancompanion.domain.gate.RecordedEvidence
import dev.russiancompanion.domain.gate.UNRECOVERABLE_RULE_SNAPSHOT
import dev.russiancompanion.domain.gate.decodeLegacyEvidenceIds
import dev.russiancompanion.domain.gate.displayedRuleText
import dev.russiancompanion.domain.gate.encodeConditionOutcomes
import dev.russiancompanion.domain.gate.encodeEvidenceIds
import dev.russiancompanion.domain.gate.encodeGateRuleSnapshot
import dev.russiancompanion.domain.gate.gateRuleSnapshot
import dev.russiancompanion.domain.gate.parseConditionOutcomes
import dev.russiancompanion.domain.gate.parseLegacyConditionOutcomes
import dev.russiancompanion.domain.gate.parseStoredGateStatus
import dev.russiancompanion.domain.gate.reproduceDecision
import dev.russiancompanion.domain.gate.reproducedDecisionMatches
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
 * Converts legacy comma/TSV encodings to JSON. A rule snapshot is stored
 * only when a candidate built from the preserved packaged conditions, with
 * or without the current tutor requirement, recomputes the stored status
 * and outcomes from that decision's named evidence. Otherwise the snapshot
 * is marked unrecoverable so migration cannot invent a historical rule.
 */
internal val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `gate_decisions` ADD COLUMN `ruleSnapshot` TEXT NOT NULL DEFAULT ''")
        rewriteDisplayedRules(db)
        rewriteDecisions(db)
    }
}

private data class LegacyEvidenceRow(
    val id: String,
    val gateId: String,
    val recorded: RecordedEvidence,
)

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
        val status: String,
    )
    val evidenceById = loadEvidence(db)
    val decisions = mutableListOf<LegacyDecision>()
    db.query(
        "SELECT `id`, `gateDefinitionId`, `ruleVersion`, `evidenceIds`, `conditionOutcomes`, `status` FROM `gate_decisions`",
    ).use { cursor ->
        while (cursor.moveToNext()) {
            decisions += LegacyDecision(
                id = cursor.getString(0),
                gateId = cursor.getString(1),
                version = cursor.getString(2),
                evidenceIds = cursor.getString(3),
                outcomes = cursor.getString(4),
                status = cursor.getString(5),
            )
        }
    }
    for (decision in decisions) {
        val ids = decodeLegacyEvidenceIds(decision.evidenceIds)
        val outcomes = parseConditionOutcomes(decision.outcomes)
            ?: parseLegacyConditionOutcomes(decision.outcomes)
        val status = parseStoredGateStatus(decision.status)
        val resolved = resolveNamedEvidence(ids, decision.gateId, evidenceById)
        val snapshot = recoverableSnapshot(
            version = decision.version,
            candidates = snapshotCandidates(db, decision.gateId),
            evidenceIds = ids,
            evidence = resolved.orEmpty(),
            status = status,
            outcomes = outcomes,
            evidenceResolved = resolved != null,
        )
        db.execSQL(
            """
            UPDATE `gate_decisions`
            SET `ruleSnapshot` = ?, `evidenceIds` = ?, `conditionOutcomes` = ?
            WHERE `id` = ?
            """.trimIndent(),
            arrayOf(
                snapshot,
                encodeEvidenceIds(ids),
                outcomes?.let(::encodeConditionOutcomes) ?: decision.outcomes,
                decision.id,
            ),
        )
    }
}

private fun loadEvidence(db: SupportSQLiteDatabase): Map<String, LegacyEvidenceRow> {
    val evidenceById = mutableMapOf<String, LegacyEvidenceRow>()
    db.query(
        "SELECT `id`, `gateDefinitionId`, `evidenceType`, `enteredValue`, `recordedAtLocal` FROM `gate_evidence`",
    ).use { cursor ->
        while (cursor.moveToNext()) {
            val kind = GateEvidenceKind.entries.firstOrNull { it.name == cursor.getString(2) } ?: continue
            val id = cursor.getString(0)
            evidenceById[id] = LegacyEvidenceRow(
                id = id,
                gateId = cursor.getString(1),
                recorded = RecordedEvidence(
                    id = id,
                    kind = kind,
                    enteredValue = cursor.getString(3),
                    recordedAtLocal = cursor.getString(4),
                ),
            )
        }
    }
    return evidenceById
}

private fun resolveNamedEvidence(
    ids: List<String>,
    gateId: String,
    evidenceById: Map<String, LegacyEvidenceRow>,
): List<RecordedEvidence>? {
    if (ids.size != ids.toSet().size) return null
    val resolved = ArrayList<RecordedEvidence>(ids.size)
    for (id in ids) {
        val row = evidenceById[id] ?: return null
        if (row.gateId != gateId) return null
        resolved += row.recorded
    }
    return resolved
}

private fun snapshotCandidates(
    db: SupportSQLiteDatabase,
    gateId: String,
): List<List<GateRuleCondition>> {
    val shipped = loadRuleConditions(db, gateId, includeTutor = false)
    val withTutor = loadRuleConditions(db, gateId, includeTutor = true)
    return listOf(shipped, withTutor).distinct()
}

private fun recoverableSnapshot(
    version: String,
    candidates: List<List<GateRuleCondition>>,
    evidenceIds: List<String>,
    evidence: List<RecordedEvidence>,
    status: GateStatus?,
    outcomes: List<ConditionOutcome>?,
    evidenceResolved: Boolean,
): String {
    if (!evidenceResolved || status == null || outcomes == null) {
        return UNRECOVERABLE_RULE_SNAPSHOT
    }
    for (conditions in candidates) {
        if (conditions.isEmpty()) continue
        val snapshot = encodeGateRuleSnapshot(gateRuleSnapshot(version, conditions))
        val reproduced = reproduceDecision(snapshot, evidenceIds, evidence)
        if (reproducedDecisionMatches(status, outcomes, reproduced)) {
            return snapshot
        }
    }
    return UNRECOVERABLE_RULE_SNAPSHOT
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
