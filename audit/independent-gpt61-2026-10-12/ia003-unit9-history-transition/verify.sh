#!/usr/bin/env bash
set -euo pipefail
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
checks=(
 --tests '*Unit9WiderPolicyTest'
 --tests '*Unit3GenderTeachingTest.correctionHasAnExactWhitelistAndNoScoredOrPolicyChanges'
)
# Unit3 whitelist is historical against its exact corrected source;all9 new Unit9 cases use current source.
# Initial72 results retained separately. Four screenshot exporters16methods remain excluded.
# Not a fresh72 or complete-suite result.
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/parallel-tests.gradle testDebugUnitTest "${checks[@]}" compileDebugSources lintDebug verifyReleaseGateAttachment --max-workers=4 --console=plain
