#!/usr/bin/env bash
set -euo pipefail
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
checks=(
 --tests '*CumulativeReviewGateTest'
 --tests '*CumulativeReviewStorageTest'
 --tests '*Unit4CumulativeReviewTest'
 --tests '*BackupLicenseTest'
 --tests '*DatabaseMigrationTest'
 --tests '*BackupSnapshotCorrectionTest'
 --tests '*RuntimeCorrectionTest.todayOpensAdvancementReviewInsteadOfTheLastLessonStep'
 --tests '*RuntimeCorrectionTest.unitOneSequenceKeepsDraftsThroughRemediationResumeAndAdvancement'
 --tests '*RuntimeCorrectionTest.failedAdvancementLeavesTheReviewInPlace'
 --tests '*RuntimeCorrectionTest.remediationReachesGuidedPracticeThenReturnsToTheFailedCheck'
 --tests '*RuntimeCorrectionTest.storeWorkRunsOnTheStoreThread'
 --tests '*RuntimeCorrectionTest.failedWritePreservesPriorDataAndShowsOneFailure'
 --tests '*Unit4NominativeRoleTest'
 --tests '*UnitReviewPipelineTest.committedUnitPackagesMatchTheGenerator'
 --tests '*CurriculumReadinessReconciliationTest.committedReportsMatchTheGeneratorAndStayByteIdentical'
 --tests '*OwnerGovernanceDecisionTest.preparationManifestBindsFactsAndDoesNotPassAFinalAudit'
 --tests '*GapClosureDossierTest.dossierMatchesLiveReadinessAndDoesNotAuthorizeRelease'
 --tests '*ReleaseSourceDigestTest.committedCandidateRecordsTheCurrentSourceDigestAndIdentity'
 --tests '*Units2630SourceIntakeTest.generatedRussianPenguinAudioAndReleaseStayClosed'
 --tests '*V2RebuildTest.structuralMappingSafeguardsRejectIncompleteOrInventedRecords'
 --tests '*BatchECapabilityResearchTest.digestsGovernanceAndLaterUnitsStayUnchanged'
)
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/parallel-tests.gradle \
 testDebugUnitTest "${checks[@]}" compileDebugSources lintDebug verifyReleaseGateAttachment --max-workers=4 --console=plain
