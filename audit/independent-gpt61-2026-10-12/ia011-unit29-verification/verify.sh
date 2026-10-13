set -e
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/parallel-tests.gradle testDebugUnitTest \
 --tests '*Unit29ShortAdjectiveCorrectionTest' \
 --tests '*Unit29ActiveTimeCorrectionTest' \
 --tests '*LabExplanationCorrectionTest' \
 --tests '*Unit29InstructionTest' \
 --tests '*V2RebuildTest.structuralMappingSafeguardsRejectIncompleteOrInventedRecords' \
 --tests '*UnitReviewPipelineTest.committedUnitPackagesMatchTheGenerator' \
 --tests '*CurriculumReadinessReconciliationTest.committedReportsMatchTheGeneratorAndStayByteIdentical' \
 --tests '*OwnerGovernanceDecisionTest.preparationManifestBindsFactsAndDoesNotPassAFinalAudit' \
 --tests '*GapClosureDossierTest.dossierMatchesLiveReadinessAndDoesNotAuthorizeRelease' \
 --tests '*ReleaseSourceDigestTest.committedCandidateRecordsTheCurrentSourceDigestAndIdentity' \
 --tests '*BatchECapabilityResearchTest.digestsGovernanceAndLaterUnitsStayUnchanged' \
 --tests '*Units2630SourceIntakeTest.retainedSourceBytesMatchTheFrozenIdentityTwice' \
 --tests '*Units2630SourceIntakeTest.admittedCapabilitiesBindSpanUnitObjectiveAndClaim' \
 --tests '*Units2630SourceIntakeTest.generatedRussianPenguinAudioAndReleaseStayClosed' \
 --tests '*Units2630SourceIntakeTest.partialStrandsDoNotClearBroadClaimsAndOptionalMaterialDoesNotGate' \
 --tests '*Units2630StructuralMappingTest.preparedLateUnitIsNotPermanentlyBlockedFromAdvancement' \
 --tests '*Units2630StructuralMappingTest.russianWithoutProvenanceIsRejectedAndPenguinStaysOut' \
 --tests '*CompleteRuleCorrectionTest.coordinatedKeyAndApplicationEditsCannotReverseTheFrozenContext' \
 --tests '*Unit9QuantityRuleCorrectionTest.coordinatedKeyAndApplicationEditsCannotReverseTheTeenContext' \
 --tests '*Unit9QuantityRuleCorrectionTest.threeChoiceContextCannotSwapItsAnswerAndApplicationTogether' \
 compileDebugSources lintDebug verifyReleaseGateAttachment --max-workers=4 --console=plain
