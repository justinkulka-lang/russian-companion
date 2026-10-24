"""Read-only saved broad discovery check. Synthetic skips are not runtime passes."""
from pathlib import Path
from datetime import datetime,timezone
import json,hashlib,zipfile,subprocess,xml.etree.ElementTree as ET
D=Path(__file__).resolve().parent;R=next(p for p in D.parents if (p/'AGENTS.md').exists());sha=lambda b:hashlib.sha256(b).hexdigest()
for n,v in json.loads((D/'SHA256.json').read_text()).items():assert sha((D/n).read_bytes())==v,n
with zipfile.ZipFile(D/'RAW_DISCOVERY.zip') as z:
 assert z.testzip() is None
 index=json.loads((D/'RAW_DISCOVERY_INDEX.json').read_text());assert sorted(z.namelist())==sorted(x['path'] for x in index)
 for x in index:
  b=z.read(x['path']);assert len(b)==x['bytes'] and sha(b)==x['sha256'];assert not x['path'].lower().endswith('.pdf')
 c=json.loads(z.read('DISCOVERY/CONTEXT.json'));r=json.loads(z.read('DISCOVERY/RESULT.json'));assert r['sourceCommit']==c['sourceCommit']=='0d52922edc0047e11985b2bb8b4946d7122602c5'
 assert r['testCounts']==dict(reported=1378,passed=0,failed=0,errors=0,skipped=1378) and r['classes']==233 and r['exitCode']==0 and r['syntheticDryRun']
 assert c['selectors']==[] and c['syntheticDryRun'] and '--test-dry-run' in c['command']
 assert sha(z.read('run.py'))==c['runnerSha256']
 assert sha((D/'launch_mode.py').read_bytes())==json.loads((D/'RUNTIME_PREPARATION.json').read_text())['runtimeLauncherSha256']
 for p,v in c['initScriptSha256'].items():assert sha(z.read('INIT/'+Path(p).name))==v
 for p,v in json.loads(z.read('DISCOVERY/SHA256.json')).items():assert sha(z.read('DISCOVERY/'+p))==v,p
 for p,v in c['inputTrees'].items():assert subprocess.check_output(['git','rev-parse',r['sourceCommit']+':'+p],cwd=R,text=True).strip()==v
 rows={(x['testClass'],x['method']) for x in r['tests']};assert len(rows)==len(r['tests'])==1378
 xml=set();classes=0;start=datetime.fromisoformat(c['startedUtc']).timestamp()
 for name in z.namelist():
  if not name.startswith('DISCOVERY/XML/') or not name.endswith('.xml'):continue
  classes+=1;s=ET.fromstring(z.read(name));stamp=datetime.fromisoformat(s.get('timestamp').replace('Z','+00:00'))
  if stamp.tzinfo is None:stamp=stamp.replace(tzinfo=timezone.utc)
  assert stamp.timestamp()>=start-1
  for t in s.findall('testcase'):
   assert t.find('skipped') is not None and t.find('failure') is None and t.find('error') is None;k=(t.get('classname'),t.get('name'));assert k not in xml;xml.add(k)
 assert classes==233 and xml==rows
 events=[json.loads(s) for s in z.read('DISCOVERY/EVENTS.jsonl').splitlines()];ev=[e for e in events if e['kind']=='RESULT'];assert len(ev)==1378 and all(e['resultType']=='SKIPPED' for e in ev);assert {(e['className'],e['name']) for e in ev}==rows
 assert not any(k[0].split('.')[-1] in c['exclusions'] for k in rows)
 old=json.loads((R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-contract-broad-2026-10-24/RUNTIME_RESULT.json').read_text());previous={(t['testClass'],t['method']) for t in old['tests']}
 cohort=json.loads((D.parent/'ATTEMPT_1/REGRESSION_RESULT.json').read_text());newClasses={'dev.russiancompanion.v2.'+x for x in ['Unit7GenderReviewBaselineTest','Unit7GenderCompatibilityTest','Unit7GenderReviewUiTest','Unit7BindingConservationTest']};extra={(t['testClass'],t['method']) for t in cohort['tests'] if t['testClass'] in newClasses}
 assert previous<=rows and rows-previous==extra and len(previous)==1365 and len(extra)==13
 focused=json.loads((D.parent/'ATTEMPT_2/REGRESSION_RESULT.json').read_text());assert focused['inputTrees']==r['inputTrees']
 liveRuntime='RUNTIME_DIRECTORY_PRESENT_CHECK_CONTEXT_AND_PROCESSES' if (Path('/workspace/artifacts/unit7-source-reconciliation-2026-10-24/BROAD')/'REGRESSION').exists() else 'PREPARED_NOT_LAUNCHED'
 print(json.dumps(dict(savedDiscoveryVerified=True,sourceCommit=r['sourceCommit'],syntheticSkips=1378,runtimePasses=0,classes=233,priorMethodsRetained=1365,addedMethods=13,removedMethods=0,fullRuntime=liveRuntime,testsExecutedByVerifier=0,completeSuite=False)))
