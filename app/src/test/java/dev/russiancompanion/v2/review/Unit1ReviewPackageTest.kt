package dev.russiancompanion.v2.review

import dev.russiancompanion.release.ReleaseDecision
import dev.russiancompanion.release.assessRepositoryRelease
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.unit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class Unit1ReviewPackageTest {
    @Test
    fun committedPackageMatchesTheCurriculumTraversal() {
        val pack = pack()
        val rendered = renderUnit1ReviewFiles(pack)
        val dir = File(repo(), "review/unit-1")
        if (System.getenv("WRITE_UNIT1_REVIEW") == "1" || System.getenv("WRITE_UNIT_REVIEW") == "1") {
            dir.mkdirs()
            rendered.files.forEach { (name, text) -> File(dir, name).writeText(text) }
        }
        val committed = UNIT1_REVIEW_FILE_NAMES.associateWith { name -> File(dir, name).readText() }
        val errors = validateUnit1ReviewFiles(pack, committed)
        assertTrue(errors.joinToString("\n"), errors.isEmpty())
        assertEquals(rendered.files, committed)
        assertEquals(rendered.files, renderUnit1ReviewFiles(pack).files)
    }

    @Test
    fun packageDigestIsTheInstructionalContentDigest() {
        val pack = pack()
        val review = buildUnit1ReviewPackage(pack)
        assertEquals(V2Validator.unitContentReviewDigest(pack, 1), review.contentReviewDigest)
        assertEquals(V2Validator.contentReviewDigest(pack), review.packContentReviewDigest)
        assertNotEquals(review.contentReviewDigest, review.packContentReviewDigest)
        assertNotEquals(review.contentReviewDigest, review.reviewPackageDigest)
        assertEquals(repositorySourceDigest(), review.repositorySourceDigest)
        assertTrue(review.edges.any { it.kind == "INSTRUCTION" })
        assertTrue(review.edges.any { it.kind == "REMEDIATION" })
        assertTrue(review.edges.any { it.kind == "REFERENCE" })
        assertTrue(review.edges.any { it.kind == "EXAMPLE" })
        assertTrue(review.unresolved.isEmpty())
        val unit = pack.unit(1)!!
        val ids = review.records.map { it.id }
        assertEquals(ids, ids.distinct())
        unit.objectives.forEach { assertTrue(ids.contains(it.id)) }
        unit.sessions.forEach { session ->
            assertTrue(ids.contains(session.id))
            session.steps.forEach { step ->
                assertTrue(ids.contains(step.id))
                step.exampleIds.forEach { assertTrue(ids.contains(it)) }
                step.itemIds.forEach { assertTrue(ids.contains(it)) }
                step.referenceIds.forEach { assertTrue(ids.contains(it)) }
            }
        }
        unit.objectives.flatMap { it.exampleIds }.forEach { exampleId ->
            val letterId = pack.examples.first { it.id == exampleId }.letterId
            assertTrue(ids.contains(letterId))
        }
        assertEquals("DEVELOPMENT", unit.installState)
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", unit.readiness)
        assertEquals("PENDING_QUALIFIED_REVIEW", pack.linguisticReview.status)
        assertEquals("PENDING_QUALIFIED_REVIEW", pack.unitReviews.single { it.unitNumber == 1 }.status)
        assertEquals("PENDING_CURRICULUM_REVIEWER", unit.mappingStatus)
        assertTrue(review.records.any { it.type == "UNIT_REVIEW" && it.id == "rev-pending-unit-1" })
        assertTrue(review.records.none { it.type == "PACK_REVIEW" })
    }

    @Test
    fun reviewerAndMappingFieldsStayBlank() {
        val pack = pack()
        val files = renderUnit1ReviewFiles(pack).files
        val worksheet = parseTsv(files.getValue(UNIT1_WORKSHEET_NAME))
        worksheet.drop(1).forEach { row ->
            assertEquals(16, row.size)
            (10 until row.size).forEach { index -> assertEquals("", row[index]) }
        }
        val mappingRows = parseTsv(files.getValue(UNIT1_MAPPING_NAME))
        val mappingHeader = mappingRows.first()
        mappingRows.drop(1).forEach { row ->
            listOf(
                "isbn",
                "owned_copy_verified",
                "variance_conflict",
                "reviewer_notes",
                "reviewer_identity",
                "review_date",
                "reviewed_version_digest",
                "reviewer_qualification",
            ).forEach { column ->
                assertEquals(column, "", row[mappingHeader.indexOf(column)])
            }
            assertEquals("PENDING_CURRICULUM_REVIEWER", row[mappingHeader.indexOf("alignment_disposition")])
            assertEquals("Lesson 1", row[mappingHeader.indexOf("corresponding_lesson")])
            assertEquals("1-11", row[mappingHeader.indexOf("corresponding_pages")])
        }
        assertFalse(files.getValue(UNIT1_PACKET_NAME).contains("The New Penguin Russian Course"))
        assertFalse(files.values.joinToString("\n").contains("Qualified Reviewer"))
        assertEquals("DEVELOPMENT", pack.unit(1)!!.installState)
    }

    @Test
    fun validatorRejectsOmissionsDuplicatesBrokenLinksAndFabrication() {
        val pack = pack()
        val files = renderUnit1ReviewFiles(pack).files.toMutableMap()
        val manifest = files.getValue(UNIT1_MANIFEST_NAME)
        val omitted = manifest.replaceFirst(Regex("\\n    \\{\"id\": \"[^\"]+\".*\\},?\\n"), "\n")
        assertTrue(validateUnit1ReviewFiles(pack, files + (UNIT1_MANIFEST_NAME to omitted)).any { it.contains("do not match the Unit 1 traversal") || it.contains("omitted") })

        val duplicated = manifest.replaceFirst("\"records\": [", "\"records\": [\n    {\"id\": \"unit-1\", \"type\": \"UNIT\", \"contentHash\": \"abc\", \"sourceId\": \"\", \"rightsIds\": [], \"reviewStatus\": \"NO_SEPARATE_REVIEW_FIELD\"},")
        assertTrue(validateUnit1ReviewFiles(pack, files + (UNIT1_MANIFEST_NAME to duplicated)).any { it.contains("repeats a stable id") })

        val uniqueRecordHash = Regex("\"records\": \\[\\s*\\{\"id\": \"[^\"]+\".*?\"contentHash\": \"([0-9a-f]{64})\"")
            .find(manifest)!!
            .groupValues[1]
        val badHash = manifest.replace(uniqueRecordHash, "0".repeat(64))
        assertTrue(validateUnit1ReviewFiles(pack, files + (UNIT1_MANIFEST_NAME to badHash)).any { it.contains("does not match") })

        val badDigest = manifest.replace(buildUnit1ReviewPackage(pack).contentReviewDigest, "1".repeat(64))
        assertTrue(validateUnit1ReviewFiles(pack, files + (UNIT1_MANIFEST_NAME to badDigest)).any { it.contains("Content-review digest does not match") })

        val badPackage = manifest.replace(buildUnit1ReviewPackage(pack).reviewPackageDigest, "2".repeat(64))
        assertTrue(validateUnit1ReviewFiles(pack, files + (UNIT1_MANIFEST_NAME to badPackage)).any { it.contains("Review package digest does not match") })

        val approved = files.getValue(UNIT1_WORKSHEET_NAME).replaceFirst(
            Regex("(\t[0-9a-f]{64})\t\t\t\t\t\t"),
            "$1\tAPPROVED\t\t\t\t\t",
        )
        assertTrue(validateUnit1ReviewFiles(pack, files + (UNIT1_WORKSHEET_NAME to approved)).any { it.contains("pre-populates a reviewer field") || it.contains("disposition") })

        val named = files.getValue(UNIT1_INSTRUCTIONS_NAME) + "\nQualified Reviewer\n"
        assertTrue(validateUnit1ReviewFiles(pack, files + (UNIT1_INSTRUCTIONS_NAME to named)).any { it.contains("fabricates a reviewer identity") })

        val aligned = files.getValue(UNIT1_PACKET_NAME).replace("not an approved edition alignment", "Penguin-aligned")
        assertTrue(validateUnit1ReviewFiles(pack, files + (UNIT1_PACKET_NAME to aligned)).any { it.contains("Penguin-aligned") })

        val prose = files.getValue(UNIT1_PACKET_NAME) + "\nThe New Penguin Russian Course\n"
        assertTrue(validateUnit1ReviewFiles(pack, files + (UNIT1_PACKET_NAME to prose)).any { it.contains("Penguin prose") })

        val mapping = files.getValue(UNIT1_MAPPING_NAME)
        val inventedIsbn = mapping.replace("\t\tLesson 1\t", "\t9780140120417\t\tLesson 1\t")
        assertTrue(validateUnit1ReviewFiles(pack, files + (UNIT1_MAPPING_NAME to inventedIsbn)).any { it.contains("invented ISBN") })
        val wrongLesson = mapping.replace("Lesson 1", "Lesson 9")
        assertTrue(validateUnit1ReviewFiles(pack, files + (UNIT1_MAPPING_NAME to wrongLesson)).any { it.contains("wrong unit or lesson") })
        val approvedBlankPages = mapping.replace("PENDING_CURRICULUM_REVIEWER", "APPROVED").replace("1-11", "")
        val approvedErrors = validateUnit1ReviewFiles(pack, files + (UNIT1_MAPPING_NAME to approvedBlankPages))
        assertTrue(approvedErrors.joinToString("\n"), approvedErrors.any { it.contains("not pending") || it.contains("blank page range") })
        assertTrue(approvedErrors.any { it.contains("without an identified qualified reviewer") })
        val redistributed = mapping + "\nPENGUIN_EDITION_MAPPING_INPUT.pdf\n"
        assertTrue(validateUnit1ReviewFiles(pack, files + (UNIT1_MAPPING_NAME to redistributed)).any { it.contains("redistributable curriculum content") })

        val installed = manifest.replace("\"installState\": \"DEVELOPMENT\"", "\"installState\": \"INSTALLED\"")
        assertTrue(validateUnit1ReviewFiles(pack, files + (UNIT1_MANIFEST_NAME to installed)).any { it.contains("installed") })

        val broken = pack.copy(
            units = pack.units.map { unit ->
                if (unit.number != 1 || unit.sessions.isEmpty()) unit else {
                    val session = unit.sessions.first()
                    val step = session.steps.first()
                    val brokenStep = step.copy(referenceIds = step.referenceIds + "ref-missing")
                    unit.copy(sessions = listOf(session.copy(steps = listOf(brokenStep) + session.steps.drop(1))) + unit.sessions.drop(1))
                }
            },
        )
        assertTrue(buildUnit1ReviewPackage(broken).unresolved.any { it.contains("ref-missing") && it.contains("cannot be resolved") })
        assertEquals("DEVELOPMENT", pack.unit(1)!!.installState)
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(1)!!.readiness)
    }

    @Test
    fun reviewPackageDoesNotBypassTheReleaseGate() {
        val assessment = assessRepositoryRelease(repo())
        assertEquals(ReleaseDecision.NOT_APPROVED, assessment.decision)
        assertTrue(assessment.blockers.isNotEmpty())
        val candidate = File(repo(), "release/acceptance/candidate.json").readText()
        assertTrue(candidate.contains("\"publicRelease\": \"NOT_APPROVED\""))
        assertTrue(candidate.contains("\"frozen\": false"))
        assertFalse(File(repo(), "app/src/main/AndroidManifest.xml").readText().contains("INTERNET"))
        assertEquals("DEVELOPMENT", pack().unit(1)!!.installState)
        assertEquals("DEVELOPMENT", pack().unit(2)!!.installState)
        assertTrue(pack().units.drop(5).all { it.installState == "NOT_YET_INSTALLED" })
    }

    @Test
    fun instructionalDigestChangesForEachGovernedFieldCategory() {
        val pack = pack()
        val baseline = V2Validator.contentReviewDigest(pack)
        val letter = pack.letters.first()
        val example = pack.examples.first()
        val item = pack.items.first()
        val reference = pack.references.first()
        val source = pack.sources.first()
        val rights = pack.rights.first()
        val mutations = listOf(
            "letter note" to pack.copy(letters = pack.letters.map { if (it.id == letter.id) it.copy(englishNote = it.englishNote + " x") else it }),
            "letter source" to pack.copy(letters = pack.letters.map { if (it.id == letter.id) it.copy(sourceId = it.sourceId + "-x") else it }),
            "letter audio" to pack.copy(letters = pack.letters.map { if (it.id == letter.id) it.copy(audioAssetId = "audio-x") else it }),
            "letter example word" to pack.copy(letters = pack.letters.map { if (it.id == letter.id) it.copy(exampleWordId = "word-x") else it }),
            "letter lookalike" to pack.copy(letters = pack.letters.map { if (it.id == letter.id) it.copy(latinLookalike = (it.latinLookalike ?: "") + "Z") else it }),
            "example note" to pack.copy(examples = pack.examples.map { if (it.id == example.id) it.copy(note = it.note + " x") else it }),
            "example source" to pack.copy(examples = pack.examples.map { if (it.id == example.id) it.copy(sourceId = it.sourceId + "-x") else it }),
            "check options" to pack.copy(items = pack.items.map { if (it.id == item.id) it.copy(options = it.options + "extra") else it }),
            "check feedback" to pack.copy(items = pack.items.map { if (it.id == item.id) it.copy(feedback = it.feedback + " x") else it }),
            "check prompt letter" to pack.copy(items = pack.items.map { if (it.id == item.id) it.copy(promptLetter = (it.promptLetter ?: "") + "x") else it }),
            "check source" to pack.copy(items = pack.items.map { if (it.id == item.id) it.copy(sourceId = it.sourceId + "-x") else it }),
            "reference title" to pack.copy(references = pack.references.map { if (it.id == reference.id) it.copy(title = it.title + " x") else it }),
            "reference summary" to pack.copy(references = pack.references.map { if (it.id == reference.id) it.copy(summary = it.summary + " x") else it }),
            "reference detail" to pack.copy(references = pack.references.map { if (it.id == reference.id) it.copy(detail = it.detail + " x") else it }),
            "reference source" to pack.copy(references = pack.references.map { if (it.id == reference.id) it.copy(sourceId = it.sourceId + "-x") else it }),
            "reference search" to pack.copy(references = pack.references.map { if (it.id == reference.id) it.copy(searchTerms = it.searchTerms + "extra") else it }),
            "objective title" to mapUnit(pack) { unit -> unit.copy(objectives = unit.objectives.mapIndexed { index, objective -> if (index == 0) objective.copy(title = objective.title + " x") else objective }) },
            "objective kind" to mapUnit(pack) { unit -> unit.copy(objectives = unit.objectives.mapIndexed { index, objective -> if (index == 0) objective.copy(kind = objective.kind + "_X") else objective }) },
            "objective instruction" to mapUnit(pack) { unit -> unit.copy(objectives = unit.objectives.mapIndexed { index, objective -> if (index == 0) objective.copy(instructionStepIds = objective.instructionStepIds + "step-x") else objective }) },
            "session title" to mapUnit(pack) { unit -> unit.copy(sessions = unit.sessions.mapIndexed { index, session -> if (index == 0) session.copy(title = session.title + " x") else session }) },
            "session effort" to mapUnit(pack) { unit -> unit.copy(sessions = unit.sessions.mapIndexed { index, session -> if (index == 0) session.copy(estimatedMinutes = session.estimatedMinutes + 1) else session }) },
            "session objectives" to mapUnit(pack) { unit -> unit.copy(sessions = unit.sessions.mapIndexed { index, session -> if (index == 0) session.copy(objectiveIds = session.objectiveIds + "obj-x") else session }) },
            "step title" to mapUnit(pack) { unit ->
                val session = unit.sessions.first()
                val step = session.steps.first()
                unit.copy(sessions = listOf(session.copy(steps = listOf(step.copy(title = step.title + " x")) + session.steps.drop(1))) + unit.sessions.drop(1))
            },
            "step body" to mapUnit(pack) { unit ->
                val session = unit.sessions.first()
                val step = session.steps.first()
                unit.copy(sessions = listOf(session.copy(steps = listOf(step.copy(body = step.body + " x")) + session.steps.drop(1))) + unit.sessions.drop(1))
            },
            "step direction" to mapUnit(pack) { unit ->
                val session = unit.sessions.first()
                val step = session.steps.first()
                unit.copy(sessions = listOf(session.copy(steps = listOf(step.copy(bookDirection = (step.bookDirection ?: "") + " x")) + session.steps.drop(1))) + unit.sessions.drop(1))
            },
            "step reference" to mapUnit(pack) { unit ->
                val session = unit.sessions.first()
                val step = session.steps.first()
                unit.copy(sessions = listOf(session.copy(steps = listOf(step.copy(referenceIds = step.referenceIds + "ref-x")) + session.steps.drop(1))) + unit.sessions.drop(1))
            },
            "step remediation" to mapUnit(pack) { unit ->
                val session = unit.sessions.first()
                val step = session.steps.first()
                unit.copy(sessions = listOf(session.copy(steps = listOf(step.copy(remediationStepId = (step.remediationStepId ?: "") + "x")) + session.steps.drop(1))) + unit.sessions.drop(1))
            },
            "step unavailable" to mapUnit(pack) { unit ->
                val session = unit.sessions.first()
                val step = session.steps.first()
                unit.copy(sessions = listOf(session.copy(steps = listOf(step.copy(unavailableReason = "closed")) + session.steps.drop(1))) + unit.sessions.drop(1))
            },
            "advancement" to mapUnit(pack) { unit -> unit.copy(advancement = unit.advancement!!.copy(minimumDistinctChecks = unit.advancement.minimumDistinctChecks + 1)) },
            "mapping note" to mapUnit(pack) { unit -> unit.copy(mappingNote = unit.mappingNote + " x") },
            "source" to pack.copy(sources = pack.sources.map { if (it.id == source.id) it.copy(title = it.title + " x") else it }),
            "rights" to pack.copy(rights = pack.rights.map { if (it.id == rights.id) it.copy(license = it.license + " x") else it }),
        )
        mutations.forEach { (label, mutated) ->
            assertNotEquals(label, baseline, V2Validator.contentReviewDigest(mutated))
        }
        val workflow = listOf(
            "letter review" to pack.copy(letters = pack.letters.map { if (it.id == letter.id) it.copy(reviewStatus = "APPROVED") else it }),
            "example review" to pack.copy(examples = pack.examples.map { if (it.id == example.id) it.copy(reviewStatus = "APPROVED") else it }),
            "check review" to pack.copy(items = pack.items.map { if (it.id == item.id) it.copy(reviewStatus = "APPROVED") else it }),
            "reference review" to pack.copy(references = pack.references.map { if (it.id == reference.id) it.copy(reviewStatus = "APPROVED") else it }),
            "mapping status" to mapUnit(pack) { unit -> unit.copy(mappingStatus = "APPROVED") },
            "install state" to mapUnit(pack) { unit -> unit.copy(installState = "INSTALLED") },
            "readiness" to mapUnit(pack) { unit -> unit.copy(readiness = "INSTALLABLE") },
            "review signature" to pack.copy(linguisticReview = pack.linguisticReview.copy(status = "APPROVED", reviewer = "Pat Reviewer", reviewerQualification = "Advanced Russian and curriculum review attested.", reviewedContentDigest = baseline, packVersion = pack.packVersion, curriculumMappingId = "map-owned-edition")),
            "audio counter" to pack.copy(admissions = pack.admissions.copy(mappedPronunciationAudio = 4)),
            "handwriting counter" to pack.copy(admissions = pack.admissions.copy(handwritingAssets = 4)),
        )
        workflow.forEach { (label, mutated) ->
            assertEquals(label, baseline, V2Validator.contentReviewDigest(mutated))
        }
        val collidedNote = pack.copy(letters = pack.letters.map { if (it.id == letter.id) it.copy(englishNote = "AB", sourceId = "C") else it })
        val collidedSource = pack.copy(letters = pack.letters.map { if (it.id == letter.id) it.copy(englishNote = "A", sourceId = "BC") else it })
        assertNotEquals(V2Validator.contentReviewDigest(collidedNote), V2Validator.contentReviewDigest(collidedSource))
    }

    @Test
    fun contentReviewDigestSurvivesApprovalActivation() {
        val pack = pack()
        val pending = buildUnit1ReviewPackage(pack)
        val signed = pending.contentReviewDigest
        val worksheet = renderUnit1ReviewFiles(pack).files.getValue(UNIT1_WORKSHEET_NAME)
        val attested = worksheet.replace(
            "\t\t\t\t\t\t\n",
            "\tAPPROVED\t\tPat Reviewer\t2026-09-23\t$signed\tAdvanced Russian and curriculum review attested.\n",
        )
        assertTrue(attested.contains("Pat Reviewer"))
        assertFalse(worksheet.contains("Pat Reviewer"))

        val activated = pack.copy(
            letters = pack.letters.map { it.copy(reviewStatus = "APPROVED") },
            examples = pack.examples.map { it.copy(reviewStatus = "APPROVED") },
            items = pack.items.map { it.copy(reviewStatus = "APPROVED") },
            references = pack.references.map { reference ->
                if (reference.classification == "LINGUISTIC_INSTRUCTION" || reference.reviewStatus != "NOT_REQUIRED") {
                    reference.copy(reviewStatus = "APPROVED")
                } else {
                    reference
                }
            },
            admissions = pack.admissions.copy(mappedPronunciationAudio = 1),
            units = pack.units.map { unit ->
                if (unit.number == 1) {
                    unit.copy(installState = "INSTALLED", readiness = "INSTALLABLE", mappingStatus = "APPROVED")
                } else {
                    unit
                }
            },
            unitReviews = pack.unitReviews.map { reviewRecord ->
                if (reviewRecord.unitNumber == 1) {
                    reviewRecord.copy(
                        status = "APPROVED",
                        reviewer = "Pat Reviewer",
                        reviewerQualification = "Advanced Russian and curriculum review attested.",
                        packVersion = pack.packVersion,
                        curriculumMappingId = "map-owned-edition",
                    )
                } else {
                    reviewRecord
                }
            },
        )
        assertEquals(signed, V2Validator.unitContentReviewDigest(activated, 1))
        assertEquals(V2Validator.unitContentReviewDigest(pack, 2), V2Validator.unitContentReviewDigest(activated, 2))
        assertEquals(V2Validator.contentReviewDigest(pack), V2Validator.contentReviewDigest(activated))
        val recorded = activated.copy(
            unitReviews = activated.unitReviews.map { reviewRecord ->
                if (reviewRecord.unitNumber == 1) reviewRecord.copy(reviewedContentDigest = signed) else reviewRecord
            },
        )
        assertEquals(signed, V2Validator.unitContentReviewDigest(recorded, 1))
        assertNotEquals(pending.reviewPackageDigest, buildUnit1ReviewPackage(recorded).reviewPackageDigest)
        val accepted = V2Validator.validate(recorded)
        assertFalse(accepted.ok)
        assertTrue(accepted.errors.joinToString("\n"), accepted.errors.any { it.contains("mapped pronunciation audio record") })
        assertTrue(accepted.errors.any { it.contains("does not match governed audio records") })
        assertTrue(accepted.errors.any { it.contains("mapping record does not resolve") })
        assertTrue(accepted.errors.none { it.contains("Unit 2") && it.contains("APPROVED") })
        val withoutCounter = recorded.copy(admissions = recorded.admissions.copy(mappedPronunciationAudio = 0))
        val stillBlocked = V2Validator.validate(withoutCounter)
        assertFalse(stillBlocked.ok)
        assertTrue(stillBlocked.errors.any { it.contains("mapped pronunciation audio record") })

        val mutated = recorded.copy(
            items = recorded.items.map { item ->
                if (item.id.startsWith("u1-")) item.copy(feedback = item.feedback + " x") else item
            },
        )
        assertNotEquals(signed, V2Validator.unitContentReviewDigest(mutated, 1))
        assertEquals(V2Validator.unitContentReviewDigest(pack, 2), V2Validator.unitContentReviewDigest(mutated, 2))
        val invalid = V2Validator.validate(mutated)
        assertFalse(invalid.ok)
        assertTrue(invalid.errors.any { it.contains("Unit 1") && it.contains("digest") })
        assertTrue(invalid.errors.none { it.contains("Unit 2 APPROVED review digest") })
        assertEquals("DEVELOPMENT", pack.unit(1)!!.installState)
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(1)!!.readiness)
    }

    private fun mapUnit(
        pack: dev.russiancompanion.v2.curriculum.V2CurriculumPack,
        transform: (dev.russiancompanion.v2.curriculum.V2Unit) -> dev.russiancompanion.v2.curriculum.V2Unit,
    ) = pack.copy(units = pack.units.map { unit -> if (unit.number == 1) transform(unit) else unit })

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
