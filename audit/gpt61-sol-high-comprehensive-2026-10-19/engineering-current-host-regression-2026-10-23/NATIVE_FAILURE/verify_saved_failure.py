"""Read-only integrity verification; no new test execution and no complete-suite inference."""
from pathlib import Path
from collections import Counter
from datetime import datetime,timezone
import json,csv,hashlib,zipfile,xml.etree.ElementTree as ET,subprocess
A=Path(__file__).resolve().parent;R=next(p for p in A.parents if (p/'AGENTS.md').exists());sha=lambda b:hashlib.sha256(b).hexdigest()
for p,v in json.loads((A/'SHA256.json').read_text()).items():assert sha((A/p).read_bytes())==v,p
for p,v in json.loads((A/'PRESERVED_PRIOR_EVIDENCE.json').read_text()).items():assert sha((R/p).read_bytes())==v,p
archiveManifest=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit56-conservation-2026-10-23/ORIGINAL_ARCHIVE_CONSERVATION.json'
for p,v in json.loads(archiveManifest.read_text()).items():
 assert sha((R/p).read_bytes())==v
 with zipfile.ZipFile(R/p) as z:assert z.testzip() is None
for name,ix in [('RAW_NATIVE_FAILURE.zip','RAW_NATIVE_FAILURE_INDEX.json'),('PREPARED_FOLLOWUP.zip','PREPARED_FOLLOWUP_INDEX.json')]:
 with zipfile.ZipFile(A/name) as z:
  assert z.testzip() is None;index=json.loads((A/ix).read_text());assert sorted(z.namelist())==sorted(x['path'] for x in index)
  for x in index:
   b=z.read(x['path']);assert len(b)==x['bytes'] and sha(b)==x['sha256'];assert not x['path'].lower().endswith('.pdf')
c=json.loads((A/'CHECKPOINT.json').read_text());context=json.loads((A/'CONTEXT.json').read_text());assert c['sourceCommit']==context['sourceCommit']=='e66dfe0771424daf6e090fb258f77523ec40a4c1'
assert c['actualGradleClientExit']==int((A/'RUN.exit').read_text())==130
for p,v in c['inputTrees'].items():assert subprocess.check_output(['git','rev-parse','HEAD:'+p],cwd=R,text=True).strip()==v,p
with zipfile.ZipFile(A/'RAW_NATIVE_FAILURE.zip') as z:
 e=[json.loads(s) for s in z.read('REGRESSION/EVENTS.jsonl').decode().splitlines()];rs=[x for x in e if x['kind']=='RESULT'];by={(x['className'],x['name']):x for x in rs};assert len(rs)==len(by)==1130
 assert dict(Counter(x['resultType'] for x in rs))==c['eventCounts']==dict(SUCCESS=1121,FAILURE=6,SKIPPED=3)
 assert sha(z.read('DIAGNOSTICS/NATIVE_CRASH/hs_err_pid58284.log'))=='b8f81af9f62af12f1699401bda7dd2280d14d71319d867e547abd87f7f10effb'
 assert sha(z.read('DIAGNOSTICS/NATIVE_CRASH/replay_pid58284.log'))=='e5b1044f0ee7ec8787423ed1ee6b9adc8a6a44b335053bf55e16de9c17bfa1b8'
 assert 'C2 CompilerThread0' in z.read('DIAGNOSTICS/NATIVE_CRASH/hs_err_pid58284.log').decode()
 start=datetime.fromisoformat(context['startedUtc']);inv=json.loads((A/'XML_FRESHNESS_INVENTORY.json').read_text());assert len(inv)==229
 for x in inv:
  b=z.read('UNVERIFIED_XML/'+x['file']);assert sha(b)==x['sha256'];s=ET.fromstring(b);stamp=datetime.fromisoformat(s.get('timestamp').replace('Z','+00:00'))
  if stamp.tzinfo is None:stamp=stamp.replace(tzinfo=timezone.utc)
  assert stamp.timestamp()<start.timestamp()-1 and x['freshForInvocation'] is False
 assert json.loads((A/'FRESH_XML_METHODS.json').read_text())==[]
 assert c['freshXmlFiles']==c['freshXmlSelectedMethods']==0 and c['xmlAndEventAgreementVerified'] is False
 for row in json.loads((A/'FAILURE_EVENTS.json').read_text()):
  raw=by[(row['className'],row['name'])];assert raw['resultType']=='FAILURE'
  for rawE,saved in zip(raw['exceptions'],row['exceptions']):
   m=rawE.get('message','');assert saved['messagePreview']==m[:1000] and saved['fullMessageBytes']==len(m.encode()) and saved['fullMessageSha256']==sha(m.encode())
 expectedJson=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit56-conservation-2026-10-23/BROAD/DISCOVERY/RESULT.json';expected={(x['testClass'],x['method']) for x in json.loads(expectedJson.read_text())['tests']}
 rows=list(csv.DictReader((A/'DISCOVERED_METHOD_DISPOSITION.csv').open()));assert len(rows)==len(expected)==1363;assert {(x['testClass'],x['method']) for x in rows}==expected;assert set(by)<=expected;assert len(expected-set(by))==233
 for x in rows:
  k=(x['testClass'],x['method']);assert x['eventOutcome']==(by[k]['resultType'] if k in by else '') and x['freshXmlOutcome']==''
 assert sum(x['disposition']=='NO_COMPLETED_EVENT' for x in rows)==233
with zipfile.ZipFile(A/'PREPARED_FOLLOWUP.zip') as z:
 p=json.loads(z.read('PREPARATION.json'));assert p['state']=='PROPOSED_NOT_APPLIED_NOT_COMPILED_NOT_TESTED'
 for n,v in p['originalSha256'].items():assert sha((R/n).read_bytes())==v,n
 for n,v in p['proposedSha256'].items():assert sha(z.read(n))==v,n
identity=json.loads((A/'READ_ONLY_SOURCE_IDENTITY.json').read_text());assert identity['sourceCommit']==c['sourceCommit'] and identity['exitCode']==0;assert identity['canonicalSourceDigest']==identity['candidateSourceDigest']==c['canonicalSourceDigest'];assert c['apkIdentity'] is None and c['apkSignature'] is None and c['completeSuite'] is False
t=json.loads((A/'TASK_RESULTS.json').read_text());assert t['lintResult']==t['releaseGateAttachmentResult']=='NOT_REACHED' and t['lintCounts'] is None
print(json.dumps(dict(savedEvidenceVerified=True,sourceCommit=c['sourceCommit'],eventCounts=c['eventCounts'],completedEventIdentities=1130,withoutCompletedEvent=233,freshXmlFiles=0,staleXmlFiles=229,actualClientExit=130,nativeRootCause='UNCONFIRMED',originalArchivesShaCrc=66,proposalApplied=False,testsExecutedByThisVerifier=0,completeSuite=False)))
