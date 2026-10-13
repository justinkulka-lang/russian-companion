"""Capture successful current focused scope; never infer complete-suite acceptance."""
import json,hashlib,shutil,subprocess,xml.etree.ElementTree as E,re,fnmatch
from pathlib import Path
r=Path('/workspace/russian-companion');o=Path(__file__).resolve().parent
sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
git=lambda *a:subprocess.check_output(['git',*a],cwd=r).decode().strip()
log=Path('/workspace/setup/ia016-u24-integration.txt');s=log.read_text();assert 'BUILD SUCCESSFUL' in s
shutil.copy2(log,o/'INTEGRATION_VERIFICATION.txt');classes=[];totals={k:0 for k in ['tests','failures','errors','skipped']}
for f in sorted((r/'app/build/test-results/testDebugUnitTest').glob('TEST-*.xml')):
 x=E.parse(f).getroot();counts={k:int(x.get(k)) for k in totals};classes.append(dict(className=x.get('name'),timestamp=x.get('timestamp'),**counts,xmlSha256=sha(f),methods=[dict(name=t.get('name'),failed=t.find('failure') is not None,skipped=t.find('skipped') is not None) for t in x.findall('testcase')]))
 for k,v in counts.items():totals[k]+=v
 shutil.copy2(f,o/('INTEGRATION_'+f.name))
assert totals==dict(tests=33,failures=0,errors=0,skipped=0),totals
script=(o/'verify.sh').read_text();filters=re.findall(r"--tests '([^']+)'",script);assert len(filters)==18
for pattern in filters:assert any(fnmatch.fnmatch(c['className'],pattern) or any(fnmatch.fnmatch(c['className']+'.'+t['name'],pattern) for t in c['methods']) for c in classes),pattern
lint=r/'app/build/reports/lint-results-debug.xml';sevs={}
for z in E.parse(lint).getroot().findall('issue'):sevs[z.get('severity')]=sevs.get(z.get('severity'),0)+1
assert sevs.get('Error',0)==sevs.get('Fatal',0)==0;shutil.copy2(lint,o/'FINAL_LINT.xml')
tasks={t:next(z for z in s.splitlines() if z.startswith('> Task :app:'+t)) for t in ['compileDebugSources','lintDebug','verifyReleaseGateAttachment']}
assert 'UP-TO-DATE' not in tasks['verifyReleaseGateAttachment']
d=json.load(open('/workspace/setup/u27-digests.json'));unit=json.load(open(r/'review/unit-24/UNIT_24_REVIEW_MANIFEST.json'));assert unit['packContentReviewDigest']==d['pack'] and unit['repositorySourceDigest']==d['source'];assert not git('diff','--','app/src/main','app/src/test','review','release','audit/curriculum-readiness','audit/owner-governance')
m=dict(verifiedRepositoryCommit=git('rev-parse','HEAD'),implementationCommit=unit['curriculumSourceCommit'],baselineCommit='3b8b6df0150d406f8f71a2bc7eebd1a19a301eba',workingDirectory=str(r),command='bash audit/independent-gpt61-2026-10-12/ia016-unit24-context-verification/verify.sh',expandedScript=script,exitCode=0,scope='33_FOCUSED_SELECTED_TESTS_NOT_COMPLETE_SUITE',totals=totals,classes=classes,all18SelectorsExecuted=True,workingTreeNote='Production/curriculum/test/review/readiness/candidate inputs committed before execution; evidence capture.py added during run outside test/source inputs.',
 lint=dict(status='PASSED',severities=sevs,task=tasks['lintDebug'],sha256=sha(lint)),compilation=tasks['compileDebugSources']+'; production/test compilation also executed in prior generators and validators',releaseGateAttachment=dict(status='PASSED',task=tasks['verifyReleaseGateAttachment']),
 rawCurriculumSha256=sha(r/'app/src/main/assets/content/v2-curriculum.json'),packContentReviewDigest=d['pack'],repositorySourceDigest=d['source'],logSha256=sha(log),initScriptSha256={n:sha(Path('/workspace/setup')/n) for n in ['cloud-validation.gradle','parallel-tests.gradle','checkpoint-tools.gradle']},
 screenshotExporterExclusions=[dict(className=n,methods=k,reason='Overwrites retained tracked screenshot evidence; explicitly excluded from environment setup verification.') for n,k in [('VisualReviewCaptureTest',2),('Units35ChunkCaptureTest',2),('Units610ChunkCaptureTest',10),('Units1115GapCaptureTest',2)]],excludedMethods=16,
 baselineNegative=json.load(open(o/'BASELINE_CONTEXT.json')),earlyValidation=json.load(open(o/'EARLY_VALIDATION_CONTEXT.json')),snapshotExpectationRefresh=json.load(open(o/'SNAPSHOT_EXPECTATION_REFRESH.json')),
 historicalScope=dict(mainCommit='ff539220645670fbf7a1abacaa6f71e828606b80',mainSourceAnnotations=579,exporters=16,mainReported=563,mainPassed=562,mainSkipped=1,pr64BaselineCommit='420f41ff6c2a4417cd829fdee0179b6a3b797eff',pr64SourceAnnotations=691,difference=112,handoffExecutionVerified=False,interpretation='This focused execution does not prove historical complete-suite claims. Source annotations differ by112 across commits; reported historical execution remains unverified. Overlapping current scopes not summed.'),
 apk=dict(applicationId='dev.russiancompanion',versionName='2.0.0-dev',versionCode=5,assembled=False,signed=False,artifactIdentity='NOT_RECORDED',sha256='NOT_RECORDED',signature='NOT_RECORDED',historicalArtifactsAreNotCurrentCandidate=True),
 fixtureScopeReconciliation=json.load(open(o/'FIXTURE_SCOPE_RECONCILIATION.json')),coverageScope='Distinct introductory guided shop subject-focus source example; both correct independent persuasion items unchanged. Original guided persuasion item optional/non-gating with teaching. Three cards/full remediation. Required independent evidence remains two supplied persuasion readings; no independent subject-focus or productive word-order mastery claim.',productiveWordOrderClaim=False,independentSubjectFocusClaim=False,formalSourceRightsLinguisticAcceptance=False,physicalDeviceAcceptance='OUTSTANDING_LAST_AFTER_FINAL_INDEPENDENT_COMPREHENSIVE_AUDIT',release='NOT_APPROVED')
(o/'FINAL_VERIFICATION_MANIFEST.json').write_text(json.dumps(m,indent=2)+'\n');print(dict(commit=m['verifiedRepositoryCommit'],totals=totals,classes=len(classes),lint=sevs,gate=tasks['verifyReleaseGateAttachment']))
