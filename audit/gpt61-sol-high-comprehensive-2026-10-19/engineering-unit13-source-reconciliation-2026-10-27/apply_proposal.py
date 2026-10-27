"""Apply only independently reviewed identity deltas; preserve all previous bytes."""
from pathlib import Path
import json,subprocess,hashlib,zipfile,shutil,re
R=Path('/workspace/russian-companion');W=Path(__file__).parent;D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit13-source-reconciliation-2026-10-27'
G=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip();sha=lambda b:hashlib.sha256(b).hexdigest();save=lambda n,v:(D/n).write_text(json.dumps(v,indent=2)+'\n')
p=json.loads((W/'PROPOSAL_REVIEW.json').read_text());ctx=json.loads((W/'GENERATION_PREPARATION.json').read_text());con=json.loads((D/'CONSERVATION.json').read_text());assert all(row[3:].startswith('audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit13-source-reconciliation-2026-10-27/') for row in G('status','--porcelain').splitlines()) and not G('diff','HEAD','--name-only','--','app/src/main','app/src/test','catalog','docs','review','release') and G('rev-parse','HEAD')==p['sourceCommit']==ctx['sourceCommit']
assert (p['renderedFiles'],p['proposedFiles'],p['changedFiles'],p['unchangedFiles'])==(153,155,95,60)
assert ctx['newDigest']=='3c0acbb95297cf10f08da2bd8391463067eece22fec785158b5d2680d37dc2ba'
for n,h in con['immutablePriorEvidence'].items():assert sha((R/n).read_bytes())==h,n
for n,h in con['archives'].items():
 assert sha((R/n).read_bytes())==h
 with zipfile.ZipFile(R/n) as z:assert z.testzip() is None,n
assert G('rev-parse','HEAD:app/src/main')==con['unchangedProductionTree']
ix=[]
with zipfile.ZipFile(D/'HISTORICAL_PRIOR_CURRENT.zip','x',zipfile.ZIP_DEFLATED) as z:
 for row in p['files']:
  b=(R/row['path']).read_bytes();assert sha(b)==row['oldSha256'] and G('rev-parse',ctx['sourceCommit']+':'+row['path'])==row['originalGitBlob']
  if row['changed']:z.writestr(row['path'],b);ix.append(dict(path=row['path'],sourceCommit=ctx['sourceCommit'],gitBlob=row['originalGitBlob'],sha256=sha(b),bytes=len(b),historical=True))
save('HISTORICAL_PRIOR_CURRENT_INDEX.json',ix)
files={}
for folder in ['CANDIDATE_1','CANDIDATE_2','PROPOSAL']:
 for f in (W/folder).rglob('*'):
  if f.is_file():files[f.relative_to(W).as_posix()]=f
for f in W.iterdir():
 if f.is_file():files['TOOLING/'+f.name]=f
index={}
with zipfile.ZipFile(D/'RAW_GENERATION.zip','x',zipfile.ZIP_DEFLATED) as z:
 for n,f in sorted(files.items()):assert not n.lower().endswith('.pdf');z.write(f,n);index[n]=dict(bytes=f.stat().st_size,sha256=sha(f.read_bytes()))
save('RAW_GENERATION_INDEX.json',index)
for n in ['GENERATION_PREPARATION.json','GENERATION_RESULTS.json','COMPILE_CONTEXT.json','PROPOSAL_REVIEW.json']:shutil.copy2(W/n,D/n)
old_dossier=(R/'audit/curriculum-readiness/V2_CURRICULUM_GAP_CLOSURE_DOSSIER_v1.0.md').read_text()
for row in p['files']:
 f=W/'PROPOSAL'/row['path'];assert sha(f.read_bytes())==row['newSha256']
 if row['changed']:shutil.copyfile(f,R/row['path'])
new_dossier=(R/'audit/curriculum-readiness/V2_CURRICULUM_GAP_CLOSURE_DOSSIER_v1.0.md').read_text();assert [l for l in old_dossier.splitlines() if 'positive-existence implementation' in l]==[l for l in new_dossier.splitlines() if 'positive-existence implementation' in l]
for u in range(1,31):
 n=f'review/unit-{u}/UNIT_{u}_REVIEW_MANIFEST.json';old=json.loads(subprocess.check_output(['git','show',ctx['sourceCommit']+':'+n],cwd=R));new=json.loads((R/n).read_text());assert new['curriculumSourceCommit']==(ctx['actualVerifiedProductionSource'] if u==13 else old['curriculumSourceCommit'])
args=['/workspace/jdk21/bin/java','-cp',ctx['classpath'],'Digest',str(R)];out=subprocess.run(args,cwd=R,text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT);(D/'AFTER_APPLY_DIGEST.log').write_text(out.stdout);vals=re.findall(r'(?m)^[0-9a-f]{64}$',out.stdout);assert out.returncode==0 and vals==[ctx['newDigest']]
save('AFTER_APPLY_DIGEST.json',dict(headBeforeCommit=G('rev-parse','HEAD'),workingTree='REVIEWED_IDENTITY_RECORDS_APPLIED_PENDING_COMMIT',command=args,exitCode=out.returncode,repositorySourceDigest=vals[0],packContentReviewDigest='15c6ae393767cb7ffc190882867bfad923c9e81b07d0f4285cf0a678f2489280',stdoutSha256=sha(out.stdout.encode()),JUnitMethodsExecuted=0))
save('APPLIED_RECORDS.json',dict(reviewedSourceBeforeApply=ctx['sourceCommit'],actualVerifiedProductionSource=ctx['actualVerifiedProductionSource'],appliedChangedRecords=p['changedFiles'],unchangedRecords=p['unchangedFiles'],two153FileRendersMatch=True,all30PackagesValidatedPerRender=True,approvalFieldsUnchanged=True,historicalCitationsPreserved=True,currentRecordHashes={r['path']:r['newSha256'] for r in p['files']},historicalVersionsArchived=len(ix),repositorySourceDigest=ctx['newDigest'],packContentReviewDigest='15c6ae393767cb7ffc190882867bfad923c9e81b07d0f4285cf0a678f2489280',integrationStatus='PENDING_NO_NEW_RUNTIME_RESULT',generationJUnitMethodsExecuted=0))
text='''# Unit13 authentic bindings applied; affected verification pending

Two fresh JVM renders match153files each and validate all30packages each. Measured95of155records change only five identity values;60worksheets exact. Unit13 alone changes effective source to verified bd00e5082c9f31d2ae3b379dd6204dd3b28e4475;29other source choices and every reviewer/owner/rights/mapping/admission/approval field unchanged.95prior versions/Gitblobs archived as historical. Candidate remains unfrozen/sourceCommitnull/artifactNOT_RECORDED/publicReleaseNOT_APPROVED. Dossier changes only pack/source identity headers; historical source citations retained. Official production repository-source digest rechecked after applying records. No JUnit executed by generation or this application.

Repository source digest3c0acbb95297cf10f08da2bd8391463067eece22fec785158b5d2680d37dc2ba is DISTINCT from pack content-review digest15c6ae393767cb7ffc190882867bfad923c9e81b07d0f4285cf0a678f2489280. The prior core17/17test evidence remains immutable but its source-digest label was wrong. DIGEST_IDENTITY_CORRECTION.json explains the calls/values and preserves the initial mismatch before any generation/application. No test rerun or outcome change was needed to distinguish the two digests.

Strict13→12→11→10→9→8→7→6→5whole-pack proofs/current-source guard/pending kernel reference preserve original pins/assertions.19kernel-reference files/292bindings unchanged; only current kernel hash plus one pending engineering record appended, not semantic/rights acceptance. Three original tooling files archived.275prior test files remain exact except those three explicitly reviewed tooling/reference changes; whole actual baseline/core tests unchanged.74archives SHA/CRC verified. New two binding methods remain unexecuted at this checkpoint. Fresh affected verification and broad integration are pending; earlier17core/1458broad scopes are separate/unsummed.

All48OPEN/auditINCOMPLETE/four exporters16methods excluded:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance LAST after final independent comprehensive nonphysical audit. Qualified reviews/CC024semanticrisk/CC048authority conflict/originalUnit7restorecause outstanding. No APKassembly/install/signing/merge/freeze/formal advancement/releaseapproval; identity/signature null. PrivatePDF unread/unpackaged/environmentunchanged/publication user-confirmed independently unobserved/JITdirective provisional/unadopted/nativecauseUNCONFIRMED. Inspect external unit13-source-reconciliation-2026-10-27 before launch/edit.
'''
(D/'STATUS.md').write_text(text)
for name in ['prepare_tooling.py','prepare_generate.py','generate.py','review_proposal.py','apply_proposal.py']:shutil.copy2(W/name,D/name)
rel=D.relative_to(R).as_posix()
for n in ['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']:
 f=R/n;link=rel+'/STATUS.md' if n=='AGENTS.md' else '../'+rel.removeprefix('audit/')+'/STATUS.md';f.write_text(f'Current checkpoint: [Unit13 authentic bindings applied; affected verification pending]({link}). Two153file renders/all30packages validated;95of155identity-only changes/60worksheets exact/prior95historical. Sourcebd00e508/repositorydigest3c0acbb9…dc2ba DISTINCT from packdigest15c6ae39…89280; prior core label corrected in durable evidence, originals unchanged. Strict13→12→11→10→9→8→7→6→5proofs/historicalcitations conserved;74archives/wholebaseline/coreassertions exact. New2bindingmethods unexecuted; full affected/broad pending. All48OPEN/auditINCOMPLETE/four exporters excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'+f.read_text())
save('SHA256.json',{f.relative_to(D).as_posix():sha(f.read_bytes()) for f in sorted(D.rglob('*')) if f.is_file() and f.name!='SHA256.json'})
print(json.dumps(dict(applied=95,unchanged=60,historicalVersions=95,repositorySourceDigest=vals[0],other29UnitSourceChoicesUnchanged=True,newJUnitMethods=0)))
