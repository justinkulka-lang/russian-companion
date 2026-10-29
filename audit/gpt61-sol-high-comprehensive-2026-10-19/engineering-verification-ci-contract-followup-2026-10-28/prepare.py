from pathlib import Path
import subprocess,json,hashlib,shutil,zipfile,re
R=Path('/workspace/russian-companion');B=R/'audit/gpt61-sol-high-comprehensive-2026-10-19';D=B/'engineering-verification-ci-contract-followup-2026-10-28';W=Path('/workspace/artifacts/verification-ci-contract-followup-2026-10-28');OLD=B/'engineering-verification-ci-contract-baseline-2026-10-28';sha=lambda b:hashlib.sha256(b).hexdigest();save=lambda p,v:p.write_text(json.dumps(v,indent=2)+'\n');git=lambda *a:subprocess.check_output(['git',*a],cwd=R).decode().strip();assert not subprocess.check_output(['git','status','--porcelain=v1','-z','--untracked-files=all'],cwd=R);assert(OLD/'RAW_SCOPE_VERIFICATION.json').exists();D.mkdir();W.mkdir()
parent=git('rev-parse','HEAD');rel='app/src/test/java/dev/russiancompanion/acceptance/DeviceAcceptancePackageTest.kt';p=R/rel;before=p.read_bytes();s=before.decode();tests={q.relative_to(R).as_posix():sha(q.read_bytes())for q in(R/'app/src/test').rglob('*')if q.is_file()};assert len(tests)==284
historic='991d7ca4';historicFull=git('rev-parse',historic);oldWorkflow=subprocess.check_output(['git','show',historicFull+':.github/workflows/build.yml'],cwd=R);assert sha(oldWorkflow)==json.loads((B/'engineering-verification-only-ci-2026-10-28/CHANGES.json').read_text())['files']['.github/workflows/build.yml']['beforeSha256']
old='''        val workflow = File(root, ".github/workflows/build.yml").readText()'''
new=f'''        // Historical candidate-upload contract is retained against its exact original workflow.
        // Current engineering CI intentionally defers candidate assembly until after the final audit.
        val historicalWorkflow = ProcessBuilder("git", "show",
            "{historicFull}:.github/workflows/build.yml")
            .directory(root).redirectErrorStream(true).start()
        val workflow = historicalWorkflow.inputStream.bufferedReader().readText()
        assertEquals(0, historicalWorkflow.waitFor())
        assertEquals("{sha(oldWorkflow)}",
            java.security.MessageDigest.getInstance("SHA-256").digest(workflow.toByteArray())
                .joinToString("") {{ "%02x".format(it) }})
        val currentWorkflow = File(root, ".github/workflows/build.yml").readText()
        listOf("compileDebugSources", "lintDebug", "verifyReleaseGateAttachment", "testDebugUnitTest",
            "engineering-verification-source", "engineering-test-reports").forEach {{
            assertTrue("Current verification CI must retain $it", currentWorkflow.contains(it))
        }}
        listOf("assembleDebug", "packageDebug", "bundleDebug", "signDebug",
            "acceptance-candidate-debug-apk", "artifact-identity.json", "|| true").forEach {{
            assertFalse("Engineering CI must not create or hide failure for $it", currentWorkflow.contains(it))
        }}
        listOf("VisualReviewCaptureTest", "Units35ChunkCaptureTest", "Units610ChunkCaptureTest",
            "Units1115GapCaptureTest").forEach {{
            assertTrue("Retained-evidence exporter must stay excluded: $it", currentWorkflow.contains("**/$it*"))
        }}'''
assert s.count(old)==1;s=s.replace(old,new,1);p.write_text(s)
assert re.findall(r'\bassert(?:True|False|Equals|NotEquals|Null|NotNull|Same|NotSame|ArrayEquals)\([^\n]*',before.decode())==[x for x in re.findall(r'\bassert(?:True|False|Equals|NotEquals|Null|NotNull|Same|NotSame|ArrayEquals)\([^\n]*',s) if x in re.findall(r'\bassert(?:True|False|Equals|NotEquals|Null|NotNull|Same|NotSame|ArrayEquals)\([^\n]*',before.decode())]
for n,h in tests.items():
 if n!=rel:assert sha((R/n).read_bytes())==h
with zipfile.ZipFile(D/'ORIGINAL_TEST.zip','x',zipfile.ZIP_DEFLATED)as z:z.writestr(rel,before)
subprocess.run(['git','add',rel],cwd=R,check=True);tree=git('write-tree');trees={n:git('rev-parse',tree+':'+n)for n in ['app/src/main','app/src/test','catalog','docs','review','release']}
for n,v in trees.items():
 if n!='app/src/test':assert v==git('rev-parse',parent+':'+n)
scope=json.loads((OLD/'SCOPE.json').read_text());scope['inputTrees']=trees
prep={'parentCommit':parent,'inputTrees':trees,'preExistingTestHashes':{n:h for n,h in tests.items()if n!=rel},'newBaselineTestHashes':{rel:sha(p.read_bytes())},'changedTestFile':rel,'originalTestSha256':sha(before),'originalAssertionCallsRetainedInOrder':True,'other283TestFilesUnchanged':True,'historicalWorkflowCommit':historicFull,'historicalWorkflowSha256':sha(oldWorkflow),'currentEngineeringWorkflowAssertionsAdded':True,'productionBindingsContentAndReleaseFilesUnchanged':True}
for n,v in [('SCOPE.json',scope),('PREPARATION.json',prep)]:save(D/n,v);save(W/n,v)
for n in ['run.py','launch.py','supervise.py','resources.gradle']:shutil.copyfile(OLD/n,D/n);shutil.copyfile(OLD/n,W/n)
shutil.copyfile(__file__,D/'prepare.py');shutil.copyfile(__file__,W/'prepare.py')
(D/'STATUS.md').write_text('''# Current engineering CI contract distinguished from historical candidate workflow

The unchanged original test fails against verification-only CI because it demands automatic candidate APK upload. Preserve that actual baseline failure. Retain all original assertions in their original order, binding only the six candidate-workflow assertions to the exact historical workflow commit/SHA256. All current candidate identity, source digest, unexecuted physical forms, rights/release and gate assertions still use current repository files. Add explicit current-workflow controls: compile/lint/gate/tests and source/report artifacts present; APK tasks/candidate artifact/masked failures absent; all four exporters excluded. No scoring, review, source-history, source-binding, release or physical acceptance policy relaxed. Other283testfiles and production/bindings exact; official source digest unaffected by test-only change. One FULLclass/one method inventory; fresh discovery/runtime required. All48OPEN/auditINCOMPLETE/four exporters16excluded/physicalLAST/noAPK/signing/merge/freeze/approval.
''')
save(D/'SHA256.json',{p.relative_to(D).as_posix():sha(p.read_bytes())for p in D.rglob('*')if p.is_file()and p.name!='SHA256.json'});subprocess.run(['git','add',rel,str(D)],cwd=R,check=True);subprocess.run(['git','diff','--cached','--check'],cwd=R,check=True);subprocess.run(['git','commit','-m','Distinguish historical candidate workflow from current verification-only CI'],cwd=R,check=True)
