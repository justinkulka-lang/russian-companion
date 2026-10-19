source /workspace/setup/android-env.sh
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/russian-companion/audit/independent-gpt61-2026-10-12/broader-regression-2026-10-18/broader-tests.gradle -Daudit.events.path=/workspace/artifacts/cc008-tuples/FINAL/EVENTS.jsonl testDebugUnitTest --tests dev.russiancompanion.v2.CurrentPhase2TupleMutationTest --tests dev.russiancompanion.v2.FullSynthesisTupleCorrectionTest --tests dev.russiancompanion.v2.Phase2BoundedClaimsTest --rerun compileDebugSources lintDebug verifyReleaseGateAttachment --continue --offline --max-workers=4 --console=plain > /workspace/artifacts/cc008-tuples/FINAL/RUN.log 2>&1
result=$?
printf "%s\n" "$result" > /workspace/artifacts/cc008-tuples/FINAL/RUN.exit
exit "$result"
