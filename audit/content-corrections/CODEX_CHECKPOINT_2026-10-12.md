# Codex checkpoint — 2026-10-12

Authorized repository: `justinkulka-lang/russian-companion`; branch `cursor/content-audit-corrections-964f`; existing Draft PR64. Baseline/source implementation checked out: `420f41ff6c2a4417cd829fdee0179b6a3b797eff`. This checkpoint's own containing Git commit is a documentation/evidence continuation commit; obtain its exact identity with `git log -1 --format=%H -- audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md`. Do not treat it as a new app implementation or a newly signed artifact.

## Reconciliation and historical verification

Initial onboarding ran on main commit `ff539220645670fbf7a1abacaa6f71e828606b80`. Its complete command, per-class XML-derived counts/report hashes, lint severities/hash, gate attachment result and existing debug APK identity/signature are retained in `cloud-environment-2026-10-12/MAIN_HISTORICAL_VERIFICATION.json`, `MAIN_LINT.xml`, and `MAIN_APK_SIGNATURE.txt`.

Main execution: 563 reported tests, 562 passed, zero failures/errors, one skipped physical-reboot method, 84 report classes. It excluded four screenshot-export classes containing 16 tests and therefore was not a complete-suite result. Lint passed with zero errors, 20 warnings and one hint. APK and release-gate attachment checks passed on main, not on PR64. No release approval followed.

Main source annotation inventory is 579; the authorized PR64 baseline inventory is 691. PR64 adds 112 source tests. Main's 579 minus 16 excluded exporters gives 563 reported tests. Subtracting its one skip gives 562 passes. The handoff's reported 691-test run at `420f41ff…` cannot be independently certified without the missing handoff/report; matching source inventory alone proves neither execution nor passes.

PR64 current-instance verification is recorded separately in `cloud-environment-2026-10-12/PR64_VERIFICATION.json`: **67 tests passed across eight classes, zero failures/errors/skips**, with fresh lifecycle execution. Compilation, lint (zero errors, 20 warnings, one hint), and release-gate attachment passed. The exact retained install script exited 0. Its selected test scope is focused, not a complete suite; 624 source test annotations were not selected. No new signing or APK assembly is requested by that command. The old main APK remains historical and must not be represented as the PR64 candidate.

## Durable setup and publication

The draft `install_script` and `start_skill` were saved in the platform configuration during initial setup, with backend status `saved` and `requires_publish: true`. A draft save is not editor activation or publication. The retained setup script now uses compilation, lint, gate attachment and the explicitly selected PR64 tests without initiating signing. Platform draft persistence and publication status must be recorded independently.

Publication is **not performed**: available supported tools expose draft read/update only, not editor save/activation or publication. The setup skill says, “The user publishes the prepared environment through the product after setup” (`cloud-environment-onboarding:setup`, `references/onboarding.md`). It also prohibits prototype finalization or fabricated publication links. User authorization is present; the blocker is unavailable supported publication capability. Do not invent an endpoint or claim a snapshot exists.

## Inputs and access still missing

- `Russian_Companion_Codex_Handoff_2026-10-12.zip`: absent from accessible attachment references and workspace files. Its complete resume prompt and original audit/four CSVs cannot yet be read, reconciled, deduplicated or imported.
- `Private_Penguin_Reference.pdf`: absent. Expected SHA-256 cannot yet be recovered. Next session must obtain a separate private attachment and compare it to the independently supplied expected hash; keep bytes and extracted text outside GitHub/app assets.
- GitHub API access: `gh pr view 64 --repo justinkulka-lang/russian-companion --json …` returned Forbidden. A read-only REST probe failed at proxy CONNECT with HTTP 403. Git read access works; this does not establish API access. The custom `api.github.com` network addition was saved to the draft, preserving existing presets/domains; a post-save retry still returned proxy CONNECT HTTP 403. Draft saving did not activate runtime access.

The original audit is not fabricated or duplicated. Existing pending target CSV is tracked, but equivalence with all missing originals is unverified. The four tracked OpenRussian CSVs are not historical audit files.

## Next work, after publication and full handoff reconciliation

Read `UNIT27_PAST_INSTRUCTION_FOLLOWUP.md`: Unit 27 comprehension and optional-present instruction remain separate work; inspect exact contextual application-pair bindings; proceed to Units 28–30 and the broader open audit. Existing inventory records 72 single-guided sequence-review candidates, 75 targets with no semantic acceptance, and the open 19-finding audit. These inventories are review prompts, not automatic defects or closure evidence.

Continue with one agent and bounded checkpoints. Preserve all tracked evidence and source capability/provenance/rights boundaries. Keep Draft PR64 draft. Actual Galaxy S26 Ultra airplane-mode/reboot acceptance remains outstanding. Do not merge, freeze, sign, or approve release.
