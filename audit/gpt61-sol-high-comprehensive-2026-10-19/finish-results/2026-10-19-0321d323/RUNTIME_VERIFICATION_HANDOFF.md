# Separate runtime verification — NOT_EXECUTED

This is an executable plan, not a record of Android execution. Audited commit `5c176eb22f4a93bf5f7fd20af15e79ec8c4748ed`, tree `f95bfa3c1ae6b8ac07b42b6db9fa69c5e247830f`, repo `justinkulka-lang/russian-companion`, draft PR64. Current chat has Java only: no Gradle/Kotlin/SDK/adb, Android environment script or actual Git checkout. No test/build/lint/runtime result was produced. Input-verification Python commands are separate.

Use the existing Codex engineering checkout. The prior detached-worktree proposal is superseded by GAP_COMPLETION_PLAN.md. Do not reset, discard or overwrite concurrent work. Record actual HEAD, tree and dirty diff; distinguish the audited target from later corrected-source evidence. No prompt transfer to another chat is requested.

```bash
git rev-parse HEAD
git rev-parse 'HEAD^{tree}'
git status --porcelain=v1
sha256sum app/src/main/assets/content/v2-curriculum.json
java -version
./gradlew --version
```

Expected raw SHA256 is `33e5655c99d6d5003da0ab1af9249dc814472e3436e0d28afd2f878f6ade4940`; exact byte size20,036,214. Verify wrapper distribution/hash, declared Java17 compatibility, installed compile SDK37/target36/min26, dependency availability and SDK licenses against tracked configuration. Record actual versions and offline dependency resolution. Do not silently update build files, sources or dependencies. A missing prerequisite is NOT_EXECUTED, not a test failure or pass.

Use a new external results directory/run label. Read the pinned regression runner/manifests before execution; preserve every original selector and assertion. Create a reviewed **external** Gradle init script excluding:

```groovy
allprojects {
    tasks.withType(Test).configureEach {
        filter {
            excludeTestsMatching("*VisualReviewCaptureTest*")
            excludeTestsMatching("*Units35ChunkCaptureTest*")
            excludeTestsMatching("*Units610ChunkCaptureTest*")
            excludeTestsMatching("*Units1115GapCaptureTest*")
        }
    }
}
```

These four classes contain2+2+10+2 excluded exporter methods. Independently verify class/method identities from the pinned test source and actual discovery; do not regenerate screenshots or enable evidence-writing switches. Retain the explicit physical-reboot skip. Inventory1241 annotations minus16 exporters suggested1225 at the supplied broad source; actual target discovery must establish its own count.

The historical1GiB heap run was interrupted; do not repeat its resource assumptions. Review the tracked3GiB worker/four-worker/recycle8 configuration and actual host memory. Four3GiB workers require adequate aggregate RAM and overhead; use fewer workers if necessary, preserving heap/selector coverage and recording the effective configuration. Separate fresh discovery and execution outputs, with XML freshness and event reconciliation; retain interrupted outcomes and uncompleted discovered methods.

After reviewing the pinned runner, execute safe tasks with fresh logs and explicit exit capture, for example:

```bash
./gradlew --no-daemon -I /absolute/path/audit-exclusions.gradle :app:testDebugUnitTest
./gradlew --no-daemon :app:compileDebugSources
./gradlew --no-daemon :app:lintDebug
./gradlew --no-daemon :app:verifyReleaseGateAttachment
```

No assemble, bundle, package, install, sign, publish or release task is authorized here. `verifyReleaseGateAttachment` checks dependencies without executing the attached APK output tasks. A task reported UP-TO-DATE is not fresh compilation; record that distinction. A release refusal consistent with pending authority is not release approval. Capture each task's actual exit code independently; avoid pipelines that hide failure.

Required focused scenarios, as additional fresh engineering verification:

- **CC001/010:** census stored and displayed key positions; lesson/lab/cumulative scoring; same saved task order after rotation/resume; repeated-cue guided/independent and independent/independent discrimination. A first-position strategy test does not establish a false linguistic key.
- **CC008:** for each affected synthesis path, mutate cue/key/distractor/application relations using existing admitted values while retaining original immutable source bytes. Exercise the actual Kotlin validator, recording exact mutations and rejection reason. Include unchanged positive controls; do not claim a bypass from source reading alone.
- **CR060–064:** optional present-passive work cannot earn required past-stress credit or block required advancement; changing gating changes canonical review identity and invalidates obsolete accepted evidence. Preserve historical schema interpretations.
- **CC013 / restore:** injected low byte/entry/expanded-size limits; oversized and compressed manifest/learner/recording fixtures; cancellation before/after replacement boundary; bad hashes/schema/path/duplicate entries; storage/provider errors; cleanup, rollback/reopen and appearance-failure outcomes. Assert bounded consumption, exact pre-state and no premature success. Avoid host exhaustion.
- **Current lifecycle/storage:** wrong/right retry, rapid duplicate submission, stale writes across restore, background/rotation/process death, current governed attempt/completion recomputation, genuine assigned earlier-review answers, transaction failures, clean/reset restore and every supported migration. Use actual persisted data, not forged completion rows.
- **Accessibility/performance/privacy:** emulator/authorized runtime large text200%, TalkBack/focus/insets and narrow/large windows; offline start/search/reference/progress; large collections and slow provider responsiveness; merged manifest/network boundary and recording permission denial/focus/background behavior. No runtime behavior is established by source manifest alone.

Retain raw stdout/stderr, commands/cwd/env version metadata excluding secrets, exit statuses, effective selectors/exclusions, discovery records, complete fresh XML, append-only execution events, log/event/XML hashes, source/tree/runner/init-script/input hashes and per-method outcomes. Reconcile discovered/selected/completed/passed/failed/error/skipped/uncompleted sets by identity, never by totals alone. Keep all original32 partial-run failures and16 broad failures unchanged alongside new results; separate the26/31-method followups. All 64 genuine histories and the original Unit7 restore-cause uncertainty remain preserved. Never fabricate a clean broad result from overlapping scopes.

Return a fresh engineering result and remaining gaps to the auditor; retain this audit's INCOMPLETE status until required scope is actually inspected. Physical **Galaxy S26 Ultra airplane-mode/reboot/restore acceptance remains LAST and OUTSTANDING**, after final comprehensive nonphysical review and separate authorization. No formal stage, owner acceptance, freeze or release approval follows from these commands.


Finish-review addendum: use every row of EXECUTION_EVIDENCE_REQUESTS.csv (NOT_EXECUTED) and IMPLEMENTATION_WORK_ORDER.csv. Execute CC014's preview with actual Kotlin, including assignment-only and answer-only additions/omissions/conflicts and retained review-only witnesses. CC015 requires admitted instruction before required credit; CC016 requires phrase-accurate teaching while preserving original provenance; CC017 requires topic breadth and reached-level offline search.

The 64 JSON structural checks do not verify original archive bytes or Android restore. Preserve the 16 observed completion cases with later answers and their recorded cutoff semantics. Actual current validator mutation, migration, replacement-boundary/recovery, merged-manifest, 200%/TalkBack/inset, large-data/slow-provider and API/window checks remain pending. Keep the original Unit7 restore cause UNCONFIRMED unless isolated evidence establishes it. No APK assembly/signing or physical acceptance occurs here.
