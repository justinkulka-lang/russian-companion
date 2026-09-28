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
        21 to "259-270",
        22 to "271-284",
        23 to "285-295",
        24 to "296-306",
        25 to "307-321",
    )

    private val preservedContentDigests = mapOf(
        1 to "9adca20544f28770210fe3029c2e0e4c437e3b409216e9288227c316e9ebf61a",
        2 to "d51310f9ac1596ab50d2c5df7dffd9b10db23d712875680dbfb942bf577e056b",
        3 to "deadcf10b8af7efca034b8d89921412f309d291180c09c4af490eb132b1bbb86",
        4 to "5a9be201954e4a4c37188ed7aca9fae1ee67e20622c7134271c1a68cd520e59c",
        5 to "9470cc61d50236425fb01771a4d20788c88d6c98f8a8694420609f86d7b9710d",
        6 to "fa6501022bd7ff4a2eca1e47c0ad488d96254cab0eafbecc61a8420835a3ab9c",
        7 to "9db2b449a150d96b668230738731655cf01a163a552fbacd4152579be638989f",
        8 to "e1dd456abf8a7b961f2f8471f35d4178961a53147c354f3e410e88d75f025c78",
        9 to "d0f135c408fc429392cf695a8122134013fca69637a4f9c1587db3fec6b3c65f",
        10 to "6845b398da6ad901b06443dd0f56ee6ace4ba4e2da6af9e36d33934dc286c526",
        11 to "7a70c3b29f7c88934d0dcf21f40d531a45f1eff9daa8593f5af09d509bd10324",
        12 to "c610495bf16604d555187f66e456c2cd1cb127c77881ef87097332f5e2d426bb",
        13 to "e8a04394dc8c90059f01a0b7fdf46a57951a98a85ac8050a29781ab1a4a027c5",
        14 to "270cbad6d62543931aa65932bdaeda9d10475bab78f7c12f05a8f75ac1308ec6",
        15 to "1fceb09b1709fcd76676925c5e363736fad187db50e2830cbd115bb5891219dd",
        16 to "6797fdb9c88927be744444861238a7eb3c2574e25b059df57dde02f45a8015c5",
        17 to "a3d3b89b7359bac0dc1ba33483c819e31f22f69c0344be860dbac286637a404c",
        18 to "6e6faca384de88b45092fcbdfe63d86a244f4bcc8459f290c2821d05faa083ae",
        19 to "75cf1e4e1baa64f6b3025d3daa9fc63ab854d0c10fb40246e55d484077f1b6f3",
        20 to "01625d197e02f19603213ae6694002fa0bfa5418cc10484a7209b9f7c581129c",
    )

    private val preservedMappingDigests = mapOf(
        16 to "7b89d0ad3f4bdb94e023804b86d8eaf30bcd0d44cace703783682dc3d27b9afa",
        17 to "f3b65cb31a8f11abd9ebdc810be96618acb7565d78608655c169a507098e570e",
        18 to "bbc970db2dfb4a17b8433024c289db9045424d73e3e64a61f699f992704ffbda",
        19 to "907a9086e25bd1a87abef73f9271ccf32dbe4df12eedacba4eaf23603fe644b9",
        20 to "145bb8c75afc12e093bcc205093f95ffe81636a0c5cd1aee64d0aa8766f6e69f",
    )

    private val excludedLearnerText = listOf(
        "don't deserve the title Comrade",
        "wealthy foreigners",
        "Ordinary citizens of foreign countries",
        "only a dozen",
        "Creativity in baby-naming",
        ".ogg",
        "[[Media:",
        "listen",
        "good fighter",
        "Lord of the World",
    )

    private val keywordBlob = (
        "\u0441\u0435\u0431\u044f \u0447\u0442\u043e\u0431\u044b declension of numerals \u043e\u0431\u0430 \u043e\u0431\u0435 " +
            "\u043f\u043e\u043b\u0442\u043e\u0440\u0430 collective numeral \u0441\u043e\u0431\u0438\u0440\u0430\u0442\u0435\u043b\u044c\u043d " +
            " ago during elapsed accusative genitive negation negative pronoun \u043d\u0438\u043a\u0442\u043e scope " +
            "diminutive suffix register"
        )

    private val gapObjectives = mapOf(
        23 to listOf("obj-u23-completion"),
    )

    @Test
    fun structuralMappingsAreDigestBoundAndStayUnsigned() {
        val pack = pack()
        assertTrue(V2Validator.validate(pack).errors.joinToString("\n"), V2Validator.validate(pack).ok)
        pages.forEach { (number, range) ->
            val unit = pack.unit(number)!!
            val facts = governedMappingFacts(number)
            val digest = mappingPackageDigest(facts)
            assertEquals("DEVELOPMENT", unit.installState)
            assertEquals(if (number == 23 || number == 25) "NOT_PREPARED" else "CONTENT_COMPLETE_REVIEW_PENDING", unit.readiness)
            assertEquals("PENDING_CURRICULUM_REVIEWER", unit.mappingStatus)
            assertEquals(number, unit.penguinLesson)
            assertTrue(unit.mappingNote.contains(range))
            assertTrue(unit.mappingNote.contains("PENDING_CURRICULUM_REVIEWER"))
            assertFalse(unit.mappingNote.contains("APPROVED"))
            assertFalse(unit.mappingNote.contains("ISBN"))
            assertFalse(unit.mappingNote.contains("2bcc0c45"))
            assertEquals(range, "${facts.printedPageStart}-${facts.printedPageEnd}")
            assertEquals(number, facts.proposedLessonNumber)
            assertEquals("PENDING_CURRICULUM_REVIEWER", facts.mappingDisposition)
            assertEquals("UNRESOLVED", facts.isbnObservation)
            assertNull(facts.isbn)
            assertFalse(facts.observedEditionNotes.orEmpty().contains("2bcc0c45"))
            assertTrue(pack.ownedEditionMappings.none { it.unitNumber == number })
            val review = pack.unitReviews.first { it.unitNumber == number }
            assertEquals("PENDING_QUALIFIED_REVIEW", review.status)
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
        preservedContentDigests.forEach { (number, digest) ->
            assertEquals("unit $number", digest, V2Validator.unitContentReviewDigest(pack, number))
        }
        preservedMappingDigests.forEach { (number, digest) ->
            assertEquals("mapping $number", digest, mappingPackageDigest(governedMappingFacts(number)))
        }
        assertTrue(pack.units.drop(25).all { it.installState == "DEVELOPMENT" && it.sessions.isNotEmpty() })
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(28)!!.readiness)
        assertEquals(setOf(26, 27, 29, 30), pack.units.drop(25).filter { it.readiness == "NOT_PREPARED" }.map { it.number }.toSet())
        assertTrue(File(repo(), "review/unit-23/UNIT_23_REVIEW_PACKET.md").isFile)
    }

    @Test
    fun frozenSourceIdentityAndProvenanceSpansAreRequired() {
        val pack = pack()
        val root = repo()
        val freeze = Json.parseToJsonElement(File(root, "catalog/sources/units-21-25/FREEZE.json").readText()).jsonObject
        assertEquals("CC BY-SA 4.0", freeze.getValue("license").jsonObject.getValue("licenseId").jsonPrimitive.content)
        assertEquals(
            "https://creativecommons.org/licenses/by-sa/4.0/",
            freeze.getValue("license").jsonObject.getValue("licenseUrl").jsonPrimitive.content,
        )
        val licenseBytes = frozenFile(root, freeze.getValue("license").jsonObject.getValue("retainedTextFile").jsonPrimitive.content).readBytes()
        assertEquals(
            freeze.getValue("license").jsonObject.getValue("retainedTextSha256").jsonPrimitive.content,
            sha256(licenseBytes),
        )
        assertTrue(String(licenseBytes, Charsets.UTF_8).contains("Creative Commons Attribution-ShareAlike 4.0"))
        val expected = mapOf(
            "possessive" to "4220315",
            "numbers" to "4371358",
            "verbs" to "4233876",
            "cases" to "4195114",
            "names" to "4660786",
            "useful" to "3353859",
            "copyrights" to "4622060",
        )
        val byOldid = mutableMapOf<String, String>()
        val seen = mutableSetOf<String>()
        freeze.getValue("wikibooks").jsonObject.getValue("pages").jsonArray.forEach { element ->
            val page = element.jsonObject
            val key = page.getValue("key").jsonPrimitive.content
            seen += key
            assertEquals(expected.getValue(key), page.getValue("oldid").jsonPrimitive.content)
            val bytes = frozenFile(root, page.getValue("file").jsonPrimitive.content).readBytes()
            assertEquals(page.getValue("sha256").jsonPrimitive.content, sha256(bytes))
            assertEquals(page.getValue("size").jsonPrimitive.content, bytes.size.toString())
            val revision = Json.parseToJsonElement(frozenFile(root, page.getValue("revisionFile").jsonPrimitive.content).readText()).jsonObject
            assertEquals(page.getValue("oldid").jsonPrimitive.content, revision.getValue("revid").jsonPrimitive.content)
            assertEquals(page.getValue("mediawikiSha1").jsonPrimitive.content, revision.getValue("sha1").jsonPrimitive.content)
            assertEquals(page.getValue("parentid").jsonPrimitive.content, revision.getValue("parentid").jsonPrimitive.content)
            assertEquals(page.getValue("timestamp").jsonPrimitive.content, revision.getValue("timestamp").jsonPrimitive.content)
            assertTrue(page.getValue("url").jsonPrimitive.content.contains("oldid=${page.getValue("oldid").jsonPrimitive.content}"))
            byOldid[page.getValue("oldid").jsonPrimitive.content] = bytes.toString(Charsets.UTF_8)
        }
        assertEquals(expected.keys, seen)
        val decisions = freeze.getValue("decisions").jsonArray.map { it.jsonObject }
        val admitted = decisions.filter { it.getValue("disposition").jsonPrimitive.content == "ADMITTED" }
            .map { it.getValue("id").jsonPrimitive.content }.toSet()
        assertEquals(
            setOf(
                "wikibooks-possessive-pronouns-4220315",
                "wikibooks-numbers-4371358",
                "wikibooks-verbs-4233876",
                "wikibooks-cases-4195114",
                "wikibooks-names-4660786",
                "wikibooks-useful-words-3353859",
                "pressbooks-usa-grammar-87815b8724cb11aecba5bb6d8b6438f783408f2354e2560a3c0fe026fac1e171",
                "readyrussian-langgen3",
            ),
            admitted,
        )
        val excluded = decisions.filter { it.getValue("disposition").jsonPrimitive.content == "EXCLUDED" }
            .map { it.getValue("id").jsonPrimitive.content }.toSet()
        assertTrue(excluded.contains("wikibooks-names-dated-claims-4660786"))
        assertTrue(excluded.contains("wikibooks-useful-words-linked-audio-3353859"))
        assertTrue(excluded.contains("openrussian-50e210c4803237779cb562bc1abcea529066031c"))
        assertTrue(excluded.contains("penguin-private-pdf"))
        assertTrue(excluded.contains("pressbooks-baltics-travel-grammar-f5217cd052399bfe593787d1af40535805b6d795db924e8d03a2147fcb4b59aa"))
        val balticsRoute = freeze.getValue("license").jsonObject.getValue("routes").jsonObject.getValue("pressbooks-baltics-travel-grammar").jsonObject
        val balticsBytes = frozenFile(root, balticsRoute.getValue("file").jsonPrimitive.content).readBytes()
        assertEquals(balticsRoute.getValue("sha256").jsonPrimitive.content, sha256(balticsBytes))
        assertEquals(balticsRoute.getValue("byteCount").jsonPrimitive.content, balticsBytes.size.toString())
        val balticsText = balticsBytes.toString(Charsets.UTF_8)
        assertTrue(balticsText.contains("How long something takes vs. For how long"))
        assertTrue(balticsText.contains("Creative Commons Attribution-ShareAlike 4.0 International License"))
        assertTrue(balticsText.contains("Anna Tumarkin"))
        assertTrue(balticsText.contains("Shannon Donnally Quinn"))
        assertFalse(balticsText.contains("Wikipedia:Copyrights"))
        val supplementalByOldid = mutableMapOf<String, String>()
        freeze.getValue("supplemental").jsonArray.forEach { element ->
            val page = element.jsonObject
            val bytes = frozenFile(root, page.getValue("file").jsonPrimitive.content).readBytes()
            assertEquals(page.getValue("sha256").jsonPrimitive.content, sha256(bytes))
            assertEquals(page.getValue("byteCount").jsonPrimitive.content, bytes.size.toString())
            supplementalByOldid[page.getValue("oldid").jsonPrimitive.content] = bytes.toString(Charsets.UTF_8)
        }
        val intake = pack.provenanceRecords.filter { it.unitNumber in 21..25 }
        assertEquals(339, intake.size)
        assertEquals(setOf(21, 22, 23, 24, 25), intake.map { it.unitNumber }.toSet())
        assertTrue(intake.none { it.sourceId == "src-openrussian-50e210c4" })
        assertTrue(intake.all { it.audioAssetId == null })
        intake.forEach { record ->
            assertEquals(sha256(record.originalSourceText), record.sourceHash)
            assertEquals(sha256(record.shippedText), record.contentHash)
            assertTrue(record.sourceRevision.isNotBlank())
            if (record.sourceId == "src-pressbooks-diverse-russian-usa-grammar") {
                assertEquals("https://wisc.pb.unizin.org/diverserussian/chapter/usa-grammar/", record.sourceUrl)
                assertFalse(record.sourceUrl.contains("oldid="))
                assertTrue(record.attribution.contains(record.sourceRevision))
                assertTrue(record.attribution.contains("2026-09-26T22:52:41Z"))
                assertFalse(record.attribution.contains("Wikipedia:Copyrights"))
                assertFalse(record.attribution.contains("Wiktionary:Copyrights"))
                assertFalse(record.attribution.contains("Wikibooks:Copyrights"))
            } else if (record.sourceId.startsWith("src-readyrussian-")) {
                assertTrue(record.sourceUrl.contains("readyrussian.org"))
                assertFalse(record.sourceUrl.contains("oldid="))
                assertTrue(record.attribution.contains(record.sourceRevision))
                assertTrue(record.attribution.contains("ReadyRussian.org"))
                assertFalse(record.attribution.contains("Wikipedia:Copyrights"))
                assertFalse(record.attribution.contains("Wiktionary:Copyrights"))
                assertFalse(record.attribution.contains("Wikibooks:Copyrights"))
            } else {
                assertTrue(record.sourceUrl.contains(record.sourceRevision))
            }
            assertEquals("CC BY-SA 4.0", record.licenseId)
            assertEquals("https://creativecommons.org/licenses/by-sa/4.0/", record.licenseUrl)
            assertTrue(record.attribution.isNotBlank())
            assertEquals("PENDING_QUALIFIED_REVIEW", record.reviewStatus)
            assertTrue(record.reviewerIdentity.isBlank())
            assertTrue(record.reviewerQualification.isBlank())
            assertTrue(record.reviewDate.isBlank())
            assertTrue(record.reviewedDigest.isBlank())
            val page = byOldid[record.sourceRevision] ?: supplementalByOldid.getValue(record.sourceRevision)
            assertTrue(record.itemId, page.contains(record.originalSourceText))
            if (record.sourceRevision in byOldid) {
                assertTrue(record.itemId, normalized(record.originalSourceText).contains(normalized(record.shippedText)))
            }
            assertFalse(record.shippedText.contains("The New Penguin Russian Course"))
        }
        val brokenHash = pack.copy(
            provenanceRecords = pack.provenanceRecords.map { record ->
                if (record.unitNumber == 21) record.copy(sourceHash = "0".repeat(64)) else record
            },
        )
        assertTrue(V2Validator.validate(brokenHash).errors.any { it.contains("source hash does not match the frozen record") })
        val blankRevision = pack.copy(
            provenanceRecords = pack.provenanceRecords.map { record ->
                if (record.itemId == intake.first().itemId) record.copy(sourceRevision = "") else record
            },
        )
        assertTrue(V2Validator.validate(blankRevision).errors.any { it.contains("source revision is unresolved") })
        assertEquals(0, pack.admissions.generatedRussianWords)
        assertEquals(0, pack.admissions.penguinProseAssets)
        assertEquals(0, pack.audioAssets.size)
        assertTrue(pack.items.filter { it.id.startsWith("u2") && it.objectiveId.contains("-u2") }.all { !it.containsGeneratedRussian })
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
        assertTrue(reason(pack, 23).contains("Completion over a period"))
        assertTrue(reason(pack, 24).isBlank())
        val placement = pack.unit(24)!!.objectives.first { it.id == "obj-u24-placement" }
        assertEquals("RECOGNITION", placement.kind)
        assertTrue(placement.checkItemIds.size >= 2)
        val contrast = pack.unit(24)!!.objectives.first { it.id == "obj-u24-word-order-contrast" }
        assertEquals("RECOGNITION", contrast.kind)
        assertTrue(contrast.checkItemIds.size >= 2)
        assertTrue(reason(pack, 25).isBlank())
        assertTrue(pack.unit(25)!!.objectives.none { it.id == "obj-u25-productive-formation" })
        listOf(21, 22, 24).forEach { number ->
            val unit = pack.unit(number)!!
            val decision = advancementDecision(
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
        }
        val unit25 = pack.unit(25)!!
        val blocked25 = advancementDecision(
            pack,
            unit25,
            learner(
                25,
                book = true,
                correct = unit25.objectives.flatMap { it.checkItemIds }.toSet() +
                    unit25.sessions.flatMap { it.steps }.flatMap { it.itemIds }.toSet(),
                steps = unit25.sessions.flatMap { it.steps }.map { it.id }.toSet(),
            ),
        )
        assertFalse(blocked25.allowed)
        assertTrue(blocked25.remaining.joinToString("\n"), blocked25.remaining.any { it.contains("not prepared") })
        assertTrue(blocked25.remaining.any { it.contains("productive-diminutive") })
        val unit23 = pack.unit(23)!!
        val blocked = advancementDecision(
            pack,
            unit23,
            learner(
                23,
                book = true,
                correct = unit23.objectives.flatMap { it.checkItemIds }.toSet() +
                    unit23.sessions.flatMap { it.steps }.flatMap { it.itemIds }.toSet(),
                steps = unit23.sessions.flatMap { it.steps }.map { it.id }.toSet(),
            ),
        )
        assertFalse(blocked.allowed)
        assertTrue(blocked.remaining.joinToString("\n"), blocked.remaining.any { it.contains("not prepared") })
    }

    @Test
    fun partialStrandsStayShortOfTheMissingSources() {
        val pack = pack()
        assertGap(withoutCapabilities(pack, 21), 21, "without admitted reflexive-object and purpose source capabilities", "4220315", keywordBlob)
        assertGap(withoutCapabilities(pack, 22), 22, "without admitted declined-numeral, both, one-and-a-half, and collective source capabilities", "4371358", keywordBlob)
        assertGap(withoutCapabilities(pack, 24), 24, "without admitted alternation, negative-pronoun, and scope source capabilities", "4233876", keywordBlob)
        assertGap(withoutCapabilities(pack, 24), 24, "without admitted alternation, negative-pronoun, and scope source capabilities", "4195114", keywordBlob)
        assertGap(withoutCapabilities(pack, 25), 25, "without admitted productive-diminutive and register source capabilities", "4660786", keywordBlob)
        assertGap(withoutCapabilities(pack, 25), 25, "without admitted productive-diminutive and register source capabilities", "3353859", keywordBlob)
        val dictionary = sample(pack, 21, "50e210c4803237779cb562bc1abcea529066031c", "src-openrussian-50e210c4", keywordBlob)
        val fromRows = V2Validator.validate(claimComplete(withoutCapabilities(pack, 21).copy(provenanceRecords = pack.provenanceRecords + dictionary), 21)).errors
        assertTrue(fromRows.joinToString("\n"), fromRows.any { it.contains("without admitted reflexive-object and purpose source capabilities") })
    }

    @Test
    fun unitTwentyThreeElapsedTimeStaysReviewPendingAndPartialEvidenceStaysClosed() {
        val pack = pack()
        val unit = pack.unit(23)!!
        assertEquals("DEVELOPMENT", unit.installState)
        assertEquals("NOT_PREPARED", unit.readiness)
        assertEquals("PENDING_CURRICULUM_REVIEWER", unit.mappingStatus)
        assertTrue(unit.mappingNote.contains("completion over a period"))
        assertTrue(unit.topicSummary.contains("is not prepared"))
        assertTrue(unit.topicSummary.contains("Completion over a period has no admitted frozen span"))
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
        assertTrue(reason(pack, 23).contains("no admitted frozen span"))
        val completion = unit.objectives.single { it.id == "obj-u23-completion" }
        assertEquals("GUIDANCE", completion.kind)
        assertTrue(completion.checkItemIds.isEmpty())
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(pack, 23).any { it.claim == "completion-over-period" })
        val blocked = advancementDecision(
            pack,
            unit,
            learner(
                23,
                book = true,
                correct = unit.objectives.flatMap { it.checkItemIds }.toSet(),
                steps = unit.sessions.flatMap { it.steps }.map { it.id }.toSet(),
            ),
        )
        assertFalse(blocked.allowed)
        assertTrue(blocked.remaining.any { it.contains("not prepared") })
        assertTrue(blocked.remaining.any { it.contains("completion-over-period") })
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
        assertEquals("PENDING_QUALIFIED_REVIEW", review.status)
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
            assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(
                pack.copy(provenanceRecords = pack.provenanceRecords + moved, sourceCapabilities = pack.sourceCapabilities + broken),
                23,
            ).any { it.claim == "completion-over-period" })
        }
        val bookText = unit.sessions.flatMap { it.steps }.first { it.kind == "BOOK" }.body
        val bookRecord = sample(pack, 23, elapsed.sourceRevision, elapsed.sourceId, bookText).copy(objectiveId = "obj-u23-completion")
        val bookErrors = V2Validator.validate(
            claimComplete(
                pack.copy(
                    provenanceRecords = pack.provenanceRecords + bookRecord,
                    sourceCapabilities = pack.sourceCapabilities + capability(bookRecord, "cap-book-completion", 23, "obj-u23-completion", "completion-over-period"),
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
            val base = if (number in listOf(21, 22, 24, 25)) withoutCapabilities(pack, number) else pack
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
    fun datedNamesClaimsAndLinkedAudioStayOutOfLearnerContent() {
        val pack = pack()
        val root = repo()
        learnerFields(pack, 21..25).forEach { (field, text) ->
            excludedLearnerText.forEach { excluded ->
                assertFalse("$field contains $excluded", excluded in text)
            }
            assertFalse(field, mediaWiki(text))
        }
        pack.provenanceRecords.filter { it.unitNumber in 21..25 }.forEach { record ->
            excludedLearnerText.forEach { excluded ->
                assertFalse("${record.itemId} ships $excluded", excluded in record.shippedText)
            }
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
