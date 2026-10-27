from pathlib import Path
import json,hashlib,subprocess,zipfile,re,shutil
R=Path('/workspace/russian-companion');W=Path(__file__).parent;B=R/'audit/gpt61-sol-high-comprehensive-2026-10-19';D=B/'engineering-unit13-source-reconciliation-2026-10-27';D.mkdir()
base='aa93df85b4290406d5b78b0035b4a77912ae25fe';S='bd00e5082c9f31d2ae3b379dd6204dd3b28e4475';G=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip();sha=lambda b:hashlib.sha256(b).hexdigest();assert G('rev-parse','HEAD')==base and not G('status','--porcelain')
paths=['app/src/test/java/dev/russiancompanion/v2/RecordedCurriculumConservationScope.kt','app/src/test/java/dev/russiancompanion/v2/review/Unit1ReviewPackage.kt','app/src/test/resources/reviewed-curriculum-cyrillic.json']
with zipfile.ZipFile(D/'ORIGINAL_TOOLING.zip','x',zipfile.ZIP_DEFLATED) as z:
 for n in paths:z.write(R/n,n)
p=R/paths[0];t=p.read_text();t=t.replace('const val currentPackImplementation = "ca0fdaeca8de81ab6a5d4935fbcdcb068c4af913"',f'const val beforeUnit13PackImplementation = "ca0fdaeca8de81ab6a5d4935fbcdcb068c4af913"\n    const val currentPackImplementation = "{S}"')
t=t.replace('        12 to currentPackImplementation,','        12 to beforeUnit13PackImplementation,\n        13 to currentPackImplementation,')
needle='    /** Restore only Unit12\'s declaration;';assert needle in t
t=t.replace(needle,'''    /** Restore only Unit13's v2 declaration; every other field must match its actual producer. */
    fun beforeUnit13PresentReview(root: File, current: V2CurriculumPack): V2CurriculumPack {
        assertCurrent(root, current)
        val prior = current.copy(units = current.units.map { unit ->
            if (unit.number == 13) unit.copy(advancement = unit.advancement!!.copy(
                cumulativeReviewPolicyId = Unit13WiderCumulativeReviewPolicy.definition.id)) else unit
        })
        assertEquals("Only the Unit13 policy declaration may differ from its actual v2 asset", source(root, beforeUnit13PackImplementation), prior)
        return prior
    }

'''+needle)
a='''        val prior = current.copy(units = current.units.map { unit ->
            if (unit.number == 12)''';b='''        val beforeUnit13 = beforeUnit13PresentReview(root, current)
        val prior = beforeUnit13.copy(units = beforeUnit13.units.map { unit ->
            if (unit.number == 12)''';assert t.count(a)==1;t=t.replace(a,b)
t=t.replace('                    12 -> beforeUnit12GenderReview(root, current)','                    12 -> beforeUnit12GenderReview(root, current)\n                    13 -> beforeUnit13PresentReview(root, current)')
needle='''                if (number == 12) {''';idx=t.index(needle);end=t.index('\n                }',idx)+len('\n                }');t=t[:end]+'''
                if (number == 13) {
                    RecordedIntakeCorrectionScope.assertCurrentUnitMatches(current, source(root, currentUnitSources.getValue(number)), number)
                    assertNotEquals("Current Unit13 v3 must retain its own identity", digest,
                        V2Validator.unitContentReviewDigest(current, number))
                }'''+t[end:];p.write_text(t)
p=R/paths[1];t=p.read_text();needle='internal fun curriculumCommitForUnit';assert t.count(needle)==1;t=t.replace(needle,f'// Actual verified Unit13 supplied-present/aspect/past core; older sources stay historical.\ninternal const val UNIT13_PRESENT_REVIEW_CURRICULUM_COMMIT = "{S}"\n\n'+needle).replace('else if (unitNumber == 13) UNIT13_WIDER_REVIEW_CURRICULUM_COMMIT','else if (unitNumber == 13) UNIT13_PRESENT_REVIEW_CURRICULUM_COMMIT');p.write_text(t)
p=R/paths[2];oldtext=p.read_text();data=json.loads(oldtext);kernel='src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt';entry=next(x for x in data['files'] if x['path']==kernel);oldhash=entry['sha256'];newhash=sha((R/('app/'+kernel)).read_bytes());assert oldhash!=newhash
pos=oldtext.index('"sha256": "'+oldhash+'"');t=oldtext[:pos]+oldtext[pos:].replace('"sha256": "'+oldhash+'"','"sha256": "'+newhash+'"',1)
record=dict(path=kernel,sourceCommit=S,previousRecordedSha256=oldhash,sha256=newhash,additionalExistingItemReferences=[],allExistingBindingsUnchanged=True,scope='Reuses existing supplied present/aspect-partner/past-gender pools; separate Unit13v3/schema15, exact directional v1/v2 and owner13 bridges, English labels only. No new Cyrillic literal,item reference or language construction; no semantic/rights acceptance.',semanticAcceptance=False,rightsAcceptance=False)
assert t.endswith('\n  ]\n}\n');body=json.dumps(record,indent=2,ensure_ascii=False);indented='\n'.join('    '+line for line in body.splitlines());t=t[:-len('\n  ]\n}\n')]+',\n'+indented+'\n  ]\n}\n';p.write_text(t)
now=json.loads(t);assert now['subsequentEngineeringReferenceReviews'][:-1]==data['subsequentEngineeringReferenceReviews'];assert len(now['files'])==19 and sum(len(x['bindings']) for x in now['files'])==292
# New current guards use the same original assertion structure, now for Unit13's actual transition.
q=R/'app/src/test/java/dev/russiancompanion/v2/Unit13BindingConservationTest.kt';u=(R/'app/src/test/java/dev/russiancompanion/v2/Unit12BindingConservationTest.kt').read_text()
u=u.replace('Unit12Binding','Unit13Binding').replace('ReconstructsOnlyUnit12','ReconstructsOnlyUnit13').replace('beforeUnit12GenderReview(root, pack)','beforeUnit13PresentReview(root, pack)').replace('before12','before13').replace('"23b9179eaf54844f0b38074b2db56c7113c38c00"','"31ac807387253082c44bd2f6d67021478f0df66e"').replace('Unit12CumulativeReviewPolicy','Unit13WiderCumulativeReviewPolicy').replace('before13.unit(12)','before13.unit(13)').replace('it.number != 12','it.number != 13').replace('before13, 12','before13, 13').replace('pack, 12','pack, 13').replace('u.number != 12','u.number != 13').replace('7e4d3ee4652194f88a4804f1734cf8171efae714',base).replace('ca0fdaeca8de81ab6a5d4935fbcdcb068c4af913',S)
# Retain an explicit genuine Unit12 transition proof within the new chain.
needle='        val before11 =';u=u.replace(needle,'        assertEquals(RecordedIntakeCorrectionScope.load(root, "23b9179eaf54844f0b38074b2db56c7113c38c00"),\n            RecordedCurriculumConservationScope.beforeUnit12GenderReview(root, pack))\n'+needle,1);q.write_text(u)
core=B/'engineering-unit13-present-correction-2026-10-27';protected=json.loads((core/'PROTECTED_EVIDENCE_HASHES.json').read_text());protected={n:h for n,h in protected.items() if n not in paths};archives=json.loads((core/'PRIOR_ARCHIVE_HASHES.json').read_text());records=json.loads((core/'SOURCE_BINDING_HASHES.json').read_text())
oldtests={n:sha(subprocess.check_output(['git','show',base+':'+n],cwd=R)) for n in G('ls-tree','-r','--name-only',base,'app/src/test').splitlines()};assert len(oldtests)==275
con=dict(parentCommit=base,actualVerifiedProductionSource=S,unchangedProductionTree=G('rev-parse',base+':app/src/main'),changedToolingFiles=paths,originalToolingHashes={n:sha(subprocess.check_output(['git','show',base+':'+n],cwd=R)) for n in paths},allPriorTestHashes=oldtests,immutablePriorEvidence=protected,archives=archives,currentRecordHashes=records,oldSourceDigest='84b6fda72017ec2dfc2b4add6c0e82b1a1e9f40769773f18d50caee4cf7a2355',newSourceDigest='15c6ae393767cb7ffc190882867bfad923c9e81b07d0f4285cf0a678f2489280',sourceBindingsStatus='STALE_UNCHANGED',all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST')
(D/'CONSERVATION.json').write_text(json.dumps(con,indent=2)+'\n')
for n in ['compile_tooling.py','generate.py','review_proposal.py']:
 source=Path('/workspace/artifacts/unit12-source-reconciliation-2026-10-27')/n;t=source.read_text()
 if n=='review_proposal.py':t=t.replace('review/unit-12/','review/unit-13/').replace("/units/11/","/units/12/").replace("/curriculumSourceCommits/12","/curriculumSourceCommits/13").replace("/unitDispositions/11/","/unitDispositions/12/")
 (W/n).write_text(t);(D/n).write_text(t)
(D/'STATUS.md').write_text('''# Unit13 source tooling prepared; generation pending\n\nStrict before13→12→11→10→9→8→7→6→5 whole-pack proofs preserve old assertions/pins. Only Unit13 effective generator choice changes to actual verified production bd00e508;29other choices stay exact. Kernel reference updates only its current hash and appends pending engineering identity, with19files/292bindings and historical entries unchanged; no semantic/rights acceptance. Original three tooling files archived. New two-method binding class is inventory only.275prior test files conserved except the three explicitly reviewed tooling/reference changes; whole baseline/core assertions exact.155bindings STALE_UNCHANGED.\n\nExternal path /workspace/artifacts/unit13-source-reconciliation-2026-10-27. Compile tooling, authenticate actual compiled digest, render twice into external directories, independently compare all153outputs/30packages and all155proposed records before applying only identity deltas. Preserve every historical prior version; no reviewer/owner/rights/mapping/admission/approval fields may change. Actual sizes/deltas must be measured, not inferred. No JUnit executed yet in this phase.\n\nPrior bd00e508 core17/17selected methods/3classes passed; original2562b845baseline1pass/1intentionalfailure and prior44cb3e66broad1458reported/1457pass/1physicalskip remain separate/unsummed. All48OPEN/auditINCOMPLETE/four screenshot exporters16methods excluded/physicalLAST after final independent comprehensive nonphysical audit. No APKassembly/install/signing/merge/freeze/formal advancement/releaseapproval. Qualified reviews/CC024risk/CC048authority/originalUnit7restorecause outstanding. PrivatePDF unread/unpackaged/environmentunchanged/publication user-confirmed independently unobserved/JITdirective provisional/unadopted/nativecauseUNCONFIRMED.\n''')
(D/'NEXT_TASK.md').write_text('''# Authenticate renders then affected verification\n\nInspect external unit13-source-reconciliation-2026-10-27 contexts/processes/completions before launch/edit. Compile fresh tooling, prove the actual official digest matches verified bd00e508, perform two actual compiled renders validating30packages, compare output bytes and identity-only deltas, preserve historical records before application. No new source/semantic/rights approval. Then fresh affected discovery/runtime preserving every failure; reconcile historical comparisons only through exact prior scopes while keeping current controls current; fresh broad comes after affected verification. All48open/physicalLAST/four exporters excluded/no APK/signing/merge/freeze/releaseapproval.\n''')
rel=D.relative_to(R).as_posix()
for n in ['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']:
 p=R/n;link=rel+'/STATUS.md' if n=='AGENTS.md' else '../'+rel.removeprefix('audit/')+'/STATUS.md';p.write_text(f'Current checkpoint: [Unit13 source tooling prepared; generation pending]({link}). Strict13→12→11→10→9→8→7→6→5proofs/currentguard/newsource13/pendingkernelreference. Two new methods inventory only;155bindingsSTALE_UNCHANGED. Wholebaseline/coreassertions/74archives conserved. Inspect external unit13-source-reconciliation-2026-10-27 before launch/edit. All48OPEN/auditINCOMPLETE/four exporters excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
print(json.dumps({'prepared':paths,'newBindingMethodInventory':2,'originalFilesArchived':3,'sourceBindings':'155_STALE_UNCHANGED'}))
