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
        assertEquals("2115dd2ce4e2db46bca1bd3066147eb26c850c3f07ce1bdbde4ea453e432cbdc", model.packContentReviewDigest)
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
            7 to "f6a19dea12eae270c0af0760d148d34d8bbcc328fd3940cc91f9e9257f1d80fd",
            17 to "9723c8b390ccfa95f468a337f8926133acbc155623fa8523c56e0bda2e619a6b",
            23 to "99c0eacf02011ffb88c0d7e35900d446492bff20e38c126e8745a8a8f7f066fb",
            24 to "e2168f15e2fff92bb38f46c1feb687b44553885c3242dc98cc1434ed8b0e7a5f",
            25 to "58e8d7f696898fb710e64b5e82b3b5f8b79d8fcda57c1942d6a385dfb197463c",
            26 to "64812803d45e4bad57dca03ad3622e809001a8a84435a218a85c0be58bdc642c",
            27 to "7f9490a90ed32877a1a8f67fd081dbadc6ee2b7a78eda523aac4764248530d0e",
            28 to "c44ee224d88dae6b726748411cf7de876f841d7bcd55e92ce84f6c3022ef5b68",
            29 to "25dd10b1454f8b4fd77ed15a19ff8fa44e2a82115e5cb658d29a3a1b708a8575",
            30 to "2aa34719cf7930c67935e92c75becc40fa30c20ece6555a2ce1bd556be493688",
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
            7 to "a686c64ae1c0a34dfa261ba4bc53a3365b641453da92b633448fb541f03567e5",
            17 to "dd168001e7b724d849d1f23728719d71780a46094d87cbbff991f5bb9c122976",
            23 to "ea2fadf9c9f2c9ad28a84ed6ca7be7157c11ae67a1fc3612a43efee595f2b878",
            24 to "ad6912971ef9aef44aa9530b6b3a29df3bd7fa975d2b04e0801ee124ceab0eef",
            25 to "f601e7a5d98bcb07077409c6042d8cbd57bab6498aef9ed83b5692553f6d05ca",
            26 to "cd6ff3e3cb6de631d9baf9ab0baf98ad6dd0b73ce9f67c3106e131e66c67baf1",
            27 to "dd92536ade3daf2ab97f402acfcb570b609a2dca77eec997f2e4bed5695018c7",
            28 to "50d58fdad08d60d87f1184527ea8b0ac5e2bcd18e5d54f8bd63a007a57afc76c",
            29 to "31686a036e0c2607a91913d06898e58780c22069632eedb9c011e1648c837efc",
            30 to "3972f0a3c3b9935e33649a650be857c336bd7ceafb4aa772ec6be4aede2528c5",
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
