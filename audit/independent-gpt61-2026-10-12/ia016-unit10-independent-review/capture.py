"""Capture the nine selected methods; keep historical scopes separate."""
from pathlib import Path
import json,hashlib,subprocess,xml.etree.ElementTree as E,shutil,re,fnmatch
root=Path(__file__).resolve().parents[3];out=Path(__file__).resolve().parent;sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest();git=lambda *a:subprocess.check_output(['git',*a],cwd=root,text=True).strip()
log=Path('/workspace/setup/u10-independent-verification.txt');text=log.read_text();assert 'BUILD SUCCESSFUL' in text
counts={k:0 for k in ['tests','failures','errors','skipped']};classes=[]
for f in sorted((root/'app/build/test-results/testDebugUnitTest').glob('TEST-*.xml')):
 x=E.parse(f).getroot();c={k:int(x.attrib[k]) for k in counts}
 for k in counts:counts[k]+=c[k]
 classes.append(dict(name=x.attrib['name'],timestamp=x.attrib['timestamp'],counts=c,sha256=sha(f),methods=[t.attrib['name'] for t in x.findall('testcase')]))
 shutil.copyfile(f,out/('TEST_'+f.name))
assert counts==dict(tests=9,failures=0,errors=0,skipped=0) and len(classes)==3
script=(out/'verify.sh').read_text();filters=re.findall(r"--tests '([^']+)'",script);assert len(filters)==3
for p in filters:assert any(fnmatch.fnmatch(c['name'],p) or any(fnmatch.fnmatch(c['name']+'.'+t,p) for t in c['methods']) for c in classes),p
context=json.loads((out/'EXECUTION_CONTEXT.json').read_text());preserve=json.loads((out/'SOURCE_AND_PRESERVATION.json').read_text())
assert sha(root/'app/src/test/java/dev/russiancompanion/v2/Unit10IndependentReinforcementTest.kt')==context['testSha256']
for f in preserve['identities']+preserve['originalDiagnosticFiles']:assert sha(root/f['path'])==f['sha256']
assert not git('diff',context['baselineRepositoryCommit'],'--','app/src/main','catalog','review','release','audit/curriculum-readiness','audit/owner-governance')
for n,h in context['initScripts'].items():assert sha(Path(n))==h
lint=root/'app/build/reports/lint-results-debug.xml';severities={}
for i in E.parse(lint).getroot().findall('issue'):severities[i.attrib['severity']]=severities.get(i.attrib['severity'],0)+1
assert severities.get('Error',0)==severities.get('Fatal',0)==0
shutil.copyfile(lint,out/'LINT.xml');shutil.copyfile(log,out/'EXECUTION.txt')
tasks={n:next(s for s in text.splitlines() if s.startswith('> Task :app:'+n)) for n in ['compileDebugSources','lintDebug','verifyReleaseGateAttachment']};assert 'UP-TO-DATE' not in tasks['verifyReleaseGateAttachment']
manifest=json.loads((root/'review/unit-10/UNIT_10_REVIEW_MANIFEST.json').read_text());candidate=json.loads((root/'release/acceptance/candidate.json').read_text());assert candidate['sourceDigest']==manifest['repositorySourceDigest'] and candidate['frozen'] is False and candidate['sourceCommit'] is None and candidate['publicRelease']=='NOT_APPROVED'
m=dict(repositoryCommit=git('rev-parse','HEAD'),baselineRepositoryCommit=context['baselineRepositoryCommit'],scope='9_FOCUSED_METHODS_3_CLASSES_3_SELECTORS_NOT_COMPLETE_SUITE',expandedCommand=script,exitCode=0,counts=counts,classes=classes,allThreeSelectorsExecuted=True,testSha256=context['testSha256'],initScripts=context['initScripts'],logSha256=sha(log),lint=dict(task=tasks['lintDebug'],severities=severities,sha256=sha(lint)),releaseGateAttachment=dict(status='PASSED',task=tasks['verifyReleaseGateAttachment']),compilation=dict(task=tasks['compileDebugSources'],note='Reused valid main outputs; current test compilation is recorded in the raw log.'),rawCurriculumSha256=sha(root/'app/src/main/assets/content/v2-curriculum.json'),packContentReviewDigest=manifest['packContentReviewDigest'],repositorySourceDigest=manifest['repositorySourceDigest'],productionSourceReviewReleaseUnchanged=True,allNineOriginalDiagnosticFilesPreserved=True,newNativeAdmission=False,exclusions=dict(VisualReviewCaptureTest=2,Units35ChunkCaptureTest=2,Units610ChunkCaptureTest=10,Units1115GapCaptureTest=2),excludedExporterMethods=16,exclusionReason='Screenshot exporters excluded from environment setup verification; overwrite retained tracked screenshots.',apk=dict(applicationId='dev.russiancompanion',versionName='2.0.0-dev',versionCode=5,assembled=False,signed=False,identity='NOT_RECORDED',sha256='NOT_RECORDED',signature='NOT_RECORDED'),historicalScope=dict(mainCommit='ff539220645670fbf7a1abacaa6f71e828606b80',mainAnnotations=579,mainReported=563,mainPassed=562,mainSkipped=1,excluded=16,pr64BaselineCommit='420f41ff6c2a4417cd829fdee0179b6a3b797eff',pr64Annotations=691,annotationDifference=112,handoffExecutionVerified=False,completeSuiteClaim=False),formalAcceptance=False,physicalAcceptance='OUTSTANDING_LAST_AFTER_FINAL_INDEPENDENT_COMPREHENSIVE_AUDIT',release='NOT_APPROVED')
(out/'VERIFICATION_MANIFEST.json').write_text(json.dumps(m,indent=2)+'\n');print(dict(commit=m['repositoryCommit'],counts=counts,lint=severities,tasks=tasks))
