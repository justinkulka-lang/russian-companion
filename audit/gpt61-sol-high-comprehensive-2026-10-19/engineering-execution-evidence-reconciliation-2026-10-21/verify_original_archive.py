from pathlib import Path,PurePosixPath
import json,zipfile,hashlib,re,stat,subprocess
R=Path('/workspace/russian-companion');D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-execution-evidence-reconciliation-2026-10-21';base=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/finish-results/2026-10-19-0321d323';p=base/'GPT61_FINISH_AUDIT_RESULTS_5c176eb2.zip';sha=lambda b:hashlib.sha256(b).hexdigest();source='f89d37f93cbaecf1108d00a2ef1f78518db9e581';rel=p.relative_to(R).as_posix();assert p.read_bytes()==subprocess.check_output(['git','show',source+':'+rel],cwd=R)
rows=[]
with zipfile.ZipFile(p) as z:
 assert z.testzip() is None;assert len(z.namelist())==len(set(z.namelist()))
 for i in z.infolist():
  name=PurePosixPath(i.filename);assert not name.is_absolute() and '..' not in name.parts and not stat.S_ISLNK(i.external_attr>>16);assert not i.filename.lower().endswith('.pdf');payload=z.read(i);local=base/i.filename
  rows.append(dict(path=i.filename,bytes=len(payload),sha256=sha(payload),trackedSiblingExists=local.is_file(),trackedSiblingExact=local.read_bytes()==payload if local.is_file() else None,zipOnlyAtReceiptDirectory=not local.is_file(),inspection='MECHANICAL_IDENTITY_ONLY_NOT_SEMANTIC_BODY_REVIEW'))
 manifest=z.read('SHA256SUMS.txt').decode();checks=[]
 for line in manifest.splitlines():
  m=re.fullmatch(r'([0-9a-fA-F]{64})\s+\*?(.+)',line);assert m,line
  h,n=m.groups();assert n in z.namelist(),n;assert sha(z.read(n))==h.lower(),n;checks.append(n)
 assert len(checks)==len(set(checks));assert all(v['trackedSiblingExact'] is not False for v in rows)
x=dict(kind='ENGINEERING_ARCHIVE_RECEIPT_NOT_INDEPENDENT_SEMANTIC_INSPECTION',sourceCheckpoint=source,path=rel,archiveBytes=p.stat().st_size,archiveSha256=sha(p.read_bytes()),archiveMatchesPinnedGitBytes=True,crcAllMembersVerified=True,duplicateOrUnsafeNames=False,pdfEntries=False,manifestListedEntriesVerified=len(checks),allMembersIndividuallyHashed=len(rows),zipOnlyAtReceiptDirectory=sum(v['zipOnlyAtReceiptDirectory'] for v in rows),members=rows,unlistedByInternalManifest=[v['path'] for v in rows if v['path'] not in checks],qualification='CRC/internal SHA validate byte integrity and consistency,not authorship,signature,semantic coverage,credentials absence or independent audit completion. Archived scripts were not executed and no files extracted.',independentAudit='INCOMPLETE')
(D/'ORIGINAL_RETURN_ZIP_IDENTITY.json').write_text(json.dumps(x,indent=2)+'\n');print(json.dumps({k:v for k,v in x.items() if k not in ['members','unlistedByInternalManifest']}))
