#!/usr/bin/env python3
"""Replace Unit5 flag recall with a bounded, source-pending noun behavior distinction."""
import hashlib, json, csv
from copy import deepcopy
from pathlib import Path
HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
PACK = ROOT / 'app/src/main/assets/content/v2-curriculum.json'
OBJ = 'obj-u5-indeclinable'
UNCHANGED = 'Indeclinable: unchanged for case or number'
CHANGEABLE = 'Declinable: can change for case'
def sha(s): return hashlib.sha256(s.encode()).hexdigest()
def main():
    raw = PACK.read_bytes(); p = json.loads(raw)
    if any(r['itemId'] == 'prov-ca-u5-behavior-teach-1-body' for r in p['provenanceRecords']):
        print('Already repaired; retained evidence unchanged'); return
    with (ROOT/'catalog/sources/units-3-5/openrussian/nouns.csv').open() as f:
        rows = {r['bare']: r for r in csv.DictReader(f, delimiter='\t')}
    for word in ['радио','такси','метро','кино','пальто','кофе','кафе','меню']:
        assert rows[word]['indeclinable'] == '1'
    for word in ['книга','окно']:
        assert rows[word]['indeclinable'] == '0'
        assert rows[word]['sg_nom'] != rows[word]['sg_gen']
    unit = next(u for u in p['units'] if u['number'] == 5)
    objective = next(o for o in unit['objectives'] if o['id'] == OBJ)
    session = next(s for s in unit['sessions'] if any(st['id']=='u5-st-teach-indeclinable-1' for st in s['steps']))
    template = next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-u7-worked-teach-1-body')
    bibliography = ('Retained Wikibooks Russian/Grammar/Nominative oldid4213267, prov-u5-indecl-rule: indeclinable means no change according to case or number. '
        'Retained OpenRussian nouns.csv at commit50e210c4803237779cb562bc1abcea529066031c: indeclinable=1 for радио,такси,метро,кино,пальто,кофе,кафе,меню; indeclinable=0 for книга,окно. '
        'The latter rows have different sg_nom and sg_gen forms. Existing Russian display/flag provenance is retained byte-for-byte. '
        'Bounded application inference: classify these already admitted nouns by their recorded behavior, without producing new Russian forms or teaching a general ending-based guess. '
        'Independently authored English on 2026-10-12 within content-audit-2026-10-08; no primary prose or owned-book text copied; final semantic audit pending.')
    added=[]; changes=[]
    def record(text, name):
        r=deepcopy(template); original=bibliography+'\n'+text
        r.update(itemId='prov-ca-u5-behavior-'+name,unitNumber=5,objectiveId=OBJ,sourceRecordId='ca-coverage:'+OBJ+':behavior:'+name,
                 originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text))
        r['synthesis'].update(bibliography=bibliography,objectiveOrClaimBinding=OBJ+':behavior:'+name,
            authorshipDeclaration='Independently authored English on 2026-10-12 from retained lexical fields and definition; no copied primary prose or owned-book text.',applications=[])
        assert not any(x['itemId']==r['itemId'] for x in p['provenanceRecords'])
        p['provenanceRecords'].append(r);added.append(r['itemId'])
    bodies={
        1:'An indeclinable noun keeps the same form for case or number. In these examples, ра́дио and такси́ are indeclinable. Recognize the behavior of the shown noun.',
        2:'The nouns метро́, кино́, пальто́ and ко́фе are also indeclinable. Their forms stay unchanged for case or number. Learn the noun with this behavior; do not infer that every noun with a similar ending behaves the same way.',
        3:'кафе́ and меню́ are two more indeclinable nouns. An unchanged word form does not mean that a sentence has no case relationships. These checks classify the shown noun; they do not test production of a sentence or a complete paradigm.',
        4:'Contrast кни́га and окно́: these nouns are declinable, so they can change for case. Their nominative and genitive forms differ. Choose the behavior of the shown noun: unchanged for case or number, or able to change for case. No new form needs to be invented.'}
    titles={1:'Nouns that keep their form',2:'Four more unchanged nouns',3:'Recognize the behavior',4:'Contrast nouns that can change'}
    for n in range(1,5):
        ident=f'u5-st-teach-indeclinable-{n}'
        if n==4:
            st={'id':ident,'kind':'TEACH','title':titles[n],'body':bodies[n],'referenceIds':['ref-u35-attribution']}
            pos=next(i for i,x in enumerate(session['steps']) if x['id']=='u5-st-check-indeclinable');session['steps'].insert(pos,st)
            objective['instructionStepIds'].append(ident)
        else:
            st=next(x for x in session['steps'] if x['id']==ident);changes.append({'before':deepcopy(st)})
            st.update(title=titles[n],body=bodies[n]);changes[-1]['after']=deepcopy(st)
        record(st['title'],f'teach-{n}-title');record(st['body'],f'teach-{n}-body')
    rem=next(x for x in session['steps'] if x['id']=='u5-st-rem-indeclinable');changes.append({'before':deepcopy(rem)})
    rem.update(title='Review noun behavior',body='\n\n'.join(bodies.values()),exampleIds=list(objective['exampleIds']));changes[-1]['after']=deepcopy(rem)
    record(rem['title'],'rem-title');record(rem['body'],'rem-body')
    for ident,title,body in [('u5-st-guided-indeclinable-1','Guided noun classification','Choose how the shown noun behaves. Read the explanation after choosing.'),
        ('u5-st-check-indeclinable','Independent noun classification','Classify the shown noun by its case behavior. A miss returns to all four explanations and the retained examples.')]:
        st=next(x for x in session['steps'] if x['id']==ident);changes.append({'before':deepcopy(st)});st.update(title=title,body=body);changes[-1]['after']=deepcopy(st)
        record(title,ident+'-title');record(body,ident+'-body')
    prompt='Which behavior applies to the shown noun?';record(prompt,'prompt');record(UNCHANGED,'unchanged-choice');record(CHANGEABLE,'changeable-choice')
    for item in p['items']:
        if item['kind']!='SOURCE_INDECLINABLE':continue
        changes.append({'before':deepcopy(item)});unchanged=item['answer']=='1';assert item['answer'] in ['1','0']
        item.update(prompt=prompt,options=[UNCHANGED,CHANGEABLE],answer=UNCHANGED if unchanged else CHANGEABLE,
            feedback=(f"{item['promptLetter']} is indeclinable: its form stays unchanged for case or number. Choose the unchanged behavior, rather than the behavior of a declinable noun." if unchanged else
                f"{item['promptLetter']} is declinable: it can change for case. Its nominative and genitive forms differ, so the unchanged behavior does not describe this noun."))
        changes[-1]['after']=deepcopy(item);record(item['feedback'],item['id']+'-feedback')
    after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode();PACK.write_bytes(after)
    (HERE/'UNIT5_INDECLINABLE_BEHAVIOR_CORRECTION.json').write_text(json.dumps(dict(baselineCommit='e33612c3073a8ca59913d484c46404095a6bb01e',beforeSha256=hashlib.sha256(raw).hexdigest(),afterSha256=hashlib.sha256(after).hexdigest(),addedProvenanceIds=added,changes=changes,bibliography=bibliography,semanticAcceptance=False),ensure_ascii=False,indent=2)+'\n')
    print('Updated six behavior checks, four teaching cards and complete remediation;',len(added),'pending coverage records')
if __name__=='__main__':main()
