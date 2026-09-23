package dev.russiancompanion.v2.review

import dev.russiancompanion.release.ReleaseDecision
import dev.russiancompanion.release.assessRepositoryRelease
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.unit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class Unit1ReviewPackageTest {
    @Test
    fun committedPackageMatchesTheCurriculumTraversal() {
        val pack = pack()
        val rendered = renderUnit1ReviewFiles(pack)
        val dir = File(repo(), "review/unit-1")
        if (System.getenv("WRITE_UNIT1_REVIEW") == "1") {
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
        assertEquals(V2Validator.instructionalContentDigest(pack), review.instructionalContentDigest)
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
        assertEquals("PENDING_CURRICULUM_REVIEWER", unit.mappingStatus)
    }

    @Test
    fun reviewerAndMappingFieldsStayBlank() {
        val pack = pack()
        val files = renderUnit1ReviewFiles(pack).files
        val worksheet = parseTsv(files.getValue(UNIT1_WORKSHEET_NAME))
        worksheet.drop(1).forEach { row ->
            assertEquals("", row[10])
            assertEquals("", row[11])
            assertEquals("", row[12])
            assertEquals("", row[13])
            assertEquals("", row[14])
        }
        val mapping = parseTsv(files.getValue(UNIT1_MAPPING_NAME))
        mapping.drop(1).forEach { row ->
            (3 until row.size).forEach { index -> assertEquals("", row[index]) }
        }
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

        val badDigest = manifest.replace(buildUnit1ReviewPackage(pack).instructionalContentDigest, "1".repeat(64))
        assertTrue(validateUnit1ReviewFiles(pack, files + (UNIT1_MANIFEST_NAME to badDigest)).any { it.contains("Package digest does not match") })

        val approved = files.getValue(UNIT1_WORKSHEET_NAME).replaceFirst(
            Regex("(\t[0-9a-f]{64})\t\t\t\t\t"),
            "$1\tAPPROVED\t\t\t\t",
        )
        assertTrue(validateUnit1ReviewFiles(pack, files + (UNIT1_WORKSHEET_NAME to approved)).any { it.contains("pre-populates a reviewer field") || it.contains("disposition") })

        val named = files.getValue(UNIT1_INSTRUCTIONS_NAME) + "\nQualified Reviewer\n"
        assertTrue(validateUnit1ReviewFiles(pack, files + (UNIT1_INSTRUCTIONS_NAME to named)).any { it.contains("fabricates a reviewer identity") })

        val aligned = files.getValue(UNIT1_PACKET_NAME).replace("not an approved edition alignment", "Penguin-aligned")
        assertTrue(validateUnit1ReviewFiles(pack, files + (UNIT1_PACKET_NAME to aligned)).any { it.contains("Penguin-aligned") })

        val prose = files.getValue(UNIT1_PACKET_NAME) + "\nThe New Penguin Russian Course\n"
        assertTrue(validateUnit1ReviewFiles(pack, files + (UNIT1_PACKET_NAME to prose)).any { it.contains("Penguin prose") })

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
        assertTrue(pack().units.drop(2).all { it.installState == "NOT_YET_INSTALLED" })
    }

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
