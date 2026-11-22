# Codex backlog (paused 11/21 19:31 CT: out of credits)

Send these in order when Codex resets. Each is read-only. Combining items into one check is fine if they become due together.

## 1. T16c check: D29a, T16-n2, Unit 13, CI fix (due now)
Branch cursor/content-audit-corrections-964f at 4732787a. Commits:
- adbc7a80: D29a
- b1c94322 + ab5e8633: T16-n2
- e4245f3b + ea7c5669: Unit 13
- c15e0bdb: CI fix

CI: 44946188080 failed on ea7c5669; 44959344968 passed on c15e0bdb.

Checks, using the full prompt the coordinator gave on 11/21 19:09:
1. S1 admission is exact.
2. D33: no pre-D29a source-specific check is weakened. Run a ghost probe against every S1 source.
3. Lint categories: check false-positive and false-negative balance, especially the broad "row(s)" rule.
4. Spot-check 30 sweep rows and the 57 extra fixes.
5. D31b status labels.
6. Manual scan of Units 1-13.
7. Unit 13 counts and keys, including the new teaching card u13-st-teach-past-aspect-5.
8. Facts and S1 verdicts; the Вид:9 sentence.
9. Unit 13 sweep rows and mapping note.
10. References, tasks and A2-28.
11. CI fix, plus pins and hashes.

## 2. T16d check: D32b UI strings, Unit 14 (due when T16d is DONE)
Check:
- Every learner-visible UI string literal has been linted and fixed.
- "Not the reviewed answer." is gone.
- A UI-literal lint test exists.
- Then the standard unit check for Unit 14, with S1 verdicts (aspect and Повелительное наклонение) and the Unit 14 sweep rows.

## 3. Later unit checks
Add one entry each time a unit task reports DONE.
