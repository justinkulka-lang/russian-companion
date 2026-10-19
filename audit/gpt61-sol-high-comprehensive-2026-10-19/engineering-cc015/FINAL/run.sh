source /workspace/setup/android-env.sh
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/russian-companion/audit/independent-gpt61-2026-10-12/broader-regression-2026-10-18/broader-tests.gradle -Daudit.events.path=/workspace/artifacts/cc015-present/FINAL/EVENTS.jsonl testDebugUnitTest --tests dev.russiancompanion.v2.Unit4PresentTeachingTest --tests dev.russiancompanion.v2.Unit4PresentRepairUiTest --tests dev.russiancompanion.v2.Unit4NominativeRoleTest --tests dev.russiancompanion.v2.Unit4CumulativeReviewTest --tests dev.russiancompanion.v2.OptionPresentationTest --tests dev.russiancompanion.v2.OptionPresentationUiTest --rerun compileDebugSources lintDebug verifyReleaseGateAttachment --continue --offline --max-workers=4 --console=plain > /workspace/artifacts/cc015-present/FINAL/RUN.log 2>&1
result=$?
printf "%s\n" "$result" > /workspace/artifacts/cc015-present/FINAL/RUN.exit
exit "$result"
