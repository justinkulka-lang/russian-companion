"""Static, read-only source inventory; does not run app, tests or repository scripts."""
from pathlib import Path
import json,csv,re,hashlib,subprocess,collections,copy

R=Path('/workspace/russian-companion');W=Path(__file__).parent
TARGET='52cb624978fa99be9b8c2162a51f8e09c13d2c01';OLD='8606c4d74185f594d93941bef6858b2af28e0b75'
ASSET='app/src/main/assets/content/v2-curriculum.json'
sha=lambda b:hashlib.sha256(b).hexdigest()
git=lambda *a:subprocess.check_output(['git',*a],cwd=R)
raw=git('show',TARGET+':'+ASSET)
assert sha(raw)=='02202d37e0aa2c51182e4db22feff177124b6db2c270a67a93ef85e188916c19'
pack=json.loads(raw);old=json.loads(git('show',OLD+':'+ASSET))
patterns={
    'wiki_link':r'\[\[|\]\]',
    'wiki_template_or_table':r'\{\{|\}\}|\{\||\|\}',
    'wiki_emphasis':r"'{2,}",
    'embedded_markup':r'</?(?:ref|br|span|div|small|sup|sub|nowiki)\b|&(?:nbsp|amp|lt|gt|quot);',
}
compiled={k:re.compile(v)for k,v in patterns.items()}
def escape(s):return str(s).replace('~','~0').replace('/','~1')
def leaves(x,pointer='',owner='',unit=''):
    if isinstance(x,dict):
        owner=x.get('id',x.get('itemId',owner));unit=x.get('unitNumber',x.get('number',unit))
        for k,v in x.items():yield from leaves(v,pointer+'/'+escape(k),owner,unit)
    elif isinstance(x,list):
        for n,v in enumerate(x):yield from leaves(v,pointer+'/'+str(n),owner,unit)
    elif isinstance(x,str):yield pointer,owner,unit,x
def scan(document):
    result=[];total=0
    for pointer,owner,unit,value in leaves(document):
        total+=1;hits=[]
        for kind,pattern in compiled.items():
            hits += [{'kind':kind,'startCodepoint':m.start(),'endCodepoint':m.end(),'token':m.group()}for m in pattern.finditer(value)]
        if not hits:continue
        root=pointer.split('/')[1]
        if root=='units':
            assert pointer.endswith('/body')
            category='LEARNER_RENDERED_STATIC';renderer='V2Screens.kt:548;V2PracticeFlows.kt:196'
        elif root=='examples':
            assert pointer.endswith('/display')
            category='LEARNER_RENDERED_STATIC';renderer='V2Screens.kt:559;V2PracticeFlows.kt:200'
        elif root in ['provenanceRecords','sourceCapabilities','sourceTextPool']:
            category='PROVENANCE_ONLY';renderer='V2PackLoader.kt:27-68;no direct current V2 UI read of these matched evidence fields'
        else:category='UNVERIFIED';renderer='Requires field-to-renderer inspection'
        result.append({'json_pointer':pointer,'entity_id':owner,'unit':unit,'value_sha256':sha(value.encode()),'utf8_bytes':len(value.encode()),'codepoints':len(value),'matched_tokens':json.dumps(hits,ensure_ascii=False,separators=(',',':')),'classification':category,'rendering_evidence':renderer})
    return result,total
rows,total=scan(pack)
expanded=copy.deepcopy(pack);pool=expanded.pop('sourceTextPool');used=set();references=0
for key,text in pool.items():assert sha(text.encode())==key
for record in expanded['provenanceRecords']:
    if 'originalSourceTextRef'in record:
        key=record.pop('originalSourceTextRef');assert 'originalSourceText'not in record and record['sourceHash']==key
        record['originalSourceText']=pool[key];used.add(key);references+=1
assert used==set(pool)
expanded_rows,expanded_total=scan(expanded)
def visible_fields(document):
    out={}
    for unit in document['units']:
        for session in unit['sessions']:
            for step in session['steps']:
                for field in ['title','body','bookDirection','companionDirection','unavailableReason']:
                    value=step.get(field)
                    if isinstance(value,str)and any(p.search(value)for p in compiled.values()):out[(step['id'],field)]=value
    for example in document['examples']:
        for field in ['display','note']:
            value=example.get(field,'')
            if any(p.search(value)for p in compiled.values()):out[(example['id'],field)]=value
    return out
before,after=visible_fields(old),visible_fields(pack)
assert len(before)==len(after)==37 and before==after
for name,data in [('MATCHED_RAW_FIELDS.csv',rows),('MATCHED_EXPANDED_FIELDS.csv',expanded_rows)]:
    with(W/name).open('w',newline='')as f:
        writer=csv.DictWriter(f,fieldnames=list(data[0]),lineterminator='\n');writer.writeheader();writer.writerows(data)
summary={
    'targetCommit':TARGET,'originalAuditCommit':OLD,'assetPath':ASSET,'assetBytes':len(raw),'assetSha256':sha(raw),
    'patterns':patterns,'scanMode':'All decoded JSON string leaves; offsets are Unicode codepoints, not raw-file byte offsets',
    'singleApostrophesExcluded':True,'rawStringsScanned':total,'expandedStringsScanned':expanded_total,
    'rawMatchedFields':len(rows),'expandedMatchedFields':len(expanded_rows),
    'rawClassifications':dict(collections.Counter(r['classification']for r in rows)),
    'expandedClassifications':dict(collections.Counter(r['classification']for r in expanded_rows)),
    'rawMatchedRoots':dict(collections.Counter(r['json_pointer'].split('/')[1]for r in rows)),
    'sourcePoolEntriesShaVerified':len(pool),'sourcePoolReferencesExpanded':references,'allPoolEntriesUsed':True,
    'learnerFields':37,'learnerStepBodies':36,'learnerExampleDisplays':1,
    'learnerMarkupFieldsEqualOriginalAuditByEntityFieldAndExactString':True,
    'runtimeObservation':'None; source proves plain Text rendering without transformation at cited paths',
    'qualifiedLinguisticReview':'NOT_PERFORMED','sourceRightsReview':'NOT_PERFORMED',
    'noPrivatePdfAccess':True,'noRepositoryMutation':True,'noTestExecution':True,
    'finding':'CC023 remains OPEN; dangling prose/teaching quality requires separate review',
    'audit':'INCOMPLETE','physicalAcceptance':'OUTSTANDING_LAST',
}
(W/'INVENTORY_SUMMARY.json').write_text(json.dumps(summary,indent=2)+'\n')
(W/'CURRENT_FINDING_DISPOSITION.md').write_text('''# CC023 current source inventory

At source52cb6249,37learner-facing markup fields remain:36lesson/remediation step bodies and1example display. Every affected entity/field/value is exactly unchanged from original audit target8606c4d7. The earlier mapping's missing current count is now resolved. This is static rendering-path evidence, not new execution or independent finding closure.

V2SessionScreen sends step.body directly to Text and example.display directly to Text. V2LabScreen explanation uses the same untransformed fields. Source-pool expansion affects provenance only; it does not clean these learner fields. Existing validateLearnerFacingMarkup rejects MediaWiki syntax only inUnits16–30, leaving these earlier units outside that guard. Provenance/source-capability matches are recorded separately and are not learner defects merely because their frozen source contains markup.

MATCHED_RAW_FIELDS.csv and MATCHED_EXPANDED_FIELDS.csv contain exact pointers/value hashes/token offsets without bulk source prose. INVENTORY_SUMMARY.json defines patterns, totals and pool checks. Single apostrophes are not treated as wiki emphasis. The deterministic scan does not establish the quality of every teaching passage, attribution, linguistic accuracy or complete markup grammar.

Do not strip or rewrite frozen evidence. A bounded display correction may format only supported wiki emphasis and link labels at the lesson/repair/example boundary while preserving underlying governed content, keys, provenance and saved identities. That would address visible delimiters only; it would not repair dangling or weak instruction or satisfy the original raw-field rejection criterion. Alternatively a governed content correction requires authentic source/transformation records, history-boundary evidence and source-binding reconciliation. Choose after reviewing affected UI/validator/history constraints; no correction is applied here while119runtime owns the checkout.

All48independentfindingsOPEN/auditINCOMPLETE. PhysicalGalaxyacceptanceLAST. No app/test change, tests/builds/exporters/privatePDF access/APK/signing/merge/freeze/approval.
''')
manifest={p.name:{'bytes':p.stat().st_size,'sha256':sha(p.read_bytes())}for p in sorted(W.iterdir())if p.is_file()and p.name!='SHA256.json'}
(W/'SHA256.json').write_text(json.dumps(manifest,indent=2)+'\n')
print(json.dumps({k:summary[k]for k in ['rawStringsScanned','expandedStringsScanned','rawMatchedFields','expandedMatchedFields','rawClassifications','expandedClassifications','sourcePoolEntriesShaVerified','sourcePoolReferencesExpanded','learnerFields','learnerMarkupFieldsEqualOriginalAuditByEntityFieldAndExactString']},indent=2))
