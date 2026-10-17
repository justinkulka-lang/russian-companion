from pathlib import Path
import json,subprocess,re,hashlib
r=Path('/workspace/russian-companion');d=r/'audit/independent-gpt61-2026-10-12/ia003-unit13-history-transition';base='8e2c6f1a8d3d4b6d1f098bc6e347d36e92e10352';ds=json.loads(Path('/workspace/setup/u27-digests.json').read_text());old=lambda p:json.loads(subprocess.check_output(['git','show',base+':'+p],cwd=r,text=True));replacements={};units=[]
for n in range(1,31):
 path=f'review/unit-{n}/UNIT_{n}_REVIEW_MANIFEST.json';a=old(path);b=json.loads((r/path).read_text());assert a['mappingPackageDigest']==b['mappingPackageDigest']
 if n!=13:assert a['contentReviewDigest']==b['contentReviewDigest'];assert a['reviewPackageDigest']==b['reviewPackageDigest']
 else:
  assert a['contentReviewDigest']!=b['contentReviewDigest'];assert b['curriculumSourceCommit']=='41ef8eebc5f7ff25cd30cb19164662dfab858f66'
  for k in ['contentReviewDigest','reviewPackageDigest']:replacements[a[k]]=b[k]
 units.append({'unit':n,'changedFields':sorted(k for k in a.keys()|b.keys() if a.get(k)!=b.get(k)),'contentReviewDigest':b['contentReviewDigest'],'mappingPackageDigest':b['mappingPackageDigest']})
oldpack=old('review/unit-13/UNIT_13_REVIEW_MANIFEST.json')['packContentReviewDigest'];replacements[oldpack]=ds['pack'];fixtures=[]
for f in (r/'app/src/test').rglob('*.kt'):
 s=f.read_text();before=s
 for a,b in replacements.items():s=s.replace(a,b)
 if s!=before:fixtures.append({'path':str(f.relative_to(r)),'beforeSha256':hashlib.sha256(before.encode()).hexdigest(),'afterSha256':hashlib.sha256(s.encode()).hexdigest()});f.write_text(s)
f=r/'release/acceptance/candidate.json';s=f.read_text();f.write_text(s.replace(json.loads(s)['sourceDigest'],ds['source']))
f=r/'audit/curriculum-readiness/V2_CURRICULUM_GAP_CLOSURE_DOSSIER_v1.0.md';s,n=re.subn(r'(\*\*Repository source digest:\*\* `)[a-f0-9]{64}(`)',lambda x:x[1]+ds['source']+x[2],f.read_text());assert n==1;f.write_text(s.replace(oldpack,ds['pack']))
(d/'BINDING_LEDGER.json').write_text(json.dumps({'baselineCommit':base,'implementationCommit':'41ef8eebc5f7ff25cd30cb19164662dfab858f66','digests':ds,'units':units,'digestReplacements':replacements,'fixtureDigestLiteralChanges':fixtures,'all30MappingDigestsUnchanged':True,'other29ContentAndPackageDigestsUnchanged':True,'qualifiedAcceptance':False},indent=2)+'\n');print(json.dumps(ds))
