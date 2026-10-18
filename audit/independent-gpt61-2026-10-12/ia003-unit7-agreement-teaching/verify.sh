#!/usr/bin/env bash
set -euo pipefail
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
checks=(
 --tests '*Unit7AgreementTeachingTest'
 --tests '*Unit7CumulativeReviewTest.ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup'
 --tests '*Unit8CumulativeReviewTest.ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup'
 --tests '*Unit8CumulativeReviewTest.actualUnit8ContinueNominativeAgreementCueTeachingPracticeReturnAndAdvanceStayOnUnit8'
 --tests '*Unit13CumulativeReviewTest.ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup'
 --tests '*Unit30CumulativeReviewTest.ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup'
 --tests '*Unit9WiderPolicyTest.allGenuineLifecycleBackupsRestoreReopenAndRetainExactRowsAndCutoff'
 --tests '*Unit13WiderPolicyTest.allGenuineLifecycleBackupsRestoreReopenAndRetainExactRowsAndCutoff'
 --tests '*Unit13PastTeachingTest.completePastTeachingPrecedesPracticeAndRepairRestoresAllFiveExamples'
 --tests '*Unit13ContextTeachingTest.genuineBackupsRetainRawHistoryButChangedTeachingRejectsOldCreditAfterReopen'
 --tests '*UnitReviewPipelineTest.committedUnitPackagesMatchTheGenerator'
 --tests '*CurriculumReadinessReconciliationTest.committedReportsMatchTheGeneratorAndStayByteIdentical'
 --tests '*CurriculumReadinessReconciliationTest.matrixUsesCanonicalDigestsReviewsAndDoesNotInventApproval'
 --tests '*OwnerGovernanceDecisionTest.preparationManifestBindsFactsAndDoesNotPassAFinalAudit'
 --tests '*GapClosureDossierTest.dossierMatchesLiveReadinessAndDoesNotAuthorizeRelease'
 --tests '*ReleaseSourceDigestTest.committedCandidateRecordsTheCurrentSourceDigestAndIdentity'
 --tests '*Units2630SourceIntakeTest.generatedRussianPenguinAudioAndReleaseStayClosed'
 --tests '*V2RebuildTest.structuralMappingSafeguardsRejectIncompleteOrInventedRecords'
 --tests '*BatchECapabilityResearchTest.digestsGovernanceAndLaterUnitsStayUnchanged'
 --tests '*Units2125SourceIntakeTest.structuralMappingsAreDigestBoundAndStayUnsigned'
 --tests '*Units1115MappingGapTest.preservedUnitDigestsDoNotMoveWhenTheLaterGapIsRecorded'
 --tests '*Units610SourceIntakeTest.earlierUnitDigestsStayPutAndLaterUnitsStayIsolated'
)
# Four historical transition classes bind exact post-policy/pre-Unit7 source2ff71f19.
# All other selected current runtime/canonical methods use current source.
# Four screenshot exporters16methods excluded;not a complete-suite run.
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/parallel-tests.gradle testDebugUnitTest "${checks[@]}" compileDebugSources lintDebug verifyReleaseGateAttachment --max-workers=4 --console=plain
