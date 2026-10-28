"""Exclusive completion owner. Preserve complete/incomplete results; publish audit/docs only."""
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
    owned=[];p=W/'REGRESSION_RUNNER_PROCESS.json'
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
    (D/'NEXT_TASK.md').write_text('# Next task\n\n'+nexttext+'\n\nRead REPORT.md and original failure evidence. One agent, existing checkout, Draft PR64. All 48 findings OPEN; audit INCOMPLETE; physical acceptance LAST. No APK/signing/merge/freeze/release approval.\n')
    for n in allowed:
        p=R/n;link=rel+'/REPORT.md' if n=='AGENTS.md' else '../'+rel.removeprefix('audit/')+'/REPORT.md'
        p.write_text(f'Current checkpoint: [Unit14 historical comparison follow-up outcome]({link}). Actual {S}: {result["summary"]}. Seven files/all757 original assertion calls and pins retained;274other tests/current controls/155bindings/77archives conserved. Read NEXT_TASK.md. Original541run526pass14fail1skip remains immutable and separate/unsummed. All48OPEN/auditINCOMPLETE/four exporters16methods excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
    manifest()
    if result['complete']:(W/'FINAL_VERIFICATION.json').write_text(invoke(['python3','-B',str(W/'verify_results.py')]))
    else:verify_archive('RAW_INCOMPLETE');conservation()
    raw=subprocess.check_output(['git','status','--porcelain=v1','-z','--untracked-files=all'],cwd=R)
    for row in raw.split(b'\0'):
        if not row:continue
        assert row[2:3]==b' ' and not any(k in row[:2] for k in [b'R',b'C']),row
        n=row[3:].decode();assert n in allowed or n.startswith(rel+'/'),n
    for p in D.rglob('*'):
        if p.is_file():assert p.stat().st_size<100*1024*1024,(p,p.stat().st_size)
    invoke(['git','add',rel,*sorted(allowed)]);invoke(['git','commit','-m','Preserve Unit14 historical comparison full-class follow-up outcomes'])
    head=git('rev-parse','HEAD');invoke(['git','push','origin',branch]);assert git('ls-remote','origin','refs/heads/'+branch).split()[0]==head
    body=f'''Checkpoint `{head}`: Unit14 historical comparison follow-up. Actual tested source `{S}`. **{result['summary']}**.

Evidence and next instructions: `{rel}/REPORT.md`, `NEXT_TASK.md`. {nexttext}

Seven test files changed narrowly. All 757 original assertion/helper calls retained in order, plus17 guards; no methods added/removed. Three explicit old Unit14 v1 behavioral contracts use the strict whole-pack-proven declaration scope; other Unit14 transactional/current and all Unit15 assertions remain current. Only historical Unit13/teaching/digest equalities are scoped; current source/teaching/mutation validation remains current. The actual Unit15 fixture answers every declared predecessor slot. Genuine matching historical kernel/reference retained without changing old count/source pins. All274other test files,22currentUnit14 controls,production,155bindings,77archives and every byte of the original failure phase conserved.

Fresh discovery79synthetic skips/13fullclasses/zero runtime passes; all14originalfailed IDs,19currentUnit13 controls and22currentUnit14 controls included. Original dd736e1a run541reported/526passed/14failed/1physicalskip/exit1 remains immutable, separate and unsummed, including complete messages/traces in lossless ZIP and original GitHub size rejection. Earlier20core/2binding/1477broad and historical562passes/691inventory also remain separate. No complete-suite or independent acceptance claim.

Exact commands/source/trees/XML/events/counts/exits/lint/task-cache/native/CRC/memberSHA retained. Four exporters16methods excluded:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. APKidentity/signature null; no assembly/install/new signing. Gate attachment is not releaseapproval. Repository digest487d9118d7154dd59162dbb7024f1349bc53d5b7bf89e9d79022d97eaa1eeb9a differs from pack content-review digest39c3563ba1b149b36977ae0fc2c0f920e282101e9e7870c06d141fe1a10e65ec.

All48findingsOPEN/auditINCOMPLETE. PhysicalGalaxyS26Ultra airplane-mode/reboot acceptance outstanding and LAST after final independent comprehensive nonphysical audit. Qualifiedreviews/CC024risk/CC048authority/originalUnit7restorecause unresolved. No merge/freeze/formal advancement/releaseapproval. PrivatePDF unread/unpackaged;environment unchanged,prior publication user-confirmed independently unobserved. JITdirective provisional/unadopted/not a test exclusion/nativecauseUNCONFIRMED. One agent/existing checkout/OPEN DraftPR64.
'''
    (W/'PR64_BODY.md').write_text(body)
    pr=json.loads(invoke(['gh','api','--method','PATCH','repos/justinkulka-lang/russian-companion/pulls/64','-F','body=@'+str(W/'PR64_BODY.md'),'--jq','{number,state,draft,head:.head.sha}']))
    save(W/'PR_UPDATE.json',pr);assert pr['state']=='open' and pr['draft'] and pr['head']==head and not git('status','--porcelain')
    save(W/'AUTO_RESULT.json',dict(sourceCommit=S,checkpointCommit=head,**result,verified=True,pushed=True,draftPR64Updated=True,clean=True,finishedUtc=now(),all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',completeSuite=False))
try:
    with (W/'AUTO_COMPLETION.lock').open('x') as f:f.write(str(os.getpid())+'\n')
    save(W/'AUTO_PROCESS.json',dict(pid=os.getpid(),startedUtc=now(),authority='Preserve actual complete/incomplete evidence;audit-docs-only commit;normal push;DraftPR64 update;no test/app/binding edits'))
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
    constraints='All48findingsOPEN/auditINCOMPLETE/physicalGalaxyS26Ultra acceptanceLAST after final independent comprehensive nonphysical audit. Fourexporters16methods excluded. NoAPKassembly/install/newsigning/merge/freeze/formal advancement/releaseapproval;APKidentity/signature null. Qualifiedreviews/CC024risk/CC048authority/originalUnit7restorecause unresolved. PrivatePDF unread/unpackaged/environment unchanged/publication user-confirmed independently unobserved/JITdirective provisional/unadopted/not test exclusion/nativecauseUNCONFIRMED. Original541run and earlier scopes separate/unsummed,not a complete-suite result.'
    if end and end['resultExists'] and end['manifestExists']:
        while not read(W/'DURABLE_MONITOR_STATUS.json')['completionExists']:time.sleep(2)
        (W/'COLLECTION.log').write_text(invoke(['python3','-B',str(W/'collect.py')]))
        r=read(W/'REGRESSION/RESULT.json');counts=r['testCounts'];monitor=read(W/'DURABLE_MONITOR_STATUS.json')
        controls14=read(D/'UNIT14_CONTROLS_RECHECKED.json');controls13=read(D/'UNIT13_CONTROLS_RECHECKED.json');prior=read(D/'PRIOR_FAILURES_RECHECKED.json')
        good=r['exitCode']==0 and counts['failed']==counts['errors']==counts['skipped']==0 and controls14['allPassed'] and controls13['allPassed'] and prior['allPassed'] and not monitor['newNativeReports']
        summary=f"{counts['reported']} reported / {counts['passed']} passed / {counts['failed']} failed / {counts['errors']} errors / {counts['skipped']} skipped;13 full classes;exit{r['exitCode']}"
        local=lambda s:datetime.fromisoformat(s).astimezone(ZoneInfo('America/Chicago')).isoformat()
        report=f'''# Unit14 historical comparison full-class follow-up

Actual tested source `{S}`: **{summary}**. Runtime {local(c['startedUtc'])} → {local(r['finishedUtc'])} America/Chicago; exact UTC retained. Fresh discovery79synthetic skips/13fullclasses/zero runtime passes; actual XML/events/method IDs/source trees agree.

All14original failed identities rechecked/pass:{prior['allPassed']}; all19currentUnit13 controls pass:{controls13['allPassed']};all22unchanged currentUnit14 controls pass:{controls14['allPassed']}. See individual records. Seven comparison/fixture files retain every757original assertion invocation in order plus17guards, all original test methods/literal pins. Scope/justification/originals are in STATUS.md, COMPARISON_REVIEW.json and ORIGINAL_COMPARISONS.zip. Production/274other test files/155bindings/77archives and all prior failure evidence exact. No test waiver, old-hash replacement or historical class-wide substitution.

Lint:{json.dumps(r['lintIssuesBySeverity'])}. Tasks/cache:{json.dumps(r['tasks'])}. Native reports:{json.dumps(monitor['newNativeReports'])}. Full original failure messages/traces retained losslessly in ACTUAL_FAILURES_FULL.zip and RAW_AFFECTED.zip; ACTUAL_FAILURES.json is a verified identity/hash index. Raw commands/inits/tooling/XML/events/logs/GC/lint/exits and memberSHA/CRC retained. Four exporters16methods excluded. No APK identity/signature or build/sign/install.

Original dd736e1a541reported/526passed/14failed/1physicalskip/exit1 and its original147,185,270byte failure JSON remain unchanged in the prior phase's lossless archive. Follow-up outcomes are separate and unsummed. No finding is closed by engineering host checks.

{constraints}
'''
        nexttext=('Next large task: fresh broad selected-host discovery/runtime from these conserved corrected inputs. Include all current Unit14 controls and downstream classes; retain every outcome. No Unit15 curriculum work until broad outcomes are reviewed.' if good else 'Next task: inspect every actual remaining failure/skip/native/task issue from this scope, preserve all original outcomes, and correct only proven causes before another separately recorded full-class follow-up. Do not start broad or Unit15 curriculum work.')
        publish(dict(complete=True,good=good,counts=counts,classes=13,exitCode=r['exitCode'],all14OriginalFailedMethodsPassed=prior['allPassed'],all19Unit13ControlsPassed=controls13['allPassed'],all22Unit14ControlsPassed=controls14['allPassed'],summary=summary),report,nexttext)
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
        counts=dict(Counter(e['resultType'] for e in events));summary=f'INCOMPLETE: {len(events)} partial events /79 expected;{json.dumps(counts)};no verified final result'
        save(D/'PARTIAL_RESULTS.json',events);save(D/'FINAL_CONSERVATION.json',dict(sourceCommit=S,**conservation()))
        save(D/'INCOMPLETE_RESULT.json',dict(sourceCommit=S,expectedMethods=79,partialEvents=len(events),counts=counts,underlyingExit=end['gradleExit'] if end else None,stopCause='UNCONFIRMED unless raw evidence establishes it'))
        publish(dict(complete=False,good=False,partialEvents=len(events),partialCounts=counts,summary=summary),'# Follow-up INCOMPLETE\n\n'+summary+'\n\n'+constraints,'Inspect original partial/exit/process/native evidence; verify no jobs survive before separately preparing continuation. Do not invent final counts, sum scopes or waive failures.')
except BaseException:
    save(W/'AUTO_ERROR.json',dict(utc=now(),error=traceback.format_exc()));raise
