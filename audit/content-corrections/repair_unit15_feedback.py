#!/usr/bin/env python3
"""Explain nine retained Unit15 imperative and aspect contrasts without changing scored tuples."""
import csv, hashlib, json
from copy import deepcopy
from pathlib import Path
HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
PACK = ROOT / 'app/src/main/assets/content/v2-curriculum.json'
BASELINE = 'deb398001af69dc58e1dddd5a18b8361182508f0'
def sha(s): return hashlib.sha256(s.encode()).hexdigest()
def main():
    raw=PACK.read_bytes();p=json.loads(raw)
    if any(r['itemId']=='prov-ca-u15-feedback-u15-g-imperative-0' for r in p['provenanceRecords']):
        print('Already repaired; evidence unchanged');return
    template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-u5-behavior-teach-1-body')
    with (ROOT/'catalog/sources/units-3-5/openrussian/verbs.csv').open() as f:
        verbs={r['bare']:r for r in csv.DictReader(f,delimiter='\t')}
    for lemma,forms in {'читать':["чита'й","чита'йте","чита'ть"], 'сказать':["скажи'","скажи'те","сказа'ть"], 'дать':["да'й","да'йте","дать"]}.items():
        assert [verbs[lemma][k] for k in ['imperative_sg','imperative_pl','accented']]==forms
    for lemma,aspect,partner in [('говорить','imperfective','сказать'),('сказать','perfective','говорить'),('делать','imperfective','сделать'),('сделать','perfective','делать')]:
        assert verbs[lemma]['aspect']==aspect and partner in verbs[lemma]['partner'].split(';')
    bibliography=('Retained OpenRussian commit50e210c4803237779cb562bc1abcea529066031c, catalog/sources/units-3-5/openrussian/verbs.csv, CC BY-SA 4.0: читать/сказать/дать imperative_sg/imperative_pl/accented, and говорить/сказать/делать/сделать aspect/partner. '
        'Retained Wikipedia Russian grammar oldid1369617627, catalog/sources/units-26-30/wikipedia/russian-grammar-1369617627.txt, CC BY-SA 4.0 under retained FREEZE rights attestation: complete Moods / Imperative mood definition and singular/plural table, including polite second-person plural and other command constructions. '
        'Retained Wikibooks Russian/Useful Words oldid3353859, catalog/sources/units-21-25/wikibooks/useful-words-3353859.txt, CC BY-SA 4.0: complete greeting entry explicitly identifies plural/singular-polite imperative. '
        'Retained Wikibooks Russian/Verbal Aspect oldid4059032, catalog/sources/units-11-15/wikibooks/verbal-aspect-4059032.txt, CC BY-SA 4.0: complete Introduction, Forming aspectual pairs and Unpredictable pairs contexts. '
        'Imperative number labels are morphological table rows: plural form can address one person politely; the infinitive distractor is not the requested inflected imperative form, without denying other infinitival command uses. Actual politeness and imperative aspect choice depend on context. '
        'The говорить/сказать pairing is bounded to the supplied say/tell meaning and populated partner fields, not every meaning of speak/talk or an automatic prefix recipe. Source pairing-prevalence contradictions and one-to-one aspect/English-tense claims are not adopted. '
        'All scored/source tuples and exact Russian spellings/capitalization remain unchanged; original category cues/duplicate relationships and productive-competence concerns remain open. No new Russian forms or phrases. '
        'Independently authored English on 2026-10-13 within content-audit-2026-10-08; no copied primary prose or private Penguin text; final semantic audit pending.')
    corrections={
        'u15-g-imperative-0':('чита́йте is the plural imperative form of the reading verb shown here, used for multiple addressees or polite address to one person. It extends the shown singular form with the plural/polite ending. чита́ть is the infinitive, not the inflected plural imperative requested. This identifies the supplied form, not every possible way to express a command.', ['', 'чита́ть is the infinitive, not the requested inflected plural imperative form.']),
        'u15-i-imperative-1':('чита́й is the singular imperative form of the reading verb, used for one informal addressee. чита́ть is the infinitive, not that inflected imperative form. The cue shows the plural/polite form, while this question asks for the singular imperative table row.', ['', 'чита́ть is the infinitive, not the requested inflected singular imperative form.']),
        'u15-i-imperative-2':('чита́й is the singular imperative form for one informal addressee. чита́йте is the plural imperative form and can also address one person politely. Both are imperative forms of the shown infinitive; the requested singular table row selects the informal form, rather than identifying the actual number of listeners from spelling alone.', ['', 'чита́йте is the plural/polite imperative form, not the singular imperative table row requested here; polite use can still address one person.']),
        'u15-g-say-request-0':('скажи́те is the plural imperative form of the shown say/tell verb, used for multiple addressees or polite address to one person. сказа́ть is the infinitive, not the inflected plural imperative form requested. The singular and plural imperative forms keep the stress shown in these recorded examples; politeness also depends on context.', ['', 'сказа́ть is the infinitive, not the requested inflected plural imperative form.']),
        'u15-i-say-request-1':('да́йте is the plural imperative form of the shown give verb. It can address multiple people or one person politely. дать is the infinitive, not the inflected imperative form requested. Select the recorded plural imperative of this verb rather than assuming the dictionary form is the requested command form.', ['', 'дать is the infinitive, not the requested inflected plural imperative form.']),
        'u15-i-say-request-2':('скажи́ is the singular imperative form for one informal addressee. сказа́ть is the infinitive, not that inflected imperative form. The cue shows the plural/polite form, but the question asks for the singular imperative row of the same say/tell verb; the verb meaning and aspect stay the same.', ['', 'сказа́ть is the infinitive, not the requested inflected singular imperative form.']),
        'u15-g-aspect-consolidate-0':('сказа́ть is the perfective partner paired with говори́ть for the say/tell meaning shown here. де́лать is imperfective and belongs to the different do/make pair. This pairing does not mean every use of speak or talk can be replaced by the same perfective verb.', ['', 'де́лать is imperfective and belongs to the do/make pair, not the perfective partner for the supplied say/tell meaning.']),
        'u15-i-aspect-consolidate-1':('говори́ть is the imperfective partner paired with сказа́ть for the say/tell meaning shown here. сде́лать is perfective and belongs to the different do/make pair. The question asks for the imperfective member of the supplied pair, not another verb of a different meaning.', ['', 'сде́лать is perfective and belongs to the do/make pair, not the imperfective partner for the supplied say/tell meaning.']),
        'u15-i-aspect-consolidate-2':('сде́лать is the perfective partner of де́лать in the recorded do/make pair. говори́ть is imperfective and belongs to the different say/tell grouping. Learn this supplied pair rather than assuming that adding any prefix gives the matching perfective of any verb.', ['', 'говори́ть is imperfective and belongs to a different meaning group, not the perfective partner of the shown do/make verb.'])}
    added=[];changes=[]
    def record(text,item,name):
        r=deepcopy(template);original=bibliography+'\n'+text;objective=item['objectiveId']
        r.update(itemId='prov-ca-u15-feedback-'+name,unitNumber=15,objectiveId=objective,sourceRecordId='ca-coverage:'+objective+':feedback:'+name,
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
    assert len(changes)==9 and len(added)==18
    after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode();PACK.write_bytes(after)
    (HERE/'UNIT15_FEEDBACK_CORRECTION.json').write_text(json.dumps(dict(baselineCommit=BASELINE,beforeSha256=hashlib.sha256(raw).hexdigest(),afterSha256=hashlib.sha256(after).hexdigest(),addedProvenanceIds=added,changes=changes,bibliography=bibliography,semanticAcceptance=False),ensure_ascii=False,indent=2)+'\n')
    print('Explained nine source-bound contrasts; eighteen pending coverage records')
if __name__=='__main__':main()
