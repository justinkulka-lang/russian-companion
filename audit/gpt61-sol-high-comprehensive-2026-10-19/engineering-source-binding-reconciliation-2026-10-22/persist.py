from pathlib import Path
import json,hashlib,zipfile,shutil
R=Path('/workspace/russian-companion');W=Path(__file__).parent;D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-source-binding-reconciliation-2026-10-22'
sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
def save(name,v):(D/name).write_text(json.dumps(v,indent=2)+'\n')
def snapshot(mode):
 O=W/mode;assert (O/'SHA256.json').exists()
 for name,h in json.loads((O/'SHA256.json').read_text()).items():assert sha(O/name)==h
 (D/mode).mkdir()
 for name in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit']+(['LINT.xml'] if (O/'LINT.xml').exists() else []):shutil.copy2(O/name,D/mode/name)
 index=[]
 with zipfile.ZipFile(D/('RAW_'+mode+'.zip'),'w',zipfile.ZIP_DEFLATED) as z:
  for p in sorted(O.rglob('*')):
   if p.is_file():
    name=p.relative_to(O).as_posix();z.write(p,name);index.append(dict(path=name,sha256=sha(p),bytes=p.stat().st_size))
  for name in [mode+'_SESSION.json',mode+'_LAUNCHER.log']:
   p=W/name;z.write(p,'LAUNCHER/'+name);index.append(dict(path='LAUNCHER/'+name,sha256=sha(p),bytes=p.stat().st_size))
 with zipfile.ZipFile(D/('RAW_'+mode+'.zip')) as z:
  assert z.testzip() is None
  for v in index:assert hashlib.sha256(z.read(v['path'])).hexdigest()==v['sha256']
 save('RAW_'+mode+'_INDEX.json',dict(archive='RAW_'+mode+'.zip',archiveSha256=sha(D/('RAW_'+mode+'.zip')),members=index,crcAndEveryMemberHashVerified=True))
