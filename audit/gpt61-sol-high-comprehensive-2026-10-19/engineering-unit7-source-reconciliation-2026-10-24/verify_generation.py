"""Read-only validation of authentic rendered records and historical conservation."""
from pathlib import Path
import json,hashlib,subprocess,zipfile,re,sys
D=Path(sys.argv[1]).resolve() if len(sys.argv)>1 else Path(__file__).resolve().parent;R=next(p for p in D.parents if (p/'AGENTS.md').exists());sha=lambda b:hashlib.sha256(b).hexdigest();read=lambda n:json.loads((D/n).read_text());blob=lambda c,p:subprocess.check_output(['git','show',c+':'+p],cwd=R)
p=read('PROPOSAL_REVIEW.json');c=read('GENERATION_PREPARATION.json');applied='531fa3e9018d8165b10720126f41133dafe4f1cf';assert p['sourceCommit']==c['sourceCommit']=='d045c439a60582c40113a4d0c24517f6b1fc7849'
assert c['actualCurriculumChangeCommit']=='2e2ebcebc81f54c04db7f0ac24e99374674c97c5';assert c['newDigest']=='d48aa7e0090bd30422172f7b7c87fe52fe2859fa3c5be9b0851d2f9704f30e3a'
renders=read('RENDER_OUTPUT_SHA256.json');assert renders['CANDIDATE_1']==renders['CANDIDATE_2'] and len(renders['CANDIDATE_1'])==153
results=read('GENERATION_RESULTS.json');assert len(results)==3 and all(x['exitCode']==0 for x in results)
assert sha((D/'GeneratePendingEvidence.java').read_bytes())==c['generatorSha256']
for i,index in enumerate(c['compiledClassDirectories'],1):assert sha((D/f'COMPILED_INDEX_{i}.json').read_bytes())==index['indexSha256']
with zipfile.ZipFile(D/'RAW_GENERATION.zip') as z:
 assert z.testzip() is None
 ix=read('RAW_GENERATION_INDEX.json');assert sorted(z.namelist())==sorted(x['path'] for x in ix)
 for x in ix:assert sha(z.read(x['path']))==x['sha256'] and len(z.read(x['path']))==x['bytes']
 for name in ['CANDIDATE_1','CANDIDATE_2']:
  text=z.read(name+'.log').decode();assert text.count('\nVALIDATED ')==30 and 'COMPLETE 153 files' in text
  assert 'CANONICAL_SOURCE_DIGEST '+c['newDigest']+' REAL_ROOT_MATCH '+c['newDigest'] in text
  assert 'VALIDATED 7 5 '+c['actualCurriculumChangeCommit'] in text
assert len(p['files'])==155 and p['changedFiles']==95 and p['unchangedFiles']==60 and p['twoFreshJvmRendersIdentical']
replace={r['before']:r['after'] for r in p['identityTokenReplacements']};assert len(replace)==5
with zipfile.ZipFile(D/'HISTORICAL_PRIOR_CURRENT.zip') as z:
 assert z.testzip() is None and len(z.namelist())==95
 for row in p['files']:
  before=blob(p['sourceCommit'],row['path']);after=blob(applied,row['path']);expected=before
  assert sha(before)==row['oldSha256'] and sha(after)==row['newSha256']
  assert len(before)==row['oldBytes'] and len(after)==row['newBytes']
  assert subprocess.check_output(['git','rev-parse',p['sourceCommit']+':'+row['path']],cwd=R,text=True).strip()==row['originalGitBlob']
  for a,b in replace.items():expected=expected.replace(a.encode(),b.encode())
  assert expected==after,('Unexpected non-identity bytes',row['path'])
  if row['changed']:assert z.read(row['path'])==before
  if row['path'].endswith('_WORKSHEET.tsv'):assert before==after
  if row['rendered']:assert renders['CANDIDATE_1'][row['path']]==sha(after)
index=read('HISTORICAL_PRIOR_CURRENT_INDEX.json');assert index['archiveSHA256']==sha((D/'HISTORICAL_PRIOR_CURRENT.zip').read_bytes())
candidate=json.loads(blob(applied,'release/acceptance/candidate.json'));old=json.loads(blob(p['sourceCommit'],'release/acceptance/candidate.json'));assert candidate==dict(old,sourceDigest=c['newDigest'])
assert candidate['frozen'] is False and candidate['sourceCommit'] is None and candidate['publicRelease']=='NOT_APPROVED' and candidate['artifact']['status']=='NOT_RECORDED'
# The post-render cleanup moves the new Unit7 choice to its sole existing branch.
path='app/src/test/java/dev/russiancompanion/v2/review/Unit1ReviewPackage.kt';a=blob(p['sourceCommit'],path).decode();b=blob(applied,path).decode();fun=lambda s:s.split('internal fun curriculumCommitForUnit(unitNumber: Int): String =',1)[1].split('\n\n',1)[0];choices=lambda s:{str(n):next((v for k,v in re.findall(r'if \(unitNumber == (\d+)\) (\w+)',fun(s)) if int(k)==n),'UNCHANGED_DEFAULT') for n in range(1,31)}
lookup=read('SOURCE_LOOKUP_EQUIVALENCE.json');assert choices(a)==choices(b)==lookup['sourceChoices'] and len(re.findall(r'if \(unitNumber == 7\)',fun(b)))==1
with zipfile.ZipFile(D/'ORIGINAL_TOOLING.zip') as z:
 assert z.testzip() is None
 for name,h in read('TOOLING_PREPARATION.json')['originalToolingSha256'].items():
  assert sha(z.read(name))==h
  if name.endswith('.kt'):
   current=blob(applied,name).decode();lines=iter(s.strip() for s in current.splitlines());assert all(any(x==s.strip() for x in lines) for s in z.read(name).decode().splitlines() if s.strip().startswith(('assertEquals(','assertTrue(','assertFalse(','assertNotEquals(')))
ref='app/src/test/resources/reviewed-curriculum-cyrillic.json';old=json.loads(blob(p['sourceCommit'],ref));new=json.loads(blob(applied,ref));assert not new['semanticAcceptance'] and not new['rightsAcceptance'];assert len(new['files'])==19 and sum(len(v['bindings']) for v in new['files'])==292
# p.source is already the reviewed resource; no later row or binding edits are allowed.
assert old==new
preserved=read('PRESERVED_PRECEDING_SCOPES.json')
for name,value in preserved.items():assert sha((R/name).read_bytes())==value,name
archives=read('ORIGINAL_ARCHIVE_CONSERVATION.json');assert len(archives)==66
for row in archives:
 assert sha((R/row['path']).read_bytes())==row['sha256']
 with zipfile.ZipFile(R/row['path']) as z:assert z.testzip() is None
print(json.dumps(dict(generationVerified=True,generationSource=p['sourceCommit'],appliedSource=applied,twoIdenticalAuthenticRenders=True,renderedFiles=153,reviewedRecords=155,changedIdentityOnly=95,unchangedWorksheets=60,originalArchivesShaCrc=66,priorScopesFilesPreserved=len(preserved),sourceDigest=c['newDigest'],unit7Implementation=c['actualCurriculumChangeCommit'],postRenderLookupEquivalentForAll30=True,additionalJUnitMethodsExecuted=0,qualifiedAcceptance=False)))
