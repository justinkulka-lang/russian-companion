from pathlib import Path
from datetime import datetime,timezone
import subprocess,json,hashlib,time,os,shutil,traceback
R=Path('/workspace/russian-companion');W=Path('/workspace/artifacts/curriculum-audit-direction-2026-10-29');RUN=Path('/workspace/artifacts/cache-ui-ci-broad-host-regression-2026-10-28');D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-whole-curriculum-audit-direction-2026-10-29';sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest();read=lambda p:json.loads(p.read_text());save=lambda p,v:p.write_text(json.dumps(v,indent=2)+'\n');git=lambda *a:subprocess.check_output(['git',*a],cwd=R).decode().strip()
try:
 with(W/'PUBLICATION_OWNER.json').open('x')as f:json.dump({'pid':os.getpid(),'startedUtc':datetime.now(timezone.utc).isoformat(),'scope':'Wait for broad evidence publication;then only workflow/auditor-work-order and three durable pointers. No app/test/policy changes.','supersedes':'Cancelled sequential Unit15 research publication'},f,indent=2)
 while not(RUN/'AUTO_RESULT.json').exists():
  if(RUN/'AUTO_ERROR.txt').exists():save(W/'PUBLICATION_HELD.json',{'reason':'Broad evidence collector needs recovery;new owner direction remains saved outside checkout','noRepositoryChanges':True});exit(0)
  time.sleep(15)
 broad=read(RUN/'AUTO_RESULT.json');assert git('rev-parse','HEAD')==broad['evidenceCommit'];assert not subprocess.check_output(['git','status','--porcelain=v1','-z','--untracked-files=all'],cwd=R);D.mkdir()
 packet=read(W/'PACKET_MANIFEST.json')
 for n,h in packet['files'].items():
  assert Path(n).name==n and sha(W/n)==h,n
  shutil.copyfile(W/n,D/n)
 shutil.copyfile(W/'PACKET_MANIFEST.json',D/'PACKET_MANIFEST.json')
 shutil.copyfile(__file__,D/'publish-curriculum-audit-direction.py');save(D/'BROAD_EVIDENCE_CHECKPOINT.json',broad)
 pointers=['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md'];rel=D.relative_to(R).as_posix()
 for n in pointers:
  p=R/n;link=rel+'/WORKFLOW_DECISION.md'if n=='AGENTS.md'else'../'+rel.removeprefix('audit/')+'/WORKFLOW_DECISION.md';p.write_text(f'Owner workflow change: [Independent whole-curriculum review]({link}). Defer automatic Unit15–30 sequential policy/baseline campaign;use AUDITOR_WORK_ORDER.md for all30units together,then prioritize batched corrections. This supersedes older NEXT_TASK unit15defaults;preserve all old evidence/unactivated research. Broadactual5a3411a7 result in BROAD_EVIDENCE_CHECKPOINT.json;no complete-app claim. Codex engineering/oneagent;all48OPEN/auditINCOMPLETE/four exporters16excluded/final comprehensive audit then physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
 save(D/'SHA256.json',{p.relative_to(D).as_posix():sha(p)for p in D.rglob('*')if p.is_file()and p!=D/'SHA256.json'})
 for row in subprocess.check_output(['git','status','--porcelain=v1','-z','--untracked-files=all'],cwd=R).decode().split('\0'):
  if row:assert row[3:]in pointers or row[3:].startswith(rel+'/'),row
 subprocess.run(['git','add',str(D),*pointers],cwd=R,check=True);subprocess.run(['git','diff','--cached','--check'],cwd=R,check=True);subprocess.run(['git','commit','-m','Route curriculum checks to an independent whole-course audit'],cwd=R,check=True);commit=git('rev-parse','HEAD');subprocess.run(['git','push','origin','cursor/content-audit-corrections-964f'],cwd=R,check=True);assert git('ls-remote','origin','refs/heads/cursor/content-audit-corrections-964f').split()[0]==commit
 pr=json.loads(subprocess.check_output(['gh','api','repos/justinkulka-lang/russian-companion/pulls/64'],cwd=R));body=f'Owner workflow change: defer the sequential Unit15–30 campaign. Next independent step is one whole-curriculum diagnostic covering all30units, followed by prioritized batches of fixes. Work order: {rel}/AUDITOR_WORK_ORDER.md; durable direction: WORKFLOW_DECISION.md. Latest instruction commit `{commit}`. The completed broad result below remains a separate pinned execution. All48findingsOPEN/auditINCOMPLETE;final comprehensive audit and subsequent physical acceptance still required.\n\n'+pr['body'];(W/'PR64_BODY.md').write_text(body);updated=json.loads(subprocess.check_output(['gh','api','--method','PATCH','repos/justinkulka-lang/russian-companion/pulls/64','-F','body=@'+str(W/'PR64_BODY.md')],cwd=R));assert updated['head']['sha']==commit and updated['draft']and updated['state']=='open';save(W/'PUBLICATION_RESULT.json',{'commit':commit,'packagePath':rel,'broadEvidenceCommit':broad['evidenceCommit'],'appOrTestChanges':False,'pr64OpenDraft':True})
except Exception:
 (W/'PUBLICATION_ERROR.txt').write_text(traceback.format_exc());raise
