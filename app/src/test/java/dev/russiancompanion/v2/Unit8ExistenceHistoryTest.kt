package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk=[33])
class Unit8ExistenceHistoryTest {
    @Test fun positiveObjectiveNeedsEachIndependentAnswerAndLabsUseCurrentItems() {
        val p=current();val o=p.unit(8)!!.objectives.single { it.id=="obj-u8-availability" }
        LocalStore.openInMemory(context()).use { s->
            val guide=p.step(o.practiceStepIds.single())!!.second.itemIds.single();record(p,s,guide,p.item(guide)!!.answer)
            for(id in o.checkItemIds.dropLast(1))record(p,s,id,p.item(id)!!.answer)
            assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,s.v2LearnerView(),p))
            val last=o.checkItemIds.last();record(p,s,last,p.item(last)!!.answer)
            assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,s.v2LearnerView(),p))
            val queue=practiceQueue(p,s.v2LearnerView().copy(currentUnitNumber=8),"grammar").filter { it.objectiveId==o.id }
            assertEquals((listOf(guide)+o.checkItemIds).toSet(),queue.map { it.id }.toSet())
            assertFalse(queue.any { Regex("genitive|absence|absent|нет",RegexOption.IGNORE_CASE).containsMatchIn(it.toString()) })
            assertFalse(advancementDecision(p,p.unit(8)!!,s.v2LearnerView()).allowed)
        }
    }
    private fun record(p:V2CurriculumPack,s:LocalStore,id:String,answer:String) {
        val step=p.unit(8)!!.sessions.flatMap { it.steps }.single { id in it.itemIds }
        s.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,answer,PRESCRIBED_CHECK,step.id,"2026-10-19T23:00:${s.v2Attempts().size}")!!)
    }
    private fun current()=V2PackLoader.parse(File(root(),"app/src/main/assets/content/v2-curriculum.json").readText())
    private fun root()=File(if(File("app").isDirectory)"." else "..")
    private fun context():Context=ApplicationProvider.getApplicationContext()
}
