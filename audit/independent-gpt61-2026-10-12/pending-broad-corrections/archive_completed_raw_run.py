from pathlib import Path
from datetime import datetime,timezone
import json,hashlib,zipfile
base=Path('audit/independent-gpt61-2026-10-12/broader-regression-2026-10-18');run=base/'CURRENT_REGRESSION';assert (run/'MANIFEST.json').exists(),'Wait for final collection'
m=json.loads((run/'MANIFEST.json').read_text());assert m['allDiscoveredDebugHostMethodsReported'] and m['eventOutcomeCountsMatchXml'];archive=base/'CURRENT_RAW_EVIDENCE.zip';rows=[]
def digest(path):
 with path.open('rb') as f:return hashlib.file_digest(f,'sha256').hexdigest()
with zipfile.ZipFile(archive,'x',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
 for p in sorted(run.rglob('*')):
  if not p.is_file():continue
  member=str(p.relative_to(base));rows.append({'path':str(p.relative_to(base)),'member':member,'bytes':p.stat().st_size,'sha256':digest(p),'originalMtimeNs':p.stat().st_mtime_ns});z.write(p,member)
with zipfile.ZipFile(archive) as z:
 assert z.testzip() is None
 for row in rows:
  with z.open(row['member']) as f:assert hashlib.file_digest(f,'sha256').hexdigest()==row['sha256'],row['member']
index={'sourceCommit':m['sourceContext']['sourceCommit'],'recordedUtc':datetime.now(timezone.utc).isoformat(),'reason':'The complete broad failure has a59-million-character message. Preserve all original raw files byte-exact in a tracked compressed archive,without duplicating giant status files. Original local files are retained;none removed.','archive':archive.name,'archiveBytes':archive.stat().st_size,'archiveSha256':digest(archive),'uncompressedBytes':sum(r['bytes'] for r in rows),'files':rows,'restore':'Extract CURRENT_RAW_EVIDENCE.zip to a separate temporary directory. Members are rooted at CURRENT_REGRESSION/;verify every member size/SHA256 against this index before inspecting. Do not overwrite tracked evidence or rerun immutable labels.','currentRawDirectoryGitIgnored':True,'privatePdfIncluded':False,'apkAssemblyOrSigning':False}
(base/'CURRENT_RAW_EVIDENCE_INDEX.json').write_text(json.dumps(index,indent=2)+'\n');p=base/'.gitignore';assert not p.exists();p.write_text('# Complete byte-exact raw run is tracked in CURRENT_RAW_EVIDENCE.zip.\n# See CURRENT_RAW_EVIDENCE_INDEX.json;retain local originals without giant Git duplicates.\n/CURRENT_REGRESSION/\n')
print('Verified archive',archive.stat().st_size,'bytes;raw',index['uncompressedBytes'],'bytes;',len(rows),'members')
