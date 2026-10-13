#!/usr/bin/env python3
"""Replace guided-sentence replay with a distinct source-supplied reflexive object."""
import json,hashlib,re
from copy import deepcopy
from pathlib import Path
HERE=Path(__file__).resolve().parent;ROOT=HERE.parents[1];PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json';PIN_FILE=ROOT/'app/src/main/java/dev/russiancompanion/v2/curriculum/Unit21ObjectPins.kt'
BASELINE='6434cd12cdb22d0bbf6f7f848191d46b46501fca';OBJ='obj-u21-object-reference';SID='src-enwiki-reflexive-pronoun-1374712982'
def sha(s):return hashlib.sha256(s.encode()).hexdigest()
def visible(s):return re.sub(r'\[\[([^\[\]]+)\]\]',lambda m:m[1].split('|',1)[-1],s).replace("'''",'').replace("''",'')
def main():
 raw=PACK.read_bytes();p=json.loads(raw)
 if next(i for i in p['items'] if i['id']=='u21-i-object-reference-2')['promptLetter']=='Он поранил себя':print('Already repaired; evidence unchanged');return
 f=ROOT/'audit/independent-gpt61-2026-10-12/ia016-unit21-research-verification/public-candidates-2026-10-13/enwiki-reflexive-1374712982.wikitext.txt';source=f.read_text();span=source[source.index('=== Russian ==='):source.index('===Serbo-Croatian===')].rstrip();assert '{{lang|ru|Он поранил себя}}' in span and '{{lang|ru|Он поранил его}}' in span
 history=ROOT/'audit/independent-gpt61-2026-10-12/ia016-unit21-research-verification/public-candidates-2026-10-13/enwiki-reflexive-586336289.wikitext.txt';old=history.read_text();assert 'Он поранил себя' in old and 'Он поранил его' in old and 'Морфология простым языком' not in old
 basis=('English Wikipedia Reflexive pronoun1374712982, complete Russian section:exact direct-object sentences Он поранил себя / Он поранил его and supplied English meanings. Retained pre2014 revision586336289 (2013-12-16) independently documents both exact native examples and meanings before the current section cites a book dated2014; that later citation is not the source basis for this bounded intake, and no book prose is adopted. Exact API SHA1/size/SHA256/raw responses/history are retained. '
 'First-party Wikipedia text selection/arrangement is CC BY-SA4.0 via actual retained Wikipedia:Copyrights1369466282, stable oldid/history/contributor attribution. No blanket hosting-license or qualified rights approval; historical chronology is not a legal clearance. Only literal template language values are selected, with no native generation, stress addition, punctuation repair, private Penguin prose or media. '
 'Preserve the correct guided Я люблю́ себя́ / I love myself and independent Я люблю́ (свою́) жену́ / I love my wife whole-object unchanged, all original examples and earlier native/source records. The changed independent check recognizes a different subject/action with a reflexive direct object; the contrasting supplied его sentence denotes someone else. '
 'Only these supplied object readings are taught; no universal all-case antecedent algorithm, emphasis rule, automatic/accidental -ся interpretation, all possessive style restrictions or whole-article grammar adoption. The source wording about emphasis and accidental reflexive verbs is explicitly excluded as unreliable beyond scope. All three teaching cards precede practice and full remediation is retained. Independently authored English2026-10-13 within content-audit-2026-10-08; qualified source/rights/linguistic acceptance pending.')
 attribution='English Wikipedia Reflexive pronoun contributors, CC BY-SA 4.0, via Wikipedia:Copyrights oldid 1369466282. Page history for oldid 1374712982 identifies contributors. Exact native language values selected without stress/punctuation changes; qualified source/rights review pending.'
 src=dict(id=SID,title='Reflexive pronoun — bounded Russian direct-object examples',license='CC BY-SA 4.0',attribution=attribution,modification='Exact supplied native direct-object sentences selected; English context independently explained. No whole-article rule adoption, book prose or media. Historical pre2014 evidence retained; formal source/rights review pending.');rights=dict(id='rights-enwiki-reflexive-pronoun-1374712982',sourceId=SID,license='CC BY-SA 4.0',permitsAppUse=True);assert all(s['id']!=SID for s in p['sources']);p['sources'].append(src);p['rights'].append(rights)
 template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-u5-behavior-teach-1-body');primary=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ex-u21-object-reference-0-ru');added=[];native=[];changes=[]
 def record(value,name,original=None,apps=None,rid=None):
  original=original or basis+'\n'+value;r=deepcopy(template);r.update(itemId='prov-ca-u21-object-transfer-'+name,unitNumber=21,objectiveId=OBJ,recordType='label',sourceRecordId=rid or 'ca-coverage:'+OBJ+':transfer:'+name,originalSourceText=original,shippedText=value,sourceHash=sha(original),contentHash=sha(value));r['synthesis'].update(bibliography=basis,authorshipDeclaration='Independently authored English from frozen full source context; no native generation or private prose.',objectiveOrClaimBinding=OBJ+':transfer:'+name,applications=apps or []);p['provenanceRecords'].append(r);added.append(r['itemId']);return r['itemId']
 for suffix,text in [('self','Он поранил себя'),('other','Он поранил его'),('reflexive','себя'),('personal','его')]:
  assert text in span;r=deepcopy(primary);r.pop('originalSourceTextRef',None);r.update(itemId='prov-u21-object-transfer-native-'+suffix,recordType='example' if suffix in ['self','other'] else 'meaning',sourceId=SID,sourceUrl='https://en.wikipedia.org/w/index.php?title=Reflexive_pronoun&oldid=1374712982',sourceRevision='1374712982',sourceRecordId='reflexive-independent-direct-object:'+span[:77],originalSourceText=span,shippedText=text,sourceHash=sha(span),contentHash=sha(text),attribution=attribution);p['provenanceRecords'].append(r);native.append(deepcopy(r));added.append(r['itemId'])
 item=next(i for i in p['items'] if i['id']=='u21-i-object-reference-2');before=deepcopy(item)
 feedback='Он поранил себя means He has wounded himself. The direct object себя refers back to the subject:the person wounded is the same person who wounded him. The contrasting source sentence Он поранил его has a non-reflexive pronoun referring to someone else. Read the whole supplied sentence; this does not test every reflexive case or the separate reflexive-verb interpretation.'
 reasons=['','He has wounded him (someone else). describes the separate supplied Он поранил его sentence; it does not express the reflexive direct object себя in Он поранил себя.']
 a=dict(cue='Он поранил себя',answer='He has wounded himself.',distractors=['He has wounded him (someone else).']);rid='ca-rule-context:u21-i-object-reference-2'
 def field(s):return str(len(s.encode()))+':'+s
 canonical='tuple-v1:1;'+field(a['cue'])+field(a['answer'])+'1;'+field(a['distractors'][0]);original=basis+'\nComplete raw context:\n'+span+'\nContext explanation:\n'+feedback+'\nTuple-v1:'+canonical;ids=[record(v,'u21-i-object-reference-2-'+str(n),original,[a],rid) for n,v in enumerate([a['cue'],a['answer']]+a['distractors'])]
 item.update(sourceId=template['sourceId'],promptLetter=a['cue'],answer=a['answer'],options=[a['answer']]+a['distractors'],feedback=feedback,distractorRationales=reasons);item['assessment'].update(contrastSetId=item['id'],evidenceMode='STATED_APPLICATION',relationKind='STATED_APPLICATION',responseKind='ENGLISH_CLASSIFICATION',relationParticipantIds=ids,answerSourceIds=[ids[1]],distractorSourceIds=['',ids[2]],distractorDifference=reasons,contrastMembers=item['options'],whyAnswerDemonstratesObjective=feedback);changes.append(dict(before=before,after=deepcopy(item)));record(item['prompt'],'item-prompt');record(feedback,'item-feedback');record(reasons[1],'item-rationale')
 unit=p['units'][20];obj=next(o for o in unit['objectives'] if o['id']==OBJ);before=deepcopy(obj);obj['instructionStepIds'].append('u21-st-teach-object-reference-3');changes.append(dict(before=before,after=deepcopy(obj)))
 session=next(s for s in unit['sessions'] if s['id']=='u21-s-object-reference');before=deepcopy(session);step=next(s for s in session['steps'] if s['id']=='u21-st-teach-object-reference-2');step['body']=step['body'].replace('These checks recognize two retained sentences;','These checks recognize the supplied love and wounded-object sentences;');record(step['body'],'teach-2-body')
 title='Recognize the same-person object in a different sentence';body='Он поранил себя — He has wounded himself. Here the subject denotes the man and the direct object себя refers back to that same person. The verb and subject differ from guided Я люблю́ себя́. Compare the complete supplied Он поранил его — He has wounded him (someone else). In this contrast the other person is the object; the person wounded is different from the subject. In both wounded sentences the object is a person, not a possessed wife. In the retained Я люблю́ (свою́) жену́ example, a wife is the object even when the possessive refers to the subject as owner. Keep object identity separate from ownership. These are supplied direct-object readings, not productive grammar, every pronoun case, emphasis or a rule that the reflexive-verb suffix always means accidental action.'
 session['steps'].append(dict(id='u21-st-teach-object-reference-3',kind='TEACH',title=title,body=body,referenceIds=['ref-u2125-attribution']));record(title,'teach-3-title');record(body,'teach-3-body');teaching=[s for s in session['steps'] if s['kind']=='TEACH'];session['steps']=teaching+[s for s in session['steps'] if s['kind']!='TEACH'];rem=next(s for s in session['steps'] if s['kind']=='REMEDIATE');rem['body']='\n\n'.join(s['body'] for s in teaching);rem['exampleIds']=list(dict.fromkeys(e for s in teaching for e in s.get('exampleIds',[])));record(rem['body'],'remediation-body');changes.append(dict(before=before,after=deepcopy(session)))
 q=lambda s:json.dumps(s,ensure_ascii=False);rows=['        Native('+', '.join(q(r[k]) for k in ['itemId','sourceUrl','sourceRecordId','sourceHash','shippedText'])+'),' for r in native]
 code='''package dev.russiancompanion.v2.curriculum
import java.security.MessageDigest
/** Distinct supplied direct-object reflexive recognition; qualified review remains pending. */
internal object Unit21ObjectPins {
    const val SOURCE="src-enwiki-reflexive-pronoun-1374712982"
    private data class Native(val id:String,val url:String,val record:String,val hash:String,val text:String)
    private val native=listOf(
ROWS
    )
    private fun sha(s:String)=MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8)).joinToString(""){"%02x".format(it.toInt() and 255)}
    fun admittedNative(r:V2ProvenanceRecord):Boolean {
        val pin=native.singleOrNull{it.id==r.itemId}?:return false
        return r.unitNumber==21 && r.objectiveId=="obj-u21-object-reference" && r.sourceId==SOURCE && r.sourceRevision=="1374712982" &&
            r.sourceUrl==pin.url && r.sourceRecordId==pin.record && r.sourceHash==pin.hash && sha(r.originalSourceText)==pin.hash &&
            r.shippedText==pin.text && r.contentHash==sha(pin.text) && pin.text in r.originalSourceText &&
            r.transformation=="selection" && r.licenseId=="CC BY-SA 4.0" && r.attribution.contains("Wikipedia:Copyrights oldid 1369466282") && r.audioAssetId==null
    }
    fun matches(pack:V2CurriculumPack):Boolean {
        val obj=pack.unit(21)?.objectives?.singleOrNull{it.id=="obj-u21-object-reference"}?:return false
        if(obj.kind!="RECOGNITION" || !obj.gating || obj.checkItemIds!=listOf("u21-i-object-reference-1","u21-i-object-reference-2"))return false
        if(!native.all{pin->pack.provenanceRecords.singleOrNull{it.itemId==pin.id}?.let(::admittedNative)==true})return false
        if(pack.sources.none{it.id==SOURCE && it.license=="CC BY-SA 4.0"} || pack.rights.none{it.sourceId==SOURCE && it.license=="CC BY-SA 4.0" && it.permitsAppUse})return false
        val guided=obj.practiceStepIds.flatMap{pack.step(it)?.second?.itemIds.orEmpty()};if(guided!=listOf("u21-g-object-reference-0"))return false
        val item=pack.item("u21-i-object-reference-2")?:return false
        return item.assessment?.independent==true && item.assessment.testedFeature=="REFLEXIVE_MYSELF_SENTENCE" &&
            item.promptLetter!=pack.item(guided.single())?.promptLetter && CompleteRuleContextPins.itemMatches(pack,item) && V2Validator.structureValid(pack,item).isEmpty()
    }
}
'''.replace('ROWS','\n'.join(rows));PIN_FILE.write_text(code);after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode();PACK.write_bytes(after)
 (HERE/'UNIT21_OBJECT_TRANSFER_CORRECTION.json').write_text(json.dumps(dict(baselineCommit=BASELINE,beforeSha256=hashlib.sha256(raw).hexdigest(),afterSha256=hashlib.sha256(after).hexdigest(),changes=changes,addedProvenanceIds=added,nativeSelections=native,addedSources=[src],addedRights=[rights],contextIdentities={rid:dict(sourceHash=sha(original),applications=[a],objectiveId=OBJ)},bibliography=basis,nativePinCodeSha256=sha(code),frozenPublicSources={str(f.relative_to(ROOT)):sha(source),str(history.relative_to(ROOT)):sha(old)},semanticAcceptance=False,productiveReflexiveClaim=False,allCasesClaim=False),ensure_ascii=False,indent=2)+'\n');print('Added',len(added),'pending records;',len(native),'native selections;distinct reflexive object,three teaching cards')
if __name__=='__main__':main()
