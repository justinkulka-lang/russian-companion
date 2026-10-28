from pathlib import Path
from datetime import datetime,timezone
import json,subprocess
R=Path('/workspace/russian-companion');W=Path(__file__).parent;G=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip();assert not G('status','--porcelain');args=['compileDebugUnitTestKotlin','--offline','--max-workers=2','--console=plain'];c=dict(sourceCommit=G('rev-parse','HEAD'),sourceRootTree=G('rev-parse','HEAD^{tree}'),inputTrees={n:G('rev-parse','HEAD:'+n) for n in ['app/src/main','app/src/test','catalog','docs','review','release']},startedUtc=datetime.now(timezone.utc).isoformat(),workingDirectory=str(R),activation='source /workspace/setup/android-env.sh',command=['./gradlew',*args],JUnitMethodsExecuted=0)
(W/'COMPILE_CONTEXT.json').write_text(json.dumps(c,indent=2)+'\n')
with (W/'COMPILE.log').open('x') as out:p=subprocess.run(['bash','-c','source /workspace/setup/android-env.sh;exec ./gradlew "$@"','tooling-compile',*args],cwd=R,stdout=out,stderr=subprocess.STDOUT)
c.update(finishedUtc=datetime.now(timezone.utc).isoformat(),exitCode=p.returncode);(W/'COMPILE_CONTEXT.json').write_text(json.dumps(c,indent=2)+'\n');(W/'COMPILE.exit').write_text(str(p.returncode)+'\n');assert not G('status','--porcelain');print(json.dumps(c));raise SystemExit(p.returncode)
