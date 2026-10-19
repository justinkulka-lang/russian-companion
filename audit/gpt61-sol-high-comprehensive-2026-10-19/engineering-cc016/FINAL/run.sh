source /workspace/setup/android-env.sh
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/russian-companion/audit/independent-gpt61-2026-10-12/broader-regression-2026-10-18/broader-tests.gradle -Daudit.events.path=/workspace/artifacts/cc016-fragments/FINAL/EVENTS.jsonl testDebugUnitTest --tests dev.russiancompanion.v2.FragmentGlossTeachingTest --tests dev.russiancompanion.v2.FragmentGlossUiTest --tests dev.russiancompanion.v2.Unit7FeedbackCorrectionTest --tests dev.russiancompanion.v2.Unit10FeedbackCorrectionTest --tests dev.russiancompanion.v2.OptionPresentationTest --rerun compileDebugSources lintDebug verifyReleaseGateAttachment --continue --offline --max-workers=4 --console=plain > /workspace/artifacts/cc016-fragments/FINAL/RUN.log 2>&1
result=$?
printf "%s\n" "$result" > /workspace/artifacts/cc016-fragments/FINAL/RUN.exit
exit "$result"
