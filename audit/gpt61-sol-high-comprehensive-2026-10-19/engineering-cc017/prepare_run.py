from pathlib import Path
import subprocess,json,hashlib,shlex,sys,re
r=Path('/workspace/russian-companion');o=Path('/workspace/artifacts/cc017-reference-search')/sys.argv[1];o.mkdir()
git=lambda *a:subprocess.check_output(['git',*a],cwd=r,text=True).strip()
selectors=['dev.russiancompanion.v2.ReferenceDetailSearchTest','dev.russiancompanion.v2.ReferenceDetailSearchUiTest','dev.russiancompanion.v2.ReferenceNoticeCorrectionTest']
inits=['/workspace/setup/cloud-validation.gradle',str(r/'audit/independent-gpt61-2026-10-12/broader-regression-2026-10-18/broader-tests.gradle')]
cmd=['./gradlew']
for i in inits:cmd+=['-I',i]
cmd+=['-Daudit.events.path='+str(o/'EVENTS.jsonl'),'testDebugUnitTest']
for s in selectors:cmd+=['--tests',s]
cmd+=['--rerun','compileDebugSources','lintDebug','verifyReleaseGateAttachment','--continue','--offline','--max-workers=4','--console=plain']
files=[]; expected=0
for selector in selectors:
 file='app/src/test/java/'+selector.replace('.','/')+'.kt'
 if (r/file).exists(): expected+=len(re.findall(r'@Test\s+fun\s+(\w+)',(r/file).read_text()))
 else:
  file='app/src/test/java/'+selector.rsplit('.',1)[0].replace('.','/')+'.kt';assert (r/file).exists();expected+=1
 files.append(file)
c=dict(sourceCommit=git('rev-parse','HEAD'),sourceRootTree=git('rev-parse','HEAD^{tree}'),inputTrees={d:git('rev-parse','HEAD:'+d) for d in ['app/src/main','app/src/test','catalog','docs','review']},command=cmd,workingDirectory=str(r),activation='source /workspace/setup/android-env.sh',selectors=selectors,expectedMethods=expected,sourceTestHashes={f:hashlib.sha256((r/f).read_bytes()).hexdigest() for f in files},initScriptSha256={f:hashlib.sha256(Path(f).read_bytes()).hexdigest() for f in inits},workingTreeAtStart=git('status','--short') or 'CLEAN',scope='Explicit selected host tests; NOT complete suite',exclusions={'VisualReviewCaptureTest':2,'Units35ChunkCaptureTest':2,'Units610ChunkCaptureTest':10,'Units1115GapCaptureTest':2},physicalAcceptance='OUTSTANDING_LAST',apkAssemblySigning='NOT_RUN')
(o/'CONTEXT.json').write_text(json.dumps(c,indent=2)+'\n')
(o/'run.sh').write_text('source /workspace/setup/android-env.sh\n'+shlex.join(cmd)+' > '+shlex.quote(str(o/'RUN.log'))+' 2>&1\nresult=$?\nprintf "%s\\n" "$result" > '+str(o/'RUN.exit')+'\nexit "$result"\n')
print(c['sourceCommit'],c['expectedMethods'])
