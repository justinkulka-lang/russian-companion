package dev.russiancompanion.v2.curriculum

/**
 * T15g (owner decision D20, approved rule change): the learner-facing text fields, each named "owner.field" and
 * attributed to the unit that shows it. V2Validator's Russian-coverage scan reads exactly this set, and PhraseAuditTest
 * asserts it equals the blocking phrase audit's own field set.
 *
 * Units: title, shortLabel, topicSummary, mappingNote. Objectives: title. Sessions: title. Steps: title, body,
 * bookDirection, companionDirection, unavailableReason, dominantAction. Items (every item, in its objective's unit, so
 * items reached only through their objective are included): prompt, promptLetter, answer, feedback, options[i],
 * distractorRationales[i], acceptedAnswers[i]. Examples (in each unit whose objective or step lists them, else their
 * objective's unit): display, note. References (in each unit whose step lists them): title, summary, detail.
 */
internal object V2LearnerFields {
    data class Field(val unit: Int, val ownerField: String, val text: String)

    fun fields(pack: V2CurriculumPack): List<Field> {
        val out = mutableListOf<Field>()
        val objectiveUnit = pack.units.flatMap { u -> u.objectives.map { it.id to u.number } }.toMap()
        val exampleUnits = linkedMapOf<String, MutableSet<Int>>()
        val referenceUnits = linkedMapOf<String, MutableSet<Int>>()
        fun add(unit: Int, owner: String, field: String, text: String?) {
            if (!text.isNullOrEmpty()) out += Field(unit, "$owner.$field", text)
        }
        pack.units.forEach { u ->
            val n = u.number
            add(n, u.id, "title", u.title); add(n, u.id, "shortLabel", u.shortLabel)
            add(n, u.id, "topicSummary", u.topicSummary); add(n, u.id, "mappingNote", u.mappingNote)
            u.objectives.forEach { o ->
                add(n, o.id, "title", o.title)
                o.exampleIds.forEach { exampleUnits.getOrPut(it) { linkedSetOf() } += n }
            }
            u.sessions.forEach { s ->
                add(n, s.id, "title", s.title)
                s.steps.forEach { st ->
                    add(n, st.id, "title", st.title); add(n, st.id, "body", st.body)
                    add(n, st.id, "bookDirection", st.bookDirection); add(n, st.id, "companionDirection", st.companionDirection)
                    add(n, st.id, "unavailableReason", st.unavailableReason); add(n, st.id, "dominantAction", st.dominantAction)
                    st.exampleIds.forEach { exampleUnits.getOrPut(it) { linkedSetOf() } += n }
                    st.referenceIds.forEach { referenceUnits.getOrPut(it) { linkedSetOf() } += n }
                }
            }
        }
        pack.items.forEach { item ->
            val n = objectiveUnit[item.objectiveId] ?: return@forEach
            add(n, item.id, "prompt", item.prompt); add(n, item.id, "promptLetter", item.promptLetter)
            add(n, item.id, "answer", item.answer); add(n, item.id, "feedback", item.feedback)
            item.options.forEachIndexed { i, v -> add(n, item.id, "options[$i]", v) }
            item.distractorRationales.forEachIndexed { i, v -> add(n, item.id, "distractorRationales[$i]", v) }
            item.typedAnswerKey?.acceptedAnswers?.forEachIndexed { i, v -> add(n, item.id, "acceptedAnswers[$i]", v) }
        }
        pack.examples.forEach { e ->
            val units = exampleUnits[e.id] ?: setOfNotNull(objectiveUnit[e.objectiveId])
            units.sorted().forEach { n -> add(n, e.id, "display", e.display); add(n, e.id, "note", e.note) }
        }
        pack.references.forEach { r ->
            referenceUnits[r.id].orEmpty().sorted().forEach { n ->
                add(n, r.id, "title", r.title); add(n, r.id, "summary", r.summary); add(n, r.id, "detail", r.detail)
            }
        }
        return out
    }

    /** Item assessment metadata is not shown to learners; the validator still checks its Russian, as before D20. */
    fun assessmentMetadata(pack: V2CurriculumPack): List<Field> {
        val objectiveUnit = pack.units.flatMap { u -> u.objectives.map { it.id to u.number } }.toMap()
        return pack.items.flatMap { item ->
            val n = objectiveUnit[item.objectiveId] ?: return@flatMap emptyList()
            val a = item.assessment ?: return@flatMap emptyList()
            listOf(Field(n, "${item.id}.assessment.whyAnswerDemonstratesObjective", a.whyAnswerDemonstratesObjective)) +
                a.distractorDifference.mapIndexed { i, v -> Field(n, "${item.id}.assessment.distractorDifference[$i]", v) }
        }
    }
}
