from pathlib import Path
from datetime import datetime,timezone
import json,subprocess,shutil,base64,hashlib
W=Path(__file__).parent;R=Path('/workspace/russian-companion');B=R/'audit/gpt61-sol-high-comprehensive-2026-10-19'
assert not subprocess.check_output(['git','status','--porcelain'],cwd=R,text=True).strip()
ref=json.loads((B/'engineering-current-host-regression-2026-10-23/NATIVE_FAILURE/READ_ONLY_SOURCE_IDENTITY.json').read_text());cp=ref['command'][2]
shutil.copy2(B/'engineering-cc020-review-scope-2026-10-22/ExportReviewPolicies.java',W/'ExportReviewPolicies.java')
cp=str(W)+':'+cp
commands=[('COMPILE',['/workspace/jdk21/bin/javac','-cp',cp,str(W/'ExportReviewPolicies.java')]),('EXPORT',['/workspace/jdk21/bin/java','-Xmx1g','-cp',cp,'ExportReviewPolicies',str(R)]),('DIGEST',ref['command'])];records=[]
for label,cmd in commands:
 started=datetime.now(timezone.utc).isoformat();p=subprocess.run(cmd,cwd=R,text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT);(W/(label+'.log')).write_text(p.stdout);records.append(dict(label=label,command=cmd,startedUtc=started,finishedUtc=datetime.now(timezone.utc).isoformat(),exitCode=p.returncode));assert p.returncode==0,p.stdout[:1000]
raw=(W/'EXPORT.log').read_text();policies=[];pools=[];dec=lambda s:base64.b64decode(s).decode()
for line in raw.splitlines():
 v=line.split('\t')
 if v[0]=='POLICY':policies.append(dict(unit=int(v[1]),id=dec(v[2]),digest=v[3],schema=int(v[4]),pools=int(v[5])))
 elif v[0]=='POOL':pools.append(dict(unit=int(v[1]),slot=int(v[2]),objective=dec(v[3]),skill=v[4],remediationStep=dec(v[5]),items=dec(v[6]).splitlines()))
assert len(policies)==28 and len(pools)==59
candidates=[i for p in pools for i in p['items']];printUnits=[p['unit'] for p in pools if 'u1-i-decode-pair' in p['items']]
assert len(printUnits)==26 and 5 not in printUnits and 6 not in printUnits
assert '2507d56bf2bd910b198d9ff317a605598765e13d947fa4f4435aaacd2a626892' in (W/'DIGEST.log').read_text()
facts=dict(sourceCommit=subprocess.check_output(['git','rev-parse','HEAD'],cwd=R,text=True).strip(),policies=policies,pools=pools,activePolicies=len(policies),objectivePools=len(pools),candidatePlacements=len(candidates),uniqueCandidateItems=len(set(candidates)),policiesStillContainingPrintCaseSingleton=len(printUnits),unitsStillContainingPrintCaseSingleton=printUnits,commands=records,exportSha256=hashlib.sha256(raw.encode()).hexdigest(),canonicalSourceDigest='2507d56bf2bd910b198d9ff317a605598765e13d947fa4f4435aaacd2a626892',computation='Actual compiled production export and official Kotlin digest; zero JUnit methods executed',completeSuite=False)
(W/'CURRENT_POLICY_INVENTORY.json').write_text(json.dumps(facts,indent=2)+'\n');print(json.dumps({k:facts[k] for k in ['sourceCommit','activePolicies','objectivePools','candidatePlacements','uniqueCandidateItems','policiesStillContainingPrintCaseSingleton','unitsStillContainingPrintCaseSingleton','canonicalSourceDigest']}))
