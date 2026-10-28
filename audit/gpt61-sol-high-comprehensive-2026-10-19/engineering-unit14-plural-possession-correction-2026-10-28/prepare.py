from pathlib import Path
import json,hashlib,subprocess,shutil,re,zipfile
R=Path('/workspace/russian-companion');W=Path(__file__).parent
D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit14-plural-possession-correction-2026-10-28'
B=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit14-plural-possession-baseline-2026-10-28'
BASE='f670c24e04def2829a7949e83b91b83a0dedf338'
git=lambda *a:subprocess.check_output(['git',*a],cwd=R)
sha=lambda b:hashlib.sha256(b).hexdigest()
assert git('rev-parse','HEAD').decode().strip()==BASE;D.mkdir()
changedMain=['app/src/main/assets/content/v2-curriculum.json','app/src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt','app/src/main/java/dev/russiancompanion/v2/ui/V2CumulativeReviewScreen.kt']
paths=git('ls-tree','-r','--name-only',BASE,'app/src/test').decode().splitlines();assert len(paths)==277
oldTests={p:sha(git('show',BASE+':'+p)) for p in paths}
protected=json.loads((B/'CURRENT_PROTECTED_EVIDENCE_HASHES.json').read_text());archives=json.loads((B/'PROTECTED_ARCHIVE_INVENTORY.json').read_text())
assert len(archives)==77
for p,h in {**protected,**oldTests}.items():
    if p not in changedMain:assert sha((R/p).read_bytes())==h,p
for a in archives:
    assert sha((R/a['path']).read_bytes())==a['sha256']
    with zipfile.ZipFile(R/a['path']) as z:assert z.testzip() is None
kernel=changedMain[1];old=git('show',BASE+':'+kernel).decode();now=(R/kernel).read_text()
objects=lambda t:dict(re.findall(r'(?m)^internal object (\w+) \{\n(.*?)^\}',t,re.S))
o=objects(old);n=objects(now);assert len(o)==40 and len(n)==41 and all(n[k]==v for k,v in o.items())
constants=lambda t:dict(re.findall(r'^internal const val (\w+) = (\d+)$',t,re.M))
co=constants(old);cn=constants(now);assert all(cn[k]==v for k,v in co.items())
oldAsset=git('show',BASE+':'+changedMain[0]);newAsset=(R/changedMain[0]).read_bytes()
oldid=b'unit14-prior-reading-reported-time-v1';newid=b'unit14-prior-plural-possession-reported-time-v2'
assert oldAsset.count(oldid)==newAsset.count(newid)==1 and newAsset.replace(newid,oldid)==oldAsset
newTests=['app/src/test/java/dev/russiancompanion/v2/'+n+'.kt' for n in ['Unit14PluralPossessionFixtures','Unit14PluralPossessionCompatibilityTest','Unit14PluralPossessionReviewUiTest']]
assert [(R/p).read_text().count('@Test fun ') for p in newTests]==[0,10,8]
for name in ['run.py','resources.gradle','launch.py','supervise.py']:
    s=(Path('/workspace/artifacts/unit13-present-correction-2026-10-27')/name).read_text()
    for old,new in [('audit.unit13.observation','audit.unit14.observation'),('Unit13PresentReviewBaselineTest','Unit14PluralPossessionReviewBaselineTest'),('Unit13PresentCompatibilityTest','Unit14PluralPossessionCompatibilityTest'),('Unit13PresentReviewUiTest','Unit14PluralPossessionReviewUiTest'),('len(names)==17','len(names)==20')]:s=s.replace(old,new)
    s=s.replace('17 selected methods/3 full classes: Unit13v3/schema15, whole fixed positive baseline, savedv1/v2, current owner13 Unit14 bridges, eight candidate variants and three actual UI repairs; not complete suite','20 selected methods/3 full classes: Unit14v2/schema16, whole fixed baseline, genuine old14 and downstream15/owner14v1, retained owner13v1/v2 bridges,24allocation variants and three actual repairs;not complete suite')
    (W/name).write_text(s);(D/name).write_text(s)
for src,name in [('/workspace/setup/cloud-validation.gradle','cloud-validation.gradle'),(str(R/'audit/independent-gpt61-2026-10-12/broader-regression-2026-10-18/broader-tests.gradle'),'event-listener.gradle')]:shutil.copy2(src,D/name)
shutil.copy2(__file__,D/'prepare.py')
save=lambda n,v:(D/n).write_text(json.dumps(v,indent=2)+'\n')
save('EXISTING_TEST_HASHES.json',oldTests);save('PROTECTED_EVIDENCE_HASHES.json',protected);save('PROTECTED_ARCHIVE_INVENTORY.json',archives)
save('OLD_OBJECT_AND_SCHEMA_PROOF.json',dict(baselineSourceCommit=BASE,oldObjects={k:sha(v.encode()) for k,v in o.items()},oldObjectsBytePreserved=True,newObject='Unit14PluralPossessionCumulativeReviewPolicy',oldConstants=co,oldConstantsPreserved=True,assetOnlyExactDeclarationChange=True,oldAssetSha256=sha(oldAsset),newAssetSha256=sha(newAsset)))
save('PREPARATION.json',dict(baselineSourceCommit=BASE,inputTrees={p:git('rev-parse',BASE+':'+p).decode().strip() for p in ['app/src/main','app/src/test','catalog','docs','review','release']},changedMainFiles={p:dict(before=sha(git('show',BASE+':'+p)),after=sha((R/p).read_bytes())) for p in changedMain},newTestFiles={p:sha((R/p).read_bytes()) for p in newTests},preexistingTestFilesConserved=277,currentTestFileInventory=280,newJUnitMethodsInventory=18,selectedMethodsInventory=20,selectedClassesInventory=3,wholeBaselineUnchanged=True,oldPolicyObjectsBytePreserved=40,oldAssignmentSchemasPreserved=list(range(1,16)),newPolicyId=newid.decode(),newAssignmentSchema=16,dueAnswers=3,candidateItems=11,allocationVariants=24,variantCountIsNotJUnitCount=True,protectedArchivesConserved=77,sourceBindingRecords=155,sourceBindings='STALE_UNCHANGED_PENDING_AUTHENTIC_COMPILED_RECONCILIATION',repositorySourceDigest='PENDING_ACTUAL_COMPILED_OBSERVATION',packContentReviewDigest='PENDING_ACTUAL_COMPILED_OBSERVATION',exclusions={'VisualReviewCaptureTest':2,'Units35ChunkCaptureTest':2,'Units610ChunkCaptureTest':10,'Units1115GapCaptureTest':2},apkIdentity=None,apkSignature=None,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',completeSuite=False,scopesNotSummed=True))
(D/'STATUS.md').write_text('''# Unit14v2/schema16 core prepared; runtime pending

Separate supplied plural/possession/reported-time policy,three due answers/eleven existing candidates/owners8,10,13. Twenty selected JUnit methods/three classes are INVENTORY,not runtime results. Eighteen methods added in two new classes plus one fixture;all277older test files including the entire two-method baseline remain byte-exact. All40old policy object bodies/constants/schemas1–15 remain exact. Raw curriculum changes only one Unit14policy ID;oldv1title/policy/assignment registry preserved. New schema16 refused under old declaration;exact old14declaration/currentcontent reconstruction supports saved15owner14v1,not arbitrary stale teaching hashes. Current13v3 and old13v1/v2 bridges retained.

Protected77archives/old learner captures/raw failures/source evidence conserved.155sourcebindings STALE_UNCHANGED pending authentic compiled reconciliation;no renderer/resnapshot now. Predicted policy metrics28/60/121/57/18 require actual compiled observation. Repository source digest and pack content digest are separate and pending observation.

One agent/existing checkout/DraftPR64. External /workspace/artifacts/unit14-plural-possession-correction-2026-10-28 owns DISCOVERY then REGRESSION launch/raw evidence. Inspect contexts/launchers/runner/process/completion before launching or editing;never duplicate jobs/edit active inputs. Capture every raw result and preserve failures before correction. Later authenticbindings/affectedFULLclasses/broad still required. Prior two-method baseline1positive/1intendedfailure atf2628b36 and prior1477broad remain separate/unsummed.

All48findingsOPEN/auditINCOMPLETE. Four exporters16methods excluded explicitly. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance LAST after final independent comprehensive nonphysical audit. NoAPKassembly/install/newsigning/merge/freeze/formal advancement/releaseapproval;APKidentity/signaturenull. PrivatePDFunread/unpackaged;settingsunchanged/publication user-confirmed independently unobserved;SQLiteJITdirectiveprovisional/unadopted/not test exclusion/nativecauseUNCONFIRMED. Qualifiedreviews/CC024risk/CC048authority/originalUnit7restorecause unresolved.
''')
(D/'NEXT_TASK.md').write_text('''# Verify this versioned core before source reconciliation

Inspect external contexts/processes/completions before anything. Collect exact fresh discovery/runtime XML/events/commands/exits/task/cache/lint/gate/source trees. Inventory20selectedmethods/3fullclasses must be proved by actual discovery and runtime,not assumed. Preserve all failures and exact raw ZIPs before any correction. All277prior test files/whole baseline/all40oldobjects/schemas1–15/77protected archives/155stale unchanged bindings conserved. Compiled observation must distinguish repository source digest from packcontent digest and prove current28/60/121/57/18metrics.

Only after core checks:next large task strict before14 whole-pack conservation against9101af8c baseline,retaining14→13→12→11→10→9→8→7→6→5chain. Authenticate actual matching historical kernel/reference pair before changing kernel/reference;do not guess a postcore point. Then two real compiled renders/all155bindings/prior versions and exact ownsource choices. Current bindings remainSTALE_UNCHANGED until that real reconciliation. Follow with affectedFULLclasses,then fresh broad selectedhost verification. Preserve all historical assertions/pins/currentguards/rawfailures;no widening allowlists or resnapshot to make tests pass. Actual device evidence after final independent comprehensive nonphysical audit staysLAST;all48OPEN/auditINCOMPLETE/no release actions.
''')
rel=D.relative_to(R).as_posix()
for name in ['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']:
    p=R/name;link=rel+'/STATUS.md' if name=='AGENTS.md' else '../'+rel.removeprefix('audit/')+'/STATUS.md'
    p.write_text(f'Current checkpoint: [Unit14v2/schema16 core prepared;verification pending]({link}).20method/3class inventory only;277oldertests/wholebaseline/40oldobjects/schemas1–15/77archives conserved.155bindingsSTALE_UNCHANGED/digestsPENDING. Inspect external unit14-plural-possession-correction-2026-10-28 before launch/edit;do not duplicate jobs. All48OPEN/auditINCOMPLETE/four exporters16methods excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
print(json.dumps(dict(prepared=True,oldObjectsPreserved=40,oldTestsPreserved=277,archivesShaCrc=77,newJUnitInventory=18,selectedMethodInventory=20,bindings='STALE_UNCHANGED')))
