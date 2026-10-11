package dev.russiancompanion.v2
import dev.russiancompanion.v2.curriculum.*
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
class PackIdentityBeforeHostTest {
    @Test
    fun durablePackIdentityStaysHonestAndUnitIsolationHolds() {
        val pack = pack()
        assertEquals("v2-curriculum-foundation", pack.packId)
        assertEquals(3, pack.packVersion)
        assertEquals("rev-pending-v2-curriculum", pack.linguisticReview.id)
        assertEquals("IMPLEMENTED_PENDING_FINAL_AUDIT", pack.linguisticReview.status)
        val seed = File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText()
        assertTrue(seed.contains("\"packId\": \"v2-curriculum-foundation\""))
        assertTrue(seed.contains("\"packVersion\": 3"))
        assertTrue(seed.contains("\"id\": \"rev-pending-v2-curriculum\""))
        assertFalse(seed.contains("v2-units-1-2-foundation"))
        assertFalse(seed.contains("rev-pending-units-1-2"))
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(1)!!.readiness)
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(2)!!.readiness)
        listOf(3, 4, 5).forEach { number ->
            val unit = pack.unit(number)!!
            assertEquals("DEVELOPMENT", unit.installState)
            assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", unit.readiness)
            assertEquals("PENDING_CURRICULUM_REVIEWER", unit.mappingStatus)
            val text = unit.topicSummary + unit.mappingNote + unit.sessions.flatMap { it.steps }.joinToString(" ") {
                it.title + it.body + (it.bookDirection ?: "")
            }
            assertFalse(text.contains("Content-complete"))
            val packet = File(repo(), "review/unit-$number/UNIT_${number}_REVIEW_PACKET.md").readText()
            assertTrue(packet.contains("content-complete and review-pending"))
            assertFalse(packet.contains("Content-complete development"))
            assertTrue(packet.contains("IMPLEMENTED_PENDING_FINAL_AUDIT"))
        }
        listOf(6, 8, 9, 10).forEach { number ->
            val unit = pack.unit(number)!!
            assertEquals("DEVELOPMENT", unit.installState)
            assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", unit.readiness)
            assertEquals("PENDING_CURRICULUM_REVIEWER", unit.mappingStatus)
        }
        val unitSeven = pack.unit(7)!!
        assertEquals("DEVELOPMENT", unitSeven.installState)
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", unitSeven.readiness)
        assertEquals("PENDING_CURRICULUM_REVIEWER", unitSeven.mappingStatus)
        assertTrue(unitSeven.topicSummary.contains("bounded manner-adverb lesson"))
        assertFalse(unitSeven.mappingNote.contains("not prepared"))
        (11..15).forEach { number ->
            val unit = pack.unit(number)!!
            assertEquals("DEVELOPMENT", unit.installState)
            assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", unit.readiness)
            assertEquals("PENDING_CURRICULUM_REVIEWER", unit.mappingStatus)
        }
        assertTrue((16..20).all { pack.unit(it)!!.installState == "DEVELOPMENT" })
        assertTrue(listOf(16, 18, 19, 20).all { pack.unit(it)!!.readiness == "CONTENT_COMPLETE_REVIEW_PENDING" })
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(17)!!.readiness)
        assertTrue(pack.units.drop(25).all { it.installState == "DEVELOPMENT" })
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(28)!!.readiness)
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(26)!!.readiness)
        assertEquals(emptySet<Int>(), pack.units.filter { it.readiness == "NOT_PREPARED" }.map { it.number }.toSet())

        val unitOne = V2Validator.unitContentReviewDigest(pack, 1)
        val unitThree = V2Validator.unitContentReviewDigest(pack, 3)
        val unitFive = V2Validator.unitContentReviewDigest(pack, 5)
        val local = pack.copy(
            units = pack.units.map { unit ->
                if (unit.number != 5) unit else {
                    unit.copy(
                        sessions = unit.sessions.map { session ->
                            session.copy(
                                steps = session.steps.map { step ->
                                    if (step.id == "u5-st-book-work") step.copy(body = step.body + " extra book direction") else step
                                },
                            )
                        },
                    )
                }
            },
        )
        assertEquals(unitOne, V2Validator.unitContentReviewDigest(local, 1))
        assertEquals(unitThree, V2Validator.unitContentReviewDigest(local, 3))
        assertNotEquals(unitFive, V2Validator.unitContentReviewDigest(local, 5))
        assertNotEquals(V2Validator.contentReviewDigest(pack), V2Validator.contentReviewDigest(local))
        val workflow = pack.copy(
            units = pack.units.map { unit ->
                if (unit.number == 5) unit.copy(readiness = "CONTENT_COMPLETE_REVIEW_PENDING") else unit
            },
        )
        assertEquals(unitFive, V2Validator.unitContentReviewDigest(workflow, 5))
    }
    private fun repo(): File = listOf(File("."), File("..")).first { File(it,"app/src/main").isDirectory }
    private fun pack() = V2PackLoader.parse(File(repo(),"app/src/main/assets/content/v2-curriculum.json").readText())
}
