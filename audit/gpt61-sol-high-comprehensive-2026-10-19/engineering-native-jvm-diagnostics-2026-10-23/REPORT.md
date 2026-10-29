# Bounded JVM diagnostics completed; native cause remains unconfirmed

Source for all executed scopes: **8811c19449a259e834657ece2ecfe156db0d5423**,Draft PR64branch cursor/content-audit-corrections-964f. App/main,test,catalog,docs,review,release inputs are unchanged from the earlier e66dfe07native-failure invocation. No app,test,dependency,lockfile or persistent environment setting was changed. This is engineering diagnosis,not an independent audit or release decision.

| Separate scope | Methods / classes | Actual result | JVM/resource policy |
| --- | --- | --- | --- |
| ORACLE | 7 / 1 full class | 7passed,0failed/errors/skipped,exit0 | Original Oracle21.0.12.1+1-LTS-4;6GiB/forkEvery4,maxParallelForks2,one effective class worker |
| ORACLE_BATCH | 29 / 2 full classes | 29passed,0failed/errors/skipped,exit0 | Same Oracle;one6GiBfork/forkEvery4;instructional class then inset class |
| ORACLE_TARGETED_JIT | Same29 / 2 full classes | 29passed,0failed/errors/skipped,exit0 | Same single-fork policy/order;one additional compiler directive |

These are **three separate scopes**,not65distinct methods or a fresh broad pass. ORACLE's7methods are included in both29method batches. All have fresh scoped discoveries,matching XML/event method identities/outcomes and matching six input trees. DISCOVERY's7/29/29synthetic skips are not runtime passes. Runtime methods are all actual passes in these scopes. Exact commands,selectors,commits/trees,initializers,worker settings,method CSVs,XML/events/GC/lint and real exits are retained per scope and in indexed RAW_*.zip archives. Source-level conditions,not only runner exit,were checked.

The7method class is V2InsetsAccessibilityTest. The two-class batch adds all22methods of Units610InstructionalValidityTest. Those two classes started within the original crashed worker's35second lifetime window;timestamps alone do not prove original PID/class ownership. The scoped batch explicitly uses one shared test JVM to exercise that class order. One GC PID log per batch confirms the shared-worker setup. Original broad execution allowed two concurrent forks,so the bounded batch is not an exact reproduction of every scheduling condition.

The original native crash **did not reproduce** in either unmodified scope. Its cause is still **UNCONFIRMED**. Native-failure baseline e66dfe07,including C2CompilerThread0/Node::uncast/SupportSQLiteConnection.prepare,exact hs_err/compiler replay,1121SUCCESS/6FAILURE/3SKIPPED events,233without completed event,zero fresh XML and intentional client cancellation exit130,is preserved unchanged in engineering-current-host-regression-2026-10-23/NATIVE_FAILURE. The original six assertion failures remain open;none is closed by these different selected tests. The historical Unit7restore cause remains separately UNCONFIRMED.

## Provisional compiler experiment

Only this test-worker directive differs between the two29method batches:

`-XX:CompileCommand=exclude,androidx/sqlite/driver/SupportSQLiteConnection.prepare`

This prevents compilation of the specific Java wrapper named as the current C2task in the fatal report. It is **not a test exclusion**,does not disable assertions or C2globally,and does not change app bytecode. The JVM's own log confirms `CompileCommand: exclude androidx/sqlite/driver/SupportSQLiteConnection.prepare bool exclude = true`. Installed JDK help confirms the directive is supported. All29methods still execute. CONTROLLED_COMPARISON.json records the exact change,matching source/method/order/resource conditions and the absence of reproduction. Since the unmodified batch also passed,this is **PROVISIONAL_NOT_PROVEN_FIX**,not proof of a repaired JDK or a stable broad harness. Do not adopt it silently or relabel the original crash as resolved.

JDK_IDENTITIES.json records installed Oracle and Debian21.0.12.1release/executable/libjvm identities. Only Oracle was used for these Gradle test scopes;the Debian JVM was inspected,not substituted or functionally tested. No new JDK was installed. No compiler flag was added to android-env.sh,Gradle project files,saved install_script or saved start_skill. No configuration draft save was needed for this isolated experiment. Earlier saved configuration/publication status remains unchanged:publication user-confirmed,independently unobserved.

All three runtime scopes have lint **0fatal/errors,20warnings,1hint** and gate **attachment PASS**. Main/test compilation and aggregate compileDebugSources were UP-TO-DATE;lint analyses reused valid unchanged-input outputs,while lintDebug and the attachment task ran. TASK lines in each RESULT.json preserve exact cache/execution status. These are scoped results;the prior cancelled native invocation's lint/gate remain NOT_REACHED. Attachment verification is not release approval. No APK assembled/installed/signed;current APK identity/signature null.

## Next large task prepared,not launched

Fresh BROAD_TARGETED_JIT discovery at8811c194 found **1363methods/229classes**,1363synthetic skips/zero runtime passes,exit0. Exact method set and all six trees match the earlier7641174abroad discovery,which retained all1334earlier executed identities and added29. The experimental broad runtime is **PREPARED_NOT_LAUNCHED**. It uses the targeted directive with the original two6GiBforks/forkEvery4/max-workers2 policy. BROAD_EXPERIMENT_PREPARATION.json gives the command,source/trees and explicit provisional status. Full integration is needed before any claim that this option restores trustworthy broad verification. NEXT_TASK.md gives restart/duplicate-job precautions and completion criteria. No current long test job is active.

All155generated records,66original archive SHA/CRCs and original native/preceding evidence are conserved. Canonical source identity remains2507d56bf2bd910b198d9ff317a605598765e13d947fa4f4435aaacd2a626892:the exact official-computation input trees are unchanged;no regeneration or approximate digest calculation. The earlier three-file source-reference/conservation proposal stays unapplied/uncompiled/untested. Broad integration must not be called all-pass while its six known assertion failures remain.

Four screenshot exporters/16methods remain explicitly excluded: VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. The selected instructional class is not the excluded Units610ChunkCaptureTest. No WRITE_* flags or additional test exclusions. Prior562/563main-onboarding,691sourceinventory/unverifiedhandoff at420f41ff,67focused and later1334/56/85/41/21/5/66scopes remain separate;different commits/scopes explain the totals. No complete app/curriculum/device-suite claim.

Independent audit **INCOMPLETE;all48findings open**. API33injected geometry/semantics is not TalkBack/API35–36/physical acceptance. Actual Galaxy S26 Ultra airplane-mode/reboot acceptance remains outstanding and **LAST**,after final comprehensive independent nonphysical audit. Qualified linguistic/source-rights/mapping/owner reviews and CC048authority conflict remain outstanding. Candidate unfrozen/sourceCommitnull/artifactNOT_RECORDED/publicReleaseNOT_APPROVED. No merge,freeze,formal advancement or release approval. Private Penguin remains outside app/main/audit packets;no PDF packaged.
