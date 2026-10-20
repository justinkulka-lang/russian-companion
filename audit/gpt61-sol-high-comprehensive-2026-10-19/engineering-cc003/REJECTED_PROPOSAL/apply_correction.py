import sys,os
sys.dont_write_bytecode=True
sys.path.insert(0,os.getcwd())
import copy,hashlib,json,subprocess
from pathlib import Path
from catalog.curriculum_storage import loads,dumps
r=Path.cwd();f=r/'app/src/main/assets/content/v2-curriculum.json';raw=f.read_bytes();base='175a793020d85b8760a9255cf8b2b6b0091745f4'
assert raw==subprocess.check_output(['git','show',base+':app/src/main/assets/content/v2-curriculum.json'])
old=loads(raw);p=copy.deepcopy(old);u=p['units'][14];o=next(x for x in u['objectives'] if x['id']=='obj-u15-aspect-consolidate');session=next(x for x in u['sessions'] if x['id']=='u15-s-aspect-consolidate');steps={s['id']:s for s in session['steps']};sha=lambda s:hashlib.sha256(s.encode()).hexdigest()
source=Path('catalog/sources/units-11-15/wikibooks/verbal-aspect-4059032.txt').read_text()
for form in ['говори́ть','сказа́ть','де́лать','сде́лать']:assert form in source
bib=('Independent English research synthesis,2026-10-20. The New Penguin Russian Course,Lesson15, '
 'sections15.3 printed174-175,15.6 printed176,and15.11 printed178-181,privately inspected for grammatical facts only; '
 'PDF locators186-188 and190-193 for supplied private reference SHA2562bcc0c45fac4508adf283f4dd939a7e578cf2c1306fdadf4966300532beb6303. '
 'No book prose,translation,example,exercise or image is reproduced. Grammatical facts are expressed in independently authored English and new situations. '
 'Four independently invented English situations classify aspect categories only;no new Russian word,form,sentence or native application. '
 'The complete retained Wikibooks Russian/Verbal_Aspect oldid4059032 supports unchanged preparatory pairs and broad process/result distinction,not the full request rules. '
 'English source/app contexts are not evidence of Russian-form production. Original native lexical evidence retains its own provenance/licenses. '
 'Qualified linguistic/source/rights and independent changed-source review remain pending. No review stage or approval.')
template=copy.deepcopy(next(q for q in p['provenanceRecords'] if q['sourceId']=='src-content-audit-research' and q.get('synthesis')))
added=[];pins={}
def record(binding,text,context=None,original=None,application=None):
 q=copy.deepcopy(template);original=original or bib+'\n'+text
 q.update(itemId='prov-cc003-'+binding,unitNumber=15,objectiveId=o['id'],recordType='label',sourceRecordId=context or 'ca-coverage:'+o['id']+':cc003:'+binding,originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text))
 q['synthesis']={'bibliography':bib,'sourceDateOrVersion':'2026-10-08','authorshipDeclaration':'Original English scenarios,contrasts and explanation authored2026-10-20. Privately consulted reference contributes grammatical facts only;no book wording/example/translation reused. No new native application.','objectiveOrClaimBinding':o['id']+':cc003:'+binding,'applications':[] if application is None else [application]}
 for k in ['reviewerIdentity','reviewerQualification','reviewDate','reviewedDigest']:q[k]=''
 added.append(q);return q['itemId']
def field(s):return str(len(s.encode()))+':'+s
cases=[
 ('u15-g-request-aspect-repeat','A supervisor asks an assistant to inspect a gauge at every break during a week. The instruction concerns recurring inspections, with no single final inspection singled out.','imperfective','REPEAT',False,
  'The repeated schedule makes the imperfective category fit this bounded request. A perfective choice would shift attention to a particular completed inspection.'),
 ('u15-i-request-aspect-single','An organizer courteously requests one particular attendance list to be completed now. The goal is that one finished list; this is neither a recurring instruction nor an insistent reminder to get started.','perfective','SINGLE',True,
  'The request focuses on obtaining one completed result, so the perfective category fits. An imperfective choice would fit a different focus such as recurrence or an instruction to get on with the process.'),
 ('u15-i-request-aspect-repeat','A technician is instructed to rinse a measuring cup after every sample throughout the shift. The request concerns repeated rinsing rather than one completed task.','imperfective','REPEAT',True,
  'The imperfective category fits the repeated rinsing instruction. The perfective category would select a particular completed task rather than this recurring instruction.'),
 ('u15-i-request-aspect-prohibit','A visitor is told not to disconnect a live sensor. This is a ban on doing that action, rather than a warning about something happening accidentally.','imperfective','PROHIBIT',True,
  'The imperfective category fits this ordinary prohibition. A perfective negative can express certain warnings about an unintended event; that is a different situation from the explicit ban here.'),
]
newitems=[];newexamples=[]
for iid,cue,answer,feature,independent,feedback in cases:
 options=['imperfective','perfective'];wrong=next(x for x in options if x!=answer);rationale=feedback
 app=dict(cue=cue,answer=answer,distractors=[wrong]);canonical='tuple-v1:1;'+field(cue)+field(answer)+'1;'+field(wrong);context='ca-rule-context:'+iid;original=bib+'\nContext explanation:\n'+feedback+'\nTuple-v1:'+canonical;pins[context]=sha(original)
 bound=[record(iid+'-'+str(i),v,context,original,app) for i,v in enumerate([cue,answer,wrong])]
 item=copy.deepcopy(next(i for i in p['items'] if i['id']=='u8-g-availability-0'));prompt='Which aspect category fits this described request under the bounded rule taught here?';reasons=['' if x==answer else rationale for x in options]
 item.update(id=iid,prompt=prompt,promptLetter=cue,options=options,answer=answer,feedback=feedback,objectiveId=o['id'],distractorRationales=reasons)
 item['assessment'].update(testedFeature='BOUNDED_REQUEST_ASPECT_'+feature,evidenceMode='STATED_APPLICATION',contrastSetId=iid,cueLanguage='en',independent=independent,answerSourceIds=[bound[1]],distractorSourceIds=['' if x==answer else bound[2] for x in options],distractorDifference=reasons,whyAnswerDemonstratesObjective=feedback,contrastMembers=options,relationKind='STATED_APPLICATION',relationParticipantIds=bound,responseKind='ENGLISH_CLASSIFICATION')
 newitems.append(item)
 eid='ex-'+iid;ex=copy.deepcopy(next(e for e in p['examples'] if e['id']=='ex-u8-availability-0'));ex.update(id=eid,display=cue,note=feedback,sourceId=item['sourceId'],objectiveId=o['id']);newexamples.append(ex)
 for key,text in [('prompt',prompt),('feedback',feedback),('rationale',rationale),('example',cue),('example-note',feedback)]:record(iid+'-'+key,text)
p['items']+=newitems;p['examples']+=newexamples
oldexampleids=o['exampleIds'][:]
bodies=[
 ('Keep the supplied say/tell partners together','The supplied infinitives говори́ть and сказа́ть form the recorded imperfective/perfective pair for the say/tell relation. The short source label beside both forms is not a promise that every meaning of speaking has the same partner. First recognize the pair; a communicative request also needs a context.',oldexampleids[:2]),
 ('Keep the supplied do/make partners together','The supplied infinitives де́лать and сде́лать are the recorded imperfective/perfective do/make pair. Preserve the supplied spelling and stress. This one pair does not authorize a rule that adding any prefix makes the correct partner of any verb.',oldexampleids[2:]),
 ('Separate a lexical pair from an aspect decision','Pair recognition identifies related verbs. It does not by itself decide what a speaker is requesting. The English situations below ask only for the aspect category suited to a specified communicative purpose. They do not ask you to invent a Russian sentence or prove command-form production.',[]),
 ('A request focused on one completed result','For the bounded courteous request here, the speaker wants one particular finished result. Choose the perfective category. This is not a claim that every command about one occasion must use that category:insistent instructions to get started and invitations can have a different focus. Keep the stated purpose with the context.',[newexamples[1]['id']]),
 ('A request for recurring activity','When the stated instruction concerns an activity repeated on a schedule rather than a singled-out completed result, choose the imperfective category in these situations. Read the described recurrence; do not choose from a memorized lexical partner or an answer position.',[newexamples[0]['id'],newexamples[2]['id']]),
 ('An ordinary prohibition','For the explicit ban in this situation, choose the imperfective category. Do not turn this into a rule that every negative command has one category. Certain warnings about an unintended event use a different pattern. The check specifies a prohibition and excludes that warning reading.',[newexamples[3]['id']]),
]
teaching=[]
for n,(title,body,eids) in enumerate(bodies,1):
 sid=f'u15-st-teach-aspect-consolidate-{n}' if n<=3 else 'u15-st-teach-request-aspect-'+str(n-3)
 s=copy.deepcopy(steps['u15-st-teach-aspect-consolidate-1']);s.update(id=sid,title=title,body=body,exampleIds=eids);teaching.append(s)
 record('teach-'+str(n)+'-title',title);record('teach-'+str(n)+'-body',body)
guide=copy.deepcopy(steps['u15-st-guided-aspect-consolidate-1']);guide.update(id='u15-st-guided-request-aspect',title='Practise a recurring request category',body='Read the English situation and choose its aspect category. A miss returns to all pair and context teaching.',itemIds=[newitems[0]['id']]);record('guide-title',guide['title']);record('guide-body',guide['body'])
rem=copy.deepcopy(steps['u15-st-rem-aspect-consolidate']);rem.update(title='Review pair recognition and request context',body='\n\n'.join(s['body'] for s in teaching),exampleIds=list(dict.fromkeys(e for s in teaching for e in s['exampleIds'])));record('repair-title',rem['title']);record('repair-body',rem['body'])
check=copy.deepcopy(steps['u15-st-check-aspect-consolidate']);check['itemIds']+= [x['id'] for x in newitems[1:]];check['body']='Complete both preparatory partner checks and all three bounded English request-context checks. Pair recall alone does not complete this objective. A miss returns to the full explanation and both guided steps.';record('check-body',check['body'])
session['steps']=teaching+[steps['u15-st-guided-aspect-consolidate-1'],guide,rem,check];o['instructionStepIds']=['u15-st-orient']+[s['id'] for s in teaching];o['exampleIds']=rem['exampleIds'];o['practiceStepIds'].append(guide['id']);o['checkItemIds']=check['itemIds'][:];o['title']='Recognize supplied partners and bounded request-aspect contexts';session['title']=o['title'];record('scope-title',o['title'])
p['provenanceRecords']+=added
assert p['items'][:len(old['items'])]==old['items'];assert p['examples'][:len(old['examples'])]==old['examples'];assert p['provenanceRecords'][:len(old['provenanceRecords'])]==old['provenanceRecords']
assert p['units'][:14]+p['units'][15:]==old['units'][:14]+old['units'][15:]
rev=copy.deepcopy(p);rev['units'][14]=old['units'][14];rev['items']=old['items'];rev['examples']=old['examples'];rev['provenanceRecords']=old['provenanceRecords'];assert rev==old
f.write_text(dumps(p));out=Path('/workspace/artifacts/cc003-imperative');out.joinpath('CORRECTION.json').write_text(json.dumps(dict(baselineCommit=base,beforeAssetSha256=hashlib.sha256(raw).hexdigest(),afterAssetSha256=hashlib.sha256(f.read_bytes()).hexdigest(),beforeUnit=old['units'][14],afterUnit=u,addedItems=newitems,addedExamples=newexamples,addedProvenance=added,newContextPins=pins,all626Items821Examples7024RecordsExact=True,allOtherDataExact=True,formalAcceptance=False),ensure_ascii=False,indent=2)+'\n')
k=Path('app/src/main/java/dev/russiancompanion/v2/curriculum/CompleteRuleContextPins.kt');s=k.read_text();anchor='        "ca-rule-context:u8-g-availability-0:existence-v1"';assert anchor in s;s=s.replace(anchor,''.join('        "'+key+'" to "'+value+'",\n' for key,value in pins.items())+anchor,1)
anchor='            item.id == "u21-i-object-reference-2"';assert anchor in s;s=s.replace(anchor,'            item.id in setOf('+','.join('"'+i['id']+'"' for i in newitems)+') -> "obj-u15-aspect-consolidate"\n'+anchor,1);k.write_text(s)
print('Added4English context items/4worked examples;',len(added),'pending records;all existing data conserved')
