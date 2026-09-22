package dev.russiancompanion.presentation.navigation

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SnapshotRequestGuardTest {
    @Test
    fun anOlderLoadCannotOverwriteANewerRequest() = runBlocking {
        val guard = SnapshotRequestGuard()
        val first = CompletableDeferred<String>()
        val second = CompletableDeferred<String>()
        val older = async { guard.load { first.await() } }
        yield()
        val newer = async { guard.load { second.await() } }
        yield()
        first.complete("A")
        assertNull(older.await())
        second.complete("B")
        assertEquals("B", newer.await())
    }
}
