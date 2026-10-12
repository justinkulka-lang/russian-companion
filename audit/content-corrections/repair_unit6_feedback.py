#!/usr/bin/env python3
"""Explain 15 existing Unit6 contrasts; retain all scored and source tuples."""
import hashlib, json
from copy import deepcopy
from pathlib import Path
HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
PACK = ROOT / 'app/src/main/assets/content/v2-curriculum.json'
BASELINE = '37e7a806f44f690baea0339806608ad06355a68f'
def sha(s): return hashlib.sha256(s.encode()).hexdigest()
def main():
    raw = PACK.read_bytes(); p = json.loads(raw)
    if any(r['itemId']=='prov-ca-u6-feedback-u6-g-third-0' for r in p['provenanceRecords']):
        print('Already repaired; evidence unchanged'); return
    template = next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-u5-behavior-teach-1-body')
    bibliography = ('Retained Wikibooks Russian/Grammar/Accusative_case oldid4330404, catalog/sources/units-6-10/wikibooks/accusative-4330404.txt, CC BY-SA 4.0. '
        'Complete reviewed context: Usage I-love-you word-order pair (no claim about emphasis); Nouns dogs/cats subject-object reversal and animate plural accusative; '
        'Personal Pronouns nominative/accusative table rows I/me, We/us, You, He/him, She/her, It, They/them; '
        'Possessive pronouns final third-person paragraph, including invariance with the modified noun; '
        'Usage paragraph distinguishing destination with в/на + accusative from location with в/на + prepositional. '
        'Original source selections and Russian cues/options are retained. These are bounded recognition explanations, not productive-skill claims or universal movement-verb rules. '
        'Independently authored English on 2026-10-12 within content-audit-2026-10-08; no private Penguin prose or other primary prose copied; final semantic audit pending.')
    feedback = {
        'u6-g-third-0': 'его means his or its: the category refers to a masculine or neuter singular possessor. её refers to a feminine singular possessor, and их to plural possessors. These third-person possessive forms do not change with the gender or number of the possessed noun.',
        'u6-i-third-1': 'её means her: the category refers to a feminine singular possessor. его means his or its, and их means their. These third-person possessive forms do not change with the gender or number of the possessed noun.',
        'u6-i-third-2': 'их means their: the category refers to plural possessors. его and её refer to singular possessors. These third-person possessive forms do not change with the gender or number of the possessed noun.',
        'u6-g-object-0': 'Я тебя люблю places the object before the verb. Я люблю тебя places it after the verb. Both have the same meaning, I love you: the subject and object keep their roles and forms. The other option repeats the shown order.',
        'u6-i-object-1': 'Я люблю тебя places the object after the verb. Я тебя люблю places it before the verb. Both have the same meaning, I love you: the subject and object keep their roles and forms. The other option repeats the shown order.',
        'u6-i-object-2': 'Коты ненавидят собак. means cats hate dogs. The cats are the subject and the dogs the accusative object. Compared with the shown dogs hate cats line, both roles are reversed; this is not merely a change of word order. The other option repeats dogs hate cats.',
        'u6-i-object-3': 'Собаки ненавидят котов. means dogs hate cats. The dogs are the subject and the cats the accusative object. Compared with the shown cats hate dogs line, both roles are reversed; this is not merely a change of word order. The other option repeats cats hate dogs.',
        'u6-g-pronoun-0': 'Меня is the accusative object form for Я, I/me: first person singular. Нас is the form for we/us instead, first person plural. Match the same person and number across the nominative and accusative table columns.',
        'u6-i-pronoun-1': 'Нас is the accusative object form for Мы, we/us: first person plural. Меня is the form for I/me instead, first person singular. Match the same person and number across the nominative and accusative table columns.',
        'u6-i-pronoun-2': 'Её is the accusative object form for Она, she/her: third person singular feminine. Его is the form for he/him or it instead. Match the same person, number and gender across the nominative and accusative table columns.',
        'u6-i-pronoun-3': 'Их is the accusative object form for Они, they/them: third person plural. Вас is the form for plural or polite you instead, second person. Match the same person and number across the nominative and accusative table columns.',
        'u6-g-direction-0': 'Я шёл в дом describes walking into the house, a destination. Here the preposition takes the accusative, so the function is direction. Location instead describes being at a place; The same preposition with the prepositional case can express that different function.',
        'u6-i-direction-1': 'Я жил в Лондоне describes living in London, a location. Here the preposition takes the prepositional case. Direction instead describes a destination; The same preposition with the accusative can express that different function.',
        'u6-i-direction-2': 'Я шёл на улицу describes walking onto the street, a destination. Here the preposition takes the accusative, so the function is direction. Location instead describes being at a place; The same preposition with the prepositional case can express that different function.',
        'u6-i-direction-3': 'Оно на столе describes being on the table, a location. Here the preposition takes the prepositional case. Direction instead describes a destination; The same preposition with the accusative can express that different function.'
    }
    possessive = {'его':'his or its, a masculine or neuter singular possessor', 'её':'her, a feminine singular possessor', 'их':'their, plural possessors'}
    pronoun = {'Меня':'I/me, first person singular', 'Нас':'we/us, first person plural', 'Её':'she/her, third person singular feminine', 'Его':'he/him or it, third person singular masculine or neuter', 'Их':'they/them, third person plural', 'Вас':'plural or polite you, second person'}
    added=[]; changes=[]
    def record(text, item, name):
        r=deepcopy(template); original=bibliography+'\n'+text; objective=item['objectiveId']
        r.update(itemId='prov-ca-u6-feedback-'+name,unitNumber=6,objectiveId=objective,sourceRecordId='ca-coverage:'+objective+':feedback:'+name,
                 originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text))
        r['synthesis'].update(bibliography=bibliography,objectiveOrClaimBinding=objective+':feedback:'+name,
            authorshipDeclaration='Independently authored English on 2026-10-12 from retained complete source context; no copied primary prose or private book text.',applications=[])
        assert not any(x['itemId']==r['itemId'] for x in p['provenanceRecords'])
        p['provenanceRecords'].append(r); added.append(r['itemId'])
    for item in p['items']:
        if item['id'] not in feedback: continue
        assert item['feedback'].startswith('Does'); before=deepcopy(item)
        item['feedback']=feedback[item['id']]; item['assessment']['whyAnswerDemonstratesObjective']=item['feedback']
        rationales=[]
        for n, option in enumerate(item['options']):
            if option==item['answer']: rationale=''
            elif item['objectiveId']=='obj-u6-third': rationale=f'{option} identifies {possessive[option]} instead of the shown possessor category.'
            elif item['objectiveId']=='obj-u6-pronoun': rationale=f'{option} identifies {pronoun[option]} instead of the person, number or gender in the shown nominative form.'
            elif item['objectiveId']=='obj-u6-object':
                rationale=('This option repeats the shown order instead of moving the object before or after the verb.' if item['assessment']['testedFeature']=='OBJECT_ORDER' else 'This option repeats the shown subject and object roles instead of reversing who hates whom.')
            else:
                rationale=('Location describes being at a place instead of moving toward a destination in this example.' if option=='location' else 'Direction describes moving toward a destination instead of being at a place in this example.')
            rationales.append(rationale)
            if rationale: record(rationale,item,item['id']+f'-distractor-{n}')
        item['distractorRationales']=rationales;item['assessment']['distractorDifference']=rationales
        record(item['feedback'],item,item['id']);changes.append({'before':before,'after':deepcopy(item)})
    assert len(changes)==15
    after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode(); PACK.write_bytes(after)
    (HERE/'UNIT6_FEEDBACK_CORRECTION.json').write_text(json.dumps(dict(baselineCommit=BASELINE,beforeSha256=hashlib.sha256(raw).hexdigest(),afterSha256=hashlib.sha256(after).hexdigest(),addedProvenanceIds=added,changes=changes,bibliography=bibliography,semanticAcceptance=False),ensure_ascii=False,indent=2)+'\n')
    print('Explained 15 existing contrasts;',len(added),'pending coverage records')
if __name__=='__main__': main()
