package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.advancementDecision
import dev.russiancompanion.v2.curriculum.unit
import dev.russiancompanion.v2.review.buildUnitReviewPackage
import dev.russiancompanion.v2.review.governedMappingFacts
import dev.russiancompanion.v2.review.mappingPackageDigest
import dev.russiancompanion.v2.review.renderUnitReviewFiles
import dev.russiancompanion.v2.review.reviewManifestName
import dev.russiancompanion.v2.review.validateUnitReviewFiles
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.security.MessageDigest

class Units610SourceIntakeTest {
    @Test
    fun unitsSixThroughTenStayReviewPendingAndSourceBound() {
        val pack = pack()
        val validation = V2Validator.validate(pack)
        assertTrue(validation.errors.joinToString("\n"), validation.ok)
        val expectedIndependent = mapOf(6 to 18, 7 to 22, 8 to 18, 9 to 26, 10 to 22)
        val expectedGuided = mapOf(6 to 5, 7 to 6, 8 to 5, 9 to 7, 10 to 6)
        listOf(6, 7, 8, 9, 10).forEach { number ->
            val unit = pack.unit(number)!!
            assertEquals("DEVELOPMENT", unit.installState)
            assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", unit.readiness)
            assertEquals("PENDING_CURRICULUM_REVIEWER", unit.mappingStatus)
            assertEquals("IMPLEMENTED_PENDING_FINAL_AUDIT", pack.unitReviews.single { it.unitNumber == number }.status)
            assertTrue(pack.unitReviews.single { it.unitNumber == number }.reviewer.isNullOrBlank())
            val steps = unit.sessions.flatMap { it.steps }
            val independent = steps.filter { it.kind == "CHECK" }.flatMap { it.itemIds }.distinct()
            val guided = steps.filter { it.kind == "GUIDED" }.flatMap { it.itemIds }.distinct()
            assertTrue("unit $number independent ${independent.size}", independent.size >= expectedIndependent.getValue(number))
            assertTrue("unit $number guided ${guided.size}", guided.size >= expectedGuided.getValue(number))
            unit.objectives.filter { it.kind == "RECOGNITION" }.forEach { objective ->
                assertTrue(objective.exampleIds.size >= 2)
                assertTrue(objective.checkItemIds.size >= 2)
                assertEquals(1, objective.remediationStepIds.size)
                val remediation = objective.remediationStepIds.single()
                assertTrue(steps.filter { it.kind == "CHECK" && it.itemIds.any { id -> id in objective.checkItemIds } }.all { it.remediationStepId == remediation })
            }
            val review = buildUnitReviewPackage(pack, number)
            assertTrue(review.unresolved.joinToString("\n"), review.unresolved.isEmpty())
            assertEquals(mappingPackageDigest(governedMappingFacts(number)), review.mappingPackageDigest)
            assertNotEquals(review.contentReviewDigest, review.packContentReviewDigest)
        }
        assertEquals("56-67", governedMappingFacts(6).let { "${it.printedPageStart}-${it.printedPageEnd}" })
        assertEquals("107-121", governedMappingFacts(10).let { "${it.printedPageStart}-${it.printedPageEnd}" })
    }

    @Test
    fun earlierUnitDigestsStayPutAndLaterUnitsStayIsolated() {
        val pack = pack()
        assertEquals("161a578ba556d709b621b61f138ad36290da57869a87ed57979e0bc7058b0aa7", V2Validator.unitContentReviewDigest(pack, 1))
        assertEquals("4fd67bff0431a11416115de023e076c52b7879304fc4a98b508d871170703e9c", V2Validator.unitContentReviewDigest(pack, 2))
        assertEquals("29121dfbfb6a2e07d413e86b01462a76a62b9659f52597300ccd91c569224d7c", V2Validator.unitContentReviewDigest(pack, 3))
        assertEquals("9f255afab766ef14cbf8ef2688a512f74c00d3c45efc772d6b4506a431a21121", V2Validator.unitContentReviewDigest(pack, 4))
        assertEquals("c2e405cf7081d673a39def8e26035bac7a65b608c57c5f4803ba832c2305c744", V2Validator.unitContentReviewDigest(pack, 5))
        assertEquals(
            mappingPackageDigest(governedMappingFacts(5)),
            buildUnitReviewPackage(pack, 5).mappingPackageDigest,
        )
        assertEquals("ff773232faf85dc03742e639b0f6b2b1b81be103a62d2bd60e4a1595d71f94bf", buildUnitReviewPackage(pack, 1).reviewPackageDigest)
        assertEquals("8020ff933e4903f669a62b21286cc49d2ba45f7a7dc279b1eeb634aaacb434a1", buildUnitReviewPackage(pack, 2).reviewPackageDigest)
        assertEquals("3c945d375fcd26e67a8820fa0e9ba371eb9e5b0054726c8eecfb4313a17f5f67", buildUnitReviewPackage(pack, 3).reviewPackageDigest)
        assertEquals("e7f14aefe7d74e50b3295e6ba23b8394dc33d313d319a4e53c1c583b7af30b05", buildUnitReviewPackage(pack, 4).reviewPackageDigest)
        assertEquals("dbb1f94528e0fd376cca913d817d39da48f0a677a57e39fe2d3c1bee5dd5a71f", buildUnitReviewPackage(pack, 5).reviewPackageDigest)
        val unitSix = V2Validator.unitContentReviewDigest(pack, 6)
        val unitSeven = V2Validator.unitContentReviewDigest(pack, 7)
        val localSix = pack.copy(
            items = pack.items.map { item ->
                if (item.id.startsWith("u6-")) item.copy(feedback = item.feedback + " local") else item
            },
        )
        assertNotEquals(unitSix, V2Validator.unitContentReviewDigest(localSix, 6))
        assertEquals(unitSeven, V2Validator.unitContentReviewDigest(localSix, 7))
        assertEquals(V2Validator.unitContentReviewDigest(pack, 5), V2Validator.unitContentReviewDigest(localSix, 5))
        val shared = pack.copy(
            sources = pack.sources.map { source ->
                if (source.id == "src-wikibooks-russian-cc-by-sa") source.copy(attribution = source.attribution + " shared") else source
            },
        )
        assertNotEquals(unitSix, V2Validator.unitContentReviewDigest(shared, 6))
        assertNotEquals(unitSeven, V2Validator.unitContentReviewDigest(shared, 7))
        assertNotEquals(V2Validator.unitContentReviewDigest(pack, 3), V2Validator.unitContentReviewDigest(shared, 3))
        val swapped = buildUnitReviewPackage(pack, 7)
        assertNotEquals(buildUnitReviewPackage(pack, 6).reviewPackageDigest, swapped.reviewPackageDigest)
        val before = (1..9).associateWith { buildUnitReviewPackage(pack, it).reviewPackageDigest }
        val contentBefore = (1..9).associateWith { V2Validator.unitContentReviewDigest(pack, it) }
        val laterOnly = pack.copy(
            items = pack.items.map { item ->
                if (item.objectiveId.startsWith("obj-u10-")) item.copy(feedback = item.feedback + " later only") else item
            },
        )
        (1..9).forEach { number ->
            assertEquals(contentBefore.getValue(number), V2Validator.unitContentReviewDigest(laterOnly, number))
            assertEquals(before.getValue(number), buildUnitReviewPackage(laterOnly, number).reviewPackageDigest)
        }
        assertNotEquals(V2Validator.unitContentReviewDigest(pack, 10), V2Validator.unitContentReviewDigest(laterOnly, 10))
        assertNotEquals(V2Validator.contentReviewDigest(pack), V2Validator.contentReviewDigest(laterOnly))
        val sharedSource = pack.copy(
            sources = pack.sources.map { source ->
                if (source.id == "src-wikibooks-russian-cc-by-sa") source.copy(attribution = source.attribution + " shared later") else source
            },
        )
        assertEquals(V2Validator.unitContentReviewDigest(pack, 1), V2Validator.unitContentReviewDigest(sharedSource, 1))
        assertEquals(buildUnitReviewPackage(pack, 1).reviewPackageDigest, buildUnitReviewPackage(sharedSource, 1).reviewPackageDigest)
        assertNotEquals(V2Validator.unitContentReviewDigest(pack, 6), V2Validator.unitContentReviewDigest(sharedSource, 6))
        assertNotEquals(buildUnitReviewPackage(pack, 6).reviewPackageDigest, buildUnitReviewPackage(sharedSource, 6).reviewPackageDigest)
    }

    @Test
    fun wrongUnitPacketSubstitutionFails() {
        val pack = pack()
        val unitOne = renderUnitReviewFiles(pack, 1).files
        val unitTen = renderUnitReviewFiles(pack, 10).files
        val swapped = unitOne + (reviewManifestName(1) to unitTen.getValue(reviewManifestName(10)))
        val errors = validateUnitReviewFiles(pack, 1, swapped)
        assertTrue(errors.joinToString("\n"), errors.any { it.contains("not for Unit 1") || it.contains("does not match") })
    }

    @Test
    fun governedContrastsStaySourceBoundAndBookAttestationDoesNotAdvance() {
        val pack = pack()
        val joined = pack.units.filter { it.number in 6..10 }.joinToString("\n") { unit ->
            unit.sessions.joinToString("\n") { session -> session.steps.joinToString("\n") { it.body + (it.bookDirection ?: "") } }
        }
        assertTrue(joined.contains("Я шёл в дом"))
        assertTrue(joined.contains("Я жил в Лондоне"))
        assertTrue(joined.contains("11, 12, 13, 14"))
        assertTrue(joined.contains("not a statement of a present-day amount"))
        assertTrue(joined.contains("do not decline"))
        assertTrue(joined.contains("Я хочу нового кролика."))
        assertTrue(joined.contains("У меня есть Х"))
        assertTrue(joined.contains("У меня́ нет сы́на"))
        assertFalse(joined.contains("счастлив"))
        assertFalse(joined.contains("хорошое"))
        assertFalse(joined.contains("до "))
        val unit = pack.unit(9)!!
        val attestedOnly = advancementDecision(pack, unit, learner(unit.number, book = true, correct = emptySet(), steps = emptySet()))
        assertFalse(attestedOnly.allowed)
        val oneObjective = unit.objectives.first { it.kind == "RECOGNITION" }
        val partial = advancementDecision(
            pack,
            unit,
            learner(
                unit.number,
                book = true,
                correct = oneObjective.checkItemIds.toSet(),
                steps = unit.sessions.flatMap { it.steps }.map { it.id }.toSet(),
            ),
        )
        assertFalse(partial.allowed)
        assertTrue(partial.remaining.joinToString("\n"), partial.remaining.any { it.contains("Demonstrate") })
    }

    @Test
    fun noPenguinAudioOrNetworkWasAdded() {
        val root = repo()
        val curriculum = File(root, "app/src/main/assets/content/v2-curriculum.json").readText()
        assertFalse(curriculum.contains("The New Penguin Russian Course"))
        assertFalse(curriculum.contains("PENGUIN_EDITION_MAPPING_INPUT"))
        assertEquals(0, pack().audioAssets.size)
        assertEquals(0, pack().admissions.generatedRussianWords)
        assertEquals(0, pack().admissions.catalogAudioBoundToUnits)
        assertFalse(File(root, "app/src/main/AndroidManifest.xml").readText().contains("android.permission.INTERNET"))
        assertTrue(File(root, "app/src/main/assets/licenses/units-6-10-curriculum-notice.txt").isFile)
        assertTrue(File(root, "catalog/sources/units-6-10/FREEZE.json").isFile)
        assertTrue(File(root, "catalog/sources/units-6-10/openrussian/adjectives.csv").isFile)
    }

    @Test
    fun retainedSourceBytesMatchImmutableRevisions() {
        val root = repo()
        val freezeRoot = File(root, "catalog/sources/units-6-10")
        val parser = Json { ignoreUnknownKeys = true }
        val freeze = parser.parseToJsonElement(File(freezeRoot, "FREEZE.json").readText()).jsonObject
        val openrussian = freeze.getValue("openrussian").jsonObject
        assertEquals("50e210c4803237779cb562bc1abcea529066031c", openrussian.getValue("commit").jsonPrimitive.content)
        assertEquals("CC BY-SA 4.0", openrussian.getValue("licenseId").jsonPrimitive.content)
        assertEquals("https://creativecommons.org/licenses/by-sa/4.0/", openrussian.getValue("licenseUrl").jsonPrimitive.content)
        val license = File(freezeRoot, openrussian.getValue("licenseFile").jsonPrimitive.content)
        assertEquals(openrussian.getValue("licenseSha256").jsonPrimitive.content, sha256(license.readBytes()))
        val files = openrussian.getValue("files").jsonObject
        val linesByFile = files.mapValues { (_, element) ->
            val meta = element.jsonObject
            val data = File(freezeRoot, meta.getValue("path").jsonPrimitive.content).readBytes()
            val name = meta.getValue("url").jsonPrimitive.content.substringAfterLast("/")
            assertEquals(meta.getValue("sha256").jsonPrimitive.content, sha256(data))
            assertEquals(meta.getValue("gitBlob").jsonPrimitive.content, gitBlobSha1(data))
            assertTrue(meta.getValue("url").jsonPrimitive.content.contains("/blob/50e210c4803237779cb562bc1abcea529066031c/$name"))
            File(freezeRoot, meta.getValue("path").jsonPrimitive.content).readText().lineSequence().toSet()
        }
        openrussian.getValue("rows").jsonArray.forEach { element ->
            val row = element.jsonObject
            val line = row.getValue("line").jsonPrimitive.content
            assertTrue(row.getValue("recordId").jsonPrimitive.content, line in linesByFile.getValue(row.getValue("file").jsonPrimitive.content))
            assertEquals(row.getValue("recordId").jsonPrimitive.content, row.getValue("sha256").jsonPrimitive.content, sha256(line.toByteArray(Charsets.UTF_8)))
        }
        val expectedOldids = mapOf(
            "accusative" to "4330404",
            "adjectives" to "4292379",
            "genitive" to "4567338",
            "numbers" to "4371358",
            "gender" to "4310736",
            "pronouns" to "4585698",
            "nominative" to "4213267",
            "prepositional" to "4567336",
            "prepositions" to "4446858",
            "personal-pronouns" to "3598527",
            "copyrights" to "4622060",
            "adverbs" to "3197995",
            "adverb-formation" to "1374548415",
        )
        freeze.getValue("wikibooks").jsonObject.getValue("pages").jsonArray.forEach { element ->
            val page = element.jsonObject
            val key = page.getValue("key").jsonPrimitive.content
            val data = File(freezeRoot, page.getValue("file").jsonPrimitive.content).readBytes()
            val revision = parser.parseToJsonElement(File(freezeRoot, page.getValue("revisionFile").jsonPrimitive.content).readText()).jsonObject
            assertEquals(expectedOldids.getValue(key), page.getValue("oldid").jsonPrimitive.content)
            assertEquals(page.getValue("oldid").jsonPrimitive.content, revision.getValue("revid").jsonPrimitive.content)
            assertEquals(page.getValue("mediawikiSha1").jsonPrimitive.content, sha1(data))
            assertEquals(revision.getValue("sha1").jsonPrimitive.content, sha1(data))
            assertEquals(page.getValue("size").jsonPrimitive.content, data.size.toString())
            assertEquals(page.getValue("sha256").jsonPrimitive.content, sha256(data))
            assertFalse(page.getValue("file").jsonPrimitive.content.contains("oldid="))
        }
        assertTrue(File(freezeRoot, "../units-3-5/wikibooks/copyrights-4622060.txt").readText().contains("Creative Commons Attribution-ShareAlike 4.0"))
    }

    private fun sha256(bytes: ByteArray): String = digest("SHA-256", bytes)

    private fun sha1(bytes: ByteArray): String = digest("SHA-1", bytes)

    private fun gitBlobSha1(data: ByteArray): String {
        val header = "blob ${data.size}\u0000".toByteArray(Charsets.UTF_8)
        val message = MessageDigest.getInstance("SHA-1")
        message.update(header)
        message.update(data)
        return message.digest().joinToString("") { byte -> "%02x".format(byte) }
    }

    private fun digest(algorithm: String, bytes: ByteArray): String {
        return MessageDigest.getInstance(algorithm).digest(bytes).joinToString("") { byte -> "%02x".format(byte) }
    }

    private fun learner(number: Int, book: Boolean, correct: Set<String>, steps: Set<String>) = V2LearnerView(
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
        completedStepIds = steps,
        correctItemIds = correct,
        bookAttestedUnits = if (book) setOf(number) else emptySet(),
        completedUnitNumbers = emptySet(),
    )

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
