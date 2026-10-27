from pathlib import Path
from datetime import datetime,timezone
import json,subprocess,hashlib,zipfile,shutil,re
R=Path('/workspace/russian-companion');W=Path(__file__).parent;D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit12-source-reconciliation-2026-10-27';G=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip();sha=lambda b:hashlib.sha256(b).hexdigest();save=lambda p,x:p.write_text(json.dumps(x,indent=2)+'\n');p=json.loads((W/'PROPOSAL_REVIEW.json').read_text());ctx=json.loads((W/'GENERATION_PREPARATION.json').read_text());assert not G('status','--porcelain') and G('rev-parse','HEAD')==p['sourceCommit']==ctx['sourceCommit'];assert (p['renderedFiles'],p['proposedFiles'],p['changedFiles'],p['unchangedFiles'])==(153,155,95,60)
ix=[]
with zipfile.ZipFile(D/'HISTORICAL_PRIOR_CURRENT.zip','x',zipfile.ZIP_DEFLATED) as z:
 for row in p['files']:
  b=(R/row['path']).read_bytes();assert sha(b)==row['oldSha256'] and G('rev-parse',ctx['sourceCommit']+':'+row['path'])==row['originalGitBlob']
  if row['changed']:z.writestr(row['path'],b);ix.append(dict(path=row['path'],sourceCommit=ctx['sourceCommit'],gitBlob=row['originalGitBlob'],sha256=sha(b),bytes=len(b),historical=True))
save(D/'HISTORICAL_PRIOR_CURRENT_INDEX.json',ix)
files={}
for folder in ['CANDIDATE_1','CANDIDATE_2']:
 for f in (W/folder).rglob('*'):
  if f.is_file():files[f.relative_to(W).as_posix()]=f
for f in W.iterdir():
 if f.is_file() and f.name not in ['COMPILE_PROCESS.json','GENERATION_PROCESS.json']:files[f.name]=f
for n in ['release/acceptance/candidate.json','audit/curriculum-readiness/V2_CURRICULUM_GAP_CLOSURE_DOSSIER_v1.0.md']:files['PROPOSED_EXTRAS/'+n]=W/'PROPOSAL'/n
raw={}
with zipfile.ZipFile(D/'RAW_GENERATION.zip','x',zipfile.ZIP_DEFLATED) as z:
 for n,f in sorted(files.items()):assert not n.lower().endswith('.pdf');z.write(f,n);raw[n]=dict(bytes=f.stat().st_size,sha256=sha(f.read_bytes()))
save(D/'RAW_GENERATION_INDEX.json',raw);shutil.copy2(W/'PROPOSAL_REVIEW.json',D/'PROPOSAL_REVIEW.json');shutil.copy2(W/'GENERATION_PREPARATION.json',D/'GENERATION_PREPARATION.json');shutil.copy2(W/'COMPILE_CONTEXT.json',D/'COMPILE_CONTEXT.json')
con=json.loads((D/'CONSERVATION.json').read_text())
for n,h in con['immutablePriorEvidence'].items():assert sha((R/n).read_bytes())==h
for n,h in con['archives'].items():
 assert sha((R/n).read_bytes())==h
 with zipfile.ZipFile(R/n) as z:assert z.testzip() is None
old_dossier=(R/'audit/curriculum-readiness/V2_CURRICULUM_GAP_CLOSURE_DOSSIER_v1.0.md').read_text()
for row in p['files']:
 f=W/'PROPOSAL'/row['path'];assert sha(f.read_bytes())==row['newSha256']
 if row['changed']:shutil.copyfile(f,R/row['path'])
new_dossier=(R/'audit/curriculum-readiness/V2_CURRICULUM_GAP_CLOSURE_DOSSIER_v1.0.md').read_text();assert [l for l in old_dossier.splitlines() if 'positive-existence implementation' in l]==[l for l in new_dossier.splitlines() if 'positive-existence implementation' in l]
for u in range(1,31):
 n=f'review/unit-{u}/UNIT_{u}_REVIEW_MANIFEST.json';old=json.loads(subprocess.check_output(['git','show',ctx['sourceCommit']+':'+n],cwd=R));new=json.loads((R/n).read_text());assert new['curriculumSourceCommit']==('ca0fdaeca8de81ab6a5d4935fbcdcb068c4af913' if u==12 else old['curriculumSourceCommit'])
save(D/'APPLIED_RECORDS.json',dict(reviewedSourceBeforeApply=ctx['sourceCommit'],actualVerifiedProductionSource=ctx['actualVerifiedProductionSource'],appliedChangedRecords=95,unchangedRecords=60,two153FileRendersMatch=True,approvalFieldsUnchanged=True,historicalCitationsPreserved=True,currentRecordHashes={r['path']:r['newSha256'] for r in p['files']},integrationStatus='PENDING_NO_NEW_RUNTIME_RESULT'))
args=['/workspace/jdk21/bin/java','-cp',ctx['classpath'],'Digest',str(R)];out=subprocess.run(args,cwd=R,text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT);(W/'AFTER_APPLY_DIGEST.log').write_text(out.stdout);vals=re.findall(r'(?m)^[0-9a-f]{64}$',out.stdout);assert out.returncode==0 and vals==[ctx['newDigest']];save(D/'AFTER_APPLY_DIGEST.json',dict(headBeforeCommit=G('rev-parse','HEAD'),workingTree='REVIEWED_95_RECORDS_APPLIED_PENDING_COMMIT',command=args,exitCode=out.returncode,digest=vals[0],stdoutSha256=sha(out.stdout.encode()),JUnitMethodsExecuted=0));shutil.copy2(W/'AFTER_APPLY_DIGEST.log',D/'AFTER_APPLY_DIGEST.log')
text='''# Unit12 authentic bindings applied; integration pending

Two fresh JVM renders match153files each and validate all30packages each.95of155records change only five reviewed identity tokens;60worksheets exact. Current Unit12 sourceca0fdaeca8de81ab6a5d4935fbcdcb068c4af913/officialdigest84b6fda72017ec2dfc2b4add6c0e82b1a1e9f40769773f18d50caee4cf7a2355;all29other source choices and every reviewer/owner/rights/mapping/admission/approval field unchanged.95prior versions/Gitblobs historical;candidate unfrozen/sourceCommitnull/artifactNOT_RECORDED/publicReleaseNOT_APPROVED. Dossier changes only pack/source identity headers;historical source citation retained. Official Kotlin digest rechecked after application. Both fresh renders and the mechanical proposal were independently compared before application. No JUnit executed by generation/verification.

Tooling has strict12→11→10→9→8→7→6→5whole-pack proof and pending source-reference hash,not qualified semantic/rights acceptance. New2binding methods are inventory only. Existing assertions/pins/wholebaseline/coretests/73archives preserved. Next:fresh affected full-class discovery/runtime,retain all raw outcomes before any comparison changes. No new integration or broad result claimed;earlier14/1425/320/111scopes remain separate/unsummed.

All48OPEN/auditINCOMPLETE/four exporters16methods explicitly excluded:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. PhysicalLAST after final independent comprehensive nonphysical audit. NoAPKassembly/install/signing/merge/freeze/formal advancement/releaseapproval;APKidentity/signature null. Qualified reviews/CC048conflict/originalUnit7restorecause outstanding. Private reference/environment settings unchanged;JITdirective unadopted/native causeUNCONFIRMED. Inspect external unit12-source-reconciliation-2026-10-27 before launch/edit.
''';(D/'STATUS.md').write_text(text)
for f in [R/'AGENTS.md',R/'audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md',R/'audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']:
 link=('audit/' if f.name=='AGENTS.md' else '../')+'gpt61-sol-high-comprehensive-2026-10-19/engineering-unit12-source-reconciliation-2026-10-27/STATUS.md';f.write_text('Current checkpoint: [Unit12 authentic bindings applied;integration pending]('+link+'). Two153file renders/all30packages validated;95of155identity-only changes/60worksheets exact/prior95historical. Sourcec2d297e7/61bbaafe…1b19;strict12→11→10→9→8→7→6→5proofs/historicalcitation unchanged. Fresh affected discovery/runtime pending;no new runtime result.73archives/wholebaseline/coreassertions conserved;all48OPEN/auditINCOMPLETE/physicalLAST/four exporters excluded/noAPK/signing/merge/freeze/approval.\n\n'+f.read_text())
save(D/'SHA256.json',{f.relative_to(D).as_posix():sha(f.read_bytes()) for f in sorted(D.rglob('*')) if f.is_file() and f.name!='SHA256.json'});print(json.dumps(dict(applied=95,unchanged=60,historicalVersionsArchived=95,allOther29UnitSourceChoicesUnchanged=True,afterApplyOfficialDigest=vals[0],newRuntimeMethods=0)))
