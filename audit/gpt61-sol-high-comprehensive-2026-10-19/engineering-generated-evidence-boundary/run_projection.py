from pathlib import Path
import json,subprocess,hashlib
r=Path('/workspace/russian-companion');w=Path(__file__).parent
old=json.loads(Path('/workspace/artifacts/post-correction-broad-2026-10-20/units610-intake-scope/PROBE_CONTEXT.json').read_text());cp=old['commands'][0]['command'][2].replace('/units610-intake-scope','/generated-evidence-boundary')
cmds=[['/workspace/jdk21/bin/javac','-cp',cp,str(w/'EvidenceProjection.java')],['/workspace/jdk21/bin/java','-Xmx2g','-cp',cp,'EvidenceProjection',str(r),str(w/'PROJECTION_NOT_ACCEPTED')]];results=[]
for i,cmd in enumerate(cmds):
 p=subprocess.run(cmd,cwd=r,text=True,capture_output=True);(w/f'PROJECTION_{i}.txt').write_text(p.stdout+p.stderr);results.append(dict(command=cmd,exitCode=p.returncode));assert p.returncode==0,(p.stdout+p.stderr)[-1200:]
(w/'PROJECTION_CONTEXT.json').write_text(json.dumps(dict(sourceCommit=subprocess.check_output(['git','rev-parse','HEAD'],cwd=r,text=True).strip(),commands=results,notJUnit=True,trackedRepositoryFilesWritten=False,approval=False),indent=2)+'\n')
print('Diagnostic projections created outside checkout; no review/approval write.')
