"""Exclusive completion owner; records actual continuation without repairing source or waiving failures."""
from pathlib import Path
from datetime import datetime, timezone
from zoneinfo import ZoneInfo
import json,hashlib,subprocess,time,os,traceback,zipfile
R=Path('/workspace/russian-companion');W=Path(__file__).parent
D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit13-affected-continuation-2026-10-27'
branch='cursor/content-audit-corrections-964f'
git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
save=lambda p,v:p.write_text(json.dumps(v,indent=2)+'\n')
sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
def invoke(args):
    p=subprocess.run(args,cwd=R,text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
    if p.returncode:raise RuntimeError(json.dumps(dict(command=args,exit=p.returncode,output=p.stdout[-6000:])))
    return p.stdout
try:
    with (W/'AUTO_COMPLETION.lock').open('x') as f:f.write(str(os.getpid())+'\n')
    save(W/'AUTO_PROCESS.json',dict(pid=os.getpid(),startedUtc=datetime.now(timezone.utc).isoformat(),authority='Collect/verify actual continuation; audit-docs-only commit, normal push, update Draft PR64. No app/test changes.'))
    while not (W/'REGRESSION_COMPLETION.json').exists():time.sleep(20)
    end=json.loads((W/'REGRESSION_COMPLETION.json').read_text())
    assert end['resultExists'] and end['manifestExists'],end
    while not (W/'DURABLE_MONITOR_STATUS.json').exists() or not json.loads((W/'DURABLE_MONITOR_STATUS.json').read_text())['completionExists']:time.sleep(2)
    r=json.loads((W/'REGRESSION/RESULT.json').read_text());c=json.loads((W/'REGRESSION/CONTEXT.json').read_text());S=r['sourceCommit']
    assert git('rev-parse','HEAD')==S==c['sourceCommit'] and not git('status','--porcelain')
    assert r['testCounts']['reported']==165 and r['classes']==28
    assert end['runnerExit']==r['exitCode']==int(end['gradleExit'])
    (W/'COLLECTION.log').write_text(invoke(['python3',str(W/'collect.py')]))
    v=json.loads(invoke(['python3',str(W/'verify_results.py')]));save(D/'RESULT_VERIFICATION.json',v)
    interrupted=json.loads((D/'PARTIAL_RESULTS.json').read_text())['tests']
    a={(e['className'],e['name']) for e in interrupted};b={(t['testClass'],t['method']) for t in r['tests']}
    old=json.loads((D/'INTERRUPTION.json').read_text());scope=json.loads((D/'SCOPE.json').read_text())
    with zipfile.ZipFile(D/'INTERRUPTED_RAW.zip') as z: original=json.loads(z.read('ORIGINAL_WORKSPACE/DISCOVERY/RESULT.json'))
    expected={(t['testClass'],t['method']) for t in original['tests']}
    assert not a&b and len(a)==316 and len(b)==165 and a|b==expected and len(expected)==481
    assert r['inputTrees']==old['inputTrees']==scope['inputTrees']
    save(D/'METHOD_COVERAGE.json',dict(originalExpected=481,interruptedPartialEvents=316,completedContinuationMethods=165,disjointSets=True,allOriginalIdentitiesAccountedFor=True,inputTreesIdentical=True,singleCompleted481MethodRun=False,completeSuite=False,countsNotSummed=True,rows=[dict(testClass=e['className'],method=e['name'],evidenceKind='INTERRUPTED_PARTIAL_EVENT',sourceCommit=old['runtimeSource'],outcome=e['resultType']) for e in interrupted]+[dict(testClass=t['testClass'],method=t['method'],evidenceKind='COMPLETED_CONTINUATION_XML_AND_EVENT',sourceCommit=S,outcome=t['outcome']) for t in r['tests']]))
    n=r['testCounts'];counts=f"{n['reported']} reported / {n['passed']} passed / {n['failed']} failed / {n['errors']} errors / {n['skipped']} skipped"
    monitor=json.loads((W/'DURABLE_MONITOR_STATUS.json').read_text());fail=[dict(testClass=t['testClass'],method=t['method']) for t in r['tests'] if t['outcome'] in ['FAILED','ERROR']]
    local=lambda s:datetime.fromisoformat(s).astimezone(ZoneInfo('America/Chicago')).isoformat()
    report=f'''# Unit13 exact-method continuation completed

Actual continuation source `{S}`. **{counts}**, selected165methods across28classes, Gradle/runner exit{r['exitCode']}. Started{local(c['startedUtc'])}, finished{local(r['finishedUtc'])} (America/Chicago; raw UTC retained). Fresh discovery165synthetic skips/zero runtime passes. Current XML/events/discovery/exits and six input trees agree. This is an exact-method continuation, not a full-class run or complete suite.

The original runtime at `{old['runtimeSource']}` was INTERRUPTED:316partial completed events,309SUCCESS/6FAILURE/1SKIPPED, no underlying exit or final runtime XML/result. Those original six failures remain OPEN and unchanged. Stop cause UNCONFIRMED; exec-server disconnected and all original processes were absent on reconnection. INTERRUPTED_RAW.zip and index preserve every raw byte, historical worker lock and observed build results. No stale discovery XML is treated as final runtime evidence.

The continuation selected exactly the165unreported identities, including both interrupted methods, and repeated zero completed identities. METHOD_COVERAGE.json proves disjoint sets and accounting of all481original identities at identical six input trees. It does NOT establish one completed481method run. Do not add pass counts or claim a complete suite. Additional continuation failures:{json.dumps(fail)}. Full actual messages/traces retained in ACTUAL_FAILURES.json and RAW_AFFECTED.zip. All original partial failures retained in INTERRUPTED_FAILURES.json. No assertions/pins waived, failures hidden, or production/test edits in recovery. New native reports:{json.dumps(monitor['newNativeReports'])}.

Production, all276current test files (including original baseline/current controls and the earlier two deliberately scoped comparisons), all155bindings and74historical archives conserved. Original107assertions in those two comparisons remain in order. Repositorysource digest3c0acbb95297cf10f08da2bd8391463067eece22fec785158b5d2680d37dc2ba differs from packcontent15c6ae393767cb7ffc190882867bfad923c9e81b07d0f4285cf0a678f2489280. Historical digest-label correction preserved. Lint:{json.dumps(r['lintIssuesBySeverity'])}; actual tasks:{json.dumps(r['tasks'])}. Gate attachment is not release approval. APK identity/signature null; no assembly/install/signing.

Four screenshot exporters16methods excluded explicitly:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. Provisional SQLite JIT compilation directive unchanged/unadopted; no test methods excluded, native cause UNCONFIRMED. Prior17core/2binding and earlier1458broad remain separate/unsummed. Historical420f41ff691sourceinventory/unverified execution and oldmain562passes are not complete-suite evidence here.

All48independent findingsOPEN/auditINCOMPLETE. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance LAST after final independent comprehensive nonphysical audit. Qualified linguistic/source/rights/mapping/owner reviews,CC024semanticrisk,19print-singletonpolicies,CC048authority conflict and originalUnit7restorecause outstanding. No merge/freeze/formal advancement/release approval. PrivatePDF unread/unpackaged;environment unchanged/publication user-confirmed independently unobserved.
'''
    (D/'REPORT.md').write_text(report)
    nexttext='Next large task: review all preserved six original failures and any continuation failures; narrowly reconcile only proven historical assumptions without removing assertions, resetting pins, or weakening current controls. Preserve both runtime scopes and interruption. Execute affected full-class follow-up, then fresh broad discovery/runtime; no complete481run or audit closure is claimed.'
    (D/'NEXT_TASK.md').write_text('# Next task\n\n'+nexttext+'\n\nRead REPORT.md, ACTUAL_FAILURES.json, INTERRUPTED_FAILURES.json, FAILURE_COMPARISONS.json, METHOD_COVERAGE.json and RESULT_VERIFICATION.json. All48findingsOPEN/auditINCOMPLETE/physicalLAST. One agent/currentcheckout/DraftPR64. No new confirmation needed.\n')
    rel=D.relative_to(R).as_posix()
    allowed={'AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md'}
    for name in allowed:
        p=R/name;link=rel+'/REPORT.md' if name=='AGENTS.md' else '../'+rel.removeprefix('audit/')+'/REPORT.md'
        p.write_text(f'Current checkpoint: [Unit13 exact165method continuation completed]({link}). Actual{S[:8]}:{counts},28classes,exit{r["exitCode"]};XML/events/discovery/sixinputs verified. Original75e2d31c interrupted316partial events/309SUCCESS/6FAILURE/1SKIPPED,no exit/finalresult;all six failuresOPEN. Disjoint method accounting covers481identities,NOT one completed481run;scopes unsummed.155bindings/74archives conserved. Read NEXT_TASK.md. All48OPEN/auditINCOMPLETE/four exporters excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
    save(D/'SHA256.json',{p.relative_to(D).as_posix():sha(p) for p in sorted(D.rglob('*')) if p.is_file() and p.name!='SHA256.json'})
    (W/'FINAL_VERIFICATION.json').write_text(invoke(['python3',str(W/'verify_results.py')]))
    changes=git('status','--porcelain').splitlines();assert changes and all(x[3:] in allowed or x[3:].startswith(rel+'/') for x in changes),changes
    invoke(['git','add',*sorted(allowed),rel]);invoke(['git','diff','--cached','--check']);invoke(['git','commit','-m','Preserve completed Unit13 exact continuation and interrupted scope evidence'])
    head=git('rev-parse','HEAD');(W/'PUSH.log').write_text(invoke(['git','push','origin','HEAD:'+branch]))
    body=f'Unit13 continuation checkpoint `{head}`;actual tested source `{S}`. **{counts}**,165exact methods/28classes,exit{r["exitCode"]}. XML/events/discovery/sixinputtrees agree. Original75e2d31c run interrupted:316partial events/309SUCCESS/6FAILURE/1SKIPPED;no underlyingexit/finalruntime result. Original six failures OPEN and full messages retained. Continuation failures:{json.dumps(fail)}. Disjoint identity accounting covers481selected methods across two distinct scopes;NOT one completed481run or complete suite;do not sum pass counts.\n\nDurable packet `{rel}`:REPORT.md,NEXT_TASK.md,INTERRUPTED_RAW.zip,RAW_AFFECTED.zip,full failure returns,memberSHA/CRC,conservation and METHOD_COVERAGE.json. {nexttext}\n\nProduction/testinputs/155bindings/74archives conserved. Lint:{json.dumps(r["lintIssuesBySeverity"])};gate attachment task status:{r["tasks"]["verifyReleaseGateAttachment"]},not release approval. Four screenshot exporters16methods excluded explicitly:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. No APKassembly/install/signing/identity/signature. All48findingsOPEN/auditINCOMPLETE. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance LAST after final independent comprehensive nonphysical audit. No merge/freeze/formal advancement/releaseapproval. Qualifiedreviews/CC024risk/19print-singletons/CC048authority/originalUnit7restorecause outstanding. PrivatePDF unread/unpackaged;environment unchanged/publication user-confirmed independently unobserved/JITdirective unadopted/nativecauseUNCONFIRMED.\n'
    (W/'PR64_BODY.md').write_text(body)
    (W/'PR_UPDATE.json').write_text(invoke(['gh','api','--method','PATCH','repos/justinkulka-lang/russian-companion/pulls/64','-F','body=@'+str(W/'PR64_BODY.md'),'--jq','{number,state,draft,head:.head.sha}']))
    assert not git('status','--porcelain') and git('ls-remote','origin','refs/heads/'+branch).split()[0]==head
    save(W/'AUTO_RESULT.json',dict(sourceCommit=S,checkpointCommit=head,counts=n,classes=28,exitCode=r['exitCode'],verified=True,pushed=True,draftPR64Updated=True,clean=True,originalInterruptedPartialEvents=316,originalFailuresStillOpen=6,singleCompleted481MethodRun=False,completeSuite=False,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',finishedUtc=datetime.now(timezone.utc).isoformat()))
except BaseException:
    save(W/'AUTO_ERROR.json',dict(error=traceback.format_exc(),utc=datetime.now(timezone.utc).isoformat()));raise
