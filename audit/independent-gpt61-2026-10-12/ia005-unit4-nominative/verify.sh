set -e
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
checks=(
 --tests '*Unit4NominativeRoleTest'
 --tests '*Units35SourceIntakeTest'
 --tests '*Unit6AnimacyTransferTest.unit6ScoredEvidenceAndEveryPriorExampleRecordRemainWhole'
 --tests '*Unit8PluralInstructionTest.unit8ScoredObjectsAndAllPriorNativeExamplesAndProvenanceRemainIdentical'
 --tests '*Unit13ReportedTransferTest.originalIndependentItemsEarlierObjectiveAndAllExamplesRemainUnchanged'
 --tests '*Unit28TimingTransferTest.originalIndependentScoredFieldsAndEveryExampleRemainUnchanged'
 --tests '*Unit9ReinforcementReviewTest'
 --tests '*Unit10IndependentReinforcementTest'
 --tests '*Unit10ReinforcementReviewTest'
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
