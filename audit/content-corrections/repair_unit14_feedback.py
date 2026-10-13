#!/usr/bin/env python3
"""Explain nine retained Unit14 future and impersonal-past contrasts without changing scored tuples."""
import csv, hashlib, json
from copy import deepcopy
from pathlib import Path
HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
PACK = ROOT / 'app/src/main/assets/content/v2-curriculum.json'
BASELINE = 'f0936bc84fde99d12128bf8e75523eec75574653'
def sha(s): return hashlib.sha256(s.encode()).hexdigest()
def main():
    raw=PACK.read_bytes();p=json.loads(raw)
    if any(r['itemId']=='prov-ca-u14-feedback-u14-g-future-aspect-0' for r in p['provenanceRecords']):
        print('Already repaired; evidence unchanged');return
    template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-u5-behavior-teach-1-body')
    with (ROOT/'catalog/sources/units-3-5/openrussian/verbs.csv').open() as f:
        verbs={r['bare']:r for r in csv.DictReader(f,delimiter='\t')}
    for lemma,aspect in [('говорить','imperfective'),('работать','imperfective'),('поговорить','perfective'),('поработать','perfective'),('сказать','perfective'),('быть','imperfective')]:
        assert verbs[lemma]['aspect']==aspect
    assert verbs['поговорить']['presfut_sg1']=="поговорю'" and verbs['поработать']['presfut_sg1']=="порабо'таю"
    assert [verbs['сказать'][k] for k in ['presfut_sg3','presfut_pl3','presfut_sg1']]==["ска'жет","ска'жут","скажу'"]
    assert verbs['быть']['past_n']=="бы'ло"
    bibliography=('Retained Wikibooks CC BY-SA 4.0: Russian/Grammar/Future tense oldid3353822, catalog/sources/units-11-15/wikibooks/future-tense-3353822.txt, complete Imperfective or Perfective speaking/working duration examples and imperfective/perfective future tables; '
        'Russian/Grammar/Past tense oldid4567337, catalog/sources/units-11-15/wikibooks/past-tense-4567337.txt, complete Imperfective past process/duration and impersonal cold/bored state contexts; '
        'Russian/Verbal Aspect oldid4059032, catalog/sources/units-11-15/wikibooks/verbal-aspect-4059032.txt, complete Introduction process/completed-action observation and Multi-aspect period-of-time context; '
        'Russian/Grammar/Dative case oldid4289617, catalog/sources/units-11-15/wikibooks/dative-case-4289617.txt, complete Personal pronouns singular/plural and feminine/masculine/neuter qualification. '
        'Retained OpenRussian commit50e210c4803237779cb562bc1abcea529066031c, catalog/sources/units-3-5/openrussian/verbs.csv, CC BY-SA 4.0: говорить/работать/поговорить/поработать/сказать/быть aspect, поговорить/поработать presfut_sg1, сказать presfut_sg3/presfut_pl3/presfut_sg1 and быть past_n. '
        'Bounded/unbounded labels describe the supplied examples, not a rule that every definite duration requires perfective aspect or that an imperfective action lasts forever. Future perfective does not mean an event has already occurred, and the selected limited stretch does not entail a finished task. '
        'Both impersonal options are grammatical impersonal past: dative experiencer person/gender/number and cold/bored state distinguish the requested example, while the verb remains neuter singular. '
        'Source overgeneralizations about finite-duration aspect and erroneous perfective classification of being are not adopted. '
        'All scored/source tuples and exact Russian spellings/capitalization remain unchanged; original category cues/duplicate relationships and productive-competence concerns remain open. No new Russian forms or phrases. '
        'Independently authored English on 2026-10-13 within content-audit-2026-10-08; no copied primary prose or private Penguin text; final semantic audit pending.')
    corrections={
        'u14-g-future-aspect-0':('Я поговорю три́дцать мину́т is the bounded future example here: its perfective verb presents a limited stretch of speaking, and the line states thirty minutes. Я бу́ду говори́ть uses a future auxiliary and imperfective infinitive without a stated duration in this option. Both refer to the future, not to an event already completed. A stated duration does not automatically require perfective aspect in every sentence.', ['', 'Я бу́ду говори́ть is the supplied imperfective future option without the thirty-minute limit stated in the other line; it does not match this bounded future example.']),
        'u14-i-future-aspect-1':('Я порабо́таю три ча́са is the bounded future example here: the perfective verb presents a limited stretch of work, and the line states three hours. Я бу́ду рабо́тать is the supplied auxiliary-plus-imperfective future without that stated duration. The limit describes this work period, not necessarily completion of a task. A definite time period does not by itself force perfective aspect.', ['', 'Я бу́ду рабо́тать is the supplied imperfective future option without the three-hour limit stated in the other line; it does not match this bounded future example.']),
        'u14-i-future-aspect-2':('Я бу́ду говори́ть is the unbounded future example in this pair: the future auxiliary plus imperfective infinitive presents speaking without a stated duration. Я поговорю три́дцать мину́т presents a limited stretch and states thirty minutes. Unbounded here describes the supplied option; it does not mean the activity lasts forever or that imperfective future can never have a duration.', ['', 'Я поговорю три́дцать мину́т is the bounded future option with a stated thirty-minute limit, rather than the unbounded future example requested here.']),
        'u14-g-future-person-0':('Мы бу́дем говори́ть is first-person plural future: the subject means we, and the future auxiliary agrees with that plural subject. Я бу́ду говори́ть is first-person singular, meaning I. The imperfective infinitive stays the same; the subject and auxiliary distinguish the number requested.', ['', 'Я бу́ду говори́ть is first-person singular future, not the first-person plural future requested here.']),
        'u14-i-future-person-1':('ска́жут is the third-person plural future form of this perfective verb, used with they. скажу́ is first-person singular, used with I. The cue shows third-person singular, but the question asks for third-person plural of the same verb. These forms refer to future events, not events already completed.', ['', 'скажу́ is first-person singular future, not the requested third-person plural future.']),
        'u14-i-future-person-2':('Я бу́ду говори́ть is first-person singular future: the subject means I, and the auxiliary agrees with one speaker. Мы бу́дем говори́ть is first-person plural, meaning we. Both use the same imperfective infinitive after the future auxiliary; choose singular agreement.', ['', 'Мы бу́дем говори́ть is first-person plural future, not the first-person singular future requested here.']),
        'u14-g-impersonal-0':('Ему было холодно is the impersonal past example meaning he was cold: the experiencer is dative singular, while the verb is neuter singular. Им было скучно describes plural experiencers who were bored. Both options are impersonal past and keep the same neuter singular verb; the requested person and state distinguish them.', ['', 'Им было скучно is also impersonal past, but it describes plural experiencers who were bored, not the singular cold person requested here.']),
        'u14-i-impersonal-1':('Им было скучно is the impersonal past example meaning they were bored: the experiencers are dative plural, but the verb remains neuter singular. Ему было холодно describes one male experiencer who was cold. Both options are impersonal past; plural experiencers do not turn the verb into a plural form in this construction.', ['', 'Ему было холодно is also impersonal past, but it describes one male experiencer who was cold, not the plural bored experiencers requested here.']),
        'u14-i-impersonal-2':('ей было холодно is the impersonal past example meaning she was cold: the experiencer is feminine dative singular, while the verb remains neuter singular. Ему было холодно is the supplied he was cold example with a masculine dative experiencer. Both options are impersonal past; the woman in the question changes the experiencer pronoun, not the verb to feminine agreement.', ['', 'Ему было холодно is also impersonal past, but the supplied example refers to a male experiencer, not the woman requested here.'])}
    added=[];changes=[]
    def record(text,item,name):
        r=deepcopy(template);original=bibliography+'\n'+text;objective=item['objectiveId']
        r.update(itemId='prov-ca-u14-feedback-'+name,unitNumber=14,objectiveId=objective,sourceRecordId='ca-coverage:'+objective+':feedback:'+name,
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
    (HERE/'UNIT14_FEEDBACK_CORRECTION.json').write_text(json.dumps(dict(baselineCommit=BASELINE,beforeSha256=hashlib.sha256(raw).hexdigest(),afterSha256=hashlib.sha256(after).hexdigest(),addedProvenanceIds=added,changes=changes,bibliography=bibliography,semanticAcceptance=False),ensure_ascii=False,indent=2)+'\n')
    print('Explained nine source-bound contrasts; eighteen pending coverage records')
if __name__=='__main__':main()
