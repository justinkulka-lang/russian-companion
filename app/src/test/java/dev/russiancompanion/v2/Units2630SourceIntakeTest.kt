package dev.russiancompanion.v2

import dev.russiancompanion.release.ReleaseDecision
import dev.russiancompanion.release.assessRepositoryRelease
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2ProvenanceRecord
import dev.russiancompanion.v2.curriculum.V2SourceCapability
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.advancementDecision
import dev.russiancompanion.v2.curriculum.unit
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

class Units2630SourceIntakeTest {
    private val preservedContentDigests = mapOf(
        1 to "9adca20544f28770210fe3029c2e0e4c437e3b409216e9288227c316e9ebf61a",
        2 to "d51310f9ac1596ab50d2c5df7dffd9b10db23d712875680dbfb942bf577e056b",
        3 to "deadcf10b8af7efca034b8d89921412f309d291180c09c4af490eb132b1bbb86",
        4 to "5a9be201954e4a4c37188ed7aca9fae1ee67e20622c7134271c1a68cd520e59c",
        5 to "9470cc61d50236425fb01771a4d20788c88d6c98f8a8694420609f86d7b9710d",
        6 to "fa6501022bd7ff4a2eca1e47c0ad488d96254cab0eafbecc61a8420835a3ab9c",
        7 to "56f1f67b9bda3e58eedd8f7bbb390c23fce2cff39b36a5d964a028cc2eeb33d1",
        8 to "e1dd456abf8a7b961f2f8471f35d4178961a53147c354f3e410e88d75f025c78",
        9 to "d0f135c408fc429392cf695a8122134013fca69637a4f9c1587db3fec6b3c65f",
        10 to "6845b398da6ad901b06443dd0f56ee6ace4ba4e2da6af9e36d33934dc286c526",
        11 to "7a70c3b29f7c88934d0dcf21f40d531a45f1eff9daa8593f5af09d509bd10324",
        12 to "c610495bf16604d555187f66e456c2cd1cb127c77881ef87097332f5e2d426bb",
        13 to "e8a04394dc8c90059f01a0b7fdf46a57951a98a85ac8050a29781ab1a4a027c5",
        14 to "270cbad6d62543931aa65932bdaeda9d10475bab78f7c12f05a8f75ac1308ec6",
        15 to "1fceb09b1709fcd76676925c5e363736fad187db50e2830cbd115bb5891219dd",
        16 to "9d8af88bfe9cb7531d03de5fdf39634385464b62ecfd49a345a776e1963829ae",
        17 to "afa28605750babc409272ca6a50e382fc6939db6ff70ee9ef8af50df491a591d",
        18 to "42d42c697599fe90277ad3947c69d71fcf25baaa41b1d0927bb7733a0b836be5",
        19 to "38cc28f764d971e6e99330e4183da2e8ea0e8ee5764ab1d7f6412b44987a8aa8",
        20 to "db77d02ce69cc75166c5169bf1ccc0da37bc3ab31c4f0db52edfc7fe96ed7ff5",
        21 to "bce0242a119e8b01a934733d7f8855d426eb151515bfb6be539e716f29035528",
        22 to "9b019832345deb4495570153288fb0886500833b02bf362fce6a70d082bdccf6",
        23 to "c864479764d459bb7fcd5797a4e874c1f15f44c2c5fd12083d71e0ac41a85388",
        24 to "34000381d687bf714193b787e6800443e2bcdad53c03f17664554009e171cf3a",
        25 to "80c584f75c59e98b7b020feda3ecf1a89cef88cc4f62b93f971ef86597395a84",
    )

    private val preservedMappingDigests = mapOf(
        26 to "c3b63d48cd809527d6450916cfead9c419eb5a607a329c7def2d8b42df6bf725",
        27 to "f5da4227d7bdfa500293d76f3b48ab8ac7646ac0dd6c90b4e8ac4f10cc69a385",
        28 to "bdf9618b16d9a96d9d359a01e7901617c7d8b97bcd601768695131da126497aa",
        29 to "54c2f00221054ced81fe17bea8b0f156073b830e54e70c47cf769e93bd56eb36",
        30 to "e67db823a25bc0676dcd14af04c2af73c65ebff9bccde7afa3b68a57127ce367",
    )

    @Test
    fun retainedSourceBytesMatchTheFrozenIdentityTwice() {
        val root = repo()
        val freeze = Json.parseToJsonElement(File(root, "catalog/sources/units-26-30/FREEZE.json").readText()).jsonObject
        val license = freeze.getValue("license").jsonObject
        assertEquals("2026-09-24", freeze.getValue("retrieved").jsonPrimitive.content)
        assertEquals("CC BY-SA 4.0", license.getValue("licenseId").jsonPrimitive.content)
        assertEquals("https://creativecommons.org/licenses/by-sa/4.0/", license.getValue("licenseUrl").jsonPrimitive.content)
        assertEquals("4622060", license.getValue("wikibooksLicenseOldid").jsonPrimitive.content)
        val copyrights = File(root, "catalog/sources/units-3-5/wikibooks/copyrights-4622060.txt").readBytes()
        assertEquals(license.getValue("wikibooksLicenseTextSha256").jsonPrimitive.content, sha256(copyrights))
        assertEquals(sha256(copyrights), sha256(copyrights.copyOf()))
        assertTrue(String(copyrights, Charsets.UTF_8).contains("Creative Commons Attribution-ShareAlike 4.0"))
        val routes = license.getValue("routes").jsonObject
        val wikipedia = routes.getValue("wikipedia").jsonObject
        val wikibooks = routes.getValue("wikibooks").jsonObject
        assertNotEquals(wikipedia.getValue("file").jsonPrimitive.content, wikibooks.getValue("file").jsonPrimitive.content)
        assertEquals("1369466282", wikipedia.getValue("oldid").jsonPrimitive.content)
        assertTrue(wikipedia.getValue("url").jsonPrimitive.content.contains("oldid=1369466282"))
        assertEquals("2026-09-24", wikipedia.getValue("retrieved").jsonPrimitive.content)
        val wikipediaBytes = File(root, "catalog/sources/units-26-30/${wikipedia.getValue("file").jsonPrimitive.content}").readBytes()
        val wikipediaHash = sha256(wikipediaBytes)
        assertEquals(wikipediaHash, sha256(wikipediaBytes.copyOf()))
        assertEquals(wikipedia.getValue("sha256").jsonPrimitive.content, wikipediaHash)
        assertTrue(String(wikipediaBytes, Charsets.UTF_8).contains(wikipedia.getValue("requiredStatement").jsonPrimitive.content))
        assertTrue(File(root, "app/src/main/assets/licenses/cc-by-sa-4.0.txt").isFile)
        assertTrue(File(root, "app/src/main/assets/licenses/units-26-30-curriculum-notice.txt").readText().contains("Wikipedia:Copyrights oldid 1369466282"))
        assertTrue(File(root, "app/src/main/assets/licenses/units-26-30-curriculum-notice.txt").readText().contains("Wikibooks:Copyrights oldid 4622060"))
        val expected = mapOf(
            "grammar" to "1369617627",
            "alphabet" to "1371795481",
            "address" to "3353858",
        )
        freeze.getValue("pages").jsonArray.forEach { element ->
            val page = element.jsonObject
            val key = page.getValue("key").jsonPrimitive.content
            assertEquals(expected.getValue(key), page.getValue("oldid").jsonPrimitive.content)
            assertTrue(page.getValue("url").jsonPrimitive.content.contains("oldid=${page.getValue("oldid").jsonPrimitive.content}"))
            val bytes = File(root, "catalog/sources/units-26-30/${page.getValue("file").jsonPrimitive.content}").readBytes()
            val first = sha256(bytes)
            val second = sha256(bytes.copyOf())
            assertEquals(first, second)
            assertEquals(page.getValue("sha256").jsonPrimitive.content, first)
            assertEquals(page.getValue("size").jsonPrimitive.content.toInt(), bytes.size)
            val revision = Json.parseToJsonElement(
                File(root, "catalog/sources/units-26-30/${page.getValue("revisionFile").jsonPrimitive.content}").readText(),
            ).jsonObject
            assertEquals(page.getValue("oldid").jsonPrimitive.content, revision.getValue("revid").jsonPrimitive.content)
            assertEquals(page.getValue("mediawikiSha1").jsonPrimitive.content, revision.getValue("sha1").jsonPrimitive.content)
            assertEquals(page.getValue("timestamp").jsonPrimitive.content, revision.getValue("timestamp").jsonPrimitive.content)
            assertEquals(page.getValue("parentid").jsonPrimitive.content, revision.getValue("parentid").jsonPrimitive.content)
            val truncated = sha256(bytes.dropLast(1).toByteArray())
            assertNotEquals(page.getValue("sha256").jsonPrimitive.content, truncated)
        }
        assertFalse(File(root, "catalog/sources/units-26-30/missing-source-bytes.txt").exists())
    }

    @Test
    fun admittedCapabilitiesBindSpanUnitObjectiveAndClaim() {
        val pack = pack()
        val expected = mapOf(
            "cap-obj-u26-word-order" to Triple(26, "obj-u26-word-order", "word-order"),
            "cap-obj-u27-passive" to Triple(27, "obj-u27-passive", "passive-participle"),
            "cap-obj-u28-imperfective-adverb" to Triple(28, "obj-u28-imperfective-adverb", "imperfective-verbal-adverb"),
            "cap-obj-u28-perfective-adverb" to Triple(28, "obj-u28-perfective-adverb", "perfective-verbal-adverb"),
            "cap-obj-u29-active" to Triple(29, "obj-u29-active", "active-participle"),
            "cap-obj-u29-short-adjective" to Triple(29, "obj-u29-short-adjective", "short-form-adjective"),
            "cap-obj-u30-letter-names" to Triple(30, "obj-u30-letter-names", "letter-name-review"),
        )
        val admitted = pack.sourceCapabilities.filter { it.unitNumber in 26..30 }
        assertEquals(expected.keys, admitted.map { it.id }.toSet())
        admitted.forEach { capability ->
            val binding = expected.getValue(capability.id)
            assertEquals(binding.first, capability.unitNumber)
            assertEquals(binding.second, capability.objectiveId)
            assertEquals(binding.third, capability.supportedClaim)
            assertTrue(capability.sourceUrlBound(pack))
            val hosts = pack.provenanceRecords.filter {
                it.sourceId == capability.sourceId &&
                    it.sourceRevision == capability.sourceRevision &&
                    it.sourceRecordId == capability.sourceRecordId &&
                    it.originalSourceText == capability.exactSpan
            }
            assertTrue(capability.id, hosts.isNotEmpty())
            val host = hosts.first()
            assertEquals(sha256(host.originalSourceText), capability.sourceHash)
            assertEquals(sha256(host.originalSourceText), host.sourceHash)
            assertEquals(capability.unitNumber, host.unitNumber)
            assertEquals(capability.objectiveId, host.objectiveId)
            assertEquals("CC BY-SA 4.0", host.licenseId)
            assertEquals("https://creativecommons.org/licenses/by-sa/4.0/", host.licenseUrl)
            assertTrue(host.attribution.contains("CC BY-SA 4.0"))
            assertTrue(host.shareAlikeTreatment.contains("CC BY-SA 4.0"))
            assertTrue(host.shareAlikeTreatment.contains("Application code"))
            assertTrue(host.sourceUrl.contains("oldid="))
            assertEquals("PENDING_QUALIFIED_REVIEW", host.reviewStatus)
            assertTrue(host.reviewerIdentity.isBlank())
            assertNull(host.audioAssetId)
        }
        val notice = pack.references.single { it.id == "ref-u2630-attribution" }
        assertEquals("LICENSE_OR_ATTRIBUTION", notice.classification)
        assertTrue(notice.detail.contains("CC BY-SA 4.0"))
        assertTrue(notice.detail.contains("Application code"))
        assertTrue(notice.detail.contains("user recordings"))
        assertTrue(notice.detail.contains("No audio"))
    }

    @Test
    fun partialStrandsDoNotClearBroadClaimsAndOptionalMaterialDoesNotGate() {
        val pack = pack()
        assertTrue(V2Validator.validate(pack).errors.joinToString("\n"), V2Validator.validate(pack).ok)
        val unit27 = pack.unit(27)!!
        val present = unit27.objectives.single { it.id == "obj-u27-present-passive" }
        assertFalse(present.gating)
        assertTrue(unit27.objectives.none { it.id.contains("active") })
        val hosts = learnerText(pack, 27)
        listOf("чита́ющая", "чита́вшая", "живу́щие", "active present participle", "active past participle").forEach { banned ->
            assertFalse(hosts, banned in hosts)
        }
        val decision = advancementDecision(pack, unit27, learner(27))
        assertFalse(decision.remaining.any { it.contains(present.title) })
        assertTrue(pack.unit(29)!!.objectives.any { it.id == "obj-u29-active" })
        val orient = pack.unit(30)!!.sessions.flatMap { it.steps }.first { it.id == "u30-st-orient" }.body
        assertTrue(orient.contains("Unit 1"))
        assertFalse(orient.contains("for the first time"))
        assertFalse(pack.unit(30)!!.topicSummary.contains("for the first time"))
        listOf(26, 27, 28, 29, 30).forEach { number ->
            val unit = pack.unit(number)!!
            assertEquals("DEVELOPMENT", unit.installState)
            assertEquals("NOT_PREPARED", unit.readiness)
            assertEquals("PENDING_CURRICULUM_REVIEWER", unit.mappingStatus)
            assertNotEquals("CONTENT_COMPLETE_REVIEW_PENDING", unit.readiness)
            assertNotEquals("INSTALLABLE", unit.installState)
            assertNotEquals("INSTALLED", unit.installState)
            assertTrue(unit.sessions.any { session -> session.steps.any { !it.unavailableReason.isNullOrBlank() || !it.bookDirection.isNullOrBlank() } })
            assertEquals(preservedMappingDigests.getValue(number), mappingPackageDigest(governedMappingFacts(number)))
        }
        val blocked = advancementDecision(pack, pack.unit(26)!!, learner(26, book = true))
        assertFalse(blocked.allowed)
        assertTrue(blocked.remaining.any { it.contains("indefinite-pronoun") })
        assertFalse(blocked.remaining.any { it.contains("word-order") && it.contains("not content-complete") })
    }

    @Test
    fun presentPassiveGlossFollowsOnlyTheExplicitParenthetical() {
        val pack = pack()
        val objective = "obj-u27-present-passive"
        assertTrue(pack.examples.none { it.objectiveId == objective && it.display.endsWith("\u0435\u043c\u044b\u0439") && it.note == "we leave" })
        assertTrue(pack.items.none { it.objectiveId == objective && (it.promptLetter ?: "").endsWith("\u0435\u043c\u044b\u0439") && it.answer == "we leave" })
        val paired = pack.items.single { it.objectiveId == objective && it.answer == "we leave" }
        assertEquals("\u043e\u0441\u0442\u0430\u0432\u043b\u044f\u0301\u0435\u043c", paired.promptLetter)
        assertEquals("EXPLICIT_PARENTHETICAL_GLOSS", paired.assessment!!.relationKind)
        assertEquals("EXPLICIT_PARENTHETICAL_GLOSS", paired.assessment!!.evidenceMode)
        assertFalse(paired.feedback.contains("written beside the shown form"))
        val cueId = paired.assessment!!.relationParticipantIds.first()
        val participle = "\u043e\u0441\u0442\u0430\u0432\u043b\u044f\u0301\u0435\u043c\u044b\u0439"
        val stolenCue = pack.provenanceRecords.single { it.itemId == cueId }.copy(
            shippedText = participle,
            contentHash = sha256(participle),
        )
        val stolen = pack.copy(
            provenanceRecords = replace(pack, stolenCue),
            items = pack.items.map { if (it.id == paired.id) it.copy(promptLetter = stolenCue.shippedText) else it },
        )
        val stolenErrors = V2Validator.validate(stolen).errors
        assertTrue(stolenErrors.joinToString("\n"), stolenErrors.any { paired.id in it })

        val answerId = paired.assessment!!.answerSourceIds.single()
        val changedAnswer = pack.provenanceRecords.single { it.itemId == answerId }.copy(
            shippedText = "to leave",
            contentHash = sha256("to leave"),
        )
        val changedParticipant = pack.copy(
            provenanceRecords = replace(pack, changedAnswer),
            items = pack.items.map { item ->
                if (item.id != paired.id) item else item.copy(answer = "to leave", options = item.options.map { if (it == "we leave") "to leave" else it })
            },
        )
        val participantErrors = V2Validator.validate(changedParticipant).errors
        assertTrue(participantErrors.joinToString("\n"), participantErrors.any { paired.id in it })

        val moved = pack.copy(
            provenanceRecords = pack.provenanceRecords.map { record ->
                if (!record.originalSourceText.contains("(\u0077e leave)")) record else {
                    val text = record.originalSourceText
                        .replace("\u043e\u0441\u0442\u0430\u0432\u043b\u044f\u0301'''\u0435\u043c''' (we leave)", "\u043e\u0441\u0442\u0430\u0432\u043b\u044f\u0301'''\u0435\u043c'''")
                        .replace("\u043e\u0441\u0442\u0430\u0432\u043b\u044f\u0301'''\u0435\u043c'''\u044b\u0439", "\u043e\u0441\u0442\u0430\u0432\u043b\u044f\u0301'''\u0435\u043c'''\u044b\u0439 (we leave)")
                    record.copy(originalSourceText = text, sourceHash = sha256(text))
                }
            },
        )
        val movedErrors = V2Validator.validate(moved).errors
        assertTrue(movedErrors.joinToString("\n"), movedErrors.any { paired.id in it })

        val cooccurrence = pack.copy(
            provenanceRecords = pack.provenanceRecords.map { record ->
                if ("(we leave)" !in record.originalSourceText) record else {
                    val text = record.originalSourceText.replace("(we leave)", "we leave")
                    record.copy(originalSourceText = text, sourceHash = sha256(text))
                }
            },
        )
        val cooccurrenceErrors = V2Validator.validate(cooccurrence).errors
        assertTrue(cooccurrenceErrors.joinToString("\n"), cooccurrenceErrors.any { paired.id in it })
        assertFalse(cooccurrenceErrors.isEmpty())
    }

    @Test
    fun wikipediaLicenseRouteIsNotTheWikibooksSnapshot() {
        val root = repo()
        val script = """
            import json
            from pathlib import Path
            from units_26_30_intake import license_admission_errors
            root = Path('.').resolve().parent
            freeze_path = root / 'catalog/sources/units-26-30/FREEZE.json'
            source_root = freeze_path.parent
            freeze = json.loads(freeze_path.read_text())
            assert license_admission_errors(freeze, source_root) == []
            wikibooks_only = json.loads(json.dumps(freeze))
            for page in wikibooks_only['pages']:
                if page['sourceId'].startswith('src-wikipedia-'):
                    page['licenseRoute'] = 'wikibooks'
            wikibooks_errors = license_admission_errors(wikibooks_only, source_root)
            assert any('only Wikibooks license evidence' in error for error in wikibooks_errors), wikibooks_errors
            changed_hash = json.loads(json.dumps(freeze))
            changed_hash['license']['routes']['wikipedia']['sha256'] = '0' * 64
            hash_errors = license_admission_errors(changed_hash, source_root)
            assert any('Wikipedia license evidence hash does not match' in error for error in hash_errors), hash_errors
            missing_statement = license_admission_errors(freeze, source_root, {'wikipedia': b'no license statement here'})
            assert any('does not state CC BY-SA 4.0' in error for error in missing_statement), missing_statement
            print('ok')
        """.trimIndent()
        val process = ProcessBuilder("python3", "-c", script)
            .directory(File(root, "catalog"))
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().readText()
        assertEquals(output, 0, process.waitFor())
        val pack = pack()
        val wikipedia = pack.provenanceRecords.filter { it.unitNumber in 26..30 && it.sourceId.startsWith("src-wikipedia-") }
        assertTrue(wikipedia.isNotEmpty())
        wikipedia.forEach { record ->
            assertTrue(record.attribution.contains("Wikipedia:Copyrights oldid 1369466282"))
            assertFalse(record.attribution.contains("Wikibooks:Copyrights"))
        }
        val onlyWikibooks = pack.copy(
            provenanceRecords = pack.provenanceRecords.map { record ->
                if (!record.sourceId.startsWith("src-wikipedia-")) record else record.copy(
                    attribution = "Wikibooks contributors, CC BY-SA 4.0, via Wikibooks:Copyrights oldid 4622060.",
                )
            },
        )
        val errors = V2Validator.validate(onlyWikibooks).errors
        assertTrue(errors.joinToString("\n"), errors.any { it.contains("Wikipedia license route is missing") })
    }

    @Test
    fun duplicateSpanMarkersFailClosed() {
        val root = repo()
        val script = """
            import units_26_30_lessons as lessons
            def rejected(text, start, end):
                try:
                    lessons._span_in(text, start, end, 'sample')
                except SystemExit as error:
                    return 'ambiguous' in str(error)
                return False
            assert rejected('alpha beta alpha end', 'alpha', 'end')
            assert rejected('start end middle end', 'start', 'end')
            assert lessons._span_in('start unique body end', 'start', 'end', 'sample') == 'start unique body end'
            admitted = lessons._span('grammar', 'Basic word order, both in conversation and written language', 'Word order can express logical stress, and degree of definiteness.')
            assert admitted.startswith('Basic word order')
            print('ok')
        """.trimIndent()
        val process = ProcessBuilder("python3", "-c", script)
            .directory(File(root, "catalog"))
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().readText()
        assertEquals(output, 0, process.waitFor())
    }

    @Test
    fun forgedMutableWrongAndExcludedSourcesStayClosed() {
        val pack = pack()
        val host = pack.sourceCapabilities.single { it.id == "cap-obj-u26-word-order" }
        val provenance = pack.provenanceRecords.first {
            it.sourceRecordId == host.sourceRecordId && it.originalSourceText == host.exactSpan
        }
        val changedRecords = pack.provenanceRecords.map { record ->
            if (record.sourceRecordId == host.sourceRecordId && record.originalSourceText == host.exactSpan) {
                record.copy(originalSourceText = record.originalSourceText + " changed")
            } else {
                record
            }
        }
        val changedErrors = V2Validator.validate(pack.copy(provenanceRecords = changedRecords)).errors
        assertTrue(changedErrors.joinToString("\n"), changedErrors.any { it.contains("source hash does not match the frozen record") })

        val missing = pack.copy(
            provenanceRecords = pack.provenanceRecords.filter { record ->
                !(record.sourceId == host.sourceId &&
                    record.sourceRevision == host.sourceRevision &&
                    record.sourceRecordId == host.sourceRecordId &&
                    record.sourceHash == host.sourceHash)
            },
        )
        val missingErrors = V2Validator.validate(missing).errors
        assertTrue(missingErrors.joinToString("\n"), missingErrors.any { it.contains(host.id) })

        val forged = host.copy(sourceRevision = "9999999")
        assertTrue(errorsFor(pack, forged).any { it.contains("revision does not match the frozen record") })

        val other = pack.provenanceRecords.first { it.unitNumber == 30 && it.originalSourceText != host.exactSpan }
        val copied = host.copy(id = "cap-copied-span", exactSpan = other.originalSourceText)
        assertTrue(errorsFor(pack, copied).joinToString("\n"), errorsFor(pack, copied).any { it.contains("span is not in the frozen record") })

        val wrongRecord = host.copy(sourceRecordId = "grammar:not-the-admitted-record")
        assertTrue(errorsFor(pack, wrongRecord).any { it.contains("revision does not match the frozen record") || it.contains("not an admitted frozen source") })

        val wrongUnit = host.copy(id = "cap-wrong-unit", unitNumber = 27, objectiveId = "obj-u27-passive", supportedClaim = "passive-participle")
        assertTrue(errorsFor(pack, wrongUnit).any { it.contains("wrong unit or objective") })

        val wrongObjective = host.copy(id = "cap-wrong-objective", objectiveId = "obj-u26-indefinite", supportedClaim = "indefinite-pronoun")
        assertTrue(errorsFor(pack, wrongObjective).any { it.contains("wrong unit") || it.contains("not an admitted frozen source") || it.contains("revision does not match") })

        val wrongClaim = host.copy(id = "cap-wrong-claim", supportedClaim = "letter-writing")
        assertTrue(errorsFor(pack, wrongClaim).any { it.contains("wrong unit or objective") })

        val keyword = capability(sample(pack, 26, "current", "src-wiktionary-excluded", "indefinite pronoun word order postal address"), "cap-keyword", 26, "obj-u26-indefinite", "indefinite-pronoun")
        val keywordErrors = errorsFor(pack.copy(provenanceRecords = pack.provenanceRecords + keywordProvenance(pack)), keyword)
        assertTrue(keywordErrors.joinToString("\n"), keywordErrors.any { it.contains("excluded source") || it.contains("not an admitted frozen source") })

        val mutable = host.copy(
            id = "cap-mutable",
            sourceRevision = "current",
            sourceId = "src-wikipedia-russian-grammar-current",
        )
        val mutableProvenance = provenance.copy(
            itemId = "prov-mutable",
            sourceId = mutable.sourceId,
            sourceRevision = "current",
            sourceUrl = "https://en.wikipedia.org/wiki/Russian_grammar",
        )
        val mutableErrors = V2Validator.validate(
            pack.copy(
                provenanceRecords = pack.provenanceRecords + mutableProvenance,
                sourceCapabilities = pack.sourceCapabilities + mutable,
            ),
        ).errors
        assertTrue(mutableErrors.joinToString("\n"), mutableErrors.any { it.contains("mutable webpage") || it.contains("excluded source") || it.contains("not an admitted frozen source") })

        val unrelated = sample(pack, 26, "4220315", "src-wikibooks-russian-cc-by-sa", "An unrelated CC BY-SA sentence does not state the indefinite series.")
        val unrelatedErrors = V2Validator.validate(claimReady(pack.copy(provenanceRecords = pack.provenanceRecords + unrelated), 26)).errors
        assertTrue(unrelatedErrors.any { it.contains("indefinite-pronoun") })

        val pages = host.copy(id = "cap-pages", exactSpan = "320-331", supportedClaim = "indefinite-pronoun", objectiveId = "obj-u26-indefinite")
        val pageErrors = errorsFor(pack, pages)
        assertTrue(pageErrors.joinToString("\n"), pageErrors.any { it.contains("structural page range") || it.contains("not an admitted frozen source") || it.contains("span is not in the frozen record") })
    }

    @Test
    fun generatedRussianPenguinAudioAndReleaseStayClosed() {
        val pack = pack()
        val root = repo()
        assertEquals(0, pack.admissions.generatedRussianWords)
        assertEquals(0, pack.admissions.penguinProseAssets)
        assertEquals(0, pack.admissions.catalogAudioBoundToUnits)
        assertEquals(0, pack.audioAssets.size)
        assertTrue(pack.provenanceRecords.none { it.unitNumber in 26..30 && it.audioAssetId != null })
        val audio = pack.provenanceRecords.first { it.unitNumber == 26 }.copy(audioAssetId = "audio-unrelated")
        val audioErrors = V2Validator.validate(pack.copy(provenanceRecords = replace(pack, audio))).errors
        assertTrue(audioErrors.any { it.contains("audio") })
        val generated = pack.items.first { it.objectiveId.startsWith("obj-u26-") }.copy(containsGeneratedRussian = true)
        val generatedErrors = V2Validator.validate(pack.copy(items = pack.items.map { if (it.id == generated.id) generated else it })).errors
        assertTrue(generatedErrors.joinToString("\n"), generatedErrors.any { it.contains("generated") || it.contains(generated.id) })
        val curriculum = File(root, "app/src/main/assets/content/v2-curriculum.json").readText()
        assertFalse(curriculum.contains("The New Penguin Russian Course"))
        assertFalse(curriculum.contains(".pdf"))
        assertFalse(curriculum.contains("not vital"))
        assertFalse(curriculum.contains("predictible"))
        assertFalse(File(root, "app/src/main/AndroidManifest.xml").readText().contains("android.permission.INTERNET"))
        val proposal = File(root, "docs/governance/UNITS_26_30_AUTHORITY_VARIANCE_PROPOSAL.md").readText()
        assertTrue(proposal.contains("Status: PROPOSED_NOT_ACTIVATED"))
        assertFalse(proposal.contains("Status: ACTIVATED"))
        val resolution = File(root, "docs/governance/UNITS_26_30_AUTHORITY_VARIANCE_PROPOSED_RESOLUTION.md").readText()
        assertTrue(resolution.contains("PROPOSED_NOT_ACTIVATED"))
        assertTrue(resolution.contains("ACTIVATE_WITH_CORRECTIONS"))
        assertFalse(resolution.contains("Status: ACTIVATED"))
        assertEquals(
            "bb4932783224e85d60b130118191185c2e26d224254a0a54a2c52cfc92b4aaaf",
            sha256(File(root, "docs/governance/MASTER_AUTHORITY_v2.0.md").readBytes()),
        )
        val assessment = assessRepositoryRelease(root)
        assertEquals(ReleaseDecision.NOT_APPROVED, assessment.decision)
        assertTrue(assessment.blockers.joinToString("\n") { it.message }, assessment.blockers.any {
            it.message.contains("AT-031") || it.message.contains("not approved") || it.name.contains("FROZEN")
        })
        preservedContentDigests.forEach { (number, digest) ->
            assertEquals("unit $number", digest, V2Validator.unitContentReviewDigest(pack, number))
        }
        val unit23 = V2Validator.unitContentReviewDigest(pack, 23)
        val shifted = pack.copy(
            units = pack.units.map { unit -> if (unit.number == 26) unit.copy(topicSummary = unit.topicSummary + " extra") else unit },
        )
        assertEquals(unit23, V2Validator.unitContentReviewDigest(shifted, 23))
        assertNotEquals(V2Validator.unitContentReviewDigest(pack, 26), V2Validator.unitContentReviewDigest(shifted, 26))
        (1..25).forEach { number ->
            assertEquals(V2Validator.unitContentReviewDigest(pack, number), V2Validator.unitContentReviewDigest(shifted, number))
        }
    }

    private fun V2SourceCapability.sourceUrlBound(pack: V2CurriculumPack): Boolean {
        return pack.provenanceRecords.any {
            it.sourceId == sourceId && it.sourceRevision == sourceRevision && it.sourceUrl.contains("oldid=$sourceRevision")
        }
    }

    private fun errorsFor(pack: V2CurriculumPack, capability: V2SourceCapability): List<String> {
        val replaced = pack.sourceCapabilities.filter { it.id != capability.id } + capability
        return V2Validator.validate(pack.copy(sourceCapabilities = replaced)).errors
    }

    private fun keywordProvenance(pack: V2CurriculumPack): V2ProvenanceRecord {
        return sample(pack, 26, "current", "src-wiktionary-excluded", "indefinite pronoun word order postal address").copy(
            sourceUrl = "https://en.wiktionary.org/wiki/Russian",
            objectiveId = "obj-u26-indefinite",
        )
    }

    private fun replace(pack: V2CurriculumPack, record: V2ProvenanceRecord): List<V2ProvenanceRecord> {
        return pack.provenanceRecords.map { if (it.itemId == record.itemId) record else it }
    }

    private fun claimReady(pack: V2CurriculumPack, number: Int) = pack.copy(
        units = pack.units.map { unit ->
            if (unit.number != number) unit else unit.copy(readiness = "CONTENT_COMPLETE_REVIEW_PENDING")
        },
    )

    private fun capability(
        record: V2ProvenanceRecord,
        id: String,
        number: Int,
        objectiveId: String,
        claim: String,
    ) = V2SourceCapability(
        id = id,
        sourceId = record.sourceId,
        sourceRevision = record.sourceRevision,
        sourceRecordId = record.sourceRecordId,
        sourceHash = record.sourceHash,
        exactSpan = record.originalSourceText,
        unitNumber = number,
        objectiveId = objectiveId,
        supportedClaim = claim,
    )

    private fun sample(pack: V2CurriculumPack, number: Int, revision: String, sourceId: String, text: String): V2ProvenanceRecord {
        return pack.provenanceRecords.first().copy(
            itemId = "prov-intake-$number",
            unitNumber = number,
            objectiveId = "obj-u$number-probe",
            sourceRevision = revision,
            sourceId = sourceId,
            originalSourceText = text,
            shippedText = text,
            sourceHash = sha256(text),
            contentHash = sha256(text),
            sourceUrl = "https://en.wiktionary.org/wiki/Russian",
        )
    }

    private fun learnerText(pack: V2CurriculumPack, number: Int): String {
        val unit = pack.unit(number)!!
        val ids = unit.objectives.map { it.id }.toSet()
        return buildString {
            unit.sessions.forEach { session -> session.steps.forEach { append(it.body).append('\n').append(it.title).append('\n') } }
            pack.examples.filter { it.objectiveId in ids }.forEach { append(it.display).append('\n').append(it.note).append('\n') }
            pack.items.filter { it.objectiveId in ids }.forEach { item ->
                append(item.prompt).append('\n').append(item.answer).append('\n').append(item.feedback).append('\n')
            }
        }
    }

    private fun learner(number: Int, book: Boolean = false) = V2LearnerView(
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
        completedStepIds = emptySet(),
        correctItemIds = emptySet(),
        bookAttestedUnits = if (book) setOf(number) else emptySet(),
        completedUnitNumbers = emptySet(),
    )

    private fun sha256(text: String): String = sha256(text.toByteArray(Charsets.UTF_8))

    private fun sha256(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        return digest.joinToString("") { byte -> "%02x".format(byte) }
    }

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
