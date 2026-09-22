package dev.russiancompanion.data.local.seed

import dev.russiancompanion.data.local.AppDatabase
import dev.russiancompanion.data.local.BundledSeedStateEntity
import dev.russiancompanion.data.local.LearnerStateEntity
import java.security.MessageDigest

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
        val hash = json?.let(::sha256Hex).orEmpty()
        val existing = database.bundled().seedState(BundledSeedStateEntity.SINGLETON_ID)
        if (json != null && hash.isNotEmpty() && existing?.contentHash == hash) {
            database.runInTransaction {
                database.users().insertLearnerIfAbsent(LearnerStateEntity.newUser())
            }
            return snapshot(SeedApplyStatus.APPLIED)
        }
        val parsed = when (json) {
            null -> null
            else -> when (val result = SeedJson.parse(json)) {
                SeedParse.Corrupt -> return applyCorrupt()
                is SeedParse.Ok -> prepareBundledContent(result.document).copy(contentHash = hash)
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

internal fun sha256Hex(text: String): String {
    val digest = MessageDigest.getInstance("SHA-256").digest(text.toByteArray(Charsets.UTF_8))
    return digest.joinToString("") { byte -> "%02x".format(byte) }
}
