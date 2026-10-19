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
@Config(sdk = [33])
class OptionPresentationTest {
    @Test fun currentCorpusPreservesEveryOptionAndDispersesCorrectPositionsInAllThreeRoutes() {
        val p = OptionPresentationFixture.pack
        assertEquals(622, p.items.size)
        assertEquals(561, p.items.count { it.options.first() == it.answer })
        val routes = p.units.flatMap { it.sessions }.flatMap { it.steps }
            .flatMap { step -> step.itemIds.map { p.item(it)!! to step.id } }
        for ((name, displays) in listOf(
            "lesson" to routes.map { (item, step) -> item to lessonPresentedOptions(item, step) },
            "required" to p.units.flatMap { it.objectives }.filter { it.gating }.flatMap { it.checkItemIds }.distinct().map { id ->
                val item = p.item(id)!!
                val step = routes.first { it.first.id == id }.second
                item to lessonPresentedOptions(item, step)
            },
            "lab" to p.items.map { it to labPresentedOptions(it, "mixed") },
            "review" to p.items.map { it to presentedOptions(it, reviewOptionContext("corpus-distribution", 0)) },
        )) {
            if (name == "required") {
                assertEquals(429, displays.size)
                assertEquals(379, displays.count { (item, _) -> item.options.first() == item.answer })
            }
            displays.forEach { (item, order) ->
                assertEquals(item.id, item.options.sorted(), order.sorted())
                assertEquals(item.options.size, order.size)
            }
            val positions = displays.groupingBy { (item, order) -> order.indexOf(item.answer) }.eachCount()
            println("CC001 $name displays=${displays.size} correctPositions=$positions")
            // A first-position strategy must fail on a majority of these current displays.
            assertTrue("$name: $positions", (positions[0] ?: 0) < displays.size / 2)
            assertTrue("$name: $positions", positions.size >= 3)
        }
    }

    @Test fun permutationIgnoresAnswerFeedbackAndAuthoredOrderingAndHasAFixedVersionVector() {
        val i = OptionPresentationFixture.pack.items.first { it.options.size >= 3 }
        val first = lessonPresentedOptions(i, "fixed-context")
        assertEquals(first, lessonPresentedOptions(i.copy(answer = i.options.first { it != i.answer },
            feedback = "changed", options = i.options.reversed()), "fixed-context"))
        // Cross-runtime vector catches accidental changes to encoding, SHA ordering or version.
        val vector = i.copy(id = "vector", options = listOf("a", "b", "c"))
        assertEquals(listOf("a", "b", "c"), presentedOptions(vector, "vector-context"))
        assertEquals(i.options, OptionPresentationFixture.pack.item(i.id)!!.options)
    }

    @Test fun lessonLabAndReviewContextsAreStableButReviewAssignmentIdsVaryPresentation() {
        val i = OptionPresentationFixture.pack.items.first { it.options.size >= 3 }
        val order = lessonPresentedOptions(i, "step")
        repeat(10) { assertEquals(order, lessonPresentedOptions(i.copy(), "step")) }
        assertEquals(labPresentedOptions(i, "grammar"), labPresentedOptions(i.copy(), "grammar"))
        val orders = (0..99).map { presentedOptions(i, reviewOptionContext("assignment-$it", 0)) }.toSet()
        assertTrue(orders.size >= 4)
        assertNotEquals(closedResponseRuleDigest(), presentedReviewResponseRuleDigest())
    }

    @Test fun allRetainedAssignmentsKeepExactLegacyOrderAndAssignmentHash() {
        val root = OptionPresentationFixture.root
        val groups = listOf("ia003-policy-history-transition", "ia003-policy-history-transition/predecessor-schema1",
            "ia003-unit13-context-teaching", "ia003-unit13-history-transition", "ia003-unit13-past-teaching",
            "ia003-unit3-gender-correction", "ia003-unit7-agreement-teaching", "ia003-unit9-history-transition")
        var documents = 0
        for (group in groups) {
            val dir = File(root, "audit/independent-gpt61-2026-10-12/$group")
            for (file in dir.listFiles()!!.filter { it.extension == "json" }) {
                val text = file.readText()
                if (!text.contains("\"reviewAssignments\"") || !text.contains("\"profile\"")) continue
                val d = runCatching { backupJson.decodeFromString(GovernedLearnerDocument.serializer(), text) }.getOrNull() ?: continue
                documents++
                for (a in d.reviewAssignments) {
                    assertEquals(closedResponseRuleDigest(), a.responseRuleDigest)
                    assertEquals(a.digest, reviewAssignmentDigest(a))
                    for ((index, slot) in a.slots.withIndex()) {
                        val i = OptionPresentationFixture.pack.item(slot.itemId)!!
                        assertEquals(i.options, reviewPresentedOptions(i, a, index))
                    }
                }
            }
        }
        assertEquals(64, documents)
    }

    @Test fun newRulePreservesExactScoringAndOrderThroughBackupAndNamedDatabaseReopen() {
        val p = OptionPresentationFixture.pack
        val context: Context = ApplicationProvider.getApplicationContext()
        val name = "option-presentation-${System.nanoTime()}.db"
        val temp = File(context.cacheDir, "option-backup-${System.nanoTime()}")
        try {
            LocalStore.openInMemory(context).use { s ->
                OptionPresentationFixture.prepare(s)
                val a = s.openCumulativeReview(p, 3, s.learnerMutationEpoch())!!
                assertEquals(presentedReviewResponseRuleDigest(), a.responseRuleDigest)
                val before = a.slots.mapIndexed { index, slot -> reviewPresentedOptions(p.item(slot.itemId)!!, a, index) }
                val ordinary = creditItemIds(p, s.v2LearnerView())
                for ((index, slot) in a.slots.withIndex()) {
                    val item = p.item(slot.itemId)!!
                    assertTrue(s.answerCumulativeReview(p, a.id, index, before[index].first { it != item.answer }, s.learnerMutationEpoch()))
                    assertFalse(index in completedReviewSlots(p, a, s.cumulativeReviewHistory().second))
                    assertTrue(s.answerCumulativeReview(p, a.id, index, before[index].single { it == item.answer }, s.learnerMutationEpoch()))
                    assertTrue(index in completedReviewSlots(p, a, s.cumulativeReviewHistory().second))
                }
                assertEquals(ordinary, creditItemIds(p, s.v2LearnerView()))
                val bytes = ByteArrayOutputStream()
                writeGovernedBackup(bytes, "dev.russiancompanion", p.packId, p.packVersion, "FOLLOW_SYSTEM", s.governedLearnerDocument(), emptyList())
                val read = readGovernedBackup(bytes.toByteArray().inputStream(), "dev.russiancompanion", p.packId, p.packVersion, temp)
                assertTrue(read is BackupReadResult.Valid)
                LocalStore.openNamed(context, name).use { target ->
                    assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner((read as BackupReadResult.Valid).backup.document, emptyList()))
                }
                LocalStore.openNamed(context, name).use { target ->
                    val resumed = target.openCumulativeReview(p, 3, target.learnerMutationEpoch())!!
                    assertEquals(a, resumed)
                    assertEquals(before, resumed.slots.mapIndexed { index, slot -> reviewPresentedOptions(p.item(slot.itemId)!!, resumed, index) })
                    assertEquals(s.cumulativeReviewHistory(), target.cumulativeReviewHistory())
                    assertEquals(a.slots.indices.toSet(), completedReviewSlots(p, resumed, target.cumulativeReviewHistory().second))
                }
                val forged = a.copy(responseRuleDigest = "0".repeat(64)).let { it.copy(digest = reviewAssignmentDigest(it)) }
                assertFalse(reviewAssignmentCurrent(p, forged))
                assertTrue(reviewPresentedOptions(p.item(a.slots[0].itemId)!!, forged, 0).isEmpty())
            }
        } finally { context.deleteDatabase(name); temp.deleteRecursively() }
    }
}

internal object OptionPresentationFixture {
    val root get() = File(if (File("app").isDirectory) "." else "..")
    val pack by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) } }
    fun prepare(s: LocalStore) {
        val p = pack
        for (n in 1..2) {
            s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = n))
            for (step in p.unit(n)!!.sessions.flatMap { it.steps }) {
                for (id in step.itemIds) s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, p.item(id)!!.answer,
                    PRESCRIBED_CHECK, step.id, "fixed-clock")!!)
                s.v2CompleteGovernedStep(step.id, governedStepCompletionDigest(p, s.v2LearnerView(), step.id)!!, "fixed-clock")
            }
            s.v2AttestBook(n, "fixed-clock"); s.v2CompleteUnit(n, "fixed-clock")
        }
        s.v2SaveProfile(s.v2Profile().copy(currentUnitNumber = 3))
    }
}
