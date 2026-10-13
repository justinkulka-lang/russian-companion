set -e
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
checks=(
  --tests '*Unit17QuestionTransferTest'
  --tests '*Unit17AgeCalendarInstructionTest'
  --tests '*Unit17NumberInstructionTest'
  --tests '*LabExplanationCorrectionTest'
  --tests '*Units1620SourceIntakeTest.frozenSourceBytesAndRevisionsStayImmutable'
  --tests '*Units1620SourceIntakeTest.everyRussianSpanOccursInItsRecordedSource'
  --tests '*Units1620SourceIntakeTest.noPenguinContentOrPrivatePdfEnteredGit'
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
