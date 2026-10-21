from pathlib import Path
import json,hashlib,zipfile,collections,xml.etree.ElementTree as ET,subprocess
R=Path('/workspace/russian-companion');D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/post-reconciliation-host-regression-2026-10-21'
sha=lambda b:hashlib.sha256(b).hexdigest()
x=json.loads((D/'REGRESSION/RESULT.json').read_text());c=json.loads((D/'REGRESSION/CONTEXT.json').read_text());d=json.loads((D/'DISCOVERY/RESULT.json').read_text())
assert x['sourceCommit']==c['sourceCommit']=='6ab9d87eadcdb477719af5f0db3af6b90cce8d45'
assert x['testCounts']['reported']==1323 and x['classes']==219 and sum(v for k,v in x['testCounts'].items() if k!='reported')==1323
assert x['exitCode']==int((D/'REGRESSION/RUN.exit').read_text())
for manifest in ['SHA256_DISCOVERY.json','SHA256_CURRENT.json']:
 for name,h in json.loads((D/manifest).read_text()).items():assert sha((D/name).read_bytes())==h,(manifest,name)
for name in ['RAW_DISCOVERY_INDEX.json','RAW_REGRESSION_INDEX.json']:
 idx=json.loads((D/name).read_text());a=D/idx['archive'];assert sha(a.read_bytes())==idx['sha256']
 with zipfile.ZipFile(a) as z:
  assert z.testzip() is None
  assert len(z.namelist())==len(set(z.namelist()))
  for row in idx['members']:assert sha(z.read(row['path']))==row['sha256'],row['path']
with zipfile.ZipFile(D/'RAW_REGRESSION.zip') as z:
 events=[json.loads(v) for v in z.read('EVENTS.jsonl').decode().splitlines()];rs=[v for v in events if v['kind']=='RESULT'];ev={(v['className'],v['name']):v['resultType'] for v in rs};assert len(rs)==len(ev)==1323
 xmls=[n for n in z.namelist() if n.startswith('XML/') and n.endswith('.xml')];assert len(xmls)==219
 rows={}
 for n in xmls:
  for t in ET.fromstring(z.read(n)).findall('testcase'):
   k=(t.get('classname'),t.get('name'));assert k not in rows
   rows[k]='FAILURE' if t.find('failure') is not None or t.find('error') is not None else 'SKIPPED' if t.find('skipped') is not None else 'SUCCESS'
 assert rows==ev
 resultmap={(v['testClass'],v['method']):{'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[v['outcome']] for v in x['tests']};assert resultmap==rows
 assert set(rows)=={(v['testClass'],v['method']) for v in d['tests']}
 assert not any(k[0].split('.')[-1] in c['exclusions'] for k in rows)
for k,v in c['inputTrees'].items():assert subprocess.check_output(['git','rev-parse','HEAD:'+k],cwd=R,text=True).strip()==v
recheck=json.loads((D/'ORIGINAL_FOURTEEN_RECHECK.json').read_text());assert len(recheck['observations'])==14
out={'persistedZipXmlEventsAndResultExact':True,'freshDiscoveryIdentitySetExact':True,'reported':1323,'classes':219,'counts':x['testCounts'],'exitCode':x['exitCode'],'original14Outcomes':dict(collections.Counter(v['currentOutcome'] for v in recheck['observations'])),'bothManifestHashesAndArchiveCrcAllMembersVerified':True,'allFiveInputTreesUnchanged':True,'completeSuite':False,'independentAudit':'INCOMPLETE','physicalAcceptance':'OUTSTANDING_LAST'}
print(json.dumps(out,indent=2))
