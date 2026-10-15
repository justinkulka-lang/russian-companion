#!/usr/bin/env bash
set -euo pipefail
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
checks=(
 --tests '*Unit20CumulativeReviewTest'
 --tests '*CumulativeReviewGateTest'
 --tests '*CumulativeReviewStorageTest'
 --tests '*Unit19CumulativeReviewTest.ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup'
 --tests '*Unit19CumulativeReviewTest.schema11ExactPrePolicyRowsSurviveWhileCurrentRawRowsAndChangedInstructionDoNot'
 --tests '*Unit4NominativeRoleTest'
 --tests '*Unit19ConditionalObligationInstructionTest'
 --tests '*Unit15ImperativeRemediationTest.exactDeclaredUnitExceptionPreservesEveryQuestionRecordAndRequirement'
 --tests '*Unit11AgreementRemediationTest.unchangedQuestionsRecordsAndRequirementsHaveAnExactSessionOnlyException'
 --tests '*Unit14ImpersonalRemediationTest.unchangedQuestionsRecordsAndRequirementsHaveAnExactSessionOnlyException'
 --tests '*UnitReviewPipelineTest.committedUnitPackagesMatchTheGenerator'
 --tests '*CurriculumReadinessReconciliationTest.committedReportsMatchTheGeneratorAndStayByteIdentical'
 --tests '*OwnerGovernanceDecisionTest.preparationManifestBindsFactsAndDoesNotPassAFinalAudit'
 --tests '*GapClosureDossierTest.dossierMatchesLiveReadinessAndDoesNotAuthorizeRelease'
 --tests '*ReleaseSourceDigestTest.committedCandidateRecordsTheCurrentSourceDigestAndIdentity'
 --tests '*Units2630SourceIntakeTest.generatedRussianPenguinAudioAndReleaseStayClosed'
 --tests '*V2RebuildTest.structuralMappingSafeguardsRejectIncompleteOrInventedRecords'
 --tests '*BatchECapabilityResearchTest.digestsGovernanceAndLaterUnitsStayUnchanged'
)
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/parallel-tests.gradle testDebugUnitTest "${checks[@]}" compileDebugSources lintDebug verifyReleaseGateAttachment --max-workers=4 --console=plain
