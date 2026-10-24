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
        // Unit 20 directionality implementation 1bc59ded intentionally supersedes historical bb408265fb5239daeb86d43c007061bff330e02a0a9afc1aa677526ccf57302b.
        20 to "6c11d3dd467846f5aabf034024f7d27d3ed9a5cc9eec4a8462cd8be3c54069eb",
        21 to "987a27a7a3de52fa7965c6b81b5ef3ab74221b62d96f52b98e5b093c8e39ae56",
        22 to "886c572c8d1adf2e0e508fe20e872afd81c49510db28bf46d2c16823c5f83f08",
        23 to "e7cf730b6ed8317979d7c9f59c23b360f28081d0461fe7a0864643a3d855f683",
        24 to "d5cb2b37f1000c8728b529e4167f3ab06fb216cb12c7a6d317531f1b309c2e66",
        25 to "57470dadfa8a6184a38538a4bae84430e8e6d6ba2ad491d1e926690777050142",
    )

    private val preservedMappingDigests = mapOf(
        26 to "c15db22cc82dcfa6c49d6d0e50beea0f90b16dfc4db90777623e99d540790489",
        27 to "3924f3a8ac211579b4db3e387cb48632d27e9b41925e227b63b004bf652a3bc0",
        28 to "0fb9199bfef05acc54935be340710bd53e19fcd6066349300e09c29fb4b035e8",
        29 to "a7ebe370698a0dffe4f0311ccfbfff6f217171ffab7dda232447d4b62e2b4071",
        30 to "97f4c8beeb1d0e6827b94bedc1e379381c763c2c3522013890b75802630440a2",
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
            "cap-obj-u26-postal-address" to Triple(26, "obj-u26-postal-address", "postal-address"),
            "cap-obj-u27-passive" to Triple(27, "obj-u27-passive", "passive-participle"),
            "cap-obj-u27-stress" to Triple(27, "obj-u27-stress", "participle-stress"),
            "cap-obj-u27-comprehension" to Triple(27, "obj-u27-comprehension", "participle-comprehension"),
            "cap-obj-u28-imperfective-adverb" to Triple(28, "obj-u28-imperfective-adverb", "imperfective-verbal-adverb"),
            "cap-obj-u28-perfective-adverb" to Triple(28, "obj-u28-perfective-adverb", "perfective-verbal-adverb"),
            "cap-obj-u28-readings" to Triple(28, "obj-u28-readings", "verbal-adverb-reading"),
            "cap-obj-u29-active" to Triple(29, "obj-u29-active", "active-participle"),
            "cap-obj-u29-punctuation" to Triple(29, "obj-u29-punctuation", "participle-punctuation"),
            "cap-obj-u29-register" to Triple(29, "obj-u29-register", "bookish-register"),
            "cap-obj-u29-reading" to Triple(29, "obj-u29-reading", "bookish-reading"),
            "cap-obj-u29-short-adjective" to Triple(29, "obj-u29-short-adjective", "short-form-adjective"),
            "cap-obj-u30-letter-names" to Triple(30, "obj-u30-letter-names", "letter-name-review"),
            "cap-obj-u30-abbreviation" to Triple(30, "obj-u30-abbreviation", "abbreviation"),
            "cap-obj-u30-particle" to Triple(30, "obj-u30-particle", "particle"),
            "cap-obj-u30-capstone" to Triple(30, "obj-u30-capstone", "capstone-reading"),
            "cap-obj-u30-dictionary" to Triple(30, "obj-u30-dictionary", "dictionary-use"),
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
            } else if (host.sourceId == "src-content-audit-research") {
                assertContentAuditResearch(host)
            } else if (host.sourceId == "src-original-research-synthesis") {
                assertEquals("Original app text", host.licenseId)
                assertEquals("urn:russian-companion:license:original-app-text", host.licenseUrl)
                assertTrue(host.attribution.contains("original research synthesis"))
                assertTrue(host.attribution.contains(host.sourceRevision))
                assertTrue(host.attribution.contains("No Penguin prose"))
                assertTrue(host.shareAlikeTreatment.contains("not a CC BY-SA quotation"))
                assertEquals("urn:russian-companion:original-research-synthesis:${host.sourceRevision}", host.sourceUrl)
            } else {
                assertEquals("CC BY-SA 4.0", host.licenseId)
                assertEquals("https://creativecommons.org/licenses/by-sa/4.0/", host.licenseUrl)
                assertTrue(host.attribution.contains("CC BY-SA 4.0"))
                assertTrue(host.shareAlikeTreatment.contains("CC BY-SA 4.0"))
                assertTrue(host.shareAlikeTreatment.contains("Application code"))
                assertTrue(host.sourceUrl.contains("oldid="))
            }
            assertEquals("IMPLEMENTED_PENDING_FINAL_AUDIT", host.reviewStatus)
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
            assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", unit.readiness)
            assertEquals("PENDING_CURRICULUM_REVIEWER", unit.mappingStatus)
            assertNotEquals("INSTALLABLE", unit.installState)
            assertNotEquals("INSTALLED", unit.installState)
            assertTrue(unit.sessions.any { session -> session.steps.any { !it.unavailableReason.isNullOrBlank() || !it.bookDirection.isNullOrBlank() } })
            assertEquals(preservedMappingDigests.getValue(number), mappingPackageDigest(governedMappingFacts(number)))
        }
        val blocked = advancementDecision(pack, pack.unit(26)!!, learner(26, book = true))
        assertFalse(blocked.allowed)
        assertFalse(blocked.remaining.any { it.contains("not content-complete") || it.contains("source capabilit") })
        assertFalse(blocked.remaining.any { it == "This unit is not prepared" })
        assertTrue(blocked.remaining.any { it.contains("instruction") || it.contains("Demonstrate") })
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
        val live = pack()
        // Preserve the original frozen-source mutation coverage after the live claim moves to pending contextual synthesis.
        val historical = live.provenanceRecords.first { it.sourceHash == "30e7e08a32bd5ca619593ff841bc22fff849a3ce2fc516d1f0cf67ea0c8e5bb6" }
        val host = live.sourceCapabilities.single { it.id == "cap-obj-u26-word-order" }.copy(
            sourceId=historical.sourceId,sourceRevision=historical.sourceRevision,sourceRecordId=historical.sourceRecordId,
            sourceHash=historical.sourceHash,exactSpan=historical.originalSourceText,
        )
        val pack = live.copy(sourceCapabilities=live.sourceCapabilities.map { if(it.id==host.id)host else it })
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
        assertFalse(unrelatedErrors.joinToString("\n"), unrelatedErrors.any { it.contains("postal-address") })
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
        // A verified primary-reference URL in research metadata is not a bundled PDF.
        val cornellReference = "https://russian.cornell.edu/russian.web/courses/121/Leed_Less_9-12.pdf"
        val upuReference = "https://www.upu.int/UPU/media/upu/PostalEntitiesFiles/addressingUnit/rusEn.pdf"
        assertFalse(curriculum.replace(cornellReference, "").replace(upuReference, "").contains(".pdf"))
        assertTrue(pack.provenanceRecords.filter { it.originalSourceText.contains(upuReference) }.all {
            it.itemId.startsWith("prov-ca-u26-postal-") && it.unitNumber == 26 &&
                it.objectiveId == "obj-u26-postal-address" && it.sourceId == "src-content-audit-research"
        })
        assertTrue(pack.provenanceRecords.filter { it.originalSourceText.contains(cornellReference) }.all {
            ((it.itemId.startsWith("prov-ca-u6-worked-") && it.unitNumber == 6 && it.objectiveId == "obj-u6-possessive") ||
                (it.itemId.startsWith("prov-ca-u7-worked-") && it.unitNumber == 7 && it.objectiveId == "obj-u7-spelling")) &&
                it.sourceId == "src-content-audit-research"
        })
        assertTrue(pack.provenanceRecords.none { it.shippedText.contains(".pdf") })
        assertTrue(pack.units.flatMap { it.sessions }.flatMap { it.steps }.none { step ->
            listOf(step.title, step.body, step.bookDirection.orEmpty(), step.companionDirection.orEmpty())
                .any { it.contains(".pdf") }
        })
        assertTrue(pack.items.none { item ->
            (listOf(item.prompt, item.promptLetter.orEmpty(), item.feedback, item.answer) +
                item.options + item.distractorRationales).any { it.contains(".pdf") }
        })
        assertTrue(pack.examples.none { it.display.contains(".pdf") || it.note.contains(".pdf") })
        assertFalse(File(root, "app/src/main/assets").walkTopDown().any {
            it.isFile && it.extension.equals("pdf", ignoreCase = true)
        })
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
            "2736372535c82dc5a2c5ac42ab42fad0756169095f799da758c36c88f9b2af4c",
            sha256(File(root, "docs/governance/MASTER_AUTHORITY_v2.0.md").readBytes()),
        )
        val assessment = assessRepositoryRelease(root)
        assertEquals(ReleaseDecision.NOT_APPROVED, assessment.decision)
        assertTrue(assessment.blockers.joinToString("\n") { it.message }, assessment.blockers.any {
            it.message.contains("AT-031") || it.message.contains("not approved") || it.name.contains("FROZEN")
        })
        // These later teaching corrections have explicit sources; every other pin stays current.
        val corrections = mapOf(
            4 to "e6db0bdfe0bbb40edf190623e2123014946f305b",
            7 to "b7e53230e2119b1e57d781b1e1b955f074a38601",
            8 to "fe919656bfa2775b5cc2c22f2496d4258343cfea",
            10 to "7efff9e18328c612de235236d98e834bb85cd1db",
            15 to "0f8001a8ec2cb76a7bc994a2c8d77db09a272d04",
        )
        val prior = RecordedIntakeCorrectionScope.load(root, "49a926c5ace0459edf7712ca5d90db4a63ff9d31")
        preservedContentDigests.forEach { (number, digest) ->
            val correction = corrections[number]
            if (correction == null) {
                if (number in setOf(5, 6)) {
                    RecordedCurriculumConservationScope.beforeUnit5GrammarReview(root,pack).let { pack ->
                        assertEquals("unit $number", digest, V2Validator.unitContentReviewDigest(pack, number))
                    }
                    assertNotEquals("Current versioned review identity for Unit $number", digest,
                        V2Validator.unitContentReviewDigest(pack, number))
                } else {
                    assertEquals("unit $number", digest, V2Validator.unitContentReviewDigest(pack, number))
                }
            } else {
                assertEquals("historical unit $number", digest, V2Validator.unitContentReviewDigest(prior, number))
                if (number == 7) {
                    // Preserve the original teaching pin; only the versioned review declaration differs.
                    RecordedCurriculumConservationScope.beforeUnit7GenderReview(root, pack).let { pack ->
                RecordedIntakeCorrectionScope.assertCurrentUnitMatches(pack,
                    RecordedIntakeCorrectionScope.load(root, correction), number)
                    }
                    RecordedIntakeCorrectionScope.assertCurrentUnitMatches(pack,
                        RecordedIntakeCorrectionScope.load(root, RecordedCurriculumConservationScope.currentPackImplementation), number)
                    assertNotEquals(V2Validator.unitContentReviewDigest(RecordedIntakeCorrectionScope.load(root, correction), number),
                        V2Validator.unitContentReviewDigest(pack, number))
                } else {
                RecordedIntakeCorrectionScope.assertCurrentUnitMatches(pack,
                    RecordedIntakeCorrectionScope.load(root, correction), number)
                }
                assertNotEquals(digest, V2Validator.unitContentReviewDigest(pack, number))
            }
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
        val resolvedLate = setOf(
            "postal-address",
            "bookish-register",
            "participle-stress",
            "participle-comprehension",
            "bookish-reading",
            "dictionary-use",
        )
        assertTrue(pack.sourceCapabilities.any { it.supportedClaim == "letter-writing" })
        assertTrue(pack.sourceCapabilities.any { it.supportedClaim == "correspondence-work" })
        assertTrue(pack.sourceCapabilities.any { it.supportedClaim == "participle-stress" })
        assertTrue(pack.sourceCapabilities.any { it.supportedClaim == "verbal-adverb-reading" })
        assertTrue(pack.sourceCapabilities.any { it.supportedClaim == "dictionary-use" })
        assertTrue(pack.sourceCapabilities.any { it.supportedClaim == "capstone-reading" })
        assertEquals(resolvedLate, pack.sourceCapabilities.map { it.supportedClaim }.filter { it in resolvedLate }.toSet())
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(pack, 26).none { it.claim in resolvedLate })
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(pack, 27).none { it.claim in resolvedLate })
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(pack, 29).none { it.claim in resolvedLate })
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(pack, 30).none { it.claim in resolvedLate })

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
        val advanced = dev.russiancompanion.v2.curriculum.ordinaryUnitRequirements(
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
            val advancedFull = advancementDecision(
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
            assertFalse(advancedFull.allowed)
            assertEquals(listOf("Complete the assigned earlier-objective review"), advancedFull.remaining)
        assertFalse(advanced.remaining.any { it.contains("verbal-adverb-reading") })

        listOf(27, 29, 30).forEach { number ->
            assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(number)!!.readiness)
            assertTrue(V2Validator.activatedAuthorityVariance(number))
            val errors = V2Validator.validate(claimReady(pack, number)).errors
            assertFalse(errors.joinToString("\n"), errors.any { it.contains("unresolved authority variance") })
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
        assertTrue(unit26.joinToString("\n"), unit26.isEmpty())
        assertFalse(unit26.any { it.contains("postal-address") })
        assertFalse(unit26.any { it.contains("correspondence-work") })
        val unit26Claims = V2Validator.unresolvedRequiredSourceCapabilities(pack, 26).map { it.claim }
        assertEquals(emptyList<String>(), unit26Claims)
        val unit27 = V2Validator.validate(claimReady(pack, 27)).errors
        assertFalse(unit27.any { it.contains("unresolved authority variance") })
        assertFalse(unit27.any { it.contains("participle-comprehension") })
        assertFalse(unit27.any { it.contains("participle-stress") })
        val unit27WithoutVariance = V2Validator.unresolvedLateUnitAdvancementBlockers(pack, pack.unit(27)!!, emptySet())
        assertTrue(unit27WithoutVariance.joinToString("\n"), unit27WithoutVariance.isEmpty())
        val unit29 = V2Validator.validate(claimReady(pack, 29)).errors
        assertFalse(unit29.any { it.contains("unresolved authority variance") })
        assertFalse(unit29.any { it.contains("bookish-reading") })
        assertFalse(unit29.any { it.contains("bookish-register") })
        val unit29WithoutVariance = V2Validator.unresolvedLateUnitAdvancementBlockers(pack, pack.unit(29)!!, emptySet())
        assertTrue(unit29WithoutVariance.joinToString("\n"), unit29WithoutVariance.isEmpty())

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
                (it.sourceId == "src-content-audit-research" &&
                    it.sourceUrl == "urn:russian-companion:content-audit-research:2026-10-08" &&
                    sourceRevision == "content-audit-2026-10-08" &&
                    it.synthesis != null) ||
                it.sourceUrl.contains("oldid=$sourceRevision") ||
                    (it.sourceId.startsWith("src-readyrussian-") &&
                        it.sourceUrl.contains("readyrussian.org") &&
                        sourceRevision.length == 64 &&
                        it.attribution.contains(sourceRevision)) ||
                    (it.sourceId == "src-original-research-synthesis" &&
                        it.sourceUrl == "urn:russian-companion:original-research-synthesis:$sourceRevision" &&
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
