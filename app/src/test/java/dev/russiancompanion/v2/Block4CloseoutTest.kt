package dev.russiancompanion.v2

import dev.russiancompanion.v2.audio.*
import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.text.Normalizer

/**
 * T17f: Block 4 (Units 16-20) close-out. The measurements the Units 16-20 rows of audit/FINDINGS_DISPOSITION.csv cite, and the
 * T17f content fixes: WC001 (the 26 Unit 17 template rationales), WC005 (Unit 16 мужем and утром taught before the checks) and
 * WC004 (the Unit 17 count and spoken-date glosses).
 */
class Block4CloseoutTest {
    private val root = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }.canonicalFile
    private val assets = File(root, "app/src/main/assets")
    private val pack by lazy { V2PackLoader.parse(File(assets, "content/v2-curriculum.json").readText()) }
    private val units by lazy { pack.units.filter { it.number in 16..20 } }

    private fun bare(text: String) = Normalizer.normalize(Normalizer.normalize(text, Normalizer.Form.NFD).replace("́", ""),
        Normalizer.Form.NFC).lowercase().trim(' ', '.', ',', '!', '?', ';', ':')

    private fun contains(hay: String, needle: String): Boolean {
        val n = bare(needle)
        return n.isNotEmpty() && Regex("(?<![\\p{L}])${Regex.escape(n)}(?![\\p{L}])").containsMatchIn(bare(hay))
    }

    private fun required(unit: V2Unit) = unit.objectives.flatMap { it.checkItemIds }.distinct().map { pack.item(it)!! }

    @Test fun everyRequiredCheckSitsInTheEndOfUnitSessionAndItsCueOrKeyIsInItsRemediation() {
        // CC022 and CC021. Counts per unit: (required checks, cue and key in remediation, key only).
        val examples = pack.examples.associateBy { it.id }
        val measured = units.associate { unit ->
            val checks = unit.sessions.single { it.id == "u${unit.number}-s-checks" }
            assertTrue(unit.sessions.filter { it.id != checks.id }.flatMap { it.steps }.none { it.kind == "CHECK" })
            var both = 0; var keyOnly = 0
            for (objective in unit.objectives.filter { it.checkItemIds.isNotEmpty() }) {
                assertTrue(objective.id, objective.checkItemIds.all { id -> checks.steps.any { id in it.itemIds } })
                val text = objective.remediationStepIds.mapNotNull { pack.step(it)?.second }.joinToString("\n") { rem ->
                    rem.body + "\n" + rem.exampleIds.mapNotNull { examples[it] }.joinToString("\n") { it.display + " " + it.note }
                }
                for (item in objective.checkItemIds.map { pack.item(it)!! }) {
                    // A hidden answer reference (promptLetterRole ANSWER_REFERENCE, u17-i-age-2) is not a cue the learner sees.
                    val cue = item.promptLetter.orEmpty().takeIf { item.promptLetterRole == "CUE" }.orEmpty()
                        .let { cue -> cue.isNotEmpty() && contains(text, cue) }
                    val key = contains(text, item.answer)
                    assertTrue("${item.id}: neither cue nor key is in its remediation", cue || key)
                    if (cue && key) both++ else if (key) keyOnly++
                }
            }
            unit.number to listOf(required(unit).size, both, keyOnly)
        }
        assertEquals(mapOf(16 to listOf(20, 19, 1), 17 to listOf(36, 33, 3), 18 to listOf(20, 16, 4), 19 to listOf(20, 20, 0),
            20 to listOf(20, 20, 0)), measured)
    }

    @Test fun answerPositionIsDispersedOnTheLessonDisplays() {
        // CC001: authored order still puts the key first; the lesson display permutation (sha256-option-order-v1) disperses it.
        val measured = units.associate { unit ->
            val checks = unit.sessions.single { it.id == "u${unit.number}-s-checks" }.steps
            val closed = required(unit).filter { !it.isTypedResponse() }
            val shown = closed.count { item -> lessonPresentedOptions(item, checks.single { item.id in it.itemIds }.id).first() == item.answer }
            unit.number to listOf(closed.size, closed.count { it.options.first() == it.answer }, shown)
        }
        assertEquals(mapOf(16 to listOf(12, 12, 3), 17 to listOf(24, 22, 11), 18 to listOf(4, 4, 3), 19 to listOf(8, 8, 4),
            20 to listOf(8, 8, 4)), measured)
    }

    @Test fun noOptionEqualsItsCueAndNoObjectiveHasAConstantKey() {
        // CC027.
        for (unit in units) for (objective in unit.objectives.filter { it.checkItemIds.size > 1 }) {
            val items = objective.checkItemIds.map { pack.item(it)!! }
            assertTrue(objective.id, items.map { it.answer }.distinct().size > 1)
            assertTrue(objective.id, items.none { item -> item.promptLetter != null && item.promptLetter in item.options })
        }
    }

    @Test fun everyWrongOptionHasItsOwnRationaleAndNoTemplateSpliceRemains() {
        // WC001: every wrong option of a Units 16-20 closed item has a rationale; T17f rewrote the 26 Unit 17 template splices.
        val template = "is a different meaning or construction and does not match the reading of the shown form"
        var wrong = 0
        for (item in pack.items.filter { it.objectiveId.removePrefix("obj-u").substringBefore("-").toIntOrNull() in 16..20 && !it.isTypedResponse() }) {
            item.options.forEachIndexed { k, option ->
                if (option == item.answer) return@forEachIndexed
                val rationale = item.distractorRationales[k]
                assertTrue("${item.id} $option", rationale.isNotBlank() && template !in rationale)
                wrong++
            }
        }
        // T17f-n1 expectation change (owner decision D53d), old -> new: 151 -> 150 wrong options; u17-i-calendar-date-1 has two
        // options since its redesign (was three). Two of the 26 WC001 rewrites were its rationales; they left with the old check.
        assertEquals(150, wrong)
        val log = File(root, "audit/T17f-block4-changes.csv").readLines()
        assertEquals(26, log.count { it.startsWith("T17f,WC001,") && ".distractorRationales[" in it })
        assertEquals(2, log.count { it.startsWith("T17f,WC001,u17-i-calendar-date-1.distractorRationales[") })
        // A rewritten rationale names its option and says what it is.
        val item = pack.item("u17-i-clock-answers-2")!!
        assertEquals("7:15 is a quarter of the eighth hour; the shown form is eight without a quarter.",
            item.distractorRationales[item.options.indexOf("7:15")])
        assertTrue(V2Validator.validate(pack).errors.joinToString("\n"), V2Validator.validate(pack).ok)
    }

    @Test fun unit16TeachesEveryFormItsPencilCheckShowsBeforeTheChecks() {
        // WC005: мужем (a distractor) and утром (the cue) of u16-i-inst-sg-2 are on the frozen instrumental line its records quote.
        val unit = pack.unit(16)!!
        val steps = unit.sessions.flatMap { it.steps }
        val before = steps.take(steps.indexOfFirst { it.kind == "CHECK" }).joinToString("\n") { it.body }
        val item = pack.item("u16-i-inst-sg-2")!!
        for (form in item.options + item.promptLetter!!) assertTrue(form, contains(before, form))
        val line = pack.provenanceRecords.single { it.itemId == "prov-u16-inst-sg-x-1" }.originalSourceText
        assertTrue(line.contains("Husband - '''муж''' - '''мужем'''") && line.contains("Morning - '''утро''' --> '''утром'''"))
        val sentence = "мужем is instrumental singular of the noun meaning husband; утром is instrumental singular of the noun meaning morning."
        assertTrue(steps.single { it.id == "u16-st-teach-inst-sg-6" }.body.contains(sentence))
        val rem = steps.single { it.id == "u16-st-rem-inst-sg" }
        assertEquals(listOf("prov-u16-inst-sg-x-1", "prov-u16-inst-sg-x-0"), rem.russianFactSources.getValue(sentence))
    }

    @Test fun unit17ExamplesAreGlossedInEnglish() {
        // WC004: no Unit 17 example is glossed with its own Russian or with a label.
        val examples = pack.examples.filter { it.objectiveId.startsWith("obj-u17-") }
        assertTrue(examples.none { it.note.isNullOrBlank() || it.note == it.display || it.note.orEmpty().any { c -> c in 'Ѐ'..'ӿ' } })
        assertEquals("two hours", examples.single { it.id == "ex-u17-hours-1" }.note)
        assertEquals("the twenty-eighth of August", examples.single { it.id == "ex-u17-calendar-date-0" }.note)
        assertTrue(examples.none { it.note == "the month in genitive case" })
    }

    @Test fun ageChecksUseRealLearnerErrorsOrATypedKey() {
        // CC028 (owner decisions D50c, D50f): the section label "age statements" is no longer a distractor. u17-i-age-1 offers a
        // competing frozen Unit 17 count gloss that is wrong for the prompt and neither nests in nor around the key; u17-i-age-2,
        // which has no non-nested competitor on its line, is a typed check.
        val records = pack.provenanceRecords.associateBy { it.itemId }
        val age1 = pack.item("u17-i-age-1")!!
        assertEquals(listOf("I am 20 (years old)", "forty-one years"), age1.options)
        val competitor = records.getValue("prov-t17f-u17-age-forty-one-years") // D50g: bound to obj-u17-age
        assertEquals("forty-one years", competitor.shippedText)
        assertEquals(17, competitor.unitNumber)
        assertTrue(V2Validator.isFrozenSourceRecord(pack, competitor))
        assertTrue(competitor.originalSourceText.contains("forty-one years"))
        val a = age1.assessment!!
        assertTrue(competitor.itemId in a.distractorSourceIds && competitor.itemId in a.relationParticipantIds)
        assertTrue("prov-u17-age-x-1" !in a.relationParticipantIds)
        assertTrue(age1.options.none { o -> age1.options.any { it != o && it.contains(o) } })
        assertTrue(age1.options.none { '_' in it })
        assertTrue(V2Validator.featureValid(pack, age1))
        val age2 = pack.item("u17-i-age-2")!!
        assertTrue(age2.isTypedResponse())
        assertEquals("двадцать лет", age2.answer)
        assertEquals(emptyList<String>(), V2Validator.typedKeyCitationErrors(pack, age2))
        assertTrue(pack.unit(17)!!.sessions.flatMap { it.steps }.single { it.id == "u17-st-teach-age-2" }.body.contains("двадцать лет"))
        assertTrue(pack.items.filter { it.objectiveId == "obj-u17-age" && !it.id.startsWith("u17-g-") }.none { "age statements" in it.options })
        assertTrue(pack.provenanceRecords.none { it.itemId == "prov-t17f-u17-age-literal" })
        assertTrue(V2Validator.validate(pack).errors.joinToString("\n"), V2Validator.validate(pack).ok)
    }

    @Test fun everyTwoOptionRequiredCheckHasItsOwnCc036RowAndEveryFindingHasAUnits16To20Row() {
        val binary = units.flatMap { required(it) }.filter { !it.isTypedResponse() && it.options.size == 2 }.map { it.id }.toSet()
        val rows = File(root, "audit/cc036-binary-required-checks.csv").readLines().drop(1)
            .filter { it.substringBefore(",").toIntOrNull() in 16..20 }
        assertEquals(binary, rows.map { it.split(",")[1] }.toSet())
        // T17f-n1 expectation change (owner decision D53d), old -> new: 11 -> 12 rows; the redesigned u17-i-calendar-date-1 asks
        // which of the two written forms its frozen span names is shown, so it is a two-option check with its own row.
        assertEquals(12, rows.size)
        assertTrue(rows.all { ",BOUNDED_SCOPE," in it })
        val dispositions = File(root, "audit/FINDINGS_DISPOSITION.csv").readLines().filter { ",T17f," in it }.map { it.substringBefore(",") }
        val expected = listOf("CC001", "CC020", "CC021", "CC022", "CC027", "WC001", "WC005", "IA003", "IA014", "IA015", "CC005", "CC007",
            "CC026", "CC028", "CC029", "CC032", "CC035", "CC036", "WC002", "WC004", "WC008", "WC011", "D46g")
        assertEquals(expected.sorted(), dispositions.sorted())
        // CC018 and CC019 stay last; no Units 16-20 row closes them.
        assertTrue(dispositions.none { it == "CC018" || it == "CC019" })
    }

    /**
     * T17f-n1 (owner decision D53d): u17-i-calendar-date-1 asks which written form of the date is shown. The relationship is read
     * directly from the frozen date-notation span: it names exactly two written forms, each with its example, and the shown date
     * is the expanded form's example; the distractor is the other named form, with a different example.
     */
    @Test fun theDateFormCheckKeysTheFormItsFrozenSpanNamesForTheShownDate() {
        val item = pack.item("u17-i-calendar-date-1")!!
        val records = pack.provenanceRecords.associateBy { it.itemId }
        val (cue, key, distractor) = item.assessment!!.relationParticipantIds.map { records.getValue(it) }
        val span = cue.originalSourceText
        assertEquals(listOf("the expanded form", "the all-numeric date"), item.options)
        assertEquals("the expanded form", item.answer)
        assertEquals(item.promptLetter, cue.shippedText)
        assertEquals(listOf(key.shippedText, distractor.shippedText), item.options)
        assertTrue(listOf(cue, key, distractor).all { it.originalSourceText == span && it.sourceHash == cue.sourceHash && V2Validator.isFrozenSourceRecord(pack, it) })
        val named = Regex("(the all-numeric date|the expanded form) \\(for example \"\\{\\{lang\\|ru\\|([^}]+)\\}\\}\"\\)").findAll(span)
            .associate { it.groupValues[1] to it.groupValues[2] }
        assertEquals(setOf("the all-numeric date", "the expanded form"), named.keys)
        assertEquals(cue.shippedText, named.getValue(item.answer))           // the key is the form whose example is the shown date
        assertNotEquals(cue.shippedText, named.getValue(distractor.shippedText)) // the distractor's example is a different date
        assertEquals("EXPLICIT_SOURCE_PAIR", V2Validator.classifyRelation(cue, key, listOf(distractor)))
        assertTrue(V2Validator.featureValid(pack, item))
        // Both forms are taught before the check (and repeated in the remediation) in the span's own terms.
        val steps = pack.unit(17)!!.sessions.flatMap { it.steps }.associateBy { it.id }
        for (sid in listOf("u17-st-teach-calendar-date-1", "u17-st-rem-calendar-date")) {
            assertTrue(sid, steps.getValue(sid).body.contains("the expanded form") && steps.getValue(sid).body.contains("the all-numeric date"))
        }
        // A wrong key against the same span fails: the all-numeric date is not the form shown.
        val swapped = item.copy(answer = "the all-numeric date", assessment = item.assessment!!.copy(
            relationParticipantIds = listOf(cue.itemId, distractor.itemId, key.itemId), answerSourceIds = listOf(distractor.itemId)))
        assertFalse(V2Validator.validate(pack.copy(items = pack.items.map { if (it.id == item.id) swapped else it })).ok)
    }

    /**
     * T17f-n1 (owner decision D53f, CC027): every cue a Units 16-20 required closed check displays is relevant to its prompt: the
     * prompt says what the shown form is or asks about it ("shown"), the cue is a rule statement the prompt applies, or (the count
     * and ordinal checks) it is the one/first form of the same frozen set, which the unit teaches as a different quantity or
     * position not to copy. Before T17f-n1, u16-i-inst-sg-2 showed утром for a prompt that never used it, and u16-i-inst-means-1 and
     * -2 showed a third sentence of the set the same way.
     */
    @Test fun everyDisplayedCueIsRelevantToItsPrompt() {
        val records = pack.provenanceRecords.associateBy { it.itemId }
        val kinds = mutableMapOf<String, String>()
        for (item in units.flatMap { required(it) }.filter { !it.isTypedResponse() && it.promptLetterRole == "CUE" }) {
            val a = item.assessment!!
            val cue = records.getValue(a.relationParticipantIds.first())
            val key = records.getValue(a.relationParticipantIds[1])
            kinds[item.id] = when {
                Regex("\\b(?:shown|this)\\b", RegexOption.IGNORE_CASE).containsMatchIn(item.prompt) || item.prompt.contains(item.promptLetter!!) -> "PROMPT"
                a.cueLanguage == "rule" -> "RULE"
                cue.originalSourceText == key.originalSourceText && item.id.matches(Regex("u17-i-(hours|years|minutes|ordinals)-[12]")) -> "SET_REFERENCE"
                else -> "STRAY"
            }
        }
        assertEquals(emptyMap<String, String>(), kinds.filterValues { it == "STRAY" })
        assertEquals(mapOf("PROMPT" to 46, "RULE" to 2, "SET_REFERENCE" to 8), kinds.values.groupingBy { it }.eachCount().toSortedMap())
        assertEquals("The shown noun is instrumental singular. Which other instrumental singular noun means pencil?", pack.item("u16-i-inst-sg-2")!!.prompt)
        // The ordinal set reference is taught as a position not to copy.
        assertTrue(pack.unit(17)!!.sessions.flatMap { it.steps }.single { it.id == "u17-st-teach-ordinals-3" }.body
            .contains("the separately shown Russian cue may name a different position and is not the target to copy"))
    }

    /** T17f-n1 (owner decision D53f, CC028): the guided u17-g-age-0 is practice, and both of its misreadings are named in the remediation. */
    @Test fun theGuidedAgeItemsMisreadingsAreNamedInItsRemediation() {
        val item = pack.item("u17-g-age-0")!!
        assertTrue(pack.units.none { u -> u.objectives.any { item.id in it.checkItemIds } })
        assertFalse(item.assessment!!.independent)
        assertEquals(listOf("I am 20 years old", "I own 20 summers", "20 people asked me"), item.options)
        val rem = pack.unit(17)!!.sessions.flatMap { it.steps }.single { it.id == "u17-st-rem-age" }.body
        assertTrue(rem.contains("not a statement of ownership"))                 // I own 20 summers
        assertTrue(rem.contains("it does not describe people performing an action")) // 20 people asked me
        assertTrue(item.distractorRationales[1].contains("ownership") && item.distractorRationales[2].contains("people performing an action"))
    }

    /** T17f-n1 (owner decision D53f, CC026): the Units 16-20 required checks present Russian or a rule cue and carry no label or position prompt. */
    @Test fun requiredChecksAssessRussianAndNotLabelsOrPositions() {
        val signals = V2DesignDiagnostics.audit(pack).signals.filter { (it.unit ?: 0) in 16..20 }
        assertTrue(signals.filter { it.rule.startsWith("A2_24_") }.joinToString("\n"), signals.none { it.rule.startsWith("A2_24_") })
        for (item in units.flatMap { required(it) }) {
            val russian = (item.promptLetter.orEmpty().takeIf { item.promptLetterRole == "CUE" }.orEmpty() + item.prompt + item.answer + item.options.joinToString())
                .any { it in 'Ѐ'..'ӿ' }
            assertTrue(item.id, russian || item.assessment?.cueLanguage == "rule")
        }
    }

    /** T17f-n1 (owner decision D53f, WC002): the Unit 17 and 19 objective titles describe recognition, not production. */
    @Test fun unit17And19ObjectiveTitlesDescribeRecognition() {
        val objectives = pack.units.filter { it.number in 17..19 }.flatMap { it.objectives }.associateBy { it.id }
        assertEquals("Recognize three clock constructions", objectives.getValue("obj-u17-clock-productive").title)
        assertEquals("RECOGNITION", objectives.getValue("obj-u17-clock-productive").kind)
        assertEquals("Distinguish changed meanings and genuine aspect partners", objectives.getValue("obj-u19-prefix-caution").title)
        assertEquals("RECOGNITION", objectives.getValue("obj-u19-prefix-caution").kind)
        val titles = units.flatMap { it.objectives }.map { it.title.lowercase() }
        assertTrue(titles.toString(), titles.none { "produce" in it || "productive" in it || "count any" in it })
    }

    /** T17f-n1 (owner decision D53f, WC011): each of Units 16-20 has its own binder and tutor task, without index or review wording. */
    @Test fun eachUnit16To20HasItsOwnBinderAndTutorTask() {
        val binders = units.map { it.binderTask!! }
        val tutors = units.map { it.tutorTask!! }
        assertEquals(listOf("binder-u16-instrumental", "binder-u17-counts", "binder-u18-comparison-relative", "binder-u19-forms", "binder-u20-motion"),
            binders.map { it.id })
        assertEquals(5, binders.map { it.prompt }.distinct().size)
        assertEquals(5, tutors.map { it.id }.distinct().size)
        assertTrue(tutors.all { it.id.startsWith("tutor-u") && it.bring.isNotBlank() })
        val text = binders.flatMap { listOf(it.prompt, it.modelAnswer) } + tutors.map { it.bring }
        assertTrue(text.none { Regex("(?i)\\bpdf\\b|\\bindex\\b|\\breview(?:er|ed)?\\b").containsMatchIn(it) })
    }

    /**
     * T17f-n1 (owner decision D53e): with the verified-clip registry, the design audit reports an A2-23 dictation shortfall for
     * these units. The diagnostic is not suppressed; the OWNER_ACCEPTED D53e row names exactly these units and defers them to T14-A.
     */
    @Test fun theA223DictationShortfallIsReportedAndRecorded() {
        val registry = kotlinx.serialization.json.Json.decodeFromString(V2AudioVerificationRegistry.serializer(),
            File(assets, "content/audio-verification.json").readText())
        val report = V2DesignDiagnostics.audit(pack, verifiedAudioByUnit(registry))
        val short = report.signals.filter { it.rule == "A2_23_DICTATION_MINIMUM" }.mapNotNull { it.unit }.sorted()
        assertEquals(listOf(2, 3, 4, 5, 6, 8, 9, 10, 12, 15, 16, 17, 24, 26), short)
        val row = File(root, "audit/FINDINGS_DISPOSITION.csv").readLines().single { it.startsWith("D53e,") }
        assertTrue(row, ",OWNER_ACCEPTED,T17f-n1,Product Owner," in row && "T14-A" in row)
        assertTrue(row, "Units 2, 3, 4, 5, 6, 8, 9, 10, 12, 15, 16, 17, 24 and 26" in row)
        // Units 16 and 17 have verified clips (4 and 1) and no bound dictation item.
        val rows = report.unitAssessments.associateBy { it.unit }
        assertEquals(listOf(4, 0), listOf(rows.getValue(16).verifiedAudioClips, rows.getValue(16).boundDictationItems))
        assertEquals(listOf(1, 0), listOf(rows.getValue(17).verifiedAudioClips, rows.getValue(17).boundDictationItems))
        assertEquals(listOf(36, 36, 12), listOf(rows.getValue(17).checkedItems, rows.getValue(17).requiredChecks, rows.getValue(17).typedRequiredChecks))
    }

    @Test fun committedBlock4SheetIsCurrentAndListsOnlyUnits16To20Uses() {
        val clips = loadCatalogClips(assets)
        val candidates = listenCheckCandidates(pack, blockUnits(4), clips)
        assertEquals(16..20, blockUnits(4))
        assertTrue(candidates.all { c -> c.uses.isNotEmpty() && c.uses.all { it.unitNumber in 16..20 } })
        assertTrue(File(root, "audit/listen-check/block4.html").readText().contains(listenCheckCandidatesDigest("block4", candidates)))
    }
}
