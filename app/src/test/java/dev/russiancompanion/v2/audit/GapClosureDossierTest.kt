package dev.russiancompanion.v2.audit

import dev.russiancompanion.release.computeReleaseSourceDigest
import dev.russiancompanion.release.readAcceptanceCandidate
import dev.russiancompanion.v2.curriculum.V2Validator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * The gap-closure dossier is research planning bound to the live readiness
 * reconciliation. It does not admit curriculum, approve review, activate a
 * variance, freeze a candidate, or authorize release.
 */
class GapClosureDossierTest {
    @Test
    fun dossierMatchesLiveReadinessAndDoesNotAuthorizeRelease() {
        val root = root()
        val text = File(root, DOSSIER).readText()
        val pack = loadCurriculumPack(root)
        val model = reconcileCurriculumReadiness(root, pack)
        val candidate = readAcceptanceCandidate(root)

        assertEquals(V2Validator.contentReviewDigest(pack), model.packContentReviewDigest)
        assertEquals(computeReleaseSourceDigest(root), model.repositorySourceDigest)
        assertTrue(text.contains("**Repository baseline main:** `$REPOSITORY_BASELINE_MAIN`"))
        assertTrue(text.contains("**Unit 23 unprepared correction:** `$UNIT_23_UNPREPARED_CORRECTION`"))
        assertTrue(text.contains("Keep Unit 23 unprepared until completion over a period is admitted."))
        assertFalse(text.contains("**Canonical commit:**"))
        assertTrue(text.contains("**Pack content-review digest:** `${model.packContentReviewDigest}`"))
        assertTrue(text.contains("**Repository source digest:** `${model.repositorySourceDigest}`"))
        assertTrue(text.contains("**Public release:** **NOT_APPROVED**"))
        assertTrue(text.contains("This dossier authorizes research planning only."))
        assertTrue(text.contains("It does not add curriculum"))
        assertEquals("NOT_APPROVED", model.publicRelease)
        assertEquals("NOT_APPROVED", candidate?.publicRelease)
        assertEquals(false, candidate?.frozen)
        assertEquals("NOT_RECORDED", candidate?.artifact?.status)
        assertEquals(null, candidate?.sourceCommit)
        assertEquals("PROPOSED_NOT_ACTIVATED", model.varianceProposalStatus)
        assertEquals("PROPOSED_NOT_ACTIVATED", model.varianceResolutionStatus)
        assertEquals("UNEXECUTED", model.at031)
        assertEquals("ex-u4-verb-знать, ex-u4-verb-работать", model.orphanedExampleIds)
        assertTrue(text.contains("ex-u4-verb-знать"))
        assertTrue(text.contains("ex-u4-verb-работать"))
        assertTrue(text.contains("Do not bind it merely to eliminate the orphan count."))
        assertTrue(text.contains("Do not create an empty packet merely to remove the missing-packet blocker."))

        val rows = matrixRows(text)
        assertEquals((1..30).toList(), rows.map { it.unitNumber })
        rows.zip(model.units).forEach { (recorded, live) ->
            assertEquals(live.unitNumber, recorded.unitNumber)
            assertEquals(live.title, recorded.title)
            assertEquals(live.readiness, recorded.readiness)
            assertEquals(if (live.advancementCanPresentlySucceed) "YES" else "NO", recorded.advancement)
            assertEquals(live.contentBlocker, recorded.content)
            assertEquals(live.assetBlocker, recorded.assets)
            assertEquals(live.authorityVarianceBlocker, recorded.variance)
            assertEquals(if (live.reviewPackageDigest == "NONE") "NO" else "YES", recorded.packet)
        }

        val contentUnits = model.units.filter { it.contentBlocker != "NONE" }.map { it.unitNumber }
        assertTrue(contentUnits.containsAll(listOf(7, 23, 26, 27, 29, 30)))
        assertFalse(contentUnits.any { it in listOf(17, 24, 25, 28) })
        assertEquals(
            (1..30).toList(),
            model.units.filter { it.linguisticReviewBlocker != "NONE" }.map { it.unitNumber },
        )
        assertEquals(
            (1..30).toList(),
            model.units.filter { it.mappingBlocker != "NONE" }.map { it.unitNumber },
        )
        assertEquals(listOf(1, 2), model.units.filter { it.assetBlocker != "NONE" }.map { it.unitNumber })
        assertTrue(model.units.all { it.localExperienceBlocker == "NONE" })
        assertEquals(
            emptyList<Int>(),
            model.units.filter { it.reviewPackageDigest == "NONE" }.map { it.unitNumber },
        )
        assertEquals(
            listOf(27, 29, 30),
            model.units.filter { it.authorityVarianceBlocker != "NOT_APPLICABLE" }.map { it.unitNumber },
        )
        assertTrue(model.units.filter { it.authorityVarianceBlocker != "NOT_APPLICABLE" }.all {
            it.authorityVarianceBlocker == "PROPOSED_NOT_ACTIVATED"
        })

        val headings = researchHeadings(text)
        val unresolved = model.units.flatMap { row ->
            if (row.unresolvedRequiredSourceCapabilities == "NONE") emptyList()
            else row.unresolvedRequiredSourceCapabilities.split(", ").map { row.unitNumber to it }
        }
        unresolved.forEach { (unitNumber, claim) ->
            assertTrue(
                "Unit $unitNumber unresolved claim $claim is missing from the research queue",
                headings.any { it.unitNumber == unitNumber && it.claim == claim },
            )
        }
        val resolvedClaims = setOf(
            "word-order",
            "passive-participle",
            "imperfective-verbal-adverb",
            "perfective-verbal-adverb",
            "active-participle",
            "short-form-adjective",
            "letter-name-review",
            "abbreviation",
            "particle",
            "adverb-introduction",
            "instrumental-complements",
            "age",
            "weekdays",
            "months",
            "clock-question",
            "comparison",
            "relative-kotoryy",
            "conditional-by",
            "obligation-dolzhen",
            "motion-directionality",
            "motion-foot-transport",
            "motion-carrying-leading",
            "motion-po-setting-out",
            "motion-prefixed-path",
            "adverb-form-gloss",
            "clock-expression-recognition",
            "calendar-date",
            "reflexive-object",
            "purpose-clause",
            "declined-numeral",
            "both-forms",
            "one-and-a-half",
            "collective-numeral",
            "negation-alternation",
            "negative-pronoun",
            "register",
            "elapsed-time-duration",
            "diminutive-form-classification",
            "indefinite-pronoun",
            "participle-punctuation",
        )
        assertTrue(headings.none { it.claim in resolvedClaims })
        assertTrue(headings.none { it.unitNumber in listOf(16, 18, 20) })
        assertEquals(
            emptyList<String>(),
            headings.filter { it.unitNumber == 19 }.map { it.claim },
        )
        assertTrue(text.contains("0f0744dfeb76433fddb5604a065c92427c54848a"))
        assertTrue(text.contains("was merged into the Batch A branch"))
        assertTrue(text.contains("reachable from `main`"))
        assertTrue(text.contains("Batch E excluded only the six inspected Russian Wikipedia revisions."))
        assertTrue(text.contains("do not establish that the five capabilities are impossible to source from a different licensed project"))
        assertFalse(text.contains("merged into this Batch A branch only"))
        assertFalse(text.contains("closed as superseded"))
        assertFalse(text.contains("PR #42 merged into main"))
    }

    private fun matrixRows(text: String): List<DossierMatrixRow> {
        val lines = text.lineSequence().dropWhile { !it.startsWith("| Unit |") }.drop(2).takeWhile { it.startsWith("|") }
        return lines.map { line ->
            val cells = line.trim().removePrefix("|").removeSuffix("|").split("|").map { it.trim() }
            assertEquals(9, cells.size)
            DossierMatrixRow(
                unitNumber = cells[0].toInt(),
                title = cells[1],
                readiness = cells[2],
                advancement = cells[3],
                content = cells[4],
                assets = cells[5],
                variance = cells[6],
                packet = cells[7],
            )
        }.toList()
    }

    private fun researchHeadings(text: String): List<ResearchHeading> {
        val headings = mutableListOf<ResearchHeading>()
        var unitNumber = 0
        val unitStart = Regex("""^### Unit (\d+) —""")
        val claim = Regex("""^#### `([^`]+)` — `([^`]+)`""")
        text.lineSequence().forEach { line ->
            unitStart.find(line)?.let { unitNumber = it.groupValues[1].toInt() }
            claim.find(line)?.let {
                headings += ResearchHeading(unitNumber, it.groupValues[1], it.groupValues[2])
            }
        }
        return headings
    }

    private fun root(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }

    private data class DossierMatrixRow(
        val unitNumber: Int,
        val title: String,
        val readiness: String,
        val advancement: String,
        val content: String,
        val assets: String,
        val variance: String,
        val packet: String,
    )

    private data class ResearchHeading(
        val unitNumber: Int,
        val claim: String,
        val objective: String,
    )

    private companion object {
        const val DOSSIER = "audit/curriculum-readiness/V2_CURRICULUM_GAP_CLOSURE_DOSSIER_v1.0.md"
        const val REPOSITORY_BASELINE_MAIN = "c153103a0048f69f8de339b83e77d93572ebddb7"
        const val UNIT_23_UNPREPARED_CORRECTION = "fd69bd03298d69669d09daafe8d364b0e576d033"
    }
}
