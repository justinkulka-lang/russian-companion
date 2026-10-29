"""Apply only independently reviewed identity deltas, preserving every prior record."""
from pathlib import Path
import json,subprocess,hashlib,zipfile,shutil,re
R=Path('/workspace/russian-companion');W=Path(__file__).parent;D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit14-source-reconciliation-2026-10-28'
git=lambda *a:subprocess.check_output(['git',*a],cwd=R)
sha=lambda b:hashlib.sha256(b).hexdigest()
def save(n,v):(D/n).write_text(json.dumps(v,indent=2)+'\n')
p=json.loads((W/'PROPOSAL_REVIEW.json').read_text());ctx=json.loads((W/'GENERATION_PREPARATION.json').read_text());con=json.loads((D/'CONSERVATION.json').read_text())
assert not git('status','--porcelain=v1','-z') and git('rev-parse','HEAD').decode().strip()==p['sourceCommit']==ctx['sourceCommit']
assert p['renderedFiles']==153 and p['proposedFiles']==len(p['files'])==155 and p['changedFiles']+p['unchangedFiles']==155
assert ctx['newDigest']==con['newRepositorySourceDigest']=='487d9118d7154dd59162dbb7024f1349bc53d5b7bf89e9d79022d97eaa1eeb9a'
for n,h in con['immutablePriorEvidence'].items():assert sha((R/n).read_bytes())==h,n
for n,h in con['currentRecordHashes'].items():assert sha((R/n).read_bytes())==h,n
for n,h in con['allPriorTestHashes'].items():
    if n not in con['changedToolingFiles']:assert sha((R/n).read_bytes())==h,n
for n,h in con['archives'].items():
    assert sha((R/n).read_bytes())==h
    with zipfile.ZipFile(R/n) as z:assert z.testzip() is None,n
assert git('rev-parse','HEAD:app/src/main').decode().strip()==con['unchangedProductionTree']
ix=[]
with zipfile.ZipFile(D/'HISTORICAL_PRIOR_CURRENT.zip','x',zipfile.ZIP_DEFLATED) as z:
    for row in p['files']:
        n=row['path'];b=(R/n).read_bytes();assert sha(b)==row['oldSha256'] and git('rev-parse',ctx['sourceCommit']+':'+n).decode().strip()==row['originalGitBlob']
        assert sha((W/'PROPOSAL'/n).read_bytes())==row['newSha256']
        if row['changed']:z.writestr(n,b);ix.append(dict(path=n,sourceCommit=ctx['sourceCommit'],gitBlob=row['originalGitBlob'],sha256=sha(b),bytes=len(b),historical=True))
assert len(ix)==p['changedFiles'];save('HISTORICAL_PRIOR_CURRENT_INDEX.json',ix)
files={}
for folder in ['CANDIDATE_1','CANDIDATE_2','PROPOSAL']:
    for f in (W/folder).rglob('*'):
        if f.is_file():files[f.relative_to(W).as_posix()]=f
for f in W.iterdir():
    if f.is_file():files['TOOLING/'+f.name]=f
files['INPUT_PROJECTION/release/acceptance/candidate.json']=W/'INPUT_PROJECTION/release/acceptance/candidate.json'
index={}
with zipfile.ZipFile(D/'RAW_GENERATION.zip','x',zipfile.ZIP_DEFLATED) as z:
    for n,f in sorted(files.items()):
        assert not n.lower().endswith('.pdf');z.write(f,n);index[n]=dict(bytes=f.stat().st_size,sha256=sha(f.read_bytes()))
save('RAW_GENERATION_INDEX.json',index)
for n in ['GENERATION_PREPARATION.json','GENERATION_RESULTS.json','COMPILE_CONTEXT.json','PROPOSAL_REVIEW.json']:shutil.copy2(W/n,D/n)
dossier='audit/curriculum-readiness/V2_CURRICULUM_GAP_CLOSURE_DOSSIER_v1.0.md';oldDossier=(R/dossier).read_text()
for row in p['files']:
    if row['changed']:shutil.copyfile(W/'PROPOSAL'/row['path'],R/row['path'])
assert [x for x in oldDossier.splitlines() if 'positive-existence implementation' in x]==[x for x in (R/dossier).read_text().splitlines() if 'positive-existence implementation' in x]
for u in range(1,31):
    n=f'review/unit-{u}/UNIT_{u}_REVIEW_MANIFEST.json';old=json.loads(git('show',ctx['sourceCommit']+':'+n));new=json.loads((R/n).read_text());assert new['curriculumSourceCommit']==(ctx['actualVerifiedProductionSource'] if u==14 else old['curriculumSourceCommit'])
args=['/workspace/jdk21/bin/java','-cp',ctx['classpath'],'Digest',str(R)];out=subprocess.run(args,cwd=R,text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT);(D/'AFTER_APPLY_DIGEST.log').write_text(out.stdout)
values=re.findall(r'(?m)^[0-9a-f]{64}$',out.stdout);assert out.returncode==0 and values==[ctx['newDigest']]
save('AFTER_APPLY_DIGEST.json',dict(headBeforeCommit=git('rev-parse','HEAD').decode().strip(),command=args,exitCode=out.returncode,repositorySourceDigest=values[0],packContentReviewDigest=con['newPackContentReviewDigest'],stdoutSha256=sha(out.stdout.encode()),JUnitMethodsExecuted=0))
save('APPLIED_RECORDS.json',dict(reviewedSourceBeforeApply=ctx['sourceCommit'],actualVerifiedProductionSource=ctx['actualVerifiedProductionSource'],appliedChangedRecords=p['changedFiles'],unchangedRecords=p['unchangedFiles'],two153FileRendersMatch=True,all30PackagesValidatedPerRender=True,approvalFieldsUnchanged=True,historicalCitationsPreserved=True,currentRecordHashes={r['path']:r['newSha256'] for r in p['files']},historicalVersionsArchived=len(ix),repositorySourceDigest=ctx['newDigest'],packContentReviewDigest=con['newPackContentReviewDigest'],integrationStatus='PENDING_NO_NEW_RUNTIME_RESULT',generationJUnitMethodsExecuted=0))
(D/'STATUS.md').write_text(f'''# Unit14 authentic bindings applied;focused proofs pending

Two actual JVM renders match153files each,validating all30packages each. Measured{p['changedFiles']}of155identity-only record changes;{p['unchangedFiles']}unchanged. Priorversions/Gitblobs archived as historical. Only Unit14effective source changes to verifiedd72afd12;29othersources/reviewer/owner/rights/mapping/admission/approval fields exact. Candidate remains unfrozen/sourceCommitnull/artifactNOT_RECORDED/publicReleaseNOT_APPROVED. Dossier changes onlysource/pack headers,retains historical citations. Post-apply actualcompiled repositorydigest matches487d9118…eeb9a DISTINCT from packcontent39c3563b…e65ec. NoJUnitexecuted by compile/generation/apply.

Strictbefore14→13→12→11→10→9→8→7→6→5proofs preserve originalpins/assertions;old14source96242a17guard retained in provedprior scope,current14d72guard separate. Actualmatching9101kernel/reference authenticated;19files/292bindings andallhistory preserved,currentkernelhash plus onepending engineering append,not semantic/rights acceptance. Wholebaseline/coretests/77archives exact;280prior testfiles except3explicittooling/referencefiles conserved. New2bindingmethods stillunexecuted;focused/affected/broadpending.

All48OPEN/auditINCOMPLETE/four exporters16methods excluded/physicalGalaxyS26Ultra airplane-mode/reboot acceptanceLAST after final independent comprehensive nonphysical audit. NoAPKassembly/install/newsigning/merge/freeze/formaladvancement/releaseapproval;APKidentity/signaturenull. PrivatePDFunread/unpackaged;environmentunchanged/publication user-confirmed independently unobserved/JITdirectiveunadopted/nativecauseUNCONFIRMED. Oneagent/existingcheckout/DraftPR64.
''')
for n in ['prepare_tooling.py','prepare_generate.py','generate.py','review_proposal.py','apply_proposal.py']:shutil.copy2(W/n,D/n)
rel=D.relative_to(R).as_posix()
for name in ['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']:
    f=R/name;link=rel+'/STATUS.md' if name=='AGENTS.md' else '../'+rel.removeprefix('audit/')+'/STATUS.md'
    f.write_text(f'Current checkpoint: [Unit14authentic bindings applied;focused proofs pending]({link}).Two153file renders/30packages/{p["changedFiles"]}identity-only changes/{p["unchangedFiles"]}unchanged/priorversionshistorical;only14sourcechoice changed. Repository487d9118 DISTINCT pack39c3563b. Strict14→13→12→11→10→9→8→7→6→5/oldpins/19referencefiles292bindings/77archives/wholebaseline conserved.2newbindingmethods unexecuted;affected/broadpending. All48OPEN/auditINCOMPLETE/four exporters16methods excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'+f.read_text())
print(json.dumps(dict(applied=p['changedFiles'],unchanged=p['unchangedFiles'],historicalVersions=len(ix),repositorySourceDigest=values[0],packContentReviewDigest=con['newPackContentReviewDigest'],onlyUnit14SourceChanged=True,newJUnitMethodsExecuted=0)))
