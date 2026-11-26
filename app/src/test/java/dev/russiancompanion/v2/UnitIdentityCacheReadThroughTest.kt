package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Count canonical work rather than assert machine-dependent duration. No learner credit is fabricated. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class UnitIdentityCacheReadThroughTest {
    @Test fun repeatedColdIdentityChecksComputeTheSameUnitOnlyOnce() {
        val pack = digestFixture()
        val expected = V2Validator.unitContentReviewDigest(pack, 1)
        V2PackIdentityIndex.clearForTests()
        V2Validator.unitDigestExecutionsForTests.set(0)
        repeat(12) { assertTrue(prescribedUnitIdentityMatches(pack, 1, expected)) }
        assertEquals("One immutable pack/unit should not be canonicalized for every recorded attempt",
            1, V2Validator.unitDigestExecutionsForTests.get())
    }

    @Test fun aLivePackEvictedByOtherPackInstancesIsRepopulatedOnce() {
        val pack = digestFixture()
        val expected = V2Validator.unitContentReviewDigest(pack, 1)
        V2PackIdentityIndex.clearForTests()
        V2PackIdentityIndex.warm(pack)
        val otherPacks = (1..9).map { pack.copy(packVersion = 100 + it) }
        otherPacks.forEach { V2PackIdentityIndex.warm(it) }
        V2Validator.unitDigestExecutionsForTests.set(0)
        repeat(12) { assertTrue(prescribedUnitIdentityMatches(pack, 1, expected)) }
        java.lang.ref.Reference.reachabilityFence(otherPacks)
        assertEquals("Bounded eviction must not leave a live pack permanently on the uncached path",
            1, V2Validator.unitDigestExecutionsForTests.get())
    }

    @Test fun cachedAndMutatedPackIdentitiesRemainDistinctAndRejectEachOthersCredit() {
        val original = digestFixture()
        val unit = original.unit(1)!!
        val changed = original.copy(units = listOf(unit.copy(title = unit.title + " — test-only changed identity")))
        val originalDigest = V2Validator.unitContentReviewDigest(original, 1)
        val changedDigest = V2Validator.unitContentReviewDigest(changed, 1)
        assertNotEquals(originalDigest, changedDigest)
        V2PackIdentityIndex.clearForTests()
        repeat(3) {
            assertTrue(prescribedUnitIdentityMatches(original, 1, originalDigest))
            assertFalse(prescribedUnitIdentityMatches(original, 1, changedDigest))
            assertTrue(prescribedUnitIdentityMatches(changed, 1, changedDigest))
            assertFalse(prescribedUnitIdentityMatches(changed, 1, originalDigest))
        }
    }

    /** A unit-scoped digest fixture, not an admitted or validated replacement curriculum. */
    private fun digestFixture(): V2CurriculumPack {
        val pack = Unit8PresentFixtures.pack
        return pack.copy(units = listOf(pack.unit(1)!!))
    }
}
