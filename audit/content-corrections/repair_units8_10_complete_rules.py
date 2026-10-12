#!/usr/bin/env python3
"""Admit complete, bounded presence/absence/possession explanations; preserve original excerpts."""
import hashlib, json
from copy import deepcopy
from pathlib import Path
HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
PACK = ROOT/'app/src/main/assets/content/v2-curriculum.json'
PRESENCE = 'In this line, the thing that exists is in the nominative case.'
ABSENCE = 'In this line, the absent thing is in the genitive case.'
POSSESSION = 'In this line, the possessor is genitive and the possessed thing is nominative.'
OBJECTIVES = ['obj-u8-availability','obj-u10-have','obj-u10-absence']
def sha(s): return hashlib.sha256(s.encode()).hexdigest()
def canonical(applications):
    def field(s): return str(len(s.encode()))+':'+s
    out='tuple-v1:'+str(len(applications))+';'
    for a in applications:
        out+=field(a['cue'])+field(a['answer'])+str(len(a['distractors']))+';'+''.join(field(v) for v in a['distractors'])
    return out
def main():
    raw=PACK.read_bytes();p=json.loads(raw)
    if any(r['itemId']=='prov-ca-rule-u8-g-availability-0-0' for r in p['provenanceRecords']):
        print('Already repaired; retained evidence unchanged');return
    source=ROOT/'catalog/sources/units-6-10/wikibooks/genitive-4567338.txt'
    text=source.read_text()
    assert "'''нет''' followed by the genitive case" in text
    assert 'The thing that is possessed remains in the nominative' in text
    assert "'''Есть''' on its own" in text and 'It is followed by the nominative case' in text
    template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-u5-behavior-teach-1-body')
    bibliography=('Retained Wikibooks Russian/Grammar/Genitive oldid4567338, https://en.wikibooks.org/wiki/Russian/Grammar/Genitive?oldid=4567338, '
        'Possession and Negation section: у calls the genitive for the possessor, the possessed thing in the simple у + genitive + есть + nominative pattern is nominative; '
        'есть for existence is followed by nominative; нет for absence is followed by genitive. The retained full source supplies the word and referents missing from earlier excerpts. '
        'Apply only these bounded rules to previously admitted Russian cues. Quantity phrases are not generalized as simple nominative objects. '
        'Independent English synthesis on2026-10-12 within content-audit-2026-10-08; no source prose or private Penguin prose copied; qualified semantic audit pending.')
    added=[];changed=[];pins={}
    def record(value,obj,name,original=None,applications=None,record_id=None):
        r=deepcopy(template);original=original or bibliography+'\n'+value
        r.update(itemId='prov-ca-rule-'+name,unitNumber=int(obj.split('-')[1][1:]),objectiveId=obj,recordType='label',
            sourceRecordId=record_id or 'ca-coverage:'+obj+':complete-rule:'+name,originalSourceText=original,shippedText=value,
            sourceHash=sha(original),contentHash=sha(value))
        r['synthesis'].update(bibliography=bibliography,authorshipDeclaration='Independently authored English,2026-10-12, applying retained grammar to previously admitted examples; no primary or owned-book prose.',
            objectiveOrClaimBinding=obj+':complete-rule:'+name,applications=applications or [])
        assert not any(x['itemId']==r['itemId'] for x in p['provenanceRecords']);p['provenanceRecords'].append(r);added.append(r['itemId']);return r['itemId']
    for item in p['items']:
        if item['objectiveId'] not in OBJECTIVES:continue
        before=deepcopy(item);obj=item['objectiveId'];is_absence='нет' in item['promptLetter'].lower()
        positive=PRESENCE if obj=='obj-u8-availability' else POSSESSION
        answer=ABSENCE if is_absence else positive;other=positive if is_absence else ABSENCE
        item.update(kind='CONTRAST_SELECTION',prompt='Which explanation accounts for the noun cases in the shown line?',options=[positive,ABSENCE],answer=answer,sourceId=template['sourceId'])
        feedback=(f"{item['promptLetter']} expresses absence. The noun naming the absent thing is genitive. The presence or simple possession explanation would assert that the thing exists or is possessed, which this line does not assert." if is_absence else
            (f"{item['promptLetter']} expresses presence. The noun naming what exists is nominative. The genitive absence explanation would describe absence, which this line does not assert." if obj=='obj-u8-availability' else
            f"{item['promptLetter']} uses the simple possession construction. The possessor after the possession preposition is genitive; the possessed thing or slot is nominative. The absence explanation would deny possession, which this line does not do."))
        rationale=('The other explanation asserts presence or simple possession rather than the absence shown here.' if is_absence else 'The other explanation describes absence rather than the presence or simple possession shown here.')
        item.update(feedback=feedback,distractorRationales=['' if o==answer else rationale for o in item['options']])
        app=dict(cue=item['promptLetter'],answer=answer,distractors=[other]);rid='ca-rule-context:'+item['id']
        original=bibliography+'\nContext explanation:\n'+feedback+'\nTuple-v1:'+canonical([app])
        ids=[record(v,obj,item['id']+'-'+str(n),original,[app],rid) for n,v in enumerate([item['promptLetter'],answer,other])]
        a=item['assessment'];a.update(evidenceMode='STATED_APPLICATION',relationKind='STATED_APPLICATION',relationParticipantIds=ids,answerSourceIds=[ids[1]],
            distractorSourceIds=['' if o==answer else ids[2] for o in item['options']],distractorDifference=item['distractorRationales'],contrastMembers=item['options'],whyAnswerDemonstratesObjective=feedback)
        record(item['prompt'],obj,item['id']+'-prompt');record(feedback,obj,item['id']+'-feedback');record(rationale,obj,item['id']+'-rationale')
        pins[rid]=dict(sourceHash=sha(original),applications=[app],objectiveId=obj)
        changed.append(dict(before=before,after=deepcopy(item)))
    lessons={
        'u8-s-availability':[
            ('Presence and the nominative','Use the presence construction when the line says that something exists. In these supplied examples, the noun naming what exists is nominative. Read the Russian line and identify whether it states presence.'),
            ('Absence and the genitive','Use the absence construction when the line says that something does not exist. In these supplied examples, the absence word is followed by a genitive noun. Presence and absence require different case explanations.'),
            ('Compare the complete examples','Compare the positive and negative lines below. For a positive line choose the nominative presence explanation; for a negative line choose the genitive absence explanation.')],
        'u10-s-have':[
            ('The possessor and the possessed thing','In the simple possession pattern shown here, the possessor after the possession preposition is genitive. The thing possessed is nominative. The schematic slot in the first example represents that possessed thing.'),
            ('Possession versus absence','A simple positive possession line places the possessed thing in the nominative. An absence line places the absent thing in the genitive. Decide whether the whole shown line affirms or denies possession.'),
            ('Positive statements and questions','The son examples keep the same simple possession pattern in a statement or question: genitive possessor and nominative possessed thing. The form of a question does not turn the noun into an absence construction.'),
            ('Keep quantity and absence separate','The many-friends example includes a quantity expression. Quantity patterns are separate from the simple nominative possessed thing in the son checks. The no-son example below denies possession and uses the genitive for the absent thing.'),
            ('Another absence example','The no-money example also denies possession. Choose the genitive absence explanation, rather than the simple positive possession explanation.')],
        'u10-s-absence':[
            ('Identify absence','In an absence line, the absent thing is genitive. An absence line may describe what a person does not have or what does not exist at a place.'),
            ('Choose a complete case explanation','The absence construction uses a genitive noun for the absent thing. The simple positive possession construction instead uses a genitive possessor and a nominative possessed thing. Choose from the meaning and construction of the whole supplied line.'),
            ('Apply the absence pattern','Both negative examples below use the genitive for the absent thing. Keep the absent thing separate from any possessor or place mentioned in the line.'),
            ('Compare with positive possession','The positive son example below affirms possession. Its possessed noun is nominative, so the genitive absence explanation does not classify this line.')],
    }
    for unit in p['units']:
        for session in unit['sessions']:
            if session['id'] not in lessons:continue
            obj=next(o['id'] for o in unit['objectives'] if any(id in o['instructionStepIds'] for id in [x['id'] for x in session['steps'] if x['kind']=='TEACH']))
            teaching=[x for x in session['steps'] if x['kind']=='TEACH'];assert len(teaching)==len(lessons[session['id']])
            for st,(title,body) in zip(teaching,lessons[session['id']]):
                old=deepcopy(st);examples=st['body'] if st.get('exampleIds') else ''
                st.update(title=title,body=body+(' '+examples if examples else ''));changed.append(dict(before=old,after=deepcopy(st)))
                record(title,obj,st['id']+'-title');record(st['body'],obj,st['id']+'-body')
            session['steps']=teaching+[st for st in session['steps'] if st['kind']!='TEACH']
            rem=next(st for st in session['steps'] if st['kind']=='REMEDIATE');old=deepcopy(rem)
            rem.update(title='Review the complete constructions',body='\n\n'.join(st['body'] for st in teaching),exampleIds=list(dict.fromkeys(id for st in teaching for id in st.get('exampleIds',[]))))
            changed.append(dict(before=old,after=deepcopy(rem)));record(rem['title'],obj,rem['id']+'-title');record(rem['body'],obj,rem['id']+'-body')
            for st in session['steps']:
                if st['kind'] in ['GUIDED','CHECK']:
                    old=deepcopy(st);st['body']='Read the whole shown line, then choose the complete case explanation. A missed check returns to all teaching and examples.'
                    changed.append(dict(before=old,after=deepcopy(st)));record(st['body'],obj,st['id']+'-body')
    after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode();PACK.write_bytes(after)
    (HERE/'UNITS8_10_COMPLETE_RULE_CORRECTION.json').write_text(json.dumps(dict(baselineCommit='3fc46829970055f25394d910d6884ee9acc08e41',beforeSha256=hashlib.sha256(raw).hexdigest(),afterSha256=hashlib.sha256(after).hexdigest(),frozenPublicSource=str(source.relative_to(ROOT)),frozenPublicSourceSha256=hashlib.sha256(source.read_bytes()).hexdigest(),addedProvenanceIds=added,changes=changed,contextIdentities=pins,bibliography=bibliography,semanticAcceptance=False),ensure_ascii=False,indent=2)+'\n')
    print('Corrected14 complete rule choices and three lessons;',len(added),'pending records')
if __name__=='__main__':main()
