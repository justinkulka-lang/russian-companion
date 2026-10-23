from pathlib import Path
import json,hashlib,zipfile,subprocess,shutil
R=Path('/workspace/russian-companion');W=Path(__file__).parent;D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-saved-review-entry-label-2026-10-23/RECONCILIATION';g=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip();sha=lambda b:hashlib.sha256(b).hexdigest();save=lambda n,v:(D/n).write_text(json.dumps(v,indent=2)+'\n');p=json.loads((W/'PROPOSAL_REVIEW.json').read_text());c=json.loads((W/'GENERATION_PREPARATION.json').read_text());assert not g('status','--porcelain') and g('rev-parse','HEAD')==p['sourceCommit'];assert p['changedFiles']==95 and p['unchangedProposedFiles']==60
for r in p['files']:assert sha((R/r['path']).read_bytes())==r['oldSha256'] and sha((W/'CANDIDATE_1'/r['path']).read_bytes())==r['newSha256']
D.mkdir()
with zipfile.ZipFile(D/'HISTORICAL_PRIOR_CURRENT.zip','w',zipfile.ZIP_DEFLATED) as z:
 for r in p['files']:
  if r['changed']:z.write(R/r['path'],r['path'])
with zipfile.ZipFile(D/'HISTORICAL_PRIOR_CURRENT.zip') as z:
 assert z.testzip() is None and len(z.namelist())==95
 for r in p['files']:
  if r['changed']:assert sha(z.read(r['path']))==r['oldSha256']
save('HISTORICAL_PRIOR_CURRENT_INDEX.json',dict(kind='HISTORICAL_PENDING_BINDINGS_NOT_APPROVAL',originalCommit=p['sourceCommit'],archive='HISTORICAL_PRIOR_CURRENT.zip',archiveSha256=sha((D/'HISTORICAL_PRIOR_CURRENT.zip').read_bytes()),files=p['files'],archivedChangedFiles=95,reusedUnchangedFiles=60))
for n in ['GENERATION_PREPARATION.json','GENERATION_RESULTS.json','PROPOSAL_REVIEW.json','Digest.java','GeneratePendingEvidence.java','generate.py','review_proposal.py','apply_reviewed.py','run.py','resources.gradle']:shutil.copyfile(W/n,D/n)
logs={n:W/n for n in ['COMPILE.log','CANDIDATE_1.log','CANDIDATE_2.log']};logs['CORRECTION_COMPILE.log']=W.parent/'CORRECTION_COMPILE.log'
with zipfile.ZipFile(D/'RAW_GENERATION.zip','w',zipfile.ZIP_DEFLATED) as z:
 for n,f in logs.items():z.write(f,n)
save('RAW_GENERATION_INDEX.json',{n:dict(bytes=f.stat().st_size,sha256=sha(f.read_bytes())) for n,f in logs.items()})
save('CORRECTION_COMPILE_CONTEXT.json',dict(sourceCommit=p['sourceCommit'],workingDirectory=str(R),activation='source /workspace/setup/android-env.sh',command=['./gradlew','-I','/workspace/setup/cloud-validation.gradle','compileDebugUnitTestKotlin','--offline','--max-workers=2','--console=plain'],exitCode=0,exactLaunchTime='Not separately captured; complete stdout preserved',result='BUILD SUCCESSFUL; main and test compilation after entry-label edit'))
preserved={}
for name in ['engineering-unit5-current-pack-history-2026-10-22','engineering-cc020-unit5-grammar-2026-10-22','engineering-ui-source-bindings-2026-10-22','final-independent-recheck-results/2026-10-22-claude-code']:
 for f in sorted((R/'audit/gpt61-sol-high-comprehensive-2026-10-19'/name).rglob('*')):
  if f.is_file():preserved[f.relative_to(R).as_posix()]=sha(f.read_bytes())
for f in D.parent.rglob('*'):
 if f.is_file() and D not in f.parents:preserved[f.relative_to(R).as_posix()]=sha(f.read_bytes())
save('PRESERVED_PRECEDING_SCOPES.json',preserved)
for r in p['files']:
 if r['changed']:shutil.copyfile(W/'CANDIDATE_1'/r['path'],R/r['path'])
expected={r['path'] for r in p['files'] if r['changed']};assert set(g('diff','--name-only').splitlines())==expected
(D/'SOURCE_BINDING_CHANGE.patch').write_bytes(subprocess.check_output(['git','diff','--unified=0','--',*sorted(expected)],cwd=R))
(D/'STATUS.md').write_text('# Entry-label source bindings applied; verification pending\n\nTwo authentic153-file renders agree and canonical Kotlin digest matches real root/projection. Exactly95of155records replace only the old global repository-source digest with the new digest; every other byte and60worksheets stay exact. Prior95versions retained as historical. No curriculum policy/source pins or review/owner/approval fields changed. Candidate unfrozen/sourceCommitnull/artifactNOT_RECORDED/publicReleaseNOT_APPROVED. Baseline2reported/1pass/1fail remains separate. Fresh66method/13full-class discovery and actual runtime pending; not a full suite. All48findings open; physicalLAST; no APK assembly/install/signing, merge/freeze/approval.\n')
save('SHA256_PREPARATION.json',{f.relative_to(D).as_posix():sha(f.read_bytes()) for f in sorted(D.rglob('*')) if f.is_file() and f.name!='SHA256_PREPARATION.json'})
print('Applied95global-digest-only changes;60worksheets and every other byte unchanged; prior95versions retained.')
