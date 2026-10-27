"""Single-agent evidence owner. No app/test edits; preserve actual complete or incomplete outcome."""
from evidence import *
from datetime import datetime,timezone
from zoneinfo import ZoneInfo
from collections import Counter
import time,os,traceback
branch='cursor/content-audit-corrections-964f';rel=D.relative_to(R).as_posix()
allowed={'AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md'}
now=lambda:datetime.now(timezone.utc).isoformat()
local=lambda s:datetime.fromisoformat(s).astimezone(ZoneInfo('America/Chicago')).isoformat()
def invoke(args):
    p=subprocess.run(args,cwd=R,text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
    if p.returncode:raise RuntimeError(json.dumps(dict(command=args,exit=p.returncode,output=p.stdout[-6000:])))
    return p.stdout
def active_job_processes():
    found=[]
    metadata=W/'REGRESSION_RUNNER_PROCESS.json';owned=[]
    if metadata.exists():
        m=read(metadata);owned=[m['pid'],m['supervisorPid']]
    for p in Path('/proc').iterdir():
        if not p.name.isdigit() or int(p.name)==os.getpid():continue
        try:
            stat=(p/'stat').read_text().rsplit(')',1)[1].split();argv=(p/'cmdline').read_bytes().split(b'\0');cmd=b' '.join(argv).decode(errors='replace');binary=Path(argv[0].decode(errors='replace')).name if argv else ''
            if stat[0]!='Z' and (int(p.name) in owned or (binary=='java' and (str(W) in cmd or 'Gradle Test Executor' in cmd))):found.append(dict(pid=int(p.name),state=stat[0],command=cmd[:180]))
        except OSError:pass
    return found
def publish(title,report,nexttext,result):
    assert git('rev-parse','HEAD')==S and git('branch','--show-current')==branch
    (D/'REPORT.md').write_text(report)
    (D/'NEXT_TASK.md').write_text('# Next task\n\n'+nexttext+'\n\nRead REPORT.md and raw verification/context/conservation records. One agent/currentcheckout/DraftPR64;no new confirmation required. All48findingsOPEN/auditINCOMPLETE/physicalLAST/noAPK/signing/merge/freeze/releaseapproval.\n')
    for name in allowed:
        p=R/name;link=rel+'/REPORT.md' if name=='AGENTS.md' else '../'+rel.removeprefix('audit/')+'/REPORT.md'
        p.write_text(f'Current checkpoint: [{title}]({link}). Actualsource{S[:8]};{result["summary"]}.276testfiles/sixinputs/155bindings/74archives conserved;original627+107assertions/currentcontrols unchanged. Read NEXT_TASK.md. Earlier scopes separate/unsummed. All48OPEN/auditINCOMPLETE/four exporters16methods excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
    manifest()
    if result['complete']:(W/'FINAL_VERIFICATION.json').write_text(invoke(['python3','-B',str(W/'verify_results.py')]))
    else:
        verify_archive('RAW_INCOMPLETE');conservation()
        for n,h in read(D/'SHA256.json').items():assert sha((D/n).read_bytes())==h
    raw=subprocess.check_output(['git','status','--porcelain=v1','-z','--untracked-files=all'],cwd=R);changes=[x for x in raw.split(b'\0') if x];assert changes
    for row in changes:
        assert row[2:3]==b' ' and not any(k in row[:2] for k in [b'R',b'C']),row
        p=row[3:].decode();assert p in allowed or p.startswith(rel+'/'),p
    invoke(['git','add',*sorted(allowed),rel]);invoke(['git','diff','--cached','--check']);invoke(['git','commit','-m',title])
    head=git('rev-parse','HEAD');(W/'PUSH.log').write_text(invoke(['git','push','origin','HEAD:'+branch]))
    body=f'''{title}. Checkpoint `{head}`;actual runtime source `{S}`. **{result['summary']}**. Start `{rel}/REPORT.md` and NEXT_TASK.md. {nexttext}

Fresh discovery1477synthetic skips/257classes/zero runtime passes;all1458prior broad IDs+19Unit13IDs/no removals/all63follow-upIDs included. Whole debug host-test task,NO method/class filters. Selected-host scope,not complete app/curriculum/device or independent acceptance. Original316partial/165continuation/63fullclass and prior1458broad scopes are immutable,separate/unsummed. The original six failure messages/pins/assertions were retained;five historical comparisons reconciled without changing production or weakening current controls.

All276test-source files/sixinput trees/155bindings/74protectedarchives conserved;original627+107assertions/current controls byte-exact. Raw commands/source roots/XML/events/exits/cache/lint/gate/native/CRC/memberSHA and prior-failure rechecks in the packet. Four screenshot exporters16methods explicitly excluded:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. Provisional SQLite JITcompilationdirective unchanged/unadopted/nativecauseUNCONFIRMED;it excludes no test methods. No APKassembly/install/signing;identity/signature null;gate attachment is not release approval.

All48independent findingsOPEN/auditINCOMPLETE. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance LAST after final independent comprehensive nonphysical audit. Qualifiedreviews/CC024risk/19print-singletons/CC048authority/originalUnit7restorecause outstanding. No merge/freeze/formal advancement/releaseapproval. PrivatePDF unread/unpackaged/environmentsettings unchanged/publication user-confirmed independently unobserved. Repositorysource3c0acbb95297cf10f08da2bd8391463067eece22fec785158b5d2680d37dc2ba differs from packcontent15c6ae393767cb7ffc190882867bfad923c9e81b07d0f4285cf0a678f2489280. Historicalmain562passes and420f41ff691sourceinventory are not complete-suite evidence.
'''
    (W/'PR64_BODY.md').write_text(body);pr=json.loads(invoke(['gh','api','--method','PATCH','repos/justinkulka-lang/russian-companion/pulls/64','-F','body=@'+str(W/'PR64_BODY.md'),'--jq','{number,state,draft,head:.head.sha}']));save(W/'PR_UPDATE.json',pr)
    assert pr['state']=='open' and pr['draft'] and pr['head']==head and not git('status','--porcelain') and git('ls-remote','origin','refs/heads/'+branch).split()[0]==head
    save(W/'AUTO_RESULT.json',dict(sourceCommit=S,checkpointCommit=head,**result,verified=True,pushed=True,draftPR64Updated=True,clean=True,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',completeSuite=False,finishedUtc=now()))
try:
    with (W/'AUTO_COMPLETION.lock').open('x') as f:f.write(str(os.getpid())+'\n')
    save(W/'AUTO_PROCESS.json',dict(pid=os.getpid(),startedUtc=now(),authority='Verify/preserve actual complete or incomplete outcomes,audit-docs-only commit,normalpush,DraftPR64 update;no app/test changes'))
    c=read(W/'REGRESSION/CONTEXT.json');S=c['sourceCommit'];started=datetime.fromisoformat(c['startedUtc']).timestamp();absentSince=None
    while not (W/'REGRESSION_COMPLETION.json').exists():
        active=active_job_processes()
        if active:absentSince=None
        elif time.time()-started>120:
            absentSince=absentSince or time.time()
            if time.time()-absentSince>60:
                save(W/'INCOMPLETE_PROCESS_DISAPPEARANCE.json',dict(observedUtc=now(),sourceCommit=S,underlyingExit=None,cause='UNCONFIRMED;original supervisor/runner/owned Java workers absent for60seconds after startup grace',activeOwnedProcesses=[],lastMonitor=read(W/'DURABLE_MONITOR_STATUS.json') if (W/'DURABLE_MONITOR_STATUS.json').exists() else None));break
        time.sleep(20)
    end=read(W/'REGRESSION_COMPLETION.json') if (W/'REGRESSION_COMPLETION.json').exists() else None
    assert git('rev-parse','HEAD')==S and not git('status','--porcelain')
    if end and end['resultExists'] and end['manifestExists']:
        while not read(W/'DURABLE_MONITOR_STATUS.json')['completionExists']:time.sleep(2)
        (W/'COLLECTION.log').write_text(invoke(['python3','-B',str(W/'collect.py')]))
        v=json.loads(invoke(['python3','-B',str(W/'verify_results.py')]));save(D/'RESULT_VERIFICATION.json',v)
        r=read(W/'REGRESSION/RESULT.json');n=r['testCounts'];monitor=read(W/'DURABLE_MONITOR_STATUS.json');recheck=read(D/'PRIOR_FAILURES_RECHECKED.json')
        failures=read(D/'ACTUAL_FAILURES.json');skips=read(D/'SKIPPED_METHODS.json')['methods']
        expectedSkip={('dev.russiancompanion.acceptance.AcceptanceBoundaryTest','at020_physicalRebootPersistsGovernedRecords')}
        good=r['exitCode']==0 and not failures['count'] and {(t['testClass'],t['method']) for t in skips}==expectedSkip and recheck['allPassed'] and not monitor['newNativeReports']
        counts=f"{n['reported']} reported / {n['passed']} passed / {n['failed']} failed / {n['errors']} errors / {n['skipped']} skipped"
        report=f'''# Unit13 broad selected-host regression completed

Actual tested source `{S}`. **{counts}**,257classes,Gradle/runner exit{r['exitCode']}. Started{local(c['startedUtc'])},finished{local(r['finishedUtc'])} (America/Chicago;raw UTC retained). Fresh discovery1477synthetic skips/zero runtime passes;exact fresh XML/events/method IDs/source roots/six input trees/exits agree. All1458prior broad IDs plus19Unit13methods/fourclasses retained,zero removals;all63current follow-up IDs included. Whole Gradle debug host-test task,NO method/class filters. This is selected-host regression,not complete app/curriculum/device acceptance or independent audit closure.

Actual failures:{json.dumps([dict(testClass=t['testClass'],method=t['method']) for t in failures['failures']])}. Full messages/traces retained in ACTUAL_FAILURES.json and RAW_BROAD.zip. Actual skipped identities:{json.dumps(skips)};physical skip is NOT Galaxy evidence. Native reports:{json.dumps(monitor['newNativeReports'])}. Prior failure groups6/8/2 reduce to12unique identities and are rechecked in PRIOR_FAILURES_RECHECKED.json;overlap is never summed. All prior failure records remain immutable.

All276current test-source files and sixinput trees byte-exact at parent57f27050. Includes five comparison corrections retaining627original assertion/helper calls and two prior Unit12corrections retaining107original assertions/pins;current controls unchanged. All155current binding records and74protected archives SHA+CRC verified. Prior63/63evidence and original627assertion conservation independently reverified read-only. No production/test/source-binding regeneration or repair during this run;pipeline comparisons do not authorize source mutation. Original316partial/165continuation/63followup and prior1458broad scopes remain separate/unsummed,not one retrospectively completed481run.

Lint:{json.dumps(r['lintIssuesBySeverity'])}. Actual tasks/cache states:{json.dumps(r['tasks'])}. Gate attachment is not release approval. APKidentity/signature null;assembly/install/signingNOT_RUN. Source repositorydigest3c0acbb95297cf10f08da2bd8391463067eece22fec785158b5d2680d37dc2ba distinct from packcontent15c6ae393767cb7ffc190882867bfad923c9e81b07d0f4285cf0a678f2489280;older label correction preserved. Exact commands/activation/initializerSHA/source trees/XML/events/GC/lint/logs/exits in separate RAW_DISCOVERY.zip and RAW_BROAD.zip with memberSHA/CRC. Four screenshot exporters16methods excluded:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. ProvisionalSQLiteJITcompilationdirective unchanged/unadopted/nativecauseUNCONFIRMED;no test methods excluded by it.

Historicalmainff539220563reported562pass1skip differs in commit/scope;420f41ff691is source inventory/unverified handoff execution,with67focused methods verified separately. No counts are added across sources/scopes. All48independent findingsOPEN/auditINCOMPLETE. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance LAST after final independent comprehensive nonphysical audit. Qualifiedreviews/CC024risk/19print-singletons/CC048authority/originalUnit7restorecause outstanding. No merge/freeze/formal advancement/releaseapproval. PrivatePDF unread/unpackaged/environmentunchanged/publication user-confirmed independently unobserved. One agent/currentcheckout/DraftPR64.
'''
        nexttext=('Next large task: bounded Unit14 substantive cumulative-review research from this verified current source. Inventory actual objectives/checks/current policy/saved-history boundaries and remaining CC020 limits;do not assume a version/schema or activate a proposal. Preserve all old policy meanings,actual source bindings and historical evidence. Capture an actual-current baseline and genuine partial backup before any later implementation. Qualified review and final independent comprehensive nonphysical audit remain outstanding;physical acceptance last.' if good else 'Next task: inspect every full actual broad failure,unexpected skip,native report or task error at the pinned source. Preserve outcomes and original assertions/pins/current controls. Reconcile narrowly,then execute affected full-class follow-up before another broad run. Do not advance curriculum work while broad failures remain unresolved.')
        publish('Preserve actual Unit13 broad selected-host regression outcomes',report,nexttext,dict(complete=True,counts=n,classes=257,exitCode=r['exitCode'],good=good,allPriorFailedIdsRechecked=True,allPriorFailedIdsPassed=recheck['allPassed'],summary=counts+',257classes,exit'+str(r['exitCode'])))
    else:
        while active_job_processes():time.sleep(20)
        if end:
            while not read(W/'DURABLE_MONITOR_STATUS.json')['completionExists']:time.sleep(2)
        else:
            while not read(W/'DURABLE_MONITOR_STATUS.json').get('runtimeInactiveWithoutExit'):time.sleep(2)
        events=[];p=W/'REGRESSION/EVENTS.jsonl'
        if p.exists():
            for line in p.read_text().splitlines():
                try:events.append(json.loads(line))
                except ValueError:pass
        results=[e for e in events if e.get('kind')=='RESULT'];keys=[(e['className'],e['name']) for e in results];assert len(keys)==len(set(keys))
        expected={(t['testClass'],t['method']) for t in read(W/'SCOPE.json')['expectedMethods']};assert set(keys)<=expected
        partial=dict(Counter(e['resultType'] for e in results));summary=f'INCOMPLETE: {len(results)}partial method events of1477expected;event counts{json.dumps(partial)};no verified final broad runtime result'
        archive_mode('REGRESSION','RAW_INCOMPLETE',monitor=True)
        save(D/'INCOMPLETE_RESULT.json',dict(sourceCommit=S,kind='PARTIAL_RUNTIME_EVENTS_NOT_FINAL_JUNIT_RESULT',expectedMethods=1477,partialEventCount=len(results),eventOutcomeCounts=partial,originalCompletion=end,underlyingExit=end['gradleExit'] if end else None,finalRuntimeResult='UNAVAILABLE',cause='UNCONFIRMED unless an actual raw native/exit report establishes it',completeSuite=False,countsNotSummed=True))
        save(D/'PARTIAL_RESULTS.json',results);save(D/'FINAL_CONSERVATION.json',dict(sourceCommit=S,**conservation()))
        report=f'''# Unit13 broad runtime INCOMPLETE

Runtime source `{S}`,started{local(c['startedUtc'])}. **{summary}**. Actual supervisor completion:{json.dumps(end)}. No final runtime XML/event reconciliation or broad pass is claimed. Stop causeUNCONFIRMED unless actual raw evidence establishes it. RAW_INCOMPLETE.zip preserves partial raw events/logs/GC,original context/command,process metadata/monitor and all available exits. Indexed memberSHA/CRC verified. FINAL_CONSERVATION.json preserves276testfiles/sixinputs/155bindings/74archives. Original failure events are retained without waivers. Do not sum this partial scope with earlier runs or mistake synthetic discovery skips for runtime evidence.

Fresh discovery1477synthetic skips/257classes/zero runtime passes was verified/saved before launch;all1458prior IDs+19Unit13/no removals. Four exporters16methods excluded explicitly:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. No APKassembly/install/signing/identity/signature;no final lint or gate claim unless an actual exit/task record proves it. All48findingsOPEN/auditINCOMPLETE/physicalLAST after final independent comprehensive nonphysical audit. No merge/freeze/formal advancement/releaseapproval. Environment/privatePDF unchanged;JITdirective unadopted/nativecauseUNCONFIRMED.
'''
        publish('Preserve incomplete Unit13 broad runtime evidence',report,'Next task: inspect raw context/events/exits/native/process evidence and conserve this incomplete scope. Verify no original workers survive before planning an explicitly separate continuation or retry. Do not invent a final result,combine counts,waive failures,or advance Unit14. Keep source inputs and all prior evidence intact.',dict(complete=False,good=False,partialEventCount=len(results),partialEventCounts=partial,underlyingExit=end['gradleExit'] if end else None,summary=summary))
except BaseException:
    save(W/'AUTO_ERROR.json',dict(error=traceback.format_exc(),utc=now()));raise
