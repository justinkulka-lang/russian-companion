from evidence import *
import xml.etree.ElementTree as ET
from datetime import datetime,timezone
from collections import Counter
scope=read(D/'SCOPE.json');prep=read(D/'PREPARATION.json');expected={(r['testClass'],r['method'])for r in scope['expectedMethods']};outcomes={};failures={}
for mode,name in [('DISCOVERY','RAW_DISCOVERY'),('REGRESSION','RAW_FOLLOWUP')]:
    verify_archive(name)
    with zipfile.ZipFile(D/(name+'.zip'))as z:
        c=read(D/(mode+'_CONTEXT.json'));r=read(D/(mode+'_RESULT.json'))
        assert c==json.loads(z.read(mode+'/CONTEXT.json'))and r==json.loads(z.read(mode+'/RESULT.json'))
        assert c['inputTrees']==r['inputTrees']==prep['inputTrees']
        assert c['sourceCommit']==r['sourceCommit']and c['sourceRootTree']==git('rev-parse',c['sourceCommit']+'^{tree}')
        for p,h in c['inputTrees'].items():assert git('rev-parse',c['sourceCommit']+':'+p)==h
        assert c['selectors']==scope['selectors']and len(c['selectors'])==17 and c['command'].count('--tests')==17
        assert sha(z.read('TOOLING/run.py'))==c['runnerSha256']
        for p,h in c['initScriptSha256'].items():assert sha(z.read('INIT/'+Path(p).name))==h
        xmls=[n for n in z.namelist()if n.startswith(mode+'/XML/')and n.endswith('.xml')];assert len(xmls)==17
        rows={}
        for n in xmls:
            suite=ET.fromstring(z.read(n));stamp=datetime.fromisoformat(suite.get('timestamp').replace('Z','+00:00'));stamp=stamp.replace(tzinfo=timezone.utc)if stamp.tzinfo is None else stamp
            assert stamp.timestamp()>=datetime.fromisoformat(c['startedUtc']).timestamp()-1
            for t in suite.findall('testcase'):
                k=(t.get('classname'),t.get('name'));assert k not in rows
                rows[k]='FAILED'if t.find('failure')is not None else 'ERROR'if t.find('error')is not None else 'SKIPPED'if t.find('skipped')is not None else 'PASSED'
                if mode=='REGRESSION':
                    for kind in ['failure','error']:
                        f=t.find(kind)
                        if f is not None:failures[(*k,kind)]=(f.get('message',''),f.text or '')
        assert set(rows)==expected and rows=={(t['testClass'],t['method']):t['outcome']for t in r['tests']}
        events=[json.loads(x)for x in z.read(mode+'/EVENTS.jsonl').splitlines()];events=[e for e in events if e.get('kind')=='RESULT']
        assert len(events)==119 and {(e['className'],e['name']):e['resultType']for e in events}=={k:{'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[v]for k,v in rows.items()}
        count=Counter(rows.values());assert r['testCounts']==dict(reported=119,passed=count['PASSED'],failed=count['FAILED'],errors=count['ERROR'],skipped=count['SKIPPED'])
        assert r['exitCode']==int(z.read(mode+'/RUN.exit'))==json.loads(z.read(mode+'_COMPLETION.json'))['runnerExit']
        outcomes[mode]=rows
idx=read(D/'ACTUAL_FAILURES.json');storage=idx['storage'];assert sha((D/storage['archive']).read_bytes())==storage['archiveSha256']
with zipfile.ZipFile(D/storage['archive'])as z:
    assert z.testzip()is None;data=z.read(storage['member']);assert sha(data)==storage['memberSha256']and len(data)==storage['memberBytes'];full=json.loads(data)
assert {(f['testClass'],f['method'],f['kind']):(f['message'],f['trace'])for f in full['failures']}==failures
assert full['count']==idx['count']==len(failures)
assert read(D/'FINAL_CONSERVATION.json')==conservation()
for n,h in read(D/'SHA256.json').items():assert sha((D/n).read_bytes())==h,n
print(json.dumps({'verified':True,'sourceCommit':read(D/'REGRESSION_RESULT.json')['sourceCommit'],'counts':read(D/'REGRESSION_RESULT.json')['testCounts'],'classes':17,'all60FailedIdsIncluded':True,'all45SiblingsIncluded':True,'all14ControlsIncluded':True,'conservation':True,'all48Findings':'OPEN','audit':'INCOMPLETE','physicalAcceptance':'OUTSTANDING_LAST','completeSuite':False}))
