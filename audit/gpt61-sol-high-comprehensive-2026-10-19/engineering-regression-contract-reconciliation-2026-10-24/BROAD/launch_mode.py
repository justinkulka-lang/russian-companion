from pathlib import Path
from datetime import datetime,timezone
import subprocess,json,hashlib,sys
W=Path(sys.argv[1]);mode=sys.argv[2];R=Path('/workspace/russian-companion');assert mode in ['DISCOVERY','REGRESSION'];assert not (W/mode).exists() and not (W/(mode+'_LAUNCHER.json')).exists();git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip();assert not git('status','--porcelain');assert git('branch','--show-current')=='cursor/content-audit-corrections-964f';source=git('rev-parse','HEAD')
if mode=='REGRESSION':
 d=json.loads((W/'DISCOVERY/RESULT.json').read_text());c=json.loads((W/'DISCOVERY/CONTEXT.json').read_text());assert d['sourceCommit']==c['sourceCommit'] and d['exitCode']==0 and d['classes']==229 and d['syntheticDryRun'] and d['testCounts']==dict(reported=1365,passed=0,failed=0,errors=0,skipped=1365)
 assert c['inputTrees']==d['inputTrees']
 changed=git('diff','--name-only',d['sourceCommit'],source).splitlines()
 allowed={'AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md'}
 checkpoint='audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-regression-contract-reconciliation-2026-10-24/'
 assert all(n in allowed or n.startswith(checkpoint) for n in changed),'Non-checkpoint input changed; preserve evidence and run fresh discovery'
 assert git('rev-parse',d['sourceCommit']+'^{tree}')==c['sourceRootTree']
 for p,v in d['inputTrees'].items():
  assert git('rev-parse',d['sourceCommit']+':'+p)==v
  assert git('rev-parse','HEAD:'+p)==v
 for name,value in json.loads((W/'DISCOVERY/SHA256.json').read_text()).items():assert hashlib.sha256((W/'DISCOVERY'/name).read_bytes()).hexdigest()==value,name
 for p,v in c['initScriptSha256'].items():assert hashlib.sha256(Path(p).read_bytes()).hexdigest()==v
 assert hashlib.sha256((W/'run.py').read_bytes()).hexdigest()==c['runnerSha256']
if len(sys.argv)>3:
 assert sys.argv[3:]==['--preflight-only'] and mode=='REGRESSION'
 print(json.dumps(dict(preflightOnly=True,sourceCommit=source,discoverySourceCommit=d['sourceCommit'],sameSixInputTrees=True,methods=1365,classes=229,runtimeLaunched=False)))
 sys.exit(0)
with (W/(mode+'_LAUNCHER.log')).open('xb') as out:p=subprocess.Popen(['python3',str(W/'supervise.py'),mode],cwd=R,stdin=subprocess.DEVNULL,stdout=out,stderr=subprocess.STDOUT,start_new_session=True)
x=dict(pid=p.pid,sourceCommit=source,startedUtc=datetime.now(timezone.utc).isoformat(),argv=p.args,detached=True)
with (W/(mode+'_LAUNCHER.json')).open('x') as f:json.dump(x,f,indent=2);f.write('\n')
print(json.dumps(x))
