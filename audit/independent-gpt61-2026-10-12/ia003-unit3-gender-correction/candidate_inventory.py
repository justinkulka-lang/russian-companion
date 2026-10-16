#!/usr/bin/env python3
"""Reproduce a recorded candidate inventory, not runtime or qualified acceptance."""
import hashlib
import json
import subprocess
import sys
from pathlib import Path
ROOT = Path(__file__).resolve().parents[3]
BASELINE = 'b17b477776ae3ad34b26a8833f2c2c84ef0189ec'
OUT = ROOT / 'audit/independent-gpt61-2026-10-12/ia003-unit3-gender-correction/NEXT_CANDIDATE_INVENTORY.json'
paths = ['app/src/main/assets/content/v2-curriculum.json', 'app/src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt', 'catalog/sources/units-6-10/wikibooks/numbers-4371358.txt', 'catalog/sources/units-6-10/wikibooks/numbers-4371358.revision.json', 'catalog/sources/units-3-5/wikibooks/gender-4310736.txt', 'catalog/sources/units-3-5/openrussian/nouns.csv', 'catalog/sources/units-3-5/openrussian/LICENSE', 'catalog/sources/units-3-5/wikibooks/copyrights-4622060.txt']
inputs = {p: subprocess.check_output(['git', 'show', BASELINE + ':' + p], cwd=ROOT) for p in paths}
pack = json.loads(inputs[paths[0]])
sha = lambda b: hashlib.sha256(b).hexdigest()
canon = lambda o: sha(json.dumps(o,ensure_ascii=False,sort_keys=True,separators=(',',':')).encode())
items = {i['id']:i for i in pack['items']}
examples = {e['id']:e for e in pack['examples']}
def objective(n, ident):
    unit = next(u for u in pack['units'] if u['number']==n)
    o = next(o for o in unit['objectives'] if o['id']==ident)
    steps = {s['id']:s for se in unit['sessions'] for s in se['steps']}
    teaching = [steps[x] for x in o['instructionStepIds'] if steps[x]['kind']=='TEACH']
    repair = [steps[x] for x in o['remediationStepIds']]
    return {'id':ident,'ownerUnit':n,'objectiveSha256':canon(o),'teaching':[{'id':s['id'],'sha256':canon(s),'exampleIds':s.get('exampleIds',[])} for s in teaching],'guidedSteps':o['practiceStepIds'],'independentItems':[{'id':i,'sha256':canon(items[i]),'key':items[i]['answer'],'sourceId':items[i]['sourceId']} for i in o['checkItemIds']],'repair':[{'id':s['id'],'sha256':canon(s),'bodyEqualsCompleteTeaching':s['body']=='\n\n'.join(t['body'] for t in teaching),'exampleIds':s.get('exampleIds',[])} for s in repair]}
gender = objective(3,'obj-u3-gender'); plural = objective(8,'obj-u8-plural'); number = objective(9,'obj-u9-odindva')
assert all(x['bodyEqualsCompleteTeaching'] for o in [gender,plural,number] for x in o['repair'])
assert len(gender['independentItems'])==5 and len(number['teaching'])==6
result = {'kind':'RECORDED_STATIC_CANDIDATE_NOT_ACTIVATION_OR_QUALIFIED_APPROVAL','inspectedCommit':BASELINE,'objectHashEncoding':'SHA256 of sorted-key UTF8 compact JSON;not the app governed identity algorithm','inputSha256':{p:sha(b) for p,b in inputs.items()},'candidateTarget':9,'instructionalReason':'Supplied one/two constructions explicitly distinguish masculine/feminine/neuter and plural-form agreement. This provides a context to revisit earlier supplied lexical gender together with existing supplied plural review;not numeric milestone spacing.','earlierGender':gender,'existingPlural':plural,'targetNumeralContext':number,'candidateObjectiveIds':['obj-u1-decoding','obj-u8-plural','obj-u3-gender'],'existingPolicyId':'unit9-prior-reading-nominative-plural-v1','existingPolicyUnchanged':True,'newPolicyId':None,'newSchemaActivated':False,'sourceInterpretationLimits':['Use complete retained numeral context;do not re-admit the source simplified compound-number last-digit rule or its uncommon-oblique-case claim.','Noun gender recognition does not demonstrate productive numeral agreement or all declined cases.','Plural grammatical agreement is not nouns losing lexical gender;one pair of jeans is not multiple pairs.','All five owner3 candidates need genuine current teaching/guided/independent completion;dictionary labels alone do not establish eligibility.','Prior source/rights reviews and current engineering inspection are not qualified acceptance.'],'requiredBeforeImplementation':['Author explicit bounded obligation and source/rights disposition;do not mutate Unit9 v1.','Capture genuine current-corrected-source Unit9 and downstream Unit10 lifecycle histories/backups using real APIs;earlier pre-Unit3-correction histories are historical only.','Define a new scoped assignment version if needed;do not extend target6/schema4 or change schema1 legacy meaning.','Preserve exact registered old9 declarations and ordinary/owner9 identities only across that policy-ID transition;never accept changed teaching/source under a compatibility projection.','Retain fresh assigned answer boundaries,accepted-order/cutoff,backup/reopen/replacement/rollback/packless refusal and full repair return.','Refresh canonical ancestry only after actual implementation;record exact selected tests,all failures,lint/attachment and exclusions.'],'JUnitMethodsExecuted':0,'formalAcceptance':False,'IA003':'OPEN','physicalAcceptance':'LAST_AFTER_FINAL_INDEPENDENT_COMPREHENSIVE_AUDIT_OUTSTANDING','releaseAuthorization':False}
s = json.dumps(result,ensure_ascii=False,indent=2)+'\n'
if '--check' in sys.argv:
    assert OUT.read_text()==s
    print('PASS: recorded candidate inventory reproduced from exact Git blobs;zero app tests executed')
else:
    OUT.write_text(s)
    print('Recorded candidate only;no policy or scheduling activation')
