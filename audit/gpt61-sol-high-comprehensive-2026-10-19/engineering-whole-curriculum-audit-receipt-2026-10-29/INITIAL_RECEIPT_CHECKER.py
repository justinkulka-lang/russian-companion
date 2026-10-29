from pathlib import Path
from collections import Counter
import subprocess,json,hashlib,csv,io,re,ast
R=Path('/workspace/russian-companion');W=Path('/workspace/artifacts/whole-curriculum-audit-receipt-2026-10-29');W.mkdir();S='25270a526a698a177cefdb31c7b7a0a9618d9579';TARGET='5a3411a76b88753617ea18b3e1a54f44f6923b9f';REL='audit/gpt61-sol-high-comprehensive-2026-10-19/whole-curriculum-audit-results/2026-10-29-claude-code';git=lambda *a:subprocess.check_output(['git',*a],cwd=R);sha=lambda b:hashlib.sha256(b).hexdigest();save=lambda n,v:(W/n).write_text(json.dumps(v,indent=2)+'\n')
assert not git('status','--porcelain=v1','-z','--untracked-files=all');before=git('rev-parse','HEAD').decode().strip();paths=git('diff-tree','--no-commit-id','--name-only','-r',S).decode().splitlines();assert len(paths)==8 and all(n.startswith(REL+'/')for n in paths)
files={}
for n in paths:
 data=git('show',S+':'+n);name=Path(n).name;assert not name.lower().endswith('.pdf');(W/name).write_bytes(data);files[name]={'bytes':len(data),'sha256':sha(data),'gitBlob':git('rev-parse',S+':'+n).decode().strip()}
checks={}
for line in(W/'SHA256SUMS.txt').read_text().splitlines():
 h,n=line.split(None,1);n=n.strip();assert n in files and files[n]['sha256']==h;checks[n]=h
assert len(checks)==7 and set(checks)==set(files)-{'SHA256SUMS.txt'}
scope=json.loads((W/'AUDIT_SCOPE_AND_EXECUTION.json').read_text());ident=scope['identities'];assert ident['auditedSourceCommit']==TARGET and ident['auditedSourceTree']==git('rev-parse',TARGET+'^{tree}').decode().strip()
for n,h in ident['inputTrees'].items():assert git('rev-parse',TARGET+':'+n).decode().strip()==h==git('rev-parse','HEAD:'+n).decode().strip()
raw=git('show',TARGET+':app/src/main/assets/content/v2-curriculum.json');pack=json.loads(raw);assert len(raw)==ident['curriculum']['bytes']and sha(raw)==ident['curriculum']['sha256'];assert len(pack['items'])==626 and len(pack['sourceCapabilities'])==58
readcsv=lambda n:list(csv.DictReader(io.StringIO((W/n).read_text())))
findings=readcsv('FINDINGS.csv');coverage=readcsv('ITEM_AND_CAPABILITY_COVERAGE.csv');units=readcsv('UNIT_COVERAGE.csv');batches=readcsv('BATCHED_CORRECTION_WORK_ORDER.csv');print('headers', {n:list(v[0])for n,v in [('findings',findings),('coverage',coverage),('units',units),('batches',batches)]})
assert len(findings)==59 and len(coverage)==684 and len(units)==30 and len(batches)==15
fid='finding_id';assert len({x[fid]for x in findings})==59;assert {f'CC{i:03d}'for i in range(1,49)}|{f'WC{i:03d}'for i in range(1,12)}=={x[fid]for x in findings}
sevs=Counter(x['severity']for x in findings);assert dict(sevs)=={'HIGH':18,'MEDIUM':28,'LOW':13}
prior=list(csv.DictReader(io.StringIO(git('show',TARGET+':audit/gpt61-sol-high-comprehensive-2026-10-19/final-independent-recheck-results/2026-10-22-claude-code/FINDINGS.csv').decode())));priorMap={x['finding_id']:x for x in prior};assert len(priorMap)==48
for x in findings:
 if x[fid]in priorMap:assert x['severity']==priorMap[x[fid]]['severity']
for x in findings:assert x['status']=='OPEN',x
# Coverage identities verified independently from pinned input; row verdicts remain auditor judgments.
ids={x.get('entity_id',x.get('id',x.get('item_id')))for x in coverage};expected={x['id']for x in pack['items']}|{x['id']for x in pack['sourceCapabilities']};assert ids==expected and len(ids)==684
unitkey='unit'if 'unit'in units[0]else'unit_number';assert {int(x[unitkey])for x in units}==set(range(1,31))
code=(W/'mechanical_analysis.py').read_text();tree=ast.parse(code)
for n in ast.walk(tree):
 if isinstance(n,ast.Import):assert all(x.name in {'json','re','sys','collections','unicodedata','hashlib'}for x in n.names)
 assert not isinstance(n,ast.ImportFrom)
(W/'PINNED_CURRICULUM.json').write_bytes(raw)
cmd=['python3','-I',str(W/'mechanical_analysis.py'),str(W/'PINNED_CURRICULUM.json'),str(W/'REPRODUCED_MECHANICAL_ANALYSIS.json')]
with(W/'MECHANICAL_REPRODUCTION.log').open('w')as f:p=subprocess.run(cmd,cwd=W,stdout=f,stderr=subprocess.STDOUT)
assert p.returncode==0
m=json.loads((W/'REPRODUCED_MECHANICAL_ANALYSIS.json').read_text());mr=m['rows'];req=[x for x in mr if x['role']=='REQUIRED_CHECK'];counts={'items':len(mr),'requiredChecks':len(req),'twoOptionRequired':sum(x['n_options']==2 for x in req),'cueAsSoleDistractorRequired':sum(x['cue_dup_sole']for x in req),'cueAsAnyDistractorRequired':sum(x['cue_dup_any']for x in req),'normalizedDuplicateGroups':len({x['dup_group']for x in mr if x['dup_group']}),'constantKeyObjectives':len(m['constant_key_objectives'])};assert counts==dict(items=626,requiredChecks=429,twoOptionRequired=208,cueAsSoleDistractorRequired=39,cueAsAnyDistractorRequired=42,normalizedDuplicateGroups=31,constantKeyObjectives=9),counts
save('VERIFIED_RECEIPT.json',{'returnCommit':S,'returnParent':git('rev-parse',S+'^').decode().strip(),'localBeforeImport':before,'auditedSource':TARGET,'artifactPaths':REL,'files':files,'allSevenManifestEntriesVerified':True,'coverageRows':684,'all626ItemAnd58CapabilityIdsExact':True,'units':30,'findings':59,'priorFindingsPreserved':48,'newFindings':11,'severityCounts':dict(sevs),'allFindingsOpen':True,'batches':15,'mechanicalCommand':cmd,'mechanicalExitCode':0,'reproducedCounts':counts,'mechanicalCountsAreHeuristicFlagsNotQualifiedJudgments':True,'auditorExecutedTests':False,'broadRunIsExistingEngineeringEvidence':{'source':TARGET,'reported':1508,'passed':1507,'failed':0,'skipped':1},'pdfIdentityMatchesExpectedMetadataOnlyNotRefetchedByReceipt':True,'qualifiedReview':'OUTSTANDING','verdict':'INCOMPLETE','physicalAcceptance':'OUTSTANDING_LAST','appChangesByReceipt':False})
print(json.dumps({'verified':True,'returnCommit':S,'counts':counts,'findings':59,'batches':15}))
