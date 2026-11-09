package dev.russiancompanion.v2.curriculum

import kotlinx.serialization.Serializable

@Serializable
internal data class V2UnitAssessmentSummary(
    val unit: Int,
    val checkedItems: Int,
    val requiredChecks: Int,
    val typedRequiredChecks: Int,
    val verifiedAudioClips: Int,
    val boundDictationItems: Int,
)

/** Report-only A2-23/24 checks. A catalog audio record does not establish an owner-verified clip. */
internal object V2AssessmentDiagnostics {
    private val labelOrPosition = Regex(
        "\\b(?:label|position|column|row|first option|second option|third option|equals sign|" +
            "frozen line|admitted line|source set|source line|written (?:after|beside)|listed (?:after|under))\\b",
        RegexOption.IGNORE_CASE,
    )

    // Token boundaries avoid finding a one-letter key inside an unrelated word. No case/stress folding.
    private fun containsKey(value: String, key: String): Boolean = key.isNotBlank() &&
        Regex("(?<![\\p{L}\\p{N}])${Regex.escape(key)}(?![\\p{L}\\p{N}])").containsMatchIn(value)

    fun audit(pack: V2CurriculumPack, verifiedAudioByUnit: Map<Int, Set<String>> = emptyMap()):
        Pair<List<V2UnitAssessmentSummary>, List<V2DesignSignal>> {
        val signals = mutableListOf<V2DesignSignal>()
        val summaries = mutableListOf<V2UnitAssessmentSummary>()
        val items = pack.items.associateBy { it.id }
        val examples = pack.examples.associateBy { it.id }
        for (unit in pack.units.sortedBy { it.number }) {
            val steps = unit.sessions.flatMap { it.steps }.associateBy { it.id }
            val requiredIds = unit.objectives.filter { it.gating }.flatMap { it.checkItemIds }.toSet()
            val checkedIds = (unit.objectives.flatMap { it.checkItemIds } + steps.values
                .filter { it.kind in setOf("CHECK", "PRACTICE") }.flatMap { it.itemIds }).toSet()
            val clips = verifiedAudioByUnit[unit.number].orEmpty()
            fun verifiedDictation(item: V2CheckItem): Boolean = item.kind == "TYPED_DICTATION" &&
                item.dictationAudioAssetId in clips &&
                typedItemDefects(item.copy(kind = "TYPED_FORM", dictationAudioAssetId = null)).isEmpty()
            val checked = checkedIds.mapNotNull(items::get).filter { scoreCheckedResponse(it, it.answer) != null || verifiedDictation(it) }
            val typed = requiredIds.mapNotNull(items::get).count {
                it.isTypedResponse() && (typedItemDefects(it).isEmpty() || verifiedDictation(it))
            }
            val dictations = checked.count(::verifiedDictation)
            summaries += V2UnitAssessmentSummary(unit.number, checked.size, requiredIds.size, typed, clips.size, dictations)
            fun add(rule: String, ids: List<String>, field: String, explanation: String, linked: List<String> = emptyList()) {
                signals += V2DesignSignal(rule, V2DesignSignalKind.REVIEW_SIGNAL, unit.number,
                    ids.distinct().sorted(), field, linked.distinct().sorted(), explanation)
            }
            if (checked.size < 20) add("A2_23_CHECKED_MINIMUM", listOf(unit.id), "checkedItems",
                "${checked.size} checked items; at least 20 required. Any shortfall needs a recorded reason accepted by the Product Owner.")
            // Integer arithmetic avoids rounding 29.9 percent up to the required 30 percent.
            if (unit.number >= 3 && (requiredIds.isEmpty() || typed * 10 < requiredIds.size * 3))
                add("A2_23_TYPED_MINIMUM", listOf(unit.id), "requiredChecks",
                    "$typed of ${requiredIds.size} required checks are available typed items; at least 30 percent required from Unit 3.")
            if (clips.isNotEmpty() && dictations == 0) add("A2_23_DICTATION_MINIMUM", listOf(unit.id), "dictationAudioAssetId",
                "Verified audio is available but no checked dictation item is bound to one of those clips.")
            for (id in requiredIds.sorted()) {
                val item = items[id]
                if (item == null) {
                    add("A2_24_MISSING_REQUIRED_ITEM", listOf(id), "checkItemIds", "The declared required check does not exist.")
                    continue
                }
                val keys = if (item.isTypedResponse()) item.typedAnswerKey?.acceptedAnswers.orEmpty() else listOf(item.answer)
                if (keys.any { containsKey(item.prompt, it) }) add("A2_24_KEY_IN_PROMPT", listOf(id), "prompt",
                    "An accepted key occurs in the prompt. Review whether the wording discloses the answer.")
                val distractors = item.options.filter { it != item.answer }.distinct()
                if (!item.isTypedResponse() && distractors.size == 1 && item.promptLetterRole == "CUE" &&
                    distractors.single() == item.promptLetter) add("A2_24_ONLY_DISTRACTOR_IS_CUE", listOf(id), "options",
                    "The only distractor is the displayed cue; rejecting that cue gives away the key.")
                val teaching = unit.objectives.filter { id in it.checkItemIds }.flatMap { it.instructionStepIds }
                    .distinct().mapNotNull(steps::get).filter { it.kind == "TEACH" }
                val revealing = teaching.filter { step ->
                    val displayed = listOf(step.title, step.body, step.bookDirection.orEmpty(), step.companionDirection.orEmpty()) +
                        step.exampleIds.mapNotNull(examples::get).flatMap { listOf(it.display, it.note) }
                    displayed.any { value -> keys.any { containsKey(value, it) } }
                }
                if (revealing.isNotEmpty()) add("A2_24_EXACT_KEY_IN_TEACHING", listOf(id), "instructionStepIds",
                    "Linked teaching states an accepted key. Review the exact teaching/check pairing under A2-24.", revealing.map { it.id })
                if (labelOrPosition.containsMatchIn(item.prompt)) add("A2_24_LABEL_OR_POSITION", listOf(id), "prompt",
                    "Prompt refers to a label or position; review whether it checks retrieval rather than Russian.")
            }
        }
        return summaries to signals
    }
}
