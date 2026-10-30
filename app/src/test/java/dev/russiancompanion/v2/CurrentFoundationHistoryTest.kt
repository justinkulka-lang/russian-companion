package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.RussianCompanionApplication
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.security.MessageDigest
import java.util.zip.ZipFile

/** Actual current-source controls alongside separately scoped historical contracts. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class CurrentFoundationHistoryTest {

    @Test fun genuineFreshCurrentPrerequisitesReachUnitFourteenWithCurrentOwnerBindings() {
        val context = context(); val document = FreshCurrentPrerequisiteHistory.document(current, 14, context)
        LocalStore.openInMemory(context).use { store ->
            assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(document, emptyList()))
            assertEquals((1..13).toSet(), currentCompletedUnitNumbers(current, store.v2LearnerView()))
            document.reviewAssignments.forEach { a ->
                assertTrue(reviewAssignmentCurrent(current, a))
                a.slots.forEach { slot -> assertEquals(V2Validator.unitContentReviewDigest(current, slot.ownerUnit), slot.unitDigest) }
            }
            FreshCurrentPrerequisiteHistory.ordinary(store, current, 14)
            assertTrue(ordinaryUnitRequirements(current, current.unit(14)!!, store.v2LearnerView()).allowed)
            val task = store.openCumulativeReview(current, 14, store.learnerMutationEpoch())!!
            assertEquals(16, task.schema)
            assertTrue(reviewAssignmentCurrent(current, task))
        }
    }

    @Test fun historicalEpochProofCannotAcceptUnrelatedCurrentTeachingChanges() {
        val before = RecordedLiteracyEpochScope.beforeCorrection(root, current)
        val changed = current.copy(units = current.units.map { u -> if (u.number != 4) u else
            u.copy(sessions = u.sessions.map { session -> session.copy(steps = session.steps.map { step ->
                if (step.kind == "TEACH") step.copy(body = step.body + " changed") else step
            }) }) })
        assertTrue(runCatching { RecordedLiteracyEpochScope.beforeCorrection(root, changed) }.exceptionOrNull() is AssertionError)
        assertTrue(runCatching { RecordedCurriculumConservationScope.assertCurrent(root, before) }.exceptionOrNull() is AssertionError)
    }
    private fun context(): Context = ApplicationProvider.getApplicationContext<Context>().also {
        (it.applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
    }
    private fun hash(bytes: ByteArray) = MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
    companion object {
        private val root = File(if (File("app").isDirectory) "." else "..")
        private val current by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) } }
    }
}
