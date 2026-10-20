source /workspace/setup/android-env.sh
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/russian-companion/audit/independent-gpt61-2026-10-12/broader-regression-2026-10-18/broader-tests.gradle -Daudit.events.path=/workspace/artifacts/cc017-reference-search/BASELINE/EVENTS.jsonl testDebugUnitTest --tests dev.russiancompanion.v2.ReferenceDetailSearchTest --rerun compileDebugSources lintDebug verifyReleaseGateAttachment --continue --offline --max-workers=4 --console=plain > /workspace/artifacts/cc017-reference-search/BASELINE/RUN.log 2>&1
result=$?
printf "%s\n" "$result" > /workspace/artifacts/cc017-reference-search/BASELINE/RUN.exit
exit "$result"
