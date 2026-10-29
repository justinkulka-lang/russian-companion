from pathlib import Path
import json,hashlib,subprocess,zipfile,re,shutil
R=Path('/workspace/russian-companion');W=Path(__file__).parent;D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit14-source-reconciliation-2026-10-28'
BASE='ab5795a015e63695482d00582ab8796e791aabfc';S='d72afd1232857a857624c20c58f4bcd3cb5c8564';MATCH='9101af8c182b3415426ed9226cb211a707a2c393'
git=lambda *a:subprocess.check_output(['git',*a],cwd=R)
sha=lambda b:hashlib.sha256(b).hexdigest()
assert git('rev-parse','HEAD').decode().strip()==BASE and not git('status','--porcelain=v1','-z');D.mkdir()
save=lambda n,v:(D/n).write_text(json.dumps(v,indent=2)+'\n')
paths=['app/src/test/java/dev/russiancompanion/v2/RecordedCurriculumConservationScope.kt','app/src/test/java/dev/russiancompanion/v2/review/Unit1ReviewPackage.kt','app/src/test/resources/reviewed-curriculum-cyrillic.json']
with zipfile.ZipFile(D/'ORIGINAL_TOOLING.zip','x',zipfile.ZIP_DEFLATED) as z:
    for p in paths:z.write(R/p,p)
k='app/src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt';ref=paths[2];matches=[]
for s in [MATCH,'f670c24e04def2829a7949e83b91b83a0dedf338',BASE,'31ac807387253082c44bd2f6d67021478f0df66e']:
    data=json.loads(git('show',s+':'+ref));recorded=next(x['sha256'] for x in data['files'] if x['path']==k.removeprefix('app/'));actual=sha(git('show',s+':'+k))
    matches.append(dict(commit=s,kernelSha256=actual,referenceRecordedSha256=recorded,matches=actual==recorded,referenceFileSha256=sha(git('show',s+':'+ref))))
assert matches[0]['matches'] and matches[1]['matches'] and not matches[2]['matches'] and matches[3]['matches'];save('MATCHING_HISTORICAL_KERNEL_REFERENCE.json',dict(selectedCommit=MATCH,observations=matches,priorUnit13PairUnchanged=True,postCorePairIsNotMatching=True))
p=R/paths[0];t=p.read_text();old='const val currentPackImplementation = "bd00e5082c9f31d2ae3b379dd6204dd3b28e4475"';assert t.count(old)==1
t=t.replace(old,f'const val beforeUnit14PackImplementation = "bd00e5082c9f31d2ae3b379dd6204dd3b28e4475"\n    const val beforeUnit14ReviewImplementation = "96242a17a87cc2ec0f5d7418cd98a9d3220a9e99"\n    const val currentPackImplementation = "{S}"')
old='        13 to currentPackImplementation,\n        14 to "96242a17a87cc2ec0f5d7418cd98a9d3220a9e99",';assert t.count(old)==1
t=t.replace(old,'        13 to beforeUnit14PackImplementation,\n        14 to currentPackImplementation,')
needle="    /** Restore only Unit13's v2 declaration;";assert t.count(needle)==1
t=t.replace(needle,'''    /** Restore only Unit14's v1 declaration; prove the complete actual-current baseline. */
    fun beforeUnit14PluralPossessionReview(root: File, current: V2CurriculumPack): V2CurriculumPack {
        assertCurrent(root, current)
        val prior = current.copy(units = current.units.map { unit ->
            if (unit.number == 14) unit.copy(advancement = unit.advancement!!.copy(
                cumulativeReviewPolicyId = Unit14CumulativeReviewPolicy.definition.id)) else unit
        })
        assertEquals("Only the Unit14 policy declaration may differ from its actual v1 asset", source(root, beforeUnit14PackImplementation), prior)
        assertEquals("The actual-current Unit14 baseline must match in full", source(root, "9101af8c182b3415426ed9226cb211a707a2c393"), prior)
        RecordedIntakeCorrectionScope.assertCurrentUnitMatches(prior, source(root, beforeUnit14ReviewImplementation), 14)
        return prior
    }

'''+needle)
old='''        val prior = current.copy(units = current.units.map { unit ->
            if (unit.number == 13)''';new='''        val beforeUnit14 = beforeUnit14PluralPossessionReview(root, current)
        val prior = beforeUnit14.copy(units = beforeUnit14.units.map { unit ->
            if (unit.number == 13)''';assert t.count(old)==1;t=t.replace(old,new)
t=t.replace('                    13 -> beforeUnit13PresentReview(root, current)','                    13 -> beforeUnit13PresentReview(root, current)\n                    14 -> beforeUnit14PluralPossessionReview(root, current)')
needle='''                if (number == 13) {''';start=t.index(needle);end=t.index('\n                }',start)+len('\n                }')
t=t[:end]+'''
                if (number == 14) {
                    RecordedIntakeCorrectionScope.assertCurrentUnitMatches(current, source(root, currentUnitSources.getValue(number)), number)
                    assertNotEquals("Current Unit14 v2 must retain its own identity", digest,
                        V2Validator.unitContentReviewDigest(current, number))
                }'''+t[end:];p.write_text(t)
p=R/paths[1];t=p.read_text();needle='internal fun curriculumCommitForUnit';assert t.count(needle)==1
t=t.replace(needle,f'// Actual verified Unit14 supplied-plural/possession/reported-time core; older sources stay historical.\ninternal const val UNIT14_PLURAL_POSSESSION_REVIEW_CURRICULUM_COMMIT = "{S}"\n\n'+needle)
old='else if (unitNumber == 14) UNIT14_IMPERSONAL_REPAIR_CURRICULUM_COMMIT';assert t.count(old)==1;t=t.replace(old,'else if (unitNumber == 14) UNIT14_PLURAL_POSSESSION_REVIEW_CURRICULUM_COMMIT');p.write_text(t)
p=R/ref;original=p.read_text();data=json.loads(original);kernel=k.removeprefix('app/');entry=next(x for x in data['files'] if x['path']==kernel);oldhash=entry['sha256'];newhash=sha((R/k).read_bytes());assert oldhash!=newhash
assert original.count('"sha256": "'+oldhash+'"')>=1
pos=original.index('"sha256": "'+oldhash+'"');t=original[:pos]+original[pos:].replace('"sha256": "'+oldhash+'"','"sha256": "'+newhash+'"',1)
record=dict(path=kernel,sourceCommit=S,previousRecordedSha256=oldhash,sha256=newhash,additionalExistingItemReferences=[],allExistingBindingsUnchanged=True,scope='Reuses existing supplied nominative plurals, possession/absence and reported-frame-time pools; separate Unit14v2/schema16, exact oldv1 and downstream owner14 bridge, English labels only. No new Cyrillic literal,item reference or language construction; no semantic/rights acceptance.',semanticAcceptance=False,rightsAcceptance=False)
assert t.endswith('\n  ]\n}\n');indented='\n'.join('    '+line for line in json.dumps(record,indent=2,ensure_ascii=False).splitlines());t=t[:-len('\n  ]\n}\n')]+',\n'+indented+'\n  ]\n}\n';p.write_text(t)
now=json.loads(t);assert now['subsequentEngineeringReferenceReviews'][:-1]==data['subsequentEngineeringReferenceReviews'];assert len(now['files'])==19 and sum(len(x['bindings']) for x in now['files'])==292
u=(R/'app/src/test/java/dev/russiancompanion/v2/Unit13BindingConservationTest.kt').read_text()
for old,new in [('Unit13Binding','Unit14Binding'),('ReconstructsOnlyUnit13','ReconstructsOnlyUnit14'),('beforeUnit13PresentReview(root, pack)','beforeUnit14PluralPossessionReview(root, pack)'),('before13','before14'),('31ac807387253082c44bd2f6d67021478f0df66e',MATCH),('Unit13WiderCumulativeReviewPolicy','Unit14CumulativeReviewPolicy'),('before14.unit(13)','before14.unit(14)'),('it.number != 13','it.number != 14'),('before14, 13','before14, 14'),('pack, 13','pack, 14'),('u.number != 13','u.number != 14'),('aa93df85b4290406d5b78b0035b4a77912ae25fe',MATCH),('bd00e5082c9f31d2ae3b379dd6204dd3b28e4475',S)]:u=u.replace(old,new)
needle='        assertEquals(RecordedIntakeCorrectionScope.load(root, "23b9179';assert u.count(needle)==1
u=u.replace(needle,'        assertEquals(RecordedIntakeCorrectionScope.load(root, "31ac807387253082c44bd2f6d67021478f0df66e"),\n            RecordedCurriculumConservationScope.beforeUnit13PresentReview(root, pack))\n'+needle)
(R/'app/src/test/java/dev/russiancompanion/v2/Unit14BindingConservationTest.kt').write_text(u)
core=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit14-plural-possession-correction-2026-10-28';prior=json.loads((core/'PROTECTED_EVIDENCE_HASHES.json').read_text());oldtests={p:sha(git('show',BASE+':'+p)) for p in git('ls-tree','-r','--name-only',BASE,'app/src/test').decode().splitlines()};assert len(oldtests)==280
archives=json.loads((core/'PROTECTED_ARCHIVE_INVENTORY.json').read_text());assert len(archives)==77
records=json.loads((core/'SOURCE_BINDING_HASHES.json').read_text());assert len(records)==155
protected={p:sha(git('show',BASE+':'+p)) for p in prior if p not in paths}
save('CONSERVATION.json',dict(parentCommit=BASE,actualVerifiedProductionSource=S,unchangedProductionTree=git('rev-parse',BASE+':app/src/main').decode().strip(),changedToolingFiles=paths,originalToolingHashes={p:sha(git('show',BASE+':'+p)) for p in paths},allPriorTestHashes=oldtests,immutablePriorEvidence=protected,archives={a['path']:a['sha256'] for a in archives},currentRecordHashes=records,oldRepositorySourceDigest='3c0acbb95297cf10f08da2bd8391463067eece22fec785158b5d2680d37dc2ba',newRepositorySourceDigest='487d9118d7154dd59162dbb7024f1349bc53d5b7bf89e9d79022d97eaa1eeb9a',oldPackContentReviewDigest='15c6ae393767cb7ffc190882867bfad923c9e81b07d0f4285cf0a678f2489280',newPackContentReviewDigest='39c3563ba1b149b36977ae0fc2c0f920e282101e9e7870c06d141fe1a10e65ec',sourceBindingsStatus='STALE_UNCHANGED',all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST'))
for p,h in oldtests.items():
    if p not in paths:assert sha((R/p).read_bytes())==h,p
for n in ['compile_tooling.py','generate.py','GeneratePendingEvidence.java','Digest.java']:shutil.copy2(Path('/workspace/artifacts/unit13-source-reconciliation-2026-10-27')/n,W/n)
for n in ['prepare_tooling.py','compile_tooling.py','generate.py']:shutil.copy2(W/n,D/n)
(D/'STATUS.md').write_text('''# Unit14 source tooling prepared; generation pending

Strict before14→13→12→11→10→9→8→7→6→5whole-pack proofs preserve original assertions/pins. NewUnit14source alone selects actual20/20verified d72afd12core;29other choices exact. Kernel reference currenthash plus one pending engineering append;19files/292bindings/history unchanged,no semantic/rights acceptance. Actual9101af8c/f670matching pairs verified;ab5795a0postcore newkernel/oldreference does not match;31acprior13pair retained. Three originaltooling/reference files archived.280prior testsourcefiles exact except these3reviewedtooling/referencefiles;wholebaseline/coretests exact. New2methodbindingclass is inventory only.155bindingsSTALE_UNCHANGED.

External /workspace/artifacts/unit14-source-reconciliation-2026-10-28. Compile actualtooling and source digest,then two independent real compiled renders/all153outputs/30packages;review exact actual155record deltas before apply. Preserve priorversions/worksheets/29otherchoices/reviewer/rights/mapping/owner/admission/approval fields. NoJUnit/generation yet. Do not duplicatejobs or edit activeinputs. Then focusedproofs/affectedFULLclasses/fresh broad. Prior20core/2baseline/1477broad separate/unsummed.

All48OPEN/auditINCOMPLETE/four exporters16methods excluded/physical GalaxyS26Ultra airplane-mode/reboot acceptance LAST after final independent comprehensive nonphysical audit. NoAPKassembly/install/newsigning/merge/freeze/formaladvancement/releaseapproval;APKidentity/signaturenull. PrivatePDFunread/unpackaged/environmentunchanged/publication user-confirmed independently unobserved/JITdirectiveunadopted/nativecauseUNCONFIRMED. Qualifiedreviews/CC024risk/CC048authority/originalUnit7restorecause unresolved. Oneagent/existingcheckout/DraftPR64.
''')
(D/'NEXT_TASK.md').write_text('Authenticate actualcompiled source/two realrenders and exact155recordidentity deltas;preserve all originals/77archives/277untouchedoldertests/three reviewedtoolingfiles. Then focusedbindingproofs,affectedFULLclasses and freshbroad. No waiver/resnapshot/qualified acceptance. All48OPEN/auditINCOMPLETE/physicalLAST/no release actions.\n')
rel=D.relative_to(R).as_posix()
for name in ['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']:
    p=R/name;link=rel+'/STATUS.md' if name=='AGENTS.md' else '../'+rel.removeprefix('audit/')+'/STATUS.md'
    p.write_text(f'Current checkpoint: [Unit14 source tooling prepared;generation pending]({link}). Strict14→13→12→11→10→9→8→7→6→5/currentguard/only14sourcechoice/pendingkernelreference;actual9101matchingpair authenticated. New2methodinventory only;155bindingsSTALE_UNCHANGED. Wholebaseline/coreassertions/77archives conserved. Inspect external unit14-source-reconciliation-2026-10-28 before launch/edit. All48OPEN/auditINCOMPLETE/four exporters16methods excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
print(json.dumps(dict(prepared=True,changedTooling=paths,newMethodsInventory=2,oldTests=280,matchingPreCorePair=MATCH,bindings='155_STALE_UNCHANGED')))
