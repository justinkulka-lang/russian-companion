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
@Config(sdk = [33])
class Unit29PunctuationContextRegressionTest {
    @Test fun categoryAndConnectiveCreditMustNotDemonstrateContextualPunctuation() {
        val root = File(if (File("app").isDirectory) "." else "..")
        val p = V2PackLoader.parse(File(root,"app/src/main/assets/content/v2-curriculum.json").readText())
        val o = p.unit(29)!!.objectives.single { it.id == "obj-u29-punctuation" }
        LocalStore.openInMemory(ApplicationProvider.getApplicationContext<Context>()).use { store ->
            val ids = o.practiceStepIds.flatMap { p.step(it)!!.second.itemIds } + o.checkItemIds
            for (id in ids) {
                val step = p.unit(29)!!.sessions.flatMap { it.steps }.first { id in it.itemIds }
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,p.item(id)!!.answer,PRESCRIBED_CHECK,step.id,"2026-10-13T16:00")!!)
            }
            assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),p))
            assertTrue("A completed punctuation objective must require a complete construction and comma decision, not a grammatical category cue and source connective",
                o.checkItemIds.none { p.item(it)!!.promptLetter == "личным местоимением" || p.item(it)!!.answer == "кроме тех случаев" })
        }
    }
}
