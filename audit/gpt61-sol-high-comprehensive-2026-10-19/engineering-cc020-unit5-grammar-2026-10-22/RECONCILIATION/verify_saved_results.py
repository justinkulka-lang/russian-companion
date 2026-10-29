"""Read-only Git/byte/XML verification; not semantic audit or release approval."""
from pathlib import Path,PurePosixPath
import argparse,json,hashlib,subprocess,zipfile,xml.etree.ElementTree as ET
from datetime import datetime,timezone
p=argparse.ArgumentParser();p.add_argument('--repository',type=Path,required=True);R=p.parse_args().repository;D=Path(__file__).resolve().parent
git=lambda *a:subprocess.check_output(['git',*a],cwd=R);sha=lambda b:hashlib.sha256(b).hexdigest();read=lambda p:json.loads(p.read_text())
def check(value,message):
 if not value:raise ValueError(message)
for path,h in read(D.parent/'SHA256_FINAL_CHECKPOINT.json').items():check(sha((D.parent/path).read_bytes())==h,'final checkpoint '+path)
for name in ['SHA256_PREPARATION.json','SHA256_CURRENT.json']:
 for path,h in read(D/name).items():check(sha((D/path).read_bytes())==h,'manifest '+name+': '+path)
check(set(read(D/'SHA256_CURRENT.json'))=={f.relative_to(D).as_posix() for f in D.rglob('*') if f.is_file() and f.name!='SHA256_CURRENT.json'},'current manifest completeness')
proposal=read(D/'PROPOSAL_REVIEW.json');check(proposal['twoFreshJvmRendersIdentical'] and proposal['changedFiles']==95 and proposal['unchangedProposedFiles']==60,'reviewed repeated-render conservation')
result=read(D/'REGRESSION/RESULT.json');context=read(D/'REGRESSION/CONTEXT.json');discovery=read(D/'DISCOVERY/RESULT.json');source=context['sourceCommit']
check(result['sourceCommit']==source==discovery['sourceCommit'],'same pinned source')
check(result['inputTrees']==context['inputTrees']==discovery['inputTrees'],'same input trees')
check(result['testCounts']==dict(reported=144,passed=142,failed=2,errors=0,skipped=0) and result['classes']==21 and result['exitCode']==1,'executed scope')
check(discovery['testCounts']==dict(reported=144,passed=0,failed=0,errors=0,skipped=144),'synthetic discovery, zero passes')
def raw_archive(label):
 index=read(D/('RAW_'+label+'_INDEX.json'));z=zipfile.ZipFile(D/('RAW_'+label+'.zip'))
 check(z.testzip() is None and len(z.namelist())==len(set(z.namelist()))==len(index) and set(z.namelist())==set(index),'archive inventory/CRC '+label)
 for name,v in index.items():
  rel=PurePosixPath(name);check(not rel.is_absolute() and '..' not in rel.parts,'archive path safety');b=z.read(name);check(sha(b)==v['sha256'] and len(b)==v['bytes'],'archive member '+name)
 return z
with raw_archive('PRE_RESTART_DISCOVERY') as z:
 originalDiscovery=json.loads(z.read('RESULT.json'));check(originalDiscovery['syntheticDryRun'] and originalDiscovery['testCounts']==dict(reported=144,passed=0,failed=0,errors=0,skipped=144),'original synthetic discovery preserved separately')
with raw_archive('INTERRUPTED_REGRESSION') as z:
 interruption=read(D/'INTERRUPTION_RECORD.json');ev=[json.loads(l) for l in z.read('EVENTS.jsonl').decode().splitlines()];completed=[e for e in ev if e['kind']=='RESULT'];check(len(completed)==66 and len({(e['className'],e['name']) for e in completed})==66,'interrupted completed events');check(sum(e['resultType']=='SUCCESS' for e in completed)==64 and sum(e['resultType']=='SKIPPED' for e in completed)==2,'interrupted outcomes preserved');check(interruption['finalExitCode'] is None and interruption['selectedMethodsWithoutCompletedEvents']==78,'no complete interrupted result');check('RUN.exit' not in z.namelist(),'no fabricated exit')
for mode in ['DISCOVERY','REGRESSION']:
 with raw_archive(mode) as z:
  rr=read(D/mode/'RESULT.json');cc=read(D/mode/'CONTEXT.json');check(int(z.read('RUN.exit'))==rr['exitCode']==int((D/mode/'RUN.exit').read_text()),'actual exit')
  events=[json.loads(l) for l in z.read('EVENTS.jsonl').decode().splitlines()];completed=[e for e in events if e['kind']=='RESULT'];ev={(e['className'],e['name']):e['resultType'] for e in completed};check(len(completed)==len(ev)==144,'events')
  xmls=[n for n in z.namelist() if n.startswith('XML/') and n.endswith('.xml')];check(len(xmls)==21,'class inventory');rows={};start=datetime.fromisoformat(cc['startedUtc'])
  for name in xmls:
   suite=ET.fromstring(z.read(name));stamp=datetime.fromisoformat(suite.get('timestamp').replace('Z','+00:00'));stamp=stamp.replace(tzinfo=timezone.utc) if stamp.tzinfo is None else stamp;check(stamp.timestamp()>=start.timestamp()-1,'fresh XML')
   for test in suite.findall('testcase'):
    key=(test.get('classname'),test.get('name'));check(key not in rows,'duplicate testcase');rows[key]='FAILURE' if test.find('failure') is not None or test.find('error') is not None else 'SKIPPED' if test.find('skipped') is not None else 'SUCCESS'
  expected={(v['testClass'],v['method']):{'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[v['outcome']] for v in rr['tests']}
  check(ev==rows==expected,'exact identities and outcomes');check(set(rows)=={(v['testClass'],v['method']) for v in discovery['tests']},'discovery identities');check({k[0] for k in rows}==set(cc['selectors']),'full selected classes');check(not any(k[0].split('.')[-1] in cc['exclusions'] for k in rows),'exporter exclusions')
  if mode=='REGRESSION':
   issues=ET.fromstring(z.read('LINT.xml')).findall('issue');check(rr['lintIssuesBySeverity']=={s:sum(v.get('severity')==s for v in issues) for s in ['Fatal','Error','Warning','Hint']},'lint identity');check(rr['lintIssuesBySeverity']['Error']==rr['lintIssuesBySeverity']['Fatal']==0,'lint errors')
   log=z.read('RUN.log').decode();check('BUILD FAILED' in log,'original failed scope preserved')
   for task in ['lintDebug','verifyReleaseGateAttachment']:check('> Task :app:'+task in log.splitlines(),'executed '+task)
follow=read(D/'FOLLOWUP_REGRESSION/RESULT.json');fc=read(D/'FOLLOWUP_REGRESSION/CONTEXT.json');fd=read(D/'FOLLOWUP_DISCOVERY/RESULT.json');check(follow['testCounts']==dict(reported=14,passed=14,failed=0,errors=0,skipped=0) and follow['classes']==2 and follow['exitCode']==0,'separate followup14passed');check(fd['testCounts']==dict(reported=14,passed=0,failed=0,errors=0,skipped=14),'followup discovery synthetic');check(fc['sourceCommit']==follow['sourceCommit']==fd['sourceCommit'] and fc['inputTrees']==follow['inputTrees']==fd['inputTrees'],'same followup inputs');check(all(fc['inputTrees'][k]==v for k,v in context['inputTrees'].items() if k!='app/src/test'),'only test input tree differs from144scope')
for label in ['DISCOVERY','REGRESSION']:
 with raw_archive('FOLLOWUP_'+label) as z:
  rr=read(D/('FOLLOWUP_'+label)/'RESULT.json');ee=[json.loads(l) for l in z.read('EVENTS.jsonl').decode().splitlines()];ev={(e['className'],e['name']):e['resultType'] for e in ee if e['kind']=='RESULT'};xx={}
  for name in z.namelist():
   if name.startswith('XML/') and name.endswith('.xml'):
    suite=ET.fromstring(z.read(name));stamp=datetime.fromisoformat(suite.get('timestamp').replace('Z','+00:00'));stamp=stamp.replace(tzinfo=timezone.utc) if stamp.tzinfo is None else stamp;check(stamp.timestamp()>=datetime.fromisoformat(read(D/('FOLLOWUP_'+label)/'CONTEXT.json')['startedUtc']).timestamp()-1,'fresh followup XML')
    for t in suite.findall('testcase'):xx[(t.get('classname'),t.get('name'))]='FAILURE' if t.find('failure') is not None or t.find('error') is not None else 'SKIPPED' if t.find('skipped') is not None else 'SUCCESS'
  expected={(r['testClass'],r['method']):'SKIPPED' if label=='DISCOVERY' else 'SUCCESS' for r in rr['tests']};check(len(xx)==14 and xx==ev==expected,'followup XML/events/counts exact');check(int(z.read('RUN.exit'))==0,'followup actual exit')
  if label=='REGRESSION':
   log=z.read('RUN.log').decode();check('BUILD SUCCESSFUL' in log,'followup build passed');issues=ET.fromstring(z.read('LINT.xml')).findall('issue');check({q:sum(i.get('severity')==q for i in issues) for q in ['Fatal','Error','Warning','Hint']}==dict(Fatal=0,Error=0,Warning=20,Hint=1),'followup lint');check('> Task :app:verifyReleaseGateAttachment' in log.splitlines(),'followup gate attachment executed')
check({(r['testClass'],r['method']) for r in result['tests'] if r['outcome']=='FAILED'}<={(r['testClass'],r['method']) for r in follow['tests'] if r['outcome']=='PASSED'},'both original failed identities pass separately')
changed=git('diff','--name-only',source,follow['sourceCommit'],'--','app/src/test').decode().splitlines();check(set(changed)=={'app/src/test/java/dev/russiancompanion/v2/RecordedCurriculumConservationScope.kt','app/src/test/java/dev/russiancompanion/v2/audit/OwnerGovernanceDecisionTest.kt','app/src/test/java/dev/russiancompanion/v2/review/UnitReviewPipelineTest.kt'},'only three declared guard test files differ')
for path in changed:
 old=git('show',source+':'+path).decode();new=git('show',follow['sourceCommit']+':'+path).decode();a=[l.strip() for l in old.splitlines() if l.strip().startswith(('assert','org.junit.Assert.assert'))];b=[l.strip() for l in new.splitlines() if l.strip().startswith(('assert','org.junit.Assert.assert'))];check(all(b.count(v)>=a.count(v) for v in a),'all original assertion lines retained')
with raw_archive('CURRENT_POLICY_EXPORT') as z:
 inv=read(D/'CURRENT_POLICY_INVENTORY.json');check(inv['sourceCommit']==source and inv['activePolicies']==28 and inv['objectivePools']==59 and inv['candidatePlacements']==79 and inv['uniqueCandidateItems']==44 and inv['policiesStillContainingPrintCaseSingleton']==27,'current production policy inventory');check(sha(z.read('CURRENT_POLICY_EXPORT.log'))==inv['exportSha256'],'production export identity');check({tuple(r['identity']) for r in inv['changedCanonicalRows']}=={('POLICY','5'),('POOL','5','0')},'only Unit5 policy/pool rows changed')
with raw_archive('SOURCE_PIN_COMPILE') as z:
 for name in ['PIN_COMPILE.log','GUARD_COMPILE.log']:check('BUILD SUCCESSFUL' in z.read(name).decode(),'successful current source compilation '+name)
with raw_archive('GENERATION') as z:
 prep=read(D/'GENERATION_PREPARATION.json')
 for name in ['CANDIDATE_1.log','CANDIDATE_2.log']:
  text=z.read(name).decode();check('CANONICAL_SOURCE_DIGEST '+prep['newDigest']+' REAL_ROOT_MATCH '+prep['newDigest'] in text,'canonical Kotlin digest');check('COMPLETE 153 files' in text,'authentic renderer completion')
check(read(D/'FINAL_CANONICAL_DIGEST.json')['canonicalKotlinDigest']==prep['newDigest'] and read(D/'FINAL_CANONICAL_DIGEST.json')['sourceCommit']==follow['sourceCommit'],'canonical digest unchanged by guard reconciliation')
check(len(read(D/'GENERATION_RESULTS.json'))==3 and all(v['exitCode']==0 for v in read(D/'GENERATION_RESULTS.json')),'both JVM exits')
idx=read(D/'HISTORICAL_PRIOR_CURRENT_INDEX.json');check(sha((D/idx['archive']).read_bytes())==idx['archiveSha256'],'historical archive digest');check(len(idx['files'])==155 and sum(v['changed'] for v in idx['files'])==95,'155/95/60 inventory');current_files=True
with zipfile.ZipFile(D/idx['archive']) as z:
 check(z.testzip() is None and len(z.namelist())==len(set(z.namelist()))==95,'historical95 CRC')
 for row in idx['files']:
  old=git('show',idx['originalCommit']+':'+row['path']);new=git('show',source+':'+row['path']);check(sha(old)==row['oldSha256'] and sha(new)==row['newSha256'],'source-bound generated identities');permitted=proposal['exactPermittedTokenReplacements']; expected=old
  for a,b in permitted.items():
   if a in [prep['oldDigest'], next(k for k in permitted if k=='bbd8919abe7d6f4968440b9ee9d60812959ad148c121428644103ad76eee7880')] or row['path'].startswith('review/unit-5/') or row['path'].endswith('FINAL_AUDIT_PREPARATION_MANIFEST.json') or 'READINESS_RECONCILIATION' in row['path']:expected=expected.replace(a.encode(),b.encode())
  check(new==expected,'only reviewed identity tokens; every other byte exact')
  if row['changed']:check(z.read(row['path'])==old,'archived prior bytes')
  if row['path'].endswith(('_REVIEW_WORKSHEET.tsv','_OWNED_EDITION_MAPPING_WORKSHEET.tsv')):check(not row['changed'],'worksheets untouched')
  current_files &= sha((R/row['path']).read_bytes())==row['newSha256']
for path,h in read(D/'PRESERVED_PRECEDING_SCOPES.json').items():check(sha((R/path).read_bytes())==h,'preserved earlier evidence')
for path,h in read(D/'CONSERVATION.json')['original66ArchiveHashes'].items():
 check(sha((R/path).read_bytes())==h,'original archive hash')
 with zipfile.ZipFile(R/path) as z:check(z.testzip() is None,'original archive CRC')
for path,tree in context['inputTrees'].items():check(git('rev-parse',source+':'+path).decode().strip()==tree,'historical input Git tree')
candidate=json.loads(git('show',source+':release/acceptance/candidate.json'));check(candidate['sourceDigest']==prep['newDigest'] and candidate['frozen'] is False and candidate['sourceCommit'] is None and candidate['artifact']=={'status':'NOT_RECORDED'} and candidate['publicRelease']=='NOT_APPROVED','candidate remains unapproved')
con=read(D/'CONSERVATION.json');asset='app/src/main/assets/content/v2-curriculum.json';before=git('show',con['comparisonCommit']+':'+asset);after=git('show',source+':'+asset);rep=con['exactSingleRawByteSubstitution'];check(before.count(rep['before'].encode())==1 and before.replace(rep['before'].encode(),rep['after'].encode())==after,'only one raw curriculum policy ID changed')
check(sha(before)==con['rawAssetSha256Before'] and sha(after)==con['rawAssetSha256After'],'raw asset identity')
p='app/src/test/java/dev/russiancompanion/v2/Unit5GrammarReviewTest.kt';check(git('show',read(D.parent/'CONFIRMED_BASELINE/CONTEXT.json')['sourceCommit']+':'+p)==git('show',source+':'+p),'confirmed baseline regression file unchanged')
p='app/src/test/java/dev/russiancompanion/v2/Unit5CumulativeReviewTest.kt';check(git('show',con['comparisonCommit']+':'+p).split(b'    companion object')[0]==git('show',source+':'+p).split(b'    companion object')[0],'all original Unit5 method/assertion bytes unchanged')
check(read(D/'CLASS_SCOPES.json')['historicalFixtureControlMethods']==45,'explicit historical controls distinguished')
for path,h in read(D.parent/'SHA256_INITIAL_CORRECTION.json').items():check(sha((D.parent/path).read_bytes())==h,'immutable initial correction evidence '+path)
print(json.dumps(dict(status='UNIT5_BOUNDED_REPAIR_WITH_SEPARATE_GUARD_FOLLOWUP_VERIFIED',testedSource=source,counts=result['testCounts'],classes=21,followupSource=follow['sourceCommit'],followupCounts=follow['testCounts'],followupClasses=2,freshAllPass144=False,followupHeadMatchesInputTrees=all(git('rev-parse','HEAD:'+k).decode().strip()==v for k,v in fc['inputTrees'].items()),canonicalKotlinDigest=prep['newDigest'],twoFreshJvmRendersIdentical=True,prior95Unchanged60Current155Verified=True,original66ArchivesAndEarlierScopeHashesVerified=True,xmlEventsDiscoveryExact=True,currentHeadMatchesInputTrees=all(git('rev-parse','HEAD:'+p).decode().strip()==v for p,v in context['inputTrees'].items()),currentHeadMatches155Files=current_files,completeSuite=False,findingsClosed=0,independentAudit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST'),indent=2))
