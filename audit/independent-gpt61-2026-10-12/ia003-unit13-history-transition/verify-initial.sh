#!/usr/bin/env bash
set -euo pipefail
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
checks=(
 --tests '*Unit13WiderPolicyTest'
 --tests '*Unit13CumulativeReviewTest'
 --tests '*Unit14CumulativeReviewTest'
 --tests '*Unit15CumulativeReviewTest.ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup'
 --tests '*Unit11AgreementRemediationTest.completeAgreementTeachingPrecedesPracticeAndRepairRestoresTheCheckedForms'
 --tests '*Unit11AgreementRemediationTest.wrongIndependentAnswerShowsCompleteRepairAndGuidedSuccessStillNeedsANewCheckAnswer'
 --tests '*Unit13ContextTeachingTest'
 --tests '*Unit9WiderPolicyTest.genuineOldPartialTasksUseOwnEligibilityAndActualLegacyPolicyOnAdvance'
 --tests '*Unit9WiderPolicyTest.genuinePriorPackDeclaresV1AndCannotAcceptFutureV2Tasks'
 --tests '*Unit9WiderPolicyTest.versionsRolesLinksEpochAndPacklessWritesFailClosedWithoutChangingHistory'
 --tests '*CumulativeReviewGateTest'
 --tests '*CumulativeReviewStorageTest'
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
# Unit13 context-only whole-pack whitelist binds exact post-teaching8e2c6f1a source.
# Selected historical activation methods bind their already-recorded archives.
# All other selected current runtime/canonical methods use current source.
# Four screenshot exporters16methods remain excluded;not a complete-suite run.
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/parallel-tests.gradle testDebugUnitTest "${checks[@]}" compileDebugSources lintDebug verifyReleaseGateAttachment --max-workers=4 --console=plain
