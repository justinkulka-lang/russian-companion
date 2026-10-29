# CC020 cumulative-review scope investigation

**CC020 remains OPEN.** The investigation corrects factual overstatements and confirms a substantive assessment concern. It changes no app code, tests, curriculum, policies, schemas, review/source bindings or governance, and it does not close any independent finding.

## Evidence and source

All inventory and runtime work pins `b4f409a984b66ebd1e3b9dceee4d47fb7f2a5bae`. Canonical JVM export calls the actual declared-policy resolver and, reflectively for diagnostics only, its new-assignment schema selector. The helper does not allocate learner credit, modify code or instrument test execution. EXPORT_CONTEXT.json records exact commands and helper identity; RAW_DIAGNOSTIC.zip retains original compilation/export output. The relevant policy code and raw curriculum bytes are identical at external audit target `8606c4d74185f594d93941bef6858b2af28e0b75` and this source. These exceptions therefore existed at the audit target; they are not new fixes.

The raw curriculum SHA256 is `fc2a59cdc61ec82640f29743707b67f04812a372e13fac08987aa16e512df2ca`. Inventory denominators are **28 declared policies (Units3-30), 59 objective pools, 75 candidate placements, 44 unique candidate items**. All candidate bindings resolve to prior-unit objectives and their actual checkItemIds; instruction/practice/remediation links were checked mechanically. This does not establish learner-specific eligibility or source/linguistic acceptance for every binding.

POLICY_INVENTORY.csv and POOL_BINDINGS.csv record actual IDs, digests, schemas, skill labels, owner units, candidate counts and selection rules. OBJECTIVE_BINDINGS.csv records teaching/practice/check/remediation links and the model's explicit gating=true decoding default. CANDIDATE_ITEM_BINDINGS.csv records each placement. UNIQUE_CANDIDATE_ASSESSMENT.csv and CANDIDATE_ENGINEERING_READING.csv preserve the 44 candidates' prompts, cues, options, keys and feedback read for this investigation, with declared features and qualifications. This is engineering field reading, not independent native/rights review or a full curriculum/source-fidelity audit.

## What the audit description misses

- **25 policies have two pools; three have three pools.** The currently declared wider policies are Unit6 `unit6-prior-reading-subject-noun-behavior-v2`, Unit9 `unit9-prior-reading-plural-gender-v2`, and Unit13 `unit13-prior-reading-aspect-past-gender-v2`. Their owner sequences are 1/5/4, 1/8/3 and 1/12/11 respectively. Merely reading the retained v1 declarations overlooks current activation.
- **Five targets use history-sensitive allocation:** 3, 5, 6, 9 and13. Current valid misses and least-recent accepted order affect candidate choice; clock strings and optional/invalid rows do not define that ordering. The other23 targets use first eligible candidates. A singleton pool still cannot vary its item.
- Existing schemas1-6 have registered, distinct meanings. New allocation and existing saved immutable assignments are different operations. Reopening an assignment does not resample it. Prior policies remain registered for compatibility.

AUDIT_CLAIM_RECONCILIATION.csv distinguishes actual external descriptions, external acceptance proposals, production-label interpretation and engineering control questions. In particular, the ordinary-credit bypass question is an engineering control question, **not an allegation attributed to the auditor**. Original FINDINGS.csv and its HIGH/OPEN status are unchanged and hash-bound by FINDING_DISPOSITION.json.

## What remains concerning

**All28 policies include the same singleton `u1-i-decode-pair`.** That accounts for28of59 declared objective pools, not a measured learner-attempt frequency. It asks which line is a single letter in uppercase/lowercase print. Its own objective is titled Decode a printed letter pair; its teaching explicitly says the unit does not introduce words. This supports print-case recognition, not evidence of syllable, word or phrase decoding. The production BOUNDED_READING label must not be mistaken for demonstrated substantive reading.

The other candidates are bounded supplied-item form recognition, grammatical classification or interpretation. Their declared skill enums and two/three pool counts do not establish generalisation or adequate breadth. Seven of the44 unique candidates are printed-letter/case tasks. Field reading of all44 does not validate every Russian key, upstream source span, remediation chain or distractor. These issues overlap CC002 and CC027; the overlap does not close any finding.

MA2-027 requires sampling previously taught objectives across more than one skill where applicable; MA2-024/057 require taught-objective scope, and MA2-088 requires completion of due review as part of the main gate. MA2-027/088 do not themselves require held-out/unseen items, a particular number of older units beyond N-1, or a spaced allocation algorithm. Those can be useful stronger proposals, but must not silently be promoted into the cited clauses. MA2-074 makes AnkiDroid the default long-term SRS; it neither forbids internal spacing nor excuses weak cumulative review. REQUIREMENT_RECONCILIATION.csv retains these distinctions. Literal structural compliance and substantive pedagogical adequacy are separate questions; no complete compliance verdict is issued here.

## Actual selected execution

Fresh discovery at the same source enumerated13 selected method identities across six partial classes:13 synthetic skips and zero runtime passes. Actual execution then reported **13 passed / 0 failed / 0 errors / 0 skipped, exit0**. Fresh XML, START/RESULT events and selectors match exactly. Full commands, hashes, sources/input trees and UTC timing are in DISCOVERY/CONTEXT.json and REGRESSION/CONTEXT.json. No new or altered tests were used.

The result must stay split by fixture meaning:

| Fixture scope in the one executed run | Methods | Evidence limits |
| --- | --- | --- |
| Current raw curriculum | 8 passed | Two selected scenarios each for Unit4/6/9/13; not all28 target reviews |
| Historical pre-teaching-correction pack `547457b63b317dea560e4bdc36083c1a4dce315e` | 5 passed | Unit3/5/6 selection/version controls under current production code; not current teaching acceptance |

The four current positive gate/backup scenarios require fresh assignment-bound review answers after ordinary work, all three slots for the wider policies, book attestation, and accepted completion snapshots retained through backup. The current negative scenarios cover missing-taught-owner repair in Unit4 and optional replay, stale review, transaction failure and disabled declaration refusal in Unit6/9/13. The five historical controls exercise miss/least-recent selection, malformed version/untaught-owner rejection and multi-candidate allocation. FIXTURE_SCOPE_RECONCILIATION.json maps every method and actual outcome to its fixture source.

These checks support the presence of a separate due-review gate; they do not measure language learning, prove all-target runtime behavior, or fix weak assessment content. Earlier67/56/11/broad scopes remain separate and are not added to13. The current run is neither full-class nor complete-suite execution.

Main/test compilation, compileDebugSources and main/unit lint analyses were UP-TO-DATE. lintDebug and verifyReleaseGateAttachment executed/PASS. Lint has0 fatal/errors,20 warnings and1 hint. Gate attachment verifies dependency wiring, not public-release approval. No APK assembly, installation or signing.

## Conservation, next work and boundaries

All six source/runtime input trees and the original external return are unchanged. All original66 archive SHA256/CRCs and preceding binding/UI/broad scope hashes verify. The current release-source binding remains `7ce2727f…`; this evidence-only work does not require another digest replacement. The old Unit7 restore cause remains UNCONFIRMED. No schema6 authentic-history fixture was created, no64-history all-runtime replay is claimed, and CC047 remains open.

The next large task is the bounded repair research in REPAIR_WORK_ORDER.md: map source-admitted substantive earlier skills, demonstrate the selected gap, then design any new policy/version and compatibility handling before implementation. The singleton Unit1 objective currently has only this one check item; a name change cannot produce reading evidence. Do not invent Russian, copy Penguin expression, relabel one skill as many, or change meanings of schemas1-6. No special user approval is requested for routine authorized research and engineering; actual review/owner/release acceptance remains separate.

Four screenshot exporters remain excluded: VisualReviewCaptureTest(2), Units35ChunkCaptureTest(2), Units610ChunkCaptureTest(10), Units1115GapCaptureTest(2), total16 methods. No WRITE_* generation. Current APK hash/certificate/signature are NOT_ESTABLISHED; configured identity stays dev.russiancompanion/2.0.0-dev/code5. Install/start settings remain previously saved and unchanged; publication is user-confirmed, independently unobserved. Private Penguin PDF was not accessed. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance remains outstanding and LAST after final independent comprehensive nonphysical audit. No merge, freeze, signing, formal advancement, owner acceptance or release approval.
