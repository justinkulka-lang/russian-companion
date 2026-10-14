set -e
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
checks=(
 --tests '*Unit6AnimacyTransferTest'
 --tests '*Unit6FeedbackCorrectionTest'
 --tests '*Unit8PluralInstructionTest.unit8ScoredObjectsAndAllPriorNativeExamplesAndProvenanceRemainIdentical'
 --tests '*Unit13ReportedTransferTest.originalIndependentItemsEarlierObjectiveAndAllExamplesRemainUnchanged'
 --tests '*Unit28TimingTransferTest.originalIndependentScoredFieldsAndEveryExampleRemainUnchanged'
 --tests '*Units610InstructionalValidityTest.everyRecognitionObjectiveHasAnIndependentFeatureValidCheck'
 --tests '*CompleteRuleCorrectionTest.coordinatedKeyAndApplicationEditsCannotReverseTheFrozenContext'
 --tests '*LabExplanationCorrectionTest'
 --tests '*UnitReviewPipelineTest.committedUnitPackagesMatchTheGenerator'
 --tests '*CurriculumReadinessReconciliationTest.committedReportsMatchTheGeneratorAndStayByteIdentical'
 --tests '*OwnerGovernanceDecisionTest.preparationManifestBindsFactsAndDoesNotPassAFinalAudit'
 --tests '*GapClosureDossierTest.dossierMatchesLiveReadinessAndDoesNotAuthorizeRelease'
 --tests '*ReleaseSourceDigestTest.committedCandidateRecordsTheCurrentSourceDigestAndIdentity'
 --tests '*Units2630SourceIntakeTest.generatedRussianPenguinAudioAndReleaseStayClosed'
 --tests '*Units2630SourceIntakeTest.retainedSourceBytesMatchTheFrozenIdentityTwice'
 --tests '*V2RebuildTest.structuralMappingSafeguardsRejectIncompleteOrInventedRecords'
 --tests '*BatchECapabilityResearchTest.digestsGovernanceAndLaterUnitsStayUnchanged'
)
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/parallel-tests.gradle testDebugUnitTest "${checks[@]}" compileDebugSources lintDebug verifyReleaseGateAttachment --max-workers=4 --console=plain
