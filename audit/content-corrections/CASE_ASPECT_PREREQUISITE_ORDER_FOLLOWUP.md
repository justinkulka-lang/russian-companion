# Case and aspect teaching before guided practice

Draft PR #64 remains in progress. This is a bounded order-only correction to six cue-reviewed sessions, not semantic acceptance or closure of CA009/CA010.

## Reviewed dependencies

| Session | Relevant teaching previously after guided practice |
|---|---|
| Unit 6 feminine noun declension | The accusative-declension explanation; the first card supplied only two forms with the same English gloss. |
| Unit 7 spelling | The tested plural adjective form and the ending-spelling explanation. |
| Unit 8 spelling | The tested plural noun form and the seven-letter spelling explanation. |
| Unit 12 aspect | The context-dependent aspect-counterpart explanation; the first card supplied identical English glosses. |
| Unit 16 instrumental singular nouns | The hard-stem ending explanation and case-form distractor cards. |
| Unit 16 instrumental adjectives | The instrumental-ending explanation and adjective-form distractor cards. |

All existing TEACH cards now precede each session's single GUIDED step. Teaching retains its relative order. GUIDED, REMEDIATE and CHECK retain their relative order. All step objects, bodies, IDs, examples, Russian cues, items, keys, options, assessment bindings, objectives, independent-check identities, advancement rules and provenance records are unchanged. All other sessions are unchanged. No new source, rights record, Russian form or synthesis record is added.

The correction makes existing teaching available earlier. It does not establish complete case/form annotation, sufficient sentence-level worked examples, alignment between the Unit 7 spelling rule and its plural-form question, or linguistic accuracy. These content questions remain pending review. Unit 3's gender session has several teaching/practice pairs and is outside the single-guided scan; its ordering is not changed by this batch. Unit 12 compound future was inspected separately: its initial card already contains the exact guided answer and contrast. This batch does not classify that session's ordering as a confirmed defect.

Implementation ancestor: `0e763dfd4284587065fbb48aae5c5ee5add54f8f`, parent `86a2ed63989e38ae5b36ac64e5e8f347bfe076ae`. All 30 refreshed review packets bind that actual implementation ancestor. Instructional digests change only for Units 6, 7, 8, 12 and 16; every mapping-package digest remains unchanged. Review-package digests change because they bind the new ancestor.

## Verification and limits

Fresh focused host JUnit run: **53 tests, 53 passed, zero failures or skips**. Six added regressions each reject the exact preceding curriculum because relevant teaching follows guided practice. The existing saved-position regression now covers all ten reordered sessions from this and the preceding batch: an identity bound to the old guided position is accepted for the old order, rejected for the current order, and a newly bound current position is accepted. This executes production resume-identity logic, not Android persistence or Compose.

Production Kotlin validation: zero errors. Exact scope verification confirms only six session orders change and every per-step object and every other curriculum object is preserved. Candidate, readiness, preparation, dossier, all 30 review packets and current target-evidence bindings are refreshed. Exact digest pins were updated, while negative integrity, review-state, source and cross-unit isolation assertions remain intact.

The deterministic single-guided inventory now retains **130 unresolved cue-level sequencing flags**, down from 136. Flags are not 130 confirmed defects; each cue, answer and distractor requires comparison with instruction available before practice. Multi-guided sessions remain outside the scan. None receives semantic acceptance through this structural repair.

All 75 historical audit targets resolve uniquely and remain pending semantic acceptance: 65 unchanged, six changed and four absent from the historical snapshot. These comparisons do not provide acceptance verdicts. Earlier corrections and evidence remain retained separately.

Host verification uses production domain/validator code with Android/Room compilation stubs. It does not execute Compose, Android persistence, an APK or a physical device. Current-head Android CI must be checked separately; an earlier-head result or APK does not verify this batch.

Evidence: `pr64-case-aspect-sequence-verification-2026-10-09/`. The repair ledger, repair script and scope-check script are alongside this report. To reproduce the scope comparison, supply the curriculum and a JSON object of all 30 unit manifests from the preceding head to `verify_case_aspect_prerequisite_scope.py --baseline PATH --baseline-manifests PATH`.

- Repository-source digest: `b41ea55aababf208fcf77117905725c6bd19d944dc67f358b6da1dd55ecde15f`.
- Schema-3 pack content-review digest: `d1d74b79987cf9d789d845a9d6e8d444d4c60bb03b12734acec5800b8a1b64ca`.

## Remaining work

Review remaining cues before changing sequences, then strengthen incomplete explanations and worked examples with retained evidence. The 19-finding audit remains open. Wider content accuracy, beginner practice, cumulative review/reference coverage, detailed Penguin mappings, required audio/handwriting assets, the comprehensive retained final Claude curriculum audit, independent verification and owner/device acceptance remain unfinished.

Review and mapping signatures stay pending. Candidate remains unfrozen, sourceCommit null, artifact NOT_RECORDED and public release NOT_APPROVED. No ready marking, merge, freeze, device acceptance or release approval is issued.
