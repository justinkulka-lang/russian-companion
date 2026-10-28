from pathlib import Path
import hashlib,json,subprocess,shutil,zipfile
R=Path('/workspace/russian-companion');W=Path(__file__).parent
D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit14-plural-possession-baseline-2026-10-28'
Q=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit14-substantive-research-2026-10-28'
BASE='9101af8c182b3415426ed9226cb211a707a2c393'
git=lambda *a:subprocess.check_output(['git',*a],cwd=R)
sha=lambda b:hashlib.sha256(b).hexdigest()
assert git('rev-parse','HEAD').decode().strip()==BASE
D.mkdir()
test='app/src/test/java/dev/russiancompanion/v2/Unit14PluralPossessionReviewBaselineTest.kt'
paths=git('ls-tree','-r','--name-only',BASE,'app/src/test').decode().splitlines();assert len(paths)==276
oldtests={p:sha(git('show',BASE+':'+p)) for p in paths}
protected=json.loads((Q/'PROTECTED_EVIDENCE_HASHES.json').read_text())
for p,h in {**protected,**oldtests}.items():assert sha((R/p).read_bytes())==h,p
history=json.loads((Q/'HISTORICAL_UNIT14_UNIT15_INVENTORY.json').read_text())
assert len(history['archives'])==74
for a in history['archives']:
    with zipfile.ZipFile(R/a['path']) as z:assert z.testzip() is None
assert (R/test).read_text().count('@Test fun ')==2
trees={p:git('rev-parse',BASE+':'+p).decode().strip() for p in ['app/src/main','app/src/test','catalog','docs','review','release']}
old=Path('/workspace/artifacts/unit13-present-baseline-2026-10-27')
for name in ['run.py','resources.gradle','launch.py','supervise.py']:
    s=(old/name).read_text().replace('audit.unit13.capture.dir','audit.unit14.capture.dir').replace('Unit13PresentReviewBaselineTest','Unit14PluralPossessionReviewBaselineTest')
    s=s.replace('Two Unit13 methods: current v2/schema6 positive with actual Unit11v2/schema13 and Unit12v2/schema14 prerequisites, partial backup/read/replace/named reopen/completion/advance14; separate intentionally failing future present4/aspect2/past2 contrast, not a current gate bypass or complete suite','Two Unit14 methods: actual-current v1/schema1 positive through Unit15/owner14v1 and actual advance16, three genuine backup captures/read/replace/named reopen, current Unit11v2/12v2/13v3 prerequisites; separate intentionally failing proposed plural-possession due-answer contrast, not current gate bypass or complete suite')
    (W/name).write_text(s);(D/name).write_text(s)
shutil.copy2('/workspace/setup/cloud-validation.gradle',D/'cloud-validation.gradle')
shutil.copy2(R/'audit/independent-gpt61-2026-10-12/broader-regression-2026-10-18/broader-tests.gradle',D/'event-listener.gradle')
shutil.copy2(__file__,D/'prepare.py')
save=lambda n,x:(D/n).write_text(json.dumps(x,indent=2)+'\n')
save('PROTECTED_EVIDENCE_HASHES.json',protected);save('EXISTING_TEST_HASHES.json',oldtests)
save('PRIOR_ARCHIVE_INVENTORY.json',history['archives'])
with zipfile.ZipFile(D/'ORIGINAL_BASELINE_TEST.zip','w',zipfile.ZIP_DEFLATED) as z:z.write(R/test,test)
save('PREPARATION.json',dict(baselineSourceCommit=BASE,inputTrees=trees,newTestFile=test,newTestSha256=sha((R/test).read_bytes()),existingTestFiles=276,newMethodsInventory=2,newClassesInventory=1,productionChanged=False,newPolicyActivated=False,registeredObjectsPreserved=40,schemasPreserved=list(range(1,16)),sourceBindingsConserved=155,priorProtectedArchivesConserved=74,protectedFiles=len(protected),positiveScope='actual current14v1/schema1; current11v2/schema13+12v2/schema14+13v3/schema15; actual advance14to15, genuine15v1/owner14v1 partial and actual advance15to16',captureNames=['PARTIAL_UNIT14_V1.zip','COMPLETED_UNIT14_V1.zip','PARTIAL_UNIT15_OWNER14_V1.zip'],futureContrast='expected diagnostic failure before proposed Unit14v2/schema16; not a current gate bypass',exclusions={'VisualReviewCaptureTest':2,'Units35ChunkCaptureTest':2,'Units610ChunkCaptureTest':10,'Units1115GapCaptureTest':2},runtimeDirective='SQLite prepare JIT compilation directive PROVISIONAL_UNADOPTED; not test exclusion; native cause UNCONFIRMED',apkIdentity=None,apkSignature=None,apkAssemblyInstallSigning='NOT_RUN',all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',completeSuite=False,scopesNotSummed=True,privatePdf='NOT_READ_OR_PACKAGED',environmentSettings='UNCHANGED; publication user-confirmed independently unobserved'))
(D/'STATUS.md').write_text('''# Actual-current Unit14 and downstream Unit15 baseline prepared

Two new JUnit methods in one separate class are inventory only. Production,276existing test files,40policy objects,schemas1–15,155bindings and74protected archives are unchanged. Proposed Unit14v2/schema16 remains NOT_ACTIVATED. Original whole baseline is archived before execution.

Positive control pins the entire actual-current production pack at9101af8c182b3415426ed9226cb211a707a2c393 and checks full equality while v1 is active. It starts at fresh Unit1 and actually advances through current Units1–13, including11v2/schema13,12v2/schema14,13v3/schema15. It completes current14teaching/three guided/sixCHECK/book, opens14v1/schema1, captures partial after slot0, checks supported read/replace/named reopen, finishes/advances15/captures completed14, completes ordinary15 and captures15v1/schema1 partial with actual owner14v1 identity before any14core change. It reopens/completes/actually advances16 and reopens again. Synthetic correct answers/book attestations prove host lifecycle only.

The second current-production method contrasts proposed plural/possession due answers with legitimate v1 advancement. Expected failure is diagnostic and must be retained; not current-gate bypass. No test expectations or production may be changed to force green. Only after verified positive captures may the separate compatible14v2/schema16 core begin.

One agent/existing checkout/DraftPR64. External workspace /workspace/artifacts/unit14-plural-possession-baseline-2026-10-28 owns launch/raw capture/collection. Before launch/edit inspect DISCOVERY/REGRESSION CONTEXT/RESULT, launcher/runner/completion metadata and live processes. Never duplicate a run or edit active inputs. Discovery skips are not runtime passes. Latest completed broad cd2b1c91:1477reported/1476pass/1physicalskip/257classes,exit0 remains separate/unsummed.

All48findingsOPEN/auditINCOMPLETE. Four screenshot exporters/16methods explicitly excluded in PREPARATION.json/cloud-validation.gradle. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance LAST after final independent comprehensive nonphysical audit. No APKassembly/install/signing/merge/freeze/formal advancement/releaseapproval;APKidentity/signaturenull. PrivatePDF unread/unpackaged;settings unchanged/publication user-confirmed independently unobserved;SQLiteJITdirective provisional/unadopted/nativecauseUNCONFIRMED. Qualifiedreviews/CC024risk/CC048authority/oldUnit7restorecause unresolved.
''')
(D/'NEXT_TASK.md').write_text('''# Verify baseline before implementing Unit14v2

Inspect external processes/contexts/completion and raw outcomes before edits. Collect actual fresh discovery/runtime XML/events/commands/exit/task/cache/lint/gate evidence. Verify the positive lifecycle before concluding it passed;the future contrast is expected to fail under valid14v1. Archive the complete original baseline and raw failure unchanged.

Verify exact actual producer commit;each captured ZIP bytes/SHA/memberSHA/CRC/manifest;backup format/document schema/assignment schema/answer-record schema separately. Check actual14partial slot0/no14completion,14completed/profile15,15partial slot0/no15completion/owner14v1;all slots/digests/orders/history/profile/recordings/prerequisite state. Confirm supported read/replace/named reopen/real advances14→15→16 and completed15 reopen. Preserve74prior archives plus actual new learner backups in a named inventory;do not include raw-result packaging as learner captures or assume counts.

Only after verified baseline, next large task is separate compatible Unit14v2/schema16 implementation from ../engineering-unit14-substantive-research-2026-10-28/NEXT_TASK.md. Preserve40old objects/schemas1–15/whole baseline/276older tests/all evidence/155bindings. New3due answers from6plural+4possession+1reported candidates;24variants are not24JUnit methods. Add exact current-content old14v1 owner support for genuine15 task,retain13v1/v2 bridges/current13v3 and stale content refusals. Later strict before14 whole-pack proof/authentic bindings/affected/broad required. All48OPEN/auditINCOMPLETE/physicalLAST/no release actions.
''')
rel=D.relative_to(R).as_posix()
for name in ['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']:
    f=R/name;link=rel+'/STATUS.md' if name=='AGENTS.md' else '../'+rel.removeprefix('audit/')+'/STATUS.md'
    f.write_text(f'Current checkpoint: [Actual-current Unit14/Unit15 baseline prepared]({link}). Two new methods/oneclass inventory only;production/276oldertests/40objects/schemas1–15/155bindings/74archives conserved;proposed14v2/schema16 NOT_ACTIVATED. Inspect external unit14-plural-possession-baseline-2026-10-28 before launch/edit. Genuine14v1partial/completed and15owner14v1partial planned;future contrast not current-gate bypass. All48OPEN/auditINCOMPLETE/four exporters16methods excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'+f.read_text())
print(json.dumps(dict(prepared=True,existingTestsConserved=276,protectedFilesConserved=len(protected),priorArchivesShaCrc=74,newMethodsInventory=2,productionChanged=False)))
