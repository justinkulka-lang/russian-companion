from pathlib import Path
import json,hashlib,subprocess,zipfile,shutil
R=Path('/workspace/russian-companion');W=Path(__file__).parent;D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit6-source-reconciliation-2026-10-23'
sha=lambda b:hashlib.sha256(b).hexdigest();git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip();save=lambda n,v:(D/n).write_text(json.dumps(v,indent=2)+'\n')
p=json.loads((W/'PROPOSAL_REVIEW.json').read_text());assert not git('status','--porcelain') and git('rev-parse','HEAD')==p['sourceCommit']
for row in p['files']:assert sha((R/row['path']).read_bytes())==row['oldSha256'] and sha((W/'PROPOSAL'/row['path']).read_bytes())==row['newSha256']
D.mkdir()
with zipfile.ZipFile(D/'HISTORICAL_PRIOR_CURRENT.zip','x',zipfile.ZIP_DEFLATED) as z:
 for row in p['files']:
  if row['changed']:z.write(R/row['path'],row['path'])
with zipfile.ZipFile(D/'HISTORICAL_PRIOR_CURRENT.zip') as z:
 assert z.testzip() is None and len(z.namelist())==p['changedFiles']
 for row in p['files']:
  if row['changed']:assert sha(z.read(row['path']))==row['oldSha256']
save('HISTORICAL_PRIOR_CURRENT_INDEX.json',dict(kind='HISTORICAL_PENDING_BINDINGS_NOT_APPROVAL',originalCommit=p['sourceCommit'],archive='HISTORICAL_PRIOR_CURRENT.zip',archiveSHA256=sha((D/'HISTORICAL_PRIOR_CURRENT.zip').read_bytes()),files=p['files'],archivedChangedFiles=p['changedFiles'],reusedUnchangedFiles=p['unchangedFiles']))
for name in ['GENERATION_PREPARATION.json','GENERATION_RESULTS.json','PROPOSAL_REVIEW.json','Digest.java','GeneratePendingEvidence.java','generate.py','review_proposal.py','apply_reviewed.py','COMPILE_CONTEXT.json','ORIGINAL_ASSERTION_CONSERVATION.json',*[p.name for p in W.glob('COMPILED_INDEX_*.json')]]:shutil.copyfile(W/name,D/name)
logs=['SOURCE_COMPILE.log','COMPILE.log','CANDIDATE_1.log','CANDIDATE_2.log'];index=[]
with zipfile.ZipFile(D/'RAW_GENERATION.zip','x',zipfile.ZIP_DEFLATED) as z:
 for name in logs:
  data=(W/name).read_bytes();z.writestr(name,data);index.append(dict(path=name,bytes=len(data),sha256=sha(data)))
save('RAW_GENERATION_INDEX.json',index)
save('RENDER_OUTPUT_SHA256.json',{label:{f.relative_to(W/label).as_posix():sha(f.read_bytes()) for f in sorted((W/label).rglob('*')) if f.is_file()} for label in ['CANDIDATE_1','CANDIDATE_2']})
# Preserve preceding scopes by original tracked bytes and all original archive CRCs.
preserved={}
for name in ['engineering-unit6-present-correction-2026-10-23','engineering-unit6-present-baseline-2026-10-23','engineering-unit6-substantive-research-2026-10-23','engineering-unit5-current-pack-history-2026-10-22','engineering-saved-review-entry-label-2026-10-23','final-independent-recheck-results/2026-10-22-claude-code']:
 for f in sorted((R/'audit/gpt61-sol-high-comprehensive-2026-10-19'/name).rglob('*')):
  if f.is_file():preserved[f.relative_to(R).as_posix()]=sha(f.read_bytes())
save('PRESERVED_PRECEDING_SCOPES.json',preserved)
allowlist=json.loads((R/'audit/independent-gpt61-2026-10-12/current-comprehensive-audit/RETAINED_ARCHIVE_ALLOWLIST.json').read_text())['sha256'];archives=[]
for path,expected in sorted(allowlist.items()):
 assert sha((R/path).read_bytes())==expected
 with zipfile.ZipFile(R/path) as z:assert z.testzip() is None
 archives.append(dict(path=path,sha256=expected,crc='PASS'))
assert len(archives)==66;save('ORIGINAL_ARCHIVE_CONSERVATION.json',archives)
for row in p['files']:
 if row['changed']:shutil.copyfile(W/'PROPOSAL'/row['path'],R/row['path'])
assert set(git('diff','--name-only').splitlines())=={r['path'] for r in p['files'] if r['changed']}
(D/'SOURCE_BINDING_CHANGE.patch').write_bytes(subprocess.check_output(['git','diff','--unified=0','--',*[r['path'] for r in p['files'] if r['changed']]],cwd=R))
(D/'STATUS.md').write_text('# Unit6 v3 bindings applied; verification pending\n\nAt source3a5ccbb8, two fresh authentic153-file renders match.95of155records change only five reviewed identity tokens (global source,pack content,Unit6 content,actual Unit6 implementation commit,Unit6 review package); every other byte and60worksheets exact. Prior95versions archived. Source/fixture guards prove exact Unit6 and Unit5 declaration transitions; old mixed-v2 assertions retained and current-v3 lifecycle added. No app/test/catalog/governance/source admission/approval writes in this application step. Candidate unfrozen/sourceCommitnull/artifactNOT_RECORDED/publicReleaseNOT_APPROVED. Fresh85method/17full-class discovery/runtime planned,not yet executed; prior21/21and separate5/5remain separate. Four exporters/16methods excluded; all48findings open; physicalLAST; no assembly/install/signing/merge/freeze/approval.\n')
save('SHA256_PREPARATION.json',{f.relative_to(D).as_posix():sha(f.read_bytes()) for f in sorted(D.rglob('*')) if f.is_file() and f.name!='SHA256_PREPARATION.json'})
print('Applied reviewed records; prior versions archived;85method runtime pending.')
