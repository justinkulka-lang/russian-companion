# Independent review of the applied Unit 14 fixture patch

## Conclusion

The fixture delta between baseline `5b5ae5b654894d8daf3054ee55c6a760b1df0470` and review head `52cb624978fa99be9b8c2162a51f8e09c13d2c01` is one inserted line in each of `Unit16CumulativeReviewTest.kt` through `Unit30CumulativeReviewTest.kt`. The line is:

`fourteenth.slots.indices.drop(2).forEach { answer(p, s, fourteenth, it) }`

It is the statement already present in unchanged `Unit15CumulativeReviewTest.kt` at line 330. In every Unit 16–30 file it is the next line after `answer(p, s, fourteenth, 0); answer(p, s, fourteenth, 1)` and the line immediately before `runBlocking { advanceFrom(p, s, 14) }`. Insertion lines are 328 for Units 16–20, 333 for Unit 21, 334 for Units 22–29, and 340 for Unit 30.

Deleting that single line from each head blob restores the baseline blob byte for byte. The same fifteen blobs are identical at correction commit `9b947e06c347cd278ca36e2c894dc0eb5bbaaffb` and at the review head, so the later discovery commit did not alter the fixtures.

Across the fifteen files the source assertion inventory is 1,330 calls before the patch and 1,330 calls after it. Call text matches in order. Per file the counts are 87 (Units 16–20), 88 (Unit 21), 89 (Units 22–29), and 95 (Unit 30). Each file keeps its seven `@Test` functions. Every string literal matches, including the historical `oldPack` pins and `547457b63b317dea560e4bdc36083c1a4dce315e`. Unit 30 `advanceFrom` still sets `val next = (n + 1).coerceAtMost(30)`.

`app/src/test` contains 281 files on both sides. The fifteen files above are the only changed test files, so the other 266 are byte-identical. The 281 are 280 Kotlin sources plus `app/src/test/resources/reviewed-curriculum-cyrillic.json`. The `app/src/main` tree is `e8434cfe2b18d483ee27be79aba461add2544f38` on both sides. `LocalStore.kt` SHA-256 `7b255cc434d66d9101a55bfd328461fc3869d11a117db0b4f798f06997e26637`, `V2CumulativeReview.kt` SHA-256 `cb279ccc1eaeec8d34ad7e51cccf6860b984f96549ae0bf347d538a1edfa483a`, and `v2-curriculum.json` SHA-256 `02202d37e0aa2c51182e4db22feff177124b6db2c270a67a93ef85e188916c19` match the baseline.

The retained preparation record lists 155 binding paths, 77 archive blobs, and 146 prior-failure-evidence paths. Each recorded SHA-256 matches the baseline blob and the head blob. The 47 files in `engineering-unit14-broad-host-regression-2026-10-28` are inside those 146, including `ACTUAL_FAILURES.json`, `ACTUAL_FAILURES_FULL.zip`, `RAW_BROAD.zip`, and `REGRESSION_RESULT.json`. `ORIGINAL_FIXTURES.zip` SHA-256 `7cbc5aa1715fc19abe089813fff016883fa668ca8feae85fef5ae39a4420088c` holds the fifteen baseline test files, and each member equals its baseline blob. The fifteen `FIXTURE_CORRECTION_REVIEW.json` old and new SHA-256 values match this independent recount. `FILE_CONSERVATION.csv` has a header and one data row for each of these fifteen files.

## Behavior

`prepare()` opens Unit 14 with `openCumulativeReview(p, 14, ...)`. Slot count follows `cumulativeReviewDeclaredPolicy`. The current pack declares `unit14-prior-plural-possession-reported-time-v2`, whose policy object has three pools. Assignment construction emits one slot per pool, so `indices.drop(2)` is index 2. The new line calls `answer()` for that remaining assigned slot and then calls `advanceFrom(14)`.

Every pack that these tests actually pass into `prepare()` other than the current pack declares `unit14-prior-reading-reported-time-v1`. That set is pre-teaching commit `547457b63b317dea560e4bdc36083c1a4dce315e` and the fifteen schema pins from `85591389950bdfa388d7a18283205edd8feb61aa` through `8a2f6c9a12c97318d78a26c051d045418345ac7d`. The v1 policy object has two pools. `drop(2)` is empty, and `forEach` does not call `answer()`.

Old-format pin `20a227817562e3a8645488fd52db2e7589713a7a` has `requireDueReview` false and no policy id. Those tests call `ordinary()` and do not call `prepare()`. Placement tests do not call `prepare()`. The inserted line therefore does not run on those paths.

`answer()` still asserts that `answerCumulativeReview` returned true, using a correct response by default, which is the same default the original index 0 and index 1 calls use. `v2AdvanceUnitIfCurrentAsync` is unchanged and still errors with `current advancement evidence is incomplete` at `LocalStore.kt:1350` when a pack is present and `canOpenUnit` is false or `advancementDecision` is not allowed. The fixture does not catch that error.

`drop(2)` answers every assigned index at or above 2. Under the policies these tests load, that is one slot for the current three-pool assignment and no slot for a two-pool assignment.

The new line adds executions of the existing `answer()` helper when a third slot exists. That helper body contains `assertTrue`. The 1,330 source-level `assert*` and `fail(` call expressions stay text-identical. The additional execution checks that the extra slot write succeeded. It does not replace a target-unit assertion.

## Substantiated concerns

The fifteen fixture files show no hidden assertion edit, historical pin substitution, deleted negative, placement, schema, or old-format case, or changed Unit 30 terminal expression.

Commit `9b947e06` also adds the follow-up evidence directory and prepends a checkpoint paragraph to `AGENTS.md`, `audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md`, and `audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md`. Commit `52cb6249` prepends a second checkpoint paragraph to those three files and adds the discovery records. `STATUS.md` describes the correction as fixture-only. The `app/src/test` delta is the fifteen lines. The commits also contain those checkpoint paragraphs and the evidence directory.

At the review head, `STATUS.md` still says fresh discovery and actual runtime remain not run. `DISCOVERY_RESULT.json` records source `9b947e06`, `syntheticDryRun` true, exit 0, and counts reported 119, passed 0, failed 0, errors 0, skipped 119, with `completeSuite` false and `allFreshSelectedMethodsReported` false. `DISCOVERY_SCOPE_VERIFICATION.json` records 119 synthetic skips and 0 runtime passes, with the 60 failed ids, 45 siblings, and 14 controls included. The discovery JSON is the discovery record. Neither file is a runtime result. The active follow-up runtime was not inspected and its outcome is not inferred.

The assertions that sit after `prepare()` in the previously failing methods are still present and were not executed in this review. Satisfying the Unit 14 prerequisite inside `prepare()` leaves any later failure able to surface, because the production throw and the target assertion text remain. This package does not claim those methods pass.

## Scope and limitations

Reviewer and model: Grok 4.7. Effort was not a separately observed setting.

Baseline is `5b5ae5b654894d8daf3054ee55c6a760b1df0470`. Correction commit is `9b947e06c347cd278ca36e2c894dc0eb5bbaaffb`. Review head is `52cb624978fa99be9b8c2162a51f8e09c13d2c01`. Parents are baseline to correction, then correction to head.

Inspected: the fifteen fixture diffs and byte restoration; assertion, `@Test`, and string-literal equality; Unit 30 `coerceAtMost(30)`; all 281 test-file blobs; the `app/src/main` tree; the 155, 77, and 146 retained hashes; `ORIGINAL_FIXTURES.zip`; Unit 14 policy objects and the advancement fields of the current pack and the historical pins named by the tests; `prepare`, `answer`, `advanceFrom`, and `LocalStore.v2AdvanceUnitIfCurrentAsync`; the follow-up preparation, fixture-review, discovery, ownership, and status records; the checkpoint diffs in `AGENTS.md` and the two content-correction notes.

Uninspected as execution: tests, Gradle, emulators, devices, and every received script under the follow-up directory. The live runtime process, its PIDs, and any uncommitted external workspace output were left untouched. `repositorySourceDigest` and `packContentReviewDigest` were not recomputed. Members inside the 77 archives were not re-expanded. The prior broad-failure trace investigation was not repeated.

No-execution status: this review executed no tests and no received scripts. Discovery counts above are quoted from retained JSON. They are not a runtime verdict.

All 48 findings remain OPEN. The audit remains INCOMPLETE. Physical Galaxy S26 Ultra airplane-mode and reboot acceptance remains outstanding and last, after the final independent comprehensive nonphysical audit. No Unit 15 curriculum implementation, APK assembly, install, signing, merge, freeze, or release approval.
