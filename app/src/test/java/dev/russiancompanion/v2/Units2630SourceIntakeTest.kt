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
        17 to "31939e0feab01eb6726077682cc4d10f39614fe63fda2ea0ffe7b0f1ba1079d2",
        18 to "6e6faca384de88b45092fcbdfe63d86a244f4bcc8459f290c2821d05faa083ae",
        19 to "75cf1e4e1baa64f6b3025d3daa9fc63ab854d0c10fb40246e55d484077f1b6f3",
        20 to "7eca8b47f1286dc2b1718c03f4cefdfefe96f76979d9bf44616501e00faa71f3",
        21 to "53a04dc4f69a5f19af1c9d4b09984e06a3d54ba586148ecef3531a0f6e850afa",
        22 to "d408624ff0aa05793384f6a2b4d9e2a27b008225f31498795e5ccf9b3225e169",
        23 to "ea02b0454ea4c2b37095bc296f55c8eca78c42e671b4ac668a958bf2e9ab078f",
        24 to "66c55c2586ea28b908ef9bb8159c008dd119c747f39a80ca9788a10266aa9b7b",
        25 to "9bff6481102c3f713e0b70e29b0b59310e36e58634a14ea6ebf2405975808901",
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
        assertTrue(File(root, "app/src/main/assets/licenses/units-26-30-curriculum-notice.txt").readText().contains("oldid 154562152"))
        val expected = mapOf(
            "grammar" to "1369617627",
            "alphabet" to "1371795481",
            "address" to "3353858",
            "morfologiya" to "148939075",
            "neopredelennoe" to "149963314",
            "prichastie" to "153078996",
            "ostavlyaemyj" to "74231596",
            "obsuzhdaemyj" to "74233962",
            "napisannyj" to "74228228",
            "prochitannyj" to "74232159",
            "guidelines" to "92541199",
            "vechera" to "5336197",
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
            "cap-obj-u26-indefinite" to Triple(26, "obj-u26-indefinite", "indefinite-pronoun"),
            "cap-obj-u26-letter-writing" to Triple(26, "obj-u26-letter-writing", "letter-writing"),
            "cap-obj-u26-correspondence" to Triple(26, "obj-u26-correspondence", "correspondence-work"),
            "cap-obj-u27-passive" to Triple(27, "obj-u27-passive", "passive-participle"),
            "cap-obj-u27-stress" to Triple(27, "obj-u27-stress", "participle-stress"),
            "cap-obj-u28-imperfective-adverb" to Triple(28, "obj-u28-imperfective-adverb", "imperfective-verbal-adverb"),
            "cap-obj-u28-perfective-adverb" to Triple(28, "obj-u28-perfective-adverb", "perfective-verbal-adverb"),
            "cap-obj-u28-readings" to Triple(28, "obj-u28-readings", "verbal-adverb-reading"),
            "cap-obj-u29-active" to Triple(29, "obj-u29-active", "active-participle"),
            "cap-obj-u29-punctuation" to Triple(29, "obj-u29-punctuation", "participle-punctuation"),
            "cap-obj-u29-short-adjective" to Triple(29, "obj-u29-short-adjective", "short-form-adjective"),
            "cap-obj-u30-letter-names" to Triple(30, "obj-u30-letter-names", "letter-name-review"),
            "cap-obj-u30-abbreviation" to Triple(30, "obj-u30-abbreviation", "abbreviation"),
            "cap-obj-u30-particle" to Triple(30, "obj-u30-particle", "particle"),
            "cap-obj-u30-dictionary" to Triple(30, "obj-u30-dictionary", "dictionary-use"),
            "cap-obj-u30-capstone" to Triple(30, "obj-u30-capstone", "capstone-reading"),
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
            if (host.sourceId == "src-ruwikisource-vechera-5336197") {
                assertEquals("Public domain", host.licenseId)
                assertTrue(host.attribution.contains("Чернышевский"))
                assertTrue(host.attribution.contains("5723825"))
                assertFalse(host.shareAlikeTreatment.contains("CC BY-SA"))
                assertTrue(host.shareAlikeTreatment.contains("public domain"))
                assertTrue(host.sourceUrl.contains("oldid=${host.sourceRevision}"))
            } else if (host.sourceId.startsWith("src-readyrussian-")) {
                assertEquals("CC BY-SA 4.0", host.licenseId)
                assertEquals("https://creativecommons.org/licenses/by-sa/4.0/", host.licenseUrl)
                assertTrue(host.attribution.contains("ReadyRussian.org"))
                assertTrue(host.attribution.contains(host.sourceRevision))
                assertFalse(host.attribution.contains("Wikipedia:Copyrights"))
                assertFalse(host.attribution.contains("Wiktionary:Copyrights"))
                assertTrue(host.sourceUrl.contains("readyrussian.org"))
                assertNull(host.audioAssetId)
            } else {
                assertEquals("CC BY-SA 4.0", host.licenseId)
                assertEquals("https://creativecommons.org/licenses/by-sa/4.0/", host.licenseUrl)
                assertTrue(host.attribution.contains("CC BY-SA 4.0"))
                assertTrue(host.shareAlikeTreatment.contains("CC BY-SA 4.0"))
                assertTrue(host.shareAlikeTreatment.contains("Application code"))
                assertTrue(host.sourceUrl.contains("oldid="))
            }
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
            assertEquals(
                if (number == 28) "CONTENT_COMPLETE_REVIEW_PENDING" else "NOT_PREPARED",
                unit.readiness,
            )
            assertEquals("PENDING_CURRICULUM_REVIEWER", unit.mappingStatus)
            if (number != 28) {
                assertNotEquals("CONTENT_COMPLETE_REVIEW_PENDING", unit.readiness)
            }
            assertNotEquals("INSTALLABLE", unit.installState)
            assertNotEquals("INSTALLED", unit.installState)
            assertTrue(unit.sessions.any { session -> session.steps.any { !it.unavailableReason.isNullOrBlank() || !it.bookDirection.isNullOrBlank() } })
            assertEquals(preservedMappingDigests.getValue(number), mappingPackageDigest(governedMappingFacts(number)))
        }
        val blocked = advancementDecision(pack, pack.unit(26)!!, learner(26, book = true))
        assertFalse(blocked.allowed)
        assertTrue(blocked.remaining.any { it.contains("postal-address") && !it.contains("letter-writing") && !it.contains("correspondence-work") })
        assertFalse(blocked.remaining.any { it.contains("indefinite-pronoun") })
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
        assertTrue(unrelatedErrors.any { it.contains("postal-address") })
        assertFalse(V2Validator.unresolvedRequiredSourceCapabilities(pack, 26).any { it.claim == "indefinite-pronoun" })

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

    @Test
    fun neighboringSpansKeywordsAndInactiveVariancesStayClosed() {
        val pack = pack()
        assertTrue(V2Validator.validate(pack).errors.joinToString("\n"), V2Validator.validate(pack).ok)
        val unresolved = setOf(
            "postal-address",
            "bookish-register",
            "participle-comprehension",
            "bookish-reading",
        )
        assertTrue(pack.sourceCapabilities.any { it.supportedClaim == "letter-writing" })
        assertTrue(pack.sourceCapabilities.any { it.supportedClaim == "correspondence-work" })
        assertTrue(pack.sourceCapabilities.any { it.supportedClaim == "participle-stress" })
        assertTrue(pack.sourceCapabilities.any { it.supportedClaim == "verbal-adverb-reading" })
        assertTrue(pack.sourceCapabilities.any { it.supportedClaim == "dictionary-use" })
        assertTrue(pack.sourceCapabilities.any { it.supportedClaim == "capstone-reading" })
        assertTrue(pack.sourceCapabilities.none { it.supportedClaim in unresolved })

        val abbreviation = pack.sourceCapabilities.single { it.id == "cap-obj-u30-abbreviation" }
        assertTrue(abbreviation.exactSpan.contains("\u0432 \u041c\u0418\u0414 \u0438 \u0432 \u041c\u0418\u0414\u0435"))
        assertFalse(abbreviation.exactSpan.contains("\u0421\u0421\u0421\u0420"))
        val gloss = pack.provenanceRecords.first { it.shippedText == "\u0421\u0421\u0421\u0420" }
        assertTrue(
            errorsForRebound(pack, gloss, "cap-gloss-as-abbreviation", 30, "obj-u30-abbreviation", "abbreviation")
                .any { it.contains("does not state the abbreviation contrast") },
        )
        val address = pack.provenanceRecords.first { it.originalSourceText.startsWith("The lines are marked with abbreviations:") }
        assertTrue(
            errorsForRebound(pack, address, "cap-address-as-abbreviation", 30, "obj-u30-abbreviation", "abbreviation")
                .any { it.contains("does not state the abbreviation contrast") },
        )
        assertTrue(
            errorsForRebound(pack, address, "cap-address-as-postal", 26, "obj-u26-postal-address", "postal-address")
                .any { it.contains("address marks do not clear postal-address") },
        )
        val letterOnly = "\u0431\u0443\u043a\u0432\u0435\u043d\u043d\u044b\u0435 \u0430\u0431\u0431\u0440\u0435\u0432\u0438\u0430\u0442\u0443\u0440\u044b \u0438 \u0437\u0432\u0443\u043a\u043e\u0432\u044b\u0435 \u0430\u0431\u0431\u0440\u0435\u0432\u0438\u0430\u0442\u0443\u0440\u044b \u043d\u0430 \u0433\u043b\u0430\u0441\u043d\u0443\u044e"
        assertTrue(
            errorsForText(pack, letterOnly, "cap-letter-only", 30, "obj-u30-abbreviation", "abbreviation")
                .any { it.contains("does not state the abbreviation contrast") },
        )
        assertTrue(
            errorsForText(pack, "abbreviation particle dictionary postal address", "cap-keyword-abbreviation", 30, "obj-u30-abbreviation", "abbreviation")
                .any { it.contains("does not state the abbreviation contrast") },
        )
        assertTrue(
            errorsForText(pack, "\u043a\u0442\u043e-\u0442\u043e \u043a\u0442\u043e-\u043d\u0438\u0431\u0443\u0434\u044c \u043a\u043e\u0435-\u0447\u0442\u043e", "cap-inventory", 26, "obj-u26-indefinite", "indefinite-pronoun")
                .any { it.contains("indefinite inventory does not clear") },
        )
        val oneExample = "\u041f\u0440\u043e\u0433\u0440\u0430\u043c\u043c\u0430, \u043d\u0430\u043f\u0438\u0441\u0430\u043d\u043d\u0430\u044f \u0432 \u0441\u043f\u0435\u0448\u043a\u0435, \u0432\u044b\u043f\u043e\u043b\u043d\u0438\u043b\u0430 \u043d\u0435\u0434\u043e\u043f\u0443\u0441\u0442\u0438\u043c\u0443\u044e \u043e\u043f\u0435\u0440\u0430\u0446\u0438\u044e."
        assertTrue(
            errorsForText(pack, oneExample, "cap-one-example", 29, "obj-u29-punctuation", "participle-punctuation")
                .any { it.contains("does not state participle-punctuation") },
        )
        val indefinite = pack.sourceCapabilities.single { it.supportedClaim == "indefinite-pronoun" }
        val punctuation = pack.sourceCapabilities.single { it.supportedClaim == "participle-punctuation" }
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(pack, 26).none { it.claim == "indefinite-pronoun" })
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(pack, 29).none { it.claim == "participle-punctuation" })
        val brokenIndefinite = pack.copy(
            sourceCapabilities = pack.sourceCapabilities.filter { it.id != indefinite.id } + indefinite.copy(sourceHash = "0".repeat(64)),
        )
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(brokenIndefinite, 26).any { it.claim == "indefinite-pronoun" })
        val brokenPunctuation = pack.copy(
            sourceCapabilities = pack.sourceCapabilities.filter { it.id != punctuation.id } + punctuation.copy(sourceRevision = "9999999"),
        )
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(brokenPunctuation, 29).any { it.claim == "participle-punctuation" })
        val indefiniteHost = pack.provenanceRecords.first {
            it.sourceRecordId == indefinite.sourceRecordId && it.originalSourceText == indefinite.exactSpan
        }
        assertTrue(
            errorsForRebound(pack, indefiniteHost, "cap-indefinite-as-postal", 26, "obj-u26-postal-address", "postal-address")
                .any { it.contains("address marks do not clear postal-address") },
        )
        val punctuationHost = pack.provenanceRecords.first {
            it.sourceRecordId == punctuation.sourceRecordId && it.originalSourceText == punctuation.exactSpan
        }
        assertTrue(
            errorsForRebound(pack, punctuationHost, "cap-punctuation-as-register", 29, "obj-u29-register", "bookish-register")
                .any { it.contains("adverbial bookish note does not clear bookish-register") },
        )
        assertTrue(
            errorsForText(pack, "\u0444\u043e\u0440\u043c\u043e\u043e\u0431\u0440\u0430\u0437\u0443\u044e\u0449\u0438\u0435 (\u0431\u044b, \u043f\u0443\u0441\u0442\u044c, \u0434\u0430)", "cap-class-list", 30, "obj-u30-particle", "particle")
                .any { it.contains("does not state the particle contrast") },
        )
        val lunch = pack.provenanceRecords.first { it.shippedText.startsWith("\u041f\u043e\u043e\u0431\u0435\u0434\u0430\u0432") }
        assertTrue(
            errorsForRebound(pack, lunch, "cap-lunch-as-register", 29, "obj-u29-register", "bookish-register")
                .any { it.contains("adverbial bookish note does not clear bookish-register") },
        )
        val reading = errorsForRebound(pack, lunch, "cap-verbal-reading", 28, "obj-u28-readings", "verbal-adverb-reading")
        assertTrue(reading.joinToString("\n"), reading.any { it.contains("bounded verbal-adverb examples do not clear verbal-adverb-reading") })
        assertTrue(reading.any { it.contains("has no reviewable instruction and check chain") })

        val dictionary = errorsForText(pack, "nominative heading", "cap-dictionary-row", 30, "obj-u30-abbreviation", "abbreviation", "src-openrussian-50e210c4")
        assertTrue(dictionary.any { it.contains("a dictionary row does not clear abbreviation") })
        val attested = errorsForText(pack, "Self-attest Lesson 30", "cap-self-attest", 30, "obj-u30-abbreviation", "abbreviation")
        assertTrue(attested.any { it.contains("book self-attestation does not clear abbreviation") })

        pack.items.filter { it.objectiveId == "obj-u30-abbreviation" || it.objectiveId == "obj-u30-particle" }.forEach { item ->
            assertEquals(item.id, 1, item.options.count { it == item.answer })
            val cyrillicOptions = item.options.map { option -> option.any { it.code in 0x0400..0x04FF } }.toSet()
            assertEquals(item.id, 1, cyrillicOptions.size)
            assertEquals(item.id, item.options.toSet().size, item.options.size)
            val assessment = item.assessment!!
            assertTrue(item.id, assessment.whyAnswerDemonstratesObjective.contains(item.answer))
            item.options.forEachIndexed { index, option ->
                if (option != item.answer) {
                    assertTrue(item.id, assessment.distractorDifference[index].contains(option))
                    assertTrue(item.id, item.distractorRationales[index].contains(option))
                }
            }
            val differences = assessment.distractorDifference.filter { it.isNotBlank() }
            assertEquals(item.id, differences.toSet().size, differences.size)
            assertTrue(item.id, differences.isNotEmpty())
        }
        val layout = pack.items.first { it.objectiveId == "obj-u30-abbreviation" && it.assessment!!.independent }
        listOf(
            "Which admitted form does the source also allow beside the shown form?",
            "Which admitted form is written beside the shown form?",
            "Which admitted form is listed with the shown form?",
            "Which admitted form is grouped with the shown form?",
            "Which admitted form is shown in the source beside this one?",
            "Which admitted form is the other source line?",
            "Which item appears in the source set?",
        ).forEach { prompt ->
            val recalled = layout.copy(prompt = prompt)
            val layoutErrors = V2Validator.validate(
                pack.copy(items = pack.items.map { if (it.id == layout.id) recalled else it }),
            ).errors
            assertTrue(prompt + "\n" + layoutErrors.joinToString("\n"), layoutErrors.any { it.contains("line-label recall") })
        }

        val particle = pack.items.first { it.objectiveId == "obj-u30-particle" && it.assessment!!.independent }
        val shownOnly = particle.copy(
            assessment = particle.assessment!!.copy(
                whyAnswerDemonstratesObjective = "The frozen line gives ${particle.promptLetter} and keeps the same sentence.",
            ),
        )
        val shownOnlyErrors = V2Validator.validate(
            pack.copy(items = pack.items.map { if (it.id == particle.id) shownOnly else it }),
        ).errors
        assertTrue(shownOnlyErrors.joinToString("\n"), shownOnlyErrors.any { it.contains("does not cite the keyed answer") })
        val otherLine = particle.copy(prompt = "Which admitted example is the other command line?")
        val otherErrors = V2Validator.validate(
            pack.copy(items = pack.items.map { if (it.id == particle.id) otherLine else it }),
        ).errors
        assertTrue(otherErrors.joinToString("\n"), otherErrors.any { it.contains("line-label recall") })
        val sourceSet = particle.copy(prompt = "Which item appears in the source set?")
        val sourceSetErrors = V2Validator.validate(
            pack.copy(items = pack.items.map { if (it.id == particle.id) sourceSet else it }),
        ).errors
        assertTrue(sourceSetErrors.joinToString("\n"), sourceSetErrors.any { it.contains("line-label recall") })
        val mixed = particle.copy(options = listOf(particle.answer, "command"))
        val mixedErrors = V2Validator.validate(
            pack.copy(items = pack.items.map { if (it.id == particle.id) mixed else it }),
        ).errors
        assertTrue(mixedErrors.joinToString("\n"), mixedErrors.any { it.contains("response type") })
        val unsupported = particle.copy(
            answer = "command",
            options = listOf("command", particle.options.first { it != particle.answer }),
            assessment = particle.assessment!!.copy(whyAnswerDemonstratesObjective = "The keyed use is command."),
        )
        val unsupportedErrors = V2Validator.validate(
            pack.copy(items = pack.items.map { if (it.id == particle.id) unsupported else it }),
        ).errors
        assertTrue(unsupportedErrors.joinToString("\n"), unsupportedErrors.any { it.contains("keyed answer is not in the frozen span") })

        val readings = pack.unit(28)!!.objectives.single { it.id == "obj-u28-readings" }
        assertEquals("RECOGNITION", readings.kind)
        assertTrue(readings.checkItemIds.size >= 2)
        assertTrue(readings.exampleIds.size >= 2)
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(28)!!.readiness)
        val unit28 = pack.unit(28)!!
        val advanced = advancementDecision(
            pack,
            unit28,
            learner(28, book = true).copy(
                completedStepIds = unit28.sessions.flatMap { it.steps }.map { it.id }.toSet(),
                correctItemIds = (
                    unit28.objectives.flatMap { it.checkItemIds } +
                        unit28.sessions.flatMap { it.steps }.flatMap { it.itemIds }
                    ).toSet(),
            ),
        )
        assertTrue(advanced.remaining.joinToString("\n"), advanced.allowed)
        assertFalse(advanced.remaining.any { it.contains("verbal-adverb-reading") })

        listOf(27, 29, 30).forEach { number ->
            assertEquals("NOT_PREPARED", pack.unit(number)!!.readiness)
            val errors = V2Validator.validate(claimReady(pack, number)).errors
            assertTrue(errors.joinToString("\n"), errors.any { it == "Unit $number is blocked by an unresolved authority variance" })
        }
        pack.provenanceRecords.filter { it.unitNumber == 30 && it.sourceId.startsWith("src-ruwiki-") }.forEach { record ->
            assertTrue(record.attribution.contains("oldid 154562152"))
            assertTrue(record.attribution.contains("CC BY-SA 4.0"))
            assertFalse(record.attribution.contains("Wikipedia:Copyrights"))
            assertFalse(record.attribution.contains("Wikibooks:Copyrights"))
            assertFalse(record.attribution.contains("Wiktionary:Copyrights"))
        }
    }

    @Test
    fun readingAndCorrespondenceExperiencesFailClosed() {
        val pack = pack()
        assertFalse(V2Validator.ADMITTED_CORRESPONDENCE_WORK.isEmpty())
        assertTrue(V2Validator.ADMITTED_PARTICIPLE_COMPREHENSION.isEmpty())
        assertFalse(V2Validator.ADMITTED_VERBAL_ADVERB_READING.isEmpty())
        assertTrue(V2Validator.ADMITTED_BOOKISH_READING.isEmpty())

        val unit28 = V2Validator.validate(claimReady(pack, 28)).errors
        assertFalse(unit28.joinToString("\n"), unit28.any { it.contains("verbal-adverb-reading") })
        assertFalse(unit28.any { it.contains("unresolved authority variance") })
        assertTrue(unit28.joinToString("\n"), unit28.isEmpty())
        val unit26 = V2Validator.validate(claimReady(pack, 26)).errors
        assertTrue(unit26.joinToString("\n"), unit26.any { it.contains("postal-address") })
        assertFalse(unit26.any { it.contains("correspondence-work") })
        val unit26Claims = V2Validator.unresolvedRequiredSourceCapabilities(pack, 26).map { it.claim }
        assertEquals(listOf("postal-address"), unit26Claims)
        val unit27 = V2Validator.validate(claimReady(pack, 27)).errors
        assertTrue(unit27.any { it == "Unit 27 is blocked by an unresolved authority variance" })
        assertTrue(unit27.any { it.contains("participle-comprehension") })
        val unit27WithoutVariance = V2Validator.unresolvedLateUnitAdvancementBlockers(pack, pack.unit(27)!!, emptySet())
        assertTrue(unit27WithoutVariance.any { it.contains("participle-comprehension") })
        assertFalse(unit27WithoutVariance.any { it.contains("authority variance") })
        val unit29 = V2Validator.validate(claimReady(pack, 29)).errors
        assertTrue(unit29.any { it == "Unit 29 is blocked by an unresolved authority variance" })
        assertTrue(unit29.any { it.contains("bookish-reading") })
        val unit29WithoutVariance = V2Validator.unresolvedLateUnitAdvancementBlockers(pack, pack.unit(29)!!, emptySet())
        assertTrue(unit29WithoutVariance.any { it.contains("bookish-reading") })
        assertFalse(unit29WithoutVariance.any { it.contains("authority variance") })

        val approved = pack.copy(
            unitReviews = pack.unitReviews.map { review ->
                if (review.unitNumber == 28) review.copy(status = "APPROVED") else review
            },
        )
        assertFalse(V2Validator.unresolvedRequiredSourceCapabilities(approved, 28).any { it.claim == "verbal-adverb-reading" })
        assertFalse(V2Validator.unresolvedRequiredSourceCapabilities(claimReady(pack, 28), 28).any { it.claim == "verbal-adverb-reading" })
        assertTrue(pack.unit(28)!!.objectives.any { it.checkItemIds.isNotEmpty() })
        assertFalse(V2Validator.unresolvedRequiredSourceCapabilities(pack, 28).any { it.claim == "verbal-adverb-reading" })

        val attested = errorsForText(pack, "Self-attest Lesson 26", "cap-attest-correspondence", 26, "obj-u26-correspondence", "correspondence-work")
        assertTrue(attested.any { it.contains("book self-attestation does not clear correspondence-work") })
        assertTrue(attested.any { it.contains("has no independent correspondence-work admission") })
        val keyword = errorsForText(pack, "correspondence work letter writing", "cap-keyword-correspondence", 26, "obj-u26-correspondence", "correspondence-work")
        assertTrue(keyword.any { it.contains("has no independent correspondence-work admission") })
        val comprehension = errorsForText(pack, "participle comprehension passage", "cap-keyword-comprehension", 27, "obj-u27-comprehension", "participle-comprehension")
        assertTrue(comprehension.any { it.contains("has no independent participle-comprehension admission") })
        val bookish = errorsForText(pack, "bookish reading passage", "cap-keyword-bookish", 29, "obj-u29-reading", "bookish-reading")
        assertTrue(bookish.any { it.contains("has no independent bookish-reading admission") })

        val readingSpan = pack.sourceCapabilities.single { it.supportedClaim == "verbal-adverb-reading" }.exactSpan
        val probe = pack.provenanceRecords.first { it.unitNumber == 28 && it.originalSourceText != readingSpan }
        val matched = V2Validator.LateExperienceAdmission(
            sourceId = probe.sourceId,
            sourceRevision = probe.sourceRevision,
            sourceRecordId = probe.sourceRecordId,
            sourceHash = probe.sourceHash,
            exactSpan = probe.originalSourceText,
            unitNumber = 28,
            objectiveId = "obj-u28-readings",
            governedClaim = "verbal-adverb-reading",
        )
        val candidate = capability(probe, "cap-local-reading", 28, "obj-u28-readings", "verbal-adverb-reading").copy(
            sourceId = matched.sourceId,
            sourceRevision = matched.sourceRevision,
            sourceRecordId = matched.sourceRecordId,
            sourceHash = matched.sourceHash,
            exactSpan = matched.exactSpan,
        )
        assertFalse(V2Validator.admitsLateExperience(candidate, V2Validator.ADMITTED_VERBAL_ADVERB_READING, "verbal-adverb-reading"))
        assertTrue(V2Validator.admitsLateExperience(candidate, setOf(matched), "verbal-adverb-reading"))
        assertFalse(V2Validator.admitsLateExperience(candidate, setOf(matched.copy(sourceHash = "0".repeat(64))), "verbal-adverb-reading"))
        assertFalse(V2Validator.admitsLateExperience(candidate.copy(objectiveId = "obj-u28-perfective-adverb"), setOf(matched), "verbal-adverb-reading"))
        val admittedButUnchecked = errorsForRebound(pack, probe, "cap-admitted-unchecked", 28, "obj-u28-readings", "verbal-adverb-reading")
        assertTrue(admittedButUnchecked.any { it.contains("has no reviewable instruction and check chain") })
    }

    private fun errorsForRebound(
        pack: V2CurriculumPack,
        record: V2ProvenanceRecord,
        id: String,
        number: Int,
        objectiveId: String,
        claim: String,
    ): List<String> {
        val rebound = record.copy(itemId = "prov-$id", unitNumber = number, objectiveId = objectiveId)
        val added = capability(rebound, id, number, objectiveId, claim)
        return V2Validator.validate(
            pack.copy(
                provenanceRecords = pack.provenanceRecords + rebound,
                sourceCapabilities = pack.sourceCapabilities + added,
            ),
        ).errors
    }

    private fun errorsForText(
        pack: V2CurriculumPack,
        text: String,
        id: String,
        number: Int,
        objectiveId: String,
        claim: String,
        sourceId: String = "src-ruwiki-morfologiya-148939075",
    ): List<String> {
        val record = pack.provenanceRecords.first().copy(
            itemId = "prov-$id",
            unitNumber = number,
            objectiveId = objectiveId,
            sourceId = sourceId,
            sourceRevision = "148939075",
            sourceRecordId = "morfologiya:$id",
            originalSourceText = text,
            shippedText = text,
            sourceHash = sha256(text),
            contentHash = sha256(text),
            sourceUrl = "https://ru.wikipedia.org/w/index.php?title=%D0%9C%D0%BE%D1%80%D1%84%D0%BE%D0%BB%D0%BE%D0%B3%D0%B8%D1%8F_%D1%80%D1%83%D1%81%D1%81%D0%BA%D0%BE%D0%B3%D0%BE_%D1%8F%D0%B7%D1%8B%D0%BA%D0%B0&oldid=148939075",
        )
        val added = capability(record, id, number, objectiveId, claim)
        return V2Validator.validate(
            pack.copy(
                provenanceRecords = pack.provenanceRecords + record,
                sourceCapabilities = pack.sourceCapabilities + added,
            ),
        ).errors
    }

    private fun V2SourceCapability.sourceUrlBound(pack: V2CurriculumPack): Boolean {
        return pack.provenanceRecords.any {
            it.sourceId == sourceId && it.sourceRevision == sourceRevision && (
                it.sourceUrl.contains("oldid=$sourceRevision") ||
                    (it.sourceId.startsWith("src-readyrussian-") &&
                        it.sourceUrl.contains("readyrussian.org") &&
                        sourceRevision.length == 64 &&
                        it.attribution.contains(sourceRevision))
                )
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
