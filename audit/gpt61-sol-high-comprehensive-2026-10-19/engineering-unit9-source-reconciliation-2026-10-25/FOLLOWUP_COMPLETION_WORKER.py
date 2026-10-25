"""Collect the actual completed follow-up; audit/docs-only commit, normal push, Draft PR64 update."""
from pathlib import Path
from datetime import datetime,timezone
import json,time,os,subprocess,hashlib,zipfile,traceback
R=Path('/workspace/russian-companion');W=Path(__file__).parent;B=W.parent;D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit9-source-reconciliation-2026-10-25';branch='cursor/content-audit-corrections-964f'
run=lambda a:subprocess.check_output(a,cwd=R,text=True).strip();sha=lambda b:hashlib.sha256(b).hexdigest();save=lambda p,x:p.write_text(json.dumps(x,indent=2)+'\n')
try:
 with (W/'AUTO_COMPLETION.lock').open('x') as f:f.write(str(os.getpid())+'\n')
 save(W/'AUTO_COMPLETION_PROCESS.json',dict(pid=os.getpid(),startedUtc=datetime.now(timezone.utc).isoformat(),scope='collect/verify actual follow-up; audit/docs-only commit,normal push,DraftPR64 update'))
 while not (W/'REGRESSION_COMPLETION.json').exists():time.sleep(45)
 done=json.loads((W/'REGRESSION_COMPLETION.json').read_text());assert done['resultExists'] and done['manifestExists'],'Incomplete runner;preserve raw evidence'
 r=json.loads((W/'REGRESSION/RESULT.json').read_text());c=json.loads((W/'REGRESSION/CONTEXT.json').read_text());source=r['sourceCommit'];assert run(['git','rev-parse','HEAD'])==source==c['sourceCommit'];assert run(['git','branch','--show-current'])==branch and not run(['git','status','--porcelain']);assert r['testCounts']['reported']==74 and r['classes']==15 and done['runnerExit']==r['exitCode']==int(done['gradleExit'])
 # Preserve source/archive identities and prove every original assertion/pin line survives.
 rows=json.loads((D/'ORIGINAL_COMPARISONS_INDEX.json').read_text())
 with zipfile.ZipFile(D/'ORIGINAL_COMPARISONS.zip') as z:
  assert z.testzip() is None
  for x in rows:
   old=z.read(x['path']);new=(R/x['path']).read_bytes();assert sha(old)==x['sha256'] and sha(new)==x['appliedSha256'];assert old==subprocess.check_output(['git','show',x['sourceCommit']+':'+x['path']],cwd=R);it=iter(t.strip() for t in new.decode().splitlines() if t.strip());assert all(any(t==s.strip() for t in it) for s in old.decode().splitlines() if s.strip())
 original=json.loads((D/'ATTEMPT_1/REGRESSION_RESULT.json').read_text());failed={(t['testClass'],t['method']) for t in original['tests'] if t['outcome'] in ['FAILED','ERROR']};rechecked=[x for x in r['tests'] if (x['testClass'],x['method']) in failed];assert len(failed)==len(rechecked)==5
 run(['python3',str(B/'collect_attempt.py'),'ATTEMPT_2','FOLLOWUP']);A=D/'ATTEMPT_2';save(A/'FIVE_FAILURE_RECHECK.json',dict(originalSource=original['sourceCommit'],originalCounts=original['testCounts'],followupSource=source,rechecked=rechecked,overlappingScopesNotSummed=True));save(A/'ASSERTION_CONSERVATION.json',dict(originalFiles=rows,everyOriginalNonblankLineRetained=True,onlyFourHistoricalComparisonTestFilesChanged=True))
 m=json.loads((W/'DURABLE_MONITOR_STATUS.json').read_text());idx={}
 with zipfile.ZipFile(A/'MONITORING.zip','x',zipfile.ZIP_DEFLATED) as z:
  for n in ['durable_monitor.py','DURABLE_MONITOR.log','DURABLE_MONITOR_STATUS.json','complete_followup.py']:
   p=W/n;z.write(p,n);idx[n]=dict(bytes=p.stat().st_size,sha256=sha(p.read_bytes()))
 save(A/'MONITORING_INDEX.json',idx);n=r['testCounts'];counts=f"{n['reported']}reported/{n['passed']}passed/{n['failed']}failed/{n['errors']}errors/{n['skipped']}skipped";failures=[x for x in r['tests'] if x['outcome'] in ['FAILED','ERROR']];good=not failures and r['exitCode']==0
 text=f'''# Unit9 historical-comparison follow-up completed

Actual tested `{source}`,started `{c['startedUtc']}`,finished `{r['finishedUtc']}`(UTC): **{counts}**,15fullclasses,actual Gradle/runner exit{r['exitCode']}. Fresh discovery/XML/unique events/outcomes/six input trees match. Exact commands,activation,initializers,source/root/input trees,full XML,raw logs/GC/lint/exits are in ATTEMPT_2/RAW.zip. Five original failed identities were rechecked in full classes; see FIVE_FAILURE_RECHECK.json. Actual follow-up failures:{json.dumps(failures)}.

Original integration remains **242reported/236passed/5failed/0errors/1physicalskip**,49classes,exit1 at fd246abd6c004633d28f5f00dc3069b6dd0f6e45; ATTEMPT_1/INTEGRATION_REPORT.md and all raw failure bytes remain unchanged. Follow-up and initial scopes overlap and are not summed. This is selected-host evidence,not complete-suite/app/curriculum/device acceptance or independent finding closure.

Four historical comparison scopes were narrowed. Every original nonblank line/assertion/source pin is retained; exact original files/Gitblobs/SHA are in ORIGINAL_COMPARISONS.zip/index. Whole-pack comparisons reconstruct the exact genuine before9 declaration. Old Unit9 slot assertions run against a separately created real prior-v2 task; the original current task still exercises schema11/v3/current objectives/skills,completion/advancement/backup. Old placement remains on the genuine prior declaration;current owner5 and genuinely taught5->missing8 routes have separate actual UI controls. The preserved Unit9 intake digest runs on strict before9,with explicit actual current source identity. Production/catalog/docs/review/release and155records unchanged by reconciliation. All70archives/wholebaseline/coreassertions conserved. The original source-binding phase's two153file renders/30packages/95identity-only changes/60unchangedworksheets/95historical versions remain verified,not regenerated here.

Lint:{json.dumps(r['lintIssuesBySeverity'])}. Tasks/cache:{json.dumps(r['tasks'])}. Gate attachment is not release approval. APK identity/signature null;assembly/install/signingNOT_RUN. Four exporters/16methods explicitly excluded:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. All48findingsOPEN/auditINCOMPLETE;23print-singletonpolicies/qualified reviews/CC048authority conflict/originalUnit7restorecause remain. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance OUTSTANDING/LAST after final independent comprehensive nonphysical audit. No merge/freeze/formal advancement/releaseapproval. Private Penguin/environment settings unchanged;earlier publication user-confirmed/independently unobserved. Provisional SQLite JIT directive remains unadopted/native causeUNCONFIRMED;newnative reports:{json.dumps(m['newNativeReports'])}.
'''
 (D/'FOLLOWUP_REPORT.md').write_text(text);(D/'FOLLOWUP_STATUS.md').write_text(text)
 next_text='Next large task: fresh broad selected-host discovery and uninterrupted regression on current source. Derive exact inventory (expected1410methods/241classes is not execution),compare all earlier1394method IDs and preserve every raw outcome/source/six inputs. No new broad result is claimed.' if good else 'Review actual ATTEMPT_2 failures/raw XML and preserve this result before further changes. Do not launch broad regression while affected failures remain unresolved.'
 (D/'NEXT_TASK.md').write_text('# Next task after Unit9 comparison follow-up\n\n'+next_text+'\n\nRead FOLLOWUP_REPORT.md and the immutable INTEGRATION_REPORT.md. One agent/existing checkout. All48OPEN/auditINCOMPLETE/four exporters excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n')
 pointers=[R/'AGENTS.md',R/'audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md',R/'audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']
 for p in pointers:
  link=('audit/' if p.name=='AGENTS.md' else '../')+'gpt61-sol-high-comprehensive-2026-10-19/engineering-unit9-source-reconciliation-2026-10-25/FOLLOWUP_REPORT.md';p.write_text(f'Current checkpoint: [Unit9 historical comparisons: follow-up completed]({link}). Actual{source}:{counts},15classes,exit{r["exitCode"]};original242scope236pass/5fail/1physicalskip retained,separate/unsummed. AllfivefailedIDs rechecked/all original assertions/pins retained;production/155records/70archives conserved. Read NEXT_TASK.md. All48OPEN/physicalLAST/four exporters excluded/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
 def manifest():
  save(D/'SHA256.json',{p.relative_to(D).as_posix():sha(p.read_bytes()) for p in sorted(D.rglob('*')) if p.is_file() and p.name!='SHA256.json'});run(['git','add',str(D),*[str(p) for p in pointers]])
 manifest();v=json.loads(run(['python3',str(D/'verify_saved_results.py')]));save(A/'FINAL_VERIFICATION.json',v);manifest();allowed={p.relative_to(R).as_posix() for p in pointers};changed=run(['git','diff','--cached','--name-only']).splitlines();assert changed and all(x in allowed or x.startswith(D.relative_to(R).as_posix()+'/') for x in changed);run(['git','diff','--cached','--check']);run(['git','commit','-m','Preserve Unit9 comparison follow-up evidence and next broad checkpoint']);head=run(['git','rev-parse','HEAD']);run(['git','push','origin','HEAD:'+branch]);previous=run(['gh','pr','view','64','--json','body','--jq','.body']);body=f'''Unit9 historical comparison follow-up completed;checkpoint `{head}`.

Actual tested `{source}`: **{counts}**,15fullclasses,exit{r['exitCode']}. Allfive original failed identities rechecked;fresh XML/events/discovery/six inputs verified. Lint:{json.dumps(r['lintIssuesBySeverity'])};gate:{r['tasks']['verifyReleaseGateAttachment']}. Original242scope at fd246abd remains236pass/5fail/1physicalskip,exit1,with full raw failures. Scopes overlap/not summed;not a complete-suite/independent acceptance result.

Only four exact historical-comparison test files changed;every original assertion/pin retained and archived with source/Gitblob/SHA. Actual-current Unit8/9/10 controls included. Production/155records/70archives unchanged. Report:`audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit9-source-reconciliation-2026-10-25/FOLLOWUP_REPORT.md`;NEXT_TASK/AGENTS/continuation/checkpoint updated. Next:{next_text}

All48OPEN/auditINCOMPLETE/four exporters16methods excluded/physical acceptance LAST after final independent comprehensive nonphysical audit. No APK assembly/install/signing/merge/freeze/formal advancement/releaseapproval. Private reference/environment settings unchanged;JITdirective unadopted/native causeUNCONFIRMED.

---
Historical checkpoint (superseded active-state statements below):

'''+previous;p=W/'PR64_FOLLOWUP_COMPLETED_BODY.md';p.write_text(body);response=json.loads(run(['gh','api','--method','PATCH','repos/justinkulka-lang/russian-companion/pulls/64','-F','body=@'+str(p),'--jq','{number,draft,head:.head.sha}']));assert response['draft'] and response['head']==head and not run(['git','status','--porcelain']);save(W/'AUTO_COMPLETION_RESULT.json',dict(completedUtc=datetime.now(timezone.utc).isoformat(),testedSource=source,evidenceCommit=head,counts=n,classes=15,exitCode=r['exitCode'],pushed=True,prUpdated=True,allFiveFailedIdentitiesRechecked=True,scopesNotSummed=True,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST'));print((W/'AUTO_COMPLETION_RESULT.json').read_text(),flush=True)
except BaseException:
 save(W/'AUTO_COMPLETION_ERROR.json',dict(atUtc=datetime.now(timezone.utc).isoformat(),traceback=traceback.format_exc(),instruction='Preserve raw/staged evidence;diagnose actual error;never force-push or duplicate collector'));raise
