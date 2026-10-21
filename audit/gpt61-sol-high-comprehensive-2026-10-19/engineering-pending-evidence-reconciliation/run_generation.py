from pathlib import Path
import json,subprocess,sys
r=Path('/workspace/russian-companion');w=Path(__file__).parent;label=sys.argv[1]
old=json.loads((w/'SOURCE_BINDING_REVIEW.json').read_text());cp=old['commands'][0]['command'][2]
cmds=([['/workspace/jdk21/bin/javac','-cp',cp,str(w/'GeneratePendingEvidence.java')]] if label=='CANDIDATE_1' else [])+[['/workspace/jdk21/bin/java','-Xmx2g','-cp',cp,'GeneratePendingEvidence',str(r),str(w/label)]];records=[]
for i,cmd in enumerate(cmds):
 with (w/f'{label}_{i}.log').open('w') as out:p=subprocess.run(cmd,cwd=r,text=True,stdout=out,stderr=subprocess.STDOUT)
 records.append(dict(command=cmd,exitCode=p.returncode));assert p.returncode==0,(label,i,p.returncode)
(w/f'{label}_CONTEXT.json').write_text(json.dumps(dict(sourceCommit=subprocess.check_output(['git','rev-parse','HEAD'],cwd=r,text=True).strip(),commands=records,trackedFilesWritten=False,notJUnit=True,readinessProposedManifestOverride=True,sourceSnapshotCopyFlag='NOT_OBSERVED_IN_SOURCE_SNAPSHOT'),indent=2)+'\n')
print(label,'completed')
