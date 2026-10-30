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

internal object Unit12GenderFixtures {
    val root = File(if (File("app").isDirectory) "." else "..")
    val pack by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) } }
    fun context(): Context = ApplicationProvider.getApplicationContext<Context>().also {
        (it.applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
    }
    fun prepare(store: LocalStore, through: Int = 12) {
        for (number in 1..through) {
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = number, currentSessionId = null))
            ordinary(store, number)
            if (number < 12) {
                if (number >= 3) {
                    val task = store.openCumulativeReview(pack, number, store.learnerMutationEpoch())!!
                    task.slots.indices.forEach { answer(store, task, it) }
                }
                assertTrue(advance(store, number))
            }
        }
    }
    fun ordinary(store: LocalStore, number: Int, p: V2CurriculumPack = pack) {
        for (step in p.unit(number)!!.sessions.flatMap { it.steps }) {
            step.itemIds.forEach { id -> store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, p.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "unit12-fixture")!!) }
            store.v2CompleteGovernedStep(step.id, governedStepCompletionDigest(p, store.v2LearnerView(), step.id)!!, "unit12-fixture")
        }
        store.v2AttestBook(number, "unit12-fixture")
    }
    fun open(store: LocalStore) = store.openCumulativeReview(pack, 12, store.learnerMutationEpoch())!!
    fun answer(store: LocalStore, task: CumulativeReviewAssignment, index: Int, correct: Boolean = true) {
        val item = pack.item(task.slots[index].itemId)!!
        assertTrue(store.answerCumulativeReview(pack, task.id, index, if (correct) item.answer else item.options.first { it != item.answer }, store.learnerMutationEpoch()))
    }
    fun advance(store: LocalStore, number: Int, p: V2CurriculumPack = pack): Boolean = runBlocking {
        val next = p.unit(number + 1)!!.sessions.first().id
        store.v2AdvanceUnitIfCurrentAsync(store.learnerMutationEpoch(), number, "unit12-fixture", number + 1, next, resumePositionDigest(p, next, 0)!!, GOVERNED_IDENTITY_SCHEMA, p)
    }
    fun archive(store: LocalStore): ByteArray = ByteArrayOutputStream().also {
        writeGovernedBackup(it, "dev.russiancompanion", pack.packId, pack.packVersion, "DARK", store.governedLearnerDocument(), emptyList())
    }.toByteArray()
    fun document(path: String): GovernedLearnerDocument {
        val stage = File(context().cacheDir, "unit12-read-${System.nanoTime()}")
        try {
            val decoded = File(root, path).inputStream().use { readGovernedBackup(it, "dev.russiancompanion", pack.packId, pack.packVersion, stage) }
            assertTrue(decoded is BackupReadResult.Valid)
            return (decoded as BackupReadResult.Valid).backup.document
        } finally { stage.deleteRecursively() }
    }
}
