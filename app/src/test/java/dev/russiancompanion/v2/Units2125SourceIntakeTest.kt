package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.BatchBSources
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2ProvenanceRecord
import dev.russiancompanion.v2.curriculum.V2SourceCapability
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.advancementDecision
import dev.russiancompanion.v2.curriculum.unit
import dev.russiancompanion.v2.review.buildUnitReviewPackage
import dev.russiancompanion.v2.review.governedMappingFacts
import dev.russiancompanion.v2.review.mappingPackageDigest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.security.MessageDigest

class Units2125SourceIntakeTest {
    private val pages = mapOf(
        21 to "257-268",
        22 to "269-282",
        23 to "283-293",
        24 to "294-304",
        25 to "305-319",
    )

    private val packagedMappingNotePages = mapOf(
        21 to "259-270",
        22 to "271-284",
        23 to "285-295",
        24 to "296-306",
        25 to "307-321",
    )

    private val preservedContentDigests = mapOf(
        1 to "161a578ba556d709b621b61f138ad36290da57869a87ed57979e0bc7058b0aa7",
        2 to "4fd67bff0431a11416115de023e076c52b7879304fc4a98b508d871170703e9c",
        3 to "28aa1c4770ecd53891d2313a0026f8876594ad623813c4a74a9a381d36e69300",
        4 to "1ebcde1ebabf867b08e137a96bf30f56cccbb0ea85cfd8ecb54cee463a79bf11",
        5 to "3c9149de87feeda96f64e195f3dab0428700049f3a7342e4ba764191a16db141",
        6 to "ba6c6924af88effded224c79e37b467780c4c5372dd1b5dd6ba8a37e3f0233fe",
        7 to "0bda8d40aae3c040f5c6f9a8a2771de45f95dbdad1bf8232854a2fc3c999e9e9",
        8 to "96b3a661d0ad5edd55ed44e2890a88250081275a7881713c9daf37ed94d7f42a",
        9 to "418b5840eacbb7dd06791fec7e490befe34ae03defab0706ee10b42565c8dea3",
        10 to "3ae5a3b41bc970ef43589ace5929e65818fa620c8660f0bd1c4de5275667cf84",
        11 to "35de2337c00321867e4c30871b048181738017a79babc4f0135ad4131e1b6c69",
        12 to "1c5d43e55bbd627515f1fab69ddea0406fecf2257ba696acbfd88b413c404f97",
        13 to "7102c47201a3d4973d781d315dbbb8ceccca3424174b9c90ad127bac037828df",
        14 to "9e136e9af2fe3611d6862fa46e3217f85ad45c8b23ad622eec6f36e48ab97f38",
        15 to "ea945a9c9cd01a30db3522794b9df36c5a4fc291092a153da0672bb4b8a9a6bd",
        16 to "31893860b30df7c38248356c9014db1a361110f7a8a51ce53ae7ed6fd61bae87",
        // Unit17 question-transfer correction supersedes historical 6172c2a9be50c4b3c79b962dc5f9b055f8d2d267d3a6a8627a3e9805e4fc8cca.
            17 to "415d0ecc0e4e8ce649dff17aef09851beddc39f191f6bedf783af07904481c62",
        18 to "603827ede7e8394cef33bf1af9fb4ef6217a80f0e6624f1bd385e4a0766b69ac",
        19 to "3096baba4cdc192ac7d701f6eedd3c8ca0be12c54ab64efa52b0c25fdbf83a5e",
        // Prior Unit20 directionality implementation supersedes historical bb408265fb5239daeb86d43c007061bff330e02a0a9afc1aa677526ccf57302b.
        20 to "6c11d3dd467846f5aabf034024f7d27d3ed9a5cc9eec4a8462cd8be3c54069eb",
    )

    private val preservedMappingDigests = mapOf(
        16 to "f74c6fa2206f8faad47ad15e91b8dce61f886293f64d9a3f2e47947b2d5db8e7",
        17 to "724a3220ec28b620687871fbca1639cc8e64b58fed7fcfe8ba1a29bdf39d6328",
        18 to "f195680e986e1c1069418022707886a4b11b4fe9cf8410e9e8b78e71525008bb",
        19 to "56534b156e7412d1bd8e51bf493e8d80f4b58461446382483a8ed118681dd6b2",
        20 to "f24f256481096bcc5f47bcb849f053bfda59fc67e670492c6b4e7a42b514a8c9",
    )

    private val excludedLearnerText = listOf(
        "don't deserve the title Comrade",
        "wealthy foreigners",
        "Ordinary citizens of foreign countries",
        "only a dozen",
        "Creativity in baby-naming",
        ".ogg",
        "[[Media:",
        "good fighter",
        "Lord of the World",
    )

    private val listeningInstruction = Regex("(?i)\\blisten\\b")

    private val keywordBlob = (
        "\u0441\u0435\u0431\u044f \u0447\u0442\u043e\u0431\u044b declension of numerals \u043e\u0431\u0430 \u043e\u0431\u0435 " +
            "\u043f\u043e\u043b\u0442\u043e\u0440\u0430 collective numeral \u0441\u043e\u0431\u0438\u0440\u0430\u0442\u0435\u043b\u044c\u043d " +
            " ago during elapsed accusative genitive negation negative pronoun \u043d\u0438\u043a\u0442\u043e scope " +
            "diminutive suffix register"
        )

    private val gapObjectives = emptyMap<Int, List<String>>()

    @Test
    fun structuralMappingsAreDigestBoundAndStayUnsigned() {
        val pack = pack()
        assertTrue(V2Validator.validate(pack).errors.joinToString("\n"), V2Validator.validate(pack).ok)
        pages.forEach { (number, range) ->
            val unit = pack.unit(number)!!
            val facts = governedMappingFacts(number)
            val digest = mappingPackageDigest(facts)
            assertEquals("DEVELOPMENT", unit.installState)
            assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", unit.readiness)
            assertEquals("PENDING_CURRICULUM_REVIEWER", unit.mappingStatus)
            assertEquals(number, unit.penguinLesson)
            assertTrue(unit.mappingNote.contains(range))
            assertFalse(unit.mappingNote.contains(packagedMappingNotePages.getValue(number)))
            assertTrue(unit.mappingNote.contains("PENDING_CURRICULUM_REVIEWER"))
            assertFalse(unit.mappingNote.contains("APPROVED"))
            assertFalse(unit.mappingNote.contains("ISBN"))
            assertFalse(unit.mappingNote.contains("2bcc0c45"))
            assertEquals(range, "${facts.printedPageStart}-${facts.printedPageEnd}")
            assertEquals(number, facts.proposedLessonNumber)
            assertEquals("PENDING_CURRICULUM_REVIEWER", facts.mappingDisposition)
            assertEquals("UNRESOLVED", facts.isbnObservation)
            assertNull(facts.isbn)
            val notes = facts.observedEditionNotes.orEmpty()
            val comparison = notes.substringAfter("Reference-file comparison")
            assertTrue(comparison.contains("2bcc0c45fac4508adf283f4dd939a7e578cf2c1306fdadf4966300532beb6303"))
            assertTrue(comparison.contains("does not match"))
            assertTrue(comparison.contains("Binary-identity continuity is not established"))
            assertTrue(comparison.contains("not evidence of altered instructional content"))
            assertFalse(notes.substringBefore("Reference-file comparison").contains("2bcc0c45"))
            assertTrue(pack.ownedEditionMappings.filter { it.unitNumber == number }.all { it.status == "PENDING_CURRICULUM_REVIEWER" && it.reviewer.isNullOrBlank() && it.reviewedContentDigest.isNullOrBlank() })
            assertEquals(1, pack.ownedEditionMappings.count { it.unitNumber == number })
            val review = pack.unitReviews.first { it.unitNumber == number }
            assertEquals("IMPLEMENTED_PENDING_FINAL_AUDIT", review.status)
            assertTrue(review.reviewer.isNullOrBlank())
            assertEquals(digest, buildUnitReviewPackage(pack, number).mappingPackageDigest)
            assertNotEquals(digest, mappingPackageDigest(facts.copy(printedPageStart = facts.printedPageStart!! + 1)))
            assertNotEquals(digest, mappingPackageDigest(facts.copy(printedPageEnd = facts.printedPageEnd!! + 1)))
            assertNotEquals(digest, mappingPackageDigest(facts.copy(topicDescription = facts.topicDescription + " extra")))
            assertNotEquals(digest, mappingPackageDigest(facts.copy(proposedLessonNumber = facts.proposedLessonNumber!! + 1)))
            assertNotEquals(digest, mappingPackageDigest(facts.copy(mappingDisposition = "APPROVED")))
            assertNotEquals(digest, mappingPackageDigest(facts.copy(isbn = "0000000000")))
            val signedReview = buildUnitReviewPackage(pack, number)
            assertNotEquals(signedReview.contentReviewDigest, signedReview.packContentReviewDigest)
            assertNotEquals(signedReview.contentReviewDigest, signedReview.mappingPackageDigest)
            assertNotEquals(signedReview.reviewPackageDigest, signedReview.repositorySourceDigest)
            assertNotEquals(signedReview.mappingPackageDigest, signedReview.repositorySourceDigest)
        }
        assertEquals(
            "Possession with the reflexive possessive pronoun; reflexive reference; and purpose clauses",
            governedMappingFacts(21).topicDescription,
        )
        assertEquals(
            "Numeral behavior beyond basic counting, including declined numerals, forms for both and one and a half, compound quantities, and collective or quantity expressions",
            governedMappingFacts(22).topicDescription,
        )
        assertEquals(
            "Expressions of elapsed time, duration, ago, during, and distinctions involving completion over a period",
            governedMappingFacts(23).topicDescription,
        )
        assertTrue(governedMappingFacts(24).topicDescription.orEmpty().contains("\u043d\u0435"))
        assertEquals(
            "Diminutives, Russian personal-name forms, social address, requests, apologies, and polite expressions",
            governedMappingFacts(25).topicDescription,
        )
        RecordedCurriculumConservationScope.assertHistoricalPinsAndCurrentUnits(repo(), pack, preservedContentDigests)
        preservedMappingDigests.forEach { (number, digest) ->
            assertEquals("mapping $number", digest, mappingPackageDigest(governedMappingFacts(number)))
        }
        assertTrue(pack.units.drop(25).all { it.installState == "DEVELOPMENT" && it.sessions.isNotEmpty() })
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(28)!!.readiness)
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(26)!!.readiness)
        assertEquals(emptySet<Int>(), pack.units.filter { it.readiness == "NOT_PREPARED" }.map { it.number }.toSet())
        assertTrue(File(repo(), "review/unit-23/UNIT_23_REVIEW_PACKET.md").isFile)
    }


    @Test
    fun unsupportedObjectivesCannotAcquireAdvancementChecksOrContentCompleteStatus() {
        val pack = pack()
        gapObjectives.forEach { (number, ids) ->
            val unit = pack.unit(number)!!
            ids.forEach { id ->
                val objective = unit.objectives.first { it.id == id }
                assertEquals("GUIDANCE", objective.kind)
                assertTrue(objective.checkItemIds.isEmpty())
            }
            val withCheck = attachCheck(pack, number, ids.first())
            val attached = V2Validator.validate(withCheck).errors
            assertTrue(attached.joinToString("\n"), attached.any { it.contains("must not carry advancement checks") })
            val claimed = V2Validator.validate(claimComplete(pack, number)).errors
            assertTrue(claimed.joinToString("\n"), claimed.any { it.contains("not content-complete") })
            assertEquals("NOT_PREPARED", unit.readiness)
        }
        assertTrue(reason(pack, 21).isBlank())
        assertTrue(reason(pack, 22).isBlank())
        assertTrue(reason(pack, 23).isBlank())
        assertTrue(reason(pack, 24).isBlank())
        val placement = pack.unit(24)!!.objectives.first { it.id == "obj-u24-placement" }
        assertEquals("RECOGNITION", placement.kind)
        assertTrue(placement.checkItemIds.size >= 2)
        val contrast = pack.unit(24)!!.objectives.first { it.id == "obj-u24-word-order-contrast" }
        assertEquals("RECOGNITION", contrast.kind)
        assertTrue(contrast.checkItemIds.size >= 2)
        assertTrue(reason(pack, 25).isBlank())
        assertTrue(pack.unit(25)!!.objectives.none { it.id == "obj-u25-productive-formation" })
        listOf(21, 22, 23, 24, 25).forEach { number ->
            val unit = pack.unit(number)!!
            val decision = dev.russiancompanion.v2.curriculum.ordinaryUnitRequirements(
                pack,
                unit,
                learner(
                    number,
                    book = true,
                    correct = pack.items.filter { item -> unit.objectives.any { it.id == item.objectiveId } }.map { it.id }.toSet(),
                    steps = unit.sessions.flatMap { it.steps }.map { it.id }.toSet(),
                ),
            )
            assertTrue(decision.remaining.joinToString("\n"), decision.allowed)
            val decisionFull = advancementDecision(
                pack,
                unit,
                learner(
                    number,
                    book = true,
                    correct = pack.items.filter { item -> unit.objectives.any { it.id == item.objectiveId } }.map { it.id }.toSet(),
                    steps = unit.sessions.flatMap { it.steps }.map { it.id }.toSet(),
                ),
            )
            assertFalse(decisionFull.allowed)
            assertEquals(listOf("Complete the assigned earlier-objective review"), decisionFull.remaining)
        }
    }

    @Test
    fun partialStrandsStayShortOfTheMissingSources() {
        val pack = pack()
        assertGap(withoutCapabilities(pack, 21), 21, "without admitted reflexive-object and purpose source capabilities", "4220315", keywordBlob)
        assertGap(withoutCapabilities(pack, 22), 22, "without admitted declined-numeral, both, one-and-a-half, and collective source capabilities", "4371358", keywordBlob)
        assertGap(withoutCapabilities(pack, 24), 24, "without admitted alternation, negative-pronoun, and scope source capabilities", "4233876", keywordBlob)
        assertGap(withoutCapabilities(pack, 24), 24, "without admitted alternation, negative-pronoun, and scope source capabilities", "4195114", keywordBlob)
        assertGap(withoutCapabilities(pack, 25), 25, "without admitted documented-name-form and register source capabilities", "4660786", keywordBlob)
        assertGap(withoutCapabilities(pack, 25), 25, "without admitted documented-name-form and register source capabilities", "3353859", keywordBlob)
        val dictionary = sample(pack, 21, "50e210c4803237779cb562bc1abcea529066031c", "src-openrussian-50e210c4", keywordBlob)
        val fromRows = V2Validator.validate(claimComplete(withoutCapabilities(pack, 21).copy(provenanceRecords = pack.provenanceRecords + dictionary), 21)).errors
        assertTrue(fromRows.joinToString("\n"), fromRows.any { it.contains("without admitted reflexive-object and purpose source capabilities") })
    }

    @Test
    fun unitTwentyThreeElapsedTimeStaysReviewPendingAndPartialEvidenceStaysClosed() {
        val pack = pack()
        val unit = pack.unit(23)!!
        assertEquals("DEVELOPMENT", unit.installState)
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", unit.readiness)
        assertEquals("PENDING_CURRICULUM_REVIEWER", unit.mappingStatus)
        assertTrue(unit.mappingNote.contains("completion over a period"))
        assertTrue(unit.topicSummary.contains("duration-versus-completion contrast"))
        assertFalse(unit.topicSummary.contains("Completion over a period has no admitted frozen span"))
        assertTrue(unit.topicSummary.contains("Book self-attestation does not replace the app checks"))
        assertTrue(unit.topicSummary.contains("not installed"))
        assertTrue(unit.sessions.flatMap { it.steps }.any { it.kind == "TEACH" })
        assertTrue(unit.sessions.flatMap { it.steps }.any { it.kind == "BOOK" })
        assertTrue(unit.objectives.single { it.id == "obj-u23-book" }.checkItemIds.isEmpty())
        assertEquals("SELF_ATTESTATION", unit.objectives.single { it.id == "obj-u23-book" }.kind)
        listOf("obj-u23-ranges", "obj-u23-beginning", "obj-u23-during-event", "obj-u23-duration", "obj-u23-elapsed").forEach { id ->
            val objective = unit.objectives.single { it.id == id }
            assertEquals("RECOGNITION", objective.kind)
            assertTrue(objective.checkItemIds.size >= 2)
        }
        assertTrue(reason(pack, 23).isBlank())
        val completion = unit.objectives.single { it.id == "obj-u23-completion" }
        assertEquals("RECOGNITION", completion.kind)
        assertTrue(completion.checkItemIds.size >= 2)
        assertFalse(V2Validator.unresolvedRequiredSourceCapabilities(pack, 23).any { it.claim == "completion-over-period" })
        val advanced = dev.russiancompanion.v2.curriculum.ordinaryUnitRequirements(
            pack,
            unit,
            learner(
                23,
                book = true,
                correct = unit.objectives.flatMap { it.checkItemIds }.toSet() +
                    unit.sessions.flatMap { it.steps }.flatMap { it.itemIds }.toSet(),
                steps = unit.sessions.flatMap { it.steps }.map { it.id }.toSet(),
            ),
        )
        assertTrue(advanced.remaining.joinToString("\n"), advanced.allowed)
            val advancedFull = advancementDecision(
            pack,
            unit,
            learner(
                23,
                book = true,
                correct = unit.objectives.flatMap { it.checkItemIds }.toSet() +
                    unit.sessions.flatMap { it.steps }.flatMap { it.itemIds }.toSet(),
                steps = unit.sessions.flatMap { it.steps }.map { it.id }.toSet(),
            ),
        )
            assertFalse(advancedFull.allowed)
            assertEquals(listOf("Complete the assigned earlier-objective review"), advancedFull.remaining)
        val elapsed = pack.sourceCapabilities.single { it.supportedClaim == "elapsed-time-duration" }
        assertEquals("obj-u23-duration", elapsed.objectiveId)
        assertEquals(23, elapsed.unitNumber)
        assertTrue(elapsed.exactSpan.contains("emphasizes the duration"))
        assertEquals("src-pressbooks-diverse-russian-usa-grammar", elapsed.sourceId)
        assertFalse(elapsed.exactSpan in BatchBSources.BOUNDED_TIME_SPANS)
        assertTrue(pack.provenanceRecords.filter { it.unitNumber == 23 }.none {
            it.originalSourceText in BatchBSources.BOUNDED_TIME_SPANS
        })
        val review = pack.unitReviews.single { it.unitNumber == 23 }
        assertEquals("IMPLEMENTED_PENDING_FINAL_AUDIT", review.status)
        assertTrue(review.reviewer.isNullOrBlank())
        val stripped = withoutCapabilities(pack, 23)
        val numbersHour = sample(pack, 23, "4371358", "src-wikibooks-russian-cc-by-sa", "one hour one year one minute elapsed during ago")
        val stretched = V2Validator.validate(claimComplete(stripped.copy(provenanceRecords = stripped.provenanceRecords + numbersHour), 23)).errors
        assertTrue(stretched.joinToString("\n"), stretched.any { it.contains("without admitted elapsed-time-duration and completion-over-period source capabilities") })
        val dictionary = sample(pack, 23, "50e210c4803237779cb562bc1abcea529066031c", "src-openrussian-50e210c4", keywordBlob)
            .copy(objectiveId = "obj-u23-duration")
        val dictionaryCapability = capability(dictionary, "cap-dictionary-duration", 23, "obj-u23-duration", "elapsed-time-duration")
        val fromRows = V2Validator.validate(
            claimComplete(
                stripped.copy(
                    provenanceRecords = stripped.provenanceRecords + dictionary,
                    sourceCapabilities = stripped.sourceCapabilities + dictionaryCapability,
                ),
                23,
            ),
        ).errors.joinToString("\n")
        assertTrue(fromRows, fromRows.contains("without admitted elapsed-time-duration and completion-over-period source capabilities"))
        assertTrue(fromRows, fromRows.contains("no independent elapsed-time-duration admission"))
        val bare = V2Validator.validate(claimComplete(stripped, 23)).errors
        assertTrue(bare.joinToString("\n"), bare.any { it.contains("without admitted elapsed-time-duration and completion-over-period source capabilities") })
        assertTrue(bare.none { it.contains("no teach step") })
        val ago = pack.provenanceRecords.first().copy(
            itemId = "prov-u23-ago-span",
            unitNumber = 23,
            objectiveId = "obj-u23-duration",
            sourceId = "src-wikipedia-russian-grammar-1369617627",
            sourceRevision = "1369617627",
            sourceRecordId = "grammar:ago-probe",
            originalSourceText = BatchBSources.AGO_SPAN,
            shippedText = "ago",
            sourceHash = sha256(BatchBSources.AGO_SPAN),
            contentHash = sha256("ago"),
        )
        val overreach = V2Validator.validate(
            pack.copy(
                provenanceRecords = pack.provenanceRecords + ago,
                sourceCapabilities = pack.sourceCapabilities.filter { it.supportedClaim != "elapsed-time-duration" } + capability(
                    ago,
                    "cap-u23-bounded",
                    23,
                    "obj-u23-duration",
                    "elapsed-time-duration",
                ),
            ),
        ).errors.joinToString("\n")
        assertTrue(overreach, overreach.contains("no independent elapsed-time-duration admission"))
        assertTrue(overreach, overreach.contains("bounded time expression does not clear elapsed-time-duration"))
        assertTrue(overreach, overreach.contains("partial duration evidence does not clear elapsed-time-duration"))
        val host = pack.provenanceRecords.first { it.originalSourceText == elapsed.exactSpan }
        val wrongObjective = V2Validator.validate(
            pack.copy(sourceCapabilities = pack.sourceCapabilities + capability(host, "cap-wrong-objective", 23, "obj-u23-duration", "completion-over-period")),
        ).errors.joinToString("\n")
        assertTrue(wrongObjective, wrongObjective.contains("attached to the wrong unit or objective"))
        val wrongUnit = V2Validator.validate(
            pack.copy(sourceCapabilities = pack.sourceCapabilities + capability(host, "cap-wrong-unit", 24, "obj-u23-completion", "completion-over-period")),
        ).errors.joinToString("\n")
        assertTrue(wrongUnit, wrongUnit.contains("attached to the wrong unit or objective"))
        val wrongClaim = V2Validator.validate(
            pack.copy(sourceCapabilities = pack.sourceCapabilities + capability(host, "cap-wrong-claim", 23, "obj-u23-completion", "elapsed-time-duration")),
        ).errors.joinToString("\n")
        assertTrue(wrongClaim, wrongClaim.contains("attached to the wrong unit or objective"))
        val moved = host.copy(objectiveId = "obj-u23-completion", itemId = "prov-u23-completion-host")
        val forged = capability(moved, "cap-forged-completion", 23, "obj-u23-completion", "completion-over-period")
        listOf(
            forged.copy(sourceId = "src-openrussian-50e210c4"),
            forged.copy(sourceRevision = "999"),
            forged.copy(sourceHash = "0".repeat(64)),
            forged.copy(exactSpan = "not the frozen span"),
        ).forEach { broken ->
            val errors = V2Validator.validate(
                pack.copy(
                    provenanceRecords = pack.provenanceRecords + moved,
                    sourceCapabilities = pack.sourceCapabilities + broken,
                ),
            ).errors.joinToString("\n")
            assertTrue(errors, errors.contains("revision does not match the frozen record") || errors.contains("span is not in the frozen record"))
            assertTrue(errors, errors.contains("no independent completion-over-period admission"))
            val withoutCompletion = pack.sourceCapabilities.filter { it.id != "cap-u23-completion-over-period" }
            assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(
                pack.copy(provenanceRecords = pack.provenanceRecords + moved, sourceCapabilities = withoutCompletion + broken),
                23,
            ).any { it.claim == "completion-over-period" })
        }
        val bookText = unit.sessions.flatMap { it.steps }.first { it.kind == "BOOK" }.body
        val bookRecord = sample(pack, 23, elapsed.sourceRevision, elapsed.sourceId, bookText).copy(objectiveId = "obj-u23-completion")
        val withoutCompletion = pack.copy(
            sourceCapabilities = pack.sourceCapabilities.filter { it.id != "cap-u23-completion-over-period" },
        )
        val bookErrors = V2Validator.validate(
            claimComplete(
                withoutCompletion.copy(
                    provenanceRecords = withoutCompletion.provenanceRecords + bookRecord,
                    sourceCapabilities = withoutCompletion.sourceCapabilities + capability(bookRecord, "cap-book-completion", 23, "obj-u23-completion", "completion-over-period"),
                ),
                23,
            ),
        ).errors.joinToString("\n")
        assertTrue(bookErrors, bookErrors.contains("no independent completion-over-period admission"))
        assertTrue(bookErrors, bookErrors.contains("without an admitted completion-over-period source capability"))
        assertFalse(bookErrors, bookErrors.contains("book self-attestation clears"))
    }

    @Test
    fun keywordTextDoesNotAdmitASourceCapability() {
        val pack = pack()
        listOf(21, 22, 23, 24, 25).forEach { number ->
            val unrelated = sample(pack, number, "9000001", "src-wikibooks-russian-cc-by-sa", keywordBlob)
            val base = if (number in listOf(21, 22, 23, 24, 25)) withoutCapabilities(pack, number) else pack
            val claimed = claimComplete(base.copy(provenanceRecords = base.provenanceRecords + unrelated), number)
            val errors = V2Validator.validate(claimed).errors
            assertTrue(errors.joinToString("\n"), errors.any { it.contains("source capability") || it.contains("source capabilities") })
            assertTrue(errors.none { it.contains("keyword") })
        }
    }

    @Test
    fun forgedRevisionMixedSpanAndWrongObjectiveCapabilitiesFail() {
        val pack = pack()
        val host = pack.provenanceRecords.first { it.unitNumber == 21 && it.originalSourceText.length > 40 }
        val other = pack.provenanceRecords.first { it.unitNumber == 25 && it.originalSourceText != host.originalSourceText }
        val forged = capability(host, "cap-forged", host.unitNumber, "obj-u21-object-reference", "reflexive-object")
            .copy(sourceRevision = "9999999")
        val mixed = capability(host, "cap-mixed", host.unitNumber, "obj-u21-object-reference", "reflexive-object")
            .copy(exactSpan = other.originalSourceText)
        val wrongUnit = capability(host, "cap-wrong-unit", 22, "obj-u22-declension", "declined-numeral")
        val wrongObjective = capability(host, "cap-wrong-objective", 21, "obj-u21-svoy", "reflexive-object")
        val keywordRecord = sample(pack, 21, "9000001", "src-wikibooks-russian-cc-by-sa", keywordBlob).copy(
            objectiveId = "obj-u21-object-reference",
        )
        val keywordCapability = capability(
            keywordRecord,
            "cap-keywords",
            21,
            "obj-u21-object-reference",
            "reflexive-object",
        )
        val withCapabilities = pack.copy(
            provenanceRecords = pack.provenanceRecords + keywordRecord,
            sourceCapabilities = listOf(forged, mixed, wrongUnit, wrongObjective, keywordCapability),
        )
        val errors = V2Validator.validate(claimComplete(withCapabilities, 21)).errors.joinToString("\n")
        assertTrue(errors, errors.contains("cap-forged") && errors.contains("revision does not match the frozen record"))
        assertTrue(errors, errors.contains("cap-mixed") && errors.contains("span is not in the frozen record"))
        assertTrue(errors, errors.contains("cap-wrong-unit") && errors.contains("wrong unit or objective"))
        assertTrue(errors, errors.contains("cap-wrong-objective") && errors.contains("wrong unit or objective"))
        assertTrue(errors, errors.contains("cap-keywords") && errors.contains("no reviewable instruction and check chain"))
        assertTrue(errors, errors.contains("without admitted reflexive-object and purpose source capabilities"))
    }

    @Test
    fun checksRejectCircularFeedbackAndLineLabelRecall() {
        val pack = pack()
        val governed = pack.items.filter { item ->
            val unit = item.objectiveId.removePrefix("obj-u").substringBefore("-").toIntOrNull()
            unit in 21..25 && item.assessment != null
        }
        assertTrue(governed.isNotEmpty())
        governed.forEach { item ->
            val assessment = item.assessment ?: error(item.id)
            val why = assessment.whyAnswerDemonstratesObjective
            val differences = assessment.distractorDifference.filter { it.isNotBlank() }
            assertFalse(item.id, item.prompt.contains("line is the", ignoreCase = true))
            assertFalse(item.id, item.prompt.contains("English label", ignoreCase = true))
            assertFalse(item.id, why.trim().endsWith("?"))
            assertFalse(item.id, why.startsWith("Does this", ignoreCase = true))
            assertEquals(item.id, differences.size, differences.toSet().size)
            differences.forEach { difference ->
                assertFalse(item.id, difference.startsWith("Does this", ignoreCase = true))
            }
        }
        val donor = governed.first { item -> item.options.count { it != item.answer } >= 2 }
        val circular = donor.copy(
            prompt = "Which admitted line is the film agreement line?",
            feedback = "Does this answer show the film agreement line?",
            distractorRationales = donor.options.map { option ->
                if (option == donor.answer) "" else "Does this row fail the film agreement line?"
            },
            assessment = donor.assessment!!.copy(
                whyAnswerDemonstratesObjective = "Does this answer show the film agreement line?",
                distractorDifference = donor.options.map { option ->
                    if (option == donor.answer) "" else "Does this row fail the film agreement line?"
                },
            ),
        )
        val errors = V2Validator.validate(pack.copy(items = pack.items.map { if (it.id == donor.id) circular else it })).errors
        val text = errors.joinToString("\n")
        assertTrue(text, text.contains("prompt discloses its classification"))
        assertTrue(text, text.contains("tests line-label recall"))
        assertTrue(text, text.contains("circular feedback"))
        assertTrue(text, text.contains("repeats a generic rationale"))
    }

    @Test
    fun listeningInstructionGuardAllowsListenerNounsButRejectsAudioDirections() {
        listOf("one listener", "several listeners", "Listeners hear polite address", "Listening stays with the book. No audio is mapped here.").forEach {
            assertFalse(it, listeningInstruction.containsMatchIn(it))
        }
        listOf("listen to the recording", "Listen here", "[[Media:clip.ogg|listen]]").forEach {
            assertTrue(it, listeningInstruction.containsMatchIn(it))
        }
        assertTrue(".ogg" in excludedLearnerText)
        assertTrue("[[Media:" in excludedLearnerText)
        assertTrue("don't deserve the title Comrade" in excludedLearnerText)
    }

    @Test
    fun datedNamesClaimsAndLinkedAudioStayOutOfLearnerContent() {
        val pack = pack()
        val root = repo()
        learnerFields(pack, 21..25).forEach { (field, text) ->
            excludedLearnerText.forEach { excluded ->
                assertFalse("$field contains $excluded", excluded in text)
            }
            assertFalse("$field contains a listening instruction", listeningInstruction.containsMatchIn(text))
            assertFalse(field, mediaWiki(text))
        }
        pack.provenanceRecords.filter { it.unitNumber in 21..25 }.forEach { record ->
            excludedLearnerText.forEach { excluded ->
                assertFalse("${record.itemId} ships $excluded", excluded in record.shippedText)
            }
            assertFalse("${record.itemId} ships a listening instruction", listeningInstruction.containsMatchIn(record.shippedText))
        }
        val names = frozenFile(root, "wikibooks/names-4660786.txt").readText()
        assertTrue(names.contains("don't deserve the title Comrade"))
        assertTrue(names.contains("Creativity in baby-naming"))
        val useful = frozenFile(root, "wikibooks/useful-words-3353859.txt").readText()
        assertTrue(useful.contains(".ogg"))
        assertTrue(useful.contains("[[Media:"))
        assertTrue(pack.provenanceRecords.filter { it.unitNumber == 25 && it.sourceRevision == "3353859" }.none { record ->
            record.originalSourceText.contains(".ogg") || record.originalSourceText.contains("[[Media:")
        })
        assertEquals(0, pack.audioAssets.size)
        assertTrue(pack.provenanceRecords.none { it.unitNumber in 21..25 && it.audioAssetId != null })
        assertFalse(File(root, "catalog/sources/units-21-25").walkTopDown().any { file ->
            file.isFile && file.extension.lowercase() in setOf("ogg", "mp3", "wav", "png", "jpg", "jpeg", "pdf")
        })
    }

    @Test
    fun noPenguinContentInternetPermissionOrOpenReleaseGate() {
        val root = repo()
        val listed = ProcessBuilder(listOf("git", "ls-files", "*.pdf", "*.PDF"))
            .directory(root)
            .redirectError(File("/dev/null"))
            .start()
            .let { process ->
                val text = process.inputStream.bufferedReader().readText()
                check(process.waitFor() == 0)
                text
            }
        assertEquals("", listed.trim())
        val curriculum = File(root, "app/src/main/assets/content/v2-curriculum.json").readText()
        assertFalse(curriculum.contains("The New Penguin Russian Course"))
        assertFalse(curriculum.contains("PENGUIN_EDITION_MAPPING_INPUT"))
        assertEquals(0, pack().admissions.generatedRussianWords)
        assertFalse(File(root, "app/src/main/AndroidManifest.xml").readText().contains("android.permission.INTERNET"))
        val gradle = File(root, "app/build.gradle.kts").readText()
        assertTrue(gradle.contains("dependsOn(\"checkReleaseGate\")"))
        assertTrue(gradle.contains("verifyReleaseGateAttachment"))
        assertTrue(File(root, "app/src/main/java/dev/russiancompanion/release/ReleaseGate.kt").readText().contains("Public release is not approved."))
        val candidate = File(root, "release/acceptance/candidate.json").readText()
        assertTrue(candidate.contains("\"publicRelease\": \"NOT_APPROVED\""))
        assertTrue(candidate.contains("\"frozen\": false"))
        assertTrue(candidate.contains("\"sourceCommit\": null"))
        assertTrue(candidate.contains("AT-031"))
        val workflow = File(root, ".github/workflows/build.yml").readText()
        assertTrue(workflow.contains("verifyReleaseGateAttachment"))
        assertFalse(workflow.contains("assembleRelease"))
    }

    private fun withoutCapabilities(pack: V2CurriculumPack, number: Int) = pack.copy(
        sourceCapabilities = pack.sourceCapabilities.filter { it.unitNumber != number },
    )

    private fun assertGap(pack: V2CurriculumPack, number: Int, message: String, revision: String, text: String) {
        val added = sample(pack, number, revision, "src-wikibooks-russian-cc-by-sa", text)
        val errors = V2Validator.validate(claimComplete(pack.copy(provenanceRecords = pack.provenanceRecords + added), number)).errors
        assertTrue(errors.joinToString("\n"), errors.any { it.contains(message) })
    }

    private fun attachCheck(pack: V2CurriculumPack, number: Int, objectiveId: String): V2CurriculumPack {
        val donor = pack.unit(number)!!.objectives.firstOrNull { it.kind == "RECOGNITION" }?.checkItemIds?.firstOrNull()
            ?: pack.items.first { it.objectiveId.startsWith("obj-u21-") }.id
        return pack.copy(
            units = pack.units.map { unit ->
                if (unit.number != number) {
                    unit
                } else {
                    unit.copy(
                        objectives = unit.objectives.map { objective ->
                            if (objective.id == objectiveId) objective.copy(checkItemIds = listOf(donor)) else objective
                        },
                    )
                }
            },
        )
    }

    private fun reason(pack: V2CurriculumPack, number: Int): String {
        return pack.unit(number)!!.sessions.flatMap { it.steps }.mapNotNull { it.unavailableReason }.joinToString("\n")
    }

    private fun claimComplete(pack: V2CurriculumPack, number: Int) = pack.copy(
        units = pack.units.map { unit ->
            if (unit.number != number) {
                unit
            } else {
                unit.copy(
                    readiness = "CONTENT_COMPLETE_REVIEW_PENDING",
                    sessions = unit.sessions.map { session ->
                        session.copy(
                            steps = session.steps.map { step ->
                                if (step.kind == "UNAVAILABLE" || !step.unavailableReason.isNullOrBlank()) {
                                    step.copy(kind = "ORIENT", unavailableReason = null)
                                } else {
                                    step
                                }
                            },
                        )
                    },
                )
            }
        },
    )

    private fun capability(
        record: V2ProvenanceRecord,
        id: String,
        unitNumber: Int,
        objectiveId: String,
        claim: String,
    ) = V2SourceCapability(
        id = id,
        sourceId = record.sourceId,
        sourceRevision = record.sourceRevision,
        sourceRecordId = record.sourceRecordId,
        sourceHash = record.sourceHash,
        exactSpan = record.originalSourceText,
        unitNumber = unitNumber,
        objectiveId = objectiveId,
        supportedClaim = claim,
    )

    private fun sample(pack: V2CurriculumPack, number: Int, revision: String, sourceId: String, text: String): V2ProvenanceRecord {
        return pack.provenanceRecords.first().copy(
            itemId = "prov-gap-probe-$number",
            unitNumber = number,
            sourceRevision = revision,
            sourceId = sourceId,
            originalSourceText = text,
            shippedText = text,
            sourceHash = sha256(text),
            contentHash = sha256(text),
        )
    }

    private fun learner(number: Int, book: Boolean, correct: Set<String>, steps: Set<String>) = V2LearnerView(
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
        completedStepIds = steps,
        correctItemIds = correct,
        bookAttestedUnits = if (book) setOf(number) else emptySet(),
        completedUnitNumbers = emptySet(),
    )

    private fun frozenFile(root: File, relative: String): File {
        return File(File(root, "catalog/sources/units-21-25"), relative).canonicalFile
    }

    private fun mediaWiki(text: String): Boolean {
        return listOf("'''", "''", "[[", "]]", "{{", "}}", "<ref", "</ref>", "{|", "|}").any { it in text }
    }

    private fun learnerFields(pack: V2CurriculumPack, units: IntRange): List<Pair<String, String>> {
        val fields = mutableListOf<Pair<String, String>>()
        pack.units.filter { it.number in units }.forEach { unit ->
            fields += unit.id to unit.topicSummary
            fields += unit.id to unit.mappingNote
            unit.sessions.forEach { session ->
                fields += session.id to session.title
                session.steps.forEach { step ->
                    fields += step.id to step.title
                    fields += step.id to step.body
                    step.bookDirection?.let { fields += step.id to it }
                    step.unavailableReason?.let { fields += step.id to it }
                }
            }
            unit.objectives.forEach { objective ->
                fields += objective.id to objective.title
            }
        }
        val exampleIds = pack.units.filter { it.number in units }.flatMap { unit -> unit.objectives.flatMap { it.exampleIds } }.toSet()
        pack.examples.filter { it.id in exampleIds }.forEach { example ->
            fields += example.id to example.display
            fields += example.id to example.note
        }
        val itemIds = pack.units.filter { it.number in units }.flatMap { unit ->
            unit.sessions.flatMap { session -> session.steps.flatMap { it.itemIds } } + unit.objectives.flatMap { it.checkItemIds }
        }.toSet()
        pack.items.filter { it.id in itemIds }.forEach { item ->
            fields += item.id to item.prompt
            item.promptLetter?.let { fields += item.id to it }
            fields += item.id to item.answer
            fields += item.id to item.feedback
            item.options.forEach { fields += item.id to it }
            item.distractorRationales.forEach { fields += item.id to it }
            item.assessment?.let { assessment ->
                fields += item.id to assessment.whyAnswerDemonstratesObjective
                assessment.distractorDifference.forEach { fields += item.id to it }
                assessment.contrastMembers.forEach { fields += item.id to it }
            }
        }
        pack.provenanceRecords.filter { it.unitNumber in units }.forEach { record ->
            fields += record.itemId to record.shippedText
        }
        pack.references.filter { it.introducedInUnit in units }.forEach { reference ->
            fields += reference.id to reference.title
            fields += reference.id to reference.summary
            fields += reference.id to reference.detail
        }
        return fields
    }

    private fun normalized(text: String) = text.replace("\u0301", "").replace("'", "")

    private fun sha256(text: String): String = sha256(text.toByteArray(Charsets.UTF_8))

    private fun sha256(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        return digest.joinToString("") { byte -> "%02x".format(byte) }
    }

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
