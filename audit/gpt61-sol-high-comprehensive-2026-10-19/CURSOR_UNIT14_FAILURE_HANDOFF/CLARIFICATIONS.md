# Clarifications

`CURSOR_ACTUAL_FAILURE_RECONCILIATION.md` is preserved unchanged in this directory. The points below do not replace it.

## Preparation failed before the target assertions

Each of the 60 failures entered `ready` → `prepare` on the current shared pack and threw inside `prepare$15` at `advanceFrom(14)`. The only app test frame is that `prepare` continuation. The four current-pack methods in each of Units 16–30 therefore never reached their own target-unit assertions, UI continuation, held-write checks, or backup checks. Those assertions remain in source and remain required.

The 45 schema, old-format, and placement methods in those same classes passed. Schema methods call the same `prepare` on historical packs. Old-format and placement methods do not advance Unit 14 through this helper.

## The helper change preserves the production refusal

`LocalStore.v2AdvanceUnitIfCurrentAsync` still throws `current advancement evidence is incomplete` at `LocalStore.kt:1350` when `advancementDecision` is not allowed. The recommendation does not catch that error, change the gate, change the Unit 14 policy, or mark a review complete without an answer.

It asks the fixture to answer every assigned Unit 14 index after indices 0 and 1, using the existing `answer()` helper, which writes the item's declared answer and asserts that the write returned true. Unit 15 already does this at `Unit15CumulativeReviewTest.kt:330` and passed. On a historical two-slot assignment, `drop(2)` iterates nothing, which matches the schema methods that already passed with only indices 0 and 1.

Answering the remaining index does not prove that the four target methods will pass. Their bodies were not executed. A later assertion failure would be a separate defect, not a reason to weaken the gate.

## Proposed verification is not executed evidence

`FOLLOWUP_SCOPE.csv` lists 119 source methods: 60 failures, 45 passed siblings, and 14 Unit 14 and Unit 15 controls. That inventory was counted from `@Test` declarations and matched to the broad XML. It was not run again for this package. A passing follow-up does not exist yet. This directory records no new Gradle result, emulator result, or device result.

All 48 independent findings remain OPEN. The audit remains INCOMPLETE. Physical acceptance remains last. No merge, freeze, signing, or release approval.
