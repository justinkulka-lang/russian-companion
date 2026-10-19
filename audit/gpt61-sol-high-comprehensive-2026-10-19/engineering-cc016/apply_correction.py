"""Read a pinned baseline; change only five mutable step bodies/two repair example lists."""
import sys
sys.dont_write_bytecode=True
import copy,hashlib,json,subprocess
from pathlib import Path
from catalog.curriculum_storage import loads,dumps
r=Path.cwd();base='37419b9c20c72eaf48ef2e5244520973ed29de8c';asset=r/'app/src/main/assets/content/v2-curriculum.json';raw=asset.read_bytes()
assert raw==subprocess.check_output(['git','show',base+':app/src/main/assets/content/v2-curriculum.json'])
old=loads(raw);p=copy.deepcopy(old);sha=lambda s:hashlib.sha256(s.encode('utf-8')).hexdigest();changes=[]
allsteps={s['id']:s for u in p['units'] for session in u['sessions'] for s in session['steps']}
prefix=allsteps['u7-st-teach-objectadj-3']['body'].split('этих женщин')[0]
assert prefix=='Моя русская кошка любит мою новую собаку — My Russian cat likes my new dog. '
bodies={
 'u7-st-teach-objectadj-3':prefix+(
  'The separate fragment этих женщин means these women. Its plural forms can be accusative or genitive; '
  'the source illustrates an animate accusative object. The fragment alone is not a complete sentence and supplies '
  'no predicate or first-person subject. The preceding complete cat and dog sentence has its own predicate; '
  'do not carry that meaning into the separate fragment.'),
 'u10-st-teach-animate-1':(
  'этих женщин means these women: an accusative or genitive plural phrase. In the source object example it is accusative; '
  'the displayed phrase alone does not state a complete action or identify a speaker. Эти женщины means these women '
  'in the nominative plural. It is the subject phrase in the other source example; the display contains no predicate or object. '
  'Compare the supplied forms and their case roles, rather than translating either phrase as a whole sentence. '
  'This explanation remains review-pending. Guided work and independent checks are separate; activity is not demonstrated ability.'),
 'u10-st-teach-animate-2':(
  'A complete source example can establish which phrase is the subject or object, but only the noun phrases are displayed here. '
  'For these supplied animate plural forms, the accusative object form matches the genitive plural form. The nominative phrase '
  'names the subject in the source example. Full sentence meanings belong to that source context, not to either isolated phrase. '
  'The short dictionary glosses on later cards name the entry meaning, not the number or case of a displayed inflected form. '
  'These choices practise recognition of the supplied pair; they do not supply unseen sentence production.'),
}
for sid,body in bodies.items():
 before=copy.deepcopy(allsteps[sid]);allsteps[sid]['body']=body;changes.append(dict(unit=7 if sid.startswith('u7-') else 10,stepId=sid,before=before,after=copy.deepcopy(allsteps[sid])))
for number,stem in [(7,'objectadj'),(10,'animate')]:
 unit=p['units'][number-1];session=next(s for s in unit['sessions'] if s['id']==f'u{number}-s-{stem}');obj=next(o for o in unit['objectives'] if o['id']==f'obj-u{number}-{stem}')
 rem=allsteps[f'u{number}-st-rem-{stem}'];before=copy.deepcopy(rem);rem['body']='\n\n'.join(s['body'] for s in session['steps'] if s['kind']=='TEACH');rem['exampleIds']=obj['exampleIds'][:];changes.append(dict(unit=number,stepId=rem['id'],before=before,after=copy.deepcopy(rem)))
template=next(q for q in p['provenanceRecords'] if q['sourceId']=='src-content-audit-research' and q.get('synthesis'))
bib=('Retained complete Wikibooks Russian/Grammar/Accusative_case oldid4330404, including the original subject/object examples '
 'and animate plural case relation; Russian/Grammar/Adjectives oldid4292379 for the unchanged complete cat/dog and rabbit lines '
 'and agreement context. Existing Unit7/10 native fragments and corrected example notes retain their separate original provenance '
 'and CC BY-SA4.0 attribution. Independently authored English explanation and arrangement on2026-10-19, under governed '
 'research-source version2026-10-08. No expanded native clause, new Russian, stress, private Penguin prose or new native application. '
 'Qualified source/linguistic/rights review remains pending. Full source sentence meanings must not be assigned to isolated fragments.')
added=[]
for change in changes:
 number=change['unit'];obj='obj-u7-objectadj' if number==7 else 'obj-u10-animate';sid=change['stepId'];body=change['after']['body'];q=copy.deepcopy(template);original=bib+'\n'+body
 q.update(itemId='prov-cc016-'+sid,unitNumber=number,objectiveId=obj,recordType='label',sourceRecordId='ca-coverage:'+obj+':cc016:'+sid,
  originalSourceText=original,shippedText=body,sourceHash=sha(original),contentHash=sha(body))
 q['synthesis']={'bibliography':bib,'sourceDateOrVersion':'2026-10-08','authorshipDeclaration':'Independently authored English explanation and arrangement on2026-10-19; native fragments/clauses retain existing provenance and licenses. No private-book text.','objectiveOrClaimBinding':obj+':cc016:'+sid}
 for key in ['reviewerIdentity','reviewerQualification','reviewDate','reviewedDigest']:q[key]=''
 added.append(q)
p['provenanceRecords']+=added
assert p['items']==old['items'] and p['examples']==old['examples']
assert p['provenanceRecords'][:-5]==old['provenanceRecords'] and len(old['provenanceRecords'])==6984
assert {k:v for k,v in p.items() if k not in ['units','provenanceRecords']}=={k:v for k,v in old.items() if k not in ['units','provenanceRecords']}
assert [u for u in p['units'] if u['number'] not in [7,10]]==[u for u in old['units'] if u['number'] not in [7,10]]
# Revert exactly the declared step objects and compare the entire curriculum.
reverted=copy.deepcopy(p)
for c in changes:
 for session in reverted['units'][c['unit']-1]['sessions']:
  session['steps']=[c['before'] if s['id']==c['stepId'] else s for s in session['steps']]
reverted['provenanceRecords']=reverted['provenanceRecords'][:-5];assert reverted==old
asset.write_text(dumps(p));out=Path('/workspace/artifacts/cc016-fragments')
(out/'CORRECTION.json').write_text(json.dumps(dict(baselineCommit=base,beforeAssetSha256=hashlib.sha256(raw).hexdigest(),afterAssetSha256=hashlib.sha256(asset.read_bytes()).hexdigest(),changes=changes,addedProvenance=added,all626Items821Examples6984RecordsPreserved=True,allOtherDataExact=True,formalAcceptance=False),ensure_ascii=False,indent=2)+'\n')
print('Changed three teaching bodies/two repair bodies;preserved all items/examples/old records;added5 pending English records')
