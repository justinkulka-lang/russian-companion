"""Read-only persisted evidence verification; no build or source edits."""
from pathlib import Path
import argparse,json,hashlib,zipfile,xml.etree.ElementTree as ET,subprocess,collections
p=argparse.ArgumentParser();p.add_argument('--repository',type=Path,default=Path('/workspace/russian-companion'));R=p.parse_args().repository;D=Path(__file__).resolve().parent
sha=lambda b:hashlib.sha256(b).hexdigest()
x=json.loads((D/'REGRESSION/RESULT.json').read_text());c=json.loads((D/'REGRESSION/CONTEXT.json').read_text());d=json.loads((D/'DISCOVERY/RESULT.json').read_text())
assert x['sourceCommit']==c['sourceCommit'] and x['inputTrees']==c['inputTrees']==d['inputTrees']
assert x['testCounts']['reported']==1334 and x['classes']==221
assert sum(v for k,v in x['testCounts'].items() if k!='reported')==1334
assert x['exitCode']==int((D/'REGRESSION/RUN.exit').read_text())
for manifest in ['SHA256_PREPARATION.json','SHA256_CURRENT.json']:
 for n,h in json.loads((D/manifest).read_text()).items():assert sha((D/n).read_bytes())==h,(manifest,n)
for mode in ['DISCOVERY','REGRESSION']:
 idx=json.loads((D/('RAW_'+mode+'_INDEX.json')).read_text());assert sha((D/idx['archive']).read_bytes())==idx['sha256']
 with zipfile.ZipFile(D/idx['archive']) as z:
  assert z.testzip() is None and len(z.namelist())==len(set(z.namelist()))
  assert set(z.namelist())=={v['path'] for v in idx['members']}
  for v in idx['members']:assert sha(z.read(v['path']))==v['sha256']
  rr=json.loads((D/mode/'RESULT.json').read_text());cc=json.loads((D/mode/'CONTEXT.json').read_text());events=[json.loads(l) for l in z.read('EVENTS.jsonl').decode().splitlines()];finished=[v for v in events if v['kind']=='RESULT'];ev={(v['className'],v['name']):v['resultType'] for v in finished};assert len(finished)==len(ev)==1334
  xmls=[n for n in z.namelist() if n.startswith('XML/') and n.endswith('.xml')];assert len(xmls)==221;rows={}
  from datetime import datetime,timezone
  start=datetime.fromisoformat(cc['startedUtc'])
  for n in xmls:
   suite=ET.fromstring(z.read(n));stamp=datetime.fromisoformat(suite.get('timestamp').replace('Z','+00:00'));stamp=stamp.replace(tzinfo=timezone.utc) if stamp.tzinfo is None else stamp;assert stamp.timestamp()>=start.timestamp()-1
   for t in suite.findall('testcase'):
    k=(t.get('classname'),t.get('name'));assert k not in rows
    rows[k]='FAILURE' if t.find('failure') is not None or t.find('error') is not None else 'SKIPPED' if t.find('skipped') is not None else 'SUCCESS'
  assert rows==ev
  assert {(v['testClass'],v['method']):{'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[v['outcome']] for v in rr['tests']}==rows
  assert set(rows)=={(v['testClass'],v['method']) for v in d['tests']}
  assert not any(k[0].split('.')[-1] in cc['exclusions'] for k in rows)
  for k,v in cc['inputTrees'].items():assert subprocess.check_output(['git','rev-parse',cc['sourceCommit']+':'+k],cwd=R,text=True).strip()==v
current={k:subprocess.check_output(['git','rev-parse','HEAD:'+k],cwd=R,text=True).strip() for k in c['inputTrees']}
print(json.dumps(dict(sourceCommit=x['sourceCommit'],persistedZipXmlEventsResultsExact=True,freshDiscoveryIdentitiesExact=True,reported=1334,classes=221,counts=x['testCounts'],exitCode=x['exitCode'],manifestsAndArchiveCrcAllMembersVerified=True,currentHeadMatchesInputTrees=current==c['inputTrees'],completeSuite=False,independentAudit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST'),indent=2))
