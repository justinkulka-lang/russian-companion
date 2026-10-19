from pathlib import Path
import json,re,hashlib,subprocess

BASE=Path('audit/independent-gpt61-2026-10-12')
assert (BASE/'current-broad-conservation-followup/INITIAL/MANIFEST.json').exists(), 'Wait for active collector'
OUT=BASE/'remaining-broad-fixture-followup'
OUT.mkdir()
git=lambda *a:subprocess.check_output(['git',*a],text=True).strip()
sha=lambda b:hashlib.sha256(b).hexdigest()
write=lambda p,d:p.write_text(json.dumps(d,indent=2)+'\n')
source=git('rev-parse','HEAD'); changes={}; selected={}
def edit(cls,names,fn):
    p=Path('app/src/test/java/dev/russiancompanion/v2')/(cls+'.kt')
    before=p.read_text(); after=fn(before)
    assert before!=after,cls
    p.write_text(after)
    changes[cls]={'beforeSha256':sha(before.encode()),'afterSha256':sha(after.encode()),'changedMethods':names,'originalFileGitBlob':git('rev-parse',source+':'+str(p))}
    selected['dev.russiancompanion.v2.'+cls.replace('/','.')]=names

def method_edit(text,name,fn):
    m=re.search(r'fun '+re.escape(name)+r'\(',text); assert m,name
    end=text.index('\n    }',m.end())+len('\n    }')
    old=text[m.start():end]; new=fn(old); assert old!=new,name
    return text[:m.start()]+new+text[end:]

def ordinary_positive(method,var,full_message):
    # Duplicate the exact original call/fixture for the full refusal proof.
    start=method.index('val '+var+' = advancementDecision(')
    call_start=method.index('advancementDecision(',start)
    i=call_start+len('advancementDecision('); depth=1
    while depth:
        if method[i]=='(':depth+=1
        elif method[i]==')':depth-=1
        i+=1
    full_call=method[call_start:i]
    method=method[:call_start]+full_call.replace('advancementDecision(', 'dev.russiancompanion.v2.curriculum.ordinaryUnitRequirements(',1)+method[i:]
    marker='assertTrue('+var+'.remaining.joinToString("\\n"), '+var+'.allowed)'
    if marker not in method:
        marker='assertTrue("$number $readiness ${decision.remaining}", decision.allowed)'
    assert marker in method,(var,method)
    extra=marker+'\n            val '+var+'Full = '+full_call+'\n            assertFalse('+var+'Full.allowed)\n            assertEquals(listOf("'+full_message+'"), '+var+'Full.remaining)'
    return method.replace(marker,extra,1)

review='Complete the assigned earlier-objective review'
unverifiable='The cumulative-review rule cannot be verified'
edit('Units2125SourceIntakeTest', ['unsupportedObjectivesCannotAcquireAdvancementChecksOrContentCompleteStatus','unitTwentyThreeElapsedTimeStaysReviewPendingAndPartialEvidenceStaysClosed'], lambda t:method_edit(method_edit(t,'unsupportedObjectivesCannotAcquireAdvancementChecksOrContentCompleteStatus',lambda s:ordinary_positive(s,'decision',review)),'unitTwentyThreeElapsedTimeStaysReviewPendingAndPartialEvidenceStaysClosed',lambda s:ordinary_positive(s,'advanced',review)))
edit('Units2630SourceIntakeTest',['neighboringSpansKeywordsAndInactiveVariancesStayClosed'],lambda t:method_edit(t,'neighboringSpansKeywordsAndInactiveVariancesStayClosed',lambda s:ordinary_positive(s,'advanced',review)))
edit('Units2630StructuralMappingTest',['pageRangeBookAttestationAndEmptyContentCannotAdvance','preparedLateUnitIsNotPermanentlyBlockedFromAdvancement'],lambda t:method_edit(method_edit(t,'pageRangeBookAttestationAndEmptyContentCannotAdvance',lambda s:ordinary_positive(s,'rangedDecision',unverifiable)),'preparedLateUnitIsNotPermanentlyBlockedFromAdvancement',lambda s:ordinary_positive(s,'decision',unverifiable)))

def readiness(t):
    def adjust(s):
        s=s.replace('= advancementDecision(', '= dev.russiancompanion.v2.curriculum.ordinaryUnitRequirements(')
        s=s.replace('assertTrue(liveModel().units.first', 'assertFalse(liveModel().units.first').replace('assertTrue(row.advancementCanPresentlySucceed)','assertFalse(row.advancementCanPresentlySucceed)')
        marker='        val early = pack.unit(1).copy(readiness = "NOT_PREPARED")'
        extra='''        // Ordinary source/readiness completion is not current assigned-review evidence.
        listOf(7, 16, 17, 26, 27, 30).forEach { number ->
            val current = pack.unit(number)
            val full = advancementDecision(pack, current, idealizedLearner(pack, current))
            assertFalse(full.allowed)
            assertEquals(listOf("Complete the assigned earlier-objective review"), full.remaining)
        }
'''+marker
        assert marker in s;s=s.replace(marker,extra)
        return s
    return method_edit(t,'notPreparedUnitsCannotAdvanceAndReadinessChangeDoesNotClearOtherBlockers',adjust)
edit('audit/CurriculumReadinessReconciliationTest',['notPreparedUnitsCannotAdvanceAndReadinessChangeDoesNotClearOtherBlockers'],readiness)

def objective_skip(t):
    # This private helper is used by the single selected method, for Units3/5.
    needle='''        assertTrue(blocked.remaining.joinToString(), blocked.remaining.any { it.contains(skipped.title) })'''
    assert t.count(needle)==1
    t=t.replace(needle,needle+'''
        val ordinaryBlocked = dev.russiancompanion.v2.curriculum.ordinaryUnitRequirements(pack, unit, learner)
        assertFalse(ordinaryBlocked.allowed)
        assertTrue(ordinaryBlocked.remaining.any { it.contains(skipped.title) })''')
    start=t.index('    private fun assertObjectiveSkipBlocks(')
    end=t.index('\n    }',start)+len('\n    }');old=t[start:end]
    needle='''        val ready = advancementDecision(pack, unit, store.v2LearnerView())
        assertTrue(ready.remaining.joinToString(), ready.allowed)'''
    assert needle in old
    new=old.replace(needle,'''        val ready = dev.russiancompanion.v2.curriculum.ordinaryUnitRequirements(pack, unit, store.v2LearnerView())
        assertTrue(ready.remaining.joinToString(), ready.allowed)
        val full = advancementDecision(pack, unit, store.v2LearnerView())
        assertFalse(full.allowed)
        assertEquals(listOf("Complete the assigned earlier-objective review"), full.remaining)''')
    return t[:start]+new+t[end:]
edit('V2RebuildTest',['advancementCannotSkipARequiredObjective'],objective_skip)

def boundaries(t):
    def shared(s):
        assert '"u8-i-availability-2" to "RULE_CLASSIFIES_SOURCE_EXAMPLE"' in s
        return s.replace('"u8-i-availability-2" to "RULE_CLASSIFIES_SOURCE_EXAMPLE"','"u8-i-availability-2" to "STATED_APPLICATION"')
    t=method_edit(t,'sameTruncatedSourceRecordIdWithDifferentFrozenContentCannotShareASpan',shared)
    def labels(s):
        for old,new in [('1 -> nominative','In this basic counting phrase, the noun is nominative singular.'),('2, 3, 4 -> genitive singular','In this basic counting phrase, the noun is genitive singular.'),('5 and more -> genitive plural','In this basic counting phrase, the noun is genitive plural.')]:
            assert old in s;s=s.replace('"'+old+'"','"'+new+'"')
        assert 'Boundary("ODIN_MASCULINE", "мальчик", "один")' in s
        s=s.replace('Boundary("ODIN_MASCULINE", "мальчик", "один")','Boundary("ODIN_MASCULINE", "час", "один")')
        assert 'it.answer.contains("must be declined")' in s
        return s.replace('it.answer.contains("must be declined")','it.answer.contains("both numeral and noun are declined")')
    return method_edit(t,'unitNineBoundaryCasesAreIndependentQualifyingChecks',labels)
edit('Units610InstructionalValidityTest',['sameTruncatedSourceRecordIdWithDifferentFrozenContentCannotShareASpan','unitNineBoundaryCasesAreIndependentQualifyingChecks'],boundaries)

def unit4(t):
    name='allPriorExamplesRecordsCapabilitiesAndOtherUnitsRemainWhole'
    start=t.index(' @Test fun '+name+'() {');end=t.index('\n }',start)+3;old=t[start:end]
    assert 'val p=pack();val old=baseline();' in old
    new=old.replace('val p=pack();val old=baseline();','val p=historicalConservationPack();val old=baseline();',1)
    extra='''
  val live=pack();assertFalse(p==live);assertEquals(p.unit(4),live.unit(4))
  val ids=p.unit(4)!!.objectives.map{it.id}.toSet()
  assertEquals(p.items.filter{it.objectiveId in ids},live.items.filter{it.objectiveId in ids})
  val exampleIds=(p.unit(4)!!.objectives.flatMap{it.exampleIds}+p.unit(4)!!.sessions.flatMap{it.steps}.flatMap{it.exampleIds}).toSet()
  assertEquals(p.examples.filter{it.id in exampleIds},live.examples.filter{it.id in exampleIds})
  assertEquals(p.provenanceRecords.filter{it.unitNumber==4},live.provenanceRecords.filter{it.unitNumber==4})
  assertEquals(p.authoredEnglishRecords.filter{it.unitNumber==4},live.authoredEnglishRecords.filter{it.unitNumber==4})
  assertEquals(p.sourceCapabilities.filter{it.unitNumber==4},live.sourceCapabilities.filter{it.unitNumber==4})'''
    new=new[:-3]+extra+'\n }';t=t[:start]+new+t[end:]
    needle=' private fun baseline():'
    assert needle in t
    helper=''' private fun historicalConservationPack():V2CurriculumPack{val x=ProcessBuilder("git","show","057367e58751c4db86af05f5b84b463537ffa3c7:app/src/main/assets/content/v2-curriculum.json").directory(root()).start();val p=V2PackLoader.parse(x.inputStream.bufferedReader().readText());assertEquals(0,x.waitFor());return p}
'''
    return t.replace(needle,helper+needle,1)
edit('Unit4NominativeRoleTest',['allPriorExamplesRecordsCapabilitiesAndOtherUnitsRemainWhole'],unit4)

def pipeline(t):
    def adjust(s):
        needle='''        assertEquals("c2e405cf7081d673a39def8e26035bac7a65b608c57c5f4803ba832c2305c744", V2Validator.unitContentReviewDigest(pack, 5))'''
        assert needle in s
        replacement='''        fun actualAsset(commit: String): dev.russiancompanion.v2.curriculum.V2CurriculumPack {
            val command = ProcessBuilder("git", "show", "$commit:app/src/main/assets/content/v2-curriculum.json").directory(repo()).start()
            val asset = V2PackLoader.parse(command.inputStream.bufferedReader().readText())
            assertEquals(0, command.waitFor())
            return asset
        }
        val historical = actualAsset("ae5804bced1fc1641e4d57fdbd90bfde118e9d5f")
        assertEquals("c2e405cf7081d673a39def8e26035bac7a65b608c57c5f4803ba832c2305c744", V2Validator.unitContentReviewDigest(historical, 5))
        val activated = actualAsset("1e84576413cbd0e51cc07934baf416154e69bd0b")
        // Original Unit5 activation ledger,not a freshly copied current snapshot.
        val activatedDigest = "3c9149de87feeda96f64e195f3dab0428700049f3a7342e4ba764191a16db141"
        assertEquals(activatedDigest, V2Validator.unitContentReviewDigest(activated, 5))
        assertEquals(activated.unit(5), pack.unit(5))
        assertEquals(activatedDigest, V2Validator.unitContentReviewDigest(pack, 5))'''
        return s.replace(needle,replacement,1)
    return method_edit(t,'mappingPackageDigestBindsStructuralFactsAndIgnoresReviewerFields',adjust)
edit('review/UnitReviewPipelineTest',['mappingPackageDigestBindsStructuralFactsAndIgnoresReviewerFields'],pipeline)

def report(t):
    def adjust(s):
        start=s.index('        rows.forEach { row ->');end=s.index('\n        val markdownRows',start)
        old=s[start:end];new=old.replace('unit.advancementCanPresentlySucceed','ordinary.allowed')
        marker='''            assertEquals(unit.title, row.getValue("title").jsonPrimitive.content)'''
        extra='''            val currentUnit = pack.units.single { it.number == unit.unitNumber }
            val fixture = idealizedLearner(pack, currentUnit)
            val ordinary = dev.russiancompanion.v2.curriculum.ordinaryUnitRequirements(pack, currentUnit, fixture)
            val full = dev.russiancompanion.v2.curriculum.advancementDecision(pack, currentUnit, fixture)
            assertEquals(full.allowed, unit.advancementCanPresentlySucceed)
            if (unit.unitNumber in 3..30) {
                assertTrue(ordinary.remaining.joinToString("\\n"), ordinary.allowed)
                assertFalse(full.allowed)
                assertEquals(listOf("Complete the assigned earlier-objective review"), full.remaining)
            }
            // The immutable report predates review activation;its positive rows describe ordinary requirements.
'''+marker
        assert marker in new;new=new.replace(marker,extra,1);s=s[:start]+new+s[end:]
        needle='''                    it.advancementCanPresentlySucceed'''
        assert s.count(needle)==1
        s=s.replace(needle,'''                    dev.russiancompanion.v2.curriculum.ordinaryUnitRequirements(
                        pack, pack.units.single { u -> u.number == it.unitNumber },
                        idealizedLearner(pack, pack.units.single { u -> u.number == it.unitNumber }),
                    ).allowed''')
        return s
    return method_edit(t,'reportMatrixMatchesTheLiveCurriculum',adjust)
edit('audit/PostPr53ReadinessReconciliationTest',['reportMatrixMatchesTheLiveCurriculum'],report)

# Run whole current Unit4 class and the current independent rule/UI/forgery classes.
for cls in ['Unit4NominativeRoleTest','CompleteRuleCorrectionTest','Unit9QuantityRuleCorrectionTest','Unit9CompositionFeedbackCorrectionTest']:
    text=(Path('app/src/test/java/dev/russiancompanion/v2')/(cls+'.kt')).read_text()
    methods=re.findall(r'@Test\s+fun (\w+)\(',text)
    assert methods
    selected['dev.russiancompanion.v2.'+cls]=methods

write(OUT/'CORRECTION.json',{'sourceBefore':source,'changedClasses':changes,'productionChanges':False,'newTestAnnotations':0,'runtime':'PENDING_INITIAL','allTwelveObservedCasesCovered':True,'separateScopesNotSummed':True,'formalAcceptance':False})
write(OUT/'selection.json',{'selectedMethods':selected})
(OUT/'test-candidates.gradle').write_text('// Exact selected current classes;method selectors in selection.json.\nallprojects { tasks.withType(Test).configureEach {\n'+''.join("    include '**/"+c.split('.')[-1]+".class'\n" for c in selected)+'} }\n')
(OUT/'.gitignore').write_text('# Only immutable collector outputs;force-add verified evidence after completion.\n/INITIAL/\n/RETRY/\n')
write(OUT/'SOURCE_REVIEW.json',{'unit4ActualConservationSource':'057367e58751c4db86af05f5b84b463537ffa3c7','unit4Baseline':'b5610846cae953c3655466794c83cdcb4063ac4e','unit5HistoricalHashSource':'ae5804bced1fc1641e4d57fdbd90bfde118e9d5f','unit5ActualActivation':'1e84576413cbd0e51cc07934baf416154e69bd0b','sourceLedgersSha256':{str(p):sha(p.read_bytes()) for p in [Path('audit/content-corrections/UNITS8_10_COMPLETE_RULE_CORRECTION.json'),BASE/'ia003-unit5-gate/UNIT5_POLICY_ACTIVATION.json',Path('app/src/test/java/dev/russiancompanion/v2/Unit9CompositionFeedbackCorrectionTest.kt'),Path('app/src/test/java/dev/russiancompanion/v2/Unit9QuantityRuleCorrectionTest.kt')]},'runtimeOutputExclusion':'Only this followup INITIAL/RETRY plus completed broad CURRENT_REGRESSION are Git-ignored;source/report/governed-path guard unchanged. Force-add verified followup raw evidence. This prevents the collector itself creating an untracked-governed-path failure;no unexpected source path is allowed.','privatePdfUsed':False,'qualifiedAcceptance':False})
(OUT/'SOURCE_AND_SCOPE.md').write_text('''# Remaining twelve broad fixture cases\n\nOriginal1225-method broad run remains1208passed/16failed/1physical skip at6d91a7de. First four cases have a separate26-method followup. This scope addresses the other twelve;no fresh all-pass broad result is claimed.\n\nThree late source-intake positives retain every source/readiness/attribution and downstream forgery assertion. Their identical original learner fixtures separately pass ordinary requirements and fail full advancement with exactly assigned earlier-objective review. Two structural-mapping positives retain the original permissive rules and source-blocker negatives:ordinary requirements remain allowed,but full advancement must reject the unverifiable cumulative-review rule. No policy is disabled or prior completion/review fabricated by this correction. The existing permissive fixture is an explicit negative for full advancement.\n\nThe readiness method retains every readiness/objective/source negative and its original positive ordinary predicates. The live reconciler's actual no-review result is explicitly false;full decisions for7/16/17/26/27/30must refuse exactly assigned review. The required-objective helper independently proves the omitted objective blocks ordinary requirements,then passes ordinary requirements only after actual stored answers;full advancement still rejects the missing assigned review.\n\nTwo instructional-validity cases use the already-ledgered current bounded full-rule English answers and actual supplied masculine час cue;all14independent feature/context/answer/validator/no-English-match assertions remain. The pattern check requires the actual animate-object declined-numeral-and-noun explanation. The Unit8 shared-record mutation uses its actual STATED_APPLICATION relation while preserving equal record identity,different frozen content,rewritten rationale and validator refusal. Whole unchanged current complete-rule,quantity-rule and composition classes separately exercise source/UI/forgery/history guards.\n\nUnit4's global53-record/other-unit conservation contract is checked at its actual057367e5source,with every original assertion. The same method separately requires the entire current Unit4,all owned items,actual referenced examples,provenance,authored-English and capabilities to equal that authentic source;the other six current Unit4methods remain unchanged and all run. The Unit5oldhash stays tested at its originalae5804bcsource;the genuine1e845764activation asset and original activation-ledger digest must both match the entire current Unit5and digest. All mapping/reviewer-field mutations and later pipeline assertions stay current.\n\nThe historical post-PR53report/JSON/Markdown/blobs/allowlist and every diff-lock negative remain exact. Only this run's exact generated output directories are ignored before execution,preventing the collector from violating the unchanged governed-untracked-path guard. The report's positive rows remain checked as ordinary readiness;the live full decision for each3–30must remain false with exactly assigned review. Every other source/review/rights/candidate/approval predicate stays current;the historical report is never rewritten to imply current advancement.\n\nExact selectors/counts/committed inputs/commands/fresh XML/events/lint/gate will be recorded in INITIAL. No production/catalog/docs/review changes,new source admission,policy/schema changes,APK assembly/signing,merge,freeze or release approval. All four screenshot exporters16methods remain excluded:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. Private Penguin attachment never enters Git/assets/logs/packets. Final independent comprehensive audit FIRST;actual Galaxy S26 Ultra airplane-mode/reboot acceptance LAST/outstanding.\n''')
print('Prepared',sum(map(len,selected.values())),'methods',len(selected),'classes;12observed cases;NOT_YET_VERIFIED')
