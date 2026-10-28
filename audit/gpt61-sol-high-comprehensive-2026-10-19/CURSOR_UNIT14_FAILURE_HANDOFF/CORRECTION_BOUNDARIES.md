# Correction boundaries

Do not treat this file as an applied edit. No test file was changed in this delivery.

## Candidate files and insertion points

All paths are under `app/src/test/java/dev/russiancompanion/v2/`. Insert one new line after the existing index-0/index-1 answer and before `runBlocking { advanceFrom(p, s, 14) }`.

| File | Keep this answer line | Insert before this advance line |
| --- | --- | --- |
| `Unit16CumulativeReviewTest.kt` | 327 | 328 |
| `Unit17CumulativeReviewTest.kt` | 327 | 328 |
| `Unit18CumulativeReviewTest.kt` | 327 | 328 |
| `Unit19CumulativeReviewTest.kt` | 327 | 328 |
| `Unit20CumulativeReviewTest.kt` | 327 | 328 |
| `Unit21CumulativeReviewTest.kt` | 332 | 333 |
| `Unit22CumulativeReviewTest.kt` | 333 | 334 |
| `Unit23CumulativeReviewTest.kt` | 333 | 334 |
| `Unit24CumulativeReviewTest.kt` | 333 | 334 |
| `Unit25CumulativeReviewTest.kt` | 333 | 334 |
| `Unit26CumulativeReviewTest.kt` | 333 | 334 |
| `Unit27CumulativeReviewTest.kt` | 333 | 334 |
| `Unit28CumulativeReviewTest.kt` | 333 | 334 |
| `Unit29CumulativeReviewTest.kt` | 333 | 334 |
| `Unit30CumulativeReviewTest.kt` | 339 | 340 |

The answer line is:

`answer(p, s, fourteenth, 0); answer(p, s, fourteenth, 1)`

The recommended inserted line, already used by Unit 15 at line 330, is:

`fourteenth.slots.indices.drop(2).forEach { answer(p, s, fourteenth, it) }`

That is the whole candidate. It answers remaining assigned indices. It does not replace the answers to indices 0 and 1.

## What stays unchanged

- Every original assertion in the 60 failed methods and in the 45 siblings.
- Literal policy ids, commit pins, and `oldPack` / `preTeachingCorrectionPack` arguments.
- Historical packs and their schema methods.
- Target-unit policies and the two-slot target answers inside each test method.
- Negative scenarios, including wrong answers, packless advance rejection, injected advance failure, and book-attestation removal.
- Old-format methods, which do not open a Unit 14 review.
- Placement methods, which leave Unit 14 untaught on purpose.
- `advanceFrom`, including Unit 30 `val next = (n + 1).coerceAtMost(30)` at `Unit30CumulativeReviewTest.kt:419`. The observed failure is `advanceFrom(14)`, where the next unit is 15, so that terminal clamp is not the failing call.
- `LocalStore.kt`, curriculum JSON, and Unit 14 and Unit 15 tests. Unit 15 is the control that already contains the extra loop.

## Why the extra loop is empty on historical two-slot assignments

`drop(2)` yields only indices at or after 2. A Unit 14 assignment with two slots has indices 0 and 1, so the loop body does not run. The schema methods in Units 16–30 call this same `prepare` on packs whose Unit 14 policy is `unit14-prior-reading-reported-time-v1` and passed in the broad run. Those passing runs are the observed two-answer behavior. Adding an empty loop does not add a third answer on those packs.

The current shared pack declares `unit14-prior-plural-possession-reported-time-v2` with three pools. Indices 0 and 1 leave one assigned slot unanswered, and `advanceFrom(14)` then hits the unchanged refusal at `LocalStore.kt:1350`.

## Unresolved reason to alter the recommendation

None was found that calls for a different edit. Two limits remain, and both are reasons to run the follow-up rather than to change the candidate:

- The exception text does not print the unmet-requirement list. The stack, the answer line, the passing Unit 15 control, and the passing v1 schema methods support the missing later index. They are not a captured slot dump.
- After preparation succeeds, the 60 target bodies will run for the first time on this pack. A failure there would be new evidence and would not be fixed by widening this helper change.

Do not answer Unit 14 inside old-format or placement methods. Do not switch these tests onto `Unit14PluralPossessionFixtures`. Do not implement Unit 15 curriculum work as part of this correction.
