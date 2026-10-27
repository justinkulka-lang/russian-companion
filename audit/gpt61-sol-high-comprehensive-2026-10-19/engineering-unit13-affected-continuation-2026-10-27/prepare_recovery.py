from pathlib import Path
from datetime import datetime, timezone
from collections import Counter
import hashlib, json, os, subprocess, zipfile

R = Path('/workspace/russian-companion')
W = Path(__file__).parent
OLD = Path('/workspace/artifacts/unit13-affected-integration-2026-10-27')
D = R / 'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit13-affected-continuation-2026-10-27'
git = lambda *a: subprocess.check_output(['git', *a], cwd=R, text=True).strip()
sha = lambda b: hashlib.sha256(b).hexdigest()
save = lambda p, v: p.write_text(json.dumps(v, indent=2) + '\n')
assert not git('status', '--porcelain')
S = git('rev-parse', 'HEAD')
assert S == '75e2d31c441e87e7f53c550b2bf34e9804737bfb'
assert not (OLD / 'REGRESSION_COMPLETION.json').exists()
assert not (OLD / 'AUTO_RESULT.json').exists()
assert not (OLD / 'REGRESSION/RUN.exit').exists()
pids = [549058,549061,549091,549123,549124,552850,552929]
assert all(not Path('/proc', str(p)).exists() for p in pids)
for p in Path('/proc').iterdir():
    if not p.name.isdigit(): continue
    try: cmd = (p/'cmdline').read_bytes().replace(b'\0',b' ').decode(errors='replace')
    except OSError: continue
    assert not ('Gradle Test Executor' in cmd and 'java' in cmd), (p.name, cmd[:150])
context = json.loads((OLD/'REGRESSION/CONTEXT.json').read_text())
assert context['sourceCommit'] == S
assert all(git('rev-parse', 'HEAD:'+p) == h for p,h in context['inputTrees'].items())
events = [json.loads(x) for x in (OLD/'REGRESSION/EVENTS.jsonl').read_text().splitlines()]
results = [e for e in events if e.get('kind') == 'RESULT']
keys = [(e['className'], e['name']) for e in results]
assert len(keys) == len(set(keys)) == 316
discovery = json.loads((OLD/'DISCOVERY/RESULT.json').read_text())
expected = {(t['testClass'],t['method']) for t in discovery['tests']}
assert len(expected) == 481 and set(keys) <= expected
remaining = sorted(expected - set(keys))
assert len(remaining) == 165
active = {}
for e in events:
    k=(e.get('className'),e.get('name'))
    if e.get('kind') == 'START': active[k]=e['atMillis']
    elif e.get('kind') == 'RESULT': active.pop(k,None)
assert set(active) <= set(remaining)
D.mkdir()
files = {'ORIGINAL_WORKSPACE/'+p.relative_to(OLD).as_posix():p for p in OLD.rglob('*') if p.is_file()}
B=R/'app/build/test-results/testDebugUnitTest'
files.update({'BUILD_RESULTS_OBSERVED_NOT_FINAL/'+p.relative_to(B).as_posix():p for p in B.rglob('*') if p.is_file()})
index={}
with zipfile.ZipFile(D/'INTERRUPTED_RAW.zip','x',zipfile.ZIP_DEFLATED) as z:
    for n,p in sorted(files.items()):
        assert not n.lower().endswith('.pdf')
        data=p.read_bytes();z.writestr(n,data);index[n]=dict(bytes=len(data),sha256=sha(data))
save(D/'INTERRUPTED_RAW_INDEX.json',index)
with zipfile.ZipFile(D/'INTERRUPTED_RAW.zip') as z:
    assert z.testzip() is None and set(z.namelist())==set(index)
    for n,h in index.items(): assert sha(z.read(n))==h['sha256']
counts=dict(Counter(e['resultType'] for e in results))
record=dict(observedUtc=datetime.now(timezone.utc).isoformat(),runtimeSource=S,inputTrees=context['inputTrees'],originalExpectedMethods=481,originalExpectedClasses=83,completedPartialEventCount=316,partialEventCounts=counts,activeWithoutResult=[dict(testClass=k[0],method=k[1],startedMillis=v) for k,v in active.items()],missingOriginalProcesses=pids,underlyingExit=None,finalResult=None,stopCause='UNCONFIRMED; exec-server transport disconnected, subsequently all original processes absent',lastResultUtc=datetime.fromtimestamp(max(e['atMillis'] for e in results)/1000,timezone.utc).isoformat(),freshFinalXml='NOT_AVAILABLE; observed build result files are preserved but not accepted as final runtime results',originalCompletionWorker='DEAD_WITHOUT_RESULT; historical lock preserved, not reused',continuationMethods=165,continuationClasses=len({k[0] for k in remaining}),originalFailureCount=counts['FAILURE'],originalFailuresNotWaived=True,completeSuite=False,countsNotSummed=True)
save(D/'INTERRUPTION.json',record)
save(D/'PARTIAL_RESULTS.json',dict(sourceCommit=S,kind='INCOMPLETE_RUNTIME_EVENTS_NOT_FINAL_JUNIT_RESULT',tests=results))
scope=dict(originalRuntimeSource=S,inputTrees=context['inputTrees'],expectedMethods=[dict(testClass=c,method=m) for c,m in remaining],selectors=[c+'.'+m for c,m in remaining],classes=len({k[0] for k in remaining}),methodCount=len(remaining),originalScope=481,partialCompleted=316,noRepeatedCompletedIds=True,includesEveryUnreportedAndBothInterruptedMethods=True,completeSuite=False)
save(W/'SCOPE.json',scope);save(D/'SCOPE.json',scope)
save(D/'INTERRUPTED_FAILURES.json',dict(sourceCommit=S,failures=[e for e in results if e['resultType']=='FAILURE'],count=6,assertionsNotWaived=True))
research=Path('/workspace/artifacts/unit13-affected-failure-research-2026-10-27/OBSERVED_FAILURE_COMPARISONS.json')
if research.exists():
    v=json.loads(research.read_text());v['status']='READ_ONLY_DIAGNOSIS_OF_PRESERVED_INTERRUPTED_EVENTS';save(D/'FAILURE_COMPARISONS.json',v)
prep=json.loads((R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit13-affected-integration-2026-10-27/PREPARATION.json').read_text())
save(D/'CONSERVATION_INPUTS.json',prep)
for n,h in prep['currentRecordHashes'].items(): assert sha((R/n).read_bytes())==h
for n,h in prep['archives'].items():
    assert sha((R/n).read_bytes())==h
    with zipfile.ZipFile(R/n) as z: assert z.testzip() is None
assert len(prep['currentRecordHashes'])==155 and len(prep['archives'])==74
status='''# Interrupted Unit13 affected integration; exact continuation prepared

The original runtime at `75e2d31c441e87e7f53c550b2bf34e9804737bfb` did not finish. Raw events record **316 completed methods: 309 SUCCESS, 6 FAILURE, 1 SKIPPED**, from a 481-method / 83-class selected scope. No underlying exit, final runtime XML reconciliation, final result or automatic checkpoint exists. The exec-server disconnected; on reconnection all original runner/worker/monitor/completion processes were absent. Stop cause remains UNCONFIRMED. The last event result is 2026-10-27 14:09:45 America/Chicago (raw UTC retained). The prior monitor's 315-method count lagged this final event.

INTERRUPTED_RAW.zip preserves the original workspace, context, commands, original completion lock/process metadata, discovery, partial runtime events/logs/GC, and observed build result files. The observed build XML is not accepted as final runtime evidence. Every archive member has a recorded SHA256; ZIP CRC and member identities verified. No private PDF included.

The separate continuation selects exactly all **165 unreported identities**, including the two methods that had START but no RESULT. It repeats zero already-reported methods. Its classes and method identities are in SCOPE.json. Fresh discovery/runtime is required; no continuation passes claimed here. Application/test/catalog/docs/review/release trees must match the original runtime. This is an exact-method continuation, not a full-class run. The first run's six failures remain OPEN and retained in full; two whole-unit failures differ only by the old/new Unit13 policy ID. Do not combine counts into a single completed 481-method run or claim a complete suite. After continuation, diagnose any further failures and narrowly reconcile historical comparisons, retain every original assertion and current control, then execute full-class follow-up before a fresh broad run.

All 155 current bindings and 74 historical archives are conserved. Repository source digest `3c0acbb95297cf10f08da2bd8391463067eece22fec785158b5d2680d37dc2ba` differs from curriculum pack digest `15c6ae393767cb7ffc190882867bfad923c9e81b07d0f4285cf0a678f2489280`. APK identity/signature absent; no assembly/install/signing. Lint and gate attachment were not reached in the interrupted run; prior results are historical. Four exporters / 16 methods excluded: VisualReviewCaptureTest (2), Units35ChunkCaptureTest (2), Units610ChunkCaptureTest (10), Units1115GapCaptureTest (2). Provisional SQLite JIT compile directive unchanged/unadopted; native cause UNCONFIRMED, no tests excluded by it.

All 48 independent findings OPEN; audit INCOMPLETE. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance LAST after final independent comprehensive nonphysical audit. No merge/freeze/formal advancement/release approval. One agent, current checkout, Draft PR64. Environment unchanged; publication user-confirmed, independently unobserved. The old worker is dead; do not reuse its lock or invent a completion record. Inspect the separate continuation's context/process/completion/AUTO_RESULT or ERROR before launching or editing inputs.
'''
(D/'STATUS.md').write_text(status)
rel=D.relative_to(R).as_posix()
for name in ['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']:
    p=R/name;link=rel+'/STATUS.md' if name=='AGENTS.md' else '../'+rel.removeprefix('audit/')+'/STATUS.md'
    p.write_text(f'Current checkpoint: [Unit13 interrupted run preserved; exact 165-method continuation prepared]({link}). Original75e2d31c:316partial events/309SUCCESS/6FAILURE/1SKIPPED,no exit/final runtime result;all original processes absent/stopcauseUNCONFIRMED. Original481scope retained;165unreported including2interrupted methods selected,none rerun yet. Inspect external unit13-affected-continuation-2026-10-27 before launch/edit. Six failures OPEN,scopes separate/unsummed;155bindings/74archives conserved. All48OPEN/auditINCOMPLETE/four exporters excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
print(json.dumps(record,indent=2))
