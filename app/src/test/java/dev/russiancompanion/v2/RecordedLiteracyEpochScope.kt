package dev.russiancompanion.v2

import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import org.junit.Assert.*
import java.io.ByteArrayOutputStream
import java.io.File

/** Exact historical models for pre-literacy contracts; never a production eligibility exception. */
internal object RecordedLiteracyEpochScope {
    const val beforeCommit = "f653da03d1bc657e8acaeef52141552c531e42ce"
    const val latestCommit = "0107931bfee92fa932c93ae63d3f4a34080cb2bf"
    private val root = File(if (File("app").isDirectory) "." else "..")
    private val verified = java.util.Collections.newSetFromMap(java.util.IdentityHashMap<V2CurriculumPack, Boolean>())

    fun assertLatest(root: File, candidate: V2CurriculumPack) {
        assertTrue("Actual current pack must equal the entire recorded literacy implementation $latestCommit",
            candidate == RecordedCurriculumConservationScope.source(root, latestCommit))
    }
    fun beforeCorrection(root: File, candidate: V2CurriculumPack): V2CurriculumPack {
        assertLatest(root, candidate)
        val before = RecordedCurriculumConservationScope.source(root, beforeCommit)
        if (verified.add(candidate)) {
            assertEquals(before, candidate.copy(items = before.items, units = before.units))
            assertEquals(before.units.drop(2), candidate.units.drop(2))
            assertEquals(before.unit(1), candidate.unit(1))
            assertEquals(before.items.map { it.id to it.answer }, candidate.items.map { it.id to it.answer })
            for (n in 1..30) {
                val a = V2Validator.unitContentReviewDigest(before, n)
                val b = V2Validator.unitContentReviewDigest(candidate, n)
                if (n <= 2) assertNotEquals("Changed foundation $n", a, b) else assertEquals("Unchanged unit $n", a, b)
            }
        }
        return before // An actual Git source, never a synthetic pack made by swapping current fields.
    }
    fun policyEpoch(root: File, candidate: V2CurriculumPack): V2CurriculumPack {
        val before = RecordedCurriculumConservationScope.source(root, beforeCommit)
        if (candidate == before) return before
        return beforeCorrection(root, candidate)
    }
    fun assertKnownEpoch(root: File, candidate: V2CurriculumPack) { policyEpoch(root, candidate) }
    fun context(actualCurrent: V2CurriculumPack, target: Int) = Context(beforeCorrection(root, actualCurrent), target)

    /** Scoped receiver binds both model and all unqualified store helpers to the same historical source. */
    class Context(val pack: V2CurriculumPack, private val target: Int) {
        val current: V2CurriculumPack get() = pack
        fun pack(): V2CurriculumPack = pack
        fun verifiedCurrentPack(): V2CurriculumPack = pack
        init { println("HISTORICAL_LITERACY_SCOPE model=$beforeCommit target=$target; not current credit acceptance") }
        fun open(store: LocalStore, p: V2CurriculumPack = pack, number: Int = target) =
            store.openCumulativeReview(p, number, store.learnerMutationEpoch())!!
        fun ordinary(store: LocalStore, number: Int, p: V2CurriculumPack = pack) =
            FreshCurrentPrerequisiteHistory.ordinary(store, p, number)
        fun answer(store: LocalStore, task: CumulativeReviewAssignment, index: Int,
                   correct: Boolean = true, p: V2CurriculumPack = pack) {
            val item = p.item(task.slots[index].itemId)!!
            assertTrue(store.answerCumulativeReview(p, task.id, index,
                if (correct) item.answer else item.options.first { it != item.answer }, store.learnerMutationEpoch()))
        }
        fun advance(store: LocalStore, number: Int = target, p: V2CurriculumPack = pack): Boolean = runBlocking {
            val next = p.unit(number + 1)!!.sessions.first().id
            store.v2AdvanceUnitIfCurrentAsync(store.learnerMutationEpoch(), number, "historical-literacy-scope", number + 1,
                next, resumePositionDigest(p, next, 0)!!, GOVERNED_IDENTITY_SCHEMA, p)
        }
        fun archive(store: LocalStore): ByteArray = ByteArrayOutputStream().also {
            writeGovernedBackup(it, "dev.russiancompanion", pack.packId, pack.packVersion, "DARK", store.governedLearnerDocument(), emptyList())
        }.toByteArray()
        fun record(value: JsonObject) {
            System.getProperty("audit.history.output")?.let { path ->
                require(path.startsWith("/workspace/artifacts/"))
                val scoped = JsonObject(value + mapOf("modelCurriculumSource" to JsonPrimitive(beforeCommit),
                    "scope" to JsonPrimitive("HISTORICAL_PRE_LITERACY_CORRECTION")))
                File(path).appendText(scoped.toString() + "\n")
            }
        }
    }
}
