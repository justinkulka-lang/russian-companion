"""Preserve an attached Cursor return outside the live repository; read-only checks only."""
from pathlib import Path
from datetime import datetime,timezone
import json,hashlib,subprocess,zipfile,sys,csv,io
R=Path('/workspace/russian-companion');W=Path(__file__).parent
B=R/'audit/gpt61-sol-high-comprehensive-2026-10-19'
A=B/'engineering-unit14-affected-integration-2026-10-28'
P=B/'engineering-unit14-historical-comparison-followup-2026-10-28'
D=B/'engineering-unit14-broad-host-regression-2026-10-28'
SOURCE=Path('/workspace/attachments/cd31fd9e-61e0-4cb4-ad1b-486416c2a5d0/Pasted text.txt')
sha=lambda data:hashlib.sha256(data).hexdigest()
save=lambda n,value:(W/n).write_text(json.dumps(value,indent=2)+'\n')
git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
blob=lambda c,n:subprocess.check_output(['git','show',c+':'+n],cwd=R)
pin='8eb9d3d4cc3fdbcd70d5b8904087ddad18acfaf1'
assert git('rev-parse','HEAD')==pin and not git('status','--porcelain')
raw=SOURCE.read_bytes();assert len(raw)==20227
(W/'RECEIVED_CURSOR_RETURN.txt').write_bytes(raw)
report=raw.split(b'## CURSOR_RECONCILIATION_REVIEW.md\r\n\r\n',1)[1].split(b'\r\n## CURSOR_FINDINGS.csv',1)[0]
(W/'CURSOR_RECONCILIATION_REVIEW.md').write_bytes(report)
section=raw.split(b'## CURSOR_FINDINGS.csv',1)[1].split(b'## CURSOR_METHOD_RECONCILIATION.csv',1)[0]
header=section.split(b'```csv\r\n',1)[1].split(b'```',1)[0]
(W/'CURSOR_FINDINGS.csv').write_bytes(header)
rows=list(csv.DictReader(io.StringIO(header.decode())));assert rows==[]
contexts=[]
for directory in [A,P,D]:
    for mode in ['DISCOVERY','REGRESSION']:
        path=directory/(mode+'_CONTEXT.json')
        if path.exists():
            value=json.loads(path.read_text())
            contexts.append(dict(directory=directory.name,mode=mode,sourceCommit=value['sourceCommit'],path=path.relative_to(R).as_posix(),sha256=sha(path.read_bytes())))
corrections=[dict(id='CURSOR-RECEIPT-META-001',type='AUDITOR_TABLE_SOURCE_IDENTITY_CORRECTION',originalClaim='Affected discovery source: same as affected runtime dd736e1a',actualDiscoverySource=contexts[0]['sourceCommit'],actualRuntimeSource=contexts[1]['sourceCommit'],evidence=contexts[0]['path'],appDefect=False,changesTestCounts=False),dict(id='CURSOR-RECEIPT-META-002',type='AUDITOR_TABLE_SOURCE_IDENTITY_CORRECTION',originalClaim='Follow-up discovery source: same as follow-up runtime 2faa5471',actualDiscoverySource=contexts[2]['sourceCommit'],actualRuntimeSource=contexts[3]['sourceCommit'],evidence=contexts[2]['path'],appDefect=False,changesTestCounts=False)]
save('SOURCE_IDENTITY_CORRECTIONS.json',dict(originalReturnUnchanged=True,corrections=corrections,primaryContexts=contexts))
asset='app/src/main/assets/content/v2-curriculum.json';old=blob('9101af8c182b3415426ed9226cb211a707a2c393',asset);new=blob('d72afd1232857a857624c20c58f4bcd3cb5c8564',asset)
assert old==blob('bd00e5082c9f31d2ae3b379dd6204dd3b28e4475',asset)
assert new==blob(pin,asset)
oldid=b'unit14-prior-reading-reported-time-v1';newid=b'unit14-prior-plural-possession-reported-time-v2'
assert old.count(oldid)==1 and new==old.replace(oldid,newid,1)
assert sha(old)=='765f06b3719192a5b0f19df553e3a5f20d00e2e7eb42130c908ceac15e4a9acf'
assert sha(new)=='02202d37e0aa2c51182e4db22feff177124b6db2c270a67a93ef85e188916c19'
checks=dict(assetOldBytes=len(old),assetOldSha256=sha(old),assetCurrentBytes=len(new),assetCurrentSha256=sha(new),singleDeclarationSubstitutionByteExact=True,producerAssetUnchangedAtPinnedHead=True)
sys.path.insert(0,str(P));from assertion_calls import assertions
reviews=json.loads((P/'COMPARISON_REVIEW.json').read_text());totalOld=totalNew=0
with zipfile.ZipFile(P/'ORIGINAL_COMPARISONS.zip') as z:
    assert z.testzip() is None
    for row in reviews:
        original=z.read(row['path']);current=blob(pin,row['path']);assert sha(original)==row['originalSha256'] and sha(current)==row['newSha256']
        before=assertions(original.decode());after=assertions(current.decode());index=0
        for call in after:
            if index<len(before) and call==before[index]:index+=1
        assert index==len(before);totalOld+=len(before);totalNew+=len(after)
assert (totalOld,totalNew)==(757,774)
checks.update(originalAssertionCalls=totalOld,newAssertionCalls=totalNew,originalCallsRetainedInOrder=True)
index=json.loads((A/'ACTUAL_FAILURES.json').read_text());archive=A/index['storage']['archive']
with zipfile.ZipFile(archive) as z:
    assert z.testzip() is None
    data=z.read(index['storage']['member']);assert sha(data)==index['storage']['memberSha256'] and len(data)==index['storage']['memberBytes']
    assert len(data)==147185270 and sha(data)=='ca6e78b5e77094119edd207a02b6e6e6f84e39ff4a9381baa394fdaa9a736508'
checks.update(fullFailureZipBytes=archive.stat().st_size,fullFailureZipSha256=sha(archive.read_bytes()),fullFailureMemberBytes=len(data),fullFailureMemberSha256=sha(data),fullFailureArchiveCrc=True)
checks['helperBlob']=git('rev-parse',pin+':app/src/test/java/dev/russiancompanion/v2/RecordedCurriculumConservationScope.kt')
assert checks['helperBlob']=='b14696e5136520b7e3e570bfcf756329dfa7976a'
checks.update(gradleRuns=0,junitRuns=0,apkBuilds=0,privateReferenceAccess=False,repositoryChanged=False)
save('CODEX_INDEPENDENT_CHECKS.json',checks)
save('RECEIPT.json',dict(receivedUtc=datetime.now(timezone.utc).isoformat(),reviewedCommit=pin,origin='User-pasted Cursor return attachment',sourceAttachmentId='file_000000008bf081f5bae031ab25b04966',sourceBytes=len(raw),sourceSha256=sha(raw),reviewVerdictAsReported='SUPPORTED WITHIN REVIEWED SCOPE',reviewerModelAsReported='Grok 4.7',reviewerEffortAsReported='not exposed',modelIndependentlyVerified=False,reviewerNoEditsNoExecutionClaimIndependentlyVerified=False,reviewedSevenFileScope=True,completeProjectAudit=False,reportedSubstantiatedFindings=0,reported14MethodAssessments='SUPPORTED;actual CSV not received',receiptStatus='PARTIAL_RETURN_WITH_SOURCE_TABLE_METADATA_CORRECTIONS',receivedDeliverables=['CURSOR_RECONCILIATION_REVIEW.md','CURSOR_FINDINGS.csv'],missingDeliverables=['CURSOR_METHOD_RECONCILIATION.csv','CURSOR_SCOPE_AND_EVIDENCE.json'],missingLocalCursorPaths=['/tmp/unit14-cursor-review-output','/tmp/rc-review'],correctionRecord='SOURCE_IDENTITY_CORRECTIONS.json',independentChecks='CODEX_INDEPENDENT_CHECKS.json',repositoryReceiptPublication='QUEUED_UNTIL_ACTIVE_BROAD_RUN_COMPLETION',all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST'))
assert git('rev-parse','HEAD')==pin and not git('status','--porcelain')
print(json.dumps(dict(receiptStatus='PARTIAL',receivedFiles=2,missingFiles=2,metadataCorrections=2,verifiedOriginalAssertions=totalOld,verifiedNewAssertions=totalNew,verifiedFullFailureBytes=147185270,repositoryClean=True,modelAsReported='Grok 4.7')))
