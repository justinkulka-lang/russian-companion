# Current readiness register

This is a read-only mapping of the 48 independent findings and 70 execution requests. It is not the final comprehensive audit, an implementation pass, or an approval.

The app is not audit-ready. All 48 findings stay independently OPEN. Audit status stays INCOMPLETE. Physical Galaxy S26 Ultra airplane-mode and reboot acceptance stays outstanding and last.

## Target

- Repository: justinkulka-lang/russian-companion
- Branch: cursor/content-audit-corrections-964f
- Draft PR 64: https://github.com/justinkulka-lang/russian-companion/pull/64
- Pinned reviewed target: 8eb9d3d4cc3fdbcd70d5b8904087ddad18acfaf1
- Subject: Preserve verified 1499-method discovery and broad runtime ownership
- Commit time: 2026-10-28 2:06 PM America/Chicago (2026-10-28 19:06:31 UTC)
- PR head observed again on 2026-10-28 at 3:12 PM America/Chicago and at the end of this mapping: still 8eb9d3d4cc3fdbcd70d5b8904087ddad18acfaf1
- Newer head: none
- Review checkout: /tmp/rc-review, read-only use. /workspace stayed on cursor/phase-1-data-model-964f at fc2c94c70cc2ecdb59457e477cb1c7124970eab8
- Model: Grok 4.7. Effort: unknown. No effort-tier control was exposed to this session.
- Original audit target behind the findings: 8606c4d74185f594d93941bef6858b2af28e0b75. That target is not the current pin.

## What was compared

Current claims were checked against the pinned pack and Kotlin, not copied from the October 22 audit as if it were still the source. The original external status of every finding and the NOT_EXECUTED status of every request are preserved.

Input trees at the pin:

- app/src/main: e8434cfe2b18d483ee27be79aba461add2544f38
- app/src/test: 2f06c31e5b789f2c0007537f7d83722c21c548e3
- v2-curriculum.json file SHA-256: 02202d37e0aa2c51182e4db22feff177124b6db2c270a67a93ef85e188916c19 (20,350,052 bytes)

That file hash is the asset bytes. It is not the repository source digest and it is not the pack content-review digest. Those two official identities were not recomputed. The last engineering record cited in the Unit 14 binding checkpoint, repository 487d9118… and pack 39c3563b…, remains a historical label, not a digest produced by this mapping.

Matching trees:

- Broad discovery 2ebf4f282e42eaa5df8eb3a9e2ac491618e79f96 matches both trees. Result: 1499 synthetic skips, 261 classes, zero runtime passes.
- Follow-up discovery 0c6789a291c6170dab792c908dba5c9b379067fa matches the current test tree. It is discovery only: 79 synthetic skips.
- Follow-up runtime 2faa5471c4cf36728c09684584a255268bd34adb matches both trees: 79 reported, 79 passed, 0 failed, 0 errors, 0 skipped, 13 classes, exit 0.
- Affected discovery 8e0b82ddc0ff573697ce4ce9b18abe082ed2c2d0 matches main and not test (test b0a5ec5dd28a990b30d754a425413f96448705d9): 541 synthetic skips.
- Affected runtime dd736e1a8657bd02a34063752afd82146ab2f842 matches main and not test: 541 reported, 526 passed, 14 failed, 0 errors, 1 skipped, exit 1.
- Unit 13 broad cd2b1c91 and the October audit broad e5b5d3c08689cc04eadcb4c2edd155940dee7bf7 do not match the pin. They are historical inputs only.

The broad runtime aimed at 8eb9d3d4 is ACTIVE_RUN_PENDING. Its final result was not available. Discovery and runtime commits stay separate. Overlapping runs are not summed.

Four screenshot exporters stay excluded. Current @Test counts are VisualReviewCaptureTest 2, Units35ChunkCaptureTest 2, Units610ChunkCaptureTest 10, and Units1115GapCaptureTest 2, totaling 16 methods. Host API 33 geometry is not physical TalkBack, API 35, or API 36 behavior. A passing release-gate attachment would not approve release. The provisional SQLite JIT note does not establish the native cause. The original Unit 7 restore cause stays UNCONFIRMED; this mapping did not rehash the 64 histories or isolate that failure.

No Gradle, unit test, lint, build, exporter, emulator, APK, commit, push, or PR edit was run. The active broad worker was not inspected or interrupted. The Penguin PDF and the private-reference branch were not opened.

## Register counts

Findings, 48 rows, independent_closure NOT_CLOSED on every row:

- PARTIAL_ENGINEERING_EVIDENCE: 5 (CC001, CC008, CC013, CC020, CC046)
- CODE_CHANGE_PRESENT_VERIFICATION_GAP: 5 (CC012, CC014, CC016, CC018, CC019)
- OPEN_ENGINEERING_GAP: 33
- UNVERIFIED: 5 (CC021, CC023, CC027, CC028, CC036)
- EVIDENCE_SUPPORTED_PENDING_INDEPENDENT_RECHECK: 0

Execution requests, 70 rows, original execution status NOT_EXECUTED on every row:

- ACTIVE_RUN_PENDING: 10
- TEST_EXISTS_NO_MATCHING_RUNTIME: 18
- MISSING_EVIDENCE: 39
- DEFERRED_PHYSICAL_LAST: 3 (requests 55, 63, and 64)
- RETAINED_RUNTIME_SCENARIO_MATCH: 0
- PARTIAL_SCENARIO_COVERAGE: 0
- HISTORICAL_INPUTS_ONLY: 0 as a sole state. Historical mismatches are recorded in input_tree_match instead of being promoted to current evidence.
- UNVERIFIED: 0 as a request state. Unmeasured content counts are on the finding rows.

No request was marked executed, completed, or waived.

## Most important remaining blockers

1. The broad runtime at the pin has discovery evidence and no runtime result. Until that worker finishes, there is no current whole-suite pass or fail count.
2. CC048 is an unresolved conflict, not a missing file. V2Validator.kt:2035-2037 and :5592-5599 treat Units 27, 29, and 30 as activated and the unresolved set as empty. The 2026-10-02 owner disposition in OWNER_MAPPING_AND_VARIANCE_ACCEPTANCE.md activates the pedagogical boundaries and says that activation is not audit or release approval. MA2-178 in MASTER_AUTHORITY_v2.0.md and Amendment 1 section 3 still say those units stay PROPOSED_NOT_ACTIVATED until acceptance after the final curriculum audit. OWNER_VARIANCE_DECISION_PACKET.md:113-121 still says Unit 29 stays in the unresolved set, PROPOSED_NOT_ACTIVATED, and NOT_PREPARED. This mapping does not pick a side.
3. CC020 is partial and uneven. Units 5-14 now have versioned multi-slot policies of already-taught items, with schemas 7-16. Units 3-4 and 15-30 still assign the decoding singleton plus one prior-unit singleton. None of the policies is an unseen or spaced sample. The Unit 14 follow-up proves the current Unit 14 v2 and Unit 15 v1 controls only.
4. Qualified linguistic, source, rights, and mapping review has not accepted the curriculum. Sampled high findings CC002, CC003, CC004, CC015, CC024, and CC031 still match the current pack text.
5. Units 1-2 still have 33 letters, no audio assets, and disabled listening, pronunciation, and handwriting offers.
6. Backup limits and review-preview code exist, but schemas 6-16 have no cited authentic retained history, and the Unit 7 restore cause is still unconfirmed.
7. Physical device acceptance has not started and is last.

## What current source implements

CC001. Lesson and review option order go through sha256-option-order-v1. The bias measurement, rotation stability, and always-first strategy check were not rerun.

CC012 and CC016. Unit 8 availability is titled as positive existence, and the Unit 8 object has no genitive wording. The three cited teaching bodies now describe phrase case and say the fragment is not a complete action. Those are source observations, not executed acceptance.

CC013 and CC014. Import-limit tests and review-assignment preview fields are present. The 18-method limit pass is an older tree. The current class has 20 methods and is inside the pending broad run. Backup code does not consult StatFs.

CC018 and CC019. Both V2 scaffolds pad the outer modifier with safe drawing united with IME, and they set child window insets to zero. Option rows use selectable or toggleable on the row and a null inner click. TalkBack and API 35/36 rendering were not observed. mergeDescendants is not set on those rows.

CC020. Active policy ids in the pack match cumulativeReviewPolicy: unit5-prior-gender-subject-v2, unit6-prior-present-subject-noun-behavior-v3, unit7-prior-gender-inanimate-case-v2, unit8-prior-present-nominative-agreement-v2, unit9-prior-noun-behavior-plural-gender-v3, unit10-prior-present-numeral-gender-v2, unit11-prior-noun-behavior-possession-absence-v2, unit12-prior-gender-past-gender-v2, unit13-prior-present-aspect-past-gender-v3, unit14-prior-plural-possession-reported-time-v2, then unit15-prior-reading-impersonal-state-v1 through the Unit 30 two-slot v1 policies.

CC008 and CC046. Four synthesis families are pinned and every other family returns true. Requests 22 and 60 now have real class names in the engineering map. The audit request bytes still say NOT_EXECUTED, and no class is named HostActivityLifecycleTest.

CC024, CC031, CC005, CC006, and CC007. u12-i-future-perf-2 still has prompt letter "I will speak" for Я скажу́. u7-i-endings-1 still asks for a genitive phrase. Unit 16 has no surname text, Unit 22 has no general-fraction text, and Unit 20 has no bringing stem.

CC037, CC039, CC040, CC042, and CC048. Units 26, 29, and 30 say they are in development while readiness is CONTENT_COMPLETE_REVIEW_PENDING. Mapped audio is cleared by any one letter, and there are zero audio assets, so the gate is fail-closed for a different reason. Onboarding uses remember. The Gradle wrapper has no distributionSha256Sum, and verification metadata is absent.

## Execution evidence

The only current-tree runtime with a finished result is the 79-method follow-up at 2faa5471. It does not cover the broad suite, the unpinned synthesis negatives, the position-bias scenario, or physical behavior.

Requests whose named methods are in the pending 1499-method discovery and whose method names implement the stated host positive and negative are ACTIVE_RUN_PENDING: 1, 17, 31, 48, 49, 58, 59, 60, 66, and 68. A later pass of those methods still leaves the uncovered cases in the register, including review schemas 6-16, the one-letter audio criterion, and release approval.

Requests 22 and 60 were inspected beyond the class name. LearnerReloadRecoveryTest has one retry method. HostActivityRecreationTest has three recreation and delayed-write methods. RuntimeCorrectionTest has failed-write and late-callback methods. Corrupt pack or database open, and a full stale-epoch matrix, are not those methods.

Requests 55, 63, and 64 are DEFERRED_PHYSICAL_LAST. Host API 33 does not satisfy airplane mode, TalkBack, or device window behavior, and those cases are not waived.

## Recommended order for Codex

Do this in order. Do not start the next engineering edit while the broad worker owns the checkout.

1. Preserve the active broad runtime's complete or incomplete evidence. Record its runtime commit separately from discovery 2ebf4f28. Do not sum it with 541 or 79. Do not treat a green result as audit closure, and separate the legacy UI tests before quoting the pass total.
2. Read the pending classes named above against the uncovered cases. Add only the missing negatives that the existing methods do not encode: review-preview pairs, unpinned tuple mutations, onboarding recreation, and the one-mapped-letter release negative.
3. Host engineering that does not need new curriculum: selected-option rationale (CC034), V2 semantics and inset checks (CC018, CC019), onboarding rememberSaveable (CC040), and wrapper checksum plus exporter exclusion (CC042).
4. Authentic backup fixtures for schemas 6-16 (CC047, CURSOR-R2-001) without rewriting the original archives. Leave the Unit 7 restore cause UNCONFIRMED unless a new isolated reproduction establishes it.
5. Qualified research, not implementation, for foundation audio and handwriting (CC002), the speak/say gloss (CC024), Unit 7 genitive (CC031), duplicate tasks (CC010, CC030), and the book-scope gaps (CC003 through CC007, CC032). Unit 15 curriculum design and implementation stay unstarted. Its current coverage is the v1 two-slot impersonal review and partner-recall aspect checks.
6. Owner decisions: reconcile CC048 without silent interpretation; decide book-directed versus admitted scope for CC032 and CC033; decide whether RECORD_AUDIO is removed or a future feature (CC038).
7. After a final independent nonphysical audit, and only then, Galaxy S26 Ultra airplane-mode and reboot acceptance.

## New concern

CURSOR-R2-001. V2CumulativeReview.kt:7-37 now defines assignment schemas 1 through 16. CC047 reported that the 64 retained histories contained schemas 1-5 only and called out schema 6. Schemas 7-16, added with the Unit 5-14 policies, also have no cited authentic retained history. This does not create CC049 and it does not close CC047.

No other new defect was substantiated beyond residuals already recorded on CC001-CC048. Unrecounted aggregates are evidence gaps, not new measured defect totals.

## Limits

This mapping does not declare the app audit-ready. It does not close any finding, execute any request, approve a mapping, accept a variance, or advance release.
