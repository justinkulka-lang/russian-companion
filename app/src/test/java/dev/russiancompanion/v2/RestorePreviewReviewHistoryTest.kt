package dev.russiancompanion.v2

import dev.russiancompanion.v2.backup.GovernedLearnerDocument
import dev.russiancompanion.v2.backup.backupJson
import dev.russiancompanion.v2.backup.restorePreview
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class RestorePreviewReviewHistoryTest {
    @Test fun retainedReviewOnlyPairsShowTwoIncomingAnswers() {
        for ((before, after) in pairs()) {
            assertEquals(before, after.copy(reviewAnswers = before.reviewAnswers))
            assertEquals(2, after.reviewAnswers.size - before.reviewAnswers.size)
            val preview = restorePreview(before, after)
            assertEquals(2, preview.additions)
            assertEquals(0, preview.omissions)
            assertEquals(0, preview.conflicts)
            assertEquals(restorePreview(before, before).replacements, preview.replacements)
        }
    }

    @Test fun retainedReviewOnlyPairsShowTwoAnswersRemovedInReverse() {
        for ((before, after) in pairs()) {
            val preview = restorePreview(after, before)
            assertEquals(0, preview.additions)
            assertEquals(2, preview.omissions)
            assertEquals(0, preview.conflicts)
            assertEquals(restorePreview(before, before).replacements, preview.replacements)
        }
    }

    @Test fun sameAnswerIdentityWithChangedSelectionIsAConflictInBothDirections() {
        for ((_, after) in pairs()) {
            val first = after.reviewAnswers.first()
            val alternative = after.reviewAnswers.first { it.selected != first.selected }.selected
            val changed = after.copy(reviewAnswers = after.reviewAnswers.map {
                if (it.id == first.id) it.copy(selected = alternative) else it
            })
            for ((current, incoming) in listOf(after to changed, changed to after)) {
                val preview = restorePreview(current, incoming)
                assertEquals(0, preview.additions)
                assertEquals(0, preview.omissions)
                assertEquals(1, preview.conflicts)
                assertEquals(restorePreview(after, after).replacements, preview.replacements)
            }
        }
    }

    @Test fun openAssignmentAddedOrRemovedIsVisibleWithoutOtherRecordChanges() {
        for ((before, _) in pairs()) {
            val referenced = before.unitCompletions.map { it.reviewAssignmentId }.toSet()
            val assignment = before.reviewAssignments.last { it.id !in referenced }
            assertFalse(before.reviewAnswers.any { it.assignmentId == assignment.id })
            val absent = before.copy(reviewAssignments = before.reviewAssignments.filterNot { it.id == assignment.id })
            val added = restorePreview(absent, before)
            assertEquals(1, added.additions)
            assertEquals(0, added.omissions)
            assertEquals(0, added.conflicts)
            val removed = restorePreview(before, absent)
            assertEquals(0, removed.additions)
            assertEquals(1, removed.omissions)
            assertEquals(0, removed.conflicts)
            assertEquals(added.replacements, removed.replacements)
        }
    }

    @Test fun unchangedReviewHistoryCountsSharedRecordsAndDoesNotMutateTheDocument() {
        for ((_, after) in pairs()) {
            val original = backupJson.encodeToString(GovernedLearnerDocument.serializer(), after)
            val noReview = after.copy(reviewAssignments = emptyList(), reviewAnswers = emptyList())
            val preview = restorePreview(after, after)
            assertEquals(0, preview.additions)
            assertEquals(0, preview.omissions)
            assertEquals(0, preview.conflicts)
            assertEquals(after.reviewAssignments.size + after.reviewAnswers.size,
                preview.replacements - restorePreview(noReview, noReview).replacements)
            assertEquals(original, backupJson.encodeToString(GovernedLearnerDocument.serializer(), after))
        }
    }

    private fun pairs(): List<Pair<GovernedLearnerDocument, GovernedLearnerDocument>> = groups.map { group ->
        fun read(label: String) = backupJson.decodeFromString(GovernedLearnerDocument.serializer(),
            File(root(), "$group/$label.json").readText())
        read("UNSTARTED_REVIEW") to read("PARTIAL_REVIEW")
    }

    private fun root() = File(if (File("app").isDirectory) "." else "..")

    private val groups = listOf(
        "audit/independent-gpt61-2026-10-12/ia003-policy-history-transition",
        "audit/independent-gpt61-2026-10-12/ia003-policy-history-transition/predecessor-schema1",
        "audit/independent-gpt61-2026-10-12/ia003-unit13-context-teaching",
        "audit/independent-gpt61-2026-10-12/ia003-unit13-history-transition",
        "audit/independent-gpt61-2026-10-12/ia003-unit13-past-teaching",
        "audit/independent-gpt61-2026-10-12/ia003-unit3-gender-correction",
        "audit/independent-gpt61-2026-10-12/ia003-unit7-agreement-teaching",
        "audit/independent-gpt61-2026-10-12/ia003-unit9-history-transition",
    )
}
