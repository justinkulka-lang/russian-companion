# Independent comprehensive nonphysical recheck: 2026-10-22

**Outcome: INCOMPLETE.** This is a diagnostic recheck with findings. It is not an Amendment 1 `FINAL_CURRICULUM_AUDIT_PASSED` artifact, and it approves nothing.

| Item | Value |
|---|---|
| Review target | `8606c4d74185f594d93941bef6858b2af28e0b75` (tree `7c36e7e6…`, 8,596 tracked files) |
| Work-order delivery | `ae1105fbc78f6b5fbb2dbdce9058b08c5a44792e` (15 added files and 3 additive pointer edits). PR64 head was still `ae1105fb` when fetched on 2026-10-22. |
| Reviewer | Claude Code cloud session. Configured and last-served model `claude-opus-5-5` (`get_session`); no subagents. This is not the GPT-6.1 Sol/High chat named in the work order. |
| Physical acceptance | OUTSTANDING and LAST |

No app file was modified. No APK was assembled, installed or signed. No exporter or WRITE_* regeneration was run. Nothing was merged or frozen, no Amendment 1 stage was advanced, and no owner or release approval was given. The private Penguin PDF is not in this return, and no book prose is quoted. Book references are neutral page and section locators only.

## 1. What was inspected and how

**Original return**
- `GPT61_FINISH_AUDIT_RESULTS` ZIP `0321d323…`, 658,763 bytes.
- All 42 entries pass CRC, and the internal SHA256SUMS list verifies 41/41.
- All 42 members resolve byte-exact through `ORIGINAL_RETURN_MEMBER_RESOLUTION.csv`.
- `SHA256_PREPARATION.json` verifies 14/14.
- `VERIFY_INPUT_INVENTORIES.py` exits 0 when run with `-I`. Its printed booleans are hard-coded.

**Curriculum** (`v2-curriculum.json`, 20,350,033 bytes, sha256 `fc2a59cd…`)
- Units 1–16 were read from full renders (all fields).
- Units 17–30 were read from compact renders. Those omit the "why", distractorDifference and contrast lines and truncate long remediation bodies, so remediation coverage was also tested mechanically.
- Read in full: all 37 sources and rights, 27 references, 33 letters, 30 unit reviews, admissions and the pack review.
- Checked mechanically: 58 source capabilities (with targeted reads), 7,036 provenance records (hashes only), 1,655 authored-English records and 438 source-pool entries.
- Denominators: 30 units, 195 objectives, 626 items (429 required checks), 821 examples.

**Book** (private owned PDF)
- Verified at 13,596,737 bytes, sha256 `2bcc0c45…`, kept outside the checkout. It is image-only, so it was read visually.
- Read in full: all 30 lessons, the grammatical tables, the spelling rules, the glossary, the index and the back cover.
- Sampled only: the vocabularies and the answer key.
- Every mapped section and exercise locator was compared with the book (printed page = PDF index − 12).

**Engineering** (static reading only; partial coverage is listed in `REPOSITORY_FILE_COVERAGE.csv`)
- Build, manifest, startup and pack loading.
- V2 UI: session, lab, alphabet lab, navigation and scaffold insets.
- Cumulative review (read in full), completion evidence, progress rules and release gate (read in full).
- Backup format and limits, Room migrations and schema diff, notices, audio reachability, tuple pins and variance flags.

**Runtime evidence**
- Both retained raw scopes were extracted and validated from their JUnit XML. No execution was possible (section 4).

**History and archives**
- 64 histories: assignment digests recomputed with an independent port of the canonical encoding.
- 66 archives: SHA and CRC checked.
- 167 historical members and the 155-record index: hashes checked.

## 2. Original 17 findings: current independent disposition

None is closed. "Static-resolved" means the source no longer shows the defect, but no runtime, qualified-review or owner evidence exists.

| ID | Sev | Disposition at target | Note |
|---|---|---|---|
| CC001 | HIGH | Changed, not resolved | Presentation permutation reproduced. Key first in 44.4% of 620 placements against about 40.1% expected; stored order is still key-first 563/626. |
| CC002 | HIGH | Open | No sound, listening, handwriting or transliteration. v2 has no audio path. 8 letters are never presented. Unit 1 checks are glyph matching. |
| CC003 | HIGH | Open (scope narrowed) | Unit 15 still partner recall; byte-identical to Unit 12/13 checks. |
| CC004 | HIGH | Open (relabelled) | Independent checks key если and потому что; no purpose check. |
| CC005 | MED | Open | No surname declension anywhere in the raw pack. |
| CC006 | MED | Open | No fractions. |
| CC007 | MED | Open | No bringing constructions. |
| CC008 | MED | Narrowed | 4 of 9 Phase 2/3 tuples pinned; 5 unpinned. |
| CC009 | MED | Open | Self-authored capability spans; no reviewer. |
| CC010 | MED | Open | All 8 groups persist; further duplicates in CC030. |
| CC011 | MED | Open | Unchanged dash/colon position recall. |
| CC012 | HIGH | Static-resolved for Unit 8 | A new same-class defect in Unit 7 is registered as CC031. |
| CC013 | MED | Static-resolved | Bounded import verified in code; not executed. |
| CC014 | MED | Static-resolved | Preview counts review records; not executed. |
| CC015 | HIGH | Changed, not resolved | Teaching now states each check answer verbatim; the pattern is still not taught. |
| CC016 | MED | Static-resolved | Fragment and sentence glosses separated. CR009–CR011 re-adjudicated accordingly. |
| CC017 | MED | Open | 27 references; no substantive grammar reference breadth. |

## 3. New findings (CC018–CC048)

There are 31 new findings: 9 HIGH, 11 MEDIUM and 11 LOW. `FINDINGS.csv` holds the full rows; the highest-impact ones are summarised here.

### HIGH

- **CC018 – Insets.** Edge-to-edge is enabled at targetSdk 36, but scaffold, top bar and bottom navigation insets are set to zero, and there is no safe-drawing or IME padding. Content is likely drawn under the system bars and keyboard. This is static evidence; device rendering is unverified.
- **CC019 – Option semantics.** Answer option rows carry the radio role but are not clickable, and the clickable inner RadioButton has no label. TalkBack would likely meet unlabelled controls.
- **CC020 – Cumulative review.** Every due review for Units 4–30 is a fixed two-item replay: the same Unit 1 case-pair item, plus one already-passed item from the previous unit. That does not satisfy MA2-027 or MA2-088. The original return had rated MA2-027 as satisfied.
- **CC021 – Remediation.** For 71 objective-check pairs, the remediation contains neither the cue nor the key. Eleven objectives have no remediation coverage of any check.
- **CC022 – Scripted teaching.** Teaching repeatedly states the exact check answer and distractors, while generalisable rules are withheld or forbidden. Examples: gender from endings, prepositional endings, plural formation, genitive formation, unlabelled dative form cards and imperative formation.
- **CC023 – Raw markup.** Raw wikitext, wiki links, dangling fragments and tautologies are shown as teaching and as the entire remediation (Units 4–15).
- **CC024 – Mistranslation.** сказать and скажу are glossed and keyed as "speak". The owned book's vocabulary glosses the verb as "to say" (p420).
- **CC027 – Non-diagnostic checks.**
  - 52 two-option items (39 of them gating) have the displayed cue as their only distractor.
  - Several objectives use the same key for every check.
  - Some items show a decoy cue.
  - Noun-form objectives can be answered from the numeral alone.
  - The question wording discloses the key in some items.
  - Comma checks display the commas.
  - Unit 1 identity checks can be answered by glyph matching.
- **CC031 – Early genitive.** A Unit 7 required check analyses a genitive phrase before Unit 9. The CR043 correction introduced it.

### MEDIUM

- **CC025** – Gloss and spelling errors: э́ти glossed "this"; "genetive".
- **CC026** – Label, position, English or app-policy recall in gating checks across Units 7–30. u17-i-calendar-date-1 has two defensible answers.
- **CC028** – Absurd distractors.
- **CC029** – Governance jargon in learner text: "admitted" in 251 prompts, "frozen" in 147 feedback blocks.
- **CC030** – Additional duplicate tasks; six Unit 12/13/15 checks are the same two tasks.
- **CC032** – Book-scope gaps by unit.
- **CC033** – Mapping locator omissions: Ex 8/3, Ex 10/1, EXTRA Ex 24/3, Ex 28/4, §15.8 and §25.4.
- **CC034** – Distractor rationales are never rendered.
- **CC035** – Provenance hygiene, including Unit 1 letter names attributed to a Unicode source.
- **CC036** – 208 of 429 required checks have two options.
- **CC048** – Units 27, 29 and 30 are hard-coded as activated authority variances, and no unresolved variance remains. Amendment 1 §3 and MA2-178 keep them PROPOSED_NOT_ACTIVATED until post-final-audit owner acceptance. The decision packet also contradicts itself. This is for owner and Codex resolution.

### LOW

CC037–CC047 cover:
- status text contradictions;
- the unused RECORD_AUDIO permission and unrendered letter notes;
- release-gate audio criterion and v1 coupling;
- onboarding state that is not saved across recreation;
- main-thread and null-state handling;
- supply-chain pinning;
- learner text hard-coded outside the pack;
- dead legacy UI counted in test totals;
- reference entry hygiene;
- an execution-request selector that matches no class;
- no schema-6 history fixture.

**Correction to my own working notes:** u12-i-spelling-2 deliberately asks for the rejected spelling. It is a pedagogical concern (it sits in CC036), not a keyed language error.

## 4. Runtime evidence (scopes kept separate, never summed)

**Broad scope** (`e5b5d3c0`)
- 221 classes and 1,334 methods: 1,324 passed, 9 failed, 1 skipped (a physical reboot test). Exit code 1.
- Lint: 0 fatal, 0 errors, 20 warnings, 1 hint. Compile tasks were up to date; verifyReleaseGateAttachment ran.

**Follow-up scope** (`20c5eb96`)
- 9 selected classes and 56 methods, all passed. Exit code 0.
- These are exactly the broad scope's methods in those classes.

**Tree identity**
- The `app/` tree `c83cbcc4` is identical at both runtime commits and at the target.
- The `review/` and `release/` trees match the target only at `20c5eb96`.

At the target, only those 56 methods are evidenced against identical inputs. No fresh complete-suite result exists.

**Host method index:** `HOST_METHOD_EVIDENCE_INDEX.csv` matches the raw outcomes for all 1,334 rows, the target file SHAs for all 219 files, and the method declaration lines for all 1,334 methods.

**Independent execution was unavailable.**
- `dl.google.com` was refused (HTTP 403) by the session network policy, so the Android SDK cannot be obtained.
- GitHub Actions runs on the target and delivery commits failed within seconds, with no runner assigned and no logs.

The 70 execution requests keep their NOT_EXECUTED status and each has a separate disposition:
- 4 partially evidenced, but not at the target;
- 13 with a retained raw pass, but not at the target and not scenario-verified;
- 17 static reassessments of findings;
- 8 needing an emulator or device;
- 1 structural history check;
- 27 static-only.

## 5. Book alignment

- All mapped locators in Lessons 1–30 match the owned edition.
- Six locators are omitted (CC033).
- Lesson 30 has no numbered exercise, so its empty exercise list is accurate.
- The ISBN check digits on the back cover are valid. Registry verification was not performed.
- Topic-level gaps against the normative schedule and the book are in `UNIT_COVERAGE.csv` and CC032.
- Unread book ranges: PDF 1–12, 402–428, 431, 434–438, 441–463 and 466–512.

## 6. Histories, archives and indexes

**64 authentic histories**
- All present at the target, with every assignment digest recomputed and matching.
- No structural defects and no recordings.
- Assignment schemas 1–5 only.

**66 original archives:** SHA and CRC match.

**155-record source-binding index**
- 95 records changed and 60 are unchanged; all match the target bytes.
- All 95 prior versions are present in `HISTORICAL_PRIOR_CURRENT.zip`.
- The 95 files differ only by the release-digest substitution (I recomputed the digest independently).

**167 historical generated members:** CRC passes and all 167 hashes match their index.

The oldUnit7 restore cause remains **UNCONFIRMED**.

## 7. Why the outcome is INCOMPLETE

- **Execution:** not possible; host blocked.
- **Repository files:** 8,040 of 8,596 tracked files were not inspected (`REPOSITORY_FILE_COVERAGE.csv`).
- **Book:** the vocabulary and key ranges listed above are unread.
- **Units 17–30:** rationale and "why" fields were checked only mechanically.
- **Provenance:** semantics not reviewed.
- **Third-party sources:** span fidelity is unverifiable, because no snapshots are retained and Wikipedia was unreachable.
- **Qualified review:** no qualified linguistic or rights review, and no owner mapping acceptance.
- **Physical acceptance:** outstanding, and last.
