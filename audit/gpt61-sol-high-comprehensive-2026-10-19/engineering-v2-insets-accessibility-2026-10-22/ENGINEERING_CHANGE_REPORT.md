# CC018 / CC019: V2 safe areas and labelled selection controls

Status: **bounded host engineering correction verified; independent findings remain open**. Current generated release-source bindings are stale and require a separate reconciliation before broader verification or a new audit packet.

## Exact sources and outcomes

- Baseline: `e1871a59d2b0b41d71dfcf0fa11b1903fa84792f`. Seven new regression methods, one class: **0 passed / 7 failed / 0 errors / 0 skipped**, exit 1.
- Corrected source: `8e53775a` (full SHA in FINAL/CONTEXT.json). The same seven methods plus four existing OptionPresentationUiTest controls: **11 passed / 0 failed / 0 errors / 0 skipped**, two classes, exit 0.
- The new regression file and complete test tree are byte-identical across baseline and correction. All preexisting test files are unchanged from `19d9585e`. Scope is explicit and focused, not a fresh broad or complete-suite result. The seven baseline failures are preserved unchanged, not replaced by the eleven passing records.

The environment interruption occurred after baseline completion. On resumption, the worktree was clean and the attempted production patch had not landed. The saved baseline artifacts survived. No baseline rerun, weakened assertion, exporter or WRITE_* regeneration was used.

## Baseline evidence and bounded implementation

Both geometry failures reached the real V2 scaffold title after successfully verifying delivery of injected status-bar and IME insets. The title's top was 54 pixels against a 90-pixel status-bar inset. The other five failures reached the actual onboarding, lesson and practice controls: either the visible label lacked a click action or the tagged clickable radio lacked the option text. These are genuine host assertions, not a build failure or an absent fixture.

`V2Chrome.kt` now applies the union of safe-drawing and keyboard insets once around each scaffold, then consumes Scaffold's internal content padding. The existing child-bar zero insets are intentional under that outer safe area. This handles cutouts and system bars, uses the larger bottom inset for keyboard/navigation overlap, and avoids adding both bottom heights. Footer and bottom navigation remain above the keyboard.

`V2Screens.kt` and `V2PracticeFlows.kt` now place selectable/toggleable behavior on the entire labelled row. Inner radio buttons and checkboxes are decorative with null click callbacks. Compose exposes the merged text, role, state and action together. Existing automation tags move to the corresponding row. Locked/pending enabled state is retained, lesson reselection does not submit twice, and choosing an optional-practice answer still requires a separate submit action. This changes neither answer values nor presentation permutations, scoring, assignment identity or response rules.

Tests exercise both scaffold variants with status/navigation/cutout insets, keyboard opening and closing, exact bottom padding and footer placement; labelled onboarding radio/checkbox actions; lesson option selected and locked states; and optional practice selection without submission. The four existing controls also verify lesson/lab order and scoring through saved-state restoration plus current and legacy cumulative-review option meanings.

## Commands and raw evidence

BASELINE/CONTEXT.json and FINAL/CONTEXT.json record complete commands, selectors, source trees, exclusions and activation with `source /workspace/setup/android-env.sh`. Both use testDebugUnitTest `--rerun`, offline dependencies and two workers. Final additionally requests compileDebugSources, lintDebug and verifyReleaseGateAttachment with `--continue`.

Fresh XML identities and outcomes match START/RESULT event records exactly: 7 baseline and 11 corrected. Raw suite timestamps/event milliseconds are retained. Exact shell launch timestamps were not separately recorded. The baseline's original context field runLogCreatedEpoch was an observed file ctime after completion, **not** a reliable launch timestamp; that original artifact remains unchanged and the qualification is recorded in the new contexts.

RAW_BASELINE.zip and RAW_FINAL.zip preserve complete logs, exit files, XML, event streams and GC logs; RAW_FINAL.zip also includes lint XML. Their member indexes bind exact bytes and CRC verification passed. RESULT.json/RESULTS.csv preserve per-method outcomes, full failure messages and task distinctions. collect_execution_evidence.py records the collection procedure; do not rerun it over these immutable outputs. Run the separate read-only verify_saved_results.py for verification.

The corrected build exited 0. Main Kotlin compilation and main/unit lint analysis executed; unit-test Kotlin compilation and aggregate compileDebugSources were UP-TO-DATE. lintDebug and verifyReleaseGateAttachment executed successfully. Exact lint severities are in FINAL/RESULT.json. Gate attachment proves dependency wiring, not public-release approval. No APK assembly, installation or signing occurred.

## Conservation and pending source bindings

CONSERVATION.json verifies all original 66 archive SHA256 values and CRCs. Main assets (including curriculum), catalog, governance/docs, review and release trees stay unchanged. Existing histories, prior generated records, all historical versions, screenshots and original audit outputs are retained. Original48 findings and original70 request statuses remain unchanged. Old Unit7 restore cause remains UNCONFIRMED.

The three UI source changes affect the global release-source digest, despite unchanged governed curriculum and assignment-policy bytes. SOURCE_BINDING_DIAGNOSIS.json independently ports the canonical length-prefixed hashing algorithm across 1,376 release inputs. The old source reproduces recorded digest `eb5c52ed91f086f5d235f39ae9f667fc099c5ca9dcd5463ea5179a80eacae0bd`; corrected source computes `7ce2727f34fe75b051cf65d5d908a5b2253a66fc90c34aae9318c864f32230b5`. A current canonical-Kotlin comparison is not claimed by this focused scope. The candidate still records the old digest. It remains unfrozen, sourceCommit null, artifact NOT_RECORDED and publicRelease NOT_APPROVED.

**Next large task:** reconcile current generated source bindings against the exact corrected source using authentic generators, preserving every previous version and all reviewer/mapping/approval fields. Verify repeated rendering, compare the canonical current digest, then run the affected full-class checks. Do not just replace assertions or relabel historical evidence, and do not publish an audit packet claiming current bindings before this step. A fresh broad run and independent semantic recheck remain separate later work.

## Limits

Robolectric API33 geometry/semantics do not establish Android35/36 window behavior, actual keyboard usability, TalkBack speech/focus navigation, font scaling, emulator compatibility or physical rendering. No such evidence is fabricated. CC018 and CC019 remain open pending independent recheck and the applicable UI/accessibility evidence. All 48 independent findings remain unclosed; CC048 governance reconciliation is separate and no owner decision is inferred here.

Four screenshot exporters remain explicitly excluded: VisualReviewCaptureTest (2), Units35ChunkCaptureTest (2), Units610ChunkCaptureTest (10), Units1115GapCaptureTest (2), total16 methods. No tests are counted as physical-device acceptance. Earlier broad1,334 / focused56 / historical562 and691 scopes remain separate and are not replaced or summed with this11-method run.

Configured app identity stays dev.russiancompanion / 2.0.0-dev / code5. A current APK SHA256, certificate and signature are NOT_ESTABLISHED. Install/start settings are previously saved and unchanged; environment publication remains user-confirmed rather than independently observed. The private Penguin PDF was not accessed or copied. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance stays outstanding and LAST after final independent comprehensive nonphysical audit. No merge, freeze, signing, formal advancement, owner acceptance or release approval.
