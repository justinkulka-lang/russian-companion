#!/usr/bin/env python3
"""Build a pinned read-only audit packet from tracked Git blobs, never local secrets/builds."""
import argparse,csv,hashlib,io,json,subprocess,sys,zipfile
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
DEFAULT_TARGET='ebd71fd0d1226a5ce3ec9a310bb3638c3bc34fb9'
sys.path.insert(0,str(ROOT/'catalog'))
from curriculum_storage import loads

def git(*args):return subprocess.check_output(['git','-C',str(ROOT),*args])
def digest(b):return hashlib.sha256(b).hexdigest()
def main():
 ap=argparse.ArgumentParser();ap.add_argument('--target',default=DEFAULT_TARGET);ap.add_argument('--output',type=Path,required=True);args=ap.parse_args()
 target=git('rev-parse',args.target+'^{commit}').decode().strip();entries={};inventory=[]
 def put(name,b):
  assert not name.startswith('/') and '..' not in name.split('/') and name not in entries
  entries[name]=b.encode() if isinstance(b,str) else b
 def jput(name,obj):put(name,json.dumps(obj,ensure_ascii=False,indent=2)+'\n')
 for row in git('ls-tree','-rz','--full-tree',target).split(b'\0'):
  if not row:continue
  metadata,path=row.split(b'\t',1);mode,kind,oid=metadata.decode().split();path=path.decode();assert kind=='blob' and mode in ['100644','100755'],(path,kind,mode)
  b=git('cat-file','blob',oid);assert not path.lower().endswith(('.pdf','.zip','.apk','.jks','.keystore')),path
  assert hashlib.sha1(b'blob '+str(len(b)).encode()+b'\0'+b).hexdigest()==oid
  put('repo/'+path,b);inventory.append(dict(path=path,gitMode=mode,gitBlob=oid,bytes=len(b),sha256=digest(b)))
 jput('TRACKED_REPOSITORY_INVENTORY.json',dict(commit=target,files=inventory))
 docroot=Path(__file__).resolve().parent
 for name in ['START_HERE.md','AUDIT_PROMPT.md','DELIVERABLES.md']:put(name,(docroot/name).read_bytes())
 put('PACKET_BUILDER.py',Path(__file__).read_bytes())
 raw=entries['repo/app/src/main/assets/content/v2-curriculum.json'];pack=loads(raw)
 assert len(pack['units'])==30 and len(pack['items'])==586 and len(pack['sourceCapabilities'])==58
 jput('projections/CURRICULUM_EXPANDED.json',pack)
 projection_index=[]
 for u in pack['units']:
  objs={o['id'] for o in u['objectives']};items=[i for i in pack['items'] if i['objectiveId'] in objs];examples=[e for e in pack['examples'] if e.get('objectiveId') in objs];provenance=[r for r in pack['provenanceRecords'] if r.get('unitNumber')==u['number']]
  name=f"projections/units/UNIT_{u['number']:02d}.json";jput(name,dict(derivedFromCommit=target,rawCurriculumSha256=digest(raw),unit=u,items=items,examples=examples,sourceCapabilities=[c for c in pack['sourceCapabilities'] if c['unitNumber']==u['number']],provenanceRecords=provenance))
  projection_index.append(dict(unit=u['number'],path=name,assessmentItems=len(items),provenanceRecords=len(provenance)))
 jput('projections/INDEX.json',dict(rawCurriculumSha256=digest(raw),expansion='catalog/curriculum_storage.py loads; no semantic modifications',globalRecords='CURRICULUM_EXPANDED.json',units=projection_index))
 commits=[('PR64 original baseline','420f41ff6c2a4417cd829fdee0179b6a3b797eff'),('Historical main onboarding','ff539220645670fbf7a1abacaa6f71e828606b80'),('Reconstructed handoff snapshot','b1218041cce417d072d8f86f97b973aa08cfaa31'),('Before Unit27 remaining instruction','e1af6c2cc71e1057d25349a66a2393b211ea34af'),('Unit27 implementation','a1155d8b9cbaeef3e86507fe3f2eebf30819c9df'),('Before Unit28 instruction','b68f619ea6791cfef857fcb62e098f9740940e45'),('Before Unit29 instruction','1fd1c8cfd8cafd673907a5807ed45199d9179fde'),('Before Unit30 instruction','dce9b28a6259e12eea33a5258859cd530775db10'),('Latest implementation ancestor','0136eb4000641f6b1edd020ed9caa29f59cdc35c'),('Latest tested checkpoint','39740d16f8ac078841e9b9c9390e6ac87e6feb7b')]
 histories=[];known={digest(raw):'repo/app/src/main/assets/content/v2-curriculum.json'}
 for label,commit in commits:
  b=git('show',commit+':app/src/main/assets/content/v2-curriculum.json');sha=digest(b)
  if sha not in known:
   known[sha]='history/curriculum-snapshots/'+sha+'.json';put(known[sha],b)
  commitbytes=git('cat-file','commit',commit);assert hashlib.sha1(b'commit '+str(len(commitbytes)).encode()+b'\0'+commitbytes).hexdigest()==commit
  put('history/commit-objects/'+commit+'.txt',commitbytes)
  histories.append(dict(label=label,commit=commit,curriculumPath=known[sha],curriculumSha256=sha,ancestorOfTarget=subprocess.run(['git','-C',str(ROOT),'merge-base','--is-ancestor',commit,target],capture_output=True).returncode==0))
 put('history/COMMITS_AND_PARENTS.txt',git('log','--format=%H %P %cI %s',target))
 put('comparisons/BASELINE_TO_TARGET_STAT.txt',git('diff','--stat',commits[0][1],target))
 put('comparisons/BASELINE_TO_TARGET.patch',git('diff','--binary',commits[0][1],target))
 tested=commits[-1][1];changed=git('diff','--name-only',tested,target).decode().splitlines();assert all(p.startswith('audit/content-corrections/') for p in changed),changed
 put('comparisons/TESTED_TO_AUDIT_TARGET.patch',git('diff',tested,target));jput('comparisons/TESTED_TO_AUDIT_TARGET.json',dict(testedCommit=tested,auditTarget=target,changedPaths=changed,applicationSourceUnchanged=True))
 handoff=ROOT.parent/'artifacts/Russian_Companion_Codex_Handoff_2026-10-12.zip'
 handoff_info=dict(kind='RECONSTRUCTED_NOT_RECOVERED_ORIGINAL',present=handoff.exists())
 if handoff.exists():
  handoff_info['sha256']=digest(handoff.read_bytes());assert handoff_info['sha256']=='046f66597140b74bea0d92511abe7898b507f9e441db512ad1797524d4e28124'
  with zipfile.ZipFile(handoff) as old:
   for name in ['README.md','RESUME_PROMPT.md','MANIFEST.json']:put('history/reconstructed-handoff/'+name,old.read(name))
 jput('HISTORY_INDEX.json',dict(kind='Historical evidence, not current approval or a Git checkout',snapshots=histories,reconstructedHandoff=handoff_info,originalAuditAndFourHistoricalCsvs='NOT_RECOVERED',fullGitObjectDatabaseIncluded=False))
 targets=list(csv.DictReader(io.StringIO(entries['repo/audit/content-corrections/ITEM_TARGET_RECONCILIATION_PENDING.csv'].decode())));assert len(targets)==75
 def csvput(name,header,rows=()):
  buf=io.StringIO(newline='');w=csv.writer(buf);w.writerow(header);w.writerows(rows);put('output_templates/'+name,buf.getvalue())
 csvput('FINDINGS.csv','finding_id,severity,category,unit,content_id,current_path,locator,requirement_id,observed_behavior,expected_behavior,evidence,confidence,proposed_correction,acceptance_test,status'.split(','))
 unit_for_obj={o['id']:u['number'] for u in pack['units'] for o in u['objectives']}
 csvput('ITEM_ASSESSMENT_AUDIT.csv','item_id,unit,objective_id,tested_feature,inspected,verdict,cue_sufficient,answer_unique,distractors_valid,feedback_valid,teaching_support,source_binding,reason,finding_ids'.split(','),[(i['id'],unit_for_obj[i['objectiveId']],i['objectiveId'],i.get('assessment',{}).get('testedFeature',''),'false','NOT_VERIFIED','','','','','','','UNFILLED_TEMPLATE','') for i in pack['items']])
 csvput('UNIT_COVERAGE.csv','unit,objectives_checked,instruction_checked,examples_checked,guided_checked,independent_checked,remediation_checked,prerequisites_checked,mapping_checked,rights_checked,application_behavior_checked,verdict,gaps,finding_ids'.split(','),[(n,*(['NOT_INSPECTED']*10),'NOT_VERIFIED','UNFILLED_TEMPLATE','') for n in range(1,31)])
 csvput('TARGET_75_DISPOSITIONS.csv','correction_id,finding_id,unit,content_id,current_snapshot_comparison,diagnostic_disposition,evidence,new_finding_ids'.split(','),[(t['correction_id'],t['finding_id'],t['unit'],t['content_id'],'','NOT_VERIFIED','UNFILLED_TEMPLATE','') for t in targets])
 csvput('REQUIREMENTS_MATRIX.csv','requirement_id,requirement_summary,evidence_paths,inspection_method,verdict,limitations,finding_ids'.split(','))
 for name in ['INDEPENDENT_AUDIT_REPORT.md','CODEX_RETURN_HANDOFF.md']:put('output_templates/'+name,'# UNFILLED TEMPLATE\n\nNo audit has been performed by this packet. Follow DELIVERABLES.md.\n')
 for name in ['SOURCE_AND_BINDING_REVIEW.json','AUDIT_SCOPE_AND_EVIDENCE.json']:jput('output_templates/'+name,dict(schemaVersion=1,template=True,targetCommit=target,authorityDisposition='DIAGNOSTIC_ONLY',inspectionStatus='NOT_PERFORMED'))
 jput('PACKET_IDENTITY.json',dict(schemaVersion=1,purpose='Independent GPT-6.1 High diagnostic audit; not formal review/release approval',targetRepository='justinkulka-lang/russian-companion',targetBranch='cursor/content-audit-corrections-964f',draftPr=64,auditTarget=target,rawCurriculumSha256=digest(raw),canonicalPackDigest='ec1debe60310f19952a7d4bf676fe18ff6e25b1dfb848c60761d7cd9d40d7ff1',sourceDigest='9bf60851690b6f1c880a25f39c0afece1b5bf87130b45e9a2fc3411c69191467',counts=dict(trackedFiles=len(inventory),units=30,assessmentItems=586,provenanceRecords=5689,sourceCapabilities=58,retainedTargets=75),latestTestedCommit=tested,latestTests=dict(tests=22,failures=0,errors=0,skipped=0,classes=10,completeSuite=False),excludedExporters=['VisualReviewCaptureTest','Units35ChunkCaptureTest','Units610ChunkCaptureTest','Units1115GapCaptureTest'],privateAttachment=dict(filename='Private_Penguin_Reference.pdf',sha256='0c441ffd8855b292922a40b10935d5ffb1b088b7118acfcc1856e259dc01867a',bytes=13572361,included=False),missingEvidence=['Original historical audit and four original CSVs','Current APK identity/signature','Actual Galaxy S26 Ultra airplane-mode/reboot acceptance','Independent environment publication status'],configurationSaved=True,publicationBasis='User confirmed Published',currentArtifact='NOT_RECORDED',release='NOT_APPROVED',outputTemplates='Unfilled; not findings',exclusions='Untracked files, build outputs, caches, credentials, private PDF and private extracted book text',packetConstructionDocsCommit=git('rev-parse','HEAD').decode().strip()))
 verifier='''#!/usr/bin/env python3
import hashlib,json,sys
from pathlib import Path
root=Path(__file__).resolve().parent
manifest=json.loads((root/'MANIFEST.json').read_text())
for row in manifest['files']:
 p=root/row['path'];assert p.is_file(),row['path'];b=p.read_bytes();assert len(b)==row['bytes'] and hashlib.sha256(b).hexdigest()==row['sha256'],row['path']
actual={p.relative_to(root).as_posix() for p in root.rglob('*') if p.is_file()}
expected={r['path'] for r in manifest['files']}|{'MANIFEST.json'}
assert actual==expected,('Unexpected/missing files',actual^expected)
for row in json.loads((root/'TRACKED_REPOSITORY_INVENTORY.json').read_text())['files']:
 b=(root/'repo'/row['path']).read_bytes();assert hashlib.sha1(b'blob '+str(len(b)).encode()+b'\\0'+b).hexdigest()==row['gitBlob'],row['path']
print('Verified',len(manifest['files']),'files; tracked Git blob identities verified. Integrity is not an audit pass.')
'''
 put('VERIFY_PACKET.py',verifier)
 jput('MANIFEST.json',dict(schemaVersion=1,auditTarget=target,manifestSelfExcluded=True,files=[dict(path=n,bytes=len(b),sha256=digest(b)) for n,b in sorted(entries.items())]))
 args.output.parent.mkdir(parents=True,exist_ok=True)
 with zipfile.ZipFile(args.output,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
  for name,b in sorted(entries.items()):
   info=zipfile.ZipInfo(name,date_time=(2026,10,12,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;info.external_attr=0o100644<<16;z.writestr(info,b,compresslevel=9)
 sha=digest(args.output.read_bytes());args.output.with_suffix(args.output.suffix+'.sha256').write_text(sha+'  '+args.output.name+'\n')
 with zipfile.ZipFile(args.output) as z:
  assert z.testzip() is None
  for row in json.loads(z.read('MANIFEST.json'))['files']:assert digest(z.read(row['path']))==row['sha256']
 print(json.dumps(dict(path=str(args.output),bytes=args.output.stat().st_size,sha256=sha,entries=len(entries),trackedFiles=len(inventory),auditTarget=target)))
if __name__=='__main__':main()
