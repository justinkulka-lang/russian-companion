#!/usr/bin/env python3
"""Replace opposite-label credit with a distinct already admitted active present form."""
import hashlib, json
from copy import deepcopy
from pathlib import Path
HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
PACK = ROOT / 'app/src/main/assets/content/v2-curriculum.json'
BASELINE = '169b6d4cd25c6e13795019fc5e8f78d02bc8cf28'
def sha(s): return hashlib.sha256(s.encode()).hexdigest()
def canonical(a):
    def field(s): return str(len(s.encode()))+':'+s
    return 'tuple-v1:1;'+field(a['cue'])+field(a['answer'])+str(len(a['distractors']))+';'+''.join(field(s) for s in a['distractors'])
def main():
    raw=PACK.read_bytes();p=json.loads(raw)
    if any(r['itemId']=='prov-ca-u29-time-u29-i-active-2-0' for r in p['provenanceRecords']):
        print('Already repaired; evidence unchanged');return
    source=ROOT/'catalog/sources/units-26-30/wikipedia/russian-grammar-1369617627.txt'
    text=source.read_text();assert "==== Active present participle ====\nЛю́ди, '''живу́щие'''" in text
    template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-u5-behavior-teach-1-body')
    admitted=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ex-u29-active-2-ru')
    assert admitted['shippedText']=='живу́щие' and 'living' in admitted['originalSourceText']
    bibliography=('Retained Wikipedia Russian grammar oldid1369617627, catalog/sources/units-26-30/wikipedia/russian-grammar-1369617627.txt, CC BY-SA 4.0 under retained FREEZE rights attestation: complete Adjectival participle introduction and Active present participle / Active past participle sections. '
        'The already admitted living form is the example immediately under Active present participle; the separate reading examples distinguish past and present. This bounded classification identifies participial tense, not the time of an unseen sentence or a productive formation algorithm. '
        'Original reversal followed its literal prompt; its defect was awarding time-comprehension credit for choosing the complementary label, not mistranslating a form. Replace it with the distinct already admitted living form. '
        'Independently authored English on 2026-10-13 within content-audit-2026-10-08; no new Russian forms, copied primary prose or private Penguin text; qualified semantic audit pending.')
    changes=[];added=[]
    def record(value,name,original=None,applications=None,rid=None):
        original=original or bibliography+'\n'+value;r=deepcopy(template)
        r.update(itemId='prov-ca-u29-time-'+name,unitNumber=29,objectiveId='obj-u29-active',recordType='label',sourceRecordId=rid or 'ca-coverage:obj-u29-active:time:'+name,
            originalSourceText=original,shippedText=value,sourceHash=sha(original),contentHash=sha(value))
        r['synthesis'].update(bibliography=bibliography,authorshipDeclaration='Independently authored English on 2026-10-13 from retained complete participle context; no copied primary prose or private book text.',objectiveOrClaimBinding='obj-u29-active:time:'+name,applications=applications or [])
        p['provenanceRecords'].append(r);added.append(r['itemId']);return r['itemId']
    item=next(i for i in p['items'] if i['id']=='u29-i-active-2');before=deepcopy(item)
    assert 'other time' in item['prompt'] and item['promptLetter']=='чита́вшая'
    feedback='живу́щие means living and is the active present participle in the supplied example: the people perform the living action. Select in the present for this form itself. The past label describes a different participial tense; living gives the lexical meaning rather than the requested time classification. This identifies the admitted form, not the tense of an unseen sentence.'
    rationales=['','The past label names a different participial tense; the supplied living form is active present.','Living gives the lexical meaning, rather than the requested present or past time classification.']
    item.update(sourceId=template['sourceId'],prompt='Which English time label describes the shown active participle itself?',promptLetter='живу́щие',options=['in the present','in the past','living'],answer='in the present',feedback=feedback,distractorRationales=rationales)
    app={'cue':item['promptLetter'],'answer':item['answer'],'distractors':item['options'][1:]};rid='ca-rule-context:'+item['id']
    original=bibliography+'\nContext explanation:\n'+feedback+'\nTuple-v1:'+canonical(app)
    ids=[record(v,item['id']+'-'+str(n),original,[app],rid) for n,v in enumerate([app['cue'],app['answer']]+app['distractors'])]
    item['assessment'].update(evidenceMode='STATED_APPLICATION',relationKind='STATED_APPLICATION',relationParticipantIds=ids,answerSourceIds=[ids[1]],distractorSourceIds=['',ids[2],ids[3]],distractorDifference=rationales,contrastMembers=item['options'],whyAnswerDemonstratesObjective=feedback)
    changes.append({'before':before,'after':deepcopy(item)})
    for field in ['prompt','feedback']:record(item[field],item['id']+'-'+field)
    for n,v in enumerate(rationales):
        if v:record(v,item['id']+'-rationale-'+str(n))
    unit=next(u for u in p['units'] if u['number']==29);session=next(s for s in unit['sessions'] if s['id']=='u29-s-active')
    bodies={
        'u29-st-teach-active-2':'For чита́ющая, select in the present when asked for its time label. For чита́вшая, select in the past for its own time label. Classify the displayed form itself. Selecting the other time label would test following a reversal instruction rather than interpreting the participle. The gloss that read gives an action meaning rather than the requested time classification.',
        'u29-st-teach-active-3':'The retained живу́щие means living. In the supplied example, people perform the living action; this is an active present participle. Its time classification is in the present, while living is its lexical meaning. This is recognition of the admitted example, not a general algorithm for generating participles of unfamiliar verbs. Interpreting an isolated form does not establish the complete tense or actor of a sentence that has not been supplied.'}
    for step in session['steps']:
        if step['id'] in bodies:
            old=deepcopy(step);step['body']=bodies[step['id']];changes.append({'before':old,'after':deepcopy(step)});record(step['body'],step['id']+'-body')
    rem=next(s for s in session['steps'] if s['kind']=='REMEDIATE');old=deepcopy(rem)
    rem['body']='\n\n'.join(s['body'] for s in session['steps'] if s['kind']=='TEACH');changes.append({'before':old,'after':deepcopy(rem)});record(rem['body'],rem['id']+'-body')
    after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode();PACK.write_bytes(after)
    (HERE/'UNIT29_ACTIVE_TIME_CORRECTION.json').write_text(json.dumps(dict(baselineCommit=BASELINE,beforeSha256=hashlib.sha256(raw).hexdigest(),afterSha256=hashlib.sha256(after).hexdigest(),frozenPublicSource=str(source.relative_to(ROOT)),frozenPublicSourceSha256=hashlib.sha256(source.read_bytes()).hexdigest(),addedProvenanceIds=added,changes=changes,contextIdentities={rid:{'sourceHash':sha(original),'applications':[app],'objectiveId':'obj-u29-active'}},bibliography=bibliography,semanticAcceptance=False),ensure_ascii=False,indent=2)+'\n')
    print('Replaced one reversed-time check, updated two teaching cards/remediation;',len(added),'pending coverage/context records')
if __name__=='__main__':main()
