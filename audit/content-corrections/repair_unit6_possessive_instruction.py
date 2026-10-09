#!/usr/bin/env python3
"""Explain the retained possessive contrasts without changing keys or Russian forms."""
import hashlib,json
from copy import deepcopy
from pathlib import Path
HERE=Path(__file__).resolve().parent;ROOT=HERE.parents[1];PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json';OBJ='obj-u6-possessive'
def sha(s):return hashlib.sha256(s.encode()).hexdigest()
def main():
 raw=PACK.read_bytes();p=json.loads(raw);u=next(u for u in p['units'] if u['number']==6);s=next(s for s in u['sessions'] if s['id']=='u6-s-possessive')
 if any(r['itemId']=='prov-ca-u6-worked-teach-1-body' for r in p['provenanceRecords']):print('Already repaired; retained evidence unchanged');return
 template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-6-190')
 bibliography=('Cornell Russian Language Program, Leed lessons 9-12, https://russian.cornell.edu/russian.web/courses/121/Leed_Less_9-12.pdf, '
  'printed pages 31 and 37 (PDF pages 1 and 7), text retrieved 2026-10-09: adjective/possessive agreement in gender, number and case. '
  'Retained OpenRussian commit 50e210c4803237779cb562bc1abcea529066031c, adjectives.csv:мой#decl_m_nom, #decl_f_nom, #decl_n_nom, #decl_pl_nom and adjectives.csv:твой#decl_m_nom, #decl_f_nom; '
  'https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/adjectives.csv. '
  'Existing retained phrases and contrast: prov-ex-u6-possessive-0-ru, prov-ex-u6-possessive-1-ru, prov-ca-6-190, prov-ca-6-191, prov-ca-6-195 and prov-ca-6-196. '
  'Application inference: use the agreement rule to explain those already retained contrasts. '
  'Independently authored English on 2026-10-09 within the content-audit-2026-10-08 batch; no primary-source prose or new Russian form is copied. Final semantic audit pending.')
 retained_ids=['prov-ex-u6-possessive-0-ru', 'prov-ex-u6-possessive-1-ru', 'prov-ca-6-190', 'prov-ca-6-191', 'prov-ca-6-195', 'prov-ca-6-196']
 assert all(any(r['itemId']==id for r in p['provenanceRecords']) for id in retained_ids)
 added=[]
 def record(text,name):
  r=deepcopy(template);original=bibliography+'\n'+text;r.update(itemId='prov-ca-u6-worked-'+name,recordType='label',sourceRecordId='ca-coverage:'+OBJ+':worked:'+name,originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text))
  r['synthesis'].update(bibliography=bibliography,objectiveOrClaimBinding=OBJ+':worked:'+name,authorshipDeclaration='Independently authored English for Russian Companion on 2026-10-09, applying reference rules to previously retained Russian forms; no owned-book prose.',applications=[])
  assert not any(x['itemId']==r['itemId'] for x in p['provenanceRecords']);p['provenanceRecords'].append(r);added.append(r['itemId'])
 bodies={
  1:'In мой стол, стол is masculine singular. In моя собака, собака is feminine singular. Both phrases mean my plus a noun, but the Russian form of my changes to match the noun.',
  2:'Possessives such as мо́й and тво́й agree with the noun they describe in gender, number and case. Choose the form from the noun, not from the gender of its owner. Start with the nominative forms below, then compare the feminine accusative example.',
  3:'Наш means our; Ваш means your. In the worked phrase на́ша сестра́, both words are feminine singular nominative. To put this phrase in the direct-object accusative, change both words: на́ша сестра́ becomes на́шу сестру́. Keep the same noun and feminine singular agreement. на́шего бра́та changes the referent, and на́ши is plural.',
  4:'In the nominative singular, мо́й is masculine and моя́ is feminine. For стол choose мо́й; for собака choose моя́. The form follows the noun being described.',
  5:'моё is the neuter singular nominative form of my. мои́ is the plural nominative form. A singular noun does not take the plural form in these choices.',
  6:'тво́й is masculine singular nominative; твоя́ is feminine singular nominative. Both mean your. To change the masculine form to the feminine form while keeping nominative singular, choose твоя́.'}
 titles={1:'My: agreement with the noun',2:'Match gender, number and case',3:'Our: a worked case change',4:'Masculine and feminine forms',5:'Neuter and plural forms',6:'Your: masculine and feminine'}
 for n in bodies:
  st=next(st for st in s['steps'] if st['id']==f'u6-st-teach-possessive-{n}');st['body']=bodies[n];st['title']=titles[n];record(st['body'],f'teach-{n}-body');record(st['title'],f'teach-{n}-title')
 rem=next(st for st in s['steps'] if st['kind']=='REMEDIATE');rem['body']='\n\n'.join(bodies[n] for n in [2,1,4,5,3,6]);rem['title']='Review the agreement rule';rem['exampleIds']=list(next(o for o in u['objectives'] if o['id']==OBJ)['exampleIds']);record(rem['body'],'rem-body');record(rem['title'],'rem-title')
 for kind,text,name in [('GUIDED','Choose the form that matches the noun. Then read why the other forms do not fit.','guided-body'),('CHECK','Use gender, number and case to choose a form. If you miss a check, review the explanation.','check-body')]:
  st=next(st for st in s['steps'] if st['kind']==kind);st['body']=text;record(text,name)
 for id,gender,answer,wrong in [('u6-g-possessive-0','feminine','моя́','мо́й'),('u6-i-possessive-1','masculine','мо́й','моя́')]:
  i=next(i for i in p['items'] if i['id']==id);i['prompt']=f'Which nominative form meaning my agrees with the shown {gender} singular noun?';i['feedback']=f"{i['promptLetter']} is {gender} singular, so the nominative form is {answer}.";a=i['assessment'];a['whyAnswerDemonstratesObjective']=i['feedback']
  differences=[]
  for option in i['options']:
   if option==answer:differences.append('')
   elif option=='мои́':differences.append('мои́ is plural nominative; the noun here is singular.')
   else:
    other='masculine' if option=='мо́й' else 'feminine';differences.append(f'{option} is {other} singular nominative; it does not agree with the {gender} singular noun.')
  i['distractorRationales']=differences;a['distractorDifference']=differences
  prefix='guided' if not a['independent'] else 'check-1';record(i['prompt'],prefix+'-prompt');record(i['feedback'],prefix+'-feedback')
  for n,text in enumerate(differences):
   if text:record(text,prefix+'-distractor-'+str(n))
 assert len(added)==23,added
 after=(json.dumps(p,ensure_ascii=False,indent=2)+'\n').encode();PACK.write_bytes(after)
 e=dict(schemaVersion=1,previousHead='278afd701b775624924893ce11afc92a5c10bae3',unitNumber=6,objectiveId=OBJ,disposition='IMPLEMENTED_PENDING_FINAL_AUDIT',beforeCurriculumFileSha256=hashlib.sha256(raw).hexdigest(),afterCurriculumFileSha256=hashlib.sha256(after).hexdigest(),addedProvenanceIds=added,retainedRecordIds=retained_ids,changedItemIds=['u6-g-possessive-0','u6-i-possessive-1'],bibliography=bibliography,scope='Existing possessive session explanations, titles, remediation examples and two items English prompt/feedback/rationale fields only; keys, Russian forms and advancement unchanged.')
 (HERE/'UNIT6_POSSESSIVE_INSTRUCTION_CORRECTION.json').write_text(json.dumps(e,ensure_ascii=False,indent=2)+'\n');print('Added possessive explanations and worked case contrast; 23 pending coverage records; keys and Russian forms unchanged')
if __name__=='__main__':main()
