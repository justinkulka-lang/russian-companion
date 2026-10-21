from pathlib import Path
import json,hashlib,csv,io,subprocess
r=Path('/workspace/russian-companion');w=Path(__file__).parent;five={4,7,8,10,15};sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
# This renders a bounded documentary update, preserving the full original in the historical archive.
def dossier(out):
 model=json.loads((out/'audit/curriculum-readiness/V2_CURRICULUM_READINESS_RECONCILIATION.json').read_text());p=r/'audit/curriculum-readiness/V2_CURRICULUM_GAP_CLOSURE_DOSSIER_v1.0.md';s=p.read_text()
 old=json.loads((r/'audit/curriculum-readiness/V2_CURRICULUM_READINESS_RECONCILIATION.json').read_text())
 for key,label in [('packContentReviewDigest','Pack content-review digest'),('repositorySourceDigest','Repository source digest')]:
  needle=f'**{label}:** `{old[key]}`';assert s.count(needle)==1;s=s.replace(needle,f'**{label}:** `{model[key]}`')
 needle='**6 unbound examples**';assert s.count(needle)==1;s=s.replace(needle,'**8 unbound examples**')
 marker='## Unit 4 orphan analysis';assert marker in s
 s=s.replace(marker,'''## Current scope of this planning dossier

The pending-packet reconciliation updates source/pack identities and mechanical reachability only. The original independent audit remains INCOMPLETE; the current 17-finding return and its engineering dispositions require qualified independent recheck. Structural completeness counters do not establish full curriculum, language, rights, media or runtime coverage. Existing historical research notes below retain their original scope and counts at those checkpoints. No reviewer fields or formal stages are approved by this update.

'''+marker,1)
 needle='The generator creates this example for obj-u4-present, but the objective\'s exampleIds include only ex-u4-verb-читать and ex-u4-verb-делать. It is packaged but unreachable from any unit chain.'
 assert s.count(needle)==2;s=s.replace(needle,'The current objective and session steps do not reference this retained example. It remains packaged but unreachable from any unit chain; the later present-form teaching adds other exact-form examples.')
 s+='''

## Current positive-existence checkpoint: retained Unit8 absence examples

`ex-u8-availability-2` and `ex-u8-availability-3` remain byte-identical in the catalog with their original provenance. The positive-existence implementation `fe919656bfa2775b5cc2c22f2496d4258343cfea` removes their use in the current Unit8 availability chain. The current total is **8 unbound examples** and **14 unbound check records**: the six previously disclosed examples plus these two historical absence examples. No examples or histories were deleted, rebound to reduce the count, or reclassified as current assessed evidence. Qualified source/rights/language review, the independent comprehensive audit and physical acceptance remain outstanding. Physical Galaxy S26 Ultra acceptance stays last; no release approval.
'''
 target=out/p.relative_to(r);target.write_text(s)
for label in ['CANDIDATE_1','CANDIDATE_2']:
 out=w/label;assert len([p for p in out.rglob('*') if p.is_file()]) in [153,154];dossier(out)
a=w/'CANDIDATE_1';b=w/'CANDIDATE_2';paths=sorted(p.relative_to(a).as_posix() for p in a.rglob('*') if p.is_file());assert len(paths)==154 and paths==sorted(p.relative_to(b).as_posix() for p in b.rglob('*') if p.is_file())
file_rows=[]
for path in paths:
 assert (a/path).read_bytes()==(b/path).read_bytes(),path
 file_rows.append(dict(path=path,oldSha256=sha(r/path),newSha256=sha(a/path),oldBytes=(r/path).stat().st_size,newBytes=(a/path).stat().st_size,changed=(r/path).read_bytes()!=(a/path).read_bytes()))
units=[]
for n in range(1,31):
 path=f'review/unit-{n}/UNIT_{n}_REVIEW_MANIFEST.json';old=json.loads((r/path).read_text());new=json.loads((a/path).read_text());delta=[k for k in new if new[k]!=old[k]]
 assert new['unitReviewStatus']==old['unitReviewStatus']=='IMPLEMENTED_PENDING_FINAL_AUDIT'
 for key in ['installState','readiness','mappingStatus']:assert new['unit'][key]==old['unit'][key]
 assert new['mappingPackageDigest']==old['mappingPackageDigest'] and new['unresolved']==[]
 if n not in five:assert set(delta)=={'packContentReviewDigest','repositorySourceDigest'},(n,delta)
 else:assert all(k in delta for k in ['curriculumSourceCommit','contentReviewDigest','reviewPackageDigest'])
 oldrecords={x['id']:x for x in old['records']};newrecords={x['id']:x for x in new['records']}
 row=dict(unit=n,changedManifestFields=delta,oldSource=old['curriculumSourceCommit'],newSource=new['curriculumSourceCommit'],oldContent=old['contentReviewDigest'],newContent=new['contentReviewDigest'],oldPackage=old['reviewPackageDigest'],newPackage=new['reviewPackageDigest'],oldCounts=old['counts'],newCounts=new['counts'],addedRecordIds=sorted(newrecords.keys()-oldrecords.keys()),removedRecordIds=sorted(oldrecords.keys()-newrecords.keys()),changedRecordIds=sorted(k for k in oldrecords.keys()&newrecords.keys() if oldrecords[k]!=newrecords[k]),oldEdges=len(old['edges']),newEdges=len(new['edges']))
 mapping=f'review/unit-{n}/UNIT_{n}_OWNED_EDITION_MAPPING_WORKSHEET.tsv'
 if (r/mapping).read_bytes()!=(a/mapping).read_bytes():
  assert n==8
  parse=lambda p:list(csv.DictReader(io.StringIO('\n'.join(x for x in p.read_text().splitlines() if x and not x.startswith('#'))),delimiter='\t',quoting=csv.QUOTE_NONE))
  oldmap,newmap=parse(r/mapping),parse(a/mapping);assert len(oldmap)==len(newmap)
  changes=[]
  for x,y in zip(oldmap,newmap):
   fields=[k for k in x if x[k]!=y[k]]
   if fields:assert fields==['app_title'] and y['app_title']=='Recognize supplied positive existence statements';changes.append(x)
  assert len(changes)==2

 worksheet=f'review/unit-{n}/UNIT_{n}_REVIEW_WORKSHEET.tsv'
 for p in [r/worksheet,a/worksheet]:
  for x in csv.DictReader(io.StringIO('\n'.join(line for line in p.read_text().splitlines() if line and not line.startswith('#'))),delimiter='\t',quoting=csv.QUOTE_NONE):
   for key in ['disposition','correction_comment','reviewer_identity','review_date','reviewed_version_digest','reviewer_qualification']:assert x[key]=='',(p,key)
 units.append(row)
owner='audit/owner-governance/FINAL_AUDIT_PREPARATION_MANIFEST.json';old=json.loads((r/owner).read_text());new=json.loads((a/owner).read_text());assert set(k for k in new if new[k]!=old[k])=={'packContentReviewDigest','repositorySourceDigest','curriculumSourceCommits','unitDispositions'}
for k in ['modelVersion','date','varianceDecision','mappingDecision']:assert old[k]==new[k]==''
assert old['disposition']==new['disposition']=='NOT_RECORDED' and old['evidenceRecords']==new['evidenceRecords']==[]
for x,y in zip(old['unitDispositions'],new['unitDispositions']):assert x['unitNumber']==y['unitNumber'] and x['disposition']==y['disposition']==''
readiness='audit/curriculum-readiness/V2_CURRICULUM_READINESS_RECONCILIATION.json';oldmodel=json.loads((r/readiness).read_text());model=json.loads((a/readiness).read_text())
for k in ['publicRelease','candidateFrozen','candidateArtifact','candidateSourceCommit','releaseDecision','retainedEvidence','at031','varianceProposalStatus','varianceResolutionStatus']:assert model[k]==oldmodel[k],k
assert model['publicRelease']==model['releaseDecision']=='NOT_APPROVED' and not model['candidateFrozen'] and model['candidateArtifact']=='NOT_RECORDED' and model['candidateSourceCommit']=='null'
assert model['internetPermissionDeclaredInMergedDebugManifest']=='NOT_OBSERVED_IN_SOURCE_SNAPSHOT'
for x,y in zip(oldmodel['units'],model['units']):
 for k in ['unitReviewStatus','reviewerIdentity','reviewerQualification','signedReviewedDigest','mappingStatus','installState','readiness']:assert x[k]==y[k],(x['unitNumber'],k)
record=dict(kind='REVIEWED_PENDING_EVIDENCE_PROPOSAL_NOT_APPROVAL',generatorSourceCommit=json.loads((w/'CANDIDATE_1_CONTEXT.json').read_text())['sourceCommit'],twoFreshJvmRenderingsByteIdentical=True,files=file_rows,units=units,twentyNineMappingWorksheetsByteIdentical=True,unit8MappingOnlyTwoAppTitlesChanged=True,allMappingFactsAndReviewCellsUnchanged=True,allReviewerDispositionFieldsRemainBlank=True,allReleaseCandidateAndPreparationApprovalFieldsUnchanged=True,readinessTotalsBefore=oldmodel['totals'],readinessTotalsAfter=model['totals'],trackedFilesApplied=False,diagnosticRenderingNotJUnit=True)
(w/'PROPOSAL_REVIEW.json').write_text(json.dumps(record,indent=2)+'\n')
print('Reviewed154 proposed files:',sum(x['changed'] for x in file_rows),'changed;two renderings identical;pending/approval blanks preserved.')
