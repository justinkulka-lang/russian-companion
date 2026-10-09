# Guided prerequisite teaching before practice

Draft PR #64 remains in progress. This batch addresses four reviewed sequencing problems; it does not close CA009/CA010 or provide semantic curriculum approval.

## Reviewed dependencies and correction

| Session | Existing teaching after practice | Correction |
|---|---|---|
| Unit 6 possessives | The exact stressed possessive forms in the guided options and the possessive contrast | Move all six existing teaching cards before the single guided choice. |
| Unit 21 purpose | The ordinary-purpose versus bookish-register explanation used by the question and feedback | Move all three existing teaching cards before the single guided choice. |
| Unit 28 imperfective verbal adverb | The explicit relative-time, no-independent-tense and shared-subject explanation used by the timing question and distractors | Move both existing teaching cards before the single guided choice. |
| Unit 28 perfective verbal adverb | The explicit anteriority before a past or future main action used by the future-sentence question | Move all three existing teaching cards before the single guided choice. |

This is an order-only repair. Teaching cards retain their relative order; GUIDED, REMEDIATE and CHECK retain theirs. Every step object, example, item, answer key, option, Russian cue, assessment binding, objective, independent-check identity, advancement rule and provenance record is unchanged. Other sessions are unchanged. No source, rights record, Russian form, synthesis record or mapping approval is added.

The scope is access to existing prerequisite teaching. Unit 6 still needs review of explicit gender/number explanations; Unit 28 still needs review of sentence-level worked-example coverage and same-subject explanation in its perfective session. Moving existing cards does not establish that these lessons are complete or linguistically accepted.

Implementation ancestor: `8ff440be0691161ffaf053961d0fdfec1863856b`, parent `cf6dbe57dacf13535c9c8c547eef72b6702feae7`. All 30 review packets bind that actual implementation ancestor. Instructional digests change only for Units 6, 21 and 28; all mapping-package digests remain unchanged. All review-package digests change because they bind the new ancestor.

## Verification

Fresh focused host run: **47 tests, 47 passed, zero failures or skips**. Four new regressions independently reject the exact preceding curriculum because the relevant teaching follows guided practice. A fifth regression reconstructs the previous order in each corrected session: a saved guided-position identity is valid for that old order, rejected for the current order, and a newly bound current position is accepted. It runs production resume-identity logic; Android storage and UI handling are outside this host test.

Production Kotlin validation reports zero errors. Exact scope comparison verifies only four session orders changed, with every step object and all other curriculum objects preserved. Candidate, readiness, preparation, dossier, all 30 review packets and current target-evidence bindings are refreshed. Exact digest expectations were updated; negative integrity, review-state, source and cross-unit isolation assertions remain intact.

The deterministic sequencing inventory now retains **136 unresolved cue-level review candidates**, down from 140 after the four reviewed repairs. These are flags, not 136 confirmed defects. Multiple-guided-step sessions remain outside that scan. No sequence candidate is granted semantic acceptance.

All 75 historical audit targets remain uniquely resolved and pending semantic acceptance: 65 unchanged, six changed, four absent in the historical snapshot. Snapshot comparison is not an acceptance verdict. Earlier Unit 24 and Unit 25 corrections and their evidence remain retained separately.

The host harness uses production domain/validator code with Android/Room compilation stubs. It does not execute Compose, Android persistence, an APK or a physical device. Current-head Android CI must be checked independently; results at earlier commits do not verify this batch.

Retained evidence: `pr64-sequence-verification-2026-10-09/`. The repair and scope-check scripts and correction ledger are alongside this report. To reproduce the scope check, provide the curriculum and all 30 manifests from the preceding head to `verify_guided_prerequisite_scope.py --baseline PATH --baseline-manifests PATH`.

- Repository-source digest: `a97b0b94acec8e829de55d86e1d8db99eacfd540d6225dd45a78774595fc28cf`.
- Schema-3 pack content-review digest: `20e41143b4550fcb20e92589fbf36b56ec072514ba653f2363a5ad34b6a1d82d`.
- Unit 6 instructional digest: `a9aa3117e82201373679079f03d624e38c8584047b773cdd78e4c31c528517f5`.
- Unit 21 instructional digest: `1f78bbc71dd6838477cb1e7aa13daf6d1a1951ce104f5f9f67e32c8490e22fc5`.
- Unit 28 instructional digest: `e979faa95a8b8acf8e2d03cce0760f24953b652da2d4f663ce1a169342593312`.

## Remaining work

Review the remaining flagged cues before changing their sequences, then strengthen the incomplete explanations and worked examples with retained evidence. The 19-finding audit remains open. Wider content accuracy, beginner practice, cumulative review/reference coverage, detailed Penguin mappings, required audio/handwriting assets, the comprehensive retained final Claude curriculum audit, independent verification and owner/device acceptance remain unfinished.

Review and mapping signatures remain pending. Candidate remains unfrozen, sourceCommit null, artifact NOT_RECORDED and public release NOT_APPROVED. No ready marking, merge, freeze, device acceptance or release approval is issued by this correction.
