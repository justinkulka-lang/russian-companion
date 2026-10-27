"""Reproduce full-message/source comparison diagnosis; zero Gradle/JUnit executions."""
from pathlib import Path
import hashlib
import json
import subprocess
import sys
import xml.etree.ElementTree as ET
import zipfile

D = Path(__file__).resolve().parent
R = next(p for p in D.parents if (p / '.git').exists())
A = D / 'ATTEMPT_1'
r = json.loads((A / 'REGRESSION_RESULT.json').read_text())
source = r['sourceCommit']
sha = lambda data: hashlib.sha256(data).hexdigest()
blob = lambda commit, path: subprocess.check_output(['git', 'show', commit + ':' + path], cwd=R)
old_id = 'unit12-prior-reading-past-gender-v1'
new_id = 'unit12-prior-gender-past-gender-v2'
old_digest = '1c5d43e55bbd627515f1fab69ddea0406fecf2257ba696acbfd88b413c404f97'
new_digest = '8c3ae9bbd682e069dbcd4dc579c30af36bbce321120ce7da804260e5a44d7033'
raw_path = 'app/src/main/assets/content/v2-curriculum.json'
prior_raw = blob('7ed190ec7ccd3ac761e5d6457a04da93cc92dbd5', raw_path)
current_raw = blob(source, raw_path)
assert prior_raw.count(old_id.encode()) == current_raw.count(new_id.encode()) == 1
assert prior_raw.replace(old_id.encode(), new_id.encode()) == current_raw
assert current_raw == blob('ca0fdaeca8de81ab6a5d4935fbcdcb068c4af913', raw_path)
ref_path = 'app/src/test/resources/reviewed-curriculum-cyrillic.json'
ref_before11 = json.loads(blob('fe7968e8c690909363c753ec546cd9fe329334f9', ref_path))
ref_before12 = json.loads(blob('7e4d3ee4652194f88a4804f1734cf8171efae714', ref_path))
ref_current = json.loads(blob(source, ref_path))
assert [len(x['subsequentEngineeringReferenceReviews']) for x in [ref_before11, ref_before12, ref_current]] == [5, 6, 7]
assert ref_current['subsequentEngineeringReferenceReviews'][:-1] == ref_before12['subsequentEngineeringReferenceReviews']
assert not ref_current['semanticAcceptance'] and not ref_current['rightsAcceptance']
proposal = json.loads((D / 'PROPOSAL_REVIEW.json').read_text())
assert next(x for x in proposal['identityTokenReplacements'] if x['field'] == 'contentReviewDigest') == {
    'field': 'contentReviewDigest', 'before': old_digest, 'after': new_digest}
capture_path = R / 'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit12-gender-baseline-2026-10-26/BASELINE_CURRENT_UNIT12_V1.zip'
with zipfile.ZipFile(capture_path) as z:
    saved = next(a for a in json.loads(z.read('learner.json'))['reviewAssignments'] if a['targetUnit'] == 12)
assert saved['policyId'] == old_id and saved['schema'] == 1
assert [s['objectiveId'] for s in saved['slots']] == ['obj-u1-decoding', 'obj-u11-past-aspect']
assert [s['ownerUnit'] for s in saved['slots']] == [1, 11]
observation = json.loads((D.parent / 'engineering-unit12-gender-correction-2026-10-27/CURRENT_POLICY_OBSERVATION.json').read_text())
policy = next(p for p in observation['policies'] if p['unit'] == 12)
new_pools = [p for p in observation['pools'] if p['unit'] == 12]
assert policy['id'] == new_id and policy['schema'] == 14
assert [p['objective'] for p in new_pools] == ['obj-u3-gender', 'obj-u11-past-aspect']
rows = []
with zipfile.ZipFile(A / 'RAW.zip') as z:
    for path in sorted(z.namelist()):
        if not path.startswith('REGRESSION/XML/') or not path.endswith('.xml'):
            continue
        for t in ET.fromstring(z.read(path)).findall('testcase'):
            failure = t.find('failure')
            if failure is None:
                continue
            message = failure.get('message', '')
            method = t.get('name')
            name = t.get('classname')
            recorded = next(x for x in r['tests'] if x['testClass'] == name and x['method'] == method)
            assert recorded['fullMessageBytes'] == len(message.encode())
            assert recorded['fullMessageSha256'] == sha(message.encode())
            proof = {}
            if len(message) > 100000:
                _, marker, values = message.partition('expected:<')
                assert marker and values.count('> but was:<') == 1 and values.endswith('>')
                expected, actual = values.split('> but was:<')
                actual = actual[:-1]
                if expected.replace(old_id, new_id) == actual:
                    assert expected.count(old_id) == actual.count(new_id) == 1
                    direction = 'EXPECTED_OLD_ACTUAL_NEW'
                else:
                    assert expected.replace(new_id, old_id) == actual
                    assert expected.count(new_id) == actual.count(old_id) == 1
                    direction = 'EXPECTED_NEW_ACTUAL_OLD'
                proof = dict(kind='EXACT_ONE_POLICY_ID_TOKEN_DIFFERENCE', direction=direction,
                             expectedBytes=len(expected.encode()), actualBytes=len(actual.encode()),
                             everyOtherByteExact=True, beforeToken=old_id, afterToken=new_id)
            elif method == 'kernelReferenceChangesOnlyItsExactHashAndAppendsPendingEngineeringRecord':
                assert message == 'java.lang.AssertionError: expected:<6> but was:<7>'
                proof = dict(kind='PRIOR_UNIT11_REFERENCE_CHECK_AFTER_UNIT12_APPEND',
                             oldBaselineNotes=5, oldExpectedNotes=6, genuinePriorNotes=6, currentNotes=7,
                             currentUnit12ReferenceControl='PASSED', acceptanceGranted=False)
            elif method == 'ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup':
                assert message == 'java.lang.AssertionError: expected:<[obj-u1-decoding, obj-u11-past-aspect]> but was:<[obj-u3-gender, obj-u11-past-aspect]>'
                proof = dict(kind='PRIOR_V1_OBJECTIVE_EXPECTATION_ON_CURRENT_V2_INPUT',
                             priorSchema=1, currentSchema=14, savedV1MeaningMustRemainUnchanged=True)
            elif method == 'placementWithTaughtUnit1ButUntaughtUnit11RepairsTheMissingOwnerInsteadOfUnit1':
                assert 'Text + EditableText = [Open earlier Unit 11 instruction and checks]' in message
                assert "Text = '[Open earlier Unit 3 instruction and checks]'" in message
                proof = dict(kind='PRIOR_V1_OWNER_PRECONDITION_ON_CURRENT_V2_INPUT',
                             priorOwners=[1, 11], currentOwners=[3, 11],
                             limitation='The old fixture establishes Unit1,not the current owner3. Current genuinely-completed3-to-missing11 routing passed in Unit12GenderReviewUiTest.')
            else:
                assert old_digest in message and new_digest in message
                assert name in ['dev.russiancompanion.v2.Units1620SourceIntakeTest',
                                'dev.russiancompanion.v2.Units2125SourceIntakeTest',
                                'dev.russiancompanion.v2.Units2630SourceIntakeTest']
                proof = dict(kind='RETAINED_PRIOR_UNIT12_CONTENT_DIGEST_ON_CURRENT_DECLARATION',
                             oldDigest=old_digest, currentDigest=new_digest,
                             digestBasis='Actual Kotlin render manifests;raw source differs only by the Unit12 policy-ID token.',
                             oldPinMustRemainUnchanged=True)
            rows.append(dict(testClass=name, method=method, outcome='FAILED', xmlMember=path,
                             fullMessageBytes=len(message.encode()), fullMessageSha256=sha(message.encode()), proof=proof,
                             disposition='OPEN_REQUIRES_SCOPED_HISTORICAL_COMPARISON_RECONCILIATION',
                             laterAssertionsInFailedMethod='NOT_EVIDENCED_BY_THIS_FAILED_EXECUTION'))
assert len(rows) == 8
controls = [x for x in r['tests'] if x['testClass'].endswith('Unit12BindingConservationTest') or 'Unit12Gender' in x['testClass']]
assert len(controls) == 17 and all(x['outcome'] == 'PASSED' for x in controls)
result = dict(sourceCommit=source, evidenceCommit='451d5d0255f18cd6075f2549652c269112e32109',
              reviewType='READ_ONLY_FULL_MESSAGE_HASH_AND_EXACT_SOURCE_COMPARISON_NOT_A_FIX_OR_INDEPENDENT_AUDIT',
              originalCounts=r['testCounts'], originalExitCode=r['exitCode'], originalResultImmutable=True,
              rawOnlyUnit12DeclarationChanged=True, currentControlsPassedWithinSameRun=controls, failures=rows,
              codeOrTestChanges=False, assertionsWaived=False, newJUnitExecutions=0,
              all48Findings='OPEN', audit='INCOMPLETE', physicalAcceptance='OUTSTANDING_LAST')
data = (json.dumps(result, ensure_ascii=False, indent=2) + '\n').encode()
assert sys.argv[1:] in [[], ['--check']]
target = D / 'FULL_FAILURE_REVIEW.json'
if sys.argv[1:] == ['--check']:
    assert target.read_bytes() == data
else:
    assert not target.exists()
    target.write_bytes(data)
print(json.dumps(dict(verified=True, sourceCommit=source, completeMessagesHashMatched=8,
                      exactOneTokenLargeComparisons=2, currentControlsPassedWithinSameRun=17,
                      failuresStillOpen=8, originalExitCode=1, newJUnitExecutions=0)))
