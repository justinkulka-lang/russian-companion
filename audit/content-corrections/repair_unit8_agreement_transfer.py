#!/usr/bin/env python3
"""Replace repeated adjective evidence with the exact supplied blue adjective pair."""
import json,hashlib,re
from copy import deepcopy
from pathlib import Path
HERE=Path(__file__).resolve().parent;ROOT=HERE.parents[1];PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json';PIN_FILE=ROOT/'app/src/main/java/dev/russiancompanion/v2/curriculum/Unit8AgreementPins.kt'
BASELINE='2f06e472876d5248a403b8f523305918afca65fa';OBJ='obj-u8-agreement'
def sha(s):return hashlib.sha256(s.encode()).hexdigest()
def visible(s):return re.sub(r'\[\[([^\[\]]+)\]\]',lambda m:m[1].split('|',1)[-1],s).replace("'''",'').replace("''",'')
def main():
 raw=PACK.read_bytes();p=json.loads(raw)
 if next(i for i in p['items'] if i['id']=='u8-i-agreement-3')['promptLetter']=='синий':print('Already repaired; evidence unchanged');return
 f=ROOT/'catalog/sources/units-6-10/wikibooks/adjectives-4292379.txt';source=f.read_text();span=source[source.index('===Soft endings==='):source.index('===5- and 7-letter spelling rules===')].rstrip();assert 'синий' in visible(span) and 'синие' in visible(span)
 basis=('Complete retained Wikibooks Adjectives4292379, especially nominative/soft-ending examples for синий and синие книги; actual retained Wikibooks copyrights4622060 and CC BY-SA4.0 contributor attribution. Exact unaccented source words selected with existing mechanical emphasis/link-label unfolding. No generated native morphology, apostrophe-to-accent conversion, new stress or punctuation, private Penguin prose, media or new source/rights declaration. '
 'Retain the original OpenRussian50e210c4803237779cb562bc1abcea529066031c guided new/new-plural pair and independent possessive/demonstrative pairs unchanged. The independent adjective now uses a distinct lexeme and ending shape from the guided adjective. '
 'Scope is recognition of these supplied nominative singular/plural pairs, not agreement production in arbitrary sentences, every case or a universal -ний/all-adjective/short-adjective algorithm. Broad and inaccurate original source claims about all adjectives, Latin-letter ending spelling, conjugation and short-form permanence/temporariness are not adopted; the source is preserved unchanged. '
 'All current teaching precedes guided practice; remediation restores every card/example. Independently authored English2026-10-13 within content-audit-2026-10-08. Qualified source/rights/linguistic acceptance pending.')
 template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-u5-behavior-teach-1-body');primary=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ex-u8-agreement-0-ru');added=[];native=[];changes=[]
 def record(value,name,original=None,apps=None,rid=None):
  original=original or basis+'\n'+value;r=deepcopy(template);r.update(itemId='prov-ca-u8-agreement-transfer-'+name,unitNumber=8,objectiveId=OBJ,recordType='label',sourceRecordId=rid or 'ca-coverage:'+OBJ+':transfer:'+name,originalSourceText=original,shippedText=value,sourceHash=sha(original),contentHash=sha(value));r['synthesis'].update(bibliography=basis,authorshipDeclaration='Independently authored English2026-10-13 from complete frozen source/form context; no native generation or private prose.',objectiveOrClaimBinding=OBJ+':transfer:'+name,applications=apps or []);p['provenanceRecords'].append(r);added.append(r['itemId']);return r['itemId']
 for suffix,text in [('singular','синий'),('plural','синие')]:
  r=deepcopy(primary);r.pop('originalSourceTextRef',None);r.update(itemId='prov-u8-agreement-transfer-native-'+suffix,recordType='meaning',sourceRecordId='adjectives-independent-blue:'+span[:77],originalSourceText=span,shippedText=text,sourceHash=sha(span),contentHash=sha(text));p['provenanceRecords'].append(r);native.append(deepcopy(r));added.append(r['itemId'])
 item=next(i for i in p['items'] if i['id']=='u8-i-agreement-3');old=deepcopy(item)
 feedback='синий is the supplied masculine singular nominative form; синие is the plural nominative form of the same dark-blue adjective. In the retained nominative example синие книги, the modifier is plural. The source supplies these words without written stress; this check recognizes this pair, not every adjective ending or case.'
 reasons=['','синий is the supplied masculine singular form, so it does not provide the requested plural nominative form.']
 a={'cue':'синий','answer':'синие','distractors':['синий']};rid='ca-rule-context:u8-i-agreement-3'
 def field(s):return str(len(s.encode()))+':'+s
 canonical='tuple-v1:1;'+field(a['cue'])+field(a['answer'])+'1;'+field(a['distractors'][0]);original=basis+'\nComplete raw context:\n'+span+'\nContext explanation:\n'+feedback+'\nTuple-v1:'+canonical;ids=[record(v,'u8-i-agreement-3-'+str(n),original,[a],rid) for n,v in enumerate([a['cue'],a['answer']]+a['distractors'])]
 item.update(kind='CONTRAST_SELECTION',sourceId=template['sourceId'],promptLetter='синий',answer='синие',options=['синие','синий'],feedback=feedback,distractorRationales=reasons)
 item['assessment'].update(contrastSetId=item['id'],evidenceMode='STATED_APPLICATION',relationKind='STATED_APPLICATION',responseKind='RUSSIAN_TOKEN',relationParticipantIds=ids,answerSourceIds=[ids[1]],distractorSourceIds=['',ids[2]],distractorDifference=reasons,contrastMembers=item['options'],whyAnswerDemonstratesObjective=feedback)
 changes.append({'before':old,'after':deepcopy(item)});record(item['prompt'],'item-prompt');record(feedback,'item-feedback');record(reasons[1],'item-rationale')
 unit=p['units'][7];obj=next(o for o in unit['objectives'] if o['id']==OBJ);before=deepcopy(obj);obj['instructionStepIds'].append('u8-st-teach-agreement-6');changes.append({'before':before,'after':deepcopy(obj)})
 session=next(s for s in unit['sessions'] if s['id']=='u8-s-agreement');before=deepcopy(session)
 cards={
  1:('Recognize the supplied plural modifiers','синие книги — the dark blue books. Наши — Our. These supplied examples introduce plural modifiers or plural referents. This lesson asks you to recognize the listed form pairs; it does not demonstrate producing agreement for every noun or case.'),
  2:('Read the adjective with its noun','In the retained nominative example синие книги — the dark blue books, the adjective modifies a plural noun. The complete phrase provides the plural reading here; do not infer a universal noun-case interpretation from an isolated spelling.'),
  3:('Compare the possessive forms','мо́й and мои́ both retain the meaning my. In the supplied fields, мо́й is masculine singular nominative and мои́ is plural nominative. The requested plural is a number distinction, not a different owner or a change of English meaning.'),
  4:('Work the guided adjective pair','но́вый and но́вые both mean new. The supplied masculine singular nominative form is но́вый; the plural nominative is но́вые. Guided practice uses this pair. Recognize the whole form and requested number; this does not establish every case ending.'),
  5:('Compare the demonstrative forms','э́та and э́ти both retain the meaning this. The supplied feminine singular nominative form is э́та; the plural nominative form is э́ти. These paired fields do not require a different English meaning or a plural form with a feminine-only gender.'),
  6:('Apply the distinction to a different adjective','The retained nominative section supplies синий as the masculine singular form and синие in синие книги as the plural form of the same dark-blue adjective. This source prints these words without written stress; no stress mark is inserted. The independent adjective check uses this different lexeme and ending shape from the guided new/new-plural pair. Choose the supplied plural rather than repeat its singular form. These examples are bounded nominative recognition, not a rule that every adjective or every word with similar final letters changes identically.')}
 for n,(title,body) in cards.items():
  if n==6:
   step={'id':'u8-st-teach-agreement-6','kind':'TEACH','title':title,'body':body,'referenceIds':['ref-u610-attribution'],'exampleIds':['ex-u8-agreement-0']};session['steps'].append(step)
  else:
   step=next(s for s in session['steps'] if s['id']=='u8-st-teach-agreement-'+str(n));step.update(title=title,body=body)
  record(title,'teach-'+str(n)+'-title');record(body,'teach-'+str(n)+'-body')
 teaching=[s for s in session['steps'] if s['kind']=='TEACH'];session['steps']=teaching+[s for s in session['steps'] if s['kind']!='TEACH'];rem=next(s for s in session['steps'] if s['kind']=='REMEDIATE');rem['body']='\n\n'.join(s['body'] for s in teaching);rem['exampleIds']=list(dict.fromkeys(e for s in teaching for e in s.get('exampleIds',[])));record(rem['body'],'remediation-body');changes.append({'before':before,'after':deepcopy(session)})
 q=lambda s:json.dumps(s,ensure_ascii=False)
 rows=['        Native('+', '.join(q(r[k]) for k in ['itemId','sourceUrl','sourceRecordId','sourceHash','shippedText'])+'),' for r in native]
 code='''package dev.russiancompanion.v2.curriculum
import java.security.MessageDigest
/** Supplied unaccented blue adjective evidence; no universal morphology/acceptance claim. */
internal object Unit8AgreementPins {
    private data class Native(val id:String,val url:String,val record:String,val hash:String,val text:String)
    private val native=listOf(
ROWS
    )
    private fun sha(s:String)=MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8)).joinToString(""){"%02x".format(it.toInt() and 255)}
    fun matches(pack:V2CurriculumPack):Boolean {
        val obj=pack.unit(8)?.objectives?.singleOrNull{it.id=="obj-u8-agreement"}?:return false
        if(obj.kind!="RECOGNITION" || !obj.gating || obj.checkItemIds!=listOf("u8-i-agreement-1","u8-i-agreement-2","u8-i-agreement-3"))return false
        if(!native.all{pin->pack.provenanceRecords.singleOrNull{it.itemId==pin.id}?.let{r->
            r.unitNumber==8 && r.objectiveId==obj.id && r.sourceId=="src-wikibooks-russian-cc-by-sa" && r.sourceRevision=="4292379" &&
            r.sourceUrl==pin.url && r.sourceRecordId==pin.record && r.sourceHash==pin.hash && sha(r.originalSourceText)==pin.hash &&
            r.shippedText==pin.text && r.contentHash==sha(pin.text) && pin.text in V2Validator.unfoldWikiLabels(r.originalSourceText) &&
            r.transformation=="selection" && r.licenseId=="CC BY-SA 4.0"
        }==true})return false
        val guided=obj.practiceStepIds.flatMap{pack.step(it)?.second?.itemIds.orEmpty()}
        if(guided!=listOf("u8-g-agreement-0"))return false
        val item=pack.item("u8-i-agreement-3")?:return false
        return item.assessment?.independent==true && item.assessment.testedFeature=="PLURAL_AGREEMENT" &&
            item.promptLetter!=pack.item(guided.single())?.promptLetter && CompleteRuleContextPins.itemMatches(pack,item) && V2Validator.structureValid(pack,item).isEmpty()
    }
}
'''.replace('ROWS','\n'.join(rows));PIN_FILE.write_text(code);after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode();PACK.write_bytes(after)
 (HERE/'UNIT8_AGREEMENT_TRANSFER_CORRECTION.json').write_text(json.dumps(dict(baselineCommit=BASELINE,beforeSha256=hashlib.sha256(raw).hexdigest(),afterSha256=hashlib.sha256(after).hexdigest(),changes=changes,addedProvenanceIds=added,nativeSelections=native,contextIdentities={rid:{'sourceHash':sha(original),'applications':[a],'objectiveId':OBJ}},bibliography=basis,nativePinCodeSha256=sha(code),frozenPublicSources={str(f.relative_to(ROOT)):sha(source)},semanticAcceptance=False,productiveAgreementClaim=False,universalAdjectiveAlgorithmClaim=False),ensure_ascii=False,indent=2)+'\n');print('Added',len(added),'records;',len(native),'native selections; replaced one independent pair, five revised teaching cards plus one added')
if __name__=='__main__':main()
