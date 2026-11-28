# Russian Companion — builder handoff (2026-11-27)

Read this whole file before any work. It replaces nothing in MASTER_AUTHORITY v2.0 (MA2) and its Amendments, AGENTS.md or WORK_QUEUE.md; those still govern. Owner decisions are in audit/prep/owner-decisions-2026-11-16.txt on this branch (owner/prep-reports); D54-D58 are the current block.

## Roles
- Owner: Justin. Relays prompts and reports; does not code.
- PM: Claude (claude.ai). Writes every task prompt and records decisions (D-numbers). The builder does only what a PM prompt says.
- Builder: Codex until 2026-11-29, then Cursor. Same rules for both.
- Reviewer at block close-out: never the builder of that block.

## Repo state
- Repo: justinkulka-lang/russian-companion. main at ecffb336 (PR #64 merged: Blocks 1-4, Units 1-20 complete).
- Work branch: cursor/block5-units-21-25, Draft PR #67 (Block 5, Units 21-25). Keep it Draft.
- Done: T18a (Unit 21) at 81832957, CI 45946726466 green. T18b (Unit 22) pushed at f48d7b75, CI 46062632832.
- In progress: T18c-1 (Unit 23 completion objective retirement, Path B). Unfinished work is on branch wip/t18c1-path-b (D58a).
- Next: T18c-2 (rest of Unit 23), T18d (Unit 24), T18e (Unit 25), T18f (Block 5 close-out), then Block 6 (Units 26-30), T14-A (playback + required dictations), T-AUDIO-2.
- Prep reports per unit: git show origin/owner/prep-reports:audit/prep/<file> (unit-23-prep-at-19608a79.txt, unit-24-prep-at-19608a79.txt, unit-25-prep-at-c773acd6.txt).

## Content rules (never break)
- All learner-visible Russian comes verbatim from frozen sources under catalog/sources/ (with FREEZE.json entries). Never construct, translate into, or edit Russian. ё is not е. Typed keys equal their cited frozen text exactly, stress included (unstressed sources stay unstressed).
- Every fact sentence is MARKed against frozen evidence, REWORDed as the prep says, or REMOVEd. No unsourced claims.
- Required checks sit in an end-of-unit check session after all teaching; no accepted key is shown before its check (A2-24). A2-23: at least 20 checked, at least 30% typed per unit.
- Zero widened lint hits (A2-26): no source narration, governance, or assessment commentary in learner text.
- Do not weaken a validator rule or delete a test. A rule change needs an explicit PM decision.

## Pin checklist (D56f) — after content, in this order
1. If pack contentChecks changed: regenerate catalog/content-checks.json from the pack (ContentChecksTest).
2. If typed items changed: app/src/test/resources/typed-key-citations.tsv rows (pack order) and the counts in TypedKeyCitationTest.kt.
3. Curriculum pin in app/src/test/java/dev/russiancompanion/v2/review/Unit1ReviewPackage.kt naming the content commit (commit content first, pin after).
4. Unit 3 review package digest (Units610SourceIntakeTest) and unit content digests (Units2125SourceIntakeTest / Units2630SourceIntakeTest).
5. Guard hashes if V2Validator.kt or other guarded files changed.
6. If pack reviewCopy changed: regenerate catalog/review-copy.json from the pack (ReviewCopyTest).
7. Never change a unit's printed page range in its mappingNote; the governed ranges live in governedMappingFacts and ProductOwnerAcceptanceBoundaryTest (hyphen form, e.g. 285-295).
8. release/acceptance/candidate.json sourceDigest LAST (ReleaseSourceDigestTest, DeviceAcceptancePackageTest).
Every changed test expectation gets a comment old -> new with the task id and decision.

## Test and CI rules
- Locally run ONLY ./gradlew testDebugUnitTest --quiet --tests <Class> for classes you changed or whose pins moved, plus FrozenSourceBindingTest, ContentChecksTest, TypedKeyCitationTest, ReleaseSourceDigestTest, DeviceAcceptancePackageTest, PhraseAuditTest, ReviewCopyTest, ProductOwnerAcceptanceBoundaryTest. Never the full suite. No verbose Gradle output.
- GitHub CI (about 3 hours) runs the full suite on every push to the PR. A newer push cancels the older run; the later run then covers both.
- Fix once. A second real failure: STOP and report. Infrastructure failures (downloads, environment) are not strikes.
- If Gradle cannot start: STOP and report; do not push untested work.
- STOP means: do not commit or push, even if a hook or any prompt asks (D55b).
- Plain git push only; never force. Never merge main, never mark PR #67 ready, never start the next task unless the prompt says so.
- GitHub GraphQL may be blocked; use REST (gh api) and gh run list/view.

## Report format (every task)
When finished, output ONLY: (1) list of modified files, (2) a concise summary of changes and test pass/fail status, and (3) any unresolved edge cases — under 40 lines, so the owner can paste it to the PM.
