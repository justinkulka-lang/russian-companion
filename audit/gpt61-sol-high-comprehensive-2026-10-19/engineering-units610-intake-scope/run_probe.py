import subprocess,json,hashlib
from pathlib import Path
r=Path('/workspace/russian-companion');w=Path(__file__).parent
context=json.loads(Path('/workspace/artifacts/post-correction-broad-2026-10-20/review-rule-diagnostic/CONTEXT.json').read_text())
cp=context['compileCommand'][2].replace('/workspace/artifacts/post-correction-broad-2026-10-20/review-rule-diagnostic',str(w))+':'+str(r/'app/build/intermediates/javac/debug/compileDebugJavaWithJavac/classes')
asset='app/src/main/assets/content/v2-curriculum.json'
refs=['692e9eddeaef4d8f4ca9e85bf352983e3526e47c','e6db0bdfe0bbb40edf190623e2123014946f305b']
for ref in refs:(w/(ref+'.json')).write_bytes(subprocess.check_output(['git','show',ref+':'+asset],cwd=r))
cmd1=['/workspace/jdk21/bin/javac','-cp',cp,str(w/'IntakeDigestProbe.java')]
cmd2=['/workspace/jdk21/bin/java','-Xmx2g','-cp',cp,'IntakeDigestProbe',*[str(w/(x+'.json')) for x in refs],str(r/asset)]
commands=[]
for cmd,name in [(cmd1,'COMPILE'),(cmd2,'PROBE')]:
 p=subprocess.run(cmd,cwd=r,text=True,capture_output=True);(w/(name+'.txt')).write_text(p.stdout+p.stderr);commands.append(dict(command=cmd,exitCode=p.returncode));assert p.returncode==0,(p.stdout+p.stderr)[-1500:]
(w/'PROBE_CONTEXT.json').write_text(json.dumps(dict(sourceCommit=subprocess.check_output(['git','rev-parse','HEAD'],cwd=r,text=True).strip(),commands=commands,noJUnitExecuted=True,noRepositoryInputChanged=True),indent=2)+'\n')
print((w/'PROBE.txt').read_text())
