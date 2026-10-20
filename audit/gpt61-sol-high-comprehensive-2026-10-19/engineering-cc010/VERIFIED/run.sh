source /workspace/setup/android-env.sh
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/russian-companion/audit/independent-gpt61-2026-10-12/broader-regression-2026-10-18/broader-tests.gradle -Daudit.events.path=/workspace/artifacts/cc010-reuse/VERIFIED/EVENTS.jsonl testDebugUnitTest --tests dev.russiancompanion.v2.RepeatedTaskCreditTest --tests dev.russiancompanion.v2.RepeatedTaskRepairUiTest --tests dev.russiancompanion.v2.Unit10IndependentReinforcementTest --tests dev.russiancompanion.v2.Unit10ReinforcementReviewTest --tests dev.russiancompanion.v2.Unit23TimeInstructionTest --rerun compileDebugSources lintDebug verifyReleaseGateAttachment --continue --offline --max-workers=4 --console=plain > /workspace/artifacts/cc010-reuse/VERIFIED/RUN.log 2>&1
result=$?
printf "%s\n" "$result" > /workspace/artifacts/cc010-reuse/VERIFIED/RUN.exit
exit "$result"
