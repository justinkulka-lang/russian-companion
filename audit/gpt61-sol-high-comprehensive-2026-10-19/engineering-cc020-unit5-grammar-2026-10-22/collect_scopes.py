from pathlib import Path
import json,hashlib,csv,zipfile,shutil,subprocess,xml.etree.ElementTree as ET
R=Path('/workspace/russian-companion');W=Path(__file__).parent;D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-cc020-unit5-grammar-2026-10-22';sha=lambda b:hashlib.sha256(b).hexdigest();git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip();save=lambda n,v:(D/n).write_text(json.dumps(v,indent=2)+'\n')
assert (W/'INITIAL_FINAL/RUN.exit').is_file()
for mode,expected in [('BASELINE',dict(reported=4,passed=1,failed=3,errors=0,skipped=0)),('CONFIRMED_BASELINE',dict(reported=4,passed=1,failed=3,errors=0,skipped=0)),('INITIAL_FINAL',dict(reported=13,passed=13,failed=0,errors=0,skipped=0))]:
 O=W/mode;out=D/mode;out.mkdir()
 if mode=='INITIAL_FINAL':
  (O/'XML').mkdir()
  for p in (R/'app/build/test-results/testDebugUnitTest').glob('TEST-*.xml'):shutil.copyfile(p,O/'XML'/p.name)
  shutil.copyfile(R/'app/build/reports/lint-results-debug.xml',O/'LINT.xml')
 source=git('rev-parse','2fb455d4') if mode=='INITIAL_FINAL' else json.loads((O/'SOURCE.json').read_text())['sourceCommit']
 inits=['/workspace/setup/cloud-validation.gradle','audit/independent-gpt61-2026-10-12/broader-regression-2026-10-18/broader-tests.gradle','/workspace/artifacts/ui-source-binding-reconciliation-2026-10-22/resources.gradle']+([str(W/'capture.gradle')] if mode=='BASELINE' else [])
 args=[]
 for script in inits:args+=['-I',script]
 args+=['-Daudit.events.path='+str(O/'EVENTS.jsonl'),'-Daudit.gc.dir='+str(O)]
 if mode=='BASELINE':args+=['-Daudit.unit5.legacy.capture='+str(W/'BASELINE_LEGACY_UNIT5.zip')]
 args+=['testDebugUnitTest','--rerun','--tests','dev.russiancompanion.v2.Unit5GrammarReviewTest']
 if mode=='INITIAL_FINAL':args+=['--tests','dev.russiancompanion.v2.Unit5GrammarReviewUiTest','--tests','dev.russiancompanion.v2.Unit5CumulativeReviewTest','compileDebugSources','lintDebug','verifyReleaseGateAttachment','--continue']
 args+=['--offline','--max-workers=2','--console=plain']
 context=dict(sourceCommit=source,inputTrees={p:git('rev-parse',source+':'+p) for p in ['app/src/main','app/src/test','catalog','docs','review','release']},cwd=str(R),activation='source /workspace/setup/android-env.sh',command=['./gradlew',*args],initScriptSha256={s:sha((R/s if not s.startswith('/') else Path(s)).read_bytes()) for s in inits},timingQualification='Raw XML timestamps and START/RESULT event milliseconds retained; exact shell launch UTC was not separately recorded.',exclusions={'VisualReviewCaptureTest':2,'Units35ChunkCaptureTest':2,'Units610ChunkCaptureTest':10,'Units1115GapCaptureTest':2},scope='Explicit selected classes; Unit5CumulativeReviewTest final seven methods use exact pre-change pack b869da02 with unchanged assertions',completeSuite=False,physicalAcceptance='OUTSTANDING_LAST')
 rows=[];suite_records=[]
 for p in sorted((O/'XML').glob('TEST-*.xml')):
  suite=ET.parse(p).getroot();suite_records.append(suite.attrib)
  for t in suite.findall('testcase'):
   elem=t.find('failure');error=t.find('error');skip=t.find('skipped');outcome='FAILED' if elem is not None else 'ERROR' if error is not None else 'SKIPPED' if skip is not None else 'PASSED';e=elem if elem is not None else error if error is not None else skip
   rows.append(dict(testClass=t.get('classname'),method=t.get('name'),outcome=outcome,seconds=t.get('time'),message=e.get('message','') if e is not None else ''))
 counts={k:sum(r['outcome']==v for r in rows) for k,v in [('passed','PASSED'),('failed','FAILED'),('errors','ERROR'),('skipped','SKIPPED')]};counts['reported']=len(rows);assert counts==expected,(mode,counts)
 events=[json.loads(x) for x in (O/'EVENTS.jsonl').read_text().splitlines()];done=[e for e in events if e['kind']=='RESULT'];ev={(e['className'],e['name']):e['resultType'] for e in done};rm={(r['testClass'],r['method']):{'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[r['outcome']] for r in rows};assert len(rows)==len(rm)==len(done) and rm==ev
 exitcode=int((O/'RUN.exit').read_text());assert exitcode==(0 if mode=='INITIAL_FINAL' else 1)
 log=(O/'RUN.log').read_text();tasks={}
 for n in ['compileDebugKotlin','compileDebugUnitTestKotlin','compileDebugSources','lintAnalyzeDebug','lintAnalyzeDebugUnitTest','lintDebug','verifyReleaseGateAttachment']:
  prefix='> Task :app:'+n;tasks[n]=next((line for line in log.splitlines() if line==prefix or line.startswith(prefix+' ')),'NOT_REQUESTED_OR_NOT_REACHED')
 lint=None
 if mode=='INITIAL_FINAL':
  issues=ET.parse(O/'LINT.xml').getroot().findall('issue');lint={s:sum(e.get('severity')==s for e in issues) for s in ['Fatal','Error','Warning','Hint']};assert lint['Fatal']==lint['Error']==0
 (out/'CONTEXT.json').write_text(json.dumps(context,indent=2)+'\n');(out/'RESULT.json').write_text(json.dumps(dict(sourceCommit=source,testCounts=counts,tests=rows,suites=suite_records,exitCode=exitcode,tasks=tasks,lint=lint,xmlEventsExact=True,completeSuite=False),indent=2)+'\n')
 with (out/'RESULTS.csv').open('w',newline='') as f:
  w=csv.DictWriter(f,fieldnames=list(rows[0]),lineterminator='\n');w.writeheader();w.writerows(rows)
 files={p.relative_to(O).as_posix():p for p in O.rglob('*') if p.is_file()}
 with zipfile.ZipFile(D/('RAW_'+mode+'.zip'),'w',zipfile.ZIP_DEFLATED) as z:
  for n,p in files.items():z.write(p,n)
 save('RAW_'+mode+'_INDEX.json',{n:dict(sha256=sha(p.read_bytes()),bytes=p.stat().st_size) for n,p in files.items()})
save('INITIAL_EXECUTION_SCOPES.json',dict(initialHarness='BASELINE:three new-policy ID failures and one successful legacy capture/replay control; wrong candidate step spelling was not reached',correctedBaseline='CONFIRMED_BASELINE:two policy-ID failures, one actual advancement-without-gender failure and one legacy replay pass',correctedInitialScope='INITIAL_FINAL:all13passed, comprising4unchanged backend regressions/2new version-aware UI controls/7existing historical-obligation methods',scopesSummed=False,legacyCapture='Additional genuine partial Unit5 backup, distinct from and not a replacement for the original64 histories',independentFindingClosed=False))
print('Retained both independent baseline scopes and successful13method initial correction, with exact raw artifacts.')
