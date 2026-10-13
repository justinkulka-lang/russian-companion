#!/usr/bin/env python3
"""Explain nine retained Unit11 past-tense and reflexive contrasts without changing scored tuples."""
import csv, hashlib, json
from copy import deepcopy
from pathlib import Path
HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
PACK = ROOT / 'app/src/main/assets/content/v2-curriculum.json'
BASELINE = '1464bd6419b8514b7133f0bc9954bc90710de6b9'
def sha(s): return hashlib.sha256(s.encode()).hexdigest()
def main():
    raw=PACK.read_bytes();p=json.loads(raw)
    if any(r['itemId']=='prov-ca-u11-feedback-u11-g-past-read-0' for r in p['provenanceRecords']):
        print('Already repaired; evidence unchanged');return
    template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-u5-behavior-teach-1-body')
    with (ROOT/'catalog/sources/units-3-5/openrussian/verbs.csv').open() as f:
        verbs={r['bare']:r for r in csv.DictReader(f,delimiter='\t')}
    expected={'читать':["чита'л","чита'ла","чита'ло","чита'ли"], 'быть':["бы'л","была'","бы'ло","бы'ли"], 'улыбаться':["улыба'лся","улыба'лась","улыба'лось","улыба'лись"], 'учиться':["учи'лся","учи'лась","учи'лось","учи'лись"]}
    for lemma, forms in expected.items():
        assert [verbs[lemma][k] for k in ['past_m','past_f','past_n','past_pl']]==forms
        assert verbs[lemma]['aspect']=='imperfective'
    bibliography=('Retained Wikibooks Russian/Grammar/Past tense oldid4567337, catalog/sources/units-11-15/wikibooks/past-tense-4567337.txt, CC BY-SA 4.0: complete Regular conjugation read table, To be past forms and Reflexive verbs smiling/studying examples. '
        'Retained OpenRussian commit50e210c4803237779cb562bc1abcea529066031c, catalog/sources/units-3-5/openrussian/verbs.csv, CC BY-SA 4.0: читать, быть, улыбаться, учиться past_m/past_f/past_n/past_pl; улыбаться accented infinitive. '
        'The Wikibooks To be paragraph incorrectly labels быть perfective; the retained lexical aspect field is imperfective. That incorrect source claim is not adopted. '
        'Bounded recognition of existing past forms and gender/number agreement; no aspect-to-English-tense equivalence, productive paradigm claim, new Russian text, or assertion that every reflexive verb means acting on oneself. '
        'All scored/source tuples and exact Russian spellings/capitalization remain unchanged. '
        'Independently authored English on 2026-10-13 within content-audit-2026-10-08; no copied primary prose or private Penguin text; final semantic audit pending.')
    corrections={
        'u11-g-past-read-0':('чита́ла is the feminine past form for a singular subject of the verb meaning to read. чита́ло is neuter singular and чита́ли is plural. The past form matches the subject in gender and number; choose feminine singular for this question.', ['', 'чита́ло is neuter singular past, not feminine singular.', 'чита́ли is plural past, not feminine singular.']),
        'u11-i-past-read-1':('чита́л is the masculine past form for a singular subject of the verb meaning to read. чита́ли is plural past. The cue shows the feminine singular form, but the question asks for masculine singular of the same verb.', ['', 'чита́ли is plural past, not masculine singular.']),
        'u11-i-past-read-2':('Она чита́ла means she was reading: the feminine singular subject matches the feminine past verb. Они чита́ли means they were reading and uses plural agreement. The question asks for the feminine singular line, not the plural line.', ['', 'Они чита́ли has a plural subject and plural past verb, rather than feminine singular agreement.']),
        'u11-g-past-be-0':('была́ is the feminine past form for a singular subject meaning was. бы́ло is neuter singular and бы́ли is plural. Choose the past form with the feminine singular agreement requested here.', ['', 'бы́ло is neuter singular past, not feminine singular.', 'бы́ли is plural past, not feminine singular.']),
        'u11-i-past-be-1':('бы́ли is the plural past form meaning were. бы́л is masculine singular past. The cue shows feminine singular, but the requested row is plural; past agreement follows the subject number.', ['', 'бы́л is masculine singular past, not the plural past requested here.']),
        'u11-i-past-be-2':('бы́ли is the plural past form meaning were. бы́л is masculine singular past. The cue shows neuter singular, but the question asks for the plural form of the same verb.', ['', 'бы́л is masculine singular past, not the plural past requested here.']),
        'u11-g-reflexive-0':('улыба́лась is the feminine past form for a singular subject of the verb meaning to smile. улыба́лись is plural past, while улыба́ться is the infinitive, not a past form. The reflexive ending remains on the past verb; choose feminine singular agreement.', ['', 'улыба́лись is plural past, not feminine singular.', 'улыба́ться is the infinitive meaning to smile, not a past-tense form.']),
        'u11-i-reflexive-1':('Она улыба́лась means she was smiling, with feminine past agreement between the singular subject and verb. Они улыба́лись means they were smiling and has plural agreement. Both verbs keep their reflexive ending, but only the first line is feminine singular.', ['', 'Они улыба́лись has a plural subject and plural past verb, rather than feminine singular agreement.']),
        'u11-i-reflexive-2':('учи́лась is the feminine past form for a singular subject of the verb meaning to study. учи́лись is plural past. Both retain the reflexive ending, so that ending alone does not distinguish them; the question requires feminine singular agreement.', ['', 'учи́лись is plural past, not feminine singular.'])}
    added=[];changes=[]
    def record(text,item,name):
        r=deepcopy(template);original=bibliography+'\n'+text;objective=item['objectiveId']
        r.update(itemId='prov-ca-u11-feedback-'+name,unitNumber=11,objectiveId=objective,sourceRecordId='ca-coverage:'+objective+':feedback:'+name,
            originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text))
        r['synthesis'].update(bibliography=bibliography,objectiveOrClaimBinding=objective+':feedback:'+name,
            authorshipDeclaration='Independently authored English on 2026-10-13 from retained complete grammar context and lexical fields; no copied primary prose or private book text.',applications=[])
        assert not any(x['itemId']==r['itemId'] for x in p['provenanceRecords'])
        p['provenanceRecords'].append(r);added.append(r['itemId'])
    for item in p['items']:
        if item['id'] not in corrections:continue
        before=deepcopy(item);assert item['feedback'].startswith('Does');feedback,rationales=corrections[item['id']]
        item.update(feedback=feedback,distractorRationales=rationales)
        item['assessment'].update(whyAnswerDemonstratesObjective=feedback,distractorDifference=rationales)
        record(feedback,item,item['id'])
        for n,rationale in enumerate(rationales):
            if rationale:record(rationale,item,item['id']+f'-distractor-{n}')
        changes.append({'before':before,'after':deepcopy(item)})
    assert len(changes)==9 and len(added)==21
    after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode();PACK.write_bytes(after)
    (HERE/'UNIT11_FEEDBACK_CORRECTION.json').write_text(json.dumps(dict(baselineCommit=BASELINE,beforeSha256=hashlib.sha256(raw).hexdigest(),afterSha256=hashlib.sha256(after).hexdigest(),addedProvenanceIds=added,changes=changes,bibliography=bibliography,semanticAcceptance=False),ensure_ascii=False,indent=2)+'\n')
    print('Explained nine source-bound contrasts; twenty-one pending coverage records')
if __name__=='__main__':main()
