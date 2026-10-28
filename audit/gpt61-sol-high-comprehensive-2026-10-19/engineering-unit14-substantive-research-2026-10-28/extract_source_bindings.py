"""Pinned public-source field/fragment and authored-label integrity;not qualified acceptance."""
from pathlib import Path
from functools import lru_cache
import csv,hashlib,io,json,posixpath,re,subprocess,sys
BASE='2926452c190bac07b1363788ae555322f9b22eae'
D=Path(__file__).resolve().parent;R=next(p for p in D.parents if (p/'.git').exists())
@lru_cache(None)
def blob(n):return subprocess.check_output(['git','show',BASE+':'+n],cwd=R)
sha=lambda b:hashlib.sha256(b).hexdigest()
enc=lambda x:(json.dumps(x,ensure_ascii=False,indent=2)+'\n').encode()
p=json.loads(blob('app/src/main/assets/content/v2-curriculum.json'));items={i['id']:i for i in p['items']};records={r['itemId']:r for r in p['provenanceRecords']}
resolve=lambda r:p['sourceTextPool'][r['originalSourceTextRef']] if 'originalSourceTextRef' in r else r['originalSourceText']
accent=lambda s:s.replace("'",'\u0301')
freezePath='catalog/sources/units-6-10/FREEZE.json';freeze=json.loads(blob(freezePath))['openrussian'];nounMeta=freeze['files']['nouns.csv']
nounPath=posixpath.normpath(posixpath.join('catalog/sources/units-6-10',nounMeta['path']));data=blob(nounPath)
assert sha(data)==nounMeta['sha256'] and subprocess.check_output(['git','hash-object','--stdin'],input=data,cwd=R).decode().strip()==nounMeta['gitBlob']
assert freeze['commit']=='50e210c4803237779cb562bc1abcea529066031c'
licensePath=posixpath.normpath(posixpath.join('catalog/sources/units-6-10',freeze['licenseFile']));assert sha(blob(licensePath))==freeze['licenseSha256']
table=list(csv.DictReader(io.StringIO(data.decode()),delimiter='\t'));fieldProof={}
for n in range(14):
    rid=f'prov-ex-or-u8-plural-{n}';r=records[rid];lemma,field=r['sourceRecordId'].removeprefix('nouns.csv:').split('#')
    matches=[x for x in table if x['bare']==lemma];assert len(matches)==1,(lemma,len(matches));row=matches[0]
    assert r['sourceRevision']==freeze['commit'] and resolve(r)==row[field] and sha(row[field].encode())==r['sourceHash']
    assert accent(row[field])==r['shippedText'] and sha(r['shippedText'].encode())==r['contentHash']
    fieldProof[rid]=dict(provenanceId=rid,lemma=lemma,field=field,sourceRecordId=r['sourceRecordId'],sourceHash=r['sourceHash'],contentHash=r['contentHash'],shippedText=r['shippedText'])
selectedFields=set();plural=[]
for n in range(1,7):
    i=items[f'u8-i-plural-{n}'];a=i['assessment'];participants=[records[k] for k in a['relationParticipantIds']]
    assert a['relationKind']=='SAME_LEMMA_DIFFERENT_FIELD' and a['testedFeature']=='PLURAL_FORMATION'
    assert [r['shippedText'] for r in participants]==[i['promptLetter'],*i['options']]
    assert records[a['answerSourceIds'][0]]['shippedText']==i['answer']
    assert all(r['itemId'] in fieldProof for r in participants)
    selectedFields.update(a['relationParticipantIds']);plural.append(dict(item=i['id'],cue=i['promptLetter'],options=i['options'],answer=i['answer'],sourceFieldIds=a['relationParticipantIds'],rawKeyIndex=i['options'].index(i['answer'])))
assert len(selectedFields)==12

def frozen_page(base,revision):
    fp=base+'/FREEZE.json';f=json.loads(blob(fp));page,=[x for x in f['wikibooks']['pages'] if str(x['oldid'])==revision]
    path=base+'/'+page['file'];raw=blob(path);assert sha(raw)==page['sha256'] and len(raw)==page['size']
    rp=base+'/'+page['revisionFile'];r=json.loads(blob(rp));assert r['revid']==int(revision) and r['sha1']==page['mediawikiSha1']
    return dict(freezePath=fp,freezeSha256=sha(blob(fp)),path=path,sha256=sha(raw),gitBlob=subprocess.check_output(['git','rev-parse',BASE+':'+path],cwd=R).decode().strip(),revision=revision,revisionPath=rp,revisionSha256=sha(blob(rp)),recordedMediaWikiSha1=page['mediawikiSha1']),raw.decode()
genitive,gtext=frozen_page('catalog/sources/units-6-10','4567338')
future,ftext=frozen_page('catalog/sources/units-11-15','3353822')
def unfold(s):
    s=s.replace("'''",'').replace("''",'')
    return re.sub(r'\[\[([^\]]+)\]\]',lambda m:m.group(1).split('|')[-1],s)
classifications=[]
for iid,sourceText,revision in [(f'u10-i-have-{n}',gtext,'4567338') for n in range(1,5)]+[('u13-i-report-frame-time',ftext,'3353822')]:
    i=items[iid];a=i['assessment'];participants=[records[k] for k in a['relationParticipantIds']]
    assert sorted(r['shippedText'] for r in participants)==sorted([i['promptLetter'],*i['options']])
    for n,option in enumerate(i['options']):
        if option!=i['answer']:
            ref=a['distractorSourceIds'][n];assert ref and records[ref]['shippedText']==option
    labelPins=[]
    for r in participants:
        context=resolve(r);assert sha(context.encode())==r['sourceHash'] and sha(r['shippedText'].encode())==r['contentHash']
        assert r['sourceId']=='src-content-audit-research'
        labelPins.append(dict(provenanceId=r['itemId'],recordType=r['recordType'],sourceId=r['sourceId'],sourceRevision=r['sourceRevision'],sourceRecordId=r['sourceRecordId'],sourceHash=r['sourceHash'],contentHash=r['contentHash'],origin='EXISTING_AUTHORED_RESEARCH_SYNTHESIS_NOT_LITERAL_WIKI_QUOTE'))
    assert records[a['answerSourceIds'][0]]['shippedText']==i['answer']
    aliases=[e for e in p['examples'] if e.get('display')==i['promptLetter']];sourcePins=[]
    for e in aliases:
        rid='prov-'+e['id']+'-ru'
        if rid not in records:continue
        r=records[rid]
        if r['sourceId']!='src-wikibooks-russian-cc-by-sa':continue
        text=resolve(r);assert r['sourceRevision']==revision and sha(text.encode())==r['sourceHash'] and text in sourceText
        assert r['shippedText']==i['promptLetter'] and r['shippedText'] in unfold(text)
        assert sha(r['shippedText'].encode())==r['contentHash']
        sourcePins.append(dict(exampleId=e['id'],provenanceId=rid,sourceRevision=revision,sourceHash=r['sourceHash'],contentHash=r['contentHash'],frozenFragmentByteOffset=sourceText.encode().find(text.encode())))
    assert sourcePins,iid
    classifications.append(dict(item=iid,cue=i['promptLetter'],answer=i['answer'],testedFeature=a['testedFeature'],responseKind=a.get('responseKind'),rawKeyIndex=i['options'].index(i['answer']),authoredLabelPins=labelPins,retainedNativeSourcePins=sourcePins,limit='Native cue is source-bound;English classification/feedback is existing authored synthesis,not a literal source quote or newly approved interpretation.'))
result=dict(verified=True,sourceCommit=BASE,dictionary=dict(freezePath=freezePath,freezeSha256=sha(blob(freezePath)),sourceCommit=freeze['commit'],path=nounPath,sha256=sha(data),gitBlob=nounMeta['gitBlob'],licensePath=licensePath,licenseSha256=freeze['licenseSha256'],licenseIdentityIsNotRightsApproval=True),pluralCandidates=plural,selectedPluralFields=len(selectedFields),completeRepairDictionaryFields=list(fieldProof.values()),genitiveSource=genitive,reportedFutureSource=future,classificationItems=classifications,scope='Frozen local field/fragment/hash/link and authored-label integrity only;not live-source,linguistic,rights,mapping or owner acceptance.',privatePdf='NOT_READ_OR_PACKAGED',newJUnitMethodsExecuted=0)
target=D/'SELECTED_SOURCE_BINDINGS.json';assert sys.argv[1:] in [[],['--check']]
if sys.argv[1:]==['--check']:assert target.read_bytes()==enc(result)
else:assert not target.exists();target.write_bytes(enc(result))
print(json.dumps(dict(verified=True,pluralItems=6,selectedNounFields=12,fullRepairNounFields=14,existingClassifications=5,existingAuthoredLabelsDistinguishedFromNativeSource=True,newJUnitMethodsExecuted=0)))
