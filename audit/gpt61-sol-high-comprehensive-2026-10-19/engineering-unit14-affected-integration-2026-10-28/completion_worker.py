"""Exclusive completion owner: preserve actual outcomes; audit/docs-only normal push. No fixes."""
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
    found=[];m=W/'REGRESSION_RUNNER_PROCESS.json';owned=[]
    if m.exists():q=read(m);owned=[q['pid'],q['supervisorPid']]
    for p in Path('/proc').iterdir():
        if not p.name.isdigit() or int(p.name)==os.getpid():continue
        try:
            state=(p/'stat').read_text().rsplit(')',1)[1].split()[0];argv=(p/'cmdline').read_bytes().split(b'\0');cmd=b' '.join(argv).decode(errors='replace')
            if state!='Z' and (int(p.name) in owned or (Path(argv[0].decode(errors='replace')).name=='java' and (str(W) in cmd or 'Gradle Test Executor' in cmd))):found.append(int(p.name))
        except (OSError,ValueError):pass
    return found
def publish(result,report,nexttext):
    assert git('rev-parse','HEAD')==S and git('branch','--show-current')==branch
    (D/'REPORT.md').write_text(report)
    (D/'NEXT_TASK.md').write_text('# Next task\n\n'+nexttext+'\n\nRead REPORT.md/raw outcomes/source/conservation. One agent/existing checkout/DraftPR64;no new confirmation required. All48OPEN/auditINCOMPLETE/physicalLAST/noAPK/signing/merge/freeze/releaseapproval.\n')
    for name in allowed:
        p=R/name;link=rel+'/REPORT.md' if name=='AGENTS.md' else '../'+rel.removeprefix('audit/')+'/REPORT.md'
        p.write_text(f'Current checkpoint: [Unit14 affected outcome preserved]({link}).Actual{S[:8]}:{result["summary"]}.All281testfiles/sixinputs/155bindings/77archives exact;no test edits/waivers. All original assertions/pins/currentcontrols conserved. Read NEXT_TASK.md. Earlier scopes separate/unsummed. All48OPEN/auditINCOMPLETE/four exporters16methods excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
    manifest()
    if result['complete']:(W/'FINAL_VERIFICATION.json').write_text(invoke(['python3','-B',str(W/'verify_results.py')]))
    else:verify_archive('RAW_INCOMPLETE');conservation()
    raw=subprocess.check_output(['git','status','--porcelain=v1','-z','--untracked-files=all'],cwd=R);rows=[x for x in raw.split(b'\0') if x];assert rows
    for row in rows:
        assert row[2:3]==b' ' and not any(k in row[:2] for k in [b'R',b'C']),row
        n=row[3:].decode();assert n in allowed or n.startswith(rel+'/'),n
    invoke(['git','add',rel,*sorted(allowed)]);invoke(['git','commit','-m','Preserve actual Unit14 affected integration outcomes'])
    head=git('rev-parse','HEAD');invoke(['git','push','origin',branch]);assert git('ls-remote','origin','refs/heads/'+branch).split()[0]==head
    body=f'''Checkpoint `{head}`: Unit14 affected integration outcome preserved. Actual runtime source `{S}`. **{result['summary']}**. Evidence and instructions: `{rel}/REPORT.md`,NEXT_TASK.md. {nexttext}

Fresh discovery541synthetic skips/96fullclasses/zero runtime passes;all481prior affected IDs retained+60scope additions/all22currentUnit14controls. Inherited87classes expanded with9missingolder14/downstream15classes before execution. No test edits;281testfiles/sixinputtrees/155reconciledbindings/77archives conserved. Full raw failures and all original assertions/pins/currentcontrols preserved. Not a complete-suite or independent acceptance claim;prior20core/2binding/1477broad and all earlier scopes separate/unsummed.

Exact commands/source/XML/events/counts/exits/task/cache/lint/gate/native/CRC/memberSHA retained. Four exporters16methods excluded:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. APKidentity/signaturenull;noAPKassembly/install/newsigning. Gate attachment is not releaseapproval. Repositorysource487d9118d7154dd59162dbb7024f1349bc53d5b7bf89e9d79022d97eaa1eeb9a DISTINCT pack39c3563ba1b149b36977ae0fc2c0f920e282101e9e7870c06d141fe1a10e65ec.

All48independentfindingsOPEN/auditINCOMPLETE. PhysicalGalaxyS26Ultra airplane-mode/reboot acceptanceLAST after final independent comprehensive nonphysical audit. Qualifiedreviews/CC024risk/CC048authority/originalUnit7restorecause outstanding. No merge/freeze/formaladvancement/releaseapproval. PrivatePDFunread/unpackaged/environmentunchanged/publication user-confirmed independently unobserved/JITdirectiveunadopted/nativecauseUNCONFIRMED. Oneagent/existingcheckout/DraftPR64.
'''
    (W/'PR64_BODY.md').write_text(body);pr=json.loads(invoke(['gh','api','--method','PATCH','repos/justinkulka-lang/russian-companion/pulls/64','-F','body=@'+str(W/'PR64_BODY.md'),'--jq','{number,state,draft,head:.head.sha}']))
    save(W/'PR_UPDATE.json',pr);assert pr['state']=='open' and pr['draft'] and pr['head']==head and not git('status','--porcelain')
    save(W/'AUTO_RESULT.json',dict(sourceCommit=S,checkpointCommit=head,**result,verified=True,pushed=True,draftPR64Updated=True,clean=True,finishedUtc=now(),all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',completeSuite=False))
try:
    with (W/'AUTO_COMPLETION.lock').open('x') as f:f.write(str(os.getpid())+'\n')
    save(W/'AUTO_PROCESS.json',dict(pid=os.getpid(),startedUtc=now(),authority='Verify/preserve actual complete or incomplete outcomes,audit-docs-only commit,normalpush,DraftPR64 update;no app/test/sourcebinding changes'))
    c=read(W/'REGRESSION/CONTEXT.json');S=c['sourceCommit'];started=datetime.fromisoformat(c['startedUtc']).timestamp();absentSince=None;scope=read(W/'SCOPE.json');n=scope['methodCount']
    while not (W/'REGRESSION_COMPLETION.json').exists():
        if active_jobs():absentSince=None
        elif time.time()-started>120:
            absentSince=absentSince or time.time()
            if time.time()-absentSince>60:
                save(W/'INCOMPLETE_PROCESS_DISAPPEARANCE.json',dict(observedUtc=now(),sourceCommit=S,underlyingExit=None,cause='UNCONFIRMED;owned supervisor/runner/workers absent beyond grace',activeOwnedProcesses=[]));break
        time.sleep(20)
    end=read(W/'REGRESSION_COMPLETION.json') if (W/'REGRESSION_COMPLETION.json').exists() else None
    assert git('rev-parse','HEAD')==S and not git('status','--porcelain')
    constraints='All48OPEN/auditINCOMPLETE/four exporters16methods explicitly excluded/physicalGalaxyS26Ultra airplane-mode/reboot acceptanceLAST after final independent comprehensive nonphysical audit. NoAPKassembly/install/newsigning/merge/freeze/formaladvancement/releaseapproval;APKidentity/signaturenull. PrivatePDFunread/unpackaged/environmentunchanged/publication user-confirmed independently unobserved/JITdirectiveunadopted/not test exclusion/nativecauseUNCONFIRMED. Qualifiedreviews/CC024risk/CC048authority/originalUnit7restorecause unresolved. Prior20core/2binding/1477broad,316interrupted/165continuation/63followup/1458broad and historical562passes/691sourceinventory remain immutable,separate/unsummed,not complete-suite evidence.'
    if end and end['resultExists'] and end['manifestExists']:
        while not read(W/'DURABLE_MONITOR_STATUS.json')['completionExists']:time.sleep(2)
        (W/'COLLECTION.log').write_text(invoke(['python3','-B',str(W/'collect.py')]))
        r=read(W/'REGRESSION/RESULT.json');counts=r['testCounts'];fail=read(D/'ACTUAL_FAILURES.json');skip=read(D/'SKIPPED_METHODS.json');monitor=read(W/'DURABLE_MONITOR_STATUS.json');controls=read(D/'UNIT14_CONTROLS_RECHECKED.json')
        good=r['exitCode']==0 and fail['count']==0 and controls['allPassed'] and not monitor['newNativeReports'] and {(x['testClass'],x['method']) for x in skip['methods']}=={('dev.russiancompanion.acceptance.AcceptanceBoundaryTest','at020_physicalRebootPersistsGovernedRecords')}
        summary=f"{counts['reported']}reported/{counts['passed']}passed/{counts['failed']}failed/{counts['errors']}errors/{counts['skipped']}skipped,96classes,exit{r['exitCode']}"
        local=lambda t:datetime.fromisoformat(t).astimezone(ZoneInfo('America/Chicago')).isoformat()
        report=f'''# Unit14 affected integration completed

Actual tested source `{S}`: **{summary}**. Runtime {local(c['startedUtc'])} → {local(r['finishedUtc'])} America/Chicago;rawUTC retained. Actual fresh XML/events/discovery IDs/sixinputtrees/exits agree. Fresh discovery541synthetic skips/96classes/zero runtime passes;all481prior affected IDs+60scope additions/all22current14controls retained. Original87class inventory omitted9older14/downstream15classes,which were added before discovery. All selected classes run in full;not complete app/curriculum/device acceptance.

All281testsourcefiles/155reconciledbindings/77protectedarchives/sixinputtrees unchanged. No test edits,resnapshots,waivers,policy/source changes or allowlist expansion. Original assertions/pins/currentcontrols exact. Every actual full failure message/trace retained in ACTUAL_FAILURES.json/RAW_AFFECTED.zip. Failure IDs:{json.dumps([dict(testClass=x['testClass'],method=x['method']) for x in fail['failures']])}. Actual skips:{json.dumps(skip['methods'])}. All22current14controls passed:{controls['allPassed']}. Native reports:{json.dumps(monitor['newNativeReports'])}. A physical @Ignore is NOT device evidence. Do not infer historical-scope justification until full failure/source review.

Lint:{json.dumps(r['lintIssuesBySeverity'])}. Tasks/cache:{json.dumps(r['tasks'])}. Gate attachment is not releaseapproval. Exact rawcommands/activation/inithashes/source/trees/XML/events/logs/GC/lint/exits/native monitoring and separate discovery/runtimeZIPs retain memberSHA/CRC. Productionrepositorydigest487d9118…eeb9a is DISTINCT from packcontent39c3563b…e65ec;both fullvalues remain in PREPARATION.json. NoAPKidentity/signature;noAPKassembly/install/newsigning.

{constraints}
'''
        nexttext=('Next large task:fresh broad selected-host discovery/runtime from these verified inputs,including current14/source/dowstream controls. Preserve every result;do not infer full app/curriculum/device or independent approval.' if good else 'Next large task:inspect every actual affected failure/unexpectedskip/native/task issue at the pinned source. Archive original comparison files and retain every assertion/hash/source pin/currentcontrol. Reconcile only proven historical scopes or actual defects,then full affected-class followups before any broad run. Do not advance Unit15 curriculum work while affected failures remain unresolved.')
        publish(dict(complete=True,good=good,counts=counts,classes=96,exitCode=r['exitCode'],all22Unit14ControlsPassed=controls['allPassed'],summary=summary),report,nexttext)
    else:
        while active_jobs():time.sleep(20)
        target='completionExists' if end else 'runtimeInactiveWithoutExit'
        while not read(W/'DURABLE_MONITOR_STATUS.json').get(target):time.sleep(2)
        archive_mode('REGRESSION','RAW_INCOMPLETE',monitor=True)
        events=[];p=W/'REGRESSION/EVENTS.jsonl'
        if p.exists():
            for line in p.read_text().splitlines():
                try:events.append(json.loads(line))
                except ValueError:pass
        events=[x for x in events if x.get('kind')=='RESULT'];counts=dict(Counter(x['resultType'] for x in events))
        save(D/'PARTIAL_RESULTS.json',events);save(D/'FINAL_CONSERVATION.json',dict(sourceCommit=S,**conservation()))
        save(D/'INCOMPLETE_RESULT.json',dict(sourceCommit=S,expectedMethods=n,partialEvents=len(events),partialOutcomeCounts=counts,underlyingExit=end['gradleExit'] if end else None,finalRuntimeResult='UNAVAILABLE',cause='UNCONFIRMED_UNLESS_RAW_EXIT_NATIVE_EVIDENCE_ESTABLISHES_IT'))
        summary=f'INCOMPLETE:{len(events)}partial events/{n}expected;{json.dumps(counts)};no verified final runtime result'
        publish(dict(complete=False,good=False,partialEvents=len(events),partialOutcomeCounts=counts,underlyingExit=end['gradleExit'] if end else None,summary=summary),f'# Unit14 affected runtime INCOMPLETE\n\nSource`{S}`. **{summary}**. Original completion:{json.dumps(end)}. StopcauseUNCONFIRMED unless raw evidence establishes it. Raw bytes/events/logs/exits/monitor/GC and all source conservation preserved. No final pass/lint/gate claim without actual records.\n\n'+constraints,'Next task:inspect original partial/native/exit/process evidence;verify no owned workers survive,then plan explicitly separate continuation or retry. Never invent final counts,sum scopes,waive failures or advance curriculum work.')
except BaseException:
    save(W/'AUTO_ERROR.json',dict(utc=now(),error=traceback.format_exc()));raise
