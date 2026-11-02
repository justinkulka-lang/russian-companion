package dev.russiancompanion.v2

import android.content.Context
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.RussianCompanionApplication
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.V2UnitScreen
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class CurrentReviewEntryLabelTest {
    @get:Rule val compose = createComposeRule()

    @Test fun unstartedReviewUsesCurrentLabelAndCallbackWithoutChangingLearnerState() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        (context.applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
        LocalStore.openInMemory(context).use { store ->
            // Explicit placement opens the current unit without importing or inventing prior work.
            store.v2SaveProfile(store.v2Profile().copy(
                onboardingCompleted = true,
                currentUnitNumber = 5,
                startingPoint = "RESUME_FROM_BOOK",
            ))
            val learner = store.v2LearnerView()
            assertEquals(V2UnitState.CURRENT, unitState(pack, pack.unit(5)!!, learner))
            assertNull(currentCumulativeAssignment(pack, learner, 5))
            assertEquals("unit5-prior-gender-subject-v2", cumulativeReviewDeclaredPolicy(pack, 5)!!.id)
            val before = store.governedLearnerDocument()
            val credits = currentCompletedUnitNumbers(pack, learner)
            val opened = AtomicInteger()

            compose.setContent {
                V2UnitScreen(
                    pack = pack,
                    learner = learner,
                    unitNumber = 5,
                    onBack = {},
                    onTab = {},
                    onOpenSession = {},
                    onAttestBook = {},
                    onAdvance = {},
                    onOpenCumulativeReview = { opened.incrementAndGet() },
                )
            }
            compose.onNodeWithTag("v2-open-cumulative-review")
                .performScrollTo()
                .assertTextContains("Review earlier noun genders and supplied subjects")
                .performClick()
            compose.runOnIdle {
                assertEquals(1, opened.get())
                assertEquals(before, store.governedLearnerDocument())
                assertEquals(credits, currentCompletedUnitNumbers(pack, store.v2LearnerView()))
                assertNull(currentCumulativeAssignment(pack, store.v2LearnerView(), 5))
            }
        }
    }

    companion object {
        private val root = File(if (File("app").isDirectory) "." else "..")
        private val pack by lazy {
            V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText())
                .also { V2PackIdentityIndex.warm(it) }
        }
    }
}
