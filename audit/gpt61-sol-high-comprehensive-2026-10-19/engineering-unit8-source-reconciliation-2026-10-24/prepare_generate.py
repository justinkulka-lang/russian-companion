from pathlib import Path
from datetime import datetime,timezone
import json,subprocess,shutil,hashlib
R=Path('/workspace/russian-companion');W=Path(__file__).parent;OLD=Path('/workspace/artifacts/unit7-source-reconciliation-2026-10-24');sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest();git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
assert not git('status','--porcelain');base=json.loads((OLD/'GENERATION_PREPARATION.json').read_text());cp=base['classpath'].replace(str(OLD),str(W),1)
for n in ['Digest.java','GeneratePendingEvidence.java','generate.py']:shutil.copy2(OLD/n,W/n)
for label,args in [('DIGEST_COMPILE',['/workspace/jdk21/bin/javac','-cp',cp,str(W/'Digest.java')]),('DIGEST',['/workspace/jdk21/bin/java','-cp',cp,'Digest',str(R)])]:
 start=datetime.now(timezone.utc).isoformat();p=subprocess.run(args,cwd=R,text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT);(W/(label+'.log')).write_text(p.stdout);assert p.returncode==0,p.stdout
 if label=='DIGEST':digest=p.stdout.strip();digestCommand=args
assert digest=='e1f57fec1eeeacde3162069434116852aaa0278f2e501db859db50cc43c21670'
P=W/'INPUT_PROJECTION';P.mkdir()
for p in (OLD/'INPUT_PROJECTION').iterdir():
 if p.name=='release':shutil.copytree(R/'release',P/'release')
 else:(P/p.name).symlink_to(R/p.name,target_is_directory=(R/p.name).is_dir())
assert not (P/'.git').exists();original=(R/'release/acceptance/candidate.json').read_bytes();candidate=json.loads(original);oldDigest=candidate['sourceDigest'];assert oldDigest=='d48aa7e0090bd30422172f7b7c87fe52fe2859fa3c5be9b0851d2f9704f30e3a';new=original.replace(oldDigest.encode(),digest.encode());assert original.count(oldDigest.encode())==1;(P/'release/acceptance/candidate.json').write_bytes(new)
indexes=[]
for i,folder in enumerate([x for x in cp.split(':') if Path(x).is_dir() and '/app/build/' in x],1):
 root=Path(folder);index={p.relative_to(root).as_posix():sha(p) for p in sorted(root.rglob('*')) if p.is_file()};data=json.dumps(index,indent=2)+'\n';(W/f'COMPILED_INDEX_{i}.json').write_text(data);indexes.append(dict(directory=folder,files=len(index),indexSha256=hashlib.sha256(data.encode()).hexdigest()))
c=dict(sourceCommit=git('rev-parse','HEAD'),actualCurriculumChangeCommit='77e3d26eab138796a699d9133e45e4f793e95cc1',actualVerifiedProductionSource='370d8716e2747c439da1a450a83b71b129a5c879',classpath=cp,inputProjection=str(P),projectionIsGitCheckout=False,realWorkingDirectory=str(R),oldDigest=oldDigest,newDigest=digest,digestCommand=digestCommand,oldCandidateSha256=hashlib.sha256(original).hexdigest(),proposedCandidateSha256=hashlib.sha256(new).hexdigest(),onlyProposedCandidateField='sourceDigest',candidateApplied=False,inputTrees={p:git('rev-parse','HEAD:'+p) for p in ['app/src/main','app/src/test','catalog','docs','review','release']},compiledSourceEvidence=json.loads((W/'COMPILE_CONTEXT.json').read_text()),compiledClassDirectories=indexes,dependencyJarHashes={p:sha(Path(p)) for p in cp.split(':') if p.endswith('.jar')},generatorSha256=sha(W/'GeneratePendingEvidence.java'),rawCurriculumSHA256=sha(R/'app/src/main/assets/content/v2-curriculum.json'),noPrivateReferenceRead=True,scope='Actual compiled Unit8 declaration/source binding generation,not approval')
(W/'GENERATION_PREPARATION.json').write_text(json.dumps(c,indent=2)+'\n');print(json.dumps({k:c[k] for k in ['sourceCommit','newDigest','actualCurriculumChangeCommit']}))
