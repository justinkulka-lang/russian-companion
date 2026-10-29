# Independent whole-curriculum diagnostic audit — Russian Companion v2

**Verdict: INCOMPLETE.** All curriculum entities in scope were read and given a verdict: 30 units, 1,270 steps, 626 items, 821 examples and 58 capability objects. The audit is still incomplete for four reasons: it executed no tests; no qualified native-linguistic or rights review took place; third-party source spans were not re-fetched; and physical-device acceptance stays last by design. Every finding is OPEN. This is a diagnostic. It is not an acceptance, a closure or a release approval.

Audit date: 2026-10-29. Auditor: a Claude Code cloud session working as one agent with no subagents, independent of the Codex engineering executor. The model and effort level come from session metadata and are given in the chat reply that delivered these files. Session policy keeps model identifiers out of repository files.

## 1. Identities

| Item | Value |
|---|---|
| Audited source commit | `5a3411a76b88753617ea18b3e1a54f44f6923b9f` (tree `f7aaebcf…`, 11,893 tracked files) |
| Input trees | app/src/main `4a10247f`, app/src/test `c43ea562`, catalog `af307958`, docs `7364239d`, review `3cb94ec5`, release `81e1b4ac` |
| Raw curriculum | `app/src/main/assets/content/v2-curriculum.json`: 20,350,052 bytes, SHA256 `02202d37e0aa2c51182e4db22feff177124b6db2c270a67a93ef85e188916c19` |
| sourceTextPool | 438 entries, 0 bad hashes; 3,581 references (3,455 inline); 0 reference errors; 0 unused |
| Later commits, kept distinct | `95891358` (broad-run evidence) and `734d8da6` (work order; PR 64 head). Neither is the audited source. |
| Change since the last audit (8606c4d7) | In the curriculum, only `units[4..13].advancement.cumulativeReviewPolicyId` (Units 5–14) changed. Engineering changes are in 9 main files plus CI and the Gradle wrapper. |
| Private Penguin PDF | 13,596,737 bytes, SHA256 `2bcc0c45fac4508adf283f4dd939a7e578cf2c1306fdadf4966300532beb6303`. It was obtained through the documented private-reference access and kept outside the checkout. The PDF, page images and book prose appear in no output. |

## 2. Inspected scope and method

- **Recomputed counts:**
  - 30 units, 195 objectives, 228 sessions.
  - 1,270 steps: TEACH 574, GUIDED 168, CHECK 164, REMEDIATE 150, ORIENT 60, BOOK 60, ANKI/BINDER/TUTOR 31 each, SPEAK 1.
  - 626 items: 429 required checks, 180 guided, 14 lab-only, 3 optional.
  - 821 examples, 58 capability objects, 37 sources/rights, 27 references, 33 letters, 7,036 provenance records, 1,655 authored-English records, 30 mappings.
  - The capability count is 58. The handoff figure of 37 is the source/rights count.
- **Reading method:**
  - Every unit was rendered from the raw JSON as the learner sees it: step bodies, examples, prompts, cues, options, keys, distractor rationales, feedback, objective and assessment claims, book tasks and mappings.
  - Each render was read against the raw fields.
  - Where display, grading, remediation routing, review credit or advancement depended on Kotlin, that code was read: V2CumulativeReview, V2ProgressRules, V2AnswerFeedback, V2OptionPresentation, V2CumulativeReviewScreen, V2Validator and the delta files.
- **Recorded verdicts:**
  - Each of the 626 items and 58 capabilities has an individual verdict and defect codes in ITEM_AND_CAPABILITY_COVERAGE.csv.
  - Items without a recorded defect are marked "no defect found in this reading", never PASS, because all of them still await the Amendment 1 review chain.
- **Independence from prior work:**
  - Neither earlier conclusions nor preparer inventories were relied on.
  - All counts were recomputed, and the mechanical measures can be reproduced with the supplementary `mechanical_analysis.py`.

## 3. Cross-course conclusions

The defects follow a small number of course-wide root causes. The same patterns repeat in most units, so per-unit patching would not fix them. Corrections should therefore be grouped by root cause (Section 6), not made unit by unit.

### 3.1 Most required checks do not measure the target knowledge

330 of 429 required checks (77%) carry a recorded defect:

- **289 have a validity defect.** The answer can be found without the target knowledge, the item does not test the construct it claims, or the tested content was never taught.
- **41 have only feedback, text or format defects.**
- **The rest:** 98 had no defect found (28 of them with noted limitations), and 1 is a concern.

The recurring mechanisms (CC027, CC036, CC010/CC030, CC026, CC028, WC007) are:

- **Cue repeated as the only distractor.** In 39 required checks the only distractor is the cue itself, so "pick the option that is not the cue" always scores. In 42 the cue appears among the distractors.
- **Two-option checks.** 208 required checks (48.5%) have two options, a 50% guess rate.
- **Constant keys.** Nine gating objectives have the same key in every check: u4-nominative, u7-adverb-invariance, u11-past-be, u15-imperative, u24-concord, u24-placement, u25-name-question, u26-vy-capital and u29-active.
- **Repeatable strategies.**
  - The -ый dictionary form is always the key in 12 Unit 7 items spread over five objectives.
  - Unit 1 key labels contain the cue glyph.
  - Infinitive-only distractors in Unit 15, longer-option keys in Unit 9, and pronoun give-aways in Units 11, 12 and 14.
- **Duplicates.** 31 groups of identical tasks (66 items, 41 of them required checks) share the same cue, options and key. 18 groups cross units: Units 1↔2, 6↔10, 7↔8, 12↔13↔15, 12↔14, 26↔30 and others. The original 8 CC010 groups all persist.
- **Meta and label recall.** 72 items ask about the app's own scope, source labels, line positions or check design instead of Russian. These are heaviest in Units 7, 13 and 24–30.
- **Impossible distractors.** 43 items use semantically impossible distractors such as role reversals or "the table wrote the letter", in Units 17, 20 and 26–29.
- **No Russian at all.** 11 required checks contain no Russian, and the Unit 25 diminutive checks are English transliteration matches.

### 3.2 Teaching gives the answers instead of teaching rules

- **Answers announced (CC022, CC015).** In 136 items (116 required checks) the answer is stated in prior teaching, for example "when the check asks for X, choose Y". The share of required checks is highest in Unit 16 (10/12), Unit 13 (7/9), Unit 20 (6/8), Unit 28 (4/6) and Unit 17 (15/25). In Units 4, 26, 27 and 29 the teaching for whole objectives (present forms, word order, stress, punctuation) states every answer.
- **Rules withheld.** At the same time the transferable rule is often missing:
  - Unit 3 tells learners not to infer gender from endings, although the book teaches gender rules (§3.8).
  - No Unit 4 conjugation pattern.
  - No Unit 5 prepositional ending.
  - No Unit 12 dative endings.
  - No Unit 15 imperative formation.
- **Content tested but never taught (WC005).** 27 items test meanings, endings or labels that no earlier step presents.
- **Remediation does not cover the checks (CC021).**
  - In 71 required checks neither the cue nor the key appears in the remediation.
  - Eleven objectives have no remediation coverage at all.
  - Many remediation bodies are a single raw wiki-markup fragment (CC023).
- **Later material appears early (WC006, CC031).**
  - Past-tense forms appear in Units 6–10.
  - Genitive is checked in Unit 7.
  - The aspect term appears in Unit 4.
  - Instrumental and dative plurals appear in Unit 12.

### 3.3 Learner-facing text is unreliable, and more of it is now visible

- **Rationales are now shown (WC001).** CC034's correction made distractor rationales visible to learners. Mechanically, they include:
  - 97 template splices that do not explain the chosen option;
  - 24 "Rejected reading:" restatements;
  - 15 rationales that name the wrong "shown form";
  - 91 wrong options in Units 1–5 that have no rationale at all.
- **Rationale content errors.** Reading found content errors as well: Unit 24 calls не́кто/не́что "predicative", which contradicts its own teaching.
- **Accuracy and gloss defects (CC024, CC025, WC004):**
  - "Will speak" is keyed for скажу/скажем.
  - Unit 11 contradicts itself about the past tense of быть.
  - Unit 20 stresses у́летят (needs qualified confirmation).
  - Examples are glossed with themselves, with grammatical labels or with other Russian fragments (Units 6, 9, 13, 17, 19, 23, 28, 30).
  - Genitive pronouns are glossed as nominative (Unit 10), and plurals as singular (Unit 8).
- **Governance and engineering wording (CC029).** Words such as admitted, frozen, retained and supplied row appear in the prompts or feedback of 358 items. Some learner text narrates check design or engineering history.

### 3.4 In-app scope versus the book was never decided course-wide

Book topics are missing, relabelled or only partly covered:

- Units 1–2 contain no pronunciation, listening, transliteration or handwriting content (CC002).
- Contextual imperative aspect is missing (CC003).
- Purpose clauses are missing (CC004).
- Surname declension is missing (CC005).
- General fractions are missing (CC006).
- "Bringing" verbs (при-) are missing (CC007).
- Unit 8 existence questions and buying are missing (CC012).
- CC032 lists further gaps: Units 9–11, 14, 16, 18, 19, 21, 26 and 30.

Reading objectives in Units 28–30 never show the text being assessed (WC003). Unit 26 teaches a postal convention that differs from the owned edition; it rests on the app's own synthesis rather than a cited standard (WC010). Six mapping locators are missing from otherwise accurate mapping lists (CC033). These are owner decisions first and content work second.

### 3.5 Cumulative review replays items rather than reviewing

- **What changed.** Units 5–14 now draw from several earlier objectives instead of always replaying the Unit 1 decoding pair.
- **What did not change.**
  - Units 15–30 still replay u1-i-decode-pair plus one fixed item from the previous unit.
  - Unit 2 has no review policy.
  - Every candidate is an exact replay of a previously passed item, and several replayed items are themselves defective.
  - There is no spacing.
- **How it was built.** Each unit received its own hard-coded policy and a new assignment schema (now schemas 1–16), with review text hard-coded in Kotlin (CC020, CC043, CC047).
- **Recommended direction.**
  - Replace this with one review definition declared in the pack, an honest "review" label, and item variants instead of exact replays.
  - AnkiDroid remains the spaced-repetition system (MA2-074).

### 3.6 Labels overstate what is assessed

- All 626 items are supplied-option recognition. No item elicits production or uses unseen material at scale.
- Even so, some capabilities are named "productive" (Unit 17), and Unit 19 claims "productive meanings" of prefixes.
- Units 1–2 promise recording and playback that the v2 runtime does not have (WC002).
- Units 27, 29 and 30 are coded as activated variances before any final audit or owner acceptance (CC048).

### 3.7 Provenance and rights rest largely on the app's own synthesis

- 15 of 58 capabilities rest on self-authored spans.
- 4,211 provenance records cite src-content-audit-research, and 375 cite src-original-research-synthesis.
- No provenance record carries a reviewer, and the pack-level linguistic reviewer is null.
- Page-level licence marking for ReadyRussian and Pressbooks spans, literary sentences inside Wikipedia spans, and the stressed Unit 30 edition are unconfirmed (CC035, CC009).

### 3.8 What works

- The Unit 5 declinable/indeclinable contrasts and the Unit 9 case-pattern classification items vary their keys and are well designed. They do, however, reuse only taught phrases.
- The Unit 29 short- and long-adjective items have accurate rationales.
- Unit 16 teaching states endings and contrasts clearly, although it also announces the answers.
- The Unit 30 initialism items are reasonably diagnostic.
- Engineering corrections since 8606c4d7 are real in static inspection and in the validated host run: insets (CC018), option semantics (CC019), displayed rationales (CC034), saveable onboarding (CC040) and wrapper checksum (CC042, partial).

## 4. Highest-priority defects

1. **WC001 (HIGH):** displayed rationales are inaccurate, templated or missing. This is learner-facing now.
2. **CC027 / CC036 / CC010 / CC030 (HIGH/MEDIUM):** non-diagnostic required checks: the cue serves as the distractor, keys are constant, checks are duplicated or have two options.
3. **CC022 / CC015 / WC005 (HIGH):** teaching announces answers while rules are withheld or never taught.
4. **CC021 / CC023 (HIGH):** remediation does not cover the checks or is raw markup.
5. **WC003 (HIGH):** reading objectives never show the text.
6. **CC002 (HIGH):** the Units 1–2 foundation lacks pronunciation, listening and handwriting content.
7. **CC020 (HIGH, partial):** cumulative review is exact replay, and Units 15–30 are unchanged.
8. **CC024 / WC004 (HIGH/MEDIUM):** keyed mistranslation and contradictory teaching statements.
9. **CC003 / CC004 / CC031 (HIGH):** scope gaps and early analysed genitive.

## 5. Required checks with recorded defects, by unit

| Unit | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 | 11 | 12 | 13 | 14 | 15 |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| Defect/required | 14/18 | 5/5 | 11/12 | 14/14 | 9/14 | 13/19 | 31/33 | 17/19 | 20/35 | 17/22 | 2/8 | 10/12 | 8/9 | 3/6 | 5/6 |

| Unit | 16 | 17 | 18 | 19 | 20 | 21 | 22 | 23 | 24 | 25 | 26 | 27 | 28 | 29 | 30 |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| Defect/required | 10/12 | 21/25 | 2/4 | 6/8 | 7/8 | 4/6 | 10/14 | 3/12 | 9/14 | 12/16 | 23/23 | 8/8 | 6/6 | 16/24 | 14/17 |

## 6. Recommended correction batches

These batches are grouped by root cause, not by unit. BATCHED_CORRECTION_WORK_ORDER.csv gives dependencies, the evidence required and the recheck criteria. Engineering batches are listed separately from owner, rights and qualified-linguistic decisions.

| Batch | Priority | Owner | Root cause | Findings |
|---|---|---|---|---|
| B01-ENG | P1 | Engineering | No pack-wide assessment-design rules in the validator | CC001, CC010, CC027, CC030, CC036, WC007, CC008 |
| B01-CONTENT | P1 | Content | Items authored as one-off pairs from frozen spans | CC011, CC026, CC028, CC010, CC030, CC027, CC036, WC007 |
| B02-ENG | P1 | Engineering | No guard against announced answers, untaught content, uncovered remediation or markup | CC021, CC022, CC023, WC005 |
| B02-CONTENT | P1 | Content | Teaching quotes spans and scripts answers; sequencing | CC015, CC022, CC021, CC023, CC031, WC005, WC006, WC009, CC003, CC004 |
| B03-ENG | P1 | Engineering | Rationales shown without a quality gate | WC001, CC034 |
| B03-CONTENT | P1 | Content | Governance wording, wrong glosses, template feedback | WC001, CC024, CC025, WC004, CC029, CC037, WC011 |
| B04-OWNER | P2 | Product Owner | In-app versus book-delegated scope never decided | CC002–CC007, CC012, CC032, WC003, WC010 |
| B04-CONTENT | P2 | Content | Implement scope decisions, references and mapping locators | CC017, CC032, CC033, CC045, WC003, WC010, CC002 |
| B05-ENG | P2 | Engineering | Per-unit hard-coded review with exact replay | CC020, CC043, CC047 |
| B06-ENG / B06-OWNER | P2 | Engineering / Owner | Overstated labels; early variance activation | WC002, CC038, CC039 / CC048 |
| B07-QUAL | P2 | Rights and source review | Self-authored spans; unconfirmed page-level rights | CC035, CC009, CC008, WC003, WC010 |
| B08-QUAL | P3 | Qualified linguist | No native/linguistic review of any shipped Russian | WC008, WC004, CC024, CC025, CC016, WC010 |
| B09-ENG | P3 | Engineering | App residuals | CC013, CC014, CC018, CC019, CC040, CC041, CC042, CC044, CC046 |
| B10-VERIFY | LAST | Verification | Diagnostics are not acceptance | Fresh broad run, then the final comprehensive audit, then physical acceptance last |

**Dependencies:**

- B01-ENG and B02-ENG come first, so content rewrites are checked against them.
- B04-OWNER decisions gate B04-CONTENT and the scope-dependent parts of B02-CONTENT (Units 15 and 21).
- B05 waits for B01-CONTENT, so review stops replaying defective items.
- Full B08 review follows content stabilisation. Urgent confirmations (у́летят, скажу, быть) can start now.
- The unactivated Unit 15 proposal was not adopted. No per-unit policy or schema is recommended.

## 7. Reconciliation of the 48 prior findings

All IDs, severities and status histories are preserved in FINDINGS.csv, and all 48 findings remain OPEN.

| Disposition | Count | Findings |
|---|---|---|
| Unchanged, still not resolved | 16 | CC002, CC003, CC009, CC011, CC015, CC017, CC023, CC024, CC025, CC031, CC035, CC038, CC039, CC044, CC045, CC048 |
| Not resolved, relabelled | 1 | CC004 |
| Confirmed and expanded | 11 | CC010 (under CC030), CC022, CC026, CC027, CC028, CC029, CC030, CC032, CC037, CC043, CC047 |
| Confirmed, recomputed or unchanged | 6 | CC005, CC006, CC007, CC033, CC021, CC036 |
| Unchanged; earlier change still not a resolution | 2 | CC001, CC008 |
| Partially resolved | 2 | CC020, CC042 |
| Statically resolved, not closed | 8 | CC012, CC013, CC014, CC016, CC018, CC019, CC034, CC040 |
| Historical or not re-inspected | 2 | CC046, CC041 |

Eleven new findings are registered separately: WC001–WC011, of which 3 are HIGH, 6 MEDIUM and 2 LOW. In total there are 59 findings: 18 HIGH, 28 MEDIUM and 13 LOW.

## 8. Runtime evidence

- **Broad host run.** The 1,508-method broad selected JVM host run at 5a3411a7 completed. Evidence is in `engineering-cache-ui-ci-broad-host-regression-2026-10-28`, published in evidence commit 95891358.
  - Result: 1,507 passed, 0 failed, 0 errors, 1 skipped (AcceptanceBoundaryTest#at020_physicalRebootPersistsGovernedRecords; the physical evidence is still outstanding).
  - 264 classes, exit 0, no selectors.
- **Validation done by this audit:**
  - RAW_RUNTIME.zip SHA256 `6f6d368b…` matches its manifest.
  - All 358 members re-hash with 0 mismatches.
  - The recorded input trees equal the audited trees.
- **Exclusions.** The four screenshot exporters (16 methods) remain excluded. 27 result rows come from legacy v1 UI tests (CC044).
- **What this run is not.** It is not a complete app suite, an instrumented run or device evidence, and it was not combined with any other run.
- **NOT_EXECUTED by this audit:** every Gradle task, lint and instrumented tests. The Android SDK is absent and dl.google.com is unreachable from the container.

## 9. Book alignment

- **Lessons read.** All 30 lessons were read in full in the same PDF (identical SHA256) during this auditor's 2026-10-22 session, and every mapped locator was verified then. In the lesson body, the printed page equals the PDF index minus 12.
- **Carried forward.** The mapping bytes are unchanged since 8606c4d7, so that verification carries forward.
- **Re-located on 2026-10-29:**
  - §15.7–15.8: printed p177, PDF 189.
  - §20.5: p248, PDF 260.
  - §22.13: p279, PDF 291.
  - §30.4: p370, PDF 382.
- **Mapping omissions:** Ex 8/3, Ex 10/1, §15.8, Ex 24/3, §25.4 and Ex 28/4.
- **Sampled only:** the back matter (PDF 402–428, 431, 434–438, 441–463, 466–512).
- **No book prose** is reproduced in any return.

## 10. Where qualified, rights or owner judgment is still needed

- **Qualified linguist:** naturalness and key validity of the lines in WC008; the stress and gloss corrections in WC004, CC024 and CC025; whether the archaic Unit 28 passage suits this level.
- **Rights reviewer:** page-level licence marking for the ReadyRussian and Pressbooks spans; literary sentences inside Wikipedia spans; the stressed Unit 30 edition; replacing self-authored capability spans.
- **Product Owner:**
  - Decide the in-app versus book-delegated scope for each book topic (B04-OWNER), including the Units 1–2 audio and pronunciation foundation and reading-text display.
  - Choose the postal convention (WC010).
  - Decide the variance status of Units 27, 29 and 30 (CC048).

## 11. Gaps and limitations

- No test, lint or build was executed by this audit (environment blocker). Runtime conclusions rest on the validated broad run.
- No qualified native-linguistic or rights review was performed.
- Third-party span fidelity was not re-fetched, and no source snapshots are retained.
- The book's back matter was sampled only.
- Retained learner histories for schemas 6–16, and restore with recordings, were not verified (CC047).
- No device, TalkBack, inset, IME or process-death verification was done.
- Physical Galaxy S26 Ultra airplane-mode/reboot acceptance is outstanding and remains LAST.

## 12. Boundaries kept

This audit made no change to the app, tests or content. It did not fix, merge, freeze, assemble, install or sign anything, granted no source, rights or owner acceptance, and gave no release approval. It does not replace the final comprehensive app/curriculum audit, which must follow the corrections. Physical device acceptance remains last.

## Returned files

| File | Purpose |
|---|---|
| CURRICULUM_AUDIT_REPORT.md | This report |
| UNIT_COVERAGE.csv | All 30 units: entity counts, teaching/assessment/review/book coverage, explicit missing scope, finding IDs |
| ITEM_AND_CAPABILITY_COVERAGE.csv | 626 items and 58 capabilities, with JSON path, role, verdict, defect codes, mechanical flags and linked findings |
| FINDINGS.csv | 48 reconciled prior findings and 11 new ones, with status history and acceptance criteria |
| BATCHED_CORRECTION_WORK_ORDER.csv | 15 root-cause batches with priorities, owners, dependencies and recheck criteria |
| AUDIT_SCOPE_AND_EXECUTION.json | Identities, counts, inspection method, book locators, runtime validation, NOT_EXECUTED commands and gaps |
| mechanical_analysis.py | Supplementary script that reproduces the mechanical counts |
| SHA256SUMS.txt | Checksums for all of the above |
