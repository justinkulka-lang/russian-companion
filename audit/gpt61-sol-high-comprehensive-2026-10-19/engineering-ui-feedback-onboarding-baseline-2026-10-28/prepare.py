from pathlib import Path
import subprocess,json,hashlib,shutil,re,ast,zipfile
R=Path('/workspace/russian-companion');B=R/'audit/gpt61-sol-high-comprehensive-2026-10-19';D=B/'engineering-ui-feedback-onboarding-baseline-2026-10-28';W=Path('/workspace/artifacts/ui-feedback-onboarding-baseline-2026-10-28');OLD=Path('/workspace/artifacts/unit-identity-cache-correction-2026-10-28');DRAFT=Path('/workspace/artifacts/codex-ui-presentation-baseline-2026-10-28')
assert not subprocess.check_output(['git','status','--porcelain=v1','-z','--untracked-files=all'],cwd=R)
assert not D.exists()and not W.exists();D.mkdir();W.mkdir()
git=lambda *a:subprocess.check_output(['git',*a],cwd=R).decode().strip();sha=lambda b:hashlib.sha256(b).hexdigest();save=lambda p,v:p.write_text(json.dumps(v,indent=2)+'\n')
parent=git('rev-parse','HEAD');before={p.relative_to(R).as_posix():sha(p.read_bytes())for p in(R/'app/src/test').rglob('*')if p.is_file()};assert len(before)==282
newtests=[];methods=[];selectors=[]
with zipfile.ZipFile(D/'ORIGINAL_BASELINE_TESTS.zip','x',zipfile.ZIP_DEFLATED)as z:
 for name in ['SelectedOptionRationaleUiTest.kt','OnboardingStateRestorationTest.kt']:
  source=DRAFT/name;rel='app/src/test/java/dev/russiancompanion/v2/'+name;assert not(R/rel).exists();shutil.copyfile(source,R/rel);newtests.append(rel);z.writestr(rel,source.read_bytes())
  cls='dev.russiancompanion.v2.'+name.removesuffix('.kt');selectors.append(cls);methods +=[{'testClass':cls,'method':m}for m in re.findall(r'@Test\s+fun\s+(\w+)\s*\(',source.read_text())]
assert len(methods)==6
subprocess.run(['git','add',*newtests],cwd=R,check=True);tree=git('write-tree');paths=['app/src/main','app/src/test','catalog','docs','review','release'];trees={p:git('rev-parse',tree+':'+p)for p in paths}
for p in paths:
 if p!='app/src/test':assert trees[p]==git('rev-parse',parent+':'+p)
prep={'parentCommit':parent,'inputTrees':trees,'preExistingTestHashes':before,'newBaselineTestHashes':{p:sha((R/p).read_bytes())for p in newtests},'existingTestsUnchanged':282,'newMethods':6,'productionUnchangedInBaseline':True,'bindings':'RECONCILIATION_PENDING after cache correction;unchanged here','all48Findings':'OPEN','audit':'INCOMPLETE','physicalAcceptance':'OUTSTANDING_LAST'}
scope={'inputTrees':trees,'selectors':selectors,'expectedMethods':methods,'methodCount':6,'classes':2,'inventoryOnlyUntilFreshDiscovery':True,'baselineExpectedDistribution':'hypothesis only:3rationale UI failures+2saved-state failures+1merged-semantics control pass','completeSuite':False}
for n,v in [('PREPARATION.json',prep),('SCOPE.json',scope)]:save(D/n,v);save(W/n,v)
for n in ['run.py','launch.py','supervise.py','resources.gradle']:
 text=(OLD/n).read_text()
 if n=='run.py':text=text.replace('25 methods /2FULLclasses: unchanged3method cache baseline plus entire22method existing RemainingReauditCorrectionTest including cold/warm/off-main and weak-cache guards; preserve every outcome; not complete suite','6 methods /2FULLclasses: actual selected-rationale UI and onboarding saved-state baselines with merged-semantics control;preserve every outcome;not complete suite')
 (W/n).write_text(text);(D/n).write_text(text)
shutil.copyfile(__file__,D/'prepare.py');shutil.copyfile(__file__,W/'prepare.py')
for p in W.glob('*.py'):ast.parse(p.read_text())
(D/'STATUS.md').write_text('''# Selected feedback and onboarding UI baseline prepared

Two new test files/6methods exercise lesson, optional-practice and assigned-review wrong-option explanations, onboarding saved-state restoration without automatic submission, and merged lesson-option label/role/selection/click semantics. They call existing UI APIs before any corresponding production UI correction. Existing282tests and all production/source/content/binding/history bytes remain unchanged in this baseline. The cache correction is the current production source and has a separate completed25/25verification.

Expected3rationale+2state failures/1semantics control pass is only a hypothesis. Preserve every actual outcome; do not alter assertions to force that distribution. Fresh discovery/runtime NOT_RUN. This is host API33 evidence, not physical rotation/processdeath/TalkBack or reboot acceptance. Source bindings remain RECONCILIATION_PENDING from the cache change. Inspect /workspace/artifacts/ui-feedback-onboarding-baseline-2026-10-28 before launch/edit. Codex only;all48OPEN/auditINCOMPLETE/four exporters16excluded/physicalLAST/noAPK/signing/merge/freeze/approval.
''')
(D/'NEXT_TASK.md').write_text('Run fresh6method/2FULLclass discovery and actual baseline. Review every actual failure/positive control before bounded UI edits. Preserve entire baseline and all282older tests. Correct selected-option explanations by authored value while retaining common feedback/grading/credit; save onboarding page and allchoices without automatic submission. Resolve genuine fixture/compiler problems transparently without claiming test execution. After production changes, authentic bindings and affected FULLclasses/full119retry precede stable broad/final independent audit. No further Cursor use. All48OPEN/auditINCOMPLETE/physicalLAST/noAPK/signing/merge/freeze/approval.\n')
rel=D.relative_to(R).as_posix();pointers=['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']
for n in pointers:
 p=R/n;link=rel+'/STATUS.md'if n=='AGENTS.md'else'../'+rel.removeprefix('audit/')+'/STATUS.md'
 p.write_text(f'Current checkpoint: [Feedback/onboarding UI baseline prepared]({link}).6newmethods/2FULLclasses inventory only;production/all282oldertests unchanged. Actualcachecorrection341444a1:25/25passed separately,unsummed. BindingsRECONCILIATION_PENDING. Inspect external ui-feedback-onboarding-baseline-2026-10-28 contexts/PIDs before launch/edit. Codex only/no further Cursor use. All48OPEN/auditINCOMPLETE/four exporters16excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
save(D/'SHA256.json',{p.relative_to(D).as_posix():sha(p.read_bytes())for p in sorted(D.rglob('*'))if p.is_file()and p!=D/'SHA256.json'})
allowed=set(pointers)|set(newtests)
for row in subprocess.check_output(['git','status','--porcelain=v1','-z','--untracked-files=all'],cwd=R).split(b'\0'):
 if not row:continue
 assert row[2:3]==b' 'and not any(k in row[:2]for k in[b'R',b'C']);n=row[3:].decode();assert n in allowed or n.startswith(rel+'/'),n
subprocess.run(['git','add',rel,*sorted(allowed)],cwd=R,check=True);subprocess.run(['git','diff','--cached','--check'],cwd=R,check=True)
subprocess.run(['git','commit','-m','Capture feedback and onboarding restoration UI regressions'],cwd=R,check=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
print(json.dumps({'baselineCommit':git('rev-parse','HEAD'),'methods':6,'classes':2,'oldTestsUnchanged':282,'productionUnchanged':True,'runtime':'NOT_RUN'}))
