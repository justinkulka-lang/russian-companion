from pathlib import Path
import hashlib,json,subprocess,shutil,zipfile
R=Path('/workspace/russian-companion');W=Path(__file__).parent;D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit14-affected-integration-2026-10-28'
BASE='d597db3c738069b3109845e668b99f89a9251601';git=lambda *a:subprocess.check_output(['git',*a],cwd=R);sha=lambda b:hashlib.sha256(b).hexdigest()
assert git('rev-parse','HEAD').decode().strip()==BASE and not git('status','--porcelain=v1','-z');D.mkdir()
save=lambda p,v:p.write_text(json.dumps(v,indent=2)+'\n')
prior=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit14-source-reconciliation-2026-10-28'
inherited=json.loads((prior/'NEXT_AFFECTED_SCOPE.json').read_text());selectors=inherited['selectors'];assert len(selectors)==87
files=sorted((R/'app/src/test/java/dev/russiancompanion/v2').glob('Unit14*Test.kt'))+sorted((R/'app/src/test/java/dev/russiancompanion/v2').glob('Unit15*Test.kt'))
added=['dev.russiancompanion.v2.'+p.stem for p in files if 'dev.russiancompanion.v2.'+p.stem not in selectors];assert len(added)==9
selectors=selectors+added;assert len(selectors)==len(set(selectors))==96
trees={p:git('rev-parse',BASE+':'+p).decode().strip() for p in ['app/src/main','app/src/test','catalog','docs','review','release']}
tests={p:sha((R/p).read_bytes()) for p in git('ls-tree','-r','--name-only',BASE,'app/src/test').decode().splitlines()};assert len(tests)==281
bindings=json.loads((prior/'APPLIED_RECORDS.json').read_text())['currentRecordHashes'];assert len(bindings)==155
archives=json.loads((prior/'CONSERVATION.json').read_text())['archives'];assert len(archives)==77
for p,h in {**tests,**bindings,**archives}.items():assert sha((R/p).read_bytes())==h,p
for p in archives:
    with zipfile.ZipFile(R/p) as z:assert z.testzip() is None,p
scope=dict(kind='FULL_CLASS_AFFECTED_SCOPE_REQUIRES_DISCOVERY',inheritedClasses=87,addedForCoverage=added,classes=96,selectors=selectors,inputTrees=trees,methodCount='NOT_MEASURED',expectedMethods=[],testEditsBeforeRun=False)
save(W/'SCOPE.json',scope);save(D/'SCOPE.json',scope);save(D/'INHERITED_SCOPE.json',inherited)
prep=dict(parentCommit=BASE,inputTrees=trees,productionTree=trees['app/src/main'],existingTestFiles=tests,currentRecordHashes=bindings,archives=archives,testFilesByteExact=281,bindingsByteExact=155,archivesShaCrc=77,policyObjects=41,assignmentSchemas=list(range(1,17)),testEdits=False,productionEdits=False,sourceBindingEdits=False,repositorySourceDigest='487d9118d7154dd59162dbb7024f1349bc53d5b7bf89e9d79022d97eaa1eeb9a',packContentReviewDigest='39c3563ba1b149b36977ae0fc2c0f920e282101e9e7870c06d141fe1a10e65ec',all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',apkIdentity=None,apkSignature=None,completeSuite=False)
save(W/'PREPARATION.json',prep);save(D/'PREPARATION.json',prep)
old=Path('/workspace/artifacts/unit13-broad-host-regression-2026-10-27')
for name in ['run.py','resources.gradle','launch.py','supervise.py','durable_monitor.py','start_runtime.py']:
    text=(old/name).read_text()
    if name=='run.py':
        text=text.replace('selectors=[]',"selectors=json.loads((W/'SCOPE.json').read_text())['selectors']")
        start=text.index("scope='Unit13 broad selected-host");end=text.index("',physicalAcceptance=",start)
        text=text[:start]+"scope='Unit14 affected full classes,96 selectors including nine formerly omitted Unit14/15 classes;method count from fresh discovery;all281tests unchanged;preserve every failure;not complete suite"+text[end:]
    if name=='start_runtime.py':text=text.replace("['python3',", "['python3','-B',")
    (W/name).write_text(text);(D/name).write_text(text)
shutil.copy2(__file__,D/'prepare.py')
(D/'STATUS.md').write_text('''# Unit14 affected integration prepared

96fullclasses are inventory only;method count requires fresh discovery. The inherited87selectors are preserved;9older Unit14/downstream15classes were missing and are now explicitly included in SCOPE.json. All281test-sourcefiles/41policyobjects/schemas1–16/155reconciledbindings/77archives and sixinputtrees remain unchanged. No pre-run test edits,expectation changes,resnapshots or policy/source corrections. Run unchanged fullclasses first and preserve every actual failure before reconciliation. Prior2binding/20core/1477broad remain separate/unsummed.

External /workspace/artifacts/unit14-affected-integration-2026-10-28 owns discovery/runtime/rawcollection/monitor/completion. Inspect contexts/launchers/runners/completions/monitor/AUTO_RESULT or ERROR before launch/edit;never duplicatejobs or change activeinputs. Fresh discovery first;exclusive completion owner must preserve actual complete or incomplete outcomes,commit audit/docs only,normalpush/update DraftPR64. Source/path guards required. Four exporters16methods remain excluded;provisionalSQLiteJITcompilationdirective unchanged/unadopted/not test exclusion/nativecauseUNCONFIRMED.

All48findingsOPEN/auditINCOMPLETE. Physical GalaxyS26Ultra airplane-mode/reboot acceptanceLAST after final independent comprehensive nonphysical audit. Qualifiedreviews/CC024risk/CC048authority/originalUnit7restorecause unresolved. No APKassembly/install/newsigning/merge/freeze/formaladvancement/releaseapproval;APKidentity/signaturenull. PrivatePDFunread/unpackaged/environmentunchanged/publication user-confirmed independently unobserved. Oneagent/existingcheckout/DraftPR64.
''')
(D/'NEXT_TASK.md').write_text('Inspect external contexts/processes/monitor/completion/AUTO_RESULT or ERROR before any action. Fresh96class discovery must measure exact method IDs;then identical-input runtime. No test/source edits during run. Preserve all rawfailures/exits/native reports;do not assume pass or stopcause. Verify XML/events/source/commands/cache/lint/gate/281testfiles/155bindings/77archives. After actual outcome,triage failures and narrowly justify historical scopes without changing original assertions/pins/currentcontrols;fresh broad follows resolved affected work. All48OPEN/auditINCOMPLETE/physicalLAST/no release actions.\n')
rel=D.relative_to(R).as_posix()
for name in ['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']:
    p=R/name;link=rel+'/STATUS.md' if name=='AGENTS.md' else '../'+rel.removeprefix('audit/')+'/STATUS.md'
    p.write_text(f'Current checkpoint: [Unit14affected integration prepared]({link}).96classinventory(87inherited+9missingolder14/downstream15);methodcountrequiresfreshdiscovery. No test edits;281testfiles/41objects/schemas1–16/155bindings/77archives/sixinputs conserved. Inspect external unit14-affected-integration-2026-10-28 before launch/edit. All48OPEN/auditINCOMPLETE/four exporters16methods excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
print(json.dumps(dict(prepared=True,classesInventory=96,inherited87Retained=True,addedNine=added,methodCount='NOT_MEASURED',testEdits=False,testsConserved=281,bindingsConserved=155,archivesShaCrc=77)))
