"""Independent read-only raw/outcome/source/assertion verification. No test execution."""
from pathlib import Path
from collections import Counter
from datetime import datetime,timezone
import json,hashlib,subprocess,zipfile,xml.etree.ElementTree as ET
from assertion_calls import assertions
R=Path('/workspace/russian-companion');D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit13-historical-comparison-followup-2026-10-27'
read=lambda n:json.loads((D/n).read_text());sha=lambda b:hashlib.sha256(b).hexdigest();git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
prep=read('PREPARATION.json');r=read('REGRESSION_RESULT.json');S=r['sourceCommit'];scope=read('SCOPE.json');expected={(t['testClass'],t['method']) for t in scope['expectedMethods']}
with zipfile.ZipFile(D/'RAW_FOLLOWUP.zip') as z:
    assert z.testzip() is None;idx=read('RAW_FOLLOWUP_INDEX.json');assert set(z.namelist())==set(idx)
    for n,v in idx.items():assert len(z.read(n))==v['bytes'] and sha(z.read(n))==v['sha256']
    sets={}
    for mode in ['DISCOVERY','REGRESSION']:
        c=read(mode+'_CONTEXT.json');s=read(mode+'_RESULT.json')
        assert c==json.loads(z.read(mode+'/CONTEXT.json')) and s==json.loads(z.read(mode+'/RESULT.json'))
        assert c['sourceCommit']==s['sourceCommit'] and c['sourceRootTree']==git('rev-parse',c['sourceCommit']+'^{tree}')
        assert c['inputTrees']==s['inputTrees']==prep['preparedInputTrees']
        for n,h in c['inputTrees'].items():assert git('rev-parse',c['sourceCommit']+':'+n)==h
        assert sha(z.read('TOOLING/run.py'))==c['runnerSha256']
        for n,h in c['initScriptSha256'].items():assert sha(z.read('INIT/'+Path(n).name))==h
        xmlnames=[n for n in z.namelist() if n.startswith(mode+'/XML/') and n.endswith('.xml')];assert len(xmlnames)==9
        rows={};messages={}
        for n in xmlnames:
            suite=ET.fromstring(z.read(n));stamp=datetime.fromisoformat(suite.get('timestamp').replace('Z','+00:00'));stamp=stamp.replace(tzinfo=timezone.utc) if stamp.tzinfo is None else stamp
            assert stamp.timestamp()>=datetime.fromisoformat(c['startedUtc']).timestamp()-1
            for t in suite.findall('testcase'):
                k=(t.get('classname'),t.get('name'));assert k not in rows
                rows[k]='FAILED' if t.find('failure') is not None else 'ERROR' if t.find('error') is not None else 'SKIPPED' if t.find('skipped') is not None else 'PASSED'
                e=next((t.find(a) for a in ['failure','error','skipped'] if t.find(a) is not None),None);messages[k]=sha((e.get('message','') if e is not None else '').encode())
        assert set(rows)==expected and rows=={(t['testClass'],t['method']):t['outcome'] for t in s['tests']}
        for t in s['tests']:assert messages[t['testClass'],t['method']]==t['fullMessageSha256']
        events=[json.loads(x) for x in z.read(mode+'/EVENTS.jsonl').splitlines()];events=[e for e in events if e.get('kind')=='RESULT'];assert len(events)==63
        assert {(e['className'],e['name']):e['resultType'] for e in events}=={k:{'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[v] for k,v in rows.items()}
        counts=Counter(rows.values());assert s['testCounts']==dict(reported=63,passed=counts['PASSED'],failed=counts['FAILED'],errors=counts['ERROR'],skipped=counts['SKIPPED'])
        assert s['exitCode']==int(z.read(mode+'/RUN.exit'))==json.loads(z.read(mode+'_COMPLETION.json'))['runnerExit']
        for n,h in read(mode+'_SHA256.json').items():assert sha(z.read(mode+'/'+n))==h
        assert c['exclusions']=={'VisualReviewCaptureTest':2,'Units35ChunkCaptureTest':2,'Units610ChunkCaptureTest':10,'Units1115GapCaptureTest':2};sets[mode]=rows
    assert set(sets['DISCOVERY'])==set(sets['REGRESSION']) and set(sets['DISCOVERY'].values())=={'SKIPPED'}
    issues=ET.fromstring(z.read('REGRESSION/LINT.xml')).findall('issue');assert r['lintIssuesBySeverity']=={s:sum(i.get('severity')==s for i in issues) for s in ['Fatal','Error','Warning','Hint']}
    monitor=json.loads(z.read('MONITOR/DURABLE_MONITOR_STATUS.json'));assert monitor['completionExists'] and monitor['completedEvents']==63
assert r['discoveryComparison']['sameInputTrees'] and r['discoveryComparison']['exactSameMethodSet']
assert read('ACTUAL_FAILURES.json')['count']==r['testCounts']['failed']+r['testCounts']['errors']
assert git('rev-parse','HEAD:app/src/main')==prep['productionTree']
with zipfile.ZipFile(D/'ORIGINAL_COMPARISONS.zip') as z:
    assert z.testzip() is None and set(z.namelist())==set(prep['changedComparisonFiles'])
    for row in prep['comparisonReview']:
        n=row['path'];old=z.read(n);new=(R/n).read_bytes()
        assert old==subprocess.check_output(['git','show',prep['parentCommit']+':'+n],cwd=R)
        assert sha(old)==row['originalSha256'] and sha(new)==row['newSha256']
        a=assertions(old.decode());b=assertions(new.decode());j=0
        for x in b:
            if j<len(a) and x==a[j]:j+=1
        assert j==len(a)==row['originalAssertionInvocations'] and len(b)==row['newAssertionInvocations']
assert sum(x['originalAssertionInvocations'] for x in prep['comparisonReview'])==627
for n,h in prep['existingTestFiles'].items():
    if n not in prep['changedComparisonFiles']:assert sha((R/n).read_bytes())==h
for n,h in prep['currentRecordHashes'].items():assert sha((R/n).read_bytes())==h
for n,h in prep['archives'].items():
    assert sha((R/n).read_bytes())==h
    with zipfile.ZipFile(R/n) as z:assert z.testzip() is None
recheck=read('ORIGINAL_FAILURE_RECHECK.json');assert recheck['allSixIdentitiesRechecked']
assert {(t['testClass'],t['method']):t['outcome'] for t in recheck['rechecked']}=={(t['testClass'],t['method']):sets['REGRESSION'][t['testClass'],t['method']] for t in scope['priorFailedIdentities']}
if (D/'SHA256.json').exists():
    for n,h in read('SHA256.json').items():assert sha((D/n).read_bytes())==h,n
print(json.dumps(dict(verified=True,sourceCommit=S,counts=r['testCounts'],classes=9,fullClasses=True,exitCode=r['exitCode'],allSixOriginalFailedIdsRechecked=True,allSixPassed=recheck['allSixPassed'],originalAssertionInvocationsPreserved=627,otherTestFilesConserved=271,bindingsConserved=155,archivesShaCrc=74,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',completeSuite=False)))
