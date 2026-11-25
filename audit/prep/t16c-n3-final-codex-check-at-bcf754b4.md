# T16c final read-only recheck

Date: November 25, 2026. Repository: `justinkulka-lang/russian-companion`, branch `cursor/content-audit-corrections-964f`, exact head `bcf754b46d9ea54aea0e4104d7ddd86bcc753e2c` (implementation `fc91121a`, followed by identity/digest commits).

**Verdict: 0 BLOCKING findings; 5 LOW findings logged below. Close T16c under D46h.** This does not mean every guard is complete. In particular, the original finding-3 prompts still pass full-pack validation, although they now fail the item-level quantity check. No wrong currently shipped answer or count prompt was demonstrated in this scoped check. No repository file, content, test, queue, branch or remote record was changed.

The governing decisions were read from `owner/prep-reports`, including D46b–h and D42a/D43b. The original probes were recovered from `audit/prep/t16c-n2-recheck-codex-at-5ebb790e.txt` and the earlier scratch harness. All mutations below were in memory or in separate scratch probe source, never in the repository.

## Original probes

| Prior finding | Probe rerun | Result now |
|---|---|---|
| 1 — UI literals | Variable holding `review-pending`; `Text(buildString { append("APPROVED") })`; nested conditional literal with braced branches; Unicode-escaped `APPROVED`; direct `Text("APPROVED")` control | All produce lint findings. Unicode escapes are decoded. |
| 2 — review counts | Unit 3 introduction changed to `Answer sixty review checks before continuing.` | `introductionNamesACount` returns true; `reviewCopyDefects` rejects it. The new enforcement is in production validation, beyond the old test-only short list. |
| 3 — quantities | `Which phrase means half of two hours?` and `Which phrase means double two hours?`, retaining the two-hour key | Both now produce `prompt names a competing count`, and `featureValid` is false. **Both still return zero full-pack validation errors. Original end-to-end bypass remains; LOW under D46h.** |
| 4 — accepted answers | Add `бо́льший` before `лу́чший` in the Unit 18 best-form item's accepted list, recomputing its review digest | Citation enforcement rejects the uncited extra answer; full validation fails; the citation pin test rejects the mutation. |
| 5 — binder exemption | `Make a table by copying the genitive row with rows for each case.` and `Make a table from the source table with rows for singular and plural.` | Both are flagged in binder-task prompt and model-answer fields. The span between the two binder phrases is no longer swallowed. |

## Broader variants

- **UI:** escaped `Release`, `BasicText`, `ClickableText(AnnotatedString(...))`, `V2Status(buildString { appendLine(...) })`, `arrayOf` reached through a variable, and `StringBuilder.append` are caught when their individual literals contain the prohibited text. Two remaining gaps are recorded as LOW-1.
- **Review counts:** seventy-seven, ninetyfour, two hundred and six, billion, thousand and fullwidth digits are rejected. Current Unit 3–30 introductions pass. Count synonyms `nil` and `a pair` are missed; LOW-2. The probe using `first` also passes, but ordinal wording is not used as evidence against the specified complete-cardinal pattern.
- **Quantities:** `a third`, `a quarter`, `полтора`, `полторы`, `1.5`, `0,25`, `⅓`, threefold, a quarter-hour expression, en-dash compounds, ninety two, and one billion two fail the item check. The unchanged `two hours` control passes. Multiplier-family omissions and failure to propagate item rejection into full validation are LOW-3.
- **Accepted answers:** an answer citing the other Unit 5 item's different cell, all-capital and mid-word-capital variants fail citation enforcement and full validation. Existing targeted tests also exercise missing, stray, synthetic and mismatched citations and primary-key stress changes. A wrong extra form supplied with a genuine frozen citation for that form can satisfy citation equality, but the shipped tuple pin test rejects the added answer/citation row. Exact frozen attestation alone does not establish that an answer means what the prompt asks; the pin protects the current authored set.
- **Binder:** source framing, `read this row`, reviewer approval and `from the dictionary` inserted between the binder phrases are flagged in both task fields. Existing tests cover BINDER steps and framing after the phrases as well. Plain `Make a table with rows for each case.` and `Write each form in each row of your table.` remain allowed. No new binder-exemption bypass was reproduced.

## D46b accepted-answer audit

**PASS: 197 typed items, 201 accepted-answer entries, zero uncited or mismatched shipped entries.** The 197 primary forms use `keyCitation`; the four additional forms use `acceptedAnswerCitations`. Every literal accepted entry equals its cited record's `shippedText` exactly. The cited text occurs whole and with exact stress in its source view, subject only to the approved first-letter case exception.

The audit used the compiled Kotlin validator and citation tests plus an independent Python enumeration. The latter checked frozen-record identity/hash conditions and exact key/citation equality for all 201 entries. The four additional forms were also located directly as whole TSV cells in the frozen CSV files; the actual CSV byte hashes match their FREEZE manifests:

| Item | Accepted form | Frozen cell | Citation | D42a/D43b |
|---|---|---|---|---|
| `u5-i-type-kuda` | Куда | `others.csv`: куда | `prov-u5-key-kuda-unstressed` | Only К/к differs. |
| `u5-i-type-otkuda` | Откуда | `others.csv`: откуда | `prov-u5-key-otkuda-unstressed` | Only О/о differs. |
| `u5-i-type-skolko` | Сколько | `others.csv`: сколько | `prov-u5-key-skolko-unstressed` | Only С/с differs. |
| `u5-i-type-kakoy` | Какой | `adjectives.csv`: какой | `prov-u5-key-kakoy-unstressed` | Only К/к differs. |

All four prompts are exactly `Type the question word that has this meaning.` Thus all four fall inside the narrowed D43b interpretation of D42a. They are exact lowercase frozen cells with permitted initial capitalization, rather than byte-identical capitalized source cells. Their unstressed spelling is present in the frozen bare cells; no stress was silently removed from a stressed cell to create the citations.

This audit concerns the authored accepted list. The separately authorized A2-21 normalization of learner input is not an uncited extra authored answer.

## Allowlist audit

The file contains **335 entries, 335 unique (file, literal) pairs and zero blank reasons**. Each entry was matched to current literals and its use sites inspected. The automated explicit/current/needed check passes. The reasons are not all sufficiently specific or accurate: LOW-5 lists six misclassified uses. LOW-4 identifies the one allowlisted literal confirmed visible to a learner.

The 135 rows described as Compose test tags include one ViewModel key, identified below. The remaining tag uses are tags rather than rendered text. Status labels use plain mapped values; the allowlisted map keys are not displayed as raw status values. License filenames are used as paths or lookup keys, with human-readable notice titles displayed. No other allowlisted learner-visible literal was identified in this review.

## LOW findings and written dispositions

### LOW-1 — UI literal checking does not close every construction or renderer use

`UiStringLintTest.kt:88–119` still checks literals separately and exempts an allowlisted (file, text) pair unless its immediate use is recognized as direct rendering.

Reproductions:

```kotlin
val x = "Release " + "stays blocked."; Text(x)
```

This yields no lint findings. Each fragment passes separately, while the rendered result contains the prohibited sentence.

In the allowlisted file `v2/ui/V2Screens.kt`, `V2Status("v2-continue")` yields no lint findings, whereas `Text("v2-continue")` is reported. The filename/literal exemption is reusable in an unrecognized renderer. The actual current occurrence is a button tag, not that rendering probe.

**Disposition:** log only under D46h. No current rendered prohibited sentence or identifier from these constructions was demonstrated. No value-flow analysis or new renderer rule was added.

### LOW-2 — Count synonyms can evade the complete-cardinal check

`V2ReviewCopy.kt:24–32` accepts `Answer nil checks.` and `Answer a pair of checks.`: `introductionNamesACount` is false and there is no corresponding count defect. Standard English cardinal tens and compounds tested above are covered; the broader promise to exclude every count expression is not complete.

**Disposition:** log only. Neither expression occurs in the current introductions; no inaccurate shipped introduction was demonstrated. No pattern expansion was made.

### LOW-3 — Quantity rejection remains incomplete and is not an unconditional pack rejection

`V2Validator.kt:3973–4001` uses a finite modifier pattern. `Which phrase means quintuple two hours?` has no item-structure errors, is feature-valid and passes full validation. `дважды two hours` also passes the quantity item check, although separate Russian/English provenance checks reject that full-pack mutation. Do not confuse that provenance rejection with correct quantity detection.

More significantly, the original `half of two hours` and `double two hours` prompts now fail the item check but still pass full validation. Their item ceases to count as feature-valid, while other checks satisfy the aggregate objective requirements. Therefore item-level invalidity is not a guaranteed rejection of this pack mutation.

**Disposition:** log only under D46h. Every current count prompt was checked against its pinned quantity; no wrong prompt ships now. No additional fix round was started.

### LOW-4 — Allowlisted backup filename is visible

`ui-lint-allowlist.tsv:39` exempts `russian-companion-backup.zip` as `asset or file path`. `V2BackupScreen.kt:75` passes it to the CreateDocument launch as the suggested save name, which is shown in the system save dialog. This is a learner-visible allowlist exception, even though the filename itself is appropriate content.

**Disposition:** log visibility explicitly, as requested. The filename is not wrong course content, so this is not BLOCKING. Neither filename nor exemption was changed.

### LOW-5 — Six allowlist reasons misdescribe their actual use

| Allowlist line | Literal | Actual use; issue with the current reason |
|---|---|---|
| 25 | `russian-companion-staging` | Thread name, rather than a record/route/schema/rule identifier. |
| 31 | `staging-delete-hold` | Developer `error(...)` diagnostic; the generic record/route reason does not identify that use. |
| 34 | `v2-backup-${}` | ViewModelProvider lookup key; the stated Compose test-tag reason is incorrect. |
| 211 | `staging-directory` | Developer `error(...)` diagnostic, not the stated record/route category. |
| 212 | `staging-create-hold` | Developer `error(...)` diagnostic, not the stated record/route category. |
| 309 | `{$name}` | Template substitution token in `String.replace`, not a storage/identity/route key. |

**Disposition:** log only. These current uses are internal, and no learner-visible leak from them was found. Nonblank text alone is insufficient to establish that each reason accurately describes its entry. Reasons were not edited.

## Verification limits

The current unmodified pack returns zero errors from compiled `V2Validator.validate`. **Targeted JVM result: 27 tests passed, zero failures**, across `UiStringLintTest` (6), `TypedKeyCitationTest` (5), `Unit17CountMeaningRuleTest` (5), and `WidenedLearnerVoiceLintTest` (11). The production Kotlin sources and literal scanner were compiled unchanged; minimal Android Context/BuildConfig/profile scaffolding and a step-label helper were supplied outside the repository to run them without an Android build. This is a targeted read-only verification, not a new APK build, device run, final curriculum approval or replacement for exact-commit CI evidence. Unrelated existing Units 20–30 advisory lint output is outside this five-fix close-out and is not reclassified here.

Final repository status: clean. All five LOW findings are retained in this report with a no-fix disposition under D46h. T16c ends with this recheck.
