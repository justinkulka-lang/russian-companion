# Next large task: diagnose and correct 14 retained regression failures

Use the existing checkout and one agent on `cursor/content-audit-corrections-964f`, Draft PR64. This work is already authorized. No new confirmation is required. No test job remains active at this checkpoint.

Read ENGINEERING_REGRESSION_REPORT.md, OPEN_FAILURES.json, CURRENT_FAILURE_TRIAGE.csv and IDENTITY_COVERAGE.json here first. Exact source commits, commands, outcome counts and full raw evidence are retained. The final selected scope at `56cd6694645bd35c589e9cfa597a271e2d2eb1ff` completed 509 methods: 501 passed, eight failed, zero errors/skips, exit 1, 80 classes. The two earlier partial scopes remain separate; do not sum their counts into an uninterrupted suite. All 1,320 discovered identities have per-scope records; four interrupted identities were reexecuted explicitly. Four screenshot exporters / 16 methods remain excluded.

## Start with the two Unit 7 behavior failures

`Unit7AgreementTeachingTest#genuineBackupsRetainRawHistoryButChangedTeachingRejectsOldCreditAfterReopen` reaches the completed-unit assertion at line 123: expected units 1–6, observed 1–3. The earlier replacement assertion returned `REPLACED`. This observation is not a restore-refusal diagnosis and does not establish the cause of the older Unit 7 restore failure.

`Unit7AgreementTeachingTest#freshTeachingAndAnswersReestablishCompletionWithoutRelabellingOldAssignments` throws at line 163, where `openCumulativeReview(...)!!` assumes an available assignment. Inspect prerequisite/current-credit decisions and the history's exact original source. Later Unit 4 teaching changes are a hypothesis to investigate, not a proven diagnosis. Preserve every raw assignment, answer, attempt, completion and backup byte. Do not grant credit to stale teaching, lower prerequisites, or bypass replacement ownership to make the tests pass. Separate the original historical contract from genuine current-pack repair behavior. Verify any fixture correction with current eligibility/refusal and history-conservation checks.

## Diagnose the two complete-rule failures

`CompleteRuleCorrectionTest#completeChoicesExplainTheCasesAndAllTeachingPrecedesPractice` fails at line 28, the option-wording assertion. The later teaching-order and full-validator checks in that method were not reached. Reconcile each affected item's admitted scope with the actual implementation sources; preserve the full original rule contract historically where appropriate. Do not weaken current instructional requirements or accept classification as productive application.

`CompleteRuleCorrectionTest#availabilityPlayerShowsCompleteRulesWithItsFullRussianCue` finds two matching cue nodes. Inspect both rendered contexts before changing the selector or UI. Do not hide or delete required cues merely to force uniqueness. Verify the intended item cue and all its answer choices remain visible and usable, including the possession route.

## Reconcile six historical conservation comparisons

- `FragmentGlossTeachingTest#everyNativeItemExampleHistoricalRecordAndUnrelatedUnitRemainsExact`
- `HistoricalTeachingFixtureScopeTest#currentUnit14And15RetainTheirExactCorrectionWhileTheWholePackHasAdvanced`
- `Unit15AspectPreparationHistoryTest#originalItemsExamplesSourcesPoliciesAndOtherUnitsStayExact`
- `Unit4PresentTeachingTest#existingNativeItemsExamplesRecordsAndOtherUnitContentsRemainExact`
- `Unit7AgreementTeachingTest#unchangedQuestionsRecordsAndRequirementsHaveAnExactSessionOnlyException`
- `Unit8ExistenceHistoryTest#frozenEvidenceOtherObjectivesAndLaterGenitiveStayExact`

For each, identify the exact before/implementation/current commits and trace every difference against retained correction records. Retain original assertions against their actual historical source. Add strict current checks against genuine implementation ancestors and the declared correction boundaries; do not replace broad comparisons with unchecked current values or delete conserved-field assertions. Current source pointers for Units 4/7/8/10/15 were already reconciled in `engineering-pending-evidence-reconciliation`; reuse that evidence without assuming all these failures share a cause. Full raw comparisons are in the ZIPs, including a roughly 62 MB Unit 7 message; use bounded previews during investigation and preserve original bytes.

## Reconcile four digest assertions

- `BatchECapabilityResearchTest#digestsGovernanceAndLaterUnitsStayUnchanged`
- `Units1115MappingGapTest#preservedUnitDigestsDoNotMoveWhenTheLaterGapIsRecorded`
- `Units2125SourceIntakeTest#structuralMappingsAreDigestBoundAndStayUnsigned`
- `V2RebuildTest#structuralMappingSafeguardsRejectIncompleteOrInventedRecords`

The Batch E assertion expects aggregate `e90420ab…`; current is `bbd8919a…`. The three mapping assertions stop at Unit 4's old `1ebcde1e…` versus current `c440d047…`. These are observations, not permission to refresh hashes. Preserve exact historical pins on the corresponding source, bind current fields to genuine implementation sources and run every later mutation/refusal/unsigned-governance assertion. Do not bypass checks that were previously unreachable after the first failure.

## Evidence and completion requirements

Preserve all 66 original archives / 64 authentic histories, the 167-file historical generated-evidence archive, all 154 current generated-file hashes and candidate bytes unless a separately justified change requires a newly preserved baseline. Do not silently regenerate tracked pending review material. Keep all approval/reviewer/mapping/signature fields pending and the original 17 external findings at their qualified-review statuses. CC003's contextual application gap remains open.

Use `/workspace/setup/android-env.sh` and the retained init/resource scripts. Save exact selectors, source and input-tree identities, underlying command exit, fresh XML/events and every outcome. Distinguish lint execution from cached analysis. No new APK exists from this work: configured identity is `dev.russiancompanion`, version `2.0.0-dev`, code 5; assembly/install/signing were not run. Keep focused followups separate from these immutable scopes.

The detached launcher completed the final scope after two transport-associated interruptions. Keep future long runners independent of terminal streams, record their PID/session and inspect actual exit/results before publishing. This does not prove the earlier stop causes. Do not launch duplicate jobs or queue source mutations against active test inputs.

Finish justified engineering corrections and checkpoints, then prepare the final comprehensive independent nonphysical recheck against the exact corrected commit and privately obtained Penguin reference. Actual Galaxy S26 Ultra airplane-mode/reboot acceptance remains LAST and outstanding. No merge, freeze, formal stage advancement, owner acceptance, APK assembly/signing or release approval. Original historical audit/four CSVs remain unavailable. Environment settings remain saved; publication is user-confirmed and independently unverified.
