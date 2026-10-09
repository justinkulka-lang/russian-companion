#!/usr/bin/env python3
"""Remove an unsupported case classification from an existing absence check."""
import hashlib
import json
from copy import deepcopy
from pathlib import Path

HERE=Path(__file__).resolve().parent
PACK=HERE.parents[1]/'app/src/main/assets/content/v2-curriculum.json'
OBJECTIVE='obj-u24-stand-ins'
def sha(text): return hashlib.sha256(text.encode()).hexdigest()
def main():
    before=PACK.read_bytes();data=json.loads(before)
    item=next(i for i in data['items'] if i['id']=='u24-i-stand-ins-2')
    if item['assessment']['testedFeature']=='NEGATIVE_ABSENCE_NEKOGO':
        print('Already repaired; evidence unchanged');return
    assert item['assessment']['testedFeature']=='GENITIVE_ABSENCE_NEKOGO'
    assert item['promptLetter']=='не́кого спросить'
    assert item['answer']=='there is nobody to ask'
    template=next(r for r in data['provenanceRecords'] if r['itemId']==item['assessment']['answerSourceIds'][0])
    bibliography=(
        'Existing admitted construction and meaning: '+template['itemId']+'. '
        'Gramota dictionary search for некого: https://gramota.ru/poisk?mode=slovari&query=некого; '
        'Gramota reference answer 317459, 2024-09-26: https://gramota.ru/spravka/vopros/317459. '
        'Search excerpts checked 2026-10-09; direct page retrieval returned HTTP 403. '
        'The reference distinguishes a missing nominative from genitive/accusative form identity. '
        'Follow-up inference: shape and absence meaning alone do not identify a case in this English meaning check. '
        'This repair removes the unsupported genitive classification and retains the existing Russian construction. '
        'Independently authored 2026-10-09 within the content-audit-2026-10-08 batch; final semantic audit pending.'
    )
    added=[]
    def record(text,name,applications=None):
        r=deepcopy(template);binding=OBJECTIVE+':NEGATIVE_ABSENCE_NEKOGO'
        original=bibliography+'\n'+('\n'.join([item['promptLetter']]+item['options']) if applications else text)
        r.update(itemId='prov-ca-u24-absence-'+name,recordType='label',sourceRecordId='ca-contrast:u24-absence' if applications else 'ca-coverage:'+OBJECTIVE+':'+name,
                 originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text))
        r['synthesis'].update(bibliography=bibliography,objectiveOrClaimBinding=binding,
            authorshipDeclaration='Independently authored for Russian Companion on 2026-10-09; no new Russian form is introduced.',
            applications=applications or [])
        assert not any(x['itemId']==r['itemId'] for x in data['provenanceRecords'])
        data['provenanceRecords'].append(r);added.append(r['itemId']);return r['itemId']
    item['prompt']='Choose the English description. Which reading expresses absence of a person available for the action?'
    item['assessment']['testedFeature']='NEGATIVE_ABSENCE_NEKOGO'
    application=[dict(cue=item['promptLetter'],answer=item['answer'],distractors=[v for v in item['options'] if v!=item['answer']])]
    values=[item['promptLetter']]+item['options']
    ids={value:record(value,'participant-'+str(n),application) for n,value in enumerate(values)}
    item['assessment']['answerSourceIds']=[ids[item['answer']]]
    item['assessment']['distractorSourceIds']=['' if v==item['answer'] else ids[v] for v in item['options']]
    item['assessment']['relationParticipantIds']=[ids[value] for value in values]
    record(item['prompt'],'prompt')
    example=next(e for e in data['examples'] if e['id']=='ex-u24-stand-ins-2')
    example['note']='there is nobody to ask; negative absence construction'
    record(example['note'],'example-note')
    unit=next(u for u in data['units'] if u['number']==24)
    session=next(s for s in unit['sessions'] if s['id']=='u24-s-stand-ins')
    explanation=('не́кто is an indefinite pronoun, a person whose identity is not specified. '
        'It is not the predicative negative of кто. Absence of a person uses a case form: '
        'не́кого has the same form in the genitive and accusative, while не́кому is dative and не́кем instrumental. '
        'The sentence determines the case; absence meaning alone does not. There is no nominative form in this negative paradigm.')
    for step in session['steps']:
        if step['id'] in ('u24-st-teach-stand-ins-3','u24-st-rem-stand-ins'):step['body']=explanation
        if step['id']=='u24-st-rem-stand-ins':step['exampleIds']=['ex-u24-stand-ins-0','ex-u24-stand-ins-1','ex-u24-stand-ins-2']
    record(explanation,'explanation')
    assert sum(s['kind']=='GUIDED' for s in session['steps'])==1
    session['steps']=[s for s in session['steps'] if s['kind']=='TEACH']+[s for s in session['steps'] if s['kind']!='TEACH']
    after=(json.dumps(data,ensure_ascii=False,indent=2)+'\n').encode();PACK.write_bytes(after)
    evidence=dict(schemaVersion=1,findingId='CA001',unitNumber=24,disposition='IMPLEMENTED_PENDING_FINAL_AUDIT',
        beforeCurriculumFileSha256=hashlib.sha256(before).hexdigest(),afterCurriculumFileSha256=hashlib.sha256(after).hexdigest(),
        addedProvenanceIds=added,keysAndOptionsUnchanged=True,advancementUnchanged=True,primaryPageRetrieval='HTTP_403_SEARCH_EVIDENCE_ONLY',
        bibliography=bibliography)
    (HERE/'UNIT24_ABSENCE_CORRECTION.json').write_text(json.dumps(evidence,ensure_ascii=False,indent=2)+'\n')
    print('Removed unsupported genitive classification; keys and gates unchanged')
if __name__=='__main__':main()
