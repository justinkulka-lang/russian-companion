# Full Android CI readiness-document corrections

Pull-request run `37953341967`, job `113897495334`, tested branch head `f0c1f4e3a3572a9637c3978bced65854c12f2bbd` through merge checkout `8dc92ce1e499c3cf051e0f08c03cae035eb3aa8e`. Debug assembly, lint and gate attachment succeeded. The suite completed 597 tests with two failures and one skipped physical-device test. This is historical verification before the remediation and Unit 11 correction batches, not a current-head pass.

The two remaining failures were documentation consistency checks:

- The committed readiness JSON recorded `NOT_GENERATED` for the merged debug manifest because it was generated without an Android build. CI had actually built the manifest and observed `ABSENT` for the Internet permission. The Markdown comparison passed, but the JSON comparison failed. Committed source snapshots now explicitly record `NOT_OBSERVED_IN_SOURCE_SNAPSHOT` for that ephemeral build observation. Live reconciliation still reads the actual merged manifest, and the Android matrix test still requires `ABSENT`. Every other field remains bound to the live source facts.
- The gap-closure dossier's digest headers still identified the earlier curriculum. Its current bindings now match the live pack and source digests. Historical baseline/Unit 23 correction identities, the research-only purpose, matrix, unsigned approvals and release restrictions are preserved.

The new regression compares the source snapshot with the live model after changing only the build-dependent observation, and checks the explicit non-attestation value and explanation. Host verification also includes the complete gap-closure dossier test. Exact historical failure identity and counts are retained in `PR64_FULL_SUITE_f0c1_FAILURES.json`.

No Android assertion is replaced with a fabricated manifest, and no APK or physical-device acceptance is inferred from a source snapshot. Current-head Android verification remains pending. PR64 stays draft and release stays NOT_APPROVED.

Fresh host verification completed 40 tests, 40 passed, zero failed or skipped. It includes both previously failing documentation checks. Output and XML are retained as `pr64-unit11-verification-2026-10-09/HOST_JUNIT_40.*`; this is not an Android-suite pass.
