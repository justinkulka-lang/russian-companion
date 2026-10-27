"""Preserve actual fresh Unit13 evidence; never launch tests or change app/test inputs."""
from pathlib import Path
from datetime import datetime
from zoneinfo import ZoneInfo
import csv,hashlib,json,re,shutil,subprocess,xml.etree.ElementTree as ET,zipfile
R=Path('/workspace/russian-companion');W=Path(__file__).parent
D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit13-present-baseline-2026-10-27'
sha=lambda b:hashlib.sha256(b).hexdigest()
git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
def save(n,v):(D/n).write_text(json.dumps(v,indent=2)+'\n')
prep=json.loads((D/'PREPARATION.json').read_text());base=prep['baselineSourceCommit']
c=json.loads((W/'REGRESSION/CONTEXT.json').read_text());r=json.loads((W/'REGRESSION/RESULT.json').read_text());S=c['sourceCommit']
assert not git('status','--porcelain');assert git('rev-parse','HEAD')==S==r['sourceCommit']
assert not (D/'RAW_BASELINE.zip').exists()
for mode in ['DISCOVERY','REGRESSION']:
 done=json.loads((W/(mode+'_COMPLETION.json')).read_text());res=json.loads((W/mode/'RESULT.json').read_text())
 assert done['sourceCommit']==res['sourceCommit']==S
 assert done['runnerExit']==res['exitCode']==int(done['gradleExit'])
 assert done['resultExists'] and done['manifestExists']
 manifest=json.loads((W/mode/'SHA256.json').read_text())
 for p,h in manifest.items():assert sha((W/mode/p).read_bytes())==h,(mode,p)
 for n in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copy2(W/mode/n,D/(mode+'_'+n))
files={p.relative_to(W).as_posix():p for mode in ['DISCOVERY','REGRESSION'] for p in (W/mode).rglob('*') if p.is_file()}
for mode in ['DISCOVERY','REGRESSION']:
 for suffix in ['COMPLETION.json','LAUNCHER.json','RUNNER_PROCESS.json','LAUNCHER.exit','RUNNER.log','LAUNCHER.log']:
  p=W/(mode+'_'+suffix);assert p.is_file();files[p.name]=p
for n in ['run.py','resources.gradle','supervise.py','launch.py']:files['RUNNER/'+n]=W/n
for p,h in c['initScriptSha256'].items():
 p=Path(p);assert sha(p.read_bytes())==h;files['INIT/'+p.name]=p
index=[]
with zipfile.ZipFile(D/'RAW_BASELINE.zip','x',zipfile.ZIP_DEFLATED) as z:
 for n,p in sorted(files.items()):
  assert not n.lower().endswith('.pdf');z.write(p,n);index.append(dict(path=n,bytes=p.stat().st_size,sha256=sha(p.read_bytes())))
save('RAW_BASELINE_INDEX.json',index)
# Original raw outcomes are now saved even if positive-capture assertions fail below.
assert r['classes']==1 and r['testCounts']==dict(reported=2,passed=1,failed=1,errors=0,skipped=0),r['testCounts']
assert r['exitCode']==1 and r['selectedFreshXmlAndEventsMatch'] and r['eventOutcomeCountsMatchXml']
assert r['discoveryComparison']['sameInputTrees'] and r['discoveryComparison']['exactSameMethodSet']
positive='actualCurrentV2PartialTaskRoundtripsReopensAndCompletesItsOwnObligation'
negative='priorPrintAspectAndPastAnswersCannotReplaceFreshAssignedPresentAnswer'
by={t['method']:t for t in r['tests']};assert by[positive]['outcome']=='PASSED' and by[negative]['outcome']=='FAILED'
assert 'The proposed Unit13 obligation must refuse advancement without its fresh assigned present-form answer' in by[negative]['messagePreview']
xml=ET.parse(W/'REGRESSION/XML/TEST-dev.russiancompanion.v2.Unit13PresentReviewBaselineTest.xml').getroot()
assert re.search('actualAdvanceWithoutPresent=true',xml.findtext('system-out',''))
cap=W/'REGRESSION/CAPTURE';identity=json.loads((cap/'IDENTITY.json').read_text())
assert identity['producerCommit']==S and identity['curriculumSourceCommit']==base
assert identity['policyId']=='unit13-prior-reading-aspect-past-gender-v2' and identity['assignmentSchema']==6
for original,new in [('PARTIAL_UNIT13_V2.zip','BASELINE_CURRENT_UNIT13_V2.zip'),('IDENTITY.json','CAPTURE_IDENTITY.json'),('COMPLETED_UNIT13_V2.json','COMPLETED_UNIT13_V2.json')]:shutil.copy2(cap/original,D/new)
archive=D/'BASELINE_CURRENT_UNIT13_V2.zip';assert identity['zipSHA256']==sha(archive.read_bytes()) and identity['zipBytes']==archive.stat().st_size
with zipfile.ZipFile(archive) as z:
 assert z.testzip() is None and set(z.namelist())=={'manifest.json','learner.json'}
 members=[dict(path=i.filename,bytes=i.file_size,crc32=f'{i.CRC:08x}',sha256=sha(z.read(i.filename))) for i in z.infolist()]
 manifest=json.loads(z.read('manifest.json'));document=json.loads(z.read('learner.json'))
 assert document==json.loads((cap/'PARTIAL_UNIT13_V2.json').read_text())
 for f in manifest['files']:
  assert f['sha256']==sha(z.read(f['path'])) and f['bytes']==len(z.read(f['path']))
assert manifest['formatVersion']==3 and document['schemaVersion']==identity['documentSchemaVersion']
a=next(a for a in document['reviewAssignments'] if a['targetUnit']==13)
answers=[x for x in document['reviewAnswers'] if x['assignmentId']==a['id']]
assert a['schema']==6 and a['policyId']==identity['policyId'] and a['digest']==identity['assignmentDigest']
assert a['policyDigest']==identity['policyDigest'] and a['policyDigest']=='7e5076dfb0def9c62d25787335555bd93136b98b2bf1c0d823a58cc1830a1a26'
assert [s['ownerUnit'] for s in a['slots']]==[1,12,11] and [s['objectiveId'] for s in a['slots']]==['obj-u1-decoding','obj-u12-aspect','obj-u11-past-aspect']
assert a['slots'][1]['unitDigest']=='8c3ae9bbd682e069dbcd4dc579c30af36bbce321120ce7da804260e5a44d7033'
assert a['slots'][2]['unitDigest']=='1d4c25c25e20b7fb6629995fcfb66e29f02554e276c5a7f5ec69fb5ac0a2ca5c'
assert len(answers)==1 and answers[0]['slotIndex']==0 and answers[0]['acceptedOrder']>a['boundary']
assert answers[0]['assignmentDigest']==a['digest'] and answers[0]['schema']==1
assert document['recordings']==[] and identity['recordingCount']==manifest['recordingCount']==0
assert set(x['unitNumber'] for x in document['unitCompletions'])==set(range(1,13)) and document['profile']['currentUnitNumber']==13
prereqs={x['targetUnit']:x for x in document['reviewAssignments']}
assert prereqs[11]['schema']==13 and prereqs[12]['schema']==14
completed=json.loads((D/'COMPLETED_UNIT13_V2.json').read_text())
assert completed['profile']['currentUnitNumber']==14 and set(x['unitNumber'] for x in completed['unitCompletions'])==set(range(1,14))
assert next(x for x in completed['reviewAssignments'] if x['targetUnit']==13)==a
completedAnswers=[x for x in completed['reviewAnswers'] if x['assignmentId']==a['id']]
assert {x['slotIndex'] for x in completedAnswers}=={0,1,2} and len(completedAnswers)==3
assert answers[0] in completedAnswers
row=next(x for x in completed['unitCompletions'] if x['unitNumber']==13)
assert row['reviewAssignmentId']==a['id'] and row['reviewPolicyDigest']==a['policyDigest'] and row['reviewCompletionOrder']>a['boundary']
assert row['reviewCompletionOrder']==max(x['acceptedOrder'] for x in completedAnswers)
assert sha((R/prep['newTestFile']).read_bytes())==prep['newTestSha256']
if (D/'ORIGINAL_BASELINE_TEST.zip').exists():
 with zipfile.ZipFile(D/'ORIGINAL_BASELINE_TEST.zip') as z:assert sha(z.read(prep['newTestFile']))==prep['newTestSha256']
else:
 with zipfile.ZipFile(D/'ORIGINAL_BASELINE_TEST.zip','x',zipfile.ZIP_DEFLATED) as z:z.write(R/prep['newTestFile'],prep['newTestFile'])
protected=json.loads((D/'PROTECTED_EVIDENCE_HASHES.json').read_text())
for p,h in protected.items():assert sha((R/p).read_bytes())==h,p
oldTests=git('ls-tree','-r','--name-only',base,'app/src/test').splitlines();assert len(oldTests)==271
for p in oldTests:assert (R/p).read_bytes()==subprocess.check_output(['git','show',f'{base}:{p}'],cwd=R),p
assert git('diff','--name-only',base,S,'--','app/src/test').splitlines()==[prep['newTestFile']]
for p,h in prep['inputTrees'].items():
 if p!='app/src/test':assert c['inputTrees'][p]==h==git('rev-parse',S+':'+p),p
history=json.loads((R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit13-substantive-research-2026-10-27/HISTORICAL_UNIT13_UNIT14_INVENTORY.json').read_text())
archiveHashes={x['path']:x['sha256'] for x in history['archives']};assert len(archiveHashes)==73
assert all(protected[p]==h for p,h in archiveHashes.items())
save('PRIOR_ARCHIVE_HASHES.json',archiveHashes)
archives=list(archiveHashes)
for p in archives:
 with zipfile.ZipFile(R/p) as z:assert z.testzip() is None,p
assert not [p for p in git('ls-tree','-r','--name-only',S).splitlines() if p.lower().endswith('.pdf')]
save('CAPTURE_VERIFICATION.json',dict(producerCommit=S,curriculumSourceCommit=base,fullCurrentCurriculumEqualsBaseline=True,zipBytes=archive.stat().st_size,zipSha256=sha(archive.read_bytes()),zipCrcPassed=True,members=members,backupFormatVersion=manifest['formatVersion'],documentSchemaVersion=document['schemaVersion'],answerRecordSchema=1,recordingCount=0,assignment=a,partialAnswers=answers,completedAnswers=completedAnswers,completion=row,prerequisitePolicies={str(n):dict(policyId=prereqs[n]['policyId'],schema=prereqs[n]['schema']) for n in [11,12]},partialUnitCompletionNumbers=sorted(x['unitNumber'] for x in document['unitCompletions']),completedUnitCompletionNumbers=sorted(x['unitNumber'] for x in completed['unitCompletions']),completedProfileUnit=14,positiveBackupReadReplaceNamedReopenActualAdvancePassed=True,syntheticLearner=True,oldArchiveRelabelled=False))
save('CONSERVATION.json',dict(sourceCommit=S,baselineSourceCommit=base,inputTrees=c['inputTrees'],protectedFileCount=len(protected),priorArchivesShaCrc=73,additionalProtectedDiagnosticZipFiles=12,currentBindingsUnchanged=155,preexistingTestFilesUnchanged=271,newTestFiles=1,newMethodInventory=2,oldObjectsConserved=39,oldSchemasConserved=list(range(1,15)),productionCatalogDocsReviewReleaseUnchanged=True,proposedV3Schema15='NOT_ACTIVATED',canonicalSourceDigest='84b6fda72017ec2dfc2b4add6c0e82b1a1e9f40769773f18d50caee4cf7a2355',digestBasis='Existing official compiled digest; production identical, no new calculation/render/regeneration',apkIdentity=None,apkSignature=None,apkAssemblyInstallSigning='NOT_RUN',environmentSettingsChanged=False,publication='USER_CONFIRMED_NOT_INDEPENDENTLY_OBSERVED',all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST'))
save('BASELINE_OBSERVATION.json',dict(sourceCommit=S,testCounts=r['testCounts'],actualAdvanceWithoutFreshAssignedPresentAnswer=True,currentDeclaredGateBypassed=False,ordinaryKnowledgeBypassed=False,positivePassed=True,proposedV3Activated=False,fullFailureSha256=by[negative]['fullMessageSha256'],scope='Two selected methods only; no complete-suite or independent acceptance claim',priorBroad=dict(source='44cb3e669d287d2f9848113f1722ab1224af9c37',reported=1458,passed=1457,failed=0,skipped=1,classes=253,exitCode=0),overlappingScopesNotSummed=True))
shutil.copy2(Path(__file__),D/'persist_baseline.py')
start=datetime.fromisoformat(c['startedUtc']).astimezone(ZoneInfo('America/Chicago'));end=datetime.fromisoformat(r['finishedUtc']).astimezone(ZoneInfo('America/Chicago'))
report=f'''# Actual-current Unit13 v2 baseline and genuine partial backup\n\nActual producer `{S}`. Two selected methods in one full class: **2 reported / 1 passed / 1 intentionally failed / 0 errors / 0 skipped**, Gradle/runner exit1. Fresh discovery had2 synthetic skips/zero runtime passes; exact XML/events/discovered method sets and input trees match. Actual run {start.isoformat()} → {end.isoformat()} (America/Chicago); raw UTC retained. This is not a complete suite or independent acceptance.\n\nThe positive v2/schema6 backup/read/replace/named reopen/completion/actual advance14 control PASSED. Full production curriculum matches pinned `{base}`; actual Unit11v2/schema13 and Unit12v2/schema14 prerequisites were established via current store APIs, followed by all Unit13 instruction, four guided items, nine CHECK items and book self-attestation. These are synthetic correct answers, not learner ability/book/source acceptance.\n\nNew capture `BASELINE_CURRENT_UNIT13_V2.zip`: {archive.stat().st_size}bytes, SHA256 `{sha(archive.read_bytes())}`, all ZIP CRC/member hashes verified. Backup format3/document schema3, assignment schema6 and answer-record schema1 are separate identities. Partial target13 task has three slots (Unit1 print, Unit12 aspect1, Unit11 past gender); only slot0 answered, no Unit13 completion, zero recordings observed. Identity/digests/answers/accepted orders and completed snapshot are saved in CAPTURE_VERIFICATION.json. Original named store was reopened to finish slots1/2, commit completion and advance14, then reopened again; decoded backup was replaced into a separate in-memory store and reopened through the assignment API. No physical restore/reboot is claimed.\n\nThe future-requirement contrast FAILED at its explicit assertion: valid current v2 advanced after its three declared answers without a fresh assigned present-form answer. This is expected diagnostic evidence for proposed v3, not a bypass of current declared review or ordinary knowledge. The earlier Unit10 present answer/optional practice do not supply new assignment-bound credit. Original full failure/XML/events/logs and entire baseline class are preserved, not waived or rewritten. Proposed v3/schema15 remains NOT ACTIVATED; no app/policy/source/test-assertion corrections applied.\n\nMain/test compilation, lint and gate task status is recorded exactly in REGRESSION_RESULT.json: `{r['tasks']}`. Lint severity counts `{r['lintIssuesBySeverity']}`. Gate attachment is not release approval. No APK assembled/installed/signed; no APK identity/signature exists for this phase. No production rendering or source-binding regeneration.\n\nAll271 pre-existing test files,39 policies/schemas1–14,155 source bindings and73 prior archives remain exact; archives passed SHA+CRC. One new class/two methods only. Prior broad at44cb3e66:1458 reported/1457 passed/1 physical skip,253 classes,exit0 remains separate/unsummed. Four exporters/16 methods explicitly excluded: VisualReviewCaptureTest2, Units35ChunkCaptureTest2, Units610ChunkCaptureTest10, Units1115GapCaptureTest2. This two-method scope does not execute the physical test.\n\nAll48 findings OPEN; independent audit INCOMPLETE. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance remains LAST after final independent comprehensive nonphysical audit. Qualified reviews, CC048 authority conflict and original Unit7 restore cause remain unresolved. No merge/freeze/formal advancement/release approval. Private PDF unread/unpackaged; environment settings unchanged/publication user-confirmed independently unobserved. Provisional SQLite JIT compilation directive remains unadopted; it excludes compilation, not test methods; native cause UNCONFIRMED.\n\nEvidence-checker correction: initial collector incorrectly equated answer-record schema with assignment schema6. Actual answer schema1 is required by CUMULATIVE_REVIEW_SCHEMA; original checker/error are archived in COLLECTOR_PRECHECK_1.zip. A second collector scope assumption counted all85 protected ZIPs as the73 declared historical archives; COLLECTOR_PRECHECK_2.zip preserves the error. The checker now uses the explicit73-entry archive inventory and retains hashes for12 additional diagnostic ZIPs. Raw app outcomes/capture unchanged; no rerun.

Next large task: versioned Unit13v3/schema15 implementation from NEXT_TASK.md and the research packet; preserve this whole baseline, raw failure and genuine v2 capture.\n'''
(D/'COMPLETED_STATUS.md').write_text(report)
print(json.dumps(dict(source=S,counts=r['testCounts'],positive='PASSED',contrast='EXPECTED_FAILURE',captureSha256=sha(archive.read_bytes()),captureBytes=archive.stat().st_size,priorArchives=73,priorTests=271)))
