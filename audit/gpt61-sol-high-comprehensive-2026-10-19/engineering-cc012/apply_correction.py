import sys
sys.dont_write_bytecode=True
sys.path.insert(0,__import__("os").getcwd())
import copy,hashlib,json,subprocess
from pathlib import Path
from catalog.curriculum_storage import loads,dumps
r=Path.cwd();asset=r/'app/src/main/assets/content/v2-curriculum.json';raw=asset.read_bytes();base='7c369a8ce8adf7571046ec83331de0630765b759'
assert raw==subprocess.check_output(['git','show',base+':app/src/main/assets/content/v2-curriculum.json'])
old=loads(raw);p=copy.deepcopy(old);u=p['units'][7];o=next(x for x in u['objectives'] if x['id']=='obj-u8-availability');session=next(x for x in u['sessions'] if x['id']=='u8-s-availability');steps={s['id']:s for s in session['steps']};items={i['id']:i for i in p['items']};rs={q['itemId']:q for q in p['provenanceRecords']};sha=lambda s:hashlib.sha256(s.encode()).hexdigest()
source=Path('catalog/sources/units-6-10/wikibooks/genitive-4567338.txt').read_text()
examples=[next(e for e in p['examples'] if e['id']==f'ex-u8-availability-{i}') for i in [0,1]]
for e in examples:assert e['display'] in source and e['note'] in source
cue0,cue1=[e['display'] for e in examples];gloss0,gloss1=[e['note'] for e in examples]
bib=('Complete retained Wikibooks Russian/Grammar/Genitive oldid4567338, https://en.wikibooks.org/wiki/Russian/Grammar/Genitive?oldid=4567338, '
 'positive existence subsection: the existence word can state there is/are and the noun naming what exists is nominative. '
 'Use only the two separately admitted positive Unit8 native examples and their exact existing source translations. '
 'The prepositional case has already been introduced inUnit5; it is not the case of the noun naming what exists in these examples. '
 'Independent English explanation/contrast on2026-10-19 within content-audit-2026-10-08. '
 'No private-book text, new Russian/stress/phrase or generated native application. Original licensed native evidence retains its attribution. '
 'Qualified source/linguistic/rights review remains pending.')
template=copy.deepcopy(next(q for q in p['provenanceRecords'] if q['sourceId']=='src-content-audit-research' and q.get('synthesis')))
added=[];pins={}
def record(binding,text,context=None,original=None,application=None):
 q=copy.deepcopy(template);original=original or bib+'\n'+text
 q.update(itemId='prov-cc012-'+binding,unitNumber=8,objectiveId=o['id'],recordType='label',sourceRecordId=context or 'ca-coverage:'+o['id']+':cc012:'+binding,originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text))
 q['synthesis']={'bibliography':bib,'sourceDateOrVersion':'2026-10-08','authorshipDeclaration':'Independently authored English on2026-10-19; native strings retain their separately admitted Wikibooks provenance. No private-book text.','objectiveOrClaimBinding':o['id']+':cc012:'+binding,'applications':[] if application is None else [application]}
 for k in ['reviewerIdentity','reviewerQualification','reviewDate','reviewedDigest']:q[k]=''
 added.append(q);return q['itemId']
def field(s):return str(len(s.encode()))+':'+s
function_options=['The line states that something exists at the named place.','The line asks where something is.']
case_options=['The noun naming what exists is in the nominative case.','The noun naming what exists is in the prepositional case.']
specs=[('u8-g-availability-0',cue0,'Which function does the shown line have?',function_options,function_options[0],
 'The supplied line states that Frenchmen exist on this street; it is a statement, not a location question.',
 'This alternative describes a question, while the supplied line makes a positive statement.','POSITIVE_EXISTENCE_FUNCTION'),
 ('u8-i-availability-1',cue1,'Which function does the shown line have?',function_options,function_options[0],
 'The supplied line states that old buildings exist in Moscow; it is a statement, not a location question.',
 'This alternative describes a question, while the supplied line makes a positive statement.','POSITIVE_EXISTENCE_FUNCTION'),
 ('u8-i-availability-2',cue0,'Which supplied English meaning matches the whole line?',[gloss0,gloss1],gloss0,
 'The source pairs this line with '+gloss0+'. The other source translation concerns different things in a different place.',
 'The other supplied meaning concerns old buildings in Moscow rather than Frenchmen on this street.','POSITIVE_EXISTENCE_MEANING'),
 ('u8-i-availability-3',cue1,'Which case names what exists in this supplied positive line?',case_options,case_options[0],
 'The noun naming the old buildings is nominative in this positive existence statement. The location phrase is a separate part of the line.',
 'This alternative confuses the location phrase with the noun naming what exists.','POSITIVE_EXISTENCE_NOMINATIVE')]
for iid,cue,prompt,options,answer,feedback,rationale,feature in specs:
 item=items[iid];app=dict(cue=cue,answer=answer,distractors=[x for x in options if x!=answer]);canonical='tuple-v1:1;'+field(cue)+field(answer)+str(len(app['distractors']))+';'+''.join(field(x) for x in app['distractors']);context='ca-rule-context:'+iid+':existence-v1';original=bib+'\nContext explanation:\n'+feedback+'\nTuple-v1:'+canonical;pins[context]=sha(original)
 bound=[record(iid+'-'+str(i),text,context,original,app) for i,text in enumerate([cue,answer]+app['distractors'])]
 reasons=['' if v==answer else rationale for v in options];dids=['' if v==answer else bound[2+app['distractors'].index(v)] for v in options]
 item.update(prompt=prompt,promptLetter=cue,options=options,answer=answer,feedback=feedback,distractorRationales=reasons)
 item['assessment'].update(testedFeature=feature,answerSourceIds=[bound[1]],distractorSourceIds=dids,distractorDifference=reasons,whyAnswerDemonstratesObjective=feedback,contrastMembers=options,relationParticipantIds=bound)
 for key,text in [('prompt',prompt),('feedback',feedback),('rationale',rationale)]:record(iid+'-'+key,text)
o['title']='Recognize supplied positive existence statements';o['exampleIds']=[e['id'] for e in examples];session['title']=o['title'];record('objective-title',o['title'])
bodies=[('Presence in the supplied street example',cue0+' — '+gloss0+'. This positive line states that something exists at the named place. It is a statement, not a question asking where something is. Recognize what exists and where the line places it.'),
 ('Read the supplied Moscow statement',cue1+' — '+gloss1+'. This line states that old buildings exist in Moscow. Compare its full meaning with the street example: the things and the named place differ. Keep each whole source line with its own supplied translation.'),
 ('Identify the noun naming what exists','In both supplied positive statements, the noun naming what exists is nominative. Distinguish that noun from the location phrase. The previously introduced prepositional case is not the case of the noun naming what exists here. These two supplied examples do not establish an unrestricted sentence-generation rule.')]
for i,(title,body) in enumerate(bodies,1):
 s=steps[f'u8-st-teach-availability-{i}'];s.update(title=title,body=body,exampleIds=[examples[i-1]['id']] if i<3 else [])
 record('teach-'+str(i)+'-title',title);record('teach-'+str(i)+'-body',body)
for stem in ['guided','check']:
 s=steps['u8-st-'+stem+'-availability'+('-1' if stem=='guided' else '')];s['body']='Read the whole positive statement and choose the requested function, supplied meaning or noun case. A miss returns to all three teaching cards and both worked examples.';record(stem+'-body',s['body'])
s=steps['u8-st-rem-availability'];s.update(title='Review the supplied positive statements',body='\n\n'.join(body for title,body in bodies),exampleIds=o['exampleIds'][:]);record('repair-title',s['title']);record('repair-body',s['body'])
p['provenanceRecords']+=added
assert p['examples']==old['examples'];assert p['provenanceRecords'][:-len(added)]==old['provenanceRecords']
assert [x for x in p['items'] if x['id'] not in [s[0] for s in specs]]==[x for x in old['items'] if x['id'] not in [s[0] for s in specs]]
assert p['units'][:7]+p['units'][8:]==old['units'][:7]+old['units'][8:]
# Exact inverse declared delta, including full Unit8 and four mutable item objects.
rev=copy.deepcopy(p);rev['units'][7]=old['units'][7];rev['items']=old['items'];rev['provenanceRecords']=old['provenanceRecords'];assert rev==old
asset.write_text(dumps(p));out=Path('/workspace/artifacts/cc012-sequencing');out.joinpath('CORRECTION.json').write_text(json.dumps(dict(baselineCommit=base,beforeAssetSha256=hashlib.sha256(raw).hexdigest(),afterAssetSha256=hashlib.sha256(asset.read_bytes()).hexdigest(),beforeUnit=old['units'][7],afterUnit=u,changedItems=[dict(id=i,before=next(x for x in old['items'] if x['id']==i),after=items[i]) for i,*_ in specs],addedProvenance=added,newContextPins=pins,old6989ProvenanceAnd821ExamplesPreserved=True,allOtherDataExact=True,formalAcceptance=False),ensure_ascii=False,indent=2)+'\n')
# Add a current-context mapping while retaining every old immutable context/hash for evidence.
f=Path('app/src/main/java/dev/russiancompanion/v2/curriculum/CompleteRuleContextPins.kt');s=f.read_text();anchor='    fun required(itemId: String)';idx=s.index(anchor)
mapping='    // Current Unit8 scope replaces analyzed absence; original context pins remain immutable evidence.\n    private val currentUnit8Contexts = mapOf(\n'+''.join('        "'+iid+'" to "ca-rule-context:'+iid+':existence-v1",\n' for iid,*_ in specs)+'    )\n'
s=s[:idx]+mapping+s[idx:];s=s.replace('    private val hashes = mapOf(', '    private val hashes = mapOf(') # insertion checked below
idx=s.index('        "ca-rule-context:u8-g-availability-0"');s=s[:idx]+''.join('        "'+key+'" to "'+value+'",\n' for key,value in pins.items())+s[idx:]
s=s.replace('it.sourceRecordId == "ca-rule-context:${item.id}"','it.sourceRecordId == (currentUnit8Contexts[item.id] ?: "ca-rule-context:${item.id}")');f.write_text(s)
print('Bound Unit8 to two existing positive lines;4mutable items;all6989old records retained;',len(added),'new pending records')
