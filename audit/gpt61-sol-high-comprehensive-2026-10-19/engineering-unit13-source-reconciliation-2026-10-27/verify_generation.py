"""Read-only verification of two real renders, applied identity deltas and conservation."""
from pathlib import Path
import json,hashlib,zipfile,subprocess,re
D=Path(__file__).resolve().parent;R=Path('/workspace/russian-companion');sha=lambda b:hashlib.sha256(b).hexdigest();read=lambda n:json.loads((D/n).read_text());git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip();blob=lambda s,n:subprocess.check_output(['git','show',s+':'+n],cwd=R)
c=read('CONSERVATION.json');ctx=read('GENERATION_PREPARATION.json');p=read('PROPOSAL_REVIEW.json');applied=read('APPLIED_RECORDS.json');before=ctx['sourceCommit'];S=ctx['actualVerifiedProductionSource']
assert S=='bd00e5082c9f31d2ae3b379dd6204dd3b28e4475' and git('rev-parse','HEAD:app/src/main')==c['unchangedProductionTree']==git('rev-parse',S+':app/src/main')
assert (p['renderedFiles'],p['proposedFiles'],p['changedFiles'],p['unchangedFiles'])==(153,155,95,60)
assert ctx['newDigest']==applied['repositorySourceDigest']=='3c0acbb95297cf10f08da2bd8391463067eece22fec785158b5d2680d37dc2ba'
assert applied['packContentReviewDigest']=='15c6ae393767cb7ffc190882867bfad923c9e81b07d0f4285cf0a678f2489280'
assert read('AFTER_APPLY_DIGEST.json')['repositorySourceDigest']==ctx['newDigest']
assert read('DIGEST_IDENTITY_CORRECTION.json')['historicalPacketPreserved']
with zipfile.ZipFile(D/'RAW_GENERATION.zip') as z:
 assert z.testzip() is None;idx=read('RAW_GENERATION_INDEX.json');assert set(idx)==set(z.namelist())
 for n,v in idx.items():assert len(z.read(n))==v['bytes'] and sha(z.read(n))==v['sha256'],n
 a=sorted(n.removeprefix('CANDIDATE_1/') for n in z.namelist() if n.startswith('CANDIDATE_1/'));b=sorted(n.removeprefix('CANDIDATE_2/') for n in z.namelist() if n.startswith('CANDIDATE_2/'));assert a==b and len(a)==153
 for n in a:assert z.read('CANDIDATE_1/'+n)==z.read('CANDIDATE_2/'+n)==(R/n).read_bytes(),n
 results=read('GENERATION_RESULTS.json');assert len(results)==3 and all(x['exitCode']==0 for x in results)
 for k in [1,2]:
  log=z.read(f'TOOLING/CANDIDATE_{k}.log').decode();rows=re.findall(r'^VALIDATED (\d+) 5 ([0-9a-f]{40})$',log,re.M);assert [int(x[0]) for x in rows]==list(range(1,31))
  assert 'COMPLETE 153 files' in log and f'REAL_ROOT_MATCH {ctx["newDigest"]}' in log
 for key,val in ctx['inputTrees'].items():assert ctx['compiledSourceEvidence']['inputTrees'][key]==val
 assert ctx['compiledSourceEvidence']['exitCode']==0 and ctx['compiledSourceEvidence']['JUnitMethodsExecuted']==0
 changes={x['field']:(x['before'],x['after']) for x in p['identityTokenReplacements']};assert len(changes)==5
 with zipfile.ZipFile(D/'HISTORICAL_PRIOR_CURRENT.zip') as hist:
  assert hist.testzip() is None;hi=read('HISTORICAL_PRIOR_CURRENT_INDEX.json');assert len(hi)==95 and set(hist.namelist())=={x['path'] for x in hi}
  for x in p['files']:
   n=x['path'];old=blob(before,n);new=(R/n).read_bytes();assert sha(old)==x['oldSha256'] and sha(new)==x['newSha256']
   expected=old
   replacements={k:v for k,v in changes.items() if k in ['packContentReviewDigest','repositorySourceDigest']} if n.endswith('V2_CURRICULUM_GAP_CLOSURE_DOSSIER_v1.0.md') else changes
   for prev,nxt in replacements.values():expected=expected.replace(prev.encode(),nxt.encode())
   assert expected==new,n
   if x['changed']:assert hist.read(n)==old
   else:assert old==new
   if n.endswith('_WORKSHEET.tsv'):assert not x['changed']
for n,h in applied['currentRecordHashes'].items():assert sha((R/n).read_bytes())==h,n
assert len(applied['currentRecordHashes'])==155
for n,h in c['immutablePriorEvidence'].items():
 assert sha(blob(before,n) if n in applied['currentRecordHashes'] else (R/n).read_bytes())==h,n
for n,h in c['allPriorTestHashes'].items():
 if n not in c['changedToolingFiles']:assert sha((R/n).read_bytes())==h,n
assert len(c['allPriorTestHashes'])==275 and len(c['changedToolingFiles'])==3
for n,h in c['archives'].items():
 assert sha((R/n).read_bytes())==h,n
 with zipfile.ZipFile(R/n) as z:assert z.testzip() is None,n
assert len(c['archives'])==74
with zipfile.ZipFile(D/'ORIGINAL_TOOLING.zip') as z:
 assert z.testzip() is None
 for n in c['changedToolingFiles']:assert sha(z.read(n))==c['originalToolingHashes'][n] and z.read(n)==blob(c['parentCommit'],n)
 n='app/src/test/resources/reviewed-curriculum-cyrillic.json';old=json.loads(z.read(n));new=json.loads((R/n).read_text());kernel='src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt'
 assert len(new['files'])==19 and sum(len(x['bindings']) for x in new['files'])==292
 for x,y in zip(old['files'],new['files']):
  if x['path']==kernel:assert {k:v for k,v in x.items() if k!='sha256'}=={k:v for k,v in y.items() if k!='sha256'} and y['sha256']==sha((R/('app/'+kernel)).read_bytes())
  else:assert x==y
 assert new['subsequentEngineeringReferenceReviews'][:-1]==old['subsequentEngineeringReferenceReviews']
 last=new['subsequentEngineeringReferenceReviews'][-1];assert last['sourceCommit']==S and not last['semanticAcceptance'] and not last['rightsAcceptance']
for u in range(1,31):
 n=f'review/unit-{u}/UNIT_{u}_REVIEW_MANIFEST.json';old=json.loads(blob(before,n));now=json.loads((R/n).read_text());assert now['curriculumSourceCommit']==(S if u==13 else old['curriculumSourceCommit'])
candidate=json.loads((R/'release/acceptance/candidate.json').read_text());assert candidate['frozen']==False and candidate['sourceCommit'] is None and candidate['publicRelease']=='NOT_APPROVED'
assert c['all48Findings']=='OPEN' and c['physicalAcceptance']=='OUTSTANDING_LAST'
if (D/'SHA256.json').exists():
 for n,h in read('SHA256.json').items():assert sha((D/n).read_bytes())==h,n
print(json.dumps(dict(verified=True,productionSource=S,renderSource=before,twoRealRendersFilesEach=153,validatedPackagesPerRender=30,appliedIdentityOnlyRecords=95,unchangedWorksheets=60,sourceChoicesChangedOnlyUnit13=True,historicalVersionsPreserved=95,archivesShaCrc=74,oldTestsUnchangedExceptThreeToolingReferences=True,referenceFiles=19,referenceBindings=292,repositorySourceDigest=ctx['newDigest'],packContentReviewDigest=applied['packContentReviewDigest'],generationJUnitMethods=0,affectedRuntime='PENDING',all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST')))
