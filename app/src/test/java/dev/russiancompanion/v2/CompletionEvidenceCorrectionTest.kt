package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.io.ByteArrayOutputStream

@RunWith(RobolectricTestRunner::class)
@Config(sdk=[33])
class CompletionEvidenceCorrectionTest {
    @Test fun changedContentCannotKeepAnOldCompletionOrUnlockItsSuccessor() {
        val p=pack()
        LocalStore.openInMemory(context()).use { store ->
            completeEvidence(p,store,6)
            val learner=store.v2LearnerView()
            assertTrue(advancementDecision(p,p.unit(6)!!,learner).allowed)
            assertEquals(V2UnitState.COMPLETED,unitState(p,p.unit(6)!!,learner))
            assertEquals(V2UnitState.AVAILABLE,unitState(p,p.unit(7)!!,learner))
            val item=p.item("u6-i-feminine-1") ?: p.items.first { it.objectiveId in p.unit(6)!!.objectives.map { o->o.id } }
            val changed=p.copy(items=p.items.map { if(it.id==item.id) it.copy(answer=it.options.first { a->a!=it.answer }) else it })
            assertFalse(advancementDecision(changed,changed.unit(6)!!,learner).allowed)
            assertNotEquals("Historical completion is not current completion",V2UnitState.COMPLETED,unitState(changed,changed.unit(6)!!,learner))
            assertEquals(V2UnitState.LOCKED,unitState(changed,changed.unit(7)!!,learner))
            assertTrue("History must survive content changes",store.governedLearnerDocument().unitCompletions.any { it.unitNumber==6 })
        }
    }

    @Test fun hashCorrectRestoredUnsupportedCompletionIsHistoricalAndCannotUnlock() {
        val p=pack()
        LocalStore.openInMemory(context()).use { source ->
            source.v2CompleteUnit(6,"2026-10-12T19:00")
            val bytes=ByteArrayOutputStream().also { writeStoreBackup(source,it,"dev.russiancompanion",p.packId,p.packVersion,"DARK") }.toByteArray()
            val temp=File(context().cacheDir,"completion-restore-test").apply { mkdirs() }
            val read=readGovernedBackup(bytes.inputStream(),"dev.russiancompanion",p.packId,p.packVersion,temp)
            assertTrue("The archive hashes are valid; this is a semantic completion test",read is BackupReadResult.Valid)
            LocalStore.openInMemory(context()).use { restored ->
                assertEquals(GovernedReplaceOutcome.REPLACED,restoreStoreBackup(restored,(read as BackupReadResult.Valid).backup,temp))
                val learner=restored.v2LearnerView()
                assertTrue(6 in learner.completedUnitNumbers)
                assertNotEquals(V2UnitState.COMPLETED,unitState(p,p.unit(6)!!,learner))
                assertEquals(V2UnitState.LOCKED,unitState(p,p.unit(7)!!,learner))
            }
            temp.deleteRecursively()
        }
    }

    @Test fun changedEvidenceIsRecomputedAndLaterPlacementDoesNotUnlockFutureUnits() {
        val p=pack()
        LocalStore.openInMemory(context()).use { store ->
            completeEvidence(p,store,6)
            val first=store.v2LearnerView()
            assertEquals(V2UnitState.LOCKED,unitState(p,p.unit(8)!!,first))
            val unit=p.unit(6)!!
            val step=unit.sessions.flatMap { it.steps }.first { it.kind=="CHECK" && it.itemIds.isNotEmpty() }
            val item=p.item(step.itemIds.first())!!
            val missingAttestation=first.copy(bookAttestedUnits=first.bookAttestedUnits - 6)
            assertEquals(V2UnitState.CURRENT,unitState(p,unit,missingAttestation))
            assertEquals(V2UnitState.LOCKED,unitState(p,p.unit(7)!!,missingAttestation))
            assertEquals(V2UnitState.COMPLETED,unitState(p,unit,first))
            // An already advanced profile still cannot bypass its now-stale claimed prerequisite.
            store.v2SaveProfile(store.v2Profile().copy(currentUnitNumber=7))
            val advanced=store.v2LearnerView()
            val changed=p.copy(items=p.items.map { if(it.id==item.id) it.copy(answer=it.options.first { a->a!=it.answer }) else it })
            assertEquals(V2UnitState.LOCKED,unitState(changed,changed.unit(7)!!,advanced))
            val repair=continueTarget(changed,advanced)
            assertEquals(6,repair.unitNumber)
            assertTrue(repair.opensUnitReview)
            assertNull(repair.sessionId)
        }
    }

    @Test fun unboundLegacyViewCannotTurnClaimedCompletionIntoCurrentEvidence() {
        val p=pack()
        LocalStore.openInMemory(context()).use { store ->
            completeEvidence(p,store,6)
            val current=store.v2LearnerView()
            assertEquals(V2UnitState.COMPLETED,unitState(p,p.unit(6)!!,current))
            val claimedPrerequisites=current.copy(governedAttempts=null,governedStepDigests=null,governedStepSchemas=null)
            assertEquals(V2UnitState.LOCKED,unitState(p,p.unit(6)!!,claimedPrerequisites))
            // Preserve the original resume-placement case: only Unit6 is claimed, without evidence.
            val unbound=claimedPrerequisites.copy(completedUnitNumbers=setOf(6))
            assertEquals(V2UnitState.CURRENT,unitState(p,p.unit(6)!!,unbound))
            assertEquals(V2UnitState.LOCKED,unitState(p,p.unit(7)!!,unbound))
        }
    }
    private fun completeEvidence(p:V2CurriculumPack,store:LocalStore,n:Int) {
        require(n in 1..29)
        // Genuine earlier-owner evidence is required by the current cumulative-review rules.
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted=true,hasPenguin=true,startingPoint="RESUME_FROM_BOOK"))
        for(number in 1..n) {
            val unit=p.unit(number)!!
            store.v2SaveProfile(store.v2Profile().copy(currentUnitNumber=number))
            unit.sessions.flatMap { it.steps }.forEach { step->
                store.v2CompleteGovernedStep(step.id,stepContentDigest(p,step),"2026-10-12T19:00")
                step.itemIds.forEach { id->
                    val item=p.item(id)!!
                    store.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,item.answer,PRESCRIBED_CHECK,step.id,"2026-10-12T19:00")!!)
                }
            }
            store.v2AttestBook(number,"2026-10-12T19:00")
            if(cumulativeReviewPolicy(number)!=null) {
                assertFalse(advancementDecision(p,unit,store.v2LearnerView()).allowed)
                val assignment=store.openCumulativeReview(p,number,store.learnerMutationEpoch())!!
                assignment.slots.indices.forEach { index->
                    assertTrue(store.answerCumulativeReview(p,assignment.id,index,p.item(assignment.slots[index].itemId)!!.answer,store.learnerMutationEpoch()))
                }
                assertTrue(advancementDecision(p,unit,store.v2LearnerView()).allowed)
            }
            val next=p.unit(number+1)!!.sessions.first().id
            assertTrue(runBlocking { store.v2AdvanceUnitIfCurrentAsync(store.learnerMutationEpoch(),number,"2026-10-12T19:00",number+1,next,
                resumePositionDigest(p,next,0)!!,GOVERNED_IDENTITY_SCHEMA,p) })
            if(cumulativeReviewPolicy(number)!=null) {
                val completion=store.governedLearnerDocument().unitCompletions.single { it.unitNumber==number }
                val assignment=store.cumulativeReviewHistory().first.single { it.id==completion.reviewAssignmentId }
                assertEquals(assignment.policyDigest,completion.reviewPolicyDigest)
            }
        }
        store.v2SaveProfile(store.v2Profile().copy(currentUnitNumber=n,currentSessionId=null,currentStepIndex=0))
        assertEquals((1..n).toSet(),currentCompletedUnitNumbers(p,store.v2LearnerView()))
    }
    private fun context():Context=ApplicationProvider.getApplicationContext()
    private fun pack()=V2PackLoader.parse(File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json").readText())
}
