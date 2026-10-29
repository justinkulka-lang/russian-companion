"""Reproduce pinned research and optionally verify the current checkpoint's conservation."""
from pathlib import Path
import hashlib,json,re,subprocess,sys,zipfile
D=Path(__file__).resolve().parent;R=next(p for p in D.parents if (p/'.git').exists());BASE='2926452c190bac07b1363788ae555322f9b22eae'
git=lambda *a:subprocess.check_output(['git',*a],cwd=R)
sha=lambda b:hashlib.sha256(b).hexdigest()
assert sys.argv[1:] in [[],['--current']]
for name in ['extract_research.py','extract_source_bindings.py']:
    out=subprocess.check_output(['python3','-B',str(D/name),'--check'],cwd=R,text=True);assert json.loads(out)['verified']
summary=json.loads((D/'SUMMARY.json').read_text());inputs=json.loads((D/'INPUTS.json').read_text());inventory=json.loads((D/'CURRENT_POLICY_INVENTORY.json').read_text());protected=json.loads((D/'PROTECTED_EVIDENCE_HASHES.json').read_text())
assert summary['proposalStatus']=='NOT_ACTIVATED' and summary['newJUnitMethodsExecuted']==0 and summary['registeredPolicyObjectsPreserved']==40 and summary['schemasPreserved']==list(range(1,16))
assert summary['objectiveChains']==72 and summary['inventoriedCheckRecords']==226 and summary['candidateItems']==11 and summary['assignedSlots']==3
assert summary['projectedCountsNotActivated']==dict(activePolicies=28,objectivePools=60,candidatePlacements=121,uniqueCandidateItems=57,printSingletonPolicies=18)
for n,h in json.loads((D/'GENERATED_SHA256.json').read_text()).items():assert sha((D/n).read_bytes())==h,n
for n,h in protected.items():assert sha(git('show',BASE+':'+n))==h,n
history=json.loads((D/'HISTORICAL_UNIT14_UNIT15_INVENTORY.json').read_text());assert history['archivesChecked']==74 and len(history['unit14Fixtures'])==3 and not history['unit15Fixtures']
for archive in history['archives']:
    assert archive['crcVerified']
    if sys.argv[1:]==['--current']:
        with zipfile.ZipFile(R/archive['path']) as z:assert z.testzip() is None
for n,h in json.loads((D/'SHA256.json').read_text()).items():assert sha((D/n).read_bytes())==h,n
if sys.argv[1:]==['--current']:
    for n,h in protected.items():assert sha((R/n).read_bytes())==h,n
    for n,h in inputs['inputTrees'].items():assert git('rev-parse','HEAD:'+n).decode().strip()==h,n
    allowed={'AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md'};rel=D.relative_to(R).as_posix()
    committed=git('diff','--name-only',BASE,'HEAD').decode().splitlines()
    assert all(n in allowed or n.startswith(rel+'/') for n in committed),committed
    for row in git('status','--porcelain=v1','-z','--untracked-files=all').split(b'\0'):
        if not row:continue
        assert row[2:3]==b' ' and not any(k in row[:2] for k in [b'R',b'C']),row
        n=row[3:].decode();assert n in allowed or n.startswith(rel+'/'),n
    for n in allowed:assert (R/n).read_bytes().endswith(git('show',BASE+':'+n)),n
print(json.dumps(dict(verified=True,researchSource=BASE,currentConservationVerified=sys.argv[1:]==['--current'],objectiveChains=72,checkInventory=226,selectedCandidates=11,plannedDueAnswers=3,archivesShaCrc=74,historicalUnit14ArchiveRecords=3,historicalUnit15ArchiveRecords=0,registeredObjectsPreserved=40,existingSchemasPreserved=15,bindingsConserved=155,testSourceFilesConserved=276,newJUnitMethodsExecuted=0,proposal='NOT_ACTIVATED',all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST')))
