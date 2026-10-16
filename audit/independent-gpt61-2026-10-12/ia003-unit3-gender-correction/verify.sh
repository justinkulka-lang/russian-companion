#!/usr/bin/env bash
set -euo pipefail
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
# Three current package references only;runtime/source63 result preserved separately.
# Four screenshot exporters16methods remain excluded by cloud-validation.gradle.
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/parallel-tests.gradle testDebugUnitTest --tests '*Units610SourceIntakeTest.earlierUnitDigestsStayPutAndLaterUnitsStayIsolated' compileDebugSources lintDebug verifyReleaseGateAttachment --max-workers=4 --console=plain
