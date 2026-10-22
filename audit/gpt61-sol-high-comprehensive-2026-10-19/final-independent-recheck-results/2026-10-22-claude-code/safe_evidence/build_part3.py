"""Build requirements, engineering, gap, work-order, IA and repository/supplement coverage returns."""
import csv, json, os, re, sys, subprocess, hashlib, collections
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import findings_data as FD

S = sys.argv[1]; OUT = sys.argv[2]
ORIG = os.path.join(S, 'orig_zip'); T = os.path.join(S, 'target'); DL = os.path.join(S, 'delivery')
TARGET = '8606c4d74185f594d93941bef6858b2af28e0b75'; DELIV = 'ae1105fbc78f6b5fbb2dbdce9058b08c5a44792e'

def rcsv(p): return list(csv.DictReader(open(p, encoding='utf-8')))
def wcsv(name, rows, fields, outdir=OUT):
    with open(os.path.join(outdir, name), 'w', newline='', encoding='utf-8') as f:
        w = csv.DictWriter(f, fieldnames=fields, lineterminator='\n'); w.writeheader()
        for r in rows: w.writerow({k: r.get(k, '') for k in fields})

# ---------------- REQUIREMENTS_MATRIX.csv ----------------
ma = open(os.path.join(T, 'docs/governance/MASTER_AUTHORITY_v2.0.md'), encoding='utf-8').read()
clauses = dict(re.findall(r'\*\*(MA2-\d{3})\.\*\*\s*(.+)', ma))
origm = {r['requirement_id']: r for r in rcsv(os.path.join(ORIG, 'REQUIREMENTS_MATRIX.csv'))}
V = {}
def v(ids, verdict, ev, fids='', lim=''):
    for i in ids.split(): V[i] = (verdict, ev, fids, lim)
STATIC = 'SATISFIED_STATIC_NOT_ACCEPTED'
v('MA2-001', 'UNMET', 'Units read in full; teaching scripts keys, rules withheld, non-diagnostic checks', 'CC002;CC015;CC021;CC022;CC027')
v('MA2-002', STATIC, 'BOOK/ANKI/BINDER/TUTOR steps in every unit')
v('MA2-003', 'NOT_VERIFIED_RUNTIME', 'Today/Continue logic read statically only')
v('MA2-004', 'UNMET', 'No sound/handwriting/transliteration foundation; Unit 1 glyph matching', 'CC002;CC022;CC027')
v('MA2-005', 'PARTIAL', 'Generic step titles such as "Admitted instruction"/"Admitted lines"', 'CC029')
v('MA2-006', 'NOT_VERIFIED_NOT_INSPECTED', 'Legacy v1 reachability only checked', 'CC044')
v('MA2-007', 'UPHELD_CURRENTLY', 'ReleaseGate fail-closed; many blockers present; no release action taken', 'CC039')
v('MA2-008', STATIC, 'Gate independent of CI result')
v('MA2-009', 'NOT_VERIFIED_DEVICE', 'Airplane-mode behaviour requires device')
v('MA2-010 MA2-012 MA2-013 MA2-014', STATIC, 'No java.net/HTTP/WebView usage in app/src/main/java (grep); no account/AI code observed')
v('MA2-011', STATIC, 'Source manifest has no INTERNET; merged manifest not built')
v('MA2-015', STATIC, 'No network permission; RECORD_AUDIO declared but unused by v2', 'CC038')
v('MA2-016 MA2-017', STATIC, 'Room DB local; versioned v2 pack in assets')
v('MA2-018', 'UNMET', 'Book Lessons 1-30 read; scope gaps', 'CC003;CC004;CC005;CC006;CC007;CC032')
v('MA2-019', 'NOT_VERIFIED_RUNTIME', 'Onboarding read statically; default route not executed')
v('MA2-020 MA2-021', 'UNMET', 'Foundation scope absent', 'CC002;CC038')
v('MA2-022', STATIC, 'Units declare ids, objectives, prerequisites, minutes, advancement')
v('MA2-023', 'UNMET', 'Item and unit audits', 'CC001;CC003;CC004;CC010;CC011;CC015;CC021;CC022;CC023;CC026;CC027;CC028;CC030')
v('MA2-024', 'UNMET', 'Untutored or scripted items; mistranslated glosses', 'CC015;CC022;CC024;CC025')
v('MA2-025', 'NOT_APPLICABLE_NO_AUDIO_SHIPPED', 'audioAssets=0')
v('MA2-026', 'UNMET', 'Genitive analysed in Unit 7', 'CC031')
v('MA2-027', 'UNMET', 'Fixed two-slot replay (changed from original SATISFIED_DIAGNOSTIC)', 'CC020')
v('MA2-028', 'UNMET', 'Listening/speaking absent; scope gaps', 'CC002;CC032')
v('MA2-029 MA2-030', 'UNMET', 'Early genitive; past chunks in Unit 6', 'CC031;CC032')
v('MA2-031', STATIC, 'No Penguin prose observed in units read; 0 tracked PDFs; capstone is ReadyRussian', '', 'Not a full textual comparison against the whole book')
v('MA2-032', STATIC, 'BOOK steps direct learner to owned copy')
v('MA2-033', 'PARTIAL', 'Mappings identify edition and lesson; locator omissions', 'CC033')
v('MA2-034', STATIC, 'Mapping notes independently written')
v('MA2-035', 'UNMET_PENDING_OWNER', 'All 30 mappings PENDING_CURRICULUM_REVIEWER')
v('MA2-036', STATIC, 'Pending status disclosed')
v('MA2-037', 'NOT_VERIFIED_NOT_INSPECTED', '')
v('MA2-038', STATIC, 'Pending mapping visible; release blocked', 'CC037')
v('MA2-039', 'PARTIAL', 'ORIENT steps state scope', 'CC029')
v('MA2-040 MA2-041 MA2-042', 'NOT_VERIFIED_RUNTIME', 'Session save/resume code partially read; HostActivityRecreationTest passed at e5b5d3c0 (not at target)')
v('MA2-043', 'PARTIAL', 'Raw markup and jargon', 'CC023;CC029')
v('MA2-044', 'PARTIAL', 'Book tasks explicit; four companion steps share one generic body')
v('MA2-045 MA2-046', 'UNMET', 'Remediation omits checked content', 'CC021;CC023;CC034')
v('MA2-047 MA2-048 MA2-049', 'NOT_VERIFIED_RUNTIME', 'Bookmarks/notes surfaces not executed')
v('MA2-050', 'PARTIAL', 'Tutor queue exists; recordings absent in v2', 'CC002;CC038')
v('MA2-051 MA2-052 MA2-053 MA2-054', 'UNMET', 'Listening/Pronunciation/Handwriting disabled; no audio path', 'CC002')
v('MA2-055 MA2-056', 'PARTIAL', 'Labs exist over reached items; content unreviewed')
v('MA2-057', STATIC, 'Mixed review uses completed units')
v('MA2-058', 'PARTIAL', 'Labs include the current unit before its teaching is complete')
v('MA2-059 MA2-060', STATIC, 'No scoring of speech; no ASR')
v('MA2-061', 'UNMET', 'Keyed mistranslation; ambiguous key; positional skew', 'CC001;CC024;CC026')
v('MA2-062', 'PARTIAL', 'Feedback shown; rationale for chosen option not rendered', 'CC034')
v('MA2-063', 'SATISFIED_TRIVIALLY', 'No transliteration content exists', 'CC038')
v('MA2-064 MA2-065 MA2-066', 'NOT_APPLICABLE_V2_NO_AUDIO', 'v2 has no playback/recording path')
v('MA2-067 MA2-121', STATIC, 'Local reference search (title/summary/detail/searchTerms)', 'CC045')
v('MA2-068', 'UNMET', 'Reference breadth', 'CC017')
v('MA2-069', 'UNMET', 'Records pending final audit; letter-name reference misattributed', 'CC035')
v('MA2-070', 'NOT_VERIFIED_RUNTIME', '')
v('MA2-071', 'PARTIAL', 'ref-ca-* entries title=summary=detail', 'CC045')
v('MA2-072 MA2-073', STATIC, 'No media browser')
v('MA2-074 MA2-075 MA2-076 MA2-077 MA2-078 MA2-079 MA2-080 MA2-081', STATIC, 'Companion steps self-attested and optional')
v('MA2-082', 'UNMET', 'Demonstrated-check evidence not diagnostic', 'CC026;CC027;CC036')
v('MA2-083', STATIC, 'V2ObjectiveState labels')
v('MA2-084', 'PARTIAL', 'Two-option checks dominate', 'CC036')
v('MA2-085 MA2-086', 'NOT_VERIFIED_RUNTIME', '')
v('MA2-087', 'UNMET', 'Foundation checks are glyph matching', 'CC002;CC027')
v('MA2-088', 'UNMET', 'Due review is a fixed replay', 'CC020')
v('MA2-089 MA2-090 MA2-091', STATIC, 'Disclaimers present; no streaks/leaderboards observed')
v('MA2-092', 'UNMET', 'Letter names lack provenance; Kotlin learner text without provenance', 'CC035;CC043')
v('MA2-093', 'PARTIAL', 'Rights recorded; share-alike unconfirmed')
v('MA2-094 MA2-099', 'UNMET_PENDING_CHAIN', 'All records IMPLEMENTED_PENDING_FINAL_AUDIT')
v('MA2-095 MA2-096 MA2-097 MA2-098', 'NOT_APPLICABLE_NO_AUDIO_SHIPPED', 'audioAssets=0')
v('MA2-100 MA2-101', STATIC, 'Digest invalidation implemented (static)')
v('MA2-102 MA2-103', 'UNMET', 'Source-label recall treated as curriculum', 'CC011;CC026')
v('MA2-104 MA2-105', 'PARTIAL', 'Sources/rights per record; external fidelity unverifiable', 'CC035')
v('MA2-106 MA2-107', STATIC, 'No ND/NC sources in pack')
v('MA2-108', 'UNMET_RELEASE_BLOCKED', 'Share-alike confirmation not recorded')
v('MA2-109', STATIC, 'Offline notices and cc-by-sa-4.0.txt packaged')
v('MA2-110 MA2-111', STATIC, 'No scans/mirrors as rights basis; Penguin excluded')
v('MA2-112', STATIC, 'Today/Course/Practice/Reference/Progress')
v('MA2-113 MA2-114 MA2-115 MA2-117 MA2-118 MA2-119 MA2-120', 'NOT_VERIFIED_RUNTIME', 'Static partial reads only', 'CC040;CC041')
v('MA2-116', 'PARTIAL', 'Non-lazy Column for session/unit/practice/saved-work')
v('MA2-122', STATIC, 'Dark default; Follow system available')
v('MA2-123 MA2-125 MA2-127 MA2-128 MA2-130 MA2-131', 'NOT_VERIFIED_DEVICE', 'Requires device/screenshot evidence')
v('MA2-124', 'NOT_VERIFIED_DEVICE', 'heightIn(min=48dp) rows observed; inner controls unverified', 'CC019')
v('MA2-126', 'UNMET_STATIC', 'Unlabelled radio/checkbox controls', 'CC019')
v('MA2-129', 'UNMET_STATIC', 'Insets zeroed under edge-to-edge', 'CC018')
v('MA2-132', 'NOT_VERIFIED_DEVICE', '')
v('MA2-133 MA2-139', STATIC, 'SAF export of governed document and recordings; saved-status text')
v('MA2-134 MA2-135 MA2-136 MA2-137 MA2-138', 'SATISFIED_STATIC_UNEXECUTED', 'V2BackupFormat bounded validation before mutation; preview includes review records', 'CC013;CC014')
v('MA2-140', 'PARTIAL', 'Some composition/main work', 'CC041')
v('MA2-141', 'NOT_VERIFIED_RUNTIME', '')
v('MA2-142', STATIC, 'Room as learner source of truth')
v('MA2-143', STATIC, 'Migrations 1->11, schemas 1-11; DatabaseMigrationTest 7/7 passed at e5b5d3c0 (app tree identical to target)')
v('MA2-144', 'NOT_VERIFIED_RUNTIME', '', 'CC047')
v('MA2-145', STATIC, 'Strict loader and full validation at startup; failure message state')
v('MA2-146', STATIC, 'Streams with bounded buffers')
v('MA2-147', STATIC, 'verifyReleaseGateAttachment ran in both retained scopes; ReleaseOutputTasksTest passed at e5b5d3c0')
v('MA2-148 MA2-149', 'UNMET_PENDING_CHAIN', 'Review/mapping records pending')
v('MA2-150', 'PARTIAL', 'Dangling fragments/tautologies in teaching', 'CC023')
v('MA2-151', STATIC, 'No broken links; 8 orphan examples, 14 optional lab-only items')
v('MA2-152', 'PARTIAL', 'Validator broad; tuple pinning partial', 'CC008')
v('MA2-153', STATIC, 'Validator does not substitute for chain (code comments/status)')
v('MA2-154 MA2-155 MA2-156 MA2-157 MA2-158', STATIC, 'No excluded features observed')
v('MA2-159 MA2-160 MA2-161 MA2-162 MA2-163', 'NOT_VERIFIED_PROCESS', 'Process requirements; history not audited end to end')
v('MA2-164', 'UPHELD_CURRENTLY', 'Candidate not frozen; artifact NOT_RECORDED')
v('MA2-165 MA2-166 MA2-167 MA2-168 MA2-169 MA2-170 MA2-171 MA2-173', 'NOT_VERIFIED_PHYSICAL_OUTSTANDING_LAST', 'Physical acceptance outstanding and last')
v('MA2-172', 'UNMET_PENDING_CHAIN', 'Review-chain records absent')
v('MA2-174', 'UPHELD_CURRENTLY', 'No owner release approval exists')
v('MA2-175 MA2-176', STATIC, 'Sessions subdivide scope; reviews allowed')
v('MA2-177', 'UNMET', 'Analysed early case; no reviewer approvals of chunks', 'CC031')
v('MA2-178', 'UNMET', 'Variance units hard-coded as activated before final audit (changed from original)', 'CC048')
AM = {'AM1-01': ('UPHELD_CURRENTLY', 'This return is diagnostic and is not FINAL_CURRICULUM_AUDIT_PASSED', ''),
      'AM1-02': ('NOT_APPLICABLE_NO_ARTIFACT', 'No audit/final-curriculum-audit artifact exists or is created', ''),
      'AM1-03': ('NOT_VERIFIED_RUNTIME', 'Path normalization code not executed', ''),
      'AM1-04': ('NOT_APPLICABLE_NO_ARTIFACT', '', ''),
      'AM1-05': ('NOT_VERIFIED_RUNTIME', 'Stage-sequence validator not executed', ''),
      'AM1-06': (STATIC, 'All records IMPLEMENTED_PENDING_FINAL_AUDIT with empty stages', ''),
      'AM1-07': (STATIC, 'Digest invalidation implemented (static)', ''),
      'AM1-08': (STATIC, 'Unit digest isolates unit document (static)', ''),
      'AM1-09': ('UNMET_PENDING_CHAIN', 'No owner acceptance', ''),
      'AM1-10': (STATIC, 'Reviewer fields null', ''),
      'AM1-11': ('UNMET_PENDING_CHAIN', 'Retained source bytes not in repo for external spans', 'CC035'),
      'AM1-12': ('UNMET_PENDING_CHAIN', 'finalAudit fields empty', ''),
      'AM1-13': (STATIC, 'No approval inferred', ''),
      'AM1-14': (STATIC, 'Penguin not packaged; private PDF kept outside deliverables', ''),
      'AM1-15': ('UNMET_PENDING_OWNER', 'Mappings pending', 'CC033'),
      'AM1-16': ('UNMET', 'Units 27/29/30 hard-coded activated before final audit', 'CC048'),
      'AM1-17': (STATIC, 'Roles documented', '')}
rows = []
for rid, r in origm.items():
    if rid.startswith('AM1'):
        verdict, ev, fids = AM[rid]; lim = ''
    else:
        verdict, ev, fids, lim = V.get(rid, ('NOT_VERIFIED_NOT_INSPECTED', 'Not specifically inspected in this recheck', '', ''))
    rows.append(dict(requirement_id=rid, requirement_summary=clauses.get(rid, r['requirement_summary']), evidence_paths=ev,
                     inspection_method='Independent static reading at %s; book visual reading; mechanical scripts; no execution' % TARGET[:8],
                     verdict=verdict, original_verdict=r['verdict'], limitations=(lim + '; ' if lim else '') + 'Static/diagnostic only; not acceptance; no runtime, device or qualified-review evidence.',
                     finding_ids=fids))
wcsv('REQUIREMENTS_MATRIX.csv', rows, ['requirement_id', 'requirement_summary', 'evidence_paths', 'inspection_method', 'verdict', 'original_verdict', 'limitations', 'finding_ids'])
req_counts = collections.Counter(r['verdict'] for r in rows)

# ---------------- APP_ENGINEERING_AUDIT.csv ----------------
SURF = {
 'startup': ('STATIC_PARTIAL', 'V2StartupHost loads pack on IO; strict decode + full validation each start; failure message', 'CC041'),
 'onboarding': ('DEFECT_STATIC', 'remember not rememberSaveable', 'CC040'),
 'local-data': ('STATIC_PARTIAL', 'retryOpen path; LearnerReloadRecoveryTest passed at e5b5d3c0', ''),
 'today': ('NOT_VERIFIED', 'not executed', ''),
 'course': ('STATIC_PARTIAL', 'status text contradictions', 'CC037'),
 'unit': ('STATIC_PARTIAL', 'gate rule display read partially', ''),
 'session': ('DEFECT_STATIC', 'unlabelled option controls; raw step.kind header; same feedback for wrong answers', 'CC019;CC029;CC034'),
 'question': ('DEFECT_STATIC', 'presentation permutation residual skew; non-diagnostic items', 'CC001;CC027'),
 'remediation': ('DEFECT_STATIC', 'remediation omits checked content', 'CC021;CC023'),
 'cumulative-review': ('DEFECT_STATIC', 'fixed two-slot replay; hard-coded learner text', 'CC020;CC043'),
 'practice': ('STATIC_PARTIAL', 'labs include current unit pre-teaching', ''),
 'alphabet': ('DEFECT_STATIC', 'no audio; englishNote unrendered; transliteration no-op', 'CC002;CC038'),
 'reading': ('DEFECT_STATIC', 'printed case matching only', 'CC002;CC027'),
 'listening': ('UNMET_DISCLOSED', 'disabled offer V2_MAPPED_AUDIO_ABSENT', 'CC002'),
 'pronunciation': ('UNMET_DISCLOSED', 'disabled offer; no recording path in v2', 'CC002;CC038'),
 'grammar': ('STATIC_PARTIAL', 'lab explanation panel present; unlabelled radio rows', 'CC019'),
 'vocabulary': ('STATIC_PARTIAL', '', ''),
 'mixed': ('STATIC_PARTIAL', 'completed units only', ''),
 'saved-work': ('STATIC_PARTIAL', 'V2SavedWorkScreen read', ''),
 'reference-search': ('DEFECT_STATIC', 'search over title/summary/detail/searchTerms; ID search terms', 'CC017;CC045'),
 'reference-entry': ('DEFECT_STATIC', 'breadth; title=summary=detail', 'CC017;CC045'),
 'progress': ('DEFECT_STATIC', 'evidence computed in composition', 'CC041'),
 'settings': ('STATIC_PARTIAL', 'transliteration toggle has no content effect', 'CC038'),
 'appearance': ('STATIC_PARTIAL', 'AppearanceStoreTest passed at e5b5d3c0', ''),
 'attributions': ('STATIC_PARTIAL', 'V2OfflineNotices read; 37 sources listed; cc-by-sa-4.0.txt packaged', ''),
 'backup-export': ('STATIC_PARTIAL', 'writeGovernedBackup bounded output; documentReference label', ''),
 'backup-preview': ('SATISFIED_STATIC_UNEXECUTED', 'CC014 review keys counted; preview on Main.immediate', 'CC014;CC041'),
 'backup-confirm': ('STATIC_PARTIAL', 'replace after validation; recording-name guard', ''),
 'notes': ('NOT_VERIFIED', '', ''), 'bookmarks': ('NOT_VERIFIED', '', ''), 'tutor-queue': ('NOT_VERIFIED', '', 'CC038'),
 'book-work': ('STATIC_PARTIAL', 'self-attestation labelled', ''), 'anki-binder': ('STATIC_PARTIAL', 'companion steps share one body', 'CC029'),
 'privacy': ('STATIC_PARTIAL', 'no network code; RECORD_AUDIO unused', 'CC038'),
 'microphone': ('NOT_APPLICABLE_V2', 'v2 never records', 'CC038'),
 'audio-integrity': ('NOT_APPLICABLE_V2', 'no audio shipped', ''),
 'file-security': ('SATISFIED_STATIC_UNEXECUTED', 'allow-listed entry names; safe ids; canonical evidence paths', 'CC013'),
 'persistence': ('STATIC_PARTIAL', 'Room v11 migrations complete; DatabaseMigrationTest passed at e5b5d3c0', 'CC047'),
 'write-coordination': ('STATIC_PARTIAL', 'RuntimeCorrectionTest passed at e5b5d3c0; not read in full', ''),
 'lifecycle': ('NOT_VERIFIED', 'device required', 'CC040'),
 'performance': ('NOT_VERIFIED', '20 MB pack parse + full validation at every start unmeasured', 'CC041'),
 'accessibility': ('DEFECT_STATIC', 'insets and control semantics', 'CC018;CC019'),
 'compatibility': ('NOT_VERIFIED', 'targetSdk 36 edge-to-edge', 'CC018'),
 'supply-chain': ('DEFECT_STATIC', 'wrapper checksum/verification metadata absent', 'CC042'),
 'release-gate': ('SATISFIED_STATIC_FAIL_CLOSED', 'read in full; weak audio criterion; v1 coupling', 'CC039'),
 'source-binding': ('DEFECT_STATIC', '4/9 tuple pins; self-authored spans', 'CC008;CC035'),
 'content-loader': ('STATIC_PARTIAL', 'sourceTextPool expansion verified mechanically (438 entries, 0 unused)', ''),
 'legacy-ui': ('DEFECT_STATIC', 'unreachable legacy UI still tested', 'CC044'),
}
rows = []
for r in rcsv(os.path.join(ORIG, 'APP_ENGINEERING_AUDIT.csv')):
    verdict, ev, fids = SURF.get(r['surface_id'], ('NOT_VERIFIED', '', ''))
    rows.append(dict(r, original_verdict=r['verdict'], verdict=verdict, inspected='STATIC_READ (no execution)',
                     inspection_method='Independent source reading at %s; retained raw runtime scopes consulted separately' % TARGET[:8],
                     evidence=ev, finding_ids=fids, limitations='No Gradle/Robolectric/emulator/device execution; Android SDK host blocked'))
wcsv('APP_ENGINEERING_AUDIT.csv', rows, ['domain', 'surface_id', 'feature_or_behavior', 'requirement_ids', 'implementation_paths', 'positive_scenario',
     'negative_scenario', 'inspection_method', 'inspected', 'verdict', 'original_verdict', 'evidence', 'limitations', 'finding_ids'])

# ---------------- GAP_CLOSURE.csv ----------------
G = {
 'G01': ('Original ZIP 0321d323 verified: 42 entries CRC OK, internal SHA256SUMS 41/41; all members resolve byte-exact', 'CLOSED_IDENTITY', 'none for identity'),
 'G02': ('All 626 items, 821 examples, 58 capabilities present and reviewed (items/examples read; capabilities mechanical + targeted)', 'SUBSTANTIALLY_ADDRESSED', 'capability third-party fidelity unverifiable'),
 'G03': ('Sources/rights/references read in full; provenance hashes recomputed; tuple pins re-read', 'PARTIAL', 'external snapshots absent; Wikipedia unreachable; 7036 provenance records not semantically read'),
 'G04': ('17 findings re-adjudicated against raw at target', 'CLOSED_RECONCILIATION; FINDINGS_REMAIN', 'no closure without runtime/qualified review'),
 'G05': ('App main sources partially read (see REPOSITORY_FILE_COVERAGE.csv)', 'PARTIAL', 'most test bodies, legacy code, historical evidence bodies uninspected'),
 'G06': ('All 30 lessons, tables, spelling rules, glossary, index and back cover read; vocabulary/key sampled', 'PARTIAL', 'PDF 1-12, 402-428, 431, 434-438, 441-463, 466-512 unread'),
 'G07': ('Retained raw scopes validated; independent execution attempted', 'NOT_EXECUTED_UNAVAILABLE', 'dl.google.com 403; no SDK; no emulator/device'),
 'G08': ('75 targets, 18 IA, 64 histories, 66 archives, 167 members, 155 index re-checked', 'SUBSTANTIALLY_ADDRESSED_STATIC', 'replay/restore not executed; oldUnit7 cause UNCONFIRMED'),
 'G09': ('Model identity via get_session: claude-opus-5-5 (configured and last served)', 'RECORDED', 'not GPT-6.1 Sol/High; different reviewer'),
 'G10': ('No physical work', 'OUTSTANDING_LAST', 'Galaxy S26 Ultra airplane-mode/reboot acceptance'),
}
rows = []
for r in rcsv(os.path.join(ORIG, 'GAP_CLOSURE.csv')):
    work, disp, lim = G[r['gap_id']]
    rows.append(dict(r, original_disposition=r['disposition'], work_actually_performed=work, disposition=disp, remaining_limit=lim,
                     evidence='This return; safe_evidence/'))
wcsv('GAP_CLOSURE.csv', rows, ['gap_id', 'prior_gap', 'inputs_now_available', 'work_actually_performed', 'evidence', 'original_disposition', 'disposition', 'remaining_limit', 'next_owner'])

# ---------------- IMPLEMENTATION_WORK_ORDER.csv ----------------
rows = []
for r in rcsv(os.path.join(ORIG, 'IMPLEMENTATION_WORK_ORDER.csv')):
    c = FD.ORIGINAL[r['finding_id']]
    rows.append(dict(r, current_disposition=c['disposition'] + ' / ' + c['status'], severity=c['sev'], bounded_correction=r['bounded_correction'] + ' | Recheck note: ' + c['remaining'], independent_recheck_needed='YES'))
for n in FD.NEW:
    rows.append(dict(finding_id=n['id'], severity=n['sev'], current_disposition='NEW / OPEN', exact_entities_and_paths=n['content'] + ' | ' + n['path'],
                     authority_and_source_constraints=n['req'] + '; no Penguin prose; no generated Russian', bounded_correction=n['fix'],
                     positive_control=n['test'], negative_regression=n['test'], history_and_identity_effects='Governed byte changes invalidate digests/assignments per AM1',
                     prerequisites_or_blockers='Qualified review / owner acceptance where content changes', executor='Codex/Cursor per AM1 roles', independent_recheck_needed='YES'))
wcsv('IMPLEMENTATION_WORK_ORDER.csv', rows, ['finding_id', 'severity', 'current_disposition', 'exact_entities_and_paths', 'authority_and_source_constraints',
     'bounded_correction', 'positive_control', 'negative_regression', 'history_and_identity_effects', 'prerequisites_or_blockers', 'executor', 'independent_recheck_needed'])

# ---------------- safe_evidence/HISTORICAL_IA_RECHECKS.csv ----------------
os.makedirs(os.path.join(OUT, 'safe_evidence'), exist_ok=True)
IA = {
 'IA001': ('STATIC_RETAINED', 'V2LabScreen shows V2QuestionCue and explanation panel (read)'),
 'IA002': ('STATIC_RETAINED', 'V2CompletionEvidence recomputes completion from governed evidence (read in full)'),
 'IA003': ('GATE_ADDRESSED_COVERAGE_DEFECT_CONFIRMED', 'Due-review gate present; review is a fixed two-slot replay (CC020); 64 histories structurally clean'),
 'IA004': ('OPEN', 'CC002'),
 'IA005': ('STATIC_RETAINED', 'u4 nominative-role tasks separate; constant key noted (CC027)'),
 'IA006': ('NOT_INDEPENDENTLY_RECHECKED', 'Renderer CUE-role handling observed for u5 cues; full flag audit not repeated'),
 'IA007': ('STATIC_RETAINED', 'Unit 5 indeclinable language read'),
 'IA008': ('STATIC_RETAINED_WITH_CONCERN', 'Options/feedback complete; rationales not rendered (CC034)'),
 'IA009': ('CHANGED_NOT_RESOLVED', 'All teaching read; scripted keys, withheld rules, raw markup (CC021-CC023)'),
 'IA010': ('STATIC_RETAINED', 'u29 active time labels'),
 'IA011': ('STATIC_RETAINED', 'u29 short-adjective checks genuine'),
 'IA012': ('CHANGED_NOT_RESOLVED', 'Comma tasks display their commas (CC027)'),
 'IA013': ('STATIC_RETAINED_WITH_CONCERN', 'Initialism items correct; letter names only А/Б/В duplicating Unit 1 (CC030/CC032)'),
 'IA014': ('STATIC_RETAINED', 'Directionality correction; bringing scope CC007 open'),
 'IA015': ('CHANGED_NOT_RESOLVED', 'CC008 narrowed to 5 unpinned records'),
 'IA016': ('CHANGED_NOT_RESOLVED', 'CC010 8 groups + CC030'),
 'IA017': ('STATIC_RETAINED_WITH_CONCERN', 'Notices bounded; CC017 breadth open; CC045'),
 'IA018': ('STATIC_RETAINED', 'Export counts from serialized document (writeGovernedBackup read)'),
}
rows = []
for r in rcsv(os.path.join(ORIG, 'safe_evidence', 'HISTORICAL_IA_RECHECKS.csv')):
    d, ev = IA[r['historical_id']]
    rows.append(dict(r, independent_disposition_2026_10_22=d, independent_evidence=ev, runtime_status_2026_10_22='NOT_EXECUTED (unavailable)', approval='NONE'))
wcsv('HISTORICAL_IA_RECHECKS_2026_10_22.csv', rows, ['historical_id', 'prior_diagnostic_disposition', 'current_recheck', 'runtime_status',
     'independent_disposition_2026_10_22', 'independent_evidence', 'runtime_status_2026_10_22', 'approval'], os.path.join(OUT, 'safe_evidence'))

# ---------------- REPOSITORY_FILE_COVERAGE.csv ----------------
files = subprocess.run(['git', '-C', T, 'ls-files'], capture_output=True, text=True).stdout.split('\n')
files = [f for f in files if f]
FULL = {'app/src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt', 'app/src/main/java/dev/russiancompanion/release/ReleaseGate.kt',
        'app/src/main/java/dev/russiancompanion/v2/curriculum/V2CompletionEvidence.kt', 'app/src/main/java/dev/russiancompanion/v2/ui/V2PracticeFlows.kt',
        'app/src/main/java/dev/russiancompanion/v2/curriculum/OriginalApplicationTuplePins.kt', 'app/src/main/java/dev/russiancompanion/presentation/MainActivity.kt',
        'app/src/main/AndroidManifest.xml', 'docs/governance/MASTER_AUTHORITY_v2.0.md', 'docs/governance/MASTER_AUTHORITY_v2.0_AMENDMENT_1.md',
        'app/src/main/java/dev/russiancompanion/v2/notices/V2OfflineNotices.kt', 'app/src/main/java/dev/russiancompanion/v2/ui/V2Routes.kt',
        'release/acceptance/candidate.json', 'AGENTS.md'}
PART = {'app/src/main/java/dev/russiancompanion/v2/curriculum/V2Validator.kt': 'lines 109-115, 470-480, 1495-1600, 2035-2037, 5575-5617 plus greps',
        'app/src/main/java/dev/russiancompanion/v2/backup/V2BackupFormat.kt': 'outline + lines 496-935',
        'app/src/main/java/dev/russiancompanion/v2/curriculum/V2GovernedIdentity.kt': 'lines 133-140, 440-490',
        'app/src/main/java/dev/russiancompanion/data/local/DatabaseMigrations.kt': 'migration list + lines 436-458',
        'app/src/main/java/dev/russiancompanion/data/local/LocalStore.kt': 'lines 2285-2330 + recording-name guard diff',
        'app/src/main/java/dev/russiancompanion/v2/ui/V2Screens.kt': 'session/option rows, 680-760, 820-851',
        'app/src/main/java/dev/russiancompanion/v2/ui/V2NavHost.kt': 'lines 180-232', 'app/src/main/java/dev/russiancompanion/data/audio/AndroidPracticeAudio.kt': 'lines 1-80',
        'app/src/main/java/dev/russiancompanion/v2/curriculum/V2ProgressRules.kt': 'lines 330-420', 'app/src/main/java/dev/russiancompanion/RussianCompanionApplication.kt': 'lines 1-80',
        'app/src/main/java/dev/russiancompanion/v2/ui/V2App.kt': 'startup/writes (earlier context)', 'app/src/main/java/dev/russiancompanion/v2/ui/V2Chrome.kt': 'scaffold/insets',
        'app/src/main/java/dev/russiancompanion/v2/ui/V2BackupCoordinator.kt': 'restorePreview/export', 'app/src/main/java/dev/russiancompanion/v2/curriculum/V2PackLoader.kt': 'loader',
        'app/src/test/java/dev/russiancompanion/v2/HostActivityLifecycleTest.kt': 'class declarations/methods', 'docs/governance/OWNER_VARIANCE_DECISION_PACKET.md': 'grep of activation/status lines',
        'app/src/main/res/values/strings.xml': 'count + grep', 'app/build.gradle.kts': 'build configuration', 'gradle/wrapper/gradle-wrapper.properties': 'wrapper',
        '.github/workflows/build.yml': 'CI steps'}
hostidx = {r['target_source_file'] for r in rcsv(os.path.join(DL, 'audit/gpt61-sol-high-comprehensive-2026-10-19/final-independent-recheck-2026-10-22/HOST_METHOD_EVIDENCE_INDEX.csv'))}
hist = {r['path'] for r in rcsv(os.path.join(ORIG, 'safe_evidence', 'historical_lifecycle_review.csv'))}
arch = set(json.load(open(os.path.join(S, 'work', 'archives_check.json')))['archives'] and [a['path'] for a in json.load(open(os.path.join(S, 'work', 'archives_check.json')))['archives']])
c155 = {x['path'] for x in json.load(open(os.path.join(DL, 'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-source-binding-reconciliation-2026-10-22/CONSERVATION.json')))['current155Files']}
RUNTIME = {'audit/gpt61-sol-high-comprehensive-2026-10-19/post-recording-guard-host-regression-2026-10-22/RAW_REGRESSION.zip',
           'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-source-binding-reconciliation-2026-10-22/RAW_REGRESSION.zip',
           'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-pending-evidence-reconciliation/HISTORICAL_GENERATED_EVIDENCE.zip',
           'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-source-binding-reconciliation-2026-10-22/HISTORICAL_PRIOR_CURRENT.zip'}
def role(p):
    if p.startswith('app/src/main/assets/content/'): return 'curriculum/content asset'
    if p.startswith('app/src/main/assets/'): return 'packaged asset'
    if p.startswith('app/src/main/'): return 'production source'
    if p.startswith('app/src/test/'): return 'test source'
    if p.startswith('app/schemas/'): return 'Room schema'
    if p.startswith('audit/'): return 'audit evidence'
    if p.startswith('review/'): return 'review packet'
    if p.startswith('docs/'): return 'documentation/governance'
    if p.startswith('catalog/'): return 'legacy catalog'
    if p.startswith('release/'): return 'release evidence'
    return 'build/config/other'
rows = []; cov = collections.Counter()
for p in files:
    lim = ''
    if p == 'app/src/main/assets/content/v2-curriculum.json':
        st, m = 'READ_SEMANTIC_ALL_UNITS + MECHANICAL', 'All 30 units rendered and read; entities mechanically validated (see CURRICULUM_ENTITY_COVERAGE.csv)'; lim = 'Units 17-30 compact render; provenance not semantically read'
    elif p in FULL: st, m = 'READ_FULL', 'Read in full'
    elif p in PART: st, m = 'READ_PARTIAL', PART[p]
    elif p in hist: st, m = 'MECHANICAL_STRUCTURAL', 'History JSON: digests recomputed, links/orders checked (safe_evidence/histories_check.json)'
    elif p in arch: st, m = 'MECHANICAL_HASH_CRC', 'sha256 vs conservation manifest + zip CRC (safe_evidence/archives_check.json)'
    elif p in RUNTIME: st, m = 'MECHANICAL_EXTRACTED_VALIDATED', 'Extracted; member hashes and JUnit XML counts validated'
    elif p in c155: st, m = 'MECHANICAL_HASH', 'newSha256 matches; digest-only substitution verified'
    elif p in hostidx: st, m = 'IDENTITY_ONLY_SHA_AND_DECLARATION_LINES', 'Host index sha and method lines verified; bodies not reviewed'
    elif p in ('app/schemas/dev.russiancompanion.data.local.AppDatabase/10.json', 'app/schemas/dev.russiancompanion.data.local.AppDatabase/11.json'):
        st, m = 'MECHANICAL_DIFF', 'Schema 10->11 entity diff'
    elif p.startswith('audit/gpt61-sol-high-comprehensive-2026-10-19/finish-results/'): st, m = 'MECHANICAL_VERIFIED_ORIGINAL_RETURN', 'Original ZIP and members verified'
    else: st, m = 'NOT_INSPECTED', ''
    cov[st] += 1
    rows.append(dict(path=p, role=role(p), inspection_status=st, inspection_method=m, evidence='safe_evidence/' if st != 'NOT_INSPECTED' else '',
                     limitations=lim or ('Uninspected in this recheck' if st == 'NOT_INSPECTED' else ''),
                     finding_ids=''))
wcsv('REPOSITORY_FILE_COVERAGE.csv', rows, ['path', 'role', 'inspection_status', 'inspection_method', 'evidence', 'limitations', 'finding_ids'])

# ---------------- CURRENT_INPUT_SUPPLEMENT.csv ----------------
diff = subprocess.run(['git', '-C', DL, 'diff', '--name-status', TARGET, DELIV], capture_output=True, text=True).stdout.strip().split('\n')
rows = []
for line in diff:
    stt, p = line.split('\t', 1)
    fp = os.path.join(DL, p)
    b = open(fp, 'rb').read()
    blob = subprocess.run(['git', '-C', DL, 'rev-parse', '%s:%s' % (DELIV, p)], capture_output=True, text=True).stdout.strip()
    review = {'COPY_PASTE_PROMPT.md': 'READ_FULL', 'REQUIRED_RETURNS.md': 'READ_FULL', 'README.md': 'READ_FULL', 'MECHANICAL_VERIFICATION.json': 'READ_FULL',
              'POINTER_CHANGE.patch': 'READ_FULL (additive pointer text only)', 'HOST_METHOD_EVIDENCE_INDEX.csv': 'MECHANICAL_ALL_ROWS (outcomes vs raw; sha/lines vs target)',
              'SHA256_PREPARATION.json': 'MECHANICAL (14 entries match)', 'VERIFY_INPUT_INVENTORIES.py': 'EXECUTED -I (exit 0; printed booleans hard-coded)',
              'TARGET_FILE_INVENTORY.csv': 'MECHANICAL (8596 rows = git ls-files at target)'}.get(os.path.basename(p), 'IDENTITY_ONLY')
    if p in ('AGENTS.md',) or p.endswith('CODEX_CHECKPOINT_2026-10-12.md') or p.endswith('CODEX_ENGINEERING_CONTINUATION.md'):
        review = 'DIFF_READ (additive retarget paragraph only)'
    rows.append(dict(path=p, change=stt, delivery_commit=DELIV, git_blob=blob, sha256=hashlib.sha256(b).hexdigest(), bytes=len(b), independent_review_status=review))
rows.append(dict(path='(branch head check)', change='NONE', delivery_commit=DELIV, git_blob='', sha256='', bytes='',
                 independent_review_status='origin/cursor/content-audit-corrections-964f fetched 2026-10-22: head = ae1105fb; 0 commits after delivery'))
wcsv('CURRENT_INPUT_SUPPLEMENT.csv', rows, ['path', 'change', 'delivery_commit', 'git_blob', 'sha256', 'bytes', 'independent_review_status'])
json.dump({'requirements': req_counts, 'fileCoverage': cov, 'files': len(files)}, open(os.path.join(S, 'work', 'part3_stats.json'), 'w'), indent=1)
print('part3 ok', dict(req_counts), dict(cov), len(files))
