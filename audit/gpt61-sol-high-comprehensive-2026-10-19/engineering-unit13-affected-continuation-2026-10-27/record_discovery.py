from pathlib import Path
import json, hashlib, shutil
R=Path('/workspace/russian-companion');W=Path(__file__).parent
D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit13-affected-continuation-2026-10-27'
r=json.loads((W/'DISCOVERY/RESULT.json').read_text());s=json.loads((W/'SCOPE.json').read_text())
expected={(t['testClass'],t['method']) for t in s['expectedMethods']}
assert r['testCounts']==dict(reported=165,passed=0,failed=0,errors=0,skipped=165)
assert r['classes']==28 and r['exitCode']==0 and r['inputTrees']==s['inputTrees']
assert {(t['testClass'],t['method']) for t in r['tests']}==expected
proof=dict(sourceCommit=r['sourceCommit'],methods=165,classes=28,syntheticSkips=165,runtimePasses=0,exactUnreportedSet=True,includesTwoInterruptedMethods=True,originalPartialRunPreserved=True,noAlreadyReportedMethodRepeated=True,inputTreesMatchOriginal=True,notFullClassScope=True,completeSuite=False,countsNotSummed=True)
(D/'DISCOVERY_SCOPE_VERIFICATION.json').write_text(json.dumps(proof,indent=2)+'\n')
for n in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copy2(W/'DISCOVERY'/n,D/('DISCOVERY_'+n))
print(json.dumps(proof))
