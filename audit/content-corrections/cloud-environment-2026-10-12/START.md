# Android development environment startup

Use the existing `/workspace/russian-companion` checkout on `cursor/content-audit-corrections-964f`. Do not create a worktree unless requested. First read root `AGENTS.md`, the engineering continuation instructions and checkpoint. Work with one agent.

Run `source /workspace/setup/android-env.sh` in each shell. If helpers/dependencies are absent, run the retained `install.sh` from this directory. It installs a checksum-verified JDK 21 and Android command-line tools 19, SDK platform 37.0, build tools 36.0.0 and platform tools; uses the supplied proxy and system Java truststore; keeps caches under `/workspace`; and avoids Python cache generation inside the checkout. No long-running app service or emulator is required for this workflow.

Confirm `java -version`, `javac -version`, and `sdkmanager --sdk_root="$ANDROID_HOME" --list_installed`. From the checkout, the verified focused PR64 workflow is:

```bash
source /workspace/setup/android-env.sh
./gradlew -I /workspace/setup/cloud-validation.gradle compileDebugSources lintDebug verifyReleaseGateAttachment testDebugUnitTest \
  --tests '*Unit27PastInstructionTest' --tests '*Unit26PostalInstructionTest' \
  --tests '*RequiredPathProgressTest' --tests '*LearnerReloadRecoveryTest' --tests '*HostActivityRecreationTest' \
  --tests '*Aud57CorrectionTest' --tests '*ContentAuditCheckpointRepairTest' \
  --tests '*ContentAuditCorrectionTest' --max-workers=4 --console=plain
```

This compiles the app and runs selected tests without assembling/signing a new APK. Confirm actual executed XML under `app/build/test-results/testDebugUnitTest`, lint reports under `app/build/reports`, and the underlying command's exit status. Distinguish cached results from new executions. Exact historical/current results are in the adjacent verification manifests. No PR64 APK identity is asserted by the compilation command.

The external Gradle init script redirects Robolectric user home and Maven cache, uses Maven Central through the supplied Java proxy, preserves TLS verification, and excludes `VisualReviewCaptureTest` (2), `Units35ChunkCaptureTest` (2), `Units610ChunkCaptureTest` (10), and `Units1115GapCaptureTest` (2). They overwrite retained screenshots and write to `/opt/cursor`. Do not remove these exclusions during setup verification or enable `WRITE_*` evidence-regeneration variables.

For a broader **non-export** test run when needed, omit the eight `--tests` filters but retain the init script and all four exclusions. Label that scope accurately; it is not the complete suite. Physical-device evidence and screenshot export are unrun here. Do not invoke release tasks or signing while prohibited. Preserve repository source, dependencies, lockfiles and historical evidence during environment setup; check `git status` before/after. Never assume daemons survive environment restoration.

Saved configuration does not prove publication. Check the supported product publication status; if no supported publication capability is available, report that blocker instead of claiming success.
