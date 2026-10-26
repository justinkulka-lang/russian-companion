"""Durable single-agent completion: collect actual integration evidence and publish docs only."""
from pathlib import Path
from datetime import datetime,timezone
import json,subprocess,time,os,hashlib,traceback,zipfile
R=Path('/workspace/russian-companion');W=Path('/workspace/artifacts/unit10-source-reconciliation-2026-10-25');D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit10-source-reconciliation-2026-10-25';branch='cursor/content-audit-corrections-964f';run=lambda a:subprocess.check_output(a,cwd=R,text=True).strip();save=lambda p,v:p.write_text(json.dumps(v,indent=2)+'\n')
try:
 with (W/'AUTO_COMPLETION.lock').open('x') as f:f.write(str(os.getpid())+'\n')
 save(W/'AUTO_COMPLETION_PROCESS.json',dict(pid=os.getpid(),startedUtc=datetime.now(timezone.utc).isoformat(),scope='collect/verify evidence; audit/docs commit,normal push,DraftPR64 body only; no app edits'))
 while not (W/'REGRESSION_COMPLETION.json').exists():time.sleep(45)
 done=json.loads((W/'REGRESSION_COMPLETION.json').read_text());assert done['resultExists'] and done['manifestExists'],'Incomplete runner: retain raw evidence and diagnose manually'
 r=json.loads((W/'REGRESSION/RESULT.json').read_text());c=json.loads((W/'REGRESSION/CONTEXT.json').read_text());source=r['sourceCommit'];assert run(['git','rev-parse','HEAD'])==source==c['sourceCommit'];assert run(['git','branch','--show-current'])==branch and not run(['git','status','--porcelain']);assert r['testCounts']['reported']==320 and r['classes']==59 and done['runnerExit']==r['exitCode']==int(done['gradleExit'])
 for _ in range(10):
  m=json.loads((W/'DURABLE_MONITOR_STATUS.json').read_text())
  if m['completionExists']:break
  time.sleep(5)
 run(['python3',str(W/'collect_attempt.py'),'ATTEMPT_1']);A=D/'ATTEMPT_1';idx={}
 with zipfile.ZipFile(A/'MONITORING.zip','x',zipfile.ZIP_DEFLATED) as z:
  for n in ['durable_monitor.py','DURABLE_MONITOR.log','DURABLE_MONITOR_STATUS.json']:
   p=W/n;z.write(p,n);idx[n]=dict(bytes=p.stat().st_size,sha256=hashlib.sha256(p.read_bytes()).hexdigest())
 save(A/'MONITORING_INDEX.json',idx);n=r['testCounts'];failures=[dict(testClass=x['testClass'],method=x['method']) for x in r['tests'] if x['outcome'] in ['FAILED','ERROR']];skips=[dict(testClass=x['testClass'],method=x['method']) for x in r['tests'] if x['outcome']=='SKIPPED'];counts=f"320reported/{n['passed']}passed/{n['failed']}failed/{n['errors']}errors/{n['skipped']}skipped";good=not failures and r['exitCode']==0
 text=f'''# Unit10 authentic bindings: affected integration completed

Actual tested source `{source}`,started `{c['startedUtc']}`,finished `{r['finishedUtc']}`(UTC). **{counts}**,59fullclasses,actual Gradle/runner exit{r['exitCode']}. Fresh discovery/XML/unique events/outcomes/six input trees agree. This is selected affected integration,not complete-suite/app/curriculum/device acceptance or independent finding closure. Full raw evidence/commands/activation/initializers/source/root/input trees/GC/lint/exits are in ATTEMPT_1/RAW.zip,with every full failure message retained in XML. Recorded skips:{json.dumps(skips)}.

Actual failed/error identities:{json.dumps(failures,indent=2)}

Two authentic153-file renders matched and all30packages validated in each.95of155records changed only five reviewed identity tokens;60worksheets exact;95prior versions/Gitblobs archived. All29other unit source choices,all reviewer/owner/mapping/rights/admission/approval fields and historical citations unchanged. Candidate unfrozen/sourceCommitnull/artifactNOT_RECORDED/publicReleaseNOT_APPROVED. Actual production source85e7eaf2/canonical072ea177f73cf7f8ac2a2eedc4afc61f42c2e55d9aeb2c6b2adaf0184cef21f6; official Kotlin digest rechecked after application. Source tooling uses strict10→9→8→7→6→5genuine whole-pack proofs and a pending reference entry; these do not establish semantic/rights acceptance.

Lint:{json.dumps(r['lintIssuesBySeverity'],sort_keys=True)}. Task/cache statuses:{json.dumps(r['tasks'],sort_keys=True)}. Gate attachment is not release approval.71prior archives/whole baseline/original core assertions conserved. Earlier85e7eaf213/13core andc14379621positive/1intentional contrast failure ande65a281cbroad1409pass/1physicalskip remain immutable,separate and unsummed. Historical main562passing was a different selected source/scope,not complete-suite;420f41ff691was source inventory/unverified handoff execution with67focused methods verified.

Four screenshot exporters/16methods explicitly excluded:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance remains outstanding/LAST after final independent comprehensive nonphysical audit. APK identity/signature null;assembly/install/signingNOT_RUN. All48findingsOPEN/auditINCOMPLETE;22print-singleton policies/qualified reviews/CC048conflict/originalUnit7restorecause remain. No merge/freeze/formal advancement/releaseapproval. Private Penguin untouched;environment settings unchanged/earlier publication user-confirmed and independently unobserved;provisional JIT directive unadopted/native causeUNCONFIRMED. New native report observations:{json.dumps(m['newNativeReports'])}.
'''
 (D/'INTEGRATION_REPORT.md').write_text(text);(D/'STATUS.md').write_text(text)
 next_task='Fresh broad discovery and then uninterrupted selected-host regression are the next large task after these production/source changes. Derive actual scope and compare all earlier1410method identities; added methods are inventory until discovery/runtime. Preserve exact source/input trees and every raw outcome; no further broad pass is claimed here.' if good else 'Review ATTEMPT_1/FAILED_METHODS.json and every full raw XML failure before changing anything. Preserve this failed integration. Separate current behavioral defects from historical comparison assumptions. Archive originals and retain every original assertion/source pin,scope only exact historical comparisons,and retain current-source/current-behavior controls. Verify affected full classes in a separate fresh follow-up;never relabel this failed320scope as all-pass or sum overlaps.'
 (D/'NEXT_TASK.md').write_text('# Next task after actual affected integration\n\n'+next_task+'\n\nRead INTEGRATION_REPORT.md and STATUS.md first. One agent/existing PR64checkout only;no test job remains active at this completed checkpoint. All48OPEN/auditINCOMPLETE;four exporters excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n')
 pointers=[R/'AGENTS.md',R/'audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md',R/'audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']
 for p in pointers:
  link=('audit/' if p.name=='AGENTS.md' else '../')+'gpt61-sol-high-comprehensive-2026-10-19/engineering-unit10-source-reconciliation-2026-10-25/INTEGRATION_REPORT.md';p.write_text(f'Current checkpoint: [Unit10 authentic bindings: affected integration completed]({link}). Tested{source}:{counts},59classes,exit{r["exitCode"]}. All actual failures/raw evidence preserved;two153renders/95identity-only records/60worksheets/95prior versions/71archives verified. Read INTEGRATION_REPORT.md/NEXT_TASK.md. Scope is not complete acceptance;all48OPEN/physicalLAST/four exporters excluded/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
 def manifest():
  save(D/'SHA256.json',{p.relative_to(D).as_posix():hashlib.sha256(p.read_bytes()).hexdigest() for p in sorted(D.rglob('*')) if p.is_file() and p.name!='SHA256.json'});run(['git','add',str(D),*[str(p) for p in pointers]])
 manifest();v=json.loads(run(['python3',str(D/'verify_saved_results.py')]));save(A/'FINAL_VERIFICATION.json',v);manifest();allowed={p.relative_to(R).as_posix() for p in pointers};changed=run(['git','diff','--cached','--name-only']).splitlines();assert changed and all(n in allowed or n.startswith(D.relative_to(R).as_posix()+'/') for n in changed);run(['git','diff','--cached','--check']);run(['git','commit','-m','Preserve Unit9 affected integration evidence and continuation checkpoint']);head=run(['git','rev-parse','HEAD']);run(['git','push','origin','HEAD:'+branch]);previous=run(['gh','pr','view','64','--json','body','--jq','.body']);body=f'''Unit10 authentic bindings applied and affected integration completed;checkpoint `{head}`.

Actual tested `{source}`: **{counts}**,59fullclasses,exit{r['exitCode']}. Fresh XML/events/discovery/six inputs verified;full raw failures retained in ATTEMPT_1. Failed/error identities:{json.dumps(failures)}. Lint:{json.dumps(r['lintIssuesBySeverity'])};gate task:{r['tasks']['verifyReleaseGateAttachment']}. This is affected integration,not complete-suite/independent acceptance. Earlier scopes remain separate/unsummed.

Two independent153-file renders/all30packages validated;95of155identity-only changes/60worksheets exact/prior95versions archived.29other source choices and all approvals/rights/owner/admission fields/historical citations unchanged.71archives/wholebaseline/coreassertions conserved. Source85e7eaf2/canonical072ea177…21f6 rechecked. Candidate unfrozen/unapproved/no artifact. Report:`audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit10-source-reconciliation-2026-10-25/INTEGRATION_REPORT.md`;NEXT_TASK.md/AGENTS/continuation/checkpoint updated.

Four exporters/16methods excluded;physical acceptance outstanding/LAST after final independent comprehensive nonphysical audit. All48OPEN/auditINCOMPLETE/22print-singleton policies and qualified reviews/CC048conflict/originalUnit7restorecause outstanding. No APK assembly/install/signing,merge/freeze/formal advancement/releaseapproval. Private reference/environment settings untouched;JITdirective unadopted/native causeUNCONFIRMED.

---
Historical checkpoint (superseded active-state statements below):

'''+previous
 bodyfile=W/'PR64_INTEGRATION_COMPLETED_BODY.md';bodyfile.write_text(body);response=json.loads(run(['gh','api','--method','PATCH','repos/justinkulka-lang/russian-companion/pulls/64','-F','body=@'+str(bodyfile),'--jq','{number,draft,head:.head.sha}']));assert response['draft'] and response['head']==head;assert not run(['git','status','--porcelain']);save(W/'AUTO_COMPLETION_RESULT.json',dict(completedUtc=datetime.now(timezone.utc).isoformat(),testedSource=source,evidenceCommit=head,counts=n,classes=59,exitCode=r['exitCode'],pushed=True,prUpdated=True,failures=failures,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST'));print((W/'AUTO_COMPLETION_RESULT.json').read_text(),flush=True)
except BaseException:
 save(W/'AUTO_COMPLETION_ERROR.json',dict(atUtc=datetime.now(timezone.utc).isoformat(),traceback=traceback.format_exc(),instruction='Preserve raw/staged evidence;diagnose actual error;do not duplicate collection or force-push'));raise
