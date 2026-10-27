package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import java.io.File

/** Historical contracts keep their original pins; current content has separate exact source guards. */
internal object RecordedCurriculumConservationScope {
    const val priorSource = "49a926c5ace0459edf7712ca5d90db4a63ff9d31"
    const val currentSource = "b7e53230e2119b1e57d781b1e1b955f074a38601"
    // Exact declaration-only transitions are proved separately below.
    const val beforeUnit6PackImplementation = "2fb455d4cf0b69808d431a2b9db3583b4d4b0dcb"
    const val beforeUnit7PackImplementation = "53d9c292e1f32f204bdff2b09b8f10d02b6b28a6"
    const val beforeUnit8PackImplementation = "2e2ebcebc81f54c04db7f0ac24e99374674c97c5"
    const val beforeUnit8TeachingImplementation = "fe919656bfa2775b5cc2c22f2496d4258343cfea"
    const val beforeUnit9PackImplementation = "7e2296f067da861cbebdc9cf63bfd8e0ddd62436"
    const val unit8PackImplementation = "370d8716e2747c439da1a450a83b71b129a5c879"
    const val beforeUnit10PackImplementation = "83a7b075983be5f7f9117e72d2967a9aaa06ce56"
    const val beforeUnit10TeachingImplementation = "7efff9e18328c612de235236d98e834bb85cd1db"
    const val beforeUnit11PackImplementation = "85e7eaf21335a107d64b99a4f1969bab4f74670d"
    const val beforeUnit12PackImplementation = "c2d297e79fa677aa1375917cfbe052e4c0806633"
    const val beforeUnit13PackImplementation = "ca0fdaeca8de81ab6a5d4935fbcdcb068c4af913"
    const val currentPackImplementation = "bd00e5082c9f31d2ae3b379dd6204dd3b28e4475"
    private val sources = mutableMapOf<String, V2CurriculumPack>()
    private val currentUnitSources = mapOf(
        4 to "e6db0bdfe0bbb40edf190623e2123014946f305b",
        7 to beforeUnit8PackImplementation,
        8 to unit8PackImplementation,
        9 to beforeUnit10PackImplementation,
        10 to beforeUnit11PackImplementation,
        11 to beforeUnit12PackImplementation,
        12 to beforeUnit13PackImplementation,
        13 to currentPackImplementation,
        14 to "96242a17a87cc2ec0f5d7418cd98a9d3220a9e99",
        15 to "0f8001a8ec2cb76a7bc994a2c8d77db09a272d04",
    )

    fun source(root: File, commit: String): V2CurriculumPack =
        sources.getOrPut(root.canonicalPath + ":" + commit) { RecordedIntakeCorrectionScope.load(root, commit) }

    /** Restore only Unit13's v2 declaration; every other field must match its actual producer. */
    fun beforeUnit13PresentReview(root: File, current: V2CurriculumPack): V2CurriculumPack {
        assertCurrent(root, current)
        val prior = current.copy(units = current.units.map { unit ->
            if (unit.number == 13) unit.copy(advancement = unit.advancement!!.copy(
                cumulativeReviewPolicyId = Unit13WiderCumulativeReviewPolicy.definition.id)) else unit
        })
        assertEquals("Only the Unit13 policy declaration may differ from its actual v2 asset", source(root, beforeUnit13PackImplementation), prior)
        return prior
    }

    /** Restore only Unit12's declaration; prove every other field against its genuine v1 producer. */
    fun beforeUnit12GenderReview(root: File, current: V2CurriculumPack): V2CurriculumPack {
        assertCurrent(root, current)
        val beforeUnit13 = beforeUnit13PresentReview(root, current)
        val prior = beforeUnit13.copy(units = beforeUnit13.units.map { unit ->
            if (unit.number == 12) unit.copy(advancement = unit.advancement!!.copy(
                cumulativeReviewPolicyId = Unit12CumulativeReviewPolicy.definition.id)) else unit
        })
        assertEquals("Only the Unit12 policy declaration may differ from the preceding asset", source(root, beforeUnit12PackImplementation), prior)
        return prior
    }

    /** Restore only Unit11's declaration; prove every other field against the genuine v1 producer. */
    fun beforeUnit11NounReview(root: File, current: V2CurriculumPack): V2CurriculumPack {
        assertCurrent(root, current)
        val beforeUnit12 = beforeUnit12GenderReview(root, current)
        val prior = beforeUnit12.copy(units = beforeUnit12.units.map { unit ->
            if (unit.number == 11) unit.copy(advancement = unit.advancement!!.copy(
                cumulativeReviewPolicyId = Unit11CumulativeReviewPolicy.definition.id)) else unit
        })
        assertEquals("Only the Unit11 policy declaration may differ from the preceding asset", source(root, beforeUnit11PackImplementation), prior)
        return prior
    }

    /** Restore only Unit10's declaration; prove equality with its genuine schema1 producer asset. */
    fun beforeUnit10PresentReview(root: File, current: V2CurriculumPack): V2CurriculumPack {
        assertCurrent(root, current)
        val beforeUnit11 = beforeUnit11NounReview(root, current)
        val prior = beforeUnit11.copy(units = beforeUnit11.units.map { unit ->
            if (unit.number == 10) unit.copy(advancement = unit.advancement!!.copy(
                cumulativeReviewPolicyId = Unit10CumulativeReviewPolicy.definition.id)) else unit
        })
        assertEquals("Only the Unit10 policy declaration may differ from the preceding asset", source(root, beforeUnit10PackImplementation), prior)
        return prior
    }

    /** Restore only Unit9's declaration; prove every other field against the genuine schema5 producer. */
    fun beforeUnit9NounBehaviorReview(root: File, current: V2CurriculumPack): V2CurriculumPack {
        assertCurrent(root, current)
        val beforeUnit10 = beforeUnit10PresentReview(root, current)
        val prior = beforeUnit10.copy(units = beforeUnit10.units.map { unit ->
            if (unit.number == 9) unit.copy(advancement = unit.advancement!!.copy(
                cumulativeReviewPolicyId = Unit9WiderCumulativeReviewPolicy.definition.id)) else unit
        })
        assertEquals("Only the Unit9 policy declaration may differ from the preceding asset", source(root, beforeUnit9PackImplementation), prior)
        return prior
    }

    /** Restore only Unit8's declaration; every other current field must equal the genuine prior pack. */
    fun beforeUnit8PresentReview(root: File, current: V2CurriculumPack): V2CurriculumPack {
        assertCurrent(root, current)
        val beforeUnit9 = beforeUnit9NounBehaviorReview(root, current)
        val prior = beforeUnit9.copy(units = beforeUnit9.units.map { unit ->
            if (unit.number == 8) unit.copy(advancement = unit.advancement!!.copy(
                cumulativeReviewPolicyId = Unit8CumulativeReviewPolicy.definition.id)) else unit
        })
        assertEquals("Only the Unit8 policy declaration may differ from the preceding asset", source(root, beforeUnit8PackImplementation), prior)
        return prior
    }

    /** Exact current asset -> genuine pre-Unit7 declaration; no content substitutions. */
    fun beforeUnit7GenderReview(root: File, current: V2CurriculumPack): V2CurriculumPack {
        assertCurrent(root, current)
        val beforeUnit8 = beforeUnit8PresentReview(root, current)
        val prior = beforeUnit8.copy(units = beforeUnit8.units.map { unit ->
            if (unit.number == 7) unit.copy(advancement = unit.advancement!!.copy(
                cumulativeReviewPolicyId = Unit7CumulativeReviewPolicy.definition.id)) else unit
        })
        assertEquals("Only the Unit7 policy declaration may differ from the preceding asset", source(root, beforeUnit7PackImplementation), prior)
        return prior
    }

    /** Restore only the Unit6 declaration, proving equality with the genuine preceding pack. */
    fun beforeUnit6PresentReview(root: File, current: V2CurriculumPack): V2CurriculumPack {
        assertCurrent(root, current)
        val beforeUnit7 = beforeUnit7GenderReview(root, current)
        val prior = beforeUnit7.copy(units = beforeUnit7.units.map { unit ->
            if (unit.number == 6) unit.copy(advancement = unit.advancement!!.copy(
                cumulativeReviewPolicyId = Unit6WiderCumulativeReviewPolicy.definition.id)) else unit
        })
        assertEquals("Only the Unit6 policy declaration may differ from the preceding asset", source(root, beforeUnit6PackImplementation), prior)
        return prior
    }

    /** Historical Unit5 activation assertions apply before this exact declaration-only transition. */
    fun beforeUnit5GrammarReview(root: File, current: V2CurriculumPack): V2CurriculumPack {
        val beforeUnit6 = beforeUnit6PresentReview(root, current)
        val prior = beforeUnit6.copy(units = beforeUnit6.units.map { unit ->
            if (unit.number == 5) unit.copy(advancement = unit.advancement!!.copy(
                cumulativeReviewPolicyId = Unit5CumulativeReviewPolicy.definition.id)) else unit
        })
        assertEquals("Only the Unit5 policy declaration may differ from the preceding asset", source(root, currentSource), prior)
        return prior
    }

    fun assertCurrent(root: File, current: V2CurriculumPack) {
        V2PackIdentityIndex.warm(current)
        assertEquals("Current pack must match its last actual asset implementation", source(root, currentPackImplementation), current)
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
            if (number in setOf(5, 6)) {
                // The later review declarations legitimately changed these unit identities.
                // Keep the original pin on a full-pack-proven historical declaration scope.
                val preceding = beforeUnit5GrammarReview(root, current)
                assertEquals("pre-policy unit $number", digest, V2Validator.unitContentReviewDigest(preceding, number))
                assertNotEquals("The current review obligation must have its own identity for Unit $number",
                    digest, V2Validator.unitContentReviewDigest(current, number))
            } else if (number in setOf(4, 7, 8, 10, 15)) {
                RecordedIntakeCorrectionScope.assertCurrentUnitMatches(current, source(root, currentUnitSources.getValue(number)), number)
                assertNotEquals("Declared correction must differ from its prior pin for Unit $number", digest, V2Validator.unitContentReviewDigest(current, number))
            } else {
                // Keep the original unchanged-unit pin in its exact prior declaration scope.
                (when (number) {
                    9 -> beforeUnit9NounBehaviorReview(root, current)
                    11 -> beforeUnit11NounReview(root, current)
                    12 -> beforeUnit12GenderReview(root, current)
                    13 -> beforeUnit13PresentReview(root, current)
                    else -> current
                }).let { current ->
                assertEquals("unchanged current unit $number", digest, V2Validator.unitContentReviewDigest(current, number))
                }
                if (number == 9) {
                    RecordedIntakeCorrectionScope.assertCurrentUnitMatches(current, source(root, currentUnitSources.getValue(number)), number)
                    assertNotEquals(digest, V2Validator.unitContentReviewDigest(current, number))
                }
                if (number == 11) {
                    // Historical identity stays pinned above; actual current instruction and obligation stay separately guarded.
                    RecordedIntakeCorrectionScope.assertCurrentUnitMatches(current, source(root, currentUnitSources.getValue(number)), number)
                    assertNotEquals("Current Unit11 review declaration must retain its own identity", digest,
                        V2Validator.unitContentReviewDigest(current, number))
                }
                if (number == 12) {
                    // Keep the original v1 pin above and independently guard the actual v2 source/identity.
                    RecordedIntakeCorrectionScope.assertCurrentUnitMatches(current, source(root, currentUnitSources.getValue(number)), number)
                    assertNotEquals("Current Unit12 review declaration must retain its own identity", digest,
                        V2Validator.unitContentReviewDigest(current, number))
                }
                if (number == 13) {
                    RecordedIntakeCorrectionScope.assertCurrentUnitMatches(current, source(root, currentUnitSources.getValue(number)), number)
                    assertNotEquals("Current Unit13 v3 must retain its own identity", digest,
                        V2Validator.unitContentReviewDigest(current, number))
                }
            }
        }
    }
}
