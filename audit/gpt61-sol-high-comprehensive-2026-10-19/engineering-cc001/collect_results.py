from pathlib import Path
import json,hashlib,shutil,subprocess,re,csv,xml.etree.ElementTree as E
from datetime import datetime, timezone
r=Path('/workspace/russian-companion');work=Path('/workspace/artifacts/cc001-options')
dest=r/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-cc001'
assert not dest.exists()
final=work/'FINAL';assert (final/'RUN.exit').read_text().strip()=='0'
(final/'XML').mkdir(exist_ok=False)
for p in (r/'app/build/test-results/testDebugUnitTest').glob('TEST-*.xml'):shutil.copy2(p,final/'XML'/p.name)
shutil.copy2(r/'app/build/reports/lint-results-debug.xml',final/'LINT.xml')
results={}
for label in ['INITIAL','FINAL']:
 o=work/label;c=json.loads((o/'CONTEXT.json').read_text());expected=set()
 for selector in c['selectors']:
  code=subprocess.check_output(['git','show',c['sourceCommit']+':app/src/test/java/'+selector.replace('.','/')+'.kt'],text=True)
  expected|={(selector,m) for m in re.findall(r'@Test\s+fun\s+(\w+)',code)}
 rows=[];stdout=''
 for xml in sorted((o/'XML').glob('TEST-*.xml')):
  suite=E.parse(xml).getroot();when=datetime.fromisoformat(suite.get('timestamp').replace('Z','+00:00'))
  if when.tzinfo is None:when=when.replace(tzinfo=timezone.utc)
  assert when.timestamp()>=(o/'run.sh').stat().st_mtime-1,(xml,when)
  stdout+='\n'+(suite.findtext('system-out') or '')
  for t in suite.findall('testcase'):
   status='FAILED' if t.find('failure') is not None else 'ERROR' if t.find('error') is not None else 'SKIPPED' if t.find('skipped') is not None else 'PASSED'
   rows.append(dict(testClass=t.get('classname'),method=t.get('name'),outcome=status,seconds=t.get('time')))
 events=[json.loads(x) for x in (o/'EVENTS.jsonl').read_text().splitlines()];events=[x for x in events if x['kind']=='RESULT']
 assert {(x['testClass'],x['method']) for x in rows}==expected
 assert sorted((x['testClass'],x['method']) for x in rows)==sorted((x['className'],x['name']) for x in events)
 assert len(rows)==len(expected)==c['expectedMethods']==33
 counts=dict(reported=len(rows),passed=sum(x['outcome']=='PASSED' for x in rows),failed=sum(x['outcome']=='FAILED' for x in rows),errors=sum(x['outcome']=='ERROR' for x in rows),skipped=sum(x['outcome']=='SKIPPED' for x in rows))
 assert counts['passed']==33 and int((o/'RUN.exit').read_text())==0
 lint={}
 for issue in E.parse(o/'LINT.xml').getroot().findall('issue'):lint[issue.get('severity')]=lint.get(issue.get('severity'),0)+1
 assert lint.get('Error',0)==lint.get('Fatal',0)==0
 log=(o/'RUN.log').read_text();tasks={}
 for task in ['testDebugUnitTest','compileDebugKotlin','compileDebugUnitTestKotlin','compileDebugSources','lintAnalyzeDebug','lintAnalyzeDebugUnitTest','lintDebug','verifyReleaseGateAttachment']:
  prefix='> Task :app:'+task;tasks[task]=next((line for line in log.splitlines() if line==prefix or line.startswith(prefix+' ')),'NOT_REACHED')
 corpus={}
 for name,count,raw in re.findall(r'CC001 (\w+) displays=(\d+) correctPositions=\{([^}]*)\}',stdout):
  corpus[name]=dict(displays=int(count),correctPositionCounts={k.strip():int(v) for k,v in (pair.split('=') for pair in raw.split(','))})
 if label=='FINAL':
  inventory=json.loads((work/'POSITION_SUMMARY.json').read_text())
  assert set(corpus)==set(inventory['routes'])
  for name,cases in corpus.items():
   assert cases['displays']==inventory['routes'][name]['displays']
   assert cases['correctPositionCounts']==inventory['routes'][name]['correctPositionCounts']
 result=dict(sourceCommit=c['sourceCommit'],exitCode=0,testCounts=counts,classes=len({x['testClass'] for x in rows}),tests=rows,tasks=tasks,
  lintIssuesBySeverity=lint,selectedSourceInventoryMatchesFreshXmlAndEvents=True,xmlFreshRelativeToRunScript=True,
  kotlinCorpus=corpus,corpusMatchesIndependentStaticInventory=label=='FINAL',completeSuite=False,scopesNotSummed=True,
  qualification=c.get('qualification','Authoritative latest selected host scope; no Activity recreation or physical-device proof.'))
 (o/'RESULT.json').write_text(json.dumps(result,indent=2)+'\n');results[label]=result
 shutil.copytree(o,dest/label)
 print(label,c['sourceCommit'],counts,lint,tasks)
for name in ['POSITION_INVENTORY.csv','POSITION_SUMMARY.json','position_inventory.py','PRESENTATION_POLICY.md','CONSERVATION.json','collect_results.py','JAVA_VERSION.txt','ENGINEERING_CHANGE_REPORT.md']:
 shutil.copy2(work/name,dest/name)
base='ea147fb87a15a78f30a83ad4ec86f1bf86bf6eaf';source=results['FINAL']['sourceCommit']
assert subprocess.check_output(['git','rev-parse',results['INITIAL']['sourceCommit']+':app/src/main'])==subprocess.check_output(['git','rev-parse',source+':app/src/main'])
(dest/'SOURCE_CHANGE.patch').write_bytes(subprocess.check_output(['git','diff','--binary',base,source,'--','app/src']))
manifest=dict(finding='CC001',engineeringDisposition='IMPLEMENTED_SCOPED_HOST_VERIFIED_PENDING_INDEPENDENT_RECHECK',
 sourceCommit=source,preChangeSource=base,authoritativeScope='FINAL/RESULT.json',scopeCounts={k:v['testCounts'] for k,v in results.items()},
 scopesMustNotBeSummed=True,productionTreeIdenticalAcrossBothRuns=True,
 auditRequiredScope=dict(total=429,rawCorrectFirst=379,displayCorrectFirst=198),legacyReviewPresentationException=True,
 existingLessonLabUpgrade='Older source did not persist presentation versions; upgrade adopts v1 display while preserving selected values and accepted credit. Subsequent resume order is deterministic.',
 all66RetainedArchiveHashesMatch=True,all64HistoricalLearnerDocumentsLegacyOrderChecked=True,
 curriculumCatalogDocsReviewExistingTestsUnchanged=True,completeSuite=False,
 apk=dict(configuredIdentity='dev.russiancompanion / 2.0.0-dev / versionCode5',assemblySigning='NOT_RUN',sha256Signature='NOT_RECORDED_NO_NEW_ARTIFACT'),
 environment=dict(changes='NONE_THIS_TASK',settings='PREVIOUSLY_SAVED',publication='USER_CONFIRMED_INDEPENDENTLY_UNVERIFIED'),
 independentAudit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST_AFTER_FINAL_INDEPENDENT_COMPREHENSIVE_AUDIT',releaseApproval=False)
(dest/'VERIFICATION_MANIFEST.json').write_text(json.dumps(manifest,indent=2)+'\n')
original=r/'audit/gpt61-sol-high-comprehensive-2026-10-19/finish-results/2026-10-19-0321d323'
with (dest/'FINDING_ENGINEERING_DISPOSITIONS.csv').open('w',newline='') as f:
 w=csv.writer(f,lineterminator='\n');w.writerow(['finding_id','original_audit_disposition','engineering_disposition','evidence','independent_recheck'])
 for row in csv.DictReader((original/'IMPLEMENTATION_WORK_ORDER.csv').open()):
  id=row['finding_id'];evidence={'CC001':'FINAL/RESULT.json','CC013':'../engineering-cc013/HARDENED/RESULT.json','CC014':'../engineering-cc014/ENGINEERING_CHANGE_REPORT.md'}.get(id,'')
  w.writerow([id,row['current_disposition'],'IMPLEMENTED_SCOPED_HOST_VERIFIED_PENDING_INDEPENDENT_RECHECK' if evidence else 'OUTSTANDING',evidence,'REQUIRED'])
with (dest/'EXECUTION_REQUEST_PROGRESS.csv').open('w',newline='') as f:
 w=csv.writer(f,lineterminator='\n');w.writerow(['request_row','requirement_id','scenario','engineering_status','evidence','remaining_limit'])
 for index,row in enumerate(csv.DictReader((original/'EXECUTION_EVIDENCE_REQUESTS.csv').open()),1):
  hit='CC001' in row['scenario']
  w.writerow([index,row['requirement_id'],row['scenario'],'PARTIAL_HOST_EVIDENCE_SUPPLIED' if hit else 'NOT_FULFILLED_BY_THIS_SCOPE',
   'FINAL/RESULT.json;POSITION_INVENTORY.csv;PRESENTATION_POLICY.md' if hit else '',
   'Host saved-state restoration only; actual Activity rotation/device and independent changed-source recheck outstanding. Legacy presentation explicitly preserved.' if hit else 'See prior CC013/CC014 reports where applicable; all other requests remain outstanding.'])
