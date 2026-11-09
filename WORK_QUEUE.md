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
5. If the task is too big for one session, split it into at most three parts (-a, -b, -c) in this file. Do the first part and treat it as the task. A part may not split again. If a part is still too big, report it as BLOCKED with what remains.
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

Latest owner override: T02-n1a-n1 is DONE on CI 41383507336 at b3603622, with no further investigation. Do T03-n3 next, including retirement of the two failing historical packet assertions and a single push CI trigger with branch concurrency cancellation. Exact-commit CI 41409788228 at f70434e6 has passed: T03-n3, T03-n4 and T02-n1a are DONE. Resume normal queue order; T02-n1b is split under step 5, T02-n1b-a is DONE, and T02-n1b-b is next (CODEX). This supersedes the earlier priority order. The CI result on the exact committed configuration is the test evidence (A2-13); no behavior checks may be removed or exclusions added to obtain a pass. Before every push, fetch and verify that the local branch contains the current remote head.

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
| T02-n1a | CODEX | First part of T02-n1: retire historical-only Unit7–14 binding-conservation tests and historical capture exporters/hooks. Restore the current unit-entry label, callback and no-assignment-mutation check using a fresh current fixture. Preserve useful current assertions from mixed tests. | Historical-only binding-conservation classes and learner-history/screenshot export switches are removed. Current unit-entry behavior is tested without a historical learner archive; current source/rights and review checks remain. Full unit suite passes. | T04 | DONE — implementation 609228e1; full exact-commit CI 41409788228 at f70434e6 passes after resource repair and authorized historical-packet test retirement. |
| T02-n1b-a | CODEX | Port the mixed Unit13/14 review baseline classes to fresh current curriculum/store fixtures. Keep partial/completed backup, restore, named-reopen, assignment-bound advancement and Unit15 downstream owner checks. Remove only their historical pack/policy dependency and baseline contrast bookkeeping. | These two classes load only the current bundled pack; current partial/completed tasks restore/reopen and enforce current gates, including Unit15 owner digests. Source/rights and layout checks remain unchanged. Full exact-commit CI passes. | T02-n1a | DONE — dd2fdecb3fd4ef8a7cbca789e29d82809652559d; current Unit13/14 persistence and Unit15 owner checks; full exact-commit CI 41464879583 passed. |
| T02-n1b-b-a | CODEX | Port mixed foundation checks to fresh current Units1–3 progress/backup/restore/named-reopen states, current prerequisite-built Unit5 entry and current stale credit/step/resume refusal. Retain current literacy and real old-install reset behavior, retiring historical-only foundation conservation contracts. Owner addition: ignore only WORK_QUEUE.md in the CI push path filter. | Foundation behavior uses current API-built fixtures and synthetic English changes, with source/rights and UI/layout checks preserved. Queue-only pushes skip CI; any push touching another path runs the unchanged full suite. Full exact-commit CI passes. | T02-n1b-a | DONE — 1df43e16b534e733a3aea944cb9cfa89b760d994; current foundation fixtures and WORK_QUEUE.md paths-ignore; full exact-commit CI 41505945681 passed. |
| T02-n1b-b-b-a | CODEX | Retire historical-only cumulative-review fixtures in Unit4–30. Keep current ordinary completion, replay refusal, continue UI, current placement, epoch rejection, backup snapshots, and current policy/validator checks. | Those tests no longer load historical packs, pre-policy rows, or conservation scopes. The current checks above remain. Full exact-commit CI passes. | T02-n1b-b-a | DONE — 8199cfe3602d61e6809cea613bb07c057fbc559a; historical Unit4–30 cumulative-review fixtures retired and current review checks kept; full exact-commit CI 41674313979 passed. |
| T02-n1b-b-b-b | CODEX | Retire remaining historical-only teaching, progress, and transfer fixtures. Preserve stale credit/step/resume refusal, backup safety, and current policy/validator behavior on fresh current data or a synthetic English change. Do not port historical-only expectations. Source/rights intake stays in T02-n1b-c. | Those behavior tests no longer depend on historical packs or conservation fixtures. Useful current checks remain. Full exact-commit CI passes. | T02-n1b-b-b-a | DONE — 459ed1e09dee397887144616c131be58ac73c642; historical teaching, progress, and transfer fixtures retired and current live-pack checks kept; full exact-commit CI 41706719411 passed. |
| T02-n1b-b-b | CODEX | Finish the remaining mixed foundation and later-unit teaching/progress/review fixture ports in T02-n1b-b. Preserve stale credit/step/resume refusal, backup safety, current policy and validator behavior while retiring historical-only baseline contracts. Split further only if needed under step 5. | Remaining mixed behavior tests use fresh current data and synthetic English changes rather than historical conservation fixtures, with useful current checks preserved. Full exact-commit CI passes and original T02-n1b-b criteria are met. | T02-n1b-b-b-a, T02-n1b-b-b-b | DONE — 459ed1e09dee397887144616c131be58ac73c642; both parts meet the original criteria on the combined tree; full exact-commit CI 41706719411 passed. |
| T02-n1b-b | CODEX | Port remaining mixed foundation/progress/review and changed-teaching checks to fresh current fixtures. Preserve current backup safety, entry/no-mutation, stale credit/step/resume refusal, current review policy and validator behavior. Retire historical-only baseline contracts after useful assertions are ported. | Remaining mixed behavior tests use fresh current data and synthetic English changes rather than historical learner/pack conservation fixtures; no useful current behavior checks lost. Full exact-commit CI passes. | T02-n1b-b-a, T02-n1b-b-b | DONE — 459ed1e09dee397887144616c131be58ac73c642; foundation, teaching, progress, and review parts meet the original criteria; full exact-commit CI 41706719411 passed. |
| T02-n1b-c-a | CODEX | Retire historical-only option-presentation, device-workflow, Unit 20 digest, and Batch B citation-donor contrasts. Keep the current corpus, current workflow, current unit-identity, and current source-pin checks in those tests. Do not remove shared conservation helpers. | Those checks no longer load a historical pack or workflow. Current checks in the same tests remain. Full exact-commit CI passes. | T02-n1b-b | DONE — f6875cf6c638ea5fb9ea955f4a887582f5a97bc7; historical option-presentation, device-workflow, Unit 20, and citation-donor contrasts retired; current checks kept; full exact-commit CI 41809735218 passed. |
| T02-n1b-c-b-a | CODEX | Retire the historical digest and pack-equality contrasts in the Units 6–10 source-intake test. Keep the current source binding, freeze-byte, isolation, and no-Penguin checks. Do not remove a shared helper that another current check still calls. | That test no longer loads a historical pack. Current source and isolation checks remain. Full exact-commit CI passes. | T02-n1b-c-a | DONE — 467bb6a7b94b1faf8d636f657d42760cfe668b80; Units 6–10 historical digest and pack-equality contrasts retired; current source and isolation checks kept; full exact-commit CI 41842627202 passed. |
| T02-n1b-c-b-b-a | CODEX | Retire the Units 11–15 historical digest and pack-equality contrasts in the Units 16–20 source-intake test. Keep the current mapping, readiness, freeze-byte, and no-Penguin checks. Do not remove a shared helper that another current check still calls. | That test no longer loads a historical pack. Current source checks remain. Full exact-commit CI passes. | T02-n1b-c-b-a | DONE — a1398be99ab434b53edd92b853ccc6678e86658e; Units 11–15 historical digest contrasts retired from the Units 16–20 intake test; current mapping and source checks kept; full exact-commit CI 41881810039 passed. |
| T02-n1b-c-b-b-b-a | CODEX | Retire the historical digest and pack-equality contrasts in the Units 26–30 source-intake test. Keep the current digest pins, freeze-byte, release, and isolation checks. Do not remove a shared helper that another current check still calls. | That test no longer loads a historical pack. Current source checks remain. Full exact-commit CI passes. | T02-n1b-c-b-b-a | DONE — 9a1c50032422cb1ed35f9b2cbf14fe4cdfdee3ed; Units 1–2 and 4–15 historical digest contrasts retired from the Units 26–30 intake test; current digest pins and source checks kept; full exact-commit CI 41908416874 passed. |
| T02-n1b-c-b-b-b-b-a | CODEX | Retire the historical digest and pack-equality contrasts in the Units 21–25 source-intake test. Keep the current digest pins, mapping, and source checks. Do not remove a shared helper that another current check still calls. | That test no longer loads a historical pack. Current source checks remain. Full exact-commit CI passes. | T02-n1b-c-b-b-b-a | DONE — 0b6c331eab9b2de21d768fcb521403f5cb6b0aa7; Units 1–2 and 4–15 historical digest contrasts retired from the Units 21–25 intake test; current digest pins and source checks kept; full exact-commit CI 41928215452 passed. |
| T02-n1b-c-b-b-b-b-b-a | CODEX | Retire the historical digest and pack-equality contrasts in the Units 11–15 mapping-gap test. Keep the current Unit 3 digest pin, mapping, and later-unit isolation checks. Do not remove a shared helper that another current check still calls. | That test no longer loads a historical pack. Current mapping checks remain. Full exact-commit CI passes. | T02-n1b-c-b-b-b-b-a | DONE — 2bd5b54235b7cb9bad81c9974591ad453ed662da; Units 1–2 and 4–10 historical digest contrasts retired from the Units 11–15 mapping-gap test; current Unit 3 pin, mapping, and isolation checks kept; full exact-commit CI 41997544123 passed. |
| T02-n1b-c-b-b-b-b-b-b-a | CODEX | Retire the historical digest and pack-equality contrasts in the rebuild mapping-safeguard test. Keep the current Unit 3 digest pin, mapping rejection, and no-Penguin checks. Do not remove a shared helper that another current check still calls. | That test no longer loads a historical pack. Current mapping checks remain. Full exact-commit CI passes. | T02-n1b-c-b-b-b-b-b-a | DONE — 7f4efa1306af7e3d45f95dc7ccf4502b1dfdbaab; Units 1–2, 4, and 5 historical digest contrasts retired from the rebuild mapping-safeguard test; current Unit 3 pin and mapping checks kept; full exact-commit CI 42044880569 passed. |
| T02-n1b-c-b-b-b-b-b-b-b-a | CODEX | Retire the historical pack-equality contrasts in the owner-governance decision test. Keep the current unresolved-capability, readiness, variance, mapping, and release checks. Do not remove a shared helper that another current check still calls. | That test no longer loads a historical pack. Current governance checks remain. Full exact-commit CI passes. | T02-n1b-c-b-b-b-b-b-b-a | DONE — 2544ac2de687c3c3edd95670af34ceb9c923c534; historical pack equality retired from the owner-governance decision test; current capability, readiness, and variance checks kept; full exact-commit CI 42105077842 passed. |
| T02-n1b-c-b-b-b-b-b-b-b-b-a | CODEX | Retire the historical digest and pack-equality contrasts in the review-pipeline mapping test. Keep the current mapping binding, Unit 3 digest pin, worksheet checks, and refusal of a relabelled packet. Do not remove a shared helper that another current check still calls. | That mapping test no longer loads a historical pack. Current review checks remain. Full exact-commit CI passes. | T02-n1b-c-b-b-b-b-b-b-b-a | DONE — 7798996685f7dcae193a5e081a1a450593e3e9ec; Units 1–2, 4, and 5 historical digest contrasts retired from the review-pipeline mapping test; current mapping and relabel refusal kept; full exact-commit CI 42129111778 passed. |
| T02-n1b-c-b-b-b-b-b-b-b-b-b-a | CODEX | Retire the Unit 14 historical prior-declaration equality and compiled observation. Keep the current policy-pool and live-pack assignment checks. Do not remove a shared helper that another current check still calls. | That test no longer loads a historical pack. Current Unit 14 checks remain. Full exact-commit CI passes. | T02-n1b-c-b-b-b-b-b-b-b-b-a | DONE — fd7d537ef42f5e7e857c4f20f580d58bcfb58dd8; Unit 14 historical pack equality and compiled observation retired; current policy pools and live-pack assignment kept; full exact-commit CI 42150248387 passed. |
| T02-n1b-c-b-b-b-b-b-b-b-b-b-b-a | CODEX | Retire the Batch A historical explanation-pack equality and the unused Unit 13 historical loader. Keep the current Batch A explanation, validation, expanded-construct, and Unit 13 live-store checks. Do not remove a shared helper that another current check still calls. | Those checks no longer load a historical pack. Current Batch A and Unit 13 checks remain. Full exact-commit CI passes. | T02-n1b-c-b-b-b-b-b-b-b-b-b-a | DONE — c9fe8b366ea9bc72f914ae54d8b241c0dd21a232; Batch A historical pack equality and the unused Unit 13 loader retired; current Batch A explanations and Unit 13 live-store checks kept; full exact-commit CI 42233093719 passed. |
| T02-n1-final | CODEX | Retire the remaining historical-only expectations, including the reviewed-curriculum source-guard comparison. Delete RecordedCurriculumConservationScope and RecordedLiteracyEpochScope and port or retire their callers. Keep current source/rights, validator, release, and layout checks. | Those checks no longer depend on a historical pack or comparison file. The conservation helpers are gone. Current source/rights checks remain. Full exact-commit CI passes. | T02-n1b-c-b-b-b-b-b-b-b-b-b-b-a | DONE — e2c9d2489a3f1e5c287e255b16e6b603cad4e634; historical source-guard comparison retired; conservation helpers deleted; Units 1–2 source commit kept locally; full exact-commit CI 42249913161 passed. |
| T02-n1 | CODEX | Finish retirement of historical-only conservation expectations and remove the conservation helpers after their callers are ported or retired. Preserve current source/rights, validator, release, and layout checks. | No historical-only conservation expectations or those helpers remain. Current checks remain. Full exact-commit CI passes. | T02-n1a, T02-n1b | DONE — e2c9d2489a3f1e5c287e255b16e6b603cad4e634; closed by T02-n1-final; full exact-commit CI 42249913161 passed. |
| T02-n1b-c | CODEX | Finish mixed source/rights/intake review-test ports; retire historical-only conservation expectations and remove historical helpers once their callers are ported. Check the original T02-n1b completion criteria across the suite. Preserve current source/rights, current validator/release and all layout checks; do not regenerate historical packets. | No historical-only conservation/baseline tests or capture/export switches remain; helpers have no remaining historical callers. Current source/rights, behavior and review checks remain, using current fixtures. Full exact-commit CI passes and original T02-n1b requirements are met. | T02-n1b-c-a, T02-n1-final | DONE — e2c9d2489a3f1e5c287e255b16e6b603cad4e634; closed by T02-n1-final; full exact-commit CI 42249913161 passed. |
| T02-n1b | CODEX | Finish T02-n1: port useful current checks in mixed historical/baseline tests to fresh current fixtures, retire historical-only baseline contracts and remove historical conservation helpers after their callers are ported. Preserve current source/rights, validator, backup-safety and review behavior checks. Notes from discarded local work for this part only: current API-built Unit5 prerequisites for entry/no-mutation checks; ten fresh Units1–3 progress/backup/restore/named-reopen states; current Unit13/14 partial/completed assignments and Unit15 downstream-owner restore/reopen; stale answer, step and resume checks using synthetic English changes to current instruction. Preserve the remote layout checks. Do not import discarded commits or apply these notes during T03-n4. | No historical-only conservation/baseline tests or historical capture/export switches remain. Useful mixed/current checks run with current fixtures, including the restored unit-entry behavior. Full unit suite passes. | T02-n1b-a, T02-n1b-b, T02-n1b-c | DONE — e2c9d2489a3f1e5c287e255b16e6b603cad4e634; closed by T02-n1-final; full exact-commit CI 42249913161 passed. |
| T03-n2 | CODEX | Finish T03 dev-data compatibility removal (A2-08 to A2-11): remove prior-policy/pre-activation identity fallbacks, old backup format/schema readers, and historical learner-row preservation migrations/tests. Keep each unit's current review policy and one clean reset path for development installs. | One current schema/format per record type; obsolete policies, backup formats and learner histories are rejected or reset. Current review gates, import safety and real old-install-to-current reset behavior are tested. Full unit suite and required checks pass. | T03-n2-a, T03-n2-b, T03-n2-c | DONE — combined parts a/b/c at 8bf4ab0bcb34a5d76918868380ef3b3b87b43883; current review identity, current-only backup versions and tested development reset meet the original criteria; full exact-commit CI 42452347097 passed. |
| T03-n2-a | CODEX | Remove prior-policy and pre-activation identity fallbacks. Accept only each unit's current review policy for declaration, assignment, prescribed identity, and completion credit. Reject prior policy ids, prior-active unit digests, and empty-link or pre-activation completion credit. Do not change backup readers or database migrations. | Current review gates and current assignment credit stay. An older policy id, prior-active digest, or pre-activation completion does not supply current credit. Full exact-commit CI passes. | T02-n1b | DONE — aa20b3c5f627938073ad5f58eaecf9087f11fce8; prior-policy and pre-activation identities no longer supply current credit; full exact-commit CI 42299490608 passed. |
| T03-n2-b | CODEX | Stop admitting backup format and schema versions other than the current version. Keep import-safety rejection and the current backup round-trip. Do not change database migrations. | Obsolete backup formats and schemas are rejected. Current backup restore and forged-link rejection remain tested. Full exact-commit CI passes. | T03-n2-a | DONE — b7f5d57d3be19972711869bbe1162fa99cb73b25; only current backup format/schema admitted, current restore and import safeguards retained; full exact-commit CI 42445292140 passed. |
| T03-n2-c | CODEX | Remove historical learner-row preservation migrations and the tests that only assert them. Keep MIGRATION_11_12 and a tested development reset. Update callers that only invoke the retired migrations, without dropping current retrieval checks. | Older learner-row preservation paths are gone. A development install still resets to the current database and that reset is tested. Full exact-commit CI passes. | T03-n2-b | DONE — 8bf4ab0bcb34a5d76918868380ef3b3b87b43883; historical preservation migrations retired, versions 1–11 reset and current reopen tested; full exact-commit CI 42452347097 passed. |
| T02-n1a-n1 | CODEX | Investigate the two non-heap assertions observed in T02-n1a CI: AndroidPracticeAudioTest#completingTheModelDoesNotReleaseTheRecordingPlayer expected a recording player after model completion, and HostActivityRecreationTest#retryDropsAFailedReadFromTheReplacedStore did not enter its held-read latch. Use controlled heap/fork settings coordinated with T03-n4; preserve the behavior assertions and diagnose timing or implementation causes rather than adding retries, filters or skips. Neither component/test was modified by T02-n1a. | Both assertions pass in controlled isolated and full CI on the exact commit. If the CI-resource fix alone resolves them, record that passing evidence without making an unnecessary behavior change. No weakened assertions or added exclusions/skips. | T04 | DONE — Owner accepts passing assertions in exact-commit CI 41383507336 at b3603622; no investigation required. |
| VERIFY-CURSOR-2 | CODEX | Verify the owner-authorized temporary Cursor work from T02-n1b-b-a through T02-n1-final, T07-n1 and T03-n2-a against each Done when and Amendment 2, using existing exact-commit CI without rerunning tests. Record findings and the Codex handover. | Current remote head reviewed; exact-commit CI identities and results checked; T07-n1 test-scope preservation checked; discrepancies queued; split rule and handover recorded. | T03-n2-a | DONE — reviewed remote 38fc7d8af80008e6eaafcb02dbc31899cab0dedb and 17 passing exact-commit CI runs; two completion gaps queued below. |
| VERIFY-CURSOR-2-n1 | CODEX | Restore useful current behavior checks removed with mixed historical tests by 459ed1e0. Restore the current Unit4 present-instruction gate scenario from Unit4PresentTeachingTest#correctTargetsCannotFinishOrdinaryRequirementsUntilNewTeachingIsRecorded, selecting current required teaching instead of comparing to an old pack. Restore Unit15AspectPreparationHistoryTest#overlappingEnglishLabelsRejectWithoutThrowingAndDoNotBecomeNativeEvidence as a live-pack negative validator check in a current test class. Keep historical-only pack comparisons retired. | With current fixtures, correct target answers cannot waive required present teaching; completing that teaching resolves ordinary requirements while due review stays independent. A current Unit15 item mutated to overlapping English aspect labels is rejected without throwing. No historical pack/learner archive, weakened assertion or new exclusion/skip. Full exact-commit CI passes. | T02-n1b-b | DONE — 6cc88cb6542893ec1d1765bcdc788a27a0eb6846; current Unit4 instruction gate and Unit15 overlapping-English-label rejection restored; full exact-commit CI 42457904737 passed. |
| VERIFY-CURSOR-2-n2 | CODEX | Finish the historical-only test retirement missed by T02-n1-final. Remove SourceTextPoolTest's optional source.pool.baseline whole-pack equality contract while retaining current Python/Kotlin expansion and corruption checks. Retire historical per-unit packet existence/status/content requirements in Units2630StructuralMappingTest, V2RebuildTest and Units2125SourceIntakeTest, and the WRITE_STRUCTURAL_MAPPING disk export hook. Keep current mapping facts, source/rights records, semantic worksheet checks, and current curriculum-origin verification. Do not regenerate or delete source/rights records merely to pass. | Current tests no longer require old review/unit-* packet artifacts or a whole-pack comparison file, and cannot export those historical mapping worksheets. Current source/rights, mapping, validator, release, corruption and layout checks remain; relevant worksheet semantics run in memory. Full exact-commit CI passes. | T02-n1-final | DONE — 968a90508f77f31b3627eaa70c18f0a5551887df; optional whole-pack baseline and historical packet requirements/export hook retired, current semantics retained; full exact-commit CI 42461548472 passed. |

## Phase 1 — Clean base

| ID | Type | Task | Done when | Needs | Status |
|---|---|---|---|---|---|
| T04 | CODEX | Move audio playback and recording into v2 (A2-19). Remove the legacy v1 UI, its tests and the release-gate legacy dependencies (A2-38, CC038, CC039, CC044). | v2 plays a bundled clip and records and replays a take in tests. No legacy UI code remains. | T03-n1 | DONE — bd7d5fdb63707934927a2a22820572c2c1f30bd3; v2 clip playback, recording, and replay; legacy UI and release-gate pathway checks removed. |
| T05-a | CODEX | Add shared data-driven content-check models/evaluation. Move the Unit4 nominative, Unit6 animacy, Unit8/9 agreement and Unit13 reported-context pins into pack data and remove their Kotlin pin files. Keep every existing constraint and current behavior check. | Those five pin files are gone; their source, objective, item and context constraints are evaluated from data with negative coverage. Full exact-commit CI passes. | T04 | DONE — f5581b60403f4455114db3bcfec8355a7e18a0e2; shared pack-driven source/objective/item evaluator replaces five Unit4–13 pin files, with mutation coverage; full exact-commit CI 42482686807 passed. |
| T05-b | CODEX | Move Unit17 question, Unit20 directionality, Unit21 object, Unit24 context and Unit28 timing pins into the shared data-driven validator. Remove those Kotlin pin files, preserving every existing rule. | Those five pin files are gone and current source/context/gating checks remain. Full exact-commit CI passes. | T05-a | DONE — caad05d29127b6200b22734fc6be21ff993c5a34; five Unit17–28 pin files removed, source/rights/context/gating rules retained in shared data-driven checks; full exact-commit CI 42494147076 passed. |
| T05-c | CODEX | Move Unit29 adjective/punctuation and Unit30 language pins into data-driven validator checks. Remove those Kotlin pin files and verify the original T05 criteria across all units. | No Unit*Pins.kt files remain; all former constraints remain enforced from data. Full exact-commit CI passes. | T05-b | DONE — 8b80beaf8bfba0f54f0467be951f656a55b2fe3a; final Unit29/30 pin files removed, all former source/item/context contracts evaluated from data; full exact-commit CI 42501717654 passed. |
| T05 | CODEX | Replace the per-unit Kotlin pin files with data-driven validator checks (A2-16), in three non-recursive parts below. | No Unit*Pins.kt files remain. Validator covers the same rules. CI passes. | T05-a, T05-b, T05-c | DONE — combined parts a/b/c at 8b80beaf8bfba0f54f0467be951f656a55b2fe3a; all 13 Unit*Pins.kt files removed, same constraints enforced by shared data-driven checks; full exact-commit CI 42501717654 passed. |
| T06 | CODEX | Fix CC018 (safe insets), CC019 (TalkBack option labels), CC040 (state saving) and CC041 (main-thread work). | Robolectric/UI tests cover each fix. | T04 | DONE — 436d3f7295f05285b33bccb22ae8f539dfdbdcad; screen state/letter selection semantics fixed, safe insets/labelled controls/worker-thread paths verified; full exact-commit CI 42519460569 passed. |
| T07-a | CODEX | Move current cumulative-review titles, introductions and panel copy into pack data and keep existing current-policy UI behavior. Retire unused prior-policy copy branches. Verify the already committed wrapper checksum and SDK-license handling for CC042. | Review UI reads pack copy with current text, copy validation and UI coverage; no retired copy variants remain in the runtime helper. Wrapper checksum and CI license configuration are verified. Full exact-commit CI passes. | T05, T06 | DONE — 127f81b369b50f82062c0775b5407aa7a967abb5; current review copy loaded and validated from pack, wrapper/SDK configuration verified; full exact-commit CI 42527432682 passed. |
| T07-b | CODEX | Move provenance that the app does not read at runtime out of the bundled pack under A2-15. Keep all needed runtime source/rights IDs, source and rights records and current behavior checks; complete original T07 criteria. | Non-runtime provenance is not bundled, required runtime bindings/source/rights remain intact, pack loads, full exact-commit CI passes. | T07-a | DONE — 92f5ea01e0bfa689898cb5e137d6ba66a8a5b0e3; unused review administration moved to non-bundled catalog metadata; runtime provenance/source/rights checks preserved; full exact-commit CI 42533483695 passed. |
| T07 | CODEX | Move hard-coded review text into the pack (CC043), verify wrapper checksum and CI license handling (CC042), and move non-runtime provenance out of the bundled pack (A2-15), in two non-recursive parts below. | Pack loads. CI passes. | T07-a, T07-b | DONE — parts a/b at 92f5ea01e0bfa689898cb5e137d6ba66a8a5b0e3; review copy is pack-driven, wrapper/SDK configuration verified, unused provenance metadata separated; full exact-commit CI 42533483695 passed. |
| T07-n1 | CODEX | Cut full unit-test run time without removing behavior checks. Profile repeated fixture/setup and computation costs; compare with the VERIFY-CURSOR run at 712fff3e (4h 0m 51s unit-suite window). Do not implement this during VERIFY-CURSOR. | A completed passing full-suite run is measurably faster on a documented comparable runner, targeting 90 minutes or less. Behavior checks remain; no added exclusions/skips, narrowed test filters or weakened assertions. Record before/after scope and timings in this Log. | — | DONE — 9f217f857062221d4ca9116f47067aa9ca388813; exact-commit CI 42284640468 passed in 1h 20m 16s. |
| T04-n1 | CODEX | Fix Listen-panel microphone permission and recording state handling before T14-A connects verified audio. V2ListenPanel currently never requests RECORD_AUDIO, keeps capturing=true when recordStop returns null, and does not reconcile recording state when the panel leaves composition or the app stops. Keep real engine/clip binding in T14-A. Strengthen capture coverage through the production recorder adapter boundary; the current TakeAudio fake replaces startCapture and stopCapture with byte-writing behavior. | UI tests cover permission grant/denial, failed/empty stop followed by a successful new recording, and leaving/backgrounding the panel during capture. Capture stops safely and the UI recovers without claiming a usable take. Production-adapter recording, replacement take and replay are covered alongside the existing playback/speed/A-B checks. Full unit suite passes. | T04 | IN_PROGRESS |
| T03-n3 | CODEX | Correct T03-n1's historical-evidence regeneration. Exact-commit T03-n4 CI also fails Unit1ReviewPackageTest#committedPackageMatchesTheCurriculumTraversal and UnitReviewPipelineTest#committedUnitPackagesMatchTheGenerator because historical Unit1 packet renderings/source binding are stale; these assertions block full-suite closure. Retire per-unit review-packet regeneration and old per-unit approval workflow requirements that force refreshing historical governance packets to pass tests. Preserve source/rights records and useful current validator/release checks; do not generate replacement bundles before their A2-12 stage. Coordinate historical-only test retirement with T02-n1b. | Tests no longer require regenerating review/unit-* governance packets merely to pass. Current behavior and source/rights checks remain, with no inferred per-unit audit/owner approval. Full unit suite and required checks pass. | T03-n1 | DONE — f70434e6923fda67f8dc96ab68284ef1ef95f471; historical packet regeneration/assertions retired and CI deduplicated; exact-commit CI 41409788228 passed. |
| T03-n4 | CODEX | Fix required CI test resources without removing behavior checks. Exact-head run 41249369919 reported 1,360 tests, 1,004 failures and 1 skip after heap exhaustion; local verification uses external heap/fork/recycling settings absent from the committed CI path. Keep the full current unit/layout suite and its existing physical-device skip; add no exclusions or regeneration switches. | Required CI on the exact fix commit completes the full configured unit suite, lint and release-gate attachment successfully. A local pass does not substitute for CI evidence. | T03-n1 | DONE — resource fix b3603622 validated by full exact-commit CI 41409788228 at f70434e6; no heap failures, lint/gate and full unit suite pass. |
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

- 2026-11-02 — Owner override — T02-n1a-n1 DONE using both passing assertions in exact-commit CI 41383507336 at b3603622. T03-n3 now IN_PROGRESS, including CI deduplication/concurrency; passing exact-commit CI authorizes closing T03-n3, T03-n4 and T02-n1a together.

### T03-n3 implementation — 2026-11-02

Removed the two historical committed-packet assertions identified by the owner: Unit1ReviewPackageTest#committedPackageMatchesTheCurriculumTraversal and UnitReviewPipelineTest#committedUnitPackagesMatchTheGenerator. Removed WRITE_UNIT1_REVIEW/WRITE_UNIT_REVIEW packet export hooks and the disk writer. The remaining curriculum-source validation reads an in-memory rendering instead of a historical committed manifest. Current traversal, source/rights, digest, omission/duplication/fabricated-approval rejection and release-gate assertions remain. No runtime code, curriculum content, source/rights record or historical packet was changed or regenerated. The fixture instructions and review/README.md retire the old per-unit regeneration/approval workflow under A2-12 to A2-14.

Engineering verification now uses only the push trigger, with a workflow/branch concurrency group and cancel-in-progress=true. It still runs the full unfiltered host suite, compilation, lint and release-gate attachment. No exclusions, retries or skips were added. The current unfrozen/unapproved candidate's top-level sourceDigest was refreshed for the workflow change; all approval states and prior candidate identities remain unchanged. Full exact-commit GitHub CI is pending; T03-n3 remains IN_PROGRESS, and T03-n4/T02-n1a remain BLOCKED until that run passes, as directed by the owner.

### T03-n3 exact-commit CI and authorized closure — 2026-11-02

The single [push CI run 41409788228](https://github.com/justinkulka-lang/russian-companion/actions/runs/41409788228) checked out **`f70434e6923fda67f8dc96ab68284ef1ef95f471`** and **PASSED**. Compilation, lint, release-gate attachment and the full unfiltered configured host suite passed with committed heap=3g, forks=1, recycle after eight classes. Standard CI XML reports **1,356 tests: 1,355 passed, 0 failures, 0 errors, 1 existing physical-reboot skip**, across 243 suites. The unit-suite window was **3h 50m 3.875s**, 08:07:04.178Z–11:57:08.053Z; Gradle reported **BUILD SUCCESSFUL in 3h 57m 15s**. Lint had 0 fatal/errors and 271 warnings; no OutOfMemoryError occurred in the job log. Runtime remains covered by T07-n1; no runtime optimization was attempted.

The two historical packet assertions explicitly retired by the owner are the only test methods removed in this task. Both current audio/lifecycle assertions named in T02-n1a-n1 passed again. Remaining source/rights, traversal, digest, validator rejection, current layout and release behavior checks pass. No packet regeneration, new evidence bundle, new exclusion, narrowed CI test filter, retry or skip was used. Historical packet/source/rights files remain unchanged, with their retired workflow identified in review/README.md. Local targeted development validation also passed 14/14 tests (0 failures/errors/skips), Gradle success in 3m 57s; it does not substitute for this CI evidence.

Per the latest explicit owner direction, this passing exact-commit run closes **T03-n3**, **T03-n4** and **T02-n1a**, with **run ID 41409788228** in each row. T02-n1a-n1 was already closed by owner acceptance of run 41383507336 at b3603622; no investigation or behavior change was needed. Normal queue order now resumes at **T02-n1b — CODEX**; that task was not started in this session.

- 2026-11-02 — T03-n3 / T03-n4 / T02-n1a — DONE on exact-commit CI 41409788228 at f70434e6: 1,355 passed, 0 failed, 1 existing skip; historical packet regeneration retired, single push CI with branch concurrency cancellation, source/rights and current behavior checks retained. Next T02-n1b, CODEX.

- 2026-11-02 — T02-n1b split under Continue protocol step 5 into a (Unit13/14 persistence and Unit15 downstream current fixtures), b (remaining mixed current behavior fixtures), c (mixed source/rights/intake checks and final helper retirement). T02-n1b-a IN_PROGRESS; only this first part is implemented in this session. Parent completion criteria remain unchanged.

### T02-n1b-a implementation — 2026-11-02

Ported Unit13PresentReviewBaselineTest and Unit14PluralPossessionReviewBaselineTest to current review-persistence tests. Both now load only the current bundled curriculum and create fresh learner records through current LocalStore/governed-review APIs. Removed the two historical pack loads/old-policy fixtures and baseline-observation bookkeeping. All four behavior methods remain (with current names and policy expectations); no behavior method, current gate assertion, source/rights or layout check was deleted.

The Unit13 partial and completed current assignments are backed up, restored to named stores and reopened with document, task, owner-digest and completion-state assertions. Unit14 does the same for partial/completed current assignments and downstream Unit15 owner-bound review; every remaining assigned slot must be answered before advancing. Existing optional/earlier-answer refusal, no-mutation on refused advancement, prerequisite, completion-link and original-store reopen checks remain. No runtime code, curriculum, source/rights record, CI configuration, current candidate digest or historical packet changed. No baseline capture, learner-history fixture, evidence bundle, retry, exclusion or skip was added. Shared historical helpers remain for their other callers until parts b/c port them.

Targeted local development validation passed all 4 tests in the two renamed classes, with 0 failures/errors/skips, Gradle success in 5m 4s. It used the committed resource settings; external local repository/proxy setup is not CI evidence. Full unfiltered exact-commit GitHub CI is required before closing T02-n1b-a. Parent T02-n1b remains unfinished; parts b/c were not started.

### T02-n1b-a exact-commit CI and closure — 2026-11-02

The single [push CI run 41464879583](https://github.com/justinkulka-lang/russian-companion/actions/runs/41464879583) checked out **`dd2fdecb3fd4ef8a7cbca789e29d82809652559d`** and **PASSED** compilation, lint, release-gate attachment and the full unfiltered configured host suite. Standard CI XML reports **1,356 tests: 1,355 passed, 0 failures, 0 errors, 1 existing physical-reboot skip**, across 243 suites. All four methods in the two renamed current persistence classes executed and passed (2/2 each, no skips). Committed resource settings were heap=3g, forks=1, recycle after eight classes; the log contains no OutOfMemoryError.

The unit-suite window was **3h 54m 23.352s**, 14:53:34.753Z–18:47:58.105Z; Gradle reported **BUILD SUCCESSFUL in 4h 2m 16s**. Lint reported 0 fatal/errors and 271 warnings. The existing T07-n1 covers runtime above 90 minutes; no optimization was attempted in this task. These are completed CI results, not a method-count prediction or a local substitute for A2-13 evidence. No reports or new governance bundle are committed; the standard reports remain attached to the CI run.

T02-n1b-a is **DONE**: the two former historical positive fixtures now exercise current Unit13/14 assignments, current gates, partial/completed backup restoration and named reopening, plus current Unit15 downstream owner identities. Their current negative gate checks remain. Source/rights records, current source/validator and layout checks remain unchanged and pass in the full suite. No runtime/curriculum changes, historical packet regeneration, new exclusions, retries or skips were used.

Only this first part was implemented. Parent **T02-n1b remains TODO**, with its original completion criteria intact; **T02-n1b-b** and **T02-n1b-c** remain TODO for remaining mixed fixture ports and final historical-helper retirement. Next: **T02-n1b-b — CODEX**. No owner action is needed.

- 2026-11-02 — T02-n1b-a — dd2fdecb3fd4ef8a7cbca789e29d82809652559d — DONE: exact-commit CI 41464879583, 1,355 passed, 0 failed, 1 existing skip; Unit13/14 current persistence ports and Unit15 owner checks passed; parent/remaining parts stay queued.

- 2026-11-02 — T02-n1b-b split under Continue step 5 into foundation part a and remaining later-unit teaching/review part b. Only T02-n1b-b-a is IN_PROGRESS this session, including the owner-directed CI push paths-ignore entry for WORK_QUEUE.md alone. Parent completion criteria remain unchanged.

### T02-n1b-b-a implementation — 2026-11-02

Added fresh current Units1–3 persistence coverage for ten states: empty learner, wrong check, correct check, guided work, completed teaching step, Unit1 completed, partial Unit2, Unit2 completed, partial Unit3 review and Unit3 completed. Records are produced through current LocalStore/governed-review APIs; each state is backed up in memory, read, restored to a named store and reopened alongside the original named store. Full document equality and current progress, selections, completion, resume and review-link semantics are checked. No historical learner archive or pack is loaded by these controls.

CurrentReviewEntryLabelTest retains the placement entry check and adds earned Unit5 prerequisites created through the current store APIs, with the current label/callback and no-assignment/no-state-mutation assertions. CurrentFoundationHistoryTest keeps current Unit14 prerequisite/owner checks and replaces its helper-only historical-epoch contract with actual current answer-credit, completed-step and resume invalidation after a synthetic English instruction change, followed by fresh work restoring those identities. CumulativeReviewGateTest keeps the real version-10 SQLite reset test, using fresh current records instead of two historical pack loads; all its current review, rollback, backup safety and UI checks remain.

Retired LiteracyCorrectionCompatibilityTest#patchPreservesAllKeysAndAllOtherCourseContentWithinAnExactWhitelist, a pure historical whole-pack correction/conservation contract, under A2-08/A2-13. The useful current lowercase-cue, alternatives and linked-example assertions remain in CurrentLiteracyCueTest; only the historical prompt comparison was removed. The historicalEpochProofCannotAcceptUnrelatedCurrentTeachingChanges helper contract is replaced by the current behavioral guard above. Shared historical helpers and remaining mixed callers stay queued in parts b-b/c; no runtime, curriculum, source/rights record or layout check was changed.

Owner addition: the push trigger now has paths-ignore containing exactly WORK_QUEUE.md. It retains the single push trigger and branch concurrency cancellation, and the full compile/lint/release-gate/unit-suite command is unchanged. YAML parsing and a path-decision check confirm queue-only changes are ignored, while mixed changes, README.md, nested docs/WORK_QUEUE.md and workflow changes run. The current unfrozen/unapproved candidate's top-level sourceDigest was refreshed for the workflow change; all prior candidate identities and approval states remain unchanged. No test exclusions, retries, skips, historical packet regeneration or new evidence bundle were added.

Targeted local development validation passed **16 tests, 0 failures/errors/skips**, Gradle success in **2m 34s**, after correcting a new test's assumption about prescribed selections in the existing guided step. The test now verifies those selections persist and cannot by themselves advance the unit; production behavior was unchanged. Full unfiltered exact-commit CI remains required before closure; this local check is not the A2-13 evidence. Parent tasks and remaining fixture-port parts are not complete.

- 2026-11-02 - Owner direction: Cursor is temporary implementer until Codex usage returns. Codex verifies Cursor's commits on return.

### T02-n1b-b-a exact-commit CI and closure — 2026-11-02

Confirmed `1df43e16b534e733a3aea944cb9cfa89b760d994` against the row. Foundation checks use current store APIs and a synthetic English instruction change. The historical whole-pack whitelist was retired; current lowercase-cue, alternative and linked-example checks remain in CurrentLiteracyCueTest. No source/rights or layout test file changed. `paths-ignore` lists only `WORK_QUEUE.md`; the compile, lint, release-gate and full unit-suite command is unchanged.

The single [push CI run 41505945681](https://github.com/justinkulka-lang/russian-companion/actions/runs/41505945681) checked out **`1df43e16b534e733a3aea944cb9cfa89b760d994`** and **PASSED**. Standard CI XML reports **1,357 tests: 1,356 passed, 0 failures, 0 errors, 1 existing physical-reboot skip**, across 244 suites. The compile/lint/test step ran 19:01:47Z–22:59:51Z (3h 58m 4s). This closure changes only WORK_QUEUE.md, so the paths-ignore skips a new run. The A2-13 evidence remains run 41505945681.

T02-n1b-b-a is **DONE**. Parent T02-n1b-b and T02-n1b stay TODO. Next: **T02-n1b-b-b — CODEX**. No owner action is needed.

- 2026-11-02 — T02-n1b-b-a — 1df43e16b534e733a3aea944cb9cfa89b760d994 — DONE: exact-commit CI 41505945681, 1,356 passed, 0 failed, 1 existing skip; current foundation fixtures and WORK_QUEUE.md-only CI ignore; parent and remaining parts stay queued.

### T02-n1b-b-b split and cumulative-review retirement — 2026-11-03

T02-n1b-b-b is split under Continue step 5. T02-n1b-b-b-a retires historical-only Unit4–30 cumulative-review fixtures. T02-n1b-b-b-b keeps the remaining teaching, progress, and transfer fixtures. Parent criteria stay unchanged. Source/rights intake stays in T02-n1b-c.

Retired, and not ported, because current behavior is already covered: every Unit4–30 `rawCurrentCompletionAndForgedPreReviewActivationDoNotCreditThisUnit` (historical pre-teaching pack; current raw completion and forged activation are in CompletionEvidenceCorrectionTest and CumulativeReviewGateTest); every `oldFormatOneAndTwo*` method and its `oldPack` helper (historical packs and formats 1–2; current format-1 restore is in CumulativeReviewStorageTest, and current backup snapshots stay in the ordinary completion tests); conservation-scoped placement tests for Units 7–14 (current placement remains in Unit4–7 and Units 15–30); Unit11 and Unit14 historical continue UI (current UI remains in Unit11NounReviewUiTest, Unit14PluralPossessionReviewUiTest, and the other units' current continue tests); Unit12–14 ordinary completion tests that ran only on a historical pack (current policy, gates, and backup remain in the Unit12–14 baseline, persistence, and UI tests). Nested historical objective blocks were removed from the Unit7–10 ordinary completion tests; their current policy, answer, advance, and backup assertions remain. The Unit7 current placement helper no longer calls the conservation pin. No runtime, curriculum, source/rights, or layout file changed. Exact-commit CI is still required before this part is DONE.

### T02-n1b-b-b-a exact-commit CI and closure — 2026-11-03

The single [push CI run 41674313979](https://github.com/justinkulka-lang/russian-companion/actions/runs/41674313979) checked out **`8199cfe3602d61e6809cea613bb07c057fbc559a`** and **PASSED**. Standard CI XML reports **1,290 tests: 1,289 passed, 0 failures, 0 errors, 1 existing physical-reboot skip**, across 244 suites. The compile/lint/test step ran 16:28:35Z–18:49:02Z (2h 20m 27s). The drop from 1,357 tests is the retired historical methods. This closure changes only WORK_QUEUE.md, so the paths-ignore skips a new run. The A2-13 evidence remains run 41674313979.

T02-n1b-b-b-a is **DONE**. Parent T02-n1b-b and T02-n1b stay TODO. Next: **T02-n1b-b-b-b — CODEX**. No owner action is needed.

- 2026-11-03 — T02-n1b-b-b-a — 8199cfe3602d61e6809cea613bb07c057fbc559a — DONE: exact-commit CI 41674313979, 1,289 passed, 0 failed, 1 existing skip; historical Unit4–30 cumulative-review fixtures retired; parent and T02-n1b-b-b-b stay queued.

### T02-n1b-b-b-b historical teaching, progress, and transfer retirement — 2026-11-03

Retired, and not ported, because the fixture loaded a historical pack or asserted conservation, old credit, old resume, or old backup equality already covered on current data. Deleted classes: HistoricalTeachingFixtureScopeTest, Unit13ContextTeachingTest, Unit13PastTeachingTest, Unit14ImpersonalRemediationTest, Unit15ImperativeRemediationTest, Unit15AspectPreparationHistoryTest, Unit7InvariancePreparationHistoryTest, and Unit5CumulativeReviewTest (its pack was the pre-grammar-review git snapshot; current Unit 5 policy, backup, and declaration stay in Unit5GrammarReviewTest and Unit5GrammarReviewUiTest). Deleted historical methods from CompleteRuleCorrectionTest, FragmentGlossTeachingTest, Unit4NominativeRoleTest, Unit4PresentTeachingTest, Unit6AnimacyTransferTest, Unit7AgreementTeachingTest, Unit8AgreementTransferTest, Unit8ExistenceHistoryTest, Unit8PluralInstructionTest, Unit9AgreementTransferTest, Unit11AgreementRemediationTest, Unit13ReportedTransferTest, Unit17QuestionTransferTest, Unit20DirectionalityApplicationTest, Unit21ObjectTransferTest, Unit24ContextTransferTest, Unit28TimingTransferTest, Unit29AdjectiveApplicationTest, Unit29PunctuationContextRegressionTest, Unit29ShortAdjectiveCorrectionTest, and Unit30LanguageCorrectionTest. Removed the unused Unit5GrammarReviewTest historical loader. Unit7 and Unit11 structure tests keep the live-pack teach count, remediation body, validator, and wrong-answer repair; only the historical contrast was removed. FragmentGlossUiTest no longer loads the historical sentence. CurrentFoundationHistoryTest, CurrentFoundationPersistenceTest, IndeclinableCorrectionTest, and source/rights intake files are unchanged. No runtime, curriculum, or layout file changed. Exact-commit CI is still required before this part is DONE.

### T02-n1b-b-b-b exact-commit CI and closure — 2026-11-03

The single [push CI run 41706719411](https://github.com/justinkulka-lang/russian-companion/actions/runs/41706719411) checked out **`459ed1e09dee397887144616c131be58ac73c642`** and **PASSED**. Standard CI XML reports **1,207 tests: 1,206 passed, 0 failures, 0 errors, 1 existing physical-reboot skip**, across 236 suites. The compile/lint/test step ran 19:54:58Z–22:58:16Z (3h 3m 18s). The drop from 1,290 tests and 244 suites is the retired classes and methods. This closure changes only WORK_QUEUE.md, so the paths-ignore skips a new run. The A2-13 evidence remains run 41706719411.

T02-n1b-b-b-b is **DONE**. Parent T02-n1b-b-b, T02-n1b-b, and T02-n1b stay TODO. Next: **T02-n1b-b-b — CODEX**. No owner action is needed.

- 2026-11-03 — T02-n1b-b-b-b — 459ed1e09dee397887144616c131be58ac73c642 — DONE: exact-commit CI 41706719411, 1,206 passed, 0 failed, 1 existing skip; historical teaching, progress, and transfer fixtures retired; parent and T02-n1b-c stay queued.

### T02-n1b-b-b parent closure — 2026-11-03

Both parts are DONE and together meet the original T02-n1b-b criteria for foundation, teaching, progress, and review fixtures. T02-n1b-b-b-a retired historical Unit4–30 cumulative-review fixtures (exact-commit CI 41674313979 on 8199cfe3). T02-n1b-b-b-b retired historical teaching, progress, and transfer fixtures. The later run, [41706719411](https://github.com/justinkulka-lang/russian-companion/actions/runs/41706719411), checked out **`459ed1e09dee397887144616c131be58ac73c642`**, which contains both parts, and **PASSED**: 1,207 tests, 1,206 passed, 0 failures, 0 errors, 1 existing physical-reboot skip, 236 suites. Current stale credit, backup, policy, and validator checks remain on live packs or a synthetic English change. Remaining git-show and conservation callers are source/rights intake and stay in T02-n1b-c. This closure changes only WORK_QUEUE.md, so the paths-ignore skips a new run.

T02-n1b-b-b is **DONE**. Parent T02-n1b-b and T02-n1b stay TODO. Next: **T02-n1b-b — CODEX**. No owner action is needed.

- 2026-11-03 — T02-n1b-b-b — 459ed1e09dee397887144616c131be58ac73c642 — DONE: parts b-b-a and b-b-b meet the original criteria; combined exact-commit CI 41706719411, 1,206 passed, 0 failed, 1 existing skip; source/rights intake stays in T02-n1b-c.

### T02-n1b-b parent closure — 2026-11-04

Both parts are DONE and together meet the original T02-n1b-b criteria. T02-n1b-b-a ported foundation checks to current fixtures (exact-commit CI 41505945681 on 1df43e16). T02-n1b-b-b retired the remaining historical teaching, progress, and review fixtures. The later run, [41706719411](https://github.com/justinkulka-lang/russian-companion/actions/runs/41706719411), checked out **`459ed1e09dee397887144616c131be58ac73c642`**, which contains both parts, and **PASSED**: 1,207 tests, 1,206 passed, 0 failures, 0 errors, 1 existing physical-reboot skip, 236 suites. Current backup safety, entry, stale credit/step/resume refusal, review policy, and validator checks remain. Remaining git-show callers are source/rights intake and stay in T02-n1b-c. This closure changes only WORK_QUEUE.md, so the paths-ignore skips a new run.

T02-n1b-b is **DONE**. Parent T02-n1b stays TODO. Next: **T02-n1b-c — CODEX**. No owner action is needed.

- 2026-11-04 — T02-n1b-b — 459ed1e09dee397887144616c131be58ac73c642 — DONE: parts b-a and b-b meet the original criteria; combined exact-commit CI 41706719411, 1,206 passed, 0 failed, 1 existing skip; source/rights intake stays in T02-n1b-c.

### T02-n1b-c split and first historical-contrast retirement — 2026-11-04

T02-n1b-c is split under Continue step 5. T02-n1b-c-a retires historical contrasts in option presentation, the device-acceptance workflow, the Unit 20 pre-correction digest, and the Batch B citation donor. T02-n1b-c-b keeps the remaining source-intake, mapping, review-pipeline, rebuild, and governance conservation work. T02-n1b-c-c removes historical helpers only after their callers are gone and checks the original T02-n1b criteria. Parent criteria stay unchanged.

Retired, and not ported: OptionPresentationTest's two historical pack comparisons and raw-position baseline; DeviceAcceptancePackageTest's historical workflow hash and the assertions that the old workflow uploaded a candidate; Aud44CorrectionTest's git snapshot of the pre-correction Unit 20 digest; BatchBUnits2125Test's equality of two citation items and four shipped texts to a historical pack. Current corpus dispersion, backup reopen, current workflow contents, current unit-identity stability, and the current Unit 21 source pins remain. No runtime, curriculum, source record, or layout file changed. Exact-commit CI is still required before this part is DONE.

### T02-n1b-c-a exact-commit CI and closure — 2026-11-04

The single [push CI run 41809735218](https://github.com/justinkulka-lang/russian-companion/actions/runs/41809735218) checked out **`f6875cf6c638ea5fb9ea955f4a887582f5a97bc7`** and **PASSED**. Standard CI XML reports **1,207 tests: 1,206 passed, 0 failures, 0 errors, 1 existing physical-reboot skip**, across 236 suites. The compile/lint/test step ran 09:46:30Z–11:49:15Z (2h 2m 45s). The count matches the previous run because the retired lines were inside existing tests. This closure changes only WORK_QUEUE.md, so the paths-ignore skips a new run. The A2-13 evidence remains run 41809735218.

T02-n1b-c-a is **DONE**. Parent T02-n1b-c and T02-n1b stay TODO. Next: **T02-n1b-c-b — CODEX**. No owner action is needed.

- 2026-11-04 — T02-n1b-c-a — f6875cf6c638ea5fb9ea955f4a887582f5a97bc7 — DONE: exact-commit CI 41809735218, 1,206 passed, 0 failed, 1 existing skip; historical option-presentation, device-workflow, Unit 20, and citation-donor contrasts retired; parent and T02-n1b-c-b stay queued.

### T02-n1b-c-b split and Units 6–10 historical retirement — 2026-11-04

T02-n1b-c-b is split under Continue step 5. T02-n1b-c-b-a retires the historical digest and pack-equality contrasts in Units610SourceIntakeTest. T02-n1b-c-b-b keeps the remaining source-intake, mapping, review-pipeline, rebuild, and governance conservation work. Parent criteria stay unchanged.

Retired, and not ported: the Units 1–2 pre-correction digest pins, the Unit 4 and Unit 5 historical pack equalities, and the historical review-package digest pins in `earlierUnitDigestsStayPutAndLaterUnitsStayIsolated`. The current Unit 3 digest, Unit 5 mapping digest, live-pack isolation, source binding, freeze bytes, and no-Penguin checks remain. Shared conservation helpers stay because other current checks still call them. No runtime, curriculum, source record, or layout file changed. Exact-commit CI is still required before this part is DONE.

### T02-n1b-c-b-a exact-commit CI and closure — 2026-11-04

The single [push CI run 41842627202](https://github.com/justinkulka-lang/russian-companion/actions/runs/41842627202) checked out **`467bb6a7b94b1faf8d636f657d42760cfe668b80`** and **PASSED**. Standard CI XML reports **1,207 tests: 1,206 passed, 0 failures, 0 errors, 1 existing physical-reboot skip**, across 236 suites. The compile/lint/test step ran 13:57:01Z–16:55:22Z (2h 58m 21s). The count matches the previous run because the retired lines were inside an existing test. This closure changes only WORK_QUEUE.md, so the paths-ignore skips a new run. The A2-13 evidence remains run 41842627202.

T02-n1b-c-b-a is **DONE**. Parent T02-n1b-c-b, T02-n1b-c, and T02-n1b stay TODO. Next: **T02-n1b-c-b-b — CODEX**. No owner action is needed.

- 2026-11-04 — T02-n1b-c-b-a — 467bb6a7b94b1faf8d636f657d42760cfe668b80 — DONE: exact-commit CI 41842627202, 1,206 passed, 0 failed, 1 existing skip; Units 6–10 historical digest and pack-equality contrasts retired; parent and T02-n1b-c-b-b stay queued.

### T02-n1b-c-b-b split and Units 16–20 historical retirement — 2026-11-04

T02-n1b-c-b-b is split under Continue step 5. T02-n1b-c-b-b-a retires the Units 11–15 historical digest contrasts inside Units1620SourceIntakeTest. T02-n1b-c-b-b-b keeps the remaining source-intake, mapping, review-pipeline, rebuild, and governance conservation work. Parent criteria stay unchanged.

Retired, and not ported: the historical Unit 11, 12, 13, 14, and 15 digest pins and the equality of those units to recorded packs. The current Units 16–20 mapping, readiness, freeze-byte, and no-Penguin checks remain. Shared conservation helpers stay because other current checks still call them. No runtime, curriculum, source record, or layout file changed. Exact-commit CI is still required before this part is DONE.

### T02-n1b-c-b-b-a exact-commit CI and closure — 2026-11-04

The single [push CI run 41881810039](https://github.com/justinkulka-lang/russian-companion/actions/runs/41881810039) checked out **`a1398be99ab434b53edd92b853ccc6678e86658e`** and **PASSED**. Standard CI XML reports **1,207 tests: 1,206 passed, 0 failures, 0 errors, 1 existing physical-reboot skip**, across 236 suites. The compile/lint/test step ran 17:56:32Z–19:43:01Z (1h 46m 29s). The count matches the previous run because the retired lines were inside an existing test. This closure changes only WORK_QUEUE.md, so the paths-ignore skips a new run. The A2-13 evidence remains run 41881810039.

T02-n1b-c-b-b-a is **DONE**. Parent T02-n1b-c-b-b, T02-n1b-c-b, T02-n1b-c, and T02-n1b stay TODO. Next: **T02-n1b-c-b-b-b — CODEX**. No owner action is needed.

- 2026-11-04 — T02-n1b-c-b-b-a — a1398be99ab434b53edd92b853ccc6678e86658e — DONE: exact-commit CI 41881810039, 1,206 passed, 0 failed, 1 existing skip; Units 11–15 historical digest contrasts retired from the Units 16–20 intake test; parent and T02-n1b-c-b-b-b stay queued.

### T02-n1b-c-b-b-b split and Units 26–30 historical retirement — 2026-11-04

T02-n1b-c-b-b-b is split under Continue step 5. T02-n1b-c-b-b-b-a retires the historical digest contrasts inside Units2630SourceIntakeTest. T02-n1b-c-b-b-b-b keeps the remaining source-intake, mapping, review-pipeline, rebuild, and governance conservation work. Parent criteria stay unchanged.

Retired, and not ported: the historical Units 1–2 and 4–15 digest pins and the equality of those units to recorded packs. The current Units 3 and 16–25 digest pins, freeze-byte, release, and unit-isolation checks remain. Shared conservation helpers stay because other current checks still call them. No runtime, curriculum, source record, or layout file changed. Exact-commit CI is still required before this part is DONE.

### T02-n1b-c-b-b-b-a exact-commit CI and closure — 2026-11-04

The single [push CI run 41908416874](https://github.com/justinkulka-lang/russian-companion/actions/runs/41908416874) checked out **`9a1c50032422cb1ed35f9b2cbf14fe4cdfdee3ed`** and **PASSED**. Standard CI XML reports **1,207 tests: 1,206 passed, 0 failures, 0 errors, 1 existing physical-reboot skip**, across 236 suites. The compile/lint/test step ran 20:38:29Z–22:20:23Z (1h 41m 54s). The count matches the previous run because the retired lines were inside an existing test. This closure changes only WORK_QUEUE.md, so the paths-ignore skips a new run. The A2-13 evidence remains run 41908416874.

T02-n1b-c-b-b-b-a is **DONE**. Parent T02-n1b-c-b-b-b, T02-n1b-c-b-b, T02-n1b-c-b, T02-n1b-c, and T02-n1b stay TODO. Next: **T02-n1b-c-b-b-b-b — CODEX**. No owner action is needed.

- 2026-11-04 — T02-n1b-c-b-b-b-a — 9a1c50032422cb1ed35f9b2cbf14fe4cdfdee3ed — DONE: exact-commit CI 41908416874, 1,206 passed, 0 failed, 1 existing skip; Units 1–2 and 4–15 historical digest contrasts retired from the Units 26–30 intake test; parent and T02-n1b-c-b-b-b-b stay queued.

### T02-n1b-c-b-b-b-b split and Units 21–25 historical retirement — 2026-11-04

T02-n1b-c-b-b-b-b is split under Continue step 5. T02-n1b-c-b-b-b-b-a retires the historical digest contrasts inside Units2125SourceIntakeTest. T02-n1b-c-b-b-b-b-b keeps the remaining mapping, review-pipeline, rebuild, and governance conservation work. Parent criteria stay unchanged.

Retired, and not ported: the historical Units 1–2 and 4–15 digest pins and the equality of those units to recorded packs. The current Units 3 and 16–20 digest pins, mapping pins, and source checks remain. Shared conservation helpers stay because other current checks still call them. No runtime, curriculum, source record, or layout file changed. Exact-commit CI is still required before this part is DONE.

### T02-n1b-c-b-b-b-b-a exact-commit CI and closure — 2026-11-04

The single [push CI run 41928215452](https://github.com/justinkulka-lang/russian-companion/actions/runs/41928215452) checked out **`0b6c331eab9b2de21d768fcb521403f5cb6b0aa7`** and **PASSED**. Standard CI XML reports **1,207 tests: 1,206 passed, 0 failures, 0 errors, 1 existing physical-reboot skip**, across 236 suites. The compile/lint/test step ran 22:47:42Z–01:45:55Z (2h 58m 13s). The count matches the previous run because the retired lines were inside an existing test. This closure changes only WORK_QUEUE.md, so the paths-ignore skips a new run. The A2-13 evidence remains run 41928215452.

T02-n1b-c-b-b-b-b-a is **DONE**. Parent T02-n1b-c-b-b-b-b, T02-n1b-c-b-b-b, T02-n1b-c-b-b, T02-n1b-c-b, T02-n1b-c, and T02-n1b stay TODO. Next: **T02-n1b-c-b-b-b-b-b — CODEX**. No owner action is needed.

- 2026-11-04 — T02-n1b-c-b-b-b-b-a — 0b6c331eab9b2de21d768fcb521403f5cb6b0aa7 — DONE: exact-commit CI 41928215452, 1,206 passed, 0 failed, 1 existing skip; Units 1–2 and 4–15 historical digest contrasts retired from the Units 21–25 intake test; parent and T02-n1b-c-b-b-b-b-b stay queued.

### T02-n1b-c-b-b-b-b-b split and Units 11–15 mapping-gap historical retirement — 2026-11-05

T02-n1b-c-b-b-b-b-b is split under Continue step 5. T02-n1b-c-b-b-b-b-b-a retires the historical digest contrasts inside Units1115MappingGapTest. T02-n1b-c-b-b-b-b-b-b keeps the remaining review-pipeline, rebuild, governance, compatibility, batch, and source-guard conservation work. Parent criteria stay unchanged.

Retired, and not ported: the historical Units 1–2 and 4–10 digest pins and the equality of those units to recorded packs. The current Unit 3 digest pin, Units 11–15 mapping checks, and the check that a Unit 11 mapping-note change does not move Units 1–10 remain. Shared conservation helpers stay because other current checks still call them. No runtime, curriculum, source record, or layout file changed. Exact-commit CI is still required before this part is DONE.

### T02-n1b-c-b-b-b-b-b-a exact-commit CI and closure — 2026-11-05

The single [push CI run 41997544123](https://github.com/justinkulka-lang/russian-companion/actions/runs/41997544123) checked out **`2bd5b54235b7cb9bad81c9974591ad453ed662da`** and **PASSED**. Standard CI XML reports **1,207 tests: 1,206 passed, 0 failures, 0 errors, 1 existing physical-reboot skip**, across 236 suites. The compile/lint/test step ran 08:51:05Z–11:52:00Z (3h 0m 55s). The count matches the previous run because the retired lines were inside an existing test. This closure changes only WORK_QUEUE.md, so the paths-ignore skips a new run. The A2-13 evidence remains run 41997544123.

T02-n1b-c-b-b-b-b-b-a is **DONE**. Parent T02-n1b-c-b-b-b-b-b and the earlier source-intake parents stay TODO. Next: **T02-n1b-c-b-b-b-b-b-b — CODEX**. No owner action is needed.

- 2026-11-05 — T02-n1b-c-b-b-b-b-b-a — 2bd5b54235b7cb9bad81c9974591ad453ed662da — DONE: exact-commit CI 41997544123, 1,206 passed, 0 failed, 1 existing skip; Units 1–2 and 4–10 historical digest contrasts retired from the Units 11–15 mapping-gap test; parent and T02-n1b-c-b-b-b-b-b-b stay queued.

### T02-n1b-c-b-b-b-b-b-b split and rebuild historical retirement — 2026-11-05

T02-n1b-c-b-b-b-b-b-b is split under Continue step 5. T02-n1b-c-b-b-b-b-b-b-a retires the historical digest contrasts inside V2RebuildTest. T02-n1b-c-b-b-b-b-b-b-b keeps the remaining review-pipeline, governance, compatibility, batch, and source-guard conservation work. Parent criteria stay unchanged.

Retired, and not ported: the historical Units 1–2, 4, and 5 digest pins and the equality of those units to recorded packs. The current Unit 3 digest pin, mapping rejection, and no-Penguin checks remain. Shared conservation helpers stay because other current checks still call them. No runtime, curriculum, source record, or layout file changed. Exact-commit CI is still required before this part is DONE.

### T02-n1b-c-b-b-b-b-b-b-a exact-commit CI and closure — 2026-11-05

The single [push CI run 42044880569](https://github.com/justinkulka-lang/russian-companion/actions/runs/42044880569) checked out **`7f4efa1306af7e3d45f95dc7ccf4502b1dfdbaab`** and **PASSED**. Standard CI XML reports **1,207 tests: 1,206 passed, 0 failures, 0 errors, 1 existing physical-reboot skip**, across 236 suites. The compile/lint/test step ran 14:27:51Z–17:11:11Z (2h 43m 20s). The count matches the previous run because the retired lines were inside an existing test. This closure changes only WORK_QUEUE.md, so the paths-ignore skips a new run. The A2-13 evidence remains run 42044880569.

T02-n1b-c-b-b-b-b-b-b-a is **DONE**. Parent T02-n1b-c-b-b-b-b-b-b and the earlier source-intake parents stay TODO. Next: **T02-n1b-c-b-b-b-b-b-b-b — CODEX**. No owner action is needed.

- 2026-11-05 — T02-n1b-c-b-b-b-b-b-b-a — 7f4efa1306af7e3d45f95dc7ccf4502b1dfdbaab — DONE: exact-commit CI 42044880569, 1,206 passed, 0 failed, 1 existing skip; Units 1–2, 4, and 5 historical digest contrasts retired from the rebuild mapping-safeguard test; parent and T02-n1b-c-b-b-b-b-b-b-b stay queued.

### T02-n1b-c-b-b-b-b-b-b-b split and governance historical retirement — 2026-11-05

T02-n1b-c-b-b-b-b-b-b-b is split under Continue step 5. T02-n1b-c-b-b-b-b-b-b-b-a retires the historical pack equality inside OwnerGovernanceDecisionTest. T02-n1b-c-b-b-b-b-b-b-b-b keeps the remaining review-pipeline, compatibility, batch, and source-guard conservation work. Parent criteria stay unchanged.

Retired, and not ported: the prior aggregate digest, the pre-review declaration proof, and the equality of the current pack to a recorded commit. The current unresolved-capability, readiness, variance, mapping, and release checks remain. Shared conservation helpers stay because other current checks still call them. No runtime, curriculum, source record, or layout file changed. Exact-commit CI is still required before this part is DONE.

### T02-n1b-c-b-b-b-b-b-b-b-a exact-commit CI and closure — 2026-11-05

The single [push CI run 42105077842](https://github.com/justinkulka-lang/russian-companion/actions/runs/42105077842) checked out **`2544ac2de687c3c3edd95670af34ceb9c923c534`** and **PASSED**. Standard CI XML reports **1,207 tests: 1,206 passed, 0 failures, 0 errors, 1 existing physical-reboot skip**, across 236 suites. The compile/lint/test step ran 20:38:01Z–22:58:41Z (2h 20m 40s). The count matches the previous run because the retired lines were inside an existing test. This closure changes only WORK_QUEUE.md, so the paths-ignore skips a new run. The A2-13 evidence remains run 42105077842.

T02-n1b-c-b-b-b-b-b-b-b-a is **DONE**. Parent T02-n1b-c-b-b-b-b-b-b-b and the earlier source-intake parents stay TODO. Next: **T02-n1b-c-b-b-b-b-b-b-b-b — CODEX**. No owner action is needed.

- 2026-11-05 — T02-n1b-c-b-b-b-b-b-b-b-a — 2544ac2de687c3c3edd95670af34ceb9c923c534 — DONE: exact-commit CI 42105077842, 1,206 passed, 0 failed, 1 existing skip; historical pack equality retired from the owner-governance decision test; parent and T02-n1b-c-b-b-b-b-b-b-b-b stay queued.

### T02-n1b-c-b-b-b-b-b-b-b-b split and review-pipeline historical retirement — 2026-11-05

T02-n1b-c-b-b-b-b-b-b-b-b is split under Continue step 5. T02-n1b-c-b-b-b-b-b-b-b-b-a retires the historical digest contrasts inside UnitReviewPipelineTest. T02-n1b-c-b-b-b-b-b-b-b-b-b keeps the remaining compatibility, batch, and source-guard conservation work. Parent criteria stay unchanged.

Retired, and not ported: the historical Units 1–2, 4, and 5 digest pins and the equality of those units to recorded packs. The current mapping binding, Unit 3 digest pin, worksheet checks, no-Penguin check, and refusal of a relabelled current packet remain. Shared conservation helpers stay because other current checks still call them. No runtime, curriculum, source record, or layout file changed. Exact-commit CI is still required before this part is DONE.

### T02-n1b-c-b-b-b-b-b-b-b-b-a exact-commit CI and closure — 2026-11-05

The single [push CI run 42129111778](https://github.com/justinkulka-lang/russian-companion/actions/runs/42129111778) checked out **`7798996685f7dcae193a5e081a1a450593e3e9ec`** and **PASSED**. Standard CI XML reports **1,207 tests: 1,206 passed, 0 failures, 0 errors, 1 existing physical-reboot skip**, across 236 suites. The compile/lint/test step ran 23:26:40Z–02:20:02Z (2h 53m 22s). The count matches the previous run because the retired lines were inside an existing test. This closure changes only WORK_QUEUE.md, so the paths-ignore skips a new run. The A2-13 evidence remains run 42129111778.

T02-n1b-c-b-b-b-b-b-b-b-b-a is **DONE**. Parent T02-n1b-c-b-b-b-b-b-b-b-b and the earlier source-intake parents stay TODO. Next: **T02-n1b-c-b-b-b-b-b-b-b-b-b — CODEX**. No owner action is needed.

- 2026-11-05 — T02-n1b-c-b-b-b-b-b-b-b-b-a — 7798996685f7dcae193a5e081a1a450593e3e9ec — DONE: exact-commit CI 42129111778, 1,206 passed, 0 failed, 1 existing skip; Units 1–2, 4, and 5 historical digest contrasts retired from the review-pipeline mapping test; parent and T02-n1b-c-b-b-b-b-b-b-b-b-b stay queued.

### T02-n1b-c-b-b-b-b-b-b-b-b-b split and Unit 14 historical retirement — 2026-11-05

T02-n1b-c-b-b-b-b-b-b-b-b-b is split under Continue step 5. T02-n1b-c-b-b-b-b-b-b-b-b-b-a retires the historical prior-declaration equality inside the Unit 14 compatibility test. T02-n1b-c-b-b-b-b-b-b-b-b-b-b keeps the remaining compatibility, batch, and source-guard conservation work. Parent criteria stay unchanged.

Retired, and not ported: the equality of the current Unit 14 pack to a recorded prior declaration, the claim that the old pack does not credit a current assignment, and the compiled historical observation. The current policy-pool bounds and the live-pack assignment schema, currency, and owner units remain. Shared conservation helpers stay because other current checks still call them. No runtime, curriculum, source record, or layout file changed. Exact-commit CI is still required before this part is DONE.

### T02-n1b-c-b-b-b-b-b-b-b-b-b-a exact-commit CI and closure — 2026-11-05

The single [push CI run 42150248387](https://github.com/justinkulka-lang/russian-companion/actions/runs/42150248387) checked out **`fd7d537ef42f5e7e857c4f20f580d58bcfb58dd8`** and **PASSED**. Standard CI XML reports **1,207 tests: 1,206 passed, 0 failures, 0 errors, 1 existing physical-reboot skip**, across 236 suites. The compile/lint/test step ran 02:27:01Z–05:28:58Z (3h 1m 57s). The count matches the previous run because the retired lines were inside an existing test. This closure changes only WORK_QUEUE.md, so the paths-ignore skips a new run. The A2-13 evidence remains run 42150248387.

T02-n1b-c-b-b-b-b-b-b-b-b-b-a is **DONE**. Parent T02-n1b-c-b-b-b-b-b-b-b-b-b and the earlier source-intake parents stay TODO. Next: **T02-n1b-c-b-b-b-b-b-b-b-b-b-b — CODEX**. No owner action is needed.

- 2026-11-05 — T02-n1b-c-b-b-b-b-b-b-b-b-b-a — fd7d537ef42f5e7e857c4f20f580d58bcfb58dd8 — DONE: exact-commit CI 42150248387, 1,206 passed, 0 failed, 1 existing skip; Unit 14 historical pack equality and compiled observation retired; parent and T02-n1b-c-b-b-b-b-b-b-b-b-b-b stay queued.

### T02-n1b-c-b-b-b-b-b-b-b-b-b-b split and Batch A historical retirement — 2026-11-06

T02-n1b-c-b-b-b-b-b-b-b-b-b-b is split under Continue step 5. T02-n1b-c-b-b-b-b-b-b-b-b-b-b-a retires the Batch A historical explanation pack and the unused Unit 13 historical loader. T02-n1b-c-b-b-b-b-b-b-b-b-b-b-b keeps the reviewed-curriculum source-guard comparison. Parent criteria stay unchanged.

Retired, and not ported: the original 41-item explanation contract on a recorded pack, the equality of retained Batch A items to that pack, and the unused Unit 13 historical loader. The current 47-item Batch A explanation, validation, expanded-construct, and clock-vocabulary checks remain, as do the Unit 13 live-store checks. Shared conservation helpers stay because Unit1ReviewPackage still reads RecordedLiteracyEpochScope.latestCommit. The source-guard file still has its own historical git lookup, which the next part owns. No runtime, curriculum, source record, or layout file changed. Exact-commit CI is still required before this part is DONE.

### T02-n1b-c-b-b-b-b-b-b-b-b-b-b-a exact-commit CI and closure — 2026-11-06

The single [push CI run 42233093719](https://github.com/justinkulka-lang/russian-companion/actions/runs/42233093719) checked out **`c9fe8b366ea9bc72f914ae54d8b241c0dd21a232`** and **PASSED**. Standard CI XML reports **1,206 tests: 1,205 passed, 0 failures, 0 errors, 1 existing physical-reboot skip**, across 236 suites. The compile/lint/test step ran 14:21:34Z–16:04:47Z (1h 43m 13s). The count is one below the previous run because the retired historical Batch A method was a whole test. This closure changes only WORK_QUEUE.md, so the paths-ignore skips a new run. The A2-13 evidence remains run 42233093719.

T02-n1b-c-b-b-b-b-b-b-b-b-b-b-a is **DONE**. Parent T02-n1b-c-b-b-b-b-b-b-b-b-b-b and the earlier source-intake parents stay TODO. Next: **T02-n1b-c-b-b-b-b-b-b-b-b-b-b-b — CODEX**. No owner action is needed.

- 2026-11-06 — T02-n1b-c-b-b-b-b-b-b-b-b-b-b-a — c9fe8b366ea9bc72f914ae54d8b241c0dd21a232 — DONE: exact-commit CI 42233093719, 1,205 passed, 0 failed, 1 existing skip; Batch A historical pack equality and the unused Unit 13 loader retired; parent and T02-n1b-c-b-b-b-b-b-b-b-b-b-b-b stay queued.

### T02-n1-final — 2026-11-06

Owner direction collapsed the remaining T02-n1 rows into T02-n1-final and stopped further splits. The duplicate unfinished sub-rows are removed. Continue step 5 now allows at most three parts, and a part may not split again.

Retired, and not ported: the reviewed-curriculum comparison against commit e66dfe07, including the 288/22 historical binding contract and the claim that current files differ from that snapshot by four bindings. The current 19-file, 292-binding, 26 item-id catalog, the two pending research-cue bindings, the four present-form item references, and the missing-item refusal remain. RecordedCurriculumConservationScope and RecordedLiteracyEpochScope are deleted. Their only live caller was Unit1ReviewPackage, which now keeps the Units 1–2 source commit as UNITS_1_2_LITERACY_CURRICULUM_COMMIT. RecordedIntakeCorrectionScope had no remaining caller and is deleted with them. Current source-commit, validator, release, and layout checks stay. Exact-commit CI is still required before T02-n1-final, T02-n1b-c, T02-n1b, and T02-n1 are DONE.

### T02-n1-final exact-commit CI and closure — 2026-11-06

The single [push CI run 42249913161](https://github.com/justinkulka-lang/russian-companion/actions/runs/42249913161) checked out **`e2c9d2489a3f1e5c287e255b16e6b603cad4e634`** and **PASSED**. Standard CI XML reports **1,206 tests: 1,205 passed, 0 failures, 0 errors, 1 existing physical-reboot skip**, across 236 suites. The compile/lint/test step ran 16:11:22Z–17:57:06Z (1h 45m 44s). The count matches the previous run because the retired source-guard lines stayed inside existing tests. This closure changes only WORK_QUEUE.md, so the paths-ignore skips a new run. The A2-13 evidence remains run 42249913161.

T02-n1-final, T02-n1b-c, T02-n1b, and T02-n1 are **DONE**. Next eligible task is **T03-n2 — CODEX**. It was not started. No owner action is needed.

- 2026-11-06 — T02-n1-final — e2c9d2489a3f1e5c287e255b16e6b603cad4e634 — DONE: exact-commit CI 42249913161, 1,205 passed, 0 failed, 1 existing skip; source-guard historical comparison and conservation helpers retired; T02-n1b-c, T02-n1b, and T02-n1 closed with the same run.

### T07-n1 — 2026-11-06

Owner direction removed the T07 dependency and started T07-n1. T03-n2 was not started. The comparable before window is exact-commit CI 42249913161 on e2c9d248: compile/lint/test 16:11:22Z–17:57:06Z (1h 45m 44s; 10:11 a.m.–11:57 a.m. CT). XML summed to 100.4 minutes across 1,206 tests, 1,205 passed, 1 existing physical-reboot skip, and 236 suites.

The same checks still run. Governed-attempt duplicate detection uses one bounded SQL lookup instead of loading the attempt table. Immutable check-item digests, pack and unit content-review digests, and provenance hashes are reused by object identity, and a copied or edited pack still misses. Provenance, authored-English, and item validation use one index per pass instead of scanning the pack for every record. Prescribed-credit membership is reused for an unchanged learner view. Heap stays 3g, forks stay 1, and classes still recycle after 8. No exclusions, skips, narrowed filters, or weakened assertions were added. Exact-commit CI is still required before this task is DONE.

### T07-n1 exact-commit CI — 2026-11-06

The single [push CI run 42271099550](https://github.com/justinkulka-lang/russian-companion/actions/runs/42271099550) checked out **`6485b96f516a1bac5420c155391a5ea5bca4a2d0`** and **FAILED**. The compile/lint/test step ran 18:29:53Z–19:50:52Z (1h 20m 59s; 12:29 p.m.–1:50 p.m. CT). Gradle reported BUILD FAILED in 1h 20m 58s. Eight tests failed, all on recorded source identity after the validator edit: AcceptanceBoundaryTest at006, DeviceAcceptancePackageTest physical-device package, the five ReviewedCurriculumSourceGuardTest methods, and ReleaseSourceDigestTest committed candidate. V2Validator.kt is in the reviewed-cyrillic catalog, so its sha256 no longer matches, and the current candidate sourceDigest was not refreshed. No check was removed. T07-n1 stays IN_PROGRESS. What remains is to refresh those two recorded identities and pass a new exact-commit run. The step was under 90 minutes, but a failed suite does not meet the done-when. T03-n2 was not started.

Follow-up: the reviewed-cyrillic catalog sha256 for V2Validator.kt and the current unfrozen candidate sourceDigest are refreshed to the edited source. Preserved candidate identities, approval status, and every behavior check stay. Exact-commit CI is still required before this task is DONE.

### T07-n1 exact-commit CI and closure — 2026-11-06

The single [push CI run 42284640468](https://github.com/justinkulka-lang/russian-companion/actions/runs/42284640468) checked out **`9f217f857062221d4ca9116f47067aa9ca388813`** and **PASSED**. Standard CI XML reports **1,206 tests: 1,205 passed, 0 failures, 0 errors, 1 existing physical-reboot skip**, across 236 suites. The compile/lint/test step ran 20:01:37Z–21:21:53Z (1h 20m 16s; 2:01 p.m.–3:21 p.m. CT). XML suite time summed to 72.6 minutes, down from 100.4 minutes on run 42249913161 (1h 45m 44s). This closure changes only WORK_QUEUE.md, so the paths-ignore skips a new run. The A2-13 evidence remains run 42284640468.

T07-n1 is **DONE**. Next eligible task is **T03-n2 — CODEX**. It was not started. No owner action is needed.

- 2026-11-06 — T07-n1 — 9f217f857062221d4ca9116f47067aa9ca388813 — DONE: exact-commit CI 42284640468, 1,205 passed, 0 failed, 1 existing skip; compile/lint/test 1h 20m 16s; repeated digests and attempt lookups reused; catalog hash and candidate source digest refreshed.

### T03-n2-a exact-commit CI and closure — 2026-11-06

The single [push CI run 42299490608](https://github.com/justinkulka-lang/russian-companion/actions/runs/42299490608) checked out **`aa20b3c5f627938073ad5f58eaecf9087f11fce8`** and **PASSED**. Standard CI XML reports **1,207 tests: 1,206 passed, 0 failures, 0 errors, 1 existing physical-reboot skip**, across 236 suites. The compile/lint/test step ran 21:47:29Z–22:51:10Z (1h 3m 41s; 3:47 p.m.–4:51 p.m. CT). XML suite time summed to 57.5 minutes. Current review gates remain. Prior policy ids, prior-active digests, and empty or pre-activation completion links do not supply current credit. Backup readers and database migrations were not changed. This closure changes only WORK_QUEUE.md, so the paths-ignore skips a new run. The A2-13 evidence remains run 42299490608.

T03-n2-a is **DONE**. T03-n2 stays open. Next eligible task is **T03-n2-b — CODEX**. It was not started. No owner action is needed.

- 2026-11-06 — T03-n2-a — aa20b3c5f627938073ad5f58eaecf9087f11fce8 — DONE: exact-commit CI 42299490608 passed, 1,206 passed, 0 failed, 1 existing skip; prior-policy and pre-activation identities rejected.

### VERIFY-CURSOR-2 — 2026-11-08

2026-11-06 - Owner direction: Codex resumes as primary implementer. Cursor is no longer active.

Owner-directed verification before T03-n2-b: fetched the working-branch ref and reset the checkout to the then-current authoritative remote **38fc7d8af80008e6eaafcb02dbc31899cab0dedb**. Older local copies were not used for the review. Read AGENTS.md, Amendment 2 and this queue. Reviewed the implementation diffs/current source against each listed Done when; passing implementation work was not repeated. No unit suite, build, lint invocation or new CI run was requested by this verification. Existing exact-commit CI is the test evidence under A2-13, as explicitly directed by the owner.

GitHub API verification confirmed all **17 distinct recorded push runs** below completed successfully, each at exactly the full commit recorded in its task row (attempt 1, Engineering verification). For every run, the combined compile/lint/release-gate/full-host-test step and report upload succeeded. The committed workflow retains the full unfiltered test command; WORK_QUEUE.md is its sole ignored push path. This review changes only WORK_QUEUE.md, so its push is excluded by that owner-requested filter.

| Task implementation | Tested commit | Existing exact-commit CI | Done-when review |
|---|---|---|---|
| T02-n1b-b-a | `1df43e16` | [41505945681](https://github.com/justinkulka-lang/russian-companion/actions/runs/41505945681) | PASS: current foundation states, earned/placed entry, English identity mutation and queue-only path ignore retained. |
| T02-n1b-b-b-a | `8199cfe3` | [41674313979](https://github.com/justinkulka-lang/russian-companion/actions/runs/41674313979) | PASS: historical declarations, formats and placement fixtures retired; current review gates, snapshots, UI and epoch checks remain. |
| T02-n1b-b-b-b | `459ed1e0` | [41706719411](https://github.com/justinkulka-lang/russian-companion/actions/runs/41706719411) | GAP: historical loaders retired, but useful live-pack/mixed current checks were also removed; VERIFY-CURSOR-2-n1. |
| T02-n1b-c-a | `f6875cf6` | [41809735218](https://github.com/justinkulka-lang/russian-companion/actions/runs/41809735218) | PASS: old option/workflow/citation/digest contrasts retired; current corpus, CI wiring, source pins and isolation retained. |
| T02-n1b-c-b-a | `467bb6a7` | [41842627202](https://github.com/justinkulka-lang/russian-companion/actions/runs/41842627202) | PASS: Units 6–10 historical pack/digest comparisons removed; current source bindings, mapping and isolation remain. |
| T02-n1b-c-b-b-a | `a1398be9` | [41881810039](https://github.com/justinkulka-lang/russian-companion/actions/runs/41881810039) | PASS: Units 16–20 intake no longer loads historical Units 11–15 packs; current mapping, readiness and source checks remain. |
| T02-n1b-c-b-b-b-a | `9a1c5003` | [41908416874](https://github.com/justinkulka-lang/russian-companion/actions/runs/41908416874) | PASS: Units 26–30 historical contrasts removed; current digest pins, source and isolation checks remain. |
| T02-n1b-c-b-b-b-b-a | `0b6c331e` | [41928215452](https://github.com/justinkulka-lang/russian-companion/actions/runs/41928215452) | PASS: Units 21–25 historical contrasts removed; current mapping, source and unaffected-unit checks remain. |
| T02-n1b-c-b-b-b-b-b-a | `2bd5b542` | [41997544123](https://github.com/justinkulka-lang/russian-companion/actions/runs/41997544123) | PASS: mapping-gap test uses current Unit3 identity and live mapping-mutation isolation, without historical packs. |
| T02-n1b-c-b-b-b-b-b-b-a | `7f4efa13` | [42044880569](https://github.com/justinkulka-lang/russian-companion/actions/runs/42044880569) | PASS: rebuild mapping test retains current Unit3, mapping rejection and no-Penguin checks without old pack comparisons. |
| T02-n1b-c-b-b-b-b-b-b-b-a | `2544ac2d` | [42105077842](https://github.com/justinkulka-lang/russian-companion/actions/runs/42105077842) | PASS: current capability, readiness, mapping, variance and release checks retained; old whole-pack equality retired. |
| T02-n1b-c-b-b-b-b-b-b-b-b-a | `77989966` | [42129111778](https://github.com/justinkulka-lang/russian-companion/actions/runs/42129111778) | PASS: live mapping binding, worksheet semantics and relabelled-origin refusal remain; historical digest blocks retired. |
| T02-n1b-c-b-b-b-b-b-b-b-b-b-a | `fd7d537e` | [42150248387](https://github.com/justinkulka-lang/russian-companion/actions/runs/42150248387) | PASS: current Unit14 policy pools, schema and assignment owner checks retained; old declaration/compiled observation removed. |
| T02-n1b-c-b-b-b-b-b-b-b-b-b-b-a | `c9fe8b36` | [42233093719](https://github.com/justinkulka-lang/russian-companion/actions/runs/42233093719) | PASS: historical Batch A method/pack equality and unused Unit13 loader removed; current 47-item explanation and wrong-key checks remain. |
| T02-n1-final | `e2c9d248` | [42249913161](https://github.com/justinkulka-lang/russian-companion/actions/runs/42249913161) | PARTIAL: all three conservation helpers removed, current source guard retained; remaining packet/export/comparison dependencies require VERIFY-CURSOR-2-n2. |
| T03-n2-a | `aa20b3c5` | [42299490608](https://github.com/justinkulka-lang/russian-companion/actions/runs/42299490608) | PASS: only current policy and unit identity supply credit; prior/pre-activation/empty-link acceptance removed; new current rejection test passes. |
| T07-n1 | `9f217f85` | [42284640468](https://github.com/justinkulka-lang/russian-companion/actions/runs/42284640468) | PASS: same tests/assertions/configuration; passing full verification under 90 minutes on comparable hosted runner. |

The aggregate T02-n1b-b-b and T02-n1b-b rows inherit the 459ed1e0 assessment; T02-n1b-c, T02-n1b and T02-n1 inherit the final retirement assessment at e2c9d248. Their published implementation/CI records remain intact. Passing CI does not resolve the two completion gaps below; the follow-up tasks record the missing criteria rather than silently accepting them.

**Finding 1 — useful current checks removed with mixed history (VERIFY-CURSOR-2-n1).** Commit 459ed1e0 removed the Unit4 correct-targets/missing-required-teaching ordinary-gate scenario, whose historical dependency only selected the formerly added teaching steps; it can exercise the current rule with current steps. The same commit deleted Unit15AspectPreparationHistoryTest in full, including overlappingEnglishLabelsRejectWithoutThrowingAndDoNotBecomeNativeEvidence, which called only current() and performed a negative validator mutation, with no historical pack dependency. Current generic foundation and instructional-validity tests remain, but those specific current negative/gating scenarios were not ported. Restore current checks without resurrecting conservation or old learner compatibility. This is a test-coverage/completion gap, not a claimed failing application behavior.

**Finding 2 — retirement still incomplete (VERIFY-CURSOR-2-n2).** At reviewed head 38fc7d8a, SourceTextPoolTest still conditionally loads source.pool.baseline and asserts equality of the entire pack to that external comparison. Units2630StructuralMappingTest#mappingWorksheetsBindStructuralFactsAndStayUnresolved still compares a committed per-unit worksheet to a rendering, writes it under WRITE_STRUCTURAL_MAPPING, and requires/reads historical review packets/manifests/worksheets/instructions. V2RebuildTest#durablePackIdentityStaysHonestAndUnitIsolationHolds still reads old Unit3–5 packet prose, and Units2125SourceIntakeTest still requires the Unit23 packet file. These leave historical-only packet dependencies/export and comparison behavior behind the T02-n1-final broad completion claim (A2-12 to A2-14). The fix must keep current source/rights and mapping facts intact and preserve useful semantic checks in memory. Current curriculum-source commit validation is an origin check and remains required; baseline-named classes that exercise live current gates are not retired just for their names.

**T07-n1 scope and timing confirmed.** Compared the complete performance change range from 8a2cabcd (before implementation) through 9f217f85 (passing implementation), including 6485b96f. The diff for app/src/test/java, app/src/androidTest, .github/workflows, app/build.gradle.kts and gradle.properties is empty: no tests/methods/assertions were removed or weakened and no exclusions, skips or narrowed filters were added. The test-resource change is solely the V2Validator.kt hash in the reviewed source catalog; candidate changes refresh only the current unapproved/unfrozen source identity. Runtime changes bound duplicate-attempt lookup to the same matching fields/order and reuse immutable-identity digests/indices; validation conditions and canonical fields remain. Source/rights records, curriculum assets, governance records, historical packet files and the three current layout classes have no changes across the reviewed work.

Independently read the existing CI JUnit artifacts for before run 42249913161 (e2c9d248) and after run 42284640468 (9f217f85): both contain **1,206 tests, 0 failures/errors, 1 existing physical-reboot skip, 236 suites**, with identical executed method identities and the same skip. Both GitHub jobs use ubuntu-latest. API step timestamps confirm **1h 45m 44s** before and **1h 20m 16s** after. This isolates the performance change from the earlier authorized historical-test retirement and satisfies the under-90-minute target. No suite was rerun.

**T03-n2-a confirmed.** Current declaration/assignment lookup accepts only cumulativeReviewPolicy(targetUnit), recorded unit identity must equal the current digest, and completion credit rejects pre-activation, empty-link and missing-order records. Current assignment/owner and accepted-answer snapshot checks remain. The removed prior-policy lookup branches/digest reconstruction fallbacks are absent. Backup readers and database migrations were unchanged, as required for this part. Existing CI 42299490608 at aa20b3c5 contains **1,207 tests, 0 failures/errors, 1 existing physical-reboot skip, 236 suites**; its only method addition relative to the performance pass is Unit5GrammarReviewTest#priorPolicyAndPreActivationIdentitiesDoNotSupplyCurrentCredit. No method was removed. Current V2Validator.kt and V2CumulativeReview.kt source-catalog hashes match disk, and the candidate remains NOT_APPROVED and unfrozen. Remaining obsolete backup formats and row-preservation migrations are correctly deferred to T03-n2-b/c.

**Split rule confirmed.** Continue step 5 already states at most three parts (-a, -b, -c), and a part may not split again; if a part remains too big it is BLOCKED with what remains. The owner collapsed the unfinished recursive retirement chain into T02-n1-final on 2026-11-06. Historical completed IDs remain records; no further recursive split was created by this review. T03-n2 has exactly a/b/c. Follow-up fixes are independent TODO findings, not further splits.

VERIFY-CURSOR-2 is complete with the two fix tasks queued at the end of Phase 0. No fixes or T03-n2-b implementation were started. Next eligible normal-order task remains **T03-n2-b — CODEX**; the new fixes remain visible in the queue. No owner action is required to proceed under the existing queue order.


### 2026-11-08 — T03-n2-b current-only backup admission (implementation; CI pending)

Resumed from authoritative remote 05920ac6639ab4376b94d412710f9d6a9eb9bb62; start commit 9f82ab26. Archive import now accepts only BACKUP_FORMAT_VERSION and BACKUP_SCHEMA_VERSION (both currently 3). Direct learner replacement rejects every other schema without mutation. The writer always emits the current format and refuses unsupported document schemas before writing bytes; obsolete schema-specific completion handling is retired. Database migrations and schema fixtures are unchanged for T03-n2-c.

Replaced the two legacy-positive restore expectations with rejection/no-mutation checks and added independently rewritten, correctly checksummed archive cases for obsolete, future, extreme and mismatched versions. Current review round-trip/rollback, forged-link rejection, bad hashes, archive limits, cancellation and rights checks remain. Refreshed only the unfrozen NOT_APPROVED candidate's current sourceDigest; preserved candidate identities are unchanged. Development smoke: 72 tests, 0 failures/errors/skips, BUILD SUCCESSFUL in 4m 34s (local environment init only; not acceptance evidence). No CI configuration, behavior exclusions or skips were added. Full unchanged exact-commit CI is required before marking this part DONE. T03-n2-c and the verification fixes were not started.


### 2026-11-08 — T03-n2-b completed on exact-commit CI

Implementation **b7f5d57d3be19972711869bbe1162fa99cb73b25** passed [CI 42445292140](https://github.com/justinkulka-lang/russian-companion/actions/runs/42445292140), push event, first attempt. The run's head SHA and checkout log both match that implementation commit. The unchanged committed command `./gradlew compileDebugSources lintDebug verifyReleaseGateAttachment testDebugUnitTest --continue --stacktrace` succeeded, including compilation, lint and release-gate attachment. Gradle reports BUILD SUCCESSFUL in **1h 2m 46s**; the combined verification step ran **04:05:08–05:07:55 UTC (1h 2m 47s)**, and the job ran **04:04:26–05:07:58 UTC (1h 3m 32s)**.

Read the uploaded engineering-test-reports artifact (13604913282): **1,210 tests, 0 failures, 0 errors, 1 existing physical-reboot skip, 237 suites**. The skip remains AcceptanceBoundaryTest#at020_physicalRebootPersistsGovernedRecords. BackupVersionBoundaryTest (3), CumulativeReviewStorageTest (18), CumulativeReviewGateTest (8), BackupArchiveBoundaryTest (8), BackupImportLimitsTest (19) and BackupLicenseTest (14) all passed without skips. The current backup round-trip, forged-link/hash rejection and import safety remain covered; obsolete format/schema admission and legacy-positive restore expectations are retired under A2-08/A2-09. No database migrations, source/rights records, CI triggers, exclusions or skips changed. The candidate stays unfrozen and NOT_APPROVED; its preserved historical identities are untouched. No evidence folder or governance bundle was created.

**T03-n2-b DONE.** Parent T03-n2 remains open until part c completes. Next eligible task: **T03-n2-c — CODEX**. T03-n2-c and VERIFY-CURSOR-2-n1/n2 remain unstarted. No owner action is needed.


### 2026-11-08 — Owner-directed continuous mode

Owner authorized consecutive eligible tasks, each with fetched remote ancestry, one-task implementation, push, passing exact-commit CI and queue closure before the next. Stop at the next STOP-OWNER/STOP-CLAUDE, BLOCKED task, failed exact-commit CI after one fix attempt, completion of T04-n1/T08/T12/T14/T15–T19, five completed tasks, or low session capacity (finish or cleanly park and push the current task, recording the exact stopping point). Three-part/no-recursive-split rule and all behavior/source/evidence constraints remain in force. This overrides only the per-task stop/wait-for-continue steps for this run. Starting T03-n2-c from eb1bcb4f1e3fa7bed00fe0b54a5df11e578d0135.


### 2026-11-08 — T03-n2-c implementation; exact-commit CI pending

Removed MIGRATION_1_2 through MIGRATION_10_11 and their historical-only row-preservation tests. Kept MIGRATION_11_12 unchanged. Both persistent LocalStore builders now use that migration and Room's all-table destructive reset explicitly for development versions 1–10; current databases are not reset. The new reset test opens every exported schema 1–11, verifies development exports/history are discarded, then writes current progress and verifies it survives reopen. The existing real version-9 reset/review-credit check remains. Exported schema files are unchanged.

Retired only the migration-specific method/helper and source-string expectation from PracticeRetrievalDigestTest, ReauditCorrectionTest and V2RebuildTest; current retrieval, scoring, accepted order, backward/equal-clock writes, backup, named reopen and onboarding checks remain. Refreshed only the unfrozen NOT_APPROVED candidate's current sourceDigest; preserved candidates, source/rights records, CI and test configuration are unchanged. Targeted development checks passed (BUILD SUCCESSFUL in 2m 25s); exact-commit CI is still required. No later task was started.


### 2026-11-08 — T03-n2-c exact-commit CI and closure (continuous run: task 1)

Implementation **8bf4ab0bcb34a5d76918868380ef3b3b87b43883** passed [CI 42452347097](https://github.com/justinkulka-lang/russian-companion/actions/runs/42452347097), first attempt, push event; run head and checkout log match. The unchanged full compile/lint/gate/unit-test command passed, BUILD SUCCESSFUL in **1h 18m 59s** (verification step 05:51:34–07:10:33 UTC; job 05:50:49–07:10:36 UTC). Uploaded JUnit reports: **1,203 tests, 0 failures/errors, 1 existing AcceptanceBoundaryTest#at020_physicalRebootPersistsGovernedRecords skip, 237 suites**. DatabaseMigrationTest, CumulativeReviewStorageTest, PracticeRetrievalDigestTest, ReauditCorrectionTest and V2RebuildTest all passed. Local development smoke was 40 tests, 0 failures/errors/skips.

The seven-test net reduction is authorized retirement of old row-preservation tests: seven DatabaseMigrationTest methods and PracticeRetrievalDigestTest#migrationMarksExistingAttemptIdentityUnrecoverable were removed, and a current reset/reopen test was added. The mixed accepted-order test retains all current checks; only its retired migration helper was removed. V2RebuildTest keeps onboarding and drops only the old migration-name source assertion. No current behavior check, source/rights record, test exclusion/skip or CI configuration was removed/changed. No evidence folder was created. Parts a, b and c now jointly satisfy T03-n2, so the parent is also DONE on this combined passing tree; this is one implementation task in the continuous-run count. Next: **VERIFY-CURSOR-2-n1 — CODEX**. No stop point has been reached; continue after pushing this closure.


### 2026-11-08 — VERIFY-CURSOR-2-n1 implementation; exact-commit CI pending

Restored Unit4PresentTeachingTest's ordinary-instruction gate scenario using the current required present TEACH steps, with no old-pack comparison. Correct current answers demonstrate the objective while ordinary requirements report missing instruction; completing that instruction clears ordinary requirements, while the separately assigned cumulative review still blocks advancement. Restored the current Unit15 overlapping-English-aspect-label negative validator case in Unit15AspectPreparationTest, requiring an item-specific response-type/explicit-target rejection without throwing. These are additions only; no current checks were removed or weakened. Current curriculum, runtime code, source/rights records, candidate identity, CI and test configuration are unchanged. Both test classes passed local development smoke, BUILD SUCCESSFUL in 52s. Full exact-commit CI is required before closure. Continuous run task 2; no later task started.


### 2026-11-08 — VERIFY-CURSOR-2-n1 exact-commit CI and closure (continuous run: task 2)

Implementation **6cc88cb6542893ec1d1765bcdc788a27a0eb6846** passed [CI 42457904737](https://github.com/justinkulka-lang/russian-companion/actions/runs/42457904737), first push attempt; run head and checkout log match. The unchanged full compile/lint/gate/unit-test command passed: BUILD SUCCESSFUL in **46m 40s** (verification step 07:14:35–08:01:17 UTC, job 07:13:39–08:01:20 UTC). Uploaded engineering-test-reports artifact 13608774326 contains **1,205 tests, 0 failures/errors, 1 existing physical-reboot skip, 237 suites**. Both added methods passed: Unit4PresentTeachingTest#correctTargetsCannotWaiveCurrentRequiredPresentTeaching and Unit15AspectPreparationTest#overlappingEnglishLabelsRejectWithoutThrowingAndDoNotBecomeNativeEvidence. Local smoke: 6 tests passed, no skips. No tests/assertions were removed, and no exclusions, skips, historical fixtures, source/rights edits, runtime changes or evidence folder were introduced. Finding VERIFY-CURSOR-2-n1 is closed. Next: **VERIFY-CURSOR-2-n2 — CODEX**. Two implementation tasks completed; no stop point reached. Continue after pushing this closure.


### 2026-11-08 — VERIFY-CURSOR-2-n2 implementation; exact-commit CI pending

Removed SourceTextPoolTest's optional source.pool.baseline whole-pack equality contract; current Python/Kotlin expansion equality, digest identity, strict decoding and all corrupt/missing/conflicting/unbound-pool rejection checks remain. Units2630StructuralMappingTest now renders worksheets in memory for current page-range and semantic checks; removed only historical disk-equality/packet existence/content checks and the WRITE_STRUCTURAL_MAPPING export switch. Current mapping facts/digests, unsigned/pending status, empty reviewer columns, source/rights, validator, release and curriculum-origin checks remain. Removed only historical per-unit packet assertions from V2RebuildTest and Units2125SourceIntakeTest. No test method was deleted. No source/rights file, historical packet, runtime source, candidate, workflow, filter, exclusion or skip changed; no evidence folder was created. Targeted development checks passed, BUILD SUCCESSFUL in 3m 50s; full exact-commit CI is required. Continuous run task 3; no later task started.


### 2026-11-08 — VERIFY-CURSOR-2-n2 exact-commit CI and continuous-run stop (task 3)

Implementation **968a90508f77f31b3627eaa70c18f0a5551887df** passed [CI 42461548472](https://github.com/justinkulka-lang/russian-companion/actions/runs/42461548472), first push attempt; run head and checkout log match. The unchanged full compile/lint/gate/unit-test command passed, BUILD SUCCESSFUL in **1h 1m 36s** (verification step 08:07:49–09:09:27 UTC, job 08:07:11–09:09:32 UTC). All required workflow steps succeeded. The engineering-test-reports artifact is retained by CI as 13611123054 for this exact head. The executor was unavailable when CI completed, so its JUnit archive was not locally downloaded or counted; no predicted totals are claimed. Local development smoke had **23 tests, 0 failures/errors/skips**, BUILD SUCCESSFUL in 3m 50s.

No test method was deleted. Current pool-expansion/corruption checks, current mapping facts/source/rights, validator/release/origin checks and in-memory worksheet semantics remain. Only the optional whole-pack baseline equality, historical per-unit packet existence/status/content/disk equality requirements, and WRITE_STRUCTURAL_MAPPING export switch were retired. Source/rights records, runtime sources, candidate identities, workflows, exclusions and skips are unchanged. No evidence folder or Russian content was generated. VERIFY-CURSOR-2-n2 is DONE.

**Continuous-mode stop: T05 is BLOCKED by the execution environment.** The selected executor became unavailable during task 3's CI. Repeated exec_command probes return "No usable execution environment is available." The environment status connector later reports running/connected, but commands still cannot access the checkout. All task 3 implementation changes were already committed and pushed before the outage; no uncommitted implementation work was left. No T05 implementation or split was started. Restore a usable attached execution environment, fetch the authoritative remote branch (including this queue closure), confirm ancestry and resume T05 after clearing its operational BLOCKED status.

Every Git push in this run used the required fetch into the branch's remote-tracking ref and ancestry confirmation. Because the executor cannot perform Git commands for this final queue-only closure, it is published through GitHub's commit/ref API instead: fetch the current remote ref, create a child commit with that exact head as its sole parent, recheck the remote ref, then advance it with force=false and expected_sha set to the fetched head. This preserves the authoritative remote and permits only a fast-forward; no force push or merge is used. WORK_QUEUE.md is the only changed path, so its existing CI push filter applies.

Completed implementation tasks in this run:
- T03-n2-c: **8bf4ab0bcb34a5d76918868380ef3b3b87b43883**, CI **42452347097**, closure **22fb3baa6b59943f17367c5e6f341ac83fd60878**. Parent T03-n2 closed on the combined passing tree; this is not an additional implementation task.
- VERIFY-CURSOR-2-n1: **6cc88cb6542893ec1d1765bcdc788a27a0eb6846**, CI **42457904737**, closure **be21f3601a935c559672e0612fc2c6598357a4bf**.
- VERIFY-CURSOR-2-n2: **968a90508f77f31b3627eaa70c18f0a5551887df**, CI **42461548472**, closure is this commit.

Stopped after three completed tasks at the operational blocker, below the five-task cap. Next task: **T05 — CODEX — BLOCKED (executor required)**. No owner curriculum/device/listen-check approval is requested.


### 2026-11-08 — Owner resumes continuous mode in restored executor

Fetched and reset to authoritative remote d0674b3b2eafc1f82b2cccb6ba5181a3753687fe. Commands work in the restored executor. T05 operational blocker cleared and status returned to TODO. Owner authorizes up to five consecutive completed tasks with exact-commit CI per task, stopping at the next STOP-OWNER/STOP-CLAUDE, BLOCKED task, CI failure after one fix attempt, completion of T04-n1/T08/T12/T14/T15–T19, low session capacity or environment loss. At a stop, push available work and record exactly where the run stopped. No previous-run task counts carry forward. Three-part/no-recursive-split and all behavior/source/evidence constraints remain unchanged.


### 2026-11-08 — T05-a implementation; exact-commit CI pending

T05 is split into exactly three non-recursive parts: a (shared evaluator and Units 4/6/8/9/13), b (Units 17/20/21/24/28), c (Units 29/30 and final original-criteria check). Added serializable pack contentChecks and a shared evaluator for exact source identity/hash/text/license/transformation, objective kind/gating/check and guided lists, and item role/feature/prompt/context/structure constraints. Existing supplied text/IDs/source bindings are copied into catalog/content-checks.json and the bundled pack; the generator reads that data. No Russian text was authored. Removed the five corresponding Kotlin Unit*Pins files and routed their existing validator sites to the shared evaluator. Content checks are bound into the pack content-review digest; unit instructional digests remain unchanged, matching the prior separation between validator code and unit content.

Added mutation tests for missing/duplicate rule groups, bound source fields and duplicate sources, objective kind/gating/check/guided lists, item roles/features/prompts and optional reference assessments. Current teaching/transfer/layout checks remain unchanged. The source guard's catalog removes only the five deleted Kotlin files, with its exact cardinalities updated from 19 files/292 bindings to 14/280; all remaining literal bindings, rights/pending status and offender checks remain. Refreshed only the unfrozen NOT_APPROVED candidate's current sourceDigest; preserved candidates and CI/test configuration are unchanged. The first development smoke found the stale source-guard file-count expectation, corrected before CI; the completed current smoke passed, BUILD SUCCESSFUL in 4m 38s. No evidence folder, historical package regeneration, behavior deletion, exclusion or skip was introduced. Full exact-commit CI is required before DONE. This is task 1 in the resumed continuous run; b/c and later tasks remain unstarted.


### 2026-11-08 — T05-a exact-commit CI and capacity stop (resumed continuous run: task 1)

Implementation **f5581b60403f4455114db3bcfec8355a7e18a0e2** passed [CI 42482686807](https://github.com/justinkulka-lang/russian-companion/actions/runs/42482686807), first push attempt. Run head and checkout log match that exact commit. The unchanged committed command `./gradlew compileDebugSources lintDebug verifyReleaseGateAttachment testDebugUnitTest --continue --stacktrace` passed all required checks: BUILD SUCCESSFUL in **1h 21m 11s**; verification step **12:58:55–14:20:07 UTC (1h 21m 12s)**, job **12:58:11–14:20:10 UTC (1h 21m 59s)**. Read the uploaded engineering-test-reports artifact **13620478831**: **1,208 tests, 0 failures, 0 errors, 1 existing AcceptanceBoundaryTest#at020_physicalRebootPersistsGovernedRecords skip, 238 suites**. ContentChecksTest's three mutation/data-contract methods and all five ReviewedCurriculumSourceGuardTest methods passed without skips. Local current smoke: **50 tests, 0 failures/errors/skips**.

T05-a meets its Done when: the five Unit4/6/8/9/13 Kotlin pin files are removed; their source, objective, item and context constraints are now pack data evaluated by shared code, with negative tests. The generator consumes the same catalog data and the pack content-review digest covers it. No current test method/assertion was deleted, and no CI exclusion/skip/filter was added. The only existing test expectation changes are the source catalog's exact remaining-file/binding cardinalities after deleting those five Kotlin files. Licensed text and source/rights records are unchanged; the data carries existing text, with nothing authored in Russian. Candidate remains unfrozen and NOT_APPROVED, with preserved candidate identities untouched. No historical packet, governance bundle or evidence folder was generated.

**Continuous-mode stop under owner condition 5: low session capacity, after completing and pushing T05-a.** The implementation was pushed before adding this stop Log entry; this queue-only closure is also committed/pushed after a fresh fetch and ancestry confirmation. Exactly **one implementation task completed in this resumed run**, with no failed exact-commit CI or fix attempt. The executor is working and there is no operational blocker. **Stopped precisely before T05-b — CODEX — TODO**; its Unit17/20/21/24/28 conversion has not been started. T05-c (Unit29/30 and final original-criteria check) also remains TODO, and parent T05 remains TODO pending both parts. The three-part split is complete as planning; no part may split again. No task is left IN_PROGRESS and no uncommitted implementation work remains. Resume with T05-b from the authoritative remote head. No owner curriculum, device, listen-check or approval action is needed.


### 2026-11-08 — T05-b resumed continuous run

Fetched the authoritative branch and confirmed ancestry at d4d016d93b0808058d107c39c5d941d0ebc8d9cf. T05-b is the first task in this resumed run under the standing five-task/checkpoint/blocker/capacity stop rules. It is already a part and will not split again. T05-c and later tasks remain unstarted.


### 2026-11-08 — T05-b implementation; exact-commit CI pending

Moved Unit17 question, Unit20 directionality, Unit21 object, Unit24 context and Unit28 timing constraints into catalog/content-checks.json and the bundled pack; removed all five Kotlin pin files. Existing supplied Native-row values were checked against the current provenance records before copying. Shared evaluation now supports raw-text containment separately from unfolded wiki labels, attribution/no-audio restrictions, declared source/rights requirements, required guided membership separately from exact guided lists, and exact cue/answer/options plus distinct-prompt constraints. The Unit21 standalone source-admission guard now uses the same data-driven source matcher. Existing direct test calls in Aud44CorrectionTest and BatchBUnits2125Test were ported to shared evaluation with every assertion retained. Unit24's unused helper was removed with its file; its used constraints remain.

Expanded current mutation coverage for attribution/audio, declared source/rights/license/permission removal or changes, literal cues/answers/options, multiple prompt contrasts, matching modes and guided membership without narrowing it to an exact list. Source guard now catalogs 9 remaining Kotlin comparison files/249 bindings/26 item-ID bindings/0 research-cue bindings, reflecting only deletion of the five Kotlin files. The two former Unit28 research-cue bindings moved out of Kotlin into data; underlying provenance and pending-review status remain unchanged, and semanticAcceptance/rightsAcceptance remain false. Current source/rights records and text are unchanged, with no authored Russian. Only the unfrozen NOT_APPROVED candidate's sourceDigest was refreshed; preserved candidates, CI, exclusions and skips are unchanged.

Development checks: the completed 62-test affected-unit/evaluator/source-guard run had one stale fixture-count failure (expected 2 research bindings, actual 0), corrected before CI. All other tests passed. The corrected evaluator/source-guard run, including the new membership test, passed all 11 tests (0 failures/errors/skips), BUILD SUCCESSFUL in 44s. Full exact-commit CI is required before DONE. No test method/current behavior assertion was deleted, no evidence folder was created, no historical packet was regenerated, and no part was split again. T05-c and later tasks remain unstarted.


### 2026-11-08 — T05-b exact-commit CI and closure (continuous run: task 1)

Implementation **caad05d29127b6200b22734fc6be21ff993c5a34** passed [CI 42494147076](https://github.com/justinkulka-lang/russian-companion/actions/runs/42494147076), first push attempt; run head and checkout log match. The unchanged full compile/lint/release-gate/unit-test command passed: BUILD SUCCESSFUL in **1h 20m 33s** (verification step **15:19:25–16:39:58 UTC**, job **15:18:40–16:40:02 UTC**). Uploaded engineering-test-reports artifact **13624664106** contains **1,211 tests, 0 failures/errors, 1 existing AcceptanceBoundaryTest#at020_physicalRebootPersistsGovernedRecords skip, 238 suites**. All six ContentChecksTest methods and all five source-guard methods passed without skips. Existing affected unit/Aud44/BatchB assertions remain and pass; no test method was deleted. All native source identity/hash/text, rights/attribution/no-audio, context, item-role/feature/cue/answer/options and exact-list versus guided-membership requirements remain enforced from data. Current source/rights records, review-pending flags, CI configuration/exclusions/skips and preserved candidate identities are unchanged; no evidence folder or Russian content was generated.

**T05-b DONE.** Exactly one implementation task is completed in this resumed continuous run. Parent T05 remains open. Next eligible task: **T05-c — CODEX**. No stop condition has been reached; continue after pushing this closure. T05-c and later tasks have not been started yet.


### 2026-11-08 — T05-c implementation; exact-commit CI pending

Removed Unit29AdjectiveNativePins, Unit29PunctuationPins and Unit30LanguagePins after moving every Native tuple into pack/catalog data and checking its supplied values against existing provenance. The shared data now has 14 groups: the last four cover adjective applications, punctuation, letter names and abbreviation treatment. Kept the adjective source-only predicate distinct from its caller's exact check/guided IDs, item-feature, context and structure checks; those lookups now read shared data. Kept Unit30's two objective-specific checks independent, including its supplied-objective argument. Kept punctuation's exact old/guided cue, answer and applicable options checks, guided membership, new-item features/context, new-source rights and standalone native admission. Existing source/claim routing and the global Russian-Wikipedia copyright/audio/license guard remain in V2Validator.

All Unit*Pins.kt files are now absent, with no references to the retired classes. The generator consumes the same catalog data, shared negative tests cover every group's source/objective/item constraints, and the pack review digest binds the contracts. Added coverage for no Unit*Pins.kt files, source-only versus item checks, missing lookups, independent language objectives and supplied-objective validation, plus the separate punctuation native-admission/global license-route responsibilities. Unit21 attribution/audio rejection assertions remain; punctuation's standalone matcher retains its prior scope while global validation rejects those changes. No current test method or behavior assertion was deleted. Source-forgery coverage now selects an actual provenance binding rather than assuming the first catalog entry is one; all forgery/removal/catalog assertions remain. The exact remaining source-catalog counts are 6 files/189 bindings/26 item-ID bindings, with both acceptance flags still false.

Development checks: completed 33-test run had two test-fixture assumptions exposed by the new groups/catalog ordering (rights test assuming every standalone source matcher also owns the global attribution/audio checks, and first-entry provenance selection). The affected Unit29/30 checks passed. Corrected fixtures and added explicit semantic coverage passed all **14 evaluator/source-guard tests, 0 failures/errors/skips**, BUILD SUCCESSFUL in 47s. Refreshed only the unfrozen NOT_APPROVED candidate's current sourceDigest; preserved candidates, source/rights records, text, CI configuration, filters/exclusions/skips and pending-review status are unchanged. No Russian was authored and no evidence folder or historical packet was generated. Full exact-commit CI is required before T05-c or parent T05 can close. This is task 2 in the continuous run; later tasks remain unstarted.


### 2026-11-08 — T05-c exact-commit CI, parent closure and capacity stop (continuous run: task 2)

Implementation **8b80beaf8bfba0f54f0467be951f656a55b2fe3a** passed [CI 42501717654](https://github.com/justinkulka-lang/russian-companion/actions/runs/42501717654), first push attempt; run head and checkout log match. The unchanged full compile/lint/release-gate/unit-test command passed: BUILD SUCCESSFUL in **1h 3m 49s** (verification step **16:48:47–17:52:37 UTC**, job **16:48:03–17:52:41 UTC**). Read engineering-test-reports artifact **13627109136**: **1,214 tests, 0 failures/errors, 1 existing AcceptanceBoundaryTest#at020_physicalRebootPersistsGovernedRecords skip, 238 suites**. All nine ContentChecksTest methods and all five source-guard methods passed without skips. Current Unit29/30 UI, context, source and advancement tests pass in this full suite. Corrected local evaluator/source-guard run: 14 tests passed, no failures/errors/skips.

**T05-c and parent T05 DONE.** Original Done when verified on the combined passing tree: no Unit*Pins.kt files remain under app/src/main; all 13 retired files' source identity/hash/text/license/transformation, attribution/audio/rights, objective kind/gating/check/guided, item role/feature/cue/answer/options, source-only and context/structure rules are enforced through shared pack data and the retained validator call sites. No current test method was removed; existing unit behavior assertions remain, with test-call/fixture selections updated for the shared API and retired source files. The additional negative coverage distinguishes the former source-only predicate, independent language objectives, supplied-objective validation, and standalone punctuation admission from its still-required global license route. All three non-recursive parts are complete. Current source/rights records and text, pending-review status, exclusions/skips/CI, candidate approval/freeze flags and preserved candidate identities remain unchanged. No Russian was authored, and no evidence folder or historical packet was generated.

**Continuous-mode stop under owner condition 5: low session capacity, after completing and pushing T05-b and T05-c.** Both implementation commits were pushed before adding this stop Log entry; this queue-only closure is also committed/pushed after a fresh fetch and ancestry confirmation. Exactly **two implementation tasks** completed in this resumed run; parent T05 closes on the same combined tree and is not a separate implementation task. No exact-commit CI failed and no CI fix attempt was needed. The executor is working; there is no operational blocker. **Stopped precisely before T06 — CODEX — TODO** (CC018 safe insets, CC019 TalkBack labels, CC040 state saving, CC041 main-thread work). No T06 implementation or split was started, no task is left IN_PROGRESS, and no uncommitted implementation work remains. Resume with T06 from the authoritative remote head. No owner approval, curriculum, device or listen-check action is needed.

Completed tasks in this run:
- T05-b: **caad05d29127b6200b22734fc6be21ff993c5a34**, CI **42494147076**, queue closure **199a58bc26b312b646639b8f794bb9e5013aa402**.
- T05-c: **8b80beaf8bfba0f54f0467be951f656a55b2fe3a**, CI **42501717654**, queue closure is this commit; parent T05 is also DONE here.


### 2026-11-08 — T06 start under continuous mode

Fetched the authoritative branch and confirmed ancestry at 3dc8d1ae07bd56ecd26e236c5ef562b817a91a6f. T06 is task 1 of this resumed continuous run, under the standing stop/checkpoint/five-task/capacity rules. Existing safe-inset, labelled-control, onboarding/session restoration and worker-thread fixes will be verified rather than repeated; remaining transient screen-state gaps are in scope. No later task has started.


### 2026-11-08 — T06 implementation; exact-commit CI pending

Checked all four finding areas on current source. CC018 already uses safeDrawing union IME insets once at both scrolling/lazy scaffold roots, zero child-bar insets and consumed content padding; existing V2InsetsAccessibilityTest exercises system bars, side cutouts, IME opening/closing, footer and navigation geometry. CC019 already has merged labelled checkbox/radio/selectable actions for onboarding, lesson options and practice; existing tests check role, selected/checked state, disabled actions and no duplicate submission. Added the missing selected-state semantics on interactive Alphabet Lab letter tiles, whose selection previously changed only color. No semantics are added to noninteractive tiles.

CC040 already saves onboarding, session/practice choices and note drafts, with navigation/coordinator recreation coverage. Fixed three remaining screen-state gaps: Today's expanded plan uses rememberSaveable keyed by unit number; Alphabet Lab selection and Reference query use rememberSaveable keyed by pack identity/version. Added ScreenStateRestorationTest for each saved-instance-state restoration, selection semantics, unit-change reset, no automatic callbacks and unchanged stored learner records. No process-death, physical-device or TalkBack acceptance is claimed. CC041 already loads persistence off main, computes governed snapshots/attempt identities on workers, and keeps pending writes/reconstruction in the activity coordinator; existing ReauditCorrectionTest#r10, FullAuditCorrectionTest#f11 tests and HostActivityLifecycleTest recreation checks cover responsiveness, worker threads and retained outcomes. Those fixes were verified rather than reimplemented.

The first targeted development run passed **20 tests**, BUILD SUCCESSFUL in **2m 18s**, including new state tests, V2InsetsAccessibilityTest, OnboardingStateRestorationTest, OptionPresentationUiTest, the r10/f11 worker checks and current sourceDigest validation. Its HostActivityLifecycleTest filename-based filter matched no recreation tests because their class is HostActivityRecreationTest. Corrected that filter and ran both recreation methods separately: **2 tests passed, 0 failures/errors/skips**, BUILD SUCCESSFUL in **53s**. Refreshed only the unfrozen NOT_APPROVED candidate's current sourceDigest; preserved candidates, curriculum, source/rights records and CI/test configuration are unchanged. No current test/behavior assertion was deleted, no exclusion/skip was added, no Russian was generated, and no evidence folder was created. Full exact-commit CI is required before DONE. T06 was not split; T07 and later tasks remain unstarted.


### 2026-11-08 — T06 local-test scope correction; CI still pending

Corrected the preceding local-smoke account: the first 20-test run did not include activity recreation because the filename and class name differ. Ran HostActivityRecreationTest#recreatedActivityKeepsTheDraftAnswerAndResumePosition and #recreatedActivityDuringADelayedWriteKeepsOneOutcome with the actual class name; both passed. No implementation/test code changed for this correction. Exact-commit CI 42519460569 remains running at implementation 436d3f7295f05285b33bccb22ae8f539dfdbdcad; this queue-only correction must not start a duplicate run. T06 remains IN_PROGRESS.


### 2026-11-08 — T06 exact-commit CI and closure (continuous run: task 1)

Implementation **436d3f7295f05285b33bccb22ae8f539dfdbdcad** passed [CI 42519460569](https://github.com/justinkulka-lang/russian-companion/actions/runs/42519460569), first push attempt; run head and checkout log match. The unchanged committed compile/lint/release-gate/unit-test command passed: BUILD SUCCESSFUL in **1h 21m 48s** (verification **20:19:48–21:41:37 UTC**, job **20:18:42–21:41:42 UTC**). Read engineering-test-reports artifact **13635199207**: **1,217 tests, 0 failures/errors, 1 existing AcceptanceBoundaryTest#at020_physicalRebootPersistsGovernedRecords skip, 239 suites**. All three new ScreenStateRestorationTest methods, all seven V2InsetsAccessibilityTest methods and all three HostActivityRecreationTest methods passed without skips. Existing onboarding/option restoration, r10/f11 worker/response and other current behavior checks also passed in the full suite. Local smoke was 20 passing tests plus the corrected separate two-method activity recreation run; the filter/name correction is documented above.

Closed CC018 through existing once-only safeDrawing/IME inset behavior and geometry coverage; CC019 through existing labelled single-action options plus newly exposed alphabet selection semantics; CC040 through existing onboarding/session/draft restoration plus saved Today expansion, alphabet choice and Reference query (including no automatic action/storage mutation and unit-change reset); CC041 through the existing tested worker-side reads/identity/preparation/snapshots and responsive activity coordinator. No current check was deleted or weakened; no exclusions/skips/CI changes, source/rights/curriculum changes, generated Russian or evidence folder were introduced. The candidate stays unfrozen and NOT_APPROVED, with preserved candidates untouched.

**T06 DONE.** One implementation task completed in this resumed continuous run. Next eligible task: **T07 — CODEX**. No stop condition reached; continue after pushing this closure. T07 and later tasks remain unstarted at this point.


### 2026-11-08 — T07-a implementation; exact-commit CI pending

Split T07 into two non-recursive parts: a (CC043 current review copy plus verification of already-fixed CC042), b (A2-15 non-runtime provenance and original task closure). Added reviewCopy pack data containing 28 current titles, 28 introductions and 18 shared messages/templates, with catalog/review-copy.json as generator input. Copied only the English strings selected by cumulativeReviewPolicy for each current unit; retired unsupported older-policy title/introduction branches. Review panel, unit-entry reminder/button, Continue target and cumulative-review requirement messages now read the pack copy. Policy/assignment IDs, digests, pools, schema, eligibility, repair and advancement behavior are unchanged. Formatting preserves the existing unit/count/feedback values. The pack validator requires complete nonblank unit/message coverage and valid known template tokens, and the pack content-review digest binds the copy. Unit instructional digests remain unchanged, matching the prior separation of hard-coded review UI from unit content.

Added ReviewCopyTest for catalog/pack agreement, review-digest binding, formatting, malformed/missing/unknown copy rejection, and UI rendering from changed English pack copy with the same repair callback and no implicit assignment/store mutation. Existing review UI/behavior checks remain. Updated the English-only RequiredPathProgressTest fixture to supply its explicit review messages; all original assertions remain. Refreshed only the unfrozen NOT_APPROVED candidate's current sourceDigest and the two changed source-guard file hashes; binding sets, source/rights data, preserved candidates and review-pending flags are unchanged. No Russian text was authored or generated, no current test/assertion was deleted, and no evidence folder or historical packet was created.

CC042 already has distributionSha256Sum pinned for Gradle 9.6.0 and uses android-actions/setup-android with accept-android-sdk-licenses=true before SDK package installation; there is no yes/pipefail license pipeline. Verified the official https://services.gradle.org/distributions/gradle-9.6.0-bin.zip.sha256 response matches committed **bbaeb2fef8710818cf0e261201dab964c572f92b942812df0c3620d62a529a01**. Exact-commit CI 42519460569 has successful SDK setup and package-install steps using the committed configuration; the T07-a run will verify it again. No wrapper/workflow changes or new filters/exclusions/skips were needed.

Development checks: **24 copy/current-review/source-guard/sourceDigest tests passed**, BUILD SUCCESSFUL in **4m 41s**. The explicit English scheduling fixture also passed separately (BUILD SUCCESSFUL in 32s). Full exact-commit CI is required before DONE. This is task 2 of the continuous run. T07-b, T04-n1 and later tasks remain unstarted; T07 parent remains open for provenance work.

### 2026-11-08 — Continuous run stopped after executor restart

Stopped at **T07-a IN_PROGRESS**, implementation **127f81b369b50f82062c0775b5407aa7a967abb5**, already pushed. The executor restarted and lost the active CI-monitor cell. The recovered checkout is clean at that implementation; fetching the authoritative remote confirmed the same head and ancestry. At **22:11 UTC**, exact-commit [CI 42527432682](https://github.com/justinkulka-lang/russian-companion/actions/runs/42527432682) remains **in_progress**, with run head matching the implementation. No CI result is claimed and T07-a is not DONE. No fix attempt was made; no failure has been reported at this stop. T06 is the only completed task in this run (436d3f7295f05285b33bccb22ae8f539dfdbdcad; passing CI 42519460569).

Owner continuous-mode stop condition 5 (environment loss) applies. This queue-only stop record is pushed; no further implementation is started. On resume, inspect the existing T07-a exact-commit CI result, handle any failure within the owner's one-fix-attempt rule, and record its result before closing T07-a or starting **T07-b (CODEX)**. T07 parent remains open; T07-b, T04-n1 and later tasks are unstarted. No new evidence folder, exclusion, skip or Russian text was added.

### 2026-11-08 — T07-a exact-commit CI and closure (resumed continuous run: task 1)

Resumed from the pushed environment-loss stop record at 4c7a69b1. Existing exact-commit [CI 42527432682](https://github.com/justinkulka-lang/russian-companion/actions/runs/42527432682) **PASSED** on implementation **127f81b369b50f82062c0775b5407aa7a967abb5**, first attempt; run head and checkout log agree. The unchanged compile/lint/release-gate/full-unit-test command finished **BUILD SUCCESSFUL in 1h 4m 1s**, 21:54:05–22:58:07 UTC. SDK setup and package installation also passed. Read artifact 13638107173: **1,220 tests, 0 failures/errors, 1 existing physical-reboot skip, 240 suites**. ReviewCopyTest (3) and RequiredPathProgressTest (10) all passed without skips. No suite rerun or fix attempt was needed.

T07-a closes the review-copy portion of CC043 and verification of the already-fixed CC042. T07 remains open for A2-15 provenance work in **T07-b (CODEX)**. This queue-only closure skips duplicate CI by the existing path filter; no behavior check, exclusion, skip, source/rights record or preserved candidate was changed by closure. The environment is available after the earlier restart; the owner has said Continue, so the resumed continuous run proceeds to T07-b after pushing this result.

### 2026-11-08 — T07-b non-runtime provenance separation

Runtime-use review found that source identities, spans/hashes, shipped text, transformations, license/attribution/share-alike data, synthesis applications and review status are read by startup validation, content-review digests or offline attribution. These remain bundled: removing them would remove current source/rights checks. The four unused per-provenance administrative fields (reviewerIdentity, reviewerQualification, reviewDate, reviewedDigest) moved to **catalog/provenance-review-metadata.json**, keyed by unchanged itemId. All 93 explicitly present sets were empty. All **7,036** provenance rows and every source/rights record remain; runtime review status and approval flags are unchanged. No claim is made that the entire provenance dataset is unused or has moved out of the app.

A direct rejoin of the moved fields reproduced the pre-task JSON document exactly. The asset shrank from **20,448,437 to 20,440,532 bytes**; no instructional text or Russian was authored, generated or changed. The shared authoring projection preserves metadata outside a generator's subset and rejects conflicting data/duplicate identities. The base curriculum generator and content-audit writer apply it before saving, preventing reintroduction of unused fields. Catalog documentation explains the runtime boundary and the non-bundled authoring record; it is not an audit bundle or acceptance evidence. Runtime models retain their existing optional defaults, so current behavior and source/rights tests need no weakening or removal.

Added coverage for lossless separation, repeated projection and conflict/identity rejection on English fixtures, with the Python checks invoked from the normal unit suite. Added typed-pack, validator, pack/all-unit review-digest and offline-notice equivalence checks after rejoining the real preserved metadata. Only the unfrozen NOT_APPROVED candidate's sourceDigest was refreshed; preserved candidates, Kotlin source guards and source/rights bindings are unchanged. Full exact-commit CI is required before DONE. T07 parent remains open until this part passes. No evidence folder, behavior-check deletion, CI exclusion or skip was added.

Development checks before push: the first smoke passed **15 tests** (ProvenanceMetadataTest 2, SourceTextPoolTest 3, Units35SourceIntakeTest 5, ReviewedCurriculumSourceGuardTest 5), BUILD SUCCESSFUL in **1m 40s**. The initial union included unmatched notice/sourceDigest name filters; verified the XML class list and ran the correctly named ReferenceNoticeCorrectionTest and ReleaseSourceDigestTest separately. Their result is recorded below; no unmatched filter is claimed as test coverage. The three English Python projection tests also passed and run inside ProvenanceMetadataTest in CI.

Separate notice/digest smoke: **15 tests passed** (ReferenceNoticeCorrectionTest 1, ReleaseSourceDigestTest 14), 0 failures/errors/skips, BUILD SUCCESSFUL in **51s**. Current candidate sourceDigest is **bc4c3590f5a589e3899a6ccb4fe4455165e4af96aa1da10b6a6b06a3139aa9c3**; it remains unfrozen and NOT_APPROVED.

### 2026-11-09 — T07-b exact-commit CI and T07 closure (continuous run: task 2)

Implementation **92f5ea01e0bfa689898cb5e137d6ba66a8a5b0e3** passed [CI 42533483695](https://github.com/justinkulka-lang/russian-companion/actions/runs/42533483695), first attempt; run head and checkout log match. The unchanged compile/lint/release-gate/full-unit-test command finished **BUILD SUCCESSFUL in 1h 20m 3s**, **2026-11-08 23:07:01–2026-11-09 00:27:05 UTC**. Read exact-commit report artifact **13640669609**: **1,222 tests, 0 failures/errors, 1 existing AcceptanceBoundaryTest#at020_physicalRebootPersistsGovernedRecords skip, 241 suites**. ProvenanceMetadataTest (2, including the three English Python checks), SourceTextPoolTest (3), Units35SourceIntakeTest (5), ReviewedCurriculumSourceGuardTest (5), ReleaseSourceDigestTest (14) and ReferenceNoticeCorrectionTest (1) all passed without skips. No CI fix attempt or rerun was needed.

**T07-b DONE; T07 DONE.** Combined parts satisfy original T07: pack-driven current review copy (CC043), verified committed wrapper checksum/SDK-license setup (CC042), and A2-15 separation of unused provenance review administration while retaining all fields read by runtime validation/digests/offline notices. All 7,036 provenance rows, source/rights records and current behavior checks remain. No generated Russian, new evidence folder, behavior-check deletion, exclusion or skip was introduced. Candidate remains unfrozen and NOT_APPROVED; no review or acceptance stage is inferred from CI.

### 2026-11-09 — Continuous run stopped after second executor restart

Stopped **after completing T07-b and its T07 parent**, before starting **T04-n1 (CODEX)**. The executor restarted while T07-b's pushed exact-commit CI was being monitored. On recovery, the clean checkout and freshly fetched authoritative remote both matched 92f5ea01e0bfa689898cb5e137d6ba66a8a5b0e3; existing CI had passed, so the task was finished rather than left pending. This closure and stop record are pushed as a queue-only update, with ancestry fetched and checked before push. Owner continuous-mode stop condition 5 (environment loss) applies. No task is left IN_PROGRESS by this run.

Completed in this resumed run: **T07-a** (127f81b369b50f82062c0775b5407aa7a967abb5; passing CI 42527432682), **T07-b** and **T07** (92f5ea01e0bfa689898cb5e137d6ba66a8a5b0e3; passing CI 42533483695). T04-n1 and later implementation tasks remain unstarted; T04-n1 is the next eligible task and is an owner review checkpoint when finished. Resume there on Continue.
