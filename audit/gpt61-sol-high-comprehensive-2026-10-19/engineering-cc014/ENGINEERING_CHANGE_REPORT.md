# CC014: restore preview includes cumulative-review history

Latest independent audit received:19outputs,17findings(6HIGH/11MEDIUM),70executionrequests,INCOMPLETE. Archive658763bytes/SHA2560321d323cdd56a3453ced784267f66527c30de5d24e749ba0471285af245016b publishedat24dfcee0 andverifiedbyCodex(all41internalhashes/CRC/schemas/identityconservation);sixuseruploads matcharchived/published originals. Originals unchanged. See ../finish-results/2026-10-19-0321d323/CODEX_RECEIPT_VERIFICATION.json.

## Problem and correction

Restore replaced cumulative-review assignments and answers but omitted both from preview record enumeration. Incoming/removal/conflict counts could therefore show no review changes when two answers would actually be added or removed. V2BackupEngine now serializes both collections under separate record-key namespaces using their existing IDs. Shared review records contribute to replacement counts;changed serialized data under a shared ID contributes to conflicts,consistent with other record types. No archive/schema/history/assignment/completion policy was changed.

Baseline source59457187:five new Kotlin regressions all FAILED as expected before the correction. Their original assertions are unchanged. Each stopped at its first failing witness;do not claim all8pairs ran successfully in baseline. Corrected source68eb86dc:fresh scoped run8methods/3classes,8PASSED/0FAILED/0ERRORS/0SKIPS. Five pure Kotlin methods exercise all8retained pairs for adding/removing2answers,changedselectionconflict both directions,openassignmentadd/remove andunchanged-historyreplacementcounts/no mutation. Two Robolectric tests exercise actual coordinator preview/restore/cancel and actual named database reopen acrossall8pairs. One existing backup roundtrip method also passed.

The coordinator tests use genuine retained host-generated backups;they do not establish current-content eligibility,owner/book acceptance or physicaldevice behavior. Actual preview counts are checked against restored/reopened review collections. Cancellation preserves the learner fingerprint. Changed-selection controls use existing fixture selections rather than new app-native content.

## Execution evidence

BASELINE andCORRECTED retain exactcommands/sourcecommits,log/exit,XML,eventrecords,contexts andresults. FreshXMLtimestamps checked against runs;event/result method identities agree exactly. Corrected eight methods match explicitexpectedselector identities. This is not fresh broad discovery or a complete-suite result;baselineandcorrected counts are not summed.

Corrected command executes testDebugUnitTest with three explicit selectors,compileDebugSources,lintDebug,verifyReleaseGateAttachment,--rerun,--continue,--offline,--max-workers=4 andreviewedcloud/broader init scripts. Activation:/workspace/setup/android-env.sh. Sourcecompilation taskcompileDebugKotlin executed;compileDebugSources aggregate reportedUP-TO-DATE after prerequisitecompilation. Lint:0fatal/errors,20warnings,1hint. Gateattachment executed/PASS,not releaseauthority. Original init-script XML relocation was superseded bytaskconfiguration inbaseline;fresh defaultXML was preserved before subsequentexecution andtheactualsource isrecorded.

All24usedJSON/ZIPfixturehashes verified unchanged. Existing tests and historicalevidence unchanged;onlyexisting productionfilemodified is V2BackupEngine.kt,plusnewtests. Curriculum/catalog/docs/review unchanged. Sourcepatch supplied. Resourcepolicy4forks/3GiB/forkEvery8 on32GiBhost. Four exporters/16methods excluded;noWRITE_*switches. ConfiguredAPKidentitydev.russiancompanion/2.0.0-dev/code5;noAPKassembly/signing ornewartifacthash/signature.

## Disposition and next work

CC014 engineering correction IMPLEMENTED_SCOPED_HOST_VERIFIED_PENDING_INDEPENDENT_RECHECK. Preserve independent audit OPEN status until auditor rechecks corrected source and evidence. One of70evidence-request rows receives this scopedhostevidence;do not close allruntime/accessibility/performance/broad-suiterequests fromit. EXECUTION_REQUEST_PROGRESS.csv tracks the mapping without rewriting originals.

Other16findings remain,includingCC008hypothesis andCC009qualification debt. Next engineering priority:CC013bounded importresource consumption with small injectedlimits andnonmutatingrejection/cancellation/cleanup checks. Follow current17workorders;CC010transferclaim isqualified,no universalunseen-stimulusrule. CR009-CR011 areagainCHANGED_BUT_NOT_RESOLVED due teachingglosses,not blanketresolution75. Preserve all64histories/failures/legacyversions andoriginalUnit7restore uncertainty. Complete unreadindependent/source/rights/bookscope andlatercorrected-sourcerecheck beforefinalacceptance.

Environment settings unchanged;publication remainsuser-confirmed independentlyunverified. No merge,freeze,APKassembly/signing,formalstageadvancement,owneracceptance orreleaseapproval. PhysicalGalaxyS26Ultraairplane-mode/rebootacceptance LAST/outstanding.
