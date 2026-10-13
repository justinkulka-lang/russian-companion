# IA013 Unit 30 engineering verification

Start with FINAL_VERIFICATION_MANIFEST.json for the exact tested commit, command, selected class/method counts, XML hashes, lint/gate results and unexecuted acceptance. This is focused engineering verification, not a complete-suite or linguistic/source/rights acceptance result. Original diagnostic return files and historical evidence remain unchanged.

UNIT30_LANGUAGE_CORRECTION.json under audit/content-corrections records before/after changes and source selections. PRESERVATION.json and UNIT_BINDING_PRESERVATION.json distinguish preserved evidence from intentional reference-role changes and Unit 30 identity changes. Two former name/English-respelling examples remain packaged as disclosed historical orphans; six old reference questions are separately reachable through optional practice. None supplies current language-check credit.

Reproduce preservation without changing the checkout:

```bash
python3 audit/independent-gpt61-2026-10-12/ia013-unit30-verification/verify_preservation.py
```

The helper replays the checked-in repair script against the exact historical baseline in a temporary directory, comparing curriculum and generated native-pin bytes, idempotence, original provenance/source/rights/capabilities/other units, prior pins and diagnostic receipt hashes. It only refreshes PRESERVATION.json on success.

With the documented Android environment active and retained external init scripts available, reproduce the selected integration scope:

```bash
bash audit/independent-gpt61-2026-10-12/ia013-unit30-verification/verify.sh
```

It excludes VisualReviewCaptureTest (2 methods), Units35ChunkCaptureTest (2), Units610ChunkCaptureTest (10) and Units1115GapCaptureTest (2), because they overwrite retained screenshot evidence. It does not assemble an APK or authorize signing, freeze, merge or release. Follow cloud-environment-2026-10-12/START.md under audit/content-corrections for toolchain/bootstrap details.

BASELINE_NEGATIVE.txt and BASELINE_TEST-* retain the expected original-credit failure. INITIAL_COMPILE_FAILURE.txt is a failed compile, with no test execution. INITIAL_CORRECTED_VALIDATION.txt and EXPANDED_VALIDATION.txt are separate overlapping prototype scopes. before-dossier-fix/ retains the failed 33-test integration run and stale exact-orphan expectation. Final XML/log/lint belong to the corrected run; do not add overlapping scope counts together.

The next correction is IA012_UNIT29_CONTEXT_RESEARCH_PENDING.md in the parent diagnostic directory. Final independent comprehensive audit precedes physical-device acceptance. Actual Galaxy S26 Ultra airplane-mode/reboot acceptance remains outstanding and last; host checks provide no physical evidence. The private Penguin PDF must be privately reattached against the durable continuity pin, never obtained from this repository or packet.
