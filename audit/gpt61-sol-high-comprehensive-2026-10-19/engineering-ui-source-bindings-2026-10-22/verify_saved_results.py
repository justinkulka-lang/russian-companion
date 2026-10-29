"""Read-only Git/byte/XML verification; not semantic audit or release approval."""
from pathlib import Path,PurePosixPath
import argparse,json,hashlib,subprocess,zipfile,xml.etree.ElementTree as ET
from datetime import datetime,timezone
p=argparse.ArgumentParser();p.add_argument('--repository',type=Path,required=True);R=p.parse_args().repository;D=Path(__file__).resolve().parent
git=lambda *a:subprocess.check_output(['git',*a],cwd=R);sha=lambda b:hashlib.sha256(b).hexdigest();read=lambda p:json.loads(p.read_text())
def check(value,message):
 if not value:raise ValueError(message)
for name in ['SHA256_PREPARATION.json','SHA256_CURRENT.json']:
 for path,h in read(D/name).items():check(sha((D/path).read_bytes())==h,'manifest '+name+': '+path)
check(set(read(D/'SHA256_CURRENT.json'))=={f.relative_to(D).as_posix() for f in D.rglob('*') if f.is_file() and f.name!='SHA256_CURRENT.json'},'current manifest completeness')
proposal=read(D/'PROPOSAL_REVIEW.json');check(proposal['twoFreshJvmRendersIdentical'] and proposal['changedFiles']==95 and proposal['unchangedProposedFiles']==60,'reviewed repeated-render conservation')
result=read(D/'REGRESSION/RESULT.json');context=read(D/'REGRESSION/CONTEXT.json');discovery=read(D/'DISCOVERY/RESULT.json');source=context['sourceCommit']
check(result['sourceCommit']==source==discovery['sourceCommit'],'same pinned source')
check(result['inputTrees']==context['inputTrees']==discovery['inputTrees'],'same input trees')
check(result['testCounts']==dict(reported=67,passed=67,failed=0,errors=0,skipped=0) and result['classes']==11 and result['exitCode']==0,'executed scope')
check(discovery['testCounts']==dict(reported=67,passed=0,failed=0,errors=0,skipped=67),'synthetic discovery, zero passes')
def raw_archive(label):
 index=read(D/('RAW_'+label+'_INDEX.json'));z=zipfile.ZipFile(D/('RAW_'+label+'.zip'))
 check(z.testzip() is None and len(z.namelist())==len(set(z.namelist()))==len(index) and set(z.namelist())==set(index),'archive inventory/CRC '+label)
 for name,v in index.items():
  rel=PurePosixPath(name);check(not rel.is_absolute() and '..' not in rel.parts,'archive path safety');b=z.read(name);check(sha(b)==v['sha256'] and len(b)==v['bytes'],'archive member '+name)
 return z
for mode in ['DISCOVERY','REGRESSION']:
 with raw_archive(mode) as z:
  rr=read(D/mode/'RESULT.json');cc=read(D/mode/'CONTEXT.json');check(int(z.read('RUN.exit'))==rr['exitCode']==int((D/mode/'RUN.exit').read_text()),'actual exit')
  events=[json.loads(l) for l in z.read('EVENTS.jsonl').decode().splitlines()];completed=[e for e in events if e['kind']=='RESULT'];ev={(e['className'],e['name']):e['resultType'] for e in completed};check(len(completed)==len(ev)==67,'events')
  xmls=[n for n in z.namelist() if n.startswith('XML/') and n.endswith('.xml')];check(len(xmls)==11,'class inventory');rows={};start=datetime.fromisoformat(cc['startedUtc'])
  for name in xmls:
   suite=ET.fromstring(z.read(name));stamp=datetime.fromisoformat(suite.get('timestamp').replace('Z','+00:00'));stamp=stamp.replace(tzinfo=timezone.utc) if stamp.tzinfo is None else stamp;check(stamp.timestamp()>=start.timestamp()-1,'fresh XML')
   for test in suite.findall('testcase'):
    key=(test.get('classname'),test.get('name'));check(key not in rows,'duplicate testcase');rows[key]='FAILURE' if test.find('failure') is not None or test.find('error') is not None else 'SKIPPED' if test.find('skipped') is not None else 'SUCCESS'
  expected={(v['testClass'],v['method']):{'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[v['outcome']] for v in rr['tests']}
  check(ev==rows==expected,'exact identities and outcomes');check(set(rows)=={(v['testClass'],v['method']) for v in discovery['tests']},'discovery identities');check({k[0] for k in rows}==set(cc['selectors']),'full selected classes');check(not any(k[0].split('.')[-1] in cc['exclusions'] for k in rows),'exporter exclusions')
  if mode=='REGRESSION':
   issues=ET.fromstring(z.read('LINT.xml')).findall('issue');check(rr['lintIssuesBySeverity']=={s:sum(v.get('severity')==s for v in issues) for s in ['Fatal','Error','Warning','Hint']},'lint identity');check(rr['lintIssuesBySeverity']['Error']==rr['lintIssuesBySeverity']['Fatal']==0,'lint errors')
   log=z.read('RUN.log').decode();check('BUILD SUCCESSFUL' in log,'successful build')
   for task in ['lintDebug','verifyReleaseGateAttachment']:check('> Task :app:'+task in log.splitlines(),'executed '+task)
with raw_archive('GENERATION') as z:
 prep=read(D/'GENERATION_PREPARATION.json')
 for name in ['CANDIDATE_1.log','CANDIDATE_2.log']:
  text=z.read(name).decode();check('CANONICAL_SOURCE_DIGEST '+prep['newDigest']+' REAL_ROOT_MATCH '+prep['newDigest'] in text,'canonical Kotlin digest');check('COMPLETE 153 files' in text,'authentic renderer completion')
check(len(read(D/'GENERATION_RESULTS.json'))==3 and all(v['exitCode']==0 for v in read(D/'GENERATION_RESULTS.json')),'both JVM exits')
idx=read(D/'HISTORICAL_PRIOR_CURRENT_INDEX.json');check(sha((D/idx['archive']).read_bytes())==idx['archiveSha256'],'historical archive digest');check(len(idx['files'])==155 and sum(v['changed'] for v in idx['files'])==95,'155/95/60 inventory');current_files=True
with zipfile.ZipFile(D/idx['archive']) as z:
 check(z.testzip() is None and len(z.namelist())==len(set(z.namelist()))==95,'historical95 CRC')
 for row in idx['files']:
  old=git('show',idx['originalCommit']+':'+row['path']);new=git('show',source+':'+row['path']);check(sha(old)==row['oldSha256'] and sha(new)==row['newSha256'],'source-bound generated identities');check(new==old.replace(prep['oldDigest'].encode(),prep['newDigest'].encode()),'only digest bytes')
  if row['changed']:check(z.read(row['path'])==old,'archived prior bytes')
  if row['path'].endswith(('_REVIEW_WORKSHEET.tsv','_OWNED_EDITION_MAPPING_WORKSHEET.tsv')):check(not row['changed'],'worksheets untouched')
  current_files &= sha((R/row['path']).read_bytes())==row['newSha256']
for path,h in read(D/'PRESERVED_PRECEDING_SCOPES.json').items():check(sha((R/path).read_bytes())==h,'preserved earlier evidence')
for path,h in read(D/'CONSERVATION.json')['original66ArchiveHashes'].items():
 check(sha((R/path).read_bytes())==h,'original archive hash')
 with zipfile.ZipFile(R/path) as z:check(z.testzip() is None,'original archive CRC')
for path,tree in context['inputTrees'].items():check(git('rev-parse',source+':'+path).decode().strip()==tree,'historical input Git tree')
candidate=json.loads(git('show',source+':release/acceptance/candidate.json'));check(candidate['sourceDigest']==prep['newDigest'] and candidate['frozen'] is False and candidate['sourceCommit'] is None and candidate['artifact']=={'status':'NOT_RECORDED'} and candidate['publicRelease']=='NOT_APPROVED','candidate remains unapproved')
print(json.dumps(dict(status='SAVED_SOURCE_BINDINGS_AND_FOCUSED_HOST_EVIDENCE_VERIFIED',testedSource=source,counts=result['testCounts'],classes=11,canonicalKotlinDigest=prep['newDigest'],twoFreshJvmRendersIdentical=True,prior95Unchanged60Current155Verified=True,original66ArchivesAndEarlierScopeHashesVerified=True,xmlEventsDiscoveryExact=True,currentHeadMatchesInputTrees=all(git('rev-parse','HEAD:'+p).decode().strip()==v for p,v in context['inputTrees'].items()),currentHeadMatches155Files=current_files,completeSuite=False,findingsClosed=0,independentAudit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST'),indent=2))
