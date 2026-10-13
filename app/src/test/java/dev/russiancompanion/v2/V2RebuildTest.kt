package dev.russiancompanion.v2

import android.content.Context
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
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
import dev.russiancompanion.v2.curriculum.V2AudioAsset
import dev.russiancompanion.v2.curriculum.V2OwnedEditionMapping
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.V2UnitReviewRecord
import dev.russiancompanion.v2.curriculum.V2UnitState
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.advancementDecision
import dev.russiancompanion.v2.curriculum.distinctCorrectCheckCount
import dev.russiancompanion.v2.curriculum.canOpenUnit
import dev.russiancompanion.v2.curriculum.PRESCRIBED_CHECK
import dev.russiancompanion.v2.curriculum.V2CheckItem
import dev.russiancompanion.v2.curriculum.continueTarget
import dev.russiancompanion.v2.curriculum.item
import dev.russiancompanion.v2.curriculum.prepareGovernedAttempt
import dev.russiancompanion.v2.curriculum.stepContentDigest
import dev.russiancompanion.v2.curriculum.verifiedResume
import dev.russiancompanion.v2.curriculum.todaysSession
import dev.russiancompanion.v2.curriculum.unit
import dev.russiancompanion.v2.curriculum.unitState
import dev.russiancompanion.v2.ui.V2App
import dev.russiancompanion.v2.ui.V2ContentInset
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
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
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(2)?.readiness)
        assertEquals("PENDING_CURRICULUM_REVIEWER", pack.unit(2)?.mappingStatus)
        listOf(3, 4, 5).forEach { number ->
            val unit = pack.unit(number)!!
            assertEquals("DEVELOPMENT", unit.installState)
            assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", unit.readiness)
            assertEquals("PENDING_CURRICULUM_REVIEWER", unit.mappingStatus)
            assertEquals(listOf(number - 1), unit.prerequisiteUnitNumbers)
            assertTrue(unit.objectives.any { it.kind == "RECOGNITION" })
            assertTrue(unit.objectives.any { it.kind == "SELF_ATTESTATION" })
            assertTrue(unit.sessions.flatMap { it.steps }.any { it.kind == "TEACH" })
            assertTrue(unit.sessions.flatMap { it.steps }.any { it.kind == "CHECK" })
            val independent = unit.sessions.flatMap { it.steps }.filter { it.kind == "CHECK" }.flatMap { it.itemIds }.distinct()
            assertTrue(independent.size >= if (number == 3) 12 else 14)
            assertTrue(unit.sessions.flatMap { it.steps }.none { it.kind == "UNAVAILABLE" || !it.unavailableReason.isNullOrBlank() })
            assertTrue(unit.sessions.flatMap { it.steps }.filter { it.kind == "BOOK" }.all { !it.bookDirection.isNullOrBlank() })
            assertFalse(unit.topicSummary.contains("Content-complete"))
            val decision = advancementDecision(pack, unit, dev.russiancompanion.v2.curriculum.V2LearnerView(
                onboardingCompleted = true,
                currentUnitNumber = number,
                startingPoint = V2StartingPoint.NEW_TO_CYRILLIC,
                hasPenguin = true,
                hasAnki = false,
                hasBinder = false,
                hasTutor = false,
                dailyMinutes = 25,
                transliterationEnabled = true,
                currentSessionId = null,
                currentStepIndex = 0,
                completedStepIds = unit.sessions.flatMap { it.steps }.map { it.id }.toSet(),
                correctItemIds = emptySet(),
                bookAttestedUnits = setOf(number),
                completedUnitNumbers = emptySet(),
            ))
            assertFalse(decision.allowed)
            assertTrue(decision.remaining.any { it.contains("distinct in-app checks") })
        }
        assertEquals((1..30).toList(), pack.unitReviews.map { it.unitNumber })
        assertTrue(pack.unitReviews.all { it.status == "IMPLEMENTED_PENDING_FINAL_AUDIT" && it.reviewer.isNullOrBlank() })
        assertTrue(pack.unit(2)!!.sessions.flatMap { it.steps }.none { it.kind == "UNAVAILABLE" || !it.unavailableReason.isNullOrBlank() })
        assertEquals("BOOK", pack.unit(2)!!.sessions.flatMap { it.steps }.first { it.id == "u2-st-hw" }.kind)
        assertEquals("BOOK", pack.unit(2)!!.sessions.flatMap { it.steps }.first { it.id == "u2-st-pron" }.kind)
        val reviewObjective = pack.unit(2)!!.objectives.first { it.id == "obj-u2-review" }
        assertTrue(reviewObjective.exampleIds.size >= 2)
        assertTrue(reviewObjective.instructionStepIds.isNotEmpty())
        assertTrue(reviewObjective.practiceStepIds.isNotEmpty())
        assertTrue(reviewObjective.checkItemIds.isNotEmpty())
        assertTrue(reviewObjective.remediationStepIds.isNotEmpty())
        assertEquals(0, pack.audioAssets.size)
        assertEquals(0, pack.handwritingAssets.size)
        assertEquals(30, pack.ownedEditionMappings.size)
        assertTrue(pack.ownedEditionMappings.all { it.status == "PENDING_CURRICULUM_REVIEWER" && it.reviewer.isNullOrBlank() && it.reviewedContentDigest.isNullOrBlank() })
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
        assertTrue((11..15).all { pack.unit(it)!!.installState == "DEVELOPMENT" })
        assertTrue((16..20).all { pack.unit(it)!!.installState == "DEVELOPMENT" })
        assertTrue(listOf(16, 18, 19, 20).all { pack.unit(it)!!.readiness == "CONTENT_COMPLETE_REVIEW_PENDING" })
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(17)!!.readiness)
        assertTrue(pack.units.drop(25).all { it.installState == "DEVELOPMENT" && it.mappingStatus == "PENDING_CURRICULUM_REVIEWER" })
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(28)!!.readiness)
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(26)!!.readiness)
        assertEquals(emptySet<Int>(), pack.units.filter { it.readiness == "NOT_PREPARED" }.map { it.number }.toSet())
        assertEquals(0, pack.admissions.generatedRussianWords)
        assertEquals(0, pack.admissions.penguinProseAssets)
        assertEquals(0, pack.admissions.catalogAudioBoundToUnits)
        assertEquals("IMPLEMENTED_PENDING_FINAL_AUDIT", pack.linguisticReview.status)
        assertEquals((1..30).toList(), pack.unitReviews.map { it.unitNumber })
        assertTrue(pack.unitReviews.all { it.status == "IMPLEMENTED_PENDING_FINAL_AUDIT" && it.reviewer.isNullOrBlank() && it.reviewedContentDigest.isNullOrBlank() })
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

        val staged = pack.copy(
            units = pack.units.map { unit ->
                if (unit.number in 1..2 && unit.readiness == "CONTENT_COMPLETE_REVIEW_PENDING") {
                    unit.copy(readiness = "REVIEW_APPROVED")
                } else {
                    unit
                }
            },
            linguisticReview = pack.linguisticReview.copy(
                status = "APPROVED",
                reviewer = "Qualified Reviewer",
                reviewerQualification = "Advanced Russian and curriculum review attested.",
                packVersion = pack.packVersion,
                reviewedContentDigest = V2Validator.contentReviewDigest(pack),
                curriculumMappingId = "map-owned-edition",
            ),
        )
        val packApprovedOnly = V2Validator.validate(staged)
        assertFalse(packApprovedOnly.ok)
        assertTrue(
            packApprovedOnly.errors.joinToString("\n"),
            packApprovedOnly.errors.any { it.contains("Unit 1 readiness claims review approval") } &&
                packApprovedOnly.errors.any { it.contains("Unit 2 readiness claims review approval") },
        )

        val unitOneOnly = approveUnitReview(staged, 1)
        val unitOneResult = V2Validator.validate(unitOneOnly)
        assertFalse(unitOneResult.ok)
        assertTrue(unitOneResult.errors.any { it.contains("Unit 2 readiness claims review approval") })
        assertTrue(unitOneResult.errors.none { it.contains("Unit 1 readiness claims review approval") })

        val bothContentComplete = approveUnitReview(unitOneOnly, 2)
        val bothResult = V2Validator.validate(bothContentComplete)
        assertTrue(bothResult.errors.joinToString("\n"), bothResult.ok)

        val unitThreeApproved = approveUnitReview(bothContentComplete, 3)
        assertEquals("IMPLEMENTED_PENDING_FINAL_AUDIT", unitThreeApproved.unitReviews.first { it.unitNumber == 4 }.status)
        assertEquals("IMPLEMENTED_PENDING_FINAL_AUDIT", unitThreeApproved.unitReviews.first { it.unitNumber == 5 }.status)
        assertEquals("APPROVED", unitThreeApproved.unitReviews.first { it.unitNumber == 1 }.status)
        assertEquals(
            V2Validator.unitContentReviewDigest(bothContentComplete, 1),
            V2Validator.unitContentReviewDigest(unitThreeApproved, 1),
        )
        assertEquals(
            V2Validator.unitContentReviewDigest(bothContentComplete, 2),
            V2Validator.unitContentReviewDigest(unitThreeApproved, 2),
        )
        val unitFourClaim = unitThreeApproved.copy(
            units = unitThreeApproved.units.map { unit ->
                if (unit.number == 4) unit.copy(readiness = "REVIEW_APPROVED") else unit
            },
        )
        val unitFourResult = V2Validator.validate(unitFourClaim)
        assertFalse(unitFourResult.ok)
        assertTrue(unitFourResult.errors.any { it.contains("Unit 4 readiness claims review approval") })
        assertTrue(unitFourResult.errors.none { it.contains("Unit 3 readiness claims review approval") })
        val mappedThree = unitThreeApproved.copy(
            units = unitThreeApproved.units.map { unit ->
                if (unit.number == 3) unit.copy(mappingStatus = "APPROVED") else unit
            },
        )
        val mappedResult = V2Validator.validate(mappedThree)
        assertFalse(mappedResult.ok)
        assertTrue(mappedResult.errors.any { it.contains("Unit 3 mapping must remain pending") })
        assertTrue(mappedResult.errors.none { it.contains("Unit 4 mapping") })
    }

    @Test
    fun installedUnitRejectsPendingAndRejectedInstructionalRecords() {
        val installed = installedUnitOne(pack())
        val pending = V2Validator.validate(installed)
        assertTrue(pending.errors.any { it.contains("Check u1-i-а-identity review status IMPLEMENTED_PENDING_FINAL_AUDIT") })
        assertTrue(pending.errors.any { it.contains("Letter ") && it.contains("IMPLEMENTED_PENDING_FINAL_AUDIT") })
        assertTrue(pending.errors.any { it.contains("Example ") && it.contains("IMPLEMENTED_PENDING_FINAL_AUDIT") })

        val approved = rebindReview(approveInstruction(installed))
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
                result.errors.any { it.contains("Installed unit $number") && it.contains("review chain is not OWNER_ACCEPTED") },
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
    fun unitsThreeToFiveStayDevelopmentAndLaterUnitsStayClosed() {
        val pack = pack()
        val learner = LocalStore.openInMemory(context()).also { stores += it }.v2LearnerView()
        val unit3 = pack.unit(3)!!
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", unit3.readiness)
        assertEquals("DEVELOPMENT", unit3.installState)
        assertEquals(V2UnitState.LOCKED, unitState(pack, unit3, learner))
        assertFalse(canOpenUnit(unitState(pack, unit3, learner)))
        assertTrue(unit3.sessions.isNotEmpty())
        assertTrue((11..15).all { pack.unit(it)!!.installState == "DEVELOPMENT" && pack.unit(it)!!.sessions.isNotEmpty() })
        assertTrue((16..20).all { pack.unit(it)!!.installState == "DEVELOPMENT" && pack.unit(it)!!.sessions.isNotEmpty() })
        assertTrue(pack.units.drop(25).all {
            it.installState == "DEVELOPMENT" && it.sessions.isNotEmpty() && it.objectives.isNotEmpty()
        })
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(28)!!.readiness)
        val store = LocalStore.openInMemory(context()).also { stores += it }
        store.v2CompleteUnit(1, "2026-09-23T12:00")
        store.v2CompleteUnit(2, "2026-09-23T12:00")
        store.v2SaveProfile(store.v2Profile().copy(currentUnitNumber = 3, onboardingCompleted = true))
        assertEquals("Unsupported historical prerequisite rows do not unlock current placement", V2UnitState.LOCKED, unitState(pack, unit3, store.v2LearnerView()))
        assertEquals(V2UnitState.LOCKED, unitState(pack, pack.unit(4)!!, store.v2LearnerView()))
        val attested = store.v2LearnerView().copy(
            completedStepIds = unit3.sessions.flatMap { it.steps }.map { it.id }.toSet(),
            bookAttestedUnits = setOf(3),
        )
        val blocked = advancementDecision(pack, unit3, attested)
        assertFalse(blocked.allowed)
        assertTrue(blocked.remaining.any { it.contains("distinct in-app checks") })
        val text = (3..5).joinToString(" ") { number ->
            val unit = pack.unit(number)!!
            unit.topicSummary + unit.mappingNote + unit.sessions.flatMap { it.steps }.joinToString(" ") { it.body + (it.bookDirection ?: "") }
        }
        assertFalse(text.contains("Penguin-aligned"))
        assertFalse(text.contains("The New Penguin Russian Course"))
        assertFalse(text.contains("Content-complete"))
        assertTrue(pack.items.any { it.id.startsWith("u3-") })
        assertTrue(pack.items.any { it.id.startsWith("u4-") })
        assertTrue(pack.items.any { it.id.startsWith("u5-") })
    }

    @Test
    fun genericBookDirectionCountersAndEmptyArraysCannotSatisfyContentCompleteness() {
        val pack = pack()
        val claimed = pack.copy(
            units = pack.units.map { unit ->
                if (unit.number == 3) bookDirectionOnly(unit) else unit
            },
        )
        val claimedResult = V2Validator.validate(claimed)
        assertFalse(claimedResult.ok)
        assertTrue(claimedResult.errors.any { it.contains("book direction does not replace") })
        assertTrue(claimedResult.errors.any { it.contains("no teach step") })

        val letterId = pack.letters.first().id
        val counted = claimed.copy(
            admissions = claimed.admissions.copy(mappedPronunciationAudio = 1),
            audioAssets = listOf(
                V2AudioAsset(
                    id = "audio-unrelated-unit-30",
                    sourceId = "src-unicode-cyrillic",
                    rightsId = "rights-unicode-cyrillic",
                    unitNumber = 30,
                    letterId = letterId,
                    intakeReviewStatus = "APPROVED",
                ),
            ),
        )
        val countedResult = V2Validator.validate(counted)
        assertFalse(countedResult.ok)
        assertTrue(countedResult.errors.any { it.contains("book direction does not replace") })
        assertTrue(countedResult.errors.none { it.contains("Unit 3") && it.contains("mapped pronunciation audio record") })

        val emptied = pack.copy(
            units = pack.units.map { unit ->
                if (unit.number == 3) {
                    unit.copy(readiness = "CONTENT_COMPLETE_REVIEW_PENDING", sessions = emptyList(), objectives = emptyList())
                } else {
                    unit
                }
            },
        )
        val emptiedResult = V2Validator.validate(emptied)
        assertFalse(emptiedResult.ok)
        assertTrue(emptiedResult.errors.any { it.contains("empty sessions or objectives do not count") })
    }

    @Test
    fun durablePackIdentityStaysHonestAndUnitIsolationHolds() {
        val pack = pack()
        assertEquals("v2-curriculum-foundation", pack.packId)
        assertEquals(3, pack.packVersion)
        assertEquals("rev-pending-v2-curriculum", pack.linguisticReview.id)
        assertEquals("IMPLEMENTED_PENDING_FINAL_AUDIT", pack.linguisticReview.status)
        val seed = File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText()
        val identity = Json.parseToJsonElement(seed).jsonObject
        assertEquals(JsonPrimitive("v2-curriculum-foundation"), identity["packId"])
        assertEquals(JsonPrimitive(3), identity["packVersion"])
        assertEquals(JsonPrimitive("rev-pending-v2-curriculum"), identity["linguisticReview"]!!.jsonObject["id"])
        assertFalse(seed.contains("v2-units-1-2-foundation"))
        assertFalse(seed.contains("rev-pending-units-1-2"))
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(1)!!.readiness)
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(2)!!.readiness)
        listOf(3, 4, 5).forEach { number ->
            val unit = pack.unit(number)!!
            assertEquals("DEVELOPMENT", unit.installState)
            assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", unit.readiness)
            assertEquals("PENDING_CURRICULUM_REVIEWER", unit.mappingStatus)
            val text = unit.topicSummary + unit.mappingNote + unit.sessions.flatMap { it.steps }.joinToString(" ") {
                it.title + it.body + (it.bookDirection ?: "")
            }
            assertFalse(text.contains("Content-complete"))
            val packet = File(repo(), "review/unit-$number/UNIT_${number}_REVIEW_PACKET.md").readText()
            assertTrue(packet.contains("content-complete and review-pending"))
            assertFalse(packet.contains("Content-complete development"))
            assertTrue(packet.contains("IMPLEMENTED_PENDING_FINAL_AUDIT"))
        }
        listOf(6, 8, 9, 10).forEach { number ->
            val unit = pack.unit(number)!!
            assertEquals("DEVELOPMENT", unit.installState)
            assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", unit.readiness)
            assertEquals("PENDING_CURRICULUM_REVIEWER", unit.mappingStatus)
        }
        val unitSeven = pack.unit(7)!!
        assertEquals("DEVELOPMENT", unitSeven.installState)
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", unitSeven.readiness)
        assertEquals("PENDING_CURRICULUM_REVIEWER", unitSeven.mappingStatus)
        assertTrue(unitSeven.topicSummary.contains("bounded manner-adverb lesson"))
        assertFalse(unitSeven.mappingNote.contains("not prepared"))
        (11..15).forEach { number ->
            val unit = pack.unit(number)!!
            assertEquals("DEVELOPMENT", unit.installState)
            assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", unit.readiness)
            assertEquals("PENDING_CURRICULUM_REVIEWER", unit.mappingStatus)
        }
        assertTrue((16..20).all { pack.unit(it)!!.installState == "DEVELOPMENT" })
        assertTrue(listOf(16, 18, 19, 20).all { pack.unit(it)!!.readiness == "CONTENT_COMPLETE_REVIEW_PENDING" })
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(17)!!.readiness)
        assertTrue(pack.units.drop(25).all { it.installState == "DEVELOPMENT" })
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(28)!!.readiness)
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(26)!!.readiness)
        assertEquals(emptySet<Int>(), pack.units.filter { it.readiness == "NOT_PREPARED" }.map { it.number }.toSet())

        val unitOne = V2Validator.unitContentReviewDigest(pack, 1)
        val unitThree = V2Validator.unitContentReviewDigest(pack, 3)
        val unitFive = V2Validator.unitContentReviewDigest(pack, 5)
        val local = pack.copy(
            units = pack.units.map { unit ->
                if (unit.number != 5) unit else {
                    unit.copy(
                        sessions = unit.sessions.map { session ->
                            session.copy(
                                steps = session.steps.map { step ->
                                    if (step.id == "u5-st-book-work") step.copy(body = step.body + " extra book direction") else step
                                },
                            )
                        },
                    )
                }
            },
        )
        assertEquals(unitOne, V2Validator.unitContentReviewDigest(local, 1))
        assertEquals(unitThree, V2Validator.unitContentReviewDigest(local, 3))
        assertNotEquals(unitFive, V2Validator.unitContentReviewDigest(local, 5))
        assertNotEquals(V2Validator.contentReviewDigest(pack), V2Validator.contentReviewDigest(local))
        val workflow = pack.copy(
            units = pack.units.map { unit ->
                if (unit.number == 5) unit.copy(readiness = "CONTENT_COMPLETE_REVIEW_PENDING") else unit
            },
        )
        assertEquals(unitFive, V2Validator.unitContentReviewDigest(workflow, 5))
    }

    @Test
    fun notPreparedBookDirectionCannotBePromotedOrAdvanced() {
        val pack = pack()
        val unit3 = pack.unit(3)!!
        val promoted = listOf(
            "installed" to unit3.copy(installState = "INSTALLED", readiness = "INSTALLABLE", mappingStatus = "APPROVED"),
            "installable" to unit3.copy(readiness = "INSTALLABLE"),
            "review-approved" to unit3.copy(readiness = "REVIEW_APPROVED"),
            "mapping-approved" to unit3.copy(readiness = "MAPPING_APPROVED", mappingStatus = "APPROVED"),
        )
        promoted.forEach { (label, replacement) ->
            val candidate = pack.copy(
                units = pack.units.map { unit -> if (unit.number == 3) replacement else unit },
            )
            val result = V2Validator.validate(candidate)
            assertFalse(label + "\n" + result.errors.joinToString("\n"), result.ok)
        }
        val reviewed = approveUnitReview(
            pack.copy(
                units = pack.units.map { unit ->
                    if (unit.number == 3) unit.copy(readiness = "REVIEW_APPROVED") else unit
                },
            ),
            3,
        )
        val reviewedResult = V2Validator.validate(reviewed)
        assertTrue(reviewedResult.errors.joinToString("\n"), reviewedResult.ok)
        assertEquals("IMPLEMENTED_PENDING_FINAL_AUDIT", pack.unitReviews.first { it.unitNumber == 3 }.status)
        assertTrue(pack.unitReviews.first { it.unitNumber == 3 }.reviewer.isNullOrBlank())
        assertFalse(pack.publicReleaseEligible)
        assertEquals("DEVELOPMENT", reviewed.unit(3)!!.installState)
        val store = LocalStore.openInMemory(context()).also { stores += it }
        store.v2CompleteUnit(1, "2026-09-23T12:00")
        store.v2CompleteUnit(2, "2026-09-23T12:00")
        val attested = store.v2LearnerView().copy(
            currentUnitNumber = 3,
            completedStepIds = unit3.sessions.flatMap { it.steps }.map { it.id }.toSet(),
            bookAttestedUnits = setOf(3),
        )
        val decision = advancementDecision(pack, unit3, attested)
        assertFalse(decision.allowed)
        assertTrue(decision.remaining.any { it.contains("distinct in-app checks") })
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
            ).verifiedResume(pack),
        )
        val before = continueTarget(pack, store.v2LearnerView())
        assertEquals(first.id, before.sessionId)
        assertEquals(0, before.stepIndex)
        store.v2CompleteStep(first.steps.first().id, "2026-09-22T12:00")
        store.v2SaveProfile(
            store.v2Profile().copy(currentSessionId = first.id, currentStepIndex = 1).verifiedResume(pack),
        )
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
        assertEquals(V2UnitState.LOCKED, unitState(pack, pack.unit(2)!!, store.v2LearnerView()))
        val firstUnit = pack.unit(1)!!
        firstUnit.sessions.flatMap { it.steps }.forEach { step ->
            store.v2CompleteGovernedStep(step.id, stepContentDigest(pack, step), "2026-09-22T12:01")
            step.itemIds.forEach { id -> recordPrescribed(store, pack, pack.item(id)!!, "2026-09-22T12:01") }
        }
        store.v2AttestBook(1, "2026-09-22T12:01")
        assertEquals(V2UnitState.CURRENT, unitState(pack, pack.unit(2)!!, store.v2LearnerView()))
        assertTrue(pack.unit(1)!!.sessions.flatMap { it.steps }.none { it.unavailableReason != null })
        val unit2Steps = pack.unit(2)!!.sessions.flatMap { it.steps }
        assertTrue(unit2Steps.none { it.kind == "UNAVAILABLE" || !it.unavailableReason.isNullOrBlank() })
        assertTrue(unit2Steps.first { it.id == "u2-st-hw" }.bookDirection!!.isNotBlank())
        assertTrue(unit2Steps.first { it.id == "u2-st-pron" }.bookDirection!!.isNotBlank())
        assertEquals(0, pack.admissions.catalogAudioBoundToUnits)
        assertEquals(0, pack.admissions.mappedPronunciationAudio)
        assertEquals(0, pack.admissions.handwritingAssets)
    }

    @Test
    fun advancementNeedsMoreThanOneCheckAndBookAttestation() {
        val pack = pack()
        val store = LocalStore.openInMemory(context()).also { stores += it }
        val unit = pack.unit(1)!!
        unit.sessions.flatMap { it.steps }.filter { it.kind == "ORIENT" || it.kind == "TEACH" }.forEach {
            store.v2CompleteGovernedStep(it.id, stepContentDigest(pack, it), "2026-09-22T12:00")
        }
        val early = advancementDecision(pack, unit, store.v2LearnerView())
        assertFalse(early.allowed)
        store.v2AttestBook(1, "2026-09-22T12:01")
        recordPrescribed(store, pack, pack.item("u1-i-а-identity")!!, "2026-09-22T12:02")
        val still = advancementDecision(pack, unit, store.v2LearnerView())
        assertFalse(still.allowed)
        recordPrescribed(store, pack, pack.item("u1-i-р-not-p")!!, "2026-09-22T12:03")
        val skipped = advancementDecision(pack, unit, store.v2LearnerView())
        assertFalse(skipped.remaining.joinToString(), skipped.allowed)
        assertTrue(skipped.remaining.any { it.startsWith("Demonstrate every check for ") })
        unit.objectives.filter { it.kind == "RECOGNITION" }.forEach { objective ->
            pack.items.filter { it.objectiveId == objective.id }.forEach { item ->
                recordPrescribed(store, pack, item, "2026-09-22T12:04")
            }
        }
        val ready = advancementDecision(pack, unit, store.v2LearnerView())
        assertTrue(ready.remaining.joinToString(), ready.allowed)
    }

    @Test
    fun advancementCannotSkipARequiredObjective() {
        val pack = pack()
        val store = LocalStore.openInMemory(context()).also { stores += it }
        assertObjectiveSkipBlocks(pack, store, 3, "obj-u3-naming")
        assertObjectiveSkipBlocks(pack, store, 5, "obj-u5-prepositional")
    }

    @Test
    fun todayShowsOneBoundedSessionInsteadOfTheUnitInventory() {
        val pack = pack()
        val store = LocalStore.openInMemory(context()).also { stores += it }
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, dailyMinutes = 35))
        val today = todaysSession(pack, pack.unit(1)!!, store.v2LearnerView())
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
    fun resolvedAssetRecordsAreRequiredInsteadOfCounters() {
        val source = pack()
        val letterId = source.examples.first { it.objectiveId == "obj-u1-letter-identity" }.letterId
        val counted = approveInstruction(
            installedUnitOne(source).copy(
                admissions = source.admissions.copy(mappedPronunciationAudio = 1, handwritingAssets = 1),
            ),
        )
        val countedResult = V2Validator.validate(counted)
        assertFalse(countedResult.ok)
        assertTrue(countedResult.errors.joinToString("\n"), countedResult.errors.any { it.contains("does not match governed audio records") })
        assertTrue(countedResult.errors.any { it.contains("does not match governed handwriting records") })
        assertTrue(countedResult.errors.any { it.contains("mapped pronunciation audio record") })
        assertTrue(countedResult.errors.any { it.contains("mapping record does not resolve") })

        val withRecords = source.copy(
            letters = source.letters.map { letter ->
                if (letter.id == letterId) letter.copy(audioAssetId = "audio-fixture-unit-1") else letter
            },
            audioAssets = listOf(
                V2AudioAsset(
                    id = "audio-fixture-unit-1",
                    sourceId = "src-unicode-cyrillic",
                    rightsId = "rights-unicode-cyrillic",
                    unitNumber = 1,
                    letterId = letterId,
                    intakeReviewStatus = "APPROVED",
                ),
            ),
            admissions = source.admissions.copy(mappedPronunciationAudio = 1),
            ownedEditionMappings = listOf(structuralMapping()),
        )
        val signed = V2Validator.unitContentReviewDigest(withRecords, 1)
        val activated = approveInstruction(withRecords).copy(
            units = withRecords.units.map { unit ->
                when (unit.number) {
                    1 -> unit.copy(installState = "INSTALLED", mappingStatus = "APPROVED", readiness = "INSTALLABLE")
                    else -> unit
                }
            },
            ownedEditionMappings = listOf(
                structuralMapping(
                    status = "APPROVED",
                    reviewer = "Pat Reviewer",
                    reviewerQualification = "Advanced Russian and curriculum review attested.",
                    reviewedContentDigest = signed,
                ),
            ),
            unitReviews = listOf(approvedUnitReview(withRecords, 1, signed)),
        )
        assertEquals(signed, V2Validator.unitContentReviewDigest(activated, 1))
        assertEquals(V2Validator.contentReviewDigest(withRecords), V2Validator.contentReviewDigest(activated))
        val accepted = V2Validator.validate(activated)
        assertTrue(accepted.errors.joinToString("\n"), accepted.ok)

        val otherUnitMapping = activated.ownedEditionMappings.first().copy(unitNumber = 2)
        val moved = activated.copy(ownedEditionMappings = listOf(otherUnitMapping))
        val reboundDigest = V2Validator.unitContentReviewDigest(moved, 2)
        val rebound = moved.copy(
            ownedEditionMappings = listOf(otherUnitMapping.copy(reviewedContentDigest = reboundDigest)),
            unitReviews = listOf(approvedUnitReview(moved, 1, V2Validator.unitContentReviewDigest(moved, 1))),
        )
        val isolated = V2Validator.validate(rebound)
        assertFalse(isolated.ok)
        assertTrue(isolated.errors.joinToString("\n"), isolated.errors.any { it.contains("unit 1") && it.contains("mapping record does not resolve") })

        val unitTwoInstalled = activated.copy(
            units = activated.units.map { unit ->
                if (unit.number == 2) {
                    unit.copy(installState = "INSTALLED", mappingStatus = "APPROVED", readiness = "INSTALLABLE")
                } else {
                    unit
                }
            },
        )
        val unitTwoResult = V2Validator.validate(unitTwoInstalled)
        assertFalse(unitTwoResult.ok)
        assertTrue(unitTwoResult.errors.any { it.contains("unit 2") && it.contains("review chain is not OWNER_ACCEPTED") })
        assertTrue(unitTwoResult.errors.any { it.contains("unit 2") && it.contains("mapping record does not resolve") })
        assertTrue(unitTwoResult.errors.any { it.contains("handwriting asset record") })
        assertTrue(unitTwoResult.errors.none { it.contains("Installed unit 1 review chain is not OWNER_ACCEPTED") })
    }

    @Test
    fun structuralMappingSafeguardsRejectIncompleteOrInventedRecords() {
        val pack = pack()
        assertEquals(30, pack.ownedEditionMappings.size)
        assertTrue(pack.ownedEditionMappings.all { it.status == "PENDING_CURRICULUM_REVIEWER" && it.reviewer.isNullOrBlank() && it.reviewedContentDigest.isNullOrBlank() })
        assertEquals("161a578ba556d709b621b61f138ad36290da57869a87ed57979e0bc7058b0aa7", V2Validator.unitContentReviewDigest(pack, 1))
        assertEquals("4fd67bff0431a11416115de023e076c52b7879304fc4a98b508d871170703e9c", V2Validator.unitContentReviewDigest(pack, 2))
        assertEquals("29121dfbfb6a2e07d413e86b01462a76a62b9659f52597300ccd91c569224d7c", V2Validator.unitContentReviewDigest(pack, 3))
        assertEquals("9f255afab766ef14cbf8ef2688a512f74c00d3c45efc772d6b4506a431a21121", V2Validator.unitContentReviewDigest(pack, 4))
        assertEquals("2d0bd5ce23de6a68c10be5a349554b768f551faaf0a90c3432bd5d6e00d1cd73", V2Validator.unitContentReviewDigest(pack, 5))
        assertEquals("ecae24c4e29c7247517a26716213878e43c8cb4de414f5cbc93c6b0f5ca85a0a", V2Validator.contentReviewDigest(pack))
        val seed = File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText()
        assertFalse(seed.contains("The New Penguin Russian Course"))
        assertEquals(0, pack.admissions.penguinProseAssets)

        val baseline = structuralMapping()
        assertEquals(
            V2Validator.unitContentReviewDigest(pack.copy(ownedEditionMappings = listOf(baseline)), 1),
            V2Validator.unitContentReviewDigest(pack.copy(ownedEditionMappings = listOf(baseline.copy(status = "APPROVED"))), 1),
        )
        assertNotEquals(
            V2Validator.unitContentReviewDigest(pack.copy(ownedEditionMappings = listOf(baseline)), 1),
            V2Validator.unitContentReviewDigest(pack.copy(ownedEditionMappings = listOf(baseline.copy(printedPageEnd = 99))), 1),
        )

        val missingIdentity = V2Validator.validate(pack.copy(ownedEditionMappings = listOf(structuralMapping(editionTitle = null))))
        assertTrue(missingIdentity.errors.joinToString("\n"), missingIdentity.errors.any { it.contains("missing edition identity") })

        val inventedIsbn = V2Validator.validate(
            pack.copy(ownedEditionMappings = listOf(structuralMapping(isbn = "9780140120417", isbnObservation = "UNRESOLVED"))),
        )
        assertTrue(inventedIsbn.errors.joinToString("\n"), inventedIsbn.errors.any { it.contains("invented ISBN") })

        val blankPages = V2Validator.validate(
            pack.copy(
                ownedEditionMappings = listOf(
                    structuralMapping(
                        status = "APPROVED",
                        reviewer = "Pat Reviewer",
                        reviewerQualification = "Advanced Russian and curriculum review attested.",
                        reviewedContentDigest = V2Validator.unitContentReviewDigest(pack, 1),
                        printedPageStart = null,
                        printedPageEnd = null,
                    ),
                ),
            ),
        )
        assertTrue(blankPages.errors.joinToString("\n"), blankPages.errors.any { it.contains("blank page range") })

        val unsigned = V2Validator.validate(
            pack.copy(
                ownedEditionMappings = listOf(
                    structuralMapping(
                        status = "APPROVED",
                        reviewedContentDigest = V2Validator.unitContentReviewDigest(pack, 1),
                    ),
                ),
            ),
        )
        assertTrue(unsigned.errors.joinToString("\n"), unsigned.errors.any { it.contains("without an identified qualified reviewer") })

        val digestMismatch = V2Validator.validate(
            pack.copy(
                ownedEditionMappings = listOf(
                    structuralMapping(
                        status = "APPROVED",
                        reviewer = "Pat Reviewer",
                        reviewerQualification = "Advanced Russian and curriculum review attested.",
                        reviewedContentDigest = "0".repeat(64),
                    ),
                ),
            ),
        )
        assertTrue(digestMismatch.errors.joinToString("\n"), digestMismatch.errors.any { it.contains("does not match the unit content-review digest") })

        val wrongLesson = V2Validator.validate(pack.copy(ownedEditionMappings = listOf(structuralMapping(proposedLessonNumber = 4))))
        assertTrue(wrongLesson.errors.joinToString("\n"), wrongLesson.errors.any { it.contains("wrong unit or lesson") })

        val redistributed = V2Validator.validate(
            pack.copy(
                sources = pack.sources.map { source ->
                    if (source.id == pack.sources.first().id) source.copy(title = "The New Penguin Russian Course") else source
                },
            ),
        )
        assertTrue(redistributed.errors.joinToString("\n"), redistributed.errors.any { it.contains("not redistributable curriculum content") })
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

    private fun recordPrescribed(store: LocalStore, pack: dev.russiancompanion.v2.curriculum.V2CurriculumPack, item: V2CheckItem, at: String) {
        val stepId = pack.units.flatMap { it.sessions }.flatMap { it.steps }.first { item.id in it.itemIds }.id
        store.v2RecordGovernedAttempt(
            checkNotNull(prepareGovernedAttempt(pack, item.id, item.answer, PRESCRIBED_CHECK, stepId, at)),
        )
    }

    private fun assertObjectiveSkipBlocks(pack: dev.russiancompanion.v2.curriculum.V2CurriculumPack, store: LocalStore, unitNumber: Int, skippedObjectiveId: String) {
        val unit = pack.unit(unitNumber)!!
        val skipped = unit.objectives.first { it.id == skippedObjectiveId }
        val skippedItems = pack.items.filter { it.objectiveId == skipped.id }.map { it.id }.toSet()
        unit.sessions.flatMap { it.steps }.filter { it.kind == "ORIENT" || it.kind == "TEACH" }.forEach {
            store.v2CompleteGovernedStep(it.id, stepContentDigest(pack, it), "2026-09-23T12:00")
        }
        store.v2AttestBook(unitNumber, "2026-09-23T12:01")
        pack.items.filter { it.objectiveId in unit.objectives.map { objective -> objective.id } && it.id !in skippedItems }.forEach { item ->
            recordPrescribed(store, pack, item, "2026-09-23T12:02")
        }
        val learner = store.v2LearnerView()
        val correct = distinctCorrectCheckCount(
            pack,
            unit.sessions.flatMap { it.steps }.flatMap { it.itemIds }.distinct(),
            learner.correctItemIds,
        )
        assertTrue("Unit $unitNumber recorded $correct checks", correct >= unit.advancement!!.minimumDistinctChecks)
        val blocked = advancementDecision(pack, unit, learner)
        assertFalse(blocked.remaining.joinToString(), blocked.allowed)
        assertTrue(blocked.remaining.joinToString(), blocked.remaining.any { it.contains(skipped.title) })
        pack.items.filter { it.id in skippedItems }.forEach { item ->
            recordPrescribed(store, pack, item, "2026-09-23T12:03")
        }
        val ready = advancementDecision(pack, unit, store.v2LearnerView())
        assertTrue(ready.remaining.joinToString(), ready.allowed)
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

    private fun structuralMapping(
        unitNumber: Int = 1,
        status: String = "PENDING_CURRICULUM_REVIEWER",
        reviewer: String? = null,
        reviewerQualification: String? = null,
        reviewedContentDigest: String? = null,
        editionTitle: String? = "Fixture edition",
        proposedLessonNumber: Int? = unitNumber,
        printedPageStart: Int? = 1,
        printedPageEnd: Int? = 11,
        isbn: String? = "FIXTURE-ISBN",
        isbnObservation: String = "OBSERVED",
    ): V2OwnedEditionMapping {
        return V2OwnedEditionMapping(
            id = "map-owned-edition",
            unitNumber = unitNumber,
            status = status,
            reviewer = reviewer,
            reviewerQualification = reviewerQualification,
            reviewedContentDigest = reviewedContentDigest,
            editionTitle = editionTitle,
            author = "Fixture author",
            publisherImprint = "Fixture imprint",
            proposedLessonNumber = proposedLessonNumber,
            printedPageStart = printedPageStart,
            printedPageEnd = printedPageEnd,
            topicDescription = "Fixture topic",
            observationSource = "Fixture observation",
            isbn = isbn,
            isbnObservation = isbnObservation,
        )
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
        return approveUnitReview(next, 1)
    }

    private fun approvedUnitReview(
        source: dev.russiancompanion.v2.curriculum.V2CurriculumPack,
        unitNumber: Int,
        digest: String = V2Validator.unitContentReviewDigest(source, unitNumber),
    ): V2UnitReviewRecord {
        return V2UnitReviewRecord(
            id = "rev-pending-unit-$unitNumber",
            unitNumber = unitNumber,
            status = "APPROVED",
            reviewer = "Qualified Reviewer",
            reviewerQualification = "Advanced Russian and curriculum review attested.",
            packVersion = source.packVersion,
            reviewedContentDigest = digest,
            curriculumMappingId = "map-owned-edition",
        )
    }

    private fun approveUnitReview(
        source: dev.russiancompanion.v2.curriculum.V2CurriculumPack,
        unitNumber: Int,
    ): dev.russiancompanion.v2.curriculum.V2CurriculumPack {
        val others = source.unitReviews.filter { it.unitNumber != unitNumber }
        return source.copy(unitReviews = others + approvedUnitReview(source, unitNumber))
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
    ): dev.russiancompanion.v2.curriculum.V2CurriculumPack = approveUnitReview(source, 1)

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

    private fun bookDirectionOnly(unit: dev.russiancompanion.v2.curriculum.V2Unit) = unit.copy(
        readiness = "CONTENT_COMPLETE_REVIEW_PENDING",
        sessions = unit.sessions.map { session ->
            session.copy(steps = session.steps.filter { it.kind == "BOOK" })
        }.filter { it.steps.isNotEmpty() },
        objectives = unit.objectives.filter { it.kind == "SELF_ATTESTATION" },
    )

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun context(): Context = ApplicationProvider.getApplicationContext()

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}

private fun dev.russiancompanion.v2.curriculum.V2CheckItem.isGovernance(): Boolean {
    return dev.russiancompanion.v2.curriculum.GOVERNANCE_PROMPT_MARKERS.any { marker ->
        "$id $kind $prompt $feedback".lowercase().contains(marker)
    }
}
