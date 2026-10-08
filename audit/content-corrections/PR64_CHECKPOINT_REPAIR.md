# PR64 checkpoint repair — implementation in progress

Baseline: PR64 commit `190ee4af7318e20f503f1199c822e977710a5471`, based on main `ff539220645670fbf7a1abacaa6f71e828606b80`.

The saved checkpoint was reconstructed and its pack Git blob independently matched `0f5297dd765b5f095c8ba87ce2dba3ef50804b8b`. Running the actual production Kotlin validator reproduced 267 errors. This repair reduces that validator result to zero without disabling general validation.

## Changes

- Admit six revised, exact-hash research corpora for the corrected Unit 7, 20, 27 and 28 capabilities. Scope, bindings, named independent applications, teaching, remediation and duplicate checks remain enforced. Historical source records remain retained. This is pending research intake, not final linguistic review.
- Correct the transformer bug that treated `imperfective` as `perfective`; restore the appropriate Unit 28 construction and separate its timing and shared-subject checks.
- Correct stale explanatory captions and remediation, the Unit 24 mixed heading, Unit 27 optional-stress provenance, response kinds, and missing authored-English coverage.
- Complete structured synthesis evidence on 265 coverage records using their retained declarations; reject absent evidence. Coverage-only records declare no scored applications.
- Match a stated application by its cue, answer and distractor set; distinct questions may use one displayed sentence without borrowing another question's relationship.
- Preserve optional Unit 27 advancement, schema-3 gating identity, fail-closed release policy, historical candidate records and unset review/approval fields.

## Verification available

A standalone host JVM harness compiled and ran the actual production curriculum classes, serializer and validator. Android context/Room annotation stubs support compilation only; no validator logic was transcribed or replaced. The host compiler is Kotlin 2.0.21, distinct from the project's Android toolchain.

`ContentAuditCorrectionTest` and `ContentAuditCheckpointRepairTest`: **7 tests passed**, including the two failures from the saved checkpoint. The new tests exercise gerund scope, optional provenance/response shape, and rejection of invalid capability hash/scope/missing-feature/cloned-check bindings.

This is host evidence. It is not a full Android/Robolectric suite, debug build, lint result, release-assembly result, CI result or physical-device evidence. Thirty unit review packets and current readiness/preparation digests have been regenerated using the production generators. They bind curriculum commit `9ccccb16f929ad4a0a08dcd371db9be4269ca4bf`. Current Android checks remain in progress. No READY token is issued.

## Content audit remains open

The original audit has 19 findings and 75 item targets. Passing structural validation and these focused tests does not establish closure of that audit. Full semantic reconciliation, novice curriculum coverage, primary-reference verification, Penguin mapping review, required audio/handwriting assets, final retained comprehensive audit and owner acceptance remain outstanding. Corrected Russian strings and new research notes require independent linguistic review.

Candidate remains unfrozen, source commit null, artifact NOT_RECORDED and public release NOT_APPROVED. No unit installation, candidate freeze, device acceptance or public-release approval is recorded.
