"""Use actual compiled Kotlin methods after core verification; never edits source/review records."""
from pathlib import Path
from datetime import datetime,timezone
import json,subprocess,shutil,hashlib,base64,re
R=Path('/workspace/russian-companion');W=Path(__file__).parent;O=W/'FOLLOWUP';r=json.loads((O/'REGRESSION/RESULT.json').read_text());assert r['testCounts']==dict(reported=14,passed=14,failed=0,errors=0,skipped=0) and r['exitCode']==0;source=subprocess.check_output(['git','rev-parse','HEAD'],cwd=R,text=True).strip();assert source==r['sourceCommit'];assert not subprocess.check_output(['git','status','--porcelain'],cwd=R,text=True).strip()
old=Path('/workspace/artifacts/unit10-present-correction-2026-10-25');prior=json.loads((old/'OFFICIAL_SOURCE_DIGEST.json').read_text());parts=prior['commands'][1]['argv'][2].split(':');cp=':'.join([str(W)]+parts[1:]);assert all(Path(p).exists() for p in cp.split(':'))
for n in ['Digest.java','ExportReviewPolicies.java']:shutil.copy2(old/n,W/n)
commands=[]
for name,argv in [('HELPER_COMPILE',['/workspace/jdk21/bin/javac','-cp',cp,str(W/'Digest.java'),str(W/'ExportReviewPolicies.java')]),('OFFICIAL_DIGEST',['/workspace/jdk21/bin/java','-cp',cp,'Digest',str(R)]),('POLICY_EXPORT',['/workspace/jdk21/bin/java','-cp',cp,'ExportReviewPolicies',str(R)])]:
 start=datetime.now(timezone.utc).isoformat();p=subprocess.run(argv,cwd=R,stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True);(W/(name+'.txt')).write_text(p.stdout);commands.append(dict(label=name,argv=argv,startedUtc=start,exitCode=p.returncode));assert p.returncode==0,p.stdout[:1000]
raw=(W/'OFFICIAL_DIGEST.txt').read_text();values=re.findall(r'(?m)^[0-9a-f]{64}$',raw);assert len(values)==1;digest=values[0];assert digest!='072ea177f73cf7f8ac2a2eedc4afc61f42c2e55d9aeb2c6b2adaf0184cef21f6';policies=[];pools=[];dec=lambda x:base64.b64decode(x).decode()
for line in (W/'POLICY_EXPORT.txt').read_text().splitlines():
 v=line.split('\t')
 if v[0]=='POLICY':policies.append(dict(unit=int(v[1]),id=dec(v[2]),digest=v[3],schema=int(v[4]),pools=int(v[5])))
 if v[0]=='POOL':pools.append(dict(unit=int(v[1]),slot=int(v[2]),objective=dec(v[3]),skill=v[4],remediationStep=dec(v[5]),items=dec(v[6]).splitlines()))
old_obs=json.loads((old/'CURRENT_POLICY_OBSERVATION.json').read_text());assert len(policies)==28 and len(pools)==59;assert next(x for x in policies if x['unit']==11)['id']=='unit11-prior-noun-behavior-possession-absence-v2' and next(x for x in policies if x['unit']==11)['schema']==13;assert [x for x in policies if x['unit']!=11]==[x for x in old_obs['policies'] if x['unit']!=11];assert [x for x in pools if x['unit']!=11]==[x for x in old_obs['pools'] if x['unit']!=11]
repeat=[x['unit'] for x in pools if x['items']==['u1-i-decode-pair']];placements=sum(len(x['items']) for x in pools);unique=len({i for x in pools for i in x['items']});assert (placements,unique,len(repeat))==(103,51,21)
classes={}
for name in ['dev/russiancompanion/release/ReleaseSourceDigestKt.class','dev/russiancompanion/v2/curriculum/V2CumulativeReviewKt.class','dev/russiancompanion/v2/curriculum/Unit11NounCumulativeReviewPolicy.class']:
 hits=[Path(root)/name for root in cp.split(':') if Path(root).is_dir() and (Path(root)/name).is_file()];assert hits;classes[name]=dict(path=str(hits[0]),sha256=hashlib.sha256(hits[0].read_bytes()).hexdigest())
metadata=dict(sourceCommit=source,utc=datetime.now(timezone.utc).isoformat(),method='ReleaseSourceDigestKt.computeReleaseSourceDigest(root,Collections.emptyMap())',canonicalSourceDigest=digest,commands=commands,compiledClasses=classes,helperSha256={n:hashlib.sha256((W/n).read_bytes()).hexdigest() for n in ['Digest.java','ExportReviewPolicies.java']},inputTrees=r['inputTrees'],runtimeJUnitMethodsAdded=0,candidateOr155RecordsEdited=False)
(W/'OFFICIAL_SOURCE_DIGEST.json').write_text(json.dumps(metadata,indent=2)+'\n');(W/'CURRENT_POLICY_OBSERVATION.json').write_text(json.dumps(dict(sourceCommit=source,policies=policies,pools=pools,activePolicies=28,objectivePools=59,candidatePlacements=placements,uniqueCandidateItems=unique,unitsWithPrintSingleton=repeat,printSingletonPolicies=len(repeat),runtimeMethodsExecuted=0),indent=2)+'\n');print(json.dumps(dict(source=source,officialDigest=digest,activePolicies=28,pools=59,placements=placements,unique=unique,printSingletonPolicies=21,newJUnitExecutions=0)))
