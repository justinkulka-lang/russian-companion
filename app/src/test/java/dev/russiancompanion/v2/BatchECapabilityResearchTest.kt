package dev.russiancompanion.v2

import dev.russiancompanion.release.computeReleaseSourceDigest
import dev.russiancompanion.release.readAcceptanceCandidate
import dev.russiancompanion.v2.audit.loadCurriculumPack
import dev.russiancompanion.v2.audit.reconcileCurriculumReadiness
import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.curriculum.V2SourceCapability
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.advancementDecision
import dev.russiancompanion.v2.curriculum.unit
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.security.MessageDigest

/**
 * Batch E searched five unresolved capabilities and retained the pages it
 * inspected. None of those pages is an admission. Readiness and advancement
 * stay closed, and Units 26–30 stay unchanged.
 */
class BatchECapabilityResearchTest {
    @Test
    fun retainedPagesAreExcludedAndDoNotClearTheFiveCapabilities() {
        val root = root()
        val manifest = Json.parseToJsonElement(File(root, MANIFEST).readText()).jsonObject
        val dossier = File(root, DOSSIER).readText()
        val curriculum = File(root, "app/src/main/assets/content/v2-curriculum.json").readText()
        val pack = loadCurriculumPack(root)
        val pages = manifest.getValue("pages").jsonArray.map { it.jsonObject }
        assertEquals(6, pages.size)
        assertEquals("EXCLUDED", manifest.getValue("disposition").jsonPrimitive.content)
        assertEquals(
            "154562152",
            manifest.getValue("licenseRoute").jsonObject.getValue("oldid").jsonPrimitive.content,
        )
        pages.forEach { page ->
            val file = File(root, "audit/curriculum-readiness/batch-e-research/${page.getValue("file").jsonPrimitive.content}")
            val bytes = file.readBytes()
            assertEquals(page.getValue("sha256").jsonPrimitive.content, sha256(bytes))
            assertEquals(page.getValue("byteCount").jsonPrimitive.content.toInt(), bytes.size)
            val revision = Json.parseToJsonElement(
                File(root, "audit/curriculum-readiness/batch-e-research/${page.getValue("revisionFile").jsonPrimitive.content}").readText(),
            ).jsonObject
            assertEquals(page.getValue("oldid").jsonPrimitive.content, revision.getValue("revid").jsonPrimitive.content)
            val oldid = page.getValue("oldid").jsonPrimitive.content
            assertFalse(curriculum.contains(oldid))
            assertTrue(dossier.contains(oldid))
            assertTrue(dossier.contains(page.getValue("exclusion").jsonPrimitive.content))
            val claim = page.getValue("capability").jsonPrimitive.content
            val unitNumber = page.getValue("unit").jsonPrimitive.content.toInt()
            val objectiveId = REQUIRED.getValue(claim)
            val capability = V2SourceCapability(
                id = "cap-batch-e-$oldid",
                sourceId = "src-ruwiki-batch-e-$oldid",
                sourceRevision = oldid,
                sourceRecordId = "batch-e:$oldid",
                sourceHash = page.getValue("sha256").jsonPrimitive.content,
                exactSpan = file.readText(),
                unitNumber = unitNumber,
                objectiveId = objectiveId,
                supportedClaim = claim,
            )
            val errors = errors(pack, capability)
            assertTrue(errors, errors.contains("no independent $claim admission"))
            assertTrue(errors, errors.contains("revision does not match the frozen record"))
            assertFalse(
                V2Validator.unresolvedRequiredSourceCapabilities(
                    pack.copy(sourceCapabilities = pack.sourceCapabilities + capability),
                    unitNumber,
                ).any { it.claim == claim },
            )
        }
        assertTrue(dossier.contains("Units 26–30 were not modified."))
    }

    @Test
    fun identityMutationsSubstitutionsAndRelabelingFailClosed() {
        val pack = loadCurriculumPack(root())
        val claims = listOf(
            Triple(7, "obj-u7-adverb", "adverb-paradigm"),
            Triple(17, "obj-u17-clock-answers", "productive-clock-time"),
            Triple(23, "obj-u23-completion", "completion-over-period"),
            Triple(24, "obj-u24-placement", "negation-scope"),
            Triple(25, "obj-u25-nickname-rule", "productive-diminutive"),
        )
        claims.forEach { (unitNumber, objectiveId, claim) ->
            val keyword = when (claim) {
                "adverb-paradigm" -> "adverb paradigm co-occurrence"
                "productive-clock-time" -> "productive clock co-occurrence"
                "completion-over-period" -> "completion co-occurrence"
                "negation-scope" -> "negation scope co-occurrence"
                else -> "productive diminutive co-occurrence"
            }
            val keywordErrors = planted(pack, probe(unitNumber, objectiveId, claim, keyword))
            assertTrue(keywordErrors, keywordErrors.contains("keyword or co-occurrence evidence does not clear $claim"))
            assertTrue(keywordErrors, keywordErrors.contains("no independent $claim admission"))
            val dictionary = probe(unitNumber, objectiveId, claim, "dictionary row").copy(
                sourceId = "src-openrussian-50e210c4",
                sourceRecordId = "dictionary-row",
            )
            assertTrue(errors(pack, dictionary).contains("a dictionary row does not clear $claim"))
            val book = probe(unitNumber, objectiveId, claim, "Self-attest only after the Lesson work in your copy.")
            assertTrue(errors(pack, book).contains("book self-attestation does not clear $claim"))
            val wrongObjective = probe(unitNumber, "obj-u1-alphabet", claim, "wrong objective")
            assertTrue(errors(pack, wrongObjective).contains("wrong unit or objective"))
            val wrongUnit = probe(if (unitNumber == 7) 8 else 7, objectiveId, claim, "wrong unit")
            assertTrue(errors(pack, wrongUnit).contains("wrong unit or objective"))
            val wrongClaim = probe(unitNumber, objectiveId, "register", "wrong claim")
            assertTrue(errors(pack, wrongClaim).contains("wrong unit or objective"))
        }

        val gloss = pack.sourceCapabilities.single { it.id == "cap-u7-adverb-form-gloss" }
        assertTrue(errors(pack, gloss.copy(id = "cap-e-rev", sourceRevision = "1")).contains("revision does not match the frozen record"))
        assertTrue(errors(pack, gloss.copy(id = "cap-e-record", sourceRecordId = "other-record")).contains("revision does not match the frozen record"))
        assertTrue(errors(pack, gloss.copy(id = "cap-e-source", sourceId = "src-ruwiki-batch-e-117812844")).contains("revision does not match the frozen record"))
        assertTrue(errors(pack, gloss.copy(id = "cap-e-hash", sourceHash = "0".repeat(64))).contains("revision does not match the frozen record"))
        assertTrue(errors(pack, gloss.copy(id = "cap-e-span", exactSpan = "not the frozen span")).contains("span is not in the frozen record"))
        assertTrue(errors(pack, gloss.copy(id = "cap-e-unit", unitNumber = 17, objectiveId = "obj-u17-clock-answers", supportedClaim = "productive-clock-time")).contains("revision does not match the frozen record") ||
            errors(pack, gloss.copy(id = "cap-e-unit", unitNumber = 17, objectiveId = "obj-u17-clock-answers", supportedClaim = "productive-clock-time")).contains("wrong unit or objective") ||
            errors(pack, gloss.copy(id = "cap-e-unit", unitNumber = 17, objectiveId = "obj-u17-clock-answers", supportedClaim = "productive-clock-time")).contains("does not clear"))
        val relabeledGloss = gloss.copy(id = "cap-e-relabel-adverb", supportedClaim = "adverb-paradigm")
        val glossErrors = planted(pack, relabeledGloss)
        assertTrue(glossErrors, glossErrors.contains("does not clear adverb-paradigm"))
        assertFalse(V2Validator.unresolvedRequiredSourceCapabilities(
            pack.copy(sourceCapabilities = pack.sourceCapabilities + relabeledGloss),
            7,
        ).any { it.claim == "adverb-paradigm" || it.claim == "bounded-manner-adverb-formation" })

        val quarter = pack.sourceCapabilities.single { it.supportedClaim == "productive-quarter-hour-time" }
        val relabeledQuarter = quarter.copy(
            id = "cap-e-relabel-clock",
            objectiveId = "obj-u17-clock-answers",
            supportedClaim = "productive-clock-time",
        )
        assertTrue(planted(pack, relabeledQuarter).contains("quarter-hour patterns do not clear productive-clock-time"))
        val duration = pack.sourceCapabilities.single { it.supportedClaim == "elapsed-time-duration" }
        val relabeledDuration = duration.copy(
            id = "cap-e-relabel-completion",
            objectiveId = "obj-u23-completion",
            supportedClaim = "completion-over-period",
        )
        assertTrue(planted(pack, relabeledDuration).contains("duration evidence does not clear completion-over-period"))
        val alternation = pack.sourceCapabilities.single { it.supportedClaim == "negation-alternation" }
        val relabeledAlternation = alternation.copy(
            id = "cap-e-relabel-scope",
            objectiveId = "obj-u24-placement",
            supportedClaim = "negation-scope",
        )
        assertTrue(planted(pack, relabeledAlternation).contains("does not clear negation-scope"))
        val classification = pack.sourceCapabilities.single { it.supportedClaim == "diminutive-form-classification" }
        val relabeledClassification = classification.copy(id = "cap-e-relabel-diminutive", supportedClaim = "productive-diminutive")
        val diminutiveErrors = errors(pack, relabeledClassification)
        assertTrue(diminutiveErrors, diminutiveErrors.contains("bounded diminutive recognition does not clear productive-diminutive"))
        assertTrue(diminutiveErrors, diminutiveErrors.contains("no independent application check for productive-diminutive"))

        val stamped = pack.copy(
            provenanceRecords = pack.provenanceRecords.map { it.copy(reviewStatus = "APPROVED") },
            admissions = pack.admissions.copy(penguinProseAssets = 9, generatedRussianWords = 4),
        )
        claims.forEach { (unitNumber, _, claim) ->
            assertFalse(
                V2Validator.unresolvedRequiredSourceCapabilities(stamped, unitNumber).any { it.claim == claim },
            )
        }
    }

    @Test
    fun missingChainAndReadinessFlipKeepAdvancementBlocked() {
        val pack = loadCurriculumPack(root())
        val gaps = mapOf(
            7 to "without an admitted bounded manner-adverb formation source capability",
            17 to "Unit 17 is missing productive clock-time and calendar-date source capabilities",
            23 to "completion-over-period",
            24 to "scope source capabilities",
            25 to "without admitted documented-name-form and register source capabilities",
        )
        val blockers = mapOf(
            7 to "bounded manner-adverb formation",
            17 to "productive clock-time",
            23 to "completion-over-period",
            24 to "negation-scope",
            25 to "documented-name-form",
        )
        val closed = setOf(7, 17, 23, 24, 25)
        gaps.forEach { (unitNumber, gap) ->
            val unit = pack.unit(unitNumber)!!
            assertEquals("DEVELOPMENT", unit.installState)
            val learner = prepared(unit)
            if (unitNumber in closed) {
                assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", unit.readiness)
                val advanced = advancementDecision(pack, unit, learner)
                assertTrue(advanced.remaining.joinToString("\n"), advanced.allowed)
            } else {
                assertEquals("NOT_PREPARED", unit.readiness)
                val blocked = advancementDecision(pack, unit, learner)
                assertFalse(blocked.allowed)
                assertTrue(blocked.remaining.any { it.contains("not prepared") })
                assertTrue(blocked.remaining.any { it.contains(blockers.getValue(unitNumber)) })
            }

            val stripped = pack.copy(
                units = pack.units.map { current ->
                    if (current.number != unitNumber) {
                        current
                    } else {
                        current.copy(
                            readiness = "CONTENT_COMPLETE_REVIEW_PENDING",
                            objectives = current.objectives.map { objective ->
                                objective.copy(
                                    instructionStepIds = emptyList(),
                                    exampleIds = emptyList(),
                                    practiceStepIds = emptyList(),
                                    checkItemIds = emptyList(),
                                    remediationStepIds = emptyList(),
                                )
                            },
                        )
                    }
                },
            )
            val flippedErrors = V2Validator.validate(stripped).errors.joinToString("\n")
            assertTrue(flippedErrors, flippedErrors.contains(gap))
            assertFalse(flippedErrors, flippedErrors.contains("telling-time, weekday, date, and age"))
            assertTrue(flippedErrors, flippedErrors.contains("missing instruction, examples, guided practice, independent checks, or remediation"))
            val decision = advancementDecision(stripped, stripped.unit(unitNumber)!!, learner)
            assertFalse(decision.allowed)
            assertTrue(decision.remaining.joinToString("\n"), decision.remaining.any { it.contains(blockers.getValue(unitNumber)) })
        }
    }

    @Test
    fun unit17MessagesFollowTheUnresolvedCapabilitySet() {
        val pack = loadCurriculumPack(root())
        val clockOnly = "Unit 17 is missing a productive clock-time source capability"
        val dateOnly = "Unit 17 is missing a calendar-date source capability"
        val both = "Unit 17 is missing productive clock-time and calendar-date source capabilities"
        assertEquals(listOf(clockOnly), V2Validator.unit17SourceBlockers(setOf("productive-clock-time")))
        assertEquals(listOf(dateOnly), V2Validator.unit17SourceBlockers(setOf("calendar-date")))
        assertEquals(
            listOf(both),
            V2Validator.unit17SourceBlockers(setOf("productive-clock-time", "calendar-date")),
        )
        assertEquals(emptyList<String>(), V2Validator.unit17SourceBlockers(emptySet()))
        assertFalse(V2Validator.unit17SourceBlockers(setOf("productive-clock-time")).any { it.contains("calendar-date") })
        assertFalse(V2Validator.unit17SourceBlockers(setOf("calendar-date")).any { it.contains("clock-time") })
        assertFalse(V2Validator.unit17SourceBlockers(setOf("productive-clock-time")).any { it.contains("weekday") || it.contains("age") })

        val liveClaims = V2Validator.unresolvedRequiredSourceCapabilities(pack, 17).map { it.claim }.toSet()
        assertEquals(emptySet<String>(), liveClaims)
        val liveUnit = pack.unit(17)!!
        val liveErrors = V2Validator.validate(pack).errors
        assertEquals(emptyList<String>(), liveErrors.filter { it.startsWith("Unit 17 is missing") })
        assertFalse(liveErrors.joinToString("\n"), liveErrors.any { it.contains("telling-time") || it.contains("weekday") || it.contains("age sources") })
        val liveDecision = advancementDecision(pack, liveUnit, prepared(liveUnit))
        assertTrue(liveDecision.remaining.joinToString("\n"), liveDecision.allowed)
        assertFalse(liveDecision.remaining.any { it.contains("calendar-date") || it.contains("weekday") || it.contains("age") || it.contains("clock-time") })

        val withoutClock = pack.copy(
            sourceCapabilities = pack.sourceCapabilities.filter { it.supportedClaim != "productive-clock-time" },
        )
        val clockErrors = V2Validator.validate(withoutClock).errors
        assertEquals(listOf(clockOnly), clockErrors.filter { it.startsWith("Unit 17 is missing") })
        val withoutDate = pack.copy(
            sourceCapabilities = pack.sourceCapabilities.filter { it.supportedClaim != "calendar-date" },
        )
        val dateErrors = V2Validator.validate(withoutDate).errors
        assertEquals(listOf(dateOnly), dateErrors.filter { it.startsWith("Unit 17 is missing") })
        val withoutBoth = pack.copy(
            sourceCapabilities = pack.sourceCapabilities.filter {
                it.supportedClaim != "productive-clock-time" && it.supportedClaim != "calendar-date"
            },
        )
        val bothErrors = V2Validator.validate(withoutBoth).errors
        assertEquals(listOf(both), bothErrors.filter { it.startsWith("Unit 17 is missing") })

        listOf(
            "clock-expression-recognition",
            "clock-question",
            "age",
            "weekdays",
            "calendar-date",
            "productive-quarter-hour-time",
        ).forEach { claim ->
            val source = pack.sourceCapabilities.single { it.unitNumber == 17 && it.supportedClaim == claim }
            val relabeled = source.copy(
                id = "cap-e-relabel-$claim",
                objectiveId = "obj-u17-clock-answers",
                supportedClaim = "productive-clock-time",
            )
            val errors = planted(pack, relabeled)
            assertTrue(errors, errors.contains("no independent productive-clock-time admission"))
            if (claim == "clock-expression-recognition") {
                assertTrue(errors, errors.contains("bounded clock phrases do not clear productive-clock-time"))
            }
            if (claim == "productive-quarter-hour-time") {
                assertTrue(errors, errors.contains("quarter-hour patterns do not clear productive-clock-time"))
            }
            val hosted = pack.copy(sourceCapabilities = pack.sourceCapabilities + relabeled)
            assertFalse(
                V2Validator.unresolvedRequiredSourceCapabilities(hosted, 17).any { it.claim == "productive-clock-time" },
            )
        }
    }

    @Test
    fun digestsGovernanceAndLaterUnitsStayUnchanged() {
        val root = root()
        val pack = loadCurriculumPack(root)
        val model = reconcileCurriculumReadiness(root, pack)
        val candidate = readAcceptanceCandidate(root)
        assertEquals("dc56345a9309e7dc8f5fe5e159f3ec935958d64b83443d4a9f17d6c653911a2f", model.packContentReviewDigest)
        assertEquals("b2f3fa07b4a592c2865e12fe35fa881125aac237a344793aac86cd1c877933f2", model.repositorySourceDigest)
        assertEquals(model.repositorySourceDigest, computeReleaseSourceDigest(root))
        assertEquals("NOT_APPROVED", model.publicRelease)
        assertEquals("NOT_APPROVED", candidate?.publicRelease)
        assertEquals(false, candidate?.frozen)
        assertEquals(null, candidate?.sourceCommit)
        assertEquals("NOT_RECORDED", candidate?.artifact?.status)
        assertEquals("UNEXECUTED", model.at031)
        assertEquals("PROPOSED_NOT_ACTIVATED", model.varianceProposalStatus)
        assertTrue(model.units.all { it.installState == "DEVELOPMENT" })
        assertTrue(model.units.all { it.unitReviewStatus == "IMPLEMENTED_PENDING_FINAL_AUDIT" })
        assertTrue(model.units.all { it.mappingStatus == "PENDING_CURRICULUM_REVIEWER" })
        assertTrue(model.units.all { it.reviewerIdentity == "ABSENT" && it.signedReviewedDigest == "ABSENT" })
        listOf(27, 29, 30).forEach { number ->
            assertEquals("ACTIVATED", model.units.single { it.unitNumber == number }.authorityVarianceBlocker)
            assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", model.units.single { it.unitNumber == number }.readiness)
        }
        val content = mapOf(
            7 to "ec283b3bc0e58306d84e536947ab747d5ea264cea729bf4838124404ca958359",
            17 to "a3d3b89b7359bac0dc1ba33483c819e31f22f69c0344be860dbac286637a404c",
            23 to "c46606f18f1b6c044a48934370c8ff82e32a4fe900164a779d846d7937855151",
            24 to "497c9978cd3a89159732f7c4b53a09d0923d98bbaa01f61722a769d1a808f74c",
            25 to "4f7acdd8f55829490434d73308921fb4d63da86f6792ec65623720c2dfc89957",
            26 to "042693573ae94ddc31e1c68c625d854f8495934fbf1d39cd800684afcb078b06",
            27 to "b43404505a7bc8bc72a34d2af039d5695d1e919cdc309643ecc7c835cfd99db0",
            28 to "afa29cbd6d710bcb853d74189d012584660996b25270af78c3d23efe35cf1cc5",
            29 to "bb8645fa3be7f3ac9b7cc7a7d61d551843975f8deccb419a3ad529416c3fb0e3",
            30 to "ed3a589a8d91e48c49675840118ca2951ad752f9784b0c8eeb21a74f66285187",
        )
        val mapping = mapOf(
            7 to "291453d0e1e635835556daff70c3d2c944c2434084f3986174d666f7c686a135",
            17 to "88fece04e7b8a1a707237548f2cc8f62f066bdf9392e8263c92c9d57518e24a2",
            23 to "5c5561eacd86686a73ffd88d81fc083ae208ada14e3a7b9c3f43749bfdefe254",
            24 to "b8db8481ea62d3f88768f74385ed7411fe70559bbcab4c37a8c1f14792d20f87",
            25 to "5fffe6bca512b1a1e7fa9470fb8566e736aeecfabc363cadb0d94a0cbdd7fce3",
            26 to "9f46e2d70aeefde9067f0868c5099ea7df08c9cb5f924e858094d817d91a83fa",
            27 to "a15bf8dc76fa2ab5d7bc1448a9f2129da7ee281461338e280c503ee3a5150bdb",
            28 to "44452909b63f4e4b67632c3c24e9f997efa75bdf314d8b4fbb4e0933f29278ff",
            29 to "a40de565a8d86d7288e31bfaeb75d9c41761b810f99ee56afa1add74df334f21",
            30 to "5361634a4e364130af45cfe253e75c60882f293cd9079bd403373e12cfde6505",
        )
        val reviewPackages = mapOf(
            7 to "a132bfded20b47d1b57181455f33061fae72b4111180bd5476b6e9fd3daf87ca",
            17 to "82f356856af7a78a2866c2ecafb70072448df274e6b60e595221565fb856d947",
            23 to "b19da1257d74f83b59e36f98e5b618718ab13f9c325690af2024f8bce53e413b",
            24 to "8317cd5d2e8af21340436434e926083d0b9c0661d2ac4178e5e44a05032fb1cd",
            25 to "58b25263b6afc7e4ce51db941ea9f2f78702b9434138a5d69750b438c05bed06",
            26 to "c00427fa210a835655b729de6574d285b9be804c29f18f6ff77f0f21adc90d82",
            27 to "3a68a30ed98a92fccec81208ebc4cb3f8f15ecd478a06893693637fff62c0303",
            28 to "8a9a200da55420a44cc1368ad057a4698fdfcdcf0895277902d0f8a0f0547b51",
            29 to "cd7eed324e12a8c25f4a38d69290c4aa7ec6d7e65919adac59576b348ee62619",
            30 to "02a4ddb5c659428962eed21d738bfdccb58d70ad692746d65b5de4fc482cef18",
        )
        (content.keys).forEach { number ->
            val row = model.units.single { it.unitNumber == number }
            assertEquals(content.getValue(number), row.unitContentReviewDigest)
            assertEquals(mapping.getValue(number), row.mappingPackageDigest)
            assertEquals(reviewPackages.getValue(number), row.reviewPackageDigest)
        }
        val candidateText = File(root, "release/acceptance/candidate.json").readText()
        assertTrue(candidateText.contains("0bd4dbf5a24738b9ff9bb7e782a1370021ea59b065c9e764c556f16c1e0784b4"))
        assertTrue(candidateText.contains("7a24899055474d1d7913213b0af1dedade528d4015cd1faef35c0f71d4e8c37e"))
        assertEquals(
            "NONE",
            model.units.single { it.unitNumber == 26 }.unresolvedRequiredSourceCapabilities,
        )
        assertEquals(
            "NONE",
            model.units.single { it.unitNumber == 29 }.unresolvedRequiredSourceCapabilities,
        )
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", model.units.single { it.unitNumber == 26 }.readiness)
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", model.units.single { it.unitNumber == 29 }.readiness)
        assertEquals("ACTIVATED", model.units.single { it.unitNumber == 29 }.authorityVarianceBlocker)
    }

    private fun probe(unitNumber: Int, objectiveId: String, claim: String, span: String) = V2SourceCapability(
        id = "cap-e-$unitNumber-$claim-${span.length}",
        sourceId = "src-batch-e-probe",
        sourceRevision = "0",
        sourceRecordId = "probe",
        sourceHash = "ab".repeat(32),
        exactSpan = span,
        unitNumber = unitNumber,
        objectiveId = objectiveId,
        supportedClaim = claim,
    )

    private fun errors(pack: dev.russiancompanion.v2.curriculum.V2CurriculumPack, capability: V2SourceCapability): String {
        return V2Validator.validate(
            pack.copy(sourceCapabilities = pack.sourceCapabilities + capability),
        ).errors.joinToString("\n")
    }

    private fun planted(pack: dev.russiancompanion.v2.curriculum.V2CurriculumPack, capability: V2SourceCapability): String {
        val matched = pack.provenanceRecords.filter {
            it.sourceId == capability.sourceId &&
                it.sourceRevision == capability.sourceRevision &&
                it.sourceRecordId == capability.sourceRecordId &&
                it.sourceHash == capability.sourceHash &&
                it.originalSourceText == capability.exactSpan
        }
        val hosted = if (matched.isNotEmpty()) {
            pack.copy(
                provenanceRecords = pack.provenanceRecords.map { record ->
                    if (record in matched) {
                        record.copy(unitNumber = capability.unitNumber, objectiveId = capability.objectiveId)
                    } else {
                        record
                    }
                },
            )
        } else {
            val template = pack.provenanceRecords.first { it.unitNumber == capability.unitNumber }
            val record = template.copy(
                itemId = "prov-${capability.id}",
                unitNumber = capability.unitNumber,
                objectiveId = capability.objectiveId,
                originalSourceText = capability.exactSpan,
                shippedText = capability.exactSpan.lineSequence().first { it.any(Char::isLetter) }.take(48),
                sourceId = capability.sourceId,
                sourceRevision = capability.sourceRevision,
                sourceRecordId = capability.sourceRecordId,
                sourceHash = sha256(capability.exactSpan.toByteArray(Charsets.UTF_8)),
            )
            pack.copy(provenanceRecords = pack.provenanceRecords + record)
        }
        val bound = if (matched.isNotEmpty()) {
            capability
        } else {
            capability.copy(sourceHash = sha256(capability.exactSpan.toByteArray(Charsets.UTF_8)))
        }
        return errors(hosted, bound)
    }

    private fun prepared(unit: dev.russiancompanion.v2.curriculum.V2Unit) = V2LearnerView(
        onboardingCompleted = true,
        currentUnitNumber = unit.number,
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
        correctItemIds = (
            unit.objectives.flatMap { it.checkItemIds } +
                unit.sessions.flatMap { it.steps }.flatMap { it.itemIds }
            ).toSet(),
        bookAttestedUnits = setOf(unit.number),
        completedUnitNumbers = emptySet(),
    )

    private fun sha256(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

    private fun root(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }

    private companion object {
        const val MANIFEST = "audit/curriculum-readiness/batch-e-research/MANIFEST.json"
        const val DOSSIER = "audit/curriculum-readiness/V2_CURRICULUM_GAP_CLOSURE_DOSSIER_v1.0.md"
        val REQUIRED = mapOf(
            "adverb-paradigm" to "obj-u7-adverb",
            "productive-clock-time" to "obj-u17-clock-answers",
            "completion-over-period" to "obj-u23-completion",
            "negation-scope" to "obj-u24-placement",
            "productive-diminutive" to "obj-u25-nickname-rule",
        )
    }
}
