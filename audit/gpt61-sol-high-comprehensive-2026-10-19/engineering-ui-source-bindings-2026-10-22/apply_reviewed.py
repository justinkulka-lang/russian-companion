from pathlib import Path
import json,hashlib,zipfile,subprocess,shutil
R=Path('/workspace/russian-companion');W=Path(__file__).parent;D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-ui-source-bindings-2026-10-22';git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip();sha=lambda raw:hashlib.sha256(raw).hexdigest();proposal=json.loads((W/'PROPOSAL_REVIEW.json').read_text());ctx=json.loads((W/'GENERATION_PREPARATION.json').read_text());assert not git('status','--porcelain') and git('rev-parse','HEAD')==proposal['sourceCommit']
for row in proposal['files']:
 assert sha((R/row['path']).read_bytes())==row['oldSha256'] and sha((W/'CANDIDATE_1'/row['path']).read_bytes())==row['newSha256']
D.mkdir();save=lambda name,value:(D/name).write_text(json.dumps(value,indent=2)+'\n');index=[]
with zipfile.ZipFile(D/'HISTORICAL_PRIOR_CURRENT.zip','w',zipfile.ZIP_DEFLATED) as archive:
 for row in proposal['files']:
  rec=dict(row,historical=True,originalMeaning='Prior pending engineering/review inputs at cd8c9342, not accepted audit/release evidence')
  if row['changed']:archive.write(R/row['path'],row['path']);rec['originalLocation']='HISTORICAL_PRIOR_CURRENT.zip::'+row['path']
  else:rec['originalLocation']='REUSED_UNCHANGED_CURRENT_FILE::'+row['path']
  index.append(rec)
with zipfile.ZipFile(D/'HISTORICAL_PRIOR_CURRENT.zip') as archive:
 assert archive.testzip() is None and len(archive.namelist())==95
 for row in index:
  if row['changed']:assert sha(archive.read(row['path']))==row['oldSha256']
save('HISTORICAL_PRIOR_CURRENT_INDEX.json',dict(kind='HISTORICAL_PENDING_BINDINGS_NOT_CURRENT_ACCEPTANCE',originalCommit=proposal['sourceCommit'],archive='HISTORICAL_PRIOR_CURRENT.zip',archiveSha256=sha((D/'HISTORICAL_PRIOR_CURRENT.zip').read_bytes()),archivedChangedFiles=95,reusedUnchangedFiles=60,files=index))
for name in ['GENERATION_PREPARATION.json','GENERATION_RESULTS.json','PROPOSAL_REVIEW.json','GeneratePendingEvidence.java','prepare_generation.py','generate.py','review_proposal.py','apply_reviewed.py','run.py','resources.gradle','SELECTED_SCOPE.json']:shutil.copyfile(W/name,D/name)
logs=['COMPILE.log','CANDIDATE_1.log','CANDIDATE_2.log','GENERATION_LAUNCHER.log']
with zipfile.ZipFile(D/'RAW_GENERATION.zip','w',zipfile.ZIP_DEFLATED) as archive:
 for name in logs:archive.write(W/name,name)
save('RAW_GENERATION_INDEX.json',{name:dict(sha256=sha((W/name).read_bytes()),bytes=(W/name).stat().st_size) for name in logs})
base=R/'audit/gpt61-sol-high-comprehensive-2026-10-19';preserved={}
for name in ['engineering-source-binding-reconciliation-2026-10-22','engineering-v2-insets-accessibility-2026-10-22','final-independent-recheck-results/2026-10-22-claude-code','post-recording-guard-host-regression-2026-10-22','engineering-backup-recording-filenames-2026-10-22','engineering-backup-archive-boundaries-2026-10-22','post-unit3-isolation-host-regression-2026-10-21']:
 for file in sorted((base/name).rglob('*')):
  if file.is_file():preserved[file.relative_to(R).as_posix()]=sha(file.read_bytes())
save('PRESERVED_PRECEDING_SCOPES.json',preserved)
for row in index:
 if row['changed']:shutil.copyfile(W/'CANDIDATE_1'/row['path'],R/row['path'])
expected={row['path'] for row in index if row['changed']};assert set(git('diff','--name-only').splitlines())==expected
assert not git('diff','--name-only','--','app/src/main','app/src/test','catalog','docs')
(D/'SOURCE_BINDING_CHANGE.patch').write_bytes(subprocess.check_output(['git','diff','--unified=0','--',*sorted(expected)],cwd=R))
summary='Current UI source bindings applied; runtime verification pending. Canonical Kotlin digest matches 7ce2727f… at real root and projection; two fresh 153-file renderer outputs match. Of155 proposed records, exactly95 replace only eb5c52ed… with7ce2727f…; every other byte is exact and60 worksheets are unchanged. Original95 versions are archived as historical. Candidate remains unfrozen/sourceCommit null/artifact NOT_RECORDED/publicRelease NOT_APPROVED. No app/test/catalog/governance, approval or prior evidence edits. Planned fresh discovery and actual67-method/11-full-class scope remain unexecuted. Four screenshot exporters/16methods excluded; all48 independent findings unclosed; physical acceptance LAST; no APK assembly/install/signing, merge, freeze or formal advancement.'
(D/'STATUS.md').write_text('# UI source bindings applied; verification pending\n\n'+summary+'\n\nUse the external /workspace/artifacts/ui-source-binding-reconciliation-2026-10-22/{DISCOVERY,REGRESSION}/CONTEXT.json,RESULT.json,RUN.exit before claiming runtime state or launching another job. Never rerun mutating generation/application scripts over these immutable outputs.\n')
for name in ['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']:
 file=R/name;head,rest=file.read_text().split('\n',1);file.write_text(head+'\n\nCurrent checkpoint: [UI source bindings applied; verification pending](audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-ui-source-bindings-2026-10-22/STATUS.md). '+summary+'\n'+rest)
save('SHA256_PREPARATION.json',{file.relative_to(D).as_posix():sha(file.read_bytes()) for file in sorted(D.rglob('*')) if file.is_file() and file.name!='SHA256_PREPARATION.json'})
print(summary)
