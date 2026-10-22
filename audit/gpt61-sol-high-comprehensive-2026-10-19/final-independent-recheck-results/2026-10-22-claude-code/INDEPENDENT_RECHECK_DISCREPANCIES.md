# Independent recheck discrepancies: 2026-10-22

## Missing or unavailable inputs
1. **Android SDK host** (`dl.google.com`): refused (403). No execution was possible. GitHub Actions on the target and delivery commits never assigned a runner.
2. **Third-party sources**: en.wikipedia.org and ru.wikipedia.org were refused (403). The repository retains no source snapshots, so the 58 capability spans can be checked for self-consistency only.
3. **Original early audit files**: the original "audit/four" CSVs and the missing original outputs remain unavailable. Later returns were not substituted for them.
4. **Earlier separate scopes**: the ff2a5f55 1323-method result, the 34/3/37 focused scopes, main ff539220 (562 pass, 1 skip) and the 420f41ff 691 annotations were not re-validated. They remain separate histories.

## Reviewer and publication provenance
5. **Reviewer identity**: this return was produced by a Claude Code session (configured and last-served model `claude-opus-5-5`, confirmed by `get_session`), not the GPT-6.1 Sol/High chat named in the work order.
6. **Branch**: the work order says to publish to "the authorized draft branch". This session is restricted to branch `ccr-55fcda78-eddach`, so the return was committed there on top of `ae1105fb`, not on PR64's `cursor/content-audit-corrections-964f`. A maintainer can cherry-pick the single additive commit.
7. **Private PDF**: verified at 13,596,737 bytes, sha256 `2bcc0c45…`. The earlier 0c441ffd input was not used. The PDF is image-only with no text layer, so it was read visually; no OCR was available.

## Unread or partially read scope (exact ranges)
8. **Book**: PDF 1–12 (front matter); 402–428, 431 and 434–438 (Russian–English vocabulary); 441–463 (English–Russian vocabulary); 466–512 (key bodies 1/1 to 29/5).
9. **Repository**: 8,040 of 8,596 tracked files are NOT_INSPECTED (`REPOSITORY_FILE_COVERAGE.csv`). Partially read sources and their line ranges are listed there.
10. **Curriculum**: provenance records (7,036), authored-English records (1,655) and source-pool entries (438) were checked mechanically only. For Units 17–30, the distractor "why", distractorDifference and contrast fields were not read line by line.
11. **Test bodies**: the 1,334 method identities were checked against their files, but the method bodies were not semantically reviewed, except the two backup boundary classes.

## Discrepancies found in inputs and records
12. **Request selectors**: requests 22 and 60 name `*HostActivityLifecycleTest`, which matches no class. The file declares `LearnerReloadRecoveryTest` and `HostActivityRecreationTest` (CC046).
13. **MA2-027**: the original matrix rated it SATISFIED_DIAGNOSTIC. Reading `V2CumulativeReview.kt` in full shows a fixed two-item replay, so it is UNMET (CC020).
14. **MA2-178 and Amendment 1 §3 vs code**: Units 27, 29 and 30 are hard-coded as activated and `UNRESOLVED_VARIANCE_UNITS` is empty. `OWNER_VARIANCE_DECISION_PACKET.md` still says Unit 29 "stays in the hardcoded unresolved-variance set" and "NOT_PREPARED" (CC048).
15. **CR009–CR011** (correction targets, not CC009–CC011): the original return had CHANGED_BUT_NOT_RESOLVED. At the target they are resolved statically, together with CC016.
16. **CR043**: the correction of CA007 introduced a new sequence defect (CC031).
17. **Inventory counts**: 154 of the 155 index paths are counted as MECHANICAL_HASH in `REPOSITORY_FILE_COVERAGE.csv`. The 155th, `release/acceptance/candidate.json`, is counted as READ_FULL; its newSha256 also matches.
18. **Correction to my own working notes**: an interim note claimed u12-i-spelling-2 "keys an incorrect spelling". It keys the rejected spelling on purpose. It is reclassified as a pedagogical concern (CC036), not a language error.

## Denominators re-verified (no change from the work order)
8,596 files; 30 units; 626 items; 821 examples; 58 capabilities; 37 sources and rights; 27 references; 33 letters; 195 requirements; 17 findings; 75 targets; 18 IA; 70 requests; 64 histories; 66 archives; 167 members; 155 index records; 1,334 host methods.

One additional derived count: 429 required checks, of which 208 have two options.
