#!/usr/bin/env bash
set -euo pipefail
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
checks=(
 --tests '*MultiPoolReviewSelectionTest'
 --tests '*CumulativeReviewSelectionTest'
 --tests '*CumulativeReviewGateTest'
 --tests '*CumulativeReviewStorageTest'
 --tests '*Unit5CumulativeReviewTest'
 --tests '*Unit6CumulativeReviewTest'
 --tests '*Unit4NominativeRoleTest'
 --tests '*IndeclinableCorrectionTest'
 --tests '*Unit30CumulativeReviewTest.ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup'
 --tests '*UnitReviewPipelineTest.committedUnitPackagesMatchTheGenerator'
 --tests '*CurriculumReadinessReconciliationTest.committedReportsMatchTheGeneratorAndStayByteIdentical'
 --tests '*CurriculumReadinessReconciliationTest.matrixUsesCanonicalDigestsReviewsAndDoesNotInventApproval'
 --tests '*OwnerGovernanceDecisionTest.preparationManifestBindsFactsAndDoesNotPassAFinalAudit'
 --tests '*GapClosureDossierTest.dossierMatchesLiveReadinessAndDoesNotAuthorizeRelease'
 --tests '*ReleaseSourceDigestTest.committedCandidateRecordsTheCurrentSourceDigestAndIdentity'
 --tests '*Units2630SourceIntakeTest.generatedRussianPenguinAudioAndReleaseStayClosed'
 --tests '*V2RebuildTest.structuralMappingSafeguardsRejectIncompleteOrInventedRecords'
 --tests '*BatchECapabilityResearchTest.digestsGovernanceAndLaterUnitsStayUnchanged'
)
# cloud-validation.gradle excludes the four screenshot exporters (16 methods).
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/parallel-tests.gradle testDebugUnitTest "${checks[@]}" compileDebugSources lintDebug verifyReleaseGateAttachment --max-workers=4 --console=plain
