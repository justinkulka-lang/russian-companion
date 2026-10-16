#!/usr/bin/env bash
set -euo pipefail
cd /workspace/russian-companion
source /workspace/setup/android-env.sh
# Only current digest references changed; initial97 result is preserved separately.
# Four screenshot exporters/16 methods remain excluded by cloud-validation.gradle.
./gradlew -I /workspace/setup/cloud-validation.gradle -I /workspace/setup/parallel-tests.gradle testDebugUnitTest --tests '*GapClosureDossierTest.dossierMatchesLiveReadinessAndDoesNotAuthorizeRelease' --tests '*Units2630SourceIntakeTest.generatedRussianPenguinAudioAndReleaseStayClosed' --tests '*Units2125SourceIntakeTest.structuralMappingsAreDigestBoundAndStayUnsigned' --tests '*Units1115MappingGapTest.preservedUnitDigestsDoNotMoveWhenTheLaterGapIsRecorded' compileDebugSources lintDebug verifyReleaseGateAttachment --max-workers=4 --console=plain
