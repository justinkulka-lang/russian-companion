"""Exclusive broad completion owner: preserve outcomes; publish audit/docs only."""
from evidence import *
from datetime import datetime,timezone
from zoneinfo import ZoneInfo
from collections import Counter
import os,time,traceback
branch='cursor/content-audit-corrections-964f';rel=D.relative_to(R).as_posix()
allowed={'AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md'}
now=lambda:datetime.now(timezone.utc).isoformat()
def invoke(args):
    p=subprocess.run(args,cwd=R,text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
    if p.returncode:raise RuntimeError(json.dumps(dict(command=args,exit=p.returncode,output=p.stdout[-6000:])))
    return p.stdout
def active_jobs():
    p=W/'REGRESSION_RUNNER_PROCESS.json';owned=[]
    if p.exists():q=read(p);owned=[q['pid'],q['supervisorPid']]
    found=[]
    for p in Path('/proc').iterdir():
        if not p.name.isdigit() or int(p.name)==os.getpid():continue
        try:
            state=(p/'stat').read_text().rsplit(')',1)[1].split()[0];args=(p/'cmdline').read_bytes().split(b'\0');cmd=b' '.join(args).decode(errors='replace')
            if state!='Z' and (int(p.name) in owned or (Path(args[0].decode(errors='replace')).name=='java' and (str(W) in cmd or 'Gradle Test Executor' in cmd))):found.append(int(p.name))
        except (OSError,ValueError):pass
    return found
def publish(result,report,nexttext):
    assert git('rev-parse','HEAD')==S and git('branch','--show-current')==branch
    (D/'REPORT.md').write_text(report)
    (D/'NEXT_TASK.md').write_text('# Next task\n\n'+nexttext+'\n\nRead REPORT.md and full original outcomes. One agent/existing checkout/OPEN DraftPR64. All48findingsOPEN/auditINCOMPLETE/physicalLAST/noAPK/signing/merge/freeze/releaseapproval.\n')
    for n in allowed:
        p=R/n;link=rel+'/REPORT.md' if n=='AGENTS.md' else '../'+rel.removeprefix('audit/')+'/REPORT.md'
        p.write_text(f'Current checkpoint: [Unit14 broad host verification outcome]({link}). Actual{S}: {result["summary"]}.281testfiles/sixinputs/155bindings/77archives/previousfailureevidence exact;all original assertions/pins/currentcontrols conserved. Read NEXT_TASK.md. Earlier scopes separate/unsummed. All48OPEN/auditINCOMPLETE/four exporters16methods excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
    manifest()
    if result['complete']:(W/'FINAL_VERIFICATION.json').write_text(invoke(['python3','-B',str(W/'verify_results.py')]))
    else:
        verify_archive('RAW_INCOMPLETE');conservation()
        for n,h in read(D/'SHA256.json').items():assert sha((D/n).read_bytes())==h,n
    raw=subprocess.check_output(['git','status','--porcelain=v1','-z','--untracked-files=all'],cwd=R)
    for row in raw.split(b'\0'):
        if not row:continue
        assert row[2:3]==b' ' and not any(k in row[:2] for k in [b'R',b'C']),row
        n=row[3:].decode();assert n in allowed or n.startswith(rel+'/'),n
    for p in D.rglob('*'):
        if p.is_file():assert p.stat().st_size<100*1024*1024,(p,p.stat().st_size)
    invoke(['git','add',rel,*sorted(allowed)]);invoke(['git','diff','--cached','--check'])
    invoke(['git','commit','-m','Preserve actual Unit14 broad selected-host verification outcomes'])
    head=git('rev-parse','HEAD');invoke(['git','push','origin',branch]);assert git('ls-remote','origin','refs/heads/'+branch).split()[0]==head
    body=f'''Checkpoint `{head}`: Unit14 broad selected-host verification. Actual tested source `{S}`. **{result['summary']}**.

Evidence: `{rel}/REPORT.md`, `NEXT_TASK.md`, fresh raw discovery/runtime archives with memberSHA/CRC, exact command/source/trees/XML/events/exits/lint/gate/cache/native and prior-failure records. {nexttext}

Fresh discovery1499synthetic skips/261classes/zero runtime passes;all1477prior broad identities retained+22Unit14/no removals/all79follow-up IDs/all541affected IDs/all24unique prior-failure IDs included. Whole debug host-test task with NO method/class filters. Four screenshot exporters16methods explicitly excluded:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. This is not complete app/curriculum/device or independent acceptance;physical @Ignore is not Galaxy evidence.

All281test-source files/sixinputtrees/155bindings/77archives and all protected prior failure/follow-up evidence exact. All original757assertions/pins/currentcontrols and unchanged current Unit13/14 controls conserved. No app/test/source-binding changes during broad preparation/runtime. Complete failure messages/traces retained losslessly;ACTUAL_FAILURES.json is an indexed projection,ACTUAL_FAILURES_FULL.zip retains exact full bytes and RAW_BROAD.zip retains XML/events/logs. Original541run526pass14fail1skip and separate79/79follow-up remain immutable/separate/unsummed. Earlier1477broad and old562passes/691inventory are also separate,not complete-suite evidence.

APKidentity/signaturenull;no assembly/install/newsigning. Gate attachment is not releaseapproval. Repositorydigest487d9118d7154dd59162dbb7024f1349bc53d5b7bf89e9d79022d97eaa1eeb9a differs from packcontent39c3563ba1b149b36977ae0fc2c0f920e282101e9e7870c06d141fe1a10e65ec. Provisional SQLite JITdirective remains unadopted and excludes no test method;nativecauseUNCONFIRMED.

All48independentfindingsOPEN/auditINCOMPLETE. PhysicalGalaxyS26Ultra airplane-mode/reboot acceptance outstanding and LAST after final independent comprehensive nonphysical audit. Qualifiedreviews/CC024risk/18print-singletonpolicies/CC048authority/originalUnit7restorecause unresolved. No merge/freeze/formal advancement/releaseapproval. PrivatePDF unread/unpackaged/environment unchanged/prior publication user-confirmed independently unobserved. One agent/existing checkout/OPEN DraftPR64.
'''
    (W/'PR64_BODY.md').write_text(body)
    pr=json.loads(invoke(['gh','api','--method','PATCH','repos/justinkulka-lang/russian-companion/pulls/64','-F','body=@'+str(W/'PR64_BODY.md'),'--jq','{number,state,draft,head:.head.sha}']))
    save(W/'PR_UPDATE.json',pr);assert pr['state']=='open' and pr['draft'] and pr['head']==head and not git('status','--porcelain')
    save(W/'AUTO_RESULT.json',dict(sourceCommit=S,checkpointCommit=head,**result,verified=True,pushed=True,draftPR64Updated=True,clean=True,finishedUtc=now(),all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',completeSuite=False))
try:
    with (W/'AUTO_COMPLETION.lock').open('x') as f:f.write(str(os.getpid())+'\n')
    save(W/'AUTO_PROCESS.json',dict(pid=os.getpid(),startedUtc=now(),authority='Preserve actual complete/incomplete broad outcomes;verify evidence;only audit/docs commit;normal push;DraftPR64 update;no source/test/binding edits'))
    c=read(W/'REGRESSION/CONTEXT.json');S=c['sourceCommit'];started=datetime.fromisoformat(c['startedUtc']).timestamp();absentSince=None
    while not (W/'REGRESSION_COMPLETION.json').exists():
        if active_jobs():absentSince=None
        elif time.time()-started>120:
            absentSince=absentSince or time.time()
            if time.time()-absentSince>60:
                save(W/'INCOMPLETE_PROCESS_DISAPPEARANCE.json',dict(observedUtc=now(),sourceCommit=S,underlyingExit=None,cause='UNCONFIRMED;owned jobs absent beyond grace'));break
        time.sleep(20)
    end=read(W/'REGRESSION_COMPLETION.json') if (W/'REGRESSION_COMPLETION.json').exists() else None
    assert git('rev-parse','HEAD')==S and not git('status','--porcelain')
    constraints='All48independentfindingsOPEN/auditINCOMPLETE. PhysicalGalaxyS26Ultra airplane-mode/reboot acceptance outstanding and LAST after final independent comprehensive nonphysical audit. Fourexporters16methods excluded. NoAPKassembly/install/newsigning/merge/freeze/formal advancement/releaseapproval;APKidentity/signaturenull. Qualifiedreviews/CC024risk/18print-singletons/CC048authority/originalUnit7restorecause unresolved. PrivatePDF unread/unpackaged/environment unchanged/publication user-confirmed independently unobserved/JITdirective unadopted/not a test exclusion/nativecauseUNCONFIRMED. Every earlier scope remains separate/unsummed;no complete-suite claim.'
    if end and end['resultExists'] and end['manifestExists']:
        while not read(W/'DURABLE_MONITOR_STATUS.json')['completionExists']:time.sleep(2)
        (W/'COLLECTION.log').write_text(invoke(['python3','-B',str(W/'collect.py')]))
        r=read(W/'REGRESSION/RESULT.json');n=r['testCounts'];monitor=read(W/'DURABLE_MONITOR_STATUS.json')
        prior=read(D/'PRIOR_FAILURES_RECHECKED.json');follow=read(D/'CURRENT_FOLLOWUP_RECHECKED.json');u13=read(D/'UNIT13_CONTROLS_RECHECKED.json');u14=read(D/'UNIT14_CONTROLS_RECHECKED.json')
        skips=read(D/'SKIPPED_METHODS.json')['methods'];expectedSkip={('dev.russiancompanion.acceptance.AcceptanceBoundaryTest','at020_physicalRebootPersistsGovernedRecords')}
        good=r['exitCode']==0 and n['failed']==n['errors']==0 and prior['allPassed'] and follow['allPassed'] and u13['allPassed'] and u14['allPassed'] and not monitor['newNativeReports'] and {(t['testClass'],t['method']) for t in skips}==expectedSkip
        summary=f"{n['reported']} reported / {n['passed']} passed / {n['failed']} failed / {n['errors']} errors / {n['skipped']} skipped;261 classes;exit{r['exitCode']}"
        local=lambda s:datetime.fromisoformat(s).astimezone(ZoneInfo('America/Chicago')).isoformat()
        report=f'''# Unit14 broad selected-host verification

Actual tested source `{S}`: **{summary}**. Runtime {local(c['startedUtc'])} → {local(r['finishedUtc'])} America/Chicago;exact UTC retained. Whole debug host task,NO method/class filters. Fresh discovery1499synthetic skips/261classes/zero runtime passes verified before launch;all1477prior IDs+22Unit14/no removals. Actual XML/events/source/inputtrees/exits/counts agree.

All79follow-up identities rechecked/pass:{follow['allPassed']};24unique historical failure IDs rechecked/pass:{prior['allPassed']};all19currentUnit13 controls pass:{u13['allPassed']};all22currentUnit14 controls pass:{u14['allPassed']}. Original failure groups overlap and are not summed. All541affected IDs included. Every full actual failure message/trace is retained in ACTUAL_FAILURES_FULL.zip and RAW_BROAD.zip,with ACTUAL_FAILURES.json identity/hash index. Actual skips:{json.dumps(skips)}. The physical @Ignore is NOT actual Galaxy evidence. Native reports:{json.dumps(monitor['newNativeReports'])}.

All281test-source files/sixinputs/155bindings/77protectedarchives and prior failure/follow-up evidence exact. No source mutation in broad phase. Earlier seven-file reconciliation retained all757original assertion/helper calls and pins;prior79/79result and conservation independently reverified read-only. All current controls unchanged. This broad result does not close qualified linguistic/source/rights/governance findings. Earlier541failed/79follow-up/1477broad and every older scope remain immutable/separate/unsummed.

Lint:{json.dumps(r['lintIssuesBySeverity'])}. Actual tasks/cache:{json.dumps(r['tasks'])}. Compile UP-TO-DATE or cache states are not fresh execution. Exact commands/activation/runner/initSHA/source roots/XML/events/GC/logs/lint/gate/exits/native data in separate SHA-indexed archives;memberSHA/CRC verified. Gate attachment is not releaseapproval. APKidentity/signaturenull;no assembly/install/newsigning. Repositorysource487d9118d7154dd59162dbb7024f1349bc53d5b7bf89e9d79022d97eaa1eeb9a differs from packcontent39c3563ba1b149b36977ae0fc2c0f920e282101e9e7870c06d141fe1a10e65ec. Four exporters16methods explicitlyexcluded:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2.

Historicalmainff539220563reported562pass1skip differs in commit/scope. Handoff420f41ff691sourceinventory/reported execution remains unverified;67focused methods were separately verified. No counts summed and no complete-suite claim.

{constraints}
'''
        nexttext=('Next large task: bounded Unit15 substantive cumulative-review research from this verified source. Inventory actual objectives/checks/policy/saved-history boundaries and remaining CC020 limits;do not assume a schema/version or activate a proposal. Preserve every old policy meaning/source binding/history;capture actual-current behavior and genuine partial backup before any later implementation. Qualified review and final independent comprehensive nonphysical audit remain outstanding;physical acceptance last.' if good else 'Next task: inspect every full actual broad failure,unexpected skip,native report or task error at the pinned source. Preserve original outcomes/assertions/hash pins/currentcontrols. Reconcile narrowly,then full affected-class follow-ups before another broad run. Do not advance Unit15 curriculum work while broad failures remain unresolved.')
        publish(dict(complete=True,good=good,counts=n,classes=261,exitCode=r['exitCode'],all24PriorFailureIdsPassed=prior['allPassed'],all79FollowupIdsPassed=follow['allPassed'],all19Unit13ControlsPassed=u13['allPassed'],all22Unit14ControlsPassed=u14['allPassed'],summary=summary),report,nexttext)
    else:
        while active_jobs():time.sleep(20)
        key='completionExists' if end else 'runtimeInactiveWithoutExit'
        while not read(W/'DURABLE_MONITOR_STATUS.json').get(key):time.sleep(2)
        archive_mode('REGRESSION','RAW_INCOMPLETE',monitor=True)
        events=[];p=W/'REGRESSION/EVENTS.jsonl'
        if p.exists():
            for line in p.read_text().splitlines():
                try:e=json.loads(line)
                except ValueError:continue
                if e.get('kind')=='RESULT':events.append(e)
        keys=[(e['className'],e['name']) for e in events];expected={(t['testClass'],t['method']) for t in read(W/'SCOPE.json')['expectedMethods']};assert len(keys)==len(set(keys)) and set(keys)<=expected
        counts=dict(Counter(e['resultType'] for e in events));summary=f'INCOMPLETE:{len(events)} partial events/1499expected;{json.dumps(counts)};no verified final runtime result'
        save(D/'PARTIAL_RESULTS.json',events);save(D/'FINAL_CONSERVATION.json',dict(sourceCommit=S,**conservation()))
        save(D/'INCOMPLETE_RESULT.json',dict(sourceCommit=S,expectedMethods=1499,partialEvents=len(events),counts=counts,originalCompletion=end,underlyingExit=end['gradleExit'] if end else None,stopCause='UNCONFIRMED unless raw evidence establishes it',finalRuntimeResult='UNAVAILABLE',completeSuite=False))
        publish(dict(complete=False,good=False,partialEvents=len(events),partialCounts=counts,summary=summary),'# Unit14 broad runtime INCOMPLETE\n\n'+summary+'\n\n'+constraints,'Inspect original partial/exit/process/native evidence;verify no jobs survive before separately planning continuation/retry. Do not invent final counts,sum scopes,waive failures or advance Unit15.')
except BaseException:
    save(W/'AUTO_ERROR.json',dict(utc=now(),error=traceback.format_exc()));raise
