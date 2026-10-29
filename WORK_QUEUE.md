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

## Owner-directed transitional test rule — 2026-10-29

- T01 is DONE by explicit Product Owner direction as a documentation-only task. Its earlier nonpassing, interrupted test run does not block closure.
- T01-n1 runs the full configured unit-test suite once to completion at the committed current head, raising Gradle heap if needed. Record the tested commit, result, scope and every failing test name in this file's Log. Do not fix failures or create an evidence folder. A completed failing run satisfies T01-n1 because this task establishes the baseline; an interrupted run does not.
- For T02 and T03 only, a completed suite may retain failures from the T01-n1 baseline, but it must add no new failing test names. Tests deleted under A2-08 or A2-13 leave the active baseline; record those removals in this Log. Do not otherwise expand the baseline.
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
| T01-n1 | CODEX | Run the full configured unit-test suite once to completion at the committed current head. Raise Gradle heap if needed. Record the tested commit, result, execution scope and every failing test name in this file's Log. Do not fix failures and do not create an evidence folder. | Suite completes and failing test names are recorded in the Log; failures are allowed for this baseline task only. | T01 | IN_PROGRESS |
| T02 | CODEX | Archive historical audit material (A2-14). First list every file in app/src, build files and CI that reads a path under audit/. At 9101af8c, 61 test files and RetainedFinalAudit.kt do. Delete tests that only check historical audit evidence or old dev schemas (A2-08, A2-13). Keep the audit/final-curriculum-audit/ path that RetainedFinalAudit.kt reads. Then push the historical material to branch archive/audit-history-2026-10-29, remove it from the working branch, and write audit/ARCHIVE_INDEX.md with the archive commit hash. | Nothing in app/src, build files or CI reads an archived path. audit/ holds only ARCHIVE_INDEX.md and audit/final-curriculum-audit/. Completed unit-test suite adds no failures beyond the active T01-n1 baseline; tests retired under A2-08 or A2-13 leave that baseline. | T01, T01-n1 | TODO |
| T03 | CODEX | Consolidate the cumulative-review assignment schemas into one. Drop dev-data compatibility code and the tests that only preserve old dev schemas (A2-08 to A2-11). Add a clean DB reset migration for dev installs. | One review schema. Completed unit-test suite adds no failures beyond the active T01-n1 baseline; tests retired under A2-08 or A2-13 leave that baseline. | T02 | TODO |
| T03-n1 | CODEX | Fix every T01-n1 baseline failure remaining after T03. Respect the A2-08/A2-13 retirement of obsolete tests; do not preserve old development schemas or regenerate historical governance evidence to make tests pass. | All remaining baseline failures are fixed and the full required unit-test suite passes. | T03 | TODO |

## Phase 1 — Clean base

| ID | Type | Task | Done when | Needs | Status |
|---|---|---|---|---|---|
| T04 | CODEX | Move audio playback and recording into v2 (A2-19). Remove the legacy v1 UI, its tests and the release-gate legacy dependencies (A2-38, CC038, CC039, CC044). | v2 plays a bundled clip and records and replays a take in tests. No legacy UI code remains. | T03-n1 | TODO |
| T05 | CODEX | Replace the per-unit Kotlin pin files with data-driven validator checks (A2-16). | No Unit*Pins.kt files remain. Validator covers the same rules. CI passes. | T04 | TODO |
| T06 | CODEX | Fix CC018 (safe insets), CC019 (TalkBack option labels), CC040 (state saving) and CC041 (main-thread work). | Robolectric/UI tests cover each fix. | T04 | TODO |
| T07 | CODEX | Move hard-coded review text into the pack (CC043). Pin the Gradle wrapper checksum and fix the CI license step (CC042). Move non-runtime provenance out of the bundled pack (A2-15). | Pack loads. CI passes. | T05 | TODO |

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
| T14-A | CODEX | Bind the clips marked Match in block1-results.json to Units 1–2. Add dictation items (A2-20, A2-23). | Units 1–2 play verified audio. | T14, T13-O | TODO |
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

- 2026-10-29 — T01 — e71bbf60f6331e9a2b4370bb671f9fa56665e3b9 — Amendment 2 and supplied AGENTS installed; old AGENTS copied to audit/retired/. File changes committed; task BLOCKED by test failures and an interrupted run, with no full-suite result.
- 2026-10-29 — T01 — e71bbf60f6331e9a2b4370bb671f9fa56665e3b9 — DONE by explicit Product Owner direction as documentation-only; previous failures are assigned to the T01-n1 baseline and the temporary T02/T03 rule.
