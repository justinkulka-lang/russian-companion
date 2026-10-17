#!/usr/bin/env bash
set -euo pipefail
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
checks=(
 --tests '*Unit13PastTeachingTest'
 --tests '*Unit13CumulativeReviewTest.ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup'
 --tests '*Unit14CumulativeReviewTest.ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup'
 --tests '*Unit9WiderPolicyTest.allGenuineLifecycleBackupsRestoreReopenAndRetainExactRowsAndCutoff'
 --tests '*Unit9WiderPolicyTest.fiveGenderCandidatesRequireCurrentTaughtEvidenceAndExactRepairWithoutChangingAssetTuples'
 --tests '*Unit11AgreementRemediationTest.completeAgreementTeachingPrecedesPracticeAndRepairRestoresTheCheckedForms'
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
# Unit9's whole-pack transition whitelist alone uses its exact historical post-transition source.
# Its selection/eligibility assertions and all new runtime/repair cases use current source.
# Four screenshot exporters16methods excluded by cloud-validation.gradle. Not a full suite.
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/parallel-tests.gradle testDebugUnitTest "${checks[@]}" compileDebugSources lintDebug verifyReleaseGateAttachment --max-workers=4 --console=plain
