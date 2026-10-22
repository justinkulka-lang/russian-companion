from pathlib import Path
import json,subprocess,hashlib,shutil,os,datetime
R=Path('/workspace/russian-companion');W=Path(__file__).parent;old=Path('/workspace/artifacts/pending-evidence-reconciliation-2026-10-21');g=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip();assert not g('status','--porcelain');head=g('rev-parse','HEAD');P=W/'INPUT_PROJECTION';P.mkdir()
for p in R.iterdir():
 if p.name in ['.git','release']:continue
 (P/p.name).symlink_to(p,target_is_directory=p.is_dir())
(P/'release').mkdir()
for p in (R/'release').iterdir():
 if p.name!='acceptance':(P/'release'/p.name).symlink_to(p,target_is_directory=p.is_dir())
(P/'release/acceptance').mkdir()
for p in (R/'release/acceptance').iterdir():
 if p.name!='candidate.json':(P/'release/acceptance'/p.name).symlink_to(p,target_is_directory=p.is_dir())
record=json.loads((R/'audit/gpt61-sol-high-comprehensive-2026-10-19/post-recording-guard-host-regression-2026-10-22/DIGEST_READ_ONLY_DIAGNOSIS.json').read_text());old_digest=record['candidateStoredDigest'];new_digest=record['currentComputedDigest'];raw=(R/'release/acceptance/candidate.json').read_bytes();assert raw.count(old_digest.encode())==1;proposed=raw.replace(old_digest.encode(),new_digest.encode());(P/'release/acceptance/candidate.json').write_bytes(proposed)
a=json.loads(raw);b=json.loads(proposed);assert [k for k in a if a[k]!=b[k]]==['sourceDigest'];assert not b['frozen'] and b['sourceCommit'] is None and b['publicRelease']=='NOT_APPROVED' and b['artifact']['status']=='NOT_RECORDED'
java=old/'GeneratePendingEvidence.java';shutil.copy2(java,W/java.name);cp=json.loads((old/'SOURCE_BINDING_REVIEW.json').read_text())['commands'][0]['command'][2];parts=cp.split(':');parts[0]=str(W);cp=':'.join(parts);assert all(Path(p).exists() for p in parts)
ctx=dict(sourceCommit=head,inputProjection=str(P),projectionIsGitCheckout=False,projectionHasGitDirectory=False,realWorkingDirectory=str(R),originalCandidateSha256=hashlib.sha256(raw).hexdigest(),proposedCandidateSha256=hashlib.sha256(proposed).hexdigest(),onlyProposedCandidateField='sourceDigest',oldDigest=old_digest,newDigest=new_digest,candidateApplied=False,noPrivateReferenceRead=True,mainTestCatalogDocsReviewTrees={p:g('rev-parse','HEAD:'+p) for p in ['app/src/main','app/src/test','catalog','docs','review']},generatorSha256=hashlib.sha256(java.read_bytes()).hexdigest(),classpath=cp,dependencyJarHashes={p:hashlib.sha256(Path(p).read_bytes()).hexdigest() for p in parts if Path(p).is_file()},scope='Authentic repository renderers;not JUnit or independent audit;no tracked output writes')
(W/'GENERATION_PREPARATION.json').write_text(json.dumps(ctx,indent=2)+'\n');print('Prepared private output-only projection at',head)
