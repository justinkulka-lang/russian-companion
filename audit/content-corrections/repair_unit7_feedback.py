#!/usr/bin/env python3
"""Explain two retained Unit7 agreement contrasts without changing scored tuples."""
import csv, hashlib, json
from copy import deepcopy
from pathlib import Path
HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
PACK = ROOT / 'app/src/main/assets/content/v2-curriculum.json'
BASELINE = 'f9e861d252b69a788c45be18ca4e2683ed0dd42f'
def sha(s): return hashlib.sha256(s.encode()).hexdigest()
def main():
    raw=PACK.read_bytes();p=json.loads(raw)
    if any(r['itemId']=='prov-ca-u7-feedback-u7-i-endings-4' for r in p['provenanceRecords']):
        print('Already repaired; evidence unchanged');return
    with (ROOT/'catalog/sources/units-6-10/openrussian/adjectives.csv').open() as f:
        adjectives={r['bare']:r for r in csv.DictReader(f,delimiter='\t')}
    with (ROOT/'catalog/sources/units-3-5/openrussian/nouns.csv').open() as f:
        nouns={r['bare']:r for r in csv.DictReader(f,delimiter='\t')}
    assert nouns['машина']['gender']=='f' and nouns['машина']['sg_nom']=="маши'на"
    assert [adjectives['новый'][k] for k in ['decl_f_nom','decl_m_nom','decl_pl_nom']]==["но'вая","но'вый","но'вые"]
    assert adjectives['этот']['decl_pl_nom']=="э'ти" and adjectives['этот']['decl_f_nom']=="э'та"
    template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-u5-behavior-teach-1-body')
    bibliography=('Retained Wikibooks Russian/Grammar/Adjectives oldid4292379, catalog/sources/units-6-10/wikibooks/adjectives-4292379.txt, CC BY-SA 4.0: '
        'complete opening agreement definition and Nominative adjectives / Hard endings numbered examples new car and these good new students. '
        'Retained OpenRussian commit50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0: adjectives.csv новый decl_f_nom/decl_m_nom/decl_pl_nom, '
        'этот decl_pl_nom/decl_f_nom; nouns.csv машина gender=f and sg_nom. '
        'Original Russian options, noun cue and complete plural phrase remain unchanged; no ending-only gender inference or new Russian form is taught. '
        'Bounded recognition of these nominative examples, not a productive paradigm or a claim that every source rule is correct. '
        'Independently authored English on 2026-10-12 within content-audit-2026-10-08; no copied primary prose or private Penguin text; final semantic audit pending.')
    corrections={
        'u7-i-endings-4':(
            'но́вая agrees with the shown feminine singular noun in the nominative, as in the retained new car phrase. но́вый is masculine singular and но́вые is plural. Choose the form that matches the noun in this phrase, rather than the dictionary form or a plural form.',
            ['', 'но́вый is nominative masculine singular instead of the feminine singular agreement required here.', 'но́вые is nominative plural instead of the singular agreement required here.']),
        'u7-g-demonstrative-0':(
            'The shown line means these good new students and refers to more than one student. э́ти is the nominative plural form of this demonstrative, so it matches the plural phrase. э́та is feminine singular and does not match this phrase.',
            ['', 'э́та is nominative feminine singular instead of the plural agreement used in the shown phrase.'])}
    added=[];changes=[]
    def record(text,item,name):
        r=deepcopy(template);original=bibliography+'\n'+text;objective=item['objectiveId']
        r.update(itemId='prov-ca-u7-feedback-'+name,unitNumber=7,objectiveId=objective,sourceRecordId='ca-coverage:'+objective+':feedback:'+name,
            originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text))
        r['synthesis'].update(bibliography=bibliography,objectiveOrClaimBinding=objective+':feedback:'+name,
            authorshipDeclaration='Independently authored English on 2026-10-12 from retained complete grammar context and lexical fields; no copied primary prose or private book text.',applications=[])
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
    assert len(changes)==2 and len(added)==5
    after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode();PACK.write_bytes(after)
    (HERE/'UNIT7_FEEDBACK_CORRECTION.json').write_text(json.dumps(dict(baselineCommit=BASELINE,beforeSha256=hashlib.sha256(raw).hexdigest(),afterSha256=hashlib.sha256(after).hexdigest(),addedProvenanceIds=added,changes=changes,bibliography=bibliography,semanticAcceptance=False),ensure_ascii=False,indent=2)+'\n')
    print('Explained two agreement contrasts; five pending coverage records')
if __name__=='__main__':main()
