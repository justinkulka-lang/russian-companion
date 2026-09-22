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
        val accepted = coordinator.submit(
            work = {
                worker = Thread.currentThread()
                started.countDown()
                release.await()
                1
            },
            onFailure = { 0 },
        ) { finished.countDown() }
        assertTrue(accepted)
        assertTrue(started.await(3, TimeUnit.SECONDS))
        assertTrue(busy)
        assertNotEquals(caller, worker)
        assertFalse(coordinator.submit(work = { 2 }, onFailure = { 0 }, onResult = { }))
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
        val accepted = coordinator.submit(
            work = {
                nestedAccepted = coordinator.submit(work = { 2 }, onFailure = { 0 }, onResult = { })
                1
            },
            onFailure = { 0 },
        ) { }
        assertTrue(accepted)
        assertFalse(nestedAccepted)
        assertEquals(listOf(true, false), busySnapshots)
    }

    @Test
    fun aPersistenceExceptionDeliversExactlyOneFailure() {
        val results = mutableListOf<String>()
        val coordinator = UiWorkCoordinator(
            dispatcher = kotlinx.coroutines.Dispatchers.Unconfined,
            scope = CoroutineScope(kotlinx.coroutines.Dispatchers.Unconfined),
            runSynchronously = true,
            onBusyChange = { },
        )
        val accepted = coordinator.submit(
            work = { error("persist failed") },
            onFailure = { "not-saved" },
        ) { results += it }
        assertTrue(accepted)
        assertEquals(listOf("not-saved"), results)
        assertFalse(coordinator.isBusy())
    }

    @Test
    fun anExportExceptionDeliversExactlyOneFailureOffTheCallerThread() {
        val executor = Executors.newSingleThreadExecutor()
        val dispatcher = executor.asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher + SupervisorJob())
        val results = mutableListOf<String>()
        val finished = CountDownLatch(1)
        val coordinator = UiWorkCoordinator(
            dispatcher = dispatcher,
            scope = scope,
            runSynchronously = false,
            onBusyChange = { },
        )
        val accepted = coordinator.submit(
            work = { error("export failed") },
            onFailure = { "not-confirmed" },
        ) {
            results += it
            finished.countDown()
        }
        assertTrue(accepted)
        assertTrue(finished.await(3, TimeUnit.SECONDS))
        assertEquals(listOf("not-confirmed"), results)
        assertFalse(coordinator.isBusy())
        dispatcher.close()
        executor.shutdownNow()
    }
}
