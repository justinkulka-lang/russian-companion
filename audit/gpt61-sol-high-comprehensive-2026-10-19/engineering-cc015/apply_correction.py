"""Bounded Unit4 teaching correction. Run at repo root; refuses any base other than b5e1daf5."""
import sys
sys.dont_write_bytecode=True
import copy,hashlib,json,subprocess
from pathlib import Path
from catalog.curriculum_storage import loads,dumps
from catalog.units_3_5_intake import FrozenSources
r=Path.cwd();base='b5e1daf50ac8597d00aeb6976f894d3ce44c03ca';f=r/'app/src/main/assets/content/v2-curriculum.json';raw=f.read_bytes()
assert raw==subprocess.check_output(['git','show',base+':app/src/main/assets/content/v2-curriculum.json'])
old=loads(raw);p=copy.deepcopy(old);frozen=FrozenSources();u=p['units'][3];objective=next(o for o in u['objectives'] if o['id']=='obj-u4-present');session=next(s for s in u['sessions'] if s['id']=='u4-s-present')
sha=lambda x:hashlib.sha256(x.encode('utf-8')).hexdigest()
records={q['itemId']:q for q in p['provenanceRecords']};items={i['id']:i for i in p['items']}
template=copy.deepcopy(next(q for q in p['provenanceRecords'] if q['sourceId']=='src-content-audit-research' and q.get('synthesis')))
bib=('Complete frozen Wikibooks Russian/Grammar/Verbs oldid4233876, including first/second conjugation and exceptions; '
 'complete retained OpenRussian commit50e210c4803237779cb562bc1abcea529066031c rows for the four entries, '
 'with exact already-admitted Unit4 presfut fields. Bounded independently authored English explanation and arrangement on2026-10-19 '
 'within governed research-source version2026-10-08. No inflection, stress, phrase or sentence generated. '
 'Native forms retain their existing lexical provenance, CC BY-SA4.0 attribution and share-alike treatment. '
 'This is not Penguin text or a newly approved paradigm. Qualified linguistic/source/rights review remains pending.')
added_records=[]
def cover(binding,text):
 q=copy.deepcopy(template);original=bib+'\n'+text
 q.update(itemId='prov-cc015-'+binding,unitNumber=4,objectiveId='obj-u4-present',recordType='label',sourceRecordId='ca-coverage:obj-u4-present:cc015:'+binding,
 originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text))
 q['synthesis']={'bibliography':bib,'sourceDateOrVersion':'2026-10-08','authorshipDeclaration':'Independently authored English on2026-10-19; all native forms copied from separately admitted lexical records. No private-book material.', 'objectiveOrClaimBinding':'obj-u4-present:cc015:'+binding}
 for key in ['reviewerIdentity','reviewerQualification','reviewDate','reviewedDigest']:q[key]=''
 added_records.append(q)

specs=[
 ('говорить',"verbs.csv:говори'ть",'мы','we','speaking',
  'Compare the written endings in these supplied forms; do not substitute the endings from the earlier reading example.'),
 ('видеть',"verbs.csv:ви'деть",'он','he','seeing',
  'The infinitive alone does not tell you which supplied form to choose. Notice that the I form differs from the other forms here; learn the whole supplied forms.'),
 ('писать',"verbs.csv:писа'ть",'вы','you (plural)','writing',
  'The supplied present forms have a different written stem from the infinitive. Keep the written stress exactly as shown; do not append an ending to the whole infinitive.'),
 ('идти',"verbs.csv:идти'",'они','they','going',
  'The supplied finite forms differ from the infinitive. Learn these whole forms; this card does not teach how to choose between motion verbs in context.'),
]
new_teaching=[];new_guides=[];source_evidence=[]
labels={'я':'I','ты':'you (one familiar person)','он':'he','мы':'we','вы':'you (plural)','они':'they'}
fields={'я':'presfut_sg1','ты':'presfut_sg2','он':'presfut_sg3','мы':'presfut_pl1','вы':'presfut_pl2','они':'presfut_pl3'}
for bare,rid,target,targetlabel,meaning,caution in specs:
 item=items[f'u4-i-verb-{bare}-{target}'];row=frozen.row(rid);assert row['aspect']=='imperfective'
 persons=['я','ты','он',target] if target not in ['я','ты','он'] else ['я','ты','он','мы']
 forms={person:records[f'prov-u4-form-{bare}-{person}']['shippedText'] for person in persons}
 for person,form in forms.items():
  q=records[f'prov-u4-form-{bare}-{person}'];assert q['originalSourceText']==row[fields[person]]
  assert q['sourceRecordId']==rid+'#'+fields[person] and q['reviewStatus']=='IMPLEMENTED_PENDING_FINAL_AUDIT'
 assert set(forms.values())==set(item['options'])
 title='Match '+targetlabel+' in the supplied '+meaning+' forms'
 body=(f'The infinitive {item["promptLetter"]} names this entry. Match each supplied present form to its person: '+
  '; '.join(labels[person]+': '+forms[person] for person in persons)+'. '+
  f'When the check requests {targetlabel}, choose {item["answer"]}. '+caution+' '+
  'First practise a different person from this same entry, then answer the requested person without using option position. These supplied choices do not demonstrate producing an unseen form.')
 sid='u4-st-teach-present-'+bare;eid='ex-u4-present-'+bare+'-'+target;gid='u4-g-present-'+bare+'-я';guideid='u4-st-guided-present-'+bare
 example=dict(id=eid,kind='SOURCE_FORM',letterId='',display=item['answer'],note=targetlabel+': the supplied form for this entry.',objectiveId='obj-u4-present',sourceId=item['sourceId'],reviewStatus=item['reviewStatus'],containsGeneratedRussian=False)
 # Existing models use kinds and optional fields copied from the prior source examples.
 example_template=copy.deepcopy(next(e for e in old['examples'] if e['id']=='ex-u4-verb-читать'))
 example_template.update(id=eid,display=item['answer'],note=example['note']);p['examples'].append(example_template)
 teaching=dict(id=sid,kind='TEACH',title=title,body=body,referenceIds=['ref-u35-attribution'],exampleIds=[eid]);new_teaching.append(teaching)
 guide=copy.deepcopy(item);guide.update(id=gid,prompt='Which supplied present form belongs to I?',answer=forms['я'],feedback='The supplied I form is '+forms['я']+'. Compare it with the separately shown '+targetlabel+' form '+item['answer']+'.')
 # Explicit distinct target: guided I, independent original target; all options remain exact admitted forms.
 guide['distractorRationales']=['' if option==forms['я'] else 'This supplied form belongs to '+next(labels[person] for person,form in forms.items() if form==option)+', while this practice asks for I.' for option in guide['options']]
 p['items'].append(guide);gstep=dict(id=guideid,kind='GUIDED',title='Practise I before the independent '+targetlabel+' check',body='Compare the supplied forms above. Choose I here; the independent check asks for a different person.',itemIds=[gid],remediationStepId='u4-st-rem-present');new_guides.append(gstep)
 objective['instructionStepIds'].append(sid);objective['exampleIds'].append(eid);objective['practiceStepIds'].append(guideid)
 for key,value in [('title',title),('body',body),('example-note',example['note']),('guide-title',gstep['title']),('guide-body',gstep['body']),('guide-prompt',guide['prompt']),('guide-feedback',guide['feedback'])]:cover(bare+'-'+key,value)
 for n,rationale in enumerate(guide['distractorRationales']):
  if rationale:cover(bare+'-rationale-'+str(n),rationale)
 source_evidence.append(dict(itemId=item['id'],infinitive=item['promptLetter'],requiredTarget=target,requiredForm=item['answer'],sourceRow=rid,field=fields[target],nativeRecord=records[f'prov-u4-form-{bare}-{target}'],taughtForms=forms,newTeachingStep=sid,newExample=eid,guidedItem=gid,guidedPerson='я',newGuidedStep=guideid))
rem=next(st for st in session['steps'] if st['id']=='u4-st-rem-present');check=next(st for st in session['steps'] if st['id']=='u4-st-check-present')
# Preserve the old seven cards and both supported guides; teach the four new rows before new guided work.
existing=[st for st in session['steps'] if st['id'] not in [rem['id'],check['id']]]
session['steps']=existing+new_teaching+new_guides+[rem,check]
teaching=[st for st in session['steps'] if st['kind']=='TEACH'];rem['body']='\n\n'.join(st['body'] for st in teaching);rem['exampleIds']=objective['exampleIds'][:]
cover('complete-repair',rem['body']);p['provenanceRecords']+=added_records
assert len(p['items'])==626 and p['items'][:622]==old['items']
assert p['examples'][:len(old['examples'])]==old['examples']
assert p['provenanceRecords'][:len(old['provenanceRecords'])]==old['provenanceRecords']
assert {k:v for k,v in p.items() if k not in ['units','items','examples','provenanceRecords']}=={k:v for k,v in old.items() if k not in ['units','items','examples','provenanceRecords']}
assert p['units'][:3]+p['units'][4:]==old['units'][:3]+old['units'][4:]
f.write_text(dumps(p))
out=Path('/workspace/artifacts/cc015-present');(out/'CORRECTION.json').write_text(json.dumps(dict(baseCommit=base,beforeUnit=old['units'][3],afterUnit=u,newItemIds=[i['id'] for i in p['items'][622:]],newExampleIds=[e['id'] for e in p['examples'][len(old['examples']):]],addedRecordIds=[q['itemId'] for q in added_records],existingItemCount=622,newItemCount=626,existingProvenanceCount=len(old['provenanceRecords']),addedProvenanceCount=len(added_records),formalAcceptance=False),ensure_ascii=False,indent=2)+'\n')
(out/'SOURCE_BINDINGS.json').write_text(json.dumps(source_evidence,ensure_ascii=False,indent=2)+'\n')
print('Preserved622 items,817 examples,6943 old records;added4 guides,4 examples,4 teaching steps and',len(added_records),'pending coverage records')
