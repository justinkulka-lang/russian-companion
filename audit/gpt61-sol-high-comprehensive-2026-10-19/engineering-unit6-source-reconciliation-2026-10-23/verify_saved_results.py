#!/usr/bin/env python3
"""Read-only verification of saved source binding evidence and selected outcomes."""
from pathlib import Path
import hashlib,json,subprocess,zipfile,xml.etree.ElementTree as ET,collections
D=Path(__file__).resolve().parent
R=Path(subprocess.check_output(['git','rev-parse','--show-toplevel'],cwd=D,text=True).strip())
sha=lambda data:hashlib.sha256(data).hexdigest()
read=lambda n:json.loads((D/n).read_text())
for name,expected in read('SHA256_CURRENT.json').items():assert sha((D/name).read_bytes())==expected,name
for filename in ['SHA256_PREPARATION.json','SHA256_RUN_PREPARATION.json']:
 for name,expected in read(filename).items():assert sha((D/name).read_bytes())==expected,(filename,name)
proposal=read('PROPOSAL_REVIEW.json');ctx=read('GENERATION_PREPARATION.json')
assert proposal['proposedFiles']==155 and proposal['changedFiles']==95 and proposal['unchangedFiles']==60
assert proposal['twoFreshJvmRendersIdentical'] and proposal['everyOtherByteExact']
renders=read('RENDER_OUTPUT_SHA256.json');assert renders['CANDIDATE_1']==renders['CANDIDATE_2'] and len(renders['CANDIDATE_1'])==153
assert len(read('GENERATION_RESULTS.json'))==3 and all(x['exitCode']==0 for x in read('GENERATION_RESULTS.json'))
with zipfile.ZipFile(D/'HISTORICAL_PRIOR_CURRENT.zip') as old:
 assert old.testzip() is None and len(old.namelist())==95
 assert set(old.namelist())=={r['path'] for r in proposal['files'] if r['changed']}
 for row in proposal['files']:
  after=(R/row['path']).read_bytes();assert sha(after)==row['newSha256'],row['path']
  before=old.read(row['path']) if row['changed'] else after
  assert sha(before)==row['oldSha256']
  expected=before
  for t in proposal['identityTokenReplacements']:expected=expected.replace(t['before'].encode(),t['after'].encode())
  assert after==expected,row['path']
  if row['rendered']:assert sha(after)==renders['CANDIDATE_1'][row['path']]
  if row['path'].endswith('_WORKSHEET.tsv'):assert not row['changed']
assert len(proposal['identityTokenReplacements'])==5
candidate=json.loads((R/'release/acceptance/candidate.json').read_text())
assert candidate['sourceDigest']==ctx['newDigest'] and candidate['frozen'] is False
assert candidate['sourceCommit'] is None and candidate['publicRelease']=='NOT_APPROVED'
manifest=json.loads((R/'review/unit-6/UNIT_6_REVIEW_MANIFEST.json').read_text())
assert manifest['curriculumSourceCommit']==ctx['actualCurriculumChangeCommit']=='53d9c292e1f32f204bdff2b09b8f10d02b6b28a6'
for basename,indexname in [('RAW_GENERATION.zip','RAW_GENERATION_INDEX.json'),('RAW_RUNTIME.zip','RAW_RUNTIME_INDEX.json')]:
 with zipfile.ZipFile(D/basename) as z:
  assert z.testzip() is None;index=read(indexname);assert sorted(z.namelist())==sorted(r['path'] for r in index)
  for row in index:
   data=z.read(row['path']);assert len(data)==row['bytes'] and sha(data)==row['sha256']
  if basename=='RAW_GENERATION.zip':
   for name in ['CANDIDATE_1.log','CANDIDATE_2.log']:
    text=z.read(name).decode();assert ('CANONICAL_SOURCE_DIGEST '+ctx['newDigest']+' REAL_ROOT_MATCH '+ctx['newDigest']) in text
    assert sum(line.startswith('VALIDATED ') for line in text.splitlines())==30
  else:
   for mode in ['DISCOVERY','REGRESSION']:
    result=read(mode+'/RESULT.json');context=read(mode+'/CONTEXT.json')
    assert result==json.loads(z.read(mode+'/RESULT.json')) and context==json.loads(z.read(mode+'/CONTEXT.json'))
    dry=mode=='DISCOVERY';assert result['exitCode']==0 and result['classes']==17
    assert result['testCounts']==dict(reported=85,passed=0 if dry else 85,failed=0,errors=0,skipped=85 if dry else 0)
    assert result['sourceCommit']==context['sourceCommit']=='cae0d574202b0713e8b926ee0ac0b87dbbf30281'
    assert context['exclusions']==dict(VisualReviewCaptureTest=2,Units35ChunkCaptureTest=2,Units610ChunkCaptureTest=10,Units1115GapCaptureTest=2)
    for path,expected in json.loads(z.read(mode+'/SHA256.json')).items():assert sha(z.read(mode+'/'+path))==expected
    for path,expected in context['initScriptSha256'].items():
     base=Path(path).name;data=(D/base).read_bytes() if base=='resources.gradle' else z.read('INIT/'+base);assert sha(data)==expected
    names=[]
    for name in z.namelist():
     if name.startswith(mode+'/XML/') and name.endswith('.xml'):
      suite=ET.fromstring(z.read(name))
      for case in suite.findall('testcase'):
       assert case.find('failure') is None and case.find('error') is None
       assert (case.find('skipped') is not None)==dry
       names.append((case.get('classname'),case.get('name')))
    events=[json.loads(x) for x in z.read(mode+'/EVENTS.jsonl').decode().splitlines()];events=[e for e in events if e['kind']=='RESULT']
    assert len(names)==len(set(names))==85 and sorted(names)==sorted((e['className'],e['name']) for e in events)
    assert all(e['resultType']==('SKIPPED' if dry else 'SUCCESS') for e in events)
    if not dry:
     assert result['discoveryComparison']['sameInputTrees'] and result['discoveryComparison']['exactSameMethodSet']
     issues=ET.fromstring(z.read(mode+'/LINT.xml')).findall('issue')
     assert {s:sum(x.get('severity')==s for x in issues) for s in ['Fatal','Error','Warning','Hint']}==dict(Fatal=0,Error=0,Warning=20,Hint=1)
     assert result['tasks']['verifyReleaseGateAttachment']=='> Task :app:verifyReleaseGateAttachment'
runtime,discovery=read('REGRESSION/RESULT.json'),read('DISCOVERY/RESULT.json');assert runtime['inputTrees']==discovery['inputTrees']
cases=[json.loads(x) for x in (D/'HISTORY_CASES.jsonl').read_text().splitlines()]
assert collections.Counter(c['kind'] for c in cases)==dict(original_archive=64,legacy_unit5_to_current_unit6=1,legacy_unit5_to_current_unit6_v3=1)
assert collections.Counter(tuple(c['currentCompletedUnits']) for c in cases if c['kind']=='original_archive')=={(1,2):24,(1,2,3):40}
assert read('HISTORY_CASE_SUMMARY.json')['caseObservations']==66
for row in read('ORIGINAL_ARCHIVE_CONSERVATION.json'):
 assert sha((R/row['path']).read_bytes())==row['sha256']
 with zipfile.ZipFile(R/row['path']) as z:assert z.testzip() is None
for name,expected in read('PRESERVED_PRECEDING_SCOPES.json').items():assert sha((R/name).read_bytes())==expected,name
baseline='app/src/test/java/dev/russiancompanion/v2/Unit6PresentReviewTest.kt';assert sha((R/baseline).read_bytes())==read('CONSERVATION.json')['baselineWholeTestFileSHA256']
assert read('ORIGINAL_ASSERTION_CONSERVATION.json')['allOriginalAssertionExpressionsRetained']
assert read('ORIGINAL_ASSERTION_CONSERVATION.json')['original64ArchiveMethodUnchanged']
for i,item in enumerate(ctx['compiledClassDirectories'],1):assert sha((D/('COMPILED_INDEX_'+str(i)+'.json')).read_bytes())==item['indexSha256']
print(json.dumps(dict(source=runtime['sourceCommit'],reported=85,passed=85,fullClasses=17,caseObservationsNotTests=66,originalArchivesConserved=66,changedVersionsArchived=95,unchangedWorksheets=60,sourceBindings='VERIFIED_SELECTED_SCOPE',completeSuite=False,newRuntimeTestsExecutedByVerifier=0)))
