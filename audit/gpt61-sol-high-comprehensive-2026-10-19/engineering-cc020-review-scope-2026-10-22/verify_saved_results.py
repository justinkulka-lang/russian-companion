"""Read-only verification of the scoped CC020 investigation, not finding closure."""
from pathlib import Path,PurePosixPath
from datetime import datetime,timezone
import argparse,json,hashlib,subprocess,zipfile,csv,xml.etree.ElementTree as ET
p=argparse.ArgumentParser();p.add_argument('--repository',type=Path,required=True);R=p.parse_args().repository;D=Path(__file__).resolve().parent;read=lambda p:json.loads(p.read_text());sha=lambda b:hashlib.sha256(b).hexdigest();git=lambda *a:subprocess.check_output(['git',*a],cwd=R)
def check(v,m):
 if not v:raise ValueError(m)
manifest=read(D/'SHA256_CURRENT.json');check(set(manifest)=={p.relative_to(D).as_posix() for p in D.rglob('*') if p.is_file() and p.name!='SHA256_CURRENT.json'},'manifest completeness')
for n,h in manifest.items():check(sha((D/n).read_bytes())==h,'payload '+n)
def archive(mode):
 idx=read(D/('RAW_'+mode+'_INDEX.json'));z=zipfile.ZipFile(D/('RAW_'+mode+'.zip'));check(z.testzip() is None and len(z.namelist())==len(set(z.namelist()))==len(idx) and set(z.namelist())==set(idx),'raw inventory/CRC '+mode)
 for n,v in idx.items():
  rel=PurePosixPath(n);check(not rel.is_absolute() and '..' not in rel.parts,'archive path');b=z.read(n);check(sha(b)==v['sha256'] and len(b)==v['bytes'],'archive byte identity')
 return z
runtime=read(D/'REGRESSION/RESULT.json');discovery=read(D/'DISCOVERY/RESULT.json');context=read(D/'REGRESSION/CONTEXT.json');source=runtime['sourceCommit'];check(source==context['sourceCommit']==discovery['sourceCommit'],'one pinned source');check(runtime['inputTrees']==discovery['inputTrees']==context['inputTrees'],'same inputs');check(runtime['testCounts']==dict(reported=13,passed=13,failed=0,errors=0,skipped=0) and runtime['exitCode']==0,'actual scope');check(discovery['testCounts']==dict(reported=13,passed=0,failed=0,errors=0,skipped=13),'dry scope zero passes')
for mode in ['DISCOVERY','REGRESSION']:
 with archive(mode) as z:
  rr=read(D/mode/'RESULT.json');cc=read(D/mode/'CONTEXT.json');check(int(z.read('RUN.exit'))==rr['exitCode']==int((D/mode/'RUN.exit').read_text()),'exit');events=[json.loads(s) for s in z.read('EVENTS.jsonl').decode().splitlines()];done=[e for e in events if e['kind']=='RESULT'];ev={(e['className'],e['name']):e['resultType'] for e in done};check(len(done)==len(ev)==13,'events');rows={};xmls=[n for n in z.namelist() if n.startswith('XML/') and n.endswith('.xml')];check(len(xmls)==6,'partial class count')
  for n in xmls:
   suite=ET.fromstring(z.read(n));stamp=datetime.fromisoformat(suite.get('timestamp').replace('Z','+00:00'));stamp=stamp.replace(tzinfo=timezone.utc) if stamp.tzinfo is None else stamp;check(stamp.timestamp()>=datetime.fromisoformat(cc['startedUtc']).timestamp()-1,'fresh XML')
   for t in suite.findall('testcase'):
    key=(t.get('classname'),t.get('name'));check(key not in rows,'unique testcase');rows[key]='FAILURE' if t.find('failure') is not None or t.find('error') is not None else 'SKIPPED' if t.find('skipped') is not None else 'SUCCESS'
  expected={(v['testClass'],v['method']):{'PASSED':'SUCCESS','FAILED':'FAILURE','SKIPPED':'SKIPPED','ERROR':'FAILURE'}[v['outcome']] for v in rr['tests']};check(rows==ev==expected,'exact XML/event/outcome');check({a+'.'+b for a,b in rows}==set(cc['selectors']),'exact method selectors');check(set(rows)=={(v['testClass'],v['method']) for v in discovery['tests']},'same discovered methods');check(not any(a.split('.')[-1] in cc['exclusions'] for a,b in rows),'exporter exclusions')
  if mode=='REGRESSION':
   issues=ET.fromstring(z.read('LINT.xml')).findall('issue');check(rr['lintIssuesBySeverity']=={s:sum(i.get('severity')==s for i in issues) for s in ['Fatal','Error','Warning','Hint']},'lint');log=z.read('RUN.log').decode();check('BUILD SUCCESSFUL' in log,'build');check('> Task :app:lintDebug' in log.splitlines() and '> Task :app:verifyReleaseGateAttachment' in log.splitlines(),'executed lint/gate attachment')
with archive('DIAGNOSTIC') as z:check(z.read('EXPORT.log').decode().count('POLICY\t')==28,'canonical exporter denominator')
facts=read(D/'INVENTORY_FACTS.json');raw=git('show',source+':app/src/main/assets/content/v2-curriculum.json');check(sha(raw)==facts['rawCurriculumSha256'],'raw curriculum pin');check(facts['policyCount']==28 and facts['pools']==59 and facts['candidatePlacements']==75 and facts['uniqueCandidateItems']==44,'denominators')
for name,count in [('POLICY_INVENTORY.csv',28),('POOL_BINDINGS.csv',59),('CANDIDATE_ITEM_BINDINGS.csv',75),('UNIQUE_CANDIDATE_ASSESSMENT.csv',44),('CANDIDATE_ENGINEERING_READING.csv',44),('OBJECTIVE_BINDINGS.csv',59),('AUDIT_CLAIM_RECONCILIATION.csv',8),('REQUIREMENT_RECONCILIATION.csv',8),('SCHEMA_COMPATIBILITY.csv',6)]:
 with (D/name).open(newline='') as f:rows=list(csv.DictReader(f));check(len(rows)==count,'table denominator '+name)
for path in ['app/src/main/assets/content/v2-curriculum.json','app/src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt']:check(git('show',source+':'+path)==git('show',facts['externalAuditTarget']+':'+path),'same evidence at external target')
fixture=read(D/'FIXTURE_SCOPE_RECONCILIATION.json');check(fixture['countsByFixtureScope']=={'CURRENT_PACK':dict(reported=8,passed=8),'HISTORICAL_PRE_TEACHING_CORRECTION_PACK':dict(reported=5,passed=5)},'fixture scopes');check(len(fixture['selectedMethods'])==13,'selected method map')
for path,h in read(D/'PRESERVED_PRECEDING_SCOPES.json').items():check(sha((R/path).read_bytes())==h,'preceding scope retained')
for path,h in read(D/'CONSERVATION.json')['original66ArchiveHashes'].items():
 check(sha((R/path).read_bytes())==h,'original archive hash')
 with zipfile.ZipFile(R/path) as z:check(z.testzip() is None,'original archive CRC')
for path,tree in context['inputTrees'].items():check(git('rev-parse',source+':'+path).decode().strip()==tree,'historical input tree')
finding=read(D/'FINDING_DISPOSITION.json');check(sha((R/finding['originalFile']).read_bytes())==finding['originalFileSha256'] and finding['independentClosure'] is False,'original finding and open disposition')
print(json.dumps(dict(status='CC020_SCOPED_EVIDENCE_VERIFIED_FINDING_OPEN',source=source,policies=28,pools=59,candidatePlacements=75,uniqueCandidates=44,executedMethods=13,currentPackMethodsPassed=8,historicalPackMethodsPassed=5,selectedPartialClasses=6,exactXmlEventsAndSelectors=True,priorEvidenceAnd66ArchivesConserved=True,currentHeadMatchesInputTrees=all(git('rev-parse','HEAD:'+p).decode().strip()==v for p,v in context['inputTrees'].items()),productionOrPolicyChanges=False,all28PoliciesExecuted=False,completeSuite=False,findingsClosed=0,physicalAcceptance='OUTSTANDING_LAST'),indent=2))
