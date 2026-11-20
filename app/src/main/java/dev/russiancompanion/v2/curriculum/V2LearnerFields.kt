package dev.russiancompanion.v2.curriculum

/**
 * T15g (owner decisions D20 and D21e, approved rule change): the shared learner field set. V2Validator's
 * Russian-coverage scan and the blocking phrase audit both read exactly these fields, each named "owner.field",
 * given its field kind, and attributed to the unit that shows it.
 *
 * The set is every pack field the app renders to a learner (RENDERED, with the UI file and expression that renders it;
 * V2LearnerFieldsTest derives the rendered fields from the UI code and checks them against this table), plus the two
 * unit field the owner directed to keep scanning although no screen renders it now (DECLARED_NOT_RENDERED). T15h (D25e) adds
 * step.kind, reference.reviewStatus and the offline notice fields, and declares shortLabel rendered.
 *
 * Attribution: unit fields, binder and tutor tasks, objectives, sessions and steps to their unit; every item to its
 * objective's unit (so items linked only by item.objectiveId are included); examples to each unit whose objective or
 * step lists them, else their objective's unit; references, linked or not, to their introducedInUnit and to each unit
 * whose step links them; review-copy titles and introductions to their unit and review messages to every unit with a
 * review title; letters to Unit 1.
 */
internal object V2LearnerFields {
    data class Field(val unit: Int, val ownerField: String, val text: String, val kind: String)

    /** Field kind -> (UI file, expression that renders it). */
    val RENDERED: Map<String, Pair<String, String>> = linkedMapOf(
        "unit.title" to ("V2Screens.kt" to "unit.title"),
        "unit.mappingNote" to ("V2Screens.kt" to "unit.mappingNote"),
        // T15h (D25e): shortLabel is rendered through topicLabel() on Today, the course cards and unit headings.
        "unit.shortLabel" to ("V2Screens.kt" to "topicLabel()"),
        "binderTask.prompt" to ("V2UnitTestScreen.kt" to "task.prompt"),
        "binderTask.modelAnswer" to ("V2UnitTestScreen.kt" to "task.modelAnswer"),
        "tutorTask.bring" to ("V2UnitTestScreen.kt" to "task.bring"),
        "objective.title" to ("V2Screens.kt" to "objective.title"),
        "session.title" to ("V2Screens.kt" to "session.title"),
        "step.title" to ("V2Screens.kt" to "step.title"),
        "step.body" to ("V2Screens.kt" to "step.body"),
        "step.bookDirection" to ("V2Screens.kt" to "step.bookDirection"),
        "step.companionDirection" to ("V2Screens.kt" to "step.companionDirection"),
        "step.unavailableReason" to ("V2Screens.kt" to "step.unavailableReason"),
        "step.dominantAction" to ("V2Screens.kt" to "step.dominantAction"),
        "step.kind" to ("V2Screens.kt" to "step.kind"),
        "item.prompt" to ("V2PracticeFlows.kt" to "item.prompt"),
        "item.promptLetter" to ("V2Screens.kt" to "item.promptLetter"),
        "item.feedback" to ("V2CumulativeReviewScreen.kt" to "item.feedback"),
        "item.options" to ("V2AnswerFeedback.kt" to "item.options"),
        "item.distractorRationales" to ("V2AnswerFeedback.kt" to "item.distractorRationales"),
        "item.acceptedAnswers" to ("V2TypedResponseInput.kt" to "typedAnswerKey!!.acceptedAnswers"),
        "example.display" to ("V2Screens.kt" to "example.display"),
        "example.note" to ("V2Screens.kt" to "example.note"),
        "reference.title" to ("V2Screens.kt" to "entry.title"),
        "reference.summary" to ("V2Screens.kt" to "entry.summary"),
        "reference.detail" to ("V2Screens.kt" to "entry.detail"),
        "reference.reviewStatus" to ("V2Screens.kt" to "entry.reviewStatus"),
        "reviewCopy.title" to ("V2CumulativeReviewScreen.kt" to "cumulativeReviewTitle"),
        "reviewCopy.introduction" to ("V2CumulativeReviewScreen.kt" to "reviewCopy.introductions"),
        "reviewCopy.message" to ("V2CumulativeReviewScreen.kt" to "reviewCopy.text("),
        "letter.uppercase" to ("V2Screens.kt" to "letter.uppercase"),
        "letter.lowercase" to ("V2Screens.kt" to "letter.lowercase"),
        "letter.group" to ("V2Screens.kt" to "letter.group"),
        "letter.latinLookalike" to ("V2Screens.kt" to "letter.latinLookalike"),
        // T15h (D25e): the offline notices (V2OfflineNotices.sourceNotice -> V2Screens notices list) show each pack source's
        // ID, title, licence, attribution and modification, and its provenance records' licence URLs and share-alike text.
        "notice.sourceId" to ("V2Screens.kt" to "source.id"),
        "notice.title" to ("V2Screens.kt" to "source.creatorOrProject"),
        "notice.license" to ("V2Screens.kt" to "source.license"),
        "notice.attribution" to ("V2Screens.kt" to "source.attribution"),
        "notice.modification" to ("V2Screens.kt" to "source.modification"),
        "notice.licenseUrl" to ("V2Screens.kt" to "source.licenseUrls"),
        "notice.shareAlike" to ("V2Screens.kt" to "source.shareAlike"),
    )

    /** T15h (D25e): the offline notice is app-wide, so its fields belong to no unit (unit 0); the build-time phrase audit
     *  checks their Cyrillic against the FREEZE.json titles and attributions, which the app does not ship. */
    const val NOTICE_UNIT = 0

    /** Owner direction (D20, D21e): kept in the shared set although no screen renders it at present (D25e: shortLabel is rendered). */
    // T15h (D25e): item.answer is never rendered as its own field (the UI compares it; its text is shown as one of the
    // options or as the typed accepted answer), so it is declared and scanned, not rendered.
    val DECLARED_NOT_RENDERED: Set<String> = setOf("unit.topicSummary", "item.answer")

    val KINDS: Set<String> get() = RENDERED.keys + DECLARED_NOT_RENDERED

    fun fields(pack: V2CurriculumPack): List<Field> {
        val out = mutableListOf<Field>()
        val objectiveUnit = pack.units.flatMap { u -> u.objectives.map { it.id to u.number } }.toMap()
        val exampleUnits = linkedMapOf<String, MutableSet<Int>>()
        val referenceUnits = linkedMapOf<String, MutableSet<Int>>()
        fun add(unit: Int, owner: String, field: String, text: String?, kind: String) {
            if (!text.isNullOrEmpty()) out += Field(unit, "$owner.$field", text, kind)
        }
        pack.units.forEach { u ->
            val n = u.number
            add(n, u.id, "title", u.title, "unit.title"); add(n, u.id, "shortLabel", u.shortLabel, "unit.shortLabel")
            add(n, u.id, "topicSummary", u.topicSummary, "unit.topicSummary"); add(n, u.id, "mappingNote", u.mappingNote, "unit.mappingNote")
            u.binderTask?.let { t ->
                add(n, t.id, "prompt", t.prompt, "binderTask.prompt"); add(n, t.id, "modelAnswer", t.modelAnswer, "binderTask.modelAnswer")
            }
            u.tutorTask?.let { t -> add(n, t.id, "bring", t.bring, "tutorTask.bring") }
            u.objectives.forEach { o ->
                add(n, o.id, "title", o.title, "objective.title")
                o.exampleIds.forEach { exampleUnits.getOrPut(it) { linkedSetOf() } += n }
            }
            u.sessions.forEach { s ->
                add(n, s.id, "title", s.title, "session.title")
                s.steps.forEach { st ->
                    add(n, st.id, "title", st.title, "step.title"); add(n, st.id, "body", st.body, "step.body")
                    add(n, st.id, "bookDirection", st.bookDirection, "step.bookDirection")
                    add(n, st.id, "companionDirection", st.companionDirection, "step.companionDirection")
                    add(n, st.id, "unavailableReason", st.unavailableReason, "step.unavailableReason")
                    add(n, st.id, "dominantAction", st.dominantAction, "step.dominantAction")
                    add(n, st.id, "kind", st.kind, "step.kind")
                    st.exampleIds.forEach { exampleUnits.getOrPut(it) { linkedSetOf() } += n }
                    st.referenceIds.forEach { referenceUnits.getOrPut(it) { linkedSetOf() } += n }
                }
            }
        }
        pack.items.forEach { item ->
            val n = objectiveUnit[item.objectiveId] ?: return@forEach
            add(n, item.id, "prompt", item.prompt, "item.prompt"); add(n, item.id, "promptLetter", item.promptLetter, "item.promptLetter")
            add(n, item.id, "answer", item.answer, "item.answer"); add(n, item.id, "feedback", item.feedback, "item.feedback")
            item.options.forEachIndexed { i, v -> add(n, item.id, "options[$i]", v, "item.options") }
            item.distractorRationales.forEachIndexed { i, v -> add(n, item.id, "distractorRationales[$i]", v, "item.distractorRationales") }
            item.typedAnswerKey?.acceptedAnswers?.forEachIndexed { i, v -> add(n, item.id, "acceptedAnswers[$i]", v, "item.acceptedAnswers") }
        }
        pack.examples.forEach { e ->
            val units = exampleUnits[e.id] ?: setOfNotNull(objectiveUnit[e.objectiveId])
            units.sorted().forEach { n -> add(n, e.id, "display", e.display, "example.display"); add(n, e.id, "note", e.note, "example.note") }
        }
        pack.references.forEach { r ->
            (referenceUnits[r.id].orEmpty() + r.introducedInUnit).sorted().forEach { n ->
                add(n, r.id, "title", r.title, "reference.title"); add(n, r.id, "summary", r.summary, "reference.summary")
                add(n, r.id, "detail", r.detail, "reference.detail")
                add(n, r.id, "reviewStatus", r.reviewStatus, "reference.reviewStatus")
            }
        }
        pack.reviewCopy.titles.forEach { (n, v) -> add(n, "reviewCopy", "titles[$n]", v, "reviewCopy.title") }
        pack.reviewCopy.introductions.forEach { (n, v) -> add(n, "reviewCopy", "introductions[$n]", v, "reviewCopy.introduction") }
        pack.reviewCopy.titles.keys.sorted().forEach { n ->
            pack.reviewCopy.messages.forEach { (key, v) -> add(n, "reviewCopy", "messages[$key]", v, "reviewCopy.message") }
        }
        pack.letters.forEach { l ->
            add(1, l.id, "uppercase", l.uppercase, "letter.uppercase"); add(1, l.id, "lowercase", l.lowercase, "letter.lowercase")
            add(1, l.id, "group", l.group, "letter.group"); add(1, l.id, "latinLookalike", l.latinLookalike, "letter.latinLookalike")
        }
        pack.sources.forEach { source ->
            val records = pack.provenanceRecords.filter { it.sourceId == source.id }
            val n = NOTICE_UNIT
            add(n, source.id, "id", source.id, "notice.sourceId"); add(n, source.id, "title", source.title, "notice.title")
            add(n, source.id, "license", source.license, "notice.license"); add(n, source.id, "attribution", source.attribution, "notice.attribution")
            add(n, source.id, "modification", source.modification, "notice.modification")
            records.map { it.licenseUrl }.filter { it.isNotBlank() }.distinct().forEachIndexed { i, v -> add(n, source.id, "licenseUrls[$i]", v, "notice.licenseUrl") }
            records.map { it.shareAlikeTreatment }.filter { it.isNotBlank() }.distinct().forEachIndexed { i, v -> add(n, source.id, "shareAlike[$i]", v, "notice.shareAlike") }
        }
        return out
    }

    /** Item assessment metadata is not shown to learners; the validator still checks its Russian, as before D20. */
    fun assessmentMetadata(pack: V2CurriculumPack): List<Field> {
        val objectiveUnit = pack.units.flatMap { u -> u.objectives.map { it.id to u.number } }.toMap()
        return pack.items.flatMap { item ->
            val n = objectiveUnit[item.objectiveId] ?: return@flatMap emptyList()
            val a = item.assessment ?: return@flatMap emptyList()
            listOf(Field(n, "${item.id}.assessment.whyAnswerDemonstratesObjective", a.whyAnswerDemonstratesObjective, "assessment.why")) +
                a.distractorDifference.mapIndexed { i, v -> Field(n, "${item.id}.assessment.distractorDifference[$i]", v, "assessment.difference") }
        }
    }
}
