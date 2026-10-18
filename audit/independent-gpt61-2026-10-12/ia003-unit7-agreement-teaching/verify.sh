#!/usr/bin/env bash
set -euo pipefail
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
checks=(
 --tests '*Unit7AgreementTeachingTest'
)
# Four historical transition classes bind exact post-policy/pre-Unit7 source2ff71f19.
# All other selected current runtime/canonical methods use current source.
# Four screenshot exporters16methods excluded;not a complete-suite run.
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/parallel-tests.gradle testDebugUnitTest "${checks[@]}" compileDebugSources lintDebug verifyReleaseGateAttachment --max-workers=4 --console=plain
