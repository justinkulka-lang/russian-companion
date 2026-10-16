#!/usr/bin/env bash
set -euo pipefail
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
# Only the failed fixture method changed; original74 result is preserved separately.
# cloud-validation.gradle excludes all four screenshot exporters (16 methods).
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/parallel-tests.gradle testDebugUnitTest --tests '*MultiPoolReviewSelectionTest.unit6CanSelectEveryAuthoredCandidateAndResolveMissesIncludingOppositeKeyContrasts' compileDebugSources lintDebug verifyReleaseGateAttachment --max-workers=4 --console=plain
