# Unit 14 test-reconciliation review

**Verdict: SUPPORTED WITHIN REVIEWED SCOPE**

Reviewed commit: `8eb9d3d4cc3fdbcd70d5b8904087ddad18acfaf1`  
Branch: `cursor/content-audit-corrections-964f`  
Remote head at the end of this review: the same commit. No newer head.

This is a bounded engineering review of the Unit 14 historical-contract reconciliation. It is not app, curriculum, linguistic, mapping, owner, or release approval. All 48 independent audit findings remain OPEN. The project audit remains INCOMPLETE. Physical Galaxy S26 Ultra acceptance was not performed and stays last.

## Findings

No substantiated defect. The seven-file reconciliation keeps the original assertion texts in order, rebinds only the historical subjects the failures actually hit, and leaves current positive and negative controls on the current pack. `CURSOR_FINDINGS.csv` therefore contains the header only.

The closest thing to a hidden rebinding is real and was checked, not waived. In `Unit13BindingConservationTest.currentDeclarationReconstructsOnlyUnit13ThenEveryGenuineEarlierPack`, the non-Unit-13 unit-list assertion is textually unchanged, but `pack` inside the new `let` is the declaration-restored pack (`Unit13BindingConservationTest.kt` lines 20–22). That assertion no longer compares the live unit list. The live pack is still checked by `assertCurrent` on the outer `pack` (line 23), by the unchanged item and provenance equalities (line 24), by `beforeUnit14PluralPossessionReview`’s full-pack equality to the Git sources, and by the unchanged `Unit14BindingConservationTest` (lines 19–20). The raw curriculum file differs from those Git sources by one policy id, so the rebinding does not conceal teaching, items, provenance, or another unit.

## What the reconciliation changes

Correction commit `0c6789a291c6170dab792c908dba5c9b379067fa` (1:06 p.m. CT, Oct 28, 2026) is the only commit that edits the seven test files between baseline `ae82d742ce2f9fac89acb91f618224187922e5ca` and the pinned head. Those seven files are identical at `0c6789a2` and `8eb9d3d4`. `RecordedCurriculumConservationScope.kt` is the same blob at `ae82d742`, `0c6789a2`, and `8eb9d3d4` (`b14696e5136520b7e3e570bfcf756329dfa7976a`). The helper was not rewritten by the reconciliation.

## A. Historical scope

`beforeUnit14PluralPossessionReview` (`RecordedCurriculumConservationScope.kt` lines 43–54) requires the caller’s pack to equal producer `d72afd1232857a857624c20c58f4bcd3cb5c8564`, copies it, and changes only Unit 14 `cumulativeReviewPolicyId` to `unit14-prior-reading-reported-time-v1`. It then requires that restored pack to equal both `bd00e5082c9f31d2ae3b379dd6204dd3b28e4475` and `9101af8c182b3415426ed9226cb211a707a2c393`, and Unit 14 of that pack to match `96242a17a87cc2ec0f5d7418cd98a9d3220a9e99`.

Independent Git comparison of `app/src/main/assets/content/v2-curriculum.json`:

| Commit | Bytes | SHA-256 |
| --- | ---: | --- |
| `bd00e508` and `9101af8c` | 20,350,042 | `765f06b3719192a5b0f19df553e3a5f20d00e2e7eb42130c908ceac15e4a9acf` |
| `d72afd12` through `8eb9d3d4` | 20,350,052 | `02202d37e0aa2c51182e4db22feff177124b6db2c270a67a93ef85e188916c19` |

Replacing the single occurrence of `unit14-prior-reading-reported-time-v1` with `unit14-prior-plural-possession-reported-time-v2` makes the `9101af8c` file byte-identical to `d72afd12`. The parsed difference is only `.units[13].advancement.cumulativeReviewPolicyId` (Unit 14). `V2PackLoader` sets `ignoreUnknownKeys = false`. `V2CurriculumPack`, `V2Unit`, and `V2AdvancementRule` are data classes, so pack equality covers teaching, items, examples, provenance, capabilities, and advancement. Unit 14’s other advancement flags stay `requireInstruction`, `requireBookSelfAttestation`, `requireAppChecks`, `minimumDistinctChecks = 6`, `requireDueReview`, and `tutorOptional`.

`96242a17`’s Unit 14 object is identical to `9101af8c`’s. `31ac807387253082c44bd2f6d67021478f0df66e` differs from `9101af8c` only in Unit 13’s policy id. Between `9101af8c` and `d72afd12`, the only `app/src/main` curriculum changes are that one JSON string and `V2CumulativeReview.kt`. The Kotlin diff adds schema 16 and `Unit14PluralPossessionCumulativeReviewPolicy`, points `cumulativeReviewPolicy(14)` at it, and keeps the v1 object for saved v1 assignments. Other units’ pool literals are unchanged. No historical hash allowlist was added to the helper or the seven files.

`POLICY_ONLY_ASSET_PROOF.json` matches these hashes and the two policy ids.

## B. Assertion text and meaning

Using the evidence directory’s assertion scanner, the seven files contain 757 original `assert*` / `fail` invocations and 774 after the correction. Every original invocation remains a textual subsequence. Seventeen calls were added. File hashes match `COMPARISON_REVIEW.json` and `ORIGINAL_COMPARISONS.zip`, which matches `ae82d742` byte for byte. ZIP CRC is clean.

Textual preservation is not the semantic result. The meaningful subject changes are:

- Unit 13’s non-13 unit list now compares the restored pre-Unit-14 pack, with live equality moved to `assertCurrent` and `Unit14BindingConservationTest`.
- Unit 13’s kernel append contract now reads `9101af8c` instead of the working tree, and the live file gets a separate kernel-hash check.
- Unit 13’s 59/112/52/19 inventory is computed with the v1 Unit 14 policy object substituted for the current dispatcher. Current 60/121/57/18 counts were added on `cumulativeReviewPolicy`.
- Three Unit 14 methods pass the restored pack into otherwise unchanged assertions.
- Unit 14’s stale-answer method stays on `pack()` and answers every remaining declared slot before the original refusals.
- Unit 14 impersonal whole-unit equality uses the restored pack. Current unit, items, provenance, capabilities, examples, and validation stay on the live pack.
- Unit 15 `prepare` answers every remaining Unit 14 slot. Its assertions are unchanged.
- Both source-intake files compare the old Unit 14 digest literal only on the restored pack, then require the live unit to match `d72afd12` and to have a different digest.

The v1-to-v2 pool arithmetic is consistent with the added counts: Unit 14 goes from two singleton pools to pools of 6, 4, and 1 candidates (`Unit11NounCumulativeReviewPolicy` pool 1 has four ids). That is +1 pool, +9 placements, and −1 print-singleton policy.

## C. Unit 13 binding

`9101af8c` is a genuine matching pair. `MATCHING_HISTORICAL_KERNEL_REFERENCE.zip` CRC-checks clean and its two members equal `git show 9101af8c` of `reviewed-curriculum-cyrillic.json` (SHA-256 `fe7c4472c07f40aa71ad6fcf6ac62ff6d4409bcdf17a17d83f5cb78fc59d320f`) and `V2CumulativeReview.kt` (SHA-256 `bda592eac2572396d9852ec818740d0a02d8b4879d7593568833ecac6515adc3`). The reference records that same kernel hash.

From `aa93df85b4290406d5b78b0035b4a77912ae25fe` to `9101af8c`, the reference keeps 19 files and 292 bindings, changes only the kernel entry’s `sha256`, preserves the prior review array, and appends exactly one record whose `sourceCommit` is `bd00e508`. Acceptance flags stay false. Those are the assertions at `Unit13BindingConservationTest.kt` lines 59–82.

The live reference at `8eb9d3d4` is the same shape against `9101af8c`: only the kernel `sha256` changes, to `cb279ccc1eaeec8d34ad7e51cccf6860b984f96549ae0bf347d538a1edfa483a`, which matches the live Kotlin file, and exactly one record is appended for `d72afd12`. `Unit14BindingConservationTest.kt` lines 47–78 still enforce that live contract. Its blob is unchanged from `ae82d742` to the pinned head. The narrower live check inside the Unit 13 test (lines 84–91) is therefore not the only current coverage.

The original failure message is `expected:<8> but was:<9>` (`Unit13BindingConservationTest.kt:71` at `dd736e1a`). That is the one-append count against the live file, which already had the later Unit 14 record. Pinning the count to `9101af8c` preserves the Unit 13 contract.

## D. Unit 13 compatibility and inventories

`exactPriorDeclarationsRefuseNewTasksAndPreserveBothUnit14OwnerBindings` builds the restored baseline from `beforeUnit14` and then sets only Unit 13’s policy id (`Unit13PresentCompatibilityTest.kt` lines 32–35). `before13` is the Git pack at `31ac8073`, which differs from `9101af8c` only by that Unit 13 id. The original failure is that equality, labeled “Entire actual baseline pack differs only by Unit13 declaration”. The full message contains each Unit 14 policy id once.

Lines 37–47 keep 59 pools, 112 placements, 52 unique candidates, and 19 print-singleton policies on the v1-substituted list, and add 60 / 121 / 57 / 18 on the current dispatcher. The observation sets `policyInventoryScope` to the historical Unit 13 activation and `canonicalSourceDigestLegacyLabelMeansPackContentReviewDigest` to true (lines 49–58). `V2Validator.contentReviewDigest` hashes `canonicalInstructionalDocument(pack)` (line 95). It is the pack content-review aggregate. The observation writer does not store the repository source digest in that field. Broad status records repository `487d9118d7154dd59162dbb7024f1349bc53d5b7bf89e9d79022d97eaa1eeb9a` and pack content-review `39c3563ba1b149b36977ae0fc2c0f920e282101e9e7870c06d141fe1a10e65ec` as distinct values. This review did not recompute either digest.

From line 72 onward, future-task, schema, owner-digest, item, rule, and teaching refusals still use the current `pack`. The producer used to open the saved Unit 13/14 tasks remains `before13` / its v1 variant, which was already the historical pack before this edit.

## E. Unit 14 behavior

These three methods now call `beforeUnit14PluralPossessionReview` and otherwise keep their assertions:

- `ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup` (line 38). Original message: expected `[obj-u1-decoding, obj-u13-reported]` but was `[obj-u8-plural, obj-u10-have, obj-u13-reported]`. That is the v1 two-slot contract. Current schema 16 coverage is `Unit14PluralPossessionReviewBaselineTest.earlierPluralPossessionAndOptionalAnswersCannotReplaceFreshAssignedDueAnswers` (three objectives, owners 8/10/13, advancement refused until the plural and possession slots are answered) and `Unit14PluralPossessionCompatibilityTest.assignedMissSurvivesOrdinaryCorrectionIdempotenceRollbackBackupReopenAndLateAnswer` (late wrong answer, completion retained, backup round-trip). The book-attestation refusal at line 51 runs on the restored pack, whose Unit 14 advancement flags other than the policy id are the current flags.
- `actualUnit14ContinueReportedTimeCueTeachingPracticeReturnAndAdvanceStayOnUnit14` (line 179). Original failure waits for `v2-example-ex-u13-reported-3`, the v1 reported-time route. Current routes are `Unit14PluralPossessionReviewUiTest.actualReportedTimeRepairRestoresAllTeachingAndReturnsWithoutDueCreditThenAdvances` (slot 2, schema 16, three slots, then advance) plus the plural and possession repair methods.
- `placementWithTaughtUnit1ButUntaughtUnit13RepairsTheMissingOwnerInsteadOfUnit1` (line 223). Original UI text was “Open earlier Unit 8 instruction and checks”, not Unit 13. V1’s first missing owner after taught Unit 1 is Unit 13. Current v2 owners are 8, 10, and 13. `unpreparedFiniteReviewRoutesToAllThreeOwnersWithoutCredit` and `actuallyCompletedUnit8DoesNotHideMissingUnit10And13OrCreateReviewCredit` cover that order and the no-credit result.

`optionalReplaysStalePrecheckAndInjectedAdvanceFailureCannotCreateCompletion` still uses `pack()` (line 74). The original failure is `assertTrue` at old line 82, message `java.lang.AssertionError`, after only slots 0 and 1. Lines 82–84 now answer every later declared slot through `answer`. The method then still requires advancement to fail after a wrong re-answer, the injected advance failure to roll back, `requireDueReview = false` to be rejected by both assignment and validator, and a packless advance to fail without creating completion (lines 85–103).

## F. Teaching and source intake

`allThreeStateCardsPrecedePracticeAndRepairRestoresEveryExample` compares the correction-era Unit 14 (`96242a17` via `correctionEraPack`) to the restored pack (lines 37–38). Current checks at lines 40–49 compare the live file with `d72afd12` and still compare items, provenance, capabilities, examples, and `V2Validator.validate` on the live pack. The original failure is whole-unit inequality after the policy id changed. The triage projection says equality returns if that declaration is substituted back.

`structuralMappingsMatchTheOwnedEditionObservations` and `generatedRussianPenguinAudioAndReleaseStayClosed` failed with the same comparison: Unit 14 expected `9e136e9af2fe3611d6862fa46e3217f85ad45c8b23ad622eec6f36e48ab97f38` but was `5b42bce13ad71142cbd536373d7ac5a3906839e0bc61ed9a69abecb10bbca171`. Both files still contain that old literal (`Units1620SourceIntakeTest.kt` line 39, `Units2630SourceIntakeTest.kt` line 44). It is now asserted on the restored pack (`Units1620` lines 113–116, `Units2630` lines 605–608). The live unit must match `d72afd12` and must not have that digest. This review did not reimplement the canonical-unit digest. The follow-up XML records both methods passed.

## G. Unit 15 fixture

All four Unit 15 failures are `java.lang.IllegalStateException: current advancement evidence is incomplete`, thrown from `LocalStore.v2AdvanceUnitIfCurrentAsync` and reached through `Unit15CumulativeReviewTest.prepare` at line 330 of the pre-correction file. That line was `advanceFrom` for Unit 14 after `answer` on slots 0 and 1 only. The current Unit 14 assignment has three slots.

The repair is one line (`Unit15CumulativeReviewTest.kt` line 330): `fourteenth.slots.indices.drop(2).forEach { answer(p, s, fourteenth, it) }`. `answer` records the item’s real answer. Units 1–13 in `prepare` were already advancing; the trace dies at Unit 14. Unit 15’s own assignment remains two slots, `obj-u1-decoding` and `obj-u14-impersonal`, and its completion, backup, stale-answer, and epoch assertions are unchanged. `pack()` is the live `v2-curriculum.json`. The held-write method still opens Unit 15, replaces the learner epoch, and requires the fingerprint to stay unchanged and no Unit 15 review to be stored.

## H. Evidence integrity and execution boundaries

`ACTUAL_FAILURES_FULL.zip` is 7,064,165 bytes, SHA-256 `d21ba2b001807b92be80f07dd98623fc636e52460dea2c6e538633c15b204532`. Its member `ACTUAL_FAILURES.json` is 147,185,270 bytes, SHA-256 `ca6e78b5e77094119edd207a02b6e6e6f84e39ff4a9381baa394fdaa9a736508`, and CRC-clean. All 14 indexed message and trace hashes match that member. The small `ACTUAL_FAILURES.json` is the index. `READ_ONLY_ALL_FAILURE_TRIAGE.json` stores shorter slices (the kernel detail is 24 bytes; the full message is 50). This review used the full member and the raw XML.

Affected raw XML member hashes in `FAILURE_TRACE_LOCATORS.json` match `RAW_AFFECTED.zip` for the Unit 13 binding, Unit 14 cumulative, and Unit 15 cumulative files. That ZIP CRC-checks clean (154 members).

Recorded executions, counted from CSV and checked against raw XML where noted. These totals are separate and were not added together:

| Run | Source | Reported | Passed | Failed | Errors | Skipped | Classes | Exit |
| --- | --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| Affected runtime | `dd736e1a8657bd02a34063752afd82146ab2f842` | 541 | 526 | 14 | 0 | 1 | 96 | 1 |
| Affected discovery | same | 541 | 0 | 0 | 0 | 541 | 96 | 0 |
| Follow-up runtime | `2faa5471c4cf36728c09684584a255268bd34adb` | 79 | 79 | 0 | 0 | 0 | 13 | 0 |
| Follow-up discovery | same | 79 | 0 | 0 | 0 | 79 | 13 | 0 |
| Broad discovery | `2ebf4f282e42eaa5df8eb3a9e2ac491618e79f96` | 1499 | 0 | 0 | 0 | 1499 | 261 | 0 |

The follow-up raw archive contains 13 JUnit XML files and 79 `testcase` tags, with no failure, error, or skipped child. Recorded `app/src/main` and `app/src/test` tree ids match `git rev-parse` for `dd736e1a`, `2faa5471`, `2ebf4f28`, and `8eb9d3d4`. `app/src/main` is `e8434cfe2b18d483ee27be79aba461add2544f38` on all four. The test tree changes from `b0a5ec5d…` at `dd736e1a` to `2f06c31e…` at `2faa5471` and stays there through the pinned head.

Broad discovery is the Unit 13 broad runtime’s 1477 identities plus the 22 Unit 14 methods in four classes, with zero removals. All 79 follow-up identities are inside those 1499. The four screenshot exporters contribute zero methods. The broad directory has no regression result. Discovery is not execution.

## Inspected and left unverified

Inspected: the Unit 14 prefix of `AGENTS.md` (it matches the continuation log); `audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md` (Unit 14 checkpoint section); the three named evidence directories’ required reports and JSON; the full failure member; affected and follow-up raw XML for the failing classes and the follow-up suite; the seven-file diff; the helper; Unit 14 v2 baseline, UI, compatibility, and binding tests; policy and pack model code; Git blobs for the named producers.

Not verified by rerunning anything: Gradle, JUnit, lint, digest recomputation, APK assembly, or the broad runtime. The broad completion worker was not touched. The private Penguin PDF and private-reference branch were not opened. `/workspace` remained on `cursor/phase-1-data-model-964f` and was not edited. The review checkout is the separate clone `/tmp/rc-review`.
