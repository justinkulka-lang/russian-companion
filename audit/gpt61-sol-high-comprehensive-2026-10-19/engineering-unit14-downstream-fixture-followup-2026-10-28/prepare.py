from pathlib import Path
import subprocess,json,hashlib,zipfile,csv,io,re,shutil,sys
R=Path('/workspace/russian-companion');B=R/'audit/gpt61-sol-high-comprehensive-2026-10-19'
NAME='engineering-unit14-downstream-fixture-followup-2026-10-28'
D=B/NAME;W=Path('/workspace/artifacts')/NAME
git=lambda *a:subprocess.check_output(['git',*a],cwd=R).decode().strip()
sha=lambda b:hashlib.sha256(b).hexdigest()
save=lambda p,v:p.write_text(json.dumps(v,indent=2)+'\n')
assert not subprocess.check_output(['git','status','--porcelain=v1','-z','--untracked-files=all'],cwd=R)
assert not D.exists() and not W.exists()
D.mkdir();W.mkdir();parent=git('rev-parse','HEAD')
prior=json.loads((B/'engineering-unit14-broad-host-regression-2026-10-28/PREPARATION.json').read_text())
sys.path.insert(0,'/workspace/artifacts/unit14-historical-comparison-followup-2026-10-28')
from assertion_calls import assertions
shutil.copyfile('/workspace/artifacts/unit14-historical-comparison-followup-2026-10-28/assertion_calls.py',W/'assertion_calls.py')
insert=b'        fourteenth.slots.indices.drop(2).forEach { answer(p, s, fourteenth, it) }\n'
oldblock=b'        answer(p, s, fourteenth, 0); answer(p, s, fourteenth, 1)\n        runBlocking { advanceFrom(p, s, 14) }'
reviews=[]
with zipfile.ZipFile(D/'ORIGINAL_FIXTURES.zip','x',zipfile.ZIP_DEFLATED)as z:
 for n in range(16,31):
    path=f'app/src/test/java/dev/russiancompanion/v2/Unit{n}CumulativeReviewTest.kt';p=R/path;old=p.read_bytes()
    assert sha(old)==prior['existingTestFiles'][path] and old.count(oldblock)==1
    new=old.replace(oldblock,oldblock.replace(b'        runBlocking',insert+b'        runBlocking'),1)
    assert new.count(insert)==1 and new.replace(insert,b'',1)==old
    before=assertions(old.decode());after=assertions(new.decode());assert before==after
    assert re.findall(rb'@Test\s+fun\s+(\w+)',old)==re.findall(rb'@Test\s+fun\s+(\w+)',new)
    z.writestr(path,old);p.write_bytes(new)
    reviews.append({'path':path,'originalSha256':sha(old),'newSha256':sha(new),'originalAssertionCalls':len(before),'newAssertionCalls':len(after),'onlyChange':'one extra remaining-assignment-index loop immediately before Unit14 advance','removingInsertedLineRestoresOriginalBytes':True})
changed={r['path']for r in reviews}
for path,h in prior['existingTestFiles'].items():
 if path not in changed:assert sha((R/path).read_bytes())==h
for key in ['currentRecordHashes','archives','priorFailureEvidenceHashes']:
 for path,h in prior[key].items():assert sha((R/path).read_bytes())==h
for path in prior['archives']:
 with zipfile.ZipFile(R/path)as z:assert z.testzip()is None
protected=dict(prior['priorFailureEvidenceHashes'])
for p in (B/'engineering-unit14-broad-host-regression-2026-10-28').rglob('*'):
 if p.is_file():protected[p.relative_to(R).as_posix()]=sha(p.read_bytes())
subprocess.run(['git','add',*[r['path']for r in reviews]],cwd=R,check=True)
tree=git('write-tree');trees={n:git('rev-parse',tree+':'+n)for n in prior['inputTrees']}
for path,h in prior['inputTrees'].items():
 if path!='app/src/test':assert trees[path]==h
follow=list(csv.DictReader((B/'CURSOR_UNIT14_FAILURE_HANDOFF/FOLLOWUP_SCOPE.csv').open()))
expected=[{'testClass':'dev.russiancompanion.v2.'+r['test_class'],'method':r['method'],'role':r['role']}for r in follow]
assert len(expected)==119
selectors=sorted({r['testClass']for r in expected});assert len(selectors)==17
allmethods=set()
for cls in selectors:
 body=(R/('app/src/test/java/'+cls.replace('.','/')+'.kt')).read_text()
 allmethods|={(cls,m)for m in re.findall(r'@Test\s+fun\s+(\w+)\s*\(',body)}
assert allmethods=={(r['testClass'],r['method'])for r in expected}
prep={'parentCommit':parent,'testedFailureSource':'8eb9d3d4cc3fdbcd70d5b8904087ddad18acfaf1','cursorDeliveryCommit':'5dfc303f3928f996dedfccd0afa7a030cd3eb6cc','inputTrees':trees,'productionTree':trees['app/src/main'],'existingTestFiles':{p:sha((R/p).read_bytes())for p in prior['existingTestFiles']},'currentRecordHashes':prior['currentRecordHashes'],'archives':prior['archives'],'priorFailureEvidenceHashes':protected,'fixtureReview':reviews,'unchangedOtherTestFiles':266,'originalAssertionCalls':sum(r['originalAssertionCalls']for r in reviews),'repositorySourceDigest':prior['repositorySourceDigest'],'packContentReviewDigest':prior['packContentReviewDigest'],'all48Findings':'OPEN','audit':'INCOMPLETE','physicalAcceptance':'OUTSTANDING_LAST'}
scope={'inputTrees':trees,'selectors':selectors,'expectedMethods':expected,'methodCount':119,'classes':17,'inventoryOnlyUntilFreshDiscovery':True,'runtimeStatus':'NOT_RUN','failedMethods':[r for r in expected if r['role']=='failed'],'passedSiblings':[r for r in expected if r['role']=='passed_sibling'],'controls':[r for r in expected if r['role']=='control'],'completeSuite':False}
for name,value in [('PREPARATION.json',prep),('SCOPE.json',scope),('FIXTURE_CORRECTION_REVIEW.json',reviews)]:save(W/name,value);save(D/name,value)
shutil.copyfile('/workspace/artifacts/cursor-unit14-git-delivery-2026-10-28/CODEX_RECEIPT_VERIFICATION.json',D/'CURSOR_DELIVERY_RECEIPT.json')
shutil.copyfile('/workspace/artifacts/cursor-actual-failure-receipt-2026-10-28/CODEX_ALL_60_TRACE_CHECKS.json',D/'ALL_60_ORIGINAL_TRACE_SOURCE_CHECKS.json')
shutil.copyfile(__file__,W/'prepare.py');shutil.copyfile(__file__,D/'prepare.py')
(D/'STATUS.md').write_text(f'''# Downstream Unit14 prerequisite fixture correction prepared

Exactly15test files gain one line each in prepare(): after the original Unit14 index0/index1 answers, answer remaining assigned indices via drop(2) before advancement. Removing that line restores each original file byte-for-byte. All{prep['originalAssertionCalls']} original assertion calls, test identities, pins, historical scenarios, target assertions and Unit30 terminal behavior are unchanged.266other tests, production,155bindings,77archives and all prior broad failure evidence are conserved. No production-gate change or Unit15 curriculum change.

The complete nine-file Cursor delivery was independently verified and integrated unchanged from5dfc303f. All60full traces match these prerequisite paths; the prior broad1499reported/1438pass/60fail/1physicalskip/exit1 remains immutable and separate.

119methods/17FULLclasses is a prepared inventory only:60original failed methods+45siblings+14unchangedUnit14/15controls. Fresh discovery and actual runtime remain NOT_RUN. Inspect the external workspace engineering-unit14-downstream-fixture-followup-2026-10-28 before any launch or edit. Four exporters16methods remain excluded. APKidentity/signaturenull;no assembly/install/signing. All48OPEN/auditINCOMPLETE/physicalLAST/no merge/freeze/releaseapproval.
''')
(D/'NEXT_TASK.md').write_text('Fresh119method/17FULLclass discovery must match prepared identities, then separately pinned actual runtime with exact commands, XML/events, counts, full failures, lint/gate/cache/native/conservation evidence. Preserve complete or incomplete outcomes. No blanket hash updates or source substitutions. Review follow-up before another broad run or Unit15 curriculum work. One agent/existing checkout/OPEN DraftPR64; all48OPEN/auditINCOMPLETE/physicalLAST/noAPK/signing/merge/freeze/approval.\n')
rel=D.relative_to(R).as_posix()
for name in ['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']:
 p=R/name;link=rel+'/STATUS.md'if name=='AGENTS.md'else '../'+rel.removeprefix('audit/')+'/STATUS.md'
 p.write_text(f'Current checkpoint: [Downstream Unit14 fixture-only correction prepared]({link}).15one-line insertions/all{prep["originalAssertionCalls"]}assertion calls and original bytes conserved;266other tests/production/155bindings/77archives/prior60failureevidence exact.119methods/17fullclasses inventory only;fresh discovery/runtime pending. Inspect external workspace before launch/edit. All48OPEN/auditINCOMPLETE/four exporters16excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
print(json.dumps({'directory':str(D),'workspace':str(W),'parent':parent,'changedTestFiles':15,'originalAssertions':prep['originalAssertionCalls'],'methods':119,'classes':17,'runtime':'NOT_RUN'},indent=2))
