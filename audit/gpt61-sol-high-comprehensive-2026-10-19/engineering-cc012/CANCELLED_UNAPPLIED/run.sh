source /workspace/setup/android-env.sh
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/russian-companion/audit/independent-gpt61-2026-10-12/broader-regression-2026-10-18/broader-tests.gradle -Daudit.events.path=/workspace/artifacts/cc012-sequencing/FINAL/EVENTS.jsonl testDebugUnitTest --tests dev.russiancompanion.v2.Unit8ExistenceSequenceTest --tests dev.russiancompanion.v2.Unit8ExistenceHistoryTest --tests dev.russiancompanion.v2.Unit8ExistenceUiTest --tests dev.russiancompanion.v2.Unit8CumulativeReviewTest --tests dev.russiancompanion.v2.Unit8AgreementTransferTest --rerun compileDebugSources lintDebug verifyReleaseGateAttachment --continue --offline --max-workers=4 --console=plain > /workspace/artifacts/cc012-sequencing/FINAL/RUN.log 2>&1
result=$?
printf "%s\n" "$result" > /workspace/artifacts/cc012-sequencing/FINAL/RUN.exit
exit "$result"
