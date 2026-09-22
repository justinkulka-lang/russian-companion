package dev.russiancompanion.v2

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.V2UnitState
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.advancementDecision
import dev.russiancompanion.v2.curriculum.canOpenUnit
import dev.russiancompanion.v2.curriculum.continueTarget
import dev.russiancompanion.v2.curriculum.todaysSession
import dev.russiancompanion.v2.curriculum.unit
import dev.russiancompanion.v2.curriculum.unitState
import dev.russiancompanion.v2.ui.V2App
import dev.russiancompanion.v2.ui.V2ContentInset
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], qualifiers = "w411dp-h914dp-xxhdpi")
class V2RebuildTest {
    @get:Rule
    val compose = createComposeRule()

    private val stores = mutableListOf<LocalStore>()

    @After
    fun close() {
        stores.forEach { it.close() }
    }

    @Test
    fun packValidatesAndRefusesGeneratedOrCatalogBoundContent() {
        val pack = pack()
        val validation = V2Validator.validate(pack)
        assertTrue(validation.errors.joinToString(), validation.ok)
        assertEquals(30, pack.units.size)
        assertEquals("DEVELOPMENT", pack.unit(1)?.installState)
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(1)?.readiness)
        assertEquals("DEVELOPMENT", pack.unit(2)?.installState)
        assertEquals("NOT_PREPARED", pack.unit(2)?.readiness)
        assertEquals("PENDING_CURRICULUM_REVIEWER", pack.unit(1)?.mappingStatus)
        val unit1Text = pack.unit(1)!!.topicSummary + pack.unit(1)!!.sessions.flatMap { it.steps }.joinToString(" ") { it.body }
        assertFalse(unit1Text.contains("Penguin-aligned"))
        assertFalse(unit1Text.contains("This installed unit"))
        assertTrue(pack.unit(1)!!.sessions.flatMap { it.steps }.none { it.kind == "UNAVAILABLE" || !it.unavailableReason.isNullOrBlank() })
        assertTrue(pack.unit(1)!!.sessions.none { session -> session.steps.any { it.kind == "LISTEN" } })
        assertEquals(0, pack.admissions.mappedPronunciationAudio)
        pack.unit(1)!!.objectives.filter { it.kind == "RECOGNITION" }.forEach { objective ->
            assertTrue(objective.id, objective.exampleIds.size >= 2)
            assertTrue(objective.instructionStepIds.isNotEmpty())
            assertTrue(objective.checkItemIds.isNotEmpty())
            assertTrue(objective.remediationStepIds.isNotEmpty())
        }
        assertTrue(pack.units.drop(2).all { it.installState == "NOT_YET_INSTALLED" })
        assertEquals(0, pack.admissions.generatedRussianWords)
        assertEquals(0, pack.admissions.penguinProseAssets)
        assertEquals(0, pack.admissions.catalogAudioBoundToUnits)
        assertEquals("PENDING_QUALIFIED_REVIEW", pack.linguisticReview.status)
        assertTrue(pack.items.none { it.isGovernance() })
        assertTrue(pack.items.all { !it.promptLetter.isNullOrBlank() })
        assertTrue(pack.references.all { it.classification.isNotBlank() })
        assertEquals("LINGUISTIC_INSTRUCTION", pack.references.first { it.id == "ref-stress" }.classification)
        assertEquals("PRODUCT_HELP", pack.references.first { it.id == "ref-study" }.classification)
        assertEquals("OPERATIONAL_GUIDANCE", pack.references.first { it.id == "ref-limits" }.classification)
        assertEquals("NOT_REQUIRED", pack.references.first { it.id == "ref-study" }.reviewStatus)
        assertEquals("NOT_REQUIRED", pack.references.first { it.id == "ref-limits" }.reviewStatus)
        assertTrue(pack.units.none { unit ->
            unit.sessions.any { session -> session.steps.any { it.itemIds.isNotEmpty() && it.body.contains("The New Penguin Russian Course") && it.body.contains("exercise") } }
        })
        val seed = File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText()
        assertFalse(seed.contains("The New Penguin Russian Course lesson prose"))
        assertFalse(seed.contains("u1-i-no-catalog-word"))
        assertFalse(seed.contains("u2-i-no-score"))
        assertFalse(seed.contains("u2-i-handwriting-source"))
        assertFalse(seed.contains("u2-i-review-unit1"))
    }

    @Test
    fun approvedReviewRequiresACompleteMatchingRecord() {
        val pack = pack()
        val premature = pack.copy(
            linguisticReview = pack.linguisticReview.copy(status = "APPROVED"),
        )
        val prematureResult = V2Validator.validate(premature)
        assertFalse(prematureResult.ok)
        assertTrue(prematureResult.errors.any { it.contains("APPROVED review is missing") })

        val unknown = pack.copy(
            linguisticReview = pack.linguisticReview.copy(status = "SIGNED_OFF"),
        )
        val unknownResult = V2Validator.validate(unknown)
        assertFalse(unknownResult.ok)
        assertTrue(unknownResult.errors.any { it.contains("Unrecognized linguistic review status") })

        val digest = V2Validator.instructionalContentDigest(pack)
        val complete = pack.copy(
            units = pack.units.map { unit ->
                if (unit.readiness == "CONTENT_COMPLETE_REVIEW_PENDING") {
                    unit.copy(readiness = "REVIEW_APPROVED")
                } else {
                    unit
                }
            },
            linguisticReview = pack.linguisticReview.copy(
                status = "APPROVED",
                reviewer = "Qualified Reviewer",
                packVersion = pack.packVersion,
                reviewedContentDigest = digest,
                curriculumMappingId = "map-owned-edition",
            ),
        )
        val completeResult = V2Validator.validate(complete)
        assertTrue(completeResult.errors.joinToString(), completeResult.ok)
    }

    @Test
    fun installedUnitRejectsPendingAndRejectedInstructionalRecords() {
        val installed = installedUnitOne(pack())
        val pending = V2Validator.validate(installed)
        assertTrue(pending.errors.any { it.contains("Check u1-i-а-identity review status PENDING_QUALIFIED_REVIEW") })
        assertTrue(pending.errors.any { it.contains("Letter ") && it.contains("PENDING_QUALIFIED_REVIEW") })
        assertTrue(pending.errors.any { it.contains("Example ") && it.contains("PENDING_QUALIFIED_REVIEW") })

        val approved = approveInstruction(installed)
        val approvedResult = V2Validator.validate(approved)
        assertTrue(
            approvedResult.errors.joinToString(),
            approvedResult.errors.none {
                it.contains("review status") || it.contains("NOT_REQUIRED") || it.contains("approval must bind")
            },
        )

        val rejected = approved.copy(
            items = approved.items.map { item ->
                if (item.id == "u1-i-а-identity") item.copy(reviewStatus = "REJECTED") else item
            },
        )
        val rejectedResult = V2Validator.validate(rejected)
        assertTrue(rejectedResult.errors.any { it.contains("Check u1-i-а-identity review status REJECTED") })

        val letterId = approved.examples.first { it.objectiveId == "obj-u1-letter-identity" }.letterId
        val notRequiredLetter = approved.copy(
            letters = approved.letters.map { letter ->
                if (letter.id == letterId) letter.copy(reviewStatus = "NOT_REQUIRED") else letter
            },
        )
        val notRequiredResult = V2Validator.validate(notRequiredLetter)
        assertTrue(notRequiredResult.errors.any { it.contains("Letter $letterId") && it.contains("cannot be NOT_REQUIRED") })

        val notRequiredReference = approved.copy(
            references = approved.references.map { reference ->
                if (reference.id == "ref-confusable") reference.copy(reviewStatus = "NOT_REQUIRED") else reference
            },
        )
        val referenceResult = V2Validator.validate(notRequiredReference)
        assertTrue(
            referenceResult.errors.any {
                it.contains("Reference ref-confusable") && it.contains("cannot be NOT_REQUIRED")
            },
        )
    }

    @Test
    fun linguisticReferencesCannotUseNotRequired() {
        val approved = approveInstruction(installedUnitOne(pack()))
        listOf(
            "ref-stress",
            "ref-pronunciation",
            "ref-transliteration",
            "ref-decoding",
            "ref-cyrillic-print",
        ).forEach { id ->
            val reference = approved.references.first { it.id == id }
            assertEquals("LINGUISTIC_INSTRUCTION", reference.classification)
            val mutated = withReachableReferences(
                approved.copy(
                    references = approved.references.map { entry ->
                        if (entry.id == id) entry.copy(reviewStatus = "NOT_REQUIRED") else entry
                    },
                ),
                1,
                listOf(id),
            )
            val result = V2Validator.validate(mutated)
            assertTrue(
                result.errors.joinToString(),
                result.errors.any { it.contains("Reference $id") && it.contains("cannot be NOT_REQUIRED") },
            )
        }
    }

    @Test
    fun productHelpAndOperationalGuidanceMayUseNotRequired() {
        val approved = approveInstruction(installedUnitOne(pack()))
        val license = approved.references.first { it.id == "ref-limits" }.copy(
            id = "ref-attributions",
            title = "Bundled component notices",
            summary = "License names for components shipped with the app.",
            detail = "This note identifies licenses. It does not teach Russian.",
            classification = "LICENSE_OR_ATTRIBUTION",
            reviewStatus = "NOT_REQUIRED",
            searchTerms = listOf("licenses"),
        )
        val reachable = rebindReview(
            withReachableReferences(
                approved.copy(references = approved.references + license),
                1,
                listOf("ref-study", "ref-limits", "ref-attributions"),
            ),
        )
        val result = V2Validator.validate(reachable)
        listOf("ref-study" to "PRODUCT_HELP", "ref-limits" to "OPERATIONAL_GUIDANCE", "ref-attributions" to "LICENSE_OR_ATTRIBUTION")
            .forEach { (id, classification) ->
                val entry = reachable.references.first { it.id == id }
                assertEquals(classification, entry.classification)
                assertEquals("NOT_REQUIRED", entry.reviewStatus)
                assertTrue(
                    result.errors.joinToString(),
                    result.errors.none { it.contains(id) },
                )
            }
    }

    @Test
    fun referencesRejectMissingAndUnknownClassifications() {
        val source = pack()
        val missing = source.copy(
            references = source.references.map { entry ->
                if (entry.id == "ref-stress") entry.copy(classification = "") else entry
            },
        )
        val missingResult = V2Validator.validate(missing)
        assertTrue(
            missingResult.errors.any {
                it.contains("Reference ref-stress") && it.contains("missing a classification")
            },
        )

        val unknown = source.copy(
            references = source.references.map { entry ->
                if (entry.id == "ref-decoding") entry.copy(classification = "SIGNED_OFF") else entry
            },
        )
        val unknownResult = V2Validator.validate(unknown)
        assertTrue(
            unknownResult.errors.any {
                it.contains("Reference ref-decoding") && it.contains("unknown classification")
            },
        )
    }

    @Test
    fun noUnitCanBeInstalledWithPendingReviewOrMapping() {
        val source = pack()
        listOf(1, 2).forEach { number ->
            val installed = source.copy(
                units = source.units.map { unit ->
                    if (unit.number == number) {
                        unit.copy(installState = "INSTALLED", readiness = "INSTALLABLE")
                    } else {
                        unit
                    }
                },
            )
            val result = V2Validator.validate(installed)
            assertFalse(result.ok)
            assertTrue(
                result.errors.joinToString(),
                result.errors.any { it.contains("Installed unit $number") && it.contains("APPROVED qualified-review record") },
            )
            assertTrue(
                result.errors.joinToString(),
                result.errors.any { it.contains("Installed unit $number") && it.contains("mapping must be APPROVED") },
            )
        }
        val pendingReview = source.copy(
            units = source.units.map { unit ->
                if (unit.number == 1) unit.copy(installState = "INSTALLED") else unit
            },
        )
        val pendingResult = V2Validator.validate(pendingReview)
        assertTrue(
            pendingResult.errors.any { it.contains("Unit 1") && it.contains("cannot be INSTALLED while review is pending") },
        )
    }

    @Test
    fun installedUnitsAreRejectedWhileRequiredExperiencesAreMissing() {
        val pack = pack()
        val installed = pack.copy(
            units = pack.units.map { unit ->
                if (unit.number in 1..2) unit.copy(installState = "INSTALLED") else unit
            },
        )
        val result = V2Validator.validate(installed)
        assertFalse(result.ok)
        assertTrue(result.errors.any { it.contains("unavailable required experiences") || it.contains("APPROVED") })
    }

    @Test
    fun unitsThreeToThirtyAreNotInstalledAndDoNotJump() {
        val pack = pack()
        val learner = LocalStore.openInMemory(context()).also { stores += it }.v2LearnerView()
        val unit3 = pack.unit(3)!!
        assertEquals(V2UnitState.NOT_YET_INSTALLED, unitState(pack, unit3, learner))
        assertFalse(canOpenUnit(unitState(pack, unit3, learner)))
        assertTrue(unit3.sessions.isEmpty())
        assertTrue(unit3.objectives.isEmpty())
    }

    @Test
    fun unitOneProgressesInOrderAndRestoresAfterRestart() {
        val name = "v2-restart-${System.nanoTime()}.db"
        val store = LocalStore.openNamed(context(), name).also { stores += it }
        val pack = pack()
        val first = pack.unit(1)!!.sessions.first()
        store.v2SaveProfile(
            store.v2Profile().copy(
                onboardingCompleted = true,
                currentSessionId = first.id,
                currentStepIndex = 0,
            ),
        )
        val before = continueTarget(pack, store.v2LearnerView())
        assertEquals(first.id, before.sessionId)
        assertEquals(0, before.stepIndex)
        store.v2CompleteStep(first.steps.first().id, "2026-09-22T12:00")
        store.v2SaveProfile(store.v2Profile().copy(currentSessionId = first.id, currentStepIndex = 1))
        val after = continueTarget(pack, store.v2LearnerView())
        assertEquals(first.id, after.sessionId)
        assertEquals(1, after.stepIndex)
        store.close()
        stores.remove(store)
        val reopened = LocalStore.openNamed(context(), name).also { stores += it }
        val restored = continueTarget(pack, reopened.v2LearnerView())
        assertEquals(first.id, restored.sessionId)
        assertEquals(1, restored.stepIndex)
        assertTrue(first.steps.first().id in reopened.v2LearnerView().completedStepIds)
    }

    @Test
    fun unitTwoRequiresUnitOneAndCannotUseUnrelatedCatalogAudio() {
        val pack = pack()
        val store = LocalStore.openInMemory(context()).also { stores += it }
        val locked = unitState(pack, pack.unit(2)!!, store.v2LearnerView())
        assertEquals(V2UnitState.LOCKED, locked)
        store.v2CompleteUnit(1, "2026-09-22T12:00")
        store.v2SaveProfile(store.v2Profile().copy(currentUnitNumber = 2, onboardingCompleted = true))
        assertEquals(V2UnitState.CURRENT, unitState(pack, pack.unit(2)!!, store.v2LearnerView()))
        assertTrue(pack.unit(1)!!.sessions.flatMap { it.steps }.none { it.unavailableReason != null })
        assertTrue(pack.unit(2)!!.sessions.flatMap { it.steps }.any { it.unavailableReason != null })
        assertEquals(0, pack.admissions.catalogAudioBoundToUnits)
    }

    @Test
    fun advancementNeedsMoreThanOneCheckAndBookAttestation() {
        val pack = pack()
        val store = LocalStore.openInMemory(context()).also { stores += it }
        val unit = pack.unit(1)!!
        unit.sessions.flatMap { it.steps }.filter { it.kind == "ORIENT" || it.kind == "TEACH" }.forEach {
            store.v2CompleteStep(it.id, "2026-09-22T12:00")
        }
        val early = advancementDecision(pack, unit, store.v2LearnerView())
        assertFalse(early.allowed)
        store.v2AttestBook(1, "2026-09-22T12:01")
        store.v2RecordAttempt("u1-i-а-identity", "Cyrillic А", true, "2026-09-22T12:02")
        val still = advancementDecision(pack, unit, store.v2LearnerView())
        assertFalse(still.allowed)
        store.v2RecordAttempt("u1-i-р-not-p", "The Cyrillic letter Р", true, "2026-09-22T12:03")
        val ready = advancementDecision(pack, unit, store.v2LearnerView())
        assertTrue(ready.remaining.joinToString(), ready.allowed)
    }

    @Test
    fun todayShowsOneBoundedSessionInsteadOfTheUnitInventory() {
        val pack = pack()
        val store = LocalStore.openInMemory(context()).also { stores += it }
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, dailyMinutes = 35))
        val today = todaysSession(pack.unit(1)!!, store.v2LearnerView())
        assertEquals(pack.unit(1)!!.sessions.first().id, today?.id)
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        compose.onNodeWithTag("v2-today-unit").assertIsDisplayed()
        compose.onNodeWithTag("v2-today-objective").assertIsDisplayed()
        compose.onNodeWithText("Today’s session").assertIsDisplayed()
        compose.onNodeWithText("Show full unit plan").assertIsDisplayed()
        compose.onNodeWithText("Settings and data").assertDoesNotExist()
        compose.onNodeWithText("Work in your book").assertDoesNotExist()
        assertAtContentInset("v2-today-unit")
        assertAtContentInset("v2-today-objective")
    }

    @Test
    @Config(sdk = [33], qualifiers = "w411dp-h914dp-xxhdpi", fontScale = 2.0f)
    fun primaryContentStartsAtTheInsetWhenFontIsLarge() {
        val store = LocalStore.openInMemory(context()).also { stores += it }
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true))
        compose.setContent { V2App(store = store, pack = pack(), onRetryOpen = {}) }
        assertAtContentInset("v2-today-unit")
        assertAtContentInset("v2-today-objective")
        compose.onNodeWithText("Course").performClick()
        compose.onNodeWithTag("v2-course-intro").performScrollTo()
        assertAtContentInset("v2-course-intro")
        compose.onNodeWithText("Unit 1 · Printed letters", substring = true).performClick()
        assertAtContentInset("v2-back")
        assertAtContentInset("v2-unit-heading")
        assertAtContentInset("v2-unit-state")
        assertAtContentInset("v2-unit-install")
        compose.onNodeWithTag("v2-section-letter-board").performScrollTo()
        assertAtContentInset("v2-section-letter-board")
        compose.onNodeWithTag("v2-section-sessions").performScrollTo()
        assertAtContentInset("v2-section-sessions")
        compose.onNodeWithTag("v2-back").performClick()
        compose.onNodeWithText("Practice").performClick()
        assertAtContentInset("v2-practice-heading")
        compose.onNodeWithText("Alphabet Lab").performClick()
        assertAtContentInset("v2-back")
        assertAtContentInset("v2-section-confusable")
        compose.onNodeWithTag("v2-confusable-Р").performScrollTo()
        assertAtContentInset("v2-confusable-Р")
        compose.onNodeWithTag("v2-back").performClick()
        compose.onNodeWithText("Reference").performClick()
        assertAtContentInset("v2-reference-heading")
        compose.onNodeWithText("Progress").performClick()
        assertAtContentInset("v2-progress-heading")
        compose.onNodeWithText("Settings, appearance, and attributions").performScrollTo().performClick()
        assertAtContentInset("v2-settings-heading")
        assertAtContentInset("v2-back")
    }

    @Test
    fun onboardingAndPrimaryControlsAreVisible() {
        val store = LocalStore.openInMemory(context()).also { stores += it }
        compose.setContent { V2App(store = store, pack = pack(), onRetryOpen = {}) }
        compose.onNodeWithTag("v2-onboarding-welcome").assertIsDisplayed()
        compose.onNodeWithTag("v2-onboarding-next").performClick()
        compose.onNodeWithText("I have the book").assertIsDisplayed()
        repeat(3) { compose.onNodeWithTag("v2-onboarding-next").performClick() }
        compose.onNodeWithText("Start Unit 1").performClick()
        compose.onNodeWithTag("v2-today-unit").assertIsDisplayed()
        compose.onNodeWithTag("v2-continue").assertIsDisplayed()
        compose.onNodeWithText("Course").performClick()
        compose.onNodeWithText("Unit 1 · Printed letters", substring = true).assertIsDisplayed()
        compose.onAllNodesWithText("Not yet installed", substring = true)[0].assertIsDisplayed()
        compose.onNodeWithText("Practice").performClick()
        compose.onNodeWithText("Alphabet Lab").assertIsDisplayed()
        compose.onNodeWithText("Reference").performClick()
        compose.onNodeWithText("Cyrillic print forms").assertIsDisplayed()
        compose.onNodeWithText("Progress").performClick()
        compose.onNodeWithText("not fluency", substring = true).assertIsDisplayed()
    }

    @Test
    fun unitAndLabHeadingsStayInsideTheRoot() {
        val store = LocalStore.openInMemory(context()).also { stores += it }
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true))
        compose.setContent { V2App(store = store, pack = pack(), onRetryOpen = {}) }
        compose.onNodeWithText("Course").performClick()
        compose.onNodeWithText("Unit 1 · Printed letters", substring = true).performClick()
        compose.onNodeWithTag("v2-back").assertIsDisplayed()
        compose.onNodeWithTag("v2-unit-heading").assertIsDisplayed()
        assertAtContentInset("v2-back")
        assertAtContentInset("v2-unit-heading")
        assertAtContentInset("v2-unit-state")
        assertAtContentInset("v2-section-letter-board")
        compose.onNodeWithTag("v2-letter-board").assertIsDisplayed()
        compose.onNodeWithTag("v2-back").performClick()
        compose.onNodeWithText("Practice").performClick()
        compose.onNodeWithText("Alphabet Lab").performClick()
        compose.onNodeWithTag("v2-letter-board").assertIsDisplayed()
        compose.onNodeWithTag("v2-confusable-Р").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("v2-print-pair-grid").performScrollTo().assertIsDisplayed()
        assertAtContentInset("v2-back")
        assertAtContentInset("v2-section-confusable")
    }

    @Test
    fun noInternetPermissionIsDeclared() {
        val manifest = File(repo(), "app/src/main/AndroidManifest.xml").readText()
        assertFalse(manifest.contains("INTERNET"))
    }

    @Test
    fun upgradeKeepsExportAndRecordingRows() {
        val store = LocalStore.openInMemory(context()).also { stores += it }
        store.applySeed(LocalStore.readPackagedSeed(context()))
        val before = store.v2LearnerView()
        assertFalse(before.onboardingCompleted)
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 1))
        assertTrue(store.v2LearnerView().onboardingCompleted)
        assertTrue(File(repo(), "app/src/main/java/dev/russiancompanion/data/local/DatabaseMigrations.kt").readText().contains("MIGRATION_6_7"))
    }

    private fun assertAtContentInset(tag: String) {
        val node = compose.onNodeWithTag(tag)
        val unclipped = node.getUnclippedBoundsInRoot()
        assertTrue("$tag starts at ${unclipped.left}", unclipped.left >= V2ContentInset)
        val visible = node.getBoundsInRoot()
        if (visible.right - visible.left > 1.dp && visible.bottom - visible.top > 1.dp) {
            assertEquals("$tag visible left", unclipped.left, visible.left)
            assertTrue("$tag visible at ${visible.left}", visible.left >= V2ContentInset)
        }
    }

    private fun installedUnitOne(source: dev.russiancompanion.v2.curriculum.V2CurriculumPack): dev.russiancompanion.v2.curriculum.V2CurriculumPack {
        val next = source.copy(
            units = source.units.map { unit ->
                if (unit.number == 1) {
                    unit.copy(installState = "INSTALLED", mappingStatus = "APPROVED", readiness = "INSTALLABLE")
                } else {
                    unit
                }
            },
        )
        val digest = V2Validator.instructionalContentDigest(next)
        return next.copy(
            linguisticReview = next.linguisticReview.copy(
                status = "APPROVED",
                reviewer = "Qualified Reviewer",
                packVersion = next.packVersion,
                reviewedContentDigest = digest,
                curriculumMappingId = "map-owned-edition",
            ),
        )
    }

    private fun approveInstruction(
        source: dev.russiancompanion.v2.curriculum.V2CurriculumPack,
    ): dev.russiancompanion.v2.curriculum.V2CurriculumPack {
        return source.copy(
            items = source.items.map { it.copy(reviewStatus = "APPROVED") },
            examples = source.examples.map { it.copy(reviewStatus = "APPROVED") },
            letters = source.letters.map { it.copy(reviewStatus = "APPROVED") },
            references = source.references.map { reference ->
                if (reference.classification == "LINGUISTIC_INSTRUCTION") {
                    reference.copy(reviewStatus = "APPROVED")
                } else if (reference.reviewStatus == "NOT_REQUIRED") {
                    reference
                } else {
                    reference.copy(reviewStatus = "APPROVED")
                }
            },
        )
    }

    private fun rebindReview(
        source: dev.russiancompanion.v2.curriculum.V2CurriculumPack,
    ): dev.russiancompanion.v2.curriculum.V2CurriculumPack {
        val digest = V2Validator.instructionalContentDigest(source)
        return source.copy(
            linguisticReview = source.linguisticReview.copy(
                status = "APPROVED",
                reviewer = "Qualified Reviewer",
                packVersion = source.packVersion,
                reviewedContentDigest = digest,
                curriculumMappingId = "map-owned-edition",
            ),
        )
    }

    private fun withReachableReferences(
        source: dev.russiancompanion.v2.curriculum.V2CurriculumPack,
        unitNumber: Int,
        referenceIds: List<String>,
    ): dev.russiancompanion.v2.curriculum.V2CurriculumPack {
        return source.copy(
            units = source.units.map { unit ->
                if (unit.number != unitNumber || unit.sessions.isEmpty()) {
                    unit
                } else {
                    val session = unit.sessions.first()
                    val step = session.steps.first()
                    val updated = step.copy(referenceIds = (step.referenceIds + referenceIds).distinct())
                    unit.copy(
                        sessions = listOf(session.copy(steps = listOf(updated) + session.steps.drop(1))) +
                            unit.sessions.drop(1),
                    )
                }
            },
        )
    }

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun context(): Context = ApplicationProvider.getApplicationContext()

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}

private fun dev.russiancompanion.v2.curriculum.V2CheckItem.isGovernance(): Boolean {
    return dev.russiancompanion.v2.curriculum.GOVERNANCE_PROMPT_MARKERS.any { marker ->
        "$id $kind $prompt $feedback".lowercase().contains(marker)
    }
}
