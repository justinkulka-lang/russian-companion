#!/usr/bin/env bash
set -euo pipefail
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
checks=(
 --tests '*Unit9WiderPolicyTest'
 --tests '*Unit9CumulativeReviewTest'
 --tests '*Unit10CumulativeReviewTest'
 --tests '*Unit11CumulativeReviewTest.ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup'
 --tests '*Unit3GenderTeachingTest'
 --tests '*Unit6WiderPolicyTest.genuineOldPartialTasksUseOwnEligibilityAndActualLegacyPolicyOnAdvance'
 --tests '*Unit6WiderPolicyTest.genuinePriorPackDeclaresV1AndCannotAcceptFutureV2Tasks'
 --tests '*Unit6WiderPolicyTest.versionsRolesLinksEpochAndPacklessWritesFailClosedWithoutChangingHistory'
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
# Unit6WiderPolicyTest deliberately uses its exact archived pre-Unit3-teaching source.
# Current Unit9/10/11 paths use the current source. Four screenshot exporters16methods
# remain excluded by cloud-validation.gradle. Not a complete-suite run.
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/parallel-tests.gradle testDebugUnitTest "${checks[@]}" compileDebugSources lintDebug verifyReleaseGateAttachment --max-workers=4 --console=plain
