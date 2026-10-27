"""Read-only saved Unit13 core verification; no Gradle/JUnit execution."""
from pathlib import Path
from datetime import datetime,timezone
from collections import Counter
import argparse,json,hashlib,subprocess,zipfile,xml.etree.ElementTree as ET,re
D=Path(__file__).resolve().parent;R=Path('/workspace/russian-companion');parser=argparse.ArgumentParser();parser.add_argument('--current',action='store_true');current=parser.parse_args().current
sha=lambda b:hashlib.sha256(b).hexdigest();read=lambda n:json.loads((D/n).read_text());git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip();blob=lambda s,n:subprocess.check_output(['git','show',s+':'+n],cwd=R)
m=read('SHA256.json');assert set(m)=={p.relative_to(D).as_posix() for p in D.rglob('*') if p.is_file() and p.name!='SHA256.json'}
for n,h in m.items():assert sha((D/n).read_bytes())==h,n
prep=read('PREPARATION.json');con=read('CONSERVATION.json');r=read('REGRESSION_RESULT.json');S=r['sourceCommit'];assert con['sourceCommit']==S
for n,h in prep['existingTestFileHashes'].items():assert sha(blob(S,n))==h,n
for n,h in prep['newTestFiles'].items():assert sha(blob(S,n))==h,n
assert len(prep['existingTestFileHashes'])==272
for n,h in con['inputTrees'].items():assert git('rev-parse',S+':'+n)==h,n
for n,h in prep['unchangedTrees'].items():assert con['inputTrees'][n]==h,n
raw='app/src/main/assets/content/v2-curriculum.json';assert blob(prep['baselineParent'],raw).replace(b'unit13-prior-reading-aspect-past-gender-v2',b'unit13-prior-present-aspect-past-gender-v3')==blob(S,raw)
kernel=blob(S,'app/src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt').decode();objects={m[1]:sha(m[0].encode()) for m in re.findall(r'(internal object (\w+CumulativeReviewPolicy) \{.*?\n\})',kernel,re.S)}
assert len(objects)==40
for n,h in read('OLD_POLICY_OBJECT_HASHES.json').items():assert objects[n]==h,n
records=read('SOURCE_BINDING_HASHES.json');assert len(records)==155
for n,h in records.items():assert sha(blob(S,n))==h,n
archives=read('PRIOR_ARCHIVE_HASHES.json');assert len(archives)==74
for n,h in archives.items():
 assert sha((R/n).read_bytes())==h,n
 with zipfile.ZipFile(R/n) as z:assert z.testzip() is None,n
with zipfile.ZipFile(D/'VERIFIED_TEST_SOURCES.zip') as z:
 assert z.testzip() is None
 for n in z.namelist():assert z.read(n)==blob(S,n),n
with zipfile.ZipFile(D/'RAW_CORE.zip') as z:
 assert z.testzip() is None;idx=read('RAW_CORE_INDEX.json');assert set(z.namelist())=={x['path'] for x in idx}
 for x in idx:assert len(z.read(x['path']))==x['bytes'] and sha(z.read(x['path']))==x['sha256'],x['path']
 phases={}
 for mode in ['DISCOVERY','REGRESSION']:
  c=read(mode+'_CONTEXT.json');s=read(mode+'_RESULT.json');assert c==json.loads(z.read(mode+'/CONTEXT.json')) and s==json.loads(z.read(mode+'/RESULT.json'))
  assert c['sourceCommit']==s['sourceCommit']==S and c['sourceRootTree']==git('rev-parse',S+'^{tree}') and c['workingTreeAtStart']=='CLEAN'
  assert c['inputTrees']==s['inputTrees']==con['inputTrees']
  assert sha(z.read('TOOLING/run.py'))==c['runnerSha256']
  for n,h in c['initScriptSha256'].items():assert sha(z.read('INIT/'+Path(n).name))==h
  rows={};failureHashes={};start=datetime.fromisoformat(c['startedUtc']);xmlnames=[n for n in z.namelist() if n.startswith(mode+'/XML/') and n.endswith('.xml')];assert len(xmlnames)==3
  for n in xmlnames:
   suite=ET.fromstring(z.read(n));stamp=datetime.fromisoformat(suite.get('timestamp').replace('Z','+00:00'));stamp=stamp.replace(tzinfo=timezone.utc) if stamp.tzinfo is None else stamp;assert stamp.timestamp()>=start.timestamp()-1
   for t in suite.findall('testcase'):
    key=(t.get('classname'),t.get('name'));assert key not in rows
    status='FAILED' if t.find('failure') is not None else 'ERROR' if t.find('error') is not None else 'SKIPPED' if t.find('skipped') is not None else 'PASSED';rows[key]=status
    e=next((t.find(k) for k in ['failure','error','skipped'] if t.find(k) is not None),None);msg=e.get('message','') if e is not None else '';failureHashes[key]=sha(msg.encode())
  assert len(rows)==17 and rows=={(t['testClass'],t['method']):t['outcome'] for t in s['tests']}
  for t in s['tests']:assert failureHashes[t['testClass'],t['method']]==t['fullMessageSha256']
  expected={(cls,method) for cls,methods in prep['methodsInventory'].items() for method in methods};assert set(rows)==expected
  events=[json.loads(x) for x in z.read(mode+'/EVENTS.jsonl').splitlines()];events=[x for x in events if x['kind']=='RESULT'];assert len(events)==17 and {(x['className'],x['name']):x['resultType'] for x in events}=={k:{'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[v] for k,v in rows.items()}
  n=Counter(rows.values());assert s['testCounts']==dict(reported=17,passed=n['PASSED'],failed=n['FAILED'],errors=n['ERROR'],skipped=n['SKIPPED'])
  assert int(z.read(mode+'/RUN.exit'))==s['exitCode']==json.loads(z.read(mode+'_COMPLETION.json'))['runnerExit']
  for n,h in read(mode+'_SHA256.json').items():assert sha(z.read(mode+'/'+n))==h,(mode,n)
  assert c['exclusions']==prep['exclusions'] and not s['completeSuite'];phases[mode]=rows
 assert set(phases['DISCOVERY'])==set(phases['REGRESSION']) and set(phases['DISCOVERY'].values())=={'SKIPPED'}
 if 'REGRESSION/LINT.xml' in z.namelist():
  issues=ET.fromstring(z.read('REGRESSION/LINT.xml')).findall('issue');assert r['lintIssuesBySeverity']=={s:sum(x.get('severity')==s for x in issues) for s in ['Fatal','Error','Warning','Hint']}
 if (D/'CURRENT_POLICY_OBSERVATION.json').exists():
  o=read('CURRENT_POLICY_OBSERVATION.json');raw=json.loads(z.read('REGRESSION/CURRENT_POLICY_OBSERVATION.json'));assert all(o[k]==v for k,v in raw.items()) and o['sourceCommit']==S
  assert (o['activePolicies'],o['pools'],o['placements'],o['uniqueCandidates'],o['printSingletonPolicies'])==(28,59,112,52,19)
  assert re.fullmatch('[0-9a-f]{64}',o['canonicalSourceDigest'])
  printed=[]
  for name in z.namelist():
   if name.startswith('REGRESSION/XML/') and name.endswith('.xml'):
    output=ET.fromstring(z.read(name)).findtext('system-out','')
    for line in output.splitlines():
     if 'UNIT13_COMPILED_OBSERVATION ' in line:printed.append(json.loads(line.split('UNIT13_COMPILED_OBSERVATION ',1)[1]))
  assert printed==[raw]
identity=read('OFFICIAL_SOURCE_DIGEST.json');compiled=read('COMPILED_CURRICULUM_INDEX.json')
assert identity['sourceCommit']==compiled['sourceCommit']==S and identity['inputTrees']==compiled['inputTrees']==con['inputTrees']
assert identity['canonicalSourceDigest']==read('CURRENT_POLICY_OBSERVATION.json')['canonicalSourceDigest']
assert identity['observationSha256']==sha((D/'CURRENT_POLICY_OBSERVATION.json').read_bytes())
assert identity['compiledArchiveSha256']==sha((D/'COMPILED_CURRICULUM.zip').read_bytes())
with zipfile.ZipFile(D/'COMPILED_CURRICULUM.zip') as z:
 assert z.testzip() is None and set(z.namelist())==set(compiled['classes']) and len(z.namelist())==identity['compiledClasses']
 for n,v in compiled['classes'].items():assert len(z.read(n))==v['bytes'] and sha(z.read(n))==v['sha256'],n
assert r['discoveryComparison']['exactSameMethodSet'] and r['discoveryComparison']['sameInputTrees']
assert read('ACTUAL_FAILURES.json')['tests']==[x for x in r['tests'] if x['outcome'] in ['FAILED','ERROR']]
if current:
 for n,h in read('PROTECTED_EVIDENCE_HASHES.json').items():assert sha((R/n).read_bytes())==h,n
 for n,h in con['inputTrees'].items():assert git('rev-parse','HEAD:'+n)==h,n
assert con['sourceBindingsStatus']=='STALE_UNCHANGED_NOT_REGENERATED' and con['all48Findings']=='OPEN' and con['audit']=='INCOMPLETE' and con['physicalAcceptance']=='OUTSTANDING_LAST'
print(json.dumps(dict(verified=True,testedSource=S,currentConservation=current,counts=r['testCounts'],classes=3,exitCode=r['exitCode'],oldPolicyObjectsByteExact=39,all272PreexistingTestsConserved=True,baselineWholeFileUnchanged=True,archivesShaCrc=74,currentRecords='155_STALE_UNCHANGED',newRuntimeMethodsByVerifier=0,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',completeSuite=False)))
