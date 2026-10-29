from pathlib import Path
import subprocess,json,hashlib,shutil,re,ast,zipfile
R=Path('/workspace/russian-companion');B=R/'audit/gpt61-sol-high-comprehensive-2026-10-19';D=B/'engineering-unit-identity-cache-baseline-2026-10-28';W=Path('/workspace/artifacts/unit-identity-cache-baseline-2026-10-28')
OLD=Path('/workspace/artifacts/engineering-unit14-downstream-fixture-followup-2026-10-28')
assert not subprocess.check_output(['git','status','--porcelain=v1','-z','--untracked-files=all'],cwd=R)
assert not D.exists()and not W.exists();D.mkdir();W.mkdir()
git=lambda *a:subprocess.check_output(['git',*a],cwd=R).decode().strip()
sha=lambda b:hashlib.sha256(b).hexdigest()
save=lambda p,v:p.write_text(json.dumps(v,indent=2)+'\n')
parent=git('rev-parse','HEAD');before={p.relative_to(R).as_posix():sha(p.read_bytes())for p in(R/'app/src/test').rglob('*')if p.is_file()};assert len(before)==281
name='app/src/test/java/dev/russiancompanion/v2/UnitIdentityCacheReadThroughTest.kt';source=Path('/workspace/artifacts/codex-unit-identity-cache-baseline-2026-10-28/UnitIdentityCacheReadThroughTest.kt');assert not(R/name).exists();shutil.copyfile(source,R/name)
with zipfile.ZipFile(D/'ORIGINAL_BASELINE_TEST.zip','x',zipfile.ZIP_DEFLATED)as z:z.writestr(name,source.read_bytes())
subprocess.run(['git','add',name],cwd=R,check=True);tree=git('write-tree');inputpaths=['app/src/main','app/src/test','catalog','docs','review','release'];trees={p:git('rev-parse',tree+':'+p)for p in inputpaths}
methods=re.findall(r'@Test\s+fun\s+(\w+)\s*\(',source.read_text());assert len(methods)==3
scope={'inputTrees':trees,'selectors':['dev.russiancompanion.v2.UnitIdentityCacheReadThroughTest'],'expectedMethods':[{'testClass':'dev.russiancompanion.v2.UnitIdentityCacheReadThroughTest','method':m}for m in methods],'methodCount':3,'classes':1,'inventoryOnlyUntilFreshDiscovery':True,'baselineExpectedDistribution':'hypothesis only:2canonical-count failures/1strict identity control pass','completeSuite':False}
prep={'parentCommit':parent,'sourceMainTree':trees['app/src/main'],'inputTrees':trees,'preExistingTestHashes':before,'newBaselineTestPath':name,'newBaselineTestSha256':sha(source.read_bytes()),'existingTestsUnchanged':281,'newMethods':3,'productionUnchanged':True,'noSourceBindingMutation':True,'noFixtureAssertionMutation':True,'interrupted119Source':'52cb624978fa99be9b8c2162a51f8e09c13d2c01','interrupted119Checkpoint':'06942ee4cc0d1787ff5d69da6680050ca133e52f','all48Findings':'OPEN','audit':'INCOMPLETE','physicalAcceptance':'OUTSTANDING_LAST'}
for n,v in [('PREPARATION.json',prep),('SCOPE.json',scope)]:save(D/n,v);save(W/n,v)
for n in ['run.py','launch.py','supervise.py','resources.gradle']:
 text=(OLD/n).read_text()
 if n=='run.py':text=text.replace('119 methods / 17 full classes:60 original broad failures,45 siblings,14 unchanged Unit14/15 controls; preserve every outcome; not complete suite','3 methods /1fullclass: canonical-execution-count cold and eviction baselines plus strict cross-pack identity control; preserve actual outcomes; not complete suite')
 (W/n).write_text(text);(D/n).write_text(text)
shutil.copyfile(__file__,D/'prepare.py');shutil.copyfile(__file__,W/'prepare.py')
for p in W.glob('*.py'):ast.parse(p.read_text())
(D/'STATUS.md').write_text('''# Unit identity cache baseline prepared

One new3method FULLclass counts repeated canonical unit digest work on a cold and an evicted immutable pack, with a changed-pack rejection control. No production correction is applied. All281pre-existing test files, source, bindings and original fixtures are unchanged. The one-unit copy is a digest fixture, not an admitted replacement curriculum. Baseline expected2count failures/1strict control pass remains a hypothesis until actual execution. Entire baseline test bytes are archived unchanged.

Fresh discovery and actual runtime are NOT_RUN at this checkpoint. Inspect external /workspace/artifacts/unit-identity-cache-baseline-2026-10-28 contexts/PIDs/completions before launch/edit. Prior119run is intentionally incomplete:68SUCCESS/2interruptionSKIPPED/49noevent, not a completed run; all previous scopes separate/unsummed. No further Cursor use. All48OPEN/auditINCOMPLETE/four exporters16excluded/physicalLAST/noAPK/signing/merge/freeze/releaseapproval.
''')
(D/'NEXT_TASK.md').write_text('Run fresh3method/1class discovery and baseline at exact source/input trees; preserve original outcomes before applying any cache correction. If canonical counts demonstrate the repeated work while identity controls pass, implement a narrow bounded weak read-through cache, preserving canonical hashes and original assertions. Source bindings become stale after production edits and require authentic compiled reconciliation. Then verify affected FULLclasses and retry full119scope at separately recorded inputs. Do not sum interrupted/baseline/followup runs. Codex only; all48OPEN/auditINCOMPLETE/physicalLAST/noAPK/signing/merge/freeze/approval.\n')
rel=D.relative_to(R).as_posix();pointers=['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']
for n in pointers:
 p=R/n;link=rel+'/STATUS.md'if n=='AGENTS.md'else'../'+rel.removeprefix('audit/')+'/STATUS.md'
 p.write_text(f'Current checkpoint: [Cold/evicted unit identity baseline prepared]({link}).3new methods/1FULLclass inventory only;production/all281oldertests/bindings/fixtures unchanged. Inspect external unit-identity-cache-baseline-2026-10-28 contexts/PIDs/completions before launch/edit. Prior119intentionallyincomplete68SUCCESS/2interruptionSKIPPED/49noevent;notcompleted. Codex only;no further Cursor use. All48OPEN/auditINCOMPLETE/four exporters16excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
save(D/'SHA256.json',{p.relative_to(D).as_posix():sha(p.read_bytes())for p in sorted(D.rglob('*'))if p.is_file()and p!=D/'SHA256.json'})
allowed=set(pointers)|{name}
for row in subprocess.check_output(['git','status','--porcelain=v1','-z','--untracked-files=all'],cwd=R).split(b'\0'):
 if not row:continue
 assert row[2:3]==b' 'and not any(k in row[:2]for k in[b'R',b'C']);n=row[3:].decode();assert n in allowed or n.startswith(rel+'/'),n
subprocess.run(['git','add',rel,*sorted(allowed)],cwd=R,check=True);subprocess.run(['git','diff','--cached','--check'],cwd=R,check=True)
subprocess.run(['git','commit','-m','Capture cold and evicted unit identity digest regressions'],cwd=R,check=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
print(json.dumps({'baselineCommit':git('rev-parse','HEAD'),'methods':3,'classes':1,'productionUnchanged':True,'oldTestsUnchanged':281,'runtime':'NOT_RUN'}))
