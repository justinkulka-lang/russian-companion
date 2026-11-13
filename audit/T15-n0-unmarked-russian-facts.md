# T15-n0 unmarked Russian factual-claim inventory

This is the T15-n0 baseline inventory at 9decefb2, not a list regenerated after later content corrections. Subsequent per-entry dispositions are recorded in WORK_QUEUE.md. Unit 6 entries are retained here to make its marker/disposition regression checks independent of inventory deletion.

Scope: all item feedback and distractor rationales (including optional reference items), and all REMEDIATE step bodies, in shipped Units 1–30. The [CSV](T15-n0-unmarked-russian-facts.csv) contains 4,353 entries copied verbatim from the pack. It records concrete glosses/translations, form identities, grammar/usage claims, and instructions that themselves encode a linguistic rule. Pure directions, app normalization, task-scope statements and source-layout/position-only claims are excluded. Claims are recorded for review, not certified as true, licensed independently, or unsourced. English-only entries can already be protected by the quotation rule.

Cyrillic-bearing entries carry the exact label “already protected by the Cyrillic rule”. The inventory includes Unit 1 and optional items outside the legacy English-host traversal. It does not inventory prompts, teaching bodies, references or example fields. No marker or content change is made by this inventory.

Rows are deduplicated by unit, owner ID, field and verbatim sentence, using the Kotlin validator's quote-preserving sentence ranges. Sentence fragments retained by that splitter, including source markup and embedded glosses, remain separate actionable spans. Source path: `app/src/main/assets/content/v2-curriculum.json`; SHA-256: `87e3fc725da146df4a27cbf7595c966fca7844960bdba32465d2bc877c444fc4`. Each row identifies the exact owner and field in that existing pack; no source is added.

Totals: 1,442 feedback, 1,196 distractor rationale, 1,715 remediation; 2,063 Cyrillic-bearing and 2,290 English-only. Required checks: `u6-i-possessive-1`, `u6-i-feminine-1`, `u5-i-type-kuda` are present. Pure directions beginning “Compare both strokes” and “Choose the Russian name” are absent.
