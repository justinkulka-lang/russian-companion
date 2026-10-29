from pathlib import Path
from datetime import datetime,timezone
import json,hashlib,subprocess,xml.etree.ElementTree as ET,zipfile,shutil,csv
R=Path('/workspace/russian-companion');W=Path('/workspace/artifacts/ui-feedback-onboarding-correction-2026-10-28');D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-ui-feedback-onboarding-correction-2026-10-28'
read=lambda p:json.loads(p.read_text());sha=lambda b:hashlib.sha256(b).hexdigest();save=lambda p,v:p.write_text(json.dumps(v,indent=2)+'\n');git=lambda *a:subprocess.check_output(['git',*a],cwd=R).decode().strip()
assert not subprocess.check_output(['git','status','--porcelain=v1','-z','--untracked-files=all'],cwd=R)
prep=read(D/'PREPARATION.json');scope=read(W/'SCOPE.json');end=read(W/'REGRESSION_COMPLETION.json');ctx=read(W/'REGRESSION/CONTEXT.json');dis=read(W/'DISCOVERY/RESULT.json');assert end['runnerExit']==1 and end['gradleExit']=='0'and not end['resultExists']
assert git('rev-parse','HEAD')==ctx['sourceCommit']==dis['sourceCommit'];assert prep['inputTrees']==scope['inputTrees']==ctx['inputTrees']==dis['inputTrees']
for n,h in prep['testHashes'].items():assert sha((R/n).read_bytes())==h
assert len(prep['testHashes'])==284
expected={(x['testClass'],x['method'])for x in scope['expectedMethods']};discovered={(x['testClass'],x['method'])for x in dis['tests']};missing=discovered-expected
assert len(expected)==61 and len(discovered)==62 and expected<=discovered and missing=={('dev.russiancompanion.v2.V2RebuildTest','primaryContentStartsAtTheInsetWhenFontIsLarge')}
rows=[];failures=[];suites=[]
for p in sorted((W/'REGRESSION/XML').glob('*.xml')):
 s=ET.parse(p).getroot();suites.append(s.attrib);stamp=datetime.fromisoformat(s.get('timestamp').replace('Z','+00:00'));start=datetime.fromisoformat(ctx['startedUtc']);assert stamp>=start
 for t in s.findall('testcase'):
  bad=next((t.find(x)for x in ['failure','error']if t.find(x)is not None),None);status='FAILED'if t.find('failure')is not None else'ERROR'if t.find('error')is not None else'SKIPPED'if t.find('skipped')is not None else'PASSED'
  rows.append({'testClass':t.get('classname'),'method':t.get('name'),'outcome':status,'seconds':t.get('time')})
  if bad is not None:failures.append({'testClass':t.get('classname'),'method':t.get('name'),'message':bad.get('message',''),'trace':bad.text or ''})
xmlids={(x['testClass'],x['method'])for x in rows};assert len(rows)==len(xmlids)==62 and xmlids==discovered
assert {x['testClass']for x in rows}==set(scope['selectors']) and len(suites)==6
assert {(x['testClass'],x['method'])for x in dis['tests']}==xmlids
es=[read for read in [json.loads(x)for x in(W/'REGRESSION/EVENTS.jsonl').read_text().splitlines()]if read['kind']=='RESULT'];assert len(es)==62
by={(x['className'],x['name']):x['resultType']for x in es};assert set(by)==xmlids
for x in rows:assert by[(x['testClass'],x['method'])]=={'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[x['outcome']]
counts={'reported':len(rows),'passed':sum(x['outcome']=='PASSED'for x in rows),'failed':sum(x['outcome']=='FAILED'for x in rows),'errors':sum(x['outcome']=='ERROR'for x in rows),'skipped':sum(x['outcome']=='SKIPPED'for x in rows)}
assert counts==dict(reported=62,passed=62,failed=0,errors=0,skipped=0)
log=(W/'REGRESSION/RUN.log').read_text();tasks={}
for name in ['testDebugUnitTest','compileDebugKotlin','compileDebugUnitTestKotlin','compileDebugSources','lintAnalyzeDebug','lintAnalyzeDebugUnitTest','lintDebug','verifyReleaseGateAttachment']:
 prefix='> Task :app:'+name;tasks[name]=next((s for s in log.splitlines()if s==prefix or s.startswith(prefix+' ')),'NOT_REACHED')
assert all(tasks[n]!='NOT_REACHED'and 'FAILED'not in tasks[n]for n in ['testDebugUnitTest','compileDebugSources','lintDebug','verifyReleaseGateAttachment'])
lint=R/'app/build/reports/lint-results-debug.xml';shutil.copyfile(lint,W/'REGRESSION/LINT_CAPTURED_AFTER_COLLECTOR_REFUSAL.xml');issues=ET.parse(lint).getroot().findall('issue');severities={s:sum(x.get('severity')==s for x in issues)for s in ['Fatal','Error','Warning','Hint']};assert severities==dict(Fatal=0,Error=0,Warning=20,Hint=1)
result={'sourceCommit':ctx['sourceCommit'],'inputTrees':ctx['inputTrees'],'testCounts':counts,'classes':6,'exitCode':0,'gradleExitCode':0,'originalCollectorExitCode':1,'originalCollectorResultExists':False,'derivation':'POST_RUN_INDEPENDENT_XML_EVENT_DISCOVERY_RECONCILIATION','derivedAtUtc':datetime.now(timezone.utc).isoformat(),'predictedInventory':61,'actualDiscoveredAndExecuted':62,'additionalMethod':sorted(missing),'inventoryMismatchCause':'Regex required @Test immediately before fun and missed intervening @Config; no app/test change or runtime failure','originalScopeAndRawRunnerUnchanged':True,'tests':rows,'suites':suites,'tasks':tasks,'lintIssuesBySeverity':severities,'lintCapture':'Actual completed run report copied after collector refusal; no later Gradle invocation','completeSuite':False,'scopesNotSummed':True,'physicalAcceptance':'OUTSTANDING_LAST','apkAssemblyInstallSigning':'NOT_RUN','apkIdentity':None,'apkSignature':None,'all48Findings':'OPEN','audit':'INCOMPLETE'}
save(W/'POST_RUN_RECONCILIATION.json',result);save(D/'REGRESSION_RESULT.json',result);save(D/'INVENTORY_CORRECTION.json',{k:result[k]for k in ['predictedInventory','actualDiscoveredAndExecuted','additionalMethod','inventoryMismatchCause','originalScopeAndRawRunnerUnchanged','originalCollectorExitCode','gradleExitCode']})
with(D/'REGRESSION_RESULTS.csv').open('w',newline='')as f:w=csv.DictWriter(f,fieldnames=list(rows[0]));w.writeheader();w.writerows(rows)
save(D/'ACTUAL_FAILURES.json',{'sourceCommit':ctx['sourceCommit'],'failures':failures,'count':len(failures),'originalBaselineFailuresRetainedSeparately':True})
for mode,label in [('DISCOVERY','RAW_DISCOVERY'),('REGRESSION','RAW_RUNTIME')]:
 files={p.relative_to(W).as_posix():p for p in(W/mode).rglob('*')if p.is_file()}
 for p in W.glob(mode+'_*'):
  if p.is_file():files[p.name]=p
 for n in ['run.py','launch.py','supervise.py','resources.gradle','prepare.py','SCOPE.json']:files['TOOLING/'+n]=W/n
 if mode=='REGRESSION':files['POST_RUN_RECONCILIATION.json']=W/'POST_RUN_RECONCILIATION.json'
 files['TOOLING/reconcile-ui-verification.py']=Path(__file__)
 for n,h in read(W/mode/'CONTEXT.json')['initScriptSha256'].items():
  p=Path(n);assert sha(p.read_bytes())==h;files['INIT/'+p.name]=p
 idx={}
 with zipfile.ZipFile(D/(label+'.zip'),'x',zipfile.ZIP_DEFLATED)as z:
  for n,p in sorted(files.items()):
   assert not n.lower().endswith('.pdf');data=p.read_bytes();z.writestr(n,data);idx[n]={'bytes':len(data),'sha256':sha(data)}
 save(D/(label+'_INDEX.json'),idx)
 with zipfile.ZipFile(D/(label+'.zip'))as z:
  assert z.testzip()is None
  for n,v in idx.items():assert sha(z.read(n))==v['sha256']and len(z.read(n))==v['bytes']
 shutil.copyfile(W/mode/'CONTEXT.json',D/(mode+'_CONTEXT.json'))
 if mode=='DISCOVERY':
  for n in ['RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copyfile(W/mode/n,D/(mode+'_'+n))
shutil.copyfile(__file__,D/'reconcile-ui-verification.py')
(D/'COMPLETED_STATUS.md').write_text('''# Feedback and onboarding correction verified in selected host scope

Actual source `ff450b7215d5f7ab24f7e9b10d7b831430e5af82`: **62 reported / 62 passed / zero failures, errors or skips**, six FULL classes, Gradle exit 0. All 284 test files, including all six refined baseline methods, remained exact. Three wrong-option UI routes now display the exact authored rationale alongside unchanged exact common feedback. Both onboarding restoration tests pass without automatic submission; existing option-order, runtime, rebuild and Unit14 feedback controls also pass.

The preparatory regex predicted 61 methods, missing V2RebuildTest.primaryContentStartsAtTheInsetWhenFontIsLarge because @Config intervenes between @Test and fun. Actual discovery and runtime both contain exactly 62 matching methods/classes. The original collector exited 1 on that mismatch after Gradle succeeded, before emitting its result. Its exception, original 61-method scope, completion record, XML and events are immutable in RAW_RUNTIME.zip. Post-run independent reconciliation verifies all 62 XML/event outcomes and discovery identities; it does not represent a new execution or an original collector success. No app/test changes or rerun were needed to reconcile the inventory. Future scopes must derive exact methods from actual discovery before runtime.

Lint: zero fatal/errors, 20 warnings, one hint. compileDebugSources and verifyReleaseGateAttachment passed; exact cache/task statuses and command are retained. Raw source/test identities, logs, init scripts, XML/events, collector exception and derived result have SHA256/CRC-verified archives. APK assembly/install/signing NOT_RUN; APK identity/signature null. First baseline37ea3178 and refined baselinecf6ced08 each retain their separate 6reported/1pass/5fail evidence unchanged. Cache25 and this62 scope remain separate/unsummed; no complete-suite claim.

Bindings remain RECONCILIATION_PENDING. All48 independent findings remain OPEN, audit INCOMPLETE, including CC034/CC040 pending independent recheck and CC019 physical TalkBack acceptance. Four screenshot exporters/16methods remain explicitly excluded. Final independent comprehensive Claude Code audit precedes physical Galaxy S26 Ultra airplane-mode/reboot acceptance LAST. No merge, freeze, new signing or release approval. GitHub Actions budget remains an independent execution blocker; host passes are not CI passes.
''')
for n in ['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']:
 p=R/n;rel=D.relative_to(R).as_posix()+'/COMPLETED_STATUS.md';link=rel if n=='AGENTS.md'else'../'+rel.removeprefix('audit/')
 p.write_text(f'Current checkpoint: [Feedback and onboarding correction verified]({link}). Actualff450b72:62/62passed/6FULLclasses/Gradleexit0;originalcollectorrefused61vs62 inventory (exit1),honestlyreconciledfromrawdiscovery/XML/events without rerun/testchange. All284tests unchanged;baselines retained;bindingsPENDING. Nextverification-onlyCI+authenticbindings+full119retry/broad. Codex only;all48OPEN/auditINCOMPLETE/four exporters16excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
save(D/'SHA256.json',{p.relative_to(D).as_posix():sha(p.read_bytes())for p in sorted(D.rglob('*'))if p.is_file()and p!=D/'SHA256.json'})
subprocess.run(['git','add',str(D),'AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md'],cwd=R,check=True);subprocess.run(['git','diff','--cached','--check'],cwd=R,check=True);subprocess.run(['git','commit','-m','Record 62 passing UI and runtime controls with inventory reconciliation'],cwd=R,check=True)
print(json.dumps({k:result[k]for k in ['sourceCommit','testCounts','classes','gradleExitCode','originalCollectorExitCode']}))
