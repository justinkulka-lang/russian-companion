package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class CumulativePolicyHistoryBaselineTest {
    @Test fun genuineSchemaThreeLifecycleRestoresReopensAndContinuesWithoutErasingAcceptedSnapshots() = lifecycle("")

    @Test fun genuinePredecessorSchemaOneLifecycleRestoresReopensAndContinuesWithoutRelabelling() = lifecycle("predecessor-schema1/")

    private fun lifecycle(prefix: String) {
        val p = V2PackLoader.parse(File(root(), "app/src/main/assets/content/v2-curriculum.json").readText())
        V2PackIdentityIndex.warm(p)
        val sourceSchema = if (prefix.isEmpty()) 3 else 1
        for (label in listOf("BEFORE_UNIT6_WORK", "ORDINARY_UNIT6", "UNSTARTED_REVIEW", "PARTIAL_REVIEW",
            "ANSWERED_REVIEW", "COMPLETED_UNIT6", "POST_COMPLETION_WRONG", "COMPLETED_UNIT7")) {
            val dir = File(root(), "audit/independent-gpt61-2026-10-12/ia003-policy-history-transition/$prefix")
            val document = backupJson.decodeFromString(GovernedLearnerDocument.serializer(), File(dir, "$label.json").readText())
            val assignment = document.reviewAssignments.singleOrNull { it.targetUnit == 6 }
            assignment?.let { assertEquals(sourceSchema, it.schema) }
            val completed = when (label) {
                "COMPLETED_UNIT7" -> (1..7).toSet()
                "COMPLETED_UNIT6", "POST_COMPLETION_WRONG" -> (1..6).toSet()
                else -> (1..5).toSet()
            }
            val expectedSlots = when (label) {
                "PARTIAL_REVIEW", "POST_COMPLETION_WRONG", "COMPLETED_UNIT7" -> setOf(0)
                "ANSWERED_REVIEW", "COMPLETED_UNIT6" -> setOf(0, 1)
                else -> emptySet()
            }
            val name = "baseline-history-$label-${System.nanoTime()}.db"
            val staging = File(context().cacheDir, "baseline-history-${System.nanoTime()}")
            try {
                val read = File(dir, "$label.zip").inputStream().use {
                    readGovernedBackup(it, "dev.russiancompanion", p.packId, p.packVersion, staging)
                }
                assertTrue(read is BackupReadResult.Valid)
                LocalStore.openNamed(context(), name).use { s ->
                    val epoch = s.learnerMutationEpoch()
                    s.openLearnerReplacement()
                    try { assertEquals(GovernedReplaceOutcome.REPLACED,
                        s.replaceGovernedLearner((read as BackupReadResult.Valid).backup.document, emptyList())) }
                    finally { s.closeLearnerReplacement() }
                    assertNotEquals(epoch, s.learnerMutationEpoch())
                    assertEquals(document.reviewAssignments to document.reviewAnswers, s.cumulativeReviewHistory())
                    assertEquals(document.unitCompletions, s.governedLearnerDocument().unitCompletions)
                }
                LocalStore.openNamed(context(), name).use { s ->
                    assertEquals(completed, currentCompletedUnitNumbers(p, s.v2LearnerView()))
                    assertEquals(assignment, currentCumulativeAssignment(p, s.v2LearnerView(), 6))
                    assertEquals(expectedSlots, assignment?.let {
                        completedReviewSlots(p, it, s.v2LearnerView().reviewAnswers)
                    }.orEmpty())
                    if (label != "BEFORE_UNIT6_WORK") {
                        val opened = s.openCumulativeReview(p, 6, s.learnerMutationEpoch())!!
                        if (assignment != null) assertEquals(assignment, opened) else assertEquals(3, opened.schema)
                        if (6 !in completed) {
                            assertEquals(label == "ANSWERED_REVIEW", advancementDecision(p, p.unit(6)!!, s.v2LearnerView()).allowed)
                            opened.slots.forEachIndexed { index, slot ->
                                assertTrue(s.answerCumulativeReview(p, opened.id, index, p.item(slot.itemId)!!.answer, s.learnerMutationEpoch()))
                            }
                            assertTrue(advancementDecision(p, p.unit(6)!!, s.v2LearnerView()).allowed)
                            val session = p.unit(7)!!.sessions.first().id
                            assertTrue(runBlocking { s.v2AdvanceUnitIfCurrentAsync(s.learnerMutationEpoch(), 6, "continue", 7,
                                session, resumePositionDigest(p, session, 0)!!, GOVERNED_IDENTITY_SCHEMA, p) })
                            val row = s.governedLearnerDocument().unitCompletions.single { it.unitNumber == 6 }
                            assertEquals(opened.id, row.reviewAssignmentId)
                            assertEquals(opened.policyDigest, row.reviewPolicyDigest)
                            assertTrue(row.reviewCompletionOrder > opened.boundary)
                        }
                    }
                    if (label == "COMPLETED_UNIT7") {
                        val downstream = document.reviewAssignments.single { it.targetUnit == 7 }
                        assertTrue(reviewAssignmentCurrent(p, downstream))
                        val changed = p.copy(items = p.items.map {
                            if (it.id == downstream.slots[1].itemId) it.copy(feedback = it.feedback + " changed") else it
                        })
                        V2PackIdentityIndex.warm(changed)
                        assertFalse(reviewAssignmentCurrent(changed, downstream))
                        assertFalse(6 in currentCompletedUnitNumbers(changed, s.v2LearnerView()))
                        assertFalse(7 in currentCompletedUnitNumbers(changed, s.v2LearnerView()))
                    }
                }
            } finally {
                staging.deleteRecursively()
                context().deleteDatabase(name)
                File(context().filesDir, "recordings-$name").deleteRecursively()
            }
        }
    }

    private fun root() = File(if (File("app").isDirectory) "." else "..")
    private fun context(): Context = ApplicationProvider.getApplicationContext()
}
