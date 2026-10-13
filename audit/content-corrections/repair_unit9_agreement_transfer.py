#!/usr/bin/env python3
"""Admit a distinct masculine noun from an exact retained one/hour example."""
import json,hashlib,re
from copy import deepcopy
from pathlib import Path
HERE=Path(__file__).resolve().parent;ROOT=HERE.parents[1];PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json';PIN_FILE=ROOT/'app/src/main/java/dev/russiancompanion/v2/curriculum/Unit9AgreementPins.kt'
BASELINE='5612180322ccf9c38a39def22f3640c7477a9d0d';OBJ='obj-u9-odindva'
def sha(s):return hashlib.sha256(s.encode()).hexdigest()
def visible(s):return re.sub(r'\[\[([^\[\]]+)\]\]',lambda m:m[1].split('|',1)[-1],s).replace("'''",'').replace("''",'')
def main():
 raw=PACK.read_bytes();p=json.loads(raw)
 if next(i for i in p['items'] if i['id']=='u9-i-odindva-4')['promptLetter']=='час':print('Already repaired; evidence unchanged');return
 f=ROOT/'catalog/sources/units-6-10/wikibooks/numbers-4371358.txt';source=f.read_text();span=source[source.index('==Use of numerals in context=='):source.index('==Ordinal numbers==')].rstrip();assert '* один час' in span
 basis=('Complete retained Wikibooks Russian/Numbers4371358, one/gender/number paragraph and complete additional examples including один час; actual retained copyrights4622060/CC BY-SA4.0 contributor-history attribution. Exact unaccented source words selected with existing mechanical link-label/emphasis unfolding. No generated native morphology, new stress/punctuation, private Penguin prose/media or new source/rights declaration. '
 'Preserve the correct guided мальчик/один pair and independent девушка/одна, животное/одно, джинсы/одни, девушки/две scored items whole and unchanged. Replace only repeated independent masculine cue with час from the supplied один час example. '
 'This recognizes the supplied masculine one agreement with a distinct noun, not every numeral declension, every noun/case or productive sentence formation. Original overly simplified compound-numeral last-digit statements and the assertion that non-nominative constructions are uncommon are not adopted; raw context retained unchanged. Earlier whole-numeral teen-exception guard remains. '
 'All six teaching cards precede practice and remediation restores every original example and all cards. Independently authored English2026-10-13 within content-audit-2026-10-08; qualified source/rights/linguistic acceptance pending.')
 template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-u5-behavior-teach-1-body');primary=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ex-u9-odindva-0-ru');added=[];native=[];changes=[]
 def record(value,name,original=None,apps=None,rid=None):
  original=original or basis+'\n'+value;r=deepcopy(template);r.update(itemId='prov-ca-u9-agreement-transfer-'+name,unitNumber=9,objectiveId=OBJ,recordType='label',sourceRecordId=rid or 'ca-coverage:'+OBJ+':transfer:'+name,originalSourceText=original,shippedText=value,sourceHash=sha(original),contentHash=sha(value));r['synthesis'].update(bibliography=basis,authorshipDeclaration='Independently authored English from complete frozen source context; no native generation/private prose.',objectiveOrClaimBinding=OBJ+':transfer:'+name,applications=apps or []);p['provenanceRecords'].append(r);added.append(r['itemId']);return r['itemId']
 for suffix,text in [('noun','час'),('masculine','один'),('feminine','одна')]:
  r=deepcopy(primary);r.pop('originalSourceTextRef',None);r.update(itemId='prov-u9-agreement-transfer-native-'+suffix,recordType='meaning',sourceRecordId='numbers-independent-hour:'+span[:77],originalSourceText=span,shippedText=text,sourceHash=sha(span),contentHash=sha(text));p['provenanceRecords'].append(r);native.append(deepcopy(r));added.append(r['itemId'])
 item=next(i for i in p['items'] if i['id']=='u9-i-odindva-4');old=deepcopy(item)
 feedback='The supplied example один час uses masculine singular один with час. одна is the feminine singular form shown with девушка, so it does not agree with this noun here. Both forms mean one; the distinction is grammatical gender, not a different number. This recognizes the supplied counting form, not every declined case of the numeral.'
 reasons=['','одна is the feminine singular form; the supplied example with час uses masculine singular один.']
 a={'cue':'час','answer':'один','distractors':['одна']};rid='ca-rule-context:u9-i-odindva-4'
 def field(s):return str(len(s.encode()))+':'+s
 canonical='tuple-v1:1;'+field(a['cue'])+field(a['answer'])+'1;'+field(a['distractors'][0]);original=basis+'\nComplete raw context:\n'+span+'\nContext explanation:\n'+feedback+'\nTuple-v1:'+canonical;ids=[record(v,'u9-i-odindva-4-'+str(n),original,[a],rid) for n,v in enumerate([a['cue'],a['answer']]+a['distractors'])]
 item.update(kind='CONTRAST_SELECTION',sourceId=template['sourceId'],promptLetter='час',feedback=feedback,distractorRationales=reasons)
 item['assessment'].update(contrastSetId=item['id'],evidenceMode='STATED_APPLICATION',relationKind='STATED_APPLICATION',responseKind='RUSSIAN_TOKEN',relationParticipantIds=ids,answerSourceIds=[ids[1]],distractorSourceIds=['',ids[2]],distractorDifference=reasons,contrastMembers=item['options'],whyAnswerDemonstratesObjective=feedback)
 changes.append({'before':old,'after':deepcopy(item)});record(item['prompt'],'item-prompt');record(feedback,'item-feedback');record(reasons[1],'item-rationale')
 session=next(s for s in p['units'][8]['sessions'] if s['id']=='u9-s-odindva');before=deepcopy(session)
 cards={
  1:('Choose one by gender','один мальчик — one boy; одна девушка — one girl. For these singular counting examples, one agrees with the noun: masculine один and feminine одна. Both still mean one. Guided practice uses мальчик.'),
  2:('Choose the form of two','два is the form for masculine and neuter counted nouns; две is the feminine form. The supplied example две девушки — two girls uses feminine две. The following noun is genitive singular in this counting construction; the numeral choice still follows its gender.'),
  3:('Recognize neuter and plural-form one','одно животное — one animal uses neuter singular одно. одни джинсы — one [pair of] jeans uses plural одни with the plural-form noun. The plural numeral here does not mean several pairs; these are different gender/number forms of one.'),
  4:('Keep the value while changing agreement','одна́ and одно́ both represent 1 in the retained cardinal table. They are feminine and neuter singular forms, respectively. The existing table stress is retained; the separate counting examples are printed without added stress.'),
  5:('Distinguish plural one from feminine two','одни́ represents 1 in the supplied plural-form example, while две represents 2 with a feminine noun. Distinguish одни джинсы from две девушки by the complete supplied construction, rather than treating every plural-looking form as two.'),
  6:('Apply masculine one to a different noun','оди́н — one and два — two retain their supplied values. The additional source example один час supplies masculine singular один with час. The independent masculine check uses this different noun from guided мальчик. Choose один rather than feminine одна for час in this counting context; this is supplied-form recognition, not all numeral cases or unrestricted sentence production.')}
 for n,(title,body) in cards.items():
  step=next(s for s in session['steps'] if s['id']=='u9-st-teach-odindva-'+str(n));step.update(title=title,body=body);record(title,'teach-'+str(n)+'-title');record(body,'teach-'+str(n)+'-body')
 teaching=[s for s in session['steps'] if s['kind']=='TEACH'];session['steps']=teaching+[s for s in session['steps'] if s['kind']!='TEACH'];rem=next(s for s in session['steps'] if s['kind']=='REMEDIATE');rem['body']='\n\n'.join(s['body'] for s in teaching);rem['exampleIds']=list(dict.fromkeys(e for s in teaching for e in s.get('exampleIds',[])));record(rem['body'],'remediation-body')
 for kind,body in [('GUIDED','Choose the form of one that agrees with the shown noun in the supplied counting example.'),('CHECK','Choose the supplied form of one or two for the noun and counting context. A missed check returns to the complete explanation.')]:
  next(s for s in session['steps'] if s['kind']==kind)['body']=body;record(body,kind.lower()+'-body')
 changes.append({'before':before,'after':deepcopy(session)})
 q=lambda s:json.dumps(s,ensure_ascii=False)
 rows=['        Native('+', '.join(q(r[k]) for k in ['itemId','sourceUrl','sourceRecordId','sourceHash','shippedText'])+'),' for r in native]
 code='''package dev.russiancompanion.v2.curriculum
import java.security.MessageDigest
/** Exact one/hour counting evidence; no all-case or productive mastery claim. */
internal object Unit9AgreementPins {
    private data class Native(val id:String,val url:String,val record:String,val hash:String,val text:String)
    private val native=listOf(
ROWS
    )
    private fun sha(s:String)=MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8)).joinToString(""){"%02x".format(it.toInt() and 255)}
    fun matches(pack:V2CurriculumPack):Boolean {
        val obj=pack.unit(9)?.objectives?.singleOrNull{it.id=="obj-u9-odindva"}?:return false
        if(obj.kind!="RECOGNITION" || !obj.gating || obj.checkItemIds!=listOf("u9-i-odindva-1","u9-i-odindva-2","u9-i-odindva-3","u9-i-odindva-4","u9-i-odindva-5"))return false
        if(!native.all{pin->pack.provenanceRecords.singleOrNull{it.itemId==pin.id}?.let{r->
            r.unitNumber==9 && r.objectiveId==obj.id && r.sourceId=="src-wikibooks-russian-cc-by-sa" && r.sourceRevision=="4371358" &&
            r.sourceUrl==pin.url && r.sourceRecordId==pin.record && r.sourceHash==pin.hash && sha(r.originalSourceText)==pin.hash &&
            r.shippedText==pin.text && r.contentHash==sha(pin.text) && pin.text in V2Validator.unfoldWikiLabels(r.originalSourceText) &&
            r.transformation=="selection" && r.licenseId=="CC BY-SA 4.0"
        }==true})return false
        val guided=obj.practiceStepIds.flatMap{pack.step(it)?.second?.itemIds.orEmpty()}
        if(guided!=listOf("u9-g-odindva-0"))return false
        val item=pack.item("u9-i-odindva-4")?:return false
        return item.assessment?.independent==true && item.assessment.testedFeature=="ODIN_MASCULINE" &&
            item.promptLetter!=pack.item(guided.single())?.promptLetter && CompleteRuleContextPins.itemMatches(pack,item) && V2Validator.structureValid(pack,item).isEmpty()
    }
}
'''.replace('ROWS','\n'.join(rows));PIN_FILE.write_text(code);after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode();PACK.write_bytes(after)
 (HERE/'UNIT9_AGREEMENT_TRANSFER_CORRECTION.json').write_text(json.dumps(dict(baselineCommit=BASELINE,beforeSha256=hashlib.sha256(raw).hexdigest(),afterSha256=hashlib.sha256(after).hexdigest(),changes=changes,addedProvenanceIds=added,nativeSelections=native,contextIdentities={rid:{'sourceHash':sha(original),'applications':[a],'objectiveId':OBJ}},bibliography=basis,nativePinCodeSha256=sha(code),frozenPublicSources={str(f.relative_to(ROOT)):sha(source)},semanticAcceptance=False,productiveAgreementClaim=False,allNumeralCasesClaim=False),ensure_ascii=False,indent=2)+'\n');print('Added',len(added),'records;',len(native),'native selections; replaced one independent masculine cue and revised six teaching cards')
if __name__=='__main__':main()
