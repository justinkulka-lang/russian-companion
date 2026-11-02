# WORK_QUEUE — Russian Companion (year one)

Authority: MASTER_AUTHORITY_v2.0, Amendment 1, Amendment 2. Amendment 2 controls for the matters it names.
This file is the only source of what to do next (A2-41).

Status values: TODO, IN_PROGRESS, DONE, BLOCKED, WAITING_OWNER, WAITING_CLAUDE.
Task types:
- CODEX = Codex does it.
- STOP-OWNER = the Product Owner does it. Codex stops and says exactly what is needed.
- STOP-CLAUDE = Claude does it. Codex stops and says which bundle to hand over.

---

## Continue protocol

When the owner says "continue":

1. Read AGENTS.md, Amendment 2 and this file.
2. Pick the task:
   - If a task is IN_PROGRESS, resume it.
   - Otherwise take the first TODO task whose "Needs" tasks are all DONE.
   - If the only remaining tasks wait on an owner or Claude result, report that and stop.
3. If the picked task is STOP-OWNER or STOP-CLAUDE, do not do it. Set it to WAITING_OWNER or WAITING_CLAUDE. Tell the owner exactly what to do or hand over, and where the result goes.
   - If its row says "non-blocking," go back to step 2 and pick the next eligible task in the same session.
   - Otherwise stop.
4. If it is CODEX, set it IN_PROGRESS and commit that change. Do only what the task says.
5. If the task is too big for one session, split it into lettered parts (T08a, T08b) in this file. Do the first part and treat it as the task.
6. Run the full unit-test suite. A task is not DONE with failing tests unless the task says otherwise. The owner-directed transitional exceptions below apply only to T01, T01-n1, T02 and T03; T03-n1 and T04 onward require a passing suite.
7. Commit with a plain message that starts with the task ID.
   - Before every push, fetch `origin/cursor/content-audit-corrections-964f` into its remote-tracking ref and confirm that the current remote head is an ancestor of the local branch. Do not push a branch that lacks the fetched remote head.
8. Update this file:
   - set the task DONE with the commit hash and one line of what changed
   - add any new problem found as a new TODO task at the end of the current phase, with an ID like T08-n1, and do not fix it now
9. Report to the owner in 10 lines or fewer:
   - task done and what changed
   - test result
   - findings closed
   - the next task ID and type
   - any owner action needed
10. Stop.

If blocked: set BLOCKED, write the reason and what would unblock it, report it and stop. Do not skip a blocked task unless its row says "skippable."

Owner results arrive as files. Example: a listen-check export saved to audit/listen-check/. When the owner says "continue" after supplying one, check the file exists and is valid, set the waiting task DONE, and go on.

## Owner-directed execution order — 2026-11-02

The second parallel Codex session is closed; the remote branch is authoritative. The local duplicate T02-n1 commits were discarded by reset to ff05f671, without merge or push. Useful check ideas are notes in T02-n1b, not implementation in this task.

Override normal queue and blocked-task order to work on T03-n4, then T02-n1a-n1, then rerun required CI and close T02-n1a. Do one task per session, then resume normal queue order. The CI result on the exact committed configuration is the test evidence (A2-13); no behavior checks may be removed or exclusions added to obtain a pass. Before every push, fetch and verify that the local branch contains the current remote head.

## Owner-directed transitional test rule — 2026-10-29

- T01 is DONE by explicit Product Owner direction as a documentation-only task. Its earlier nonpassing, interrupted test run does not block closure.
- T01-n1 runs the full configured unit-test suite once to completion at the committed current head, raising Gradle heap if needed. Record the tested commit, result, scope and every failing test name in this file's Log. Do not fix failures or create an evidence folder. A completed failing run satisfies T01-n1 because this task establishes the baseline; an interrupted run does not.
- For T02 and T03 only, a completed suite may retain failures from the T01-n1 baseline, but it must add no new failing test names. Tests deleted under A2-08 or A2-13 leave the active baseline; record those removals in this Log. Do not otherwise expand the baseline. All other required checks remain unchanged.
- T03-n1 follows T03 and fixes every remaining baseline failure. Its required suite must pass.
- From T04 on, step 6 applies in full: the suite must pass. The temporary T02/T03 exception does not carry forward.

Never:
- generate Russian
- add network access
- score pronunciation or free writing
- reproduce Penguin text
- preserve dev learner data or old schemas (A2-08, A2-09)
- create evidence beyond A2-12
- start user-testing tasks before the final audit and verification are DONE

---

## Phase 0 — Setup

| ID | Type | Task | Done when | Needs | Status |
|---|---|---|---|---|---|
| T00 | STOP-OWNER | Ratify Amendment 2. Give Codex the ratified file. | Signed file supplied | — | DONE — Ratified attachment supplied by the Product Owner on 2026-10-29. |
| T01 | CODEX | Commit Amendment 2 to docs/governance/. Commit this file to the repo root. Before replacing AGENTS.md, copy the old AGENTS.md into audit/retired/ so T02 archives it. Then replace AGENTS.md with the supplied version. There is no root NEXT_TASK.md; skip copying/deleting it. Per-folder NEXT_TASK.md files under audit/ stay in place for T02. | Files committed. AGENTS.md points here. | T00 | DONE — e71bbf60f6331e9a2b4370bb671f9fa56665e3b9; documentation installed and old AGENTS retired. Product Owner explicitly directed closure on 2026-10-29; prior test failures are measured in T01-n1. |
| T01-n1 | CODEX | Run the full configured unit-test suite once to completion at the committed current head. Raise Gradle heap if needed. Record the tested commit, result, execution scope and every failing test name in this file's Log. Do not fix failures and do not create an evidence folder. | Suite completes and failing test names are recorded in the Log. A completed failing run satisfies this measurement task; T02/T03 follow the temporary rule above. | T01 | DONE — d4a26833dc182477afa68d5bf9b3b971b4018e7c; completed baseline: 1,548 tests, 54 failed, 1 skipped; all failing names recorded below. |
| T02 | CODEX | Archive historical audit material (A2-14). First list every file in app/src, build files and CI that reads a path under audit/. At 9101af8c, 61 test files and RetainedFinalAudit.kt do. Delete tests that only check historical audit evidence or old dev schemas (A2-08, A2-13). Keep the audit/final-curriculum-audit/ path that RetainedFinalAudit.kt reads. Then push the historical material to branch archive/audit-history-2026-10-29, remove it from the working branch, and write audit/ARCHIVE_INDEX.md with the archive commit hash. | Nothing in app/src, build files or CI reads an archived path. audit/ holds only ARCHIVE_INDEX.md and audit/final-curriculum-audit/. Completed unit-test suite adds no failures beyond the active T01-n1 baseline; tests retired under A2-08 or A2-13 leave that baseline. | T01, T01-n1 | DONE — 20d2f566bf1508aaa570d02f5e80f726094bb40f; historical audit tree removed using archive 231a2d31; current-head full-suite verification is recorded below; T02-n1 tracks retirement gaps. |
| T03 | CODEX | Consolidate the cumulative-review assignment schemas into one. Drop dev-data compatibility code and the tests that only preserve old dev schemas (A2-08 to A2-11). Add a clean DB reset migration for dev installs. | One review schema. Completed unit-test suite adds no failures beyond the active T01-n1 baseline; tests retired under A2-08 or A2-13 leave that baseline. All other required CI checks pass. | T02 | DONE — 64eb5cb2ea55da2698dc03ac5f144d68dbb024c0; one assignment schema and dev learner reset; current-head full-suite verification is recorded below; T03-n2 tracks compatibility gaps. |
| T03-n1 | CODEX | Fix every T01-n1 baseline failure remaining after T03. Respect the A2-08/A2-13 retirement of obsolete tests; do not preserve old development schemas or regenerate historical governance evidence to make tests pass. | All remaining baseline failures are fixed and the full required unit-test suite passes. | T03 | DONE — a8ed2b72c12e5fd2fb5b4c0b2107cffd780cf0c2; remaining baseline failures fixed on the current schema; full unit suite passed. |
| T02-n1a | CODEX | First part of T02-n1: retire historical-only Unit7–14 binding-conservation tests and historical capture exporters/hooks. Restore the current unit-entry label, callback and no-assignment-mutation check using a fresh current fixture. Preserve useful current assertions from mixed tests. | Historical-only binding-conservation classes and learner-history/screenshot export switches are removed. Current unit-entry behavior is tested without a historical learner archive; current source/rights and review checks remain. Full unit suite passes. | T04 | BLOCKED — implementation 609228e1; required full CI fails. Resolve T03-n4 and investigate the two non-heap assertions in T02-n1a-n1, then rerun the full suite. |
| T02-n1b | CODEX | Finish T02-n1: port useful current checks in mixed historical/baseline tests to fresh current fixtures, retire historical-only baseline contracts and remove historical conservation helpers after their callers are ported. Preserve current source/rights, validator, backup-safety and review behavior checks. Notes from discarded local work for this part only: current API-built Unit5 prerequisites for entry/no-mutation checks; ten fresh Units1–3 progress/backup/restore/named-reopen states; current Unit13/14 partial/completed assignments and Unit15 downstream-owner restore/reopen; stale answer, step and resume checks using synthetic English changes to current instruction. Preserve the remote layout checks. Do not import discarded commits or apply these notes during T03-n4. | No historical-only conservation/baseline tests or historical capture/export switches remain. Useful mixed/current checks run with current fixtures, including the restored unit-entry behavior. Full unit suite passes. | T02-n1a | TODO |
| T03-n2 | CODEX | Finish T03 dev-data compatibility removal (A2-08 to A2-11): remove prior-policy/pre-activation identity fallbacks, old backup format/schema readers, and historical learner-row preservation migrations/tests. Keep each unit's current review policy and one clean reset path for development installs. | One current schema/format per record type; obsolete policies, backup formats and learner histories are rejected or reset. Current review gates, import safety and real old-install-to-current reset behavior are tested. Full unit suite and required checks pass. | T02-n1b | TODO |
| T02-n1a-n1 | CODEX | Investigate the two non-heap assertions observed in T02-n1a CI: AndroidPracticeAudioTest#completingTheModelDoesNotReleaseTheRecordingPlayer expected a recording player after model completion, and HostActivityRecreationTest#retryDropsAFailedReadFromTheReplacedStore did not enter its held-read latch. Use controlled heap/fork settings coordinated with T03-n4; preserve the behavior assertions and diagnose timing or implementation causes rather than adding retries, filters or skips. Neither component/test was modified by T02-n1a. | Both assertions pass in controlled isolated and full CI on the exact commit. If the CI-resource fix alone resolves them, record that passing evidence without making an unnecessary behavior change. No weakened assertions or added exclusions/skips. | T04 | TODO |

## Phase 1 — Clean base

| ID | Type | Task | Done when | Needs | Status |
|---|---|---|---|---|---|
| T04 | CODEX | Move audio playback and recording into v2 (A2-19). Remove the legacy v1 UI, its tests and the release-gate legacy dependencies (A2-38, CC038, CC039, CC044). | v2 plays a bundled clip and records and replays a take in tests. No legacy UI code remains. | T03-n1 | DONE — bd7d5fdb63707934927a2a22820572c2c1f30bd3; v2 clip playback, recording, and replay; legacy UI and release-gate pathway checks removed. |
| T05 | CODEX | Replace the per-unit Kotlin pin files with data-driven validator checks (A2-16). | No Unit*Pins.kt files remain. Validator covers the same rules. CI passes. | T04 | TODO |
| T06 | CODEX | Fix CC018 (safe insets), CC019 (TalkBack option labels), CC040 (state saving) and CC041 (main-thread work). | Robolectric/UI tests cover each fix. | T04 | TODO |
| T07 | CODEX | Move hard-coded review text into the pack (CC043). Pin the Gradle wrapper checksum and fix the CI license step (CC042). Move non-runtime provenance out of the bundled pack (A2-15). | Pack loads. CI passes. | T05 | TODO |
| T07-n1 | CODEX | Cut full unit-test run time without removing behavior checks. Profile repeated fixture/setup and computation costs; compare with the VERIFY-CURSOR run at 712fff3e (4h 0m 51s unit-suite window). Do not implement this during VERIFY-CURSOR. | A completed passing full-suite run is measurably faster on a documented comparable runner, targeting 90 minutes or less. Behavior checks remain; no added exclusions/skips, narrowed test filters or weakened assertions. Record before/after scope and timings in this Log. | T07 | TODO |
| T04-n1 | CODEX | Fix Listen-panel microphone permission and recording state handling before T14-A connects verified audio. V2ListenPanel currently never requests RECORD_AUDIO, keeps capturing=true when recordStop returns null, and does not reconcile recording state when the panel leaves composition or the app stops. Keep real engine/clip binding in T14-A. Strengthen capture coverage through the production recorder adapter boundary; the current TakeAudio fake replaces startCapture and stopCapture with byte-writing behavior. | UI tests cover permission grant/denial, failed/empty stop followed by a successful new recording, and leaving/backgrounding the panel during capture. Capture stops safely and the UI recovers without claiming a usable take. Production-adapter recording, replacement take and replay are covered alongside the existing playback/speed/A-B checks. Full unit suite passes. | T04 | TODO |
| T03-n3 | CODEX | Correct T03-n1's historical-evidence regeneration. Exact-commit T03-n4 CI also fails Unit1ReviewPackageTest#committedPackageMatchesTheCurriculumTraversal and UnitReviewPipelineTest#committedUnitPackagesMatchTheGenerator because historical Unit1 packet renderings/source binding are stale; these assertions block full-suite closure. Retire per-unit review-packet regeneration and old per-unit approval workflow requirements that force refreshing historical governance packets to pass tests. Preserve source/rights records and useful current validator/release checks; do not generate replacement bundles before their A2-12 stage. Coordinate historical-only test retirement with T02-n1b. | Tests no longer require regenerating review/unit-* governance packets merely to pass. Current behavior and source/rights checks remain, with no inferred per-unit audit/owner approval. Full unit suite and required checks pass. | T03-n1 | TODO |
| T03-n4 | CODEX | Fix required CI test resources without removing behavior checks. Exact-head run 41249369919 reported 1,360 tests, 1,004 failures and 1 skip after heap exhaustion; local verification uses external heap/fork/recycling settings absent from the committed CI path. Keep the full current unit/layout suite and its existing physical-device skip; add no exclusions or regeneration switches. | Required CI on the exact fix commit completes the full configured unit suite, lint and release-gate attachment successfully. A local pass does not substitute for CI evidence. | T03-n1 | BLOCKED — resource fix b3603622 removes heap failures; exact-commit CI completes 1,358 tests with 2 historical review-packet assertions tracked by T03-n3. Full CI pass remains required. |
| VERIFY-CURSOR-n1 | CODEX | Resolve the named Windows verification failures in the Log. Use a real Python launcher rather than the Store alias; use short test-temp paths and diagnose Robolectric SQLite open failures. Fix native-path containment in ReleaseGate.kt, preserving traversal/symlink and mismatched commit/APK rejection. Replace Unix-only /dev/null test redirects with portable subprocess handling. Preserve the complete source/rights and behavior checks; diagnose any remaining named failure separately. | The named failures are resolved on the stated host; valid contained evidence is accepted and unsafe/mismatched evidence rejected. The complete configured unit suite and required checks pass. Host setup/portability failures remain distinguished from T02–T04 behavior regressions. | T04 | TODO |

## Phase 2 — Engines

| ID | Type | Task | Done when | Needs | Status |
|---|---|---|---|---|---|
| T08 | CODEX | Add typed-response items: schema, input UI, exact-match scoring with accepted variants and only the A2-21 normalization (A2-21, A2-22). | Tests cover correct, variant, stress-stripped, ё/е and wrong input. | T07 | TODO |
| T09 | CODEX | Add the A2-24 item-validity checks and the A2-23 per-unit minimum checks to the validator. In report mode they list violations per unit and do not fail the build. | Report lists violations per unit. | T08 | TODO |
| T10 | CODEX | Show distractor rationales on wrong answers (A2-25). Add a validator lint for governance terms and raw markup in learner text (A2-26), in report mode. | Wrong answer shows its own rationale. Lint report produced. | T09 | TODO |
| T11 | CODEX | Add the in-app spaced review queue (A2-27) and the due-review size rules (A2-28). | Tests cover missed-item return, interval growth, the 8-item / 3-objective rule and the repeat limit. | T08 | TODO |
| T12 | CODEX | Add the assignment checklist on Today, per-unit target dates, the binder task schema with an optional model answer, the unit test and the gradebook (A2-30 to A2-35). | Each feature works with English-only test fixtures (MA2-162). | T11 | TODO |
| T13 | CODEX | Add the per-clip audio verification record. Build the listen-check sheet generator (A2-17, A2-18). Generate the Block 1 sheet at audit/listen-check/block1.html. | Sheet opens offline, plays clips and exports results. | T04 | TODO |
| T13-O | STOP-OWNER | Non-blocking. Complete the Block 1 listen-check sheet. Save the export to audit/listen-check/block1-results.json. Codex keeps working on other tasks meanwhile. | Results file present | T13 | TODO |

## Phase 3 — Content by block

Each block task means:
- fix every CRITICAL and HIGH finding scoped to its units
- reach the A2-23 minimums, using licensed inventoried sources only
- add a binder task, a unit test and the A2-36 reference entries for each unit
- clear the A2-24 validity and A2-26 lint reports for those units
- bring cumulative review for those units to A2-28
- generate that block's listen-check sheet if audio candidates exist

MEDIUM and LOW findings for those units are fixed or given a written disposition in audit/FINDINGS_DISPOSITION.csv.

| ID | Type | Task | Done when | Needs | Status |
|---|---|---|---|---|---|
| T14 | CODEX | Block 1, Units 1–5. Includes CC002 foundation content (pronunciation, listening, handwriting recognition) and CC015. | Block 1 meets A2-01. Validity and lint reports are clean for Units 1–5. | T12, T10 | TODO |
| T14-A | CODEX | Bind the clips marked Match in block1-results.json to Units 1–2. Add dictation items (A2-20, A2-23). | Units 1–2 play verified audio. The Listen panel in the app receives the real audio engine and a bound clip. It is not called with null. | T14, T13-O, T04-n1 | TODO |
| T15 | CODEX | Block 2, Units 6–10. | Block 2 meets A2-01. | T14 | TODO |
| T16 | CODEX | Block 3, Units 11–15. Includes CC003. | Block 3 meets A2-01. | T15 | TODO |
| T17 | CODEX | Block 4, Units 16–20. Includes CC005 and CC007. | Block 4 meets A2-01. | T16 | TODO |
| T18 | CODEX | Block 5, Units 21–25. Includes CC004 and CC006. | Block 5 meets A2-01. | T17 | TODO |
| T19 | CODEX | Block 6, Units 26–30. Includes CC009 and CC048 (per A2-37). | Block 6 meets A2-01. | T18 | TODO |
| T19-O | STOP-OWNER | Complete any remaining listen-check sheets for Blocks 2–6. Save the exports to audit/listen-check/. | Result files present, or the owner says to proceed without audio for those blocks | T19 | TODO |
| T19-A | CODEX | Bind all clips marked Match for Blocks 2–6. Add dictation items where audio exists. | Bound clips play. Validator passes. | T19-O | TODO |

## Phase 4 — Final audit (before any user testing)

| ID | Type | Task | Done when | Needs | Status |
|---|---|---|---|---|---|
| T20 | CODEX | Pre-audit readiness. Turn the A2-23, A2-24 and A2-26 checks from report mode to failing mode. Disposition every CC and IA finding in audit/FINDINGS_DISPOSITION.csv. Build the final-audit bundle at audit/final-curriculum-audit/ (A2-12). | Checks in failing mode pass. Bundle committed. | T14-A, T19-A | TODO |
| T21 | STOP-CLAUDE | Final curriculum audit of Units 1–30. Owner gives Claude the repo and commit. Claude's report goes to audit/final-curriculum-audit/REPORT.md. | Report committed | T20 | TODO |
| T22 | CODEX | Fix every finding in REPORT.md. List the changed records in audit/final-curriculum-audit/DELTA_SCOPE.md. | All findings fixed or dispositioned. CI passes. | T21 | TODO |
| T23 | STOP-CLAUDE | Delta audit of the changed records (A2-03). If Claude returns findings, Codex adds T22-n tasks and this step repeats. | Delta report says PASSED | T22 | TODO |
| T24 | CODEX | Independent verification (A2-04). Build the test APK and record its SHA-256. Write USER_TEST_SCRIPT.md covering MA2-131 and MA2-165 to MA2-171 in plain steps with a pass/fail line each. Its first step is adding a Russian keyboard on the phone, for typed answers. | Verification report, APK and script committed | T23 | TODO |

## Phase 5 — User testing (after the final audit only)

| ID | Type | Task | Done when | Needs | Status |
|---|---|---|---|---|---|
| T25 | STOP-OWNER | Install the test APK on the Galaxy S26 Ultra. Run USER_TEST_SCRIPT.md. Record results in audit/user-test/RESULTS.md with screenshots. | Results file present | T24 | TODO |
| T26 | CODEX | Fix the user-test failures. List the changed records. | CI passes | T25 | TODO |
| T27 | STOP-CLAUDE | Delta audit of the user-test fixes. | Delta report says PASSED | T26 | TODO |
| T28 | CODEX | Re-verify. Build a new APK. List only the workflows the owner must re-test. | Verification and APK committed | T27 | TODO |
| T29 | STOP-OWNER | Re-test the listed workflows. Record the results. | All pass | T28 | TODO |

## Phase 6 — Acceptance

| ID | Type | Task | Done when | Needs | Status |
|---|---|---|---|---|---|
| T30 | CODEX | Prepare the OWNER_ACCEPTED record and the release-signed personal study APK with its SHA-256 (A2-07). | Record and APK ready for signature | T29 | TODO |
| T31 | STOP-OWNER | Sign OWNER_ACCEPTED. | Signed | T30 | TODO |
| T32 | CODEX | Freeze. Tag the commit and record the frozen APK identity. From here, MA2-143 and MA2-144 apply (A2-10). | Tag pushed | T31 | TODO |

## Log

(Codex appends one line per finished task: date, task ID, commit, one-line note.)

2026-10-30 - Owner direction: Cursor is temporary implementer until Codex usage returns. Codex verifies Cursor's commits on return.

2026-10-31 - Owner direction: Codex resumes as primary implementer. Cursor is no longer active.

- 2026-10-29 — T01 — e71bbf60f6331e9a2b4370bb671f9fa56665e3b9 — Amendment 2 and supplied AGENTS installed; old AGENTS copied to audit/retired/. File changes committed; task BLOCKED by test failures and an interrupted run, with no full-suite result.
- 2026-10-29 — T01 — e71bbf60f6331e9a2b4370bb671f9fa56665e3b9 — DONE by explicit Product Owner direction as documentation-only; previous failures are assigned to the T01-n1 baseline and the temporary T02/T03 rule.

- 2026-10-30 — T01-n1 — d4a26833dc182477afa68d5bf9b3b971b4018e7c — DONE: completed baseline at a622248a2561696fb263a4256a4a6333fa5b9098 (1,493 passed, 54 failed, 1 skipped); failure names logged, no fixes or evidence folder; temporary T02/T03 rule and T03-n1 repair task recorded.
- 2026-10-30 — T02 — 20d2f566bf1508aaa570d02f5e80f726094bb40f — DONE: historical audit tree removed using existing archive 231a2d31188b48afa256a759dec4389eaf5b44c3; full suite owed.
- 2026-10-30 — T03 — 64eb5cb2ea55da2698dc03ac5f144d68dbb024c0 — DONE: one cumulative-review assignment schema; dev database migration discards learner rows; full suite owed.
- 2026-10-31 — T03-n1 — a8ed2b72c12e5fd2fb5b4c0b2107cffd780cf0c2 — DONE: remaining baseline failures fixed without restoring schemas 2–16; full unit suite 1,397 passed, 0 failed, 1 physical skip, 0 errors.
- 2026-10-31 — T04 — bd7d5fdb63707934927a2a22820572c2c1f30bd3 — DONE: v2 plays a bundled clip and records and replays a take; legacy UI, its tests, and release-gate pathway dependencies removed; full unit suite 1,359 passed, 0 failed, 1 physical skip, 0 errors.

### T02 baseline removals and filtered tests — 2026-10-30

These 24 T01-n1 failing names left the active baseline under A2-08, A2-13, and A2-14. They only checked archived audit evidence or old development schemas. The other 30 failing names remain active. `AcceptanceBoundaryTest#at020_physicalRebootPersistsGovernedRecords` remains the physical skip.

- `dev.russiancompanion.v2.BatchECapabilityResearchTest#digestsGovernanceAndLaterUnitsStayUnchanged`
- `dev.russiancompanion.v2.SavedReviewEntryLabelTest#genuineSavedLegacyTaskLabelsItsOwnReviewAtTheUnitEntry`
- `dev.russiancompanion.v2.SavedReviewEntryLabelTest#noSavedTaskUsesTheCurrentDeclarationWithoutCreatingAnAssignment`
- `dev.russiancompanion.v2.Unit10PresentReviewUiTest#genuineSchemaOneEntryContinueAndReviewKeepTheirOwnPartialMeaning`
- `dev.russiancompanion.v2.Unit11NounReviewUiTest#genuineSchemaOneEntryContinueAndReviewKeepTheirOwnPartialMeaning`
- `dev.russiancompanion.v2.Unit12GenderReviewUiTest#genuineSchemaOneEntryContinueAndReviewKeepTheirOwnPartialMeaning`
- `dev.russiancompanion.v2.Unit13PresentCompatibilityTest#genuineV2PartialCompletesWithoutRelabellingAndHistoricalV1CanRefreshPrerequisites`
- `dev.russiancompanion.v2.Unit13PresentReviewUiTest#genuineSchemaOneEntryContinueAndReviewKeepTheirOwnPartialMeaning`
- `dev.russiancompanion.v2.Unit13PresentReviewUiTest#genuineSchemaSixEntryContinueAndReviewKeepTheirOwnPartialMeaning`
- `dev.russiancompanion.v2.Unit14PluralPossessionReviewUiTest#genuineCompletedV1DetailRetainsOldTitleAndTwoOfTwoAnswers`
- `dev.russiancompanion.v2.Unit14PluralPossessionReviewUiTest#genuinePartialV1EntryAndContinueRetainOldTitleAndOneOfTwoAnswers`
- `dev.russiancompanion.v2.Unit5GrammarReviewUiTest#genuineLegacyTaskKeepsItsOwnTitleInstructionsAndQuestionMeaning`
- `dev.russiancompanion.v2.Unit6CumulativeReviewTest#ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup`
- `dev.russiancompanion.v2.Unit6PresentCompatibilityTest#originalV1SchemaOneAndThreeTasksRetainTheirOwnObligationAfterCurrentReteaching`
- `dev.russiancompanion.v2.Unit6PresentReviewUiTest#genuineV1EntryContinueAndReviewKeepTheirOwnTwoSlotMeaning`
- `dev.russiancompanion.v2.Unit6PresentReviewUiTest#genuineV2EntryContinueAndReviewKeepTheirOwnTitleAndPartialMeaning`
- `dev.russiancompanion.v2.Unit7GenderReviewUiTest#genuineSchemaOneEntryContinueAndReviewKeepTheirOwnPartialMeaning`
- `dev.russiancompanion.v2.Unit8PresentReviewUiTest#genuineSchemaOneEntryContinueAndReviewKeepTheirOwnPartialMeaning`
- `dev.russiancompanion.v2.Unit9NounBehaviorReviewUiTest#genuineSchemaFiveEntryContinueAndReviewKeepTheirOwnPartialMeaning`
- `dev.russiancompanion.v2.audit.CurriculumReadinessReconciliationTest#catalogReachabilityCountsOrphansSharesAndIntraUnitDuplicatesSeparately`
- `dev.russiancompanion.v2.audit.CurriculumReadinessReconciliationTest#committedReportsMatchTheGeneratorAndStayByteIdentical`
- `dev.russiancompanion.v2.audit.GapClosureDossierTest#dossierMatchesLiveReadinessAndDoesNotAuthorizeRelease`
- `dev.russiancompanion.v2.audit.OwnerGovernanceDecisionTest#preparationManifestBindsFactsAndDoesNotPassAFinalAudit`
- `dev.russiancompanion.v2.audit.PostPr53ReadinessReconciliationTest#reportMatrixMatchesTheLiveCurriculum`

Other historical-only tests were retired with the archive and were not baseline failures, including `Unit14PluralPossessionCompatibilityTest#existingOwner13V1AndV2BridgesStillSupportOldUnit14TasksWithoutRelabelling`.

Filtered run at `20d2f566bf1508aaa570d02f5e80f726094bb40f` (backup fixture fix included in that commit's tree before this queue note; the backup classes were re-run after the profile fix and passed inside the same filtered command): 75 classes, 333 tests, 30 failed, 1 skipped, 0 errors, Gradle exit 1, 28m 33s. Every failure is one of the 30 remaining T01-n1 names. Full suite owed before T03-n1 can be DONE.

Classes run: AcceptanceBoundaryTest, DeviceAcceptancePackageTest, ReviewedCurriculumSourceGuardTest, ReleaseSourceDigestTest, BackupArchiveBoundaryTest, BackupImportCoordinatorLimitsTest, BackupImportLimitsTest, BatchECapabilityResearchTest, CompleteRuleCorrectionTest, CurrentFoundationHistoryTest, LiteracyCorrectionCompatibilityTest, OptionPresentationTest, Unit10BindingConservationTest, Unit10FeedbackCorrectionTest, Unit10PresentCompatibilityTest, Unit10PresentReviewUiTest, Unit11AgreementRemediationTest, Unit11BindingConservationTest, Unit11CumulativeReviewTest, Unit11FeedbackCorrectionTest, Unit11NounCompatibilityTest, Unit11NounReviewUiTest, Unit12BindingConservationTest, Unit12FeedbackCorrectionTest, Unit12GenderCompatibilityTest, Unit12GenderReviewUiTest, Unit13BindingConservationTest, Unit13ContextTeachingTest, Unit13FeedbackCorrectionTest, Unit13PastTeachingTest, Unit13PresentCompatibilityTest, Unit13PresentReviewUiTest, Unit14BindingConservationTest, Unit14FeedbackCorrectionTest, Unit14ImpersonalRemediationTest, Unit14PluralPossessionCompatibilityTest, Unit14PluralPossessionReviewUiTest, Unit15FeedbackCorrectionTest, Unit15ImperativeRemediationTest, Unit21ObjectTransferTest, Unit29ActiveTimeCorrectionTest, Unit29ShortAdjectiveCorrectionTest, Unit3GenderTeachingTest, Unit4NominativeRoleTest, Unit4PresentTeachingTest, Unit5GrammarReviewTest, Unit5GrammarReviewUiTest, Unit6CumulativeReviewTest, Unit6FeedbackCorrectionTest, Unit6PresentCompatibilityTest, Unit6PresentReviewUiTest, Unit7AgreementTeachingTest, Unit7BindingConservationTest, Unit7FeedbackCorrectionTest, Unit7GenderCompatibilityTest, Unit7GenderReviewUiTest, Unit7InvariancePreparationHistoryTest, Unit8BindingConservationTest, Unit8PresentCompatibilityTest, Unit8PresentReviewUiTest, Unit9BindingConservationTest, Unit9CompositionFeedbackCorrectionTest, Unit9NounBehaviorCompatibilityTest, Unit9NounBehaviorReviewUiTest, Unit9NumberFeedbackCorrectionTest, Unit9QuantityRuleCorrectionTest, Units1115MappingGapTest, Units2125SourceIntakeTest, Units2630SourceIntakeTest, Units610SourceIntakeTest, OwnerGovernanceDecisionTest, Unit1ReviewPackageTest, UnitReviewPipelineTest, EvidenceSchemaTest, LinguisticReleaseGateTest.

### T03 schema consolidation and filtered tests — 2026-10-30

Assignment schema is 1 only. Schemas 2–16 are unsupported. Database version 12 deletes dev learner rows and leaves bundled content. The live `files[]` sha256 for `V2CumulativeReview.kt` is `83a2ad815a4024b06eea81a628f0fce5e11da40186d188e890f85e933de1339b`. Historical `subsequentEngineeringReferenceReviews` hashes were not changed. The 30 remaining T01-n1 names stay active.

These tests only preserved old dev assignment or migration rows (A2-08, A2-09). Each schema11 method below was replaced by `rawCurrentCompletionAndForgedPreReviewActivationDoNotCreditThisUnit`, which keeps the current raw-completion refusal. The two migration methods were renamed to assert the discard.

- `CumulativeReviewGateTest#migratedVersionTenKeepsRawHistoryButChangedGenderTeachingRejectsOldLessonCredit`
- `CumulativeReviewStorageTest#realVersionNineDatabaseMigratesWithoutRewritingHistoricalRowsOrInventingCredit`
- `Unit4CumulativeReviewTest` through `Unit10CumulativeReviewTest`, except there is no Unit1–3 class: `#schema11OrdinaryLegacyRowsWithFalseActivationMarkerSurviveReopenWithoutSyntheticReview` (Unit4, Unit5, Unit6, Unit7, Unit8, Unit9, Unit10)
- `Unit11CumulativeReviewTest#schema11LegacyRowsRemainStoredButChangedAgreementInstructionRequiresRepair`
- `Unit12CumulativeReviewTest` and `Unit13CumulativeReviewTest`: `#schema11ExactPrePolicyRowsSurviveWhileCurrentRawRowsAndChangedInstructionDoNot`
- `Unit14CumulativeReviewTest#schema11RowsRemainStoredButChangedImpersonalInstructionRequiresRepair`
- `Unit15CumulativeReviewTest#schema11RowsRemainStoredButChangedImperativeInstructionRequiresRepair`
- `Unit16CumulativeReviewTest` through `Unit30CumulativeReviewTest`: `#schema11ExactPrePolicyRowsSurviveWhileCurrentRawRowsAndChangedInstructionDoNot`

Filtered run of the 80 affected and baseline classes, finished 10:30 PM America/Chicago: 420 tests, 31 failed, 1 skipped, 0 errors, Gradle exit 1, 2h 12m 39s. Thirty failures are the remaining T01-n1 names. `Unit9CumulativeReviewTest#actualUnit9ContinueNominativePluralCueTeachingPracticeReturnAndAdvanceStayOnUnit9` failed once with `AppNotIdleException` after 976s in that long run. Rerun of that method alone passed in 18s at 10:31 PM America/Chicago. No new failing name remains. Full suite and the other CI checks are owed before T03-n1 can be DONE.

### T03-n1 baseline repair — 2026-10-31

The 30 remaining T01-n1 names pass. Units 1 and 2 content pins stay on the pre-explanation pack. Current assignments stay schema 1. Opening a schema 8 dev database discards its attempt rows. Review packets were regenerated from the existing generator after the source-digest change. No curriculum JSON was edited. Four screenshot exporters stayed excluded. `AcceptanceBoundaryTest#at020_physicalRebootPersistsGovernedRecords` stayed skipped.

Full unit suite at `a8ed2b72c12e5fd2fb5b4c0b2107cffd780cf0c2`, finished 5:14 AM America/Chicago: 1,398 tests, 1,397 passed, 0 failed, 1 skipped, 0 errors, Gradle exit 0, 2h 47m 8s. `lintDebug` did not run: the offline cache has no `com.android.tools.lint:lint-gradle:32.4.1` and no `androidx.concurrent:concurrent-futures:1.1.0`. Findings closed: none.

Classes run: AcceptanceBoundaryTest, CumulativeReviewGateTest, CumulativeReviewStorageTest, CurrentFoundationHistoryTest, DatabaseMigrationTest, DeviceAcceptancePackageTest, LiteracyHistoryCaptureTest, OptionPresentationTest, OwnerGovernanceDecisionTest, ReleaseSourceDigestTest, ReviewedCurriculumSourceGuardTest, Unit10BindingConservationTest, Unit10CumulativeReviewTest, Unit10PresentCompatibilityTest, Unit10PresentReviewBaselineTest, Unit10PresentReviewUiTest, Unit11BindingConservationTest, Unit11CumulativeReviewTest, Unit11NounCompatibilityTest, Unit11NounReviewBaselineTest, Unit11NounReviewUiTest, Unit12BindingConservationTest, Unit12CumulativeReviewTest, Unit12GenderCompatibilityTest, Unit12GenderReviewBaselineTest, Unit12GenderReviewUiTest, Unit13BindingConservationTest, Unit13CumulativeReviewTest, Unit13PresentCompatibilityTest, Unit13PresentReviewBaselineTest, Unit13PresentReviewUiTest, Unit14BindingConservationTest, Unit14CumulativeReviewTest, Unit14PluralPossessionCompatibilityTest, Unit14PluralPossessionReviewBaselineTest, Unit14PluralPossessionReviewUiTest, Unit15CumulativeReviewTest, Unit16CumulativeReviewTest, Unit17CumulativeReviewTest, Unit18CumulativeReviewTest, Unit19CumulativeReviewTest, Unit1ReviewPackageTest, Unit20CumulativeReviewTest, Unit21CumulativeReviewTest, Unit22CumulativeReviewTest, Unit23CumulativeReviewTest, Unit24CumulativeReviewTest, Unit25CumulativeReviewTest, Unit26CumulativeReviewTest, Unit27CumulativeReviewTest, Unit28CumulativeReviewTest, Unit29CumulativeReviewTest, Unit30CumulativeReviewTest, Unit4CumulativeReviewTest, Unit5CumulativeReviewTest, Unit5GrammarReviewTest, Unit6CumulativeReviewTest, Unit6PresentCompatibilityTest, Unit6PresentReviewTest, Unit6PresentReviewUiTest, Unit7BindingConservationTest, Unit7CumulativeReviewTest, Unit7GenderCompatibilityTest, Unit7GenderReviewBaselineTest, Unit7GenderReviewUiTest, Unit7InvariancePreparationHistoryTest, Unit8BindingConservationTest, Unit8CumulativeReviewTest, Unit8PresentCompatibilityTest, Unit8PresentReviewBaselineTest, Unit8PresentReviewUiTest, Unit9BindingConservationTest, Unit9CumulativeReviewTest, Unit9NounBehaviorReviewBaselineTest, Unit9NounBehaviorReviewUiTest, UnitReviewPipelineTest, Units1115MappingGapTest, Units2125SourceIntakeTest, Units2630SourceIntakeTest, Units610SourceIntakeTest.

### T01-n1 completed unit-test baseline — 2026-10-30

Tested commit: `a622248a2561696fb263a4256a4a6333fa5b9098`. The working tree stayed clean at this commit throughout the run. Only WORK_QUEUE.md is changed to record these results; no application, test, build or CI fixes are part of T01-n1.

Command (after sourcing `/workspace/setup/android-env.sh`):

```sh
./gradlew -I /workspace/setup/cloud-validation.gradle -I /tmp/russian-companion-t01-n1.gradle -Dorg.gradle.jvmargs='-Xmx4g -Dfile.encoding=UTF-8' -Pkotlin.compiler.execution.strategy=in-process --max-workers=1 :app:testDebugUnitTest --stacktrace
```

Scope: the full configured debug unit-test task, with no test-name inclusion filter. The existing CI exclusions in `.github/workflows/build.yml` also apply here: `VisualReviewCaptureTest`, `Units35ChunkCaptureTest`, `Units610ChunkCaptureTest` and `Units1115GapCaptureTest`. These screenshot exporters are excluded because they overwrite retained captures. No additional test exclusions were introduced. The existing `@Ignore` skips `dev.russiancompanion.acceptance.AcceptanceBoundaryTest#at020_physicalRebootPersistsGovernedRecords`, which requires a physical device.

Execution settings were supplied outside the repository: Gradle heap 4 GiB; test-worker heap 3 GiB; one worker/fork at a time; worker recycled after eight test classes; fail-fast disabled; test-task up-to-date skipping disabled. These settings did not change the committed build or test sources.

Completed result: **1,548 tests: 1,493 passed, 54 failed, 1 skipped, 0 errors**, across 271 reported test suites. Gradle completed in **5h 36m 52s** and returned exit status **1** because of failing tests. Its final summary agrees with the fresh standard JUnit XML reports under `app/build/test-results/testDebugUnitTest/`. There was no heap-exhaustion error or interrupted-run result in this baseline. No failing tests were fixed and no governance evidence folder was created.

The following 54 fully qualified test names are the initial active baseline for T02/T03. Any removal under A2-08 or A2-13 must be recorded here; all remaining failures pass to T03-n1.

- `dev.russiancompanion.acceptance.AcceptanceBoundaryTest#at006_interfaceDoesNotOriginateRussianAndUnavailableAudioStaysUnavailable`
- `dev.russiancompanion.acceptance.DeviceAcceptancePackageTest#physicalDevicePackageStaysUnexecutedAndDoesNotApproveRelease`
- `dev.russiancompanion.acceptance.ReviewedCurriculumSourceGuardTest#changedMovedAndMissingComparisonFilesRemainBlocked`
- `dev.russiancompanion.acceptance.ReviewedCurriculumSourceGuardTest#coherentSourceForgeryAndRemovedItemIdsCannotKeepUnchangedCodeAdmitted`
- `dev.russiancompanion.acceptance.ReviewedCurriculumSourceGuardTest#exactComparisonFilesKeepAllLiteralBindingsAndPendingResearchStatus`
- `dev.russiancompanion.acceptance.ReviewedCurriculumSourceGuardTest#newUiResourceAndCurriculumTextCannotBorrowAnExistingLiteral`
- `dev.russiancompanion.acceptance.ReviewedCurriculumSourceGuardTest#presentItemReferencesAddOnlyFourBindingsAndEachMissingItemStaysBlocked`
- `dev.russiancompanion.release.ReleaseSourceDigestTest#committedCandidateRecordsTheCurrentSourceDigestAndIdentity`
- `dev.russiancompanion.v2.BatchECapabilityResearchTest#digestsGovernanceAndLaterUnitsStayUnchanged`
- `dev.russiancompanion.v2.OptionPresentationTest#currentCorpusPreservesEveryOptionAndDispersesCorrectPositionsInAllThreeRoutes`
- `dev.russiancompanion.v2.SavedReviewEntryLabelTest#genuineSavedLegacyTaskLabelsItsOwnReviewAtTheUnitEntry`
- `dev.russiancompanion.v2.SavedReviewEntryLabelTest#noSavedTaskUsesTheCurrentDeclarationWithoutCreatingAnAssignment`
- `dev.russiancompanion.v2.Unit10BindingConservationTest#currentDeclarationReconstructsOnlyUnit10ThenEveryGenuineEarlierPack`
- `dev.russiancompanion.v2.Unit10PresentReviewUiTest#genuineSchemaOneEntryContinueAndReviewKeepTheirOwnPartialMeaning`
- `dev.russiancompanion.v2.Unit11BindingConservationTest#currentDeclarationReconstructsOnlyUnit11ThenEveryGenuineEarlierPack`
- `dev.russiancompanion.v2.Unit11CumulativeReviewTest#ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup`
- `dev.russiancompanion.v2.Unit11NounReviewUiTest#genuineSchemaOneEntryContinueAndReviewKeepTheirOwnPartialMeaning`
- `dev.russiancompanion.v2.Unit12BindingConservationTest#currentDeclarationReconstructsOnlyUnit12ThenEveryGenuineEarlierPack`
- `dev.russiancompanion.v2.Unit12GenderReviewUiTest#genuineSchemaOneEntryContinueAndReviewKeepTheirOwnPartialMeaning`
- `dev.russiancompanion.v2.Unit13BindingConservationTest#currentDeclarationReconstructsOnlyUnit13ThenEveryGenuineEarlierPack`
- `dev.russiancompanion.v2.Unit13PresentCompatibilityTest#genuineV2PartialCompletesWithoutRelabellingAndHistoricalV1CanRefreshPrerequisites`
- `dev.russiancompanion.v2.Unit13PresentReviewUiTest#genuineSchemaOneEntryContinueAndReviewKeepTheirOwnPartialMeaning`
- `dev.russiancompanion.v2.Unit13PresentReviewUiTest#genuineSchemaSixEntryContinueAndReviewKeepTheirOwnPartialMeaning`
- `dev.russiancompanion.v2.Unit14BindingConservationTest#currentDeclarationReconstructsOnlyUnit14ThenEveryGenuineEarlierPack`
- `dev.russiancompanion.v2.Unit14PluralPossessionCompatibilityTest#exactPriorDeclarationRefusesNewSchemaAndCompiledObservationRecordsBoundedPools`
- `dev.russiancompanion.v2.Unit14PluralPossessionReviewUiTest#genuineCompletedV1DetailRetainsOldTitleAndTwoOfTwoAnswers`
- `dev.russiancompanion.v2.Unit14PluralPossessionReviewUiTest#genuinePartialV1EntryAndContinueRetainOldTitleAndOneOfTwoAnswers`
- `dev.russiancompanion.v2.Unit5GrammarReviewUiTest#genuineLegacyTaskKeepsItsOwnTitleInstructionsAndQuestionMeaning`
- `dev.russiancompanion.v2.Unit6CumulativeReviewTest#ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup`
- `dev.russiancompanion.v2.Unit6PresentCompatibilityTest#originalV1SchemaOneAndThreeTasksRetainTheirOwnObligationAfterCurrentReteaching`
- `dev.russiancompanion.v2.Unit6PresentReviewUiTest#genuineV1EntryContinueAndReviewKeepTheirOwnTwoSlotMeaning`
- `dev.russiancompanion.v2.Unit6PresentReviewUiTest#genuineV2EntryContinueAndReviewKeepTheirOwnTitleAndPartialMeaning`
- `dev.russiancompanion.v2.Unit7BindingConservationTest#currentDeclarationReconstructsEachGenuinePrecedingWholePackAndNothingElse`
- `dev.russiancompanion.v2.Unit7GenderReviewUiTest#genuineSchemaOneEntryContinueAndReviewKeepTheirOwnPartialMeaning`
- `dev.russiancompanion.v2.Unit7InvariancePreparationHistoryTest#originalItemsExamplesSourcesPoliciesAndOtherUnitsStayExact`
- `dev.russiancompanion.v2.Unit8BindingConservationTest#currentDeclarationReconstructsOnlyUnit8ThenEachGenuineEarlierPack`
- `dev.russiancompanion.v2.Unit8PresentReviewUiTest#genuineSchemaOneEntryContinueAndReviewKeepTheirOwnPartialMeaning`
- `dev.russiancompanion.v2.Unit9BindingConservationTest#currentDeclarationReconstructsOnlyUnit9ThenEveryGenuineEarlierPack`
- `dev.russiancompanion.v2.Unit9NounBehaviorReviewUiTest#genuineSchemaFiveEntryContinueAndReviewKeepTheirOwnPartialMeaning`
- `dev.russiancompanion.v2.Units1115MappingGapTest#preservedUnitDigestsDoNotMoveWhenTheLaterGapIsRecorded`
- `dev.russiancompanion.v2.Units2125SourceIntakeTest#structuralMappingsAreDigestBoundAndStayUnsigned`
- `dev.russiancompanion.v2.Units2630SourceIntakeTest#generatedRussianPenguinAudioAndReleaseStayClosed`
- `dev.russiancompanion.v2.Units610SourceIntakeTest#earlierUnitDigestsStayPutAndLaterUnitsStayIsolated`
- `dev.russiancompanion.v2.audit.CurriculumReadinessReconciliationTest#catalogReachabilityCountsOrphansSharesAndIntraUnitDuplicatesSeparately`
- `dev.russiancompanion.v2.audit.CurriculumReadinessReconciliationTest#committedReportsMatchTheGeneratorAndStayByteIdentical`
- `dev.russiancompanion.v2.audit.GapClosureDossierTest#dossierMatchesLiveReadinessAndDoesNotAuthorizeRelease`
- `dev.russiancompanion.v2.audit.OwnerGovernanceDecisionTest#preparationManifestBindsFactsAndDoesNotPassAFinalAudit`
- `dev.russiancompanion.v2.audit.OwnerGovernanceDecisionTest#unresolvedSourceCapabilitiesStayEmptyAndVarianceUnitsStayUnprepared`
- `dev.russiancompanion.v2.audit.PostPr53ReadinessReconciliationTest#reportMatrixMatchesTheLiveCurriculum`
- `dev.russiancompanion.v2.review.Unit1ReviewPackageTest#committedPackageMatchesTheCurriculumTraversal`
- `dev.russiancompanion.v2.review.UnitReviewPipelineTest#committedUnitPackagesMatchTheGenerator`
- `dev.russiancompanion.v2.review.UnitReviewPipelineTest#curriculumSourceCommitMustContainTheGovernedCurriculum`
- `dev.russiancompanion.v2.review.UnitReviewPipelineTest#mappingPackageDigestBindsStructuralFactsAndIgnoresReviewerFields`
- `dev.russiancompanion.v2.review.UnitReviewPipelineTest#unitOneAndUnitTwoPackagesStayIsolated`


### VERIFY-CURSOR — 2026-11-01

- 2026-11-01 — VERIFY-CURSOR — tested `712fff3ea9cc2b1f6dabf03b9a21d1725886fa44` — completed verification of Cursor's T02, T03, T03-n1 and T04; 1,359 passed, 0 failed, 1 physical skip; unit-suite window 4h 0m 51s; follow-up fixes and runtime task queued.

Owner direction made the remote branch authoritative. The local branch was reset to its fetched head `712fff3ea9cc2b1f6dabf03b9a21d1725886fa44`, discarding the duplicate local T02 implementation and blocker from this branch. At that head, T02, T03, T03-n1 and T04 are all marked DONE. Their DONE statuses are retained; the specific verification gaps below have new fix tasks. No passing implementation was redone.

| Task | Verification against its Done when line and Amendment 2 | Disposition |
|---|---|---|
| T02 | The archive/index/path conditions pass: archive commit `231a2d31188b48afa256a759dec4389eaf5b44c3` has the exact pre-removal audit tree `f469a7776699b4c7d8a8a3203e37b48d661e5a40`; working audit/ contains only ARCHIVE_INDEX.md. RetainedFinalAudit.kt still reads the authorized final-audit path, which is absent pending T20. Source, build and CI have no archived-path reads; remaining provenance strings do not open files. Historical test retirement is incomplete, and one mixed current behavior check was removed with its old fixture. | Archive conditions verified (A2-14); correct retirement in T02-n1. |
| T03 | Assignment and answer schema is 1 only; other schema numbers are rejected. Database version 12 registers MIGRATION_11_12, deletes development learner rows and retains bundled content. However, runtime prior-policy/identity fallbacks, old backup readers and old row-preservation migrations/tests remain. The schema-number consolidation and reset pass; the instruction to drop dev-data compatibility is incomplete. | Keep the working schema/reset; finish compatibility removal in T03-n2 (A2-08 to A2-11). |
| T03-n1 | The full configured suite passes at this resulting head, including remaining active baseline tests. The original T01-n1 list and Cursor's retirement log remain as history. Curriculum assets were not changed by Cursor's T02–T04 commits. Passing tests do not resolve the retirement and compatibility gaps above. | Current-head full-suite requirement verified; no baseline expansion or application repair in VERIFY-CURSOR. |
| T04 | Legacy presentation/navigation UI and its tests are removed; shared helpers/theme are in v2. ReleaseGate no longer depends on legacy pathway/plan/default-gate readiness, while release attachment and other fail-closed checks pass. V2ClipPlaybackTest covers bundled model playback, speed changes without rewriting source bytes, take replay and A/B sequencing, using a player fake and a capture adapter. The production V2Screens.kt call is still V2ListenPanel(null, null), and recording permission/error/lifecycle handling is incomplete. | Session-level test and legacy-removal conditions verified (A2-19, A2-38). Real app engine/bound-clip wiring is explicitly required in T14-A; permission/state fixes are T04-n1. No device/audio-match approval is claimed. |

Specific follow-up evidence:

- T02-n1: Unit7–14 BindingConservationTest classes, Unit*ReviewBaselineTest classes and LiteracyHistoryCaptureTest still reconstruct old source packs or retain capture-export switches. These historical contracts remain despite A2-08/A2-13 retirement. T02 also deleted `SavedReviewEntryLabelTest#noSavedTaskUsesTheCurrentDeclarationWithoutCreatingAnAssignment`: its assertion concerned the current V2UnitScreen label, callback and absence of learner mutations. The remaining Unit5GrammarReviewUiTest covers V2CumulativeReviewScreen, a different screen. Restore the entry behavior with a fresh current fixture; do not restore the old learner ZIP. Do not delete current behavior checks merely because a mixed test used history.
- T03-n2: `V2CumulativeReview.kt` retains the historical registry in cumulativeReviewAssignmentPolicy, priorActive* digest handling and historicalReviewCompletionCurrent's pre-activation branch. `V2BackupFormat.kt` still admits format/schema versions 1 through 3. `DatabaseMigrations.kt` and DatabaseMigrationTest retain older learner-row preservation paths/assertions. Keep only current record meanings and a tested development reset path.
- T04-n1: V2ListenPanel sets capturing=false only when recordStop returns a non-null reference. AndroidPracticeAudio.stopCapture clears its recorder and may return null, leaving the panel stuck in its recording state. The manifest declares RECORD_AUDIO, but no production runtime permission request remains. MainActivity releases audio onStop; the panel has no state reconciliation/disposal handling. T14-A now requires non-null real engine and bound clip inputs and depends on this fix.

Completed full validation at the exact requested head (checkout stayed clean throughout):

```sh
# After sourcing /workspace/setup/android-env.sh
./gradlew -I /workspace/setup/verify-cursor.gradle -Dorg.gradle.jvmargs='-Xmx4g -Dfile.encoding=UTF-8' -Pkotlin.compiler.execution.strategy=in-process --max-workers=1 compileDebugSources lintDebug verifyReleaseGateAttachment :app:testDebugUnitTest --continue --stacktrace
```

Scope: one full configured debug unit-test run, with no test-name inclusion filter. Only the existing CI exclusions apply: VisualReviewCaptureTest, Units35ChunkCaptureTest, Units610ChunkCaptureTest and Units1115GapCaptureTest, which overwrite retained screenshot evidence. No additional tests were excluded. The existing physical-device-only `dev.russiancompanion.acceptance.AcceptanceBoundaryTest#at020_physicalRebootPersistsGovernedRecords` is the sole skip. External execution settings: 4 GiB Gradle heap, 3 GiB worker heap, one test fork at a time, worker recycled after eight classes, fail-fast disabled and test-task up-to-date skipping disabled. No committed build/test settings changed.

Completed result: **1,360 tests: 1,359 passed, 0 failed, 1 skipped, 0 errors**, across 248 fresh standard JUnit XML suites. The unit-suite window was **4h 0m 51.158s**, from the first suite timestamp `2026-11-01T05:03:49.719Z` to the last suite's timestamp plus duration `2026-11-01T09:04:40.877Z`. Gradle reported **BUILD SUCCESSFUL in 4h 7m 20s**, exit **0**; the runner measured **14,841 seconds** including startup, compilation, lint and gate checks. `compileDebugSources`, `lintDebug` and `verifyReleaseGateAttachment` passed. Lint reported 0 fatal/errors and 271 warnings. There were no failing test names or heap-exhaustion errors.

The full-suite runtime exceeds 90 minutes, so T07-n1 is added immediately after T07; it is not implemented here. This verification changes only WORK_QUEUE.md. It adds the owner handover line, the strengthened T14-A criterion, the three fix tasks, the runtime task and the mandatory fetch/ancestry check before every push. No new baseline captures, conserved-binding inventories, interrupted-run folders or governance evidence bundles were created. The result describes tested commit 712fff3e; it does not retroactively claim a completed full run on Cursor's individual T02/T03 commits.


### VERIFY-CURSOR — Supplemental Windows verification — 2026-11-01

The fetched remote already contained `4c85088a395736228c9e8bc21f3fdcdd14553328`, which completed the owner-directed verification and recorded a passing Linux run at application head `712fff3e`. That commit and its results are preserved. Codex should have refreshed the remote before starting this additional Windows execution. This supplement adds uncovered findings, strengthens the existing recording follow-up, and records the completed Windows result. No passing implementation was redone and T05 was not started.

- 2026-11-01 — VERIFY-CURSOR — tested head `712fff3ea9cc2b1f6dabf03b9a21d1725886fa44` — **1,360 tests: 1,313 passed, 46 failed, 0 errors, 1 skipped; 248 fresh JUnit suites**. Complete configured debug unit suite with no test-name inclusion filter; total invocation wall time **7h 1m 33s**, Gradle exit **1**. Started `2026-11-01T11:23:50.577311-06:00`, finished `2026-11-01T18:25:23.342463-06:00` (America/Chicago). Fresh standard XML is from `app/build/test-results/testDebugUnitTest/`. These Windows results are distinct from the previously committed Linux result of 1,359 passed, 0 failed and 1 physical skip, with a 4h 0m 51s suite window.
- Windows unit-suite window: **6h 54m 56.850s**, from first-suite timestamp `2026-11-01T17:24:08.273Z` to final-suite timestamp plus duration `2026-11-02T00:19:05.123Z`. Gradle reported BUILD FAILED in **7h 1m 18s** because of the 46 test failures. Lint passed with **0 fatal/errors and 271 warnings**; `verifyReleaseGateAttachment` passed. DatabaseMigrationTest passed 8/8 and V2ClipPlaybackTest passed 1/1. The sole skip is the existing physical-device reboot test. These Linux/Windows timings are not a controlled performance comparison.
- Scope/settings: JDK 21 and the committed wrapper; `:app:testDebugUnitTest :app:lintDebug :app:verifyReleaseGateAttachment --continue --console=plain --stacktrace`; external init script with the same four exporter exclusions, a 3 GiB test heap, one fork, recycling after eight classes, fail-fast disabled and test up-to-date skipping disabled. Gradle used 3 GiB, one worker and in-process Kotlin compilation. No WRITE_* switch was enabled, no source/test/build configuration was changed, and no governance evidence folder was created.
- Interruption disclosure: the Oct 31 Windows attempt stopped before completion near 292 minutes; no final XML or live workers remained on Nov 1. It is not counted as a completed suite or retained as governance evidence. An earlier setup-only preflight stopped before any tests to restore committed LF bytes. The completed Windows execution above ran in a background process at the same unchanged application head.
- Runtime: this completed invocation also exceeded 90 minutes. The existing T07-n1 remains immediately after T07, with its passing Linux baseline intact. No runtime optimization was implemented during verification.
- The exact owner-direction Log line and non-null real-engine/bound-clip criterion in T14-A were already present in the fetched commit and are retained without duplication. Existing T02-n1, T03-n2 and T04-n1 findings are preserved.

Verification disposition:

| Cursor task | Done when / Amendment 2 verification | Disposition |
|---|---|---|
| T02 / `20d2f566` | The archive at `231a2d31188b48afa256a759dec4389eaf5b44c3` matches the pre-removal audit tree. Current path reads do not open archived material; final-curriculum-audit remains authorized and absent pending T20. The existing verification also identifies mixed/current behavior lost in historical test retirement. | Keep passing archive/path work and existing T02-n1; no historical material was restored. Current-head runs do not certify the intermediate T02/T03 baseline delta or attribute later failures to T02. |
| T03 / `64eb5cb2` | Assignment/answer schema 1, rejection of schemas 2–16 and database-12 learner reset remain. Historical/pre-activation completion and prior-policy/digest compatibility remain in V2CumulativeReview.kt, alongside the backup/migration compatibility already recorded. | Keep working consolidation/reset and existing T03-n2; remaining dev-data compatibility is not accepted under A2-08 to A2-11. |
| T03-n1 / `a8ed2b72` | The existing Linux full-suite pass is retained. The commit also regenerated review/unit-1 through review/unit-30 manifests/instructions/packets and retained `committedUnitPackagesMatchTheGenerator` requirements, despite its explicit prohibition on refreshing historical governance evidence merely to pass. | Preserve valid current-behavior repairs; add T03-n3 for the A2-02/A2-12 to A2-14 evidence-budget gap. Passing tests do not resolve that gap. |
| T04 / `bd7d5fdb` | Legacy navigation/theme UI and release-gate legacy-pathway dependencies are removed; shared code is in v2. Model speed/replay/A-B orchestration exists. V2ClipPlaybackTest uses the real playback/cache adapter with a player fake, but TakeAudio replaces production startCapture/stopCapture with a byte-writing fake. The panel lacks permission/disposal state reconciliation; MainActivity does release audio onStop. A null recordStop leaves capturing=true. | Keep passing legacy removal/playback; strengthen existing T04-n1 with production recording-adapter coverage. Real engine plus verified bound clip remains in the already-strengthened T14-A; no unverified clip was bound and no device/audio-match approval is claimed. |

CI is not green: [exact-head run 41249369919](https://github.com/justinkulka-lang/russian-companion/actions/runs/41249369919) at `712fff3e` reported 1,360 tests, 1,004 failures and 1 physical skip, beginning with OutOfMemoryError in CompleteRuleCorrectionTest and cascading through later classes; Gradle failed in 47m 35s. This is not evidence of 1,004 independent product defects. Run 41249372401 at that head also failed. On refresh, both runs at the documentation-only `4c85088a` head also concluded failure: [41291748751](https://github.com/justinkulka-lang/russian-companion/actions/runs/41291748751) and [41291744708](https://github.com/justinkulka-lang/russian-companion/actions/runs/41291744708). The required-CI acceptance condition remains unfulfilled; T03-n4 tracks committed resource configuration. The local Linux pass is preserved without substituting it for required CI evidence.

Windows failure diagnosis (VERIFY-CURSOR-n1):

- **6 failures** in CatalogComplianceTest, Aud44CorrectionTest, BatchAUnits71620Test, SourceTextPoolTest and two Units2630SourceIntakeTest cases resolve their Python launcher to the Windows Store alias, exiting 9009.
- **11 failures** concern release evidence. ReleaseGate.kt appends a literal `/` to a canonical native root and compares it with the native candidate path, rejecting valid Windows evidence. Git blame places that comparison at `08fecd4bb`, before T02–T04. Keep all containment and mismatch rejection checks when fixing it.
- **26 failures** in review/backup cases report SQLITE_CANTOPEN or missing files in long Robolectric temp paths; three recorded paths are 265, 266 and 268 characters. Verify with short temp paths to confirm the Windows length contribution; do not assume every remaining error has that cause.
- **3 failures** in Units1620SourceIntakeTest, Units2125SourceIntakeTest and Units2630StructuralMappingTest occur before content assertions because ProcessBuilder redirects stderr to Unix-only `/dev/null`. A separate `git ls-files -- '*.pdf' '*.PDF'` read returned no tracked PDFs. Preserve every source/rights assertion; these failures do not establish a prohibited-content finding or constitute source/rights approval.

These findings are separated from demonstrated T02–T04 behavior regressions. No final audit, INDEPENDENT_AUDIT_VERIFIED stage, linguistic approval or owner acceptance is claimed.

All failing Windows test names:

- `dev.russiancompanion.catalog.CatalogComplianceTest#regenerationPreservesTheActivatedCompliancePackage`
- `dev.russiancompanion.release.ReleaseGateTest#notApplicableShareAlikeDoesNotBlock`
- `dev.russiancompanion.release.ReleaseGateTest#packagedCompletePathwayClearsPathwayIncompleteWhenShareAlikeAndEvidenceAreSupplied`
- `dev.russiancompanion.release.ReleaseGateTest#aTutorOnlyGateAndAPartialPlanDoNotDependOnTheLegacyPathway`
- `dev.russiancompanion.release.ReleaseGateTest#confirmedShareAlikeClearsOnlyThatBlocker`
- `dev.russiancompanion.release.ReleaseGateTest#ownerDecisionCannotClearReleaseWithoutEveryMandatoryProcedure`
- `dev.russiancompanion.release.ReleaseGateTest#beginnerArcLengthDoesNotBlockRelease`
- `dev.russiancompanion.release.ReleaseGateTest#unconfirmedShareAlikeBlocksAnOtherwisePreparedCatalog`
- `dev.russiancompanion.release.ReleaseGateTest#aPartialOrMissingLessonDoesNotDependOnTheLegacyPathway`
- `dev.russiancompanion.release.ReleaseGateTest#contradictoryShareAlikeTreatmentFailsClosed`
- `dev.russiancompanion.release.ReleaseSourceDigestTest#mismatchedApkHashRejectsRetainedEvidence`
- `dev.russiancompanion.release.ReleaseSourceDigestTest#mismatchedCandidateCommitRejectsRetainedEvidence`
- `dev.russiancompanion.v2.Aud44CorrectionTest#unit17RecordEditsLeaveLaterUnitIdentitiesAndDigestsUnchanged`
- `dev.russiancompanion.v2.BatchAUnits71620Test#wikipediaLicenseRouteIsNotTheWikibooksSnapshot`
- `dev.russiancompanion.v2.SourceTextPoolTest#pythonAndKotlinExpansionRestoreTheSameRecordsAndTypedPack`
- `dev.russiancompanion.v2.Unit14CumulativeReviewTest#ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup`
- `dev.russiancompanion.v2.Unit14CumulativeReviewTest#actualUnit14ContinueReportedTimeCueTeachingPracticeReturnAndAdvanceStayOnUnit14`
- `dev.russiancompanion.v2.Unit14ImpersonalRemediationTest#wrongIndependentAnswerShowsCompleteRepairAndGuidedSuccessStillNeedsANewCheckAnswer`
- `dev.russiancompanion.v2.Unit14PluralPossessionCompatibilityTest#assignedMissSurvivesOrdinaryCorrectionIdempotenceRollbackBackupReopenAndLateAnswer`
- `dev.russiancompanion.v2.Unit14PluralPossessionReviewBaselineTest#actualCurrentV1AndDownstreamUnit15RoundtripReopenAndCompleteTheirOwnObligations`
- `dev.russiancompanion.v2.Unit14PluralPossessionReviewBaselineTest#earlierPluralPossessionAndOptionalAnswersCannotReplaceFreshAssignedDueAnswers`
- `dev.russiancompanion.v2.Unit15CumulativeReviewTest#ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup`
- `dev.russiancompanion.v2.Unit15ImperativeRemediationTest#wrongIndependentAnswerShowsCompleteRepairAndGuidedSuccessStillNeedsANewCheckAnswer`
- `dev.russiancompanion.v2.Unit16CumulativeReviewTest#ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup`
- `dev.russiancompanion.v2.Unit17CumulativeReviewTest#ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup`
- `dev.russiancompanion.v2.Unit18CumulativeReviewTest#ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup`
- `dev.russiancompanion.v2.Unit19CumulativeReviewTest#ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup`
- `dev.russiancompanion.v2.Unit20CumulativeReviewTest#ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup`
- `dev.russiancompanion.v2.Unit21CumulativeReviewTest#ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup`
- `dev.russiancompanion.v2.Unit22CumulativeReviewTest#ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup`
- `dev.russiancompanion.v2.Unit23CumulativeReviewTest#ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup`
- `dev.russiancompanion.v2.Unit24CumulativeReviewTest#ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup`
- `dev.russiancompanion.v2.Unit25CumulativeReviewTest#ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup`
- `dev.russiancompanion.v2.Unit26CumulativeReviewTest#ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup`
- `dev.russiancompanion.v2.Unit27CumulativeReviewTest#ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup`
- `dev.russiancompanion.v2.Unit28CumulativeReviewTest#ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup`
- `dev.russiancompanion.v2.Unit29CumulativeReviewTest#actualUnit29ContinueRelativeFutureTimingCueTeachingPracticeReturnAndAdvanceStayOnUnit29`
- `dev.russiancompanion.v2.Unit29CumulativeReviewTest#ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup`
- `dev.russiancompanion.v2.Unit30CumulativeReviewTest#ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup`
- `dev.russiancompanion.v2.Unit30CumulativeReviewTest#actualUnit30ContinueActiveParticipleTimeCueTeachingPracticeReturnAndTerminalCompletionStayOnUnit30`
- `dev.russiancompanion.v2.Unit7InvariancePreparationHistoryTest#oldAcceptedWorkAndResumeStayStoredThroughBackupReopenButNeedCurrentWork`
- `dev.russiancompanion.v2.Units1620SourceIntakeTest#noPenguinContentOrPrivatePdfEnteredGit`
- `dev.russiancompanion.v2.Units2125SourceIntakeTest#noPenguinContentInternetPermissionOrOpenReleaseGate`
- `dev.russiancompanion.v2.Units2630SourceIntakeTest#wikipediaLicenseRouteIsNotTheWikibooksSnapshot`
- `dev.russiancompanion.v2.Units2630SourceIntakeTest#duplicateSpanMarkersFailClosed`
- `dev.russiancompanion.v2.Units2630StructuralMappingTest#russianWithoutProvenanceIsRejectedAndPenguinStaysOut`

### T02-n1 split — 2026-11-01

T02-n1 is split into T02-n1a and T02-n1b under Continue protocol step 5. Historical conservation helpers still feed current review/source tests across many units; deleting them before porting those callers would lose useful behavior checks. T02-n1a handles historical-only binding proofs, capture exporters/hooks and the fresh unit-entry check. T02-n1b handles the remaining mixed fixtures and helper retirement. No runtime policy or curriculum change is included.


### T02-n1a implementation — 2026-11-01

Retired Unit7–14 BindingConservationTest historical whole-pack/hash proofs and LiteracyHistoryCaptureTest's pre-correction archive production. Current source/rights, source-file hash and pending-acceptance checks remain in ReviewedCurriculumSourceGuard and its tests. Converted the three screenshot exporters into current layout tests: normal/large font, semantic visibility, content bounds and rendered pixel-gutter assertions remain; PNG writes and retained-artifact destinations are removed. CI no longer excludes the retired exporter names and will run the converted layout tests.

Removed opt-in learner-history writers from the mixed Unit6–14 review tests and RecordedLiteracyEpochScope without removing their surrounding current review, backup read/replace, named-reopen or advancement assertions. Unit14's helper now returns its archive bytes in memory instead of also exporting identity/ZIP files. Remaining historical fixture/policy reconstruction is T02-n1b, not accepted as complete here; per-unit governance-packet regeneration remains T03-n3.

Restored CurrentReviewEntryLabelTest with a fresh in-memory learner using explicit RESUME_FROM_BOOK placement and the actual current pack. The Unit5 entry uses its current review label, invokes its callback once and creates no review assignment or other learner mutation. No historical learner ZIP, new Russian, runtime code, curriculum asset or source-inventory change was used.

Local validation: all test sources compiled; the 15 tests in CurrentReviewEntryLabelTest, Units35LayoutTest, Units610LayoutTest and Units1115ReviewStatusLayoutTest passed, with 0 failures/errors/skips, in a 78-second targeted invocation. This is targeted development validation, not the required full-suite result. External local settings used JDK 21, 3 GiB Gradle/test heaps, one fork/worker and recycling per class. No exporter exclusion or generation switch was used. Full configured CI validation is pending; T02-n1a remains IN_PROGRESS.


T02-n1a follow-up: the repository-wide scope scan found README and DeviceAcceptancePackageTest still requiring the retired exporter exclusions. README now describes the full suite and in-memory layout checks; the current CI-wiring assertion requires an unfiltered host suite instead of obsolete exporter names. That wiring test passed 1/1 (0 failures/errors/skips) in a 19-second targeted invocation. Its useful current release-blocking assertions remain. The current, unfrozen, unrecorded candidate's top-level sourceDigest was refreshed for the changed CI workflow; all preserved prior/failed/frozen candidate identities and every approval/artifact status remain unchanged. No historical review packet or evidence bundle was regenerated. The earlier fd37a322 CI run is superseded by this source revision; full CI is required on the follow-up commit before closure.


### T02-n1a validation blocker — 2026-11-01

Implementation is published at `609228e16756ec6f368847a8b7a186ce5b3c8cc0`; T02-n1a is **BLOCKED**, not DONE. The required full configured suite was run by CI at that exact implementation commit with no test-name inclusion filter or exporter exclusions. No runtime code or curriculum asset changed.

- [Push CI 41371651042](https://github.com/justinkulka-lang/russian-companion/actions/runs/41371651042): **1,358 tests, 346 passed, 1,011 failed, 0 errors, 1 physical-device skip**, across 243 standard JUnit XML suites. Gradle failed in **35m 25s**; the job ran from `2026-11-02T01:16:54Z` to `2026-11-02T01:53:05Z` (36m 11s). Compile, lint and release-gate attachment completed; lint reported **0 fatal/errors and 271 warnings**.
- The CI XML reports **1,009 OutOfMemoryError failures**, beginning in CompleteRuleCorrectionTest, plus **2 assertion failures**. These are not 1,011 demonstrated independent application defects. T03-n4 already tracks the missing committed heap/fork/recycling configuration; it remains an acceptance blocker.
- `AndroidPracticeAudioTest#completingTheModelDoesNotReleaseTheRecordingPlayer` failed at line 73: expected 2 players after model completion, found 1. This assertion failed only in the push run.
- `HostActivityRecreationTest#retryDropsAFailedReadFromTheReplacedStore` failed at its initial held-refresh latch: expected count 0, found 1. It failed in both runs, after heap exhaustion began. Its cause is not assumed to be an independent regression or solely resource pressure.
- [PR CI 41371655202](https://github.com/justinkulka-lang/russian-companion/actions/runs/41371655202) also failed: **1,358 tests, 347 passed, 1,010 failed, 1 physical skip**; Gradle failed in **48m 57s**. Both full results and all failing names remain in CI logs/reports. The superseded fd37a322 runs do not establish acceptance for 609228e1.

T02-n1a-n1 records the two non-heap assertions for controlled investigation after/coordinated with CI resource repair. Do not weaken or retire those current behavior checks to obtain a pass. Local development validation remains 15/15 current UI/layout checks plus 1/1 corrected acceptance/CI-wiring check; it does not replace the required passing full suite.

Unblock by prioritizing T03-n4 and the residual-assertion investigation, then obtaining a passing full configured suite on the resulting exact source commit. WORK_QUEUE's blocked-task rule prevents silently skipping this task. T02-n1b and T05 were not started; no evidence bundle, baseline capture or historical packet was generated.

- 2026-11-02 — Owner direction — remote ff05f671 is authoritative; parallel session closed; local duplicate work discarded. Priority is T03-n4, T02-n1a-n1, then required CI/closure of T02-n1a, one task per session.


### T03-n4 committed resource configuration — 2026-11-02

The committed Gradle path now uses a 3 GiB Gradle heap, one Gradle worker, disabled cross-project parallel execution and in-process Kotlin compilation. Every host Test task uses a 3 GiB test-worker heap, one fork at a time, JVM recycling after eight classes and fail-fast disabled. The test task logs these effective settings once before execution. This moves the previously external resource controls into the same path invoked by GitHub CI.

No test source, behavior assertion, test-name filter, exclusion, skip, retry or generation switch is changed. The current unit/layout suite and existing physical-device skip remain. Only the current, unfrozen, unrecorded candidate's top-level sourceDigest was refreshed for the changed build inputs; every prior/failed/frozen candidate identity and all approval/artifact statuses are unchanged. No historical review packets or evidence bundles were regenerated.

Development smoke validation compiled the Kotlin build configuration and passed the current candidate-identity and CI-wiring checks: 2 tests, 0 failures/errors/skips, Gradle success in 1m 57s. Its task log confirms heap=3g, forks=1 and recycling after eight classes, without external heap/fork overrides. Local repository/proxy setup is external. This targeted local check is not the A2-13 acceptance evidence. The full unfiltered GitHub push workflow on the exact resource-fix commit is required; T03-n4 stays IN_PROGRESS until that result is known. The later assertion investigation and T02-n1a closure are not performed in this task.

### T03-n4 exact-commit CI result and blocker — 2026-11-02

Resource implementation: `b36036227c0a5d6a70e26f8150bfab892e4c0ae8`. The [push CI run 41383507336](https://github.com/justinkulka-lang/russian-companion/actions/runs/41383507336) checked out that exact commit and completed the full unfiltered configured unit suite with the committed settings: heap=3g, forks=1, recycle after eight classes. Compilation, lint and release-gate attachment passed. Standard CI JUnit reports contain **1,358 tests: 1,355 passed, 2 failed, 0 errors, 1 existing physical-reboot skip**. No OutOfMemoryError occurs in the job log. Lint reports 0 fatal/errors and 271 warnings. The unit-suite window was **2h 8m 12.871s** (03:36:59.062Z–05:45:11.933Z); Gradle reported BUILD FAILED in **2h 13m 46s**. Existing T07-n1 covers the run time above 90 minutes; no performance work is done here.

Every failing test name:
- `dev.russiancompanion.v2.review.Unit1ReviewPackageTest#committedPackageMatchesTheCurriculumTraversal`
- `dev.russiancompanion.v2.review.UnitReviewPipelineTest#committedUnitPackagesMatchTheGenerator`

Both report a repository-source/release-binding mismatch and stale deterministic Unit1 packet/manifest/reviewer-instruction renderings. This is the already-queued T03-n3 historical-evidence dependency, now documented with exact-commit CI evidence. No historical packets were regenerated, checks weakened, exclusions added, or runtime/curriculum behavior changed. T03-n4 is **BLOCKED**, not DONE: the resource fix works without heap failures in this completed run, but its full-pass acceptance criterion is unmet. Resolve the T03-n3 dependency through its authorized task and obtain passing exact-commit CI before closure.

The same reports show `AndroidPracticeAudioTest#completingTheModelDoesNotReleaseTheRecordingPlayer` and `HostActivityRecreationTest#retryDropsAFailedReadFromTheReplacedStore` passed without behavior/test changes. This is input to T02-n1a-n1, not closure of that separate task: its controlled isolated verification and full passing CI remain pending. T02-n1a remains BLOCKED. Per the owner's explicit order, the next session takes T02-n1a-n1 despite T03-n4's blocker, then the required CI/closure step; the remaining historical-packet failures also block that closure. The parallel PR run 41383513861 was still in progress when this result was recorded; no result is inferred for it.

- 2026-11-02 — T03-n4 — BLOCKED after completed exact-commit CI at b3603622: committed resource settings eliminate heap failures; two historical-packet failures remain under T03-n3. Discarded local checks are notes only in T02-n1b. Next: T02-n1a-n1, CODEX, under owner priority.
