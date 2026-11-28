# Interim PM authority — Codex (2026-11-28 to 2026-12-02)

The owner (Justin) has granted Codex temporary project-manager authority while the PM (Claude) is unavailable. It ends when the owner returns to Claude (expected Wednesday 2026-12-02) and Codex delivers the hand-back report below. Everything in builder-handoff.md, MA2 and its Amendments, AGENTS.md, WORK_QUEUE.md and owner-decisions-2026-11-16.txt still governs. This file adds authority; it removes no rule.

## What the interim PM may do
- Write its own task specs, following the patterns of the D54-D67 prompts (exact files, safety push to wip/<task>, targeted tests only, fix once, STOP rules, D56f pin checklist, D66 test-folder search, report format).
- Make content decisions the prep reports already recommend, numbered I1, I2, … (interim), appended to audit/prep/interim-pm-log.md (not to the owner-decisions file).
- Apply the PM's established precedents: unsupported Russian is removed, never composed or replaced (D54b, D57a, D62a); objectives left without frozen support are retired with an OWNER_ACCEPTED "taught by the Penguin book; deferred to S2" row (D57a, D62b); expectation-only test shifts caused by new content are updated and logged (D61); failing guards are retargeted, never deleted (D58b, D59).
- Rule a failure "infrastructure" (not a strike) when the cause is downloads, environment or runners.

## What the interim PM may NOT do (stop and leave for the PM/owner)
- Merge anything into main, mark PR #67 ready, or close/delete PRs or branches other than wip/*.
- Loosen, remove or bypass a validator rule, guard or pin for more than one named objective; delete a test class; weaken an assertion except as the precedents above allow.
- Freeze or intake new sources (no new FREEZE.json entries).
- Compose, translate into, or edit Russian text.
- Review its own work as the independent close-out reviewer. Block 5's independent diff review (T18f) is done by Cursor (available from 2026-11-29) or left for the PM.
- Start T-AUDIO-2, T14-A, ElevenLabs work, or anything that spends owner credits.
- Change owner listen-check decisions, verifier, dates or clip SHAs.

## Work order
1. T18de (already written by the PM; see D67) -> exact-commit CI green.
2. T18f Block 5 close-out: queue DONE entries; findings dispositions for Units 21-25; CC004 and CC006 are not defined anywhere in the repo, so record that and do not invent them. Then the owner asks CURSOR for the independent narrow diff review of origin/main..cursor/block5-units-21-25 (D53g-style stopping rule: BLOCKING only if wrong learner-visible content ships now). Fixes for BLOCKING items: one packet, fix once.
3. If T18f's review passes: stop. Do not merge; the PM decides.
4. Only if time remains and step 3 is reached: start Block 6 on a NEW branch cursor/block6-units-26-30 from cursor/block5-units-21-25 (not main), Draft PR into cursor/block5-units-21-25 is NOT allowed — open the Draft PR into main and say in its body that it depends on PR #67. Units 26-30 one at a time using audit/prep/unit-26..30-prep-at-c773acd6.txt. Unit 29 has a known missing frozen file (T15a-n2 note on the T19 row): apply the retire-and-defer precedent if needed.

## CI
Full CI takes about 3 hours. Codex cannot wait or poll across sessions: end each task after the push, and let the owner start the next task once GitHub shows the run green. When a run fails, read the failing tests from the run log (gh run view <id> --log-failed) and write one fix packet.

## Log (required)
Keep audit/prep/interim-pm-log.md on branch owner/prep-reports, one entry per task: date, task id, commits, CI run id and result, interim decisions (I-numbers) with reasons, anything stopped and why. Commit it after every task (prep-branch pushes do not run CI).

## Hand-back report (when the owner asks, Wednesday)
Output, under 80 lines: (1) branch heads and CI status for every branch touched; (2) tasks completed with commits and CI runs; (3) every interim decision I1… with its reason; (4) open failures, STOPs and anything left for the PM; (5) the next task you would have run. The owner pastes this to the PM.
