from pathlib import Path
import ast,hashlib,json,re,subprocess,zipfile
R=Path('/workspace/russian-companion');W=Path(__file__).parent
B=R/'audit/gpt61-sol-high-comprehensive-2026-10-19'
D=B/'engineering-unit13-historical-comparison-followup-2026-10-27'
P=B/'engineering-unit13-affected-continuation-2026-10-27'
git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
blob=lambda s,n:subprocess.check_output(['git','show',s+':'+n],cwd=R)
sha=lambda b:hashlib.sha256(b).hexdigest()
save=lambda p,v:p.write_text(json.dumps(v,indent=2)+'\n')
base='45b9f67ce168c75d17c5b9b1bb1674cb90a7be18'
assert git('rev-parse','HEAD')==base and not git('status','--porcelain')
assert git('branch','--show-current')=='cursor/content-audit-corrections-964f'
for p in Path('/proc').iterdir():
    if not p.name.isdigit():continue
    try:cmd=(p/'cmdline').read_bytes().replace(b'\0',b' ').decode(errors='replace')
    except OSError:continue
    assert not ('Gradle Test Executor' in cmd and 'java' in cmd),(p.name,cmd[:150])
prior=json.loads((P/'CONSERVATION_INPUTS.json').read_text())
originalHashes={n:sha((R/n).read_bytes()) for n in prior['existingTestFiles']}
names=['Unit13CumulativeReviewTest','Unit13PastTeachingTest','Unit13ContextTeachingTest','Units1620SourceIntakeTest','Units2630SourceIntakeTest']
files=['app/src/test/java/dev/russiancompanion/v2/'+n+'.kt' for n in names]
D.mkdir()
with zipfile.ZipFile(D/'ORIGINAL_COMPARISONS.zip','x',zipfile.ZIP_DEFLATED) as z:
    for n in files:z.writestr(n,blob(base,n))
asset='app/src/main/assets/content/v2-curriculum.json'
oldId='unit13-prior-reading-aspect-past-gender-v2';newId='unit13-prior-present-aspect-past-gender-v3'
old=blob('31ac807387253082c44bd2f6d67021478f0df66e',asset);current=(R/asset).read_bytes()
assert old==blob('ca0fdaeca8de81ab6a5d4935fbcdcb068c4af913',asset)
assert current.count(newId.encode())==1 and old==current.replace(newId.encode(),oldId.encode(),1)
save(D/'POLICY_ONLY_ASSET_PROOF.json',dict(currentAssetSha256=sha(current),oldAssetSha256=sha(old),currentProducer='bd00e5082c9f31d2ae3b379dd6204dd3b28e4475',oldProducer='ca0fdaeca8de81ab6a5d4935fbcdcb068c4af913',matchingHistoricalCheckpoint='31ac807387253082c44bd2f6d67021478f0df66e',onlyRawByteDifference=dict(oldPolicy=oldId,newPolicy=newId),noOtherAssetFieldChanged=True))
p=R/files[0];t=p.read_text()
for method in ['ordinaryCompletionAndEarlierReviewCannotAdvanceUntilThisAssignmentThenSnapshotSurvivesBackup','placementWithTaughtUnit1ButUntaughtUnit12RepairsTheMissingOwnerInsteadOfUnit1']:
    needle='    @Test fun '+method+'() {\n        val p = pack()'
    replacement='    @Test fun '+method+'() {\n        // This original v2 contract keeps its exact owners, slot skills, and assertions.\n        // The strict whole-pack proof changes only Unit13\'s declaration; current v3 has separate controls.\n        val p = RecordedCurriculumConservationScope.beforeUnit13PresentReview(root(), pack())'
    assert t.count(needle)==1,method;t=t.replace(needle,replacement)
p.write_text(t)
for n,var in [(files[1],'p'),(files[2],'validationPack')]:
    p=R/n;t=p.read_text();line=f'        assertEquals(historicalUnit, {var}.unit(13))'
    assert t.count(line)==1
    new=f'''        // Preserve the old whole-unit comparison on its full-pack-proven v2 declaration only.
        RecordedCurriculumConservationScope.beforeUnit13PresentReview(root(), {var}).let {{ {var} ->
    {line}
        }}
        // Actual current unit identity and all teaching/content/validation assertions below remain current.
        RecordedIntakeCorrectionScope.assertCurrentUnitMatches({var},
            RecordedCurriculumConservationScope.source(root(), RecordedCurriculumConservationScope.currentPackImplementation), 13)'''
    p.write_text(t.replace(line,new))
p=R/files[3];t=p.read_text()
needle='''            } else {
                assertEquals("unit $number", digest, V2Validator.unitContentReviewDigest(pack, number))
            }
        }
    }
'''
replacement='''            } else if (number == 13) {
                // Retain the original v2 digest on its proven whole-pack source; never reset the pin.
                RecordedCurriculumConservationScope.beforeUnit13PresentReview(repo(), pack).let { pack ->
                    assertEquals("unit $number", digest, V2Validator.unitContentReviewDigest(pack, number))
                }
                RecordedIntakeCorrectionScope.assertCurrentUnitMatches(pack,
                    historicalSourcePack(RecordedCurriculumConservationScope.currentPackImplementation), number)
                org.junit.Assert.assertNotEquals("Current Unit13 v3 review identity", digest,
                    V2Validator.unitContentReviewDigest(pack, number))
            } else {
                assertEquals("unit $number", digest, V2Validator.unitContentReviewDigest(pack, number))
            }
        }
    }
'''
assert t.count(needle)==1;t=t.replace(needle,replacement);p.write_text(t)
p=R/files[4];t=p.read_text()
needle='''                } else {
                    assertEquals("unit $number", digest, V2Validator.unitContentReviewDigest(pack, number))
                }
            } else {'''
replacement='''                } else if (number == 13) {
                    // Retain the exact old v2 pin; independently guard the actual current v3 unit.
                    RecordedCurriculumConservationScope.beforeUnit13PresentReview(root, pack).let { pack ->
                        assertEquals("unit $number", digest, V2Validator.unitContentReviewDigest(pack, number))
                    }
                    RecordedIntakeCorrectionScope.assertCurrentUnitMatches(pack,
                        RecordedIntakeCorrectionScope.load(root, RecordedCurriculumConservationScope.currentPackImplementation), number)
                    assertNotEquals("Current Unit13 v3 review identity", digest,
                        V2Validator.unitContentReviewDigest(pack, number))
                } else {
                    assertEquals("unit $number", digest, V2Validator.unitContentReviewDigest(pack, number))
                }
            } else {'''
assert t.count(needle)==1;p.write_text(t.replace(needle,replacement))
from assertion_calls import assertions
reviews=[]
for n in files:
    before=blob(base,n).decode();after=(R/n).read_text();a=assertions(before);b=assertions(after);j=0
    for x in b:
        if j<len(a) and x==a[j]:j+=1
    assert j==len(a),(n,j,len(a))
    assert re.findall(r'@Test\b',before)==re.findall(r'@Test\b',after)
    reviews.append(dict(path=n,originalSha256=sha(before.encode()),newSha256=sha(after.encode()),originalAssertionInvocations=len(a),newAssertionInvocations=len(b),everyOriginalAssertionPreservedInOrder=True,noTestMethodAddedOrRemoved=True))
save(D/'COMPARISON_REVIEW.json',reviews)
for n,h in originalHashes.items():
    if n not in files:assert sha((R/n).read_bytes())==h,n
for n,h in prior['currentRecordHashes'].items():assert sha((R/n).read_bytes())==h,n
for n,h in prior['archives'].items():
    assert sha((R/n).read_bytes())==h
    with zipfile.ZipFile(R/n) as z:assert z.testzip() is None
subprocess.run(['git','add',*files],cwd=R,check=True)
indexTree=git('write-tree')
trees={n:git('rev-parse',indexTree+':'+n) for n in ['app/src/main','app/src/test','catalog','docs','review','release']}
currentNames={'Unit13PresentReviewBaselineTest','Unit13PresentCompatibilityTest','Unit13PresentReviewUiTest','Unit13BindingConservationTest'}
with zipfile.ZipFile(P/'INTERRUPTED_RAW.zip') as z:discovery=json.loads(z.read('ORIGINAL_WORKSPACE/DISCOVERY/RESULT.json'))
tests=[t for t in discovery['tests'] if t['testClass'].split('.')[-1] in set(names)|currentNames]
assert len(tests)==63 and len({t['testClass'] for t in tests})==9
scope=dict(selectors=sorted({t['testClass'] for t in tests}),expectedMethods=[dict(testClass=t['testClass'],method=t['method']) for t in tests],methodCount=63,classes=9,inputTrees=trees,fullClasses=True,inventoryOnlyUntilFreshDiscovery=True,priorFailedIdentities=[dict(testClass=e['className'],method=e['name']) for e in json.loads((P/'INTERRUPTED_FAILURES.json').read_text())['failures']],completeSuite=False)
save(W/'SCOPE.json',scope);save(D/'SCOPE.json',scope)
prep=dict(parentCommit=base,productionTree=trees['app/src/main'],preparedInputTrees=trees,existingTestFiles=originalHashes,changedComparisonFiles=files,comparisonReview=reviews,currentRecordHashes=prior['currentRecordHashes'],archives=prior['archives'],repositorySourceDigest=prior['repositorySourceDigest'],packContentReviewDigest=prior['packContentReviewDigest'],plannedClasses=9,methodCount=63,originalAssertionInvocations=sum(x['originalAssertionInvocations'] for x in reviews),newAssertionInvocations=sum(x['newAssertionInvocations'] for x in reviews),otherTestFilesUnchanged=len(originalHashes)-5,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST')
save(W/'PREPARATION.json',prep);save(D/'PREPARATION.json',prep)
save(D/'FAILURE_RECONCILIATION.json',dict(originalEvidenceDirectory=P.relative_to(R).as_posix(),originalRuntimeSource='75e2d31c441e87e7f53c550b2bf34e9804737bfb',originalInterruptedPartialEvents=316,originalPartialCounts=dict(SUCCESS=309,FAILURE=6,SKIPPED=1),separateContinuationSource='3248ab597dc13e8746464ba7601cf29e440995fa',separateContinuationReported=165,separateContinuationPassed=165,scopesNotSummed=True,originalFailuresUnchanged=True,changes=[dict(testClass='Unit13CumulativeReviewTest',methods=[scope['priorFailedIdentities'][i]['method'] for i in [0,1]],scope='Only these two original v2 behavioral methods use strict beforeUnit13PresentReview; all other methods and shared pack remain actual current',currentControls='Full Unit13PresentCompatibilityTest and Unit13PresentReviewUiTest, unmodified'),dict(testClass='Unit13PastTeachingTest and Unit13ContextTeachingTest',scope='Only original whole-unit equality is inside strict before13 scope; original teaching/items/examples/provenance/capabilities/validator checks stay actual current',newCurrentControl='Exact actual Unit13 match against original v3 producer bd00e508'),dict(testClass='Units1620SourceIntakeTest and Units2630SourceIntakeTest',scope='Only Unit13 original v2 digest assertion uses strict before13; original literal digest preserved; all other unit and rights/admission/release controls unchanged',newCurrentControl='Exact current Unit13 identity/source guard plus distinct current-v3 digest assertion')],linguisticOrRightsApproval=False,externalAuditFindingsClosed=0))
total=prep['originalAssertionInvocations']
(D/'STATUS.md').write_text(f'''# Unit13 historical comparisons reconciled; full-class follow-up prepared

Parent `{base}`. Five archived original files; all **{total} original assertion/helper invocations retained in order**, original test methods and literal pins retained. No production change. Two original v2 behavioral methods use the strict full-pack before13 scope; only the whole-unit equality in two teaching methods is scoped, while all subsequent current teaching/item/provenance/example/capability/validator checks stay current. Two Unit13 old digest checks retain their actual v2 pin on the proven source and independently assert exact current-v3 source/identity and distinct digest. No shared pack helper changed, no current control scoped backwards. POLICY_ONLY_ASSET_PROOF.json proves the current raw asset differs from its actual prior producer only by one policy ID. Existing full-pack reconstruction helper is unchanged.

The original interrupted run at75e2d31c remains316partial events/309SUCCESS/6FAILURE/1SKIPPED with no exit/final result. Its six full failures remain immutable. The completed separate165method continuation at3248ab59 passed165/165/exit0. These are separate scopes,not one completed481run;no counts summed. Fresh follow-up inventory63methods/9fullclasses includes every six failed identity plus all19existing current Unit13 baseline/compatibility/UI/binding controls. Inventory is not execution;actual discovery/runtime required.

Production/{prep['otherTestFilesUnchanged']}other test files/whole original baseline/current core controls/155bindings/74historical archives conserved. Original two Unit12 comparison corrections remain byte-exact. Four screenshot exporters16methods excluded:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. No APKassembly/install/signing;APK identity/signature absent. Repositorysource3c0acbb95297cf10f08da2bd8391463067eece22fec785158b5d2680d37dc2ba differs from packcontent15c6ae393767cb7ffc190882867bfad923c9e81b07d0f4285cf0a678f2489280.

One agent/current checkout/DraftPR64. Inspect external unit13-historical-comparison-followup-2026-10-27 contexts/processes/completions/monitor/AUTO_RESULT or ERROR before launch/edit. Exclusive completion worker owns evidence/audit-docs-only commit/normalpush/DraftPR64 update. Do not duplicate active jobs or alter active source inputs. All48findingsOPEN/auditINCOMPLETE/physicalGalaxyS26Ultra acceptanceLAST after final independent comprehensive nonphysical audit. No merge/freeze/formal advancement/releaseapproval. PrivatePDF unread/unpackaged/environmentsettings unchanged/publication user-confirmed independently unobserved/JITdirective unadopted/nativecauseUNCONFIRMED. Qualifiedreviews/CC024risk/19print-singletons/CC048authority/originalUnit7restorecause outstanding.
''')
rel=D.relative_to(R).as_posix()
for n in ['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']:
    p=R/n;link=rel+'/STATUS.md' if n=='AGENTS.md' else '../'+rel.removeprefix('audit/')+'/STATUS.md'
    p.write_text(f'Current checkpoint: [Unit13 historical comparisons reconciled; follow-up prepared]({link}). Five files/all{total}originalassertions/pins retained;only proven oldv2assumptions scoped/currentcontrols stay current.63method/9fullclass inventory only;inspect external unit13-historical-comparison-followup-2026-10-27 before launch/edit. Original316interrupted/165completed scopes preserved/separate/unsummed;155bindings/74archives/271other tests conserved. All48OPEN/auditINCOMPLETE/four exporters excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
print(json.dumps(dict(originalAssertions=total,newAssertions=prep['newAssertionInvocations'],files=5,otherTestFilesConserved=prep['otherTestFilesUnchanged'],plannedMethods=63,plannedFullClasses=9,productionUnchanged=True),indent=2))
