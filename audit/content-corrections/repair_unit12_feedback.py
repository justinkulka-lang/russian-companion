#!/usr/bin/env python3
"""Explain fifteen retained Unit12 future, case and spelling contrasts without changing scored tuples."""
import csv, hashlib, json
from copy import deepcopy
from pathlib import Path
HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
PACK = ROOT / 'app/src/main/assets/content/v2-curriculum.json'
BASELINE = '84b4141dc206c0317dd896d122f8570e8dbf932f'
def sha(s): return hashlib.sha256(s.encode()).hexdigest()
def main():
    raw=PACK.read_bytes();p=json.loads(raw)
    if any(r['itemId']=='prov-ca-u12-feedback-u12-g-future-imp-0' for r in p['provenanceRecords']):
        print('Already repaired; evidence unchanged');return
    template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-u5-behavior-teach-1-body')
    def lexical(path):
        with (ROOT/path).open() as f:return {r['bare']:r for r in csv.DictReader(f,delimiter='\t')}
    verbs=lexical('catalog/sources/units-3-5/openrussian/verbs.csv')
    nouns=lexical('catalog/sources/units-3-5/openrussian/nouns.csv')
    adjectives=lexical('catalog/sources/units-6-10/openrussian/adjectives.csv')
    assert verbs['сказать']['aspect']=='perfective' and verbs['быть']['aspect']=='imperfective' and verbs['говорить']['aspect']=='imperfective'
    assert [verbs['сказать'][k] for k in ['presfut_sg1','presfut_pl1','presfut_sg2']]==["скажу'","ска'жем","ска'жешь"]
    for lemma,forms in {'книга':["кни'ге","кни'ги","кни'гах"], 'стол':["столу'","столы'","стола'х"], 'дом':["до'му","дома'","дома'х"]}.items():
        assert [nouns[lemma][k] for k in ['sg_dat','pl_nom','pl_prep']]==forms
    assert nouns['книга']['sg_prep']==nouns['книга']['sg_dat'] and nouns['письмо']['gender']=='n'
    assert [adjectives['хороший'][k] for k in ['decl_m_nom','decl_n_nom','decl_f_nom']]==["хоро'ший","хоро'шее","хоро'шая"]
    bibliography=('Retained Wikibooks CC BY-SA 4.0: Russian/Grammar/Future tense oldid3353822, catalog/sources/units-11-15/wikibooks/future-tense-3353822.txt, complete imperfective/perfective future tables; '
        'Russian/Grammar/Dative case oldid4289617, catalog/sources/units-11-15/wikibooks/dative-case-4289617.txt, complete Nouns context; '
        'Russian/Grammar/Adjectives oldid4292379, catalog/sources/units-6-10/wikibooks/adjectives-4292379.txt, opening agreement definition, Nominative adjectives and 5- and 7-letter spelling rules with complete accepted/rejected good-letter example; '
        'Russian/Grammar/Prepositional case oldid4567336, catalog/sources/units-3-5/wikibooks/prepositional-4567336.txt, Usage and complete Nouns singular/plural context; '
        'Russian/Pronouns oldid4585698, catalog/sources/units-3-5/wikibooks/pronouns-4585698.txt, complete Formality context. '
        'Retained OpenRussian commit50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0: units-3-5/openrussian/verbs.csv сказать aspect/presfut_sg1/presfut_pl1/presfut_sg2 and быть/говорить aspect; '
        'units-3-5/openrussian/nouns.csv книга/стол/дом sg_dat/pl_nom/pl_prep, книга sg_prep and письмо gender=n; units-6-10/openrussian/adjectives.csv хороший decl_m_nom/decl_n_nom/decl_f_nom. '
        'The Future tense source incorrectly labels быть perfective; the retained lexical aspect field is imperfective. That error and one-to-one English-tense/aspect claims are not adopted. '
        'The wrong good-letter spelling is explicitly a rejected counterexample, not a form to use. Bare case-form recognition is bounded to the requested table row; shared spellings are not unambiguous case identifiers. '
        'All scored/source tuples and exact Russian spellings/capitalization remain unchanged; no new Russian forms, complete phrases or productive-paradigm claim. '
        'Independently authored English on 2026-10-13 within content-audit-2026-10-08; no copied primary prose or private Penguin text; final semantic audit pending.')
    corrections={
        'u12-g-future-imp-0':('Мы бу́дем говори́ть is the first-person plural future: the subject means we, and the future auxiliary agrees with that plural subject. Я бу́ду говори́ть is first-person singular, meaning I. Both use a future auxiliary plus the same imperfective infinitive; choose the plural line requested here.', ['', 'Я бу́ду говори́ть has a first-person singular subject and auxiliary, rather than the requested first-person plural future.']),
        'u12-i-future-imp-1':('Я бу́ду говори́ть is the first-person singular future: the subject means I, and the future auxiliary agrees with one speaker. Мы бу́дем говори́ть is first-person plural, meaning we. The imperfective infinitive stays the same while the subject and auxiliary change.', ['', 'Мы бу́дем говори́ть has a first-person plural subject and auxiliary, rather than the requested first-person singular future.']),
        'u12-i-future-imp-2':('Вы бу́дете говори́ть is the second-person plural future row: the auxiliary agrees with you in that row. This form can also address one person politely. Я бу́ду говори́ть is first-person singular and changes the person as well as the table number. The same imperfective infinitive follows each auxiliary.', ['', 'Я бу́ду говори́ть is first-person singular future, not the requested second-person plural future row.']),
        'u12-g-future-perf-0':('Мы ска́жем is the first-person plural future of the perfective verb in this table, agreeing with we. Я скажу́ is first-person singular, agreeing with I. These perfective future options use a single conjugated verb without a separate future auxiliary; choose the plural line.', ['', 'Я скажу́ is first-person singular future, not first-person plural future.']),
        'u12-i-future-perf-1':('ска́жем is the first-person plural future form of this perfective verb, used with we. ска́жешь is second-person singular, used with singular informal you. The cue shows first-person singular, but the requested table row is first-person plural.', ['', 'ска́жешь is second-person singular future, rather than the first-person plural future requested here.']),
        'u12-i-future-perf-2':('Я скажу́ is the first-person singular future of the perfective verb in the table, agreeing with I. Мы ска́жем is first-person plural, agreeing with we. Both refer to the future with a single conjugated perfective verb; the question asks for one speaker.', ['', 'Мы ска́жем is first-person plural future, not first-person singular future.']),
        'u12-g-dative-0':('кни́ге is the recorded dative singular form of кни́га. кни́ги is the nominative plural option supplied here, so it changes the number and table row. This question asks for the dative singular row; the same spelling as the answer can also be prepositional singular in a sentence.', ['', 'кни́ги is the nominative plural option supplied here, not the requested dative singular table row.']),
        'u12-i-dative-1':('столу́ is the recorded dative singular form of сто́л. столы́ is the nominative plural option supplied here. Keep the noun singular and select its dative row, rather than selecting the plural nominative row.', ['', 'столы́ is nominative plural, not the requested dative singular.']),
        'u12-i-dative-2':('до́му is the recorded dative singular form of до́м. дома́ is the nominative plural option supplied here. The requested row changes case while keeping the noun singular; the plural option changes number instead.', ['', 'дома́ is nominative plural, not the requested dative singular.']),
        'u12-g-spelling-0':('хорошее письмо is the accepted spelling in the source example. The adjective stem ends in the consonant transliterated sh; its unstressed neuter ending uses e rather than o under the five-letter spelling rule. хорошое письмо is the rejected counterexample and should not be used as correct Russian.', ['', 'хорошое письмо is the rejected spelling: it uses o where this unstressed adjective ending requires e.']),
        'u12-i-spelling-1':('хоро́шее is the nominative singular neuter form of this adjective. хоро́шая is feminine singular. The adjective agrees with the gender of the noun it modifies; the neuter form requested here is also the form used with the neuter noun in the good-letter example.', ['', 'хоро́шая is feminine singular, not the requested neuter form.']),
        'u12-i-spelling-2':('хорошое письмо is the rejected spelling requested by this question. Its unstressed adjective ending uses o where the five-letter rule requires e after the stem-final consonant transliterated sh. хорошее письмо is the accepted spelling. The keyed counterexample is incorrect Russian, not a form to use.', ['', 'хорошее письмо is the accepted spelling, while this question asks you to identify the rejected spelling.']),
        'u12-g-prep-plural-0':('кни́гах is the recorded prepositional plural form of the shown noun. кни́ге is the dative singular option supplied here and can also be prepositional singular. Neither singular use matches the plural row requested. These are table forms; the prepositional case is used with a governing preposition in a sentence.', ['', 'кни́ге is singular, including the supplied dative singular row, rather than prepositional plural.']),
        'u12-i-prep-plural-1':('стола́х is the recorded prepositional plural form of the shown noun. столу́ is the supplied dative singular option. The question requires both prepositional case and plural number; the singular dative option changes both features. In a sentence the prepositional form follows a governing preposition.', ['', 'столу́ is the supplied dative singular form, not prepositional plural.']),
        'u12-i-prep-plural-2':('дома́х is the recorded prepositional plural form of the shown noun. до́му is the supplied dative singular option. Select the prepositional plural row while keeping the plural number of the cue. These are table forms; a sentence uses the prepositional case after a governing preposition.', ['', 'до́му is the supplied dative singular form, not prepositional plural.'])}
    added=[];changes=[]
    def record(text,item,name):
        r=deepcopy(template);original=bibliography+'\n'+text;objective=item['objectiveId']
        r.update(itemId='prov-ca-u12-feedback-'+name,unitNumber=12,objectiveId=objective,sourceRecordId='ca-coverage:'+objective+':feedback:'+name,
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
    assert len(changes)==15 and len(added)==30
    after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode();PACK.write_bytes(after)
    (HERE/'UNIT12_FEEDBACK_CORRECTION.json').write_text(json.dumps(dict(baselineCommit=BASELINE,beforeSha256=hashlib.sha256(raw).hexdigest(),afterSha256=hashlib.sha256(after).hexdigest(),addedProvenanceIds=added,changes=changes,bibliography=bibliography,semanticAcceptance=False),ensure_ascii=False,indent=2)+'\n')
    print('Explained fifteen source-bound contrasts; thirty pending coverage records')
if __name__=='__main__':main()
