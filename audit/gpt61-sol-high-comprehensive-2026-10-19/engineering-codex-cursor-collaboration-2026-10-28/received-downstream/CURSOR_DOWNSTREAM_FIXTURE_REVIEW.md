# CURSOR_DOWNSTREAM_FIXTURE_REVIEW

Pin: `8eb9d3d4cc3fdbcd70d5b8904087ddad18acfaf1` (confirmed `git rev-parse HEAD` in the read-only checkout).
Scope: Units 15–30 test classes and the prerequisite helpers they use to pass Unit 14.
This review is static. No test, Gradle task, build, exporter, emulator, or repository edit was run. The active broad runtime was not opened. A source path that throws when advancement is refused is not a recorded test failure, and it is not an explanation of any partial runtime result.

All 48 findings remain OPEN. The audit remains INCOMPLETE. Physical Galaxy S26 Ultra airplane-mode and reboot acceptance remains outstanding and last. This review grants no merge, freeze, signing, or release approval.

## What the current Unit 14 assignment is

The pin asset `app/src/main/assets/content/v2-curriculum.json` is `packId v2-curriculum-foundation`, `packVersion 3`, `schemaVersion 1`. Unit 14 declares `requireDueReview true` and `cumulativeReviewPolicyId unit14-prior-plural-possession-reported-time-v2`.

`cumulativeReviewPolicy(14)` returns `Unit14PluralPossessionCumulativeReviewPolicy.definition` (`V2CumulativeReview.kt:443`). That definition (`V2CumulativeReview.kt:292-299`) has three pools:

1. A copy of `Unit9CumulativeReviewPolicy.definition.pool[1]` (`V2CumulativeReview.kt:185-186`): objective `obj-u8-plural`, skill `SUPPLIED_NOMINATIVE_PLURAL_RECOGNITION`, remediation `u8-st-rem-plural`, candidates `u8-i-plural-1` through `u8-i-plural-6`.
2. `Unit11NounCumulativeReviewPolicy.definition.pool[1]` (`V2CumulativeReview.kt:236-237`): objective `obj-u10-have`, skill `SUPPLIED_POSSESSION_ABSENCE_RECOGNITION`, remediation `u10-st-rem-have`, candidates `u10-i-have-1` through `u10-i-have-4`.
3. `Unit14CumulativeReviewPolicy.definition.pool[1]` (`V2CumulativeReview.kt:286-287`): objective `obj-u13-reported`, skill `SUPPLIED_REPORTED_FRAME_TIME_RECOGNITION`, remediation `u13-st-rem-reported`, candidate `u13-i-report-frame-time`.

`cumulativeReviewDeclaredPolicy` selects that object when the loaded pack's rule id equals it (`V2CumulativeReview.kt:510-515`). `newReviewAssignmentSchema` returns `UNIT14_PLURAL_POSSESSION_REVIEW_ASSIGNMENT_SCHEMA`, which is 16 (`V2CumulativeReview.kt:36-37` and `:597-598`). `createCumulativeReviewAssignment` builds one slot per pool (`V2CumulativeReview.kt:695-713`). Because the schema is not 1, each slot item is `recentReviewCandidate` (`V2CumulativeReview.kt:670-686` and `:703-704`). Slots 0 and 1 can therefore vary among their candidates. Slot 2 has one candidate.

`cumulativeReviewComplete` is true only when `completedReviewSlots(...).size == a.slots.size` (`V2CumulativeReview.kt:843-847`). A correct answer on indexes 0 and 1 leaves index 2 out of that set, so a current eligible 3-slot assignment is incomplete.

`advancementDecision` (`V2ProgressRules.kt:318-326`) is allowed only when `currentUnitRequirements` has an empty remaining list. For a unit with a cumulative-review policy, an incomplete review adds `Complete the assigned earlier-objective review` (`V2ProgressRules.kt:331-335`).

`LocalStore.v2AdvanceUnitIfCurrentAsync` (`LocalStore.kt:1329-1350`) passes the pack through. When `advancementDecision(...).allowed` is false it executes `error("current advancement evidence is incomplete")`. It returns true only after the completion and profile write. A discarded Boolean in the test helper does not turn that error into `false`.

The v1 policy remains in source as `Unit14CumulativeReviewPolicy` (`V2CumulativeReview.kt:283-288`): id `unit14-prior-reading-reported-time-v1`, two pools (`obj-u1-decoding` / `u1-i-decode-pair`, and `obj-u13-reported` / `u13-i-report-frame-time`). `cumulativeReviewDeclaredPolicy` returns that object when the loaded pack declares that id (`V2CumulativeReview.kt:540-541`). `newReviewAssignmentSchema` then takes the final else and returns schema 1 (`V2CumulativeReview.kt:599`). Schema 1 selects `eligible.firstOrNull()`. A fixture that answers indexes 0 and 1 covers every slot of that loaded assignment. `drop(2)` on a 2-slot list is empty.

Units 15–30 on the pin asset each declare their own 2-pool v1 policy, matching `Unit15CumulativeReviewPolicy` through `Unit30CumulativeReviewPolicy` (`V2CumulativeReview.kt:302-428` and `:444-459`). Their target-unit tests answer slot 0, assert advancement false, answer slot 1, and assert advancement true. Those assertions match the target policy they load.

## How a downstream fixture loads and answers Unit 14

Each `Unit15CumulativeReviewTest` through `Unit30CumulativeReviewTest` has its own `prepare`, `ready`, `answer`, and `advanceFrom`. None of them call `Unit6PresentFixtures` through `Unit14PluralPossessionFixtures`. Those shared fixtures answer `task.slots.indices` and assert the advance Boolean. `Unit14PluralPossessionFixtures.prepare` (`Unit14PluralPossessionFixtures.kt:21-32`) is a different scenario: it reloads `PARTIAL_UNIT14_V1.zip`, removes the terminal Unit 14 assignment, and repeats ordinary Unit 14. It is not the helper under review.

`pack()` returns `sharedPack`, parsed from the pin asset. `ready(sharedPack)` caches one document built by `prepare(sharedPack)`. `answer()` writes `pack.item(slot.itemId).answer` through `answerCumulativeReview` and asserts that the write returned true (`Unit15CumulativeReviewTest.kt:344-347`, with the same body in each later class).

`prepare` answers earlier reviews as follows, identically in all 16 classes except the Unit 14 line in Unit 15:

- Units 3, 4, 5, 7, 8, 10, 11, and 12: indexes 0 and 1.
- Units 6, 9, and 13: `slots.indices.forEach`.
- Unit 14 in Unit 15: indexes 0 and 1, then `slots.indices.drop(2)`, then `advanceFrom(14)` (`Unit15CumulativeReviewTest.kt:328-331`).
- Unit 14 in Units 16–30: indexes 0 and 1, then `advanceFrom(14)`.
- Each later prerequisite through the class target: indexes 0 and 1, which matches the current 2-pool policies for units 15–29.

The current asset policy ids have these pool counts: unit 3 `unit3-prior-literacy-v1` 2; unit 4 `unit4-prior-reading-gender-v1` 2; unit 5 `unit5-prior-gender-subject-v2` 2; unit 6 `unit6-prior-present-subject-noun-behavior-v3` 3; unit 7 `unit7-prior-gender-inanimate-case-v2` 2; unit 8 `unit8-prior-present-nominative-agreement-v2` 2; unit 9 `unit9-prior-noun-behavior-plural-gender-v3` 3; unit 10 `unit10-prior-present-numeral-gender-v2` 2; unit 11 `unit11-prior-noun-behavior-possession-absence-v2` 2; unit 12 `unit12-prior-gender-past-gender-v2` 2; unit 13 `unit13-prior-present-aspect-past-gender-v3` 3; unit 14 v2 3; units 15–30 the 2-pool v1 ids. On the current asset, the forEach and two-answer split matches those counts for units 3–13 and 15–29. The current-asset mismatch is Unit 14 inside Units 16–30 `prepare()`.

`advanceFrom` in Units 15–29 computes the next unit as `n + 1`. Unit 30 uses `val next = (n + 1).coerceAtMost(30)` (`Unit30CumulativeReviewTest.kt:418-421`), so completing Unit 30 stays on Unit 30. That terminal behavior is separate from the Unit 14 slot count.

## Prioritized observations

1. Current progression in Units 16–30 leaves a required Unit 14 slot unanswered. `ready(pack())` loads the pin asset. `prepare` opens Unit 14, answers indexes 0 and 1, and calls `advanceFrom(14)` with the Boolean discarded. The loaded policy has three slots. The completeness predicate requires all three. The advance implementation throws when the decision is not allowed. The completion-set assertion later in `prepare` and `ready` runs only if that call returns. Methods on this path are the ordinary-completion, optional-replay, actual-continue, and held-write methods. Line numbers are in the CSV.

2. Unit 15 current progression answers every loaded Unit 14 index. `Unit15CumulativeReviewTest.kt:330` answers `drop(2)` before `advanceFrom(14)`. On the pin asset that covers slot 2. On a 2-slot pack the extra loop is empty. Its own target asserts remain the 2-slot Unit 15 policy: objectives `obj-u1-decoding` and `obj-u14-impersonal`, item `u14-i-impersonal-2` (`Unit15CumulativeReviewTest.kt:44-49`).

3. Historical schema methods load v1, where two answers cover the assignment. Each `schema11...` method calls `prepare(oldPack(commit))` and later `ready(preTeachingCorrectionPack)`. All 16 `oldPack` commits were parsed. Each has Unit 14 `requireDueReview true` and policy `unit14-prior-reading-reported-time-v1`. `preTeachingCorrectionPack` is git `547457b63b317dea560e4bdc36083c1a4dce315e` (`Unit3GenderTeachingTest.kt:199-207`) and parses to the same v1 id. Those commits are ancestors of `35678e4bad772b39bc4e8696d016869569fc014b` (2026-10-28 09:51 America/Chicago), the commit that introduces the v2 policy string. They contain the v1 id once and the v2 id zero times. Indexes 0 and 1 satisfy the assignment those packs declare. Unit 15's `drop(2)` adds no third answer on those packs. These methods do not open the pin asset's schema-16 assignment.

4. `oldFormatOneAndTwo...` does not open a Unit 14 review. It uses `oldPack("20a227817562e3a8645488fd52db2e7589713a7a")`. That curriculum has a Unit 14 object with `requireDueReview false` and `cumulativeReviewPolicyId null`, so `cumulativeReviewDeclaredPolicy` returns null (`V2CumulativeReview.kt:510-513`). The method completes units with `ordinary` and `v2CompleteUnit` and asserts empty review history. Two-slot and three-slot answer rules describe a review this method does not create.

5. Placement methods load the pin asset and leave Unit 14 untaught on purpose. They complete units 1 and 2, open the target unit from course placement, and expect the missing-owner repair. They do not call `advanceFrom(14)`.

6. Target-unit 2-slot asserts in Units 15–30 match the policies those tests open. The candidate change is the Unit 14 block inside Units 16–30 `prepare()`, after runtime completion. The candidate is the Unit 15 `drop(2)` line, or `fourteenth.slots.indices.forEach`. Preserved asserts are listed per row in the CSV. Unit 30 also preserves `minimumDistinctChecks == 12` and `coerceAtMost(30)`.