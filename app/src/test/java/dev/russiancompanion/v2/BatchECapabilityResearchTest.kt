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
        assertEquals("ecae24c4e29c7247517a26716213878e43c8cb4de414f5cbc93c6b0f5ca85a0a", model.packContentReviewDigest)
        assertEquals(candidate?.sourceDigest, model.repositorySourceDigest)
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
            7 to "0a4df65c918a87854c5088faf14ad2f6b9338d0b395452429ce6fd2a4caa1a6d",
            17 to "6172c2a9be50c4b3c79b962dc5f9b055f8d2d267d3a6a8627a3e9805e4fc8cca",
            23 to "42a67675479cb835e6c429ffc76c0120c00ea389baed9e7e0cbc91f593b564e4",
            24 to "6e7a66f3c4f2f4457aa772a64e931a228933d6516cdd96986760692581d8cbc6",
            25 to "317881a1a5a77489ce03b939b567642725274c10c534e14be68007b199cde98d",
            26 to "7870405b2810239e5e744706b0376877191d00afc337ac30e7a2249ad97fddab",
            27 to "fb8a23246226a841df660cae2517b139cba2ff51ef5939792268d2c6cb50bc2e",
            28 to "d8df059f618e932eb0e77579113fea17db317584e77bef5a10dda3e82f70d402",
            29 to "b35528e0a8ca27fed0fe18541aab59755d6adcad90a7ccaae86c1f8318ab9374",
            30 to "192db1dc6049b708d930fa05879089db636eb2d2cfbb3724bf72c8732dc1114e",
        )
        val mapping = mapOf(
            7 to "e39515585eea5b131797f0c93ce3508ee99b27206cc21070675d06aa975373c0",
            17 to "724a3220ec28b620687871fbca1639cc8e64b58fed7fcfe8ba1a29bdf39d6328",
            23 to "968d763b963eb028d821bb426f6c906c079a6266184e4f89d79d21fa34699e95",
            24 to "dc6f9ac41a8520dc647df90313e05e547245bfa31fe3b2e883821f3591a24248",
            25 to "239e966c5d5b4f00ea70616e8ec949d583bb9406407438ab7f04df9fc363bf31",
            26 to "c15db22cc82dcfa6c49d6d0e50beea0f90b16dfc4db90777623e99d540790489",
            27 to "3924f3a8ac211579b4db3e387cb48632d27e9b41925e227b63b004bf652a3bc0",
            28 to "0fb9199bfef05acc54935be340710bd53e19fcd6066349300e09c29fb4b035e8",
            29 to "a7ebe370698a0dffe4f0311ccfbfff6f217171ffab7dda232447d4b62e2b4071",
            30 to "97f4c8beeb1d0e6827b94bedc1e379381c763c2c3522013890b75802630440a2",
        )
        val reviewPackages = mapOf(
            7 to "444e13e9aa62b4b120928c6f1381898565a9ff47cc2a8bf1c0edc6fc2a001a65",
            17 to "5f6ba37d5f872c8afc895c5b402d142240977157dff5f7b27c38a7104fbab6c3",
            23 to "3132b83999674df89783745ad1ec1b9b15709c9659062b0cccf858f50eae438e",
            24 to "1513078e0309750e56a94be06f9da994060afb7ddd1cea9159fe758a1c68d6f5",
            25 to "d09669e2175455dba996fed8df9d84a04651b3d09477a7834dd765a9b31fae59",
            26 to "72034166355fc1ba567a4caa64db7c908d9c523ed7e2f3d618e45e9653a14e99",
            27 to "e1607fe23cb8f2e8bbc35c9a76a49b3895992b109075052c61b8daeab4e0e941",
            28 to "a4132204cbd12285cf157a29f2fb6bd28d03404145ae1fe830de9b93953236ad",
            29 to "73eeb58d502dc27b52246f6c92bc52b4e8847ccce1c429806d28a18701d07639",
            30 to "8979f46b5b63705acfe0ec2669607abcdf9fe05ba9382fc6d9c7f81c9d1d3967",
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
