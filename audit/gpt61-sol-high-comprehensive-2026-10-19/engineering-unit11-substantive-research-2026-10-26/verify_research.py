"""Read-only research reproduction and conservation; zero Android/test executions."""
from pathlib import Path
import subprocess,json,hashlib
D=Path(__file__).resolve().parent;R=next(p for p in D.parents if (p/'.git').exists());g=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip();sha=lambda b:hashlib.sha256(b).hexdigest();base='1d96cb4e89768f69246960e263d9b51412a3b9f4'
m=json.loads((D/'SHA256.json').read_text());assert set(m)=={p.relative_to(D).as_posix() for p in D.rglob('*') if p.is_file() and p.name!='SHA256.json'};tracked=set(g('ls-files').splitlines())
for n,h in m.items():assert sha((D/n).read_bytes())==h and (D/n).relative_to(R).as_posix() in tracked,n
v=json.loads(subprocess.check_output(['python3',str(D/'extract_research.py'),'--check'],cwd=R,text=True));assert v['verified'] and v['runtimeMethodsExecuted']==0
for n,h in json.loads((D/'PROTECTED_EVIDENCE_HASHES.json').read_text()).items():assert sha((R/n).read_bytes())==h,n
inp=json.loads((D/'INPUTS.json').read_text());assert inp['sourceRootTree']==g('rev-parse',base+'^{tree}');intro=g('log','--diff-filter=A','--format=%H','--',str(D.relative_to(R)/'extract_research.py')).splitlines();commit=intro[-1] if intro else g('rev-parse','HEAD')
for n,h in inp['inputTrees'].items():assert g('rev-parse',base+':'+n)==h==g('rev-parse',commit+':'+n)
s=json.loads((D/'SUMMARY.json').read_text());assert not s['activated'] and s['registeredPolicyObjectsPreserved']==37 and s['assessedChecks']==51 and s['historicalArchivesChecked']==71
print(json.dumps(dict(verified=True,sourceCommit=base,chains=15,inventoriedCheckRecords=51,archivesShaCrc=71,archivesContainingUnit11=24,proposal='NOT_ACTIVATED',newJUnitMethodsExecuted=0,productionTestsAnd155RecordsUnchanged=True,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST')))
