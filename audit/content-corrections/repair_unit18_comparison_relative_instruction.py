#!/usr/bin/env python3
"""Repair English instruction around retained Unit 18 forms; leave source keys untouched."""
import hashlib, json
from copy import deepcopy
from pathlib import Path
HERE=Path(__file__).resolve().parent; ROOT=HERE.parents[1]
PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json'
def sha(s): return hashlib.sha256(s.encode()).hexdigest()
def main():
 raw=PACK.read_bytes(); p=json.loads(raw)
 if any(r['itemId']=='prov-ca-u18-instruction-comparison-objective' for r in p['provenanceRecords']):
  print('Already repaired; retained evidence unchanged'); return
 u=next(u for u in p['units'] if u['number']==18)
 template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-17-72')
 bibliography=('Retained Wikipedia Russian grammar oldid 1369617627 and Wikibooks Russian relative-pronoun records supply all existing Russian forms and glosses. '
  'Primary Cornell Beginning Russian Grammar gr13_a_a.htm and le41_50_i.htm, https://russian.cornell.edu/grammar/html/, consulted 2026-10-11 UTC: comparison types and relative agreement/case. '
  'Cornell gr13_b_d.htm identifies the contextual best meaning of the retained better gloss. Independently authored English; qualified linguistic audit pending.')
 added=[]
 def record(text,obj,name):
  r=deepcopy(template); original=bibliography+'\n'+text
  r.update(itemId='prov-ca-u18-instruction-'+name,unitNumber=18,objectiveId=obj,sourceRecordId='ca-coverage:'+obj+':instruction:'+name,recordType='label',originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text))
  r['synthesis'].update(bibliography=bibliography,objectiveOrClaimBinding=obj+':instruction:'+name,authorshipDeclaration='Independently authored English for Russian Companion, 2026-10-11 UTC; no owned-book prose.',applications=[])
  p['provenanceRecords'].append(r); added.append(r['itemId'])
 lessons={
 'comparison':('Interpret the retained comparison forms',[
  ('Comparing a quality', 'бо́льший — bigger. лу́чший — better. These retained forms concern degree of a quality, rather than naming an unrelated object. The first compares size. The second is the source gloss used by the existing choice; it can mean best in context, so better here is not a universal translation. These full adjective forms can change for gender, number and case; they are not the same construction as an unchanging short comparative. Qualified review of the contextual gloss remains pending.'),
  ('One-word and two-word comparisons', 'красне́е — redder. бо́лее до́брый — kinder. The first is a short comparative with an ending expressing a higher degree of redness; this short form does not change for gender, number or case. The second combines a word meaning more with the ordinary long adjective meaning kind. In this two-word construction the adjective still changes for agreement. Not every adjective has a one-word comparative, and these examples do not give a complete formation rule for unfamiliar adjectives.'),
  ('Comparative versus superlative', 'наибо́льший — the biggest. This retained superlative identifies the greatest size in the relevant group, rather than just a greater degree. Contrast the source labels бо́лее — more, ме́нее — less, and наиме́нее — least. More and less express comparison; least identifies the lowest degree. For наибо́льший select the biggest, not more or less. For бо́лее до́брый select kinder, not least or bigger. These choices recognize existing forms; they do not assess independently forming new comparisons or a complete comparison sentence.')]),
 'relative':('Distinguish which questions from relative clauses',[
  ('Questions selecting an apple or a book', 'Которое яблоко моё? — Which apple is mine? Которую книгу вы хотите? — Which book do you want? These are direct questions asking which member is selected, not relative clauses modifying an earlier noun. In the apple question the selecting form agrees with a neuter singular subject in the nominative. In the book question it agrees with a feminine singular direct object in the accusative. Match the whole question: ownership of an apple differs from wanting a book.'),
  ('A relative clause adds information about a noun', 'Человек, который изменил мир — The man who changed the world. The clause following the comma describes the preceding person; it does not ask which person. The relative form takes masculine singular agreement from that noun, and nominative case because it is the subject of changed inside its own clause. For a relative form, gender and number come from the noun it describes; case comes from the role inside the relative clause, not automatically from the preceding noun.'),
  ('With whom: a preposition inside the relative clause', "Же́нщина, с кото́рой я дружу́ — The woman (who) I'm friends with. The relative clause describes the woman. Its relative form has feminine singular agreement and instrumental case after the preposition meaning with. Russian keeps this preposition before the relative form, while the retained English rendering places with at the end. Compare the woman described by friendship with the man described by changing the world and the two direct questions. Recognizing these meanings does not demonstrate independently choosing a relative case or producing a new clause.")])}
 for slug,(title,cards) in lessons.items():
  obj='obj-u18-'+slug; s=next(s for s in u['sessions'] if s['id']=='u18-s-'+slug)
  s['title']=title; next(o for o in u['objectives'] if o['id']==obj)['title']=title; record(title,obj,slug+'-objective')
  for n,(ct,body) in enumerate(cards,1):
   st=next(st for st in s['steps'] if st['id']==f'u18-st-teach-{slug}-{n}');st.update(title=ct,body=body)
   record(ct,obj,slug+f'-teach-{n}-title');record(body,obj,slug+f'-teach-{n}-body')
  teaches=[st for st in s['steps'] if st['kind']=='TEACH'];s['steps']=teaches+[st for st in s['steps'] if st['kind']!='TEACH']
  rem=next(st for st in s['steps'] if st['kind']=='REMEDIATE');rem.update(title='Review the complete contrast',body='\n\n'.join(body for _,body in cards),exampleIds=list(dict.fromkeys(e for st in teaches for e in st['exampleIds'])))
  record(rem['title'],obj,slug+'-rem-title');record(rem['body'],obj,slug+'-rem-body')
  for kind in ['GUIDED','CHECK']:
   st=next(st for st in s['steps'] if st['kind']==kind);st['body']='Read the shown form and select its English meaning using the distinctions above. A missed check returns to the complete explanation and examples.';record(st['body'],obj,slug+'-'+kind.lower())
 explanations={
 'u18-g-comparison-0':('The retained full adjective compares size.',['better compares quality rather than size.','redder compares redness rather than size.']),
 'u18-i-comparison-1':('This superlative identifies the greatest size in the relevant group.',['more indicates a greater degree without identifying a maximum.','less indicates a lower degree rather than a maximum.']),
 'u18-i-comparison-2':('The word meaning more combines with the adjective meaning kind.',['least identifies the lowest degree rather than a higher degree of kindness.','bigger compares size rather than kindness.']),
 'u18-g-relative-0':('This direct question selects an apple and asks about ownership.',['Which book do you want? asks about a wanted book, not ownership of an apple.','The man who changed the world describes a person rather than asking which apple is owned.']),
 'u18-i-relative-1':('The relative clause describes the woman; its preposition meaning with takes instrumental case.',['The man who changed the world describes a different person and action.','Which apple is mine? asks a direct question about ownership, rather than describing the woman.']),
 'u18-i-relative-2':('The relative clause describes the man; the relative form is the subject of changed in its own clause.',["The woman (who) I'm friends with describes a different person and relationship.",'Which book do you want? asks a direct question about a book rather than describing the man.'])}
 changed=[]
 for i in p['items']:
  if i['id'] not in explanations:continue
  explanation,distractors=explanations[i['id']]
  i['prompt']='Which English meaning matches the shown form?';i['feedback']=i['promptLetter']+' means '+i['answer']+'. '+explanation;i['distractorRationales']=['']+distractors
  i['assessment']['whyAnswerDemonstratesObjective']+='\n'+i['feedback'];i['assessment']['distractorDifference']=['']+distractors
  for text,label in [(i['prompt'],'prompt'),(i['feedback'],'feedback'),(i['assessment']['whyAnswerDemonstratesObjective'],'rationale')]+[(d,'distractor-'+str(n)) for n,d in enumerate(distractors,1)]:record(text,i['objectiveId'],i['id']+'-'+label)
  changed.append(i['id'])
 after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode();PACK.write_bytes(after)
 ledger=dict(schemaVersion=1,previousHead='1f26a6c2eb064d3b6b585c9a45612a318b18fc84',unitNumber=18,disposition='IMPLEMENTED_PENDING_FINAL_AUDIT',beforeCurriculumFileSha256=hashlib.sha256(raw).hexdigest(),afterCurriculumFileSha256=hashlib.sha256(after).hexdigest(),addedProvenanceIds=added,changedItemIds=changed,bibliography=bibliography,scope='Two instructional sessions, six teaching cards reordered before guided practice, complete remediation, six item English explanations. Original source records, forms, keys, mappings and advancement preserved. Contextual better/best gloss remains pending qualified review.')
 (HERE/'UNIT18_COMPARISON_RELATIVE_INSTRUCTION_CORRECTION.json').write_text(json.dumps(ledger,ensure_ascii=False,indent=2)+'\n')
 print('Unit 18 repaired;',len(added),'new pending provenance records')
if __name__=='__main__':main()
