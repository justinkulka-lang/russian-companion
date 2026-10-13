set -e
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
checks=(
  --tests '*Unit21ObjectTransferTest'
  --tests '*Unit21ReflexivePurposeInstructionTest'
  --tests '*Units2630SourceIntakeTest.retainedSourceBytesMatchTheFrozenIdentityTwice'
  --tests '*Units610InstructionalValidityTest.everyRecognitionObjectiveHasAnIndependentFeatureValidCheck'
  --tests '*LabExplanationCorrectionTest'
  --tests '*Unit9AgreementTransferTest.completedAgreementNeedsADifferentIndependentMasculineNoun'
  --tests '*Units2125SourceIntakeTest.frozenSourceIdentityAndProvenanceSpansAreRequired'
  --tests '*Units2125SourceIntakeTest.noPenguinContentInternetPermissionOrOpenReleaseGate'
  --tests '*Units2125SourceIntakeTest.structuralMappingsAreDigestBoundAndStayUnsigned'
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
