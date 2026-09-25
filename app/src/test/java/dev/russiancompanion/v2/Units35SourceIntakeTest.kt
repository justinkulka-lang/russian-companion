package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.V2AuthoredEnglishRecord
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.unit
import dev.russiancompanion.v2.review.buildUnitReviewPackage
import dev.russiancompanion.v2.review.mappingPackageDigest
import dev.russiancompanion.v2.review.governedMappingFacts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class Units35SourceIntakeTest {
    @Test
    fun everyRussianCandidateHasAnAdmittedImmutableSourceAndRights() {
        val pack = pack()
        val validation = V2Validator.validate(pack)
        assertTrue(validation.errors.joinToString("\n"), validation.ok)
        assertTrue(pack.provenanceRecords.isNotEmpty())
        val admitted = setOf(
            "src-openrussian-50e210c4",
            "src-wikibooks-russian-cc-by-sa",
            "src-wikipedia-russian-grammar-1369617627",
            "src-wikipedia-russian-alphabet-1371795481",
            "src-wikipedia-adverb-1374548415",
            "src-ruwiki-narechie-152437423",
            "src-wikipedia-date-time-country-1366690592",
            "src-wikipedia-date-notation-russia-1357134172",
            "src-enwiktionary-dolzhen-84965088",
            "src-pressbooks-diverse-russian-traditions-grammar",
            "src-wikibooks-russian-various-stuff-3353858",
        )
        pack.provenanceRecords.forEach { record ->
            assertTrue(record.itemId, record.sourceId in admitted)
            if (record.unitNumber in 26..30) {
                assertTrue(record.itemId, record.sourceUrl.contains("oldid=${record.sourceRevision}"))
            }
            assertTrue(record.sourceRevision.isNotBlank())
            assertTrue(record.sourceUrl.isNotBlank())
            assertTrue(record.attribution.isNotBlank())
            assertEquals("CC BY-SA 4.0", record.licenseId)
            assertEquals("", record.reviewerIdentity)
            assertEquals("", record.reviewerQualification)
            assertEquals("", record.reviewDate)
            assertEquals("", record.reviewedDigest)
            assertEquals(null, record.audioAssetId)
            assertTrue(pack.sources.any { it.id == record.sourceId && it.license == "CC BY-SA 4.0" })
            assertTrue(pack.rights.any { it.sourceId == record.sourceId && it.permitsAppUse && it.license == "CC BY-SA 4.0" })
        }
        assertEquals(
            "3f4f2543804372be1d6ef62fb280d452bc017fbadc0dd6534e863c86be50050c",
            mappingPackageDigest(governedMappingFacts(1)),
        )
        assertEquals(
            "090a3fec638b8a19d34e11d8be88a7796ce18a56a87b7b5b06d626ed777d6448",
            mappingPackageDigest(governedMappingFacts(3)),
        )
        assertEquals(
            "3760c217663b5b3f037dd9f8d85a5e1a51c320861191d59a01ae6c2c9b4ee4ef",
            mappingPackageDigest(governedMappingFacts(4)),
        )
        assertEquals(
            "a1affd13da11e1173c23a2cab33b22f7ad6ea99aa54c99d54fc945fd6d7d0806",
            mappingPackageDigest(governedMappingFacts(5)),
        )
        assertNotEquals(buildUnitReviewPackage(pack, 3).contentReviewDigest, buildUnitReviewPackage(pack, 4).contentReviewDigest)
        assertNotEquals(buildUnitReviewPackage(pack, 4).contentReviewDigest, buildUnitReviewPackage(pack, 5).contentReviewDigest)
        assertNotEquals(buildUnitReviewPackage(pack, 3).reviewPackageDigest, buildUnitReviewPackage(pack, 4).reviewPackageDigest)
        assertTrue(buildUnitReviewPackage(pack, 3).records.any { it.type == "PROVENANCE" })
        assertTrue(buildUnitReviewPackage(pack, 3).records.any { it.type == "AUTHORED_ENGLISH" && it.classification == "INDEPENDENTLY_AUTHORED_SCAFFOLD" })
        assertTrue(buildUnitReviewPackage(pack, 1).records.none { it.type == "AUTHORED_ENGLISH" })
        assertTrue(buildUnitReviewPackage(pack, 3).unresolved.isEmpty())
        assertTrue(pack.provenanceRecords.filter { it.sourceId == "src-openrussian-50e210c4" }.all { "/blob/" in it.sourceUrl && "/tree/" !in it.sourceUrl })
    }

    @Test
    fun learnerGlossesAreDelimitedSensesAndEnglishClaimsFailClosed() {
        val pack = pack()
        listOf(
            "prov-u3-gloss-чай" to "tea",
            "prov-u3-gloss-хлеб" to "bread",
            "prov-u4-pronoun-gloss-она" to "she",
        ).forEach { (id, sense) ->
            val record = pack.provenanceRecords.first { it.itemId == id }
            assertEquals("sense selection", record.transformation)
            assertEquals(sense, record.shippedText)
            assertEquals("PENDING_QUALIFIED_REVIEW", record.reviewStatus)
            assertTrue(record.originalSourceText.contains(","))
            assertTrue(record.shippedText != record.originalSourceText)
        }
        val learner = pack.units.filter { it.number in 3..5 }.flatMap { it.sessions }.flatMap { it.steps }
            .joinToString("\n") { "${it.title}\n${it.body}\n${it.bookDirection.orEmpty()}" }
        assertFalse(learner.contains("methinks"))
        assertFalse(learner.contains("she, it, he"))
        assertFalse(learner.contains("auscultate"))
        val gender = pack.unit(3)!!.sessions.first { it.id == "u3-s-gender" }
        val teaches = gender.steps.filter { it.kind == "TEACH" }
        assertTrue(teaches.size >= 4)
        assertTrue(teaches.first().body.length < 500)
        assertFalse(teaches.first().body.contains("хлеб"))
        assertFalse(teaches.first().body.contains("молоко"))
        val kinds = gender.steps.map { it.kind }
        kinds.forEachIndexed { index, kind ->
            if (kind == "GUIDED") assertEquals("TEACH", kinds[index - 1])
        }
        val poisoned = pack.copy(
            units = pack.units.map { unit ->
                if (unit.number != 3) unit else unit.copy(
                    sessions = unit.sessions.map { session ->
                        session.copy(
                            steps = session.steps.map { step ->
                                if (step.kind != "TEACH") step else step.copy(body = step.body + " Russian has ten genders.")
                            },
                        )
                    },
                )
            },
        )
        assertTrue(V2Validator.validate(poisoned).errors.any { it.contains("English linguistic claim has no admitted source record") })
        val smuggled = pack.copy(
            authoredEnglishRecords = pack.authoredEnglishRecords + V2AuthoredEnglishRecord(
                id = "en-smuggle",
                unitNumber = 3,
                text = "Russian has ten genders",
                classification = "INDEPENDENTLY_AUTHORED_SCAFFOLD",
            ),
        )
        assertTrue(V2Validator.validate(smuggled).errors.any { it.contains("independently authored English makes a linguistic claim") })
        val unitThree = V2Validator.unitContentReviewDigest(pack, 3)
        val edited = pack.copy(
            authoredEnglishRecords = pack.authoredEnglishRecords.map { record ->
                if (record.unitNumber != 3) record else record.copy(text = record.text + " Please continue.")
            },
        )
        assertNotEquals(unitThree, V2Validator.unitContentReviewDigest(edited, 3))
        assertEquals(V2Validator.unitContentReviewDigest(pack, 1), V2Validator.unitContentReviewDigest(edited, 1))
        assertEquals(V2Validator.unitContentReviewDigest(pack, 2), V2Validator.unitContentReviewDigest(edited, 2))
    }

    @Test
    fun excludedSourcesAndUnresolvedRevisionsFailClosed() {
        val pack = pack()
        val base = pack.provenanceRecords.first { it.unitNumber == 3 }
        val penguin = pack.copy(
            sources = pack.sources + pack.sources.first { it.id == base.sourceId }.copy(id = "src-penguin-prose", title = "penguin excerpt"),
            provenanceRecords = pack.provenanceRecords + base.copy(itemId = "prov-penguin", sourceId = "src-penguin-prose"),
        )
        assertTrue(V2Validator.validate(penguin).errors.any { it.contains("excluded source cannot enter the seed") })

        val fsi = pack.copy(provenanceRecords = pack.provenanceRecords + base.copy(itemId = "prov-fsi", sourceId = "src-fsi-mirror"))
        assertTrue(V2Validator.validate(fsi).errors.any { it.contains("excluded source cannot enter the seed") })

        val nc = pack.copy(provenanceRecords = pack.provenanceRecords + base.copy(itemId = "prov-nc", licenseId = "CC BY-NC-SA 4.0"))
        assertTrue(V2Validator.validate(nc).errors.any { it.contains("incompatible license") })

        val unresolved = pack.copy(provenanceRecords = pack.provenanceRecords + base.copy(itemId = "prov-unresolved", sourceRevision = ""))
        assertTrue(V2Validator.validate(unresolved).errors.any { it.contains("source revision is unresolved") })

        val unsupported = pack.copy(
            units = pack.units.map { unit ->
                if (unit.number != 3) unit else unit.copy(
                    sessions = unit.sessions.map { session ->
                        session.copy(
                            steps = session.steps.map { step ->
                                if (step.id == "u3-st-orient") step.copy(body = step.body + " привет") else step
                            },
                        )
                    },
                )
            },
        )
        assertTrue(V2Validator.validate(unsupported).errors.any { it.contains("Russian text has no admitted source record") })
    }

    @Test
    fun sourceMutationChangesOnlyTheAffectedDigestAndReviewerFieldsDoNot() {
        val pack = pack()
        val unitThree = V2Validator.unitContentReviewDigest(pack, 3)
        val unitFour = V2Validator.unitContentReviewDigest(pack, 4)
        val unitFive = V2Validator.unitContentReviewDigest(pack, 5)
        val record = pack.provenanceRecords.first { it.unitNumber == 3 }
        val mutated = pack.copy(
            provenanceRecords = pack.provenanceRecords.map { current ->
                if (current.itemId != record.itemId) current else current.copy(shippedText = current.shippedText + "x")
            },
        )
        assertNotEquals(unitThree, V2Validator.unitContentReviewDigest(mutated, 3))
        assertEquals(unitFour, V2Validator.unitContentReviewDigest(mutated, 4))
        assertEquals(unitFive, V2Validator.unitContentReviewDigest(mutated, 5))
        assertNotEquals(V2Validator.contentReviewDigest(pack), V2Validator.contentReviewDigest(mutated))

        val signed = pack.copy(
            provenanceRecords = pack.provenanceRecords.map { current ->
                current.copy(
                    reviewerIdentity = "Pat Reviewer",
                    reviewerQualification = "Attested",
                    reviewDate = "2026-09-23",
                    reviewedDigest = "abc",
                )
            },
        )
        assertEquals(unitThree, V2Validator.unitContentReviewDigest(signed, 3))
        assertEquals(unitFour, V2Validator.unitContentReviewDigest(signed, 4))
        assertEquals(unitFive, V2Validator.unitContentReviewDigest(signed, 5))
        assertEquals(V2Validator.contentReviewDigest(pack), V2Validator.contentReviewDigest(signed))

        val item = pack.items.first { it.id.startsWith("u3-") }
        val feedback = pack.copy(items = pack.items.map { if (it.id == item.id) it.copy(feedback = it.feedback + " extra") else it })
        val answer = pack.copy(items = pack.items.map { if (it.id == item.id) it.copy(answer = it.answer + "x", options = it.options + (it.answer + "x")) else it })
        val options = pack.copy(items = pack.items.map { if (it.id == item.id) it.copy(options = it.options + "extra option") else it })
        assertNotEquals(unitThree, V2Validator.unitContentReviewDigest(feedback, 3))
        assertNotEquals(unitThree, V2Validator.unitContentReviewDigest(answer, 3))
        assertNotEquals(unitThree, V2Validator.unitContentReviewDigest(options, 3))
        assertEquals(unitFour, V2Validator.unitContentReviewDigest(feedback, 4))

        val remediation = pack.copy(
            units = pack.units.map { unit ->
                if (unit.number != 3) unit else unit.copy(
                    objectives = unit.objectives.map { objective ->
                        if (objective.remediationStepIds.isEmpty()) objective else objective.copy(
                            remediationStepIds = objective.remediationStepIds + "missing-remediation",
                        )
                    },
                )
            },
        )
        assertNotEquals(unitThree, V2Validator.unitContentReviewDigest(remediation, 3))
        assertEquals(unitFour, V2Validator.unitContentReviewDigest(remediation, 4))

        val source = pack.copy(
            sources = pack.sources.map { current ->
                if (current.id != "src-openrussian-50e210c4") current else current.copy(attribution = current.attribution + " extra")
            },
        )
        assertNotEquals(unitThree, V2Validator.unitContentReviewDigest(source, 3))
        assertEquals(V2Validator.unitContentReviewDigest(pack, 1), V2Validator.unitContentReviewDigest(source, 1))

        val audio = pack.copy(
            provenanceRecords = pack.provenanceRecords.map { current ->
                if (current.itemId != record.itemId) current else current.copy(audioAssetId = "audio-unmatched")
            },
        )
        assertNotEquals(unitThree, V2Validator.unitContentReviewDigest(audio, 3))
        assertEquals(unitFour, V2Validator.unitContentReviewDigest(audio, 4))
        assertTrue(V2Validator.validate(audio).errors.any { it.contains("audio mapping is not an exact admitted text match") })
    }

    @Test
    fun penguinMarkersStayOutAndReleaseStaysClosed() {
        val root = repo()
        val seed = File(root, "app/src/main/assets/content/v2-curriculum.json").readText()
        assertFalse(seed.contains("The New Penguin Russian Course"))
        assertFalse(seed.contains("PENGUIN_EDITION_MAPPING_INPUT"))
        assertFalse(seed.contains("mapping input v1"))
        assertTrue(File(root, "app/src/main/assets/licenses/units-3-5-curriculum-notice.txt").isFile)
        assertTrue(File(root, "app/src/main/assets/licenses/cc-by-sa-4.0.txt").isFile)
        assertFalse(File(root, "app/src/main/AndroidManifest.xml").readText().contains("android.permission.INTERNET"))
        val candidate = File(root, "release/acceptance/candidate.json").readText()
        assertTrue(candidate.contains("\"publicRelease\": \"NOT_APPROVED\""))
        assertTrue(candidate.contains("\"frozen\": false"))
        assertFalse(root.walkTopDown().any { file ->
            file.isFile && file.extension.equals("pdf", ignoreCase = true) && !file.path.contains("/.git/")
        })
        val pack = pack()
        assertFalse(pack.publicReleaseEligible)
        listOf(3, 4, 5).forEach { number ->
            assertEquals("DEVELOPMENT", pack.unit(number)!!.installState)
            assertEquals("PENDING_CURRICULUM_REVIEWER", pack.unit(number)!!.mappingStatus)
            assertEquals("PENDING_QUALIFIED_REVIEW", pack.unitReviews.first { it.unitNumber == number }.status)
        }
    }

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
