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
        assertEquals(V2Validator.contentReviewDigest(pack), unit1.contentReviewDigest)
        assertEquals(unit1.contentReviewDigest, unit2.contentReviewDigest)
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
        parseTsv(files2.getValue(reviewMappingName(2))).drop(1).forEach { row ->
            (3 until row.size).forEach { index -> assertEquals("", row[index]) }
        }
    }

    @Test
    fun committedUnitPackagesMatchTheGenerator() {
        val pack = pack()
        listOf(1, 2).forEach { number ->
            val dir = File(repo(), "review/unit-$number")
            if (System.getenv("WRITE_UNIT_REVIEW") == "1") {
                writeReview(number, renderUnitReviewFiles(pack, number).files)
            }
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
        assertTrue(unit30.unresolved.any { it.contains("Unit 30") && it.contains("advancement") })
        val errors = validateUnitReviewFiles(pack, 30, renderUnitReviewFiles(pack, 30).files)
        assertTrue(errors.any { it.contains("cannot resolve") })
        val outside = buildUnitReviewPackage(pack, 31)
        assertTrue(outside.unresolved.any { it.contains("outside Units 1-30") })
        assertNotEquals(buildUnitReviewPackage(pack, 3).reviewPackageDigest, buildUnitReviewPackage(pack, 4).reviewPackageDigest)
    }

    private fun writeReview(unitNumber: Int, files: Map<String, String>) {
        val dir = File(repo(), "review/unit-$unitNumber")
        dir.mkdirs()
        files.forEach { (name, text) -> File(dir, name).writeText(text) }
    }

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
