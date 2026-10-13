set -e
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/parallel-tests.gradle testDebugUnitTest \
 --tests '*Unit20DirectionalityApplicationTest' \
 --tests '*Unit19PrefixUnit20MotionInstructionTest' \
 --tests '*Unit30LanguageCorrectionTest.languageCompletionMustNotDependOnEnglishTableOrHeadingAnswers' \
 --tests '*LabExplanationCorrectionTest' \
 --tests '*V2RebuildTest.structuralMappingSafeguardsRejectIncompleteOrInventedRecords' \
 --tests '*UnitReviewPipelineTest.committedUnitPackagesMatchTheGenerator' \
 --tests '*CurriculumReadinessReconciliationTest.committedReportsMatchTheGeneratorAndStayByteIdentical' \
 --tests '*OwnerGovernanceDecisionTest.preparationManifestBindsFactsAndDoesNotPassAFinalAudit' \
 --tests '*GapClosureDossierTest.dossierMatchesLiveReadinessAndDoesNotAuthorizeRelease' \
 --tests '*ReleaseSourceDigestTest.committedCandidateRecordsTheCurrentSourceDigestAndIdentity' \
 --tests '*BatchECapabilityResearchTest.digestsGovernanceAndLaterUnitsStayUnchanged' \
 --tests '*Units1620SourceIntakeTest.frozenSourceBytesAndRevisionsStayImmutable' \
 --tests '*Units1620SourceIntakeTest.everyRussianSpanOccursInItsRecordedSource' \
 --tests '*Units1620SourceIntakeTest.unitTwentyCannotBecomeContentCompleteFromARedLinkVerbPageOrDictionaryRows' \
 --tests '*Units1620SourceIntakeTest.noPenguinContentOrPrivatePdfEnteredGit' \
 --tests '*BatchAUnits71620Test.admittedCapabilitiesRejectForgeryCrossBindingAndWrongSpans' \
 --tests '*CompleteRuleCorrectionTest.coordinatedKeyAndApplicationEditsCannotReverseTheFrozenContext' \
 --tests '*Unit9QuantityRuleCorrectionTest.coordinatedKeyAndApplicationEditsCannotReverseTheTeenContext' \
 --tests '*Unit9QuantityRuleCorrectionTest.threeChoiceContextCannotSwapItsAnswerAndApplicationTogether' \
 compileDebugSources lintDebug verifyReleaseGateAttachment --max-workers=4 --console=plain
