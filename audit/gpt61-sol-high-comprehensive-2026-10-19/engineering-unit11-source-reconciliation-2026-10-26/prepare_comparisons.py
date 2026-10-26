from pathlib import Path
import json,hashlib,subprocess,re
R=Path('/workspace/russian-companion');W=Path(__file__).parent;sha=lambda b:hashlib.sha256(b).hexdigest();G=lambda *a:subprocess.check_output(['git',*a],cwd=R);base='app/src/test/java/dev/russiancompanion/v2/';head=G('rev-parse','HEAD').decode().strip();assert head=='6dd46bd664fdd93e917228d71f50c42b395d196e' and not G('status','--porcelain').strip();rows=[]
def save(name,old,new,exceptions=[]):
 assert old!=new
 a=[x.strip() for x in old.splitlines() if x.strip() and x.strip() not in exceptions];b=iter(x.strip() for x in new.splitlines() if x.strip());assert all(any(y==x for y in b) for x in a),name
 (W/('PREPARED_'+name)).write_text(new);rows.append(dict(path=base+name,originalSha256=sha(old.encode()),preparedSha256=sha(new.encode()),allOriginalNonblankLinesInOrderExcept=exceptions,allOriginalAssertionsAndPinsRetained=True))
name='Unit10BindingConservationTest.kt';s=(R/base/name).read_text();t=s
line='        assertEquals(pack.units.filter { it.number != 10 }, before10.units.filter { it.number != 10 })';assert t.count(line)==1;t=t.replace(line,'        // This original equality describes the Unit10-only transition before Unit11 v2.\n        RecordedCurriculumConservationScope.beforeUnit11NounReview(root, pack).let { pack ->\n    '+line+'\n        }')
a='        val now = Json.parseToJsonElement(File(root, path).readText()).jsonObject';b='        val now = Json.parseToJsonElement(historicalReferenceBytes(path).decodeToString()).jsonObject';c='                val actual = MessageDigest.getInstance("SHA-256").digest(File(root, "app/$name").readBytes()).joinToString("") { "%02x".format(it) }';d='                val actual = MessageDigest.getInstance("SHA-256").digest(historicalReferenceBytes("app/$name")).joinToString("") { "%02x".format(it) }';assert t.count(a)==t.count(c)==1;t=t.replace(a,b).replace(c,d);idx=t.rfind('\n}');t=t[:idx]+'''
    // Genuine matching Unit10 reference/kernel pair after its verified broad checkpoint.
    // Actual-current hash/append is independently checked by Unit11BindingConservationTest.
    private fun historicalReferenceBytes(path: String): ByteArray {
        val process = ProcessBuilder("git", "show", "1d96cb4e89768f69246960e263d9b51412a3b9f4:$path")
            .directory(root).start()
        val bytes = process.inputStream.readBytes()
        assertEquals("Historical Unit10 reference input $path", 0, process.waitFor())
        return bytes
    }
'''+t[idx:];save(name,s,t,[a.strip(),c.strip()])
name='Unit10PresentCompatibilityTest.kt';s=(R/base/name).read_text();t=s;start=t.index('        val priorDeclarationOnly =');end=t.index('\n        LocalStore.openInMemory',start);block=t[start:end];assert block.count('assertEquals(')==1;t=t[:start]+'        // Keep the original whole-pack Unit10 declaration proof before Unit11 v2.\n        RecordedCurriculumConservationScope.beforeUnit11NounReview(root, pack).let { pack ->\n'+''.join('    '+l+'\n' for l in block.splitlines())+'        }'+t[end:];save(name,s,t)
name='Unit11AgreementRemediationTest.kt';s=(R/base/name).read_text();t=s;line='        assertEquals(p.unit(11), live.unit(11))';assert t.count(line)==1;t=t.replace(line,'        // Exact instructional correction predates the declaration-only Unit11 review change.\n        RecordedCurriculumConservationScope.beforeUnit11NounReview(root(), live).let { live ->\n    '+line+'\n        }\n        assertEquals(Unit11NounCumulativeReviewPolicy.definition.id, live.unit(11)!!.advancement!!.cumulativeReviewPolicyId)\n        assertNotEquals(V2Validator.unitContentReviewDigest(p, 11), V2Validator.unitContentReviewDigest(live, 11))');save(name,s,t)
name='Unit11CumulativeReviewTest.kt';s=(R/base/name).read_text();t=s;start=t.index('            assertEquals(listOf("obj-u1-decoding"');end=t.index('\n            assertTrue(completedReviewSlots',start);block=t[start:end];assert block.count('assertEquals(')==3
before='''            // Preserve original v1 slot assertions against a real prior-declaration task.
            val prior = RecordedCurriculumConservationScope.beforeUnit11NounReview(root(), p)
            createCumulativeReviewAssignment(prior, s.v2LearnerView(), 11, "historical-unit11-v1", 0)!!.let { a ->
                assertEquals(1, a.schema)
''';after='''            }
            assertEquals(13, a.schema)
            assertEquals(Unit11NounCumulativeReviewPolicy.definition.id, a.policyId)
            assertEquals(listOf("obj-u5-indeclinable", "obj-u10-have"), a.slots.map { it.objectiveId })
            assertEquals(listOf("SUPPLIED_NOUN_BEHAVIOR_RECOGNITION", "SUPPLIED_POSSESSION_ABSENCE_RECOGNITION"), a.slots.map { it.skill })
            assertTrue(a.slots[0].itemId in Unit11NounCumulativeReviewPolicy.definition.pool[0].candidateItemIds)
            assertTrue(a.slots[1].itemId in Unit11NounCumulativeReviewPolicy.definition.pool[1].candidateItemIds)'''
t=t[:start]+before+''.join('    '+l+'\n' for l in block.splitlines())+after+t[end:]
for method in ['actualUnit11ContinuePossessionAbsenceCueTeachingPracticeReturnAndAdvanceStayOnUnit11','placementWithTaughtUnit1ButUntaughtUnit10RepairsTheMissingOwnerInsteadOfUnit1']:
 start=t.index('    @Test fun '+method+'() {');end=t.index('\n    @Test fun ',start+1);chunk=t[start:end];needle='        val p = pack()\n';a=chunk.index(needle)+len(needle);b=chunk.rfind('\n    }');assert a<b
 chunk=chunk[:a]+'''        // Historical v1 reading/absence obligation; actual-current noun/possession paths
        // and both owner5/10 routes are covered by Unit11NounReviewUiTest.
        RecordedCurriculumConservationScope.beforeUnit11NounReview(root(), p).let { p ->
'''+''.join('    '+l+'\n' for l in chunk[a:b].splitlines())+'        }'+chunk[b:];t=t[:start]+chunk+t[end:]
save(name,s,t)
name='Units2630SourceIntakeTest.kt';s=(R/base/name).read_text();t=s;anchor='''                } else {
                    assertEquals("unit $number", digest, V2Validator.unitContentReviewDigest(pack, number))
                }
            } else {''';assert t.count(anchor)==1;new='''                } else if (number == 11) {
                    // The original digest names the exact prior reading/absence declaration.
                    RecordedCurriculumConservationScope.beforeUnit11NounReview(root, pack).let { pack ->
                        assertEquals("unit $number", digest, V2Validator.unitContentReviewDigest(pack, number))
                    }
                    RecordedIntakeCorrectionScope.assertCurrentUnitMatches(pack,
                        RecordedIntakeCorrectionScope.load(root, RecordedCurriculumConservationScope.currentPackImplementation), number)
                    assertNotEquals("Current versioned review identity for Unit $number", digest,
                        V2Validator.unitContentReviewDigest(pack, number))
'''+anchor;t=t.replace(anchor,new);save(name,s,t)
# Actual prior raw pack -> actual new pack must be one declaration token only.
p='app/src/main/assets/content/v2-curriculum.json';old=G('show','745234c99e3d69fadca835ba7f8eb98ad1a8ab48:'+p);new=G('show','c2d297e79fa677aa1375917cfbe052e4c0806633:'+p);a=b'unit11-prior-reading-possession-absence-v1';b=b'unit11-prior-noun-behavior-possession-absence-v2';assert old.count(a)==new.count(b)==1 and old.replace(a,b)==new
# Verify authentic old reference + kernel, not a projected pair.
refpath='app/src/test/resources/reviewed-curriculum-cyrillic.json';kernel='app/src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt';hist='1d96cb4e89768f69246960e263d9b51412a3b9f4';ref=G('show',hist+':'+refpath);k=G('show',hist+':'+kernel);j=json.loads(ref);entry=next(x for x in j['files'] if x['path']=='src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt');assert entry['sha256']==sha(k) and len(j['subsequentEngineeringReferenceReviews'])==5
(W/'HISTORICAL_COMPARISON_PREPARATION.json').write_text(json.dumps(dict(sourceBeforeEdit=head,applied=False,originalFailureScope='395/386pass/8fail/1physicalskip;71classes;exit1',files=rows,rawSourceProof=dict(oldSource='745234c99e3d69fadca835ba7f8eb98ad1a8ab48',newSource='c2d297e79fa677aa1375917cfbe052e4c0806633',oldSha256=sha(old),newSha256=sha(new),oldToken=a.decode(),newToken=b.decode(),oneTokenOnlyEveryOtherByteExact=True),genuineReferencePair=dict(commit=hist,referencePath=refpath,referenceSha256=sha(ref),kernelPath=kernel,kernelSha256=sha(k),recordedKernelHashMatches=True,pendingEngineeringReviews=5),allOriginalAssertionsAndPinsRetained=True,twoNonassertInputExpressionsChanged=True,productionAndCurrentBindingsUnchanged=True,all48Findings='OPEN',completeSuite=False),indent=2)+'\n');print('Prepared five test files; all original assertion lines/pins retained; not applied.')
