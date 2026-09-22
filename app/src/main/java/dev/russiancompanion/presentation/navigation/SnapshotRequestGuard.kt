package dev.russiancompanion.presentation.navigation

/**
 * Drops stale asynchronous snapshot results when a newer surface request has begun.
 */
internal class SnapshotRequestGuard {
    private var generation = 0

    @Synchronized
    fun begin(): Int {
        generation += 1
        return generation
    }

    @Synchronized
    fun isCurrent(token: Int): Boolean {
        return token == generation
    }

    suspend fun <T> load(block: suspend () -> T): T? {
        val token = begin()
        val result = block()
        return if (isCurrent(token)) result else null
    }
}
