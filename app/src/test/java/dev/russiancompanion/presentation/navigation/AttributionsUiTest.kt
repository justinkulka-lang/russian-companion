package dev.russiancompanion.presentation.navigation

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.data.local.audio
import dev.russiancompanion.data.local.minimalDocument
import dev.russiancompanion.data.local.rights
import dev.russiancompanion.data.local.source
import dev.russiancompanion.data.local.seed.AudioModelSeed
import dev.russiancompanion.data.local.seed.PlanDefinitionSeed
import dev.russiancompanion.data.local.seed.SeedJson
import dev.russiancompanion.data.local.seed.SeedParse
import dev.russiancompanion.data.local.text
import dev.russiancompanion.data.navigation.RoomNavigationGateway
import dev.russiancompanion.domain.navigation.LicenseNoticeOption
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class AttributionsUiTest {
    @get:Rule
    val compose = createComposeRule()

    private val stores = mutableListOf<LocalStore>()

    @After
    fun closeStores() {
        stores.forEach { it.close() }
    }

    @Test
    fun emptyInventoryShowsNotReadyWithoutAReleaseBlock() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(
            SeedJson.encode(
                minimalDocument().copy(
                    courseSegments = emptyList(),
                    sourceRecords = emptyList(),
                ),
            ),
        )
        show(store)

        click("Attributions and licenses")
        show("No attribution inventory is installed. This installation is not release-ready.")
        compose.onNodeWithText("Public release is blocked until share-alike confirmation is recorded.")
            .assertDoesNotExist()
    }

    @Test
    fun packagedSeedShowsInstalledInventoryAfterShareAlikeConfirmation() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(LocalStore.readPackagedSeed(context))
        show(
            store,
            readSeed = { LocalStore.readPackagedSeed(context) },
            readCatalogNotice = { LocalStore.readPackagedCatalogNotice(context) },
            readLicenseNotices = { LocalStore.readPackagedLicenseNotices(context) },
        )

        click("Attributions and licenses")
        compose.onNodeWithText("Public release is blocked until share-alike confirmation is recorded.")
            .assertDoesNotExist()
        compose.onNodeWithText("No attribution inventory is installed. This installation is not release-ready.")
            .assertDoesNotExist()
        show("Catalog notice")
        compose.onNodeWithText("This app does not own the packaged Russian audio or stress-marked text.", substring = true)
            .assertIsDisplayed()
        show("Packaged license texts")
        show("CC BY-SA 4.0")
        show("CC BY 3.0 US")
        show("CC BY 2.0 FR")
        val raw = checkNotNull(LocalStore.readPackagedSeed(context))
        val parsed = SeedJson.parse(raw)
        val source = (parsed as SeedParse.Ok).document.sourceRecords.minBy { it.id }
        compose.onNodeWithText(source.attribution, substring = true).assertExists()
        compose.onNodeWithText("Public release is blocked until share-alike confirmation is recorded.")
            .assertDoesNotExist()
    }

    @Test
    fun inventoryShowsStoredRightsAndTheUnconfirmedReleaseBlock() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(SeedJson.encode(inventoryDocument("UNCONFIRMED")))
        show(store)

        click("Attributions and licenses")
        show("Public release is blocked until share-alike confirmation is recorded.")
        click("TEST-LICENSE: Test attribution")
        show("Source: source-1")
        show("Provenance: test-source")
        show("Redistribution basis: test-rights")
        show("License: TEST-LICENSE")
        show("Attribution: Test attribution")
        show("Modification status: Unmodified")
        show("Share-alike treatment: share-alike-applies")
        show("Share-alike confirmation: Unconfirmed")
        click("Close record")

        click("audio-1")
        show("Asset: audio-1")
        show("License status: Compatible")
        show("Compatibility: Compatible")
        show("Attribution required")
        show("Inclusion: Included")
        show("Audio file: audio/audio-1")
        show("This audio is eligible for practice.")
        click("Close record")

        click("audio-missing")
        show("Audio file: audio/audio-missing")
        show("This audio has no rights record and is excluded.")
        compose.onNodeWithText("License status: Compatible").assertDoesNotExist()
        click("Close record")

        click("audio-bad")
        show("License status: Incompatible")
        show("Compatibility: Incompatible")
        show("Inclusion: Excluded")
        show("This audio is excluded from practice.")
        click("Back")

        repeat(3) { click("Next") }
        click("Lesson 1")
        click("Confirm lesson")
        click("Practice")
        show("SOURCE_TEXT_TOKEN")
        compose.onNodeWithText("audio-missing").assertDoesNotExist()
        compose.onNodeWithText("audio-bad").assertDoesNotExist()
        click("TEST-LICENSE: Test attribution")
        show("Audio file: audio/audio-1")
        show("This audio is eligible for practice.")
        click("Back to practice")
        click("SOURCE_TEXT_TOKEN")
        show("Practice session")
        show("TEST-LICENSE: Test attribution")
        click("Attributions and licenses")
        show("Audio file: audio/audio-1")
        click("Back to practice")
        show("Practice session")
    }

    @Test
    fun confirmedShareAlikeDoesNotShowTheReleaseBlock() {
        openConfirmation("CONFIRMED")
        click("TEST-LICENSE: Test attribution")
        show("Share-alike confirmation: Confirmed")
        compose.onNodeWithText("Public release is blocked until share-alike confirmation is recorded.")
            .assertDoesNotExist()
        compose.onNodeWithText("No attribution inventory is installed. This installation is not release-ready.")
            .assertDoesNotExist()
    }

    @Test
    fun notApplicableShareAlikeDoesNotShowTheReleaseBlock() {
        openConfirmation("NOT_APPLICABLE")
        click("TEST-LICENSE: Test attribution")
        show("Share-alike confirmation: Not applicable")
        compose.onNodeWithText("Public release is blocked until share-alike confirmation is recorded.")
            .assertDoesNotExist()
    }

    private fun openConfirmation(confirmation: String) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(SeedJson.encode(inventoryDocument(confirmation)))
        show(store)
        click("Attributions and licenses")
    }

    private fun show(
        store: LocalStore,
        readSeed: () -> String? = { null },
        readCatalogNotice: () -> String? = { null },
        readLicenseNotices: () -> List<LicenseNoticeOption> = { emptyList() },
    ) {
        compose.setContent {
            CompanionApp(
                gateway = RoomNavigationGateway(
                    store,
                    readSeed,
                    PermissivePracticeAudio(),
                    readCatalogNotice = readCatalogNotice,
                    readLicenseNotices = readLicenseNotices,
                ),
                onRetryOpen = {},
                today = { "2026-09-21" },
                practiceAudio = PermissivePracticeAudio(),
            )
        }
    }

    private fun click(text: String) {
        val node = compose.onNodeWithText(text, substring = false)
        node.assertExists()
        runCatching { node.performScrollTo() }
        compose.onNodeWithText(text, substring = false).performClick()
    }

    private fun show(text: String) {
        compose.onNodeWithText(text, substring = false).assertExists()
        runCatching { compose.onNodeWithText(text, substring = false).performScrollTo() }
        compose.onNodeWithText(text, substring = false).assertIsDisplayed()
    }

    private fun inventoryDocument(confirmation: String) = minimalDocument().copy(
        planDefinitions = listOf(
            PlanDefinitionSeed(
                id = "plan-book",
                segmentId = "segment-1",
                activityType = "BOOK",
                instruction = "Use your copy of the book for this lesson",
                sortOrder = 1,
                required = true,
                lessonNumber = 1,
            ),
        ),
        sourceRecords = listOf(source().copy(shareAlikeConfirmation = confirmation)),
        sourceTexts = listOf(text(matchVerified = true)),
        assetRightsRecords = listOf(
            rights(),
            rights(
                id = "rights-bad",
                assetIdentifier = "audio-bad",
                licenseStatus = "INCOMPATIBLE",
                inclusionResult = "EXCLUDED",
            ),
        ),
        audioModels = listOf(
            audio(),
            audio(id = "audio-bad", rightsRecordId = "rights-bad"),
            AudioModelSeed(
                id = "audio-missing",
                localAudioReference = "audio/audio-missing",
                sourceTextId = null,
                sourceRecordId = null,
                rightsRecordId = null,
            ),
        ),
    )
}
