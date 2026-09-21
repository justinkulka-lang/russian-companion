package dev.russiancompanion.domain.plan

internal enum class ChecklistActivity {
    BOOK,
    ANKI_DROID,
    LISTEN_RECORD_COMPARE,
    TUTOR,
}

internal enum class RecordedCategory {
    READING,
    LISTENING,
    VOCABULARY_AND_FORMS,
    GRAMMAR,
    SPEAKING_AND_PRONUNCIATION,
    WRITING,
    TUTOR_WORK,
}

internal enum class RecordedGateStatus {
    SATISFIED,
    NOT_YET_SATISFIED,
    NOT_EVALUABLE,
}

internal data class ChecklistItem(
    val planDefinitionId: String,
    val activity: ChecklistActivity,
    val instruction: String,
    val sortOrder: Int,
    val required: Boolean,
    val completed: Boolean,
)

internal data class DailyPlan(
    val items: List<ChecklistItem>,
    val addableTutorTasks: List<ChecklistItem>,
) {
    val requiredItems: List<ChecklistItem>
        get() = items.filter { it.required }

    val requiredCompletedCount: Int
        get() = requiredItems.count { it.completed }

    val nextIncompleteRequired: ChecklistItem?
        get() = requiredItems.firstOrNull { !it.completed }

    val requiredWorkPrescribed: Boolean
        get() = requiredItems.isNotEmpty()

    val requiredWorkComplete: Boolean
        get() = requiredWorkPrescribed && requiredItems.all { it.completed }

    companion object {
        fun empty(): DailyPlan = DailyPlan(emptyList(), emptyList())
    }
}

internal data class PlanDefinitionView(
    val id: String,
    val activity: ChecklistActivity,
    val instruction: String,
    val sortOrder: Int,
    val required: Boolean,
)

internal data class PlanInstanceView(
    val planDefinitionId: String,
    val completed: Boolean,
)

internal fun canMarkComplete(item: ChecklistItem): Boolean {
    return !item.completed && item.activity != ChecklistActivity.LISTEN_RECORD_COMPARE
}

internal fun assembleDailyPlan(
    definitions: List<PlanDefinitionView>,
    instances: List<PlanInstanceView>,
): DailyPlan {
    val instanceByPlan = instances.associateBy { it.planDefinitionId }
    val ordered = definitions.sortedWith(compareBy({ it.sortOrder }, { it.id }))
    val items = ordered.mapNotNull { definition ->
        val instance = instanceByPlan[definition.id]
        if (definition.activity == ChecklistActivity.TUTOR && instance == null) return@mapNotNull null
        ChecklistItem(
            planDefinitionId = definition.id,
            activity = definition.activity,
            instruction = definition.instruction,
            sortOrder = definition.sortOrder,
            required = definition.required,
            completed = instance?.completed == true,
        )
    }
    val addable = ordered.filter { definition ->
        definition.activity == ChecklistActivity.TUTOR &&
            !definition.required &&
            instanceByPlan[definition.id] == null
    }.map { definition ->
        ChecklistItem(
            planDefinitionId = definition.id,
            activity = definition.activity,
            instruction = definition.instruction,
            sortOrder = definition.sortOrder,
            required = definition.required,
            completed = false,
        )
    }
    return DailyPlan(items, addable)
}

internal data class GateStatusInput(
    val id: String,
    val displayedRuleText: String,
)

internal data class DecisionView(
    val id: String,
    val gateId: String,
    val status: RecordedGateStatus,
    val evaluatedAtLocal: String,
)

internal data class GateSummary(
    val id: String,
    val displayedRuleText: String,
    val status: RecordedGateStatus?,
)

internal fun summarizeGates(
    gates: List<GateStatusInput>,
    decisions: List<DecisionView>,
): List<GateSummary> {
    return gates.map { gate ->
        val latest = decisions
            .filter { it.gateId == gate.id }
            .maxWithOrNull(compareBy({ it.evaluatedAtLocal }, { it.id }))
        GateSummary(id = gate.id, displayedRuleText = gate.displayedRuleText, status = latest?.status)
    }
}

internal data class ActivityLogView(
    val id: String,
    val category: RecordedCategory,
    val minutes: Int,
    val completed: Boolean,
    val recordedAtLocal: String,
)

internal data class CategorySummary(
    val category: RecordedCategory,
    val recordedMinutes: Long,
    val completionCount: Int,
)

internal data class ActivitySummary(
    val latest: ActivityLogView?,
    val totals: List<CategorySummary>,
) {
    companion object {
        fun none(): ActivitySummary = ActivitySummary(latest = null, totals = emptyList())
    }
}

internal fun summarizeActivity(logs: List<ActivityLogView>): ActivitySummary {
    val latest = logs.maxWithOrNull(compareBy({ it.recordedAtLocal }, { it.id }))
    val totals = RecordedCategory.entries.mapNotNull { category ->
        val matching = logs.filter { it.category == category }
        val minutes = matching.sumOf { it.minutes.toLong() }
        val completions = matching.count { it.completed }
        if (minutes == 0L && completions == 0) null
        else CategorySummary(category, minutes, completions)
    }
    return ActivitySummary(latest = latest, totals = totals)
}

internal sealed interface PlanUpdate {
    data object Applied : PlanUpdate

    data object Unchanged : PlanUpdate

    data class NotSaved(val planDefinitionId: String) : PlanUpdate
}
