#!/usr/bin/env python3
"""Distinct source-native guided timing; independent scored fields preserved."""
import sys,json,hashlib
from pathlib import Path
from copy import deepcopy
ROOT=Path(__file__).resolve().parents[2];sys.path.insert(0,str(ROOT/'catalog'))
from curriculum_storage import loads,dumps
PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json'
BASELINE='74f17e621a4b7ca2d9faf3228ef8f96c53c08c22'
sha=lambda s:hashlib.sha256(s.encode()).hexdigest()
def main():
 raw=PACK.read_bytes();p=loads(raw)
 if any(r['itemId'].startswith('prov-u28-timing-native-') for r in p['provenanceRecords']):return
 source=ROOT/'catalog/sources/units-26-30/wikisource/vechera-5336197.txt'
 paragraph=next(x for x in source.read_text().splitlines() if 'устав писать' in x)
 verbs=ROOT/'catalog/sources/units-3-5/openrussian/verbs.csv';lines=verbs.read_text().splitlines()
 literary=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ex-u28-readings-0-ru')
 lexical=next(r for r in p['provenanceRecords'] if r['sourceId']=='src-openrussian-50e210c4')
 english=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-u5-behavior-teach-1-body')
 basis=('Retained Wikisource Вечера у княгини Старобельской oldid5336197, complete opening paragraph of ЗНАКОМСТВО С ДРУГОМ ПРИНЦЕССЫ КОРЛЕОНЕ with explicit actor Павел Сергеич Вязовский. Exact native clause selections with unchanged source punctuation and NBSP; no stress or actor inserted. Existing public-domain author/work/rights basis retained, qualified rights review pending. Frozen OpenRussian verbs.csv commit50e210c4803237779cb562bc1abcea529066031c identifies читать as imperfective and устать as perfective. Existing CC BY-SA4.0 attribution/rights apply to selected dictionary rows. Retained Russian grammar1369617627 complete Adverbial participle section supplies earlier/simultaneous relation; absolute usually-present/only-past labels are not adopted as sentence tense. Independent English,2026-10-13. Guided literary context also occurs in later reading; not unseen evidence or productive mastery. Original independent relative-time/shared-subject scored fields remain. No private Penguin text; source/rights/semantic review pending.')
 added=[];native=[];changes=[];unit=p['units'][27]
 def coverage(text,obj,name):
  r=deepcopy(english);r.update(itemId='prov-ca-u28-timing-'+name,unitNumber=28,objectiveId=obj,sourceRecordId='ca-coverage:'+obj+':timing:'+name,originalSourceText=basis+'\n'+text,shippedText=text,sourceHash=sha(basis+'\n'+text),contentHash=sha(text))
  r['synthesis'].update(bibliography=basis,authorshipDeclaration='Independent English,2026-10-13; exact licensed native selections separately bound; no generated native text.',objectiveOrClaimBinding=obj+':timing:'+name,applications=[])
  p['provenanceRecords'].append(r);added.append(r['itemId'])
 def select(template,text,span,obj,name,record,url=None):
  assert text in span
  r=deepcopy(template);r.pop('originalSourceTextRef',None)
  r.update(itemId='prov-u28-timing-native-'+name,unitNumber=28,objectiveId=obj,sourceRecordId=record,originalSourceText=span,shippedText=text,sourceHash=sha(span),contentHash=sha(text))
  if url:r['sourceUrl']=url
  p['provenanceRecords'].append(r);added.append(r['itemId']);native.append(deepcopy(r));return r
 for kind,word,cue,answer in [('imperfective','читать','отдыхал читая','simultaneous with the main predicate'),('perfective','устать','устав писать ложился на\u00a0диван','anterior to a past main action')]:
  obj='obj-u28-'+kind+'-adverb';cap=next(c for c in p['sourceCapabilities'] if c['id']=='cap-'+obj);oldcap=deepcopy(cap)
  hosts=[r for r in p['provenanceRecords'] if r['sourceRecordId']==cap['sourceRecordId'] and r['sourceHash']==cap['sourceHash']];assert len(hosts)==23
  row=next(x for x in lines if x.startswith(word+'\t'));assert row.split('\t')[4]==kind
  for name,text in [('clause',cue),('actor','Павел Сергеич Вязовский')]:select(literary,text,paragraph,obj,kind+'-'+name,'vechera-timing-full-paragraph:'+sha(paragraph))
  for name,text in [('verb',word),('aspect',kind)]:select(lexical,text,row,obj,kind+'-'+name,'verbs.csv:'+word+'#timing-context','https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv')
  item=next(i for i in p['items'] if i['id']=='u28-g-'+kind+'-adverb-0');before=deepcopy(item)
  opts=[answer,'an absolute present label','a different subject from the main verb']
  meaning=('Reading accompanies resting; the same person performs both actions in this supplied episode.' if kind=='imperfective' else 'Becoming tired of writing precedes lying down; the same person performs both actions in this supplied episode.')
  feedback='In '+cue+', '+answer+' is the matching relative-time description. '+meaning+' The main predicate is past; the verbal adverb does not independently label absolute sentence time.'
  reasons=['','an absolute present label mistakes relative timing for an independent tense.','a different subject from the main verb is not the shared-actor reading supplied by this paragraph.']
  item.update(prompt='Which English description identifies the relative timing in this retained clause?',promptLetter=cue,answer=answer,options=opts,feedback=feedback,distractorRationales=reasons)
  item['assessment'].update(contrastMembers=opts,distractorDifference=reasons,whyAnswerDemonstratesObjective=feedback)
  app=dict(cue=cue,answer=answer,distractors=opts[1:]);apps=deepcopy(hosts[0]['synthesis']['applications'])+[app];assert len(apps)==3
  session=next(s for s in unit['sessions'] if s['objectiveIds']==[obj]);oldsession=deepcopy(session)
  body=('The retained paragraph introduces Павел Сергеич Вязовский as the actor. '+cue+' — '+meaning+' The source marks '+word+' as '+kind+'. '+answer+' describes timing relative to the main predicate in this clause. The actor is supplied by the paragraph, not inserted into a new Russian sentence. This is an exact clause selection with source punctuation and spacing preserved. Compare the independent letter sentence already taught: it keeps its original relative-time and shared-subject reading. The same literary episode appears later in reading practice; this is not unseen evidence, a rule that fixes absolute tense, or unrestricted formation/production mastery.')
  step=dict(id='u28-st-teach-'+kind+'-timing-context',kind='TEACH',title='Read the retained literary timing context',body=body,referenceIds=['ref-u2630-attribution'])
  teaching=[s for s in session['steps'] if s['kind']=='TEACH']+[step];session['steps']=teaching+[s for s in session['steps'] if s['kind']!='TEACH']
  objective=next(o for o in unit['objectives'] if o['id']==obj);oldobj=deepcopy(objective);objective['instructionStepIds'].append(step['id'])
  rem=next(s for s in session['steps'] if s['kind']=='REMEDIATE');rem['body']='\n\n'.join(s['body'] for s in teaching);rem['exampleIds']=list(dict.fromkeys(e for s in teaching for e in s.get('exampleIds',[])))
  for name,text in [('teach-title',step['title']),('teach-body',body),('rem-body',rem['body']),('prompt',item['prompt']),('feedback',feedback)]+[(str(n)+'-reason',x) for n,x in enumerate(reasons) if x]:coverage(text,obj,kind+'-'+name)
  cap['exactSpan'] += '\n'+basis+'\n'+paragraph+'\n'+row+'\n'+'\n'.join([item['prompt'],feedback,*reasons,body,rem['body']])+'\n'+json.dumps(apps,ensure_ascii=False)
  cap['sourceHash']=sha(cap['exactSpan']);mapping={}
  for h in hosts:
   r=deepcopy(h);r.pop('originalSourceTextRef',None);r.update(itemId='prov-u28-timing-rebind-'+h['itemId'],originalSourceText=cap['exactSpan'],sourceHash=cap['sourceHash']);r['synthesis'].update(applications=apps,bibliography=basis)
   p['provenanceRecords'].append(r);added.append(r['itemId']);mapping[h['itemId']]=r['itemId']
  for i in p['items']:
   if i['objectiveId']==obj and i['assessment']['independent']:
    was=deepcopy(i)
    for k in ['relationParticipantIds','answerSourceIds','distractorSourceIds']:i['assessment'][k]=[mapping.get(x,x) for x in i['assessment'][k]]
    changes.append(dict(before=was,after=deepcopy(i)))
  ids=[]
  for n,text in enumerate([cue,answer,*opts[1:]]):
   r=deepcopy(hosts[0]);r.pop('originalSourceTextRef',None);r.update(itemId='prov-u28-timing-guided-'+kind+'-'+str(n),originalSourceText=cap['exactSpan'],sourceHash=cap['sourceHash'],shippedText=text,contentHash=sha(text));r['synthesis'].update(applications=apps,bibliography=basis)
   p['provenanceRecords'].append(r);added.append(r['itemId']);ids.append(r['itemId'])
  item['assessment'].update(relationParticipantIds=ids,answerSourceIds=[ids[1]],distractorSourceIds=['',*ids[2:]])
  changes += [dict(before=before,after=deepcopy(item)),dict(before=oldsession,after=deepcopy(session)),dict(before=oldobj,after=deepcopy(objective)),dict(before=oldcap,after=deepcopy(cap))]
  pins=ROOT/'app/src/main/java/dev/russiancompanion/v2/curriculum/ContentAuditCapabilityPins.kt';pins.write_text(pins.read_text().replace(oldcap['sourceHash'],cap['sourceHash']))
 q=lambda x:json.dumps(x,ensure_ascii=False)
 rows=['        Native('+', '.join(q(r[k]) for k in ['itemId','objectiveId','sourceId','sourceRevision','sourceUrl','sourceRecordId','sourceHash','shippedText','licenseId'])+'),' for r in native]
 code='''package dev.russiancompanion.v2.curriculum
import java.security.MessageDigest
/** Bounded guided literary timing; independent past/future and shared-subject evidence retained. */
internal object Unit28TimingPins {
    private data class Native(val id:String,val obj:String,val source:String,val revision:String,val url:String,val record:String,val hash:String,val text:String,val license:String)
    private val native=listOf(
ROWS
    )
    private fun sha(s:String)=MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8)).joinToString(""){"%02x".format(it.toInt() and 255)}
    fun matches(pack:V2CurriculumPack):Boolean {
        if(!native.all{pin->pack.provenanceRecords.singleOrNull{it.itemId==pin.id}?.let{r->r.unitNumber==28 && r.objectiveId==pin.obj && r.sourceId==pin.source && r.sourceRevision==pin.revision && r.sourceUrl==pin.url && r.sourceRecordId==pin.record && r.sourceHash==pin.hash && sha(r.originalSourceText)==pin.hash && r.shippedText==pin.text && r.contentHash==sha(pin.text) && pin.text in r.originalSourceText && r.transformation=="selection" && r.licenseId==pin.license}==true})return false
        for(kind in listOf("imperfective","perfective")) {
            val id="obj-u28-$kind-adverb";val obj=pack.unit(28)?.objectives?.singleOrNull{it.id==id}?:return false
            if(obj.kind!="RECOGNITION" || !obj.gating || obj.checkItemIds!=listOf("u28-i-$kind-adverb-1","u28-i-$kind-adverb-2"))return false
            val guided=obj.practiceStepIds.flatMap{pack.step(it)?.second?.itemIds.orEmpty()}
            if(guided!=listOf("u28-g-$kind-adverb-0"))return false
            val g=pack.item(guided.single())?:return false
            val imp=kind=="imperfective"
            if(g.promptLetter!=(if(imp)"отдыхал читая" else "устав писать ложился на диван") || g.answer!=(if(imp)"simultaneous with the main predicate" else "anterior to a past main action") || g.options!=listOf(g.answer,"an absolute present label","a different subject from the main verb") || g.assessment?.testedFeature!=(if(imp)"IMPERFECTIVE_ADVERB_SIMULTANEOUS" else "PERFECTIVE_ADVERB_ANTERIOR") || g.assessment.independent!=false || V2Validator.structureValid(pack,g).isNotEmpty())return false
            val cue=if(imp)"читая письмо, он улыбался" else "написав письмо, она отправит его завтра"
            val answers=if(imp)listOf("simultaneous with the main predicate","the reader and the person smiling are the same person") else listOf("anterior to a future main action","the writer and sender are the same person")
            if(!obj.checkItemIds.mapIndexed{n,check->pack.item(check)?.let{i->i.promptLetter==cue && i.answer==answers[n] && i.assessment?.independent==true && V2Validator.structureValid(pack,i).isEmpty()}==true}.all{it})return false
        }
        return true
    }
}
'''.replace('ROWS','\n'.join(rows))
 (ROOT/'app/src/main/java/dev/russiancompanion/v2/curriculum/Unit28TimingPins.kt').write_text(code)
 after=dumps(p).encode();PACK.write_bytes(after)
 (ROOT/'audit/content-corrections/UNIT28_TIMING_TRANSFER_CORRECTION.json').write_text(json.dumps(dict(baselineCommit=BASELINE,beforeSha256=hashlib.sha256(raw).hexdigest(),afterSha256=hashlib.sha256(after).hexdigest(),bibliography=basis,changes=changes,addedProvenanceIds=added,nativeSelections=native,nativePinCodeSha256=sha(code),semanticAcceptance=False,unseenContextClaim=False,productiveFormationClaim=False),ensure_ascii=False,indent=2)+'\n')
 print('Added',len(added),'pending records;',len(native),'native/lexical selections; preserved original independent scored fields')
if __name__=='__main__':main()
