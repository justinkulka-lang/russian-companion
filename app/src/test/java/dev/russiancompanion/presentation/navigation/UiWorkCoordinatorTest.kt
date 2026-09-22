package dev.russiancompanion.presentation.navigation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class UiWorkCoordinatorTest {
    @Test
    fun overlappingSubmitIsRejectedAndWorkLeavesTheCallerThread() {
        val executor = Executors.newSingleThreadExecutor()
        val dispatcher = executor.asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher + SupervisorJob())
        var busy = false
        val coordinator = UiWorkCoordinator(
            dispatcher = dispatcher,
            scope = scope,
            runSynchronously = false,
            onBusyChange = { busy = it },
        )
        val started = CountDownLatch(1)
        val release = CountDownLatch(1)
        val finished = CountDownLatch(1)
        val caller = Thread.currentThread()
        var worker: Thread? = null
        val accepted = coordinator.submit({
            worker = Thread.currentThread()
            started.countDown()
            release.await()
            1
        }) { finished.countDown() }
        assertTrue(accepted)
        assertTrue(started.await(3, TimeUnit.SECONDS))
        assertTrue(busy)
        assertNotEquals(caller, worker)
        assertFalse(coordinator.submit({ 2 }) { })
        release.countDown()
        assertTrue(finished.await(3, TimeUnit.SECONDS))
        assertFalse(coordinator.isBusy())
        dispatcher.close()
        executor.shutdownNow()
    }

    @Test
    fun synchronousSubmitStillRejectsANestedCall() = runBlocking {
        var busySnapshots = listOf<Boolean>()
        lateinit var coordinator: UiWorkCoordinator
        coordinator = UiWorkCoordinator(
            dispatcher = kotlinx.coroutines.Dispatchers.Unconfined,
            scope = this,
            runSynchronously = true,
            onBusyChange = { busySnapshots = busySnapshots + it },
        )
        var nestedAccepted = true
        val accepted = coordinator.submit({
            nestedAccepted = coordinator.submit({ 2 }) { }
            1
        }) { }
        assertTrue(accepted)
        assertFalse(nestedAccepted)
        assertEquals(listOf(true, false), busySnapshots)
    }
}
