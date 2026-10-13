#!/usr/bin/env python3
"""Explain twelve retained Unit13 aspect and reported-speech contrasts without changing scored tuples."""
import csv, hashlib, json
from copy import deepcopy
from pathlib import Path
HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
PACK = ROOT / 'app/src/main/assets/content/v2-curriculum.json'
BASELINE = '0e4bed79fa5b864714ffe0582d7664d2defeeab9'
def sha(s): return hashlib.sha256(s.encode()).hexdigest()
def main():
    raw=PACK.read_bytes();p=json.loads(raw)
    if any(r['itemId']=='prov-ca-u13-feedback-u13-g-past-aspect-0' for r in p['provenanceRecords']):
        print('Already repaired; evidence unchanged');return
    template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-u5-behavior-teach-1-body')
    with (ROOT/'catalog/sources/units-3-5/openrussian/verbs.csv').open() as f:
        verbs={r['bare']:r for r in csv.DictReader(f,delimiter='\t')}
    for lemma,aspect,partner in [('читать','imperfective','прочитать'),('прочитать','perfective','читать'),('говорить','imperfective','сказать'),('сказать','perfective','говорить'),('делать','imperfective','сделать'),('сделать','perfective','делать')]:
        assert verbs[lemma]['aspect']==aspect and partner in verbs[lemma]['partner'].split(';')
    assert [verbs['читать'][k] for k in ['past_m','past_f']]==["чита'л","чита'ла"]
    assert verbs['прочитать']['past_m']=="прочита'л" and verbs['сказать']['past_f']=="сказа'ла" and verbs['сказать']['presfut_sg3']=="ска'жет"
    bibliography=('Retained Wikibooks CC BY-SA 4.0: Russian/Grammar/Past tense oldid4567337, catalog/sources/units-11-15/wikibooks/past-tense-4567337.txt, complete Usage/imperfective/perfective and Regular conjugation contexts; '
        'Russian/Verbal Aspect oldid4059032, catalog/sources/units-11-15/wikibooks/verbal-aspect-4059032.txt, complete Introduction, Forming aspectual pairs and Unpredictable pairs contexts, including the explicit observation that a completed action can be viewed as process; '
        'Russian/Grammar/Future tense oldid3353822, catalog/sources/units-11-15/wikibooks/future-tense-3353822.txt, complete Present or future reported-nationality/evening/Moscow context. '
        'Retained OpenRussian commit50e210c4803237779cb562bc1abcea529066031c, catalog/sources/units-3-5/openrussian/verbs.csv, CC BY-SA 4.0: читать/прочитать/говорить/сказать/делать/сделать aspect and partner fields, читать past_m/past_f, прочитать past_m, сказать past_f/presfut_sg3. '
        'Source errors/overgeneralizations are not adopted: being incorrectly classified as perfective; contradictory claims about how many verbs have pairs; automatic prefix-based pairing; simple English past/progressive mapped one-to-one to Russian aspect; every reported tense retained regardless of context. '
        'The говорить/сказать pairing is bounded to the source say/tell grouping and populated lexical partner fields, not every meaning of speak or talk. '
        'Reported examples distinguish past reporting from future content, present-time nationality and future time clauses; labels classify the supplied examples rather than prescribe mechanical pronoun/tense rewriting. '
        'All scored/source tuples and exact Russian spellings/capitalization remain unchanged; original category cues/duplicate relationships and productive-competence concerns remain open. No new Russian forms or phrases. '
        'Independently authored English on 2026-10-13 within content-audit-2026-10-08; no copied primary prose or private Penguin text; final semantic audit pending.')
    corrections={
        'u13-g-past-aspect-0':('я прочитал is the perfective past option: it presents reading as a completed whole. я читал is imperfective past, presenting the reading activity or process without asserting that completed whole. Both are past forms. English simple past wording alone does not determine Russian aspect; the question explicitly asks for the perfective form.', ['', 'я читал uses the imperfective past, not the perfective past form requested here. Its process focus does not mean the activity could never have finished.']),
        'u13-i-past-aspect-1':('я читал is the imperfective past option, presenting reading as activity or process. я прочитал presents the reading as a completed whole and is perfective past. A completed activity can still be described with imperfective aspect when the focus is on the process; English simple past can also translate an imperfective verb.', ['', 'я прочитал is perfective past and presents a completed whole, rather than the imperfective past requested here.']),
        'u13-i-past-aspect-2':('чита́л is the masculine past form of the shown infinitive. чита́ла is feminine past. Both belong to the same imperfective verb; this question changes gender agreement rather than selecting a different aspect.', ['', 'чита́ла is feminine past, not the requested masculine past form of the same verb.']),
        'u13-g-tense-keep-0':('The same tense label fits this nationality report: we keep the tense the same. Он сказа́л, что он англича́нин has a past reporting verb but a present-time nationality statement; a past reporting verb does not itself require English-style backshift in Russian. The other label describes changing the reported tense in the English comparison. This is the shown example, not a rule that every report must preserve every tense or word unchanged.', ['', 'we usually change the tense is the label for the English backshift comparison, not the same tense classification of the shown Russian nationality report.']),
        'u13-i-tense-keep-1':('Она́ сказа́ла, что она́ ска́жет ему́ ве́чером is the reported evening example: she said in the past that she would tell him in the evening. The reporting verb is past while the embedded verb remains future. The Moscow option instead gives a future time clause and a plan to speak Russian, without a past reporting frame. Recognize the supplied examples; pronoun changes depend on who is speaking.', ['', 'Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски is the future plan about Moscow, not the reported evening example with a past reporting verb.']),
        'u13-i-tense-keep-2':('we usually change the tense is the source label for changing the reported tense in its English backshift comparison. we keep the tense the same labels the contrasting Russian example. English backshift can follow a past reporting verb, but is not mandatory in every report; the question classifies the supplied labels rather than imposing an always-change rule.', ['', 'we keep the tense the same describes retention, not changing the reported tense in the English comparison requested here.']),
        'u13-g-infinitive-0':('сказа́ть is the perfective partner paired with говори́ть for the say/tell meaning shown here. де́лать is imperfective and belongs to the different do/make pair. The pairing here does not mean every use of speak or talk can be replaced by the same perfective verb.', ['', 'де́лать is imperfective and belongs to the do/make pair, not the perfective partner in the shown say/tell grouping.']),
        'u13-i-infinitive-1':('сде́лать is the perfective partner of де́лать in the retained do/make pair. говори́ть is imperfective and belongs to the different say/tell grouping. Learn this recorded pair rather than assuming that adding any prefix gives the matching perfective of any verb.', ['', 'говори́ть is imperfective and belongs to a different lexical grouping, not the perfective partner of the shown do/make verb.']),
        'u13-i-infinitive-2':('говори́ть is the imperfective partner paired with сказа́ть for the say/tell meaning shown here. сде́лать is perfective and belongs to the different do/make pair. The question asks for the imperfective member of this recorded pairing, not any verb of another meaning.', ['', 'сде́лать is perfective and belongs to the do/make pair, not the imperfective partner in the shown say/tell grouping.']),
        'u13-g-reported-0':('Она́ сказа́ла, что она́ ска́жет ему́ ве́чером is the reported evening example: a past she said frame contains a future statement about telling him in the evening. The Moscow option is a future time clause and a plan to speak Russian, with no past reporting frame. The source supplies this pair; it is not a rule to replace every first-person pronoun mechanically with she.', ['', 'Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски is the future plan about Moscow, not the reported evening example.']),
        'u13-i-reported-1':('Я скажу ему ве́чером is the direct evening statement, meaning I will tell him in the evening. It has a first-person future verb without a past reporting frame. The shown reported example instead says that she said she would tell him. The Moscow option describes a different future plan, not this direct evening statement.', ['', 'Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски describes a future plan about Moscow, not the direct evening statement requested here.']),
        'u13-i-reported-2':('Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски is the future plan about Moscow: when the speaker is there, the speaker will speak only Russian. Both clauses refer to the future, including the clause meaning when. Я скажу ему ве́чером instead says I will tell him in the evening. Neither option is itself a past reporting frame; identify the Moscow plan requested here.', ['', 'Я скажу ему ве́чером is the direct evening statement, not the future plan about Moscow.'])}
    added=[];changes=[]
    def record(text,item,name):
        r=deepcopy(template);original=bibliography+'\n'+text;objective=item['objectiveId']
        r.update(itemId='prov-ca-u13-feedback-'+name,unitNumber=13,objectiveId=objective,sourceRecordId='ca-coverage:'+objective+':feedback:'+name,
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
    assert len(changes)==12 and len(added)==24
    after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode();PACK.write_bytes(after)
    (HERE/'UNIT13_FEEDBACK_CORRECTION.json').write_text(json.dumps(dict(baselineCommit=BASELINE,beforeSha256=hashlib.sha256(raw).hexdigest(),afterSha256=hashlib.sha256(after).hexdigest(),addedProvenanceIds=added,changes=changes,bibliography=bibliography,semanticAcceptance=False),ensure_ascii=False,indent=2)+'\n')
    print('Explained twelve source-bound contrasts; twenty-four pending coverage records')
if __name__=='__main__':main()
