package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2ProvenanceRecord
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.advancementDecision
import dev.russiancompanion.v2.curriculum.unit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class BatchAUnits71620Test {
    @Test
    fun wikipediaLicenseRouteIsNotTheWikibooksSnapshot() {
        val root = repo()
        val script = """
            import json
            from pathlib import Path
            from units_26_30_intake import license_admission_errors
            root = Path('.').resolve().parent
            freeze_path = root / 'catalog/sources/units-16-20/FREEZE.json'
            source_root = freeze_path.parent
            freeze = json.loads(freeze_path.read_text())
            assert license_admission_errors(freeze, source_root) == [], license_admission_errors(freeze, source_root)
            wikibooks_only = json.loads(json.dumps(freeze))
            for page in wikibooks_only['pages']:
                if page['sourceId'].startswith('src-wikipedia-'):
                    page['licenseRoute'] = 'wikibooks'
            wikibooks_errors = license_admission_errors(wikibooks_only, source_root)
            assert any('only Wikibooks license evidence' in error for error in wikibooks_errors), wikibooks_errors
            wikipedia_only = json.loads(json.dumps(freeze))
            for page in wikipedia_only['pages']:
                if page['sourceId'].startswith('src-wikibooks-'):
                    page['licenseRoute'] = 'wikipedia'
            wikipedia_errors = license_admission_errors(wikipedia_only, source_root)
            assert any('only Wikipedia license evidence' in error for error in wikipedia_errors), wikipedia_errors
            print('ok')
        """.trimIndent()
        val process = ProcessBuilder("python3", "-c", script)
            .directory(File(root, "catalog"))
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().readText()
        assertEquals(output, 0, process.waitFor())
    }

    @Test
    fun exactSpanIdentityRejectsKeywordAndCrossClaimSubstitution() {
        val pack = pack()
        val complement = pack.sourceCapabilities.first { it.supportedClaim == "instrumental-complements" }
        listOf(
            complement.copy(exactSpan = "January"),
            complement.copy(sourceRevision = "4292379"),
            complement.copy(sourceHash = "0".repeat(64)),
            complement.copy(unitNumber = 18, objectiveId = "obj-u18-comparison", supportedClaim = "comparison"),
            complement.copy(objectiveId = "obj-u17-age", supportedClaim = "age"),
        ).forEach { mutated ->
            val next = replaceCapability(pack, complement.id, mutated)
            val errors = V2Validator.validate(next).errors
            assertTrue(errors.joinToString("\n"), errors.any { complement.id in it })
        }

        val comparison = pack.sourceCapabilities.first { it.supportedClaim == "comparison" }
        val relativeSwap = pack.copy(
            sourceCapabilities = pack.sourceCapabilities.filter { it.supportedClaim != "relative-kotoryy" } +
                comparison.copy(id = "cap-swapped-relative", objectiveId = "obj-u18-relative", supportedClaim = "relative-kotoryy"),
        )
        val relativeErrors = V2Validator.validate(claimComplete(relativeSwap, 18)).errors
        assertTrue(relativeErrors.joinToString("\n"), relativeErrors.any { it.contains("without comparison and relative-clause sources") })

        val keyword = pack.copy(
            sourceCapabilities = pack.sourceCapabilities.filter { it.unitNumber != 17 },
            provenanceRecords = pack.provenanceRecords + keywordRecord(pack),
        )
        val keywordErrors = V2Validator.validate(claimComplete(keyword, 17)).errors
        assertTrue(keywordErrors.joinToString("\n"), keywordErrors.any { it.contains("without telling-time, weekday, date, and age sources") })

        val clockOnly = pack.sourceCapabilities.filter {
            it.unitNumber != 17 || it.supportedClaim == "clock-question" || it.supportedClaim == "age" ||
                it.supportedClaim == "weekdays" || it.supportedClaim == "months"
        }
        val clockPack = pack.copy(sourceCapabilities = clockOnly)
        val clockErrors = V2Validator.validate(claimComplete(clockPack, 17)).errors
        assertTrue(clockErrors.joinToString("\n"), clockErrors.any { it.contains("without telling-time, weekday, date, and age sources") })

        val conditional = pack.sourceCapabilities.first { it.supportedClaim == "conditional-by" }
        val probabilitySwap = pack.copy(
            sourceCapabilities = pack.sourceCapabilities.filter { it.supportedClaim != "obligation-dolzhen" } +
                conditional.copy(id = "cap-swapped-obligation", objectiveId = "obj-u19-obligation", supportedClaim = "obligation-dolzhen"),
        )
        val obligationErrors = V2Validator.validate(claimComplete(probabilitySwap, 19)).errors
        assertTrue(obligationErrors.joinToString("\n"), obligationErrors.any { it.contains("without conditional and obligation sources") })

        val motion = pack.sourceCapabilities.first { it.supportedClaim == "motion-directionality" }
        val foreign = pack.copy(
            sourceCapabilities = pack.sourceCapabilities.filter { it.supportedClaim != "instrumental-complements" } +
                motion.copy(id = complement.id, unitNumber = 16, objectiveId = "obj-u16-complements", supportedClaim = "instrumental-complements"),
        )
        val foreignErrors = V2Validator.validate(claimComplete(foreign, 16)).errors
        assertTrue(foreignErrors.joinToString("\n"), foreignErrors.any { it.contains("without an admitted instrumental complement span") })
    }

    @Test
    fun partialUnitsStayBlockedAndClosedChainsCanBeReviewPending() {
        val pack = pack()
        listOf(7, 17, 19).forEach { number ->
            val decision = advancementDecision(pack, pack.unit(number)!!, learner(pack, number))
            assertFalse(decision.allowed)
            assertTrue(decision.remaining.any { it.contains("not prepared") })
        }
        listOf(16, 18, 20).forEach { number ->
            assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(number)!!.readiness)
            val decision = advancementDecision(pack, pack.unit(number)!!, learner(pack, number))
            assertTrue(decision.remaining.joinToString("\n"), decision.allowed)
            assertTrue(V2Validator.validate(claimComplete(pack, number)).errors.joinToString("\n"), V2Validator.validate(claimComplete(pack, number)).ok)
        }
        val unit7 = V2Validator.contentCompletenessErrors(pack, pack.unit(7)!!)
        assertTrue(unit7.joinToString("\n"), unit7.any { it.contains("introductory adverb paradigm") })
        val flipped = pack.unit(17)!!.copy(readiness = "CONTENT_COMPLETE_REVIEW_PENDING")
        val stillBlocked = advancementDecision(pack, flipped, learner(pack, 17))
        assertFalse(stillBlocked.allowed)
        assertTrue(stillBlocked.remaining.any { it.contains("productive clock-time and calendar-date") })
        val flipped19 = pack.unit(19)!!.copy(readiness = "CONTENT_COMPLETE_REVIEW_PENDING")
        val probability = advancementDecision(pack, flipped19, learner(pack, 19))
        assertFalse(probability.allowed)
        assertTrue(probability.remaining.any { it.contains("probability source capability") })
    }

    @Test
    fun generatedRussianAndInternetStayClosed() {
        val pack = pack()
        val marked = pack.copy(admissions = pack.admissions.copy(generatedRussianWords = 1))
        assertTrue(V2Validator.validate(marked).errors.any { it.contains("Generated Russian words are forbidden") })
        val item = pack.items.first { it.objectiveId == "obj-u16-complements" }
        val flagged = pack.copy(items = pack.items.map { if (it.id == item.id) it.copy(containsGeneratedRussian = true) else it })
        assertTrue(V2Validator.validate(flagged).errors.any { it.contains("marks generated Russian") })
        val manifest = File(repo(), "app/src/main/AndroidManifest.xml").readText()
        assertFalse(manifest.contains("android.permission.INTERNET"))
        assertTrue(pack.provenanceRecords.none { it.originalSourceText.contains("The New Penguin Russian Course") })
        assertTrue(pack.unitReviews.all { it.reviewer.isNullOrBlank() && it.status == "PENDING_QUALIFIED_REVIEW" })
        assertTrue(pack.ownedEditionMappings.isEmpty())
    }

    private fun replaceCapability(pack: V2CurriculumPack, id: String, mutated: dev.russiancompanion.v2.curriculum.V2SourceCapability) =
        pack.copy(sourceCapabilities = pack.sourceCapabilities.map { if (it.id == id) mutated else it })

    private fun keywordRecord(pack: V2CurriculumPack): V2ProvenanceRecord {
        return pack.provenanceRecords.first().copy(
            itemId = "prov-keyword-calendar",
            unitNumber = 17,
            objectiveId = "obj-u17-calendar-date",
            originalSourceText = "Monday January years old который час o'clock",
            shippedText = "Monday",
        )
    }

    private fun claimComplete(pack: V2CurriculumPack, number: Int) = pack.copy(
        units = pack.units.map { unit ->
            if (unit.number != number) {
                unit
            } else {
                unit.copy(
                    readiness = "CONTENT_COMPLETE_REVIEW_PENDING",
                    sessions = unit.sessions.map { session ->
                        session.copy(
                            steps = session.steps.map { step ->
                                if (step.kind == "UNAVAILABLE" || !step.unavailableReason.isNullOrBlank()) {
                                    step.copy(kind = "ORIENT", unavailableReason = null)
                                } else {
                                    step
                                }
                            },
                        )
                    },
                )
            }
        },
    )

    private fun learner(pack: V2CurriculumPack, number: Int): V2LearnerView {
        val unit = pack.unit(number)!!
        val steps = unit.sessions.flatMap { it.steps }
        return V2LearnerView(
            onboardingCompleted = true,
            currentUnitNumber = number,
            startingPoint = V2StartingPoint.NEW_TO_CYRILLIC,
            hasPenguin = true,
            hasAnki = false,
            hasBinder = false,
            hasTutor = false,
            dailyMinutes = 25,
            transliterationEnabled = true,
            currentSessionId = null,
            currentStepIndex = 0,
            completedStepIds = steps.map { it.id }.toSet(),
            correctItemIds = (unit.objectives.flatMap { it.checkItemIds } + steps.flatMap { it.itemIds }).toSet(),
            bookAttestedUnits = setOf(number),
            completedUnitNumbers = emptySet(),
        )
    }

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
