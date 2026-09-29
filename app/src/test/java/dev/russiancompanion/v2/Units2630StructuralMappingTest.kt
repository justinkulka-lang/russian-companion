package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.V2AdvancementRule
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2Example
import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.curriculum.V2Objective
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2ProvenanceRecord
import dev.russiancompanion.v2.curriculum.V2Session
import dev.russiancompanion.v2.curriculum.V2SourceCapability
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.V2Step
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.advancementDecision
import dev.russiancompanion.v2.curriculum.openable
import dev.russiancompanion.v2.curriculum.unit
import dev.russiancompanion.v2.review.governedMappingFacts
import dev.russiancompanion.v2.review.mappingPackageDigest
import dev.russiancompanion.v2.review.structuralMappingWorksheet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.security.MessageDigest

class Units2630StructuralMappingTest {
    private val pages = mapOf(
        26 to "320-331",
        27 to "332-342",
        28 to "343-354",
        29 to "355-367",
        30 to "368-377",
    )

    private val requiredClaim = mapOf(
        26 to ("obj-u26-indefinite" to "indefinite-pronoun"),
        27 to ("obj-u27-passive" to "passive-participle"),
        28 to ("obj-u28-imperfective-adverb" to "imperfective-verbal-adverb"),
        29 to ("obj-u29-active" to "active-participle"),
        30 to ("obj-u30-dictionary" to "dictionary-use"),
    )

    private val gapMessage = mapOf(
        26 to "Unit 26 is not content-complete without admitted indefinite-pronoun, word-order, postal-address, letter-writing, and correspondence-work source capabilities",
        27 to "Unit 27 is not content-complete without admitted passive-participle, participle-stress, and participle-comprehension source capabilities",
        28 to "Unit 28 is not content-complete without admitted imperfective-verbal-adverb, perfective-verbal-adverb, and verbal-adverb-reading source capabilities",
        29 to "Unit 29 is not content-complete without admitted active-participle, participle-punctuation, short-form-adjective, bookish-register, and bookish-reading source capabilities",
        30 to "Unit 30 is not content-complete without admitted dictionary-use, abbreviation, letter-name-review, particle, and capstone-reading source capabilities",
    )

    private val liveGap = mapOf(
        26 to "Unit 26 is not content-complete without admitted postal-address source capabilities",
        27 to "Unit 27 is not content-complete without admitted participle-stress and participle-comprehension source capabilities",
        29 to "Unit 29 is not content-complete without admitted bookish-register and bookish-reading source capabilities",
        30 to "Unit 30 is not content-complete without admitted dictionary-use source capabilities",
    )

    private val keywordBlob = (
        "\u0441\u0435\u0431\u044f \u0447\u0442\u043e\u0431\u044b indefinite pronoun word order postal address letter writing " +
            "passive participle stress verbal adverb active participle punctuation short adjective bookish " +
            "dictionary abbreviation particle diminutive suffix register scope ago"
        )

    @Test
    fun structuralObservationsStayPendingAndUnsigned() {
        val pack = pack()
        assertTrue(V2Validator.validate(pack).errors.joinToString("\n"), V2Validator.validate(pack).ok)
        pages.forEach { (number, range) ->
            val unit = pack.unit(number)!!
            val facts = governedMappingFacts(number)
            val review = pack.unitReviews.single { it.unitNumber == number }
            assertEquals("DEVELOPMENT", unit.installState)
            assertEquals(
                if (number == 28) "CONTENT_COMPLETE_REVIEW_PENDING" else "NOT_PREPARED",
                unit.readiness,
            )
            assertEquals("PENDING_CURRICULUM_REVIEWER", unit.mappingStatus)
            assertEquals(number, unit.penguinLesson)
            assertTrue(unit.objectives.isNotEmpty())
            assertTrue(unit.sessions.isNotEmpty())
            assertTrue(unit.advancement != null)
            assertTrue(unit.openable())
            assertEquals("IMPLEMENTED_PENDING_FINAL_AUDIT", review.status)
            assertTrue(review.reviewer.isNullOrBlank())
            assertTrue(review.reviewerQualification.isNullOrBlank())
            assertTrue(review.reviewedContentDigest.isNullOrBlank())
            assertTrue(pack.provenanceRecords.any { it.unitNumber == number && it.audioAssetId == null })
            assertTrue(pack.examples.any { it.objectiveId.startsWith("obj-u$number-") })
            assertTrue(pack.items.any { it.objectiveId.startsWith("obj-u$number-") })
            assertFalse(unit.mappingNote.contains(range))
            assertFalse(unit.mappingNote.contains("printed pages"))
            assertTrue(unit.mappingNote.contains("Observed structural scope"))
            if (number in setOf(27, 29, 30)) {
                assertTrue(unit.mappingNote.contains("unresolved authority variance remains unactivated"))
            } else {
                assertFalse(unit.mappingNote.contains("unresolved authority variance"))
            }
            assertTrue(unit.mappingNote.contains(facts.topicDescription!!))
            assertTrue(unit.mappingNote.contains("PENDING_CURRICULUM_REVIEWER"))
            assertFalse(unit.mappingNote.contains("APPROVED"))
            assertFalse(unit.mappingNote.contains("ISBN"))
            assertEquals(range, "${facts.printedPageStart}-${facts.printedPageEnd}")
            assertEquals("PENDING_CURRICULUM_REVIEWER", facts.mappingDisposition)
            assertEquals("UNRESOLVED", facts.isbnObservation)
            assertNull(facts.isbn)
            assertTrue(pack.ownedEditionMappings.none { it.unitNumber == number })
            val decision = advancementDecision(pack, unit, learner(number, book = true))
            if (number == 28) {
                assertFalse(decision.allowed)
                assertFalse(decision.remaining.any { it == "This unit is not prepared" })
                assertFalse(decision.remaining.any { it.contains("not content-complete") || it.contains("verbal-adverb-reading") })
                assertTrue(decision.remaining.any { it.contains("instruction") || it.contains("Demonstrate") })
            } else {
                assertFalse(decision.allowed)
                assertTrue(decision.remaining.any { it == "This unit is not prepared" })
                if (number in liveGap) {
                    assertTrue(decision.remaining.any { it == liveGap.getValue(number) })
                } else {
                    assertFalse(decision.remaining.any { it.contains("not content-complete") })
                }
            }
            if (number in setOf(27, 29, 30)) {
                assertTrue(decision.remaining.any { it == "Unit $number is blocked by an unresolved authority variance" })
            } else {
                assertFalse(decision.remaining.any { it.contains("unresolved authority variance") })
            }
        }
        val unit30 = pack.unit(30)!!
        assertFalse(unit30.mappingNote.contains("Printed page 378"))
        assertFalse(unit30.mappingNote.contains("Letter names are only reviewed and applied here."))
        assertFalse(pack.unit(27)!!.mappingNote.contains("Active participles are not part of this lesson."))
        assertTrue(pack.unit(27)!!.mappingNote.contains("optional present passive material"))
        assertTrue(pack.unit(30)!!.mappingNote.contains("dictionary use"))
    }

    @Test
    fun packagedCurriculumOmitsUnits2630PageRanges() {
        val root = repo()
        val curriculum = File(root, "app/src/main/assets/content/v2-curriculum.json").readText()
        val generator = File(root, "catalog/build_v2_curriculum.py").readText()
        val pack = pack()
        pages.forEach { (number, range) ->
            val unit = pack.unit(number)!!
            assertFalse(curriculum.contains(range))
            assertFalse(generator.contains(range))
            assertFalse(unit.mappingNote.contains(range))
            assertFalse(unit.title.contains(range))
            assertFalse(unit.topicSummary.contains(range))
            val worksheet = File(root, "review/unit-$number/UNIT_${number}_OWNED_EDITION_MAPPING_WORKSHEET.tsv").readText()
            assertTrue(worksheet.contains(range))
            assertEquals(range, "${governedMappingFacts(number).printedPageStart}-${governedMappingFacts(number).printedPageEnd}")
        }
        assertFalse(curriculum.contains("Printed page 378"))
        assertFalse(generator.contains("Printed page 378"))
        assertFalse(curriculum.contains("Active participles are not part of this lesson."))
        assertFalse(curriculum.contains("Letter names are only reviewed and applied here."))
    }

    @Test
    fun mappingWorksheetsBindStructuralFactsAndStayUnresolved() {
        val pack = pack()
        pages.keys.forEach { number ->
            val rendered = structuralMappingWorksheet(pack, number)
            val dir = File(repo(), "review/unit-$number")
            val file = File(dir, "UNIT_${number}_OWNED_EDITION_MAPPING_WORKSHEET.tsv")
            if (System.getenv("WRITE_STRUCTURAL_MAPPING") == "1") {
                dir.mkdirs()
                file.writeText(rendered)
            }
            assertEquals(rendered, file.readText())
            assertTrue(File(dir, "UNIT_${number}_REVIEW_PACKET.md").exists())
            assertTrue(File(dir, "UNIT_${number}_REVIEW_MANIFEST.json").exists())
            assertTrue(File(dir, "UNIT_${number}_REVIEW_WORKSHEET.tsv").exists())
            assertTrue(File(dir, "REVIEWER_INSTRUCTIONS.md").exists())
            val packet = File(dir, "UNIT_${number}_REVIEW_PACKET.md").readText()
            assertTrue(packet.contains("Unsupported-capability register"))
            assertTrue(packet.contains("Book-directed register"))
            assertTrue(packet.contains("Optional non-gating register"))
            assertTrue(packet.contains("No reviewer has signed this packet."))
            val facts = governedMappingFacts(number)
            val digest = mappingPackageDigest(facts)
            assertTrue(rendered.contains(pages.getValue(number)))
            assertTrue(rendered.contains("mappingPackageDigest: $digest"))
            assertTrue(rendered.contains("PENDING_CURRICULUM_REVIEWER"))
            assertTrue(rendered.contains("not a review packet"))
            assertFalse(rendered.contains("\tAPPROVED\t"))
            val row = rendered.lines().first { it.startsWith("obj-u$number-") || it.startsWith("unit-$number\t") }.split("\t")
            listOf(9, 10, 18, 19, 20, 21, 22, 23).forEach { index ->
                assertEquals("column $index", "", row[index])
            }
            assertNotEquals(digest, mappingPackageDigest(facts.copy(printedPageStart = facts.printedPageStart!! + 1)))
        }
    }

    @Test
    fun unresolvedVarianceBlocksCompletionWithoutChoosingTheProposal() {
        val pack = pack()
        val proposal = File(repo(), "docs/governance/UNITS_26_30_AUTHORITY_VARIANCE_PROPOSAL.md").readText()
        assertTrue(proposal.contains("Status: PROPOSED_NOT_ACTIVATED"))
        assertFalse(proposal.contains("Status: ACTIVATED"))
        assertTrue(proposal.contains("Letter names are introduced in Lesson 1"))
        assertTrue(proposal.contains("Active participles first appear in Lesson 29"))
        assertTrue(proposal.contains("Printed page 378 begins the Grammatical Tables."))
        assertTrue(proposal.contains("does not choose the letter-name or active-participle resolutions"))
        pages.keys.forEach { number ->
            val active = V2Validator.validate(withClaim(pack, number, "active-participle")).errors
            assertFalse(active.joinToString("\n"), active.any { it.contains("cannot contain active-participle instruction") })
            val letters = V2Validator.validate(withClaim(pack, number, "letter-name-introduction")).errors
            assertFalse(letters.joinToString("\n"), letters.any { it.contains("cannot introduce letter names") })
        }
        val unitOne = V2Validator.validate(withClaim(pack, 1, "letter-name-introduction")).errors
        assertFalse(unitOne.any { it.contains("cannot introduce letter names") })
        assertFalse(unitOne.any { it.contains("unresolved authority variance") })
        assertFalse(V2Validator.validate(pack).errors.any { it.contains("unresolved authority variance") })
        listOf(27, 29, 30).forEach { number ->
            val message = "Unit $number is blocked by an unresolved authority variance"
            val complete = V2Validator.validate(claimComplete(pack, number)).errors
            assertTrue(complete.joinToString("\n"), complete.any { it == message })
            val installable = V2Validator.validate(claimInstallable(pack, number)).errors
            assertTrue(installable.joinToString("\n"), installable.any { it == message })
            val installed = V2Validator.validate(claimInstalled(pack, number)).errors
            assertTrue(installed.joinToString("\n"), installed.any { it == message })
        }
        val unit26 = V2Validator.validate(claimComplete(pack, 26)).errors
        assertFalse(unit26.joinToString("\n"), unit26.any { it.contains("unresolved authority variance") })
        assertTrue(unit26.any { it == liveGap.getValue(26) })
        val unit28 = V2Validator.validate(claimComplete(pack, 28)).errors
        assertFalse(unit28.joinToString("\n"), unit28.any { it.contains("unresolved authority variance") })
        assertFalse(unit28.any { it.contains("verbal-adverb-reading") })
        assertTrue(unit28.joinToString("\n"), unit28.isEmpty())
    }

    @Test
    fun emptyUnitsCannotBecomeContentCompleteOrAdvance() {
        val pack = pack()
        pages.keys.forEach { number ->
            val claimed = claimComplete(pack, number)
            val errors = V2Validator.validate(claimed).errors
            if (number in liveGap) {
                assertTrue(errors.joinToString("\n"), errors.any { it == liveGap.getValue(number) })
            } else {
                assertFalse(errors.joinToString("\n"), errors.any { it.contains("verbal-adverb-reading") || it.contains("not content-complete") })
            }
            val bookDirected = bookShell(claimed, number)
            val bookErrors = V2Validator.validate(bookDirected).errors
            assertTrue(bookErrors.joinToString("\n"), bookErrors.any { it == gapMessage.getValue(number) })
            val decision = advancementDecision(bookDirected, bookDirected.unit(number)!!, learner(number, book = true))
            assertFalse(decision.allowed)
            assertFalse(decision.remaining.any { it == "This unit is not prepared" })
            assertTrue(decision.remaining.any { it == gapMessage.getValue(number) })
        }
    }

    @Test
    fun pageRangeBookAttestationAndEmptyContentCannotAdvance() {
        val pack = pack()
        pages.forEach { (number, range) ->
            val empty = permissiveAdvancement(pack, number, "CONTENT_COMPLETE_REVIEW_PENDING").let { current ->
                current.copy(
                    units = current.units.map { unit ->
                        if (unit.number != number) unit else unit.copy(objectives = emptyList(), sessions = emptyList())
                    },
                )
            }
            assertTrue(empty.unit(number)!!.objectives.isEmpty())
            assertTrue(empty.unit(number)!!.sessions.isEmpty())
            val emptyDecision = advancementDecision(empty, empty.unit(number)!!, learner(number, book = true))
            assertFalse(emptyDecision.allowed)
            assertTrue(emptyDecision.remaining.joinToString("\n"), emptyDecision.remaining.any { it == gapMessage.getValue(number) })
            assertFalse(emptyDecision.remaining.any { it == "This unit is not prepared" })

            val attested = bookShell(claimComplete(pack, number), number)
            val attestedDecision = advancementDecision(attested, attested.unit(number)!!, learner(number, book = true))
            assertFalse(attestedDecision.allowed)
            assertTrue(attestedDecision.remaining.any { it == gapMessage.getValue(number) })
            assertFalse(attestedDecision.remaining.any { it == "Self-attest the book work for this unit" })

            val required = requiredClaim.getValue(number)
            val ranged = permissiveAdvancement(
                capabilityOn(
                    pack,
                    number,
                    range,
                    "cap-pages-$number",
                    required.first,
                    required.second,
                    "4220315",
                    oldidUrl(),
                ),
                number,
                "CONTENT_COMPLETE_REVIEW_PENDING",
            )
            val rangedDecision = advancementDecision(ranged, ranged.unit(number)!!, learner(number, book = true))
            val rangedErrors = V2Validator.validate(ranged).errors
            assertTrue(rangedErrors.joinToString("\n"), rangedErrors.any { it.contains("structural page range is not linguistic evidence") })
            if (number == 28) {
                assertTrue(rangedDecision.remaining.joinToString("\n"), rangedDecision.allowed)
            } else {
                assertFalse(rangedDecision.allowed)
                assertTrue(rangedDecision.remaining.joinToString("\n"), rangedDecision.remaining.any { it == liveGap.getValue(number) })
            }

            if (number in setOf(27, 29, 30)) {
                val variance = "Unit $number is blocked by an unresolved authority variance"
                assertTrue(emptyDecision.remaining.any { it == variance })
                assertTrue(attestedDecision.remaining.any { it == variance })
                assertTrue(rangedDecision.remaining.any { it == variance })
            } else {
                assertFalse(emptyDecision.remaining.any { it.contains("unresolved authority variance") })
                assertFalse(attestedDecision.remaining.any { it.contains("unresolved authority variance") })
                assertFalse(rangedDecision.remaining.any { it.contains("unresolved authority variance") })
            }
        }
    }

    @Test
    fun preparedLateUnitIsNotPermanentlyBlockedFromAdvancement() {
        val pack = pack()
        pages.keys.forEach { number ->
            val unprepared = permissiveAdvancement(pack, number, "NOT_PREPARED")
            val blocked = advancementDecision(unprepared, unprepared.unit(number)!!, learner(number, book = true))
            assertFalse(blocked.allowed)
            assertTrue(blocked.remaining.any { it == "This unit is not prepared" })
            if (number in liveGap) {
                assertTrue(blocked.remaining.any { it == liveGap.getValue(number) })
            } else {
                assertFalse(blocked.remaining.any { it.contains("not content-complete") })
            }
            listOf("CONTENT_COMPLETE_REVIEW_PENDING", "INSTALLABLE").forEach { readiness ->
                val prepared = permissiveAdvancement(pack, number, readiness)
                val decision = advancementDecision(prepared, prepared.unit(number)!!, learner(number, book = false))
                assertFalse(decision.remaining.any { it == "This unit is not prepared" })
                if (number == 28) {
                    assertTrue("$number $readiness ${decision.remaining}", decision.allowed)
                } else {
                    assertEquals(
                        blocked.remaining.filter { it != "This unit is not prepared" },
                        decision.remaining,
                    )
                    assertFalse("$number $readiness ${decision.remaining}", decision.allowed)
                    if (number in liveGap) {
                        assertTrue(decision.remaining.any { it == liveGap.getValue(number) })
                    }
                }
                if (number in setOf(27, 29, 30)) {
                    assertTrue(decision.remaining.any { it == "Unit $number is blocked by an unresolved authority variance" })
                } else {
                    assertFalse(decision.remaining.any { it.contains("unresolved authority variance") })
                }
            }
        }
    }

    @Test
    fun pageRangesKeywordsAndMutablePagesDoNotClearBlockers() {
        val pack = pack()
        val pageRange = capabilityOn(pack, 26, "320-331", "cap-pages", "obj-u26-indefinite", "indefinite-pronoun", "4220315", oldidUrl())
        val pageErrors = V2Validator.validate(claimComplete(pageRange, 26)).errors
        assertTrue(pageErrors.joinToString("\n"), pageErrors.any { it.contains("structural page range is not linguistic evidence") })
        assertTrue(pageErrors.any { it == liveGap.getValue(26) })

        val keywords = capabilityOn(pack, 26, keywordBlob, "cap-keywords", "obj-u26-word-order", "word-order", "4220315", oldidUrl())
        val keywordErrors = V2Validator.validate(claimComplete(keywords, 26)).errors
        assertTrue(keywordErrors.joinToString("\n"), keywordErrors.any { it.contains("is not an admitted frozen source") })
        assertTrue(keywordErrors.any { it == liveGap.getValue(26) })
        assertFalse(keywordErrors.any { it.contains("keyword match admits") })

        val mutable = capabilityOn(
            pack,
            29,
            "active participle formation notes",
            "cap-mutable",
            "obj-u29-active",
            "active-participle",
            "current",
            "https://en.wikipedia.org/wiki/Russian_grammar",
        )
        val mutableErrors = V2Validator.validate(claimComplete(mutable, 29)).errors
        assertTrue(mutableErrors.joinToString("\n"), mutableErrors.any { it.contains("mutable webpage cannot clear a source blocker") })
        assertTrue(mutableErrors.any { it == liveGap.getValue(29) })

        val forgedHost = capabilityOn(pack, 28, "verbal adverb formation", "cap-forged", "obj-u28-imperfective-adverb", "imperfective-verbal-adverb", "4220315", oldidUrl())
        val forged = forgedHost.copy(
            sourceCapabilities = forgedHost.sourceCapabilities.map { capability ->
                if (capability.id == "cap-forged") capability.copy(sourceRevision = "9999999") else capability
            },
        )
        val forgedErrors = V2Validator.validate(forged).errors
        assertTrue(forgedErrors.joinToString("\n"), forgedErrors.any { it.contains("cap-forged revision does not match the frozen record") })

        val mixedHost = capabilityOn(pack, 26, "letter writing conventions", "cap-mixed", "obj-u26-letter-writing", "letter-writing", "4220315", oldidUrl())
        val mixed = mixedHost.copy(
            sourceCapabilities = mixedHost.sourceCapabilities.map { capability ->
                if (capability.id == "cap-mixed") capability.copy(exactSpan = "320-331") else capability
            },
        )
        val mixedErrors = V2Validator.validate(mixed).errors
        assertTrue(mixedErrors.joinToString("\n"), mixedErrors.any { it.contains("cap-mixed span is not in the frozen record") })

        val wrong = capabilityOn(pack, 26, "postal address formatting", "cap-wrong", "obj-u27-passive", "passive-participle", "4220315", oldidUrl())
        val wrongErrors = V2Validator.validate(wrong).errors
        assertTrue(wrongErrors.joinToString("\n"), wrongErrors.any { it.contains("cap-wrong is attached to the wrong unit or objective") })
    }

    @Test
    fun russianWithoutProvenanceIsRejectedAndPenguinStaysOut() {
        val pack = pack()
        val russian = "\u0442\u0435\u0441\u0442"
        val objective = V2Objective(
            id = "obj-u26-probe",
            unitNumber = 26,
            title = "Probe",
            kind = "RECOGNITION",
        )
        val example = V2Example(
            id = "ex-u26-probe",
            kind = "LEXICAL",
            letterId = "",
            display = russian,
            note = "Probe",
            sourceId = pack.sources.first().id,
            reviewStatus = "IMPLEMENTED_PENDING_FINAL_AUDIT",
            objectiveId = objective.id,
        )
        val withExample = pack.copy(
            examples = pack.examples + example,
            units = pack.units.map { unit ->
                if (unit.number != 26) unit else unit.copy(objectives = listOf(objective))
            },
        )
        val errors = V2Validator.validate(withExample).errors
        assertTrue(errors.joinToString("\n"), errors.any { it == "Unit 26 Russian text has no resolved provenance record" })
        val root = repo()
        val listed = ProcessBuilder(listOf("git", "ls-files", "*.pdf", "*.PDF"))
            .directory(root)
            .redirectError(File("/dev/null"))
            .start()
            .let { process ->
                val text = process.inputStream.bufferedReader().readText()
                check(process.waitFor() == 0)
                text
            }
        assertEquals("", listed.trim())
        val curriculum = File(root, "app/src/main/assets/content/v2-curriculum.json").readText()
        assertFalse(curriculum.contains("The New Penguin Russian Course"))
        assertFalse(curriculum.contains(".pdf"))
        assertEquals(0, pack.admissions.penguinProseAssets)
        assertEquals(0, pack.admissions.generatedRussianWords)
        val proposal = File(root, "docs/governance/UNITS_26_30_AUTHORITY_VARIANCE_PROPOSAL.md").readText()
        assertTrue(proposal.contains("Status: PROPOSED_NOT_ACTIVATED"))
        assertFalse(proposal.contains("Status: ACTIVATED"))
        val authority = File(root, "docs/governance/MASTER_AUTHORITY_v2.0.md").readBytes()
        assertEquals("bb4932783224e85d60b130118191185c2e26d224254a0a54a2c52cfc92b4aaaf", sha256(authority))
        val candidate = File(root, "release/acceptance/candidate.json").readText()
        assertTrue(candidate.contains("\"publicRelease\": \"NOT_APPROVED\""))
        assertTrue(candidate.contains("\"frozen\": false"))
        assertTrue(candidate.contains("\"sourceCommit\": null"))
        assertTrue(candidate.contains("AT-031"))
    }

    private fun withClaim(pack: V2CurriculumPack, number: Int, claim: String): V2CurriculumPack {
        val objectiveId = "obj-u$number-$claim"
        val record = sample(pack, number, "4220315", "src-wikibooks-russian-cc-by-sa", claim)
        return pack.copy(
            provenanceRecords = pack.provenanceRecords + record,
            sourceCapabilities = pack.sourceCapabilities + V2SourceCapability(
                id = "cap-$number-$claim",
                sourceId = record.sourceId,
                sourceRevision = record.sourceRevision,
                sourceRecordId = record.sourceRecordId,
                sourceHash = record.sourceHash,
                exactSpan = record.originalSourceText,
                unitNumber = number,
                objectiveId = objectiveId,
                supportedClaim = claim,
            ),
            units = pack.units.map { unit ->
                if (unit.number != number) {
                    unit
                } else {
                    unit.copy(
                        objectives = unit.objectives + V2Objective(
                            id = objectiveId,
                            unitNumber = number,
                            title = claim,
                            kind = "GUIDANCE",
                        ),
                    )
                }
            },
        )
    }

    private fun capabilityOn(
        pack: V2CurriculumPack,
        number: Int,
        text: String,
        id: String,
        objectiveId: String,
        claim: String,
        revision: String,
        sourceUrl: String,
    ): V2CurriculumPack {
        val record = sample(pack, number, revision, "src-wikibooks-russian-cc-by-sa", text).copy(
            itemId = "prov-u2630-$id",
            objectiveId = objectiveId,
            sourceUrl = sourceUrl,
            sourceRecordId = "probe:$id",
        )
        return pack.copy(
            provenanceRecords = pack.provenanceRecords + record,
            sourceCapabilities = pack.sourceCapabilities + V2SourceCapability(
                id = id,
                sourceId = record.sourceId,
                sourceRevision = record.sourceRevision,
                sourceRecordId = record.sourceRecordId,
                sourceHash = record.sourceHash,
                exactSpan = record.originalSourceText,
                unitNumber = number,
                objectiveId = objectiveId,
                supportedClaim = claim,
            ),
        )
    }

    private fun bookShell(pack: V2CurriculumPack, number: Int): V2CurriculumPack {
        val objective = V2Objective(
            id = "obj-u$number-book",
            unitNumber = number,
            title = "Self-attest Lesson $number book work",
            kind = "SELF_ATTESTATION",
            instructionStepIds = listOf("u$number-st-book"),
        )
        val session = V2Session(
            id = "u$number-s-book",
            title = "Self-attest Lesson $number",
            estimatedMinutes = 5,
            objectiveIds = listOf(objective.id),
            steps = listOf(
                V2Step(
                    id = "u$number-st-book",
                    kind = "BOOK",
                    title = "Work Lesson $number in your copy",
                    body = "Do Lesson $number in the copy you own.",
                    bookDirection = "Open Lesson $number in the copy you own.",
                ),
            ),
        )
        return pack.copy(
            units = pack.units.map { unit ->
                if (unit.number != number) {
                    unit
                } else {
                    unit.copy(
                        installState = "DEVELOPMENT",
                        objectives = listOf(objective),
                        sessions = listOf(session),
                        advancement = V2AdvancementRule(
                            requireInstruction = false,
                            requireBookSelfAttestation = true,
                            requireAppChecks = false,
                            minimumDistinctChecks = 0,
                            requireDueReview = false,
                            tutorOptional = true,
                        ),
                    )
                }
            },
        )
    }

    private fun claimComplete(pack: V2CurriculumPack, number: Int) = pack.copy(
        units = pack.units.map { unit ->
            if (unit.number != number) unit else unit.copy(readiness = "CONTENT_COMPLETE_REVIEW_PENDING")
        },
    )

    private fun claimInstallable(pack: V2CurriculumPack, number: Int) = pack.copy(
        units = pack.units.map { unit ->
            if (unit.number != number) unit else unit.copy(readiness = "INSTALLABLE")
        },
    )

    private fun claimInstalled(pack: V2CurriculumPack, number: Int) = pack.copy(
        units = pack.units.map { unit ->
            if (unit.number != number) unit else unit.copy(installState = "INSTALLED", readiness = "INSTALLABLE")
        },
    )

    private fun permissiveAdvancement(pack: V2CurriculumPack, number: Int, readiness: String) = pack.copy(
        units = pack.units.map { unit ->
            if (unit.number != number) {
                unit
            } else {
                unit.copy(
                    readiness = readiness,
                    advancement = V2AdvancementRule(
                        requireInstruction = false,
                        requireBookSelfAttestation = false,
                        requireAppChecks = false,
                        minimumDistinctChecks = 0,
                        requireDueReview = false,
                        tutorOptional = true,
                    ),
                )
            }
        },
    )

    private fun sample(pack: V2CurriculumPack, number: Int, revision: String, sourceId: String, text: String): V2ProvenanceRecord {
        return pack.provenanceRecords.first().copy(
            itemId = "prov-u2630-$number",
            unitNumber = number,
            objectiveId = "obj-u$number-probe",
            sourceRevision = revision,
            sourceId = sourceId,
            originalSourceText = text,
            shippedText = text,
            sourceHash = sha256(text),
            contentHash = sha256(text),
            sourceUrl = oldidUrl(),
        )
    }

    private fun oldidUrl() = "https://en.wikibooks.org/wiki/Russian/Possessive_Pronouns?oldid=4220315"

    private fun learner(number: Int, book: Boolean) = V2LearnerView(
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
        completedStepIds = emptySet(),
        correctItemIds = emptySet(),
        bookAttestedUnits = if (book) setOf(number) else emptySet(),
        completedUnitNumbers = emptySet(),
    )

    private fun sha256(text: String): String = sha256(text.toByteArray(Charsets.UTF_8))

    private fun sha256(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        return digest.joinToString("") { byte -> "%02x".format(byte) }
    }

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
