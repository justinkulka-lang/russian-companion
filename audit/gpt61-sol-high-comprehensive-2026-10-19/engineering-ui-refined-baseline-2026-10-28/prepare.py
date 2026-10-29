from pathlib import Path
import subprocess,json,hashlib,shutil,zipfile
R=Path('/workspace/russian-companion');base=R/'audit/gpt61-sol-high-comprehensive-2026-10-19';OLD=base/'engineering-ui-feedback-onboarding-baseline-2026-10-28';D=base/'engineering-ui-refined-baseline-2026-10-28';W=Path('/workspace/artifacts/ui-refined-baseline-2026-10-28')
assert not subprocess.check_output(['git','status','--porcelain=v1','-z','--untracked-files=all'],cwd=R)
D.mkdir();W.mkdir();sha=lambda b:hashlib.sha256(b).hexdigest();save=lambda p,v:p.write_text(json.dumps(v,indent=2)+'\n');git=lambda *a:subprocess.check_output(['git',*a],cwd=R).decode().strip()
parent=git('rev-parse','HEAD');test='app/src/test/java/dev/russiancompanion/v2/SelectedOptionRationaleUiTest.kt';prior={p.relative_to(R).as_posix():sha(p.read_bytes())for p in(R/'app/src/test').rglob('*')if p.is_file()}
p=R/test;s=p.read_text();s=s.replace('.performScrollTo().assertTextContains(rationale).assertTextContains(item.feedback)', '.performScrollTo().assertTextEquals("Not the reviewed answer. ${item.feedback}")\n                compose.onNodeWithTag("v2-item-rationale-${item.id}")\n                    .performScrollTo().assertTextEquals(rationale).assertIsDisplayed()',1)
s=s.replace('.performScrollTo().assertTextContains(item.feedback)\n            assertTrue', '.performScrollTo().assertTextEquals("Recorded as correct. ${item.feedback}")\n            compose.onNodeWithTag("v2-item-rationale-${item.id}").assertDoesNotExist()\n            assertTrue',1)
s=s.replace('.assertTextContains(rationale).assertTextContains(item.feedback)', '.assertTextEquals("Not the reviewed answer. ${item.feedback}")\n            compose.onNodeWithTag("v2-lab-rationale").performScrollTo().assertTextEquals(rationale).assertIsDisplayed()',1)
s=s.replace('.assertTextContains(rationale).assertTextContains(item.feedback)', '.assertTextEquals("Not the reviewed answer. ${item.feedback}")\n            compose.onNodeWithTag("v2-review-rationale-$index").performScrollTo().assertTextEquals(rationale).assertIsDisplayed()',1)
assert 'assertTextContains' not in s
p.write_text(s)
for n,h in prior.items():
 if n!=test:assert sha((R/n).read_bytes())==h
subprocess.run(['git','add',test],cwd=R,check=True);tree=git('write-tree');trees={n:git('rev-parse',tree+':'+n)for n in ['app/src/main','app/src/test','catalog','docs','review','release']}
for n,h in trees.items():
 if n!='app/src/test':assert h==git('rev-parse',parent+':'+n)
scope=json.loads((OLD/'SCOPE.json').read_text());scope['inputTrees']=trees;scope['baselineExpectedDistribution']='hypothesis:3missing separate rationale nodes +2state failures +1semantics pass; actual outcomes required'
prep={'parentCommit':parent,'inputTrees':trees,'preExistingTestHashes':{n:h for n,h in prior.items()if n!=test},'newBaselineTestHashes':{test:sha(p.read_bytes())},'revisedOnlyNewTest':test,'all282OlderTestsUnchanged':True,'other283FilesUnchanged':True,'productionUnchangedInBaseline':True,'originalBaseline':str(OLD.relative_to(R)),'reason':'Preserve exact original common feedback and require separate exact authored rationale; retain original failure witnesses','bindings':'RECONCILIATION_PENDING'}
for n,v in [('SCOPE.json',scope),('PREPARATION.json',prep)]:save(D/n,v);save(W/n,v)
with zipfile.ZipFile(D/'REVISED_BASELINE_TESTS.zip','x',zipfile.ZIP_DEFLATED)as z:
 for n in [test,'app/src/test/java/dev/russiancompanion/v2/OnboardingStateRestorationTest.kt']:z.writestr(n,(R/n).read_bytes())
for n in ['run.py','launch.py','supervise.py','resources.gradle']:shutil.copyfile(OLD/n,W/n);shutil.copyfile(OLD/n,D/n)
shutil.copyfile(__file__,D/'prepare.py');shutil.copyfile(__file__,W/'prepare.py')
(D/'STATUS.md').write_text('''# Refined feedback placement baseline

The first baseline at 37ea3178 remains immutable (6 reported, 1 passed, 5 failed). Only the new SelectedOptionRationaleUiTest assertions now require the original common feedback exactly plus a separate exact authored rationale node, absent after a correct answer. No older assertions, method IDs, grading/credit/assignment controls or onboarding tests changed. All 282 older test files and all production remain unchanged. Other 283 current test files exact. Run fresh discovery and runtime on these six methods in two full classes before production changes. Inventory is not runtime evidence. All 48 findings OPEN, audit INCOMPLETE; bindings pending; physical acceptance last; no APK/signing/merge/freeze/approval. Four screenshot exporters/16 methods remain excluded in the captured runner/init configuration.
''')
save(D/'SHA256.json',{p.relative_to(D).as_posix():sha(p.read_bytes())for p in D.rglob('*')if p.is_file()and p.name!='SHA256.json'})
subprocess.run(['git','add',str(D)],cwd=R,check=True);subprocess.run(['git','diff','--cached','--check'],cwd=R,check=True);subprocess.run(['git','commit','-m','Refine new rationale checks to preserve exact common feedback'],cwd=R,check=True)
