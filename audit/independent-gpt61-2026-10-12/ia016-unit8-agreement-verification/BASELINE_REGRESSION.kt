package dev.russiancompanion.v2
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
@RunWith(RobolectricTestRunner::class)
@Config(sdk=[33])
class Unit8AgreementTransferTest {
    @Test fun completedAgreementNeedsADifferentIndependentAdjective() {
        val p=pack();val o=p.unit(8)!!.objectives.single { it.id=="obj-u8-agreement" }
        LocalStore.openInMemory(context()).use { store ->
            for(id in o.practiceStepIds.flatMap { p.step(it)!!.second.itemIds }+o.checkItemIds) {
                val step=p.unit(8)!!.sessions.flatMap { it.steps }.first { id in it.itemIds }
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,p.item(id)!!.answer,PRESCRIBED_CHECK,step.id,"2026-10-13T20:00")!!)
            }
            assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),p))
            val guided=o.practiceStepIds.flatMap { p.step(it)!!.second.itemIds }.map { p.item(it)!!.promptLetter }
            assertTrue("Independent adjective evidence must use a different supplied adjective, not repeat the guided form pair",
                p.item("u8-i-agreement-3")!!.promptLetter !in guided)
        }
    }
    private fun context():Context=ApplicationProvider.getApplicationContext()
    private fun root()=File(if(File("app").isDirectory)"." else "..")
    private fun pack()=V2PackLoader.parse(File(root(),"app/src/main/assets/content/v2-curriculum.json").readText())
}
