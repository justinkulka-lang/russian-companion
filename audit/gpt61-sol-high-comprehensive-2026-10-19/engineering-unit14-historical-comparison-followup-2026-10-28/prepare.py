from pathlib import Path
import subprocess,json,hashlib,zipfile,re,sys,os
R=Path('/workspace/russian-companion');W=Path(__file__).parent
B=R/'audit/gpt61-sol-high-comprehensive-2026-10-19'
D=B/'engineering-unit14-historical-comparison-followup-2026-10-28'
P=B/'engineering-unit14-affected-integration-2026-10-28'
git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
blob=lambda s,n:subprocess.check_output(['git','show',s+':'+n],cwd=R)
sha=lambda b:hashlib.sha256(b).hexdigest()
save=lambda p,v:p.write_text(json.dumps(v,indent=2)+'\n')
base='ae82d742ce2f9fac89acb91f618224187922e5ca'
assert git('rev-parse','HEAD')==base and not git('status','--porcelain')
assert git('branch','--show-current')=='cursor/content-audit-corrections-964f'
for p in Path('/proc').iterdir():
    if not p.name.isdigit():continue
    try:argv=(p/'cmdline').read_bytes().split(b'\0');cmd=b' '.join(argv).decode(errors='replace')
    except OSError:continue
    assert not (Path(argv[0].decode(errors='replace')).name=='java' and 'Gradle Test Executor' in cmd),(p.name,cmd[:150])
prior=json.loads((P/'PREPARATION.json').read_text())
originalHashes={n:sha((R/n).read_bytes()) for n in prior['existingTestFiles']}
assert len(originalHashes)==281
names=['Unit13BindingConservationTest','Unit13PresentCompatibilityTest','Unit14CumulativeReviewTest','Unit14ImpersonalRemediationTest','Unit15CumulativeReviewTest','Units1620SourceIntakeTest','Units2630SourceIntakeTest']
files=['app/src/test/java/dev/russiancompanion/v2/'+n+'.kt' for n in names]
D.mkdir()
with zipfile.ZipFile(D/'ORIGINAL_COMPARISONS.zip','x',zipfile.ZIP_DEFLATED) as z:
    for n in files:z.writestr(n,blob(base,n))
asset='app/src/main/assets/content/v2-curriculum.json'
oldId='unit14-prior-reading-reported-time-v1';newId='unit14-prior-plural-possession-reported-time-v2'
matching='9101af8c182b3415426ed9226cb211a707a2c393'
old=blob(matching,asset);current=(R/asset).read_bytes()
assert old==blob('bd00e5082c9f31d2ae3b379dd6204dd3b28e4475',asset)
assert current.count(newId.encode())==1 and old==current.replace(newId.encode(),oldId.encode(),1)
save(D/'POLICY_ONLY_ASSET_PROOF.json',dict(currentAssetSha256=sha(current),oldAssetSha256=sha(old),currentProducer='d72afd1232857a857624c20c58f4bcd3cb5c8564',oldProducer=matching,onlyRawByteDifference=dict(oldPolicy=oldId,newPolicy=newId),noOtherAssetFieldChanged=True))
refPath='app/src/test/resources/reviewed-curriculum-cyrillic.json';kernelPath='app/src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt'
reference=blob(matching,refPath);kernel=blob(matching,kernelPath);referenceJson=json.loads(reference)
entry=next(e for e in referenceJson['files'] if e['path']=='src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt')
assert entry['sha256']==sha(kernel)=='bda592eac2572396d9852ec818740d0a02d8b4879d7593568833ecac6515adc3'
assert len(referenceJson['subsequentEngineeringReferenceReviews'])==8
assert referenceJson['subsequentEngineeringReferenceReviews'][-1]['sourceCommit']=='bd00e5082c9f31d2ae3b379dd6204dd3b28e4475'
with zipfile.ZipFile(D/'MATCHING_HISTORICAL_KERNEL_REFERENCE.zip','x',zipfile.ZIP_DEFLATED) as z:
    z.writestr(refPath,reference);z.writestr(kernelPath,kernel)
save(D/'MATCHING_HISTORICAL_KERNEL_REFERENCE.json',dict(sourceCommit=matching,referenceSha256=sha(reference),kernelSha256=sha(kernel),recordedKernelSha256=entry['sha256'],entries=8,matchingPair=True,semanticAcceptance=False,rightsAcceptance=False))
def change(n,old,new):
    p=R/n;t=p.read_text();assert t.count(old)==1,(n,old[:120],t.count(old));p.write_text(t.replace(old,new))
# Only the old Unit13 whole-unit-list equality is scoped. Other comparisons stay current.
n=files[0];line='        assertEquals(pack.units.filter { it.number != 13 }, before13.units.filter { it.number != 13 })'
change(n,line,'''        // Original Unit13 contract predates only the proven Unit14 declaration change.
        RecordedCurriculumConservationScope.beforeUnit14PluralPossessionReview(root, pack).let { pack ->
    '''+line+'''
        }
        RecordedCurriculumConservationScope.assertCurrent(root, pack)''')
change(n,'        val now = Json.parseToJsonElement(File(root, path).readText()).jsonObject',
'''        // Keep the exact Unit13 append/count/source assertions on its genuine matching pair.
        val referenceCommit = "9101af8c182b3415426ed9226cb211a707a2c393"
        val now = Json.parseToJsonElement(sourceBytes(referenceCommit, path).toString(Charsets.UTF_8)).jsonObject''')
change(n,'MessageDigest.getInstance("SHA-256").digest(File(root, "app/$name").readBytes())',
    'MessageDigest.getInstance("SHA-256").digest(sourceBytes(referenceCommit, "app/$name"))')
needle='''        assertFalse(last.getValue("rightsAcceptance").jsonPrimitive.boolean)
    }
}'''
change(n,needle,'''        assertFalse(last.getValue("rightsAcceptance").jsonPrimitive.boolean)
        // Current kernel identity remains separately checked, alongside unchanged Unit14 binding proofs.
        val live = Json.parseToJsonElement(File(root, path).readText()).jsonObject
        val liveEntry = live.getValue("files").jsonArray.map { it.jsonObject }.single {
            it.getValue("path").jsonPrimitive.content == kernel
        }
        val liveHash = MessageDigest.getInstance("SHA-256").digest(File(root, "app/$kernel").readBytes()).joinToString("") { "%02x".format(it) }
        assertEquals(liveHash, liveEntry.getValue("sha256").jsonPrimitive.content)
        assertFalse(live.getValue("semanticAcceptance").jsonPrimitive.boolean)
        assertFalse(live.getValue("rightsAcceptance").jsonPrimitive.boolean)
    }

    private fun sourceBytes(commit: String, path: String): ByteArray {
        val process = ProcessBuilder("git", "show", "$commit:$path").directory(root).start()
        val bytes = process.inputStream.readBytes()
        assertEquals(0, process.waitFor())
        return bytes
    }
}''')
# Only the Unit13 baseline equality and old inventory use the prior Unit14 declaration.
n=files[1]
change(n,'        val restored = pack.copy(units = pack.units.map { u -> if (u.number != 13) u else',
'''        val before14 = RecordedCurriculumConservationScope.beforeUnit14PluralPossessionReview(root, pack)
        val restored = before14.copy(units = before14.units.map { u -> if (u.number != 13) u else''')
change(n,'        val policies = (3..30).map { cumulativeReviewPolicy(it)!! }',
'''        // Original Unit13 inventory is a historical contract; all task/mutation checks below stay current.
        val policies = (3..30).map { if (it == 14) Unit14CumulativeReviewPolicy.definition else cumulativeReviewPolicy(it)!! }
        val currentPolicies = (3..30).map { cumulativeReviewPolicy(it)!! }
        assertEquals(Unit14PluralPossessionCumulativeReviewPolicy.definition, currentPolicies.single { it.targetUnit == 14 })
        assertEquals(60, currentPolicies.sumOf { it.pool.size })
        assertEquals(121, currentPolicies.sumOf { p -> p.pool.sumOf { it.candidateItemIds.size } })
        assertEquals(57, currentPolicies.flatMap { it.pool }.flatMap { it.candidateItemIds }.distinct().size)
        assertEquals(18, currentPolicies.count { p -> p.pool.any { it.candidateItemIds == listOf("u1-i-decode-pair") } })''')
change(n,'            put("canonicalSourceDigest", V2Validator.contentReviewDigest(pack))',
'''            put("canonicalSourceDigest", V2Validator.contentReviewDigest(pack))
            put("canonicalSourceDigestLegacyLabelMeansPackContentReviewDigest", true)
            put("policyInventoryScope", "historical Unit13 activation, before Unit14 v2 declaration")
            put("currentPools", 60); put("currentPlacements", 121)
            put("currentUniqueCandidates", 57); put("currentPrintSingletonPolicies", 18)''')
# Three explicit v1 behavioral contracts retain exact old answers, UI slot index and missing-owner order.
n=files[2]
for method in ['ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup','actualUnit14ContinueReportedTimeCueTeachingPracticeReturnAndAdvanceStayOnUnit14','placementWithTaughtUnit1ButUntaughtUnit13RepairsTheMissingOwnerInsteadOfUnit1']:
    change(n,'    @Test fun '+method+'() {\n        val p = pack()',
        '    @Test fun '+method+'() {\n        // Preserve this exact v1 contract; full-pack proof and unchanged current v2 controls remain required.\n        val p = RecordedCurriculumConservationScope.beforeUnit14PluralPossessionReview(root(), pack())')
change(n,'''            answer(p, s, a, 0); answer(p, s, a, 1)
            assertTrue(advancementDecision(p, p.unit(14)!!, s.v2LearnerView()).allowed)''',
'''            answer(p, s, a, 0); answer(p, s, a, 1)
            // Current v2 requires every declared slot before testing stale answers and transactional rollback.
            a.slots.indices.drop(2).forEach { answer(p, s, a, it) }
            assertTrue(advancementDecision(p, p.unit(14)!!, s.v2LearnerView()).allowed)''')
# Only the historical whole-unit equality is scoped; all current content/validator checks remain current.
n=files[3];line='        assertEquals(historicalUnit, validationPack.unit(14))'
change(n,line,'''        RecordedCurriculumConservationScope.beforeUnit14PluralPossessionReview(root(), validationPack).let { validationPack ->
    '''+line+'''
        }
        RecordedIntakeCorrectionScope.assertCurrentUnitMatches(validationPack,
            RecordedCurriculumConservationScope.source(root(), RecordedCurriculumConservationScope.currentPackImplementation), 14)''')
# Current Unit15 preparation must finish the actual declared predecessor assignment; no assertion changes.
n=files[4]
change(n,'''        answer(p, s, fourteenth, 0); answer(p, s, fourteenth, 1)
        runBlocking { advanceFrom(p, s, 14) }''',
'''        answer(p, s, fourteenth, 0); answer(p, s, fourteenth, 1)
        fourteenth.slots.indices.drop(2).forEach { answer(p, s, fourteenth, it) }
        runBlocking { advanceFrom(p, s, 14) }''')
# Keep old Unit14 digest literals, independently guard exact current identity.
for n,indent,root,loader,qual in [(files[5],'            ','repo()','historicalSourcePack(RecordedCurriculumConservationScope.currentPackImplementation)','org.junit.Assert.'),(files[6],'                ','root','RecordedIntakeCorrectionScope.load(root, RecordedCurriculumConservationScope.currentPackImplementation)','')]:
    needle=indent+'''\u007d else {
'''+indent+'''    assertEquals("unit $number", digest, V2Validator.unitContentReviewDigest(pack, number))
'''+indent+'''}
'''+('        }\n    }\n' if n==files[5] else '            } else {')
    new=indent+'''} else if (number == 14) {
'''+indent+'''    // Retain the old v1 pin only on its proven whole-pack declaration scope.
'''+indent+f'''    RecordedCurriculumConservationScope.beforeUnit14PluralPossessionReview({root}, pack).let {{ pack ->
'''+indent+'''        assertEquals("unit $number", digest, V2Validator.unitContentReviewDigest(pack, number))
'''+indent+'''    }
'''+indent+f'''    RecordedIntakeCorrectionScope.assertCurrentUnitMatches(pack, {loader}, number)
'''+indent+f'''    {qual}assertNotEquals("Current Unit14 v2 review identity", digest, V2Validator.unitContentReviewDigest(pack, number))
'''+needle
    change(n,needle,new)
sys.path.insert(0,str(B/'engineering-unit13-historical-comparison-followup-2026-10-27'))
from assertion_calls import assertions
reviews=[]
for n in files:
    before=blob(base,n).decode();after=(R/n).read_text();a=assertions(before);b=assertions(after);j=0
    for x in b:
        if j<len(a) and x==a[j]:j+=1
    assert j==len(a),(n,j,len(a))
    assert re.findall(r'@Test\b',before)==re.findall(r'@Test\b',after)
    reviews.append(dict(path=n,originalSha256=sha(before.encode()),newSha256=sha(after.encode()),originalAssertionInvocations=len(a),newAssertionInvocations=len(b),everyOriginalAssertionPreservedInOrder=True,noTestMethodAddedOrRemoved=True))
for n,h in originalHashes.items():
    if n not in files:assert sha((R/n).read_bytes())==h,n
for n,h in prior['currentRecordHashes'].items():assert sha((R/n).read_bytes())==h,n
for n,h in prior['archives'].items():
    assert sha((R/n).read_bytes())==h,n
    with zipfile.ZipFile(R/n) as z:assert z.testzip() is None
subprocess.run(['git','add',*files],cwd=R,check=True);indexTree=git('write-tree')
trees={n:git('rev-parse',indexTree+':'+n) for n in ['app/src/main','app/src/test','catalog','docs','review','release']}
originalScope=json.loads((P/'SCOPE.json').read_text());extra={'Unit14PluralPossessionCompatibilityTest','Unit14PluralPossessionReviewBaselineTest','Unit14PluralPossessionReviewUiTest','Unit14BindingConservationTest','Unit13PresentReviewBaselineTest','Unit13PresentReviewUiTest'}
tests=[t for t in originalScope['expectedMethods'] if t['testClass'].split('.')[-1] in set(names)|extra]
failures=json.loads((P/'ACTUAL_FAILURES.json').read_text())['failures']
assert len(tests)==79 and len({t['testClass'] for t in tests})==13 and len(failures)==14
controls13=[t for t in tests if t['testClass'].split('.')[-1] in {'Unit13PresentCompatibilityTest','Unit13PresentReviewBaselineTest','Unit13PresentReviewUiTest','Unit13BindingConservationTest'}]
controls14=originalScope['currentUnit14ControlMethods'];assert len(controls13)==19 and len(controls14)==22
scope=dict(selectors=sorted({t['testClass'] for t in tests}),expectedMethods=tests,methodCount=79,classes=13,inputTrees=trees,fullClasses=True,inventoryOnlyUntilFreshDiscovery=True,priorFailedIdentities=[dict(testClass=x['testClass'],method=x['method']) for x in failures],currentUnit14ControlMethods=controls14,currentUnit13ControlMethods=controls13,completeSuite=False)
prep=dict(parentCommit=base,productionTree=trees['app/src/main'],inputTrees=trees,existingTestFiles={n:sha((R/n).read_bytes()) for n in originalHashes},originalTestFiles=originalHashes,changedComparisonFiles=files,comparisonReview=reviews,currentRecordHashes=prior['currentRecordHashes'],archives=prior['archives'],priorFailureEvidenceHashes={p.relative_to(R).as_posix():sha(p.read_bytes()) for p in P.iterdir() if p.is_file()},repositorySourceDigest=prior['repositorySourceDigest'],packContentReviewDigest=prior['packContentReviewDigest'],plannedClasses=13,methodCount=79,originalAssertionInvocations=sum(x['originalAssertionInvocations'] for x in reviews),newAssertionInvocations=sum(x['newAssertionInvocations'] for x in reviews),otherTestFilesUnchanged=274,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST')
for n,v in [('SCOPE.json',scope),('PREPARATION.json',prep),('COMPARISON_REVIEW.json',reviews)]:save(W/n,v);save(D/n,v)
print(json.dumps(dict(originalAssertions=prep['originalAssertionInvocations'],newAssertions=prep['newAssertionInvocations'],files=7,otherTestFilesConserved=274,plannedMethods=79,plannedFullClasses=13,productionUnchanged=True),indent=2))
