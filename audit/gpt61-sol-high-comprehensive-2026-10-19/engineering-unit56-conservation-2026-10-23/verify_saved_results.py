#!/usr/bin/env python3
"""Read-only saved scope verification; does not launch tests or a broad runtime."""
from pathlib import Path
import hashlib,json,zipfile,subprocess,xml.etree.ElementTree as ET
D=Path(__file__).resolve().parent;R=Path(subprocess.check_output(['git','rev-parse','--show-toplevel'],cwd=D,text=True).strip())
sha=lambda b:hashlib.sha256(b).hexdigest();read=lambda n:json.loads((D/n).read_text())
for name,expected in read('SHA256_CURRENT.json').items():assert sha((D/name).read_bytes())==expected,name
for name,expected in read('SHA256_BASELINE.json').items():assert sha((D/name).read_bytes())==expected,name
specs=[('BASELINE','RAW_BASELINE.zip','RAW_BASELINE_INDEX.json',41,3,dict(reported=41,passed=38,failed=3,errors=0,skipped=0),1),('CORRECTED','RAW_CORRECTED.zip','RAW_CORRECTED_INDEX.json',41,3,dict(reported=41,passed=41,failed=0,errors=0,skipped=0),0),('BROAD','RAW_BROAD_DISCOVERY.zip','RAW_BROAD_DISCOVERY_INDEX.json',1363,229,None,None)]
for label,archive,index,count,classes,expected_counts,exitcode in specs:
 with zipfile.ZipFile(D/archive) as z:
  assert z.testzip() is None;rows=read(index);assert sorted(z.namelist())==sorted(r['path'] for r in rows)
  for row in rows:
   data=z.read(row['path']);assert len(data)==row['bytes'] and sha(data)==row['sha256']
  for mode in ['DISCOVERY'] if label=='BROAD' else ['DISCOVERY','REGRESSION']:
   dry=mode=='DISCOVERY';result=read(label+'/'+mode+'/RESULT.json');ctx=read(label+'/'+mode+'/CONTEXT.json')
   assert result==json.loads(z.read(mode+'/RESULT.json')) and ctx==json.loads(z.read(mode+'/CONTEXT.json'))
   assert result['testCounts']==(dict(reported=count,passed=0,failed=0,errors=0,skipped=count) if dry else expected_counts)
   assert result['classes']==classes and result['exitCode']==(0 if dry else exitcode)
   for name,expected in json.loads(z.read(mode+'/SHA256.json')).items():assert sha(z.read(mode+'/'+name))==expected
   names=[];outcomes=[]
   for name in z.namelist():
    if name.startswith(mode+'/XML/') and name.endswith('.xml'):
     for t in ET.fromstring(z.read(name)).findall('testcase'):
      names.append((t.get('classname'),t.get('name')));outcomes.append('FAILURE' if t.find('failure') is not None or t.find('error') is not None else 'SKIPPED' if t.find('skipped') is not None else 'SUCCESS')
   events=[json.loads(x) for x in z.read(mode+'/EVENTS.jsonl').decode().splitlines()];events=[x for x in events if x['kind']=='RESULT']
   assert len(names)==len(set(names))==count and sorted(names)==sorted((x['className'],x['name']) for x in events)
   by={(x['className'],x['name']):x['resultType'] for x in events};assert all(by[n]==o for n,o in zip(names,outcomes))
   for name,expected in ctx['initScriptSha256'].items():
    basename=Path(name).name;data=(D/label/basename).read_bytes() if basename=='resources.gradle' else z.read('INIT/'+basename);assert sha(data)==expected
   assert ctx['exclusions']==dict(VisualReviewCaptureTest=2,Units35ChunkCaptureTest=2,Units610ChunkCaptureTest=10,Units1115GapCaptureTest=2)
   if not dry:
    assert result['discoveryComparison']['sameInputTrees'] and result['discoveryComparison']['exactSameMethodSet']
    issues=ET.fromstring(z.read(mode+'/LINT.xml')).findall('issue');assert {s:sum(x.get('severity')==s for x in issues) for s in ['Fatal','Error','Warning','Hint']}==dict(Fatal=0,Error=0,Warning=20,Hint=1)
    assert result['tasks']['verifyReleaseGateAttachment']=='> Task :app:verifyReleaseGateAttachment'
base,correct=read('BASELINE/REGRESSION/RESULT.json'),read('CORRECTED/REGRESSION/RESULT.json');assert {(x['testClass'],x['method']) for x in base['tests']}=={(x['testClass'],x['method']) for x in correct['tests']}
assert len(read('BASELINE_FAILURES.json')['tests'])==3 and all(x['corrected']=='PASSED' for x in read('THREE_FAILURE_RECHECK.json'))
assert correct['inputTrees']==read('BROAD/DISCOVERY/RESULT.json')['inputTrees']
for row in read('ORIGINAL_ASSERTION_CONSERVATION.json')['tests']:
 before=subprocess.check_output(['git','show','929942950013701ddee7771a67523726d03f3fa7:'+row['path']],cwd=R)
 after=subprocess.check_output(['git','show',correct['sourceCommit']+':'+row['path']],cwd=R)
 assert sha(before)==row['beforeSHA256'] and sha(after)==row['afterSHA256']
 if row['wholeFileUnchanged']:assert before==after
 else:
  lines=iter(after.decode().splitlines())
  for line in before.decode().splitlines():assert any(line.strip()==s.strip() for s in lines)
for name,expected in read('UNCHANGED_CURRENT_GENERATED_RECORDS.json').items():assert sha((R/name).read_bytes())==expected
assert len(read('UNCHANGED_CURRENT_GENERATED_RECORDS.json'))==155
for name,expected in read('PRESERVED_PRECEDING_EVIDENCE.json').items():assert sha((R/name).read_bytes())==expected
for name,expected in read('ORIGINAL_ARCHIVE_CONSERVATION.json').items():
 assert sha((R/name).read_bytes())==expected
 with zipfile.ZipFile(R/name) as z:assert z.testzip() is None
identity=read('READ_ONLY_SOURCE_IDENTITY.json');assert identity['canonicalSourceDigest']==identity['candidateSourceDigest']==json.loads((R/'release/acceptance/candidate.json').read_text())['sourceDigest']
assert read('BROAD_RUNTIME_PREPARATION.json')['state']=='PREPARED_NOT_LAUNCHED'
comparison=read('BROAD_METHOD_SET_COMPARISON.json');assert len(comparison['newIdentities'])==29 and comparison['removedIdentities']==[] and comparison['newRuntimePasses']==0
print(json.dumps(dict(baseline=dict(reported=41,passed=38,failed=3),corrected=dict(reported=41,passed=41,failed=0),broadDiscovery=dict(methods=1363,classes=229,syntheticSkips=1363,runtimePasses=0),scopesNotSummed=True,generatedRecordsUnchanged=155,newRuntimeTestsExecutedByVerifier=0)))
