import sys,os
sys.dont_write_bytecode=True;sys.path.insert(0,os.getcwd())
import copy,hashlib,json,subprocess
from pathlib import Path
from catalog.curriculum_storage import loads,dumps
r=Path.cwd();out=Path('/workspace/artifacts/cc003-imperative');f=r/'app/src/main/assets/content/v2-curriculum.json';proposal=json.loads((out/'REJECTED_PROPOSAL/CORRECTION.json').read_text());assert hashlib.sha256(f.read_bytes()).hexdigest()==proposal['afterAssetSha256']
base='175a793020d85b8760a9255cf8b2b6b0091745f4';raw=subprocess.check_output(['git','show',base+':app/src/main/assets/content/v2-curriculum.json']);old=loads(raw);p=copy.deepcopy(old);u=p['units'][14];o=next(x for x in u['objectives'] if x['id']=='obj-u15-aspect-consolidate');session=next(x for x in u['sessions'] if x['id']=='u15-s-aspect-consolidate');steps={s['id']:s for s in session['steps']};sha=lambda s:hashlib.sha256(s.encode()).hexdigest()
bib=('Complete retained Wikibooks Russian/Verbal_Aspect oldid4059032,recorded say/tell and do/make aspect pairs and bounded process/result discussion. '
 'Existing native forms and lexical examples retain their original provenance,spelling/stress and licenses. Independently authored English preparation explanation and arrangement on2026-10-20. '
 'This material teaches only the already-admitted lexical partners;it adds no contextual Russian application,book prose or new native form. '
 'CC003 contextual request/prohibition assessment remains unresolved pending exact source admission and linguistic qualification. No review/owner approval.')
bodies=[
 ('Keep the supplied say/tell partners together','The supplied infinitives говори́ть and сказа́ть form the recorded imperfective/perfective pair for the say/tell relation. The short English label beside both examples does not mean that every use of speaking has this same partner. Recognize the requested member of this supplied pair.',o['exampleIds'][:2]),
 ('Keep the supplied do/make partners together','The supplied infinitives де́лать and сде́лать form the recorded imperfective/perfective do/make pair. Preserve their supplied spelling and stress. This one example does not establish a general rule that adding any prefix produces the correct partner for any verb.',o['exampleIds'][2:]),
 ('Distinguish partner recall from contextual use','These choices ask for members of the supplied infinitive pairs. They do not ask you to choose an aspect for a particular request or prohibition. Knowing a lexical partner is useful preparation, but does not demonstrate that contextual skill. Compare the requested aspect and the meaning group before selecting a supplied form.',[]),
]
changes=[]
for n,(title,body,examples) in enumerate(bodies,1):
 s=steps[f'u15-st-teach-aspect-consolidate-{n}'];before=copy.deepcopy(s);s.update(title=title,body=body,exampleIds=examples);changes.append(dict(id=s['id'],before=before,after=copy.deepcopy(s)))
teaching=[steps[f'u15-st-teach-aspect-consolidate-{n}'] for n in [1,2,3]];before=copy.deepcopy(steps['u15-st-rem-aspect-consolidate']);rem=steps['u15-st-rem-aspect-consolidate'];rem.update(title='Review both supplied lexical pairs',body='\n\n'.join(s['body'] for s in teaching),exampleIds=o['exampleIds'][:]);changes.append(dict(id=rem['id'],before=before,after=copy.deepcopy(rem)))
session['steps']=teaching+[steps['u15-st-guided-aspect-consolidate-1'],rem,steps['u15-st-check-aspect-consolidate']]
template=copy.deepcopy(next(q for q in p['provenanceRecords'] if q['sourceId']=='src-content-audit-research' and q.get('synthesis')));added=[]
for change in changes:
 for field in ['title','body']:
  text=change['after'][field];q=copy.deepcopy(template);original=bib+'\n'+text;binding=change['id']+'-'+field
  q.update(itemId='prov-cc003-preparation-'+binding,unitNumber=15,objectiveId=o['id'],recordType='label',sourceRecordId='ca-coverage:'+o['id']+':cc003-preparation:'+binding,originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text))
  q['synthesis']={'bibliography':bib,'sourceDateOrVersion':'2026-10-08','authorshipDeclaration':'Independent English explanation and arrangement2026-10-20;native lexical forms retain their separately admitted provenance. No new native application.','objectiveOrClaimBinding':o['id']+':cc003-preparation:'+binding,'applications':[]}
  for k in ['reviewerIdentity','reviewerQualification','reviewDate','reviewedDigest']:q[k]=''
  added.append(q)
p['provenanceRecords']+=added
assert p['items']==old['items'] and p['examples']==old['examples'];assert p['provenanceRecords'][:-8]==old['provenanceRecords'];assert p['units'][:14]+p['units'][15:]==old['units'][:14]+old['units'][15:]
rev=copy.deepcopy(p);rev['units'][14]=old['units'][14];rev['provenanceRecords']=old['provenanceRecords'];assert rev==old
f.write_text(dumps(p));pin='app/src/main/java/dev/russiancompanion/v2/curriculum/CompleteRuleContextPins.kt';Path(pin).write_bytes(subprocess.check_output(['git','show',base+':'+pin]))
(out/'PREPARATION_CORRECTION.json').write_text(json.dumps(dict(baselineCommit=base,beforeAssetSha256=hashlib.sha256(raw).hexdigest(),afterAssetSha256=hashlib.sha256(f.read_bytes()).hexdigest(),beforeUnit=old['units'][14],afterUnit=u,changes=changes,addedProvenance=added,all626Items821Examples7024RecordsExact=True,contextualAssessmentGap='CC003_OPEN_SOURCE_ADMISSION_AND_LINGUISTIC_QUALIFICATION_REQUIRED',rejectedProposalStored='REJECTED_PROPOSAL',formalAcceptance=False),ensure_ascii=False,indent=2)+'\n');print('Preparation only:3cards before guide;full4-example repair;8pendingrecords;CC003 contextual gap OPEN')
