package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.backup.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.io.ByteArrayOutputStream
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import org.json.JSONObject

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class CumulativeReviewStorageTest {
    @Test fun placementAndBareLegacyCorrectIdsCannotCreateAssignments() {
        val p = pack()
        LocalStore.openInMemory(context()).use { s ->
            s.v2SaveProfile(s.v2Profile().copy(currentUnitNumber = 3, startingPoint = "RESUME_FROM_BOOK"))
            s.v2CompleteUnit(1, "old"); s.v2CompleteUnit(2, "old")
            p.unit(1)!!.objectives.flatMap { it.checkItemIds }.forEach { s.v2RecordAttempt(it, p.item(it)!!.answer, true, "old") }
            assertNull(s.openUnit3CumulativeReview(p, s.learnerMutationEpoch()))
            assertTrue(s.cumulativeReviewHistory().first.isEmpty())
        }
    }

    @Test fun immutableSlotsSpanTwoTaughtObjectivesAndRepeatedOpenDoesNotResample() {
        val p = pack()
        prepared(p).use { s ->
            val a = open(p, s)
            assertEquals(listOf("obj-u1-decoding", "obj-u1-confusable"), a.slots.map { it.objectiveId })
            assertEquals(2, a.slots.map { it.skill }.distinct().size)
            assertTrue(reviewAssignmentCurrent(p, a))
            answer(p, s, a, 0, false)
            assertEquals(a, open(p, s)); assertEquals(1, s.cumulativeReviewHistory().first.size)
        }
    }

    @Test fun prescribedAndOptionalAnswersNeverSupplyReviewOrGainExtraPrescribedCredit() {
        val p = pack()
        prepared(p).use { s ->
            val a = open(p, s); val before = s.v2Attempts(); val credit = creditItemIds(p, s.v2LearnerView())
            val item = p.item(a.slots[0].itemId)!!
            s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, item.id, item.answer, OPTIONAL_PRACTICE, practiceStepId("mixed"), "later")!!)
            assertTrue(completedReviewSlots(p, a, s.cumulativeReviewHistory().second).isEmpty())
            answer(p, s, a, 0, true); answer(p, s, a, 1, true)
            assertEquals(setOf(0, 1), completedReviewSlots(p, a, s.cumulativeReviewHistory().second))
            assertEquals(before.size + 1, s.v2Attempts().size); assertEquals(credit, creditItemIds(p, s.v2LearnerView()))
        }
    }

    @Test fun latestAcceptedAnswerWinsAndDuplicateTapsDoNotAllocateExtraOrder() {
        val p = pack()
        prepared(p).use { s ->
            val a = open(p, s)
            answer(p, s, a, 0, true); answer(p, s, a, 0, true)
            assertEquals(1, s.cumulativeReviewHistory().second.size)
            answer(p, s, a, 1, true); answer(p, s, a, 0, false)
            assertEquals(setOf(1), completedReviewSlots(p, a, s.cumulativeReviewHistory().second))
            answer(p, s, a, 0, true)
            assertEquals(setOf(0, 1), completedReviewSlots(p, a, s.cumulativeReviewHistory().second))
        }
    }

    @Test fun staleEpochWrongAssignmentInvalidSlotAndOutOfOptionsAreRejectedWithoutWrites() {
        val p = pack()
        prepared(p).use { s ->
            val a = open(p, s); val epoch = s.learnerMutationEpoch()
            assertFalse(s.answerCumulativeReview(p, "another", 0, "x", epoch))
            assertFalse(s.answerCumulativeReview(p, a.id, 8, "x", epoch))
            assertFalse(s.answerCumulativeReview(p, a.id, 0, "out-of-options", epoch))
            s.openLearnerReplacement(); s.closeLearnerReplacement()
            assertFalse(s.answerCumulativeReview(p, a.id, 0, p.item(a.slots[0].itemId)!!.answer, epoch))
            assertNull(s.openUnit3CumulativeReview(p, epoch))
            assertTrue(s.cumulativeReviewHistory().second.isEmpty())
        }
    }

    @Test fun changedKeyTeachingAndOwnerAvailabilityRejectCreditButUnrelatedUnitsDoNot() {
        val p = pack()
        prepared(p).use { s ->
            val a = open(p, s); answer(p, s, a, 0, true); answer(p, s, a, 1, true)
            val i = p.item(a.slots[0].itemId)!!
            val changed = p.copy(items = p.items.map { if (it.id == i.id) it.copy(answer = i.options.first { x -> x != i.answer }) else it })
            val reversedRule = a.copy(responseRuleDigest = "0".repeat(64)).let { it.copy(digest = reviewAssignmentDigest(it)) }
            assertFalse(reviewAssignmentCurrent(p, reversedRule))
            assertFalse(reviewAssignmentCurrent(p.copy(schemaVersion = 99), a))
            assertFalse(reviewAssignmentCurrent(changed, a)); assertTrue(completedReviewSlots(changed, a, s.cumulativeReviewHistory().second).isEmpty())
            assertFalse(s.answerCumulativeReview(changed, a.id, 0, i.answer, s.learnerMutationEpoch()))
            val unavailable = p.copy(units = p.units.map { if (it.number == 1) it.copy(installState = "NOT_YET_INSTALLED") else it })
            assertFalse(reviewAssignmentCurrent(unavailable, a))
            val unrelated = p.copy(units = p.units.map { if (it.number == 4) it.copy(topicSummary = "unrelated change") else it })
            assertTrue(reviewAssignmentCurrent(unrelated, a))
            val u = p.unit(1)!!; val first = u.objectives.first { it.id == a.slots[0].objectiveId }.instructionStepIds.first()
            val teaching = p.copy(units = p.units.map { if (it.number == 1) it.copy(sessions = it.sessions.map { x -> x.copy(steps = x.steps.map { if (it.id == first) it.copy(body = it.body + " changed") else it }) }) else it })
            assertFalse(reviewAssignmentCurrent(teaching, a))
        }
    }

    @Test fun offlineCloseAndReopenRetainsExactAssignmentAnswersAndCurrentCompletion() {
        val p = pack(); val name = "review-reopen-${System.nanoTime()}.db"
        var assignment: CumulativeReviewAssignment? = null
        try {
            LocalStore.openNamed(context(), name).use { s -> finish(p, s); assignment = open(p, s); answer(p, s, assignment!!, 0, true); answer(p, s, assignment!!, 1, true) }
            LocalStore.openNamed(context(), name).use { s ->
                assertEquals(assignment, open(p, s)); assertEquals(setOf(0, 1), completedReviewSlots(p, assignment!!, s.cumulativeReviewHistory().second))
            }
        } finally { context().deleteDatabase(name) }
    }

    @Test fun newBackupRoundtripReplacesReviewHistoryAndRestoreFaultRollsItBack() {
        val p = pack()
        prepared(p).use { source ->
            val a = open(p, source); answer(p, source, a, 0, true)
            val bytes = ByteArrayOutputStream(); writeGovernedBackup(bytes, "dev.russiancompanion", p.packId, p.packVersion, "FOLLOW_SYSTEM", source.governedLearnerDocument(), emptyList())
            val temp = File(context().cacheDir, "review-read-${System.nanoTime()}")
            try {
                val read = readGovernedBackup(bytes.toByteArray().inputStream(), "dev.russiancompanion", p.packId, p.packVersion, temp)
                assertTrue(read is BackupReadResult.Valid)
                val document = (read as BackupReadResult.Valid).backup.document
                assertEquals(3, document.schemaVersion)
                LocalStore.openInMemory(context()).use { target ->
                    assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner(document, emptyList()))
                    assertEquals(source.cumulativeReviewHistory(), target.cumulativeReviewHistory())
                    val fingerprint = target.governedLearnerFingerprint()
                    V2BackupFaults.failInsideReplacement = true
                    try { target.replaceGovernedLearner(document.copy(reviewAssignments = emptyList(), reviewAnswers = emptyList()), emptyList()) }
                    finally { V2BackupFaults.failInsideReplacement = false }
                    assertEquals(fingerprint, target.governedLearnerFingerprint())
                }
            } finally { temp.deleteRecursively() }
        }
    }

    @Test fun schemaOneBackupRestoresNoSyntheticReviewAndClearsReplacedLearnerHistory() {
        val p = pack()
        prepared(p).use { s ->
            val a = open(p, s); answer(p, s, a, 0, true)
            val old = s.governedLearnerDocument().copy(schemaVersion = 1, reviewAssignments = emptyList(), reviewAnswers = emptyList(),
                unitCompletions = s.governedLearnerDocument().unitCompletions.map { it.copy(preReviewActivation = true) })
            val bytes = ByteArrayOutputStream(); writeGovernedBackup(bytes, "dev.russiancompanion", p.packId, p.packVersion, "FOLLOW_SYSTEM", old, emptyList(), formatVersion = 1)
            val temp = File(context().cacheDir, "review-old-${System.nanoTime()}")
            try {
                val read = readGovernedBackup(bytes.toByteArray().inputStream(), "dev.russiancompanion", p.packId, p.packVersion, temp)
                assertTrue(read is BackupReadResult.Valid)
                assertEquals(GovernedReplaceOutcome.REPLACED, s.replaceGovernedLearner((read as BackupReadResult.Valid).backup.document, emptyList()))
                assertTrue(s.cumulativeReviewHistory().first.isEmpty()); assertTrue(s.cumulativeReviewHistory().second.isEmpty())
                assertEquals(setOf(1, 2), currentCompletedUnitNumbers(p, s.v2LearnerView()))
            } finally { temp.deleteRecursively() }
        }
    }

    @Test fun forgedLinksOrdersRolesAndAssignmentHashCannotReplaceAnyLearnerRows() {
        val p = pack()
        prepared(p).use { s ->
            val a = open(p, s); answer(p, s, a, 0, true)
            val doc = s.governedLearnerDocument(); val b = doc.reviewAnswers.single(); val fingerprint = s.governedLearnerFingerprint()
            val bad = listOf(doc.copy(reviewAnswers = listOf(b.copy(assignmentId = "missing"))),
                doc.copy(reviewAnswers = listOf(b.copy(acceptedOrder = a.boundary))),
                doc.copy(reviewAnswers = listOf(b.copy(role = PRESCRIBED_CHECK))),
                doc.copy(reviewAssignments = listOf(a.copy(digest = "forged"))),
                doc.copy(reviewAnswers = listOf(b, b.copy(id = "duplicate-order"))),
                doc.copy(schemaVersion = 1))
            for (d in bad) { assertEquals(GovernedReplaceOutcome.UNCHANGED, s.replaceGovernedLearner(d, emptyList())); assertEquals(fingerprint, s.governedLearnerFingerprint()) }
        }
    }

    @Test fun realVersionNineDatabaseMigratesWithoutRewritingHistoricalRowsOrInventingCredit() {
        val name = "review-migrate-${System.nanoTime()}.db"
        val schema = JSONObject(File(root(), "app/schemas/dev.russiancompanion.data.local.AppDatabase/9.json").readText()).getJSONObject("database")
        val helper = FrameworkSQLiteOpenHelperFactory().create(SupportSQLiteOpenHelper.Configuration.builder(context()).name(name)
            .callback(object : SupportSQLiteOpenHelper.Callback(9) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    val entities = schema.getJSONArray("entities")
                    for (n in 0 until entities.length()) { val e = entities.getJSONObject(n); val table = e.getString("tableName")
                        db.execSQL(e.getString("createSql").replace("\${TABLE_NAME}", table))
                        val indices = e.optJSONArray("indices") ?: continue
                        for (j in 0 until indices.length()) db.execSQL(indices.getJSONObject(j).getString("createSql").replace("\${TABLE_NAME}", table))
                    }
                    val setup = schema.getJSONArray("setupQueries"); for (n in 0 until setup.length()) db.execSQL(setup.getString(n))
                    db.execSQL("INSERT INTO v2_attempts (id,itemId,selected,correct,recordedAtLocal,acceptedOrder) VALUES ('historical','old-item','old-answer',1,'old-time',99)")
                    db.execSQL("INSERT INTO v2_unit_completions VALUES (3,'old-completion')")
                }
                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
            }).build())
        try {
            helper.writableDatabase; helper.close()
            LocalStore.openNamed(context(), name).use { s ->
                val old = s.v2Attempts().single(); assertEquals("historical", old.id); assertEquals(99, old.acceptedOrder)
                assertEquals("old-time", old.recordedAtLocal); assertEquals("old-answer", old.selected)
                assertEquals(setOf(3), s.v2LearnerView().completedUnitNumbers)
                assertTrue(s.cumulativeReviewHistory().first.isEmpty()); assertTrue(s.cumulativeReviewHistory().second.isEmpty())
            }
        } finally { helper.close(); context().deleteDatabase(name) }
    }

    @Test fun legacyDocumentWithoutNewFieldsDecodesWithNoSyntheticReviewRows() {
        LocalStore.openInMemory(context()).use { s ->
            val json = JSONObject(backupJson.encodeToString(GovernedLearnerDocument.serializer(), s.governedLearnerDocument().copy(schemaVersion = 1)))
            json.remove("reviewAssignments"); json.remove("reviewAnswers")
            val old = backupJson.decodeFromString(GovernedLearnerDocument.serializer(), json.toString())
            assertEquals(1, old.schemaVersion); assertTrue(old.reviewAssignments.isEmpty()); assertTrue(old.reviewAnswers.isEmpty())
            assertEquals(GovernedReplaceOutcome.REPLACED, s.replaceGovernedLearner(old, emptyList()))
        }
    }

    @Test fun malformedRolePreBoundaryAndWrongAssignmentDigestNeverSupplyCompletion() {
        val p = pack()
        prepared(p).use { s ->
            val a = open(p, s); answer(p, s, a, 0, true)
            val valid = s.cumulativeReviewHistory().second.single()
            for (bad in listOf(valid.copy(role = PRESCRIBED_CHECK), valid.copy(acceptedOrder = a.boundary),
                valid.copy(assignmentDigest = "forged"), valid.copy(schema = 0))) {
                assertTrue(completedReviewSlots(p, a, listOf(bad)).isEmpty())
            }
        }
    }

    @Test fun genericKernelPreservesTheOriginalUnit3PolicyAndAssignmentIdentity() {
        val p = pack()
        val legacyPool = listOf(
            Triple("obj-u1-decoding", "BOUNDED_READING", listOf("u1-i-decode-pair")),
            Triple("obj-u1-confusable", "PRINTED_LETTER_RECOGNITION", listOf(
                "u1-i-р-not-p", "u1-i-н-not-h", "u1-i-с-not-c", "u1-i-в-not-b", "u1-i-у-not-y", "u1-i-х-not-x")),
        )
        val legacyDigest = reviewHash(canonicalParts(listOf("unit3-prior-literacy-v1", "target:3", "slots:2") +
            legacyPool.map { canonicalParts(listOf(it.first, it.second, canonicalParts(it.third))) }))
        assertEquals(legacyDigest, cumulativeReviewPolicy(3)!!.digest)
        prepared(p).use { s ->
            val original = createUnit3ReviewAssignment(p, s.v2LearnerView(), "same-id", 0)!!
            assertEquals(original, createCumulativeReviewAssignment(p, s.v2LearnerView(), 3, "same-id", 0))
            val persisted = open(p, s)
            assertEquals(persisted, s.openCumulativeReview(p, 3, s.learnerMutationEpoch()))
        }
    }

    @Test fun undeclaredTargetsAndFlagOnlyLaterGatesCannotCreateHistory() {
        val p = pack()
        prepared(p).use { s ->
            val before = s.governedLearnerFingerprint()
            for (n in listOf(1, 2, 8, 27, 30, 31)) {
                assertNull(cumulativeReviewPolicy(n))
                assertNull(s.openCumulativeReview(p, n, s.learnerMutationEpoch()))
                assertNull(createCumulativeReviewAssignment(p, s.v2LearnerView(), n, "undeclared", 0))
            }
            val flagOnly = p.copy(units = p.units.map { if (it.number == 8) it.copy(
                advancement = it.advancement!!.copy(requireDueReview = true, cumulativeReviewPolicyId = Unit3CumulativeReviewPolicy.id)) else it })
            assertFalse(cumulativeReviewDeclarationValid(flagOnly, 8))
            assertTrue(currentUnitRequirements(flagOnly, flagOnly.unit(8)!!, s.v2LearnerView()).remaining
                .contains("The cumulative-review rule cannot be verified"))
            assertTrue(V2Validator.validate(flagOnly).errors.contains("Unit 8 has no verified cumulative-review policy"))
            val emptyFlag = flagOnly.copy(units = flagOnly.units.map { if (it.number == 8) it.copy(
                advancement = it.advancement!!.copy(cumulativeReviewPolicyId = null)) else it })
            assertTrue(currentUnitRequirements(emptyFlag, emptyFlag.unit(8)!!, s.v2LearnerView()).remaining
                .contains("The cumulative-review rule cannot be verified"))
            assertNull(s.openCumulativeReview(emptyFlag, 8, s.learnerMutationEpoch()))
            assertNull(s.openCumulativeReview(flagOnly, 8, s.learnerMutationEpoch()))
            assertEquals(before, s.governedLearnerFingerprint())
            assertTrue(s.cumulativeReviewHistory().first.isEmpty())
        }
    }

    @Test fun typedPoliciesRefuseEmptyRepeatedAndAmbiguousCandidatePools() {
        val valid = cumulativeReviewPolicy(3)!!
        for (invalid in listOf(
            { valid.copy(pool = emptyList()) },
            { valid.copy(pool = listOf(valid.pool.first())) },
            { valid.copy(pool = listOf(valid.pool.first(), valid.pool.first())) },
            { valid.copy(pool = valid.pool.map { it.copy(candidateItemIds = emptyList()) }) },
            { valid.copy(pool = valid.pool.map { it.copy(candidateItemIds = listOf("duplicate", "duplicate")) }) },
            { valid.copy(targetUnit = 2) },
        )) {
            try { invalid(); fail("invalid policy accepted") } catch (_: IllegalArgumentException) { }
        }
    }

    @Test fun disabledOrWrongPolicyRejectsOldAssignmentsAndAnswersWithoutMutation() {
        val p = pack()
        prepared(p).use { s ->
            val a = open(p, s); val before = s.governedLearnerFingerprint()
            for (disabled in listOf(false, true)) {
                val changed = p.copy(units = p.units.map { if (it.number == 3) it.copy(advancement =
                    it.advancement!!.copy(requireDueReview = disabled, cumulativeReviewPolicyId = if (disabled) "unknown-policy" else Unit3CumulativeReviewPolicy.id)) else it })
                assertFalse(reviewAssignmentCurrent(changed, a))
                assertNull(s.openCumulativeReview(changed, 3, s.learnerMutationEpoch()))
                assertFalse(s.answerCumulativeReview(changed, a.id, 0, p.item(a.slots[0].itemId)!!.answer, s.learnerMutationEpoch()))
                assertEquals(before, s.governedLearnerFingerprint())
            }
        }
    }

    @Test fun completionMetadataCannotLinkAReviewToAnotherUnitDuringRestore() {
        val p = pack()
        prepared(p).use { s ->
            val a = open(p, s); answer(p, s, a, 0, true); answer(p, s, a, 1, true)
            val doc = s.governedLearnerDocument()
            val link = BackupUnitCompletion(3, "snapshot", false, a.policyDigest, a.id,
                doc.reviewAnswers.maxOf { it.acceptedOrder })
            assertNull(completionActivationDefects(doc.copy(unitCompletions = doc.unitCompletions + link)))
            val forged = doc.copy(unitCompletions = doc.unitCompletions + link.copy(unitNumber = 4))
            assertNotNull(completionActivationDefects(forged))
            LocalStore.openInMemory(context()).use { target ->
                val before = target.governedLearnerFingerprint()
                assertEquals(GovernedReplaceOutcome.UNCHANGED, target.replaceGovernedLearner(forged, emptyList()))
                assertEquals(before, target.governedLearnerFingerprint())
            }
        }
    }

    private fun prepared(p: V2CurriculumPack) = LocalStore.openInMemory(context()).also { finish(p, it) }
    private fun finish(p: V2CurriculumPack, s: LocalStore) {
        for (n in 1..2) {
            s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = n))
            for (step in p.unit(n)!!.sessions.flatMap { it.steps }) {
                for (id in step.itemIds) s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, p.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "same-clock")!!)
                s.v2CompleteGovernedStep(step.id, governedStepCompletionDigest(p, s.v2LearnerView(), step.id)!!, "same-clock")
            }
            s.v2AttestBook(n, "same-clock"); s.v2CompleteUnit(n, "same-clock")
        }
        s.v2SaveProfile(s.v2Profile().copy(currentUnitNumber = 3))
        assertEquals(setOf(1, 2), currentCompletedUnitNumbers(p, s.v2LearnerView()))
    }
    private fun open(p: V2CurriculumPack, s: LocalStore) = s.openUnit3CumulativeReview(p, s.learnerMutationEpoch())!!
    private fun answer(p: V2CurriculumPack, s: LocalStore, a: CumulativeReviewAssignment, index: Int, correct: Boolean) {
        val item = p.item(a.slots[index].itemId)!!
        assertTrue(s.answerCumulativeReview(p, a.id, index, if (correct) item.answer else item.options.first { it != item.answer }, s.learnerMutationEpoch()))
    }
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    private fun root() = File(if (File("app").isDirectory) "." else "..")
    private fun pack() = sharedPack
    companion object {
        // The pack is immutable. Each test still uses its own store, learner and copied mutations.
        private val sharedPack by lazy {
            val root = File(if (File("app").isDirectory) "." else "..")
            V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) }
        }
    }
}
