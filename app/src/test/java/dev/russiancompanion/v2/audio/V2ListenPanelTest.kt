package dev.russiancompanion.v2.audio

import android.Manifest
import android.app.Application
import android.content.Context
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.audio.AndroidPracticeAudio
import dev.russiancompanion.data.audio.RecorderProbe
import dev.russiancompanion.data.audio.RecordingPlayerFactory
import dev.russiancompanion.v2.ui.V2ListenPanel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowMediaRecorder

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class V2ListenPanelTest {
    @get:Rule val compose = createComposeRule()
    private val context = ApplicationProvider.getApplicationContext<Application>()
    private val probe = RecorderProbe(context)
    private val players = RecordingPlayerFactory()
    private val audio = AndroidPracticeAudio(context, playerFactory = players, recorderFactory = probe::create)
    private val registry = PermissionRegistry(context)
    private val owner = PanelLifecycle()
    private val visible = mutableStateOf(true)
    private val registryOwner = object : ActivityResultRegistryOwner { override val activityResultRegistry = registry }

    @Before fun denyMicrophoneInitially() { shadowOf(context).denyPermissions(Manifest.permission.RECORD_AUDIO) }

    private fun show(granted: Boolean = true) {
        if (granted) shadowOf(context).grantPermissions(Manifest.permission.RECORD_AUDIO)
        val model = "audio/" + context.assets.list("audio")!!.first { it.endsWith(".ogg") }
        compose.setContent {
            CompositionLocalProvider(LocalActivityResultRegistryOwner provides registryOwner, LocalLifecycleOwner provides owner) {
                Column { if (visible.value) V2ListenPanel(model, audio) }
            }
        }
    }

    @Test fun permissionDenialDoesNotCaptureAndGrantStartsOnlyAfterTheRecordRequest() {
        show(granted = false)
        compose.onNodeWithTag("v2-record").performClick()
        assertEquals(Manifest.permission.RECORD_AUDIO, registry.requested)
        assertEquals(1, registry.launches)
        assertTrue(probe.created.isEmpty())
        compose.onNodeWithTag("v2-record").assertIsNotEnabled()
        compose.runOnIdle { registry.respond(false) }
        compose.onNodeWithText("Microphone permission is needed to record. Listening still works.").assertIsDisplayed()
        compose.onNodeWithTag("v2-record").assertIsEnabled()
        compose.onNodeWithTag("v2-stop-recording").assertDoesNotExist()
        compose.onNodeWithTag("v2-replay-take").assertDoesNotExist()
        compose.onNodeWithTag("v2-play-model").assertIsEnabled()
        assertFalse(audio.isCapturing())
        compose.onNodeWithTag("v2-record").performClick()
        compose.runOnIdle { registry.respond(true) }
        compose.onNodeWithTag("v2-stop-recording").assertIsDisplayed()
        assertTrue(audio.isCapturing())
        assertEquals(1, probe.created.size)
        keepAndReplayTake()
    }

    @Test fun emptyStopReturnsToRecordAndASubsequentTakeCanBeReplayed() = recoverFromStop("empty")
    @Test fun failedStopReturnsToRecordAndASubsequentTakeCanBeReplayed() = recoverFromStop("stop")

    private fun recoverFromStop(failure: String) {
        probe.failure = failure
        show()
        compose.onNodeWithTag("v2-record").performClick()
        assertEquals(0, registry.launches)
        assertTrue(audio.isCapturing())
        compose.runOnIdle { probe.writeTake(if (failure == "empty") byteArrayOf() else byteArrayOf(1, 2)) }
        val output = probe.output()
        compose.onNodeWithTag("v2-stop-recording").performClick()
        compose.onNodeWithTag("v2-record").assertIsEnabled()
        compose.onNodeWithTag("v2-replay-take").assertDoesNotExist()
        compose.onNodeWithTag("v2-compare-take").assertDoesNotExist()
        compose.onNodeWithText("No recording was saved. Try again.").assertIsDisplayed()
        assertFalse(audio.isCapturing())
        assertFalse(output.exists())
        compose.runOnIdle { probe.failure = null }
        compose.onNodeWithTag("v2-record").performClick()
        keepAndReplayTake()
    }

    @Test fun leavingCompositionCancelsCaptureAndReturningDoesNotOfferTheDiscardedTake() {
        show()
        compose.onNodeWithTag("v2-record").performClick()
        compose.runOnIdle { probe.writeTake() }
        val output = probe.output()
        compose.runOnIdle { visible.value = false }
        compose.waitForIdle()
        assertFalse(audio.isCapturing())
        assertFalse(output.exists())
        assertEquals(ShadowMediaRecorder.STATE_RELEASED, shadowOf(probe.created.last()).state)
        compose.runOnIdle { visible.value = true }
        compose.onNodeWithTag("v2-record").assertIsEnabled()
        compose.onNodeWithTag("v2-replay-take").assertDoesNotExist()
        compose.onNodeWithTag("v2-record").performClick()
        keepAndReplayTake()
    }

    @Test fun backgroundingDiscardsCaptureAndForegroundingAllowsANewTake() {
        show()
        compose.onNodeWithTag("v2-record").performClick()
        compose.runOnIdle { probe.writeTake() }
        val output = probe.output()
        compose.runOnIdle { owner.registry.currentState = Lifecycle.State.CREATED }
        assertFalse(audio.isCapturing())
        assertFalse(output.exists())
        compose.onNodeWithTag("v2-record").assertIsNotEnabled()
        compose.onNodeWithTag("v2-replay-take").assertDoesNotExist()
        compose.runOnIdle { owner.registry.currentState = Lifecycle.State.RESUMED }
        compose.onNodeWithTag("v2-record").assertIsEnabled().performClick()
        keepAndReplayTake()
    }

    @Test fun aPermissionResultAfterBackgroundingDoesNotStartAnUnexpectedCapture() {
        show(granted = false)
        compose.onNodeWithTag("v2-record").performClick()
        compose.runOnIdle {
            owner.registry.currentState = Lifecycle.State.CREATED
            registry.respond(true)
            owner.registry.currentState = Lifecycle.State.RESUMED
        }
        compose.onNodeWithTag("v2-record").assertIsEnabled()
        assertTrue(probe.created.isEmpty())
        assertFalse(audio.isCapturing())
        compose.onNodeWithTag("v2-replay-take").assertDoesNotExist()
        compose.onNodeWithTag("v2-record").performClick()
        compose.runOnIdle { registry.respond(true) }
        keepAndReplayTake()
    }

    private fun keepAndReplayTake() {
        compose.runOnIdle { probe.writeTake() }
        val output = probe.output()
        compose.onNodeWithTag("v2-stop-recording").performClick()
        assertFalse(audio.isCapturing())
        compose.onNodeWithTag("v2-record").assertIsEnabled()
        compose.onNodeWithTag("v2-replay-take").assertIsDisplayed().performClick()
        assertEquals(output.absolutePath, players.created.last().path)
        compose.runOnIdle { players.created.last().complete() }
        compose.onNodeWithTag("v2-compare-take").assertIsDisplayed()
    }
}

private class PanelLifecycle : LifecycleOwner {
    val registry = LifecycleRegistry.createUnsafe(this).apply { currentState = Lifecycle.State.RESUMED }
    override val lifecycle: Lifecycle get() = registry
}

private class PermissionRegistry(private val context: Context) : ActivityResultRegistry() {
    private var code: Int? = null
    var requested: String? = null
    var launches = 0
    override fun <I, O> onLaunch(requestCode: Int, contract: ActivityResultContract<I, O>, input: I, options: ActivityOptionsCompat?) {
        code = requestCode
        requested = contract.createIntent(context, input).getStringArrayExtra("androidx.activity.result.contract.extra.PERMISSIONS")?.single()
        launches++
    }
    fun respond(granted: Boolean) { dispatchResult(code!!, granted) }
}
