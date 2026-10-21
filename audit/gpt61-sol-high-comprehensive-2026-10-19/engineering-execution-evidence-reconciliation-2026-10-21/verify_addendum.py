"""Read-only identity validation. Does not adjudicate any requested scenario."""
from pathlib import Path
import json,csv,hashlib,subprocess,zipfile,xml.etree.ElementTree as ET,argparse
parser=argparse.ArgumentParser();parser.add_argument('--repository',required=True);args=parser.parse_args();R=Path(args.repository);D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-execution-evidence-reconciliation-2026-10-21';sha=lambda b:hashlib.sha256(b).hexdigest()
q=json.loads((D/'REQUEST_EVIDENCE_RECONCILIATION.json').read_text());raw=(R/q['originalRequestsPath']).read_bytes();assert sha(raw)==q['originalRequestsSha256'];original=list(csv.DictReader(raw.decode().splitlines()));assert len(original)==len(q['rows'])==70
for i,v in enumerate(q['rows']):
 assert v['originalRow']==i+1 and v['originalRequest']==original[i] and v['originalAuditExecutionStatus']=='NOT_EXECUTED';assert v['originalRowSha256']==sha(json.dumps(original[i],sort_keys=True,separators=(',',':'),ensure_ascii=False).encode());assert not v['independentRequestFulfilled']
idx=json.loads((D/'CURRENT_METHOD_EVIDENCE_INDEX.json').read_text());x=json.loads((R/idx['resultPath']).read_text());assert sha((R/idx['resultPath']).read_bytes())==idx['resultSha256'];assert sha((R/idx['rawArchivePath']).read_bytes())==idx['rawArchiveSha256'];expected={v['testClass']+'#'+v['method']:v for v in x['tests']};records={v['identity']:v for v in idx['records']};assert len(records)==1323 and set(records)==set(expected)
with zipfile.ZipFile(R/idx['rawArchivePath']) as z:
 xmls={}
 for name in {v['rawXmlMember'] for v in records.values()}:
  xmls[name]={(v.get('classname'),v.get('name')):('FAILED' if v.find('failure') is not None else 'ERROR' if v.find('error') is not None else 'SKIPPED' if v.find('skipped') is not None else 'PASSED') for v in ET.fromstring(z.read(name)).findall('testcase')}
 for identity,v in records.items():
  assert v['outcome']==expected[identity]['outcome']==xmls[v['rawXmlMember']][(v['testClass'],v['method'])];source=(R/v['sourcePath']).read_bytes();assert sha(source)==v['sourceFileSha256'];assert v['method'] in source.decode().splitlines()[v['methodDeclarationLine']-1];assert v['testClass'].rsplit('.',1)[-1] in source.decode().splitlines()[v['classDeclarationLine']-1]
for v in q['rows']:
 assert set(v['currentEvidenceIdentities'])<=set(records)
 for origin in v['oldFocusedScopeOrigins']:
  assert sha((R/origin['path']).read_bytes())==origin['sha256']
  for item in origin['sourceFileComparisons']:
   old=subprocess.check_output(['git','show',origin['historicalSource']+':'+item['path']],cwd=R);assert sha(old)==item['historicalFileSha256'];assert sha((R/item['path']).read_bytes())==item['currentFileSha256'];assert (old!=(R/item['path']).read_bytes())==item['fileBytesChanged']
resolution=json.loads((D/'ORIGINAL_RETURN_MEMBER_RESOLUTION.json').read_text());assert sha((R/resolution['archivePath']).read_bytes())==resolution['archiveSha256'];assert len(resolution['members'])==42;assert len({v['sha256'] for v in resolution['members'] if v['action'].startswith('EXPOSE')})==30
with zipfile.ZipFile(R/resolution['archivePath']) as z:
 assert z.testzip() is None
 for v in resolution['members']:assert z.read(v['archiveMember'])==(R/v['canonicalRepositoryPath']).read_bytes() and sha(z.read(v['archiveMember']))==v['sha256']
p=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/final-independent-recheck-preparation-2026-10-21';fix=json.loads((D/'WORK_ORDER_INSTRUCTION_FIX.json').read_text());assert sha((R/fix['path']).read_bytes())==fix['correctedSha256'];old=subprocess.check_output(['git','show',fix['previousCheckpoint']+':'+fix['path']],cwd=R);assert sha(old)==fix['previousSha256'];assert old.replace(fix['oldReference'].encode(),fix['newReference'].encode())==(R/fix['path']).read_bytes()
for name,h in json.loads((p/'SHA256_PREPARATION.json').read_text()).items():assert sha((p/name).read_bytes())==h
manifest=D/'SHA256.json'
if manifest.exists():
 for name,h in json.loads(manifest.read_text()).items():assert sha((R/name).read_bytes())==h
print(json.dumps({'original70RowsExact':True,'current1323SourceLinesXmlOutcomesBound':True,'all42OriginalZipMembersResolvedExact':True,'newHistoricalFiles':30,'reusedTrackedFiles':12,'oldFocusedScopesNotSummed':True,'currentWorkOrderLocatorCorrect':True,'independentScenarioAdjudication':False,'newAndroidTestsExecuted':0,'physicalAcceptance':'OUTSTANDING_LAST'}))
