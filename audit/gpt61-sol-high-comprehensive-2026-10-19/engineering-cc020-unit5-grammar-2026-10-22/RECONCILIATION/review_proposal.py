"""Review repeated renderer output against exact permitted identity-only replacements."""
from pathlib import Path
import json,hashlib,subprocess
R=Path('/workspace/russian-companion');W=Path(__file__).parent;g=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip();sha=lambda b:hashlib.sha256(b).hexdigest();c=json.loads((W/'GENERATION_PREPARATION.json').read_text());assert not g('status','--porcelain') and g('rev-parse','HEAD')==c['sourceCommit'];runs=json.loads((W/'GENERATION_RESULTS.json').read_text());assert len(runs)==3 and all(x['exitCode']==0 for x in runs)
a,b=W/'CANDIDATE_1',W/'CANDIDATE_2';m='review/unit-5/UNIT_5_REVIEW_MANIFEST.json';oldm=json.loads((R/m).read_text());newm=json.loads((a/m).read_text());tokens={oldm[k]:newm[k] for k in ['repositorySourceDigest','packContentReviewDigest','contentReviewDigest','curriculumSourceCommit','reviewPackageDigest']};assert len(tokens)==5
# Dossier has no renderer. Only its two explicit identity headers may change.
dossier='audit/curriculum-readiness/V2_CURRICULUM_GAP_CLOSURE_DOSSIER_v1.0.md';raw=(R/dossier).read_bytes();after=raw
for k in ['repositorySourceDigest','packContentReviewDigest']:after=after.replace(oldm[k].encode(),newm[k].encode())
for O in [a,b]:
 assert len([p for p in O.rglob('*') if p.is_file()])==153
 for rel,data in [('release/acceptance/candidate.json',(W/'INPUT_PROJECTION/release/acceptance/candidate.json').read_bytes()),(dossier,after)]:
  p=O/rel;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(data)
def delta(x,y,p=''):
 if type(x)!=type(y):return [(p,x,y)]
 if isinstance(x,dict):return sum((delta(x.get(k),y.get(k),p+'/'+k) for k in sorted(x.keys()|y.keys())),[])
 if isinstance(x,list):
  if len(x)!=len(y):return [(p+' length',len(x),len(y))]
  return sum((delta(i,j,p+'/'+str(n)) for n,(i,j) in enumerate(zip(x,y))),[])
 return [(p,x,y)] if x!=y else []
paths=sorted(p.relative_to(a).as_posix() for p in a.rglob('*') if p.is_file());assert len(paths)==155 and paths==sorted(p.relative_to(b).as_posix() for p in b.rglob('*') if p.is_file());rows=[]
for rel in paths:
 before=(R/rel).read_bytes();new=(a/rel).read_bytes();assert new==(b/rel).read_bytes(),('Render difference',rel)
 permitted={oldm[k]:newm[k] for k in ['repositorySourceDigest','packContentReviewDigest']}
 if rel.startswith('review/unit-5/') or rel.endswith('FINAL_AUDIT_PREPARATION_MANIFEST.json') or 'READINESS_RECONCILIATION' in rel:permitted=tokens
 expected=before
 for x,y in permitted.items():expected=expected.replace(x.encode(),y.encode())
 assert new==expected,('UNEXPECTED nonidentity change',rel)
 fields=delta(json.loads(before),json.loads(new)) if rel.endswith('.json') else []
 rows.append(dict(path=rel,oldSha256=sha(before),newSha256=sha(new),oldBytes=len(before),newBytes=len(new),changed=before!=new,jsonDeltas=fields,originalGitCommit=c['sourceCommit'],originalGitBlob=g('rev-parse',c['sourceCommit']+':'+rel)))
 if rel.endswith(('_REVIEW_WORKSHEET.tsv','_OWNED_EDITION_MAPPING_WORKSHEET.tsv')):assert before==new
ca,cb=json.loads((R/'release/acceptance/candidate.json').read_text()),json.loads((a/'release/acceptance/candidate.json').read_text());assert [k for k in ca if ca[k]!=cb[k]]==['sourceDigest'];assert cb['frozen'] is False and cb['sourceCommit'] is None and cb['artifact']=={'status':'NOT_RECORDED'} and cb['publicRelease']=='NOT_APPROVED'
proposal=dict(kind='REVIEWED_UNIT5_VERSIONED_SOURCE_IDENTITY_RECONCILIATION_NOT_APPROVAL',sourceCommit=c['sourceCommit'],files=rows,twoFreshJvmRendersIdentical=True,changedFiles=sum(r['changed'] for r in rows),unchangedProposedFiles=sum(not r['changed'] for r in rows),exactPermittedTokenReplacements=tokens,allOtherBytesExact=True,all60ReviewerAndMappingWorksheetsExact=True,candidateOnlyField='sourceDigest',allOtherCandidateFieldsExact=True,allSourceRightsNativeReviewerOwnerApprovalAndReadinessNonidentityFieldsExact=True,dossier='Manual replacement of two explicit identity headers; all other bytes exact; affected dossier test required',trackedFilesApplied=False)
(W/'PROPOSAL_REVIEW.json').write_text(json.dumps(proposal,indent=2)+'\n');print(json.dumps({k:v for k,v in proposal.items() if k not in ['files']},indent=2))
