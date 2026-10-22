from pathlib import Path
import json,subprocess,datetime
W=Path(__file__).parent;R=Path('/workspace/russian-companion');ctx=json.loads((W/'GENERATION_PREPARATION.json').read_text());cp=ctx['classpath'];records=[]
cmd=['/workspace/jdk21/bin/javac','-cp',cp,str(W/'GeneratePendingEvidence.java')]
for label,cmd in [('COMPILE',cmd)]+[(f'CANDIDATE_{n}',['/workspace/jdk21/bin/java','-Xmx2g','-cp',cp,'GeneratePendingEvidence',str(W/'INPUT_PROJECTION'),str(W/f'CANDIDATE_{n}'),ctx['newDigest'],str(R)]) for n in [1,2]]:
 start=datetime.datetime.now(datetime.timezone.utc).isoformat()
 with (W/(label+'.log')).open('w') as out:p=subprocess.run(cmd,cwd=R,stdout=out,stderr=subprocess.STDOUT)
 records.append(dict(label=label,command=cmd,startedUtc=start,finishedUtc=datetime.datetime.now(datetime.timezone.utc).isoformat(),exitCode=p.returncode));(W/'GENERATION_RESULTS.json').write_text(json.dumps(records,indent=2)+'\n');assert p.returncode==0,(label,p.returncode)
print('Both fresh renders completed;no tracked outputs written.')
