# PR #64 Android CI binding correction

Baseline: `2ec77840719ed4ae7ba04062f692c1653a9e93ba`. Keep this PR draft.

Android CI run [38061390437](https://github.com/justinkulka-lang/russian-companion/actions/runs/38061390437) completed 632 tests, with 12 failures and one skip. Debug assembly and test compilation reached completion before the unit-test failures. Its exact archived Git tree is `764932b376dba0fd27a8c1d9809feeae995e265e`, matching the branch head. The archived source bytes hash to `402ec4c0244647ecdb7c5fc5997a3fe43afc915dc9e87d4786d807ef3cc804ae`; the candidate and active reports retained a different digest from the previous desktop verification. This correction binds the exact committed bytes. It does not normalize source bytes or change the production digest algorithm.

Changes:

- Refresh the unfrozen candidate, all 30 review packets, readiness reconciliation, research dossier and final-audit preparation source bindings. All review-manifest fields other than repositorySourceDigest are identical.
- Refresh 15 stale review-package digest expectations in the Units 6–10 and Batch E tests. Keep every content and mapping digest assertion.
- The legacy Unit 16 relationship test now accepts expanded feedback only when its exact text resolves to the retained pending original-synthesis record, whose source, hashes and unsigned status are checked. Its key-swap, explanation-swap, distractor, source and production-validator checks remain active.
- The formatting test permits stress on complete taught words while requiring the unstressed instrumental suffix. Frozen-source and MediaWiki-emphasis checks remain active.

Host verification:

The initial 12-method repair run passed eight methods and failed four. One failure revealed further stale Batch E review-package expectations. The other three needed historical Git objects absent from the initial local source snapshot. The historical objects were imported from GitHub, each commit/tree/blob SHA verified, and the four-method rerun passed. Real ancestry, historical diff locking and curriculum-at-commit checks run without bypasses. All 12 methods that failed in Android CI now pass across these repair runs. A separate run passes all 21 Unit 16 and release-source-binding tests. There are 32 distinct passing methods across the runs; this is not one full Android-suite pass. Initial failed evidence is retained.

Production curriculum validation reports zero errors. Curriculum, production code, licensed sources, rights, examples, answer keys, options, advancement requirements and mappings are byte-for-byte unchanged. All 30 manifests preserve their content, mapping, pack and review-package digests. Candidate remains unfrozen, sourceCommit null, artifact NOT_RECORDED, publicRelease NOT_APPROVED; review signatures remain pending.

Host tools: Kotlin 2.0.21, serialization 1.7.3 and JUnit Platform 1.11.4, with explicit Android asset and Room annotation stubs. These checks do not exercise Compose, Android persistence, APK execution or physical-device behavior. A new Android CI run must establish the repository-toolchain result separately.

Evidence: `pr64-ci-binding-verification-2026-10-11/`. The 19-finding audit remains open, all 75 correction targets await semantic acceptance, and 126 sequencing flags remain unresolved review candidates. Next content work is Unit 17 ordinal/time instruction. Nothing is marked ready, merged, frozen or released.
