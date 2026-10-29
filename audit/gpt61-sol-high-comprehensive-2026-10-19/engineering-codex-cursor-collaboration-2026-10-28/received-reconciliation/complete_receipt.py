"""Receive the missing pasted outputs; preserve initial receipt and live repository."""
from pathlib import Path
from datetime import datetime,timezone
import json,hashlib,csv,io,re,zipfile,subprocess
R=Path('/workspace/russian-companion');W=Path(__file__).parent
B=R/'audit/gpt61-sol-high-comprehensive-2026-10-19'
A=B/'engineering-unit14-affected-integration-2026-10-28'
P=B/'engineering-unit14-historical-comparison-followup-2026-10-28'
raw=Path('/workspace/attachments/f44ce689-5aee-479d-9315-3ef52133c507/Pasted text.txt').read_bytes()
sha=lambda data:hashlib.sha256(data).hexdigest()
save=lambda n,value:(W/n).write_text(json.dumps(value,indent=2)+'\n')
git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
pin='8eb9d3d4cc3fdbcd70d5b8904087ddad18acfaf1'
assert git('rev-parse','HEAD')==pin and not git('status','--porcelain')
assert len(raw)==28356
with (W/'RECEIVED_CURSOR_SUPPLEMENT.txt').open('xb') as f:f.write(raw)
csvdata=raw.split(b'```csv\r\n',1)[1].split(b'\r\n```',1)[0]+b'\r\n'
jsondata=raw.split(b'```json\r\n',1)[1].split(b'\r\n```',1)[0]+b'\r\n'
addendum=raw.split(b'## Source-identity addendum\r\n',1)[1]
for n,data in [('CURSOR_METHOD_RECONCILIATION.csv',csvdata),('CURSOR_SCOPE_AND_EVIDENCE.json',jsondata),('CURSOR_SOURCE_IDENTITY_ADDENDUM.md',addendum)]:
    with (W/n).open('xb') as f:f.write(data)
reader=csv.DictReader(io.StringIO(csvdata.decode()));fields=reader.fieldnames;rows=list(reader)
expectedFields=['test_class','method','original_failure_evidence','claimed_cause','actual_change','historical_or_current_scope','preserved_assertions','current_positive_controls','current_negative_controls','coverage_gap','assessment']
assert fields==expectedFields and len(rows)==14 and all(set(x)==set(fields) and all(x[k].strip() for k in fields) for x in rows)
expected=json.loads((P/'SCOPE.json').read_text())['priorFailedIdentities']
ids={(x['test_class'],x['method']) for x in rows};assert len(ids)==14 and ids=={(x['testClass'],x['method']) for x in expected}
assert {x['assessment'] for x in rows}=={'SUPPORTED'}
scope=json.loads(jsondata);assert scope['reviewedCommit']==pin and scope['verdict']=='SUPPORTED WITHIN REVIEWED SCOPE'
assert scope['auditFindingsRemainOpen']==48 and scope['auditStatus']=='INCOMPLETE'
assert scope['baselineCommit']=='ae82d742ce2f9fac89acb91f618224187922e5ca'
assert scope['sevenFileCorrectionCommit']=='0c6789a291c6170dab792c908dba5c9b379067fa'
assert scope['followUpSourceCommit']=='2faa5471c4cf36728c09684584a255268bd34adb'
assert scope['assertionScan']['originalInvocations']==757 and scope['assertionScan']['newInvocations']==774
for n in ['CURSOR_FINDINGS.csv','CURSOR_METHOD_RECONCILIATION.csv']:list(csv.DictReader(io.StringIO((W/n).read_text())))
index=json.loads((A/'ACTUAL_FAILURES.json').read_text());failures={(x['testClass'],x['method']):x for x in index['failures']}
locators=json.loads((A/'FAILURE_TRACE_LOCATORS.json').read_text());xmlHashes={(x['testClass'],x['method']):x['rawMemberSha256'] for x in locators['failures']}
hashChecks=[]
for row in rows:
    key=(row['test_class'],row['method']);actual=failures[key]
    hashes=re.findall(r'\b[0-9a-f]{64}\b',row['original_failure_evidence'])
    # Intake messages also contain the two unit digests; those are values, not message hashes.
    permitted={actual['messageSha256'],actual['traceSha256'],xmlHashes[key],
        '9e136e9af2fe3611d6862fa46e3217f85ad45c8b23ad622eec6f36e48ab97f38',
        '5b42bce13ad71142cbd536373d7ac5a3906839e0bc61ed9a69abecb10bbca171'}
    assert set(hashes)<=permitted,(key,hashes,actual)
    hashChecks.append(dict(testClass=key[0],method=key[1],explicitHashesChecked=hashes,messageBytes=actual['messageBytes'],messageSha256=actual['messageSha256'],traceSha256=actual['traceSha256']))
for old,row in [('8e0b82ddc0ff573697ce4ce9b18abe082ed2c2d0','Affected discovery'),('dd736e1a8657bd02a34063752afd82146ab2f842','Affected runtime'),('0c6789a291c6170dab792c908dba5c9b379067fa','Follow-up discovery'),('2faa5471c4cf36728c09684584a255268bd34adb','Follow-up runtime')]:
    assert old.encode() in addendum and row.encode() in addendum
archiveChecks=[]
for record in scope['inspectedArchiveMembers']:
    path=B/record['archive'];item=dict(path=path.relative_to(R).as_posix(),bytes=path.stat().st_size,sha256=sha(path.read_bytes()))
    if 'archiveBytes' in record:assert item['bytes']==record['archiveBytes']
    if 'archiveSha256' in record:assert item['sha256']==record['archiveSha256']
    with zipfile.ZipFile(path) as z:
        assert z.testzip() is None
        if 'member' in record:
            data=z.read(record['member']);assert len(data)==record['memberBytes'] and sha(data)==record['memberSha256']
        for n in record.get('membersChecked',[]):assert n in z.namelist()
    item['crcClean']=True;archiveChecks.append(item)
checks=dict(exact14OriginalFailureIdentities=True,all14ReportedSupported=True,allCsvFieldsPresent=True,scopeJsonParses=True,pinnedCommitMatches=True,sourceAddendumMatchesPrimaryContexts=True,methodEvidenceHashChecks=hashChecks,reportedArchiveShaAndCrcChecks=archiveChecks,originalReviewUnchanged=True,initialReceiptUnchanged=True,initialManifestUnchanged=True,gradleRuns=0,junitRuns=0,apkBuilds=0,privateReferenceAccess=False,externalSessionModelObserved=False,externalOriginalFileBytesIndependentlyObserved=False)
save('SUPPLEMENT_VERIFICATION.json',checks)
save('COMPLETE_RECEIPT.json',dict(receivedUtc=datetime.now(timezone.utc).isoformat(),status='COMPLETE_FOUR_PASTED_DELIVERABLES_WITH_SEPARATE_SOURCE_ADDENDUM',reviewedCommit=pin,verdictAsReported=scope['verdict'],reportedSubstantiatedFindings=0,methodAssessments=dict(SUPPORTED=14),receivedDeliverables=['CURSOR_RECONCILIATION_REVIEW.md','CURSOR_FINDINGS.csv','CURSOR_METHOD_RECONCILIATION.csv','CURSOR_SCOPE_AND_EVIDENCE.json'],sourceAddendum='CURSOR_SOURCE_IDENTITY_ADDENDUM.md',initialAttachmentId='file_000000008bf081f5bae031ab25b04966',supplementAttachmentId='file_000000000fbc81f98fd273ff8da0973f',supplementBytes=len(raw),supplementSha256=sha(raw),reviewerModelAsReported=scope['model']['name'],reviewerEffortAsReported=scope['model']['effort'],modelIndependentlyVerified=False,deliveryForm='Two user-pasted attachments;original received bytes preserved;code-fence payloads extracted with CRLF',originalExternalFileBytesIndependentlyVerified=False,originalReviewAndInitialReceiptPreserved=True,metadataCorrectionsRecordedSeparately=True,repositoryPublication='QUEUED_UNTIL_EXCLUSIVE_BROAD_RUNTIME_COMPLETION',all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST'))
assert git('rev-parse','HEAD')==pin and not git('status','--porcelain')
print(json.dumps(dict(receiptStatus='COMPLETE_FOUR_PASTED_DELIVERABLES',methodRows=14,allAssessments='SUPPORTED',archiveChecks=len(archiveChecks),modelAsReported=scope['model']['name'],repositoryClean=True,testsRun=0)))
