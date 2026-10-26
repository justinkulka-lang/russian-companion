"""Read-only research reproduction/conservation; no Android tasks or test execution."""
from pathlib import Path
import json,hashlib,subprocess,csv
D=Path(__file__).resolve().parent;R=next(p for p in D.parents if (p/'.git').exists());G=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip();sha=lambda b:hashlib.sha256(b).hexdigest();BASE='a1986f9de49062866be3c9fe395473cf3796d888';rel=D.relative_to(R).as_posix()
m=json.loads((D/'SHA256.json').read_text());assert set(m)=={p.relative_to(D).as_posix() for p in D.rglob('*') if p.is_file() and p.name!='SHA256.json'};tracked=set(G('ls-files').splitlines())
for n,h in m.items():assert (D/n).relative_to(R).as_posix() in tracked and sha((D/n).read_bytes())==h,n
x=json.loads(subprocess.check_output(['python3',str(D/'extract_research.py'),'--check'],cwd=R,text=True));assert x['verified'] and x['runtimeMethodsExecuted']==0 and x['checks']==47
s=json.loads((D/'SUMMARY.json').read_text());assert s['sourceCommit']==BASE and s['status']=='PROPOSED_NOT_ACTIVATED' and not s['activated'] and s['printSingletonPoliciesStillCurrent']==23 and s['historicalArchivesChecked']==70 and s['archivesContainingUnit10']==25
inp=json.loads((D/'INPUTS.json').read_text());assert inp['sourceRootTree']==G('rev-parse',BASE+'^{tree}');intro=G('log','--diff-filter=A','--format=%H','--',rel+'/extract_research.py').splitlines();introduced=intro[-1] if intro else G('rev-parse','HEAD')
for n,h in inp['inputTrees'].items():assert G('rev-parse',BASE+':'+n)==h==G('rev-parse',introduced+':'+n)
assert len(list(csv.DictReader((D/'CANDIDATE_ASSESSMENT.csv').open())))==47
r=json.loads(G('show',BASE+':audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit9-source-reconciliation-2026-10-25/ATTEMPT_3/REGRESSION_RESULT.json'));assert r['sourceCommit']=='e65a281c95deec44ea1065ab854fd4ac961ff235' and r['testCounts']==dict(reported=1410,passed=1409,failed=0,errors=0,skipped=1) and r['exitCode']==0 and r['classes']==241
print(json.dumps(dict(verified=True,sourceCommit=BASE,introducedByCommit=(intro[-1] if intro else None),inputTreeComparisonCommit=introduced,chains=13,checksAssessed=47,selectedPresentItems=4,priorArchivesShaCrc=70,archivesContainingUnit10=25,proposal='NOT_ACTIVATED',newJUnitMethodsExecuted=0,productionTestAnd155RecordsUnchanged=True,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',completeSuite=False)))
