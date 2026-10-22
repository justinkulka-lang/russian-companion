# Codex return handoff: independent recheck 2026-10-22 (Claude Code)

**Return status:** INCOMPLETE. This return is diagnostic only. It authorises no app change, merge, freeze, Amendment 1 advancement, signing or approval.

**Pins**
- Target: `8606c4d74185f594d93941bef6858b2af28e0b75`
- Delivery: `ae1105fbc78f6b5fbb2dbdce9058b08c5a44792e`
- This return was committed on session branch `ccr-55fcda78-eddach`, based on `ae1105fb`. It was not pushed to the PR64 draft branch (see `INDEPENDENT_RECHECK_DISCREPANCIES.md`).

## Priority order for engineering and content work

1. **Restore the execution path.** The session host could not reach `dl.google.com`.
   - Run the 70 requests (`EXECUTION_EVIDENCE_REQUESTS.csv`) at the exact target, with the 4 exporter classes excluded.
   - Correct the selectors for requests 22 and 60. `*HostActivityLifecycleTest` matches no class; use `*LearnerReloadRecoveryTest` and `*HostActivityRecreationTest` (CC046).
2. **HIGH engineering and accessibility:** CC018 (zeroed insets under edge-to-edge) and CC019 (unlabelled option controls). Add tests on V2 screens, not on the legacy ShellPage.
3. **HIGH assessment design:**
   - CC020: cumulative review is a fixed two-item replay.
   - CC027: non-diagnostic stimuli and options.
   - CC022 and CC015: scripted keys and withheld rules.
   - CC021: remediation omits the checked content.
   - CC023: raw markup used as teaching.
   - CC024: сказать keyed as "speak".
   - CC031: genitive analysed in Unit 7.
4. **Open original scope findings:** CC002–CC007, CC010, CC011, CC017; CC008 for the 5 unpinned tuples; CC009 qualification.
5. **MEDIUM and LOW items:** see `IMPLEMENTATION_WORK_ORDER.csv`. CC048 (variance activation before the final audit) needs an owner and Codex governance decision, not a code edit by itself.
6. Every governed curriculum byte change invalidates digests, assignments and review stages under Amendment 1. Keep the historical evidence untouched.

## What this return verified, so it does not need repeating
- Original ZIP integrity.
- Private PDF identity.
- Mapped book locators.
- HOST_METHOD index alignment with raw outcomes.
- Tree identity across `e5b5d3c0`, `20c5eb96` and the target.
- 64 history digests; 66 archive hashes; the 167-member and 155-record indexes.
- The 95-file digest-only substitution.

## Outputs
All 25 requested files plus `safe_evidence/`. The mechanical scripts are reproducible with `python3 -I`; they write only to their stated output paths.
