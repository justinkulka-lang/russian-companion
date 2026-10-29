# Unit12 affected integration: full failure diagnosis

This read-only review follows evidence commit `451d5d0255f18cd6075f2549652c269112e32109`. Tested source remains `6e485a79fd617c20f6f7ece94c5e1bdc9c505ec3`: **462 reported, 453 passed, 8 failed, 0 errors, 1 skipped; exit 1**. No new JUnit executions, application changes, assertion changes, or finding closures occurred during this diagnosis.

`FULL_FAILURE_REVIEW.json` records every failed identity, full-message byte length and SHA256, original XML member, and comparison proof. Reproduce it with `python3 audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit12-source-reconciliation-2026-10-27/review_integration_failures.py --check` from the repository root. The original full messages remain in `ATTEMPT_1/RAW.zip`.

| Failed class | Methods | Exact observed mismatch |
| --- | ---: | --- |
| Unit11BindingConservationTest | 2 | Prior reference expects 6 pending records; current has 7. The other comparison differs only by the Unit12 policy-ID token after the strict historical restoration chain. |
| Unit11NounCompatibilityTest | 1 | Full pack comparison differs only by the Unit12 policy-ID token; every other byte matches. |
| Unit12CumulativeReviewTest | 2 | Prior v1 expects objective/owner Unit1; current v2 requires Unit3. The old placement fixture establishes Unit1 rather than current owner Unit3. |
| Units1620SourceIntakeTest | 1 | Retained prior Unit12 content digest compared with current Unit12 declaration. |
| Units2125SourceIntakeTest | 1 | Shared historical mapping helper lacks the Unit12 historical branch. |
| Units2630SourceIntakeTest | 1 | Retained prior Unit12 content digest compared with current Unit12 declaration. Later rights/release assertions in this failed method were not reached. |

The two large comparisons were checked in full, including their opposite expected/actual directions. All 15 current Unit12 core/baseline/UI methods and both new binding methods passed **within this same 462-method run**; these 17 are a subset, not an additional run or added total. Their named identities are in the JSON. This supports the diagnosis; it does not close the failures or prove the remainder of the failed methods.

Next: reconcile only the proven historical comparison inputs, preserving original assertions, source pins, saved-v1 semantics, and actual-current mutation/owner/reference guards. Retain originals as historical evidence before editing. Then perform fresh discovery and a separate full-class affected follow-up covering all eight failed identities, direct helper callers, and current controls. Preserve the original failed run unchanged and do not add overlapping test counts.

The four screenshot exporters remain excluded (16 methods): VisualReviewCaptureTest (2), Units35ChunkCaptureTest (2), Units610ChunkCaptureTest (10), Units1115GapCaptureTest (2). All 48 independent findings remain OPEN; audit INCOMPLETE. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance remains outstanding and last after the final independent comprehensive nonphysical audit. No APK assembly, installation, signing, merge, freeze, formal advancement, or release approval.
