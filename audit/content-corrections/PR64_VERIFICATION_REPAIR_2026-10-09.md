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
