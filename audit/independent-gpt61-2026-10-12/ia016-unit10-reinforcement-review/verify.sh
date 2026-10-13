set -e
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/parallel-tests.gradle testDebugUnitTest --tests '*Unit10ReinforcementReviewTest' --tests '*CompleteRuleCorrectionTest.coordinatedKeyAndApplicationEditsCannotReverseTheFrozenContext' --max-workers=4 --console=plain
