package dev.russiancompanion.v2

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.data.local.MIGRATION_6_7
import dev.russiancompanion.data.local.MIGRATION_7_8
import dev.russiancompanion.v2.backup.backupJson
import dev.russiancompanion.v2.backup.BackupAttempt
import dev.russiancompanion.v2.backup.readGovernedBackup
import dev.russiancompanion.v2.backup.BackupReadResult
import dev.russiancompanion.v2.backup.restoreStoreBackup
import dev.russiancompanion.v2.backup.writeStoreBackup
import dev.russiancompanion.v2.curriculum.GOVERNED_IDENTITY_SCHEMA
import dev.russiancompanion.v2.curriculum.GUIDED_PRACTICE_GAP_STEP_IDS
import dev.russiancompanion.v2.curriculum.OPTIONAL_PRACTICE
import dev.russiancompanion.v2.curriculum.PRESCRIBED_CHECK
import dev.russiancompanion.v2.curriculum.UNRECOVERABLE_GOVERNED_IDENTITY
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.creditItemIds
import dev.russiancompanion.v2.curriculum.governedSelectionKey
import dev.russiancompanion.v2.curriculum.governedSessionSelections
import dev.russiancompanion.v2.curriculum.governedStepCompletionDigest
import dev.russiancompanion.v2.curriculum.stepReadyForGovernedCompletion
import dev.russiancompanion.v2.curriculum.item
import dev.russiancompanion.v2.curriculum.practiceOffers
import dev.russiancompanion.v2.curriculum.practiceQueue
import dev.russiancompanion.v2.curriculum.practiceStepId
import dev.russiancompanion.v2.curriculum.prepareGovernedAttempt
import dev.russiancompanion.v2.curriculum.savedWorkEntries
import dev.russiancompanion.v2.curriculum.step
import dev.russiancompanion.v2.curriculum.stepContentDigest
import dev.russiancompanion.v2.curriculum.verifiedResume
import dev.russiancompanion.v2.ui.V2App
import dev.russiancompanion.v2.ui.V2UiWork
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class PracticeRetrievalDigestTest {
    @get:Rule
    val compose = createComposeRule()

    private val stores = mutableListOf<LocalStore>()

    @After
    fun tearDown() {
        V2UiWork.blockForTests = null
        stores.forEach { it.close() }
    }

    @Test
    fun alphabetPracticeScoresFeedbackWithoutPrescribedCreditOrDuplicateRows() {
        val pack = pack()
        val store = memory()
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 1))
        val item = practiceQueue(pack, store.v2LearnerView(), "alphabet").first()
        val wrong = item.options.first { it != item.answer }
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        compose.onNodeWithText("Practice").performClick()
        compose.onNodeWithText("Alphabet recognition").performScrollTo().performClick()
        compose.onNodeWithTag("v2-lab-prompt").assertIsDisplayed()
        compose.onNodeWithTag("v2-lab-option-$wrong").performScrollTo().performClick()
        compose.onNodeWithTag("v2-lab-submit").performClick()
        compose.onNodeWithText("Not the reviewed answer.", substring = true).assertIsDisplayed()
        assertEquals(1, store.v2Attempts().count { it.itemId == item.id })
        val row = store.v2Attempts().single { it.itemId == item.id }
        assertEquals(OPTIONAL_PRACTICE, row.attemptRole)
        assertEquals(V2Validator.checkItemContentDigest(item), row.itemDigest)
        assertEquals(V2Validator.unitContentReviewDigest(pack, 1), row.unitContentDigest)
        assertEquals(GOVERNED_IDENTITY_SCHEMA, row.identitySchema)
        assertFalse(row.correct)
        assertFalse(item.id in creditItemIds(pack, store.v2LearnerView()))
        store.v2RecordGovernedAttempt(
            prepareGovernedAttempt(pack, item.id, wrong, OPTIONAL_PRACTICE, practiceStepId("alphabet"), "2026-10-04T01:00:00")!!,
        )
        assertEquals(1, store.v2Attempts().count { it.itemId == item.id })
    }

    @Test
    fun unansweredPracticeAndBlockedLabsDoNotRecordCompletion() {
        val pack = pack()
        val store = memory()
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 1))
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        compose.onNodeWithText("Practice").performClick()
        compose.onNodeWithText("Listening Lab. Mapped audio is not installed.", substring = true)
            .performScrollTo()
            .assertIsDisplayed()
        compose.onNodeWithText("Pronunciation Lab. Model audio is not installed.", substring = true)
            .performScrollTo()
            .assertIsDisplayed()
        compose.onNodeWithText("Handwriting. Handwriting assets are not installed.", substring = true)
            .performScrollTo()
            .assertIsDisplayed()
        compose.onNodeWithText("Reading Lab").performScrollTo().performClick()
        compose.onNodeWithTag("v2-lab-empty").assertIsDisplayed()
        compose.onNodeWithTag("v2-back").performClick()
        compose.onNodeWithText("Alphabet recognition").performScrollTo().performClick()
        compose.onNodeWithTag("v2-back").performClick()
        assertTrue(store.v2Attempts().isEmpty())
        assertTrue(store.v2StepProgress().isEmpty())
        val offers = practiceOffers(pack.copy(schemaVersion = 2), store.v2LearnerView())
        assertTrue(offers.all { !it.runnable })
        assertTrue(practiceQueue(pack.copy(schemaVersion = 2), store.v2LearnerView(), "alphabet").isEmpty())
        assertNull(prepareGovernedAttempt(pack, "missing", "x", OPTIONAL_PRACTICE, "practice:alphabet", "t"))
        GUIDED_PRACTICE_GAP_STEP_IDS.forEach { id ->
            assertTrue(pack.step(id)!!.second.itemIds.isEmpty())
        }
    }

    @Test
    fun legacyAttemptLeavesContinueDisabledAndCreatesNoCompletedStep() {
        val pack = pack()
        val step = pack.step("u1-st-vowels-check")!!.second
        val item = pack.item(step.itemIds.single())!!
        val store = memory()
        rememberVowels(pack, store)
        store.v2RecordAttempt(item.id, item.answer, true, "2026-10-04T05:00:00")
        assertUnverifiedCannotFinish(pack, store, step.id, item.id)
    }

    @Test
    fun mismatchedDigestAttemptLeavesContinueDisabledAndCreatesNoCompletedStep() {
        val pack = pack()
        val step = pack.step("u1-st-vowels-check")!!.second
        val item = pack.item(step.itemIds.single())!!
        val store = memory()
        rememberVowels(pack, store)
        val prepared = prepareGovernedAttempt(pack, item.id, item.answer, PRESCRIBED_CHECK, step.id, "2026-10-04T05:01:00")!!
        store.v2RecordGovernedAttempt(prepared.copy(id = "mismatch-item", itemDigest = "f".repeat(64)))
        store.v2RecordGovernedAttempt(prepared.copy(id = "mismatch-unit", unitContentDigest = "e".repeat(64)))
        assertEquals("f".repeat(64), store.v2Attempts().single { it.id == "mismatch-item" }.itemDigest)
        assertEquals("e".repeat(64), store.v2Attempts().single { it.id == "mismatch-unit" }.unitContentDigest)
        assertUnverifiedCannotFinish(pack, store, step.id, item.id)
    }

    @Test
    fun otherStepAttemptLeavesContinueDisabledAndCreatesNoCompletedStep() {
        val pack = pack()
        val step = pack.step("u1-st-vowels-check")!!.second
        val item = pack.item(step.itemIds.single())!!
        val otherStepId = "u1-st-confusable-rest"
        assertFalse(item.id in pack.step(otherStepId)!!.second.itemIds)
        val store = memory()
        rememberVowels(pack, store)
        store.v2RecordGovernedAttempt(
            prepareGovernedAttempt(pack, item.id, item.answer, PRESCRIBED_CHECK, otherStepId, "2026-10-04T05:02:00")!!,
        )
        assertEquals(otherStepId, store.v2Attempts().single().stepId)
        assertUnverifiedCannotFinish(pack, store, step.id, item.id)
    }

    @Test
    fun governedAnswerEnablesContinueAfterAFreshLoadAndSurvivesRefresh() {
        val pack = pack()
        val step = pack.step("u1-st-vowels-check")!!.second
        val item = pack.item(step.itemIds.single())!!
        val store = memory()
        rememberStep(pack, store, step.id)
        store.v2RecordGovernedAttempt(
            prepareGovernedAttempt(pack, item.id, item.answer, PRESCRIBED_CHECK, step.id, "2026-10-04T06:00:00")!!,
        )
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        compose.onNodeWithTag("v2-continue").performClick()
        compose.onNodeWithTag("v2-option-${item.id}-${item.answer}").performScrollTo().assertIsSelected()
        compose.onNodeWithTag("v2-session-continue").assertIsEnabled()
        assertEquals(0, compose.onAllNodesWithText("Answer each item before this step is recorded.").fetchSemanticsNodes().size)
        compose.onNodeWithText("Bookmark this step").performScrollTo().performClick()
        compose.onNodeWithTag("v2-persistence-state").performScrollTo().assertTextEquals("Saved")
        compose.onNodeWithTag("v2-option-${item.id}-${item.answer}").performScrollTo().assertIsSelected()
        compose.onNodeWithTag("v2-session-continue").assertIsEnabled()
        assertTrue(stepReadyForGovernedCompletion(pack, store.v2LearnerView(), step.id))
    }

    @Test
    fun twoGovernedAnswersOnOneStepBothRemainAfterTheSecondWrite() {
        val pack = pack()
        val step = pack.step("u1-st-confusable-rest")!!.second
        val first = pack.item(step.itemIds[0])!!
        val second = pack.item(step.itemIds[1])!!
        val store = memory()
        rememberStep(pack, store, step.id)
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        compose.onNodeWithTag("v2-continue").performClick()
        val firstOption = first.options.first { it != first.answer }
        val secondOption = second.options.first { it != second.answer }
        compose.onNodeWithTag("v2-option-${first.id}-$firstOption").performScrollTo().performClick()
        compose.onNodeWithTag("v2-option-${second.id}-$secondOption").performScrollTo().performClick()
        compose.onNodeWithTag("v2-option-${first.id}-$firstOption").performScrollTo().assertIsSelected()
        compose.onNodeWithTag("v2-option-${second.id}-$secondOption").performScrollTo().assertIsSelected()
        assertEquals(firstOption, governedSessionSelections(pack, store.v2LearnerView())[governedSelectionKey(step.id, first.id)])
        assertEquals(secondOption, governedSessionSelections(pack, store.v2LearnerView())[governedSelectionKey(step.id, second.id)])
        compose.onNodeWithTag("v2-session-continue").assertIsNotEnabled()
    }

    @Test
    fun unverifiedAttemptsStayExcludedAfterReload() {
        val pack = pack()
        val step = pack.step("u1-st-vowels-check")!!.second
        val item = pack.item(step.itemIds.single())!!
        val store = memory()
        rememberStep(pack, store, step.id)
        store.v2RecordAttempt(item.id, item.answer, true, "2026-10-04T06:01:00")
        val prepared = prepareGovernedAttempt(pack, item.id, item.answer, PRESCRIBED_CHECK, step.id, "2026-10-04T06:02:00")!!
        store.v2RecordGovernedAttempt(prepared.copy(id = "reload-mismatch", itemDigest = "a".repeat(64)))
        store.v2RecordGovernedAttempt(
            prepareGovernedAttempt(pack, item.id, item.answer, OPTIONAL_PRACTICE, practiceStepId("alphabet"), "2026-10-04T06:03:00")!!,
        )
        store.v2RecordGovernedAttempt(
            prepareGovernedAttempt(pack, item.id, item.answer, PRESCRIBED_CHECK, "u1-st-confusable-rest", "2026-10-04T06:04:00")!!,
        )
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        compose.onNodeWithTag("v2-continue").performClick()
        compose.onNodeWithText("Answer each item before this step is recorded.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("v2-session-continue").assertIsNotEnabled()
        compose.onNodeWithText("Bookmark this step").performScrollTo().performClick()
        compose.onNodeWithTag("v2-session-continue").assertIsNotEnabled()
        assertFalse(governedSessionSelections(pack, store.v2LearnerView()).containsKey(governedSelectionKey(step.id, item.id)))
        assertFalse(stepReadyForGovernedCompletion(pack, store.v2LearnerView(), step.id))
        assertTrue(store.v2StepProgress().none { it.stepId == step.id })
    }

    @Test
    fun optionalPracticeAcceptsALaterAnswerAndSuppressesTheSameSelection() {
        val pack = pack()
        val store = memory()
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 1))
        val item = practiceQueue(pack, store.v2LearnerView(), "alphabet").first()
        val wrong = item.options.first { it != item.answer }
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        compose.onNodeWithText("Practice").performClick()
        compose.onNodeWithText("Alphabet recognition").performScrollTo().performClick()
        compose.onNodeWithTag("v2-lab-option-$wrong").performScrollTo().performClick()
        compose.onNodeWithTag("v2-lab-submit").performClick()
        compose.onNodeWithText("Not the reviewed answer.", substring = true).performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("v2-lab-option-$wrong").assertIsEnabled()
        assertEquals(0, compose.onAllNodesWithTag("v2-lab-submit").fetchSemanticsNodes().size)
        compose.onNodeWithTag("v2-lab-option-${item.answer}").performScrollTo().performClick()
        compose.onNodeWithTag("v2-lab-submit").performClick()
        compose.onNodeWithText("Recorded as correct.", substring = true).performScrollTo().assertIsDisplayed()
        val rows = store.v2Attempts().filter { it.itemId == item.id }
        assertEquals(2, rows.size)
        assertTrue(rows.all { it.attemptRole == OPTIONAL_PRACTICE })
        assertFalse(item.id in creditItemIds(pack, store.v2LearnerView()))
        assertEquals(0, compose.onAllNodesWithTag("v2-lab-submit").fetchSemanticsNodes().size)
        store.v2RecordGovernedAttempt(
            prepareGovernedAttempt(pack, item.id, item.answer, OPTIONAL_PRACTICE, practiceStepId("alphabet"), "2026-10-04T05:03:00")!!,
        )
        assertEquals(2, store.v2Attempts().count { it.itemId == item.id })
    }

    @Test
    fun checkStepDoesNotCompleteUntilItIsAnswered() {
        val pack = pack()
        val store = memory()
        val located = pack.step("u1-st-vowels-check")!!
        val index = located.first.steps.indexOfFirst { it.id == "u1-st-vowels-check" }
        store.v2SaveProfile(
            store.v2Profile().copy(
                onboardingCompleted = true,
                currentUnitNumber = 1,
                currentSessionId = located.first.id,
                currentStepIndex = index,
            ).verifiedResume(pack),
        )
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        compose.onNodeWithTag("v2-continue").performClick()
        compose.onNodeWithText("Answer each item before this step is recorded.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("v2-session-continue").assertIsNotEnabled()
        compose.onNodeWithTag("v2-back").performClick()
        assertTrue(store.v2Attempts().isEmpty())
        assertFalse("u1-st-vowels-check" in store.v2LearnerView().completedStepIds)
    }

    @Test
    fun savedWorkResumesABookmarkWithoutANewAttemptAndHidesCompletedChecks() {
        val pack = pack()
        val store = memory()
        val located = pack.step("u1-st-orient")!!
        val step = located.second
        val sessionId = located.first.id
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 1))
        store.v2ToggleBookmark(step.id, "STEP", "2026-10-04T02:00:00", stepContentDigest(pack, step), GOVERNED_IDENTITY_SCHEMA)
        val prescribed = prepareGovernedAttempt(
            pack,
            practiceQueue(pack, store.v2LearnerView(), "alphabet").first().id,
            practiceQueue(pack, store.v2LearnerView(), "alphabet").first().answer,
            PRESCRIBED_CHECK,
            "u1-st-vowels-check",
            "2026-10-04T02:01:00",
        )!!
        store.v2RecordGovernedAttempt(prescribed)
        val entries = savedWorkEntries(pack, store.v2LearnerView())
        assertTrue(entries.any { it.resumable && it.title == step.title })
        assertFalse(entries.any { it.id.startsWith("attempt:") })
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        compose.onNodeWithText("Saved work").performScrollTo().performClick()
        compose.onNodeWithTag("v2-saved-bookmark:${step.id}").performScrollTo().performClick()
        compose.onNodeWithTag("v2-step-title").assertTextEquals(step.title)
        assertEquals(1, store.v2Attempts().size)
        compose.onNodeWithTag("v2-back").performClick()
        compose.onNodeWithTag("v2-saved-resume:$sessionId").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("v2-saved-bookmark:${step.id}").performScrollTo().performClick()
        assertEquals(1, store.v2Attempts().size)
        compose.onNodeWithTag("v2-back").performClick()
        compose.onNodeWithTag("v2-saved-resume:$sessionId").performScrollTo().performClick()
        assertEquals(1, store.v2Attempts().size)
    }

    @Test
    fun emptyAndUnverifiedSavedPositionsStayClosed() {
        val pack = pack()
        val store = memory()
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 1))
        assertEquals("No saved work yet.", savedWorkEntries(pack, store.v2LearnerView()).let { if (it.isEmpty()) "No saved work yet." else it.first().detail })
        store.v2SaveProfile(
            store.v2Profile().copy(currentSessionId = "u1-s-orient", currentStepIndex = 0, resumeIdentitySchema = 0, resumeContentDigest = ""),
        )
        val stale = savedWorkEntries(pack, store.v2LearnerView()).single { it.kind == "STALE" }
        assertFalse(stale.resumable)
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        compose.onNodeWithTag("v2-continue").assertIsNotEnabled()
        compose.onNodeWithText("Saved lesson cannot be verified").assertIsDisplayed()
    }

    @Test
    fun legacyAndMismatchedAttemptsDoNotGrantCreditAndBackupKeepsTheOriginalDigest() {
        val pack = pack()
        val store = memory()
        val item = practiceQueue(pack, store.v2LearnerView().copy(currentUnitNumber = 1), "alphabet").first()
        store.v2RecordAttempt(item.id, item.answer, true, "2026-10-04T03:00:00")
        val legacy = store.v2Attempts().single()
        assertEquals(UNRECOVERABLE_GOVERNED_IDENTITY, legacy.itemDigest)
        assertEquals(0, legacy.identitySchema)
        assertFalse(item.id in creditItemIds(pack, store.v2LearnerView()))
        val stepId = pack.units.flatMap { it.sessions }.flatMap { it.steps }.first { item.id in it.itemIds }.id
        val write = prepareGovernedAttempt(pack, item.id, item.answer, PRESCRIBED_CHECK, stepId, "2026-10-04T03:01:00")!!
        store.v2RecordGovernedAttempt(write)
        assertTrue(item.id in creditItemIds(pack, store.v2LearnerView()))
        val changed = pack.copy(items = pack.items.map { if (it.id == item.id) it.copy(answer = it.options.first { option -> option != it.answer }) else it })
        assertFalse(item.id in creditItemIds(changed, store.v2LearnerView()))
        val stored = store.v2Attempts().single { it.identitySchema == GOVERNED_IDENTITY_SCHEMA }
        assertEquals(write.itemDigest, stored.itemDigest)
        val file = File.createTempFile("practice-backup", ".zip")
        file.outputStream().use { writeStoreBackup(store, it, "dev.russiancompanion", pack.packId, pack.packVersion, "DARK") }
        val read = readGovernedBackup(file.inputStream(), "dev.russiancompanion", pack.packId, pack.packVersion, File.createTempFile("practice-restore", "").apply { delete(); mkdirs() })
        val validated = (read as BackupReadResult.Valid).backup
        val restored = memory()
        assertEquals(dev.russiancompanion.v2.backup.GovernedReplaceOutcome.REPLACED, restoreStoreBackup(restored, validated, File.createTempFile("practice-recovery", "").apply { delete(); mkdirs() }))
        val kept = restored.v2Attempts().single { it.id == stored.id }
        assertEquals(stored.itemDigest, kept.itemDigest)
        assertEquals(stored.ruleDigest, kept.ruleDigest)
        assertEquals(stored.unitContentDigest, kept.unitContentDigest)
        val oldJson = """{"id":"old","itemId":"item","selected":"yes","correct":true,"recordedAtLocal":"t"}"""
        val old = backupJson.decodeFromString(BackupAttempt.serializer(), oldJson)
        assertEquals(UNRECOVERABLE_GOVERNED_IDENTITY, old.itemDigest)
        assertEquals(0, old.identitySchema)
        assertEquals("LEGACY_UNVERIFIED", old.attemptRole)
    }

    @Test
    fun migrationMarksExistingAttemptIdentityUnrecoverable() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name("practice-migration-${System.nanoTime()}.db")
                .callback(object : SupportSQLiteOpenHelper.Callback(6) {
                    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) = Unit

                    override fun onUpgrade(db: androidx.sqlite.db.SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
                })
                .build(),
        )
        val db = helper.writableDatabase
        MIGRATION_6_7.migrate(db)
        db.execSQL("INSERT INTO v2_attempts (id, itemId, selected, correct, recordedAtLocal) VALUES ('a', 'item', 'yes', 1, 't')")
        db.execSQL("INSERT INTO v2_step_progress (stepId, completedAtLocal) VALUES ('u1-st-orient', 't')")
        MIGRATION_7_8.migrate(db)
        db.query("SELECT itemDigest, ruleDigest, identitySchema, attemptRole FROM v2_attempts").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(UNRECOVERABLE_GOVERNED_IDENTITY, cursor.getString(0))
            assertEquals(UNRECOVERABLE_GOVERNED_IDENTITY, cursor.getString(1))
            assertEquals(0, cursor.getInt(2))
            assertEquals("LEGACY_UNVERIFIED", cursor.getString(3))
        }
        db.query("SELECT contentDigest, identitySchema FROM v2_step_progress").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(UNRECOVERABLE_GOVERNED_IDENTITY, cursor.getString(0))
            assertEquals(0, cursor.getInt(1))
        }
        helper.close()
    }

    @Test
    fun readingVocabularyGrammarAndMixedStayOptionalAndPrerequisiteFiltered() {
        val pack = pack()
        val store = memory()
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 4))
        val before = store.v2LearnerView()
        val reading = practiceQueue(pack, before, "reading").first { it.id == "u4-i-stress-6f15e9f1" }
        assertTrue(practiceQueue(pack, before, "vocabulary").isNotEmpty())
        assertTrue(practiceQueue(pack, before, "grammar").isNotEmpty())
        assertTrue(practiceQueue(pack, before, "alphabet").isEmpty())
        assertTrue(practiceQueue(pack, before, "mixed").isEmpty())
        val wrong = reading.options.first { it != reading.answer }
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        compose.onNodeWithText("Practice").performClick()
        compose.onNodeWithText("Reading Lab").performScrollTo().performClick()
        compose.onNodeWithTag("v2-lab-option-$wrong").performScrollTo().performClick()
        compose.onNodeWithTag("v2-lab-option-$wrong").performClick()
        compose.onNodeWithTag("v2-lab-submit").performClick()
        compose.onNodeWithText("Not the reviewed answer.", substring = true).performScrollTo().assertIsDisplayed()
        assertEquals(1, store.v2Attempts().size)
        assertEquals(OPTIONAL_PRACTICE, store.v2Attempts().single().attemptRole)
        assertFalse(reading.id in creditItemIds(pack, store.v2LearnerView()))
        compose.onNodeWithTag("v2-back").performClick()
        compose.onNodeWithText("Vocabulary and forms").performScrollTo().performClick()
        compose.onNodeWithTag("v2-lab-prompt").assertIsDisplayed()
        compose.onNodeWithTag("v2-back").performClick()
        compose.onNodeWithText("Grammar Lab").performScrollTo().performClick()
        compose.onNodeWithTag("v2-lab-prompt").assertIsDisplayed()
        compose.onNodeWithTag("v2-back").performClick()
        compose.onNodeWithText("Mixed review").performScrollTo().performClick()
        compose.onNodeWithTag("v2-lab-empty").assertIsDisplayed()
        assertEquals(1, store.v2Attempts().size)
        store.v2CompleteUnit(3, "2026-10-04T04:00:00")
        val mixed = practiceQueue(pack, store.v2LearnerView(), "mixed")
        assertTrue(mixed.isNotEmpty())
        assertTrue(mixed.all { it.kind == "SOURCE_MEANING" || it.kind == "SOURCE_GENDER" })
        assertTrue(mixed.none { it.id == reading.id })
    }

    @Test
    fun prescribedCheckScoresRemediationAndCompletesOnce() {
        val pack = pack()
        val store = memory()
        val located = pack.step("u1-st-vowels-check")!!
        val step = located.second
        val index = located.first.steps.indexOfFirst { it.id == step.id }
        val item = pack.item(step.itemIds.single())!!
        val wrong = item.options.first { it != item.answer }
        store.v2SaveProfile(
            store.v2Profile().copy(
                onboardingCompleted = true,
                currentUnitNumber = 1,
                currentSessionId = located.first.id,
                currentStepIndex = index,
            ).verifiedResume(pack),
        )
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        compose.onNodeWithTag("v2-continue").performClick()
        compose.onNodeWithTag("v2-option-${item.id}-$wrong").performScrollTo().performClick()
        compose.onNodeWithTag("v2-option-${item.id}-$wrong").performClick()
        compose.onNodeWithText("Not the reviewed answer.", substring = true).performScrollTo().assertIsDisplayed()
        assertEquals(1, store.v2Attempts().count { it.itemId == item.id })
        compose.onNodeWithTag("v2-remediation-${item.id}").performScrollTo().performClick()
        val explanation = pack.step(step.remediationStepId!!)!!.second
        compose.onNodeWithTag("v2-step-title").assertTextEquals(explanation.title)
        assertTrue(store.v2StepProgress().isEmpty())
        compose.onNodeWithTag("v2-back").performClick()
        compose.onNodeWithTag("v2-option-${item.id}-${item.answer}").performScrollTo().performClick()
        compose.onNodeWithText("Recorded as correct.", substring = true).performScrollTo().assertIsDisplayed()
        assertEquals(2, store.v2Attempts().count { it.itemId == item.id })
        assertTrue(store.v2Attempts().any { it.itemId == item.id && it.correct && it.attemptRole == PRESCRIBED_CHECK })
        compose.onNodeWithTag("v2-session-continue").performClick()
        assertEquals(1, store.v2StepProgress().count { it.stepId == step.id })
        assertEquals(stepContentDigest(pack, step), store.v2StepProgress().single { it.stepId == step.id }.contentDigest)
        assertTrue(step.id in store.v2LearnerView().completedStepIds)
        assertFalse(savedWorkEntries(pack, store.v2LearnerView()).any { it.kind == "RESUME" && it.title == step.title })
    }

    @Test
    fun mismatchedResumeDigestStaysClosed() {
        val pack = pack()
        val store = memory()
        store.v2SaveProfile(
            store.v2Profile().copy(
                onboardingCompleted = true,
                currentUnitNumber = 1,
                currentSessionId = "u1-s-orient",
                currentStepIndex = 0,
                resumeIdentitySchema = GOVERNED_IDENTITY_SCHEMA,
                resumeContentDigest = "a".repeat(64),
            ),
        )
        val stale = savedWorkEntries(pack, store.v2LearnerView()).single { it.kind == "STALE" }
        assertFalse(stale.resumable)
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        compose.onNodeWithTag("v2-continue").assertIsNotEnabled()
        compose.onNodeWithText("Saved lesson does not match").assertIsDisplayed()
        compose.onNodeWithText("Saved work").performScrollTo().performClick()
        compose.onNodeWithText("Not resumed").assertIsDisplayed()
        assertTrue(store.v2Attempts().isEmpty())
        assertTrue(store.v2StepProgress().isEmpty())
    }

    @Test
    @Config(sdk = [33], qualifiers = "w411dp-h914dp-land", fontScale = 2f)
    fun practiceAndSavedWorkStayReachableAtLargeFontInLandscape() {
        val pack = pack()
        val store = memory()
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 1))
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        compose.onNodeWithText("Practice").performClick()
        compose.onNodeWithTag("v2-practice-heading").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Saved work").performScrollTo().performClick()
        compose.onNodeWithTag("v2-saved-heading").assertIsDisplayed()
        compose.onNodeWithText("No saved work yet.").assertIsDisplayed()
    }

    private fun rememberStep(pack: V2CurriculumPack, store: LocalStore, stepId: String) {
        val located = pack.step(stepId)!!
        val index = located.first.steps.indexOfFirst { it.id == stepId }
        store.v2SaveProfile(
            store.v2Profile().copy(
                onboardingCompleted = true,
                currentUnitNumber = 1,
                currentSessionId = located.first.id,
                currentStepIndex = index,
            ).verifiedResume(pack),
        )
    }

    private fun rememberVowels(pack: V2CurriculumPack, store: LocalStore) {
        rememberStep(pack, store, "u1-st-vowels-check")
    }

    private fun assertUnverifiedCannotFinish(
        pack: V2CurriculumPack,
        store: LocalStore,
        stepId: String,
        itemId: String,
    ) {
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        compose.onNodeWithTag("v2-continue").performClick()
        compose.onNodeWithText("Answer each item before this step is recorded.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("v2-session-continue").assertIsNotEnabled()
        val learner = store.v2LearnerView()
        assertTrue(learner.attemptSelections.isEmpty())
        assertFalse(governedSessionSelections(pack, learner).containsKey(governedSelectionKey(stepId, itemId)))
        assertFalse(stepReadyForGovernedCompletion(pack, learner, stepId))
        assertNull(governedStepCompletionDigest(pack, learner, stepId))
        assertTrue(store.v2StepProgress().none { it.stepId == stepId })
    }

    private fun memory(): LocalStore = LocalStore.openInMemory(context()).also { stores += it }

    private fun context(): Context = ApplicationProvider.getApplicationContext()

    private fun pack(): V2CurriculumPack {
        val root = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
        return V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText())
    }
}
