#!/usr/bin/env bash
set -euo pipefail
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
checks=(
 --tests '*Unit16CumulativeReviewTest'
 --tests '*CumulativeReviewGateTest'
 --tests '*CumulativeReviewStorageTest'
 --tests '*Unit15CumulativeReviewTest.ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup'
 --tests '*Unit15CumulativeReviewTest.schema11RowsRemainStoredButChangedImperativeInstructionRequiresRepair'
 --tests '*Unit4NominativeRoleTest'
 --tests '*Unit15ImperativeRemediationTest'
 --tests '*Unit15FeedbackCorrectionTest'
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
