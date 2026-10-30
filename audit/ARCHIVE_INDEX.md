# Audit archive index

Historical audit material that was on `cursor/content-audit-corrections-964f` at `4a3bf74920fa413ffb85935557eb5dfbdfd13bca` is on branch `archive/audit-history-2026-10-29`.

Archive commit: `231a2d31188b48afa256a759dec4389eaf5b44c3`

That commit is the full tree at `4a3bf749` plus a work-queue note that T02 was in progress. It was not recreated for this removal.

The working branch keeps this index. `audit/final-curriculum-audit/` is the directory `RetainedFinalAudit.kt` is allowed to read. That directory was absent at archive time and is still absent. T20 creates the final-audit bundle there.

No file under `app/src`, the Gradle build files, or `.github/workflows/build.yml` opens a path that this index archived. `RetainedFinalAudit.kt` still names `audit/final-curriculum-audit`. Phase 2 and Phase 3 claim tests still check that this directory is absent. `LinguisticReleaseGateTest` still rejects a synthetic `audit/other-audit/` identity created in a temporary stage. Packaged curriculum provenance text in `v2-curriculum.json` still names historical directories; it does not open them.

Tests that only checked archived audit evidence or old development schemas were retired with this tree (A2-08, A2-13, A2-14). The T01-n1 names that left the active baseline are listed in `WORK_QUEUE.md`.
