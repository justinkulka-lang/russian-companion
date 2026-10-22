"""Read-only verification of source-pinned baseline/corrected evidence,not independent acceptance."""
from pathlib import Path
import argparse,json,hashlib,subprocess,zipfile,xml.etree.ElementTree as ET
p=argparse.ArgumentParser();p.add_argument('--repository',required=True);args=p.parse_args();R=Path(args.repository);D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-backup-recording-filenames-2026-10-22';sha=lambda b:hashlib.sha256(b).hexdigest();git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
for name in ['SHA256_PREPARATION.json','SHA256_BASELINE_AND_PREPARED_CORRECTION.json','SHA256_CURRENT.json']:
 for path,h in json.loads((D/name).read_text()).items():assert sha((D/path).read_bytes())==h,(name,path)
allresults={}
for mode,counts,classes in [('BASELINE',dict(reported=3,passed=1,failed=2,errors=0,skipped=0),1),('FINAL',dict(reported=37,passed=37,failed=0,errors=0,skipped=0),5)]:
 x=json.loads((D/mode/'RESULT.json').read_text());c=json.loads((D/mode/'CONTEXT.json').read_text());assert x['testCounts']==counts and x['classes']==classes;assert x['exitCode']==int((D/mode/'RUN.exit').read_text())==(1 if mode=='BASELINE' else 0);assert c['sourceCommit']==x['sourceCommit'];assert git('rev-parse',x['sourceCommit']+'^{tree}')==c['sourceRootTree']
 for path,h in c['inputTrees'].items():assert git('rev-parse',x['sourceCommit']+':'+path)==h
 idx=json.loads((D/('RAW_'+mode+'_INDEX.json')).read_text());assert sha((D/idx['archive']).read_bytes())==idx['sha256']
 with zipfile.ZipFile(D/idx['archive']) as z:
  assert z.testzip() is None and len(z.namelist())==len(set(z.namelist()))
  for v in idx['members']:assert sha(z.read(v['path']))==v['sha256']
  events=[json.loads(line) for line in z.read('EVENTS.jsonl').decode().splitlines()];rs=[v for v in events if v['kind']=='RESULT'];em={(v['className'],v['name']):v['resultType'] for v in rs};assert len(rs)==len(em)==counts['reported'];xml={};xmlnames=[n for n in z.namelist() if n.startswith('XML/') and n.endswith('.xml')];assert len(xmlnames)==classes
  for name in xmlnames:
   for t in ET.fromstring(z.read(name)).findall('testcase'):
    key=(t.get('classname'),t.get('name'));assert key not in xml;xml[key]='FAILURE' if t.find('failure') is not None or t.find('error') is not None else 'SKIPPED' if t.find('skipped') is not None else 'SUCCESS'
  outcome={'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'};assert xml==em=={(v['testClass'],v['method']):outcome[v['outcome']] for v in x['tests']};assert set(xml)=={tuple(s.rsplit('.',1)) for s in c['selectors']};assert not any(k[0].rsplit('.',1)[-1] in c['exclusions'] for k in xml)
 assert c['runnerSha256']==sha((D/'run.py').read_bytes())
 allresults[mode]=(x,c)
b,bc=allresults['BASELINE'];f,fc=allresults['FINAL'];assert bc['runnerSha256']==fc['runnerSha256'] and bc['initScriptSha256']==fc['initScriptSha256'];assert b['inputTrees']['app/src/test']==f['inputTrees']['app/src/test'];correction=json.loads((D/'CORRECTION.json').read_text());path=correction['productionPath'];before=subprocess.check_output(['git','show',b['sourceCommit']+':'+path],cwd=R);after=subprocess.check_output(['git','show',f['sourceCommit']+':'+path],cwd=R);assert sha(before)==correction['beforeSha256'] and sha(after)==correction['afterSha256'];assert git('diff','--name-only',b['sourceCommit'],f['sourceCommit'],'--','app/src/main')==path
lint=ET.parse(D/'FINAL/LINT.xml').getroot().findall('issue');assert {s:sum(v.get('severity')==s for v in lint) for s in ['Fatal','Error','Warning','Hint']}==f['lintIssuesBySeverity'];assert f['lintIssuesBySeverity']['Error']==f['lintIssuesBySeverity']['Fatal']==0;assert f['tasks']['verifyReleaseGateAttachment']=='> Task :app:verifyReleaseGateAttachment'
print(json.dumps(dict(baselineCounts=b['testCounts'],correctedCounts=f['testCounts'],baselineExit=1,correctedExit=0,xmlEventsSelectorsOutcomesExact=True,threeManifestsAndBothRawArchivesVerified=True,wholeTestTreeUnchangedAcrossCorrection=True,onlyProductionGuardChanged=True,currentHeadMatchesCorrectedInputTrees=all(git('rev-parse','HEAD:'+k)==v for k,v in fc['inputTrees'].items()),lint=f['lintIssuesBySeverity'],completeSuite=False,independentAudit='INCOMPLETE',original17Findings='UNCLOSED',physicalAcceptance='OUTSTANDING_LAST'),indent=2))
