package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2ProvenanceRecord
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.advancementDecision
import dev.russiancompanion.v2.curriculum.unit
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.review.governedMappingFacts
import dev.russiancompanion.v2.review.renderUnitReviewFiles
import dev.russiancompanion.v2.review.reviewPacketName
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.security.MessageDigest

class Units1620SourceIntakeTest {
    private val pages = mapOf(
        16 to "188-200",
        17 to "201-214",
        18 to "215-229",
        19 to "230-239",
        20 to "240-256",
    )

    private val preservedContentDigests = mapOf(
        11 to "2a0b74e3722f0f0a1c2e0041c73c6654211eee81c918e44c1b23929fcf24672b",
        12 to "5d36c3dea6b2217a568c338cf1a8d2d05a67132d1b21c9652cd1356ad34458f5",
        13 to "a83baf7bb4a5839fb36bb8e7c335a7b6e185718397626d58fa01ace4359fe35d",
        14 to "2f9bbf502b90f2a8c0ef0c8f1fb32b8b5e564290aa16c77e1603f0fa8bbecd39",
        15 to "80225df0cc95cc791d6cc062121b3d1988b66fa4d7c11cf7b234ac38d227b6dc",
    )

    @Test
    fun structuralMappingsMatchTheOwnedEditionObservations() {
        val pack = pack()
        assertTrue(V2Validator.validate(pack).errors.joinToString("\n"), V2Validator.validate(pack).ok)
        pages.forEach { (number, range) ->
            val unit = pack.unit(number)!!
            val facts = governedMappingFacts(number)
            assertEquals("DEVELOPMENT", unit.installState)
            val expectedReadiness = "CONTENT_COMPLETE_REVIEW_PENDING"
            assertEquals(expectedReadiness, unit.readiness)
            assertEquals("PENDING_CURRICULUM_REVIEWER", unit.mappingStatus)
            assertEquals(number, unit.penguinLesson)
            assertTrue(unit.mappingNote.contains(range))
            assertTrue(unit.mappingNote.contains("PENDING_CURRICULUM_REVIEWER"))
            assertFalse(unit.mappingNote.contains("APPROVED"))
            assertEquals(range, "${facts.printedPageStart}-${facts.printedPageEnd}")
            assertEquals(number, facts.proposedLessonNumber)
            assertEquals("PENDING_CURRICULUM_REVIEWER", facts.mappingDisposition)
            assertEquals("UNRESOLVED", facts.isbnObservation)
            assertNull(facts.isbn)
            assertTrue(pack.ownedEditionMappings.filter { it.unitNumber == number }.all { it.status == "PENDING_CURRICULUM_REVIEWER" && it.reviewer.isNullOrBlank() && it.reviewedContentDigest.isNullOrBlank() })
            assertEquals(1, pack.ownedEditionMappings.count { it.unitNumber == number })
            assertEquals("IMPLEMENTED_PENDING_FINAL_AUDIT", pack.unitReviews.first { it.unitNumber == number }.status)
            assertTrue(pack.unitReviews.first { it.unitNumber == number }.reviewer.isNullOrBlank())
        }
        assertTrue(pack.units.drop(25).all { it.installState == "DEVELOPMENT" && it.sessions.isNotEmpty() })
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(28)!!.readiness)
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(26)!!.readiness)
        assertEquals(emptySet<Int>(), pack.units.filter { it.readiness == "NOT_PREPARED" }.map { it.number }.toSet())
        preservedContentDigests.forEach { (number, digest) ->
            assertEquals("unit $number", digest, V2Validator.unitContentReviewDigest(pack, number))
        }
    }

    @Test
    fun frozenSourceBytesAndRevisionsStayImmutable() {
        val root = repo()
        val freeze = Json.parseToJsonElement(File(root, "catalog/sources/units-16-20/FREEZE.json").readText()).jsonObject
        val openRussian = freeze.getValue("openrussian").jsonObject
        assertEquals("50e210c4803237779cb562bc1abcea529066031c", openRussian.getValue("commit").jsonPrimitive.content)
        assertEquals("CC BY-SA 4.0", openRussian.getValue("licenseId").jsonPrimitive.content)
        val pages = freeze.getValue("wikibooks").jsonObject.getValue("pages").jsonArray
        val expected = mapOf(
            "instrumental" to "4219602",
            "adjectives" to "4292379",
            "numbers" to "4371358",
            "aspect" to "4059032",
            "copyrights" to "4622060",
            "grammar" to "1369617627",
            "days" to "3594124",
            "months" to "4376719",
            "interrogative" to "4220316",
            "lesson3" to "4460699",
            "clock-country" to "1366690592",
            "date-russia" to "1357134172",
        )
        val seen = mutableSetOf<String>()
        pages.forEach { element ->
            val page = element.jsonObject
            val key = page.getValue("key").jsonPrimitive.content
            seen += key
            assertEquals(expected.getValue(key), page.getValue("oldid").jsonPrimitive.content)
            val bytes = frozenFile(root, page.getValue("file").jsonPrimitive.content).readBytes()
            assertEquals(page.getValue("sha256").jsonPrimitive.content, sha256(bytes))
            val revision = Json.parseToJsonElement(frozenFile(root, page.getValue("revisionFile").jsonPrimitive.content).readText()).jsonObject
            assertEquals(page.getValue("oldid").jsonPrimitive.content, revision.getValue("revid").jsonPrimitive.content)
            assertEquals(page.getValue("mediawikiSha1").jsonPrimitive.content, revision.getValue("sha1").jsonPrimitive.content)
            assertEquals(page.getValue("size").jsonPrimitive.content, bytes.size.toString())
        }
        assertEquals(expected.keys, seen)
        val excluded = freeze.getValue("decisions").jsonArray.map { it.jsonObject }
            .filter { it.getValue("disposition").jsonPrimitive.content == "EXCLUDED" }
            .map { it.getValue("id").jsonPrimitive.content }
            .toSet()
        assertTrue(excluded.contains("wikibooks-verbs-of-motion-red-link"))
        assertTrue(excluded.contains("wikibooks-verbs-4233876"))
        assertTrue(excluded.contains("penguin-private-pdf"))
    }

    @Test
    fun everyRussianSpanOccursInItsRecordedSource() {
        val pack = pack()
        val root = repo()
        val freeze = Json.parseToJsonElement(File(root, "catalog/sources/units-16-20/FREEZE.json").readText()).jsonObject
        val wikibooksPages = freeze.getValue("wikibooks").jsonObject.getValue("pages").jsonArray
        val supplementalPages = freeze.getValue("supplemental").jsonArray
        val byOldid = (wikibooksPages + supplementalPages).associate { element ->
            val page = element.jsonObject
            page.getValue("oldid").jsonPrimitive.content to frozenFile(root, page.getValue("file").jsonPrimitive.content).readText()
        }
        pack.provenanceRecords.filter { it.unitNumber in 16..20 }.forEach { record ->
            assertEquals(sha256(record.originalSourceText), record.sourceHash)
            val visible = when (record.transformation) {
                "pressbooks markup selection" -> dev.russiancompanion.v2.curriculum.BatchARemainingSources.htmlVisible(record.originalSourceText)
                "wiktionary template selection" -> dev.russiancompanion.v2.curriculum.BatchARemainingSources.unfoldWiktionary(record.originalSourceText)
                else -> dev.russiancompanion.v2.curriculum.V2Validator.unfoldWikiLabels(record.originalSourceText)
            }
            assertTrue(
                record.itemId,
                normalized(record.originalSourceText).contains(normalized(record.shippedText)) ||
                    normalized(visible).contains(normalized(record.shippedText)),
            )
            val page = byOldid[record.sourceRevision]
            if (page != null) {
                if (record.transformation == "readyrussian text extraction") {
                    assertFalse(record.itemId, record.originalSourceText.contains("<"))
                    assertFalse(record.itemId, record.originalSourceText.contains(">"))
                    assertFalse(record.itemId, readyRussianMedia.containsMatchIn(record.originalSourceText))
                    val visible = dev.russiancompanion.v2.curriculum.BatchARemainingSources.htmlVisible(page)
                    record.originalSourceText.split("\n\n").filter { it.isNotBlank() }.forEach { piece ->
                        assertTrue(
                            "${record.itemId} extracted piece is not visible page text",
                            visible.contains(piece),
                        )
                    }
                } else {
                    assertTrue(record.itemId, page.contains(record.originalSourceText))
                }
            } else if (record.sourceId == "src-content-audit-research") {
                assertContentAuditResearch(record)
            } else {
                assertTrue(record.sourceRevision.startsWith("50e210c4803237779cb562bc1abcea529066031c"))
            }
            assertFalse(record.originalSourceText.contains("The New Penguin Russian Course"))
        }
        assertEquals(0, pack.admissions.generatedRussianWords)
        assertEquals(0, pack.admissions.penguinProseAssets)
        assertEquals(0, pack.audioAssets.size)
        assertTrue(pack.provenanceRecords.any { it.unitNumber in setOf(18, 20) && it.shippedText.any { char -> char.code in 0x0400..0x04FF } })
    }

    @Test
    fun unsupportedSubtopicsStayUnavailableAndUnitsCannotAdvance() {
        val pack = pack()
        assertFalse(reason(pack, 16).contains("becoming complements"))
        assertFalse(reason(pack, 17).contains("Productive clock-time"))
        assertTrue(pack.references.first { it.id == "ref-u17-attribution" }.detail.contains("ReadyRussian Grammar 20"))
        assertFalse(reason(pack, 17).contains("Calendar-date"))
        assertFalse(reason(pack, 18).contains("Comparison forms"))
        assertFalse(reason(pack, 19).contains("Probability"))
        assertFalse(reason(pack, 20).contains("red link"))
        listOf(17).forEach { number ->
            val unit = pack.unit(number)!!
            val decision = advancementDecision(
                pack,
                unit,
                learner(
                    number,
                    book = true,
                    correct = (unit.objectives.flatMap { it.checkItemIds } + unit.sessions.flatMap { it.steps }.flatMap { it.itemIds }).toSet(),
                    steps = unit.sessions.flatMap { it.steps }.map { it.id }.toSet(),
                ),
            )
            assertTrue(decision.remaining.joinToString("\n"), decision.allowed)
            assertFalse(decision.remaining.any { it.contains("not prepared") })
        }
        listOf(16, 18, 19, 20).forEach { number ->
            val unit = pack.unit(number)!!
            val decision = advancementDecision(
                pack,
                unit,
                learner(
                    number,
                    book = true,
                    correct = (unit.objectives.flatMap { it.checkItemIds } + unit.sessions.flatMap { it.steps }.flatMap { it.itemIds }).toSet(),
                    steps = unit.sessions.flatMap { it.steps }.map { it.id }.toSet(),
                ),
            )
            assertTrue(decision.remaining.joinToString("\n"), decision.allowed)
        }
    }

    @Test
    fun unitEighteenCannotBecomeContentCompleteWithoutComparisonAndRelativeSources() {
        val pack = pack()
        assertTrue(
            V2Validator.validate(claimComplete(pack, 18)).errors.joinToString("\n"),
            V2Validator.validate(claimComplete(pack, 18)).ok,
        )
        val stripped = withoutCapabilities(pack, 18)
        val errors = V2Validator.validate(claimComplete(stripped, 18)).errors
        assertTrue(errors.joinToString("\n"), errors.any { it.contains("without comparison and relative-clause sources") })
        val adjectiveOnly = stripped.copy(
            provenanceRecords = pack.provenanceRecords + pack.provenanceRecords.first { it.sourceRevision == "4292379" }.copy(
                unitNumber = 18,
                originalSourceText = "comparative лу'чше and superlative который are inferred",
            ),
        )
        val inferred = V2Validator.validate(claimComplete(adjectiveOnly, 18)).errors
        assertTrue(inferred.joinToString("\n"), inferred.any { it.contains("without comparison and relative-clause sources") })
        val dictionaryOnly = stripped.copy(
            provenanceRecords = pack.provenanceRecords + pack.provenanceRecords.first { it.sourceId == "src-openrussian-50e210c4" }.copy(
                unitNumber = 18,
                originalSourceText = "comparative хороший superlative который",
            ),
        )
        val fromRows = V2Validator.validate(claimComplete(dictionaryOnly, 18)).errors
        assertTrue(fromRows.joinToString("\n"), fromRows.any { it.contains("without comparison and relative-clause sources") })
    }

    @Test
    fun unitNineteenCannotBecomeContentCompleteWithoutConditionalAndObligationSources() {
        val pack = pack()
        val complete = V2Validator.validate(claimComplete(pack, 19))
        assertTrue(complete.errors.joinToString("\n"), complete.ok)
        val stripped = withoutCapabilities(pack, 19)
        val errors = V2Validator.validate(claimComplete(stripped, 19)).errors
        assertTrue(errors.joinToString("\n"), errors.any { it.contains("without conditional and obligation sources") })
        val aspectOnly = stripped.copy(
            provenanceRecords = pack.provenanceRecords + pack.provenanceRecords.first { it.unitNumber == 19 }.copy(
                originalSourceText = "бы and должен are not on this aspect page",
            ),
        )
        val still = V2Validator.validate(claimComplete(aspectOnly, 19)).errors
        assertTrue(still.joinToString("\n"), still.any { it.contains("without conditional and obligation sources") })
        val obligation = pack.sourceCapabilities.first { it.supportedClaim == "obligation-dolzhen" }
        val relabeled = pack.copy(
            sourceCapabilities = pack.sourceCapabilities.filter { it.supportedClaim != "probability-dolzhen" } +
                obligation.copy(
                    id = "cap-forged-obligation-as-probability",
                    objectiveId = "obj-u19-probability",
                    supportedClaim = "probability-dolzhen",
                ),
        )
        val substituted = V2Validator.validate(claimComplete(relabeled, 19)).errors.joinToString("\n")
        assertTrue(substituted, substituted.contains("without conditional and obligation sources"))
        assertTrue(substituted, substituted.contains("no independent probability-dolzhen admission"))
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(relabeled, 19).any { it.claim == "probability-dolzhen" })
    }

    @Test
    fun unitTwentyCannotBecomeContentCompleteFromARedLinkVerbPageOrDictionaryRows() {
        val pack = pack()
        assertTrue(
            V2Validator.validate(claimComplete(pack, 20)).errors.joinToString("\n"),
            V2Validator.validate(claimComplete(pack, 20)).ok,
        )
        val stripped = withoutCapabilities(pack, 20)
        val base = V2Validator.validate(claimComplete(stripped, 20)).errors
        assertTrue(base.joinToString("\n"), base.any { it.contains("red link, general verb page, or dictionary rows") })
        val samples = listOf(
            sample(pack, "red-link", "src-wikibooks-russian-cc-by-sa", "Verbs of Motion"),
            sample(pack, "4233876", "src-wikibooks-russian-cc-by-sa", "A general verb page mentions unidirectional and multidirectional motion."),
            sample(pack, "50e210c4803237779cb562bc1abcea529066031c", "src-openrussian-50e210c4", "dictionary row unidirectional multidirectional"),
        )
        samples.forEach { record ->
            val claimed = stripped.copy(provenanceRecords = stripped.provenanceRecords + record)
            val errors = V2Validator.validate(claimComplete(claimed, 20)).errors
            assertTrue(errors.joinToString("\n"), errors.any { it.contains("red link, general verb page, or dictionary rows") })
        }
    }

    @Test
    fun learnerFacingTextDropsWikibooksEmphasisAndKeepsTheFrozenSource() {
        val pack = pack()
        assertTrue(V2Validator.validate(pack).errors.joinToString("\n"), V2Validator.validate(pack).ok)
        val ordinals = mapOf(
            "\u043f'''\u0435'''\u0440\u0432\u044b\u0439" to "\u043f\u0435\u0301\u0440\u0432\u044b\u0439",
            "\u0432\u0442\u043e\u0440'''\u043e'''\u0439" to "\u0432\u0442\u043e\u0440\u043e\u0301\u0439",
            "\u0442\u0440'''\u0435'''\u0442\u0438\u0439" to "\u0442\u0440\u0435\u0301\u0442\u0438\u0439",
            "\u0447\u0435\u0442\u0432'''\u0451'''\u0440\u0442\u044b\u0439" to "\u0447\u0435\u0442\u0432\u0451\u0440\u0442\u044b\u0439",
        )
        ordinals.forEach { (raw, clean) ->
            val record = pack.provenanceRecords.first {
                it.unitNumber == 17 && it.shippedText == clean && it.transformation == "wikibooks emphasis formatting"
            }
            assertTrue(record.originalSourceText.contains(raw))
            assertFalse(record.shippedText.contains("'''"))
            assertEquals(sha256(record.originalSourceText), record.sourceHash)
            assertEquals("4371358", record.sourceRevision)
            assertEquals("IMPLEMENTED_PENDING_FINAL_AUDIT", record.reviewStatus)
            if (raw.contains("\u0451")) assertFalse(clean.contains("\u0301"))
        }
        val display = pack.examples.filter { it.id.startsWith("ex-u17-ordinals-") }.map { it.display }
        assertEquals(listOf("\u043f\u0435\u0301\u0440\u0432\u044b\u0439", "\u0432\u0442\u043e\u0440\u043e\u0301\u0439", "\u0442\u0440\u0435\u0301\u0442\u0438\u0439", "\u0432\u043e\u0441\u0435\u043c\u043d\u0430\u0434\u0446\u0430\u0442\u0443\u044e \u0434\u0435\u0432\u0443\u0448\u043a\u0443"), display)
        learnerFields(pack, 16..20).forEach { (field, text) ->
            assertFalse(field, mediaWiki(text))
        }
        val instrumental = pack.unit(16)!!.sessions.flatMap { it.steps }.first { it.body.contains("-ом") }.body
        assertFalse(mediaWiki(instrumental))
        // Whole-word stress marks are taught explicitly; the suffix itself is unstressed.
        assertTrue(instrumental.contains("-ом"))
        assertFalse(instrumental.contains("-о\u0301м"))
        assertTrue(instrumental.contains("\u043e"))
        listOf(16, 18, 20).forEach { number ->
            val unit = pack.unit(number)!!
            assertEquals("DEVELOPMENT", unit.installState)
            assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", unit.readiness)
            assertTrue(pack.provenanceRecords.any { it.unitNumber == number && it.shippedText.any { char -> char.code in 0x0400..0x04FF } })
        }
        assertEquals("DEVELOPMENT", pack.unit(17)!!.installState)
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(17)!!.readiness)
        assertEquals("DEVELOPMENT", pack.unit(19)!!.installState)
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(19)!!.readiness)
        listOf(16, 17, 19).forEach { number ->
            val unit = pack.unit(number)!!
            assertEquals("DEVELOPMENT", unit.installState)
            assertEquals("PENDING_CURRICULUM_REVIEWER", unit.mappingStatus)
            assertTrue(pack.unitReviews.first { it.unitNumber == number }.reviewer.isNullOrBlank())
            val packet = renderUnitReviewFiles(pack, number).files.getValue(reviewPacketName(number))
            val learnerLine = listOf(
                "body=",
                "display=",
                "prompt=",
                "promptLetter=",
                "options=",
                "answer=",
                "feedback=",
                "shippedText=",
                "contrastMembers=",
                "whyAnswerDemonstratesObjective=",
            )
            packet.lines().forEach { line ->
                if (mediaWiki(line) && learnerLine.any { it in line } && "originalSourceText=" !in line && "sourceRecordId=" !in line) {
                    assertFalse(line, true)
                }
            }
        }
        val markers = listOf("'''", "''", "[[", "]]", "{{", "}}", "<ref", "</ref>", "{|", "|}")
        markers.forEach { marker ->
            assertTrue(marker, V2Validator.validate(inject(pack, "step", marker)).errors.any { it.contains("MediaWiki markup in learner-facing") })
            assertTrue(marker, V2Validator.validate(inject(pack, "remediation", marker)).errors.any { it.contains("MediaWiki markup in learner-facing") })
            assertTrue(marker, V2Validator.validate(inject(pack, "example", marker)).errors.any { it.contains("MediaWiki markup in learner-facing") })
            assertTrue(marker, V2Validator.validate(inject(pack, "note", marker)).errors.any { it.contains("MediaWiki markup in learner-facing") })
            assertTrue(marker, V2Validator.validate(inject(pack, "prompt", marker)).errors.any { it.contains("MediaWiki markup in learner-facing") })
            assertTrue(marker, V2Validator.validate(inject(pack, "promptLetter", marker)).errors.any { it.contains("MediaWiki markup in learner-facing") })
            assertTrue(marker, V2Validator.validate(inject(pack, "option", marker)).errors.any { it.contains("MediaWiki markup in learner-facing") })
            assertTrue(marker, V2Validator.validate(inject(pack, "answer", marker)).errors.any { it.contains("MediaWiki markup in learner-facing") })
            assertTrue(marker, V2Validator.validate(inject(pack, "feedback", marker)).errors.any { it.contains("MediaWiki markup in learner-facing") })
            assertTrue(marker, V2Validator.validate(inject(pack, "assessment", marker)).errors.any { it.contains("MediaWiki markup in learner-facing") })
            assertTrue(marker, V2Validator.validate(inject(pack, "title", marker)).errors.any { it.contains("MediaWiki markup in learner-facing") })
        }
        val preservedOriginal = pack.provenanceRecords.first { it.unitNumber == 17 && it.shippedText == "\u043f\u0435\u0301\u0440\u0432\u044b\u0439" }
        val dirtyOriginal = pack.copy(
            provenanceRecords = pack.provenanceRecords.map { record ->
                if (record.itemId == preservedOriginal.itemId) record else record
            },
        )
        assertTrue(V2Validator.validate(dirtyOriginal).errors.none { it.contains(preservedOriginal.itemId) && it.contains("MediaWiki markup") })
    }

    @Test
    fun noPenguinContentOrPrivatePdfEnteredGit() {
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
        assertFalse(File(root, "catalog/sources/units-16-20").walkTopDown().any { it.isFile && it.extension.equals("pdf", ignoreCase = true) })
        val candidate = File(root, "release/acceptance/candidate.json").readText()
        assertTrue(candidate.contains("\"publicRelease\": \"NOT_APPROVED\""))
        assertTrue(candidate.contains("\"frozen\": false"))
        assertTrue(candidate.contains("\"sourceCommit\": null"))
        assertTrue(candidate.contains("AT-031"))
    }

    private fun withoutCapabilities(pack: V2CurriculumPack, number: Int) = pack.copy(
        sourceCapabilities = pack.sourceCapabilities.filter { it.unitNumber != number },
    )

    private fun reason(pack: dev.russiancompanion.v2.curriculum.V2CurriculumPack, number: Int): String {
        return pack.unit(number)!!.sessions.flatMap { it.steps }.mapNotNull { it.unavailableReason }.joinToString("\n")
    }

    private fun claimComplete(pack: dev.russiancompanion.v2.curriculum.V2CurriculumPack, number: Int) = pack.copy(
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

    private fun sample(pack: dev.russiancompanion.v2.curriculum.V2CurriculumPack, revision: String, sourceId: String, text: String): V2ProvenanceRecord {
        return pack.provenanceRecords.first().copy(
            unitNumber = 20,
            sourceRevision = revision,
            sourceId = sourceId,
            originalSourceText = text,
            shippedText = text,
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
        return File(File(root, "catalog/sources/units-16-20"), relative).canonicalFile
    }

    private fun mediaWiki(text: String): Boolean {
        return listOf("'''", "''", "[[", "]]", "{{", "}}", "<ref", "</ref>", "{|", "|}").any { it in text }
    }

    private fun learnerFields(pack: V2CurriculumPack, units: IntRange): List<Pair<String, String>> {
        val fields = mutableListOf<Pair<String, String>>()
        pack.units.filter { it.number in units }.forEach { unit ->
            unit.sessions.forEach { session ->
                fields += session.id to session.title
                session.steps.forEach { step ->
                    fields += step.id to step.title
                    fields += step.id to step.body
                }
            }
        }
        val exampleIds = pack.units.filter { it.number in units }.flatMap { unit -> unit.objectives.flatMap { it.exampleIds } }.toSet()
        pack.examples.filter { it.id in exampleIds }.forEach { example ->
            fields += example.id to example.display
            fields += example.id to example.note
        }
        val itemIds = pack.units.filter { it.number in units }.flatMap { unit ->
            unit.sessions.flatMap { session -> session.steps.flatMap { it.itemIds } }
        }.toSet()
        pack.items.filter { it.id in itemIds }.forEach { item ->
            fields += item.id to item.prompt
            item.promptLetter?.let { fields += item.id to it }
            fields += item.id to item.answer
            fields += item.id to item.feedback
            item.options.forEach { fields += item.id to it }
            item.assessment?.let { assessment ->
                fields += item.id to assessment.whyAnswerDemonstratesObjective
                assessment.distractorDifference.forEach { fields += item.id to it }
                assessment.contrastMembers.forEach { fields += item.id to it }
            }
        }
        pack.provenanceRecords.filter { it.unitNumber in units }.forEach { record ->
            fields += record.itemId to record.shippedText
        }
        return fields
    }

    private fun inject(pack: V2CurriculumPack, field: String, marker: String): V2CurriculumPack {
        val unit = pack.unit(17)!!
        return when (field) {
            "title" -> pack.copy(units = pack.units.map { if (it.number == 17) it.copy(sessions = it.sessions.map { session -> session.copy(steps = session.steps.map { step -> if (step.kind == "TEACH") step.copy(title = step.title + marker) else step }) }) else it })
            "step", "remediation" -> pack.copy(units = pack.units.map { current ->
                if (current.number != 17) current else current.copy(sessions = current.sessions.map { session ->
                    session.copy(steps = session.steps.map { step ->
                        val selected = if (field == "remediation") step.kind == "REMEDIATE" else step.kind == "TEACH"
                        if (selected) step.copy(body = step.body + marker) else step
                    })
                })
            })
            "example" -> pack.copy(examples = pack.examples.map { if (it.id == "ex-u17-ordinals-0") it.copy(display = it.display + marker) else it })
            "note" -> pack.copy(examples = pack.examples.map { if (it.id == "ex-u17-ordinals-0") it.copy(note = it.note + marker) else it })
            "prompt" -> pack.copy(items = pack.items.map { if (it.id.startsWith("u17-i-ordinals")) it.copy(prompt = it.prompt + marker) else it })
            "promptLetter" -> pack.copy(items = pack.items.map { if (it.id.startsWith("u17-i-ordinals")) it.copy(promptLetter = (it.promptLetter ?: "") + marker) else it })
            "option" -> pack.copy(items = pack.items.map { if (it.id.startsWith("u17-i-ordinals")) it.copy(options = it.options + marker) else it })
            "answer" -> pack.copy(items = pack.items.map { if (it.id.startsWith("u17-i-ordinals")) it.copy(answer = it.answer + marker) else it })
            "feedback" -> pack.copy(items = pack.items.map { if (it.id.startsWith("u17-i-ordinals")) it.copy(feedback = it.feedback + marker) else it })
            "assessment" -> pack.copy(items = pack.items.map { item ->
                val assessment = item.assessment
                if (!item.id.startsWith("u17-i-ordinals") || assessment == null) item else item.copy(
                    assessment = assessment.copy(whyAnswerDemonstratesObjective = assessment.whyAnswerDemonstratesObjective + marker),
                )
            })
            else -> error(field)
        }.let { edited ->
            if (field == "title") edited else edited
        }.also { _ -> unit.number }
    }

    private val readyRussianMedia = Regex("(?i)<img|data:image|base64|<script|<audio|\\.ogg|data:audio")

    private fun normalized(text: String) = text.replace("\u0301", "").replace("'", "")

    private fun sha256(text: String): String = sha256(text.toByteArray(Charsets.UTF_8))

    private fun sha256(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        return digest.joinToString("") { byte -> "%02x".format(byte) }
    }

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
