source /workspace/setup/android-env.sh
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/russian-companion/audit/independent-gpt61-2026-10-12/broader-regression-2026-10-18/broader-tests.gradle -Daudit.events.path=/workspace/artifacts/cc001-options/FINAL/EVENTS.jsonl testDebugUnitTest --tests dev.russiancompanion.v2.OptionPresentationTest --tests dev.russiancompanion.v2.OptionPresentationUiTest --tests dev.russiancompanion.v2.CumulativeReviewStorageTest --tests dev.russiancompanion.v2.CumulativeReviewSelectionTest --rerun compileDebugSources lintDebug verifyReleaseGateAttachment --continue --offline --max-workers=4 --console=plain > /workspace/artifacts/cc001-options/FINAL/RUN.log 2>&1
result=$?
printf "%s\n" "$result" > /workspace/artifacts/cc001-options/FINAL/RUN.exit
exit "$result"
