"""Wait for the actual runner, verify complete evidence, then commit only audit/pointers."""
from pathlib import Path
from datetime import datetime,timezone
import subprocess,json,hashlib,zipfile,shutil,time,os,traceback
W=Path(__file__).parent;R=Path('/workspace/russian-companion')
REL=Path('audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-targeted-jit-broad-2026-10-23');D=R/REL
EXPECTED='b07432aada33a636d1ab547f8537da02208649ec'
sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
now=lambda:datetime.now(timezone.utc).isoformat()
def save(p,v):p.write_text(json.dumps(v,indent=2)+'\n')
def cmd(args,**kw):return subprocess.check_output(args,cwd=R,text=True,**kw).strip()
def state(value,**kw):save(W/'COLLECTOR_STATUS.json',dict(state=value,utc=now(),**kw))
def manifest():save(D/'SHA256.json',{p.relative_to(D).as_posix():sha(p) for p in sorted(D.rglob('*')) if p.is_file() and p!=D/'SHA256.json'})
try:
 state('WAITING_FOR_RUNNER',sourceCommit=EXPECTED)
 launcher=json.loads((W/'LAUNCHER.json').read_text());assert launcher['sourceCommit']==EXPECTED
 while not (W/'LAUNCHER_COMPLETION.json').exists():
  proc=Path('/proc')/str(launcher['pid'])/'cmdline'
  assert proc.exists() and b'supervise.py' in proc.read_bytes(),'Supervisor exited without a completion record'
  time.sleep(30)
 completed=json.loads((W/'LAUNCHER_COMPLETION.json').read_text())
 assert completed['sourceCommit']==EXPECTED
 assert completed['validatedResultExists'] and completed['manifestExists'],'No validated complete result; preserve raw evidence and investigate. Do not substitute stale XML.'
 O=W/'REGRESSION';result=json.loads((O/'RESULT.json').read_text());context=json.loads((O/'CONTEXT.json').read_text())
 assert result['sourceCommit']==context['sourceCommit']==EXPECTED
 assert cmd(['git','rev-parse','HEAD'])==EXPECTED and not cmd(['git','status','--porcelain'])
 assert result['testCounts']['reported']==1363 and result['classes']==229
 assert completed['runnerExitCode']==result['exitCode']==int((O/'RUN.exit').read_text())
 assert result['exitCode'] in [0,1]
 state('COLLECTING_VERIFIED_COMPLETION',sourceCommit=EXPECTED)
 # Unchanged protected prior evidence, including current generated records and native reports.
 prior=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-native-jvm-diagnostics-2026-10-23'
 conserved=json.loads((prior/'PRESERVED_PRIOR_EVIDENCE.json').read_text())
 for p in prior.rglob('*'):
  if p.is_file():conserved[p.relative_to(R).as_posix()]=sha(p)
 for name,value in conserved.items():assert sha(R/name)==value,name
 save(D/'CONSERVED_INPUTS.json',conserved)
 # Only raw run evidence and exact runner/init scripts; never collect repository assets/private PDFs.
 files={}
 for mode in ['DISCOVERY','REGRESSION']:
  for p in (W/mode).rglob('*'):
   if p.is_file():files[mode+'/'+p.relative_to(W/mode).as_posix()]=p
 for name in ['run.py','resources.gradle','supervise.py','LAUNCHER.json','RUNNER_PROCESS.json','LAUNCHER.exit','LAUNCHER_COMPLETION.json','RUNNER.log','LAUNCHER.log']:
  files[name]=W/name
 for name in context['initScriptSha256']:files['INIT/'+Path(name).name]=Path(name)
 assert not (D/'RAW_BROAD.zip').exists()
 index=[]
 with zipfile.ZipFile(D/'RAW_BROAD.zip','x',compression=zipfile.ZIP_DEFLATED,compresslevel=9) as z:
  for name,p in sorted(files.items()):
   assert p.is_file() and not p.name.lower().endswith('.pdf')
   index.append(dict(path=name,bytes=p.stat().st_size,sha256=sha(p)));z.write(p,name)
 save(D/'RAW_BROAD_INDEX.json',index)
 for name in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:
  shutil.copy2(O/name,D/('RUNTIME_'+name))
 shutil.copy2(W/'verify_saved_broad.py',D/'verify_saved_broad.py')
 shutil.copy2(W/'complete_evidence.py',D/'complete_evidence.py')
 save(D/'FAILED_METHODS.json',[t for t in result['tests'] if t['outcome'] in ['FAILED','ERROR']])
 save(D/'SKIPPED_METHODS.json',[t for t in result['tests'] if t['outcome']=='SKIPPED'])
 save(D/'CONSERVATION.json',dict(sixInputTrees=context['inputTrees'],currentGeneratedRecords=155,canonicalSourceDigest='2507d56bf2bd910b198d9ff317a605598765e13d947fa4f4435aaacd2a626892',digestBasis='Same six source trees as preceding official Kotlin computation; no approximate recomputation.',originalArchivesShaCrc=66,protectedFiles=len(conserved),apkIdentity=None,apkSignature=None,apkAssemblyInstallSigning='NOT_RUN',settingsSavedThisTask=[],publication='Earlier user-confirmed; independently unobserved. No new publication or persistent setup change.',nativeRootCause='UNCONFIRMED',directive='EXPERIMENTAL_NOT_ADOPTED',independentAudit='INCOMPLETE_ALL48OPEN',physicalAcceptance='OUTSTANDING_LAST'))
 counts=result['testCounts'];failed=[t['testClass']+'.'+t['method'] for t in result['tests'] if t['outcome'] in ['FAILED','ERROR']]
 text=f'''# Experimental broad host regression: completed selected scope

Actual runtime source: `{EXPECTED}`. Started `{context['startedUtc']}`, finished `{result['finishedUtc']}`. Gradle exit {result['exitCode']}; runner exit {completed['runnerExitCode']}. {counts['reported']} methods in {result['classes']} classes: **{counts['passed']} passed, {counts['failed']} failed, {counts['errors']} errors, {counts['skipped']} skipped**. This is one completed selected debug-host scope, not the complete app, curriculum or device suite. Do not add focused or earlier partial runs to its counts.

Fresh XML, recorded events and individual outcomes agree; all 1,363 discovery identities are present exactly once. Six input trees match discovery and earlier diagnostics. Full commands, initializer hashes, exact source, task statuses and test identities are in RUNTIME_CONTEXT.json/RUNTIME_RESULT.json; all full raw failures, XML, events, GC and logs are in RAW_BROAD.zip with member identities. The four screenshot exporters remain excluded (VisualReviewCaptureTest2, Units35ChunkCaptureTest2, Units610ChunkCaptureTest10, Units1115GapCaptureTest2). No additional test exclusions or method selectors. Physical AcceptanceBoundaryTest.at020 remains ignored, not accepted.

The single test-worker compiler directive `-XX:CompileCommand=exclude,androidx/sqlite/driver/SupportSQLiteConnection.prepare` remained experimental. It excludes compilation of that wrapper, not test execution. A completed run does not establish native root cause or a permanent fix; prior root cause remains UNCONFIRMED and old native evidence is conserved. Oracle JDK21.0.12.1+1-LTS-4, offline Gradle9.6, two6GiBforks/forkEvery4/max-workers2; no saved environment setting adopted.

Lint severities: `{json.dumps(result['lintIssuesBySeverity'],sort_keys=True)}`. Task results: `{json.dumps(result['tasks'],sort_keys=True)}`. Release-gate attachment passed; attachment is not release approval. No APK assembly/install/signing; APK identity/signature are null. All155generated records, original66archives and protected prior evidence remain exact. See CONSERVATION.json and CONSERVED_INPUTS.json. The read-only verifier independently checks SHA256, archive CRC/member identity, fresh XML, events, discovery, source trees, skips, exclusions, lint and attachment. It executes zero tests.

Earlier broad source e5b5d3c0 had 1,334 reported / 1,324 passed / 9 failed / 1 skipped; separate20c5eb96 follow-up56/56 is not added. Current discovery adds29methods and removes none; previous interrupted/native attempts remain incomplete and separate. Historical main ff539220 had563reported/562passed/1skipped, not a complete suite. The420f41ff handoff's691 source annotations are not verified execution; its67focused checks and the different source/scope do not establish a691-test run.

All48independent findings remain open, audit INCOMPLETE. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance is LAST after final independent comprehensive nonphysical audit. No merge, freeze, formal advancement or release approval. Private Penguin is outside packets and app assets.

Remaining failed/error methods ({len(failed)}):

'''+''.join('- `'+s+'`\n' for s in failed)+'''\nNext task: review FAILED_METHODS.json and raw actual failures before considering the unapplied PREPARED_FOLLOWUP.zip retained in the preceding NATIVE_FAILURE checkpoint. Preserve all original assertions/hashes and historical comparison scope; use fresh full-class discovery/runtime/lint/gate and conservation for any correction. Do not blindly replace hashes or claim this failing broad scope became green from a later focused run. If unexpected failures exist, investigate those before applying the proposal. Keep checkpoints and Draft PR64 updated without force.\n'''
 (D/'REPORT.md').write_text(text)
 (D/'STATUS.md').write_text('# Current status\n\nThe experimental broad run has completed. Read [REPORT.md](REPORT.md), exact runtime records and FAILED_METHODS.json. The previous launch preparation remains historical. Verify the saved evidence before editing or attempting another run. No background test job is required by this completed checkpoint.\n')
 manifest()
 verification=cmd(['python3',str(D/'verify_saved_broad.py')]);save(D/'PERSISTED_VERIFICATION.json',json.loads(verification));manifest()
 cmd(['python3',str(D/'verify_saved_broad.py')])
 for file,link in [(R/'AGENTS.md',str(REL/'REPORT.md')),(R/'audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','../gpt61-sol-high-comprehensive-2026-10-19/engineering-targeted-jit-broad-2026-10-23/REPORT.md'),(R/'audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md','../gpt61-sol-high-comprehensive-2026-10-19/engineering-targeted-jit-broad-2026-10-23/REPORT.md')]:
  old=file.read_text();first,rest=old.split('\n',1)
  file.write_text(first+'\n\nCurrent checkpoint: [Experimental broad host scope completed]('+link+f'). Source{EXPECTED[:8]}: {counts["reported"]}reported/{counts["passed"]}passed/{counts["failed"]}failed/{counts["errors"]}errors/{counts["skipped"]}skipped in229classes,exit{result["exitCode"]}. Fresh XML/events/discovery/six trees match. Experimental directive remains provisional/root causeUNCONFIRMED; original assertions and prior failures preserved. Read REPORT.md before corrections. Four exporters excluded; all48open/physicalLAST/no APK/signing/merge/freeze/approval.\n'+rest)
 cmd(['git','diff','--check'])
 cmd(['git','add',str(REL),'AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md'])
 cmd(['git','diff','--cached','--check'])
 staged=cmd(['git','diff','--cached','--name-only']).splitlines()
 assert all(p.startswith(str(REL)+'/') or p in ['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md'] for p in staged)
 assert cmd(['git','rev-parse','HEAD'])==EXPECTED
 cmd(['git','commit','-m','Record verified experimental broad host outcomes and preserved failures'])
 commit=cmd(['git','rev-parse','HEAD']);state('COMMITTED_PENDING_PUSH',commit=commit,counts=counts)
 subprocess.run(['git','push','origin','HEAD:cursor/content-audit-corrections-964f'],cwd=R,check=True)
 pr=json.loads(cmd(['gh','api','repos/justinkulka-lang/russian-companion/pulls/64']))
 assert pr['draft'] and pr['state']=='open' and pr['head']['sha']==commit
 prefix=f'''Experimental broad selected-host run completed at `{EXPECTED}`; evidence commit `{commit}`. **{counts['reported']} reported: {counts['passed']} passed, {counts['failed']} failed, {counts['errors']} errors, {counts['skipped']} skipped / 229 classes, exit {result['exitCode']}.** Fresh XML/events/discovery/source trees verified and raw evidence retained. This is not a complete app/curriculum/device suite. Four exporters/16 methods remain excluded.\n\nThe targeted SQLite wrapper compiler directive remains experimental; native root cause UNCONFIRMED. No assertion fixes were applied in this run. Read `{REL}/REPORT.md` and FAILED_METHODS.json for the next correction task. All48independent findings remain open; physical acceptance LAST. No APK/signing/merge/freeze/formal advancement/release approval.\n\n---\n\n'''
 body=W/'PR64_COMPLETED_BODY.md';body.write_text(prefix+pr['body'])
 cmd(['gh','api','--method','PATCH','repos/justinkulka-lang/russian-companion/pulls/64','-F','body=@'+str(body),'--jq','.head.sha'])
 cmd(['python3',str(D/'verify_saved_broad.py')]);assert not cmd(['git','status','--porcelain'])
 state('PUSHED_AND_PR_UPDATED',commit=commit,counts=counts,sourceCommit=EXPECTED,completeSuite=False)
except BaseException:
 (W/'COLLECTOR_ERROR.txt').write_text(traceback.format_exc());state('STOPPED_WITH_ERROR',errorFile=str(W/'COLLECTOR_ERROR.txt'));raise
