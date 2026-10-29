from pathlib import Path
import json,hashlib,zipfile,subprocess,shutil
R=Path('/workspace/russian-companion');W=Path(__file__).parent;D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-ui-source-bindings-2026-10-22';sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest();save=lambda n,v:(D/n).write_text(json.dumps(v,indent=2)+'\n');git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
assert not git('status','--porcelain');assert (W/'REGRESSION/RUN.exit').read_text().strip()=='0';records={}
for mode in ['DISCOVERY','REGRESSION']:
 O=W/mode;assert (O/'SHA256.json').exists()
 for name,h in json.loads((O/'SHA256.json').read_text()).items():assert sha(O/name)==h
 (D/mode).mkdir()
 for name in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit']+(['LINT.xml'] if (O/'LINT.xml').exists() else []):shutil.copyfile(O/name,D/mode/name)
 files={p.relative_to(O).as_posix():p for p in sorted(O.rglob('*')) if p.is_file()};files['LAUNCHER/'+mode+'_LAUNCHER.log']=W/(mode+'_LAUNCHER.log')
 index={n:dict(sha256=sha(p),bytes=p.stat().st_size) for n,p in files.items()}
 with zipfile.ZipFile(D/('RAW_'+mode+'.zip'),'w',zipfile.ZIP_DEFLATED) as z:
  for n,p in files.items():z.write(p,n)
 with zipfile.ZipFile(D/('RAW_'+mode+'.zip')) as z:
  assert z.testzip() is None
  for n,v in index.items():assert hashlib.sha256(z.read(n)).hexdigest()==v['sha256']
 save('RAW_'+mode+'_INDEX.json',index);records[mode]=json.loads((O/'RESULT.json').read_text())
assert records['DISCOVERY']['testCounts']==dict(reported=67,passed=0,failed=0,errors=0,skipped=67)
assert records['REGRESSION']['testCounts']==dict(reported=67,passed=67,failed=0,errors=0,skipped=0)
assert records['REGRESSION']['classes']==11
proposal=json.loads((D/'PROPOSAL_REVIEW.json').read_text());prep=json.loads((D/'GENERATION_PREPARATION.json').read_text());index=json.loads((D/'HISTORICAL_PRIOR_CURRENT_INDEX.json').read_text());preserved=json.loads((D/'PRESERVED_PRECEDING_SCOPES.json').read_text())
for n,h in preserved.items():assert sha(R/n)==h,n
for row in index['files']:assert sha(R/row['path'])==row['newSha256']
archives=json.loads((R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-source-binding-reconciliation-2026-10-22/CONSERVATION.json').read_text())['original66ArchiveHashes']
for n,h in archives.items():
 assert sha(R/n)==h,n
 with zipfile.ZipFile(R/n) as z:assert z.testzip() is None
candidate=json.loads((R/'release/acceptance/candidate.json').read_text());assert candidate['sourceDigest']==prep['newDigest'] and candidate['frozen'] is False and candidate['sourceCommit'] is None and candidate['artifact']=={'status':'NOT_RECORDED'} and candidate['publicRelease']=='NOT_APPROVED'
source=records['REGRESSION']['sourceCommit'];assert records['DISCOVERY']['sourceCommit']==source
save('CONSERVATION.json',dict(precedingScopeFilesVerified=len(preserved),original66ArchiveHashes=archives,changed95HistoricalMembersVerified=True,unchanged60RecordsReused=True,current155RecordsVerified=True,preservedPrecedingScopeHashIndex='PRESERVED_PRECEDING_SCOPES.json',mainTestCatalogDocsTreesUnchanged={p:git('rev-parse',source+':'+p)==prep['mainTestCatalogDocsReviewTrees'][p] for p in ['app/src/main','app/src/test','catalog','docs']},allOtherGeneratedBytesExact=True,allApprovalAndMappingReviewerFieldsExact=True,privatePdfRead=False,physicalAcceptance='OUTSTANDING_LAST'))
old=json.loads((R/'audit/gpt61-sol-high-comprehensive-2026-10-19/post-recording-guard-host-regression-2026-10-22/REGRESSION/RESULT.json').read_text());actual={x['testClass']+'#'+x['method']:x['outcome'] for x in records['REGRESSION']['tests']};recheck=[dict(identity=x['testClass']+'#'+x['method'],historicalOutcome=x['outcome'],newOutcome=actual[x['testClass']+'#'+x['method']]) for x in old['tests'] if x['outcome']=='FAILED'];assert len(recheck)==9 and all(r['newOutcome']=='PASSED' for r in recheck)
save('NINE_HISTORICAL_FAILURE_RECHECK.json',dict(historicalSource=old['sourceCommit'],historicalScope=old['testCounts'],newSource=source,newScope=records['REGRESSION']['testCounts'],methodRechecks=recheck,originalRawFailuresRewritten=False,scopesSummed=False))
ui=json.loads((R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-v2-insets-accessibility-2026-10-22/FINAL/RESULT.json').read_text());assert all(actual[x['testClass']+'#'+x['method']]=='PASSED' for x in ui['tests'])
save('UI_ELEVEN_RECHECK.json',dict(priorUiSource=ui['sourceCommit'],newSource=source,identities=[x['testClass']+'#'+x['method'] for x in ui['tests']],allElevenPassed=True,priorRawScopePreserved=True,scopesSummed=False))
for name in ['persist.py','verify_saved_results.py']:shutil.copyfile(W/name,D/name)
save('CHECKPOINT.json',dict(status='CURRENT_UI_SOURCE_BINDINGS_AND_FOCUSED_HOST_SCOPE_VERIFIED',sourceCommit=source,sourceBindingDigest=prep['newDigest'],generationSource=prep['sourceCommit'],canonicalKotlinDigestRealAndProjectionMatch=True,twoFresh153FileJvmRendersIdentical=True,proposedRecords=155,changedFiles=95,unchangedFiles=60,discovery=records['DISCOVERY']['testCounts'],runtime=records['REGRESSION']['testCounts'],classes=11,lint=records['REGRESSION']['lintIssuesBySeverity'],candidate=dict(frozen=False,sourceCommit=None,artifactStatus='NOT_RECORDED',publicRelease='NOT_APPROVED'),all48IndependentFindings='NOT_CLOSED',all70OriginalRequests='ORIGINAL_NOT_EXECUTED_STATUSES_RETAINED',nextLargeTask='Investigate CC020 cumulative review against MA2-027 and existing assignment/restore compatibility before proposing any policy/schema change; then other high findings. Fresh broad run and final independent comprehensive nonphysical recheck remain separate work.',completeSuite=False,apkAssemblyInstallSigning='NOT_RUN',apkIdentity='Configured dev.russiancompanion/2.0.0-dev/code5; current APK hash/certificate/signature NOT_ESTABLISHED',environment='Install/start settings previously saved unchanged; publication user-confirmed, independently unobserved',physicalAcceptance='OUTSTANDING_LAST',approval='NONE'))
print('Persisted exact67method scope, prior95/unchanged60/current155, all preceding scopes and66archives.')
