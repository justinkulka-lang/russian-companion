package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import java.io.File

/** Historical contracts keep their original pins; current content has separate exact source guards. */
internal object RecordedCurriculumConservationScope {
    const val priorSource = "49a926c5ace0459edf7712ca5d90db4a63ff9d31"
    const val currentSource = "b7e53230e2119b1e57d781b1e1b955f074a38601"
    private val sources = mutableMapOf<String, V2CurriculumPack>()
    private val currentUnitSources = mapOf(
        4 to "e6db0bdfe0bbb40edf190623e2123014946f305b",
        7 to currentSource,
        8 to "fe919656bfa2775b5cc2c22f2496d4258343cfea",
        10 to "7efff9e18328c612de235236d98e834bb85cd1db",
        14 to "96242a17a87cc2ec0f5d7418cd98a9d3220a9e99",
        15 to "0f8001a8ec2cb76a7bc994a2c8d77db09a272d04",
    )

    fun source(root: File, commit: String): V2CurriculumPack =
        sources.getOrPut(root.canonicalPath + ":" + commit) { RecordedIntakeCorrectionScope.load(root, commit) }

    fun assertCurrent(root: File, current: V2CurriculumPack) {
        V2PackIdentityIndex.warm(current)
        assertEquals("Current pack must match its last actual asset implementation", source(root, currentSource), current)
    }

    fun historicalTransition(root: File, current: V2CurriculumPack, implementation: String, vararg units: Int): V2CurriculumPack {
        assertCurrent(root, current)
        units.forEach { number ->
            RecordedIntakeCorrectionScope.assertCurrentUnitMatches(current, source(root, currentUnitSources.getValue(number)), number)
        }
        val valid = V2Validator.validate(current)
        assertTrue(valid.errors.joinToString("\n"), valid.ok)
        return source(root, implementation)
    }

    fun assertHistoricalPinsAndCurrentUnits(root: File, current: V2CurriculumPack, pins: Map<Int, String>) {
        assertCurrent(root, current)
        val historical = source(root, priorSource)
        pins.forEach { (number, digest) ->
            assertEquals("historical unit $number", digest, V2Validator.unitContentReviewDigest(historical, number))
            if (number in setOf(4, 7, 8, 10, 15)) {
                RecordedIntakeCorrectionScope.assertCurrentUnitMatches(current, source(root, currentUnitSources.getValue(number)), number)
                assertNotEquals("Declared correction must differ from its prior pin for Unit $number", digest, V2Validator.unitContentReviewDigest(current, number))
            } else {
                assertEquals("unchanged current unit $number", digest, V2Validator.unitContentReviewDigest(current, number))
            }
        }
    }
}
