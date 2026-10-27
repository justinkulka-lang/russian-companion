"""Exclusive actual outcome collector and audit-only publisher, never edits app/test inputs."""
from pathlib import Path
from datetime import datetime,timezone
from zoneinfo import ZoneInfo
import json,hashlib,subprocess,time,os,traceback
R=Path('/workspace/russian-companion');W=Path(__file__).parent
D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit13-historical-comparison-followup-2026-10-27'
branch='cursor/content-audit-corrections-964f';rel=D.relative_to(R).as_posix()
git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
save=lambda p,v:p.write_text(json.dumps(v,indent=2)+'\n')
sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
def invoke(args):
    p=subprocess.run(args,cwd=R,text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
    if p.returncode:raise RuntimeError(json.dumps(dict(command=args,exit=p.returncode,output=p.stdout[-6000:])))
    return p.stdout
try:
    with (W/'AUTO_COMPLETION.lock').open('x') as f:f.write(str(os.getpid())+'\n')
    save(W/'AUTO_PROCESS.json',dict(pid=os.getpid(),startedUtc=datetime.now(timezone.utc).isoformat(),authority='Actual evidence verification,audit-docs-only commit,normalpush,DraftPR64 update;no app/test changes'))
    while not (W/'REGRESSION_COMPLETION.json').exists():time.sleep(20)
    end=json.loads((W/'REGRESSION_COMPLETION.json').read_text());assert end['resultExists'] and end['manifestExists'],end
    while not (W/'DURABLE_MONITOR_STATUS.json').exists() or not json.loads((W/'DURABLE_MONITOR_STATUS.json').read_text())['completionExists']:time.sleep(2)
    r=json.loads((W/'REGRESSION/RESULT.json').read_text());c=json.loads((W/'REGRESSION/CONTEXT.json').read_text());S=r['sourceCommit']
    assert git('rev-parse','HEAD')==S==c['sourceCommit'] and git('branch','--show-current')==branch and not git('status','--porcelain')
    assert end['runnerExit']==r['exitCode']==int(end['gradleExit']) and r['testCounts']['reported']==63 and r['classes']==9
    (W/'COLLECTION.log').write_text(invoke(['python3',str(W/'collect.py')]))
    v=json.loads(invoke(['python3',str(W/'verify_results.py')]));save(D/'RESULT_VERIFICATION.json',v)
    n=r['testCounts'];counts=f"{n['reported']} reported / {n['passed']} passed / {n['failed']} failed / {n['errors']} errors / {n['skipped']} skipped"
    recheck=json.loads((D/'ORIGINAL_FAILURE_RECHECK.json').read_text());fail=[dict(testClass=t['testClass'],method=t['method']) for t in r['tests'] if t['outcome'] in ['FAILED','ERROR']]
    monitor=json.loads((W/'DURABLE_MONITOR_STATUS.json').read_text());good=r['exitCode']==0 and not fail and recheck['allSixPassed']
    local=lambda s:datetime.fromisoformat(s).astimezone(ZoneInfo('America/Chicago')).isoformat()
    report=f'''# Unit13 historical comparison full-class follow-up completed

Actual tested source `{S}`. **{counts}**,9FULLclasses,Gradle/runner exit{r['exitCode']}. Started{local(c['startedUtc'])},finished{local(r['finishedUtc'])} (America/Chicago;raw UTC retained). Fresh discovery63synthetic skips/zero runtime passes;exact fresh XML/events/method IDs/six input trees and exits agree. Selected full-class scope only,not complete app/curriculum/device or independent acceptance. No tests added/removed. All19existing current Unit13 baseline/compatibility/UI/binding controls included.

All six originally failed identities rechecked:{json.dumps(recheck,indent=2)}. Actual follow-up failures:{json.dumps(fail)};full messages/traces in ACTUAL_FAILURES.json/RAW_FOLLOWUP.zip. Original75e2d31c interrupted316partial events/309SUCCESS/6FAILURE/1physicalSKIP,with no finalruntime exit/XML/result,remain immutable. Separate3248ab59 continuation165/165/exit0 remains a distinct scope. Do not sum pass counts or claim one completed481run. All old failures,627original assertion/helper invocations and literal digest pins retained,none waived. Assertion invocation counts are NOT test-method counts.

Five old comparison files archived byte-exact at parent45b9f67c. Only two original v2 behavior methods use the unchanged full-pack-proven before13 scope;all other behavior methods/shared pack remain current. Only the old whole-unit equality in each of two teaching methods is scoped;all subsequent original teaching/items/examples/provenance/capabilities/validator assertions remain actual current,with added exact current Unit13 source guards. Two old Unit13 digest pins stay on their genuine whole-pack-proven source,with independent exact current-v3 identity/source and distinct digest assertions. POLICY_ONLY_ASSET_PROOF.json proves raw current asset differs from actual prior producer only in the one Unit13 policy ID. All627original assertions retained in order;635current assertion/helper invocations. Production/271other testfiles/whole original baseline/currentcore controls/originalUnit12comparisoncorrections/155bindings/74protectedarchives conserved,SHA+CRC verified.

Lint:{json.dumps(r['lintIssuesBySeverity'])}. Actual tasks/cache states:{json.dumps(r['tasks'])}. Gate attachment is not release approval. No APKassembly/install/signing;APKidentity/signature null. Native reports:{json.dumps(monitor['newNativeReports'])}. Four screenshot exporters16methods excluded explicitly:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. Unchanged provisionalSQLiteJITcompilationdirective is unadopted/nativecauseUNCONFIRMED;it excludes no test methods.

Repositorysource digest3c0acbb95297cf10f08da2bd8391463067eece22fec785158b5d2680d37dc2ba differs from packcontent15c6ae393767cb7ffc190882867bfad923c9e81b07d0f4285cf0a678f2489280. Earlier17core/2binding/165continuation/prior1458broad scopes remain separate/unsummed. Historical420f41ff691sourceinventory/unverified execution and oldmain562passes are not current complete-suite evidence. Broad post-Unit13 result remains pending.

All48independent findingsOPEN/auditINCOMPLETE. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance LAST after final independent comprehensive nonphysical audit. Qualified linguistic/source/rights/mapping/owner reviews,CC024semanticrisk,19print-singletonpolicies,CC048authority conflict and originalUnit7restorecause outstanding. No merge/freeze/formal advancement/releaseapproval. PrivatePDF unread/unpackaged/environmentunchanged/publication user-confirmed independently unobserved. One agent/currentcheckout/DraftPR64.
'''
    (D/'REPORT.md').write_text(report)
    nexttext=('Next large task: fresh broad selected-host discovery/runtime at the completed current source. Verify the actual method/class set against prior broad1458IDs plus19Unit13methods,with no removals;do not claim the predicted1477count until discovery. Include all five comparison corrections,current controls and exact current binding records. Preserve every prior scope/failure/interruption and four exporter exclusions. No fresh broad runtime outcome exists yet.' if good else 'Next task: inspect full actual follow-up failures or task errors at this exact source;preserve outcomes and every original assertion/current guard. Do not proceed to broad while unresolved affected failures remain.')
    (D/'NEXT_TASK.md').write_text('# Next task after actual full-class follow-up\n\n'+nexttext+'\n\nRead REPORT.md,ORIGINAL_FAILURE_RECHECK.json,RESULT_VERIFICATION.json,FINAL_CONSERVATION.json and raw evidence. All48findingsOPEN/auditINCOMPLETE/physicalLAST. One agent/currentcheckout/DraftPR64;no new confirmation required. No APK/signing/merge/freeze/releaseapproval.\n')
    allowed={'AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md'}
    for name in allowed:
        p=R/name;link=rel+'/REPORT.md' if name=='AGENTS.md' else '../'+rel.removeprefix('audit/')+'/REPORT.md'
        p.write_text(f'Current checkpoint: [Unit13 historical comparison full-class follow-up completed]({link}). Actual{S[:8]}:{counts},9FULLclasses,exit{r["exitCode"]};XML/events/discovery/sixinputs verified/all6originalfailedIDs rechecked/pass={recheck["allSixPassed"]}. Fivefiles/all627originalassertions/pins/currentcontrols/271other tests/155bindings/74archives conserved. Original316interrupted/165completed/63followup scopes separate/unsummed;no completed481run. Read NEXT_TASK.md. All48OPEN/auditINCOMPLETE/four exporters excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
    save(D/'SHA256.json',{p.relative_to(D).as_posix():sha(p) for p in sorted(D.rglob('*')) if p.is_file() and p.name!='SHA256.json'})
    (W/'FINAL_VERIFICATION.json').write_text(invoke(['python3',str(W/'verify_results.py')]))
    raw=subprocess.check_output(['git','status','--porcelain=v1','-z','--untracked-files=all'],cwd=R);changes=[x for x in raw.split(b'\0') if x];assert changes
    for row in changes:
        assert row[2:3]==b' ' and not any(k in row[:2] for k in [b'R',b'C']),row
        path=row[3:].decode();assert path in allowed or path.startswith(rel+'/'),path
    invoke(['git','add',*sorted(allowed),rel]);invoke(['git','diff','--cached','--check']);invoke(['git','commit','-m','Preserve verified Unit13 historical comparison full-class follow-up'])
    head=git('rev-parse','HEAD');(W/'PUSH.log').write_text(invoke(['git','push','origin','HEAD:'+branch]))
    body=f'''Unit13 full-class follow-up checkpoint `{head}`;actual tested source `{S}`. **{counts}**,9FULLclasses,exit{r['exitCode']}. Fresh discovery/XML/events/sixinput trees agree;all6originalfailed identities rechecked,allpassed={recheck['allSixPassed']}. Selected affected scope,not complete suite or independent acceptance.

Five historical comparison files retain all627original assertion/helper invocations and literal pins;only proven v2 assumptions scoped to exact whole-pack producer. Current teaching/validator/runtime/mutation/eligibility controls stay current;independent exact v3 source/identity guards added. All19unchanged Unit13baseline/currentcompatibility/UI/binding methods included. Production/271other testfiles/155bindings/74protectedarchives conserved. Original5files archived;assertion preservation reverified from exact Git parent. Packet `{rel}`:REPORT.md,NEXT_TASK.md,ORIGINAL_FAILURE_RECHECK.json,ACTUAL_FAILURES.json,RAW_FOLLOWUP.zip,memberSHA/CRC/conservation. {nexttext}

Original75e2d31c interrupted316partial events/309SUCCESS/6FAILURE/1physicalSKIP with no underlyingexit/finalruntime result,and separate3248ab59continuation165/165remain immutable. Do not sum scopes into one completed481run. Lint:{json.dumps(r['lintIssuesBySeverity'])};tasks:{json.dumps(r['tasks'])}. Gate attachment not release approval. Four exporters16methods explicitly excluded:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. No APKassembly/install/signing/identity/signature.

All48independent findingsOPEN/auditINCOMPLETE. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance LAST after final independent comprehensive nonphysical audit. Qualifiedreviews/CC024risk/19print-singletons/CC048authority/originalUnit7restorecause outstanding. No merge/freeze/formal advancement/releaseapproval. PrivatePDF unread/unpackaged/environmentunchanged/publication user-confirmed independently unobserved/JITdirective unadopted/nativecauseUNCONFIRMED. Repositorysource3c0acbb9…dc2ba distinct from packcontent15c6ae39…89280;prior digest-label correction preserved.
'''
    (W/'PR64_BODY.md').write_text(body);pr=json.loads(invoke(['gh','api','--method','PATCH','repos/justinkulka-lang/russian-companion/pulls/64','-F','body=@'+str(W/'PR64_BODY.md'),'--jq','{number,state,draft,head:.head.sha}']));save(W/'PR_UPDATE.json',pr)
    assert pr['state']=='open' and pr['draft'] and pr['head']==head
    assert not git('status','--porcelain') and git('ls-remote','origin','refs/heads/'+branch).split()[0]==head
    save(W/'AUTO_RESULT.json',dict(sourceCommit=S,checkpointCommit=head,counts=n,fullClasses=9,exitCode=r['exitCode'],allSixOriginalFailedIdsRechecked=True,allSixPassed=recheck['allSixPassed'],verified=True,pushed=True,draftPR64Updated=True,clean=True,good=good,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',completeSuite=False,finishedUtc=datetime.now(timezone.utc).isoformat()))
except BaseException:
    save(W/'AUTO_ERROR.json',dict(error=traceback.format_exc(),utc=datetime.now(timezone.utc).isoformat()));raise
