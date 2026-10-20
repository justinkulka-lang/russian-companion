from pathlib import Path
import subprocess,json,hashlib,shlex,sys,re
r=Path('/workspace/russian-companion');o=Path('/workspace/artifacts/post-correction-broad-2026-10-20/continuation')/sys.argv[1];o.mkdir()
git=lambda *a:subprocess.check_output(['git',*a],cwd=r,text=True).strip()
selectors=json.loads(Path('/workspace/artifacts/post-correction-broad-2026-10-20/STOPPED_RECOVERY/CONTINUATION_SELECTORS.json').read_text())
inits=['/workspace/setup/cloud-validation.gradle',str(r/'audit/independent-gpt61-2026-10-12/broader-regression-2026-10-18/broader-tests.gradle')]
inits.append('/workspace/artifacts/post-correction-broad-2026-10-20/continuation/resources.gradle')
cmd=['./gradlew']
for i in inits:cmd+=['-I',i]
cmd+=['-Daudit.events.path='+str(o/'EVENTS.jsonl'),'-Daudit.gc.dir='+str(o),'testDebugUnitTest']
for s in selectors:cmd+=['--tests',s]
cmd+=['--rerun','compileDebugSources','lintDebug','verifyReleaseGateAttachment','--continue','--offline','--max-workers=2','--console=plain']
files=[]; expected=0
for selector in selectors:
 file='app/src/test/java/'+selector.replace('.','/')+'.kt'
 if (r/file).exists(): expected+=len(re.findall(r'@Test\s+fun\s+(\w+)',(r/file).read_text()))
 else:
  file='app/src/test/java/'+selector.rsplit('.',1)[0].replace('.','/')+'.kt';assert (r/file).exists();expected+=1
 files.append(file)
c=dict(sourceCommit=git('rev-parse','HEAD'),sourceRootTree=git('rev-parse','HEAD^{tree}'),inputTrees={d:git('rev-parse','HEAD:'+d) for d in ['app/src/main','app/src/test','catalog','docs','review']},command=cmd,workingDirectory=str(r),activation='source /workspace/setup/android-env.sh',selectors=selectors,expectedMethods=expected,sourceTestHashes={f:hashlib.sha256((r/f).read_bytes()).hexdigest() for f in files},initScriptSha256={f:hashlib.sha256(Path(f).read_bytes()).hexdigest() for f in inits},workingTreeAtStart=git('status','--short') or 'CLEAN',scope='246 missing methods plus2interrupted skips from stopped broad run; separate selected host scope, NOT a complete suite',workerPolicy='2forks/6GiB/forkEvery4;12GiB total heap budget;GC logs retained',broadOriginalSourceCommit='8bff21a3d5c4104709cde6e203d65ef7df3a935c',exclusions={'VisualReviewCaptureTest':2,'Units35ChunkCaptureTest':2,'Units610ChunkCaptureTest':10,'Units1115GapCaptureTest':2},physicalAcceptance='OUTSTANDING_LAST',apkAssemblySigning='NOT_RUN')
(o/'CONTEXT.json').write_text(json.dumps(c,indent=2)+'\n')
(o/'run.sh').write_text('source /workspace/setup/android-env.sh\n'+shlex.join(cmd)+' > '+shlex.quote(str(o/'RUN.log'))+' 2>&1\nresult=$?\nprintf "%s\\n" "$result" > '+str(o/'RUN.exit')+'\nexit "$result"\n')
print(c['sourceCommit'],c['expectedMethods'])
