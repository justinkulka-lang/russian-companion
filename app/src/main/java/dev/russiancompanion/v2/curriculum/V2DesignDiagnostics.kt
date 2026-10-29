package dev.russiancompanion.v2.curriculum

import java.text.Normalizer
import kotlinx.serialization.Serializable

@Serializable
internal enum class V2DesignSignalKind { STRUCTURAL_ERROR, REVIEW_SIGNAL }

@Serializable
internal data class V2DesignSignal(
    val rule: String,
    val kind: V2DesignSignalKind,
    val unit: Int?,
    val entityIds: List<String>,
    val field: String,
    val relatedStepIds: List<String> = emptyList(),
    val explanation: String,
)

@Serializable
internal data class V2DesignReport(
    val reportSchemaVersion: Int = 1,
    val diagnosticVersion: String = "assessment-teaching-v1",
    val verdict: String,
    val counts: Map<String, Int>,
    val authoredRequiredKeyPositions: Map<Int, Int>,
    val signals: List<V2DesignSignal>,
    val limitations: List<String> = listOf(
        "Review signals are not confirmed linguistic or pedagogical defects.",
        "Lexical absence may be legitimate transfer; lexical presence does not prove adequate teaching.",
        "Authored key positions do not establish displayed positions after runtime permutation.",
        "This report neither replaces runtime validation nor grants review, rights or release approval.",
    ),
) {
    val structuralErrors: Int get() = signals.count { it.kind == V2DesignSignalKind.STRUCTURAL_ERROR }
    val reviewSignals: Int get() = signals.count { it.kind == V2DesignSignalKind.REVIEW_SIGNAL }
}

/** Explicit authoring diagnostics. Deliberately not called by the app's compatibility validator. */
internal object V2DesignDiagnostics {
    private val spaces = Regex("\\s+")
    private val markup = Regex("'{2,}|\\[\\[|\\]\\]|\\{\\{|\\}\\}|\\{\\||\\|\\}|</?(?:ref|br|span|div|small|sup|sub|nowiki)\\b|&(?:nbsp|amp|lt|gt|quot);", RegexOption.IGNORE_CASE)
    private val answerInstruction = Regex("\\b(?:choose|select|selecting|the answer|matching description|is the matching)\\b", RegexOption.IGNORE_CASE)
    private val sentenceBreak = Regex("(?<=[.;!?])\\s+")

    // Do not lowercase or remove accents: those can change the construct and the correct answer.
    private fun text(value: String): String = spaces.replace(Normalizer.normalize(value, Normalizer.Form.NFC).trim(), " ")
    private fun cue(item: V2CheckItem): String = if (item.promptLetterRole == "CUE") item.promptLetter.orEmpty() else ""
    private fun hasCyrillic(value: String): Boolean = value.any { it in '\u0400'..'\u052f' }

    private data class Task(
        val kind: String, val language: String, val prompt: String, val cue: String,
        val options: List<String>, val key: String,
    )

    fun audit(pack: V2CurriculumPack): V2DesignReport {
        val signals = mutableListOf<V2DesignSignal>()
        val items = pack.items.associateBy { it.id }
        val examples = pack.examples.associateBy { it.id }
        val stepRows = pack.units.flatMap { u -> u.sessions.flatMap { s -> s.steps.map { u.number to it } } }
        val steps = stepRows.associate { it.second.id to it.second }
        val position = stepRows.mapIndexed { index, row -> row.second.id to index }.toMap()
        val owner = pack.units.flatMap { u -> u.objectives.map { it.id to u.number } }.toMap()
        val required = linkedMapOf<String, V2CheckItem>()

        fun add(rule: String, kind: V2DesignSignalKind, unit: Int?, ids: List<String>, field: String,
                explanation: String, linked: List<String> = emptyList()) {
            signals += V2DesignSignal(rule, kind, unit, ids.sorted(), field, linked.distinct().sorted(), explanation)
        }
        fun inspectMarkup(id: String, unit: Int?, field: String, value: String?) {
            if (value != null && markup.containsMatchIn(value)) add("LEARNER_MARKUP", V2DesignSignalKind.STRUCTURAL_ERROR,
                unit, listOf(id), field, "A learner text field contains source-markup syntax; inspect the rendering and correct the governed text.")
        }
        fun stepText(step: V2Step): String = buildString {
            append(step.body); append('\n'); append(step.bookDirection.orEmpty()); append('\n'); append(step.companionDirection.orEmpty())
            step.exampleIds.mapNotNull(examples::get).forEach { append('\n'); append(it.display); append('\n'); append(it.note) }
        }

        for (unit in pack.units) for (objective in unit.objectives) {
            if (objective.kind != "RECOGNITION" || !objective.gating || objective.checkItemIds.isEmpty()) continue
            val checked = objective.checkItemIds.distinct().mapNotNull { id ->
                items[id].also { if (it == null) add("MISSING_CHECK_ITEM", V2DesignSignalKind.STRUCTURAL_ERROR,
                    unit.number, listOf(objective.id, id), "checkItemIds", "A gating objective references a missing item.") }
            }
            checked.forEach { required[it.id] = it }
            val teaching = objective.instructionStepIds.mapNotNull { id ->
                steps[id].also { if (it == null) add("MISSING_INSTRUCTION_LINK", V2DesignSignalKind.STRUCTURAL_ERROR,
                    unit.number, listOf(objective.id), "instructionStepIds", "A declared instruction step does not exist.", listOf(id)) }
            }.filter { it.kind == "TEACH" }
            val repairs = objective.remediationStepIds.mapNotNull { id ->
                steps[id].also { if (it == null) add("MISSING_REMEDIATION_LINK", V2DesignSignalKind.STRUCTURAL_ERROR,
                    unit.number, listOf(objective.id), "remediationStepIds", "A declared remediation step does not exist.", listOf(id)) }
            }
            if (teaching.isEmpty()) add("NO_LINKED_TEACHING", V2DesignSignalKind.STRUCTURAL_ERROR,
                unit.number, listOf(objective.id), "instructionStepIds", "A gating recognition objective has no linked teaching card.")
            if (repairs.isEmpty()) add("NO_LINKED_REMEDIATION", V2DesignSignalKind.STRUCTURAL_ERROR,
                unit.number, listOf(objective.id), "remediationStepIds", "A gating recognition objective has no linked remediation.")
            if (checked.size > 1 && checked.map { text(it.answer) }.distinct().size == 1) add("CONSTANT_REQUIRED_KEY",
                V2DesignSignalKind.REVIEW_SIGNAL, unit.number, listOf(objective.id) + checked.map { it.id }, "answer",
                "Every required check has the same key. Review shortcut strategies and the objective's actual construct.")

            val teachingText = text(teaching.joinToString("\n", transform = ::stepText))
            val repairText = text(repairs.joinToString("\n", transform = ::stepText))
            for (item in checked) {
                val shownCue = text(cue(item))
                val key = text(item.answer)
                if (shownCue.isNotEmpty() && item.options.any { it != item.answer && text(it) == shownCue }) add("CUE_AS_DISTRACTOR",
                    V2DesignSignalKind.REVIEW_SIGNAL, unit.number, listOf(item.id), "options",
                    "A distractor exactly repeats the visible cue. Inspect whether rejecting the cue alone gives away the answer.")
                if (item.options.size < 3) add("FEWER_THAN_THREE_OPTIONS", V2DesignSignalKind.REVIEW_SIGNAL,
                    unit.number, listOf(item.id), "options", "Review diagnostic value and a bounded justification; option count alone does not prove invalidity.")
                if (!hasCyrillic(item.prompt + shownCue + item.options.joinToString(" "))) add("NO_CYRILLIC_IN_REQUIRED_DISPLAY",
                    V2DesignSignalKind.REVIEW_SIGNAL, unit.number, listOf(item.id), "prompt/cue/options",
                    "The question displays no Cyrillic. Review the Russian-learning construct or make orientation explicitly nongating.")
                val scripts = teaching.filter { step -> stepText(step).split(sentenceBreak).any { sentence ->
                    key.isNotEmpty() && text(sentence).contains(key) && answerInstruction.containsMatchIn(sentence)
                } }
                if (scripts.isNotEmpty()) add("ANSWER_IN_INSTRUCTION_SIGNAL", V2DesignSignalKind.REVIEW_SIGNAL,
                    unit.number, listOf(item.id), "instruction", "An instruction sentence names the key and an answer-selection phrase. A human must distinguish answer scripts from legitimate rules/examples.", scripts.map { it.id })
                fun neither(context: String): Boolean = context.isEmpty() ||
                    (key.isNotEmpty() && !context.contains(key) && (shownCue.isEmpty() || !context.contains(shownCue)))
                if (neither(teachingText)) add("TEACHING_TRANSFER_OR_GAP", V2DesignSignalKind.REVIEW_SIGNAL,
                    unit.number, listOf(item.id), "instruction", "Neither cue nor key was located in linked teaching. Check the taught rule and legitimate transfer; this is not proof the content is untaught.", teaching.map { it.id })
                if (neither(repairText)) add("REMEDIATION_TRANSFER_OR_GAP", V2DesignSignalKind.REVIEW_SIGNAL,
                    unit.number, listOf(item.id), "remediation", "Neither cue nor key was located in linked remediation. Review explanation adequacy rather than inserting answer scripts.", repairs.map { it.id })
                val uses = stepRows.filter { it.second.kind == "CHECK" && item.id in it.second.itemIds }
                if (teaching.isNotEmpty() && uses.any { use -> teaching.none { (position[it.id] ?: Int.MAX_VALUE) < (position[use.second.id] ?: -1) } })
                    add("CHECK_BEFORE_LINKED_TEACHING", V2DesignSignalKind.STRUCTURAL_ERROR, unit.number,
                        listOf(item.id), "step order", "A required check occurs before any linked teaching card.", uses.map { it.second.id })
            }
        }

        required.values.groupBy { Task(it.kind, it.language, text(it.prompt), text(cue(it)), it.options.map(::text).sorted(), text(it.answer)) }
            .values.filter { it.size > 1 }.forEach { group -> add("IDENTICAL_REQUIRED_DISPLAY_TASK", V2DesignSignalKind.REVIEW_SIGNAL,
                null, group.map { it.id }, "prompt/cue/options/key", "Required items have identical visible task signatures. Review context and construct before declaring a duplicate.") }
        for ((number, step) in stepRows) {
            inspectMarkup(step.id, number, "title", step.title); inspectMarkup(step.id, number, "body", step.body)
            inspectMarkup(step.id, number, "bookDirection", step.bookDirection); inspectMarkup(step.id, number, "companionDirection", step.companionDirection)
            inspectMarkup(step.id, number, "unavailableReason", step.unavailableReason)
        }
        for (example in pack.examples) {
            inspectMarkup(example.id, owner[example.objectiveId], "display", example.display)
            inspectMarkup(example.id, owner[example.objectiveId], "note", example.note)
        }
        for (item in pack.items) {
            val number = owner[item.objectiveId]
            inspectMarkup(item.id, number, "prompt", item.prompt); inspectMarkup(item.id, number, "visibleCue", cue(item))
            inspectMarkup(item.id, number, "feedback", item.feedback)
            item.options.forEachIndexed { n, value -> inspectMarkup(item.id, number, "options[$n]", value) }
            item.distractorRationales.forEachIndexed { n, value -> inspectMarkup(item.id, number, "distractorRationales[$n]", value) }
        }
        for (entry in pack.references) {
            inspectMarkup(entry.id, entry.introducedInUnit, "title", entry.title)
            inspectMarkup(entry.id, entry.introducedInUnit, "summary", entry.summary)
            inspectMarkup(entry.id, entry.introducedInUnit, "detail", entry.detail)
        }
        val sorted = signals.distinct().sortedWith(compareBy({ it.rule }, { it.entityIds.joinToString("\u0000") }, { it.field }))
        return V2DesignReport(verdict = if (sorted.isEmpty()) "NO_MECHANICAL_SIGNALS_NOT_ACCEPTANCE" else "NEEDS_CORRECTION_OR_REVIEW",
            counts = linkedMapOf("units" to pack.units.size, "objectives" to pack.units.sumOf { it.objectives.size },
                "items" to pack.items.size, "requiredChecks" to required.size, "structuralErrors" to sorted.count { it.kind == V2DesignSignalKind.STRUCTURAL_ERROR },
                "reviewSignals" to sorted.count { it.kind == V2DesignSignalKind.REVIEW_SIGNAL }),
            authoredRequiredKeyPositions = required.values.groupingBy { it.options.indexOf(it.answer) }.eachCount().toSortedMap(),
            signals = sorted)
    }
}
