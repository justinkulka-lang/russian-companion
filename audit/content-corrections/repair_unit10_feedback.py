#!/usr/bin/env python3
"""Explain two retained Unit10 pronoun and animate-plural contrasts without changing scored tuples."""
import csv, hashlib, json
from copy import deepcopy
from pathlib import Path
HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
PACK = ROOT / 'app/src/main/assets/content/v2-curriculum.json'
BASELINE = '80c106875c36f2e11911c7ebdcd5f8d39fd330db'
def sha(s): return hashlib.sha256(s.encode()).hexdigest()
def main():
    raw=PACK.read_bytes();p=json.loads(raw)
    if any(r['itemId']=='prov-ca-u10-feedback-u10-g-pronoun-0' for r in p['provenanceRecords']):
        print('Already repaired; evidence unchanged');return
    template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-u5-behavior-teach-1-body')
    bibliography=('Retained Wikibooks CC BY-SA 4.0: Russian/Grammar/Genitive oldid4567338, catalog/sources/units-6-10/wikibooks/genitive-4567338.txt, complete Personal Pronouns table and following preposition note; '
        'Russian/Pronouns oldid4585698, catalog/sources/units-3-5/wikibooks/pronouns-4585698.txt, complete Formality and Personal sections; '
        'Russian/Personal Pronouns oldid3598527, catalog/sources/units-3-5/wikibooks/personal-pronouns-3598527.txt, complete person/number table; '
        'Russian/Grammar/Accusative_case oldid4330404, catalog/sources/units-6-10/wikibooks/accusative-4330404.txt, Nouns animate-plural subject/object examples and Adjectives agreement context. '
        'Bare genitive table-form recognition, not a claim that third-person forms remain unchanged after prepositions. '
        'Animate object is syntactically accusative, with genitive-like forms, not syntactically genitive. '
        'All scored/source tuples and exact Russian spellings/capitalization remain unchanged. '
        'Independently authored English on 2026-10-13 within content-audit-2026-10-08; no copied primary prose or private Penguin text; final semantic audit pending.')
    corrections={
        'u10-g-pronoun-0':('Меня́ is the genitive form of Я, meaning I. Нас is the genitive form of Мы, meaning we. Keep the same first-person singular referent when moving to the genitive row.', ['', 'Нас belongs to Мы (we), first-person plural, rather than Я (I), first-person singular.']),
        'u10-i-pronoun-1':('Нас is the genitive form of Мы, meaning we. Меня́ belongs to Я, meaning I. Both are first person, but the cue refers to the plural group, not one speaker.', ['', 'Меня́ belongs to Я (I), first-person singular, rather than Мы (we), first-person plural.']),
        'u10-i-pronoun-2':('Тебя́ is the genitive form of Ты, the singular informal you. Вас belongs to Вы, used for plural you or polite address to one person. Match the shown pronoun, rather than replacing it with the other you form.', ['', 'Вас belongs to Вы (plural or polite you), not the singular informal Ты shown here.']),
        'u10-i-pronoun-3':('Вас is the genitive form of Вы, used for plural you or polite address to one person. Тебя́ belongs to Ты, singular informal you. Preserve the shown pronoun and its form of address.', ['', 'Тебя́ belongs to Ты (singular informal you), not the plural or polite Вы shown here.']),
        'u10-i-pronoun-4':('Их is the bare genitive table form of Они, meaning they. Нас belongs to Мы, meaning we. Both refer to a plural group, but they is third person and we is first person. This checks the bare table form; third-person forms can change after prepositions.', ['', 'Нас belongs to Мы (we), first-person plural, rather than Они (they), third-person plural.']),
        'u10-g-animate-0':('этих женщин is the animate plural accusative object phrase in the source example meaning I like these women. Its forms match the genitive plural. Эти женщины is the nominative subject phrase in the example meaning These women like me. Choose the object form, not the subject form.', ['', 'Эти женщины is the nominative plural subject phrase, rather than the animate accusative object phrase requested here.']),
        'u10-i-animate-1':('Эти женщины is the nominative plural subject phrase in the source example meaning These women like me. The shown этих женщин is the animate plural accusative object phrase in I like these women, with genitive-like forms. The other row requested here is the nominative subject form.', ['', 'этих женщин repeats the animate plural accusative object phrase shown in the cue; the requested other row is the nominative subject phrase.'])}
    added=[];changes=[]
    def record(text,item,name):
        r=deepcopy(template);original=bibliography+'\n'+text;objective=item['objectiveId']
        r.update(itemId='prov-ca-u10-feedback-'+name,unitNumber=10,objectiveId=objective,sourceRecordId='ca-coverage:'+objective+':feedback:'+name,
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
    assert len(changes)==7 and len(added)==14
    after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode();PACK.write_bytes(after)
    (HERE/'UNIT10_FEEDBACK_CORRECTION.json').write_text(json.dumps(dict(baselineCommit=BASELINE,beforeSha256=hashlib.sha256(raw).hexdigest(),afterSha256=hashlib.sha256(after).hexdigest(),addedProvenanceIds=added,changes=changes,bibliography=bibliography,semanticAcceptance=False),ensure_ascii=False,indent=2)+'\n')
    print('Explained seven source-bound contrasts; fourteen pending coverage records')
if __name__=='__main__':main()
