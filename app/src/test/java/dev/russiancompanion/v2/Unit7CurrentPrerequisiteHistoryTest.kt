package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.RussianCompanionApplication
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit7CurrentPrerequisiteHistoryTest {
    @Test fun staleUnit4PrerequisiteRefusesUnit7ReviewUntilFreshRepairWithoutRelabellingHistory() {
        // Original contract on actual pre-literacy source; current refusal has separate direct coverage.
        with(RecordedLiteracyEpochScope.context(verifiedCurrentPack(), 7)) {
        val root = File(if (File("app").isDirectory) "." else "..")
        val current = verifiedCurrentPack()
        val before = sourcePack("b5e1daf50ac8597d00aeb6976f894d3ce44c03ca")
        val after = sourcePack("e6db0bdfe0bbb40edf190623e2123014946f305b")
        assertEquals(after.unit(4), current.unit(4))
        assertEquals(V2Validator.unitContentReviewDigest(after, 4), V2Validator.unitContentReviewDigest(current, 4))
        val original = backupJson.decodeFromString(GovernedLearnerDocument.serializer(),
            File(root, "audit/independent-gpt61-2026-10-12/ia003-unit7-agreement-teaching/COMPLETED_UNIT8.json").readText())
        val context: Context = ApplicationProvider.getApplicationContext()
        (context.applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
        LocalStore.openInMemory(context).use { store ->
            store.openLearnerReplacement()
            try { assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(original, emptyList())) }
            finally { store.closeLearnerReplacement() }
            assertEquals((1..6).toSet(), currentCompletedUnitNumbers(before, store.v2LearnerView()))
            assertEquals((1..3).toSet(), currentCompletedUnitNumbers(after, store.v2LearnerView()))
            assertEquals((1..3).toSet(), currentCompletedUnitNumbers(current, store.v2LearnerView()))
            assertEquals(original, store.governedLearnerDocument())
            ordinary(current,store,7)
            assertTrue(ordinaryUnitRequirements(current,current.unit(7)!!,store.v2LearnerView()).allowed)
            assertEquals(listOf(6),unmetCurrentPrerequisites(current,current.unit(7)!!,store.v2LearnerView()))
            val beforeRefusal=store.governedLearnerDocument()
            assertNull(store.openCumulativeReview(current,7,store.learnerMutationEpoch()))
            assertEquals(beforeRefusal,store.governedLearnerDocument())
            for (number in 4..8) {
                ordinary(current,store,number)
                val assignment=store.openCumulativeReview(current,number,store.learnerMutationEpoch())
                assertNotNull("Review unavailable for Unit $number: ${advancementDecision(current,current.unit(number)!!,store.v2LearnerView()).remaining}",assignment)
                val a=assignment!!
                assertEquals(a,store.openCumulativeReview(current,number,store.learnerMutationEpoch()))
                a.slots.indices.forEach { index -> assertTrue(store.answerCumulativeReview(current,a.id,index,current.item(a.slots[index].itemId)!!.answer,store.learnerMutationEpoch())) }
                assertTrue(runBlocking {
                    val next=current.unit(number+1)!!.sessions.first().id
                    store.v2AdvanceUnitIfCurrentAsync(store.learnerMutationEpoch(),number,"fresh-prerequisite",number+1,next,resumePositionDigest(current,next,0)!!,GOVERNED_IDENTITY_SCHEMA,current)
                })
                // Fresh Unit7 ordinary work above plus its unchanged retained review becomes eligible once Unit6 is repaired.
                assertEquals((1..if(number==6) 7 else number).toSet(),currentCompletedUnitNumbers(current,store.v2LearnerView()))
                val document=store.governedLearnerDocument()
                assertEquals(original.attempts,document.attempts.take(original.attempts.size))
                assertTrue(document.reviewAssignments.containsAll(original.reviewAssignments))
                assertTrue(document.reviewAnswers.containsAll(original.reviewAnswers))
                if (number == 7) assertEquals(original.reviewAssignments.single { it.targetUnit == 7 }, a)
                if (number == 8) {
                    assertFalse(a in original.reviewAssignments)
                    assertEquals(V2Validator.unitContentReviewDigest(current,7),a.slots[1].unitDigest)
                }
            }
        }
        }
    }
    private fun ordinary(p:V2CurriculumPack,s:LocalStore,number:Int) {
        s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted=true,currentUnitNumber=number,currentSessionId=null))
        p.unit(number)!!.sessions.flatMap { it.steps }.forEach { step ->
            step.itemIds.forEach { id -> s.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,p.item(id)!!.answer,PRESCRIBED_CHECK,step.id,"fresh-prerequisite")!!) }
            s.v2CompleteGovernedStep(step.id,governedStepCompletionDigest(p,s.v2LearnerView(),step.id)!!,"fresh-prerequisite")
        }
        s.v2AttestBook(number,"fresh-prerequisite")
    }
    @Test fun allEightOriginalBackupsReopenUnchangedButCurrentCreditStopsAtStaleUnit4() {
        // Original contract on actual pre-literacy source; current refusal has separate direct coverage.
        with(RecordedLiteracyEpochScope.context(verifiedCurrentPack(), 7)) {
        val p=verifiedCurrentPack()
        val before=sourcePack("b5e1daf50ac8597d00aeb6976f894d3ce44c03ca")
        val context:Context=ApplicationProvider.getApplicationContext()
        (context.applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
        val root=File(if(File("app").isDirectory) "." else "..")
        val evidence=File(root,"audit/independent-gpt61-2026-10-12/ia003-unit7-agreement-teaching")
        val labels=listOf("BEFORE_UNIT7_WORK","ORDINARY_UNIT7","UNSTARTED_REVIEW","PARTIAL_REVIEW","ANSWERED_REVIEW","COMPLETED_UNIT7","POST_COMPLETION_WRONG","COMPLETED_UNIT8")
        for(label in labels) {
            val original=backupJson.decodeFromString(GovernedLearnerDocument.serializer(),File(evidence,"$label.json").readText())
            val staging=File(context.cacheDir,"u7-prerequisite-$label-${System.nanoTime()}")
            val decoded=readGovernedBackup(File(evidence,"$label.zip").inputStream(),"dev.russiancompanion",p.packId,p.packVersion,staging)
            assertTrue(label,decoded is BackupReadResult.Valid)
            assertEquals(original,(decoded as BackupReadResult.Valid).backup.document)
            val name="u7-prerequisite-$label-${System.nanoTime()}.db"
            fun check(store:LocalStore) {
                assertEquals(label,original,store.governedLearnerDocument())
                assertEquals((1..6).toSet(),currentCompletedUnitNumbers(before,store.v2LearnerView()))
                assertEquals((1..3).toSet(),currentCompletedUnitNumbers(p,store.v2LearnerView()))
                assertFalse(ordinaryUnitRequirements(p,p.unit(4)!!,store.v2LearnerView()).allowed)
                assertEquals(listOf(6),unmetCurrentPrerequisites(p,p.unit(7)!!,store.v2LearnerView()))
                assertNull(store.openCumulativeReview(p,7,store.learnerMutationEpoch()))
                assertEquals(label,original,store.governedLearnerDocument())
            }
            try {
                LocalStore.openNamed(context,name).use { store ->
                    store.openLearnerReplacement()
                    try { assertEquals(GovernedReplaceOutcome.REPLACED,store.replaceGovernedLearner(original,emptyList())) }
                    finally { store.closeLearnerReplacement() }
                    check(store)
                }
                LocalStore.openNamed(context,name).use { check(it) }
            } finally {
                staging.deleteRecursively();context.deleteDatabase(name)
                File(context.filesDir,"recordings-$name").deleteRecursively()
            }
        }
        }
    }
    companion object {
        private val sources=mutableMapOf<String,V2CurriculumPack>()
        private fun sourcePack(commit:String)=sources.getOrPut(commit) {
            val root=File(if(File("app").isDirectory) "." else "..")
            val command=ProcessBuilder("git","show","$commit:app/src/main/assets/content/v2-curriculum.json").directory(root).start()
            V2PackLoader.parse(command.inputStream.bufferedReader().readText()).also {
                assertEquals(0,command.waitFor());V2PackIdentityIndex.warm(it)
            }
        }
        private val current by lazy {
            val root=File(if(File("app").isDirectory) "." else "..")
            V2PackLoader.parse(File(root,"app/src/main/assets/content/v2-curriculum.json").readText()).also {
                V2PackIdentityIndex.warm(it)
                // The original equality records the pre-Unit5/6 declaration scope only.
                RecordedCurriculumConservationScope.beforeUnit5GrammarReview(root,it).let { it ->
                    assertEquals("Current pack must retain the exact latest asset implementation",sourcePack("b7e53230e2119b1e57d781b1e1b955f074a38601"),it)
                }
                // Return the actual current asset; restore/reopen/credit checks below stay current.
                RecordedCurriculumConservationScope.assertCurrent(root,it)
                val unit4=sourcePack("e6db0bdfe0bbb40edf190623e2123014946f305b")
                assertEquals(unit4.unit(4),it.unit(4))
                assertEquals(V2Validator.unitContentReviewDigest(unit4,4),V2Validator.unitContentReviewDigest(it,4))
            }
        }
        internal fun verifiedCurrentPack():V2CurriculumPack=current
    }

}
