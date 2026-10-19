# CC013: bounded backup imports and matching export policy

Corrected source: `8b76738a2e70da4f7f0db3f4b60f588b41aef0fe`. Previous engineering checkpoint: `5ce61345fe9340b571785a1789c03a535a17fe5a`. CC013 is IMPLEMENTED_SCOPED_HOST_VERIFIED_PENDING_INDEPENDENT_RECHECK. Original independent audit statuses remain unchanged; the overall audit is INCOMPLETE.

## Change

The importer previously copied the entire incoming stream, expanded manifest/learner JSON with readBytes(), collected all entry names and copied recordings without resource caps. It now bounds incoming bytes, entry count, ZIP metadata, both JSON payloads, each recording and total expansion. Bounds are enforced while streaming, with cancellation checks. Standard/ZIP64 directory limits are checked before ZipFile allocation, including conflicting-header cases. Existing path/hash/schema/version/identity checks remain.

Rejected or cancelled reads delete only atomically reserved staging files. Existing paths remain intact. The coordinator passes cancellation into the reader and shows an explicit size-limit failure while preserving the learner and allowing the next valid preview. Export uses the same supported limits and fails rather than claiming success for a backup beyond that policy. No curriculum, archive-format/schema, review policy, assignment identity or accepted-history meaning changed.

See IMPORT_POLICY.md for exact caps and their limits. This is prevention demonstrated with small fixtures; no exhaustion/crash exploit was run or claimed.

## Fresh executed evidence

Authoritative latest scope: HARDENED/RESULT.json — **32 methods,5 classes,32 passed,0 failures,0 errors,0 skips**. Exact source/commands/initialization/selector hashes/logs/XML/events and outcomes are retained. Selected method inventory at the exact source matches fresh XML and append-only result events. This is a selected debug-host scope, not a complete-suite result.

The scope contains20 importer/exporter tests,2 Robolectric coordinator limit/cancellation tests,5 CC014 preview tests,2 CC014 coordinator restore/cancel/reopen tests and3 existing backup roundtrip/recording/malformed-integrity controls. The default-policy test reads all64 retained archives; that is one JUnit method with64 checked inputs, not64 separate reported methods. Synthetic zero-byte recording data is test-only, not an admitted audio model or a listening-acceptance result.

Tests cover exact-limit acceptance; unknown-length input stopped at one excess byte; forged expanded-size metadata; aggregate expansion; cleanup of partial files; preservation of unowned files and the learner fingerprint; cancellation during provider read and recording expansion; next valid preview after rejection; normal ZIP64 compatibility; oversized/conflicting primary/ZIP64 metadata; and export policy/output bounds. Existing integrity and CC014 regressions still pass. All66 retained archive allowlist hashes match; all64 backup files are unchanged.

compileDebugKotlin executed; compileDebugSources aggregate was UP-TO-DATE after prerequisites. lintDebug completed with0fatal/errors,20warnings,1hint. verifyReleaseGateAttachment executed/PASS,not release authority. Actual Java version retained. Commands use the prepared activation script,offline cached dependencies,reviewed cloud/broader initialization,4workers/3GiB/forkEvery8,with four screenshot exporters/16methods excluded and noWRITE_* regeneration.

Earlier checkpoints are preserved separately: INITIAL at060d177c had23methods allpassing; FINAL at0eed6c2e had30methods allpassing. Later hardening added ambiguous-ZIP64 checks and atomic staging reservation. Do not sum these overlapping scopes or substitute either earlier source for the authoritative32-method result.

Only existing production files V2BackupFormat.kt and V2BackupCoordinator.kt changed in this task. Two new test classes were added. All prior tests,raw curriculum,catalog,docs,review content,original failures,64 genuine histories and legacy semantics remain exact. SOURCE_CHANGE.patch preserves the source diff; its unified-diff context spaces are intentional.

## Remaining work

CC013 and CC014 are engineering-verified pending independent changed-source/evidence review. Other15 findings remain outstanding,including CC008 actual-validator evidence and CC009 source qualification. EXECUTION_REQUEST_PROGRESS.csv supplies scoped host evidence for the CC013/CC014 request rows; other runtime/accessibility/performance/compatibility requests remain outstanding. No fresh all-pass broad result is claimed; old1225/1208pass/16fail/1physicalskip and26/31-method followups remain separate.

Next engineering priority: CC001 response ordering, preserving keyed values,accepted attempts,saved presentation/resume behavior and legacy review-assignment semantics. Do not implement naive reshuffling that changes an already assigned task after resume. Content/source/rights findings still require their bounded prerequisites and independent recheck. No new audit prompt transfer is needed; Codex retains engineering.

Configured APK:dev.russiancompanion/2.0.0-dev/versionCode5. No new APK assembly/signing/hash/signature. Environment settings unchanged;publication remains user-confirmed and independently unverified. No merge,freeze,formal stage advancement,owner acceptance orrelease approval. Actual Galaxy S26 Ultra airplane-mode/reboot acceptance remains LAST/outstanding after final independent comprehensive nonphysical review of corrected source.
