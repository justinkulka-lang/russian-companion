set -e
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
checks=(
  --tests '*Unit9AgreementTransferTest'
  --tests '*Unit9NumberFeedbackCorrectionTest'
  --tests '*Units1115MappingGapTest.preservedUnitDigestsDoNotMoveWhenTheLaterGapIsRecorded'
  --tests '*Units610InstructionalValidityTest.everyRecognitionObjectiveHasAnIndependentFeatureValidCheck'
  --tests '*LabExplanationCorrectionTest'
  --tests '*Unit8AgreementTransferTest.completedAgreementNeedsADifferentIndependentAdjective'
  --tests '*Units610SourceIntakeTest.retainedSourceBytesMatchImmutableRevisions'
  --tests '*Units610SourceIntakeTest.noPenguinAudioOrNetworkWasAdded'
  --tests '*Units610SourceIntakeTest.governedContrastsStaySourceBoundAndBookAttestationDoesNotAdvance'
  --tests '*UnitReviewPipelineTest.committedUnitPackagesMatchTheGenerator'
  --tests '*CurriculumReadinessReconciliationTest.committedReportsMatchTheGeneratorAndStayByteIdentical'
  --tests '*OwnerGovernanceDecisionTest.preparationManifestBindsFactsAndDoesNotPassAFinalAudit'
  --tests '*GapClosureDossierTest.dossierMatchesLiveReadinessAndDoesNotAuthorizeRelease'
  --tests '*ReleaseSourceDigestTest.committedCandidateRecordsTheCurrentSourceDigestAndIdentity'
  --tests '*BatchECapabilityResearchTest.digestsGovernanceAndLaterUnitsStayUnchanged'
  --tests '*V2RebuildTest.structuralMappingSafeguardsRejectIncompleteOrInventedRecords'
  --tests '*CompleteRuleCorrectionTest.coordinatedKeyAndApplicationEditsCannotReverseTheFrozenContext'
  --tests '*Unit9QuantityRuleCorrectionTest.coordinatedKeyAndApplicationEditsCannotReverseTheTeenContext'
)
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/parallel-tests.gradle testDebugUnitTest "${checks[@]}" compileDebugSources lintDebug verifyReleaseGateAttachment --max-workers=4 --console=plain
