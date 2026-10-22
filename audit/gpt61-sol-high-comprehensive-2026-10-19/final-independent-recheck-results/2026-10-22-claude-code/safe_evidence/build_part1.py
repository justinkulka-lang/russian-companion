"""Build findings, reconciliation, 75-target and execution-request returns (independent recheck 2026-10-22)."""
import csv, json, os, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import findings_data as FD

S = sys.argv[1]
OUT = sys.argv[2]
ORIG = os.path.join(S, 'orig_zip')
TARGET = '8606c4d74185f594d93941bef6858b2af28e0b75'
os.makedirs(OUT, exist_ok=True)

def rcsv(name):
    return list(csv.DictReader(open(os.path.join(ORIG, name), encoding='utf-8')))

def wcsv(name, rows, fields):
    with open(os.path.join(OUT, name), 'w', newline='', encoding='utf-8') as f:
        w = csv.DictWriter(f, fieldnames=fields, lineterminator='\n')
        w.writeheader()
        for r in rows:
            w.writerow({k: r.get(k, '') for k in fields})

# ---------------- FINDINGS.csv ----------------
orig = {r['finding_id']: r for r in rcsv('FINDINGS.csv')}
fields = ['finding_id', 'severity', 'category', 'unit', 'content_id', 'current_path', 'locator', 'requirement_id',
          'observed_behavior', 'expected_behavior', 'evidence', 'confidence', 'proposed_correction', 'acceptance_test',
          'status', 'original_status', 'independent_disposition_2026_10_22', 'evidence_target_commit', 'closure']
rows = []
for fid, o in orig.items():
    c = FD.ORIGINAL[fid]
    rows.append(dict(o, observed_behavior='CURRENT (8606c4d7): ' + c['observed'],
                     evidence=c['evidence'] + ' | original evidence retained in FINDING_RECONCILIATION.csv and safe_evidence/original_return/FINDINGS.csv',
                     status=c['status'], original_status=o['status'], independent_disposition_2026_10_22=c['disposition'],
                     severity=c['sev'], evidence_target_commit=TARGET,
                     closure='NOT_CLOSED (no runtime, qualified-review or owner evidence; this recheck cannot close findings)'))
for n in FD.NEW:
    rows.append(dict(finding_id=n['id'], severity=n['sev'], category=n['cat'], unit=n['unit'], content_id=n['content'],
                     current_path=n['path'], locator=n['path'], requirement_id=n['req'], observed_behavior=n['obs'],
                     expected_behavior=n['exp'], evidence='Independent reading/mechanical analysis at ' + TARGET + '; see INDEPENDENT_AUDIT_REPORT.md and safe_evidence/',
                     confidence=n['conf'], proposed_correction=n['fix'], acceptance_test=n['test'], status='OPEN_NEW',
                     original_status='NEW_THIS_RECHECK', independent_disposition_2026_10_22='NEW_FINDING',
                     evidence_target_commit=TARGET, closure='NOT_CLOSED'))
wcsv('FINDINGS.csv', rows, fields)

# ---------------- FINDING_RECONCILIATION.csv ----------------
rec = {r['finding_id']: r for r in rcsv('FINDING_RECONCILIATION.csv')}
fields = ['finding_id', 'prior_status', 'prior_disposition_original_return', 'current_disposition', 'raw_entities_checked',
          'requirement_locators', 'independent_evidence', 'unexecuted_acceptance_checks', 'remaining_action',
          'historical_observation_original_return']
rows = []
for fid, r in rec.items():
    c = FD.ORIGINAL[fid]
    rows.append(dict(finding_id=fid, prior_status=r['prior_status'], prior_disposition_original_return=r['current_disposition'],
                     current_disposition=c['disposition'] + ' / ' + c['status'], raw_entities_checked=r['raw_entities_checked'],
                     requirement_locators=r['requirement_locators'], independent_evidence=c['observed'] + ' [' + c['evidence'] + ']',
                     unexecuted_acceptance_checks=r['unexecuted_acceptance_checks'], remaining_action=c['remaining'],
                     historical_observation_original_return=orig[fid]['observed_behavior']))
for n in FD.NEW:
    rows.append(dict(finding_id=n['id'], prior_status='NONE', prior_disposition_original_return='NOT_IN_ORIGINAL_RETURN',
                     current_disposition='NEW_FINDING / OPEN_NEW', raw_entities_checked=n['content'],
                     requirement_locators=n['req'], independent_evidence=n['obs'],
                     unexecuted_acceptance_checks=n['test'], remaining_action=n['fix'], historical_observation_original_return=''))
wcsv('FINDING_RECONCILIATION.csv', rows, fields)

# ---------------- TARGET_75_DISPOSITIONS.csv ----------------
special = {
 'CR009': ('RESOLVED_STATIC_NARROW', 'Changed from original CHANGED_BUT_NOT_RESOLVED: example note is a phrase gloss and u7-st-teach-objectadj-3 now separates the full sentence from the fragment.', 'CC016'),
 'CR010': ('RESOLVED_STATIC_NARROW', 'Changed from original CHANGED_BUT_NOT_RESOLVED: phrase-only gloss; u10-st-teach-animate-1/2 state the phrase supplies no predicate.', 'CC016'),
 'CR011': ('RESOLVED_STATIC_NARROW', 'Changed from original CHANGED_BUT_NOT_RESOLVED: "these women, nominative plural"; teaching scoped.', 'CC016'),
 'CR005': ('RESOLVED_STATIC_NARROW', 'Now an English classification of the inanimate object form; 2-option item.', 'CC036'),
 'CR006': ('RESOLVED_STATIC_NARROW', 'Target gender/case now stated; 2-option item.', 'CC036'),
 'CR018': ('RESOLVED_STATIC_NARROW', 'Gloss no longer identifies Anna as reader.', 'CC028'),
 'CR019': ('RESOLVED_STATIC_NARROW', 'Key correct; distractors absurd ("the letter put Anna away").', 'CC028'),
 'CR020': ('RESOLVED_STATIC_NARROW', 'Bookish qualifier attributed to дабы; purpose objective still lacks independent purpose check.', 'CC004'),
 'CR021': ('RESOLVED_STATIC_NARROW', 'Guided чтобы item correct.', 'CC004'),
 'CR022': ('RESOLVED_STATIC_NARROW', 'Narrow qualifier issue resolved; item keys если (condition) inside the purpose objective.', 'CC004'),
 'CR025': ('RESOLVED_STATIC_NARROW', 'Masculine а/я nouns now included; the ending itself is still not stated; title "Admitted instruction".', 'CC022;CC029'),
 'CR033': ('RESOLVED_STATIC_NARROW', 'Pair overstatement removed; related keyed mistranslation of сказать as speak persists elsewhere in Unit 12.', 'CC024'),
 'CR034': ('RESOLVED_STATIC_NARROW', 'Relative/shared-subject reading present; stray capital mid-sentence.', 'CC009;CC025'),
 'CR043': ('RESOLVED_NARROW_NEW_DEFECT_INTRODUCED', 'Ambiguity removed, but the corrected Unit 7 required check now analyses a genitive phrase before Unit 9.', 'CC031;CC022'),
 'CR045': ('RESOLVED_STATIC_NARROW', 'Ambiguity removed; distractors absurd.', 'CC028'),
 'CR046': ('RESOLVED_STATIC_NARROW', 'Component scope fixed; sibling u17-i-calendar-date-1 is ambiguous label recall.', 'CC026'),
 'CR047': ('RESOLVED_STATIC_NARROW', 'Whole-date scope fixed.', 'CC026'),
 'CR058': ('RESOLVED_STATIC_NARROW', 'Aspect removed from Unit 11; objective id still says past-aspect while titled gender agreement.', ''),
 'CR059': ('RESOLVED_STATIC_NARROW', 'Early dative/short-form removed; Unit 7 manner checks key English/meta descriptions.', 'CC026'),
 'CR064': ('RESOLVED_STATIC_NARROW', 'Canonical unit instructional document includes objective.gating (V2Validator.kt:475); not executed.', ''),
 'CR069': ('RESOLVED_STATIC_NARROW', 'Categorical claim removed; u26 letter-writing checks test English inference.', 'CC026'),
}
for k in ['CR036', 'CR037', 'CR038', 'CR039', 'CR040', 'CR041']:
    special[k] = ('RESOLVED_STATIC_NARROW', 'Relative-time/shared-subject keys correct; source qualification (CC009) open; non-diagnostic option families.', 'CC009;CC028')
special['CR035'] = ('RESOLVED_STATIC_NARROW', 'Relative timing described correctly.', 'CC009')
for k in ['CR052', 'CR053', 'CR054', 'CR055', 'CR056']:
    special[k] = ('RESOLVED_STATIC_NARROW', 'Guided routes present; foundation scope (CC002) and glyph-matching checks (CC027) remain.', 'CC002;CC027')
for k in ['CR060', 'CR061', 'CR062', 'CR063']:
    special[k] = ('RESOLVED_STATIC_NARROW', 'Present passive objective gating=false; required stress objective checks past passive only (u27-i-stress-2/3/4); u27-i-stress-4 keys an app-policy statement.', 'CC026' if k == 'CR061' else '')
fields = ['correction_id', 'content_finding_id_CA_namespace', 'unit', 'content_id', 'original_diagnostic_disposition',
          'original_new_finding_ids', 'current_raw_text_checked_at_target', 'independent_disposition_2026_10_22',
          'independent_evidence', 'linked_finding_ids_CC_namespace', 'namespace_note']
rows = []
for r in rcsv('TARGET_75_DISPOSITIONS.csv'):
    d, note, links = special.get(r['correction_id'], ('RESOLVED_STATIC_NARROW', 'Current raw text no longer exhibits the originally targeted defect.', ''))
    rows.append(dict(correction_id=r['correction_id'], content_finding_id_CA_namespace=r['finding_id'], unit=r['unit'],
                     content_id=r['content_id'], original_diagnostic_disposition=r['diagnostic_disposition'],
                     original_new_finding_ids=r['new_finding_ids'],
                     current_raw_text_checked_at_target='YES: ' + FD.CUR + ' (raw JSON field text dumped and read; work/targets_dump.txt)',
                     independent_disposition_2026_10_22=d, independent_evidence=note, linked_finding_ids_CC_namespace=links,
                     namespace_note='CR = correction target; CA = content-audit finding; CC = qualified finding. CR009-CR011 are correction targets, not CC009-CC011.'))
wcsv('TARGET_75_DISPOSITIONS.csv', rows, fields)

# ---------------- EXECUTION_EVIDENCE_REQUESTS.csv ----------------
rt = json.load(open(os.path.join(S, 'work', 'runtime_validation.json')))
broad_pass = {
 22: 'LearnerReloadRecoveryTest 1/1 and HostActivityRecreationTest 3/3 PASSED at e5b5d3c0 (selector *HostActivityLifecycleTest matches no class; CC046)',
 29: 'LabCueCorrectionTest 4/4, LabExplanationCorrectionTest 3/3 PASSED at e5b5d3c0',
 31: 'CumulativeReviewGateTest 8/8, CumulativePolicyHistoryBaselineTest 2/2, MultiPoolReviewSelectionTest 7/7 PASSED at e5b5d3c0',
 45: 'AppearanceStoreTest 3/3 PASSED at e5b5d3c0',
 47: 'BackupSnapshotCorrectionTest 1/1 PASSED at e5b5d3c0',
 48: 'BackupLicenseTest 14/14 PASSED at e5b5d3c0',
 49: 'BackupLicenseTest 14/14, RuntimeCorrectionTest 24/24 PASSED at e5b5d3c0',
 58: 'BackupRecordingFilenameBoundaryTest 3/3, BackupArchiveBoundaryTest 8/8 PASSED at e5b5d3c0',
 59: 'DatabaseMigrationTest 7/7, CompletionEvidenceCorrectionTest 4/4 PASSED at e5b5d3c0',
 60: 'RuntimeCorrectionTest 24/24, HostActivityRecreationTest 3/3, LearnerReloadRecoveryTest 1/1 PASSED at e5b5d3c0 (selector mismatch CC046)',
 66: 'ReleaseGateTest 24/24, ReleaseOutputTasksTest 3/3, LinguisticReleaseGateTest 5/5 PASSED at e5b5d3c0',
 67: 'FullSynthesisTupleCorrectionTest 3/3 PASSED at e5b5d3c0',
 68: 'SourceTextPoolTest 3/3 PASSED at e5b5d3c0',
}
emulator = {41, 55, 56, 57, 61, 62, 63, 64}
static_cc = {5: 'CC001 narrowed (permutation reproduced; residual skew)', 6: 'CC002 open', 7: 'CC003 open', 8: 'CC004 open', 9: 'CC005 open',
             10: 'CC006 open', 11: 'CC007 open', 12: 'CC008 narrowed (4/9 pinned)', 13: 'CC009 open', 14: 'CC010 open + CC030',
             15: 'CC011 open + CC026', 16: 'CC012 static-resolved for Unit 8; CC031 new', 17: 'CC013 static-resolved', 18: 'CC014 static-resolved',
             19: 'CC015 changed-not-resolved', 20: 'CC016 static-resolved', 21: 'CC017 open'}
fields = ['request_no', 'requirement_id', 'scenario', 'required_environment', 'command_or_test_identity', 'expected_behavior',
          'evidence_required', 'execution_status', 'remaining_blocker', 'independent_disposition_2026_10_22',
          'retained_raw_evidence_relevance', 'independent_execution_this_session']
rows = []
for i, r in enumerate(rcsv('EXECUTION_EVIDENCE_REQUESTS.csv'), 1):
    if i in (1, 2, 3, 4):
        disp = 'PARTIALLY_EVIDENCED_NOT_AT_TARGET'
        rel = ('Broad e5b5d3c0: 1334 methods 1324 PASSED/9 FAILED/1 SKIPPED, exit 1; follow-up 20c5eb96: 56/56 PASSED, exit 0; compile tasks UP-TO-DATE; '
               'lintDebug 0 fatal/0 error/20 warning/1 hint; verifyReleaseGateAttachment ran. app/ tree c83cbcc4 identical at e5b5d3c0, 20c5eb96 and target; '
               'review/release trees differ at e5b5d3c0. Scopes not summed.')
    elif i in static_cc:
        disp = 'STATIC_REASSESSED_NOT_EXECUTED'
        rel = static_cc[i]
    elif i in broad_pass:
        disp = 'RETAINED_RAW_PASS_NOT_AT_TARGET_NOT_SCENARIO_VERIFIED'
        rel = broad_pass[i] + '; app/ tree identical to target; requested new negative scenarios not shown present; test bodies not semantically reviewed'
    elif i in emulator:
        disp = 'UNAVAILABLE_EMULATOR_OR_DEVICE_REQUIRED'
        rel = 'No emulator/device in this session; host Robolectric results do not establish this scenario.'
    elif i == 70:
        disp = 'STRUCTURAL_RECHECK_DONE_REPLAY_NOT_EXECUTED'
        rel = '64 histories: digests recomputed independently, 0 structural defects, 0 recordings, schemas 1-5 only (CC047); 66 archives sha+CRC match; oldUnit7 restore cause UNCONFIRMED.'
    else:
        disp = 'NOT_EXECUTED_STATIC_ONLY'
        rel = 'Static code/content reading only; see APP_ENGINEERING_AUDIT.csv for the surface.'
    rows.append(dict(r, request_no=i, independent_disposition_2026_10_22=disp, retained_raw_evidence_relevance=rel,
                     independent_execution_this_session='UNAVAILABLE: Android SDK host dl.google.com CONNECT 403; no ANDROID_HOME; Gradle/Robolectric not runnable; no emulator/device'))
wcsv('EXECUTION_EVIDENCE_REQUESTS.csv', rows, fields)
print('part1 ok')
