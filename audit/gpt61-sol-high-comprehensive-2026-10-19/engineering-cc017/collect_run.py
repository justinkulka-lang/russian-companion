from pathlib import Path
import json,shutil,subprocess,re,sys,xml.etree.ElementTree as E
from datetime import datetime,timezone
r=Path('/workspace/russian-companion');o=Path('/workspace/artifacts/cc017-reference-search')/sys.argv[1]
assert (o/'RUN.exit').exists() and not (o/'XML').exists()
(o/'XML').mkdir()
for p in (r/'app/build/test-results/testDebugUnitTest').glob('TEST-*.xml'):shutil.copy2(p,o/'XML'/p.name)
shutil.copy2(r/'app/build/reports/lint-results-debug.xml',o/'LINT.xml')
c=json.loads((o/'CONTEXT.json').read_text());expected=set()
for selector in c['selectors']:
 path='app/src/test/java/'+selector.replace('.','/')+'.kt'
 if subprocess.run(['git','cat-file','-e',c['sourceCommit']+':'+path],capture_output=True).returncode==0:
  code=subprocess.check_output(['git','show',c['sourceCommit']+':'+path],text=True);expected|={(selector,m) for m in re.findall(r'@Test\s+fun\s+(\w+)',code)}
 else:expected.add(tuple(selector.rsplit('.',1)))
rows=[]
for xml in sorted((o/'XML').glob('TEST-*.xml')):
 suite=E.parse(xml).getroot();when=datetime.fromisoformat(suite.get('timestamp').replace('Z','+00:00'))
 if when.tzinfo is None:when=when.replace(tzinfo=timezone.utc)
 assert when.timestamp()>=(o/'run.sh').stat().st_mtime-1,(xml,when)
 for t in suite.findall('testcase'):
  status='FAILED' if t.find('failure') is not None else 'ERROR' if t.find('error') is not None else 'SKIPPED' if t.find('skipped') is not None else 'PASSED'
  rows.append(dict(testClass=t.get('classname'),method=t.get('name'),outcome=status,seconds=t.get('time')))
events=[json.loads(x) for x in (o/'EVENTS.jsonl').read_text().splitlines()];events=[x for x in events if x['kind']=='RESULT']
assert {(x['testClass'],x['method']) for x in rows}==expected
assert sorted((x['testClass'],x['method']) for x in rows)==sorted((x['className'],x['name']) for x in events)
assert len(rows)==len(expected)==c['expectedMethods']
by_event={(x['className'],x['name']):x['resultType'] for x in events}
for x in rows:
 assert by_event[(x['testClass'],x['method'])]=={'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[x['outcome']]

counts=dict(reported=len(rows),passed=sum(x['outcome']=='PASSED' for x in rows),failed=sum(x['outcome']=='FAILED' for x in rows),errors=sum(x['outcome']=='ERROR' for x in rows),skipped=sum(x['outcome']=='SKIPPED' for x in rows))
lint={}
for issue in E.parse(o/'LINT.xml').getroot().findall('issue'):lint[issue.get('severity')]=lint.get(issue.get('severity'),0)+1
log=(o/'RUN.log').read_text();tasks={}
for task in ['testDebugUnitTest','compileDebugKotlin','compileDebugUnitTestKotlin','compileDebugSources','lintAnalyzeDebug','lintAnalyzeDebugUnitTest','lintDebug','verifyReleaseGateAttachment']:
 prefix='> Task :app:'+task;tasks[task]=next((line for line in log.splitlines() if line==prefix or line.startswith(prefix+' ')),'NOT_REACHED')
result=dict(sourceCommit=c['sourceCommit'],exitCode=int((o/'RUN.exit').read_text()),testCounts=counts,classes=len({x['testClass'] for x in rows}),tests=rows,tasks=tasks,lintIssuesBySeverity=lint,selectedSourceInventoryMatchesFreshXmlAndEvents=True,xmlFreshRelativeToRunScript=True,completeSuite=False,scopesNotSummed=True)
(o/'RESULT.json').write_text(json.dumps(result,indent=2)+'\n');print(c['sourceCommit'],counts,lint,tasks)
