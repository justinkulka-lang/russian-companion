set -e
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
checks=(
 --tests '*Unit28TimingTransferTest'
 --tests '*Unit28InstructionTest'
 --tests '*Unit27RemainingInstructionTest'
 --tests '*Units2630SourceIntakeTest.retainedSourceBytesMatchTheFrozenIdentityTwice'
 --tests '*Units2630SourceIntakeTest.admittedCapabilitiesBindSpanUnitObjectiveAndClaim'
 --tests '*Units610InstructionalValidityTest.everyRecognitionObjectiveHasAnIndependentFeatureValidCheck'
 --tests '*LabExplanationCorrectionTest'
 --tests '*UnitReviewPipelineTest.committedUnitPackagesMatchTheGenerator'
 --tests '*CurriculumReadinessReconciliationTest.committedReportsMatchTheGeneratorAndStayByteIdentical'
 --tests '*OwnerGovernanceDecisionTest.preparationManifestBindsFactsAndDoesNotPassAFinalAudit'
 --tests '*GapClosureDossierTest.dossierMatchesLiveReadinessAndDoesNotAuthorizeRelease'
 --tests '*ReleaseSourceDigestTest.committedCandidateRecordsTheCurrentSourceDigestAndIdentity'
 --tests '*BatchECapabilityResearchTest.digestsGovernanceAndLaterUnitsStayUnchanged'
 --tests '*V2RebuildTest.structuralMappingSafeguardsRejectIncompleteOrInventedRecords'
 --tests '*CompleteRuleCorrectionTest.coordinatedKeyAndApplicationEditsCannotReverseTheFrozenContext'
 --tests '*Unit24ContextTransferTest.correctIndependentPersuasionItemsAndOptionalOriginalReadingArePreserved'
)
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/parallel-tests.gradle testDebugUnitTest "${checks[@]}" compileDebugSources lintDebug verifyReleaseGateAttachment --max-workers=4 --console=plain
