# Codex read-only preparation reports

Owner-supplied copies of Codex's read-only preparation reports, for the implementer of each unit task. Each file name records the head it was written against; re-check every cited file and line against the current head before relying on it.

- unit-07-prep-at-3d624565.txt: T15b (Unit 7). Note: the 12 UNATTESTED phrases it lists are now 9 under the two-part phrase rule (see WORK_QUEUE.md, T15a-n3).
- unit-08-prep-at-9a9b9760.txt: T15c (Unit 8)
- units-09-10-prep-at-9a9b9760.txt: T15d, T15e (Units 9, 10), including the Unit 9 cumulative-review cue fix
- units-11-12-prep-at-9a9b9760.txt: Units 11, 12
- units-13-15-prep-at-4c402ef7.txt plus units-13-15-prep-unit-15-remainder.txt: Units 13, 14, 15 (the first file was truncated in Unit 15 section 4; the remainder file completes it)
- units-16-17-prep-at-20bfde5c.txt: Units 16, 17 (T17). Note the Unit 17 validator conflict on six "row" prompts and the new intake candidates (Wiktionary "под"; ReadyRussian Grammar 24 Capitalization).
- unit-18-prep-at-20bfde5c.txt: Unit 18 (T17), report 1 of 3 for Units 18-20.
- unit-19-prep-at-20bfde5c.txt: Unit 19 (T17), report 2 of 3. Note: phrase matcher piped-link bug; probability capability binding constraint; intake candidates Wikipedia "Сослагательное наклонение" and Wiktionary "бы".
- unit-20-prep-at-20bfde5c.txt: Unit 20 (T17), report 3 of 3. Note: the one failing phrase is a stress-extraction bug (bold prefix у read as stress in "улетят", G:1383), not the piped-link bug; fix the formatter selection in units_16_20_batch_a.py without discarding real stress. Adding typed checks needs the catalog/content-checks.json checked-list pin updated.
- owner-decisions-2026-11-16.txt: owner decisions D1-D6 (D6 is a standing approval of coordinator recommendations) (Unit 17 and Unit 19 validator changes, S1 additions, Unit 15 gap) and the scope of the phrase-matcher plus stress-extraction fix.
- unit-21-prep-at-7d6e66e1.txt: Unit 21, report 1 of 2 for Units 21-22. Blocked on S1 (Reflexive pronoun, four records, wounded-object check). New intake candidates: Wiktionary "чтобы" and "дабы". Restore the parentheses in Я люблю́ (свою́) жену́ (G:508).
- unit-22-prep-at-7d6e66e1.txt: Unit 22, report 2 of 2. Feasible (20 checked/6 typed). Three phrase failures, all from the piped-link bug. Lint misses "The frozen note says…" narration, assessment-workflow commentary and raw table-cell quotes ("| Дательный"). Intake candidates: Wiktionary "двое" and Wikipedia "Имя числительное в русском языке" (collective usage).
- unit-23-prep-at-19608a79.txt: Unit 23, report 1 of 2 for Units 23-24. Blocked: three unattested Russian example sentences for the completion check (Анна читала/прочитала…, Борис писал…), on a legacy synthesis exception. The completion claims need S1 aspect pages plus attested examples. Intake candidates: Wiktionary "назад" and "за".
- unit-24-prep-at-19608a79.txt: Unit 24, report 2 of 2. Four unattested phrases and two unsupported stresses (не́кому, не́кем), admitted through the synthesis/coverage path. Key finding: V2Validator.kt:5233-5264 accepts Russian that matches any provenance shippedText, not only frozen files. See D7.
- scan-units-1-30-part1-at-1f8777d1.txt: Units 1-30 no-witness Russian scan, Part 1 (Units 1-15). No lexical failures; only alphabet lists, which D9 exempts.
- scan-units-1-30-part2-at-1f8777d1.txt: Units 1-30 scan, Part 2 (16-30), with the full accounting and the D7b allowlist. The remainder is in scan-units-1-30-part2-remainder.txt. 41 real failures, 28 required checks. See D10.
- scan-units-1-30-part2-remainder.txt: the rest of the Part 2 allowlist (Units 28-30) and the section 4 intake pages.
- d7b-allowlist.txt: the complete D7b blocking-audit allowlist, 227 lines (183 from the scan plus 44 COMPOSED from the D15c rescan) (unit | owner.field | text | type).
- unit-25-prep-at-c773acd6.txt: Unit 25. No Russian witness failures; 23 checked / 7 typed is feasible. The name-form policy check depends on synthesis; see D12.
- unit-26-prep-at-c773acd6.txt: Unit 26. A third extraction bug (inline HTML spans). The postal capability is synthesis-only; see D13.
- unit-27-prep-at-c773acd6.txt: Unit 27. Rebuild from frozen sources only (Ivan/book). The stress and comprehension capabilities get rebound; see D14. S2 is reduced to 3 pages.
- unit-28-prep-at-c773acd6.txt: Unit 28. Frozen-only rebuild from Gogol (R14) keeps the future-main-action check. Exposes the composed-sentence audit gap; see D15.
- rescan-d15c-composed-at-c773acd6.txt: D15c sentence-level rescan. 44 new COMPOSED entries, all in Units 27-29; Units 1-26 and 30 are clean.
- unit-29-prep-at-c773acd6.txt: Unit 29. S1 clears the punctuation checks; register and reading are rebuilt from G:2169, G:44 and V:47. See D16. S2 is now 2 pages.
- unit-30-prep-at-c773acd6.txt: Unit 30, plus the Units 11-30 summary table. The dictionary check is rebuilt from R14:135; see D17. Prep is complete.
- t15f-codex-check-at-e853de1e.txt: Codex check of T15f. FAIL on block tags, lint exemptions and manual misses; fixes go to T15f-n1, see D18d.
- s1-intake-research-units-11-15-at-9a9b9760.txt: S1 source intake
- t15f-changed-learner-fields.csv: T15f, every changed learner field in Units 1-30 (442 rows: unit, owner, field, old, new) between 1bf80b92 and 34b714dd, for the Codex check of T15f.
