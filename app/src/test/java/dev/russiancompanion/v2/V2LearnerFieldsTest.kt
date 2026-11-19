package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/**
 * T15g (owner decision D21e): the validator's Russian coverage and the blocking phrase audit read one shared field set,
 * every pack field the app renders to a learner. This test derives the rendered fields from the UI code and checks them
 * against V2LearnerFields; a regression puts a word without a witness into each field kind.
 */
class V2LearnerFieldsTest {
    private val root = PhraseAudit.root
    private val pack by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()) }
    private val ui by lazy {
        File(root, "app/src/main/java/dev/russiancompanion/v2/ui").listFiles()!!.filter { it.extension == "kt" }
            .associate { it.name to it.readText() }
    }

    // Identifier and state properties the UI reads for logic, not as learner text.
    private val notText = setOf("id", "kind", "sourceId", "reviewStatus", "language", "objectiveId", "mappingStatus",
        "installState", "readiness", "classification", "promptLetterRole", "audioAssetId", "exampleWordId", "letterId")

    // Learner text is a String or a List<String>; lists of entities and ID lists are structure, not text.
    private fun textProperties(type: Class<*>): Set<String> = type.declaredFields.filter { f ->
        f.type == String::class.java || (List::class.java.isAssignableFrom(f.type) &&
            (f.genericType as? java.lang.reflect.ParameterizedType)?.actualTypeArguments?.singleOrNull() == String::class.java)
    }.map { it.name }.filter { it !in notText && !it.endsWith("Ids") && !it.endsWith("Id") }.toSet()

    @Test fun theSharedFieldSetIsTheSetTheUiRenders() {
        // 1. Each rendered kind names the UI file and expression that renders it.
        V2LearnerFields.RENDERED.forEach { (kind, where) ->
            val (file, expression) = where
            assertTrue("$kind: $file does not contain $expression", ui.getValue(file).contains(expression))
        }
        // 2. Derived from the UI code: every learner-text property the screens read on a pack entity is a shared kind.
        val receivers = mapOf(
            "unit" to ("unit" to V2Unit::class.java), "session" to ("session" to V2Session::class.java),
            "step" to ("step" to V2Step::class.java), "item" to ("item" to V2CheckItem::class.java),
            "example" to ("example" to V2Example::class.java), "entry" to ("reference" to V2ReferenceEntry::class.java),
            "objective" to ("objective" to V2Objective::class.java), "letter" to ("letter" to V2Letter::class.java),
        )
        val derived = mutableSetOf<String>()
        val access = Regex("\\b(unit|session|step|item|example|entry|objective|letter|task)\\??\\.([a-zA-Z]+)\\b")
        ui.values.forEach { text ->
            access.findAll(text).forEach { m ->
                val (receiver, property) = m.destructured
                if (receiver == "task") {
                    if (property in textProperties(V2BinderTask::class.java)) derived += "binderTask.$property"
                    if (property in textProperties(V2TutorTask::class.java)) derived += "tutorTask.$property"
                } else {
                    val (kind, type) = receivers.getValue(receiver)
                    if (property in textProperties(type)) derived += "$kind.$property"
                }
            }
        }
        if (ui.values.any { "typedAnswerKey!!.acceptedAnswers" in it || "typedAnswerKey?.acceptedAnswers" in it }) derived += "item.acceptedAnswers"
        if (ui.values.any { "cumulativeReviewTitle" in it }) derived += "reviewCopy.title"
        if (ui.values.any { "reviewCopy.introductions" in it }) derived += "reviewCopy.introduction"
        if (ui.values.any { "reviewCopy.text(" in it }) derived += "reviewCopy.message"
        assertEquals(V2LearnerFields.RENDERED.keys, derived)
        // 3. The shared set is the rendered set plus the owner-declared unit fields; nothing else.
        assertEquals(setOf("unit.shortLabel", "unit.topicSummary"), V2LearnerFields.DECLARED_NOT_RENDERED)
        assertTrue(V2LearnerFields.DECLARED_NOT_RENDERED.none { kind -> ui.values.any { kind in it } })
        // Every kind the shared set emits for the current pack is declared; binderTask.modelAnswer and
        // step.unavailableReason have no value in the current pack, and the regression below shows they are scanned.
        val produced = V2LearnerFields.fields(pack).map { it.kind }.toSet()
        assertTrue(V2LearnerFields.KINDS.containsAll(produced))
        assertEquals(setOf("binderTask.modelAnswer", "step.unavailableReason"), V2LearnerFields.KINDS - produced)
        // The audit and the validator read the same list.
        assertEquals(V2LearnerFields.fields(pack).map { Triple(it.unit, it.ownerField, it.text) },
            PhraseAudit.fields(pack).map { Triple(it.unit, it.ownerField, it.text) })
    }

    /**
     * Regression: a word without a witness in Unit 29 in each field kind is rejected by the validator, and an unattested
     * phrase there is reported by the audit. сущ is allowlisted only for Unit 30; Книга лежит на столе is allowlisted only
     * for Unit 30 and attested in no frozen line. A single unstressed word is outside the audit's phrase rule (and сущ
     * has the frozen homograph су́щ), so the audit side uses the phrase; the validator side uses the word.
     */
    @Test fun aWordOrPhraseWithoutWitnessInAnyRenderedFieldKindIsRejected() {
        val word = "сущ"
        val phrase = "Книга лежит на столе"
        assertFalse(PhraseAudit.present("$phrase и"))
        val baseline = V2Validator.validate(pack)
        assertTrue(baseline.errors.joinToString("\n"), baseline.ok)
        val baselineAudit = PhraseAudit.failures(PhraseAudit.fields(pack))
        val kinds = (V2LearnerFields.KINDS - setOf("letter.uppercase", "letter.lowercase", "letter.group", "letter.latinLookalike")).sorted()
        for (kind in kinds) {
            val withWord = insert(pack, kind, "($word)")
            val errors = V2Validator.validate(withWord).errors
            assertTrue("$kind: $errors", errors.any { it == "Russian text has no admitted source record: $word" })
            val withPhrase = insert(pack, kind, "($phrase)")
            val audit = PhraseAudit.failures(PhraseAudit.fields(withPhrase)) - baselineAudit
            assertTrue("$kind: $audit", audit.any { it.unit != 30 && it.text == phrase && it.type == "UNATTESTED" })
        }
        // Letters render on the Unit 1 alphabet screens: a letter field that is not a letter form is rejected there.
        val letter = pack.letters.first()
        for (kind in listOf("group", "latinLookalike")) {
            val changed = pack.copy(letters = pack.letters.map { if (it.id != letter.id) it else if (kind == "group") it.copy(group = it.group + " ($word)") else it.copy(latinLookalike = "($word)") })
            assertTrue(kind, V2Validator.validate(changed).errors.any { it == "Russian text has no admitted source record: $word" })
        }
    }

    private fun insert(p: V2CurriculumPack, kind: String, text: String): V2CurriculumPack {
        // Binder and tutor tasks exist in Units 3-5 only and typed items in Units 1-10 only; every other kind is probed in Unit 29.
        val n = when {
            kind.startsWith("binderTask.") || kind.startsWith("tutorTask.") -> 5
            kind == "item.acceptedAnswers" -> 10
            else -> 29
        }
        val u = p.units.single { it.number == n }
        fun units(f: (V2Unit) -> V2Unit) = p.copy(units = p.units.map { if (it.number == n) f(it) else it })
        fun steps(f: (V2Step) -> V2Step): V2CurriculumPack {
            val target = u.sessions.flatMap { it.steps }.first { kind != "step.dominantAction" || it.dominantAction != null }
            return units { unit -> unit.copy(sessions = unit.sessions.map { s -> s.copy(steps = s.steps.map { if (it.id == target.id) f(it) else it }) }) }
        }
        val objectives = u.objectives.map { it.id }.toSet()
        val placed = p.units.flatMap { x -> x.objectives.flatMap { it.checkItemIds } + x.sessions.flatMap { s -> s.steps.flatMap { it.itemIds } } }.toSet()
        // An item linked only by item.objectiveId, and a typed item for accepted answers.
        // Prefer an objective-only item; a kind that no objective-only item carries (typed answers, distractor
        // rationales) is probed on a placed item of the same unit.
        val item by lazy {
            val fits = p.items.filter { it.objectiveId in objectives && (kind != "item.acceptedAnswers" || it.typedAnswerKey != null) &&
                (kind != "item.distractorRationales" || it.distractorRationales.isNotEmpty()) }
            fits.firstOrNull { it.id !in placed } ?: fits.firstOrNull() ?: fail("Unit $n has no item for $kind") as Nothing
        }
        fun items(f: (V2CheckItem) -> V2CheckItem) = p.copy(items = p.items.map { if (it.id == item.id) f(it) else it })
        val linked = p.units.flatMap { x -> x.objectives.flatMap { it.exampleIds } + x.sessions.flatMap { s -> s.steps.flatMap { it.exampleIds } } }.toSet()
        val example by lazy { p.examples.firstOrNull { it.objectiveId in objectives && it.id !in linked } ?: p.examples.first { it.objectiveId in objectives } }
        fun examples(f: (V2Example) -> V2Example) = p.copy(examples = p.examples.map { if (it.id == example.id) f(it) else it })
        // An unlinked reference introduced in Unit 29.
        val unlinked = p.references.first().copy(id = "ref-t15g-probe", title = "Probe", summary = "Probe", detail = "Probe", introducedInUnit = n)
        fun reference(f: (V2ReferenceEntry) -> V2ReferenceEntry) = p.copy(references = p.references + f(unlinked))
        return when (kind) {
            "unit.title" -> units { it.copy(title = it.title + " $text") }
            "unit.shortLabel" -> units { it.copy(shortLabel = it.shortLabel + " $text") }
            "unit.topicSummary" -> units { it.copy(topicSummary = it.topicSummary + " $text") }
            "unit.mappingNote" -> units { it.copy(mappingNote = it.mappingNote + " $text") }
            "binderTask.prompt" -> units { it.copy(binderTask = it.binderTask!!.copy(prompt = it.binderTask!!.prompt + " $text")) }
            "binderTask.modelAnswer" -> units { it.copy(binderTask = it.binderTask!!.copy(modelAnswer = (it.binderTask!!.modelAnswer ?: "") + " $text")) }
            "tutorTask.bring" -> units { it.copy(tutorTask = it.tutorTask!!.copy(bring = it.tutorTask!!.bring + " $text")) }
            "objective.title" -> units { unit -> unit.copy(objectives = unit.objectives.mapIndexed { i, o -> if (i == 0) o.copy(title = o.title + " $text") else o }) }
            "session.title" -> units { unit -> unit.copy(sessions = unit.sessions.mapIndexed { i, s -> if (i == 0) s.copy(title = s.title + " $text") else s }) }
            "step.title" -> steps { it.copy(title = it.title + " $text") }
            "step.body" -> steps { it.copy(body = it.body + " $text") }
            "step.bookDirection" -> steps { it.copy(bookDirection = (it.bookDirection ?: "") + " $text") }
            "step.companionDirection" -> steps { it.copy(companionDirection = (it.companionDirection ?: "") + " $text") }
            "step.unavailableReason" -> steps { it.copy(unavailableReason = (it.unavailableReason ?: "") + " $text") }
            "step.dominantAction" -> steps { it.copy(dominantAction = it.dominantAction + " $text") }
            "item.prompt" -> items { it.copy(prompt = it.prompt + " $text") }
            "item.promptLetter" -> items { it.copy(promptLetter = (it.promptLetter ?: "") + " $text") }
            "item.answer" -> items { it.copy(answer = it.answer + " $text") }
            "item.feedback" -> items { it.copy(feedback = it.feedback + " $text") }
            "item.options" -> items { it.copy(options = it.options + text) }
            "item.distractorRationales" -> items { it.copy(distractorRationales = it.distractorRationales + text) }
            "item.acceptedAnswers" -> items { it.copy(typedAnswerKey = it.typedAnswerKey!!.copy(acceptedAnswers = it.typedAnswerKey!!.acceptedAnswers + text)) }
            "example.display" -> examples { it.copy(display = it.display + " $text") }
            "example.note" -> examples { it.copy(note = it.note + " $text") }
            "reference.title" -> reference { it.copy(title = "Probe $text") }
            "reference.summary" -> reference { it.copy(summary = "Probe $text") }
            "reference.detail" -> reference { it.copy(detail = "Probe $text") }
            "reviewCopy.title" -> p.copy(reviewCopy = p.reviewCopy.copy(titles = p.reviewCopy.titles + (n to (p.reviewCopy.titles[n].orEmpty() + " $text"))))
            "reviewCopy.introduction" -> p.copy(reviewCopy = p.reviewCopy.copy(introductions = p.reviewCopy.introductions + (n to (p.reviewCopy.introductions[n].orEmpty() + " $text"))))
            "reviewCopy.message" -> p.copy(reviewCopy = p.reviewCopy.copy(messages = p.reviewCopy.messages + ("t15gProbe" to "Probe $text")))
            else -> fail("No insertion for $kind") as Nothing
        }
    }
}
