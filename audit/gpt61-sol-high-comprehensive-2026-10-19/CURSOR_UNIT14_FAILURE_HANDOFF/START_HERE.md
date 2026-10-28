# START HERE

This directory is one evidence-only handoff. It does not change app or test source, and it does not execute verification.

## Coordinates

- Repository: `justinkulka-lang/russian-companion`
- PR64 branch, not modified by this delivery: `cursor/content-audit-corrections-964f`
- Documentation checkpoint this branch starts from: `e2d09925c1eb90cb1db0a427ef03c77e62b9a005`
- Actual tested source: `8eb9d3d4cc3fdbcd70d5b8904087ddad18acfaf1`
- Broad evidence checkpoint: `5b012b30a1c7827a1e421ff3234ec13a47c1b281`
- Reviewer for this package: Grok 4.7. Effort was not recorded as a separate observed setting.

The cumulative-review test files and `LocalStore.kt` are identical between the tested source and the documentation checkpoint. Line numbers below are those shared lines.

## Inspected scope

Retained evidence, read only:

- `audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit14-broad-host-regression-2026-10-28/ACTUAL_FAILURES_FULL.zip` at `5b012b30a1c7827a1e421ff3234ec13a47c1b281`. SHA256 `e0ca232b5dd36688d069f9840465512fbea89806b49292d0a6ef67891b327cd0`. Sole member `ACTUAL_FAILURES.json`, 122,281 bytes, SHA256 `1fdebe5bcb07107347fc2ffa2beed1875136f6c42885664ba493a25ae0e5fa98`.
- The loose index `ACTUAL_FAILURES.json` in that same directory.
- `RAW_BROAD.zip` in that same directory, including all 261 JUnit XML suites.
- `REGRESSION_RESULT.json` in that same directory.
- `Unit14CumulativeReviewTest.kt` through `Unit30CumulativeReviewTest.kt` and `LocalStore.kt` at the tested source.

Not inspected for this package: screenshot-exporter bodies, linguistic acceptance, the Penguin PDF, and any re-execution. The broad result is selected host scope: 1,499 reported / 1,438 passed / 60 failed / 0 errors / 1 skipped, 261 classes, exit 1, `completeSuite` false. The skip is `AcceptanceBoundaryTest.at020_physicalRebootPersistsGovernedRecords`.

## Limitations

The exception text does not name the unmet requirement list. Causal support comes from the stack line, the two-index answer immediately above it, the passing Unit 14 and Unit 15 controls, and the passing two-slot historical schema methods. Target-method bodies did not run. This package does not prove that no later assertion will fail after preparation succeeds.

## Inventory

- `START_HERE.md`
- `CURSOR_ACTUAL_FAILURE_RECONCILIATION.md` — original report, unchanged
- `CURSOR_ACTUAL_FAILURE_RECONCILIATION.csv` — original header and all 15 class rows
- `CLARIFICATIONS.md`
- `FAILURE_EVIDENCE_MAP.csv` — 60 failure rows with hashes taken from the full archive member
- `FOLLOWUP_SCOPE.csv` — 119 source `@Test` methods
- `CORRECTION_BOUNDARIES.md`
- `PACKAGE_VERIFICATION.json`
- `SHA256SUMS.txt` — every other file in this directory

## Next bounded Codex action

Review this package. If accepted, edit only `prepare` in the 15 files named in `CORRECTION_BOUNDARIES.md`: after the existing answers to indices 0 and 1, answer `fourteenth.slots.indices.drop(2)` before `advanceFrom(14)`. Then run the 119 methods in `FOLLOWUP_SCOPE.csv` as full classes. Do not start that run from this package. Do not treat this package as execution evidence, merge approval, or release approval.

All 48 independent findings remain OPEN. The audit remains INCOMPLETE. Physical Galaxy S26 Ultra airplane-mode and reboot acceptance remains last. No merge, freeze, signing, or release approval.
