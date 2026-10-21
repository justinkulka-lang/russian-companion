from pathlib import Path
import subprocess,hashlib,zipfile,json,csv,io
R=Path('/workspace/russian-companion');D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-execution-evidence-reconciliation-2026-10-21';SOURCE='f89d37f93cbaecf1108d00a2ef1f78518db9e581';zpath=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/finish-results/2026-10-19-0321d323/GPT61_FINISH_AUDIT_RESULTS_5c176eb2.zip';byblob={};sha=lambda x:hashlib.sha256(x).hexdigest()
for line in subprocess.check_output(['git','ls-tree','-rz',SOURCE],cwd=R).split(b'\0'):
 if not line:continue
 meta,path=line.split(b'\t');oid=meta.decode().split()[2];byblob.setdefault(oid,[]).append(path.decode())
rows=[]
with zipfile.ZipFile(zpath) as z:
 for i in z.infolist():
  raw=z.read(i);oid=hashlib.sha1(b'blob '+str(len(raw)).encode()+b'\0'+raw).hexdigest();matches=byblob.get(oid,[])
  if matches:
   target=R/matches[0];assert target.read_bytes()==raw;action='REUSE_EXISTING_EXACT_TRACKED_BYTES'
  else:
   target=D/'historical-original-return-members'/i.filename;assert '..' not in Path(i.filename).parts and not Path(i.filename).is_absolute();target.parent.mkdir(parents=True,exist_ok=True);assert not target.exists();target.write_bytes(raw);action='EXPOSE_PREVIOUSLY_ZIP_ONLY_EXACT_BYTES'
  rows.append(dict(archiveMember=i.filename,bytes=len(raw),sha256=sha(raw),gitBlob=oid,canonicalRepositoryPath=target.relative_to(R).as_posix(),action=action,originalAuditTarget='5c176eb22f4a93bf5f7fd20af15e79ec8c4748ed',historicalStatus='ORIGINAL_EXTERNAL_OUTPUT_UNCHANGED_NOT_CURRENT_REVIEW',bodyInspectionByThisExposure='NOT_ESTABLISHED',allEquivalentPreviouslyTrackedPaths=matches))
assert len(rows)==42 and sum(r['action'].startswith('EXPOSE') for r in rows)==30
(D/'ORIGINAL_RETURN_MEMBER_RESOLUTION.json').write_text(json.dumps(dict(kind='HISTORICAL_OUTPUT_ACCESSIBILITY_NOT_NEW_AUDIT',archivePath=zpath.relative_to(R).as_posix(),archiveSha256=sha(zpath.read_bytes()),pinnedPreExposureSource=SOURCE,originalAuditTarget='5c176eb22f4a93bf5f7fd20af15e79ec8c4748ed',members=rows,exposedNewFiles=30,reusedExactTrackedFiles=12,noExistingEvidenceModified=True,privatePdfIncluded=False,independentAudit='INCOMPLETE'),indent=2)+'\n')
with (D/'ORIGINAL_RETURN_MEMBER_RESOLUTION.csv').open('w',newline='') as f:
 w=csv.writer(f,lineterminator='\n');w.writerow(['archive_member','bytes','sha256','canonical_repository_path','action','historical_target','semantic_inspection_status'])
 for v in rows:w.writerow([v['archiveMember'],v['bytes'],v['sha256'],v['canonicalRepositoryPath'],v['action'],v['originalAuditTarget'],'NOT_ESTABLISHED_BY_EXPOSURE'])
print({'newHistoricalFilesExposed':30,'identicalTrackedFilesReused':12,'allArchiveMembersResolved':42,'noOldFilesModified':True})
