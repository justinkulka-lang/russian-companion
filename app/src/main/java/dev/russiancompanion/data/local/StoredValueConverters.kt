package dev.russiancompanion.data.local

import androidx.room.TypeConverter

internal class StoredValueConverters {
    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isEmpty()) emptyList() else value.split('\n')
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String = value.joinToString("\n")

    @TypeConverter
    fun toPlanActivityType(value: String): PlanActivityType = PlanActivityType.valueOf(value)

    @TypeConverter
    fun fromPlanActivityType(value: PlanActivityType): String = value.name

    @TypeConverter
    fun toActivityCategory(value: String): ActivityCategory = ActivityCategory.valueOf(value)

    @TypeConverter
    fun fromActivityCategory(value: ActivityCategory): String = value.name

    @TypeConverter
    fun toEvidenceType(value: String): EvidenceType = EvidenceType.valueOf(value)

    @TypeConverter
    fun fromEvidenceType(value: EvidenceType): String = value.name

    @TypeConverter
    fun toSelfAttestationStatus(value: String): SelfAttestationStatus = SelfAttestationStatus.valueOf(value)

    @TypeConverter
    fun fromSelfAttestationStatus(value: SelfAttestationStatus): String = value.name

    @TypeConverter
    fun toCompletionState(value: String): CompletionState = CompletionState.valueOf(value)

    @TypeConverter
    fun fromCompletionState(value: CompletionState): String = value.name

    @TypeConverter
    fun toGateDecisionStatus(value: String): GateDecisionStatus = GateDecisionStatus.valueOf(value)

    @TypeConverter
    fun fromGateDecisionStatus(value: GateDecisionStatus): String = value.name

    @TypeConverter
    fun toExportStatus(value: String): ExportStatus = ExportStatus.valueOf(value)

    @TypeConverter
    fun fromExportStatus(value: ExportStatus): String = value.name

    @TypeConverter
    fun toEligibilityStatus(value: String): EligibilityStatus = EligibilityStatus.valueOf(value)

    @TypeConverter
    fun fromEligibilityStatus(value: EligibilityStatus): String = value.name

    @TypeConverter
    fun toLicenseStatus(value: String): LicenseStatus = LicenseStatus.valueOf(value)

    @TypeConverter
    fun fromLicenseStatus(value: LicenseStatus): String = value.name

    @TypeConverter
    fun toInclusionResult(value: String): InclusionResult = InclusionResult.valueOf(value)

    @TypeConverter
    fun fromInclusionResult(value: InclusionResult): String = value.name

    @TypeConverter
    fun toModificationStatus(value: String): ModificationStatus = ModificationStatus.valueOf(value)

    @TypeConverter
    fun fromModificationStatus(value: ModificationStatus): String = value.name

    @TypeConverter
    fun toShareAlikeConfirmation(value: String): ShareAlikeConfirmation = ShareAlikeConfirmation.valueOf(value)

    @TypeConverter
    fun fromShareAlikeConfirmation(value: ShareAlikeConfirmation): String = value.name

    @TypeConverter
    fun toRuleOperator(value: String): RuleOperator = RuleOperator.valueOf(value)

    @TypeConverter
    fun fromRuleOperator(value: RuleOperator): String = value.name
}
