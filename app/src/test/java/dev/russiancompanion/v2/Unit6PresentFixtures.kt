package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.RussianCompanionApplication
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import java.io.ByteArrayOutputStream
import java.io.File

internal object Unit6PresentFixtures {
    val root = File(if (File("app").isDirectory) "." else "..")
    val pack by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) } }
    fun context(): Context = ApplicationProvider.getApplicationContext<Context>().also {
        (it.applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
    }
    fun prepare(store: LocalStore, through: Int = 6) {
        for (number in 1..through) {
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = number, currentSessionId = null))
            for (step in pack.unit(number)!!.sessions.flatMap { it.steps }) {
                step.itemIds.forEach { id -> store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack, id, pack.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "unit6-fixture")!!) }
                store.v2CompleteGovernedStep(step.id, governedStepCompletionDigest(pack, store.v2LearnerView(), step.id)!!, "unit6-fixture")
            }
            store.v2AttestBook(number, "unit6-fixture")
            if (number < 6) {
                if (number >= 3) {
                    val task = store.openCumulativeReview(pack, number, store.learnerMutationEpoch())!!
                    task.slots.indices.forEach { answer(store, task, it) }
                }
                assertTrue(advance(store, number))
            }
        }
    }
    fun open(store: LocalStore) = store.openCumulativeReview(pack, 6, store.learnerMutationEpoch())!!
    fun answer(store: LocalStore, task: CumulativeReviewAssignment, index: Int, correct: Boolean = true) {
        val item = pack.item(task.slots[index].itemId)!!
        assertTrue(store.answerCumulativeReview(pack, task.id, index, if (correct) item.answer else item.options.first { it != item.answer }, store.learnerMutationEpoch()))
    }
    fun advance(store: LocalStore, number: Int): Boolean = runBlocking {
        val next = pack.unit(number + 1)!!.sessions.first().id
        store.v2AdvanceUnitIfCurrentAsync(store.learnerMutationEpoch(), number, "unit6-fixture", number + 1, next, resumePositionDigest(pack, next, 0)!!, GOVERNED_IDENTITY_SCHEMA, pack)
    }
    fun archive(store: LocalStore): ByteArray = ByteArrayOutputStream().also {
        writeGovernedBackup(it, "dev.russiancompanion", pack.packId, pack.packVersion, "DARK", store.governedLearnerDocument(), emptyList())
    }.toByteArray()
    fun document(path: String): GovernedLearnerDocument {
        val stage = File(context().cacheDir, "unit6-read-${System.nanoTime()}")
        try {
            val decoded = File(root, path).inputStream().use { readGovernedBackup(it, "dev.russiancompanion", pack.packId, pack.packVersion, stage) }
            assertTrue(decoded is BackupReadResult.Valid)
            return (decoded as BackupReadResult.Valid).backup.document
        } finally { stage.deleteRecursively() }
    }
    const val v2Capture = "audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit6-present-baseline-2026-10-23/BASELINE_CURRENT_UNIT6_V2.zip"
}
