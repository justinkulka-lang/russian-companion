from pathlib import Path
import json,hashlib,subprocess,shutil
R=Path('/workspace/russian-companion');W=Path(__file__).parent
sha=lambda b:hashlib.sha256(b).hexdigest()
git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
ctx=json.loads((W/'GENERATION_PREPARATION.json').read_text());assert not git('status','--porcelain') and git('rev-parse','HEAD')==ctx['sourceCommit']
results=json.loads((W/'GENERATION_RESULTS.json').read_text());assert len(results)==3 and all(r['exitCode']==0 for r in results)
a,b=W/'CANDIDATE_1',W/'CANDIDATE_2'
paths=sorted(p.relative_to(a).as_posix() for p in a.rglob('*') if p.is_file());assert len(paths)==153 and paths==sorted(p.relative_to(b).as_posix() for p in b.rglob('*') if p.is_file())
for rel in paths:assert (a/rel).read_bytes()==(b/rel).read_bytes(),rel
old9=json.loads((R/'review/unit-9/UNIT_9_REVIEW_MANIFEST.json').read_text());new9=json.loads((a/'review/unit-9/UNIT_9_REVIEW_MANIFEST.json').read_text())
fields=['curriculumSourceCommit','contentReviewDigest','packContentReviewDigest','reviewPackageDigest','repositorySourceDigest']
assert [k for k in old9 if old9[k]!=new9[k]]==fields
replacements={old9[k]:new9[k] for k in fields};assert len(replacements)==5
assert new9['curriculumSourceCommit']==ctx['actualVerifiedProductionSource'] and new9['repositorySourceDigest']==ctx['newDigest']
assert new9['mappingPackageDigest']==old9['mappingPackageDigest']
O=W/'PROPOSAL';O.mkdir()
for rel in paths:
 p=O/rel;p.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(a/rel,p)
extra={}
extra['release/acceptance/candidate.json']=(W/'INPUT_PROJECTION/release/acceptance/candidate.json').read_bytes()
rel='audit/curriculum-readiness/V2_CURRICULUM_GAP_CLOSURE_DOSSIER_v1.0.md';before=(R/rel).read_bytes();after=before
for k in ['packContentReviewDigest','repositorySourceDigest']:
 assert after.count(old9[k].encode())==1;after=after.replace(old9[k].encode(),new9[k].encode())
extra[rel]=after
for rel,data in extra.items():p=O/rel;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(data)

def deltas(x,y,path=''):
 if x==y:return []
 if isinstance(x,dict) and isinstance(y,dict):
  assert x.keys()==y.keys(),path;return [r for k in x for r in deltas(x[k],y[k],path+'/'+k)]
 if isinstance(x,list) and isinstance(y,list):
  assert len(x)==len(y),path;return [r for i,(u,v) in enumerate(zip(x,y)) for r in deltas(u,v,path+'/'+str(i))]
 return [dict(path=path,before=x,after=y)]
rows=[]
for rel in sorted(paths+list(extra)):
 before=(R/rel).read_bytes();after=(O/rel).read_bytes();expected=before
 activeReplacements = ({old9[k]:new9[k] for k in ['packContentReviewDigest','repositorySourceDigest']}
    if rel=='audit/curriculum-readiness/V2_CURRICULUM_GAP_CLOSURE_DOSSIER_v1.0.md' else replacements)
 for x,y in activeReplacements.items():expected=expected.replace(x.encode(),y.encode())
 assert after==expected,('Non-identity byte delta',rel)
 changes=deltas(json.loads(before),json.loads(after)) if rel.endswith('.json') else []
 allowed=None
 if rel.startswith('review/') and rel.endswith('.json'):
  allowed=['/'+k for k in fields] if rel.startswith('review/unit-9/') else ['/packContentReviewDigest','/repositorySourceDigest']
 elif rel.endswith('candidate.json'):allowed=['/sourceDigest']
 elif rel.endswith('V2_CURRICULUM_READINESS_RECONCILIATION.json'):
  allowed=['/packContentReviewDigest','/repositorySourceDigest','/totals/packContentReviewDigest','/totals/repositorySourceDigest','/units/8/unitContentReviewDigest','/units/8/reviewPackageDigest','/units/8/curriculumSourceCommit']
 elif rel.endswith('FINAL_AUDIT_PREPARATION_MANIFEST.json'):
  allowed=['/packContentReviewDigest','/repositorySourceDigest','/curriculumSourceCommits/9','/unitDispositions/8/contentDigest']
 if allowed is not None:assert [r['path'] for r in changes]==allowed,(rel,changes)
 if rel.endswith('_WORKSHEET.tsv'):assert before==after,rel
 rows.append(dict(path=rel,changed=before!=after,oldSha256=sha(before),newSha256=sha(after),oldBytes=len(before),newBytes=len(after),originalGitCommit=ctx['sourceCommit'],originalGitBlob=git('rev-parse',ctx['sourceCommit']+':'+rel),jsonChanges=changes,rendered=rel in paths))
candidate=json.loads(extra['release/acceptance/candidate.json']);oldcandidate=json.loads((R/'release/acceptance/candidate.json').read_text());assert deltas(oldcandidate,candidate)==[{'path':'/sourceDigest','before':ctx['oldDigest'],'after':ctx['newDigest']}]
assert candidate['frozen'] is False and candidate['sourceCommit'] is None and candidate['publicRelease']=='NOT_APPROVED'
proposal=dict(kind='REVIEWED_DECLARATION_TRANSITION_BINDINGS_NOT_APPROVAL',sourceCommit=ctx['sourceCommit'],actualVerifiedProductionSource=ctx['actualVerifiedProductionSource'],firstDeclarationChangeCommit=ctx['actualCurriculumChangeCommit'],twoFreshJvmRendersIdentical=True,renderedFiles=153,proposedFiles=len(rows),changedFiles=sum(r['changed'] for r in rows),unchangedFiles=sum(not r['changed'] for r in rows),identityTokenReplacements=[{'field':k,'before':old9[k],'after':new9[k]} for k in fields],everyOtherByteExact=True,manualSupplement='Candidate sourceDigest only; dossier pack/source identity headers only',all60ReviewerMappingWorksheetsUnchanged=True,allReviewerOwnerRightsSourceAdmissionAndApprovalFieldsUnchanged=True,files=rows,trackedFilesApplied=False)
(W/'PROPOSAL_REVIEW.json').write_text(json.dumps(proposal,indent=2)+'\n');print(json.dumps({k:v for k,v in proposal.items() if k not in ['files','identityTokenReplacements']}))
