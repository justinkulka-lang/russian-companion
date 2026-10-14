#!/usr/bin/env bash
set -euo pipefail
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
# Historical diagnostic source is deliberately outside normal app test sources.
# Keep the Android source-set override outside the checkout as an onboarding helper.
test -f /workspace/setup/ia003-diagnostics.gradle
git diff --exit-code 11d70e5e4512ff93758ebf246f2a4773f83ca0e0 -- app/src/main
./gradlew -I /workspace/setup/cloud-validation.gradle \
 -I /workspace/setup/parallel-tests.gradle -I /workspace/setup/ia003-diagnostics.gradle \
 testDebugUnitTest --tests '*Ia003CumulativeReviewBaselineTest' \
 compileDebugSources lintDebug verifyReleaseGateAttachment --max-workers=4 --console=plain
