# PR64 required-path corrections — implementation remains in progress

Baseline: `69f4325815879ec29f1754cc8d2867ad7d19f19c`. Existing draft PR #64 is the correction branch. No merge or release is authorized by this report.

## Verified baseline

The current curriculum was reconstructed and independently matched Git blob `d234d652aed396b901a3b68e32b5badd7b8b81cf` (14,954,522 bytes). The production Kotlin serializer and validator report zero structural errors. All 30 unit content digests match the current review manifests. Schema-3 pack content-review digest remains `7e46c09bd636a00a8e966662c55402683cc597da0667b141bb508c510ab3d315`.

Full Android CI at the baseline is **not passing**. Push run `37910943643`, job `113755656616`, reports **586 tests, 39 failures, 1 skipped**. The exact baseline failure names are retained in `PR64_FULL_SUITE_BASELINE_FAILURES.tsv`; that list is historical evidence, not a claim that the same count persists after this patch.

## Corrections

- Continue and Today select required sessions ahead of untouched optional-only sessions. The shared orientation remains required. Unknown or unassigned session ownership is retained on the required path. A verified saved position can still resume explicitly opened optional study; an unverified resume remains blocked.
- Today prioritizes required objectives. Correct responses belonging to optional objectives no longer satisfy the minimum required-check count.
- Completion and UI use one required-question selector for mixed sessions. Optional questions remain available and visibly optional. Required selections must retain their exact governed identities and prescribed-attempt role. Explicitly opened optional-only sessions still require their own responses. The current Unit 27 pack had already separated optional stress checks; the mixed-session cases are regression safeguards, not an additional observed Unit 27 content defect.
- Update two runtime tests to traverse the real guided-practice route added by the earlier foundation repair and return to the failed check with selections intact. Their Android execution is pending.
- Regenerate all 30 review packets with the production generator. Exactly 90 generated files change, solely in their repository-source binding. Unit content, mapping and review-package digests remain unchanged. Refresh the current candidate, readiness records and preparation manifest to source digest `24769c7187f82db5f121e58f437f3efe926445876506adc84b2ad117a88306d2`.

## Historical assertion corrections

- Replace the old empty-mapping assertion with 30 pending, unsigned unit records and unresolved ISBN identity. Retain generated-Russian, offline-manifest and rights checks.
- Bind corrected Batch A relationships to the declared cue, answer and distractor applications. Feedback and assessment explanations must both name the keyed form and answer, but may use different wording. Preserve rejection checks for exchanged explanations.
- Use the admitted corrected comprehension corpus hash and feature set. Invalid identities and book self-attestation still leave the claim unresolved; diagnostic assertions name the mutated capability.
- Clone the complete independent-check shape when testing duplication. A repeated prompt alone with a different contextual option set is not a duplicate check.

## Verification and limits

**17 focused host JUnit tests pass:** ContentAuditCorrectionTest (3), ContentAuditCheckpointRepairTest (4), RequiredPathProgressTest (10). The first eight scheduling regressions reproduce four failures against the original production progress rules and pass after correction. `PR64_REQUIRED_PATH_HOST_TESTS.txt` retains the focused summary.

The host harness compiles the actual production curriculum, governed identity, validator, notice and entity classes with Kotlin 2.0.21 and serialization 1.7.3. Android Context/AssetManager and Room annotation stubs support compilation only. The Compose screen and Robolectric tests require Android CI; no current Android pass, lint clearance or APK identity is asserted. The broader 11-class host curriculum run completed: 104 tests, 96 passed and 8 failed. The failures were historical source/message/layout assertions, retained in the host summary. A follow-up corrects these assertions to the current declared applications and pinned research corpus while retaining negative capability and swapped-explanation checks. All 8 affected tests pass across the targeted reruns: 5 passed in the first follow-up, then the remaining 3 passed after the additional historical assertions were corrected. The final 3-test run includes the complete 41-item Batch A exchange checks and Phase 2/3 identity mutations. The 4 modified test classes compile against the actual production classes. `PR64_HISTORICAL_ASSERTION_HOST_TESTS.txt` retains the run summaries, including the intermediate failures. This is not a fresh full 104-test or Android-suite pass.

The complete 19-finding audit remains open. Preserve source-capability forgery and semantic rejection tests while reconciling historical assertions with the corrected, review-pending sources. Broader curriculum coverage, exact primary-reference verification, Penguin mapping acceptance, required audio/handwriting assets, a comprehensive retained final audit, owner acceptance and physical-device evidence remain pending. Zero structural errors and these tests are not linguistic acceptance.

Candidate remains `frozen: false`, `sourceCommit: null`, artifact `NOT_RECORDED`, public release `NOT_APPROVED`. Review signatures remain blank. No READY token, installation approval, candidate freeze or release approval is issued.
