#!/usr/bin/env bash
set -euo pipefail
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/parallel-tests.gradle testDebugUnitTest --tests '*Unit12CumulativeReviewTest.oldFormatOneAndTwoKeepRawUnit12HistoryWithoutInventingCreditOrRepairingUnit11' --tests '*GapClosureDossierTest.dossierMatchesLiveReadinessAndDoesNotAuthorizeRelease' compileDebugSources lintDebug verifyReleaseGateAttachment --max-workers=4 --console=plain
