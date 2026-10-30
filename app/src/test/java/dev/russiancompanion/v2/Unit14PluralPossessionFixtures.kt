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

internal object Unit14PluralPossessionFixtures {
    val root = File(if (File("app").isDirectory) "." else "..")
    val before14 by lazy { RecordedIntakeCorrectionScope.load(root, "9101af8c182b3415426ed9226cb211a707a2c393") }
    val pack by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) } }
    fun context(): Context = ApplicationProvider.getApplicationContext<Context>().also {
        (it.applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
    }
    fun prepare(store: LocalStore) {
        // Genuine fresh API history for the actual current pack, memoized only within this JVM.
        val fresh = FreshCurrentPrerequisiteHistory.document(pack, 14, context())
        assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(fresh, emptyList()))
        ordinary(store, 14)
        assertEquals((1..13).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
        assertTrue(ordinaryUnitRequirements(pack, pack.unit(14)!!, store.v2LearnerView()).allowed)
    }
    fun ordinary(store: LocalStore, number: Int, p: V2CurriculumPack = pack) {
        for (step in p.unit(number)!!.sessions.flatMap { it.steps }) {
            step.itemIds.forEach { id -> store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id,
                p.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "unit14-fixture")!!) }
            store.v2CompleteGovernedStep(step.id,
                governedStepCompletionDigest(p, store.v2LearnerView(), step.id)!!, "unit14-fixture")
        }
        store.v2AttestBook(number, "unit14-fixture")
    }
    fun open(store: LocalStore, p: V2CurriculumPack = pack, number: Int = 14) =
        store.openCumulativeReview(p, number, store.learnerMutationEpoch())!!
    fun answer(store: LocalStore, task: CumulativeReviewAssignment, index: Int, correct: Boolean = true, p: V2CurriculumPack = pack) {
        val item = p.item(task.slots[index].itemId)!!
        assertTrue(store.answerCumulativeReview(p, task.id, index,
            if (correct) item.answer else item.options.first { it != item.answer }, store.learnerMutationEpoch()))
    }
    fun advance(store: LocalStore, number: Int = 14, p: V2CurriculumPack = pack): Boolean = runBlocking {
        val next = p.unit(number + 1)!!.sessions.first().id
        store.v2AdvanceUnitIfCurrentAsync(store.learnerMutationEpoch(), number, "unit14-fixture", number + 1,
            next, resumePositionDigest(p, next, 0)!!, GOVERNED_IDENTITY_SCHEMA, p)
    }
    fun archive(store: LocalStore): ByteArray = ByteArrayOutputStream().also {
        writeGovernedBackup(it, "dev.russiancompanion", pack.packId, pack.packVersion, "DARK", store.governedLearnerDocument(), emptyList())
    }.toByteArray()
    fun document(path: String): GovernedLearnerDocument {
        val stage = File(context().cacheDir, "unit14-read-${System.nanoTime()}")
        try {
            val read = File(root, path).inputStream().use { readGovernedBackup(it, "dev.russiancompanion", pack.packId, pack.packVersion, stage) }
            assertTrue(read is BackupReadResult.Valid)
            return (read as BackupReadResult.Valid).backup.document
        } finally { stage.deleteRecursively() }
    }
    fun miss(store: LocalStore, id: String) {
        val item = pack.item(id)!!
        val step = pack.units.flatMap { it.sessions }.flatMap { it.steps }.single { it.kind == "CHECK" && id in it.itemIds }
        // Same-answer writes are intentionally idempotent. A real correction followed by a miss
        // creates a new accepted history event even when an allocation variant revisits this item.
        store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack, id, item.answer,
            PRESCRIBED_CHECK, step.id, "unit14-correct-${System.nanoTime()}")!!)
        store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack, id, item.options.first { it != item.answer },
            PRESCRIBED_CHECK, step.id, "unit14-miss-${System.nanoTime()}")!!)
    }
    fun deleteStore(context: Context, name: String) {
        context.deleteDatabase(name); File(context.filesDir, "recordings-$name").deleteRecursively()
    }
}
