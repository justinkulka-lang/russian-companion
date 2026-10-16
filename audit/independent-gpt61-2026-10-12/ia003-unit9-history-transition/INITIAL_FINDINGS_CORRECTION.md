# Initial72 findings and corrections

At beab2fbbd644e95f796fc27f64baf97e95f00d2c:72methods/19classes,70passed/2failed,0errors/skips. Preserve INITIAL_XML, INITIAL_RUN.log/.exit and INITIAL_VERIFICATION_MANIFEST.json. Lint/attachment were not reached.

Unit3 correctionHasAnExactWhitelistAndNoScoredOrPolicyChanges failed atline47 because it applies the historical Unit3-only whitelist to the later Unit9 declaration. Bind only this historical preservation method to the exact post-Unit3 source61c68cfcf20cd92a4b6c310c4abf8ff2284e36e5;all original whitelist/assertions stay intact. The other six Unit3 runtime/repair methods ran on current source and passed. Current Unit9 fiveGenderCandidatesRequireCurrentTaughtEvidenceAndExactRepairWithoutChangingAssetTuples independently proves the current sole asset change is the Unit9 declaration against that same exact baseline.

The new Unit9 gender UI test timed out waiting for the second guided step's book option. It had answered the first guided card but omitted the existing v2-next-practice button, assuming all guided steps appeared together. Production V2Screens has the correct explicit next-practice navigation;the existing ordinary Unit3 UI regressions already use it. Correct the new test to traverse all four cards with each next-practice/return button and assert4 guides. Do not remove a guided route,skip assertions,increase timeout or alter production behavior.

Rerun all9 new Unit9 methods and the corrected historical whitelist as a separate10-method scope with compile/lint/attachment. No production/canonical input changes;not a fresh72 or full-suite result. Initial70pass and final results must not be summed. The earlier compilation failure and deliberately stopped diagnostic scope remain separate.
