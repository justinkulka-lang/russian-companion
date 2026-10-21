from pathlib import Path
import json,subprocess,hashlib,base64,re
r=Path('/workspace/russian-companion');w=Path(__file__).parent
old=json.loads(Path('/workspace/artifacts/post-correction-broad-2026-10-20/generated-evidence-boundary/PROJECTION_CONTEXT.json').read_text());cp=old['commands'][0]['command'][2].replace('/workspace/artifacts/post-correction-broad-2026-10-20/generated-evidence-boundary',str(w))
cmds=[['/workspace/jdk21/bin/javac','-cp',cp,str(w/'SourceBindingAudit.java')],['/workspace/jdk21/bin/java','-Xmx2g','-cp',cp,'SourceBindingAudit',str(r)]];results=[]
for i,cmd in enumerate(cmds):
 p=subprocess.run(cmd,cwd=r,text=True,capture_output=True);(w/f'BINDING_AUDIT_{i}.txt').write_text(p.stdout+p.stderr);results.append(dict(command=cmd,exitCode=p.returncode));assert p.returncode==0,(p.stdout+p.stderr)[-1800:]
rows=[]
for line in (w/'BINDING_AUDIT_1.txt').read_text().splitlines():
 if not re.match(r'^\d+\t',line):continue
 n,old,e,new,e2,h,size,equal=line.split('\t');decode=lambda x:base64.b64decode(x).decode().splitlines();rows.append(dict(unit=int(n),recordedSourceCommit=old,recordedSourceErrors=decode(e),proposedSourceCommit=new,proposedSourceErrors=decode(e2),canonicalDocumentSha256=h,canonicalBytes=int(size),canonicalDocumentsByteIdentical=equal=='true'))
assert len(rows)==30 and [x['unit'] for x in rows if x['recordedSourceErrors']]==[4,7,8,10,15]
assert all(not x['proposedSourceErrors'] and x['canonicalDocumentsByteIdentical'] for x in rows)
(w/'SOURCE_BINDING_REVIEW.json').write_text(json.dumps(dict(sourceCommit=subprocess.check_output(['git','rev-parse','HEAD'],cwd=r,text=True).strip(),commands=results,rows=rows,notJUnit=True,canonicalDocumentIncludesReachableSourceRightsMediaMappingDependencies=True,qualification=False),indent=2)+'\n')
print('All30 proposed source pointers pass actual errors/ancestry checks and canonical byte comparisons;five old pointers fail.')
