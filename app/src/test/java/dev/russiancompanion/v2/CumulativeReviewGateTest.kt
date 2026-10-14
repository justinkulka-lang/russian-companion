package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.ui.V2App
import dev.russiancompanion.v2.ui.StoreV2Persistence
import dev.russiancompanion.v2.ui.V2Persistence
import dev.russiancompanion.v2.ui.V2UiWork
import kotlinx.coroutines.CompletableDeferred
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.ByteArrayOutputStream
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import org.json.JSONObject

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class CumulativeReviewGateTest {
    @get:Rule val compose = createComposeRule()
    @After fun resetUiMode() { V2UiWork.blockForTests = null }

    @Test fun completeOrdinaryWorkCannotAdvanceUntilBothAssignedObjectivesAreReviewed() {
        val p = pack()
        ready(p).use { s ->
            assertTrue(ordinaryUnitRequirements(p, p.unit(3)!!, s.v2LearnerView()).allowed)
            assertFalse(advancementDecision(p, p.unit(3)!!, s.v2LearnerView()).allowed)
            val a = s.openUnit3CumulativeReview(p, s.learnerMutationEpoch())!!
            answer(p, s, a, 0)
            assertFalse(advancementDecision(p, p.unit(3)!!, s.v2LearnerView()).allowed)
            answer(p, s, a, 1)
            assertTrue(advancementDecision(p, p.unit(3)!!, s.v2LearnerView()).allowed)
            val learner=s.v2LearnerView()
            assertFalse(advancementDecision(p,p.unit(3)!!,learner.copy(bookAttestedUnits=learner.bookAttestedUnits-3)).allowed)
            assertFalse(advancementDecision(p,p.unit(3)!!,learner.copy(governedAttempts=learner.governedAttempts!!.filter{it.unitNumber!=3})).allowed)
            val invalid=p.copy(units=p.units.map{if(it.number==3)it.copy(advancement=it.advancement!!.copy(requireDueReview=false))else it})
            assertFalse(advancementDecision(invalid,invalid.unit(3)!!,learner).allowed)
            assertFalse(V2Validator.validate(invalid).ok)
            assertTrue(V2Validator.validate(p).errors.joinToString("\n"),V2Validator.validate(p).ok)
            assertTrue(advance(p, s))
            assertEquals(4, s.v2Profile().currentUnitNumber)
            assertEquals(setOf(1, 2, 3), currentCompletedUnitNumbers(p, s.v2LearnerView()))
            val row = s.governedLearnerDocument().unitCompletions.single { it.unitNumber == 3 }
            assertFalse(row.preReviewActivation); assertEquals(a.id, row.reviewAssignmentId)
            assertTrue(row.reviewCompletionOrder > a.boundary)
        }
    }

    @Test fun optionalAndPrescribedReplaysCannotCompleteReviewOrBypassTransactionalAdvance() {
        val p = pack()
        ready(p).use { s ->
            val a = s.openUnit3CumulativeReview(p, s.learnerMutationEpoch())!!
            for (slot in a.slots) {
                val i = p.item(slot.itemId)!!
                s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, i.id, i.answer, OPTIONAL_PRACTICE, practiceStepId("mixed"), "replay")!!)
            }
            assertFalse(advancementDecision(p, p.unit(3)!!, s.v2LearnerView()).allowed)
            assertTrue(runCatching { advance(p, s) }.isFailure)
            answer(p, s, a, 0); answer(p, s, a, 1)
            val captured = s.v2LearnerView(); assertTrue(advancementDecision(p, p.unit(3)!!, captured).allowed)
            val item = p.item(a.slots[1].itemId)!!
            assertTrue(s.answerCumulativeReview(p, a.id, 1, item.options.first { it != item.answer }, s.learnerMutationEpoch()))
            assertTrue(runCatching { advance(p, s) }.isFailure)
            assertEquals(3, s.v2Profile().currentUnitNumber)
            assertFalse(3 in s.v2LearnerView().completedUnitNumbers)
        }
    }

    @Test fun completedHistoryKeepsItsAcceptedAnswerSnapshotAndTransactionFailureRollsBack() {
        val p = pack()
        ready(p).use { s ->
            val a = s.openUnit3CumulativeReview(p, s.learnerMutationEpoch())!!
            answer(p, s, a, 0); answer(p, s, a, 1)
            s.failAdvanceAfterCompletionForTests = true
            assertTrue(runCatching { advance(p, s) }.isFailure)
            assertFalse(3 in s.v2LearnerView().completedUnitNumbers); assertEquals(3, s.v2Profile().currentUnitNumber)
            assertTrue(advance(p, s))
            val item = p.item(a.slots[0].itemId)!!
            s.answerCumulativeReview(p, a.id, 0, item.options.first { it != item.answer }, s.learnerMutationEpoch())
            assertFalse(unit3ReviewComplete(p, s.v2LearnerView()))
            assertTrue(3 in currentCompletedUnitNumbers(p, s.v2LearnerView()))
        }
    }

    @Test fun migratedVersionTenRetainsGenuineOldLessonCreditWithoutFabricatingReviewCompletion() {
        val p = pack(); val old = legacyPack(); val name = "review-activation-${System.nanoTime()}.db"
        val schema = JSONObject(File(root(), "app/schemas/dev.russiancompanion.data.local.AppDatabase/10.json").readText()).getJSONObject("database")
        val document = ready(old).use { s -> s.v2CompleteUnit(3, "historical-completion"); s.governedLearnerDocument() }
        val helper = FrameworkSQLiteOpenHelperFactory().create(SupportSQLiteOpenHelper.Configuration.builder(context()).name(name)
            .callback(object : SupportSQLiteOpenHelper.Callback(10) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    val entities = schema.getJSONArray("entities")
                    for (n in 0 until entities.length()) {
                        val e = entities.getJSONObject(n); val table = e.getString("tableName")
                        db.execSQL(e.getString("createSql").replace("\${TABLE_NAME}", table))
                        val indices = e.optJSONArray("indices") ?: continue
                        for (j in 0 until indices.length()) db.execSQL(indices.getJSONObject(j).getString("createSql").replace("\${TABLE_NAME}", table))
                    }
                    val setup = schema.getJSONArray("setupQueries"); for (n in 0 until setup.length()) db.execSQL(setup.getString(n))
                    val pr = document.profile
                    db.execSQL("INSERT INTO v2_profiles VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)", arrayOf<Any?>(pr.id,1,4,pr.startingPoint,1,0,1,0,15,1,null,0,"",0))
                    document.steps.forEach { db.execSQL("INSERT INTO v2_step_progress VALUES (?,?,?,?)", arrayOf<Any?>(it.stepId,it.completedAtLocal,it.contentDigest,it.identitySchema)) }
                    document.attempts.forEach { db.execSQL("INSERT INTO v2_attempts VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)", arrayOf<Any?>(it.id,it.itemId,it.selected,if(it.correct)1 else 0,it.recordedAtLocal,it.itemDigest,it.unitContentDigest,it.ruleDigest,it.identitySchema,it.attemptRole,it.objectiveId,it.unitNumber,it.stepId,it.acceptedOrder)) }
                    document.bookAttestations.forEach { db.execSQL("INSERT INTO v2_book_attestations VALUES (?,?)", arrayOf<Any?>(it.unitNumber,it.recordedAtLocal)) }
                    document.unitCompletions.forEach { db.execSQL("INSERT INTO v2_unit_completions VALUES (?,?)", arrayOf<Any?>(it.unitNumber,it.recordedAtLocal)) }
                }
                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
            }).build())
        try {
            helper.writableDatabase; helper.close()
            LocalStore.openNamed(context(), name).use { s ->
                assertEquals(document.attempts.map { it.id }, s.v2Attempts().map { it.id })
                assertEquals(setOf(1, 2, 3), currentCompletedUnitNumbers(p, s.v2LearnerView()))
                assertEquals(V2UnitState.CURRENT, unitState(p, p.unit(4)!!, s.v2LearnerView()))
                assertTrue(s.governedLearnerDocument().unitCompletions.all { it.preReviewActivation })
                assertTrue(s.cumulativeReviewHistory().first.isEmpty())
                assertFalse(unit3ReviewComplete(p, s.v2LearnerView()))
                val changed = p.copy(items = p.items.map { if (it.objectiveId == "obj-u3-gender") it.copy(feedback = it.feedback + " changed") else it })
                assertFalse(3 in currentCompletedUnitNumbers(changed, s.v2LearnerView()))
            }
        } finally { helper.close(); context().deleteDatabase(name) }
    }

    @Test fun versionThreeBackupPreservesCompletionSnapshotAndRejectsForgedActivationLinks() {
        val p = pack()
        ready(p).use { s ->
            val a = s.openUnit3CumulativeReview(p, s.learnerMutationEpoch())!!; answer(p,s,a,0); answer(p,s,a,1); advance(p,s)
            val d = s.governedLearnerDocument(); val bytes = ByteArrayOutputStream()
            writeGovernedBackup(bytes,"dev.russiancompanion",p.packId,p.packVersion,"DARK",d,emptyList())
            val temp = File(context().cacheDir,"review-gate-backup-${System.nanoTime()}")
            try {
                val read = readGovernedBackup(bytes.toByteArray().inputStream(),"dev.russiancompanion",p.packId,p.packVersion,temp)
                assertTrue(read is BackupReadResult.Valid)
                LocalStore.openInMemory(context()).use { target ->
                    assertEquals(GovernedReplaceOutcome.REPLACED,target.replaceGovernedLearner((read as BackupReadResult.Valid).backup.document,emptyList()))
                    assertEquals(setOf(1,2,3),currentCompletedUnitNumbers(p,target.v2LearnerView()))
                    val before = target.governedLearnerFingerprint()
                    val forged = d.copy(unitCompletions=d.unitCompletions.map { if(it.unitNumber==3) it.copy(reviewAssignmentId="missing") else it })
                    assertEquals(GovernedReplaceOutcome.UNCHANGED,target.replaceGovernedLearner(forged,emptyList()))
                    assertEquals(before,target.governedLearnerFingerprint())
                    val fakePre=d.copy(reviewAssignments=emptyList(),reviewAnswers=emptyList(),unitCompletions=d.unitCompletions.map {
                        if(it.unitNumber==3) it.copy(preReviewActivation=true,reviewPolicyDigest="",reviewAssignmentId="",reviewCompletionOrder=0) else it })
                    assertEquals(GovernedReplaceOutcome.REPLACED,target.replaceGovernedLearner(fakePre,emptyList()))
                    assertFalse(3 in currentCompletedUnitNumbers(p,target.v2LearnerView()))
                }
            } finally { temp.deleteRecursively() }
        }
    }

    @Test fun actualContinueReviewAndGateUiRequireBothChoicesAndShowTargetedFeedback() {
        val p = pack()
        ready(p).use { s ->
            compose.setContent { V2App(store=s,pack=p,onRetryOpen={}) }
            waitTag("v2-continue"); compose.onNodeWithTag("v2-continue").performClick()
            waitTag("v2-review-start"); compose.onNodeWithTag("v2-review-start").performScrollTo().performClick()
            waitTag("v2-review-count")
            val a=s.cumulativeReviewHistory().first.single(); val first=p.item(a.slots[0].itemId)!!
            compose.onNodeWithTag("v2-review-option-0-${first.options.first { it != first.answer }}").performScrollTo().performClick()
            waitTag("v2-review-feedback-0"); compose.onNodeWithTag("v2-review-feedback-0").assertTextEquals("Not the reviewed answer. ${first.feedback}")
            compose.onNodeWithTag("v2-review-remediate-0").performScrollTo().performClick()
            waitTag("v2-review-practice"); compose.onNodeWithTag("v2-review-practice").performClick()
            waitTag("v2-return-to-check"); compose.onNodeWithTag("v2-return-to-check").assertIsDisplayed().performClick()
            waitTag("v2-review-count"); assertEquals(a, s.cumulativeReviewHistory().first.single())
            compose.onNodeWithTag("v2-review-option-0-${first.answer}").performScrollTo().performClick()
            compose.waitUntil(30000) { s.cumulativeReviewHistory().second.lastOrNull()?.selected == first.answer }
            val second=p.item(a.slots[1].itemId)!!
            compose.onNodeWithTag("v2-review-option-1-${second.answer}").performScrollTo().performClick()
            waitTag("v2-review-gate"); compose.onNodeWithTag("v2-review-gate").performScrollTo().performClick()
            waitTag("v2-advance"); compose.onNodeWithTag("v2-advance").performScrollTo().assertIsEnabled().performClick()
            compose.waitUntil(30000) { s.v2Profile().currentUnitNumber==4 }
        }
    }

    @Test fun placementOnlyUiOffersRealEarlierTeachingAndNoEmptyReviewCompletion() {
        val p=pack()
        LocalStore.openInMemory(context()).use { s ->
            s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted=true,currentUnitNumber=3,startingPoint="RESUME_FROM_BOOK"))
            compose.setContent { V2App(store=s,pack=p,onRetryOpen={}) }
            compose.onNodeWithText("Course").performClick()
            // Review route is reached through the current unit's detail.
            waitTag("v2-unit-row-3"); compose.onNodeWithTag("v2-unit-row-3").performScrollTo().performClick()
            waitTag("v2-open-cumulative-review"); compose.onNodeWithTag("v2-open-cumulative-review").performScrollTo().performClick()
            waitTag("v2-review-insufficient"); compose.onNodeWithTag("v2-review-start").assertDoesNotExist()
            assertTrue(s.cumulativeReviewHistory().first.isEmpty())
            compose.onNodeWithTag("v2-review-repair").performScrollTo().performClick()
            waitTag("v2-unit-heading"); compose.onNodeWithText("Unit 1").assertIsDisplayed()
        }
    }

    @Test fun asynchronousReviewWriteCannotCrossActualLearnerReplacementAndBackRemainsResponsive() {
        val p=pack()
        ready(p).use { s ->
            val entered=CompletableDeferred<Unit>(); val release=CompletableDeferred<Unit>()
            val api=object:V2Persistence by StoreV2Persistence(s,p) {
                override suspend fun openCumulativeReview(pack:V2CurriculumPack) {
                    entered.complete(Unit); release.await(); StoreV2Persistence(s,p).openCumulativeReview(pack)
                }
            }
            V2UiWork.blockForTests=false
            compose.setContent { V2App(store=s,pack=p,onRetryOpen={},persistence=api) }
            waitTag("v2-continue"); compose.onNodeWithTag("v2-continue").performClick()
            waitTag("v2-review-start"); compose.onNodeWithTag("v2-review-start").performScrollTo().performClick()
            compose.waitUntil(30000) { compose.waitForIdle(); entered.isCompleted }
            compose.onNodeWithTag("v2-back").performClick(); waitTag("v2-continue")
            val replacement=s.governedLearnerDocument().copy(profile=s.governedLearnerDocument().profile.copy(currentUnitNumber=1),
                steps=emptyList(),attempts=emptyList(),unitCompletions=emptyList(),bookAttestations=emptyList())
            s.openLearnerReplacement()
            try { assertEquals(GovernedReplaceOutcome.REPLACED,s.replaceGovernedLearner(replacement,emptyList())) }
            finally { s.closeLearnerReplacement() }
            val fingerprint=s.governedLearnerFingerprint(); release.complete(Unit)
            compose.waitUntil(30000) { compose.onAllNodesWithText("Not saved. The previous record is unchanged.",substring=true).fetchSemanticsNodes().isNotEmpty() }
            assertEquals(fingerprint,s.governedLearnerFingerprint()); assertTrue(s.cumulativeReviewHistory().first.isEmpty())
        }
    }

    private fun waitTag(tag:String) = compose.waitUntil(conditionDescription = tag, timeoutMillis = 30000) { compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty() }
    private fun ready(p:V2CurriculumPack)=LocalStore.openInMemory(context()).also { s ->
        for(n in 1..3) {
            s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted=true,currentUnitNumber=n,currentSessionId=null))
            for(step in p.unit(n)!!.sessions.flatMap { it.steps }) {
                step.itemIds.forEach { id -> s.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,p.item(id)!!.answer,PRESCRIBED_CHECK,step.id,"same-clock")!!) }
                s.v2CompleteGovernedStep(step.id,governedStepCompletionDigest(p,s.v2LearnerView(),step.id)!!,"same-clock")
            }
            s.v2AttestBook(n,"same-clock"); if(n<3) s.v2CompleteUnit(n,"same-clock")
        }
    }
    private fun answer(p:V2CurriculumPack,s:LocalStore,a:CumulativeReviewAssignment,index:Int) {
        assertTrue(s.answerCumulativeReview(p,a.id,index,p.item(a.slots[index].itemId)!!.answer,s.learnerMutationEpoch()))
    }
    private fun advance(p:V2CurriculumPack,s:LocalStore):Boolean = runBlocking {
        val session=p.unit(4)!!.sessions.first().id
        s.v2AdvanceUnitIfCurrentAsync(s.learnerMutationEpoch(),3,"advance",4,session,resumePositionDigest(p,session,0)!!,GOVERNED_IDENTITY_SCHEMA,p)
    }
    private fun context():Context=ApplicationProvider.getApplicationContext()
    private fun root()=File(if(File("app").isDirectory)"." else "..")
    private fun pack()=sharedPack
    private fun legacyPack():V2CurriculumPack {
        val p=ProcessBuilder("git","show","20a227817562e3a8645488fd52db2e7589713a7a:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
        val result=V2PackLoader.parse(p.inputStream.bufferedReader().readText());assertEquals(0,p.waitFor());V2PackIdentityIndex.warm(result);return result
    }
    companion object { private val sharedPack by lazy {
        val root=File(if(File("app").isDirectory)"." else "..")
        V2PackLoader.parse(File(root,"app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) }
    } }
}
