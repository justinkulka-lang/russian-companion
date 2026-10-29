from pathlib import Path
import json,hashlib,zipfile,subprocess,shutil
R=Path('/workspace/russian-companion');W=Path(__file__).parent;D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-cc020-review-scope-2026-10-22';read=lambda p:json.loads(p.read_text());sha=lambda b:hashlib.sha256(b).hexdigest();save=lambda n,v:(D/n).write_text(json.dumps(v,indent=2)+'\n');git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
assert not git('status','--porcelain');runtime=read(W/'REGRESSION/RESULT.json');assert runtime['exitCode']==0 and runtime['testCounts']==dict(reported=13,passed=13,failed=0,errors=0,skipped=0);D.mkdir()
for p in (W/'INVENTORY').iterdir():shutil.copyfile(p,D/p.name)
for n in ['ExportReviewPolicies.java','EXPORT_CONTEXT.json','SELECTED_SCOPE.json','inventory.py','analyze.py','run.py','resources.gradle','persist.py','verify_saved_results.py','publish_results.py']:shutil.copyfile(W/n,D/n)
for mode in ['DISCOVERY','REGRESSION']:
 O=W/mode;out=D/mode;out.mkdir()
 for n,h in read(O/'SHA256.json').items():assert sha((O/n).read_bytes())==h
 for n in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit']+(['LINT.xml'] if (O/'LINT.xml').exists() else []):shutil.copyfile(O/n,out/n)
 files={p.relative_to(O).as_posix():p for p in O.rglob('*') if p.is_file()};files['LAUNCHER/'+mode+'_LAUNCHER.log']=W/(mode+'_LAUNCHER.log')
 with zipfile.ZipFile(D/('RAW_'+mode+'.zip'),'w',zipfile.ZIP_DEFLATED) as z:
  for n,p in files.items():z.write(p,n)
 save('RAW_'+mode+'_INDEX.json',{n:dict(sha256=sha(p.read_bytes()),bytes=p.stat().st_size) for n,p in files.items()})
files={n:W/n for n in ['COMPILE.log','EXPORT.log','INVENTORY_BUILD.log']}
with zipfile.ZipFile(D/'RAW_DIAGNOSTIC.zip','w',zipfile.ZIP_DEFLATED) as z:
 for n,p in files.items():z.write(p,n)
save('RAW_DIAGNOSTIC_INDEX.json',{n:dict(sha256=sha(p.read_bytes()),bytes=p.stat().st_size) for n,p in files.items()})
prior=R/'audit/gpt61-sol-high-comprehensive-2026-10-19';preserved={}
for name in ['engineering-ui-source-bindings-2026-10-22','engineering-v2-insets-accessibility-2026-10-22','engineering-source-binding-reconciliation-2026-10-22','final-independent-recheck-results/2026-10-22-claude-code','post-recording-guard-host-regression-2026-10-22']:
 for file in (prior/name).rglob('*'):
  if file.is_file():preserved[file.relative_to(R).as_posix()]=sha(file.read_bytes())
save('PRESERVED_PRECEDING_SCOPES.json',preserved)
archives=read(prior/'engineering-ui-source-bindings-2026-10-22/CONSERVATION.json')['original66ArchiveHashes']
for n,h in archives.items():
 assert sha((R/n).read_bytes())==h
 with zipfile.ZipFile(R/n) as z:assert z.testzip() is None
save('CONSERVATION.json',dict(original66ArchiveHashes=archives,precedingScopeFilesVerified=len(preserved),inputTrees=runtime['inputTrees'],productionTestCatalogDocsReviewReleaseChanged=False,originalCC020AuditFileUnchanged=True,rawCurriculumSha256=read(D/'INVENTORY_FACTS.json')['rawCurriculumSha256'],privateReferenceRead=False,sourceBindingDigest='7ce2727f34fe75b051cf65d5d908a5b2253a66fc90c34aae9318c864f32230b5',physicalAcceptance='OUTSTANDING_LAST'))
selected=read(W/'SELECTED_SCOPE.json');outcomes={v['testClass']+'.'+v['method']:v['outcome'] for v in runtime['tests']};assert set(outcomes)=={v['selector'] for v in selected['methods']}
byScope={scope:{'reported':sum(v['fixtureScope']==scope for v in selected['methods']),'passed':sum(v['fixtureScope']==scope and outcomes[v['selector']]=='PASSED' for v in selected['methods'])} for scope in ['CURRENT_PACK','HISTORICAL_PRE_TEACHING_CORRECTION_PACK']}
save('FIXTURE_SCOPE_RECONCILIATION.json',dict(runtimeSource=runtime['sourceCommit'],historicalFixtureCommit='547457b63b317dea560e4bdc36083c1a4dce315e',countsByFixtureScope=byScope,selectedMethods=[dict(v,outcome=outcomes[v['selector']]) for v in selected['methods']],completeSuite=False,all28PoliciesExecuted=False))
save('CHECKPOINT.json',dict(status='CC020_SCOPE_INVESTIGATED_FINDING_OPEN',sourceCommit=runtime['sourceCommit'],inventory=read(D/'INVENTORY_FACTS.json'),runtime=runtime['testCounts'],classes=runtime['classes'],fixtureScopes=byScope,lint=runtime['lintIssuesBySeverity'],productionOrTestsChanged=False,policiesOrSchemasChanged=False,all48IndependentFindings='NOT_CLOSED',nextLargeTask='Research a bounded substantive multi-skill review repair using admitted earlier content; preserve exact legacy assignments/schemas and reproduce the selected gap before activation. Shared print-pair slot needs underlying CC002/CC027 assessment repair, not a label-only change.',completeSuite=False,apkAssemblyInstallSigning='NOT_RUN',apkHashCertificateSignature='NOT_ESTABLISHED;configured dev.russiancompanion/2.0.0-dev/code5',environment='Install/start settings previously saved unchanged;publication user-confirmed independently unobserved',physicalAcceptance='OUTSTANDING_LAST',approval='NONE'))
print('Persisted CC020 inventory and actual13methods:8current-pack/5historical-pack, all passed; no app or test changes.')
