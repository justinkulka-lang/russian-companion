package dev.russiancompanion.data.local.seed

import dev.russiancompanion.data.local.AppDatabase
import dev.russiancompanion.data.local.BundledSeedStateEntity
import dev.russiancompanion.data.local.LearnerStateEntity

internal enum class SeedApplyStatus {
    ABSENT,
    CORRUPT,
    APPLIED,
}

internal data class SeedOutcome(
    val status: SeedApplyStatus,
    val contentVersion: Int?,
    val eligibleAudioCount: Int,
    val excludedAudioCount: Int,
    val eligibleGateCount: Int,
    val excludedGateCount: Int,
    val shareAlikeBlocksRelease: Boolean,
)

internal class BundledContentSeeder(
    private val database: AppDatabase,
) {
    fun apply(json: String?): SeedOutcome {
        val parsed = when (json) {
            null -> null
            else -> when (val result = SeedJson.parse(json)) {
                SeedParse.Corrupt -> return applyCorrupt()
                is SeedParse.Ok -> prepareBundledContent(result.document)
            }
        }
        database.runInTransaction {
            if (parsed != null) {
                database.bundled().replaceBundledContent(parsed)
            }
            database.users().insertLearnerIfAbsent(LearnerStateEntity.newUser())
        }
        return snapshot(if (parsed == null) SeedApplyStatus.ABSENT else SeedApplyStatus.APPLIED)
    }

    private fun applyCorrupt(): SeedOutcome {
        database.runInTransaction {
            database.users().insertLearnerIfAbsent(LearnerStateEntity.newUser())
        }
        return snapshot(SeedApplyStatus.CORRUPT)
    }

    private fun snapshot(status: SeedApplyStatus): SeedOutcome {
        val bundled = database.bundled()
        return SeedOutcome(
            status = status,
            contentVersion = bundled.seedState(BundledSeedStateEntity.SINGLETON_ID)?.contentVersion,
            eligibleAudioCount = bundled.eligibleAudioCount(),
            excludedAudioCount = bundled.excludedAudioCount(),
            eligibleGateCount = bundled.eligibleGateCount(),
            excludedGateCount = bundled.excludedGateCount(),
            shareAlikeBlocksRelease = bundled.unconfirmedShareAlikeCount() > 0,
        )
    }
}
