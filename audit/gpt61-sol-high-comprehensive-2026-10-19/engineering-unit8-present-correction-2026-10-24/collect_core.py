from pathlib import Path
import subprocess,json,hashlib,zipfile,shutil
R=Path('/workspace/russian-companion');W=Path(__file__).parent;B=R/'audit/gpt61-sol-high-comprehensive-2026-10-19';D=B/'engineering-unit8-present-correction-2026-10-24';S='77e3d26eab138796a699d9133e45e4f793e95cc1';sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest();git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
assert git('rev-parse','HEAD')==S and not git('status','--porcelain')
c=json.loads((W/'REGRESSION_COMPLETION.json').read_text());r=json.loads((W/'REGRESSION/RESULT.json').read_text());assert c['resultExists'] and c['manifestExists'] and r['sourceCommit']==S and r['testCounts']==dict(reported=12,passed=11,failed=1,errors=0,skipped=0)
assert not (D/'RAW_CORE.zip').exists()
files={}
for mode in ['DISCOVERY','REGRESSION']:
 for p in (W/mode).rglob('*'):
  if p.is_file():files[p.relative_to(W).as_posix()]=p
 for n in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copy2(W/mode/n,D/(mode+'_'+n))
 for x in ['COMPLETION.json','LAUNCHER.json','RUNNER_PROCESS.json','LAUNCHER.exit','RUNNER.log','LAUNCHER.log']:files[mode+'_'+x]=W/(mode+'_'+x)
for n in ['run.py','resources.gradle','supervise.py']:files[n]=W/n
for n in json.loads((W/'REGRESSION/CONTEXT.json').read_text())['initScriptSha256']:files['INIT/'+Path(n).name]=Path(n)
index={}
with zipfile.ZipFile(D/'RAW_CORE.zip','x',zipfile.ZIP_DEFLATED) as z:
 for n,p in sorted(files.items()):assert not n.lower().endswith('.pdf');z.write(p,n);index[n]=dict(bytes=p.stat().st_size,sha256=sha(p))
(D/'RAW_CORE_INDEX.json').write_text(json.dumps(index,indent=2)+'\n')
for n in ['verify_saved_results.py','collect_core.py']:shutil.copy2(W/n,D/n)
record=json.loads((B/'engineering-unit8-present-baseline-2026-10-24/CURRENT_RECORD_HASHES.json').read_text());assert len(record)==155
for n,h in record.items():assert sha(R/n)==h
archives=json.loads((B/'engineering-unit56-conservation-2026-10-23/ORIGINAL_ARCHIVE_CONSERVATION.json').read_text())
for p in [B/'engineering-unit6-present-baseline-2026-10-23/BASELINE_CURRENT_UNIT6_V2.zip',B/'engineering-unit7-gender-baseline-2026-10-24/BASELINE_CURRENT_UNIT7_V1.zip',B/'engineering-unit8-present-baseline-2026-10-24/BASELINE_CURRENT_UNIT8_V1.zip']:archives[str(p.relative_to(R))]=sha(p)
for n,h in archives.items():
 assert sha(R/n)==h
 with zipfile.ZipFile(R/n) as z:assert z.testzip() is None
pointers=['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md'];conserved={n:sha(R/n) for n in git('ls-files','audit').splitlines() if n not in record and n not in pointers and not n.startswith(str(D.relative_to(R))+'/')}
(D/'CONSERVATION.json').write_text(json.dumps(dict(sourceCommit=S,currentGeneratedRecordHashes=record,currentGeneratedRecordStatus='STALE_UNCHANGED_NOT_REGENERATED',archives=archives,immutablePriorEvidenceHashes=conserved,oldPolicyObjectsConserved=34,baselineFileUnchanged=True,sourceDigest='NOT_COMPUTED_IMPLEMENTATION_PENDING_FOLLOWUP',nativeRootCause='UNCONFIRMED',experimentalDirective='NOT_ADOPTED',settingsChanges=[],apkIdentity=None,apkSignature=None,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST'),indent=2)+'\n')
with zipfile.ZipFile(D/'INITIAL_UI_TEST_SOURCE.zip','x',zipfile.ZIP_DEFLATED) as z:
 n='app/src/test/java/dev/russiancompanion/v2/Unit8PresentReviewUiTest.kt';z.write(R/n,n)
(D/'INITIAL_OUTCOME.md').write_text('''# Initial Unit8 core result

77e3d26eab138796a699d9133e45e4f793e95cc1:12reported/11passed/1failed/0errors/0skipped,3fullclasses,exit1. Wholebaseline2/2andcompatibility5/5passed. FourUIcontrols passed; newunpreparedscopeassert failed because it expects contiguous"These finite recognitions do not demonstrate unseen reading",butnewcopylists productiveconjugationfirst. No scope meaning mismatch, source/selection/history defect claimed. Original test assertions/wholefile archived and unchanged. Next: reorder onlynewUIcopydisclaimer, then fresh12method full-class discovery/runtime. Preservefirst12scope asfailing; do not relabel it afterfollowup.\n''')
def manifest():
 (D/'SHA256.json').write_text(json.dumps({p.relative_to(D).as_posix():sha(p) for p in sorted(D.rglob('*')) if p.is_file() and p.name!='SHA256.json'},indent=2)+'\n');subprocess.run(['git','add',str(D)],cwd=R,check=True)
manifest();v=json.loads(subprocess.check_output(['python3',str(D/'verify_saved_results.py')],cwd=R,text=True));print(json.dumps(v));(D/'INITIAL_VERIFICATION.json').write_text(json.dumps(v,indent=2)+'\n');manifest()
