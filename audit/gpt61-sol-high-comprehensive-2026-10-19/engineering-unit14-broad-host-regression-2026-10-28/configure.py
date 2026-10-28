from pathlib import Path
import shutil
W=Path(__file__).parent;R=Path('/workspace/russian-companion')
B=R/'audit/gpt61-sol-high-comprehensive-2026-10-19'
P=B/'engineering-unit14-historical-comparison-followup-2026-10-28'
OLD=B/'engineering-unit13-broad-host-regression-2026-10-27'
D=B/'engineering-unit14-broad-host-regression-2026-10-28'
for n in ['run.py','resources.gradle','launch.py','supervise.py','start_runtime.py','durable_monitor.py','evidence.py','collect.py','verify_results.py']:
    text=((OLD if n=='run.py' else P)/n).read_text()
    text=text.replace('engineering-unit14-historical-comparison-followup-2026-10-28','engineering-unit14-broad-host-regression-2026-10-28')
    if n=='run.py':
        text=text.replace('Unit13 broad selected-host debug unit-test task,NO method/class filters;all prior1458 identities plus19Unit13 methods predicted until fresh discovery;includes all five historical comparison corrections and19current controls;four exporters16methods excluded;provisional JIT directive excludes compilation only;not complete app/curriculum/device acceptance.','Unit14 broad selected-host debug unit-test task;NO method/class filters;predicted1499methods/261classes=1477prior+22Unit14/no removals;all79follow-up/541affected IDs included;four exporters16methods excluded;not complete app/curriculum/device acceptance.')
    if n=='evidence.py':
        a=text.index('    from assertion_calls import assertions');b=text.index("    for n,h in prep['archives'].items():",a)
        text=text[:a]+'''    prior=json.loads(subprocess.check_output(['python3','-B',str(R/prep['followupEvidenceDirectory']/'verify_results.py')],cwd=R,text=True))
    assert prior['verified'] and prior['counts']==dict(reported=79,passed=79,failed=0,errors=0,skipped=0)
'''+text[b:]
        text=text.replace(",'assertion_calls.py'",'')
    if n=='collect.py':
        text=text.replace('RAW_AFFECTED','RAW_BROAD').replace("==scope['classes']==13","==scope['classes']==261").replace('classes=13','classes=261')
        text+='''
for label,key in [('CURRENT_FOLLOWUP_RECHECKED','currentFollowupMethods'),('UNIT14_PRIOR_FAILURES_RECHECKED','originalUnit14FailedIdentities')]:
    methods=[dict(**x,outcome=by[x['testClass'],x['method']]) for x in scope[key]]
    save(D/(label+'.json'),dict(sourceCommit=S,methods=methods,allIncluded=True,allPassed=all(t['outcome']=='PASSED' for t in methods),scopesNotSummed=True))
save(D/'PRIOR_FAILURE_GROUPS_RECHECKED.json',dict(sourceCommit=S,groups=[dict(name=g['name'],originalSource=g['originalSource'],methods=[dict(**t,outcome=by[t['testClass'],t['method']]) for t in g['identities']]) for g in scope['failureGroups']],overlapNotSummed=True))
'''
    if n=='verify_results.py':
        text=text.replace('fresh affected discovery','fresh broad discovery')
        text=text.replace("assert c['selectors']==scope['selectors'] and len(c['selectors'])==13 and c['command'].count('--tests')==13 and c['workingTreeAtStart']=='CLEAN'", "assert c['selectors']==[] and '--tests' not in c['command'] and c['workingTreeAtStart']=='CLEAN'")
        text=text.replace('RAW_AFFECTED','RAW_BROAD').replace('classes=13,exitCode=','classes=261,exitCode=')
        text=text.replace("assert len(scope['priorFailedIdentities'])==14", "assert len(scope['originalUnit14FailedIdentities'])==14")
        text=text.replace("('PRIOR_FAILURES_RECHECKED','priorFailedIdentities',14)","('PRIOR_FAILURES_RECHECKED','priorFailedIdentities',len(scope['priorFailedIdentities'])),('CURRENT_FOLLOWUP_RECHECKED','currentFollowupMethods',79),('UNIT14_PRIOR_FAILURES_RECHECKED','originalUnit14FailedIdentities',14)")
        needle="assert r['discoveryComparison']['sameInputTrees'] and r['discoveryComparison']['exactSameMethodSet']"
        text=text.replace(needle,needle+'''
prior=read(R/scope['priorBroadResultPath']);assert sha((R/scope['priorBroadResultPath']).read_bytes())==scope['priorBroadResultSha256']
before={(t['testClass'],t['method']) for t in prior['tests']};assert len(before)==1477 and before<=expected
assert expected-before=={(t['testClass'],t['method']) for t in scope['expectedAddedMethods']} and len(expected-before)==22
assert {(t['testClass'],t['method']) for t in scope['previousAffectedMethods']}<=expected
assert len(expected)==1499 and len({t[0] for t in expected})==261
assert len(scope['priorFailedIdentities'])==24
assert read(D/'SKIPPED_METHODS.json')['methods']==[t for t in r['tests'] if t['outcome']=='SKIPPED']
groups=read(D/'PRIOR_FAILURE_GROUPS_RECHECKED.json');assert groups['sourceCommit']==r['sourceCommit'] and groups['overlapNotSummed']
assert groups['groups']==[dict(name=g['name'],originalSource=g['originalSource'],methods=[dict(**t,outcome=sets['REGRESSION'][t['testClass'],t['method']]) for t in g['identities']]) for g in scope['failureGroups']]
''')
        text=text.replace('all14OriginalFailedIdentitiesIncluded=True,all19Unit13ControlsIncluded=True,','all1477PriorBroadIdentitiesRetained=True,addedUnit14Methods=22,removedMethods=0,noMethodOrClassFilters=True,all79FollowupMethodsIncluded=True,all541AffectedMethodsIncluded=True,allPriorFailureIdentitiesIncluded=True,all19Unit13ControlsIncluded=True,')
    (W/n).write_text(text)
for n in ['prepare.py','configure.py','run.py','resources.gradle','launch.py','supervise.py','start_runtime.py','durable_monitor.py','evidence.py','collect.py','verify_results.py']:
    shutil.copy2(W/n,D/n)
print('Broad runner, conservation, lossless failure collector and verifier configured')
