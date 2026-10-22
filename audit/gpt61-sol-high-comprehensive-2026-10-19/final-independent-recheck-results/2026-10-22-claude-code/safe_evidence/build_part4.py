"""Build JSON returns and safe_evidence bundle (no private book material)."""
import json, os, sys, shutil, hashlib, csv, collections
S = sys.argv[1]; OUT = sys.argv[2]; W = os.path.join(S, 'work'); SE = os.path.join(OUT, 'safe_evidence')
TARGET = '8606c4d74185f594d93941bef6858b2af28e0b75'; DELIV = 'ae1105fbc78f6b5fbb2dbdce9058b08c5a44792e'
os.makedirs(SE, exist_ok=True)
rt = json.load(open(os.path.join(W, 'runtime_validation.json')))
p2 = json.load(open(os.path.join(W, 'part2_stats.json'))); p3 = json.load(open(os.path.join(W, 'part3_stats.json')))
hist = json.load(open(os.path.join(W, 'histories_check.json'))); arc = json.load(open(os.path.join(W, 'archives_check.json')))

def dump(name, obj, d=OUT):
    with open(os.path.join(d, name), 'w', encoding='utf-8') as f:
        json.dump(obj, f, indent=1, ensure_ascii=False); f.write('\n')

dump('CURRENT_RUNTIME_EVIDENCE_VALIDATION.json', {
 'schema': 'independent-recheck-runtime-validation-1', 'reviewer': 'Claude Code session (configured model claude-opus-5-5)', 'date': '2026-10-22',
 'targetCommit': TARGET, 'deliveryCommit': DELIV,
 'scopesSummed': False, 'combinedAllPassClaim': False,
 'scopes': {
  'broad_e5b5d3c0': dict(rt['broad_e5b5d3c0'], archive='audit/gpt61-sol-high-comprehensive-2026-10-19/post-recording-guard-host-regression-2026-10-22/RAW_REGRESSION.zip',
     tasks={'compileDebugKotlin': 'UP-TO-DATE', 'compileDebugUnitTestKotlin': 'UP-TO-DATE', 'compileDebugSources': 'UP-TO-DATE', 'testDebugUnitTest': 'FAILED (9 test failures, --rerun)',
            'lintAnalyzeDebug': 'UP-TO-DATE', 'lintDebug': 'executed', 'verifyReleaseGateAttachment': 'executed'},
     lint={'Fatal': 0, 'Error': 0, 'Warning': 20, 'Hint': 1, 'warningIds': {'PluralsCandidate': 10, 'UnusedResources': 7, 'ModifierParameter': 2, 'DataExtractionRules': 1}, 'hintIds': {'AutoboxingStateCreation': 1}},
     freshness='RESULT.json flags selectedFreshXmlAndEventsMatch=true, eventOutcomeCountsMatchXml=true; discovery exactSameMethodSet=true',
     skippedMethod='dev.russiancompanion.acceptance.AcceptanceBoundaryTest#at020_physicalRebootPersistsGovernedRecords (physical; not device evidence)'),
  'followup_20c5eb96': dict(rt['followup_20c5eb96'], archive='audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-source-binding-reconciliation-2026-10-22/RAW_REGRESSION.zip',
     tasks={'compile*': 'UP-TO-DATE', 'testDebugUnitTest': 'executed (selectors)', 'lintDebug': 'executed', 'verifyReleaseGateAttachment': 'executed', 'build': 'BUILD SUCCESSFUL in 9m 5s'},
     lint='identical LINT.xml sha256 76239d74... to broad scope',
     methodSetVsBroad='exactly the 56 broad-scope methods of the 9 classes (47 PASSED + 9 FAILED in broad)')},
 'treeIdentity': {'app': {'e5b5d3c0': 'c83cbcc47a8c88976469b6a542347fe6bfd40af5', '20c5eb96': 'c83cbcc47a8c88976469b6a542347fe6bfd40af5', '8606c4d7': 'c83cbcc47a8c88976469b6a542347fe6bfd40af5'},
                  'review': {'e5b5d3c0': '366549a8a89d34b33ff9568247d7a4ff5ef676d6', '20c5eb96': '88afd115e13f41a7a5ebeca6021fb8874164818e', '8606c4d7': '88afd115e13f41a7a5ebeca6021fb8874164818e'},
                  'release': {'e5b5d3c0': '9436916a11c4f6de63acf0e23edae91ae29bad26', '20c5eb96': '13f7017696fdfd04d37be121651809cdbeecd607', '8606c4d7': '13f7017696fdfd04d37be121651809cdbeecd607'},
                  'catalog_docs': 'identical at all three', 'commits_20c5eb96_to_target': 1, 'nonAuditFilesChanged_20c5eb96_to_target': ['AGENTS.md']},
 'atTargetInterpretation': '56 methods (9 classes) evidenced PASS at a commit whose app/review/release/catalog/docs trees equal the target. The other 1277 PASSED and 1 SKIPPED methods are evidenced only at e5b5d3c0, whose review/ and release/ trees differ (92 non-audit files). No fresh complete-suite result exists at the target.',
 'hostMethodEvidenceIndex': {'rows': 1334, 'broadOutcomesMatchRaw': '1334/1334', 'followupOutcomesMatchRaw': '56/56 (1278 NOT_SELECTED)', 'targetFileSha256Match': '219/219 files',
                             'declarationLinesContainMethod': '1334/1334', 'independentScenarioReview': 'UNINSPECTED for all rows; test bodies not semantically reviewed (except backup boundary tests)'},
 'nineFailureRecheck': 'NINE_FAILURE_METHOD_RECHECK.json: 9 FAILED->PASSED with test bytes unchanged; consistent with raw data; does not close findings',
 'excludedExporters': {'VisualReviewCaptureTest': 2, 'Units35ChunkCaptureTest': 2, 'Units610ChunkCaptureTest': 10, 'Units1115GapCaptureTest': 2, 'methods': 16, 'run': False},
 'independentExecution': {'status': 'UNAVAILABLE', 'reason': 'dl.google.com CONNECT 403 under the session network policy (re-tested 2026-10-22); no ANDROID_HOME/SDK; Gradle/Robolectric cannot run; no emulator or device',
                          'ciEvidence': 'GitHub Actions runs on 8606c4d7 and ae1105fb concluded failure in 3-4 s with no runner assigned and no logs (HTTP 404); no CI execution evidence'},
 'apk': {'assembled': False, 'installed': False, 'signed': False, 'hash': None, 'configuredIdentity': 'dev.russiancompanion / 2.0.0-dev / versionCode 5 (configuration only)'},
 'selectorDiscrepancy': 'Requests 22 and 60 name *HostActivityLifecycleTest; no such class (file declares LearnerReloadRecoveryTest and HostActivityRecreationTest) - CC046',
 'separateEarlierScopes': 'ff2a5f55 1323-method selected result, 34/3/37 focused scopes, main ff539220 562 pass/1 skip and 420f41ff 691 annotations were NOT re-validated in this session and remain separate',
 'physicalAcceptance': 'OUTSTANDING_LAST'})

caps = open(os.path.join(W, 'caps_out.txt'), encoding='utf-8').read()
dump('SOURCE_AND_BINDING_REVIEW.json', {
 'targetCommit': TARGET, 'curriculum': {'path': 'app/src/main/assets/content/v2-curriculum.json', 'bytes': 20350033, 'sha256': 'fc2a59cdc61ec82640f29743707b67f04812a372e13fac08987aa16e512df2ca'},
 'sources': {'count': 37, 'rights': 37, 'oneToOne': True, 'permitsAppUse': 'all true', 'licences': 'CC BY-SA 4.0 x31; Original app text x3; Public domain writing-system facts x1 (Unicode); Public domain x1 (Chernyshevsky via ru.wikisource); see entities',
             'notes': ['src-wikibooks-russian-cc-by-sa record carries no oldid itself', 'three sources state qualified source/rights review pending', 'src-unicode-cyrillic used for Russian letter names (misattribution, CC035)']},
 'sourceCapabilities': {'count': 58, 'selfHashMatches': 58, 'bindingValid': 58, 'spanInSourceTextPool': 51,
                        'notInPool': ['cap-u7-adverb-invariance', 'cap-u7-adverb-bounded-pairs', 'cap-u16-instrumental-complements', 'cap-u17-age', 'cap-u17-clock-expression-recognition', 'cap-u21-reflexive-object', 'cap-obj-u26-correspondence'],
                        'externalFidelity': 'UNVERIFIABLE: no retained source snapshots in repo; en/ru.wikipedia.org CONNECT rejected (403) in this session',
                        'selfAuthoredSpans': ['cap-u20-motion-po-setting-out', 'cap-u20-motion-prefixed-path', 'cap-obj-u28-imperfective-adverb', 'cap-obj-u28-perfective-adverb', 'cap-u7-bounded-manner-adverb', 'cap-obj-u27-comprehension'],
                        'table': caps},
 'provenance': {'records': 7036, 'sourceHashRecomputed': 7036, 'contentHashRecomputed': 7036, 'sourceTextPool': {'entries': 438, 'refs': 3581, 'unused': 0, 'shaOk': 438},
                'semanticReview': 'NOT_PERFORMED per record', 'reviewerIdentities': 0},
 'authoredEnglishRecords': {'count': 1655, 'distinctTexts': 1265, 'longRecordsUnmatchedInShippedText': '538/1488'},
 'tuplePins': {'pinned': ['phase2:bounded-manner-adverb-formation', 'phase2:completion-over-period', 'phase3:bookish-reading', 'phase3:dictionary-use'],
               'unpinned': ['phase2:documented-name-form', 'phase3:postal-address', 'phase3:participle-stress', 'phase3:participle-comprehension', 'phase3:bookish-register'], 'finding': 'CC008'},
 'releaseSourceDigest': {'recomputedIndependently': True, 'files': 1376, 'values': {'8502cadc...': ['7a8b705a', 'ff2a5f55'], 'eb5c52ed...': ['49c85a89', 'e5b5d3c0', '20c5eb96', '8606c4d7', 'ae1105fb']},
                         'candidate': 'sourceDigest eb5c52ed; frozen false; sourceCommit null; artifact NOT_RECORDED; publicRelease NOT_APPROVED'},
 'sourceBindingCorrection95': {'files': 95, 'onlyDigestSubstitution': True, 'index155': arc['summary'], 'historicalPriorCurrentZip': 'CRC OK; contains all 95 prior versions'},
 'penguin': {'pdfVerified': {'bytes': 13596737, 'sha256': '2bcc0c45fac4508adf283f4dd939a7e578cf2c1306fdadf4966300532beb6303'}, 'keptOutsideCheckout': True,
             'trackedPdfsAtTarget': 0, 'isbnObserved': '0-14-012041-6 / 9780140120417 (check digits valid; registry not verified)', 'proseQuoted': False},
 'variance': {'activatedInCode': [27, 29, 30], 'unresolvedVarianceUnits': [], 'finding': 'CC048'},
 'approvals': 'NONE: no reviewer, owner mapping, final audit or release approval exists or was created'})

dump('AUDIT_SCOPE_AND_EVIDENCE.json', {
 'outcome': 'INCOMPLETE', 'auditType': 'Independent comprehensive NONPHYSICAL diagnostic recheck; NOT an Amendment 1 FINAL_CURRICULUM_AUDIT_PASSED artifact',
 'reviewer': {'tool': 'Claude Code (cloud session)', 'configuredModel': 'claude-opus-5-5', 'lastServedModel': 'claude-opus-5-5 (get_session)', 'subagents': 0, 'note': 'Not the GPT-6.1 Sol/High chat named in the work order'},
 'pins': {'target': TARGET, 'targetTree': '7c36e7e6041e6c6415964c8904f8f4478d23805f', 'trackedFiles': 8596, 'delivery': DELIV, 'deliveryChanges': '15 added + 3 modified (additive pointer text)', 'pr64HeadAtFetch': DELIV},
 'denominators': {'units': 30, 'objectives': 195, 'items': 626, 'requiredChecks': 429, 'examples': 821, 'sourceCapabilities': 58, 'sources': 37, 'rights': 37, 'references': 27, 'letters': 33,
                  'mappings': 30, 'unitReviews': 30, 'provenanceRecords': 7036, 'authoredEnglishRecords': 1655, 'sourceTextPool': 438, 'requirements': 195, 'originalFindings': 17,
                  'targets': 75, 'iaIdentities': 18, 'executionRequests': 70, 'histories': 64, 'archives': 66, 'historicalMembers': 167, 'sourceBindingIndex': 155, 'hostMethodRows': 1334},
 'coverage': {'curriculumEntityRows': sum(p2['entityCounts'].values()), 'entityTypes': p2['entityCounts'], 'itemVerdicts': p2['itemVerdicts'], 'repositoryFiles': p3['fileCoverage'], 'requirements': p3['requirements']},
 'readingMethod': {'units1to16': 'full renders (all fields) read with offsets', 'units17to30': 'compact renders (why/distractorDifference/contrast lines omitted; remediation bodies truncated); remediation coverage tested mechanically',
                   'book': 'All 30 lessons, tables, spelling rules, glossary, index, back cover read visually; vocabulary and key sampled'},
 'unreadRemaining': {'book': ['PDF 1-12', 'PDF 402-428', 'PDF 431', 'PDF 434-438', 'PDF 441-463', 'PDF 466-512'],
                     'repository': '8040 of 8596 tracked files NOT_INSPECTED (REPOSITORY_FILE_COVERAGE.csv), including most test bodies, legacy code, catalog, docs other than MA2/AM1, review packets beyond digest bytes, and historical audit bodies',
                     'curriculum': 'provenanceRecords/authoredEnglish/sourceTextPool semantics; rationale/why fields for Units 17-30 read only mechanically',
                     'engineering': 'V2Validator outside listed ranges; V2GovernedIdentity, LocalStore, V2Chrome, V2WriteCoordinator, V2Persistence, V2BackupScreen bodies; legacy presentation/domain/data code',
                     'external': 'Third-party source spans (no snapshots; Wikipedia unreachable)', 'runtime': 'All execution (SDK host blocked)', 'physical': 'Galaxy S26 Ultra acceptance OUTSTANDING_LAST'},
 'histories': {'checked': len(hist), 'structuralDefects': sum(1 for h in hist if h.get('defects')), 'recordings': 0, 'schemas': '1-5 (no 6)'},
 'archives': arc['summary'],
 'boundaries': {'appModified': False, 'apkAssembledInstalledSigned': False, 'exportersOrWriteRegenerationRun': False, 'merge': False, 'freeze': False, 'amendment1Advancement': False,
                'ownerOrReleaseApproval': False, 'privatePdfInDeliverables': False, 'bookProseQuoted': False},
 'findings': {'original17': 'none closed; CC012/CC013/CC014/CC016 static-resolved only', 'new': 'CC018-CC048 (31): 9 HIGH, 11 MEDIUM, 11 LOW'}})

dump('checkpoint.json', {'checkpoint': 'final-independent-recheck-2026-10-22-claude-code', 'state': 'RETURNED_INCOMPLETE', 'target': TARGET, 'delivery': DELIV,
 'completedTasks': ['input/ZIP verification', 'Penguin PDF verification and lesson reading', 'all 30 units read', 'entity mechanical checks', 'engineering static review',
                    'runtime scope validation (no execution)', 'findings/targets/IA/requests/histories/archives adjudication', 'returns written'],
 'nextActions': ['Allow dl.google.com (or provide an SDK-equipped runner) and execute the 70 requests', 'Address CC018-CC048 and open original findings',
                 'Read remaining book back-matter ranges and uninspected repository files', 'Owner/Codex reconcile CC048 variance activation', 'Physical acceptance LAST'],
 'physicalAcceptance': 'OUTSTANDING_LAST', 'approvals': 'NONE'})

# safe_evidence: reproducible scripts and mechanical outputs (no private book notes)
for fn in ['mech_curriculum.py', 'mech_curriculum_out.json', 'cyrillic_coverage.py', 'remediation_coverage.py', 'remediation_coverage_out.txt', 'release_digest.py',
           'render_units.py', 'view_unit.py', 'caps_check.py', 'caps_out.txt', 'check_histories.py', 'histories_check.json', 'check_archives.py', 'archives_check.json',
           'validate_runtime.py', 'runtime_validation.json', 'item_flags.py', 'item_flags.json', 'dump_targets.py', 'targets_dump.txt', 'dump_entities.py',
           'findings_data.py', 'build_part1.py', 'build_part2.py', 'build_part3.py', 'build_part4.py', 'verify_inputs_out.json', 'prep_sha.txt']:
    src = os.path.join(W, fn)
    if os.path.isfile(src): shutil.copy2(src, os.path.join(SE, fn))
os.makedirs(os.path.join(SE, 'original_return'), exist_ok=True)
for fn in ['FINDINGS.csv', 'TARGET_75_DISPOSITIONS.csv', 'EXECUTION_EVIDENCE_REQUESTS.csv', 'REQUIREMENTS_MATRIX.csv', 'GAP_CLOSURE.csv', 'FINDING_RECONCILIATION.csv']:
    shutil.copy2(os.path.join(S, 'orig_zip', fn), os.path.join(SE, 'original_return', fn))
shutil.copy2(os.path.join(S, 'orig_zip', 'safe_evidence', 'HISTORICAL_IA_RECHECKS.csv'), os.path.join(SE, 'original_return', 'HISTORICAL_IA_RECHECKS.csv'))
print('part4 ok')
