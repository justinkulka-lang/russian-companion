set -e
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
checks=(
 --tests '*Unit13ReportedTransferTest'
 --tests '*Unit13FeedbackCorrectionTest'
 --tests '*Units1115InstructionalValidityTest.everyRecognitionObjectiveHasAnIndependentFeatureValidCheck'
 --tests '*Unit28TimingTransferTest.originalSixGovernedAnswersCompleteBothTimingObjectives'
 --tests '*Units2630SourceIntakeTest.retainedSourceBytesMatchTheFrozenIdentityTwice'
 --tests '*Units2630SourceIntakeTest.generatedRussianPenguinAudioAndReleaseStayClosed'
 --tests '*LabExplanationCorrectionTest'
 --tests '*UnitReviewPipelineTest.committedUnitPackagesMatchTheGenerator'
 --tests '*CurriculumReadinessReconciliationTest.committedReportsMatchTheGeneratorAndStayByteIdentical'
 --tests '*OwnerGovernanceDecisionTest.preparationManifestBindsFactsAndDoesNotPassAFinalAudit'
 --tests '*GapClosureDossierTest.dossierMatchesLiveReadinessAndDoesNotAuthorizeRelease'
 --tests '*ReleaseSourceDigestTest.committedCandidateRecordsTheCurrentSourceDigestAndIdentity'
 --tests '*BatchECapabilityResearchTest.digestsGovernanceAndLaterUnitsStayUnchanged'
 --tests '*V2RebuildTest.structuralMappingSafeguardsRejectIncompleteOrInventedRecords'
 --tests '*CompleteRuleCorrectionTest.coordinatedKeyAndApplicationEditsCannotReverseTheFrozenContext'
)
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/parallel-tests.gradle testDebugUnitTest "${checks[@]}" compileDebugSources lintDebug verifyReleaseGateAttachment --max-workers=4 --console=plain
