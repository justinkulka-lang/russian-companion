# Literacy authoring stage — implementation pending final audit

CORRECTION.json independently authors 46 wrong-option explanations and replaces four literal Unit 1 replays in Unit 2 with actual lowercase-to-uppercase case variants using three Cyrillic choices. Existing answer VALUES and IDs remain; canonical content digests distinguish revised item semantics. Unit 2 instruction and remediation share the same worked-example card, now including the existing Я / я example. No words, audio, handwriting or private Penguin content are added.

UNICODE_RESEARCH.json retains exact case/name rows, bibliography, actual file/blob SHA, authorship and objective bindings. The official ICU mirror is pinned to 8eca245c7484ac6cc179e3e5f7c1ea7680810f39. Full original source and licence are retained in the engineering baseline's RAW_BASELINE.zip. The accompanying Unicode licence is preserved. These facts support writing-system correspondences; they do not establish phonetic/linguistic/pedagogical acceptance. Sign/letter-name explanations reuse the pending curriculum's existing claims. Final independent review remains outstanding.

Run the explicit stage against exact pre-correction bytes (asset SHA02202d37… in CORRECTION.json):

```
python3 catalog/apply_literacy_family_correction.py --input /absolute/baseline.json --output /absolute/fresh-corrected.json --report /absolute/fresh-change-report.json
```

It refuses another input or an existing output, does not modify the input, and never runs the older whole-pack intake builder. A future full curriculum rebuild must preserve/reapply this authoring stage against its retained baseline or explicitly reconcile it; running build_v2_curriculum.py alone is not proof that all subsequent corrections survive. Do not silently regenerate the full pack from an old intake.

Research and per-item English authorship are retained here with pending status. The legacy authoredEnglishRecords intake currently restricts that scaffold record type to Units 3–30; this stage does not falsify a Unit 3 scaffold record or weaken validation to evade that limit. Existing Unit 1/2 sourceId src-app-english-v2 and review-pending status remain. No final-audit artifact, qualification, rights acceptance or owner decision is fabricated.

This is a bounded correction. Script ambiguity in some Unit 1 checks, missing listening/pronunciation, true decoding, guided-item rationales and later curriculum defects remain open. All 59 findings remain OPEN. Old raw attempts/completions/assignments must be preserved while obsolete digests stop granting current credit. Use the ten actual pre-correction capture scenarios to verify this boundary.
