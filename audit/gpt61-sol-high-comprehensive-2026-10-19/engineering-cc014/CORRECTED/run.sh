source /workspace/setup/android-env.sh
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/russian-companion/audit/independent-gpt61-2026-10-12/broader-regression-2026-10-18/broader-tests.gradle -Daudit.events.path=/workspace/artifacts/cc014-preview/CORRECTED/EVENTS.jsonl testDebugUnitTest --tests dev.russiancompanion.v2.RestorePreviewReviewHistoryTest --tests dev.russiancompanion.v2.RestorePreviewReviewIntegrationTest --tests dev.russiancompanion.v2.BackupLicenseTest.backupRoundTripRestoresGovernedRecordsWithoutChangingTheLiveCopy --rerun compileDebugSources lintDebug verifyReleaseGateAttachment --continue --offline --max-workers=4 --console=plain > /workspace/artifacts/cc014-preview/CORRECTED/RUN.log 2>&1
result=$?
printf "%s\n" "$result" > /workspace/artifacts/cc014-preview/CORRECTED/RUN.exit
exit "$result"
