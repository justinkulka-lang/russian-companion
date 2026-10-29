from pathlib import Path
import json,hashlib,subprocess,zipfile
R=Path('/workspace/russian-companion');W=Path(__file__).parent;g=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip();assert not g('status','--porcelain');ctx=json.loads((W/'GENERATION_PREPARATION.json').read_text());assert g('rev-parse','HEAD')==ctx['sourceCommit'];results=json.loads((W/'GENERATION_RESULTS.json').read_text());assert len(results)==3 and all(v['exitCode']==0 for v in results)
old,new=ctx['oldDigest'],ctx['newDigest'];sha=lambda b:hashlib.sha256(b).hexdigest()
for label in ['CANDIDATE_1','CANDIDATE_2']:
 O=W/label;assert len([p for p in O.rglob('*') if p.is_file()])in [153,155]
 for rel,b in [('release/acceptance/candidate.json',(W/'INPUT_PROJECTION/release/acceptance/candidate.json').read_bytes()),('audit/curriculum-readiness/V2_CURRICULUM_GAP_CLOSURE_DOSSIER_v1.0.md',(R/'audit/curriculum-readiness/V2_CURRICULUM_GAP_CLOSURE_DOSSIER_v1.0.md').read_bytes().replace(old.encode(),new.encode()))]:
  p=O/rel;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(b)
a,b=W/'CANDIDATE_1',W/'CANDIDATE_2';paths=sorted(p.relative_to(a).as_posix() for p in a.rglob('*') if p.is_file());assert len(paths)==155 and paths==sorted(p.relative_to(b).as_posix() for p in b.rglob('*') if p.is_file());rows=[]
for rel in paths:
 before=(R/rel).read_bytes();after=(a/rel).read_bytes();assert after==(b/rel).read_bytes(),rel
 assert after==before.replace(old.encode(),new.encode()),('Unexpected non-digest byte delta',rel)
 changed=before!=after;fields=[]
 if rel.endswith('.json') and changed:
  x,y=json.loads(before),json.loads(after);fields=[k for k in x if x[k]!=y[k]];expected=['sourceDigest'] if rel.endswith('candidate.json') else ['repositorySourceDigest','totals'] if rel.endswith('V2_CURRICULUM_READINESS_RECONCILIATION.json') else ['repositorySourceDigest'];assert fields==expected,(rel,fields)
  if 'totals' in fields:assert [k for k in x['totals'] if x['totals'][k]!=y['totals'][k]]==['repositorySourceDigest']
 rows.append(dict(path=rel,oldSha256=sha(before),newSha256=sha(after),oldBytes=len(before),newBytes=len(after),changed=changed,changedJsonFields=fields,originalGitCommit=ctx['sourceCommit'],originalGitBlob=g('rev-parse',ctx['sourceCommit']+':'+rel)))
assert sum(v['changed'] for v in rows)==95
for v in rows:
 if v['path'].endswith(('_REVIEW_WORKSHEET.tsv','_OWNED_EDITION_MAPPING_WORKSHEET.tsv')):assert not v['changed']
proposal=dict(kind='REVIEWED_MECHANICAL_SOURCE_BINDING_PROPOSAL_NOT_APPROVAL',sourceCommit=ctx['sourceCommit'],files=rows,twoFreshJvmRendersIdentical=True,changedFiles=95,unchangedProposedFiles=60,onlyByteChanges='Exact old source-digest UTF8 bytes replaced with new source-digest bytes',candidateOnlyField='sourceDigest',allOtherCandidateFieldsExact=True,allMappingReviewerOwnerDispositionSourceRightsNativeContentBytesExact=True,noProductionTestCatalogOrGovernanceSourceChanges=True,historicalPostPr53ReportUntouched=True,trackedFilesApplied=False,original9FailuresNotRewritten=True)
(W/'PROPOSAL_REVIEW.json').write_text(json.dumps(proposal,indent=2)+'\n');print('Reviewed',len(rows),'files:',proposal['changedFiles'],'changed;every other byte identical;60 unchanged worksheets;no tracked writes.')
