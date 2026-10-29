from pathlib import Path
import subprocess,json,hashlib,zipfile,shutil
R=Path('/workspace/russian-companion');W=Path(__file__).parent;C=W/'CORRECTED_COPY';D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit8-present-correction-2026-10-24';S='370d8716e2747c439da1a450a83b71b129a5c879';sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
assert subprocess.check_output(['git','rev-parse','HEAD'],cwd=R,text=True).strip()==S and not subprocess.check_output(['git','status','--porcelain'],cwd=R,text=True).strip()
r=json.loads((C/'REGRESSION/RESULT.json').read_text());end=json.loads((C/'REGRESSION_COMPLETION.json').read_text());assert r['sourceCommit']==S and r['exitCode']==end['runnerExit']==0 and r['testCounts']==dict(reported=12,passed=12,failed=0,errors=0,skipped=0)
files={}
for mode in ['DISCOVERY','REGRESSION']:
 for p in (C/mode).rglob('*'):
  if p.is_file():files[p.relative_to(C).as_posix()]=p
 for n in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copy2(C/mode/n,D/('CORRECTED_'+mode+'_'+n))
 for x in ['COMPLETION.json','LAUNCHER.json','RUNNER_PROCESS.json','LAUNCHER.exit','RUNNER.log','LAUNCHER.log']:files[mode+'_'+x]=C/(mode+'_'+x)
for n in ['run.py','resources.gradle','supervise.py']:files[n]=C/n
for n in json.loads((C/'REGRESSION/CONTEXT.json').read_text())['initScriptSha256']:files['INIT/'+Path(n).name]=Path(n)
index={}
with zipfile.ZipFile(D/'RAW_CORE_CORRECTED.zip','x',zipfile.ZIP_DEFLATED) as z:
 for n,p in sorted(files.items()):assert not n.lower().endswith('.pdf');z.write(p,n);index[n]=dict(bytes=p.stat().st_size,sha256=sha(p))
(D/'RAW_CORE_CORRECTED_INDEX.json').write_text(json.dumps(index,indent=2)+'\n')
for n in ['verify_corrected_results.py','collect_corrected.py','OFFICIAL_DIGEST_OUTPUT.txt','OFFICIAL_SOURCE_DIGEST.json','Digest.java','CURRENT_POLICY_OBSERVATION.json','ExportReviewPolicies.java','POLICY_COMPILE.txt','POLICY_EXPORT.txt']:shutil.copy2(W/n,D/n)
print(json.dumps({'collected':True,'source':S,'counts':r['testCounts'],'officialDigest':json.loads((D/'OFFICIAL_SOURCE_DIGEST.json').read_text())['canonicalSourceDigest']}))
