# PR64 verification repair — content acceptance pending

Parent branch checkpoint: `18b7eb5643d6a1ae2320f963da8e0ff5332c9f4d`.
Preserved content implementation: `061b8d678ae76531092767da78a837f9d2b67930`, whose parent is that checkpoint.

## What changed

- Preserve the 64 declension explanations and 140 pending research coverage records from the content implementation. Russian cells, answer keys, options, and frozen provenance remain as recorded by that implementation.
- Replace obsolete schema-2 content and review-package snapshots with the current schema-3 snapshots. Keep changed-content, wrong-source, relabel, cloned-check, unsupported-capability, missing-rights, invented-mapping, unsigned-review, and fail-closed release rejection tests.
- Assert that all 30 observed mapping records are pending and unsigned. Their existence is not mapping approval. The historical post-PR53 report still records zero mappings; its historical bytes and digests are preserved.
- Check the explicitly admitted content-audit research source against its exact identity, hashes, rights, structured bibliography/authorship/binding, and absent acceptance. Do not force independently authored corrections into a quoted-source license.
- Keep the whole active Unit 1 session unfinished in the runtime fixture. Seed actual governed answers for the other completed sessions. This allows the test to traverse new guided practice, then test failed-answer remediation, note persistence, resume, and advancement without expecting Continue to repeat completed work.
- Find attribution notices by text rather than source-count-dependent lazy-list indices.
- Require all 30 review packets to bind the content implementation commit. Keep rejection of HEAD-as-content-pin and obsolete or predating content pins.
- Regenerate the 30 review packets, current readiness reports, final-audit preparation manifest, and current candidate source binding. Reviewer and owner acceptance fields stay unset.

## Observed verification

The production Kotlin validator reports **zero errors**.

A single fresh host JUnit run completed **36 tests, 36 passed, 0 failed, 0 skipped**. It covers 18 previously failing host-compatible methods and 18 required-path/content-correction regressions. Retained output and XML are in `pr64-verification-2026-10-09/`.

This harness compiles the repository's actual curriculum, validation, identity, review, readiness, and release code with Kotlin 2.0.21. Android context and Room annotation stubs support compilation only. It does not execute Compose, Robolectric, the Android Gradle build, an APK, or a physical device.

Latest inspected pre-repair Android CI: pull-request run `37929211005`, branch head `18b7eb5`, merge checkout `60556ac7`. Debug assembly, lint, and release-gate attachment succeeded; **596 tests completed, 25 failed, 1 skipped**. All 25 failing names are retained in `PR64_FULL_SUITE_18b7_FAILURES.tsv`. Those results are historical failure evidence, not results for this repair. Current full Android verification remains pending when this record is authored.

Repository-source digest: `3c8a7ffd929bcaafbb593e64f1ee7580485077949539431b7a1e4a0c87b9b6c6`.
Schema-3 pack content-review digest: `d28094322a590bd83f08a5d38f03eb1bfddcf728d51304fa579e322dfac1803f`.

## Remaining work

The 19-finding preliminary content audit is still open. Structural validation and host tests do not establish semantic closure of the 75 targets or full curriculum coverage. Broader novice instruction, primary-reference verification, all 30 Penguin objective/exercise mappings, mapped pronunciation audio, handwriting assets, a retained comprehensive final curriculum audit, independent verification, and owner/device acceptance remain outstanding.

PR64 stays draft. Units stay DEVELOPMENT / CONTENT_COMPLETE_REVIEW_PENDING. The candidate stays unfrozen, `sourceCommit: null`, artifact NOT_RECORDED, public release NOT_APPROVED. Acceptance forms remain NOT_EXECUTED. No audit-readiness token, merge, freeze, review approval, or release approval is issued.

## Android compilation follow-up and target reconciliation

The `ae5804b` push run `37951085157` (job `113889721577`) and pull-request run `37951091356` failed during test Kotlin compilation. The observed error was `RuntimeCorrectionTest.kt:204:80`: a `V2Step` was passed to `stepCompletionItemIds`, which requires a `String` step ID. Unit tests did not execute in those runs. This does not invalidate the earlier 36-test host result, whose harness does not compile that Android test.

Commit `d40f56a78f05b456e61ea25f81e624bd05a3d0ac` changes the argument to `completed.id`. Fresh Android runs `37952679276` (push) and `37952684783` (pull request) were started. Full Android results remain pending at the time this follow-up is authored.

A fresh production-domain fixture probe confirms that the actual pack opens the unfinished iotated teaching step, the guided step, then the independent check. After all required steps have governed completion records, the route opens advancement review even when the independent answers were wrong. This verifies navigation and fixture preparation only; it does not award objective mastery or exercise Compose, persistence, or remediation UI. The probe source and successful output are retained alongside the earlier host evidence.

`ITEM_TARGET_CURRENT_EVIDENCE.json` and its Markdown index reconcile all 75 retained target IDs against current content. All resolve uniquely: 69 objects match the retained preliminary implementation snapshot, two item explanations changed, and four objective entries were absent from that snapshot. The current evidence now includes those objectives and their related steps, provenance, and advancement context. All 75 targets remain pending semantic review. `reconcile_item_targets.py --check` verifies deterministic evidence against exact raw input hashes without modifying content or approval state.
