#!/usr/bin/env bash
set -euo pipefail
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
# Two lifecycle regression methods only, not a complete suite.
# Four screenshot exporters (16 methods) are excluded by cloud-validation.gradle.
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/parallel-tests.gradle testDebugUnitTest --tests '*CumulativePolicyHistoryBaselineTest' compileDebugSources --max-workers=4 --console=plain
