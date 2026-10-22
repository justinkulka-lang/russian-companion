from pathlib import Path
import json,subprocess,hashlib,shutil
R=Path('/workspace/russian-companion');W=Path(__file__).parent;prior=Path('/workspace/artifacts/source-binding-reconciliation-2026-10-22');git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip();sha=lambda raw:hashlib.sha256(raw).hexdigest()
assert not git('status','--porcelain');head=git('rev-parse','HEAD');assert head=='cd8c9342dcd72161d45da3709a64cb487b732406'
P=W/'INPUT_PROJECTION';P.mkdir()
for p in R.iterdir():
 if p.name not in ['.git','release']:(P/p.name).symlink_to(p,target_is_directory=p.is_dir())
(P/'release').mkdir()
for p in (R/'release').iterdir():
 if p.name!='acceptance':(P/'release'/p.name).symlink_to(p,target_is_directory=p.is_dir())
(P/'release/acceptance').mkdir()
for p in (R/'release/acceptance').iterdir():
 if p.name!='candidate.json':(P/'release/acceptance'/p.name).symlink_to(p,target_is_directory=p.is_dir())
diag=json.loads((R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-v2-insets-accessibility-2026-10-22/SOURCE_BINDING_DIAGNOSIS.json').read_text());old=diag['candidateRecordedDigest'];new=diag['digests']['8e53775a'];raw=(R/'release/acceptance/candidate.json').read_bytes();assert raw.count(old.encode())==1;proposed=raw.replace(old.encode(),new.encode());a,b=json.loads(raw),json.loads(proposed);assert [k for k in a if a[k]!=b[k]]==['sourceDigest'];assert not b['frozen'] and b['sourceCommit'] is None and b['publicRelease']=='NOT_APPROVED' and b['artifact']['status']=='NOT_RECORDED';(P/'release/acceptance/candidate.json').write_bytes(proposed)
s=(prior/'GeneratePendingEvidence.java').read_text();needle='File root=new File(args[0]);Path out=Path.of(args[1]);';replacement='''File root=new File(args[0]);
  String digest=dev.russiancompanion.release.ReleaseSourceDigestKt.computeReleaseSourceDigest(root,Collections.emptyMap());
  String realDigest=dev.russiancompanion.release.ReleaseSourceDigestKt.computeReleaseSourceDigest(new File(args[3]),Collections.emptyMap());
  if(!digest.equals(args[2]) || !realDigest.equals(args[2]))throw new AssertionError("Canonical digest mismatch: "+digest+" real="+realDigest);
  System.out.println("CANONICAL_SOURCE_DIGEST "+digest+" REAL_ROOT_MATCH "+realDigest);
  Path out=Path.of(args[1]);''';assert needle in s;s=s.replace(needle,replacement);(W/'GeneratePendingEvidence.java').write_text(s)
oldctx=json.loads((prior/'GENERATION_PREPARATION.json').read_text());parts=oldctx['classpath'].split(':');parts[0]=str(W);assert all(Path(p).exists() for p in parts)
compiled=[]
for directory in parts:
 p=Path(directory)
 if p.is_dir() and 'app/build/' in directory:
  entries={f.relative_to(p).as_posix():sha(f.read_bytes()) for f in sorted(p.rglob('*.class'))};compiled.append(dict(directory=directory,classes=len(entries),indexSha256=sha(json.dumps(entries,sort_keys=True).encode())))
ctx=dict(sourceCommit=head,inputProjection=str(P),projectionIsGitCheckout=False,projectionHasGitDirectory=False,realWorkingDirectory=str(R),originalCandidateSha256=sha(raw),proposedCandidateSha256=sha(proposed),onlyProposedCandidateField='sourceDigest',oldDigest=old,newDigest=new,candidateApplied=False,noPrivateReferenceRead=True,mainTestCatalogDocsReviewTrees={p:git('rev-parse','HEAD:'+p) for p in ['app/src/main','app/src/test','catalog','docs','review','release']},generatorSha256=sha((W/'GeneratePendingEvidence.java').read_bytes()),classpath=':'.join(parts),dependencyJarHashes={p:sha(Path(p).read_bytes()) for p in parts if Path(p).is_file()},compiledClassDirectories=compiled,compiledSourceEvidence='Successful corrected UI scope at 8e53775a, main/test trees unchanged since; main/test compiler outputs not modified before generation',scope='Authentic renderers and canonical Kotlin digest; no JUnit execution or independent audit; no tracked writes')
(W/'GENERATION_PREPARATION.json').write_text(json.dumps(ctx,indent=2)+'\n')
s=(prior/'generate.py').read_text().replace("str(W/f'CANDIDATE_{n}')])", "str(W/f'CANDIDATE_{n}'),ctx['newDigest'],str(R)])");assert "ctx['newDigest']" in s;(W/'generate.py').write_text(s)
shutil.copyfile(prior/'review_proposal.py',W/'review_proposal.py')
print('Output-only projection prepared; canonical digest checked by both fresh JVM invocations.')
