from pathlib import Path
import subprocess,json,hashlib,shutil,re,zipfile
R=Path('/workspace/russian-companion');B=R/'audit/gpt61-sol-high-comprehensive-2026-10-19';OLD=B/'engineering-ui-refined-baseline-2026-10-28';D=B/'engineering-ui-feedback-onboarding-correction-2026-10-28';W=Path('/workspace/artifacts/ui-feedback-onboarding-correction-2026-10-28');DRAFT=Path('/workspace/artifacts/ui-correction-draft-v2')
assert not subprocess.check_output(['git','status','--porcelain=v1','-z','--untracked-files=all'],cwd=R)
assert (OLD/'RAW_SCOPE_VERIFICATION.json').exists()
D.mkdir();W.mkdir();sha=lambda b:hashlib.sha256(b).hexdigest();save=lambda p,v:p.write_text(json.dumps(v,indent=2)+'\n');git=lambda *a:subprocess.check_output(['git',*a],cwd=R).decode().strip()
parent=git('rev-parse','HEAD');tests={p.relative_to(R).as_posix():sha(p.read_bytes())for p in(R/'app/src/test').rglob('*')if p.is_file()};assert len(tests)==284
ids=json.loads((DRAFT/'SOURCE_IDENTITIES.json').read_text())
for rel,v in ids.items():
 p=R/rel;assert (sha(p.read_bytes())if p.exists()else None)==v['beforeSha256'];data=(DRAFT/p.name).read_bytes();assert sha(data)==v['afterSha256'];p.write_bytes(data)
for n,h in tests.items():assert sha((R/n).read_bytes())==h
subprocess.run(['git','add',*ids.keys()],cwd=R,check=True);tree=git('write-tree');trees={n:git('rev-parse',tree+':'+n)for n in ['app/src/main','app/src/test','catalog','docs','review','release']}
for n,h in trees.items():
 if n!='app/src/main':assert h==git('rev-parse',parent+':'+n)
classes=['SelectedOptionRationaleUiTest','OnboardingStateRestorationTest','OptionPresentationUiTest','RuntimeCorrectionTest','V2RebuildTest','Unit14FeedbackCorrectionTest'];methods=[]
for cls in classes:
 s=(R/('app/src/test/java/dev/russiancompanion/v2/'+cls+'.kt')).read_text();methods.extend({'testClass':'dev.russiancompanion.v2.'+cls,'method':m}for m in re.findall(r'@Test\s+fun\s+(\w+)\s*\(',s))
assert len(methods)==61
scope={'inputTrees':trees,'selectors':['dev.russiancompanion.v2.'+c for c in classes],'expectedMethods':methods,'methodCount':61,'classes':6,'inventoryOnlyUntilFreshDiscovery':True,'completeSuite':False}
prep={'parentCommit':parent,'inputTrees':trees,'testHashes':tests,'testFilesUnchanged':284,'sourceChanges':ids,'baselineSource':json.loads((OLD/'RAW_SCOPE_VERIFICATION.json').read_text())['sourceCommit'],'wholeRevisedBaselineUnchanged':True,'originalBaselineRetained':True,'bindings':'RECONCILIATION_PENDING; authentic generation required after source changes','all48Findings':'OPEN','audit':'INCOMPLETE','physicalAcceptance':'OUTSTANDING_LAST'}
for n,v in [('SCOPE.json',scope),('PREPARATION.json',prep)]:save(D/n,v);save(W/n,v)
with zipfile.ZipFile(D/'ORIGINAL_PRODUCTION.zip','x',zipfile.ZIP_DEFLATED)as z:
 for n,v in ids.items():
  if v['beforeSha256']is not None:z.writestr(n,subprocess.check_output(['git','show',parent+':'+n],cwd=R))
for n in ['run.py','launch.py','supervise.py','resources.gradle']:
 s=(OLD/n).read_text()
 if n=='run.py':s=s.replace('6 methods /2FULLclasses: actual selected-rationale UI and onboarding saved-state baselines with merged-semantics control;preserve every outcome;not complete suite','61 methods /6FULLclasses: unchanged6methodUIbaseline plus existing option presentation/runtime/rebuild/Unit14feedback controls;not complete suite')
 (W/n).write_text(s);(D/n).write_text(s)
shutil.copyfile(__file__,D/'prepare.py');shutil.copyfile(__file__,W/'prepare.py')
(D/'STATUS.md').write_text('''# Feedback and onboarding UI correction prepared

Display the existing authored wrong-option rationale by option value in a separate text node for lessons, optional practice and assigned review. Original common feedback text/tags, answer grading, recorded values, credit, review assignment and scoring policies are unchanged. Missing/blank rationale and correct selections produce no new wrong-option rationale. Save onboarding page and all six draft choices with rememberSaveable; no submission occurs merely on restoration. Replace two inaccurate unconditional installation claims with truthful Unit 1 starting guidance. No source curriculum, canonical identity function, native answer, policy/schema, historical admission or source allowlist changes.

All 284 test files (including the whole refined six-method baseline) remain exact. Planned 61 methods in six FULL classes are inventory only until discovery/runtime. Preserve every actual outcome. Source bindings remain RECONCILIATION_PENDING. Earlier first/refined baselines and cache scope remain separate and unsummed. All 48 findings OPEN, audit INCOMPLETE; final independent comprehensive audit required before physical Galaxy S26 Ultra airplane-mode/reboot acceptance LAST. Four screenshot exporters/16 methods excluded. No APK assembled/installed/signed or merge/freeze/release approval.
''')
for n in ['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']:
 p=R/n;rel=D.relative_to(R).as_posix()+'/STATUS.md';link=rel if n=='AGENTS.md' else '../'+rel.removeprefix('audit/')
 p.write_text(f'Current checkpoint: [Feedback and onboarding correction prepared]({link}). Four production files/284tests unchanged;61methods/6FULLclasses inventory only. Inspect external ui-feedback-onboarding-correction-2026-10-28 before launch/edit. Original and refined baselines retained;bindingsPENDING. Codex only;all48OPEN/auditINCOMPLETE/four exporters16excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
save(D/'SHA256.json',{p.relative_to(D).as_posix():sha(p.read_bytes())for p in D.rglob('*')if p.is_file()and p.name!='SHA256.json'})
allowed=set(ids)|{n for n in ['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']}
for row in subprocess.check_output(['git','status','--porcelain=v1','-z','--untracked-files=all'],cwd=R).decode().split('\0'):
 if row:assert row[3:]in allowed or row[3:].startswith(str(D.relative_to(R))+'/'),row
subprocess.run(['git','add',str(D),*allowed],cwd=R,check=True);subprocess.run(['git','diff','--cached','--check'],cwd=R,check=True);subprocess.run(['git','commit','-m','Show selected option rationale and restore onboarding drafts'],cwd=R,check=True)
