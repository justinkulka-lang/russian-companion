package dev.russiancompanion.data.local

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.seed.GateConditionSeed
import dev.russiancompanion.data.local.seed.PlanDefinitionSeed
import dev.russiancompanion.data.local.seed.SeedJson
import dev.russiancompanion.domain.gate.GateEvidenceKind
import dev.russiancompanion.domain.gate.GateStatus
import dev.russiancompanion.domain.gate.RecordedEvidence
import dev.russiancompanion.domain.gate.encodeConditionOutcomes
import dev.russiancompanion.domain.gate.evaluateGate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class GateStoreTest {
    @Test
    fun savedEvidenceChangesOnlyTheRecordedDecision() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        LocalStore.openInMemory(context).use { store ->
            store.applySeed(SeedJson.encode(gateDocument()))
            store.confirmLesson("segment-1", "2026-09-21")
            val low = store.recordGateEvidenceAndDecide(
                gateDefinitionId = "gate-1",
                entries = listOf(
                    EvidenceType.BOOK_REVISION_SCORE to "0",
                    EvidenceType.ANKI_DROID_STATUS to "started",
                ),
                recordedAtLocal = "2026-09-21T09:00",
            )
            assertEquals(GateDecisionStatus.NOT_YET_SATISFIED, low.status)
            assertTrue(store.evidenceFor("gate-1").all { it.selfAttestationStatus == SelfAttestationStatus.SELF_ATTESTED })

            val met = store.recordGateEvidenceAndDecide(
                gateDefinitionId = "gate-1",
                entries = listOf(
                    EvidenceType.BOOK_REVISION_SCORE to "1",
                    EvidenceType.ANKI_DROID_STATUS to "mature",
                ),
                recordedAtLocal = "2026-09-21T10:00",
            )
            assertEquals(GateDecisionStatus.SATISFIED, met.status)
            val reproduced = evaluateGate(
                ruleConditions(store.conditions("gate-1"), tutorRequired = false),
                store.evidenceFor("gate-1").map { it.toTestEvidence() },
            )
            assertEquals(GateStatus.SATISFIED, reproduced.status)
            assertEquals(encodeConditionOutcomes(reproduced.outcomes), met.conditionOutcomes)
            assertEquals("1", store.evidenceFor("gate-1").last { it.evidenceType == EvidenceType.BOOK_REVISION_SCORE }.enteredValue)

            store.addTutorSignOffRequirement("gate-1", "2026-09-21T11:00")
            assertEquals(GateDecisionStatus.NOT_EVALUABLE, store.decisions().last().status)
            val signed = store.recordGateEvidenceAndDecide(
                gateDefinitionId = "gate-1",
                entries = listOf(EvidenceType.TUTOR_SIGN_OFF to "recorded"),
                recordedAtLocal = "2026-09-21T12:00",
            )
            assertEquals(GateDecisionStatus.SATISFIED, signed.status)
            assertEquals(
                SelfAttestationStatus.USER_RECORDED,
                store.evidenceFor("gate-1").single { it.evidenceType == EvidenceType.TUTOR_SIGN_OFF }.selfAttestationStatus,
            )
            assertTrue(store.evidenceFor("gate-1").none { it.evidenceType.name.contains("DRILL") })
        }
    }

    @Test
    fun invalidAndUnreadableEvidenceDoNotProduceASatisfiedDecision() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        LocalStore.openInMemory(context).use { store ->
            store.applySeed(SeedJson.encode(gateDocument()))
            try {
                store.recordGateEvidenceAndDecide(
                    gateDefinitionId = "gate-1",
                    entries = listOf(EvidenceType.BOOK_REVISION_SCORE to "nope"),
                    recordedAtLocal = "2026-09-21T09:00",
                )
                error("an unusable book score was saved")
            } catch (_: IllegalArgumentException) {
                assertTrue(store.evidenceFor("gate-1").isEmpty())
                assertTrue(store.decisions().isEmpty())
            }
            try {
                store.recordGateEvidenceAndDecide(
                    gateDefinitionId = "gate-1",
                    entries = listOf(EvidenceType.PRESCRIBED_WORK_COMPLETION to "done"),
                    recordedAtLocal = "2026-09-21T09:01",
                )
                error("evidence outside the displayed rule was saved")
            } catch (_: IllegalArgumentException) {
                assertTrue(store.decisions().isEmpty())
            }
            store.recordEvidence(
                gateDefinitionId = "gate-1",
                evidenceType = EvidenceType.BOOK_REVISION_SCORE,
                enteredValue = "user-entered",
                recordedAtLocal = "2026-09-21T09:02",
            )
            val decision = store.recordGateEvidenceAndDecide(
                gateDefinitionId = "gate-1",
                entries = emptyList(),
                recordedAtLocal = "2026-09-21T09:03",
            )
            assertEquals(GateDecisionStatus.NOT_EVALUABLE, decision.status)
            assertTrue(decision.conditionOutcomes.contains("UNREADABLE"))
            assertEquals(1, store.evidenceFor("gate-1").size)
        }
    }

    private fun gateDocument() = minimalDocument().copy(
        planDefinitions = listOf(
            PlanDefinitionSeed(
                id = "plan-book",
                segmentId = "segment-1",
                activityType = "BOOK",
                instruction = "Use your copy of the book for this lesson",
                sortOrder = 1,
                required = true,
                lessonNumber = 1,
            ),
        ),
        gateDefinitions = listOf(
            gate(
                conditions = listOf(
                    GateConditionSeed("c-book", "BOOK_REVISION_SCORE", "AT_LEAST", "1", 0),
                    GateConditionSeed("c-anki", "ANKI_DROID_STATUS", "EQUALS", "mature", 1),
                ),
            ),
        ),
    )

    private fun GateEvidenceEntity.toTestEvidence(): RecordedEvidence {
        return RecordedEvidence(
            id = id,
            kind = when (evidenceType) {
                EvidenceType.BOOK_REVISION_SCORE -> GateEvidenceKind.BOOK_REVISION_SCORE
                EvidenceType.ANKI_DROID_STATUS -> GateEvidenceKind.ANKI_DROID_STATUS
                EvidenceType.PRESCRIBED_WORK_COMPLETION -> GateEvidenceKind.PRESCRIBED_WORK_COMPLETION
                EvidenceType.TUTOR_SIGN_OFF -> GateEvidenceKind.TUTOR_SIGN_OFF
            },
            enteredValue = enteredValue,
            recordedAtLocal = recordedAtLocal,
        )
    }
}
