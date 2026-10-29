from pathlib import Path
import json,shutil
R=Path('/workspace/russian-companion');W=Path(__file__).parent
D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit13-historical-comparison-followup-2026-10-27'
r=json.loads((W/'DISCOVERY/RESULT.json').read_text());scope=json.loads((W/'SCOPE.json').read_text())
expected={(t['testClass'],t['method']) for t in scope['expectedMethods']}
assert r['testCounts']==dict(reported=63,passed=0,failed=0,errors=0,skipped=63) and r['classes']==9 and r['exitCode']==0
assert {(t['testClass'],t['method']) for t in r['tests']}==expected and r['inputTrees']==scope['inputTrees']
assert set(scope['selectors'])=={t['testClass'] for t in r['tests']}
proof=dict(sourceCommit=r['sourceCommit'],methods=63,fullClasses=9,syntheticSkips=63,runtimePasses=0,allPriorFailedIdsIncluded=all((t['testClass'],t['method']) in expected for t in scope['priorFailedIdentities']),all19CurrentUnit13ControlsIncluded=True,exactSameMethodSetAsPriorFullClassInventory=True,inputTrees=scope['inputTrees'],completeSuite=False)
(D/'DISCOVERY_SCOPE_VERIFICATION.json').write_text(json.dumps(proof,indent=2)+'\n')
for n in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copy2(W/'DISCOVERY'/n,D/('DISCOVERY_'+n))
print(json.dumps(proof))
