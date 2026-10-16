#!/usr/bin/env bash
set -euo pipefail
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
# On the pinned pre-transition production source, copy BASELINE_EXPORT.kt.txt to
# app/src/test/java/dev/russiancompanion/v2/PolicyHistoryBaselineExportTest.kt first.
# Temporary exporter writes only the new fixture directory, never screenshots.
# cloud-validation.gradle excludes all four screenshot exporters (16 methods).
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/parallel-tests.gradle testDebugUnitTest --tests '*PolicyHistoryBaselineExportTest' --max-workers=4 --console=plain
