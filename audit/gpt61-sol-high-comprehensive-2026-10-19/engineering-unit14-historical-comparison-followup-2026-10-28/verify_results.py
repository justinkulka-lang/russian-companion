"""Read-only verification of separate fresh affected discovery and actual runtime archives."""
from evidence import *
from datetime import datetime,timezone
from collections import Counter
import xml.etree.ElementTree as ET
r=read(D/'REGRESSION_RESULT.json');scope=read(D/'SCOPE.json');prep=read(D/'PREPARATION.json');expected={(t['testClass'],t['method']) for t in scope['expectedMethods']}
sets={};xmlfailures={}
for mode,name in [('DISCOVERY','RAW_DISCOVERY'),('REGRESSION','RAW_AFFECTED')]:
    verify_archive(name)
    with zipfile.ZipFile(D/(name+'.zip')) as z:
        c=read(D/(mode+'_CONTEXT.json'));s=read(D/(mode+'_RESULT.json'))
        assert c==json.loads(z.read(mode+'/CONTEXT.json')) and s==json.loads(z.read(mode+'/RESULT.json'))
        assert c['sourceCommit']==s['sourceCommit'] and c['sourceRootTree']==git('rev-parse',c['sourceCommit']+'^{tree}')
        assert c['inputTrees']==s['inputTrees']==prep['inputTrees']
        for p,h in c['inputTrees'].items():assert git('rev-parse',c['sourceCommit']+':'+p)==h
        assert c['selectors']==scope['selectors'] and len(c['selectors'])==13 and c['command'].count('--tests')==13 and c['workingTreeAtStart']=='CLEAN'
        assert sha(z.read('TOOLING/run.py'))==c['runnerSha256']
        for p,h in c['initScriptSha256'].items():assert sha(z.read('INIT/'+Path(p).name))==h
        xmlnames=[n for n in z.namelist() if n.startswith(mode+'/XML/') and n.endswith('.xml')];assert len(xmlnames)==scope['classes']
        rows={};messages={};lengths={}
        for n in xmlnames:
            suite=ET.fromstring(z.read(n));stamp=datetime.fromisoformat(suite.get('timestamp').replace('Z','+00:00'));stamp=stamp.replace(tzinfo=timezone.utc) if stamp.tzinfo is None else stamp
            assert stamp.timestamp()>=datetime.fromisoformat(c['startedUtc']).timestamp()-1
            for t in suite.findall('testcase'):
                k=(t.get('classname'),t.get('name'));assert k not in rows
                rows[k]='FAILED' if t.find('failure') is not None else 'ERROR' if t.find('error') is not None else 'SKIPPED' if t.find('skipped') is not None else 'PASSED'
                e=next((t.find(a) for a in ['failure','error','skipped'] if t.find(a) is not None),None);data=(e.get('message','') if e is not None else '').encode();messages[k]=sha(data);lengths[k]=len(data)
                if mode=='REGRESSION':
                    for kind in ['failure','error']:
                        failure=t.find(kind)
                        if failure is not None:xmlfailures[(*k,kind)]=(failure.get('message',''),failure.text or '')
        assert set(rows)==expected and rows=={(t['testClass'],t['method']):t['outcome'] for t in s['tests']}
        for t in s['tests']:assert messages[t['testClass'],t['method']]==t['fullMessageSha256'] and lengths[t['testClass'],t['method']]==t['fullMessageBytes']
        events=[json.loads(x) for x in z.read(mode+'/EVENTS.jsonl').splitlines()];events=[e for e in events if e.get('kind')=='RESULT'];assert len(events)==scope['methodCount']
        assert {(e['className'],e['name']):e['resultType'] for e in events}=={k:{'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[v] for k,v in rows.items()}
        count=Counter(rows.values());assert s['testCounts']==dict(reported=scope['methodCount'],passed=count['PASSED'],failed=count['FAILED'],errors=count['ERROR'],skipped=count['SKIPPED'])
        assert s['exitCode']==int(z.read(mode+'/RUN.exit'))==json.loads(z.read(mode+'_COMPLETION.json'))['runnerExit']
        for n,h in read(D/(mode+'_SHA256.json')).items():assert sha(z.read(mode+'/'+n))==h,n
        assert c['exclusions']=={'VisualReviewCaptureTest':2,'Units35ChunkCaptureTest':2,'Units610ChunkCaptureTest':10,'Units1115GapCaptureTest':2}
        assert not any(k[0].split('.')[-1] in c['exclusions'] for k in rows)
        if mode=='REGRESSION':
            monitor=json.loads(z.read('MONITOR/DURABLE_MONITOR_STATUS.json'));assert monitor['completionExists'] and monitor['completedEvents']==scope['methodCount']
            issues=ET.fromstring(z.read('REGRESSION/LINT.xml')).findall('issue');assert s['lintIssuesBySeverity']=={v:sum(x.get('severity')==v for x in issues) for v in ['Fatal','Error','Warning','Hint']}
        sets[mode]=rows
assert set(sets['DISCOVERY'])==set(sets['REGRESSION']) and set(sets['DISCOVERY'].values())=={'SKIPPED'}
assert r['discoveryComparison']['sameInputTrees'] and r['discoveryComparison']['exactSameMethodSet']
assert {(t['testClass'],t['method']) for t in scope['priorFailedIdentities']}<=expected
assert len(scope['priorFailedIdentities'])==14
for label,key,n in [('UNIT13_CONTROLS_RECHECKED','currentUnit13ControlMethods',19),('PRIOR_FAILURES_RECHECKED','priorFailedIdentities',14)]:
    record=read(D/(label+'.json'));assert record['allIncluded'] and len(record['methods'])==n
    assert {(t['testClass'],t['method']) for t in record['methods']}=={(t['testClass'],t['method']) for t in scope[key]}
    for t in record['methods']:assert t['outcome']==sets['REGRESSION'][t['testClass'],t['method']]
    assert record['allPassed']==all(t['outcome']=='PASSED' for t in record['methods'])
assert {(t['testClass'],t['method']) for t in scope['currentUnit14ControlMethods']}<=expected
failureindex=read(D/'ACTUAL_FAILURES.json');storage=failureindex['storage']
assert storage['format']=='LOSSLESS_ZIP' and storage['archive']=='ACTUAL_FAILURES_FULL.zip'
assert storage['member']=='ACTUAL_FAILURES.json' and sha((D/storage['archive']).read_bytes())==storage['archiveSha256']
with zipfile.ZipFile(D/storage['archive']) as z:
    assert z.testzip() is None and z.namelist()==[storage['member']]
    raw=z.read(storage['member']);assert len(raw)==storage['memberBytes'] and sha(raw)==storage['memberSha256']
    fullfailures=json.loads(raw)
assert fullfailures['sourceCommit']==failureindex['sourceCommit']==r['sourceCommit']
assert fullfailures['count']==failureindex['count']==len(fullfailures['failures'])==len(xmlfailures)==r['testCounts']['failed']+r['testCounts']['errors']
assert fullfailures['originalAssertionsNotWaived'] and failureindex['originalAssertionsNotWaived']
assert {(f['testClass'],f['method'],f['kind']):(f['message'],f['trace']) for f in fullfailures['failures']}==xmlfailures
assert failureindex['failures']==[dict(testClass=f['testClass'],method=f['method'],kind=f['kind'],
    **{key+suffix:value for key in ['message','trace']
       for suffix,value in [('Bytes',len(f[key].encode())),('Sha256',sha(f[key].encode()))]}) for f in fullfailures['failures']]
controls=read(D/'UNIT14_CONTROLS_RECHECKED.json');assert controls['all22Included'] and len(controls['methods'])==22
for t in controls['methods']:assert t['outcome']==sets['REGRESSION'][t['testClass'],t['method']]
assert controls['allPassed']==all(t['outcome']=='PASSED' for t in controls['methods'])
current=conservation();assert read(D/'FINAL_CONSERVATION.json')==dict(sourceCommit=r['sourceCommit'],**current)
if (D/'SHA256.json').exists():
    for n,h in read(D/'SHA256.json').items():assert sha((D/n).read_bytes())==h,n
print(json.dumps(dict(verified=True,sourceCommit=r['sourceCommit'],counts=r['testCounts'],classes=13,exitCode=r['exitCode'],all14OriginalFailedIdentitiesIncluded=True,all19Unit13ControlsIncluded=True,all22Unit14ControlsIncluded=True,all22Unit14ControlsPassed=controls['allPassed'],testFilesByteExact=281,bindingsConserved=155,archivesShaCrc=77,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',completeSuite=False)))
