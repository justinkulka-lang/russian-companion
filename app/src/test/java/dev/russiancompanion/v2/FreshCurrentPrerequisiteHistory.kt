package dev.russiancompanion.v2

import android.content.Context
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.GovernedLearnerDocument
import dev.russiancompanion.v2.curriculum.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

/** Test-only memo of genuine fresh API history, keyed by actual pack identity and target. */
internal object FreshCurrentPrerequisiteHistory {
    private val documents = java.util.IdentityHashMap<V2CurriculumPack, MutableMap<Int, GovernedLearnerDocument>>()
    fun document(p: V2CurriculumPack, target: Int, context: Context): GovernedLearnerDocument {
        documents[p]?.get(target)?.let { return it }
        require(target in 2..30)
        val result = LocalStore.openInMemory(context).use { producer ->
            for (n in 1 until target) {
                ordinary(producer, p, n)
                if (n >= 3) {
                    val task = producer.openCumulativeReview(p, n, producer.learnerMutationEpoch())!!
                    task.slots.indices.forEach { index ->
                        assertTrue(producer.answerCumulativeReview(p, task.id, index,
                            p.item(task.slots[index].itemId)!!.answer, producer.learnerMutationEpoch()))
                    }
                }
                assertTrue(advancementDecision(p, p.unit(n)!!, producer.v2LearnerView()).allowed)
                assertTrue(runBlocking {
                    val next = p.unit(n + 1)!!.sessions.first().id
                    producer.v2AdvanceUnitIfCurrentAsync(producer.learnerMutationEpoch(), n, "fresh-current-fixture", n + 1,
                        next, resumePositionDigest(p, next, 0)!!, GOVERNED_IDENTITY_SCHEMA, p)
                })
            }
            assertEquals((1 until target).toSet(), currentCompletedUnitNumbers(p, producer.v2LearnerView()))
            assertEquals(target, producer.v2Profile().currentUnitNumber)
            producer.governedLearnerDocument()
        }
        documents.getOrPut(p) { mutableMapOf() }[target] = result
        return result
    }
    fun ordinary(store: LocalStore, p: V2CurriculumPack, n: Int) {
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = n, currentSessionId = null))
        for (step in p.unit(n)!!.sessions.flatMap { it.steps }) {
            for (id in step.itemIds) store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, p.item(id)!!.answer,
                PRESCRIBED_CHECK, step.id, "fresh-current-fixture")!!)
            store.v2CompleteGovernedStep(step.id, governedStepCompletionDigest(p, store.v2LearnerView(), step.id)!!, "fresh-current-fixture")
        }
        store.v2AttestBook(n, "fresh-current-fixture")
    }
}
