"""Exclusive single-agent evidence completion; never changes app/test/source inputs."""
from pathlib import Path
from datetime import datetime,timezone
from zoneinfo import ZoneInfo
import json,subprocess,time,os,hashlib,zipfile,traceback
R=Path('/workspace/russian-companion');W=Path(__file__).parent;D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit12-source-reconciliation-2026-10-27';branch='cursor/content-audit-corrections-964f'
run=lambda a:subprocess.check_output(a,cwd=R,text=True).strip();sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest();save=lambda p,x:p.write_text(json.dumps(x,indent=2)+'\n');local=lambda s:datetime.fromisoformat(s).astimezone(ZoneInfo('America/Chicago')).isoformat()
try:
 with (W/'AUTO_COMPLETION.lock').open('x') as f:f.write(str(os.getpid())+'\n')
 save(W/'AUTO_COMPLETION_PROCESS.json',dict(pid=os.getpid(),startedUtc=datetime.now(timezone.utc).isoformat(),scope='Exclusive evidence collection/verification/docs-only commit/normal push/DraftPR64 update;no app/test/source changes'))
 while not (W/'REGRESSION_COMPLETION.json').exists():time.sleep(45)
 end=json.loads((W/'REGRESSION_COMPLETION.json').read_text());assert end['resultExists'] and end['manifestExists'],'Incomplete actual runner;preserve raw evidence/diagnose manually'
 r=json.loads((W/'REGRESSION/RESULT.json').read_text());c=json.loads((W/'REGRESSION/CONTEXT.json').read_text());source=r['sourceCommit'];assert source==c['sourceCommit']==run(['git','rev-parse','HEAD']);assert not run(['git','status','--porcelain']) and run(['git','branch','--show-current'])==branch
 assert r['testCounts']['reported']==461 and r['classes']==79 and end['runnerExit']==r['exitCode']==int(end['gradleExit'])
 while not json.loads((W/'DURABLE_MONITOR_STATUS.json').read_text())['completionExists']:time.sleep(5)
 m=json.loads((W/'DURABLE_MONITOR_STATUS.json').read_text());run(['python3',str(W/'collect_attempt.py'),'ATTEMPT_1']);A=D/'ATTEMPT_1';idx={}
 with zipfile.ZipFile(A/'MONITORING.zip','x',zipfile.ZIP_DEFLATED) as z:
  for n in ['durable_monitor.py','DURABLE_MONITOR.log','DURABLE_MONITOR_STATUS.json']:
   p=W/n;z.write(p,n);idx[n]=dict(bytes=p.stat().st_size,sha256=sha(p))
 save(A/'MONITORING_INDEX.json',idx);n=r['testCounts'];counts=f"{n['reported']} reported / {n['passed']} passed / {n['failed']} failed / {n['errors']} errors / {n['skipped']} skipped";failures=[dict(testClass=x['testClass'],method=x['method']) for x in r['tests'] if x['outcome'] in ['FAILED','ERROR']];skips=[dict(testClass=x['testClass'],method=x['method']) for x in r['tests'] if x['outcome']=='SKIPPED'];good=not failures and r['exitCode']==0
 text=f'''# Unit12 authentic bindings: affected integration completed

Actual tested source `{source}`. Started {local(c['startedUtc'])},finished {local(r['finishedUtc'])} (America/Chicago; raw UTC retained). **{counts}**,79fullclasses,actual Gradle/runner exit{r['exitCode']}. Fresh discovery/XML/unique event identities/outcomes/source root/six input trees agree. This is selected affected integration,not complete app/curriculum/device acceptance or independent finding closure. Raw exact commands/activation/initializers/exits/GC/lint/XML/events/full failures are retained in ATTEMPT_1/RAW.zip with indexed hashes. Recorded skip identities:{json.dumps(skips)}.

Actual failed/error identities:
{json.dumps(failures,indent=2)}

Two independent153file renders matched;all30packages validated in each.95of155records changed only five identity tokens;60worksheets exact;95prior versions/Gitblobs archived as historical. Only Unit12's effective generator source changed to verified ca0fdaeca8de81ab6a5d4935fbcdcb068c4af913 (first production changeca0fdaec);canonical compiled digest84b6fda72017ec2dfc2b4add6c0e82b1a1e9f40769773f18d50caee4cf7a2355 rechecked after application. All29other source choices and all reviewer/owner/mapping/rights/admission/approval fields unchanged;historical citations unchanged. Candidate unfrozen/sourceCommitnull/artifactNOT_RECORDED/publicReleaseNOT_APPROVED. Strict12→11→10→9→8→7→6→5genuine pack conservation;kernel reference appends pending engineering identity only,not semantic/rights acceptance.73archives SHA/CRC and all other preexisting test bytes conserved;three explicit tooling/reference files archived and reviewed.

Lint:{json.dumps(r['lintIssuesBySeverity'],sort_keys=True)}. Actual task/cache statuses:{json.dumps(r['tasks'],sort_keys=True)}. Gate attachment is not release approval. APK identity/signature null;assembly/install/signingNOT_RUN. Native reports observed:{json.dumps(m['newNativeReports'])}.

Earlier core ca0fdaec15/15 and original23b9179eUnit12baseline2reported/1positivepass/1intentionalfailure/exit1 remain immutable,separate and unsummed;the whole baseline is unchanged. Prior Unit11selected67b915f169/69 and3434fb83broad1441reported/1438pass/2fail/1physicalskip/exit1 are separate. Mainff539220563reported562pass1skip andPR64420f41ff691sourceinventory/unverified handoff execution/67focused methods are distinct sources/scopes,not complete-suite claims.

Four screenshot exporters16methods explicitly excluded:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance remains outstanding and LAST,after final independent comprehensive nonphysical audit. All48findingsOPEN/auditINCOMPLETE;20print-singleton policies/qualified linguistic-source-rights-mapping-owner reviews/CC048authority conflict/originalUnit7restorecause remain. No merge/freeze/formal advancement/release approval. Private Penguin unread/unpackaged;environment settings unchanged/earlier publication user-confirmed independently unobserved. Provisional JITdirective excluded wrapper compilation,not tests;unadopted/native causeUNCONFIRMED.
'''
 (D/'INTEGRATION_REPORT.md').write_text(text);(D/'STATUS.md').write_text(text)
 next_task=('Next large task: fresh broad discovery then selected-host regression after the Unit12 production/source changes. Compare all1441prior selected method IDs,derive actual additions/removals and preserve exact inputs/raw outcomes. No broad result at this Unit12 source is claimed.' if good else 'Next large task: diagnose every retained actual integration failure from ATTEMPT_1 full XML before changing anything. Separate current behavior defects from historical comparison assumptions. Archive originals/retain all original assertions and source pins;scope only exact proven historical comparisons while retaining actual current controls. Run a separate fresh affected full-class follow-up;never relabel this failed461scope as all-pass or sum scopes.')
 (D/'NEXT_TASK.md').write_text('# Next task after actual Unit12 integration\n\n'+next_task+'\n\nRead INTEGRATION_REPORT.md/STATUS.md. One agent/existing PR64checkout only. No test job remains active at this completed checkpoint. All48OPEN/auditINCOMPLETE/four exporters excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n')
 pointers=[R/'AGENTS.md',R/'audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md',R/'audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']
 for p in pointers:
  link=('audit/' if p.name=='AGENTS.md' else '../')+'gpt61-sol-high-comprehensive-2026-10-19/engineering-unit12-source-reconciliation-2026-10-27/INTEGRATION_REPORT.md';p.write_text(f'Current checkpoint: [Unit12 authentic bindings:affected integration completed]({link}). Actual{source}:{counts},79classes,exit{r["exitCode"]}. All raw outcomes/failures preserved;two153renders/95identity-only records/60worksheets/95historical versions/73archives verified. Read NEXT_TASK.md. All48OPEN/auditINCOMPLETE/physicalLAST/four exporters excluded/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
 def manifest():
  save(D/'SHA256.json',{p.relative_to(D).as_posix():sha(p) for p in sorted(D.rglob('*')) if p.is_file() and p.name!='SHA256.json'});run(['git','add',str(D),*[str(p) for p in pointers]])
 manifest();v=json.loads(run(['python3',str(D/'verify_saved_results.py')]));save(A/'FINAL_VERIFICATION.json',v);manifest();allowed={p.relative_to(R).as_posix() for p in pointers};changed=run(['git','diff','--cached','--name-only']).splitlines();assert changed and all(p in allowed or p.startswith(D.relative_to(R).as_posix()+'/') for p in changed);run(['git','diff','--cached','--check']);run(['git','commit','-m','Preserve actual Unit12 source-binding integration evidence and checkpoint']);head=run(['git','rev-parse','HEAD']);run(['git','push','origin','HEAD:'+branch])
 previous=run(['gh','pr','view','64','--json','body','--jq','.body']);body=f'''Unit12 source reconciliation and affected integration completed;checkpoint `{head}`.

Actual source `{source}`: **{counts}**,79fullclasses,exit{r['exitCode']}. Started {local(c['startedUtc'])},finished {local(r['finishedUtc'])} America/Chicago;raw UTC preserved. Fresh discovery/XML/events/source root/six input trees verified. Full raw failures retained in ATTEMPT_1. Failed/error identities:{json.dumps(failures)}. Lint:{json.dumps(r['lintIssuesBySeverity'])};gate:{r['tasks']['verifyReleaseGateAttachment']}. Selected integration,not complete-suite/independent acceptance;all earlier scopes separate/unsummed.

Two153file renders/all30packages each matched;95of155identity-only records/60worksheets exact/prior95historical versions archived. Sourceca0fdaec/canonical84b6fda7…a2355;29other unit source choices and approval/reviewer/owner/rights/admission fields/historical citations unchanged.73archives conserved. Report:`{D.relative_to(R)}/INTEGRATION_REPORT.md`;NEXT_TASK.md/AGENTS/continuation/checkpoint updated.

All48OPEN/auditINCOMPLETE;four exporters16methods excluded;physical acceptance outstanding/LAST after final independent comprehensive nonphysical audit.20print-singleton policies/qualified reviews/CC048authority conflict/originalUnit7restorecause outstanding. No APKassembly/install/signing/merge/freeze/formal advancement/releaseapproval. Private PDF/environment settings unchanged;JITdirective unadopted/native causeUNCONFIRMED.

'''
 f=W/'PR64_INTEGRATION_COMPLETED_BODY.md';f.write_text(body);pr=json.loads(run(['gh','api','--method','PATCH','repos/justinkulka-lang/russian-companion/pulls/64','-F','body=@'+str(f),'--jq','{number,draft,state,head:.head.sha}']));assert pr['draft'] and pr['state']=='open' and pr['head']==head;assert not run(['git','status','--porcelain']);save(W/'AUTO_COMPLETION_RESULT.json',dict(completedUtc=datetime.now(timezone.utc).isoformat(),testedSource=source,evidenceCommit=head,counts=n,classes=79,exitCode=r['exitCode'],pushed=True,prUpdated=True,failures=failures,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST'));print((W/'AUTO_COMPLETION_RESULT.json').read_text(),flush=True)
except BaseException:
 save(W/'AUTO_COMPLETION_ERROR.json',dict(atUtc=datetime.now(timezone.utc).isoformat(),traceback=traceback.format_exc(),instruction='Retain actual raw/staged evidence;diagnose before any duplicate launch/collection;no force-push'));raise
