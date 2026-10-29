from pathlib import Path
import csv,json,hashlib,subprocess,shutil,zipfile,xml.etree.ElementTree as ET
from datetime import datetime,timezone
R=Path('/workspace/russian-companion');W=Path(__file__).parent;D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-v2-insets-accessibility-2026-10-22';D.mkdir(exist_ok=True)
git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
sha=lambda b:hashlib.sha256(b).hexdigest()
assert git('rev-parse','HEAD')==git('rev-parse','8e53775a')
assert (W/'FINAL/RUN.exit').is_file(),'Run not finished'
assert not git('status','--porcelain','--','app','catalog','docs','review','release')
for name in ['SOURCE_BINDING_DIAGNOSIS.json']:shutil.copyfile(W/name,D/name)
common=json.loads((W/'BASELINE/CONTEXT.json').read_text());contexts={}
for mode,commit,expected in [('BASELINE','e1871a59',7),('FINAL','8e53775a',11)]:
 O=W/mode; out=D/mode;out.mkdir(exist_ok=True)
 if mode=='FINAL':
  (O/'XML').mkdir(exist_ok=True)
  for p in (R/'app/build/test-results/testDebugUnitTest').glob('TEST-*.xml'):shutil.copyfile(p,O/'XML'/p.name)
  shutil.copyfile(R/'app/build/reports/lint-results-debug.xml',O/'LINT.xml')
 context=dict(common);context.pop('runLogCreatedEpoch',None);context.pop('startedUtcFromRunLogMtimeApproximate',None)
 context['sourceCommit']=git('rev-parse',commit);context['inputTrees']={n:git('rev-parse',commit+':'+n) for n in common['inputTrees']};context['expectedMethods']=expected
 context['workingTreeAtLaunch']='CLEAN';context['scope']='Seven unchanged new reproductions'+(' plus four unchanged option presentation UI controls' if mode=='FINAL' else '')+'; not complete suite'
 context['command']=[a.replace('/BASELINE/','/'+mode+'/').replace('/BASELINE','/'+mode) for a in common['command']]
 if mode=='FINAL':context['command'][context['command'].index('--offline'):context['command'].index('--offline')]=['--tests','dev.russiancompanion.v2.OptionPresentationUiTest','compileDebugSources','lintDebug','verifyReleaseGateAttachment','--continue']
 context['timingQualification']='Raw XML suite timestamps and START/RESULT event milliseconds retained. Exact shell launch timestamp was not separately recorded. Baseline original context runLogCreatedEpoch is file ctime observed after completion, not launch time.'
 contexts[mode]=context
 (out/'CONTEXT.json').write_text(json.dumps(context,indent=2)+'\n')
 rows=[];suites=[]
 for p in sorted((O/'XML').glob('TEST-*.xml')):
  suite=ET.parse(p).getroot();suites.append(suite.attrib)
  for t in suite.findall('testcase'):
   fail=t.find('failure');error=t.find('error');skip=t.find('skipped')
   status='FAILED' if fail is not None else 'ERROR' if error is not None else 'SKIPPED' if skip is not None else 'PASSED'
   elem=fail if fail is not None else error if error is not None else skip
   rows.append(dict(testClass=t.get('classname'),method=t.get('name'),outcome=status,seconds=t.get('time'),message=elem.get('message','') if elem is not None else ''))
 events=[json.loads(s) for s in (O/'EVENTS.jsonl').read_text().splitlines()];results=[s for s in events if s['kind']=='RESULT'];em={(s['className'],s['name']):s['resultType'] for s in results}
 ids={(s['testClass'],s['method']) for s in rows};assert len(rows)==len(ids)==len(results)==len(em)==expected
 assert ids==set(em)
 for s in rows:assert em[(s['testClass'],s['method'])]=={'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[s['outcome']]
 counts={n:sum(s['outcome']==v for s in rows) for n,v in [('passed','PASSED'),('failed','FAILED'),('errors','ERROR'),('skipped','SKIPPED')]};counts['reported']=len(rows)
 assert counts['failed']==(7 if mode=='BASELINE' else 0) and counts['passed']==(0 if mode=='BASELINE' else 11)
 assert all(s['testClass'].split('.')[-1] not in common['exclusions'] for s in rows)
 log=(O/'RUN.log').read_text();tasks={}
 for name in ['testDebugUnitTest','compileDebugKotlin','compileDebugUnitTestKotlin','compileDebugSources','lintAnalyzeDebug','lintAnalyzeDebugUnitTest','lintDebug','verifyReleaseGateAttachment']:
  prefix='> Task :app:'+name;tasks[name]=next((s for s in log.splitlines() if s==prefix or s.startswith(prefix+' ')),'NOT_REQUESTED_OR_NOT_REACHED')
 lint=None
 if mode=='FINAL':
  issues=ET.parse(O/'LINT.xml').getroot().findall('issue');lint={s:sum(i.get('severity')==s for i in issues) for s in ['Fatal','Error','Warning','Hint']}
  assert lint['Fatal']==lint['Error']==0
  for n in ['lintDebug','verifyReleaseGateAttachment']:assert tasks[n]=='> Task :app:'+n
 exitcode=int((O/'RUN.exit').read_text());assert exitcode==(1 if mode=='BASELINE' else 0)
 summary=dict(sourceCommit=context['sourceCommit'],inputTrees=context['inputTrees'],testCounts=counts,classes=len(suites),tests=rows,suites=suites,exitCode=exitcode,tasks=tasks,lint=lint,eventXmlIdentityAndOutcomesMatch=True,rawEventEarliestMillis=min(s['atMillis'] for s in events),rawEventLatestMillis=max(s['atMillis'] for s in events),completeSuite=False,independentAuditClosure=False,physicalAcceptance='OUTSTANDING_LAST')
 (out/'RESULT.json').write_text(json.dumps(summary,indent=2)+'\n')
 with (out/'RESULTS.csv').open('w',newline='') as f:
  writer=csv.DictWriter(f,fieldnames=list(rows[0]),lineterminator='\n');writer.writeheader();writer.writerows(rows)
 rawfiles=sorted(p for p in O.rglob('*') if p.is_file())
 index={p.relative_to(O).as_posix():dict(sha256=sha(p.read_bytes()),bytes=p.stat().st_size) for p in rawfiles}
 archive=D/('RAW_'+mode+'.zip')
 with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED) as z:
  for p in rawfiles:z.write(p,p.relative_to(O).as_posix())
 with zipfile.ZipFile(archive) as z:
  assert z.testzip() is None
  assert all(sha(z.read(n))==v['sha256'] for n,v in index.items())
 (D/('RAW_'+mode+'_INDEX.json')).write_text(json.dumps(index,indent=2)+'\n')
prior=json.loads((R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-source-binding-reconciliation-2026-10-22/CONSERVATION.json').read_text())
conservation={'original66ArchiveHashes':prior['original66ArchiveHashes'],'inputTreesUnchangedFromBaseline':{p:git('rev-parse','e1871a59:'+p)==git('rev-parse','8e53775a:'+p) for p in ['app/src/test','app/src/main/assets','catalog','docs','review','release']},'allOriginalTestFilesUnchanged':True,'newRegressionFileUnchangedAcrossBaselineAndFinal':True,'oldUnit7RestoreCause':'UNCONFIRMED','all48IndependentFindings':'NOT_CLOSED','physicalAcceptance':'OUTSTANDING_LAST'}
assert all(conservation['inputTreesUnchangedFromBaseline'].values())
assert not git('diff','19d9585e','8e53775a','--','app/src/test',':(exclude)app/src/test/java/dev/russiancompanion/v2/V2InsetsAccessibilityTest.kt')
for name,h in conservation['original66ArchiveHashes'].items():
 assert sha((R/name).read_bytes())==h
 with zipfile.ZipFile(R/name) as z:assert z.testzip() is None
(D/'CONSERVATION.json').write_text(json.dumps(conservation,indent=2)+'\n')
(D/'SOURCE_CHANGE.patch').write_bytes(subprocess.check_output(['git','diff','e1871a59','8e53775a','--','app/src/main'],cwd=R))
print(json.dumps({'baseline':json.loads((D/'BASELINE/RESULT.json').read_text())['testCounts'],'final':json.loads((D/'FINAL/RESULT.json').read_text())['testCounts'],'evidenceDirectory':str(D)}))
