#!/usr/bin/env python3
"""Build a current diagnostic packet exclusively from pinned tracked Git blobs."""
import argparse,csv,hashlib,io,json,subprocess,sys,zipfile
from pathlib import Path
ROOT=Path(__file__).resolve().parents[3]
BASE='audit/independent-gpt61-2026-10-12'
def git(*a):return subprocess.check_output(['git','-C',str(ROOT),*a])
def sha(b):return hashlib.sha256(b).hexdigest()
def main():
 ap=argparse.ArgumentParser();ap.add_argument('--target',required=True);ap.add_argument('--output',required=True,type=Path);a=ap.parse_args();target=git('rev-parse',a.target+'^{commit}').decode().strip();entries={};inventory=[]
 allowlist=json.loads(git('show',target+':'+BASE+'/current-comprehensive-audit/RETAINED_ARCHIVE_ALLOWLIST.json'))['sha256'];archive_inventory=[]
 def put(n,b):
  assert n not in entries and not n.startswith('/') and '..' not in n.split('/');entries[n]=b.encode() if isinstance(b,str) else b
 def jp(n,v):put(n,json.dumps(v,ensure_ascii=False,indent=2)+'\n')
 for row in git('ls-tree','-rz','--full-tree',target).split(b'\0'):
  if not row:continue
  meta,path=row.split(b'\t',1);mode,kind,oid=meta.decode().split();path=path.decode();assert kind=='blob' and mode in ['100644','100755'],path
  assert not path.lower().endswith(('.pdf','.apk','.jks','.keystore','.pem','.env')),path
  # Preserve the original public test-result archive; never add local attachments.
  b=git('cat-file','blob',oid);assert hashlib.sha1(b'blob '+str(len(b)).encode()+b'\0'+b).hexdigest()==oid
  assert not b.startswith(b'%PDF-') and sha(b)!='0c441ffd8855b292922a40b10935d5ffb1b088b7118acfcc1856e259dc01867a',path
  if path.lower().endswith('.zip'):
   assert path in allowlist and sha(b)==allowlist[path],path
   with zipfile.ZipFile(io.BytesIO(b)) as retained:
    members=[]
    for member in retained.infolist():
     assert not member.filename.lower().endswith('.pdf') and 'private_penguin' not in member.filename.lower(),(path,member.filename)
     if not member.is_dir():
      payload=retained.read(member);assert b'%PDF-' not in payload,(path,member.filename);members.append(dict(path=member.filename,bytes=len(payload),sha256=sha(payload)))
    archive_inventory.append(dict(path=path,sha256=sha(b),members=members))
  put('repo/'+path,b);inventory.append(dict(path=path,gitMode=mode,gitBlob=oid,bytes=len(b),sha256=sha(b)))
 raw=entries['repo/app/src/main/assets/content/v2-curriculum.json'];loader=ROOT/'catalog/curriculum_storage.py';assert loader.read_bytes()==entries['repo/catalog/curriculum_storage.py'];sys.path.insert(0,str(loader.parent));from curriculum_storage import loads
 p=loads(raw);assert len(p['units'])==30 and len(p['items'])==622 and len(p['sourceCapabilities'])==58 and len(p['provenanceRecords'])==6943
 result=json.loads(entries['repo/'+BASE+'/broader-regression-2026-10-18/CURRENT_BROAD_RESULTS.json']);assert result['allDiscoveredDebugHostMethodsReported'] and result['eventOutcomeCountsMatchXml'] and result['testCounts']==dict(reported=1225,passed=1208,failed=16,errors=0,skipped=1)
 disposition=json.loads(entries['repo/'+BASE+'/CURRENT_HOST_VERIFICATION_DISPOSITION.json']);assert disposition['engineeringReadyForIndependentDiagnosticReview'] and not disposition['newBroadOpenCases']
 assert disposition['freshAllPassingBroadRunAtCurrentTestSource'] is False and len(disposition['separatePassingFollowups'])==2
 assert [x['counts']['passed'] for x in disposition['separatePassingFollowups']]==[26,31]
 for path in ['app/src/main','catalog','docs','review']:assert git('rev-parse',target+':'+path)==git('rev-parse',disposition['sourceCommit']+':'+path)
 assert git('rev-parse',target+':app/src/test')==git('rev-parse',disposition['sourceCommit']+':app/src/test')
 for n in ['AUDIT_PROMPT.md','DELIVERABLES.md']:
  put(n,entries['repo/'+BASE+'/current-comprehensive-audit/'+n])
 assert len(archive_inventory)==len(allowlist)==66
 jp('RETAINED_ARCHIVE_INVENTORY.json',dict(scope='64 authentic host-generated lifecycle backups plus original aborted and complete failed broad test archives;not physical/human acceptance',archives=archive_inventory))
 commit_object=git('cat-file','commit',target);assert hashlib.sha1(b'commit '+str(len(commit_object)).encode()+b'\0'+commit_object).hexdigest()==target
 root_tree=commit_object.splitlines()[0].decode().split()[1];tree_ids={root_tree}|set(git('ls-tree','-rd','--format=%(objectname)',target).decode().splitlines())
 tree_objects={oid:git('cat-file','tree',oid).hex() for oid in sorted(tree_ids)}
 put('history/TARGET_COMMIT_OBJECT.txt',commit_object);jp('history/TARGET_TREE_OBJECTS.json',tree_objects)
 jp('TRACKED_REPOSITORY_INVENTORY.json',dict(commit=target,rootTree=root_tree,files=inventory));jp('projections/CURRICULUM_EXPANDED.json',p)
 for u in p['units']:
  objs={o['id'] for o in u['objectives']}
  example_ids={eid for o in u['objectives'] for eid in o['exampleIds']}|{eid for session in u['sessions'] for step in session['steps'] for eid in step.get('exampleIds',[])}|{e['id'] for e in p['examples'] if e['objectiveId'] in objs}
  examples=[e for e in p['examples'] if e['id'] in example_ids];assert {e['id'] for e in examples}==example_ids
  jp(f"projections/UNIT_{u['number']:02d}.json",dict(derivedFromCommit=target,rawCurriculumSha256=sha(raw),unit=u,items=[i for i in p['items'] if i['objectiveId'] in objs],examples=examples,exampleOwnership='Union of actual objective/step exampleId references and explicit objectiveId ownership,including retained historical examples;global expanded asset retains all817 examples',provenanceRecords=[r for r in p['provenanceRecords'] if r['unitNumber']==u['number']],sourceCapabilities=[c for c in p['sourceCapabilities'] if c['unitNumber']==u['number']]))
 identity=dict(schemaVersion=1,purpose='Independent comprehensive GPT-6.1 High DIAGNOSTIC_ONLY audit',targetRepository='justinkulka-lang/russian-companion',targetBranch='cursor/content-audit-corrections-964f',draftPr=64,auditTarget=target,rawCurriculumSha256=sha(raw),counts=dict(trackedFiles=len(inventory),units=30,assessmentItems=622,examples=817,sourceCapabilities=58,provenanceRecords=6943,retainedTargets=75),verification=dict(completeFailedBroadRun=result,separateFollowupDisposition=disposition,freshAllPassingBroadRunAtCurrentTestSource=False),privateAttachment=dict(filename='Private_Penguin_Reference.pdf',sha256='0c441ffd8855b292922a40b10935d5ffb1b088b7118acfcc1856e259dc01867a',bytes=13572361,included=False,pinBasis='Verified attachment continuity;independent historical expected hash unavailable'),historicalInputs=dict(originalAuditAndFourCsvs='NOT_RECOVERED',originalNineDiagnostics='repo/'+BASE+'/received-results/',original32Failures='repo/'+BASE+'/broader-regression-2026-10-18/INITIAL/',original64Histories='Retained in tracked correction directories;see continuation/checkpoints'),fullGitObjectDatabaseIncluded=False,qualifiedAcceptance=False,releaseApproval=False,physicalAcceptance='OUTSTANDING_LAST_AFTER_FINAL_INDEPENDENT_AUDIT',outputTemplates='Unfilled scaffolding,not inspection evidence')
 jp('PACKET_IDENTITY.json',identity);put('START_HERE.md',f'''# Current independent comprehensive audit packet\n\nAudit target `{target}`, justinkulka-lang/russian-companion, cursor/content-audit-corrections-964f, Draft PR64. Use GPT-6.1 with High reasoning as selected by the user;this is diagnostic work,not formal acceptance/release approval.\n\nAttach this ZIP and the private PDF separately in a new audit chat. Paste the complete AUDIT_PROMPT.md. Read DELIVERABLES.md for the nine required results. Run `python3 VERIFY_PACKET.py` after extraction;integrity does not prove correctness.\n\nThe pinned tracked snapshot under repo/ contains current implementation,frozen source evidence,all retained correction/history/failure records and the original nine diagnostic deliverables. Projections expand the authoritative raw asset without semantic changes. Current scope:30 units,622 assessment items,58 capabilities,6943 provenance records. Older586-item packet is historical.\n\nComplete real debug-host run at its pinned source: {result['testCounts']}. This is a failed broad run. The16observed cases have separate scoped engineering reconciliation:26passing methods/3classes,then31passing methods/12classes at separately pinned sources,with unchanged main/catalog/docs/review and all other test bytes outside reviewed methods/helpers. There is no fresh all-pass broad run at the final test source. Do not sum scopes,convert historical failures to passes,or infer runtime completeness. Exact inputs/commands/XML/events/exclusions are retained. Discovery skips are not passes. Four exporters/16 methods excluded;physical reboot ignore outstanding. Current artifact hash/signature NOT_RECORDED;no APK assembly/signing. Publication is user-confirmed,independently unverified;configuration saved.\n\nPrivate PDF is not included:13,572,361 bytes,SHA2560c441ffd8855b292922a40b10935d5ffb1b088b7118acfcc1856e259dc01867a. Obtain it as a separate private attachment. Never put PDF/extracted prose in repository/app/public deliverables. Original historical audit/four CSVs remain unavailable.\n\nNo .git database,caches,credentials,builds or local files are included. Actual historical-Git-dependent tests require a real authorized checkout;do not fabricate history or call supplied results independently reproduced. Final comprehensive independent audit first;Galaxy S26 Ultra airplane-mode/reboot acceptance last. No merge,freeze,signing or release approval.\n''')
 put('history/COMMITS_AND_PARENTS.txt',git('log','--format=%H %P %cI %s',target))
 # Preserve exact IDs in unfilled templates;never manufacture findings.
 def cp(n,h,rows=()):
  b=io.StringIO(newline='');w=csv.writer(b);w.writerow(h.split(','));w.writerows(rows);put('output_templates/'+n,b.getvalue())
 owners={o['id']:u['number'] for u in p['units'] for o in u['objectives']}
 cp('ITEM_ASSESSMENT_AUDIT.csv','item_id,unit,objective_id,tested_feature,inspected,verdict,cue_sufficient,answer_unique,distractors_valid,feedback_valid,teaching_support,source_binding,reason,finding_ids',[(i['id'],owners[i['objectiveId']],i['objectiveId'],i.get('assessment',{}).get('testedFeature',''),'false','NOT_VERIFIED','','','','','','','UNFILLED_TEMPLATE','') for i in p['items']])
 targets=list(csv.DictReader(io.StringIO(entries['repo/audit/content-corrections/ITEM_TARGET_RECONCILIATION_PENDING.csv'].decode())));assert len(targets)==75
 cp('TARGET_75_DISPOSITIONS.csv','correction_id,finding_id,unit,content_id,current_snapshot_comparison,diagnostic_disposition,evidence,new_finding_ids',[(t['correction_id'],t['finding_id'],t['unit'],t['content_id'],'','NOT_VERIFIED','UNFILLED_TEMPLATE','') for t in targets])
 cp('FINDINGS.csv','finding_id,severity,category,unit,content_id,current_path,locator,requirement_id,observed_behavior,expected_behavior,evidence,confidence,proposed_correction,acceptance_test,status')
 cp('UNIT_COVERAGE.csv','unit,objectives_checked,instruction_checked,examples_checked,guided_checked,independent_checked,remediation_checked,prerequisites_checked,mapping_checked,rights_checked,application_behavior_checked,verdict,gaps,finding_ids',[(n,*(['NOT_INSPECTED']*10),'NOT_VERIFIED','UNFILLED_TEMPLATE','') for n in range(1,31)])
 cp('REQUIREMENTS_MATRIX.csv','requirement_id,requirement_summary,evidence_paths,inspection_method,verdict,limitations,finding_ids')
 for n in ['INDEPENDENT_AUDIT_REPORT.md','CODEX_RETURN_HANDOFF.md']:put('output_templates/'+n,'# UNFILLED TEMPLATE\n\nNo audit performed. Follow DELIVERABLES.md.\n')
 for n in ['SOURCE_AND_BINDING_REVIEW.json','AUDIT_SCOPE_AND_EVIDENCE.json']:jp('output_templates/'+n,dict(schemaVersion=1,targetCommit=target,template=True,authorityDisposition='DIAGNOSTIC_ONLY',inspectionStatus='NOT_PERFORMED'))
 put('VERIFY_PACKET.py',"""import hashlib,json\nfrom pathlib import Path\nr=Path(__file__).resolve().parent;m=json.loads((r/'MANIFEST.json').read_text())\nfor x in m['files']:\n b=(r/x['path']).read_bytes();assert len(b)==x['bytes'] and hashlib.sha256(b).hexdigest()==x['sha256'],x['path']\nassert {p.relative_to(r).as_posix() for p in r.rglob('*') if p.is_file()}=={x['path'] for x in m['files']}|{'MANIFEST.json'}\nfor x in json.loads((r/'TRACKED_REPOSITORY_INVENTORY.json').read_text())['files']:\n b=(r/'repo'/x['path']).read_bytes();assert hashlib.sha1(b'blob '+str(len(b)).encode()+b'\\0'+b).hexdigest()==x['gitBlob'],x['path']\ncommit=(r/'history/TARGET_COMMIT_OBJECT.txt').read_bytes();target=json.loads((r/'PACKET_IDENTITY.json').read_text())['auditTarget']
assert hashlib.sha1(b'commit '+str(len(commit)).encode()+b'\\0'+commit).hexdigest()==target
trees=json.loads((r/'history/TARGET_TREE_OBJECTS.json').read_text());tree_bytes={oid:bytes.fromhex(value) for oid,value in trees.items()}
for oid,b in tree_bytes.items():assert hashlib.sha1(b'tree '+str(len(b)).encode()+b'\\0'+b).hexdigest()==oid
def walk(oid,prefix=''):
 b=tree_bytes[oid];pos=0;files={}
 while pos<len(b):
  end=b.index(b'\\0',pos);mode,name=b[pos:end].split(b' ',1);child=b[end+1:end+21].hex();pos=end+21;path=prefix+name.decode()
  if mode==b'40000':files.update(walk(child,path+'/'))
  else:files[path]=(mode.decode(),child)
 return files
root_tree=commit.splitlines()[0].decode().split()[1];actual=walk(root_tree)
expected={x['path']:(x['gitMode'],x['gitBlob']) for x in json.loads((r/'TRACKED_REPOSITORY_INVENTORY.json').read_text())['files']}
assert actual==expected,'Snapshot does not match target Git tree'
print('Every packet hash/size,tracked Git blob,target commit and full Git tree verified;integrity is not an audit pass.')
""")
 jp('MANIFEST.json',dict(schemaVersion=1,auditTarget=target,manifestSelfExcluded=True,files=[dict(path=n,bytes=len(b),sha256=sha(b)) for n,b in sorted(entries.items())]))
 a.output.parent.mkdir(parents=True,exist_ok=True)
 with zipfile.ZipFile(a.output,'x',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
  for n,b in sorted(entries.items()):
   info=zipfile.ZipInfo(n,date_time=(2026,10,18,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;info.external_attr=0o100644<<16;z.writestr(info,b,compresslevel=9)
 with zipfile.ZipFile(a.output) as z:
  assert z.testzip() is None
  for row in json.loads(z.read('MANIFEST.json'))['files']:assert sha(z.read(row['path']))==row['sha256']
 digest=sha(a.output.read_bytes());a.output.with_suffix(a.output.suffix+'.sha256').write_text(digest+'  '+a.output.name+'\n');print(json.dumps(dict(path=str(a.output),bytes=a.output.stat().st_size,sha256=digest,entries=len(entries),trackedFiles=len(inventory),auditTarget=target)))
if __name__=='__main__':main()
