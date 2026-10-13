set -e
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
checks=(
  --tests '*Unit29PunctuationContextRegressionTest'
  --tests '*Unit20DirectionalityApplicationTest.motionCompletionMustRequireDistinctIndependentDirectionalityContexts'
  --tests '*Unit29InstructionTest'
  --tests '*Unit29ShortAdjectiveCorrectionTest.grammarGateMustNotBeSatisfiedByVocabularyGlosses'
  --tests '*Unit29ActiveTimeCorrectionTest.creditedCheckMustInterpretADistinctAdmittedFormRatherThanTheOppositeLabel'
  --tests '*Unit30LanguageCorrectionTest.languageCompletionMustNotDependOnEnglishTableOrHeadingAnswers'
  --tests '*LabExplanationCorrectionTest'
  --tests '*V2RebuildTest.structuralMappingSafeguardsRejectIncompleteOrInventedRecords'
  --tests '*UnitReviewPipelineTest.committedUnitPackagesMatchTheGenerator'
  --tests '*CurriculumReadinessReconciliationTest.committedReportsMatchTheGeneratorAndStayByteIdentical'
  --tests '*OwnerGovernanceDecisionTest.preparationManifestBindsFactsAndDoesNotPassAFinalAudit'
  --tests '*GapClosureDossierTest.dossierMatchesLiveReadinessAndDoesNotAuthorizeRelease'
  --tests '*ReleaseSourceDigestTest.committedCandidateRecordsTheCurrentSourceDigestAndIdentity'
  --tests '*BatchECapabilityResearchTest.digestsGovernanceAndLaterUnitsStayUnchanged'
  --tests '*Units2630SourceIntakeTest.retainedSourceBytesMatchTheFrozenIdentityTwice'
  --tests '*Units2630SourceIntakeTest.admittedCapabilitiesBindSpanUnitObjectiveAndClaim'
  --tests '*Units2630SourceIntakeTest.partialStrandsDoNotClearBroadClaimsAndOptionalMaterialDoesNotGate'
  --tests '*Units2630SourceIntakeTest.generatedRussianPenguinAudioAndReleaseStayClosed'
  --tests '*BatchAUnits71620Test.admittedCapabilitiesRejectForgeryCrossBindingAndWrongSpans'
  --tests '*CompleteRuleCorrectionTest.coordinatedKeyAndApplicationEditsCannotReverseTheFrozenContext'
  --tests '*Unit9QuantityRuleCorrectionTest.coordinatedKeyAndApplicationEditsCannotReverseTheTeenContext'
  --tests '*Unit9QuantityRuleCorrectionTest.threeChoiceContextCannotSwapItsAnswerAndApplicationTogether'
)
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/parallel-tests.gradle testDebugUnitTest "${checks[@]}" compileDebugSources lintDebug verifyReleaseGateAttachment --max-workers=4 --console=plain
