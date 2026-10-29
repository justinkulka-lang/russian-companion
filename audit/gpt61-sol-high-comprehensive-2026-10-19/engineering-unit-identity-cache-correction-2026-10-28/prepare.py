from pathlib import Path
import subprocess,json,hashlib,zipfile,shutil,re,ast
R=Path('/workspace/russian-companion');B=R/'audit/gpt61-sol-high-comprehensive-2026-10-19';D=B/'engineering-unit-identity-cache-correction-2026-10-28';W=Path('/workspace/artifacts/unit-identity-cache-correction-2026-10-28');OLD=Path('/workspace/artifacts/unit-identity-cache-baseline-2026-10-28')
assert not D.exists()and not W.exists();D.mkdir();W.mkdir()
git=lambda *a:subprocess.check_output(['git',*a],cwd=R).decode().strip();sha=lambda b:hashlib.sha256(b).hexdigest();save=lambda p,v:p.write_text(json.dumps(v,indent=2)+'\n')
parent=git('rev-parse','HEAD');source='app/src/main/java/dev/russiancompanion/v2/curriculum/V2GovernedIdentity.kt';old=subprocess.check_output(['git','show',parent+':'+source],cwd=R);new=(R/source).read_bytes()
draft=json.loads(Path('/workspace/artifacts/codex-unit-identity-cache-baseline-2026-10-28/PROPOSED_SOURCE_IDENTITIES.json').read_text());assert sha(old)==draft['oldSha256']and sha(new)==draft['newSha256']
changed=git('diff','--name-only').splitlines();assert changed==[source]
tests={p.relative_to(R).as_posix():sha(p.read_bytes())for p in(R/'app/src/test').rglob('*')if p.is_file()};assert len(tests)==282
for p,h in tests.items():assert sha(subprocess.check_output(['git','show',parent+':'+p],cwd=R))==h,p
with zipfile.ZipFile(D/'ORIGINAL_PRODUCTION_SOURCE.zip','x',zipfile.ZIP_DEFLATED)as z:z.writestr(source,old)
subprocess.run(['git','add',source],cwd=R,check=True);tree=git('write-tree');paths=['app/src/main','app/src/test','catalog','docs','review','release'];trees={p:git('rev-parse',tree+':'+p)for p in paths}
for p in paths:
 if p!='app/src/main':assert trees[p]==git('rev-parse',parent+':'+p)
selectors=['dev.russiancompanion.v2.UnitIdentityCacheReadThroughTest','dev.russiancompanion.v2.RemainingReauditCorrectionTest'];methods=[]
for cls in selectors:
 p=R/('app/src/test/java/'+cls.replace('.','/')+'.kt');methods +=[{'testClass':cls,'method':m}for m in re.findall(r'@Test\s+fun\s+(\w+)\s*\(',p.read_text())]
assert len(methods)==25
protected={}
for folder in ['engineering-unit14-broad-host-regression-2026-10-28','engineering-unit14-downstream-fixture-followup-2026-10-28','engineering-unit-identity-cache-baseline-2026-10-28']:
 for p in(B/folder).rglob('*'):
  if p.is_file():protected[p.relative_to(R).as_posix()]=sha(p.read_bytes())
prior=json.loads((B/'engineering-unit14-downstream-fixture-followup-2026-10-28/PREPARATION.json').read_text())
prep={'parentCommit':parent,'changedProductionFile':source,'oldSourceSha256':sha(old),'newSourceSha256':sha(new),'inputTrees':trees,'testHashes':tests,'testsUnchanged':282,'baselineWholeTestUnchanged':True,'currentRecordHashes':prior['currentRecordHashes'],'archives':prior['archives'],'protectedEvidenceHashes':protected,'sourceBindingStatus':'RECONCILIATION_PENDING;155original records retained unchanged; no acceptance inferred','officialRepositorySourceDigest':'PENDING_COMPILED_OBSERVATION','packCanonicalCodeAndAssetUnchanged':True,'all48Findings':'OPEN','audit':'INCOMPLETE','physicalAcceptance':'OUTSTANDING_LAST'}
scope={'inputTrees':trees,'selectors':selectors,'expectedMethods':methods,'methodCount':25,'classes':2,'inventoryOnlyUntilFreshDiscovery':True,'completeSuite':False}
for n,v in [('PREPARATION.json',prep),('SCOPE.json',scope)]:save(D/n,v);save(W/n,v)
for n in ['run.py','launch.py','supervise.py','resources.gradle']:
 text=(OLD/n).read_text()
 if n=='run.py':text=text.replace('3 methods /1fullclass: canonical-execution-count cold and eviction baselines plus strict cross-pack identity control; preserve actual outcomes; not complete suite','25 methods /2FULLclasses: unchanged3method cache baseline plus entire22method existing RemainingReauditCorrectionTest including cold/warm/off-main and weak-cache guards; preserve every outcome; not complete suite')
 (W/n).write_text(text);(D/n).write_text(text)
shutil.copyfile(__file__,D/'prepare.py');shutil.copyfile(__file__,W/'prepare.py')
for p in W.glob('*.py'):ast.parse(p.read_text())
(D/'STATUS.md').write_text('''# Bounded unit identity read-through correction prepared

Only V2GovernedIdentity.kt changes: cold/evicted unitDigest computes the same canonical digest once and stores it in the existing bounded weak identity cache. warm() distinguishes partial maps from full-unit coverage and fills missing units while reusing cached exact values. Cache limit8, weak keys, canonical calculations, policy/schema meanings, source data, answer keys and saved identity rules are unchanged. No hash allowlist or fabricated acceptance. All282test files, including the entire3method genuine baseline, remain byte-exact.

The original baseline source7c3005f8 reported3tests/1strictidentitycontrolPASS/2real countfailures(12vs1)/exit1. Full failures remain preserved in the separate baseline directory. Source/review binding reconciliation is PENDING after this production edit;155records stay unchanged and no current acceptance is inferred. All77protected archives/prior1499/interrupted119/baseline evidence remain unchanged.

25methods/2FULLclasses is prepared inventory only: the3unchanged baseline methods and the entire22method RemainingReauditCorrectionTest, including existing cold/warm/off-main and weak-key reclamation controls. Fresh discovery/runtime NOT_RUN. Inspect external /workspace/artifacts/unit-identity-cache-correction-2026-10-28 contexts/PIDs/completions before launch/edit. Codex only/no further Cursor use. All48OPEN/auditINCOMPLETE/four exporters16excluded/physicalLAST/noAPK/signing/merge/freeze/approval.
''')
(D/'NEXT_TASK.md').write_text('Execute fresh25method/2FULLclass discovery and actual corrected runtime; preserve every outcome. Review unchanged baseline and existing warm/off-main/reclamation controls. Then remaining UI regressions, authentic source-binding reconciliation and affected FULLclasses/full119retry before stable broad verification or curriculum advancement. Do not sum separate runs or imply qualified acceptance. Codex only;all48OPEN/auditINCOMPLETE/physicalLAST/noAPK/signing/merge/freeze/approval.\n')
rel=D.relative_to(R).as_posix();pointers=['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']
for n in pointers:
 p=R/n;link=rel+'/STATUS.md'if n=='AGENTS.md'else'../'+rel.removeprefix('audit/')+'/STATUS.md'
 p.write_text(f'Current checkpoint: [Unit identity read-through correction prepared]({link}).Oneproductioncachefile changed;canonical identities/cachelimit8/weakkeys/policies/asset/all282tests/whole3methodbaseline unchanged.25methods/2FULLclasses inventory only;fresh discovery/runtime pending.155bindingsRECONCILIATION_PENDING/77archives/priorraw evidence unchanged. Inspect external unit-identity-cache-correction-2026-10-28 before launch/edit. Codex only/no further Cursor use. All48OPEN/auditINCOMPLETE/four exporters16excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
save(D/'SHA256.json',{p.relative_to(D).as_posix():sha(p.read_bytes())for p in sorted(D.rglob('*'))if p.is_file()and p!=D/'SHA256.json'})
allowed=set(pointers)|{source}
for row in subprocess.check_output(['git','status','--porcelain=v1','-z','--untracked-files=all'],cwd=R).split(b'\0'):
 if not row:continue
 assert row[2:3]==b' 'and not any(k in row[:2]for k in[b'R',b'C']);n=row[3:].decode();assert n in allowed or n.startswith(rel+'/'),n
subprocess.run(['git','add',rel,*sorted(allowed)],cwd=R,check=True);subprocess.run(['git','diff','--cached','--check'],cwd=R,check=True)
subprocess.run(['git','commit','-m','Cache canonical unit identities after cold reads and eviction'],cwd=R,check=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
print(json.dumps({'correctionCommit':git('rev-parse','HEAD'),'methods':25,'classes':2,'all282TestsUnchanged':True,'bindings':'RECONCILIATION_PENDING','runtime':'NOT_RUN'}))
