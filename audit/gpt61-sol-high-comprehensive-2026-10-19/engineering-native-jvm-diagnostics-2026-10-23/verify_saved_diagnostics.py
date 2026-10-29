"""Read-only diagnostic evidence check. Does not execute tests or infer a native-crash fix."""
from pathlib import Path
from collections import Counter
from datetime import datetime,timezone
import json,hashlib,zipfile,xml.etree.ElementTree as ET,subprocess
D=Path(__file__).resolve().parent;R=next(p for p in D.parents if (p/'AGENTS.md').exists());sha=lambda b:hashlib.sha256(b).hexdigest()
for p,v in json.loads((D/'SHA256.json').read_text()).items():assert sha((D/p).read_bytes())==v,p
for p,v in json.loads((D/'PRESERVED_PRIOR_EVIDENCE.json').read_text()).items():assert sha((R/p).read_bytes())==v,p
archiveMap=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit56-conservation-2026-10-23/ORIGINAL_ARCHIVE_CONSERVATION.json'
for p,v in json.loads(archiveMap.read_text()).items():
 assert sha((R/p).read_bytes())==v
 with zipfile.ZipFile(R/p) as z:assert z.testzip() is None
results={};trees=None
for scope in ['ORACLE','ORACLE_BATCH','ORACLE_TARGETED_JIT','BROAD_TARGETED_JIT']:
 with zipfile.ZipFile(D/('RAW_'+scope+'.zip')) as z:
  assert z.testzip() is None;ix=json.loads((D/('RAW_'+scope+'_INDEX.json')).read_text());assert sorted(z.namelist())==sorted(x['path'] for x in ix)
  for x in ix:
   b=z.read(x['path']);assert len(b)==x['bytes'] and sha(b)==x['sha256'];assert not x['path'].lower().endswith('.pdf')
  modes=['DISCOVERY'] if scope=='BROAD_TARGETED_JIT' else ['DISCOVERY','REGRESSION']
  for mode in modes:
   O=D/scope/mode;c=json.loads((O/'CONTEXT.json').read_text());r=json.loads((O/'RESULT.json').read_text());assert r['sourceCommit']==c['sourceCommit']=='8811c19449a259e834657ece2ecfe156db0d5423' and r['exitCode']==0
   if trees is None:trees=r['inputTrees']
   assert r['inputTrees']==trees
   for p,v in json.loads((O/'SHA256.json').read_text()).items():assert sha(z.read(mode+'/'+p))==v
   assert sha((D/scope/'run.py').read_bytes())==c['runnerSha256']
   for p,v in c['initScriptSha256'].items():assert sha(z.read('INIT/'+Path(p).name))==v
   events=[json.loads(s) for s in z.read(mode+'/EVENTS.jsonl').decode().splitlines()];ev=[x for x in events if x['kind']=='RESULT'];em={(x['className'],x['name']):x['resultType'] for x in ev};assert len(ev)==len(em)
   xm={};start=datetime.fromisoformat(c['startedUtc'])
   for name in z.namelist():
    if not name.startswith(mode+'/XML/'):continue
    suite=ET.fromstring(z.read(name));stamp=datetime.fromisoformat(suite.get('timestamp').replace('Z','+00:00'))
    if stamp.tzinfo is None:stamp=stamp.replace(tzinfo=timezone.utc)
    assert stamp.timestamp()>=start.timestamp()-1
    for t in suite.findall('testcase'):
     key=(t.get('classname'),t.get('name'));assert key not in xm;xm[key]='FAILED' if t.find('failure') is not None else 'ERROR' if t.find('error') is not None else 'SKIPPED' if t.find('skipped') is not None else 'PASSED'
   rm={(x['testClass'],x['method']):x['outcome'] for x in r['tests']};assert rm==xm
   assert {k:{'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[v] for k,v in xm.items()}==em
   if mode=='DISCOVERY':assert r['testCounts']['passed']==0 and r['testCounts']['skipped']==r['testCounts']['reported']
   else:
    d=json.loads((D/scope/'DISCOVERY/RESULT.json').read_text());assert set(xm)=={(x['testClass'],x['method']) for x in d['tests']};assert r['testCounts']['passed']==r['testCounts']['reported'] and r['testCounts']['skipped']==r['testCounts']['failed']==r['testCounts']['errors']==0;results[scope]=r
assert len(results['ORACLE']['tests'])==7 and len(results['ORACLE_BATCH']['tests'])==len(results['ORACLE_TARGETED_JIT']['tests'])==29
names=lambda r:{(x['testClass'],x['method']) for x in r['tests']};assert names(results['ORACLE'])<=names(results['ORACLE_BATCH'])==names(results['ORACLE_TARGETED_JIT'])
summary=json.loads((D/'RUNTIME_SCOPES.json').read_text());assert summary['ORACLE_BATCH']['classOrder']==summary['ORACLE_TARGETED_JIT']['classOrder']
assert len(summary['ORACLE_BATCH']['testJvmPidLogs'])==len(summary['ORACLE_TARGETED_JIT']['testJvmPidLogs'])==1
with zipfile.ZipFile(D/'RAW_ORACLE_TARGETED_JIT.zip') as z:assert 'CompileCommand: exclude androidx/sqlite/driver/SupportSQLiteConnection.prepare bool exclude = true' in z.read('REGRESSION/RUN.log').decode()
b=json.loads((D/'BROAD_TARGETED_JIT/DISCOVERY/RESULT.json').read_text());old=json.loads((R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit56-conservation-2026-10-23/BROAD/DISCOVERY/RESULT.json').read_text());assert names(b)==names(old) and len(b['tests'])==1363 and b['classes']==229
for p,v in trees.items():assert subprocess.check_output(['git','rev-parse','HEAD:'+p],cwd=R,text=True).strip()==v,p
comparison=json.loads((D/'CONTROLLED_COMPARISON.json').read_text());assert comparison['rootCause']=='UNCONFIRMED' and comparison['mitigationStatus']=='PROVISIONAL_NOT_PROVEN_FIX' and comparison['persistedEnvironmentChanged'] is False
print(json.dumps(dict(savedEvidenceVerified=True,sourceCommit='8811c19449a259e834657ece2ecfe156db0d5423',separateRuntimeCounts={k:v['testCounts'] for k,v in results.items()},oldEvidenceConserved=True,originalArchivesShaCrc=66,broadSyntheticSkips=1363,broadRuntime='NOT_LAUNCHED',nativeCause='UNCONFIRMED',mitigation='PROVISIONAL_NOT_PROVEN_FIX',settingsChanged=False,testsExecutedByVerifier=0,completeSuite=False)))
