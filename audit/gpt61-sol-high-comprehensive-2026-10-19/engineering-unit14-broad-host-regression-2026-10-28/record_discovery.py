from evidence import *
import shutil,xml.etree.ElementTree as ET
r=read(W/'DISCOVERY/RESULT.json');scope=read(W/'SCOPE.json');expected={(t['testClass'],t['method']) for t in scope['expectedMethods']};actual={(t['testClass'],t['method']) for t in r['tests']}
assert r['testCounts']==dict(reported=1499,passed=0,failed=0,errors=0,skipped=1499) and r['classes']==261 and r['exitCode']==0
assert len(actual)==1499 and actual==expected and r['inputTrees']==scope['inputTrees']
prior=read(R/scope['priorBroadResultPath']);assert sha((R/scope['priorBroadResultPath']).read_bytes())==scope['priorBroadResultSha256']
before={(t['testClass'],t['method']) for t in prior['tests']};assert len(before)==1477 and before<=actual
assert actual-before=={(t['testClass'],t['method']) for t in scope['expectedAddedMethods']} and len(actual-before)==22
for key,count in [('currentFollowupMethods',79),('previousAffectedMethods',541),('priorFailedIdentities',24),('currentUnit13ControlMethods',19),('currentUnit14ControlMethods',22)]:
    assert len(scope[key])==count and {(t['testClass'],t['method']) for t in scope[key]}<=actual
c=read(W/'DISCOVERY/CONTEXT.json');assert c['selectors']==[] and '--tests' not in c['command'] and '--test-dry-run' in c['command']
assert c['exclusions']=={'VisualReviewCaptureTest':2,'Units35ChunkCaptureTest':2,'Units610ChunkCaptureTest':10,'Units1115GapCaptureTest':2}
assert not any(t['testClass'].split('.')[-1] in c['exclusions'] for t in r['tests'])
scope['predictionOnlyUntilFreshDiscovery']=False;scope['runtimeStatus']='NOT_RUN'
save(W/'SCOPE.json',scope);save(D/'SCOPE.json',scope)
for n in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copy2(W/'DISCOVERY'/n,D/('DISCOVERY_'+n))
archive_mode('DISCOVERY','RAW_DISCOVERY')
with zipfile.ZipFile(D/'RAW_DISCOVERY.zip') as z:
    assert sha(z.read('TOOLING/run.py'))==c['runnerSha256']
    for n,h in c['initScriptSha256'].items():assert sha(z.read('INIT/'+Path(n).name))==h
    rows=[]
    for n in z.namelist():
        if n.startswith('DISCOVERY/XML/') and n.endswith('.xml'):
            for t in ET.fromstring(z.read(n)).findall('testcase'):
                assert t.find('skipped') is not None and t.find('failure') is None and t.find('error') is None
                rows.append((t.get('classname'),t.get('name')))
    assert len(rows)==1499 and len(set(rows))==1499 and set(rows)==expected
    events=[json.loads(x) for x in z.read('DISCOVERY/EVENTS.jsonl').splitlines()];events=[e for e in events if e.get('kind')=='RESULT']
    assert len(events)==1499 and {(e['className'],e['name']) for e in events}==expected and {e['resultType'] for e in events}=={'SKIPPED'}
v=dict(sourceCommit=r['sourceCommit'],methods=1499,classes=261,syntheticSkips=1499,runtimePasses=0,all1477PriorBroadIdsRetained=True,addedUnit14Methods=22,removedMethods=[],all79FollowupIdsIncluded=True,all541AffectedIdsIncluded=True,all24PriorFailedIdsIncluded=True,all19Unit13ControlsIncluded=True,all22Unit14ControlsIncluded=True,noMethodOrClassFilters=True,inputTreesMatchPrepared=True,xmlAndEventsExact=True,runnerAndInitHashesVerified=True,archiveCrcAndMemberShaVerified=True,completeSuite=False)
save(D/'DISCOVERY_SCOPE_VERIFICATION.json',v);print(json.dumps(dict(verified=True,**v)))
