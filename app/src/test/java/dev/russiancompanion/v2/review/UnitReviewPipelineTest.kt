package dev.russiancompanion.v2.review

import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.unit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class UnitReviewPipelineTest {
    @Test
    fun unitOneAndUnitTwoPackagesStayIsolated() {
        val pack = pack()
        val unit1 = buildUnitReviewPackage(pack, 1)
        val unit2 = buildUnitReviewPackage(pack, 2)
        assertEquals(V2Validator.unitContentReviewDigest(pack, 1), unit1.contentReviewDigest)
        assertEquals(V2Validator.unitContentReviewDigest(pack, 2), unit2.contentReviewDigest)
        assertNotEquals(unit1.contentReviewDigest, unit2.contentReviewDigest)
        assertEquals(V2Validator.contentReviewDigest(pack), unit1.packContentReviewDigest)
        assertEquals(unit1.packContentReviewDigest, unit2.packContentReviewDigest)
        assertNotEquals(unit1.contentReviewDigest, unit1.packContentReviewDigest)
        assertNotEquals(unit1.reviewPackageDigest, unit2.reviewPackageDigest)
        assertNotEquals(unit1.contentReviewDigest, unit1.reviewPackageDigest)
        assertNotEquals(unit1.reviewPackageDigest, unit1.repositorySourceDigest)
        assertNotEquals(unit2.reviewPackageDigest, unit2.repositorySourceDigest)
        assertEquals(unit1.repositorySourceDigest, unit2.repositorySourceDigest)
        assertTrue(unit1.unresolved.isEmpty())
        assertTrue(unit2.unresolved.isEmpty())
        assertTrue(unit1.unavailableIds.isEmpty())
        assertTrue(unit2.unavailableIds.isEmpty())
        assertTrue(unit2.bookDirectedIds.contains("u2-st-hw"))
        assertTrue(unit2.bookDirectedIds.contains("u2-st-pron"))
        assertEquals("DEVELOPMENT", pack.unit(2)!!.installState)
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(2)!!.readiness)
        assertEquals("PENDING_CURRICULUM_REVIEWER", pack.unit(2)!!.mappingStatus)

        val files1 = renderUnitReviewFiles(pack, 1).files
        val files2 = renderUnitReviewFiles(pack, 2).files
        if (System.getenv("WRITE_UNIT_REVIEW") == "1") {
            writeReview(1, files1)
            writeReview(2, files2)
        }
        assertTrue(validateUnitReviewFiles(pack, 1, files1).joinToString("\n"), validateUnitReviewFiles(pack, 1, files1).isEmpty())
        assertTrue(validateUnitReviewFiles(pack, 2, files2).joinToString("\n"), validateUnitReviewFiles(pack, 2, files2).isEmpty())
        val wrongUnit = validateUnitReviewFiles(pack, 1, files2)
        assertTrue(wrongUnit.joinToString("\n"), wrongUnit.any { it.contains("not part of the Unit 1 package") || it.contains("omitted") || it.contains("not for Unit 1") })
        val reverse = validateUnitReviewFiles(pack, 2, files1)
        assertTrue(reverse.any { it.contains("not part of the Unit 2 package") || it.contains("omitted") || it.contains("not for Unit 2") })

        val swappedManifest = files1.getValue(reviewManifestName(1)).replace(unit1.reviewPackageDigest, unit2.reviewPackageDigest)
        val swapped = validateUnitReviewFiles(pack, 1, files1 + (reviewManifestName(1) to swappedManifest))
        assertTrue(swapped.any { it.contains("Review package digest does not match") })

        val approvedWorksheet = files2.getValue(reviewWorksheetName(2)).replaceFirst(
            Regex("(\t[0-9a-f]{64})\t\t\t\t\t\t"),
            "$1\tAPPROVED\t\tPat Reviewer\t2026-09-23\t${unit2.contentReviewDigest}\tAdvanced Russian and curriculum review attested.",
        )
        val filled = validateUnitReviewFiles(pack, 2, files2 + (reviewWorksheetName(2) to approvedWorksheet))
        assertTrue(filled.any { it.contains("pre-populates a reviewer field") })
        assertTrue(validateUnitReviewFiles(pack, 1, files1).isEmpty())
        files2.values.forEach { text ->
            assertFalse(text.contains("Penguin-aligned"))
            assertFalse(text.contains("Qualified Reviewer"))
        }
        parseTsv(files2.getValue(reviewWorksheetName(2))).drop(1).forEach { row ->
            (10 until row.size).forEach { index -> assertEquals("", row[index]) }
        }
        val unit2Mapping = parseTsv(files2.getValue(reviewMappingName(2)))
        val unit2MappingHeader = unit2Mapping.first()
        unit2Mapping.drop(1).forEach { row ->
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
                assertEquals(column, "", row[unit2MappingHeader.indexOf(column)])
            }
            assertEquals("Lesson 2", row[unit2MappingHeader.indexOf("corresponding_lesson")])
            assertEquals("12-23", row[unit2MappingHeader.indexOf("corresponding_pages")])
            assertEquals("PENDING_CURRICULUM_REVIEWER", row[unit2MappingHeader.indexOf("alignment_disposition")])
            assertEquals("map-pending-unit-2", row[unit2MappingHeader.indexOf("mapping_record_id")])
            assertEquals(mappingPackageDigest(governedMappingFacts(2)), row[unit2MappingHeader.indexOf("mapping_package_digest")])
        }
    }

    @Test
    fun committedUnitPackagesMatchTheGenerator() {
        val pack = pack()
        val packetUnits = (1..30).toList()
        if (System.getenv("WRITE_UNIT_REVIEW") == "1") {
            packetUnits.forEach { number ->
                writeReview(number, renderUnitReviewFiles(pack, number).files)
            }
        }
        packetUnits.forEach { number ->
            val dir = File(repo(), "review/unit-$number")
            val committed = reviewFileNames(number).associateWith { name -> File(dir, name).readText() }
            val errors = validateUnitReviewFiles(pack, number, committed)
            assertTrue(errors.joinToString("\n"), errors.isEmpty())
            assertEquals(renderUnitReviewFiles(pack, number).files, committed)
        }
    }

    @Test
    fun unitsOutsideThePreparedPairCannotApproveOrSkipMissingRecords() {
        val pack = pack()
        val unit30 = buildUnitReviewPackage(pack, 30)
        assertTrue(unit30.unresolved.joinToString("\n"), unit30.unresolved.isEmpty())
        assertEquals("IMPLEMENTED_PENDING_FINAL_AUDIT", unit30.unitReviewStatus)
        assertTrue(unit30.unavailableIds.isEmpty())
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(30)!!.readiness)
        assertTrue(unit30.records.none { it.type == "UNIT_REVIEW" && it.reviewStatus == "APPROVED" })
        val outside = buildUnitReviewPackage(pack, 31)
        assertTrue(outside.unresolved.any { it.contains("outside Units 1-30") })
        assertNotEquals(buildUnitReviewPackage(pack, 6).reviewPackageDigest, buildUnitReviewPackage(pack, 7).reviewPackageDigest)
    }

    @Test
    fun aLaterUnitMutationDoesNotApproveOrInvalidateAnEarlierUnit() {
        val pack = pack()
        val unitThree = V2Validator.unitContentReviewDigest(pack, 3)
        val unitFour = V2Validator.unitContentReviewDigest(pack, 4)
        val unitFive = V2Validator.unitContentReviewDigest(pack, 5)
        val local = pack.copy(
            units = pack.units.map { unit ->
                if (unit.number != 5) unit else {
                    val session = unit.sessions.first { it.steps.any { step -> step.id == "u5-st-book-work" } }
                    unit.copy(
                        sessions = unit.sessions.map { current ->
                            if (current.id != session.id) current else current.copy(
                                steps = current.steps.map { step ->
                                    if (step.id == "u5-st-book-work") step.copy(body = step.body + " extra book direction") else step
                                },
                            )
                        },
                    )
                }
            },
        )
        assertEquals(unitThree, V2Validator.unitContentReviewDigest(local, 3))
        assertEquals(unitFour, V2Validator.unitContentReviewDigest(local, 4))
        assertNotEquals(unitFive, V2Validator.unitContentReviewDigest(local, 5))
        assertEquals(buildUnitReviewPackage(pack, 3).reviewPackageDigest, buildUnitReviewPackage(local, 3).reviewPackageDigest)

        val workflow = pack.copy(
            units = pack.units.map { unit ->
                if (unit.number == 5) unit.copy(readiness = "CONTENT_COMPLETE_REVIEW_PENDING") else unit
            },
            unitReviews = pack.unitReviews.map { review ->
                if (review.unitNumber == 5) review.copy(status = "REJECTED", notes = "Workflow only.") else review
            },
        )
        assertEquals(unitFive, V2Validator.unitContentReviewDigest(workflow, 5))
        assertEquals(V2Validator.contentReviewDigest(pack), V2Validator.contentReviewDigest(workflow))

        val shared = pack.copy(
            references = pack.references.map { reference ->
                if (reference.id == "ref-book-topics") reference.copy(detail = reference.detail + " extra") else reference
            },
        )
        assertNotEquals(unitThree, V2Validator.unitContentReviewDigest(shared, 3))
        assertNotEquals(unitFive, V2Validator.unitContentReviewDigest(shared, 5))
        assertEquals(V2Validator.unitContentReviewDigest(pack, 1), V2Validator.unitContentReviewDigest(shared, 1))
        assertEquals(V2Validator.unitContentReviewDigest(pack, 2), V2Validator.unitContentReviewDigest(shared, 2))
    }

    @Test
    fun unitTwoOnlyMutationLeavesUnitOneDigestUntouched() {
        val pack = pack()
        val unitOne = V2Validator.unitContentReviewDigest(pack, 1)
        val unitTwo = V2Validator.unitContentReviewDigest(pack, 2)
        val aggregate = V2Validator.contentReviewDigest(pack)
        val local = pack.copy(
            units = pack.units.map { unit ->
                if (unit.number != 2) {
                    unit
                } else {
                    val session = unit.sessions.first { session -> session.steps.any { it.id == "u2-st-hw" } }
                    val steps = session.steps.map { step ->
                        if (step.id == "u2-st-hw") step.copy(body = step.body + " extra book direction") else step
                    }
                    unit.copy(sessions = unit.sessions.map { if (it.id == session.id) session.copy(steps = steps) else it })
                }
            },
        )
        assertEquals(unitOne, V2Validator.unitContentReviewDigest(local, 1))
        assertNotEquals(unitTwo, V2Validator.unitContentReviewDigest(local, 2))
        assertNotEquals(aggregate, V2Validator.contentReviewDigest(local))
        assertEquals(buildUnitReviewPackage(pack, 1).reviewPackageDigest, buildUnitReviewPackage(local, 1).reviewPackageDigest)

        val limits = pack.copy(
            references = pack.references.map { reference ->
                if (reference.id == "ref-limits") reference.copy(detail = reference.detail + " extra") else reference
            },
        )
        assertEquals(unitOne, V2Validator.unitContentReviewDigest(limits, 1))
        assertNotEquals(unitTwo, V2Validator.unitContentReviewDigest(limits, 2))

        val sharedLetter = pack.letters.first { letter ->
            letter.id in reachableLetters(pack, 1) && letter.id in reachableLetters(pack, 2)
        }
        val shared = pack.copy(
            letters = pack.letters.map { letter ->
                if (letter.id == sharedLetter.id) letter.copy(englishNote = letter.englishNote + " shared") else letter
            },
        )
        assertNotEquals(unitOne, V2Validator.unitContentReviewDigest(shared, 1))
        assertNotEquals(unitTwo, V2Validator.unitContentReviewDigest(shared, 2))
    }

    @Test
    fun mappingPackageDigestBindsStructuralFactsAndIgnoresReviewerFields() {
        val pack = pack()
        val facts = governedMappingFacts(1)
        val prior = mappingPackageDigest(facts)
        val pending = buildUnitReviewPackage(pack, 1)
        assertEquals(prior, pending.mappingPackageDigest)
        assertEquals("161a578ba556d709b621b61f138ad36290da57869a87ed57979e0bc7058b0aa7", pending.contentReviewDigest)
        assertEquals("4fd67bff0431a11416115de023e076c52b7879304fc4a98b508d871170703e9c", V2Validator.unitContentReviewDigest(pack, 2))
        assertEquals("ca8c750f59bb71309cdc503ff566ae5431a19db90959011af1d2232310bf3f10", V2Validator.unitContentReviewDigest(pack, 3))
        assertEquals("1ebcde1ebabf867b08e137a96bf30f56cccbb0ea85cfd8ecb54cee463a79bf11", V2Validator.unitContentReviewDigest(pack, 4))
        assertEquals("c2e405cf7081d673a39def8e26035bac7a65b608c57c5f4803ba832c2305c744", V2Validator.unitContentReviewDigest(pack, 5))
        val mutations = listOf(
            "lesson number" to facts.copy(proposedLessonNumber = 9),
            "printed page start" to facts.copy(printedPageStart = 2),
            "printed page end" to facts.copy(printedPageEnd = 12),
            "edition title" to facts.copy(editionTitle = facts.editionTitle + " x"),
            "author" to facts.copy(author = facts.author + " x"),
            "publisher" to facts.copy(publisherImprint = facts.publisherImprint + " x"),
            "topic description" to facts.copy(topicDescription = facts.topicDescription + " x"),
            "observation source" to facts.copy(observationSource = facts.observationSource + " x"),
            "ISBN observation state" to facts.copy(isbnObservation = "OBSERVED"),
            "ISBN value" to facts.copy(isbn = "FIXTURE-ISBN"),
        )
        mutations.forEach { (label, mutated) ->
            val mappingDigest = mappingPackageDigest(mutated)
            val reviewDigest = buildUnitReviewPackage(pack, 1, mutated).reviewPackageDigest
            assertNotEquals(label, prior, mappingDigest)
            assertNotEquals(label, pending.reviewPackageDigest, reviewDigest)
            assertEquals(label, pending.contentReviewDigest, buildUnitReviewPackage(pack, 1, mutated).contentReviewDigest)
        }
        assertNotEquals(prior, mappingPackageDigest(facts.copy(printedPageEnd = 99)))

        val files = renderUnitReviewFiles(pack, 1).files
        val worksheet = files.getValue(reviewMappingName(1))
        val rows = parseTsv(worksheet)
        val header = rows.first()
        val identity = header.indexOf("reviewer_identity")
        val qualification = header.indexOf("reviewer_qualification")
        val date = header.indexOf("review_date")
        val notes = header.indexOf("reviewer_notes")
        val completedRows = rows.mapIndexed { index, row ->
            if (index == 0) row else row.toMutableList().also { filled ->
                filled[identity] = "Pat Reviewer"
                filled[qualification] = "Advanced Russian and curriculum review attested."
                filled[date] = "2026-09-23"
                filled[notes] = "Reviewed against the owned copy."
            }
        }
        val pendingFacts = mappingFactsFromRow(1, rows[1])
        val completedFacts = mappingFactsFromRow(1, completedRows[1])
        assertEquals(pendingFacts, completedFacts)
        assertEquals(prior, mappingPackageDigest(pendingFacts))
        assertEquals(prior, mappingPackageDigest(completedFacts))
        assertEquals("", rows[1][identity])
        assertEquals("", rows[1][qualification])
        assertEquals("", rows[1][date])
        assertEquals("", rows[1][notes])

        val otherUnit = files + (reviewMappingName(1) to renderUnitReviewFiles(pack, 2).files.getValue(reviewMappingName(2)))
        val swapped = validateUnitReviewFiles(pack, 1, otherUnit)
        assertTrue(swapped.joinToString("\n"), swapped.any { it.contains("wrong unit or lesson") })

        val changedPages = worksheet.replace("1-11", "1-12")
        val invalidated = validateUnitReviewFiles(pack, 1, files + (reviewMappingName(1) to changedPages))
        assertTrue(invalidated.joinToString("\n"), invalidated.any { it.contains("Mapping package digest does not match") })

        val listed = ProcessBuilder(listOf("git", "ls-files", "*.pdf", "*.PDF"))
            .directory(repo())
            .redirectError(File(if (System.getProperty("os.name").startsWith("Windows", ignoreCase = true)) "NUL" else "/dev/null"))
            .start()
            .let { process ->
                val text = process.inputStream.bufferedReader().readText()
                check(process.waitFor() == 0)
                text
            }
        assertEquals("", listed.trim())
        val curriculum = File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText()
        assertFalse(curriculum.contains("PENGUIN_EDITION_MAPPING_INPUT"))
        assertFalse(curriculum.contains("The New Penguin Russian Course"))
        assertTrue(files.getValue("REVIEWER_INSTRUCTIONS.md").contains("not comprehensive copyright detection"))
    }

    @Test
    fun curriculumSourceCommitMustContainTheGovernedCurriculum() {
        val pack = pack()
        val root = repo()
        val head = ProcessBuilder("git", "rev-parse", "HEAD")
            .directory(root)
            .redirectError(File(if (System.getProperty("os.name").startsWith("Windows", ignoreCase = true)) "NUL" else "/dev/null"))
            .start()
            .let { process ->
                val text = process.inputStream.bufferedReader().readText().trim()
                check(process.waitFor() == 0)
                text
            }
        assertTrue(curriculumSourceCommitErrors(head, root, 1).any { it.contains("packet commit itself") })
        val predating = "08663616a79af9009c657ecfb66d7f6d42a531bf"
        val rejected = curriculumSourceCommitErrors(predating, root, 2)
        assertTrue(rejected.joinToString("\n"), rejected.any { it.contains("Unit 2 content-review digest") || it.contains("readable curriculum") })
        (1..30).forEach { number ->
            val pin = curriculumCommitForUnit(number)
            val manifest = File(root, "review/unit-$number/${reviewManifestName(number)}").readText()
            assertTrue(manifest.contains(pin))
            assertFalse(manifest.contains(predating))
            val errors = curriculumSourceCommitErrors(pin, root, number)
            assertTrue(errors.joinToString("\n"), errors.isEmpty())
        }
        assertTrue(curriculumSourceCommitErrors(UNITS_1_5_CURRICULUM_COMMIT, root, 1).isNotEmpty())
        assertTrue(curriculumSourceCommitErrors(UNITS_6_10_CURRICULUM_COMMIT, root, 7).isNotEmpty())
        assertTrue(curriculumSourceCommitErrors(UNITS_26_30_CURRICULUM_COMMIT, root, 27).isNotEmpty())
    }

    private fun reachableLetters(pack: dev.russiancompanion.v2.curriculum.V2CurriculumPack, unitNumber: Int): Set<String> {
        val unit = pack.unit(unitNumber) ?: return emptySet()
        val exampleIds = unit.objectives.flatMap { it.exampleIds } + unit.sessions.flatMap { session ->
            session.steps.flatMap { it.exampleIds }
        }
        return exampleIds.map { id -> pack.examples.first { it.id == id }.letterId }.toSet()
    }

    private fun writeReview(unitNumber: Int, files: Map<String, String>) {
        val dir = File(repo(), "review/unit-$unitNumber")
        dir.mkdirs()
        files.forEach { (name, text) -> File(dir, name).writeText(text) }
    }

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
