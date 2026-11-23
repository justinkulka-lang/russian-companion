# Codex backlog (paused 11/21 19:31 CT: out of credits)

Send these in order when Codex resets. Each is read-only. Combining items into one check is fine if they become due together.

## 1. T16c check: DONE 11/23 (FAIL on items 1, 3, 11; fixes in T16c-n1 per D36). Report: t16c-codex-check-at-4732787a.txt
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

## 2. T16d check: DONE 11/23 (FAIL on lint-test coverage and leaks; fixes in T16c-n1 per D37). Report: t16d-t16e-codex-check-at-23c636c4.txt
Commits: d8af77f8 (D32b UI strings), f5861810 (Unit 14 content), 860e96e4 (pins). Head when pushed: 860e96e4; CI pending.
- D32b: confirm "Not the reviewed answer." is gone (V2PracticeFlows.kt:195, V2Screens.kt:644) and that the three further fixes are present (V2Screens.kt:761, :849; V2UnitTestScreen.kt:24). Then check the three exemption kinds in UiStringLintTest:
  - the D31b labels;
  - the V2AnswerFeedback.kt rationale fragments. Code says these are "never displayed". Verify that independently: no path renders them.
  - the identifier-like literals. The exemption must not hide learner-visible text.
  Finally, run your own probe over every UI literal.
- Unit 14:
  - 20/14 counts; typed keys and their exact stress.
  - The full future paradigms are taught before the checks: future-tense:41/44/47, verbs.csv:3/245/1485.
  - Facts: 10 MARK / 3 REWORD. Check S1 PARTIAL 1554 and 1559 against Вид:4, Grammatical aspect:86 and past-tense:16-17.
  - быть is taught as imperfective, and the source's "perfective быть" clause is not used.
  - 18 phrases; the 62 sweep rows; zero lint hits.
  - References, binder, tutor; A2-28 record 8,8,8,8,1,0.
  - The transformer trim fix for trailing spaces in teaching cards changes nothing else.
- Pins and hashes, recomputed.

## 3. T16e check: DONE 11/23 (content PASS; review-copy and readiness leaks go to D37)
- 20 checked / 14 typed. Check the keys (verbs.csv:3/6/19/20/30/62) and the new teaching card u15-st-teach-aspect-consolidate-4.
- u15-i-imperative-2 is redesigned to the plural imperative. Confirm it no longer duplicates another check's answer.
- 87 of 90 sweep rows applied. Confirm that the 3 skipped rows really were already met.
- Facts: 7 MARK / 12 REWORD / 2 REMOVE. Check the Повелительное наклонение (152579064) citations on markers 1593, 1600 and 1627.
- D4 gap sentence; references, binder and tutor; A2-28 record 8,8,8,8,2,0.
- Test changes in Unit15AspectPreparationTest and Unit15FeedbackCorrectionTest are legitimate. Recompute pins and hashes.

## 4. T16f check: Block 3 close-out (DUE NOW: 16350eb1, 64d04b4b, 143e5730; DONE f2ad5aad marks Block 3 complete; CI 45067573646 passed on 143e5730)
- block3.html: 3 clips (дать, дом, стол). Confirm that leaving out cue/teaching-only uses (быть, ли) is correct under the listen-check rule.
- WC005: labels in Units 12, 14 and 15 come from frozen table fields. CC021: all 100 Units 11-15 checks are covered by remediation. CC024: сказа́ть is glossed "say, tell". WC004: the nationality reports use the source's English. WC009: links.
- 22 disposition rows: 16 FIXED (check each fix exists), and BOUNDED_SCOPE or DEFERRED rows that are honest. 40 CC036 rows, justified individually.
- Block 3 is literally complete against WORK_QUEUE.md lines 161-166.

## 5. T17a check: D34 and Unit 16 (due once T17a is DONE)
Commits: 6d6fd938 (D34), 569341cc (Unit 16), 43996033 (pins), then later commits.
- D34: locators §15.8 p177/PDF189, ex 8/3 p89/PDF101 and ex 10/1 p109/PDF121. Check the +12 offset against the existing mappings. The CC032 book-only lists per unit are accurate.
- Unit 16: 20/8; 52 MARK / 12 REWORD / 2 REMOVE; the S1 claim (line 1766) cites Wiktionary под 92761073 lines 106, 135 and 136.
- Shipped text takes the "longest clean stretch" of each cited line. Confirm each one is still a verbatim contiguous substring of the frozen line.
- Departures: the two means checks stay at 2 options (prompt-letter rule), and the "became" explanations stay. Confirm neither is a weakening.
- ref-u1620-attribution edits ("frozen" changed to "pinned", "Proposed" dropped). The Units 18-20 digests move only because of that.
- CI fix f9d776ae (run 45091734015 failed on a03fe699): Units1620SourceIntakeTest now accepts T17a records from the S1 and Units 21-25 freezes through the D7b verified path (verbatim in the manifest-named file). Confirm this is no weaker than D7b, that only those four records use it, and that a record from an unlisted freeze or revision is still rejected.

## 6. T17b check: D1 and Unit 17 (DUE NOW: 246cc437 D1, b0216798, 561aa5af, 102e0ed9; DONE e88562bd; CI 45178861958 passed)
- D1: the validator's count-question rule moves from layout wording ("two-hour row") to meaning. It still rejects a wrong count and a missing count context. Guard hash c800faf9 -> ba035cd3. Run your own probes.
- Unit 17: counts, the 208 sweep rows, the ReadyRussian Grammar 24 citations (only the CC BY-SA handout portion), facts, phrases, references, the A2-28 record [8,8,8,8,3,0].
- Facts: 180 MARK / 11 REWORD / 6 REMOVE. The S1 claims 1920, 1947 and 1948 cite ReadyRussian Grammar 24. Typed keys come from numbers-4371358.txt:109-130.
- Departures: no third options were added, and the "Choose <answer>" sentences are removed. Confirm the binary checks are justified under CC036. Check the Aud44CorrectionTest change.
- Test moves in UnitTestAndBinderTest (examples shift to Units 18 and 22) are legitimate. Recompute the pins.

## 7. T17c check: Unit 18 (DUE NOW: 6810533d, 70aaf80e, 675d531b; DONE ac0b7766; CI 45228317272 passed)
- 20 checked / 16 typed. Typed keys: grammar:301, 303 and interrogative:168-173. Two typed guided items (са́мый до́брый, Который). Facts 44 MARK / 1 REMOVE. Check the section 4 corrections (simple comparative; the superlative gloss; бо́лее до́брый is rare).
- Test changes to verify as not weakened:
  - Unit18ComparisonRelativeInstructionTest: governance and scope expectations removed. Confirm the six closed-item protections are intact.
  - BatchAUnits71620Test: 47 closed items are still checked as before, and 18 typed items are asserted separately. Confirm the typed items get real assertions.
  - Unit19CumulativeReviewTest: typed guided items are typed in through v2-typed-<id>. Confirm the closed path still clicks.
- Wording: positive forms in parentheses, са́мый in the "most" contrast, and practice/check instructions that cover typed answers. Confirm no Russian was composed.
- Unit 18 digest 04147a35 -> 114c6675 (pinned in Aud44, Units2125 and Units2630 intake tests).

## 8. T17d check: D2 and Unit 19 (due once T17d is DONE; D2 commit fecc7ab4)
- D2: governedRelation now lets probability-1's wrong options be competing meanings from other verified frozen records (not content-audit, synthesis or coverage, and displayed as frozen text). The cue and answer must still come from W:39-41. Guard hash ba035cd3 -> 7dd3d3c4. Run your own probes against Unit19ProbabilityBindingRuleTest: an unsourced meaning, a tampered record, a Russian distractor, an answer outside the span. Confirm no other governed span changed.
- Unit 19: 20/12; facts 53/7/7. S1 claims: 2225 (Сослагательное:18), 2234 reworded to "sentence" (бы:58). 48 sweep rows. "Choose <answer>." sentences are treated as non-facts; confirm none carries a claim. The builder's unstressed() is now aligned with the Kotlin phrase rule for plain [[...]] links.

## 9. T16c-n1 check (due once T16c-n1 is DONE): D36a-c. Re-run the S1 wrong-revision, row/table and disabled-assertion probes, and scan for any other disabled assertions.

## 10. Later unit checks
Add one entry each time a unit task reports DONE.
