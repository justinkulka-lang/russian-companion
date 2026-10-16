# IA003: bounded wider review and history transition design

This is a pre-implementation design and genuine baseline capture, not activation,
runtime proof of a new policy, qualified acceptance, or closure of IA003. Production
is unchanged from `c6adbec66cde2d22b7f73289b6328a94a1d1a11d`. Use one agent and Draft PR64.

## First bounded scope

Propose a separately named Unit6 v2 obligation with three independent objective
slots: existing Unit1 printed-pair recognition, Unit4 supplied nominative-subject
recognition, and Unit5 supplied noun-behavior recognition. Retain both Unit4
train/student choices and all five Unit5 taxi/metro/cinema/book/window choices,
their full repairs and guided returns. Use the existing recent-check selector
within each authored pool. This revisits an earlier syntax objective as well as
the immediate earlier morphology objective, instead of repeating only the
immediately previous grammar objective and the same reading pair.

Unit6 is chosen because its current possession/accusative work uses noun gender,
case and noun behavior, and these two prior supplied contrasts are already
taught, independently checked and source-reviewed. It is not chosen as a numeric
milestone. The normative schedule still introduces Unit4 cases, Unit5
indeclinability and Unit6 possession/accusative in that order. This proposal does
not import new protected book content, claim all noun/case skills are covered,
or introduce another vocabulary SRS. It is a bounded first transition, not a
complete solution to recurrence across Units3–30.

Authority read: MASTER_AUTHORITY_v2.0.md MA2-023/024/026/027/074/082–088/175/176;
original diagnostic IA003 and its acceptance test. Review existing
`../ia003-units5-6-recent-selection/DESIGN_AND_SOURCE_REVIEW.md` and
`SOURCE_AND_ELIGIBILITY_INVENTORY.json`, plus their frozen source/rights contexts.
`PROPOSED_SCOPE_INVENTORY.json` pins complete current objective chains and every
candidate's cue, options, key, feedback, remediation, source and rights records.
These are static source declarations, not proof that any learner is eligible or
that the content has qualified approval. The retained source inspection from the
prior checkpoint is referenced, not represented as a fresh linguistic review.

Do not add noun gender as another slot merely because it is earlier. The current
gender repair contains an exception explanation rather than the full set of
independent contrasts, and some independent records require further instructional
coverage review. Do not silently widen that pool or substitute a dictionary flag
for reviewed teaching. Unit1's existing skill enum is not proof of whole-word
decoding or pronunciation. No listening, production or unseen transfer is added.

## Required implementation contract

1. Preserve every v1 policy definition and digest. Register the explicit active
   Unit6 v2 and historical Unit6 v1 definitions by target, ID and digest. Unknown
   IDs/digests and disabled or mismatched active declarations fail closed. Do not
   accept arbitrary historical hashes or infer versions from numeric suffixes.
2. Allocate new Unit6 v2 work with a new separately scoped assignment version.
   Schema1 defaults, schema2 target3 and schema3 targets5/6 retain their exact
   meaning. Unit6 v1 assignments generated as schema1 or schema3 remain immutable;
   do not resample, relabel or append the new slot. Answers stay schema1 unless a
   separate evidenced change is necessary. Preserve the assignment hash algorithm.
3. Check a saved assignment against its registered policy, not just the active
   policy. Validate source/item/teaching/owner currentness and actual taught
   eligibility under that assignment's policy. Both open and answer currently call
   active-policy allocation first: separate existing-assignment eligibility from
   new-policy allocation, so extra v2 eligibility cannot rewrite a valid v1 task.
   This must not let stale/untaught owners or placement-only evidence gain credit.
4. Reconstruct only exact allowed prior Unit6 declaration identities while keeping
   all other current fields. `prescribedUnitIdentityMatches` currently recognizes
   current identity or false/null pre-review identity; it must also recognize the
   explicitly registered prior active-v1 declaration. A content/key/source/teaching
   change must still reject credit. Never whitelist a captured unit digest alone.
5. Apply that same narrowly defined identity compatibility to saved assignments
   whose OWNER is Unit6. Unit7's genuine v1 slot binds the full Unit6 digest;
   changing only Unit6's declaration would otherwise invalidate its already
   accepted review and later history. `reviewAssignmentCurrent` currently compares
   slot owner digests directly. Check later saved assignments and accepted snapshots,
   not only Unit6's own ordinary attempts. Do not normalize unrelated content.
6. Historical completion must bind the recorded policy digest to the linked actual
   assignment, and replay only answers at or before its accepted completion order.
   Do not compare it solely to the current active policy. Preserve acyclic
   completion validation and ordinary requirements. Raw completion flags remain
   insufficient; later wrong answers must not erase genuine earlier completion.
7. Final store advancement already writes `assignment?.policyDigest`, assignment ID
   and maximum accepted review order, rather than looking up the active digest.
   Preserve this correct behavior and assert the actual legacy/v2 row values.
   Keep final transactional gate recheck, rollback, replacement epoch and packless
   refusal. A read-only eligibility query must not create an assignment.
8. Review UI repair against the assignment's registered policy. Preserve complete
   feedback/repair/guided return and the same task. Guided or ordinary work cannot
   substitute for fresh answers to each assigned slot after its boundary.

## Baseline and verification plan

Capture before changing production through ordinary/check/step/attestation,
open/answer and atomic advance APIs. Synthetic learners and book attestations are
host fixtures, not human work. Preserve before-work, ordinary/no-assignment,
unstarted, partial, answered/unadvanced, accepted completion, later wrong answer,
and completed downstream Unit7 states. Each capture exercises actual format3
backup decoding, replacement and a named-database close/reopen. Source, command,
init hashes, fresh XML, original log/exit and state hashes belong in
`BASELINE_CAPTURE.json`. Do not manufacture an older assignment by editing a
newer row's schema/digest. Existing genuine older schema1/2 histories remain at
their original paths, referenced by hash rather than duplicated.

The separate predecessor capture now supplies genuine unstarted/partial Unit6
schema1 histories using the exact `74eb8b655ebd0af388c4fba05a986d04e6ec02a8`
production tree. The runner temporarily overlaid its sole changed production
file, asserted the entire main tree matched that predecessor, and restored the
current source byte-for-byte in finally. Current test sources plus the archived
exporter were compiled; this is not a clean historical full-repository test-commit
claim. Its one focused method passed separately from the current-source one.
All eight schema1 lifecycle states also survived actual backup/replacement/reopen.
See `predecessor-schema1/CAPTURE_CONTEXT.json`, original log/XML/exit and the
combined manifest. This supplies pre-transition evidence, not upgraded v2 proof.

Implement negative tests for wrong policy/target/version, unlinked completion,
wrong role/slot/order/boundary, stale/coherently changed source and teaching,
missing prerequisites, invalid options, packless advance, stale epochs and atomic
replacement/advance rollback. Restore genuine v1 and v2 histories, restart and
continue partial tasks; retain completed Unit7 and reject altered Unit6 content.
Test both new non-first choices and repair paths with all three new slots. Run
affected current Unit6, selector, gate/storage, later-owner and canonical checks.
Refresh source/content/package bindings only after actual implementation ancestry
exists. Preserve prior failures and report exact separate run counts; never sum
overlaps or describe annotation inventory as a complete-suite result.

Four screenshot exporters/16 methods remain excluded: VisualReviewCaptureTest2,
Units35ChunkCaptureTest2, Units610ChunkCaptureTest10, Units1115GapCaptureTest2.
Preserve all6914 provenance records, native/scored/stress/feedback/examples,
source/rights/capabilities, original nine diagnostics and historical receipts.
No private Penguin content is required for this reuse of existing taught checks.
Keep that PDF outside GitHub/assets/logs/packets/environment snapshots; obtain it
as a private attachment against the continuity hash in durable instructions.

Environment settings remain saved and unchanged; publication is USER_CONFIRMED,
independently unverified. No APK assembled or signed. Configured identity remains
dev.russiancompanion/2.0.0-dev/code5; current artifact/hash/signature NOT_RECORDED.
IA003 and broader findings remain OPEN. Final independent comprehensive audit
FIRST; actual Galaxy S26 Ultra airplane-mode/reboot acceptance LAST/outstanding.
No merge, freeze, signing or release approval.
