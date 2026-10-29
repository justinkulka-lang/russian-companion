from pathlib import Path
from datetime import datetime,timezone
import json,subprocess,shutil,hashlib,re
R=Path('/workspace/russian-companion');W=Path(__file__).parent;OLD=Path('/workspace/artifacts/unit13-source-reconciliation-2026-10-27');sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest();git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
assert not git('status','--porcelain');compiled=json.loads((W/'COMPILE_CONTEXT.json').read_text());assert compiled['exitCode']==0 and compiled['JUnitMethodsExecuted']==0 and all(compiled['inputTrees'][n]==git('rev-parse','HEAD:'+n) for n in compiled['inputTrees']);base=json.loads((OLD/'GENERATION_PREPARATION.json').read_text());cp=base['classpath'].replace(str(OLD),str(W),1)
for n in ['Digest.java','GeneratePendingEvidence.java']:shutil.copy2(OLD/n,W/n)
for label,args in [('DIGEST_COMPILE',['/workspace/jdk21/bin/javac','-cp',cp,str(W/'Digest.java')]),('DIGEST',['/workspace/jdk21/bin/java','-cp',cp,'Digest',str(R)])]:
 start=datetime.now(timezone.utc).isoformat();p=subprocess.run(args,cwd=R,text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT);(W/(label+'.log')).write_text(p.stdout);assert p.returncode==0,p.stdout
 if label=='DIGEST':
  values=re.findall(r'(?m)^[0-9a-f]{64}$',p.stdout);assert len(values)==1;digest=values[0];digestCommand=args
assert digest=='487d9118d7154dd59162dbb7024f1349bc53d5b7bf89e9d79022d97eaa1eeb9a'
P=W/'INPUT_PROJECTION';P.mkdir()
for p in (OLD/'INPUT_PROJECTION').iterdir():
 if p.name=='release':shutil.copytree(R/'release',P/'release')
 else:(P/p.name).symlink_to(R/p.name,target_is_directory=(R/p.name).is_dir())
assert not (P/'.git').exists();original=(R/'release/acceptance/candidate.json').read_bytes();candidate=json.loads(original);oldDigest=candidate['sourceDigest'];assert oldDigest=='3c0acbb95297cf10f08da2bd8391463067eece22fec785158b5d2680d37dc2ba';new=original.replace(oldDigest.encode(),digest.encode());assert original.count(oldDigest.encode())==1;(P/'release/acceptance/candidate.json').write_bytes(new)
indexes=[]
for i,folder in enumerate([x for x in cp.split(':') if Path(x).is_dir() and '/app/build/' in x],1):
 root=Path(folder);index={p.relative_to(root).as_posix():sha(p) for p in sorted(root.rglob('*')) if p.is_file()};data=json.dumps(index,indent=2)+'\n';(W/f'COMPILED_INDEX_{i}.json').write_text(data);indexes.append(dict(directory=folder,files=len(index),indexSha256=hashlib.sha256(data.encode()).hexdigest()))
c=dict(sourceCommit=git('rev-parse','HEAD'),actualCurriculumChangeCommit='d72afd1232857a857624c20c58f4bcd3cb5c8564',actualVerifiedProductionSource='d72afd1232857a857624c20c58f4bcd3cb5c8564',classpath=cp,inputProjection=str(P),projectionIsGitCheckout=False,realWorkingDirectory=str(R),oldDigest=oldDigest,newDigest=digest,digestCommand=digestCommand,oldCandidateSha256=hashlib.sha256(original).hexdigest(),proposedCandidateSha256=hashlib.sha256(new).hexdigest(),onlyProposedCandidateField='sourceDigest',candidateApplied=False,inputTrees={p:git('rev-parse','HEAD:'+p) for p in ['app/src/main','app/src/test','catalog','docs','review','release']},compiledSourceEvidence=json.loads((W/'COMPILE_CONTEXT.json').read_text()),compiledClassDirectories=indexes,dependencyJarHashes={p:sha(Path(p)) for p in cp.split(':') if p.endswith('.jar')},generatorSha256=sha(W/'GeneratePendingEvidence.java'),rawCurriculumSHA256=sha(R/'app/src/main/assets/content/v2-curriculum.json'),noPrivateReferenceRead=True,scope='Actual compiled Unit14 declaration/source binding generation,not approval')
(W/'GENERATION_PREPARATION.json').write_text(json.dumps(c,indent=2)+'\n');print(json.dumps({k:c[k] for k in ['sourceCommit','newDigest','actualCurriculumChangeCommit']}))
