# V2 Implementation Plan

**Authority:** `MASTER_AUTHORITY_v2.0.md` → `PREMIUM_OFFLINE_APP_BLUEPRINT_v2.0.md`  
**Reconciliation:** `V2_REBUILD_RECONCILIATION.md`  
**Authorized cycle:** foundation rebuild + Units 1–2 vertical slice  
**Do not merge this work. Draft PR only.**

This cycle combines Blueprint Phases 0–4 plus the reusable frame for Units 3–30. Later phases (full Units 3–30 mappings, complete restore, candidate freeze) remain out of scope.

---

## Phase A — Governance reset

Independently reviewable as documentation and identity only.

- Install v2.0 Master Authority, Blueprint, and Research Foundation.
- Mark v1 governance/design/research documents superseded.
- Record failed v1.1.0 accurately; record v1.1.1 as pedagogically rejected and not frozen.
- Increment identity to `2.0.0-dev` / `versionCode` 5.
- Add content-completeness and linguistic-review blockers to the release gate so a structural 30-shell can no longer become APPROVED.
- Keep `assembleRelease` fail-closed. Do not freeze. Do not mark AT procedures passed.

## Phase B — Content schema, pack, and validator

- Add a versioned v2 curriculum pack with stable IDs.
- Entities: units, sessions, steps, objectives, instruction blocks, closed-response items, reference entries, sources, rights, review records.
- Install Units 1–2 to production depth using only independently written English, public-domain letter forms, and truthful unavailable states.
- Install Units 3–30 as map rows with `NOT_YET_INSTALLED`.
- Reject unverified Russian words, catalog-order audio, empty placeholders, and Penguin prose.
- Room schema v7: additive user tables for v2 profile, step progress, attempts, bookmarks, notes, tutor queue. Preserve v6 user recordings, logs, and exports.

## Phase C — Design system and five-destination shell

- Material 3, dark default, Follow system, safe insets, 48 dp targets.
- Compact primary navigation: Today, Course, Practice, Reference, Progress.
- Secondary: settings, appearance, data health, backup/export, attributions.
- Onboarding: welcome, study system, starting point (default New to Cyrillic), daily commitment, ready → Unit 1.
- Structured unit cards. No 30-button wall.

## Phase D — Unit 1, Unit 2, and shared tools

- Guided session player with saved position and resume.
- Unit 1: writing-system orientation, staged letter groups, confusable shapes, print forms, decoding, pronunciation support, stress awareness, book direction, companions, cumulative letter check, remediation.
- Unit 2: handwriting purpose, print-to-handwriting correspondence (book-directed while assets are absent), Unit 1 review, pronunciation reinforcement, reading fluency, unscored self-recording, book direction, check.
- Labs filtered by current/previous units: letter recognition, sound-symbol review, decoding, model playback when licensed mapped audio exists, local recording, A/B, current-unit review, bookmarks, factual completion, manual self-assessment, mark-for-tutor.
- Reference limited to introduced material plus study instructions and limitations.
- Locked / not-installed units cannot be opened as fake lessons.

## Phase E — Progress, gates, export, verification

- Objective states: Not started, Learning, Review required, Demonstrated in app checks, Completed by self-attestation only.
- Transparent Unit 1–2 advancement rules. No proficiency claims.
- Keep local export and backup-status reporting. Do not claim backup protection without a successful export.
- Replace obsolete tests. Add the required v2 automated coverage.
- Capture Galaxy S26 Ultra-equivalent screenshots.
- Stop at a draft PR after debug assemble, lint, gate attachment, fail-closed release, and test totals.

## Phase F — Out of this cycle

- Reviewer-approved Penguin edition mappings.
- Licensed letter/example audio intake.
- Handwriting asset intake.
- Units 3–30 instructional bodies.
- Complete restore preview/snapshot.
- Physical acceptance, AT-031, freeze, publication.
