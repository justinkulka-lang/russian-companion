#!/usr/bin/env bash
set -euo pipefail
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
checks=(
 --tests '*Unit14CumulativeReviewTest'
)
# Unit13 context-only whole-pack whitelist binds exact post-teaching8e2c6f1a source.
# Selected historical activation methods bind their already-recorded archives.
# All other selected current runtime/canonical methods use current source.
# Four screenshot exporters16methods remain excluded;not a complete-suite run.
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/parallel-tests.gradle testDebugUnitTest "${checks[@]}" compileDebugSources lintDebug verifyReleaseGateAttachment --max-workers=4 --console=plain
