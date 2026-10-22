"""Apply already reviewed renderer output once; archive every changed prior version."""
from pathlib import Path
import json,hashlib,zipfile,subprocess,shutil
R=Path('/workspace/russian-companion');W=Path(__file__).parent;D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-cc020-unit5-grammar-2026-10-22/RECONCILIATION';g=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip();sha=lambda b:hashlib.sha256(b).hexdigest();p=json.loads((W/'PROPOSAL_REVIEW.json').read_text());assert not g('status','--porcelain') and g('rev-parse','HEAD')==p['sourceCommit'];D.mkdir();save=lambda n,v:(D/n).write_text(json.dumps(v,indent=2)+'\n');rows=p['files']
for r in rows:assert sha((R/r['path']).read_bytes())==r['oldSha256'] and sha((W/'CANDIDATE_1'/r['path']).read_bytes())==r['newSha256']
with zipfile.ZipFile(D/'HISTORICAL_PRIOR_CURRENT.zip','w',zipfile.ZIP_DEFLATED) as z:
 for r in rows:
  if r['changed']:z.write(R/r['path'],r['path'])
with zipfile.ZipFile(D/'HISTORICAL_PRIOR_CURRENT.zip') as z:
 assert z.testzip() is None and len(z.namelist())==p['changedFiles']
 for r in rows:
  if r['changed']:assert sha(z.read(r['path']))==r['oldSha256']
save('HISTORICAL_PRIOR_CURRENT_INDEX.json',dict(kind='HISTORICAL_PENDING_REVIEW_BINDINGS_NOT_APPROVAL',originalCommit=p['sourceCommit'],archive='HISTORICAL_PRIOR_CURRENT.zip',archiveSha256=sha((D/'HISTORICAL_PRIOR_CURRENT.zip').read_bytes()),archivedChangedFiles=p['changedFiles'],reusedUnchangedFiles=p['unchangedProposedFiles'],files=rows))
for n in ['GENERATION_PREPARATION.json','GENERATION_RESULTS.json','PROPOSAL_REVIEW.json','GeneratePendingEvidence.java','generate.py','Digest.java','review_proposal.py','apply_reviewed.py','run.py','resources.gradle']:shutil.copyfile(W/n,D/n)
logs=['COMPILE.log','CANDIDATE_1.log','CANDIDATE_2.log']
with zipfile.ZipFile(D/'RAW_GENERATION.zip','w',zipfile.ZIP_DEFLATED) as z:
 for n in logs:z.write(W/n,n)
save('RAW_GENERATION_INDEX.json',{n:dict(sha256=sha((W/n).read_bytes()),bytes=(W/n).stat().st_size) for n in logs})
base=R/'audit/gpt61-sol-high-comprehensive-2026-10-19';preserved={}
for n in ['engineering-ui-source-bindings-2026-10-22','engineering-source-binding-reconciliation-2026-10-22','engineering-v2-insets-accessibility-2026-10-22','final-independent-recheck-results/2026-10-22-claude-code','post-recording-guard-host-regression-2026-10-22','engineering-backup-recording-filenames-2026-10-22','engineering-backup-archive-boundaries-2026-10-22','engineering-cc020-review-scope-2026-10-22']:
 for f in sorted((base/n).rglob('*')):
  if f.is_file():preserved[f.relative_to(R).as_posix()]=sha(f.read_bytes())
for f in D.parent.rglob('*'):
 if f.is_file() and D not in f.parents:preserved[f.relative_to(R).as_posix()]=sha(f.read_bytes())
save('PRESERVED_PRECEDING_SCOPES.json',preserved)
for r in rows:
 if r['changed']:shutil.copyfile(W/'CANDIDATE_1'/r['path'],R/r['path'])
expected={r['path'] for r in rows if r['changed']};assert set(g('diff','--name-only').splitlines())==expected
(D/'SOURCE_BINDING_CHANGE.patch').write_bytes(subprocess.check_output(['git','diff','--unified=0','--',*sorted(expected)],cwd=R))
(D/'STATUS.md').write_text('# Unit 5 generated evidence applied; runtime pending\n\nTwo authentic153-file renders match. Only five reviewed identity tokens change in95of155records; all60worksheets and every nonidentity byte stay exact. Prior95versions archived. Current source and pack bindings reflect the versioned Unit5policy; no approval fields change. Fresh discovery and affected144-method/21-full-class runtime are pending. Runtime includes historical fixtures and must not be described as all current-pack acceptance. All48findings remain open; physicalLAST; no APK assembly/install/signing/merge/freeze/approval.\n')
save('SHA256_PREPARATION.json',{f.relative_to(D).as_posix():sha(f.read_bytes()) for f in sorted(D.rglob('*')) if f.is_file() and f.name!='SHA256_PREPARATION.json'})
print('Applied',p['changedFiles'],'changed; preserved',len(preserved),'earlier records')
