package dev.russiancompanion.release

import dev.russiancompanion.data.local.audio
import dev.russiancompanion.data.local.gate
import dev.russiancompanion.data.local.minimalDocument
import dev.russiancompanion.data.local.rights
import dev.russiancompanion.data.local.seed.GateConditionSeed
import dev.russiancompanion.data.local.seed.PlanDefinitionSeed
import dev.russiancompanion.data.local.seed.SeedJson
import dev.russiancompanion.data.local.source
import dev.russiancompanion.data.local.text
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import java.io.File

class ReleaseGateTest {
    @Test
    fun packagedCatalogBlocksPublicRelease() {
        val assessment = assessPackagedRelease(packagedSeed())
        assertEquals(ReleaseDecision.NOT_APPROVED, assessment.decision)
        assertEquals(packagedBlockers, assessment.blockers)
        assertFalse(assessment.blockers.contains(ReleaseBlocker.SHARE_ALIKE_UNCONFIRMED))
        val report = File("build/reports/release-gate.txt")
        report.parentFile?.mkdirs()
        report.writeText(assessment.report())
    }

    @Test
    fun unconfirmedShareAlikeBlocksAnOtherwisePreparedCatalog() {
        val assessment = assessPackagedRelease(
            SeedJson.encode(preparedCatalog(shareAlike = "UNCONFIRMED")),
            retainedEvidence,
        )
        assertEquals(ReleaseDecision.NOT_APPROVED, assessment.decision)
        assertEquals(listOf(ReleaseBlocker.SHARE_ALIKE_UNCONFIRMED), assessment.blockers)
    }

    @Test
    fun confirmedShareAlikeClearsOnlyThatBlocker() {
        val waiting = assessPackagedRelease(SeedJson.encode(preparedCatalog(shareAlike = "CONFIRMED")))
        assertEquals(
            listOf(
                ReleaseBlocker.OFFLINE_VERIFICATION_NOT_RETAINED,
                ReleaseBlocker.REBOOT_EVIDENCE_NOT_RETAINED,
                ReleaseBlocker.LESSON_CYCLE_NOT_RETAINED,
            ),
            waiting.blockers,
        )
        val ready = assessPackagedRelease(
            SeedJson.encode(preparedCatalog(shareAlike = "CONFIRMED")),
            retainedEvidence,
        )
        assertEquals(ReleaseDecision.APPROVED, ready.decision)
        assertEquals(emptyList<ReleaseBlocker>(), ready.blockers)
    }

    @Test
    fun notApplicableShareAlikeDoesNotBlock() {
        val assessment = assessPackagedRelease(
            SeedJson.encode(preparedCatalog(shareAlike = "NOT_APPLICABLE")),
            retainedEvidence,
        )
        assertEquals(ReleaseDecision.APPROVED, assessment.decision)
    }

    @Test
    fun absentOrCorruptContentFailsClosed() {
        assertEquals(
            listOf(ReleaseBlocker.CONTENT_UNREADABLE),
            assessPackagedRelease(null).blockers,
        )
        assertEquals(
            listOf(ReleaseBlocker.CONTENT_UNREADABLE),
            assessPackagedRelease("{").blockers,
        )
    }

    @Test
    fun aTutorOnlyGateAndAPartialPlanStayBlocked() {
        val tutorGate = preparedCatalog(shareAlike = "CONFIRMED").copy(
            gateDefinitions = listOf(
                gate(
                    conditions = listOf(
                        GateConditionSeed("c-tutor", "TUTOR_SIGN_OFF", "PRESENT", null, 0),
                    ),
                ),
            ),
        )
        assertEquals(
            true,
            assessPackagedRelease(SeedJson.encode(tutorGate), retainedEvidence)
                .blockers
                .contains(ReleaseBlocker.DEFAULT_GATE_NOT_INSTALLED),
        )
        val bookOnly = preparedCatalog(shareAlike = "CONFIRMED").copy(
            planDefinitions = listOf(plan("plan-book", "BOOK", 1)),
        )
        assertEquals(
            true,
            assessPackagedRelease(SeedJson.encode(bookOnly), retainedEvidence)
                .blockers
                .contains(ReleaseBlocker.PLAN_DEFINITIONS_NOT_INSTALLED),
        )
    }

    private fun packagedSeed(): String {
        val candidates = listOf(
            File("src/main/assets/content/bundled-content.json"),
            File("app/src/main/assets/content/bundled-content.json"),
        )
        return candidates.first { it.isFile }.readText()
    }

    private fun preparedCatalog(shareAlike: String) = minimalDocument().copy(
        planDefinitions = listOf(
            plan("plan-book", "BOOK", 1),
            plan("plan-anki", "ANKI_DROID", 2),
            plan("plan-listen", "LISTEN_RECORD_COMPARE", 3),
        ),
        gateDefinitions = listOf(
            gate(
                conditions = listOf(
                    GateConditionSeed("c-book", "BOOK_REVISION_SCORE", "AT_LEAST", "1", 0),
                    GateConditionSeed("c-anki", "ANKI_DROID_STATUS", "EQUALS", "mature", 1),
                ),
            ),
        ),
        sourceRecords = listOf(source().copy(shareAlikeConfirmation = shareAlike)),
        sourceTexts = listOf(text(matchVerified = true)),
        assetRightsRecords = listOf(rights()),
        audioModels = listOf(audio()),
    )

    private fun plan(id: String, activityType: String, sortOrder: Int) = PlanDefinitionSeed(
        id = id,
        segmentId = "segment-1",
        activityType = activityType,
        instruction = "Use the assigned local activity",
        sortOrder = sortOrder,
        required = true,
    )

    private companion object {
        val retainedEvidence = RetainedReleaseEvidence(
            offlineVerification = true,
            deviceReboot = true,
            lessonCycle = true,
        )
        val packagedBlockers = listOf(
            ReleaseBlocker.PATHWAY_NOT_INSTALLED,
            ReleaseBlocker.PLAN_DEFINITIONS_NOT_INSTALLED,
            ReleaseBlocker.DEFAULT_GATE_NOT_INSTALLED,
            ReleaseBlocker.LICENSED_AUDIO_NOT_PREPARED,
            ReleaseBlocker.RIGHTS_RECORDS_ABSENT,
            ReleaseBlocker.OFFLINE_VERIFICATION_NOT_RETAINED,
            ReleaseBlocker.REBOOT_EVIDENCE_NOT_RETAINED,
            ReleaseBlocker.LESSON_CYCLE_NOT_RETAINED,
        )
    }
}
