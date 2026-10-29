"""Read-only Unit12 binding/conservation/runtime evidence verification; no generators/Android tasks."""
from pathlib import Path
from datetime import datetime,timezone
from collections import Counter
import json,hashlib,subprocess,zipfile,xml.etree.ElementTree as ET
D=Path(__file__).resolve().parent;R=next(p for p in D.parents if (p/'.git').exists());sha=lambda b:hashlib.sha256(b).hexdigest();git=lambda *a:subprocess.check_output(['git',*a],cwd=R)
manifest=json.loads((D/'SHA256.json').read_text());assert set(manifest)=={p.relative_to(D).as_posix() for p in D.rglob('*') if p.is_file() and p.name!='SHA256.json'}
tracked=set(git('ls-files').decode().splitlines())
for n,h in manifest.items():assert str((D/n).relative_to(R)) in tracked and sha((D/n).read_bytes())==h,n
p=json.loads((D/'PROPOSAL_REVIEW.json').read_text());assert p['sourceCommit']=='650334e75a28a3dda1c6c9352b5db9f31c1b60a7' and p['actualVerifiedProductionSource']=='ca0fdaeca8de81ab6a5d4935fbcdcb068c4af913'
assert p['twoFreshJvmRendersIdentical'] and [p[k] for k in ['renderedFiles','proposedFiles','changedFiles','unchangedFiles']]==[153,155,95,60]
applied=json.loads((D/'APPLIED_COMMIT.json').read_text())['commit'];fields=p['identityTokenReplacements'];replacements={x['before']:x['after'] for x in fields};assert len(replacements)==5
with zipfile.ZipFile(D/'RAW_GENERATION.zip') as z,zipfile.ZipFile(D/'HISTORICAL_PRIOR_CURRENT.zip') as history:
 assert z.testzip() is None and history.testzip() is None and len(history.namelist())==95
 index=json.loads((D/'RAW_GENERATION_INDEX.json').read_text());assert set(z.namelist())==set(index)
 for n,v in index.items():assert sha(z.read(n))==v['sha256'] and len(z.read(n))==v['bytes']
 for row in p['files']:
  n=row['path'];old=git('show',p['sourceCommit']+':'+n);new=git('show',applied+':'+n);assert sha(old)==row['oldSha256'] and sha(new)==row['newSha256']
  assert git('rev-parse',p['sourceCommit']+':'+n).decode().strip()==row['originalGitBlob']
  expected=old
  active={x['before']:x['after'] for x in fields if x['field'] in ['packContentReviewDigest','repositorySourceDigest']} if n.endswith('V2_CURRICULUM_GAP_CLOSURE_DOSSIER_v1.0.md') else replacements
  for a,b in active.items():expected=expected.replace(a.encode(),b.encode())
  assert new==expected,('Unexpected byte change',n)
  if row['changed']:assert history.read(n)==old
  else:assert n not in history.namelist() and old==new
  if row['rendered']:assert z.read('CANDIDATE_1/'+n)==z.read('CANDIDATE_2/'+n)==new
 for label in ['CANDIDATE_1.log','CANDIDATE_2.log']:
  log=z.read(label).decode();assert sum(x.startswith('VALIDATED ') for x in log.splitlines())==30 and 'COMPLETE 153 files' in log
 assert b'84b6fda72017ec2dfc2b4add6c0e82b1a1e9f40769773f18d50caee4cf7a2355' in z.read('DIGEST.log')
# The historical source citation is not a current source binding and must not be retconned.
n='audit/curriculum-readiness/V2_CURRICULUM_GAP_CLOSURE_DOSSIER_v1.0.md'
a=git('show',p['sourceCommit']+':'+n).decode();b=git('show',applied+':'+n).decode();assert [l for l in a.splitlines() if 'positive-existence implementation' in l]==[l for l in b.splitlines() if 'positive-existence implementation' in l]
candidate=json.loads(git('show',applied+':release/acceptance/candidate.json'));assert candidate['sourceDigest']=='84b6fda72017ec2dfc2b4add6c0e82b1a1e9f40769773f18d50caee4cf7a2355' and candidate['frozen'] is False and candidate['sourceCommit'] is None and candidate['artifact']['status']=='NOT_RECORDED' and candidate['publicRelease']=='NOT_APPROVED'
con=json.loads((D/'CONSERVATION.json').read_text())
for n,h in con['immutablePriorEvidence'].items():assert sha((R/n).read_bytes())==h,n
assert len(con['archives'])==73
for n,h in con['archives'].items():
 assert sha((R/n).read_bytes())==h
 with zipfile.ZipFile(R/n) as z:assert z.testzip() is None
with zipfile.ZipFile(D/'ORIGINAL_TOOLING.zip') as z:
 assert z.testzip() is None
 for row in json.loads((D/'ORIGINAL_TOOLING_INDEX.json').read_text()):
  n=row['path'];assert sha(z.read(n))==row['sha256'] and z.read(n)==git('show',row['sourceCommit']+':'+n)
for unit in range(1,31):
 n=f'review/unit-{unit}/UNIT_{unit}_REVIEW_MANIFEST.json';before=json.loads(git('show',p['sourceCommit']+':'+n));after=json.loads(git('show',applied+':'+n));assert (after['curriculumSourceCommit']=='ca0fdaeca8de81ab6a5d4935fbcdcb068c4af913') if unit==12 else (after['curriculumSourceCommit']==before['curriculumSourceCommit'])
assert json.loads((D/'AFTER_APPLY_DIGEST.json').read_text())['digest']=='84b6fda72017ec2dfc2b4add6c0e82b1a1e9f40769773f18d50caee4cf7a2355'
gen=json.loads((D/'GENERATION_PREPARATION.json').read_text());compiled=json.loads((D/'COMPILE_CONTEXT.json').read_text());assert gen['sourceCommit']==compiled['sourceCommit']==p['sourceCommit'] and compiled['exitCode']==0 and compiled['JUnitMethodsExecuted']==0
assert git('rev-parse',gen['sourceCommit']+':app/src/main')==git('rev-parse',gen['actualVerifiedProductionSource']+':app/src/main')
for n,h in compiled['inputTrees'].items():assert git('rev-parse',compiled['sourceCommit']+':'+n).decode().strip()==h
assert gen['newDigest']=='84b6fda72017ec2dfc2b4add6c0e82b1a1e9f40769773f18d50caee4cf7a2355'
with zipfile.ZipFile(D/'RAW_GENERATION.zip') as z:
 assert z.read('PROPOSED_EXTRAS/release/acceptance/candidate.json')==git('show',applied+':release/acceptance/candidate.json')
 assert z.read('PROPOSED_EXTRAS/audit/curriculum-readiness/V2_CURRICULUM_GAP_CLOSURE_DOSSIER_v1.0.md')==git('show',applied+':audit/curriculum-readiness/V2_CURRICULUM_GAP_CLOSURE_DOSSIER_v1.0.md')
assert sha((D/'AFTER_APPLY_DIGEST.log').read_bytes())==json.loads((D/'AFTER_APPLY_DIGEST.json').read_text())['stdoutSha256']
prep=json.loads((D/'PREPARATION.json').read_text())
allowedTooling={r['path'] for r in prep['originalTooling']}
for n,h in prep['preexistingTestFiles'].items():
 if n not in allowedTooling:assert sha(git('show',applied+':'+n))==h,n
reports=[]
for attempt in (json.loads((D/'ATTEMPTS.json').read_text()) if (D/'ATTEMPTS.json').exists() else []):
 A=D/attempt['directory'];source=attempt['sourceCommit'];counts=attempt['testCounts']
 with zipfile.ZipFile(A/'RAW.zip') as z:
  assert z.testzip() is None;index=json.loads((A/'RAW_INDEX.json').read_text());assert set(z.namelist())==set(index)
  for n,v in index.items():assert sha(z.read(n))==v['sha256'] and len(z.read(n))==v['bytes']
  phases={}
  for mode in ['DISCOVERY','REGRESSION']:
   c=json.loads(z.read(mode+'/CONTEXT.json'));s=json.loads(z.read(mode+'/RESULT.json'));phase_source=c['sourceCommit'];assert phase_source==s['sourceCommit']
   # Discovery can precede an evidence-only commit: retain both real commit identities,
   # verify each root against its own source and all six inputs against BOTH sources.
   if mode=='REGRESSION':assert phase_source==source
   assert c['sourceRootTree']==git('rev-parse',phase_source+'^{tree}').decode().strip() and c['inputTrees']==s['inputTrees']
   for n,h in c['inputTrees'].items():
    assert git('rev-parse',phase_source+':'+n).decode().strip()==h
    assert git('rev-parse',source+':'+n).decode().strip()==h
   assert len(c['exclusions'])==4 and sum(c['exclusions'].values())==16 and c['selectors']==attempt['selectors']
   assert s['classes']==attempt['classes'] and s['testCounts']['reported']==counts['reported']
   assert sha(z.read('run.py'))==c['runnerSha256']
   for n,h in c['initScriptSha256'].items():assert sha(z.read('INIT/'+Path(n).name))==h
   rows={};start=datetime.fromisoformat(c['startedUtc'])
   for n in z.namelist():
    if n.startswith(mode+'/XML/') and n.endswith('.xml'):
     suite=ET.fromstring(z.read(n));t=datetime.fromisoformat(suite.get('timestamp').replace('Z','+00:00'));t=t.replace(tzinfo=timezone.utc) if t.tzinfo is None else t;assert t.timestamp()>=start.timestamp()-1
     for x in suite.findall('testcase'):
      key=(x.get('classname'),x.get('name'));assert key not in rows;rows[key]='FAILED' if x.find('failure') is not None else 'ERROR' if x.find('error') is not None else 'SKIPPED' if x.find('skipped') is not None else 'PASSED'
   assert rows=={(x['testClass'],x['method']):x['outcome'] for x in s['tests']}
   events=[json.loads(x) for x in z.read(mode+'/EVENTS.jsonl').splitlines()];events=[x for x in events if x['kind']=='RESULT'];assert len(events)==len(rows)==counts['reported']
   assert {(x['className'],x['name']):x['resultType'] for x in events}=={k:{'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[v] for k,v in rows.items()}
   n=Counter(rows.values());assert s['testCounts']==dict(reported=len(rows),passed=n['PASSED'],failed=n['FAILED'],errors=n['ERROR'],skipped=n['SKIPPED'])
   end=json.loads(z.read(mode+'_COMPLETION.json'));assert end['runnerExit']==s['exitCode']==int(end['gradleExit'])==int(z.read(mode+'/RUN.exit'))
   for n,h in json.loads(z.read(mode+'/SHA256.json')).items():assert sha(z.read(mode+'/'+n))==h
   phases[mode]=(rows,s)
  assert set(phases['DISCOVERY'][0])==set(phases['REGRESSION'][0]) and phases['DISCOVERY'][1]['testCounts']==dict(reported=counts['reported'],passed=0,failed=0,errors=0,skipped=counts['reported'])
  s=phases['REGRESSION'][1];assert s['testCounts']==counts and s['discoveryComparison']['sameInputTrees'] and s['discoveryComparison']['exactSameMethodSet']
  reports.append(dict(directory=attempt['directory'],sourceCommit=source,testCounts=counts,classes=s['classes'],exitCode=s['exitCode'],lint=s['lintIssuesBySeverity'],gate=s['tasks']['verifyReleaseGateAttachment']))
print(json.dumps(dict(verified=True,records=155,changed=95,worksheetsUnchanged=60,priorVersionsArchived=95,originalPlusAdditionalArchivesShaCrc=73,attempts=reports,scopesNotSummed=True,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',completeSuite=False,testsExecutedByVerifier=0)))
