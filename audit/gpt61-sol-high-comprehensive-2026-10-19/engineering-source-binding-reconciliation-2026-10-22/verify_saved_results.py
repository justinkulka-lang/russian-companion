"""Verify saved evidence against recorded Git contexts;never builds or rewrites records."""
from pathlib import Path
import argparse,json,hashlib,zipfile,subprocess,xml.etree.ElementTree as ET
from datetime import datetime,timezone
p=argparse.ArgumentParser();p.add_argument('--repository',type=Path,default=Path('/workspace/russian-companion'));R=p.parse_args().repository;D=Path(__file__).resolve().parent;git=lambda *a:subprocess.check_output(['git',*a],cwd=R);sha=lambda b:hashlib.sha256(b).hexdigest();read=lambda p:json.loads(p.read_text())
x=read(D/'REGRESSION/RESULT.json');c=read(D/'REGRESSION/CONTEXT.json');disc=read(D/'DISCOVERY/RESULT.json');assert x['sourceCommit']==c['sourceCommit'];assert x['inputTrees']==disc['inputTrees']==c['inputTrees'];assert x['testCounts']['reported']==56 and x['classes']==9 and sum(v for k,v in x['testCounts'].items() if k!='reported')==56;assert x['exitCode']==int((D/'REGRESSION/RUN.exit').read_text())
for name in ['SHA256_PREPARATION.json','SHA256_DISCOVERY.json','SHA256_CURRENT.json']:
 for n,h in read(D/name).items():assert sha((D/n).read_bytes())==h,(name,n)
for mode in ['DISCOVERY','REGRESSION']:
 idx=read(D/('RAW_'+mode+'_INDEX.json'));assert sha((D/idx['archive']).read_bytes())==idx['archiveSha256']
 with zipfile.ZipFile(D/idx['archive']) as z:
  assert z.testzip() is None and len(z.namelist())==len(set(z.namelist())) and set(z.namelist())=={v['path'] for v in idx['members']}
  for v in idx['members']:assert sha(z.read(v['path']))==v['sha256']
  rr=read(D/mode/'RESULT.json');cc=read(D/mode/'CONTEXT.json');events=[json.loads(l) for l in z.read('EVENTS.jsonl').decode().splitlines()];rs=[v for v in events if v['kind']=='RESULT'];ev={(v['className'],v['name']):v['resultType'] for v in rs};assert len(rs)==len(ev)==56;xmls=[n for n in z.namelist() if n.startswith('XML/') and n.endswith('.xml')];assert len(xmls)==9;rows={};start=datetime.fromisoformat(cc['startedUtc'])
  for n in xmls:
   s=ET.fromstring(z.read(n));stamp=datetime.fromisoformat(s.get('timestamp').replace('Z','+00:00'));stamp=stamp.replace(tzinfo=timezone.utc) if stamp.tzinfo is None else stamp;assert stamp.timestamp()>=start.timestamp()-1
   for t in s.findall('testcase'):
    key=(t.get('classname'),t.get('name'));assert key not in rows;rows[key]='FAILURE' if t.find('failure') is not None or t.find('error') is not None else 'SKIPPED' if t.find('skipped') is not None else 'SUCCESS'
  assert ev==rows=={(v['testClass'],v['method']):{'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[v['outcome']] for v in rr['tests']};assert set(rows)=={(v['testClass'],v['method']) for v in disc['tests']};assert {k[0] for k in rows}==set(cc['selectors']);assert not any(k[0].split('.')[-1] in cc['exclusions'] for k in rows)
idx=read(D/'HISTORICAL_PRIOR_CURRENT_INDEX.json');ctx=read(D/'GENERATION_PREPARATION.json');assert sha((D/idx['archive']).read_bytes())==idx['archiveSha256'];currentFilesMatch=True
with zipfile.ZipFile(D/idx['archive']) as z:
 assert z.testzip() is None and len(z.namelist())==95
 for v in idx['files']:
  old=git('show',idx['originalCommit']+':'+v['path']);new=git('show',c['sourceCommit']+':'+v['path']);assert sha(old)==v['oldSha256'] and sha(new)==v['newSha256'];assert new==old.replace(ctx['oldDigest'].encode(),ctx['newDigest'].encode())
  if sha((R/v['path']).read_bytes())!=v['newSha256']:currentFilesMatch=False
  if v['changed']:assert sha(z.read(v['path']))==v['oldSha256']
for rel,h in read(D/'PRESERVED_PRECEDING_SCOPES.json').items():assert sha((R/rel).read_bytes())==h
for k,v in c['inputTrees'].items():assert git('rev-parse',c['sourceCommit']+':'+k).decode().strip()==v
headTrees={k:git('rev-parse','HEAD:'+k).decode().strip() for k in c['inputTrees']};print(json.dumps(dict(testedSource=c['sourceCommit'],counts=x['testCounts'],classes=9,exitCode=x['exitCode'],xmlEventsDiscoveryExact=True,manifestsAndRawArchiveMembersVerified=True,historical95AndReused60Verified=True,onlyDigestBytesChanged=True,priorScopesUnchanged=True,currentHeadMatchesInputTrees=headTrees==c['inputTrees'],currentHeadMatches155ReconciledFiles=currentFilesMatch,completeSuite=False,independentAudit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST'),indent=2))
