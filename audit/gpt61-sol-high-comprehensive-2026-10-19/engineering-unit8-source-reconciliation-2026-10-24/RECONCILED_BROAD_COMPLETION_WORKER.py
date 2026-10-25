"""One-agent durable completion worker: collect verified host evidence, update docs/PR64 only."""
from pathlib import Path
from datetime import datetime,timezone
import os,json,time,subprocess,hashlib,zipfile,traceback
R=Path('/workspace/russian-companion');W=Path('/workspace/artifacts/unit8-source-reconciliation-2026-10-24');O=W/'RECONCILED_BROAD';D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit8-source-reconciliation-2026-10-24';name='ATTEMPT_5_RECONCILED_BROAD';branch='cursor/content-audit-corrections-964f'
def run(args):return subprocess.check_output(args,cwd=R,text=True).strip()
def save(p,v):p.write_text(json.dumps(v,indent=2)+'\n')
try:
 with (O/'AUTO_COMPLETION.lock').open('x') as f:f.write(str(os.getpid())+'\n')
 save(O/'AUTO_COMPLETION_PROCESS.json',dict(pid=os.getpid(),startedUtc=datetime.now(timezone.utc).isoformat(),scope='evidence/docs/normal branch push/Draft PR64 body only',noAppChanges=True))
 while not (O/'REGRESSION_COMPLETION.json').exists():time.sleep(45)
 done=json.loads((O/'REGRESSION_COMPLETION.json').read_text());assert done['resultExists'] and done['manifestExists'],'Runner incomplete; preserve external raw evidence for manual reconciliation'
 r=json.loads((O/'REGRESSION/RESULT.json').read_text());c=json.loads((O/'REGRESSION/CONTEXT.json').read_text());source=c['sourceCommit'];counts=r['testCounts'];assert r['sourceCommit']==source==run(['git','rev-parse','HEAD']);assert run(['git','branch','--show-current'])==branch and not run(['git','status','--porcelain']);assert counts['reported']==1394 and r['classes']==237 and done['runnerExit']==r['exitCode']==int(done['gradleExit']);assert r['discoveryComparison']['sameInputTrees'] and r['discoveryComparison']['exactSameMethodSet']
 for _ in range(11):
  monitor=json.loads((O/'DURABLE_MONITOR_STATUS.json').read_text())
  if monitor['completionExists']:break
  time.sleep(5)
 run(['python3',str(W/'collect_attempt.py'),name,'RECONCILED_BROAD']);A=D/name;index={}
 with zipfile.ZipFile(A/'MONITORING.zip','x',zipfile.ZIP_DEFLATED) as z:
  for filename in ['durable_monitor.py','DURABLE_MONITOR.log','DURABLE_MONITOR_STATUS.json']:
   p=O/filename;z.write(p,filename);index[filename]=dict(bytes=p.stat().st_size,sha256=hashlib.sha256(p.read_bytes()).hexdigest())
 save(A/'MONITORING_INDEX.json',index)
 with zipfile.ZipFile(A/'MONITORING.zip') as z:
  assert z.testzip() is None
  for n,v in index.items():assert hashlib.sha256(z.read(n)).hexdigest()==v['sha256']
 failed=[dict(testClass=x['testClass'],method=x['method']) for x in r['tests'] if x['outcome'] in ['FAILED','ERROR']];skips=[dict(testClass=x['testClass'],method=x['method']) for x in r['tests'] if x['outcome']=='SKIPPED'];success=not failed and r['exitCode']==0;label='completed successfully' if success else 'completed with retained failures';total=f"1394reported/{counts['passed']}passed/{counts['failed']}failed/{counts['errors']}errors/{counts['skipped']}skipped"
 text=f'''# Reconciled Unit8 broad selected-host regression {label}

Actual tested source `{source}`. Started `{c['startedUtc']}`; finished `{r['finishedUtc']}` (UTC). **{total}**,237classes,actual Gradle/runner exit{r['exitCode']}. This is one selected debug-host regression,not complete app/curriculum/device acceptance or independent audit closure.

Fresh XML,unique result events/outcomes and all1394fresh-discovered method identities match exactly; no missing/extra method. Six input trees match discovery690eb8179f9f01c49a42d61a2fdc21e2fac0e10c. Each phase retains its actual source/root and both six-tree identities are verified; evidence-only commits are not relabelled as test execution. All1394previous broad identities retained unchanged. {name}/RAW.zip and its index preserve full commands,activation,initializers,resources,source/root/six trees,XML/events/logs/GC/lint/exits and any full failures. Monitoring ZIP/index preserves actual observations. Read-only verifiers execute zero tests.

Four screenshot exporters/16methods explicitly excluded:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. No extra exclusions or WRITE_*flags. Actual skipped identities:{json.dumps(skips)}. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance remains outstanding and LAST after final independent comprehensive nonphysical audit. Host results include historical/synthetic fixture scenarios and do not establish every shipped flow or semantic claim.

OracleJDK21.0.12.1+1-LTS-4,Gradle9.6,offline,API33Robolectric,two6GiBforks/forkEvery4/maxworkers2. Provisional SQLite-wrapper JIT compile directive remains EXPERIMENTAL_NOT_ADOPTED and excludes no tests; native root causeUNCONFIRMED. New native report observations:{json.dumps(monitor['newNativeReports'])}. Environment settings unchanged; earlier publication remains user-confirmed and independently unobserved.

Lint severities:{json.dumps(r['lintIssuesBySeverity'],sort_keys=True)}. Exact task/cache statuses:{json.dumps(r['tasks'],sort_keys=True)}. Gate attachment is not release approval. APK identity/signature null;assembly/install/signingNOT_RUN.

Production remains370d8716; earlier official compiled canonical digest e1f57fec1eeeacde3162069434116852aaa0278f2e501db859db50cc43c21670 remains applicable to unchanged included inputs.155current records/60worksheets/95prior versions/69historical archives and original assertions/pins conserved. Historical source/candidate semantic/native/source/rights/mapping/owner approvals are unchanged. Private Penguin untouched/outside app/assets/audit packets.

Separate immutable history: f6eb548d broad1394reported/1391passed/2failed/1physicalskip,exit1; ce066d6f focused23/23passed,4classes,exit0; earlier8a5f6442 integration192reported/185passed/6failed/1physicalskip and81f95b8d focused50/50passed remain preserved. These overlap and are not summed or retroactively relabelled. Earlierdcc93148 broad1380reported/1379passed/1physicalskip predates Unit8 production. Mainff539220 historical563reported/562passed/1skip was not complete-suite; PR64baseline420f41ff691was a different source inventory/unverified handoff execution with67focused methods verified. Different source and scope explain the counts.

All48independent findingsOPEN/auditINCOMPLETE;24print-singleton policies and qualified linguistic/source/rights/mapping/owner review,CC048authority conflict and originalUnit7restorecause remain outstanding. No merge/freeze/formal advancement/release approval.

Actual failed/error identities:{json.dumps(failed,indent=2)}
'''
 (D/'RECONCILED_BROAD_REPORT.md').write_text(text);(D/'STATUS.md').write_text(text);(D/'RECONCILED_BROAD_STATUS.md').write_text(text)
 next_task='Reconcile this fresh host baseline against the independent work order and remaining substantive curriculum/assessment corrections.24print-singleton policies and qualified reviews remain outstanding. Choose/document a bounded next correction/research task; do not repeat broad execution without a justified change or concern.' if success else 'Review every actual failed identity and full raw XML in ATTEMPT_5_RECONCILED_BROAD before changing code. Preserve this failed broad. Distinguish current behavior from historical fixture assumptions; archive originals and preserve assertions/source pins for any scoped reconciliation,then verify affected full classes and current controls in separate fresh runs.'
 (D/'NEXT_TASK.md').write_text('# Next task after completed reconciled broad\n\n'+next_task+'\n\nRead RECONCILED_BROAD_REPORT.md first. Existing one-agent/PR64 checkout only. All48OPEN/auditINCOMPLETE/four screenshot exporters excluded. Final independent comprehensive nonphysical audit FIRST; actual Galaxy S26 Ultra airplane-mode/reboot acceptance LAST/outstanding. No APK assembly/install/signing,merge/freeze/formal advancement/release approval. No test run remains active at this completed checkpoint.\n')
 pointers=[R/'AGENTS.md',R/'audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md',R/'audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']
 for p in pointers:
  link=('audit/' if p.name=='AGENTS.md' else '../')+'gpt61-sol-high-comprehensive-2026-10-19/engineering-unit8-source-reconciliation-2026-10-24/RECONCILED_BROAD_REPORT.md';p.write_text(f'Current checkpoint: [Reconciled Unit8 broad selected-host regression {label}]({link}). Tested{source}:{total},237classes,exit{r["exitCode"]}. Fresh XML/events/discovery/six trees and raw hashes verified. Prior failures/assertions/history preserved; no overlapping counts summed. Read RECONCILED_BROAD_REPORT.md/NEXT_TASK.md. All48OPEN/auditINCOMPLETE/physicalLAST/four exporters excluded/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
 def manifest():
  save(D/'SHA256.json',{p.relative_to(D).as_posix():hashlib.sha256(p.read_bytes()).hexdigest() for p in sorted(D.rglob('*')) if p.is_file() and p.name!='SHA256.json'});run(['git','add',str(D),*[str(p) for p in pointers]])
 manifest();verification=json.loads(run(['python3',str(D/'verify_saved_results.py')]));run(['python3',str(D/'verify_broad_comparisons.py')]);save(A/'FINAL_VERIFICATION.json',verification);manifest();allowed={p.relative_to(R).as_posix() for p in pointers};changed=run(['git','diff','--cached','--name-only']).splitlines();assert changed and all(n in allowed or n.startswith(D.relative_to(R).as_posix()+'/') for n in changed);run(['git','diff','--cached','--check']);run(['git','commit','-m','Save reconciled Unit8 broad host evidence and continuation checkpoint']);head=run(['git','rev-parse','HEAD']);run(['git','push','origin','HEAD:'+branch])
 old_body=run(['gh','pr','view','64','--json','body','--jq','.body']);body=f'''Reconciled Unit8 broad selected-host regression {label}; evidence checkpoint `{head}`.

Actual tested source `{source}`: **{total}**,237classes,exit{r['exitCode']}. Fresh XML/events/discovery/six inputs and raw SHA/CRC verified; every1394method reported exactly once. Full failure identities:{json.dumps(failed)}. Exact commands/task-cache/lint/gate/exits and monitoring preserved in {name}. Lint:{json.dumps(r['lintIssuesBySeverity'])}; gate task:{r['tasks']['verifyReleaseGateAttachment']}. Gate attachment is not release approval.

Four screenshot exporters/16methods excluded; physical reboot check remains outstanding/LAST. APK identity/signature null;assembly/install/signingNOT_RUN. Production/155records/95priorversions/60worksheets/69historical archives/original assertions conserved. Earlier1391pass/2fail/1physicalskip broad and separate23/23focused remain unchanged; overlapping counts are not summed. This selected host result is not complete app/curriculum/device acceptance.

Report:`audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit8-source-reconciliation-2026-10-24/RECONCILED_BROAD_REPORT.md`; NEXT_TASK.md and AGENTS/continuation/checkpoint updated. All48findingsOPEN/auditINCOMPLETE;24print-singleton policies/qualified reviews/CC048conflict/originalUnit7restorecause remain. Final independent comprehensive nonphysical audit FIRST; Galaxy S26 Ultra airplane-mode/reboot acceptance LAST. No merge/freeze/formal advancement/releaseapproval. Private Penguin untouched; provisional JIT directive unadopted/native causeUNCONFIRMED; no environment-setting changes.

---

Historical prior checkpoint (superseded current-state statements below):

'''+old_body
 bodyfile=O/'PR64_COMPLETED_BODY.md';bodyfile.write_text(body);response=json.loads(run(['gh','api','--method','PATCH','repos/justinkulka-lang/russian-companion/pulls/64','-F','body=@'+str(bodyfile),'--jq','{number,draft,head:.head.sha}']));assert response['draft'] and response['head']==head;assert not run(['git','status','--porcelain']);save(O/'AUTO_COMPLETION_RESULT.json',dict(completedUtc=datetime.now(timezone.utc).isoformat(),testedSource=source,evidenceCommit=head,counts=counts,classes=237,exitCode=r['exitCode'],pushed=True,prUpdated=True,pr=response,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',apkAssemblyInstallSigning='NOT_RUN'));print((O/'AUTO_COMPLETION_RESULT.json').read_text(),flush=True)
except BaseException:
 save(O/'AUTO_COMPLETION_ERROR.json',dict(atUtc=datetime.now(timezone.utc).isoformat(),traceback=traceback.format_exc(),note='Stop here; preserve raw artifacts and any staged evidence. Do not duplicate collection or force-push.'));raise
